package com.konnect.jpms.employee;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.IStatementsPostgres;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;


public class ImportEmpReportingStructure extends ActionSupport implements ServletRequestAware,ServletResponseAware,IConstants, IStatements,IStatementsPostgres {

	private static final long serialVersionUID = 1L;

	File fileUpload;
	//String strLocation;
	String[] f_strWLocation1;

	//String strOrg;
	private String f_org;
	String f_strWLocation;
	String fromPage;
	
	String Supervisor_name=null;
	String HOD1_name=null;
	String HR_name=null;
	String superId=null;
	String hod=null;
	String hr=null;
	
	String exceldownload;
	HttpSession session;
	CommonFunctions CF;
	boolean flag = true;
	
	String strUserType=null;
	
	String fileUploadFileName;
	String Leave_type_string;
	
	String leave_id_list=null;
	String[] Leave_type_array;
	
	 HttpServletResponse response;
	 HttpServletRequest request;
	
	StringBuilder sbMessage = new StringBuilder("<ul style=\"margin:0px\">");
	List<String> alErrorList = new ArrayList<String>();
	List<FillOrganisation> organisationList;
	List<FillWLocation> wLocationList;
	
	public String execute() throws Exception {
		
		session = request.getSession();
		UtilityFunctions uF = new UtilityFunctions();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		strUserType = (String)session.getAttribute(USERTYPE);
		if(CF==null) {
			return LOGIN;
		}
		
		/*if(getStrOrg()==null || getStrOrg().trim().equals("")){
			setStrOrg((String)session.getAttribute(ORGID));
		}*/
		
		if(getF_org()==null || getF_org().trim().equals("")){
			setF_org((String)session.getAttribute(ORGID));
		}
		
		if(getF_strWLocation() != null && !getF_strWLocation().equals("")) {
			setF_strWLocation1(getF_strWLocation().split(","));
		} else {
			setF_strWLocation1(null);
		}
		
		
		//System.out.println("strOrg in ImportEmpReportingStructure==>"+f_org);
		//System.out.println("strLocation in ImportEmpReportingStructure==>"+f_strWLocation);
		
		
		if(getExceldownload()!=null && !getExceldownload().equals("")) {
			if(getExceldownload().equalsIgnoreCase("true")) {
				genratedexcel(uF,null,f_org,f_strWLocation);
			}
		}
		
		if(getFileUpload() != null) {
			loadExcel(getFileUpload());
			if(alErrorList.size()>0){
				sbMessage.append(alErrorList.get(alErrorList.size()-1));
			}
			session.setAttribute("sbMessage", sbMessage.toString());
			return SUCCESS;
		}
		
		if(fromPage !=null && fromPage.equals("P")) {
			return VIEW;
		}
		return "success";	
	}
	

//*********Code to load the excel file with data and call the update function*******//	
	public void loadExcel(File file) throws IOException ,NumberFormatException{
		Database db = new Database();
		db.setRequest(request);
		Connection con=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		ResultSet rsEm = null,result=null,result1=null;
		UtilityFunctions uF = new UtilityFunctions();
		String dateFormat = "dd/MM/yyyy";
		String timeFormat = "HH:mm:ss";
		try {
			FileInputStream fis = new FileInputStream(file);
			List<List> dataList = new ArrayList<List>();
			HSSFWorkbook wb = new HSSFWorkbook(fis);
			HSSFSheet sheet=wb.getSheetAt(0);
			HSSFRow row; 
			HSSFCell cell;
			Iterator rows = sheet.rowIterator();
			
			con = db.makeConnection(con);
			con.setAutoCommit(false);
			
			while (rows.hasNext()) {
				
				row=(HSSFRow) rows.next();
				Iterator cells = row.cellIterator();
				List<String> cellList = new ArrayList<String>();
				while (cells.hasNext())  {
					cell = (HSSFCell) cells.next();
					cellList.add(uF.getCellString(cell, wb, dateFormat, timeFormat));
					/*cell=(HSSFCell) cells.next();
					if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
						cellList.add(cell.toString().trim());
						
					} else if(cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
						System.out.println("cell.getNumericCellValue() ===>> " + cell.getNumericCellValue());
						cellList.add(String.valueOf(cell.getNumericCellValue()));
						
					} else if(cell.getCellType()== HSSFCell.CELL_TYPE_BOOLEAN) {
						cellList.add(String.valueOf(cell.getBooleanCellValue()));
					}*/
				}
				dataList.add(cellList);
			}
			
			//System.out.println("dataList--->"+dataList.size());
//			System.out.println("dataList--->"+dataList);

		if (dataList.size() == 0) {
			flag = false;
			}else {
				
			int ii=0;
			List<String> empCodeList = new ArrayList<String>();
			Map<String,String> hmemp_per_id = new HashMap<String,String>();
			Map<String,String> hmEmpUserType = new HashMap<String,String>();
			Map<String,String> hmEmp_code_Id=new HashMap<String,String>();
			
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("Select upper(empcode) as empcode,emp_per_id, emp_fname,emp_mname,emp_lname,usertype_id from employee_personal_details ep, employee_official_details ef, " +
				" user_details ud where ef.emp_id=ep.emp_per_id and ep.emp_per_id = ud.emp_id and ep.is_alive=true ");
			/*if (getF_strWLocation1() != null && getF_strWLocation1().length > 0) {
				sbQuery.append(" and wlocation_id in (" + StringUtils.join(getF_strWLocation1(), ",") + ") ");
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and wlocation_id in (" + session.getAttribute(WLOCATION_ACCESS) + ")");
			}
			if (uF.parseToInt(getF_org()) > 0) {
					sbQuery.append(" and org_id = " + uF.parseToInt(getF_org()));
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and org_id in (" + session.getAttribute(ORG_ACCESS) + ")");
			}*/
			pst=con.prepareStatement(sbQuery.toString());
//			System.out.println(" pst 1 for loading all detail==>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				empCodeList.add(rs.getString("empcode").toUpperCase().trim());
				hmemp_per_id.put(rs.getString("empcode").toUpperCase().trim(), rs.getString("emp_per_id").toUpperCase().trim());
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				hmEmp_code_Id.put(rs.getString("emp_fname").toUpperCase().trim() +strEmpMName.toUpperCase().trim()+" "+rs.getString("emp_lname").toUpperCase().trim(),rs.getString("empcode").toUpperCase().trim());				
				hmEmpUserType.put(rs.getString("emp_per_id"), rs.getString("usertype_id"));
			}
			rs.close();
			pst.close();
			
			for (int i = 2; i < dataList.size(); i++) {
				
				List<String> cellList = dataList.get(i);
				String employeeCode = cellList.get(1).toUpperCase().trim();
//					if(!employeeCode.equals("-") && uF.parseToLong(employeeCode)>0) {
//						employeeCode = getStringValue(cellList.get(1).toUpperCase().trim());
//					} else {
//						employeeCode = cellList.get(1).toUpperCase().trim();
//					}
					//System.out.println("empcode=="+employeeCode);
					
				String employeename = cellList.get(2).toUpperCase().trim();
					//System.out.println("employeename==>"+employeename);
				String Supervisor_code = cellList.get(3).toUpperCase().trim();
//					if(!Supervisor_code.equals("-") && uF.parseToLong(Supervisor_code)>0) {
//						Supervisor_code = getStringValue(Supervisor_code).toUpperCase().trim();
//					} else {
//						Supervisor_code = Supervisor_code.toUpperCase().trim();
//					}
					//System.out.println("Supervisor_code=="+Supervisor_code);
					
				Supervisor_name=cellList.get(4).trim().toUpperCase().trim();
					//System.out.println("Supervisor_name==>"+Supervisor_name);
				String HOD1_code= cellList.get(5).toUpperCase().trim();
//					if(!HOD1_code.equals("-") && uF.parseToLong(HOD1_code)>0) {
//						HOD1_code = getStringValue(HOD1_code).toUpperCase().trim();
//					} else {
//						HOD1_code = HOD1_code.toUpperCase().trim();
//					}
					
					//System.out.println("HOD1_code=="+HOD1_code);
					
				HOD1_name=cellList.get(6).trim().toUpperCase().trim();
					//System.out.println("HOD1_name==>"+HOD1_name);
				String HR_code= cellList.get(7).toUpperCase().trim();
//					if(!HR_code.equals("-") && uF.parseToLong(HR_code)>0) {
//						HR_code = getStringValue(HR_code).toUpperCase().trim();
//					} else {
//						HR_code = HR_code.toUpperCase().trim();
//					}
					//System.out.println("HR_code=="+HR_code);
					
					HR_name = cellList.get(8).toUpperCase().trim();
					//System.out.println("HR_name==>"+HR_name);

					Leave_type_string = cellList.get(9).toUpperCase().trim();
					ii++;
					int employee_id=0;
					
					if(employeeCode!=null) {
						sbQuery = new StringBuilder();
						sbQuery.append("Select * from employee_personal_details ep,employee_official_details ef where upper(ep.empcode) = ? and ef.emp_id=ep.emp_per_id and ep.is_alive=true");
						if (getF_strWLocation1() != null && getF_strWLocation1().length > 0) {
							sbQuery.append(" and wlocation_id in (" + StringUtils.join(getF_strWLocation1(), ",") + ") ");
						} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
							sbQuery.append(" and wlocation_id in (" + session.getAttribute(WLOCATION_ACCESS) + ")");
						}
						if (uF.parseToInt(getF_org()) > 0) {
								sbQuery.append(" and ef.org_id = " + uF.parseToInt(getF_org()));
						} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
							sbQuery.append(" and ef.org_id in (" + session.getAttribute(ORG_ACCESS) + ")");
						}
						
						pst=con.prepareStatement(sbQuery.toString());
						pst.setString(1, employeeCode.toUpperCase().trim());
						//System.out.println("pst for employee code=="+pst);
						rsEm = pst.executeQuery();
						
						if(rsEm.next()) {
				 			employee_id = rsEm.getInt("emp_per_id");	
				 		} else {
				 			alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +employeeCode.toUpperCase().trim()+" Not Exist Please Check Employee Code</li>");
				 			flag = false;
				 			break;
				 		}
						rsEm.close();
						pst.close();
					}
					
					
					if(employee_id == 0) {
						continue;
					}
					
//					System.out.println("IERS/321--hmEmp_code_Id=="+hmEmp_code_Id);
//					System.out.println("IERS/321--Supervisor_code="+Supervisor_code+"====>!Supervisor_code.equals(-)=="+!Supervisor_code.equals("-"));
//					System.out.println("IERS/322--empCodeList=="+!empCodeList.contains(Supervisor_code));
//					System.out.println("IERS/323--Supervisor_name=="+Supervisor_name+"---!Supervisor_name.equals(-)=="+!Supervisor_name.equals("-"));
//					System.out.println("IERS/324--hmEmp_code_Id.get(Supervisor_name)=="+!empCodeList.contains(hmEmp_code_Id.get(Supervisor_name))+"---->hmEmp_code_Id.get(Supervisor_name)=="+hmEmp_code_Id.get(Supervisor_name));
//					System.out.println("IERS/326----->"+hmEmp_code_Id.get("ABHIJIT BHALCHANDRA KAPATKAR "));
					if((Supervisor_code != null && !Supervisor_code.equals("-") && Supervisor_code.length()>0 && !empCodeList.contains(Supervisor_code)) || (Supervisor_name != null && !Supervisor_name.equals("-") && Supervisor_name.length()>0 && !empCodeList.contains(hmEmp_code_Id.get(Supervisor_name)))) {
	 					
	 					alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">"+""+ Supervisor_code+" "+ Supervisor_name + " Not exists, please Enter correct Supervisor Code or Name for the Employee "+employeeCode+"</li>");
	 					flag = false;
	 					break;
					}
					
					if((HOD1_code != null && !HOD1_code.equals("-") && HOD1_code.length()>0 && !empCodeList.contains(HOD1_code)) || (HOD1_name != null && !HOD1_name.equals("-") && HOD1_name.length()>0 && !empCodeList.contains(hmEmp_code_Id.get(HOD1_name)))) {
		 				alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">"+""+ HOD1_code+" "+ HOD1_name + " Not exists, please Enter correct HOD Code or Name for the Employee "+employeeCode+"</li>");
		 				flag = false;
		 				break;
					}
					
					if((HR_code != null && !HR_code.equals("-") && HR_code.length()>0 && !empCodeList.contains(HR_code)) || (HR_name != null && !HR_name.equals("-") && HR_name.length()>0 && !empCodeList.contains(hmEmp_code_Id.get(HR_name)))) {
		 				alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">"+""+ HR_code+" "+ HR_name + " Not exists, please Enter correct HR Code or Name for the Employee "+employeeCode+"</li>");
		 				flag = false;
		 				break;
					}
				
					Map<String,String> leaveType = new HashMap<String,String>();
					
					sbQuery = new StringBuilder();
					sbQuery.append("select leave_type_id,leave_type_code from leave_type lt,employee_official_details eod where lt.org_id=eod.org_id");
					
					if (getF_strWLocation1() != null && getF_strWLocation1().length > 0) {
						sbQuery.append(" and wlocation_id in (" + StringUtils.join(getF_strWLocation1(), ",") + ") ");
					} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
						sbQuery.append(" and wlocation_id in (" + session.getAttribute(WLOCATION_ACCESS) + ")");
					}
					if (uF.parseToInt(getF_org()) > 0) {
							sbQuery.append(" and lt.org_id = " + uF.parseToInt(getF_org()));
					} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
						sbQuery.append(" and lt.org_id in (" + session.getAttribute(ORG_ACCESS) + ")");
					}
					pst=con.prepareStatement(sbQuery.toString());
