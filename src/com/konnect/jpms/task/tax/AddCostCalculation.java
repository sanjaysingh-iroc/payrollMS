package com.konnect.jpms.task.tax;

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

public class AddCostCalculation extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	String strSessionEmpId;
	
	String strOrg;
	String strOrgName;
	String costCalId;
	String costCalLabel;
	String monthday;
	String workingday;
	String fixedday;
	String costCalType;
	String fixdays;
	String fixArticaldays;
	String operation;
	
	List<FillOrganisation> orgList;
	
	private String userscreen;
	private String navigationId;
	private String toPage;
	
	public String execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		UtilityFunctions uF = new UtilityFunctions();
		
		String existOrgId = getExistOrgId();
		orgList = new FillOrganisation(request).fillOrganisationWithoutCurrentOrgId(existOrgId);
		
		if (operation!=null && operation.equals("D")) {
			return deleteCostCal(getCostCalId(), uF); 
		} 
		if (operation!=null && operation.equals("E")) { 
			return viewCostCal(getCostCalId(), uF);
		}
		if (getCostCalId() != null && getCostCalId().length()>0) { 
			return updateCostCal(uF);
		}
		
		if (getStrOrg() != null && getStrOrg().length()>0) {
			return insertCostCal(uF);
		}
		
		return LOAD;
	}

	
	private String getExistOrgId() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		StringBuilder sbOrgIds = null;
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select org_id from cost_calculation_settings ");
			rs = pst.executeQuery();
			while(rs.next()) {
				if(sbOrgIds == null) {
					sbOrgIds = new StringBuilder();
					sbOrgIds.append(rs.getString("org_id"));
				} else {
					sbOrgIds.append(","+rs.getString("org_id"));
				}
			}
			rs.close();
			pst.close();
			if(sbOrgIds == null) {
				sbOrgIds = new StringBuilder();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return sbOrgIds.toString();

	}

	public String insertCostCal(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("INSERT INTO cost_calculation_settings(org_id,calculation_type,calculation_type_label,days,artical_days,added_by,entry_date) " +
					"VALUES (?,?,?,?, ?,?,?)");
			pst.setInt(1, uF.parseToInt(getStrOrg()));
			pst.setInt(2, uF.parseToInt(getCostCalType()));
			if(uF.parseToInt(getCostCalType()) == 1) {
				pst.setString(3, getMonthday());
			} else if(uF.parseToInt(getCostCalType()) == 2) {
				pst.setString(3, getWorkingday());
			} else if(uF.parseToInt(getCostCalType()) == 3) {
				pst.setString(3, getFixedday());
			}
			pst.setDouble(4, uF.parseToDouble(getFixdays()));
			pst.setDouble(5, uF.parseToDouble(getFixArticaldays()));
			pst.setInt(6, uF.parseToInt(strSessionEmpId));
			pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.execute();
			pst.close();

			session.setAttribute(MESSAGE, SUCCESSM+CF.getOrgNameById(con, getStrOrg())+" cost calculation saved successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	
	
	
	public String viewCostCal(String strId, UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from cost_calculation_settings where cost_calculation_id=?");
			pst.setInt(1, uF.parseToInt(strId));
			rs = pst.executeQuery();
			while(rs.next()) {
				setCostCalType(rs.getString("calculation_type"));
				setFixdays(rs.getString("days"));
				setFixArticaldays(rs.getString("artical_days"));
				setStrOrg(rs.getString("org_id"));
				setStrOrgName(CF.getOrgNameById(con, rs.getString("org_id")));
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
		return LOAD;

	}
	
	

	public String updateCostCal(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("update cost_calculation_settings set calculation_type=?,calculation_type_label=?,days=?,artical_days=?,updated_by=?," +
					"update_date=? where cost_calculation_id=?");
			pst.setInt(1, uF.parseToInt(getCostCalType()));
			if(uF.parseToInt(getCostCalType()) == 1) {
				pst.setString(2, getMonthday());
			} else if(uF.parseToInt(getCostCalType()) == 2) {
				pst.setString(2, getWorkingday());
			} else if(uF.parseToInt(getCostCalType()) == 3) {
				pst.setString(2, getFixedday());
			}
			pst.setDouble(3, uF.parseToDouble(getFixdays()));
			pst.setDouble(4, uF.parseToDouble(getFixArticaldays()));
			pst.setInt(5, uF.parseToInt(strSessionEmpId));
			pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(7, uF.parseToInt(getCostCalId()));
			pst.executeUpdate();
			pst.close();
			
			session.setAttribute(MESSAGE, SUCCESSM+CF.getOrgNameById(con, getStrOrg())+" cost calculation updated successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
//		if("LeaveTypeReport.action".equalsIgnoreCase(request.getParameter("URI"))){
//			return "success_redirect";
//		}
		return SUCCESS;

	}
	
	public String deleteCostCal(String strId,UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("delete from cost_calculation_settings where cost_calculation_id=?");
			pst.setInt(1, uF.parseToInt(strId));
			pst.execute();
			pst.close();
			
			session.setAttribute(MESSAGE, SUCCESSM+"Cost calculation deleted successfully."+END);
			
			//Delete Salary Heads related to the level.
			
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
//		if("LeaveTypeReport.action".equalsIgnoreCase(request.getParameter("URI"))){
//			return "success_redirect";
//		}
		return SUCCESS;

	}


	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getCostCalId() {
		return costCalId;
	}

	public void setCostCalId(String costCalId) {
		this.costCalId = costCalId;
	}

	public String getCostCalLabel() {
		return costCalLabel;
	}

	public void setCostCalLabel(String costCalLabel) {
		this.costCalLabel = costCalLabel;
	}

	public String getMonthday() {
		return monthday;
	}

	public void setMonthday(String monthday) {
		this.monthday = monthday;
	}

	public String getWorkingday() {
		return workingday;
	}

	public void setWorkingday(String workingday) {
		this.workingday = workingday;
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

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getCostCalType() {
		return costCalType;
	}

	public void setCostCalType(String costCalType) {
		this.costCalType = costCalType;
	}

	public String getStrOrgName() {
		return strOrgName;
	}

	public void setStrOrgName(String strOrgName) {
		this.strOrgName = strOrgName;
	}

	public String getFixedday() {
		return fixedday;
	}

	public void setFixedday(String fixedday) {
		this.fixedday = fixedday;
	}

	public String getFixdays() {
		return fixdays;
	}

	public void setFixdays(String fixdays) {
		this.fixdays = fixdays;
	}


	public String getFixArticaldays() {
		return fixArticaldays;
	}


	public void setFixArticaldays(String fixArticaldays) {
		this.fixArticaldays = fixArticaldays;
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