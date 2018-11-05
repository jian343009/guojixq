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
		String content = "";
		BaseData bd = Dao.getBaseDataByName(name);
		if (bd != null) {
			content = bd.getContent();
			if ("配置信息".equals(name)) {
				content = this.getXMLcontent(content, data);
			}
		}
		ChannelBuffer buf = ChannelBuffers.dynamicBuffer();
		buf.writeShort(cmd);
		buf.writeBytes(Global.getUTF(content));
		return buf;
	}

	private String getXMLcontent(String content, ChannelBuffer data) {
		if (content == null || content.isEmpty()) {
			return "";
		}
		Document doc = Global.xmlParser(content);// 解析content为可读文档
		String token = "";
		int unlocky = 0;
		Device device = null;
		if (data.readableBytes() > 2) {
			String imei = Global.readUTF(data);
			log.info("imei = " + imei + "---------------------------imei");
			if (imei.length() > 0) {				
				device = Dao.getDevice(0, imei, "CMD10");
			}
		}
		if (device != null) {
			if (device.getToken() == null || device.getToken().isEmpty()) {
				token = Global.md5(device.getId() + device.getImei() + device.getFirstTime() + Math.random());
				device.setToken(token);
			} else {// 这是有token的情况
				token = device.getToken();
			}
			unlocky = Global.getRandom(99999);
			device.setUnlocky(unlocky);
			Dao.save(device);// 保存
			log.info("这是有imei的情况----token = " + token + "----unlocky = " + unlocky);
		} else {
			token = Global.md5(ServerTimer.getFullWithS() + Math.random());
			unlocky = this.getUnlockyByToken(token);
			if(doc!=null){
			this.changeXMLChannel(doc, token);
			log.info("这是无imei的情况----token = " + token + "----unlocky = " + unlocky);
			}
			
		}
		return this.changeXMLCode(doc, token, unlocky);
	}

	private String changeXMLCode(Document doc, String token, int unlocky) {
		if (doc == null) {
			return "";
		}
		NodeList lessons = doc.getElementsByTagName("lesson");
		for (int i = 0; i < lessons.getLength(); i++) {
			Element e = (Element) lessons.item(i);
			int lesson = Global.getInt(e.getAttribute("id"));
			e.setAttribute("code", "" + next(unlocky, lesson));
			String urlValue = this.changeUrl("url", token, lesson, e);
			this.changeUrl("urlHW", token, lesson, e);
			this.changeUrl("urlTV", token, lesson, e);
		}
		String outxml = null;
		try {// Document 转回 String
			outxml = this.docmentToString(doc);
		} catch (TransformerException e) {
			log.info("xml回写出错：" + e.getMessage());
		}
		return outxml;
	}

	// 改文档的channel信息
	private void changeXMLChannel(Document doc, String token) {		
		NodeList channels = doc.getElementsByTagName("channel").item(0).getChildNodes();
		for (int i = 0; i < channels.getLength(); i++) {
			if (channels.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) channels.item(i);
				String Channelname = e.getAttribute("info") + "#" + token;
				e.setAttribute("info", Channelname);
			}
		}		
	}

	private String changeUrl(String url, String token, int lesson, Element e) {
		String urlValue = e.getAttribute(url);
		if (urlValue != null && urlValue.contains("/")) {
			int cut = urlValue.lastIndexOf('/');
			urlValue = urlValue.substring(0, cut) + "/down.php?token=" + token + "&lesson=" + lesson;
			e.setAttribute(url, urlValue);// 关键执行步骤
		}
		return urlValue;

	}

	public String docmentToString(Document doc) throws TransformerException {
		if(doc==null){
			return "";
		}
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer tf = factory.newTransformer();
		tf.setOutputProperty("encoding", "utf8");
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		tf.transform(new DOMSource(doc), new StreamResult(bos));
		return bos.toString();
	}

	public static int next(int code, int count) {
		for (int i = 0; i < count; i++) {
			code ^= (code << 20);
			code ^= (code >> 21);
			code ^= (code << 5);
		}
		code = 0xFFFF & code + code >> 16;
		return code;
	}

	public static int getUnlockyByToken(String token) {
		int code = 0;
		if (token != null && !token.isEmpty()) {
			for (int i = 0; i < token.length(); i++) {
				code += token.charAt(i) << (i % 5);
			}
		}
		return code;
	}

}
