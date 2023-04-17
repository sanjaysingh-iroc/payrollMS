package com.konnect.jpms.leave;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class TravelReport extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public HttpSession session;
	public CommonFunctions CF = null;
	
	String strUserType = null;  
	String strSesionEmpId = null;
	
	String strStartDate;
	String strEndDate;
	
	String f_org;
	String[] f_wLocation; 
	String[] f_department;
	String[] f_service;
	
	List<FillOrganisation> organisationList;
	List<FillWLocation> wLocationList;
	List<FillDepartment> departmentList;
	List<FillServices> serviceList;
	
	
	public String execute() throws Exception { 
		session=request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strSesionEmpId = (String)session.getAttribute(EMPID);
		
		request.setAttribute(PAGE, "/jsp/leave/TravelReport.jsp");
		request.setAttribute(TITLE, "Travel Report");
		
		UtilityFunctions uF = new UtilityFunctions();
		
		if(getStrStartDate()!=null && getStrStartDate().equalsIgnoreCase("NULL")){
			setStrStartDate(null);
			setStrEndDate(null);
		}
		
		if(getStrEndDate()!=null && getStrEndDate().equalsIgnoreCase("NULL")){
			setStrStartDate(null);
			setStrEndDate(null);
		}
		
		viewTravelReport(uF);
		
		return loadTravelReport(uF);

	}
	
	
	private void viewTravelReport(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con=db.makeConnection(con);
			
			Map<String, String> hmEmployeeNameMap = CF.getEmpNameMap(con, null, null);
			if(hmEmployeeNameMap == null) hmEmployeeNameMap = new HashMap<String, String>();
			Map<String, String> hmEmpCode = CF.getEmpCodeMap(con);
			if(hmEmpCode == null) hmEmpCode = new HashMap<String, String>();
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select * from emp_leave_entry where leave_type_id=? and is_approved=1 and istravel=true ");
			if((getStrStartDate()!=null && !getStrStartDate().equals(""))  && (getStrEndDate()!=null && !getStrEndDate().equals(""))){
				sbQuery.append(" and to_date(approval_from::text,'"+DBDATE+"') >='"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE)+"' ");
				sbQuery.append(" and to_date(approval_from::text,'"+DBDATE+"') <='"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"' ");	
			}
			
			sbQuery.append(" and emp_id in(select eod.emp_id from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id  ");
			if(getF_department()!=null && getF_department().length>0){
                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
            }
            
            if(getF_service()!=null && getF_service().length>0){
                sbQuery.append(" and (");
                for(int i=0; i<getF_service().length; i++){
                	 sbQuery.append(" eod.service_id like '%,"+getF_service()[i]+",%'");
                    
                    if(i<getF_service().length-1){
                        sbQuery.append(" OR "); 
                    }
                }
                sbQuery.append(") ");
                
            } 
            
            if(getF_wLocation()!=null && getF_wLocation().length>0){
                sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_wLocation(), ",")+") ");
            }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
            
            if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			sbQuery.append(") order by entrydate desc");
			pst=con.prepareStatement(sbQuery.toString());
			pst.setInt(1, TRAVEL_LEAVE);
