package com.konnect.jpms.payroll;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
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

public class ImportAllowance extends ActionSupport implements ServletRequestAware, IConstants, IStatements {
	private static final long serialVersionUID = 1L;

	File fileUpload;
	String fileUploadFileName;
	Map session;
	
	String paycycle;
	CommonFunctions CF;
	String other_allowance;
	String strSessionEmpId;

	public String execute() throws Exception {
		session = ActionContext.getContext().getSession();
		strSessionEmpId = (String) session.get(EMPID);
		CF = (CommonFunctions) session.get(CommonFunctions);
		if (CF == null)
			return LOGIN;


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
		ResultSet rsEm = null;
		try {

			UtilityFunctions uF = new UtilityFunctions();
			
			
			con = db.makeConnection(con);
			FileInputStream fis = new FileInputStream(file);

			XSSFWorkbook workbook = new XSSFWorkbook(fis);

			

				XSSFSheet employsheet = workbook.getSheetAt(0);
				List<List<String>> dataList = new ArrayList<List<String>>();
				Iterator rows = employsheet.rowIterator();

				while (rows.hasNext()) {
					XSSFRow row = (XSSFRow) rows.next();

					Iterator cells = row.cellIterator();
					List<String> cellList = new ArrayList<String>();

					while (cells.hasNext()) {
						XSSFCell cell = (XSSFCell) cells.next();
						cellList.add(cell.toString().trim());
					}
					dataList.add(cellList);
				}

				String []arrPaycycle=null;
				int nPaycycle = 0;
				if(getPaycycle()!=null){
					arrPaycycle = getPaycycle().split("-");
					nPaycycle = uF.parseToInt(arrPaycycle[2]);
				}
//				System.out.println("===>"+dataList.size());
				for (int i = 1; i < dataList.size(); i++) {

					try {

						List<String> cellList = dataList.get(i);
						String cell1 = cellList.get(1);
						String cell2 = cellList.get(2);
						if (cell1.contains(".")) {
							cell1 = cell1.substring(0, cell1.indexOf("."));

						}
//						System.out.println("===>>>>"+cell1.toString());
						int employee_id=0;

							pst = con.prepareStatement("Select * from employee_personal_details where upper(empcode) = ? ");
							pst.setString(1, cell1.toUpperCase().trim());
							rsEm = pst.executeQuery();
							while (rsEm.next()) {
								employee_id = rsEm.getInt("emp_per_id");
							}
							rsEm.close();
							pst.close();
							
							pst = con.prepareStatement("insert into other_individual_allowance_details" +
									" (emp_id, pay_paycycle, amount, pay_amount, added_by," +
									"  entry_date, paid_from, paid_to, is_approved,allowance_code,approved_by,approved_date) values (?,?,?,?,?,?,?,?,?,?,?,?)");
							pst.setInt(1,employee_id);
							pst.setInt(2, nPaycycle);
							pst.setDouble(3, uF.parseToDouble(cell2));
							pst.setDouble(4, uF.parseToDouble(cell2));
							pst.setInt(5, uF.parseToInt(strSessionEmpId));
							pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.setDate(7, uF.getDateFormat(arrPaycycle[0], DATE_FORMAT));
							pst.setDate(8, uF.getDateFormat(arrPaycycle[1], DATE_FORMAT));
							pst.setInt(9, 1);
							pst.setInt(10, uF.parseToInt(other_allowance));
							pst.setInt(11, uF.parseToInt(strSessionEmpId));
							pst.setDate(12, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.execute();
							pst.close();
							


						}

					 catch (Exception e) {
						e.printStackTrace();
					}
				}// end for loop
			
		}// try block end

		catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rsEm);
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		sbMessage.append("</ul>");
		request.setAttribute("sbMessage", sbMessage.toString());
	}


	public String getOther_allowance() {
		return other_allowance;
	}

	public void setOther_allowance(String other_allowance) {
		this.other_allowance = other_allowance;
	}

	public String getPaycycle() {
		return paycycle;
	}

	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
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