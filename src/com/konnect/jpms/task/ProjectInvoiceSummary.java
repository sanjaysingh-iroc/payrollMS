package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

public class ProjectInvoiceSummary extends ActionSupport implements
		ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strSessionEmpId;
	int pro_id;
	CommonFunctions CF;
	
	public String execute() {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) return LOGIN;
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		UtilityFunctions uF = new UtilityFunctions();
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		
		try {
			con = db.makeConnection(con);
			
			getProjectInvoiceSumary(con, uF, CF);
			
			if(getSave()!=null){
				return  updateInfo();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeConnection(con);
		}

		return SUCCESS;
	}

	
	String []modifiedActualTime;
	String []taskId;
	String []billableAmount1;
	String []billableRate1;
	
	String saveDraft;
	String save;
	String paycycle;
	
	public String updateInfo(){
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			int i=0;
			for(i=0; getModifiedActualTime()!=null && getModifiedActualTime().length>0 && getTaskId()!=null && i<getTaskId().length; i++){
				
//				pst = con.prepareStatement("update activity_info set invoice_time =? where task_id =? ");
//				pst.setDouble(1, uF.parseToDouble(getModifiedActualTime()[i]));
//				pst.setInt(2, uF.parseToInt(getTaskId()[i]));
//				pst.addBatch();
				
				
				String []arrPaycycle = {"","",""};
				if(getPaycycle()!=null){
					arrPaycycle = getPaycycle().split("-");
				}
				
				pst = con.prepareStatement("insert into project_invoice_details (invoice_generated_date, invoice_generated_by, invoice_time, invoice_amount, invoice_from_date, invoice_to_date, invoice_paycycle, task_id, invoice_rate, pro_id,invoice_code) values (?,?,?,?,?,?,?,?,?,?,?) ");
				pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(2, uF.parseToInt(strSessionEmpId));
				pst.setDouble(3, uF.parseToDouble(getModifiedActualTime()[i]));
				pst.setDouble(4, uF.parseToDouble(getBillableAmount1()[i]));
				pst.setDate(5, uF.getDateFormat(arrPaycycle[0], DATE_FORMAT));
				pst.setDate(6, uF.getDateFormat(arrPaycycle[1], DATE_FORMAT));
				pst.setInt(7, uF.parseToInt(arrPaycycle[2]));
				pst.setInt(8, uF.parseToInt(getTaskId()[i]));
				pst.setDouble(9, uF.parseToDouble(getBillableRate1()[i]));
				pst.setInt(10, getPro_id());
				pst.setString(11, getPro_id()+"-"+uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "MM")+uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "yy"));
//				pst.addBatch();
				pst.execute();
				pst.close();
				
