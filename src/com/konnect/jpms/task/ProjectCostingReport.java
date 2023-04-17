package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillClients;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ProjectCostingReport extends ActionSupport implements ServletRequestAware, IStatements{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpServletRequest request;
	HttpSession session;
	CommonFunctions CF; 
	String strEmpId;
	String strUserType;
	
	
	String strType;
	
	String f_org;
	String[] f_strWLocation; 
	String[] f_department;
	String[] f_service;
	String[] f_level;
	String[] f_project_service;
	String[] f_client;
	
	String selectOne;
	String strStartDate;
	String strEndDate;
	String financialYear;
	String monthFinancialYear;
	String paycycle;
	String strMonth;
	
	List<FillOrganisation> organisationList;
	List<FillWLocation> wLocationList;
	List<FillDepartment> departmentList;
	List<FillServices> serviceList;
	List<FillServices> projectServiceList;
	List<FillLevel> levelList;
	List<FillFinancialYears> financialYearList;
	List<FillMonth> monthList;
	List<FillPayCycles> paycycleList;
	
	List<FillClients> clientList;

	
	public String execute(){
		session = request.getSession();
		
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF == null) return LOGIN;
		
		strUserType = (String)session.getAttribute(BASEUSERTYPE);
		strEmpId =(String) session.getAttribute(EMPID);
		
		request.setAttribute(PAGE, "/jsp/task/ProjectCostingReport.jsp");
		request.setAttribute(TITLE, "Project Costing");
		
		UtilityFunctions uF = new UtilityFunctions();
		
//		boolean isView  = CF.getAccess(session, request, uF);
//		if(!isView){
//			request.setAttribute(PAGE, PAccessDenied);
//			request.setAttribute(TITLE, TAccessDenied);
//			return ACCESS_DENIED;
//		}
		
		if(getF_org() == null){
			setF_org((String)session.getAttribute(ORGID));
		}
		
//		getProjectDetails(0, uF, CF, 0);
		
		loadProjectCostingReport(uF);
		getProjectCosting();
		
		return SUCCESS;
	}
	
	
	public String loadProjectCostingReport(UtilityFunctions uF) {
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		} else {
			organisationList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillServices(getF_org(), uF);
		projectServiceList = new FillServices(request).fillProjectServices();
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		monthList = new FillMonth().fillMonth();
		paycycleList = new FillPayCycles(request).fillPayCycles(CF, getF_org());
		clientList = new FillClients(request).fillAllClients(false);
		getSelectedFilter(uF);
		
		return SUCCESS;
	}
	
	
	public void getProjectCosting() {

			UtilityFunctions uF=new UtilityFunctions();
			Database db = new Database();
			db.setRequest(request);
			Connection con = null;
			PreparedStatement pst = null;
			ResultSet rs = null;
			try {
				con = db.makeConnection(con);
				
//				Map<String, String> hmProActualCost = (Map<String, String>)request.getAttribute("hmProActualCost");
//				Map<String, String> hmProReimbursement = (Map<String, String>)request.getAttribute("hmProReimbursement");
				
				Map<String, String> hmReimbursementAmountMap = CF.getReimbursementAmount(con, "P", "P", true, getStrStartDate(), getStrEndDate(), uF);
				
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select * from projectmntnc p where p.approve_status = 'approved' ");

				if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)) {
				//===start parvez date: 14-10-2022===	
					/*sbQuery.append(" and ( pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
							+uF.parseToInt((String)session.getAttribute(EMPID))+" ) or added_by = "+uF.parseToInt((String)session.getAttribute(EMPID))
							+" or project_owner = "+uF.parseToInt((String)session.getAttribute(EMPID))+" ) ");*/
					sbQuery.append(" and ( pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
							+uF.parseToInt((String)session.getAttribute(EMPID))+" ) or added_by = "+uF.parseToInt((String)session.getAttribute(EMPID))
							+" or project_owners like '%,"+(String)session.getAttribute(EMPID)+",%' ) ");
				//===end parvez date: 14-10-2022===	
//					sbQuery.append(" and p.added_by = "+uF.parseToInt(strEmpId));
				}
				/*if(uF.parseToInt(getF_org())>0 || (getF_strWLocation() != null && getF_strWLocation().length>0) || (getF_department() != null && getF_department().length>0) || (getF_service() != null && getF_service().length>0) || (getF_level() != null && getF_level().length>0)) {
					sbQuery.append(" and p.pro_id in (select pro_id from projectmntnc where pro_id>0 ");
				}*/
				if(uF.parseToInt(getF_org())>0) {
					sbQuery.append(" and p.org_id in ("+getF_org()+")");
				} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
					sbQuery.append(" and p.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
				}
				if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
		            sbQuery.append(" and p.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+")");
		        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
					sbQuery.append(" and p.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
				}
				
				if(getF_department() != null && getF_department().length>0) {
					sbQuery.append(" and p.department_id in ("+StringUtils.join(getF_department(), ",")+") ");
				}
				if(getF_service() != null && getF_service().length>0) {
					sbQuery.append(" and p.sbu_id in ("+StringUtils.join(getF_service(), ",")+") ");
				}
				if(getF_project_service() != null && getF_project_service().length>0) {
					String services = uF.getConcateData(getF_project_service());
					sbQuery.append(" and p.service in ("+services+") ");
				}
				if(getF_client() != null && getF_client().length>0) {
					sbQuery.append(" and p.client_id in ("+StringUtils.join(getF_client(), ",")+") ");
				}
				
				if(getStrStartDate()!=null && getStrEndDate()!=null && !getStrStartDate().equalsIgnoreCase("null") && !getStrStartDate().equals("") && !getStrEndDate().equalsIgnoreCase("null") && !getStrEndDate().equals("")) {
					sbQuery.append(" and ('"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE)+"' between p.start_date and p.deadline or '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"' between p.start_date and p.deadline " +
						"or p.start_date between '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "' or p.deadline between '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "' ) ");
