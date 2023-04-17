package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillClients;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class BillingRegister extends ActionSupport implements ServletRequestAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	
	CommonFunctions CF;
	HttpSession session;
	String strEmpId;
	String strUserType;
	 
//	String paycycle;
//	List<FillPayCycles> payCycleList; 
//	List<FillProjectList> projectList;
//	List<FillWLocation> wLocationList;
//	String strProject;
//	String f_strWLocation;
	
//	String f_start;
//	String f_end;
	
	String f_org;
	String[] f_strWLocation; 
	String[] f_department;
	String[] f_client;
	
	String strStartDate;
	String strEndDate;
	
	List<FillOrganisation> organisationList;
	List<FillWLocation> wLocationList;
	List<FillDepartment> departmentList;
	List<FillClients> clientList;
 
	String strProType;
	boolean poFlag;
	
	public String execute() throws Exception {
		UtilityFunctions uF=new UtilityFunctions();
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;

		request.setAttribute(PAGE, "/jsp/task/BillingRegister.jsp");
		request.setAttribute(TITLE, "Billing Register");
		strEmpId =(String) session.getAttribute(EMPID);
		strUserType =(String) session.getAttribute(BASEUSERTYPE);
		
		boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}
		if(uF.parseToInt(getF_org()) == 0){
			setF_org((String)session.getAttribute(ORGID));
		}
		checkProjectOwner(uF);
