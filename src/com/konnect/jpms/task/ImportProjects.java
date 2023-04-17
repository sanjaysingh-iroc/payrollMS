package com.konnect.jpms.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.employee.AddEmployeeMode;
import com.konnect.jpms.select.FillClients;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ImportProjects extends ActionSupport implements ServletRequestAware, IConstants, IStatements {
	private static final long serialVersionUID = 1L;

	File fileUpload;
	String fileUploadFileName;
	HttpSession session;

	CommonFunctions CF;
	String strSessionEmpId;

	String btnSubmit;
	
	public String execute() throws Exception {
//		session = ActionContext.getContext().getSession();
		
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		CF  = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;

//		request.setAttribute(PAGE, "/jsp/task/ImportProjects.jsp");
//		request.setAttribute(TITLE, "Add Projects Mode");

		if (getFileUpload() != null) {
			loadExcel(getFileUpload());
		}

		if(getBtnSubmit() != null) {
			return SUCCESS;
		} else {
			return LOAD;
		}
	}
	
//===start parvez date: 18-10-2022===	
	/*public void loadExcel(File file) throws IOException {

		Database db = new Database();
		db.setRequest(request);
		StringBuilder sbMessage = new StringBuilder("<ul style=\"margin:0px\">");

		PreparedStatement pst=null;
		Connection con=null;
		ResultSet rs=null;
		List<String> alErrorList = new ArrayList<String>();
		try {

			UtilityFunctions uF = new UtilityFunctions();
			con = db.makeConnection(con);
			con.setAutoCommit(false);
			
			FileInputStream fis = new FileInputStream(file);
			System.out.println("getFileUploadFileName======>"+getFileUploadFileName());
			XSSFWorkbook workbook = new XSSFWorkbook(fis);

				System.out.println("Project Detail case 6 Start======>");
				XSSFSheet employsheet = workbook.getSheetAt(0);
				List<List<String>> dataList = new ArrayList<List<String>>();
				Iterator rows = employsheet.rowIterator();

				while (rows.hasNext()) {
					XSSFRow row = (XSSFRow) rows.next();
					Iterator cells = row.cellIterator();
					List<String> cellList = new ArrayList<String>();
					while (cells.hasNext()) {
						XSSFCell cell = (XSSFCell) cells.next();
						cellList.add(cell.toString());
					}
					dataList.add(cellList);
				}

				boolean flag = false;				
			for (int i = 2; i < dataList.size() && !dataList.isEmpty() && dataList.size()>0; i++) {

				List<String> cellList = dataList.get(i);
				if (cellList.size() < 5) {
					continue;
				}

				// Client Id
				String proName = cellList.get(1);
				if (proName != null && !proName.equals("") && proName.length() > 0) {
					
					// Client Name
					String clientName = cellList.get(2);
					String clientID = "";
					if (clientName != null && !clientName.equals("")) {
						pst = con.prepareStatement("select client_id from client_details where upper(client_name)=?");
						pst.setString(1, clientName.toUpperCase().trim());
//						System.out.println("pst ===>> " + pst);
						rs = pst.executeQuery();
						while (rs.next()) {
							clientID = rs.getString("client_id");
						}
						rs.close();
						pst.close();
					} else {
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"Please check the Client name"+ " on Row no-"+(i+1)+".</li>");
						flag = false;
						break;
					}
					if(uF.parseToInt(clientID)==0){
						
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								" Client name ("+clientName+") does not exists,check the Client name"+ " on Row no-"+(i+1)+".</li>");
						flag = false;
						break;
					}
//					System.out.println("Client Id--"+clientID);

					// Poc id
					String pocId = "";
					String spocName = cellList.get(3);
					if (spocName != null && !spocName.equals("") && clientID != null && !clientID.equals("")) {

							pst = con.prepareStatement("select poc_id from client_poc where upper(contact_fname)=? and client_id=?");
							pst.setString(1, spocName.toUpperCase().trim());
							pst.setInt(2, uF.parseToInt(clientID));
							rs = pst.executeQuery();
//							System.out.println("qry--"+pst);
							while (rs.next()) {
								pocId = rs.getString("poc_id");
							}
							rs.close();
							pst.close();
					} else {
						
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"Please check the SPOC name"+ " on Row no-"+(i+1)+".</li>");
						flag = false;
						break;
					}
					
//					System.out.println("poc Id--"+pocId);
					
					if(uF.parseToInt(pocId)==0){
						
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								" SPOC name does not exists,check the SPOC name (" + spocName + ") on Row no-"+(i+1)+".</li>");
						flag = false;
						break;
					}
					// Reference by id from reference name
					String referenceCode = cellList.get(4);
					String refEmpPerID = "";
					
					if (referenceCode != null && !referenceCode.equals("")) {

						pst = con.prepareStatement("SELECT emp_per_id FROM employee_personal_details  where upper(empcode)=?");
						pst.setString(1, referenceCode.toUpperCase().trim());
						rs = pst.executeQuery();
						while (rs.next()) {
							refEmpPerID = rs.getString("emp_per_id");
						}
						rs.close();
						pst.close();
						
					}else{
						
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"Please check the Reference employee code"+ " on Row no-"+(i+1)+".</li>");
						flag = false;
						break;
					}
					if(uF.parseToInt(refEmpPerID)==0){
						
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"  Reference employee code does not exists,check the Reference employee code"+ " on Row no-"+(i+1)+".</li>");
						flag = false;
						break;
					}
					
					// emp_per_id from emp name
					String projOwnerCode = cellList.get(5);
					String empPerID = "";
					if (projOwnerCode != null && !projOwnerCode.equals("")) {
							
						pst = con.prepareStatement("SELECT emp_per_id FROM employee_personal_details  where upper(empcode)=?");
						pst.setString(1, projOwnerCode.toUpperCase().trim());
						rs = pst.executeQuery();
						while (rs.next()) {
							empPerID = rs.getString("emp_per_id");
						}
						rs.close();
						pst.close();
						
					}else{
						
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"Please check the Project owner employee code"+ " on Row no-"+(i+1)+".</li>");
						flag = false;
						break;
					}
					if(uF.parseToInt(empPerID)==0){
						
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"  Project owner employee code does not exists,check the Project owner employee code"+ " on Row no-"+(i+1)+".</li>");
						flag = false;
						break;
					}
//					System.out.println("emp per id--"+empPerID);
					
					// service id from service name
					String projectService = cellList.get(6);
					String serviceProjectId = "";
					if (projectService != null && !projectService.equals("")) {

						pst = con.prepareStatement("SELECT service_project_id FROM services_project  where upper(service_name)=?");
						pst.setString(1, projectService.toUpperCase().trim());
						rs = pst.executeQuery();
						while (rs.next()) {
							serviceProjectId = rs.getString("service_project_id");
						}
						rs.close();
						pst.close();
					}else{
						
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"Please check the Service name"+ " on Row no-"+(i+1)+".</li>");
						flag = false;
						break;
					}
//					System.out.println("service project id="+serviceProjectId);
					
					if(uF.parseToInt(serviceProjectId)==0){
						
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"  Service name does not exists,check the Service name"+ " on Row no-"+(i+1)+".</li>");
						flag = false;
						break;
					}
					
					
					// get Organization id from organization name
					String orgName = cellList.get(7);
					String orgId = "";
					if (orgName != null && !orgName.equals("")) {

						pst = con.prepareStatement("SELECT org_id FROM org_details where upper(org_name)=?");
						pst.setString(1, orgName.toUpperCase().trim());
						rs = pst.executeQuery();
						while (rs.next()) {
							orgId = rs.getString("org_id");
						}
						rs.close();
						pst.close();
					}else{
						
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"Please check the Organisation name"+ " on Row no-"+(i+1)+".</li>");
						flag = false;
						break;
					}
					if(uF.parseToInt(orgId)==0){
						
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"  Organisation name does not exists,check the Organisation name"+ " on Row no-"+(i+1)+".</li>");
						flag = false;
						break;
					}
//					System.out.println("org id--"+orgId);
					// get worklocation id from worklocation name
					String workLocation = cellList.get(8);
					String wlocationId = "";
					if (workLocation != null && !workLocation.equals("")) {
						pst = con.prepareStatement("SELECT wlocation_id FROM work_location_info where upper(wlocation_name)=?");
						pst.setString(1, workLocation.toUpperCase().trim());
						rs = pst.executeQuery();
						while (rs.next()) {
							wlocationId = rs.getString("wlocation_id");
						}
						rs.close();
						pst.close();
					}else{
						
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"Please check the Work Location"+ " on Row no-"+(i+1)+".</li>");
						flag = false;
						break;
					}
					
//					System.out.println("work location id="+wlocationId);
					
					if(uF.parseToInt(wlocationId)==0){
						
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								" Work Location does not exists,check the Work Location "+ " on Row no-"+(i+1)+".</li>");
						flag = false;
						break;
					}
					
					// get service id i.e SBU from sbu name
					String serviceName = cellList.get(9);
					String serviceId = "";
					if (serviceName != null && !serviceName.equals("")) {

						pst = con.prepareStatement("SELECT service_id FROM services where upper(service_name)=?");
						pst.setString(1, serviceName.toUpperCase().trim());
						rs = pst.executeQuery();
						while (rs.next()) {
							serviceId = rs.getString("service_id");
						}
						rs.close();
						pst.close();
					}else{
						
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"Please check the SBU "+ " on Row no-"+(i+1)+".</li>");
						flag = false;
						break;
					}
					
					if(uF.parseToInt(orgId)==0){
						
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								" SBU does not exists,check the SBU "+ " on Row no-"+(i+1)+".</li>");
						flag = false;
						break;
					}
					
//					System.out.println("service id--"+serviceId);

					// get department id from department name
					String deptName = cellList.get(10);
					String deptId = "";
					if (deptName != null && !deptName.equals("")) {

						pst = con.prepareStatement("SELECT dept_id FROM department_info where upper(dept_name)=?");
						pst.setString(1, deptName.toUpperCase().trim());
						rs = pst.executeQuery();
						while (rs.next()) {
							deptId = rs.getString("dept_id");
						}
						rs.close();
						pst.close();
					}else{
						
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"Please check the Department name"+ " on Row no-"+(i+1)+".</li>");
						flag = false;
						break;
					}
					
					if(uF.parseToInt(deptId)==0){
						
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								" Department name does not exists,check the department name "+ " on Row no-"+(i+1)+".</li>");
						flag = false;
						break;
					}
					
//					System.out.println("dept id--"+deptId);
					
					// Short Description
					String shortDesc = cellList.get(11);
					// Long Description
					String longDesc = cellList.get(12);
					
					// Project Priority
					String projPriority = cellList.get(13);
					String proPriority = "";
					if (projPriority != null && !projPriority.equals("") && projPriority.toUpperCase().trim().equals("LOW")) {
						proPriority = "0";
					} else if (projPriority != null && !projPriority.equals("") && projPriority.toUpperCase().trim().equals("MEDIUM")) {
						proPriority = "1";
					} else if (projPriority != null && !projPriority.equals("") && projPriority.toUpperCase().trim().equals("HIGH")) {
						proPriority = "2";
					}else{
						
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"Please check the Priority"+ " on Row no-"+(i+1)+".</li>");
						flag = false;
						break;
					}

					// Start Date
					String startDate = cellList.get(14);
					if (startDate == null || startDate.equals("")){
						
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"Please check the Start date "+ " on Row no-"+(i+1)+".</li>");
						flag = false;
						break;
					}
					//Dead Line	
					String deadLine = cellList.get(15);
					
					if (deadLine == null || deadLine.equals("")){
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"Please check the End date/Deadline "+ " on Row no-"+(i+1)+".</li>");
						flag = false;
						break;
					}
					

					// Reporting Currency id from currency name
					String reportingCurrencyName = cellList.get(16);
					String repCurrencyId = "";
					if (reportingCurrencyName != null && !reportingCurrencyName.equals("")) {
						pst = con.prepareStatement("SELECT currency_id FROM currency_details where upper(long_currency)=?");
						pst.setString(1, reportingCurrencyName.toUpperCase().trim());
						rs = pst.executeQuery();
						while (rs.next()) {
							repCurrencyId = rs.getString("currency_id");
						}
						rs.close();
						pst.close();
					}else{

						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"Please check the Reporting Currency"+ " on Row no-"+(i+1)+".</li>");
						flag = false;
						break;
					}
					
					if(uF.parseToInt(repCurrencyId)==0){
						
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								" Reporting Currency does not exists,check the reporting currency "+ " on Row no-"+(i+1)+".</li>");
						flag = false;
						break;
					}

					// Billing Currency id from currency name
					String billCurrencyName = cellList.get(17);
					String billCurrencyId = "";
					if (billCurrencyName != null && !billCurrencyName.equals("")) {
						pst = con.prepareStatement("SELECT currency_id FROM currency_details where upper(long_currency)=?");
						pst.setString(1, billCurrencyName.toUpperCase().trim());
						rs = pst.executeQuery();
						while (rs.next()) {
							billCurrencyId = rs.getString("currency_id");
						}
						rs.close();
						pst.close();
					}else{

						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"Please check the Billing Currency"+ " on Row no-"+(i+1)+".</li>");
						flag = false;
						break;
					}
					
					if(uF.parseToInt(billCurrencyId)==0){
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								" Billing Currency does not exists,check the billing currency "+ " on Row no-"+(i+1)+".</li>");
						flag = false;
						break;
					}
					// billing type
					String billingType = cellList.get(18);
					String setbillingType = "";
					String fixedAmount = "";
					String assignmentCosting = "";
					String noOfHoursPerDay = ""; 
					String setAssignmentCosting = "";
					

					if (billingType != null && !billingType.equals("") && billingType.toUpperCase().trim().equals("FIXED RATE")) {
						
						setbillingType = "F";
						fixedAmount = cellList.get(19);
						assignmentCosting = cellList.get(20);
						if (assignmentCosting != null && !assignmentCosting.equals("") && assignmentCosting.toUpperCase().trim().equals("DAILY")) {
							
							setAssignmentCosting = "D";

						} else if (assignmentCosting != null && !assignmentCosting.equals("") && assignmentCosting.toUpperCase().trim().equals("HOURLY")) {
							
							setAssignmentCosting = "H";

						} else if (assignmentCosting != null && !assignmentCosting.equals("") && assignmentCosting.toUpperCase().trim().equals("MONTHLY")) {
						
							setAssignmentCosting = "M";
						
						}

					} else if (billingType != null && !billingType.equals("") && billingType.toUpperCase().trim().equals("HOURLY ACTUALS")) {

						setbillingType = "H";

					} else if (billingType != null && !billingType.equals("") && billingType.toUpperCase().trim().equals("DAILY ACTUALS")) {
						setbillingType = "D";
						noOfHoursPerDay = cellList.get(21);

					} else if (billingType != null && !billingType.equals("") && billingType.toUpperCase().trim().equals("MONTHLY ACTUALS")) {
						setbillingType = "M";
						
					}else{

						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"Please check the Billing Type"+ " on Row no-"+(i+1)+".</li>");
						flag = false;
						break;
					}
					
					List<String> dayList=new ArrayList<String>();
					dayList.add("MONDAY");
					dayList.add("TUESDAY");
					dayList.add("WEDNESDAY");
					dayList.add("THURSDAY");
					dayList.add("FRIDAY");
					dayList.add("SATURDAY");
					dayList.add("SUNDAY");
					
					// Estimated Hours
					String estHours = cellList.get(22);
					
					// Billing Kind
					String billingFreq = cellList.get(23);
//					System.out.println();
					
					String setBillingKind = " ";
					String dayCycle = " ";
					String weekDayCycle = " ";

					if (billingFreq != null && !billingFreq.equals("") && billingFreq.toUpperCase().trim().equals("ONE TIME")) {
						setBillingKind = "O";

					} else if (billingFreq != null && !billingFreq.equals("") && billingFreq.toUpperCase().trim().equals("WEEKLY")) {
						setBillingKind = "W";
						weekDayCycle = cellList.get(25);
						
						if(!dayList.contains(""+weekDayCycle.toUpperCase().trim())){
							alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
									"Please check Weekly Cycle"+ " on Row no-"+(i+1)+".</li>");
							flag = false;
							break;
						}

					} else if (billingFreq != null && !billingFreq.equals("") && billingFreq.toUpperCase().trim().equals("BIWEEKLY")) {
						setBillingKind = "B";
						dayCycle = cellList.get(24);
						if(uF.parseToInt(dayCycle)<=0 || uF.parseToInt(dayCycle)>31){
							alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
									"Please check Day Cycle"+ " on Row no-"+(i+1)+".</li>");
							flag = false;
							break;
						}

					} else if (billingFreq != null && !billingFreq.equals("") && billingFreq.toUpperCase().trim().equals("MONTHLY")) {
						setBillingKind = "M";
						dayCycle = cellList.get(24);
						if(uF.parseToInt(dayCycle)<=0 || uF.parseToInt(dayCycle)>31){
							alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
									"Please check Day Cycle"+ " on Row no-"+(i+1)+".</li>");
							flag = false;
							break;
						}
					} else if (billingFreq != null && !billingFreq.equals("") && billingFreq.toUpperCase().trim().equals("QUARTERLY")) {
						setBillingKind = "Q";
//						dayCycle = cellList.get(24);
						if(uF.parseToInt(dayCycle)<=0 || uF.parseToInt(dayCycle)>31){
							alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
									"Please check Day Cycle"+ " on Row no-"+(i+1)+".</li>");
							flag = false;
							break;
						}
					} else if(billingFreq != null && !billingFreq.equals("") && billingFreq.toUpperCase().trim().equals("MILESTONE BASED")){
						setBillingKind = "O";
						
					}else if(billingFreq != null && !billingFreq.equals("") && billingFreq.toUpperCase().trim().equals("HALF YEAR")){
						setBillingKind = "H";
						
					}else if(billingFreq != null && !billingFreq.equals("") && billingFreq.toUpperCase().trim().equals("ANNUALLY")){
						setBillingKind = "A";
						
					}else{

						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"Please check the Billing Frequency"+ " on Row no-"+(i+1)+".</li>");
						flag = false;				
						break;
					}
					
//					System.out.println("CellList==" + cellList.toString());
					
					String[] fYarr = CF.getFinancialYear(con, uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), CF, uF);
					String tempfStartDate[] = fYarr[0].split("/");
					String tempfEndDate[] = fYarr[1].split("/");

					String startFYear = tempfStartDate[2].substring(2, 4);
					String endFYear = tempfEndDate[2].substring(2, 4);

					pst = con.prepareStatement("select dept_code from department_info where dept_id=?");
					pst.setInt(1, uF.parseToInt(deptId));
					rs = pst.executeQuery();
					String strDeptCode = null;
					while (rs.next()) {
						strDeptCode = rs.getString("dept_code");
					}
					rs.close();
					pst.close();

					pst = con.prepareStatement("select wloacation_code from work_location_info where wlocation_id=?");
					pst.setInt(1, uF.parseToInt(wlocationId));
					rs = pst.executeQuery();
					String strWLocationCode = null;
					while (rs.next()) {
						strWLocationCode = rs.getString("wloacation_code");
					}
					rs.close();
					pst.close();

					String fYear = startFYear + "-" + endFYear;
					pst = con.prepareStatement("select max(pro_count) as pro_count from projectmntnc where pro_fyear=? and department_id=? and wlocation_id=?");
					pst.setString(1, fYear);
					pst.setInt(2, uF.parseToInt(deptId));
					pst.setInt(3, uF.parseToInt(wlocationId));
					rs = pst.executeQuery();
//					System.out.println("pst=" + pst);
					int nProCount = 0;
					while (rs.next()) {
						nProCount = uF.parseToInt(rs.getString("pro_count"));
					}
					rs.close();
					pst.close();
					nProCount++;

					int nLength = nProCount + "".length();
					if (nLength == 0) {
						nLength = 1;
					}

					StringBuilder sbProjectCount = new StringBuilder();
					for (int i1 = 0; i1 < (4 - nLength); i1++) {
						sbProjectCount.append("0");
					}
					sbProjectCount.append(nProCount);

					String projectCode = fYear + "/" + strDeptCode + "/" + strWLocationCode + "-" + sbProjectCount.toString();

//					System.out.println("projectcode-" + projectCode);
//					System.out.println("strDeptCode" + strDeptCode);
//					System.out.println("strWLocationCode-" + strWLocationCode);
//					System.out.println("sbProjectCount.toString()--" + sbProjectCount.toString());

					pst = con.prepareStatement("insert into projectmntnc (pro_name, priority, description, activity, service, taskstatus, deadline, "
							+ "idealtime, timestatus, client_id, project_code, poc, start_date, added_by,level_id,wlocation_id,billing_type,billing_amount, "
							+ "entry_date, actual_calculation_type, department_id, billing_kind, pro_count,pro_fyear,project_owner,curr_id,sbu_id,org_id,"
							+ "short_description,billing_curr_id,bill_days_type,hours_for_bill_day,billing_cycle_weekday,billing_cycle_day,reference_by_id) "
							+ "values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?)");
					pst.setString(1, proName);
					pst.setInt(2, uF.parseToInt(proPriority));
					pst.setString(3, longDesc);
					pst.setString(4, proName);
					
					pst.setInt(5, uF.parseToInt(serviceProjectId));
					pst.setString(6, "New Task");
					pst.setDate(7, uF.getDateFormat(deadLine, DATE_FORMAT));
					pst.setInt(8,(int)uF.parseToDouble(estHours));
					pst.setString(9, "n");
					pst.setInt(10, uF.parseToInt(clientID));
					pst.setString(11, projectCode);
					pst.setInt(12, uF.parseToInt(pocId));
					pst.setDate(13, uF.getDateFormat(startDate, DATE_FORMAT));
					pst.setInt(14, uF.parseToInt((String) session.getAttribute(EMPID)));
					pst.setString(15, "0");
					pst.setInt(16, uF.parseToInt(wlocationId));
					pst.setString(17, setbillingType);
					if (setbillingType != null && (setbillingType.equals("H") || setbillingType.equals("D") || setbillingType.equals("M"))) {
						pst.setDouble(18, uF.parseToDouble("0"));
					} else {
						pst.setDouble(18, uF.parseToDouble(fixedAmount));
					}
					pst.setDate(19, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()) + "", DBDATE));
					if (setbillingType != null && setbillingType.equalsIgnoreCase("F")) {
						pst.setString(20, setAssignmentCosting);
					} else {
						pst.setString(20, setbillingType);
					}
					pst.setInt(21, uF.parseToInt(deptId));
					pst.setString(22, setBillingKind);
					pst.setInt(23, nProCount);
					pst.setString(24, fYear);
					pst.setInt(25, uF.parseToInt(empPerID));
					pst.setInt(26, uF.parseToInt(repCurrencyId));
					pst.setInt(27, uF.parseToInt(serviceId));
					pst.setInt(28, uF.parseToInt(orgId));
					pst.setString(29, shortDesc);
					pst.setInt(30, uF.parseToInt(billCurrencyId));
					pst.setInt(31, uF.parseToInt(noOfHoursPerDay) > 0 ? uF.parseToInt(noOfHoursPerDay) : 1);
					pst.setDouble(32, uF.parseToDouble(noOfHoursPerDay));
					pst.setString(33, weekDayCycle);
					pst.setInt(34, uF.parseToInt(dayCycle));
					pst.setInt(35, uF.parseToInt(refEmpPerID));
//					System.out.println("pst====>" + pst);
					pst.executeUpdate();
					pst.close();
//					con.commit();
					if (setbillingType != null && !setbillingType.equalsIgnoreCase("F")) {
						boolean frqFlag = false;
						String freqEndDate = deadLine;
						if (uF.parseToInt(dayCycle) > 0) {

							freqEndDate = dayCycle + "/" + uF.getDateFormat(startDate, DATE_FORMAT, "MM") + "/"
									+ uF.getDateFormat(startDate, DATE_FORMAT, "yyyy");

							Date stDate = uF.getDateFormatUtil(startDate, DATE_FORMAT);
							Date endDate = uF.getDateFormatUtil(deadLine, DATE_FORMAT);
							Date freqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);

							if (freqDate.after(stDate)) {
								frqFlag = true;
							}

							if (frqFlag) {
								freqEndDate = freqEndDate;
							} else if (setBillingKind != null && setBillingKind.equals("M")) {
								freqEndDate = uF.getDateFormat(uF.getFutureMonthDate(freqEndDate, 1) + "", DBDATE, DATE_FORMAT);
							} else if (setBillingKind != null && setBillingKind.equals("B")) {
								freqEndDate = uF.getDateFormat(uF.getFutureDate(startDate, 15) + "", DBDATE, DATE_FORMAT);
							}
							// System.out.println("freqEndDate ====> " +
							// freqEndDate);
							Date newFreqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
							if (newFreqDate.after(endDate)) {
								freqEndDate = deadLine;
							}

						}
						if (weekDayCycle != null && !setBillingKind.equals("") && setBillingKind != null && setBillingKind.equals("W")) {
							freqEndDate = uF.getDateFormat(uF.getDateOfPassedDay(startDate, weekDayCycle) + "", DBDATE, DATE_FORMAT);

							Date endDate = uF.getDateFormatUtil(deadLine, DATE_FORMAT);
							Date newFreqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);

							if (newFreqDate.after(endDate)) {
								freqEndDate = deadLine;
							}
						}
						String proFreqName = "";
						if (setBillingKind != null && setBillingKind.equals("M")) {
							String freqMonth = uF.getDateFormat(freqEndDate, DATE_FORMAT, "MM");
							String freqYear = uF.getDateFormat(freqEndDate, DATE_FORMAT, "yyyy");
							String strMonth = uF.getMonth(uF.parseToInt(freqMonth));
							proFreqName = freqYear + " " + strMonth;
						} else if (setBillingKind != null && setBillingKind.equals("B")) {
							String freqMonth = uF.getDateFormat(freqEndDate, DATE_FORMAT, "MM");
							String freqYear = uF.getDateFormat(freqEndDate, DATE_FORMAT, "yyyy");
							String strMonth = uF.getMonth(uF.parseToInt(freqMonth));
							String freqDate = uF.getDateFormat(freqEndDate, DATE_FORMAT, "dd");
							String strHalf = "- First";
							if (uF.parseToInt(freqDate) > 15) {
								strHalf = "- Second";
							}
							proFreqName = freqYear + " " + strMonth + " " + strHalf;
						} else if (setBillingKind != null && setBillingKind.equals("W")) {
							String freqMonth = uF.getDateFormat(freqEndDate, DATE_FORMAT, "MM");
							String freqYear = uF.getDateFormat(freqEndDate, DATE_FORMAT, "yyyy");
							String strMonth = uF.getMonth(uF.parseToInt(freqMonth));
							String strWeekName = uF.getWeekOfMonthOnPassedDate(freqEndDate);
							proFreqName = freqYear + " " + strMonth + " Week-" + strWeekName;
						}

						pst = con.prepareStatement("insert into projectmntnc_frequency(pro_id, pro_start_date, pro_end_date, freq_start_date, freq_end_date, "
								+ "added_by, entry_date, pro_freq_name) values (?,?,?,?, ?,?,?,?)");
						pst.setInt(1, uF.parseToInt(pocId));
						pst.setDate(2, uF.getDateFormat(startDate, DATE_FORMAT));
						pst.setDate(3, uF.getDateFormat(deadLine, DATE_FORMAT));
						pst.setDate(4, uF.getDateFormat(startDate, DATE_FORMAT));
						if (frqFlag || (setBillingKind != null && setBillingKind.equals("O"))) {
							pst.setDate(5, uF.getDateFormat(freqEndDate, DATE_FORMAT));
						} else {
							pst.setDate(5, uF.getDateFormat(freqEndDate, DATE_FORMAT));
						}
						pst.setInt(6, uF.parseToInt((String) session.getAttribute(EMPID)));
						pst.setDate(7, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()) + "", DBDATE));
						pst.setString(8, proFreqName);
//						System.out.println("pst ====> " + pst);
						pst.execute();
						pst.close();
					}
					flag=true;
				}else{
					alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
							"Please check the Project name"+ " on Row no-"+(i+1)+".</li>");
					flag = false;
					break;
				}
//				System.out.println("cell list--"+cellList);
			}// end of for loop
			
			if(flag){
				con.commit();
				System.out.println("ALL Projects Imported Successfully...");
				session.setAttribute(MESSAGE, SUCCESSM+"Project Imported Successfully!"+END);
				sbMessage.append("</ul>");
				session.setAttribute("sbMessage", sbMessage.toString());
			} else {
				con.rollback();
				if(alErrorList.size()>0){
					sbMessage.append(alErrorList.get(alErrorList.size()-1));
				}
//				request.setAttribute("alReport", alReport);
//				session.setAttribute(MESSAGE, ERRORM+"Attendance not imported. Please check imported file."+END);
				session.setAttribute(MESSAGE, ERRORM+"Project not imported. Please check imported file."+END);
				sbMessage.append("</ul>");
				session.setAttribute("sbMessage", sbMessage.toString());
			}
				
		}catch (Exception e) {
			sbMessage.append("<li class=\"msg_error\" style=\"margin:0px\">There seems to error in the file, please recheck the columns and try again.</li>");
			try {
				con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			if(alErrorList.size()>0){
				sbMessage.append(alErrorList.get(alErrorList.size()-1));
			}
			sbMessage.append("</ul>");
			session.setAttribute("sbMessage", sbMessage.toString());
			session.setAttribute(MESSAGE, ERRORM+"Project not imported. Please check imported file."+END);
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}*/
	
	public void loadExcel(File file) throws IOException {

		Database db = new Database();
		db.setRequest(request);
		StringBuilder sbMessage = new StringBuilder("<ul style=\"margin:0px\">");

		PreparedStatement pst=null;
		Connection con=null;
		ResultSet rs=null;
		List<String> alErrorList = new ArrayList<String>();
		try {

			UtilityFunctions uF = new UtilityFunctions();
			con = db.makeConnection(con);
			con.setAutoCommit(false);
			
			FileInputStream fis = new FileInputStream(file);
			System.out.println("getFileUploadFileName======>"+getFileUploadFileName());
			XSSFWorkbook workbook = new XSSFWorkbook(fis);

				System.out.println("Project Detail case 6 Start======>");
				XSSFSheet employsheet = workbook.getSheetAt(0);
				List<List<String>> dataList = new ArrayList<List<String>>();
				Iterator rows = employsheet.rowIterator();

				while (rows.hasNext()) {
					XSSFRow row = (XSSFRow) rows.next();
					Iterator cells = row.cellIterator();
					List<String> cellList = new ArrayList<String>();
					while (cells.hasNext()) {
						XSSFCell cell = (XSSFCell) cells.next();
						cellList.add(cell.toString());
					}
					dataList.add(cellList);
				}

				boolean flag = false;				
			for (int i = 2; i < dataList.size() && !dataList.isEmpty() && dataList.size()>0; i++) {

				List<String> cellList = dataList.get(i);
		/*		if (cellList.size() < 5) {
					continue;
				}
*/
				// Client Id
				String proName = cellList.get(1);
				if (proName != null && !proName.equals("") && proName.length() > 0) {
					
					// Client Name
					String clientName = cellList.get(2);
					String clientID = "";
					if (clientName != null && !clientName.equals("")) {
						pst = con.prepareStatement("select client_id from client_details where upper(client_name)=?");
						pst.setString(1, clientName.toUpperCase().trim());
//						System.out.println("pst ===>> " + pst);
						rs = pst.executeQuery();
						while (rs.next()) {
							clientID = rs.getString("client_id");
						}
						rs.close();
						pst.close();
					} else {
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"Please check the Client name"+ " on Row no-"+(i+1)+".</li>");
						flag = false;
						break;
					}
					if(uF.parseToInt(clientID)==0){
						
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								" Client name ("+clientName+") does not exists,check the Client name"+ " on Row no-"+(i+1)+".</li>");
						flag = false;
						break;
					}
//					System.out.println("Client Id--"+clientID);

					// Poc id
					String pocId = "";
					String spocName = cellList.get(3);
					if (spocName != null && !spocName.equals("") && clientID != null && !clientID.equals("")) {

							pst = con.prepareStatement("select poc_id from client_poc where upper(contact_fname)=? and client_id=?");
							pst.setString(1, spocName.toUpperCase().trim());
							pst.setInt(2, uF.parseToInt(clientID));
							rs = pst.executeQuery();
//							System.out.println("qry--"+pst);
							while (rs.next()) {
								pocId = rs.getString("poc_id");
							}
							rs.close();
							pst.close();
					} else {
						
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"Please check the SPOC name"+ " on Row no-"+(i+1)+".</li>");
						flag = false;
						break;
					}
					
//					System.out.println("poc Id--"+pocId);
					
					if(uF.parseToInt(pocId)==0){
						
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								" SPOC name does not exists,check the SPOC name (" + spocName + ") on Row no-"+(i+1)+".</li>");
						flag = false;
						break;
					}
					// Reference by id from reference name
					String referenceCode = cellList.get(4);
					String refEmpPerID = "";
					
					if (referenceCode != null && !referenceCode.equals("")) {

						pst = con.prepareStatement("SELECT emp_per_id FROM employee_personal_details  where upper(empcode)=?");
						pst.setString(1, referenceCode.toUpperCase().trim());
						rs = pst.executeQuery();
						while (rs.next()) {
							refEmpPerID = rs.getString("emp_per_id");
						}
						rs.close();
						pst.close();
						
					}else{
						
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"Please check the Reference employee code"+ " on Row no-"+(i+1)+".</li>");
						flag = false;
						break;
					}
					if(uF.parseToInt(refEmpPerID)==0){
						
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"  Reference employee code does not exists,check the Reference employee code"+ " on Row no-"+(i+1)+".</li>");
						flag = false;
						break;
					}
					
					// emp_per_id from emp name
					String projOwnerCode = cellList.get(5);
					String empPerID = "";
					
					List<String> alEmpPerIDs = new ArrayList<String>();
					if (projOwnerCode != null && !projOwnerCode.equals("")) {
						StringBuilder sbEmpPerID = null;
						String[] arrProOwnerCode = projOwnerCode.split(",");
						StringBuilder sbProjOwnersCode = null;
						for(int ii=0; ii<arrProOwnerCode.length; ii++){
							if(sbProjOwnersCode == null){
								sbProjOwnersCode = new StringBuilder();
								sbProjOwnersCode.append("'"+arrProOwnerCode[ii].toUpperCase().trim()+"'");
							}else{
								sbProjOwnersCode.append(","+"'"+arrProOwnerCode[ii].toUpperCase().trim()+"'");
							}
						}
//						pst = con.prepareStatement("SELECT emp_per_id FROM employee_personal_details  where upper(empcode)=?");
						pst = con.prepareStatement("SELECT emp_per_id FROM employee_personal_details  where upper(empcode) in ("+sbProjOwnersCode.toString()+")");
//						pst.setString(1, projOwnerCode.toUpperCase().trim());
//						System.out.println("pst===>"+pst);
						rs = pst.executeQuery();
						while (rs.next()) {
							if(sbEmpPerID==null){
								sbEmpPerID = new StringBuilder();
								sbEmpPerID.append(","+rs.getString("emp_per_id")+",");
							}else{
								sbEmpPerID.append(rs.getString("emp_per_id")+",");
							}
							alEmpPerIDs.add(rs.getString("emp_per_id"));
//							empPerID = rs.getString("emp_per_id");
						}
						rs.close();
						pst.close();
						
						empPerID = sbEmpPerID.toString();
						
					}else{
						
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"Please check the Project owner employee code"+ " on Row no-"+(i+1)+".</li>");
						flag = false;
						break;
					}
					if(alEmpPerIDs.contains("0")){
						
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"  Project owner employee code does not exists,check the Project owner employee code"+ " on Row no-"+(i+1)+".</li>");
						flag = false;
						break;
					}
