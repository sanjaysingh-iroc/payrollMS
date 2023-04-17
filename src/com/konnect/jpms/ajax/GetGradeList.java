package com.konnect.jpms.ajax;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillGrade;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetGradeList extends ActionSupport implements ServletRequestAware{

	String strDesignation;
	String strDesignationUpdate;
	String strGrade;
	String strGradeUpdate;
	String count;
	String type;
	String levelId;
	String levelIds;
	String orgId;
	String fromPage;
	String typeEA;
	
	List<FillGrade> gradeList;
	
	private static final long serialVersionUID = 1L;

	public String execute() {
		
//		System.out.println("count ==> " + count);
		String strDesigId = request.getParameter("DId");
		try {
			if(getStrDesignationUpdate()!=null && getStrDesignationUpdate().length() > 0) {
				gradeList = new FillGrade(request).fillGradeFromDesignation(getStrDesignationUpdate());
			} else if(getStrDesignation()!=null && getStrDesignation().length() > 0) {
				if(getStrGradeUpdate()==null || getStrGradeUpdate().length() == 0) {
					setStrGradeUpdate(getStrGrade());
				}
				gradeList = new FillGrade(request).fillGradeFromDesignation(getStrDesignation());
			} else if(strDesigId!=null) {
				gradeList = new FillGrade(request).fillGradeFromDesignation(strDesigId);
			}
			
//			System.out.println("levelIds ===> " + levelIds);
			if(levelId != null && !levelId.equals("")) {
				String desigIds = getDesigIds(levelId);
//				System.out.println("desigIds ===>> " + desigIds);
				gradeList = new FillGrade(request).fillGradeFromMultipleDesignation(desigIds);
			} else if(levelIds != null && orgId != null) {
				gradeList = new FillGrade(request).fillGrade(levelIds, orgId);
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return SUCCESS;
		
	}


	private String getDesigIds(String levelId) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		StringBuilder sbDesigIds = new StringBuilder();
		try {

			con = db.makeConnection(con);
			
			pst = con.prepareStatement("SELECT designation_id FROM designation_details where level_id = ?");
			pst.setInt(1, uF.parseToInt(levelId));
			rs = pst.executeQuery();
//			System.out.println("pst selectGradeFromDesignation==>" + pst); 
			while (rs.next()) {
				if(sbDesigIds.toString().equals("")) {
				sbDesigIds.append(","+rs.getString("designation_id")+",");
				} else {
					sbDesigIds.append(rs.getString("designation_id")+",");
				}
			}
			rs.close();
			pst.close();
			
//			System.out.println("sbDesigIds ===>> " +sbDesigIds);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return sbDesigIds.toString();
	}
	
	HttpServletRequest request;
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public List<FillGrade> getGradeList() {
		return gradeList;
	}

	public void setGradeList(List<FillGrade> gradeList) {
		this.gradeList = gradeList;
	}

	public String getStrDesignation() {
		return strDesignation;
	}

	public void setStrDesignation(String strDesignation) {
		this.strDesignation = strDesignation;
	}

	public String getStrGrade() {
		return strGrade;
	}

	public void setStrGrade(String strGrade) {
		this.strGrade = strGrade;
	}

	public String getStrDesignationUpdate() {
		return strDesignationUpdate;
	}

	public void setStrDesignationUpdate(String strDesignationUpdate) {
		this.strDesignationUpdate = strDesignationUpdate;
	}

	public String getStrGradeUpdate() {
		return strGradeUpdate;
	}

	public void setStrGradeUpdate(String strGradeUpdate) {
		this.strGradeUpdate = strGradeUpdate;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLevelId() {
		return levelId;
	}

	public void setLevelId(String levelId) {
		this.levelId = levelId;
	}

	public String getLevelIds() {
		return levelIds;
	}

	public void setLevelIds(String levelIds) {
		this.levelIds = levelIds;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	public String getTypeEA() {
		return typeEA;
	}

	public void setTypeEA(String typeEA) {
		this.typeEA = typeEA;
	}
}