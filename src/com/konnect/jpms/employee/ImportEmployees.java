package com.konnect.jpms.employee;

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

import com.konnect.jpms.select.FillEmploymentType;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ImportEmployees extends ActionSupport implements ServletRequestAware, IConstants, IStatements {
	private static final long serialVersionUID = 1L;

	File fileUpload;
	String fileUploadFileName;
	HttpSession session;

	CommonFunctions CF;
	String strSessionEmpId;   

	private String fromPage; 
	private String mode;
	
	private List<FillEmploymentType> empTypeList;
	
	private String[] f_strWLocation; 
	private String f_org;
	
	private List<FillWLocation> wLocationList;
	private List<FillOrganisation> organisationList;
	UtilityFunctions uF = new UtilityFunctions();
	
	public String execute() throws Exception {
//		session = ActionContext.getContext().getSession();
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		CF  = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;

		request.setAttribute(PAGE, "/jsp/employee/AddEmployeeMode.jsp");
		request.setAttribute(TITLE, "Add Employee Mode");

		if (getFileUpload() != null) {
			loadExcel(getFileUpload());
		}

		organisationList = new FillOrganisation(request).fillOrganisation();
		wLocationList = new FillWLocation(request).fillWLocation(null);
		
		if(fromPage !=null && fromPage.equals("P")) {
			return VIEW;
		}
		return "success";
	}
	
	public void loadExcel(File file) throws IOException {

		Database db = new Database();
		db.setRequest(request);
		StringBuilder sbMessage = new StringBuilder("<ul style=\"margin:0px\">");

		PreparedStatement pst=null;
		Connection con=null;
		ResultSet rs=null;
		
		
		PreparedStatement pst1=null;
		ResultSet rs1=null;
		
		List<String> alErrorList = new ArrayList<String>();
		try {

			UtilityFunctions uF = new UtilityFunctions();
			
			AddEmployeeMode aE = new AddEmployeeMode();
			aE.request = request;
			aE.session = session;
			aE.CF = CF;
			
			con = db.makeConnection(con);
			con.setAutoCommit(false);
			
			empTypeList = new FillEmploymentType().fillEmploymentType(request);
			
			Map<String,String> userPresent= CF.getUsersMap(con);
			FileInputStream fis = new FileInputStream(file);
//			Map<String, String> hmEmpLeaveStartDateMap = CF.getEmpProbationEndDateMap(con, uF);
//			System.out.println("getFileUploadFileName======>"+getFileUploadFileName());
			XSSFWorkbook workbook = new XSSFWorkbook(fis);

//			System.out.println("Employ Detail case 6 Start======>");
//				XSSFSheet employsheet = workbook.getSheet("Employee Details");
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

//			System.out.println("Size of Employee details rows==>" + dataList.size());
//			System.out.println("Employee details==>" + dataList);
			
			Map<String,String> stateMp=new HashMap<String,String>();
			Map<String,String> countryMp=new HashMap<String,String>();
			pst = con.prepareStatement("Select state_id,country_id,upper(state_name) as state_name from state");
			rs = pst.executeQuery();
			while (rs.next()) {
				stateMp.put(rs.getString("state_name"), rs.getString("state_id"));
				countryMp.put(rs.getString("state_name"), rs.getString("country_id"));
			}
			rs.close();
			pst.close();
			
			Map<String,String> branchMp=new HashMap<String,String>();
			pst = con.prepareStatement("Select branch_id,upper(branch_code) as branch_code from branch_details");
			rs = pst.executeQuery();
			while (rs.next()) {
				branchMp.put(rs.getString("branch_code").trim(), rs.getString("branch_id"));
			}
			rs.close();
			pst.close();
			
			List<String> empCodeList=new ArrayList<String>();
			pst = con.prepareStatement("Select upper(empcode) as empcode from employee_personal_details ");
			rs = pst.executeQuery();
			while (rs.next()) {
				empCodeList.add(rs.getString("empcode"));
			}
			rs.close();
			pst.close();
			
			Map<String,String> orgMp=new HashMap<String,String>();
			pst = con.prepareStatement("Select org_id,upper(org_code) as org_code from org_details ");
			rs = pst.executeQuery();
			while (rs.next()) {		
				orgMp.put(rs.getString("org_code"), rs.getString("org_id"));
			}
			rs.close();
			pst.close();
			
			Map<String,String> wLocationMp=new HashMap<String,String>();
			pst = con.prepareStatement("Select wlocation_id,upper(wlocation_name) as wlocation_name,org_id from work_location_info");
			rs = pst.executeQuery();
			while (rs.next()) {
				wLocationMp.put(rs.getString("wlocation_name")+"_"+rs.getString("org_id"), rs.getString("wlocation_id"));
			}
			rs.close();
			pst.close();
			
//			System.out.println("wLocationMp=="+wLocationMp);
			//for(Map.Entry<String, String>entry:)
			/*for(int i=0;i<wLocationMp.size();i++){
				System.out.println(""+wLocationMp.get(i));
			}*/
			
			Map<String,String> departmentMp=new HashMap<String,String>();
			pst = con.prepareStatement("Select dept_id,upper(dept_name) as dept_name,org_id from department_info");
			rs = pst.executeQuery();
			while (rs.next()) {
				departmentMp.put(rs.getString("dept_name")+"_"+rs.getString("org_id"), rs.getString("dept_id"));
			}
			rs.close();
			pst.close();

			Map<String,String> servicesMp=new HashMap<String,String>();
			pst = con.prepareStatement("Select service_id,upper(service_name) as service_name,org_id from services");
			rs = pst.executeQuery();
			while (rs.next()) {
				servicesMp.put(rs.getString("service_name")+"_"+rs.getString("org_id"), rs.getString("service_id"));
			}
			rs.close();
			pst.close();

			Map<String,String> levelMp=new HashMap<String,String>();
			pst = con.prepareStatement("Select level_id,upper(level_code) as level_code, org_id from level_details");
			rs = pst.executeQuery();
			while (rs.next()) {
				levelMp.put(rs.getString("level_code")+"_"+rs.getString("org_id"), rs.getString("level_id"));
			}
			rs.close();
			pst.close();

			Map<String,String> designationMp=new HashMap<String,String>();
			pst = con.prepareStatement("Select designation_id,upper(designation_code) as designation_code,level_id from designation_details");
			rs = pst.executeQuery();
			while (rs.next()) {
				designationMp.put(rs.getString("designation_code")+"_"+rs.getString("level_id"), rs.getString("designation_id"));
			}
			rs.close();
			pst.close();

			Map<String,String> gradesMp=new HashMap<String,String>();
			pst = con.prepareStatement("Select grade_id,designation_id,upper(grade_code) as grade_code from grades_details");
			rs = pst.executeQuery();
			while (rs.next()) {
				gradesMp.put(rs.getString("grade_code")+"_"+rs.getString("designation_id"), rs.getString("grade_id"));
			}
			rs.close();
			pst.close();
			
			Map<String,String> hmBloodGroup = CF.getBloodGroupMap();
			
			boolean flag = false;
			ArrayList<String> checkFlagList = new ArrayList<String>();
			int ii = 0;
//			System.out.println("IE/241---dataList.size() ===>> " + dataList.size());
			for (int i = 2; i < dataList.size(); i++) {
				
				boolean errorFlag = false;
				List<String> cellList = dataList.get(i);
//				System.out.println("IE/245--- cellList.size()===>"+ cellList);
				if(cellList.size() == 0 || cellList.get(1) == null || cellList.get(1).trim().equals("") || cellList.get(1).trim().equalsIgnoreCase("NULL")){
					if(cellList.size() >= 5){
					alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" + cellList.get(2) + " " + cellList.get(3)
							+ ", please check the Employee code ('"+cellList.get(1)+"') of this employee and try again.</li>");
					}
					continue;
				} else if(cellList.size() < 5) {
					alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check the columns.</li>");
					flag = false;
//					break;
					errorFlag = true;
				}
				ii++;
				
				String empcode = cellList.get(1);
				String empFName = cellList.get(2);
				String empMName = cellList.get(3);
				String empLName = cellList.get(4);

				if (empcode.contains(".")) {
					empcode = empcode.substring(0, empcode.indexOf("."));
				} 
				
				if(empCodeList.contains(empcode.toUpperCase().trim())){
					alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" + empFName + " " + empLName
							+ " already exists, please check the Employee code ('"+empcode+"') of this employee and try again.</li>");
					flag = false;	
//					break;
					errorFlag = true;
				}
				
				if(cellList.size()<46) {
					alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" + empFName + " " + empLName
							+ ", please check the columns, need 46 columns in this row but only "+cellList.size()+" columns are there.</li>");
					flag = false;	
//					break;
					errorFlag = true;
				}
//				System.out.println("IE/291--- cellList.size()===>"+ cellList.size());
				
				empCodeList.add(empcode.toUpperCase().trim());
			
				String pAddress = cellList.get(5);
				String city = cellList.get(6);
				String pincode = cellList.get(7);
				String state = cellList.get(8);

//						String country = cellList.get(9);
				String tAddress = cellList.get(10);
				String tCity = cellList.get(11);
				String tPincode = cellList.get(12);
				String tState = cellList.get(13);
//						String tCountry = cellList.get(14);
				String email = cellList.get(15);
				String cEmail = cellList.get(16);
				String mobile = cellList.get(17);
				String phone = cellList.get(18);
				String emergengyCname = cellList.get(19);
				String emergengyContact = cellList.get(20);
				String panNo = cellList.get(21);
				String gender = cellList.get(22).trim();
				String dob = cellList.get(23);
				String empType = cellList.get(24).trim();
				String branchName = cellList.get(25);
				String service = cellList.get(26);
				String department = cellList.get(27);
				String designation = cellList.get(28);
				String grade = cellList.get(29);
				String DOJ = cellList.get(30);
				
				String bankAC = cellList.get(31);
				String bankBranch = cellList.get(32);
				
//				System.out.println("bankBranch"+bankBranch);
				
				String PFNO = cellList.get(33);
				String orgCode = cellList.get(34);
				String levelCode = cellList.get(35);
				
				String UID_NO = cellList.get(36);
				String UAN_No = cellList.get(37);
				String emp_esic_no = cellList.get(38);
				
				String seperationDate = cellList.get(39);
				String confirmaratonDate = cellList.get(40);
				String actualConfirmationDate = cellList.get(41);
				String promotionDate = cellList.get(42);
				String incrementDate = cellList.get(43);
				String fatherName = cellList.get(44);
				String bloodGroup = cellList.get(45);
				String bioMatricMachineId = cellList.get(46);
				
				/*if(i==2){
					System.out.println("IE/347---orgCode.toUpperCase().trim() ===>> " + orgCode.toUpperCase().trim());
					System.out.println("IE/348---orgMp ===>> " + orgMp);
				}*/
				
				int orgId=uF.parseToInt(orgMp.get(orgCode.toUpperCase().trim()));
				if(orgId == 0) {
					alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
							"Please check the Organisation Code for Employee " + empFName + " " + empLName+"('"+empcode+"').</li>");
					flag = false;
//					break;
					errorFlag = true;
				}
				
				
				if(!gender.equalsIgnoreCase("male") && !gender.equalsIgnoreCase("m") && !gender.equalsIgnoreCase("female") && !gender.equalsIgnoreCase("f")) {
					alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
							"Please check the Gender for Employee " + empFName + " " + empLName+"('"+empcode+"').</li>");
					flag = false;