//					System.out.println("emp per id--"+empPerID);
					
					
					// Portfolio manager by id from portfolio name
					String portfolioManagerCode = cellList.get(6);
					String pmEmpPerID = "";
					
					if (portfolioManagerCode != null && !portfolioManagerCode.equals("")) {

						pst = con.prepareStatement("SELECT emp_per_id FROM employee_personal_details  where upper(empcode)=?");
						pst.setString(1, portfolioManagerCode.toUpperCase().trim());
						rs = pst.executeQuery();
						while (rs.next()) {
							pmEmpPerID = rs.getString("emp_per_id");
						}
						rs.close();
						pst.close();
						
					}
					/*else{
						
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"Please check the Portfolio Manager employee code"+ " on Row no-"+(i+1)+".</li>");
						flag = false;
						break;
					}
					if(uF.parseToInt(pmEmpPerID)==0){
						
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"  Portfolio Manager employee code does not exists,check the Portfolio Manager employee code"+ " on Row no-"+(i+1)+".</li>");
						flag = false;
						break;
					}*/
					
					// Account manager by id from portfolio name
					String accManagerCode = cellList.get(7);
					String accMEmpPerID = "";
					
					if (accManagerCode != null && !accManagerCode.equals("")) {

						pst = con.prepareStatement("SELECT emp_per_id FROM employee_personal_details  where upper(empcode)=?");
						pst.setString(1, accManagerCode.toUpperCase().trim());
						rs = pst.executeQuery();
						while (rs.next()) {
							accMEmpPerID = rs.getString("emp_per_id");
						}
						rs.close();
						pst.close();
						
					}
					/*else{
						
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"Please check the Account Manager employee code"+ " on Row no-"+(i+1)+".</li>");
						flag = false;
						break;
					}
					if(uF.parseToInt(accMEmpPerID)==0){
						
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"  Account Manager employee code does not exists,check the Portfolio Manager employee code"+ " on Row no-"+(i+1)+".</li>");
						flag = false;
						break;
					}*/
					
					// Delivery manager by id from portfolio name
					String delvManagerCode = cellList.get(8);
					String delvMEmpPerID = "";
					
					if (delvManagerCode != null && !delvManagerCode.equals("")) {

						pst = con.prepareStatement("SELECT emp_per_id FROM employee_personal_details  where upper(empcode)=?");
						pst.setString(1, delvManagerCode.toUpperCase().trim());
						rs = pst.executeQuery();
						while (rs.next()) {
							delvMEmpPerID = rs.getString("emp_per_id");
						}
						rs.close();
						pst.close();
						
					}
					/*else{
						
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"Please check the Delivery Manager employee code"+ " on Row no-"+(i+1)+".</li>");
						flag = false;
						break;
					}
					if(uF.parseToInt(delvMEmpPerID)==0){
						
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"  Delivery Manager employee code does not exists,check the Portfolio Manager employee code"+ " on Row no-"+(i+1)+".</li>");
						flag = false;
						break;
					}*/
					
					// service id from service name
					String projectService = cellList.get(9);
					String serviceProjectId = "";
					if (projectService != null && !projectService.equals("")) {

						pst = con.prepareStatement("SELECT service_project_id FROM services_project  where upper(service_name)=?");
						pst.setString(1, projectService.toUpperCase().trim());
						rs = pst.executeQuery();
						while (rs.next()) {
							serviceProjectId = rs.getString("service_project_id");
						}
						rs.close();
						pst.close();
					}else{
						
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"Please check the Service name"+ " on Row no-"+(i+1)+".</li>");
						flag = false;
						break;
					}
