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

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.konnect.jpms.export.DataStyle;
import com.konnect.jpms.select.FillClients;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ProfitabilityReport extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	
	HttpSession session;
	CommonFunctions CF; 
	String strUserType;
	
	List<FillOrganisation> organisationList;
	List<FillWLocation> wlocationList;
	List<FillDepartment> departmentList;
	List<FillServices> serviceList;
	List<FillClients> clientList;
	List<FillEmployee> ownerList;
	
	String f_org;
	String strWLocation;
	String strDept;
	String strService;
	String strClient;
	String strOwner;
	
	String reportType;
	String strStartDate;
	String strEndDate;
	
	public String execute() throws Exception {
		session = request.getSession();
		
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		strUserType = (String) session.getAttribute(BASEUSERTYPE);
		
		if(getF_org()==null){
			setF_org((String)session.getAttribute(ORGID));
		}
		
		UtilityFunctions uF = new UtilityFunctions();
		request.setAttribute(PAGE, "/jsp/task/ProfitabilityReport.jsp");
		request.setAttribute(TITLE, "Profitability Report");
		if(getReportType()==null) {
			setReportType("3");
		}
		
		if(getStrStartDate()==null && getStrEndDate()==null) {
			
			Date currDate=uF.getCurrentDate(CF.getStrTimeZone());
			
			String startdate="01/"+uF.getDateFormat(""+currDate, DBDATE, "MM")+"/"+uF.getDateFormat(""+currDate, DBDATE, "yyyy");
			
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(""+currDate, DBDATE, "MM"))-1);
			calendar.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(""+currDate, DBDATE, "yyyy")));
			calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));

			Date date = calendar.getTime();
			DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
			String endDate=DATE_FORMAT.format(date);
			
			setStrStartDate(startdate);
			setStrEndDate(endDate);
		}
		
		
		if(getReportType()!=null && getReportType().equals("1")){
			getProfitabilityReportClientWise(uF);
		}else if(getReportType()!=null && getReportType().equals("2")){
			getProfitabilityReportDivisionWise(uF);
		}else{ 
			getProfitabilityReport(uF);
		}
		
		loanProfitabilityReport(uF);
		
		return SUCCESS;

	}
	
	
	private void getProfitabilityReportClientWise(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		List alOuter = new ArrayList();
		PreparedStatement pst = null;
		ResultSet rs  = null;
		
		try {
			con = db.makeConnection(con);

			String proids=getProjectIdListByFilter(con,uF);
			pst = con.prepareStatement("select * from client_details");
			rs = pst.executeQuery();
			Map<String, String> hmClient = new HashMap<String, String>();
			while(rs.next()) {
				hmClient.put(rs.getString("client_id"), rs.getString("client_name"));
			}
			rs.close();
			pst.close();
			
			
			
			pst = con.prepareStatement("select sum(amount) as amount, emp_id, to_char(paid_from, 'MM-YYYY') as month from payroll_generation where earning_deduction = 'E' group by emp_id, to_char(paid_from, 'MM-YYYY')");
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmSalaryMap = new HashMap<String, String>();
			while(rs.next()) {
				hmSalaryMap.put(rs.getString("emp_id")+"_"+rs.getString("month"), rs.getString("amount"));
			}
			rs.close();
			pst.close();
			
			//pst = con.prepareStatement("select count(to_char(task_date, 'MMYYYY')::integer) as days, to_char(task_date, 'MM-YYYY') as month, ai.emp_id, pro_id from task_activity ta, activity_info ai where ai.task_id = ta.activity_id group by to_char(task_date, 'MM-YYYY'), ai.emp_id, pro_id order by ai.emp_id");
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select count(to_char(task_date, 'MMYYYY')::integer) as days, to_char(task_date, 'MM-YYYY') as month, ta.emp_id, pmt.client_id " +
					" from task_activity ta, activity_info ai,projectmntnc pmt where ai.task_id = ta.activity_id and pmt.pro_id=ai.pro_id ");
			if(proids!=null && !proids.equals("")) {
				sbQuery.append(" and ai.pro_id in ("+proids+")");
			}
			
			if(getStrStartDate()!=null && !getStrStartDate().equalsIgnoreCase("null") && !getStrStartDate().equals("") && getStrEndDate()!=null && !getStrEndDate().equalsIgnoreCase("null") && !getStrEndDate().equals("")) {
				sbQuery.append(" and to_date(task_date::text, 'YYYY-MM-DD') between '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"' ");
			}
			sbQuery.append(" group by to_char(task_date, 'MM-YYYY'), ta.emp_id, pmt.client_id order by ta.emp_id");
			
//			pst = con.prepareStatement("select count(to_char(task_date, 'MMYYYY')::integer) as days, to_char(task_date, 'MM-YYYY') as month, ai.emp_id, pmt.client_id " +
//					" from task_activity ta, activity_info ai,projectmntnc pmt where ai.task_id = ta.activity_id and pmt.pro_id=ai.pro_id group by to_char(task_date, 'MM-YYYY'), ai.emp_id, pmt.client_id order by ai.emp_id");
			pst=con.prepareStatement(sbQuery.toString());
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmActualAmountMap = new HashMap<String, String>();
			Calendar cal = GregorianCalendar.getInstance();
			
			while(rs.next()) {
				
				cal.set(Calendar.MONTH, (uF.parseToInt(uF.getDateFormat(rs.getString("month"), "MM-yyyy", "MM"))-1));
				cal.set(Calendar.YEAR, (uF.parseToInt(uF.getDateFormat(rs.getString("month"), "MM-yyyy", "yyyy"))));
				
				int nActualDays = cal.getActualMaximum(Calendar.DATE);
				
				double dblAmt = uF.parseToDouble(hmActualAmountMap.get(rs.getString("client_id")));
				double dblEmpAmtPaid = uF.parseToDouble(hmSalaryMap.get(rs.getString("emp_id")+"_"+rs.getString("month")));
				double dblDays = uF.parseToDouble(rs.getString("days"));
						
//				hmActualAmountMap.put(rs.getString("pro_id"), uF.formatIntoOneDecimalWithOutComma(((dblEmpAmtPaid * dblDays)/nActualDays)+dblAmt));
				hmActualAmountMap.put(rs.getString("client_id"), (((dblEmpAmtPaid * dblDays)/nActualDays)+dblAmt)+"");
			}
			rs.close();
			pst.close();
			
			//pst = con.prepareStatement("select received_amount, exchange_rate, pid.curr_id, pa.pro_id from promntc_invoice_details pid, promntc_bill_amt_details pa where pid.promntc_invoice_id = pa.invoice_id");
//			pst = con.prepareStatement("select received_amount, exchange_rate, pid.curr_id, pid.client_id from promntc_invoice_details pid, promntc_bill_amt_details pa where pid.promntc_invoice_id = pa.invoice_id ");
			sbQuery=new StringBuilder();
			sbQuery.append("select received_amount, exchange_rate, pid.curr_id, pid.client_id from promntc_invoice_details pid, promntc_bill_amt_details pa where pid.promntc_invoice_id = pa.invoice_id ");
			if(proids!=null && !proids.equals("")){
				sbQuery.append(" and pid.pro_id in ("+proids+")");
			}
			if(uF.parseToInt(getStrOwner())>0) {
				sbQuery.append(" and pid.pro_owner_id="+uF.parseToInt(getStrOwner()));
			}
			if(getStrStartDate()!=null && !getStrStartDate().equals("") && getStrEndDate()!=null && !getStrEndDate().equals("")) {
//				sbQuery.append(" and (invoice_from_date, invoice_to_date) overlaps (to_date('" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "'::text, 'YYYY-MM-DD'),to_date('"
//						+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "'::text, 'YYYY-MM-DD')) ");
				sbQuery.append(" and invoice_generated_date between ? and ? ");
			}
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmAmountReceived = new HashMap<String, String>();
			while(rs.next()) {
				double dblReceivedAmt = uF.parseToDouble(rs.getString("received_amount"));
				double dblExRate = uF.parseToDouble(rs.getString("exchange_rate"));
				double dblReceivedAmt1 =  0;
				if(dblExRate>0) {
					dblReceivedAmt1 = dblReceivedAmt*dblExRate;  // calculated in Local currency INR
				}
				
				double dbl = dblReceivedAmt1 + uF.parseToDouble(hmAmountReceived.get(rs.getString("client_id")));
				hmAmountReceived.put(rs.getString("client_id"), uF.formatIntoOneDecimalWithOutComma(dbl));
			}
			rs.close();
			pst.close();
			
			
			
			Map hmCurrMap = CF.getCurrencyDetails(con);
			
			
			List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
			List<DataStyle> alInnerExport = new ArrayList<DataStyle>();

			alInnerExport.add(new DataStyle("Profitability Report from "+getStrStartDate()+" - "+getStrEndDate(),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		   	alInnerExport.add(new DataStyle("Client Name",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		   	alInnerExport.add(new DataStyle("Actual Amount",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		   	alInnerExport.add(new DataStyle("Billed Amount",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		   	alInnerExport.add(new DataStyle("Amount Received",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY)); 
		   	alInnerExport.add(new DataStyle("Profitability(w.r.t. Actual)",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY)); 
			alInnerExport.add(new DataStyle("Profitability(w.r.t. Billing)",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));

			reportListExport.add(alInnerExport); 
			
			
			
			//pst = con.prepareStatement("select sum(invoice_amount) as invoice_amount, pcmc.client_id, pcmc.curr_id from projectmntnc pcmc  left join promntc_invoice_details pi  on pcmc.pro_id = pi.pro_id group  by pcmc.client_id, pcmc.curr_id");
			sbQuery=new StringBuilder();
			sbQuery.append("select sum(invoice_amount) as invoice_amount, pcmc.client_id, pcmc.curr_id from projectmntnc pcmc  " +
					"left join promntc_invoice_details pi on pcmc.pro_id = pi.pro_id where pcmc.pro_id>0 ");
			if(proids!=null && !proids.equals("")){
				sbQuery.append(" and pcmc.pro_id in ("+proids+")");
			}
			if(getStrStartDate()!=null && !getStrStartDate().equals("") && getStrEndDate()!=null && !getStrEndDate().equals("")){
//				sbQuery.append(" and (invoice_from_date, invoice_to_date) overlaps (to_date('" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "'::text, 'YYYY-MM-DD'),to_date('"
//						+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "'::text, 'YYYY-MM-DD')) ");
				sbQuery.append(" and invoice_generated_date between ? and ? ");
			}
			sbQuery.append(" group  by pcmc.client_id, pcmc.curr_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
//			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			
			
			List<String> alInner = new ArrayList<String>();
			while(rs.next()){
				if(rs.getString("client_id")==null || uF.parseToInt(rs.getString("client_id"))==0){
					continue;
				}
				
				Map hmCurr = (Map)hmCurrMap.get(rs.getString("curr_id"));
				if(hmCurr==null)hmCurr=new HashMap();
				alInner = new ArrayList<String>();
				
				alInnerExport=new ArrayList<DataStyle>();
				
				alInner.add(rs.getString("client_id"));
				alInner.add(uF.showData(hmClient.get(rs.getString("client_id")), ""));				
				
				alInnerExport.add(new DataStyle(uF.showData(hmClient.get(rs.getString("client_id")), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				 
				double dblActualAmount = uF.parseToDouble(hmActualAmountMap.get(rs.getString("client_id")));
				alInner.add(uF.formatIntoComma(dblActualAmount));
				alInnerExport.add(new DataStyle(uF.showData(uF.formatIntoComma(dblActualAmount), "0"),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				alInner.add(hmCurr.get("SHORT_CURR")+""+uF.showData(rs.getString("invoice_amount"), "0"));
				alInnerExport.add(new DataStyle(uF.showData(rs.getString("invoice_amount"), "0"),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				alInner.add(uF.showData(hmAmountReceived.get(rs.getString("client_id")), "0"));
				alInnerExport.add(new DataStyle(uF.showData(hmAmountReceived.get(rs.getString("client_id")), "0"),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				double dblRecdAmt = uF.parseToDouble(hmAmountReceived.get(rs.getString("client_id")));
				double dblInvoiceAmt = uF.parseToDouble(rs.getString("invoice_amount"));
				
				if(dblRecdAmt>0){
					alInner.add("<font color=\""+((dblInvoiceAmt>dblActualAmount)?"green":"red")+"\">"+uF.formatIntoOneDecimalWithOutComma((dblRecdAmt-dblActualAmount)* 100 /dblRecdAmt)+"%</font>");
					alInnerExport.add(new DataStyle(uF.showData(uF.formatIntoOneDecimalWithOutComma((dblRecdAmt-dblActualAmount)* 100 /dblRecdAmt), "0"),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				}else if(dblActualAmount>0){
					alInner.add("<font color=\"red\">-100%</font>");
					alInnerExport.add(new DataStyle("-100",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				}else{
					alInner.add(uF.formatIntoOneDecimalWithOutComma(0)+"%");
					alInnerExport.add(new DataStyle(uF.formatIntoOneDecimalWithOutComma(0),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				}
				
				if(dblInvoiceAmt>0){
					alInner.add("<font color=\""+((dblInvoiceAmt>dblActualAmount)?"green":"red")+"\">"+uF.formatIntoOneDecimalWithOutComma((dblInvoiceAmt-dblActualAmount)* 100 /dblInvoiceAmt)+"%</font>");
					alInnerExport.add(new DataStyle(uF.formatIntoOneDecimalWithOutComma((dblInvoiceAmt-dblActualAmount)* 100 /dblInvoiceAmt),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				}else if(dblActualAmount>0){
					alInner.add("<font color=\"red\">-100%</font>");
					alInnerExport.add(new DataStyle("-100",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				}else{
					alInner.add(uF.formatIntoOneDecimalWithOutComma(0)+"%");
					alInnerExport.add(new DataStyle(uF.formatIntoOneDecimalWithOutComma(0),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				}
				
				
				alOuter.add(alInner);
				reportListExport.add(alInnerExport);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("reportListExport",reportListExport);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		request.setAttribute("alOuter", alOuter);
	}


	private String getProjectIdListByFilter(Connection con, UtilityFunctions uF) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		String proids = "";
		try {
			if(uF.parseToInt(getStrWLocation())>0 || uF.parseToInt(getStrDept())>0 || uF.parseToInt(getStrService())>0 || uF.parseToInt(getStrClient())>0){
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select pro_id from projectmntnc where pro_id>0");
				if(uF.parseToInt(getF_org())>0) {
					sbQuery.append(" and org_id in ("+getF_org()+")");
				} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
					sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
				}
				if(uF.parseToInt(getStrWLocation())>0) {
		            sbQuery.append(" and wlocation_id ="+uF.parseToInt(getStrWLocation()));
		        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
					sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
				}
				if(uF.parseToInt(getStrDept())>0){
					sbQuery.append(" and department_id="+uF.parseToInt(getStrDept()));
				}
				if(uF.parseToInt(getStrService())>0){
					sbQuery.append(" and service='"+getStrService()+"'");
				}
				if(uF.parseToInt(getStrClient())>0){
					sbQuery.append(" and client_id="+uF.parseToInt(getStrClient()));
				}
				if(uF.parseToInt(getStrOwner())>0){
					sbQuery.append(" and added_by="+uF.parseToInt(getStrOwner()));
				}
				pst = con.prepareStatement(sbQuery.toString());
//				System.out.println("pst =======>> " + pst);
				rs = pst.executeQuery();
				int i = 0;
				while(rs.next()) {
					if(i==0) {
						proids=rs.getString("pro_id");
					} else {
						proids+=","+rs.getString("pro_id");
					}
					i++;
				}
//				System.out.println("proids ======>> " + proids);
				rs.close();
				pst.close();				
			}
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
		return proids;
	}


	private void getProfitabilityReportDivisionWise(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		List alOuter = new ArrayList();
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			con = db.makeConnection(con);
			String proids=getProjectIdListByFilter(con,uF);
			pst = con.prepareStatement("select * from services_project");
			rs = pst.executeQuery();
			Map<String, String> hmServices = new HashMap<String, String>();
			while(rs.next()){
				hmServices.put(rs.getString("service_project_id"), rs.getString("service_name"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select sum(amount) as amount, emp_id, to_char(paid_from, 'MM-YYYY') as month from payroll_generation where earning_deduction = 'E' group by emp_id, to_char(paid_from, 'MM-YYYY')");
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmSalaryMap = new HashMap<String, String>();
			while(rs.next()){
				hmSalaryMap.put(rs.getString("emp_id")+"_"+rs.getString("month"), rs.getString("amount"));
			}
			rs.close();
			pst.close();
			
//			pst = con.prepareStatement("select count(to_char(task_date, 'MMYYYY')::integer) as days, to_char(task_date, 'MM-YYYY') as month, " +
//					"ai.emp_id, pmt.service from task_activity ta, activity_info ai,projectmntnc pmt where ai.task_id = ta.activity_id " +
//					"and pmt.pro_id=ai.pro_id group by to_char(task_date, 'MM-YYYY'), ai.emp_id, pmt.service order by ai.emp_id");
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select count(to_char(task_date, 'MMYYYY')::integer) as days, to_char(task_date, 'MM-YYYY') as month, " +
					"ta.emp_id, pmt.service from task_activity ta, activity_info ai, projectmntnc pmt where ai.task_id = ta.activity_id " +
					"and pmt.pro_id=ai.pro_id ");
			if(proids!=null && !proids.equals("")){
				sbQuery.append(" and ai.pro_id in ("+proids+")");
			}
			if(getStrStartDate()!=null && !getStrStartDate().equals("") && getStrEndDate()!=null && !getStrEndDate().equals("")){
				sbQuery.append(" and to_date(task_date::text, 'YYYY-MM-DD') between '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"' ");
			}
			sbQuery.append(" group by to_char(task_date, 'MM-YYYY'), ta.emp_id, pmt.service order by ta.emp_id");
			pst=con.prepareStatement(sbQuery.toString());
			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmActualAmountMap = new HashMap<String, String>();
			Calendar cal = GregorianCalendar.getInstance();
			
			while(rs.next()){
				
				cal.set(Calendar.MONTH, (uF.parseToInt(uF.getDateFormat(rs.getString("month"), "MM-yyyy", "MM"))-1));
				cal.set(Calendar.YEAR, (uF.parseToInt(uF.getDateFormat(rs.getString("month"), "MM-yyyy", "yyyy"))));
				
				int nActualDays = cal.getActualMaximum(Calendar.DATE);
				
				double dblAmt = uF.parseToDouble(hmActualAmountMap.get(rs.getString("service")));
				double dblEmpAmtPaid = uF.parseToDouble(hmSalaryMap.get(rs.getString("emp_id")+"_"+rs.getString("month")));
				double dblDays = uF.parseToDouble(rs.getString("days"));
				
						
//				hmActualAmountMap.put(rs.getString("pro_id"), uF.formatIntoOneDecimalWithOutComma(((dblEmpAmtPaid * dblDays)/nActualDays)+dblAmt));
				hmActualAmountMap.put(rs.getString("service"), (((dblEmpAmtPaid * dblDays)/nActualDays)+dblAmt)+"");
			}
			rs.close();
			pst.close();
			
//			pst = con.prepareStatement("select received_amount, exchange_rate, pid.curr_id, pmt.service from promntc_invoice_details pid, promntc_bill_amt_details pa,projectmntnc pmt " +
//					"where pid.promntc_invoice_id = pa.invoice_id and pmt.pro_id=pa.pro_id and pmt.pro_id=pid.pro_id");
			
			sbQuery=new StringBuilder();
			sbQuery.append("select received_amount, exchange_rate, pid.curr_id, pmt.service from promntc_invoice_details pid, promntc_bill_amt_details pa,projectmntnc pmt " +
					"where pid.promntc_invoice_id = pa.invoice_id and pmt.pro_id=pa.pro_id and pmt.pro_id=pid.pro_id");
			if(proids!=null && !proids.equals("")){
				sbQuery.append(" and pid.pro_id in ("+proids+")");
			}
			if(uF.parseToInt(getStrOwner())>0){
				sbQuery.append(" and pid.pro_owner_id="+uF.parseToInt(getStrOwner()));
			}
			if(getStrStartDate()!=null && !getStrStartDate().equals("") && getStrEndDate()!=null && !getStrEndDate().equals("")){
//				sbQuery.append(" and (invoice_from_date, invoice_to_date) overlaps (to_date('" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "'::text, 'YYYY-MM-DD'),to_date('"
//						+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "'::text, 'YYYY-MM-DD')) ");
				sbQuery.append(" and invoice_generated_date between ? and ? ");
			}
			pst=con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmAmountReceived = new HashMap<String, String>();
			while(rs.next()){
				double dblReceivedAmt = uF.parseToDouble(rs.getString("received_amount"));
				double dblExRate = uF.parseToDouble(rs.getString("exchange_rate"));
				double dblReceivedAmt1 =  0;
				if(dblExRate>0){
					dblReceivedAmt1 = dblReceivedAmt*dblExRate;  // calculated in Local currency INR
				}
				
				double dbl = dblReceivedAmt1 + uF.parseToDouble(hmAmountReceived.get(rs.getString("service")));
				hmAmountReceived.put(rs.getString("service"), uF.formatIntoOneDecimalWithOutComma(dbl));
			}
			rs.close();
			pst.close();
			
			List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
			List<DataStyle> alInnerExport = new ArrayList<DataStyle>();

			alInnerExport.add(new DataStyle("Profitability Report from "+getStrStartDate()+" - "+getStrEndDate(),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		   	alInnerExport.add(new DataStyle("Division Name",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		   	alInnerExport.add(new DataStyle("Actual Amount",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		   	alInnerExport.add(new DataStyle("Billed Amount",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		   	alInnerExport.add(new DataStyle("Amount Received",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY)); 
		   	alInnerExport.add(new DataStyle("Profitability(w.r.t. Actual)",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY)); 
			alInnerExport.add(new DataStyle("Profitability(w.r.t. Billing)",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));

			reportListExport.add(alInnerExport); 
			
			
			Map hmCurrMap = CF.getCurrencyDetails(con);
			
//			pst = con.prepareStatement("select sum(invoice_amount) as invoice_amount, pcmc.service, pcmc.curr_id from projectmntnc pcmc " +
//					"left join promntc_invoice_details pi  on pcmc.pro_id = pi.pro_id group  by pcmc.service, pcmc.curr_id");
			sbQuery=new StringBuilder();
			sbQuery.append("select sum(invoice_amount) as invoice_amount, pcmc.service, pcmc.curr_id from projectmntnc pcmc " +
					"left join promntc_invoice_details pi  on pcmc.pro_id = pi.pro_id where pcmc.pro_id>0 ");
			if(proids!=null && !proids.equals("")){
				sbQuery.append(" and pcmc.pro_id in ("+proids+")");
			}
			if(getStrStartDate()!=null && !getStrStartDate().equals("") && getStrEndDate()!=null && !getStrEndDate().equals("")){
//				sbQuery.append(" and (invoice_from_date, invoice_to_date) overlaps (to_date('" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "'::text, 'YYYY-MM-DD'),to_date('"
//						+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "'::text, 'YYYY-MM-DD')) ");
				sbQuery.append(" and invoice_generated_date between ? and ? ");
			}
			sbQuery.append(" group  by pcmc.service, pcmc.curr_id");
			pst=con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
//			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			List<String> alInner = new ArrayList<String>();
			while(rs.next()){
				
				if(rs.getString("service")==null || uF.parseToInt(rs.getString("service").trim())==0){
					continue;
				}
				
				Map hmCurr = (Map)hmCurrMap.get(rs.getString("curr_id"));
				if(hmCurr==null)hmCurr=new HashMap();
				alInner = new ArrayList<String>();
				alInnerExport=new ArrayList<DataStyle>();
				
				alInner.add(rs.getString("service"));
				alInner.add(uF.showData(hmServices.get(rs.getString("service").trim()), ""));
				alInnerExport.add(new DataStyle(uF.showData(hmServices.get(rs.getString("service")), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				double dblActualAmount = uF.parseToDouble(hmActualAmountMap.get(rs.getString("service").trim()));
				alInner.add(hmCurr.get("SHORT_CURR")+" "+uF.formatIntoComma(dblActualAmount));
				alInnerExport.add(new DataStyle(uF.showData(uF.formatIntoComma(dblActualAmount), "0"),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				alInner.add(hmCurr.get("SHORT_CURR")+" "+uF.showData(rs.getString("invoice_amount"), "0"));
				alInnerExport.add(new DataStyle(uF.showData(rs.getString("invoice_amount"), "0"),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				alInner.add(hmCurr.get("SHORT_CURR")+" "+uF.showData(hmAmountReceived.get(rs.getString("service").trim()), "0"));
				alInnerExport.add(new DataStyle(uF.showData(hmAmountReceived.get(rs.getString("service")), "0"),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				double dblRecdAmt = uF.parseToDouble(hmAmountReceived.get(rs.getString("service").trim()));
				double dblInvoiceAmt = uF.parseToDouble(rs.getString("invoice_amount"));
				
				if(dblRecdAmt>0){
					alInner.add("<font color=\""+((dblInvoiceAmt>dblActualAmount)?"green":"red")+"\">"+uF.formatIntoOneDecimalWithOutComma((dblRecdAmt-dblActualAmount)* 100 /dblRecdAmt)+"%</font>");
					alInnerExport.add(new DataStyle(uF.showData(uF.formatIntoOneDecimalWithOutComma((dblRecdAmt-dblActualAmount)* 100 /dblRecdAmt), "0"),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				}else if(dblActualAmount>0){
					alInner.add("<font color=\"red\">-100%</font>");
					alInnerExport.add(new DataStyle("-100",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				}else{
					alInner.add(uF.formatIntoOneDecimalWithOutComma(0)+"%");
					alInnerExport.add(new DataStyle(uF.formatIntoOneDecimalWithOutComma(0),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				}
				
				if(dblInvoiceAmt>0){
					alInner.add("<font color=\""+((dblInvoiceAmt>dblActualAmount)?"green":"red")+"\">"+uF.formatIntoOneDecimalWithOutComma((dblInvoiceAmt-dblActualAmount)* 100 /dblInvoiceAmt)+"%</font>");
					alInnerExport.add(new DataStyle(uF.formatIntoOneDecimalWithOutComma((dblInvoiceAmt-dblActualAmount)* 100 /dblInvoiceAmt),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				}else if(dblActualAmount>0){
					alInner.add("<font color=\"red\">-100%</font>");
					alInnerExport.add(new DataStyle("-100",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				}else{
					alInner.add(uF.formatIntoOneDecimalWithOutComma(0)+"%");
					alInnerExport.add(new DataStyle(uF.formatIntoOneDecimalWithOutComma(0),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				}
				
				
				alOuter.add(alInner);
				reportListExport.add(alInnerExport);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("reportListExport",reportListExport);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		request.setAttribute("alOuter", alOuter);
	}




	private void loanProfitabilityReport(UtilityFunctions uF) {
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
			wlocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		} else {
			organisationList = new FillOrganisation(request).fillOrganisation();
			wlocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
//		wlocationList = new FillWLocation(request).fillWLocation();
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillProjectServices();
		clientList = new FillClients(request).fillAllClients(false);
		ownerList = new FillEmployee(request).fillEmployeeNameByParentLevel(0);
		getSelectedFilter(uF);
		
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
		if(getStrWLocation()!=null) {
			String strLocation="";
			int k=0;
			for(int i=0;wlocationList!=null && i<wlocationList.size();i++) {
				if(getStrWLocation().equals(wlocationList.get(i).getwLocationId())) {
					strLocation = wlocationList.get(i).getwLocationName();
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
		if(getStrDept()!=null) {
			String strDepartment="";
			int k=0;
			for(int i=0;departmentList!=null && i<departmentList.size();i++) {
				if(getStrDept().equals(departmentList.get(i).getDeptId())) {
					strDepartment=departmentList.get(i).getDeptName();
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
		
		/*alFilter.add("SERVICE");
		if(getStrDept()!=null) {
			String strSbu = "";
			for(int i=0;serviceList!=null && i<serviceList.size();i++) {
				if(getStrDept().equals(serviceList.get(i).getServiceId())) {
					strSbu=serviceList.get(i).getServiceName();
				}
			}
			if(strSbu!=null && !strSbu.equals("")) {
				hmFilter.put("SERVICE", strSbu);
			} else {
				hmFilter.put("SERVICE", "All SBUs");
			}
		} else {
			hmFilter.put("SERVICE", "All SBUs");
		}*/
		
		
		alFilter.add("PROJECT_SERVICE");
		if(getStrService()!=null) {
			String strProjectService="";
			for(int i=0;serviceList!=null && i<serviceList.size();i++) {
				if(getStrService().equals(serviceList.get(i).getServiceId())) {
					strProjectService=serviceList.get(i).getServiceName();
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
		if(getStrClient()!=null) {
			String strClient="";
			for(int i=0; clientList!=null && i<clientList.size();i++) {
				if(getStrClient().equals(clientList.get(i).getClientId())) {
					strClient=clientList.get(i).getClientName();
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
		
		alFilter.add("PROJECT_OWNER");
		if(getStrClient()!=null) {
			String strClient="";
			for(int i=0; ownerList!=null && i<ownerList.size();i++) {
				if(getStrClient().equals(ownerList.get(i).getEmployeeId())) {
					strClient=ownerList.get(i).getEmployeeName();
				}
			}
			if(strClient!=null && !strClient.equals("")) {
				hmFilter.put("PROJECT_OWNER", strClient);
			} else {
				hmFilter.put("PROJECT_OWNER", "All Owners");
			}
		} else {
			hmFilter.put("PROJECT_OWNER", "All Owners");
		}
		
		alFilter.add("REPORT_TYPE");
		if(getReportType()!=null) {
			if(uF.parseToInt(getReportType()) == 1) {
				hmFilter.put("REPORT_TYPE", "ClientWise");
			} else if(uF.parseToInt(getReportType()) == 2) {
				hmFilter.put("REPORT_TYPE", "DivisionWise");
			} else if(uF.parseToInt(getReportType()) == 3) {
				hmFilter.put("REPORT_TYPE", "ProjectWise");
			}
		}
		
		alFilter.add("PERIOD");
		String strtDate = "";
		String endDate = "";
		if(getStrStartDate() != null && !getStrStartDate().equalsIgnoreCase("") && !getStrStartDate().equals("")) {
			strtDate = uF.getDateFormat(getStrStartDate(), DATE_FORMAT, CF.getStrReportDateFormat());
		}
		if(getStrEndDate() != null && !getStrEndDate().equalsIgnoreCase("") && !getStrEndDate().equals("")) {
			endDate = uF.getDateFormat(getStrEndDate(), DATE_FORMAT, CF.getStrReportDateFormat());
		}
		hmFilter.put("PERIOD", strtDate +" - "+ endDate);
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	
	private void getProfitabilityReport(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		List alOuter = new ArrayList();
		
		PreparedStatement pst = null;
		ResultSet rs  = null;
		
		try {
		
			con = db.makeConnection(con);
			
			String proids=getProjectIdListByFilter(con,uF);
			
			pst = con.prepareStatement("select sum(amount) as amount, emp_id, to_char(paid_from, 'MM-YYYY') as month from payroll_generation where earning_deduction = 'E' group by emp_id, to_char(paid_from, 'MM-YYYY')");
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmSalaryMap = new HashMap<String, String>();
			while(rs.next()) {
				hmSalaryMap.put(rs.getString("emp_id")+"_"+rs.getString("month"), rs.getString("amount"));
			}
			rs.close();
			pst.close();
//			System.out.println("hmSalaryMap=====>"+hmSalaryMap);
			
			//pst = con.prepareStatement("select count(to_char(task_date, 'MMYYYY')::integer) as days, to_char(task_date, 'MM-YYYY') as month, ai.emp_id, pro_id from task_activity ta, activity_info ai where ai.task_id = ta.activity_id group by to_char(task_date, 'MM-YYYY'), ai.emp_id, pro_id order by ai.emp_id");
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select count(to_char(task_date, 'MMYYYY')::integer) as days, to_char(task_date, 'MM-YYYY') as month, ta.emp_id, pro_id from task_activity ta, activity_info ai where ai.task_id = ta.activity_id ");
			if(proids!=null && !proids.equals("")){
				sbQuery.append(" and ai.pro_id in ("+proids+") ");
			}
			if(getStrStartDate()!=null && !getStrStartDate().equals("") && getStrEndDate()!=null && !getStrEndDate().equals("")){
				sbQuery.append(" and to_date(task_date::text, 'YYYY-MM-DD') between '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"' ");
			}
			sbQuery.append(" group by to_char(task_date, 'MM-YYYY'), ta.emp_id, pro_id order by ta.emp_id");			
			pst=con.prepareStatement(sbQuery.toString());
			System.out.println("pst======>"+pst);
			rs = pst.executeQuery(); 
			Map<String, String> hmActualAmountMap = new HashMap<String, String>();
			Calendar cal = GregorianCalendar.getInstance();
			  
			while(rs.next()){
				
				cal.set(Calendar.MONTH, (uF.parseToInt(uF.getDateFormat(rs.getString("month"), "MM-yyyy", "MM"))-1));
				cal.set(Calendar.YEAR, (uF.parseToInt(uF.getDateFormat(rs.getString("month"), "MM-yyyy", "yyyy"))));
				
				int nActualDays = cal.getActualMaximum(Calendar.DATE);
				
				double dblAmt = uF.parseToDouble(hmActualAmountMap.get(rs.getString("pro_id")));
				double dblEmpAmtPaid = uF.parseToDouble(hmSalaryMap.get(rs.getString("emp_id")+"_"+rs.getString("month")));
				double dblDays = uF.parseToDouble(rs.getString("days"));
				
//				System.out.println("nActualDays======>"+nActualDays);
//				System.out.println("dblEmpAmtPaid======>"+dblEmpAmtPaid);
//				System.out.println("dblDays======>"+dblDays);
//				System.out.println("dblAmt======>"+dblAmt);
				
//				hmActualAmountMap.put(rs.getString("pro_id"), uF.formatIntoOneDecimalWithOutComma(((dblEmpAmtPaid * dblDays)/nActualDays)+dblAmt));
				hmActualAmountMap.put(rs.getString("pro_id"), (((dblEmpAmtPaid * dblDays)/nActualDays)+dblAmt)+"");
			}
			rs.close();
			pst.close();
//			System.out.println("hmActualAmountMap=====>"+hmActualAmountMap);
			
//			pst = con.prepareStatement("select received_amount, exchange_rate, pid.curr_id, pa.pro_id from promntc_invoice_details pid, promntc_bill_amt_details pa where pid.promntc_invoice_id = pa.invoice_id");
			sbQuery=new StringBuilder();
			sbQuery.append("select received_amount, exchange_rate, pid.curr_id, pa.pro_id from promntc_invoice_details pid, promntc_bill_amt_details pa where pid.promntc_invoice_id = pa.invoice_id ");
			if(proids!=null && !proids.equals("")){
				sbQuery.append(" and pid.pro_id in ("+proids+")");
			}
			if(uF.parseToInt(getStrOwner())>0){
				sbQuery.append(" and pid.pro_owner_id="+uF.parseToInt(getStrOwner()));
			}
			if(getStrStartDate()!=null && !getStrStartDate().equals("") && getStrEndDate()!=null && !getStrEndDate().equals("")){
//				sbQuery.append(" and (invoice_from_date, invoice_to_date) overlaps (to_date('" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "'::text, 'YYYY-MM-DD'),to_date('"
//						+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "'::text, 'YYYY-MM-DD')) ");
				sbQuery.append(" and invoice_generated_date between ? and ? ");
			}
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmAmountReceived = new HashMap<String, String>();
			while(rs.next()){
				double dblReceivedAmt = uF.parseToDouble(rs.getString("received_amount"));
				double dblExRate = uF.parseToDouble(rs.getString("exchange_rate"));
				double dblReceivedAmt1 =  0;
				if(dblExRate>0){
					dblReceivedAmt1 = dblReceivedAmt*dblExRate;  // calculated in Local currency INR
				}
				
				double dbl = dblReceivedAmt1 + uF.parseToDouble(hmAmountReceived.get(rs.getString("pro_id")));
				hmAmountReceived.put(rs.getString("pro_id"), uF.formatIntoOneDecimalWithOutComma(dbl));
			}
			rs.close();
			pst.close();
//			System.out.println("hmAmountReceived=====>"+hmAmountReceived);
			
			Map hmCurrMap = CF.getCurrencyDetails(con);
			
			List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
			List<DataStyle> alInnerExport = new ArrayList<DataStyle>();

			alInnerExport.add(new DataStyle("Profitability Report from "+getStrStartDate()+" - "+getStrEndDate(),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		   	alInnerExport.add(new DataStyle("Project Name",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		   	alInnerExport.add(new DataStyle("Actual Amount",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		   	alInnerExport.add(new DataStyle("Billed Amount",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		   	alInnerExport.add(new DataStyle("Amount Received",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY)); 
		   	alInnerExport.add(new DataStyle("Profitability(w.r.t. Actual)",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY)); 
			alInnerExport.add(new DataStyle("Profitability(w.r.t. Billing)",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));

			reportListExport.add(alInnerExport); 
			
			
//			pst = con.prepareStatement("select sum(invoice_amount) as invoice_amount, pcmc.pro_id, pcmc.curr_id, pro_name from projectmntnc pcmc left join promntc_invoice_details pi  on pcmc.pro_id = pi.pro_id group  by pcmc.pro_id, pcmc.curr_id, pro_name ");
			sbQuery=new StringBuilder();
			sbQuery.append("select sum(invoice_amount) as invoice_amount, pcmc.pro_id, pcmc.curr_id, pro_name from projectmntnc pcmc left join promntc_invoice_details pi on pcmc.pro_id = pi.pro_id where pcmc.pro_id>0 ");
			if(proids!=null && !proids.equals("")){
				sbQuery.append(" and pcmc.pro_id in ("+proids+")");
			}
			if(getStrStartDate()!=null && !getStrStartDate().equals("") && getStrEndDate()!=null && !getStrEndDate().equals("")){
//				sbQuery.append(" and (invoice_from_date, invoice_to_date) overlaps (to_date('" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "'::text, 'YYYY-MM-DD'),to_date('"
//						+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "'::text, 'YYYY-MM-DD')) ");
				sbQuery.append(" and invoice_generated_date between ? and ? ");
			}
			sbQuery.append(" group  by pcmc.pro_id, pcmc.curr_id, pro_name");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
//			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			
			
			List<String> alInner = new ArrayList<String>();
			while(rs.next()) {
				Map hmCurr = (Map)hmCurrMap.get(rs.getString("curr_id"));
				if(hmCurr==null)hmCurr=new HashMap();
				alInner = new ArrayList<String>();
				alInnerExport=new ArrayList<DataStyle>();
				
				alInner.add(rs.getString("pro_id"));
				alInner.add(rs.getString("pro_name"));
				alInnerExport.add(new DataStyle(uF.showData(rs.getString("pro_name"), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				double dblActualAmount = uF.parseToDouble(hmActualAmountMap.get(rs.getString("pro_id")));
				alInner.add(uF.formatIntoComma(dblActualAmount)); //2
				alInnerExport.add(new DataStyle(uF.showData(uF.formatIntoComma(dblActualAmount), "0"),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				alInner.add(hmCurr.get("SHORT_CURR")+""+uF.showData(uF.formatIntoComma(rs.getDouble("invoice_amount")), "0")); //3
				alInnerExport.add(new DataStyle(uF.showData(uF.formatIntoComma(rs.getDouble("invoice_amount")), "0"),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				alInner.add(uF.showData(hmAmountReceived.get(rs.getString("pro_id")), "0")); //4
				alInnerExport.add(new DataStyle(uF.showData(hmAmountReceived.get(rs.getString("pro_id")), "0"),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				double dblRecdAmt = uF.parseToDouble(hmAmountReceived.get(rs.getString("pro_id")));
				double dblInvoiceAmt = uF.parseToDouble(rs.getString("invoice_amount"));
				
				if(dblRecdAmt>0) {
					alInner.add("<font color=\""+((dblInvoiceAmt>dblActualAmount)?"green":"red")+"\">"+uF.formatIntoOneDecimalWithOutComma((dblRecdAmt-dblActualAmount)* 100 /dblRecdAmt)+"%</font>"); //5
					alInnerExport.add(new DataStyle(uF.showData(uF.formatIntoOneDecimalWithOutComma((dblRecdAmt-dblActualAmount)* 100 /dblRecdAmt), "0"),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				} else if(dblActualAmount>0) {
					alInner.add("<font color=\"red\">-100%</font>"); //5
					alInnerExport.add(new DataStyle("-100",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				} else {
					alInner.add(uF.formatIntoOneDecimalWithOutComma(0)+"%"); //5
					alInnerExport.add(new DataStyle(uF.formatIntoOneDecimalWithOutComma(0),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				}
				
				if(dblInvoiceAmt>0) {
					alInner.add("<font color=\""+((dblInvoiceAmt>dblActualAmount)?"green":"red")+"\">"+uF.formatIntoOneDecimalWithOutComma((dblInvoiceAmt-dblActualAmount)* 100 /dblInvoiceAmt)+"%</font>"); //6
					alInnerExport.add(new DataStyle(uF.formatIntoOneDecimalWithOutComma((dblInvoiceAmt-dblActualAmount)* 100 /dblInvoiceAmt),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				} else if(dblActualAmount>0) {
					alInner.add("<font color=\"red\">-100%</font>"); //6
					alInnerExport.add(new DataStyle("-100",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				} else {
					alInner.add(uF.formatIntoOneDecimalWithOutComma(0)+"%"); //6
					alInnerExport.add(new DataStyle(uF.formatIntoOneDecimalWithOutComma(0),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				}
				
				alOuter.add(alInner);
				reportListExport.add(alInnerExport);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("reportListExport",reportListExport);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		request.setAttribute("alOuter", alOuter);
	}



	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}

	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}

	public List<FillWLocation> getWlocationList() {
		return wlocationList;
	}
	public void setWlocationList(List<FillWLocation> wlocationList) {
		this.wlocationList = wlocationList;
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
	public List<FillClients> getClientList() {
		return clientList;
	}
	public void setClientList(List<FillClients> clientList) {
		this.clientList = clientList;
	}
	public List<FillEmployee> getOwnerList() {
		return ownerList;
	}
	public void setOwnerList(List<FillEmployee> ownerList) {
		this.ownerList = ownerList;
	}
	public String getStrWLocation() {
		return strWLocation;
	}
	public void setStrWLocation(String strWLocation) {
		this.strWLocation = strWLocation;
	}
	public String getStrDept() {
		return strDept;
	}
	public void setStrDept(String strDept) {
		this.strDept = strDept;
	}
	public String getStrService() {
		return strService;
	}
	public void setStrService(String strService) {
		this.strService = strService;
	}
	public String getStrClient() {
		return strClient;
	}
	public void setStrClient(String strClient) {
		this.strClient = strClient;
	}
	public String getStrOwner() {
		return strOwner;
	}
	public void setStrOwner(String strOwner) {
		this.strOwner = strOwner;
	}
	public String getReportType() {
		return reportType;
	}
	public void setReportType(String reportType) {
		this.reportType = reportType;
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

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}
		
}