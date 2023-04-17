package com.konnect.jpms.cafeteria;

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

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.library.BookReport;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ViewCafeteria extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	public HttpSession session;
	String strSessionOrgId;
	String strSessionEmpId;
	String strUserType;
	
	public CommonFunctions CF;
	
	private String f_org;
	private String[] f_wlocation;
	
	private List<FillWLocation> wLocationList;
	private List<FillOrganisation> organisationList;
	
	private String strSearchJob;
	private String dataType;
	
	private String alertID;
	
	private static Logger log = Logger.getLogger(ViewCafeteria.class);
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)	return LOGIN;

		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		StringBuilder sbpageTitleNaviTrail = new StringBuilder();
		sbpageTitleNaviTrail.append("<li><i class=\"fa fa-wrench\"></i><a href=\"Library.action\" style=\"color: #3c8dbc;\"> Utility</a></li>" +
			"<li class=\"active\">Cafeteria</li>");
		request.setAttribute("PAGETITLE_NAVITRAIL", sbpageTitleNaviTrail.toString());
		
		UtilityFunctions uF = new UtilityFunctions();
		if(getF_org() == null) {
			setF_org((String)session.getAttribute(ORGID));
		}
		request.setAttribute(PAGE, PViewCafeteria);
		request.setAttribute(TITLE, TViewCafeteria);
		
		String strDomain = request.getServerName().split("\\.")[0];
		CF.deleteWRUserAlerts(CF, request, strDomain, getAlertID());
		
//		if(strUserType != null && strUserType.equals(ADMIN)) {
//			updateUserAlerts(uF, FOOD_REQUEST_ALERT);
//		} 
		
		loadEmployee(uF);
		getSelectedFilter(uF);
		getSearchAutoCompleteData(uF);
		
		if(getDataType() == null || getDataType().trim().equals("") || getDataType().trim().equalsIgnoreCase("NULL")) {
			setDataType("T");    
		}

		getCafeteriaDetails(uF);
		return SUCCESS;
	}
	
