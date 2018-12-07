import java.io.UnsupportedEncodingException;

import org.jboss.logging.Logger;

import cmd.CMD10;
import main.Global;
import main.ServerTimer;


public class Test {
	private static final Logger log = Logger.getLogger("Test");

	public static void main(String[] args) throws UnsupportedEncodingException {
		String str ="1111";
		String before=str.toString();
		str="222";
		show(before);
	}

	public static void show(Object obj){
		log.info(String.valueOf(obj));
	}
}
