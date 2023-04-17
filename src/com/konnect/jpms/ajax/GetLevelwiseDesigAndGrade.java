package com.konnect.jpms.ajax;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDesig;
import com.konnect.jpms.select.FillGrade;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetLevelwiseDesigAndGrade extends ActionSupport implements ServletRequestAware{

	
	List<FillDesig> desigList;
	List<FillGrade> gradeList;
	
	String type;
	String strLevelId;
	String page;
	
	private static final long serialVersionUID = 1L;

	public String execute() {
	
		UtilityFunctions uF = new UtilityFunctions();
		
		desigList = new FillDesig(request).fillDesigFromLevel(getStrLevelId());
		String strDesigs = getOrgwiseDesigId(uF, getStrLevelId());
		gradeList = new FillGrade(request).fillGradeFromMultipleDesignation(strDesigs);
		
		return SUCCESS;
		
	}

	private String getOrgwiseDesigId(UtilityFunctions uF, String strLevels) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		StringBuilder sbDesigIds = null;
		
		try {

			con = db.makeConnection(con);
			if(strLevels != null && !strLevels.equals("")) {
				pst = con.prepareStatement("select designation_id from designation_details where level_id in ("+strLevels+")");
			} else {
				pst = con.prepareStatement("select designation_id from designation_details");
			}
			rs = pst.executeQuery();			
			while(rs.next()) {
				if(sbDesigIds == null) {
					sbDesigIds = new StringBuilder();
					sbDesigIds.append(rs.getString("designation_id"));
				} else {
					sbDesigIds.append("," + rs.getString("designation_id"));
				}
			}
            rs.close();
            pst.close();
			if(sbDesigIds != null) {
//				System.out.println("sbDesigIds ===> " + sbDesigIds.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();			
		}finally{
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
	
	public List<FillDesig> getDesigList() {
		return desigList;
	}
	
	public void setDesigList(List<FillDesig> desigList) {
		this.desigList = desigList;
	}
	
	public List<FillGrade> getGradeList() {
		return gradeList;
	}
	
	public void setGradeList(List<FillGrade> gradeList) {
		this.gradeList = gradeList;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	public String getStrLevelId() {
		return strLevelId;
	}

	public void setStrLevelId(String strLevelId) {
		this.strLevelId = strLevelId;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}
	
}
