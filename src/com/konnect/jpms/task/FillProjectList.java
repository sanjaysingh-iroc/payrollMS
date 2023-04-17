package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.UtilityFunctions;

public class FillProjectList {
String projectID;
String projectName;

public FillProjectList(String projectID, String projectName) {
	this.projectID = projectID;
	this.projectName = projectName;
}

HttpServletRequest request;
public FillProjectList(HttpServletRequest request) {
	this.request = request;
}

public FillProjectList() {
	
}

public int getProjectId(int activity_id) {
	int proid=0;
	String selectEmployeeByShift = "select pro_id from activity_info where task_id=?";
	Connection con = null;
	PreparedStatement pst = null;
	ResultSet rs = null;
	Database db = new Database();
	db.setRequest(request);
	try {
		con = db.makeConnection(con);
		pst = con.prepareStatement(selectEmployeeByShift);
		pst.setInt(1,activity_id);
		rs = pst.executeQuery();
		while(rs.next()) {
			proid=rs.getInt("pro_id");				
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
	return proid;
}


public List<FillProjectList> fillAllProjectDetails(boolean isCompleted, boolean isBlocked) {
	
	List<FillProjectList> al = new ArrayList<FillProjectList>();
//	String selectEmployeeByShift = "select pro_id,pro_name from projectmntnc where approve_status='n' order by deadline ";
	Connection con = null;
	PreparedStatement pst = null;
	ResultSet rs = null;
	Database db = new Database();
	db.setRequest(request);
	try {
		con = db.makeConnection(con);
		if(isCompleted && !isBlocked) {
			pst = con.prepareStatement("select pro_id,pro_name from projectmntnc where approve_status='approved' order by pro_name ");
		} else if(!isCompleted && isBlocked) {
			pst = con.prepareStatement("select pro_id,pro_name from projectmntnc where approve_status='blocked' order by pro_name ");
		} else if(isCompleted && isBlocked) {
			pst = con.prepareStatement("select pro_id,pro_name from projectmntnc where approve_status='approved' and approve_status='blocked' order by pro_name ");
		} else {
			pst = con.prepareStatement("select pro_id,pro_name from projectmntnc where approve_status='n' order by pro_name ");
		}
		rs = pst.executeQuery(); 
		while(rs.next()) {
			al.add(new FillProjectList(rs.getString("pro_id"), rs.getString("pro_name")));				
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
	return al;
}



public List<FillProjectList> fillAllApprovedProjectDetails() {
	List<FillProjectList> al = new ArrayList<FillProjectList>();
	String selectEmployeeByShift = "select pro_id,pro_name from projectmntnc where approve_status!='n' order by deadline ";
	Connection con = null;
	PreparedStatement pst = null;
	ResultSet rs = null;
	Database db = new Database();
	db.setRequest(request);
	try {
		con = db.makeConnection(con);
		pst = con.prepareStatement(selectEmployeeByShift);
		rs = pst.executeQuery(); 
		while(rs.next()) {
			al.add(new FillProjectList(rs.getString("pro_id"), rs.getString("pro_name")));				
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
	return al;
}


public List<FillProjectList> fillProjectAllDetails() {
	
	List<FillProjectList> al = new ArrayList<FillProjectList>();
	Connection con = null;
	PreparedStatement pst = null;
	ResultSet rs = null;
	Database db = new Database();
	db.setRequest(request);
	try {
		con = db.makeConnection(con);
		pst = con.prepareStatement("select pro_id,pro_name from projectmntnc order by pro_id");
		rs = pst.executeQuery(); 
		while(rs.next()) {
			al.add(new FillProjectList(rs.getString("pro_id"), rs.getString("pro_name")));				
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
	return al;
}


public List<FillProjectList> fillProjectDetailsByManager(int emp_id, boolean isCompleted, boolean isBlocked)
{
	
	List<FillProjectList> al = new ArrayList<FillProjectList>();
	Connection con = null;
	PreparedStatement pst = null;
	ResultSet rs = null;
	Database db = new Database();
	db.setRequest(request);
	try {
		con = db.makeConnection(con);
		if(isCompleted && !isBlocked) {
			pst = con.prepareStatement("select pro_id,pro_name from projectmntnc where approve_status='approved' and added_by=? order by deadline");
		} else if(!isCompleted && isBlocked) {
			pst = con.prepareStatement("select pro_id,pro_name from projectmntnc where approve_status='blocked' and added_by=? order by deadline");
		} else if(isCompleted && isBlocked) {
			pst = con.prepareStatement("select pro_id,pro_name from projectmntnc where approve_status='approved' and approve_status='blocked' and added_by=? order by deadline");
		} else {
			pst = con.prepareStatement("select pro_id,pro_name from projectmntnc where approve_status='n' and added_by=? order by deadline");
		}
		pst.setInt(1, emp_id);
		rs = pst.executeQuery(); 
		while(rs.next()) {
			al.add(new FillProjectList(rs.getString("pro_id"), rs.getString("pro_name")));				
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
	return al;
}


public List<FillProjectList> fillProjectDetailsByCustomer(int emp_id, boolean isRunning, boolean isCompleted, boolean isBlocked)
{
	
	List<FillProjectList> al = new ArrayList<FillProjectList>();
	Connection con = null;
	PreparedStatement pst = null;
	ResultSet rs = null;
	Database db = new Database();
	db.setRequest(request);
	try {
		con = db.makeConnection(con);
		StringBuilder sbQue = new StringBuilder();
		sbQue.append("select pro_id,pro_name from projectmntnc where pro_id > 0 ");
		if(isRunning && isCompleted && isBlocked) {
			sbQue.append(" and (approve_status='n' or approve_status='approved' or approve_status='blocked') ");
		} else if(isRunning && isCompleted) {
			sbQue.append(" and (approve_status='n' or approve_status='approved') ");
		} else if(isCompleted && isBlocked) {
			sbQue.append(" and (approve_status='approved' or approve_status='blocked') ");
		} else if(isRunning && isBlocked) {
			sbQue.append(" and (approve_status='n' or approve_status='blocked') ");
		} else if(isRunning) {
			sbQue.append(" and approve_status='n' ");
		} else if(isCompleted) {
			sbQue.append(" and approve_status='approved' ");
		} else if(isBlocked) {
			sbQue.append(" and approve_status='blocked' ");
		}
		sbQue.append(" and poc=? order by deadline");
		pst = con.prepareStatement(sbQue.toString());
		pst.setInt(1, emp_id);
		rs = pst.executeQuery(); 
		while(rs.next()) {
			al.add(new FillProjectList(rs.getString("pro_id"), rs.getString("pro_name")));				
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
	return al;
}


public List<FillProjectList> fillProjectDetailsByEmp(int emp_id, boolean isCompleted, int clientId) {
	
	List<FillProjectList> al = new ArrayList<FillProjectList>();
	Connection con = null;
	PreparedStatement pst = null;
	ResultSet rs = null;
	Database db = new Database();
	db.setRequest(request);
	try {
		con = db.makeConnection(con);
		StringBuilder sbQuery = new StringBuilder();
		if(isCompleted) {
//			sbQuery.append("select pro_id, pro_name from projectmntnc where approve_status='approved' and approve_status!= 'blocked' and pro_id in (select pro_id from project_emp_details where emp_id = ?) ");
//			sbQuery.append("select distinct(pro.pro_id),pro.pro_name from activity_info ac, projectmntnc pro where ac.resource_ids like '%,"+ emp_id +",%' and pro.pro_id= ac.pro_id and pro.approve_status='approved' ");
			sbQuery.append("select distinct(pro.pro_id),pro.pro_name from project_emp_details ped, projectmntnc pro where ped.emp_id = "+ emp_id +" and pro.pro_id= ped.pro_id and pro.approve_status='approved' ");
			if(clientId > 0) {
				sbQuery.append(" and client_id = "+clientId+" ");
			}
			sbQuery.append(" order by pro_name");
		} else {
//			sbQuery.append("select pro_id, pro_name from projectmntnc where approve_status='n' and approve_status!= 'blocked' and pro_id in (select pro_id from project_emp_details where emp_id = ?) ");
//			sbQuery.append("select distinct(pro.pro_id),pro.pro_name from activity_info ac, projectmntnc pro where ac.resource_ids like '%,"+ emp_id +",%' and pro.pro_id= ac.pro_id and pro.approve_status='n' ");
			sbQuery.append("select distinct(pro.pro_id),pro.pro_name from project_emp_details ped, projectmntnc pro where ped.emp_id = "+ emp_id +" and pro.pro_id= ped.pro_id and pro.approve_status='n' ");
			if(clientId > 0) {
				sbQuery.append(" and client_id = "+clientId+" ");
			}
			sbQuery.append(" order by pro_name");
		}
		pst = con.prepareStatement(sbQuery.toString());
//		System.out.println("FPL/273---pst ===>> " + pst);
//		pst = con.prepareStatement("select distinct(pro.pro_id),pro.pro_name from activity_info ac,projectmntnc pro where ac.emp_id=? and pro.pro_id= ac.pro_id and ac.approve_status='n' and pro.approve_status='n' order by pro_id");
//		pst.setInt(1, emp_id);
		rs = pst.executeQuery();
		while(rs.next()) {
			al.add(new FillProjectList(rs.getString("pro_id"), rs.getString("pro_name")));				
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
	return al;
}


public List<FillProjectList> fillProjectDetailsByEmpWithOther(int emp_id, boolean isCompleted, String clientIds) {
	
	List<FillProjectList> al = new ArrayList<FillProjectList>();
	Connection con = null;
	PreparedStatement pst = null;
	ResultSet rs = null;
	Database db = new Database();
	db.setRequest(request);
	try {
		con = db.makeConnection(con);
		
		if(isCompleted) {
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select distinct(pro.pro_id),pro.pro_name from activity_info ac,projectmntnc pro where ac.resource_ids like '%,"+ emp_id +",%' and pro.pro_id= ac.pro_id and pro.approve_status='approved' ");
			if(clientIds != null && !clientIds.equalsIgnoreCase("null")) {
				sbQuery.append("and client_id in ("+clientIds+") ");
			}
			sbQuery.append(" order by pro_name");
			pst = con.prepareStatement(sbQuery.toString());
		} else {
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select distinct(pro.pro_id),pro.pro_name from activity_info ac,projectmntnc pro where ac.resource_ids like '%,"+ emp_id +",%' and pro.pro_id= ac.pro_id and pro.approve_status='n' ");
			if(clientIds != null && !clientIds.equalsIgnoreCase("null")) {
				sbQuery.append("and client_id in ("+clientIds+") ");
			}
			sbQuery.append(" order by pro_name");
			pst = con.prepareStatement(sbQuery.toString());
		}
//		pst = con.prepareStatement("select distinct(pro.pro_id),pro.pro_name from activity_info ac,projectmntnc pro where ac.emp_id=? and pro.pro_id= ac.pro_id and ac.approve_status='n' and pro.approve_status='n' order by pro_id");
//		pst.setInt(1, emp_id);
		rs = pst.executeQuery();
		while(rs.next()) {
			al.add(new FillProjectList(rs.getString("pro_id"), rs.getString("pro_name")));				
		}
		rs.close();
		pst.close();
		al.add(new FillProjectList("-1", "Other Activity"));
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		db.closeResultSet(rs);
		db.closeStatements(pst);
		db.closeConnection(con);
	}
	return al;
}


	public List<FillProjectList> fillProjectFrequencyList(String userType, String empId, String timesheetType) {
		
		List<FillProjectList> al = new ArrayList<FillProjectList>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			StringBuilder sbQuery = new StringBuilder();
	//		pst = con.prepareStatement("select pro_id,pro_name from projectmntnc order by pro_id");
			sbQuery.append("select pf.*,p.pro_id,p.pro_name,actual_calculation_type from projectmntnc p, projectmntnc_frequency pf where p.billing_type != 'F' " +
				"and p.pro_id = pf.pro_id and (pf.is_delete != true or pf.is_delete is null) ");
			if(userType != null && userType.equals(IConstants.CUSTOMER)) {
				sbQuery.append(" and p.poc = "+uF.parseToInt(empId)+" ");
			}
			if(userType != null && userType.equals(IConstants.MANAGER)) {
				sbQuery.append(" and (p.pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "+uF.parseToInt(empId)+" ) or pf.added_by = "+uF.parseToInt(empId)+" ) ");
			}
			sbQuery.append(" order by p.pro_name");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			
			while(rs.next()) {
				String pendingCnt = null;
				if(userType != null && userType.equals(IConstants.CUSTOMER)) {
					pendingCnt = getCustomerTimeSheetApprovalStatus(con, rs.getString("pro_id"), rs.getString("freq_start_date"), rs.getString("freq_end_date"), timesheetType);
				} else {
					pendingCnt = getProjectTimeSheetApprovalStatus(con, rs.getString("pro_id"), rs.getString("freq_start_date"), rs.getString("freq_end_date"), timesheetType);
				}
				if(timesheetType != null && timesheetType.equals("PA") && uF.parseToInt(pendingCnt) > 0) {
					continue;
				} else if(timesheetType != null && !timesheetType.equals("") && timesheetType.equals("PC") && uF.parseToInt(pendingCnt) == 0) {
					continue;
				}
				al.add(new FillProjectList(rs.getString("pro_freq_id"), rs.getString("pro_name")+ " ("+uF.showData(rs.getString("pro_freq_name"), "-")+")"));				
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
		return al;
	}

	private String getProjectTimeSheetApprovalStatus(Connection con, String proId, String freqStDate, String freqEndDate, String timesheetType) {
		 
		PreparedStatement pst = null; 
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		String pendingCnt = "0";
		try {
			
			StringBuilder sbTasks = null;
			pst = con.prepareStatement("select task_id from activity_info where pro_id = ?");				
			pst.setInt(1, uF.parseToInt(proId));
			rs = pst.executeQuery();			 
//			System.out.println("pst ===> " + pst);
			while(rs.next()) {
				if(sbTasks == null) {
					sbTasks = new StringBuilder();
					sbTasks.append(rs.getString("task_id"));
				} else {
					sbTasks.append(","+rs.getString("task_id"));
				}
			}
			rs.close();
			pst.close();
			
			if(sbTasks == null) {
				sbTasks = new StringBuilder();
			}
			
			if(sbTasks.toString().length() > 0) {
				boolean flag1 = false;
				pst = con.prepareStatement("select is_billable_approved from task_activity where is_billable_approved <= 1 and " +
						"activity_id in("+sbTasks.toString()+") and task_date between ? and ? group by is_billable_approved");
				pst.setDate(1, uF.getDateFormat(freqStDate, IConstants.DBDATE));
				pst.setDate(2, uF.getDateFormat(freqEndDate, IConstants.DBDATE));
				rs = pst.executeQuery();
//				System.out.println("pst flag1 ===> " + pst);
				while(rs.next()) {
					flag1 = true;
//					pendingCnt = "1";
				}
				rs.close();
				pst.close();
				
				boolean flag = false;
				pst = con.prepareStatement("select is_billable_approved from task_activity where activity_id in("+sbTasks.toString()+") and " +
						"task_date between ? and ? ");
				pst.setDate(1, uF.getDateFormat(freqStDate, IConstants.DBDATE));
				pst.setDate(2, uF.getDateFormat(freqEndDate, IConstants.DBDATE));
				rs = pst.executeQuery();
//				System.out.println("pst flag ===> " + pst);
				while(rs.next()) {
					flag = true;
				}
				rs.close();
				pst.close();
				
				if(timesheetType != null && timesheetType.equals("PA")) {
					if(!flag1 && flag) {
						pendingCnt = "0";
					} else {
						pendingCnt = "1";
					}
				} else {
					if((flag1 && flag) || !flag) {
						pendingCnt = "1";
					} else {
						pendingCnt = "0";
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pendingCnt;
	}
	
	
	public List<FillProjectList> fillProjectDetailsByProjectIds(String proIds) {
		
		List<FillProjectList> al = new ArrayList<FillProjectList>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			if(proIds != null && proIds.length() > 0) {			
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select distinct(pro.pro_id),pro.pro_name from projectmntnc pro where pro.pro_id in("+proIds+") ");
				sbQuery.append(" order by pro_name");
				pst = con.prepareStatement(sbQuery.toString());
				rs = pst.executeQuery();
				while(rs.next()) {
					al.add(new FillProjectList(rs.getString("pro_id"), rs.getString("pro_name")));				
				}
				rs.close();
				pst.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}
	
	
	private String getCustomerTimeSheetApprovalStatus(Connection con, String proId, String freqStDate, String freqEndDate, String timesheetType) {
		 
		PreparedStatement pst = null; 
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		String pendingCnt = "0";
		try {
			
			StringBuilder sbTasks = null;
			pst = con.prepareStatement("select task_id from activity_info where pro_id = ?");				
			pst.setInt(1, uF.parseToInt(proId));
			rs = pst.executeQuery();			 
//			System.out.println("pst ===> " + pst);
			while(rs.next()) {
				if(sbTasks == null) {
					sbTasks = new StringBuilder();
					sbTasks.append(rs.getString("task_id"));
				} else {
					sbTasks.append(","+rs.getString("task_id"));
				}
			}
			rs.close();
			pst.close();
			
			if(sbTasks == null) {
				sbTasks = new StringBuilder();
			}
			
			if(sbTasks.toString().length() > 0) {
				boolean flag1 = false;
				pst = con.prepareStatement("select is_billable_approved from task_activity where is_billable_approved = 2 and " +
						"activity_id in("+sbTasks.toString()+") and task_date between ? and ? group by is_billable_approved");
				pst.setDate(1, uF.getDateFormat(freqStDate, IConstants.DBDATE));
				pst.setDate(2, uF.getDateFormat(freqEndDate, IConstants.DBDATE));
				rs = pst.executeQuery();
//				System.out.println("pst ===> " + pst);
				while(rs.next()) {
					flag1 = true;
//					pendingCnt = "1";
				}
				rs.close();
				pst.close();
				
				boolean flag = false;
				pst = con.prepareStatement("select is_billable_approved from task_activity where activity_id in("+sbTasks.toString()+") and " +
						"task_date between ? and ? and is_billable_approved = 1");
				pst.setDate(1, uF.getDateFormat(freqStDate, IConstants.DBDATE));
				pst.setDate(2, uF.getDateFormat(freqEndDate, IConstants.DBDATE));
				rs = pst.executeQuery();
//				System.out.println("pst ===> " + pst);
				while(rs.next()) {
					flag = true;
				}
				rs.close();
				pst.close();
				
				if(timesheetType != null && timesheetType.equals("PA")) {
					if(flag1) {
						pendingCnt = "0";
					} else {
						pendingCnt = "1";
					}
				} else {
					if(flag) {
						pendingCnt = "1";
					} else {
						pendingCnt = "0";
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pendingCnt;
	}
	
//===created by parvez date: 18-02-2022===	
	//===start===
	public List<FillProjectList> fillProjectDetailsByCustomer(int clientId) {
		
		List<FillProjectList> al = new ArrayList<FillProjectList>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select pro_id,pro_name from projectmntnc where pro_id > 0 and approve_status!= 'blocked' and client_id=? order by pro_name");
			pst.setInt(1, clientId);
			rs = pst.executeQuery();
			while(rs.next()) {
				al.add(new FillProjectList(rs.getString("pro_id"), rs.getString("pro_name")));				
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
		return al;
	}
//===end===	
	
	public String getProjectID() {
		return projectID;
	}
	public void setProjectID(String projectID) {
		this.projectID = projectID;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}


}
