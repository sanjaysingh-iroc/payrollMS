package com.konnect.jpms.master;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillSalaryHeads;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddAlignSalaryHead extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	String strUserType;  
	String strSessionEmpId; 
	
	String strOrg;
	String productionLineId;
	String strLevel;
	List<String> strSalaryHeadId;
	String strAlignSalaryHeadId;
	
	List<FillLevel> levelList;
	List<FillSalaryHeads> salaryHeadList;
	
	String userscreen;
	String navigationId;
	String toPage;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null){
			return LOGIN;
		}
		
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId =  (String)session.getAttribute(EMPID);	
		
		String operation = request.getParameter("operation");
		String strId = request.getParameter("ID");
		
		UtilityFunctions uF = new UtilityFunctions();
		
		if (operation != null && operation.equals("D")) {
			return deleteAlignSalaryHead(uF, strId);
		}
		if (operation != null && operation.equals("E")) {
			return viewAlignSalaryHead(uF, strId);
		}
		
		if (uF.parseToInt(getStrAlignSalaryHeadId()) >0) {
			return updateProductionLine(uF);
		}
		if (uF.parseToInt(getStrLevel()) >0) {
			return insertAlignSalaryHead(uF);
		}
		
		return loadAlignSalaryHead(uF);
	}

	private String loadAlignSalaryHead(UtilityFunctions uF) {
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getStrOrg()));
		salaryHeadList = new ArrayList<FillSalaryHeads>();
		
		return LOAD;
	}

	public String insertAlignSalaryHead(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			StringBuilder sbSalaryHeads = null;
			for(int i = 0; getStrSalaryHeadId() != null &&  i < getStrSalaryHeadId().size(); i++){
				if(uF.parseToInt(getStrSalaryHeadId().get(i).trim()) > 0){
					if(sbSalaryHeads == null){
						sbSalaryHeads = new StringBuilder();
						sbSalaryHeads.append(","+getStrSalaryHeadId().get(i).trim()+",");
					} else {
						sbSalaryHeads.append(getStrSalaryHeadId().get(i).trim()+",");	
					}						
				}
			}
			
			pst = con.prepareStatement("insert into production_line_heads(production_line_id,level_id,salary_heads,added_by,added_date)" +
					"values(?,?,?,?, ?)");
			pst.setInt(1, uF.parseToInt(getProductionLineId()));
			pst.setInt(2, uF.parseToInt(getStrLevel()));
			pst.setString(3, sbSalaryHeads != null ? sbSalaryHeads.toString() : null);
			pst.setInt(4, uF.parseToInt(strSessionEmpId));
			pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.execute();
			pst.close();

			session.setAttribute(MESSAGE, SUCCESSM+"saved successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(MESSAGE, "Error in insertion");
			return ERROR;
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}

	
	public String viewAlignSalaryHead(UtilityFunctions uF, String strId) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);

			pst = con.prepareStatement("select * from production_line_heads where production_line_head_id=?");
			pst.setInt(1, uF.parseToInt(strId));
			rs = pst.executeQuery();			
			while(rs.next()){
				setStrAlignSalaryHeadId(rs.getString("production_line_head_id"));
				setProductionLineId(rs.getString("production_line_id"));
				setStrLevel(rs.getString("level_id"));
				
				if (rs.getString("salary_heads")!=null){
					setStrSalaryHeadId(Arrays.asList(rs.getString("salary_heads").split(",")));
				}
			}
			rs.close();
			pst.close();	
			
			pst = con.prepareStatement("select * from level_details where level_id=?");
			pst.setInt(1, uF.parseToInt(getStrLevel()));
			rs = pst.executeQuery();			
			while(rs.next()){
				request.setAttribute("strLevelName",uF.showData(rs.getString("level_name"), ""));
			}
			rs.close();
			pst.close();
			
			if(uF.parseToInt(getStrLevel()) > 0) {
				salaryHeadList = new FillSalaryHeads(request).fillAllowanceSalaryHeadsByLevel(getStrLevel());
			} else {		
				salaryHeadList = new ArrayList<FillSalaryHeads>();
			}	
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return LOAD;
	}
	
	
	public String updateProductionLine(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);

			StringBuilder sbSalaryHeads = null;
			for(int i = 0; getStrSalaryHeadId() != null &&  i < getStrSalaryHeadId().size(); i++){
				if(uF.parseToInt(getStrSalaryHeadId().get(i).trim()) > 0){
					if(sbSalaryHeads == null){
						sbSalaryHeads = new StringBuilder();
						sbSalaryHeads.append(","+getStrSalaryHeadId().get(i).trim()+",");
					} else {
						sbSalaryHeads.append(getStrSalaryHeadId().get(i).trim()+",");	
					}						
				}
			}
			
			pst = con.prepareStatement("update production_line_heads set salary_heads=?,added_by=?,added_date=? " +
					"where production_line_id=? and level_id=?");
			pst.setString(1, sbSalaryHeads != null ? sbSalaryHeads.toString() : null);
			pst.setInt(2, uF.parseToInt(strSessionEmpId));
			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(4, uF.parseToInt(getProductionLineId()));
			pst.setInt(5, uF.parseToInt(getStrLevel()));
			pst.execute();
			pst.close();

			session.setAttribute(MESSAGE, SUCCESSM+" updated successfully."+END);
			
		} catch (Exception e) {
			request.setAttribute(MESSAGE, "Error in updation");
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;		
	}

	public String deleteAlignSalaryHead(UtilityFunctions uF, String strId) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("delete from production_line_heads where production_line_head_id=?");
			pst.setInt(1, uF.parseToInt(strId));
			pst.execute();
			pst.close();
			
			session.setAttribute(MESSAGE, SUCCESSM+"Deleted successfully."+END);
		} catch (Exception e) {
			request.setAttribute(MESSAGE, "Error in deletion");
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
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

	public String getProductionLineId() {
		return productionLineId;
	}

	public void setProductionLineId(String productionLineId) {
		this.productionLineId = productionLineId;
	}

	public String getStrLevel() {
		return strLevel;
	}

	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}

	public List<String> getStrSalaryHeadId() {
		return strSalaryHeadId;
	}

	public void setStrSalaryHeadId(List<String> strSalaryHeadId) {
		this.strSalaryHeadId = strSalaryHeadId;
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

	public String getStrAlignSalaryHeadId() {
		return strAlignSalaryHeadId;
	}

	public void setStrAlignSalaryHeadId(String strAlignSalaryHeadId) {
		this.strAlignSalaryHeadId = strAlignSalaryHeadId;
	}

	public List<FillSalaryHeads> getSalaryHeadList() {
		return salaryHeadList;
	}

	public void setSalaryHeadList(List<FillSalaryHeads> salaryHeadList) {
		this.salaryHeadList = salaryHeadList;
	}

	public List<FillLevel> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}
}