//	private void updateUserAlerts(UtilityFunctions uF, String alertType) {
//		Connection con = null;
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
//			db.closeConnection(con);
//		}
//	}
	
	private Map<String, String> getConfirmedOrderCount(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		Map<String, String> hmDishConfirmedOrders = new HashMap<String,String>();
		
		try {
			con = db.makeConnection(con);
//			int dishOrderCount = 0;
			StringBuilder sbQuery = new StringBuilder();
			Map<String, String> hmEmpNames = CF.getEmpNameMap(con,null, null);
			
			sbQuery.append("select count(order_id) as order_count ,dish_id from dish_order_details where order_status = 1 group by dish_id ");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			while(rs.next()) {
//				dishOrderCount = rs.getInt("order_count");
				hmDishConfirmedOrders.put(rs.getString("dish_id"), rs.getString("order_count"));
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
		
		return hmDishConfirmedOrders;
	}
	
	private void getCafeteriaDetails(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			Map<String, String> hmOrgName = CF.getOrgName(con);
			Map<String, String> hmEmpNames = CF.getEmpNameMap(con,null, null);
			Map<String, String> hmEmpWLocation = CF.getWLocationMap(con,null,request,null); 
			Map<String, String> hmDishConfirmedOrders = getConfirmedOrderCount(uF); 
			
			//todays menu
			Map<String,List<String>> hmBreakFastMenu = new HashMap<String,List<String>>();
			Map<String,List<String>> hmLunchMenu = new HashMap<String,List<String>>();
			Map<String,List<String>> hmDinnerMenu = new HashMap<String,List<String>>();
			Map<String,List<String>> hmOtherMenu = new HashMap<String,List<String>>();
			
			//tomorrows menu
//			Map<String,List<String>> hmTomorrowsBreakFastMenu = new HashMap<String,List<String>>();
//			Map<String,List<String>> hmTomorrowsLunchMenu = new HashMap<String,List<String>>();
//			Map<String,List<String>> hmTomorrowsDinnerMenu = new HashMap<String,List<String>>();
//			Map<String,List<String>> hmTomorrowsOtherMenu = new HashMap<String,List<String>>();
			
			// day after tomorrows menu
//			Map<String,List<String>> hmDATBreakFastMenu = new HashMap<String,List<String>>();
//			Map<String,List<String>> hmDATLunchMenu = new HashMap<String,List<String>>();
//			Map<String,List<String>> hmDATDinnerMenu = new HashMap<String,List<String>>();
//			Map<String,List<String>> hmDATOtherMenu = new HashMap<String,List<String>>();
			
			//get today,tomorrow and day after tomorrows date
//			java.util.Date currDate = uF.getDateFormatUtil(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()),DBDATE, DATE_FORMAT),DATE_FORMAT );
//			java.util.Date tomorrowDate = uF.getDateFormatUtil(uF.getDateFormat(""+uF.getFutureDate(uF.getCurrentDate(CF.getStrTimeZone()), 1), DBDATE, DATE_FORMAT),DATE_FORMAT );
//			java.util.Date dayAfterTomorrowDate = uF.getDateFormatUtil(uF.getDateFormat(""+uF.getFutureDate(uF.getCurrentDate(CF.getStrTimeZone()), 2),DBDATE, DATE_FORMAT),DATE_FORMAT );
			
			Map<String,String> hmMealTypes = new HashMap<String,String>();
			hmMealTypes.put(BREAKFAST, "Breakfast");
			hmMealTypes.put(LUNCH, "Lunch");
			hmMealTypes.put(DINNER, "Dinner");
			hmMealTypes.put(OTHER, "Other");
			//main Map to get all today,tomorrow and day after tomorrow menu data
			
			Map<String, String> hmDishCount = new HashMap<String, String>();
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select count(*) as cnt,dish_id from dish_order_details where order_status = 0 and order_date=? group by dish_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			while(rs.next()) {
				hmDishCount.put(rs.getString("dish_id"), rs.getString("cnt"));
			}
			rs.close();
			pst.close();
			
			sbQuery = new StringBuilder();
			sbQuery.append("select * from dish_details where dish_id > 0 and ? between dish_from_date and dish_to_date ");
			if(getF_org()!=null && !getF_org().equals("")) {
				sbQuery.append(" and org_id in ("+getF_org()+")");
			} else {
				if(strUserType != null && strUserType.equals(HRMANAGER)) {
					 if(session.getAttribute(ORG_ACCESS) != null && !session.getAttribute(ORG_ACCESS).equals("")) {
						 sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
					 } else {
						 sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
					 }
				} else if(strUserType != null && !strUserType.equals(HRMANAGER) && !strUserType.equals(ADMIN)) {
					sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
				}
			}
			
			if(getF_wlocation()!=null && !getF_wlocation().equals("")) {
				sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_wlocation(), ",")+")");
			} else {
				if(strUserType != null && strUserType.equals(HRMANAGER)) {
					 if(session.getAttribute(WLOCATION_ACCESS) != null && !session.getAttribute(WLOCATION_ACCESS).equals("")){
						 sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
					 } else {
						 sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
					 }
				} else if(strUserType != null && !strUserType.equals(HRMANAGER) && !strUserType.equals(ADMIN)) {
					sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
				}
			}
			
			if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("")){
				sbQuery.append(" and (upper(dish_name) like '%"+getStrSearchJob().trim().toUpperCase()+"%' )");
			}
		
			sbQuery.append(" order by entry_date ");
			
			pst = con.prepareStatement(sbQuery.toString());
			if(getDataType() != null && getDataType().equals("T")) {
				pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			} else if(getDataType() != null && getDataType().equals("TM")) {
				pst.setDate(1, uF.getFutureDate(uF.getCurrentDate(CF.getStrTimeZone()), 1));
			} else if(getDataType() != null && getDataType().equals("DAT")) {
				pst.setDate(1, uF.getFutureDate(uF.getCurrentDate(CF.getStrTimeZone()), 2));
			}
			rs = pst.executeQuery();
			while(rs.next()) {
				List<String> alInner = new ArrayList<String>();
				alInner.add(rs.getString("dish_id"));//0
				alInner.add(uF.showData(rs.getString("dish_name"),"-"));//1
				alInner.add(uF.showData(rs.getString("dish_type"),"-"));//2
				alInner.add(uF.showData(rs.getString("dish_price"),"-"));//3
				alInner.add(uF.showData(rs.getString("dish_comment"),"-"));//4
				alInner.add(uF.showData(hmOrgName.get(rs.getString("org_id")),"-"));//5
				alInner.add(uF.showData(hmEmpWLocation.get(rs.getString("wlocation_id")),"-"));//6
				alInner.add(uF.showData(hmEmpNames.get(rs.getString("added_by")),"-"));//7
				alInner.add(uF.getDateFormat(rs.getString("dish_from_date"), DBDATE, CF.getStrReportDateFormat()));//8
				alInner.add(uF.getDateFormat(rs.getString("dish_to_date"), DBDATE, CF.getStrReportDateFormat()));//9
				alInner.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));//10
				alInner.add(uF.showData(hmEmpNames.get(rs.getString("updated_by")),"-"));//11
				alInner.add(uF.getDateFormat(rs.getString("last_updated_date"), DBDATE, CF.getStrReportDateFormat()));//12
				
				String from_time = rs.getString("dish_from_time");
				String to_time = rs.getString("dish_to_time");
				if(from_time != null && !from_time.equals("")) {
					
					alInner.add(uF.getTimeFormatStr(from_time.substring(0,from_time.lastIndexOf(":")),DBTIME,TIME_FORMAT_AM_PM));//13
				}else {
					alInner.add("");//13
				}
				
				if(to_time != null  && !to_time.equals("")) {
					
					alInner.add(uF.getTimeFormatStr(to_time.substring(0,to_time.lastIndexOf(":")),DBTIME,TIME_FORMAT_AM_PM));//14
				}else {
					alInner.add("");//14
				}
				
				
				String extenstion = null;
				if(rs.getString("dish_image") !=null && !rs.getString("dish_image").trim().equals("")) {
					extenstion = FilenameUtils.getExtension(rs.getString("dish_image").trim());
				}
				alInner.add(extenstion);//15
