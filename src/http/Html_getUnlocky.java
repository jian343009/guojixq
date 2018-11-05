package http;

import java.util.HashMap;
import java.util.logging.Logger;

import cmd.CMD10;
import dao.Dao;
import data.Device;
import main.Global;

public class Html_getUnlocky extends Html {
	private static final Logger log = Logger.getLogger(Html_getUnlocky.class.getName());

	@Override
	public String getHtml(String content) {		
		HashMap<String, String> map = Global.decodeUrlParam(content);		
		Device device = Dao.getDeviceByToken(map.get("token"));
		int lesson = Global.getInt(map.get("lesson"));
		String code = "";		
		if (lesson > 1 && device != null) {
			int unlockMark = device.getUnlocky();			
			int bought = device.getBuyState();// 取出用户购买记录
			log.info("bought--------"+bought);
			int pow = 1 << lesson;
			log.info("lesson = " + lesson);
			log.info("请的的code----" + unlockMark + "----------------请的的code");
			
			if ((bought & pow) == pow) {// 是否购买
				if (device.getUnlockNum(lesson) <= 5) {// 解锁次数
					unlockMark = CMD10.next(unlockMark, lesson);
					code += unlockMark;
					log.info("解锁：" + device.getExtra() + "code = " + code);
					device.modUnlockNum(lesson, 1);
					Dao.save(device);
					return code;
				}
			}
		}
		log.info("这个是返回到服务器的code---------------"+code);
		return code;
	}

}
