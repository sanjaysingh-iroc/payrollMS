package com.konnect.jpms.salary;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.payroll.ApprovePayroll;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillSalaryHeads;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class SalaryPreview extends ActionSupport implements ServletRequestAware, IStatements{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5947893602821384559L;
	
	String headName;
	String earningOrDeduction;
	String headAmountType;
	String salarySubHead;
	String curr_short;
	String headAmount;
	String removeId; 
	String operation; 
	String salaryHeadId;
	String salaryId;
	String level;
	
	boolean isSave=false;
	HttpSession session;
	CommonFunctions CF; 
	String empId;
	
	List<List<String>> al = new ArrayList<List<String>>();
	
	public String execute()	{

		session = request.getSession();if(session==null)return LOGIN;
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		
		String strEmpId = (String)request.getParameter("emp_id");
		
		
		viewSalaryDetails(strEmpId);
		
		return SUCCESS;
			
	}

	public void viewSalaryDetails(String strEmpId) {
		
//		System.out.println("======+>"+strEmpId);
		Connection con=null;
		Database db=new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			UtilityFunctions uF = new UtilityFunctions();
			
			
//			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
//			int nMinDay = cal.getActualMinimum(Calendar.DATE);
//			int nMaxDay = cal.getActualMaximum(Calendar.DATE);
//			
//			
//			cal.set(Calendar.DATE, nMinDay);
//			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "MM"))-1);
//			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "yyyy")));
//			
//			
//			String strD1 = ((cal.get(Calendar.DATE)<10)?"0"+cal.get(Calendar.DATE):cal.get(Calendar.DATE))
//					+"/"+((cal.get(Calendar.MONTH)+1<10)?"0"+(cal.get(Calendar.MONTH)+1):cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.YEAR);
//			
//			cal.set(Calendar.DATE, nMaxDay);
//			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "MM"))-1);
//			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "yyyy")));
//			
//			
//			String strD2 = ((cal.get(Calendar.DATE)<10)?"0"+cal.get(Calendar.DATE):cal.get(Calendar.DATE))
//					+"/"+((cal.get(Calendar.MONTH)+1<10)?"0"+(cal.get(Calendar.MONTH)+1):cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.YEAR);
			
			
			
//			System.out.println("strD1===="+strD1);
//			System.out.println("strD2===="+strD2);
//			uF.getDateFormatUtil( uF.getCurrentDate(CF.getStrTimeZone()), DATE_FORMAT);
			
			con=db.makeConnection(con);
			
			
			String orgId=null;
			pst = con.prepareStatement("select paycycle_duration,org_id from employee_official_details where emp_id=?");
			pst.setInt(1, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			while (rs.next()) {
//				paycycleType = rs.getString("paycycle_duration");
				orgId = rs.getString("org_id");
			}
			rs.close();
			pst.close();
			
			
			String[] pay =CF.getPayCycleFromDate(con, uF.getDateFormatUtil( uF.getCurrentDate(CF.getStrTimeZone()), DATE_FORMAT), CF.getStrTimeZone(), CF, orgId) ;
			
			ApprovePayroll objApprovePayroll = new ApprovePayroll();
			objApprovePayroll.setServletRequest(request);
			objApprovePayroll.session=session;
			objApprovePayroll.CF=CF;
			objApprovePayroll.setF_org(orgId);
			objApprovePayroll.viewClockEntriesForPayrollApproval(CF, strEmpId, pay[0], pay[1],pay[2]);
			
			
			
			
			
			
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
	
}
