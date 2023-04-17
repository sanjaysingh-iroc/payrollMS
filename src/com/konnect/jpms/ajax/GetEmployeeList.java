package com.konnect.jpms.ajax;

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

import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetEmployeeList extends ActionSupport implements IStatements, ServletRequestAware{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3105338539839910045L;
	private static Logger log = Logger.getLogger(GetEmployeeList.class);
	HttpSession session;
	CommonFunctions CF;
	private String level;
	private String location;
	private String payCycle;
	private String multiple;
	private String project;
	private String T;
	private String PAY;
	private String strMul;
	
	private String strPro;
	private String financialYear;
	private String type;

	private String hrValidReq;
	private String hodValidReq;
	private String supervisorValidReq;
	private String strEmpId;
	
	private List<FillEmployee> empNamesList;
	private String f_org;
	private String strUserType;
	private String strDepart;
	private String strDesig;
	private String strSBU;
	private String strGrade;
	private String strEmployeementType;
	
	private String fromPage;
	
	public String execute() throws Exception {
		
//		System.out.println("GetEmployeeList: execute()");
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strUserType = (String) session.getAttribute(USERTYPE);
		
		UtilityFunctions uF = new UtilityFunctions();
		String strD1 = "", strD2 = "";
		
		String[] strPayCycleDates = null;

		if (getPayCycle() != null) {
			strPayCycleDates = getPayCycle().split("-");
			strD1 = strPayCycleDates[0];
			strD2 = strPayCycleDates[1];
		
		} else {
//			strPayCycleDates = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF,request);
			strPayCycleDates = CF.getCurrentPayCycleByOrg(CF.getStrTimeZone(), CF, getF_org(), request);
			strD1 = strPayCycleDates[0];
			strD2 = strPayCycleDates[1];
		}
		
//		System.out.println("getType() ===>> " + getType());
//		System.out.println("getLevel() ===>> " + getLevel());
//		System.out.println("getF_org() ===>> " + getF_org());
		System.out.println("getFromPage() ===>> " + getFromPage());
		
		if(getFromPage()!=null && getFromPage().equals("CKE")) {
			empNamesList = new FillEmployee(request).fillEmployeeNameForAttendance(strD1,strD2, uF.parseToInt(getLevel()),uF.parseToInt(getF_org()),uF.parseToInt(getLocation()));
		} else if(getFromPage()!=null && getFromPage().equals("EMP_VARI_FORM") && getFromPage().equals("ACTIVITY_HISTORY_REPORT")) {
			empNamesList = new FillEmployee(request).fillEmployeeNameOrgLocationDepartSBUDesigGrade(CF, getF_org(), getLocation(), getStrDepart(), getStrSBU(), getLevel(), getStrDesig(), getStrGrade(), getStrEmployeementType(), false);
		} else if(getType()!=null && getType().equalsIgnoreCase("SUPERVISOR")) {
			empNamesList = new FillEmployee(request).fillSupervisorNameCode(uF.parseToInt(getStrEmpId()), getF_org(), getStrDepart());	
		} else if(getType()!=null && getType().equalsIgnoreCase("HOD")) {
			empNamesList = new FillEmployee(request).fillHODNameCode(getStrEmpId(), getF_org(), uF.parseToInt(getLocation()), CF);
		} else if(getType()!=null && getType().equalsIgnoreCase("HR")) {
			empNamesList = new FillEmployee(request).fillEmployeeNameHR(getStrEmpId(),uF.parseToInt(getF_org()), uF.parseToInt(getLocation()), CF, uF);	
		} else if(uF.parseToInt(getLocation())>0 && uF.parseToInt(getLevel()) == 0) {
			empNamesList = new FillEmployee(request).fillEmployeeNameByLocation(getLocation(), false);	
		} else {
			//empNamesList = new FillEmployee().fillEmployeeName(strD1, uF.parseToInt(getLevel()));
			empNamesList = new FillEmployee(request).fillEmployeeName(strD1, uF.parseToInt(getLevel()), uF.parseToInt(getF_org()), uF.parseToInt(getLocation()));
//			empNamesList = getEmployeeList(strD1, uF.parseToInt(getLevel()),uF.parseToInt(getF_org()));
		}
		
		if( getStrPro()!=null && getStrPro().equalsIgnoreCase("P")) 
			setProject("P");
		else
			setProject(null);
		
		if( getStrMul()!=null && getStrMul().equals("LblAll") ){
			setMultiple("LblAll");
		} else {
			if( getStrMul()!=null && getStrMul().length()!=0 ) 
				setMultiple(null);
			else
				setMultiple("true");
		}
		
		String strAction = request.getServletPath();
		if(strAction!=null){
			strAction = strAction.replace("/","");
		}
		
		
		if(strAction!=null && strAction.equalsIgnoreCase("ClockEntries.action")){
			setMultiple(null);
		}
		
		
		return SUCCESS;
	
	}
	
	public List<FillEmployee> getEmployeeList(String strD2, int level_id,int org_id) {
		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpCode = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id " +
					" and epd.is_alive= true and epd.joining_date <= ? ");
			if(strUserType!=null && (strUserType.equalsIgnoreCase(MANAGER))){
				sbQuery.append(" and emp_per_id in (select emp_id from employee_official_details where supervisor_emp_id="+uF.parseToInt((String) session.getAttribute(EMPID))+") ");
			}
			
			if(org_id>0){
				sbQuery.append(" and eod.org_id="+org_id);
			}
			
			if(level_id>0){
				sbQuery.append(" and eod.grade_id in (SELECT grade_id FROM grades_details where designation_id in " +
						" (SELECT designation_id FROM designation_details  WHERE level_id =" + level_id+ ")) ");
			}
			
			sbQuery.append(" order by epd.emp_fname");
			
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strD2, DATE_FORMAT));
			
