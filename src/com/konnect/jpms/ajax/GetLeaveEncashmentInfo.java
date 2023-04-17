package com.konnect.jpms.ajax;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.reports.EmployeeLeaveEntryReport;
import com.konnect.jpms.select.FillLeaveType;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetLeaveEncashmentInfo extends ActionSupport implements IConstants, ServletRequestAware {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	CommonFunctions CF;
	HttpSession session;
	String strUserType;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		UtilityFunctions uF = new UtilityFunctions();
		
		String strEmpId = (String)request.getParameter("EMPID");
		String strLeaveTypeId = (String)request.getParameter("LID");
		
		strUserType = (String)session.getAttribute(USERTYPE);
		
		if(strUserType!=null && strUserType.equalsIgnoreCase(EMPLOYEE)){
			strEmpId = (String)session.getAttribute(EMPID);
		}
		
		
//		System.out.println("strEmpId=>"+strEmpId);
//		System.out.println("strLeaveTypeId=>"+strLeaveTypeId);
		
		
		if(uF.parseToInt(strLeaveTypeId)>0){
			getLeaveEncashmentInfo(strEmpId, strLeaveTypeId, CF, uF);
		}
		
		return SUCCESS;
		
	}

	public void getLeaveEncashmentInfo(String strEmpId, String strLeaveTypeId, CommonFunctions CF, UtilityFunctions uF){
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			
			con = db.makeConnection(con);
			
//			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			String strEmpOrgId = CF.getEmpOrgId(con, uF, strEmpId);
			String strEmpWLocationId = CF.getEmpWlocationId(con, uF, strEmpId);
			String strEmpLevelId = CF.getEmpLevelId(con, strEmpId);
			
//			System.out.println("strEmpId ===>> " + strEmpId);
			pst = con.prepareStatement("select elt.min_leave_encashment,elt.encashment_applicable," +
					"elt.encashment_times,elt.max_leave_encash,is_carryforward,effective_date_type " +
					"from leave_type lt, emp_leave_type elt where lt.leave_type_id = elt.leave_type_id " +
					"and lt.leave_type_id=? and elt.level_id=? and elt.wlocation_id=? and elt.org_id=?");
			pst.setInt(1, uF.parseToInt(strLeaveTypeId));
			pst.setInt(2, uF.parseToInt(strEmpLevelId));
			pst.setInt(3, uF.parseToInt(strEmpWLocationId));
			pst.setInt(4, uF.parseToInt(strEmpOrgId));
			System.out.println("pst=="+pst);
			rs = pst.executeQuery();
			double dblMinLeavesForEncashment = 0;
			int encashApplicable=0;
			int encashNoOfTimes=0;
			boolean isCarryforward = false;
			String effective_date_type = "";
			double dblMaxLeavesForEncashment = 0;
			while(rs.next()){
				dblMinLeavesForEncashment = rs.getDouble("min_leave_encashment");
				encashApplicable = rs.getInt("encashment_applicable");
				encashNoOfTimes = rs.getInt("encashment_times");
				isCarryforward = uF.parseToBoolean(rs.getString("is_carryforward"));
				effective_date_type = rs.getString("effective_date_type");
				dblMaxLeavesForEncashment = rs.getDouble("max_leave_encash");
			}
			rs.close();
			pst.close();
			
			System.out.println("isCarryforward ===>> " + isCarryforward + " -- encashApplicable ===>> " + encashApplicable + " -- encashNoOfTimes ===>> " + encashNoOfTimes);
			
			if(isCarryforward || encashApplicable == 2){	
				String effectiveDate="";
				boolean isEncahsmentApplied = false;
				String strEntryDate = null;
				
				if(effective_date_type.equals("CY")){
					int nCurrentYear = uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "yyyy"));
					effectiveDate = "01/01/"+nCurrentYear;
				} else {
					String strDate = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT);
					String[] arrDate = CF.getFinancialYear(con, strDate, CF, uF);
					int nCurrentYear = uF.parseToInt(uF.getDateFormat(arrDate[0], DATE_FORMAT, "yyyy"));
					effectiveDate = "01/04/"+nCurrentYear;
				}
				
				pst = con.prepareStatement("select * from emp_leave_encashment where emp_id = ? and leave_type_id = ? and entry_date>=?");
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setInt(2, uF.parseToInt(strLeaveTypeId));
//				pst.setDate(3, uF.getPrevDate(CF.getStrTimeZone(), 365));
				pst.setDate(3, uF.getDateFormat(effectiveDate, DATE_FORMAT));
