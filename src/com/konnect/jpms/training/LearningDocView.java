package com.konnect.jpms.training;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class LearningDocView extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	
	HttpSession session;
	String strUserType = null;
	
	private CommonFunctions CF;
	
	private String strViewDocument;
	private String strId;
	
	public String execute() {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null) return LOGIN;

		strUserType = (String)session.getAttribute(USERTYPE);
		
		request.setAttribute(TITLE, "Learning");
		request.setAttribute(PAGE, "LearningDocView.jsp");
		
		UtilityFunctions uF = new UtilityFunctions();
		getContentsList(uF);
		
		return LOAD;
	}
	
	private void getContentsList(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select * from course_content_details where course_content_id = ?");
			pst.setInt(1, uF.parseToInt(getStrId()));
			rs = pst.executeQuery();
			String strCourseContent = "";
			while (rs.next()) {
				if(rs.getString("content_type") != null &&(rs.getString("content_type").equals("IMAGE") || rs.getString("content_type").equals("PDF") || rs.getString("content_type").equals("ATTACH") || rs.getString("content_type").equals("PPT"))) {
					String filePath = null;
					if(CF.getIsRemoteLocation()){
						filePath = CF.getStrDocRetriveLocation() + rs.getString("course_content_name");
					} else {
						filePath = request.getContextPath()+"/userImages/" + rs.getString("course_content_name") + "";
					}
					strCourseContent = filePath;
				}
			}
			rs.close();
			pst.close();
			
			request.setAttribute("strCourseContent", strCourseContent);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getStrViewDocument() {
		return strViewDocument;
	}

	public void setStrViewDocument(String strViewDocument) {
		this.strViewDocument = strViewDocument;
	}

	public String getStrId() {
		return strId;
	}

	public void setStrId(String strId) {
		this.strId = strId;
	}

}
