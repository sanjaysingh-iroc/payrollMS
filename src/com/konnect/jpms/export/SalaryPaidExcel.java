package com.konnect.jpms.export;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.IOUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class SalaryPaidExcel implements ServletRequestAware, ServletResponseAware, IStatements{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static int nInitialRow = 10;
	HSSFWorkbook hwb = null;
	HSSFSheet sheet = null;
	String filePath;
	
	CommonFunctions CF = null;
	UtilityFunctions uF = null;
	HttpSession session;
	
	
	String paycycle;
	String wLocation;
	String f_department;
	String f_service;
	String level;

	
	public void execute() throws Exception {
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		
		uF = new UtilityFunctions();
		
		Database db=new Database();
		db.setRequest(request);
		Connection con=null;
		
		try {
			con=db.makeConnection(con);
			
	//		String paycycle = request.getParameter("paycycle");
	//		String wLocation = request.getParameter("wLocation");
	//		String f_department = request.getParameter("f_department");
	//		String f_service = request.getParameter("f_service");
	//		String level = request.getParameter("level");
			
			
	//		System.out.println("paycycle==>"+paycycle);
			
			
			
	//		SalaryPaidExcel sb = new SalaryPaidExcel("/home/konnect/Desktop/", "Salary");
			
			String arr[] = getPaycycle().split("-");
			
			init("Salary Sheet"+uF.getDateFormat(arr[0], DATE_FORMAT, "ddMMyy")+"-"+uF.getDateFormat(arr[1], DATE_FORMAT, "ddMMyy"));
			
			Map hmSalaryHead = CF.getSalaryHeadsMap(con);
			
			
			
			List<String> alSalaryHeadId = new ArrayList<String>();
			Map hmEmpSalaryData = new HashMap();
			Map hmWorkingDays = new HashMap();
			
			alSalaryHeadId.add("Sr. No");
			alSalaryHeadId.add("Emp Name");
			alSalaryHeadId.add("Designation");
			alSalaryHeadId.add("Working Days");
			alSalaryHeadId.add("Net Salary");
			alSalaryHeadId.add("Gross Salary");
			
			
			
			getData(alSalaryHeadId, hmSalaryHead, hmEmpSalaryData, hmWorkingDays, uF);
			addHeaders(alSalaryHeadId, hmSalaryHead, uF);
			
			
			
			
			writeDateFile(alSalaryHeadId, hmSalaryHead, hmEmpSalaryData, hmWorkingDays, CF, uF );
			
			addLogoImage(CF);
			writeExcelFile(response, "Payroll_"+uF.getDateFormat(arr[0], DATE_FORMAT, "ddMMyy")+"_"+uF.getDateFormat(arr[1], DATE_FORMAT, "ddMMyy")+".xls");
			
	//		sb.writeExcelFile(null, "/home/konnect/Desktop/test.xls");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeConnection(con);
		}
	
//		return "success";
	
	}
	
	public void getData(List<String> alSalaryHeadId, Map hmSalaryHead, Map hmEmpSalaryData, Map hmWorkingDays, UtilityFunctions uF){
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;	
		try {
			
			con = db.makeConnection(con);
			
			
			
			
			String arr[] = getPaycycle().split("-");
			
			
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from payroll_generation pg, employee_official_details eod where eod.emp_id = pg.emp_id  and paycycle = ? ");
			if(uF.parseToInt(getF_department())>0){
				sbQuery.append(" and depart_id = "+uF.parseToInt(getF_department()));
			}
			if(uF.parseToInt(getwLocation())>0){
				sbQuery.append(" and wlocation_id = "+uF.parseToInt(getwLocation()));
			}
			
//			if(uF.parseToInt(getF_service())>0){
//				sbQuery.append(" and service_id like '%"+uF.parseToInt(getF_service())+",%'");
//			}
			if(uF.parseToInt(getF_service())>0){
				sbQuery.append(" and pg.service_id ="+uF.parseToInt(getF_service()));
			}
			if(uF.parseToInt(getLevel())>0){
				sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id = "+uF.parseToInt(getLevel())+")");
			}
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(arr[2]));
			rs = pst.executeQuery();
			
			
			String strEmpIdOld = null;
			String strEmpIdNew = null;
			Map hmSalaryData = new HashMap();
			
			double dblGrossEAmount = 0;
			double dblGrossDAmount = 0;
			
			
			while(rs.next()){
				
				if(!alSalaryHeadId.contains(rs.getString("salary_head_id"))){
					alSalaryHeadId.add(rs.getString("salary_head_id"));
				}
				
				strEmpIdNew = rs.getString("emp_id");
				
				
				if(strEmpIdNew!=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)){
					hmSalaryData = new HashMap();
					dblGrossEAmount = 0;
					dblGrossDAmount = 0;
				}
				
				hmSalaryData.put(rs.getString("salary_head_id"), rs.getString("amount"));
				
				if(rs.getString("earning_deduction").equalsIgnoreCase("E")){
					dblGrossEAmount += uF.parseToDouble(rs.getString("amount"));
				}else if(rs.getString("earning_deduction").equalsIgnoreCase("D")){
					dblGrossDAmount += uF.parseToDouble(rs.getString("amount"));
				}
				
				hmSalaryData.put("GROSS_EARNING", uF.formatIntoTwoDecimal(dblGrossEAmount));
				hmSalaryData.put("GROSS_DEDUCTION", uF.formatIntoTwoDecimal(dblGrossDAmount));
				hmSalaryData.put("NET", uF.formatIntoTwoDecimal(dblGrossEAmount - dblGrossDAmount));
				
				hmEmpSalaryData.put(strEmpIdNew, hmSalaryData);
				
				
				strEmpIdOld = strEmpIdNew;
			}
			rs.close();
			pst.close();
			
			
			
			pst = con.prepareStatement("select * from attendance_details where to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ? order by emp_id");
			pst.setDate(1, uF.getDateFormat(arr[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(arr[1], DATE_FORMAT));
			rs = pst.executeQuery();
			
			while(rs.next()){
				
				String strDate = uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT);
				
				List alTemp = (List)hmWorkingDays.get(rs.getString("emp_id"));
				if(alTemp==null)alTemp=new ArrayList();
				
				if(!alTemp.contains(strDate)){
					alTemp.add(strDate);
				}
				
				hmWorkingDays.put(rs.getString("emp_id"), alTemp);
				
			}
			rs.close();
			pst.close();
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	HSSFFont dayRowBoldfont;
	HSSFFont dayRowNormalfont;
	
	public void init(String sheetName){
		this.hwb = new HSSFWorkbook();		
		this.sheet = hwb.createSheet(sheetName);
		dayRowBoldfont = hwb.createFont();;
		dayRowNormalfont = hwb.createFont();;
	}
	
	public void addLogoImage(CommonFunctions CF) {
		try {
 
//			InputStream is = new FileInputStream(filePath+File.separator+"logo1.jpeg");
//			InputStream is = new FileInputStream("http:////localhost:8080/PayrollMS/userImages/"+CF.getStrOrgLogo());
			
			URL url = new URL("http://"+CF.getStrEmailLocalHost()+":8080"+request.getContextPath()+"/userImages/"+CF.getStrOrgLogo());
			URLConnection urlcon = url.openConnection();
			InputStream is = urlcon.getInputStream();
			
			
			
			byte[] bytes = IOUtils.toByteArray(is);
			int pictureIdx = hwb.addPicture(bytes, Workbook.PICTURE_TYPE_JPEG);
			is.close();

			CreationHelper helper = hwb.getCreationHelper();
			Drawing drawing = sheet.createDrawingPatriarch();
			ClientAnchor anchor = helper.createClientAnchor();
			anchor.setCol1(1);
			anchor.setRow1(1);
			Picture pict = drawing.createPicture(anchor, pictureIdx);
			pict.resize();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void writeDateFile(List<String> alSalaryHeadId, Map hmSalaryHead, Map hmEmpSalary,Map hmWorkingDays, CommonFunctions CF, UtilityFunctions uF ) {
		Database db=new Database();
		db.setRequest(request);
		Connection con=null;
		try {
			
			
			con=db.makeConnection(con);
			
			Map hmEmpNames = CF.getEmpNameMap(con,null, null);
			Map hmEmpDesig = CF.getEmpDesigMap(con);
			
			
			HSSFRow rowhead = null;				
			HSSFCell cell = null;
			Excelstyle  estyle = new Excelstyle(hwb);
			
			
			Set set = hmEmpSalary.keySet();
			Iterator it = set.iterator();
			int nRowCount = 0;
			
			
			Map hmTotal = new HashMap();
			
			while(it.hasNext()){
				String strEmpId = (String)it.next();
				Map hmTemp = (Map)hmEmpSalary.get(strEmpId) ;
				
				rowhead = sheet.createRow(nInitialRow+2+2+nRowCount++);
				
				
				
				
				for(int x=0; x<alSalaryHeadId.size(); x++){
					cell = rowhead.createCell(x+1);
					
					
					if(x==0){
						cell.setCellValue(nRowCount);
						sheet.setColumnWidth(x+1, 10 * 256);
						cell.setCellStyle(getStyle(true, false, true, false, false));
					}else
					if(x==1){
						cell.setCellValue((String)hmEmpNames.get(strEmpId));
						sheet.setColumnWidth(x+1, 25 * 256);
						cell.setCellStyle(getStyle(false, false, true, false, false));
					}else
					if(x==2){
						cell.setCellValue((String)hmEmpDesig.get(strEmpId));
						sheet.setColumnWidth(x+1, 15 * 256);
						cell.setCellStyle(getStyle(false, false, true, false, false));
					}else
					if(x==3){
						List alTemp = (List)hmWorkingDays.get(strEmpId);
						if(alTemp==null)alTemp=new ArrayList();
						cell.setCellValue(alTemp.size());
						sheet.setColumnWidth(x+1, 20 * 256);
						cell.setCellStyle(getStyle(true, false, true, false, false));
					}else
					if(x==4){
						cell.setCellValue(uF.parseToDouble((String)hmTemp.get("NET")));
						sheet.setColumnWidth(x+1, 15 * 256);
						cell.setCellStyle(getStyle(false, true, true, false, false));
						
						double dblTotal = uF.parseToDouble((String)hmTotal.get("NET_TOTAL"));
						dblTotal += uF.parseToDouble((String)hmTemp.get("NET"));
						hmTotal.put("NET_TOTAL", uF.formatIntoTwoDecimal(dblTotal));
					}else
					if(x==5){
						cell.setCellValue(uF.parseToDouble((String)hmTemp.get("GROSS_EARNING")));
						sheet.setColumnWidth(x+1, 15 * 256);
						cell.setCellStyle(getStyle(false, true, true, false, false));
						
						double dblTotal = uF.parseToDouble((String)hmTotal.get("GROSS_EARNING_TOTAL"));
						dblTotal += uF.parseToDouble((String)hmTemp.get("GROSS_EARNING"));
						hmTotal.put("GROSS_EARNING_TOTAL", uF.formatIntoTwoDecimal(dblTotal));
					}else{
						cell.setCellValue(uF.parseToDouble((String)hmTemp.get((String)alSalaryHeadId.get(x))));
						sheet.setColumnWidth(x+1, 20 * 256);
						cell.setCellStyle(getStyle(false, true, false, false, false));
						
						double dblTotal = uF.parseToDouble((String)hmTotal.get((String)alSalaryHeadId.get(x)));
						dblTotal += uF.parseToDouble((String)hmTemp.get((String)alSalaryHeadId.get(x)));
						hmTotal.put((String)alSalaryHeadId.get(x), uF.formatIntoTwoDecimal(dblTotal));
						
					}
					
				}
				
			}
			
			
			
			rowhead = sheet.createRow(nInitialRow+2+3+nRowCount++);
			for(int x=0; x<alSalaryHeadId.size(); x++){
				cell = rowhead.createCell(x+1);
				
				
				
				if(x>5){
					cell.setCellValue(uF.parseToDouble((String)hmTotal.get((String)alSalaryHeadId.get(x))));
					sheet.setColumnWidth(x+1, 20 * 256);
					cell.setCellStyle(getStyle(false, true, true, true, true));
				}else if(x==4){
					cell.setCellValue(uF.parseToDouble((String)hmTotal.get("GROSS_EARNING_TOTAL")));
					sheet.setColumnWidth(x+1, 20 * 256);
					cell.setCellStyle(getStyle(false, true, true, true, true));
				}else if(x==5){
					cell.setCellValue(uF.parseToDouble((String)hmTotal.get("NET_TOTAL")));
					sheet.setColumnWidth(x+1, 20 * 256);
					cell.setCellStyle(getStyle(false, true, true, true, true));
				}else{
					cell.setCellStyle(getStyle(false, true, true, true, true));
				}
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			db.closeConnection(con);
		}
	}
	
	
	public void writeExcelFile(HttpServletResponse response, String fileName) {

		FileOutputStream fileOut = null;
		try {

			
			/*
			fileOut = new FileOutputStream(fileName);
			hwb.write(fileOut);
			fileOut.close();*/
			
			
//			ServletOutputStream op = response.getOutputStream();
//			response.setContentType("application/vnd.ms-excel:UTF-8");
//			response.setContentLength(hwb.getBytes().length);
//			response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
//			op.write(hwb.getBytes());
//			op.flush();
//			op.close();
			
			
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			hwb.write(bout);
			bout.close();
			
			response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
			response.setContentType("application/vnd.ms-excel:UTF-8");
			response.setContentLength(bout.size());
			
			ServletOutputStream op = response.getOutputStream();
			op.write(bout.toByteArray());
			op.flush();
			op.close();
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	
	public void addHeaders(List<String> alSalaryHeadId, Map hmSalaryHead, UtilityFunctions uF ){
		
		try {
			
			
			HSSFRow rowhead = null;				
			HSSFCell cell = null;
			
			String arr[] = getPaycycle().split("-");
			
			rowhead = sheet.createRow(nInitialRow);
			cell = rowhead.createCell(5);
			cell.setCellValue("Salary from "+arr[0]+" to "+arr[1]);
			cell.setCellStyle(getStyle(true, false, true, false, false));		
					
					
			rowhead = sheet.createRow(nInitialRow+2);
			
			for(int x=0; x<alSalaryHeadId.size(); x++){
				cell = rowhead.createCell(x+1);
				if(uF.parseToInt(alSalaryHeadId.get(x))>0){
					cell.setCellValue((String)hmSalaryHead.get(alSalaryHeadId.get(x)));
					cell.setCellStyle(getStyle(true, false, true, false, true));
					
				}else{
					cell.setCellValue(alSalaryHeadId.get(x));
					cell.setCellStyle(getStyle(true, false, true, false, true));
					
				}
				
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	};
	
	
	
	public HSSFCellStyle getStyle(boolean isCenterAlign, boolean isRightAlign, boolean isBold, boolean isTopBorder, boolean isBottomBorder){
		
		HSSFCellStyle cellStyle= hwb.createCellStyle();
		
		
		if(isBold){
			dayRowBoldfont.setFontName("Verdana");
			dayRowBoldfont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			cellStyle.setFont(dayRowBoldfont);
		}else{
			dayRowBoldfont.setFontName("Verdana");
			dayRowNormalfont.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
			cellStyle.setFont(dayRowNormalfont);
		}
		
		
		cellStyle.setWrapText(false);
		
		if(isRightAlign){
			cellStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
		}
		if(isCenterAlign){
			cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		}

		if(isTopBorder){
			cellStyle.setBorderTop((short) 1);
		}
		
		if(isBottomBorder){
			cellStyle.setBorderBottom((short) 1);
		}
		
		return cellStyle;
		
	}

	HttpServletRequest request;
	HttpServletResponse response;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
		
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

	public String getF_service() {
		return f_service;
	}

	public void setF_service(String f_service) {
		this.f_service = f_service;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}
	
	
}
