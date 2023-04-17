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

/**
 * @author workrig
 *
 */
public class GetLiveEmployeeList extends ActionSupport implements IStatements, ServletRequestAware{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3105338539839910045L;
	private static Logger log = Logger.getLogger(GetLiveEmployeeList.class);
	HttpSession session;
	CommonFunctions CF;
	
	private String strOrg;
	private String f_strWLocation;
	private String f_department;
	private String f_level;
	private String f_service;
	private String multiple;
	private String validate;
	
	List<FillEmployee> empList;
	
	String fromPage;
	
	public String execute() throws Exception {
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		UtilityFunctions uF = new UtilityFunctions();
		
		String strD2 = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT);
		
		Database db = new Database();
		db.setRequest(request);
		Connection con=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			StringBuilder sbQurey = new StringBuilder();
			sbQurey.append("select * from employee_official_details eod, employee_personal_details epd where epd.emp_per_id = eod.emp_id and is_alive=true "
							+" and epd.emp_status!='TERMINATED' and epd.emp_per_id not in (select emp_id from emp_off_board eob where approved_1=1 and approved_2=1)");
			if(uF.parseToInt(getStrOrg())>0){
				sbQurey.append("and org_id = "+uF.parseToInt(getStrOrg()));
			}
			if(uF.parseToInt(getF_department())>0){
				sbQurey.append("and depart_id = "+uF.parseToInt(getF_department()));
			}
			if(uF.parseToInt(getF_level())>0){
				sbQurey.append(" and grade_id in (select grade_id from level_details l, designation_details di, grades_details gd where l.level_id = di.level_id and di.designation_id = gd.designation_id and l.level_id = "+uF.parseToInt(getF_level()) +") ");
			}
			if(uF.parseToInt(getF_strWLocation())>0){
				sbQurey.append(" and wlocation_id = "+uF.parseToInt(getF_strWLocation()));
			}
			if(uF.parseToInt(getF_service())>0){
				sbQurey.append(" and service_id like '%,"+uF.parseToInt(getF_service())+",%'");
			}
			
			sbQurey.append(" order by emp_fname, emp_lname");
			
			pst = con.prepareStatement(sbQurey.toString());
		//	System.out.println("pst ==== >>>>> " +pst);
			rs = pst.executeQuery();
			empList = new ArrayList<FillEmployee>();
			while(rs.next()){
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				empList.add(new FillEmployee(rs.getString("emp_id"), rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname"),""));
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
		
		return SUCCESS;
	
	}
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public List<FillEmployee> getEmpList() {
		return empList;
	}

	public void setEmpList(List<FillEmployee> empList) {
		this.empList = empList;
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

	public String getMultiple() {
		return multiple;
	}

	public void setMultiple(String multiple) {
		this.multiple = multiple;
	}

	public String getF_service() {
		return f_service;
	}

	public void setF_service(String f_service) {
		this.f_service = f_service;
	}

	public String getValidate() {
		return validate;
	}

	public void setValidate(String validate) {
		this.validate = validate;
	}

	public String getStrOrg() {
		return strOrg;
	}

	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

}
