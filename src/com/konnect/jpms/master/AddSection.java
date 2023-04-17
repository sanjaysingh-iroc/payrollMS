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
import com.konnect.jpms.select.FillUnderSection;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;
 
public class AddSection extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;

	HttpSession session;
	CommonFunctions CF;
	
	String strSalaryHeadId;
	String strUnderSection;
	String financialYear;
	List<FillAmountType> amountTypeList;
	List<FillUnderSection> underSectionList;
	
	String operation;
	
	String userscreen;
	String navigationId;
	String toPage;
	
	String sectionId;
	String sectionCode;
	String sectionDesc;
	String sectionExemptionLimit;
	String sectionLimitType;
	private String sectionCeilingAmount;
	
	private boolean isPFApplicable;
	private boolean isCeilingApplicable;
	private String slabType;
	
	private String subSectionName0;
	private String subSectionDesc0;
	private String subSectionLimitType0;
	private String subSectionAmount0;
	
	private String subSectionName1;
	private String subSectionDesc1;
	private String subSectionLimitType1;
	private String subSectionAmount1;
	
	private String subSectionName2;
	private String subSectionDesc2;
	private String subSectionLimitType2;
	private String subSectionAmount2;
	
	private String subSectionName3;
	private String subSectionDesc3;
	private String subSectionLimitType3;
	private String subSectionAmount3;
	
	private String subSectionName4;
	private String subSectionDesc4;
	private String subSectionLimitType4;
	private String subSectionAmount4;
	
	private boolean isAdjustGrossTotalIncomeLimitSubSec0;
	private boolean isAdjustGrossTotalIncomeLimitSubSec1;
	private boolean isAdjustGrossTotalIncomeLimitSubSec2;
	private boolean isAdjustGrossTotalIncomeLimitSubSec3;
	private boolean isAdjustGrossTotalIncomeLimitSubSec4;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		amountTypeList = new FillAmountType().fillAmountType();
		underSectionList = new FillUnderSection().fillUnderSection();
		request.setAttribute("amountTypeList", amountTypeList);
		request.setAttribute("underSectionList", underSectionList);
		
		if(getOperation() != null && getOperation().equals("D")) {
			return deleteSection();
		} else if (getOperation() != null && getOperation().equals("E")) { 
			return viewSection();
		} else if (getOperation() != null && getOperation().equals("U")) { 
			return updateSection();
		} else if (getOperation() != null && getOperation().equals("A")) {
			return insertSection();
		}
		
		if(operation == null || operation.equals("")) {
			setOperation("A");
		}
		
		return LOAD;
		
	}

	private String viewSection() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			String[] strFinancialYearDates = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;

			if (getFinancialYear() != null && !getFinancialYear().equals("") && !getFinancialYear().equals("null")) {
				strFinancialYearDates = getFinancialYear().split("-");
				strFinancialYearStart = strFinancialYearDates[0];
				strFinancialYearEnd = strFinancialYearDates[1];
			} else {
				strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
				setFinancialYear(strFinancialYearDates[0] + "-" + strFinancialYearDates[1]);
				
				strFinancialYearStart = strFinancialYearDates[0];
				strFinancialYearEnd = strFinancialYearDates[1];
			}
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from section_details where section_id=? and financial_year_start=? and financial_year_end=?");
			pst.setInt(1, uF.parseToInt(getSectionId()));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
			while(rs.next()) {
				setSectionCode(rs.getString("section_code"));
				setSectionDesc(rs.getString("section_description"));
				setSectionExemptionLimit(rs.getString("section_exemption_limit"));
				setSectionLimitType(rs.getString("section_limit_type"));
				setStrUnderSection(rs.getString("under_section"));
				setOperation("U");
				setIsPFApplicable(rs.getBoolean("is_pf_applicable"));
				setIsCeilingApplicable(rs.getBoolean("is_ceiling_applicable"));
				setSectionCeilingAmount(rs.getString("ceiling_amount"));
				setSlabType(rs.getString("slab_type"));
				setSubSectionName0(rs.getString("sub_section_1"));
				setSubSectionAmount0(rs.getString("sub_section_1_amt"));
				setSubSectionLimitType0(rs.getString("sub_section_1_limit_type"));
				setSubSectionDesc0(rs.getString("sub_section_1_description"));
				setIsAdjustGrossTotalIncomeLimitSubSec0(rs.getBoolean("sub_section_1_is_adjust_gross_income_limit"));
				
				setSubSectionName1(rs.getString("sub_section_2"));
				setSubSectionAmount1(rs.getString("sub_section_2_amt"));
				setSubSectionLimitType1(rs.getString("sub_section_2_limit_type"));
				setSubSectionDesc1(rs.getString("sub_section_2_description"));
				setIsAdjustGrossTotalIncomeLimitSubSec1(rs.getBoolean("sub_section_2_is_adjust_gross_income_limit"));
				
				setSubSectionName2(rs.getString("sub_section_3"));
				setSubSectionAmount2(rs.getString("sub_section_3_amt"));
				setSubSectionLimitType2(rs.getString("sub_section_3_limit_type"));
				setSubSectionDesc2(rs.getString("sub_section_3_description"));
				setIsAdjustGrossTotalIncomeLimitSubSec2(rs.getBoolean("sub_section_3_is_adjust_gross_income_limit"));
				
				setSubSectionName3(rs.getString("sub_section_4"));
				setSubSectionAmount3(rs.getString("sub_section_4_amt"));
				setSubSectionLimitType3(rs.getString("sub_section_4_limit_type"));
				setSubSectionDesc3(rs.getString("sub_section_4_description"));
				setIsAdjustGrossTotalIncomeLimitSubSec3(rs.getBoolean("sub_section_4_is_adjust_gross_income_limit"));
				
				setSubSectionName4(rs.getString("sub_section_5"));
				setSubSectionAmount4(rs.getString("sub_section_5_amt"));
				setSubSectionLimitType4(rs.getString("sub_section_5_limit_type"));
				setSubSectionDesc4(rs.getString("sub_section_5_description"));
				setIsAdjustGrossTotalIncomeLimitSubSec4(rs.getBoolean("sub_section_5_is_adjust_gross_income_limit"));
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return LOAD;

	}

	
	public String insertSection() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			String[] strFinancialYearDates = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;

			if (getFinancialYear() != null && !getFinancialYear().equals("") && !getFinancialYear().equals("null")) {
				strFinancialYearDates = getFinancialYear().split("-");
				strFinancialYearStart = strFinancialYearDates[0];
				strFinancialYearEnd = strFinancialYearDates[1];
			} else {
				strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
				setFinancialYear(strFinancialYearDates[0] + "-" + strFinancialYearDates[1]);
				
				strFinancialYearStart = strFinancialYearDates[0];
				strFinancialYearEnd = strFinancialYearDates[1];
			}
			
			con = db.makeConnection(con); 
			
			int sectionDetailsId = 0;
			pst = con.prepareStatement("select max(section_details_id) as section_details_id from section_details");
			rs = pst.executeQuery();
			if (rs.next()){
				sectionDetailsId = uF.parseToInt(rs.getString("section_details_id"));
			}
			rs.close();
			pst.close();
			
			sectionDetailsId++;
