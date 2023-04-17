package com.konnect.jpms.training;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ViewCertificate extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;
	private String operation;
	Boolean autoGenerate = false;
	CommonFunctions CF=null;
	
	private static Logger log = Logger.getLogger(ViewCertificate.class);
	
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
	private String fromPage;
	public String execute() {
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);

		if(CF==null)return LOGIN;
	
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		String certificateId = request.getParameter("ID");
		
		viewCertificate(certificateId);
		
		return SUCCESS;

	}

	private void viewCertificate( String certificateId) {

		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		Connection con = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		List<String> certiDetails = new ArrayList<String>();
		try {
			 
			con = db.makeConnection(con);
			boolean flag = false;
			pst = con.prepareStatement("select * from certificate_details where parent_certificate_id =? or root_certificate_id =? order by certificate_details_id desc");
			pst.setInt(1, uF.parseToInt(certificateId));
			pst.setInt(2, uF.parseToInt(certificateId));
			rs = pst.executeQuery();
			while (rs.next()) {
				flag = true;
			}
			rs.close();
			pst.close();
			
			pst=con.prepareStatement("select * from certificate_details where certificate_details_id =?");
			pst.setInt(1, uF.parseToInt(certificateId));
//			System.out.println("pst==>"+pst);
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
				
				certiDetails.add(rs.getString("certificate_details_id"));//0
				certiDetails.add(rs.getString("certificate_name"));//1
				certiDetails.add(rs.getString("certificate_title"));//2
				
				if(rs.getString("update_date") != null && rs.getString("updated_by") != null) {
					certiDetails.add(CF.getEmpNameMapByEmpId(con, rs.getString("updated_by")));//3
					certiDetails.add(uF.getDateFormat(rs.getString("update_date"), DBDATE, CF.getStrReportDateFormat()));//4
				} else {
					certiDetails.add(CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));//3
					certiDetails.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));//4
				}
				certiDetails.add(rs.getString("parent_certificate_id"));//5
				certiDetails.add(rs.getString("root_certificate_id"));//6
				boolean certiStatus = checkCertiStatus(con, rs.getString("certificate_details_id"));
				certiDetails.add(""+certiStatus);//7
				
			}
			rs.close();
			pst.close();
			
			String strFirstLine = CF.getCertificateFirstLine(con, getFirstLine());
			String strSecondLine = CF.getCertificateSecondLine(con, getSecondLine());
			String strThirdLine = CF.getCertificateThirdLine(con, getThirdLine());
			String strfontSize = CF.getCertificateFontSize(con, getFontSize());
			
			request.setAttribute("strFirstLine", strFirstLine);
			request.setAttribute("strSecondLine", strSecondLine);
			request.setAttribute("strThirdLine", strThirdLine);
			request.setAttribute("strfontSize", strfontSize);
			request.setAttribute("certiDetails", certiDetails);
			request.setAttribute("flag", flag+"");
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
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

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

}
