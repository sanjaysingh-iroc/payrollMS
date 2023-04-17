package com.konnect.jpms.leave;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.reports.EmployeeLeaveEntryReport;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ViewEmpLeaveBalance extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;

	CommonFunctions CF = null;

	private String strEmpId;

	public String execute() {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) {
			return LOGIN;
		}
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);

		UtilityFunctions uF = new UtilityFunctions();
		if(uF.parseToInt(getStrEmpId()) > 0){
			viewEmpLeaveBalance(uF);
		}
		
		return SUCCESS;
	}

	private void viewEmpLeaveBalance(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			
			StringBuilder sb = new StringBuilder();
			
			int nCurrentYear = uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "yyyy"));
			
			int nEmpId = uF.parseToInt(getStrEmpId());
				
			String strWlocationid = CF.getEmpWlocationId(con, uF, getStrEmpId());
			String strOrgid=CF.getEmpOrgId(con, uF, getStrEmpId());
			
			String strEmpName = CF.getEmpNameMapByEmpId(con, getStrEmpId());
			
			EmployeeLeaveEntryReport leaveEntryReport = new EmployeeLeaveEntryReport();
			leaveEntryReport.request = request;
			leaveEntryReport.session = session;
			leaveEntryReport.CF = CF;
			leaveEntryReport.setStrEmpId(strEmpId);
			leaveEntryReport.setDataType("L");
			leaveEntryReport.viewEmployeeLeaveEntry1();
			
			List<List<String>> leaveList = (List<List<String>>)request.getAttribute("leaveList");;
			if(leaveList == null) leaveList = new ArrayList<List<String>>();
			
			int nLeaveListSize = leaveList.size();
			
			sb.append("<div id=\"popup_name" + strEmpId + "\">" + 
//					   "<h4 class=\"textcolorWhite\">Consolidate leave information of "+uF.showData(strEmpName, "")+" since "+"01/01/"+nCurrentYear+"</h4>" + 
					   "<table>"+
					   "<tr>"+
					   "<td class=\"reportHeading\">Leave Type</td>" + 
					   "<td class=\"reportHeading\">Total No Of Leaves<br>(in days)</td>" +
					   "<td class=\"reportHeading\">Pending Leaves<br>(in days)</td>" +
					   "<td class=\"reportHeading\">Approved Leaves<br>(in days)</td>" +
					   "<td class=\"reportHeading\">Denied Leaves<br>(in days)</td>" +
					   "<td class=\"reportHeading\">Remaining Leaves<br>(in days)</td>" +
					   "</tr>");
			
			for (int j=0; j<nLeaveListSize; j++) {
				List<String> cinnerlist = (List<String>)leaveList.get(j);
				
				
				sb.append("<tr>" + 
						"<td class=\"reportHeading\">"+uF.showData(cinnerlist.get(0), "")+"</td>" +
						"<td class=\"reportLabel alignCenter\">" + uF.showData(cinnerlist.get(1), "")+ "</td>" +
						"<td class=\"reportLabel alignCenter\">" + uF.showData(cinnerlist.get(2), "")+ "</td>" +
						"<td class=\"reportLabel alignCenter\">" + uF.showData(cinnerlist.get(3), "")+ "</td>" +
						"<td class=\"reportLabel alignCenter\">" + uF.showData(cinnerlist.get(4), "")+ "</td>" +
						"<td class=\"reportLabel alignCenter\">" + uF.showData(cinnerlist.get(5), "")+ "</td>" +
						"</tr>");
			}
			sb.append("</table>" + "</div>");
			
//			System.out.println("viewEmpLeaveBalaace ===>> " + sb.toString());
			request.setAttribute("viewEmpLeaveBalaace", sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	public String getStrEmpId() {
		return strEmpId;
	}
	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}
}