//					break;
					errorFlag = true;
				}/*else{
					System.out.println("false gender===>"+gender);
				}*/
				
				/*if(i==2){
					System.out.println("--"+branchName.toUpperCase().trim()+"_"+orgId);
					System.out.println("wLocationMp=="+wLocationMp);
				}*/
				
				
				
				int locationId=uF.parseToInt(wLocationMp.get(branchName.toUpperCase().trim()+"_"+orgId));
				/*if(i==2){
					System.out.println("locationId**"+locationId);
				}*/
				
				
				if(locationId == 0) {
					alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
							"Please check the Branch Name(Work Location) for Employee " + empFName + " " + empLName+"('"+empcode+"').</li>");
					flag = false;
//					break;
					errorFlag = true;
				}
				
				int departmentId = uF.parseToInt(departmentMp.get(department.toUpperCase().trim()+"_"+orgId));
				if(departmentId == 0) {
					alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
						"Please check the Department Name for Employee " + empFName + " " + empLName+"('"+empcode+"').</li>");
					flag = false;
//					break;
					errorFlag = true;
				}
				
				int servicesId = uF.parseToInt(servicesMp.get(service.toUpperCase().trim()+"_"+orgId));
				if(servicesId == 0) {
					alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
						"Please check the Service Name for Employee " + empFName + " " + empLName+"('"+empcode+"').</li>");
					flag = false;
