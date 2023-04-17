package com.konnect.jpms.requsitions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillTravel;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ImportReimbursements extends ActionSupport implements ServletRequestAware, IConstants, IStatements {
	private static final long serialVersionUID = 1L;

	File fileUpload;
	String fileUploadFileName;
	HttpSession session;

	CommonFunctions CF;
	String strSessionEmpId;

	public String execute() throws Exception {
//		session = ActionContext.getContext().getSession();
		
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		CF  = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;

		request.setAttribute(PAGE, "/jsp/requisitions/ImportReimbursements.jsp");
		request.setAttribute(TITLE, "Add Reimbursements Mode");

		if (getFileUpload() != null) { 
			loadExcel(getFileUpload());
		}

		return LOAD;
	}
	
	
	public String loadExcel(File file) throws IOException {

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
				
//				System.out.println("cellist==>"+cellList);
				/*if (cellList.size() < 5) {
					continue;
				}*/
					
				// Employee Code
				String empCode=cellList.get(1);
										
				String emp_per_id="";
				String orgId="";
						
				if (empCode != null && !empCode.equals("")){
					
					pst = con.prepareStatement("select emp_per_id from employee_personal_details where upper(empcode)=?");
					pst.setString(1, empCode.toUpperCase().trim());
					rs = pst.executeQuery();
					while (rs.next()) {
						emp_per_id = rs.getString("emp_per_id");
					}
					
					rs.close();
					pst.close();
					
				}else{
						// error empCode
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"Please check the Emplyee Code "+ " on Row no-"+(i+1)+".</li>");
						flag = false;
						break;
						
				}
				if(uF.parseToInt(emp_per_id)==0){
						
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								" Employee Code does not exists,check the Employee Code"+ " on Row no-"+(i+1)+".</li>");
						flag = false;
						break;
				}else{
						
						pst = con.prepareStatement("select org_id from employee_official_details where emp_id=?");
						pst.setInt(1, uF.parseToInt(emp_per_id));
						rs = pst.executeQuery();
						while (rs.next()) {
							orgId = rs.getString("org_id");
						}
						
						rs.close();
						pst.close();
					
				}
														
					//Get Current PayCycle 
				String[] strPayCycleDates = null;
				strPayCycleDates = CF.getCurrentPayCycleByOrg(CF.getStrTimeZone(), CF,orgId,request);

				String strStartDate = strPayCycleDates[0];
				String strEndDate = strPayCycleDates[1];
				String strPayCycle = strPayCycleDates[2];
			
				
				String reimbursementPlan =cellList.get(2);
//				System.out.println("Paycycle--"+reimbursementPlan);
				
				// Number of Employees
				String numberOfEmployees=cellList.get(8);
				if (numberOfEmployees == null || numberOfEmployees.equals("")){
					alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
							"Please check the Number of Employees "+ " on Row no-"+(i+1)+".</li>");
					flag = false;
					break;
					
				}
