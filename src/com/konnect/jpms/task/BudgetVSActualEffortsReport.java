package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillClients;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class BudgetVSActualEffortsReport extends ActionSupport implements ServletRequestAware, IStatements{
	private static final long serialVersionUID = 1L;
	
	HttpSession session;
	CommonFunctions CF;
	String strEmpId;
	String strUserType;
	
	List<FillProjectList> projectdetailslist;
	List<FillClients> clientList;
	String[] pro_id;
	String[] client;
	String strStartDate;
	String strEndDate;
	
	public String execute() throws Exception {
		session = request.getSession();
		
		//System.out.println("BvsAR/24--execute");
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		UtilityFunctions uF = new UtilityFunctions();
		request.setAttribute(PAGE, "/jsp/task/BudgetVSActualEffortsReport.jsp");
		request.setAttribute(TITLE, "Budget vs Actual Efforts Report");
		strEmpId =(String) session.getAttribute(EMPID);
		strUserType =(String) session.getAttribute(BASEUSERTYPE);
		
		getBudgetActualEffortReport(uF);
		
		return loadBudgetActualEffortReport(uF);

	}
	
	public String loadBudgetActualEffortReport(UtilityFunctions uF) {
		
		projectdetailslist = new FillProjectList(request).fillProjectAllDetails();
		clientList = new FillClients(request).fillClients(false);
		
		getSelectedFilter(uF);
		 
		return SUCCESS;
	}
	
	private void getBudgetActualEffortReport(UtilityFunctions uF){
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs  = null;
		
		try{
			
			con = db.makeConnection(con);
			List<List<String>> alOuter = new ArrayList<List<String>>();
			
			if(getStrStartDate()!=null && getStrStartDate().equalsIgnoreCase("NULL")) {
				setStrStartDate(null);
				setStrEndDate(null);
			}		
			if(getStrEndDate()!=null && getStrEndDate().equalsIgnoreCase("NULL")) {
				setStrStartDate(null);
				setStrEndDate(null);
			}
			if(getStrStartDate()==null && getStrEndDate()==null) {
				Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
				int nMaxDate = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
				int nMinDate = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
				
				setStrStartDate(uF.getDateFormat(nMinDate+"/"+(cal.get(Calendar.MONTH)+ 1)+"/"+cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT));
				setStrEndDate(uF.getDateFormat(nMaxDate+"/"+(cal.get(Calendar.MONTH)+ 1)+"/"+cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT));
			}
			
			StringBuilder sbQuery = new StringBuilder();
			Map<String, String> hmProjectData =  new HashMap<String, String>();
			Map<String, List<String>> hmClientProId = new HashMap<String, List<String>>();
			
			sbQuery.append("select cd.client_id, cd.client_name, p.pro_id, p.pro_name,p.actual_calculation_type from client_details cd," +
					" projectmntnc p where cd.client_id = p.client_id ");
			
			if(getClient() != null && getClient().length>0) {
				sbQuery.append(" and p.client_id in ("+StringUtils.join(getClient(), ",")+") ");
			}
			if(getPro_id()!=null && getPro_id().length>0 ){
				sbQuery.append(" and p.pro_id in ("+StringUtils.join(getPro_id(), ",")+") ");
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
			rs = pst.executeQuery();
			List<String> alInner1 = new ArrayList<String>();
			
			while(rs.next()) {
				alInner1 = hmClientProId.get(rs.getString("client_id"));
				if(alInner1 == null) alInner1 = new ArrayList<String>();
				alInner1.add(rs.getString("pro_id"));			
				hmClientProId.put(rs.getString("client_id"), alInner1);
				hmProjectData.put(rs.getString("pro_id")+"_CLIENT", rs.getString("client_name"));
				hmProjectData.put(rs.getString("pro_id")+"_PRO_NAME", rs.getString("pro_name"));
				hmProjectData.put(rs.getString("pro_id")+"_CALC_TYPE", rs.getString("actual_calculation_type"));
			}
			rs.close();
			pst.close();
			
			Iterator<String> it = hmClientProId.keySet().iterator();
			while (it.hasNext()) {
				String clientId = it.next();
				List<String> alList = hmClientProId.get(clientId);
				for(int i=0; alList != null && !alList.isEmpty() && i< alList.size(); i++) {
					
					pst = con.prepareStatement("select * from project_emp_details where pro_id in ("+alList.get(i)+")");
					rs = pst.executeQuery();
					List<String> alEmpIds = new ArrayList<String>();
					Map<String, String> hmEmpRatePerDay = new LinkedHashMap<String, String>();
					while(rs.next()) {
						alEmpIds.add(rs.getString("emp_id"));
					}
					rs.close();
					pst.close();
					
					Map<String, String> hmEmpwiseHrs = new LinkedHashMap<String, String>();
					Map<String, String> hmEstimatedHrs = new LinkedHashMap<String, String>();
					for(int j=0;alEmpIds != null && !alEmpIds.isEmpty() && j< alEmpIds.size(); j++) {
						pst = con.prepareStatement("select task_id, pro_id,resource_ids,idealtime from activity_info where pro_id = ? and resource_ids like '%,"+alEmpIds.get(j)+",%' ");
						pst.setInt(1, uF.parseToInt(alList.get(i)));
						rs = pst.executeQuery();

						StringBuilder sbTaskIds = null;
						while(rs.next()) {
							if(sbTaskIds == null) {
								sbTaskIds = new StringBuilder();
								sbTaskIds.append(rs.getString("task_id"));
							} else {
								sbTaskIds.append(","+rs.getString("task_id"));
							}
							String[] strResources = rs.getString("resource_ids").split(",");
//							System.out.println("resourceCount="+strResources.length);
							int resourceCnt = uF.parseToInt(strResources.length+"")-1;
//							System.out.println("BvsAER/178--resourceCnt="+resourceCnt+"---taskId="+rs.getString("task_id")+"---length="+strResources.length);
							if(resourceCnt != 0){
								double estHrs = uF.parseToDouble(rs.getString("idealtime"))/resourceCnt;
								hmEstimatedHrs.put(alList.get(i), ""+estHrs);
							}
							
						}
						rs.close();
						pst.close();
						
						if(sbTaskIds != null && !sbTaskIds.equals("")) {
							StringBuilder sbQue = new StringBuilder();
							sbQue.append("select sum(actual_hrs) as hrs from task_activity where emp_id = ? and activity_id in ("+sbTaskIds.toString()+") ");
							if(getStrStartDate() != null && !getStrStartDate().equalsIgnoreCase("null") && !getStrStartDate().equals("") && getStrEndDate() != null && !getStrEndDate().equalsIgnoreCase("null") && !getStrEndDate().equals("")) {
								sbQue.append(" and task_date between '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "' ");
							}
							pst = con.prepareStatement(sbQue.toString());
							pst.setInt(1, uF.parseToInt(alEmpIds.get(j)));
//							System.out.println("BvsEE/196--pst="+pst);
							rs = pst.executeQuery();
							while(rs.next()) {
								double actHrs = uF.parseToDouble(hmEmpwiseHrs.get(alEmpIds.get(j)));
								actHrs += rs.getDouble("hrs");
								hmEmpwiseHrs.put(alList.get(i), ""+actHrs);
							}
							rs.close();
							pst.close();
						}
					}
					
					List<String> proList = new ArrayList<String>();
					proList.add(hmProjectData.get(alList.get(i)+"_CLIENT")); //0
					proList.add(hmProjectData.get(alList.get(i)+"_PRO_NAME")); //1
					
					
					double actDays = uF.parseToDouble(hmEmpwiseHrs.get(alList.get(i)))/8;
					double actMonths = actDays/30;
					double estDays = uF.parseToDouble(hmEstimatedHrs.get(alList.get(i)))/8;
					double estMonths = estDays/30;
					double variance = 0;
					if(hmProjectData.get(alList.get(i)+"_CALC_TYPE").equalsIgnoreCase("D")){
						proList.add(uF.showData(uF.formatIntoOneDecimal(actDays),"0.0")); //2
						proList.add(uF.showData(uF.formatIntoOneDecimal(estDays),"0.0")); //3
						variance = estDays-actDays;
						proList.add(uF.formatIntoOneDecimal(variance)); //4
					} else if(hmProjectData.get(alList.get(i)+"_CALC_TYPE").equalsIgnoreCase("M")){
						proList.add(uF.showData(uF.formatIntoOneDecimal(actMonths),"0.0")); //2
						proList.add(uF.showData(uF.formatIntoOneDecimal(estMonths),"0.0")); //3
						variance = estMonths-actMonths;
						proList.add(uF.formatIntoOneDecimal(variance)); //4
					} else{
						proList.add(uF.showData(hmEmpwiseHrs.get(alList.get(i)),"0.0")); //2
						proList.add(uF.showData(uF.formatIntoOneDecimal(uF.parseToDouble(hmEstimatedHrs.get(alList.get(i)))),"0.0")); //3
						variance = uF.parseToDouble(hmEstimatedHrs.get(alList.get(i)))-uF.parseToDouble(hmEmpwiseHrs.get(alList.get(i)));
						proList.add(uF.formatIntoOneDecimal(variance)); //4
					}
					
					
					alOuter.add(proList);
				}
			}
			request.setAttribute("alOuter", alOuter);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs); 
			db.closeStatements(pst);  
			db.closeConnection(con);
		}
	}
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		alFilter.add("PROJECT");
		if(getPro_id()!=null) {
			String strProjects="";
			int k=0;
			for(int i=0;projectdetailslist!=null && i<projectdetailslist.size();i++) {
				for(int j=0;j<getPro_id().length;j++) {
					if(getPro_id()[j].equals(projectdetailslist.get(i).getProjectID())) {
						if(k==0) {
							strProjects=projectdetailslist.get(i).getProjectName();
						} else {
							strProjects+=", "+projectdetailslist.get(i).getProjectName();
						}
						k++;
					}
				}
			}
			if(strProjects!=null && !strProjects.equals("")) {
				hmFilter.put("PROJECT", strProjects);
			} else {
				hmFilter.put("PROJECT", "All Projects");
			}
		} else {
			hmFilter.put("PROJECT", "All Projects");
		}
		
		alFilter.add("CLIENT");
		if(getClient()!=null) {
			String strClient="";
			int k=0;
			for(int i=0; clientList!=null && i<clientList.size();i++) {
				for(int j=0;j<getClient().length;j++) {
					if(getClient()[j].equals(clientList.get(i).getClientId())) {
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
		
		if((getStrStartDate()!=null && !getStrStartDate().equals(""))  && (getStrEndDate()!=null && !getStrEndDate().equals(""))){
			alFilter.add("FROMTO");
			hmFilter.put("FROMTO", uF.getDateFormat(getStrStartDate(), DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, CF.getStrReportDateFormat()));
		}
		
		String selectedFilter=CF.getSelectedFilter1(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public List<FillProjectList> getProjectdetailslist() {
		return projectdetailslist;
	}

	public void setProjectdetailslist(List<FillProjectList> projectdetailslist) {
		this.projectdetailslist = projectdetailslist;
	}

	public List<FillClients> getClientList() {
		return clientList;
	}

	public void setClientList(List<FillClients> clientList) {
		this.clientList = clientList;
	}

	public String[] getPro_id() {
		return pro_id;
	}

	public void setPro_id(String[] pro_id) {
		this.pro_id = pro_id;
	}

	public String[] getClient() {
		return client;
	}

	public void setClient(String[] client) {
		this.client = client;
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
	
}