//				System.out.println("pst=="+pst);
				rs = pst.executeQuery();
				while(rs.next()){
					isEncahsmentApplied = true;
					strEntryDate = uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat());
				}
				rs.close();
				pst.close();
//				System.out.println("strEntryDate ===>> " +strEntryDate);
				
				pst = con.prepareStatement("select count(leave_encash_id) as cnt from emp_leave_encashment where emp_id = ? and leave_type_id = ? and entry_date>=?");
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setInt(2, uF.parseToInt(strLeaveTypeId));
				pst.setDate(3, uF.getDateFormat(effectiveDate, DATE_FORMAT));
				System.out.println("pst====>"+pst);
				rs = pst.executeQuery();
				int nApllied=0;
				while(rs.next()){
					nApllied = rs.getInt("cnt");
				}
				rs.close();
				pst.close();
				
//				System.out.println("nApllied====>"+nApllied+" encashNoOfTimes=====>"+encashNoOfTimes);
//				if(!isEncahsmentApplied){
				if(nApllied <encashNoOfTimes){
					
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
					double dblBalance = 0;
					double dblPendingLeaves = 0;
					for (int j=0; j<nLeaveListSize; j++) {
						List<String> cinnerlist = (List<String>)leaveList.get(j);
						
						if(uF.parseToInt(cinnerlist.get(6)) == uF.parseToInt(strLeaveTypeId)){
							dblBalance = uF.parseToDouble(cinnerlist.get(5));							
							dblPendingLeaves = uF.parseToDouble(cinnerlist.get(8));							
						}					
					}
					
					double dblAvailbleForEncashment = 0;
					dblAvailbleForEncashment = dblBalance - dblMinLeavesForEncashment - dblPendingLeaves;
					if(dblAvailbleForEncashment<=0){
						dblAvailbleForEncashment = 0;
					}
//					System.out.println("dblBalance=="+dblBalance);
//					System.out.println("dblMinLeavesForEncashment=="+dblMinLeavesForEncashment);
//					System.out.println("dblPendingLeaves=="+dblPendingLeaves);
//					System.out.println("dblAvailbleForEncashment=="+dblAvailbleForEncashment);
//					System.out.println("avail=="+dblBalance);
					
					StringBuilder sb = new StringBuilder();
					sb.append("Total Available Leaves = "+dblBalance+"<br/>");
					sb.append("Total Pending Leaves = "+dblPendingLeaves+"<br/>");
					sb.append("Available for encashment = "+dblAvailbleForEncashment+"<br/>");
					sb.append("Maximum encashment apply = "+dblMaxLeavesForEncashment+"<br/>");
					sb.append("<input type=\"hidden\" name=\"strAvailableEncashment\" id=\"strAvailableEncashment\" value=\""+dblAvailbleForEncashment+"\">");
					sb.append("<input type=\"hidden\" name=\"strMaxLeavesForEncashment\" id=\"strMaxLeavesForEncashment\" value=\""+dblMaxLeavesForEncashment+"\">");
					request.setAttribute("STATUS_MSG", sb.toString());
//					System.out.println("sb.toString() ===>> " + sb.toString());
					
				}else{
					StringBuilder sb = new StringBuilder();
					
					sb.append("You can apply "+encashNoOfTimes+" for leave encashment in a year. Last applied date was "+strEntryDate);
					sb.append("<input type=\"hidden\" name=\"strAvailableEncashment\" id=\"strAvailableEncashment\" value=\"0\">");
					sb.append("<input type=\"hidden\" name=\"strMaxLeavesForEncashment\" id=\"strMaxLeavesForEncashment\" value=\"0\">");
					request.setAttribute("STATUS_MSG", sb.toString());
					
//					System.out.println("sb.toString() ===>> " + sb.toString());
				}
				
				
			} else {
				String effectiveDate="";
				String effectiveEndDate="";
				
				boolean isEncahsmentApplied = false;
				String strEntryDate = null;
				
				if(effective_date_type.equals("CY")){
					int nCurrentYear = uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "yyyy"));
					effectiveDate = "01/01/"+(nCurrentYear-1);
					effectiveEndDate = "31/12/"+nCurrentYear;
				} else {
					String strDate = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT);
					String[] arrDate = CF.getFinancialYear(con, strDate, CF, uF);
					int nCurrentYear = uF.parseToInt(uF.getDateFormat(arrDate[0], DATE_FORMAT, "yyyy"));
					effectiveDate = "01/04/"+(nCurrentYear-1);
					effectiveEndDate = "31/03/"+nCurrentYear;
				}
				
				pst = con.prepareStatement("select * from emp_leave_encashment where emp_id = ? and leave_type_id = ? and entry_date between ? and ?");
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setInt(2, uF.parseToInt(strLeaveTypeId));
//				pst.setDate(3, uF.getPrevDate(CF.getStrTimeZone(), 365));
				pst.setDate(3, uF.getDateFormat(effectiveDate, DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(effectiveEndDate, DATE_FORMAT));
//				System.out.println("pst=="+pst);
				rs = pst.executeQuery();
				while(rs.next()){
					isEncahsmentApplied = true;
					strEntryDate = uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat());
				}
				rs.close();
				pst.close();
