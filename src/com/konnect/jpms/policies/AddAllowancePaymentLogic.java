package com.konnect.jpms.policies;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillAllowanceCondition;
import com.konnect.jpms.select.FillAllowancePaymentLogic;
import com.konnect.jpms.select.FillSalaryHeads;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddAllowancePaymentLogic extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	
	CommonFunctions CF;
	HttpSession session;
	
	String strOrg;
	String strLevel;
	String strSalaryHeadId;
	
	String paymentLogicId;
	String strPaymentLogicSlab;
	String strAllowanceConditionSlab;
	String strAllowancePaymentLogic;
	
	String strFixedAmt;
	String strCalsalaryHead;
	
	List<FillAllowanceCondition> allowanceConditionSlabList;
	List<FillAllowancePaymentLogic> allowancePaymentLogicList;
	List<FillSalaryHeads> salaryHeadList;
	
	String btnSubmitPublish;
	String strPublish;
	String effectiveDate;
	String strPerHourDayAmt;
	
	String userscreen;
	String navigationId;
	String toPage;
	
	boolean isDeductFullAmount;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		UtilityFunctions uF = new UtilityFunctions();
		String operation = request.getParameter("operation");
		String strId = request.getParameter("ID");

		loadAllowancePaymentLogic(uF);
		
		if (operation!=null && operation.equals("D")) {
			return deleteAllowancePaymentLogic(uF,strId);
		} 
		if (operation!=null && operation.equals("E")) {
			return viewAllowancePaymentLogic(uF,strId);
		}

		if (getPaymentLogicId()!=null && getPaymentLogicId().length()>0 && !getPaymentLogicId().equalsIgnoreCase("NULL")) {
			return updateAllowancePaymentLogic(uF);
		}
		if (getStrPaymentLogicSlab() != null && !getStrPaymentLogicSlab().trim().equals("") && !getStrPaymentLogicSlab().trim().equalsIgnoreCase("NULL")) {
			return insertAllowancePaymentLogic(uF);
		}
		
		return LOAD;
		
	}

	private String deleteAllowancePaymentLogic(UtilityFunctions uF, String strId) {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs= null;
		Database db = new Database();
		db.setRequest(request);
		
		try {

			con = db.makeConnection(con);
			
			pst = con.prepareStatement("delete from allowance_payment_logic where payment_logic_id=?");
			pst.setInt(1, uF.parseToInt(strId));
			pst.execute();
			pst.close();
			
			session.setAttribute(MESSAGE, SUCCESSM+"Allowance payment logic deleted successfully."+END);
		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(MESSAGE, ERRORM+"Allowance payment logic could not be deleted. Please try again."+END);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}

	private String viewAllowancePaymentLogic(UtilityFunctions uF, String strId) {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs= null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select * from allowance_payment_logic where payment_logic_id=?");
			pst.setInt(1, uF.parseToInt(strId));
			rs = pst.executeQuery();
			String strConditionId = null;
			while(rs.next()){
				setPaymentLogicId(rs.getString("payment_logic_id"));
				setStrPaymentLogicSlab(uF.showData(rs.getString("payment_logic_slab"),""));
				setStrAllowanceConditionSlab(rs.getString("allowance_condition_id"));
				setStrAllowancePaymentLogic(rs.getString("payment_logic"));
				setStrFixedAmt(""+uF.parseToDouble(rs.getString("fixed_amount")));
				setStrCalsalaryHead(rs.getString("cal_salary_head_id"));
				setStrPublish(rs.getString("is_publish"));
				
				strConditionId = rs.getString("allowance_condition_id");
				
				setEffectiveDate(uF.getDateFormat(rs.getString("effective_date"), DBDATE, DATE_FORMAT));
				setStrPerHourDayAmt(""+uF.parseToDouble(rs.getString("per_hour_day")));
				setIsDeductFullAmount(uF.parseToBoolean(rs.getString("is_deduct_full_amount")));
			}
			rs.close();
			pst.close();
			
			if(uF.parseToInt(strConditionId) > 0){
				pst = con.prepareStatement("select allowance_condition from allowance_condition_details where allowance_condition_id=?");
				pst.setInt(1, uF.parseToInt(strConditionId));
				rs = pst.executeQuery();
				int nAllowanceCondition = 0;
				while(rs.next()){
					nAllowanceCondition = uF.parseToInt(rs.getString("allowance_condition"));
				}
				rs.close();
				pst.close();
				
				allowancePaymentLogicList = new FillAllowancePaymentLogic().fillAllowancePaymentLogic(nAllowanceCondition);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return LOAD;
	}

	private String updateAllowancePaymentLogic(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs= null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			 
			double dblFixedAmt = 0.0d;
			if(uF.parseToInt(getStrAllowancePaymentLogic()) == A_FIXED_ONLY_ID 
					|| uF.parseToInt(getStrAllowancePaymentLogic()) == A_FIXED_X_DAYS_ID 
					|| uF.parseToInt(getStrAllowancePaymentLogic()) == A_FIXED_X_HOURS_ID 
					|| uF.parseToInt(getStrAllowancePaymentLogic()) == A_FIXED_X_CUSTOM_ID 
					|| uF.parseToInt(getStrAllowancePaymentLogic()) == A_FIXED_X_ACHIEVED_ID 
					|| uF.parseToInt(getStrAllowancePaymentLogic()) == A_FIXED_AND_PER_HOUR_ID 
					|| uF.parseToInt(getStrAllowancePaymentLogic()) == A_FIXED_AND_PER_DAY_ID){
				dblFixedAmt = uF.parseToDouble(getStrFixedAmt());
			} else if(uF.parseToInt(getStrAllowancePaymentLogic()) == A_FIXED_ONLY_DEDUCTION_ID && !getIsDeductFullAmount()){
				dblFixedAmt = uF.parseToDouble(getStrFixedAmt());
			}
			
			int nCalSalaryHeadId = 0;
			if(uF.parseToInt(getStrAllowancePaymentLogic()) == A_EQUAL_TO_SALARY_HEAD_ID 
					|| uF.parseToInt(getStrAllowancePaymentLogic()) == A_SALARY_HEAD_X_DAYS_ID 
					|| uF.parseToInt(getStrAllowancePaymentLogic()) == A_SALARY_HEAD_X_HOURS_ID 
					|| uF.parseToInt(getStrAllowancePaymentLogic()) == A_SALARY_HEAD_X_CUSTOM_ID 
					|| uF.parseToInt(getStrAllowancePaymentLogic()) == A_SALARY_HEAD_X_ACHIEVED_ID){
				nCalSalaryHeadId = uF.parseToInt(getStrCalsalaryHead());
			}
			
			pst = con.prepareStatement("update allowance_payment_logic  set payment_logic_slab=?,allowance_condition_id=?,payment_logic=?," +
					"updated_by=?,update_date=?,fixed_amount=?,cal_salary_head_id=?,is_publish=?,effective_date=?,per_hour_day=?," +
					"is_deduct_full_amount=? where payment_logic_id=?");
			pst.setString(1, getStrPaymentLogicSlab());
			pst.setInt(2, uF.parseToInt(getStrAllowanceConditionSlab()));
			pst.setInt(3, uF.parseToInt(getStrAllowancePaymentLogic()));
			pst.setInt(4, uF.parseToInt((String)session.getAttribute(EMPID)));
			pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setDouble(6, dblFixedAmt);
			pst.setInt(7, nCalSalaryHeadId);
			if(getBtnSubmitPublish() != null && getBtnSubmitPublish().equals("Submit & Publish")) {
				pst.setBoolean(8, true);
			} else {
				pst.setBoolean(8, false);
			}
			pst.setDate(9, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
			pst.setDouble(10, (uF.parseToInt(getStrAllowancePaymentLogic()) == A_FIXED_AND_PER_HOUR_ID || uF.parseToInt(getStrAllowancePaymentLogic()) == A_FIXED_AND_PER_DAY_ID) ? uF.parseToDouble(getStrPerHourDayAmt()) : 0.0d);
			pst.setBoolean(11, getIsDeductFullAmount());
			pst.setInt(12, uF.parseToInt(getPaymentLogicId()));
			pst.execute();
			pst.close();
			
			session.setAttribute(MESSAGE, SUCCESSM+"Allowance payment logic updated successfully."+END);
		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(MESSAGE, ERRORM+"Allowance payment logic could not be updated. Please try again."+END);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}

	private String insertAllowancePaymentLogic(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs= null;
		Database db = new Database();
		db.setRequest(request);
		
		try {

			con = db.makeConnection(con);
			
			double dblFixedAmt = 0.0d;
			if(uF.parseToInt(getStrAllowancePaymentLogic()) == A_FIXED_ONLY_ID 
					|| uF.parseToInt(getStrAllowancePaymentLogic()) == A_FIXED_X_DAYS_ID 
					|| uF.parseToInt(getStrAllowancePaymentLogic()) == A_FIXED_X_HOURS_ID 
					|| uF.parseToInt(getStrAllowancePaymentLogic()) == A_FIXED_X_CUSTOM_ID 
					|| uF.parseToInt(getStrAllowancePaymentLogic()) == A_FIXED_X_ACHIEVED_ID 
					|| uF.parseToInt(getStrAllowancePaymentLogic()) == A_FIXED_AND_PER_HOUR_ID 
					|| uF.parseToInt(getStrAllowancePaymentLogic()) == A_FIXED_AND_PER_DAY_ID){
				dblFixedAmt = uF.parseToDouble(getStrFixedAmt());
			} else if(uF.parseToInt(getStrAllowancePaymentLogic()) == A_FIXED_ONLY_DEDUCTION_ID && !getIsDeductFullAmount()){
				dblFixedAmt = uF.parseToDouble(getStrFixedAmt());
			}
			
			int nCalSalaryHeadId = 0;
			if(uF.parseToInt(getStrAllowancePaymentLogic()) == A_EQUAL_TO_SALARY_HEAD_ID 
					|| uF.parseToInt(getStrAllowancePaymentLogic()) == A_SALARY_HEAD_X_DAYS_ID 
					|| uF.parseToInt(getStrAllowancePaymentLogic()) == A_SALARY_HEAD_X_HOURS_ID 
					|| uF.parseToInt(getStrAllowancePaymentLogic()) == A_SALARY_HEAD_X_CUSTOM_ID 
					|| uF.parseToInt(getStrAllowancePaymentLogic()) == A_SALARY_HEAD_X_ACHIEVED_ID){
				nCalSalaryHeadId = uF.parseToInt(getStrCalsalaryHead());
			}
			
			pst = con.prepareStatement("insert into allowance_payment_logic(payment_logic_slab,allowance_condition_id,payment_logic,added_by,entry_date," +
					"salary_head_id,level_id,org_id,fixed_amount,cal_salary_head_id,is_publish,effective_date,per_hour_day,is_deduct_full_amount)" +
					" values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
			pst.setString(1, getStrPaymentLogicSlab());
			pst.setInt(2, uF.parseToInt(getStrAllowanceConditionSlab()));
			pst.setInt(3, uF.parseToInt(getStrAllowancePaymentLogic()));
			pst.setInt(4, uF.parseToInt((String)session.getAttribute(EMPID)));
			pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(6, uF.parseToInt(getStrSalaryHeadId()));
			pst.setInt(7, uF.parseToInt(getStrLevel()));
			pst.setInt(8, uF.parseToInt(getStrOrg()));
			pst.setDouble(9, dblFixedAmt);
			pst.setInt(10, nCalSalaryHeadId);
			if(getBtnSubmitPublish() != null && getBtnSubmitPublish().equals("Submit & Publish")) {
				pst.setBoolean(11, true);
			} else {
				pst.setBoolean(11, false);
			}
			pst.setDate(12, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
			pst.setDouble(13, (uF.parseToInt(getStrAllowancePaymentLogic()) == A_FIXED_AND_PER_HOUR_ID || uF.parseToInt(getStrAllowancePaymentLogic()) == A_FIXED_AND_PER_DAY_ID) ? uF.parseToDouble(getStrPerHourDayAmt()) : 0.0d);
			pst.setBoolean(14, getIsDeductFullAmount());
			pst.execute();
			pst.close();
			
			session.setAttribute(MESSAGE, SUCCESSM+"Allowance payment logic saved successfully."+END);
		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(MESSAGE, ERRORM+"Allowance payment logic could not be saved. Please try again."+END);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}


	private void loadAllowancePaymentLogic(UtilityFunctions uF) {
		allowanceConditionSlabList = new FillAllowanceCondition(request).fillAllowanceConditionSlab(uF,getStrOrg(),getStrLevel(),getStrSalaryHeadId());
//		allowancePaymentLogicList = new FillAllowancePaymentLogic().fillAllowancePaymentLogic();
		allowancePaymentLogicList = new ArrayList<FillAllowancePaymentLogic>();
		salaryHeadList = new FillSalaryHeads(request).fillSalaryHeadsForAllowancePolicy(getStrLevel(), "E");
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

	public String getStrLevel() {
		return strLevel;
	}

	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}

	public String getStrSalaryHeadId() {
		return strSalaryHeadId;
	}

	public void setStrSalaryHeadId(String strSalaryHeadId) {
		this.strSalaryHeadId = strSalaryHeadId;
	}

	public String getStrPaymentLogicSlab() {
		return strPaymentLogicSlab;
	}

	public void setStrPaymentLogicSlab(String strPaymentLogicSlab) {
		this.strPaymentLogicSlab = strPaymentLogicSlab;
	}

	public List<FillAllowanceCondition> getAllowanceConditionSlabList() {
		return allowanceConditionSlabList;
	}

	public void setAllowanceConditionSlabList(List<FillAllowanceCondition> allowanceConditionSlabList) {
		this.allowanceConditionSlabList = allowanceConditionSlabList;
	}

	public List<FillAllowancePaymentLogic> getAllowancePaymentLogicList() {
		return allowancePaymentLogicList;
	}

	public void setAllowancePaymentLogicList(List<FillAllowancePaymentLogic> allowancePaymentLogicList) {
		this.allowancePaymentLogicList = allowancePaymentLogicList;
	}

	public String getPaymentLogicId() {
		return paymentLogicId;
	}

	public void setPaymentLogicId(String paymentLogicId) {
		this.paymentLogicId = paymentLogicId;
	}

	public String getStrAllowanceConditionSlab() {
		return strAllowanceConditionSlab;
	}

	public void setStrAllowanceConditionSlab(String strAllowanceConditionSlab) {
		this.strAllowanceConditionSlab = strAllowanceConditionSlab;
	}

	public String getStrAllowancePaymentLogic() {
		return strAllowancePaymentLogic;
	}

	public void setStrAllowancePaymentLogic(String strAllowancePaymentLogic) {
		this.strAllowancePaymentLogic = strAllowancePaymentLogic;
	}

	public String getStrFixedAmt() {
		return strFixedAmt;
	}

	public void setStrFixedAmt(String strFixedAmt) {
		this.strFixedAmt = strFixedAmt;
	}

	public String getStrCalsalaryHead() {
		return strCalsalaryHead;
	}

	public void setStrCalsalaryHead(String strCalsalaryHead) {
		this.strCalsalaryHead = strCalsalaryHead;
	}

	public List<FillSalaryHeads> getSalaryHeadList() {
		return salaryHeadList;
	}

	public void setSalaryHeadList(List<FillSalaryHeads> salaryHeadList) {
		this.salaryHeadList = salaryHeadList;
	}

	public String getBtnSubmitPublish() {
		return btnSubmitPublish;
	}

	public void setBtnSubmitPublish(String btnSubmitPublish) {
		this.btnSubmitPublish = btnSubmitPublish;
	}

	public String getStrPublish() {
		return strPublish;
	}

	public void setStrPublish(String strPublish) {
		this.strPublish = strPublish;
	}

	public String getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public String getStrPerHourDayAmt() {
		return strPerHourDayAmt;
	}

	public void setStrPerHourDayAmt(String strPerHourDayAmt) {
		this.strPerHourDayAmt = strPerHourDayAmt;
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
	public boolean getIsDeductFullAmount() {
		return isDeductFullAmount;
	}

	public void setIsDeductFullAmount(boolean isDeductFullAmount) {
		this.isDeductFullAmount = isDeductFullAmount;
	}
}