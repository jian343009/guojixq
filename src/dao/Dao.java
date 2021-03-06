package dao;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Logger;

import main.Global;
import main.ServerTimer;
import net.sf.json.JSONObject;

import org.apache.commons.io.IOUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import data.*;

public class Dao {

	private static final Logger log = Logger.getLogger(Dao.class.getName());
	
	public static List<Record> getRecentRecord(String imei){
		Session ss = HSF.getSession();
		Criteria ct = ss.createCriteria(Record.class);
		if(!imei.isEmpty()){
			ct.add(Restrictions.like("imei", imei));
		}
		ct.addOrder(Order.desc("id"));
		ct.setMaxResults(300);
		List<Record> list = ct.list();
		ss.close();
		return list;
	}

	@SuppressWarnings("unchecked")
	public static ChannelEveryday getChannelEverydayToday(String channel){
		ChannelEveryday ce = null;
		Session ss = HSF.getSession();
		List<ChannelEveryday> list = ss.createCriteria(ChannelEveryday.class).add(Restrictions.eq("channel", channel)).add(Restrictions.eq("day", ServerTimer.distOfDay())).setMaxResults(1).list();
		ss.close();
		if(list.size() >0){
			ce = list.get(0);
		}else{
			int today = ServerTimer.distOfDay();
			ce = new ChannelEveryday();
			ce.setChannel(channel);
			ce.setYesterday(getChannelNewDeviceYesterday(channel, today));
			ce.setDay(today);
			ce.setDayStr(ServerTimer.getYMD());
			Dao.save(ce);
		}
		return ce;
	}
	public static int getChannelNewDeviceYesterday(String channel, int today){
		int newDevice = 1;
		Session ss = HSF.getSession();
		Criteria ct = ss.createCriteria(ChannelEveryday.class);
		ct.add(Restrictions.eq("channel", channel));
		ct.add(Restrictions.eq("day", today -1));
		ct.setMaxResults(1);
		List<ChannelEveryday> list = ct.list();
		ss.close();
		if(list.size() >0){
			newDevice = list.get(0).getNewDevice();
		}
		return newDevice;
	}
	@SuppressWarnings("unchecked")
	public static List<ChannelEveryday> getChannelEverydayByChannel(String channel){
		List<ChannelEveryday> list = null;
		Session ss = HSF.getSession();
		Criteria ct = ss.createCriteria(ChannelEveryday.class);
		if(!channel.isEmpty()){
			ct.add(Restrictions.like("channel", channel));
		}
		list = ct.addOrder(Order.desc("id")).setMaxResults(300).list();
		ss.close();
		return list;
	}
	
