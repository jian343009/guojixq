package cmd;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import dao.Dao;
import dao.Data;
import data.BaseData;
import data.Device;
import main.Global;

import static cmd.CMD0.reBuf;

/*
 * 获取价格信息
 */
public class CMD14 implements ICMD {
	private static final Logger log = Logger.getLogger(CMD14.class.getName());

	@Override
	public ChannelBuffer getBytes(int code, ChannelBuffer data) {
		ChannelBuffer buf = ChannelBuffers.dynamicBuffer();
		buf.writeShort(code);

		int devID = data.readInt();
		int lesson = data.readInt();
		if(lesson==21) {//赠送21课
			reBuf(buf, 2, "本课为习题集，共24类200题，不能独立购买，购买前面20课，将自动赠送本课。");
		}
		Device device = Dao.getDeviceExist(devID, null);
		if (device == null) {
			reBuf(buf, 2, "没找到该用户");
		}
		int pow = 1 << lesson, buys = device.getBuyState();
		if (lesson == 0 && Device.isBuyAll(buys)) {
			reBuf(buf, 3, "您已经购买全部课程，\n请退出重新打开软件。");
		}
		if (lesson != 0 && (buys & pow) == pow) {
			reBuf(buf, 3, "该课程已购买。\n请点击\"我已购买\"按钮。");
		}
		String channel = getChannel(device);

		int price = getPrice(device, lesson);
		int	红包 = 可用红包额(device, lesson);
		红包=(红包>=price)?(price-1):红包;//红包抵扣不能超过支付价格
		
		String payChannel = Data.fromMap(BaseData.getContent("可用支付方式")).get(channel).asString();
		Data prda = BaseData.getPriceData(channel);//当前渠道的价格Data

		String msg = "", buyinfo = "", 红包抵扣 = "";
		String 送21 = (buys & (1 << 21)) == 0 ? "解锁后赠送习题集。" : "";
		int total = 0;//支付回调时用于解锁对应的课程
		
		//支付提示信息
		if (lesson == 0) {//多课支付
			List<Integer> list = new ArrayList<Integer>();
			for (int i = 2; i < 21; i++) {
				if ((buys & (1 << i)) != (1 << i)) {
					total += (1 << i);
					list.add(i);
				}
			}
			//输出支付提示信息
			if(红包>=1) {
				红包抵扣="红包抵扣"+红包+"元，还需支付"+(price-红包)+"元，";
			}
			buyinfo = "解锁第"+list.toString().replace("[", "").replace("]", "") + "课，共"
					+ list.size()+"课，原价"+getFullPrice(device)+"元，"
					+ "折后"+price+"元，"+ 红包抵扣+送21;
			price=(红包>=1)?(price-红包):price;
		} else {//单课支付
			if (红包 >= 1) {
				红包抵扣 = "(原价"+price+"元,红包抵扣"+红包+"元)";
				price = price-红包;//实付金额
			}
			送21 = Device.isBuyAll((buys | (1 << lesson))) ? 送21 : "";
			msg = prda.get("内容").get(lesson).asString();
			//输出支付提示信息
			if (msg.contains("#")) {
				buyinfo = msg.split("#")[0] + price + "元" + 红包抵扣 + msg.split("#")[1] + 送21;
			} else {
				buyinfo = price + 红包抵扣 + msg + 送21;
			}
		}
		buf.writeByte(1);
		buf.writeBytes(Global.getUTF(payChannel));
		buf.writeInt(price);
		buf.writeBytes(Global.getUTF(buyinfo));
		buf.writeInt(total);
		return buf;
	}

	public static int getPrice(Device device, int lesson) {
		if (device == null) {
			return 0;
		}
		int price = 0;
		int	buys = device.getBuyState();	
		Data priceData = BaseData.getPriceData(getChannel(device));
		if (priceData == null || Device.isBuyAll(buys)) {
			return 0;
		} else if (lesson > 1 && lesson < 22) {
			price = priceData.get("价格").get(lesson).asInt();
		} else if (lesson == 0) {
			price = getFullPrice(device);
			int 折扣 = priceData.get("价格").get("折扣").asInt();
			price = price * 折扣 / 100;
		}
		return price;
	}
	private static int getFullPrice(Device device) {
		Data priceData = BaseData.getPriceData(getChannel(device));
		int price = 0,
			buys = device.getBuyState();
		for (int i = 2; i <= 20; i++) {
			if ((buys & (1 << i)) != (1 << i)) {
				price += priceData.get("价格").get(i).asInt();
			}
		}
		return price;
	}
	
	public static int 可用红包额(Device device, int lesson) {
		if (device == null) {
			return 0;
		}
		CMD13.getStatus(device, 1);//获取价格时候更新红包状态
		CMD13.getStatus(device, 2);//解决支付时红包过期的问题
		String reward = device.getReward();
		if (Global.isEmpty(reward) || !reward.contains("未使用")) {
			return 0;//提高无可用红包时的效率
		}
		int 红包 = 0;
		Data rd = device.getRewardData();
		if (canUseReward(device, lesson)) {
			for (int les : new int[] { 1, 2 }) {
				if ("未使用".equals(rd.get(les).get("状态").asString())) {
					红包 += rd.get(les).get("金额").asInt();
				}
			}
		}
		return 红包;
	}

	private static String getChannel(Device device) {
		String channel = device.getChannel();
		channel = "华为平台,苹果平台,乐视电视,oppo联运".contains(channel) ? channel : "其它平台";
		return channel;
	}

	// 当前用户是否可以使用红包
	public static boolean canUseReward(Device device, int lesson) {
		if (device == null) {
			return false;
		}
		//不能使用红包的课程的状态在其它地方刷新状态
		Data rsdata = CMD13.getRewardSwitch(device.getId());
		String limit = rsdata.get("buyType").asString();
		if ("3".equals(rsdata.get("type").asString())) {
			return false;
		} else {
			// buyType,0通用,1多课
			if ("0".equals(limit) || ("1".equals(limit) && lesson == 0)) {
				return true;//通用和多课支付，都可以使用
			}
		}
		return false;
	}

}