//					System.out.println("pst leave=="+pst);
					result1 = pst.executeQuery();
					while(result1.next()) {
						if(result1.getString("leave_type_code") != null) {
							leaveType.put(result1.getString("leave_type_code").toUpperCase(),result1.getString("leave_type_id"));
						}
					} 
					result1.close();
					pst.close();
// 					System.out.println("leaveType ===>> " + leaveType);
					
					StringBuilder sbLeaveType=null;
					if(getLeave_type_string() !=null && !getLeave_type_string().equals("") && !getLeave_type_string().equals("-")) {
						setLeave_type_array(getLeave_type_string().split(","));

						for(int l=0;getLeave_type_array()!=null && l<getLeave_type_array().length;l++) {
							if(leaveType.get(getLeave_type_array()[l].toUpperCase())==null) {
								alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +"Please Enter Correct Leave code ," +Leave_type_array[l]+" Leave code not exist </li>");
								flag = false;
								break;
							} else {
								if(sbLeaveType==null) {
									sbLeaveType = new StringBuilder();
									sbLeaveType.append(leaveType.get(getLeave_type_array()[l]));
								} else {
									sbLeaveType.append(","+leaveType.get(getLeave_type_array()[l]));
								}
							}
						}
					}
					if(flag == false) {
						break;
					}
					if(sbLeaveType==null) {
						sbLeaveType = new StringBuilder();
					}
					
					if(Supervisor_code!=null && !Supervisor_code.equals("") && !Supervisor_code.equals("-")){
						superId = hmemp_per_id.get(Supervisor_code);
						
						//System.out.println("superId==>"+superId);
					} else {
						superId = hmemp_per_id.get(hmEmp_code_Id.get(Supervisor_name));
						//System.out.println("superId for name==>"+superId);
					}
					
					if(HOD1_code!=null && !HOD1_code.equals("") && !HOD1_code.equals("-")){
						hod = hmemp_per_id.get(HOD1_code);
					} else {
						hod = hmemp_per_id.get(hmEmp_code_Id.get(HOD1_name));
					}
					if(HR_code!=null && !HR_code.equals("") && !HR_code.equals("-")){
						hr = hmemp_per_id.get(HR_code);
					} else {
						hr = hmemp_per_id.get(hmEmp_code_Id.get(HR_name));
					}

					Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
					
				if((uF.parseToInt(superId)>0) && hmEmpUserType.get(superId).equals(hmUserTypeId.get(EMPLOYEE))) {
					alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +"Employee official details not imported, please check the UserType Code for Supervisor- "+Supervisor_code+"</li>");
					flag = false;
					break;
				}
				if((uF.parseToInt(hod)>0) && !(hmEmpUserType.get(hod).equals(hmUserTypeId.get(ADMIN))) && !(hmEmpUserType.get(hod).equals(hmUserTypeId.get(HRMANAGER))) && !(hmEmpUserType.get(hod).equals(hmUserTypeId.get(RECRUITER))) && !hmEmpUserType.get(hod).equals(hmUserTypeId.get(HOD))) {
					alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +"Employee official details not imported, please check the UserType Code for HOD- "+HOD1_code+"</li>");
					flag = false;
					break;
				}
				if( (uF.parseToInt(hr)>0) && !(hmEmpUserType.get(hr).equals(hmUserTypeId.get(ADMIN))) && !(hmEmpUserType.get(hr).equals(hmUserTypeId.get(HRMANAGER)))) {
					alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +"Employee official details not imported, please check the UserType Code for HR- "+HR_code+"</li>");
					flag = false;
					break;
				}
				
			 if(employee_id>0) {
				 	
				 if(uF.parseToInt(superId)>0 || uF.parseToInt(hod)>0 || uF.parseToInt(hr)>0) {
						sbQuery = new StringBuilder();
						
						sbQuery.append("update employee_official_details set emp_id=? ");
						if(uF.parseToInt(superId)>0) {
							sbQuery.append(", supervisor_emp_id="+uF.parseToInt(superId));
						}
						if(uF.parseToInt(hod)>0) {
							sbQuery.append(", hod_emp_id="+uF.parseToInt(hod));
						}
						if(uF.parseToInt(hr)>0) {
							sbQuery.append(", emp_hr="+uF.parseToInt(hr));
						}
						sbQuery.append(" where emp_id=?" );
						if (getF_strWLocation1() != null && getF_strWLocation1().length > 0) {
							sbQuery.append(" and wlocation_id in (" + StringUtils.join(getF_strWLocation1(), ",") + ") ");
						} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
							sbQuery.append(" and wlocation_id in (" + session.getAttribute(WLOCATION_ACCESS) + ")");
						}
						if (uF.parseToInt(getF_org()) > 0) {
								sbQuery.append(" and org_id = " + uF.parseToInt(getF_org()));
						} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
							sbQuery.append(" and org_id in (" + session.getAttribute(ORG_ACCESS) + ")");
						}
						pst = con.prepareStatement(sbQuery.toString());
						
						pst.setInt(1, employee_id);
						pst.setInt(2, employee_id);
						//System.out.println("pst update" +pst);
						pst.executeUpdate();
						pst.close();
					}
					
					if(sbLeaveType.toString()!=null && !sbLeaveType.toString().equals("")) {
						pst=con.prepareStatement("update probation_policy set leaves_types_allowed=? where emp_id=?");
						pst.setString(1, sbLeaveType.toString());
						pst.setInt(2, employee_id);
						//System.out.println("pst for update leave"+pst);
						int x=pst.executeUpdate();
						pst.close();
						if(x==0) {
							pst=con.prepareStatement("INSERT INTO probation_policy(emp_id, leaves_types_allowed) VALUES(?,?)");
							pst.setInt(1,employee_id );
							pst.setString(2,sbLeaveType.toString());
							pst.execute();
							pst.close();
						}
					} 	
			   }
			 
			}//end of for loop
			
			if(ii == 0 ){
				con.rollback();
				
				sbMessage.append("<li class=\"msg_error\" style=\"margin:0px\">Employee Reporting Structure not imported. Please check imported file.</li>");
				session.setAttribute(MESSAGE, ERRORM+"Employee Reporting Structure not imported. Please check imported file."+END);
				sbMessage.append("</ul>");
				//System.out.println("sbMessage in rollback if ===>> " + sbMessage.toString());
				request.setAttribute("sbMessage", sbMessage.toString());
			} else {
				if(flag) {
					con.commit();
					session.setAttribute(MESSAGE, SUCCESSM+"Employee Reporting Structure Imported Successfully!"+END);
					sbMessage.append("<li class=\"msg savesuccess\" style=\"margin:0px\">Employee Reporting Structure Imported Successfully!</li>");
					sbMessage.append("</ul>");
					System.out.println("sbMessage in commit else if ===>> " + sbMessage.toString());
					request.setAttribute("sbMessage", sbMessage.toString());
				} else {
					con.rollback();
					/*session.setAttribute(MESSAGE, ERRORM+"Employee Reporting Structure not imported. Please check imported file."+END);
					sbMessage.append("</ul>");*/
					
					
					/*alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +"Employee Reporting Structure not imported. Please check imported file</li>");
					sbMessage.append("</ul>");
					session.setAttribute("sbMessage", sbMessage.toString());*/
					
					System.out.println("sbMessage in rollback else else ===>> " + sbMessage.toString());
				}
			}
			
		}//end of else
					
		}catch (Exception e) {
			
			e.printStackTrace();
			flag =false;
			try  {
				con.rollback();
				alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +"Employee Reporting Structure not imported please check imported file</li>");
				sbMessage.append("</ul>");
				session.setAttribute("sbMessage", sbMessage.toString());
				
			} catch (SQLException e1) {
				e1.printStackTrace();
				alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +"Employee Reporting Structure Not Imported</li>");
				session.setAttribute(MESSAGE,ERRORM+ "Employee Reporting Structure not imported. Please check imported file."+ END);
			}
			session.setAttribute(MESSAGE,ERRORM+ "Employee Reporting Structure not imported. Please check imported file."+ END);
		}  finally {
			db.closeResultSet(rsEm);
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
			
	}
	

