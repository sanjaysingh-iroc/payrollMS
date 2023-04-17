package com.konnect.jpms.payroll;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class ImportDA extends ActionSupport implements ServletRequestAware, IConstants, IStatements {
	private static final long serialVersionUID = 1L;

	File fileUpload;
	String fileUploadFileName;
	Map session;
	
	CommonFunctions CF;
	String strSessionEmpId;

	public String execute() throws Exception {
		session = ActionContext.getContext().getSession();
		strSessionEmpId = (String) session.get(EMPID);
		CF = (CommonFunctions) session.get(CommonFunctions);
		if (CF == null)
			return LOGIN;
		request.setAttribute(PAGE, "/jsp/payroll/ImportDA.jsp");
		request.setAttribute(TITLE, "Import DA");

		if (getFileUpload() != null) {
			loadExcel(getFileUpload());
		}

		return "success";
	}

	public void loadExcel(File file) throws IOException {

		Database db = new Database();
		db.setRequest(request);
		StringBuilder sbMessage = new StringBuilder("<ul style=\"margin:0px\">");

		Connection con=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {

			UtilityFunctions uF = new UtilityFunctions();
			
			
			con = db.makeConnection(con);
			FileInputStream fis = new FileInputStream(file);

			XSSFWorkbook workbook = new XSSFWorkbook(fis);

			

				XSSFSheet employsheet = workbook.getSheetAt(0);
				List dataList = new ArrayList();
				Iterator rows = employsheet.rowIterator();

				while (rows.hasNext()) {
					XSSFRow row = (XSSFRow) rows.next();

					Iterator cells = row.cellIterator();
					List cellList = new ArrayList();

					while (cells.hasNext()) {
						XSSFCell cell = (XSSFCell) cells.next();
						cellList.add(cell);
					}
					dataList.add(cellList);
				}

				
				System.out.println("===>"+dataList.size());
				for (int i = 1; i < dataList.size(); i++) {

					try {

						ArrayList cellList = (ArrayList) dataList.get(i);
						XSSFCell cell1 = (XSSFCell) cellList.get(1);
						XSSFCell cell2 = (XSSFCell) cellList.get(2);
						System.out.println("===>>>>"+cell1.toString());
						List<String> employee_id=new ArrayList<String>();

							pst = con.prepareStatement("select distinct(emp_per_id)as emp_id from employee_personal_details where RTRIM(LTRIM(UPPER(convert(varchar,emp_city_id)))) = ? ");
							pst.setString(1, cell1.toString().toUpperCase().trim());
							 rs = pst.executeQuery();
							while (rs.next()) {
								employee_id.add(rs.getString("emp_id"));
							}
							rs.close();
							pst.close();
							
							for(String emp:employee_id){
								Map<String,String> details=new HashMap<String,String>();
								
								pst=con.prepareStatement("select * from emp_salary_details where emp_id =? and salary_head_id=2 and effective_date=(select max(effective_date) from emp_salary_details  where emp_id =? and salary_head_id=2 )");
								pst.setInt(1, uF.parseToInt(emp));
								pst.setInt(2, uF.parseToInt(emp));
								rs=pst.executeQuery();
								while(rs.next()){
									details.put("PAY_TYPE", rs.getString("pay_type"));
									details.put("IS_DISPLAY", rs.getString("isdisplay"));
									details.put("SERVICE", rs.getString("service_id"));
									details.put("E_D", rs.getString("earning_deduction"));
									
								}
								rs.close();
								pst.close();
								
								pst=con.prepareStatement("update emp_salary_details set is_approved= ? " +
										"  where emp_id =? and salary_head_id=2 and " +
										"effective_date=(select max(effective_date) from emp_salary_details  where emp_id =? and salary_head_id=2 )");
								pst.setBoolean(1,false);
								pst.setInt(2, uF.parseToInt(emp));
								pst.setInt(3, uF.parseToInt(emp));
								pst.execute();
								pst.close();
								
								pst=con.prepareStatement("insert into emp_salary_details(emp_id,salary_head_id,amount,entry_date,user_id," +
										"pay_type,isdisplay,service_id,effective_date,approved_date,approved_by,is_approved,earning_deduction)" +
										"values(?,?,?,?,?,?,?,?,?,?,?,?,?) ");
								
								pst.setInt(1, uF.parseToInt(emp));
								pst.setInt(2, 2);
								pst.setDouble(3, uF.parseToDouble(cell2.toString()));
								pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
								pst.setInt(5,uF.parseToInt(strSessionEmpId));
								pst.setString(6, details.get("PAY_TYPE"));
								pst.setBoolean(7, uF.parseToBoolean(details.get("IS_DISPLAY")));
								pst.setInt(8, uF.parseToInt(details.get("SERVICE")));
								pst.setDate(9, uF.getCurrentDate(CF.getStrTimeZone()));
								pst.setDate(10, uF.getCurrentDate(CF.getStrTimeZone()));
								pst.setInt(11,uF.parseToInt(strSessionEmpId));
								pst.setBoolean(12,true);
								pst.setString(13, details.get("E_D"));
								pst.execute();
								pst.close();
								
								CF.updateNextEmpSalaryEffectiveDate(con, uF, uF.parseToInt(emp), uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), DATE_FORMAT);
								
							}
							
							
							


						}

					 catch (Exception e) {
						e.printStackTrace();
					}
				}// end for loop
			
		}// try block end

		catch (Exception e) {
			
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		sbMessage.append("</ul>");
		request.setAttribute("sbMessage", sbMessage.toString());
	}


	

	public Map getSession() {
		return session;
	}

	public void setSession(Map session) {
		this.session = session;
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
	
	public String getStringValue(String str){
		
		try{
			str = String.valueOf(Double.valueOf(str).longValue());
		}catch(Exception ex){
			
		}
		return str;
	}

}
