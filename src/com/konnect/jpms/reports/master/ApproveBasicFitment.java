package com.konnect.jpms.reports.master;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.select.FillYears;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.CommonFunctions; 
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ApproveBasicFitment extends ActionSupport implements ServletRequestAware, IStatements{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5947893602821384559L;
	String strUserType = null;
	String strSessionEmpId = null;
	
	HttpSession session;
	CommonFunctions CF; 

	String f_org;
	String[] strWLocation;
	String[] department;
	String strMonth;
	String strYear;
	
	List<FillOrganisation> organisationList;
	List<FillWLocation> wLocationList;
	List<FillDepartment> departmentList;
	List<FillMonth> monthList;
	List<FillYears> yearList;
	
	String operation;
	
	public String execute()	{
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		request.setAttribute(PAGE, "/jsp/reports/master/ApproveBasicFitment.jsp");
		request.setAttribute(TITLE, "Approve Basic Fitment");
		
		UtilityFunctions uF = new UtilityFunctions();
//		boolean isView  = CF.getAccess(session, request, uF);
//		if(!isView){
//			request.setAttribute(PAGE, PAccessDenied);
//			request.setAttribute(TITLE, TAccessDenied);
//			return ACCESS_DENIED;
//		}
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		if(getOperation()!=null && getOperation().equals("D")){
			denyGrade(uF);
			return "ajax";
		}
		
		if(uF.parseToInt(getF_org()) == 0){
			setF_org((String)session.getAttribute(ORGID));
		}
		if(getStrMonth() ==null){
			setStrMonth(uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "MM"))+"");
			setStrYear(uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "yyyy"))+"");
		}
		getBasicFitmentData(uF);
		
		
		return loadAttendanceRegister(uF);
	}
	
private void getBasicFitmentData(UtilityFunctions uF) {

	Connection con = null;
	PreparedStatement pst = null;
	ResultSet rs = null;
	Database db = new Database();
	db.setRequest(request);
	try {
		con = db.makeConnection(con);
		
		pst = con.prepareStatement("select * from org_details where org_id=?");
		pst.setInt(1, uF.parseToInt(getF_org()));
		rs = pst.executeQuery();
		int nIncrementBase = 0;
		int nMonth = 0;
		while(rs.next()){
			nIncrementBase = rs.getInt("increment_type");
			nMonth = rs.getInt("increment_month");
		}
		rs.close();
		pst.close();
		
		if(nIncrementBase == 1){
			getBasicFitmentJoiningDateEmpList(con,uF);
		} else if(nIncrementBase == 2 && nMonth == uF.parseToInt(getStrMonth())){
			getBasicFitmentCalendarYearEmpList(con,uF);
		} else if(nIncrementBase == 2){
			getDeferBasicFitmentCalendarYearEmpList(con,uF);
		}
		
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		db.closeResultSet(rs);
		db.closeStatements(pst);
		db.closeConnection(con);
	}
}

