package com.konnect.jpms.reports;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
import org.apache.struts2.ServletActionContext;

import org.apache.struts2.dispatcher.StrutsRequestWrapper;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.apache.struts2.interceptor.ServletRequestAware;



import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UploadImage;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;


public class EditAndDeleteEvent extends ActionSupport implements ServletRequestAware, IStatements{

	HttpSession session;
	CommonFunctions CF = null;
	String strSessionEmpId;
	String strSessionOrgId;
	String strUserType;
	private String empId;
	
	private String strEventId;
	private String operation;
	
	private String strEventName;
	private String strSharing;
	private String strEventdesc;
	private String strLocation;
	private String strStartDate;
	private String strEndDate;
	private String startTime;
	private String endTime;
	private File strEventImage; 
	

	private String strEventName1;
	private String strSharing1;
	private String strEventdesc1;
	private String strLocation1;
	private String strStartDate1;
	private String strEndDate1;
	private String startTime1;
	private String endTime1;
	private File strEventImage1; 
	
	
	private String eventId;
	private String strEventImageFileName;
	private String submit;
	
public String execute() throws Exception {
		
		session = request.getSession(true);
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);

		if(CF==null)return LOGIN;
		
		strSessionEmpId = (String)session.getAttribute(EMPID);
		strSessionOrgId = (String)session.getAttribute(ORGID);
		strUserType = (String)session.getAttribute(USERTYPE);
		getLevelList();
		setStrEventId(request.getParameter("eventId"));
		request.setAttribute("eventId",strEventId);
		
		UtilityFunctions uF = new UtilityFunctions();
		getLoggedEmpImage(uF);
		
		getLevelNameMap(uF);
		//System.out.println("eventId==>"+getEventId()+"==>opeartion==>"+getOperation());		
		if(getSubmit()==null && getOperation() != null && getOperation().equals("E_E")) {
			
			getEventData(uF);
		} else if(getOperation() != null && getOperation().equals("E_D")) {
			deleteEvent(uF);
		} else if(getOperation() != null && (getOperation().equals("U") || getOperation().equals("C"))) {
			
			updateEventData(uF);
		}

		if(getSubmit()!=null && !getSubmit().equals("")){
		
			setOperation("U");
			updateEventData1(uF);
			
			return SUCCESS;
		}else{
		
			return LOAD;
		}
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
//				System.out.println("MyLargeImage==>"+ MYLargeImage);
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
	
