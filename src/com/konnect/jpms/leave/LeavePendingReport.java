package com.konnect.jpms.leave;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.konnect.jpms.export.DataStyle;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class LeavePendingReport extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF=null;
	String strUserType = null;
	String strUserTypeId = null; 
	String strBaseUserType = null;
	String strBaseUserTypeId = null;
	String strSessionEmpId = null; 
	private static Logger log = Logger.getLogger(LeaveApprovedReport.class);
	 
	String alertStatus;
	String alert_type;
	
	String strStartDate;
	String strEndDate;
	
	String strLocation;
	String strDepartment;
	String strSbu;
	String strLevel;
	
	String f_org;
	String[] f_wLocation; 
	String[] f_level;
	String[] f_department;
	String[] f_service;
	
	List<FillOrganisation> organisationList;
	List<FillWLocation> wLocationList;
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	List<FillServices> serviceList;
	 
	public String execute() throws Exception {
		UtilityFunctions uF = new UtilityFunctions(); 
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
			  
		strUserType = (String) session.getAttribute(USERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		strBaseUserTypeId = (String) session.getAttribute(BASEUSERTYPEID);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		
		request.setAttribute(PAGE, PLeavePendingReport);
		request.setAttribute(TITLE, "Leave Pending Report");
		
		if(getF_org()==null) {
			setF_org((String)session.getAttribute(ORGID));
		}
		
		if(getF_org()==null || getF_org().trim().equals("")) {
			setF_org((String)session.getAttribute(ORGID));
		}
		
		if(getStrLocation() != null && !getStrLocation().equals("")) {
			setF_wLocation(getStrLocation().split(","));
		} else {
			setF_wLocation(null);
		}
		if(getStrDepartment() != null && !getStrDepartment().equals("")) {
			setF_department(getStrDepartment().split(","));
		} else {
			setF_department(null);
		}
		if(getStrSbu() != null && !getStrSbu().equals("")) {
			setF_service(getStrSbu().split(","));
		} else {
			setF_service(null);
		}
		if(getStrLevel() != null && !getStrLevel().equals("")) {
			setF_level(getStrLevel().split(","));
		} else {
			setF_level(null);
		}
		
		if(getStrStartDate()!=null && getStrStartDate().equalsIgnoreCase("NULL")) {
			setStrStartDate(null);
			setStrEndDate(null);
		}
		
		if(getStrEndDate()!=null && getStrEndDate().equalsIgnoreCase("NULL")) {
			setStrStartDate(null);
			setStrEndDate(null);
		}
		
		if(getStrStartDate()==null && getStrEndDate()==null) {
			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
			int nMaxDate = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
			int nMinDate = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
			
			setStrStartDate(uF.getDateFormat(nMinDate+"/"+(cal.get(Calendar.MONTH)+ 1)+"/"+cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT));
			setStrEndDate(uF.getDateFormat(nMaxDate+"/"+(cal.get(Calendar.MONTH)+ 1)+"/"+cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT));
		}
		viewLeavePending(uF);
		return loadManagerLeavePending(uF);

	}
	
	
	 
	private void viewLeavePending(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con=db.makeConnection(con);
			List<List<String>> reportList = new ArrayList<List<String>>();
			List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
			List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
			
			Map<String, String> hmUserTypeMap = CF.getUserTypeMap(con);
			if(hmUserTypeMap == null) hmUserTypeMap = new HashMap<String, String>();
			Map<String, String> hmDepartment =CF.getDepartmentMap(con, null, null);
			if(hmDepartment == null) hmDepartment = new HashMap<String, String>();
			Map<String, Map<String, String>> hmEmpInfo = CF.getEmpInfoMap(con, false);
			if(hmEmpInfo == null) hmEmpInfo = new HashMap<String, Map<String,String>>();
		
			Map<String, String> hmEmployeeNameMap = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmEmpDesigMap = CF.getEmpDesigMap(con);
			Map<String, String> hmEmpDepartmentMap = CF.getEmpDepartmentMap(con);
			
			StringBuilder sbQuery=new StringBuilder();

			sbQuery.append("select a.* from (select * from emp_leave_entry ele,leave_type lt where ele.leave_type_id=lt.leave_type_id and ele.leave_type_id > 0"+
				"and ele.is_approved=0 and (ele.is_modify is null or ele.is_modify=false) ");
			if(getStrStartDate()!=null && getStrEndDate()!=null && getStrStartDate().length()>0 && getStrEndDate().length()>0) {
				sbQuery.append("  and to_date(approval_from::text,'"+DBDATE+"') >='"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE)+"' ");
				sbQuery.append("  and to_date(approval_from::text,'"+DBDATE+"') <='"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"'  ");	
			}
			sbQuery.append("and emp_id in (select emp_id from employee_official_details  where emp_id > 0 ");
			if(getF_level()!=null && getF_level().length>0) {
                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
            }
            if(getF_department()!=null && getF_department().length>0) {
                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
            }
            
            if(getF_service()!=null && getF_service().length>0) {
                sbQuery.append(" and (");
                for(int i=0; i<getF_service().length; i++) {
                	sbQuery.append(" service_id like '%,"+getF_service()[i]+",%'");
                    
                    if(i<getF_service().length-1) {
                        sbQuery.append(" OR "); 
                    }
                }
                sbQuery.append(" ) ");
            } 
            
            if(getF_wLocation()!=null && getF_wLocation().length>0) {
                sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_wLocation(), ",")+") ");
            } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
            
            if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			sbQuery.append(")");
			sbQuery.append(") a, employee_personal_details epd where a.emp_id=epd.emp_per_id order by epd.emp_fname, epd.emp_lname");
			pst=con.prepareStatement(sbQuery.toString());
			rs=pst.executeQuery();	 
			
			alInnerExport.add(new DataStyle("Leave Pending Report",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Employee Id",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Employee Name",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Designation",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Department",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Date of Joining",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Leave Type",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Apply Date",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("From",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("To",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("No.of Days",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Emp Reason",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Approving Profile",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			
			reportListExport.add(alInnerExport);
			
			while(rs.next()) {
				alInnerExport = new ArrayList<DataStyle>();
				List<String> alInner = new ArrayList<String>();
				alInner.add(CF.getEmpCodeByEmpId(con, rs.getString("emp_id")));
				alInner.add(hmEmployeeNameMap.get(rs.getString("emp_id")));
				alInner.add(rs.getString("leave_type_name"));
				alInner.add(uF.getDateFormat(rs.getString("entrydate"), DBDATE, CF.getStrReportDateFormat()));
				
				alInner.add(uF.getDateFormat(rs.getString("approval_from"), DBDATE, CF.getStrReportDateFormat()));
				alInner.add(uF.getDateFormat(rs.getString("approval_to_date"), DBDATE, CF.getStrReportDateFormat()));
				
				alInner.add(uF.showData(rs.getString("emp_no_of_leave")+ ((uF.parseToBoolean(rs.getString("is_modify")))?"<div title=\"Canceled\" class=\"leftearly\">&nbsp;</div>":""),""));
				alInner.add(uF.showData(rs.getString("reason"),"-"));
				alInner.add(uF.showData(rs.getString("manager_reason"),"-"));
				
				alInner.add(rs.getString("leave_id"));
				alInner.add(rs.getString("leave_type_id")); 
				
				if(uF.parseToBoolean(rs.getString("is_modify"))) {
					alInner.add("Last Modified on "+uF.getDateFormat(rs.getString("modify_date"), DBDATE, CF.getStrReportDateFormat())+" by "+hmEmployeeNameMap.get(rs.getString("modify_by")));
				} else {
					alInner.add("Modify");
				}
				alInner.add(rs.getString("is_approved"));
				
				String empDesignation = hmEmpDesigMap.get(rs.getString("emp_id"));
				alInner.add(empDesignation);
			
				String dpt =hmEmpDepartmentMap.get(rs.getString("emp_id"));
				String dptNme = hmDepartment.get(dpt);
				alInner.add(dptNme);
				
				Map<String, String> empInfo = hmEmpInfo.get(rs.getString("emp_id"));
				if(empInfo == null) empInfo = new HashMap<String, String>();
				
				alInner.add(uF.getDateFormat(uF.showData(empInfo.get("JOINING_DATE"), ""), DBDATE, CF.getStrReportDateFormat()));
				reportList.add(alInner);
				
				alInnerExport.add(new DataStyle(""+CF.getEmpCodeByEmpId(con, rs.getString("emp_id")),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(""+hmEmployeeNameMap.get(rs.getString("emp_id")),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(""+empDesignation,Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(""+dptNme,Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(""+uF.showData(empInfo.get("JOINING_DATE"), ""),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(""+rs.getString("leave_type_name"),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(""+uF.getDateFormat(rs.getString("entrydate"), DBDATE, CF.getStrReportDateFormat()),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(""+uF.getDateFormat(rs.getString("approval_from"), DBDATE, CF.getStrReportDateFormat()),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(""+uF.getDateFormat(rs.getString("approval_to_date"), DBDATE, CF.getStrReportDateFormat()),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(""+uF.showData(rs.getString("emp_no_of_leave")," "),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(""+uF.showData(rs.getString("reason"),"-"),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(""+uF.showData(rs.getString("manager_reason"),"-"),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				
				reportListExport.add(alInnerExport);
			}
			rs.close();
			pst.close();
			request.setAttribute("reportList", reportList);
			session.setAttribute("reportListExport", reportListExport);
					
		} catch (Exception e) {
			e.printStackTrace(); 
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	

	public String loadManagerLeavePending(UtilityFunctions uF) {
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillServices(getF_org(),uF);
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(),(String) session.getAttribute(WLOCATION_ACCESS));
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		} else {
			organisationList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		getSelectedFilter(uF);
		return "load";
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
		
		alFilter.add("LEVEL");
		if(getF_level()!=null) {
			String strLevel="";
			int k=0;
			for(int i=0;levelList!=null && i<levelList.size();i++) {
				for(int j=0;j<getF_level().length;j++) {
					if(getF_level()[j].equals(levelList.get(i).getLevelId())) {
						if(k==0) {
							strLevel=levelList.get(i).getLevelCodeName();
						} else {
							strLevel+=", "+levelList.get(i).getLevelCodeName();
						}
						k++;
					}
				}
			}
			if(strLevel!=null && !strLevel.equals("")) {
				hmFilter.put("LEVEL", strLevel);
			} else {
				hmFilter.put("LEVEL", "All Levels");
			}
		} else {
			hmFilter.put("LEVEL", "All Levels");
		}
		
		if((getStrStartDate()!=null && !getStrStartDate().equals(""))  && (getStrEndDate()!=null && !getStrEndDate().equals(""))){
			alFilter.add("FROMTO");
			hmFilter.put("FROMTO", uF.getDateFormat(getStrStartDate(), DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, CF.getStrReportDateFormat()));
		}

		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}

	private HttpServletRequest request;

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

	public String[] getF_level() {
		return f_level;
	}

	public void setF_level(String[] f_level) {
		this.f_level = f_level;
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

	public void setDepartmentList(List<FillDepartment> departmentList) {
		this.departmentList = departmentList;
	}

	public List<FillLevel> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}

	public List<FillServices> getServiceList() {
		return serviceList;
	}

	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
	}

	public String getStrLocation() {
		return strLocation;
	}

	public void setStrLocation(String strLocation) {
		this.strLocation = strLocation;
	}

	public String getStrDepartment() {
		return strDepartment;
	}

	public void setStrDepartment(String strDepartment) {
		this.strDepartment = strDepartment;
	}

	public String getStrSbu() {
		return strSbu;
	}

	public void setStrSbu(String strSbu) {
		this.strSbu = strSbu;
	}

	public String getStrLevel() {
		return strLevel;
	}

	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}

}