//					System.out.println("service project id="+serviceProjectId);
					
					if(uF.parseToInt(serviceProjectId)==0){
						
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"  Service name does not exists,check the Service name"+ " on Row no-"+(i+1)+".</li>");
						flag = false;
						break;
					}
					
					
					// get Organization id from organization name
					String orgName = cellList.get(10);
					String orgId = "";
					if (orgName != null && !orgName.equals("")) {

						pst = con.prepareStatement("SELECT org_id FROM org_details where upper(org_name)=?");
						pst.setString(1, orgName.toUpperCase().trim());
						rs = pst.executeQuery();
						while (rs.next()) {
							orgId = rs.getString("org_id");
						}
						rs.close();
						pst.close();
					}else{
						
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"Please check the Organisation name"+ " on Row no-"+(i+1)+".</li>");
						flag = false;
						break;
					}
					if(uF.parseToInt(orgId)==0){
						
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"  Organisation name does not exists,check the Organisation name"+ " on Row no-"+(i+1)+".</li>");
						flag = false;
						break;
					}
//					System.out.println("org id--"+orgId);
					// get worklocation id from worklocation name
					String workLocation = cellList.get(11);
					String wlocationId = "";
					if (workLocation != null && !workLocation.equals("")) {
						pst = con.prepareStatement("SELECT wlocation_id FROM work_location_info where upper(wlocation_name)=?");
						pst.setString(1, workLocation.toUpperCase().trim());
						rs = pst.executeQuery();
						while (rs.next()) {
							wlocationId = rs.getString("wlocation_id");
						}
						rs.close();
						pst.close();
					}else{
						
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"Please check the Work Location"+ " on Row no-"+(i+1)+".</li>");
						flag = false;
						break;
					}
					