private void getDeferBasicFitmentCalendarYearEmpList(Connection con, UtilityFunctions uF) {

	PreparedStatement pst = null;
	ResultSet rs = null;
	try {
		Map<String, String> hmGradeMap = new HashMap<String, String>();
		Map<String, String> hmGradeWeightageMap = new HashMap<String, String>();
		Map<String, Map<String, String>> hmDesigGradeWeightageMap = new HashMap<String, Map<String, String>>();
	
		Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
		boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
		
		pst = con.prepareStatement("select * from grades_details gd, designation_details dd where dd.designation_id = gd.designation_id " +
				"and is_fitment=true and dd.level_id in(select ld.level_id from level_details ld where ld.org_id=?) and dd.level_id > 0 " +
				"order by gd.weightage desc,gd.grade_id,dd.level_id");
		pst.setInt(1, uF.parseToInt(getF_org()));
		rs = pst.executeQuery();
		while (rs.next()) {
			hmGradeMap.put(rs.getString("grade_id"), rs.getString("grade_code"));
			hmGradeWeightageMap.put(rs.getString("grade_id"), ""+uF.parseToInt(rs.getString("weightage")));
			
			Map<String, String> hmGradeWeightage = hmDesigGradeWeightageMap.get(rs.getString("designation_id"));
			if(hmGradeWeightage==null) hmGradeWeightage = new LinkedHashMap<String, String>();
			
			hmGradeWeightage.put(rs.getString("grade_id"),rs.getString("weightage"));
			
			hmDesigGradeWeightageMap.put(rs.getString("designation_id"), hmGradeWeightage);
			
		}
		rs.close();
		pst.close();
		
		Map<String, String> hmBasicFitAmtMap = new HashMap<String, String>();
		pst = con.prepareStatement("select * from basic_fitment_details bfd, grades_details gd where bfd.GRADE_ID = gd.GRADE_ID and bfd.trail_status=1");
		rs = pst.executeQuery();
		while (rs.next()) {
			hmBasicFitAmtMap.put(rs.getString("grade_id"), rs.getString("amount"));
		}
		rs.close();
		pst.close();
		
		Map<String, Map<String, String>> hmDeferMap = new HashMap<String, Map<String, String>>();
		StringBuilder sbQuery = new StringBuilder();
		sbQuery.append("select * from emp_defer_basic_fitment where defer_status=true and fitment_month=? and fitment_year=?");
		sbQuery.append(" and emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " +
				"where epd.emp_per_id = eod.emp_id and org_id=? ");
		if(getStrWLocation()!=null && getStrWLocation().length>0){
            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getStrWLocation(), ",")+") ");
        }
		if(getDepartment()!=null && getDepartment().length>0){
            sbQuery.append(" and depart_id in ("+StringUtils.join(getDepartment(), ",")+") ");
        }
		sbQuery.append(")");
		pst = con.prepareStatement(sbQuery.toString());
		pst.setInt(1, uF.parseToInt(getStrMonth()));
		pst.setInt(2, uF.parseToInt(getStrYear()));
		pst.setInt(3, uF.parseToInt(getF_org()));
		rs = pst.executeQuery();
		while (rs.next()) {
			Map<String, String> hmInner = new HashMap<String, String>();
			hmInner.put("EMP_ID", rs.getString("emp_id"));
			hmInner.put("DEFER_DATE", uF.getDateFormat(rs.getString("defer_date"), DBDATE, DATE_FORMAT));
			hmInner.put("GRADE_FROM", rs.getString("grade_from"));
			hmInner.put("GRADE_TO", rs.getString("grade_to"));
			hmInner.put("FITMENT_MONTH", rs.getString("fitment_month"));
			hmInner.put("FITMENT_YEAR", rs.getString("fitment_year"));
			hmInner.put("ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, DATE_FORMAT));
			hmInner.put("APPROVE_BY", rs.getString("approve_by"));
			hmInner.put("DEFER_STATUS", rs.getString("defer_status"));
			
			hmDeferMap.put(rs.getString("emp_id"), hmInner);
		}
		rs.close();
		pst.close();
		
		Map<String, Map<String, String>> hmDeferedEmp = new HashMap<String, Map<String, String>>();
		sbQuery = new StringBuilder();
		sbQuery.append("select * from emp_defer_basic_fitment where defer_status=true and EXTRACT(month FROM defer_date)=? and EXTRACT(year FROM defer_date)=?");
		sbQuery.append(" and emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " +
				"where epd.emp_per_id = eod.emp_id and org_id=? ");
		if(getStrWLocation()!=null && getStrWLocation().length>0){
            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getStrWLocation(), ",")+") ");
        }
		if(getDepartment()!=null && getDepartment().length>0){
            sbQuery.append(" and depart_id in ("+StringUtils.join(getDepartment(), ",")+") ");
        }
		sbQuery.append(")");
		pst = con.prepareStatement(sbQuery.toString());
		pst.setInt(1, uF.parseToInt(getStrMonth()));
		pst.setInt(2, uF.parseToInt(getStrYear()));
		pst.setInt(3, uF.parseToInt(getF_org()));
		rs = pst.executeQuery();
		while (rs.next()) {
			Map<String, String> hmInner = new HashMap<String, String>();
			hmInner.put("EMP_ID", rs.getString("emp_id"));
			hmInner.put("DEFER_DATE", uF.getDateFormat(rs.getString("defer_date"), DBDATE, DATE_FORMAT));
			hmInner.put("GRADE_FROM", rs.getString("grade_from"));
			hmInner.put("GRADE_TO", rs.getString("grade_to"));
			hmInner.put("FITMENT_MONTH", rs.getString("fitment_month"));
			hmInner.put("FITMENT_MONTH_NAME", uF.getMonth(uF.parseToInt(rs.getString("FITMENT_MONTH"))));
			hmInner.put("FITMENT_YEAR", rs.getString("fitment_year"));
			hmInner.put("ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, DATE_FORMAT));
			hmInner.put("APPROVE_BY", rs.getString("approve_by"));
			hmInner.put("DEFER_STATUS", rs.getString("defer_status"));
			
			hmDeferedEmp.put(rs.getString("emp_id"), hmInner);
		}
		rs.close();
		pst.close();
		
		Map<String, Map<String, String>> hmEmpBasicFitAmtMap = new HashMap<String, Map<String, String>>();
		sbQuery = new StringBuilder();
		sbQuery.append("select * from emp_basic_fitment_details where fitment_month=? and fitment_year=?");
		sbQuery.append(" and emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " +
				"where epd.emp_per_id = eod.emp_id and org_id=? ");
		if(getStrWLocation()!=null && getStrWLocation().length>0){
            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getStrWLocation(), ",")+") ");
        }
		if(getDepartment()!=null && getDepartment().length>0){
            sbQuery.append(" and depart_id in ("+StringUtils.join(getDepartment(), ",")+") ");
        }
		sbQuery.append(")");
		pst = con.prepareStatement(sbQuery.toString());
		pst.setInt(1, uF.parseToInt(getStrMonth()));
		pst.setInt(2, uF.parseToInt(getStrYear()));
		pst.setInt(3, uF.parseToInt(getF_org()));
		rs = pst.executeQuery();
		while (rs.next()) {
			Map<String, String> hmInner = hmEmpBasicFitAmtMap.get(rs.getString("EMP_ID"));
			if(hmInner==null) hmInner = new HashMap<String, String>();
			hmInner.put("EMP_FITMENT_ID", rs.getString("EMP_FITMENT_ID"));
			hmInner.put("EMP_ID", rs.getString("EMP_ID"));
			hmInner.put("GRADE_FROM", rs.getString("GRADE_FROM"));
			hmInner.put("GRADE_TO", rs.getString("GRADE_TO"));
			hmInner.put("FITMENT_MONTH", rs.getString("fitment_month"));
			hmInner.put("FITMENT_YEAR", rs.getString("fitment_year"));
			hmInner.put("ENTRY_DATE", uF.getDateFormat(rs.getString("ENTRY_DATE"), DBDATE, DATE_FORMAT));
			hmInner.put("APPROVE_BY", rs.getString("APPROVE_BY"));
			hmInner.put("APPROVE_STATUS", rs.getString("APPROVE_STATUS"));
			
			hmEmpBasicFitAmtMap.put(rs.getString("EMP_ID"), hmInner);
		}
		rs.close();
		pst.close();
		
		Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
		Map<String, String> hmEmpDesigMap = CF.getEmpDesigMapId(con);
		Map<String, String> hmLevelMap = CF.getLevelMap(con);
		sbQuery = new StringBuilder();
		sbQuery.append("select * from employee_personal_details epd,employee_official_details eod  where epd.emp_per_id = eod.emp_id " +
				"and eod.grade_id in (select grade_id from grades_details where is_fitment=true) and eod.org_id=? and epd.IS_ALIVE=true ");
		if(getStrWLocation()!=null && getStrWLocation().length>0){
            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getStrWLocation(), ",")+") ");
        }
		if(getDepartment()!=null && getDepartment().length>0){
            sbQuery.append(" and depart_id in ("+StringUtils.join(getDepartment(), ",")+") ");
        }
		sbQuery.append("and eod.emp_id in(select emp_id from emp_defer_basic_fitment where defer_status=true and EXTRACT(month FROM defer_date)=? and EXTRACT(year FROM defer_date)=?)"+
				" order by epd.EMP_FNAME, epd.EMP_LNAME");
		pst = con.prepareStatement(sbQuery.toString());
		pst.setInt(1, uF.parseToInt(getF_org()));
		pst.setInt(2, uF.parseToInt(getStrMonth()));
		pst.setInt(3, uF.parseToInt(getStrYear()));
		rs = pst.executeQuery();
		Map<String, Map<String, String>> hmFitmentMap = new LinkedHashMap<String, Map<String, String>>();
		int cnt = 0;
		while (rs.next()){
			String nextGrade = getNextGradeWeightage(rs.getString("grade_id"),hmEmpDesigMap.get(rs.getString("emp_id")),hmGradeWeightageMap.get(rs.getString("grade_id")),hmDesigGradeWeightageMap,uF);
			if(nextGrade==null && hmEmpBasicFitAmtMap.get(rs.getString("emp_id"))==null){ 
				continue;
			}
			cnt++;
			Map<String, String> hmBasicFitment = hmFitmentMap.get(rs.getString("emp_id"));
			if(hmBasicFitment==null) hmBasicFitment = new LinkedHashMap<String, String>();
			
			hmBasicFitment.put("EMP_ID", rs.getString("emp_id"));
			hmBasicFitment.put("GRADE_ID", rs.getString("grade_id"));
			
			String levelName =uF.showData(hmLevelMap.get(hmEmpLevelMap.get(rs.getString("emp_id"))), "");
			
			StringBuilder sb = new StringBuilder(); 
			
			Map<String, String> hmInner = hmEmpBasicFitAmtMap.get(rs.getString("emp_id"));
			if(hmInner!=null && hmInner.get("APPROVE_STATUS").equals("1")){
				double dblCurrentbasic = getCurrentBasic(con,uF,rs.getString("emp_id"),hmInner.get("GRADE_FROM"));
				
			
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				sb.append("<div style=\"float:left;\" id=\"myDiv_" + cnt + "\" ><strong>"+rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname")+"</strong>, <strong>"+levelName+"</strong>, " +
						"is due for an auto increment based on Fitment for upgrade from Grade <strong>"+uF.showData(hmGradeMap.get(hmInner.get("GRADE_FROM")), "")+"</strong> " +
						"(Rs. <strong>"+dblCurrentbasic+"/-)</strong> " +
						"to <strong>"+uF.showData(hmGradeMap.get(hmInner.get("GRADE_TO")), "")+"</strong> " +
						"(Rs. <strong>"+uF.showData(hmBasicFitAmtMap.get(hmInner.get("GRADE_TO")), "0")+"/-)</strong></div>");
				
				sb.append("<div style=\"float:left; padding-left: 15px;\" id=\"myDivM_" + cnt + "\" > ");
				 /*sb.append("<img title=\"Approved\" src=\""+ request.getContextPath()+ "/images1/icons/approved.png\" border=\"0\">");*/
				sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>");
				sb.append("</div> ");
			} else if(hmInner!=null && hmInner.get("APPROVE_STATUS").equals("-1")){
				double dblCurrentbasic = getCurrentBasic(con,uF,rs.getString("emp_id"),hmInner.get("GRADE_FROM"));
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				sb.append("<div style=\"float:left;\" id=\"myDiv_" + cnt + "\" ><strong>"+rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname")+"</strong>, <strong>"+levelName+"</strong>, " +
						"is due for an auto increment based on Fitment for upgrade from Grade <strong>"+uF.showData(hmGradeMap.get(hmInner.get("GRADE_FROM")), "")+"</strong> " +
						"(Rs. <strong>"+dblCurrentbasic+"/-)</strong> " +
						"to <strong>"+uF.showData(hmGradeMap.get(hmInner.get("GRADE_TO")), "")+"</strong> " +
						"(Rs. <strong>"+uF.showData(hmBasicFitAmtMap.get(hmInner.get("GRADE_TO")), "0")+"/-)</strong></div>");
				
				sb.append("<div style=\"float:left; padding-left: 15px;\" id=\"myDivM_" + cnt + "\" > ");
				/*sb.append("<img title=\"Denied\" src=\""+ request.getContextPath()+ "/images1/icons/denied.png\" border=\"0\">");*/
				sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\" title=\"Denied\"></i>");
				sb.append("</div> ");
			} else {
				double dblCurrentbasic = getCurrentBasic(con,uF,rs.getString("emp_id"),rs.getString("grade_id"));
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				sb.append("<div style=\"float:left;\" id=\"myDiv_" + cnt + "\" ><strong>"+rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname")+"</strong>, <strong>"+levelName+"</strong>, " +
						"is due for an auto increment based on Fitment for upgrade from Grade <strong>"+uF.showData(hmGradeMap.get(rs.getString("grade_id")), "")+"</strong> " +
						"(Rs. <strong>"+dblCurrentbasic+"/-)</strong> " +
						"to <strong>"+uF.showData(hmGradeMap.get(nextGrade), "")+"</strong> " +
						"(Rs. <strong>"+uF.showData(hmBasicFitAmtMap.get(nextGrade), "0")+"/-)</strong></div>");
				
				if(hmDeferMap.containsKey(rs.getString("emp_id"))){
					Map<String, String> hmDefer = hmDeferMap.get(rs.getString("emp_id"));
					sb.append("<div style=\"float:left; padding-left: 15px;\" id=\"myDivM_" + cnt + "\" > ");
					 /*sb.append("&nbsp;&nbsp;<strong>[Defer Date-&nbsp;"+uF.showData(hmDefer.get("DEFER_DATE"), "")+"]&nbsp;&nbsp;</strong><img src=\"images1/icons/pending.png\" title=\"Defer\" /> ");*/
					sb.append("&nbsp;&nbsp;<strong>[Defer Date-&nbsp;"+uF.showData(hmDefer.get("DEFER_DATE"), "")+"]&nbsp;&nbsp;</strong><i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\" title=\"Defer\"></i> ");
					
					sb.append("</div> ");
				} else {
					String divId="myDivM_" + cnt;
					sb.append("<div style=\"float:left; padding-left: 15px;\" id=\"" + divId + "\" > ");
					if(hmDeferedEmp.containsKey(rs.getString("emp_id"))){
						Map<String, String> hmDeferInner = hmDeferedEmp.get(rs.getString("emp_id"));
						sb.append("&nbsp;&nbsp;<strong>[Defer From-&nbsp;"+uF.showData(hmDeferInner.get("FITMENT_MONTH_NAME"), "")+","+uF.showData(hmDeferInner.get("FITMENT_YEAR"), "")+"]&nbsp;&nbsp;</strong> ");
					}
				
					
					sb.append("&nbsp;&nbsp;<a href=\"javascript:void(0)\" onclick=\"if(confirm('Are you sure, you want to approve this request?')) " +
							"getSalaryStructure('"+rs.getString("emp_id")+"','"+rs.getString("grade_id")+"','"+nextGrade+"','"+getStrMonth()+"','"+getStrYear()+"','"+rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname")+"');\" >" +
									"<i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"  title=\"Approve\"></i></a> ");
					
					String denyUrl="ApproveBasicFitment.action?operation=D&emp_id="+rs.getString("emp_id")+"&grade_from="+rs.getString("grade_id")+"&grade_to="+nextGrade+"&fitmentMonth="+getStrMonth()+"&fitmentYear="+getStrYear();
					sb.append("&nbsp;<a href=\"javascript:void(0)\" onclick=\"if(confirm('Are you sure, you want to deny this request?')) getContent('"+divId+"', '"+denyUrl+"');\">" + " <i class=\"fa fa-times-circle cross\" aria-hidden=\"true\" title=\"Deny\"></i></a> ");
					
					sb.append("&nbsp;<a href=\"javascript:void(0)\" onclick=\"if(confirm('Are you sure, you want to defer this request?')) " +
							/*"getDefer('"+rs.getString("emp_id")+"','"+rs.getString("grade_id")+"','"+nextGrade+"','"+getStrMonth()+"','"+getStrYear()+"','"+rs.getString("emp_fname")+" "+rs.getString("emp_lname")+"');\"><img src=\"images1/icons/pending.png\" title=\"Defer\" /></a> ");*/
							"getDefer('"+rs.getString("emp_id")+"','"+rs.getString("grade_id")+"','"+nextGrade+"','"+getStrMonth()+"','"+getStrYear()+"','"+rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname")+"');\"><i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\" title=\"Defer\"></i></a> ");
					
					sb.append("</div> ");
				}
			}
			hmBasicFitment.put("EMP_DATA", sb.toString());
			
			
			hmFitmentMap.put(rs.getString("emp_id"),hmBasicFitment);
			
		}
		rs.close();
		pst.close();
		request.setAttribute("hmFitmentMap",hmFitmentMap );
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		if(rs!=null){
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			rs = null;
		}
		if(pst!=null){
			try {
				pst.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			pst = null;
		}
	}
}

