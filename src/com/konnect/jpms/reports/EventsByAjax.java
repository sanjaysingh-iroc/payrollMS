package com.konnect.jpms.reports;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class EventsByAjax extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8136050867869680257L;
	HttpSession session;
	CommonFunctions CF = null;
	String strSessionEmpId;
	String strSessionOrgId;
	String strUserType;
	
	private String strEventName;
	private String strSharing;
	private String strEventdesc;
	
	private String strLocation;
	private String strStartDate;
	private String strEndDate;
	private String startTime;
	private String endTime;
	private String buttonPost;
	 
	private File strEventImage;
	private String strEventImageFileName;
	
	private String eventOffsetCnt;
	private String lastEventId;
	private String remainEvents;
	public String execute() throws Exception {
		
		session = request.getSession(true);
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);

		if(CF==null)return LOGIN;
		
		strSessionEmpId = (String)session.getAttribute(EMPID);
		strSessionOrgId = (String)session.getAttribute(ORGID);
		strUserType = (String)session.getAttribute(USERTYPE);
		UtilityFunctions uF = new UtilityFunctions();
		getLoggedEmpImage(uF);
//		System.out.println("lastEventId==>"+lastEventId+"\teventoffsetCnt==>"+eventOffsetCnt);
		getAllEvents(uF);
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
				hmLevelMap.put(rs.getString("level_id"),rs.getString("level_name"));
			}
			rs.close();
			pst.close();
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		return hmLevelMap;
	}

	public void getAllEvents(UtilityFunctions uF){
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmEventIds = new LinkedHashMap<String, String>();
			Map<String, List<String>> hmEvents = new LinkedHashMap<String, List<String>>();
			Map<String, String> hmCustImage = CF.getCustomerImageMap(con);
			Map<String, String> hmResourceName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmResourceImage = CF.getEmpProfileImage(con);
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			Map<String, String> hmLevelMap = getLevelNameMap(con, uF);
			String logUserLevel = hmEmpLevelMap.get(strSessionEmpId);
//			System.out.println("empLevel==>"+logUserLevel);
			String curr_date=uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT);
			
			if(strUserType.equals(ADMIN)){
				pst = con.prepareStatement("select * from events where event_id < "+uF.parseToInt(getLastEventId())+" order by event_id desc");
			}else{
				pst = con.prepareStatement("select * from events where (sharing_level is null or length(trim(sharing_level))=0 or sharing_level like '%,"+logUserLevel+",%' or added_by = ("+strSessionEmpId+")) and event_id < "+uF.parseToInt(getLastEventId())+"order by event_id desc");
			}
//			System.out.println("pst ========>>> " + pst);
			rs = pst.executeQuery();
			StringBuilder sbEventIds = null;
			List<String> alEventIds = new ArrayList<String>();
			setRemainEvents("NO");
			while(rs.next()) {
				if(alEventIds.size() == 10) {
					setRemainEvents("YES");
					break;
				}
				
				alEventIds.add(rs.getString("event_id"));
				hmEventIds.put(rs.getString("event_id"), rs.getString("event_id"));
				if(sbEventIds == null) {
					sbEventIds = new StringBuilder();
					sbEventIds.append(rs.getString("event_id"));
				} else{
					sbEventIds.append(","+rs.getString("event_id"));
				}
				
				setLastEventId(rs.getString("event_id"));
			
			}
			rs.close();
			pst.close();
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
					
					String eventImgPath="";
					if(rs.getString("event_image")!=null && !rs.getString("event_image").equals("")){
						if(CF.getStrDocSaveLocation()==null){
							eventImgPath =  DOCUMENT_LOCATION +"/"+rs.getString("added_by")+"/"+rs.getString("event_image") ;
						} else {
							eventImgPath = CF.getStrDocRetriveLocation()+I_EVENTS+"/"+rs.getString("added_by")+"/"+rs.getString("event_image");

						}
					}
					//System.out.println("eventImg==>"+eventImgPath);
					String eventImage = "<img class='attachment-img' alt=\"Attachment Image\"  src=\""+eventImgPath+"\" data-original=\""+eventImgPath+"\" />";
					alInner.add(eventImage); //12
					String addedImage = "<img class='lazy' border=\"0\" style=\"max-height: 160px; max-width: 100%; border: 1px solid #CCCCCC; padding: 5px;\" src=\"userImages/avatar_photo.png\" />"; 
					alInner.add(addedImage);//13
					String MYLargeImage = "<img class='lazy img-circle' border=\"1px solid #CCCCCC\" height=\"80\" width=\"80\" src=\"userImages/avatar_photo.png\" />";
					String MYImage = "<img class='lazy img-circle' border=\"0\" height=\"25\" width=\"25\" src=\"userImages/avatar_photo.png\" />";
					String from_time = rs.getString("from_time");
					String to_time = rs.getString("to_time");
					
					if(from_time != null && !from_time.equals("")){
						
						alInner.add(uF.getTimeFormatStr(from_time.substring(0,from_time.lastIndexOf(":")),DBTIME,TIME_FORMAT_AM_PM));//14
					}else{
						alInner.add("");//14
					}
					if(to_time != null  && !to_time.equals("")){
						
						alInner.add(uF.getTimeFormatStr(to_time.substring(0,to_time.lastIndexOf(":")),DBTIME,TIME_FORMAT_AM_PM));//15
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
					String strLive = "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5;width: 14px !important; height: 14px !important;margin-left: 5px;margin-right: 5px;\"  title=\"Upcoming\"></i>";
					if(isLive){
						strStatus = "<span class=\"label label-success\"><i class=\"fa fa-clock-o\"></i>Ongoing</span>";
						 /*strLive = "<img border=\"0\" style=\"width: 14px !important; height: 14px !important;margin-left: 5px;margin-right: 5px;\" src=\"images1/icons/approved.png\"  >";*/
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
//			System.out.println("hmEvents==>"+hmEvents.size());
			
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

	public String getButtonPost() {
		return buttonPost;
	}

	public void setButtonPost(String buttonPost) {
		this.buttonPost = buttonPost;
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
	public String getEventOffsetCnt() {
		return eventOffsetCnt;
	}
	public void setEventOffsetCnt(String eventOffsetCnt) {
		this.eventOffsetCnt = eventOffsetCnt;
	}
	public String getLastEventId() {
		return lastEventId;
	}
	public void setLastEventId(String lastEventId) {
		this.lastEventId = lastEventId;
	}
	public String getRemainEvents() {
		return remainEvents;
	}
	public void setRemainEvents(String remainEvents) {
		this.remainEvents = remainEvents;
	}
	
}
