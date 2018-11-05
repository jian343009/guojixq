package http;

import java.util.Hashtable;
import java.util.List;
import java.util.logging.Logger;

import net.sf.json.JSONArray;

import dao.Dao;
import dao.Data;
import data.*;

import main.Global;


public class Html_rate implements IHtml {
	private static final Logger log = Logger.getLogger(Html_rate.class.getName());

	@Override
	public String getHtml(String content) {
		
		String tr="";
		List<ChannelEveryday> list = Dao.getChannelEverydayByChannel(content);
		for(int m=0;m<list.size();m++){
			ChannelEveryday ce = list.get(m);
			tr += "<tr>" +
					"<td>"+ce.getChannel()+"</td>" +
					"<td>"+ce.getDayStr()+"</td>" +
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
					"</tr>";
		}
		
		String html = "";
		if(!content.isEmpty()){
			html = tr;
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
										"<th>渠道</th>" +
										"<th data-priority=\"2\">时间</th>" +
										"<th data-priority=\"3\">打开设备</th>" +
										"<th data-priority=\"4\">新增设备</th>" +
										"<th data-priority=\"4\">次日返回</th>" +
										"<th data-priority=\"4\">2~6返回</th>" +
										"<th data-priority=\"4\">7~返回</th>" +
										"<th data-priority=\"5\">支付次数</th>" +
										"<th data-priority=\"6\">新增支付次数</th>" +
										"<th>支付金额</th>" +
										"<th data-priority=\"8\">支付宝</th>" +
										"<th data-priority=\"9\">微信支付</th>" +
										"<th data-priority=\"9\">苹果支付</th>" +
										"<th data-priority=\"9\">华为支付</th>" +
										"<th data-priority=\"9\">其他支付</th>" +
									"</tr>"+
								"</thead>" +
								"<tbody id='detail'>" + 
									tr + 
								"</tbody>" +
							"</table>" +
						"</div>" +
					"</div>";
			
			String tr2 = "";
			List<Count> list2 = Dao.getAllDayCount();
			for(int m=0;m<list2.size()-1;m++){
				Count count = list2.get(m);
				Count next = list2.get(m+1);				
				int Open = 0;int[] daysOpen = {1,2,4,8,15};
				for(int i:daysOpen){				
					Open += count.getData().get("返回").get(i).get("共计").asInt()
						-count.getData().get("返回").get(i).get("详细").get("其他版本").asInt();
				}
				int NewDevice =  count.getData().get("返回").get(0).get("共计").asInt()
						-count.getData().get("返回").get(0).get("详细").get("其他版本").asInt();
				Data data = count.getData().get("支付");
				tr2 += "<tr>" +
						"<td>"+count.getDayStr()+"</td>" +
						"<td>"+count.getOpen()+"<br>7版本："+Open+"</td>" +
						"<td>"+count.getNewDevice()+"<br>7版本："+NewDevice+"</td>" +
						"<td>"+count.getReturnNum(1)+"("+(next.getNewDevice() == 0 ? 0 : count.getReturnNum(1)*100/next.getNewDevice())+"%)</td>" +
						"<td>"+count.getReturnNum(2)+"</td>" +
						"<td>"+count.getReturnNum(7)+"</td>" +
						"<td>"+count.getPay()+show_奇偶(data.get("总计次数"))+"</td>"+
						"<td>"+count.getNewPay()+"</td>" +
						"<td>"+count.getTotalPay()+show_奇偶(data.get("总计金额"))+"</td>" +
						"<td>"+count.getAliPay()+show_奇偶(data.get("详细金额").get("支付宝"))+"</td>" +
						"<td>"+count.getWxPay()+show_奇偶(data.get("详细金额").get("微信支付"))+"</td>" +
						"<td>"+count.getApplePay()+show_奇偶(data.get("详细金额").get("苹果支付"))+"</td>" +
						"<td>"+count.getHwPay()+show_奇偶(data.get("详细金额").get("华为支付"))+"</td>" +
						"<td>"+count.getWiiPay()+show_奇偶(data.get("详细金额").get("其它支付"))+"</td>" +
						"</tr>";
			}
			
			body +=
				"<div align=\"center\" data-role=\"collapsible\">"+
					"<h3 align=\"center\">每天统计</h3>" +
					"<div>" +
						"<table data-role=\"table\" id='t2' data-mode=\"columntoggle\" class=\"ui-responsive table-stroke\" border='1' >" +
							"<thead>" +
								"<tr>" +
									"<th>时间</th>" +
									"<th data-priority=\"3\">打开设备</th>" +
									"<th data-priority=\"4\">新增设备</th>" +
									"<th data-priority=\"4\">次日返回</th>" +
									"<th data-priority=\"4\">2~6返回</th>" +
									"<th data-priority=\"4\">7~返回</th>" +
									"<th data-priority=\"5\">支付次数</th>" +
									"<th data-priority=\"6\">新增支付次数</th>" +
									"<th>支付金额</th>" +
									"<th data-priority=\"8\">支付宝</th>" +
									"<th data-priority=\"9\">微信支付</th>" +
									"<th data-priority=\"9\">苹果支付</th>" +
									"<th data-priority=\"9\">华为支付</th>" +
									"<th data-priority=\"9\">其他支付</th>" +
								"</tr>"+
							"</thead>" +
							"<tbody>" + 
								tr2 + 
							"</tbody>" +
						"</table>" +
					"</div>" +
				"</div>";
			
			tr2 = "";
			list2 = Dao.getAllMonthCount();
			for(int m=0;m<list2.size();m++){
				Count count = list2.get(m);
				tr2 += "<tr>" +
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
						"</tr>";
			}
			
			body +=
				"<div align=\"center\" data-role=\"collapsible\">"+
					"<h3 align=\"center\">每月统计</h3>" +
					"<div>" +
						"<table data-role=\"table\" id='t2' data-mode=\"columntoggle\" class=\"ui-responsive table-stroke\" border='1' >" +
							"<thead>" +
								"<tr>" +
									"<th>时间</th>" +
									"<th data-priority=\"3\">打开设备</th>" +
									"<th data-priority=\"4\">新增设备</th>" +
									"<th data-priority=\"5\">支付次数</th>" +
									"<th data-priority=\"6\">新增支付次数</th>" +
									"<th>支付金额</th>" +
									"<th data-priority=\"8\">支付宝</th>" +
									"<th data-priority=\"9\">微信支付</th>" +
									"<th data-priority=\"9\">苹果支付</th>" +
									"<th data-priority=\"9\">华为支付</th>" +
									"<th data-priority=\"9\">其他支付</th>" +
								"</tr>"+
							"</thead>" +
							"<tbody>" + 
								tr2 + 
							"</tbody>" +
						"</table>" +
					"</div>" +
				"</div>";
			html = Http.getHtml(body);
		}

		return html;
	}
	
	private String show_奇偶(Data data){
		StringBuilder sb =new StringBuilder();
		sb.append("<br>奇：").append(data.get("1").asString())
		.append("<br>偶：").append(data.get("0").asString());
		return sb.toString();
	}

}
