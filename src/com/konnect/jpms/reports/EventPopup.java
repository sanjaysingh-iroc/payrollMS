package com.konnect.jpms.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class EventPopup extends ActionSupport implements ServletRequestAware, IStatements {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4179145895739713025L;
	String strUserType=null;
	HttpSession session; 
	CommonFunctions CF;
	
	String strSessionEmpId;
	String strSessionOrgId;
	private String eventId;
	
	private static Logger log = Logger.getLogger(EventPopup.class);
	
	public String execute() throws Exception {
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		strSessionEmpId = (String)session.getAttribute(EMPID);
		strSessionOrgId = (String)session.getAttribute(ORGID);
		strUserType = (String)session.getAttribute(BASEUSERTYPE);
		request.setAttribute(PAGE, "/jsp/reports/EventPopup.jsp");
		strUserType = (String)session.getAttribute(USERTYPE);
		UtilityFunctions uF = new UtilityFunctions();
		getEventById(uF);
		return SUCCESS;
	}
	
	private void getEventById(UtilityFunctions uF){
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			
			con = db.makeConnection(con);
			
			Map<String, String> hmResourceName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmResourceImage = CF.getEmpProfileImage(con);
			Map<String, String> hmCustImage = CF.getCustomerImageMap(con);
			//Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			Map<String, String> hmLevelMap = getLevelNameMap(con,uF);
			Map<String, String> hmLevelCodeMap = CF.getLevelCodeName(con, uF);
			
			Map<String, String> hmCustName = CF.getCustomerNameMap(con);
			List<String> alInner = new ArrayList<String>();
								
			List<String> levelList = CF.getLevelList(con, uF);
			List<String> levelIdList = new ArrayList<String>();
			
			Set levelIdSet = hmLevelCodeMap.keySet();
 			Iterator it = levelIdSet.iterator();
 			while(it.hasNext()){
 				levelIdList.add((String)it.next());
 			}
			
			pst = con.prepareStatement("select * from events where event_id = ?");
			pst.setInt(1, uF.parseToInt(getEventId()));
			rs = pst.executeQuery();
			while(rs.next()) {
				alInner.add(Integer.toString(rs.getInt("event_id")));//0
				alInner.add(uF.getDateFormat(rs.getString("event_date"), DBDATE, DATE_FORMAT));//1
				alInner.add(uF.getDateFormat(rs.getString("event_end_date"), DBDATE, DATE_FORMAT));//2
				alInner.add(uF.getDateFormat(rs.getString("posted_date"),DBTIMESTAMP, DATE_FORMAT_STR+" "+TIME_FORMAT_AM_PM));//3
				alInner.add(rs.getString("event_title"));//4
				alInner.add(rs.getString("event_desc"));//5
				alInner.add(hmResourceName.get(rs.getString("added_by")));//6
				alInner.add(rs.getString("location"));//7
				String sharing_level = rs.getString("sharing_level");
				String[] levels = null;
				if(sharing_level!=null && !sharing_level.equals("")){
					levels = rs.getString("sharing_level").split(",");
					String sharing ="";
					for(String l:levels){
						if(l!=null || !l.equals("")){
							sharing = sharing+","+ hmLevelMap.get(l);
						}
					}
					alInner.add(sharing.substring(6,sharing.length()));//8
				}else{					
					alInner.add("All");//8
				}
				
				alInner.add(rs.getString("event_image"));//9
				alInner.add(rs.getString("added_by"));//10
				String extenstion = null;
				if(rs.getString("event_image") !=null && !rs.getString("event_image").trim().equals("")){
					extenstion = FilenameUtils.getExtension(rs.getString("event_image").trim());
				}
				alInner.add(extenstion);//11
				
				String eventImgPath = "";
				if(CF.getStrDocSaveLocation()==null){
					eventImgPath =  DOCUMENT_LOCATION +"/"+rs.getString("added_by")+"/"+rs.getString("event_image") ;
				} else {
					eventImgPath =  CF.getStrDocRetriveLocation()+I_EVENTS+"/"+rs.getString("added_by")+"/"+rs.getString("event_image") ;

				}
				
				alInner.add(eventImgPath); //12
				String addedImage = "<img class='lazy img-circle' border=\"0\" height=\"30\" width=\"30\" src=\"userImages/avatar_photo.png\" />"; 
				if(CF.getStrDocRetriveLocation()==null) { 
					addedImage = "<img class='lazy' border=\"0\" height=\"30\" width=\"30\" src=\""+DOCUMENT_LOCATION +"/"+rs.getString("added_by")+"/"+rs.getString("event_image")+"\" />";
			  	} else if(rs.getString("added_by")!=null && !rs.getString("added_by").equals("")) {
			  		addedImage = "<img class='lazy' border=\"0\" height=\"30\" width=\"30\" src=\""+CF.getStrDocRetriveLocation()+I_EVENTS+"/"+rs.getString("added_by")+"/"+rs.getString("event_image")+"\" />";
			  		//addedImage = "<img class='lazy' border=\"0\" height=\"30\" width=\"30\" src=\""+CF.getStrDocSaveLocation()+"Events"+"/"+rs.getString("added_by")+"\" />";
			  	}
				alInner.add(addedImage);//13
				String MYLargeImage = "<img class='lazy img-circle' border=\"1px solid #CCCCCC\" height=\"80\" width=\"80\" src=\"userImages/avatar_photo.png\" />";
				String MYImage = "<img class='lazy img-circle' border=\"0\" height=\"25\" width=\"25\" src=\"userImages/avatar_photo.png\" />";
				String from_time = rs.getString("from_time");
				String to_time = rs.getString("to_time");
				
				if(from_time != null && !from_time.equals("")){
					//alInner.add(from_time.substring(0,from_time.lastIndexOf(":")));//14
					alInner.add(uF.getTimeFormatStr(from_time.substring(0,from_time.lastIndexOf(":")),DBTIME,TIME_FORMAT_AM_PM));
				}else{
					alInner.add("");//14
				}
				if(to_time != null  && !to_time.equals("")){
					//alInner.add(to_time.substring(0,to_time.lastIndexOf(":")));//15
					alInner.add(uF.getTimeFormatStr(to_time.substring(0,to_time.lastIndexOf(":")),DBTIME,TIME_FORMAT_AM_PM));
				}else{
					alInner.add("");//15
					
				}
				
				if(strUserType != null && strUserType.equals(CUSTOMER)) {
					String createdByImage1 = hmCustImage.get(rs.getString("added_by"));
					String strClientId = CF.getClientIdBySPOCId(con, uF, rs.getString("added_by"));
					String MYImage1 = "<img class='lazy img-circle' border=\"0\" height=\"30\" width=\"30\" src=\"userImages/avatar_photo.png\" />"; 
					if(CF.getStrDocRetriveLocation()==null) { 
						MYImage1 = "<img class='lazy img-circle' border=\"0\" height=\"30\" width=\"30\" src=\""+DOCUMENT_LOCATION + createdByImage1+"\" />";
				  	} else if(createdByImage1 != null && !createdByImage1.equals("")) {
				  		MYImage1= "<img class='lazy img-circle' border=\"0\" height=\"30\" width=\"30\" src=\""+CF.getStrDocRetriveLocation() +I_CUSTOMER+"/"+strClientId+"/"+I_IMAGE+"/"+I_CUSTOMER_SPOC+"/"+I_22x22+"/"+createdByImage1+"\" />";
		            }
					alInner.add(MYImage1);//16
				} else {
					String createdByImage1 = hmResourceImage.get(rs.getString("added_by"));
					String MYImage1 = "<img class='lazy img-circle' border=\"0\" height=\"30\" width=\"30\" src=\"userImages/avatar_photo.png\" />"; 
					if(CF.getStrDocRetriveLocation()==null) { 
						MYImage1 = "<img class='lazy img-circle' border=\"0\" height=\"30\" width=\"30\" src=\""+DOCUMENT_LOCATION + createdByImage1+"\" />";
					} else if(createdByImage1 != null && !createdByImage1.equals("")) {
				  		MYImage1 = "<img class='lazy img-circle' border=\"0\" height=\"30\" width=\"30\" src=\""+CF.getStrDocRetriveLocation() +I_PEOPLE+"/"+I_IMAGE+"/"+rs.getString("added_by")+"/"+I_22x22+"/"+createdByImage1+"\" />";
					}
					//request.setAttribute("MYImg1", MYImage1);
					alInner.add(MYImage1);//16
				}
			}
			
			List<String> availableExt = CF.getAvailableExtention();
			request.setAttribute("availableExt", availableExt);
			request.setAttribute("alInner", alInner);
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
	private HttpServletRequest request;
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}
	
	
}
