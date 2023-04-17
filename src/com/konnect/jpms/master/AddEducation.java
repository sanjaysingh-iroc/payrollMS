package com.konnect.jpms.master;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillWeightage;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;
 
public class AddEducation extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	CommonFunctions CF=null;
	HttpSession session;
//	String orgId;
	String strOrg;
	
	List<FillOrganisation> orgList;
	
	String strWeightage;
	List<FillWeightage> weightageList;
	
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
		weightageList = new FillWeightage().fillWeightage();
		
		if (operation!=null && operation.equals("D")) {
			return deleteEducation(strId); 
		}
		if (operation!=null && operation.equals("E")) { 
			return viewEducation(strId);
		}
		if (getEducationId()!=null && getEducationId().length()>0) { 
			return updateEducation();
		}
		
		
		if(getEducationName()!=null && getEducationName().length()>0){
			return insertEducation();
		}
		
		
		return LOAD;
		
	}

	public String loadValidateEducation() {
		return LOAD;
	}

	public String insertEducation() {

		Connection con = null;
		PreparedStatement pst = null; 
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("insert into educational_details (education_name, education_details, org_id,weightage) values (?,?,?,?)");
			pst.setString(1, getEducationName());
			pst.setString(2, getEducationDescription());
			pst.setInt(3, uF.parseToInt(getStrOrg()));
			pst.setInt(4, uF.parseToInt(getStrWeightage()));
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
	
	public String updateEducation() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		String updateSkill = "UPDATE educational_details SET education_name=?, education_details=?, org_id=?,weightage=? WHERE edu_id=?";
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(updateSkill);
			pst.setString(1, getEducationName());
			pst.setString(2, getEducationDescription());
			pst.setInt(3, uF.parseToInt(getStrOrg()));
			pst.setInt(4, uF.parseToInt(getStrWeightage()));
			pst.setInt(5, uF.parseToInt(getEducationId()));
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
	
	public String viewEducation(String strId) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		ResultSet rs = null;
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from educational_details where edu_id=?");
			pst.setInt(1, uF.parseToInt(strId));
			rs = pst.executeQuery();
			
			while(rs.next()){
				setEducationId(rs.getString("edu_id"));
				setEducationName(rs.getString("education_name"));
				setEducationDescription(rs.getString("education_details"));
				setStrOrg(rs.getString("org_id"));
				
				setStrWeightage(rs.getString("weightage"));
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
	
	public String deleteEducation(String strId) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("delete from educational_details where edu_id=?");
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

	String educationId;
	String educationName;
	String educationDescription;
	
	
	public String getEducationId() {
		return educationId;
	}

	public void setEducationId(String educationId) {
		this.educationId = educationId;
	}

	public String getEducationName() {
		return educationName;
	}

	public void setEducationName(String educationName) {
		this.educationName = educationName;
	}

	public String getEducationDescription() {
		return educationDescription;
	}

	public void setEducationDescription(String educationDescription) {
		this.educationDescription = educationDescription;
	}

	public void validate() {

		/*if (getBonusId() != null && getBonusId().length() == 0) {
			addFieldError("bonusId", "Bonus ID is required");
		}
		if (getBonusName() != null && getBonusName().length() == 0) {
			addFieldError("password", "Bonus Name is required");
		}
		if (getBonusCode() != null && getBonusCode().length() == 0) {
			addFieldError("bonusCode", "Bonus Code is required");
		}*/
		loadValidateEducation();

	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

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

	public String getStrWeightage() {
		return strWeightage;
	}

	public void setStrWeightage(String strWeightage) {
		this.strWeightage = strWeightage;
	}

	public List<FillWeightage> getWeightageList() {
		return weightageList;
	}

	public void setWeightageList(List<FillWeightage> weightageList) {
		this.weightageList = weightageList;
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