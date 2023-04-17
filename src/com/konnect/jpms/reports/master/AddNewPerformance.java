package com.konnect.jpms.reports.master;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
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

public class AddNewPerformance extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = -2009608472005407630L;

	HttpSession session;
	private HttpServletRequest request; 
	String strUserType = null;
	String strSessionEmpId = null;

	private CommonFunctions CF;
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
		
			if(getOperation() != null && getOperation().equals("Add")) {
				addNewPerformance(uF);
				return SUCCESS;
			}
			return LOAD;
	}
	
	
	public void addNewPerformance(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);

			List<String> selectedEmpList = new ArrayList<String>();
			if (getStrEmployee() != null && getStrEmployee().length() > 0) {
				List<String> emp = Arrays.asList(getStrEmployee().split(","));
				for(int i=0;emp!=null && !emp.isEmpty() && i<emp.size();i++){
					selectedEmpList.add(emp.get(i).trim());
				}
//				System.out.println("al.add(emp.get(i).trim()) ==========>" + al.toString());
			} else {
				for(int i=0; empList!=null && !empList.isEmpty() && i<empList.size();i++){
					selectedEmpList.add(empList.get(i).getEmployeeId());
				}
			}
			
			StringBuilder sbEmpIds = new StringBuilder();
			for (int i = 0; i < selectedEmpList.size(); i++) {
				if (i == 0) {
					sbEmpIds.append("," + selectedEmpList.get(i).trim()+",");
				} else {
					sbEmpIds.append(selectedEmpList.get(i).trim()+",");
				}
			}
			
			pst = con.prepareStatement("insert into performance_details (performance_name,emp_ids,added_by,entry_date) values(?,?,?,?)");
			pst.setString(1, getReviewName());
			pst.setString(2, sbEmpIds.toString());
			pst.setInt(3, uF.parseToInt(strSessionEmpId));
			pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.execute();
			pst.close();
			
			int performanceId = 0;
			pst = con.prepareStatement("select max(performance_id) from performance_details");
			rs = pst.executeQuery();
			while(rs.next()) {
				performanceId = rs.getInt(1);
			}
			rs.close();
			pst.close();
			
			for (int i = 0; i < selectedEmpList.size(); i++) {
				pst = con.prepareStatement("insert into performance_details_empwise (performance_id,emp_id,added_by,entry_date) values(?,?,?,?)");
				pst.setInt(1, performanceId);
				pst.setInt(2, uF.parseToInt(selectedEmpList.get(i)));
				pst.setInt(3, uF.parseToInt(strSessionEmpId));
				pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.execute();
				pst.close();
			}
			
			uploadImage(performanceId);

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
			uI.setImageType("PERFORMANCE_SAMPLE");
			uI.setEmpImage(getReviewFile());
			uI.setEmpImageFileName(getReviewFileFileName());
			uI.setPerformanceID(performanceId+"");
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


	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	

}
