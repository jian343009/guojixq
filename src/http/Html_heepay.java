package http;

import java.net.URLDecoder;
import java.util.*;

import org.jboss.logging.Logger;

import data.*;
import dao.*;
import main.*;

public class Html_heepay implements IHtml{
	private static final Logger log = Logger.getLogger(Html_heepay.class);

	public String getHtml(String content)
	{
		String html = "ok";
		String key = "57D0E7D84D8547AE84C31D34";
		
		try {
			content = URLDecoder.decode(content, "gb2312");
		} catch (Exception e) {
			e.printStackTrace();
		}
		synchronized (log) {
			LjPay pay = new LjPay();
			pay.setFirstTime(ServerTimer.getFullWithS());
			pay.setContent(content);
			Dao.save(pay);
			
			HashMap<String, String> map = Global.decodeUrlParam(content);
			log.info("汇付宝:"+map);
			String sign = Global.md5("result="+map.get("result")+"&agent_id="+map.get("agent_id")+"&jnet_bill_no="+map.get("jnet_bill_no")+"&agent_bill_id="+map.get("agent_bill_id")+"&pay_type="+map.get("pay_type")+"&pay_amt="+map.get("pay_amt")+"&remark="+map.get("remark")+"&key="+key);
			log.info("sign:"+sign);
			pay.setOrderID(map.get("jnet_bill_no"));
			LjPay before = Dao.getLjPayByOrderID(pay.getOrderID());
			String remark = "";
			if(map.get("remark") != null){
				remark = map.get("remark");
			}
			pay.setParam(remark);
			String[] params = remark.split("-");
			int deviceID = 0;
			int lesson = 0;
			int total = 0;
			String channel = "汇付宝";
			if(params.length >=4){
				deviceID = Global.getInt(params[0]);
				lesson = Global.getInt(params[1]);
				total = Global.getInt(params[2]);
				channel = params[3].split("#")[0];
			}
			pay.setChannel(channel);
			String result = "";
			if(map.get("result") != null){
				result = map.get("result");
			}
			double money = 0;
			if(map.get("pay_amt") != null){
				money = Global.getDouble(map.get("pay_amt"));
				pay.setMoney((int)money);
			}
			Device wd = Dao.getDeviceExist(deviceID,"");
			if(wd != null && money > 0 && before == null && result.equals("1") && sign.equals(map.get("sign"))){
				pay.setUserName(wd.getImei());
				
				Count mc = Dao.getCountMonth();
				Count count = Dao.getCountToday();
				mc.setPay(mc.getPay() +1);
				count.setPay(count.getPay() +1);
				ChannelEveryday ce = Dao.getChannelEverydayToday(channel);
				ce.setPay(ce.getPay() +1);
				if(wd.getBuyState() ==0){
					mc.setNewPay(mc.getNewPay() +1);
					count.setNewPay(count.getNewPay() +1);
					ce.setNewPay(ce.getNewPay() +1);
				}
				mc.setTotalPay(mc.getTotalPay() + money);
				count.setTotalPay(count.getTotalPay() + money);
				ce.setTotalPay(ce.getTotalPay() + money);
				//价格比对+红包使用
				Device.checkPrice(wd, lesson, (int)money);
				//AB测试支付记录
				Device.recordABpay(wd, (int)money, count, lesson);
				if(map.get("pay_type").equals("30")){
					mc.setWxPay(mc.getWxPay() + money);
					count.setWxPay(count.getWxPay() + money);
					ce.setWxPay(ce.getWxPay() + money);
				}else{
					mc.setWiiPay(mc.getWiiPay() + money);
					count.setWiiPay(count.getWiiPay() + money);
					ce.setWiiPay(ce.getWiiPay() + money);
				}
				Dao.save(mc);
				Dao.save(count);
				Dao.save(ce);
				if(lesson ==0){
					wd.setLastDay(ServerTimer.distOfDay());
					wd.setLastTime(ServerTimer.getFull());
					wd.setBuyState(wd.getBuyState() | total);
					wd.setBuy(wd.getBuy() +1);
					Dao.save(wd);
				}else{
	//				AppleProduct product = Dao.getWeiqiProductByLesson(lesson);
	//				if(product != null && money >= product.getPrice()){
						int pow = 1<<lesson;
						wd.setLastDay(ServerTimer.distOfDay());
						wd.setLastTime(ServerTimer.getFull());
						wd.setBuyState(wd.getBuyState() | pow);
						wd.setBuy(wd.getBuy() +1);
						wd.setUnlockNum(lesson, 0);
						Dao.save(wd);
	//				}
				}
			}
			pay.setUsed(pay.getUsed() +1);
			pay.setFirstTime(pay.getFirstTime() + "#"+ServerTimer.getFullWithS());
			Dao.save(pay);
		}
		
		return html;
	}
}
