package com.konnect.jpms.salary;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
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

public class EmpSalaryHistory extends ActionSupport implements ServletRequestAware, IStatements{

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
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		
		try {
			UtilityFunctions uF = new UtilityFunctions();
			
			List<String> alEmpSalaryDetailsEarning = new ArrayList<String>();
			List<String> alEmpSalaryDetailsDeduction = new ArrayList<String>();
			
			List<String> alEmpSalaryMonths = new ArrayList<String>();
			
			con = db.makeConnection(con);
			
			
			
			pst = con.prepareStatement("select * from payroll_generation where emp_id = ? order by entry_date desc,  generation_id");
			pst.setInt(1, uF.parseToInt(strEmpId));
			rst = pst.executeQuery();
			
			String strEntryDateNew = null;
			String strEntryDateOld = null;
			Map<String, Map<String, String>> hmSalaryMap = new HashMap<String, Map<String, String>>();
			Map<String, String> hmInnerSalaryMap = new HashMap<String, String>();
			
			double dblNet = 0;
			while(rst.next()){
				strEntryDateNew = rst.getString("month")+"-"+rst.getString("year");
				
				if(strEntryDateNew!=null && !strEntryDateNew.equalsIgnoreCase(strEntryDateOld)){
					hmInnerSalaryMap = new HashMap<String, String>();
					dblNet = 0;
				}
				
				
				
				if(rst.getString("earning_deduction")!=null && rst.getString("earning_deduction").equalsIgnoreCase("E") && !alEmpSalaryDetailsEarning.contains(rst.getString("salary_head_id"))){
					alEmpSalaryDetailsEarning.add(rst.getString("salary_head_id"));
					
					dblNet += rst.getDouble("amount");
				}
				if(rst.getString("earning_deduction")!=null && rst.getString("earning_deduction").equalsIgnoreCase("D") && !alEmpSalaryDetailsDeduction.contains(rst.getString("salary_head_id"))){
					alEmpSalaryDetailsDeduction.add(rst.getString("salary_head_id"));
					dblNet -= rst.getDouble("amount");
				}	
				
				hmInnerSalaryMap.put(rst.getString("salary_head_id"), uF.formatIntoOneDecimal(uF.parseToDouble(rst.getString("amount"))));
				hmInnerSalaryMap.put("GROSS", uF.formatIntoOneDecimal(dblNet));
				
				hmSalaryMap.put(strEntryDateNew, hmInnerSalaryMap);
				if(!alEmpSalaryMonths.contains(strEntryDateNew)){
					alEmpSalaryMonths.add(strEntryDateNew);
				}
				
				strEntryDateOld = strEntryDateNew;  
			}
			rst.close();
			pst.close();
			
//			System.out.println("alEmpSalaryDetailsEarning"+alEmpSalaryDetailsEarning.toString());
//			System.out.println("alEmpSalaryDetailsDeduction"+alEmpSalaryDetailsDeduction.toString());
//			System.out.println("alEmpSalaryMonths"+alEmpSalaryMonths.toString());
//			System.out.println("hmSalaryMap"+hmSalaryMap);
			
			Map<String, String> hmSalaryDetails = CF.getSalaryHeadsMap(con);
			
			request.setAttribute("alEmpSalaryDetailsEarning", alEmpSalaryDetailsEarning);
			request.setAttribute("alEmpSalaryDetailsDeduction", alEmpSalaryDetailsDeduction);
			request.setAttribute("alEmpSalaryMonths", alEmpSalaryMonths);
			request.setAttribute("hmSalaryDetails", hmSalaryDetails);
			request.setAttribute("hmSalaryMap", hmSalaryMap);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
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
	
}
