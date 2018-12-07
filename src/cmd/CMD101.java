package cmd;

import java.util.logging.Logger;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

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
		//record表
		Global.addRecord(deviceID, imei, lesson+"#"+step, info);
		//StepCount表
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
			sc.add单课行为(lesson, step).store();
		}else if(step.equals("开始练习")){
			sc.add单课行为(lesson, step).store();
		}else if(step.equals("结束练习")){
			sc.add单课行为(lesson, step).store();
		}else if(step.equals("完成练习")){
			sc.add单课行为(lesson, step).store();
		}
		ChannelBuffer buf = ChannelBuffers.dynamicBuffer();
		buf.writeShort(cmd);
		return buf;
	}

}
