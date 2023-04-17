package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AppraisalDashboard extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strUserTypeId = null;
	String strBaseUserType = null;
	String strBaseUserTypeId = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;

	String strSessionEmpId = null;
	
	private String alertStatus;
	private String alert_type;
	
	private String dataType;
	private String fromPage;
	private String strSearchJob;
	
	private String proPage;
	private String minLimit;
	private String currUserType;
	
	private String alertID;
	
	private String callFrom;
	private String type;
	
	private String reviewId;
	private String appFreqId;
		
	public String execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		strUserType = (String) session.getAttribute(USERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		strBaseUserTypeId = (String) session.getAttribute(BASEUSERTYPEID);
		
		strSessionEmpId = (String) session.getAttribute(EMPID);

		request.setAttribute(TITLE, "Reviews");
		request.setAttribute(PAGE, "/jsp/performance/AppraisalDashboard.jsp");

		UtilityFunctions uF = new UtilityFunctions();
		String strDomain = request.getServerName().split("\\.")[0];
		CF.deleteWRUserAlerts(CF, request, strDomain, getAlertID());
	
		if(getFromPage() == null || getFromPage().equals("") || getFromPage().equalsIgnoreCase("null" )){
			StringBuilder sbpageTitleNaviTrail = new StringBuilder();
			sbpageTitleNaviTrail.append("<li><i class=\"fa fa-line-chart\"></i><a href=\"AppraisalDashboard.action\" style=\"color: #3c8dbc;\">Performance</a></li>"
				+"<li class=\"active\">Reviews</li>");
			request.setAttribute("PAGETITLE_NAVITRAIL", sbpageTitleNaviTrail.toString());
		}
		
		if(getDataType() == null || getDataType().trim().equals("") || getDataType().trim().equalsIgnoreCase("NULL")) {
			setDataType("L");
		}
		if(uF.parseToInt(getProPage()) == 0) {
			setProPage("1");
		}
		
		if(getCurrUserType()==null && strUserType != null && strUserType.equals(MANAGER)) {
			setCurrUserType("MYTEAM");
		}
		
//		System.out.println("getCallFrom() ===>> " + getCallFrom());
		if(getCallFrom() == null || getCallFrom().equals("") || getCallFrom().equalsIgnoreCase("null")) {
			if(strUserType != null && !strUserType.equals(EMPLOYEE)) {
				getSearchAutoCompleteData(uF);
			}
			unPublishOldReview(uF);
			getAppraisalReport(uF);
//			System.out.println("getCallFrom() ===>> " + getCallFrom()+ " -- getFromPage() ===>> " + getFromPage());
			if(getFromPage() == null || getFromPage().equals("") || getFromPage().equalsIgnoreCase("null" )){
				return VIEW;
			} else {
				return LOAD;
			}
		} else {
//			System.out.println("else getFromPage() ===>> " + getFromPage());
			if(getFromPage() == null || getFromPage().equals("") || getFromPage().equalsIgnoreCase("null" )){
				return VIEW;
			} else {
//				System.out.println("else else getFromPage() ===>> " + getFromPage());
				return "requestLoad";
			}
		}
	}
	
	
	private void getSearchAutoCompleteData(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);

			Map<String,String> orientationMp= CF.getOrientationValue(con);
	    	Map<String, String> hmFrequency = new HashMap<String, String>();
			pst = con.prepareStatement("select * from appraisal_frequency");
			rs = pst.executeQuery();
			while (rs.next()) {
				hmFrequency.put(rs.getString("appraisal_frequency_id"), rs.getString("frequency_name"));
			}
			rs.close();
			pst.close();
			
			SortedSet<String> setAppraisalList = new TreeSet<String>();
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from appraisal_details  a, appraisal_details_frequency adf where a.appraisal_details_id = adf.appraisal_id "
					+" and (adf.is_delete is null or adf.is_delete = false ) and my_review_status = 0 ");
			if(strUserType != null && strUserType.equals(MANAGER) && getCurrUserType() != null && !getCurrUserType().equals(strBaseUserType)) {
				sbQuery.append(" and (a.added_by = "+uF.parseToInt(strSessionEmpId)+" or (supervisor_id like '%,"+strSessionEmpId+",%' " +
					" and (is_appraisal_publish = true or freq_publish_expire_status = 1))) ");
			}
			
			sbQuery.append(" order by appraisal_name ");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst search===> "+ pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				setAppraisalList.add(rs.getString("appraisal_name"));
				
			}
			rs.close();
			pst.close();
			
			StringBuilder sbData = null;
			Iterator<String> it = setAppraisalList.iterator();
			while (it.hasNext()) {
				String strData = it.next();
//				System.out.println("strData==>"+strData);
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
//			System.out.println("sbData==>"+sbData.toString());
			request.setAttribute("sbData", sbData.toString());
				
		} catch (Exception e) {
			e.printStackTrace(); 
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	private void unPublishOldReview(UtilityFunctions uF) {
		
		Connection con=null;
		Database db=new Database();
		db.setRequest(request);
		PreparedStatement pst=null;
	    try {	
	    	con=db.makeConnection(con);
		/*	pst = con.prepareStatement("update appraisal_details set is_publish = FALSE, publish_expire_status=1 where to_date < ? and " +
					"is_publish = TRUE and my_review_status = 0");*/
	    	pst = con.prepareStatement("update appraisal_details_frequency set is_appraisal_publish = FALSE , freq_publish_expire_status = 1 where freq_end_date < ? and " +
			"is_appraisal_publish = TRUE and (is_delete = false or is_delete is null)");
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
//			System.out.println("pst unPublish ==>"+pst);
			pst.executeUpdate();
			pst.close();
			
			pst = con.prepareStatement("update appraisal_details set is_publish = FALSE, publish_expire_status=1 where to_date < ? and " +
			"is_publish = TRUE and my_review_status = 0");
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.executeUpdate();
			pst.close();
			
		} catch (Exception e) {
				e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


//	private void updateUserAlerts1(UtilityFunctions uF) {
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
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
//			Thread t = new Thread(userAlerts);
//			t.run();
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//	}
//	
//	
//	private void updateUserAlerts(UtilityFunctions uF, String alertType) {
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//		int nEmpId = uF.parseToInt((String)session.getAttribute(EMPID));
//		try {
//			con = db.makeConnection(con);
//			String strDomain = request.getServerName().split("\\.")[0];
//			UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//			userAlerts.setStrDomain(strDomain);
//			userAlerts.setStrEmpId(""+nEmpId);
//			userAlerts.set_type(alertType);
//			userAlerts.setStatus(UPDATE_ALERT);
//			Thread t = new Thread(userAlerts);
//			t.run();
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeResultSet(rs);
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
	    	con = db.makeConnection(con);
	    	Map<String,String> orientationMp = CF.getOrientationValue(con);
	    	Map<String, String> hmFrequency = new HashMap<String, String>();
	    	Map<String, String> hmAppraisalCount = new HashMap<String, String>();
	    	
	    	List<String> appraisalFinalList = CF.getAppraisalFinalStatus(con);
			List<String> appQueAnsStatusList = CF.getAppraisalQueAnsStatus(con);
			
			if(appraisalFinalList==null) appraisalFinalList = new ArrayList<String>();
			if(appQueAnsStatusList==null) appQueAnsStatusList = new ArrayList<String>();
			
			pst = con.prepareStatement("select * from appraisal_frequency");
			rst = pst.executeQuery();
			while (rst.next()) {
				hmFrequency.put(rst.getString("appraisal_frequency_id"), rst.getString("frequency_name"));
			}
			rst.close();
			pst.close();
			
			List<String> alFrequencyId = null;
			if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("")) {	
				pst = con.prepareStatement("select * from appraisal_frequency where frequency_name is not null " +
						"and frequency_name != '' and upper(frequency_name) like '%"+getStrSearchJob().trim().toUpperCase()+"%'");
				rst = pst.executeQuery();
//				System.out.println("appraisal pst==>"+pst+"\ngetStrSearchJob()==>"+getStrSearchJob());
				while(rst.next()) {
					if(alFrequencyId == null) {
						alFrequencyId = new ArrayList<String>();
						alFrequencyId.add(rst.getString("appraisal_frequency_id"));
					} else {
						alFrequencyId.add(rst.getString("appraisal_frequency_id"));
					}
				}
			}
			
			
			pst = con.prepareStatement("select count(*) as cnt, appraisal_id,appraisal_freq_id from appraisal_final_sattlement group by appraisal_id,appraisal_freq_id");
			rst = pst.executeQuery();
			while (rst.next()) {
				hmAppraisalCount.put(rst.getString("appraisal_id")+"_"+rst.getString("appraisal_freq_id"), rst.getString("cnt"));
			}
			rst.close();
			pst.close();
			
			StringBuilder sbEmpId = null;
			if(strUserType != null && (strUserType.equals(HRMANAGER) || strUserType.equals(RECRUITER) || strUserType.equals(CEO)) && session.getAttribute(WLOCATION_ACCESS) != null && !((String)session.getAttribute(WLOCATION_ACCESS)).equals("") && session.getAttribute(ORG_ACCESS) != null && !((String)session.getAttribute(ORG_ACCESS)).equals("")) {
				pst = con.prepareStatement("select eod.emp_id from employee_official_details eod,employee_personal_details epd where " +
					" epd.emp_per_id=eod.emp_id and is_alive = true and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+") and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+") ");
				rst = pst.executeQuery();
				while (rst.next()) {
					if(sbEmpId == null) {
						sbEmpId = new StringBuilder();
						sbEmpId.append(rst.getString("emp_id"));
					} else {
						sbEmpId.append(","+rst.getString("emp_id"));
					}
				}
				rst.close();
				pst.close();
			}
			if(strUserType != null && strUserType.equals(MANAGER) && getCurrUserType() != null && !getCurrUserType().equals(strBaseUserType)) {
				pst = con.prepareStatement("select reviewee_id from appraisal_reviewee_details where supervisor_ids like '%,"+strSessionEmpId+",%' ");
				rst = pst.executeQuery();
				while (rst.next()) {
					if(sbEmpId == null) {
						sbEmpId = new StringBuilder();
						sbEmpId.append(rst.getString("reviewee_id"));
					} else {
						sbEmpId.append(","+rst.getString("reviewee_id"));
					}
				}
				rst.close();
				pst.close();
			}
			
			String[] str_EmpIds = null;
			if(sbEmpId != null) {
				str_EmpIds = sbEmpId.toString().split(",");
			}
		
			
	    	StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from appraisal_details a,appraisal_details_frequency adf where a.appraisal_details_id = adf.appraisal_id "
					+" and (adf.is_delete is null or adf.is_delete=false) and appraisal_details_id>0 "); //my_review_status = 0 
			if(strUserType != null && strUserType.equals(MANAGER) && getCurrUserType() != null && !getCurrUserType().equals(strBaseUserType)) {
				sbQuery.append(" and (a.added_by = "+uF.parseToInt(strSessionEmpId)+" or a.appraisal_details_id in (select appraisal_id from appraisal_reviewee_details where supervisor_ids like '%,"+strSessionEmpId+",%')) " +
					//" and (is_publish = true or publish_expire_status = 1))) ");
				" and (is_appraisal_publish = true or freq_publish_expire_status = 1) ");
			} else if(str_EmpIds!=null && str_EmpIds.length>0) {
                sbQuery.append(" and (");
                for(int i=0; i<str_EmpIds.length; i++) {
                    sbQuery.append(" self_ids like '%,"+str_EmpIds[i]+",%'");
                    if(i<str_EmpIds.length-1) {
                        sbQuery.append(" OR ");
                    }
                }
                sbQuery.append(") ");
            }
			if(getDataType() != null && getDataType().equals("L")) {
			//	sbQuery.append(" and is_close = false ");
				sbQuery.append(" and is_appraisal_close = false ");
			} else if(getDataType() != null && getDataType().equals("C")) {
//				sbQuery.append(" and is_close = true ");
				sbQuery.append(" and is_appraisal_close = true ");
			}
			if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("")) {
				sbQuery.append(" and (upper(appraisal_name) like '%"+getStrSearchJob().trim().toUpperCase()+"%' or upper(appraisal_type) like '%"+getStrSearchJob().trim().toUpperCase()+"%' ");
				if(alFrequencyId !=null && alFrequencyId.size() > 0) {
					sbQuery.append(" or (");
		            for(int i=0; i<alFrequencyId.size(); i++) {
		               sbQuery.append(" frequency like '%"+alFrequencyId.get(i).trim()+"%'");
		               if(i<alFrequencyId.size()-1) {
		                  sbQuery.append(" OR "); 
		                }
		            }
			        sbQuery.append(") ");
				}
				sbQuery.append(")");
			}
			sbQuery.append(" order by is_appraisal_close, is_appraisal_publish desc,freq_publish_expire_status desc,freq_end_date desc");
			int intOffset = uF.parseToInt(getMinLimit());