//***************Code to 1.create the excel file 2.download the excel file*******//	
	public void genratedexcel(UtilityFunctions uF,String empId, String f_org, String strLocation) {
		//System.out.println("in genratedexcel--");
		
		Connection con = null;
		PreparedStatement pst=null;
		PreparedStatement pst1 = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			Map<String, List<String>> hmEmpData = new LinkedHashMap<String, List<String>>();
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("Select upper(empcode) as empcode,emp_fname,emp_mname,emp_lname,ep.emp_per_id from employee_personal_details ep,employee_official_details ef where ef.emp_id=ep.emp_per_id and is_alive=true ");
				if (getF_strWLocation1() != null && getF_strWLocation1().length > 0) {
					sbQuery.append(" and wlocation_id in (" + StringUtils.join(getF_strWLocation1(), ",") + ") ");
				} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
					sbQuery.append(" and wlocation_id in (" + session.getAttribute(WLOCATION_ACCESS) + ")");
				}
				if (uF.parseToInt(getF_org()) > 0) {
						sbQuery.append(" and org_id = " + uF.parseToInt(getF_org()));
				} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
					sbQuery.append(" and org_id in (" + session.getAttribute(ORG_ACCESS) + ")");
				}
				sbQuery.append(" order by emp_fname, emp_lname");
				pst=con.prepareStatement(sbQuery.toString());
				
				//System.out.println("query for download report==>"+pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					List<String> empCodeList = new ArrayList<String>();
					empCodeList.add(rs.getString("empcode"));
					
					String strEmpMName = "";
					if(flagMiddleName) {
						if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
							strEmpMName = " "+rs.getString("emp_mname");
						}
					}
					
					empCodeList.add(rs.getString("emp_fname") +strEmpMName+" "+rs.getString("emp_lname"));
					hmEmpData.put(rs.getString("emp_per_id"), empCodeList);
				}
				
			rs.close();
			pst.close();
			con.close();
			
			try {
				
				 HSSFWorkbook workbook = new HSSFWorkbook();
				 HSSFSheet sheet=workbook.createSheet("Employee Reportng Structure");
			 	 HSSFCellStyle headerStyle1= workbook.createCellStyle();
			 	 Font headerFont1 = workbook.createFont();
			 	 headerFont1.setColor(IndexedColors.RED.getIndex());
			 	 headerFont1.setFontHeightInPoints((short)8);
			 	 headerStyle1.setAlignment(HSSFCellStyle.ALIGN_LEFT);
			 	 headerStyle1.setBorderTop(XSSFCellStyle.BORDER_THIN);
			 	 headerStyle1.setBorderRight(XSSFCellStyle.BORDER_THIN);
			 	 headerStyle1.setBorderLeft(XSSFCellStyle.BORDER_THIN);
			 	 headerStyle1.setBorderBottom(XSSFCellStyle.BORDER_THIN);
			 	 headerStyle1.setFont(headerFont1);
			 	 
			 	 HSSFCellStyle headerStyleGeen= workbook.createCellStyle();
			 	 Font headerFontG = workbook.createFont();
			 	 headerFontG.setColor(IndexedColors.GREEN.getIndex());
			 	 headerFontG.setFontHeightInPoints((short)9);
			 	 headerFontG.setBoldweight(Font.BOLDWEIGHT_BOLD);
			 	 headerStyleGeen.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			 	 headerStyleGeen.setBorderTop(XSSFCellStyle.BORDER_THIN);
			 	 headerStyleGeen.setBorderRight(XSSFCellStyle.BORDER_THIN);
			 	 headerStyleGeen.setBorderLeft(XSSFCellStyle.BORDER_THIN);
			 	 headerStyleGeen.setBorderBottom(XSSFCellStyle.BORDER_THIN);
			 	 headerStyleGeen.setFont(headerFontG);
			 	 
			 	HSSFCellStyle subheaderStyle1= workbook.createCellStyle();
			 	 Font subheaderFont= workbook.createFont();
			 	 subheaderFont.setColor(IndexedColors.BLACK.getIndex());
			 	 subheaderFont.setFontHeightInPoints((short)9);
			 	 subheaderFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
			 	 subheaderStyle1.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			 	 subheaderStyle1.setBorderTop(XSSFCellStyle.BORDER_THIN);
			 	 subheaderStyle1.setBorderRight(XSSFCellStyle.BORDER_THIN);
			 	 subheaderStyle1.setBorderLeft(XSSFCellStyle.BORDER_THIN);
			 	 subheaderStyle1.setBorderBottom(XSSFCellStyle.BORDER_THIN);
			 	 subheaderStyle1.setFont(subheaderFont);
			 	 
			 	 HSSFCellStyle borderStyle1= workbook.createCellStyle();
			 	 Font borderFont1 = workbook.createFont();
			 	 borderFont1.setColor(IndexedColors.BLACK.getIndex());
			 	 borderFont1.setFontHeightInPoints((short)9);
			 	 borderStyle1.setAlignment(HSSFCellStyle.ALIGN_LEFT);
			 	 borderStyle1.setBorderTop(XSSFCellStyle.BORDER_THIN);
			 	 borderStyle1.setBorderRight(XSSFCellStyle.BORDER_THIN);
			 	 borderStyle1.setBorderLeft(XSSFCellStyle.BORDER_THIN);
			 	 borderStyle1.setBorderBottom(XSSFCellStyle.BORDER_THIN);
			 	 borderStyle1.setFont(borderFont1);
			 	
				 HSSFRow row=null;
				 HSSFCell cell=null;
				
			     row = sheet.createRow(0);
				 cell = row.createCell(0);
			     cell.setCellValue("IMPLOYEE REPORTING STRUCTURE");
			     cell.setCellStyle(headerStyleGeen);
			     sheet.autoSizeColumn(0);
			     row.setHeightInPoints(20);
			    
			     for(int i=1;i<9;i++)
			     {
			    	cell =row.createCell(i);
			    	cell.setCellValue("");
			    	cell.setCellStyle(headerStyleGeen);
			     }	
			     
			     sheet.addMergedRegion(new CellRangeAddress(0,0,0,9));
			
			     cell = row.createCell(11);
			     cell.setCellValue("INSTRUCTION");
			     cell.setCellStyle(headerStyleGeen);
			     sheet.autoSizeColumn(0);
			     row.setHeightInPoints(20);
				
			     row=sheet.createRow(1);
			     
				 cell = row.createCell(0);
			     cell.setCellValue("Sr.No.");
			     cell.setCellStyle(subheaderStyle1);
			     sheet.autoSizeColumn(0);
			     
			     cell =row.createCell(1);
			     cell.setCellValue("Employee Code");
			     cell.setCellStyle(subheaderStyle1);
			     sheet.autoSizeColumn(1);
			     
			     cell =row.createCell(2);
			     cell.setCellValue("Employee Name");
			     cell.setCellStyle(subheaderStyle1);
			     sheet.autoSizeColumn(2);
			     
			     cell =row.createCell(3);
			     cell.setCellValue("Supervisor Code");
			     cell.setCellStyle(subheaderStyle1);
			     sheet.autoSizeColumn(3);
			     
			     cell = row.createCell(4);
			     cell.setCellValue("Supervisor Name");
			     cell.setCellStyle(subheaderStyle1);
			     sheet.autoSizeColumn(4);
			     
			     cell =row.createCell(5);
			     cell.setCellValue("H.O.D Code");
			     cell.setCellStyle(subheaderStyle1);
			     sheet.autoSizeColumn(5);
			     
			     cell =row.createCell(6);
			     cell.setCellValue("H.O.D Name");
			     cell.setCellStyle(subheaderStyle1);
			     sheet.autoSizeColumn(6);
			     
			     cell =row.createCell(7);
			     cell.setCellValue("HR Code");
			     cell.setCellStyle(subheaderStyle1);
			     sheet.autoSizeColumn(7);
			     
			     cell =row.createCell(8);
			     cell.setCellValue("HR Name");
			     cell.setCellStyle(subheaderStyle1);
			     sheet.autoSizeColumn(8);
			     
			     cell =row.createCell(9);
			     cell.setCellValue("Leave Type");
			     cell.setCellStyle(subheaderStyle1);
			     sheet.autoSizeColumn(9);
			     
			     cell =row.createCell(10);
			     cell.setCellValue("");
			     cell.setCellStyle(subheaderStyle1);
			     sheet.autoSizeColumn(10);
			     
			     cell =row.createCell(11);
			     cell.setCellValue("1.Add either name or code in reporting structure"+'\n'+
			    		 "2.Add leave type by adding leave code with separation using comma(,). For eg: (CL,PL,EL)"+'\n'+
			    		 "3.Please mark '-' at blank spaces or where,"+'\n'+
			    		 "  -->You leave blank spaces"+'\n'+"  -->You don't assign Supervisor code or name"+'\n'+
			    		 "  -->You don't assign HOD code or name"+'\n'+"  -->You don't assign HR code or name");
			     cell.setCellStyle(headerStyle1);
			     sheet.autoSizeColumn(11);
			     row.setHeightInPoints(70);
			     
			     int rowCount=1;
			     Iterator<String> it = hmEmpData.keySet().iterator();
					int count=0;
					while (it.hasNext()){
						rowCount++;
						count++;
						String strEmpId = it.next();
						List<String> innerList = hmEmpData.get(strEmpId);
						row=sheet.createRow(rowCount);
						
						 cell=row.createCell(0);
			    		 cell.setCellValue(uF.showData(""+count, ""));
			    		 cell.setCellStyle(borderStyle1);
			    		 
			    		 cell=row.createCell(1);
			    		 cell.setCellValue(uF.showData(innerList.get(0), "-"));
			    		 cell.setCellStyle(borderStyle1);
			    		 
			    		 cell=row.createCell(2);
			    		 cell.setCellValue(uF.showData(innerList.get(1), "-"));
			    		 cell.setCellStyle(borderStyle1);
			    		 
			    		 cell=row.createCell(3);
			    		 cell.setCellValue( "-");
			    		 cell.setCellStyle(borderStyle1);
			    		 
			    		 cell=row.createCell(4);
			    		 cell.setCellValue("-");
			    		 cell.setCellStyle(borderStyle1);
			    		 sheet.autoSizeColumn(4);
			    		 
			    		 cell=row.createCell(5);
			    		 cell.setCellValue("-");
			    		 cell.setCellStyle(borderStyle1);
			    		 sheet.autoSizeColumn(5);
						
			    		 cell=row.createCell(6);
			    		 cell.setCellValue( "-");
			    		 cell.setCellStyle(borderStyle1);
			    		 
			    		 cell=row.createCell(7);
			    		 cell.setCellValue("-");
			    		 cell.setCellStyle(borderStyle1);
			    		 sheet.autoSizeColumn(7);
			    		 
			    		 cell=row.createCell(8);
			    		 cell.setCellValue("-");
			    		 cell.setCellStyle(borderStyle1);
			    		 sheet.autoSizeColumn(8);
			    		 
			    		 cell=row.createCell(9);
			    		 cell.setCellValue("-");
			    		 cell.setCellStyle(borderStyle1);
			    		 sheet.autoSizeColumn(9);
					}
				
			     ByteArrayOutputStream buffer = new ByteArrayOutputStream();
					try {
						workbook.write(buffer);
						buffer.close();
						}catch (IOException e1){
						e1.printStackTrace();
						}

					response.setHeader("Content-Disposition", "attachment; filename=\"EmployeeReportingStructure.xls\"");
					response.setContentType("application/vnd.ms-excel:UTF-8");
					response.setContentLength(buffer.size());
				
					try {
						ServletOutputStream op = response.getOutputStream();
						op = response.getOutputStream();
						op.write(buffer.toByteArray());
						op.flush();
						op.close();
						}catch (IOException e){
						e.printStackTrace();
					   }
				
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					db.closeConnection(con);
				}		
		}
		catch (Exception e){
			e.printStackTrace();
		}
		finally{
			db.closeResultSet(rs1);
			db.closeResultSet(rs);
			db.closeStatements(pst1);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	public String getStringValue(String str)
	{
		try{
			str = String.valueOf(Double.valueOf(str).longValue());
		} 
		catch(Exception ex){
			ex.printStackTrace();
		}
		return str;
	}
	
	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;	
	}
	
	public String getExceldownload() {
		return exceldownload;
	}

	public void setExceldownload(String exceldownload) {
		this.exceldownload = exceldownload;
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
	
	public String[] getLeave_type_array() {
		return Leave_type_array;
	}

	public void setLeave_type_array(String[] leave_type_array) {
		Leave_type_array = leave_type_array;
	}
	public String getLeave_type_string() {
		return Leave_type_string;
	}

	public void setLeave_type_string(String leave_type_string) {
		Leave_type_string = leave_type_string;
	}
	/*public String getStrLocation() {
		return strLocation;
	}
	public void setStrLocation(String strLocation) {
		this.strLocation = strLocation;
	}*/
	/*public String getStrOrg() {
		return strOrg;
	}
	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}*/

	/*public String[] getF_strWLocation() {
		return f_strWLocation;
	}

	public void setF_strWLocation(String[] f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
	}*/
	public String getFromPage() {
		return fromPage;
	}
	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}
	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}
	public String getF_strWLocation() {
		return f_strWLocation;
	}

	public void setF_strWLocation(String f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
	}
	public String[] getF_strWLocation1() {
		return f_strWLocation1;
	}

	public void setF_strWLocation1(String[] f_strWLocation1) {
		this.f_strWLocation1 = f_strWLocation1;
	}
}
