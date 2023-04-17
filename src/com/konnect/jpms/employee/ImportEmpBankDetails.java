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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ImportEmpBankDetails extends ActionSupport implements ServletRequestAware, ServletResponseAware, IConstants, IStatements {
	private static final long serialVersionUID = 1L;
	
	File fileUpload;
	String exceldownload;
	String fileUploadFileName;
	HttpSession session;

	CommonFunctions CF;
	String strSessionEmpId;
	String strUserType=null;

	private String fromPage; 
	private String mode;
	
	
	private String[] f_strWLocation; 
	private String f_org;
	
	
	public String execute() throws Exception {
		
		session = request.getSession();
		UtilityFunctions uF = new UtilityFunctions();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		strUserType = (String)session.getAttribute(USERTYPE);
		if(CF==null) {
			return LOGIN;
		}
		if(getExceldownload()!=null && !getExceldownload().equals("")) {
			if(getExceldownload().equalsIgnoreCase("true")) {
				genratedexcel(uF);
			}
		}
		
		if (getFileUpload() != null) {
			loadExcel(getFileUpload());
		}
		
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
		ResultSet rsEm = null,result=null,result1=null;
		
		PreparedStatement pst1=null;
		ResultSet rs1=null;
		
		List<String> alErrorList = new ArrayList<String>();
		try {
			
			UtilityFunctions uF = new UtilityFunctions();
			con = db.makeConnection(con);
			con.setAutoCommit(false);
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
					cellList.add(cell.toString());
				}
				dataList.add(cellList);
			}
			
			boolean flag = false;
			if (dataList.size() == 0) {
				flag = false;
			} else{
				
				int ii=0;
				List<String> empCodeList=new ArrayList<String>();
				pst = con.prepareStatement("Select upper(empcode) as empcode from employee_personal_details ");
				rs = pst.executeQuery();
				while (rs.next()) {
					empCodeList.add(rs.getString("empcode"));
				}
				rs.close();
				pst.close();
				
				for (int i = 2; i < dataList.size(); i++) {
					List<String> cellList = dataList.get(i);
					String employeeCode = cellList.get(1).toUpperCase().trim();
					String employeeName = cellList.get(2).toUpperCase().trim();
					
					ii++;
					int employee_id=0;
					String emp_bank_name = "";
					if(employeeCode!=null) {
						pst=con.prepareStatement("Select * from employee_personal_details ep,employee_official_details ef where upper(ep.empcode) = ? and ef.emp_id=ep.emp_per_id and ep.is_alive=true");
						pst.setString(1, employeeCode.toUpperCase().trim());
						rsEm = pst.executeQuery();
						if(rsEm.next()) {
				 			employee_id = rsEm.getInt("emp_per_id");
				 			emp_bank_name = rsEm.getString("emp_bank_name");
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
					
				}
			}
			
		} catch (Exception e) {
			sbMessage.append("<li class=\"msg_error\" style=\"margin:0px\">Employee Bank Details not imported please check imported file.</li>");
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
			session.setAttribute(MESSAGE, ERRORM+"Employee Bank Details not imported. Please check imported file."+END);
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public void genratedexcel(UtilityFunctions uF) {
		
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
			sbQuery.append("Select upper(empcode) as empcode,emp_fname,emp_mname,emp_lname,ep.emp_per_id from employee_personal_details ep,employee_official_details ef where " +
					" ef.emp_id=ep.emp_per_id and is_alive=true order by emp_fname, emp_lname ");
			pst=con.prepareStatement(sbQuery.toString());
			
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
				
				XSSFWorkbook workbook = new XSSFWorkbook();
				XSSFSheet sheet=workbook.createSheet("Employee Bank Details");
				XSSFCellStyle headerStyle1= workbook.createCellStyle();
			 	Font headerFont1 = workbook.createFont();
			 	headerFont1.setColor(IndexedColors.RED.getIndex());
			 	headerFont1.setFontHeightInPoints((short)8);
			 	headerStyle1.setAlignment(XSSFCellStyle.ALIGN_LEFT);
			 	headerStyle1.setBorderTop(XSSFCellStyle.BORDER_THIN);
			 	headerStyle1.setBorderRight(XSSFCellStyle.BORDER_THIN);
			 	headerStyle1.setBorderLeft(XSSFCellStyle.BORDER_THIN);
			 	headerStyle1.setBorderBottom(XSSFCellStyle.BORDER_THIN);
			 	headerStyle1.setFont(headerFont1);
			 	 
			 	XSSFCellStyle headerStyleGeen= workbook.createCellStyle();
			 	Font headerFontG = workbook.createFont();
			 	headerFontG.setColor(IndexedColors.GREEN.getIndex());
			 	headerFontG.setFontHeightInPoints((short)9);
			 	headerFontG.setBoldweight(Font.BOLDWEIGHT_BOLD);
			 	headerStyleGeen.setAlignment(XSSFCellStyle.ALIGN_CENTER);
			 	headerStyleGeen.setBorderTop(XSSFCellStyle.BORDER_THIN);
			 	headerStyleGeen.setBorderRight(XSSFCellStyle.BORDER_THIN);
			 	headerStyleGeen.setBorderLeft(XSSFCellStyle.BORDER_THIN);
			 	headerStyleGeen.setBorderBottom(XSSFCellStyle.BORDER_THIN);
			 	headerStyleGeen.setFont(headerFontG);
			 	 
			 	XSSFCellStyle subheaderStyle1= workbook.createCellStyle();
			 	Font subheaderFont= workbook.createFont();
			 	subheaderFont.setColor(IndexedColors.BLACK.getIndex());
			 	subheaderFont.setFontHeightInPoints((short)9);
			 	subheaderFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
			 	subheaderStyle1.setAlignment(XSSFCellStyle.ALIGN_CENTER);
			 	subheaderStyle1.setBorderTop(XSSFCellStyle.BORDER_THIN);
			 	subheaderStyle1.setBorderRight(XSSFCellStyle.BORDER_THIN);
			 	subheaderStyle1.setBorderLeft(XSSFCellStyle.BORDER_THIN);
			 	subheaderStyle1.setBorderBottom(XSSFCellStyle.BORDER_THIN);
			 	subheaderStyle1.setFont(subheaderFont);
			 	 
			 	XSSFCellStyle borderStyle1= workbook.createCellStyle();
			 	Font borderFont1 = workbook.createFont();
			 	borderFont1.setColor(IndexedColors.BLACK.getIndex());
			 	borderFont1.setFontHeightInPoints((short)9);
			 	borderStyle1.setAlignment(XSSFCellStyle.ALIGN_LEFT);
			 	borderStyle1.setBorderTop(XSSFCellStyle.BORDER_THIN);
			 	borderStyle1.setBorderRight(XSSFCellStyle.BORDER_THIN);
			 	borderStyle1.setBorderLeft(XSSFCellStyle.BORDER_THIN);
			 	borderStyle1.setBorderBottom(XSSFCellStyle.BORDER_THIN);
			 	borderStyle1.setFont(borderFont1);
			 	
			 	XSSFRow row=null;
			 	XSSFCell cell=null;
				
			    row = sheet.createRow(0);
				cell = row.createCell(0);
			    cell.setCellValue("EMPLOYEE BANK DETAILS");
			    cell.setCellStyle(headerStyleGeen);
			    sheet.autoSizeColumn(0);
			    row.setHeightInPoints(20);
			    
			    for(int i=1;i<6;i++){
			    	cell =row.createCell(i);
			    	cell.setCellValue("");
			    	cell.setCellStyle(headerStyleGeen);
			    }	
			     
			    sheet.addMergedRegion(new CellRangeAddress(0,0,0,6));
				
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
			    cell.setCellValue("Employee Bank Name");
			    cell.setCellStyle(subheaderStyle1);
			    sheet.autoSizeColumn(3);
			     
			    cell = row.createCell(4);
			    cell.setCellValue("Employee Bank Branch");
			    cell.setCellStyle(subheaderStyle1);
			    sheet.autoSizeColumn(4);
			    
			    cell =row.createCell(5);
			    cell.setCellValue("Employee Bank Account Number");
			    cell.setCellStyle(subheaderStyle1);
			    sheet.autoSizeColumn(5);
			     
			    cell =row.createCell(6);
			    cell.setCellValue("Employee Bank IFSC Code");
			    cell.setCellStyle(subheaderStyle1);
			    sheet.autoSizeColumn(6);
			    
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
		    		 
		    		/*cell=row.createCell(7);
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
		    		sheet.autoSizeColumn(9);*/
			    }
			    
			    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			    try {
			    	workbook.write(buffer);
					buffer.close();
			    } catch (IOException e1){
					e1.printStackTrace();
				}
			    
			    response.setHeader("Content-Disposition", "attachment; filename=\"BulkEmployeeBankDetails.xlsx\"");
				response.setContentType("application/vnd.ms-excel:UTF-8");
				response.setContentLength(buffer.size());
				
				try {
					ServletOutputStream op = response.getOutputStream();
					op = response.getOutputStream();
					op.write(buffer.toByteArray());
					op.flush();
					op.close();
				} catch (IOException e){
					e.printStackTrace();
				}
				
			} catch(Exception e){
				e.printStackTrace();
			}finally{
				db.closeConnection(con);
			}
			
			
		} catch (Exception e){
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
	
	HttpServletRequest request;
	HttpServletResponse response;

	@Override
	public void setServletRequest(HttpServletRequest request){
		this.request = request;
	}
	
	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	public File getFileUpload() {
		return fileUpload;
	}

	public void setFileUpload(File fileUpload) {
		this.fileUpload = fileUpload;
	}

	public String getExceldownload() {
		return exceldownload;
	}

	public void setExceldownload(String exceldownload) {
		this.exceldownload = exceldownload;
	}

	public String getFileUploadFileName() {
		return fileUploadFileName;
	}

	public void setFileUploadFileName(String fileUploadFileName) {
		this.fileUploadFileName = fileUploadFileName;
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

}