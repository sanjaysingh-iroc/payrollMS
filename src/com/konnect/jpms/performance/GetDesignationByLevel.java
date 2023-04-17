package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDesig;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetDesignationByLevel extends ActionSupport implements ServletRequestAware{

	private String strLevel;
	private String strOrg;
	private List<FillDesig> desigList;
	private String page;
	
	private static final long serialVersionUID = 1L;

	public String execute() {
		UtilityFunctions uF = new UtilityFunctions();
		String strLevels = getOrgwiseLevelId(uF, getStrOrg());
		desigList = new FillDesig(request).fillDesigFromLevel(strLevels);
		
		return SUCCESS;
		
	}
	private String getOrgwiseLevelId(UtilityFunctions uF, String strOrgId) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		StringBuilder sbLevelIds = null;
		
		try { 

			con = db.makeConnection(con);
			
			if(getStrLevel() != null && uF.parseToInt(getStrLevel().trim())> 0) {
				sbLevelIds = new StringBuilder();
				sbLevelIds.append(getStrLevel().trim());
			} else {
				if(uF.parseToInt(strOrgId)> 0) {
					pst = con.prepareStatement("select level_id from level_details where org_id = ?");
					pst.setInt(1, uF.parseToInt(strOrgId));
				} else {
					pst = con.prepareStatement("select level_id from level_details");
				}
				rs = pst.executeQuery();			
				while(rs.next()){
					if(sbLevelIds == null) {
						sbLevelIds = new StringBuilder();
						sbLevelIds.append(rs.getString("level_id"));
					} else {
						sbLevelIds.append("," + rs.getString("level_id"));
					}
				}
				rs.close();
		        pst.close();
			}
			if(sbLevelIds == null) {
				sbLevelIds = new StringBuilder();
			}
           
			
		} catch (Exception e) {
			e.printStackTrace();			
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		return sbLevelIds.toString();
	}

	public String getStrLevel() {
		return strLevel;
	}

	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public List<FillDesig> getDesigList() {
		return desigList;
	}

	public void setDesigList(List<FillDesig> desigList) {
		this.desigList = desigList;
	}

	HttpServletRequest request;
	
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
	
}
