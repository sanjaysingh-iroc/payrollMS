package com.konnect.jpms.employee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.reports.UserInfo;
import com.konnect.jpms.select.FillClassAndDivision;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillEducation;
import com.konnect.jpms.select.FillEmployeeStatus;
import com.konnect.jpms.select.FillEmploymentType;
import com.konnect.jpms.select.FillGrade;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class StudentsAndStaffListView extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strBaseUserType = null;
	String strSessionEmpId = null;
	String strUsertypeId;
	
	private String alertStatus;
	private String alert_type;
	
	private List<FillClassAndDivision> classList;
	
	private String page;  
	 
	private String strLocation;
	private String strstrClass;
	private String userType;
	
	private String[] f_strWLocation; 
	private String[] strClass;

	private List<FillWLocation> wLocationList;
	private List<FillOrganisation> organisationList;
	
	private String f_org;
	
	CommonFunctions CF=null;
	String strAction = null;
	
	private String alertID;
	private String fromPage;
	
	public String execute() throws Exception {
		UtilityFunctions uF = new UtilityFunctions();		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strUserType = (String) session.getAttribute(USERTYPE);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strUsertypeId = (String) session.getAttribute(BASEUSERTYPEID);
		
		request.setAttribute(PAGE, "/jsp/employee/StudentsAndStaffListView.jsp");
		
		strAction = request.getServletPath();
		if(strAction!=null) {
			strAction = strAction.replace("/","");
		}
		
		/*if(getF_strWLocation()==null){
			setF_strWLocation((String)session.getAttribute(WLOCATIONID));
		}*/
		
		String strDomain = request.getServerName().split("\\.")[0];
		CF.deleteWRUserAlerts(CF, request, strDomain, getAlertID());
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		
//		if(getAlertStatus()!=null && getAlert_type()!=null && getAlert_type().equals(EMP_TERMINATED_ALERT)){
//			updateUserAlerts1();
//		}
		
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
			if(uF.parseToInt(getF_org())<=0) {
				for(int i=0; organisationList != null && i<organisationList.size(); i++) {
					if(uF.parseToInt(organisationList.get(i).getOrgId()) == uF.parseToInt((String) session.getAttribute(ORGID))) {
						setF_org((String) session.getAttribute(ORGID));
					} else {
						if(i==0) {
							setF_org(organisationList.get(0).getOrgId());
						}
					}
				}
			}
		} else {
			if (uF.parseToInt(getF_org()) <= 0) {
				setF_org((String) session.getAttribute(ORGID));
			}
			organisationList = new FillOrganisation(request).fillOrganisation();
		}
		
		if(getStrLocation() != null && !getStrLocation().equals("")) {
			setF_strWLocation(getStrLocation().split(","));
		} else {
			setF_strWLocation(null);
		}
		if(getStrstrClass() != null && !getStrstrClass().equals("")) {
			setStrClass(getStrstrClass().split(","));
		} else {
			setStrClass(null);
		}
		
		viewStudentAndTeacher(uF);
		return loadEmployee(uF);
		
	}

	
	private void updateUserAlerts() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		int nEmpId = uF.parseToInt((String)session.getAttribute(EMPID));
		try {
			con = db.makeConnection(con);
			String strDomain = request.getServerName().split("\\.")[0];
			UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
			userAlerts.setStrDomain(strDomain);
			userAlerts.setStrEmpId(""+nEmpId);
			if(strAction!=null && strAction.equalsIgnoreCase("StudentAndStaffLListView.action")) {
				userAlerts.set_type(NEW_JOINEES_ALERT);
			} else if(strAction!=null && (strAction.equalsIgnoreCase("PendingEmployeeReport.action"))) {
				userAlerts.set_type(NEW_JOINEE_PENDING_ALERT);
			}
			userAlerts.setStatus(UPDATE_ALERT);
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
	
	
	public String loadEmployee(UtilityFunctions uF) {
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
		} else {
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		classList = new FillClassAndDivision(request).fillClass(getF_org());
		
		getSelectedFilter(uF);
		
		return LOAD;
	}

	public String viewStudentAndTeacher(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null, pst_sid = null;
		ResultSet rs = null, rs_sid = null;
		Database db = new Database();
		db.setRequest(request);
//		EncryptionUtility eU = new EncryptionUtility();

		try {
			List<List<String>> al = new ArrayList<List<String>>();
			
//			Map<String, List<String>> hm = new HashMap<String, List<String>>();
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
			request.setAttribute("hmFeatureStatus", hmFeatureStatus);
			
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from emp_family_members ");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst in workring for GHR HR REC CEO CFO ACC ===>> " + pst);
//			System.out.println("pst1 =======> " + pst); 
			rs = pst.executeQuery();
			Map<String, Map<String, String>> hmEmpFamilyData = new HashMap<String, Map<String,String>>();
			while (rs.next()) {
				Map<String, String> hmFamilyData = hmEmpFamilyData.get(rs.getString("emp_id"));
				if(hmFamilyData == null) hmFamilyData = new HashMap<String, String>();
				
				if(rs.getString("member_type") !=null && rs.getString("member_type").equals("MOTHER")) {
					hmFamilyData.put("MOTHER", rs.getString("member_name"));
				} else if(rs.getString("member_type") !=null && rs.getString("member_type").equals("FATHER")) {
					hmFamilyData.put("FATHER", rs.getString("member_name"));
				}
				
				hmEmpFamilyData.put(rs.getString("emp_id"), hmFamilyData);
			}
			rs.close();
			pst.close();
				

			
			sbQuery = new StringBuilder();
			sbQuery.append("select * from employee_personal_details epd, employee_official_details eod, user_details ud WHERE epd.is_alive=? and eod.emp_id >0 " +
				" and epd.emp_per_id=eod.emp_id and eod.emp_id=ud.emp_id and approved_flag=? ");
			if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
                sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
            } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+") ");
			}
            
            if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+") ");
			}
            if(uF.parseToInt(getUserType())==0) {
            	sbQuery.append(" and ud.usertype_id=3 ");
            } else {
            	sbQuery.append(" and ud.usertype_id!=3 ");
            }
			sbQuery.append(" order by empcode, emp_status, emp_fname,emp_lname");
            pst = con.prepareStatement(sbQuery.toString());
			pst.setBoolean(1, true);
			pst.setBoolean(2, true);
