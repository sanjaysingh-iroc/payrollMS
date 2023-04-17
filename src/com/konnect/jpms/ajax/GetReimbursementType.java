package com.konnect.jpms.ajax;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillRimbursementType;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetReimbursementType extends ActionSupport implements IStatements, ServletRequestAware {

	private static final long serialVersionUID = 6483180990145887248L;


	HttpSession session;
	
	String paycycle;
	List<FillRimbursementType> reimbursementTypeList;
	String empId;
	private CommonFunctions CF;
	
	String reimbursementType;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		
		getEmpProjectDetails();
		
		return SUCCESS;		
	}

	private void getEmpProjectDetails() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs =null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF=new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			String orgId = CF.getEmpOrgId(con, uF, getEmpId());
			
			String[] arrDates = null;
			if (getPaycycle() != null) {
				arrDates = getPaycycle().split("-");
				setPaycycle(arrDates[0] + "-" + arrDates[1] + "-" + arrDates[2]);
			} else {
//				arrDates = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF,request);
				arrDates = CF.getCurrentPayCycleByOrg(con, CF.getStrTimeZone(), CF, orgId);
				setPaycycle(arrDates[0] + "-" + arrDates[1] + "-" + arrDates[2]);
			} 
			
			pst = con.prepareStatement("select * from activity_info where resource_ids like '%,"+getEmpId()+",%' and pro_id in (select pro_id from projectmntnc where " +
			"(start_date >= ? and deadline <= ?) or (start_date <= ? and deadline >= ?) or (start_date >= ? and start_date <= ?))");
			pst.setDate(1, uF.getDateFormat(arrDates[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(arrDates[1], DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(arrDates[0], DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(arrDates[0], DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(arrDates[0], DATE_FORMAT));
			pst.setDate(6, uF.getDateFormat(arrDates[1], DATE_FORMAT));
	//		System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			boolean isProject=false;
			while(rs.next()) {
				isProject=true;
			}
			rs.close();
			pst.close();
		
			reimbursementTypeList = new FillRimbursementType().fillRimbursementType1();
			int nReimbursementTypeList = reimbursementTypeList!=null ? reimbursementTypeList.size() : 0;
			StringBuilder sb=new StringBuilder();
			for(int i = 0; i < nReimbursementTypeList; i++){
				String strChecked = "";
				if(!isProject && reimbursementTypeList.get(i).getTypeId().trim().equals("L")){
					strChecked = "checked=\"checked\"";
				} else {
					if(isProject && reimbursementTypeList.get(i).getTypeId().trim().equals("P")){
						strChecked = "checked=\"checked\"";
					}
				}
				sb.append("<input type=\"radio\" id=\"reimbursementType\" name=\"reimbursementType\" onchange=\"showType(this.value);\" value=\""+reimbursementTypeList.get(i).getTypeId()+"\" "+strChecked+"> "+reimbursementTypeList.get(i).getTypeName());
			}
			
			request.setAttribute("STATUS_MSG",sb.toString()+"::::L");
					
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public String getPaycycle() {
		return paycycle;
	}

	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
	}

	public List<FillRimbursementType> getReimbursementTypeList() {
		return reimbursementTypeList;
	}

	public void setReimbursementTypeList(
			List<FillRimbursementType> reimbursementTypeList) {
		this.reimbursementTypeList = reimbursementTypeList;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}
	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
	}

	public String getReimbursementType() {
		return reimbursementType;
	}

	public void setReimbursementType(String reimbursementType) {
		this.reimbursementType = reimbursementType;
	}

}
