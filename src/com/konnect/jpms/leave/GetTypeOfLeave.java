package com.konnect.jpms.leave;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillLeaveType;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetTypeOfLeave extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;
	CommonFunctions CF=null;
	
	String strEmpID; 
	List<FillLeaveType> leaveTypeList;
	
	public String execute() {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null){
			return LOGIN;
		}
		UtilityFunctions uF=new UtilityFunctions();
		
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
		
			con = db.makeConnection(con);
		
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			Map<String, String> hmEmpGenderMap =CF.getEmpGenderMap(con);
			String gender=hmEmpGenderMap.get(getStrEmpID());
			Map<String, String> hmEmpWlocationMap =CF.getEmpWlocationMap(con);
			String empOrgId=getEmpOrgID(con, uF);
			
			boolean flag = getMaternityFrequency(con, uF.parseToInt(hmEmpLevelMap.get(getStrEmpID())), uF.parseToInt(getStrEmpID()),uF.parseToInt(empOrgId), uF.parseToInt(hmEmpWlocationMap.get(getStrEmpID())));
			leaveTypeList = new FillLeaveType(request).fillLeaveWithoutCompensetary(gender!=null && gender.equalsIgnoreCase("M")?true:false,flag,uF.parseToInt(hmEmpLevelMap.get(getStrEmpID())), uF.parseToInt(getStrEmpID()),uF.parseToInt(empOrgId), uF.parseToInt(hmEmpWlocationMap.get(getStrEmpID())),uF.getCurrentDate(CF.getStrTimeZone()));
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeConnection(con);
		}
		
		return SUCCESS;
	}

	private String getEmpOrgID(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		String orgId=null;
		try {
			
			pst = con.prepareStatement("select org_id from employee_official_details where emp_id=? ");
			pst.setInt(1, uF.parseToInt(getStrEmpID()));
			rs = pst.executeQuery();
			while(rs.next()){
				orgId=rs.getString("org_id");
			}
			rs.close();
			pst.close();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	
		return orgId;
	}

	public boolean getMaternityFrequency(Connection con, int nLevelId, int nEmpId,int nOrgId, int nLocationId){

		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();

		try {
			
			int totalMaternityTaken=0;
			pst = con.prepareStatement("select count(*) as leavecnt from emp_leave_entry where emp_id=? and leave_type_id " +
					"in(select leave_type_id from leave_type where is_maternity=true) group by emp_id ");
			pst.setInt(1, nEmpId);
			rs = pst.executeQuery();
			while(rs.next()){
				totalMaternityTaken=uF.parseToInt(rs.getString("leavecnt"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("SELECT * FROM leave_type lt, emp_leave_type elt where is_maternity=true and lt.leave_type_id = elt.leave_type_id " +
					" and level_id=? and lt.org_id=? and wlocation_id=? and effective_date =(select max(effective_date) from emp_leave_type " +
					" where level_id = ?  and lt.org_id=? and wlocation_id=?  and is_compensatory = false and is_work_from_home=false ) " +
					"and lt.is_compensatory = false and lt.is_work_from_home=false");
//			pst.setBoolean(1, true);
//			pst = con.prepareStatement(query.toString());
			pst.setInt(1, nLevelId);
			pst.setInt(2, nOrgId);
			pst.setInt(3, nLocationId);
			pst.setInt(4, nLevelId);
			pst.setInt(5, nOrgId);
			pst.setInt(6, nLocationId); 
//			rs = pst.executeQuery();
			rs = pst.executeQuery();
			
			StringBuilder sbDocumentCondition = new StringBuilder();
			int count = 0;
			while(rs.next()){
				count+=uF.parseToInt(rs.getString("maternity_type_frequency"));
			}
			rs.close();
			pst.close();
			
			if(count<=totalMaternityTaken){
				return false;
			}

			request.setAttribute("idDocCondition", sbDocumentCondition.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		}  finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}

	


	public String getStrEmpID() {
		return strEmpID;
	}
	public void setStrEmpID(String strEmpID) {
		this.strEmpID = strEmpID;
	}
	public List<FillLeaveType> getLeaveTypeList() {
		return leaveTypeList;
	}
	public void setLeaveTypeList(List<FillLeaveType> leaveTypeList) {
		this.leaveTypeList = leaveTypeList;
	}



	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
		
	}

}
