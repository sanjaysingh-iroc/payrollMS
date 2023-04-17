package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class SaveTaskAjax extends ActionSupport implements ServletRequestAware, IStatements{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4327666684760826584L;
	HttpServletRequest request;
	public String execute(){
		 
		return SUCCESS;
	}
	
	public void submit() {

		String pro_id=null;
		UtilityFunctions uF = new UtilityFunctions();
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {

			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select * from activity_info where pro_id=? ");
			pst.setInt(1, uF.parseToInt(pro_id));
			
			rs=pst.executeQuery();
			List<List<String>> alOuter=new ArrayList<List<String>>();
			while(rs.next()){
				List<String> alInner=new ArrayList<String>();
				alInner.add(rs.getString("activity_name"));
				alInner.add(rs.getString("priority"));
				alInner.add(rs.getString("emp_id"));
				alInner.add(rs.getString("deadline"));
				alInner.add(rs.getString("idealtime"));
				alInner.add(rs.getString("start_date"));
				alInner.add(rs.getString("dependency_task"));
				alInner.add(rs.getString("dependency_type"));
				alInner.add(rs.getString("color_code"));
				
				alOuter.add(alInner);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alOuter",alOuter);

			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
	}

}
