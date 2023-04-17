package com.konnect.jpms.leave;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.konnect.jpms.export.DataStyle;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class RegularizeLeaveBalance  extends ActionSupport implements ServletRequestAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	private static Logger log = Logger.getLogger(RegularizeLeaveBalance.class);
	
	String f_org;
	String paycycle;
	String wLocation;
	String f_department;
	
	private File uploadFile;
	
	String updateDate;
	
	public String execute() throws Exception {
  
		session = request.getSession();  
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		strUserType = (String) session.getAttribute(USERTYPE);
		  
//		request.setAttribute(TITLE, "");
//		request.setAttribute(PAGE, "/jsp/leave/RegularizeLeaveBalance.jsp");
		 
		if(uploadFile!=null){
			importRegularizeLeaveBalance(uploadFile);
			
			return SUCCESS;
		}
		
		getLeaveCard();
		
		return LOAD;
	}
	
	private void importRegularizeLeaveBalance(File path) {
		System.out.println("import Regularize Leave Balance====");
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs =null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		List<String> alReport = new ArrayList<String>();
		
		try {
			con = db.makeConnection(con);
			
			Map<String,String> hmEmpLevel = CF.getEmpLevelMap(con);
			
			FileInputStream fis = new FileInputStream(path);
			XSSFWorkbook workbook = new XSSFWorkbook(fis);
			fis.close();
			System.out.println("Start Reading Excelsheet.... ");
			XSSFSheet attendanceSheet = workbook.getSheetAt(0);

			List<List<String>> outerList=new ArrayList<List<String>>();

			Iterator rows = attendanceSheet.rowIterator();    
			int l=0;
			while (rows.hasNext()) {

				XSSFRow row = (XSSFRow) rows.next();
				Iterator cells = row.cellIterator();
				
				if(l>1){
					List<String> cellList = new ArrayList<String>();
					while (cells.hasNext()) {
						cellList.add(cells.next().toString());
					}
					outerList.add(cellList);
				}
				l++;
			}
			
			pst=con.prepareStatement("select level_id,leave_type_name,lt.leave_type_id from emp_leave_type elt, leave_type lt where elt.leave_type_id=lt.leave_type_id");
			Map<String, String> hmLeaveType=new HashMap<String, String>();
			rs=pst.executeQuery();
			while(rs.next()){
				hmLeaveType.put(rs.getString("level_id")+"_"+rs.getString("leave_type_name"), rs.getString("leave_type_id"));
			}
			rs.close();
			pst.close();
//			System.out.println(l+"  outerList=====>"+outerList.toString());
					
			for (int k=0;k<outerList.size();k++) {
				List<String> innerList=outerList.get(k);				
				String empcode=innerList.get(1);
				String leaveType=innerList.get(3);
//				int leaveBalance=uF.parseToInt(innerList.get(4));
				double leaveBalance=uF.parseToDouble(innerList.get(4));
				if (empcode.contains(".")) {
					empcode = empcode.substring(0, empcode.indexOf("."));
				}
				int emp_per_id =0; 

				pst = con.prepareStatement("select emp_per_id,empcode,org_id,service_id,wlocation_id from employee_personal_details epd,employee_official_details eod where epd.emp_per_id=eod.emp_id and empcode=?");
				pst.setString(1,empcode.trim());
				int servic_id=0;
				int org_id=0;
				String wlocation=null;
				rs = pst.executeQuery();
				while (rs.next()) {
					emp_per_id=uF.parseToInt(rs.getString("emp_per_id"));
					org_id=uF.parseToInt(rs.getString("org_id"));
					servic_id = uF.parseToInt(rs.getString("service_id")!=null && rs.getString("service_id").split(",").length>0?rs.getString("service_id").split(",")[0]:"0");
					wlocation=rs.getString("wlocation_id");
				}
				rs.close();
				pst.close();
				
				
				if(emp_per_id==0){
					alReport.add("<li class=\"msg_error\" style=\"margin:0px\">" + empcode + "  is not available.</li>");
					continue;
				}
				
				int empLevelId=uF.parseToInt(hmEmpLevel.get(""+emp_per_id));
				int leaveTypeId=uF.parseToInt(hmLeaveType.get(empLevelId+"_"+leaveType.trim()));
				
				if(leaveTypeId>0 && leaveBalance>0){
					pst=con.prepareStatement("insert into leave_register1(emp_id,_date,balance,leave_type_id,_type)values(?,?,?,?,?)");
					pst.setInt(1, emp_per_id);
					pst.setDate(2, uF.getDateFormat(getUpdateDate(), DATE_FORMAT));
					pst.setDouble(3, leaveBalance);
					pst.setInt(4, leaveTypeId);
					pst.setString(5, "C");
//					System.out.println("pst=====>"+pst);
					pst.execute();
					pst.close();
				}  
				
			}
			request.setAttribute("alReport",alReport);
			session.setAttribute(MESSAGE, SUCCESSM+"You have successfully imported leave balance."+END);
		
		} catch (Exception e) {
			session.setAttribute(MESSAGE, ERRORM+"Leave balance imported failed."+END);
			e.printStackTrace();			
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public void getLeaveCard() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmEmpCode = CF.getEmpCodeMap(con);
			
			Map<String, String> hmLeaveType = CF.getLeaveTypeMap(con);
			Map<String, String> hmLeavesColour = new HashMap<String, String>(); 
			CF.getLeavesColour(con, hmLeavesColour);
			
			String []strDate = null;
			if(getPaycycle()!=null){
				strDate = getPaycycle().split("-");
			}else{
				strDate = CF.getCurrentPayCycleByOrg(con, CF.getStrTimeZone(), CF, getF_org());
			}
			String curr_date=uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT);
			boolean isDateBetween=uF.isDateBetween(uF.getDateFormat(strDate[0], DATE_FORMAT), uF.getDateFormat(strDate[1], DATE_FORMAT), uF.getDateFormat(curr_date, DATE_FORMAT));
			if(isDateBetween){
				setUpdateDate(curr_date);
			}else{
				setUpdateDate(strDate[1]);
			}
			pst=con.prepareStatement("select org_id,org_name from org_details");
			Map<String, String> hmOrg=new HashMap<String, String>();
			rs=pst.executeQuery();
			while(rs.next()){
				hmOrg.put(rs.getString("org_id"), rs.getString("org_name"));
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmTakenPaid = new HashMap<String, String>();
			Map<String, String> hmTakenUnPaid = new HashMap<String, String>();
			pst = con.prepareStatement("select sum(leave_no) as leave_no,leave_type_id, emp_id, is_paid from leave_application_register where _date between ? and ? group by leave_type_id, emp_id, is_paid");
			pst.setDate(1, uF.getDateFormat(strDate[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strDate[1], DATE_FORMAT));
			rs = pst.executeQuery();
		
			while(rs.next()){
				if(uF.parseToBoolean(rs.getString("is_paid"))){
					hmTakenPaid.put(rs.getString("emp_id")+"_"+rs.getString("leave_type_id"), rs.getString("leave_no"));
				}else{
					hmTakenUnPaid.put(rs.getString("emp_id")+"_"+rs.getString("leave_type_id"), rs.getString("leave_no"));
				}
			}
			rs.close();
			pst.close();
			
			List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
			List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
			
			//alInnerExport.add(new DataStyle(uF.showData(hmOrg.get(getF_org()), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Leave Balance as on "+getUpdateDate(),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			
			
			//alInnerExport.add(new DataStyle("Date- "+curr_date,Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			
		   	alInnerExport.add(new DataStyle("Sr. No.",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		   	alInnerExport.add(new DataStyle("Employee Code",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		   	alInnerExport.add(new DataStyle("Employee Name",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		   	alInnerExport.add(new DataStyle("Leave Type",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Closing Balance",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			
			reportListExport.add(alInnerExport); 
			
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select emp_id,leave_type_id,balance from leave_register1 where register_id in(select max(register_id) from leave_register1 " +
					"where _type='C' and leave_type_id in (select leave_type_id from leave_type where leave_type_id in (select leave_type_id " +
					"from emp_leave_type where is_constant_balance=false)) group by emp_id,leave_type_id)");
			if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and emp_id in (select emp_id from employee_official_details where org_id = "+uF.parseToInt(getF_org())+")");
			}
			sbQuery.append(" order by emp_id,leave_type_id");
			
			pst = con.prepareStatement(sbQuery.toString());
		    rs = pst.executeQuery();
//		    System.out.println("pst======>"+pst);
		    Map<String, String> hmMainBalance=new HashMap<String, String>();
		    Map<String, List<List<String>>> hmEmpLeaveMap=new HashMap<String, List<List<String>>>();
		    while (rs.next()) {
		        hmMainBalance.put(rs.getString("emp_id")+"_"+rs.getString("leave_type_id"), rs.getString("balance"));
		        
		        List<List<String>> outerList = hmEmpLeaveMap.get(rs.getString("emp_id"));
		        if(outerList==null) outerList = new ArrayList<List<String>>();
		        
		        List<String> innerList = new ArrayList<String>();
		        innerList.add(rs.getString("leave_type_id"));
		        
		        outerList.add(innerList);
		        
		        hmEmpLeaveMap.put(rs.getString("emp_id"), outerList);
		    }
			rs.close();
			pst.close();
//		    System.out.println("hmMainBalance======>"+hmMainBalance);
		    
		    sbQuery=new StringBuilder();
		    sbQuery.append("select sum(accrued) as accrued,a.leave_type_id,a.emp_id from " +
		    		"(select max(_date) as daa,leave_type_id,emp_id from leave_register1 where _type='C' " +
		    		"and leave_type_id in (select leave_type_id from leave_type where leave_type_id in (select leave_type_id " +
					"from emp_leave_type where is_constant_balance=false)) group by emp_id,leave_type_id)as a,leave_register1 lr " +
		    		"where  _type!='C' and a.leave_type_id=lr.leave_type_id and a.daa<=lr._date ");
		    if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and a.emp_id in (select emp_id from employee_official_details where org_id = "+uF.parseToInt(getF_org())+")");
			}
		    sbQuery.append(" group by a.emp_id,a.leave_type_id order by a.emp_id,leave_type_id");
		    
		    pst = con.prepareStatement(sbQuery.toString());
		    rs = pst.executeQuery();
//		    System.out.println("pst======>"+pst);
		    Map<String, String> hmAccruedBalance=new HashMap<String, String>();
		    while (rs.next()) {
		    	hmAccruedBalance.put(rs.getString("emp_id")+"_"+rs.getString("leave_type_id"), rs.getString("accrued"));                
		    }
			rs.close();
			pst.close();
//		    System.out.println("hmAccruedBalance======>"+hmAccruedBalance);
		    
		    sbQuery=new StringBuilder();
		    sbQuery.append("select sum(leave_no) as count,leave_type_id,emp_id from (select a.daa,lar.* from " +
		    		"(select max(_date) as daa,leave_type_id,emp_id from leave_register1 where _type='C' " +
		    		"and leave_type_id in (select leave_type_id from leave_type where leave_type_id in (select leave_type_id " +
					"from emp_leave_type where is_constant_balance=false)) group by emp_id,leave_type_id) as a,leave_application_register lar " +
		    		"where a.emp_id=lar.emp_id and is_paid=true and (is_modify is null or is_modify=false) and a.leave_type_id=lar.leave_type_id " +
		    		"and a.daa<=lar._date) as a where emp_id>0");
		    if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and emp_id in (select emp_id from employee_official_details where org_id = "+uF.parseToInt(getF_org())+")");
			}
		    sbQuery.append(" group by leave_type_id,emp_id order by emp_id,leave_type_id");
		    pst = con.prepareStatement(sbQuery.toString());
		    rs = pst.executeQuery();
//		    System.out.println("pst======>"+pst);
		    Map<String, String> hmPaidBalance=new HashMap<String, String>();
		    while (rs.next()) {
		    	hmPaidBalance.put(rs.getString("emp_id")+"_"+rs.getString("leave_type_id"), rs.getString("count"));
		    }
			rs.close();
			pst.close();
//		    System.out.println("hmPaidBalance======>"+hmPaidBalance);
			
			
		    Iterator<String> it = hmEmpLeaveMap.keySet().iterator();
		    while(it.hasNext()){
		    	String strEmpId = it.next();
		    	 List<List<String>> outerList = hmEmpLeaveMap.get(strEmpId);
		    	 
		    	 for(int i=0;outerList!=null && i<outerList.size();i++){
		    		 List<String> innerList = outerList.get(i);
		    		 String leaveTypeId = innerList.get(0);
					
					double dblOpeningBalance = uF.parseToDouble(hmMainBalance.get(strEmpId+"_"+leaveTypeId));
					dblOpeningBalance += uF.parseToDouble(hmAccruedBalance.get(strEmpId+"_"+leaveTypeId));
					
					double dblPaidBalance = uF.parseToDouble(hmPaidBalance.get(strEmpId+"_"+leaveTypeId));
					double dblClosingBalance = 0;
					if(dblOpeningBalance > 0 && dblOpeningBalance >= dblPaidBalance){
						dblClosingBalance = dblOpeningBalance - dblPaidBalance; 
			        }
					
					alInnerExport=new ArrayList<DataStyle>();
					alInnerExport.add(new DataStyle(uF.showData(""+(i+1), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(uF.showData(hmEmpCode.get(strEmpId), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));	
					alInnerExport.add(new DataStyle(uF.showData(hmEmpName.get(strEmpId), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(uF.showData(hmLeaveType.get(leaveTypeId), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(uF.showData(""+dblClosingBalance, "0"),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					
					reportListExport.add(alInnerExport);
		    	 }
		    }
			request.setAttribute("reportListExport",reportListExport);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);			
			db.closeConnection(con);
		}
	}

//	public void getLeaveCard() {
//
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//		UtilityFunctions uF = new UtilityFunctions();
//
//		try {
//			con = db.makeConnection(con);
//			
//			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
//			Map<String, String> hmEmpCode = CF.getEmpCodeMap(con);
//			
//			Map<String, String> hmLeaveType = CF.getLeaveTypeMap(con);
//			Map<String, String> hmLeavesColour = new HashMap<String, String>(); 
//			CF.getLeavesColour(con, hmLeavesColour);
//			
//			String []strDate = null;
//			if(getPaycycle()!=null){
//				strDate = getPaycycle().split("-");
//			}else{
//				strDate = CF.getCurrentPayCycle(con, CF.getStrTimeZone(), CF);
//			}
//			String curr_date=uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT);
//			boolean isDateBetween=uF.isDateBetween(uF.getDateFormat(strDate[0], DATE_FORMAT), uF.getDateFormat(strDate[1], DATE_FORMAT), uF.getDateFormat(curr_date, DATE_FORMAT));
//			if(isDateBetween){
//				setUpdateDate(curr_date);
//			}else{
//				setUpdateDate(strDate[1]);
//			}
//			pst=con.prepareStatement("select org_id,org_name from org_details");
//			Map<String, String> hmOrg=new HashMap<String, String>();
//			rs=pst.executeQuery();
//			while(rs.next()){
//				hmOrg.put(rs.getString("org_id"), rs.getString("org_name"));
//			}
//			
//			
//			
//			Map<String, String> hmMonthlyLeavePolicyMap = new HashMap<String, String>();
//			pst = con.prepareStatement("select * from emp_leave_type");
//			rs = pst.executeQuery();
//			while(rs.next()){
//				hmMonthlyLeavePolicyMap.put(rs.getString("leave_type_id")+"_"+rs.getString("level_id")+"_"+rs.getString("wlocation_id"), rs.getString("no_of_leave_monthly"));
//			}
//			
//			
//			Map<String, String> hmProbationMap = new HashMap<String, String>();
//			pst = con.prepareStatement("select * from probation_policy where probation_end_date > ? or probation_end_date is null");
//			pst.setDate(1, uF.getDateFormat(strDate[1], DATE_FORMAT));
//			rs = pst.executeQuery();
//			while(rs.next()){
//				hmProbationMap.put(rs.getString("emp_id"), rs.getString("emp_id"));
//			}
//
//			Map<String, String> hmTakenPaid = new HashMap<String, String>();
//			Map<String, String> hmTakenUnPaid = new HashMap<String, String>();
//			pst = con.prepareStatement("select sum(leave_no) as leave_no,leave_type_id, emp_id, is_paid from leave_application_register where _date between ? and ? group by leave_type_id, emp_id, is_paid");
//			pst.setDate(1, uF.getDateFormat(strDate[0], DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strDate[1], DATE_FORMAT));
//			rs = pst.executeQuery();
//		
//			while(rs.next()){
//				if(uF.parseToBoolean(rs.getString("is_paid"))){
//					hmTakenPaid.put(rs.getString("emp_id")+"_"+rs.getString("leave_type_id"), rs.getString("leave_no"));
//				}else{
//					hmTakenUnPaid.put(rs.getString("emp_id")+"_"+rs.getString("leave_type_id"), rs.getString("leave_no"));
//				}
//			}
//			
//			List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
//			List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
//			
//			//alInnerExport.add(new DataStyle(uF.showData(hmOrg.get(getF_org()), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//			alInnerExport.add(new DataStyle("Leave Balance as on "+getUpdateDate(),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//			
//			
//			//alInnerExport.add(new DataStyle("Date- "+curr_date,Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//			
//		   	alInnerExport.add(new DataStyle("Sr. No.",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//		   	alInnerExport.add(new DataStyle("Employee Code",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//		   	alInnerExport.add(new DataStyle("Employee Name",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//		   	alInnerExport.add(new DataStyle("Leave Type",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//			alInnerExport.add(new DataStyle("Closing Balance",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//			
//			reportListExport.add(alInnerExport); 
//			
//			
//			StringBuilder sbQuery = new StringBuilder();
//			sbQuery.append("select *, lr1.emp_id as emp_id1, lr1.leave_type_id as leave_type_id1 from leave_register1 lr1, (select max(_date) as mx_date, emp_id, leave_type_id from leave_register1 where _date <= ? " +
//					" and leave_type_id in (select leave_type_id from leave_type) group by emp_id, leave_type_id ) lr2 where lr1.emp_id = lr2.emp_id and lr1.leave_type_id = lr2.leave_type_id and lr1._date = lr2.mx_date and mx_date <= ? and lr1.leave_type_id in (select leave_type_id from leave_type) ");
//			if(uF.parseToInt(getwLocation())>0){
//				sbQuery.append(" and lr1.emp_id in (select emp_id from employee_official_details where wlocation_id = "+uF.parseToInt(getwLocation())+")");
//			}
//			if(uF.parseToInt(getF_department())>0){
//				sbQuery.append(" and lr1.emp_id in (select emp_id from employee_official_details where depart_id = "+uF.parseToInt(getF_department())+")");
//			}
//			sbQuery.append(" order by register_id desc, emp_id1, leave_type_id1, _date");
//			 
//			pst = con.prepareStatement(sbQuery.toString());
//			pst.setDate(1, uF.getDateFormat(strDate[1], DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strDate[1], DATE_FORMAT));
//			rs = pst.executeQuery();
//			int count=0;
//			Map<String,String> hmLeaveBalanceDate=new HashMap<String, String>();
//			while(rs.next()){
//				
//				if(uF.parseToInt(rs.getString("emp_id"))==0 || hmEmpCode.get(rs.getString("emp_id"))==null){
//					continue;
//				}
//				
//				String _date=uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT);
//				if(hmLeaveBalanceDate.get(rs.getString("emp_id")+"_"+rs.getString("leave_type_id1")+"_"+_date)!=null && hmLeaveBalanceDate.containsKey(rs.getString("emp_id")+"_"+rs.getString("leave_type_id1")+"_"+_date)){
//					continue;
//				}
//				hmLeaveBalanceDate.put(rs.getString("emp_id")+"_"+rs.getString("leave_type_id1")+"_"+_date, _date);
//				
//				count++;
//				
//				alInnerExport=new ArrayList<DataStyle>();
//				alInnerExport.add(new DataStyle(uF.showData(""+count, ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				alInnerExport.add(new DataStyle(uF.showData(hmEmpCode.get(rs.getString("emp_id")), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));	
//				alInnerExport.add(new DataStyle(uF.showData(hmEmpName.get(rs.getString("emp_id")), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				alInnerExport.add(new DataStyle(uF.showData(hmLeaveType.get(rs.getString("leave_type_id")), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				alInnerExport.add(new DataStyle(uF.showData(rs.getString("balance"), "0"),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				
//				reportListExport.add(alInnerExport);
//			}
//			request.setAttribute("reportListExport",reportListExport);
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.error(e.getClass() + ": " +  e.getMessage(), e);
//		} finally {
//			db.closeResultSet(rs);
//			db.closeStatements(pst);			
//			db.closeConnection(con);
//		}
//	}
	
	
	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public String getPaycycle() {
		return paycycle;
	}

	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
	}

	public String getwLocation() {
		return wLocation;
	}

	public void setwLocation(String wLocation) {
		this.wLocation = wLocation;
	}

	public String getF_department() {
		return f_department;
	}

	public void setF_department(String f_department) {
		this.f_department = f_department;
	}

	public File getUploadFile() {
		return uploadFile;
	}

	public void setUploadFile(File uploadFile) {
		this.uploadFile = uploadFile;
	}

	public String getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
}