//			System.out.println("pst in workring for GHR HR REC CEO CFO ACC ===>> " + pst);
//			System.out.println("pst1 =======> " + pst); 
			rs = pst.executeQuery();
			String strEmpId = null;
			Map<String, Map<String, String>> hmWorkLocation = CF.getWorkLocationMap(con);
			Map<String, String> hmUserType = CF.getUserTypeMap(con);
			while (rs.next()) {
				if(rs.getInt("emp_per_id")<=0) {
					continue;
				}
				List<String> alInner = new ArrayList<String>();
				if(uF.parseToInt(getUserType())==0) {
					Map<String, String> hmFamilyData = hmEmpFamilyData.get(rs.getString("emp_per_id"));
					if(hmFamilyData == null) hmFamilyData = new HashMap<String, String>();
					
					strEmpId = rs.getString("emp_per_id");
					alInner.add(rs.getString("emp_per_id")); //0
					alInner.add(rs.getString("empcode")); //1
					StringBuilder sbEmpName = new StringBuilder();
					sbEmpName.append(uF.showData(rs.getString("emp_fname"), ""));
					if(rs.getString("emp_mname") != null && !rs.getString("emp_mname").equals("")) {
						sbEmpName.append(" " + uF.showData(rs.getString("emp_mname"), ""));
					}
					sbEmpName.append(" " + uF.showData(rs.getString("emp_lname"), ""));
					
					alInner.add(sbEmpName.toString()); //2
					alInner.add(""); //3 class
					alInner.add(""); //4 section
					alInner.add(uF.showData(hmFamilyData.get("FATHER"), "")); //5 
					alInner.add(uF.showData(hmFamilyData.get("MOTHER"), "")); //6 
					alInner.add(uF.showData(rs.getString("emp_email"), "")); //7
					alInner.add(uF.getDateFormat(rs.getString("joining_date"), DBDATE, CF.getStrReportDateFormat())); //8
					alInner.add(uF.getDateFormat(rs.getString("joining_date"), DBDATE, CF.getStrReportDateFormat())); //9
					alInner.add(uF.showData(uF.getGender(rs.getString("emp_gender")), "")); //10
					alInner.add("<a class=\"fa fa-edit\" style=\"padding: 0px 0px 0px 2px;\" href=\"AddEditStudentAndTeacher.action?operation=U&userId=" + strEmpId + "\" > </a>" +
					"<a class=\"fa fa-trash\" style=\"float:right; padding: 0px;\" href=\"javascript:void(0)\" onclick=\"(confirm('Are you sure, you want to delete this user?') ? window.location='AddEditStudentAndTeacher.action?pageType=WE&operation=D&userId="+strEmpId+"' : '')\"> </a>"); //11
					alInner.add(uF.showData(rs.getString("emp_image"), "")); //12
				} else {
					
					Map<String, String> hmFamilyData = hmEmpFamilyData.get(rs.getString("emp_per_id"));
					if(hmFamilyData == null) hmFamilyData = new HashMap<String, String>();
					
					strEmpId = rs.getString("emp_per_id");
					alInner.add(rs.getString("emp_per_id")); //0
					alInner.add(hmUserType.get(rs.getString("usertype_id"))); //1
					StringBuilder sbEmpName = new StringBuilder();
					sbEmpName.append(uF.showData(rs.getString("emp_fname"), ""));
					if(rs.getString("emp_mname") != null && !rs.getString("emp_mname").equals("")) {
						sbEmpName.append(" " + uF.showData(rs.getString("emp_mname"), ""));
					}
					sbEmpName.append(" " + uF.showData(rs.getString("emp_lname"), ""));
					
					alInner.add(sbEmpName.toString()); //2
					alInner.add(uF.showData(rs.getString("emp_email"), "")); //3
					alInner.add(uF.showData(uF.getGender(rs.getString("emp_gender")), "")); //4
					alInner.add("<a class=\"fa fa-edit\" style=\"padding: 0px 0px 0px 2px;\" href=\"AddEditStudentAndTeacher.action?operation=U&userId=" + strEmpId + "\" > </a>" +
					"<a class=\"fa fa-trash\" style=\"float:right; padding: 0px;\" href=\"javascript:void(0)\" onclick=\"(confirm('Are you sure, you want to delete this user?') ? window.location='AddEditStudentAndTeacher.action?pageType=WE&operation=D&userId="+strEmpId+"' : '')\"> </a>"); //5
					alInner.add(uF.showData(rs.getString("emp_image"), "")); //6
				}
				al.add(alInner);
//				hm.put(rs.getString("emp_off_id"), alInner);
			}
			rs.close();
			pst.close();

			boolean isEmpLimit = false;
