package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class TeamMembers extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	String strUsertype;
	String strSessionEmpId;

	String type;
	 
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) return LOGIN;

		strUsertype = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		request.setAttribute(PAGE, "/jsp/task/TeamMembers.jsp");
		UtilityFunctions uF = new UtilityFunctions();

		request.setAttribute("arrEnabledModules", CF.getArrEnabledModules());
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());

		if(getType() != null && getType().equals("MyTeam")) {
			getMyTeam(uF);
		} else {
			getTeamMembers(uF);
		}
		
		return SUCCESS;

	}

	
	private void getTeamMembers(UtilityFunctions uF) {
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			StringBuilder sbQuery = new StringBuilder();
		//===start parvez date: 15-10-2022===	
//			sbQuery.append("select pro_id from projectmntnc where project_owner=? and approve_status='n' order by pro_id");
			sbQuery.append("select pro_id from projectmntnc where project_owners like '%,"+strSessionEmpId+",%' and approve_status='n' order by pro_id");
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setInt(1, uF.parseToInt(strSessionEmpId));
		//===end parvez date: 15-10-2022===	
			rs = pst.executeQuery();
			Set<String> setProId = new HashSet<String>();
			while(rs.next()) {
				setProId.add(rs.getString("pro_id"));
			}
			rs.close();
			pst.close();
			
			sbQuery = new StringBuilder();
			sbQuery.append("select pmt.pro_id from project_emp_details ped,projectmntnc pmt where ped.pro_id = pmt.pro_id " +
					"and ped.emp_id=? and pmt.approve_status='n' order by pmt.pro_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			rs = pst.executeQuery();
			while(rs.next()) {
				setProId.add(rs.getString("pro_id"));
			}
			rs.close();
			pst.close();
			
			StringBuilder sbProId = null;
			for(String strProId : setProId) {
				if(sbProId == null) {
					sbProId = new StringBuilder();
					sbProId.append(strProId);
				} else {
					sbProId.append(","+strProId);
				}
			}
			
			List<Map<String, String>> alTeamMember = new ArrayList<Map<String, String>>();
			if(sbProId !=null) {
				sbQuery = new StringBuilder();
				sbQuery.append("select * from activity_info where pro_id in("+sbProId.toString()+") and resource_ids like '%,"+strSessionEmpId+",%'");
				pst = con.prepareStatement(sbQuery.toString());
				rs = pst.executeQuery();
				boolean flagTask = false;
				if(rs.next()) {
					flagTask = true;
				}
				rs.close();
				pst.close();
				
				sbQuery = new StringBuilder();
				sbQuery.append("select epd.* from (select distinct(ped.emp_id) as emp_id from project_emp_details ped,projectmntnc pmt " +
						"where ped.pro_id = pmt.pro_id and ped.pro_id in("+sbProId.toString()+")) a, employee_personal_details epd," +
						"employee_official_details eod where a.emp_id=epd.emp_per_id and a.emp_id=eod.emp_id and epd.emp_per_id=eod.emp_id " +
						"and epd.is_alive=true ");
				if(!flagTask) {
					sbQuery.append(" and eod.emp_id!="+uF.parseToInt(strSessionEmpId));
				}
				sbQuery.append(" order by epd.emp_fname,epd.emp_lname");
				pst = con.prepareStatement(sbQuery.toString());
//				System.out.println("pst=====>"+pst);
				rs = pst.executeQuery();
				while(rs.next()) {
					
					String strEmpMName = "";
					if(flagMiddleName) {
						if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
							strEmpMName = " "+rs.getString("emp_mname");
						}
					}
				
					
					String strShortLName = (rs.getString("emp_lname") !=null && !rs.getString("emp_lname").trim().equals("")) ? String.valueOf(rs.getString("emp_lname").trim().charAt(0)) : "";
					String strEmpShortLName = uF.showData(rs.getString("emp_fname"), "")+" "+strShortLName.toUpperCase();
					
					//String strMiddleName=(rs.getString("emp_mname")!=null && !rs.getString("emp_mname").trim().equals("")) ? rs.getString("emp_mname").trim()+" " : "";
					
					String strEmpName = rs.getString("emp_fname") + strEmpMName+" "+ rs.getString("emp_lname");
					String strEmpImage = uF.showData(rs.getString("emp_image"), "");
					
					Map<String, String> hmInner = new HashMap<String, String>();
					hmInner.put("EMP_ID", rs.getString("emp_per_id"));
					hmInner.put("EMP_SHORT_NAME", strEmpShortLName);
					hmInner.put("EMP_NAME", strEmpName); 
					hmInner.put("EMP_IMAGE", strEmpImage);
					
					alTeamMember.add(hmInner);
				}
				rs.close();
				pst.close();
			}
			request.setAttribute("alTeamMember", alTeamMember);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	public void getMyTeam(UtilityFunctions uF) {
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			Map<String, String> hmEmp = new HashMap<String, String>();
			Map<String,String> empImageMap=new HashMap<String,String>();
			pst=con.prepareStatement("select emp_per_id,emp_fname,emp_mname,emp_lname,emp_image from employee_personal_details epd join " +
        		"employee_official_details eod on (epd.emp_per_id = eod.emp_id) join department_info di on (depart_id=dept_id) " +
        		"join grades_details gd on eod.grade_id=gd.grade_id join designation_details dd on gd.designation_id=dd.designation_id " +
        		"join level_details ld on dd.level_id=ld.level_id join org_details od  on ld.org_id=od.org_id where  is_alive= true " +
        		" and emp_per_id >0 and supervisor_emp_id=? order by emp_id"); //(supervisor_emp_id=? or emp_per_id =?) 
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			rs=pst.executeQuery();
			while(rs.next()) {
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
			
				hmEmp.put(rs.getString("emp_per_id"), rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname"));
				empImageMap.put(rs.getString("emp_per_id"),rs.getString("emp_image"));
			}
			rs.close();
			pst.close();

			request.setAttribute("hmEmp", hmEmp);
			request.setAttribute("empImageMap", empImageMap);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

}
