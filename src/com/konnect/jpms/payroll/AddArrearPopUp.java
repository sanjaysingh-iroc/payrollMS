package com.konnect.jpms.payroll;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.select.FillYears;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddArrearPopUp extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	
	String strUserType = null;
	String strSessionEmpId = null;
	
	String strArearCode;
	String strArearName;
	String strArearDesc;
	String strArearAmount;
	String strArearDuration;
	String strArearEffectiveDate;
	String strArrearId;
	String arear_id;
	String strBasic;
	
	String f_org;
	String f_WLocation;
	String f_department;
	String f_service;
	String f_level;
	
	List<FillOrganisation> organisationList;
	List<FillDepartment> departmentList;
	List<FillServices> serviceList;
	List<FillWLocation> wLocationList;
	List<FillLevel> levelList;
	
	private String arrearType;
	private String defaultArrearType;
	private String strArrearDays;
	private String arrearPaycycle;
	List<FillPayCycles> paycycleList;
	
	public String execute() throws Exception {
		
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		UtilityFunctions uF = new UtilityFunctions();
		
		if(uF.parseToInt(getArear_id())>0){
			getArearEmployeeList(uF);
		} else {
			setDefaultArrearType("0");
		}
		if(uF.parseToInt(getF_org()) == 0){
			setF_org((String)session.getAttribute(ORGID));
		}
		
		paycycleList = new FillPayCycles(request).fillPayCycles(CF, getF_org());
		
		return load(uF);
	}

	private String load(UtilityFunctions uF) {
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		}else{
			organisationList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillServices(getF_org(),uF);
		
		return SUCCESS;
	}

	public void getArearEmployeeList(UtilityFunctions uF) {
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from arear_details where arear_id =?");
			pst.setInt(1, uF.parseToInt(getArear_id()));
			rs = pst.executeQuery();
//			System.out.println("pst====>"+pst);
			while(rs.next()){
				
				setStrArearCode(rs.getString("arear_code"));
				setStrArearName(rs.getString("arear_name"));
				setStrArearDesc(rs.getString("arear_description"));
				setStrArearAmount(rs.getString("arear_amount"));
				setStrArearDuration(rs.getString("duration_months"));
				setStrArearEffectiveDate(uF.getDateFormat(rs.getString("effective_date"), DBDATE, DATE_FORMAT));
				setStrArrearId(rs.getString("arear_id"));
				setStrBasic(rs.getString("basic_amount"));
				
				setDefaultArrearType(rs.getString("arrear_type"));
				setStrArrearDays(rs.getString("arrear_days"));
				if(uF.parseToInt(rs.getString("arrear_type")) == 1){
					String dt1 = uF.getDateFormat(rs.getString("paycycle_from"), DBDATE, DATE_FORMAT);
					String dt2 = uF.getDateFormat(rs.getString("paycycle_to"), DBDATE, DATE_FORMAT);
					int nPayCycle = uF.parseToInt(rs.getString("paycycle"));
					setArrearPaycycle(dt1+"-"+dt2+"-"+nPayCycle);
				}
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
	}
	public String getF_WLocation() {
		return f_WLocation;
	}
	public void setF_WLocation(String f_WLocation) {
		this.f_WLocation = f_WLocation;
	}
	public String getF_department() {
		return f_department;
	}
	public void setF_department(String f_department) {
		this.f_department = f_department;
	}
	public String getF_service() {
		return f_service;
	}
	public void setF_service(String f_service) {
		this.f_service = f_service;
	}
	public String getF_level() {
		return f_level;
	}
	public void setF_level(String f_level) {
		this.f_level = f_level;
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
	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}
	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}
	public List<FillLevel> getLevelList() {
		return levelList;
	}
	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}
	public String getStrArearCode() {
		return strArearCode;
	}
	public void setStrArearCode(String strArearCode) {
		this.strArearCode = strArearCode;
	}
	public String getStrArearName() {
		return strArearName;
	}
	public void setStrArearName(String strArearName) {
		this.strArearName = strArearName;
	}
	public String getStrArearDesc() {
		return strArearDesc;
	}
	public void setStrArearDesc(String strArearDesc) {
		this.strArearDesc = strArearDesc;
	}
	public String getStrArearAmount() {
		return strArearAmount;
	}
	public void setStrArearAmount(String strArearAmount) {
		this.strArearAmount = strArearAmount;
	}
	public String getStrArearDuration() {
		return strArearDuration;
	}
	public void setStrArearDuration(String strArearDuration) {
		this.strArearDuration = strArearDuration;
	}
	public String getStrArearEffectiveDate() {
		return strArearEffectiveDate;
	}
	public void setStrArearEffectiveDate(String strArearEffectiveDate) {
		this.strArearEffectiveDate = strArearEffectiveDate;
	}
	public String getArear_id() {
		return arear_id;
	}

	public void setArear_id(String arear_id) {
		this.arear_id = arear_id;
	}

	public String getStrSessionEmpId() {
		return strSessionEmpId;
	}

	public void setStrSessionEmpId(String strSessionEmpId) {
		this.strSessionEmpId = strSessionEmpId;
	}

	public String getStrArrearId() {
		return strArrearId;
	}

	public void setStrArrearId(String strArrearId) {
		this.strArrearId = strArrearId;
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

	public String getStrBasic() {
		return strBasic;
	}

	public void setStrBasic(String strBasic) {
		this.strBasic = strBasic;
	}

	public String getArrearType() {
		return arrearType;
	}

	public void setArrearType(String arrearType) {
		this.arrearType = arrearType;
	}

	public String getDefaultArrearType() {
		return defaultArrearType;
	}

	public void setDefaultArrearType(String defaultArrearType) {
		this.defaultArrearType = defaultArrearType;
	}

	public String getStrArrearDays() {
		return strArrearDays;
	}

	public void setStrArrearDays(String strArrearDays) {
		this.strArrearDays = strArrearDays;
	}

	public String getArrearPaycycle() {
		return arrearPaycycle;
	}

	public void setArrearPaycycle(String arrearPaycycle) {
		this.arrearPaycycle = arrearPaycycle;
	}

	public List<FillPayCycles> getPaycycleList() {
		return paycycleList;
	}

	public void setPaycycleList(List<FillPayCycles> paycycleList) {
		this.paycycleList = paycycleList;
	}
	
}