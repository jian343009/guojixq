package http;

import dao.Dao;
import data.ChannelEveryday;
import data.Count;
import data.Device;
import data.LjPay;
import main.ServerTimer;

public class AddPayRecord {
	public static void addPayRecord(Device wd,double money,int lesson,String channel,int total) {
		Count mc = Dao.getCountMonth();//月记录
		Count count = Dao.getCountToday();//日记录
		mc.setPay(mc.getPay() +1);//支付次数加一
		count.setPay(count.getPay() +1);//支付次数加一
		//每日渠道统计
		ChannelEveryday ce = Dao.getChannelEverydayToday(channel);
		ce.setPay(ce.getPay() +1);//渠道支付次数加一
		//渠道记录
		if("华为平台".equals(channel)) {
			mc.setHwPay(mc.getHwPay() + money);
			count.setHwPay(count.getHwPay() + money);
			ce.setHwPay(ce.getHwPay() + money);
		}else if("oppo联运".equals(channel)) {
			mc.setOppoPay(mc.getOppoPay()+money);
			count.setOppoPay(count.getOppoPay()+money);
			ce.setOppoPay(ce.getOppoPay()+money);
		}
		//新用户支付
		if(wd.getBuy() ==0){
			mc.setNewPay(mc.getNewPay() +1);
			count.setNewPay(count.getNewPay() +1);
			ce.setNewPay(ce.getNewPay() +1);
		}
		//总计支付额
		mc.setTotalPay(mc.getTotalPay() + money);
		count.setTotalPay(count.getTotalPay() + money);
		Dao.save(mc);
		
		//价格比对+红包使用
		Device.checkPrice(wd, lesson, (int)money);
		//AB测试支付记录
		Device.recordABpay(wd, (int)money, count, lesson);
		Dao.save(count);
		ce.setTotalPay(ce.getTotalPay() + money);
		Dao.save(ce);
		//修改用户的支付结果
		if(lesson ==0){//多课支付
			wd.setLastDay(ServerTimer.distOfDay());
			wd.setLastTime(ServerTimer.getFull());
			wd.setBuyState(wd.getBuyState() | total);
			wd.setBuy(wd.getBuy() +1);
			Dao.save(wd);
		}else{//单课支付
			int pow = 1<<lesson;
			wd.setLastDay(ServerTimer.distOfDay());
			wd.setLastTime(ServerTimer.getFull());
			wd.setBuyState(wd.getBuyState() | pow);
			wd.setBuy(wd.getBuy() +1);
			wd.setUnlockNum(lesson, 0);
			Dao.save(wd);
		}
	}
}
