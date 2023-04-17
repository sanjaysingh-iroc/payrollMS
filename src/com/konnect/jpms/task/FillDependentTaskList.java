package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.Database;

public class FillDependentTaskList {

	String dependencyId;
	String dependencyName;
	
	HttpServletRequest request;
	public FillDependentTaskList(HttpServletRequest request) {
		this.request = request;
	}
	
	public FillDependentTaskList(){
		
	}
	public String getDependencyId() {
		return dependencyId;
	}
	public void setDependencyId(String dependencyId) {
		this.dependencyId = dependencyId;
	}
	public String getDependencyName() {
		return dependencyName;
	}
	public void setDependencyName(String dependencyName) {
		this.dependencyName = dependencyName;
	}
	public FillDependentTaskList(String dependencyId, String dependencyName) {
		this.dependencyId = dependencyId;
		this.dependencyName = dependencyName;
	}
	
	
	public List<FillDependentTaskList> fillDependentTaskList(int pro_id) {
		
		List<FillDependentTaskList> al = new ArrayList<FillDependentTaskList>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("select task_id,activity_name from activity_info where pro_id=? and pro_id>0 and parent_task_id = 0");
			pst.setInt(1, pro_id);
			rs = pst.executeQuery();
			while(rs.next()){
				if(rs.getString("activity_name") != null && !rs.getString("activity_name").equals("")) {
					String activityName = rs.getString("activity_name").replace(".", ". ");
					activityName = activityName.replace(",", ", ");
					String strTemp[] = activityName.split(" ");
					StringBuilder sbTaskName = new StringBuilder();
					for(int i=0; strTemp.length >0 && i<strTemp.length; i++) {
						if(strTemp[i].trim().length()>0) {
							sbTaskName.append(strTemp[i].trim()+" ");
						}
					}
					al.add(new FillDependentTaskList(rs.getString("task_id"), sbTaskName.toString().trim()));
				}
			}	
			rs.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		return al;
	}
	
	
	public List<FillDependentTaskList> fillDependentSubTaskList(int pro_id, int taskId) {
		
		List<FillDependentTaskList> al = new ArrayList<FillDependentTaskList>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select task_id,activity_name from activity_info where pro_id=? and parent_task_id = ? and parent_task_id > 0");
			pst.setInt(1, pro_id);
			pst.setInt(2, taskId);
			rs = pst.executeQuery();
			while(rs.next()){
				if(rs.getString("activity_name") != null && !rs.getString("activity_name").equals("")) {
					String activityName = rs.getString("activity_name").replace(".", ". ");
					activityName = activityName.replace(",", ", ");
					String strTemp[] = activityName.split(" ");
					StringBuilder sbTaskName = new StringBuilder();
					for(int i=0; strTemp.length >0 && i<strTemp.length; i++) {
						if(strTemp[i].trim().length()>0) {
							sbTaskName.append(strTemp[i].trim()+" ");
						}
					}
					al.add(new FillDependentTaskList(rs.getString("task_id"), sbTaskName.toString().trim()));
				}
			}	
			rs.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		return al;
	}
	
}
