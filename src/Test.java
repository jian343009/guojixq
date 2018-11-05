import java.io.UnsupportedEncodingException;

import org.jboss.logging.Logger;

import dao.Data;
import main.Global;
import main.ServerTimer;


public class Test {
	private static final Logger log = Logger.getLogger("Test");

	/**
	 * @param args
	 * @throws UnsupportedEncodingException 
	 */
	public static void main(String[] args) throws UnsupportedEncodingException {
		String step = "aasweeddd7";
		int num = Global.getInt(step.substring(step.trim().length()-1))-1;
		show(String.valueOf(num));
	}

	public static void show(String str){
		log.info(str);
	}
}
