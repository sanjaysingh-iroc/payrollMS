package com.konnect.jpms.salary;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillGrade;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillSalaryBand;
import com.konnect.jpms.select.FillSalaryHeads;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

/**
 * @author konnect
 * 
 */
public class SalaryDetails extends ActionSupport implements ServletRequestAware, IStatements{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5947893602821384559L;
	
	String headNameOther;
	String headName;
	String earningOrDeduction;
	String headAmountType;
	String salarySubHead;  
	String curr_short;
	String headAmount;
	String removeId;
	String operation; 
	String salaryHeadId;
	String salaryId;
	String level;
	String salary_type;
	
	boolean isSave=false;
	HttpSession session;
	CommonFunctions CF; 
	String strUserType;
	String sessionEmpId;
//	String empId;
	
	private List<FillSalaryHeads> salaryHeadList;
	private List<FillSalaryHeads> salaryHeadListEarning;
	private List<FillSalaryHeads> salaryHeadListDeduction;
	private List<FillLevel> levelList;
	private List<FillSalaryBand> salBandList;
	
	List<FillSalaryHeads> salaryHeadListIncentiveEarning;
	
	List<List<String>> al = new ArrayList<List<String>>();
	List<FillOrganisation> orgList;
	String strOrg;
	
	String isVariable;
	String isAnnualVariable;
	String isCTCVariable;
	String isTaxExemption;
	
	String strIsIncentive;
	
	String strIsAllowance;
	String isContribution;
	String headMaxCapAmount;
	
	List<FillSalaryHeads> salaryHeadListAllowanceEarning;
	
	String salaryHead1;
	String salaryHead2;
	String salaryHead3;
	String salaryHead4;
	String salaryHead5;
	String salaryHead6;
	String salaryHead7;
	String salaryHead8;
	String salaryHead9;
	String salaryHead10;
	String sign1;
	String sign2;
	String sign3;
	String sign4;
	String sign5;
	String sign6;
	String sign7;
	String sign8;
	String sign9;
	
	String strGrade;
	List<FillGrade> gradeList;
	
	String isAlignPerk;
	
	String userscreen;
	String navigationId;
	String toPage;
	String autoUpdate;
	
	private String salaryBandName;
	private String salaryBandMinAmt;
	private String salaryBandMaxAmt;
	private String salaryBand;
	private String replicateSalaryBand;
	
	private String strMulPerCalFormula;
	String isDefaultCalculateAllowance;
	
	public String execute()	{
		
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		request.setAttribute(PAGE, PSalaryDetails); 
//		request.setAttribute(PAGE, "/jsp/salary/SalaryDetailsG.jsp");
		request.setAttribute(TITLE, TSalaryDetails);
		strUserType = (String) session.getAttribute(USERTYPE);
		sessionEmpId = (String) session.getAttribute(EMPID);
		UtilityFunctions uF = new UtilityFunctions();
		
//		boolean isView  = CF.getAccess(session, request, uF);
//		if(!isView) {
//			request.setAttribute(PAGE, PAccessDenied);
//			request.setAttribute(TITLE, TAccessDenied);
//			return ACCESS_DENIED;
//		}
//		System.out.println("getStrSalaryStructure======>"+CF.getStrSalaryStructure());
		request.setAttribute("salaryStructure", CF.getStrSalaryStructure());
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
			orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
			if(uF.parseToInt(getStrOrg()) == 0  && orgList!=null && orgList.size()>0) {
				setStrOrg(orgList.get(0).getOrgId());
			}
		}else{
			if(uF.parseToInt(getStrOrg()) == 0) {
				setStrOrg((String) session.getAttribute(ORGID));
			}
			orgList = new FillOrganisation(request).fillOrganisation();
		}
		
		int nSalaryStrucuterType = uF.parseToInt(CF.getStrSalaryStructure());
		if(nSalaryStrucuterType == S_GRADE_WISE) {
			if(getRemoveId()!=null) {
				removeSalaryHeadByGrade(uF);
				return SUCCESS;
			}
			getSalaryHeadListByGrade(uF);
			
			if(getOperation()!=null && getOperation().equalsIgnoreCase("assignTemplate")) {
//				System.out.println("in assignTemplate");
				String strTemplateDate = request.getParameter("strTemplateDate");
				assignGradeSalaryTemplate(uF, strTemplateDate);
//				System.out.println("success assignTemplate");
				return SUCCESS;
			}
			
			if(getOperation()!=null && getOperation().equalsIgnoreCase("ADD")) {
				addStatutoryComplianceSalaryHeadByGrade(uF);
				return SUCCESS;
			}
			
			if(getOperation()!=null && getOperation().equalsIgnoreCase("SALBASIS")) {
				addCTCBasicSalaryBasisHeadByGrade(uF);
				return SUCCESS;
			}
			
			/*if(getOperation()!=null && getOperation().equalsIgnoreCase("SALBAND")) {
				addNewSalaryBandByGrade(uF);
				return SUCCESS;
			}*/
			
			if(getOperation()!=null && getOperation().equals("A")) {
				addNewSalaryHeadByGrade(0);
				return SUCCESS;
			}
			
			if(getOperation()!=null && getOperation().equals("E")) {
				editSalaryHeadByGrade(uF);
				return SUCCESS;
			}
			
			levelList = new FillLevel(request).fillLevel(uF.parseToInt(getStrOrg()));
			if((uF.parseToInt(getLevel())==0 || uF.parseToInt(getLevel())==-1) && levelList!=null && levelList.size()>0) {
				setLevel(levelList.get(0).getLevelId());
			}
			gradeList = new FillGrade(request).fillGradeByOrgLevel(uF.parseToInt(getStrOrg()),uF.parseToInt(getLevel()));
			if((uF.parseToInt(getStrGrade()) == 0 || uF.parseToInt(getStrGrade())== -1) && gradeList!=null && gradeList.size()>0) {
				setStrGrade(gradeList.get(0).getGradeId());
			}
			
			salaryHeadList = new FillSalaryHeads(request).fillSalaryHeadsWithDuplicationByGrade(getStrGrade());
			salaryHeadListEarning = new FillSalaryHeads(request).fillSalaryHeadsByGrade(getStrGrade(), true, "E", 0);
			salaryHeadListIncentiveEarning = new FillSalaryHeads(request).fillSalaryHeadsByGrade(getStrGrade(), true, "E", 1);
			salaryHeadListAllowanceEarning = new FillSalaryHeads(request).fillSalaryHeadsByGrade(getStrGrade(), true, "E", 2);
			
			salaryHeadListDeduction = new FillSalaryHeads(request).fillSalaryHeadsWithStatutoryComplianceHeadsByGrade(getStrGrade(), true);
			
			if(uF.parseToInt(getStrGrade())>0) {
				viewSalaryDetailsByGrade(uF);
			}
			
		} else {
			if(getRemoveId()!=null) {
				removeSalaryHead(uF);
				return SUCCESS;
			}
			
			getSalaryHeadList(uF);
			
			if(getOperation()!=null && getOperation().equalsIgnoreCase("ADD")) {
				addStatutoryComplianceSalaryHead(uF);
				return SUCCESS;
			}
			
			if(getOperation()!=null && getOperation().equalsIgnoreCase("SALBASIS")) {
				addCTCBasicSalaryBasisHead(uF);
				return SUCCESS;
			}
			
			if(getOperation()!=null && getOperation().equalsIgnoreCase("SALBAND")) {
				addNewSalaryBand(uF);
				return SUCCESS;
			}
			if(getOperation()!=null && getOperation().equalsIgnoreCase("SALBANDDELETE")) {
				deleteSalaryBand(uF);
				return SUCCESS;
			}
			
			if(getOperation()!=null && getOperation().equals("A")) {
				addNewSalaryHead(0);
				return SUCCESS;
			}
			
			if(getOperation()!=null && getOperation().equals("E")) {
				editSalaryHead(uF);
				return SUCCESS;
			}
			
			levelList = new FillLevel(request).fillLevel(uF.parseToInt(getStrOrg()));
//			System.out.println("level ====>"+getLevel());
			if((uF.parseToInt(getLevel())==0 || uF.parseToInt(getLevel())==-1) && levelList!=null && levelList.size()>0) {
				setLevel(levelList.get(0).getLevelId());
			}
			System.out.println("getLevel() ===>> " + getLevel()+ " -- getSalaryBand() ===>> " + getSalaryBand());
			salBandList = new FillSalaryBand(request).fillSalaryBands(getLevel());
			if(uF.parseToInt(getSalaryBand())==0 && salBandList!=null && salBandList.size()>0) {
				setSalaryBand(salBandList.get(0).getSalaryBandId());
			} else if(salBandList==null || salBandList.size()==0) {
				setSalaryBand("0");
			}
//			System.out.println("getSalaryBand() ===>> " + getSalaryBand());
			
			salaryHeadList = new FillSalaryHeads(request).fillSalaryHeadsWithDuplication(getLevel(), getSalaryBand());
			salaryHeadListEarning = new FillSalaryHeads(request).fillSalaryHeads(getLevel(), getSalaryBand(), true, "E", 0);
			salaryHeadListIncentiveEarning = new FillSalaryHeads(request).fillSalaryHeads(getLevel(), getSalaryBand(), true, "E", 1);
			salaryHeadListAllowanceEarning = new FillSalaryHeads(request).fillSalaryHeads(getLevel(), getSalaryBand(), true, "E", 2);
			
			salaryHeadListDeduction = new FillSalaryHeads(request).fillSalaryHeadsWithStatutoryComplianceHeads(getLevel(), getSalaryBand(), true);
			
			if(uF.parseToInt(getLevel())>0) {
				viewSalaryDetails();
			}
		}
		
		getSelectedFilter(uF,nSalaryStrucuterType);
		
		clearText(uF);
		
