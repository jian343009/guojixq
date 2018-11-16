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

public class CMD14 implements ICMD {
	private static final Logger log=Logger.getLogger(CMD14.class.getName());
	@Override
	public ChannelBuffer getBytes(int code, ChannelBuffer data) {
		ChannelBuffer buf=ChannelBuffers.dynamicBuffer();
		buf.writeShort(code);
		
		int devID=data.readInt();
		int lesson=data.readInt();
		Device device=Dao.getDeviceExist(devID, null);
		if(device==null) {
			reBuf(buf, 2, "没找到该用户");
		}
		int pow=1<<lesson,buys=device.getBuyState();
		if(lesson==0 && Device.isBuyAll(buys)) {
			reBuf(buf, 3, "您已经购买全部课程，\n请退出重新打开软件。");
		}else if(lesson!=0 && (buys&pow)==pow){
			reBuf(buf, 3, "该课程已购买。\n请点击\"我已购买\"按钮。");
		}
		String channel=getChannel(device);
		
		int price=getPrice(device, lesson),
			红包=可用红包额(device,lesson);
		String payChannel=Data.fromMap(BaseData.getContent("可用支付方式")).get(channel).asString();
		Data prda=BaseData.getPriceData(channel);
		
		//log.info("device="+devID+",lesson="+lesson+",price="+price+",reward="+红包+",channel="+channel);
		
		String msg="",buyinfo="",红包抵扣="";
		if(红包>=1) {
			红包抵扣="(原价" +(price+红包)+"元,红包抵扣"+红包+"元)";
		}
		String 送21=(buys & (1<<21))==0?"(赠送第21课)":"";
		int total=0;
		if(lesson==0) {
			List<Integer> list=new ArrayList<Integer>();
			for(int i=2;i<21;i++) {
				if((buys & (1<<i))!=(1<<i)) {		
					total+=(1<<i);	list.add(i);
				}
			}
			buyinfo="支付"+price+"元"+红包抵扣+",解锁第"+
				list.toString().replace("[", "").replace("]", "")
				+"课，共"+list.size()+"课"+送21+"。";
		}else {
			送21=Device.isBuyAll((buys|(1<<lesson)))?送21:"";
			msg=prda.get("内容").get(lesson).asString();
			if(msg.contains("#")) {
				buyinfo=msg.split("#")[0]+price+"元"+红包抵扣+msg.split("#")[1]+送21;
			}else {
				buyinfo=price+红包抵扣+msg+送21;
			}
		}
		buf.writeByte(1);
		buf.writeBytes(Global.getUTF(payChannel));
		buf.writeInt(price);
		buf.writeBytes(Global.getUTF(buyinfo));
		buf.writeInt(total);
		return buf;
	}
	public static int getPrice(Device device,int lesson) {
		if(device==null) {
			return 0;
		}
		int price=0,buys=device.getBuyState();
		Data priceData=BaseData.getPriceData(getChannel(device));
		if(priceData==null||Device.isBuyAll(buys)) {
			return 0;
		}else if(lesson>1&&lesson<22) {
			price=priceData.get("价格").get(lesson).asInt();
		}else if(lesson==0) {
			int 折扣=priceData.get("价格").get("折扣").asInt();
			for(int i=2;i<=20;i++) {
				if((buys&(1<<i))!=(1<<i)) {
					price+=priceData.get("价格").get(i).asInt();
				}
			}
			price=price*折扣/100;
		}
		return price-可用红包额(device,lesson);
	}
	public static int 可用红包额(Device device,int lesson) {
		if(device==null) {	return 0;	}
		String reward=device.getReward();
		if(Global.isEmpty(reward) || !reward.contains("未使用")){
			return 0;		}
		int 红包=0;	Data rd=device.getRewardData();
		String 条件=BaseData.getContent("红包限制");
		if( "通用".equals(条件) || ("多课".equals(条件)&&lesson==0) ) {
			for(int les:new int[]{1,2}) {
				if("未使用".equals(rd.get(les).get("状态").asString())){
					红包+=rd.get(les).get("金额").asInt();
				}
			}
		}
		return 红包;
	}
	private static String getChannel(Device device) {
		String channel=device.getChannel();
		channel="华为平台，苹果平台，乐视电视".contains(channel)?channel:"其它平台";
		return channel;
	}
	

}
