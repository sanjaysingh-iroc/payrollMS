package com.konnect.jpms.master;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillAmountType;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.select.FillSalaryHeads;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;
 
public class AddBonus extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	CommonFunctions CF=null;
	HttpSession session;
	
	private String orgId;
	
	private String bonusId;
	private String bonusSlabType;
	private String bonusLevel;
	private String bonusFrom;
	private String bonusTo;
	private String bonusMin;
	private String bonusMax;
	private String bonusAmountType;
	private String bonusMinDays;
	private String bonusAmount;
	private String[] bonusPeriod;
	private String[] salaryHeadId;
	private String salaryHeadNetId;
	private String financialYearFrom;
	private String financialYearTo;
	private String strEffectiveFY;
	private String strSalaryCalculation;
	
	private List<FillLevel> levelList;
	private List<FillMonth> monthList;
	
	private List<FillFinancialYears> financialYearList;
	private String financialYear;
	
	private List<FillAmountType> amountTypeList;	
	private List<FillSalaryHeads> salaryHeadList;
	private List<FillSalaryHeads> salaryHeadListNet;
	private List<FillSalaryHeads> salaryHeadList1;
	
	private String limitSalaryHead;
	private String limitAmt;
	
	private String bonusCondition;
	private String strMin;
	private String strMax;
	private String condition1;
	private String strMax2;
	private String condition2;
	
	private String userscreen;
	private String navigationId;
	private String toPage;
	
	public String execute() throws Exception {

		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;

		String operation = request.getParameter("operation");
		String strId = request.getParameter("ID");
		String strParam = request.getParameter("param");
		UtilityFunctions uF = new UtilityFunctions();
		
		System.out.println("AddBonus/87--getBonusAmount="+getBonusAmount());
		loadValidateBonus(strParam,uF);
		
		if (operation!=null && operation.equals("D")) {
			return deleteBonus(strId,uF);
		}
		if (operation!=null && operation.equals("E")) { 
			return viewBonus(strId,uF);
		}
		if (getBonusId()!=null && getBonusId().length()>0) { 
			return updateBonus(uF);
		}		
		
		if(uF.parseToDouble(getBonusAmount()) > 0.0d){
			return insertBonus(uF);
		}else if(uF.parseToBoolean(getBonusCondition())){
			return insertBonus(uF);
		}
		return LOAD; 
		
	} 

	public String loadValidateBonus(String strParam, UtilityFunctions uF) {
		
		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		
		levelList = new FillLevel(request).fillLevel();
		monthList = new FillMonth().fillMonth();
		
		amountTypeList = new FillAmountType().fillAmountType();
		salaryHeadList = new FillSalaryHeads(request).fillSalaryHeadsWithoutCTC(strParam);
		salaryHeadListNet = new FillSalaryHeads(request).fillSalaryHeadsWithNetWithoutCTCAndGross(strParam);
		salaryHeadList1 = new FillSalaryHeads(request).fillSalaryHeadsWithoutCTC(strParam);
		
		if(getStrEffectiveFY()==null){
			setStrEffectiveFY("1");
		}
		if(getStrSalaryCalculation()==null){
			setStrSalaryCalculation("1");
		}
		
		
		return LOAD;
	}

	
	public String viewBonus(String strId, UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from bonus_details where bonus_id=?");
			pst.setInt(1, uF.parseToInt(strId));
			rs = pst.executeQuery();
			System.out.println("AddBonus/145--pst="+pst);
			while(rs.next()){
				setBonusId(rs.getString("bonus_id"));
				setBonusAmount(rs.getString("bonus_amount"));
				setBonusLevel(rs.getString("level_id"));
				setBonusMin(rs.getString("bonus_minimum"));
				setBonusMax(rs.getString("bonus_maximum"));
				setBonusAmountType(rs.getString("bonus_type"));
				setBonusMinDays(rs.getString("bonus_minimum_days"));
				setOrgId(rs.getString("org_id"));
				setBonusSlabType(rs.getString("bonus_slab_type"));
				setStrEffectiveFY(rs.getString("salary_effective_year"));
				setStrSalaryCalculation(rs.getString("salary_calculation"));
				
				String str = rs.getString("bonus_period");
				if(str!=null){
					setBonusPeriod(str.split(","));					
				}
				
				str = rs.getString("salary_head_id");
				if(str!=null) {
					setSalaryHeadId(str.split(","));
				}
				if(uF.parseToInt(rs.getString("bonus_slab_type"))==2) {
					setSalaryHeadNetId(rs.getString("salary_head_id"));
				}
				
				setLimitSalaryHead(rs.getString("limit_salary_head_id"));
				setLimitAmt(""+uF.parseToDouble(rs.getString("limit_amount")));
				
				setBonusCondition(""+uF.parseToBoolean(rs.getString("is_bonus_condition")));
				if(uF.parseToBoolean(rs.getString("is_bonus_condition"))){
					setStrMin(rs.getString("min1"));
					setStrMax(rs.getString("max1"));
					setCondition1(rs.getString("percentage1"));
					
					setStrMax2(rs.getString("max2"));
					setCondition2(rs.getString("percentage2"));
				}
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
	
	public String insertBonus(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			
			
			con = db.makeConnection(con);
//			pst = con.prepareStatement(insertBonus);
			pst = con.prepareStatement("INSERT INTO bonus_details (level_id, date_from, date_to, bonus_minimum, bonus_maximum, bonus_type, salary_head_id, " +
					"bonus_minimum_days, bonus_amount, bonus_period, entry_date, user_id, org_id, salary_calculation, salary_effective_year," +
					"limit_salary_head_id,limit_amount,is_bonus_condition,min1,max1,percentage1,max2,percentage2,bonus_slab_type) " +
					"values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?)");
			pst.setInt(1, uF.parseToInt(getBonusLevel()));
			pst.setDate(2, uF.getDateFormat(getFinancialYearFrom(), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getFinancialYearTo(), DATE_FORMAT));
			pst.setDouble(4, uF.parseToDouble(getBonusMin()));
			pst.setDouble(5, getBonusAmountType()!=null && getBonusAmountType().equals("%") ? uF.parseToDouble(getBonusMax()) : 0.0d);
			pst.setString(6, getBonusAmountType());
			
			StringBuilder sb = new StringBuilder();
			for(int i=0; getSalaryHeadId()!=null && i<getSalaryHeadId().length; i++){
				sb.append(getSalaryHeadId()[i]+",");
			}
			if(uF.parseToInt(getBonusSlabType())==1) {
				pst.setString(7, getBonusAmountType()!=null && getBonusAmountType().equals("%") ? sb.toString() : null);
			} else {
				pst.setString(7, getBonusAmountType()!=null && getBonusAmountType().equals("%") ? getSalaryHeadNetId() : null);
			}
			sb.replace(0, sb.length(), "");
			
			pst.setInt(8, uF.parseToInt(getBonusMinDays()));
			System.out.println("AddBonus/236---getBonusAmount="+getBonusAmount());
			pst.setDouble(9, uF.parseToDouble(getBonusAmount()));
			
			for(int i=0; getBonusPeriod()!=null && i<getBonusPeriod().length; i++) {
				sb.append(getBonusPeriod()[i]+",");
			}
			pst.setString(10, sb.toString());
			
			pst.setDate(11, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(12, uF.parseToInt((String)session.getAttribute(EMPID)));
			pst.setInt(13, uF.parseToInt(getOrgId()));
			pst.setInt(14, getBonusAmountType()!=null && getBonusAmountType().equals("%") ? uF.parseToInt(getStrSalaryCalculation()) : 0);
			pst.setInt(15,  getBonusAmountType()!=null && getBonusAmountType().equals("%") ? uF.parseToInt(getStrEffectiveFY()) : 0);
			pst.setInt(16, uF.parseToInt(getLimitSalaryHead()));
			pst.setDouble(17, uF.parseToDouble(getLimitAmt()));
			pst.setBoolean(18, uF.parseToBoolean(getBonusCondition()));
			pst.setDouble(19, uF.parseToBoolean(getBonusCondition()) ? uF.parseToDouble(getStrMin()) : 0.0d);
			pst.setDouble(20, uF.parseToBoolean(getBonusCondition()) ? uF.parseToDouble(getStrMax()) : 0.0d);
			pst.setDouble(21, uF.parseToBoolean(getBonusCondition()) ? uF.parseToDouble(getCondition1()) : 0.0d);
			pst.setDouble(22, uF.parseToBoolean(getBonusCondition()) ? uF.parseToDouble(getStrMax2()) : 0.0d);
			pst.setDouble(23, uF.parseToBoolean(getBonusCondition()) ? uF.parseToDouble(getCondition2()) : 0.0d);
			pst.setInt(24, uF.parseToInt(getBonusSlabType()));
			System.out.println("AB/256--pst==>"+pst);
			pst.execute(); 
			pst.close();

			session.setAttribute(MESSAGE, SUCCESSM+"Bonus policy saved successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	
	public String updateBonus(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("UPDATE bonus_details SET level_id=?, bonus_minimum=?, bonus_maximum=?, bonus_type=?, salary_head_id=?, " +
					"bonus_minimum_days=?, bonus_amount=?, bonus_period=?, entry_date=?, user_id=?, salary_calculation=?, salary_effective_year=?, " +
					"limit_salary_head_id=?, limit_amount=?,is_bonus_condition=?,min1=?,max1=?,percentage1=?,max2=?,percentage2=?, bonus_slab_type=?"
					+ " WHERE bonus_id=?");
			pst.setInt(1, uF.parseToInt(getBonusLevel()));
			pst.setDouble(2, uF.parseToDouble(getBonusMin()));
			pst.setDouble(3, getBonusAmountType()!=null && getBonusAmountType().equals("%") ? uF.parseToDouble(getBonusMax()) : 0.0d);
			pst.setString(4, getBonusAmountType());
			
			StringBuilder sb = new StringBuilder();
			for(int i=0; getSalaryHeadId()!=null && i<getSalaryHeadId().length; i++){
				sb.append(getSalaryHeadId()[i]+",");
			}
			if(uF.parseToInt(getBonusSlabType()) ==1) {
				pst.setString(5, getBonusAmountType()!=null && getBonusAmountType().equals("%") ? sb.toString() : null);
			} else {
				pst.setString(5, getBonusAmountType()!=null && getBonusAmountType().equals("%") ? getSalaryHeadNetId() : null);
			}
			sb.replace(0, sb.length(), "");
			
			pst.setInt(6, uF.parseToInt(getBonusMinDays()));
			pst.setDouble(7, uF.parseToDouble(getBonusAmount()));
			
			for(int i=0; getBonusPeriod()!=null && i<getBonusPeriod().length; i++){
				sb.append(getBonusPeriod()[i]+",");
			}
			
			pst.setString(8, sb.toString());
			pst.setDate(9, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(10, uF.parseToInt((String)session.getAttribute(EMPID)));
			pst.setInt(11, getBonusAmountType()!=null && getBonusAmountType().equals("%") ? uF.parseToInt(getStrSalaryCalculation()) : 0);
			pst.setInt(12, getBonusAmountType()!=null && getBonusAmountType().equals("%") ? uF.parseToInt(getStrEffectiveFY()) : 0);
			pst.setInt(13, uF.parseToInt(getLimitSalaryHead()));
			pst.setDouble(14, uF.parseToDouble(getLimitAmt()));
			pst.setBoolean(15, uF.parseToBoolean(getBonusCondition()));
			pst.setDouble(16, uF.parseToBoolean(getBonusCondition()) ? uF.parseToDouble(getStrMin()) : 0.0d);
			pst.setDouble(17, uF.parseToBoolean(getBonusCondition()) ? uF.parseToDouble(getStrMax()) : 0.0d);
			pst.setDouble(18, uF.parseToBoolean(getBonusCondition()) ? uF.parseToDouble(getCondition1()) : 0.0d);
			pst.setDouble(19, uF.parseToBoolean(getBonusCondition()) ? uF.parseToDouble(getStrMax2()) : 0.0d);
			pst.setDouble(20, uF.parseToBoolean(getBonusCondition()) ? uF.parseToDouble(getCondition2()) : 0.0d);
			pst.setInt(21, uF.parseToInt(getBonusSlabType()));
			pst.setInt(22, uF.parseToInt(getBonusId()));
			System.out.println("update pst==>"+pst);
			pst.execute();
			pst.close();
			
			session.setAttribute(MESSAGE, SUCCESSM+"Bonus policy updated successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	
	public String deleteBonus(String strId, UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(deleteBonus);
			pst.setInt(1, uF.parseToInt(strId));
			pst.execute();
			pst.close();
			
			session.setAttribute(MESSAGE, SUCCESSM+"Bonus policy deleted successfully."+END);
			
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

	public String getBonusId() {
		return bonusId;
	}

	public void setBonusId(String bonusId) {
		this.bonusId = bonusId;
	}

	public String getBonusLevel() {
		return bonusLevel;
	}

	public void setBonusLevel(String bonusLevel) {
		this.bonusLevel = bonusLevel;
	}

	public String getBonusFrom() {
		return bonusFrom;
	}

	public void setBonusFrom(String bonusFrom) {
		this.bonusFrom = bonusFrom;
	}

	public String getBonusTo() {
		return bonusTo;
	}

	public void setBonusTo(String bonusTo) {
		this.bonusTo = bonusTo;
	}

	public String getBonusMin() {
		return bonusMin;
	}

	public void setBonusMin(String bonusMin) {
		this.bonusMin = bonusMin;
	}

	public String getBonusMax() {
		return bonusMax;
	}

	public void setBonusMax(String bonusMax) {
		this.bonusMax = bonusMax;
	}

	public String getBonusAmountType() {
		return bonusAmountType;
	}

	public void setBonusAmountType(String bonusAmountType) {
		this.bonusAmountType = bonusAmountType;
	}

	public String getBonusMinDays() {
		return bonusMinDays;
	}

	public void setBonusMinDays(String bonusMinDays) {
		this.bonusMinDays = bonusMinDays;
	}

	public String getBonusAmount() {
		return bonusAmount;
	}

	public void setBonusAmount(String bonusAmount) {
		this.bonusAmount = bonusAmount;
	}

	public String getFinancialYearFrom() {
		return financialYearFrom;
	}

	public void setFinancialYearFrom(String financialYearFrom) {
		this.financialYearFrom = financialYearFrom;
	}

	public String getFinancialYearTo() {
		return financialYearTo;
	}

	public void setFinancialYearTo(String financialYearTo) {
		this.financialYearTo = financialYearTo;
	}

	public List<FillLevel> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}

	public List<FillMonth> getMonthList() {
		return monthList;
	}

	public void setMonthList(List<FillMonth> monthList) {
		this.monthList = monthList;
	}

	public List<FillFinancialYears> getFinancialYearList() {
		return financialYearList;
	}

	public void setFinancialYearList(List<FillFinancialYears> financialYearList) {
		this.financialYearList = financialYearList;
	}

	public String getFinancialYear() {
		return financialYear;
	}

	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}

	public List<FillAmountType> getAmountTypeList() {
		return amountTypeList;
	}

	public void setAmountTypeList(List<FillAmountType> amountTypeList) {
		this.amountTypeList = amountTypeList;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public void setBonusPeriod(String[] bonusPeriod) {
		this.bonusPeriod = bonusPeriod;
	}

	public String[] getBonusPeriod() {
		return bonusPeriod;
	}

	public List<FillSalaryHeads> getSalaryHeadList() {
		return salaryHeadList;
	}

	public void setSalaryHeadList(List<FillSalaryHeads> salaryHeadList) {
		this.salaryHeadList = salaryHeadList;
	}

	public String[] getSalaryHeadId() {
		return salaryHeadId;
	}

	public void setSalaryHeadId(String[] salaryHeadId) {
		this.salaryHeadId = salaryHeadId;
	}

	public List<FillSalaryHeads> getSalaryHeadListNet() {
		return salaryHeadListNet;
	}

	public void setSalaryHeadListNet(List<FillSalaryHeads> salaryHeadListNet) {
		this.salaryHeadListNet = salaryHeadListNet;
	}

	public String getStrEffectiveFY() {
		return strEffectiveFY;
	}

	public void setStrEffectiveFY(String strEffectiveFY) {
		this.strEffectiveFY = strEffectiveFY;
	}

	public String getStrSalaryCalculation() {
		return strSalaryCalculation;
	}

	public void setStrSalaryCalculation(String strSalaryCalculation) {
		this.strSalaryCalculation = strSalaryCalculation;
	}

	public String getLimitSalaryHead() {
		return limitSalaryHead;
	}

	public void setLimitSalaryHead(String limitSalaryHead) {
		this.limitSalaryHead = limitSalaryHead;
	}

	public String getLimitAmt() {
		return limitAmt;
	}

	public void setLimitAmt(String limitAmt) {
		this.limitAmt = limitAmt;
	}

	public List<FillSalaryHeads> getSalaryHeadList1() {
		return salaryHeadList1;
	}

	public void setSalaryHeadList1(List<FillSalaryHeads> salaryHeadList1) {
		this.salaryHeadList1 = salaryHeadList1;
	}

	public String getBonusCondition() {
		return bonusCondition;
	}

	public void setBonusCondition(String bonusCondition) {
		this.bonusCondition = bonusCondition;
	}

	public String getStrMin() {
		return strMin;
	}

	public void setStrMin(String strMin) {
		this.strMin = strMin;
	}

	public String getStrMax() {
		return strMax;
	}

	public void setStrMax(String strMax) {
		this.strMax = strMax;
	}

	public String getCondition1() {
		return condition1;
	}

	public void setCondition1(String condition1) {
		this.condition1 = condition1;
	}

	public String getStrMax2() {
		return strMax2;
	}

	public void setStrMax2(String strMax2) {
		this.strMax2 = strMax2;
	}

	public String getCondition2() {
		return condition2;
	}

	public void setCondition2(String condition2) {
		this.condition2 = condition2;
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

	public String getSalaryHeadNetId() {
		return salaryHeadNetId;
	}

	public void setSalaryHeadNetId(String salaryHeadNetId) {
		this.salaryHeadNetId = salaryHeadNetId;
	}

	public String getBonusSlabType() {
		return bonusSlabType;
	}

	public void setBonusSlabType(String bonusSlabType) {
		this.bonusSlabType = bonusSlabType;
	}
	
}