//					break;
					errorFlag = true;
				}
				
				
				int levelId = uF.parseToInt(levelMp.get(levelCode.toUpperCase().trim()+"_"+orgId));
				if(levelId == 0) {
					alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
							"Please check the Level Code for Employee " + empFName + " " + empLName+"('"+empcode+"').</li>");
					flag = false;
//					break;
					errorFlag = true;
				}
				/*if(empcode.equals("KCS002(C)")){
					System.out.println("levelMp=="+levelMp);
					System.out.println("levelCode=="+levelCode.toUpperCase().trim());
					System.out.println("levelId=="+levelCode.toUpperCase().trim()+"_"+orgId);
				}*/
				
				pst1 = con.prepareStatement("select designation_id from designation_details where level_id=? and designation_code=? ");
				pst1.setInt(1, levelId);
				pst1.setString(2, designation.trim());
				rs1 = pst1.executeQuery();
				/*if(empcode.equals("KCS002(C)") || empcode.equals("US0065") || empcode.equals("US0118") || empcode.equals("US0054") || empcode.equals("US0078") || empcode.equals("US0088")){
					System.out.println("IEmp/418---pst=="+pst1);
				}*/
				if (rs1.next()) {
					//System.out.println("====>True Case");
				} else {
					alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
						"Please check the  Level for Employee " + empFName + " " + empLName+"('"+empcode+"') is not aligned with the Designation Name. </li>");
					flag = false;
