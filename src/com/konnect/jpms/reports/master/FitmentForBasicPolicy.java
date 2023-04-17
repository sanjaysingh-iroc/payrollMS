package com.konnect.jpms.reports.master;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class FitmentForBasicPolicy extends ActionSupport implements ServletRequestAware, IStatements{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5947893602821384559L;
	
	HttpSession session;
	CommonFunctions CF; 
	String strSessionEmpId;
	String strUserType;

	String strOrg;
	List<FillOrganisation> orgList;
	String strStatus;
	
	List<String> strLevel;
	private List<FillLevel> levelList;
	
	public String execute()	{
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		request.setAttribute(PAGE, "/jsp/reports/master/FitmentForBasicPolicy.jsp");
		request.setAttribute(TITLE, "Fitment for Basic");
		
		UtilityFunctions uF = new UtilityFunctions();
		
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strUserType = (String) session.getAttribute(USERTYPE);
		
		request.setAttribute("salaryStructure", CF.getStrSalaryStructure());
		
		if(uF.parseToInt(getStrOrg()) > 0 && getStrStatus()!=null && getStrStatus().equals("Update")){
			updateFitment(uF);
		}
		
		if(uF.parseToInt(getStrOrg()) == 0){
			setStrOrg((String)session.getAttribute(ORGID));
		}
		
		if(getStrLevel() != null && getStrLevel().size() > 0){
			viewFitmentData(uF);
		}
		
		return loadFitmentForBasicPolicy(uF);
	}

	private String loadFitmentForBasicPolicy(UtilityFunctions uF) {
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		}else{
			orgList = new FillOrganisation(request).fillOrganisation();
		}
		
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getStrOrg()));
		
		getSelectedFilter(uF);
		
		return SUCCESS;
	}
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		alFilter.add("ORGANISATION");
		if(getStrOrg()!=null)  {
			String strOrg="";
			int k=0;
			for(int i=0;orgList!=null && i<orgList.size();i++){
				if(getStrOrg().equals(orgList.get(i).getOrgId())) {
					if(k==0) {
						strOrg=orgList.get(i).getOrgName();
					} else {
						strOrg+=", "+orgList.get(i).getOrgName();
					}
					k++;
				}
			}
			if(strOrg!=null && !strOrg.equals("")) {
				hmFilter.put("ORGANISATION", strOrg);
			} else {
				hmFilter.put("ORGANISATION", "All Organisation");
			}
			
		} else {
			hmFilter.put("ORGANISATION", "All Organisation");
		}
		
		alFilter.add("LEVEL");
		if(getStrLevel()!=null) {
			String strLevel="";
			int k=0;
			for(int i=0;levelList!=null && i<levelList.size();i++) {
				if(getStrLevel().equals(levelList.get(i).getLevelId())) {
					if(k==0) {
						strLevel=levelList.get(i).getLevelCodeName();
					} else {
						strLevel+=", "+levelList.get(i).getLevelCodeName();
					}
					k++;
				}
			}
			if(strLevel!=null && !strLevel.equals("")) {
				hmFilter.put("LEVEL", strLevel);
			} else {
				hmFilter.put("LEVEL", "All Levels");
			}
		} else {
			hmFilter.put("LEVEL", "All Levels");
		}
		
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}

	private void updateFitment(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			String strLevelIds = (String)request.getParameter("strLevelIds");
			if(strLevelIds != null && !strLevelIds.trim().equals("") && !strLevelIds.trim().equalsIgnoreCase("NULL") && strLevelIds.length() > 0) {
				strLevel = Arrays.asList(strLevelIds.split(","));
				
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select * from grades_details gd, designation_details dd where dd.designation_id = gd.designation_id " +
				"and is_fitment=true and dd.level_id in(select ld.level_id from level_details ld where ld.org_id=? " +
				"and ld.level_id in ("+strLevelIds+")) and dd.level_id > 0");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, uF.parseToInt(getStrOrg()));