//					sbQuery.append(" and ('"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE)+"' between p.start_date and p.deadline or '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"' between p.start_date and p.deadline) ");
				}
				sbQuery.append(" order by pro_id");
//				sbQuery.append(" group by pmntc.client_id) as d,client_details cd where d.client_id=cd.client_id");
				pst = con.prepareStatement(sbQuery.toString());
//				System.out.println("pst ===> " + pst);
				rs=pst.executeQuery();
				List<List<String>> alOuter = new ArrayList<List<String>>();
				String billingType = null;
				while(rs.next()) {
					
					Map<String, String> hmProjectData = CF.getProjectDetailsByProId(con, rs.getString("pro_id"));
//					hmProjectData.put("PRO_START_DATE", rs.getString("start_date"));
//					hmProjectData.put("PRO_END_DATE", rs.getString("deadline"));
//					hmProjectData.put("PRO_ACTUAL_BILL_TYPE", rs.getString("actual_calculation_type"));
//					hmProjectData.put("PRO_BILL_DAYS_TYPE", rs.getString("bill_days_type"));
//					hmProjectData.put("PRO_HOURS_FOR_BILL_DAY", rs.getString("hours_for_bill_day"));
					
					Map<String, String> hmProActualCostTimeAndBillCost = CF.getProjectActualCostAndTime(con,request, CF, uF, rs.getString("pro_id"), hmProjectData, false, false);
					
					billingType = rs.getString("actual_calculation_type");
					
					List<String> alInner = new ArrayList<String>();
					double dblReimbursement = uF.parseToDouble(hmReimbursementAmountMap.get(rs.getString("pro_id")));
					alInner.add(rs.getString("pro_id"));
					alInner.add(rs.getString("pro_name"));
					alInner.add(uF.showData(CF.getClientNameById(con, rs.getString("client_id")), "-"));
					alInner.add(uF.showData(CF.getEmpNameMapByEmpId(con, rs.getString("added_by")), "-"));
					alInner.add(uF.formatIntoTwoDecimal(uF.parseToDouble(hmProActualCostTimeAndBillCost.get("proActualCost"))));
					alInner.add(uF.formatIntoTwoDecimal(dblReimbursement));
					
					double totAmount = uF.parseToDouble(hmProActualCostTimeAndBillCost.get("proActualCost")) + dblReimbursement;
					alInner.add(uF.formatIntoTwoDecimal(totAmount));
					
					alOuter.add(alInner);
				}
				rs.close();
				pst.close();
				
				request.setAttribute("billingType", billingType);
				request.setAttribute("alOuter", alOuter);
				
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				db.closeResultSet(rs);
				db.closeStatements(pst);
				db.closeConnection(con);
			}
	}
	
	
	
