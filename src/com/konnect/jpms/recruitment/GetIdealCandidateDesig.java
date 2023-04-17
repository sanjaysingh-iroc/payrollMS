package com.konnect.jpms.recruitment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetIdealCandidateDesig extends ActionSupport implements ServletRequestAware{

	  String strDesig;
	  String idealCandidate;

	  private static final long serialVersionUID = 1L;

	  public String execute() {
		 
		  getDesigDetails();
		  
		return SUCCESS;			
	  }


	private void getDesigDetails() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from designation_details where designation_id = ?");
			pst.setInt(1, uF.parseToInt(getStrDesig()));
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while(rs.next()){
				setIdealCandidate(rs.getString("ideal_candidate"));
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

	}


	public String getStrDesig() {
		return strDesig;
	}


	public void setStrDesig(String strDesig) {
		this.strDesig = strDesig;
	}


	public String getIdealCandidate() {
		return idealCandidate;
	}

	public void setIdealCandidate(String idealCandidate) {
		this.idealCandidate = idealCandidate;
	}


	HttpServletRequest request;
		
	  @Override
	  public void setServletRequest(HttpServletRequest request) {
			this.request = request;
	  }

}