//				System.out.println("pst==>"+pst);
				rs = pst.executeQuery();
				List<String> gradeList = new ArrayList<String>();
				Map<String, String> hmGradeDesig = new HashMap<String, String>();
				while (rs.next()) {
					gradeList.add(rs.getString("grade_id"));
					hmGradeDesig.put(rs.getString("grade_id"), rs.getString("designation_id"));
				}
				rs.close();
				pst.close();
				
				for(int i=0;gradeList!=null && i<gradeList.size();i++){
					String strGradeId = gradeList.get(i);
					String strDesigId = hmGradeDesig.get(strGradeId);
					
	//				String strIncrementYr = (String) request.getParameter("strIncrementYr_"+gradeList.get(i));
					String strAmount = (String) request.getParameter("strAmount_"+gradeList.get(i));
	//				String fitmentType = (String) request.getParameter("fitmentType_"+gradeList.get(i));
					
					String strBaseAmount = (String) request.getParameter("strBaseAmount_"+strDesigId);
					String strIncrementAmount = (String) request.getParameter("strIncrementAmount_"+strDesigId);
					
					pst = con.prepareStatement("update basic_fitment_details set trail_status=2 where grade_id=?");
					pst.setInt(1, uF.parseToInt(gradeList.get(i)));
					pst.execute();
					pst.close();
					
					pst = con.prepareStatement("insert into basic_fitment_details(grade_id,increment_year,amount,base_amount,increment_amount," +
							"entry_date,update_by,trail_status)values(?,?,?,?, ?,?,?,?)");
					pst.setInt(1, uF.parseToInt(strGradeId));
					pst.setInt(2, 1);
					pst.setDouble(3, uF.parseToDouble(strAmount));
					pst.setDouble(4, uF.parseToDouble(strBaseAmount));
					pst.setDouble(5, uF.parseToDouble(strIncrementAmount));
					pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(7, uF.parseToInt((String) session.getAttribute(EMPID)));
					pst.setInt(8, 1);
					int x=pst.executeUpdate();
					pst.close();
					
					if(x > 0){
//						Map<String, String> hmSalaryMap = CF.getSalaryHeadsMap(con);
						
						pst = con.prepareStatement("update salary_details set salary_head_amount=? where grade_id=? and salary_head_id=? and (is_delete is null or is_delete=false)");
						pst.setDouble(1, uF.parseToDouble(strAmount));
						pst.setInt(2, uF.parseToInt(strGradeId));
						pst.setInt(3, BASIC);
//						System.out.println("pst==>"+pst);
						int y = pst.executeUpdate();
						pst.close();
						
						if(y == 0){
							pst = con.prepareStatement("select * from salary_details where salary_head_id=? and (is_delete is null or is_delete=false) limit 1");
							pst.setInt(1, BASIC);
							rs = pst.executeQuery();
							String strSalaryHeadName = null;
							while(rs.next()){
								strSalaryHeadName = rs.getString("salary_head_name");
							}
							rs.close();
							pst.close();
							
							pst=con.prepareStatement("INSERT INTO salary_details (salary_head_name, earning_deduction,salary_head_amount_type, " +
									"sub_salary_head_id, salary_head_amount, grade_id, salary_head_id, weight, org_id,is_variable, salary_type,is_ctc_variable," +
									"is_tax_exemption,added_by,added_date) " +
									"VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?)");
//							pst.setString(1, hmSalaryMap.get(""+BASIC));
							pst.setString(1, strSalaryHeadName);
							pst.setString(2, "E");
							pst.setString(3, "A");
							pst.setInt(4, 0);
							pst.setDouble(5, uF.parseToDouble(strAmount));
							pst.setInt(6, uF.parseToInt(strGradeId));
							pst.setInt(7, BASIC);
							pst.setInt(8, 1);
							pst.setInt(9, uF.parseToInt(getStrOrg()));
							pst.setBoolean(10, false);
							pst.setString(11, "M");
							pst.setBoolean(12, false);
							pst.setBoolean(13, false);
							pst.setInt(14, uF.parseToInt((String) session.getAttribute(EMPID)));
							pst.setDate(15, uF.getCurrentDate(CF.getStrTimeZone()));
//							System.out.println("pst======>"+pst);
							int z = pst.executeUpdate();
							pst.close();
							
							if(z > 0){
								pst = con.prepareStatement("select emp_id from employee_personal_details epd, employee_official_details eod where epd.emp_per_id=eod.emp_id " +
										" and eod.grade_id in (select gd.grade_id from grades_details gd, designation_details dd where " +
										"gd.designation_id = dd.designation_id and gd.grade_id=?)");
								pst.setInt(1,uF.parseToInt(strGradeId));
								rs = pst.executeQuery();
								List<String> alSalEmpList = new ArrayList<String>();
								while(rs.next()){
									alSalEmpList.add(rs.getString("emp_id"));
								}
								rs.close();
								pst.close();
								
								for(int j=0; j<alSalEmpList.size(); j++){
									String strEmpId = alSalEmpList.get(j);
									pst = con.prepareStatement("INSERT INTO emp_salary_details (emp_id," +
											"salary_head_id, amount, entry_date, user_id, pay_type, " +
											"isdisplay, service_id, effective_date, earning_deduction, " +
											"salary_type,is_approved,approved_by,approved_date,grade_id) " +
											"VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?)");
									pst.setInt(1, uF.parseToInt(strEmpId));
									pst.setInt(2, BASIC);
									pst.setDouble(3, uF.parseToDouble(strAmount));
									pst.setDate	(4, uF.getCurrentDate(CF.getStrTimeZone()));
									pst.setInt(5, uF.parseToInt((String) session.getAttribute(EMPID)));
									pst.setString(6, "M");
									pst.setBoolean(7, true);
									pst.setInt(8, 0);
									pst.setDate	(9, uF.getCurrentDate(CF.getStrTimeZone()));
									pst.setString(10, "E");
									pst.setString(11, "M");
									pst.setBoolean(12, true);
									pst.setInt(13, uF.parseToInt((String) session.getAttribute(EMPID)));
									pst.setDate	(14, uF.getCurrentDate(CF.getStrTimeZone()));
									pst.setInt(15,uF.parseToInt(strGradeId));
//									System.out.println("pst======>"+pst);
									pst.execute();
									pst.close();
									
									CF.updateNextEmpSalaryEffectiveDate(con, uF, uF.parseToInt(strEmpId), uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), DATE_FORMAT);
									/**
									 * Calaculate CTC Start
									 * */
	//								Map<String, String> hmEmpProfile = CF.getEmpProfileDetail(con, request, session, CF, uF, null, strEmpId);
	//								
	//								MyProfile myProfile = new MyProfile();
	//								myProfile.session = session;
	//								myProfile.request = request;
	//								myProfile.CF = CF;
	//								int intEmpIdReq = uF.parseToInt(strEmpId);
	//								myProfile.getSalaryHeadsforEmployee(con, uF, intEmpIdReq, hmEmpProfile);
	//								
	//								double grossAmount = 0.0d;
	//								double grossYearAmount = 0.0d;
	//								double deductAmount = 0.0d;
	//								double deductYearAmount = 0.0d;
	//								double netAmount = 0.0d;
	//								double netYearAmount = 0.0d;
	//								
	//								List<List<String>> salaryHeadDetailsList = (List<List<String>>) request.getAttribute("salaryHeadDetailsList");
	//								for(int i=0; salaryHeadDetailsList != null && !salaryHeadDetailsList.isEmpty() && i<salaryHeadDetailsList.size(); i++) {
	//									List<String> innerList = salaryHeadDetailsList.get(i);
	//										if(innerList.get(1).equals("E")) {
	//											grossAmount +=uF.parseToDouble(innerList.get(2));
	//											grossYearAmount +=uF.parseToDouble(innerList.get(3));
	//										} else if(innerList.get(1).equals("D")) {
	//											deductAmount +=uF.parseToDouble(innerList.get(2));
	//											deductYearAmount +=uF.parseToDouble(innerList.get(3));
	//										}
	//								}
	//								
	//								netAmount = grossAmount;
	//								netAmount = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(netAmount));
	//								 
	//								netYearAmount = grossYearAmount;
	//								netYearAmount = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(netYearAmount));
	//					            
	//								pst = con.prepareStatement("update employee_official_details set month_ctc=?,annual_ctc=? where emp_id=?");
	//								pst.setDouble(1, netAmount);
	//								pst.setDouble(2, netYearAmount);
	//								pst.setInt(3, uF.parseToInt(strEmpId));
	//								pst.execute();
	//								pst.close();
									
									/**
									 * Calaculate CTC End
									 * */
									
								}
							}
							
						}
					}
				}
				session.setAttribute(MESSAGE, SUCCESSM+"Updated Successfully!"+END);
			} else {
				session.setAttribute(MESSAGE, ERRORM+"Updated failed. Please try again."+END);
			}
			
		} catch (Exception e) {
			session.setAttribute(MESSAGE, ERRORM+"Updated failed. Please try again."+END);
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void viewFitmentData(UtilityFunctions uF) {

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> hmLevelMap = new LinkedHashMap<String, String>();
		Map<String, Map<String, String>> hmDesigMap = new LinkedHashMap<String, Map<String, String>>();
		Map<String, Map<String, String>> hmGradeMap = new LinkedHashMap<String, Map<String, String>>();
		try {
			con = db.makeConnection(con);
			
			String strLevelIds = StringUtils.join(getStrLevel().toArray(),",");
			request.setAttribute("strLevelIds", strLevelIds);
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from grades_details gd, designation_details dd where dd.designation_id = gd.designation_id " +
					"and is_fitment=true and dd.level_id in (select ld.level_id from level_details ld where ld.org_id=? and ld.level_id in ("+strLevelIds+")) " +
					"and dd.level_id > 0 order by gd.weightage desc");
			pst = con.prepareStatement(sbQuery.toString());			
			pst.setInt(1, uF.parseToInt(getStrOrg()));
			//System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			String strGradeIds = "";
			List<String> alLevel = new ArrayList<String>();
			Map<String, String> hmDesigGrade = new HashMap<String, String>();
			Map<String, String> hmGradeDesig = new HashMap<String, String>();
			while (rs.next()) {
				Map<String, String> hmDesig = hmDesigMap.get(rs.getString("level_id"));
				if(hmDesig==null) hmDesig = new LinkedHashMap<String, String>();
				hmDesig.put(rs.getString("designation_id"), rs.getString("designation_name")+" ["+rs.getString("designation_code")+"]");
				
				hmDesigMap.put(rs.getString("level_id"), hmDesig);
				
				Map<String, String> hmGrade = hmGradeMap.get(rs.getString("designation_id"));
				if(hmGrade==null) hmGrade = new LinkedHashMap<String, String>();
				hmGrade.put(rs.getString("grade_id"), rs.getString("grade_code"));
				
				hmGradeMap.put(rs.getString("designation_id"), hmGrade);
				
				if(strGradeIds.equals("")){
					strGradeIds = rs.getString("grade_id");
				}else{
					strGradeIds += ","+rs.getString("grade_id");
				}
				
				if(!alLevel.contains(rs.getString("level_id"))){
					alLevel.add(rs.getString("level_id"));
				}
				String sbGrade = hmDesigGrade.get(rs.getString("designation_id")); 
				if(sbGrade == null || sbGrade.trim().equals("") || sbGrade.trim().equalsIgnoreCase("NULL")){
					sbGrade = rs.getString("grade_id");
				} else {
					sbGrade +=","+ rs.getString("grade_id");
				}
				hmDesigGrade.put(rs.getString("designation_id"),sbGrade);
				
				hmGradeDesig.put(rs.getString("grade_id"), rs.getString("designation_id"));
				
			}
			rs.close();
			pst.close();
			
			if(alLevel!=null && !alLevel.isEmpty() && alLevel.size() > 0){
				String strLevelIds1 = StringUtils.join(alLevel.toArray(),",");
				
				sbQuery = new StringBuilder();
				sbQuery.append("select ld.* from level_details ld, org_details od where ld.org_id=od.org_id");
				sbQuery.append(" and ld.level_id in ("+strLevelIds1+") order by ld.level_id");
				pst = con.prepareStatement(sbQuery.toString());		
				//System.out.println("pst ==>> " + pst);
				rs = pst.executeQuery();
				while(rs.next()){
					hmLevelMap.put(rs.getString("level_id"), rs.getString("level_name")+" ["+rs.getString("level_code")+"]");
				}
				rs.close();
				pst.close();
			}
			
			Map<String, String> hmFitment = new HashMap<String, String>();
			if(strGradeIds!=null && !strGradeIds.equals("")){
				pst = con.prepareStatement("select * from basic_fitment_details where grade_id in ("+strGradeIds+") and trail_status=1");
				//System.out.println("pst==>"+pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					hmFitment.put("AMOUNT_"+rs.getString("grade_id"), rs.getString("AMOUNT"));
					
					String strDesigId = hmGradeDesig.get(rs.getString("grade_id"));
					hmFitment.put("BASE_AMOUNT_"+strDesigId, rs.getString("base_amount"));
					hmFitment.put("INCREMENT_AMOUNT_"+strDesigId, rs.getString("increment_amount"));
				}
				rs.close();
				pst.close();
				//grade_id,increment_year,amount,base_amount,increment_amount,entry_date,update_by,trail_status
			}
			request.setAttribute("hmLevelMap", hmLevelMap);
			request.setAttribute("hmDesigMap", hmDesigMap);
			request.setAttribute("hmGradeMap", hmGradeMap);
			request.setAttribute("hmFitment", hmFitment);
			request.setAttribute("hmDesigGrade", hmDesigGrade);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
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
	public List<FillOrganisation> getOrgList() {
		return orgList;
	}
	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
	}

	public String getStrStatus() {
		return strStatus;
	}

	public void setStrStatus(String strStatus) {
		this.strStatus = strStatus;
	}
	public List<String> getStrLevel() {
		return strLevel;
	}

	public void setStrLevel(List<String> strLevel) {
		this.strLevel = strLevel;
	}

	public List<FillLevel> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}
}