//				
				String dishImgPath = "userImages/dishe_avatar_photo.png";
				if(rs.getString("dish_image")!=null && !rs.getString("dish_image").equals(""))  {
					if(CF.getStrDocSaveLocation()==null) {
						dishImgPath =  DOCUMENT_LOCATION +"/"+rs.getString("added_by")+"/"+rs.getString("dish_id")+"/"+rs.getString("dish_image") ;
					} else {
						dishImgPath = CF.getStrDocRetriveLocation()+I_DISHES+"/"+rs.getString("added_by")+"/"+rs.getString("dish_id")+"/"+rs.getString("dish_image");
					}
				}
//				String dishImage = "<img class='lazy' border=\"0\" style=\"max-height:116px; max-width:260px; border: 1px solid #CCCCCC; \" src=\""+dishImgPath+"\" data-original=\""+dishImgPath+"\" />";
				String dishImage = "<img class=\"img1\" src=\""+dishImgPath+"\" data-original=\""+dishImgPath+"\" />";
				alInner.add(dishImage); //16
				alInner.add(hmDishCount.get(rs.getString("dish_id"))); //17
				alInner.add(hmMealTypes.get(rs.getString("dish_type"))); //18
				alInner.add(uF.showData(rs.getString("dish_comment"), "-")); //19
				alInner.add(hmDishConfirmedOrders.get(rs.getString("dish_id")));//20
				
//				System.out.println("fromDate==>"+ uF.getDateFormat(rs.getString("dish_from_date"), DBDATE));
//				System.out.println("toDate==>"+ uF.getDateFormat(rs.getString("dish_to_date"), DBDATE));
//				System.out.println("today==>"+ currDate);
//				System.out.println("tomorrow==>"+ tomorrowDate);
//				System.out.println("dat==>"+ dayAfterTomorrowDate);
				
//				boolean todayFlag = uF.isDateBetween(uF.getDateFormat(rs.getString("dish_from_date"), DBDATE), uF.getDateFormat(rs.getString("dish_to_date"), DBDATE), currDate);
//				boolean tomorrowFlag = uF.isDateBetween(uF.getDateFormat(rs.getString("dish_from_date"), DBDATE), uF.getDateFormat(rs.getString("dish_to_date"), DBDATE), tomorrowDate);
//				boolean DATFlag = uF.isDateBetween(uF.getDateFormat(rs.getString("dish_from_date"), DBDATE), uF.getDateFormat(rs.getString("dish_to_date"), DBDATE), dayAfterTomorrowDate);
				
