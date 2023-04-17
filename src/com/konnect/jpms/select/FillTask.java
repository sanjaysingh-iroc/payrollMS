package com.konnect.jpms.select;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class FillTask implements IStatements {
	String taskId;
	String taskName;

	private FillTask(String taskId, String taskName) {
		this.taskId = taskId;
		this.taskName = taskName;
	}
	
	HttpServletRequest request;
	public FillTask(HttpServletRequest request) {
		this.request = request;
	}
	
	public FillTask() {
	}
	
	
	public List<FillTask> fillTask(int nEmpId) {
		List<FillTask> al = new ArrayList<FillTask>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
//			pst = con.prepareStatement("select * from activity_info where emp_id =?");
			pst = con.prepareStatement("select a.* from (select task_id,activity_name,parent_task_id,pro_id from activity_info where resource_ids like '%,"+nEmpId+",%' " +
				"and task_id not in (select parent_task_id from activity_info where resource_ids like '%,"+nEmpId+",%' and parent_task_id is not null)) a, " + // and task_accept_status = 1
				"projectmntnc pmc where pmc.pro_id=a.pro_id and pmc.approve_status='n' and (parent_task_id in (select task_id from activity_info " +
				"where resource_ids like '%,"+nEmpId+",%') or parent_task_id = 0)");
//			pst.setInt(1, nEmpId);
			rs1 = pst.executeQuery();
			while (rs1.next()) {
				if(rs1.getInt("parent_task_id") > 0) {
					if(rs1.getString("activity_name") != null && !rs1.getString("activity_name").equals("")) {
						String activityName = rs1.getString("activity_name").replace(".", ". ");
						activityName = activityName.replace(",", ", ");
						String strTemp[] = activityName.split(" ");
						StringBuilder sbTaskName = new StringBuilder();
						for(int i=0; strTemp.length >0 && i<strTemp.length; i++) {
							if(strTemp[i].trim().length()>0) {
								sbTaskName.append(strTemp[i].trim()+" ");
							}
						}
						al.add(new FillTask(rs1.getString("task_id"), sbTaskName.toString().trim() +" [ST]"));		
					}
				} else {
					if(rs1.getString("activity_name") != null && !rs1.getString("activity_name").equals("")) {
						String activityName = rs1.getString("activity_name").replace(".", ". ");
						activityName = activityName.replace(",", ", ");
						String strTemp[] = activityName.split(" ");
						StringBuilder sbTaskName = new StringBuilder();
						for(int i=0; strTemp.length >0 && i<strTemp.length; i++) {
							if(strTemp[i].trim().length()>0) {
								sbTaskName.append(strTemp[i].trim()+" ");
							}
						}
						al.add(new FillTask(rs1.getString("task_id"), sbTaskName.toString().trim()));		
					}
				}
			}	
			rs1.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs1);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	
		return al;
	}
	
	public List<FillTask> fillAllTasks() {
		List<FillTask> al = new ArrayList<FillTask>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from activity_info ");
//			pst.setInt(1, nProjectId);
			rs1 = pst.executeQuery();
			while (rs1.next()) {
				if(rs1.getString("activity_name") != null && !rs1.getString("activity_name").equals("")) {
					String activityName = rs1.getString("activity_name").replace(".", ". ");
					activityName = activityName.replace(",", ", ");
					String strTemp[] = activityName.split(" ");
					StringBuilder sbTaskName = new StringBuilder();
					for(int i=0; strTemp.length >0 && i<strTemp.length; i++) {
						if(strTemp[i].trim().length()>0) {
							sbTaskName.append(strTemp[i].trim()+" ");
						}
					}
					al.add(new FillTask(rs1.getString("task_id"), sbTaskName.toString().trim()));		
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeResultSet(rs1);
			db.closeConnection(con);
		}
		return al;
	}
	
	
	public List<FillTask> fillProjectTasks(String nProjectId) {
		List<FillTask> al = new ArrayList<FillTask>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			if(nProjectId != null && nProjectId.length()>0) {
				pst = con.prepareStatement("select * from activity_info where pro_id in ("+nProjectId+")");
				rs1 = pst.executeQuery();
				while (rs1.next()) {
					if(rs1.getInt("parent_task_id") > 0) {
						if(rs1.getString("activity_name") != null && !rs1.getString("activity_name").equals("")) {
							String activityName = rs1.getString("activity_name").replace(".", ". ");
							activityName = activityName.replace(",", ", ");
							String strTemp[] = activityName.split(" ");
							StringBuilder sbTaskName = new StringBuilder();
							for(int i=0; strTemp.length >0 && i<strTemp.length; i++) {
								if(strTemp[i].trim().length()>0) {
									sbTaskName.append(strTemp[i].trim()+" ");
								}
							}
							al.add(new FillTask(rs1.getString("task_id"), sbTaskName.toString().trim()+" [ST]"));		
						}
					} else {
						if(rs1.getString("activity_name") != null && !rs1.getString("activity_name").equals("")) {
							String activityName = rs1.getString("activity_name").replace(".", ". ");
							activityName = activityName.replace(",", ", ");
							String strTemp[] = activityName.split(" ");
							StringBuilder sbTaskName = new StringBuilder();
							for(int i=0; strTemp.length >0 && i<strTemp.length; i++) {
								if(strTemp[i].trim().length()>0) {
									sbTaskName.append(strTemp[i].trim()+" ");
								}
							}
							al.add(new FillTask(rs1.getString("task_id"), sbTaskName.toString().trim()));		
						}
					}
				}	
				rs1.close();
				pst.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs1);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	
		return al;
	}
	
	
	public List<FillTask> fillTaskByProjects(int nProjectId, int nEmpId) {
		List<FillTask> al = new ArrayList<FillTask>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
//			pst = con.prepareStatement("select * from activity_info where pro_id =? and emp_id =?");
			pst = con.prepareStatement("select a.* from (select task_id,activity_name,parent_task_id,pro_id from activity_info where resource_ids like '%,"+nEmpId+",%' " +
				"and task_id not in (select parent_task_id from activity_info where resource_ids like '%,"+nEmpId+",%' and parent_task_id is not null) and task_accept_status = 1) a, " +
				"projectmntnc pmc where pmc.pro_id=a.pro_id and pmc.approve_status='n' and (parent_task_id in (select task_id from activity_info " +
				"where resource_ids like '%,"+nEmpId+",%') or parent_task_id = 0) and a.pro_id = ?");
			pst.setInt(1, nProjectId);
//			pst.setInt(2, nEmpId);
			rs1 = pst.executeQuery();
			while (rs1.next()) {
				if(rs1.getInt("parent_task_id") > 0) {
					if(rs1.getString("activity_name") != null && !rs1.getString("activity_name").equals("")) {
						String activityName = rs1.getString("activity_name").replace(".", ". ");
						activityName = activityName.replace(",", ", ");
						String strTemp[] = activityName.split(" ");
						StringBuilder sbTaskName = new StringBuilder();
						for(int i=0; strTemp.length >0 && i<strTemp.length; i++) {
							if(strTemp[i].trim().length()>0) {
								sbTaskName.append(strTemp[i].trim()+" ");
							}
						}
						al.add(new FillTask(rs1.getString("task_id"), sbTaskName.toString().trim()+" [ST]"));	
					}
				} else {
					if(rs1.getString("activity_name") != null && !rs1.getString("activity_name").equals("")) {
						String activityName = rs1.getString("activity_name").replace(".", ". ");
						activityName = activityName.replace(",", ", ");
						String strTemp[] = activityName.split(" ");
						StringBuilder sbTaskName = new StringBuilder();
						for(int i=0; strTemp.length >0 && i<strTemp.length; i++) {
							if(strTemp[i].trim().length()>0) {
								sbTaskName.append(strTemp[i].trim()+" ");
							}
						}
						al.add(new FillTask(rs1.getString("task_id"), sbTaskName.toString().trim()));		
					}
				}
			}	
			rs1.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs1);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	
		return al;
	} 
	
	
	public List<FillTask> fillTaskByMultiProjects(com.konnect.jpms.util.CommonFunctions CF, String proIds, int nEmpId) {
		List<FillTask> al = new ArrayList<FillTask>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			StringBuilder sbQuery = new StringBuilder();
//			sbQuery.append("select * from activity_info where emp_id =?  ");
//			sbQuery.append("select task_id,activity_name,parent_task_id from activity_info where resource_ids like '%,"+nEmpId+",%' and task_id not in (" +
//				"select parent_task_id from activity_info where resource_ids like '%,"+nEmpId+",%' and parent_task_id is not null)");
			
			sbQuery.append("select a.*,pmc.client_id from (select task_id,activity_name,parent_task_id,pro_id from activity_info where resource_ids like '%,"+nEmpId+",%' " +
				"and task_id not in (select parent_task_id from activity_info where resource_ids like '%,"+nEmpId+",%' and parent_task_id is not null) and task_accept_status = 1) a, " +
				"projectmntnc pmc where pmc.pro_id=a.pro_id and pmc.approve_status='n' and (parent_task_id in (select task_id from activity_info " +
				"where resource_ids like '%,"+nEmpId+",%') or parent_task_id = 0) ");
			if(proIds != null && !proIds.equals("")) {
				sbQuery.append(" and a.pro_id in("+proIds+") ");
			}
			sbQuery.append(" order by activity_name "); 
//			System.out.println("sbQuery.toString() ===>> " + sbQuery.toString());
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setInt(1, nEmpId);
			rs1 = pst.executeQuery();
			while (rs1.next()) {
//				System.out.println("activity_name ===>> " + rs1.getString("activity_name"));
				if(rs1.getInt("parent_task_id") > 0) {
					if(rs1.getString("activity_name") != null && !rs1.getString("activity_name").equals("")) {
						String activityName = rs1.getString("activity_name").replace(".", ". ");
						activityName = activityName.replace(",", ", ");
						String strTemp[] = activityName.split(" ");
						StringBuilder sbTaskName = new StringBuilder();
						for(int i=0; strTemp.length >0 && i<strTemp.length; i++) {
							if(strTemp[i].trim().length()>0) {
								sbTaskName.append(strTemp[i].trim()+" ");
							}
						}
						al.add(new FillTask(rs1.getString("task_id"), sbTaskName.toString().trim()+" [ST]" +" ("+CF.getProjectNameById(con, rs1.getString("pro_id")) +", "+CF.getClientNameById(con, rs1.getString("client_id"))+")"));		
					}
				} else {
					if(rs1.getString("activity_name") != null && !rs1.getString("activity_name").equals("")) {
						String activityName = rs1.getString("activity_name").replace(".", ". ");
						activityName = activityName.replace(",", ", ");
						String strTemp[] = activityName.split(" ");
						StringBuilder sbTaskName = new StringBuilder();
						for(int i=0; strTemp.length >0 && i<strTemp.length; i++) {
							if(strTemp[i].trim().length()>0) {
								sbTaskName.append(strTemp[i].trim()+" ");
							}
						}
						al.add(new FillTask(rs1.getString("task_id"), sbTaskName.toString().trim() +" ("+CF.getProjectNameById(con, rs1.getString("pro_id")) +", "+CF.getClientNameById(con, rs1.getString("client_id"))+")"));		
					}
				}
			}	
			rs1.close();
			pst.close();
			
			StringBuilder sbQuery1 = new StringBuilder();
			sbQuery1.append("select task_id,activity_name,parent_task_id,pro_id from activity_info where resource_ids like '%,"+nEmpId+",%' and " +
				"task_id not in (select parent_task_id from activity_info where resource_ids like '%,"+nEmpId+",%' and parent_task_id is not null) and " +
				"task_accept_status = 1 and approve_status='n' and (parent_task_id in (select task_id from activity_info where resource_ids like " +
				"'%,"+nEmpId+",%') or parent_task_id = 0) and pro_id = 0 order by activity_name ");
//			System.out.println("sbQuery.toString() all ===>>>> " + sbQuery.toString());
			pst = con.prepareStatement(sbQuery1.toString());
//			pst.setInt(1, nEmpId);
			rs1 = pst.executeQuery();
			while (rs1.next()) {
				if(rs1.getInt("parent_task_id") > 0) {
					if(rs1.getString("activity_name") != null && !rs1.getString("activity_name").equals("")) {
						String activityName = rs1.getString("activity_name").replace(".", ". ");
						activityName = activityName.replace(",", ", ");
						String strTemp[] = activityName.split(" ");
						StringBuilder sbTaskName = new StringBuilder();
						for(int i=0; strTemp.length >0 && i<strTemp.length; i++) {
							if(strTemp[i].trim().length()>0) {
								sbTaskName.append(strTemp[i].trim()+" ");
							}
						}
						al.add(new FillTask(rs1.getString("task_id"), sbTaskName.toString().trim() +" [ST]" +" (Other)"));		
					}
				} else {
					if(rs1.getString("activity_name") != null && !rs1.getString("activity_name").equals("")) {
						String activityName = rs1.getString("activity_name").replace(".", ". ");
						activityName = activityName.replace(",", ", ");
						String strTemp[] = activityName.split(" ");
						StringBuilder sbTaskName = new StringBuilder();
						for(int i=0; strTemp.length >0 && i<strTemp.length; i++) {
							if(strTemp[i].trim().length()>0) {
								sbTaskName.append(strTemp[i].trim()+" ");
							}
						}
						al.add(new FillTask(rs1.getString("task_id"), sbTaskName.toString().trim() +" (Other)"));		
					}
				}
			}	
			rs1.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs1);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	
		return al;
	} 
	