private void getBasicFitmentCalendarYearEmpList(Connection con, UtilityFunctions uF) {

	PreparedStatement pst = null;
	ResultSet rs = null;
	try {
		Map<String, String> hmGradeMap = new HashMap<String, String>();
		Map<String, String> hmGradeWeightageMap = new HashMap<String, String>();
		Map<String, Map<String, String>> hmDesigGradeWeightageMap = new HashMap<String, Map<String, String>>();
		
		pst = con.prepareStatement("select * from grades_details gd, designation_details dd where dd.designation_id = gd.designation_id " +
				"and is_fitment=true and dd.level_id in(select ld.level_id from level_details ld where ld.org_id=?) and dd.level_id > 0 " +
				"order by gd.weightage desc,gd.grade_id,dd.level_id");
		pst.setInt(1, uF.parseToInt(getF_org()));
		rs = pst.executeQuery();
		while (rs.next()) {
			hmGradeMap.put(rs.getString("grade_id"), rs.getString("grade_code"));
			hmGradeWeightageMap.put(rs.getString("grade_id"), ""+uF.parseToInt(rs.getString("weightage")));
			
			Map<String, String> hmGradeWeightage = hmDesigGradeWeightageMap.get(rs.getString("designation_id"));
			if(hmGradeWeightage==null) hmGradeWeightage = new LinkedHashMap<String, String>();
			
			hmGradeWeightage.put(rs.getString("grade_id"),rs.getString("weightage"));
			
			hmDesigGradeWeightageMap.put(rs.getString("designation_id"), hmGradeWeightage);
			
		}
		rs.close();
		pst.close();
		
		Map<String, String> hmBasicFitAmtMap = new HashMap<String, String>();
		pst = con.prepareStatement("select * from basic_fitment_details bfd, grades_details gd where bfd.GRADE_ID = gd.GRADE_ID and bfd.trail_status=1");
		rs = pst.executeQuery();
		while (rs.next()) {
			hmBasicFitAmtMap.put(rs.getString("grade_id"), rs.getString("amount"));
		}
		rs.close();
		pst.close();
		
		Map<String, Map<String, String>> hmDeferMap = new HashMap<String, Map<String, String>>();
		StringBuilder sbQuery = new StringBuilder();
		sbQuery.append("select * from emp_defer_basic_fitment where defer_status=true and fitment_month=? and fitment_year=?");
		sbQuery.append(" and emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " +
				"where epd.emp_per_id = eod.emp_id and org_id=? ");
		if(getStrWLocation()!=null && getStrWLocation().length>0){
            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getStrWLocation(), ",")+") ");
        }
		if(getDepartment()!=null && getDepartment().length>0){
            sbQuery.append(" and depart_id in ("+StringUtils.join(getDepartment(), ",")+") ");
        }
		sbQuery.append(")");
		pst = con.prepareStatement(sbQuery.toString());
		pst.setInt(1, uF.parseToInt(getStrMonth()));
		pst.setInt(2, uF.parseToInt(getStrYear()));
		pst.setInt(3, uF.parseToInt(getF_org()));		
		rs = pst.executeQuery();
		while (rs.next()) {
			Map<String, String> hmInner = new HashMap<String, String>();
			hmInner.put("EMP_ID", rs.getString("emp_id"));
			hmInner.put("DEFER_DATE", uF.getDateFormat(rs.getString("defer_date"), DBDATE, DATE_FORMAT));
			hmInner.put("GRADE_FROM", rs.getString("grade_from"));
			hmInner.put("GRADE_TO", rs.getString("grade_to"));
			hmInner.put("FITMENT_MONTH", rs.getString("fitment_month"));
			hmInner.put("FITMENT_YEAR", rs.getString("fitment_year"));
			hmInner.put("ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, DATE_FORMAT));
			hmInner.put("APPROVE_BY", rs.getString("approve_by"));
			hmInner.put("DEFER_STATUS", rs.getString("defer_status"));
			
			hmDeferMap.put(rs.getString("emp_id"), hmInner);
		}
		rs.close();
		pst.close();
		
		Map<String, Map<String, String>> hmDeferedEmp = new HashMap<String, Map<String, String>>();
		
		sbQuery = new StringBuilder();
		sbQuery.append("select * from emp_defer_basic_fitment where defer_status=true and EXTRACT(month FROM defer_date)=? and EXTRACT(year FROM defer_date)=?");
		sbQuery.append(" and emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " +
				"where epd.emp_per_id = eod.emp_id and org_id=? ");
		if(getStrWLocation()!=null && getStrWLocation().length>0){
            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getStrWLocation(), ",")+") ");
        }
		if(getDepartment()!=null && getDepartment().length>0){
            sbQuery.append(" and depart_id in ("+StringUtils.join(getDepartment(), ",")+") ");
        }
		sbQuery.append(")");
		pst = con.prepareStatement(sbQuery.toString());
		pst.setInt(1, uF.parseToInt(getStrMonth()));
		pst.setInt(2, uF.parseToInt(getStrYear()));
		pst.setInt(3, uF.parseToInt(getF_org()));		
		rs = pst.executeQuery();
		while (rs.next()) {
			Map<String, String> hmInner = new HashMap<String, String>();
			hmInner.put("EMP_ID", rs.getString("emp_id"));
			hmInner.put("DEFER_DATE", uF.getDateFormat(rs.getString("defer_date"), DBDATE, DATE_FORMAT));
			hmInner.put("GRADE_FROM", rs.getString("grade_from"));
			hmInner.put("GRADE_TO", rs.getString("grade_to"));
			hmInner.put("FITMENT_MONTH", rs.getString("fitment_month"));
			hmInner.put("FITMENT_MONTH_NAME", uF.getMonth(uF.parseToInt(rs.getString("FITMENT_MONTH"))));
			hmInner.put("FITMENT_YEAR", rs.getString("fitment_year"));
			hmInner.put("ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, DATE_FORMAT));
			hmInner.put("APPROVE_BY", rs.getString("approve_by"));
			hmInner.put("DEFER_STATUS", rs.getString("defer_status"));
			
			hmDeferedEmp.put(rs.getString("emp_id"), hmInner);
		}
		rs.close();
		pst.close();
		
		Map<String, Map<String, String>> hmEmpBasicFitAmtMap = new HashMap<String, Map<String, String>>();
		
		sbQuery = new StringBuilder();
		sbQuery.append("select * from emp_basic_fitment_details where fitment_month=? and fitment_year=?");
		sbQuery.append(" and emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " +
				"where epd.emp_per_id = eod.emp_id and org_id=? ");
		if(getStrWLocation()!=null && getStrWLocation().length>0){
            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getStrWLocation(), ",")+") ");
        }
		if(getDepartment()!=null && getDepartment().length>0){
            sbQuery.append(" and depart_id in ("+StringUtils.join(getDepartment(), ",")+") ");
        }
		sbQuery.append(")");
		pst = con.prepareStatement(sbQuery.toString());
		pst.setInt(1, uF.parseToInt(getStrMonth()));
		pst.setInt(2, uF.parseToInt(getStrYear()));
		pst.setInt(3, uF.parseToInt(getF_org()));		
		rs = pst.executeQuery();
		while (rs.next()) {
			Map<String, String> hmInner = hmEmpBasicFitAmtMap.get(rs.getString("EMP_ID"));
			if(hmInner==null) hmInner = new HashMap<String, String>();
			hmInner.put("EMP_FITMENT_ID", rs.getString("EMP_FITMENT_ID"));
			hmInner.put("EMP_ID", rs.getString("EMP_ID"));
			hmInner.put("GRADE_FROM", rs.getString("GRADE_FROM"));
			hmInner.put("GRADE_TO", rs.getString("GRADE_TO"));
			hmInner.put("FITMENT_MONTH", rs.getString("fitment_month"));
			hmInner.put("FITMENT_YEAR", rs.getString("fitment_year"));
			hmInner.put("ENTRY_DATE", uF.getDateFormat(rs.getString("ENTRY_DATE"), DBDATE, DATE_FORMAT));
			hmInner.put("APPROVE_BY", rs.getString("APPROVE_BY"));
			hmInner.put("APPROVE_STATUS", rs.getString("APPROVE_STATUS"));
			
			hmEmpBasicFitAmtMap.put(rs.getString("EMP_ID"), hmInner);
		}
		rs.close();
		pst.close();
		
		Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
		Map<String, String> hmEmpDesigMap = CF.getEmpDesigMapId(con);
		Map<String, String> hmLevelMap = CF.getLevelMap(con);
		
		sbQuery = new StringBuilder();
		sbQuery.append("select * from employee_personal_details epd,employee_official_details eod  where epd.emp_per_id = eod.emp_id " +
				"and eod.grade_id in (select grade_id from grades_details where is_fitment=true) and eod.org_id=? and epd.IS_ALIVE=true");
		if(getStrWLocation()!=null && getStrWLocation().length>0){
            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getStrWLocation(), ",")+") ");
        }
		if(getDepartment()!=null && getDepartment().length>0){
            sbQuery.append(" and depart_id in ("+StringUtils.join(getDepartment(), ",")+") ");
        }
		sbQuery.append(" order by epd.EMP_FNAME, epd.EMP_LNAME");
		pst = con.prepareStatement(sbQuery.toString());
		pst.setInt(1, uF.parseToInt(getF_org()));
		pst.setInt(2, uF.parseToInt(getStrMonth()));
		pst.setInt(3, uF.parseToInt(getStrYear()));
		
		rs = pst.executeQuery();
		Map<String, Map<String, String>> hmFitmentMap = new LinkedHashMap<String, Map<String, String>>();
		int cnt = 0;
		while (rs.next()){
			String nextGrade = getNextGradeWeightage(rs.getString("grade_id"),hmEmpDesigMap.get(rs.getString("emp_id")),hmGradeWeightageMap.get(rs.getString("grade_id")),hmDesigGradeWeightageMap,uF);
			if(nextGrade==null && hmEmpBasicFitAmtMap.get(rs.getString("emp_id"))==null){ 
				continue;
			}
//			if(hmEmpBasicFitAmtMap.get(rs.getString("emp_id"))==null){ 
//				continue;
//			}
			cnt++;
			Map<String, String> hmBasicFitment = hmFitmentMap.get(rs.getString("emp_id"));
			if(hmBasicFitment==null) hmBasicFitment = new LinkedHashMap<String, String>();
			
			hmBasicFitment.put("EMP_ID", rs.getString("emp_id"));
			hmBasicFitment.put("GRADE_ID", rs.getString("grade_id"));
			
			String levelName =uF.showData(hmLevelMap.get(hmEmpLevelMap.get(rs.getString("emp_id"))), "");
			
			StringBuilder sb = new StringBuilder(); 
			
			Map<String, String> hmInner = hmEmpBasicFitAmtMap.get(rs.getString("emp_id"));
			if(hmInner!=null && hmInner.get("APPROVE_STATUS").equals("1")){
				double dblCurrentbasic = getCurrentBasic(con,uF,rs.getString("emp_id"),hmInner.get("GRADE_FROM"));
				sb.append("<div style=\"float:left;\" id=\"myDiv_" + cnt + "\" ><strong>"+rs.getString("emp_fname")+" "+rs.getString("emp_lname")+"</strong>, <strong>"+levelName+"</strong>, " +
						"is due for an auto increment based on Fitment for upgrade from Grade <strong>"+uF.showData(hmGradeMap.get(hmInner.get("GRADE_FROM")), "")+"</strong> " +
						"(Rs. <strong>"+dblCurrentbasic+"/-)</strong> " +
						"to <strong>"+uF.showData(hmGradeMap.get(hmInner.get("GRADE_TO")), "")+"</strong> " +
						"(Rs. <strong>"+uF.showData(hmBasicFitAmtMap.get(hmInner.get("GRADE_TO")), "0")+"/-)</strong></div>");
				
				sb.append("<div style=\"float:left; padding-left: 15px;\" id=\"myDivM_" + cnt + "\" > ");
				/*sb.append("<img title=\"Approved\" src=\""+ request.getContextPath()+ "/images1/icons/approved.png\" border=\"0\">");*/
				sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>");
				sb.append("</div> ");
			} else if(hmInner!=null && hmInner.get("APPROVE_STATUS").equals("-1")){
				double dblCurrentbasic = getCurrentBasic(con,uF,rs.getString("emp_id"),hmInner.get("GRADE_FROM"));
				sb.append("<div style=\"float:left;\" id=\"myDiv_" + cnt + "\" ><strong>"+rs.getString("emp_fname")+" "+rs.getString("emp_lname")+"</strong>, <strong>"+levelName+"</strong>, " +
						"is due for an auto increment based on Fitment for upgrade from Grade <strong>"+uF.showData(hmGradeMap.get(hmInner.get("GRADE_FROM")), "")+"</strong> " +
						"(Rs. <strong>"+dblCurrentbasic+"/-)</strong> " +
						"to <strong>"+uF.showData(hmGradeMap.get(hmInner.get("GRADE_TO")), "")+"</strong> " +
						"(Rs. <strong>"+uF.showData(hmBasicFitAmtMap.get(hmInner.get("GRADE_TO")), "0")+"/-)</strong></div>");
				
				sb.append("<div style=\"float:left; padding-left: 15px;\" id=\"myDivM_" + cnt + "\" > ");
				sb.append("<img title=\"Denied\" src=\""+ request.getContextPath()+ "/images1/icons/denied.png\" border=\"0\">");				
				sb.append("</div> ");
			} else {
				double dblCurrentbasic = getCurrentBasic(con,uF,rs.getString("emp_id"),hmInner.get("GRADE_FROM"));
				sb.append("<div style=\"float:left;\" id=\"myDiv_" + cnt + "\" ><strong>"+rs.getString("emp_fname")+" "+rs.getString("emp_lname")+"</strong>, <strong>"+levelName+"</strong>, " +
						"is due for an auto increment based on Fitment for upgrade from Grade <strong>"+uF.showData(hmGradeMap.get(rs.getString("grade_id")), "")+"</strong> " +
						"(Rs. <strong>"+dblCurrentbasic+"/-)</strong> " +
						"to <strong>"+uF.showData(hmGradeMap.get(nextGrade), "")+"</strong> " +
						"(Rs. <strong>"+uF.showData(hmBasicFitAmtMap.get(nextGrade), "0")+"/-)</strong></div>");
				
				if(hmDeferMap.containsKey(rs.getString("emp_id"))){
					Map<String, String> hmDefer = hmDeferMap.get(rs.getString("emp_id"));
					sb.append("<div style=\"float:left; padding-left: 15px;\" id=\"myDivM_" + cnt + "\" > ");
					 /*sb.append("&nbsp;&nbsp;<strong>[Defer Date-&nbsp;"+uF.showData(hmDefer.get("DEFER_DATE"), "")+"]&nbsp;&nbsp;</strong><img src=\"images1/icons/pending.png\" title=\"Defer\" /> ");*/
					sb.append("&nbsp;&nbsp;<strong>[Defer Date-&nbsp;"+uF.showData(hmDefer.get("DEFER_DATE"), "")+"]&nbsp;&nbsp;</strong><i class=\"fa fa-circle\" aria-hidden=\"true\"  title=\"Defer\" style=\"color:#b71cc5\"></i> ");
					
					sb.append("</div> ");
				} else {
					String divId="myDivM_" + cnt;
					sb.append("<div style=\"float:left; padding-left: 15px;\" id=\"" + divId + "\" > ");
					if(hmDeferedEmp.containsKey(rs.getString("emp_id"))){
						Map<String, String> hmDeferInner = hmDeferedEmp.get(rs.getString("emp_id"));
						sb.append("&nbsp;&nbsp;<strong>[Defer From-&nbsp;"+uF.showData(hmDeferInner.get("FITMENT_MONTH_NAME"), "")+","+uF.showData(hmDeferInner.get("FITMENT_YEAR"), "")+"]&nbsp;&nbsp;</strong> ");
					}
					sb.append("&nbsp;&nbsp;<a href=\"javascript:void(0)\" onclick=\"if(confirm('Are you sure, you want to approve this request?')) " +
							"getSalaryStructure('"+rs.getString("emp_id")+"','"+rs.getString("grade_id")+"','"+nextGrade+"','"+getStrMonth()+"','"+getStrYear()+"','"+rs.getString("emp_fname")+" "+rs.getString("emp_lname")+"');\" >" +
									"<i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"  title=\"Approve\"></i></a> ");
					
					String denyUrl="ApproveBasicFitment.action?operation=D&emp_id="+rs.getString("emp_id")+"&grade_from="+rs.getString("grade_id")+"&grade_to="+nextGrade+"&fitmentMonth="+getStrMonth()+"&fitmentYear="+getStrYear();
					sb.append("&nbsp;<a href=\"javascript:void(0)\" onclick=\"if(confirm('Are you sure, you want to deny this request?')) getContent('"+divId+"', '"+denyUrl+"');\">" + " <i class=\"fa fa-times-circle cross\" aria-hidden=\"true\" title=\"Deny\"></i></a> ");
					
					sb.append("&nbsp;<a href=\"javascript:void(0)\" onclick=\"if(confirm('Are you sure, you want to defer this request?')) " +
							/*"getDefer('"+rs.getString("emp_id")+"','"+rs.getString("grade_id")+"','"+nextGrade+"','"+getStrMonth()+"','"+getStrYear()+"','"+rs.getString("emp_fname")+" "+rs.getString("emp_lname")+"');\"><img src=\"images1/icons/pending.png\" title=\"Defer\" /></a> ");*/
							"getDefer('"+rs.getString("emp_id")+"','"+rs.getString("grade_id")+"','"+nextGrade+"','"+getStrMonth()+"','"+getStrYear()+"','"+rs.getString("emp_fname")+" "+rs.getString("emp_lname")+"');\"><i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\" title=\"Defer\"></i></a> ");
					
					sb.append("</div> ");
				}
			}
			hmBasicFitment.put("EMP_DATA", sb.toString());
			
			
			hmFitmentMap.put(rs.getString("emp_id"),hmBasicFitment);
			
		}
		rs.close();
		pst.close();
		request.setAttribute("hmFitmentMap",hmFitmentMap );
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		if(rs!=null){
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			rs = null;
		}
		if(pst!=null){
			try {
				pst.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			pst = null;
		}
	}
}