//					System.out.println("work location id="+wlocationId);
					
					if(uF.parseToInt(wlocationId)==0){
						
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								" Work Location does not exists,check the Work Location "+ " on Row no-"+(i+1)+".</li>");
						flag = false;
						break;
					}
					
					// get service id i.e SBU from sbu name
					String serviceName = cellList.get(12);
					String serviceId = "";
					if (serviceName != null && !serviceName.equals("")) {

						pst = con.prepareStatement("SELECT service_id FROM services where upper(service_name)=?");
						pst.setString(1, serviceName.toUpperCase().trim());
						rs = pst.executeQuery();
						while (rs.next()) {
							serviceId = rs.getString("service_id");
						}
						rs.close();
						pst.close();
					}else{
						
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"Please check the SBU "+ " on Row no-"+(i+1)+".</li>");
						flag = false;
						break;
					}
					
					if(uF.parseToInt(orgId)==0){
						
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								" SBU does not exists,check the SBU "+ " on Row no-"+(i+1)+".</li>");
						flag = false;
						break;
					}
					
//					System.out.println("service id--"+serviceId);

					// get department id from department name
					String deptName = cellList.get(13);
					String deptId = "";
					if (deptName != null && !deptName.equals("")) {

						pst = con.prepareStatement("SELECT dept_id FROM department_info where upper(dept_name)=?");
						pst.setString(1, deptName.toUpperCase().trim());
						rs = pst.executeQuery();
						while (rs.next()) {
							deptId = rs.getString("dept_id");
						}
						rs.close();
						pst.close();
					}else{
						
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"Please check the Department name"+ " on Row no-"+(i+1)+".</li>");
						flag = false;
						break;
					}
					
					if(uF.parseToInt(deptId)==0){
						
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								" Department name does not exists,check the department name "+ " on Row no-"+(i+1)+".</li>");
						flag = false;
						break;
					}
					
