package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class SetEmpNewRateAndCost extends ActionSupport implements IStatements, ServletRequestAware {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	String proID;
	String empID;
	String type;
	String billingType;
	String submit;
	 
	public String execute() {
		
		HttpSession session = request.getSession();
		
		CommonFunctions CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		UtilityFunctions uF = new UtilityFunctions();
	
			getData(CF, uF);
			return LOAD;
	}
	
	

	
	private void getData(CommonFunctions CF, UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			
			con = db.makeConnection(con);
			StringBuilder allData = new StringBuilder();
			pst = con.prepareStatement("select * from project_emp_details where pro_id=? and emp_id=?");
			pst.setInt(1, uF.parseToInt(getProID()));
			pst.setInt(2, uF.parseToInt(getEmpID()));
			rs = pst.executeQuery();
//			System.out.println("pst ===>> " + pst);
			String rateCostAmt = "";
			while (rs.next()) {
				if(billingType != null && billingType.equals("H") && type != null && type.equals("rate")) {
					rateCostAmt = rs.getString("emp_rate_per_hour");
					allData.append(rs.getString("emp_rate_per_hour"));
					allData.append("::::");
				}else if(billingType != null && billingType.equals("D") && type != null && type.equals("rate")) {
					rateCostAmt = rs.getString("emp_rate_per_day");
					allData.append(rs.getString("emp_rate_per_day"));
					allData.append("::::");
				}
				if(billingType != null && billingType.equals("H") && type != null && type.equals("cost")) {
					rateCostAmt = rs.getString("emp_actual_rate_per_hour");
					allData.append(rs.getString("emp_actual_rate_per_hour"));
					allData.append("::::");
				} else if(billingType != null && billingType.equals("H") && type != null && type.equals("cost")) {
					rateCostAmt = rs.getString("emp_actual_rate_per_day");
					allData.append(rs.getString("emp_actual_rate_per_day"));
					allData.append("::::");
				}
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select * from variable_cost where pro_id=? and emp_id=?");
			pst.setInt(1, uF.parseToInt(getProID()));
			pst.setInt(2, uF.parseToInt(getEmpID()));
			rs = pst.executeQuery();
//			System.out.println("pst ===>> " + pst);
			String extraAmt = "";
			int cnt = 0;
			while (rs.next()) {
				
				if(rs.getString("amount_type")!= null && rs.getString("amount_type").equals("rate")) {
					cnt++;
					allData.append(rs.getString("variable_name"));
					allData.append("::::");
					extraAmt = rs.getString("variable_cost");
					allData.append(rs.getString("variable_cost"));
					allData.append("::::");
				} else if(rs.getString("amount_type")!= null && rs.getString("amount_type").equals("cost")) {
					cnt++;
					allData.append(rs.getString("variable_name"));
					allData.append("::::");
					extraAmt = rs.getString("variable_cost");
					allData.append(rs.getString("variable_cost"));
					allData.append("::::");
				}
			}
			rs.close();
			pst.close();
			
			if(cnt == 0) {
				allData.append("");
				allData.append("::::");
				allData.append("");
				allData.append("::::");
			}
			
			double totRateCostAmt = uF.parseToDouble(rateCostAmt) + uF.parseToDouble(extraAmt);
			
			allData.append(totRateCostAmt);
			
//			System.out.println("allData ===>> " + allData);
			request.setAttribute("allData", allData.toString());
			
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

	public String getProID() {
		return proID;
	}

	public void setProID(String proID) {
		this.proID = proID;
	}

	public String getEmpID() {
		return empID;
	}

	public void setEmpID(String empID) {
		this.empID = empID;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getBillingType() {
		return billingType;
	}

	public void setBillingType(String billingType) {
		this.billingType = billingType;
	}

	public String getSubmit() {
		return submit;
	}

	public void setSubmit(String submit) {
		this.submit = submit;
	}

}

