package cmd;

import main.Global;
import main.ServerTimer;

import java.util.Arrays;

import org.jboss.logging.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import dao.Dao;
import dao.Data;
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
		if(!Global.isEmpty(imei) && !imei.equals(device.getImei())) {
			device.setImei(imei);//更新imei，双卡或重装的情况
		}
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
		//新增用户
		if(device.getOpen() ==0){
			//第二课免费分渠道，分奇偶
			if(Arrays.asList(BaseData.getContent("第二课免费的渠道"+(device.getId()%2))
					.split("#")).contains(channel)){
				device.setBuyState(buys | 4);
			}
			//仅记录可获取红包的新增用户
			if(Global.getInt(device.getVersion())>=4) {
				Data rData=count.getRewardData();
				rData.put("新增用户",rData.get("新增用户").asInt()+1);//新增用户+1
				count.setReward(rData.asString());//保存
			}
			mc.setNewDevice(mc.getNewDevice() +1);
			count.setNewDevice(count.getNewDevice() +1);
			ce.setNewDevice(ce.getNewDevice() +1);
		}
		//赠送第21课
		if((buys&(1<<21))==0 && Device.isBuyAll(buys)) {
			device.setBuyState(buys|(1<<21));
		}
		//第二课可以获取红包，就免费第二课
		if(Global.getInt(device.getVersion())>=4 && (device.getBuyState()&4)==0) {
			int type=Data.fromMap(BaseData.getContent("红包开关"))
					.get(device.getId()%2).get("type").asInt();
			if(type==0||type==2) {
				device.setBuyState(buys|4);
			}
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
