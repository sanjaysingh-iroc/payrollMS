package com.konnect.jpms.leave;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
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

public class TravelAdvanceEligibility extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	
	String strUserType;
	String strSessionEmpId;
	String destinations;
	
	public String execute() throws Exception {
		request.setAttribute(PAGE, PTravelAdvanceEligibilityReport);
		request.setAttribute(TITLE, "Travel Advance Eligibility");
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		String strEdit = (String)request.getParameter("operation");
		
//		System.out.println("pst== getParameter =>"+request.getParameter("id"));
//		System.out.println("pst== getAttribute =>"+request.getAttribute("id"));
		
		if(strEdit!=null && strEdit.equalsIgnoreCase("U")){
			return updateAdvancesEligibility();
		}
		
		viewAdvancesEligibility();
		
		return loadAdvanceEntryEligibility();
	}
	public String loadAdvanceEntryEligibility() {
		return LOAD;
	}
	
	
	
	public String viewAdvancesEligibility() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			
			pst = con.prepareStatement("select * from employee_personal_details epd left join travel_advance_eligibility tae on tae.emp_id = epd.emp_per_id where is_alive is true order by emp_fname, emp_lname");
			rs = pst.executeQuery();

			List<List<String>> alreportList = new ArrayList<List<String>>();
			List<String> alInner = new ArrayList<String>();
			
			int count=0;
			while(rs.next()){
				alInner = new ArrayList<String>();
				
				alInner.add(rs.getString("emp_per_id"));
				alInner.add(rs.getString("empcode"));
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
			
				
				alInner.add(rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname"));
				alInner.add(uF.formatIntoComma(uF.parseToDouble(rs.getString("eligibility_amount"))));
				alInner.add(uF.showData(hmEmpName.get(rs.getString("approved_by")), ""));
				alInner.add(uF.showData(uF.getDateFormat(rs.getString("approved_date"), DBDATE, CF.getStrReportDateFormat()), ""));
				
				if(uF.parseToBoolean(rs.getString("is_eligible"))){
					/*alInner.add("<div id=\"myDiv_"+count+"\"><a href=\"javascript:void(0)\" onclick=\"confirm('Are you sure you want to deny this?')?getContent('myDiv_"+count+"','UpdateEmpTravelEligibilityApproval.action?status=0&emp_id="+rs.getString("emp_per_id")+"&count="+count+"'):''\"><img src=\"images1/tick.png\" ></div>");*/
					alInner.add("<div id=\"myDiv_"+count+"\"><a href=\"javascript:void(0)\" onclick=\"confirm('Are you sure you want to deny this?')?getContent('myDiv_"+count+"','UpdateEmpTravelEligibilityApproval.action?status=0&emp_id="+rs.getString("emp_per_id")+"&count="+count+"'):''\"><i class=\"fa fa-check checknew\" aria-hidden=\"true\"></i></div>");
					
				}else{
					 /*alInner.add("<div id=\"myDiv_"+count+"\"><a href=\"javascript:void(0)\" onclick=\"confirm('Are you sure you want to approve this?')?getContent('myDiv_"+count+"','UpdateEmpTravelEligibilityApproval.action?status=1&emp_id="+rs.getString("emp_per_id")+"&count="+count+"'):''\"><img src=\"images1/cross.png\" ></div>");*/
					alInner.add("<div id=\"myDiv_"+count+"\"><a href=\"javascript:void(0)\" onclick=\"confirm('Are you sure you want to approve this?')?getContent('myDiv_"+count+"','UpdateEmpTravelEligibilityApproval.action?status=1&emp_id="+rs.getString("emp_per_id")+"&count="+count+"'):''\"><i class=\"fa fa-times cross\" aria-hidden=\"true\"></i></div>");
					
				}
				
				count++;
				alreportList.add(alInner);
			}
			rs.close();
			pst.close();

			
			request.setAttribute("alreportList", alreportList);
			
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}
	
	public String updateAdvancesEligibility() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("update travel_advance_eligibility set eligibility_amount=? where emp_id=?");
			pst.setDouble(1, uF.parseToDouble((String)request.getParameter("value")));
			pst.setInt(2, uF.parseToInt((String)request.getParameter("id")));
			int x = pst.executeUpdate();
			pst.close();
			
			
			System.out.println("pst== U =>"+pst);
			
			if(x==0){
				pst = con.prepareStatement("insert into travel_advance_eligibility (emp_id, eligibility_amount) values (?,?)");
				pst.setInt(1, uF.parseToInt((String)request.getParameter("id")));
				pst.setDouble(2, uF.parseToDouble((String)request.getParameter("value")));
				pst.execute();
				pst.close();
				
				System.out.println("pst== I =>"+pst);
			}
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return UPDATE;
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
}