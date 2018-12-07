package http;

import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.util.Arrays;
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
			"<style>"
			+ ""
			+ ""
			+ ""
			+ "</style>"+ 
			"<script type=\"text/javascript\">"
			+
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
				"<table data-role=\"table\" id=\"table-column-toggle\" data-mode=\"column\""//表格固定宽度
				+ " class=\"ui-responsive table-stroke\" border=\"1\" style=\"table-layout:fixed;\">"+
				     "<thead>"+
				     	"<tr>" 
					+ "<th style=\"width:17px;\">ID</th>"
					+ "<th style=\"width:99px;\">name</th>"
					+ "<th>content</th>"
				       +"</tr>"+
				     "</thead>\n"+
				     "<tbody>";
			List<BaseData> list = BaseData.getAllBaseData();
			Collections.reverse(list);
			for(int m=0;m<list.size();m++){
				BaseData bd = list.get(m);
				String idStr="content"+bd.getId();
				List<String> without=Arrays.asList(new String[]{"华为平台","苹果平台","乐视电视","其它平台","oppo联运"});
				if(without.contains(bd.getName())) {
					continue;//渠道价格信息排除
				}
				String rows="";
				if(Global.isEmpty(bd.getContent()) && bd.getContent().length()>=99) {
					rows="style=\"height:30px;\"";
				}
				body +=
				"<tr>" +
				"<td>"+bd.getId()+"</td>\n" +
				"<td>"+bd.getName()+"</td>\n" +
				"<td>"
				+ "<div"+rows+">"
				+ "<textarea "+rows+" id=\""+idStr+"\" onchange=\"$('#btn"+idStr+"').show();\" >\n"
				+bd.getContent()
				+"</textarea><a href=\"#\"  id=\"btn"+idStr+"\" onclick=\"updaterule('content"
				+"', '"+bd.getId()+"');$(this).hide();\" data-role=\"button\""
				+ " data-icon=\"check\" data-iconpos=\"notext\" data-theme=\"c\""
				+ " data-inline=\"true\" style=\"display:none;\"></a>"
				+ "</div>"
				+ "</td>\n" ;
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
				if(Global.isEmpty(name)) {
					return "name不能为空";
				}
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