//				System.out.println("no--emp--"+uF.parseToInt(numberOfEmployees));
						
				// Vendor
				String vendor=cellList.get(9);
				
				//Reciept No.
				
				String receiptNo=cellList.get(10);

				// Amount 
				String amount =cellList.get(11);
				if (amount == null || amount.equals("")){
					alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
							"Please check the Amount "+ " on Row no-"+(i+1)+".</li>");
					flag = false;
					break;
					
				}
					// Purpose
				String purpose =cellList.get(12);
				if (purpose == null || purpose.equals("")){
					alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
							"Please check the Purpose "+ " on Row no-"+(i+1)+".</li>");
					flag = false;
					break;
					
				}
				String mainTravelPlan="";		
				String mainTravelType="";
				String clientID ="";			
				String proId="";
				boolean isBillable = true;
				String plcFrom="";
				String plcTo="";
				String noOfDays="";
				String totalKM="";
				String rateKM="";
				
				String modeOfTravel="";
				
				
				
				
				
					
				if (reimbursementPlan != null && !reimbursementPlan.equals("") && reimbursementPlan.toUpperCase().trim().equals("T")) {
											
					String travelPlan=cellList.get(3);
							
					List<FillTravel> travelPlanList;
					travelPlanList = new FillTravel(request).fillTravelPlan(uF.parseToInt((String) emp_per_id));
				
					if(travelPlanList.isEmpty() || travelPlanList==null || travelPlanList.size()<0){
//						System.out.println("Break");
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								" Travel  Plan does not exists,check the Travel Plan"+ " on Row no-"+(i+1)+".</li>");
						flag = false;
						break;
					}
							
					for(int k=0;k<travelPlanList.size() && !travelPlanList.isEmpty();k++){
						
							if(travelPlan!=null &&  !travelPlan.equals("")){
							
								if(travelPlanList.get(k).getPlanName().toUpperCase().trim().equals(travelPlan.toUpperCase().trim())){
								
									mainTravelPlan=travelPlanList.get(k).getLeaveId();
									
									
																		
								}
							}
							
					
					}
					
					// main travel plan empty or not
					if (mainTravelPlan == null || mainTravelPlan.equals("")){
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"Travel  Plan does not exists,check the Travel Plan"+ " on Row no-"+(i+1)+".</li>");
						flag = false;
						break;
						
					}
											
				}else if(reimbursementPlan != null && !reimbursementPlan.equals("") && reimbursementPlan.toUpperCase().trim().equals("O")){
						
							String travelType=cellList.get(4);
							
//							System.out.println("travelType==>"+travelType);
							if(travelType!=null &&  !travelType.equals("")){
								if(travelType.toUpperCase().trim().equals("TRAVEL")){
									
								mainTravelType="Travel";
								modeOfTravel=cellList.get(13);
								if(modeOfTravel!=null && !modeOfTravel.equals("")){
									
										if(modeOfTravel.toUpperCase().trim().equals("OWNED VEHICAL")){
											
											modeOfTravel="Owned vehical";
											
										}else if(modeOfTravel.toUpperCase().trim().equals("BUS")){
											
											modeOfTravel="Bus";
											
										}else if(modeOfTravel.toUpperCase().trim().equals("TRAIN")){
											
											modeOfTravel="Train";
											
										}else if(modeOfTravel.toUpperCase().trim().equals("TAXI")){
											
											modeOfTravel="Taxi";
											
										}else if(modeOfTravel.toUpperCase().trim().equals("AUTO")){
											
											modeOfTravel="Auto";
											
										}else{
											
											modeOfTravel="Other";
										}
									
								}else{
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
											"Mode of Travel does not exists,check the Mode of Travel"+ " on Row no-"+(i+1)+".</li>");
									flag = false;
									break;
								}
									
								plcFrom=cellList.get(14);
								plcTo=cellList.get(15);
								noOfDays=cellList.get(16);
								totalKM=cellList.get(17);
								rateKM=cellList.get(18);
								
								// check condition for place from
								if (plcFrom == null || plcFrom.equals("")){
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
											"Please check Place From "+ " on Row no-"+(i+1)+".</li>");
									flag = false;
									break;
									
								}
								
								// check condition for place To
								if (plcTo == null || plcTo.equals("")){
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
											"Please check Place To "+ " on Row no-"+(i+1)+".</li>");
									flag = false;
									break;
									
								}
																
