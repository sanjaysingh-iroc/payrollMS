package com.konnect.jpms.training;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class CertificateInfo extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;

//	private int empId;
	private String strCertiId;
	CommonFunctions CF = null;
	private String strSearchJob;
	private static Logger log = Logger.getLogger(CertificateInfo.class);

	public String execute() {
		UtilityFunctions uF = new UtilityFunctions();
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) {
			return LOGIN;
		}
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		viewCertificateDetails();
		prepareInformation(uF);
		if(strUserType != null && !strUserType.equals(EMPLOYEE)) {
			getSearchAutoCompleteData();
		}
		return SUCCESS;

	}

	private void getSearchAutoCompleteData() {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);

			SortedSet<String> setCertNamesList = new TreeSet<String>();
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from certificate_details  where certificate_details_id > 0 ");
	    
    	    if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("") &&  !getStrSearchJob().equalsIgnoreCase("null")) {
				sbQuery.append(" and (upper(certificate_name) like '%"+getStrSearchJob().trim().toUpperCase()+"%'"
						+" or upper(certificate_title) like '%"+getStrSearchJob().trim().toUpperCase()+"%' )");
				
            }
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst certi search===> "+ pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				setCertNamesList.add(rs.getString("certificate_name"));
			}
			rs.close();
			pst.close();
			
			StringBuilder sbData = null;
			Iterator<String> it = setCertNamesList.iterator();
			while (it.hasNext()) {
				String strData = it.next();
				if(sbData == null) {
					sbData = new StringBuilder();
					sbData.append("\""+strData+"\"");
				} else {
					sbData.append(",\""+strData+"\"");
				}
			}
			
			if(sbData == null) {
				sbData = new StringBuilder();
			}
			
//			System.out.println("sbData==>"+sbData);
			request.setAttribute("sbData", sbData.toString());
				
		} catch (Exception e) {
			e.printStackTrace(); 
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private void viewCertificateDetails() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			int i =0 ;
			Map<String, String> hmCertiData = new HashMap<String, String>();
			Map<String, List<String>> hmAllCertiData = new HashMap<String, List<String>>();
			StringBuilder strQuery = new StringBuilder();
			strQuery.append("select * from certificate_details where root_certificate_id is not null order by certificate_details_id desc");
			pst = con.prepareStatement(strQuery.toString());
			rst = pst.executeQuery();
//			System.out.println("pst ====> "+pst);
			List<String> certiIdList = new ArrayList<String>();
			while (rst.next()) {
				certiIdList = hmAllCertiData.get(rst.getString("root_certificate_id"));
				if(certiIdList == null) certiIdList = new ArrayList<String>();
				
				certiIdList.add(rst.getString("certificate_details_id"));
				hmAllCertiData.put(rst.getString("root_certificate_id"), certiIdList);

				if(hmCertiData.get(rst.getString("root_certificate_id")) == null) {
					hmCertiData.put(rst.getString("root_certificate_id"), rst.getString("certificate_details_id"));
					setStrCertiId(rst.getString("certificate_details_id"));
				}
				
				i++;
			}
			rst.close();
			pst.close();
			
			int j = 0;
			strQuery = new StringBuilder();
			List<String> certiIDList = new ArrayList<String>();
			strQuery.append("select * from certificate_details where parent_certificate_id is null and root_certificate_id is null ");
			if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("") &&  !getStrSearchJob().equalsIgnoreCase("null")) {
				 strQuery.append(" and (upper(certificate_name) like '%"+getStrSearchJob().trim().toUpperCase()+"%'"
				 +" or upper(certificate_title) like '%"+getStrSearchJob().trim().toUpperCase()+"%' )");
			 }
			 strQuery.append(" order by certificate_details_id desc");
			pst = con.prepareStatement(strQuery.toString());
			rst = pst.executeQuery();
			while (rst.next()) {
				if(j == 0 && (getStrCertiId() == null || getStrCertiId().equals(""))) {
					setStrCertiId(rst.getString("certificate_details_id"));
				}
				certiIDList.add(rst.getString("certificate_details_id"));
				j++;
			}
			rst.close();
			pst.close();
			
			request.setAttribute("certiIDList", certiIDList);
//			System.out.println("courseIDList ===> " + courseIDList);
			
		
			
//			System.out.println("hmAllCourseData ===> " + hmAllCourseData);
//			System.out.println("hmCourseData ===> " + hmCourseData);
			request.setAttribute("hmAllCertiData", hmAllCertiData);
			request.setAttribute("hmCertiData", hmCertiData);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	private boolean checkCertiStatus(Connection con, String certiId) {
		PreparedStatement pst = null;
		ResultSet rst = null;
		UtilityFunctions uF = new UtilityFunctions();
		boolean certiStatusFlag = false;
		try {
			
			pst = con.prepareStatement("select * from learning_plan_details where certificate_id = ?");
			pst.setInt(1, uF.parseToInt(certiId));
			rst = pst.executeQuery();
//			System.out.println("pst ===> " + pst);
			while (rst.next()) {
				certiStatusFlag = true;
			}
			rst.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if(rst !=null){
				try {
					rst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return certiStatusFlag;
	}
	private void prepareInformation(UtilityFunctions uF) {

		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		Connection con = null;
		ResultSet rst = null;
		try {
			con = db.makeConnection(con);
			
			Map<String, List<String>> hmCertificateDetails = new HashMap<String, List<String>>();
			pst = con.prepareStatement("select * from certificate_details order by certificate_details_id desc");
			rst = pst.executeQuery();
			
			while (rst.next()) {
				List<String> alInner = new ArrayList<String>();	
				StringBuilder sbStatus = new StringBuilder();
				alInner.add(rst.getString("certificate_details_id"));//0
				alInner.add(rst.getString("certificate_name"));//1
				alInner.add(rst.getString("certificate_title"));//2
				boolean certiStatus = checkCertiStatus(con, rst.getString("certificate_details_id"));
				if(!certiStatus){
					sbStatus.append("<div style=\"float:left;border-left:4px solid #ff9a02;padding:10px;\" class=\"custom-legend pullout\"><div class=\"legend-info\"></div></div>");//3
				}else {
					sbStatus.append("<div style=\"float:left;border-left:4px solid #15AA08;padding:10px;\" class=\"custom-legend approved\"><div class=\"legend-info\"></div></div>");//3
				}
				alInner.add(sbStatus.toString());//3
				hmCertificateDetails.put(rst.getString("certificate_details_id"), alInner);

			}
			rst.close();
			pst.close();
			
			request.setAttribute("hmCertificateDetails", hmCertificateDetails);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);

		}
	}
	
	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;

	}


	public String getStrCertiId() {
		return strCertiId;
	}


	public void setStrCertiId(String strCertiId) {
		this.strCertiId = strCertiId;
	}

	public String getStrSearchJob() {
		return strSearchJob;
	}

	public void setStrSearchJob(String strSearchJob) {
		this.strSearchJob = strSearchJob;
	}
}