//					break;
					errorFlag = true;
				}
				rs1.close();
				pst1.close();
				
				int desigId = uF.parseToInt(designationMp.get(designation.toUpperCase().trim()+"_"+levelId));
				if(desigId == 0){
					alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
							"Please check the Designation Name for Employee " + empFName + " " + empLName+"('"+empcode+"').</li>");
					flag = false;	
//					break;
					errorFlag = true;
				}
				
				/*if(empcode.equals("IN000619")){
					System.out.println("IEmp/442---gradesMp="+gradesMp);
					System.out.println("IEmp/443---grade.toUpperCase().trim()_desigId==="+grade.toUpperCase().trim()+"_"+desigId);
					System.out.println("IEmp/444---gradeId==="+gradesMp.get(grade.toUpperCase().trim()+"_"+desigId));
				}*/
				
				
				int gradeId = uF.parseToInt(gradesMp.get(grade.toUpperCase().trim()+"_"+desigId));
				if(gradeId == 0){
					alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
							"Please check the Grade Code for Employee " + empFName + " " + empLName+"('"+empcode+"').</li>");
					flag = false;
//					break;
					errorFlag = true;
				}
				
				if(!uF.isThisDateValid(dob, DATE_FORMAT)){
					alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
							"Please check Date of birth for Employee " + empFName + " " + empLName+"('"+empcode+"').</li>");
					flag = false;
//					break;
					errorFlag = true;
				}
				
				if(!uF.isThisDateValid(DOJ, DATE_FORMAT)){
					alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
							"Please check Date of joining for Employee " + empFName + " " + empLName+"('"+empcode+"').</li>");
					flag = false;
