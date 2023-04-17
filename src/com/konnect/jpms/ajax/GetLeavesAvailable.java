package com.konnect.jpms.ajax;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillLeaveType;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetLeavesAvailable extends ActionSupport implements ServletRequestAware, IStatements {
	HttpSession session;
	CommonFunctions CF;
	
	private String orgId;
	private String wlocationId;
	private String levelId;
	private String empStatus;
	
	private String[] probationLeaves;
	private List<FillLeaveType> leaveTypeList;
	private List<FillLeaveType> leaveTypeList1;
	private static final long serialVersionUID = 1L;
	
	private String leavesValidReqOpt;
	private String empId;
	private String strLeaves;
	private List<String> leavesValue = new ArrayList<String>();
	String strNoHeaderLabel;
	
	public String execute() {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		UtilityFunctions uF = new UtilityFunctions();		
		
		if(uF.parseToInt(getOrgId()) > 0 && uF.parseToInt(getWlocationId()) > 0 && uF.parseToInt(getLevelId()) > 0 && uF.parseToInt(getEmpStatus()) > 0){
			leaveTypeList = new FillLeaveType(request).fillLeave(uF.parseToInt(getOrgId()),uF.parseToInt(getWlocationId()),uF.parseToInt(getLevelId()),getEmpStatus(),uF.parseToInt(getEmpId()), uF.parseToBoolean(getStrNoHeaderLabel()));
		} else{
			leaveTypeList = new ArrayList<FillLeaveType>();
		}
		if(uF.parseToInt(getEmpId()) > 0){
			getEmpLeaves(uF);
		}
		
		
		return LOAD;
	}

	
	private void getEmpLeaves(UtilityFunctions uF) {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst =null;
		ResultSet rs = null;
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(selectProbationPolicy);
			pst.setInt(1, uF.parseToInt(getEmpId()));
			rs = pst.executeQuery();
			
			while(rs.next()) {
				String leaveTypes = rs.getString("leaves_types_allowed");
				if(leaveTypes!=null && !leaveTypes.equals("") && !leaveTypes.equalsIgnoreCase("null")) {
					setProbationLeaves(rs.getString("leaves_types_allowed").split(","));
				}
			}
			rs.close();
			pst.close();
					
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}


	public String getLeavesValidReqOpt() {
		return leavesValidReqOpt;
	}

	public void setLeavesValidReqOpt(String leavesValidReqOpt) {
		this.leavesValidReqOpt = leavesValidReqOpt;
	}

	public String getOrgId() {
		return orgId;
	}
	
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	
	public String getWlocationId() {
		return wlocationId;
	}
	
	public void setWlocationId(String wlocationId) {
		this.wlocationId = wlocationId;
	}
	
	public String getLevelId() {
		return levelId;
	}
	
	public void setLevelId(String levelId) {
		this.levelId = levelId;
	}
	
	public String getEmpStatus() {
		return empStatus;
	}
	
	public void setEmpStatus(String empStatus) {
		this.empStatus = empStatus;
	}
	
	public String[] getProbationLeaves() {
		return probationLeaves;
	}

	public void setProbationLeaves(String[] probationLeaves) {
		this.probationLeaves = probationLeaves;
	}

	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public List<FillLeaveType> getLeaveTypeList() {
		return leaveTypeList;
	}

	public void setLeaveTypeList(List<FillLeaveType> leaveTypeList) {
		this.leaveTypeList = leaveTypeList;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getStrLeaves() {
		return strLeaves;
	}

	public void setStrLeaves(String strLeaves) {
		this.strLeaves = strLeaves;
	}

	public List<String> getLeavesValue() {
		return leavesValue;
	}

	public void setLeavesValue(List<String> leavesValue) {
		this.leavesValue = leavesValue;
	}

	public String getStrNoHeaderLabel() {
		return strNoHeaderLabel;
	}

	public void setStrNoHeaderLabel(String strNoHeaderLabel) {
		this.strNoHeaderLabel = strNoHeaderLabel;
	}

}