//				System.out.println("todayFlag==>"+todayFlag+"==>tomorrowFlag==>"+tomorrowFlag+"==>DATFlag==>"+DATFlag);
				
				int mealTypeId = uF.parseToInt(rs.getString("dish_type"));
				
//				System.out.println("mealType==>"+mealTypeId);
				
//				if(todayFlag) {
					if(mealTypeId >0 && mealTypeId == 1) {
						hmBreakFastMenu.put(rs.getString("dish_id"), alInner);
					} else if(mealTypeId >0 && mealTypeId == 2) {
						hmLunchMenu.put(rs.getString("dish_id"), alInner);
					} else if(mealTypeId >0 && mealTypeId == 3) {
						hmDinnerMenu.put(rs.getString("dish_id"), alInner);
					} else {
						hmOtherMenu.put(rs.getString("dish_id"), alInner);
					}
//				}
				
//				if(tomorrowFlag) {
//					if(mealTypeId >0 && mealTypeId == 1) {
//						hmTomorrowsBreakFastMenu.put(rs.getString("dish_id"), alInner);
//					} else if(mealTypeId >0 && mealTypeId == 2) {
//						hmTomorrowsLunchMenu.put(rs.getString("dish_id"), alInner);
//					} else if(mealTypeId >0 && mealTypeId == 3) {
//						hmTomorrowsDinnerMenu.put(rs.getString("dish_id"), alInner);
//					} else {
//						hmTomorrowsOtherMenu.put(rs.getString("dish_id"), alInner);
//					}
//				}
//				
//				if(DATFlag) {
//					if(mealTypeId >0 && mealTypeId == 1) {
//						hmDATBreakFastMenu.put(rs.getString("dish_id"), alInner);
//					} else if(mealTypeId >0 && mealTypeId == 2) {
//						hmDATLunchMenu.put(rs.getString("dish_id"), alInner);
//					} else if(mealTypeId >0 && mealTypeId == 3) {
//						hmDATDinnerMenu.put(rs.getString("dish_id"), alInner);
//					} else {
//						hmDATOtherMenu.put(rs.getString("dish_id"), alInner);
//					}
//				}
				
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmTodaysBreakFastMenu==>"+hmTodaysBreakFastMenu.size());
//			System.out.println("hmTodaysLunchMenu==>"+hmTodaysLunchMenu.size());
//			System.out.println("hmTodaysDinnerMenu==>"+hmTodaysDinnerMenu.size());
//			System.out.println("hmTodaysOtherMenu==>"+hmTodaysOtherMenu.size());
						
			request.setAttribute("hmBreakFastMenu", hmBreakFastMenu);
			request.setAttribute("hmLunchMenu", hmLunchMenu);
			request.setAttribute("hmDinnerMenu", hmDinnerMenu);
			request.setAttribute("hmOtherMenu", hmOtherMenu);
			
