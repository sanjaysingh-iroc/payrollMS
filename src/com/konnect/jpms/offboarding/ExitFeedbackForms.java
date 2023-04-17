package com.konnect.jpms.offboarding;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
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

public class ExitFeedbackForms extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;

	String strSessionEmpId = null;
	UtilityFunctions uF = new UtilityFunctions();

	String alertStatus;
	String alert_type;
	
	String dataType;
	
	public String execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);

		request.setAttribute(TITLE, "Exit Feedback Dashboard");
		request.setAttribute(PAGE, "/jsp/offboarding/ExitFeedbackForms.jsp");

		getAppraisalReport();

		/*if(strUserType != null && strUserType.equals(HRMANAGER) && getAlertStatus()!=null && getAlert_type()!=null && getAlert_type().equals(NEW_REVIEW_ALERT)){
			updateUserAlerts();
		}
		
		if(strUserType != null && strUserType.equals(HRMANAGER) && getAlertStatus()!=null && getAlert_type()!=null && getAlert_type().equals(REVIEW_FINALIZATION_ALERT)){
			updateUserAlerts1();
		}*/
		 
		return SUCCESS;
	}

	
//	private void updateUserAlerts1() {
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//		UtilityFunctions uF = new UtilityFunctions();
//		int nEmpId = uF.parseToInt((String)session.getAttribute(EMPID));
//		try {
//			con = db.makeConnection(con);
//			
//			String strDomain = request.getServerName().split("\\.")[0];
//			UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//			userAlerts.setStrDomain(strDomain);
//			userAlerts.setStrEmpId(""+nEmpId);
//			userAlerts.set_type(REVIEW_FINALIZATION_ALERT);
//			userAlerts.setStatus(UPDATE_ALERT);
//			//Thread t = new Thread(userAlerts);
//			//t.run();
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//	}
	
	
//	private void updateUserAlerts() {
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//		UtilityFunctions uF = new UtilityFunctions();
//		int nEmpId = uF.parseToInt((String)session.getAttribute(EMPID));
//		try {
//			con = db.makeConnection(con);
//			String strDomain = request.getServerName().split("\\.")[0];
//			UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//			userAlerts.setStrDomain(strDomain);
//			userAlerts.setStrEmpId(""+nEmpId);
//			userAlerts.set_type(NEW_REVIEW_ALERT);
//			userAlerts.setStatus(UPDATE_ALERT);
//			//Thread t = new Thread(userAlerts);
//			//t.run();
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//	}
	
	
	public void getAppraisalReport(){
		
		List<List<String>> allExitFeedbackForms = new ArrayList<List<String>>();	
		Connection con=null;
		Database db=new Database();
		db.setRequest(request);
		PreparedStatement pst=null;
		ResultSet rst=null;
	    try {	
	    	
	    	con=db.makeConnection(con);
	    	Map<String,String> orientationMp= CF.getOrientationValue(con);
	    	Map<String, String> hmFrequency = new HashMap<String, String>();
	    	pst = con.prepareStatement("select * from appraisal_frequency");
			rst = pst.executeQuery();
			while (rst.next()) {
				hmFrequency.put(rst.getString("appraisal_frequency_id"), rst.getString("frequency_name"));
			}
			rst.close();
			pst.close();
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from appraisal_details where form_type = ? ");
			/*if(strUserType != null && strUserType.equals(MANAGER)) {
				sbQuery.append(" and supervisor_id like '%,"+strSessionEmpId+",%' and (is_publish = true or publish_expire_status = true) ");
			}
			
			if(getDataType() != null && getDataType().equals("L")) {
				sbQuery.append(" and is_close = false ");
			} else if(getDataType() != null && getDataType().equals("C")) {
				sbQuery.append(" and is_close = true ");
			}*/
//			sbQuery.append(" order by is_close, is_publish desc, publish_expire_status desc, to_date");
			pst=con.prepareStatement(sbQuery.toString());
			pst.setInt(1, EXIT_FEEDBACK_FORM);
			rst=pst.executeQuery();
			int count=0;
			StringBuilder sbIcons = new StringBuilder();
			while(rst.next()) {
				count++;
				sbIcons.replace(0, sbIcons.length(), "");
				
				List<String> exitFeedbackFormData =new ArrayList<String>(); 
				
				exitFeedbackFormData.add(rst.getString("appraisal_details_id"));
				//System.out.println("empList ===> "+empList );
				if(uF.parseToBoolean(rst.getString("is_publish"))) {
					/*exitFeedbackFormData.add("<img src=\"images1/icons/pullout.png\"/></td><td> <a href=\"javascript: void(0)\" onclick=\"openAppraisalPreview("+rst.getString("appraisal_details_id")+")\">"+rst.getString("appraisal_name")+"</a>");*/
					exitFeedbackFormData.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#ea9900\"></i></td><td> <a href=\"javascript: void(0)\" onclick=\"openAppraisalPreview("+rst.getString("appraisal_details_id")+")\">"+rst.getString("appraisal_name")+"</a>");
					
				} else {
					 /*exitFeedbackFormData.add("<img src=\"images1/icons/pending.png\"/></td><td> <a href=\"javascript: void(0)\" onclick=\"openAppraisalPreview("+rst.getString("appraisal_details_id")+")\">"+rst.getString("appraisal_name")+"</a>");*/
					exitFeedbackFormData.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\"></i></td><td> <a href=\"javascript: void(0)\" onclick=\"openAppraisalPreview("+rst.getString("appraisal_details_id")+")\">"+rst.getString("appraisal_name")+"</a>");
				}
				
				exitFeedbackFormData.add(rst.getString("appraisal_type"));
				exitFeedbackFormData.add(orientationMp.get(rst.getString("oriented_type"))+"&deg;");
				exitFeedbackFormData.add(uF.showData(hmFrequency.get(rst.getString("frequency")), ""));
				exitFeedbackFormData.add(uF.getDateFormat(rst.getString("to_date"), DBDATE, CF.getStrReportDateFormat()));
				
				sbIcons.append("<a href=\"javascript: void(0)\" onclick=\"openAppraisalPreview("+rst.getString("appraisal_details_id")+")\" title=\"Preview\"><i class=\"fa fa-eye\" aria-hidden=\"true\"></i></a>");
				
				if(strUserType != null && !strUserType.equals(MANAGER)) {
					if(uF.parseToBoolean(rst.getString("is_publish"))) {
						sbIcons.append("<div id=\"myDivM"+count+"\"  style=\"float:left\"><a href=\"javascript:void(0)\" onclick=\"if(confirm('Are you sure, you want to unpublish this form?'))"+
							"getContent('myDivM"+count+"','PublishAppraisal.action?id="+rst.getString("appraisal_details_id")+"&dcount="+count+"&from=EFF');\" >"+
							"<img src=\"images1/icons/icons/unpublish_icon_b.png\" title=\"Published\" /></a></div>");	
					} else {
						sbIcons.append("<div id=\"myDivM"+count+"\" style=\"float:left\"><a href=\"javascript:void(0)\" onclick=\"if(confirm('Are you sure, you want to publish this form?'))"+
							"getContent('myDivM"+count+"','PublishAppraisal.action?id="+rst.getString("appraisal_details_id")+"&dcount="+count+"&from=EFF');\" >"+
							"<img src=\"images1/icons/icons/publish_icon_b.png\" title=\"Waiting to be publish\" /></a></div>");
					}
				}
				exitFeedbackFormData.add(sbIcons.toString());
				
				allExitFeedbackForms.add(exitFeedbackFormData);
			}
			rst.close();
			pst.close();
		} catch (Exception e){
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		request.setAttribute("allExitFeedbackForms",allExitFeedbackForms);
	}

	
	
	String type;
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getAlertStatus() {
		return alertStatus;
	}

	public void setAlertStatus(String alertStatus) {
		this.alertStatus = alertStatus;
	}

	public String getAlert_type() {
		return alert_type;
	}

	public void setAlert_type(String alert_type) {
		this.alert_type = alert_type;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}


	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

}
