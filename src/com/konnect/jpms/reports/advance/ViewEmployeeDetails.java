package com.konnect.jpms.reports.advance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ViewEmployeeDetails extends ActionSupport implements ServletRequestAware, IStatements {

	CommonFunctions CF = null;
	HttpSession session;
	String empId;
	
	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}
	private HttpServletRequest request;
	public String execute() throws Exception {

		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		
	
		getEmpDetails();
		 
		return SUCCESS;
	}

public void getEmpDetails(){
	
	Connection con = null;
	PreparedStatement pst=null;
	ResultSet rs = null;
	Database db = new Database();
	db.setRequest(request);
	
	UtilityFunctions uF=new UtilityFunctions();
	
	try {
		con = db.makeConnection(con);
		Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
		boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
		
		pst = con.prepareStatement(" select a.*,level_name from (select a.*,dd.designation_name,level_id from (select * from(select a.*,di.dept_name from(SELECT epd.*,eod.depart_id,wlocation_id,grade_id FROM employee_personal_details epd , employee_official_details eod where epd.emp_per_id=eod.emp_id and emp_per_id=? )a LEFT JOIN department_info di  on di.dept_id=a.depart_id )a LEFT JOIN grades_details gd  on a.grade_id=gd.grade_id)a LEFT JOIN designation_details dd on a.designation_id=dd.designation_id )a LEFT JOIN level_details ld on a.level_id=ld.level_id");
		pst.setInt(1,uF.parseToInt(empId));
		rs = pst.executeQuery();
		
		Map<String,String> empDetailsList = new HashMap<String,String>();
		while (rs.next()) {
		
			empDetailsList.put("EMP_ID",rs.getString("emp_per_id"));
			empDetailsList.put("EMPCODE",rs.getString("empcode"));
			empDetailsList.put("EMP_ADDRESS",rs.getString("emp_address1"));
			empDetailsList.put("CITY",rs.getString("emp_city_id"));
			empDetailsList.put("EMAIL",rs.getString("emp_email"));
			empDetailsList.put("CONTACT",rs.getString("emp_contactno"));
			empDetailsList.put("IMAGE",rs.getString("emp_image"));
			
			String strEmpMName = "";
			if(flagMiddleName) {
				if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
					strEmpMName = " "+rs.getString("emp_mname");
				}
			}
			
			empDetailsList.put("EMP_NAME",rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname"));
			empDetailsList.put("DEPT",rs.getString("dept_name")!=null?rs.getString("dept_name"):"-");
			empDetailsList.put("DESIG",rs.getString("designation_name")!=null?rs.getString("designation_name"):"-");
			empDetailsList.put("DOB",uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, "dd MMM"));
			empDetailsList.put("LEVEL",rs.getString("level_name")!=null?rs.getString("level_name"):"-");
			empDetailsList.put("GRADE",rs.getString("grade_name")!=null?rs.getString("grade_name"):"-");
				
			
		}
		rs.close();
		pst.close();
		
		request.setAttribute("empDetailsList",empDetailsList);
		
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		db.closeResultSet(rs);
		db.closeStatements(pst);
		db.closeConnection(con);
	}
}
@Override
public void setServletRequest(HttpServletRequest request) {
	this.request = request;
}
}