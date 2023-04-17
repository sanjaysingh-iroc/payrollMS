package com.konnect.jpms.reports.master;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class PerformanceDashboard extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;

	String strSessionEmpId = null;
	
	String dataType;
	String reviewId;
	String operation;
	
	public String execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);

		request.setAttribute(TITLE, "Reviews");
		request.setAttribute(PAGE, "/jsp/reports/master/PerformanceDashboard.jsp");
		UtilityFunctions uF = new UtilityFunctions();
		
//		unPublishOldReview();
		if(getOperation() != null && getOperation().equals("D")) {
			deletePerformance(uF);
		}
		getAppraisalReport(uF);
		return LOAD;
	}

	
private void deletePerformance(UtilityFunctions uF) {
	Connection con=null;
	Database db=new Database();
	db.setRequest(request);
	PreparedStatement pst=null;
	try {	
		
		con=db.makeConnection(con);
		pst = con.prepareStatement("delete from performance_details where performance_id=?");
		pst.setInt(1, uF.parseToInt(getReviewId()));
		pst.executeUpdate();
		pst.close();
		
		pst = con.prepareStatement("delete from performance_details_empwise where performance_id=?");
		pst.setInt(1, uF.parseToInt(getReviewId()));
		pst.executeUpdate();
		pst.close();
		
	} catch (Exception e){
			e.printStackTrace();
	} finally {
		db.closeStatements(pst);
		db.closeConnection(con);
	}

}


//	private void unPublishOldReview() {
//		
//		Connection con=null;
//		Database db=new Database();
//		db.setRequest(request);
//		PreparedStatement pst=null;
//	    try {	
//	    	
//	    	con=db.makeConnection(con);
//			pst = con.prepareStatement("update appraisal_details set is_publish = FALSE, publish_expire_status=1 where to_date < ? and " +
//					"is_publish = TRUE and my_review_status = 0");
//			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
//			pst.executeUpdate();
//			pst.close();
//			
//		} catch (Exception e){
//				e.printStackTrace();
//		} finally {
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//	}


	public void getAppraisalReport(UtilityFunctions uF) {
		
		List<List<String>> allAppraisalreport = new ArrayList<List<String>>();	
		Connection con=null;
		Database db=new Database();
		db.setRequest(request);
		PreparedStatement pst=null;
		ResultSet rst=null;
	    try {	
	    	
	    	con=db.makeConnection(con);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from performance_details where performance_id>0 ");
			if(strUserType != null && strUserType.equals(MANAGER)) {
				sbQuery.append(" and added_by = "+uF.parseToInt(strSessionEmpId)+" ");
			}
			sbQuery.append(" order by performance_name");
			pst=con.prepareStatement(sbQuery.toString());
			rst=pst.executeQuery();
			int count=0;
			while(rst.next()) {
				count++;
				List<String> appraisal_info =new ArrayList<String>(); 
				
				List<String> empList =new ArrayList<String>();
				empList = getAppendData(con, rst.getString("emp_ids"));
				appraisal_info.add(rst.getString("performance_id"));
				//System.out.println("empList ===> "+empList );
				appraisal_info.add(rst.getString("performance_name"));
				appraisal_info.add(rst.getString("performance_file_name"));
				appraisal_info.add(uF.getDateFormat(rst.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				appraisal_info.add(uF.showData(empList != null && !empList.isEmpty() && empList.size() > 0 ? empList.get(0).toString() : "", ""));
				appraisal_info.add(empList != null && !empList.isEmpty() && empList.size() > 1 ? empList.get(1) : "0");
				String uploadEmpFile = getUploadEmpFile(con, uF, rst.getString("performance_id"));
				
				appraisal_info.add(uploadEmpFile);
				
				allAppraisalreport.add(appraisal_info);
			}
			rst.close();
			pst.close();
		} catch (Exception e){
				e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		request.setAttribute("allAppraisalreport",allAppraisalreport);
	}

	
	
	private String getUploadEmpFile(Connection con, UtilityFunctions uF, String reviewId) {
		
		PreparedStatement pst=null;
		ResultSet rst=null;
		String fileUpload = "";
	    try {	
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from performance_details_empwise where performance_id =? and emp_performance_file_name is not null");
			pst=con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(reviewId));
			rst=pst.executeQuery();
			int count=0;
			while(rst.next()) {
				count++;
			}
			rst.close();
			pst.close();
			fileUpload = count+"";
		} catch (Exception e){
				e.printStackTrace();
		}
		return fileUpload;
	}
	


	private List<String> getAppendData(Connection con, String strID) {
		StringBuilder sb = new StringBuilder();
		List<String> empList = new ArrayList<String>();
//		EncryptionUtils encryption = new EncryptionUtils();// Created By Dattatray Date : 20-July-2021 Note : Encryption
		if (strID != null && !strID.equals("")) {
			int flag = 0, empcnt = 0;
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);

			String[] temp = strID.split(",");
			empcnt = temp.length - 1;
			for (int i = 0; i < temp.length; i++) {

				if (temp[i] != null && !temp[i].equals("")) {
					if (flag == 0) {
						sb.append("<a href=\"MyProfile.action?empId=" + temp[i] + "\">" + hmEmpName.get(temp[i].trim()) + "</a>");// Created By Dattatray Date : 20-July-2021 Note : empId Encryption
					} else {
						sb.append(", " + "<a href=\"MyProfile.action?empId=" + temp[i] + "\">" + hmEmpName.get(temp[i].trim()) + "</a>");// Created By Dattatray Date : 20-July-2021 Note : empId Encryption
					}
					flag = 1;
				}
			}
			empList.add(sb.toString());
			empList.add(empcnt + "");
			// System.out.println("empList ========== >>>> "+empList.toString());
		}
		return empList;
	}
	
	
	String type;
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getReviewId() {
		return reviewId;
	}

	public void setReviewId(String reviewId) {
		this.reviewId = reviewId;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

}
