package com.konnect.jpms.master;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillSalaryHeads;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetSalaryHead extends ActionSupport implements ServletRequestAware{

	
	
	private static final long serialVersionUID = 1L;

	String strLevel;
	List<FillSalaryHeads> salaryHeadList;
	
	public String execute() {
		
		if(getStrLevel()!=null && !getStrLevel().equals("")){
			salaryHeadList = fillSalaryHeads();
		}
		
		return SUCCESS;
		
	}



	private List<FillSalaryHeads> fillSalaryHeads() {
		
		List<FillSalaryHeads> al = new ArrayList<FillSalaryHeads>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
 
		
		try {

			con = db.makeConnection(con);
			
			pst = con.prepareStatement("SELECT * FROM salary_details where level_id = ? order by salary_head_name");
			pst.setInt(1, uF.parseToInt(getStrLevel()));
//			System.out.println("pst======>"+pst);
			rs1 = pst.executeQuery();
			while (rs1.next()) { 
				al.add(new FillSalaryHeads(rs1.getString("salary_head_id"), rs1.getString("salary_head_name")));
			}
			rs1.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs1);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	
		return al;
	}



	public String getStrLevel() {
		return strLevel;
	}

	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}



	public List<FillSalaryHeads> getSalaryHeadList() {
		return salaryHeadList;
	}



	public void setSalaryHeadList(List<FillSalaryHeads> salaryHeadList) {
		this.salaryHeadList = salaryHeadList;
	}



	HttpServletRequest request;
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
		
	}	
}
