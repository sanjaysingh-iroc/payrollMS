package com.konnect.jpms.payroll.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.konnect.jpms.export.DataStyle;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.ComparatorWeight;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class CTCVeriableReport extends ActionSupport implements ServletRequestAware,ServletResponseAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	CommonFunctions CF = null;
	
	String financialYear;
	String strMonth;

	String strLocation;
	String strDepartment;
	String strSbu;
	private String strLevel;
	
	String f_org;
	String[] f_strWLocation;
	String[] f_department;
	String[] f_level;
	String[] f_service;
	
	List<FillOrganisation> orgList;
	List<FillWLocation> wLocationList;
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	
	List<FillFinancialYears> financialYearList; 
	List<FillMonth> monthList;
	List<FillServices> serviceList;
	
	public String execute() throws Exception {
		
		UtilityFunctions uF = new UtilityFunctions();
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		strUserType = (String) session.getAttribute(USERTYPE);
		 
		request.setAttribute(TITLE, "CTC Veriable Report");
		request.setAttribute(PAGE, "CTCVeriableReport.jsp");
		
		if(getF_org()==null || getF_org().trim().equals("")){
			setF_org((String)session.getAttribute(ORGID));
		}

		if(getStrLocation() != null && !getStrLocation().equals("")) {
			setF_strWLocation(getStrLocation().split(","));
		} else {
			setF_strWLocation(null);
		}
		if(getStrDepartment() != null && !getStrDepartment().equals("")) {
			setF_department(getStrDepartment().split(","));
		} else {
			setF_department(null);
		}
		if(getStrSbu() != null && !getStrSbu().equals("")) {
			setF_service(getStrSbu().split(","));
		} else {
			setF_service(null);
		}
		if(getStrLevel() != null && !getStrLevel().equals("")) {
			setF_level(getStrLevel().split(","));
		} else {
			setF_level(null);
		}
		
		viewCTCVeriableReport(uF);
		
		return loadCTCVeriableReport(uF);
	}
	
	public String loadCTCVeriableReport(UtilityFunctions uF) {
		
		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		monthList = new FillMonth().fillMonth();
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		}else{
			orgList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillServices(getF_org(), uF);
		
		getSelectedFilter(uF);
		
		return LOAD;
	}
	
	public void viewCTCVeriableReport(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			String[] strPayCycleDates = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;

			if (getFinancialYear() != null) {
				
				strPayCycleDates = getFinancialYear().split("-");
				strFinancialYearStart = strPayCycleDates[0];
				strFinancialYearEnd = strPayCycleDates[1];
			} else {
				strPayCycleDates = new FillFinancialYears(request).fillLatestFinancialYears();
				setFinancialYear(strPayCycleDates[0] + "-" + strPayCycleDates[1]);
				
				strFinancialYearStart = strPayCycleDates[0];
				strFinancialYearEnd = strPayCycleDates[1];
			}
			
			con = db.makeConnection(con);
			
			Map<String, String> hmEmpName = CF.getEmpNameMap(con,null, null);
			Map<String, String> hmEmpJoiningDate=CF.getEmpJoiningDateMap(con, uF);
			Map<String, String> hmEmpCode = CF.getEmpCodeMap(con);
			Map<String,String> hmSalaryDetails = CF.getSalaryHeadsMap(con);
			
			Map<String, String> hmSalaryWeight = new HashMap<String, String>();
			Map<String, Boolean> hmSalaryVariable = new HashMap<String, Boolean>();

			pst = con.prepareStatement("select salary_head_id, weight,is_variable from salary_details order by level_id,weight");
			rs = pst.executeQuery();
			while(rs.next()){
				if(hmSalaryWeight.get(rs.getString("salary_head_id"))==null){
					hmSalaryWeight.put(rs.getString("salary_head_id"), rs.getString("weight"));
					hmSalaryVariable.put(rs.getString("salary_head_id"),uF.parseToBoolean(rs.getString("is_variable")));
				}
			}
			rs.close();
			pst.close();
			
			pst=con.prepareStatement("select org_id,org_name from org_details");
			Map<String, String> hmOrg=new HashMap<String, String>();
			rs=pst.executeQuery();
			while(rs.next()){
				hmOrg.put(rs.getString("org_id"), rs.getString("org_name")); 
			}
			rs.close();
			pst.close();
			
			StringBuilder sbQuery = new StringBuilder();
			
			sbQuery.append("select * from payroll_generation_lta pg, employee_official_details eod where eod.emp_id = pg.emp_id and is_paid=true");
			sbQuery.append(" and financial_year_from_date='" + uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, DBDATE) + "' and financial_year_to_date='" + uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT, DBDATE) + "' ");
			
			if(uF.parseToInt(getF_org())>0){
				sbQuery.append("and eod.org_id="+uF.parseToInt(getF_org()));
			}
			
			if(getF_strWLocation()!=null && getF_strWLocation().length>0){
				sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
			}
			
			if(getF_department()!=null && getF_department().length>0){
				sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
			}
			
			if(getF_service()!=null && getF_service().length>0){
				sbQuery.append(" and (");
				for(int i=0; i<getF_service().length; i++){
					sbQuery.append(" eod.service_id like '%,"+getF_service()[i]+",%'");
					if(i<getF_service().length-1){
						sbQuery.append(" OR ");	
					}
				}
				sbQuery.append(" ) ");
				
			}
			
			if(getF_level()!=null && getF_level().length>0){
				sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") )");	
			}
			
			if((String)session.getAttribute(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			
			sbQuery.append(" order by pg.emp_id,pg.year,pg.month,earning_deduction desc, salary_head_id");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			System.out.println("pst===>"+pst);
			List<String> alEarningss = new ArrayList<String>();
			List<String> alDeductionss = new ArrayList<String>();
			
			List<ComparatorWeight> alEarnings = new ArrayList<ComparatorWeight>();
			List<ComparatorWeight> alDeductions = new ArrayList<ComparatorWeight>();
			
			Map<String,Map<String,String>> hmPayPayroll = new LinkedHashMap<String,Map<String,String>>();
			Map<String,List<String>> deptEmpMap=new LinkedHashMap<String,List<String>>();
			Map<String,Map<String,String>> empSalaryMap=new HashMap<String,Map<String,String>>();
			Map<String,String> payMothEmpMap=new LinkedHashMap<String,String>();
			
			while(rs.next()){
				List<String> empList = deptEmpMap.get(rs.getString("emp_id")+"_"+rs.getString("month"));
				if(empList==null)empList=new ArrayList<String>();
				if(!empList.contains(rs.getString("emp_id"))){
					empList.add(rs.getString("emp_id"));
				}
				deptEmpMap.put(rs.getString("emp_id")+"_"+rs.getString("month"),empList);
				payMothEmpMap.put(rs.getString("emp_id")+"_"+rs.getString("month"),uF.getMonth(uF.parseToInt(rs.getString("month")))+" "+rs.getString("year"));
				Map<String,String> salaryMap=empSalaryMap.get(rs.getString("emp_id")+"_"+rs.getString("month"));
				if(salaryMap==null)salaryMap=new HashMap<String,String>();
				salaryMap.put(rs.getString("salary_head_id"), rs.getString("amount"));
				
				empSalaryMap.put(rs.getString("emp_id")+"_"+rs.getString("month"),salaryMap);
				
				if("E".equalsIgnoreCase(rs.getString("earning_deduction")) && !alEarningss.contains(rs.getString("salary_head_id")) && hmSalaryDetails.containsKey(rs.getString("salary_head_id"))){
					boolean isvariable=hmSalaryVariable.get(rs.getString("salary_head_id"))!=null ? hmSalaryVariable.get(rs.getString("salary_head_id")) : false; 
					alEarningss.add(rs.getString("salary_head_id"));
					alEarnings.add(new ComparatorWeight(rs.getString("salary_head_id"),uF.parseToInt(hmSalaryWeight.get(rs.getString("salary_head_id"))),isvariable));
					
				}else if("D".equalsIgnoreCase(rs.getString("earning_deduction")) && !alDeductionss.contains(rs.getString("salary_head_id")) && hmSalaryDetails.containsKey(rs.getString("salary_head_id"))){
					boolean isvariable=hmSalaryVariable.get(rs.getString("salary_head_id"))!=null ? hmSalaryVariable.get(rs.getString("salary_head_id")) : false;
					alDeductionss.add(rs.getString("salary_head_id"));
					alDeductions.add(new ComparatorWeight(rs.getString("salary_head_id"),uF.parseToInt(hmSalaryWeight.get(rs.getString("salary_head_id"))),isvariable));
				}
			}
			rs.close();
			pst.close();
			
			List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
			List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
			
			alInnerExport.add(new DataStyle(uF.showData(hmOrg.get(getF_org()), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			
			alInnerExport.add(new DataStyle("CTC Veriable Report from "+strFinancialYearStart+" - "+strFinancialYearEnd,Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			
			String curr_date=uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT);
			alInnerExport.add(new DataStyle("Date- "+curr_date,Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			
		   	alInnerExport.add(new DataStyle("Sr. No.",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		   	alInnerExport.add(new DataStyle("Employee Code",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		   	alInnerExport.add(new DataStyle("Employee Name",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		   	alInnerExport.add(new DataStyle("Joining Date",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY)); 
			alInnerExport.add(new DataStyle("Pay Month",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			
			for (int ii=0; ii<alEarnings.size(); ii++){
				alInnerExport.add(new DataStyle(((String)hmSalaryDetails.get(((ComparatorWeight)alEarnings.get(ii)).getStrName()))+"(+)",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			}
			for (int ii=0; ii<alDeductions.size(); ii++){
				alInnerExport.add(new DataStyle(((String)hmSalaryDetails.get(((ComparatorWeight)alDeductions.get(ii)).getStrName()))+"(-)",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			}
			reportListExport.add(alInnerExport);
			
			Set set = deptEmpMap.keySet();			
			Iterator it = set.iterator();
			List<List<String>> alReportList = new ArrayList<List<String>>();
			Map<String,String> GrandtotalList=new HashMap<String,String>();
			int count=0;
			List<String> empMonthList=null;
			
			String oldEmp=null;
			String newEmp= null;
			Map<String,String> totalSalaryHead=new HashMap<String,String>();
			int sizeCount=0;
			
			while(it.hasNext()){
				sizeCount++;
				String deptId = (String)it.next();
				
				List<String> empList=deptEmpMap.get(deptId);
				
				for(String emp:empList){
					newEmp=emp;
					
					if(oldEmp!=null && !newEmp.equals(oldEmp)){
						List<String> alReportInner = new ArrayList<String>();
						alReportInner.add(" ");
						alReportInner.add("");
						alReportInner.add("");
						alReportInner.add("");
						alReportInner.add("<strong>Total</strong>");				
						
						if(GrandtotalList.size()==0){
							GrandtotalList.put("1","");
							GrandtotalList.put("2","");
							GrandtotalList.put("3","");
							GrandtotalList.put("4","");
							GrandtotalList.put("5","Grand Total");				
						}
						
						alInnerExport=new ArrayList<DataStyle>();
						alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
						alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));	
						alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
						alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
						alInnerExport.add(new DataStyle("Total",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
						
						int mpkey=6;
						for(int i=0; i<alEarnings.size(); i++){
							alReportInner.add("<strong>"+uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(uF.showData(totalSalaryHead.get(((ComparatorWeight)alEarnings.get(i)).getStrName()), "0")))+"</strong>");
							double a=uF.parseToDouble(uF.showData(totalSalaryHead.get(((ComparatorWeight)alEarnings.get(i)).getStrName()), "0"))+uF.parseToDouble(GrandtotalList.get(mpkey+""));
							GrandtotalList.put(mpkey+"", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),a));
							alInnerExport.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(uF.showData(totalSalaryHead.get(((ComparatorWeight)alEarnings.get(i)).getStrName()), "0"))),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
							mpkey++;
							
			    		}
						
						for(int i=0; i<alDeductions.size(); i++){
			    			alReportInner.add("<strong>"+uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(uF.showData(totalSalaryHead.get(((ComparatorWeight)alDeductions.get(i)).getStrName()), "0")))+"</strong>");
			    			alInnerExport.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(uF.showData(totalSalaryHead.get(((ComparatorWeight)alDeductions.get(i)).getStrName()), "0"))),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			    			
							double a=uF.parseToDouble(uF.showData(totalSalaryHead.get(((ComparatorWeight)alDeductions.get(i)).getStrName()), "0"))+uF.parseToDouble(GrandtotalList.get(mpkey+1+i+""));
							GrandtotalList.put(mpkey+1+i+"",uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),a));
			    		}
						
						alReportList.add(alReportInner);
						
			    		reportListExport.add(alInnerExport);
			    		
			    		totalSalaryHead=new HashMap<String,String>();
					}
					
					
					count++;
					Map<String,String> salaryMap=empSalaryMap.get(deptId);
					List<String> alReportInner = new ArrayList<String>();
					alReportInner.add(""+count);
					alReportInner.add(hmEmpCode.get(emp));
					alReportInner.add(hmEmpName.get(emp));
					alReportInner.add(uF.showData(hmEmpJoiningDate.get(emp),""));
					alReportInner.add(uF.showData(payMothEmpMap.get(deptId), ""));
					
					alInnerExport=new ArrayList<DataStyle>();
					alInnerExport.add(new DataStyle(uF.showData(""+count, ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(uF.showData(hmEmpCode.get(emp), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));	
					alInnerExport.add(new DataStyle(uF.showData(hmEmpName.get(emp), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(uF.showData(hmEmpJoiningDate.get(emp), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(uF.showData(payMothEmpMap.get(deptId), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					
					for(int i=0; i<alEarnings.size(); i++){
						double earningHead=uF.parseToDouble(salaryMap.get(((ComparatorWeight)alEarnings.get(i)).getStrName()))+uF.parseToDouble(totalSalaryHead.get(((ComparatorWeight)alEarnings.get(i)).getStrName()));
						totalSalaryHead.put(((ComparatorWeight)alEarnings.get(i)).getStrName(), earningHead+"");
						alReportInner.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(uF.showData(salaryMap.get(((ComparatorWeight)alEarnings.get(i)).getStrName()), "0"))));
						alInnerExport.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(uF.showData(salaryMap.get(((ComparatorWeight)alEarnings.get(i)).getStrName()), "0"))),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));	
					}
					
					for(int i=0; i<alDeductions.size(); i++){
		    			double earningHead=uF.parseToDouble(salaryMap.get(((ComparatorWeight)alDeductions.get(i)).getStrName()))+uF.parseToDouble(totalSalaryHead.get(((ComparatorWeight)alDeductions.get(i)).getStrName()));
		    			totalSalaryHead.put(((ComparatorWeight)alDeductions.get(i)).getStrName(), earningHead+"");
		    			alReportInner.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(uF.showData(salaryMap.get(((ComparatorWeight)alDeductions.get(i)).getStrName()), "0"))));
		    			alInnerExport.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(uF.showData(salaryMap.get(((ComparatorWeight)alDeductions.get(i)).getStrName()), "0"))),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		    		}
					
					alReportList.add(alReportInner);
					reportListExport.add(alInnerExport);
					oldEmp=newEmp;
				}
				
				if(sizeCount==deptEmpMap.size()){
					List<String> alReportInner = new ArrayList<String>();
					alReportInner.add(" ");
					alReportInner.add("");
					alReportInner.add("");
					alReportInner.add("");
					alReportInner.add("<strong>Total</strong>");				
					
					if(GrandtotalList.size()==0){
						GrandtotalList.put("1","");
						GrandtotalList.put("2","");
						GrandtotalList.put("3","");
						GrandtotalList.put("4","");
						GrandtotalList.put("5","Grand Total");				
					}
					
					alInnerExport=new ArrayList<DataStyle>();
					alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));	
					alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle("Total",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					
					int mpkey=6;
					for(int i=0; i<alEarnings.size(); i++){
						
						alReportInner.add("<strong>"+uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(uF.showData(totalSalaryHead.get(((ComparatorWeight)alEarnings.get(i)).getStrName()), "0")))+"</strong>");
						double a=uF.parseToDouble(uF.showData(totalSalaryHead.get(((ComparatorWeight)alEarnings.get(i)).getStrName()), "0"))+uF.parseToDouble(GrandtotalList.get(mpkey+""));
						GrandtotalList.put(mpkey+"", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),a));
						alInnerExport.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(uF.showData(totalSalaryHead.get(((ComparatorWeight)alEarnings.get(i)).getStrName()), "0"))),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
						mpkey++;
						
		    		}
					for(int i=0; i<alDeductions.size(); i++){
		    			alReportInner.add("<strong>"+uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(uF.showData(totalSalaryHead.get(((ComparatorWeight)alDeductions.get(i)).getStrName()), "0")))+"</strong>");
		    			alInnerExport.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(uF.showData(totalSalaryHead.get(((ComparatorWeight)alDeductions.get(i)).getStrName()), "0"))),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		    			
						double a=uF.parseToDouble(uF.showData(totalSalaryHead.get(((ComparatorWeight)alDeductions.get(i)).getStrName()), "0"))+uF.parseToDouble(GrandtotalList.get(mpkey+1+i+""));
						GrandtotalList.put(mpkey+1+i+"",uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),a));
		    		}
					
					alReportList.add(alReportInner);
					
		    		reportListExport.add(alInnerExport);
		    		
		    		totalSalaryHead=new HashMap<String,String>();
				}
			}
			
			List<String> alReportInner = new ArrayList<String>();
			
			if(GrandtotalList.size()>0){
				alInnerExport=new ArrayList<DataStyle>();
				for(int i=0;i<GrandtotalList.size();i++){
					alReportInner.add("<strong>"+GrandtotalList.get(i+1+"")+"</strong>");
					alInnerExport.add(new DataStyle(GrandtotalList.get(i+1+""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				}
				
			}
			
			if (GrandtotalList.size() > 0) {
				alReportList.add(alReportInner);
				reportListExport.add(alInnerExport);
			}
			
			request.setAttribute("reportList", alReportList);
			request.setAttribute("reportList", alReportList);
			request.setAttribute("alEarnings", alEarnings);
			request.setAttribute("alDeductions", alDeductions);
			request.setAttribute("hmSalaryDetails", hmSalaryDetails);
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("reportListExport",reportListExport);
			
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

		alFilter.add("ORGANISATION");
		if(getF_org()!=null)  {
			String strOrg="";
			int k=0;
			for(int i=0;orgList!=null && i<orgList.size();i++){
				if(getF_org().equals(orgList.get(i).getOrgId())) {
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
				hmFilter.put("LEVEL", "All Level's");
			}
		} else {
			hmFilter.put("LEVEL", "All Level's");
		}
		
		alFilter.add("FINANCIALYEAR");
		String[] strFinancialYears = null;
		if (getFinancialYear() != null) {
			strFinancialYears = getFinancialYear().split("-");
			setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
		} else {
			strFinancialYears = CF.getFinancialYear(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), CF, uF);
			setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
		}
		hmFilter.put("FINANCIALYEAR", uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(strFinancialYears[1], DATE_FORMAT, CF.getStrReportDateFormat()));
		
		/*alFilter.add("MONTH");
		int nselectedMonth = uF.parseToInt(getStrMonth());
		String strMonth = uF.getMonth(nselectedMonth);
		hmFilter.put("MONTH", strMonth);*/
		
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	private HttpServletResponse response;

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	public String getFinancialYear() {
		return financialYear;
	}

	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}

	public String getStrMonth() {
		return strMonth;
	}

	public void setStrMonth(String strMonth) {
		this.strMonth = strMonth;
	}

	public String getStrLocation() {
		return strLocation;
	}

	public void setStrLocation(String strLocation) {
		this.strLocation = strLocation;
	}

	public String getStrDepartment() {
		return strDepartment;
	}

	public void setStrDepartment(String strDepartment) {
		this.strDepartment = strDepartment;
	}

	public String getStrSbu() {
		return strSbu;
	}

	public void setStrSbu(String strSbu) {
		this.strSbu = strSbu;
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

	public String[] getF_level() {
		return f_level;
	}

	public void setF_level(String[] f_level) {
		this.f_level = f_level;
	}

	public String[] getF_service() {
		return f_service;
	}

	public void setF_service(String[] f_service) {
		this.f_service = f_service;
	}

	public List<FillOrganisation> getOrgList() {
		return orgList;
	}

	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
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

	public List<FillServices> getServiceList() {
		return serviceList;
	}

	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
	}

	public String getStrLevel() {
		return strLevel;
	}

	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}
	
}