public List<FillTask> fillExtraActivity(com.konnect.jpms.util.CommonFunctions CF, int nEmpId) {
	List<FillTask> al = new ArrayList<FillTask>();
	
	Connection con = null;
	PreparedStatement pst = null;
	ResultSet rs1 = null;
	Database db = new Database();
	db.setRequest(request);
	try {

		con = db.makeConnection(con);
		StringBuilder sbQuery1 = new StringBuilder();
		sbQuery1.append("select task_id,activity_name,parent_task_id,pro_id from activity_info where resource_ids like '%,"+nEmpId+",%' and " +
			"task_id not in (select parent_task_id from activity_info where resource_ids like '%,"+nEmpId+",%' and parent_task_id is not null) and " +
			"task_accept_status = 1 and approve_status='n' and (parent_task_id in (select task_id from activity_info where resource_ids like " +
			"'%,"+nEmpId+",%') or parent_task_id = 0) and pro_id = 0 order by activity_name ");
//		System.out.println("sbQuery.toString() Other ===>>>> " + sbQuery1.toString());
		pst = con.prepareStatement(sbQuery1.toString());
//		pst.setInt(1, nEmpId);
		rs1 = pst.executeQuery();
		while (rs1.next()) {
			if(rs1.getInt("parent_task_id") > 0) {
				if(rs1.getString("activity_name") != null && !rs1.getString("activity_name").equals("")) {
					String activityName = rs1.getString("activity_name").replace(".", ". ");
					activityName = activityName.replace(",", ", ");
					String strTemp[] = activityName.split(" ");
					StringBuilder sbTaskName = new StringBuilder();
					for(int i=0; strTemp.length >0 && i<strTemp.length; i++) {
						if(strTemp[i].trim().length()>0) {
							sbTaskName.append(strTemp[i].trim()+" ");
						}
					}
					al.add(new FillTask(rs1.getString("task_id"), sbTaskName.toString().trim() +" [ST]" +" (Other)"));		
				}
			} else {
				if(rs1.getString("activity_name") != null && !rs1.getString("activity_name").equals("")) {
					String activityName = rs1.getString("activity_name").replace(".", ". ");
					activityName = activityName.replace(",", ", ");
					String strTemp[] = activityName.split(" ");
					StringBuilder sbTaskName = new StringBuilder();
					for(int i=0; strTemp.length >0 && i<strTemp.length; i++) {
						if(strTemp[i].trim().length()>0) {
							sbTaskName.append(strTemp[i].trim()+" ");
						}
					}
					al.add(new FillTask(rs1.getString("task_id"), sbTaskName.toString().trim() +" (Other)"));		
				}
			}
		}	
		rs1.close();
		pst.close();
		
		
		sbQuery1 = new StringBuilder();
		sbQuery1.append("select task_name from common_tasks");
		pst = con.prepareStatement(sbQuery1.toString());
		rs1 = pst.executeQuery();
		while (rs1.next()) {
			al.add(new FillTask(rs1.getString("task_name"), rs1.getString("task_name")));
		}	
		rs1.close();
		pst.close();
		
		/*al.add(new FillTask("a Research on New Project","a Research on New Project"));
		al.add(new FillTask("a Desk Job","a Desk Job"));
		al.add(new FillTask("a Call","a Call"));
		al.add(new FillTask("a Sales Call","a Sales Call"));
		al.add(new FillTask("a Conference Call","a Conference Call"));
		al.add(new FillTask("a Meeting with my Supervisor","a Meeting with my Supervisor"));
		al.add(new FillTask("a Meeting with my Subordinate","a Meeting with my Subordinate"));
		al.add(new FillTask("a Meeting with HR","a Meeting with HR"));
		al.add(new FillTask("a Team Meeting","a Team Meeting"));
		al.add(new FillTask("a Meeting with Client","a Meeting with Client"));
		al.add(new FillTask("a Client Demo","a Client Demo"));
		al.add(new FillTask("a Client Visit","a Client Visit"));
		al.add(new FillTask("a Field Visit","a Field Visit"));
		al.add(new FillTask("a Training Session","a Training Session"));
		
		al.add(new FillTask("GMCS 1","GMCS 1"));
		al.add(new FillTask("GMCS 2","GMCS 2"));
		al.add(new FillTask("ITT","ITT"));
		al.add(new FillTask("Exam Leave","Exam Leave"));
		al.add(new FillTask("Quality Control","Quality Control"));
		
		al.add(new FillTask("Other Activity","Other Activity"));
		al.add(new FillTask("Interview","Interview"));
		al.add(new FillTask("Training and Transition","Training and Transition"));
		
		al.add(new FillTask("Certification and Other Work","Certification and Other Work"));
		
		al.add(new FillTask("to Pantry","to Pantry"));
		al.add(new FillTask("a Coffee Break","a Coffee Break"));
		al.add(new FillTask("a Break","a Break"));*/
		
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		db.closeResultSet(rs1);
		db.closeStatements(pst);
		db.closeConnection(con);
	}
	return al;

//		List<FillTask> al = new ArrayList<FillTask>();
//		al.add(new FillTask("a Research on New Project","a Research on New Project"));
//		al.add(new FillTask("a Desk Job","a Desk Job"));
//		al.add(new FillTask("a Call","a Call"));
//		al.add(new FillTask("a Sales Call","a Sales Call"));
//		al.add(new FillTask("a Conference Call","a Conference Call"));
//		al.add(new FillTask("a Meeting with my Supervisor","a Meeting with my Supervisor"));
//		al.add(new FillTask("a Meeting with my Subordinate","a Meeting with my Subordinate"));
//		al.add(new FillTask("a Meeting with HR","a Meeting with HR"));
//		al.add(new FillTask("a Team Meeting","a Team Meeting"));
//		al.add(new FillTask("a Meeting with Client","a Meeting with Client"));
//		al.add(new FillTask("a Client Demo","a Client Demo"));
//		al.add(new FillTask("a Client Visit","a Client Visit"));
//		al.add(new FillTask("a Field Visit","a Field Visit"));
//		al.add(new FillTask("a Tranning Session","a Tranning Session"));
//		al.add(new FillTask("to Pantry","to Pantry"));
//		al.add(new FillTask("a Coffee Break","a Coffee Break"));
//		al.add(new FillTask("a Break","a Break"));
//		return al;
	}


