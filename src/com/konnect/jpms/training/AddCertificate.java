package com.konnect.jpms.training;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

public class AddCertificate extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;
//	private String mode;
	private int empId;  
	private String operation;
	Boolean autoGenerate = false;
//	private String step;  
	CommonFunctions CF=null;
	 
	private String type;
	
	private String addCertificateSubmit;

	
	HttpServletRequest request;
//	String certPrintMode;
	private String certiId;
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
	private String assignToExist;

	private List<FillCertificate> certificateList;
	private List<FillCertificate> borderList;
	private List<FillCertificate> firstLineList;
	private List<FillCertificate> secondLineList;
	private List<FillCertificate> thirdLineList;
	private List<FillCertificate> firstFontList;
	private List<FillCertificate> certiLogoAlignList;
	
	private static Logger log = Logger.getLogger(AddCertificate.class);
	
	public String execute() {
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);

		if(CF==null)return LOGIN;

		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
			
		UtilityFunctions uF = new UtilityFunctions();
		String strOperation = request.getParameter("operation");
		
		borderList = new FillCertificate(request).fillBorderList();
		firstLineList = new FillCertificate(request).fillFirstLineList();
		secondLineList = new FillCertificate(request).fillSecondLineList();
		thirdLineList = new FillCertificate(request).fillThirdLineList();
		firstFontList = new FillCertificate(request).fillFontSizeList();
		certiLogoAlignList = new FillCertificate(request).fillCertiLogoAlignList();