private void denyGrade(UtilityFunctions uF) {

	Connection con = null;
	PreparedStatement pst = null;
	ResultSet rs = null;
	Database db = new Database();
	db.setRequest(request);
	try {
		con = db.makeConnection(con);
		String emp_id = request.getParameter("emp_id");
		String grade_from = request.getParameter("grade_from");
		String grade_to = request.getParameter("grade_to");
		String fitmentMonth = request.getParameter("fitmentMonth");
		String fitmentYear = request.getParameter("fitmentYear");
		
		pst = con.prepareStatement("insert into emp_basic_fitment_details (EMP_ID,GRADE_FROM,GRADE_TO,FITMENT_MONTH,FITMENT_YEAR,ENTRY_DATE,APPROVE_BY,APPROVE_STATUS) values(?,?,?,?, ?,?,?,?)");
//		pst.setInt(1, CF.getMaxID(con,"EMP_FITMENT_ID","emp_basic_fitment_details"));
		pst.setInt(1, uF.parseToInt(emp_id));
		pst.setInt(2, uF.parseToInt(grade_from));
		pst.setInt(3, uF.parseToInt(grade_to));
		pst.setInt(4, uF.parseToInt(fitmentMonth));
		pst.setInt(5, uF.parseToInt(fitmentYear));
		pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));		
		pst.setInt(7, uF.parseToInt(strSessionEmpId));
		pst.setInt(8, -1);
		int x = pst.executeUpdate();
		
		if(x>0){
			request.setAttribute("STATUS_MSG", "<img title=\"Denied\" src=\""+ request.getContextPath()+ "/images1/icons/denied.png\" border=\"0\">");
		}
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		db.closeResultSet(rs);
		db.closeStatements(pst);
		db.closeConnection(con);
	}
}


