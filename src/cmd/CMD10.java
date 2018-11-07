package cmd;

import java.io.ByteArrayOutputStream;
import java.util.logging.Logger;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import main.Global;
import main.ServerTimer;

import org.dom4j.Node;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import dao.Dao;
import data.*;

public class CMD10 implements ICMD {
	private static final Logger log = Logger.getLogger(CMD10.class.getName());

	@Override
	public ChannelBuffer getBytes(int cmd, ChannelBuffer data) {
		String name = Global.readUTF(data);
		String content = BaseData.getContent(name);
		if ( "配置信息".equals(name) && !Global.isEmpty(content)) {
			content = this.getXMLcontent(content, data);
		}
		ChannelBuffer buf = ChannelBuffers.dynamicBuffer();
		buf.writeShort(cmd);
		buf.writeBytes(Global.getUTF(content));
		return buf;
	}
	/**
	 * 课程加密
	 */
	private String getXMLcontent(String content, ChannelBuffer data) {
		Device device = null;
		if (data.readable()) {
			String imei = Global.readUTF(data);log.info(imei);
			device = Dao.getDeviceExist(0,imei);//不允许cmd10创建新用户
		}
		Document doc = Global.xmlParser(content);// 解析content为可读文档
		if(doc==null) {
			return "baseData未成功解析成document文件";
		}
		String token = "";	int unlocky = 0;
		if (device != null) {
			if (Global.isEmpty(device.getToken())) {
				device.setToken(Global.md5(device.getId() + device.getImei() 
				+ device.getFirstTime() + Math.random()));
			} 
			token = device.getToken();
			unlocky = Global.getRandom(99999);
			device.setUnlocky(unlocky);
			log.info("token=" + token + ",unlocky=" + unlocky+",device="+device.getId());
			Dao.save(device);// 保存
		} else {
			token = Global.md5(ServerTimer.getFullWithS() + Math.random());
			unlocky = getUnlockyByToken(token);
			NodeList channels = doc.getElementsByTagName("channel").item(0).getChildNodes();
			for (int i = 0; i < channels.getLength(); i++) {
				if (channels.item(i).getNodeType() == Node.ELEMENT_NODE) {
					Element e = (Element) channels.item(i);
					e.setAttribute("info",(e.getAttribute("info") + "#" + token));
				}
			}
			log.info("token=" + token + ",unlocky=" + unlocky+",device=null");
		}
		NodeList lessons = doc.getElementsByTagName("lesson");
		for (int i = 0; i < lessons.getLength(); i++) {
			Element e = (Element) lessons.item(i);
			int lesson = Global.getInt(e.getAttribute("id"));
			e.setAttribute("code", "" + next(unlocky, lesson));
			this.changeUrl("url", token, lesson, e);
			this.changeUrl("urlHW", token, lesson, e);
			//this.changeUrl("urlTV", token, lesson, e);
		}
		try {// Document 转回 String
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer tf = factory.newTransformer();
			tf.setOutputProperty("encoding", "utf8");
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			tf.transform(new DOMSource(doc), new StreamResult(bos));
			return bos.toString();
		} catch (TransformerException e) {
			log.info("xml回写出错：" + e.getMessage());
			return "xml回写出错";
		}
	}

	private void changeUrl(String url, String token, int lesson, Element e) {
		String urlValue = e.getAttribute(url);
		if (urlValue != null && urlValue.contains("/")) {
			int cut = urlValue.lastIndexOf('/');
			urlValue = urlValue.substring(0, cut) + "/down2.php?token=" + token + "&lesson=" + lesson;
			e.setAttribute(url, urlValue);// 关键执行步骤
		}
	}
	/**
	 *  单课解锁码算法
	 */
	public static int next(int code, int count) {
		for (int i = 0; i < count; i++) {
			code ^= (code << 20);
			code ^= (code >> 21);
			code ^= (code << 5);
		}
		code = 0xFFFF & code + code >> 16;
		return code;
	}
	/**
	 *    无imei的用户匹配解锁码
	 */
	public static int getUnlockyByToken(String token) {
		if (Global.isEmpty(token)) {
			return 0;
		}	
		int code = 0;
		for (int i = 0; i < token.length(); i++) {
			code += token.charAt(i) << (i % 5);
		}
		return code;
	}

}