//								System.out.println("no of days---"+cellList.get(16));
									
								}else if(travelType.toUpperCase().trim().equals("REFRESHMENT")){
									

									mainTravelType="Refreshment";
									
								}else if(travelType.toUpperCase().trim().equals("MOBILE BILL")){
									
									mainTravelType="Mobile Bill";
									
								}else if(travelType.toUpperCase().trim().equals("CONVEYANCE BILL")){
									
									mainTravelType="Conveyance Bill";
									
									modeOfTravel=cellList.get(13);
									if(modeOfTravel!=null && !modeOfTravel.equals("")){
										
											if(modeOfTravel.toUpperCase().trim().equals("OWNED VEHICAL")){
												
												modeOfTravel="Owned vehical";
												
											}else if(modeOfTravel.toUpperCase().trim().equals("BUS")){
												
												modeOfTravel="Bus";
												
											}else if(modeOfTravel.toUpperCase().trim().equals("TRAIN")){
												
												modeOfTravel="Train";
												
											}else if(modeOfTravel.toUpperCase().trim().equals("TAXI")){
												
												modeOfTravel="Taxi";
												
											}else if(modeOfTravel.toUpperCase().trim().equals("AUTO")){
												
												modeOfTravel="Auto";
												
											}else{
												
												modeOfTravel="Other";
											}
										
									}else{
											alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
													"Mode of Travel does not exists,check the Mode of Travel"+ " on Row no-"+(i+1)+".</li>");
											flag = false;
											break;
									}
									
									plcFrom=cellList.get(14);
									plcTo=cellList.get(15);
									noOfDays=cellList.get(16);
									totalKM=cellList.get(17);
									rateKM=cellList.get(18);
									
									// check condition for place from
									if (plcFrom == null || plcFrom.equals("")){
										alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
												"Please check Place From "+ " on Row no-"+(i+1)+".</li>");
										flag = false;
										break;
										
									}
									
									// check condition for place To
									if (plcTo == null || plcTo.equals("")){
										alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
												"Please check Place To "+ " on Row no-"+(i+1)+".</li>");
										flag = false;
										break;
										
									}
									
									
								}else if(travelType.toUpperCase().trim().equals("FOOD EXPENSES")){
									
										mainTravelType="Food Expenses";
									
								}else{
										alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
												"Travel Type does not exists,check the Travel Type"+ " on Row no-"+(i+1)+".</li>");
										flag = false;
										break;
								}
														
							}else{
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
											"Travel Type does not exists,check the Travel Type"+ " on Row no-"+(i+1)+".</li>");
									flag = false;
									break;
							}
										
					}else if(reimbursementPlan != null && !reimbursementPlan.equals("") && reimbursementPlan.toUpperCase().trim().equals("P")){
						
						// Client Name
						String clientName = cellList.get(5);
						
						if (clientName != null && !clientName.equals("")) {
							pst = con.prepareStatement("select client_id from client_details where upper(LTRIM(RTRIM(client_name)))=?");
							pst.setString(1, clientName.toUpperCase().trim());
							rs = pst.executeQuery();
							while (rs.next()) {
								clientID = rs.getString("client_id");
							}
							rs.close();
							pst.close();
						}else{
							
							alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
									"Please check the Client name"+ " on Row no-"+(i+1)+".</li>");
							flag = false;
							break;
						}
						if(uF.parseToInt(clientID)==0){
							
							alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
									" Client name does not exists,check the Client name"+ " on Row no-"+(i+1)+".</li>");
							flag = false;
							break;
						}
						
						// Project Name
						
						String proName=cellList.get(6);
						

						if (proName != null && !proName.equals("")) {
							pst = con.prepareStatement("select pro_id from projectmntnc where client_id =? and upper(LTRIM(RTRIM(pro_name)))=?");
							pst.setInt(1, uF.parseToInt(clientID));
							pst.setString(2, proName.toUpperCase().trim());
							rs = pst.executeQuery();
							while (rs.next()) {
								proId = rs.getString("pro_id");
							}
							rs.close();
							pst.close();
						}else{
							
							alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
									"Please check the Project name"+ " on Row no-"+(i+1)+".</li>");
							flag = false;
							break;
						}
						if(uF.parseToInt(proId)==0){
							
							alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
									" Project name does not exists,check the Project name"+ " on Row no-"+(i+1)+".</li>");
							flag = false;
							break;
						}
						
						
						// Travel Type
						String travelType=cellList.get(4);
						if(travelType!=null &&  !travelType.equals("")){
							if(travelType.toUpperCase().trim().equals("TRAVEL")){
								
								mainTravelType="Travel";
								
								modeOfTravel=cellList.get(13);
								if(modeOfTravel!=null && !modeOfTravel.equals("")){
									
									if(modeOfTravel.toUpperCase().trim().equals("OWNED VEHICAL")){
										
										modeOfTravel="Owned vehical";
										
									}else if(modeOfTravel.toUpperCase().trim().equals("BUS")){
										
										modeOfTravel="Bus";
										
									}else if(modeOfTravel.toUpperCase().trim().equals("TRAIN")){
										
										modeOfTravel="Train";
										
									}else if(modeOfTravel.toUpperCase().trim().equals("TAXI")){
										
										modeOfTravel="Taxi";
										
									}else if(modeOfTravel.toUpperCase().trim().equals("AUTO")){
										
										modeOfTravel="Auto";
										
									}else{
										
										modeOfTravel="Other";
									}
									
								}else{
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
											"Travel Mode does not exists,check the Travel Mode"+ " on Row no-"+(i+1)+".</li>");
									flag = false;
									break;
								}
								
								plcFrom=cellList.get(14);
								plcTo=cellList.get(15);
								noOfDays=cellList.get(16);
								totalKM=cellList.get(17);
								rateKM=cellList.get(18);
								
								// check condition for place from
								if (plcFrom == null || plcFrom.equals("")){
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
											"Please check Place From "+ " on Row no-"+(i+1)+".</li>");
									flag = false;
									break;
									
								}
								
								// check condition for place To
								if (plcTo == null || plcTo.equals("")){
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
											"Please check Place To "+ " on Row no-"+(i+1)+".</li>");
									flag = false;
									break;
									
								}
								
																
							}else if(travelType.toUpperCase().trim().equals("REFRESHMENT")){
								
								mainTravelType="Refreshment";
								
							}else if(travelType.toUpperCase().trim().equals("MOBILE BILL")){
								
								mainTravelType="Mobile Bill";
								
							}else if(travelType.toUpperCase().trim().equals("CONVEYANCE BILL")){
								
								mainTravelType="Conveyance Bill";
								
								modeOfTravel=cellList.get(13);
								if(modeOfTravel!=null && !modeOfTravel.equals("")){
									
									if(modeOfTravel.toUpperCase().trim().equals("OWNED VEHICAL")){
										
										modeOfTravel="Owned vehical";
										
									}else if(modeOfTravel.toUpperCase().trim().equals("BUS")){
										
										modeOfTravel="Bus";
										
									}else if(modeOfTravel.toUpperCase().trim().equals("TRAIN")){
										
										modeOfTravel="Train";
										
									}else if(modeOfTravel.toUpperCase().trim().equals("TAXI")){
										
										modeOfTravel="Taxi";
										
									}else if(modeOfTravel.toUpperCase().trim().equals("AUTO")){
										
										modeOfTravel="Auto";
										
									}else{
										
										modeOfTravel="Other";
									}
									
								}else{
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
											"Travel Mode does not exists,check the Travel Mode"+ " on Row no-"+(i+1)+".</li>");
									flag = false;
									break;
									
								}
								
								plcFrom=cellList.get(14);
								plcTo=cellList.get(15);
								noOfDays=cellList.get(16);
								totalKM=cellList.get(17);
								rateKM=cellList.get(18);
								
								
								// check condition for place from
								if (plcFrom == null || plcFrom.equals("")){
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
											"Please check Place From "+ " on Row no-"+(i+1)+".</li>");
									flag = false;
									break;
									
								}
								
								// check condition for place To
								if (plcTo == null || plcTo.equals("")){
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
											"Please check Place To "+ " on Row no-"+(i+1)+".</li>");
									flag = false;
									break;
									
								}
								
								
							}else if(travelType.toUpperCase().trim().equals("FOOD EXPENSES")){
								
								mainTravelType="Food Expenses";
								
							}else{
								alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
										"Travel Type does not exists,check the Travel Type"+ " on Row no-"+(i+1)+".</li>");
								flag = false;
								break;
							}
													
						}else{
							alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
									"Travel Type does not exists,check the Travel Type"+ " on Row no-"+(i+1)+".</li>");
							flag = false;
							break;
						}
						
						// Chargeble Client
						
						String isChargeble=cellList.get(7);
						
						if(isChargeble!=null &&  !isChargeble.equals("")){
							if(isChargeble.toUpperCase().trim().equals("YES")){
								
								isBillable=true;
								
							}else {
								
								isBillable=false;
							}
												
						}else{
							alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
									"Please check Client Chargeable  "+ " on Row no-"+(i+1)+".</li>");
							flag = false;
							break;
						}
											
					}else{
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"Please check Reimbursement Plan  "+ " on Row no-"+(i+1)+".</li>");
						flag = false;
						break;
					}

			
					// insert Query
					
					con = db.makeConnection(con);
					pst = con.prepareStatement("insert into emp_reimbursement (from_date, to_date, reimbursement_type, reimbursement_purpose, " +
						"reimbursement_amount, emp_id, entry_date, reimbursement_type1,travel_mode,no_person,travel_from,travel_to," +
						"no_days,travel_distance,travel_rate,reimbursement_info,is_billable,client_id,pro_id,vendor,receipt_no) " +
						"values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?);");
					
					pst.setDate(1, uF.getDateFormat(strStartDate, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(strEndDate, DATE_FORMAT));
					
					if (reimbursementPlan != null && reimbursementPlan.equalsIgnoreCase("P")) {
						pst.setString(3, proId);
					} else if (reimbursementPlan != null && reimbursementPlan.equalsIgnoreCase("T")) {
						pst.setString(3, mainTravelPlan);
					} else {
						pst.setString(3, mainTravelType);
					}
					// if(getStrType().equals("Travel")){
					
					pst.setString(4, purpose);
					pst.setDouble(5, uF.parseToDouble(amount));
					pst.setInt(6, uF.parseToInt(emp_per_id));
					
					pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
									
					pst.setString(8, reimbursementPlan.toUpperCase().trim());
					
					pst.setString(9, modeOfTravel);
					pst.setInt(10, (int)uF.parseToInt(numberOfEmployees));
					pst.setString(11, plcFrom);
					pst.setString(12, plcTo);
					pst.setInt(13, (int)uF.parseToDouble(noOfDays));
					pst.setDouble(14, uF.parseToDouble(totalKM));
					pst.setDouble(15, uF.parseToDouble(rateKM));
					pst.setString(16, mainTravelType);
					pst.setBoolean(17, isBillable);
					pst.setInt(18, (int)uF.parseToInt(clientID));
					if (reimbursementPlan != null && reimbursementPlan.equalsIgnoreCase("P")) {
						pst.setInt(19, uF.parseToInt(proId));
					} else {
						pst.setInt(19, 0);
					}
					pst.setString(20, vendor);
					pst.setString(21, receiptNo);
					pst.execute();
					
					pst.close();
					
					flag=true;

			   }// end of for loop
			
			if(flag){
				con.commit();
				System.out.println("Reimbursements Inserted...");
				session.setAttribute(MESSAGE, SUCCESSM+"Reimbursements Imported Successfully!"+END);
				sbMessage.append("</ul>");
				request.setAttribute("sbMessage", sbMessage.toString());
			} else {
				con.rollback();
				if(alErrorList.size()>0){
					sbMessage.append(alErrorList.get(alErrorList.size()-1));
				}

				session.setAttribute(MESSAGE, ERRORM+"Reimbursements not imported. Please check imported file."+END);
				sbMessage.append("</ul>");
				request.setAttribute("sbMessage", sbMessage.toString());
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
			request.setAttribute("sbMessage", sbMessage.toString());
			session.setAttribute(MESSAGE, ERRORM+"Reimbursements not imported. Please check imported file."+END);
			e.printStackTrace(); 
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		return SUCCESS;
	}
	
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
}
