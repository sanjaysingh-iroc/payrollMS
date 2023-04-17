package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ProjectPerformanceOverallCP extends ActionSupport implements ServletRequestAware, IStatements{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2641577290477471767L;
	HttpServletRequest request;
	HttpSession session;
	CommonFunctions CF;
	String strUserType;
	public String execute(){
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		

		strUserType = (String)session.getAttribute(BASEUSERTYPE);
		request.setAttribute(PAGE, "/jsp/task/ProjectPerformanceOverallCP.jsp");
		request.setAttribute(TITLE, "Project Performance");
		
		if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)){
			request.setAttribute(TITLE, "My Performance");
		}
		
//		getProjectDetails();
		getProjectDetailsBYService();
		getProjectDetailsBYLocation();
		return SUCCESS;
	}
	
	String f_start;
	String f_end;
	
	public void getProjectDetailsBYLocation(){

		UtilityFunctions uF=new UtilityFunctions();
				
				Database db = new Database();
				db.setRequest(request);
				Connection con = null;
				PreparedStatement pst=null;
				ResultSet rs=null;
				try {
					con = db.makeConnection(con);
					
					
					Map<String, String> hmReimbursementAmountMap = CF.getReimbursementAmount(con, "P", "WL", true, getF_start(), getF_end(), uF);
					
					StringBuilder sbQuery = new StringBuilder();
					sbQuery.append("select * from(select sum(a.variable_cost) as variable_cost, sum(a.billable_amount)as billable_amount, sum(a.budgeted_cost) as budgeted_cost,  sum(a.already_work) as already_work, sum(a.actual_amount) as actual_amount,sum(a.actual_hrs) as actual_hrs,sum(a.idealtime) as idealtime,pmntc.wlocation_id from (select pc.*,pt.actual_hrs,pt.idealtime from project_cost pc LEFT JOIN project_time pt ON(pc.pro_id=pt.pro_id))as a, projectmntnc pmntc where pmntc.pro_id=a.pro_id and approve_status = 'approved'");
					
					if(getF_start()!=null && getF_end()!=null && !getF_start().equalsIgnoreCase(LABEL_FROM_DATE) && !getF_end().equalsIgnoreCase(LABEL_TO_DATE)){
						sbQuery.append(" and start_date between '"+uF.getDateFormat(getF_start(), DATE_FORMAT, DBDATE)+"' and '"+uF.getDateFormat(getF_end(), DATE_FORMAT, DBDATE)+"' ");
					}
					if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)){
						sbQuery.append(" and added_by="+uF.parseToInt((String)session.getAttribute(EMPID)));
					}
					
					sbQuery.append(" group by pmntc.wlocation_id) as d,work_location_info wli where d.wlocation_id=wli.wlocation_id");
					pst = con.prepareStatement(sbQuery.toString());
					
					rs=pst.executeQuery();
					List<List<String>> alOuter=new ArrayList<List<String>>();
					
					double dblBillableTotal=0;
					double dblBudgetedTotal=0;
					double dblActualTotal=0;
					double diffTotal = 0;
					
					double dblIdealTimeTotal = 0;
					double dblActualTimeTotal = 0;
					int nCount = 0;
					
					
					while(rs.next()){
						
						double dblReimbursement = uF.parseToDouble(hmReimbursementAmountMap.get(rs.getString("wlocation_id")));
						
//						double dblBillable=(uF.parseToDouble(rs.getString("billable_amount"))+uF.parseToDouble(rs.getString("variable_cost")));
						double dblBillable=uF.parseToDouble(rs.getString("billable_amount"));
//						double dblBudgeted=uF.parseToDouble(rs.getString("budgeted_cost"));
						double dblBudgeted = uF.parseToDouble(rs.getString("budgeted_cost")) + uF.parseToDouble(rs.getString("variable_cost"));
						double dblActual= uF.parseToDouble(rs.getString("actual_amount"))   + dblReimbursement;
						double diff = 0;
						
						double dblIdealTime = uF.parseToDouble(rs.getString("idealtime"));
						double dblActualTime = uF.parseToDouble(rs.getString("actual_hrs"));
						
						dblBillableTotal +=dblBillable;
						dblBudgetedTotal +=dblBudgeted;
						dblActualTotal +=dblActual;
						nCount ++;
						
						dblIdealTimeTotal +=dblIdealTime;
						dblActualTimeTotal +=dblActualTime;
						
						if(dblBillable>0){
							diff = ((dblBillable-dblActual)/dblBillable) * 100;
						}
						diffTotal +=diff;
						
						List<String> alInner=new ArrayList<String>();
						alInner.add(rs.getString("wlocation_id"));
						alInner.add(rs.getString("wlocation_name"));
						
						if (dblActual>dblBudgeted && dblActual<dblBillable){
							/*alInner.add("<img src=\"images1/icons/re_submit.png\" width=\"17px\">");*/
							alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\"></i>");
							
						}else if(dblActual<dblBudgeted){
							/*alInner.add("<img src=\"images1/icons/approved.png\" width=\"17px\">");*/
							alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\"></i>");
							
						}else if(dblActual>dblBillable){
							alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\"></i>");
						}else{
							alInner.add("&nbsp;");
						}
						
						alInner.add(uF.formatIntoOneDecimal(diff)+"%");
						alInner.add(uF.formatIntoOneDecimal(dblBillable-dblActual));
						alInner.add(uF.formatIntoOneDecimal(dblBillable));
						alInner.add(uF.formatIntoOneDecimal(dblActual));
						
						alInner.add(uF.formatIntoOneDecimal(uF.parseToDouble(rs.getString("idealtime"))));
						alInner.add(uF.formatIntoOneDecimal(uF.parseToDouble(rs.getString("actual_hrs"))));
						
						alInner.add("Deadline");
						
						
						alOuter.add(alInner);
					}
					rs.close();
					pst.close();
					
					
					List<String> alInner=new ArrayList<String>();
					alInner.add("");
					alInner.add("Aggregate");
					
					if (dblActualTotal>dblBudgetedTotal && dblActualTotal<dblBillableTotal){
						/*alInner.add("<img src=\"images1/icons/re_submit.png\" width=\"17px\">");*/
						alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\"></i>");
						
					}else if(dblActualTotal<dblBudgetedTotal){
						 /*alInner.add("<img src=\"images1/icons/approved.png\" width=\"17px\">");*/
						alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\"></i>");
						
					}else if(dblActualTotal>dblBillableTotal){
						/*alInner.add("<img src=\"images1/icons/denied.png\" width=\"17px\">");*/
						alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\"></i>");
						
					}else{
						alInner.add("&nbsp;");
					}
					
					alInner.add(uF.formatIntoOneDecimal(diffTotal/nCount)+"%");
					alInner.add(uF.formatIntoOneDecimal(dblBillableTotal-dblActualTotal));
					alInner.add(uF.formatIntoOneDecimal(dblBillableTotal));
					alInner.add(uF.formatIntoOneDecimal(dblActualTotal));
					
					alInner.add(uF.formatIntoOneDecimal(dblIdealTimeTotal));
					alInner.add(uF.formatIntoOneDecimal(dblActualTimeTotal));
					alInner.add("Deadline");
					
					   
					alOuter.add(alInner);
					
					
					request.setAttribute("alOuter2",alOuter);

					

					
					Map<String, String> hmEmpGrossAmountMapH = CF.getEmpGrossSalary(uF, CF, con, uF.getCurrentDate(CF.getStrTimeZone())+"", "H");
					Map<String, String> hmEmpGrossAmountMapD = CF.getEmpGrossSalary(uF, CF, con, uF.getCurrentDate(CF.getStrTimeZone())+"", "D");
					
					pst = con.prepareStatement("select sum(variable_cost) as variable_cost, pro_id from variable_cost group by pro_id");
					rs = pst.executeQuery();
					Map<String, String> hmVariableCost = new HashMap<String, String>();
					while(rs.next()){
						hmVariableCost.put(rs.getString("pro_id"), rs.getString("variable_cost"));	
					}
					rs.close();
					pst.close();
					
					StringBuilder sbQuery1 = new StringBuilder();
					
					sbQuery1.append("select *, ai.emp_id as a_emp_id, ai.idealtime as a_idealtime, pmc.idealtime as pmc_idealtime, ai.already_work as a_already_work from activity_info ai, projectmntnc pmc where pmc.pro_id = ai.pro_id and pmc.approve_status = 'approved' and pmc.wlocation_id::integer>0 ");
					if(getF_start()!=null && getF_end()!=null && !getF_start().equalsIgnoreCase(LABEL_FROM_DATE) && !getF_end().equalsIgnoreCase(LABEL_TO_DATE)){
						sbQuery1.append(" and pmc.start_date between '"+uF.getDateFormat(getF_start(), DATE_FORMAT, DBDATE)+"' and '"+uF.getDateFormat(getF_end(), DATE_FORMAT, DBDATE)+"' ");
					}
					if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)){
						sbQuery1.append(" and added_by="+uF.parseToInt((String)session.getAttribute(EMPID)));
					}
					sbQuery1.append(" order by pmc.wlocation_id::integer ");
					pst = con.prepareStatement(sbQuery1.toString());
					rs = pst.executeQuery();
					Map hmWLocation = CF.getWorkLocationMap(con);
					double dblBugedtedAmt = 0;
					double dblActualAmt = 0;
					double dblBillableAmt = 0;
					
					
					Map<String, String> hmProPerformaceBudget = new HashMap<String, String>();
					Map<String, String> hmProPerformaceActual = new HashMap<String, String>();
					Map<String, String> hmProPerformaceBillable = new HashMap<String, String>();
					
					Map<String, String> hmProPerformaceProjectName = new HashMap<String, String>();
					Map<String, String> hmProPerformaceProjectProfitP = new HashMap<String, String>();
					Map<String, String> hmProPerformaceProjectProfitA = new HashMap<String, String>();
					
					Map<String, String> hmProPerformaceProjectAmountIndicator = new HashMap<String, String>();
					
					List alProjectId  = new ArrayList(); 
					
					String strProjectIdNew=null;
					String strProjectIdOld=null;
					
					double dblEmpRate = 0.0d;
					
					while(rs.next()){
						
						strProjectIdNew = rs.getString("wlocation_id");
						double dblReimbursement = uF.parseToDouble(hmReimbursementAmountMap.get(rs.getString("pro_id")));
						
						
						if(strProjectIdNew!=null && !strProjectIdNew.equalsIgnoreCase(strProjectIdOld)){
							dblBugedtedAmt = 0;
							dblActualAmt = 0;
							dblBillableAmt = 0;
							
						}
						
						
						
						 
						
						 if("F".equalsIgnoreCase(rs.getString("billing_type"))){
							 dblBillableAmt = uF.parseToDouble(rs.getString("billing_amount"));
						 }else{
							 dblBillableAmt += uF.parseToDouble(rs.getString("billing_amount"));
						 }
						 
						 
						 
						 if("H".equalsIgnoreCase(rs.getString("actual_calculation_type"))){
							 dblEmpRate = uF.parseToDouble(hmEmpGrossAmountMapH.get(rs.getString("a_emp_id")));
						 }else{
							 dblEmpRate = uF.parseToDouble(hmEmpGrossAmountMapD.get(rs.getString("a_emp_id")));
						 }
						 
						 
						 
						 
						 
						 dblBugedtedAmt += uF.parseToDouble(rs.getString("a_idealtime")) * dblEmpRate;
						 dblActualAmt += uF.parseToDouble(rs.getString("a_already_work")) * dblEmpRate;
						 
						 
						 hmProPerformaceBudget.put(rs.getString("wlocation_id"), uF.formatIntoOneDecimalWithOutComma(dblBugedtedAmt + uF.parseToDouble(hmVariableCost.get(rs.getString("pro_id")))));
						 hmProPerformaceActual.put(rs.getString("wlocation_id"), uF.formatIntoOneDecimalWithOutComma(dblActualAmt + dblReimbursement));
						 hmProPerformaceBillable.put(rs.getString("wlocation_id"), uF.formatIntoOneDecimalWithOutComma(dblBillableAmt));
						 
						 
						 Map<String, String> hmLocation = (Map)hmWLocation.get(rs.getString("wlocation_id"));
						 if(hmLocation==null)hmLocation=new HashMap<String, String>();
						 hmProPerformaceProjectName.put(rs.getString("wlocation_id"), uF.showData(hmLocation.get("WL_NAME"), ""));
						 
						 double diff = 0;
						 double diffP = 0;
						 if(dblBillableAmt>0){
							 diff = (dblBillableAmt-(dblActualAmt + dblReimbursement));
							 diffP = (diff/dblBillableAmt) * 100;
						 }
						 hmProPerformaceProjectProfitP.put(rs.getString("wlocation_id"), uF.formatIntoTwoDecimal(diffP));
						 hmProPerformaceProjectProfitA.put(rs.getString("wlocation_id"), uF.formatIntoTwoDecimal(diff));
							
							
						if (dblActualAmt>dblBugedtedAmt && dblActualTotal<dblBillableAmt){
							 /*hmProPerformaceProjectAmountIndicator.put(rs.getString("wlocation_id"), "<img src=\"images1/icons/re_submit.png\" width=\"17px\">");*/
							hmProPerformaceProjectAmountIndicator.put(rs.getString("wlocation_id"), "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\"></i>");
							
							
						}else if(dblActualAmt<dblBugedtedAmt){
							/*hmProPerformaceProjectAmountIndicator.put(rs.getString("wlocation_id"), "<img src=\"images1/icons/approved.png\" width=\"17px\">");*/
							hmProPerformaceProjectAmountIndicator.put(rs.getString("wlocation_id"), "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\"></i>");
						}else if(dblActualAmt>dblBillableAmt){
							/*hmProPerformaceProjectAmountIndicator.put(rs.getString("wlocation_id"), "<img src=\"images1/icons/denied.png\" width=\"17px\">");*/
							hmProPerformaceProjectAmountIndicator.put(rs.getString("wlocation_id"), "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\"></i>");
							
						}else{
							hmProPerformaceProjectAmountIndicator.put(rs.getString("wlocation_id"), "&nbsp;");
						}
						 
						 
						
						
						/*
						Date dtDeadline = uF.getDateFormat(hmDeadline.get(rs.getString("pro_id")), DBDATE);
						Date dtCurrentDate = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE);
						
						if(dtDeadline!=null && dtCurrentDate!=null && dtDeadline.after(dtCurrentDate)){
							if(dblActualTime<=dblBugedtedTime){
								hmProPerformaceProjectTimeIndicator.put(rs.getString("pro_id"), "<img src=\"images1/icons/approved.png\" width=\"17px\">");
							}else{
								hmProPerformaceProjectTimeIndicator.put(rs.getString("pro_id"), "<img src=\"images1/icons/re_submit.png\" width=\"17px\">");
							}
						}else if(dtDeadline!=null && dtCurrentDate!=null && dtCurrentDate.after(dtDeadline)){
							hmProPerformaceProjectTimeIndicator.put(rs.getString("pro_id"), "<img src=\"images1/icons/denied.png\" width=\"17px\">");
						}else{
							hmProPerformaceProjectTimeIndicator.put(rs.getString("pro_id"), "");
						}*/
						
						
						 if(!alProjectId.contains(rs.getString("wlocation_id"))){
							 alProjectId.add(rs.getString("wlocation_id"));
						 }
						 
						 strProjectIdOld = strProjectIdNew;
					}
					rs.close();
					pst.close();
					
					
					request.setAttribute("hmProPerformaceBillable",hmProPerformaceBillable);
					request.setAttribute("hmProPerformaceActual",hmProPerformaceActual);
					request.setAttribute("hmProPerformaceBudget",hmProPerformaceBudget);
					request.setAttribute("hmProPerformaceProjectProfitA",hmProPerformaceProjectProfitA);
					request.setAttribute("hmProPerformaceProjectProfitP",hmProPerformaceProjectProfitP);
					request.setAttribute("hmProPerformaceProjectAmountIndicator",hmProPerformaceProjectAmountIndicator);
					request.setAttribute("hmProPerformaceProjectName",hmProPerformaceProjectName);
					request.setAttribute("alProjectId",alProjectId);	
					
					
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					db.closeResultSet(rs);
					db.closeStatements(pst);
					db.closeConnection(con);
				}
			
	}
	
	
	public void getProjectDetails(){
		UtilityFunctions uF=new UtilityFunctions();
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs=null;
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmReimbursementAmountMap = CF.getReimbursementAmount(con, "P", "P", true, getF_start(), getF_end(), uF);
			
			Map<String, String> hmDeadline  = new HashMap<String, String>();
			Map<String, String> hmApprovedDate  = new HashMap<String, String>();
			pst = con.prepareStatement("select * from projectmntnc where approve_status = 'n' ");
			rs = pst.executeQuery();
			while(rs.next()){
				hmDeadline.put(rs.getString("pro_id"), rs.getString("deadline"));
				hmApprovedDate.put(rs.getString("pro_id"), rs.getString("approve_date"));
			}
			rs.close();
			pst.close();
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select  variable_cost, actual_amount, billable_amount, budgeted_cost, b.pro_id, already_work, b.pro_name,actual_hrs,idealtime, completed from (select pc.*,pmntc.pro_name from project_cost pc, projectmntnc pmntc where pc.pro_id = pmntc.pro_id  and approve_status = 'approved' ");
			
			if(getF_start()!=null && getF_end()!=null){
				sbQuery.append(" and start_date between '"+uF.getDateFormat(getF_start(), DATE_FORMAT, DBDATE)+"' and '"+uF.getDateFormat(getF_end(), DATE_FORMAT, DBDATE)+"' ");
			}
			if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)){
				sbQuery.append(" and added_by="+uF.parseToInt((String)session.getAttribute(EMPID)));
			}
			
			sbQuery.append(" order by pmntc.deadline desc)as b left join project_time pt on pt.pro_id=b.pro_id");
			pst = con.prepareStatement(sbQuery.toString());
			
			
			pst = con.prepareStatement(sbQuery.toString());
			
			rs=pst.executeQuery();
			List<List<String>> alOuter=new ArrayList<List<String>>();
			
			double dblBillableTotal=0;
			double dblBudgetedTotal=0;
			double dblActualTotal=0;
			double diffTotal = 0;
			
			double dblIdealTimeTotal = 0;
			double dblActualTimeTotal = 0;
			int nCount = 0;
			
			while(rs.next()){
				
				double dblReimbursement = uF.parseToDouble(hmReimbursementAmountMap.get(rs.getString("pro_id")));
				
//				double dblBillable=(uF.parseToDouble(rs.getString("billable_amount"))+uF.parseToDouble(rs.getString("variable_cost")));
				double dblBillable=uF.parseToDouble(rs.getString("billable_amount"));
//				double dblBudgeted=uF.parseToDouble(rs.getString("budgeted_cost"));
				double dblBudgeted = uF.parseToDouble(rs.getString("budgeted_cost")) + uF.parseToDouble(rs.getString("variable_cost"));
				double dblActual= uF.parseToDouble(rs.getString("actual_amount"))   + dblReimbursement;
				double diff = 0;
				
				double dblIdealTime = uF.parseToDouble(rs.getString("idealtime"));
				double dblActualTime = uF.parseToDouble(rs.getString("actual_hrs"));
				
				dblBillableTotal +=dblBillable;
				dblBudgetedTotal +=dblBudgeted;
				dblActualTotal +=dblActual;
				nCount ++;
				
				dblIdealTimeTotal +=dblIdealTime;
				dblActualTimeTotal +=dblActualTime;
				
				if(dblBillable>0){
					diff = ((dblBillable-dblActual)/dblBillable) * 100;
				}
				diffTotal +=diff;
				
				List<String> alInner=new ArrayList<String>();
				alInner.add(rs.getString("pro_id"));
				alInner.add(rs.getString("pro_name"));
				
				if (dblActual>dblBudgeted && dblActual<dblBillable){
					/*alInner.add("<img src=\"images1/icons/re_submit.png\" width=\"17px\">");*/
					alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\"></i>");
					
				}else if(dblActual<dblBudgeted){
					/*alInner.add("<img src=\"images1/icons/approved.png\" width=\"17px\">");*/
					alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\"></i>");
				}else if(dblActual>dblBillable){
					/*alInner.add("<img src=\"images1/icons/denied.png\" width=\"17px\">");*/
					alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\"></i>");
					
				}else{
					alInner.add("&nbsp;");
				}
				
				alInner.add(uF.formatIntoOneDecimal(diff)+"%");
				alInner.add(uF.formatIntoOneDecimal(dblBillable-dblActual));
				alInner.add(uF.formatIntoOneDecimal(dblBillable));
				alInner.add(uF.formatIntoOneDecimal(dblActual));
				
				alInner.add(uF.formatIntoOneDecimal(dblIdealTime));
				alInner.add(uF.formatIntoOneDecimal(dblActualTime));
				
				Date dtDeadline = uF.getDateFormat(hmDeadline.get(rs.getString("pro_id")), DBDATE);
				Date dtCurrentDate = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE);
				
				if(dtDeadline!=null && dtCurrentDate!=null && dtDeadline.after(dtCurrentDate)){
					if(dblActualTime<=dblIdealTime){
						/*alInner.add("<img src=\"images1/icons/approved.png\" width=\"17px\">");*/
						alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\"></i>");
						
					}else{
						/*alInner.add("<img src=\"images1/icons/re_submit.png\" width=\"17px\">");*/
						alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\"></i>");
						
					}
				}else if(dtDeadline!=null && dtCurrentDate!=null && dtCurrentDate.after(dtDeadline)){
					/*alInner.add("<img src=\"images1/icons/denied.png\" width=\"17px\">");*/
					alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\"></i>");
					
				}else{
					alInner.add("");
				}
				   
				alOuter.add(alInner);
			}
			rs.close();
			pst.close();
			
			List<String> alInner=new ArrayList<String>();
			alInner.add("");
			alInner.add("Aggregate");
			
			if (dblActualTotal>dblBudgetedTotal && dblActualTotal<dblBillableTotal){
				/*alInner.add("<img src=\"images1/icons/re_submit.png\" width=\"17px\">");*/
				alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\"></i>");
				
			}else if(dblActualTotal<dblBudgetedTotal){
				/*alInner.add("<img src=\"images1/icons/approved.png\" width=\"17px\">");*/
				alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\"></i>");
				
			}else if(dblActualTotal>dblBillableTotal){
				/*alInner.add("<img src=\"images1/icons/denied.png\" width=\"17px\">");*/
				alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\"></i>");
				
			}else{
				alInner.add("&nbsp;");
			}
			
			alInner.add(uF.formatIntoOneDecimal(diffTotal/nCount)+"%");
			alInner.add(uF.formatIntoOneDecimal(dblBillableTotal-dblActualTotal));
			alInner.add(uF.formatIntoOneDecimal(dblBillableTotal));
			alInner.add(uF.formatIntoOneDecimal(dblActualTotal));
			
			alInner.add(uF.formatIntoOneDecimal(dblIdealTimeTotal));
			alInner.add(uF.formatIntoOneDecimal(dblActualTimeTotal));
			alInner.add("");
			   
			alOuter.add(alInner);
			
			request.setAttribute("alOuter1",alOuter);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	public void getProjectDetailsBYService(){

		UtilityFunctions uF=new UtilityFunctions();
				
				Database db = new Database();
				db.setRequest(request);
				Connection con = null;
				PreparedStatement pst=null;
				ResultSet rs=null;
				try {
					con = db.makeConnection(con);
					
					Map<String, String> hmReimbursementAmountMap = CF.getReimbursementAmount(con, "P", "S", true, getF_start(), getF_end(), uF);
					
					StringBuilder sbQuery = new StringBuilder();
					sbQuery.append("select * from(select sum(a.variable_cost) as variable_cost, sum(a.billable_amount)as billable_amount, sum(a.budgeted_cost) as budgeted_cost,  sum(a.already_work) as already_work, sum(a.actual_amount) as actual_amount,sum(a.actual_hrs) as actual_hrs,sum(a.idealtime) as idealtime,pmntc.service  from (select pc.*,pt.actual_hrs,pt.idealtime from project_cost pc LEFT JOIN project_time pt ON(pc.pro_id=pt.pro_id))as a, projectmntnc pmntc where pmntc.pro_id=a.pro_id and approve_status = 'approved' ");
					
					if(getF_start()!=null && getF_end()!=null){
						sbQuery.append(" and start_date between '"+uF.getDateFormat(getF_start(), DATE_FORMAT, DBDATE)+"' and '"+uF.getDateFormat(getF_end(), DATE_FORMAT, DBDATE)+"' ");
					}
					if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)){
						sbQuery.append(" and added_by="+uF.parseToInt((String)session.getAttribute(EMPID)));
					}
					
					sbQuery.append(" group by pmntc.service) as d,services_project sp where sp.service_project_id=d.service::integer");
					pst = con.prepareStatement(sbQuery.toString());

					
					pst = con.prepareStatement(sbQuery.toString());
					rs=pst.executeQuery();
					List<List<String>> alOuter=new ArrayList<List<String>>();
					
					double dblBillableTotal=0;
					double dblBudgetedTotal=0;
					double dblActualTotal=0;
					double diffTotal = 0;
					
					double dblIdealTimeTotal = 0;
					double dblActualTimeTotal = 0;
					int nCount = 0;
					
					while(rs.next()){
						List<String> alInner=new ArrayList<String>();
						
						double dblReimbursement = uF.parseToDouble(hmReimbursementAmountMap.get(rs.getString("service")));
						
//						double dblBillable=(uF.parseToDouble(rs.getString("billable_amount"))+uF.parseToDouble(rs.getString("variable_cost")));
						double dblBillable=uF.parseToDouble(rs.getString("billable_amount"));
//						double dblBudgeted=uF.parseToDouble(rs.getString("budgeted_cost"));
						double dblBudgeted = uF.parseToDouble(rs.getString("budgeted_cost")) + uF.parseToDouble(rs.getString("variable_cost"));
						double dblActual= uF.parseToDouble(rs.getString("actual_amount"))   + dblReimbursement;
						double diff = 0;
						
						double dblIdealTime = uF.parseToDouble(rs.getString("idealtime"));
						double dblActualTime = uF.parseToDouble(rs.getString("actual_hrs"));
						
						dblBillableTotal +=dblBillable;
						dblBudgetedTotal +=dblBudgeted;
						dblActualTotal +=dblActual;
						nCount ++;
						
						dblIdealTimeTotal +=dblIdealTime;
						dblActualTimeTotal +=dblActualTime;
						
						if(dblBillable>0){
							diff = ((dblBillable-dblActual)/dblBillable) * 100;
						}
						diffTotal +=diff;

						
						alInner.add(rs.getString("service"));
						alInner.add(rs.getString("service_name"));
						
						if (dblActual>dblBudgeted && dblActual<dblBillable){
							/*alInner.add("<img src=\"images1/icons/re_submit.png\" width=\"17px\">");*/
							alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\"></i>");
							
						}else if(dblActual<dblBudgeted){
							/*alInner.add("<img src=\"images1/icons/approved.png\" width=\"17px\">");*/
							alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\"></i>");
							
						}else if(dblActual>dblBillable){
							/*alInner.add("<img src=\"images1/icons/denied.png\" width=\"17px\">");*/
							alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\"></i>");
							
						}else{
							alInner.add("&nbsp;");
						}
						
						
						alInner.add(uF.formatIntoOneDecimal(diff)+"%");
						alInner.add(uF.formatIntoOneDecimal(dblBillable-dblActual));
						alInner.add(uF.formatIntoOneDecimal(dblBillable));
						alInner.add(uF.formatIntoOneDecimal(dblActual));
						
						
