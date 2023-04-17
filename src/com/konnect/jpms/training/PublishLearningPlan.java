package com.konnect.jpms.training;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.SessionAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;

public class PublishLearningPlan  implements ServletRequestAware, SessionAware,
		IStatements, Runnable {
	Map session;
	CommonFunctions CF;
	HttpServletRequest request;

	String strUserType = null;
	String strSessionEmpId = null;

	private String id;
	private String dcount;

	public String execute() { 
		strUserType = (String) session.get(USERTYPE);
		strSessionEmpId = (String) session.get(EMPID);

		CF = (CommonFunctions) session.get(CommonFunctions);
		if (CF == null)
			return "login";
		
		setLearningPublishStatus();
		return "success";
	}

	private void setLearningPublishStatus() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		boolean is_publish = false;
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select is_publish from learning_plan_details where learning_plan_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
//			System.out.println("pst1==>"+pst);
			rst = pst.executeQuery();
			while (rst.next()) {
				is_publish=rst.getBoolean(1);
			}
			rst.close();
			pst.close();
			
			String query="";
			if(is_publish==true) {
				query ="update learning_plan_details set is_publish=false where learning_plan_id=?";
				is_publish = false;
			} else {
				query ="update learning_plan_details set is_publish=true where learning_plan_id=?";
				is_publish = true;
			}
			pst = con.prepareStatement(query);
			pst.setInt(1, uF.parseToInt(getId()));
//			System.out.println("pst2==>"+pst);
			pst.execute();
			pst.close();
			
			
			request.setAttribute("is_publish", is_publish);

			if(is_publish == false) {
				pst = con.prepareStatement("select learning_plan_name,learner_ids from learning_plan_details where learning_plan_id = ?");
				pst.setInt(1, uF.parseToInt(getId()));
//				System.out.println("pst3==>"+pst);
				rst = pst.executeQuery();
				String learnersId = "";
				String learningPlanName = "";
				while (rst.next()) {
					learnersId = rst.getString("learner_ids");
					learningPlanName = rst.getString("learning_plan_name");
				}
				rst.close();
				pst.close();
				
				List<String> selectEmpList = Arrays.asList(learnersId.split(","));
				Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
				
				for(int i=0; selectEmpList!= null && !selectEmpList.isEmpty() && i<selectEmpList.size(); i++) {
					if(!selectEmpList.get(i).equals("") && uF.parseToInt(selectEmpList.get(i)) > 0) {
						String strDomain = request.getServerName().split("\\.")[0];
						String alertData = "<div style=\"float: left;\"> A new Learning ("+learningPlanName+") has been aligned with you by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. </div>";
						String alertAction = "MyHR.action?callFrom=LPDash&pType=WR";
						
						UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
						userAlerts.setStrDomain(strDomain);
						userAlerts.setStrEmpId(selectEmpList.get(i));
						userAlerts.setStrData(alertData);
						userAlerts.setStrAction(alertAction);
						userAlerts.setCurrUserTypeID(hmUserTypeId.get(EMPLOYEE));
						userAlerts.setStatus(INSERT_WR_ALERT);
						Thread t = new Thread(userAlerts);
						t.run();

					}
				}
				
				sendMail(con, getId());
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	public void sendMail(Connection con, String plan_idNew) {

		ResultSet rst = null;
		PreparedStatement pst = null;
		UtilityFunctions uF = new UtilityFunctions();

		try {
			Map<String, Map<String, String>> hmEmpInfo = CF.getEmpInfoMap(con, false);
//			System.out.println("Req id is ========= "+getStrId());
			String lPlanName = "";
			String learnersId = "";
			pst = con.prepareStatement("select learning_plan_name,learner_ids from learning_plan_details where learning_plan_id = ?");
			pst.setInt(1, uF.parseToInt(plan_idNew));
			rst = pst.executeQuery();
			while (rst.next()) {
				lPlanName = rst.getString("learning_plan_name");
				learnersId = rst.getString("learner_ids");
			}
			rst.close();
			pst.close();
			
			List<String> selectEmpList = Arrays.asList(learnersId.split(","));
				
			List<String> startAndEndDate = getStartAndEndDate(con, uF, uF.parseToInt(plan_idNew));
//			System.out.println("panel_employee_id ===> "+panel_employee_id);
			if(lPlanName != null && !lPlanName.equals("")) {
				StringBuilder sbLearnersName = new StringBuilder();
				for(int i=0; selectEmpList!= null && i<selectEmpList.size(); i++){
					if(selectEmpList.get(i) != null && !selectEmpList.get(i).equals("")) {
//						System.out.println("hmEmpInfo ===>> " + hmEmpInfo);
						Map<String, String> hmEmpInner = hmEmpInfo.get(selectEmpList.get(i));
						if(hmEmpInner== null) hmEmpInner = new HashMap<String,String>();
//						System.out.println("hmEmpInner ===>> " + hmEmpInner);
						if(i==0) {
							sbLearnersName.append(hmEmpInner.get("FNAME")+" " +hmEmpInner.get("LNAME"));
						} else {
							sbLearnersName.append(", "+ uF.showData(hmEmpInner.get("FNAME"),"")+" " +uF.showData(hmEmpInner.get("LNAME"),""));
						}
						if(selectEmpList.get(i) != null && !selectEmpList.get(i).equals("")){
							String strDomain = request.getServerName().split("\\.")[0];	
							Notifications nF = new Notifications(N_NEW_LEARNING_PLAN_FOR_LEARNERS, CF);
				//			System.out.println("Emp ID is ========= "+rst1.getString("panel_emp_id"));
							 nF.setDomain(strDomain);
							 nF.request = request;
							 nF.setStrEmpId(selectEmpList.get(i));
							 nF.setStrHostAddress(CF.getStrEmailLocalHost());
							 nF.setStrHostPort(CF.getStrHostPort());
							 nF.setStrContextPath(request.getContextPath());
							 nF.setStrLearningPlanName(lPlanName);
							 nF.setStrLearningPlanStartdate(startAndEndDate.get(0));
							 nF.setStrLearningPlanEnddate(startAndEndDate.get(1));
							 nF.setStrEmpFname(hmEmpInner.get("FNAME"));
							 nF.setStrEmpLname(hmEmpInner.get("LNAME"));
							 nF.setEmailTemplate(true);
							 nF.sendNotifications();
						}
					}
				}
				
				Map<String, String> hmEmpInner = hmEmpInfo.get(strSessionEmpId);
				String strDomain = request.getServerName().split("\\.")[0];	
				Notifications nF = new Notifications(N_NEW_LEARNING_PLAN_FOR_HR, CF);
	//			System.out.println("Emp ID is ========= "+rst1.getString("panel_emp_id"));
				 nF.setDomain(strDomain);
				 nF.request = request;
				 nF.setStrEmpId(strSessionEmpId);
				 nF.setStrHostAddress(CF.getStrEmailLocalHost());
				 nF.setStrHostPort(CF.getStrHostPort());
				 nF.setStrContextPath(request.getContextPath());
				 nF.setStrLearningPlanName(lPlanName);
				 nF.setStrLearningPlanStartdate(startAndEndDate.get(0));
				 nF.setStrLearningPlanEnddate(startAndEndDate.get(1));
				 nF.setStrLearnersName(sbLearnersName.toString());
				 
				 nF.setStrEmpFname(hmEmpInner.get("FNAME"));
				 nF.setStrEmpLname(hmEmpInner.get("LNAME"));
				 nF.setEmailTemplate(true);
				 nF.sendNotifications();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private List<String> getStartAndEndDate(Connection con, UtilityFunctions uF, int learningPlanId) {
		
		PreparedStatement pst = null;
		ResultSet rst=null;
		List<String> startAndEndDate = new ArrayList<String>();
//		StringBuilder startEndDate = new StringBuilder();
		try {
			pst = con.prepareStatement("select min(from_date) as minDate, max(to_date) as maxDate from learning_plan_stage_details where learning_plan_id = ?");
			pst.setInt(1, learningPlanId);
			rst = pst.executeQuery();
		//	System.out.println("pst1 =====> " + pst1);
			String minFromDate = null, maxToDate = null; 
			while (rst.next()) {
				minFromDate = rst.getString("minDate");
				maxToDate = rst.getString("maxDate");
			}
			rst.close();
			pst.close();
			
			startAndEndDate.add(uF.getDateFormat(minFromDate, DBDATE, CF.getStrReportDateFormat()));
			startAndEndDate.add(uF.getDateFormat(maxToDate, DBDATE, CF.getStrReportDateFormat()));
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return startAndEndDate;
	}
	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDcount() {
		return dcount;
	}

	public void setDcount(String dcount) {
		this.dcount = dcount;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
	}

	@Override
	public void setSession(Map session) {
		this.session = session;
	}

	String strDomain;
	public void setDomain(String strDomain) {
		this.strDomain=strDomain;
	}

	public String getStrDomain() {
		return strDomain;
	}


	@Override
	public void run() {

	}

}
