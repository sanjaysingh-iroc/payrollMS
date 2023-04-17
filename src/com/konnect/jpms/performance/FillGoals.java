package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;

public class FillGoals implements IStatements{
	
	String id;
	
	String name;
	
	FillGoals(String id, String name) {
		this.id = id;
		this.name = name;
	}
	
	HttpServletRequest request;
	public FillGoals(HttpServletRequest request) {
		this.request = request; 
	}
	
	public FillGoals() {
	}
	public List<FillGoals>	fillGoals(){
		List<FillGoals> al = new ArrayList<FillGoals>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from goal_details");
			rs = pst.executeQuery();
			while(rs.next()) {
				al.add(new FillGoals(rs.getString("goal_id"), rs.getString("goal_title")));		
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		System.out.println("al::::"+al);
		return al;
		
	}
public List<FillGoals> fillGoalsEmpWise(String goalId) {
		
		List<FillGoals> al = new ArrayList<FillGoals>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select distinct(goal_id) as goal_id, goal_title from goal_details  "); 
			if(goalId != null && !goalId.equals("")) {
				sbQuery.append(" where goal_id in ("+goalId+") ");
			}
			pst = con.prepareStatement(sbQuery.toString());
			System.out.println("pst===>"+pst);
			rs = pst.executeQuery();
			while(rs.next()) {
				al.add(new FillGoals(rs.getString("goal_id"), rs.getString("goal_title")));				
			}
			rs.close();
			pst.close();
//			System.out.println("All Attributes Are ========== > "+al.get(0).getId() + " & " + al.get(1).getName());
			

		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		System.out.println("al::::"+al);
		return al;
	}
public String getId() {
	return id;
}

public void setId(String id) {
	this.id = id;
}

public String getName() {
	return name;
}

public void setName(String name) {
	this.name = name;
}


}
