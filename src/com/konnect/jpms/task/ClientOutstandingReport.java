package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

public class ClientOutstandingReport extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	
	HttpSession session;
	CommonFunctions CF; 
	String strEmpId; 
	String strUserType;
	
	String f_org;
	String[] f_strWLocation; 
	String[] f_department; 
	String[] f_service;
//	String[] f_level;
	String[] f_project_service;
	String[] f_client;
	
	String selectOne;
	String strStartDate;
	String strEndDate;
	String financialYear; 
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
	
	String strProType;
	boolean poFlag;
	
	public String execute() throws Exception {
		session = request.getSession();
		
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;

		UtilityFunctions uF = new UtilityFunctions();
		request.setAttribute(PAGE, "/jsp/task/ClientOutstandingReport.jsp");
		request.setAttribute(TITLE, "Client Outstanding Report");
		strEmpId =(String) session.getAttribute(EMPID);
		strUserType =(String) session.getAttribute(BASEUSERTYPE);
		
//		boolean isView  = CF.getAccess(session, request, uF);
//		if(!isView) {
//			request.setAttribute(PAGE, PAccessDenied);
//			request.setAttribute(TITLE, TAccessDenied);
//			return ACCESS_DENIED;
//		}
		
//		orgList = new FillOrganisation(request).fillOrganisation();
		if(getF_org() == null) {
			setF_org((String)session.getAttribute(ORGID));
		}
		
		checkProjectOwner(uF);
		
		loadClientOutstandingReport(uF);
		getClientOutstandingReport(uF);
		
		return SUCCESS; 

	}
	
	
	public String loadClientOutstandingReport(UtilityFunctions uF) {
		if((getStrStartDate() == null || getStrStartDate().equals("") || getStrStartDate().equalsIgnoreCase("null")) && (getStrEndDate() == null || getStrEndDate().equals("") || getStrEndDate().equalsIgnoreCase("null"))) {
			String currDate = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT);
			String minMaxDate = uF.getCurrentMonthMinMaxDate(currDate, DATE_FORMAT);
			String[] tmpDate = minMaxDate.split("::::");
			setStrStartDate(tmpDate[0]);
			setStrEndDate(tmpDate[1]);
		}
		
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
		clientList = new FillClients(request).fillClients(false);
		getSelectedFilter(uF);
		
		return SUCCESS;
	}
	

	private void checkProjectOwner(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean poFlag = false;
		try{
			con = db.makeConnection(con);
			
			StringBuilder sbQuery = new StringBuilder();
		//===start parvez date: 12-10-2022===	
//			sbQuery.append("select * from projectmntnc pmc where project_owner = ?");
			sbQuery.append("select * from projectmntnc pmc where project_owners like '%,"+strEmpId+",%' ");
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setInt(1, uF.parseToInt(strEmpId));
		//===end parvez date: 12-10-2022===	
			rs = pst.executeQuery();
			if(rs.next()) {
				poFlag = true;
			}
			rs.close();
			pst.close();
			
			setPoFlag(poFlag);
			
			if(poFlag && uF.parseToInt(getStrProType()) == 0) {
				setStrProType("2");
			}
		} catch (Exception e) {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private void getClientOutstandingReport(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		List<List<String>> alOuter = new ArrayList<List<String>>();
		
		PreparedStatement pst = null;
		ResultSet rs  = null;
		
		try {
			con = db.makeConnection(con);
			
			Map<String, Map<String, String>> hmCurrencyMap = CF.getCurrencyDetails(con);
			
			Map<String, Map<String, String>> hmWorkLocation = CF.getWorkLocationMap(con);
			if(hmWorkLocation == null) hmWorkLocation = new HashMap<String, Map<String,String>>();
			
			Map<String, String> hmDept = CF.getDeptMap(con);
			if(hmDept==null)hmDept = new HashMap<String, String>();
			
//			Map hmCurrMap = CF.getCurrencyDetails(con);
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select sum(received_amount) / (sum(exchange_rate) / count(exchange_rate)) as received_amount, " +
				"pa.pro_id from promntc_bill_amt_details pa where pro_id > 0 ");
			if(getStrStartDate() != null && !getStrStartDate().equalsIgnoreCase("null") && !getStrStartDate().equals("") && getStrEndDate() != null && !getStrEndDate().equalsIgnoreCase("null") && !getStrEndDate().equals("")) {
				sbQuery.append(" and entry_date between '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "' ");
			}
			sbQuery.append(" group by pa.pro_id ");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
//			System.out.println("pst==>>"+pst);
			Map<String, String> hmAmountReceived = new HashMap<String, String>();
			while(rs.next()) {
				hmAmountReceived.put(rs.getString("pro_id"), uF.formatIntoOneDecimalWithOutComma(rs.getDouble("received_amount")));
			}
			rs.close();
			pst.close();
			
			sbQuery = new StringBuilder();
			sbQuery.append("select sum(oc_invoice_amount) as invoice_amount, pro_id from promntc_invoice_details where pro_id > 0 ");
			if(getStrStartDate() != null && !getStrStartDate().equalsIgnoreCase("null") && !getStrStartDate().equals("") && getStrEndDate() != null && !getStrEndDate().equalsIgnoreCase("null") && !getStrEndDate().equals("")) {
				sbQuery.append(" and invoice_generated_date between '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "' ");
			}
			sbQuery.append(" group by pro_id ");
			pst = con.prepareStatement(sbQuery.toString());			
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			Map<String, String> hmProOutstandingAmount = new HashMap<String, String>();
//			int count=0;
			
			Map<String, String> hmInvoiceAmount = new HashMap<String, String>();
			
			while(rs.next()) {
				double dblAmt = uF.parseToDouble(rs.getString("invoice_amount")) - uF.parseToDouble(hmAmountReceived.get(rs.getString("pro_id")));
				hmProOutstandingAmount.put(rs.getString("pro_id"), ""+uF.formatIntoOneDecimalWithOutComma(dblAmt)); 
				hmInvoiceAmount.put(rs.getString("pro_id"), uF.formatIntoOneDecimalWithOutComma(rs.getDouble("invoice_amount")));
			}
			rs.close();
			pst.close();
//			System.out.println("invoice amount--"+hmInvoiceAmount);
//			System.out.println("hmProOutstandingAmount--"+hmProOutstandingAmount);
			sbQuery = new StringBuilder();
			sbQuery.append("select sum(billing_amount) as billing_amount, p.pro_id from projectmntnc p, projectmntnc_frequency pf where " +
					" billing_type ='F' and p.pro_id = pf.pro_id ");
			if(getStrStartDate() != null && !getStrStartDate().equalsIgnoreCase("null") && !getStrStartDate().equals("") && getStrEndDate() != null && !getStrEndDate().equalsIgnoreCase("null") && !getStrEndDate().equals("")) {
				sbQuery.append(" and ((freq_start_date <= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and freq_end_date >= '"+ uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "') ");
				sbQuery.append(" or (freq_start_date >= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and freq_start_date <= '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "') ");
				sbQuery.append(" or (freq_start_date >= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and freq_end_date <= '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "') ");
				sbQuery.append(" or (freq_start_date <= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and freq_end_date >= '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "') ");
				sbQuery.append(" or (freq_end_date >= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and freq_end_date <= '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "')) ");
			}
			sbQuery.append(" group by p.pro_id ");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
//			System.out.println("pst==//>>"+pst);
			Map<String, String> hmBillingAmount = new HashMap<String, String>();
			Map<String, String> hmProNonOutstandingAmount = new HashMap<String, String>();

			while(rs.next()) {
				hmBillingAmount.put(rs.getString("pro_id"), uF.formatIntoOneDecimalWithOutComma(rs.getDouble("billing_amount")));
				double dblAmt = uF.parseToDouble(rs.getString("billing_amount")) - uF.parseToDouble(hmInvoiceAmount.get(rs.getString("pro_id")));
				hmProNonOutstandingAmount.put(rs.getString("pro_id"), ""+uF.formatIntoOneDecimalWithOutComma(dblAmt)); 
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmBillingAmount--"+hmBillingAmount);
//			System.out.println("hmProNonOutstandingAmount-"+hmProNonOutstandingAmount);
			
			sbQuery = new StringBuilder();
			sbQuery.append("select pro_id, p.client_id, p.curr_id, p.wlocation_id, p.department_id, pro_name, client_name " +
				" from client_details cd, projectmntnc p where p.client_id = cd.client_id ");
			if(isPoFlag() && uF.parseToInt(getStrProType()) == 2) {
			//===start parvez date: 12-10-2022===	
//				sbQuery.append(" and p.project_owner="+uF.parseToInt(strEmpId));
				sbQuery.append(" and p.project_owners like '%,"+strEmpId+",%' ");
			//===end parvez date: 12-10-2022===	
			}     
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
			
			if(getStrStartDate() != null && !getStrStartDate().equalsIgnoreCase("null") && !getStrStartDate().equals("") && getStrEndDate() != null && !getStrEndDate().equalsIgnoreCase("null") && !getStrEndDate().equals("")) {
				sbQuery.append(" and ((start_date <= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and deadline >= '"+ uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "') ");
				sbQuery.append(" or (start_date >= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and start_date <= '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "') ");
				sbQuery.append(" or (start_date >= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and deadline <= '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "') ");
				sbQuery.append(" or (start_date <= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and deadline >= '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "') ");
				sbQuery.append(" or (deadline >= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and deadline <= '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "')) ");
			}
			
			sbQuery.append(" order by client_name, pro_id desc ");
			
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			List<String> alInner = new ArrayList<String>();
//			int count=0;
			while(rs.next()) {
//				count++;
//				System.out.println("rs.getStrin--"+rs.getString("pro_id"));
				alInner = new ArrayList<String>();
				Map<String, String> hmCurr = hmCurrencyMap.get(rs.getString("curr_id"));
				
				alInner.add(rs.getString("client_name")); //1
				alInner.add(rs.getString("pro_name")); //2
				alInner.add(uF.showData(hmDept.get(rs.getString("department_id")), "") ); //3
				Map<String, String> hm =hmWorkLocation.get(rs.getString("wlocation_id"));
				if(hm==null)hm=new HashMap<String, String>();
				alInner.add(uF.showData(hm.get("WL_NAME"), "")); //4
				alInner.add(uF.getDateFormat(getInvoiceDate(con, rs.getString("pro_id"), uF), DBDATE, CF.getStrReportDateFormat())); //5
				alInner.add(hmCurr.get("SHORT_CURR")+" "+uF.showData(hmProOutstandingAmount.get(rs.getString("pro_id")), "0"));//6
				alInner.add(hmCurr.get("SHORT_CURR")+" "+uF.showData(hmProNonOutstandingAmount.get(rs.getString("pro_id")), "0"));//7
				
				alOuter.add(alInner);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alOuter", alOuter);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	public String getInvoiceDate(Connection con, String proID,UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		String invoicedate=null;
		try {
			// 
			pst = con.prepareStatement("select invoice_generated_date from promntc_invoice_details where pro_id=? order by promntc_Invoice_id desc Limit 1");
			pst.setInt(1,uF.parseToInt(proID));
			rs = pst.executeQuery();
			while (rs.next()) {
					invoicedate=rs.getString("invoice_generated_date");
			}
//			System.out.println("pst--"+pst);
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		return invoicedate;
	}

 
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();

		if(isPoFlag()){
			alFilter.add("PROJECT_TYPE");
			if(getStrProType()!=null) {
				String strProType="";
				if(isPoFlag() && uF.parseToInt(getStrProType()) == 2){
					strProType = "My Projects";
				}
				if(strProType!=null && !strProType.equals("")) {
					hmFilter.put("PROJECT_TYPE", strProType);
				} else {
					hmFilter.put("PROJECT_TYPE", "All Projects");
				}
			} else {
				hmFilter.put("PROJECT_TYPE", "All Projects");
			}
		}
		
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
		
		
		alFilter.add("FROM_TO");
		String strFdt = "-";
		String strEdt = "-";
		if(getStrStartDate() != null && !getStrStartDate().equals("From Date") && !getStrStartDate().equalsIgnoreCase("null")) {
			strFdt = uF.getDateFormat(getStrStartDate(), DATE_FORMAT, CF.getStrReportDateFormat());
		}
		if(getStrEndDate() != null && !getStrEndDate().equals("To Date") && !getStrEndDate().equalsIgnoreCase("null")) {
			strEdt = uF.getDateFormat(getStrEndDate(), DATE_FORMAT, CF.getStrReportDateFormat());
		}
		hmFilter.put("FROM_TO",  strFdt+" - "+ strEdt);
		
		String selectedFilter=CF.getSelectedFilter1(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
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

	public String[] getF_strWLocation() {
		return f_strWLocation;
	}

	public void setF_strWLocation(String[] f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
	}

	public String[] getF_service() {
		return f_service;
	}

	public void setF_service(String[] f_service) {
		this.f_service = f_service;
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

	public String[] getF_department() {
		return f_department;
	}

	public List<FillServices> getServiceList() {
		return serviceList;
	}

	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
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

	public void setF_department(String[] f_department) {
		this.f_department = f_department;
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

	public List<FillServices> getProjectServiceList() {
		return projectServiceList;
	}

	public void setProjectServiceList(List<FillServices> projectServiceList) {
		this.projectServiceList = projectServiceList;
	}

	public List<FillClients> getClientList() {
		return clientList;
	}

	public void setClientList(List<FillClients> clientList) {
		this.clientList = clientList;
	}

	public String getStrProType() {
		return strProType;
	}

	public void setStrProType(String strProType) {
		this.strProType = strProType;
	}

	public boolean isPoFlag() {
		return poFlag;
	}

	public void setPoFlag(boolean poFlag) {
		this.poFlag = poFlag;
	}

	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	
		
}