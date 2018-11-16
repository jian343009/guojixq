package data;

import dao.Data;
import main.Global;

public class Count {
	
	private int id;
	private int day = 0;
	private String dayStr = "";
	private int open = 0;
	private int newDevice = 0;
	private int pay = 0;
	private int newPay = 0;
	private double totalPay = 0;
	private double aliPay = 0;
	private double wiiPay = 0;
	private double wxPay = 0;
	private double applePay = 0;
	private double hwPay = 0;
	private String detail = "";
	private String dataStr = "";
	private Data data =null;
	private String reward="";//配置文件已改
	private Data rewardData=null;
	private String ljPay="";//记录实付与应付不符的交易记录。
	
	public String getLjPay() {
		return ljPay;
	}
	public void setLjPay(String ljPay) {
		this.ljPay = ljPay;
	}
	public String getReward() {
		return reward;
	}
	public void setReward(String reward) {
		this.reward = reward;
		rewardData=null;//更新文本清空缓存
	}
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
	public int getOpen() {
		return open;
	}
	public void setOpen(int open) {
		this.open = open;
	}
	public int getNewDevice() {
		return newDevice;
	}
	public void setNewDevice(int newDevice) {
		this.newDevice = newDevice;
	}
	public int getPay() {
		return pay;
	}
	public void setPay(int pay) {
		this.pay = pay;
	}
	public int getNewPay() {
		return newPay;
	}
	public void setNewPay(int newPay) {
		this.newPay = newPay;
	}
	public double getTotalPay() {
		return totalPay;
	}
	public void setTotalPay(double totalPay) {
		this.totalPay = totalPay;
	}
	public double getAliPay() {
		return aliPay;
	}
	public void setAliPay(double aliPay) {
		this.aliPay = aliPay;
	}
	public double getWiiPay() {
		return wiiPay;
	}
	public void setWiiPay(double wiiPay) {
		this.wiiPay = wiiPay;
	}
	public double getWxPay() {
		return wxPay;
	}
	public void setWxPay(double wxPay) {
		this.wxPay = wxPay;
	}
	public double getApplePay() {
		return applePay;
	}
	public void setApplePay(double applePay) {
		this.applePay = applePay;
	}
	public double getHwPay() {
		return hwPay;
	}
	public void setHwPay(double hwPay) {
		this.hwPay = hwPay;
	}
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	public String getDataStr() {
		return dataStr;
	}
	public void setDataStr(String dataStr) {
		this.dataStr = dataStr;
	}
	
	//功能方法
	public Data getData(){
		if(data == null || !data.asString().equals(this.dataStr)){
			data = Data.fromMap(this.dataStr);
		}	return data;		
	}
	public Data getRewardData() {
		if(rewardData==null || !rewardData.asString().equals(this.reward)) {
			rewardData=Data.fromMap(this.reward);
		}	return rewardData;
	}
	public void addReturnNum(int day) {
		int[] nums = Global.getArray(Global.splitArray(this.detail), 4);
		if (day <= 0) {
			nums[0]++;
		} else if (day == 1) {
			nums[1]++;
		} else if (2 <= day && day <= 6) {
			nums[2]++;
		} else{
			nums[3]++;
		}
		this.detail = Global.concatArray(nums);
	}

	public int getReturnNum(int day) {
		int num = 0;
		int[] nums = Global.getArray(Global.splitArray(this.detail), 4);
		if (day <= 0) {
			num = nums[0];
		} else if (day == 1) {
			num = nums[1];
		} else if (2 <= day && day <= 6) {
			num = nums[2];
		} else {
			num = nums[3];
		}		
		return num;
	}
	
}
