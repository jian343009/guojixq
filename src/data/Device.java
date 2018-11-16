package data;

import com.sun.org.apache.regexp.internal.recompile;

import cmd.CMD14;
import dao.Dao;
import dao.Data;
import main.Global;

public class Device {
	
	private int id;
	private String imei = "";
	private String enter = "";
	private String firstTime = "";
	private int lastDay = 0;
	private String lastTime = "";
	private int openState = 0;
	private int buyState = 0;
	private int open = 0;
	private int buy = 0;
	private int offbuy = 0;
	private String regChannel = "";
	private String channel="";
	private String version = "";
	private String extra = "";
	private String token="";
	private int unlocky=0;
	private String reward="";//配置文件已改
	
	public String getReward() {
		return reward;
	}
	public void setReward(String reward) {
		this.reward = reward;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public int getUnlocky() {
		return unlocky;
	}
	public void setUnlocky(int unlocky) {
		this.unlocky = unlocky;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	public String getEnter() {
		return enter;
	}
	public void setEnter(String enter) {
		this.enter = enter;
	}
	public String getFirstTime() {
		return firstTime;
	}
	public void setFirstTime(String firstTime) {
		this.firstTime = firstTime;
	}
	public int getLastDay() {
		return lastDay;
	}
	public void setLastDay(int lastDay) {
		this.lastDay = lastDay;
	}
	public String getLastTime() {
		return lastTime;
	}
	public void setLastTime(String lastTime) {
		this.lastTime = lastTime;
	}
	public int getOpenState() {
		return openState;
	}
	public void setOpenState(int openState) {
		this.openState = openState;
	}
	public int getBuyState() {
		return buyState;
	}
	public void setBuyState(int buyState) {
		this.buyState = buyState;
	}
	public int getOpen() {
		return open;
	}
	public void setOpen(int open) {
		this.open = open;
	}
	public int getBuy() {
		return buy;
	}
	public void setBuy(int buy) {
		this.buy = buy;
	}
	public int getOffbuy() {
		return offbuy;
	}
	public void setOffbuy(int offbuy) {
		this.offbuy = offbuy;
	}
	public String getRegChannel() {
		return regChannel;
	}
	public void setRegChannel(String regChannel) {
		this.regChannel = regChannel;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getExtra() {
		return extra;
	}
	public void setExtra(String extra) {
		this.extra = extra;
	}
	
	public Data getRewardData() {
		return Data.fromMap(this.reward);
		
	}
	public int getUnlockNum(int lesson){
		return Global.getInt(Global.getArrayValue(this.extra, lesson));
	}
	public void setUnlockNum(int lesson, int num){
		this.extra = Global.setArrayValue(this.extra, lesson, ""+num);
	}
	public void modUnlockNum(int lesson, int num){
		int m = this.getUnlockNum(lesson);
		m += num;
		this.extra = Global.setArrayValue(this.extra, lesson, ""+m);
	}
	@Override
	public String toString() {
		return this.id+"#"+this.imei;
	}
	//比对价格和使用红包
	public static void checkPrice(Device device,int lesson,int money,int ljpayID) {
		if(device==null||Global.getInt(device.getVersion())<4) {//版本控制
			return;
		}
		Count count=Dao.getCountToday();
		if(money==CMD14.getPrice(device, lesson)) {
			int 红包=CMD14.可用红包额(device, lesson);
			if(红包>=1) {
				Data rewardData=device.getRewardData();
				for(int i:new int[] {1,2}) {
					if("未使用".equals(rewardData.get(i).get("状态").asString())) {
						rewardData.getMap(i).put("状态", "已使用");
					}
				}
				Data cd=count.getRewardData();
				Data used=cd.getMap("红包使用");
				used.put("总次数", used.get("总次数").asInt()+1)
					.put("总金额", used.get("总金额").asInt()+红包);
				if(lesson==0) {
					used.put("多课次数", used.get("多课次数").asInt()+1)
						.put("多课金额", used.get("多课金额").asInt()+红包);
				}else {
					used.put("单课次数", used.get("单课次数").asInt()+1)
						.put("单课金额", used.get("单课金额").asInt()+红包);
				}
				device.setReward(rewardData.asString());
				count.setReward(cd.asString());
				Dao.save(device);
				Dao.save(count);
			}
		}else {
			count.setLjPay(count.getLjPay()+ljpayID+"#");
		}
	}
	public static boolean isBuyAll(int buyState) {
		for(int i=2;i<=20;i++) {
			int pow=1<<i;
			if((buyState&pow)!=pow) {
				return false;
			}
		}
		return true;
	}
	
}