//				System.out.println("al.size() ===>> " + al.size() + "uF.parseToInt(CF.getStrMaxEmployee()) ===>> " + uF.parseToInt(CF.getStrMaxEmployee()));
			if(al != null && al.size()>=uF.parseToInt(CF.getStrMaxEmployee())) {
				isEmpLimit = true;
			}
			request.setAttribute("isEmpLimit", ""+isEmpLimit);
			request.setAttribute("strEmpLimit", CF.getStrMaxEmployee());
				
			request.setAttribute("reportList", al);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeResultSet(rs_sid);
			db.closeStatements(pst);
			db.closeStatements(pst_sid);
			db.closeConnection(con);
		}
		return SUCCESS;
	}

	

	
 private void getSelectedFilter(UtilityFunctions uF) {
		
	Map<String,String> hmFilter=new HashMap<String, String>();
	List<String> alFilter = new ArrayList<String>();

//	System.out.println("getF_service()----"+getF_service());
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
	if(getF_strWLocation()!=null) {
		String strLocation="";
		int k=0;
		for(int i=0;wLocationList!=null && i<wLocationList.size();i++) {
			for(int j=0;j<getF_strWLocation().length;j++) {
				if(getF_strWLocation()[j].equals(wLocationList.get(i).getwLocationId())) {
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
	
	alFilter.add("USERTYPE");
	if(uF.parseToInt(getUserType()) ==0) {
		hmFilter.put("USERTYPE", "Student");
	} else {
		hmFilter.put("USERTYPE", "Staff");
	}
	
	alFilter.add("CLASS");
	if(getStrClass()!=null) {
		String strDepartment="";
		int k=0;
		for(int i=0;classList!=null && i<classList.size();i++) {
			for(int j=0;j<getStrClass().length;j++) {
				if(getStrClass()[j].equals(classList.get(i).getClassDivId())) {
					if(k==0) {
						strDepartment = classList.get(i).getClassDivName();
					} else {
						strDepartment+=", "+classList.get(i).getClassDivName();
					}
					k++;
				}
			}
		}
		if(strDepartment!=null && !strDepartment.equals("")) {
			hmFilter.put("CLASS", strDepartment);
		} else {
			hmFilter.put("CLASS", "All Classes");
		}
	} else {
		hmFilter.put("CLASS", "All Classes");
	}
	
	
	String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
	request.setAttribute("selectedFilter", selectedFilter);
}
	
	
	String empName;

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
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

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
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

	public String getAlertID() {
		return alertID;
	}

	public void setAlertID(String alertID) {
		this.alertID = alertID;
	}

	public String getStrLocation() {
		return strLocation;
	}

	public void setStrLocation(String strLocation) {
		this.strLocation = strLocation;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	public String getStrstrClass() {
		return strstrClass;
	}

	public void setStrstrClass(String strstrClass) {
		this.strstrClass = strstrClass;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String[] getF_strWLocation() {
		return f_strWLocation;
	}

	public void setF_strWLocation(String[] f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
	}

	public String[] getStrClass() {
		return strClass;
	}

	public void setStrClass(String[] strClass) {
		this.strClass = strClass;
	}

	public List<FillClassAndDivision> getClassList() {
		return classList;
	}

	public void setClassList(List<FillClassAndDivision> classList) {
		this.classList = classList;
	}
	
}
