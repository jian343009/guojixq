package data;

import java.util.Hashtable;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import dao.Data;
import dao.HSF;
import main.ServerTimer;

public class StepCount extends Database {
	
	private int id;
	private int day = 0;
	private String dayStr = "";
	private String channel = "";
	private String detail = "";
	private Data data = null;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
	public String getDayStr() {
		return dayStr;
	}
	public void setDayStr(String dayStr) {
		this.dayStr = dayStr;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	public Data getData(){
		if(this.data == null){
			this.data = Data.fromMap(this.detail);
		}
		return this.data;
	}

	public StepCount add支付统计(String step){
		int num = this.getData().get(step).asInt() +1;
		this.getData().put(step, num);
		this.detail = this.getData().toString();
		return this;
	}
	public StepCount add单课行为(int lesson, String step){
		int num = this.getData().get(lesson).get(step).asInt() +1;
		this.getData().getMap(lesson).put(step, num);
		this.detail = this.getData().toString();
		return this;
	}

	private static Hashtable<String, StepCount> dicStepCount = new Hashtable<String, StepCount>();
	public static StepCount getByChannelToday(String channel){
		if(!channel.equals("华为平台")){
			channel = "其他平台";
		}
		int today = ServerTimer.distOfDay();
		StepCount sc = dicStepCount.get(channel);
		if(sc == null){
			Session ss = HSF.getSession();
			List<StepCount> list = ss.createCriteria(StepCount.class).add(Restrictions.eq("channel", channel)).add(Restrictions.eq("day", today)).list();
			ss.close();
			if(list.size() >0){
				sc = list.get(0);
			}else{
				sc = new StepCount();
				sc.setDay(today);
				sc.setDayStr(ServerTimer.getYMD());
				sc.setChannel(channel);

				dicStepCount.put(channel, sc);
			}
		}else if(sc.getDay() != today){
			sc.store();
			
			sc = new StepCount();
			sc.setDay(today);
			sc.setDayStr(ServerTimer.getYMD());
			sc.setChannel(channel);

			dicStepCount.put(channel, sc);
		}
		return sc;
	}
	
	public static List<StepCount> getAll(){
		Session ss = HSF.getSession();
		List<StepCount> list = ss.createCriteria(StepCount.class).addOrder(Order.desc("id")).setMaxResults(300).list();
		ss.close();
		return list;
	}
}