private void getBasicFitmentJoiningDateEmpList(Connection con, UtilityFunctions uF) {

	PreparedStatement pst = null;
	ResultSet rs = null;
	try {
		Map<String, String> hmGradeMap = new HashMap<String, String>();
		Map<String, String> hmGradeWeightageMap = new HashMap<String, String>();
		Map<String, Map<String, String>> hmDesigGradeWeightageMap = new HashMap<String, Map<String, String>>();
		pst = con.prepareStatement("select * from grades_details gd, designation_details dd where dd.designation_id = gd.designation_id " +
				"and is_fitment=true and dd.level_id in(select ld.level_id from level_details ld where ld.org_id=?) and dd.level_id > 0 " +
				"order by gd.weightage desc,gd.grade_id,dd.level_id");
		pst.setInt(1, uF.parseToInt(getF_org()));
		rs = pst.executeQuery();
		while (rs.next()) {
			hmGradeMap.put(rs.getString("grade_id"), rs.getString("GRADE_CODE"));
			hmGradeWeightageMap.put(rs.getString("grade_id"), ""+uF.parseToInt(rs.getString("WEIGHTAGE")));
			
			Map<String, String> hmGradeWeightage = hmDesigGradeWeightageMap.get(rs.getString("designation_id"));
			if(hmGradeWeightage==null) hmGradeWeightage = new LinkedHashMap<String, String>();
			
			hmGradeWeightage.put(rs.getString("grade_id"),rs.getString("WEIGHTAGE"));
			
			hmDesigGradeWeightageMap.put(rs.getString("designation_id"), hmGradeWeightage);
			
		}
		rs.close();
		pst.close();
		
		Map<String, String> hmBasicFitAmtMap = new HashMap<String, String>();
		pst = con.prepareStatement("select * from basic_fitment_details bfd, grades_details gd where bfd.GRADE_ID = gd.GRADE_ID and bfd.trail_status=1");
		rs = pst.executeQuery();
		while (rs.next()) {
			hmBasicFitAmtMap.put(rs.getString("grade_id"), rs.getString("amount"));
		}
		rs.close();
		pst.close();
		
		Map<String, Map<String, String>> hmDeferMap = new HashMap<String, Map<String, String>>();
		
		StringBuilder sbQuery = new StringBuilder();
		sbQuery.append("select * from emp_defer_basic_fitment where defer_status=true and fitment_month=? and fitment_year=?");
		sbQuery.append(" and emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " +
				"where epd.emp_per_id = eod.emp_id and org_id=? ");
		if(getStrWLocation()!=null && getStrWLocation().length>0){
            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getStrWLocation(), ",")+") ");
        }
		if(getDepartment()!=null && getDepartment().length>0){
            sbQuery.append(" and depart_id in ("+StringUtils.join(getDepartment(), ",")+") ");
        }
		sbQuery.append(")");
		pst = con.prepareStatement(sbQuery.toString());
		pst.setInt(1, uF.parseToInt(getStrMonth()));
		pst.setInt(2, uF.parseToInt(getStrYear()));
		pst.setInt(3, uF.parseToInt(getF_org()));		
		rs = pst.executeQuery();
		while (rs.next()) {
			Map<String, String> hmInner = new HashMap<String, String>();
			hmInner.put("EMP_ID", rs.getString("emp_id"));
			hmInner.put("DEFER_DATE", uF.getDateFormat(rs.getString("defer_date"), DBDATE, DATE_FORMAT));
			hmInner.put("GRADE_FROM", rs.getString("grade_from"));
			hmInner.put("GRADE_TO", rs.getString("grade_to"));
			hmInner.put("FITMENT_MONTH", rs.getString("fitment_month"));
			hmInner.put("FITMENT_YEAR", rs.getString("fitment_year"));
			hmInner.put("ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, DATE_FORMAT));
			hmInner.put("APPROVE_BY", rs.getString("approve_by"));
			hmInner.put("DEFER_STATUS", rs.getString("defer_status"));
			
			hmDeferMap.put(rs.getString("emp_id"), hmInner);
		}
		rs.close();
		pst.close();
		
		
		Map<String, Map<String, String>> hmDeferedEmp = new HashMap<String, Map<String, String>>();
		
		sbQuery = new StringBuilder();
		sbQuery.append("select * from emp_defer_basic_fitment where defer_status=true and EXTRACT(month FROM defer_date)=? and EXTRACT(year FROM defer_date)=?");
		sbQuery.append(" and emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " +
				"where epd.emp_per_id = eod.emp_id and org_id=? ");
		if(getStrWLocation()!=null && getStrWLocation().length>0){
            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getStrWLocation(), ",")+") ");
        }
		if(getDepartment()!=null && getDepartment().length>0){
            sbQuery.append(" and depart_id in ("+StringUtils.join(getDepartment(), ",")+") ");
        }
		sbQuery.append(")");
		pst = con.prepareStatement(sbQuery.toString());
		pst.setInt(1, uF.parseToInt(getStrMonth()));
		pst.setInt(2, uF.parseToInt(getStrYear()));
		pst.setInt(3, uF.parseToInt(getF_org()));		
		rs = pst.executeQuery();
		while (rs.next()) {
			Map<String, String> hmInner = new HashMap<String, String>();
			hmInner.put("EMP_ID", rs.getString("emp_id"));
			hmInner.put("DEFER_DATE", uF.getDateFormat(rs.getString("defer_date"), DBDATE, DATE_FORMAT));
			hmInner.put("GRADE_FROM", rs.getString("grade_from"));
			hmInner.put("GRADE_TO", rs.getString("grade_to"));
			hmInner.put("FITMENT_MONTH", rs.getString("fitment_month"));
			hmInner.put("FITMENT_MONTH_NAME", uF.getMonth(uF.parseToInt(rs.getString("FITMENT_MONTH"))));
			hmInner.put("FITMENT_YEAR", rs.getString("fitment_year"));
			hmInner.put("ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, DATE_FORMAT));
			hmInner.put("APPROVE_BY", rs.getString("approve_by"));
			hmInner.put("DEFER_STATUS", rs.getString("defer_status"));
			
			hmDeferedEmp.put(rs.getString("emp_id"), hmInner);
		}
		rs.close();
		pst.close();
		
		Map<String, Map<String, String>> hmEmpBasicFitAmtMap = new HashMap<String, Map<String, String>>();
		
		sbQuery = new StringBuilder();
		sbQuery.append("select * from emp_basic_fitment_details where fitment_month=? and fitment_year=?");
		sbQuery.append(" and emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " +
				"where epd.emp_per_id = eod.emp_id and org_id=? ");
		if(getStrWLocation()!=null && getStrWLocation().length>0){
            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getStrWLocation(), ",")+") ");
        }
		if(getDepartment()!=null && getDepartment().length>0){
            sbQuery.append(" and depart_id in ("+StringUtils.join(getDepartment(), ",")+") ");
        }
		sbQuery.append(")");
		pst = con.prepareStatement(sbQuery.toString());
		pst.setInt(1, uF.parseToInt(getStrMonth()));
		pst.setInt(2, uF.parseToInt(getStrYear()));
		pst.setInt(3, uF.parseToInt(getF_org()));		
		rs = pst.executeQuery();
		while (rs.next()) {
			Map<String, String> hmInner = hmEmpBasicFitAmtMap.get(rs.getString("EMP_ID"));
			if(hmInner==null) hmInner = new HashMap<String, String>();
			hmInner.put("EMP_FITMENT_ID", rs.getString("EMP_FITMENT_ID"));
			hmInner.put("EMP_ID", rs.getString("EMP_ID"));
			hmInner.put("GRADE_FROM", rs.getString("GRADE_FROM"));
			hmInner.put("GRADE_TO", rs.getString("GRADE_TO"));
			hmInner.put("FITMENT_MONTH", rs.getString("FITMENT_MONTH"));
			hmInner.put("FITMENT_MONTH_NAME", uF.getMonth(uF.parseToInt(rs.getString("FITMENT_MONTH"))));
			hmInner.put("FITMENT_YEAR", rs.getString("FITMENT_YEAR"));
			hmInner.put("ENTRY_DATE", uF.getDateFormat(rs.getString("ENTRY_DATE"), DBDATE, DATE_FORMAT));
			hmInner.put("APPROVE_BY", rs.getString("APPROVE_BY"));
			hmInner.put("APPROVE_STATUS", rs.getString("APPROVE_STATUS"));
			
			hmEmpBasicFitAmtMap.put(rs.getString("EMP_ID"), hmInner);
		}
		rs.close();
		pst.close();

		Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
		Map<String, String> hmEmpDesigMap = CF.getEmpDesigMapId(con);
		Map<String, String> hmLevelMap = CF.getLevelMap(con);
		
		sbQuery = new StringBuilder();
		sbQuery.append("select * from employee_personal_details epd,employee_official_details eod  where epd.emp_per_id = eod.emp_id " +
				"and (EXTRACT(month FROM JOINING_DATE)=? or emp_id in (select emp_id from emp_defer_basic_fitment where defer_status=true " +
				"and EXTRACT(month FROM defer_date)=?)) and eod.grade_id in (select grade_id from grades_details where is_fitment=true) " +
				" and eod.org_id=? and epd.IS_ALIVE=true ");
		if(getStrWLocation()!=null && getStrWLocation().length>0){
            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getStrWLocation(), ",")+") ");
        }
		if(getDepartment()!=null && getDepartment().length>0){
            sbQuery.append(" and depart_id in ("+StringUtils.join(getDepartment(), ",")+") ");
        }
		sbQuery.append(" order by epd.EMP_FNAME, epd.EMP_LNAME");
		pst = con.prepareStatement(sbQuery.toString());
		pst.setInt(1, uF.parseToInt(getStrMonth()));
		pst.setInt(2, uF.parseToInt(getStrMonth()));
		pst.setInt(3, uF.parseToInt(getF_org()));
		
		rs = pst.executeQuery();
		Map<String, Map<String, String>> hmFitmentMap = new HashMap<String, Map<String, String>>();
		int cnt = 0;
		while (rs.next()){
			String nextGrade = getNextGradeWeightage(rs.getString("grade_id"),hmEmpDesigMap.get(rs.getString("emp_id")),hmGradeWeightageMap.get(rs.getString("grade_id")),hmDesigGradeWeightageMap,uF);
//			if(nextGrade==null){
//				continue;
//			}
			if(nextGrade==null && hmEmpBasicFitAmtMap.get(rs.getString("emp_id"))==null){ 
				continue;
			}
			cnt++;
			Map<String, String> hmBasicFitment = hmFitmentMap.get(rs.getString("emp_id"));
			if(hmBasicFitment==null) hmBasicFitment = new LinkedHashMap<String, String>();
			
			hmBasicFitment.put("EMP_ID", rs.getString("emp_id"));
			hmBasicFitment.put("GRADE_ID", rs.getString("grade_id"));
			
			String levelName =uF.showData(hmLevelMap.get(hmEmpLevelMap.get(rs.getString("emp_id"))), "");
			
			StringBuilder sb = new StringBuilder(); 
			
			Map<String, String> hmInner = hmEmpBasicFitAmtMap.get(rs.getString("emp_id"));
			if(hmInner!=null && hmInner.get("APPROVE_STATUS").equals("1")){
				double dblCurrentbasic = getCurrentBasic(con,uF,rs.getString("emp_id"),hmInner.get("GRADE_FROM"));
				sb.append("<div style=\"float:left;\" id=\"myDiv_" + cnt + "\" ><strong>"+rs.getString("emp_fname")+" "+rs.getString("emp_lname")+"</strong>, <strong>"+levelName+"</strong>, " +
						"is due for an auto increment based on Fitment for upgrade from Grade <strong>"+uF.showData(hmGradeMap.get(hmInner.get("GRADE_FROM")), "")+"</strong> " +
						"(Rs. <strong>"+dblCurrentbasic+"/-)</strong> " +
						"to <strong>"+uF.showData(hmGradeMap.get(hmInner.get("GRADE_TO")), "")+"</strong> " +
						"(Rs. <strong>"+uF.showData(hmBasicFitAmtMap.get(hmInner.get("GRADE_TO")), "0")+"/-)</strong></div>");
				
				sb.append("<div style=\"float:left; padding-left: 15px;\" id=\"myDivM_" + cnt + "\" > ");
				/*sb.append("<img title=\"Approved\" src=\""+ request.getContextPath()+ "/images1/icons/approved.png\" border=\"0\">");*/
				sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>");
				sb.append("</div> ");
			} else if(hmInner!=null && hmInner.get("APPROVE_STATUS").equals("-1")){
				double dblCurrentbasic = getCurrentBasic(con,uF,rs.getString("emp_id"),hmInner.get("GRADE_FROM"));
				sb.append("<div style=\"float:left;\" id=\"myDiv_" + cnt + "\" ><strong>"+rs.getString("emp_fname")+" "+rs.getString("emp_lname")+"</strong>, <strong>"+levelName+"</strong>, " +
						"is due for an auto increment based on Fitment for upgrade from Grade <strong>"+uF.showData(hmGradeMap.get(hmInner.get("GRADE_FROM")), "")+"</strong> " +
						"(Rs. <strong>"+dblCurrentbasic+"/-)</strong> " +
						"to <strong>"+uF.showData(hmGradeMap.get(hmInner.get("GRADE_TO")), "")+"</strong> " +
						"(Rs. <strong>"+uF.showData(hmBasicFitAmtMap.get(hmInner.get("GRADE_TO")), "0")+"/-)</strong></div>");
				
				sb.append("<div style=\"float:left; padding-left: 15px;\" id=\"myDivM_" + cnt + "\" > ");
				sb.append("<img title=\"Denied\" src=\""+ request.getContextPath()+ "/images1/icons/denied.png\" border=\"0\">");				
				sb.append("</div> ");
			} else {
				double dblCurrentbasic = getCurrentBasic(con,uF,rs.getString("emp_id"),rs.getString("grade_id"));
				sb.append("<div style=\"float:left;\" id=\"myDiv_" + cnt + "\" ><strong>"+rs.getString("emp_fname")+" "+rs.getString("emp_lname")+"</strong>, <strong>"+levelName+"</strong>, " +
						"is due for an auto increment based on Fitment for upgrade from Grade <strong>"+uF.showData(hmGradeMap.get(rs.getString("grade_id")), "")+"</strong> " +
						"(Rs. <strong>"+dblCurrentbasic+"/-)</strong> " +
						"to <strong>"+uF.showData(hmGradeMap.get(nextGrade), "")+"</strong> " +
						"(Rs. <strong>"+uF.showData(hmBasicFitAmtMap.get(nextGrade), "0")+"/-)</strong></div>");
				
				if(hmDeferMap.containsKey(rs.getString("emp_id"))){
					Map<String, String> hmDefer = hmDeferMap.get(rs.getString("emp_id"));
					sb.append("<div style=\"float:left; padding-left: 15px;\" id=\"myDivM_" + cnt + "\" > ");
					/*sb.append("&nbsp;&nbsp;<strong>[Defer Date-&nbsp;"+uF.showData(hmDefer.get("DEFER_DATE"), "")+"]&nbsp;&nbsp;</strong><img src=\"images1/icons/pending.png\" title=\"Defer\" /> ");*/
					sb.append("&nbsp;&nbsp;<strong>[Defer Date-&nbsp;"+uF.showData(hmDefer.get("DEFER_DATE"), "")+"]&nbsp;&nbsp;</strong><i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\" title=\"Defer\"></i>");
					
					sb.append("</div> ");
				} else {
					String divId="myDivM_" + cnt;
					sb.append("<div style=\"float:left; padding-left: 15px;\" id=\"" + divId + "\" > ");
					if(hmDeferedEmp.containsKey(rs.getString("emp_id"))){
						Map<String, String> hmDeferInner = hmDeferedEmp.get(rs.getString("emp_id"));
						sb.append("&nbsp;&nbsp;<strong>[Defer From-&nbsp;"+uF.showData(hmDeferInner.get("FITMENT_MONTH_NAME"), "")+","+uF.showData(hmDeferInner.get("FITMENT_YEAR"), "")+"]&nbsp;&nbsp;</strong> ");
					}
					sb.append("&nbsp;&nbsp;<a href=\"javascript:void(0)\" onclick=\"if(confirm('Are you sure, you want to approve this request?')) " +
							"getSalaryStructure('"+rs.getString("emp_id")+"','"+rs.getString("grade_id")+"','"+nextGrade+"','"+getStrMonth()+"','"+getStrYear()+"','"+rs.getString("emp_fname")+" "+rs.getString("emp_lname")+"');\" >" +
									"<i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"  title=\"Approve\"></i></a> ");
					
					String denyUrl="ApproveBasicFitment.action?operation=D&emp_id="+rs.getString("emp_id")+"&grade_from="+rs.getString("grade_id")+"&grade_to="+nextGrade+"&fitmentMonth="+getStrMonth()+"&fitmentYear="+getStrYear();
					sb.append("<a href=\"javascript:void(0)\" onclick=\"if(confirm('Are you sure, you want to deny this request?')) getContent('"+divId+"', '"+denyUrl+"');\">" + " <i class=\"fa fa-times-circle cross\" aria-hidden=\"true\" title=\"Deny\"></i></a> ");
					
					sb.append("&nbsp;<a href=\"javascript:void(0)\" onclick=\"if(confirm('Are you sure, you want to defer this request?')) " +
							/*"getDefer('"+rs.getString("emp_id")+"','"+rs.getString("grade_id")+"','"+nextGrade+"','"+getStrMonth()+"','"+getStrYear()+"','"+rs.getString("emp_fname")+" "+rs.getString("emp_lname")+"');\"><img src=\"images1/icons/pending.png\" title=\"Defer\" /></a> ");*/
							"getDefer('"+rs.getString("emp_id")+"','"+rs.getString("grade_id")+"','"+nextGrade+"','"+getStrMonth()+"','"+getStrYear()+"','"+rs.getString("emp_fname")+" "+rs.getString("emp_lname")+"');\"><i class=\"fa fa-circle\" aria-hidden=\"true\" title=\"Defer\" style=\"color:#b71cc5\"></i></a> ");
					
					
					sb.append("</div> ");
				}
			}
			hmBasicFitment.put("EMP_DATA", sb.toString());
			
			
			hmFitmentMap.put(rs.getString("emp_id"),hmBasicFitment);
			
		}
		rs.close();
		pst.close();
		request.setAttribute("hmFitmentMap",hmFitmentMap);
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		if(rs!=null){
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			rs = null;
		}
		if(pst!=null){
			try {
				pst.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			pst = null;
		}
	}
}

