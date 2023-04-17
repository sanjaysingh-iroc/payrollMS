package com.konnect.jpms.ajax;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetSectionDescription extends ActionSupport implements ServletRequestAware, IStatements {
	

	public String execute() throws Exception {

		HttpSession session = request.getSession();
		CommonFunctions CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		String strSectionName = "";
		String strSectionDesc = "";
		String strSectionValue = "";
		try {
			
			String strSectionId = (String)request.getParameter("SID");
			String financialYear = (String)request.getParameter("financialYear");
			
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;

			if (financialYear != null) {
				String[] strFinancialYearDates = financialYear.split("-");
				strFinancialYearStart = strFinancialYearDates[0];
				strFinancialYearEnd = strFinancialYearDates[1];
			}

			con = db.makeConnection(con);
//			pst = con.prepareStatement(selectSectionV);
			pst = con.prepareStatement("SELECT * FROM section_details where section_id=? and financial_year_start=? and financial_year_end=?");
			pst.setInt(1, uF.parseToInt(strSectionId));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println("pst===>"+pst);
			rs = pst.executeQuery();
			while(rs.next()){
				strSectionName = rs.getString("section_code");
				strSectionDesc = rs.getString("section_description");
				strSectionValue = uF.formatIntoOneDecimal(rs.getDouble("section_exemption_limit"))+((rs.getString("section_limit_type")!=null && rs.getString("section_limit_type").equalsIgnoreCase("A")?"":"%"));
			}
            rs.close();
            pst.close();
			
			
			request.setAttribute("S_DESC", strSectionDesc);
			request.setAttribute("S_NAME", strSectionName);
			request.setAttribute("S_VALUE", strSectionValue);
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

}