//			sbQuery.append(" limit 10 offset "+intOffset+"");
			pst=con.prepareStatement(sbQuery.toString());
//			System.out.println("pst ====>>> " + pst);
			rst=pst.executeQuery();
			int count=0;
			StringBuilder sbIcons = new StringBuilder();
			while(rst.next()) {
				count++;
				sbIcons.replace(0, sbIcons.length(), "");
				
				List<String> appraisal_info =new ArrayList<String>(); 
//				List<String> empList =new ArrayList<String>();
//				empList = getAppendData(con, rst.getString("self_ids"));
				
				appraisal_info.add(rst.getString("appraisal_details_id")+"_"+rst.getString("appraisal_freq_id"));//0
				int totEmpCount = getAppraisalEmpCount(con, uF, rst.getString("appraisal_details_id"),rst.getString("appraisal_freq_id"));
				appraisal_info.add(totEmpCount+"");//1
				int queAnsEmpCount = getAppraisalEmpQueAnsCount(con, uF, rst.getString("appraisal_details_id"),rst.getString("appraisal_freq_id"));
				int finalcount = getAppraisalFinalCount(con, uF, rst.getString("appraisal_details_id"),rst.getString("appraisal_freq_id"));
				
				int pendingCount = totEmpCount - queAnsEmpCount;
				appraisal_info.add(""+pendingCount); //2
				
				int underReviewCount = queAnsEmpCount - finalcount;
				appraisal_info.add(""+underReviewCount);//3
				
				appraisal_info.add(""+finalcount);//4
				allAppraisalreport.add(appraisal_info);
			}
			rst.close();
			pst.close();
		} catch (Exception e) {
				e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		request.setAttribute("allAppraisalreport",allAppraisalreport);
	}

		
	private int getAppraisalEmpCount(Connection con, UtilityFunctions uF, String appraisal_id, String appFreqId) {

		PreparedStatement pst = null;
		ResultSet rst = null;
		int count = 0;
		try {
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select emp_id,supervisor_emp_id from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id " +
				" and is_alive= true and emp_per_id >0 order by supervisor_emp_id");
			pst = con.prepareStatement(sbQuery.toString());
			rst = pst.executeQuery();
//			System.out.println("PST ===>> " + pst);
			Map<String, List<String>> hmHireracyLevelEmpIds = new LinkedHashMap<String, List<String>>();
//			List<String> alHireracyLevels = new ArrayList<String>();
			while(rst.next()) {
				List<String> alInner = hmHireracyLevelEmpIds.get(rst.getString("supervisor_emp_id"));
				if(alInner==null) alInner = new ArrayList<String>();
				if(uF.parseToInt(rst.getString("supervisor_emp_id")) == uF.parseToInt(rst.getString("emp_id"))) {
					continue;
				}
				alInner.add(rst.getString("emp_id"));
				hmHireracyLevelEmpIds.put(rst.getString("supervisor_emp_id"), alInner);
			}
			rst.close();
			pst.close();

			StringBuilder sbClildEmpIds = null;
			List<String> empIDList = new ArrayList<String>();
			sbClildEmpIds = getChildEmpIds(hmHireracyLevelEmpIds, strSessionEmpId, empIDList, sbClildEmpIds);
			
			StringBuilder sbEmpId = null;
			if(strUserType != null && strUserType.equals(MANAGER)) {
				sbQuery = new StringBuilder();
				sbQuery.append("select reviewee_id from appraisal_reviewee_details ard, employee_personal_details epd where epd.emp_per_id=ard.reviewee_id  and appraisal_id=? " +
					"and (supervisor_ids like '%,"+strSessionEmpId+",%' or grand_supervisor_ids like '%,"+strSessionEmpId+",%' or hod_ids like '%,"+strSessionEmpId+",%' ");
				if(sbClildEmpIds!=null && !sbClildEmpIds.toString().equals("")) {
					sbQuery.append(" or epd.emp_per_id in ("+sbClildEmpIds.toString()+") ");
				}
				sbQuery.append(") order by emp_fname");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, uF.parseToInt(appraisal_id));
				rst = pst.executeQuery();
				while (rst.next()) {
					if(sbEmpId == null) {
						sbEmpId = new StringBuilder();
						sbEmpId.append(rst.getString("reviewee_id"));
					} else {
						sbEmpId.append(","+rst.getString("reviewee_id"));
					}
				}
				rst.close();
				pst.close();
			}
			
			sbQuery = new StringBuilder();
			sbQuery.append("select count(distinct reviewee_id) as count from appraisal_reviewee_details where appraisal_id=? and appraisal_freq_id=? ");
			if(sbEmpId!=null) {
				sbQuery.append(" and reviewee_id in ("+sbEmpId.toString()+")");
			}
			pst = con.prepareStatement(sbQuery.toString());
