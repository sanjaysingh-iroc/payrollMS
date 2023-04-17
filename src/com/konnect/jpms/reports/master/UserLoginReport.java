package com.konnect.jpms.reports.master;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillUserStatus;
import com.konnect.jpms.select.FillUserType;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class UserLoginReport extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	String strAlphaVlaue = null;
	List<FillUserType> userTypeList;
	List<FillEmployee> empCodeList;
	List<FillUserStatus> userStatusList;
	
	
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	List<FillWLocation> wLocationList;
	List<FillServices> serviceList;
	List<FillOrganisation> organisationList;

	String f_org;
	String f_strWLocation; 
	String f_department; 
	String f_level; 
	String f_service;
	
	
	String strUserType;
	String strSessionEmpId;
	HttpSession session;
	CommonFunctions CF;
	private static Logger log = Logger.getLogger(UserLoginReport.class);
	
	public String execute() throws Exception {

		UtilityFunctions uF = new UtilityFunctions();
		request.setAttribute(PAGE, "/jsp/reports/master/UserLoginReport.jsp");
		request.setAttribute(TITLE, "User Login Report");
		
		session = request.getSession();
		
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)
			return LOGIN;
		
		if(getF_strWLocation()==null){
			setF_strWLocation((String)session.getAttribute(WLOCATIONID));
		}
		if(getF_org()==null){
			setF_org((String)session.getAttribute(ORGID));
		}
		
		strAlphaVlaue = (String)request.getParameter("alphaValue");  
		
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		String strUserIdDelete = (String)request.getParameter("D");
		
		if(strUserIdDelete!=null && strUserIdDelete.length()>0){
			deleteUser(strUserIdDelete);
		}
		
		viewUser(uF);			
		return loadUser(uF);
	}
	
	public String loadUser(UtilityFunctions uF) {
		
		empCodeList = new FillEmployee(request).fillEmployeeCode(strUserType, strSessionEmpId);
		userStatusList = new FillUserStatus().fillUserStatus();
		userTypeList = new FillUserType(request).fillUserType();
		
		organisationList = new FillOrganisation(request).fillOrganisation();
		
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org())); 
		
		wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		serviceList = new FillServices(request).fillServices(getF_org(), uF);
		
		
		request.setAttribute("empCodeList", empCodeList);
		request.setAttribute("userStatusList", userStatusList);
		request.setAttribute("userTypeList", userTypeList);
		
		int empCodeId, i;
		String employeeCode;
		StringBuilder sbEmpCodeList = new StringBuilder();
		sbEmpCodeList.append("{");
	    for(i=0; i<empCodeList.size()-1;i++ ) {
	    		empCodeId = Integer.parseInt((empCodeList.get(i)).getEmployeeId());
	    		employeeCode = empCodeList.get(i).getEmployeeCode();
	    		sbEmpCodeList.append("\""+ empCodeId+"\":\""+employeeCode+"\",");
	    }
	    if(i>0){
		    empCodeId = Integer.parseInt((empCodeList.get(i)).getEmployeeId());
		    employeeCode = empCodeList.get(i).getEmployeeCode();
			sbEmpCodeList.append("\""+ empCodeId+"\":\""+employeeCode+"\"");
	    }
	    sbEmpCodeList.append("}");
	    request.setAttribute("sbEmpCodeList", sbEmpCodeList.toString());
	    
		int i1;
		String userStatusName, userStatusId;
		StringBuilder sbUserStatusList = new StringBuilder();
		sbUserStatusList.append("{");
	    for(i1=0; i1<userStatusList.size()-1;i1++ ) {
	    		userStatusId = (userStatusList.get(i1)).getStatusId();
	    		userStatusName = userStatusList.get(i1).getStatusName();
	    		sbUserStatusList.append("\""+ userStatusId+"\":\""+userStatusName+"\",");
	    }
	    
	    if(i1>0){
	    	userStatusId = (userStatusList.get(i1)).getStatusId();
		    userStatusName = userStatusList.get(i1).getStatusName();
			sbUserStatusList.append("\""+ userStatusId+"\":\""+userStatusName+"\"");
	    }
	    	
	    sbUserStatusList.append("}");
	    request.setAttribute("sbUserStatusList", sbUserStatusList.toString());
	    
		int userTypeId, i11;
		String userTypeName;
		StringBuilder sbUserTypeList = new StringBuilder();
		sbUserTypeList.append("{");
	    for(i11=0; i11<userTypeList.size()-1;i11++ ) {
	    		userTypeId = Integer.parseInt((userTypeList.get(i11)).getUserTypeId());
	    		userTypeName = userTypeList.get(i11).getUserTypeName();
	    		sbUserTypeList.append("\""+ userTypeId+"\":\""+userTypeName+"\",");
	    }
	    if(i11>0){
	    	userTypeId = Integer.parseInt((userTypeList.get(i11)).getUserTypeId());
		    userTypeName = userTypeList.get(i11).getUserTypeName();
			sbUserTypeList.append("\""+ userTypeId+"\":\""+userTypeName+"\"");
	    }
	    	
	    sbUserTypeList.append("}");
	    request.setAttribute("sbUserTypeList", sbUserTypeList.toString());
	    
		return LOAD;
	}
	
	public String viewUser(UtilityFunctions uF){
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		

		try {

			List<List<String>> al = new ArrayList<List<String>>();
			List<String> alInner = new ArrayList<String>();
			
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
//			Map<String, String> hmEmpType = CF.getEmpTypeMap(con);
			
			pst = con.prepareStatement("select * from settings where options=?");
			pst.setString(1, O_SHOW_PASSWORD);
			rs = pst.executeQuery();
			boolean isShowPassword = false;
			while(rs.next()){
				isShowPassword = uF.parseToBoolean(rs.getString("value"));
			}
			rs.close();
			pst.close();
			
			
			
			if(strAlphaVlaue!=null && strAlphaVlaue.length()>0){
				pst = con.prepareStatement(selectUserRAlpha);
				pst.setString(1, strAlphaVlaue+"%");
			}else{
				
				
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("SELECT * FROM user_details ud, user_type ut, employee_personal_details epd, employee_official_details eod WHERE ut.user_type_id=ud.usertype_id  and epd.emp_per_id = ud.emp_id  and epd.emp_per_id = eod.emp_id and eod.emp_id = ud.emp_id ");

				if(uF.parseToInt(getF_strWLocation())>0){
					sbQuery.append(" and wlocation_id = "+uF.parseToInt(getF_strWLocation()));
				}
				if(uF.parseToInt(getF_service())>0){
					sbQuery.append(" and service_id like '%,"+uF.parseToInt(getF_service()) +",%'");
				}
				if(uF.parseToInt(getF_department())>0){
					sbQuery.append(" and depart_id = "+uF.parseToInt(getF_department()));
				}
				if(uF.parseToInt(getF_level())>0){
					sbQuery.append(" and grade_id in (select grade_id from designation_details dd, level_details ld, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id = "+uF.parseToInt(getF_level())+")");
				}
				if(uF.parseToInt(getF_org())>0){
					sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
				}
				sbQuery.append(" order by empcode, status, emp_fname, emp_lname ");
				
				pst = con.prepareStatement(sbQuery.toString());
				
//				pst = con.prepareStatement(selectUserR);
			}
			
			rs = pst.executeQuery();
			int nCount=0;
			while(rs.next()){
				
				if(rs.getInt("emp_id")<0){
					continue;
				}
				
				
				alInner = new ArrayList<String>();
				alInner.add(Integer.toString(rs.getInt("user_id")));
				
				alInner.add(rs.getString("empcode"));
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				alInner.add(rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname"));
				
				String username=rs.getString("username");
				/*username+="<div id=\"editExist_"+nCount+"\" style=\"float:right;\"><a href=\"javascript:void(0)\" class=\"edit\" " +
						"onclick=\"changeUserName('"+rs.getString("emp_id")+"','"+rs.getString("user_id")+"','"+rs.getString("emp_fname")+" "+rs.getString("emp_lname")+"')\" title=\"Edit Exist\">Edit</a></div>";*/
				
				
				
				username+="<a href=\"javascript:void(0)\" class=\"edit\" " +
				"onclick=\"changeUserName('"+rs.getString("emp_id")+"','"+rs.getString("user_id")+"','"+rs.getString("emp_fname")+" "+rs.getString("emp_lname")+"')\" title=\"Edit Exist\"></a>";
				alInner.add(username);
				
				if(isShowPassword){
					alInner.add(rs.getString("password")); 
				}else{
					alInner.add(uF.getPasswordString(rs.getString("password")));
				}
				
				if(rs.getString("added_timestamp")!=null){
					alInner.add(uF.getDateFormat(rs.getString("added_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat()));
				}else{
					alInner.add("-");
				}
				
				  
				alInner.add(rs.getString("user_type"));
//				alInner.add(uF.stringMapping(hmEmpType.get(rs.getString("emp_id"))));
				alInner.add(rs.getString("emp_status")); 
				
				
				alInner.add("<div id=\"myDiv_"+nCount+"\"><a href=\"javascript:void(0)\" onclick=\"(confirm('Are You sure you want to reset the password?')?getContent('myDiv_"+nCount+"','ResetPassword.action?user_id="+rs.getString("user_id")+"'):'')\" >Reset Password</a></div>");
				if(rs.getString("reset_timestamp")!=null){
					alInner.add(uF.getDateFormat(rs.getString("reset_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat()) +" at " +uF.getDateFormat(rs.getString("reset_timestamp"), DBTIMESTAMP, CF.getStrReportTimeFormat()));
				}else{
					alInner.add("-");
				}
				   
				
//				alInner.add("<a href=\"ManageACL.action?U="+rs.getInt("user_id")+"\"> <img src="+request.getContextPath()+"/images1/access_control.png></a>");
				al.add(alInner);
				nCount++;
			}
			rs.close();
			pst.close();
			          
			request.setAttribute("reportList", al);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
		
	}
	 
	
	
	public String deleteUser(String strUserId){
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(deleteUser);
			pst.setInt(1, uF.parseToInt(strUserId));
			pst.execute();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally{
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
		
	}


	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

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

	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}

	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}

	public List<FillServices> getServiceList() {
		return serviceList;
	}

	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
	}

	public String getF_strWLocation() {
		return f_strWLocation;
	}

	public void setF_strWLocation(String f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
	}

	public String getF_department() {
		return f_department;
	}

	public void setF_department(String f_department) {
		this.f_department = f_department;
	}

	public String getF_level() {
		return f_level;
	}

	public void setF_level(String f_level) {
		this.f_level = f_level;
	}

	public String getF_service() {
		return f_service;
	}

	public void setF_service(String f_service) {
		this.f_service = f_service;
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
	
	
}