//					System.out.println("dept id--"+deptId);
					
					// Short Description
					String shortDesc = cellList.get(14);
					// Long Description
					String longDesc = cellList.get(15);
					
					// Project Priority
					String projPriority = cellList.get(16);
					String proPriority = "";
					if (projPriority != null && !projPriority.equals("") && projPriority.toUpperCase().trim().equals("LOW")) {
						proPriority = "0";
					} else if (projPriority != null && !projPriority.equals("") && projPriority.toUpperCase().trim().equals("MEDIUM")) {
						proPriority = "1";
					} else if (projPriority != null && !projPriority.equals("") && projPriority.toUpperCase().trim().equals("HIGH")) {
						proPriority = "2";
					}else{
						
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"Please check the Priority"+ " on Row no-"+(i+1)+".</li>");
						flag = false;
						break;
					}

					// Start Date
					String startDate = cellList.get(17);
					if (startDate == null || startDate.equals("")){
						
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"Please check the Start date "+ " on Row no-"+(i+1)+".</li>");
						flag = false;
						break;
					}
					//Dead Line	
					String deadLine = cellList.get(18);
					
					if (deadLine == null || deadLine.equals("")){
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"Please check the End date/Deadline "+ " on Row no-"+(i+1)+".</li>");
						flag = false;
						break;
					}
					

					// Reporting Currency id from currency name
					String reportingCurrencyName = cellList.get(19);
					String repCurrencyId = "";
					if (reportingCurrencyName != null && !reportingCurrencyName.equals("")) {
						pst = con.prepareStatement("SELECT currency_id FROM currency_details where upper(long_currency)=?");
						pst.setString(1, reportingCurrencyName.toUpperCase().trim());
						rs = pst.executeQuery();
						while (rs.next()) {
							repCurrencyId = rs.getString("currency_id");
						}
						rs.close();
						pst.close();
					}else{

						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"Please check the Reporting Currency"+ " on Row no-"+(i+1)+".</li>");
						flag = false;
						break;
					}
					
					if(uF.parseToInt(repCurrencyId)==0){
						
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								" Reporting Currency does not exists,check the reporting currency "+ " on Row no-"+(i+1)+".</li>");
						flag = false;
						break;
					}

					// Billing Currency id from currency name
					String billCurrencyName = cellList.get(20);
					String billCurrencyId = "";
					if (billCurrencyName != null && !billCurrencyName.equals("")) {
						pst = con.prepareStatement("SELECT currency_id FROM currency_details where upper(long_currency)=?");
						pst.setString(1, billCurrencyName.toUpperCase().trim());
						rs = pst.executeQuery();
						while (rs.next()) {
							billCurrencyId = rs.getString("currency_id");
						}
						rs.close();
						pst.close();
					}else{

						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"Please check the Billing Currency"+ " on Row no-"+(i+1)+".</li>");
						flag = false;
						break;
					}
					
					if(uF.parseToInt(billCurrencyId)==0){
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								" Billing Currency does not exists,check the billing currency "+ " on Row no-"+(i+1)+".</li>");
						flag = false;
						break;
					}
					// billing type
					String billingType = cellList.get(21);
					String setbillingType = "";
					String fixedAmount = "";
					String assignmentCosting = "";
					String noOfHoursPerDay = ""; 
					String setAssignmentCosting = "";
					

					if (billingType != null && !billingType.equals("") && billingType.toUpperCase().trim().equals("FIXED RATE")) {
						
						setbillingType = "F";
						fixedAmount = cellList.get(22);//19
						assignmentCosting = cellList.get(23);//20
						if (assignmentCosting != null && !assignmentCosting.equals("") && assignmentCosting.toUpperCase().trim().equals("DAILY")) {
							
							setAssignmentCosting = "D";

						} else if (assignmentCosting != null && !assignmentCosting.equals("") && assignmentCosting.toUpperCase().trim().equals("HOURLY")) {
							
							setAssignmentCosting = "H";

						} else if (assignmentCosting != null && !assignmentCosting.equals("") && assignmentCosting.toUpperCase().trim().equals("MONTHLY")) {
						
							setAssignmentCosting = "M";
						
						}

					} else if (billingType != null && !billingType.equals("") && billingType.toUpperCase().trim().equals("HOURLY ACTUALS")) {

						setbillingType = "H";

					} else if (billingType != null && !billingType.equals("") && billingType.toUpperCase().trim().equals("DAILY ACTUALS")) {
						setbillingType = "D";
						noOfHoursPerDay = cellList.get(24);//21

					} else if (billingType != null && !billingType.equals("") && billingType.toUpperCase().trim().equals("MONTHLY ACTUALS")) {
						setbillingType = "M";
						
					}else{

						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"Please check the Billing Type"+ " on Row no-"+(i+1)+".</li>");
						flag = false;
						break;
					}
					
					List<String> dayList=new ArrayList<String>();
					dayList.add("MONDAY");
					dayList.add("TUESDAY");
					dayList.add("WEDNESDAY");
					dayList.add("THURSDAY");
					dayList.add("FRIDAY");
					dayList.add("SATURDAY");
					dayList.add("SUNDAY");
					
					// Estimated Hours
					String estHours = cellList.get(25);//22
					
					// Billing Kind
					String billingFreq = cellList.get(26);//23