//					break;
					errorFlag = true;
				}
				
			//===start parvez date: 15-04-2022===	
				boolean empTypeFlag = false;
				for(int a=0; empTypeList != null && a<empTypeList.size(); a++) {
//					System.out.println("IEMP/493---empType="+empType+"---empTypeList"+empTypeList.get(a).getEmpTypeId());
					if(empType != null && !empType.equals("") && empType.equals(empTypeList.get(a).getEmpTypeId())) {
						empTypeFlag = true;
					} else if(empType != null && !empType.equals("") && empType.equals(empTypeList.get(a).getEmpTypeName())) {
						empTypeFlag = true;
					} /*else {
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"Please check Employment Type for Employee " + empFName + " " + empLName+"('"+empcode+"').</li>");
						flag = false;	
//						break;
//						errorFlag = true;
					}*/
				}
				
				if(!empTypeFlag){
					alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
							"Please check Employment Type for Employee " + empFName + " " + empLName+"('"+empcode+"').</li>");
					flag = false;
				}
			//===end parvez date: 15-04-2022===		
				
				int bankBranchId = 0;
				if(bankBranch.toUpperCase().trim().length()>0) {
					bankBranchId = uF.parseToInt(branchMp.get(bankBranch.toUpperCase().trim()));
//					System.out.println(bankBranchId + " -- bankBranch ==> " + bankBranch.toUpperCase().trim()+" -- branchMp ===>> "+branchMp);
					if(bankBranchId == 0) {
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"Please check the Bank Branch Code for Employee " + empFName + " " + empLName+"('"+empcode+"').</li>");
						flag = false;
//						break;
						errorFlag = true;
					}
				}
				
				// Insert Employ personal Detail
//				alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
//						"Please check the Employee Personal Details for Employee " + empFName + " " + empLName+"('"+empcode+"').</li>");
				
//				if(!errorFlag){
					pst = con.prepareStatement("insert into employee_personal_details(empcode, emp_fname,emp_mname, emp_lname, emp_address1,"
							+ "emp_city_id, emp_pincode, emp_state_id, emp_country_id,emp_address1_tmp,emp_city_id_tmp, emp_pincode_tmp," +
							" emp_state_id_tmp, emp_country_id_tmp,emp_email, emp_email_sec, emp_contactno_mob, emp_contactno," +
							" emergency_contact_name, emergency_contact_no, emp_pan_no, emp_gender, emp_date_of_birth, emp_image," +
							" emp_status, joining_date,approved_flag, is_alive, emp_filled_flag, emp_entry_date,emp_bank_acct_nbr," +
							" emp_bank_name, emp_pf_no,uid_no,uan_no,emp_esic_no,separation_date,confirmation_date,actual_confirmation_date," +
							"promotion_date,increment_date,blood_group)" 
							+ "values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
					pst.setString(1, empcode.trim());
					pst.setString(2, empFName.trim());
					pst.setString(3, empMName.trim());
					pst.setString(4, empLName.trim());
					pst.setString(5, pAddress.trim());
					pst.setString(6, city.trim());
					pst.setString(7, getStringValue(pincode.trim()));
					pst.setInt(8, uF.parseToInt(stateMp.get(state.toUpperCase().trim())));
					pst.setInt(9, uF.parseToInt(countryMp.get(state.toUpperCase().trim())));
	
					pst.setString(10, tAddress.trim());
					pst.setString(11, tCity.trim());
					pst.setString(12, getStringValue(tPincode.trim()));
					pst.setInt(13, uF.parseToInt(stateMp.get(tState.toUpperCase().trim())));
					pst.setInt(14, uF.parseToInt(countryMp.get(tState.toUpperCase().trim())));
	
					pst.setString(15, email.trim());
					pst.setString(16, cEmail.trim());
					//
					pst.setString(17, getStringValue(mobile.trim()));
					pst.setString(18, getStringValue(phone.trim()));
					pst.setString(19, emergengyCname.trim());
					pst.setString(20, getStringValue(emergengyContact.trim()));
	
					pst.setString(21, panNo.trim());
					pst.setString(22, uF.getGenderCode(gender.trim()));
					pst.setDate(23, uF.getDateFormat(dob, DATE_FORMAT));
					pst.setString(24, "avatar_photo.png");
					pst.setString(25, PERMANENT);
					pst.setDate(26, uF.getDateFormat(DOJ, DATE_FORMAT));
					pst.setBoolean(27, true);
					pst.setBoolean(28, true);
					pst.setBoolean(29, true);
					pst.setDate(30, uF.getCurrentDate(CF.getStrTimeZone()));
					
					pst.setString(31, getStringValue(bankAC.trim()));
					pst.setString(32, (bankBranchId>0) ? bankBranchId+"" : null);
					pst.setString(33, PFNO.trim());
					pst.setString(34, UID_NO.trim());
					pst.setString(35, UAN_No.trim());
					pst.setString(36, emp_esic_no.trim());
					
					pst.setDate(37, uF.isThisDateValid(seperationDate, DATE_FORMAT) ? uF.getDateFormat(seperationDate, DATE_FORMAT) : null);
					pst.setDate(38, uF.isThisDateValid(confirmaratonDate, DATE_FORMAT) ? uF.getDateFormat(confirmaratonDate, DATE_FORMAT) : null);
					pst.setDate(39, uF.isThisDateValid(actualConfirmationDate, DATE_FORMAT) ? uF.getDateFormat(actualConfirmationDate, DATE_FORMAT) : null);
					pst.setDate(40, uF.isThisDateValid(promotionDate, DATE_FORMAT) ? uF.getDateFormat(promotionDate, DATE_FORMAT) : null);
					pst.setDate(41, uF.isThisDateValid(incrementDate, DATE_FORMAT) ? uF.getDateFormat(incrementDate, DATE_FORMAT) : null);
					pst.setString(42, (bloodGroup != null && hmBloodGroup != null && hmBloodGroup.containsKey(bloodGroup)) ? hmBloodGroup.get(bloodGroup) : null);
	//				System.out.println("pst=====>"+pst);
					pst.execute();
					pst.close();
//				}

//				System.out.println("Employ Personal Detail Query Excuted Sucessfully");
					
				// select Employee id
				int employee_id=0;
				pst = con.prepareStatement("Select emp_per_id from employee_personal_details where upper(empcode) = ? ");
				pst.setString(1, empcode.toUpperCase().trim());
				rs = pst.executeQuery();
				while (rs.next()) {
					employee_id = rs.getInt("emp_per_id");
				}
				rs.close();
				pst.close();
				
				if(employee_id == 0){
					alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
							"Please check the Employee Personal Details for Employee " + empFName + " " + empLName+"('"+empcode+"').</li>");
					flag = false;
//					break;
					errorFlag = true;
				}
				
				boolean biometrixIdFlag = false;
