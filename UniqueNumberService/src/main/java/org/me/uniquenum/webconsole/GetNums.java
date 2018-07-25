package org.me.uniquenum.webconsole;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.me.uniquenum.util.ResponseBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

@RestController
@Component
public class GetNums {
	
	@Autowired
	private DataSource source;
	
	@Value("${scretKey}")
	private String scretKey;
	
    @RequestMapping(value = "/getNum", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String tmp=null,all="";
		BufferedReader br=new BufferedReader(new InputStreamReader(request.getInputStream(),Charset.forName("utf-8")));
		while((tmp=br.readLine())!=null){
			all+=tmp;
		}
		ResponseBean res=new ResponseBean();
		response.setContentType("application/json");
		JSONObject jo=JSON.parseObject(all);

		if(all==null||jo==null) {
			res.setCode(400);
			res.setMsg("scretKey is not right!");
		}else {
			if(!scretKey.equals(jo.getString("scretKey"))){
				res.setCode(400);
				res.setMsg("scretKey is not right!");
			}else{
				int num=jo.getInteger("num");
				
				try {
					Connection con=source.getConnection();
					try {
						Statement st=con.createStatement();
						java.sql.PreparedStatement pst=con.prepareStatement("update "
								+ "number set num=num+? where num=?");
						while(true) {
							ResultSet rs=st.executeQuery("select * from number where id=1");
							rs.next();
							long currentnum=rs.getLong("num");
							long limitNum=rs.getLong("limitNum");
							if(currentnum+num>=limitNum) {
								//exceed limit
								res.setCode(500);
								res.setSuccess(false);
								res.setMsg("Num have exceed limit");
								break;
							}
							rs.close();
							pst.setInt(1, num);
							pst.setLong(2, currentnum);
							int re=pst.executeUpdate();
							if(re>0) {
								res.setCode(200);
								res.setSuccess(true);
								res.setStart(currentnum+1);
								res.setEnd(currentnum+num);
								break;
							}
						}
						
						pst.close();
						st.close();
					}finally {
						con.close();
					}
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
			}
		}
		
		{
			response.getOutputStream().write(JSON.toJSONString(res).getBytes("utf-8"));
			return;
		}
		
	}


}