//			pst = con.prepareStatement("select count(distinct reviewee_id) as count from appraisal_reviewee_details where appraisal_id=? and appraisal_freq_id=?");
			pst.setInt(1, uF.parseToInt(appraisal_id));
			pst.setInt(2, uF.parseToInt(appFreqId));
//			System.out.println("pst ===>> " + pst);
			rst = pst.executeQuery();
			while (rst.next()) {
				count = rst.getInt("count");
			}
			rst.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}

	
	public StringBuilder getChildEmpIds(Map<String, List<String>> hmHireracyLevelEmpIds, String empId, List<String> empIDList, StringBuilder sbClildEmpIds) {

		if(empId != null && !empId.trim().equals("")) {
			if(hmHireracyLevelEmpIds == null) hmHireracyLevelEmpIds = new HashMap<String, List<String>>();
			List<String> innerList = (List<String>)hmHireracyLevelEmpIds.get(empId.trim());
//			System.out.println("getChildEmpIds() calling");
			for(int i= 0; innerList != null && !innerList.isEmpty() && i<innerList.size(); i++) {
				String empId1 = innerList.get(i);
				if(empId1 != null && !empId1.trim().equals("") && !empIDList.contains(empId1.trim())) {
					empIDList.add(empId1.trim());
					if(sbClildEmpIds==null) {
						sbClildEmpIds = new StringBuilder();
						sbClildEmpIds.append(empId1.trim());
					} else {
						sbClildEmpIds.append(","+empId1.trim());
					}
				}
				if(empId1 != null && !empId1.trim().equals("")) {
					getChildEmpIds(hmHireracyLevelEmpIds, empId1, empIDList, sbClildEmpIds);
				}
			}
		}
		return sbClildEmpIds;
	}
	

	public int getAppraisalEmpQueAnsCount(Connection con, UtilityFunctions uF, String appraisal_id,String appFreqId) {

		PreparedStatement pst = null;
		ResultSet rst = null;
		int count = 0;
		try {
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select emp_id,supervisor_emp_id from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id " +
				" and is_alive= true and emp_per_id >0 order by supervisor_emp_id");
			pst = con.prepareStatement(sbQuery.toString());
			rst = pst.executeQuery();
//			System.out.println("PST ===>> " + pst);
			Map<String, List<String>> hmHireracyLevelEmpIds = new LinkedHashMap<String, List<String>>();
//			List<String> alHireracyLevels = new ArrayList<String>();
			while(rst.next()) {
				List<String> alInner = hmHireracyLevelEmpIds.get(rst.getString("supervisor_emp_id"));
				if(alInner==null) alInner = new ArrayList<String>();
				if(uF.parseToInt(rst.getString("supervisor_emp_id")) == uF.parseToInt(rst.getString("emp_id"))) {
					continue;
				}
				alInner.add(rst.getString("emp_id"));
				hmHireracyLevelEmpIds.put(rst.getString("supervisor_emp_id"), alInner);
			}
			rst.close();
			pst.close();

			StringBuilder sbClildEmpIds = null;
			List<String> empIDList = new ArrayList<String>();
//			System.out.println("AD/579---hmHireracyLevelEmpIds=="+hmHireracyLevelEmpIds);
			sbClildEmpIds = getChildEmpIds(hmHireracyLevelEmpIds, strSessionEmpId, empIDList, sbClildEmpIds);
			
			StringBuilder sbEmpId = null;
			if(strUserType != null && strUserType.equals(MANAGER)) {
				sbQuery = new StringBuilder();
				sbQuery.append("select reviewee_id from appraisal_reviewee_details ard, employee_personal_details epd where epd.emp_per_id=ard.reviewee_id  and appraisal_id=? " +
					"and (supervisor_ids like '%,"+strSessionEmpId+",%' or grand_supervisor_ids like '%,"+strSessionEmpId+",%' or hod_ids like '%,"+strSessionEmpId+",%' ");
				if(sbClildEmpIds!=null && !sbClildEmpIds.toString().equals("")) {
					sbQuery.append(" or epd.emp_per_id in ("+sbClildEmpIds.toString()+") ");
				}
				sbQuery.append(") order by emp_fname");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, uF.parseToInt(appraisal_id));
				rst = pst.executeQuery();
				while (rst.next()) {
					if(sbEmpId == null) {
						sbEmpId = new StringBuilder();
						sbEmpId.append(rst.getString("reviewee_id"));
					} else {
						sbEmpId.append(","+rst.getString("reviewee_id"));
					}
				}
				rst.close();
				pst.close();
			}
			
			sbQuery = new StringBuilder();
			sbQuery.append("select count(distinct emp_id) as count from appraisal_question_answer where emp_id in (select distinct reviewee_id from " +
				"appraisal_reviewee_details ard where appraisal_id=? and appraisal_freq_id=? ");
			if(sbEmpId!=null) {
				sbQuery.append(" and reviewee_id in ("+sbEmpId.toString()+")");
			}
			sbQuery.append(")");
			pst = con.prepareStatement(sbQuery.toString());
