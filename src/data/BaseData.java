package data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import dao.HSF;

public class BaseData {
	
	public static final String 强制全部解锁 = "强制全部解锁";
	public static final String 微信网页支付地址 = "微信网页支付地址";
	
	
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
	public static List<BaseData> getAllBaseData(){
		bdMap.clear();
		return getList(null, null);
	}
	public static BaseData getByID(int id) {
		for(BaseData bd:getAllBaseData()) {
			if(bd.getId()==id) { return bd; }
		}
		return null;
	}
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
		}return null;
	}
	private static List<BaseData> getList(String name,String content){
		Session ss = HSF.getSession();
		Criteria c=ss.createCriteria(BaseData.class);
		if(name!=null) {
			c.add(Restrictions.eq(name, content));
		}
		List<BaseData> list = c.list();
		ss.close();
		return list;
	}
	/*  baseData缓存map  */
	private static final Map<String, BaseData> bdMap=new HashMap<String, BaseData>();
	
}
