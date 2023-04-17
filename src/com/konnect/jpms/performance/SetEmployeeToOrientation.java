package com.konnect.jpms.performance;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class SetEmployeeToOrientation extends ActionSupport implements
		ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;

	String strUserType = null;
	String strSessionEmpId = null; 
	private String employee;
	private String empList;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions); 
		if (CF == null)
			return LOGIN;
		UtilityFunctions uF = new UtilityFunctions();
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
//		System.out.println("employee "+ getEmployee());
		//orientationList = new FillOrientation().fillOrientation();
		getSelectedEmpList(uF);
		return SUCCESS;

	}
	
	public void getSelectedEmpList(UtilityFunctions uF) {
		List<String> al = new ArrayList<String>();
		if (getEmployee() != null && getEmployee().length() > 0) {
			List<String> emp=Arrays.asList(getEmployee().split(","));
			for(int i=0;emp!=null && !emp.isEmpty() && i<emp.size();i++){
				al.add(emp.get(i).trim());
			}
		}
//		System.out.println("al ===> "+al);
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		try{
		con = db.makeConnection(con);
		Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
		empList = uF.showData(getAppendData(al, hmEmpName), "");
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeConnection(con);
		}
		
		
		request.setAttribute("allData", empList);
	}
	
	
	private String getAppendData(List<String> strID, Map<String, String> mp) {
		StringBuilder sb = new StringBuilder();
		StringBuilder sbEmpId = new StringBuilder();
		StringBuilder sbEmpIdAndName = new StringBuilder();
		UtilityFunctions uF = new UtilityFunctions();
		if (strID != null) {
			for (int i = 0; i < strID.size(); i++) {
				if (i == 0) {
					sbEmpId.append(","+strID.get(i)+",");
					sb.append(mp.get(strID.get(i)));
				} else {
					sbEmpId.append(strID.get(i)+",");
					sb.append(", " + mp.get(strID.get(i)));
				}
			}
		} else {
			return null;
		}
		sbEmpIdAndName.append(uF.showData(sb.toString(),"Not Choosen"));
		sbEmpIdAndName.append("::::");
		sbEmpIdAndName.append(sbEmpId.toString());
		return sbEmpIdAndName.toString();
	}
	
	
	
	public String getEmployee() {
		return employee;
	}

	public void setEmployee(String employee) {
		this.employee = employee;
	}

	public String getEmpList() {
		return empList;
	}

	public void setEmpList(String empList) {
		this.empList = empList;
	}



	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}



}