//			pst = con.prepareStatement("select count(distinct emp_id) as count from appraisal_question_answer where emp_id in (select distinct reviewee_id from " +
//				"appraisal_reviewee_details ard where appraisal_id=? and appraisal_freq_id=?)");
			pst.setInt(1, uF.parseToInt(appraisal_id));
			pst.setInt(2, uF.parseToInt(appFreqId));
//			System.out.println("pst ===>> " + pst);
			rst = pst.executeQuery();
			while (rst.next()) {
				count = rst.getInt("count");
			}
			rst.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}
	
	
	public int getAppraisalFinalCount(Connection con, UtilityFunctions uF, String appraisal_id,String appFreqId) {

		PreparedStatement pst = null;
		ResultSet rst = null;
		int count = 0;
		try {
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select emp_id,supervisor_emp_id from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id " +
				" and is_alive= true and emp_per_id >0 order by supervisor_emp_id");
			pst = con.prepareStatement(sbQuery.toString());
			rst = pst.executeQuery();
//			System.out.println("PST ===>> " + pst);
			Map<String, List<String>> hmHireracyLevelEmpIds = new LinkedHashMap<String, List<String>>();
//			List<String> alHireracyLevels = new ArrayList<String>();
			while(rst.next()) {
				List<String> alInner = hmHireracyLevelEmpIds.get(rst.getString("supervisor_emp_id"));
				if(alInner==null) alInner = new ArrayList<String>();
				if(uF.parseToInt(rst.getString("supervisor_emp_id")) == uF.parseToInt(rst.getString("emp_id"))) {
					continue;
				}
				alInner.add(rst.getString("emp_id"));
				hmHireracyLevelEmpIds.put(rst.getString("supervisor_emp_id"), alInner);
			}
			rst.close();
			pst.close();
//			System.out.println("AD/657---hmHireracyLevelEmpIds=="+hmHireracyLevelEmpIds);
			StringBuilder sbClildEmpIds = null;
			List<String> empIDList = new ArrayList<String>();
			sbClildEmpIds = getChildEmpIds(hmHireracyLevelEmpIds, strSessionEmpId, empIDList, sbClildEmpIds);
			
			StringBuilder sbEmpId = null;
			if(strUserType != null && strUserType.equals(MANAGER)) {
				sbQuery = new StringBuilder();
				sbQuery.append("select reviewee_id from appraisal_reviewee_details ard, employee_personal_details epd where epd.emp_per_id=ard.reviewee_id  and appraisal_id=? " +
					"and (supervisor_ids like '%,"+strSessionEmpId+",%' or grand_supervisor_ids like '%,"+strSessionEmpId+",%' or hod_ids like '%,"+strSessionEmpId+",%' ");
				if(sbClildEmpIds!=null && !sbClildEmpIds.toString().equals("")) {
					sbQuery.append(" or epd.emp_per_id in ("+sbClildEmpIds.toString()+") ");
				}
				sbQuery.append(") order by emp_fname");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, uF.parseToInt(appraisal_id));
				rst = pst.executeQuery();
				while (rst.next()) {
					if(sbEmpId == null) {
						sbEmpId = new StringBuilder();
						sbEmpId.append(rst.getString("reviewee_id"));
					} else {
						sbEmpId.append(","+rst.getString("reviewee_id"));
					}
				}
				rst.close();
				pst.close();
			}
			
			sbQuery = new StringBuilder();
			sbQuery.append("select count(distinct emp_id) as count from appraisal_final_sattlement where emp_id in (select distinct reviewee_id from " +
				"appraisal_reviewee_details ard where appraisal_id=? and appraisal_freq_id=? ");
			if(sbEmpId!=null) {
				sbQuery.append(" and reviewee_id in ("+sbEmpId.toString()+")");
			}
			sbQuery.append(")");
			pst = con.prepareStatement(sbQuery.toString());
