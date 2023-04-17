package com.konnect.jpms.reports;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.konnect.jpms.export.DataStyle;
import com.konnect.jpms.export.payroll.ExcelSheetDesign;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

/**
 * @author user
 *
 */
public class ClientConveyanceReport  extends ActionSupport implements ServletRequestAware,ServletResponseAware, IStatements {
	
	private static final long serialVersionUID = 1L; 
	HttpSession session;
	String strUserType;
	String strEmpId;
	
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	private static Logger log = Logger.getLogger(ClientConveyanceReport.class);
	
	List<FillPayCycles> payCycleList;
	String paycycle;
	String type;
	
	List<FillOrganisation> orgList;	
	List<FillWLocation> wLocationList;
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;	
	String[] f_strWLocation;
	String[] f_department;
	String[] f_level;
	String f_org;
	private String strLocation;
	private String strDepartment;
	private String strLevel;
	 
	public String execute() throws Exception {
		UtilityFunctions uF = new UtilityFunctions();
		session = request.getSession();  
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		strUserType = (String) session.getAttribute(USERTYPE);
		strEmpId =(String) session.getAttribute(EMPID);
		
		request.setAttribute(PAGE, "/jsp/reports/ClientConveyanceReport.jsp");
		request.setAttribute(TITLE, "Client Conveyance Report");
		request.setAttribute("roundOffCondition", ""+uF.parseToInt(CF.getRoundOffCondtion()));
		
//		boolean isView  = CF.getAccess(session, request, uF);
//		if(!isView){
//			request.setAttribute(PAGE, PAccessDenied);
//			request.setAttribute(TITLE, TAccessDenied); 
//			return ACCESS_DENIED;
//		}

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
		if(getStrLevel() != null && !getStrLevel().equals("")) {
			setF_level(getStrLevel().split(","));
		} else {
			setF_level(null);
		}
		
		getClientConveyanceReport(uF);
		
		if(getType()!=null && getType().equals("excel")){
			generateClientConveyanceExcelReport(uF);
		}

		return loadClientConveyanceReport(uF);

	}
	
