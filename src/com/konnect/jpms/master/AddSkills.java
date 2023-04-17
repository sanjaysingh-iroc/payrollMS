package com.konnect.jpms.master;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;
 
public class AddSkills extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	CommonFunctions CF=null;
	HttpSession session;
//	String orgId;
	String strOrg; 
	
	List<FillOrganisation> orgList;
	
	String userscreen;
	String navigationId;
	String toPage;
	
	public String execute() throws Exception {
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;

		String operation = request.getParameter("operation");
		String strId = request.getParameter("ID");
		session = request.getSession();
		
		orgList = new FillOrganisation(request).fillOrganisation();
		
		if (operation!=null && operation.equals("D")) {
			return deleteSkill(strId);
		}
		if (operation!=null && operation.equals("E")) {
			return viewSkills(strId);
		}
		if (getSkillId()!=null && getSkillId().length()>0) {
			return updateSkill();
		}
		
		if(getSkilName()!=null && getSkilName().length()>0) {
			return insertSkill();
		}
		
		return LOAD;
	}


	public String insertSkill() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("insert into skills_details (skill_name, skill_description, org_id) values (?,?,?)");
			pst.setString(1, getSkilName());
			pst.setString(2, getSkilDescription());
			pst.setInt(3, uF.parseToInt(getStrOrg()));
			pst.execute();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	
	public String updateSkill() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		String updateSkill = "UPDATE skills_details SET skill_name=?, skill_description=?, org_id=? WHERE skill_id=?";
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(updateSkill);
			pst.setString(1, getSkilName());
			pst.setString(2, getSkilDescription());
			pst.setInt(3, uF.parseToInt(getStrOrg()));
			pst.setInt(4, uF.parseToInt(getSkillId()));
			pst.execute();
			pst.close();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	
	public String viewSkills(String strId) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		ResultSet rs = null;
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from skills_details where skill_id=?");
			pst.setInt(1, uF.parseToInt(strId));
			rs = pst.executeQuery();
			
			while(rs.next()){
				setSkillId(rs.getString("skill_id"));
				setSkilName(rs.getString("skill_name"));
				setSkilDescription(rs.getString("skill_description"));
				setStrOrg(rs.getString("org_id"));
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
		return UPDATE;

	}
	
	public String deleteSkill(String strId) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("delete from skills_details where skill_id=?");
			pst.setInt(1, uF.parseToInt(strId));
			pst.execute();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	String skillId;
	String skilName;
	String skilDescription;
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getSkillId() {
		return skillId;
	}

	public void setSkillId(String skillId) {
		this.skillId = skillId;
	}

	public String getSkilName() {
		return skilName;
	}

	public void setSkilName(String skilName) {
		this.skilName = skilName;
	}

	public String getSkilDescription() {
		return skilDescription;
	}

	public void setSkilDescription(String skilDescription) {
		this.skilDescription = skilDescription;
	}

	public String getStrOrg() {
		return strOrg;
	}

	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}

	public List<FillOrganisation> getOrgList() {
		return orgList;
	}

	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
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