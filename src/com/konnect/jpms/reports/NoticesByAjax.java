package com.konnect.jpms.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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

public class NoticesByAjax extends ActionSupport implements ServletRequestAware, IStatements {
	/**
	 * 
	 */
	private static final long serialVersionUID = -656271662315865746L;
	HttpSession session;
	CommonFunctions CF = null;
	String strSessionEmpId;
	String strSessionOrgId;
	String strUserType;
	
	private String operation;
	private String displayStartDate;
	private String noticeId;
	private String heading;
	private String content;
	
	private String displayEndDate;
	private String publish;	
	
	private String noticePost;
	private String ispublish;
	
	private String noticeoffsetCnt;
	private String lastNoticeId;
	private String remainNotices;
	public String execute() throws Exception {
		
		session = request.getSession(true);
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);

		if(CF==null)return LOGIN;
		
		strSessionEmpId = (String)session.getAttribute(EMPID);
		strSessionOrgId = (String)session.getAttribute(ORGID);
		strUserType = (String)session.getAttribute(USERTYPE);
		UtilityFunctions uF = new UtilityFunctions();
		getLoggedEmpImage(uF);
		//System.out.println("lastNoticeId==>"+lastNoticeId+"\tnoticeoffsetCnt==>"+noticeoffsetCnt);
		getAllNotices(uF);
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
	
	public void getAllNotices(UtilityFunctions uF){
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmNoticeIds = new LinkedHashMap<String, String>();
			Map<String, List<String>> hmNotices = new LinkedHashMap<String, List<String>>();
			
			Map<String, String> hmResourceName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmResourceImage = CF.getEmpProfileImage(con);
			Map<String, String> hmCustImage = CF.getCustomerImageMap(con);
			
			String curr_date=uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT);
			
			if(strUserType.equals(ADMIN)){
				pst = con.prepareStatement("select * from notices where notice_id < "+uF.parseToInt(getLastNoticeId())+" order by notice_id desc");
			}else if(strUserType.equals(HRMANAGER)){
				pst = con.prepareStatement("select * from notices where (added_by = ("+strSessionEmpId+") or ispublish='t') and notice_id < "+uF.parseToInt(getLastNoticeId())+"order by notice_id desc");
			}else{
				pst = con.prepareStatement("select * from notices where ispublish='t' and  display_date <= '"+uF.getDateFormat(curr_date, DATE_FORMAT)+"' and display_end_date >= '"+uF.getDateFormat(curr_date, DATE_FORMAT)+"' and notice_id < "+uF.parseToInt(getLastNoticeId())+" order by notice_id desc");
			}
//			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			StringBuilder sbNoticeIds = null;
			List<String> alNoticeIds = new ArrayList<String>();
			setRemainNotices("NO");
			while(rs.next()) {
				if(alNoticeIds.size() == 10) {
					setRemainNotices("YES");
					break;
				}
				alNoticeIds.add(rs.getString("notice_id"));
				hmNoticeIds.put(rs.getString("notice_id"), rs.getString("notice_id"));
				if(sbNoticeIds == null) {
					sbNoticeIds = new StringBuilder();
					sbNoticeIds.append(rs.getString("notice_id"));
				} else{
					sbNoticeIds.append(","+rs.getString("notice_id"));
				}
				
				setLastNoticeId(rs.getString("notice_id"));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmNoticeIds", hmNoticeIds);
//			System.out.println("sbNoticeIds ===>> " + sbNoticeIds);
			
			if(sbNoticeIds != null) {
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select * from notices where  notice_id in ("+sbNoticeIds.toString()+") order by notice_id desc ");
				pst = con.prepareStatement(sbQuery.toString());
			//	System.out.println("pst ===>> " + pst);
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
					 /*String strLive = "<img border=\"0\" style=\"width: 14px !important;; height: 14px !important; \" src=\"images1/icons/pending.png\" title=\"Waiting for Live\">";*/
					String strLive = "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\" title=\"Waiting for Live\" style=\"width: 14px !important;; height: 14px !important; \"></i>";
					
					if(isLive && uF.parseToBoolean(rs.getString("ispublish"))){
						 /*strLive = "<img border=\"0\" style=\"width: 14px !important;; height: 14px !important;;\" src=\"images1/icons/approved.png\" title=\"Published And Live\">";*/
						strLive = "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d;width: 14px !important;; height: 14px !important;\" title=\"Published And Live\"></i>";
					}else if(!uF.parseToBoolean(rs.getString("ispublish"))){
						/*strLive = "<img border=\"0\" style=\"width: 14px !important;; height: 14px !important;;\" src=\"images1/icons/re_submit.png\" title=\"Unpublished\">";*/
						strLive = "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\" title=\"Unpublished\"></i>";
					}else if(expired && uF.parseToBoolean(rs.getString("ispublish"))){
					    /*strLive = "<img border=\"0\" style=\"width: 14px !important;; height: 14px !important;;\" src=\"images1/icons/denied.png\" title=\"Closed \">";*/
						strLive = "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25;width: 14px !important;; height: 14px !important;\" title=\"Closed \"></i>";
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
			//System.out.println("hmquotes==>"+hmNotices.size());
			
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
	public String getNoticeoffsetCnt() {
		return noticeoffsetCnt;
	}
	public void setNoticeoffsetCnt(String noticeoffsetCnt) {
		this.noticeoffsetCnt = noticeoffsetCnt;
	}
	public String getLastNoticeId() {
		return lastNoticeId;
	}
	public void setLastNoticeId(String lastNoticeId) {
		this.lastNoticeId = lastNoticeId;
	}
	public String getRemainNotices() {
		return remainNotices;
	}
	public void setRemainNotices(String remainNotices) {
		this.remainNotices = remainNotices;
	}
	
	
}
