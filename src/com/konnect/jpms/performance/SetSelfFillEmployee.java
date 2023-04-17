package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

public class SetSelfFillEmployee extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;

	String strUserType;
	String strSessionEmpId;
	private String empids;
	private String removeEmpid;
	private String count="";
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		
		getSelfFillEmployee();
		return SUCCESS;

	}
	
	public void getSelfFillEmployee() {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(null, null);
			
			List<String> alEmpIds = new ArrayList<String>();
			if(getEmpids() != null && !getEmpids().equals("")) {
				alEmpIds = Arrays.asList(getEmpids().split(","));
			}
//			System.out.println("alEmpIds ===>> "+ alEmpIds);
			StringBuilder sbEmpName = null;
			StringBuilder sbEmpIds = null;
			for(int i=0; alEmpIds != null && !alEmpIds.isEmpty() && i<alEmpIds.size(); i++) {
				if(uF.parseToInt(alEmpIds.get(i))>0 && uF.parseToInt(alEmpIds.get(i)) != uF.parseToInt(getRemoveEmpid())) {
					if(sbEmpIds == null) {
						sbEmpIds = new StringBuilder();
						sbEmpIds.append(","+alEmpIds.get(i)+",");
					} else {
						sbEmpIds.append(alEmpIds.get(i)+",");
					}
					if(sbEmpName == null) {
						sbEmpName = new StringBuilder();
						sbEmpName.append(hmEmpName.get(alEmpIds.get(i))+" <a onclick=\"removeRevieweeForSelfReview('"+alEmpIds.get(i)+"');\" href=\"javascript: void(0);\">X</a>");
					} else {
						sbEmpName.append(", "+hmEmpName.get(alEmpIds.get(i))+" <a onclick=\"removeRevieweeForSelfReview('"+alEmpIds.get(i)+"');\" href=\"javascript: void(0);\">X</a>");
					}
				}
			}
			if(sbEmpIds == null) {
				sbEmpIds = new StringBuilder();
			}
			if(sbEmpName == null) {
				sbEmpName = new StringBuilder();
			}
//			System.out.println("STATUS_MSG ====>>> " + sbEmpIds+"::::"+sbEmpName);
			request.setAttribute("STATUS_MSG", sbEmpIds+"::::"+sbEmpName);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeConnection(con);
		}
	}
	

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getEmpids() {
		return empids;
	}

	public void setEmpids(String empids) {
		this.empids = empids;
	}

	public String getRemoveEmpid() {
		return removeEmpid;
	}

	public void setRemoveEmpid(String removeEmpid) {
		this.removeEmpid = removeEmpid;
	}

}