//				System.out.println("bioMatricMachineId.trim() ===>> " + bioMatricMachineId.trim());
				pst = con.prepareStatement("Select biometrix_id from employee_official_details where biometrix_id>0 and biometrix_id=?");
				pst.setInt(1, uF.parseToInt(getStringValue(bioMatricMachineId.trim())));
				rs = pst.executeQuery();
//				System.out.println("pst ===>> " + pst);
				while (rs.next()) {
					biometrixIdFlag = true;
				}
				rs.close();
				pst.close();
				
				if(biometrixIdFlag) {
					alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
							"Please check the Bio-matric Machine Id already exists, for Employee " + empFName + " " + empLName+"('"+empcode+"').</li>");
					flag = false;	
//					break;
					errorFlag = true;
				}
				
				// insert into employ official detail
//				alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
//						"Please check the Employee Official Details for Employee " + empFName + " " + empLName+"('"+empcode+"').</li>");
				
//				if(!errorFlag){
					pst = con.prepareStatement("insert into employee_official_details(depart_id,supervisor_emp_id,service_id,emp_id,wlocation_id," +
						"is_roster, emptype, first_aid_allowance, grade_id, paycycle_duration,org_id,payment_mode,biometrix_id) " +
						"values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?)");
					pst.setInt(1, departmentId);
					pst.setInt(2, 0);
					pst.setString(3, "," + servicesId + ",");
					pst.setInt(4, employee_id);
					pst.setInt(5, locationId);
					pst.setBoolean(6, true);
					pst.setString(7, uF.getEmploymentTypeCode(empType));
					pst.setBoolean(8, true);
					pst.setInt(9, gradeId);
					pst.setString(10, "M");
					pst.setInt(11, orgId);
					pst.setInt(12, 1);
					pst.setInt(13, uF.parseToInt(getStringValue(bioMatricMachineId.trim())));
	//				System.out.println("pst=====>"+pst);
					int x = pst.executeUpdate();
					pst.close();
					
					if(x > 0) {
						CF.assignReimbursementCTCForEmployee(request,con,CF,uF, employee_id);
						
						pst = con.prepareStatement("select * from level_details ld, grades_details gd, designation_details dd where dd.level_id = ld.level_id and dd.designation_id = gd.designation_id and grade_id = (select grade_id from employee_official_details where emp_id = ?)");
						pst.setInt(1, employee_id);
						rs = pst.executeQuery();
						int nLevelId = 0;
						while (rs.next()) {
							nLevelId = rs.getInt("level_id");
						}
						rs.close();
						pst.close();
						
						if(nLevelId == 0){
							alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
									"Please check the Grade Code for Employee " + empFName + " " + empLName+"('"+empcode+"').</li>");
							flag = false;
	//						break;
						}
							
