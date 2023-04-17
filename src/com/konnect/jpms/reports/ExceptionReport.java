package com.konnect.jpms.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.granule.json.JSONException;
import com.granule.json.JSONObject;
import com.itextpdf.text.BaseColor;
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
import com.lowagie.text.Element;
import com.opensymphony.xwork2.ActionSupport;

public class ExceptionReport extends ActionSupport implements ServletRequestAware, IStatements {
 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strBaseUserType = null; 
	boolean isEmpUserType = false; 
	CommonFunctions CF = null;
 
	String strStartDate;
	String strEndDate;
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
	private String strSearch;
	String exceptionStatus;
	
	private String strLocation;
	private String strDepartment;
	private String strService;
	private String strLevel;
	
	
	public String execute() throws Exception {
		UtilityFunctions uF = new UtilityFunctions(); 
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		strUserType = (String) session.getAttribute(USERTYPE);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		request.setAttribute(TITLE, "Exception Report");
		request.setAttribute(PAGE, "/jsp/reports/ExceptionReport.jsp");	
		if(getExceptionStatus() == null || getExceptionStatus().trim().equals("") || getExceptionStatus().trim().equalsIgnoreCase("NULL")) {
			setExceptionStatus("0");
		}
		
		if(getF_org()==null){
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
		
		if(getStrService() != null && !getStrService().equals("")) {
			setF_service(getStrService().split(","));
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
		execeptionReport(CF,uF,strUserType,session);
		return loadAttendanceReport(uF);
	}

	
	private String execeptionReport(CommonFunctions cF, UtilityFunctions uF, String strUserType, HttpSession session) {
		// TODO Auto-generated method stub
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {			
			List<List<String>> reportList = new ArrayList<List<String>>();
			List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
			List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
			
			con = db.makeConnection(con);
						
			Map<String, String> hmEmpDepartmentMap = CF.getEmpDepartmentNameMap(con);
			Map<String, String> hmEmployeeNameMap = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmEmployeeCodeMap = CF.getEmpCodeMap(con);
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select in_out_timestamp::date , emp_id from attendance_details  where approved !=0 and in_out_timestamp between ? and ? ");
			sbQuery.append(" and emp_id in (select emp_id from employee_official_details  eod  inner join employee_personal_details epd on eod.emp_id=epd.emp_per_id where emp_id > 0 and epd.is_delete=false and epd.is_alive=true and epd.approved_flag=true ");
			if(getF_level()!=null && getF_level().length>0){
                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd, level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
            }
            if(getF_department()!=null && getF_department().length>0){
                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
            }
            
            if(getF_service()!=null && getF_service().length>0){
                sbQuery.append(" and (");
                for(int i=0; i<getF_service().length; i++){
                	sbQuery.append("  service_id like '%,"+getF_service()[i]+",%'");
                    if(i<getF_service().length-1) {
                        sbQuery.append(" OR "); 
                    }
                }
                sbQuery.append(" ) ");
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
            
            if(uF.parseToInt(getExceptionStatus())==1) { 
				sbQuery.append(" and approved=1 ");
			} else if(uF.parseToInt(getExceptionStatus())==2) {
				sbQuery.append(" and approved=-2");
			} 
            sbQuery.append(") group by in_out_timestamp::date ,emp_id  order by in_out_timestamp,emp_id");
            pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
			System.out.println("pst=Search=="+pst);
			rs = pst.executeQuery();
			
			alInnerExport.add(new DataStyle("Exception Report",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Employee Code",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Employee Name",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Department",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Location",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Date",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Plan Roster Details",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Plan Roster Hrs",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Actual Attendance",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Status",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Exception Type",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Actual Worked Hrs",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Is Short Working hrs",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			
			reportListExport.add(alInnerExport);
			
			while (rs.next()) {
				List<String> alInner = new ArrayList<String>();
				 alInnerExport = new ArrayList<DataStyle>();
				alInner.add(hmEmployeeCodeMap.get(rs.getString("emp_id")));//0
				
				alInnerExport.add(new DataStyle(hmEmployeeCodeMap.get(rs.getString("emp_id")),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				//System.out.println("Name===>"+hmEmployeeNameMap.get(rs.getString("emp_id"))+rs.getString("emp_id"));
				alInner.add(hmEmployeeNameMap.get(rs.getString("emp_id")));//1
				alInnerExport.add(new DataStyle(hmEmployeeNameMap.get(rs.getString("emp_id")),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInner.add(hmEmpDepartmentMap.get(rs.getString("emp_id")));//2
				alInnerExport.add(new DataStyle(hmEmpDepartmentMap.get(rs.getString("emp_id")),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInner.add(uF.getDateFormat(rs.getString("in_out_timestamp"),DBDATE, DATE_FORMAT));//3
				alInnerExport.add(new DataStyle(getlocation(con,rs.getString("emp_id")),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(uF.getDateFormat(rs.getString("in_out_timestamp"),DBDATE, DATE_FORMAT),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				ArrayList shift = getshiftdetails(con,rs.getString("emp_id"),rs.getString("in_out_timestamp"));
				alInner.add(shift.get(0).toString());//4
				alInner.add(shift.get(1).toString());//4
				alInnerExport.add(new DataStyle(shift.get(0).toString(),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(shift.get(1).toString(),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				JSONObject jObject =getactualattendance(con,rs.getString("emp_id"),rs.getString("in_out_timestamp"),Double.valueOf(shift.get(1).toString()));		
				alInner.add(jObject.getString("intime")+" - " +jObject.getString("outtime"));//6
				alInnerExport.add(new DataStyle(jObject.getString("intime")+" - " +jObject.getString("outtime"),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInner.add(jObject.getString("status"));//5
				alInnerExport.add(new DataStyle(jObject.getString("status"),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInner.add(jObject.getString("exceptiontype"));//7
				alInner.add(jObject.getString("workinghours"));
				alInner.add(jObject.getString("ishortworkinghrs"));
				alInner.add(getlocation(con,rs.getString("emp_id")));
				alInnerExport.add(new DataStyle(jObject.getString("exceptiontype"),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(jObject.getString("workinghours"),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(jObject.getString("ishortworkinghrs"),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				
				reportList.add(alInner);
				reportListExport.add(alInnerExport);
			}
			rs.close();
			pst.close();	
			request.setAttribute("reportList",reportList);																																								
			session.setAttribute("reportListExport",reportListExport);	
			//System.out.println("SizeExport"+reportListExport.size());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;	
	}


	
	
	
	
	private String getlocation(Connection con, String empid) {
		// TODO Auto-generated method stub
		
		UtilityFunctions uF = new UtilityFunctions();
		String LocationName="";
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select wld.wlocation_name from  employee_official_details eod  inner join  work_location_info  wld on eod.wlocation_id = wld.wlocation_id where eod.emp_id =?");			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1,uF.parseToInt(empid));
			
			//System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				LocationName = rs.getString("wlocation_name");				
				
			}
			rs.close();
			pst.close();	
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return LocationName;
	}
	
	
	private ArrayList getshiftdetails(Connection con, String empid, String date) {
		// TODO Auto-generated method stub
		
		UtilityFunctions uF = new UtilityFunctions();
		String actualTime="";
		PreparedStatement pst = null;
		ResultSet rs = null;
		ArrayList shiftdetails = new ArrayList();
		try {
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select rd.actual_hours ,rd._date,rd.emp_id , sd._from , sd._to , sd.shift_code from shift_details sd inner join roster_details rd on sd.shift_id = rd.shift_id where rd.emp_id =? and _date =?");			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1,uF.parseToInt(empid));
			pst.setDate(2, uF.getDateFormat(date, DBDATE));
			//System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				actualTime = rs.getString("shift_code") +" - ("+rs.getString("_from")+" - "+rs.getString("_to")+" )";				
				shiftdetails.add(actualTime);
				String strHr = ""+uF.convertInHoursMins(rs.getDouble("actual_hours"));
				shiftdetails.add(strHr);
			}
			rs.close();
			pst.close();	
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return shiftdetails;
	}
	
	
	
	private JSONObject getactualattendance(Connection con, String empid, String date,Double rosterhrs) {
		// TODO Auto-generated method stub
		
		UtilityFunctions uF = new UtilityFunctions();
		PreparedStatement pst = null;
		ResultSet rs = null;
		JSONObject jsonObject = new JSONObject();
		String status ="";
		String intime="";
		String outtime="";
		String exceptiontype="-";
		String exceptionin="-";
		String exceptionout="-";
		double workinghours=0.00;
		String wkhrs="";
		String workinghoursstatus="-";
		try {
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from attendance_details  where emp_id=? and in_out_timestamp::date=? order by in_out_timestamp ");			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1,uF.parseToInt(empid));
			pst.setDate(2, uF.getDateFormat(date, DBDATE));
			//System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			int count=0;
			while (rs.next()) {
				if(rs.getInt("approved")==-2) {					
					status ="PENDING";
				} else if(rs.getInt("approved")==1) {
					status ="APPROVED";
				}
				if("OUT".equalsIgnoreCase(rs.getString("in_out"))) {
					workinghours = rs.getDouble("hours_worked");
					String strHr = ""+uF.convertInHoursMins(workinghours);
					strHr.replace(".", ":");
					//System.out.println("Time in hrs==>"+strHr);
					wkhrs =strHr;
				}
				count++;
				if("IN".equalsIgnoreCase(rs.getString("in_out")) ) {					
					intime =uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, TIME_FORMAT);					
					if(rs.getDouble("early_late")<0) {							
						exceptiontype ="Early Login";
						exceptionin ="Early Login";
					} else if(rs.getDouble("early_late")>0) {
						exceptiontype ="Late Login";	
						exceptionin ="Late Login";
					}
				} else if("OUT".equalsIgnoreCase(rs.getString("in_out"))) {
					outtime =  uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, TIME_FORMAT);
					if(rs.getDouble("early_late")<0) {								
						exceptiontype ="Early logout";		
						exceptionout ="Early logout";		
					} else if(rs.getDouble("early_late")>0) {
						exceptiontype ="Late log out";		
						exceptionout ="Late log out";		
					}
				}				
			}
			
			if(count==1) {
				if(intime.equalsIgnoreCase("")) {
					intime ="-";
				} else if(outtime.equalsIgnoreCase("")) {
					outtime="-";
				}
			}
			
			if(!exceptionin.equalsIgnoreCase("-") && !exceptionout.equalsIgnoreCase("-")) {
				exceptiontype =exceptionin+"-"+exceptionout;
			}
			if(rosterhrs>workinghours) {
				workinghoursstatus="True";
			} else {
				workinghoursstatus="False";
			}
			
			jsonObject.put("status", status);
			jsonObject.put("outtime", outtime);
			jsonObject.put("intime", intime);
			jsonObject.put("exceptiontype", exceptiontype);
			jsonObject.put("workinghours", wkhrs);
			jsonObject.put("ishortworkinghrs",workinghoursstatus);
		//	System.out.println("count"+count);
			//System.out.println("InTime==>"+intime+"outTime==>"+outtime+"status===>"+status+"Exceptiontype===>"+exceptiontype);
			rs.close();
			pst.close();	
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
		return jsonObject;
	}

	public String loadAttendanceReport(UtilityFunctions uF) {
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillServices(getF_org(),uF);
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(),(String) session.getAttribute(WLOCATION_ACCESS));
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		}else{
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

	public List<FillServices> getServiceList() {
		return serviceList;
	}

	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
	}



	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}

	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}

	public String getStrSearch() {
		return strSearch;
	}

	public void setStrSearch(String strSearch) {
		this.strSearch = strSearch;
	}
	
	public String getStrUserType() {
		return strUserType;
	}

	public void setStrUserType(String strUserType) {
		this.strUserType = strUserType;
	}

	public String getStrBaseUserType() {
		return strBaseUserType;
	}

	public void setStrBaseUserType(String strBaseUserType) {
		this.strBaseUserType = strBaseUserType;
	}

	public boolean isEmpUserType() {
		return isEmpUserType;
	}

	public void setEmpUserType(boolean isEmpUserType) {
		this.isEmpUserType = isEmpUserType;
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

	public List<FillLevel> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}

	public String getExceptionStatus() {
		return exceptionStatus;
	}

	public void setExceptionStatus(String exceptionStatus) {
		this.exceptionStatus = exceptionStatus;
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

	public String getStrService() {
		return strService;
	}

	public void setStrService(String strService) {
		this.strService = strService;
	}

	public String getStrLevel() {
		return strLevel;
	}

	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}
	
}


