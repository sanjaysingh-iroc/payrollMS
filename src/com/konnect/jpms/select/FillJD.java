package com.konnect.jpms.select;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class FillJD implements IStatements{
	
	String strJDId;
	String strJDName;
	
	UtilityFunctions uF = new UtilityFunctions();

	public FillJD(String strJDId, String strJDName) {
		super();
		this.strJDId = strJDId;
		this.strJDName = strJDName;
	}
	
	HttpServletRequest request;
	public FillJD(HttpServletRequest request) {
		this.request = request;
	}
	
	public FillJD() {}
	
	
	public List<FillJD> fillJD(String candidateId) {
		List<FillJD> al = new ArrayList<FillJD>();
		//Map<String,String> alJD = new ArrayList<Integer>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from candidate_application_details where candidate_id=?");
			pst.setInt(1, uF.parseToInt(candidateId));
			rs = pst.executeQuery();
			while(rs.next()) {
				al.add(new FillJD(rs.getString("recruitment_id"), rs.getString("job_code")));				
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
//		System.out.println("JDList:"+al);
		return al;
	}
	
	
	public List<FillJD> fillLiveJobs(UtilityFunctions uF, String candidateId) {
		List<FillJD> al = new ArrayList<FillJD>();
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		
		try {
			StringBuilder sb = new StringBuilder("");
			con = db.makeConnection(con);			
			pst = con.prepareStatement("select * from recruitment_details where close_job_status=false and job_approval_status = 1 and " +
				"recruitment_id not in (select recruitment_id from candidate_application_details where candidate_id = ?)");
			pst.setInt(1, uF.parseToInt(candidateId));
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rs.next()) {
				al.add(new FillJD(rs.getString("recruitment_id"), rs.getString("job_code")+" ("+rs.getString("job_title")+")"));
//				sb.append("<option value=\"" + rs.getString("recruitment_id") + "\">" + rs.getString("job_code") + "</option>");
			}
			rs.close();
			pst.close();
			
			request.setAttribute("option", sb.toString());
//			request.setAttribute("queCnt", queCnt);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}
	
	
	public String getStrJDId() {
		return strJDId;
	}
	
	public void setStrJDId(String strJDId) {
		this.strJDId = strJDId;
	}
	
	public String getStrJDName() {
		return strJDName;
	}

	public void setStrJDName(String strJDName) {
		this.strJDName = strJDName;
	}

}