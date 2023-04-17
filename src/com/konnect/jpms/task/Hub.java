package com.konnect.jpms.task;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.common.ViewCompanyManual;
import com.konnect.jpms.employee.EmpDashboardData;
import com.konnect.jpms.reports.NoticeReport;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UploadImage;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class Hub extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	String strUserType=null;
	HttpSession session; 
	CommonFunctions CF;
	String strSessionEmpId;
	String strSessionOrgId;
	private String alertStatus;
	private String alert_type;
	private String alertID;
	
	private String type; 
	//quotes
	private String quotePost;
	private String strQuotedesc;
	private String strQuoteBy;
	
	private String allQuotesCount;
	private String lastQuoteId;
	
	//events
	private String strEventName;
	private String strSharing;
	private String strEventdesc;
	
	private String strLocation;
	private String strStartDate;
	private String strEndDate;
	private String startTime;
	private String endTime;
	private String eventPost;
	
	private File strEventImage;
	private String strEventImageFileName;
	
	private String allEventsCount;
	private String lastEventId;
	
	//notices
	private String displayStartDate;
	private String noticeId;
	private String heading;
	private String content;
	
	private String displayEndDate;
	private String publish;
	
	private String noticePost;
	private String ispublish;
	private String allNoticesCount;
	private String lastNoticeId;
	
	private String[] strLevel;
	private List<FillLevel> levelList;
	
	private List<FillOrganisation> orgList;
	private String strOrg;
	private static Logger log = Logger.getLogger(NoticeReport.class);
	
	private String manualId;
	private String operation;
	
	public String execute() throws Exception {
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		strSessionEmpId = (String)session.getAttribute(EMPID);
		strSessionOrgId = (String)session.getAttribute(ORGID);
		strUserType = (String)session.getAttribute(USERTYPE);
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		request.setAttribute(PAGE, "/jsp/task/Hub.jsp");
		request.setAttribute(TITLE, TViewNotice);
	
		
		StringBuilder sbpageTitleNaviTrail = new StringBuilder();
		if(strUserType != null && strUserType.equals(EMPLOYEE)) {
			sbpageTitleNaviTrail.append("<li><i class=\"fa fa-github-square\"></i><a href=\"Hub.action\" style=\"color: #3c8dbc;\"> My Hub</a></li>");
		} else {
			sbpageTitleNaviTrail.append("<li><i class=\"fa fa-github-square\"></i><a href=\"Hub.action\" style=\"color: #3c8dbc;\"> Hub</a></li>");
		}
		request.setAttribute("PAGETITLE_NAVITRAIL", sbpageTitleNaviTrail.toString());
		
		UtilityFunctions uF = new UtilityFunctions();
		
		levelList = new FillLevel(request).fillLevel();
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
			if(uF.parseToInt(getStrOrg()) == 0  && orgList!=null && orgList.size()>0){
				setStrOrg(orgList.get(0).getOrgId());
			}
		}else{
			if(uF.parseToInt(getStrOrg()) == 0){
				setStrOrg((String) session.getAttribute(ORGID));
			}
			orgList = new FillOrganisation(request).fillOrganisation();
		}

		if(getAlertID()!=null && !getAlertID().equals("")){
			deleteUserAlerts();
		} 
		
		viewLiveJobList(uF, uF.parseToInt(getStrOrg()));
		
		getLoggedEmpImage(uF);
		getHubCounts(uF);
		viewProfile(uF, strSessionEmpId);
		loadEventReport(uF);
		loadNoticeReport(uF);
		viewReportManual(uF);
		if(getQuotePost()!=null && !getQuotePost().equals("") ) {
//			System.out.println("inside quote save");
			saveQuoteData(uF);
			clearQuotes();
		}
		
		if(getEventPost()!=null && !getEventPost().equals("")) {
			saveEventData(uF);
			clearEvent();
		}
		
		if(getNoticePost()!=null && !getNoticePost().equals("")) {
			 saveNotice();
			 clearNotice();
		}
		
		if(type == null || type.equals("") || type.equals("null")) {
			type = "F";
		}
