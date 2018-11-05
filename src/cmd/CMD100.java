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
		String channel = Global.readUTF(data).split("#")[0];
		String version = Global.readUTF(data);
		Global.addRecord(deviceID, imei, "CMD100启动", channel+version);
		Device device = Dao.getDevice(deviceID, imei, "CMD100");
		//channel带token的时候
		if(channel!=null && channel.contains("#")){
			String token=Global.splitStringArray(channel, "#")[1];
			if(!token.isEmpty()){
				device.setToken(token);
				device.setUnlocky(CMD10.getUnlockyByToken(token));
				log.info("device = " +device +"-----token = "+token);
			}
			channel = Global.splitStringArray(channel, "#")[0];
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
			if(Global.getInt(device.getVersion())>=7){
				count.add奇偶返回(today - firstDay, device.getId());
			}else{
				count.add奇偶返回(today - firstDay, 0);
			}
			ce.setOpen(ce.getOpen() +1);
			ce.addReturnNum(today - firstDay);
		}
		if(device.getOpen() ==0){
			if(channel.equals("华为平台")){
				device.setBuyState(device.getBuyState() | 4);
			}
			if(Global.getInt(version)==7){
				count.add新增用户(7);
			}else{
				count.add新增用户(0);//0版本代表非7版本用户
			}
			mc.setNewDevice(mc.getNewDevice() +1);
			count.setNewDevice(count.getNewDevice() +1);
			ce.setNewDevice(ce.getNewDevice() +1);
		}
		Dao.save(mc);
		Dao.save(count);
		Dao.save(ce);
		
		device.setOpen(device.getOpen() +1);
		device.setLastDay(ServerTimer.distOfDay());
		device.setLastTime(ServerTimer.getFull());
		Dao.save(device);
		
		ChannelBuffer buf = ChannelBuffers.dynamicBuffer();
		buf.writeShort(cmd);
		buf.writeInt(device.getId());
		buf.writeInt(BaseData.getContent(BaseData.强制全部解锁).contains("#"+channel+version+"#") ? Integer.MAX_VALUE : device.getBuyState());
		buf.writeBytes(Global.getUTF(""));
		return buf;
	}

}
