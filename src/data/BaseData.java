package data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import dao.Data;
import dao.HSF;
import main.Global;

public class BaseData {
	
	public static final String 强制全部解锁 = "强制全部解锁";
	public static final String 微信网页支付地址 = "微信网页支付地址";
	public static final String 可用支付方式="可用支付方式";
	//public static final int total=2097148;
	
	
	private int id;
	private String name = "";
	private String content = "";
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	/*
	 * baseData的基本查询及操作方法
	 */
	//baseData后台管理查询
	public static List<BaseData> getAllBaseData(){
		bdMap.clear();//改basedata时清空缓存
		priceDataMap.clear();//改basedata时清空缓存
		return getList(null, null);
	}
	//baseData后台修改方法
	public static BaseData getByID(int id) {
		for(BaseData bd:getAllBaseData()) {
			if(bd.getId()==id) { return bd; }
		}
		return null;
	}
	//baseData后台修改方法
	public static BaseData getByName(String name){
		BaseData bd = bdMap.get(name);
		if(bd != null) { return bd;	}
		List<BaseData> list = getList("name",name);
		if(list.size() >0){
			bd = list.get(0);
			bdMap.put(bd.getName(), bd);
		}
		return bd;
	}
	public static String getContent(String name){
		if(getByName(name) != null){
			return getByName(name).getContent();
		}return "";
	}
	
	/*
	 * 根查询方法
	 */
	private static List<BaseData> getList(String name,String content){
		Session ss = HSF.getSession();
		Criteria c=ss.createCriteria(BaseData.class);
		if(!Global.isEmpty(name)) {
			c.add(Restrictions.eq(name, content));
		}
		List<BaseData> list = c.list();
		ss.close();
		return list;
	}
	public static Data getPriceData(String channel) {
		Data priceData=priceDataMap.get(channel);
		if(priceData==null) {
			priceData=Data.fromMap(BaseData.getContent(channel));
			priceDataMap.put(channel, priceData);
		}
		return priceData;
	}
	
	/*  baseData缓存map  */
	private static final Map<String, BaseData> bdMap=new HashMap<String, BaseData>();
	private static final Map<String,Data> priceDataMap=new HashMap<String, Data>();
	
}
