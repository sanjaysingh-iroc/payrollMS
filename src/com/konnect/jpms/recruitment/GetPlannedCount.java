package com.konnect.jpms.recruitment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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

public class GetPlannedCount extends ActionSupport implements ServletRequestAware,IStatements{

	CommonFunctions CF;
	HttpSession session;
  String designation;
  String date;

	private static final long serialVersionUID = 1L;

	public String execute() {

//		request.setAttribute(PAGE, "/jsp/recruitment/RequirementRequest.jsp");
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;

		prepareOutput();
		
		getExistCount();
	
		return LOAD;
	}


	private void getExistCount() {
	
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
		
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select count(*)as count,dd.designation_id from grades_details gd, designation_details dd, level_details ld, " +
					"employee_official_details eod where dd.designation_id = gd.designation_id and ld.level_id = dd.level_id " +
					"and gd.grade_id = eod.grade_id and eod.emp_id in (select emp_per_id from employee_personal_details where is_alive = true) ");
			sbQuery.append(" and dd.designation_id = ? group by dd.designation_id order by dd.designation_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getDesignation()));
			rst = pst.executeQuery();
			System.out.println("pst ===> " + pst);
			int strExistCount = 0;
			while (rst.next()) {
				strExistCount = rst.getInt("count");				
			}
			rst.close();
			pst.close();
			
//			System.out.println("strExistCount====>"+strExistCount);
			request.setAttribute("strExistCount", strExistCount+"");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

		
	}


	private void prepareOutput() {
	
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
		
		int month=0;
		int year=0;
		String output="0";
		if(getDate()==null || getDate().equals(""))
		{
			Calendar cal=Calendar.getInstance();
			year=cal.get(Calendar.YEAR);
			month=cal.get(Calendar.MONTH)+1;
		}else{
			
			year=uF.parseToInt(uF.getDateFormat(getDate(), DATE_FORMAT, "yyyy"));
			month= uF.parseToInt(uF.getDateFormat(getDate(), DATE_FORMAT, "MM"));
		}
	
		if(getDesignation()!=null && !getDesignation().equals("") && year!=0 && month!=0){
			
		/*pst=con.prepareStatement("select recruitment_id,resource_requirement from recruitment_details rd," +
				"resource_planner_details rpd where rpd.designation_id=rd.designation_id and" +
				" ryear="+year+" and rmonth="+month+"");*/
			pst=con.prepareStatement("select resource_requirement from resource_planner_details where designation_id="+getDesignation()+" and" +
					" ryear="+year+" and rmonth="+month+"");
			rst=pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
//			System.out.println("pst ========== >> "+pst);
			while(rst.next()){
				output = rst.getString("resource_requirement");
			}
		}
//		System.out.println("output ========== >> "+output);
		request.setAttribute("Output", output);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	HttpServletRequest request;
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
		
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
	
	

}