//						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
//								"Please check the  Employee Activity Details for Employee " + empFName + " " + empLName+"('"+empcode+"').</li>");
						pst = con.prepareStatement("insert into employee_activity_details (wlocation_id, department_id, level_id, desig_id, grade_id, " +
						"emp_status_code, activity_id, reason, effective_date, entry_date, user_id, emp_id, notice_period, probation_period, appraisal_id, " +
						"extend_probation_period, org_id,service_id,increment_type,increment_percent) values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?)");
						pst.setInt(1, locationId);
						pst.setInt(2, departmentId);
						pst.setInt(3,nLevelId);
						pst.setInt(4, desigId);
						pst.setInt(5, gradeId);
						pst.setString(6, PERMANENT);
						pst.setInt(7, uF.parseToInt(ACTIVITY_NEW_JOINING));
						pst.setString(8, "New Employee");
						//pst.setDate(9, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setDate(9, uF.getDateFormat(DOJ, DATE_FORMAT));
						pst.setDate(10, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setInt(11, uF.parseToInt(strSessionEmpId));
						pst.setInt(12, employee_id);
						pst.setInt(13,  0);
						pst.setInt(14, 0);
						pst.setInt(15, 0);
						pst.setInt(16, 0);
						pst.setInt(17, orgId);
						pst.setInt(18, servicesId);
						pst.setInt(19, 0);
						pst.setDouble(20, 0);
	//					System.out.println("pst=====>"+pst);
						int y = pst.executeUpdate();
						pst.close();
						
						if(y == 0) {
							alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"Please check the Employee Activity Details for Employee " + empFName + " " + empLName+"('"+empcode+"').</li>");
							flag = false;	
	//						break;
						}
	
						SecureRandom random = new SecureRandom();
						String password = new BigInteger(130, random).toString(32).substring(5, 13);
						// insert into employ User detail
//						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
//							"Please check the Employee First and Last Name for Employee " + empFName + " " + empLName+"('"+empcode+"').</li>");
						aE.setFname(empFName.trim());
						aE.setLname(empLName.trim());
						String username = aE.getUserName(userPresent);
						
						pst = con.prepareStatement("insert into user_details(username,password,usertype_id,emp_id,status,is_termscondition," +
							"added_timestamp) values (?,?,?,?, ?,?,?) ");
						pst.setString(1, username);
						pst.setString(2, password);
						pst.setInt(3, 3);
						pst.setInt(4, employee_id);
						pst.setString(5, "ACTIVE");
						pst.setBoolean(6, true);
						pst.setTimestamp(7, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
						int z = pst.executeUpdate();
						pst.close();
						
						if(z == 0) {
							alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"Please check the Employee First and Last Name for Employee " + empFName + " " + empLName+"('"+empcode+"').</li>");
							flag = false;	
	//						break;
						}
						
						pst = con.prepareStatement("Select * from user_details where username=? and password=?");
						pst.setString(1, username);
						pst.setString(2, password);
						rs = pst.executeQuery();
						while (rs.next()) {
							userPresent.put(rs.getString("user_id"), rs.getString("username"));
						}
						rs.close();
						pst.close();
						
						/**
						 * Employee Father Family Information
						 * */
						pst = con.prepareStatement("INSERT INTO emp_family_members(member_type,member_name,member_dob,member_education, "
							+ "member_occupation,member_contact_no,member_email_id,member_gender,member_marital,emp_id,mrd_no) "
							+ "VALUES (?,?,?,?, ?,?,?,?, ?,?,?)");
						pst.setString(1, "FATHER");
						pst.setString(2, fatherName);
						pst.setDate(3, null);
						pst.setString(4, null);
						pst.setString(5, null);
						pst.setString(6, null);
						pst.setString(7, null);
						pst.setString(8, "M");
						pst.setString(9, "M");
						pst.setInt(10, employee_id);
						pst.setString(11, null);
						pst.execute();
						pst.close();
						
						flag = true;
					} else {
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
							"Please check the Employee Official Details for Employee " + empFName + " " + empLName+"('"+empcode+"').</li>");
						flag = false;	
	//					break;
					}