	private void generateClientConveyanceExcelReport(UtilityFunctions uF) {
		Database db=new Database();
		db.setRequest(request);
		Connection con=null;
		try {
			
			String[] strPayCycleDates = null;
			if (getPaycycle() != null) {
				strPayCycleDates = getPaycycle().split("-");
			} else {
				strPayCycleDates = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF, request);
				setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1]+ "-" + strPayCycleDates[2]);
			}
			
			con=db.makeConnection(con);

			Map<String, String> hmEmpNameMap = CF.getEmpNameMap(con,null, null);
			List<String> clientList =(List<String>)request.getAttribute("clientList");
			List<String> empList =(List<String>)request.getAttribute("empList");
			Map<String, String> hmClient=(Map<String, String>)request.getAttribute("hmClient");
			Map<String, String> hmAmount=(Map<String, String>)request.getAttribute("hmAmount");
			Map<String, String> hmBillable=(Map<String, String>)request.getAttribute("hmBillable");
			Map<String, String> hmClientAmount=(Map<String, String>)request.getAttribute("hmClientAmount");


			Map<String, Map<String, String>> hmConveyanceAmount=(Map<String, Map<String, String>>)request.getAttribute("hmConveyanceAmount");
			Map<String, String> hmDept =(Map<String, String>)request.getAttribute("hmDept");
			Map<String, String> hmEmpAmount=(Map<String, String>)request.getAttribute("hmEmpAmount");
			
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("Client Conveyance Report");			
			
			List<DataStyle> header=new ArrayList<DataStyle>();
			header.add(new DataStyle("Client Conveyance Report ("+strPayCycleDates[0] + "-" + strPayCycleDates[1]+")",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Sr.No.",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Client",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Department",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Chargeable Y/N",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Total",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			
			for(int i=0;empList!=null && i<empList.size();i++){ 
				header.add(new DataStyle(hmEmpNameMap.get(empList.get(i).trim()),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			}
	
		
		List<List<DataStyle>> reportData=new ArrayList<List<DataStyle>>();
		Map<String,String> hmEmpTotal=new LinkedHashMap<String, String>();
		int i=0;
		Iterator<String> it=hmConveyanceAmount.keySet().iterator();
		double clientTotalAmount=0;
		while(it.hasNext()){
			String key=it.next();
			Map<String, String> hmInner=hmConveyanceAmount.get(key);
			i++;
			clientTotalAmount+=uF.parseToDouble(hmClientAmount.get(hmInner.get("CLIENT_ID")+"_"+hmInner.get("DEPART_ID")+"_"+hmInner.get("IS_BILLABLE")));
						
			List<DataStyle> innerList=new ArrayList<DataStyle>();
			innerList.add(new DataStyle(""+(i),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			innerList.add(new DataStyle(hmClient.get(hmInner.get("CLIENT_ID")),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			innerList.add(new DataStyle(hmDept.get(hmInner.get("DEPART_ID")),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			innerList.add(new DataStyle(hmInner.get("IS_BILLABLE"),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			innerList.add(new DataStyle(uF.showData(hmClientAmount.get(hmInner.get("CLIENT_ID")+"_"+hmInner.get("DEPART_ID")+"_"+hmInner.get("IS_BILLABLE")),"0.00"),Element.ALIGN_RIGHT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			
						
			for(int j=0;empList!=null && j<empList.size();j++){ 
				innerList.add(new DataStyle(uF.showData(hmAmount.get(hmInner.get("CLIENT_ID")+"_"+hmInner.get("DEPART_ID")+"_"+hmInner.get("IS_BILLABLE")+"_"+empList.get(j)),""),Element.ALIGN_RIGHT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			}
			
			reportData.add(innerList);
				
		}
		
		
		List<DataStyle> innerList=new ArrayList<DataStyle>();
		innerList.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		innerList.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		innerList.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		innerList.add(new DataStyle("Total",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		innerList.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),clientTotalAmount),Element.ALIGN_RIGHT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		
		for(int j=0;empList!=null && j<empList.size();j++){ 
			innerList.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmEmpAmount.get(empList.get(j)))) ,Element.ALIGN_RIGHT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
		}
		
		reportData.add(innerList);
		
		ExcelSheetDesign sheetDesign=new ExcelSheetDesign();
		sheetDesign.getExcelSheetDesignData(workbook,sheet,header,reportData);
		
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		workbook.write(buffer);
		response.setContentType("application/vnd.ms-excel:UTF-8");
		response.setContentLength(buffer.size());
		response.setHeader("Content-Disposition","attachment; filename=ClientConveyanceExcelReport.xls");
		ServletOutputStream out = response.getOutputStream();
		buffer.writeTo(out);
		out.flush();
		buffer.close();
		out.close();
		
	}catch (Exception e) {
		e.printStackTrace();
	} finally {
		db.closeConnection(con);
	}
	
}
	
	private void getClientConveyanceReport(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			String[] strPayCycleDates = null;
			if (getPaycycle() != null) {
				strPayCycleDates = getPaycycle().split("-");
			} else {
				strPayCycleDates = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF,request);
				setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1]+ "-" + strPayCycleDates[2]);
			}
			con = db.makeConnection(con);
			
			Map<String, String> hmDept =CF.getDeptMap(con);
			
			Map<String, String> hmClient=new HashMap<String, String>();
			pst = con.prepareStatement("select * from client_details");
			rs = pst.executeQuery();
			while (rs.next()) {
				hmClient.put(rs.getString("client_id"), rs.getString("client_name"));
			}
			rs.close();
			pst.close();
			
			List<String> clientList = new ArrayList<String>();
			List<String> empList = new ArrayList<String>();
			Map<String, String> hmAmount=new HashMap<String, String>();
			Map<String, String> hmBillable=new HashMap<String, String>();
			Map<String, String> hmClientAmount=new HashMap<String, String>();
			Map<String, String> hmEmpAmount=new HashMap<String, String>();
			
			
			StringBuilder sbQuery=new StringBuilder();			
			sbQuery.append("select er.client_id,er.emp_id,eod.depart_id,er.is_billable,sum(er.reimbursement_amount) as reimbursement_amount " +
				" from emp_reimbursement er,employee_official_details eod where (er.from_date, er.to_date) " +
				"overlaps (to_date(?::text, 'YYYY-MM-DD'),to_date(?::text, 'YYYY-MM-DD')) and er.client_id > 0 and er.approval_1=1 and er.approval_2=1 " + //and er.ispaid=true 
				" and er.emp_id=eod.emp_id ");
			if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)) {
				sbQuery.append(" and eod.supervisor_emp_id = "+ uF.parseToInt(strEmpId) +" ");
			}
			if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and eod.org_id="+uF.parseToInt(getF_org()));
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
                sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
            } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			if(getF_department()!=null && getF_department().length>0){
                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
            }
			if(getF_level()!=null && getF_level().length>0){
                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
            }
			sbQuery.append(" group by er.client_id,er.emp_id,eod.depart_id,er.is_billable order by er.client_id,eod.depart_id");
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			Map<String, Map<String, String>> hmConveyanceAmount=new LinkedHashMap<String, Map<String, String>>();
			while (rs.next()) {
				
				if(!empList.contains(rs.getString("emp_id"))){
					empList.add(rs.getString("emp_id"));
				}
				
				String isBillable="N";
				if(uF.parseToBoolean(rs.getString("is_billable"))){
					isBillable="Y";
				}
				//er.client_id,er.emp_id,eod.depart_id,er.is_billable,sum(er.reimbursement_amount) as reimbursement_amount
				double clientAmount=uF.parseToDouble(hmClientAmount.get(rs.getString("client_id")+"_"+rs.getString("depart_id")+"_"+isBillable));
				if(clientAmount==0){
					clientAmount=uF.parseToDouble(rs.getString("reimbursement_amount"));
				}
				else{
					clientAmount+=uF.parseToDouble(rs.getString("reimbursement_amount"));;
				}
				
				hmClientAmount.put(rs.getString("client_id")+"_"+rs.getString("depart_id")+"_"+isBillable, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),clientAmount));
				
				double empAmount=uF.parseToDouble(hmEmpAmount.get(rs.getString("emp_id")));
				if(empAmount==0){
					empAmount=uF.parseToDouble(rs.getString("reimbursement_amount"));
				}
				else{
					empAmount+=uF.parseToDouble(rs.getString("reimbursement_amount"));;
				}
				
				hmEmpAmount.put(rs.getString("emp_id"), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),empAmount));
				
				Map<String, String> hmInner=hmConveyanceAmount.get(rs.getString("client_id")+"_"+rs.getString("depart_id")+"_"+isBillable);
				if(hmInner==null) hmInner=new HashMap<String, String>();
				hmInner.put("CLIENT_ID", rs.getString("client_id"));
				hmInner.put("DEPART_ID", rs.getString("depart_id"));
				hmInner.put("EMP_ID", rs.getString("emp_id"));
				hmInner.put("IS_BILLABLE", isBillable);
				double amount=uF.parseToDouble(rs.getString("reimbursement_amount"));
				hmInner.put("AMOUNT", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),amount));
				hmConveyanceAmount.put(rs.getString("client_id")+"_"+rs.getString("depart_id")+"_"+isBillable,hmInner);
				
				hmAmount.put(rs.getString("client_id")+"_"+rs.getString("depart_id")+"_"+isBillable+"_"+rs.getString("emp_id"), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),amount));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("clientList", clientList);
			request.setAttribute("empList", empList);
			request.setAttribute("hmClient", hmClient);
			request.setAttribute("hmAmount", hmAmount);
			request.setAttribute("hmBillable", hmBillable);
			request.setAttribute("hmClientAmount", hmClientAmount);
			
			request.setAttribute("hmDept", hmDept);
			request.setAttribute("hmEmpAmount", hmEmpAmount);
			request.setAttribute("hmConveyanceAmount", hmConveyanceAmount);
			
			Map<String, String> hmEmpNameMap = CF.getEmpNameMap(con,null, null);
			request.setAttribute("hmEmpNameMap", hmEmpNameMap);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	/*private void getClientConveyanceReport(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		try {

			String[] strPayCycleDates = null;
			if (getPaycycle() != null) {
				strPayCycleDates = getPaycycle().split("-");
			} else {
				strPayCycleDates = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF);
				setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1]+ "-" + strPayCycleDates[2]);
			}
			
			con = db.makeConnection(con);
			Map<String, String> hmClient=new HashMap<String, String>();
			pst = con.prepareStatement("select * from client_details");rs = pst.executeQuery();
			while (rs.next()) {
				hmClient.put(rs.getString("client_id"), rs.getString("client_name"));
			}
			
			List<String> clientList = new ArrayList<String>();
			List<String> empList = new ArrayList<String>();
			Map<String, String> hmAmount=new HashMap<String, String>();
			Map<String, String> hmBillable=new HashMap<String, String>();
			Map<String, String> hmClientAmount=new HashMap<String, String>();
			
			
			
			pst = con.prepareStatement("select * from emp_reimbursement where (from_date, to_date) overlaps " +
					"(to_date(?::text, 'YYYY-MM-DD')-1,to_date(?::text, 'YYYY-MM-DD') +1) and ispaid=true and client_id > 0 " +
					" order by client_id,emp_id ");
			pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));	
			StringBuilder sbQuery=new StringBuilder();			
			sbQuery.append("select * from emp_reimbursement where (from_date, to_date) overlaps " +
					"(to_date(?::text, 'YYYY-MM-DD')-1,to_date(?::text, 'YYYY-MM-DD') +1) and ispaid=true and client_id > 0 ");
			
			if(uF.parseToInt(getF_strWLocation())>0 || uF.parseToInt(getF_department())>0 || uF.parseToInt(getF_level())>0 || uF.parseToInt(getF_org())>0){
				sbQuery.append(" and emp_id in ( select emp_id from employee_official_details where emp_id>0 ");
			}
			
			if(uF.parseToInt(getF_strWLocation())>0){
				sbQuery.append(" and wlocation_id = "+uF.parseToInt(getF_strWLocation()));
			}
			if(uF.parseToInt(getF_department())>0){
				sbQuery.append(" and depart_id = "+uF.parseToInt(getF_department()));			
			}
			if(uF.parseToInt(getF_level())>0){
				sbQuery.append(" and grade_id in (select grade_id from designation_details dd, level_details ld, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id = "+uF.parseToInt(getF_level())+")");
			}
			
			if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and org_id="+uF.parseToInt(getF_org()));
			}
			
			if(uF.parseToInt(getF_strWLocation())>0 || uF.parseToInt(getF_department())>0 || uF.parseToInt(getF_level())>0 || uF.parseToInt(getF_org())>0){
				sbQuery.append(")");
			}
			
			sbQuery.append(" order by client_id,emp_id ");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			rs = pst.executeQuery();
			while (rs.next()) {
				if(!clientList.contains(rs.getString("client_id"))){
					clientList.add(rs.getString("client_id"));
				}
				if(!empList.contains(rs.getString("emp_id"))){
					empList.add(rs.getString("emp_id"));
				}
				double amount=uF.parseToDouble(hmAmount.get(rs.getString("client_id")+"_"+rs.getString("emp_id")));
				if(amount==0){
					amount=uF.parseToDouble(rs.getString("reimbursement_amount"));
				}
				else{
					amount+=uF.parseToDouble(rs.getString("reimbursement_amount"));;
				}
				
				double clientAmount=uF.parseToDouble(hmClientAmount.get(rs.getString("client_id")));
				if(clientAmount==0){
					clientAmount=uF.parseToDouble(rs.getString("reimbursement_amount"));
				}
				else{
					clientAmount+=uF.parseToDouble(rs.getString("reimbursement_amount"));;
				}
				
				hmClientAmount.put(rs.getString("client_id"), uF.formatIntoTwoDecimalWithOutComma(clientAmount));
				
				hmAmount.put(rs.getString("client_id")+"_"+rs.getString("emp_id"), uF.formatIntoTwoDecimalWithOutComma(amount));
				
				String isBillable="N";
				if(uF.parseToBoolean(rs.getString("is_billable"))){
					isBillable="Y";
				}
				
				hmBillable.put(rs.getString("client_id"), isBillable);
				
			}
			
			request.setAttribute("clientList", clientList);
			request.setAttribute("empList", empList);
			request.setAttribute("hmClient", hmClient);
			request.setAttribute("hmAmount", hmAmount);
			request.setAttribute("hmBillable", hmBillable);
			request.setAttribute("hmClientAmount", hmClientAmount);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeConnection(con);
			db.closeStatements(pst);
			db.closeResultSet(rs);
		}
	}*/

	public String loadClientConveyanceReport(UtilityFunctions uF) {
		
		payCycleList = new FillPayCycles(request).fillPayCycles(CF);
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
			orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		} else {
			orgList = new FillOrganisation(request).fillOrganisation();
		}
		
		orgList.add(new FillOrganisation("0","All Organization"));
		
		Collections.sort(orgList, new Comparator<FillOrganisation>() {

			@Override
			public int compare(FillOrganisation o1, FillOrganisation o2) {
				return o1.getOrgId().compareTo(o2.getOrgId());
			}
		});
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
		} else {
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
//		wLocationList = new FillWLocation(request).fillWLocation(getF_org());
//		wLocationList.add(new FillWLocation("0","All Work Location"));
			
		/*Collections.sort(wLocationList, new Comparator<FillWLocation>() {

			@Override
			public int compare(FillWLocation o1, FillWLocation o2) {
				return o1.getwLocationId().compareTo(o2.getwLocationId());
			}
		});*/
		
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
//		departmentList.add(new FillDepartment("0","All Departments"));
				
		/*Collections.sort(departmentList, new Comparator<FillDepartment>() {

			@Override
			public int compare(FillDepartment o1, FillDepartment o2) {
				return o1.getDeptId().compareTo(o2.getDeptId());
			}
		});*/
		
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
//		levelList.add(new FillLevel("0","All Levels"));
				
		/*Collections.sort(levelList, new Comparator<FillLevel>() {

			@Override
			public int compare(FillLevel o1, FillLevel o2) {
				return o1.getLevelId().compareTo(o2.getLevelId());
			}
		});*/
		
		getSelectedFilter(uF);
		
		return LOAD;
	}
	

	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();

		alFilter.add("PAYCYCLE");
		String strPaycycle = "";
		String[] strPayCycleDates = null;
		if (getPaycycle() != null) {
			strPayCycleDates = getPaycycle().split("-");
			setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
			
			strPaycycle = "Pay Cycle "+ strPayCycleDates[2]+", ";
		}
		hmFilter.put("PAYCYCLE", strPaycycle + uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT, CF.getStrReportDateFormat()));
		
		alFilter.add("ORGANISATION");
		if(getF_org()!=null)  {
			String strOrg="";
			for(int i=0;orgList!=null && i<orgList.size();i++){
				if(getF_org().equals(orgList.get(i).getOrgId())) {
					strOrg=orgList.get(i).getOrgName();
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
		
		
		alFilter.add("LEVEL");
		if(getF_level()!=null){
			String strLevel="";
			int k=0;
			for(int i=0;levelList!=null && i<levelList.size();i++){
				for(int j=0;j<getF_level().length;j++){
					if(getF_level()[j].equals(levelList.get(i).getLevelId())){
						if(k==0){
							strLevel=levelList.get(i).getLevelCodeName();
						}else{
							strLevel+=", "+levelList.get(i).getLevelCodeName();
						}
						k++;
					}
				}
			}
			if(strLevel!=null && !strLevel.equals("")){
				hmFilter.put("LEVEL", strLevel);
			}else{
				hmFilter.put("LEVEL", "All Levels");
			}
		}else{
			hmFilter.put("LEVEL", "All Levels");
		}
		
		
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
		this.response=response;
	}

	public List<FillPayCycles> getPayCycleList() {
		return payCycleList;
	}

	public void setPayCycleList(List<FillPayCycles> payCycleList) {
		this.payCycleList = payCycleList;
	}

	public String getPaycycle() {
		return paycycle;
	}

	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<FillOrganisation> getOrgList() {
		return orgList;
	}

	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
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

	public String getStrLevel() {
		return strLevel;
	}

	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}

}
