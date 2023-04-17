package com.konnect.jpms.master;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDesig;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;
 
public class AddGrade extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	public String execute() throws Exception {

		String operation = request.getParameter("operation");
		String strId = request.getParameter("ID");
		session = request.getSession();
		
		
		if (operation!=null && operation.equals("D")) {
			return deleteGrade(strId);  
		}
		if (operation!=null && operation.equals("E")) { 
			return viewGrade(strId);
		} 
		
		if (getGradeId()!=null && getGradeId().length()>0) {
				return updateGrade();
		}
		
		if(getGradeCode()!=null && getGradeCode().length()>0){
			return insertGrade();
		}
		
		loadValidateGrade();
		
		return LOAD;
		
	}

	public String loadValidateGrade() {
		
		desigList = new FillDesig(request).fillDesig();
		
		request.setAttribute("desigList", desigList);
		
		return LOAD;
	}

	public String insertGrade() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(insertGrade);
			pst.setString(1, getGradeCode());
			pst.setString(2, getGradeName());
			pst.setString(3, uF.showData(getGradeDesc(),""));
			pst.setInt(4, uF.parseToInt(getGradeDesig()));
			pst.execute();
			pst.close();
			
			session.setAttribute(MESSAGE, SUCCESSM+getGradeCode()+" saved successfully."+END);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	String orgId;
	
	public String viewGrade(String strEdit) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from grades_details where grade_id = ?");
			pst.setInt(1, uF.parseToInt(strEdit));
			rs = pst.executeQuery();

			int ndesignationId = 0;
			while(rs.next()){
				setGradeCode(rs.getString("grade_code"));
				setGradeName(rs.getString("grade_name"));
				setGradeDesc(rs.getString("grade_description"));
				setGradeId(rs.getString("grade_id"));
				
				ndesignationId = uF.parseToInt(rs.getString("designation_id"));
			}
			rs.close();
			pst.close();
			
			
			
			pst = con.prepareStatement("select * from level_details ld, designation_details dd where dd.level_id = ld.level_id and designation_id=?");
			pst.setInt(1, ndesignationId);
			rs = pst.executeQuery();
			while(rs.next()){
				setOrgId(rs.getString("org_id"));
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
	
	public String updateGrade() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		String updateGrade = "UPDATE grades_details SET grade_code=?, grade_name=?, grade_description=? WHERE grade_id=?";
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(updateGrade);
			pst.setString(1, getGradeCode());
			pst.setString(2, getGradeName());
			pst.setString(3, getGradeDesc());
			pst.setInt(4, uF.parseToInt(getGradeId()));
			pst.execute();
			pst.close();
			
			session.setAttribute(MESSAGE, SUCCESSM+getGradeCode()+" updated successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	
	public String deleteGrade(String strId) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(deleteGrade);
//			pst.setInt(1, uF.parseToInt(request.getParameter("id")));
			pst.setInt(1, uF.parseToInt(strId));
			pst.execute();
			pst.close();
			
			session.setAttribute(MESSAGE, SUCCESSM+"Deleted successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	String gradeId;
	String gradeCode;
	String gradeName;
	String gradeDesc;
	String gradeDesig;
	List<FillDesig> desigList;
	
	String userscreen;
	String navigationId;
	String toPage;
	
	public void validate() {

		request.setAttribute(PAGE, PAddGrade);
		request.setAttribute(TITLE, TGrade);
		
		
//		if (getGradeId() != null && getGradeId().length() == 0) {
//			addFieldError("gradeId", "Grade ID is required");
//		}
		if (getGradeName() != null && getGradeName().length() == 0) {
			addFieldError("password", "Grade Name is required");
		}
		if (getGradeCode() != null && getGradeCode().length() == 0) {
			addFieldError("gradeCode", "Grade Code is required");
		}
		loadValidateGrade();

	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getGradeId() {
		return gradeId;
	}

	public void setGradeId(String gradeId) {
		this.gradeId = gradeId;
	}

	public String getGradeName() {
		return gradeName;
	}

	public void setGradeName(String gradeName) {
		this.gradeName = gradeName;
	}

	public String getGradeDesc() {
		return gradeDesc;
	}

	public void setGradeDesc(String gradeDesc) {
		this.gradeDesc = gradeDesc;
	}

	public String getGradeCode() {
		return gradeCode;
	}

	public void setGradeCode(String gradeCode) {
		this.gradeCode = gradeCode;
	}

	public String getGradeDesig() {
		return gradeDesig;
	}

	public void setGradeDesig(String gradeDesig) {
		this.gradeDesig = gradeDesig;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
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