//			request.setAttribute("hmTomorrowsBreakFastMenu", hmTomorrowsBreakFastMenu);
//			request.setAttribute("hmTomorrowsLunchMenu", hmTomorrowsLunchMenu);
//			request.setAttribute("hmTomorrowsDinnerMenu", hmTomorrowsDinnerMenu);
//			request.setAttribute("hmTomorrowsOtherMenu", hmTomorrowsOtherMenu);
//			
//			request.setAttribute("hmDATBreakFastMenu", hmDATBreakFastMenu);
//			request.setAttribute("hmDATLunchMenu", hmDATLunchMenu);
//			request.setAttribute("hmDATDinnerMenu", hmDATDinnerMenu);
//			request.setAttribute("hmDATOtherMenu", hmDATOtherMenu);
			
						
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		alFilter.add("ORGANISATION");
		if(getF_org()!=null)  {
			String strOrg="";
			int k=0;
			for(int i=0;organisationList!=null && i<organisationList.size();i++){
				if(getF_org().equals(organisationList.get(i).getOrgId())) {
					if(k==0) {
						strOrg=organisationList.get(i).getOrgName();
					} else {
						strOrg+=", "+organisationList.get(i).getOrgName();
					}
					k++;
				}
			}
			if(strOrg!=null && !strOrg.equals("")) {
				hmFilter.put("ORGANISATION", strOrg);
			} else {
				hmFilter.put("ORGANISATION", "All Organisation");
			}
			
		} else {
			hmFilter.put("ORGANISATION", "All Organisation");
		}
		
		alFilter.add("LOCATION");
		if(getF_wlocation()!=null) {
			String strLocation="";
			int k=0;
			for(int i=0;wLocationList!=null && i<wLocationList.size();i++) {
				for(int j=0;j<getF_wlocation().length;j++) {
					if(getF_wlocation()[j].equals(wLocationList.get(i).getwLocationId())) {
						if(k==0) {
							strLocation=wLocationList.get(i).getwLocationName();
						} else {
							strLocation+=", "+wLocationList.get(i).getwLocationName();
						}
						k++;
					}
				}
			}
			if(strLocation!=null && !strLocation.equals("")) {
				hmFilter.put("LOCATION", strLocation);
			} else {
				hmFilter.put("LOCATION", "All Locations");
			}
		} else {
			hmFilter.put("LOCATION", "All Locations");
		}
		
		
		String selectedFilter=CF.getSelectedFilter1(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	private void getSearchAutoCompleteData(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			
			SortedSet<String> setSearchList = new TreeSet<String>();
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from dish_details where dish_id > 0 ");
			
			if(getF_org()!=null && !getF_org().equals("")) {
				sbQuery.append(" and org_id in ("+getF_org()+")");
			} else {
				if(strUserType != null && strUserType.equals(HRMANAGER)) {
					 if(session.getAttribute(ORG_ACCESS) != null && !session.getAttribute(ORG_ACCESS).equals("")) {
						 sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
					 } else {
						 sbQuery.append("  and org_id in ("+(String)session.getAttribute(ORGID)+")");
					 }
				} else if(strUserType != null && !strUserType.equals(HRMANAGER) && !strUserType.equals(ADMIN)) {
					sbQuery.append("  and org_id in ("+(String)session.getAttribute(ORGID)+")");
					
				}
				
			}
			
			if(getF_wlocation()!=null && !getF_wlocation().equals("")) {
				sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_wlocation(), ",")+")");
			} else {
				if(strUserType != null && strUserType.equals(HRMANAGER)) {
					 if(session.getAttribute(WLOCATION_ACCESS) != null && !session.getAttribute(WLOCATION_ACCESS).equals("")) {
						 sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
					 } else {
						 sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
					 }
				} else if(strUserType != null && !strUserType.equals(HRMANAGER) && !strUserType.equals(ADMIN)) {
					sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
					
				}
			}

			sbQuery.append(" order by dish_name");
			pst = con.prepareStatement(sbQuery.toString());
		
			rs = pst.executeQuery();
			while (rs.next()) {
				setSearchList.add(rs.getString("dish_name"));
				
			}
			rs.close();
			pst.close();
			
			StringBuilder sbData = null;
			Iterator<String> it = setSearchList.iterator();
			while (it.hasNext()) {
				String strData = it.next();
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
			request.setAttribute("sbData", sbData.toString());
				
		} catch (Exception e) {
			e.printStackTrace(); 
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	private String loadEmployee(UtilityFunctions uF) {
		if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
			 if(session.getAttribute(WLOCATION_ACCESS) != null && !session.getAttribute(WLOCATION_ACCESS).equals("")) {
				wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
				organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
			 } else {
				 wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATIONID));
				 organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORGID));
			 }
		} else if(strUserType!=null && strUserType.equalsIgnoreCase(ADMIN)) {
				organisationList = new FillOrganisation(request).fillOrganisation();
				wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		} else {
			 wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATIONID));
			 organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORGID));
		}
		return LOAD;
	}

	public HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public String[] getF_wlocation() {
		return f_wlocation;
	}

	public void setF_wlocation(String[] f_wlocation) {
		this.f_wlocation = f_wlocation;
	}

	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}

	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}

	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}

	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}

	public String getStrSearchJob() {
		return strSearchJob;
	}

	public void setStrSearchJob(String strSearchJob) {
		this.strSearchJob = strSearchJob;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getAlertID() {
		return alertID;
	}

	public void setAlertID(String alertID) {
		this.alertID = alertID;
	}
	
}