//					System.out.println();
					
					String setBillingKind = " ";
					String dayCycle = " ";
					String weekDayCycle = " ";

					if (billingFreq != null && !billingFreq.equals("") && billingFreq.toUpperCase().trim().equals("ONE TIME")) {
						setBillingKind = "O";

					} else if (billingFreq != null && !billingFreq.equals("") && billingFreq.toUpperCase().trim().equals("WEEKLY")) {
						setBillingKind = "W";
						weekDayCycle = cellList.get(28);//25
						
						if(!dayList.contains(""+weekDayCycle.toUpperCase().trim())){
							alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
									"Please check Weekly Cycle"+ " on Row no-"+(i+1)+".</li>");
							flag = false;
							break;
						}

					} else if (billingFreq != null && !billingFreq.equals("") && billingFreq.toUpperCase().trim().equals("BIWEEKLY")) {
						setBillingKind = "B";
						dayCycle = cellList.get(27);//24
						if(uF.parseToInt(dayCycle)<=0 || uF.parseToInt(dayCycle)>31){
							alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
									"Please check Day Cycle"+ " on Row no-"+(i+1)+".</li>");
							flag = false;
							break;
						}

					} else if (billingFreq != null && !billingFreq.equals("") && billingFreq.toUpperCase().trim().equals("MONTHLY")) {
						setBillingKind = "M";
						dayCycle = cellList.get(27);//24
						if(uF.parseToInt(dayCycle)<=0 || uF.parseToInt(dayCycle)>31){
							alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
									"Please check Day Cycle"+ " on Row no-"+(i+1)+".</li>");
							flag = false;
							break;
						}
					} else if (billingFreq != null && !billingFreq.equals("") && billingFreq.toUpperCase().trim().equals("QUARTERLY")) {
						setBillingKind = "Q";
//						dayCycle = cellList.get(27);
						/*if(uF.parseToInt(dayCycle)<=0 || uF.parseToInt(dayCycle)>31){
							alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
									"Please check Day Cycle"+ " on Row no-"+(i+1)+".</li>");
							flag = false;
							break;
						}*/
					} else if(billingFreq != null && !billingFreq.equals("") && billingFreq.toUpperCase().trim().equals("MILESTONE BASED")){
						setBillingKind = "O";
						
					}else if(billingFreq != null && !billingFreq.equals("") && billingFreq.toUpperCase().trim().equals("HALF YEAR")){
						setBillingKind = "H";
						
					}else if(billingFreq != null && !billingFreq.equals("") && billingFreq.toUpperCase().trim().equals("ANNUALLY")){
						setBillingKind = "A";
						
					}else{

						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"Please check the Billing Frequency"+ " on Row no-"+(i+1)+".</li>");
						flag = false;				
						break;
					}
					
