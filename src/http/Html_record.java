package http;

import java.util.List;
import java.util.logging.Logger;

import dao.Dao;
import data.Record;


public class Html_record implements IHtml {
	private static final Logger log = Logger.getLogger("Html_allstep");

	@Override
	public String getHtml(String content) {
		log.info("行为记录");
		StringBuffer 行为记录=new StringBuffer();
		List<Record> list = Dao.getRecentRecord(content);
		log.info(list.size()+"");
		for(Record re : list){
			行为记录.append("<tr>" +
					"<td>"+re.getDeviceID()+"</td>" +
					"<td>"+re.getImei()+"</td>" +
					"<td>"+re.getStep()+"</td>" +
					"<td>"+re.getInfo()+"</td>" +
					"<td>"+re.getTimeStr()+"</td>"+
				"</tr>\n");
		}
//		List<StepCount> step_list = Dao.getAllDayStepCount();
//		StringBuilder sb = new StringBuilder();
		String html = "";
		if(!content.isEmpty()){
			html = 行为记录.toString();
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
				              "<table data-role=\"table\" id='t1' data-mode=\"column\" class=\"ui-responsive table-stroke\" border='1' >" +
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
					              		行为记录.toString() + 
								  "</tbody>" +
							  "</table>" +
						  "</div>" +
					  "</div>";
			html = Http.getHtml(body);
		}
		return html;
	}

}
