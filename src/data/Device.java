package data;

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
	private Data rewardData=null;
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
		if(rewardData==null) {
			rewardData=Data.fromMap(this.reward);
		}	return rewardData;
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
	
}
