package http;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Logger;

import cmd.CMD13;
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
					"<td>"+ce.getOppoPay()+"</td>" +
					"<td>"+ce.getWiiPay()+"</td>" +
					"</tr>");
		}
		
		String html = "";
		if(!content.isEmpty()){
			html = 每日渠道.toString();
		}else{
			String body ="<style type=\"text/css\">table,th,td,tr{	border:1px solid #888888;}</style>"+//表格边框
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
										"<th data-priority=\"6\">oppo<br>支付</th>" +
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
			StringBuffer 每日记录=new StringBuffer(),
					红包=new StringBuffer(),
					ab测=new StringBuffer();
			String typeA=switchType(CMD13.getRewardSwitch(1).get("type").asString());
			String typeB=switchType(CMD13.getRewardSwitch(0).get("type").asString());
			String buyTypeA=switchBuyType(CMD13.getRewardSwitch(1).get("buyType").asString());
			String buyTypeB=switchBuyType(CMD13.getRewardSwitch(0).get("buyType").asString());
			String abTest="A-奇数：生成="+typeA+",使用="+buyTypeA+";B-偶数：生成="+typeB+",使用="+buyTypeB;
			
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
						"<td>"+count.getOppoPay()+"</td>" +
						"<td>"+count.getWiiPay()+"</td>" +
						"</tr>");
				if(!Global.isEmpty(count.getReward())) {
					Data creat=count.getRewardData().get("红包生成");
					Data used=count.getRewardData().get("红包使用");
					红包.append("<tr>" +
							"<td>"+日期+"</td>" +
							"<td>"+count.getRewardData().get("新增用户").asInt()+"</td>" +
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
				
				if(!Global.isEmpty(count.getAbPay())) {
					Data abData=Data.fromMap(count.getAbPay());
					ab测.append("<tr>" +
							"<td>"+日期+"</td>" +
							"<td>"+count.getTotalPay()+"</td>" +
							"<td>"+(abData.get(0).get("金额").asInt()+abData.get(1).get("金额").asInt())+"</td>" +
							"<td>"+(abData.get(0).get("次数").asInt()+abData.get(1).get("次数").asInt())+"</td>" +
							"<td>"+abData.get(1).get("单课").get("次数").asInt()+"</td>" +
							"<td>"+abData.get(1).get("单课").get("金额").asInt()+"</td>" +
							"<td>"+abData.get(1).get("多课").get("次数").asInt()+"</td>" +
							"<td>"+abData.get(1).get("多课").get("金额").asInt()+"</td>" +
							"<td>"+abData.get(0).get("单课").get("次数").asInt()+"</td>" +
							"<td>"+abData.get(0).get("单课").get("金额").asInt()+"</td>" +
							"<td>"+abData.get(0).get("多课").get("次数").asInt()+"</td>" +
							"<td>"+abData.get(0).get("多课").get("金额").asInt()+"</td>" +
							"</tr>"
							);
				}
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
									"<th data-priority=\"6\">oppo<br>支付</th>" +
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
									"<th rowspan=\"2\" >可获取红包<br>新增用户</th>\n" + 
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
			body+="<div align=\"center\" data-role=\"collapsible\">\n"+
					"<h3 align=\"center\">"+abTest+"</h3>\n" +
					"<div>\n" +
						"<table data-role=\"table\" id='ABtest' data-mode=\"columntoggle\" class=\"ui-responsive table-stroke\" border='1' >\n" +
							"<thead>\n" +
								"<tr>\n" +
									"<th rowspan=\"2\" ><br>时间</th>\n" + 
									"<th rowspan=\"2\" >所有版本<br>总支付额</th>\n" + 
									"<th colspan=\"2\" >观察版本总支付</th>\n" + 
									"<th colspan=\"2\" data-priority=\"1\" style=\"text-align:center;\" >A(奇数用户单课支付)</th>\n" +
									"<th colspan=\"2\" data-priority=\"1\" style=\"text-align:center;\" >A(奇数用户多课支付)</th>\n" +
									"<th colspan=\"2\" data-priority=\"1\" style=\"text-align:center;\" >B(偶数用户单课支付)</th>\n" +
									"<th colspan=\"2\" data-priority=\"1\" style=\"text-align:center;\" >B(偶数用户多课支付)</th>\n" +
								"</tr>\n"+
								"<tr>\n" +
									"<th>金额</th>\n" +//观察版本
									"<th>次数</th>\n" +//观察版本
									"<th>次数</th>\n" +//A(奇数用户单课支付)
									"<th>金额</th>\n" +//A(奇数用户单课支付)
									"<th>次数</th>\n" +//A(奇数用户多课支付)
									"<th>金额</th>\n" +//A(奇数用户多课支付)
									"<th>次数</th>\n" +//B(偶数用户单课支付)
									"<th>金额</th>\n" +//B(偶数用户单课支付)
									"<th>次数</th>\n" +//B(偶数用户多课支付)
									"<th>金额</th>\n" +//B(偶数用户多课支付)
								"</tr>\n"+
							"</thead>\n" +
							"<tbody>\n" + 
								ab测.toString() + 
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
	private String switchType(String type) {
		String str="";
		if("0".equals(type)) {
			str="通用";
		}else if("1".equals(type)) {
			str="仅第1课";
		}else if("2".equals(type)) {
			str="仅第2课";
		}else if("3".equals(type)) {
			str="关闭";
		}else {
			str="值错误";
		}
		return str;
	}
	private String switchBuyType(String buyType){
		String str="";
		if("0".equals(buyType)) {
			str="通用";
		}else if("1".equals(buyType)) {
			str="多课";
		}else {
			str="值错误";
		}
		return str;
	}
}
