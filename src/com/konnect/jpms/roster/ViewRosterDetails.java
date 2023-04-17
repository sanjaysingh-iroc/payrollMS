package com.konnect.jpms.roster;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;



public class ViewRosterDetails implements ServletRequestAware,IStatements{

	String emp;
	String fDate;
	int id;
	String tDate;
	String shift;
	
	private ViewRosterDetails(String emp, String fDate, String tDate, String shift) {
		this.emp = emp;
		this.fDate = fDate;
		this.tDate = tDate;
		this.shift = shift;
		
	}
	
//	public ViewRosterDetails(int serviceId) {
//		
//	    id=serviceId;
//	}
public ViewRosterDetails() {
		
	   
	}
	
	String selectRosterDetails = "select * from roster_details ";
	public List<ViewRosterDetails> viewRosterDetails(){
		
		List<ViewRosterDetails> al = new ArrayList<ViewRosterDetails>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(selectRosterDetails);
			rs = pst.executeQuery();
			while(rs.next()){
				al.add(new ViewRosterDetails(rs.getString("emp_id"), rs.getString("_date"),rs.getString("_from"),rs.getString("_to")));				
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
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
	public String getEmp() {
		return emp;
	}

	public void setEmp(String emp) {
		this.emp = emp;
	}

	public String getfDate() {
		return fDate;
	}

	public void setfDate(String fDate) {
		this.fDate = fDate;
	}


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String gettDate() {
		return tDate;
	}

	public void settDate(String tDate) {
		this.tDate = tDate;
	}

	public String getShift() {
		return shift;
	}

	public void setShift(String shift) {
		this.shift = shift;
	}

	public String getSelectRosterDetails() {
		return selectRosterDetails;
	}

	public void setSelectRosterDetails(String selectRosterDetails) {
		this.selectRosterDetails = selectRosterDetails;
	}
	
	
}