//			pst = con.prepareStatement("select count(distinct emp_id) as count from appraisal_final_sattlement where emp_id in (select distinct reviewee_id from " +
//				"appraisal_reviewee_details ard where appraisal_id=? and appraisal_freq_id=?)");
			pst.setInt(1, uF.parseToInt(appraisal_id));
			pst.setInt(2, uF.parseToInt(appFreqId));
			rst = pst.executeQuery();
			while (rst.next()) {
				count = rst.getInt("count");
			}
			rst.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}


	
	private List<String> getAppendData(Connection con, String strID) {
		StringBuilder sb = new StringBuilder();
//		EncryptionUtils encryption = new EncryptionUtils();// Created By Dattatray Date : 20-July-2021 Note : Encryption
		List<String> empList = new ArrayList<String>();
		if (strID != null && !strID.equals("")) {
			int flag = 0, empcnt = 0;
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);

			String[] temp = strID.split(",");
			empcnt = temp.length - 1;
			for (int i = 0; i < temp.length; i++) {

				if (temp[i] != null && !temp[i].equals("")) {
					if (flag == 0) { //encryption.encrypt(temp[i])
						sb.append("<a href=\"MyProfile.action?empId=" + temp[i] + "\">" + hmEmpName.get(temp[i].trim()) + "</a>");// Created By Dattatray Date : 20-July-2021 Note : empId Encryption 
					} else {
						sb.append(", " + "<a href=\"MyProfile.action?empId=" + temp[i] + "\">" + hmEmpName.get(temp[i].trim()) + "</a>");// Created By Dattatray Date : 20-July-2021 Note : empId Encryption
					}
					flag = 1;
				}
			}
			empList.add(sb.toString());
			empList.add(empcnt + "");
		}
		return empList;
	}
	
	
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


	public String getStrSearchJob() {
		return strSearchJob;
	}

	public void setStrSearchJob(String strSearchJob) {
		this.strSearchJob = strSearchJob;
	}

	public String getProPage() {
		return proPage;
	}

	public void setProPage(String proPage) {
		this.proPage = proPage;
	}

	public String getMinLimit() {
		return minLimit;
	}

	public void setMinLimit(String minLimit) {
		this.minLimit = minLimit;
	}

	public String getCurrUserType() {
		return currUserType;
	}

	public void setCurrUserType(String currUserType) {
		this.currUserType = currUserType;
	}

	public String getAlertID() {
		return alertID;
	}

	public void setAlertID(String alertID) {
		this.alertID = alertID;
	}

	public String getCallFrom() {
		return callFrom;
	}

	public void setCallFrom(String callFrom) {
		this.callFrom = callFrom;
	}


	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	public String getReviewId() {
		return reviewId;
	}

	public void setReviewId(String reviewId) {
		this.reviewId = reviewId;
	}

	public String getAppFreqId() {
		return appFreqId;
	}

	public void setAppFreqId(String appFreqId) {
		this.appFreqId = appFreqId;
	}
	
}
