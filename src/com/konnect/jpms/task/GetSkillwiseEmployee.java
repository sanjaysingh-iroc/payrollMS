package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetSkillwiseEmployee extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;

	CommonFunctions CF; 
	HttpSession session;
	String strSessionUserId;
	String strOrgId;
	
	String proId;
	String skillId;
	String taskTRId;
	String type;
	String count;
	
	List<FillDependentTaskList> dependencyList;
	List<GetDependancyTypeList> dependancyTypeList;
	
	public String execute() throws Exception {
		session = request.getSession();
		strSessionUserId = (String)session.getAttribute(USERID);
		strOrgId = (String)session.getAttribute(ORGID);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		
//		System.out.println("getType() =====>> " + getType());
		
		UtilityFunctions uF = new UtilityFunctions();
		if(getType() != null && (getType().equals("Task") || getType().equals("VA_Task"))) {
			getTaskSkillEmppoyee(uF);
		} else if(getType() != null && (getType().equals("SubTask") || getType().equals("VA_SubTask"))) {
			getTaskSkillEmppoyee(uF);
		} else if(getType() != null && getType().equals("EditTask")) { 
			getTaskSkillEmppoyee(uF);
		}
		
		return SUCCESS;

	}


	private void getTaskSkillEmppoyee(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			con = db.makeConnection(con);
			
			StringBuilder sbquery = new StringBuilder();
			sbquery.append("select ped.emp_id, ped._isteamlead from skills_description sd, project_emp_details ped where ped.pro_id = ? and sd.emp_id = ped.emp_id ");
			if(uF.parseToInt(getSkillId()) > 0) {
				sbquery.append("and sd.skill_id = "+uF.parseToInt(getSkillId())+"");
			}
			pst = con.prepareStatement(sbquery.toString());
			pst.setInt(1, uF.parseToInt(getProId()));
//			System.out.println("pst ===>> " + pst);
			StringBuilder sbTaskSkillEmps = new StringBuilder();
			rs = pst.executeQuery();
			while(rs.next()) {
				sbTaskSkillEmps.append("<option value='"+rs.getString("emp_id")+"'>"+CF.getEmpNameMapByEmpId(con, rs.getString("emp_id")));
				if(rs.getBoolean("_isteamlead")) {
					sbTaskSkillEmps.append(" [TL]");
				}
				sbTaskSkillEmps.append("</option>");
			}
			rs.close();
			pst.close();
			
//			System.out.println("sbTaskSkillEmps ===>> " + sbTaskSkillEmps.toString());
			
			request.setAttribute("sbTaskSkillEmps", sbTaskSkillEmps.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	public String getProId() {
		return proId;
	}

	public void setProId(String proId) {
		this.proId = proId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public List<FillDependentTaskList> getDependencyList() {
		return dependencyList;
	}

	public void setDependencyList(List<FillDependentTaskList> dependencyList) {
		this.dependencyList = dependencyList;
	}

	public String getSkillId() {
		return skillId;
	}

	public void setSkillId(String skillId) {
		this.skillId = skillId;
	}

	public String getTaskTRId() {
		return taskTRId;
	}

	public void setTaskTRId(String taskTRId) {
		this.taskTRId = taskTRId;
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
}