package http;

import java.util.logging.Logger;

import dao.Dao;
import data.Device;
import data.LjPay;
import main.Global;
import main.ServerTimer;

public class Html_opposign extends Html{
	public static final Logger log = Logger.getLogger(Html_opposign.class.getName());
	public static String PublicKey ="b96aB72Ae570576af7Edd3492a32f037";
	
	public String getHtml(String content) {
		String html="{\"result\":0}";
		if(Global.isEmpty(content)) {
			return html;
		}
		synchronized(log) {
			String productDesc="";
			String orderId="";
			int price=0;
			String sign="";
			String notifyId="";
			String attach="";
			String productName="";//productName=国际象棋：解锁第3课
			String contents[] = content.split("&");
			for(int i=0;i<contents.length;i++) {
				String c = contents[i];
				if(c.startsWith("productDesc=")){
					productDesc = c.replace("productDesc=", "");
				}else if(c.startsWith("partnerOrder=")){
					orderId = c.replace("partnerOrder=", "");
					LjPay before=Dao.getLjPayByOrderID(orderId);
					if(before!=null && content.equals(before.getContent())) {
						log.info("又收到了条支付回调信息");
						return "{\"result\":0}";//已经收到当前的订单信息
					}
				}else if(c.startsWith("price=")){
					price = Global.getInt(c.replace("price=", ""));
				}else if(c.startsWith("sign=")){
					sign = c.replace("sign=", "");
				}else if(c.startsWith("notifyId=")) {
					notifyId = c.replace("notifyId=", "");
				}else if(c.startsWith("attach=")) {
					attach = c.replace("attach=", "");
				}else if(c.startsWith("productName=")) {
					productName = c.replace("productName=", "");
				}
			}
			log.info("Desc:"+productDesc+"orderId:"+orderId+",price:"+price+",attach="+attach);
			
			LjPay pay=new LjPay();
			pay.setFirstTime(ServerTimer.getFullWithS());
			pay.setContent(content);
			pay.setOrderID(orderId);
			pay.setParam(attach+"-"+productName);
			Dao.save(pay);
			
			String[] params=attach.split("-");
			int deviceID=0;
			int lesson=0;
			int total=0;
			String channel="oppo联运";
			if(params.length>=4) {
				deviceID=Global.getInt(params[0]);
				lesson=Global.getInt(params[1]);
				total=Global.getInt(params[2]);
				channel=params[3].split("#")[0];
			}
			double money=price/100;
			log.info("device:"+deviceID+",money="+money+
					",lesson="+lesson+",total="+total+",channel="+channel);
			Device wd=Dao.getDeviceExist(deviceID, "");
			if(wd!=null && money>0) {
				AddPayRecord.addPayRecord(wd, money, lesson, channel, total);
				pay.setUserName(wd.toString());
				pay.setMoney((int)money);
				pay.setUsed(pay.getUsed()+1);
				pay.setChannel(channel);
				pay.setFirstTime(pay.getFirstTime()+"#"+ServerTimer.getFullWithS());
				Dao.save(pay);
			}
		}
		return html;
	}
	
}