private double getCurrentBasic(Connection con, UtilityFunctions uF, String strEmpId, String StrGradeId) {

	PreparedStatement pst = null;
	ResultSet rs = null;
	double dblBasicAmt = 0.0d;
	try {
		pst = con.prepareStatement("select amount from emp_salary_details where emp_id=? and salary_head_id in (select salary_head_id " +
				"from salary_details where grade_id=? and salary_head_id=?) and is_approved=true and effective_date in (select max(effective_date)" +
				" from emp_salary_details where emp_id=? and salary_head_id in (select salary_head_id from salary_details where grade_id=? " +
				"and salary_head_id=?) and grade_id=?) and grade_id=? ");
		pst.setInt(1, uF.parseToInt(strEmpId));
		pst.setInt(2, uF.parseToInt(StrGradeId));
		pst.setInt(3, BASIC);
		pst.setInt(4, uF.parseToInt(strEmpId));
		pst.setInt(5, uF.parseToInt(StrGradeId));
		pst.setInt(6, BASIC);
		pst.setInt(7, uF.parseToInt(StrGradeId));
		pst.setInt(8, uF.parseToInt(StrGradeId));
		rs = pst.executeQuery();
		while(rs.next()){
			dblBasicAmt = uF.parseToDouble(rs.getString("amount"));
		}
		rs.close();
		pst.close();
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		if(rs!=null){
			try {
				rs.close();
				rs = null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
		if(pst!=null){
			try {
				pst.close();
				pst = null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
	}
	return dblBasicAmt;
}

private String getNextGradeWeightage(String gradeId, String desigId, String weightage, Map<String, Map<String, String>> hmLevelGradeWeightageMap,UtilityFunctions uF) {
	
	Map<String, String> hmGradeWeightage = hmLevelGradeWeightageMap.get(desigId);
	if(hmGradeWeightage!=null && hmGradeWeightage.size()>0){
		int nWeightage = uF.parseToInt(weightage);
		Iterator<String> it = hmGradeWeightage.keySet().iterator(); 
		while(it.hasNext()){
			String strGrade = it.next();
			int newWeightage = uF.parseToInt(hmGradeWeightage.get(strGrade));
			
			if(newWeightage < nWeightage){
				return strGrade;
			}
		}
	}
	return null;
}

public String loadAttendanceRegister(UtilityFunctions uF) {
		monthList = new FillMonth().fillMonth();
		yearList = new FillYears().fillFutureYears(uF.getCurrentDate(CF.getStrTimeZone()), 2, 2);
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		}else{
			organisationList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		
		getSelectedFilter(uF);
		
		return LOAD;
	}

private void getSelectedFilter(UtilityFunctions uF) {
	Map<String,String> hmFilter=new HashMap<String, String>();
	List<String> alFilter = new ArrayList<String>();
	
	alFilter.add("ORGANISATION");
	if(getF_org()!=null)  {
		String strOrg="";
		int k=0;
		for(int i=0;organisationList!=null && i<organisationList.size();i++){
			if(getF_org().equals(organisationList.get(i).getOrgId())) {
				if(k==0) {
					strOrg=organisationList.get(i).getOrgName();
				} else {
					strOrg+=", "+organisationList.get(i).getOrgName();
				}
				k++;
			}
		}
		if(strOrg!=null && !strOrg.equals("")) {
			hmFilter.put("ORGANISATION", strOrg);
		} else {
			hmFilter.put("ORGANISATION", "All Organisation");
		}
		
	} else {
		hmFilter.put("ORGANISATION", "All Organisation");
	}
	
	alFilter.add("LOCATION");
	if(getStrWLocation()!=null) {
		String strLocation="";
		int k=0;
		for(int i=0;wLocationList!=null && i<wLocationList.size();i++) {
			for(int j=0;j<getStrWLocation().length;j++) {
				if(getStrWLocation()[j].equals(wLocationList.get(i).getwLocationId())) {
					if(k==0) {
						strLocation=wLocationList.get(i).getwLocationName();
					} else {
						strLocation+=", "+wLocationList.get(i).getwLocationName();
					}
					k++;
				}
			}
		}
		if(strLocation!=null && !strLocation.equals("")) {
			hmFilter.put("LOCATION", strLocation);
		} else {
			hmFilter.put("LOCATION", "All Locations");
		}
	} else {
		hmFilter.put("LOCATION", "All Locations");
	}
	
	alFilter.add("DEPARTMENT");
	if(getDepartment()!=null) {
		String strDepartment="";
		int k=0;
		for(int i=0;departmentList!=null && i<departmentList.size();i++) {
			for(int j=0;j<getDepartment().length;j++) {
				if(getDepartment()[j].equals(departmentList.get(i).getDeptId())) {
					if(k==0) {
						strDepartment=departmentList.get(i).getDeptName();
					} else {
						strDepartment+=", "+departmentList.get(i).getDeptName();
					}
					k++;
				}
			}
		}
		if(strDepartment!=null && !strDepartment.equals("")) {
			hmFilter.put("DEPARTMENT", strDepartment);
		} else {
			hmFilter.put("DEPARTMENT", "All Departments");
		}
	} else {
		hmFilter.put("DEPARTMENT", "All Departments");
	}
	
	String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
	request.setAttribute("selectedFilter", selectedFilter);
}

	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public String[] getStrWLocation() {
		return strWLocation;
	}

	public void setStrWLocation(String[] strWLocation) {
		this.strWLocation = strWLocation;
	}

	public String[] getDepartment() {
		return department;
	}

	public void setDepartment(String[] department) {
		this.department = department;
	}

	public String getStrMonth() {
		return strMonth;
	}

	public void setStrMonth(String strMonth) {
		this.strMonth = strMonth;
	}

	public String getStrYear() {
		return strYear;
	}

	public void setStrYear(String strYear) {
		this.strYear = strYear;
	}

	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}

	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}

	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}

	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}

	public List<FillDepartment> getDepartmentList() {
		return departmentList;
	}

	public void setDepartmentList(List<FillDepartment> departmentList) {
		this.departmentList = departmentList;
	}

	public List<FillMonth> getMonthList() {
		return monthList;
	}

	public void setMonthList(List<FillMonth> monthList) {
		this.monthList = monthList;
	}

	public List<FillYears> getYearList() {
		return yearList;
	}

	public void setYearList(List<FillYears> yearList) {
		this.yearList = yearList;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}
	
}