		return LOAD;
	}
	
	private void deleteSalaryBand(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			if(uF.parseToInt(getSalaryBand())>0) {
				pst = con.prepareStatement("delete from salary_band_details WHERE salary_band_id=?");
				pst.setInt(1, uF.parseToInt(getSalaryBand()));
				pst.executeUpdate();
				pst.close();
//				System.out.println("pst ===>> " + pst);
				
				pst = con.prepareStatement("delete from salary_details where level_id=? and salary_band_id=?");
				pst.setInt(1, uF.parseToInt(getLevel()));
				pst.setInt(2, uF.parseToInt(getSalaryBand()));
				pst.executeUpdate();
				pst.close();
//				System.out.println("pst -- ===>> " + pst);
				
				setSalaryBand(null);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void addNewSalaryBand(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			if(uF.parseToInt(getSalaryBand())>0) {
				pst = con.prepareStatement("update salary_band_details set salary_band_name=?,band_min_amount=?,band_max_amount=?,updated_by=?,update_date=? WHERE salary_band_id=?");
				pst.setString(1, getSalaryBandName());
				pst.setDouble(2, uF.parseToDouble(getSalaryBandMinAmt()));
				pst.setDouble(3, uF.parseToDouble(getSalaryBandMaxAmt()));
				pst.setInt(4, uF.parseToInt(sessionEmpId));
				pst.setTimestamp(5, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+ uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
				pst.setInt(6, uF.parseToInt(getSalaryBand()));
				pst.executeUpdate();
				pst.close();
			} else {
				pst = con.prepareStatement("insert into salary_band_details(salary_band_name,band_min_amount,band_max_amount,level_id,org_id,added_by,entry_date) " +
					"values(?,?,?,?, ?,?,?)");
				pst.setString(1, getSalaryBandName());
				pst.setDouble(2, uF.parseToDouble(getSalaryBandMinAmt()));
				pst.setDouble(3, uF.parseToDouble(getSalaryBandMaxAmt()));
				pst.setInt(4, uF.parseToInt(getLevel()));
				pst.setInt(5, uF.parseToInt(getStrOrg()));
				pst.setInt(6, uF.parseToInt(sessionEmpId));
				pst.setTimestamp(7, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+ uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
				pst.executeUpdate();
				pst.close();
				
				pst = con.prepareStatement("select max(salary_band_id) as salary_band_id from salary_band_details");
				rs = pst.executeQuery();
				while(rs.next()) {
					setSalaryBand(rs.getString("salary_band_id"));
				}
				rs.close();
				pst.close();
			}
			
			if(uF.parseToInt(getReplicateSalaryBand())>0) {
				pst = con.prepareStatement("delete from salary_details where level_id=? and salary_band_id=?");
				pst.setInt(1, uF.parseToInt(getLevel()));
				pst.setInt(2, uF.parseToInt(getSalaryBand()));
				pst.executeUpdate();
				pst.close();
					
				pst = con.prepareStatement("insert into salary_details (salary_head_name,earning_deduction,salary_head_amount_type,sub_salary_head_id,salary_head_amount,isremove," +
					"isedit,weight,level_id,salary_head_id,org_id,is_variable,salary_type,is_ctc_variable,is_tax_exemption,added_by,added_date,is_incentive,is_delete," +
					"is_allowance,multiple_calculation,grade_id,is_align_with_perk,is_annual_variable,is_reimbursement_ctc,salary_calculate_amount,is_default_cal_allowance," +
					"is_contribution,max_cap_amount,salary_band_id) select salary_head_name,earning_deduction,salary_head_amount_type,sub_salary_head_id,salary_head_amount,isremove," +
					"isedit,weight,level_id,salary_head_id,org_id,is_variable,salary_type,is_ctc_variable,is_tax_exemption,added_by,added_date,is_incentive,is_delete," +
					"is_allowance,multiple_calculation,grade_id,is_align_with_perk,is_annual_variable,is_reimbursement_ctc,salary_calculate_amount,is_default_cal_allowance," +
					"is_contribution,max_cap_amount,? from salary_details where level_id=? and salary_band_id=?");
				pst.setInt(1, uF.parseToInt(getSalaryBand()));
				pst.setInt(2, uF.parseToInt(getLevel()));
				pst.setInt(3, uF.parseToInt(getReplicateSalaryBand()));
				pst.executeUpdate();
				pst.close();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	private void assignGradeSalaryTemplate(UtilityFunctions uF, String strTemplateDate) {
		Connection con = null;
		PreparedStatement pst = null;
		PreparedStatement pst1 = null;
		PreparedStatement pst2 = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			con.setAutoCommit(false);
			
			pst = con.prepareStatement("select grade_id from grades_details where grade_id !=? and designation_id in (select dd.designation_id " +
					"from level_details ld, designation_details dd where dd.level_id = ld.level_id and ld.level_id =?) order by grade_id;");
			pst.setInt(1, uF.parseToInt(getStrGrade()));
			pst.setInt(2, uF.parseToInt(getLevel()));
//			System.out.println("pst=======>"+pst);
			rs = pst.executeQuery();
			List<String> alGrades = new ArrayList<String>();
			while(rs.next()) {
				alGrades.add(rs.getString("grade_id"));
			}
			rs.close();
			pst.close(); 
			
			
			if(alGrades != null && alGrades.size() > 0) {
				String strGradeIds = StringUtils.join(alGrades.toArray(),",");
				
				pst = con.prepareStatement("select count(sd.salary_head_id) as cnt,gd.grade_id from grades_details gd left join salary_details sd on sd.grade_id=gd.grade_id " +
						"where (sd.is_delete is null or sd.is_delete = false) and gd.grade_id in ("+strGradeIds+") group by gd.grade_id having count(sd.salary_head_id) <=1");
//				System.out.println("pst=======>"+pst);
				rs = pst.executeQuery();
				Map<String, String> hmGrade = new HashMap<String, String>();
				while(rs.next()) {
					hmGrade.put(rs.getString("grade_id"),rs.getString("cnt"));
				}
				rs.close();
				pst.close();
				
				
				pst = con.prepareStatement("insert into salary_details(salary_head_name,earning_deduction,salary_head_amount_type,sub_salary_head_id," +
						"salary_head_amount,isremove,isedit,weight,level_id,salary_head_id,org_id,is_variable,salary_type,is_ctc_variable," +
						"is_tax_exemption,added_by,added_date,is_incentive,is_delete,is_allowance,multiple_calculation,grade_id," +
						"is_align_with_perk,is_annual_variable,is_reimbursement_ctc,salary_calculate_amount,is_default_cal_allowance,is_contribution)" +
						"select salary_head_name,earning_deduction,salary_head_amount_type,sub_salary_head_id,salary_head_amount,isremove," +
						"isedit,weight,level_id,salary_head_id,org_id,is_variable,salary_type,is_ctc_variable,is_tax_exemption,added_by,added_date," +
						"is_incentive,is_delete,is_allowance,multiple_calculation,?,is_align_with_perk,is_annual_variable,is_reimbursement_ctc," +
						"salary_calculate_amount,is_default_cal_allowance,is_contribution from salary_details where grade_id = ? and salary_head_id !="+BASIC+" order by earning_deduction desc,weight");

				pst1 = con.prepareStatement("insert into salary_details(salary_head_name,earning_deduction,salary_head_amount_type,sub_salary_head_id," +
						"salary_head_amount,isremove,isedit,weight,level_id,salary_head_id,org_id,is_variable,salary_type,is_ctc_variable," +
						"is_tax_exemption,added_by,added_date,is_incentive,is_delete,is_allowance,multiple_calculation,grade_id," +
						"is_align_with_perk,is_annual_variable,is_reimbursement_ctc,salary_calculate_amount,is_default_cal_allowance,is_contribution)" +
						"select salary_head_name,earning_deduction,salary_head_amount_type,sub_salary_head_id,salary_head_amount,isremove," +
						"isedit,weight,level_id,salary_head_id,org_id,is_variable,salary_type,is_ctc_variable,is_tax_exemption,added_by,added_date," +
						"is_incentive,is_delete,is_allowance,multiple_calculation,?,is_align_with_perk,is_annual_variable,is_reimbursement_ctc," +
						"salary_calculate_amount,is_default_cal_allowance,is_contribution from salary_details where grade_id = ? order by earning_deduction desc,weight");
				boolean isAssignBasic = false;
				boolean isNotAssignBasic = false;
				for(int i=0; i < alGrades.size(); i++) {
					if(hmGrade.containsKey(alGrades.get(i)) && uF.parseToInt(hmGrade.get(alGrades.get(i))) == 1) {
						isAssignBasic = true;
						pst.setInt(1, uF.parseToInt(alGrades.get(i)));
						pst.setInt(2, uF.parseToInt(getStrGrade()));
						pst.addBatch();
					} else if(hmGrade.containsKey(alGrades.get(i)) && uF.parseToInt(hmGrade.get(alGrades.get(i))) == 0) {
						isNotAssignBasic = true;
						pst1.setInt(1, uF.parseToInt(alGrades.get(i)));
						pst1.setInt(2, uF.parseToInt(getStrGrade()));
						pst1.addBatch();
					} else if(!hmGrade.containsKey(alGrades.get(i))) {
						pst2 = con.prepareStatement("select * from salary_details where grade_id =? and (is_delete is null or is_delete = false) order by grade_id,earning_deduction desc,weight limit 1");
						pst2.setInt(1, uF.parseToInt(alGrades.get(i)));
						rs = pst2.executeQuery();
						boolean isSalaryHead = false; 
						if(rs.next()) {
							isSalaryHead = true; 
						}
						rs.close();
						pst2.close();
						
						if(!isSalaryHead) {
							isNotAssignBasic = true;
							pst1.setInt(1, uF.parseToInt(alGrades.get(i)));
							pst1.setInt(2, uF.parseToInt(getStrGrade()));
							pst1.addBatch();
						}
					}
				}
				
				if(isAssignBasic) {
					pst.executeBatch();
					pst.clearBatch();
				}
				pst.close();
				
				if(isNotAssignBasic) {
					pst1.executeBatch();
					pst1.clearBatch();
				}
				pst1.close();					
				
				con.commit();
			}			 
			
		} catch (Exception e) {
			try {
				con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();			
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst2);
			db.closeStatements(pst1);
			db.closeStatements(pst);
			db.closeConnection(con);
		}		
	}
	
	private void getSelectedFilter(UtilityFunctions uF,int nSalaryStrucuterType) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		alFilter.add("ORGANISATION");
		if(getStrOrg()!=null) {
			String strOrg="";
			int k=0;
			for(int i=0;orgList!=null && i<orgList.size();i++) {
				if(getStrOrg().equals(orgList.get(i).getOrgId())) {
					strOrg=orgList.get(i).getOrgName();
				}
			}
			if(strOrg!=null && !strOrg.equals("")) {
				hmFilter.put("ORGANISATION", strOrg);
			} else {
				hmFilter.put("ORGANISATION", "All Organizations");
			}
		} else {
			hmFilter.put("ORGANISATION", "All Organizations");
		}
		
		
		alFilter.add("LEVEL");
		if(getLevel()!=null) {
			String strLevel="";
			for(int i=0;levelList!=null && i<levelList.size();i++) {
				if(getLevel().equals(levelList.get(i).getLevelId())) {
					strLevel=levelList.get(i).getLevelCodeName();
				}
			}
			if(strLevel!=null && !strLevel.equals("")) {
				hmFilter.put("LEVEL", strLevel);
			} else {
				hmFilter.put("LEVEL", "-");
			}
		} else {
			hmFilter.put("LEVEL", "-");
		}
		
//		System.out.println("salBandList ===>> " +salBandList);
		if(salBandList!=null && salBandList.size()>0) {
//			System.out.println("salBandList.size() ===>> " + salBandList.size());
			alFilter.add("SALARY_BAND");
			if(getSalaryBand()!=null) {
				String strSalBand="";
				for(int i=0;salBandList!=null && i<salBandList.size();i++) {
					if(getSalaryBand().equals(salBandList.get(i).getSalaryBandId())) {
						strSalBand=salBandList.get(i).getSalaryBandName();
					}
				}
				if(strSalBand!=null && !strSalBand.equals("")) {
					hmFilter.put("SALARY_BAND", strSalBand);
				} else {
					hmFilter.put("SALARY_BAND", "-");
				}
			} else {
				hmFilter.put("SALARY_BAND", "-");
			}
		}
		
		if(nSalaryStrucuterType == S_GRADE_WISE) {
			alFilter.add("GRADE");
			if(getLevel()!=null) {
				String strGrade="";
				for(int i=0;gradeList!=null && i<gradeList.size();i++) {
					if(getStrGrade().equals(gradeList.get(i).getGradeId())) {
						strGrade=gradeList.get(i).getGradeCode();
					}
				}
				if(strGrade!=null && !strGrade.equals("")) {
					hmFilter.put("GRADE", strGrade);
				} else {
					hmFilter.put("GRADE", "-");
				}
			} else {
				hmFilter.put("GRADE", "-");
			}
		}
		
		String selectedFilter = CF.getSelectedFilter(CF, uF, alFilter, hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}


	private void viewSalaryDetailsByGrade(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst=null;
		PreparedStatement pst1 = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			String[] strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
			String strFinancialYearStart = strFinancialYearDates[0];
			String strFinancialYearEnd = strFinancialYearDates[1];			
			
			List<String> sHeadList=(List<String>)request.getAttribute("sHeadList");
			if(sHeadList==null) sHeadList=new ArrayList<String>();
			
			List<String> alInner = new ArrayList<String>();
			
			con = db.makeConnection(con);
			
//			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			
			Map<String, Map<String, String>> hmCurrencyDetails = CF.getCurrencyDetailsForPDF(con);
			String currId = CF.getOrgCurrencyIdByOrg(con, getStrOrg());
			String currency = null;
			if(uF.parseToInt(currId) > 0) {
				Map<String, String> hmCurr = hmCurrencyDetails.get(currId);
				if (hmCurr == null) hmCurr = new HashMap<String, String>();
				currency = hmCurr.get("SHORT_CURR")!=null && !hmCurr.get("SHORT_CURR").trim().equals("") ? hmCurr.get("SHORT_CURR") : "";
				request.setAttribute("currency", currency);
			}
			
			pst = con.prepareStatement("select * from salary_details where level_id=-1 and salary_head_id =? and is_reimbursement_ctc=?");
			pst.setInt(1, REIMBURSEMENT_CTC);
			pst.setBoolean(2, true);
			rs = pst.executeQuery();
			String strReimbursementCTC = null; 
			while (rs.next()) { 
				strReimbursementCTC =  rs.getString("salary_head_name");
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmSalaryMap = CF.getSalaryHeadsMapByGrade(con, uF.parseToInt(getStrGrade()));
			if(hmSalaryMap == null) hmSalaryMap = new LinkedHashMap<String, String>();
			if(!hmSalaryMap.containsKey(""+REIMBURSEMENT_CTC)) {
				hmSalaryMap.put(""+REIMBURSEMENT_CTC, uF.showData(strReimbursementCTC, ""));
			}
			
			List alSalaryDuplicationTracer = new ArrayList();
			Map<String, String> hmTotal = new HashMap<String, String>();
			double dblDAPercentAmt = 0.0d;
			List<String> alStatutoryIds = new ArrayList<String>();
			List<String> alBasisSalIds = new ArrayList<String>();
			Map<String, String> hmStatutoryIds = new HashMap<String, String>();
			
			pst = con.prepareStatement("SELECT * FROM salary_details WHERE level_id = ? and is_annual_variable=true and (is_delete is null or is_delete=false) order by weight");
			pst.setInt(1, uF.parseToInt(getStrGrade()));
			rs = pst.executeQuery();
			List<String> alAnnualSalaryHead = new ArrayList<String>();
			while(rs.next()) {
				if(!alAnnualSalaryHead.contains(rs.getString("salary_head_id"))) {
					alAnnualSalaryHead.add(rs.getString("salary_head_id"));
				}
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("SELECT * FROM salary_details WHERE grade_id = ? and (is_delete is null or is_delete=false) order by  earning_deduction desc, weight");
			pst.setInt(1, uF.parseToInt(getStrGrade()));
			rs = pst.executeQuery();
			List<String> alAssignToPercentageHead = new ArrayList<String>(); 
			StringBuffer sb = new StringBuffer();
			while(rs.next()) {
				
				alInner = new ArrayList<String>();
				alInner.add(rs.getString("salary_head_id"));	//0
				alInner.add(rs.getString("salary_head_name"));	//1
				alInner.add(rs.getString("earning_deduction"));	//2
				alInner.add(rs.getString("salary_head_amount_type"));	//3
				String rsHeadId = rs.getInt("sub_salary_head_id") + "";
				String alHeadId = "";
				double dblPercentAmt = 0.0d;
				
				alInner.add("");	//4
				
				StringBuilder sbMulcalType = new StringBuilder();
					
				if("P".equalsIgnoreCase(rs.getString("salary_head_amount_type"))) {
					String strMulCal = rs.getString("multiple_calculation");
					CF.appendMultiplePercentageCalType(uF,sbMulcalType,strMulCal,hmSalaryMap,alAnnualSalaryHead); 
					
					if(strMulCal != null && !strMulCal.trim().equals("") && !strMulCal.trim().equalsIgnoreCase("NULL")) {
						List<String> al = Arrays.asList(strMulCal.trim().split(","));
						int nAl = al != null ? al.size() : 0;
						for(int i = 0; i < nAl; i++) {
							String str = al.get(i);
							if(str != null && !str.trim().equals("") && !str.trim().equalsIgnoreCase("NULL")) {
								 boolean isInteger = uF.isInteger(str.trim());
								 if(isInteger) {
									if(!alAssignToPercentageHead.contains(str.trim())) {
										alAssignToPercentageHead.add(str.trim());
									}
								 }
							}
						}
					}
					
					double dblMulAmt = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("salary_calculate_amount"))));
					dblPercentAmt = dblMulAmt;
//						hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblMulAmt));
				}else{
//						hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("salary_head_amount"))));
				}	
				alInner.add((rs.getString("salary_head_amount_type") != null && rs.getString("salary_head_amount_type").equals("P")) ?  uF.formatIntoFourDecimalWithOutComma(uF.parseToDouble(rs.getString("salary_head_amount"))) : uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("salary_head_amount"))));	//5
				
				alInner.add(rs.getString("isremove"));	//6
				alInner.add(rs.getString("isedit"));	//7
				
				alInner.add(rs.getString("salary_id"));	//8
				alInner.add(rs.getString("weight"));	//9
				
//				if(uF.parseToInt(rs.getString("salary_head_id")) == DA) {
//					alInner.add(uF.formatIntoTwoDecimalWithOutComma(dblDAPercentAmt));	//10
//				} else {
					alInner.add(uF.formatIntoTwoDecimalWithOutComma(dblPercentAmt));	//10
//				}
				
				alInner.add(""+uF.parseToBoolean(rs.getString("is_incentive")));   // 11
				alInner.add(""+uF.parseToBoolean(rs.getString("is_allowance")));   // 12
				
				alInner.add(sbMulcalType.toString());   // 13
				
				
				boolean isAllowance = uF.parseToBoolean(rs.getString("is_allowance"));
				
				String isvariabledata="";
				if(!isAllowance && !sHeadList.contains(rs.getString("salary_head_id"))) {
					String ischecked="";
					if(uF.parseToBoolean(rs.getString("is_variable"))) {
						ischecked="checked";
					}
					isvariabledata ="<tr id=\"id_isvariable_"+rs.getString("salary_head_id")+"\"><td style=\"color: rgb(255, 255, 255); " +
							"font-style: italic; font-size: 12px;\">Is Variable:</td><td><input type=\"checkbox\" name=\"isVariable\"" +
							" id=\"isVariable\" "+ischecked+"/></td></tr>";
				}
				
				if(!isAllowance && !sHeadList.contains(rs.getString("salary_head_id")) && rs.getString("earning_deduction").equals("E")) {
					String ischecked="";
					if(uF.parseToBoolean(rs.getString("is_annual_variable"))) {
						ischecked="checked";
					}
					isvariabledata +="<tr id=\"id_isAnnualVariable_"+rs.getString("salary_head_id")+"\"><td style=\"color: rgb(255, 255, 255); " +
							"font-style: italic; font-size: 12px;\">Is Annual Variable:</td><td><input type=\"checkbox\" name=\"isAnnualVariable\"" +
							" id=\"isAnnualVariable\" "+ischecked+"/></td></tr>";
				}
				
				
				String isCTCvariabledata="";
				String ischecked="";
				String isTaxExemDisplay="none";
				if(uF.parseToBoolean(rs.getString("is_ctc_variable"))) {
					ischecked="checked";
					isTaxExemDisplay="table-row";
				}
				if(!isAllowance) {
					isCTCvariabledata="<tr><td style=\"color: rgb(255, 255, 255); " +
						"font-style: italic; font-size: 12px;\">Is CTC Variable:</td><td><input type=\"checkbox\" name=\"isCTCVariable\"  id=\"isCTCVariable_"+rs.getString("salary_id")+"\"  onclick=\"showTaxExemption(this.id);\" " +
						ischecked+"/></td></tr>";
				}
				
				String isTaxExemptiondata="";
				String isTaxExemptChecked="";
				if(uF.parseToBoolean(rs.getString("is_tax_exemption"))) {
					isTaxExemptChecked="checked";
				}
				if(!isAllowance) {
					isTaxExemptiondata="<tr id=\"id_isTaxExempt_"+rs.getString("salary_id")+"\" style=\"display:"+isTaxExemDisplay+";\"><td style=\"color: rgb(255, 255, 255); " +
						"font-style: italic; font-size: 12px;\">Is Tax Exemption:</td><td><input type=\"checkbox\" name=\"isTaxExemption\" id=\"isTaxExemption\" " +
						isTaxExemptChecked+"/></td></tr>";
				}
				
				String isAlignPerkdata="";
				String isAlignPerkChecked="";
				if(uF.parseToBoolean(rs.getString("is_align_with_perk"))) {
					isAlignPerkChecked="checked";
				}
				if(!isAllowance && !sHeadList.contains(rs.getString("salary_head_id")) && rs.getString("earning_deduction").equals("E")) {
					isAlignPerkdata="<tr id=\"id_isAlignPerk_"+rs.getString("salary_id")+"\"><td style=\"color: rgb(255, 255, 255); " +
						"font-style: italic; font-size: 12px;\" nowrap=\"nowrap\">Is Aligned with Perk:</td><td><input type=\"checkbox\" name=\"isAlignPerk\" id=\"isAlignPerk\" " +
						isAlignPerkChecked+"/></td></tr>";
				}
				
				/*value=\""+check+"\"*/				
				sb.append("<div id=\"popup_name_edit_"+rs.getString("salary_id")+"\" class=\"popup_block\">" +
					"<h5 class=\"textcolorWhite\">Edit the Head Field</h5>" +				
					"<form id=\"frmCreditDetails\" action=\"SalaryDetails.action\" method=\"post\">");
//				if(isAllowance) {
//					sb.append("<input type=\"hidden\" name=\"headAmount\" id=\"headAmount_"+rs.getString("salary_head_id")+"\" value=\"0\"/>");
//				}
				String strDAMsg = ""; 
				if(uF.parseToInt(rs.getString("salary_head_id")) == DA) {
					strDAMsg = "<span id=\"daSpan\" style=\"color: rgb(255, 255, 255); font-style: italic; font-size: 10px;width:100px\">This DA will be added with basic by default.</span>";
				}
				sb.append("<table class=\"table table_no_border\">" +
							"<tr style=\"display:table-row;\"><td></td><td><input type=\"hidden\" name=\"salaryHeadId\" value=\""+rs.getString("salary_head_id")+"\" ></input>" +
							"<input type=\"hidden\" name=\"salaryId\" value=\""+rs.getString("salary_id")+"\" ></input>" +
							"<input type=\"hidden\" name=\"level\" value=\""+getLevel()+"\" ></input>" +
							"<input type=\"hidden\" name=\"strGrade\" value=\""+getStrGrade()+"\" ></input>" +
							"<input type=\"hidden\" name=\"operation\" value=\"E\" ></input>" +
							"<input type=\"hidden\" name=\"earningOrDeduction\" id=\"headByte\" value=\""+rs.getString("earning_deduction")+"\"></input>" +
							"<input type=\"hidden\" name=\"strOrg\" value=\""+getStrOrg()+"\"></input>" +
							"<input type=\"hidden\" name=\"userscreen\" value=\""+getUserscreen()+"\"/>" +
							"<input type=\"hidden\" name=\"navigationId\" value=\""+getNavigationId()+"\"/>" +
							"<input type=\"hidden\" name=\"toPage\" value=\""+getToPage()+"\"/></td></tr>" +
							"<tr style=\"display:table-row;\">" +
								"<td style=\"color: rgb(255, 255, 255); font-style: italic; font-size: 12px;width:100px\" nowrap=\"nowrap\" valign=\"top\">Salary Head Name:</td>" +
								"<td><input type=\"text\" name=\"headName\" value=\""+rs.getString("salary_head_name")+"\" ></input><br/>" +
								""+ strDAMsg +								
								"</td>" +
							"</tr>"  
								+ isCTCvariabledata + isvariabledata + isTaxExemptiondata + isAlignPerkdata +"");	
				if(!isAllowance) {
					sb.append("<tr style=\"display:table-row;\">" + 
							"<td style=\"color: rgb(255, 255, 255); font-style: italic; font-size: 12px;width:100px\">Type of Head:</td>" +
							"<td>"); 
					if(uF.parseToDouble(rs.getString("salary_head_id")) == CTC) {
						sb.append("<select id=\"newSel_"+rs.getString("salary_head_id")+"\" name=\"headAmountType\" style=\"width:91px\" onchange=\"javascript:show_sub_salary_head(this.id);return false;\" disabled>");
					} else {
						sb.append("<select id=\"newSel_"+rs.getString("salary_head_id")+"\" name=\"headAmountType\" style=\"width:91px\" onchange=\"javascript:show_sub_salary_head(this.id);return false;\" >");
					}
					if(uF.parseToDouble(rs.getString("salary_head_id")) == CTC) {
						sb.append("<option value=\"A\" selected=\"selected\">A (Amount)</option>");
						sb.append("<option value=\"P\">% (Percentage)</option>");
						/*sb.append("<option value=\"MP\">% (Multiple)</option>");
						sb.append("<option value=\"M\">A (Multiple)</option>");*/
					} else {
						/*if(rs.getString("salary_head_amount_type").trim().equals("M")) {
							sb.append("<option value=\"P\">% (Single)</option>");
							sb.append("<option value=\"A\">A (Single)</option>");
							sb.append("<option value=\"MP\">% (Multiple)</option>");
							sb.append("<option value=\"M\" selected=\"selected\">A (Multiple)</option>");
						} else if(rs.getString("salary_head_amount_type").trim().equals("MP")) {
							sb.append("<option value=\"P\">% (Single)</option>");
							sb.append("<option value=\"A\">A (Single)</option>");
							sb.append("<option value=\"MP\" selected=\"selected\">% (Multiple)</option>");
							sb.append("<option value=\"M\">A (Multiple)</option>");
						} else */if(rs.getString("salary_head_amount_type").trim().equals("P")) {
							sb.append("<option value=\"A\">A (Amount)</option>");
							sb.append("<option value=\"P\" selected=\"selected\">% (Percentage)</option>");
							/*sb.append("<option value=\"MP\">% (Multiple)</option>");
							sb.append("<option value=\"M\">A (Multiple)</option>");*/
						} else {
							sb.append("<option value=\"A\" selected=\"selected\">A (Amount)</option>");
							sb.append("<option value=\"P\">% (Percentage)</option>");							
							/*sb.append("<option value=\"MP\">% (Multiple)</option>");
							sb.append("<option value=\"M\">A (Multiple)</option>");*/
						}
					}
					
					sb.append("</select>" +
								"</td>" +
								"</tr>");
				} else {
					sb.append("<tr style=\"display:table-row;\">" + 
							"<td>&nbsp;</td>" +
							"<td><input type=\"hidden\" id=\"newSel_"+rs.getString("salary_head_id")+"\" name=\"headAmountType\" value=\"A\" ></td></tr>");
				}
							
				/*sb.append("<tr style=\"display:table-row;\" id=\"id_salaryHead_"+rs.getString("salary_head_id")+"\">" +
							"<td style=\"color: rgb(255, 255, 255); font-style: italic; font-size: 12px;width:100px\">% Of:</td>" +
							"<td>" +
							"<select name=\"salarySubHead\" id=\"salarySubHead_"+rs.getString("salary_head_id")+"\">" );
				sb.append("<option value=\"0\">Select Salary Head</option>");			
				for(FillSalaryHeads sd :salaryHeadList) {
						if(sd.getSalaryHeadId().equals(rs.getString("sub_salary_head_id"))) {
							
							sb.append("<option value=\""+sd.getSalaryHeadId()+"\" selected=\"selected\"> "+sd.getSalaryHeadName()+"</option>");
						}else
							sb.append("<option value=\""+sd.getSalaryHeadId()+"\"> "+sd.getSalaryHeadName()+"</option>");
				}
				
				sb.append("</select>" +									
							"</td>" +
						"</tr>");*/
				if(isAllowance) {					
					String isDefaultCalAllowanceChecked="";
					String isShowDefaultAllowanceAmount="none";
					if(uF.parseToBoolean(rs.getString("is_default_cal_allowance"))) {
						isDefaultCalAllowanceChecked="checked";
						isShowDefaultAllowanceAmount="table-row";
					}
					sb.append("<tr id=\"id_isDefaultCalculateAllowance_"+rs.getString("salary_head_id")+"\">" +
							"<td style=\"color: rgb(255, 255, 255); font-style: italic; font-size: 12px;\">Calculate with default figure:</td>" +
							"<td><input type=\"checkbox\" name=\"isDefaultCalculateAllowance\" id=\"isDefaultCalculateAllowance_"+rs.getString("salary_head_id")+"\" " +
							"onclick=\"showDefaultAmount(this.id);\" "+isDefaultCalAllowanceChecked+"/></td>" +
							"</tr>" +
							"<tr id=\"id_headAmountAllowance_"+rs.getString("salary_head_id")+"\" style=\"display:"+isShowDefaultAllowanceAmount+";\">" +
							"<td style=\"color: rgb(255, 255, 255); font-style: italic; font-size: 12px;\">Default Amount ("+uF.showData(currency, "")+"):</td>" +
							"<td><input type=\"text\" name=\"headAmount\" id=\"headAmountAllowance_"+rs.getString("salary_head_id")+"\" value=\""+((rs.getString("salary_head_amount_type") != null && rs.getString("salary_head_amount_type").equals("P")) ?  uF.formatIntoFourDecimalWithOutComma(uF.parseToDouble(rs.getString("salary_head_amount"))) : uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("salary_head_amount"))))+"\"/></td>" +
							"</tr>");
				} else {	
					sb.append("<tr style=\"display:table-row;\" id=\"id_headAmount_"+rs.getString("salary_head_id")+"\">" +
							"<td style=\"color: rgb(255, 255, 255); font-style: italic; font-size: 12px;width:100px\"><span id=\"id_headAmountType_"+rs.getString("salary_head_id")+"\">Percentage:</span></td>" +
							"<td>");
					sb.append("<input type=\"text\" name=\"headAmount\" id=\"headAmount_"+rs.getString("salary_head_id")+"\" value=\""+((rs.getString("salary_head_amount_type") != null && rs.getString("salary_head_amount_type").equals("P")) ?  uF.formatIntoFourDecimalWithOutComma(uF.parseToDouble(rs.getString("salary_head_amount"))) : uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("salary_head_amount"))))+"\"  />");
					sb.append("</td></tr>");
					sb.append("<tr style=\"display:table-row;\" id=\"id_headMaxCapAmount_"+rs.getString("salary_head_id")+"\">" +
							"<td style=\"color: rgb(255, 255, 255); font-style: italic; font-size: 12px;width:100px\"><span>Max Cap Amount:</span></td>" +
							"<td>");
					sb.append("<input type=\"text\" name=\"headMaxCapAmount\" id=\"headMaxCapAmount_"+rs.getString("salary_head_id")+"\" value=\""+uF.formatIntoFourDecimalWithOutComma(uF.parseToDouble(rs.getString("max_cap_amount")))+"\"  />");
					sb.append("</td></tr>");
				}

				/**
				 * Multiple Percentage Calculation
				 * */
				String strMulCalPerFormula = "";
				if(rs.getString("salary_head_amount_type").trim().equals("P")) {
					strMulCalPerFormula = sbMulcalType.toString();
				}
				sb.append("<tr id=\"trMultiplePercentageCalType_"+rs.getString("salary_head_id")+"\" style=\"display: none;\">" +
						"<td style=\"color: rgb(255, 255, 255); font-style: italic; font-size: 12px;\">&nbsp;</td>");
				sb.append("<td>");
				
				sb.append("<select id=\"bracketMulP_"+rs.getString("salary_head_id")+"\" name=\"bracketMulP\" style=\"width:50px !important;\">");
				sb.append("<option value=\"\">Select</option>");
				sb.append("<option value=\"+\">+</option>");
				sb.append("<option value=\"-\">-</option>");
				sb.append("<option value=\"*\">*</option>");
				sb.append("<option value=\"/\">/</option>");
				sb.append("<option value=\"(\">(</option>");
				sb.append("<option value=\")\">)</option>");
				sb.append("</select>");
				sb.append("<a href=\"javascript:void(0)\" class=\"add_lvl\" onclick=\"addMulPerCal('1','bracketMulP_"+rs.getString("salary_head_id")+"','salaryHeadMulP_"+rs.getString("salary_head_id")+"','strMulPerCalFormula_"+rs.getString("salary_head_id")+"','spanMulCalPercentage_"+rs.getString("salary_head_id")+"');\" title=\"Add Sign\"></a>");
				
				sb.append("<select name=\"salaryHeadMulP\" id=\"salaryHeadMulP_"+rs.getString("salary_head_id")+"\" style=\"width:91px !important;\">");
				sb.append("<option value=\"\">Select Salary Head</option>");
				
				pst1 = con.prepareStatement("SELECT * FROM salary_details WHERE grade_id=? and salary_head_id !=? and weight <? " +
						"and (is_delete is null or is_delete=false) order by earning_deduction desc, weight");
				pst1.setInt(1, uF.parseToInt(getStrGrade()));
				pst1.setInt(2, uF.parseToInt(rs.getString("salary_head_id")));
				pst1.setInt(3, uF.parseToInt(rs.getString("weight")));
