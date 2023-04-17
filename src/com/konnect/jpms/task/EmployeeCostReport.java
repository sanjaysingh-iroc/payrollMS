package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillClients;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillProject;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class EmployeeCostReport extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	HttpSession session; 
	CommonFunctions CF;
	String strEmpId;
	String strUserType; 
	
	String f_org;
	String strStartDate;
	String strEndDate;
	
	String[] f_client;
	String[] f_project; 
	String[] f_strWLocation; 
	String[] f_department;
	String[] f_service;
	String[] f_project_service;
	
	
	List<FillOrganisation> organisationList;
	List<FillClients> clientList;
	List<FillWLocation> wLocationList;
	List<FillDepartment> departmentList;
	List<FillServices> serviceList;
	List<FillServices> projectServiceList;
	
	List<FillProject> projectList;
	
	HttpServletRequest request;
	

	public String execute() throws Exception {
		session = request.getSession();
		
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;

		UtilityFunctions uF = new UtilityFunctions();
//		request.setAttribute(PAGE, "/jsp/task/EmployeeCostReport.jsp");
//		request.setAttribute(TITLE, "Employee Cost Report");
		
		strEmpId =(String) session.getAttribute(EMPID);
		strUserType =(String) session.getAttribute(BASEUSERTYPE);
		
		loadOutstandingReport(uF);
		getEmployeeCostReport(uF);
		return "load";
	}
	
	
	public void getEmployeeCostReport(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		List<List<String>> alOuter = new ArrayList<List<String>>();
		PreparedStatement pst = null;
		ResultSet rs  = null;
		try {
			con = db.makeConnection(con);
			StringBuilder sbQuery = new StringBuilder();
			
			sbQuery.append("select cd.client_id, cd.client_name, p.pro_id, p.org_id, p.wlocation_id, p.department_id from client_details cd, " +
				" projectmntnc p where cd.client_id = p.client_id");
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
				sbQuery.append(" and cd.client_id in ("+StringUtils.join(getF_client(), ",")+") ");
			}
			
			if(getF_project()!=null && getF_project().length>0 ) {
				sbQuery.append(" and p.pro_id in ("+StringUtils.join(getF_project(), ",")+") ");
			}
			
			if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)) {
		//===start parvez date: 13-10-2022===	
				sbQuery.append(" and ( pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
					+uF.parseToInt((String)session.getAttribute(EMPID))+" ) or p.added_by = "+uF.parseToInt((String)session.getAttribute(EMPID))
//					+" or project_owner = "+uF.parseToInt((String)session.getAttribute(EMPID))+" ) ");
					+" or project_owners like '%,"+(String)session.getAttribute(EMPID)+",%' ) ");
		//===end parvez date: 13-10-2022===		
			}
			
			if(getStrStartDate() != null && !getStrStartDate().equalsIgnoreCase("null") && !getStrStartDate().equals("") && getStrEndDate() != null && !getStrEndDate().equalsIgnoreCase("null") && !getStrEndDate().equals("")) {
				sbQuery.append(" and ((start_date <= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and deadline >= '"+ uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "') ");
				sbQuery.append(" or (start_date >= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and start_date <= '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "') ");
				sbQuery.append(" or (start_date >= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and deadline <= '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "') ");
				sbQuery.append(" or (start_date <= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and deadline >= '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "') ");
				sbQuery.append(" or (deadline >= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and deadline <= '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "')) ");
			}
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
//			System.out.println("pst =====> " + pst);
			Map<String, String> hmClientName =  new HashMap<String, String>();
			Map<String, List<String>> hmClientProId = new HashMap<String, List<String>>();
			
			List<String> alInner = new ArrayList<String>();
			
			while(rs.next()) {
				alInner = hmClientProId.get(rs.getString("client_id"));
				if(alInner == null) alInner = new ArrayList<String>();
				alInner.add(rs.getString("pro_id"));
				hmClientProId.put(rs.getString("client_id"), alInner);
          		hmClientName.put(rs.getString("client_id"), rs.getString("client_name"));
			}
			rs.close();
			pst.close();
//			System.out.println("hmClientProId ===>> " + hmClientProId);
			
			Map<String, String> hmProName = CF.getProjectNameMap(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			
			Map<String, String> hmClientwiseHrs = new LinkedHashMap<String, String>();
			double grandTotal =0;
			double totalWorkDays = 0;
					Iterator<String> it = hmClientProId.keySet().iterator();
					while (it.hasNext()) {
						String clientId = it.next();
						List<String> alList = hmClientProId.get(clientId);
						int count = 0;
						double totalCost =0;
						double totalDays = 0;
						Map<String, String> hmProwiseHrs = new LinkedHashMap<String, String>();
						for(int i=0; alList != null && !alList.isEmpty() && i< alList.size(); i++) {
							pst = con.prepareStatement("select * from project_emp_details where pro_id in ("+alList.get(i)+")");
							rs = pst.executeQuery();
							List<String> alEmpIds = new ArrayList<String>();
							Map<String, String> hmEmpRatePerDay = new LinkedHashMap<String, String>();
							while(rs.next()) {
								alEmpIds.add(rs.getString("emp_id"));
								hmEmpRatePerDay.put(rs.getString("emp_id"), rs.getString("emp_actual_rate_per_day"));		
							}
							rs.close();
							pst.close();
							
							Map<String, String> hmEmpwiseHrs = new LinkedHashMap<String, String>();
							 
							if(alEmpIds != null && !alEmpIds.isEmpty()) {
								count++;
							}
							for(int j=0; alEmpIds != null && !alEmpIds.isEmpty() && j<alEmpIds.size(); j++) {
								
								pst = con.prepareStatement("select task_id, pro_id from activity_info where pro_id = ? and resource_ids like '%,"+alEmpIds.get(j)+",%' ");
								pst.setInt(1, uF.parseToInt(alList.get(i)));
//								System.out.println("pst ==>> " + pst);
								rs = pst.executeQuery();
		
								StringBuilder sbTaskIds = null;
								while(rs.next()) {
									if(sbTaskIds == null) {
										sbTaskIds = new StringBuilder();
										sbTaskIds.append(rs.getString("task_id"));
									} else {
										sbTaskIds.append(","+rs.getString("task_id"));
									}
								}
								rs.close();
								pst.close();
						
								if(sbTaskIds != null && !sbTaskIds.equals("")) {
									StringBuilder sbQue = new StringBuilder();
									sbQue.append("select sum(actual_hrs) as hrs from task_activity where emp_id = ? and activity_id  in ("+sbTaskIds.toString()+") ");
									if(getStrStartDate() != null && !getStrStartDate().equalsIgnoreCase("null") && !getStrStartDate().equals("") && getStrEndDate() != null && !getStrEndDate().equalsIgnoreCase("null") && !getStrEndDate().equals("")) {
										sbQue.append(" and task_date between '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "' ");
									}
									
									pst = con.prepareStatement(sbQue.toString());
									pst.setInt(1, uF.parseToInt(alEmpIds.get(j)));
//									System.out.println("pst ==>> " + pst);
									rs = pst.executeQuery();
									while(rs.next()) {
										double actHrs = uF.parseToDouble(hmEmpwiseHrs.get(alEmpIds.get(j)));
										actHrs += rs.getDouble("hrs");
										hmEmpwiseHrs.put(alEmpIds.get(j), ""+actHrs);
										
										double proActHrs = uF.parseToDouble(hmProwiseHrs.get(alList.get(i)));
										proActHrs += rs.getDouble("hrs");
										hmProwiseHrs.put(alList.get(i), ""+proActHrs);
										
										double clientActHrs = uF.parseToDouble(hmClientwiseHrs.get(clientId));
										clientActHrs += rs.getDouble("hrs");
										hmClientwiseHrs.put(clientId, ""+clientActHrs);
									}
									rs.close();
									pst.close();
								}
								
//								System.out.println("hmEmpwiseHrs ===>> " + hmEmpwiseHrs);
								List<String> proList = new ArrayList<String>();
								proList.add(hmClientName.get(clientId));
								proList.add(hmProName.get(alList.get(i)));
								proList.add(hmEmpName.get(alEmpIds.get(j)));
								double actDays = uF.parseToDouble(hmEmpwiseHrs.get(alEmpIds.get(j))) / 8;
								proList.add(uF.formatIntoOneDecimalWithOutComma(actDays));
								double actCost = actDays * uF.parseToDouble(hmEmpRatePerDay.get(alEmpIds.get(j)));
								proList.add(uF.formatIntoOneDecimalWithOutComma(actCost));
								
								totalCost += actCost; 
								totalDays += actDays;
								
								alOuter.add(proList);
							}
						}
						if(count!=0) {
						    grandTotal += totalCost;
						    totalWorkDays+= totalDays;
							List<String> clientTotalCostList = new ArrayList<String>();
							clientTotalCostList.add("Total:");
							clientTotalCostList.add("");
							clientTotalCostList.add("");
							clientTotalCostList.add(uF.formatIntoOneDecimalWithOutComma(totalDays));
							clientTotalCostList.add(uF.formatIntoOneDecimalWithOutComma(totalCost));
							alOuter.add(clientTotalCostList);
						}
				
					}
					List<String> grandTotalCostList=new ArrayList<String>();
					grandTotalCostList.add("GrandTotal:");
					grandTotalCostList.add("");
					grandTotalCostList.add("");
					grandTotalCostList.add(uF.formatIntoOneDecimalWithOutComma(totalWorkDays));
					grandTotalCostList.add(uF.formatIntoOneDecimalWithOutComma(grandTotal));
					alOuter.add(grandTotalCostList);		
			
		request.setAttribute("alOuter", alOuter);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public String loadOutstandingReport(UtilityFunctions uF) {
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
		clientList = new FillClients(request).fillClients(false);
		projectList= new ArrayList<FillProject>();
		if(getF_client()!=null)	{
			projectList= new FillProject(request).fillProjects(getF_client());
		} else {
			projectList= new FillProject(request).fillProjects();
		}
		getSelectedFilter(uF);
		return SUCCESS;
	}
	
	
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

		alFilter.add("PROJECT");
		if(getF_project()!=null) {
				String strProject="";
				int k=0;
				for(int i=0; projectList!=null && i<projectList.size();i++) {
					for(int j=0;j<getF_project().length;j++) {
						if(getF_project()[j].equals(projectList.get(i).getId())) {
							if(k==0) {
								strProject=projectList.get(i).getName();
							} else {
								strProject+=", "+projectList.get(i).getName();
							}
							k++;
						}
					}
				}
			if(strProject!=null && !strProject.equals("")) {
				hmFilter.put("PROJECT", strProject);
			} else {
				hmFilter.put("PROJECT", "All Projects");
			}
		} else {
			hmFilter.put("PROJECT", "All Project");
		}
		
		alFilter.add("FROM_TO");
		String strFdt = "-";
		String strEdt = "-";
		if(getStrStartDate() != null && !getStrStartDate().equals("")&& !getStrStartDate().equalsIgnoreCase("null")) {
			strFdt = uF.getDateFormat(getStrStartDate(), DATE_FORMAT, CF.getStrReportDateFormat());
		}
		if(getStrEndDate() != null && !getStrEndDate().equals("") && !getStrEndDate().equalsIgnoreCase("null")) {
			strEdt = uF.getDateFormat(getStrEndDate(), DATE_FORMAT, CF.getStrReportDateFormat());
		}
		hmFilter.put("FROM_TO",  strFdt+" - "+ strEdt);
		
		String selectedFilter=CF.getSelectedFilter1(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	public String[] getF_client() {
		return f_client;
	}
	public void setF_client(String[] f_client) {
		this.f_client = f_client;
	}
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request = request;
	} 
	
	public String[] getF_project() {
		return f_project;
	}
	public void setF_project(String[] f_project) {
		this.f_project = f_project;
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
	public List<FillProject> getProjectList() {
		return projectList;
	}
	public void setProjectList(List<FillProject> projectList) {
		this.projectList = projectList;
	}
	public HttpServletRequest getRequest() {
		return request;
	}
	public void setRequest(HttpServletRequest request) {
		this.request = request;
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

	public String[] getF_service() {
		return f_service;
	}

	public void setF_service(String[] f_service) {
		this.f_service = f_service;
	}

	public String[] getF_project_service() {
		return f_project_service;
	}

	public void setF_project_service(String[] f_project_service) {
		this.f_project_service = f_project_service;
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
	
}