public List<FillTask> fillAllTasksWithOtherOfEmployee(com.konnect.jpms.util.CommonFunctions CF, int nEmpId, String proIds, String clientIds) {
	List<FillTask> al = new ArrayList<FillTask>();
	
	Connection con = null;
	PreparedStatement pst = null;
	ResultSet rs1 = null;
	Database db = new Database();
	db.setRequest(request);
	try {

		con = db.makeConnection(con);
		StringBuilder sbQuery = new StringBuilder();
//		sbQuery.append("select * from activity_info where emp_id =? ");
//		sbQuery.append("select task_id,activity_name,parent_task_id from activity_info where resource_ids like '%,"+nEmpId+",%' and task_id not in (" +
//				"select parent_task_id from activity_info where resource_ids like '%,"+nEmpId+",%' and parent_task_id is not null)");
		sbQuery.append("select a.*,pmc.client_id from (select task_id,activity_name,parent_task_id,pro_id from activity_info where resource_ids like '%,"+nEmpId+",%' " +
			"and task_id not in (select parent_task_id from activity_info where resource_ids like '%,"+nEmpId+",%' and parent_task_id is not null) and task_accept_status = 1) a, " +
			"projectmntnc pmc where pmc.pro_id=a.pro_id and pmc.approve_status='n' and (parent_task_id in (select task_id from activity_info " +
			"where resource_ids like '%,"+nEmpId+",%') or parent_task_id = 0) ");
		if(proIds != null && !proIds.equals("")) {
			sbQuery.append(" and a.pro_id in("+proIds+") ");
		}
		if(clientIds != null && !clientIds.equals("")) {
			sbQuery.append(" and pmc.client_id in("+clientIds+") ");
		}
		sbQuery.append(" order by activity_name ");
//		System.out.println("sbQuery.toString() all ===>>>> " + sbQuery.toString());
		pst = con.prepareStatement(sbQuery.toString());
//		pst.setInt(1, nEmpId);
		rs1 = pst.executeQuery();
		while (rs1.next()) {
			if(rs1.getInt("parent_task_id") > 0) {
				if(rs1.getString("activity_name") != null && !rs1.getString("activity_name").equals("")) {
					String activityName = rs1.getString("activity_name").replace(".", ". ");
					activityName = activityName.replace(",", ", ");
					String strTemp[] = activityName.split(" ");
					StringBuilder sbTaskName = new StringBuilder();
					for(int i=0; strTemp.length >0 && i<strTemp.length; i++) {
						if(strTemp[i].trim().length()>0) {
							sbTaskName.append(strTemp[i].trim()+" ");
						}
					}
					al.add(new FillTask(rs1.getString("task_id"), sbTaskName.toString().trim() +" [ST]" +" ("+CF.getProjectNameById(con, rs1.getString("pro_id")) +", "+CF.getClientNameById(con, rs1.getString("client_id"))+")"));		
				}
			} else {
				if(rs1.getString("activity_name") != null && !rs1.getString("activity_name").equals("")) {
					String activityName = rs1.getString("activity_name").replace(".", ". ");
					activityName = activityName.replace(",", ", ");
					String strTemp[] = activityName.split(" ");
					StringBuilder sbTaskName = new StringBuilder();
					for(int i=0; strTemp.length >0 && i<strTemp.length; i++) {
						if(strTemp[i].trim().length()>0) {
							sbTaskName.append(strTemp[i].trim()+" ");
						}
					}
//					System.out.println("sbTaskName ===>> " + sbTaskName.toString());
					al.add(new FillTask(rs1.getString("task_id"), sbTaskName.toString().trim() +" ("+CF.getProjectNameById(con, rs1.getString("pro_id")) +", "+CF.getClientNameById(con, rs1.getString("client_id"))+")"));		
				}
//				System.out.println("activity_name ===>> " + rs1.getString("activity_name").trim() +" ("+CF.getProjectNameById(con, rs1.getString("pro_id")) +", "+CF.getClientNameById(con, rs1.getString("client_id"))+")");
			}
		}	
		rs1.close();
		pst.close();
		
		StringBuilder sbQuery1 = new StringBuilder();
		sbQuery1.append("select task_id,activity_name,parent_task_id,pro_id from activity_info where resource_ids like '%,"+nEmpId+",%' and " +
			"task_id not in (select parent_task_id from activity_info where resource_ids like '%,"+nEmpId+",%' and parent_task_id is not null) and " +
			"task_accept_status = 1 and approve_status='n' and (parent_task_id in (select task_id from activity_info where resource_ids like " +
			"'%,"+nEmpId+",%') or parent_task_id = 0) and pro_id = 0 order by activity_name ");
//		System.out.println("sbQuery.toString() all ===>>>> " + sbQuery.toString());
		pst = con.prepareStatement(sbQuery1.toString());
//		pst.setInt(1, nEmpId);
		rs1 = pst.executeQuery();
		while (rs1.next()) {
			if(rs1.getInt("parent_task_id") > 0) {
				if(rs1.getString("activity_name") != null && !rs1.getString("activity_name").equals("")) {
					String activityName = rs1.getString("activity_name").replace(".", ". ");
					activityName = activityName.replace(",", ", ");
					String strTemp[] = activityName.split(" ");
					StringBuilder sbTaskName = new StringBuilder();
					for(int i=0; strTemp.length >0 && i<strTemp.length; i++) {
						if(strTemp[i].trim().length()>0) {
							sbTaskName.append(strTemp[i].trim()+" ");
						}
					}
					al.add(new FillTask(rs1.getString("task_id"), sbTaskName.toString().trim() +" [ST]" +" (Other)"));		
				}
			} else {
				if(rs1.getString("activity_name") != null && !rs1.getString("activity_name").equals("")) {
					String activityName = rs1.getString("activity_name").replace(".", ". ");
					activityName = activityName.replace(",", ", ");
					String strTemp[] = activityName.split(" ");
					StringBuilder sbTaskName = new StringBuilder();
					for(int i=0; strTemp.length >0 && i<strTemp.length; i++) {
						if(strTemp[i].trim().length()>0) {
							sbTaskName.append(strTemp[i].trim()+" ");
						}
					}
					al.add(new FillTask(rs1.getString("task_id"), sbTaskName.toString().trim() +" (Other)"));		
				}
			}
		}	
		rs1.close();
		pst.close();
		
		sbQuery1 = new StringBuilder();
		sbQuery1.append("select task_name from common_tasks");
		pst = con.prepareStatement(sbQuery1.toString());
		rs1 = pst.executeQuery();
		while (rs1.next()) {
			al.add(new FillTask(rs1.getString("task_name"), rs1.getString("task_name")));
		}	
		rs1.close();
		pst.close();
		
		/*al.add(new FillTask("a Research on New Project","a Research on New Project"));
		al.add(new FillTask("a Desk Job","a Desk Job"));
		al.add(new FillTask("a Call","a Call"));
		al.add(new FillTask("a Sales Call","a Sales Call"));
		al.add(new FillTask("a Conference Call","a Conference Call"));
		al.add(new FillTask("a Meeting with my Supervisor","a Meeting with my Supervisor"));
		al.add(new FillTask("a Meeting with my Subordinate","a Meeting with my Subordinate"));
		al.add(new FillTask("a Meeting with HR","a Meeting with HR"));
		al.add(new FillTask("a Team Meeting","a Team Meeting"));
		al.add(new FillTask("a Meeting with Client","a Meeting with Client"));
		al.add(new FillTask("a Client Demo","a Client Demo"));
		al.add(new FillTask("a Client Visit","a Client Visit"));
		al.add(new FillTask("a Field Visit","a Field Visit"));
		al.add(new FillTask("a Training Session","a Training Session"));
		
		al.add(new FillTask("GMCS 1","GMCS 1"));
		al.add(new FillTask("GMCS 2","GMCS 2"));
		al.add(new FillTask("ITT","ITT"));
		al.add(new FillTask("Exam Leave","Exam Leave"));
		al.add(new FillTask("Quality Control","Quality Control"));
		
		al.add(new FillTask("Other Activity","Other Activity"));
		al.add(new FillTask("Interview","Interview"));
		al.add(new FillTask("Training and Transition","Training and Transition"));
		
		al.add(new FillTask("Certification and Other Work","Certification and Other Work"));
		
		al.add(new FillTask("to Pantry","to Pantry"));
		al.add(new FillTask("a Coffee Break","a Coffee Break"));
		al.add(new FillTask("a Break","a Break"));*/
		
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		db.closeResultSet(rs1);
		db.closeStatements(pst);
		db.closeConnection(con);
	}
	return al;
}



	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	
	
	
}
