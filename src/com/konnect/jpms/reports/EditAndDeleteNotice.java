package com.konnect.jpms.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class EditAndDeleteNotice extends ActionSupport implements ServletRequestAware, IStatements {
	HttpSession session;
	CommonFunctions CF = null;
	String strSessionEmpId;
	String strSessionOrgId;
	String strUserType;
	
	private String operation;
	private String noticeId;
	
	private String displayStartDate;
	private String heading;
	private String content;
	private String displayEndDate;
	private String noticePost;
	private String ispublish;
	
	
public String execute() throws Exception {
		
		session = request.getSession(true);
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strSessionEmpId = (String)session.getAttribute(EMPID);
		strSessionOrgId = (String)session.getAttribute(ORGID);
		strUserType = (String)session.getAttribute(USERTYPE);
//		System.out.println(""+getOperation());
		UtilityFunctions uF = new UtilityFunctions();
		getLoggedEmpImage(uF);
//		setNoticeId(request.getParameter("noticeId"));
		System.out.println("getNoticeId() ===>> " + getNoticeId());
		System.out.println("getOperation() ===>> " + getOperation());
		if(getOperation() != null && getOperation().equals("E")) {
			getNoticeData();
		} else if (getOperation() != null && (getOperation().equals("U") || getOperation().equals("C"))) {
			updateNotice();
			return SUCCESS;
		} else if (getOperation() != null && getOperation().equals("D")) {
			deleteNotice();
			return SUCCESS;
		}
		return LOAD;
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
			
			Map<String, List<List<String>>> hmComments = new HashMap<String, List<List<String>>>();
			Map<String, String> hmLastCommentId = new HashMap<String, String>();
			
			if(strUserType != null && strUserType.equals(CUSTOMER)) {
				String createdByImage = hmCustImage.get(strSessionEmpId);
				String strClientId = CF.getClientIdBySPOCId(con, uF, strSessionEmpId);
	//			String MYImage = CF.getStrDocRetriveLocation()+I_CUSTOMER+"/"+strClientId+"/"+I_IMAGE+"/"+I_CUSTOMER_SPOC+"/"+I_22x22+"/"+((createdByImage!=null && !createdByImage.equals("")) ? createdByImage : "avatar_photo.png");
	//			String MYImg = "<img class=\"lazy\" src=\""+ MYImage +"\" data-original=\""+ MYImage +"\" height=\"25\" width=\"25\">";
				String MYLargeImage = "<img class='lazy img-circle' border=\"1px solid #CCCCCC\" height=\"80\" width=\"80\" src=\"userImages/avatar_photo.png\" />";
				String MYImage = "<img class='lazy img-circle' border=\"0\" height=\"30\" width=\"30\" src=\"userImages/avatar_photo.png\" />"; 
				if(CF.getStrDocRetriveLocation()==null) { 
					MYImage = "<img class='lazy img-circle' border=\"0\" height=\"30\" width=\"30\" src=\"userImages/avatar_photo.png\" data-original=\""+DOCUMENT_LOCATION + createdByImage+"\" />";
					MYLargeImage = "<img class='lazy img-circle' border=\"1px solid #CCCCCC\" height=\"80\" width=\"80\" src=\"userImages/avatar_photo.png\" data-original=\""+DOCUMENT_LOCATION + createdByImage+"\" />";
			  	} else if(createdByImage != null && !createdByImage.equals("")) {
			  		MYImage = "<img class='lazy img-circle' border=\"0\" height=\"30\" width=\"30\" src=\"userImages/avatar_photo.png\" data-original=\""+CF.getStrDocRetriveLocation() +I_CUSTOMER+"/"+strClientId+"/"+I_IMAGE+"/"+I_CUSTOMER_SPOC+"/"+I_22x22+"/"+createdByImage+"\" />";
			  		MYLargeImage = "<img class='lazy img-circle' border=\"1px solid #CCCCCC\" height=\"80\" width=\"80\" src=\"userImages/avatar_photo.png\" data-original=\""+CF.getStrDocRetriveLocation() +I_CUSTOMER+"/"+strClientId+"/"+I_IMAGE+"/"+I_CUSTOMER_SPOC+"/"+I_100x100+"/"+createdByImage+"\" />";
	            }
	//			System.out.println("MyLargeImage==>"+ MYLargeImage);
				request.setAttribute("MYLargeImg", MYLargeImage);
				request.setAttribute("MYImg", MYImage);
			} else {
				String createdByImage = hmResourceImage.get(strSessionEmpId);
	//			String MYImage = CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+strSessionEmpId+"/"+I_22x22+"/"+((createdByImage!=null && !createdByImage.equals("")) ? createdByImage : "avatar_photo.png");
	//			String MYImg = "<img class=\"lazy\" src=\""+ MYImage +"\" data-original=\""+ MYImage +"\" height=\"25\" width=\"25\">";
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

	public void updateNotice() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
				
			con = db.makeConnection(con);
			String strDomain = request.getServerName().split("\\.")[0];
			Map<String, String> hmResourceName = CF.getEmpNameMap(con, null, null);
			if(getOperation() != null && getOperation().equals("U")) {
			 	pst = con.prepareStatement("update notices set heading = ?, content = ?, _date = ?, display_date = ?,display_end_date = ?,ispublish = ?,updated_by=?,last_updated = ? where notice_id=?");
				pst.setString(1,getHeading());
				pst.setString(2,getContent());
				pst.setDate(3,uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setDate(4,uF.getDateFormat(getDisplayStartDate(), DATE_FORMAT));
				pst.setDate(5,uF.getDateFormat(getDisplayEndDate(), DATE_FORMAT));
				pst.setTimestamp(8, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
				pst.setInt(7,uF.parseToInt((String)session.getAttribute(EMPID)));
				pst.setBoolean(6,uF.parseToBoolean(getIspublish()));
				pst.setInt(9,uF.parseToInt(getNoticeId()));
				pst.executeUpdate();
				System.out.println("pst ===>> " + pst);
				pst.close();
				
				if(uF.parseToBoolean(getIspublish()) == true) {
					if(hmResourceName != null){
						Set empIdSet  = hmResourceName.keySet();
						Iterator<String> it = empIdSet.iterator();
						while(it.hasNext()){
							String empId = it.next();
							String strData = "<div style=\"float: left;\"> <b>"+hmResourceName.get(strSessionEmpId)+"</b> has updated an announcement of <b><div style=\"font-size:11px;font-style:bold;\"> "+getHeading()+"</b></div></div>";
							UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
							userAlerts.setStrDomain(strDomain);
							userAlerts.set_type(NEWS_AND_ALERTS);
							userAlerts.insertTRUserAlerts(con,CF,uF,empId,strData, "Hub.action?type=A");
						}
					}
				}
			}
			
			pst = con.prepareStatement("select * from notices where notice_id=?");
			pst.setInt(1, uF.parseToInt(getNoticeId()));
			rs = pst.executeQuery();
			
			List<String> alInner = new ArrayList<String>();
			
			Map<String, String> hmResourceImage = CF.getEmpProfileImage(con);
			Map<String, String> hmCustImage = CF.getCustomerImageMap(con);
			String curr_date=uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT);
			
			while(rs.next()){
				
				alInner.add(Integer.toString(rs.getInt("notice_id")));//0
				if(rs.getString("display_date")!=null && !rs.getString("display_date").equals("")) {
					alInner.add(uF.getDateFormat(rs.getString("display_date"), DBDATE, CF.getStrReportDateFormat()));//1
				} else {
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
				
				java.sql.Date endDate = null;
				if(rs.getString("display_end_date")!=null && !rs.getString("display_end_date").equals("")){
					endDate = uF.getDateFormat(rs.getString("display_end_date"), DBDATE);
				}
				java.sql.Date nowDate = uF.getDateFormat(curr_date, DATE_FORMAT);
				boolean expired =false;
				if(endDate!=null && endDate.before(nowDate)){
					expired = true;
				}
				boolean isLive = uF.isDateBetween(uF.getDateFormat(rs.getString("display_date"), DBDATE), uF.getDateFormat(rs.getString("display_end_date"), DBDATE), uF.getDateFormat(curr_date, DATE_FORMAT));
				 /*String strLive = "<img border=\"0\" style=\"width: 14px !important; height: 14px !important;\" src=\"images1/icons/pending.png\" title=\"Waiting for Live\">";*/
				String strLive = "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5;width: 14px !important; height: 14px !important;\"title=\"Waiting for Live\"></i>";
				if(isLive && uF.parseToBoolean(rs.getString("ispublish"))){
					 /*strLive = "<img border=\"0\" style=\"width: 14px !important; height: 14px !important;\" src=\"images1/icons/approved.png\" title=\"Published And Live\">";*/
					strLive = "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Published And Live\"></i>";
				} else if(!uF.parseToBoolean(rs.getString("ispublish"))){
					/*strLive = "<img border=\"0\" style=\"width: 14px !important; height: 14px !important;\" src=\"images1/icons/re_submit.png\" title=\"Unpublished\">";*/
					strLive = "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\" title=\"Unpublished\" style=\"width: 14px !important; height: 14px !important;\"></i>";
				}else if(expired && uF.parseToBoolean(rs.getString("ispublish"))){
				     /*strLive = "<img border=\"0\" style=\"width: 14px !important; height: 14px !important;\" src=\"images1/icons/denied.png\" title=\"Closed \">";*/
					strLive = "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25;width: 14px !important; height: 14px !important;\"title=\"Closed \" ></i>";
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
					
					alInner.add(MYImage1);//12
				}
			}
			rs.close();
			pst.close();
			request.setAttribute("alInner", alInner);
				
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public String deleteNotice() {
	
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
	
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(deleteNotice);
			pst.setInt(1, uF.parseToInt(getNoticeId()));
			pst.execute();
			pst.close();
			
			
			request.setAttribute(MESSAGE, "Notice deleted successfully!");
			
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(MESSAGE, "Error in deletion");
		}finally{
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	
	}
	
	public void getNoticeData(){
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
	//	System.out.println("get notice data");
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from notices where notice_id=?");
			pst.setInt(1, uF.parseToInt(getNoticeId()));
	//		System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			while(rs.next()) {
				setOperation("U");
				setContent(rs.getString("content"));
				setHeading(rs.getString("heading"));
				if(rs.getString("display_date")!= null && !rs.getString("display_date").equals("")){
					setDisplayStartDate(uF.getDateFormat(rs.getString("display_date"),DBDATE,DATE_FORMAT));
				}else{
					setDisplayStartDate("");
				}
				if(rs.getString("display_end_date")!=null && !rs.getString("display_end_date").equals("")){
					setDisplayEndDate(uF.getDateFormat(rs.getString("display_end_date"),DBDATE,DATE_FORMAT));
				}else{
					setDisplayEndDate("");
				}
				setIspublish(rs.getString("ispublish"));
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
	private HttpServletRequest request;


	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}


	public String getOperation() {
		return operation;
	}


	public void setOperation(String operation) {
		this.operation = operation;
	}


	public String getNoticeId() {
		return noticeId;
	}


	public void setNoticeId(String noticeId) {
		this.noticeId = noticeId;
	}


	public String getDisplayStartDate() {
		return displayStartDate;
	}


	public void setDisplayStartDate(String displayStartDate) {
		this.displayStartDate = displayStartDate;
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


	
}