	public static AliPay getAliPayByContent(String content){
		AliPay pay = null;
		Session ss = HSF.getSession();
		List<AliPay> list = ss.createCriteria(AliPay.class).add(Restrictions.eq("content", content)).setMaxResults(1).list();
		ss.close();
		if(list.size() >0){
			pay = list.get(0);
		}else{
			pay = new AliPay();
			pay.setFirstTime(ServerTimer.getFull());
			pay.setContent(content);
			Dao.save(pay);
		}
		return pay;
	}
//	public static LjPay getLjPayByContent(String content){
//		LjPay pay = null;
//		Session ss = HSF.getSession();
//		List<LjPay> list = ss.createCriteria(LjPay.class).add(Restrictions.eq("content", content)).setMaxResults(1).list();
//		ss.close();
//		if(list.size() >0){
//			pay = list.get(0);
//		}else{
//			pay = new LjPay();
//			pay.setFirstTime(ServerTimer.getFullWithS());
//			pay.setContent(content);
//			Dao.save(pay);
//		}
//		return pay;
//	}
	public static LjPay getLjPayByOrderID(String orderID){
		LjPay pay = null;
		Session ss = HSF.getSession();
		List<LjPay> list = ss.createCriteria(LjPay.class).add(Restrictions.eq("orderID", orderID)).setMaxResults(1).list();
		ss.close();
		if(list.size() >0){
			pay = list.get(0);
		}
		return pay;
	}
	public static String checkReceipt(String receipt, boolean sandbox)
	{
		JSONObject obj = new JSONObject();
		try{
			String urlStr = "https://buy.itunes.apple.com/verifyReceipt";
			if(sandbox)
			{
				urlStr = "https://sandbox.itunes.apple.com/verifyReceipt";
			}
            URL url = new URL(urlStr);  
            URLConnection urlConnection = url.openConnection();
            urlConnection.setDoOutput(true);  
            OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());  
            String encodedReceipt = Global.BASE64Encod(receipt);
            out.write("{\"receipt-data\" : \""+encodedReceipt+"\"}");  
            out.flush();
            out.close();
            
            // 从服务器读取响应  
            InputStream inputStream = urlConnection.getInputStream();  
            String body = IOUtils.toString(inputStream,Charset.forName("gbk"));
            obj = JSONObject.fromObject(body);
            log.info(obj.toString());
        }catch(Exception e){
            log.warning("Exception:"+e.getMessage());
        }
		return obj.toString();
	}
	private static Count count = null;
	@SuppressWarnings("unchecked")
	public static Count getCountToday()
	{
		int today = ServerTimer.distOfDay();
		if(count == null || count.getDay() != today){
			Session ss = HSF.getSession();
			List<Count> list = ss.createCriteria(Count.class).add(Restrictions.eq("day", today)).list();
			ss.close();
			if(list.size() >0)
			{
				count = list.get(0);
			}else
			{
				count = new Count();
				count.setDay(today);
				count.setDayStr(ServerTimer.getYMD());
				Dao.save(count);
			}
		}
		return count;
	}
	private static Count monthCount = null;
	public static Count getCountMonth(){
		String month = ServerTimer.getYearMonth();
		if(monthCount == null || !monthCount.getDayStr().equals(month)){
			Session ss = HSF.getSession();
			List<Count> list = ss.createCriteria(Count.class).add(Restrictions.eq("dayStr", month)).list();
			ss.close();
			if(list.size() >0)
			{
				monthCount = list.get(0);
			}else
			{
				monthCount = new Count();
				monthCount.setDayStr(ServerTimer.getYearMonth());
				Dao.save(monthCount);
			}
		}
		return monthCount;
	}
	public static List<Count> getAllDayCount(){
		Session ss = HSF.getSession();
		List<Count> list = ss.createCriteria(Count.class).add(Restrictions.gt("day", 0)).addOrder(Order.desc("id")).setMaxResults(300).list();
		ss.close();
		return list;
	}
	
	public static List<StepCount> getAllDayStepCount(){
		Session ss = HSF.getSession();
		List<StepCount> list = ss.createCriteria(StepCount.class).add(Restrictions.gt("day", 0)).addOrder(Order.desc("id")).setMaxResults(300).list();
		ss.close();
		return list;
	}
	
	public static List<Count> getAllMonthCount(){
		Session ss = HSF.getSession();
		List<Count> list = ss.createCriteria(Count.class).add(Restrictions.eq("day", 0)).addOrder(Order.desc("id")).setMaxResults(300).list();
		ss.close();
		return list;
	}
	@SuppressWarnings("unchecked")
	public static AppleProduct getWeiqiProductByLesson(int lesson)
	{
		AppleProduct product = null;
		Session ss = HSF.getSession();
		List<AppleProduct> list = ss.createCriteria(AppleProduct.class).add(Restrictions.eq("lesson", lesson)).list();
		ss.close();
		if(list.size() >0){
			product = list.get(0);
		}
		return product;
	}
	@SuppressWarnings("unchecked")
	public static AppleProduct getAppleProductByProductIdentifier(String pi)
	{
		AppleProduct product = null;
		Session ss = HSF.getSession();
		List<AppleProduct> list = ss.createCriteria(AppleProduct.class).add(Restrictions.eq("productIdentifier", pi)).list();
		ss.close();
		if(list.size() >0){
			product = list.get(0);
		}
		return product;
	}
	//此方法用于editdevice类中，注意缓存同步问题
	public static List<Device> getDevice(int id, String imei, int start, int num){
		Session ss = HSF.getSession();
		Criteria ct = ss.createCriteria(Device.class);
		if(id >0){	
			ct.add(Restrictions.eq("id", id));
			}
		if(!imei.isEmpty()){
			ct.add(Restrictions.like("imei", imei));
			}
		ct.setFirstResult(start);
		ct.setMaxResults(num);
		List<Device> list = ct.list();
		ss.close();
		//同步缓存中的device
		Device exist=getDeviceExist(id, imei);
		if(exist!=null) {
			for(Device d:list) {
				if(d.getId()==exist.getId()||exist.getImei().equals(d.getImei())) {
					list.remove(d);
					list.add(exist);
				}
			}
		}
		return list;
	}
	private static Hashtable<Integer, Device> dicIdDevice = new Hashtable<Integer, Device>();
	private static Hashtable<String, Device> dicImeiDevice = new Hashtable<String, Device>();
	private static ArrayList<Device> listDevice = new ArrayList<Device>();
	private static void addDevice(Device device){
		if(device==null||listDevice.contains(device)) {
			return;
		}
		synchronized (listDevice) {
			dicIdDevice.put(device.getId(), device);
			dicImeiDevice.put(device.getImei(), device);
			listDevice.add(device);
			if(listDevice.size() >1000){
				Device wd = listDevice.get(0);
				dicIdDevice.remove(wd.getId());
				dicImeiDevice.remove(wd.getImei());
			}
		}
	}
	private static void orderDevice(Device device){
		if(device != null){
			synchronized (listDevice) {
				listDevice.remove(device);
				listDevice.add(device);
			}
		}
	}
	
	//查找数据库中已存在的device，不会新创建数据
	@SuppressWarnings("unchecked")
	public static Device getDeviceExist(int id,String imei)	{
		if(id<1 && Global.isEmpty(imei)) {
			return null;//解决id和imei同时空的情况
		}
		Device device=null;
		if(id>=1 && dicIdDevice.containsKey(id)) {
			device=dicIdDevice.get(id);
			orderDevice(device);
		}else if(!Global.isEmpty(imei) && dicImeiDevice.containsKey(imei)) {
			device=dicImeiDevice.get(imei);
			orderDevice(device);
		}else {
			Session ss = HSF.getSession();
			Criteria cr=ss.createCriteria(Device.class);
			if(id>=1) {
				cr.add(Restrictions.eq("id", id));
			}else if(!Global.isEmpty(imei)) {
				cr.add(Restrictions.eq("imei", imei));
			}
			List<Device> list = cr.setMaxResults(1).list();
			ss.close();
			if(list.size() >0){
				device = list.get(0);
				Dao.addDevice(device);
			}
		}
		return device;
	}
	@SuppressWarnings("unchecked")
	public synchronized static Device getDevice(int id, String imei, String enter)
	{
		Device device = Dao.getDeviceExist(id, imei);
		if(device == null){
			device = new Device();
			device.setEnter(enter);
			device.setFirstTime(ServerTimer.getFull());
			Dao.save(device);
			imei=Global.isEmpty(imei)?"device"+device.getId():imei;
			device.setImei(imei);
			Dao.save(device);
			Dao.addDevice(device);
		}
		return device;
	}
	
	@SuppressWarnings("unchecked")
	public static int getOriginalSize(String orig)
	{
		Session ss = HSF.getSession();
		Criteria ct = ss.createCriteria(ApplePay.class).add(Restrictions.eq("original", orig));
		List<ApplePay> list = ct.list();
		ss.close();
		return list.size();
	}
	@SuppressWarnings("unchecked")
	public static ApplePay getOriginalPay(String orig)
	{
		Session ss = HSF.getSession();
		Criteria ct = ss.createCriteria(ApplePay.class).add(Restrictions.eq("original", orig)).setMaxResults(1);
		List<ApplePay> list = ct.list();
		ss.close();
		
		ApplePay pay = null;
		if(list.size() >0){
			pay = list.get(0);
		}
		return pay;
	}

	public static void save(Object obj){
		   Session s=HSF.getSession();
		   Transaction ts=s.beginTransaction();
		   try
		   {
			   s.saveOrUpdate(obj);
			   ts.commit();
		   }catch(Exception e)
		   {
			   log.warning(obj.toString()+" Exception !!!"+e.getMessage());
			   ts.rollback();
		   }finally{
			   s.close();
		   }
	   }
	   
	   public static void delete(Object obj){
		   Session s=HSF.getSession();
		   Transaction ts=s.beginTransaction();
		   try
		   {
			   s.delete(obj);
			   ts.commit();
		   }catch(Exception e)
		   {
			   log.warning("Exception !!!"+e.getMessage());
			   ts.rollback();
		   }finally{
			   s.close();
		   }
	   }
	public static Device getDeviceByToken(String token) {
		if(Global.isEmpty(token)) {
			return null;
		}//只须查找缓存中的数据就可以了
		Device device=null;
		for(Device dev:listDevice) {
			if(token.equals(dev.getToken())) {
				device=dev;break;
			}
		}
		if(device==null) {//当缓存中找不到用户的时候
			Session s=HSF.getSession();
			Criteria ct=s.createCriteria(Device.class).add(Restrictions.eq("token", token)).setMaxResults(1);
			List<Device> list=ct.list();
			s.close();
			if(list.size()>=1) {
				device=list.get(0);
			}
			if(device!=null) {//缓存中的token和数据库中的token不一至
				for(Device dev:listDevice) {
					if(device.getId()==dev.getId()) {
						dev=device;break;
					}
				}
			}
		}
		return device;
		
	} 
}
