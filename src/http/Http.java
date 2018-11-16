package http;


public class Http {
	private static final String head="<html>"+
			"<head>"+
			"<meta content=\"notranslate\" name=\"google\">"+
			"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">"+
			"<meta name='viewport' content='width=device-width, initial-scale=1'>"+
			"<title>国际象棋后台管理</title>"+
			"<link rel=\"stylesheet\" href=\"http://main.miracle-cn.com/jquery.mobile-1.3.2/jquery.mobile-1.3.2.min.css\" />"+
	        "<script src=\"http://main.miracle-cn.com/jquery/jquery-1.11.1.min.js\"></script>"+
	        "<script src=\"http://main.miracle-cn.com/jquery.mobile-1.3.2/jquery.mobile-1.3.2.min.js\"></script>"+
		"</head>"+
		"<body>" +"<div data-role=\"controlgroup\" data-type=\"horizontal\" data-mini=\"true\">";
	public static String getHtml(String body){
			return	head+
			"<a href=\"/rate\" data-role=\"button\" data-icon=\"\" data-theme=\"b\""
			+ " rel=\"external\"  data-ajax=\"false\">支付统计</a>"+
			"<a href=\"/record\" data-role=\"button\" data-icon=\"\" data-theme=\"b\""
			+ " rel=\"external\"  data-ajax=\"false\">行为记录</a>"+
				"</div>"+
				body+
			"</body >" +
		"</html>";
	}
	public static String getManageHtml(String body) {
		return head+
				"<a href=\"/manage_device\" data-role=\"button\" data-icon=\"\""
				+ " data-theme=\"b\" rel=\"external\"  data-ajax=\"false\">device</a>"+
				"<a href=\"/basedata\" data-role=\"button\" data-icon=\"\""
				+ " data-theme=\"b\" rel=\"external\"  data-ajax=\"false\">basedata</a>"+
				"<a href=\"/channels\" data-role=\"button\" data-icon=\"\""
				+ " data-theme=\"b\" rel=\"external\"  data-ajax=\"false\">channels</a>"+
				"</div>"+
				body+
			"</body >" +
		"</html>";
	}
	
	    
}