//						alInner.add((uF.parseToDouble(rs.getString("budgeted_cost")))+"");
//						alInner.add(uF.formatIntoOneDecimal(billable-budgeted/budgeted*100)+"");
//						alInner.add("---");
						
						alInner.add(uF.formatIntoOneDecimal(uF.parseToDouble(rs.getString("idealtime"))));
						alInner.add(uF.formatIntoOneDecimal(uF.parseToDouble(rs.getString("actual_hrs"))));
						alInner.add("Deadline");

						alOuter.add(alInner);
					}
					rs.close();
					pst.close();
					
					List<String> alInner=new ArrayList<String>();
					alInner.add("");
					alInner.add("Aggregate");
					
					if (dblActualTotal>dblBudgetedTotal && dblActualTotal<dblBillableTotal){
						/*alInner.add("<img src=\"images1/icons/re_submit.png\" width=\"17px\">");*/
						alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\"></i>");
						
					}else if(dblActualTotal<dblBudgetedTotal){
						/*alInner.add("<img src=\"images1/icons/approved.png\" width=\"17px\">");*/
						alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\"></i>");
						
					}else if(dblActualTotal>dblBillableTotal){
						/*alInner.add("<img src=\"images1/icons/denied.png\" width=\"17px\">");*/
						alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\"></i>");
						
					}else{
						alInner.add("&nbsp;");
					}
					
					alInner.add(uF.formatIntoOneDecimal(diffTotal/nCount)+"%");
					alInner.add(uF.formatIntoOneDecimal(dblBillableTotal-dblActualTotal));
					alInner.add(uF.formatIntoOneDecimal(dblBillableTotal));
					alInner.add(uF.formatIntoOneDecimal(dblActualTotal));
					
					alInner.add(uF.formatIntoOneDecimal(dblIdealTimeTotal));
					alInner.add(uF.formatIntoOneDecimal(dblActualTimeTotal));
					alInner.add("Deadline");
					
					   
					alOuter.add(alInner);
					
					request.setAttribute("alOuter",alOuter);
					