//				System.out.println("pst========>"+pst);
			}
			if(i>0){
				pst.executeBatch();
			}
			
			/* 
			if(getSave()!=null){
				pst = con.prepareStatement("update projectmntnc set is_invoice_validated =true where pro_id =? ");
				pst.setInt(1, pro_id);
				
				pst.execute();
			}
			*/
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return "save";
	}
	
	public void getProjectInvoiceSumary(Connection con, UtilityFunctions uF, CommonFunctions CF){
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
			String []arrPaycycle = {"","",""};
			if(getPaycycle()!=null){
				arrPaycycle = getPaycycle().split("-");
			}
			
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null); 
			
			Map<String, String> hmEmpHourlyGrossAmount = CF.getEmpGrossSalary(uF, CF, con, uF.getCurrentDate(CF.getStrTimeZone())+"", "H");
			Map<String, String> hmEmpDailyGrossAmount = CF.getEmpGrossSalary(uF, CF, con, uF.getCurrentDate(CF.getStrTimeZone())+"", "D");
			
			
			Map<String, String> hmServices = new HashMap();
			pst = con.prepareStatement("select * from services_project");
			rs = pst.executeQuery();
			while(rs.next()){
				hmServices.put(rs.getString("service_project_id"), rs.getString("service_name"));
			}
			rs.close();
			pst.close();
			
			
			
			pst = con.prepareStatement("select sum(variable_cost) as variable_cost from variable_cost where pro_id = ? ");
			pst.setInt(1, getPro_id());
			rs = pst.executeQuery();
			String strVariableCost = null;
			while(rs.next()){
				strVariableCost = rs.getString("variable_cost");
			}
			rs.close();
			pst.close();
			
			
			
			pst = con.prepareStatement("select * from activity_info where pro_id = ? ");
			pst.setInt(1, getPro_id());
			rs = pst.executeQuery();
			Map<String, String> hmEmpRates = new HashMap<String, String>();
			while(rs.next()){
				hmEmpRates.put(rs.getString("emp_id"), rs.getString("billable_rate"));
			}
			rs.close();
			pst.close();
			
			
			
			pst = con.prepareStatement("select * from task_activity ta, activity_info ai, projectmntnc pcmc where ta.activity_id = ai.task_id and pcmc.pro_id = ai.pro_id  and task_date between ? and ? order by ta.task_id");
			pst.setDate(1, uF.getDateFormat(arrPaycycle[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(arrPaycycle[1], DATE_FORMAT));
			rs = pst.executeQuery();
			Map<String, String> hmWorkedHours = new HashMap<String, String>();
			List<String> alDays =  new ArrayList<String>();
			String strTaskIdNew=null;
			String strTaskIdOld=null;
			while(rs.next()){
				strTaskIdNew = rs.getString("task_id");
				if(strTaskIdNew!=null && !strTaskIdNew.equalsIgnoreCase(strTaskIdOld)){
					alDays =  new ArrayList<String>();
				}
				
				double dblWorked = 0;
				if(rs.getString("billing_type").equalsIgnoreCase("D")){
					if(!alDays.contains(rs.getString("task_date"))){
						dblWorked = 1;
						alDays.add(rs.getString("task_date"));
					}					
				}else{
					dblWorked = uF.parseToDouble(rs.getString("actual_hrs"));
				}
				dblWorked += uF.parseToDouble(hmWorkedHours.get(rs.getString("activity_id")));
				hmWorkedHours.put(rs.getString("activity_id"), dblWorked+"");
				strTaskIdOld = strTaskIdNew;
			}
			rs.close();
			pst.close();
			
			
			
			pst = con.prepareStatement("select * from ( select ai.task_id,ai.invoice_time, pmc.billing_type, pmc.actual_calculation_type, pmc.pro_name, ai.already_work, ai.already_work_days, ai.emp_id,activity_name, service, ai.idealtime, pmc.pro_id  from projectmntnc pmc, activity_info ai where ai.pro_id = pmc.pro_id ) a, project_cost pc where pc.pro_id = a.pro_id and  a.pro_id = ? ");
			pst.setInt(1, getPro_id());
			rs = pst.executeQuery();
			
			List alOuter = new ArrayList();
			List<String> alInner = new ArrayList<String>();
			
			
			double dblIdealTimeTotal = 0;
			double dblBudgetedCostTotal = 0;
			double dblBillableCostTotal = 0;
			double dblAlreadyWorkTotal = 0; 
			int count=0;
			String strBillingType = null;
//			String strBillingType1 = null;
			while(rs.next()){
				
				strBillingType = rs.getString("billing_type");
//				strBillingType = rs.getString("actual_calculation_type");
				alInner = new ArrayList<String>();
				
				
				double dblIdealTime = uF.parseToDouble(uF.roundOffInTimeInHoursMins(uF.parseToDouble(rs.getString("idealtime"))));
				double dblHourlyRate = uF.parseToDouble(((strBillingType!=null && strBillingType.equalsIgnoreCase("D"))?hmEmpDailyGrossAmount.get(rs.getString("emp_id")):hmEmpHourlyGrossAmount.get(rs.getString("emp_id"))));
				double dblAlreadyWork = 0;	
				
				double dblBudgetedCost = dblIdealTime * dblHourlyRate;
				
				double dblBillableAmount = uF.parseToDouble(rs.getString("billable_amount"));
				
				dblIdealTimeTotal +=dblIdealTime;
				dblBudgetedCostTotal +=dblBudgetedCost;
				
				if(strBillingType!=null && strBillingType.equalsIgnoreCase("F")){
					dblBillableCostTotal=dblBillableAmount;
				}else{
					dblBillableCostTotal+=dblBillableAmount;
				}
				
				
				alInner.add(rs.getString("activity_name"));
				alInner.add(hmEmpName.get(rs.getString("emp_id")));
				alInner.add(hmServices.get(rs.getString("service")));
				alInner.add(uF.formatIntoTwoDecimal(dblIdealTime));
				
				if(strBillingType!=null && strBillingType.equalsIgnoreCase("D")){
					if(uF.parseToDouble(rs.getString("invoice_time"))>=0){
						dblAlreadyWork = uF.parseToDouble(rs.getString("invoice_time"));
					}else{
//						dblAlreadyWork = uF.parseToDouble(rs.getString("already_work_days"));
						dblAlreadyWork = uF.parseToDouble(hmWorkedHours.get(rs.getString("task_id")));
						
					}
					alInner.add("<input type=\"hidden\" name=\"taskId\" value=\""+rs.getString("task_id")+"\" /><input onkeyup=\"calculate()\" type=\"text\" style=\"width:60px;text-align:right;\" value=\""+uF.formatIntoTwoDecimal(dblAlreadyWork)+"\" name=\"modifiedActualTime\">");
					dblAlreadyWork += uF.parseToDouble(rs.getString("already_work_days"));
				}else if(strBillingType!=null && strBillingType.equalsIgnoreCase("H")){
					if(uF.parseToDouble(rs.getString("invoice_time"))>=0){
						dblAlreadyWork = uF.parseToDouble(rs.getString("invoice_time"));
					}else{
						dblAlreadyWork = uF.parseToDouble(rs.getString("already_work"));
					}
					alInner.add("<input type=\"hidden\" name=\"taskId\" value=\""+rs.getString("task_id")+"\" /><input onkeyup=\"calculate()\" type=\"text\" style=\"width:60px;text-align:right;\" value=\""+uF.formatIntoTwoDecimal(dblAlreadyWork)+"\" name=\"modifiedActualTime\">");
					dblAlreadyWorkTotal += dblAlreadyWork;
				}else if(strBillingType!=null && strBillingType.equalsIgnoreCase("F")){
					
					if(uF.parseToDouble(rs.getString("invoice_time"))>=0){
						dblAlreadyWork = uF.parseToDouble(rs.getString("invoice_time"));
					}else{
						dblAlreadyWork = uF.parseToDouble(rs.getString("already_work"));
					}
					alInner.add("<input type=\"hidden\" name=\"taskId\" value=\""+rs.getString("task_id")+"\" />"+uF.formatIntoTwoDecimal(dblAlreadyWork)+"<input type=\"hidden\" value=\""+uF.formatIntoTwoDecimal(dblAlreadyWork)+"\" name=\"modifiedActualTime\">");
					dblAlreadyWorkTotal += dblAlreadyWork;
				}
				
				alInner.add(uF.formatIntoOneDecimal(dblHourlyRate));
				alInner.add(uF.formatIntoOneDecimal(dblBudgetedCost));
				
				
				alInner.add("<input type=\"hidden\" name=\"billableRate1\" value=\""+uF.formatIntoOneDecimalWithOutComma(uF.parseToDouble(hmEmpRates.get(rs.getString("emp_id"))))+"\" \">"+"<div name=\"billableRate\">"+uF.formatIntoOneDecimalWithOutComma(uF.parseToDouble(hmEmpRates.get(rs.getString("emp_id"))))+"</div>");
				if(strBillingType!=null && strBillingType.equalsIgnoreCase("F")){
					alInner.add("Fixed<input type=\"hidden\" name=\"billableAmount1\" value=\""+uF.formatIntoOneDecimal(dblBillableAmount)+"\" >");
				}else{
					alInner.add("<input type=\"hidden\" name=\"billableAmount1\"  ><div name=\"billableAmount\">"+uF.formatIntoOneDecimal(dblBillableAmount)+"</div>");
				}
				
				alOuter.add(alInner);
				count++;
				request.setAttribute("PROJECT_NAME", rs.getString("pro_name"));
			}
			rs.close();
			pst.close();
		//	if(strVariableCost!=null){
				dblBudgetedCostTotal += uF.parseToDouble(strVariableCost);
				alInner = new ArrayList<String>();
				alInner.add("<b>Total Variable Cost</b>");
				alInner.add("");
				alInner.add("");
				alInner.add("");
				alInner.add("");
				alInner.add("");
				alInner.add(uF.formatIntoOneDecimal(uF.parseToDouble(strVariableCost)));
				alInner.add("");
				alInner.add("");
				alOuter.add(alInner);
			//}
			
			alInner = new ArrayList<String>();
			alInner.add("<b>Total</b>");
			alInner.add("");
			alInner.add("");
			alInner.add(uF.roundOffInTimeInHoursMins(dblIdealTimeTotal));
			
			if(strBillingType!=null && strBillingType.equalsIgnoreCase("F")){
				alInner.add(uF.formatIntoTwoDecimal(dblAlreadyWorkTotal));
			}else{
				alInner.add("<input type=\"text\" style=\"width:60px;text-align:right;\" value=\""+uF.formatIntoTwoDecimal(dblAlreadyWorkTotal)+"\" name=\"modifiedActualTimeTotal\">");
			}
			
			alInner.add("");
			alInner.add("<div id=\"totalBudgeted\">"+uF.formatIntoOneDecimalWithOutComma(dblBudgetedCostTotal)+"</div>");
			alInner.add(""); // for html puprose only
			alInner.add("<div name=\"modifiedBillableAmountTotal\">"+uF.formatIntoOneDecimal(dblBillableCostTotal)+"</div>");
			alOuter.add(alInner);
			
			
			
			List alProfitSummary = new ArrayList();
			
			alProfitSummary.add(uF.formatIntoOneDecimal(dblBillableCostTotal - dblBudgetedCostTotal));
			if(dblBudgetedCostTotal>0){
				alProfitSummary.add(uF.formatIntoOneDecimal((dblBillableCostTotal - dblBudgetedCostTotal)/dblBudgetedCostTotal)+"%");
			}else{
				alProfitSummary.add("0%");
			}
			
			request.setAttribute("alOuter", alOuter);
			request.setAttribute("alProfitSummary", alProfitSummary);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
		
	}

	public int getPro_id() {
		return pro_id;
	}

	public void setPro_id(int pro_id) {
		this.pro_id = pro_id;
	}

	public String[] getModifiedActualTime() {
		return modifiedActualTime;
	}

	public void setModifiedActualTime(String[] modifiedActualTime) {
		this.modifiedActualTime = modifiedActualTime;
	}

	public String[] getTaskId() {
		return taskId;
	}

	public void setTaskId(String[] taskId) {
		this.taskId = taskId;
	}

	public String getSaveDraft() {
		return saveDraft;
	}

	public void setSaveDraft(String saveDraft) {
		this.saveDraft = saveDraft;
	}

	public String getSave() {
		return save;
	}

	public void setSave(String save) {
		this.save = save;
	}

	public String[] getBillableAmount1() {
		return billableAmount1;
	}

	public void setBillableAmount1(String[] billableAmount1) {
		this.billableAmount1 = billableAmount1;
	}

	public String[] getBillableRate1() {
		return billableRate1;
	}

	public void setBillableRate1(String[] billableRate1) {
		this.billableRate1 = billableRate1;
	}

	public String getPaycycle() {
		return paycycle;
	}

	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
	}
}
