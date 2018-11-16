package cmd;

import main.Global;
import main.ServerTimer;

import org.jboss.logging.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import dao.Dao;
import data.*;
/**
 * 启动应用
 */
public class CMD100 implements ICMD {
	private final Logger log = Logger.getLogger(CMD100.class);
	
	@Override
	public ChannelBuffer getBytes(int cmd, ChannelBuffer data) {
		int deviceID = data.readInt();
		String imei = Global.readUTF(data);
		String channel = Global.readUTF(data);
		String version = Global.readUTF(data);
		Global.addRecord(deviceID, imei, "CMD100启动", channel+version);
		Device device = Dao.getDevice(deviceID, imei, "CMD100");
		//channel带token的时候
		if(!Global.isEmpty(channel) && channel.contains("#")){
			String token=channel.split("#")[1];
			if(!Global.isEmpty(token)){
				device.setToken(token);
				device.setUnlocky(CMD10.getUnlockyByToken(token));
			}
			channel = channel.split("#")[0];
		}
		
		if(!device.getRegChannel().contains(channel)){
			device.setRegChannel(device.getRegChannel() + channel + "#");
		}
		device.setChannel(channel);
		device.setVersion(version);
		
		Count mc = Dao.getCountMonth();
		Count count = Dao.getCountToday();
		ChannelEveryday ce = Dao.getChannelEverydayToday(channel);
		int today = ServerTimer.distOfDay();
		if(device.getLastDay() != today){
			int firstDay = ServerTimer.distOfDay(ServerTimer.getCalendarFromString(device.getFirstTime()));
			mc.setOpen(mc.getOpen() +1);
			count.setOpen(count.getOpen() +1);
			count.addReturnNum(today - firstDay);
			ce.setOpen(ce.getOpen() +1);
			ce.addReturnNum(today - firstDay);
		}
		int buys=device.getBuyState();
		if(device.getOpen() ==0){	//新增用户
			if(!channel.equals("苹果平台")){
				device.setBuyState(buys | 4);
			}
			mc.setNewDevice(mc.getNewDevice() +1);
			count.setNewDevice(count.getNewDevice() +1);
			ce.setNewDevice(ce.getNewDevice() +1);
		}
		if((buys&(1<<21))==0 && Device.isBuyAll(buys)) {	//赠送第21课
			device.setBuyState(buys|(1<<21));
		}
		Dao.save(mc);
		Dao.save(count);
		Dao.save(ce);
		
		device.setOpen(device.getOpen() +1);
		device.setLastDay(ServerTimer.distOfDay());
		device.setLastTime(ServerTimer.getFull());
		Dao.save(device);
		
		boolean 强制解锁=BaseData.getContent(BaseData.强制全部解锁).contains("#"+channel+version+"#");
		ChannelBuffer buf = ChannelBuffers.dynamicBuffer();
		buf.writeShort(cmd);
		buf.writeInt(device.getId());
		buf.writeInt( 强制解锁? Integer.MAX_VALUE : device.getBuyState());
		buf.writeBytes(Global.getUTF(""));
		return buf;
	}

}