//					CF.getProjectHolidayCalculation(con, uF, CF, hmEmpGrossAmountMap, hmActualCostMap, hmActualTimeMap, getF_start(), getF_end());
					Map<String, String> hmEmpGrossAmountMapH = CF.getEmpGrossSalary(uF, CF, con, uF.getCurrentDate(CF.getStrTimeZone())+"", "H");
					Map<String, String> hmEmpGrossAmountMapD = CF.getEmpGrossSalary(uF, CF, con, uF.getCurrentDate(CF.getStrTimeZone())+"", "D");
					
					pst = con.prepareStatement("select sum(variable_cost) as variable_cost, pro_id from variable_cost group by pro_id");
					rs = pst.executeQuery();
					Map<String, String> hmVariableCost = new HashMap<String, String>();
					while(rs.next()){
						hmVariableCost.put(rs.getString("pro_id"), rs.getString("variable_cost"));	
					}
					rs.close();
					pst.close();
					
					StringBuilder sbQuery1 = new StringBuilder();
					
					sbQuery1.append("select *, ai.emp_id as a_emp_id, ai.idealtime as a_idealtime, pmc.idealtime as pmc_idealtime, ai.already_work as a_already_work from activity_info ai, projectmntnc pmc where pmc.pro_id = ai.pro_id and pmc.approve_status = 'approved' and pmc.service::integer>0 ");
					if(getF_start()!=null && getF_end()!=null && !getF_start().equalsIgnoreCase(LABEL_FROM_DATE) && !getF_end().equalsIgnoreCase(LABEL_TO_DATE)){
						sbQuery1.append(" and pmc.start_date between '"+uF.getDateFormat(getF_start(), DATE_FORMAT, DBDATE)+"' and '"+uF.getDateFormat(getF_end(), DATE_FORMAT, DBDATE)+"' ");
					}
					if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)){
						sbQuery1.append(" and added_by="+uF.parseToInt((String)session.getAttribute(EMPID)));
					}
					sbQuery1.append(" order by pmc.service::integer ");
					
					
					pst = con.prepareStatement(sbQuery1.toString());
					rs = pst.executeQuery();
					
					Map<String, String> hmService = CF.getProjectServicesMap(con, false);
					
					double dblBugedtedAmt = 0;
					double dblActualAmt = 0;
					double dblBillableAmt = 0;
					
					
					Map<String, String> hmProPerformaceBudgetService = new HashMap<String, String>();
					Map<String, String> hmProPerformaceActualService = new HashMap<String, String>();
					Map<String, String> hmProPerformaceBillableService = new HashMap<String, String>();
					
					Map<String, String> hmProPerformaceProjectNameService = new HashMap<String, String>();
					Map<String, String> hmProPerformaceProjectProfitServiceP = new HashMap<String, String>();
					Map<String, String> hmProPerformaceProjectProfitServiceA = new HashMap<String, String>();
					
					Map<String, String> hmProPerformaceProjectAmountIndicatorService = new HashMap<String, String>();
					
					List alServiceId  = new ArrayList(); 
					
					String strProjectIdNew=null;
					String strProjectIdOld=null;
					
					double dblEmpRate = 0.0d;
					
					while(rs.next()){
						
						strProjectIdNew = rs.getString("service");
						double dblReimbursement = uF.parseToDouble(hmReimbursementAmountMap.get(rs.getString("pro_id")));
						
						
						if(strProjectIdNew!=null && !strProjectIdNew.equalsIgnoreCase(strProjectIdOld)){
							dblBugedtedAmt = 0;
							dblActualAmt = 0;
							dblBillableAmt = 0;
							
						}
						
						
						
						 
						
						 if("F".equalsIgnoreCase(rs.getString("billing_type"))){
							 dblBillableAmt = uF.parseToDouble(rs.getString("billing_amount"));
						 }else{
							 dblBillableAmt += uF.parseToDouble(rs.getString("billing_amount"));
						 }
						 
						 
						 
						 if("H".equalsIgnoreCase(rs.getString("actual_calculation_type"))){
							 dblEmpRate = uF.parseToDouble(hmEmpGrossAmountMapH.get(rs.getString("a_emp_id")));
						 }else{
							 dblEmpRate = uF.parseToDouble(hmEmpGrossAmountMapD.get(rs.getString("a_emp_id")));
						 }
						 
						 
						 
						 
						 
						 dblBugedtedAmt += uF.parseToDouble(rs.getString("a_idealtime")) * dblEmpRate;
						 dblActualAmt += uF.parseToDouble(rs.getString("a_already_work")) * dblEmpRate;
						 
						 
						 hmProPerformaceBudgetService.put(rs.getString("service"), uF.formatIntoOneDecimalWithOutComma(dblBugedtedAmt + uF.parseToDouble(hmVariableCost.get(rs.getString("pro_id")))));
						 hmProPerformaceActualService.put(rs.getString("service"), uF.formatIntoOneDecimalWithOutComma(dblActualAmt + dblReimbursement));
						 hmProPerformaceBillableService.put(rs.getString("service"), uF.formatIntoOneDecimalWithOutComma(dblBillableAmt));
						 
						 
						 
						 hmProPerformaceProjectNameService.put(rs.getString("service"), uF.showData(hmService.get(rs.getString("service")), ""));
						 
						 double diff = 0;
						 double diffP = 0;
						 if(dblBillableAmt>0){
							 diff = (dblBillableAmt-(dblActualAmt + dblReimbursement));
							 diffP = (diff/dblBillableAmt) * 100;
						 }
						 hmProPerformaceProjectProfitServiceP.put(rs.getString("service"), uF.formatIntoTwoDecimal(diffP));
						 hmProPerformaceProjectProfitServiceA.put(rs.getString("service"), uF.formatIntoTwoDecimal(diff));
							
							
						if (dblActualAmt>dblBugedtedAmt && dblActualTotal<dblBillableAmt){
							 /*hmProPerformaceProjectAmountIndicatorService.put(rs.getString("service"), "<img src=\"images1/icons/re_submit.png\" width=\"17px\">");*/
							 hmProPerformaceProjectAmountIndicatorService.put(rs.getString("service"), "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\"></i>");
							 
						}else if(dblActualAmt<dblBugedtedAmt){
							/*hmProPerformaceProjectAmountIndicatorService.put(rs.getString("service"), "<img src=\"images1/icons/approved.png\" width=\"17px\">");*/
							hmProPerformaceProjectAmountIndicatorService.put(rs.getString("service"), "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\"></i>");
							
							
						}else if(dblActualAmt>dblBillableAmt){
							/*hmProPerformaceProjectAmountIndicatorService.put(rs.getString("service"), "<img src=\"images1/icons/denied.png\" width=\"17px\">");*/
							hmProPerformaceProjectAmountIndicatorService.put(rs.getString("service"), "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\"></i>");
							
						}else{
							hmProPerformaceProjectAmountIndicatorService.put(rs.getString("service"), "&nbsp;");
						}
						 
						 
						
						
						/*
						Date dtDeadline = uF.getDateFormat(hmDeadline.get(rs.getString("pro_id")), DBDATE);
						Date dtCurrentDate = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE);
						
						if(dtDeadline!=null && dtCurrentDate!=null && dtDeadline.after(dtCurrentDate)){
							if(dblActualTime<=dblBugedtedTime){
								hmProPerformaceProjectTimeIndicator.put(rs.getString("pro_id"), "<img src=\"images1/icons/approved.png\" width=\"17px\">");
							}else{
								hmProPerformaceProjectTimeIndicator.put(rs.getString("pro_id"), "<img src=\"images1/icons/re_submit.png\" width=\"17px\">");
							}
						}else if(dtDeadline!=null && dtCurrentDate!=null && dtCurrentDate.after(dtDeadline)){
							hmProPerformaceProjectTimeIndicator.put(rs.getString("pro_id"), "<img src=\"images1/icons/denied.png\" width=\"17px\">");
						}else{
							hmProPerformaceProjectTimeIndicator.put(rs.getString("pro_id"), "");
						}*/
						
						
						
						 if(!alServiceId.contains(rs.getString("service"))){
							 alServiceId.add(rs.getString("service"));
						 }
						 
						 
						 strProjectIdOld = strProjectIdNew;
					}
					rs.close();
					pst.close();
					
					
					
					
					request.setAttribute("hmProPerformaceBillableService",hmProPerformaceBillableService);
					request.setAttribute("hmProPerformaceActualService",hmProPerformaceActualService);
					request.setAttribute("hmProPerformaceBudgetService",hmProPerformaceBudgetService);
					request.setAttribute("hmProPerformaceProjectProfitServiceA",hmProPerformaceProjectProfitServiceA);
					request.setAttribute("hmProPerformaceProjectProfitServiceP",hmProPerformaceProjectProfitServiceP);
					request.setAttribute("hmProPerformaceProjectAmountIndicatorService",hmProPerformaceProjectAmountIndicatorService);
					request.setAttribute("hmProPerformaceProjectNameService",hmProPerformaceProjectNameService);
					request.setAttribute("alServiceId",alServiceId);	
					
					
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					db.closeResultSet(rs);
					db.closeStatements(pst);
					db.closeConnection(con);
				}
			
	}
	
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
	}

	public String getF_start() {
		return f_start;
	}

	public void setF_start(String f_start) {
		this.f_start = f_start;
	}

	public String getF_end() {
		return f_end;
	}

	public void setF_end(String f_end) {
		this.f_end = f_end;
	}
}