//			System.out.println("pf====>"+pst);
			rsEmpCode = pst.executeQuery();
			while (rsEmpCode.next()) {
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rsEmpCode.getString("emp_mname") != null && rsEmpCode.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rsEmpCode.getString("emp_mname");
					}
				}
				
				al.add(new FillEmployee(rsEmpCode.getString("emp_id"), rsEmpCode.getString("emp_fname") +strEmpMName+ " " + rsEmpCode.getString("emp_lname"), ""));
			}
			rsEmpCode.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rsEmpCode);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getPayCycle() {
		return payCycle;
	}

	public void setPayCycle(String payCycle) {
		this.payCycle = payCycle;
	}

	public List<FillEmployee> getEmpNamesList() {
		return empNamesList;
	}

	public void setEmpNamesList(List<FillEmployee> empNamesList) {
		this.empNamesList = empNamesList;
	}

	public String getMultiple() {
		return multiple;
	}

	public void setMultiple(String multiple) {
		this.multiple = multiple;
	}

	public String getT() {
		return T;
	}

	public void setT(String t) {
		T = t;
	}

	public String getPAY() {
		return PAY;
	}

	public void setPAY(String pAY) {
		PAY = pAY;
	}

	public String getStrMul() {
		return strMul;
	}

	public void setStrMul(String strMul) {
		this.strMul = strMul;
	}

	public String getFinancialYear() {
		return financialYear;
	}

	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getStrPro() {
		return strPro;
	}

	public void setStrPro(String strPro) {
		this.strPro = strPro;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getHrValidReq() {
		return hrValidReq;
	}

	public void setHrValidReq(String hrValidReq) {
		this.hrValidReq = hrValidReq;
	}

	public String getHodValidReq() {
		return hodValidReq;
	}

	public void setHodValidReq(String hodValidReq) {
		this.hodValidReq = hodValidReq;
	}

	public String getStrEmpId() {
		return strEmpId;
	}

	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}

	public String getSupervisorValidReq() {
		return supervisorValidReq;
	}

	public void setSupervisorValidReq(String supervisorValidReq) {
		this.supervisorValidReq = supervisorValidReq;
	}

	public String getStrDepart() {
		return strDepart;
	}

	public void setStrDepart(String strDepart) {
		this.strDepart = strDepart;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	public String getStrUserType() {
		return strUserType;
	}

	public void setStrUserType(String strUserType) {
		this.strUserType = strUserType;
	}

	public String getStrGrade() {
		return strGrade;
	}

	public void setStrGrade(String strGrade) {
		this.strGrade = strGrade;
	}

	public String getStrDesig() {
		return strDesig;
	}

	public void setStrDesig(String strDesig) {
		this.strDesig = strDesig;
	}

	public String getStrSBU() {
		return strSBU;
	}

	public void setStrSBU(String strSBU) {
		this.strSBU = strSBU;
	}

	public String getStrEmployeementType() {
		return strEmployeementType;
	}

	public void setStrEmployeementType(String strEmployeementType) {
		this.strEmployeementType = strEmployeementType;
	}
	
}
