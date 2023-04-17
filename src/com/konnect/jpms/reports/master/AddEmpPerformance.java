package com.konnect.jpms.reports.master;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UploadImage;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddEmpPerformance extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = -2009608472005407630L;

	HttpSession session;
	private HttpServletRequest request; 
	String strUserType = null;
	String strSessionEmpId = null;

	private CommonFunctions CF;
	
	String reviewId;
	String reviewName;
	String fromPage;
	String operation;
	File reviewFile;
	String reviewFileFileName;
	String strEmployee;
	
	List<FillEmployee> empList;
	
	public String execute() {

		session = request.getSession();
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
	
		UtilityFunctions uF = new UtilityFunctions();
//		System.out.println("getFromPage() ===>> " + getFromPage());
//		System.out.println("getOperation() ===>> " + getOperation());
		empList = new FillEmployee(request).fillEmployeeNameCode(null, null);
		
		getPerformanceName(uF);
		
		if(getOperation() != null && getOperation().equals("Add")) {
			addNewPerformance(uF);
			return SUCCESS;
		}
		return LOAD;
	}
	
	
	private void getPerformanceName(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);

			pst = con.prepareStatement("select performance_name from performance_details where performance_id =?");
			pst.setInt(1, uF.parseToInt(getReviewId()));
			rs = pst.executeQuery();
			while(rs.next()) {
				setReviewName(rs.getString("performance_name"));
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


	public void addNewPerformance(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);

			
				pst = con.prepareStatement("update performance_details_empwise set upload_date = ? where performance_id=? and emp_id=?");
				pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(2, uF.parseToInt(getReviewId()));
				pst.setInt(3, uF.parseToInt(strSessionEmpId));
				pst.execute();
				pst.close();
			
			uploadImage(uF.parseToInt(getReviewId()));

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private void uploadImage(int performanceId) {
		
		try {
			
			UploadImage uI = new UploadImage();
			uI.setServletRequest(request);
			uI.setImageType("EMP_PERFORMANCE_FILE");
			uI.setEmpImage(getReviewFile());
			uI.setEmpImageFileName(getReviewFileFileName());
			uI.setPerformanceID(performanceId+"");
			uI.setEmpId(strSessionEmpId);
			uI.setCF(CF);
			uI.upoadImage();
			
		}catch (Exception e) {
			e.printStackTrace();
			
		}
		
	}

	public String getFromPage() {
		return fromPage;
	}

	public String getReviewName() {
		return reviewName;
	}

	public void setReviewName(String reviewName) {
		this.reviewName = reviewName;
	}

	public File getReviewFile() {
		return reviewFile;
	}

	public void setReviewFile(File reviewFile) {
		this.reviewFile = reviewFile;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getReviewFileFileName() {
		return reviewFileFileName;
	}

	public void setReviewFileFileName(String reviewFileFileName) {
		this.reviewFileFileName = reviewFileFileName;
	}

	public List<FillEmployee> getEmpList() {
		return empList;
	}

	public void setEmpList(List<FillEmployee> empList) {
		this.empList = empList;
	}

	public String getStrEmployee() {
		return strEmployee;
	}

	public void setStrEmployee(String strEmployee) {
		this.strEmployee = strEmployee;
	}

	public String getReviewId() {
		return reviewId;
	}

	public void setReviewId(String reviewId) {
		this.reviewId = reviewId;
	}


	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	

}
