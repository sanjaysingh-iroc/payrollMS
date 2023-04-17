package com.konnect.jpms.select;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;

public class FillActivity implements IStatements{

	String activityId;
	String activityName;
	     
	private FillActivity(String activityId, String activityName) {
		this.activityId = activityId;
		this.activityName = activityName;
	}
	
	HttpServletRequest request;
	public FillActivity(HttpServletRequest request) {
		this.request = request;
	}
	
	public FillActivity() {
	}
	
	final static public String selectActivity = "SELECT * FROM activity_details order by activity_name";
	final static public String selectActivity1 = "SELECT * FROM activity_details where isactivity = true order by activity_name";
	
	public List<FillActivity> fillActivity(boolean isActivity){
		
		List<FillActivity> al = new ArrayList<FillActivity>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {

			con = db.makeConnection(con);
			
			if(isActivity) {
				pst = con.prepareStatement(selectActivity1);
			}else{
				pst = con.prepareStatement(selectActivity);
			}
			
			rs = pst.executeQuery();
			while(rs.next()){
				al.add(new FillActivity(rs.getString("activity_id"), rs.getString("activity_name")));				
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
		return al;
	}

	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public List<FillActivity> fillActivityByNode(boolean isActivity, boolean isNode) {
		
		List<FillActivity> al = new ArrayList<FillActivity>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		
		try {

			con = db.makeConnection(con);
			if(isNode){
				pst = con.prepareStatement("select activity_id,activity_name from activity_details ad, nodes n where ad.activity_id=n.mapped_activity_id and ad.isactivity=? order by activity_name");
			} else{
				pst = con.prepareStatement("select activity_id,activity_name from activity_details where isactivity=? and activity_id not in (select mapped_activity_id from nodes where mapped_activity_id > 0) order by activity_name");
			}
			pst.setBoolean(1, isActivity);
			rs = pst.executeQuery();
//			System.out.println("FAct/107---pst=="+pst);
			while(rs.next()){
				al.add(new FillActivity(rs.getString("activity_id"), rs.getString("activity_name")));				
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
		return al;
	}
	
	public List<FillActivity> fillActivityByNode(boolean isActivity, boolean isNode, int empId) {
		
		List<FillActivity> al = new ArrayList<FillActivity>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			
			con = db.makeConnection(con);
			
			String empStatus ="";
			pst = con.prepareStatement("select emp_status from employee_personal_details where emp_per_id = ? ");
			pst .setInt(1, empId);
			
			rs = pst.executeQuery();
			while(rs.next()){
				empStatus = rs.getString("emp_status");	
			}	
			rs.close();
			pst.close();
			
//		    System.out.println("isNode==>"+isNode);
			if(isNode){
				pst = con.prepareStatement("select activity_id,activity_name from activity_details ad, nodes n where ad.activity_id=n.mapped_activity_id and ad.isactivity=? order by activity_name");
			} else{
				if(empStatus != null && !empStatus.equals("") && empStatus.equalsIgnoreCase("PERMANENT")) {
					pst = con.prepareStatement("select activity_id,activity_name from activity_details where isactivity=? and activity_id not in (select mapped_activity_id from nodes where mapped_activity_id > 0) and activity_id not in (3,4,14) order by activity_name");
				} else if(empStatus != null && !empStatus.equals("") && !empStatus.equalsIgnoreCase("RESIGNED")) {
					pst = con.prepareStatement("select activity_id,activity_name from activity_details where isactivity=? and activity_id not in (select mapped_activity_id from nodes where mapped_activity_id > 0) and activity_id not in (14) order by activity_name");
				} else {
					pst = con.prepareStatement("select activity_id,activity_name from activity_details where isactivity=? and activity_id not in (select mapped_activity_id from nodes where mapped_activity_id > 0) order by activity_name");
				}
			}
			
			pst.setBoolean(1, isActivity);
//			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			while(rs.next()){
				al.add(new FillActivity(rs.getString("activity_id"), rs.getString("activity_name")));				
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