//		System.out.println("strOperation==>"+strOperation+"==>getCertiId==>"+getCertiId());
		if(strOperation!=null && strOperation.equalsIgnoreCase("E")) {
			viewCertificate(getCertiId());
		}else if(strOperation!=null && strOperation.equalsIgnoreCase("U")) {
			updateData(getCertiId(), uF);
			return SUCCESS;
		}else if(strOperation!=null && strOperation.equalsIgnoreCase("D")) {
			return deleteCertificate(getCertiId());
		}else if(getAddCertificateSubmit()!=null) {
			return addCertificate();
		}
		
		return "popup";

	}
	
	
	private String addCertificate() {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rst = null;
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			con = db.makeConnection(con);
			
			pst=con.prepareStatement("insert into certificate_details (certificate_name,certificate_title,certificate_logo_align,certificate_border," +
					"certificate_first_line,certificate_second_line,certificate_third_line,certificate_font_size,certificate_sign_one," +
					"certificate_sign_two,certificate_sign_three,added_by,entry_date) values(?,?,?,?, ?,?,?,?, ?,?,?,?,?)");
			pst.setString(1, getCertificateName());
			pst.setString(2, getCertificateTitle());
			pst.setInt(3, uF.parseToInt(getCertiLogoAlign()));
			pst.setInt(4, uF.parseToInt(getCertificateBorder()));
			pst.setInt(5, uF.parseToInt(getFirstLine()));
			pst.setInt(6, uF.parseToInt(getSecondLine()));
			pst.setInt(7, uF.parseToInt(getThirdLine()));
			pst.setInt(8, uF.parseToInt(getFontSize()));
			pst.setInt(9, uF.parseToInt(getSignOne()));
			pst.setInt(10, uF.parseToInt(getSignTwo()));
			pst.setInt(11, uF.parseToInt(getSignThree()));
			pst.setInt(12, uF.parseToInt(strSessionEmpId));
			pst.setDate(13, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.execute();
			pst.close();
			
			int newCertiId = 0;
			pst = con.prepareStatement("select max(certificate_details_id) as certificate_details_id from certificate_details");
			rst = pst.executeQuery();
			while (rst.next()) {
				newCertiId = rst.getInt("certificate_details_id");
			}
			rst.close();
			pst.close();
			
			String certificateName = getCertificateNameById(con, uF, ""+newCertiId);
			session.setAttribute(MESSAGE, SUCCESSM+""+certificateName+" certificate has been created successfully."+END);
			
		}catch(Exception e){
			
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		if(getType() != null && getType().equals("training")){
			return "successgoback";
		} else {
			return "success";
		}
		
	}
	
	
	private String getCertificateNameById(Connection con, UtilityFunctions uF, String certificateId) {
		PreparedStatement pst = null;
		ResultSet rst = null;
		String certificateName = null;
		try {
				pst = con.prepareStatement("select certificate_name from certificate_details where certificate_details_id = ?");
				pst.setInt(1, uF.parseToInt(certificateId));
				rst = pst.executeQuery();
				while (rst.next()) {
					certificateName = rst.getString("certificate_name");
				}
				rst.close();
				pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
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
		return certificateName;
	}
	
	
	private void updateData(String certiID, UtilityFunctions uF) {

		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rst = null;
		boolean certiStatusFlag = false;
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select * from learning_plan_details where certificate_id = ?");
			pst.setInt(1, uF.parseToInt(certiID));
			rst = pst.executeQuery();
//			System.out.println("pst ===> " + pst);
			while (rst.next()) {
				certiStatusFlag = true;
			}
			rst.close();
			pst.close();
			if(certiStatusFlag == true && (getAddCertificateSubmit() != null)){
				String certiName = getCertificateNameById(con, uF, certiID);
				
				createNewVersionOfCertificate(con, uF, certiID);
				updateNewVersionCertificate(con, uF, certiID);
				
				String newCertiName = getCertificateNameById(con, uF, getCertiId());
				session.setAttribute(MESSAGE, SUCCESSM+""+newCertiName+" certificate has been created new version of "+certiName+" certificate successfully."+END);
			} else {
				if (getAddCertificateSubmit() != null){
//					System.out.println("courseID ===> " + courseID);
					updateCertificate(con, uF, certiID);
				}
			
				String certiName = getCertificateNameById(con, uF, certiID);
				session.setAttribute(MESSAGE, SUCCESSM+""+certiName+" certificate has been updated successfully."+END);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}

	
	
	private void updateCertificate(Connection con, UtilityFunctions uF, String certiID) {

		PreparedStatement pst = null;
		
		try {
			pst=con.prepareStatement("update certificate_details set certificate_name=?, certificate_title=?, certificate_logo_align=?, " +
				"certificate_border=?, certificate_first_line=?, certificate_second_line=?, certificate_third_line=?, certificate_font_size=?, " +
				"certificate_sign_one=?, certificate_sign_two=?, certificate_sign_three=?, updated_by=?, update_date=? where certificate_details_id=? ");
			pst.setString(1, getCertificateName());
			pst.setString(2, getCertificateTitle());
			pst.setInt(3, uF.parseToInt(getCertiLogoAlign()));
			pst.setInt(4, uF.parseToInt(getCertificateBorder()));
			pst.setInt(5, uF.parseToInt(getFirstLine()));
			pst.setInt(6, uF.parseToInt(getSecondLine()));
			pst.setInt(7, uF.parseToInt(getThirdLine()));
			pst.setInt(8, uF.parseToInt(getFontSize()));
			pst.setInt(9, uF.parseToInt(getSignOne()));
			pst.setInt(10, uF.parseToInt(getSignTwo()));
			pst.setInt(11, uF.parseToInt(getSignThree()));
			pst.setInt(12, uF.parseToInt(strSessionEmpId));
			pst.setDate(13, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(14, uF.parseToInt(getCertiId()));
//			System.out.println("printing pst before execute=="+pst);
			pst.execute();
			pst.close();
			
//			String certificateName = getCertificateNameById(con, uF, getCertiId());
//			session.setAttribute(MESSAGE, SUCCESSM+""+certificateName+" certificate has been updated successfully."+END);
			
		}catch(Exception e){
			e.printStackTrace();
		} finally {
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
//		return "success";
	}
	
	
	private void updateNewVersionCertificate(Connection con, UtilityFunctions uF, String certiID) {

		PreparedStatement pst = null;
		
		try {
			
			pst=con.prepareStatement("update certificate_details set certificate_name=?, certificate_title=?, certificate_logo_align=?, " +
				"certificate_border=?, certificate_first_line=?, certificate_second_line=?, certificate_third_line=?, certificate_font_size=?, " +
				"certificate_sign_one=?, certificate_sign_two=?, certificate_sign_three=?, updated_by=?, update_date=?, ref_certificate_id=0 " +
				" where ref_certificate_id=? ");
			pst.setString(1, getCertificateName());
			pst.setString(2, getCertificateTitle());
			pst.setInt(3, uF.parseToInt(getCertiLogoAlign()));
			pst.setInt(4, uF.parseToInt(getCertificateBorder()));
			pst.setInt(5, uF.parseToInt(getFirstLine()));
			pst.setInt(6, uF.parseToInt(getSecondLine()));
			pst.setInt(7, uF.parseToInt(getThirdLine()));
			pst.setInt(8, uF.parseToInt(getFontSize()));
			pst.setInt(9, uF.parseToInt(getSignOne()));
			pst.setInt(10, uF.parseToInt(getSignTwo()));
			pst.setInt(11, uF.parseToInt(getSignThree()));
			pst.setInt(12, uF.parseToInt(strSessionEmpId));
			pst.setDate(13, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(14, uF.parseToInt(certiID));
//			System.out.println("printing pst before execute=="+pst);
			pst.execute();
			pst.close();
			
//			String certificateName = getCertificateNameById(con, uF, getCertiId());
//			session.setAttribute(MESSAGE, SUCCESSM+""+certificateName+" certificate has been updated successfully."+END);
			
		}catch(Exception e){
			e.printStackTrace();
		} finally {
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
//		return "success";
	}
	
	
	private void createNewVersionOfCertificate(Connection con, UtilityFunctions uF, String certiID) {

//		System.out.println("createNewVersionOfCourse courseID ===> " + courseID);
		PreparedStatement pst = null, pst1 = null;
		ResultSet rst = null;

		try {
			
			pst = con.prepareStatement("select * from certificate_details where certificate_details_id = ?");
			pst.setInt(1, uF.parseToInt(certiID));
			rst = pst.executeQuery();
			while (rst.next()) {
				
				pst1=con.prepareStatement("insert into certificate_details (certificate_name,certificate_title,certificate_logo_align,certificate_border," +
						"certificate_first_line,certificate_second_line,certificate_third_line,certificate_font_size,certificate_sign_one," +
						"certificate_sign_two,certificate_sign_three,added_by,entry_date,parent_certificate_id,ref_certificate_id,root_certificate_id" +
						") values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?)");
				pst1.setString(1, rst.getString("certificate_name"));
				pst1.setString(2, rst.getString("certificate_title"));
				pst1.setInt(3, uF.parseToInt(rst.getString("certificate_logo_align")));
				pst1.setInt(4, uF.parseToInt(rst.getString("certificate_border")));
				pst1.setInt(5, uF.parseToInt(rst.getString("certificate_first_line")));
				pst1.setInt(6, uF.parseToInt(rst.getString("certificate_second_line")));
				pst1.setInt(7, uF.parseToInt(rst.getString("certificate_third_line")));
				pst1.setInt(8, uF.parseToInt(rst.getString("certificate_font_size")));
				pst1.setInt(9, uF.parseToInt(rst.getString("certificate_sign_one")));
				pst1.setInt(10, uF.parseToInt(rst.getString("certificate_sign_two")));
				pst1.setInt(11, uF.parseToInt(rst.getString("certificate_sign_three")));
				pst1.setInt(12, uF.parseToInt(strSessionEmpId));
				pst1.setDate(13, uF.getCurrentDate(CF.getStrTimeZone()));
				pst1.setInt(14, uF.parseToInt(certiID));
				pst1.setInt(15, uF.parseToInt(certiID));
				if(rst.getString("root_certificate_id") != null) {
					pst1.setInt(16, uF.parseToInt(rst.getString("root_certificate_id")));
				} else {
					pst1.setInt(16, uF.parseToInt(certiID));
				}
				pst1.execute();
				pst1.close();
				
			}
			rst.close();
			pst.close();
			
			if(getAssignToExist() != null && getAssignToExist().equals("Yes")) {
				
				pst = con.prepareStatement("update learning_plan_details set certificate_id = ? where certificate_id = ?");
				pst.setInt(1, uF.parseToInt(getCertiId()));
				pst.setInt(2, uF.parseToInt(certiID));
				pst.executeUpdate();
				pst.close();
//				System.out.println("pstt7 =====> " + pstt7);
				
				getCertificateNameById(con, uF, getCertiId());
			}
			
//			System.out.println("getAssignToExist() ===> " + getAssignToExist());
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
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
			if(pst1 !=null){
				try {
					pst1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	

	private String deleteCertificate(String certiId) {

		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		Connection con = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			String certificateName = getCertificateNameById(con, uF, certiId);
			pst=con.prepareStatement("delete from certificate_details where certificate_details_id =?");
			pst.setInt(1, uF.parseToInt(certiId));
			int x = pst.executeUpdate();
			pst.close();
			if(x > 0){
				session.setAttribute(MESSAGE, SUCCESSM+""+certificateName+" certificate has been deleted successfully."+END);
			} else {
				session.setAttribute(MESSAGE, ERRORM+""+certificateName+" certificate not deleted."+END);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return "success";
	}
	
	private String viewCertificate(String certiId) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection con = null;
		UtilityFunctions uF = new UtilityFunctions();
		Database db = new Database();
		db.setRequest(request);
		try {
			 
			con = db.makeConnection(con);
			
			pst=con.prepareStatement("select * from certificate_details where certificate_details_id =?");
			pst.setInt(1, uF.parseToInt(certiId));
			rs = pst.executeQuery();
			while(rs.next()){
				setCertiId(rs.getString("certificate_details_id"));
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
			
			String strFirstLine = CF.getCertificateFirstLine(con, getFirstLine());
			String strSecondLine = CF.getCertificateSecondLine(con, getSecondLine());
			String strThirdLine = CF.getCertificateThirdLine(con, getThirdLine());
			
			request.setAttribute("strFirstLine", strFirstLine);
			request.setAttribute("strSecondLine", strSecondLine);
			request.setAttribute("strThirdLine", strThirdLine);
			setOperation("U");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return "success";
	}
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
	}
	
	public String getAddCertificateSubmit() {
		return addCertificateSubmit;
	}
	
	public void setAddCertificateSubmit(String addCertificateSubmit) {
		this.addCertificateSubmit = addCertificateSubmit;
	}
	
	public String getCertiId() {
		return certiId;
	}

	public void setCertiId(String certiId) {
		this.certiId = certiId;
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

	public String getAssignToExist() {
		return assignToExist;
	}

	public void setAssignToExist(String assignToExist) {
		this.assignToExist = assignToExist;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<FillCertificate> getCertificateList() {
		return certificateList;
	}

	public void setCertificateList(List<FillCertificate> certificateList) {
		this.certificateList = certificateList;
	}

	public List<FillCertificate> getBorderList() {
		return borderList;
	}

	public void setBorderList(List<FillCertificate> borderList) {
		this.borderList = borderList;
	}

	public List<FillCertificate> getFirstLineList() {
		return firstLineList;
	}

	public void setFirstLineList(List<FillCertificate> firstLineList) {
		this.firstLineList = firstLineList;
	}

	public List<FillCertificate> getSecondLineList() {
		return secondLineList;
	}

	public void setSecondLineList(List<FillCertificate> secondLineList) {
		this.secondLineList = secondLineList;
	}

	public List<FillCertificate> getThirdLineList() {
		return thirdLineList;
	}

	public void setThirdLineList(List<FillCertificate> thirdLineList) {
		this.thirdLineList = thirdLineList;
	}

	public List<FillCertificate> getFirstFontList() {
		return firstFontList;
	}

	public void setFirstFontList(List<FillCertificate> firstFontList) {
		this.firstFontList = firstFontList;
	}

	public List<FillCertificate> getCertiLogoAlignList() {
		return certiLogoAlignList;
	}

	public void setCertiLogoAlignList(List<FillCertificate> certiLogoAlignList) {
		this.certiLogoAlignList = certiLogoAlignList;
	}

	
}
