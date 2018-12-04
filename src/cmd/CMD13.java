package cmd;

import java.util.logging.Logger;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import dao.Dao;
import dao.Data;
import data.BaseData;
import data.Count;
import data.Device;
import main.Global;
import main.ServerTimer;

import static cmd.CMD0.reBuf;
/*
 * 获取红包信息
 */
public class CMD13 implements ICMD {
	private static final Logger log = Logger.getLogger(CMD13.class.getName());

	@Override
	public ChannelBuffer getBytes(int code, ChannelBuffer recive) {
		ChannelBuffer buf = ChannelBuffers.dynamicBuffer();
		buf.writeShort(code);
		String name = Global.readUTF(recive);
		int devID = recive.readInt();
		String channel = Global.readUTF(recive);
		int lesson = recive.readInt();
		Device device = Dao.getDeviceExist(devID, null);
		if (device == null) {
			return reBuf(buf, 2, "没找到用户");
		}
		Data devData = device.getRewardData();
		// {1:{"金额":4,"状态":"已使用",time:24242},2:{"金额":7,"状态":"未获取",time:24242}}
		Data 红包开关 = getRewardSwitch(device.getId());
		//红包开关:{1:{"type":0,"buyType":1},0:{"type":0,"buyType":1}}
		//控制可以生成红包的课程的开关
		if ("生成红包".equals(name)) {
			if (红包开关.get("type").asInt() == 3) {
				return reBuf(buf, 2, "红包功能已关闭");
			}
			if (Device.isBuyAll(device.getBuyState())) {// 有未购买课程
				return reBuf(buf, 2, "已全部购买");
			}
			if (lesson < 1 || lesson > 2) {
				return reBuf(buf, 2, "非法的课程");
			}
			if (!devData.get(lesson).get("状态").asString().isEmpty()) {
				return reBuf(buf, 2, "当前课程的红包状态是：" + devData.get(lesson).get("状态").asString());
			}
			int cash =0;//生成红包的金额
			int type=红包开关.get("type").asInt();//红包生成条件
			if ( type == 0) {
				int other = (lesson == 1 ? 2 : 1);
				int cash2 = devData.get(other).get("金额").asInt();
				cash = Global.getRandom((cash2 == 0 ? 10 : 11) - cash2) + 1;
			} else if (type == lesson) {// 限制可生成红包的课程
				cash = Global.getRandom(2) + 8;//只有一个课红包，额度为8-10元
			}else {
				return reBuf(buf, 2, "当前课程红包功能已关闭");
			}
			int time = ServerTimer.distOfMinute();//当前时间，计算红包有效时间用
			devData.getMap(lesson)
				.put("金额", cash)
				.put("状态", "未使用")
				.put("time", time);// 红包生成时间
			device.setReward(devData.asString());
			Dao.save(device);
				
			// 生成日记录统计
			Count count = Dao.getCountToday();
			Data couData = count.getRewardData();
			Data couData2 = couData.getMap("红包生成").getMap(lesson);//简化data
			couData2.put("次数", couData2.get("次数").asInt() + 1);
			couData2.put("金额", couData2.get("金额").asInt() + cash);
			count.setReward(couData.toString());
			Dao.save(count);
		}
		
		//获取红包信息
		String sta1 = getStatus(device, 1);//更新红包状态，
		String sta2 = getStatus(device, 2);//因为红包有效期
		
		String msg = "<Rewards>\n" 
			+ "<Reward lesson=\"1\" money=\"" + devData.get(1).get("金额").asInt()  
				+ "\" status=\""+ sta1 + "\" time=\""+residueTime(device,1)+"\"/>\n" 
			+ "<Reward lesson=\"2\" money=\"" + devData.get(2).get("金额").asInt() 
				+ "\" status=\""+ sta2 + "\" time=\""+residueTime(device,2)+"\"/>\n" 
			+ "<Reward type=\"" + 红包开关.get("type").asInt()+ "\" buyType=\""
					+ 红包开关.get("buyType").asInt() + "\" />\n" 
			+ "</Rewards>";

		log.info("device=" + devID + ",reward=" + devData.asString() + "\n" + msg);
		return reBuf(buf, 1, msg);
	}
	//剩余时间，这是个显示状态, residue:剩余的
	private static String residueTime(Device device,int lesson) {
		if(device==null || Global.isEmpty(device.getReward())) {
			return "未获取";//完全没有红包信息的情况
		}
		Data rewardData=device.getRewardData();
		// {1:{"金额":4,"状态":"已使用","time":24242},2:{"金额":7,"状态":"未获取","time":24242}}
		String status=rewardData.get(lesson).get("状态").asString();
		if(Global.isEmpty(status)) {
			return "未获取";//当前课程无红包信息
		}else if("未使用".equals(status)) {
			int time=rewardData.get(lesson).get("time").asInt();
			int 红包有效期 = get红包有效期(device.getId());
			if(time==0) {//有状态没时间,解决无时间限制问题
				return "已过期";
			}
			int nowTime = ServerTimer.distOfMinute();
			int lastTime = 红包有效期 - (nowTime - time);
			if (lastTime / 1440 > 0) {
				status =  "剩余" + (lastTime / 1440) + "天";
			} else if (lastTime / 60 > 0) {
				status =  "剩余" + (lastTime / 60) + "小时";
			} else if(lastTime>0){
				status = "剩余" + lastTime + "分钟";
			}else {
				status = "已过期";	
			}
		}
		return status;
	}
	
	//判断红包是否过期，以状态为准，可以会取消有效期，这个前台判断用的状态
	public static String getStatus(Device device,int lesson) {
		if(device==null || Global.isEmpty(device.getReward())) {
			return "未获取";//完全没有红包信息的情况
		}
		Data reData=device.getRewardData();
		String status=reData.get(lesson).get("状态").asString();
		if(Global.isEmpty(status)) {
			status="未获取";//当前课程没有红包信息的情况
		}else if("未使用".equals(status)&&"已过期".equals(residueTime(device, lesson))) {
			reData.getMap(lesson).put("状态", "已过期");
			device.setReward(reData.asString());
			Dao.save(device);
			status="已过期";
		}
		return status;
	}
	public static Data getRewardSwitch(int devID) {
		return Data.fromMap(BaseData.getContent("红包开关")).get(devID % 2);
	}
	private static int get红包有效期(int devID) {
		return Global.getInt(BaseData.getContent("红包有效期" + (devID % 2)));
	}
}
