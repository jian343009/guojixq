package http;

import java.util.Hashtable;
import java.util.List;
import java.util.logging.Logger;

import net.sf.json.JSONArray;

import dao.Dao;
import dao.Data;
import data.*;

import main.Global;


public class Html_record implements IHtml {
	private static final Logger log = Logger.getLogger("Html_allstep");

	@Override
	public String getHtml(String content) {
		
		String tr="";
		List<Record> list = Dao.getRecentRecord(content);
		for(Record re : list){
			tr += "<tr>" +
					"<td>"+re.getDeviceID()+"</td>" +
					"<td>"+re.getImei()+"</td>" +
					"<td>"+re.getStep()+"</td>" +
					"<td>"+re.getInfo()+"</td>" +
					"<td>"+re.getTimeStr()+"</td>"+
					"</tr>";
		}
		List<StepCount> step_list = Dao.getAllDayStepCount();
		StringBuilder sb = new StringBuilder();
		for(StepCount sc:step_list){
			Data data =sc.getData().get("退出原因");			
			sb.append("<tr>")
			.append("<td>").append(sc.getDayStr()).append(",").append(sc.getChannel()).append("</td>")
			.append("<td>").append(data.get("不吸引人").asInt()).append("</td>")
			.append("<td>").append(data.get("想学但价格太高").asInt()).append("</td>")
			.append("<td>").append(data.get("习题太少缺少练习").asInt()).append("</td>")
			.append("<td>").append(data.get("孩子看不懂").asInt()).append("</td>")
			.append("<td>").append(data.get("操作不方便").asInt()).append("</td>")
			.append("</tr>");
		}
		
		String html = "";
		if(!content.isEmpty()){
			html = tr;
		}else{
			String body =
					"<script>" +
						"function update(){" +
							"$.post('/record', $('#imei').val(), function(data){$('#detail').html(data);});" +
						"}" +
					"</script>"+
					  "<div align=\"center\" data-role=\"collapsible\">"+
			              "<h3 align=\"center\">行为记录</h3>" +
			              "<div>" +
			              //	"设备<input type='text' id='imei' onchange='update();' />"+
				              "<table data-role=\"table\" id='t1' data-mode=\"columntoggle\" class=\"ui-responsive table-stroke\" border='1' >" +
					              "<thead>" +
						              "<tr>" +
						              	  "<th data-priority=\"1\">设备ID</th>" +
							              "<th data-priority=\"1\">imei</th>" +
							              "<th data-priority=\"2\">行为</th>" +
							              "<th data-priority=\"3\">信息</th>" +
							              "<th data-priority=\"4\">时间</th>" +
						              "</tr>"+
					              "</thead>" +
					              "<tbody id='detail'>" + 
					              		tr + 
								  "</tbody>" +
							  "</table>" +
						  "</div>" +
					  "</div>";
				body +=  "<div align=\"center\" data-role=\"collapsible\">"+
			              "<h3 align=\"center\">退出原因</h3>" +
			              "<div>" +
				              "<table data-role=\"table\" id='t1' data-mode=\"columntoggle\" class=\"ui-responsive table-stroke\" border='1' >" +
					              "<thead>" +
						              "<tr>" +
						              	  "<th data-priority=\"1\">日期和渠道</th>" +
							              "<th data-priority=\"1\">不吸引人</th>" +
							              "<th data-priority=\"2\">想学但价格太高</th>" +
							              "<th data-priority=\"3\">习题太少缺少练习</th>" +
							              "<th data-priority=\"4\">孩子看不懂</th>" +
							              "<th data-priority=\"4\">操作不方便</th>" +
						              "</tr>"+
					              "</thead>" +
					              "<tbody id='detail'>" + 
					              		sb.toString() + 
								  "</tbody>" +
							  "</table>" +
						  "</div>" +
					  "</div>";
			html = Http.getHtml(body);
		}

		return html;
	}

}