//				System.out.println(" else strEntryDate else ===>> " +strEntryDate);
				
				pst = con.prepareStatement("select count(leave_encash_id) as cnt from emp_leave_encashment where emp_id = ? and leave_type_id = ? and entry_date>=?");
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setInt(2, uF.parseToInt(strLeaveTypeId));
				pst.setDate(3, uF.getDateFormat(effectiveDate, DATE_FORMAT));
				rs = pst.executeQuery();
				int nApllied=0;
				while(rs.next()){
					nApllied = rs.getInt("cnt");
				}
				rs.close();
				pst.close();
//				System.out.println(" else nApllied====>"+nApllied+" encashNoOfTimes=====>"+encashNoOfTimes);
//				if(!isEncahsmentApplied){
				if(nApllied <=encashNoOfTimes){
					
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
					double dblBalance = 0;
					double dblPendingLeaves = 0;
					for (int j=0; j<nLeaveListSize; j++) {
						List<String> cinnerlist = (List<String>)leaveList.get(j);
						
						if(uF.parseToInt(cinnerlist.get(6)) == uF.parseToInt(strLeaveTypeId)){
							dblBalance = uF.parseToDouble(cinnerlist.get(5));							
							dblPendingLeaves = uF.parseToDouble(cinnerlist.get(8));							
						}					
					}
					
					double dblAvailbleForEncashment = 0;
					dblAvailbleForEncashment = dblBalance - dblMinLeavesForEncashment - dblPendingLeaves;
					if(dblAvailbleForEncashment<=0){
						dblAvailbleForEncashment = 0;
					}
//					System.out.println("dblBalance=="+dblBalance);
//					System.out.println("dblMinLeavesForEncashment=="+dblMinLeavesForEncashment);
//					System.out.println("dblPendingLeaves=="+dblPendingLeaves);
//					System.out.println("dblAvailbleForEncashment=="+dblAvailbleForEncashment);
//					System.out.println("avail=="+dblBalance);
					
					StringBuilder sb = new StringBuilder();
					sb.append("Total Available Leaves = "+dblBalance+"<br/>");
					sb.append("Total Pending Leaves = "+dblPendingLeaves+"<br/>");
					sb.append("Available for encashment = "+dblAvailbleForEncashment+"<br/>");
					sb.append("Maximum encashment apply = "+dblMaxLeavesForEncashment+"<br/>");
					sb.append("<input type=\"hidden\" name=\"strAvailableEncashment\" id=\"strAvailableEncashment\" value=\""+dblAvailbleForEncashment+"\">");
					sb.append("<input type=\"hidden\" name=\"strMaxLeavesForEncashment\" id=\"strMaxLeavesForEncashment\" value=\""+dblMaxLeavesForEncashment+"\">");
					request.setAttribute("STATUS_MSG", sb.toString());
//					System.out.println("sb.toString() else ===>> " + sb.toString());
				}else{
					StringBuilder sb = new StringBuilder();
					
					sb.append("You can apply "+encashNoOfTimes+" for leave encashment in a year. Last applied date was "+strEntryDate);
					sb.append("<input type=\"hidden\" name=\"strAvailableEncashment\" id=\"strAvailableEncashment\" value=\"0\">");
					sb.append("<input type=\"hidden\" name=\"strMaxLeavesForEncashment\" id=\"strMaxLeavesForEncashment\" value=\"0\">");
					request.setAttribute("STATUS_MSG", sb.toString());
					
//					System.out.println("sb.toString() else ===>> " + sb.toString());
				}
				
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

