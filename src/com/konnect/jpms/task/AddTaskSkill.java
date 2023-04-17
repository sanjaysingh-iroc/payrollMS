package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillSkills;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddTaskSkill extends ActionSupport implements ServletRequestAware, IStatements {
	HttpServletRequest request;

	CommonFunctions CF; 
	HttpSession session;
	String strSessionEmpId;
	String strOrgId;
	
	String skill;
	String skilldesc; 
	String service_porject_id;
	String operation;
	String ID;
	String strSkillId;
	String strServiceId;

	List<FillSkills> skillList;

	String userscreen;
	String navigationId;
	String toPage;
	
	public String execute() throws Exception {
		
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strOrgId = (String)session.getAttribute(ORGID);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		
		request.setAttribute(PAGE, "/jsp/task/AddTaskService.jsp");
		request.setAttribute(TITLE, "Add New Project");
		
		skillList = new FillSkills(request).fillSkillsWithId();

		if(operation!=null) {
			if(operation.equals("E")) {
				getskill();
			} else if(operation.equals("D")) {
				deleteSkill();
				return "update";
			} else if(operation.equals("A")) {
				updateSkills();
				return "update";
			}
		} else {
			if(skill!=null) {
				setSkills();
				return "update";
			}
		}
		return SUCCESS;
	}
	
	public void getskill() {

				UtilityFunctions uF=new UtilityFunctions();
				Database db = new Database();
				db.setRequest(request);
				Connection con = null;
				PreparedStatement pst=null;
				ResultSet rs=null;
				try {
					con = db.makeConnection(con);
					pst = con.prepareStatement("select * from service_skills_details where service_skill_id=? ");
					pst.setInt(1, uF.parseToInt(ID));
					rs=pst.executeQuery();
					while(rs.next()) {
						skill = rs.getString("skill_id");
						skilldesc = rs.getString("skill_description");
					}
					rs.close();
					pst.close();
					
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					db.closeResultSet(rs);
					db.closeStatements(pst);
					db.closeConnection(con);
				}
		}
	
	
	public void updateSkills() {

		UtilityFunctions uF=new UtilityFunctions();
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst=null;

		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("update service_skills_details set skill_id=?, skill_description=?, updated_by=?, update_date=? where service_skill_id=?");
			pst.setInt(1, uF.parseToInt(getSkill()));
			pst.setString(2, skilldesc);
			pst.setInt(3, uF.parseToInt(strSessionEmpId));
			pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(5, uF.parseToInt(ID));
			pst.execute();
			pst.close();
			
			/*pst = con.prepareStatement("update skills_details set skill_name=?,skill_description=? where skill_id=?");
			pst.setString(1, skill);
			pst.setString(2, skilldesc);
			pst.setInt(3, uF.parseToInt(ID));
			pst.execute();*/
			
			pst = con.prepareStatement("update level_skill_rates set skill_id=? where skill_id=? and service_project_id=?");
			pst.setInt(1, uF.parseToInt(skill));
			pst.setInt(2, uF.parseToInt(strSkillId));
			pst.setInt(3, uF.parseToInt(strServiceId));
			pst.execute();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	
	
	public void deleteSkill() {
		UtilityFunctions uF=new UtilityFunctions();
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst=null;

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("delete from service_skills_details where service_skill_id = ?");
			pst.setInt(1, uF.parseToInt(ID));
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("delete from level_skill_rates where skill_id = ? and service_project_id=?");
			pst.setInt(1, uF.parseToInt(strSkillId));
			pst.setInt(2, uF.parseToInt(strServiceId));
			pst.execute();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	
	
	}
	
	public void setSkills() {

		UtilityFunctions uF=new UtilityFunctions();
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		
		try {

			con = db.makeConnection(con);
			boolean flag = false;
			pst = con.prepareStatement("select * from service_skills_details where skill_id=? and service_id=?");
			pst.setInt(1, uF.parseToInt(getSkill()));
			pst.setInt(2, uF.parseToInt(service_porject_id));
			rs = pst.executeQuery();
			while(rs.next()) {
				flag = true;
			}
			rs.close();
			pst.close();
			
			if(!flag) {
				pst = con.prepareStatement("insert into service_skills_details(skill_id,skill_description,service_id,added_by,entry_date) values(?,?,?,?, ?) ");
				pst.setInt(1, uF.parseToInt(getSkill()));
				pst.setString(2, skilldesc);
				pst.setInt(3, uF.parseToInt(service_porject_id));
				pst.setInt(4, uF.parseToInt(strSessionEmpId));
				pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.execute();
				pst.close();
			}
//			System.out.println("Skill ==> "+getSkill());
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	
	}

	public String getSkill() {
		return skill;
	}

	public void setSkill(String skill) {
		this.skill = skill;
	}

	public String getSkilldesc() {
		return skilldesc;
	}

	public void setSkilldesc(String skilldesc) {
		this.skilldesc = skilldesc;
	}

	public String getService_porject_id() {
		return service_porject_id;
	}

	public void setService_porject_id(String service_porject_id) {
		this.service_porject_id = service_porject_id;
	}

	public String getStrSkillId() {
		return strSkillId;
	}
	
	public void setStrSkillId(String strSkillId) {
		this.strSkillId = strSkillId;
	}
	
	public String getStrServiceId() {
		return strServiceId;
	}
	
	public void setStrServiceId(String strServiceId) {
		this.strServiceId = strServiceId;
	}
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
	}
	
	public List<FillSkills> getSkillList() {
		return skillList;
	}
	
	public void setSkillList(List<FillSkills> skillList) {
		this.skillList = skillList;
	}

	public String getUserscreen() {
		return userscreen;
	}

	public void setUserscreen(String userscreen) {
		this.userscreen = userscreen;
	}

	public String getNavigationId() {
		return navigationId;
	}

	public void setNavigationId(String navigationId) {
		this.navigationId = navigationId;
	}

	public String getToPage() {
		return toPage;
	}

	public void setToPage(String toPage) {
		this.toPage = toPage;
	}
	
}