//public void getProjectDetails(int nManagerId, UtilityFunctions uF, CommonFunctions CF, int nLimit) {
//		
//		Database db = new Database();
//		db.setRequest(request);
//		Connection con = null;
//		PreparedStatement pst=null;
//		ResultSet rs=null;
//		try {
//			con = db.makeConnection(con);
//			
//			Map<String, String> hmReimbursementAmountMap = CF.getReimbursementAmount(con, "P", "P", true, getStrStartDate(), getStrEndDate(), uF);
//			Map<String, String> hmEmpGrossAmountMapH = CF.getEmpNetSalary(uF, CF, con, uF.getCurrentDate(CF.getStrTimeZone())+"", "H");
//			Map<String, String> hmEmpGrossAmountMapD = CF.getEmpNetSalary(uF, CF, con, uF.getCurrentDate(CF.getStrTimeZone())+"", "D");
//			
//			StringBuilder sbQuery1 = new StringBuilder();
//			
//			sbQuery1.append("select *, ai.emp_id as a_emp_id, ai.idealtime as a_idealtime, pmc.idealtime as pmc_idealtime, ai.already_work " +
//				"as a_already_work, ai.already_work_days as a_already_work_days, pmc.actual_calculation_type from activity_info ai, " +
//				"projectmntnc pmc where pmc.pro_id = ai.pro_id and pmc.approve_status = 'approved' ");
//			
//			if(uF.parseToInt(getF_org())>0 || (getF_strWLocation() != null && getF_strWLocation().length>0) || (getF_department() != null && getF_department().length>0) || (getF_service() != null && getF_service().length>0) || (getF_level() != null && getF_level().length>0)) {
//				sbQuery1.append(" and pmc.pro_id in (select pro_id from projectmntnc where pro_id>0 ");
//			}
//			 
//			if(uF.parseToInt(getF_org())>0) {
//				sbQuery1.append(" and pmc.wlocation_id in (select wlocation_id from work_location_info where org_id = "+uF.parseToInt(getF_org())+")");
//			}
//			if(getF_strWLocation() != null && getF_strWLocation().length>0) {
//				sbQuery1.append(" and pmc.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
//			}
//			if(getF_department() != null && getF_department().length>0) {
//				sbQuery1.append(" and pmc.department_id in ("+StringUtils.join(getF_department(), ",")+") ");
//			}
//			if(getF_project_service() != null && getF_project_service().length>0) {
//				String services = uF.getConcateData(getF_project_service());
//				sbQuery1.append(" and pmc.service in ("+services+") ");
//			}
//			if(getF_client() != null && getF_client().length>0) {
//				sbQuery1.append(" and pmc.client_id in ("+StringUtils.join(getF_client(), ",")+") ");
//			}
//			
//			if(uF.parseToInt(getF_org())>0 || (getF_strWLocation() != null && getF_strWLocation().length>0) || (getF_department() != null && getF_department().length>0) || (getF_service() != null && getF_service().length>0) || (getF_level() != null && getF_level().length>0)) {
//				sbQuery1.append(" ) ");
//			}
//			
//			if(getStrStartDate()!=null && getStrEndDate()!=null && !getStrStartDate().equalsIgnoreCase(LABEL_FROM_DATE) && !getStrEndDate().equalsIgnoreCase(LABEL_TO_DATE)) {
//				sbQuery1.append(" and pmc.start_date between '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE)+"' and '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"' ");
//			}
//			if(nManagerId > 0) {
//				sbQuery1.append(" and added_by = " + nManagerId);
//			}
//			sbQuery1.append(" order by pmc.pro_id ");
//			
//			pst = con.prepareStatement(sbQuery1.toString());
//			rs = pst.executeQuery();
//			System.out.println("pst==>" + pst);
//
//			double dblActualAmt = 0;
//			double dblActualTime = 0;
//			
//			Map<String, String> hmProActualCost = new HashMap<String, String>();
//			Map<String, String> hmProReimbursement = new HashMap<String, String>();
//			
//			String strProjectIdNew = null;
//			String strProjectIdOld = null;
//			
//			double dblEmpRate = 0.0d;
//			
//			List<String> proIdList = new ArrayList<String>();
//			while(rs.next()) {
//				
//				strProjectIdNew = rs.getString("pro_id");
//				double dblReimbursement = uF.parseToDouble(hmReimbursementAmountMap.get(rs.getString("pro_id")));
//				
//				if(strProjectIdNew != null && !strProjectIdNew.equalsIgnoreCase(strProjectIdOld)) {
//					dblActualAmt = 0;
//				}
//				 
//				 if("H".equalsIgnoreCase(rs.getString("actual_calculation_type"))) {
//					 dblEmpRate = uF.parseToDouble(hmEmpGrossAmountMapH.get(rs.getString("a_emp_id")));
//					 dblActualTime += uF.parseToDouble(rs.getString("a_already_work"));
//				 } else {
//					 dblEmpRate = uF.parseToDouble(hmEmpGrossAmountMapD.get(rs.getString("a_emp_id")));
//					 dblActualTime += uF.parseToDouble(rs.getString("a_already_work_days"));
//				 }
//				 
//				 dblActualAmt += dblActualTime * dblEmpRate;
//				 
//				 hmProActualCost.put(rs.getString("pro_id"), uF.formatIntoOneDecimal(dblActualAmt));
//				 hmProReimbursement.put(rs.getString("pro_id"), uF.formatIntoOneDecimal(dblReimbursement));
//				 
//				 if(!proIdList.contains(strProjectIdNew)) {
//					 proIdList.add(strProjectIdNew);
//				 }
//				 
//				 strProjectIdOld = strProjectIdNew;
//			}
//			rs.close();
//			pst.close();
//			
//			request.setAttribute("hmProActualCost", hmProActualCost);
//			request.setAttribute("hmProReimbursement", hmProReimbursement);
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//	}
	

	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
	
		
		
		alFilter.add("ORGANISATION");
		if(getF_org()!=null)  {
			String strOrg="";
			int k=0;
			for(int i=0;organisationList!=null && i<organisationList.size();i++){
				if(getF_org().equals(organisationList.get(i).getOrgId())) {
					if(k==0) {
						strOrg=organisationList.get(i).getOrgName();
					} else {
						strOrg+=", "+organisationList.get(i).getOrgName();
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
		
		alFilter.add("LOCATION");
		if(getF_strWLocation()!=null) {
			String strLocation="";
			int k=0;
			for(int i=0;wLocationList!=null && i<wLocationList.size();i++) {
				for(int j=0;j<getF_strWLocation().length;j++) {
					if(getF_strWLocation()[j].equals(wLocationList.get(i).getwLocationId())) {
						if(k==0) {
							strLocation=wLocationList.get(i).getwLocationName();
						} else {
							strLocation+=", "+wLocationList.get(i).getwLocationName();
						}
						k++;
					}
				}
			}
			if(strLocation!=null && !strLocation.equals("")) {
				hmFilter.put("LOCATION", strLocation);
			} else {
				hmFilter.put("LOCATION", "All Locations");
			}
		} else {
			hmFilter.put("LOCATION", "All Locations");
		}
		
		alFilter.add("DEPARTMENT");
		if(getF_department()!=null) {
			String strDepartment="";
			int k=0;
			for(int i=0;departmentList!=null && i<departmentList.size();i++) {
				for(int j=0;j<getF_department().length;j++) {
					if(getF_department()[j].equals(departmentList.get(i).getDeptId())) {
						if(k==0) {
							strDepartment=departmentList.get(i).getDeptName();
						} else {
							strDepartment+=", "+departmentList.get(i).getDeptName();
						}
						k++;
					}
				}
			}
			if(strDepartment!=null && !strDepartment.equals("")) {
				hmFilter.put("DEPARTMENT", strDepartment);
			} else {
				hmFilter.put("DEPARTMENT", "All Departments");
			}
		} else {
			hmFilter.put("DEPARTMENT", "All Departments");
		}
		
		alFilter.add("SERVICE");
		if(getF_service()!=null) {
			String strService="";
			int k=0;
			for(int i=0;serviceList!=null && i<serviceList.size();i++) {
				for(int j=0;j<getF_service().length;j++) {
					if(getF_service()[j].equals(serviceList.get(i).getServiceId())) {
						if(k==0) {
							strService=serviceList.get(i).getServiceName();
						} else {
							strService+=", "+serviceList.get(i).getServiceName();
						}
						k++;
					}
				}
			}
			if(strService!=null && !strService.equals("")) {
				hmFilter.put("SERVICE", strService);
			} else {
				hmFilter.put("SERVICE", "All SBUs");
			}
		} else {
			hmFilter.put("SERVICE", "All SBUs");
		}
		
		alFilter.add("LEVEL");
		if(getF_level()!=null) {
			String strLevel="";
			int k=0;
			for(int i=0;levelList!=null && i<levelList.size();i++) {
				for(int j=0;j<getF_level().length;j++) {
					if(getF_level()[j].equals(levelList.get(i).getLevelId())) {
						if(k==0) {
							strLevel=levelList.get(i).getLevelCodeName();
						} else {
							strLevel+=", "+levelList.get(i).getLevelCodeName();
						}
						k++;
					}
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
		
		alFilter.add("PROJECT_SERVICE");
		if(getF_project_service()!=null) {
			String strProjectService="";
			int k=0;
			for(int i=0;projectServiceList!=null && i<projectServiceList.size();i++) {
				for(int j=0;j<getF_project_service().length;j++) {
					if(getF_project_service()[j].equals(projectServiceList.get(i).getServiceId())) {
						if(k==0) {
							strProjectService=projectServiceList.get(i).getServiceName();
						} else {
							strProjectService+=", "+projectServiceList.get(i).getServiceName();
						}
						k++;
					}
				}
			}
			if(strProjectService!=null && !strProjectService.equals("")) {
				hmFilter.put("PROJECT_SERVICE", strProjectService);
			} else {
				hmFilter.put("PROJECT_SERVICE", "All Services");
			}
		} else {
			hmFilter.put("PROJECT_SERVICE", "All Services");
		}
		
		alFilter.add("CLIENT");
		if(getF_client()!=null) {
			String strClient="";
			int k=0;
			for(int i=0; clientList!=null && i<clientList.size();i++) {
				for(int j=0;j<getF_client().length;j++) {
					if(getF_client()[j].equals(clientList.get(i).getClientId())) {
						if(k==0) {
							strClient=clientList.get(i).getClientName();
						} else {
							strClient+=", "+clientList.get(i).getClientName();
						}
						k++;
					}
				}
			}
			if(strClient!=null && !strClient.equals("")) {
				hmFilter.put("CLIENT", strClient);
			} else {
				hmFilter.put("CLIENT", "All Clients");
			}
		} else {
			hmFilter.put("CLIENT", "All Clients");
		}
		
		
			
		if(getSelectOne()!= null && !getSelectOne().equals("")) {
			alFilter.add("PERIOD");
			
			String strSelectOne="";
			if(uF.parseToInt(getSelectOne()) == 1) {
				strSelectOne="From - To";
			} else if(uF.parseToInt(getSelectOne()) == 2) {
				strSelectOne="Financial Year";
			} else if(uF.parseToInt(getSelectOne()) == 3) {
				strSelectOne="Month";
			} else if(uF.parseToInt(getSelectOne()) == 4) {
				strSelectOne="Paycycle";
			}
			if(strSelectOne!=null && !strSelectOne.equals("")) {
				hmFilter.put("PERIOD", strSelectOne);
			}
			
		}
		
		if(uF.parseToInt(getSelectOne()) == 1) {
			alFilter.add("FROMTO");
			String strtDate = "";
			String endDate = "";
			if(getStrStartDate() != null && !getStrStartDate().equalsIgnoreCase("") && !getStrStartDate().equals("")) {
				strtDate = uF.getDateFormat(getStrStartDate(), DATE_FORMAT, CF.getStrReportDateFormat());
			}
			if(getStrEndDate() != null && !getStrEndDate().equalsIgnoreCase("") && !getStrEndDate().equals("")) {
				endDate = uF.getDateFormat(getStrEndDate(), DATE_FORMAT, CF.getStrReportDateFormat());
			}
			hmFilter.put("FROMTO", strtDate +" - "+ endDate);
		} else if(uF.parseToInt(getSelectOne()) == 2) {
			alFilter.add("FINANCIALYEAR");
			String[] strFinancialYears = null;
			if (getFinancialYear() != null) {
				strFinancialYears = getFinancialYear().split("-");
				setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
				
				setStrStartDate(strFinancialYears[0]);
				setStrEndDate(strFinancialYears[1]);
			} else {
				strFinancialYears = CF.getFinancialYear(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), CF, uF);
				setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
				
				setStrStartDate(strFinancialYears[0]);
				setStrEndDate(strFinancialYears[1]);
			}
			hmFilter.put("FINANCIALYEAR", uF.getDateFormat(getStrStartDate(), DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, CF.getStrReportDateFormat()));
		} else if(uF.parseToInt(getSelectOne()) == 3) {
			alFilter.add("MONTH");
			int nselectedMonth = uF.parseToInt(getStrMonth());
			String strMonth = uF.getMonth(nselectedMonth);
//			int nFYSMonth = uF.parseToInt(uF.getDateFormat(CF.getStrFinancialYearFrom(), DATE_FORMAT, "MM"));
			int nFYSMonth = 0;
			int nFYSDay = 0;
			String[] strFinancialYears = null;
//			System.out.println("getMonthFinancialYear() ===>> " + getMonthFinancialYear());
			if (getMonthFinancialYear() != null) {
				strFinancialYears = getMonthFinancialYear().split("-");
				setMonthFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
				nFYSMonth = uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "MM"));
				nFYSDay = uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "dd"));
			} else {
				strFinancialYears = CF.getFinancialYear(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), CF, uF);
				setMonthFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
				nFYSMonth = uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "MM"));
				nFYSDay = uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "dd"));
			}
			
			Calendar cal = GregorianCalendar.getInstance();
			cal.set(Calendar.MONTH, uF.parseToInt(getStrMonth())-1);
			cal.set(Calendar.DATE, nFYSDay);
			if(nselectedMonth>=nFYSMonth){
				cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "yyyy")));
			}else{
				cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYears[1], DATE_FORMAT, "yyyy")));
			}
			
			int nMonthStart = cal.getActualMinimum(Calendar.DATE);
			int nMonthEnd = cal.getActualMaximum(Calendar.DATE);
			
			setStrStartDate(nMonthStart+"/"+getStrMonth()+"/"+cal.get(Calendar.YEAR));
			setStrEndDate(nMonthEnd+"/"+getStrMonth()+"/"+cal.get(Calendar.YEAR));
			
			hmFilter.put("MONTH", uF.getDateFormat(getStrStartDate(), DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, CF.getStrReportDateFormat()) + " - " + strMonth);
		} else if(uF.parseToInt(getSelectOne()) == 4) {
			alFilter.add("PAYCYCLE");
			String strPaycycle = "";
			String[] strPayCycleDates = null;
			if (getPaycycle() != null) {
				strPayCycleDates = getPaycycle().split("-");
				setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
				
				strPaycycle = "Pay Cycle "+ strPayCycleDates[2]+", ";
				setStrStartDate(strPayCycleDates[0]);
				setStrEndDate(strPayCycleDates[1]);
			} else {
				strPayCycleDates = CF.getCurrentPayCycleByOrg(CF.getStrTimeZone(), CF, getF_org(),request);
				setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
				
				strPaycycle = "Pay Cycle "+ strPayCycleDates[2]+", ";
				setStrStartDate(strPayCycleDates[0]);
				setStrEndDate(strPayCycleDates[1]);
			}
			hmFilter.put("PAYCYCLE", strPaycycle + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, CF.getStrReportDateFormat()));
		}
		
		String selectedFilter=CF.getSelectedFilter1(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}

	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public String[] getF_strWLocation() {
		return f_strWLocation;
	}

	public void setF_strWLocation(String[] f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
	}

	public String[] getF_department() {
		return f_department;
	}

	public void setF_department(String[] f_department) {
		this.f_department = f_department;
	}

	public String[] getF_service() {
		return f_service;
	}

	public void setF_service(String[] f_service) {
		this.f_service = f_service;
	}

	public String[] getF_level() {
		return f_level;
	}

	public void setF_level(String[] f_level) {
		this.f_level = f_level;
	}

	public String[] getF_project_service() {
		return f_project_service;
	}

	public void setF_project_service(String[] f_project_service) {
		this.f_project_service = f_project_service;
	}

	public String[] getF_client() {
		return f_client;
	}

	public void setF_client(String[] f_client) {
		this.f_client = f_client;
	}

	public String getSelectOne() {
		return selectOne;
	}

	public void setSelectOne(String selectOne) {
		this.selectOne = selectOne;
	}

	public String getStrStartDate() {
		return strStartDate;
	}

	public void setStrStartDate(String strStartDate) {
		this.strStartDate = strStartDate;
	}

	public String getStrEndDate() {
		return strEndDate;
	}

	public void setStrEndDate(String strEndDate) {
		this.strEndDate = strEndDate;
	}

	public String getFinancialYear() {
		return financialYear;
	}

	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}

	public String getPaycycle() {
		return paycycle;
	}

	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
	}

	public String getStrMonth() {
		return strMonth;
	}

	public void setStrMonth(String strMonth) {
		this.strMonth = strMonth;
	}

	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}

	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}

	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}

	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}

	public List<FillDepartment> getDepartmentList() {
		return departmentList;
	}

	public void setDepartmentList(List<FillDepartment> departmentList) {
		this.departmentList = departmentList;
	}

	public List<FillServices> getServiceList() {
		return serviceList;
	}

	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
	}

	public List<FillServices> getProjectServiceList() {
		return projectServiceList;
	}

	public void setProjectServiceList(List<FillServices> projectServiceList) {
		this.projectServiceList = projectServiceList;
	}

	public List<FillLevel> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}

	public List<FillFinancialYears> getFinancialYearList() {
		return financialYearList;
	}

	public void setFinancialYearList(List<FillFinancialYears> financialYearList) {
		this.financialYearList = financialYearList;
	}

	public List<FillMonth> getMonthList() {
		return monthList;
	}

	public void setMonthList(List<FillMonth> monthList) {
		this.monthList = monthList;
	}

	public List<FillPayCycles> getPaycycleList() {
		return paycycleList;
	}

	public void setPaycycleList(List<FillPayCycles> paycycleList) {
		this.paycycleList = paycycleList;
	}

	public List<FillClients> getClientList() {
		return clientList;
	}

	public void setClientList(List<FillClients> clientList) {
		this.clientList = clientList;
	}

	public String getStrType() {
		return strType;
	}

	public void setStrType(String strType) {
		this.strType = strType;
	}

	public String getMonthFinancialYear() {
		return monthFinancialYear;
	}

	public void setMonthFinancialYear(String monthFinancialYear) {
		this.monthFinancialYear = monthFinancialYear;
	}

}