//				}
				checkFlagList.add(errorFlag+"");
				
			}// end for loop
			
			if(ii == 0 ){
				con.rollback();
				if(alErrorList.size()>0){
					sbMessage.append(alErrorList.get(alErrorList.size()-1));
				} else {
					sbMessage.append("<li class=\"msg_error\" style=\"margin:0px\">Employee not imported. Please check imported file.</li>");
				}
				session.setAttribute(MESSAGE, ERRORM+"Employee not imported. Please check imported file."+END);
				sbMessage.append("</ul>");
				System.out.println("IE/804---sbMessage in rollback if ===>> " + sbMessage.toString());
				request.setAttribute("sbMessage", sbMessage.toString());
			} else {
				if(flag) {
					
				//===start parvez date: 14-09-2022===
					System.out.println("IEmp/827---checkFlagList=="+checkFlagList);
					if(checkFlagList.contains("true")){
						con.rollback();
//						System.out.println("rollback==>");
						if(alErrorList.size()>0) {
						
//							sbMessage.append(alErrorList.get(alErrorList.size()-1));
							for(int k=0 ; k<alErrorList.size(); k++){
								sbMessage.append(alErrorList.get(k));
							}
						}
						session.setAttribute(MESSAGE, ERRORM+"Employee not imported. Please check imported file."+END);
						sbMessage.append("</ul>");
//						System.out.println("sbMessage in rollback else else ===>> " + sbMessage.toString());
						request.setAttribute("sbMessage", sbMessage.toString());
					} else{
						con.commit();
						session.setAttribute(MESSAGE, SUCCESSM+"Employee Imported Successfully!"+END);
						sbMessage.append("<li class=\"msg savesuccess\" style=\"margin:0px\">Employee Imported Successfully!</li>");
						sbMessage.append("</ul>");
//						System.out.println("sbMessage in commit else if ===>> " + sbMessage.toString());
						
						request.setAttribute("sbMessage", sbMessage.toString());
					}
				//===end parvez date: 14-09-2022===	
					
				} else {
					con.rollback();
//					System.out.println("rollback==>");
					if(alErrorList.size()>0) {
					
//						sbMessage.append(alErrorList.get(alErrorList.size()-1));
						for(int k=0 ; k<alErrorList.size(); k++){
							sbMessage.append(alErrorList.get(k));
						}
					}
					session.setAttribute(MESSAGE, ERRORM+"Employee not imported. Please check imported file."+END);
					sbMessage.append("</ul>");
//					System.out.println("sbMessage in rollback else else ===>> " + sbMessage.toString());
					request.setAttribute("sbMessage", sbMessage.toString());
				}
			}
				
		} catch (Exception e) {
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
//			System.out.println("sbMessage in catch ===>> " + sbMessage.toString());
			request.setAttribute("sbMessage", sbMessage.toString());
			session.setAttribute(MESSAGE, ERRORM+"Employee not imported. Please check imported file."+END);
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request){
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
	
	public String getStringValue(String str) {
		
		try {
			if(uF.parseToDouble(str)>0) {
				str = String.valueOf(Double.valueOf(str).longValue());
			} 
		} catch(Exception ex) {
			
		}
		return str;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public List<FillEmploymentType> getEmpTypeList() {
		return empTypeList;
	}

	public void setEmpTypeList(List<FillEmploymentType> empTypeList) {
		this.empTypeList = empTypeList;
	}

	public String[] getF_strWLocation() {
		return f_strWLocation;
	}

	public void setF_strWLocation(String[] f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
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

	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}

	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}

}