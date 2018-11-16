package cmd;

import java.util.logging.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import main.Global;

public class CMD0 implements ICMD {
	private final Logger log = Logger.getLogger(CMD0.class.getName());
	
	@Override
	public ChannelBuffer getBytes(int cmd, ChannelBuffer data) {
		ChannelBuffer buf = ChannelBuffers.dynamicBuffer();
		buf.writeShort(cmd);
		return buf;
	}
	public static ChannelBuffer reBuf(ChannelBuffer buf,int code,String msg) {
		buf.writeByte(code);
		buf.writeBytes(Global.getUTF(msg));
		return buf;
	}

}
