package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.Database;

public class FillActivityDetails {

	HttpServletRequest request;
	public FillActivityDetails(HttpServletRequest request){
		this.request = request;
	}
	public FillActivityDetails(){}
	public String getActivitName(int activity_id)
	{
		
		String a_name="";
		String selectEmployeeByShift = "select activity_name from activity_info where task_id=?";
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(selectEmployeeByShift);
			pst.setInt(1,activity_id);
			rs = pst.executeQuery();
			while(rs.next()){
				a_name=rs.getString("activity_name");				
			}
			rs.close();
			pst.close();	
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		return a_name;
		
	}
}