//				System.out.println("pst 1 ===>> " + pst1);
				rs1 = pst1.executeQuery();
				List<String> salHeadList = new ArrayList<String>();
				while (rs1.next()) {
					sb.append("<option value=\""+rs1.getString("salary_head_id")+"\"> "+rs1.getString("salary_head_name")+"</option>");
					if(!salHeadList.contains(rs1.getString("salary_head_id")) && (rs1.getInt("salary_head_id") == EMPLOYEE_EPF || rs1.getInt("salary_head_id") == EMPLOYEE_ESI || rs1.getInt("salary_head_id") == EMPLOYEE_LWF)) {
						salHeadList.add(rs1.getString("salary_head_id"));
					}
				}
				rs1.close();
				pst1.close();
				if(salHeadList != null && salHeadList.contains(EMPLOYEE_EPF+"")) {
					sb.append("<option value=\""+EMPLOYER_EPF+"\">Employer PF</option>");
				}
				if(salHeadList != null && salHeadList.contains(EMPLOYEE_ESI+"")) {
					sb.append("<option value=\""+EMPLOYER_ESI+"\">Employer ESI</option>");
				} 
				if(salHeadList != null && salHeadList.contains(EMPLOYEE_LWF+"")) {
					sb.append("<option value=\""+EMPLOYER_LWF+"\">Employer LWF</option>");
				}
				sb.append("</select>");
				
				sb.append("<a href=\"javascript:void(0)\" class=\"add_lvl\" onclick=\"addMulPerCal('2','bracketMulP_"+rs.getString("salary_head_id")+"','salaryHeadMulP_"+rs.getString("salary_head_id")+"','strMulPerCalFormula_"+rs.getString("salary_head_id")+"','spanMulCalPercentage_"+rs.getString("salary_head_id")+"');\" title=\"Add Head\"></a>");
				sb.append("<img src=\"images1/icons/hd_cross_16x16.png\" style=\"vertical-align: top;\" onclick=\"resetMulPerCal('strMulPerCalFormula_"+rs.getString("salary_head_id")+"','spanMulCalPercentage_"+rs.getString("salary_head_id")+"');\">");
				sb.append("<br/>");
				sb.append("<input type=\"hidden\" name=\"strMulPerCalFormula\" id=\"strMulPerCalFormula_"+rs.getString("salary_head_id")+"\" value=\""+uF.showData(rs.getString("multiple_calculation"), "")+"\"/>");
				sb.append("<span id=\"spanMulCalPercentage_"+rs.getString("salary_head_id")+"\" style=\"color: rgb(255, 255, 255); font-style: italic; font-size: 12px;\">"+uF.showData(strMulCalPerFormula, "")+"</span>");
				
				sb.append("</td></tr>");
				/**
				 * Multiple Percentage Calculation End
				 * */

				if(isAllowance) {
					sb.append("<tr>"+
							"<td style=\"color: rgb(255, 255, 255); font-style: italic; font-size: 12px;width:100px\"></td>"+
							"<td style=\"color: rgb(255, 255, 255); font-style: italic; font-size: 12px;width:100px\">");
					sb.append("<input type=\"hidden\" name=\"salary_type\" value=\"F\"/>");
					sb.append("</td></tr>");
				} else {
					sb.append("<tr>"+
							"<td style=\"color: rgb(255, 255, 255); font-style: italic; font-size: 12px;width:100px\">Salary Type:</td>"+
							"<td style=\"color: rgb(255, 255, 255); font-style: italic; font-size: 12px;width:100px\">");
					if("F".equalsIgnoreCase(rs.getString("salary_type"))) {
						sb.append("<input type=\"radio\" name=\"salary_type\" value=\"M\" /> Monthly &nbsp;" +
								"<input type=\"radio\" name=\"salary_type\" value=\"D\" />Daily " +
								"<input type=\"radio\" name=\"salary_type\" value=\"F\" checked />Fixed ");
					}else if("D".equalsIgnoreCase(rs.getString("salary_type"))) {
						sb.append("<input type=\"radio\" name=\"salary_type\" value=\"M\" /> Monthly &nbsp;" +
								"<input type=\"radio\" name=\"salary_type\" value=\"D\" checked />Daily " +
								"<input type=\"radio\" name=\"salary_type\" value=\"F\"  />Fixed ");
					}else{
						sb.append("<input type=\"radio\" name=\"salary_type\" value=\"M\" checked /> Monthly &nbsp;" +
								"<input type=\"radio\" name=\"salary_type\" value=\"D\" />Daily " +
								"<input type=\"radio\" name=\"salary_type\" value=\"F\" />Fixed ");
					}
					sb.append("</td></tr>");
				}
						
				sb.append("<tr style=\"display:table-row;\"><td></td>" + 
							"<td><input type=\"submit\" class=\"btn btn-primary\" value=\"Submit\" align=\"center\"></input></td>" +
						"</tr></table></form></div>");
				
				int index = alSalaryDuplicationTracer.indexOf(rs.getString("salary_head_id"));
				if(index>=0) {
					al.remove(index);
					al.add(index, alInner);
				}else{
					alSalaryDuplicationTracer.add(rs.getString("salary_head_id"));
					al.add(alInner);
				}
				
				if(!alStatutoryIds.contains(rs.getString("salary_head_id")) 
						&& (uF.parseToInt(rs.getString("salary_head_id")) == PROFESSIONAL_TAX
						|| uF.parseToInt(rs.getString("salary_head_id")) == EMPLOYEE_EPF
						|| uF.parseToInt(rs.getString("salary_head_id")) == EMPLOYEE_ESI
						|| uF.parseToInt(rs.getString("salary_head_id")) == EMPLOYEE_LWF
						|| uF.parseToInt(rs.getString("salary_head_id")) == TDS)) {
					alStatutoryIds.add(rs.getString("salary_head_id"));
					hmStatutoryIds.put(rs.getString("salary_head_id"), rs.getString("salary_id"));
				}
				if(!alBasisSalIds.contains(rs.getString("salary_head_id"))  && (uF.parseToInt(rs.getString("salary_head_id")) == BASIC
						|| uF.parseToInt(rs.getString("salary_head_id")) == CTC)) {
					alBasisSalIds.add(rs.getString("salary_head_id"));
				}
					
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("SELECT max(added_date) as added_date,added_by FROM salary_details WHERE grade_id = ? and added_by >0 group by added_by limit 1");
			pst.setInt(1, uF.parseToInt(getStrGrade()));
			rs = pst.executeQuery();
			int addedBy = 0;
			while(rs.next()) {
				addedBy = uF.parseToInt(rs.getString("added_by"));

				request.setAttribute("UPDATED_DATE", uF.getDateFormat(rs.getString("added_date"), DBDATE, CF.getStrReportDateFormat()));
			}
			rs.close();
			pst.close();
			
			if(addedBy > 0) {
				String strAddedByName = CF.getEmpNameMapByEmpId(con, ""+addedBy);
				request.setAttribute("UPDATED_NAME", uF.showData(strAddedByName, ""));
			}
			
			request.setAttribute("reportList", al);
			request.setAttribute("sb", sb.toString());
			request.setAttribute("alStatutoryIds", alStatutoryIds);
			request.setAttribute("alBasisSalIds", alBasisSalIds);
			request.setAttribute("hmStatutoryIds", hmStatutoryIds);
		} catch (Exception e) {
			e.printStackTrace();
			
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}

	private void editSalaryHeadByGrade(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);		
		try {
			
			con = db.makeConnection(con);
			
			String multipleCal = getMulitpleCalFormula(uF); 
			double dblAmount = 0.0d;
			if(getHeadAmountType()!=null && getHeadAmountType().trim().equals("P")) { 
				String strFormula = CF.getGradeStructureFormula(con, uF, getStrGrade(),getStrMulPerCalFormula());
				if(uF.parseToDouble(getHeadAmount()) > 0.0d && strFormula != null && strFormula.length() > 0) {
					double dblPerAmount = uF.eval(strFormula);	
//					System.out.println("uF.parseToDouble(getHeadAmount())==>"+uF.parseToDouble(getHeadAmount())+"strFormula==>"+strFormula+"--dblPerAmount==>"+dblPerAmount);
					dblAmount = (uF.parseToDouble(getHeadAmount()) * dblPerAmount)/100;
				}
			}
			
			pst = con.prepareStatement("select * from salary_details where salary_head_id=? and salary_id=? and is_allowance=true");
			pst.setInt(1, uF.parseToInt(getSalaryHeadId()));
			pst.setInt(2, uF.parseToInt(getSalaryId()));
			rs = pst.executeQuery();
			boolean isAllowance = false; 
			if(rs.next()) {
				isAllowance = true;
			}
			rs.close();
			pst.close();
			
			double dblHeadAmount = uF.parseToDouble(getHeadAmount());
			if(isAllowance) {
				if(uF.parseToBoolean(getIsDefaultCalculateAllowance())) {
					dblHeadAmount = uF.parseToDouble(getHeadAmount());
				} else {
					dblHeadAmount = 0.0d;
				}
			}
			
//			pst = con.prepareStatement(updateSalaryDetails);
			pst = con.prepareStatement("UPDATE salary_details set salary_head_name = ?, earning_deduction = ?, salary_head_amount_type = ?, " +
					"sub_salary_head_id = ?, salary_head_amount = ?,is_variable=?, salary_type=? ,is_ctc_variable=?,added_by=?,added_date=?," +
					"is_tax_exemption=?,multiple_calculation=?,is_align_with_perk=?,is_annual_variable=?,salary_calculate_amount=?," +
					"is_default_cal_allowance=?,is_contribution=? where salary_head_id= ? and salary_id=?");
			pst.setString(1, getHeadName());
			pst.setString(2, getEarningOrDeduction());
			pst.setString(3, uF.parseToInt(getSalaryHeadId()) == CTC ? "A" : getHeadAmountType());
			pst.setInt(4, (getHeadAmountType()!=null && getHeadAmountType().trim().equals("P")) ? uF.parseToInt(getSalarySubHead()) : 0);
			pst.setDouble(5, dblHeadAmount);
			pst.setBoolean(6, uF.parseToBoolean(getIsVariable()));
			pst.setString(7, getSalary_type());
			pst.setBoolean(8, uF.parseToBoolean(getIsCTCVariable()));
			pst.setInt(9, uF.parseToInt((String) session.getAttribute(EMPID)));
			pst.setDate(10, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setBoolean(11, uF.parseToBoolean(getIsCTCVariable()) ? uF.parseToBoolean(getIsTaxExemption()) : false);
			pst.setString(12, multipleCal);
			pst.setBoolean(13, uF.parseToBoolean(getIsAlignPerk()));
			pst.setBoolean(14, uF.parseToBoolean(getIsAnnualVariable()));
			pst.setDouble(15, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblAmount)));
			pst.setBoolean(16, uF.parseToBoolean(getIsDefaultCalculateAllowance()));
			pst.setBoolean(17, uF.parseToBoolean(getIsContribution()));
			pst.setInt(18, uF.parseToInt(getSalaryHeadId()));
			pst.setInt(19, uF.parseToInt(getSalaryId()));
			System.out.println("3 pst ===>> " + pst);
			int x = pst.executeUpdate();
			pst.close();
