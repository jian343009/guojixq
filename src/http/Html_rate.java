package http;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Logger;

import net.sf.json.JSONArray;

import dao.Dao;
import dao.Data;
import data.*;

import main.Global;
import main.ServerTimer;


public class Html_rate implements IHtml {
	private static final Logger log = Logger.getLogger(Html_rate.class.getName());

	@Override
	public String getHtml(String content) {
		
		StringBuffer 每日渠道=new StringBuffer();
		List<ChannelEveryday> list = Dao.getChannelEverydayByChannel(content);
		for(int m=0;m<list.size();m++){
			ChannelEveryday ce = list.get(m);
			每日渠道.append("<tr>" +
					"<td>"+ce.getChannel()+"</td>" +
					"<td>"+simpleDate(ce.getDayStr())+"</td>" +
					"<td>"+ce.getOpen()+"</td>" +
					"<td>"+ce.getNewDevice()+"</td>" +
					"<td>"+ce.getReturnNum(1)+"("+(ce.getYesterday()==0 ? 0 : ce.getReturnNum(1)*100/ce.getYesterday())+"%)</td>" +
					"<td>"+ce.getReturnNum(2)+"</td>" +
					"<td>"+ce.getReturnNum(7)+"</td>" +
					"<td>"+ce.getPay()+"</td>"+
					"<td>"+ce.getNewPay()+"</td>" +
					"<td>"+ce.getTotalPay()+"</td>" +
					"<td>"+ce.getAliPay()+"</td>" +
					"<td>"+ce.getWxPay()+"</td>" +
					"<td>"+ce.getApplePay()+"</td>" +
					"<td>"+ce.getHwPay()+"</td>" +
					"<td>"+ce.getWiiPay()+"</td>" +
					"</tr>");
		}
		
		String html = "";
		if(!content.isEmpty()){
			html = 每日渠道.toString();
		}else{
			String body =
					"<script>" +
						"function update(){" +
							"var name = $('#channelName').val();" +
							"if(name){" +
								"$.post('/rate', name, function(data){$('#detail').html(data);});" +
							"}" +
						"}" +
					"</script>"+
					"<div align=\"center\" data-role=\"collapsible\">"+
						"<h3 align=\"center\">渠道统计</h3>" +
						"<div>" +
							"渠道<input type='text' id='channelName' onchange='update();' />"+
							"<table data-role=\"table\" id='t1' data-mode=\"columntoggle\" class=\"ui-responsive table-stroke\" border='1' >" +
								"<thead>" +
									"<tr>" +
										"<th><br>渠道</th>" +
										"<th data-priority=\"2\">记录<br>时间</th>" +
										"<th data-priority=\"1\">打开<br>设备</th>" +
										"<th data-priority=\"4\">新增<br>设备</th>" +
										"<th data-priority=\"4\">次日<br>返回</th>" +
										"<th data-priority=\"4\">2~6<br>返回</th>" +
										"<th data-priority=\"4\">7~<br>返回</th>" +
										"<th data-priority=\"3\">支付<br>次数</th>" +
										"<th data-priority=\"3\">新增<br>支付</th>" +
										"<th>支付<br>金额</th>" +
										"<th data-priority=\"5\">支付<br>宝</th>" +
										"<th data-priority=\"5\">微信<br>支付</th>" +
										"<th data-priority=\"5\">苹果<br>支付</th>" +
										"<th data-priority=\"6\">华为<br>支付</th>" +
										"<th data-priority=\"6\">其他<br>支付</th>" +
									"</tr>"+
								"</thead>" +
								"<tbody id='detail'>" + 
									每日渠道.toString() + 
								"</tbody>" +
							"</table>" +
						"</div>" +
					"</div>";
			
			//String tr2 = "";
			StringBuffer 每日记录=new StringBuffer(),红包=new StringBuffer();
			List<Count> list2 = Dao.getAllDayCount();
			for(int m=0;m<list2.size()-1;m++){
				Count count = list2.get(m);
				Count next = list2.get(m+1);				
				
				String[] weeks = {"没有","周日","周一","周二","周三","周四","周五","周六"};
				String week =weeks[ServerTimer.getCalendarFromString(
				        	count.getDayStr()).get(Calendar.DAY_OF_WEEK)];
				String 日期=simpleDate(count.getDayStr())+","+week;
				每日记录.append( "<tr>" +
						"<td>"+日期+"</td>" +
						"<td>"+count.getOpen()+"</td>" +
						"<td>"+count.getNewDevice()+"</td>" +
						"<td>"+count.getReturnNum(1)+"("+(next.getNewDevice() == 0 ? 0 : count.getReturnNum(1)*100/next.getNewDevice())+"%)</td>" +
						"<td>"+count.getReturnNum(2)+"</td>" +
						"<td>"+count.getReturnNum(7)+"</td>" +
						"<td>"+count.getPay()+"</td>"+
						"<td>"+count.getNewPay()+"</td>" +
						"<td>"+count.getTotalPay()+"</td>" +
						"<td>"+count.getAliPay()+"</td>" +
						"<td>"+count.getWxPay()+"</td>" +
						"<td>"+count.getApplePay()+"</td>" +
						"<td>"+count.getHwPay()+"</td>" +
						"<td>"+count.getWiiPay()+"</td>" +
						"</tr>");
				Data creat=count.getRewardData().get("红包生成");
				Data used=count.getRewardData().get("红包使用");
				红包.append("<tr>" +
						"<td>"+日期+"</td>" +
						"<td>"+creat.get(1).get("次数").asInt()+"</td>" +
						"<td>"+creat.get(1).get("金额").asInt()+"</td>" +
						"<td>"+creat.get(2).get("次数").asInt()+"</td>" +
						"<td>"+creat.get(2).get("金额").asInt()+"</td>" +
						"<td>"+used.get("总次数").asInt()+"</td>" +
						"<td>"+used.get("总金额").asInt()+"</td>" +
						"<td>"+used.get("单课次数").asInt()+"</td>" +
						"<td>"+used.get("单课金额").asInt()+"</td>" +
						"<td>"+used.get("多课次数").asInt()+"</td>" +
						"<td>"+used.get("多课金额").asInt()+"</td>" +
						"</tr>"
						);
			}
			
			body +=
				"<div align=\"center\" data-role=\"collapsible\">"+
					"<h3 align=\"center\">每天统计</h3>" +
					"<div>" +
						"<table data-role=\"table\" id='t2' data-mode=\"columntoggle\" class=\"ui-responsive table-stroke\" border='1' >" +
							"<thead>" +
								"<tr>" +
									"<th>记录<br>时间</th>" +
									"<th data-priority=\"1\">打开<br>设备</th>" +
									"<th data-priority=\"1\">新增<br>设备</th>" +
									"<th data-priority=\"3\">次日<br>返回</th>" +
									"<th data-priority=\"4\">2~6<br>返回</th>" +
									"<th data-priority=\"4\">7~<br>返回</th>" +
									"<th data-priority=\"3\">支付<br>次数</th>" +
									"<th data-priority=\"3\">新增<br>支付</th>" +
									"<th>支付<br>金额</th>" +
									"<th data-priority=\"5\"><br>支付宝</th>" +
									"<th data-priority=\"5\">微信<br>支付</th>" +
									"<th data-priority=\"5\">苹果<br>支付</th>" +
									"<th data-priority=\"6\">华为<br>支付</th>" +
									"<th data-priority=\"6\">其他<br>支付</th>" +
								"</tr>"+
							"</thead>" +
							"<tbody>" + 
								每日记录.toString() + 
							"</tbody>" +
						"</table>" +
					"</div>" +
				"</div>";
			
			StringBuffer 每月记录=new StringBuffer();
			list2 = Dao.getAllMonthCount();
			for(int m=0;m<list2.size();m++){
				Count count = list2.get(m);
				每月记录.append("<tr>" +
						"<td>"+count.getDayStr()+"</td>" +
						"<td>"+count.getOpen()+"</td>" +
						"<td>"+count.getNewDevice()+"</td>" +
						"<td>"+count.getPay()+"</td>"+
						"<td>"+count.getNewPay()+"</td>" +
						"<td>"+count.getTotalPay()+"</td>" +
						"<td>"+count.getAliPay()+"</td>" +
						"<td>"+count.getWxPay()+"</td>" +
						"<td>"+count.getApplePay()+"</td>" +
						"<td>"+count.getHwPay()+"</td>" +
						"<td>"+count.getWiiPay()+"</td>" +
						"</tr>");
			}
			
			body +=
				"<div align=\"center\" data-role=\"collapsible\">"+
					"<h3 align=\"center\">每月统计</h3>" +
					"<div>" +
						"<table data-role=\"table\" id='t2' data-mode=\"columntoggle\" class=\"ui-responsive table-stroke\" border='1' >" +
							"<thead>" +
								"<tr>" +
									"<th><br>时间</th>" +
									"<th data-priority=\"1\">打开<br>设备</th>" +
									"<th data-priority=\"2\">新增<br>设备</th>" +
									"<th data-priority=\"2\">支付<br>次数</th>" +
									"<th data-priority=\"3\">新增<br>支付</th>" +
									"<th>支付<br>金额</th>" +
									"<th data-priority=\"5\"><br>支付宝</th>" +
									"<th data-priority=\"5\">微信<br>支付</th>" +
									"<th data-priority=\"5\">苹果<br>支付</th>" +
									"<th data-priority=\"5\">华为<br>支付</th>" +
									"<th data-priority=\"6\">其他<br>支付</th>" +
								"</tr>"+
							"</thead>" +
							"<tbody>" + 
								每月记录.toString() + 
							"</tbody>" +
						"</table>" +
					"</div>" +
				"</div>";
			body+="<div align=\"center\" data-role=\"collapsible\">\n"+
					"<h3 align=\"center\">红包统计</h3>\n" +
					"<div>\n" +
						"<table data-role=\"table\" id='reward' data-mode=\"columntoggle\" class=\"ui-responsive table-stroke\" border='1' >\n" +
							"<thead>\n" +
								"<tr>\n" +
									"<th rowspan=\"2\" ><br>时间</th>\n" + 
									"<th colspan=\"2\" data-priority=\"1\" style=\"text-align:center;\" >第一课</th>\n" +
									"<th colspan=\"2\" data-priority=\"1\" style=\"text-align:center;\" >第二课</th>\n" +
									"<th colspan=\"2\" data-priority=\"1\" style=\"text-align:center;\" >红包使用</th>\n" +
									"<th colspan=\"2\" data-priority=\"1\" style=\"text-align:center;\" >单课使用</th>\n" +
									"<th colspan=\"2\" data-priority=\"1\" style=\"text-align:center;\" >多课使用</th>\n" +
								"</tr>\n"+
								"<tr>\n" +
									"<th>次数</th>\n" +
									"<th>金额</th>\n" +
									"<th>次数</th>\n" +
									"<th>金额</th>\n" +
									"<th>总次数</th>\n" +//总计
									"<th>总金额</th>\n" +
									"<th>单课次数</th>\n" +//单课
									"<th>单课金额</th>\n" +
									"<th>多课次数</th>\n" +//多课
									"<th>多课金额</th>\n" +
								"</tr>\n"+
							"</thead>\n" +
							"<tbody>\n" + 
								红包.toString() + 
							"</tbody>\n" +
						"</table>" +
					"</div>\n" +
				"</div>";
			html = Http.getHtml(body);
		}

		return html;
	}
	private String simpleDate(String dayStr) {
		if(!Global.isEmpty(dayStr)&&dayStr.length()>6) {
			return dayStr.substring(5);
		}
		return "";
	}
}