//		System.out.println("BR/84--");
		
		getProjectBillingDetails(uF);
		
		return loadProjectBillingReport(uF); 

	}
	
	private String loadProjectBillingReport(UtilityFunctions uF) {
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		} else {
			organisationList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		clientList = new FillClients(request).fillAllClients(false);
		getSelectedFilter(uF);
		
		return SUCCESS;
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
		
		String selectedFilter=CF.getSelectedFilter1(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
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
			/*sbQuery.append("select * from projectmntnc pmc where project_owner=?");*/
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
			
			if(poFlag && uF.parseToInt(getStrProType()) == 0){
				setStrProType("2");
			}
		} catch (Exception e) {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public void getProjectBillingDetails(UtilityFunctions uF){
		
		Database db = new Database();
		db.setRequest(request);
		Connection con=null;
		PreparedStatement pst=null;
		ResultSet rs=null;
		
		try {
			
			con = db.makeConnection(con);
			Map<String, Map<String, String>> hmCurrencyMap = CF.getCurrencyDetails(con);
			Map<String, String> hmClientDetails = CF.getProjectClientMap(con, uF);
			Map<String, String> hmProjectName = CF.getProjectNameMap(con);
				
//			StringBuilder sbQuery = new StringBuilder();
//			sbQuery.append("select * from projectmntnc pmc, client_poc cpoc, client_details cd where pmc.client_id = cd.client_id and pmc.poc=cpoc.poc_id");
//			pst = con.prepareStatement(sbQuery.toString());
//			rs = pst.executeQuery();
//			Map<String, String> hmClientDetails = new HashMap<String, String>();
//			while(rs.next()){
//				hmClientDetails.put(rs.getString("pro_id"), rs.getString("client_name"));
//			}
//			rs.close();
//			pst.close();
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select pid.promntc_invoice_id,pid.client_id,pid.pro_id,pro_name,billing_type,billing_kind,invoice_code," +
					"invoice_generated_date,oc_invoice_amount,invoice_type,is_cancel,invoice_template_type,pid.curr_id " +
					"from promntc_invoice_details pid, projectmntnc p where p.pro_id = pid.pro_id ");
			
			if(isPoFlag() && uF.parseToInt(getStrProType()) == 2){
		//===start parvez date: 12-10-2022===		
//				sbQuery.append(" and p.project_owner="+uF.parseToInt(strEmpId));
				sbQuery.append(" and p.project_owners like '%,"+strEmpId+",%' ");
		//===end parvez date: 12-10-2022===		
			}
			
			if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)) {
				sbQuery.append(" and pid.invoice_generated_by = "+ uF.parseToInt(strEmpId) +" ");
			}
			/*if(uF.parseToInt(getF_org())>0 || (getF_strWLocation() != null && getF_strWLocation().length>0) || (getF_department() != null && getF_department().length>0)) {
				sbQuery.append(" and pid.pro_id in (select pro_id from projectmntnc where pro_id>0 ");
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

			if(getF_client() != null && getF_client().length>0) {
				sbQuery.append(" and p.client_id in ("+StringUtils.join(getF_client(), ",")+") ");
			}
			/*if(uF.parseToInt(getF_org())>0 || (getF_strWLocation() != null && getF_strWLocation().length>0) || (getF_department() != null && getF_department().length>0)) {
				sbQuery.append(" ) ");
			}*/
			if(getStrStartDate() != null && !getStrStartDate().equalsIgnoreCase("null") && !getStrStartDate().equals("") && getStrEndDate() != null && !getStrEndDate().equalsIgnoreCase("null") && !getStrEndDate().equals("")) {
				sbQuery.append(" and invoice_generated_date between '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE)+"' and '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"' ");
			}
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst====="+pst);
			rs = pst.executeQuery();
			List<List<String>> alReport = new ArrayList<List<String>>();
			while(rs.next()){
				List<String> alInner = new ArrayList<String>();
				Map<String, String> hmCurr = hmCurrencyMap.get(rs.getString("curr_id"));
				
				alInner.add(hmClientDetails.get(rs.getString("client_id"))); 
				alInner.add(rs.getString("pro_name"));
				alInner.add(CF.getBillinType(rs.getString("billing_type")));
				alInner.add(CF.getBillinFreq(rs.getString("billing_kind"), rs.getString("billing_type")));
				alInner.add(rs.getBoolean("is_cancel") ? "Cancelled" : "");
				alInner.add(rs.getString("invoice_code"));
				alInner.add(uF.getDateFormat(rs.getString("invoice_generated_date"), DBDATE, DATE_FORMAT));
				alInner.add(hmCurr.get("SHORT_CURR")+" "+rs.getString("oc_invoice_amount"));
				
				if(rs.getString("invoice_type") != null && rs.getString("invoice_type").equals(""+ADHOC_PRORETA_INVOICE) && uF.parseToInt(rs.getString("invoice_template_type")) > 0) {
					alInner.add("<a href=\"GenerateInvoicePdfFormatOne.action?operation=pdfDwld&type="+ADHOC_PRORETA_INVOICE+"&pro_id="+rs.getString("pro_id")+"&invoice_id="+rs.getString("promntc_invoice_id")+"\"><i class=\"fa fa-file-pdf-o\" aria-hidden=\"true\"></i></a>");
				} else if(rs.getString("invoice_type") != null && rs.getString("invoice_type").equals(""+ADHOC_PRORETA_INVOICE) && uF.parseToInt(rs.getString("invoice_template_type")) == 0) {
					alInner.add("<a href=\"GenerateProjectInvoice.action?operation=pdfDwld&type="+ADHOC_PRORETA_INVOICE+"&pro_id="+rs.getString("pro_id")+"&invoice_id="+rs.getString("promntc_invoice_id")+"\"><i class=\"fa fa-file-pdf-o\" aria-hidden=\"true\"></i></a>");
				} else if(rs.getString("invoice_type") != null && rs.getString("invoice_type").equals(""+ADHOC_INVOICE) && uF.parseToInt(rs.getString("invoice_template_type")) > 0) {
					alInner.add("<a href=\"GenerateInvoicePdfFormatOne.action?operation=pdfDwld&type="+ADHOC_INVOICE+"&pro_id="+rs.getString("pro_id")+"&invoice_id="+rs.getString("promntc_invoice_id")+"\"><i class=\"fa fa-file-pdf-o\" aria-hidden=\"true\"></i></a>");
				} else if(rs.getString("invoice_type") != null && rs.getString("invoice_type").equals(""+ADHOC_INVOICE) && uF.parseToInt(rs.getString("invoice_template_type")) == 0) {
					alInner.add("<a href=\"GenerateProjectInvoice.action?operation=pdfDwld&type="+ADHOC_INVOICE+"&pro_id="+rs.getString("pro_id")+"&invoice_id="+rs.getString("promntc_invoice_id")+"\"><i class=\"fa fa-file-pdf-o\" aria-hidden=\"true\"></i></a>");
				} else if((CF.getBillinType(rs.getString("billing_type")).equals("Daily") ||CF.getBillinType(rs.getString("billing_type")).equals("Hourly")) && uF.parseToInt(rs.getString("invoice_template_type")) > 0) {
					alInner.add("<a href=\"GenerateInvoicePdfFormatOne.action?operation=pdfDwld&type="+PRORETA_INVOICE+"&pro_id="+rs.getString("pro_id")+"&invoice_id="+rs.getString("promntc_invoice_id")+"\"><i class=\"fa fa-file-pdf-o\" aria-hidden=\"true\"></i></a>");
				} else if((CF.getBillinType(rs.getString("billing_type")).equals("Daily") ||CF.getBillinType(rs.getString("billing_type")).equals("Hourly")) && uF.parseToInt(rs.getString("invoice_template_type")) == 0) {
					alInner.add("<a href=\"GenerateProjectInvoice.action?operation=pdfDwld&type="+PRORETA_INVOICE+"&pro_id="+rs.getString("pro_id")+"&invoice_id="+rs.getString("promntc_invoice_id")+"\"><i class=\"fa fa-file-pdf-o\" aria-hidden=\"true\"></i></a>");
				} else if(uF.parseToInt(rs.getString("invoice_template_type")) > 0) {
					alInner.add("<a href=\"GenerateInvoicePdfFormatOne.action?operation=pdfDwld&pro_id="+rs.getString("pro_id")+"&invoice_id="+rs.getString("promntc_invoice_id")+"\"><i class=\"fa fa-file-pdf-o\" aria-hidden=\"true\"></i></a>");
				} else {
					alInner.add("<a href=\"GenerateProjectInvoice.action?operation=pdfDwld&pro_id="+rs.getString("pro_id")+"&invoice_id="+rs.getString("promntc_invoice_id")+"\"><i class=\"fa fa-file-pdf-o\" aria-hidden=\"true\"></i></a>");
				}
				alReport.add(alInner);				
			}
			rs.close();
			pst.close();
			
			
			sbQuery = new StringBuilder();
			sbQuery.append("select promntc_invoice_id,client_id,pro_id,invoice_code,invoice_generated_date,oc_invoice_amount,invoice_type,is_cancel," +
					"curr_id,invoice_template_id from promntc_invoice_details where pro_id = 0 and (invoice_type = "+ADHOC_INVOICE+" or" +
					" invoice_type = "+ADHOC_PRORETA_INVOICE+" )");
			
			if((strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)) || (isPoFlag() && uF.parseToInt(getStrProType()) == 2)) {
				sbQuery.append(" and invoice_generated_by = "+ uF.parseToInt(strEmpId) +" ");
			}
			
			if(uF.parseToInt(getF_org())>0 && (getF_strWLocation()==null || getF_strWLocation().length == 0)) {
				String loctionIds = CF.getOrgLocationIds(con, uF, getF_org());
				if(loctionIds != null && loctionIds.length()>0) {
					sbQuery.append(" and wlocation_id in ("+loctionIds+")");
				}
			} else if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
	            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+")");
	        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			
			if(getF_department() != null && getF_department().length>0) {
				sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
			}
			
			if(getF_client() != null && getF_client().length>0) {
				sbQuery.append(" and client_id in ("+StringUtils.join(getF_client(), ",")+") ");
			}
			
			/*if(uF.parseToInt(getF_org())>0 || (getF_strWLocation() != null && getF_strWLocation().length>0) || (getF_department() != null && getF_department().length>0)) {
				sbQuery.append(" ) ");
			}*/
			
			if(getStrStartDate() != null && !getStrStartDate().equalsIgnoreCase("null") && !getStrStartDate().equals("") && getStrEndDate() != null && !getStrEndDate().equalsIgnoreCase("null") && !getStrEndDate().equals("")) {
				sbQuery.append(" and invoice_generated_date between '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE)+"' and '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"' ");
			}
			
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst====="+pst);
			rs = pst.executeQuery();
			while(rs.next()){
				List<String> alInner = new ArrayList<String>();
				Map<String, String> hmCurr = hmCurrencyMap.get(rs.getString("curr_id"));
				
				alInner.add(hmClientDetails.get(rs.getString("client_id")));
				alInner.add(uF.showData(hmProjectName.get(rs.getString("pro_id")), ""));
				alInner.add("AdHoc");
				alInner.add("AdHoc");
				alInner.add(rs.getBoolean("is_cancel") ? "Cancelled" : "");
				alInner.add(rs.getString("invoice_code"));
				alInner.add(uF.getDateFormat(rs.getString("invoice_generated_date"), DBDATE, DATE_FORMAT));
				alInner.add(hmCurr.get("SHORT_CURR")+" "+rs.getString("oc_invoice_amount"));
				
				if(rs.getString("invoice_type") != null && rs.getString("invoice_type").equals(""+ADHOC_PRORETA_INVOICE) && uF.parseToInt(rs.getString("invoice_template_id")) > 0) {
					alInner.add("<a href=\"GenerateInvoicePdfFormatOne.action?operation=pdfDwld&type="+ADHOC_PRORETA_INVOICE+"&pro_id="+rs.getString("pro_id")+"&invoice_id="+rs.getString("promntc_invoice_id")+"\"><i class=\"fa fa-file-pdf-o\" aria-hidden=\"true\"></i></a>");
				} else if(rs.getString("invoice_type") != null && rs.getString("invoice_type").equals(""+ADHOC_PRORETA_INVOICE) && uF.parseToInt(rs.getString("invoice_template_id")) == 0) {
					alInner.add("<a href=\"GenerateProjectInvoice.action?operation=pdfDwld&type="+ADHOC_PRORETA_INVOICE+"&pro_id="+rs.getString("pro_id")+"&invoice_id="+rs.getString("promntc_invoice_id")+"\"><i class=\"fa fa-file-pdf-o\" aria-hidden=\"true\"></i></a>");
				} else if(rs.getString("invoice_type") != null && rs.getString("invoice_type").equals(""+ADHOC_INVOICE) && uF.parseToInt(rs.getString("invoice_template_id")) > 0) {
					alInner.add("<a href=\"GenerateInvoicePdfFormatOne.action?operation=pdfDwld&type="+ADHOC_INVOICE+"&pro_id="+rs.getString("pro_id")+"&invoice_id="+rs.getString("promntc_invoice_id")+"\"><i class=\"fa fa-file-pdf-o\" aria-hidden=\"true\"></i></a>");
				} else if(rs.getString("invoice_type") != null && rs.getString("invoice_type").equals(""+ADHOC_INVOICE) && uF.parseToInt(rs.getString("invoice_template_id")) == 0) {
					alInner.add("<a href=\"GenerateProjectInvoice.action?operation=pdfDwld&type="+ADHOC_INVOICE+"&pro_id="+rs.getString("pro_id")+"&invoice_id="+rs.getString("promntc_invoice_id")+"\"><i class=\"fa fa-file-pdf-o\" aria-hidden=\"true\"></i></a>");
				}
				alReport.add(alInner);				
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alReport", alReport);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
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

	public String[] getF_client() {
		return f_client;
	}

	public void setF_client(String[] f_client) {
		this.f_client = f_client;
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

}