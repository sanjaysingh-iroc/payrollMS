package com.konnect.jpms.select;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;

public class FillEmpStatus implements IStatements{

	String empStatusId;
	String empStatusName;
	
	private FillEmpStatus(String empStatusId, String empStatusName) {
		this.empStatusId = empStatusId;
		this.empStatusName = empStatusName;
	}
	HttpServletRequest request;
	public FillEmpStatus(HttpServletRequest request) {
		this.request = request;
	}
	public FillEmpStatus() {
	}
	
	public List<FillEmpStatus> fillEmpStatus(){
		
		List<FillEmpStatus> al = new ArrayList<FillEmpStatus>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(selectEmpStatus);
			rs = pst.executeQuery();
			while(rs.next()){
				al.add(new FillEmpStatus(rs.getString("status_code"), "["+rs.getString("status_code")+"] "+rs.getString("status_name")));				
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

	public String getEmpStatusId() {
		return empStatusId;
	}

	public void setEmpStatusId(String empStatusId) {
		this.empStatusId = empStatusId;
	}

	public String getEmpStatusName() {
		return empStatusName;
	}

	public void setEmpStatusName(String empStatusName) {
		this.empStatusName = empStatusName;
	}

}  
