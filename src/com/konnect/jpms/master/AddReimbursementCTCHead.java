package com.konnect.jpms.master;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddReimbursementCTCHead extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	CommonFunctions CF=null;
	HttpSession session;
	
	private String orgId;
	private String levelId;
	private String reimCTCId;
	private String financialYear;
	
	private String reimCTCHeadId;
	private String reimCTCHeadCode;
	private String reimCTCHeadName;
	private String reimCTCHeadDesc;
	private String reimCTCHeadAmount;
	private boolean attachment;
	private boolean strIsOptimal; 	

	List<FillFinancialYears> financialYearList; 
	
	String userscreen;
	String navigationId;
	String toPage;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;

		UtilityFunctions uF = new UtilityFunctions();
		String operation = request.getParameter("operation");
		String strId = request.getParameter("ID");
		
		if (operation!=null && operation.equals("D")) {
			return deleteReimbursementCTCHead(uF,strId);
		}
		
		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		
		if (operation!=null && operation.equals("E")) { 
			return viewReimbursementCTCHead(uF,strId);
		}
		
		if (getReimCTCHeadId()!=null && getReimCTCHeadId().length()>0) { 
			return updateReimbursementCTCHead(uF);
		}
		
		if(getReimCTCHeadCode()!=null && getReimCTCHeadCode().length()>0){
			return insertReimbursementCTCHead(uF);
		}
		return LOAD;
	}

	public String insertReimbursementCTCHead(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			if (getFinancialYear() != null && !getFinancialYear().trim().equals("") && !getFinancialYear().trim().equalsIgnoreCase("NULL")) {
				String[] strFinancialYear = getFinancialYear().trim().split("-");
				String strFinancialYearStart = strFinancialYear[0];
				String strFinancialYearEnd = strFinancialYear[1];				
				 
			
				pst = con.prepareStatement("INSERT INTO reimbursement_head_details (reimbursement_head_code,reimbursement_head_name," +
						"reimbursement_head_description,added_by,entry_date,reimbursement_ctc_id,level_id,org_id) VALUES (?,?,?,?, ?,?,?,?)");
				pst.setString(1, getReimCTCHeadCode());
				pst.setString(2, getReimCTCHeadName());
				pst.setString(3, getReimCTCHeadDesc());
				pst.setInt(4, uF.parseToInt((String)session.getAttribute(EMPID)));
				pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));				
				pst.setInt(6, uF.parseToInt(getReimCTCId()));
				pst.setInt(7, uF.parseToInt(getLevelId()));				
				pst.setInt(8, uF.parseToInt(getOrgId()));
				int x = pst.executeUpdate();
				pst.close();
				if(x > 0){
					int nReimCTCHeadId = 0;
					pst = con.prepareStatement("select max(reimbursement_head_id) as reimbursement_head_id from reimbursement_head_details");
					rs=pst.executeQuery();
					while(rs.next()){
						nReimCTCHeadId = uF.parseToInt(rs.getString("reimbursement_head_id"));
					}
					rs.close();
					pst.close();
					
					if(nReimCTCHeadId > 0){
						pst = con.prepareStatement("INSERT INTO reimbursement_head_amt_details (amount,reimbursement_head_id,financial_year_start," +
								"financial_year_end,is_attachment,is_optimal) VALUES (?,?,?,?, ?,?)");
						pst.setInt(1, uF.parseToInt(getReimCTCHeadAmount()));
						pst.setInt(2, nReimCTCHeadId);
						pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
						pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
						pst.setBoolean(5, getAttachment());
						pst.setBoolean(6, getStrIsOptimal());
						pst.execute();
						pst.close();
					}					
					session.setAttribute(MESSAGE, SUCCESSM+"Reimbursement CTC Head saved successfully."+END);
				} else {
					session.setAttribute(MESSAGE, ERRORM+"Reimbursement CTC Head not saved. Please try again!"+END);
				}
			}
		} catch (Exception e) {
			session.setAttribute(MESSAGE, ERRORM+"Reimbursement CTC Head not saved. Please try again!"+END);
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	
	public String viewReimbursementCTCHead(UtilityFunctions uF, String strId) {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from reimbursement_head_details where reimbursement_head_id=?");
			pst.setInt(1, uF.parseToInt(strId));
			rs = pst.executeQuery();
			while(rs.next()){
				setReimCTCHeadId(rs.getString("reimbursement_head_id"));
				setReimCTCHeadCode(rs.getString("reimbursement_head_code"));
				setReimCTCHeadName(rs.getString("reimbursement_head_name"));
				setReimCTCHeadDesc(rs.getString("reimbursement_head_description"));
			}
			rs.close();
			pst.close();
			
			if(uF.parseToInt(strId) > 0){
				pst = con.prepareStatement("select * from reimbursement_head_amt_details where reimbursement_head_id=? order by financial_year_start desc");
				pst.setInt(1, uF.parseToInt(strId));
				rs = pst.executeQuery();
				List<Map<String, String>> alReimHeadAmt = new ArrayList<Map<String,String>>();
				while(rs.next()){
					Map<String, String> hmInner = new HashMap<String, String>();
					hmInner.put("AMOUNT",uF.showData(rs.getString("amount"), "0"));
					hmInner.put("FINANCIAL_YEAR",uF.getDateFormat(rs.getString("financial_year_start"), DBDATE, CF.getStrReportDateFormat()) + " to "
							+ uF.getDateFormat(rs.getString("financial_year_end"), DBDATE, CF.getStrReportDateFormat()));
					hmInner.put("ATTACHMENT",uF.showYesNo(rs.getString("is_attachment")));
					hmInner.put("IS_OPTIMAL",uF.showYesNo(rs.getString("is_optimal")));
					hmInner.put("FINANCIAL_YEAR_DATE_FORMAT",uF.getDateFormat(rs.getString("financial_year_start"), DBDATE, DATE_FORMAT)+"-"+uF.getDateFormat(rs.getString("financial_year_end"), DBDATE, DATE_FORMAT));
					
					alReimHeadAmt.add(hmInner);
				}
				rs.close();
				pst.close();
				
				request.setAttribute("alReimHeadAmt", alReimHeadAmt);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return UPDATE;

	}
	
	public String updateReimbursementCTCHead(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);

			if (getFinancialYear() != null && !getFinancialYear().trim().equals("") && !getFinancialYear().trim().equalsIgnoreCase("NULL")) {
				String[] strFinancialYear = getFinancialYear().trim().split("-");
				String strFinancialYearStart = strFinancialYear[0];
				String strFinancialYearEnd = strFinancialYear[1];
			
				pst = con.prepareStatement("update reimbursement_head_details set reimbursement_head_code=?,reimbursement_head_name=?," +
						"reimbursement_head_description=?,added_by=?,entry_date=? where reimbursement_ctc_id=? and level_id=? and org_id=? " +
						"and reimbursement_head_id=?");
				pst.setString(1, getReimCTCHeadCode());
				pst.setString(2, getReimCTCHeadName());
				pst.setString(3, getReimCTCHeadDesc());
				pst.setInt(4, uF.parseToInt((String)session.getAttribute(EMPID)));
				pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));				
				pst.setInt(6, uF.parseToInt(getReimCTCId()));
				pst.setInt(7, uF.parseToInt(getLevelId()));				
				pst.setInt(8, uF.parseToInt(getOrgId()));
				pst.setInt(9, uF.parseToInt(getReimCTCHeadId()));
				int x = pst.executeUpdate();
				pst.close();
				if(x > 0){
					
					pst = con.prepareStatement("update reimbursement_head_amt_details set amount=?,is_attachment=?,is_optimal=? " +
							"where reimbursement_head_id=? and financial_year_start=? and financial_year_end=?");
					pst.setInt(1, uF.parseToInt(getReimCTCHeadAmount()));
					pst.setBoolean(2, getAttachment());
					pst.setBoolean(3, getStrIsOptimal());
					pst.setInt(4, uF.parseToInt(getReimCTCHeadId()));
					pst.setDate(5, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(6, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					int y = pst.executeUpdate();
					pst.close();
					
					if(y == 0){
						pst = con.prepareStatement("INSERT INTO reimbursement_head_amt_details (amount,reimbursement_head_id,financial_year_start," +
								"financial_year_end,is_attachment,is_optimal) VALUES (?,?,?,?, ?,?)");
						pst.setInt(1, uF.parseToInt(getReimCTCHeadAmount()));
						pst.setInt(2, uF.parseToInt(getReimCTCHeadId()));
						pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
						pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
						pst.setBoolean(5, getAttachment());
						pst.setBoolean(6, getStrIsOptimal());
						pst.execute();
						pst.close();
					}
					
					session.setAttribute(MESSAGE, SUCCESSM+"Reimbursement CTC Head updated successfully."+END);
				} else {
					session.setAttribute(MESSAGE, ERRORM+"Reimbursement CTC Head not updated. Please try again!"+END);
				}
			}
			
		} catch (Exception e) {
			session.setAttribute(MESSAGE, ERRORM+"Reimbursement CTC Head not updated. Please try again!"+END);
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	
	public String deleteReimbursementCTCHead(UtilityFunctions uF, String strId) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("delete from reimbursement_head_details where reimbursement_head_id=?");
			pst.setInt(1, uF.parseToInt(strId));
			pst.execute();
			pst.close();
			
			session.setAttribute(MESSAGE, SUCCESSM+"Reimbursement CTC Head deleted successfully."+END);
			
		} catch (Exception e) {
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

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getLevelId() {
		return levelId;
	}

	public void setLevelId(String levelId) {
		this.levelId = levelId;
	}

	public String getFinancialYear() {
		return financialYear;
	}

	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}

	public boolean getAttachment() {
		return attachment;
	}

	public void setAttachment(boolean attachment) {
		this.attachment = attachment;
	}

	public boolean getStrIsOptimal() {
		return strIsOptimal;
	}

	public void setStrIsOptimal(boolean strIsOptimal) {
		this.strIsOptimal = strIsOptimal;
	}

	public String getReimCTCId() {
		return reimCTCId;
	}

	public void setReimCTCId(String reimCTCId) {
		this.reimCTCId = reimCTCId;
	}

	public String getReimCTCHeadId() {
		return reimCTCHeadId;
	}

	public void setReimCTCHeadId(String reimCTCHeadId) {
		this.reimCTCHeadId = reimCTCHeadId;
	}

	public String getReimCTCHeadCode() {
		return reimCTCHeadCode;
	}

	public void setReimCTCHeadCode(String reimCTCHeadCode) {
		this.reimCTCHeadCode = reimCTCHeadCode;
	}

	public String getReimCTCHeadName() {
		return reimCTCHeadName;
	}

	public void setReimCTCHeadName(String reimCTCHeadName) {
		this.reimCTCHeadName = reimCTCHeadName;
	}

	public String getReimCTCHeadDesc() {
		return reimCTCHeadDesc;
	}

	public void setReimCTCHeadDesc(String reimCTCHeadDesc) {
		this.reimCTCHeadDesc = reimCTCHeadDesc;
	}

	public String getReimCTCHeadAmount() {
		return reimCTCHeadAmount;
	}

	public void setReimCTCHeadAmount(String reimCTCHeadAmount) {
		this.reimCTCHeadAmount = reimCTCHeadAmount;
	}

	public List<FillFinancialYears> getFinancialYearList() {
		return financialYearList;
	}

	public void setFinancialYearList(List<FillFinancialYears> financialYearList) {
		this.financialYearList = financialYearList;
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