//					System.out.println("CellList==" + cellList.toString());
					
					String[] fYarr = CF.getFinancialYear(con, uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), CF, uF);
					String tempfStartDate[] = fYarr[0].split("/");
					String tempfEndDate[] = fYarr[1].split("/");

					String startFYear = tempfStartDate[2].substring(2, 4);
					String endFYear = tempfEndDate[2].substring(2, 4);

					pst = con.prepareStatement("select dept_code from department_info where dept_id=?");
					pst.setInt(1, uF.parseToInt(deptId));
					rs = pst.executeQuery();
					String strDeptCode = null;
					while (rs.next()) {
						strDeptCode = rs.getString("dept_code");
					}
					rs.close();
					pst.close();

					pst = con.prepareStatement("select wloacation_code from work_location_info where wlocation_id=?");
					pst.setInt(1, uF.parseToInt(wlocationId));
					rs = pst.executeQuery();
					String strWLocationCode = null;
					while (rs.next()) {
						strWLocationCode = rs.getString("wloacation_code");
					}
					rs.close();
					pst.close();

					String fYear = startFYear + "-" + endFYear;
					pst = con.prepareStatement("select max(pro_count) as pro_count from projectmntnc where pro_fyear=? and department_id=? and wlocation_id=?");
					pst.setString(1, fYear);
					pst.setInt(2, uF.parseToInt(deptId));
					pst.setInt(3, uF.parseToInt(wlocationId));
					rs = pst.executeQuery();
//					System.out.println("pst=" + pst);
					int nProCount = 0;
					while (rs.next()) {
						nProCount = uF.parseToInt(rs.getString("pro_count"));
					}
					rs.close();
					pst.close();
					nProCount++;

					int nLength = nProCount + "".length();
					if (nLength == 0) {
						nLength = 1;
					}

					StringBuilder sbProjectCount = new StringBuilder();
					for (int i1 = 0; i1 < (4 - nLength); i1++) {
						sbProjectCount.append("0");
					}
					sbProjectCount.append(nProCount);

					String projectCode = fYear + "/" + strDeptCode + "/" + strWLocationCode + "-" + sbProjectCount.toString();

//					System.out.println("projectcode-" + projectCode);
//					System.out.println("strDeptCode" + strDeptCode);
//					System.out.println("strWLocationCode-" + strWLocationCode);
//					System.out.println("sbProjectCount.toString()--" + sbProjectCount.toString());

					/*pst = con.prepareStatement("insert into projectmntnc (pro_name, priority, description, activity, service, taskstatus, deadline, "
							+ "idealtime, timestatus, client_id, project_code, poc, start_date, added_by,level_id,wlocation_id,billing_type,billing_amount, "
							+ "entry_date, actual_calculation_type, department_id, billing_kind, pro_count,pro_fyear,project_owner,curr_id,sbu_id,org_id,"
							+ "short_description,billing_curr_id,bill_days_type,hours_for_bill_day,billing_cycle_weekday,billing_cycle_day,reference_by_id) "
							+ "values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?)");*/
					pst = con.prepareStatement("insert into projectmntnc (pro_name, priority, description, activity, service, taskstatus, deadline, "
							+ "idealtime, timestatus, client_id, project_code, poc, start_date, added_by,level_id,wlocation_id,billing_type,billing_amount, "
							+ "entry_date, actual_calculation_type, department_id, billing_kind, pro_count,pro_fyear,project_owners,curr_id,sbu_id,org_id,"
							+ "short_description,billing_curr_id,bill_days_type,hours_for_bill_day,billing_cycle_weekday,billing_cycle_day,reference_by_id,"
							+ "portfolio_manager,account_manager,delivery_manager) "
							+ "values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
					pst.setString(1, proName);
					pst.setInt(2, uF.parseToInt(proPriority));
					pst.setString(3, longDesc);
					pst.setString(4, proName);
					
					pst.setInt(5, uF.parseToInt(serviceProjectId));
					pst.setString(6, "New Task");
					pst.setDate(7, uF.getDateFormat(deadLine, DATE_FORMAT));
					pst.setInt(8,(int)uF.parseToDouble(estHours));
					pst.setString(9, "n");
					pst.setInt(10, uF.parseToInt(clientID));
					pst.setString(11, projectCode);
					pst.setInt(12, uF.parseToInt(pocId));
					pst.setDate(13, uF.getDateFormat(startDate, DATE_FORMAT));
					pst.setInt(14, uF.parseToInt((String) session.getAttribute(EMPID)));
					pst.setString(15, "0");
					pst.setInt(16, uF.parseToInt(wlocationId));
					pst.setString(17, setbillingType);
					if (setbillingType != null && (setbillingType.equals("H") || setbillingType.equals("D") || setbillingType.equals("M"))) {
						pst.setDouble(18, uF.parseToDouble("0"));
					} else {
						pst.setDouble(18, uF.parseToDouble(fixedAmount));
					}
					pst.setDate(19, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()) + "", DBDATE));
					if (setbillingType != null && setbillingType.equalsIgnoreCase("F")) {
						pst.setString(20, setAssignmentCosting);
					} else {
						pst.setString(20, setbillingType);
					}
					pst.setInt(21, uF.parseToInt(deptId));
					pst.setString(22, setBillingKind);
					pst.setInt(23, nProCount);
					pst.setString(24, fYear);
					pst.setString(25, empPerID);
					pst.setInt(26, uF.parseToInt(repCurrencyId));
					pst.setInt(27, uF.parseToInt(serviceId));
					pst.setInt(28, uF.parseToInt(orgId));
					pst.setString(29, shortDesc);
					pst.setInt(30, uF.parseToInt(billCurrencyId));
					pst.setInt(31, uF.parseToInt(noOfHoursPerDay) > 0 ? uF.parseToInt(noOfHoursPerDay) : 1);
					pst.setDouble(32, uF.parseToDouble(noOfHoursPerDay));
					pst.setString(33, weekDayCycle);
					pst.setInt(34, uF.parseToInt(dayCycle));
					pst.setInt(35, uF.parseToInt(refEmpPerID));
					pst.setInt(36, uF.parseToInt(pmEmpPerID));
					pst.setInt(37, uF.parseToInt(accMEmpPerID));
					pst.setInt(38, uF.parseToInt(delvMEmpPerID));
					System.out.println("pst====>" + pst);
					pst.executeUpdate();
					pst.close();