//		System.out.println("type ===>> " + type);
		
		if(type != null && !type.equals("")){
			
			if(type.equals("A")){
				request.setAttribute(TITLE, TViewNotice);
				viewNotice(uF);	
			}
			
			if(type.equals("E")){
				request.setAttribute(TITLE, "Events");
				viewEvents(uF);	
			}
			
			if(type.equals("Q")){
				request.setAttribute(TITLE, "Quotes");
				viewQuotes(uF);	
			}
			if(type.equals("FAQ")){
				request.setAttribute(TITLE, "FAQ");
				viewFAQ(uF);	
			}
			
			if(type.equals("M")){
				request.setAttribute(TITLE, "Manual");
				
				if(strUserType != null && strUserType.equals(EMPLOYEE)) {
					updateUserAlerts(uF, NEW_MANUAL_ALERT);
				}
				
				ViewCompanyManual manual = new ViewCompanyManual(request, CF, strSessionOrgId);
				manual.viewManual(null);
				
				
			}
			
		} else {
			request.setAttribute(TITLE, TViewNotice);
			viewNotice(uF);	
		}
		
		return loadNotice();

	}
	
	
	
	public void viewTodaysEvent(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			
			Map<String, String> hmResourceName = CF.getEmpNameMap(con, null, null);
			
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			Map<String, String> hmLevelMap = getLevelNameMap(con, uF);
			
			String logUserLevel = hmEmpLevelMap.get(strSessionEmpId);
			
			List<String> alInner = new ArrayList<String>();
			pst = con.prepareStatement("select * from events where (sharing_level is null or length(trim(sharing_level))=0 or " +
				"sharing_level like '%,"+logUserLevel+",%' or added_by= ("+strSessionEmpId+")) and (? between event_date and event_end_date or " +
				" ? <event_date) order by event_date desc limit 1");
//			System.out.println("pst ========>>> " + pst);
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			while(rs.next()) {
				
				alInner.add(Integer.toString(rs.getInt("event_id")));//0
				if(rs.getString("event_date")!=null && !rs.getString("event_date").equals("")) {
					alInner.add(uF.getDateFormat(rs.getString("event_date"), DBDATE, CF.getStrReportDateFormat()));//1
				} else {
					alInner.add("");//1
				}
				
				if(rs.getString("event_end_date")!=null && !rs.getString("event_end_date").equals("")) {
					alInner.add(uF.getDateFormat(rs.getString("event_end_date"), DBDATE, CF.getStrReportDateFormat()));//2
				} else {
					alInner.add("");//2
				}
				
				alInner.add(uF.getDateFormat(rs.getString("posted_date"),DBTIMESTAMP, DATE_FORMAT_STR+" "+TIME_FORMAT_AM_PM));//3
				alInner.add(rs.getString("event_title"));//4
				alInner.add(rs.getString("event_desc"));//5
				alInner.add(hmResourceName.get(rs.getString("added_by")));//6
				alInner.add(rs.getString("location"));//7
				
//				System.out.println("events==>"+rs.getString("sharing_level"));
				String sharing_level = rs.getString("sharing_level");
				String[] levels = null;
				if(sharing_level!=null && !sharing_level.equals("")) {
					levels = rs.getString("sharing_level").split(",");
					StringBuilder sharing = null;
//					System.out.println("level's=>"+levels.length);
					for(String lvlId : levels) {
						
						if(uF.parseToInt(lvlId)>0) {
							if(sharing == null) {
								sharing = new StringBuilder();
								sharing.append(hmLevelMap.get(lvlId));
							} else {
								sharing.append(", "+hmLevelMap.get(lvlId));
							}
						}
					}
					if(sharing == null) {
						sharing = new StringBuilder();
					}
					alInner.add(sharing.toString());//8
				}else{					
					alInner.add("All Level's");//8
				}
				
				alInner.add(rs.getString("event_image"));//9
				alInner.add(rs.getString("added_by"));//10
				String extenstion = null;
				if(rs.getString("event_image") !=null && !rs.getString("event_image").trim().equals("")){
					extenstion = FilenameUtils.getExtension(rs.getString("event_image").trim());
				}
				alInner.add(extenstion);//11
//				
				String eventImgPath = "";
				if(rs.getString("event_image")!=null && !rs.getString("event_image").equals("")) {
					if(CF.getStrDocSaveLocation()==null) {
						eventImgPath =  DOCUMENT_LOCATION +"/"+rs.getString("added_by")+"/"+rs.getString("event_image") ;
					} else {
						eventImgPath = CF.getStrDocRetriveLocation()+I_EVENTS+"/"+rs.getString("added_by")+"/"+rs.getString("event_image");
					}
				}
				//System.out.println("eventImg==>"+eventImgPath);
				String eventImage = "<img class='lazy' border=\"0\" style=\"max-height: 180px; max-width: 94%; padding: 5px; border-radius: 0%;\" src=\"images1/no-events.jpg\" data-original=\""+eventImgPath+"\" />";
				alInner.add(eventImage); //12
				alInner.add("");//13
				String from_time = rs.getString("from_time");
				String to_time = rs.getString("to_time");
				
				if(from_time != null && !from_time.equals("")){
					
					alInner.add(uF.getTimeFormatStr(from_time.substring(0,from_time.lastIndexOf(":")),DBTIME,TIME_FORMAT_AM_PM));
				}else{
					alInner.add("");//14
				}
				if(to_time != null  && !to_time.equals("")){
					
					alInner.add(uF.getTimeFormatStr(to_time.substring(0,to_time.lastIndexOf(":")),DBTIME,TIME_FORMAT_AM_PM));
				}else{
					alInner.add("");//15
				}
			}
//			System.out.println("eventId==>"+lastEventId);
			rs.close();
			pst.close();
			request.setAttribute("alInner", alInner);
//			System.out.println("sbEventIds ===>> " + sbEventIds);

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	
	public void viewLiveJobList(UtilityFunctions uF, int orgId) {
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			List<List<String>> alJobList = new ArrayList<List<String>>();
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from recruitment_details where job_approval_status=1 and close_job_status=false and org_id="+orgId);
			pst=con.prepareStatement(sbQuery.toString());
//			System.out.println("pst1==>"+pst);
			rst=pst.executeQuery();
			while(rst.next()) {
				List<String> alInner = new ArrayList<String>();
				alInner.add(rst.getString("recruitment_id"));
				alInner.add(rst.getString("job_code"));
				alInner.add(rst.getString("job_title"));
				alInner.add(rst.getString("no_position"));
				
				alJobList.add(alInner);
			}
			rst.close();
			pst.close();
			request.setAttribute("alJobList", alJobList);
//			System.out.println("sbEventIds ===>> " + sbEventIds);

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	
	private void getHubCounts(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			con = db.makeConnection(con);
			/*pst = con.prepareStatement("select count(communication_id) as feedsCount from communication_1");
		//	System.out.println("pst Feeds Count========>>> " + pst);
			rs = pst.executeQuery();
			
			int feedsCount=0;
			while(rs.next()) {
				feedsCount = rs.getInt("feedsCount");
			}
			rs.close();
			pst.close();*/
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select count(*) as cnt from taskrig_user_alerts where alert_data is not null and alert_action is not null ");
			if(strUserType != null && strUserType.equalsIgnoreCase(CUSTOMER)){
				sbQuery.append(" and customer_id=?");
			} else {
				sbQuery.append(" and resource_id=?");
			}
//			sbQuery.append("");
			 pst = con.prepareStatement(sbQuery.toString());			
			 pst.setInt(1, uF.parseToInt(strSessionEmpId));
//			 System.out.println("pst ===> " +pst);
			 rs = pst.executeQuery();
			 int feedsCount = 0;
			 while (rs.next()) {
				 feedsCount = rs.getInt("cnt");
			 }
			rs.close();
			pst.close();
		//	System.out.println("feedsCount---->"+feedsCount);
			
			// For announcement

			String curr_date=uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT);
			if(strUserType.equals(ADMIN)){
				pst = con.prepareStatement("select count(notice_id) as noticeCount from notices");
			}else if(strUserType.equals(HRMANAGER)){
				pst = con.prepareStatement("select count(notice_id) as noticeCount from notices where added_by = ("+strSessionEmpId+") or ispublish='t'");
			}else{
				pst = con.prepareStatement("select count(notice_id) as noticeCount from notices where ispublish='t' and  display_date <= '"+uF.getDateFormat(curr_date, DATE_FORMAT)+"' and display_end_date >= '"+uF.getDateFormat(curr_date, DATE_FORMAT)+"'");
			}
		//	pst = con.prepareStatement("select count(notice_id) as noticeCount from notices");
			
		//	System.out.println("pst Notice Count========>>> " + pst);
			rs = pst.executeQuery();
			int totalNoticeCount=0;
			while(rs.next()) {
				totalNoticeCount = rs.getInt("noticeCount");
			}
			rs.close();
			pst.close();
			
			//For Events
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			String logUserLevel = hmEmpLevelMap.get(strSessionEmpId);
			
			if(strUserType.equals(ADMIN)){
				pst = con.prepareStatement("select count(event_id) as eventCount from events");
			}else {
				pst = con.prepareStatement("select count(event_id) as eventCount from events where sharing_level is null or length(trim(sharing_level))=0 or sharing_level like '%,"+logUserLevel+",%' or added_by= ("+strSessionEmpId+")");
			}
		//	pst = con.prepareStatement("select count(event_id) as eventCount from events");
			
		//	System.out.println("pst Event Count========>>> " + pst);
			rs = pst.executeQuery();
			int eventsCount=0;
			while(rs.next()) {
				eventsCount = rs.getInt("eventCount");
			}
			rs.close();
			pst.close();
			
			// For Quotes
			
			pst = con.prepareStatement("select count(thought_id) quotesCount from daythoughts");
			
		//	System.out.println("pst Event Quotes========>>> " + pst);
			rs = pst.executeQuery();
			int quotesCount=0;
			while(rs.next()) {
				quotesCount = rs.getInt("quotesCount");
			}
			rs.close();
			pst.close();
			
			
			pst = con.prepareStatement("select count(faq_id) faqsCount from faq_details");
			
				//System.out.println("pst Event faq========>>> " + pst);
				rs = pst.executeQuery();
				int faqCount=0;
				while(rs.next()) {
					faqCount = rs.getInt("faqsCount");
				}
				rs.close();
				pst.close();
			
			// For Mannual
			if(strUserType.equals(ADMIN)){
				pst = con.prepareStatement("select count(manual_id) as manualCount from company_manual");
			}else {
				pst = con.prepareStatement("select count(manual_id) as manualCount from company_manual where status=1 and org_id=?");
				pst.setInt(1,Integer.parseInt(strSessionOrgId));
			}
			//pst = con.prepareStatement("select count(manual_id) as manualCount from company_manual");
		//	System.out.println("pst manualCount Count========>>> " + pst);
			rs = pst.executeQuery();
			int manualCount=0;
			while(rs.next()) {
				manualCount = rs.getInt("manualCount");
			}
			rs.close();
			pst.close();
			
			request.setAttribute("feedsCount", ""+feedsCount);
			request.setAttribute("totalNoticeCount", ""+totalNoticeCount);
			request.setAttribute("eventsCount", ""+eventsCount);
			request.setAttribute("quotesCount", ""+quotesCount);
			request.setAttribute("manualCount", ""+manualCount);
			request.setAttribute("faqCount", ""+faqCount);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void updateUserAlerts(UtilityFunctions uF, String alertType) {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		int nEmpId = uF.parseToInt((String)session.getAttribute(EMPID));
		try {
			con = db.makeConnection(con);
			String strDomain = request.getServerName().split("\\.")[0];
			UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
			userAlerts.setStrDomain(strDomain);
			userAlerts.setStrEmpId(""+nEmpId);
			userAlerts.set_type(alertType);
			userAlerts.setStatus(UPDATE_ALERT);
			Thread t = new Thread(userAlerts);
			t.run();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeConnection(con);
		}
	}
	
	
	public void viewReportManual(UtilityFunctions uF){

		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		List<List<String>> reportList = new ArrayList<List<String>>();
		try{
			con = db.makeConnection(con);
			Map<String, String> hmOrgName = CF.getOrgName(con);

			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from company_manual where manual_id>0 ");
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(strUserType!=null && strUserType.equalsIgnoreCase(EMPLOYEE)) {
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+") and status = 1 ");
			}
			sbQuery.append(" order by _date desc");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			int nCount = 0;
			StringBuilder strManualIds = null;
			while(rs.next()){
				List<String> alInner = new ArrayList<String>();
				alInner.add(uF.getDateFormat(rs.getString("_date"), DBTIMESTAMP, CF.getStrReportDateFormat())
						+" "+uF.getDateFormat(rs.getString("_date"), DBTIMESTAMP, CF.getStrReportTimeFormat()));//0
				alInner.add(rs.getString("manual_title"));//1
				
				alInner.add(getStaus(rs.getInt("status"), rs.getInt("manual_id"), nCount,rs.getString("org_id")));//2

				alInner.add(rs.getString("manual_id"));//3
				if(strManualIds== null){
					strManualIds = new StringBuilder();
					strManualIds.append(rs.getString("manual_id"));
				}else{
					strManualIds.append(","+rs.getString("manual_id"));
				}
				
				alInner.add(hmOrgName.get(rs.getString("org_id")));//4
				 /*String strLive = "<img border=\"0\" style=\"width: 14px; height: 14px;\" src=\"images1/icons/pending.png\" title=\"Draft\">";*/
				String strLive = "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\" title=\"Draft\"></i>";
				if(rs.getInt("status")== 1){
					/*strLive = "<img border=\"0\" style=\"width: 14px; height: 14px;\" src=\"images1/icons/approved.png\" title=\"Published\">";*/
					strLive = "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Published\"></i>";
				}else if(rs.getInt("status")== 2){
					/*strLive = "<img border=\"0\" style=\"width: 14px; height: 14px;\" src=\"images1/icons/pullout.png\" title=\"Archived\">";*/
					strLive = "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#ea9900\" title=\"Archived\"></i>";
				}
				alInner.add(strLive);//5
				alInner.add(rs.getString("manual_body"));//6
				alInner.add(rs.getString("manual_doc"));//7
				reportList.add(alInner);
				nCount++;
			}
			rs.close();
			pst.close();
			if(strManualIds== null){
				strManualIds = new StringBuilder();
			}
			
			//System.out.println("strManualIds==>"+strManualIds.toString());
			request.setAttribute("reportList", reportList);
			request.setAttribute("strManualIds", strManualIds.toString());
						
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}

	public String getStaus(int nStatus, int nManualId, int nCount,String orgId){
		String strStatus = null;
		
		switch(nStatus){
		case -1:
			strStatus = "";
			break;
			
		case 0:
			//strStatus = "Draft <a href=\"javascript:void(0);\" onclick=\"getContent('myDiv"+nCount+"','UpdateManualStatus.action?orgId="+orgId+"&MID="+nManualId+"')\">Publish this version</a>";
			strStatus = "Draft <a href=\"UpdateManualStatus.action?orgId="+orgId+"&MID="+nManualId+"&pageFrom=MyHub"+"\">Publish this version</a> ";
			break;
			
		case 1:
			strStatus = "Published <a href=\"UpdateManualStatus.action?orgId="+orgId+"&MID="+nManualId+"&pageFrom=MyHub&operation=UP"+"\">Unpublish this version</a> ";
			break;
			
		case 2:
			//strStatus = "Archived <a href=\"javascript:void(0);\" onclick=\"getContent('myDiv"+nCount+"','UpdateManualStatus.action?orgId="+orgId+"&MID="+nManualId+"')\">Publish this version</a>";
			strStatus = "Archived <a href=\"UpdateManualStatus.action?orgId="+orgId+"&MID="+nManualId+"&pageFrom=MyHub"+"\">Publish this version</a>";
			break;
		
		}
		
		return strStatus;
	}

	public void getLoggedEmpImage(UtilityFunctions uF){
		Connection con = null;
		
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			Map<String, String> hmCustName = CF.getCustomerNameMap(con);
			Map<String, String> hmResourceImage = CF.getEmpProfileImage(con);
			Map<String, String> hmCustImage = CF.getCustomerImageMap(con);
			if(strUserType != null && strUserType.equals(CUSTOMER)) {
				String createdByImage = hmCustImage.get(strSessionEmpId);
				String strClientId = CF.getClientIdBySPOCId(con, uF, strSessionEmpId);

				String MYLargeImage = "<img class='lazy img-circle' border=\"1px solid #CCCCCC\" height=\"80\" width=\"80\" src=\"userImages/avatar_photo.png\" />";
				String MYImage = "<img class='lazy img-circle' border=\"0\" height=\"30\" width=\"30\" src=\"userImages/avatar_photo.png\" />"; 
				if(CF.getStrDocRetriveLocation()==null) { 
					MYImage = "<img class='lazy img-circle' border=\"0\" height=\"30\" width=\"30\" src=\"userImages/avatar_photo.png\" data-original=\""+DOCUMENT_LOCATION + createdByImage+"\" />";
					MYLargeImage = "<img class='lazy img-circle' border=\"1px solid #CCCCCC\" height=\"80\" width=\"80\" src=\"userImages/avatar_photo.png\" data-original=\""+DOCUMENT_LOCATION + createdByImage+"\" />";
			  	} else if(createdByImage != null && !createdByImage.equals("")) {
			  		MYImage = "<img class='lazy img-circle' border=\"0\" height=\"30\" width=\"30\" src=\"userImages/avatar_photo.png\" data-original=\""+CF.getStrDocRetriveLocation() +I_CUSTOMER+"/"+strClientId+"/"+I_IMAGE+"/"+I_CUSTOMER_SPOC+"/"+I_22x22+"/"+createdByImage+"\" />";
			  		MYLargeImage = "<img class='lazy img-circle' border=\"1px solid #CCCCCC\" height=\"80\" width=\"80\" src=\"userImages/avatar_photo.png\" data-original=\""+CF.getStrDocRetriveLocation() +I_CUSTOMER+"/"+strClientId+"/"+I_IMAGE+"/"+I_CUSTOMER_SPOC+"/"+I_100x100+"/"+createdByImage+"\" />";
	            }
				
				request.setAttribute("MYLargeImg", MYLargeImage);
				request.setAttribute("MYImg", MYImage);
			} else {
				String createdByImage = hmResourceImage.get(strSessionEmpId);

				String MYLargeImage = "<img class='lazy img-circle' border=\"1px solid #CCCCCC\" height=\"80\" width=\"80\" src=\"userImages/avatar_photo.png\" />";
				String MYImage = "<img class='lazy img-circle' border=\"0\" height=\"30\" width=\"30\" src=\"userImages/avatar_photo.png\" />"; 
				if(CF.getStrDocRetriveLocation()==null) { 
					MYImage = "<img class='lazy img-circle' border=\"0\" height=\"30\" width=\"30\" src=\"userImages/avatar_photo.png\" data-original=\""+DOCUMENT_LOCATION + createdByImage+"\" />";
					MYLargeImage = "<img class='lazy img-circle' border=\"1px solid #CCCCCC\" height=\"80\" width=\"80\" src=\"userImages/avatar_photo.png\" data-original=\""+DOCUMENT_LOCATION + createdByImage+"\" />";
			  	} else if(createdByImage != null && !createdByImage.equals("")) {
			  		MYImage = "<img class='lazy img-circle' border=\"0\" height=\"30\" width=\"30\" src=\"userImages/avatar_photo.png\" data-original=\""+CF.getStrDocRetriveLocation() +I_PEOPLE+"/"+I_IMAGE+"/"+strSessionEmpId+"/"+I_22x22+"/"+createdByImage+"\" />";
			  		MYLargeImage = "<img class='lazy img-circle' border=\"1px solid #CCCCCC\" height=\"80\" width=\"80\" src=\"userImages/avatar_photo.png\" data-original=\""+CF.getStrDocRetriveLocation() +I_PEOPLE+"/"+I_IMAGE+"/"+strSessionEmpId+"/"+I_100x100+"/"+createdByImage+"\" />";
	            }
				request.setAttribute("MYLargeImg", MYLargeImage);
				request.setAttribute("MYImg", MYImage);
			}
					
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeConnection(con);
		}

	}
	public void clearEvent(){
		UtilityFunctions uF = new UtilityFunctions();
		
		setStrEventName("");
		setStrSharing("");
		setStrEventdesc("");
		
		setStrLocation("");
		setStartTime("");
		loadEventReport(uF);
		setEndTime("");
		
		
		setStrEventImage(null);
		setStrEventImageFileName("");
	}
	
	public void clearNotice(){
		UtilityFunctions uF = new UtilityFunctions();
		
		setHeading("");
		setContent("");
		
		loadNoticeReport(uF);
		
	} 
	
	public void clearQuotes(){
		setStrQuoteBy("");
		setStrQuotedesc("");
	}
	
	public String loadNotice(){
		
		return LOAD;
	}
	
	public String saveNotice() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		//System.out.println("in save notice");
		try {
			
			
			String strDomain = request.getServerName().split("\\.")[0];
			String strData = "";
			con = db.makeConnection(con);
			Map<String, String> hmResourceName = CF.getEmpNameMap(con, null, null);
			List<String> alInner = CF.isCommAvailable(con, uF, "Announcements");
			if(alInner == null) alInner = new ArrayList<String>();
			boolean flag =false;
			int size = alInner.size();
			
			if(alInner!=null && size>0){
//				System.out.println("alInner notice==>"+size);
				if(alInner.get(3).equalsIgnoreCase(getHeading()) && alInner.get(4).equalsIgnoreCase(getContent())
					&& strSessionEmpId.equalsIgnoreCase(alInner.get(6))	) {
					flag = true;
				}
				
			}
			
			if(flag == false){
	//			pst = con.prepareStatement(insertNotice);
				pst = con.prepareStatement("INSERT INTO notices (heading, content, _date, display_date,display_end_date,ispublish,added_by,posted_date,updated_by,last_updated) VALUES (?,?,?,?,?,?,?,?,?,?)");
				pst.setString(1, getHeading());
				pst.setString(2, getContent());
				pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
				
//				if(getDisplayStartDate() != null && (getDisplayStartDate().equals("From Date") || getDisplayStartDate().equals(""))) {
//					pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
//				}else{
					pst.setDate(4, uF.getDateFormat(getDisplayStartDate(), DATE_FORMAT));
				//}
				
//				if(getDisplayEndDate() != null && (getDisplayEndDate().equals("To Date") || getDisplayEndDate().equals(""))) {
//					pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
//				}else{
					pst.setDate(5, uF.getDateFormat(getDisplayEndDate(), DATE_FORMAT));
//				}
				
				boolean flg = false;
				if(getNoticePost()!=null && !getNoticePost().equals("") && getIspublish()!=null){
					if(getIspublish().equals("1")){
						pst.setBoolean(6, true); //Publish
						flg = true;
					}else{
						pst.setBoolean(6, false); //Unpublish
						flg = false;
					}
				}else{
					if(getPublish()!=null){
						pst.setBoolean(6, true); //Publish
						flg = true;
					}else{
						pst.setBoolean(6, false); //Unpublish
						flg = false;
					}
				}
				pst.setInt(7,uF.parseToInt((String)session.getAttribute(EMPID)));
				pst.setTimestamp(8, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
				pst.setInt(9,uF.parseToInt((String)session.getAttribute(EMPID)));
				pst.setTimestamp(10, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
				
				pst.execute();
				pst.close();
				
				if(flg == true){
					if(hmResourceName != null){
						Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
						Set<String> empIdSet  = hmResourceName.keySet();
						Iterator<String> it = empIdSet.iterator();
						while(it.hasNext()){
							String empId = it.next();
							String alertData = "<div style=\"float: left;\"> <b>"+hmResourceName.get(strSessionEmpId)+"</b> has published an announcement of <b><div style=\"font-size:11px;font-style:bold;\"> "+getHeading()+"</b></div></div>";
							String alertAction = "Hub.action?pType=WR&type=A";
							UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
							userAlerts.setStrDomain(strDomain);
							userAlerts.setStrEmpId(empId);
							userAlerts.setStrData(alertData);
							userAlerts.setStrAction(alertAction);
							userAlerts.setCurrUserTypeID(hmUserTypeId.get(EMPLOYEE));
							userAlerts.setStatus(INSERT_WR_ALERT);
							Thread t = new Thread(userAlerts);
							t.run();
							
//							strData = "<div style=\"float: left;\"> <b>"+hmResourceName.get(strSessionEmpId)+"</b> has published an announcement of <b><div style=\"font-size:11px;font-style:bold;\"> "+getHeading()+"</b></div></div>";
//							UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//							userAlerts.setStrDomain(strDomain);
//							userAlerts.set_type(NEWS_AND_ALERTS);
//							userAlerts.insertTRUserAlerts(con,CF,uF,empId,strData, "Hub.action?type=A");
						}
					}
				}
				session.setAttribute(MESSAGE,SUCCESSM +"<b>"+getHeading()+" announcement posted Successfully."+ END );
			}

		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}

		return VIEW;

	}
	
	public Map<String,String> getLevelNameMap(Connection con,UtilityFunctions uF){
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> hmLevelMap = new LinkedHashMap<String,String>();
		Database db = new Database();
		db.setRequest(request);
		try{
			
			pst = con.prepareStatement("SELECT * FROM level_details ");
			rs = pst.executeQuery();
			while(rs.next()) {
				hmLevelMap.put(rs.getString("level_id"), rs.getString("level_name"));
			}
			rs.close();
			pst.close();
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		return hmLevelMap;
	}
	
	public String saveEventData(UtilityFunctions uF){
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			
			String strDomain = request.getServerName().split("\\.")[0];
			con = db.makeConnection(con);
			Map<String, String> hmResourceName = CF.getEmpNameMap(con, null, null);
			StringBuilder sharinglevel = null;
			
			if(getStrLevel()!=null && getStrLevel().length>0){
				if(sharinglevel == null){
					sharinglevel = new StringBuilder();
					sharinglevel.append(",");
				}
				for(String level:getStrLevel()){
					if(level!=null && !level.equals("")){
						sharinglevel.append(level+",");
					}else{
						sharinglevel = new StringBuilder();
						sharinglevel.append("");
						break;
					}
				}
				
			}
			if(sharinglevel == null) {
				sharinglevel = new StringBuilder();
			}
			//System.out.println("sharingLevel==>"+sharinglevel);
			List<String> alInner = CF.isCommAvailable(con, uF, "Events");
			if(alInner == null) alInner = new ArrayList<String>();
			boolean flag =false;
			int size = alInner.size();
			
			if(alInner!=null && size>0){
//				System.out.println("alInner events==>"+size);
				if(alInner.get(3).equalsIgnoreCase(getStrEventName()) && alInner.get(4).equalsIgnoreCase(getStrEventdesc())
					&& alInner.get(6).equalsIgnoreCase(getStrLocation()) && alInner.get(5).equalsIgnoreCase(sharinglevel.toString()) && strSessionEmpId.equalsIgnoreCase(alInner.get(9))	) {
					flag = true;
				}
				
			}
//			System.out.println("flag==>"+ flag);
			if(flag == false){
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("insert into events(event_title,event_desc,event_date,posted_date,added_by,location,sharing_level,event_end_date,from_time,to_time,entry_date,updated_by,last_updated)"
						+" values(?,?,?,?,?,?,?,?,?,?,?,?,?)");
				
				pst = con.prepareStatement(sbQuery.toString());
				pst.setString(1,getStrEventName());
				pst.setString(2,getStrEventdesc());
				pst.setDate(3,uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
				pst.setTimestamp(4, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
				
				pst.setInt(5,uF.parseToInt((String)session.getAttribute(EMPID)));
				pst.setString(6,getStrLocation());
				pst.setString(7,sharinglevel.toString());
				
				pst.setDate(8,uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
				pst.setTime(9, uF.getTimeFormat(getStartTime(), TIME_FORMAT));
				
				pst.setTime(10, uF.getTimeFormat(getEndTime(), TIME_FORMAT));
				pst.setDate(11,uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(12,uF.parseToInt((String)session.getAttribute(EMPID)));
				pst.setTimestamp(13, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
				pst.executeUpdate();
				pst.close();
				
				session.setAttribute(MESSAGE,SUCCESSM +"<b>"+getStrEventName()+" event added Successfully."+ END );
				
				String eventId = null;
				
				pst = con.prepareStatement("select max(event_id) as event_id from events");
				rs = pst.executeQuery();
			//	System.out.println("pst==>"+pst);
				while(rs.next()) {
					eventId = rs.getString("event_id");
				}
				rs.close();
				pst.close();
				
				
				uploadImage(eventId);
				
				Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
//				Map<String, String> hmLevelMap = getLevelNameMap(con, uF);
//				String logUserLevel = hmEmpLevelMap.get(strSessionEmpId);
				
				List<String> allSharing = new ArrayList<String>();
				 
				if(sharinglevel.toString() != null && !sharinglevel.toString().equals("")) {
					allSharing = Arrays.asList(sharinglevel.toString().split(","));
				}
				
				if(sharinglevel.toString() == null || sharinglevel.toString().equals("") ){//|| allSharing.contains(logUserLevel)){
					
					if(hmResourceName != null){
						Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
						Set<String> empIdSet  = hmResourceName.keySet();
						Iterator<String> it = empIdSet.iterator();
						while(it.hasNext()){
							String empId = it.next();
							String alertData = "<div style=\"float: left;\"> <b>"+hmResourceName.get(strSessionEmpId)+"</b> shared an event of <b><div style=\"font-size:11px;font-style:bold;\"> "+getStrEventName()+"</b></div></div>";
							String alertAction = "Hub.action?pType=WR&type=E";
							UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
							userAlerts.setStrDomain(strDomain);
							userAlerts.setStrEmpId(empId);
							userAlerts.setStrData(alertData);
							userAlerts.setStrAction(alertAction);
							userAlerts.setCurrUserTypeID(hmUserTypeId.get(EMPLOYEE));
							userAlerts.setStatus(INSERT_WR_ALERT);
							Thread t = new Thread(userAlerts);
							t.run();
							
//							String strData = "<div style=\"float: left;\"> <b>"+hmResourceName.get(strSessionEmpId)+"</b> shared an event of <b><div style=\"font-size:11px;font-style:bold;\"> "+getStrEventName()+"</b></div></div>";
//							UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//							userAlerts.setStrDomain(strDomain);
//							userAlerts.set_type(NEWS_AND_ALERTS);
//							userAlerts.insertTRUserAlerts(con,CF,uF,empId,strData, "Hub.action?type=E");
					}
				  }
				}
				
				
				if(allSharing!= null){
					if(hmResourceName != null){
						Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
						Set<String> empIdSet  = hmResourceName.keySet();
						Iterator<String> it = empIdSet.iterator();
						while(it.hasNext()){
							String empId = it.next();
							String empLevel = hmEmpLevelMap.get(empId);
							if(empLevel != null && allSharing.contains(empLevel)){
								String alertData = "<div style=\"float: left;\"> <b>"+hmResourceName.get(strSessionEmpId)+"</b> shared an event of <b><div style=\"font-size:11px;font-style:bold;\"> "+getStrEventName()+"</b></div></div>";
								String alertAction = "Hub.action?pType=WR&type=E";
								UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
								userAlerts.setStrDomain(strDomain);
								userAlerts.setStrEmpId(empId);
								userAlerts.setStrData(alertData);
								userAlerts.setStrAction(alertAction);
								userAlerts.setCurrUserTypeID(hmUserTypeId.get(EMPLOYEE));
								userAlerts.setStatus(INSERT_WR_ALERT);
								Thread t = new Thread(userAlerts);
								t.run();
								
	//							String strData = "<div style=\"float: left;\"> <b>"+hmResourceName.get(strSessionEmpId)+"</b> shared an event of <b><div style=\"font-size:11px;font-style:bold;\"> "+getStrEventName()+"</b></div></div>";
	//							UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
	//							userAlerts.setStrDomain(strDomain);
	//							userAlerts.set_type(NEWS_AND_ALERTS);
	//							userAlerts.insertTRUserAlerts(con,CF,uF,empId,strData, "Hub.action?type=E");
							}
						}
					}
					
				}
				
			}
				
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
		
	}
	
	private void uploadImage(String eventId) {
		
		try {
			
			UploadImage uI = new UploadImage();
			uI.setServletRequest(request);
			uI.setImageType("EVENT_IMAGE");
			uI.setEmpImage(getStrEventImage());
			uI.setEmpImageFileName(getStrEventImageFileName());
			uI.setEmpId((String)session.getAttribute(EMPID));
			uI.setEventId(eventId);
			uI.setCF(CF);
			uI.upoadImage();
			
		}catch (Exception e) {
			e.printStackTrace();
			
		}
		
	}
	
	public String loadEventReport(UtilityFunctions uF) {
		
		if((getStrStartDate() == null || getStrStartDate().equals("")) && (getStrEndDate() == null || getStrEndDate().equals(""))) {
			String currDate = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT);
			String minMaxDate = uF.getCurrentMonthMinMaxDate(currDate, DATE_FORMAT);
			String[] tmpDate = minMaxDate.split("::::");
			setStrStartDate(tmpDate[0]);
			setStrEndDate(tmpDate[1]);
		}
		
		return SUCCESS;
	}
	
	public String loadNoticeReport(UtilityFunctions uF) {
		
		if((getDisplayStartDate() == null || getDisplayStartDate().equals("")) && (getDisplayEndDate() == null || getDisplayEndDate().equals(""))) {
			String currDate = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT);
			String minMaxDate = uF.getCurrentMonthMinMaxDate(currDate, DATE_FORMAT);
			String[] tmpDate = minMaxDate.split("::::");
			setDisplayStartDate(tmpDate[0]);
			setDisplayEndDate(tmpDate[1]);
		}
		
		return SUCCESS;
	}
	
	public void saveQuoteData(UtilityFunctions uF ){
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			String strDomain = request.getServerName().split("\\.")[0];
			
			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
			con = db.makeConnection(con);
			Map<String, String> hmResourceName = CF.getEmpNameMap(con, null, null);
			List<String> alInner = CF.isCommAvailable(con, uF, "Quotes");
			if(alInner == null) alInner = new ArrayList<String>();
			boolean flag =false;
			int size = alInner.size();
			
			if(alInner!=null && size>0){
//				System.out.println("alInner==>"+size);
				if(alInner.get(2).equalsIgnoreCase(getStrQuoteBy()) && alInner.get(3).equalsIgnoreCase(getStrQuotedesc())
					&& strSessionEmpId.equalsIgnoreCase(alInner.get(5))	) {
					flag = true;
				}
				
			}
//			System.out.println("flag ==>"+flag);
			if(flag == false){
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("insert into daythoughts(thought_text,thought_by,entry_date,added_by,posted_date,updated_by,last_updated,day_id,year)"
						+" values(?,?,?,?,?,?,?,?,?)");
				
				pst = con.prepareStatement(sbQuery.toString());
				pst.setString(1,getStrQuotedesc());
				pst.setString(2,getStrQuoteBy());
				
				pst.setDate(3,uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(4,uF.parseToInt((String)session.getAttribute(EMPID)));
				pst.setTimestamp(5, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
				pst.setInt(6,uF.parseToInt((String)session.getAttribute(EMPID)));
				pst.setTimestamp(7, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
				pst.setInt(8,uF.getRandomDayNumber());
				pst.setInt(9,cal.get(Calendar.YEAR));
				pst.executeUpdate();
				pst.close();
				
				session.setAttribute(MESSAGE,SUCCESSM +"Quote by <b>"+getStrQuoteBy()+" added Successfully."+ END );
				
				if(hmResourceName != null){
					
					Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
					Set<String> empIdSet  = hmResourceName.keySet();
					Iterator<String> it = empIdSet.iterator();
					while(it.hasNext()){
						String empId = it.next();
						String alertData = "<div style=\"float: left;\"> <b>"+hmResourceName.get(strSessionEmpId)+"</b> posted quote by <b><div style=\"font-size:11px;font-style:bold;\"> "+getStrQuoteBy()+"</b></div></div>";
						String alertAction = "Hub.action?pType=WR&type=Q";
						UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
						userAlerts.setStrDomain(strDomain);
						userAlerts.setStrEmpId(empId);
						userAlerts.setStrData(alertData);
						userAlerts.setStrAction(alertAction);
						userAlerts.setCurrUserTypeID(hmUserTypeId.get(EMPLOYEE));
						userAlerts.setStatus(INSERT_WR_ALERT);
						Thread t = new Thread(userAlerts);
						t.run();
						
//						String strData = "<div style=\"float: left;\"> <b>"+hmResourceName.get(strSessionEmpId)+"</b> posted quote by <b><div style=\"font-size:11px;font-style:bold;\"> "+getStrQuoteBy()+"</b></div></div>";
//						UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//						userAlerts.setStrDomain(strDomain);
//						userAlerts.set_type(NEWS_AND_ALERTS);
//						userAlerts.insertTRUserAlerts(con,CF,uF,empId,strData, "Hub.action?type=Q");
					}
				}
				
				
			}
			
					
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}
	
	public void viewFAQ(UtilityFunctions UF) {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		//Map<String,String> hmFaqs = new LinkedHashMap<String,String>();
		
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFaqSection = new LinkedHashMap<String, String>();
			pst = con.prepareStatement("select * from faq_details order by section_id desc");
			rs = pst.executeQuery();
			while(rs.next()) {
				hmFaqSection.put(rs.getString("section_id"),rs.getString("section_name"));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmFaqSection", hmFaqSection);
			
			Map<String, List<List<String>>> hmFaqs = new LinkedHashMap<String, List<List<String>>>();
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from faq_details order by faq_id desc ");
			pst = con.prepareStatement(sbQuery.toString());
			//System.out.println("select pst ===>> " + pst);
			rs = pst.executeQuery();
			while(rs.next()) {
				List<List<String>> alFaqs = hmFaqs.get(rs.getString("section_id"));
				if(alFaqs ==null) alFaqs = new ArrayList<List<String>>();
				List<String> alInner = new ArrayList<String>();
				alInner.add(Integer.toString(rs.getInt("faq_id")));//0
				alInner.add(rs.getString("faq_question"));//3
				alInner.add(rs.getString("faq_answer"));//4
				alInner.add(rs.getString("section_name"));//5
				alFaqs.add(alInner);
				hmFaqs.put(rs.getString("section_id"), alFaqs);
				
			}
			
			rs.close();
			pst.close();
				
			//System.out.print("hmfads : "+hmFaqs);
			request.setAttribute("hmFaqs", hmFaqs);
				
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	public Map<String, List<String>> viewQuotes(UtilityFunctions uF){
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		Map<String, List<String>> hmQuotes = new LinkedHashMap<String, List<String>>();
		
		try {
			
			con = db.makeConnection(con);
			Map<String, String> hmQuoteIds = new LinkedHashMap<String, String>();
			
			List<List<String>> quotesList = new ArrayList<List<String>>();
			Map<String, String> hmResourceName = CF.getEmpNameMap(con, null, null);
			
			Map<String, String> hmCustName = CF.getCustomerNameMap(con);
			Map<String, String> hmResourceImage = CF.getEmpProfileImage(con);
			Map<String, String> hmCustImage = CF.getCustomerImageMap(con);
			
			pst = con.prepareStatement("select * from daythoughts order by thought_id desc");
			rs = pst.executeQuery();
			StringBuilder sbQuoteIds = null;
			List<String> alQuoteIds = new ArrayList<String>();
			
			while(rs.next()) {
				
				alQuoteIds.add(rs.getString("thought_id"));
				hmQuoteIds.put(rs.getString("thought_id"), rs.getString("thought_id"));
				if(sbQuoteIds == null) {
					sbQuoteIds = new StringBuilder();
					sbQuoteIds.append(rs.getString("thought_id"));
				} else{
					sbQuoteIds.append(","+rs.getString("thought_id"));
				}
				
				setLastQuoteId(rs.getString("thought_id"));
				if(alQuoteIds.size() == 10) {
					break;
				}
			}
			rs.close();
			pst.close();
			request.setAttribute("hmQuoteIds", hmQuoteIds);
		//	System.out.println("sbQuoteIds ===>> " + sbQuoteIds);
			
			if(sbQuoteIds != null) {
				StringBuilder sbQuery = new StringBuilder(); 
				sbQuery.append("select * from daythoughts where  thought_id in ("+sbQuoteIds.toString()+") order by thought_id desc ");
				pst = con.prepareStatement(sbQuery.toString());
//				System.out.println("pst ===>> " + pst);
				rs = pst.executeQuery();
				while(rs.next()) {
					List<String> alInner = new ArrayList<String>();
					alInner.add(Integer.toString(rs.getInt("thought_id")));//0
					alInner.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));//1
					
					alInner.add(rs.getString("thought_by"));//2
					alInner.add(rs.getString("thought_text"));//3
					String addedBy = rs.getString("added_by");
					alInner.add(uF.showData(hmResourceName.get(rs.getString("added_by")), "Someone"));//4
					
					alInner.add(uF.getDateFormat(rs.getString("posted_date"),DBTIMESTAMP, DATE_FORMAT_STR+" "+TIME_FORMAT_AM_PM));//5
					alInner.add(rs.getString("added_by"));//6
					
					if(strUserType != null && strUserType.equals(CUSTOMER)) {
						String createdByImage1 = hmCustImage.get(rs.getString("added_by"));
						String strClientId = CF.getClientIdBySPOCId(con, uF, rs.getString("added_by"));
						
						String MYImage1 = "<img class='img-circle img-sm' alt=\"User Image\" style=\"width: 40px !important;height: 40px !important;\" src=\"userImages/avatar_photo.png\" />"; 
						if(CF.getStrDocRetriveLocation()==null) { 
							MYImage1 = "<img class='img-circle img-sm' alt=\"User Image\" style=\"width: 40px !important;height: 40px !important;\" src=\"userImages/avatar_photo.png\" data-original=\""+DOCUMENT_LOCATION + createdByImage1+"\" />";
					  	} else if(createdByImage1 != null && !createdByImage1.equals("")) {
					  		MYImage1= "<img class='img-circle img-sm' alt=\"User Image\" style=\"width: 40px !important;height: 40px !important;\" src=\"userImages/avatar_photo.png\" data-original=\""+CF.getStrDocRetriveLocation() +I_CUSTOMER+"/"+strClientId+"/"+I_IMAGE+"/"+I_CUSTOMER_SPOC+"/"+I_22x22+"/"+createdByImage1+"\" />";
			            }
						alInner.add(MYImage1);//7
					} else {
						String createdByImage1 = hmResourceImage.get(rs.getString("added_by"));
						String MYImage1 = "<img class='img-circle img-sm' alt=\"User Image\" style=\"width: 40px !important;height: 40px !important;\" src=\"userImages/avatar_photo.png\" />"; 
						if(CF.getStrDocRetriveLocation()==null) { 
							MYImage1 = "<img class='img-circle img-sm' alt=\"User Image\" style=\"width: 40px !important;height: 40px !important;\" src=\"userImages/avatar_photo.png\" data-original=\""+DOCUMENT_LOCATION + createdByImage1+"\" />";
						} else if(createdByImage1 != null && !createdByImage1.equals("")) {
					  		MYImage1 = "<img class='img-circle img-sm' alt=\"User Image\" style=\"width: 40px !important;height: 40px !important;\" src=\"userImages/avatar_photo.png\" data-original=\""+CF.getStrDocRetriveLocation() +I_PEOPLE+"/"+I_IMAGE+"/"+rs.getString("added_by")+"/"+I_22x22+"/"+createdByImage1+"\" />";
						}
						
						alInner.add(MYImage1);//7
					}
					hmQuotes.put(rs.getString("thought_id"), alInner);
				}
				rs.close();
				pst.close();
			}
			request.setAttribute("hmQuotes", hmQuotes);

			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		return hmQuotes;
	}
	public Map<String, List<String>> viewEvents(UtilityFunctions uF){
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		Map<String, List<String>> hmEvents = new LinkedHashMap<String, List<String>>();
		try {

			List<List<String>> eventList = new ArrayList<List<String>>();
			con = db.makeConnection(con);
			
			Map<String, String> hmEventIds = new LinkedHashMap<String, String>();
			Map<String, String> hmResourceName = CF.getEmpNameMap(con, null, null);
			String curr_date=uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT);
			
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			Map<String, String> hmLevelMap = getLevelNameMap(con, uF);
			
			String logUserLevel = hmEmpLevelMap.get(strSessionEmpId);

			Map<String, String> hmResourceImage = CF.getEmpProfileImage(con);
			Map<String, String> hmCustImage = CF.getCustomerImageMap(con);
			
			
			
			if(strUserType.equals(ADMIN)){
				pst = con.prepareStatement("select * from events order by event_id desc");
			}else {
				pst = con.prepareStatement("select * from events where sharing_level is null or length(trim(sharing_level))=0 or sharing_level like '%,"+logUserLevel+",%' or added_by= ("+strSessionEmpId+") order by event_id desc");
			}
//			System.out.println("pst Event Count========>>> " + pst);
			rs = pst.executeQuery();
			StringBuilder sbEventIds = null;
			List<String> alEventIds = new ArrayList<String>();
		//	int eventsCount=0;
			while(rs.next()) {
			//	eventsCount++;
				alEventIds.add(rs.getString("event_id"));
				hmEventIds.put(rs.getString("event_id"), rs.getString("event_id"));
				if(sbEventIds == null) {
					sbEventIds = new StringBuilder();
					sbEventIds.append(rs.getString("event_id"));
				} else{
					sbEventIds.append(","+rs.getString("event_id"));
				}
				
				setLastEventId(rs.getString("event_id"));
				/*if(alEventIds.size() == 10) {
					break;
				}*/
				
			}
//			System.out.println("eventId==>"+lastEventId);
			rs.close();
			pst.close();
		//	request.setAttribute("eventsCount", eventsCount);
			request.setAttribute("hmEventIds", hmEventIds);
//			System.out.println("sbEventIds ===>> " + sbEventIds);
			
			if(sbEventIds != null) {
				
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select * from events where  event_id in ("+sbEventIds.toString()+") order by event_id desc ");
				pst = con.prepareStatement(sbQuery.toString());
//				System.out.println("pst ===>> " + pst);
				rs = pst.executeQuery();
				while(rs.next()) {
					List<String> alInner = new ArrayList<String>();
					alInner.add(Integer.toString(rs.getInt("event_id")));//0
					if(rs.getString("event_date")!=null && !rs.getString("event_date").equals("")){
						alInner.add(uF.getDateFormat(rs.getString("event_date"), DBDATE, CF.getStrReportDateFormat()));//1
					}else{
						alInner.add("");//1
					}
					
					if(rs.getString("event_end_date")!=null && !rs.getString("event_end_date").equals("")){
						alInner.add(uF.getDateFormat(rs.getString("event_end_date"), DBDATE, CF.getStrReportDateFormat()));//2
					}else{
						alInner.add("");//2
					}
					
					alInner.add(uF.getDateFormat(rs.getString("posted_date"),DBTIMESTAMP, DATE_FORMAT_STR+" "+TIME_FORMAT_AM_PM));//3
					alInner.add(rs.getString("event_title"));//4
					alInner.add(rs.getString("event_desc"));//5
					alInner.add(uF.showData(hmResourceName.get(rs.getString("added_by")),"Someone"));//6
					alInner.add(rs.getString("location"));//7
					
//					System.out.println("events==>"+rs.getString("sharing_level"));
					String sharing_level = rs.getString("sharing_level");
					String[] levels = null;
					if(sharing_level!=null && !sharing_level.equals("")){
						levels = rs.getString("sharing_level").split(",");
						StringBuilder sharing = null;
//						System.out.println("level's=>"+levels.length);
						for(String lvlId : levels) {
							
							if(uF.parseToInt(lvlId)>0) {
								if(sharing == null) {
									sharing = new StringBuilder();
									sharing.append(hmLevelMap.get(lvlId));
								} else {
									sharing.append(", "+hmLevelMap.get(lvlId));
								}
							}
						}
						if(sharing == null) {
							sharing = new StringBuilder();
						}
						alInner.add(sharing.toString());//8
					}else{					
						alInner.add("All Level's");//8
					}
					
					alInner.add(rs.getString("event_image"));//9
					alInner.add(rs.getString("added_by"));//10
					String extenstion = null;
					if(rs.getString("event_image") !=null && !rs.getString("event_image").trim().equals("")){
						extenstion = FilenameUtils.getExtension(rs.getString("event_image").trim());
					}
					alInner.add(extenstion);//11
//					
					String eventImgPath = "";
					if(rs.getString("event_image")!=null && !rs.getString("event_image").equals("")){
						if(CF.getStrDocSaveLocation()==null){
							eventImgPath =  DOCUMENT_LOCATION +"/"+rs.getString("added_by")+"/"+rs.getString("event_image") ;
						} else {
							eventImgPath = CF.getStrDocRetriveLocation()+I_EVENTS+"/"+rs.getString("added_by")+"/"+rs.getString("event_image");
	
						}
					}else {
						eventImgPath="userImages/event_icon.png";
					}
					//System.out.println("eventImg==>"+eventImgPath);
					String eventImage = "<img class='attachment-img' alt=\"Attachment Image\" src=\""+eventImgPath+"\" data-original=\""+eventImgPath+"\" />";
					alInner.add(eventImage); //12
					
					String addedImage = "<img class='lazy' border=\"0\" style=\"max-height: 160px; max-width: 100%; border: 1px solid #CCCCCC; padding: 5px;\" src=\"userImages/avatar_photo.png\" />"; 
					alInner.add(addedImage);//13
					String from_time = rs.getString("from_time");
					String to_time = rs.getString("to_time");
					
					if(from_time != null && !from_time.equals("")){
						
						alInner.add(uF.getTimeFormatStr(from_time.substring(0,from_time.lastIndexOf(":")),DBTIME,TIME_FORMAT_AM_PM));
					}else{
						alInner.add("");//14
					}
					if(to_time != null  && !to_time.equals("")){
						
						alInner.add(uF.getTimeFormatStr(to_time.substring(0,to_time.lastIndexOf(":")),DBTIME,TIME_FORMAT_AM_PM));
					}else{
						alInner.add("");//15
					}
					
					if(strUserType != null && strUserType.equals(CUSTOMER)) {
						String createdByImage1 = hmCustImage.get(rs.getString("added_by"));
						String strClientId = CF.getClientIdBySPOCId(con, uF, rs.getString("added_by"));
						String MYImage1 = "<img class='lazy img-circle' border=\"0\" height=\"30\" width=\"30\" src=\"userImages/avatar_photo.png\" />"; 
						if(CF.getStrDocRetriveLocation()==null) { 
							MYImage1 = "<img class='lazy img-circle' border=\"0\" height=\"30\" width=\"30\" src=\"userImages/avatar_photo.png\" data-original=\""+DOCUMENT_LOCATION + createdByImage1+"\" />";
					  	} else if(createdByImage1 != null && !createdByImage1.equals("")) {
					  		MYImage1= "<img class='lazy img-circle' border=\"0\" height=\"30\" width=\"30\" src=\"userImages/avatar_photo.png\" data-original=\""+CF.getStrDocRetriveLocation() +I_CUSTOMER+"/"+strClientId+"/"+I_IMAGE+"/"+I_CUSTOMER_SPOC+"/"+I_22x22+"/"+createdByImage1+"\" />";
			            }
						alInner.add(MYImage1);//16
					} else {
						String createdByImage1 = hmResourceImage.get(rs.getString("added_by"));
						String MYImage1 = "<img class='lazy img-circle' border=\"0\" height=\"30\" width=\"30\" src=\"userImages/avatar_photo.png\" />"; 
						if(CF.getStrDocRetriveLocation()==null) { 
							MYImage1 = "<img class='lazy img-circle' border=\"0\" height=\"30\" width=\"30\" src=\"userImages/avatar_photo.png\" data-original=\""+DOCUMENT_LOCATION + createdByImage1+"\" />";
						} else if(createdByImage1 != null && !createdByImage1.equals("")) {
					  		MYImage1 = "<img class='lazy img-circle' border=\"0\" height=\"30\" width=\"30\" src=\"userImages/avatar_photo.png\" data-original=\""+CF.getStrDocRetriveLocation() +I_PEOPLE+"/"+I_IMAGE+"/"+rs.getString("added_by")+"/"+I_22x22+"/"+createdByImage1+"\" />";
						}
						//request.setAttribute("MYImg1", MYImage1);
						alInner.add(MYImage1);//16
					}
					
					alInner.add(eventImgPath); //17
					
					String strStatus = "Upcoming";
					java.sql.Date nowDate = uF.getDateFormat(curr_date, DATE_FORMAT);
					java.sql.Date startDate  = null;
					if(rs.getString("event_date")!= null){
						startDate = uF.getDateFormat(rs.getString("event_date"), DBDATE);
					}
					boolean upComing =false;
					if(startDate!= null && startDate.after(nowDate)){
						upComing = true;
						int diffInDays = uF.parseToInt(uF.dateDifference(""+nowDate, DBDATE, startDate+"", DBDATE)) -1;
						if(diffInDays == 1) {
							strStatus = "<span class=\"label label-warning\"><i class=\"fa fa-clock-o\"></i>"+diffInDays+ " day remaining </span>";
						}else {
							strStatus = "<span class=\"label label-warning\"><i class=\"fa fa-clock-o\"></i>"+diffInDays+ " days remaining </span>";
						}
					}
					
					
					java.sql.Date endDate  = null;
					if(rs.getString("event_end_date")!= null){
						endDate = uF.getDateFormat(rs.getString("event_end_date"), DBDATE);
					}
					
					boolean expired =false;
					
					if(endDate!= null && endDate.before(nowDate)){
						expired = true;
					}
					
					boolean isLive = uF.isDateBetween(uF.getDateFormat(rs.getString("event_date"), DBDATE), uF.getDateFormat(rs.getString("event_end_date"), DBDATE), uF.getDateFormat(curr_date, DATE_FORMAT));
					 /*String strLive = "<img border=\"0\" style=\"width: 14px !important; height: 14px !important;margin-left: 5px;margin-right: 5px;\" src=\"images1/icons/pending.png\" title=\"Upcoming\">";*/
					String strLive = "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\" title=\"Upcoming\" style=\"width: 14px !important; height: 14px !important;margin-left: 5px;margin-right: 5px;\"></i>";
					if(isLive){
						strStatus = "<span class=\"label label-success\"><i class=\"fa fa-clock-o\"></i>Ongoing</span>";
						 /*strLive = "<img border=\"0\" style=\"width: 14px !important; height: 14px !important;margin-left: 5px;margin-right: 5px;\" src=\"images1/icons/approved.png\" title=\"Ongoing\">";*/
						strLive = "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d;width: 14px !important; height: 14px !important;margin-left: 5px;margin-right: 5px;\" title=\"Ongoing\"></i>";
					} else if(expired){
						strStatus = "<span class=\"label label-default\"><i class=\"fa fa-clock-o\"></i>Past event</span>";
					    /*strLive = "<img border=\"0\" style=\"width: 14px !important; height: 14px !important;margin-left: 5px;margin-right: 5px;\" src=\"images1/icons/denied.png\" title=\"Past \">";*/
						strLive = "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25;width: 14px !important; height: 14px !important;margin-left: 5px;margin-right: 5px;\" title=\"Past \"></i>";
					}
					
					alInner.add(strStatus); //18
					alInner.add(strLive);//19
					hmEvents.put(rs.getString("event_id"), alInner);
					
				}
				rs.close();
				pst.close();
				
			}
			
			List<String> availableExt = CF.getAvailableExtention();
			request.setAttribute("availableExt", availableExt);
			request.setAttribute("hmEvents", hmEvents);

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		return hmEvents;
	}
	
	
	public Map<String, List<String>>  viewNotice(UtilityFunctions uF){
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		Map<String, List<String>> hmNotices = new LinkedHashMap<String, List<String>>();
		try {

			List<List<String>> al = new ArrayList<List<String>>();
			
			con = db.makeConnection(con);
			
			Map<String, String> hmNoticeIds = new LinkedHashMap<String, String>();
			
			
			Map<String, String> hmResourceName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmCustName = CF.getCustomerNameMap(con);
			Map<String, String> hmResourceImage = CF.getEmpProfileImage(con);
			Map<String, String> hmCustImage = CF.getCustomerImageMap(con);
			
			String curr_date=uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT);
			if(strUserType.equals(ADMIN)){
				pst = con.prepareStatement("select * from notices order by notice_id desc");
			}else if(strUserType.equals(HRMANAGER)){
				pst = con.prepareStatement("select * from notices where added_by = ("+strSessionEmpId+") or ispublish='t' order by notice_id desc");
			}else{
				pst = con.prepareStatement("select * from notices where ispublish='t' and  display_date <= '"+uF.getDateFormat(curr_date, DATE_FORMAT)+"' and display_end_date >= '"+uF.getDateFormat(curr_date, DATE_FORMAT)+"' order by notice_id desc");
			}
//			System.out.println("pst ========>>> " + pst);
			rs = pst.executeQuery();
			StringBuilder sbNoticeIds = null;
			List<String> alNoticeIds = new ArrayList<String>();
			
			while(rs.next()) {
				
				alNoticeIds.add(rs.getString("notice_id"));
				hmNoticeIds.put(rs.getString("notice_id"), rs.getString("notice_id"));
				if(sbNoticeIds == null) {
					sbNoticeIds = new StringBuilder();
					sbNoticeIds.append(rs.getString("notice_id"));
				} else{
					sbNoticeIds.append(","+rs.getString("notice_id"));
				}
				
				setLastNoticeId(rs.getString("notice_id"));
				if(alNoticeIds.size() == 10) {
						break;
				}
				
			}
			rs.close();
			pst.close();
			request.setAttribute("hmNoticeIds", hmNoticeIds);
//			System.out.println("sbNoticeIds ===>> " + sbNoticeIds);
			
			if(sbNoticeIds != null) {
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select * from notices where  notice_id in ("+sbNoticeIds.toString()+") order by notice_id desc ");
				pst = con.prepareStatement(sbQuery.toString());
//				System.out.println("pst ===>> " + pst);
				rs = pst.executeQuery();
				while(rs.next()) {
					List<String> alInner = new ArrayList<String>();
					alInner.add(Integer.toString(rs.getInt("notice_id")));//0
					if(rs.getString("display_date")!=null && !rs.getString("display_date").equals("")){
						alInner.add(uF.getDateFormat(rs.getString("display_date"), DBDATE, CF.getStrReportDateFormat()));//1
					}else{
						alInner.add("");//1
					}
					
					if(rs.getString("display_end_date")!=null && !rs.getString("display_end_date").equals("")){
						alInner.add(uF.getDateFormat(rs.getString("display_end_date"), DBDATE, CF.getStrReportDateFormat()));//2
					}else{
						alInner.add("");//2
					}
					alInner.add(rs.getString("heading"));//3
					alInner.add(rs.getString("content"));//4
					alInner.add(rs.getString("ispublish"));//5
					alInner.add(rs.getString("added_by"));//6
					alInner.add(uF.getDateFormat(rs.getString("posted_date"),DBTIMESTAMP, DATE_FORMAT_STR+" "+TIME_FORMAT_AM_PM));//7
					alInner.add(uF.showData(hmResourceName.get(rs.getString("added_by")),"Someone"));//8
				
					String strContent="";
					if(rs.getString("content")!=null){
						strContent = rs.getString("content");
						strContent = strContent.replaceAll("\n", "<br/>");
					}
					alInner.add("<a href=\"javascript:$.modaldialog.success('"+strContent+"', { title: '"+rs.getString("heading")+"' });\">Read Full Notice</a>" );//9
				
					String strPublish = "Unpublished";
					if(uF.parseToBoolean(rs.getString("ispublish"))){
						strPublish = "Published";
					} 
					alInner.add(strPublish);//10
					
					java.sql.Date endDate  = null;
					if(rs.getString("display_end_date")!= null){
						endDate = uF.getDateFormat(rs.getString("display_end_date"), DBDATE);
					}
					java.sql.Date nowDate = uF.getDateFormat(curr_date, DATE_FORMAT);
					boolean expired =false;
					if(endDate!= null && endDate.before(nowDate)){
						expired = true;
					}
					
					boolean isLive = uF.isDateBetween(uF.getDateFormat(rs.getString("display_date"), DBDATE), uF.getDateFormat(rs.getString("display_end_date"), DBDATE), uF.getDateFormat(curr_date, DATE_FORMAT));
					 /*String strLive = "<img border=\"0\" style=\"width: 14px !important; height: 14px !important;margin-left: 5px;margin-right: 5px;\" src=\"images1/icons/pending.png\" title=\"Waiting for Live\">";*/
					String strLive = "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5;width: 14px !important; height: 14px !important;margin-left: 5px;margin-right: 5px;\" title=\"Waiting for Live\"></i>";
					if(isLive && uF.parseToBoolean(rs.getString("ispublish"))){
						/*strLive = "<img border=\"0\" style=\"width: 14px !important; height: 14px !important;margin-left: 5px;margin-right: 5px;\" src=\"images1/icons/approved.png\" title=\"Published And Live\">";*/
						strLive = "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d;width: 14px !important; height: 14px !important;margin-left: 5px;margin-right: 5px;\" title=\"Published And Live\"></i>";
					} else if(!uF.parseToBoolean(rs.getString("ispublish"))){
						/*strLive = "<img border=\"0\" style=\"width: 14px !important; height: 14px !important;margin-left: 5px;margin-right: 5px;\" src=\"images1/icons/re_submit.png\" title=\"Unpublished\">";*/
						strLive = "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\" style=\"margin-left: 5px;margin-right: 5px;\" title=\"Unpublished\"></i>";
					}else if(expired && uF.parseToBoolean(rs.getString("ispublish"))){
					    /*strLive = "<img border=\"0\" style=\"width: 14px !important; height: 14px !important;margin-left: 5px;margin-right: 5px;\" src=\"images1/icons/denied.png\" title=\"Closed \">";*/
						strLive = "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25;width: 14px !important; height: 14px !important;margin-left: 5px;margin-right: 5px;\" title=\"Closed \"></i>";
					}
					
					alInner.add(strLive);//11
					if(strUserType != null && strUserType.equals(CUSTOMER)) {
						String createdByImage1 = hmCustImage.get(rs.getString("added_by"));
						String strClientId = CF.getClientIdBySPOCId(con, uF, rs.getString("added_by"));
						String MYImage1 = "<img class='lazy img-circle' border=\"0\" height=\"30\" width=\"30\" src=\"userImages/avatar_photo.png\" />"; 
						if(CF.getStrDocRetriveLocation()==null) { 
							MYImage1 = "<img class='lazy img-circle' border=\"0\" height=\"30\" width=\"30\" src=\"userImages/avatar_photo.png\" data-original=\""+DOCUMENT_LOCATION + createdByImage1+"\" />";
					  	} else if(createdByImage1 != null && !createdByImage1.equals("")) {
					  		MYImage1= "<img class='lazy img-circle' border=\"0\" height=\"30\" width=\"30\" src=\"userImages/avatar_photo.png\" data-original=\""+CF.getStrDocRetriveLocation() +I_CUSTOMER+"/"+strClientId+"/"+I_IMAGE+"/"+I_CUSTOMER_SPOC+"/"+I_22x22+"/"+createdByImage1+"\" />";
			            }
						alInner.add(MYImage1);//12
					} else {
						String createdByImage1 = hmResourceImage.get(rs.getString("added_by"));
						String MYImage1 = "<img class='lazy img-circle' border=\"0\" height=\"30\" width=\"30\" src=\"userImages/avatar_photo.png\" />"; 
						if(CF.getStrDocRetriveLocation()==null) { 
							MYImage1 = "<img class='lazy img-circle' border=\"0\" height=\"30\" width=\"30\" src=\"userImages/avatar_photo.png\" data-original=\""+DOCUMENT_LOCATION + createdByImage1+"\" />";
						} else if(createdByImage1 != null && !createdByImage1.equals("")) {
					  		MYImage1 = "<img class='lazy img-circle' border=\"0\" height=\"30\" width=\"30\" src=\"userImages/avatar_photo.png\" data-original=\""+CF.getStrDocRetriveLocation() +I_PEOPLE+"/"+I_IMAGE+"/"+rs.getString("added_by")+"/"+I_22x22+"/"+createdByImage1+"\" />";
						}
						//request.setAttribute("MYImg1", MYImage1);
						alInner.add(MYImage1);//12
					}
					
					hmNotices.put(rs.getString("notice_id"), alInner);
				}
				rs.close();
				pst.close();
				
			}
			request.setAttribute("hmNotices", hmNotices);

			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return hmNotices;
	}
	
	public void viewProfile(UtilityFunctions uF, String strEmpIdReq) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		List<List<String>> alEducation;
		try {

			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
			request.setAttribute("hmFeatureStatus", hmFeatureStatus);
			
			EmpDashboardData dashboardData = new EmpDashboardData(request, session, CF, uF, con, strEmpIdReq);
			dashboardData.getPosition();
			dashboardData.getMyTeam();
			
			dashboardData.getDayThought();
			dashboardData.getResignationStatus();
			dashboardData.getMailCount();
			dashboardData.getBirthdays();
			dashboardData.getWorkAnniversary();		//added by parvez date: 28-10-2022
			
			
			CF.getAlertUpdates(CF, strEmpIdReq, request, strUserType); 
			
			Map<String, String> hmEmpProfile = CF.getEmpProfileDetail(con, request, session, CF, uF, strUserType, strEmpIdReq);
			
			String strPath = CF.getStrDocRetriveLocation() +I_PEOPLE+"/"+I_IMAGE+"/"+strSessionEmpId+"/"+I_100x100+"/"+hmEmpProfile.get("IMAGE");
			String strSavePath = CF.getStrDocSaveLocation() +I_PEOPLE+"/"+I_IMAGE+"/"+strSessionEmpId+"/"+I_100x100+"/"+hmEmpProfile.get("IMAGE");
			
			boolean flag = getFileCheckOnFilePath(strSavePath);
			if(!flag) {
				strPath = "userImages/avatar_photo.png";
			}
			request.setAttribute("LoginUserImage", strPath);
			
			CF.getElementList(con, request);
			CF.getAttributes(con, request, strEmpIdReq);
			
			List<List<String>> alSkills = new ArrayList<List<String>>();
			alSkills = CF.selectSkills(con, uF.parseToInt(strEmpIdReq));
			
			request.setAttribute(TITLE, "Hub");
			
			request.setAttribute("alSkills", alSkills);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	
	private boolean getFileCheckOnFilePath(String strPath) {
		boolean flag = false;
		//System.out.println("strPath ===>> " + strPath);
		File f = new File(strPath);
		if(f.exists() && !f.isDirectory()) { 
		    flag = true;
//		    System.out.println("flag ===>> " + flag);
		}
		//System.out.println("flag out ===>> " + flag);
		return flag;
	}
	
	private void deleteUserAlerts() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
//		int nEmpId = uF.parseToInt((String)session.getAttribute(EMPID));
		int nEmpId = uF.parseToInt(strSessionEmpId);
		try {
			con = db.makeConnection(con);
			
			String strDomain = request.getServerName().split("\\.")[0];
			UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
			userAlerts.setStrDomain(strDomain);
			userAlerts.setStrEmpId(""+nEmpId);
			userAlerts.setAlertID(getAlertID());
			userAlerts.setStatus(DELETE_WR_ALERT);
			Thread t = new Thread(userAlerts);
			t.run();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	private HttpServletRequest request;

	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}

	
	public String getAllQuotesCount() {
		return allQuotesCount;
	}


	public void setAllQuotesCount(String allQuotesCount) {
		this.allQuotesCount = allQuotesCount;
	}


	public String getLastQuoteId() {
		return lastQuoteId;
	}


	public void setLastQuoteId(String lastQuoteId) {
		this.lastQuoteId = lastQuoteId;
	}
	


	public String getAllEventsCount() {
		return allEventsCount;
	}


	public void setAllEventsCount(String allEventsCount) {
		this.allEventsCount = allEventsCount;
	}


	public String getLastEventId() {
		return lastEventId;
	}


	public void setLastEventId(String lastEventId) {
		this.lastEventId = lastEventId;
	}


	public String getAllNoticesCount() {
		return allNoticesCount;
	}


	public void setAllNoticesCount(String allNoticesCount) {
		this.allNoticesCount = allNoticesCount;
	}


	public String getLastNoticeId() {
		return lastNoticeId;
	}


	public void setLastNoticeId(String lastNoticeId) {
		this.lastNoticeId = lastNoticeId;
	}
	

	public String getQuotePost() {
		return quotePost;
	}


	public void setQuotePost(String quotePost) {
		this.quotePost = quotePost;
	}


	public String getStrQuotedesc() {
		return strQuotedesc;
	}


	public void setStrQuotedesc(String strQuotedesc) {
		this.strQuotedesc = strQuotedesc;
	}


	public String getStrQuoteBy() {
		return strQuoteBy;
	}


	public void setStrQuoteBy(String strQuoteBy) {
		this.strQuoteBy = strQuoteBy;
	}
	

	public String getStrEventName() {
		return strEventName;
	}


	public void setStrEventName(String strEventName) {
		this.strEventName = strEventName;
	}


	public String getStrSharing() {
		return strSharing;
	}


	public void setStrSharing(String strSharing) {
		this.strSharing = strSharing;
	}


	public String getStrEventdesc() {
		return strEventdesc;
	}


	public void setStrEventdesc(String strEventdesc) {
		this.strEventdesc = strEventdesc;
	}


	public String getStrLocation() {
		return strLocation;
	}


	public void setStrLocation(String strLocation) {
		this.strLocation = strLocation;
	}


	public String getStrStartDate() {
		return strStartDate;
	}


	public void setStrStartDate(String strStartDate) {
		this.strStartDate = strStartDate;
	}


	public String getStrEndDate() {
		return strEndDate;
	}


	public void setStrEndDate(String strEndDate) {
		this.strEndDate = strEndDate;
	}


	public String getStartTime() {
		return startTime;
	}


	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}


	public String getEndTime() {
		return endTime;
	}


	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getEventPost() {
		return eventPost;
	}

	public void setEventPost(String eventPost) {
		this.eventPost = eventPost;
	}

	public File getStrEventImage() {
		return strEventImage;
	}


	public void setStrEventImage(File strEventImage) {
		this.strEventImage = strEventImage;
	}


	public String getStrEventImageFileName() {
		return strEventImageFileName;
	}


	public void setStrEventImageFileName(String strEventImageFileName) {
		this.strEventImageFileName = strEventImageFileName;
	}

	
	public String getDisplayStartDate() {
		return displayStartDate;
	}


	public void setDisplayStartDate(String displayStartDate) {
		this.displayStartDate = displayStartDate;
	}


	public String getNoticeId() {
		return noticeId;
	}


	public void setNoticeId(String noticeId) {
		this.noticeId = noticeId;
	}


	public String getHeading() {
		return heading;
	}


	public void setHeading(String heading) {
		this.heading = heading;
	}


	public String getContent() {
		return content;
	}


	public void setContent(String content) {
		this.content = content;
	}


	public String getDisplayEndDate() {
		return displayEndDate;
	}


	public void setDisplayEndDate(String displayEndDate) {
		this.displayEndDate = displayEndDate;
	}


	public String getPublish() {
		return publish;
	}


	public void setPublish(String publish) {
		this.publish = publish;
	}


	public String getNoticePost() {
		return noticePost;
	}


	public void setNoticePost(String noticePost) {
		this.noticePost = noticePost;
	}


	public String getIspublish() {
		return ispublish;
	}


	public void setIspublish(String ispublish) {
		this.ispublish = ispublish;
	}
	

	public List<FillLevel> getLevelList() {
		return levelList;
	}
	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}
	
	
	public String[] getStrLevel() {
		return strLevel;
	}
	public void setStrLevel(String[] strLevel) {
		this.strLevel = strLevel;
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

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getAlertID() {
		return alertID;
	}

	public void setAlertID(String alertID) {
		this.alertID = alertID;
	}

	public List<FillOrganisation> getOrgList() {
		return orgList;
	}

	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
	}

	public String getStrOrg() {
		return strOrg;
	}

	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}

	public String getManualId() {
		return manualId;
	}

	public void setManualId(String manualId) {
		this.manualId = manualId;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

}
