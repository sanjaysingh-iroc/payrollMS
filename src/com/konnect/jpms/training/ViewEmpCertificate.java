package com.konnect.jpms.training;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ViewEmpCertificate extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;
	private String operation;
	Boolean autoGenerate = false;
	CommonFunctions CF=null;
	
	private static Logger log = Logger.getLogger(ViewEmpCertificate.class);
	
	private String certId;
	private String certificateName;
	private String certificateTitle;
	private String certiLogoAlign;
	private String certificateBorder;
	private String firstLine;
	private String secondLine;
	private String thirdLine;
	private String fontSize;
	private String signOne;
	private String signTwo;
	private String signThree;
	
	private String strEmpId;
	private String planId;
	
	private String strEmpFname;
	private String strEmpMname;
	private String strEmpLname;
	private String strGivenBy;
	private String strGivenDate;
	private String strTrainingPlanName;
	
	public String execute() {
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
//		if (CF==null){
//			CF = new CommonFunctions();
//		}
		if(CF==null)return LOGIN;
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		String strEmpId = request.getParameter("strEmpId");
		String planId = request.getParameter("planId");
		
		viewCertificate(strEmpId,planId);
		
		return SUCCESS;

	}

	
	
	private void viewCertificate(String strEmpId, String planId) {

		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		Connection con = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			 
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			Map<String, String> hmEmpCodeName = CF.getEmpNameMap(con, null, null); 	
			if (hmEmpCodeName == null) hmEmpCodeName = new HashMap<String, String>();
			
			pst = con.prepareStatement("select * from learning_plan_details where learning_plan_id=? ");
			pst.setInt(1, uF.parseToInt(planId));
			rs = pst.executeQuery();
			String certificateId = null;
			while (rs.next()) {
				certificateId = rs.getString("certificate_id");
				setStrTrainingPlanName(rs.getString("learning_plan_name"));
			}
			rs.close();
			pst.close();
			
			if(uF.parseToInt(certificateId) > 0){
				
				pst = con.prepareStatement("select * from learning_plan_finalize_details where emp_id = ? and learning_plan_id=? ");
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setInt(2, uF.parseToInt(planId));
				rs = pst.executeQuery();
				while (rs.next()) {
					setStrGivenBy(uF.showData(hmEmpCodeName.get(rs.getString("added_by")), ""));
					setStrGivenDate(uF.showData(uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()), ""));
				}
				rs.close();
				pst.close();
				
				pst=con.prepareStatement("select * from certificate_details where certificate_details_id =?");
				pst.setInt(1, uF.parseToInt(certificateId));
				rs = pst.executeQuery();
				while(rs.next()) {
					setCertId(rs.getString("certificate_details_id"));
					setCertificateName(rs.getString("certificate_name"));
					setCertificateTitle(rs.getString("certificate_title"));
					setCertiLogoAlign(rs.getString("certificate_logo_align"));
					setCertificateBorder(rs.getString("certificate_border"));
					setFirstLine(rs.getString("certificate_first_line"));
					setSecondLine(rs.getString("certificate_second_line"));
					setThirdLine(rs.getString("certificate_third_line"));
					setFontSize(rs.getString("certificate_font_size"));
					setSignOne(rs.getString("certificate_sign_one"));
					setSignTwo(rs.getString("certificate_sign_two"));
					setSignThree(rs.getString("certificate_sign_three"));
				}
				rs.close();
				pst.close();
				
				pst = con.prepareStatement(selectEmpDetails1);
				pst.setInt(1, uF.parseToInt(strEmpId));
				rs = pst.executeQuery();
				while(rs.next()){
					setStrEmpFname(rs.getString("emp_fname"));
					
					
					String strEmpMName = "";
					if(flagMiddleName) {
						if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
							strEmpMName = rs.getString("emp_mname");
						}
					}
					setStrEmpMname(strEmpMName);
					setStrEmpLname(rs.getString("emp_lname"));
					
				}
				rs.close();
				pst.close();
				
				String strFirstLine = CF.getCertificateFirstLine(con, getFirstLine());
				String strSecondLine = CF.getCertificateSecondLine(con, getSecondLine());
				String strThirdLine = CF.getCertificateThirdLine(con, getThirdLine());
				String strfontSize = CF.getCertificateFontSize(con, getFontSize());

				
				strFirstLine = parseContent(strFirstLine);
				strSecondLine = parseContent(strSecondLine);
				strThirdLine = parseContent(strThirdLine);
				
				request.setAttribute("strFirstLine", strFirstLine);
				request.setAttribute("strSecondLine", strSecondLine);
				request.setAttribute("strThirdLine", strThirdLine);
				request.setAttribute("strfontSize", strfontSize);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	
	}
	
	public String parseContent(String strParseContent){
		
		if(strParseContent!=null){
			if(getStrEmpFname()!=null && strParseContent.indexOf(C_EMPFNAME)>=0){
				strParseContent = strParseContent.replace(C_EMPFNAME, getStrEmpFname());
			}
			if(getStrEmpMname()!=null && strParseContent.indexOf(C_EMPMNAME)>=0){
				strParseContent = strParseContent.replace(C_EMPMNAME, getStrEmpMname());
			}
			if(getStrEmpLname()!=null && strParseContent.indexOf(C_EMPLNAME)>=0){
				strParseContent = strParseContent.replace(C_EMPLNAME, getStrEmpLname());
			}
			if(getStrTrainingPlanName()!=null && strParseContent.indexOf(C_TRAINING_NAME)>=0){
				strParseContent = strParseContent.replace(C_TRAINING_NAME, getStrTrainingPlanName());
			}
			if(getStrGivenDate()!=null && strParseContent.indexOf(C_CERTIFICATE_DATE)>=0){
				strParseContent = strParseContent.replace(C_CERTIFICATE_DATE, getStrGivenDate());
			}
			if(getStrGivenBy()!=null && strParseContent.indexOf(C_GIVEN_BY)>=0){
				strParseContent = strParseContent.replace(C_GIVEN_BY, getStrGivenBy());
			}
		}
		return strParseContent;
	}
	
	public String getCertId() {
		return certId;
	}

	public void setCertId(String certId) {
		this.certId = certId;
	}

	public String getCertificateName() {
		return certificateName;
	}

	public void setCertificateName(String certificateName) {
		this.certificateName = certificateName;
	}

	public String getCertificateTitle() {
		return certificateTitle;
	}

	public void setCertificateTitle(String certificateTitle) {
		this.certificateTitle = certificateTitle;
	}

	public String getCertiLogoAlign() {
		return certiLogoAlign;
	}

	public void setCertiLogoAlign(String certiLogoAlign) {
		this.certiLogoAlign = certiLogoAlign;
	}

	public String getCertificateBorder() {
		return certificateBorder;
	}

	public void setCertificateBorder(String certificateBorder) {
		this.certificateBorder = certificateBorder;
	}

	public String getFirstLine() {
		return firstLine;
	}

	public void setFirstLine(String firstLine) {
		this.firstLine = firstLine;
	}

	public String getSecondLine() {
		return secondLine;
	}

	public void setSecondLine(String secondLine) {
		this.secondLine = secondLine;
	}

	public String getThirdLine() {
		return thirdLine;
	}

	public void setThirdLine(String thirdLine) {
		this.thirdLine = thirdLine;
	}

	public String getFontSize() {
		return fontSize;
	}

	public void setFontSize(String fontSize) {
		this.fontSize = fontSize;
	}

	public String getSignOne() {
		return signOne;
	}

	public void setSignOne(String signOne) {
		this.signOne = signOne;
	}

	public String getSignTwo() {
		return signTwo;
	}

	public void setSignTwo(String signTwo) {
		this.signTwo = signTwo;
	}

	public String getSignThree() {
		return signThree;
	}

	public void setSignThree(String signThree) {
		this.signThree = signThree;
	}

	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request=request;
		
	}



	public String getStrEmpId() {
		return strEmpId;
	}



	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}



	public String getStrEmpFname() {
		return strEmpFname;
	}



	public void setStrEmpFname(String strEmpFname) {
		this.strEmpFname = strEmpFname;
	}



	public String getStrEmpMname() {
		return strEmpMname;
	}



	public void setStrEmpMname(String strEmpMname) {
		this.strEmpMname = strEmpMname;
	}



	public String getStrEmpLname() {
		return strEmpLname;
	}



	public void setStrEmpLname(String strEmpLname) {
		this.strEmpLname = strEmpLname;
	}



	public String getStrGivenBy() {
		return strGivenBy;
	}



	public void setStrGivenBy(String strGivenBy) {
		this.strGivenBy = strGivenBy;
	}



	public String getStrGivenDate() {
		return strGivenDate;
	}



	public void setStrGivenDate(String strGivenDate) {
		this.strGivenDate = strGivenDate;
	}



	public String getStrTrainingPlanName() {
		return strTrainingPlanName;
	}



	public void setStrTrainingPlanName(String strTrainingPlanName) {
		this.strTrainingPlanName = strTrainingPlanName;
	}


	
}