//			System.out.println("updateSalaryDetails==>"+pst);
			if(x == 0) {
				addNewSalaryHeadByGrade(uF.parseToInt(getSalaryHeadId()));
			} else if(x > 0) {
				updateRelativeHeadAmountByGrade(con, uF, uF.parseToInt(getStrGrade()), uF.parseToInt(getSalaryHeadId()));
				
				if(getHeadAmountType()!=null && getHeadAmountType().trim().equalsIgnoreCase("P")) { 
					String strDomain = request.getServerName().split("\\.")[0];
					EmpSalaryUpdateCron updateCron = new EmpSalaryUpdateCron();
					updateCron.request = request;
					updateCron.session = session;
					updateCron.CF = CF;
					updateCron.strDomain = strDomain;
					updateCron.setStrGradeId(getStrGrade());
					updateCron.setStrSalaryHeadId(getSalaryHeadId());
					updateCron.setStrType("edit");
					updateCron.setAutoUpdate(getAutoUpdate());
					updateCron.setEmpSalaryUpdateCronData();
				}
			}
			                   
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	private void updateRelativeHeadAmountByGrade(Connection con, UtilityFunctions uF, int nGradeId, int nSalaryHeadId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			if(nSalaryHeadId > 0) {
				pst = con.prepareStatement("SELECT * FROM salary_details WHERE grade_id = ? and multiple_calculation like '%,"+nSalaryHeadId+",%' " +
						"and (is_delete is null or is_delete=false) order by earning_deduction desc, weight");
				pst.setInt(1, nGradeId);
//				System.out.println("pst==>"+pst);
				rs = pst.executeQuery();
				List<Map<String, String>> alSal = new ArrayList<Map<String,String>>();
				while(rs.next()) {
					Map<String, String> hmSal = new HashMap<String, String>();
					
					hmSal.put("MULTIPLE_CALCULATION", rs.getString("multiple_calculation"));
					hmSal.put("SALARY_HEAD_AMOUNT_TYPE", rs.getString("salary_head_amount_type"));
					hmSal.put("SALARY_HEAD_ID", rs.getString("salary_head_id"));
					hmSal.put("SALARY_ID", rs.getString("salary_id"));
					hmSal.put("SALARY_HEAD_AMOUNT", rs.getString("salary_head_amount"));
					
					alSal.add(hmSal);
				}
				rs.close();
				pst.close();
				
				for(Map<String, String> hmSal : alSal) {					
					String strMulPerCalFormula = hmSal.get("MULTIPLE_CALCULATION");
					String strSalaryHeadAmtType = hmSal.get("SALARY_HEAD_AMOUNT_TYPE");
					int nCalSalHeadId = uF.parseToInt(hmSal.get("SALARY_HEAD_ID"));
					int nCalSalId = uF.parseToInt(hmSal.get("SALARY_ID"));
					String strPercentage = hmSal.get("SALARY_HEAD_AMOUNT");	
						
					if(nCalSalId > 0 && nCalSalHeadId > 0 && strSalaryHeadAmtType!=null && strSalaryHeadAmtType.trim().equalsIgnoreCase("P")) {
						double dblAmount = 0.0d;
						String strFormula = CF.getGradeStructureFormula(con, uF, ""+nGradeId,strMulPerCalFormula);
						if(uF.parseToDouble(strPercentage) > 0.0d && strFormula != null && strFormula.length() > 0) {
							double dblPerAmount = uF.eval(strFormula);	
							dblAmount = (uF.parseToDouble(strPercentage) * dblPerAmount)/100;
//							System.out.println("nCalSalHeadId==>"+nCalSalHeadId+"--strPercentage==>"+uF.parseToDouble(strPercentage)+"strFormula==>"+strFormula+"--dblPerAmount==>"+dblPerAmount+"--dblAmount==>"+dblAmount);
						}
						
						pst = con.prepareStatement("update salary_details set salary_calculate_amount=? WHERE grade_id= ? and salary_head_id=? and salary_id=?");
						pst.setDouble(1, uF.parseToDouble(uF.formatIntoFourDecimalWithOutComma(dblAmount)));
						pst.setInt(2, nGradeId);
						pst.setInt(3, nCalSalHeadId);
						pst.setInt(4, nCalSalId);
						int x = pst.executeUpdate();
						if(x > 0) {
							updateRelativeHeadAmountByGrade(con, uF, nGradeId, nCalSalHeadId);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void addNewSalaryHeadByGrade(int nSalaryHeadId) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			con = db.makeConnection(con);
			
			if(nSalaryHeadId==0) {
				pst = con.prepareStatement("SELECT max(salary_head_id) as salary_head_id from salary_details");
				rs = pst.executeQuery();
				while(rs.next()) {
					nSalaryHeadId = rs.getInt("salary_head_id");
				}
				rs.close();
				pst.close();
				
				nSalaryHeadId++;
				
				if(nSalaryHeadId == GROSS) {
					nSalaryHeadId++;
				}
			}
			
//			Map<String, String> hmSalaryMap = CF.getSalaryHeadsMap(con);
			
//			int nWeight = 100;
			int nWeight = 0;
			pst = con.prepareStatement("SELECT max(weight) weight FROM salary_details WHERE salary_head_id=? and (is_delete is null or is_delete=false)");
			pst.setInt(1, uF.parseToInt(getHeadName()));
			rs = pst.executeQuery();
//			System.out.println("pst ===>> " + pst);
			while(rs.next()) {
				nWeight = rs.getInt("weight");
			}
			rs.close();
			pst.close();
			
			if(nWeight == 0) {
				pst = con.prepareStatement("SELECT max(weight) weight FROM salary_details WHERE grade_id = ? and (is_delete is null or is_delete=false)");
				pst.setInt(1, uF.parseToInt(getStrGrade()));
				rs = pst.executeQuery();
	//			System.out.println("pst ===>> " + pst);
				while(rs.next()) {
					nWeight = rs.getInt("weight");
				}
				rs.close();
				pst.close();
				nWeight +=1;
			}
			
//			System.out.println("nWeight ===>> " +nWeight);
			
			boolean checkFlag = false;
			pst = con.prepareStatement("SELECT * from salary_details where salary_head_id=? and grade_id=? and (is_delete is null or is_delete=false)");
			pst.setInt(1, uF.parseToInt(getHeadName()));
			pst.setInt(2, uF.parseToInt(getStrGrade()));
			rs = pst.executeQuery();
			while(rs.next()) {
				checkFlag = true;
			}
			rs.close();
			pst.close();
			
			if(!checkFlag) {
				int x =0;
				int nSalId = 0;
//				String multipleCal = (getHeadAmountType()!=null && getHeadAmountType().trim().equals("M")) ? uF.parseToInt(getSalaryHead1())+","+getSign1()+","+uF.parseToInt(getSalaryHead2())+","+getSign2()+","+uF.parseToInt(getSalaryHead3())+","+getSign3()+","+uF.parseToInt(getSalaryHead4())+","+getSign4()+","+uF.parseToInt(getSalaryHead5()) : null;
				String multipleCal = getMulitpleCalFormula(uF); 
//				System.out.println("mutlipeCal=====>"+mutlipeCal);
				
				double dblAmount = 0.0d;
				if(getHeadAmountType()!=null && getHeadAmountType().trim().equals("P")) { 
					String strFormula = CF.getGradeStructureFormula(con, uF, getStrGrade(),getStrMulPerCalFormula());
					if(uF.parseToDouble(getHeadAmount()) > 0.0d && strFormula != null && strFormula.length() > 0) {
						double dblPerAmount = uF.eval(strFormula);	
						dblAmount = (uF.parseToDouble(getHeadAmount()) * dblPerAmount)/100;
					}
				}
				
				if(uF.parseToInt(getHeadName())>0) {
					pst = con.prepareStatement("select * from salary_details where salary_head_id=? and (is_delete is null or is_delete=false) limit 1");
					pst.setInt(1, uF.parseToInt(getHeadName()));
					rs = pst.executeQuery();
					String strSalaryHeadName = null;
					if(rs.next()) {
						strSalaryHeadName = rs.getString("salary_head_name");
					}
					rs.close();
					pst.close();
					
					pst = con.prepareStatement("INSERT INTO salary_details (salary_head_name, earning_deduction,salary_head_amount_type, " +
						"sub_salary_head_id, salary_head_amount, grade_id, salary_head_id, weight, org_id,is_variable, salary_type," +
						"is_ctc_variable,is_tax_exemption,added_by,added_date,is_incentive,is_allowance,multiple_calculation,is_align_with_perk," +
						"is_annual_variable,salary_calculate_amount,is_default_cal_allowance,is_contribution) " +
						"VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?)");
//					pst.setString(1, hmSalaryMap.get(getHeadName()));
					pst.setString(1, strSalaryHeadName);
					pst.setString(2, getEarningOrDeduction());
					pst.setString(3, getHeadAmountType());
					pst.setInt(4, (getHeadAmountType()!=null && getHeadAmountType().trim().equals("P")) ? uF.parseToInt(getSalarySubHead()) : 0);
					pst.setDouble(5, uF.parseToDouble(getHeadAmount()));
					pst.setInt(6, uF.parseToInt(getStrGrade()));
					pst.setInt(7, uF.parseToInt(getHeadName()));
					pst.setInt(8, nWeight);
					pst.setInt(9, uF.parseToInt(getStrOrg()));
					pst.setBoolean(10, uF.parseToBoolean(getIsVariable()));
					pst.setString(11, getSalary_type());
					pst.setBoolean(12, uF.parseToBoolean(getIsCTCVariable()));
					pst.setBoolean(13, uF.parseToBoolean(getIsCTCVariable()) ? uF.parseToBoolean(getIsTaxExemption()) : false);
					pst.setInt(14, uF.parseToInt((String) session.getAttribute(EMPID)));
					pst.setDate(15, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setBoolean(16, uF.parseToBoolean(getStrIsIncentive()));
					pst.setBoolean(17, uF.parseToBoolean(getStrIsAllowance()));
					pst.setString(18, multipleCal);
					pst.setBoolean(19, uF.parseToBoolean(getIsAlignPerk()));
					pst.setBoolean(20, uF.parseToBoolean(getIsAnnualVariable()));
					pst.setDouble(21, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblAmount)));
					pst.setBoolean(22, uF.parseToBoolean(getIsDefaultCalculateAllowance()));
					pst.setBoolean(23, uF.parseToBoolean(getIsContribution()));
//					System.out.println("4 pst==>"+pst);
					x = pst.executeUpdate();
					pst.close();
					
					nSalId = uF.parseToInt(getHeadName());
					
				} else {
					//pst = con.prepareStatement(insertSalaryDetails1);
					pst=con.prepareStatement("INSERT INTO salary_details (salary_head_name, earning_deduction,salary_head_amount_type, " +
						"sub_salary_head_id, salary_head_amount, grade_id, salary_head_id, weight, org_id,is_variable, salary_type," +
						"is_ctc_variable,is_tax_exemption,added_by,added_date,is_incentive,is_allowance,multiple_calculation,is_align_with_perk," +
						"is_annual_variable,salary_calculate_amount,is_default_cal_allowance,is_contribution) " +
						"VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?)");
					pst.setString(1, getHeadNameOther());
					pst.setString(2, getEarningOrDeduction());
					pst.setString(3, getHeadAmountType());
					pst.setInt(4, (getHeadAmountType()!=null && getHeadAmountType().trim().equals("P")) ? uF.parseToInt(getSalarySubHead()) : 0);
					pst.setDouble(5, uF.parseToDouble(getHeadAmount()));
					pst.setInt(6, uF.parseToInt(getStrGrade()));
					pst.setInt(7, nSalaryHeadId);
					pst.setInt(8, nWeight);
					pst.setInt(9, uF.parseToInt(getStrOrg()));
					pst.setBoolean(10, uF.parseToBoolean(getIsVariable()));
					pst.setString(11, getSalary_type());
					pst.setBoolean(12, uF.parseToBoolean(getIsCTCVariable()));
					pst.setBoolean(13, uF.parseToBoolean(getIsCTCVariable()) ? uF.parseToBoolean(getIsTaxExemption()) : false);
					pst.setInt(14, uF.parseToInt((String) session.getAttribute(EMPID)));
					pst.setDate(15, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setBoolean(16, uF.parseToBoolean(getStrIsIncentive()));
					pst.setBoolean(17, uF.parseToBoolean(getStrIsAllowance()));
					pst.setString(18, multipleCal);
					pst.setBoolean(19, uF.parseToBoolean(getIsAlignPerk()));
					pst.setBoolean(20, uF.parseToBoolean(getIsAnnualVariable()));
					pst.setDouble(21, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblAmount)));
					pst.setBoolean(22, uF.parseToBoolean(getIsDefaultCalculateAllowance()));
					pst.setBoolean(23, uF.parseToBoolean(getIsContribution()));
//					System.out.println("5 pst==>"+pst);
					x = pst.executeUpdate();
					pst.close();
					
					nSalId = nSalaryHeadId;
				}
				
				if(x > 0) {
					String strDomain = request.getServerName().split("\\.")[0];
					EmpSalaryUpdateCron updateCron = new EmpSalaryUpdateCron();
					updateCron.request = request;
					updateCron.session = session;
					updateCron.CF = CF;
					updateCron.strDomain = strDomain;
					updateCron.setStrGradeId(getStrGrade());
					updateCron.setStrSalaryHeadId(""+nSalId);
					updateCron.setStrType("add");
					updateCron.setAutoUpdate(getAutoUpdate());
					updateCron.setEmpSalaryUpdateCronData();
				}
			}
			
			setHeadName(null);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}

	private void addCTCBasicSalaryBasisHeadByGrade(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			String strSalId = (String) request.getParameter("SALID");
			
//			Map<String, String> hmSalaryMap = CF.getSalaryHeadsMap(con);
			
			pst = con.prepareStatement("SELECT * from salary_details where grade_id = ?");
			pst.setInt(1, uF.parseToInt(getStrGrade()));
			rs = pst.executeQuery();
			List<String> al = new ArrayList<String>();
			while(rs.next()) {
				al.add(rs.getString("salary_id"));
			}
			
			if(al.size() > 0) {
				for(int i = 0; i < al.size(); i++) {
					String strSalaryId = al.get(i);
					pst = con.prepareStatement("select * FROM salary_details WHERE salary_id = ?");
					pst.setInt(1, uF.parseToInt(strSalaryId));
					rs = pst.executeQuery();
					int nGradeId = 0;
					int nOrgId = 0;
					int nSalaryHeadId = 0;
					while(rs.next()) {
						nGradeId = uF.parseToInt(rs.getString("grade_id"));
						nOrgId = uF.parseToInt(rs.getString("org_id"));
						nSalaryHeadId = uF.parseToInt(rs.getString("salary_head_id"));
					}
					rs.close();
					pst.close();
					
					pst = con.prepareStatement("select * from payroll_generation where salary_head_id=? and emp_id in (select emp_id from employee_personal_details epd," +
							" employee_official_details eod where epd.emp_per_id=eod.emp_id and eod.org_id=? and eod.grade_id in (select gd.grade_id from grades_details gd, " +
							"designation_details dd where gd.designation_id = dd.designation_id and gd.grade_id=?))");
					pst.setInt(1,nSalaryHeadId);
					pst.setInt(2,nOrgId);
					pst.setInt(3,nGradeId);
					rs = pst.executeQuery();
					boolean flag = false;
					while(rs.next()) {
						flag = true;
					}
					rs.close();
					pst.close();
					
					if (flag) {
						pst = con.prepareStatement("update salary_details set is_delete = true WHERE salary_id = ?");
						pst.setInt(1, uF.parseToInt(strSalaryId));
						pst.execute();
						pst.close();
					} else {
//						pst = con.prepareStatement(deleteSalaryDetails);
//						pst.setInt(1, uF.parseToInt(strSalaryId));
//						pst.execute();
//						pst.close();
						pst = con.prepareStatement("update salary_details set is_delete = true WHERE salary_id = ?");
						pst.setInt(1, uF.parseToInt(strSalaryId));
						pst.execute();
						pst.close();
						
						pst = con.prepareStatement("delete from emp_salary_details where salary_head_id=? and emp_id in (select emp_id from employee_personal_details epd," +
								" employee_official_details eod where epd.emp_per_id=eod.emp_id and eod.org_id=? and eod.grade_id in (select gd.grade_id from grades_details gd, " +
								"designation_details dd where gd.designation_id = dd.designation_id and gd.grade_id=?))");
						pst.setInt(1,nSalaryHeadId);
						pst.setInt(2,nOrgId);
						pst.setInt(3,nGradeId);
						pst.execute();
						pst.close();
					}
				}
			}
			
			if(uF.parseToInt(strSalId) > 0) {
				boolean checkFlag = false;
				pst = con.prepareStatement("SELECT * from salary_details where salary_head_id=? and grade_id = ? and (is_delete is null or is_delete=false) ");
				pst.setInt(1, uF.parseToInt(strSalId));
				pst.setInt(2, uF.parseToInt(getStrGrade()));
				rs = pst.executeQuery();
				while(rs.next()) {
					checkFlag = true;
				}
				rs.close();
				pst.close();
				
				if(!checkFlag) {
					int x =0;
					int nSalId = 0;
					
					int salWeight = 0;
					/*if(uF.parseToInt(strSalId) == BASIC) {
						salWeight = 1;
					}*/	
					pst = con.prepareStatement("SELECT max(weight) weight FROM salary_details WHERE salary_head_id=? and (is_delete is null or is_delete=false)");
					pst.setInt(1, uF.parseToInt(strSalId));
					rs = pst.executeQuery();
//					System.out.println("pst ===>> " + pst);
					while(rs.next()) {
						salWeight = rs.getInt("weight");
					}
					rs.close();
					pst.close();
					
					if(salWeight == 0) {
						pst = con.prepareStatement("SELECT max(weight) weight FROM salary_details WHERE grade_id=? and (is_delete is null or is_delete=false)");
						pst.setInt(1, uF.parseToInt(getStrGrade()));
						rs = pst.executeQuery();
	//					System.out.println("pst ===>> " + pst);
						while(rs.next()) {
							salWeight = rs.getInt("weight");
						}
						rs.close();
						pst.close();
						salWeight +=1;
					}
					
					pst = con.prepareStatement("select * from salary_details where salary_head_id=? and (is_delete is null or is_delete=false) limit 1");
					pst.setInt(1, uF.parseToInt(strSalId));
					rs = pst.executeQuery();
					String strSalaryHeadName = null;
					if(rs.next()) {
						strSalaryHeadName = rs.getString("salary_head_name");
					}
					rs.close();
					pst.close();
					
					double dblStrAmt = uF.parseToDouble((String) request.getParameter("strAmt"));
					
					pst=con.prepareStatement("INSERT INTO salary_details (salary_head_name, earning_deduction,salary_head_amount_type, " +
						"sub_salary_head_id, salary_head_amount, grade_id, salary_head_id, weight, org_id,is_variable, salary_type,is_ctc_variable," +
						"is_tax_exemption,added_by,added_date,is_align_with_perk,is_annual_variable,is_default_cal_allowance,is_contribution) " +
						"VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?)");
//					pst.setString(1, hmSalaryMap.get(strSalId));
					pst.setString(1, strSalaryHeadName);
					pst.setString(2, "E");
					pst.setString(3, "A");
					pst.setInt(4, 0);
					pst.setDouble(5, dblStrAmt);
					pst.setInt(6, uF.parseToInt(getStrGrade()));
					pst.setInt(7, uF.parseToInt(strSalId));
					pst.setInt(8, salWeight);
					pst.setInt(9, uF.parseToInt(getStrOrg()));
					pst.setBoolean(10, false);
					pst.setString(11, "M");
					pst.setBoolean(12, false);
					pst.setBoolean(13, false);
					pst.setInt(14, uF.parseToInt((String) session.getAttribute(EMPID)));
					pst.setDate(15, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setBoolean(16, uF.parseToBoolean(getIsAlignPerk()));
					pst.setBoolean(17, uF.parseToBoolean(getIsAnnualVariable()));
					pst.setBoolean(18, uF.parseToBoolean(getIsDefaultCalculateAllowance()));
					pst.setBoolean(19, uF.parseToBoolean(getIsContribution()));
	//				System.out.println("pst======>"+pst);
					x = pst.executeUpdate();
					pst.close();
					
					nSalId = uF.parseToInt(strSalId);
					
					if(x > 0) {
						String strDomain = request.getServerName().split("\\.")[0];
						EmpSalaryUpdateCron updateCron = new EmpSalaryUpdateCron();
						updateCron.request = request;
						updateCron.session = session;
						updateCron.CF = CF;
						updateCron.strDomain = strDomain;
						updateCron.setStrGradeId(getStrGrade());
						updateCron.setStrSalaryHeadId(""+nSalId);
						updateCron.setStrType("addSalaryBasis");
						updateCron.setDblAmt(dblStrAmt);
						updateCron.setAutoUpdate(getAutoUpdate());
						updateCron.setEmpSalaryUpdateCronData();
						
					}					
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void addStatutoryComplianceSalaryHeadByGrade(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			String strSalId = (String) request.getParameter("SALID");
			
//			Map<String, String> hmSalaryMap = CF.getSalaryHeadsMap(con);
			
			boolean checkFlag = false;
			pst = con.prepareStatement("SELECT * from salary_details where salary_head_id=? and grade_id = ? and (is_delete is null or is_delete=false)");
			pst.setInt(1, uF.parseToInt(strSalId));
			pst.setInt(2, uF.parseToInt(getStrGrade()));
			rs = pst.executeQuery();
			while(rs.next()) {
				checkFlag = true;
			}
			rs.close();
			pst.close();
			
			if(!checkFlag) {
				int x =0;
				int nSalId = 0;
				
				int nWeight = 0;
				pst = con.prepareStatement("SELECT max(weight) weight FROM salary_details WHERE salary_head_id=? and (is_delete is null or is_delete=false)");
				pst.setInt(1, uF.parseToInt(strSalId));
				rs = pst.executeQuery();
//				System.out.println("pst ===>> " + pst);
				while(rs.next()) {
					nWeight = rs.getInt("weight");
				}
				rs.close();
				pst.close();
				
				if(nWeight == 0) {
					pst = con.prepareStatement("SELECT max(weight) weight FROM salary_details WHERE grade_id = ? and (is_delete is null or is_delete=false)");
					pst.setInt(1, uF.parseToInt(getStrGrade()));
					rs = pst.executeQuery();
	//				System.out.println("pst ===>> " + pst);
					while(rs.next()) {
						nWeight = rs.getInt("weight");
					}
					rs.close();
					pst.close();
					nWeight +=1;
				}
				
				pst = con.prepareStatement("select * from salary_details where salary_head_id=? and (is_delete is null or is_delete=false) limit 1");
				pst.setInt(1, uF.parseToInt(strSalId));
				rs = pst.executeQuery();
				String strSalaryHeadName = null;
				if(rs.next()) {
					strSalaryHeadName = rs.getString("salary_head_name");
				}
				rs.close();
				pst.close();
				
				pst=con.prepareStatement("INSERT INTO salary_details (salary_head_name, earning_deduction,salary_head_amount_type, " +
						"sub_salary_head_id, salary_head_amount, grade_id, salary_head_id, weight, org_id,is_variable, salary_type,is_ctc_variable," +
						"is_tax_exemption,added_by,added_date,is_align_with_perk,is_annual_variable,is_default_cal_allowance,is_contribution) " +
						"VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?)");
//				pst.setString(1, hmSalaryMap.get(strSalId));
				pst.setString(1, strSalaryHeadName);
				pst.setString(2, "D");
				pst.setString(3, "A");
				pst.setInt(4, 0);
				pst.setDouble(5, 0.0d);
				pst.setInt(6, uF.parseToInt(getStrGrade()));
				pst.setInt(7, uF.parseToInt(strSalId));
				pst.setInt(8, nWeight);
				pst.setInt(9, uF.parseToInt(getStrOrg()));
				pst.setBoolean(10, false);
				pst.setString(11, "M");
				pst.setBoolean(12, false);
				pst.setBoolean(13, false);
				pst.setInt(14, uF.parseToInt((String) session.getAttribute(EMPID)));
				pst.setDate(15, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setBoolean(16, uF.parseToBoolean(getIsAlignPerk()));
				pst.setBoolean(17, uF.parseToBoolean(getIsAnnualVariable()));
				pst.setBoolean(18, uF.parseToBoolean(getIsDefaultCalculateAllowance()));
				pst.setBoolean(19, uF.parseToBoolean(getIsContribution()));
//				System.out.println("pst======>"+pst);
				x = pst.executeUpdate();
				pst.close();
				
				nSalId = uF.parseToInt(strSalId);
				
				if(x > 0) {
					String strDomain = request.getServerName().split("\\.")[0];
					EmpSalaryUpdateCron updateCron = new EmpSalaryUpdateCron();
					updateCron.request = request;
					updateCron.session = session;
					updateCron.CF = CF;
					updateCron.strDomain = strDomain;
					updateCron.setStrGradeId(getStrGrade());
					updateCron.setStrSalaryHeadId(""+nSalId);
					updateCron.setStrType("addStatutoryHead");
					updateCron.setAutoUpdate(getAutoUpdate());
					updateCron.setEmpSalaryUpdateCronData();
				}
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void getSalaryHeadListByGrade(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			
			con = db.makeConnection(con);
			StringBuilder sb=new StringBuilder();
			pst = con.prepareStatement("SELECT salary_head_id FROM salary_details where grade_id=-1");
			rs = pst.executeQuery();
			int i=0;
			List<String> sHeadList=new ArrayList<String>();
			while (rs.next()) {
				if(i==0) {
					sb.append(rs.getString("salary_head_id"));
				}else{
					sb.append(","+rs.getString("salary_head_id"));
				}
				i++;
				sHeadList.add(rs.getString("salary_head_id"));
			}
			rs.close();
			pst.close();
			request.setAttribute("checkSalaryHead", sb.toString());
			request.setAttribute("sHeadList", sHeadList);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void removeSalaryHeadByGrade(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			 
			pst = con.prepareStatement("select * FROM salary_details WHERE salary_id = ?");
			pst.setInt(1, uF.parseToInt((getRemoveId().split("_"))[1]));
			rs = pst.executeQuery();
			int nGradeId = 0;
			int nOrgId = 0;
			int nSalaryHeadId = 0;
			while(rs.next()) {
				nGradeId = uF.parseToInt(rs.getString("grade_id"));
				nOrgId = uF.parseToInt(rs.getString("org_id"));
				nSalaryHeadId = uF.parseToInt(rs.getString("salary_head_id"));
			}
			rs.close();
			pst.close();
			
						
			pst = con.prepareStatement("select * from payroll_generation where salary_head_id=? and emp_id in (select emp_id from employee_personal_details epd," +
					" employee_official_details eod where epd.emp_per_id=eod.emp_id and eod.org_id=? and eod.grade_id in (select gd.grade_id from grades_details gd, " +
					"designation_details dd where gd.designation_id = dd.designation_id and gd.grade_id=?))");
			pst.setInt(1,nSalaryHeadId);
			pst.setInt(2,nOrgId);
			pst.setInt(3,nGradeId);
			rs = pst.executeQuery();
			boolean flag = false;
			while(rs.next()) {
				flag = true;
			}
			rs.close();
			pst.close();
			
			if (flag) {
				pst = con.prepareStatement("update salary_details set is_delete = true WHERE salary_id = ?");
				pst.setInt(1, uF.parseToInt((getRemoveId().split("_"))[1]));
				pst.execute();
				pst.close();
			} else {
//				pst = con.prepareStatement(deleteSalaryDetails);
//				pst.setInt(1, uF.parseToInt((getRemoveId().split("_"))[1]));
//				pst.execute();
//				pst.close();
				pst = con.prepareStatement("update salary_details set is_delete = true WHERE salary_id = ?");
				pst.setInt(1, uF.parseToInt((getRemoveId().split("_"))[1]));
				pst.execute();
				pst.close();
				
				pst = con.prepareStatement("delete from emp_salary_details where salary_head_id=? and emp_id in (select emp_id from employee_personal_details epd," +
						" employee_official_details eod where epd.emp_per_id=eod.emp_id and eod.org_id=? and eod.grade_id in (select gd.grade_id from grades_details gd, " +
						"designation_details dd where gd.designation_id = dd.designation_id and gd.grade_id=?))");
				pst.setInt(1,nSalaryHeadId);
				pst.setInt(2,nOrgId);
				pst.setInt(3,nGradeId);
				pst.execute();
				pst.close();
			}
			setHeadName(null);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}

	private void addCTCBasicSalaryBasisHead(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			String strSalId = (String) request.getParameter("SALID");
			
//			Map<String, String> hmSalaryMap = CF.getSalaryHeadsMap(con);
			
			pst = con.prepareStatement("SELECT * from salary_details where level_id=? and salary_band_id=?");
			pst.setInt(1, uF.parseToInt(getLevel()));
			pst.setInt(2, uF.parseToInt(getSalaryBand()));
			rs = pst.executeQuery();
			List<String> al = new ArrayList<String>();
			while(rs.next()) {
				al.add(rs.getString("salary_id"));
			}
			
			if(al.size() > 0) {
				for(int i = 0; i < al.size(); i++) {
					String strSalaryId = al.get(i);
					pst = con.prepareStatement("select * FROM salary_details WHERE salary_id=? and (is_delete is null or is_delete=false)");
					pst.setInt(1, uF.parseToInt(strSalaryId));
					rs = pst.executeQuery();
					int nLevelId = 0;
					int nOrgId = 0;
					int nSalaryHeadId = 0;
					int nSalaryBandId = 0;
					while(rs.next()) {
						nLevelId = uF.parseToInt(rs.getString("level_id"));
						nOrgId = uF.parseToInt(rs.getString("org_id"));
						nSalaryHeadId = uF.parseToInt(rs.getString("salary_head_id"));
						nSalaryBandId = uF.parseToInt(rs.getString("salary_band_id"));
					}
					rs.close();
					pst.close();
					
					pst = con.prepareStatement("select * from payroll_generation where salary_head_id=? and emp_id in (select emp_id from employee_personal_details epd," +
							" employee_official_details eod where epd.emp_per_id=eod.emp_id and eod.org_id=? and eod.grade_id in (select gd.grade_id from grades_details gd, " +
							"designation_details dd where gd.designation_id = dd.designation_id and dd.level_id in (select ld.level_id from level_details ld where ld.level_id=?)))");
					pst.setInt(1,nSalaryHeadId);
					pst.setInt(2,nOrgId);
					pst.setInt(3,nLevelId);
					rs = pst.executeQuery();
					boolean flag = false;
					while(rs.next()) {
						flag = true;
					}
					rs.close();
					pst.close();
					
					if (flag) {
						pst = con.prepareStatement("update salary_details set is_delete=true WHERE salary_id=?");
						pst.setInt(1, uF.parseToInt(strSalaryId));
						pst.execute();
						pst.close();
					} else {
//						pst = con.prepareStatement(deleteSalaryDetails);
//						pst.setInt(1, uF.parseToInt(strSalaryId));
//						pst.execute();
//						pst.close();
						
						pst = con.prepareStatement("update salary_details set is_delete=true WHERE salary_id=?");
						pst.setInt(1, uF.parseToInt(strSalaryId));
						pst.execute();
						pst.close();
						
						pst = con.prepareStatement("delete from emp_salary_details where salary_head_id=? and emp_id in (select emp_id from employee_personal_details epd," +
								" employee_official_details eod where epd.emp_per_id=eod.emp_id and eod.org_id=? and eod.grade_id in (select gd.grade_id from grades_details gd, " +
								"designation_details dd where gd.designation_id = dd.designation_id and dd.level_id in (select ld.level_id from level_details ld where ld.level_id=?)))");
						pst.setInt(1,nSalaryHeadId);
						pst.setInt(2,nOrgId);
						pst.setInt(3,nLevelId);
						pst.execute();
						pst.close();
					}
				}
			}
			
			if(uF.parseToInt(strSalId) > 0) {
				boolean checkFlag = false;
				pst = con.prepareStatement("SELECT * from salary_details where salary_head_id=? and level_id=? and salary_band_id=? and (is_delete is null or is_delete=false) ");
				pst.setInt(1, uF.parseToInt(strSalId));
				pst.setInt(2, uF.parseToInt(getLevel()));
				pst.setInt(3, uF.parseToInt(getSalaryBand()));
				rs = pst.executeQuery();
				while(rs.next()) {
					checkFlag = true;
				}
				rs.close();
				pst.close();
				
				if(!checkFlag) {
					int x =0;
					int nSalId = 0;
					
					int salWeight = 0;
					/*if(uF.parseToInt(strSalId) == BASIC) {
						salWeight = 1;
					}*/
					
					pst = con.prepareStatement("SELECT max(weight) weight FROM salary_details WHERE salary_head_id=? and (is_delete is null or is_delete=false)");
					pst.setInt(1, uF.parseToInt(strSalId));
					rs = pst.executeQuery();
//					System.out.println("pst ===>> " + pst);
					while(rs.next()) {
						salWeight = rs.getInt("weight");
					}
					rs.close();
					pst.close();
					
					if(salWeight == 0 && uF.parseToInt(strSalId) != CTC) {
						pst = con.prepareStatement("SELECT max(weight) weight FROM salary_details WHERE level_id=? and (is_delete is null or is_delete=false)");
						pst.setInt(1, uF.parseToInt(getLevel()));
						rs = pst.executeQuery();
	//					System.out.println("pst ===>> " + pst);
						while(rs.next()) {
							salWeight = rs.getInt("weight");
						}
						rs.close();
						pst.close();
						salWeight +=1;
					}
					
					pst = con.prepareStatement("select * from salary_details where salary_head_id=? and (is_delete is null or is_delete=false) limit 1");
					pst.setInt(1, uF.parseToInt(strSalId));
					rs = pst.executeQuery();
					String strSalaryHeadName = null;
					if(rs.next()) {
						strSalaryHeadName = rs.getString("salary_head_name");
					}
					rs.close();
					pst.close();					
					
					double dblStrAmt = uF.parseToDouble((String) request.getParameter("strAmt"));
					
					pst=con.prepareStatement("INSERT INTO salary_details (salary_head_name, earning_deduction,salary_head_amount_type, sub_salary_head_id, " +
						"salary_head_amount, level_id, salary_head_id, weight, org_id,is_variable, salary_type,is_ctc_variable, is_tax_exemption,added_by," +
						"added_date,is_align_with_perk,is_annual_variable,is_default_cal_allowance,is_contribution,salary_band_id) " +
						"VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?)");
//					pst.setString(1, hmSalaryMap.get(strSalId));
					pst.setString(1, strSalaryHeadName);
					pst.setString(2, "E");
					pst.setString(3, "A");
					pst.setInt(4, 0);
					pst.setDouble(5, dblStrAmt);
					pst.setInt(6, uF.parseToInt(getLevel()));
					pst.setInt(7, uF.parseToInt(strSalId));
					pst.setInt(8, salWeight);
					pst.setInt(9, uF.parseToInt(getStrOrg()));
					pst.setBoolean(10, false);
					pst.setString(11, "M");
					pst.setBoolean(12, false);
					pst.setBoolean(13, false);
					pst.setInt(14, uF.parseToInt((String) session.getAttribute(EMPID)));
					pst.setDate(15, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setBoolean(16, uF.parseToBoolean(getIsAlignPerk()));
					pst.setBoolean(17, uF.parseToBoolean(getIsAnnualVariable()));
					pst.setBoolean(18, uF.parseToBoolean(getIsDefaultCalculateAllowance()));
					pst.setBoolean(19, uF.parseToBoolean(getIsContribution()));
					pst.setInt(20, uF.parseToInt(getSalaryBand()));
	//				System.out.println("pst======>"+pst);
					x = pst.executeUpdate();
					pst.close();
					
					nSalId = uF.parseToInt(strSalId);
					
					if(x > 0) {
						String strDomain = request.getServerName().split("\\.")[0];
						EmpSalaryUpdateCron updateCron = new EmpSalaryUpdateCron();
						updateCron.request = request;
						updateCron.session = session;
						updateCron.CF = CF;
						updateCron.strDomain = strDomain;
						updateCron.setStrLevelId(getLevel());
						updateCron.setStrSalaryHeadId(""+nSalId);
						updateCron.setStrType("addSalaryBasis");
						updateCron.setDblAmt(dblStrAmt);
						updateCron.setAutoUpdate(getAutoUpdate());
						updateCron.setEmpSalaryUpdateCronData();
					}
					
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void clearText(UtilityFunctions uF) {
		setHeadName(null);
		setHeadNameOther(null);
		setIsCTCVariable(null);
		setIsVariable(null);
		setIsAnnualVariable(null);
		setIsAlignPerk(null);
		setIsTaxExemption(null);
		setIsContribution(null);
		setHeadAmount(null);
	}

	private void addStatutoryComplianceSalaryHead(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			String strSalId = (String) request.getParameter("SALID");
//			Map<String, String> hmSalaryMap = CF.getSalaryHeadsMap(con);
			
			boolean checkFlag = false;
			pst = con.prepareStatement("SELECT * from salary_details where salary_head_id=? and level_id=? and salary_band_id=? and (is_delete is null or is_delete=false)");
			pst.setInt(1, uF.parseToInt(strSalId));
			pst.setInt(2, uF.parseToInt(getLevel()));
			pst.setInt(3, uF.parseToInt(getSalaryBand()));
			rs = pst.executeQuery();
			while(rs.next()) {
				checkFlag = true;
			}
			rs.close();
			pst.close();
			
			if(!checkFlag) {
				int x =0;
				int nSalId = 0;
				
				int nWeight = 0;
				pst = con.prepareStatement("SELECT max(weight) weight FROM salary_details WHERE salary_head_id=? and (is_delete is null or is_delete=false)");
				pst.setInt(1, uF.parseToInt(strSalId));
				rs = pst.executeQuery();
//				System.out.println("pst ===>> " + pst);
				while(rs.next()) {
					nWeight = rs.getInt("weight");
				}
				rs.close();
				pst.close();
				
				if(nWeight==0) {
					pst = con.prepareStatement("SELECT max(weight) weight FROM salary_details WHERE level_id=? and (is_delete is null or is_delete=false)");
					pst.setInt(1, uF.parseToInt(getLevel()));
					rs = pst.executeQuery();
	//				System.out.println("pst ===>> " + pst);
					while(rs.next()) {
						nWeight = rs.getInt("weight");
					}
					rs.close();
					pst.close();
					nWeight +=1;
				}
				
				pst = con.prepareStatement("select * from salary_details where salary_head_id=? and (is_delete is null or is_delete=false) limit 1");
				pst.setInt(1, uF.parseToInt(strSalId));
				rs = pst.executeQuery();
				String strSalaryHeadName = null;
				if(rs.next()) {
					strSalaryHeadName = rs.getString("salary_head_name");
				}
				rs.close();
				pst.close();
				
				pst=con.prepareStatement("INSERT INTO salary_details (salary_head_name, earning_deduction,salary_head_amount_type,sub_salary_head_id, " +
					"salary_head_amount, level_id, salary_head_id, weight, org_id,is_variable, salary_type,is_ctc_variable,is_tax_exemption,added_by," +
					"added_date,is_align_with_perk,is_annual_variable,is_default_cal_allowance,is_contribution,salary_band_id) " +
					" VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?)");
//				pst.setString(1, hmSalaryMap.get(strSalId));
				pst.setString(1, strSalaryHeadName);
				pst.setString(2, "D");
				pst.setString(3, "A");
				pst.setInt(4, 0);
				pst.setDouble(5, 0.0d);
				pst.setInt(6, uF.parseToInt(getLevel()));
				pst.setInt(7, uF.parseToInt(strSalId));
				pst.setInt(8, nWeight);
				pst.setInt(9, uF.parseToInt(getStrOrg()));
				pst.setBoolean(10, false);
				pst.setString(11, "M");
				pst.setBoolean(12, false);
				pst.setBoolean(13, false);
				pst.setInt(14, uF.parseToInt((String) session.getAttribute(EMPID)));
				pst.setDate(15, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setBoolean(16, uF.parseToBoolean(getIsAlignPerk()));
				pst.setBoolean(17, uF.parseToBoolean(getIsAnnualVariable()));
				pst.setBoolean(18, uF.parseToBoolean(getIsDefaultCalculateAllowance()));
				pst.setBoolean(19, uF.parseToBoolean(getIsContribution()));
				pst.setInt(20, uF.parseToInt(getSalaryBand()));
				System.out.println("pst======>"+pst);
				x = pst.executeUpdate();
				pst.close();
				
				nSalId = uF.parseToInt(strSalId);
				
				if(x > 0) {
					String strDomain = request.getServerName().split("\\.")[0];
					EmpSalaryUpdateCron updateCron = new EmpSalaryUpdateCron();
					updateCron.request = request;
					updateCron.session = session;
					updateCron.CF = CF;
					updateCron.strDomain = strDomain;
					updateCron.setStrLevelId(getLevel());
					updateCron.setStrSalaryHeadId(""+nSalId);
					updateCron.setStrType("addStatutoryHead");
					updateCron.setAutoUpdate(getAutoUpdate());
					updateCron.setEmpSalaryUpdateCronData();
				}
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void getSalaryHeadList(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			
			con = db.makeConnection(con);
			StringBuilder sb=new StringBuilder();
			pst = con.prepareStatement("SELECT salary_head_id FROM salary_details where level_id=-1");
			rs = pst.executeQuery();
			int i=0;
			List<String> sHeadList=new ArrayList<String>();
			while (rs.next()) {
				if(i==0) {
					sb.append(rs.getString("salary_head_id"));
				}else{
					sb.append(","+rs.getString("salary_head_id"));
				}
				i++;
				sHeadList.add(rs.getString("salary_head_id"));
			}
			rs.close();
			pst.close();
			request.setAttribute("checkSalaryHead", sb.toString());
			request.setAttribute("sHeadList", sHeadList);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void editSalaryHead(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			
			con = db.makeConnection(con);
			
//			String multipleCal = (getHeadAmountType()!=null && getHeadAmountType().trim().equals("M")) ? uF.parseToInt(getSalaryHead1())+","+getSign1()+","+uF.parseToInt(getSalaryHead2())+","+getSign2()+","+uF.parseToInt(getSalaryHead3())+","+getSign3()+","+uF.parseToInt(getSalaryHead4())+","+getSign4()+","+uF.parseToInt(getSalaryHead5()) : null;
			String multipleCal = getMulitpleCalFormula(uF); 
//			System.out.println("mutlipeCal=====>"+multipleCal+"--getStrMulPerCalFormula()==>"+getStrMulPerCalFormula());
			double dblAmount = 0.0d;
			if(getHeadAmountType()!=null && getHeadAmountType().trim().equals("P")) { 
				String strFormula = CF.getLevelStructureFormula(con, uF, getLevel(), getSalaryBand(), getStrMulPerCalFormula());
//				System.out.println("strFormula==>"+strFormula);
				if(uF.parseToDouble(getHeadAmount()) > 0.0d && strFormula != null && strFormula.length() > 0) {
					double dblPerAmount = uF.eval(strFormula);	
//					System.out.println("uF.parseToDouble(getHeadAmount())==>"+uF.parseToDouble(getHeadAmount())+"strFormula==>"+strFormula+"--dblPerAmount==>"+dblPerAmount);
					dblAmount = (uF.parseToDouble(getHeadAmount()) * dblPerAmount)/100;
				}
			}
			
			pst = con.prepareStatement("select * from salary_details where salary_head_id=? and salary_id=? and is_allowance=true");
			pst.setInt(1, uF.parseToInt(getSalaryHeadId()));
			pst.setInt(2, uF.parseToInt(getSalaryId()));
			rs = pst.executeQuery();
			boolean isAllowance = false; 
			if(rs.next()) {
				isAllowance = true;
			}
			rs.close();
			pst.close();
			
			double dblHeadAmount = uF.parseToDouble(getHeadAmount());
			if(isAllowance) {
				if(uF.parseToBoolean(getIsDefaultCalculateAllowance())) {
					dblHeadAmount = uF.parseToDouble(getHeadAmount());
				} else {
					dblHeadAmount = 0.0d;
				}
			}
			
//			pst = con.prepareStatement(updateSalaryDetails);
			pst = con.prepareStatement("UPDATE salary_details set salary_head_name=?, earning_deduction=?, salary_head_amount_type=?, " +
					"sub_salary_head_id=?, salary_head_amount=?,is_variable=?, salary_type=? ,is_ctc_variable=?,added_by=?,added_date=?," +
					"is_tax_exemption=?, multiple_calculation=?, is_align_with_perk=?,is_annual_variable=?,salary_calculate_amount=?," +
					"is_default_cal_allowance=?,is_contribution=?,max_cap_amount=? where salary_head_id=? and salary_id=?");
			pst.setString(1, getHeadName());
			pst.setString(2, getEarningOrDeduction());
			pst.setString(3, uF.parseToInt(getSalaryHeadId()) == CTC ? "A" : getHeadAmountType());
			pst.setInt(4, (getHeadAmountType()!=null && getHeadAmountType().trim().equals("P")) ? uF.parseToInt(getSalarySubHead()) : 0);
			pst.setDouble(5, dblHeadAmount);
			pst.setBoolean(6, uF.parseToBoolean(getIsVariable()));
			pst.setString(7, getSalary_type());
			pst.setBoolean(8, uF.parseToBoolean(getIsCTCVariable()));
			pst.setInt(9, uF.parseToInt((String) session.getAttribute(EMPID)));
			pst.setDate(10, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setBoolean(11, uF.parseToBoolean(getIsCTCVariable()) ? uF.parseToBoolean(getIsTaxExemption()) : false);
			pst.setString(12, multipleCal);
			pst.setBoolean(13, uF.parseToBoolean(getIsAlignPerk()));
			pst.setBoolean(14, uF.parseToBoolean(getIsAnnualVariable()));
			pst.setDouble(15, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblAmount)));
			pst.setBoolean(16, uF.parseToBoolean(getIsDefaultCalculateAllowance()));
			pst.setBoolean(17, uF.parseToBoolean(getIsContribution()));
			pst.setDouble(18, uF.parseToDouble(getHeadMaxCapAmount()));
			pst.setInt(19, uF.parseToInt(getSalaryHeadId()));
			pst.setInt(20, uF.parseToInt(getSalaryId()));
//			System.out.println("1 pst==>"+pst);
			int x = pst.executeUpdate();
			pst.close();
			
			if(x == 0) {
				addNewSalaryHead(uF.parseToInt(getSalaryHeadId()));
			} else if(x > 0) {
				updateRelativeHeadAmount(con, uF, uF.parseToInt(getLevel()), uF.parseToInt(getSalaryBand()), uF.parseToInt(getSalaryHeadId()));
				
				if(getHeadAmountType()!=null && getHeadAmountType().trim().equalsIgnoreCase("P")) { 
					String strDomain = request.getServerName().split("\\.")[0];
					EmpSalaryUpdateCron updateCron = new EmpSalaryUpdateCron();
					updateCron.request = request;
					updateCron.session = session;
					updateCron.CF = CF;
					updateCron.strDomain = strDomain;
					updateCron.setStrLevelId(getLevel());
					updateCron.setStrSalaryHeadId(getSalaryHeadId());
					updateCron.setStrType("edit");
					updateCron.setAutoUpdate(getAutoUpdate());
					updateCron.setEmpSalaryUpdateCronData();
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}

	private void updateRelativeHeadAmount(Connection con, UtilityFunctions uF, int nLevelId, int nSalaryBandId, int nSalaryHeadId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			if(nSalaryHeadId > 0) {
				pst = con.prepareStatement("SELECT * FROM salary_details WHERE level_id = ? and salary_band_id=? and multiple_calculation like '%,"+nSalaryHeadId+",%' " +
						"and (is_delete is null or is_delete=false) order by earning_deduction desc, weight");
				pst.setInt(1, nLevelId);
				pst.setInt(2, nSalaryBandId);
//				System.out.println("pst==>"+pst);
				rs = pst.executeQuery();
				List<Map<String, String>> alSal = new ArrayList<Map<String,String>>();
				while(rs.next()) {
					Map<String, String> hmSal = new HashMap<String, String>();
					
					hmSal.put("MULTIPLE_CALCULATION", rs.getString("multiple_calculation"));
					hmSal.put("SALARY_HEAD_AMOUNT_TYPE", rs.getString("salary_head_amount_type"));
					hmSal.put("SALARY_HEAD_ID", rs.getString("salary_head_id"));
					hmSal.put("SALARY_ID", rs.getString("salary_id"));
					hmSal.put("SALARY_HEAD_AMOUNT", rs.getString("salary_head_amount"));
					
					alSal.add(hmSal);
				}
				rs.close();
				pst.close();
				
				for(Map<String, String> hmSal : alSal) {
					
					String strMulPerCalFormula = hmSal.get("MULTIPLE_CALCULATION");
					String strSalaryHeadAmtType = hmSal.get("SALARY_HEAD_AMOUNT_TYPE");
					int nCalSalHeadId = uF.parseToInt(hmSal.get("SALARY_HEAD_ID"));
					int nCalSalId = uF.parseToInt(hmSal.get("SALARY_ID"));
					String strPercentage = hmSal.get("SALARY_HEAD_AMOUNT");					
					
					if(nCalSalId > 0 && nCalSalHeadId > 0 && strSalaryHeadAmtType!=null && strSalaryHeadAmtType.trim().equalsIgnoreCase("P")) {
						double dblAmount = 0.0d;
						String strFormula = CF.getLevelStructureFormula(con, uF, ""+nLevelId, ""+nSalaryBandId, strMulPerCalFormula);
						if(uF.parseToDouble(strPercentage) > 0.0d && strFormula != null && strFormula.length() > 0) {
							double dblPerAmount = uF.eval(strFormula);	
							dblAmount = (uF.parseToDouble(strPercentage) * dblPerAmount)/100;
//							System.out.println("nCalSalHeadId==>"+nCalSalHeadId+"--strPercentage==>"+uF.parseToDouble(strPercentage)+"strFormula==>"+strFormula+"--dblPerAmount==>"+dblPerAmount+"--dblAmount==>"+dblAmount);
						}
						
						pst = con.prepareStatement("update salary_details set salary_calculate_amount=? WHERE level_id= ? and salary_head_id=? and salary_id=?");
						pst.setDouble(1, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblAmount)));
						pst.setInt(2, nLevelId);
						pst.setInt(3, nCalSalHeadId);
						pst.setInt(4, nCalSalId);
//						System.out.println("pst==>"+pst);
						int x = pst.executeUpdate();
						if(x > 0) {
//							System.out.println("nCalSalHeadId==>"+nCalSalHeadId+"--pst==>"+pst);
							updateRelativeHeadAmount(con, uF, nLevelId, nSalaryBandId, nCalSalHeadId);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private String getMulitpleCalFormula(UtilityFunctions uF) {
		StringBuilder sbMultipleFormula = null;
		try{
//			System.out.println("getStrMulPerCalFormula()==>"+getStrMulPerCalFormula());
			/*if(getHeadAmountType()!=null && getHeadAmountType().trim().equals("M")) {
				sbMultipleFormula = new StringBuilder();
				sbMultipleFormula.append(uF.parseToInt(getSalaryHead1())+","+getSign1());
				sbMultipleFormula.append(","+uF.parseToInt(getSalaryHead2())+","+getSign2());
				sbMultipleFormula.append(","+uF.parseToInt(getSalaryHead3())+","+getSign3());
				sbMultipleFormula.append(","+uF.parseToInt(getSalaryHead4())+","+getSign4());
				sbMultipleFormula.append(","+uF.parseToInt(getSalaryHead5())+","+getSign5());
				sbMultipleFormula.append(","+uF.parseToInt(getSalaryHead6())+","+getSign6());
				sbMultipleFormula.append(","+uF.parseToInt(getSalaryHead7())+","+getSign7());
				sbMultipleFormula.append(","+uF.parseToInt(getSalaryHead8())+","+getSign8());
				sbMultipleFormula.append(","+uF.parseToInt(getSalaryHead9())+","+getSign9());
				sbMultipleFormula.append(","+uF.parseToInt(getSalaryHead10()));
			} else */if(getHeadAmountType()!=null && getHeadAmountType().trim().equals("P") 
					&& getStrMulPerCalFormula() != null && !getStrMulPerCalFormula().trim().equals("") 
					&& !getStrMulPerCalFormula().trim().equalsIgnoreCase("NULL")) {
				sbMultipleFormula = new StringBuilder();
				sbMultipleFormula.append(getStrMulPerCalFormula());
			} else {
				return null;
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return sbMultipleFormula!=null ? sbMultipleFormula.toString() : null;
	}

	private void removeSalaryHead(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			 
			pst = con.prepareStatement("select * FROM salary_details WHERE salary_id = ?");
			pst.setInt(1, uF.parseToInt((getRemoveId().split("_"))[1]));
			rs = pst.executeQuery();
			int nLevelId = 0;
			int nOrgId = 0;
			int nSalaryHeadId = 0;
			while(rs.next()) {
				nLevelId = uF.parseToInt(rs.getString("level_id"));
				nOrgId = uF.parseToInt(rs.getString("org_id"));
				nSalaryHeadId = uF.parseToInt(rs.getString("salary_head_id"));
			}
			rs.close();
			pst.close();
			
						
			pst = con.prepareStatement("select * from payroll_generation where salary_head_id=? and emp_id in (select emp_id from employee_personal_details epd," +
					" employee_official_details eod where epd.emp_per_id=eod.emp_id and eod.org_id=? and eod.grade_id in (select gd.grade_id from grades_details gd, " +
					"designation_details dd where gd.designation_id = dd.designation_id and dd.level_id in (select ld.level_id from level_details ld where ld.level_id=?)))");
			pst.setInt(1,nSalaryHeadId);
			pst.setInt(2,nOrgId);
			pst.setInt(3,nLevelId);
			rs = pst.executeQuery();
			boolean flag = false;
			while(rs.next()) {
				flag = true;
			}
			rs.close();
			pst.close();
			
			if (flag) {
				pst = con.prepareStatement("update salary_details set is_delete = true WHERE salary_id = ?");
				pst.setInt(1, uF.parseToInt((getRemoveId().split("_"))[1]));
				pst.execute();
				pst.close();
			} else {
//				pst = con.prepareStatement(deleteSalaryDetails);
//				pst.setInt(1, uF.parseToInt((getRemoveId().split("_"))[1]));
//				pst.execute();
//				pst.close();
				pst = con.prepareStatement("update salary_details set is_delete = true WHERE salary_id = ?");
				pst.setInt(1, uF.parseToInt((getRemoveId().split("_"))[1]));
				pst.execute();
				pst.close();
				
				pst = con.prepareStatement("delete from emp_salary_details where salary_head_id=? and emp_id in (select emp_id from employee_personal_details epd," +
						" employee_official_details eod where epd.emp_per_id=eod.emp_id and eod.org_id=? and eod.grade_id in (select gd.grade_id from grades_details gd, " +
						"designation_details dd where gd.designation_id = dd.designation_id and dd.level_id in (select ld.level_id from level_details ld where ld.level_id=?)))");
				pst.setInt(1, nSalaryHeadId);
				pst.setInt(2, nOrgId);
				pst.setInt(3, nLevelId);
				pst.execute();
				pst.close();
			}
			setHeadName(null);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}

	
	private void addNewSalaryHead(int nSalaryHeadId) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			con = db.makeConnection(con);
			
			if(nSalaryHeadId==0) {
				
				pst = con.prepareStatement("SELECT max(salary_head_id) as salary_head_id from salary_details");
				rs = pst.executeQuery();
				while(rs.next()) {
					nSalaryHeadId = rs.getInt("salary_head_id");
				}
				rs.close();
				pst.close();
				
				nSalaryHeadId++;
				
				if(nSalaryHeadId == GROSS) {
					nSalaryHeadId++;
				}
			}
			
//			Map<String, String> hmSalaryMap = CF.getSalaryHeadsMap(con);
			
//			int nWeight = 100;
			int nWeight = 0;
			pst = con.prepareStatement("SELECT max(weight) weight FROM salary_details WHERE salary_head_id=? and (is_delete is null or is_delete=false)");
			pst.setInt(1, uF.parseToInt(getHeadName()));
			rs = pst.executeQuery();
//			System.out.println("pst ===>> " + pst);
			while(rs.next()) {
				nWeight = rs.getInt("weight");
			}
			rs.close();
			pst.close();
			
			if(nWeight == 0) {
				pst = con.prepareStatement("SELECT max(weight) weight FROM salary_details WHERE level_id = ? and (is_delete is null or is_delete=false)");
				pst.setInt(1, uF.parseToInt(getLevel()));
				rs = pst.executeQuery();
	//			System.out.println("pst ===>> " + pst);
				while(rs.next()) {
					nWeight = rs.getInt("weight");
				}
				rs.close();
				pst.close();
				nWeight +=1;
			}
//			System.out.println("nWeight ===>> " +nWeight);
			
			
			boolean checkFlag = false;
			pst = con.prepareStatement("SELECT * from salary_details where salary_head_id=? and level_id=? and salary_band_id=? and (is_delete is null or is_delete=false)");
			pst.setInt(1, uF.parseToInt(getHeadName()));
			pst.setInt(2, uF.parseToInt(getLevel()));
			pst.setInt(3, uF.parseToInt(getSalaryBand()));
			rs = pst.executeQuery();
			while(rs.next()) {
				checkFlag = true;
			}
			rs.close();
			pst.close();
			
			if(!checkFlag) {
				int x =0;
				int nSalId = 0;
//				String multipleCal = (getHeadAmountType()!=null && getHeadAmountType().trim().equals("M")) ? uF.parseToInt(getSalaryHead1())+","+getSign1()+","+uF.parseToInt(getSalaryHead2())+","+getSign2()+","+uF.parseToInt(getSalaryHead3())+","+getSign3()+","+uF.parseToInt(getSalaryHead4())+","+getSign4()+","+uF.parseToInt(getSalaryHead5()) : null;
				String multipleCal = getMulitpleCalFormula(uF); 
				
				double dblAmount = 0.0d;
				if(getHeadAmountType()!=null && getHeadAmountType().trim().equals("P")) { 
					String strFormula = CF.getLevelStructureFormula(con, uF, getLevel(),getSalaryBand(),getStrMulPerCalFormula());
					if(uF.parseToDouble(getHeadAmount()) > 0.0d && strFormula != null && strFormula.length() > 0) {
						double dblPerAmount = uF.eval(strFormula);	
						dblAmount = (uF.parseToDouble(getHeadAmount()) * dblPerAmount)/100;
					}
				}
				
				
				if(uF.parseToInt(getHeadName())>0) {
					
					pst = con.prepareStatement("select * from salary_details where salary_head_id=? and (is_delete is null or is_delete=false) limit 1");
					pst.setInt(1, uF.parseToInt(getHeadName()));
					rs = pst.executeQuery();
					String strSalaryHeadName = null;
					if(rs.next()) {
						strSalaryHeadName = rs.getString("salary_head_name");
					}
					rs.close();
					pst.close();
					
					pst=con.prepareStatement("INSERT INTO salary_details (salary_head_name, earning_deduction,salary_head_amount_type, " +
							"sub_salary_head_id, salary_head_amount, level_id, salary_head_id, weight, org_id,is_variable, salary_type," +
							"is_ctc_variable,is_tax_exemption,added_by,added_date,is_incentive,is_allowance,multiple_calculation,is_align_with_perk," +
							"is_annual_variable,salary_calculate_amount,is_default_cal_allowance,is_contribution,salary_band_id,max_cap_amount) " +
							"VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?)");
//					pst.setString(1, hmSalaryMap.get(getHeadName()));
					pst.setString(1, strSalaryHeadName);
					pst.setString(2, getEarningOrDeduction());
					pst.setString(3, getHeadAmountType());
					pst.setInt(4, (getHeadAmountType()!=null && getHeadAmountType().trim().equals("P")) ? uF.parseToInt(getSalarySubHead()) : 0);
					pst.setDouble(5, uF.parseToDouble(getHeadAmount()));
					pst.setInt(6, uF.parseToInt(getLevel()));
					pst.setInt(7, uF.parseToInt(getHeadName()));
					pst.setInt(8, nWeight);
					pst.setInt(9, uF.parseToInt(getStrOrg()));
					pst.setBoolean(10, uF.parseToBoolean(getIsVariable()));
					pst.setString(11, getSalary_type());
					pst.setBoolean(12, uF.parseToBoolean(getIsCTCVariable()));
					pst.setBoolean(13, uF.parseToBoolean(getIsCTCVariable()) ? uF.parseToBoolean(getIsTaxExemption()) : false);
					pst.setInt(14, uF.parseToInt((String) session.getAttribute(EMPID)));
					pst.setDate(15, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setBoolean(16, uF.parseToBoolean(getStrIsIncentive()));
					pst.setBoolean(17, uF.parseToBoolean(getStrIsAllowance()));
					pst.setString(18, multipleCal);
					pst.setBoolean(19, uF.parseToBoolean(getIsAlignPerk()));
					pst.setBoolean(20, uF.parseToBoolean(getIsAnnualVariable()));
					pst.setDouble(21, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblAmount)));
					pst.setBoolean(22, uF.parseToBoolean(getIsDefaultCalculateAllowance()));
					pst.setBoolean(23, uF.parseToBoolean(getIsContribution()));
					pst.setInt(24, uF.parseToInt(getSalaryBand()));
					pst.setDouble(25, uF.parseToDouble(getHeadMaxCapAmount()));
//					System.out.println("2 pst==>"+pst);
					x = pst.executeUpdate();
					pst.close();
					
					nSalId = uF.parseToInt(getHeadName());
					
				} else {
					//pst = con.prepareStatement(insertSalaryDetails1);
					pst=con.prepareStatement("INSERT INTO salary_details (salary_head_name, earning_deduction,salary_head_amount_type, " +
							"sub_salary_head_id, salary_head_amount, level_id, salary_head_id, weight, org_id,is_variable, salary_type," +
							"is_ctc_variable,is_tax_exemption,added_by,added_date,is_incentive,is_allowance,multiple_calculation,is_align_with_perk," +
							"is_annual_variable,salary_calculate_amount,is_default_cal_allowance,is_contribution,salary_band_id,max_cap_amount) " +
							"VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?)");
					pst.setString(1, getHeadNameOther());
					pst.setString(2, getEarningOrDeduction());
					pst.setString(3, getHeadAmountType());
					pst.setInt(4, (getHeadAmountType()!=null && getHeadAmountType().trim().equals("P")) ? uF.parseToInt(getSalarySubHead()) : 0);
					pst.setDouble(5, uF.parseToDouble(getHeadAmount()));
					pst.setInt(6, uF.parseToInt(getLevel()));
					pst.setInt(7, nSalaryHeadId);
					pst.setInt(8, nWeight);
					pst.setInt(9, uF.parseToInt(getStrOrg()));
					pst.setBoolean(10, uF.parseToBoolean(getIsVariable()));
					pst.setString(11, getSalary_type());
					pst.setBoolean(12, uF.parseToBoolean(getIsCTCVariable()));
					pst.setBoolean(13, uF.parseToBoolean(getIsCTCVariable()) ? uF.parseToBoolean(getIsTaxExemption()) : false);
					pst.setInt(14, uF.parseToInt((String) session.getAttribute(EMPID)));
					pst.setDate(15, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setBoolean(16, uF.parseToBoolean(getStrIsIncentive()));
					pst.setBoolean(17, uF.parseToBoolean(getStrIsAllowance()));
					pst.setString(18, multipleCal);
					pst.setBoolean(19, uF.parseToBoolean(getIsAlignPerk()));
					pst.setBoolean(20, uF.parseToBoolean(getIsAnnualVariable()));
					pst.setDouble(21, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblAmount)));
					pst.setBoolean(22, uF.parseToBoolean(getIsDefaultCalculateAllowance()));
					pst.setBoolean(23, uF.parseToBoolean(getIsContribution()));
					pst.setInt(24, uF.parseToInt(getSalaryBand()));
					pst.setDouble(25, uF.parseToDouble(getHeadMaxCapAmount()));
					x = pst.executeUpdate();
					pst.close();
					nSalId = nSalaryHeadId;
				}
				
				if(x > 0) {
					String strDomain = request.getServerName().split("\\.")[0];
					EmpSalaryUpdateCron updateCron = new EmpSalaryUpdateCron();
					updateCron.request = request;
					updateCron.session = session;
					updateCron.CF = CF;
					updateCron.strDomain = strDomain;
					updateCron.setStrLevelId(getLevel());
					updateCron.setStrSalaryHeadId(""+nSalId);
					updateCron.setStrType("add");
					updateCron.setAutoUpdate(getAutoUpdate());
					updateCron.setEmpSalaryUpdateCronData();
				}
				
			}
			
			setHeadName(null);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}

	public void viewSalaryDetails() {
		
		Connection con = null;
		PreparedStatement pst=null;
		PreparedStatement pst1=null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			String[] strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
			String strFinancialYearStart = strFinancialYearDates[0];
			String strFinancialYearEnd = strFinancialYearDates[1];
			
			
			List<String> sHeadList=(List<String>)request.getAttribute("sHeadList");
			if(sHeadList==null) sHeadList=new ArrayList<String>();
			
			List<String> alInner = new ArrayList<String>();
			
			con = db.makeConnection(con);
			
//			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			
			Map<String, Map<String, String>> hmCurrencyDetails = CF.getCurrencyDetailsForPDF(con);
			String currId = CF.getOrgCurrencyIdByOrg(con, getStrOrg());
			String currency = null;
			if(uF.parseToInt(currId) > 0) {
				Map<String, String> hmCurr = hmCurrencyDetails.get(currId);
				if (hmCurr == null) hmCurr = new HashMap<String, String>();
				currency = hmCurr.get("SHORT_CURR")!=null && !hmCurr.get("SHORT_CURR").trim().equals("") ? hmCurr.get("SHORT_CURR") : "";
				request.setAttribute("currency", currency);
			}
			
			List<String> alStatutoryIds = new ArrayList<String>();
			List<String> alBasisSalIds = new ArrayList<String>();
			Map<String, String> hmStatutoryIds = new HashMap<String, String>();
			
			pst = con.prepareStatement("SELECT * FROM salary_band_details WHERE salary_band_id=?");
			pst.setInt(1, uF.parseToInt(getSalaryBand()));
			rs = pst.executeQuery();
			while(rs.next()) {
				setSalaryBandName(rs.getString("salary_band_name"));
				setSalaryBandMinAmt(rs.getString("band_min_amount"));
				setSalaryBandMaxAmt(rs.getString("band_max_amount"));
			}
			rs.close();
			pst.close();
			
			
			pst = con.prepareStatement("SELECT * FROM salary_details WHERE level_id=? and salary_band_id=? and is_annual_variable=true and (is_delete is null or is_delete=false) order by weight");
			pst.setInt(1, uF.parseToInt(getLevel()));
			pst.setInt(2, uF.parseToInt(getSalaryBand()));
			rs = pst.executeQuery();
			List<String> alAnnualSalaryHead = new ArrayList<String>();
			while(rs.next()) {
				if(!alAnnualSalaryHead.contains(rs.getString("salary_head_id"))) {
					alAnnualSalaryHead.add(rs.getString("salary_head_id"));
				}
			}
			rs.close();
			pst.close();
			
			
			pst = con.prepareStatement("SELECT * FROM salary_details WHERE level_id=? and salary_band_id=? and (is_delete is null or is_delete=false) order by earning_deduction desc, weight");
			pst.setInt(1, uF.parseToInt(getLevel()));
			pst.setInt(2, uF.parseToInt(getSalaryBand()));
//			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			StringBuffer sb = new StringBuffer();
			Map<String, String> hmSalaryMap = CF.getSalaryHeadsMap(con, uF.parseToInt(getLevel())); 
			List alSalaryDuplicationTracer = new ArrayList();
//			Map<String, String> hmTotal = new HashMap<String, String>();
//			double dblDAPercentAmt = 0.0d;
			
			List<String> alAssignToPercentageHead = new ArrayList<String>(); 
			while(rs.next()) {
				
				alInner = new ArrayList<String>();
				alInner.add(rs.getString("salary_head_id"));	//0
				alInner.add(rs.getString("salary_head_name"));	//1
				alInner.add(rs.getString("earning_deduction"));	//2
				alInner.add(rs.getString("salary_head_amount_type"));	//3
				String rsHeadId = rs.getInt("sub_salary_head_id") + "";
				String alHeadId = "";
				double dblPercentAmt = 0.0d;
				
				
				alInner.add("");	//4
				
				StringBuilder sbMulcalType = new StringBuilder();
				if("P".equalsIgnoreCase(rs.getString("salary_head_amount_type"))) {
					String strMulCal = rs.getString("multiple_calculation");
					CF.appendMultiplePercentageCalType(uF,sbMulcalType,strMulCal,hmSalaryMap,alAnnualSalaryHead); 
					
					if(strMulCal != null && !strMulCal.trim().equals("") && !strMulCal.trim().equalsIgnoreCase("NULL")) {
						List<String> al = Arrays.asList(strMulCal.trim().split(","));
						int nAl = al != null ? al.size() : 0;
						for(int i = 0; i < nAl; i++) {
							String str = al.get(i);
							if(str != null && !str.trim().equals("") && !str.trim().equalsIgnoreCase("NULL")) {
								 boolean isInteger = uF.isInteger(str.trim());
								 if(isInteger) {
									if(!alAssignToPercentageHead.contains(str.trim())) {
										alAssignToPercentageHead.add(str.trim());
									}
								 }
							}
						}
					}
					
					double dblMulAmt = uF.parseToDouble((rs.getString("salary_head_amount_type") != null && rs.getString("salary_head_amount_type").equals("P")) ?  uF.formatIntoFourDecimalWithOutComma(uF.parseToDouble(rs.getString("salary_head_amount"))) : uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("salary_head_amount"))));
					dblPercentAmt = dblMulAmt;
//					hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblMulAmt));
				}else{
//					hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("salary_head_amount"))));
				}
				
				alInner.add((rs.getString("salary_head_amount_type") != null && rs.getString("salary_head_amount_type").equals("P")) ?  uF.formatIntoFourDecimalWithOutComma(uF.parseToDouble(rs.getString("salary_head_amount"))) : uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("salary_head_amount"))));	//5
				
				alInner.add(rs.getString("isremove"));	//6
				alInner.add(rs.getString("isedit"));	//7
				
				alInner.add(rs.getString("salary_id"));	//8
				alInner.add(rs.getString("weight"));	//9
				
//				if(uF.parseToInt(rs.getString("salary_head_id")) == DA) {
//					alInner.add(uF.formatIntoTwoDecimalWithOutComma(dblDAPercentAmt));	//10
//				} else {
					alInner.add(uF.formatIntoTwoDecimalWithOutComma(dblPercentAmt));	//10
//				}
				
				alInner.add(""+uF.parseToBoolean(rs.getString("is_incentive")));   // 11
				alInner.add(""+uF.parseToBoolean(rs.getString("is_allowance")));   // 12
				
				alInner.add(sbMulcalType.toString());   // 13
				
				
				boolean isAllowance = uF.parseToBoolean(rs.getString("is_allowance"));
				
				String isvariabledata="";
				if(!isAllowance && !sHeadList.contains(rs.getString("salary_head_id"))) {
					String ischecked="";
					if(uF.parseToBoolean(rs.getString("is_variable"))) {
						ischecked="checked";
					}
					isvariabledata ="<tr id=\"id_isvariable_"+rs.getString("salary_head_id")+"\"><td style=\"color: rgb(255, 255, 255); " +
							"font-style: italic; font-size: 12px;\">Is Variable:</td><td><input type=\"checkbox\" name=\"isVariable\"" +
							" id=\"isVariable\" "+ischecked+"/></td></tr>";
				}
				
				if(!isAllowance && !sHeadList.contains(rs.getString("salary_head_id")) && rs.getString("earning_deduction").equals("E")) {
					String ischecked="";
					if(uF.parseToBoolean(rs.getString("is_annual_variable"))) {
						ischecked="checked";
					}
					isvariabledata +="<tr id=\"id_isAnnualVariable_"+rs.getString("salary_head_id")+"\"><td style=\"color: rgb(255, 255, 255); " +
							"font-style: italic; font-size: 12px;\">Is Annual Variable:</td><td><input type=\"checkbox\" name=\"isAnnualVariable\"" +
							" id=\"isAnnualVariable\" "+ischecked+"/></td></tr>";
				}
				
				
				String isCTCvariabledata="";
				String ischecked="";
				String isTaxExemDisplay="none";
				if(uF.parseToBoolean(rs.getString("is_ctc_variable"))) {
					ischecked="checked";
					isTaxExemDisplay="table-row";
				}
				if(!isAllowance) {
					isCTCvariabledata="<tr><td style=\"color: rgb(255, 255, 255); " +
						"font-style: italic; font-size: 12px;\">Is CTC Variable:</td><td><input type=\"checkbox\" name=\"isCTCVariable\"  id=\"isCTCVariable_"+rs.getString("salary_id")+"\"  onclick=\"showTaxExemption(this.id);\" " +
						ischecked+"/></td></tr>";
				}
				
				String isTaxExemptiondata="";
				String isTaxExemptChecked="";
				
				if(uF.parseToBoolean(rs.getString("is_tax_exemption"))) {
					isTaxExemptChecked="checked";
				}
				if(!isAllowance) {
					isTaxExemptiondata="<tr id=\"id_isTaxExempt_"+rs.getString("salary_id")+"\" style=\"display:"+isTaxExemDisplay+";\"><td style=\"color: rgb(255, 255, 255); " +
						"font-style: italic; font-size: 12px;\">Is Tax Exemption:</td><td><input type=\"checkbox\" name=\"isTaxExemption\" id=\"isTaxExemption\" " +
						isTaxExemptChecked+"/></td></tr>";
				}
				
				String isAlignPerkdata="";
				String isAlignPerkChecked="";
				if(uF.parseToBoolean(rs.getString("is_align_with_perk"))) {
					isAlignPerkChecked="checked";
				}
				if(!isAllowance && !sHeadList.contains(rs.getString("salary_head_id")) && rs.getString("earning_deduction").equals("E")) {
					isAlignPerkdata="<tr id=\"id_isAlignPerk_"+rs.getString("salary_id")+"\"><td style=\"color: rgb(255, 255, 255); " +
						"font-style: italic; font-size: 12px;\" nowrap=\"nowrap\">Is Aligned with Perk:</td><td><input type=\"checkbox\" name=\"isAlignPerk\" id=\"isAlignPerk\" " +
						isAlignPerkChecked+"/></td></tr>";
				}
				
				String isContributiondata="";
				String isContributionChecked="";
				if(uF.parseToBoolean(rs.getString("is_contribution"))) {
					isContributionChecked="checked";
				}
				if(!isAllowance) {
					isContributiondata="<tr id=\"id_isContribution_"+rs.getString("salary_id")+"\"><td style=\"color: rgb(255, 255, 255); " +
						"font-style: italic; font-size: 12px;\">Is Contribution:</td><td><input type=\"checkbox\" name=\"isContribution\" id=\"isContribution\" " +
						isContributionChecked+"/></td></tr>";
				}
				
				/*value=\""+check+"\"*/				
				sb.append("<div id=\"popup_name_edit_"+rs.getString("salary_id")+"\" class=\"popup_block\">" +
					"<h5 class=\"textcolorWhite\">Edit the Head Field</h5>" +				
					"<form id=\"frmCreditDetails\" action=\"SalaryDetails.action\" method=\"post\">");
				/*if(isAllowance) {
					sb.append("<input type=\"hidden\" name=\"headAmount\" id=\"headAmount_"+rs.getString("salary_head_id")+"\" value=\"0\"/>");
				}*/
				
				String strDAMsg = ""; 
				if(uF.parseToInt(rs.getString("salary_head_id")) == DA) {
					strDAMsg = "<span id=\"daSpan\" style=\"color: rgb(255, 255, 255); font-style: italic; font-size: 10px;width:100px\">This DA will be added with basic by default.</span>";
				}
				sb.append("<table class=\"table table_no_border\">" +
							"<tr style=\"display:table-row;\"><td></td><td><input type=\"hidden\" name=\"salaryHeadId\" value=\""+rs.getString("salary_head_id")+"\" ></input>" +
							"<input type=\"hidden\" name=\"salaryId\" value=\""+rs.getString("salary_id")+"\" ></input>" +
							"<input type=\"hidden\" name=\"level\" value=\""+getLevel()+"\" ></input>" +
							"<input type=\"hidden\" name=\"salaryBand\" value=\""+getSalaryBand()+"\" ></input>" +
							"<input type=\"hidden\" name=\"operation\" value=\"E\" ></input>" +
							"<input type=\"hidden\" name=\"earningOrDeduction\" id=\"headByte\" value=\""+rs.getString("earning_deduction")+"\"></input>" +
							"<input type=\"hidden\" name=\"strOrg\" value=\""+getStrOrg()+"\"></input>" +
							"<input type=\"hidden\" name=\"userscreen\" value=\""+getUserscreen()+"\"/>" +
							"<input type=\"hidden\" name=\"navigationId\" value=\""+getNavigationId()+"\"/>" +
							"<input type=\"hidden\" name=\"toPage\" value=\""+getToPage()+"\"/></td></tr>" +
							"<tr style=\"display:table-row;\">" +
								"<td style=\"color: rgb(255, 255, 255); font-style: italic; font-size: 12px;width:100px\" nowrap=\"nowrap\" valign=\"top\">Salary Head Name:</td>" +
								"<td><input type=\"text\" name=\"headName\" value=\""+rs.getString("salary_head_name")+"\" ></input><br/>" +
								"" + strDAMsg +
								"</td>" +
							"</tr>"  
								+ isCTCvariabledata + isvariabledata + isTaxExemptiondata + isAlignPerkdata+ isContributiondata +"");	
				if(!isAllowance) {
					sb.append("<tr style=\"display:table-row;\">" + 
							"<td style=\"color: rgb(255, 255, 255); font-style: italic; font-size: 12px;width:100px\">Type of Head:</td>" +
							"<td>"); 
					if(uF.parseToDouble(rs.getString("salary_head_id")) == CTC) {
						sb.append("<select id=\"newSel_"+rs.getString("salary_head_id")+"\" name=\"headAmountType\" style=\"width:91px\" onchange=\"javascript:show_sub_salary_head(this.id);return false;\" disabled>");
					} else {
						sb.append("<select id=\"newSel_"+rs.getString("salary_head_id")+"\" name=\"headAmountType\" style=\"width:91px\" onchange=\"javascript:show_sub_salary_head(this.id);return false;\" >");
					}
					
					if(uF.parseToDouble(rs.getString("salary_head_id")) == CTC) {
						sb.append("<option value=\"A\" selected=\"selected\">A (Amount)</option>");
						sb.append("<option value=\"P\">% (Percentage)</option>");
						/*sb.append("<option value=\"MP\">% (Multiple)</option>");
						sb.append("<option value=\"M\">A (Multiple)</option>");*/
					} else {
						/*if(rs.getString("salary_head_amount_type").trim().equals("M")) {
							sb.append("<option value=\"P\">% (Single)</option>");
							sb.append("<option value=\"A\">A (Single)</option>");
							sb.append("<option value=\"MP\">% (Multiple)</option>");
							sb.append("<option value=\"M\" selected=\"selected\">A (Multiple)</option>");
						} else if(rs.getString("salary_head_amount_type").trim().equals("MP")) {
							sb.append("<option value=\"P\">% (Single)</option>");
							sb.append("<option value=\"A\">A (Single)</option>");
							sb.append("<option value=\"MP\" selected=\"selected\">% (Multiple)</option>");
							sb.append("<option value=\"M\">A (Multiple)</option>");
						} else */if(rs.getString("salary_head_amount_type").trim().equals("P")) {
							sb.append("<option value=\"A\">A (Amount)</option>");
							sb.append("<option value=\"P\" selected=\"selected\">% (Percentage)</option>");
							/*sb.append("<option value=\"MP\">% (Multiple)</option>");
							sb.append("<option value=\"M\">A (Multiple)</option>");*/
						} else {
							sb.append("<option value=\"A\" selected=\"selected\">A (Amount)</option>");
							sb.append("<option value=\"P\">% (Percentage)</option>");							
							/*sb.append("<option value=\"MP\">% (Multiple)</option>");
							sb.append("<option value=\"M\">A (Multiple)</option>");*/
						}
					}
					
					sb.append("</select>" +
								"</td>" +
								"</tr>");
				} else {
					sb.append("<tr style=\"display:table-row;\">" + 
							"<td>&nbsp;</td>" +
							"<td><input type=\"hidden\" id=\"newSel_"+rs.getString("salary_head_id")+"\" name=\"headAmountType\" value=\"A\" ></td></tr>"); 
				}
							
//				sb.append("<tr style=\"display:table-row;\" id=\"id_salaryHead_"+rs.getString("salary_head_id")+"\">" +
//							"<td style=\"color: rgb(255, 255, 255); font-style: italic; font-size: 12px;width:100px\">% Of:</td>" +
//							"<td>" +
//							"<select name=\"salarySubHead\" id=\"salarySubHead_"+rs.getString("salary_head_id")+"\">" );
//				sb.append("<option value=\"0\">Select Salary Head</option>");			
//				for(FillSalaryHeads sd :salaryHeadList) {
//						if(sd.getSalaryHeadId().equals(rs.getString("sub_salary_head_id"))) {
//							
//							sb.append("<option value=\""+sd.getSalaryHeadId()+"\" selected=\"selected\"> "+sd.getSalaryHeadName()+"</option>");
//						}else
//							sb.append("<option value=\""+sd.getSalaryHeadId()+"\"> "+sd.getSalaryHeadName()+"</option>");
//				}
//				
//				sb.append("</select>" +									
//							"</td>" +
//						"</tr>");
				if(isAllowance) {					
					String isDefaultCalAllowanceChecked="";
					String isShowDefaultAllowanceAmount="none";
					if(uF.parseToBoolean(rs.getString("is_default_cal_allowance"))) {
						isDefaultCalAllowanceChecked="checked";
						isShowDefaultAllowanceAmount="table-row";
					}
					sb.append("<tr id=\"id_isDefaultCalculateAllowance_"+rs.getString("salary_head_id")+"\">" +
							"<td style=\"color: rgb(255, 255, 255); font-style: italic; font-size: 12px;\">Calculate with default figure:</td>" +
							"<td><input type=\"checkbox\" name=\"isDefaultCalculateAllowance\" id=\"isDefaultCalculateAllowance_"+rs.getString("salary_head_id")+"\" " +
							"onclick=\"showDefaultAmount(this.id);\" "+isDefaultCalAllowanceChecked+"/></td>" +
							"</tr>" +
							"<tr id=\"id_headAmountAllowance_"+rs.getString("salary_head_id")+"\" style=\"display:"+isShowDefaultAllowanceAmount+";\">" +
							"<td style=\"color: rgb(255, 255, 255); font-style: italic; font-size: 12px;\">Default Amount ("+uF.showData(currency, "")+"):</td>" +
							"<td><input type=\"text\" name=\"headAmount\" id=\"headAmountAllowance_"+rs.getString("salary_head_id")+"\" value=\""+((rs.getString("salary_head_amount_type") != null && rs.getString("salary_head_amount_type").equals("P")) ?  uF.formatIntoFourDecimalWithOutComma(uF.parseToDouble(rs.getString("salary_head_amount"))) : uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("salary_head_amount"))))+"\"/></td>" +
							"</tr>");
				} else {	
					sb.append("<tr style=\"display:table-row;\" id=\"id_headAmount_"+rs.getString("salary_head_id")+"\">" +
							"<td style=\"color: rgb(255, 255, 255); font-style: italic; font-size: 12px;width:100px\"><span id=\"id_headAmountType_"+rs.getString("salary_head_id")+"\">Percentage:</span></td>" +
							"<td>");
					sb.append("<input type=\"text\" name=\"headAmount\" id=\"headAmount_"+rs.getString("salary_head_id")+"\" value=\""+((rs.getString("salary_head_amount_type") != null && rs.getString("salary_head_amount_type").equals("P")) ?  uF.formatIntoFourDecimalWithOutComma(uF.parseToDouble(rs.getString("salary_head_amount"))) : uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("salary_head_amount"))))+"\"  />");
					sb.append("</td></tr>");
					sb.append("<tr style=\"display:table-row;\" id=\"id_headMaxCapAmount_"+rs.getString("salary_head_id")+"\">" +
							"<td style=\"color: rgb(255, 255, 255); font-style: italic; font-size: 12px;width:100px\"><span>Max Cap Amount:</span></td>" +
							"<td>");
					sb.append("<input type=\"text\" name=\"headMaxCapAmount\" id=\"headMaxCapAmount_"+rs.getString("salary_head_id")+"\" value=\""+uF.formatIntoFourDecimalWithOutComma(uF.parseToDouble(rs.getString("max_cap_amount")))+"\"  />");
					sb.append("</td></tr>");
				}
				
				/**
				 * Multiple Percentage Calculation
				 * */
				String strMulCalPerFormula = "";
				if(rs.getString("salary_head_amount_type").trim().equals("P")) {
					strMulCalPerFormula = sbMulcalType.toString();
				}
				sb.append("<tr id=\"trMultiplePercentageCalType_"+rs.getString("salary_head_id")+"\" style=\"display: none;\">" +
						"<td style=\"color: rgb(255, 255, 255); font-style: italic; font-size: 12px;\">&nbsp;</td>");
				sb.append("<td>");
				
				sb.append("<select id=\"bracketMulP_"+rs.getString("salary_head_id")+"\" name=\"bracketMulP\" style=\"width:50px !important;\">");
				sb.append("<option value=\"\">Select</option>");
				sb.append("<option value=\"+\">+</option>");
				sb.append("<option value=\"-\">-</option>");
				sb.append("<option value=\"*\">*</option>");
				sb.append("<option value=\"/\">/</option>");
				sb.append("<option value=\"(\">(</option>");
				sb.append("<option value=\")\">)</option>");
				sb.append("</select>");
				sb.append("<a href=\"javascript:void(0)\" class=\"add_lvl\" onclick=\"addMulPerCal('1','bracketMulP_"+rs.getString("salary_head_id")+"','salaryHeadMulP_"+rs.getString("salary_head_id")+"','strMulPerCalFormula_"+rs.getString("salary_head_id")+"','spanMulCalPercentage_"+rs.getString("salary_head_id")+"');\" title=\"Add Sign\"></a>");
				
				sb.append("<select name=\"salaryHeadMulP\" id=\"salaryHeadMulP_"+rs.getString("salary_head_id")+"\" style=\"width:91px !important;\">");
				sb.append("<option value=\"\">Select Salary Head</option>");
				
				pst1 = con.prepareStatement("SELECT * FROM salary_details WHERE level_id=? and salary_head_id !=? and weight <? " +
						"and salary_band_id=? and (is_delete is null or is_delete=false) order by earning_deduction desc, weight");
				pst1.setInt(1, uF.parseToInt(getLevel()));
				pst1.setInt(2, uF.parseToInt(rs.getString("salary_head_id")));
				pst1.setInt(3, uF.parseToInt(rs.getString("weight")));
				pst1.setInt(4, uF.parseToInt(getSalaryBand()));
//				System.out.println("pst1 ===>> " + pst1);
				rs1 = pst1.executeQuery();
				List<String> salHeadList = new ArrayList<String>();
				while (rs1.next()) {
					sb.append("<option value=\""+rs1.getString("salary_head_id")+"\"> "+rs1.getString("salary_head_name")+"</option>");
					if(!salHeadList.contains(rs1.getString("salary_head_id")) && (rs1.getInt("salary_head_id") == EMPLOYEE_EPF || rs1.getInt("salary_head_id") == EMPLOYEE_ESI || rs1.getInt("salary_head_id") == EMPLOYEE_LWF)) {
						salHeadList.add(rs1.getString("salary_head_id"));
					}
				}
				rs1.close();
				pst1.close();
//				System.out.println("salHeadList ===>>>> " + salHeadList);
				if(salHeadList != null && salHeadList.contains(EMPLOYEE_EPF+"")) {
					sb.append("<option value=\""+EMPLOYER_EPF+"\">Employer PF</option>");
				} 
				if(salHeadList != null && salHeadList.contains(EMPLOYEE_ESI+"")) {
					sb.append("<option value=\""+EMPLOYER_ESI+"\">Employer ESI</option>");
				}
				if(salHeadList != null && salHeadList.contains(EMPLOYEE_LWF+"")) {
					sb.append("<option value=\""+EMPLOYER_LWF+"\">Employer LWF</option>");
				}
				sb.append("</select>");
//				System.out.println("sb ===>>>> " + sb.toString());
				
				sb.append("<a href=\"javascript:void(0)\" class=\"add_lvl\" onclick=\"addMulPerCal('2','bracketMulP_"+rs.getString("salary_head_id")+"','salaryHeadMulP_"+rs.getString("salary_head_id")+"','strMulPerCalFormula_"+rs.getString("salary_head_id")+"','spanMulCalPercentage_"+rs.getString("salary_head_id")+"');\" title=\"Add Head\"></a>");
				sb.append("<img src=\"images1/icons/hd_cross_16x16.png\" style=\"vertical-align: top;\" onclick=\"resetMulPerCal('strMulPerCalFormula_"+rs.getString("salary_head_id")+"','spanMulCalPercentage_"+rs.getString("salary_head_id")+"');\">");
				sb.append("<br/>");
				sb.append("<input type=\"hidden\" name=\"strMulPerCalFormula\" id=\"strMulPerCalFormula_"+rs.getString("salary_head_id")+"\" value=\""+uF.showData(rs.getString("multiple_calculation"), "")+"\"/>");
				sb.append("<span id=\"spanMulCalPercentage_"+rs.getString("salary_head_id")+"\" style=\"color: rgb(255, 255, 255); font-style: italic; font-size: 12px;\">"+uF.showData(strMulCalPerFormula, "")+"</span>");
				
				sb.append("</td></tr>");
				/**
				 * Multiple Percentage Calculation End
				 * */
				
				if(isAllowance) {
					sb.append("<tr>"+
							"<td style=\"color: rgb(255, 255, 255); font-style: italic; font-size: 12px;width:100px\"></td>"+
							"<td style=\"color: rgb(255, 255, 255); font-style: italic; font-size: 12px;width:100px\">");
					sb.append("<input type=\"hidden\" name=\"salary_type\" value=\"F\"/>");
					sb.append("</td></tr>");
				} else {
					sb.append("<tr>"+
							"<td style=\"color: rgb(255, 255, 255); font-style: italic; font-size: 12px;width:100px\">Salary Type:</td>"+
							"<td style=\"color: rgb(255, 255, 255); font-style: italic; font-size: 12px;width:100px\">");
					if("F".equalsIgnoreCase(rs.getString("salary_type"))) {
						sb.append("<input type=\"radio\" name=\"salary_type\" value=\"M\" /> Monthly &nbsp;" +
								"<input type=\"radio\" name=\"salary_type\" value=\"D\" />Daily " +
								"<input type=\"radio\" name=\"salary_type\" value=\"F\" checked />Fixed ");
					}else if("D".equalsIgnoreCase(rs.getString("salary_type"))) {
						sb.append("<input type=\"radio\" name=\"salary_type\" value=\"M\" /> Monthly &nbsp;" +
								"<input type=\"radio\" name=\"salary_type\" value=\"D\" checked />Daily " +
								"<input type=\"radio\" name=\"salary_type\" value=\"F\"  />Fixed ");
					}else{
						sb.append("<input type=\"radio\" name=\"salary_type\" value=\"M\" checked /> Monthly &nbsp;" +
								"<input type=\"radio\" name=\"salary_type\" value=\"D\" />Daily " +
								"<input type=\"radio\" name=\"salary_type\" value=\"F\" />Fixed ");
					}
					sb.append("</td></tr>");
				}
				
				sb.append("<tr style=\"display:table-row;\"><td></td>" + 
					"<td><input type=\"submit\" class=\"btn btn-primary\" value=\"Submit\" align=\"center\"></input></td>" +
				"</tr></table></form></div>");
				
				int index = alSalaryDuplicationTracer.indexOf(rs.getString("salary_head_id"));
				if(index>=0) {
					al.remove(index);
					al.add(index, alInner);
				}else{
					alSalaryDuplicationTracer.add(rs.getString("salary_head_id"));
					al.add(alInner);
				}
				
				if(!alStatutoryIds.contains(rs.getString("salary_head_id")) 
						&& (uF.parseToInt(rs.getString("salary_head_id")) == PROFESSIONAL_TAX
						|| uF.parseToInt(rs.getString("salary_head_id")) == EMPLOYEE_EPF
						|| uF.parseToInt(rs.getString("salary_head_id")) == EMPLOYEE_ESI
						|| uF.parseToInt(rs.getString("salary_head_id")) == EMPLOYEE_LWF
						|| uF.parseToInt(rs.getString("salary_head_id")) == TDS)) {
					alStatutoryIds.add(rs.getString("salary_head_id"));
					hmStatutoryIds.put(rs.getString("salary_head_id"), rs.getString("salary_id"));
				}
				if(!alBasisSalIds.contains(rs.getString("salary_head_id"))  && (uF.parseToInt(rs.getString("salary_head_id")) == BASIC
						|| uF.parseToInt(rs.getString("salary_head_id")) == CTC)) {
					alBasisSalIds.add(rs.getString("salary_head_id"));
				}					
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("SELECT max(added_date) as added_date,added_by FROM salary_details WHERE level_id=? and salary_band_id=? and added_by >0 group by added_by limit 1");
			pst.setInt(1, uF.parseToInt(getLevel()));
			pst.setInt(2, uF.parseToInt(getSalaryBand()));
			rs = pst.executeQuery();
			int addedBy = 0;
			while(rs.next()) {
				addedBy = uF.parseToInt(rs.getString("added_by"));

				request.setAttribute("UPDATED_DATE", uF.getDateFormat(rs.getString("added_date"), DBDATE, CF.getStrReportDateFormat()));
			}
			rs.close();
			pst.close();
			
			if(addedBy > 0) {
				String strAddedByName = CF.getEmpNameMapByEmpId(con, ""+addedBy);
				request.setAttribute("UPDATED_NAME", uF.showData(strAddedByName, ""));
			}
				
//				alInner.add("<a href="+request.getContextPath()+"/AddAllowance.action?E="+rs.getString("allowance_id")+">Edit</a> | <a onclick=\"return confirm('"+ConfirmDelete+"');\" href="+request.getContextPath()+"/AddAllowance.action?D="+rs.getString("allowance_id")+">Delete</a>");
			
//			System.out.println("al=====>"+al);
//			System.out.println("alStatutoryIds=====>"+alStatutoryIds);
//			System.out.println("alBasisSalIds=====>"+alBasisSalIds);
//			System.out.println("hmTotal=====>"+hmTotal);
//			System.out.println("alAssignToPercentageHead=====>"+alAssignToPercentageHead);
			
			request.setAttribute("reportList", al);
			request.setAttribute("sb", sb.toString());
			request.setAttribute("alStatutoryIds", alStatutoryIds);
			request.setAttribute("alBasisSalIds", alBasisSalIds);
			request.setAttribute("hmStatutoryIds", hmStatutoryIds);
			request.setAttribute("alAssignToPercentageHead", alAssignToPercentageHead);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeResultSet(rs1);
			db.closeStatements(pst);
			db.closeStatements(pst1);
			db.closeConnection(con);
		}
	}
	
	public void setHeadName(String headName)
	{
		this.headName = headName;
	}
	public String getHeadName()
	{
		return headName;
	}
	public void setIsSave(boolean isSave)
	{
		this.isSave = isSave;
	}
	public boolean getIsSave()
	{
		return isSave;
	}
	public void setCurr_short(String curr_short)
	{
		this.curr_short = curr_short;
	}
	public String getCurr_short()
	{
		return curr_short;
	}
	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

//	public String getEmpId() {
//		return empId;
//	}
//
//	public void setEmpId(String empId) {
//		this.empId = empId;
//	}

	public List<FillSalaryHeads> getSalaryHeadList() {
		return salaryHeadList;
	}

	public void setSalaryHeadList(List<FillSalaryHeads> salaryHeadList) {
		this.salaryHeadList = salaryHeadList;
	}

	public String getSalarySubHead() {
		return salarySubHead;
	}

	public void setSalarySubHead(String salarySubHead) {
		this.salarySubHead = salarySubHead;
	}

	public String getEarningOrDeduction() {
		return earningOrDeduction;
	}

	public void setEarningOrDeduction(String earningOrDeduction) {
		this.earningOrDeduction = earningOrDeduction;
	}

	public String getHeadAmountType() {
		return headAmountType;
	}

	public void setHeadAmountType(String headAmountType) {
		this.headAmountType = headAmountType;
	}

	public String getHeadAmount() {
		return headAmount;
	}

	public void setHeadAmount(String headAmount) {
		this.headAmount = headAmount;
	}

	public String getRemoveId() {
		return removeId;
	}

	public void setRemoveId(String removeId) {
		this.removeId = removeId;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getSalaryHeadId() {
		return salaryHeadId;
	}

	public void setSalaryHeadId(String salaryHeadId) {
		this.salaryHeadId = salaryHeadId;
	}

	public List<FillLevel> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getSalaryId() {
		return salaryId;
	}

	public void setSalaryId(String salaryId) {
		this.salaryId = salaryId;
	}

	public String getHeadNameOther() {
		return headNameOther;
	}

	public void setHeadNameOther(String headNameOther) {
		this.headNameOther = headNameOther;
	}

	public List<FillSalaryHeads> getSalaryHeadListEarning() {
		return salaryHeadListEarning;
	}

	public void setSalaryHeadListEarning(List<FillSalaryHeads> salaryHeadListEarning) {
		this.salaryHeadListEarning = salaryHeadListEarning;
	}

	public List<FillSalaryHeads> getSalaryHeadListDeduction() {
		return salaryHeadListDeduction;
	}

	public void setSalaryHeadListDeduction(List<FillSalaryHeads> salaryHeadListDeduction) {
		this.salaryHeadListDeduction = salaryHeadListDeduction;
	}

	public List<FillOrganisation> getOrgList() {
		return orgList;
	}

	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
	}

	public String getStrOrg() {
		return strOrg;
	}

	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}

	public String getIsVariable() {
		return isVariable;
	}

	public void setIsVariable(String isVariable) {
		this.isVariable = isVariable;
	}

	public String getSalary_type() {
		return salary_type;
	}

	public void setSalary_type(String salary_type) {
		this.salary_type = salary_type;
	}

	public String getIsCTCVariable() {
		return isCTCVariable;
	}

	public void setIsCTCVariable(String isCTCVariable) {
		this.isCTCVariable = isCTCVariable;
	}

	public String getIsTaxExemption() {
		return isTaxExemption;
	}

	public void setIsTaxExemption(String isTaxExemption) {
		this.isTaxExemption = isTaxExemption;
	}

	public List<FillSalaryHeads> getSalaryHeadListIncentiveEarning() {
		return salaryHeadListIncentiveEarning;
	}

	public void setSalaryHeadListIncentiveEarning(List<FillSalaryHeads> salaryHeadListIncentiveEarning) {
		this.salaryHeadListIncentiveEarning = salaryHeadListIncentiveEarning;
	}

	public String getStrIsIncentive() {
		return strIsIncentive;
	}

	public void setStrIsIncentive(String strIsIncentive) {
		this.strIsIncentive = strIsIncentive;
	}

	public String getStrIsAllowance() {
		return strIsAllowance;
	}

	public void setStrIsAllowance(String strIsAllowance) {
		this.strIsAllowance = strIsAllowance;
	}

	public List<FillSalaryHeads> getSalaryHeadListAllowanceEarning() {
		return salaryHeadListAllowanceEarning;
	}

	public void setSalaryHeadListAllowanceEarning(List<FillSalaryHeads> salaryHeadListAllowanceEarning) {
		this.salaryHeadListAllowanceEarning = salaryHeadListAllowanceEarning;
	}

	public String getSalaryHead1() {
		return salaryHead1;
	}

	public void setSalaryHead1(String salaryHead1) {
		this.salaryHead1 = salaryHead1;
	}

	public String getSalaryHead2() {
		return salaryHead2;
	}

	public void setSalaryHead2(String salaryHead2) {
		this.salaryHead2 = salaryHead2;
	}

	public String getSalaryHead3() {
		return salaryHead3;
	}

	public void setSalaryHead3(String salaryHead3) {
		this.salaryHead3 = salaryHead3;
	}

	public String getSalaryHead4() {
		return salaryHead4;
	}

	public void setSalaryHead4(String salaryHead4) {
		this.salaryHead4 = salaryHead4;
	}

	public String getSalaryHead5() {
		return salaryHead5;
	}

	public void setSalaryHead5(String salaryHead5) {
		this.salaryHead5 = salaryHead5;
	}

	public String getSign1() {
		return sign1;
	}

	public void setSign1(String sign1) {
		this.sign1 = sign1;
	}

	public String getSign2() {
		return sign2;
	}

	public void setSign2(String sign2) {
		this.sign2 = sign2;
	}

	public String getSign3() {
		return sign3;
	}

	public void setSign3(String sign3) {
		this.sign3 = sign3;
	}

	public String getSign4() {
		return sign4;
	}

	public void setSign4(String sign4) {
		this.sign4 = sign4;
	}

	public String getSalaryHead6() {
		return salaryHead6;
	}

	public void setSalaryHead6(String salaryHead6) {
		this.salaryHead6 = salaryHead6;
	}

	public String getSalaryHead7() {
		return salaryHead7;
	}

	public void setSalaryHead7(String salaryHead7) {
		this.salaryHead7 = salaryHead7;
	}

	public String getSalaryHead8() {
		return salaryHead8;
	}

	public void setSalaryHead8(String salaryHead8) {
		this.salaryHead8 = salaryHead8;
	}

	public String getSalaryHead9() {
		return salaryHead9;
	}

	public void setSalaryHead9(String salaryHead9) {
		this.salaryHead9 = salaryHead9;
	}

	public String getSalaryHead10() {
		return salaryHead10;
	}

	public void setSalaryHead10(String salaryHead10) {
		this.salaryHead10 = salaryHead10;
	}

	public String getSign5() {
		return sign5;
	}

	public void setSign5(String sign5) {
		this.sign5 = sign5;
	}

	public String getSign6() {
		return sign6;
	}

	public void setSign6(String sign6) {
		this.sign6 = sign6;
	}

	public String getSign7() {
		return sign7;
	}

	public void setSign7(String sign7) {
		this.sign7 = sign7;
	}

	public String getSign8() {
		return sign8;
	}

	public void setSign8(String sign8) {
		this.sign8 = sign8;
	}

	public String getSign9() {
		return sign9;
	}

	public void setSign9(String sign9) {
		this.sign9 = sign9;
	}

	public String getStrGrade() {
		return strGrade;
	}

	public void setStrGrade(String strGrade) {
		this.strGrade = strGrade;
	}

	public List<FillGrade> getGradeList() {
		return gradeList;
	}

	public void setGradeList(List<FillGrade> gradeList) {
		this.gradeList = gradeList;
	}

	public String getIsAlignPerk() {
		return isAlignPerk;
	}

	public void setIsAlignPerk(String isAlignPerk) {
		this.isAlignPerk = isAlignPerk;
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

	public String getIsAnnualVariable() {
		return isAnnualVariable;
	}

	public void setIsAnnualVariable(String isAnnualVariable) {
		this.isAnnualVariable = isAnnualVariable;
	}

	public String getAutoUpdate() {
		return autoUpdate;
	}

	public void setAutoUpdate(String autoUpdate) {
		this.autoUpdate = autoUpdate;
	}
	public String getStrMulPerCalFormula() {
		return strMulPerCalFormula;
	}

	public void setStrMulPerCalFormula(String strMulPerCalFormula) {
		this.strMulPerCalFormula = strMulPerCalFormula;
	}

	public String getIsDefaultCalculateAllowance() {
		return isDefaultCalculateAllowance;
	}

	public void setIsDefaultCalculateAllowance(String isDefaultCalculateAllowance) {
		this.isDefaultCalculateAllowance = isDefaultCalculateAllowance;
	}

	public String getIsContribution() {
		return isContribution;
	}

	public void setIsContribution(String isContribution) {
		this.isContribution = isContribution;
	}

	public String getSalaryBandName() {
		return salaryBandName;
	}

	public void setSalaryBandName(String salaryBandName) {
		this.salaryBandName = salaryBandName;
	}

	public String getSalaryBandMinAmt() {
		return salaryBandMinAmt;
	}

	public void setSalaryBandMinAmt(String salaryBandMinAmt) {
		this.salaryBandMinAmt = salaryBandMinAmt;
	}

	public String getSalaryBandMaxAmt() {
		return salaryBandMaxAmt;
	}

	public void setSalaryBandMaxAmt(String salaryBandMaxAmt) {
		this.salaryBandMaxAmt = salaryBandMaxAmt;
	}

	public String getSalaryBand() {
		return salaryBand;
	}

	public void setSalaryBand(String salaryBand) {
		this.salaryBand = salaryBand;
	}

	public List<FillSalaryBand> getSalBandList() {
		return salBandList;
	}

	public void setSalBandList(List<FillSalaryBand> salBandList) {
		this.salBandList = salBandList;
	}

	public String getReplicateSalaryBand() {
		return replicateSalaryBand;
	}

	public void setReplicateSalaryBand(String replicateSalaryBand) {
		this.replicateSalaryBand = replicateSalaryBand;
	}

	public String getHeadMaxCapAmount() {
		return headMaxCapAmount;
	}

	public void setHeadMaxCapAmount(String headMaxCapAmount) {
		this.headMaxCapAmount = headMaxCapAmount;
	}
	
}