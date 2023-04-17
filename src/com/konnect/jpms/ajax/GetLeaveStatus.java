package com.konnect.jpms.ajax;

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
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetLeaveStatus extends ActionSupport implements IConstants, ServletRequestAware {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6192138956998988151L;
	HttpSession session;
	CommonFunctions CF;
	String strUserType;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		String strEmpId = (String)request.getParameter("EMPID");
		String strLeaveTypeId = (String)request.getParameter("LTID");
		String strD1 = (String)request.getParameter("D1");
		String strD2 = (String)request.getParameter("D2");
		
		strUserType = (String)session.getAttribute(USERTYPE);
		
		if(strUserType!=null && strUserType.equalsIgnoreCase(EMPLOYEE)){
			strEmpId = (String)session.getAttribute(EMPID);
		}
		 
		getLeaveStatus(strEmpId, strLeaveTypeId, strD1, strD2);
		
		return SUCCESS;
	}

	public void getLeaveStatus(String strEmpId, String strLeaveTypeId, String strD1, String strD2){
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			
			String strWlocationid = CF.getEmpWlocationId(con, uF, strEmpId);
			String strOrgid = CF.getEmpOrgId(con, uF, strEmpId);
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from emp_leave_type elt,leave_type lt where lt.leave_type_id = elt.leave_type_id " +
					" and level_id in (select dd.level_id from level_details ld, designation_details dd, grades_details gd " +
					" where ld.level_id = dd.level_id and gd.designation_id = dd.designation_id " +
					"and gd.grade_id in (select grade_id from employee_official_details where emp_id = ?)) and wlocation_id=? and elt.org_id=?" +
					" and lt.is_compensatory=false and lt.is_work_from_home=false and is_constant_balance=false and lt.leave_type_id =? ");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setInt(2, uF.parseToInt(strWlocationid));
			pst.setInt(3, uF.parseToInt(strOrgid));
			pst.setInt(4, uF.parseToInt(strLeaveTypeId));
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			boolean flag = false; 
			while(rs.next()){
				flag = true; 
			}
			rs.close();
			pst.close();																							 
			
			if(flag) {
				EmployeeLeaveEntryReport leaveEntryReport = new EmployeeLeaveEntryReport();
				leaveEntryReport.request = request;
				leaveEntryReport.session = session;
				leaveEntryReport.CF = CF;
				leaveEntryReport.setStrEmpId(strEmpId);
				leaveEntryReport.setDataType("L");
				leaveEntryReport.viewEmployeeLeaveEntry1();
				 
				List<List<String>> leaveList = (List<List<String>>)request.getAttribute("leaveList");
				if(leaveList == null) leaveList = new ArrayList<List<String>>();
				
				int nLeaveListSize = leaveList.size();
				for (int j=0; j<nLeaveListSize; j++) {
					List<String> cinnerlist = (List<String>)leaveList.get(j);
					
					if(uF.parseToInt(cinnerlist.get(6)) == uF.parseToInt(strLeaveTypeId)){
					//===start parvez date: 01-11-2022===	
//						double dblRemaining = uF.parseToDouble(cinnerlist.get(5));
						double dblRemaining = uF.parseToDouble(cinnerlist.get(9));
					//===end parvez date: 01-11-2022===	
//						System.out.println("GLS/102---dblRemaining=="+dblRemaining+"---cinnerlist.get(9)=="+cinnerlist.get(9));
						request.setAttribute("dblAccruedLeaves", ""+uF.formatIntoTwoDecimalWithOutComma(dblRemaining));
						
						double dblApproved = uF.parseToDouble(cinnerlist.get(7));
						request.setAttribute("dblApprovedLeaves", ""+uF.formatIntoTwoDecimalWithOutComma(dblApproved));
						
						double dblPending = uF.parseToDouble(cinnerlist.get(8));
						request.setAttribute("dblPendingLeaves", ""+uF.formatIntoTwoDecimalWithOutComma(dblPending));
						
						request.setAttribute("leaveStatus", "true");
					}					
				}
				
//				String strDiff = uF.dateDifference(strD1,DATE_FORMAT, strD2, DATE_FORMAT);
//				Map<String, String> hmEmpNames = CF.getEmpNameMap(con,null, null);
//				if(uF.parseToDouble(strDiff) > dblRemaining){
//					if(strUserType!=null && strUserType.equalsIgnoreCase(EMPLOYEE)){
//						request.setAttribute("status", "you have accrued <strong>"+dblRemaining +"</strong> leaves and has <strong>"+dblApproved+"</strong> approved while <strong>" +dblPending +"</strong> leaves are yet to be approved. <br/> <strong>Are you sure you still wish to proceed with this leave?</strong>");
//					}else{
//						request.setAttribute("status", (String)hmEmpNames.get(strEmpId)+" has accrued <strong>"+dblRemaining +"</strong> leaves and you have <strong>"+dblApproved+"</strong> approved while <strong>" +dblPending +"</strong> leaves are yet to be approved. <br/> <strong>Are you sure you still wish to proceed with this leave?</strong>");
//					}
//				}
//				request.setAttribute("leaveStatus", "true");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
}