 package com.konnect.jpms.employee;

import java.io.File;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class AdminDashboard implements IStatements {

	HttpServletRequest request;
	HttpSession session;
	CommonFunctions CF;
	String strEmpId;
	String navigationId;
	String toPage;
	String toTab;
	String strOrg;
	String strLocation;
	String strCFYear;
	
	String strLevel;
	String strSalaryHeadId;
	String strGrade;
	String salaryBand;
	String paycycle;

	AdminDashboard(HttpServletRequest request, HttpSession session, CommonFunctions CF, String strEmpId, String navigationId, String toPage, String toTab, String strOrg, String strLocation, String strLevel, String salaryBand, String strCFYear, String strSalaryHeadId, String strGrade, String paycycle) {
		this.request = request;
		this.session = session;
		this.CF = CF;
		this.strEmpId = strEmpId;
		this.navigationId = navigationId;
		this.toPage = toPage;
		this.toTab = toTab;
		this.strOrg = strOrg;
		this.strLocation = strLocation;
		this.strLevel = strLevel;
		this.strCFYear = strCFYear;
		this.strSalaryHeadId = strSalaryHeadId;
		this.strGrade = strGrade;
		this.salaryBand = salaryBand;
		this.paycycle = paycycle;
	}

	
	public String loadDashboard() {

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		UtilityFunctions uF = new UtilityFunctions();

		try {
//			System.out.println("navigationId ===>> " + navigationId);
//			System.out.println("strOrg ===>> " + strOrg);
//			System.out.println("strLocation ===>> " + strLocation);
//			System.out.println("toPage ===>> " + toPage);
			
			con = db.makeConnection(con);
			

			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			pst = con.prepareStatement(selectEmployee1V);
			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
			rs = pst.executeQuery();
			if(rs.next()) {
				request.setAttribute("EMPCODE", rs.getString("empcode"));
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				request.setAttribute("EMPNAME", rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname"));
				request.setAttribute("DATE", uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()) + "", "yyyy-MM-dd", "EEEE, MMMM dd,yyyy"));
				request.setAttribute("IMAGE", ((rs.getString("emp_image") != null && rs.getString("emp_image").length() > 0) ? rs.getString("emp_image") : "avatar_photo.png"));
				request.setAttribute("DEPT", rs.getString("dept_name"));
				request.setAttribute("WL_NAME", rs.getString("wlocation_name"));
				request.setAttribute("EMAIL", rs.getString("emp_email"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select _label from navigation_1 where navigation_id = ?");
			pst.setInt(1, uF.parseToInt(navigationId));
			rs = pst.executeQuery();
			String naviName="";
			if(rs.next()) {
				naviName = rs.getString("_label");
			}
			rs.close();
			pst.close();

			StringBuilder sbpageTitleNaviTrail = new StringBuilder();
			sbpageTitleNaviTrail.append("<li><i class=\"fa fa-cogs\"></i><a href=\"MyDashboard.action?userscreen=Global HR&navigationId=102&toPage=CS\" style=\"color: #3c8dbc;\"> Control Panel</a></li>" +
				"<li class=\"active\">"+naviName+"</li>");
			request.setAttribute("PAGETITLE_NAVITRAIL", sbpageTitleNaviTrail.toString());
			
			
			/*pst = con.prepareStatement(selectEmployee3V);
			pst.setInt(1, uF.parseToInt((String) session.getAttribute(EMPID)));

			rs = pst.executeQuery();

			if (rs.next()) {
				request.setAttribute(MANAGER, rs.getString("emp_fname") + " " + rs.getString("emp_lname"));
			}

			pst.close();

			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
			int YEAR = cal.get(Calendar.YEAR);
			int MONTH = cal.get(Calendar.MONTH) + 1;
			int Day = cal.get(Calendar.DAY_OF_MONTH);
			int MinDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
			int MaxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

			pst = con.prepareStatement(selectPresentDays1);
			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));

			pst.setDate(2, uF.getDateFormat(YEAR + "-" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "-" + ((MinDay < 10) ? "0" + MinDay : MinDay), DBDATE));
			pst.setDate(3, uF.getDateFormat(YEAR + "-" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "-" + ((MaxDay < 10) ? "0" + MaxDay : MaxDay), DBDATE));

			rs = pst.executeQuery();

			if (rs.next()) {
				request.setAttribute("COUNT", rs.getString("count"));
			}

			pst.close();

			pst = con.prepareStatement(selectApprovalsCountAdmin);
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			int totalApprovals = 0;
			while (rs.next()) {

				if (rs.getInt("approved") == -2) {
					request.setAttribute("PENDING", rs.getString("count"));
					totalApprovals += rs.getInt("count");
				} else if (rs.getInt("approved") == -1) {
					request.setAttribute("DENIED", rs.getString("count"));
					totalApprovals += rs.getInt("count");
				} else if (rs.getInt("approved") == 1) {
					request.setAttribute("APPROVED", rs.getString("count"));
					totalApprovals += rs.getInt("count");
				}
			}
			request.setAttribute("TOTAL_APPROVALS", totalApprovals);
			pst.close();

			pst = con.prepareStatement(selectWLocation);
			rs = pst.executeQuery();

			String strW1 = null;
			String strW2 = null;
			List alBusinessName = new ArrayList();
			int i = 0;
			while (rs.next()) {
				if (i == 0) {
					strW1 = rs.getString("wlocation_name");

				} else if (i == 1) {
					strW2 = rs.getString("wlocation_name");
				} else {
					break;
				}
				alBusinessName.add(rs.getString("wlocation_name"));
				i++;

			}
			request.setAttribute("WLOCATION", alBusinessName);

			Map hmPresentDays = new HashMap();

			pst = con.prepareStatement(selectPresentDays2);
			pst.setString(1, strW1);
			pst.setString(2, strW2);

			pst.setDate(3, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()) + "", DBDATE));
			// pst.setDate(3, uF.getDateFormat("2011-04-02", "yyyy-MM-dd"));

			rs = pst.executeQuery();

			while (rs.next()) {
				hmPresentDays.put(rs.getString("wlocation_name"), rs.getString("present"));
			}

			request.setAttribute("PRENSENCE", hmPresentDays);
			pst.close();

			Map hmEmployeeCount = new HashMap();
			Map hmAbsenceCount = new HashMap();
			pst = con.prepareStatement(selectEmployeeCount);
			rs = pst.executeQuery();

			while (rs.next()) {

				int presentEmp = 0;

				presentEmp = uF.parseToInt((String) hmPresentDays.get(rs.getString("wlocation_name")));

				hmAbsenceCount.put(rs.getString("wlocation_name"), (rs.getInt("empcount") - presentEmp) + "");
				hmEmployeeCount.put(rs.getString("wlocation_name"), rs.getString("empcount"));

			}

			request.setAttribute("EMPCOUNT", hmEmployeeCount);
			request.setAttribute("ABSENCE", hmAbsenceCount);
			pst.close();*/
			
			
			getBackupDetails(uF);
			
			
			String []arrAllModules = CF.getArrAllModules();
			request.setAttribute("arrAllModules", arrAllModules);
			
 
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return LOAD;

	}
	
	public void getBackupDetails(UtilityFunctions uF){
		
		List<List<String>> alReport = new ArrayList<List<String>>();
		
		try {
			String backupLocation = CF.getBackUpLocation();	
			if(backupLocation==null)return;
			
			File folder = new File(backupLocation);
			File []listOfFiles = folder.listFiles();
			
			
			if(listOfFiles!=null){
				Arrays.sort(listOfFiles,Collections.reverseOrder());
				List<String> alInner = new ArrayList<String>();
				
				for(int i=0;listOfFiles!=null && i<listOfFiles.length;){
					alInner.add(new Date(listOfFiles[i].lastModified())+"");
					break;
				}
			}

			
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("alReport", alReport);
	}
	
	
	
}
