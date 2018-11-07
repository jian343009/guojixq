package cmd;

import java.util.logging.Logger;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import data.Device;
import data.StepCount;
import main.Global;

public class CMD101 implements ICMD {
	private final Logger log = Logger.getLogger("CMD101");
	
	@Override
	public ChannelBuffer getBytes(int cmd, ChannelBuffer data) {
		int deviceID = data.readInt();
		String imei = Global.readUTF(data);
		int lesson = data.readByte();
		String step = Global.readUTF(data);		
		String info = "";
		String channel = "";
		Device device = null;
//		if(imei.isEmpty()){
//			 device= Dao.getDeviceExist(deviceID);
//			if(device != null){
//				imei = device.getImei();
//				channel = device.getChannel().split("#")[0];
//			}
//			info = deviceID+"#"+imei;
//		}	
		Global.addRecord(deviceID, imei, lesson+"#"+step, info);
		//Global.addStep(deviceID, imei, lesson+"#"+step, channel);
		String[] array_out={"不吸引人","想学但价格太高","习题太少缺少练习","孩子看不懂","操作不方便"};
		StepCount sc =  StepCount.getByChannelToday(channel);
		if(step.equals("打开支付单课")){
			sc.add支付统计(step).store();
		}else if(step.equals("打开支付多课")){
			sc.add支付统计(step).store();
		}else if(step.equals("开始学习")){
			sc.add单课行为(lesson, step).store();
		}else if(step.equals("结束学习")){
			sc.add单课行为(lesson, step).store();
		}else if(step.equals("完成学习")){				
			if(lesson == 1 && Global.getInt(device.getVersion()) >=7){
				sc.add单课行为(lesson, (device.getId()%2)+step);//0完成学习，1完成学习，代表奇偶用户完成学习
			}	sc.add单课行为(lesson, step).store();
		}else if(step.equals("开始练习")){
			sc.add单课行为(lesson, step).store();
		}else if(step.equals("结束练习")){
			sc.add单课行为(lesson, step).store();
		}else if(step.equals("完成练习")){
			sc.add单课行为(lesson, step).store();
		}else if(step.contains("退出应用程序选择")){
			step = step.trim();
			int num = Global.getInt(step.substring(step.trim().length()-1))-1;
			if(num >= 0 && num <=4){
			sc.add退出原因(array_out[num]).store();
			}else{
				log.info("传参超界或出出错。");
			}
		}
		ChannelBuffer buf = ChannelBuffers.dynamicBuffer();
		buf.writeShort(cmd);
		return buf;
	}

}
