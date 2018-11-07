package cmd;

import java.util.logging.Logger;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import dao.Dao;
import dao.Data;
import data.Count;
import data.Device;
import main.Global;

public class CMD13 implements ICMD {
	private static final Logger log=Logger.getLogger(CMD13.class.getName());

	@Override
	public ChannelBuffer getBytes(int code, ChannelBuffer recive) {
		ChannelBuffer buf=ChannelBuffers.dynamicBuffer();
		buf.writeShort(code);
		String name=Global.readUTF(recive);	int devID=recive.readInt();
		String channel=Global.readUTF(recive);	int lesson=recive.readInt();
		Device device=Dao.getDeviceExist(devID, null);
		if(device==null) {
			return backBuffer(buf, 2, "没找到用户");
		}
		Data devData = device.getRewardData();
		// {1:{"金额":4,"状态":"已使用"},2:{"金额":7,"状态":"未获取"}}
		if("生成红包".equals(name)) {
			if (device.getBuyState() == 131068 ) {//有未购买课程
				return backBuffer(buf,2, "已全部购买");
			}else if(lesson < 1 || lesson > 3){
				return backBuffer(buf,2, "非法的课程");
			}else if (!devData.get(lesson).get("状态").asString().isEmpty()) {
				return backBuffer(buf,2, "当前课程的红包状态是："+devData.get(lesson).get("状态").asString());
			}
			
			int cash = 0;
			int other = (lesson == 1 ? 2 : 1);
			int cash2 = devData.get(other).get("金额").asInt();
			cash = Global.getRandom((cash2 == 0 ? 10 : 11) - cash2) + 1;
			devData.getMap(lesson).put("金额", cash).put("状态", "未使用");
			log.info( "device:"+devID+",生成的红包金额=" + cash);
			//日记录统计
			Count count = Dao.getCountToday();
			Data couData = count.getRewardData();
			Data couData2 = couData.getMap("红包生成").getMap(lesson);
			couData2.put("次数", couData2.get("次数").asInt()+1);
			couData2.put("金额", couData2.get("金额").asInt()+cash);
			count.setReward(couData.toString());			
			device.setReward(devData.asString());
			Dao.save(count);
			Dao.save(device);
		}
		String sta1 = devData.get(1).get("状态").asString();
		String sta2 = devData.get(2).get("状态").asString();
		sta1 = sta1.isEmpty() ? "未获取" : sta1;
		sta2 = sta2.isEmpty() ? "未获取" : sta2;
		String msg = "<Rewards><Reward lesson=\"1\" money=\"" + devData.get(1).get("金额").asInt() + "\" status=\"" + sta1
				+ "\"/><Reward lesson=\"2\" money=\"" + devData.get(2).get("金额").asInt() + "\" status=\"" + sta2
				+ "\"/></Rewards>";
		//log.info(msg);
		log.info("device="+devID+",reward="+device.getReward()+"\n"+msg);
		return backBuffer(buf,1, msg);
	}
	private ChannelBuffer backBuffer(ChannelBuffer buf,int code,String msg) {
		buf.writeByte(code);
		buf.writeBytes(Global.getUTF(msg));
		return buf;
	}

}
