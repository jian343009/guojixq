package http;

import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.List;
import java.util.logging.*;

import main.Global;
import dao.*;
import data.*;

public class Html_basedata extends Html {
	private static final Logger log = Logger.getLogger(Html_basedata.class.getName());

	@Override
	public String getHtml(String content) {
		log.info(content);
		
		String html = "";
		
		if(content.isEmpty()){
			//String[] params = "id,name,content".split(","); 
			String body = 
			"<script type=\"text/javascript\">"+
				"function updaterule(column, id){" +
					"var value = $('#'+column+id).val();" +
					"value = encodeURIComponent(value);" +
					"$.post('/basedata','id='+id+'&'+column+'='+value,function(data,status){alert(data)});"+
				"}" +
				"function add(){" +
					"var name = $('#addname').val();" +
					"$.post('/basedata', 'id=0&name='+name, function(data){alert(data);});" +
				"}"+
			"</script>"+
			"<div>" +
				"        <table>\n" + 
				"            <thead>\n" + 
				"                <tr>\n" + 
				"                    <th>baseDataName</th>\n" + 
				"                    <th><input type='text' id='addname' /></th>\n" + 
				"                    <th style=\"width:31px;\"><button onclick='add();'>新增</button></th>\n" + 
				"                </tr>\n" + 
				"            </thead>  \n" + 
				"        </table>" +
				"<table data-role=\"table\" id=\"table-column-toggle\" data-mode=\"column\" class=\"ui-responsive table-stroke\" border=\"1\">"+
				     "<thead>"+
				     	"<tr>" 
					+ "<th style=\"width:17px;\">ID</th>"
					+ "<th style=\"width:99px;\">name</th>"
					+ "<th>content</th>"
				       +"</tr>"+
				     "</thead>"+
				     "<tbody>";
			List<BaseData> list = BaseData.getAllBaseData();
			Collections.reverse(list);
			for(int m=0;m<list.size();m++){
				BaseData bd = list.get(m);
				if("红包限制".equals(bd.getName())) {
					String s2="",s3="";
					if("多课".equals(bd.getContent())) {		s2 = "selected";
					}else if("通用".equals(bd.getContent())) {	s3 = "selected";	}
					body +="<tr>\n" 
							+ "<td>"+bd.getId()+"</td>\n" 
							+ "<td>"+bd.getName()+"</td>\n"
							+ "<td><div style=\"width:130px;\"><select id=\"content"+bd.getId()+"\""
									+ " onchange=\"updaterule('content','"+bd.getId()+"');\">\n" + 
							"					<option value=\"关闭\">关闭</option>\n" + 
							"					<option value=\"多课\" "+s2+">多课</option>\n" + 
							"					<option value=\"通用\" "+s3+">通用</option>\n" + 
							"			</select></div></td></tr>" ;
					continue;
				}
				String idStr="content"+bd.getId();
				body +=
				"<tr>" +
				"<td>"+bd.getId()+"</td>" +
				"<td>"+bd.getName()+"</td>" +
				"<td><textarea id=\""+idStr+"\" onchange=\"$('#btn"+idStr+"').show();\" >"
				+bd.getContent()
				+"</textarea><a href=\"#\"  id=\"btn"+idStr+"\" onclick=\"updaterule('content"
				+"', '"+bd.getId()+"');$(this).hide();\" data-role=\"button\""
				+ " data-icon=\"check\" data-iconpos=\"notext\" data-theme=\"c\""
				+ " data-inline=\"true\" style=\"display:none;\"></a></td>" ;
//					for(int n=2;n<params.length;n++){
//						try {
//							Field fd = BaseData.class.getDeclaredField(params[n]);
//							fd.setAccessible(true);
//							String idStr = params[n]+bd.getId();
//							body += "<td><textarea id=\""+idStr+"\" onchange=\"$('#btn"+idStr+"').show();\" >"
//							+fd.get(bd)
//							+"</textarea><a href=\"#\" id=\"btn"+idStr+"\" onclick=\"updaterule('"
//									+params[n]+"', '"+bd.getId()+"');$(this).hide();\" data-role=\"button\""
//								+ " data-icon=\"check\" data-iconpos=\"notext\" data-theme=\"c\""
//								+ " data-inline=\"true\" style=\"display:none;\"></a></td>" ;
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//					}
				body +=
				"</tr>";
			}
			body += 
					"</tbody>" +
				"</table>" +
			"</div>";
			html = Http.getManageHtml(body);
		}else{
			String[] conts = content.split("&");
			int id = Global.getInt(conts[0].split("=")[1]);
			
			BaseData br = BaseData.getByID(id);
			if(id ==0 && conts.length >1 && conts[1].startsWith("name=")){
				String name = conts[1].replace("name=", "");
				BaseData bd = BaseData.getByName(name);
				if(bd == null){
					bd = new BaseData();
					bd.setName(name);
					Dao.save(bd);
					html = name+" 新增成功，请刷新页面查看。";
				}else{
					html = name+" 已经存在。";
				}
			}else if(br != null){
				for(int m=1;m<conts.length;m++){
					String[] kv = conts[m].split("=");
					String key = kv[0];
					String value = "";
					if(kv.length ==2){
						value = kv[1];
					}
					try {
						Field fd = BaseData.class.getDeclaredField(key);
						fd.setAccessible(true);
						if(fd.getType() == int.class){
							fd.set(br, Global.getInt(value));
						}else{
							fd.set(br, URLDecoder.decode(value, "utf-8"));
						}
						html += key+"修改"+fd.get(br)+";";
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				Dao.save(br);
			}else{
				html = "记录不存在";
			}
		}
		return html;
	}

}
