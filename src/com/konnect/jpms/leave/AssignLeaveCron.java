package com.konnect.jpms.leave;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class AssignLeaveCron extends Thread implements IStatements,ServletRequestAware {
	
	public CommonFunctions CF;
	public HttpServletRequest request; 
	public HttpSession session;
	public String strDomain;
	public String strOrgId;
	public String strWlocationId;
	public String strLevelId;
	public String strLeaveTypeId;
	public List<String> leaveAvailable;
	public String strEmpId;
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	public void setCronData() {
		if(!isAlive()){
			start();
		}
	}
	
	@Override
	public void run() {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			
			if(uF.parseToInt(getStrLeaveTypeId()) > 0){
				assignLeaveToEmployee(con,uF);
			} else if(uF.parseToInt(getStrEmpId()) > 0 && uF.parseToInt(getStrWlocationId()) > 0){
				assignWorkLocationLeaveToEmployee(con,uF);
			} else if(uF.parseToInt(getStrEmpId()) > 0 && uF.parseToInt(getStrLevelId()) > 0){
				assignLevelLeaveToEmployee(con,uF);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeConnection(con);
		}
	}

	private void assignLevelLeaveToEmployee(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
			pst = con.prepareStatement("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and eod.emp_id=?");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			rs = pst.executeQuery();
			String strEmpStatus = null;
			String strEmpGender = null;
			while(rs.next()){
				strEmpStatus = rs.getString("emp_status");
				strEmpGender = rs.getString("emp_gender");
			}
			rs.close();
			pst.close();
			
			if(strEmpStatus !=null && !strEmpStatus.trim().equals("") && !strEmpStatus.trim().equalsIgnoreCase("NULL")){
				int nEmpStatus = 0;
				if(strEmpStatus.trim().equalsIgnoreCase(PROBATION)){
					nEmpStatus = 1;
				} else if(strEmpStatus.trim().equalsIgnoreCase(PERMANENT)){
					nEmpStatus = 2;
				} else if(strEmpStatus.trim().equalsIgnoreCase(TEMPORARY)){
					nEmpStatus = 3;
				} else if(strEmpStatus.trim().equalsIgnoreCase(NOTICE)){
					nEmpStatus = 4;
				}
				
				if(nEmpStatus > 0){
					String strEmpWlocationId = CF.getEmpWlocationId(con, uF, getStrEmpId());
					StringBuilder sbQuery = new StringBuilder();
					sbQuery.append("select * from leave_type lt, emp_leave_type elt where elt.leave_type_id = lt.leave_type_id " +
							"and elt.wlocation_id=? and level_id=? and (leave_available like '%,0,%' or leave_available like '%,"+nEmpStatus+",%')");
					if(strEmpGender != null && strEmpGender.trim().equalsIgnoreCase("F")){
						sbQuery.append(" and leave_category in (0,1)");
					} else if(strEmpGender != null && strEmpGender.trim().equalsIgnoreCase("M")){
						sbQuery.append(" and leave_category in (0,2)");
					}	
					pst = con.prepareStatement(sbQuery.toString());
					pst.setInt(1, uF.parseToInt(strEmpWlocationId));
					pst.setInt(2, uF.parseToInt(getStrLevelId()));
					rs = pst.executeQuery();
					StringBuffer sbAvailableLeaves = null;
					while(rs.next()){
						if(sbAvailableLeaves == null){
							sbAvailableLeaves = new StringBuffer();
							sbAvailableLeaves.append(rs.getString("leave_type_id"));
						} else {
							sbAvailableLeaves.append(","+rs.getString("leave_type_id"));
						}
					}
					rs.close();
					pst.close();
					
					if(sbAvailableLeaves != null){
						pst = con.prepareStatement("update probation_policy set leaves_types_allowed=? where emp_id=?");
						pst.setString(1, sbAvailableLeaves.toString());
						pst.setInt(2, uF.parseToInt(getStrEmpId()));
						pst.execute();
						pst.close();
					}					
				}
			}
			
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
	}

	private void assignWorkLocationLeaveToEmployee(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
			pst = con.prepareStatement("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and eod.emp_id=?");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			rs = pst.executeQuery();
			String strEmpStatus = null;
			String strEmpGender = null;
			while(rs.next()){
				strEmpStatus = rs.getString("emp_status");
				strEmpGender = rs.getString("emp_gender");
			}
			rs.close();
			pst.close();
			
			if(strEmpStatus !=null && !strEmpStatus.trim().equals("") && !strEmpStatus.trim().equalsIgnoreCase("NULL")){
				int nEmpStatus = 0;
				if(strEmpStatus.trim().equalsIgnoreCase(PROBATION)){
					nEmpStatus = 1;
				} else if(strEmpStatus.trim().equalsIgnoreCase(PERMANENT)){
					nEmpStatus = 2;
				} else if(strEmpStatus.trim().equalsIgnoreCase(TEMPORARY)){
					nEmpStatus = 3;
				} else if(strEmpStatus.trim().equalsIgnoreCase(NOTICE)){
					nEmpStatus = 4;
				}
				
				if(nEmpStatus > 0){
					String strEmpLevelId = CF.getEmpLevelId(con, getStrEmpId());
					StringBuilder sbQuery = new StringBuilder();
					sbQuery.append("select * from leave_type lt, emp_leave_type elt where elt.leave_type_id = lt.leave_type_id " +
							"and elt.wlocation_id=? and elt.level_id=? and (leave_available like '%,0,%' or leave_available like '%,"+nEmpStatus+",%')");
					if(strEmpGender != null && strEmpGender.trim().equalsIgnoreCase("F")){
						sbQuery.append(" and leave_category in (0,1)");
					} else if(strEmpGender != null && strEmpGender.trim().equalsIgnoreCase("M")){
						sbQuery.append(" and leave_category in (0,2)");
					}						
					pst = con.prepareStatement(sbQuery.toString());
					pst.setInt(1, uF.parseToInt(getStrWlocationId()));
					pst.setInt(2, uF.parseToInt(strEmpLevelId));
					rs = pst.executeQuery();
					StringBuffer sbAvailableLeaves = null;
					while(rs.next()){
						if(sbAvailableLeaves == null){
							sbAvailableLeaves = new StringBuffer();
							sbAvailableLeaves.append(rs.getString("leave_type_id"));
						} else {
							sbAvailableLeaves.append(","+rs.getString("leave_type_id"));
						}
					}
					rs.close();
					pst.close();
					
					if(sbAvailableLeaves != null){
						pst = con.prepareStatement("update probation_policy set leaves_types_allowed=? where emp_id=?");
						pst.setString(1, sbAvailableLeaves.toString());
						pst.setInt(2, uF.parseToInt(getStrEmpId()));
						pst.execute();
						pst.close();
					}					
				}
			}
			
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
	}

	private void assignLeaveToEmployee(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			StringBuffer sbLeavesAvailable = null;
			if(getLeaveAvailable() != null && getLeaveAvailable().size() > 0){
				for(int i = 0; i < getLeaveAvailable().size(); i++){
					if(sbLeavesAvailable == null){
						sbLeavesAvailable = new StringBuffer();
						if(uF.parseToInt(getLeaveAvailable().get(i)) == 0){
							sbLeavesAvailable.append("'PROBATION', 'PERMANENT', 'TEMPORARY', 'NOTICE'");
						} else if(uF.parseToInt(getLeaveAvailable().get(i)) == 1){
							sbLeavesAvailable.append("'PROBATION'");
						} else if(uF.parseToInt(getLeaveAvailable().get(i)) == 2){
							sbLeavesAvailable.append("'PERMANENT'");
						} else if(uF.parseToInt(getLeaveAvailable().get(i)) == 3){
							sbLeavesAvailable.append("'TEMPORARY'");
						} else if(uF.parseToInt(getLeaveAvailable().get(i)) == 4){
							sbLeavesAvailable.append("'NOTICE'");
						}
					} else {
						if(uF.parseToInt(getLeaveAvailable().get(i)) == 0){
							sbLeavesAvailable.append(", 'PROBATION', 'PERMANENT', 'TEMPORARY', 'NOTICE'");
						} else if(uF.parseToInt(getLeaveAvailable().get(i)) == 1){
							sbLeavesAvailable.append(", 'PROBATION'");
						} else if(uF.parseToInt(getLeaveAvailable().get(i)) == 2){
							sbLeavesAvailable.append(", 'PERMANENT'");
						} else if(uF.parseToInt(getLeaveAvailable().get(i)) == 3){
							sbLeavesAvailable.append(", 'TEMPORARY'");
						} else if(uF.parseToInt(getLeaveAvailable().get(i)) == 4){
							sbLeavesAvailable.append(", 'NOTICE'");
						}
					}
				}
			} else {
				sbLeavesAvailable = new StringBuffer();
				sbLeavesAvailable.append("'PROBATION', 'PERMANENT', 'TEMPORARY', 'NOTICE'");
			}
			
			pst = con.prepareStatement("select * from leave_type where org_id=? and leave_type_id=?");
			pst.setInt(1, uF.parseToInt(getStrOrgId()));
			pst.setInt(2, uF.parseToInt(getStrLeaveTypeId()));
			rs = pst.executeQuery();
			int nLeaveCategory = 0;
			while(rs.next()){
				nLeaveCategory = uF.parseToInt(rs.getString("leave_category"));
			}
			rs.close();
			pst.close();
			
			StringBuffer sbQuery = new StringBuffer();
			sbQuery.append("select emp_id from employee_personal_details epd, employee_official_details eod where epd.emp_per_id=eod.emp_id " +
					"and epd.is_alive=true and eod.org_id=? and eod.wlocation_id=? and grade_id in (select gd.grade_id from grades_details gd, " +
					"level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id " +
					"and ld.level_id=?) ");
			if(sbLeavesAvailable != null){
				sbQuery.append(" and emp_status in ("+sbLeavesAvailable.toString()+") ");
			}
			if(nLeaveCategory == 1){
				sbQuery.append(" and emp_gender='F'");
			} else if(nLeaveCategory == 2){
				sbQuery.append(" and emp_gender='M'");
			}
			sbQuery.append("order by emp_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getStrOrgId()));
			pst.setInt(2, uF.parseToInt(getStrWlocationId()));
			pst.setInt(3, uF.parseToInt(getStrLevelId()));
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			List<String> alEmp = new ArrayList<String>();
			while(rs.next()){
				alEmp.add(rs.getString("emp_id"));
			}
			rs.close();
			pst.close();
			
			for(String strEmpId : alEmp){				
				pst = con.prepareStatement("select * from probation_policy where emp_id=?");
				pst.setInt(1, uF.parseToInt(strEmpId));
//				System.out.println("pst====>"+pst);
				rs = pst.executeQuery();
				List<String> alAssignedLeave = new ArrayList<String>();
				StringBuffer sbAvailableLeaves = null;
				while(rs.next()){
					if(rs.getString("leaves_types_allowed") != null && !rs.getString("leaves_types_allowed").trim().equals("") 
							&& !rs.getString("leaves_types_allowed").trim().equalsIgnoreCase("NULL")){
						sbAvailableLeaves = new StringBuffer();
						sbAvailableLeaves.append(rs.getString("leaves_types_allowed"));
						String[] temp = rs.getString("leaves_types_allowed").trim().split(",");
						for(int i = 0; i < temp.length; i++){
							if(uF.parseToInt(temp[i].trim()) > 0){
								if(!alAssignedLeave.contains(temp[i].trim())){
									alAssignedLeave.add(temp[i].trim());
								}
							}
						}
					}
				}
				rs.close();
				pst.close();
				
				if(!alAssignedLeave.contains(getStrLeaveTypeId())){
					if(sbAvailableLeaves == null){
						sbAvailableLeaves = new StringBuffer();
						sbAvailableLeaves.append(getStrLeaveTypeId());
					} else {
						sbAvailableLeaves.append(","+getStrLeaveTypeId());
					}
					
					pst = con.prepareStatement("update probation_policy set leaves_types_allowed=? where emp_id=?");
					pst.setString(1, sbAvailableLeaves.toString());
					pst.setInt(2, uF.parseToInt(strEmpId));
					pst.execute();
					pst.close();
				}				
			}
			
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
	}

	public CommonFunctions getCF() {
		return CF;
	}

	public void setCF(CommonFunctions cF) {
		CF = cF;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public HttpSession getSession() {
		return session;
	}

	public void setSession(HttpSession session) {
		this.session = session;
	}

	public String getStrDomain() {
		return strDomain;
	}

	public void setStrDomain(String strDomain) {
		this.strDomain = strDomain;
	}

	public String getStrOrgId() {
		return strOrgId;
	}

	public void setStrOrgId(String strOrgId) {
		this.strOrgId = strOrgId;
	}

	public String getStrWlocationId() {
		return strWlocationId;
	}

	public void setStrWlocationId(String strWlocationId) {
		this.strWlocationId = strWlocationId;
	}

	public String getStrLevelId() {
		return strLevelId;
	}

	public void setStrLevelId(String strLevelId) {
		this.strLevelId = strLevelId;
	}

	public String getStrLeaveTypeId() {
		return strLeaveTypeId;
	}

	public void setStrLeaveTypeId(String strLeaveTypeId) {
		this.strLeaveTypeId = strLeaveTypeId;
	}

	public List<String> getLeaveAvailable() {
		return leaveAvailable;
	}

	public void setLeaveAvailable(List<String> leaveAvailable) {
		this.leaveAvailable = leaveAvailable;
	}

	public String getStrEmpId() {
		return strEmpId;
	}

	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}
}