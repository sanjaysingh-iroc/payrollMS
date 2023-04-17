package com.konnect.jpms.requsitions;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillPerkType;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetPerkType extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null; 
	private CommonFunctions CF; 
	 
	String financialYear;
	List<FillPerkType> typeList;

	public String execute() {
		session = request.getSession();
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null) return LOGIN;
		UtilityFunctions uF = new UtilityFunctions();
		Connection con= null;
		Database db = new Database();
		db.setRequest(request);
		try {
			String[] strFinancialYear = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;

			if (getFinancialYear() != null) {				
				strFinancialYear = getFinancialYear().split("-");
				strFinancialYearStart = strFinancialYear[0];
				strFinancialYearEnd = strFinancialYear[1];
			}
			con = db.makeConnection(con);
			Map<String, String> hmEmpLevel = CF.getEmpLevelMap(con);
			typeList = new FillPerkType(request).fillPerkType(uF.parseToInt(hmEmpLevel.get((String)session.getAttribute(EMPID))), strFinancialYearStart, strFinancialYearEnd);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getFinancialYear() {
		return financialYear;
	}

	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}

	public List<FillPerkType> getTypeList() {
		return typeList;
	}

	public void setTypeList(List<FillPerkType> typeList) {
		this.typeList = typeList;
	}
	

}
