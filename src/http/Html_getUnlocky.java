package http;

import java.util.HashMap;
import java.util.logging.Logger;

import cmd.CMD10;
import dao.Dao;
import data.BaseData;
import data.Device;
import main.Global;

public class Html_getUnlocky implements IHtml {
	private static final Logger log = Logger.getLogger(Html_getUnlocky.class.getName());

	@Override
	public String getHtml(String content) {		
		HashMap<String, String> map = Global.decodeUrlParam(content);		
		int lesson = Global.getInt(map.get("lesson"));
		Device device = Dao.getDeviceByToken(map.get("token"));
		if(device==null) {
			log.info("未找到用户");
			return "未找到用户";
		}
		int unlockMark = device.getUnlocky();			
		int bought = device.getBuyState();// 取出用户购买记录
		log.info("device="+device.getId()+",lesson="+lesson+",unlocky="+device.getUnlocky());
		int pow = 1 << lesson;
		boolean buy=(bought & pow) == pow && (device.getUnlockNum(lesson) <= 5);
		boolean 强制解锁=BaseData.getContent(BaseData.强制全部解锁)
				.contains("#"+device.getChannel()+device.getVersion()+"#");
		if (buy || lesson==22 || 强制解锁) {// 是否购买，22课免费
			unlockMark = CMD10.next(unlockMark, lesson);
			String code = unlockMark+"";
			device.modUnlockNum(lesson, 1);
			Dao.save(device);
			return code;
		}
		return "不能解锁";
	}

}