//			System.out.println("getIsPFApplicable ===>> " + getIsPFApplicable());
//			pst = con.prepareStatement(insertSection);
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("INSERT INTO section_details (section_code, section_description, section_exemption_limit, section_limit_type, " +
				"entry_date, user_id,under_section,salary_head_id,financial_year_start,financial_year_end,section_id, is_pf_applicable," +
				"is_ceiling_applicable, ceiling_amount, is_adjusted_gross_income_limit, slab_type");
			if(getSubSectionName0() != null && getSubSectionName0().trim().length()>0) {
				sbQuery.append(",sub_section_1,sub_section_1_description,sub_section_1_amt,sub_section_1_limit_type,sub_section_1_is_adjust_gross_income_limit");
			}
			if(getSubSectionName1() != null && getSubSectionName1().trim().length()>0) {
				sbQuery.append(",sub_section_2,sub_section_2_description,sub_section_2_amt,sub_section_2_limit_type,sub_section_2_is_adjust_gross_income_limit");
			}
			if(getSubSectionName2() != null && getSubSectionName2().trim().length()>0) {
				sbQuery.append(",sub_section_3,sub_section_3_description,sub_section_3_amt,sub_section_3_limit_type,sub_section_3_is_adjust_gross_income_limit");
			}
			if(getSubSectionName3() != null && getSubSectionName3().trim().length()>0) {
				sbQuery.append(",sub_section_4,sub_section_4_description,sub_section_4_amt,sub_section_4_limit_type,sub_section_4_is_adjust_gross_income_limit");
			}
			if(getSubSectionName4() != null && getSubSectionName4().trim().length()>0) {
				sbQuery.append(",sub_section_5,sub_section_5_description,sub_section_5_amt,sub_section_5_limit_type,sub_section_5_is_adjust_gross_income_limit");
			}
			sbQuery.append(") values (?,?,?,? ,?,?,?,? ,?,?,?,?, ?,?,?,?");
			if(getSubSectionName0() != null && getSubSectionName0().trim().length()>0) {
				sbQuery.append(",'"+getSubSectionName0()+"','"+getSubSectionDesc0()+"',"+getSubSectionAmount0()+",'"+getSubSectionLimitType0()+"', "+getIsAdjustGrossTotalIncomeLimitSubSec0()+" ");
			}
			if(getSubSectionName1() != null && getSubSectionName1().trim().length()>0) {
				sbQuery.append(",'"+getSubSectionName1()+"','"+getSubSectionDesc1()+"',"+getSubSectionAmount1()+",'"+getSubSectionLimitType1()+"', "+getIsAdjustGrossTotalIncomeLimitSubSec1()+" ");
			}
			if(getSubSectionName2() != null && getSubSectionName2().trim().length()>0) {
				sbQuery.append(",'"+getSubSectionName2()+"','"+getSubSectionDesc2()+"',"+getSubSectionAmount2()+",'"+getSubSectionLimitType2()+"', "+getIsAdjustGrossTotalIncomeLimitSubSec2()+" ");
			}
			if(getSubSectionName3() != null && getSubSectionName3().trim().length()>0) {
				sbQuery.append(",'"+getSubSectionName3()+"','"+getSubSectionDesc3()+"',"+getSubSectionAmount3()+",'"+getSubSectionLimitType3()+"', "+getIsAdjustGrossTotalIncomeLimitSubSec3()+" ");
			}
			if(getSubSectionName4() != null && getSubSectionName4().trim().length()>0) {
				sbQuery.append(",'"+getSubSectionName4()+"','"+getSubSectionDesc4()+"',"+getSubSectionAmount4()+",'"+getSubSectionLimitType4()+"', "+getIsAdjustGrossTotalIncomeLimitSubSec4()+" ");
			}
			sbQuery.append(")");
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setString(1, getSectionCode());
			pst.setString(2, uF.showData(getSectionDesc(),""));
			pst.setDouble(3, uF.parseToDouble(getSectionExemptionLimit()));
			pst.setString(4, uF.showData(getSectionLimitType(),""));
			pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(6, uF.parseToInt((String)session.getAttribute(EMPID)));
			pst.setInt(7, uF.parseToInt(getStrUnderSection()));
			pst.setString(8, getStrSalaryHeadId()!=null && getStrSalaryHeadId().length()>0 ? ","+getStrSalaryHeadId()+"," : null );
			pst.setDate(9, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(10, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(11, sectionDetailsId);
			pst.setBoolean(12, getIsPFApplicable());
			pst.setBoolean(13, getIsCeilingApplicable());
			pst.setDouble(14, uF.parseToDouble(getSectionCeilingAmount()));
			boolean blnIsAdjustedGrossIncomeLimit = false;
			if(getIsAdjustGrossTotalIncomeLimitSubSec0() || getIsAdjustGrossTotalIncomeLimitSubSec1() || getIsAdjustGrossTotalIncomeLimitSubSec2() || getIsAdjustGrossTotalIncomeLimitSubSec3() || getIsAdjustGrossTotalIncomeLimitSubSec4()) {
				blnIsAdjustedGrossIncomeLimit = true;
			}
			pst.setBoolean(15, blnIsAdjustedGrossIncomeLimit);
			pst.setInt(16, uF.parseToInt(getSlabType()));
//			System.out.println("pst ===>> " + pst);
			pst.executeUpdate();
			pst.close();
//			System.out.println("pst ===>> " + pst);
			
			session.setAttribute(MESSAGE, SUCCESSM+"Section code \""+getSectionCode()+"\" saved successfully."+END);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	
	public String updateSection() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			
			String[] strFinancialYearDates = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;

			if (getFinancialYear() != null) {
				strFinancialYearDates = getFinancialYear().split("-");
				strFinancialYearStart = strFinancialYearDates[0];
				strFinancialYearEnd = strFinancialYearDates[1];
			} else {
				strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
				setFinancialYear(strFinancialYearDates[0] + "-" + strFinancialYearDates[1]);
				
				strFinancialYearStart = strFinancialYearDates[0];
				strFinancialYearEnd = strFinancialYearDates[1];
			}
			
			con = db.makeConnection(con);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("update section_details set section_code=?, section_description=?, section_exemption_limit=?, section_limit_type=?, " +
				"entry_date=?,user_id=?,under_section=?,salary_head_id=?,is_pf_applicable=?,is_ceiling_applicable=?,ceiling_amount=?" +
				",is_adjusted_gross_income_limit=?,slab_type=?");
//			if(getSubSectionName0() != null && getSubSectionName0().trim().length()>0) {
				sbQuery.append(",sub_section_1='"+getSubSectionName0()+"',sub_section_1_description='"+getSubSectionDesc0()+"',sub_section_1_amt="+uF.parseToDouble(getSubSectionAmount0())+",sub_section_1_limit_type='"+getSubSectionLimitType0()+"', sub_section_1_is_adjust_gross_income_limit="+getIsAdjustGrossTotalIncomeLimitSubSec0()+"");
//			}
//			if(getSubSectionName1() != null && getSubSectionName1().trim().length()>0) {
				sbQuery.append(",sub_section_2='"+getSubSectionName1()+"',sub_section_2_description='"+getSubSectionDesc1()+"',sub_section_2_amt="+uF.parseToDouble(getSubSectionAmount1())+",sub_section_2_limit_type='"+getSubSectionLimitType1()+"', sub_section_2_is_adjust_gross_income_limit="+getIsAdjustGrossTotalIncomeLimitSubSec1()+"");
//			}
//			if(getSubSectionName2() != null && getSubSectionName2().trim().length()>0) {
				sbQuery.append(",sub_section_3='"+getSubSectionName2()+"',sub_section_3_description='"+getSubSectionDesc2()+"',sub_section_3_amt="+uF.parseToDouble(getSubSectionAmount2())+",sub_section_3_limit_type='"+getSubSectionLimitType2()+"', sub_section_3_is_adjust_gross_income_limit="+getIsAdjustGrossTotalIncomeLimitSubSec2()+"");
//			}
//			if(getSubSectionName3() != null && getSubSectionName3().trim().length()>0) {
				sbQuery.append(",sub_section_4='"+getSubSectionName3()+"',sub_section_4_description='"+getSubSectionDesc3()+"',sub_section_4_amt="+uF.parseToDouble(getSubSectionAmount3())+",sub_section_4_limit_type='"+getSubSectionLimitType3()+"', sub_section_4_is_adjust_gross_income_limit="+getIsAdjustGrossTotalIncomeLimitSubSec3()+"");
//			}
//			if(getSubSectionName4() != null && getSubSectionName4().trim().length()>0) {
				sbQuery.append(",sub_section_5='"+getSubSectionName4()+"',sub_section_5_description='"+getSubSectionDesc4()+"',sub_section_5_amt="+uF.parseToDouble(getSubSectionAmount4())+",sub_section_5_limit_type='"+getSubSectionLimitType4()+"', sub_section_5_is_adjust_gross_income_limit="+getIsAdjustGrossTotalIncomeLimitSubSec4()+"");
//			}
			sbQuery.append(" where section_id=? and financial_year_start=? and financial_year_end=?");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setString(1, getSectionCode());
			pst.setString(2, uF.showData(getSectionDesc(),""));
			pst.setDouble(3, uF.parseToDouble(getSectionExemptionLimit()));
			pst.setString(4, uF.showData(getSectionLimitType(),""));
			pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(6, uF.parseToInt((String)session.getAttribute(EMPID)));
			pst.setInt(7, uF.parseToInt(getStrUnderSection()));
			pst.setString(8, getStrSalaryHeadId()!=null && getStrSalaryHeadId().length()>0 ? ","+getStrSalaryHeadId()+"," : null );
			pst.setBoolean(9, getIsPFApplicable());
			pst.setBoolean(10, getIsCeilingApplicable());
			pst.setDouble(11, uF.parseToDouble(getSectionCeilingAmount()));
			boolean blnIsAdjustedGrossIncomeLimit = false;
			if(getIsAdjustGrossTotalIncomeLimitSubSec0() || getIsAdjustGrossTotalIncomeLimitSubSec1() || getIsAdjustGrossTotalIncomeLimitSubSec2() || getIsAdjustGrossTotalIncomeLimitSubSec3() || getIsAdjustGrossTotalIncomeLimitSubSec4()) {
				blnIsAdjustedGrossIncomeLimit = true;
			}
			pst.setBoolean(12, blnIsAdjustedGrossIncomeLimit);
			pst.setInt(13, uF.parseToInt(getSlabType()));
			pst.setInt(14, uF.parseToInt(getSectionId()));
			pst.setDate(15, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(16, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println("pst ===>> " + pst);
			pst.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

//	public String insertSection() {
//
//		Connection con = null;
//		PreparedStatement pst = null;
//		Database db = new Database();
//		db.setRequest(request);
//		UtilityFunctions uF = new UtilityFunctions();
//
//		try {
//			con = db.makeConnection(con); 
////			pst = con.prepareStatement(insertSection);
//			pst = con.prepareStatement("INSERT INTO section_details (section_code, section_description, section_exemption_limit, section_limit_type, " +
//					"entry_date, user_id,under_section,salary_head_id) values (?,?,?,?,?,?,?,?)");
//			pst.setString(1, getSectionCode());
//			pst.setString(2, uF.showData(getSectionDesc(),""));
//			pst.setDouble(3, uF.parseToDouble(getSectionExemptionLimit()));
//			pst.setString(4, uF.showData(getSectionLimitType(),""));
//			pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
//			pst.setInt(6, uF.parseToInt((String)session.getAttribute(EMPID)));
//			pst.setInt(7, uF.parseToInt(getStrUnderSection()));
//			pst.setString(8, getStrSalaryHeadId()!=null && getStrSalaryHeadId().length()>0 ? ","+getStrSalaryHeadId()+"," : null );
//			pst.execute();
//			
//			session.setAttribute(MESSAGE, SUCCESSM+"Section code \""+getSectionCode()+"\" saved successfully."+END);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//		return SUCCESS;
//
//	}
//	
//	public String updateSection() {
//
//		Connection con = null;
//		PreparedStatement pst = null;
//		Database db = new Database();
//		db.setRequest(request);
//		UtilityFunctions uF = new UtilityFunctions();
//		int columnId = Integer.parseInt(request.getParameter("columnId"));
//		
//		String columnName=null;
//		switch(columnId) {
//			case 0 : columnName = "section_code"; break;
//			case 1 : columnName = "section_description"; break;
//			case 2 : columnName = "section_exemption_limit"; break;
//			case 3 : columnName = "section_limit_type" ; break;
//			case 4 : columnName = "under_section" ; break;
//			//case 5 : columnName = "salary_head_id" ; break;
//		}
//		
//		String updateSection = "UPDATE section_details SET "+columnName+"=?, entry_date=?, user_id=? WHERE section_id=?";
//		
//		try {
//			con = db.makeConnection(con);
//			pst = con.prepareStatement(updateSection);
//			if(columnId==2 || columnId==4){
//				pst.setDouble(1, uF.parseToDouble(request.getParameter("value")));
//			}else if(columnId==5){
//				pst.setString(1, (request.getParameter("value")!=null && request.getParameter("value").length()>0 ? ","+request.getParameter("value") + "," : null ));
//			}else{
//				pst.setString(1, uF.showData((request.getParameter("value")),""));
//			}
//			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//			pst.setInt(3, uF.parseToInt((String)session.getAttribute(EMPID)));
//			pst.setInt(4, uF.parseToInt(request.getParameter("id")));
//			pst.execute();
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			return ERROR;
//		} finally {
//			
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//		return UPDATE;
//
//	}
	
	public String deleteSection() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(deleteSection);
			pst.setInt(1, uF.parseToInt(getSectionId()));
			pst.execute();
			pst.close();
			
			session.setAttribute(MESSAGE, SUCCESSM+"Section code deleted successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	
	
	/*public void validate() {

		if (getSectionId() != null && getSectionId().length() == 0) {
			addFieldError("sectionId", "Section ID is required");
		}
		if (getSectionCode() != null && getSectionCode().length() == 0) {
			addFieldError("sectionCode", "Section Code is required");
		}

	}*/

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getSectionId() {
		return sectionId;
	}

	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}

	public String getSectionDesc() {
		return sectionDesc;
	}

	public void setSectionDesc(String sectionDesc) {
		this.sectionDesc = sectionDesc;
	}

	public String getSectionCode() {
		return sectionCode;
	}

	public void setSectionCode(String sectionCode) {
		this.sectionCode = sectionCode;
	}

	public String getSectionExemptionLimit() {
		return sectionExemptionLimit;
	}

	public void setSectionExemptionLimit(String sectionExemptionLimit) {
		this.sectionExemptionLimit = sectionExemptionLimit;
	}

	public String getSectionLimitType() {
		return sectionLimitType;
	}

	public void setSectionLimitType(String sectionLimitType) {
		this.sectionLimitType = sectionLimitType;
	}

	public String getStrSalaryHeadId() {
		return strSalaryHeadId;
	}

	public void setStrSalaryHeadId(String strSalaryHeadId) {
		this.strSalaryHeadId = strSalaryHeadId;
	}

	public String getStrUnderSection() {
		return strUnderSection;
	}

	public void setStrUnderSection(String strUnderSection) {
		this.strUnderSection = strUnderSection;
	}

	public String getFinancialYear() {
		return financialYear;
	}

	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
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

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public List<FillAmountType> getAmountTypeList() {
		return amountTypeList;
	}

	public void setAmountTypeList(List<FillAmountType> amountTypeList) {
		this.amountTypeList = amountTypeList;
	}

	public List<FillUnderSection> getUnderSectionList() {
		return underSectionList;
	}

	public void setUnderSectionList(List<FillUnderSection> underSectionList) {
		this.underSectionList = underSectionList;
	}

	public boolean getIsPFApplicable() {
		return isPFApplicable;
	}

	public void setIsPFApplicable(boolean isPFApplicable) {
		this.isPFApplicable = isPFApplicable;
	}

	public boolean getIsCeilingApplicable() {
		return isCeilingApplicable;
	}

	public void setIsCeilingApplicable(boolean isCeilingApplicable) {
		this.isCeilingApplicable = isCeilingApplicable;
	}

	public String getSectionCeilingAmount() {
		return sectionCeilingAmount;
	}

	public void setSectionCeilingAmount(String sectionCeilingAmount) {
		this.sectionCeilingAmount = sectionCeilingAmount;
	}

	public String getSubSectionName0() {
		return subSectionName0;
	}

	public void setSubSectionName0(String subSectionName0) {
		this.subSectionName0 = subSectionName0;
	}

	public String getSubSectionDesc0() {
		return subSectionDesc0;
	}

	public void setSubSectionDesc0(String subSectionDesc0) {
		this.subSectionDesc0 = subSectionDesc0;
	}

	public String getSubSectionAmount0() {
		return subSectionAmount0;
	}

	public void setSubSectionAmount0(String subSectionAmount0) {
		this.subSectionAmount0 = subSectionAmount0;
	}

	public String getSubSectionName1() {
		return subSectionName1;
	}

	public void setSubSectionName1(String subSectionName1) {
		this.subSectionName1 = subSectionName1;
	}

	public String getSubSectionDesc1() {
		return subSectionDesc1;
	}

	public void setSubSectionDesc1(String subSectionDesc1) {
		this.subSectionDesc1 = subSectionDesc1;
	}

	public String getSubSectionAmount1() {
		return subSectionAmount1;
	}

	public void setSubSectionAmount1(String subSectionAmount1) {
		this.subSectionAmount1 = subSectionAmount1;
	}

	public String getSubSectionName2() {
		return subSectionName2;
	}

	public void setSubSectionName2(String subSectionName2) {
		this.subSectionName2 = subSectionName2;
	}

	public String getSubSectionDesc2() {
		return subSectionDesc2;
	}

	public void setSubSectionDesc2(String subSectionDesc2) {
		this.subSectionDesc2 = subSectionDesc2;
	}

	public String getSubSectionAmount2() {
		return subSectionAmount2;
	}

	public void setSubSectionAmount2(String subSectionAmount2) {
		this.subSectionAmount2 = subSectionAmount2;
	}

	public String getSubSectionName3() {
		return subSectionName3;
	}

	public void setSubSectionName3(String subSectionName3) {
		this.subSectionName3 = subSectionName3;
	}

	public String getSubSectionDesc3() {
		return subSectionDesc3;
	}

	public void setSubSectionDesc3(String subSectionDesc3) {
		this.subSectionDesc3 = subSectionDesc3;
	}

	public String getSubSectionAmount3() {
		return subSectionAmount3;
	}

	public void setSubSectionAmount3(String subSectionAmount3) {
		this.subSectionAmount3 = subSectionAmount3;
	}

	public String getSubSectionName4() {
		return subSectionName4;
	}

	public void setSubSectionName4(String subSectionName4) {
		this.subSectionName4 = subSectionName4;
	}

	public String getSubSectionDesc4() {
		return subSectionDesc4;
	}

	public void setSubSectionDesc4(String subSectionDesc4) {
		this.subSectionDesc4 = subSectionDesc4;
	}

	public String getSubSectionAmount4() {
		return subSectionAmount4;
	}

	public void setSubSectionAmount4(String subSectionAmount4) {
		this.subSectionAmount4 = subSectionAmount4;
	}

	public String getSubSectionLimitType0() {
		return subSectionLimitType0;
	}

	public void setSubSectionLimitType0(String subSectionLimitType0) {
		this.subSectionLimitType0 = subSectionLimitType0;
	}

	public String getSubSectionLimitType1() {
		return subSectionLimitType1;
	}

	public void setSubSectionLimitType1(String subSectionLimitType1) {
		this.subSectionLimitType1 = subSectionLimitType1;
	}

	public String getSubSectionLimitType2() {
		return subSectionLimitType2;
	}

	public void setSubSectionLimitType2(String subSectionLimitType2) {
		this.subSectionLimitType2 = subSectionLimitType2;
	}

	public String getSubSectionLimitType3() {
		return subSectionLimitType3;
	}

	public void setSubSectionLimitType3(String subSectionLimitType3) {
		this.subSectionLimitType3 = subSectionLimitType3;
	}

	public String getSubSectionLimitType4() {
		return subSectionLimitType4;
	}

	public void setSubSectionLimitType4(String subSectionLimitType4) {
		this.subSectionLimitType4 = subSectionLimitType4;
	}

	public boolean getIsAdjustGrossTotalIncomeLimitSubSec0() {
		return isAdjustGrossTotalIncomeLimitSubSec0;
	}

	public void setIsAdjustGrossTotalIncomeLimitSubSec0(boolean isAdjustGrossTotalIncomeLimitSubSec0) {
		this.isAdjustGrossTotalIncomeLimitSubSec0 = isAdjustGrossTotalIncomeLimitSubSec0;
	}

	public boolean getIsAdjustGrossTotalIncomeLimitSubSec1() {
		return isAdjustGrossTotalIncomeLimitSubSec1;
	}

	public void setIsAdjustGrossTotalIncomeLimitSubSec1(boolean isAdjustGrossTotalIncomeLimitSubSec1) {
		this.isAdjustGrossTotalIncomeLimitSubSec1 = isAdjustGrossTotalIncomeLimitSubSec1;
	}

	public boolean getIsAdjustGrossTotalIncomeLimitSubSec2() {
		return isAdjustGrossTotalIncomeLimitSubSec2;
	}

	public void setIsAdjustGrossTotalIncomeLimitSubSec2(boolean isAdjustGrossTotalIncomeLimitSubSec2) {
		this.isAdjustGrossTotalIncomeLimitSubSec2 = isAdjustGrossTotalIncomeLimitSubSec2;
	}

	public boolean getIsAdjustGrossTotalIncomeLimitSubSec3() {
		return isAdjustGrossTotalIncomeLimitSubSec3;
	}

	public void setIsAdjustGrossTotalIncomeLimitSubSec3(boolean isAdjustGrossTotalIncomeLimitSubSec3) {
		this.isAdjustGrossTotalIncomeLimitSubSec3 = isAdjustGrossTotalIncomeLimitSubSec3;
	}

	public boolean getIsAdjustGrossTotalIncomeLimitSubSec4() {
		return isAdjustGrossTotalIncomeLimitSubSec4;
	}

	public void setIsAdjustGrossTotalIncomeLimitSubSec4(boolean isAdjustGrossTotalIncomeLimitSubSec4) {
		this.isAdjustGrossTotalIncomeLimitSubSec4 = isAdjustGrossTotalIncomeLimitSubSec4;
	}

	public String getSlabType() {
		return slabType;
	}

	public void setSlabType(String slabType) {
		this.slabType = slabType;
	}

	
}