//			System.out.println("pst====>"+pst);
			rs=pst.executeQuery();	 
			List<Map<String, String>> reportList = new ArrayList<Map<String, String>>();
			List<String> alTravelId = new ArrayList<String>();
			while(rs.next()){
				Map<String, String> hmInner = new HashMap<String, String>();
				hmInner.put("TRAVEL_ID", rs.getString("leave_id"));
				hmInner.put("EMP_ID", rs.getString("emp_id"));
				hmInner.put("EMP_CODE", uF.showData(hmEmpCode.get(rs.getString("emp_id")), ""));
				hmInner.put("EMP_NAME", uF.showData(hmEmployeeNameMap.get(rs.getString("emp_id")), ""));
				hmInner.put("FROM_DATE", uF.getDateFormat(rs.getString("approval_from"), DBDATE, CF.getStrReportDateFormat()));
				hmInner.put("TO_DATE", uF.getDateFormat(rs.getString("approval_to_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInner.put("NO_DAYS", uF.showData(rs.getString("emp_no_of_leave")+ ((uF.parseToBoolean(rs.getString("is_modify")))?"<div title=\"Canceled\" class=\"leftearly\">&nbsp;</div>":""),""));
				hmInner.put("ENTRY_DATE", uF.getDateFormat(rs.getString("entrydate"), DBDATE, CF.getStrReportDateFormat()));
				hmInner.put("EMP_REASON", uF.showData(rs.getString("reason"),"-"));
				hmInner.put("MANAGER_REASON", uF.showData(rs.getString("manager_reason"),"-"));
				
				if(uF.parseToBoolean(rs.getString("is_concierge"))){
					hmInner.put("IS_CONCIERGE", "Yes");
					if(rs.getString("travel_mode")!=null && !rs.getString("travel_mode").trim().equals("")){
						List<String> alTravelMode=Arrays.asList(rs.getString("travel_mode").trim().split(","));
						StringBuilder sbMode = null;
						for(int j=0;alTravelMode!=null && !alTravelMode.isEmpty() && j<alTravelMode.size();j++){
							if(uF.parseToInt(alTravelMode.get(j).trim()) > 0){
								if(sbMode == null){
									sbMode = new StringBuilder();
									sbMode.append(uF.getTravelMode(uF.parseToInt(alTravelMode.get(j).trim())));
								} else {
									sbMode.append(", "+uF.getTravelMode(uF.parseToInt(alTravelMode.get(j).trim())));
								}
							}
						}
						hmInner.put("TRAVEL_MODE", sbMode!=null ? sbMode.toString() : "");
						
					} else {
						hmInner.put("TRAVEL_MODE", "");
					}
					
					String strBooking = "No";
					String strBookingInfo = "";
					if(uF.parseToBoolean(rs.getString("is_booking"))){
						strBooking = "Yes";
						strBookingInfo = uF.showData(rs.getString("booking_info"), "");
					}
					hmInner.put("IS_BOOKING", strBooking);
					hmInner.put("BOOKING_INFO", strBookingInfo);
					
					String strAccommodation = "No";
					String strAccommodationInfo = "";
					if(uF.parseToBoolean(rs.getString("is_accommodation"))){
						strAccommodation = "Yes";
						strAccommodationInfo = uF.showData(rs.getString("accommodation_info"), "");
					}
					hmInner.put("IS_ACCOMMODATION", strAccommodation);
					hmInner.put("ACCOMMODATION_INFO", strAccommodationInfo);
				} else {
					hmInner.put("IS_CONCIERGE", "No");
					hmInner.put("TRAVEL_MODE", "");
					hmInner.put("IS_BOOKING", "No");
					hmInner.put("BOOKING_INFO", "");
					hmInner.put("IS_ACCOMMODATION", "No");
					hmInner.put("ACCOMMODATION_INFO", "");
				}
				
				reportList.add(hmInner);
				
				if(!alTravelId.contains(rs.getString("leave_id"))){
					alTravelId.add(rs.getString("leave_id"));
				}
			}
			rs.close();
			pst.close();
			request.setAttribute("reportList", reportList);
			
			Map<String, List<Map<String, String>>> hmBooking = new HashMap<String, List<Map<String, String>>>();
			if(alTravelId.size() > 0){
				String strTravelds = StringUtils.join(alTravelId.toArray(),",");
				
				pst = con.prepareStatement("select * from travel_booking_documents where travel_id in ("+strTravelds+")");
				rs = pst.executeQuery();
				while(rs.next()){
					List<Map<String, String>> alData = (List<Map<String, String>>) hmBooking.get(rs.getString("travel_id"));
					if(alData == null) alData = new ArrayList<Map<String,String>>();
					
					Map<String, String> hmInner = new HashMap<String, String>();
					hmInner.put("TRAVEL_BOOKING_ID", rs.getString("travel_booking_id"));
					hmInner.put("TRAVEL_ID", rs.getString("travel_id"));
					hmInner.put("EMP_ID", rs.getString("emp_id"));
					hmInner.put("DOCUMENT_NAME", rs.getString("document_name"));
					hmInner.put("ADDED_BY", rs.getString("added_by"));
					hmInner.put("ADDED_DATE", uF.getDateFormat(rs.getString("added_date"), DBDATE, CF.getStrReportDateFormat()));
					
					String strFilePath = null;
					if(rs.getString("document_name")!=null && !rs.getString("document_name").trim().equals("") && !rs.getString("document_name").trim().equalsIgnoreCase("NULL")){
						if(CF.getStrDocRetriveLocation()==null){
							strFilePath = "<a target=\"blank\" href=\"" + request.getContextPath()+DOCUMENT_LOCATION + rs.getString("document_name") + "\" title=\""+rs.getString("document_name").trim()+"\" ><i class=\"fa fa-file-o\" aria-hidden=\"true\"></i></a>";
						}else{
							strFilePath = "<a target=\"blank\" href=\""+CF.getStrDocRetriveLocation() +I_TRAVELS+"/"+I_DOCUMENT+"/"+rs.getString("emp_id")+"/"+rs.getString("travel_id") +"/"+ rs.getString("document_name") + "\" title=\""+rs.getString("document_name").trim()+"\" ><i class=\"fa fa-file-o\" aria-hidden=\"true\"></i></a>";
						}
					}
					hmInner.put("FILE_PATH", strFilePath);
					
					alData.add(hmInner);
					
					hmBooking.put(rs.getString("travel_id"), alData);
				}
				rs.close();
				pst.close();
			}
			request.setAttribute("hmBooking", hmBooking);
			
			List<List<String>> al = new ArrayList<List<String>>();
			for(int i=0; reportList!=null && i<reportList.size(); i++) { 
				Map<String, String> hmInner = (Map<String, String>)reportList.get(i);
				 
				List<String> alInner = new ArrayList<String>();
				alInner.add(""+(i+1));
				alInner.add(uF.showData(hmInner.get("EMP_CODE"), ""));
				alInner.add(uF.showData(hmInner.get("EMP_NAME"), ""));
				alInner.add(uF.showData(hmInner.get("FROM_DATE"), ""));
				alInner.add(uF.showData(hmInner.get("TO_DATE"), ""));
				alInner.add(uF.showData(hmInner.get("NO_DAYS"), ""));
				alInner.add(uF.showData(hmInner.get("ENTRY_DATE"), ""));
				alInner.add(uF.showData(hmInner.get("EMP_REASON"), ""));
				alInner.add(uF.showData(hmInner.get("IS_CONCIERGE"), ""));
				alInner.add(uF.showData(hmInner.get("TRAVEL_MODE"), ""));
				alInner.add(uF.showData(hmInner.get("IS_BOOKING"), ""));
				alInner.add(uF.showData(hmInner.get("BOOKING_INFO"), ""));
				alInner.add(uF.showData(hmInner.get("IS_ACCOMMODATION"), ""));
				alInner.add(uF.showData(hmInner.get("ACCOMMODATION_INFO"), ""));
				
				StringBuilder sbFilePath = null;
				if(hmBooking.containsKey(hmInner.get("TRAVEL_ID"))){
					List<Map<String, String>> alData = (List<Map<String, String>>)hmBooking.get(hmInner.get("TRAVEL_ID"));
					for(int j = 0; alData!=null && j<alData.size(); j++){
						Map<String, String> hmAttach = (Map<String, String>) alData.get(j);
						if(hmAttach == null) hmAttach = new HashMap<String, String>();
						
						if(hmAttach.get("FILE_PATH")!=null && !hmAttach.get("FILE_PATH").trim().equals("") && !hmAttach.get("FILE_PATH").trim().equalsIgnoreCase("NULL")){
							if(sbFilePath == null){
								sbFilePath = new StringBuilder();
								sbFilePath.append(hmAttach.get("FILE_PATH"));
							} else {
								sbFilePath.append(hmAttach.get("FILE_PATH"));
							}
						} 
					}
				}
				if(sbFilePath == null){
					sbFilePath = new StringBuilder();
				}
				alInner.add(uF.showData(sbFilePath.toString(), ""));				
				
				al.add(alInner);
			}	 
			request.setAttribute("reportList", al);
		} catch (Exception e) {
			e.printStackTrace(); 
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	private String loadTravelReport(UtilityFunctions uF) {
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillServices(getF_org(),uF);
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(),(String) session.getAttribute(WLOCATION_ACCESS));
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		}else{
			organisationList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		
		getSelectedFilter(uF);
		return LOAD;
	}
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		alFilter.add("ORGANISATION");
		if(getF_org()!=null) {
			String strOrg="";
			int k=0;
			for(int i=0;organisationList!=null && i<organisationList.size();i++) {
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
		if(getF_wLocation()!=null) {
			String strLocation="";
			int k=0;
			for(int i=0;wLocationList!=null && i<wLocationList.size();i++) {
				for(int j=0;j<getF_wLocation().length;j++) {
					if(getF_wLocation()[j].equals(wLocationList.get(i).getwLocationId())) {
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
		
		alFilter.add("DEPARTMENT");
		if(getF_department()!=null) {
			String strDepartment="";
			int k=0;
			for(int i=0;departmentList!=null && i<departmentList.size();i++) {
				for(int j=0;j<getF_department().length;j++) {
					if(getF_department()[j].equals(departmentList.get(i).getDeptId())) {
						if(k==0) {
							strDepartment=departmentList.get(i).getDeptName();
						} else {
							strDepartment+=", "+departmentList.get(i).getDeptName();
						}
						k++;
					}
				}
			}
			if(strDepartment!=null && !strDepartment.equals("")) {
				hmFilter.put("DEPARTMENT", strDepartment);
			} else {
				hmFilter.put("DEPARTMENT", "All Departments");
			}
		} else {
			hmFilter.put("DEPARTMENT", "All Departments");
		}
		
		alFilter.add("SERVICE");
		if(getF_service()!=null) {
			String strService="";
			int k=0;
			for(int i=0;serviceList!=null && i<serviceList.size();i++) {
				for(int j=0;j<getF_service().length;j++) {
					if(getF_service()[j].equals(serviceList.get(i).getServiceId())) {
						if(k==0) {
							strService=serviceList.get(i).getServiceName();
						} else {
							strService+=", "+serviceList.get(i).getServiceName();
						}
						k++;
					}
				}
			}
			if(strService!=null && !strService.equals("")) {
				hmFilter.put("SERVICE", strService);
			} else {
				hmFilter.put("SERVICE", "All SBUs");
			}
		} else {
			hmFilter.put("SERVICE", "All SBUs");
		}
		
		if((getStrStartDate()!=null && !getStrStartDate().equals(""))  && (getStrEndDate()!=null && !getStrEndDate().equals(""))){
			alFilter.add("FROMTO");
			hmFilter.put("FROMTO", uF.getDateFormat(getStrStartDate(), DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, CF.getStrReportDateFormat()));
		}

		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}

	public HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
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


	public String getF_org() {
		return f_org;
	}


	public void setF_org(String f_org) {
		this.f_org = f_org;
	}


	public String[] getF_wLocation() {
		return f_wLocation;
	}


	public void setF_wLocation(String[] f_wLocation) {
		this.f_wLocation = f_wLocation;
	}

	public String[] getF_department() {
		return f_department;
	}


	public void setF_department(String[] f_department) {
		this.f_department = f_department;
	}


	public String[] getF_service() {
		return f_service;
	}


	public void setF_service(String[] f_service) {
		this.f_service = f_service;
	}


	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}


	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}


	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}


	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}


	public List<FillDepartment> getDepartmentList() {
		return departmentList;
	}

	public List<FillServices> getServiceList() {
		return serviceList;
	}


	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
	}


}
