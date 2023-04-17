package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.SessionAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class PublishOneToOne implements ServletRequestAware, SessionAware, IStatements {
	CommonFunctions CF;
	HttpServletRequest request;
	Map session;
	private String strUserType = null;
	private String strSessionEmpId = null;

	private String oneToOneid;
	private String dcount;
	private String from;
	private String appFreqId;
	private String fromPage;
	public String execute() {
		strUserType = (String) session.get(USERTYPE);
		strSessionEmpId = (String) session.get(EMPID);

		CF = (CommonFunctions) session.get(CommonFunctions);
		if (CF == null)
			return "login";
		
		setOneToOnePublishStatus();
//		getReviewDetails();
		
		return "success";
	}
	public void setOneToOnePublishStatus()
	{
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		boolean is_publish = false;
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select is_publish from onetoone_details where id = ?");
			pst.setInt(1, uF.parseToInt(getOneToOneid()));
			rst = pst.executeQuery();

			while (rst.next()) {
				is_publish=rst.getBoolean(1);
			}
			rst.close();
			pst.close();
			
			
			String query="";
			if(is_publish == true) {
				query ="update onetoone_details set is_publish = false where  id = ?";
			} else {
				//query ="update appraisal_details_frequency set is_appraisal_publish=true where appraisal_id=? and appraisal_freq_id = ?";
				query ="update onetoone_details set is_publish = true where  id = ?";
			}
			pst = con.prepareStatement(query);
			pst.setInt(1, uF.parseToInt(getOneToOneid()));	
			System.out.println("pst=====>"+pst);
			pst.execute();
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		request.setAttribute("is_publish", is_publish);
	}
	
	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
	}

	@Override
	public void setSession(Map session) {
		this.session = session;
	}
	public String getOneToOneid() {
		return oneToOneid;
	}
	public void setOneToOneid(String oneToOneid) {
		this.oneToOneid = oneToOneid;
	}

}