	private void uploadEventImages(String eventId) {
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
	public Map<String,String> getLevelNameMap(UtilityFunctions uF){
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> hmLevelMap = new LinkedHashMap<String,String>();
		
		Database db = new Database();
		db.setRequest(request);
		try{
			con = db.makeConnection(con);
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
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		
		return hmLevelMap;
	}
	
	private void getEventData(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			
			
			Map<String, String> hmResourceImage = CF.getEmpProfileImage(con);
			Map<String, String> hmCustImage = CF.getCustomerImageMap(con);
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			Map<String, String> hmLevelMap = getLevelNameMap(uF);
			Map<String, String> hmLevelCodeMap = CF.getLevelCodeName(con, uF);
			
			Map<String, String> hmCustName = CF.getCustomerNameMap(con);
		
			
			List<String> levelList = CF.getLevelList(con, uF);
			
			List<String> levelIdList = new ArrayList<String>();
			Set levelIdSet = hmLevelCodeMap.keySet();
 			Iterator it = levelIdSet.iterator();
 			while(it.hasNext()){
 				
 				levelIdList.add((String)it.next());
 			}
			
			pst = con.prepareStatement("select * from events where event_id = ?");
			pst.setInt(1, uF.parseToInt(getStrEventId()));
			rs = pst.executeQuery();
			while(rs.next()) {
				
				setStrEventdesc(rs.getString("event_desc"));
				setStrEventName(rs.getString("event_title"));
				if(rs.getString("event_date")!= null && !rs.getString("event_date").equals("")){
					setStrStartDate(uF.getDateFormat(rs.getString("event_date"),DBDATE,DATE_FORMAT));
				}else{
					setStrStartDate("");
				}
				
				if(rs.getString("event_end_date")!= null && !rs.getString("event_end_date").equals("")){
					setStrEndDate(uF.getDateFormat(rs.getString("event_end_date"),DBDATE,DATE_FORMAT));
				}else{
					setStrEndDate("");
				}
							    
				setStrLocation(rs.getString("location"));
				
//				System.out.println(""+rs.getString("sharing_level"));
				String sharing =  rs.getString("sharing_level");
				List<String> allSharing = new ArrayList<String>();
				if(sharing != null && !sharing.equals("")) {
					allSharing = Arrays.asList(sharing.split(","));
				}
//				System.out.println("allSharing size==>"+ allSharing.size());
				StringBuilder sbSharing = new StringBuilder();
				if(allSharing.size() > 0){
					
					for(int i=0; levelIdList!=null && i<levelIdList.size(); i++) {
						if(allSharing.contains(levelIdList.get(i))) {
							sbSharing.append("<option value='"+levelIdList.get(i)+"' selected>"+hmLevelCodeMap.get(levelIdList.get(i))+"</option>");
						} else {
							sbSharing.append("<option value='"+levelIdList.get(i)+"'>"+hmLevelCodeMap.get(levelIdList.get(i))+"</option>");
						}
					}
				}else if(allSharing.size() == 0){
					sbSharing.append("<option value='"+""+"' selected>"+"All Level's"+"</option>");
					for(int i=0; levelIdList!=null && i<levelIdList.size(); i++) {
						sbSharing.append("<option value='"+levelIdList.get(i)+"'>"+hmLevelCodeMap.get(levelIdList.get(i))+"</option>");
					}
				}
				request.setAttribute("sbSharing", sbSharing.toString());
			
				String from_time = rs.getString("from_time");
				String to_time = rs.getString("to_time");
				
				if(from_time != null && !from_time.equals("")){
					setStartTime(from_time.substring(0,from_time.lastIndexOf(":")));//14
				}else{
					setStartTime("");
				}
				if(to_time != null  && !to_time.equals("")){
					setEndTime(to_time.substring(0,to_time.lastIndexOf(":")));//15
				}else{
					setEndTime("");
				}
				
				String extenstion = null;
				if(rs.getString("event_image") !=null && !rs.getString("event_image").trim().equals("")){
					extenstion = FilenameUtils.getExtension(rs.getString("event_image").trim());
				}
				
				request.setAttribute("extenstion",extenstion);
					
				String eventImgPath = "";
				if(rs.getString("event_image")!=null && !rs.getString("event_image").equals("")){
					if(CF.getStrDocSaveLocation()==null){
						eventImgPath =  DOCUMENT_LOCATION +"/"+rs.getString("added_by")+"/"+rs.getString("event_image") ;
					} else {
						eventImgPath = CF.getStrDocRetriveLocation()+I_EVENTS+"/"+rs.getString("added_by")+"/"+rs.getString("event_image");

					}
				}else {
					eventImgPath = "userImages/event_icon.png";
				}
				//System.out.println("eventImg==>"+eventImgPath);
				String eventImage = "<img class='lazy' id=\"eventImage\" border=\"0\" style=\"height: 100px; width:100px; border: 1px solid #CCCCCC; padding: 5px;\" src=\""+eventImgPath+"\" data-original=\""+eventImgPath+"\" />";
				
				request.setAttribute("eventImage",eventImage);
				request.setAttribute("eventImgPath",eventImgPath);
				request.setAttribute("eImage",(String)rs.getString("event_image"));
				List<String> availableExt = CF.getAvailableExtention();
				request.setAttribute("availableExt", availableExt);
				
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
	
	private void deleteEvent(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select * from events where event_id=?");
			pst.setInt(1, uF.parseToInt(getStrEventId()));
			rs = pst.executeQuery();
			String event_image = null;
			String proId = null;
			while(rs.next()) {
				event_image = rs.getString("event_image");
				
			}
			rs.close();
			pst.close();
			
			String strFilePath = null;
			if(CF.getStrDocSaveLocation()==null) {
					strFilePath = DOCUMENT_LOCATION +"/"+ event_image;
			} else {
					strFilePath = CF.getStrDocSaveLocation()+I_EVENTS+"/"+getEmpId() +"/"+event_image;
			}
			File file = new File(strFilePath);
			file.delete();
			
//			System.out.println("eventId==>"+getStrEventId());
			pst = con.prepareStatement("delete from events where event_id=?");
			
			pst.setInt(1, uF.parseToInt(getStrEventId()));	
//			System.out.println("pst==>"+pst);
			pst.executeUpdate();
			
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
	private void updateEventData(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
		
			
			List<String> alInner = new ArrayList<String>();
			Map<String, String> hmResourceName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmResourceImage = CF.getEmpProfileImage(con);
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			Map<String, String> hmLevelMap = getLevelNameMap(uF);
			Map<String, String> hmCustImage = CF.getCustomerImageMap(con);
			String strDomain = request.getServerName().split("\\.")[0];
			String logUserLevel = hmEmpLevelMap.get(strSessionEmpId);
			if(getOperation() != null && getOperation().equals("U")) {
							 		
					pst = con.prepareStatement("update events set event_title=?,event_desc=?,event_date=?,last_updated = ?,updated_by=?,location = ?,sharing_level = ?,event_end_date = ?,from_time = ?,to_time = ?,entry_date=? where event_id=?");
					pst.setString(1,getStrEventName1());
					pst.setString(2,getStrEventdesc1());
					pst.setDate(3,uF.getDateFormat(getStrStartDate1(), DATE_FORMAT));
					pst.setTimestamp(4, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
					pst.setInt(5,uF.parseToInt((String)session.getAttribute(EMPID)));
					pst.setString(6,getStrLocation1());
					pst.setString(7,getStrSharing1());
					pst.setDate(8,uF.getDateFormat(getStrEndDate1(), DATE_FORMAT));
					pst.setTime(9, uF.getTimeFormat(getStartTime1(), TIME_FORMAT));
					pst.setTime(10, uF.getTimeFormat(getEndTime1(), TIME_FORMAT));
					pst.setDate(11,uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(12,uF.parseToInt(getStrEventId()));
					pst.executeUpdate();
					pst.close();
	
					if(getStrEventImage()!=null && !getStrEventImage().equals("")){
						StringBuilder sbQuery1 = new StringBuilder();
						sbQuery1.append("select * from events where event_id = ?");
						pst = con.prepareStatement(sbQuery1.toString());
						pst.setInt(1,uF.parseToInt(getEventId()));
//						System.out.println("pst2==>"+pst);
						rs = pst.executeQuery();
						String event_image = null;
						String addedBy = null;
						while(rs.next()) {
							event_image = rs.getString("event_image");
						    addedBy = rs.getString("added_by");
						}
						rs.close();
						pst.close();
						
						String strFilePath = null;
						
						if(CF.getStrDocSaveLocation()==null) {
								strFilePath = DOCUMENT_LOCATION +"/"+addedBy+"/"+event_image;
						} else {
								strFilePath = CF.getStrDocSaveLocation()+I_EVENTS+"/"+addedBy+"/"+event_image;
						}
						
						File file = new File(strFilePath);
						file.delete();
						
						uploadEventImages(getEventId());
					}
					List<String> allSharing1 = new ArrayList<String>();
					if(getStrSharing1() != null && !getStrSharing1().equals("")) {
						allSharing1 = Arrays.asList(getStrSharing1().split(","));
					}
				
					if(getStrSharing1() == null || getStrSharing1().equals("")){
	
						if(hmResourceName != null){
							Set empIdSet  = hmResourceName.keySet();
							Iterator<String> it = empIdSet.iterator();
							while(it.hasNext()){
								String empId = it.next();
								String strData = "<div style=\"float: left;\"> <b>"+hmResourceName.get(strSessionEmpId)+"</b> updated an event of <b><div style=\"font-size:11px;font-style:bold;\"> "+getStrEventName1()+"</b></div></div>";
								UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
								userAlerts.setStrDomain(strDomain);
								userAlerts.set_type(NEWS_AND_ALERTS);
								userAlerts.insertTRUserAlerts(con,CF,uF,empId,strData, "Hub.action?type=E");
							}
						}
					}
					
					if(allSharing1 != null){
						if(hmResourceName != null){
							Set empIdSet  = hmResourceName.keySet();
							Iterator<String> it = empIdSet.iterator();
							while(it.hasNext()){
								String empId = it.next();
								String empLevel = hmEmpLevelMap.get(empId);
								if(empLevel != null && allSharing1.contains(empLevel)){
									String strData = "<div style=\"float: left;\"> <b>"+hmResourceName.get(strSessionEmpId)+"</b> updated an event of <b><div style=\"font-size:11px;font-style:bold;\"> "+getStrEventName1()+"</b></div></div>";
									UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
									userAlerts.setStrDomain(strDomain);
									userAlerts.set_type(NEWS_AND_ALERTS);
									userAlerts.insertTRUserAlerts(con,CF,uF,empId,strData, "Hub.action?type=E");
								}
							}
						}
					}
				}
			
			pst = con.prepareStatement("select * from events where event_id = ?");
			pst.setInt(1, uF.parseToInt(getStrEventId()));
			rs = pst.executeQuery();
			
			while(rs.next()){
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
					alInner.add("All Levels");//8
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
				} else {
					
				}
				//System.out.println("eventImg==>"+eventImgPath);
				String eventImage = "<img class='lazy' style=\"max-height: 160px; max-width: 100%; border: 1px solid #CCCCCC; padding: 5px;\" src=\""+eventImgPath+"\" data-original=\""+eventImgPath+"\" />";
				alInner.add(eventImage); //12
				String addedImage = "<img class='lazy img-circle' border=\"0\" style=\"max-height: 160px; max-width: 100%; border: 1px solid #CCCCCC; padding: 5px;\" src=\"userImages/avatar_photo.png\" />"; 
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
					String MYImage1 = "<img class='lazy' border=\"0\" height=\"30\" width=\"30\" src=\"userImages/avatar_photo.png\" />"; 
					if(CF.getStrDocRetriveLocation()==null) { 
						MYImage1 = "<img class='lazy' border=\"0\" height=\"30\" width=\"30\" src=\"userImages/avatar_photo.png\" data-original=\""+DOCUMENT_LOCATION + createdByImage1+"\" />";
					} else if(createdByImage1 != null && !createdByImage1.equals("")) {
				  		MYImage1 = "<img class='lazy' border=\"0\" height=\"30\" width=\"30\" src=\"userImages/avatar_photo.png\" data-original=\""+CF.getStrDocRetriveLocation() +I_PEOPLE+"/"+I_IMAGE+"/"+rs.getString("added_by")+"/"+I_22x22+"/"+createdByImage1+"\" />";
					}
					//request.setAttribute("MYImg1", MYImage1);
					alInner.add(MYImage1);//16
				}
				
				String strStatus = "Upcoming";
				String curr_date=uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT);
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
				String strLive = "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5;width: 14px !important; height: 14px !important;margin-left: 5px;margin-right: 5px;\" title=\"Upcoming\"></i>";
				if(isLive){
					strStatus = "<span class=\"label label-success\"><i class=\"fa fa-clock-o\"></i>Ongoing</span>";
					/*strLive = "<img border=\"0\" style=\"width: 14px !important; height: 14px !important;margin-left: 5px;margin-right: 5px;\" src=\"images1/icons/approved.png\" title=\"Ongoing\">";*/
					strLive = "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d;width: 14px !important; height: 14px !important;margin-left: 5px;margin-right: 5px;\" title=\"Ongoing\"></i>";
					
				} else if(expired){
					strStatus = "<span class=\"label label-default\"><i class=\"fa fa-clock-o\"></i>Past event</span>";
				    /*strLive = "<img border=\"0\" style=\"width: 14px !important; height: 14px !important;margin-left: 5px;margin-right: 5px;\" src=\"images1/icons/denied.png\" title=\"Past \">";*/
					strLive = "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25;width: 14px !important; height: 14px !important;margin-left: 5px;margin-right: 5px;\" title=\"Past \"></i>";
				}
				
				alInner.add(strStatus); //17
				alInner.add(strLive);//18
			}
			request.setAttribute("alInner", alInner);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private void uploadImage(String bookId) {
		try {
			UploadImage uI = new UploadImage();
			uI.setServletRequest(request);
			uI.setImageType("EVENT_IMAGE");
			uI.setEmpImage(getStrEventImage());
			uI.setEmpImageFileName(getStrEventImageFileName());
			uI.setEmpId(strSessionEmpId);
			uI.setEventId(eventId);
			uI.setCF(CF);
			uI.upoadImage();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void updateEventData1(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			
			List<String> alInner = new ArrayList<String>();
			Map<String, String> hmResourceName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmResourceImage = CF.getEmpProfileImage(con);
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			Map<String, String> hmLevelMap = getLevelNameMap(uF);
			Map<String, String> hmCustImage = CF.getCustomerImageMap(con);
			String strDomain = request.getServerName().split("\\.")[0];
			String logUserLevel = hmEmpLevelMap.get(strSessionEmpId);
			if(getOperation() != null && getOperation().equals("U")) {
				if((getStrEventName()!=null && !getStrEventName().equals("")) && (getStrEventdesc()!=null && !getStrEventdesc().equals("")) 
			    	&& (getStrLocation()!=null && !getStrLocation().equals("")) && (getStrStartDate()!=null && !getStrStartDate().equals(""))
			    	&& (getStrEndDate()!=null && !getStrEndDate().equals("")) && (getStartTime()!=null && !getStartTime().equals("")) 
			    	&& (getEndTime()!=null && !getEndTime().equals(""))){
			 		
					StringBuilder sharinglevel = null;
					if(getStrSharing()!=null && !getStrSharing().equals("")){
						
						String[] sharingList = getStrSharing().split(",");
						
						if(sharingList!=null && sharingList.length>0){
							if(sharinglevel == null){
								sharinglevel = new StringBuilder();
								sharinglevel.append(",");
							}
						
							for(String level:sharingList){
							
								if(level!=null && !level.equals("")){
									level = level.replaceAll(" ","");
									sharinglevel.append(level+",");
								}else{
									sharinglevel = new StringBuilder();
									sharinglevel.append("");
									break;
								}
							}
						}
					}
					if(sharinglevel == null) {
						sharinglevel = new StringBuilder();
					}
					
					pst = con.prepareStatement("update events set event_title=?,event_desc=?,event_date=?,last_updated = ?,updated_by=?,location = ?,sharing_level = ?,event_end_date = ?,from_time = ?,to_time = ?,entry_date=? where event_id=?");
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
					pst.setInt(12,uF.parseToInt(getEventId()));
					pst.executeUpdate();
					pst.close();
	
					if(getStrEventImage()!=null && !getStrEventImage().equals("")){
						StringBuilder sbQuery1 = new StringBuilder();
						sbQuery1.append("select * from events where event_id = ?");
						pst = con.prepareStatement(sbQuery1.toString());
						pst.setInt(1,uF.parseToInt(getEventId()));
						System.out.println("pst2==>"+pst);
						rs = pst.executeQuery();
						String event_image = null;
						String addedBy = null;
						while(rs.next()) {
							event_image = rs.getString("event_image");
						    addedBy = rs.getString("added_by");
						}
						rs.close();
						pst.close();
						
						String strFilePath = null;
						
						if(CF.getStrDocSaveLocation()==null) {
								strFilePath = DOCUMENT_LOCATION +"/"+addedBy+"/"+event_image;
						} else {
								strFilePath = CF.getStrDocSaveLocation()+I_EVENTS+"/"+addedBy+"/"+event_image;
						}
						
						File file = new File(strFilePath);
						file.delete();
						
						uploadEventImages(getEventId());
					}
					List<String> allSharing1 = new ArrayList<String>();
					if(getStrSharing1() != null && !getStrSharing().equals("")) {
						allSharing1 = Arrays.asList(getStrSharing().split(","));
					}
				
					if(getStrSharing() == null || getStrSharing().equals("")){
	
						if(hmResourceName != null){
							Set empIdSet  = hmResourceName.keySet();
							Iterator<String> it = empIdSet.iterator();
							while(it.hasNext()){
								String empId = it.next();
								String strData = "<div style=\"float: left;\"> <b>"+hmResourceName.get(strSessionEmpId)+"</b> updated an event of <b><div style=\"font-size:11px;font-style:bold;\"> "+getStrEventName()+"</b></div></div>";
								UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
								userAlerts.setStrDomain(strDomain);
								userAlerts.set_type(NEWS_AND_ALERTS);
								userAlerts.insertTRUserAlerts(con,CF,uF,empId,strData, "Hub.action?type=E");
							}
						}
					}
					
					if(allSharing1 != null){
						if(hmResourceName != null){
							Set empIdSet  = hmResourceName.keySet();
							Iterator<String> it = empIdSet.iterator();
							while(it.hasNext()){
								String empId = it.next();
								String empLevel = hmEmpLevelMap.get(empId);
								if(empLevel != null && allSharing1.contains(empLevel)){
									String strData = "<div style=\"float: left;\"> <b>"+hmResourceName.get(strSessionEmpId)+"</b> updated an event of <b><div style=\"font-size:11px;font-style:bold;\"> "+getStrEventName()+"</b></div></div>";
									UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
									userAlerts.setStrDomain(strDomain);
									userAlerts.set_type(NEWS_AND_ALERTS);
									userAlerts.insertTRUserAlerts(con,CF,uF,empId,strData, "Hub.action?type=E");
								}
							}
						}
					}
				}
			}
		
			pst = con.prepareStatement("select * from events where event_id = ?");
			pst.setInt(1, uF.parseToInt(getStrEventId()));
			rs = pst.executeQuery();
			
			while(rs.next()){
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
				alInner.add(hmResourceName.get(rs.getString("added_by")));//6
				alInner.add(rs.getString("location"));//7
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
					alInner.add("All Levels");//8
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
				}else {
					eventImgPath = "userImages/event_icon.png";
				}
				//System.out.println("eventImg==>"+eventImgPath);
				String eventImage = "<img class='attachment-img' alt=\"Attachment_Image\" src=\""+eventImgPath+"\" data-original=\""+eventImgPath+"\" />";
				alInner.add(eventImage); //12
				String addedImage = "<img class='lazy img-circle' border=\"0\" style=\"max-height: 160px; max-width: 100%; border: 1px solid #CCCCCC; padding: 5px;\" src=\"userImages/avatar_photo.png\" />"; 
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
			}
			request.setAttribute("alInner", alInner);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	public List<String> getLevelList(){

		List<String> levelList = new ArrayList<String>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try{
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from level_details");
			rs = pst.executeQuery();
			while(rs.next()){
				
				levelList.add("["+rs.getString("level_code")+"]"+rs.getString("level_name"));
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
		request.setAttribute("levelList",levelList);
		return levelList;
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

	private HttpServletRequest request;
	
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	public String getStrEventId() {
		return strEventId;
	}
	
	public void setStrEventId(String strEventId) {
		this.strEventId = strEventId;
	}
	
	public String getOperation() {
		return operation;
	}
	
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public String getEmpId() {
		return empId;
	}
	public void setEmpId(String empId) {
		this.empId = empId;
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
	public HttpServletRequest getRequest() {
		return request;
	}
	
	public String getStrEventName1() {
		return strEventName1;
	}
	
	public void setStrEventName1(String strEventName1) {
		this.strEventName1 = strEventName1;
	}
	
	public String getStrSharing1() {
		return strSharing1;
	}
	
	public void setStrSharing1(String strSharing1) {
		this.strSharing1 = strSharing1;
	}
	
	public String getStrEventdesc1() {
		return strEventdesc1;
	}
	
	public void setStrEventdesc1(String strEventdesc1) {
		this.strEventdesc1 = strEventdesc1;
	}
	
	public String getStrLocation1() {
		return strLocation1;
	}
	
	public void setStrLocation1(String strLocation1) {
		this.strLocation1 = strLocation1;
	}
	
	public String getStrStartDate1() {
		return strStartDate1;
	}
	
	public void setStrStartDate1(String strStartDate1) {
		this.strStartDate1 = strStartDate1;
	}
	
	public String getStrEndDate1() {
		return strEndDate1;
	}
	
	public void setStrEndDate1(String strEndDate1) {
		this.strEndDate1 = strEndDate1;
	}
	
	public String getStartTime1() {
		return startTime1;
	}
	
	public void setStartTime1(String startTime1) {
		this.startTime1 = startTime1;
	}
	
	public String getEndTime1() {
		return endTime1;
	}
	
	public void setEndTime1(String endTime1) {
		this.endTime1 = endTime1;
	}
	
	public void setRequest(HttpServletRequest request) {
		this.request = request;
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

	public File getStrEventImage1() {
		return strEventImage1;
	}

	public void setStrEventImage1(File strEventImage1) {
		this.strEventImage1 = strEventImage1;
	}

	public String getSubmit() {
		return submit;
	}

	public void setSubmit(String submit) {
		this.submit = submit;
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

}
