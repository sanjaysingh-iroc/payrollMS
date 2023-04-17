package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.select.FillTask;
import com.konnect.jpms.util.Database;

public class FillActivityList {
	String activityID;
	String activityName;
	
	public FillActivityList(String activityID, String activityName) {
	
		this.activityID = activityID;
		this.activityName = activityName;
	}
	
	HttpServletRequest request;
	public FillActivityList(HttpServletRequest request) {
		this.request = request;
	}
	
	public FillActivityList() {
		
	}


//	public List<FillActivityList> fillActivityDetailsByProject(int pro_id) {
//		
//		List<FillActivityList> al = new ArrayList<FillActivityList>();
//		String selectactivity = "select task_id,activity_name from activity_info where pro_id=? and approve_status='n'";
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//		try {
//			con = db.makeConnection(con);
//			pst = con.prepareStatement(selectactivity);
//			pst.setInt(1, pro_id);
//			rs = pst.executeQuery();
//			while(rs.next()) {
//				al.add(new FillActivityList(rs.getString("task_id"), rs.getString("activity_name")));				
//			}	
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			
//			db.closeStatements(pst);
//			db.closeResultSet(rs);
//			db.closeConnection(con);
//		}
//		
//		return al;
//	}

	
public List<FillActivityList> fillActivityOfProjectsAndOther(String pro_id, String nEmpId) {
		
		List<FillActivityList> al = new ArrayList<FillActivityList>();
//		String selectactivity = "select task_id,activity_name from activity_info where pro_id=? and approve_status='n'";
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			al.add(new FillActivityList("a Desk Job", "a Desk Job"));
			al.add(new FillActivityList("a Call", "a Call"));
			al.add(new FillActivityList("a Sales Call", "a Sales Call"));
			al.add(new FillActivityList("a Conference Call", "a Conference Call"));
			al.add(new FillActivityList("a Meeting with my Supervisor", "a Meeting with my Supervisor"));
			al.add(new FillActivityList("a Meeting with my Subordinate", "a Meeting with my Subordinate"));
			al.add(new FillActivityList("a Meeting with HR", "a Meeting with HR"));
			al.add(new FillActivityList("a Team Meeting", "a Team Meeting"));
			al.add(new FillActivityList("a Meeting with Client", "a Meeting with Client"));
			al.add(new FillActivityList("a Client Demo", "a Client Demo"));
			al.add(new FillActivityList("a Client Visit", "a Client Visit"));
			al.add(new FillActivityList("a Field Visit", "a Field Visit"));
			al.add(new FillActivityList("a Tranning Session", "a Tranning Session"));
			al.add(new FillActivityList("to Pantry", "to Pantry"));
			al.add(new FillActivityList("a Coffee Break", "a Coffee Break"));
			al.add(new FillActivityList("a Break", "a Break"));
			al.add(new FillActivityList("Quality Control","Quality Control"));
			
			if(pro_id != null && !pro_id.equals("")) {
//				pst = con.prepareStatement("select task_id,activity_name from activity_info where pro_id in (select pro_id from projectmntnc where " +
//					"pro_id in ("+pro_id+") and approve_status='n') order by task_id");
				pst = con.prepareStatement("select a.*,pmc.pro_name from (select task_id,activity_name,parent_task_id,pro_id from activity_info where resource_ids like '%,"+nEmpId+",%' " +
				"and task_id not in (select parent_task_id from activity_info where resource_ids like '%,"+nEmpId+",%' and parent_task_id is not null)) a, " +
				"projectmntnc pmc where pmc.pro_id=a.pro_id and pmc.approve_status='n' and (parent_task_id in (select task_id from activity_info " +
				"where resource_ids like '%,"+nEmpId+",%') or parent_task_id = 0) and a.pro_id in ("+pro_id+")");
	//			pst.setInt(1, pro_id);
				rs = pst.executeQuery();
				while(rs.next()) {
					if(rs.getInt("parent_task_id") > 0) {
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
							al.add(new FillActivityList(rs.getString("task_id"), sbTaskName.toString().trim() + " [ST]" + " (" +rs.getString("pro_name")+ ")"));
						}
					} else {
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
							al.add(new FillActivityList(rs.getString("task_id"), sbTaskName.toString().trim() + " (" +rs.getString("pro_name")+ ")"));
						}
					}
									
				}
				rs.close();
				pst.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		return al;
	}


	
	public List<FillActivityList> fillExtraActivity() {
		
		List<FillActivityList> al = new ArrayList<FillActivityList>();
		
				al.add(new FillActivityList("a Desk Job","a Desk Job"));
				al.add(new FillActivityList("a Call","a Call"));
				al.add(new FillActivityList("a Sales Call","a Sales Call"));
				al.add(new FillActivityList("a Conference Call","a Conference Call"));
				al.add(new FillActivityList("a Meeting with my Supervisor","a Meeting with my Supervisor"));
				al.add(new FillActivityList("a Meeting with my Subordinate","a Meeting with my Subordinate"));
				al.add(new FillActivityList("a Meeting with HR","a Meeting with HR"));
				al.add(new FillActivityList("a Team Meeting","a Team Meeting"));
				al.add(new FillActivityList("a Meeting with Client","a Meeting with Client"));
				al.add(new FillActivityList("a Client Demo","a Client Demo"));
				al.add(new FillActivityList("a Client Visit","a Client Visit"));
				al.add(new FillActivityList("a Field Visit","a Field Visit"));
				al.add(new FillActivityList("a Tranning Session","a Tranning Session"));
				al.add(new FillActivityList("to Pantry","to Pantry"));
				al.add(new FillActivityList("a Coffee Break","a Coffee Break"));
				al.add(new FillActivityList("a Break","a Break"));
				al.add(new FillActivityList("Quality Control","Quality Control"));
				
		return al;
	}
	
	
	public List<Integer> fillActivityIdByProject(int pro_id, String nEmpId) {
		
		List<Integer> al = new ArrayList<Integer>();
//		String selectactivity = "select task_id from activity_info where pro_id=?";
		String selectactivity = "select * from (select task_id,activity_name,parent_task_id,pro_id from activity_info where resource_ids like '%,"+nEmpId+",%' " +
				"and task_id not in (select parent_task_id from activity_info where resource_ids like '%,"+nEmpId+",%' and parent_task_id is not null)) a, " +
				"projectmntnc pmc where pmc.pro_id=a.pro_id and pmc.approve_status='n' and (parent_task_id in (select task_id from activity_info " +
				"where resource_ids like '%,"+nEmpId+",%') or parent_task_id = 0) and a.pro_id =?";
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(selectactivity);
			pst.setInt(1, pro_id);
			rs = pst.executeQuery();
			while(rs.next()) {
				al.add(rs.getInt("task_id"));				
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

	public String getActivityID() {
		return activityID;
	}

	public void setActivityID(String activityID) {
		this.activityID = activityID;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}
}