//					con.commit();
					if (setbillingType != null && !setbillingType.equalsIgnoreCase("F")) {
						boolean frqFlag = false;
						String freqEndDate = deadLine;
						if (uF.parseToInt(dayCycle) > 0) {

							freqEndDate = dayCycle + "/" + uF.getDateFormat(startDate, DATE_FORMAT, "MM") + "/"
									+ uF.getDateFormat(startDate, DATE_FORMAT, "yyyy");

							Date stDate = uF.getDateFormatUtil(startDate, DATE_FORMAT);
							Date endDate = uF.getDateFormatUtil(deadLine, DATE_FORMAT);
							Date freqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);

							if (freqDate.after(stDate)) {
								frqFlag = true;
							}

							if (frqFlag) {
								freqEndDate = freqEndDate;
							} else if (setBillingKind != null && setBillingKind.equals("M")) {
								freqEndDate = uF.getDateFormat(uF.getFutureMonthDate(freqEndDate, 1) + "", DBDATE, DATE_FORMAT);
							} else if (setBillingKind != null && setBillingKind.equals("B")) {
								freqEndDate = uF.getDateFormat(uF.getFutureDate(startDate, 15) + "", DBDATE, DATE_FORMAT);
							}
							// System.out.println("freqEndDate ====> " +
							// freqEndDate);
							Date newFreqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
							if (newFreqDate.after(endDate)) {
								freqEndDate = deadLine;
							}

						}
						if (weekDayCycle != null && !setBillingKind.equals("") && setBillingKind != null && setBillingKind.equals("W")) {
							freqEndDate = uF.getDateFormat(uF.getDateOfPassedDay(startDate, weekDayCycle) + "", DBDATE, DATE_FORMAT);

							Date endDate = uF.getDateFormatUtil(deadLine, DATE_FORMAT);
							Date newFreqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);

							if (newFreqDate.after(endDate)) {
								freqEndDate = deadLine;
							}
						}
						String proFreqName = "";
						if (setBillingKind != null && setBillingKind.equals("M")) {
							String freqMonth = uF.getDateFormat(freqEndDate, DATE_FORMAT, "MM");
							String freqYear = uF.getDateFormat(freqEndDate, DATE_FORMAT, "yyyy");
							String strMonth = uF.getMonth(uF.parseToInt(freqMonth));
							proFreqName = freqYear + " " + strMonth;
						} else if (setBillingKind != null && setBillingKind.equals("B")) {
							String freqMonth = uF.getDateFormat(freqEndDate, DATE_FORMAT, "MM");
							String freqYear = uF.getDateFormat(freqEndDate, DATE_FORMAT, "yyyy");
							String strMonth = uF.getMonth(uF.parseToInt(freqMonth));
							String freqDate = uF.getDateFormat(freqEndDate, DATE_FORMAT, "dd");
							String strHalf = "- First";
							if (uF.parseToInt(freqDate) > 15) {
								strHalf = "- Second";
							}
							proFreqName = freqYear + " " + strMonth + " " + strHalf;
						} else if (setBillingKind != null && setBillingKind.equals("W")) {
							String freqMonth = uF.getDateFormat(freqEndDate, DATE_FORMAT, "MM");
							String freqYear = uF.getDateFormat(freqEndDate, DATE_FORMAT, "yyyy");
							String strMonth = uF.getMonth(uF.parseToInt(freqMonth));
							String strWeekName = uF.getWeekOfMonthOnPassedDate(freqEndDate);
							proFreqName = freqYear + " " + strMonth + " Week-" + strWeekName;
						}

						pst = con.prepareStatement("insert into projectmntnc_frequency(pro_id, pro_start_date, pro_end_date, freq_start_date, freq_end_date, "
								+ "added_by, entry_date, pro_freq_name) values (?,?,?,?, ?,?,?,?)");
						pst.setInt(1, uF.parseToInt(pocId));
						pst.setDate(2, uF.getDateFormat(startDate, DATE_FORMAT));
						pst.setDate(3, uF.getDateFormat(deadLine, DATE_FORMAT));
						pst.setDate(4, uF.getDateFormat(startDate, DATE_FORMAT));
						if (frqFlag || (setBillingKind != null && setBillingKind.equals("O"))) {
							pst.setDate(5, uF.getDateFormat(freqEndDate, DATE_FORMAT));
						} else {
							pst.setDate(5, uF.getDateFormat(freqEndDate, DATE_FORMAT));
						}
						pst.setInt(6, uF.parseToInt((String) session.getAttribute(EMPID)));
						pst.setDate(7, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()) + "", DBDATE));
						pst.setString(8, proFreqName);
//						System.out.println("pst ====> " + pst);
						pst.execute();
						pst.close();
					}
					flag=true;
				}else{
					alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
							"Please check the Project name"+ " on Row no-"+(i+1)+".</li>");
					flag = false;
					break;
				}
//				System.out.println("cell list--"+cellList);
			}// end of for loop
			
			if(flag){
				con.commit();
				System.out.println("ALL Projects Imported Successfully...");
				session.setAttribute(MESSAGE, SUCCESSM+"Project Imported Successfully!"+END);
				sbMessage.append("</ul>");
				session.setAttribute("sbMessage", sbMessage.toString());
			} else {
				con.rollback();
				if(alErrorList.size()>0){
					sbMessage.append(alErrorList.get(alErrorList.size()-1));
				}
//				request.setAttribute("alReport", alReport);
//				session.setAttribute(MESSAGE, ERRORM+"Attendance not imported. Please check imported file."+END);
				session.setAttribute(MESSAGE, ERRORM+"Project not imported. Please check imported file."+END);
				sbMessage.append("</ul>");
				session.setAttribute("sbMessage", sbMessage.toString());
			}
				
		}catch (Exception e) {
			sbMessage.append("<li class=\"msg_error\" style=\"margin:0px\">There seems to error in the file, please recheck the columns and try again.</li>");
			try {
				con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			if(alErrorList.size()>0){
				sbMessage.append(alErrorList.get(alErrorList.size()-1));
			}
			sbMessage.append("</ul>");
			session.setAttribute("sbMessage", sbMessage.toString());
			session.setAttribute(MESSAGE, ERRORM+"Project not imported. Please check imported file."+END);
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
//===end parvez date: 18-10-2022===	
	
	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public File getFileUpload() {
		return fileUpload;
	}

	public void setFileUpload(File fileUpload) {
		this.fileUpload = fileUpload;
	}

	public String getFileUploadFileName() {
		return fileUploadFileName;
	}

	public void setFileUploadFileName(String fileUploadFileName) {
		this.fileUploadFileName = fileUploadFileName;
	}

	public String getBtnSubmit() {
		return btnSubmit;
	}

	public void setBtnSubmit(String btnSubmit) {
		this.btnSubmit = btnSubmit;
	}

}
