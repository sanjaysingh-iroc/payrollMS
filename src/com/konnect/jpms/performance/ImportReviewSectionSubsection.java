package com.konnect.jpms.performance;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.apache.poi.ss.usermodel.Cell;
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

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.konnect.jpms.export.DataStyle;
import com.konnect.jpms.leave.LeaveCard1;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IMessages;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ImportReviewSectionSubsection  extends ActionSupport implements ServletRequestAware, ServletResponseAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	private static Logger log = Logger.getLogger(LeaveCard1.class);
	
	
	private String f_org;
	private String wLocation;
	private String f_department;
	
	private File uploadF;
	
	private String reviewId;
	private String orientation;
	private String sectionId;
	private String attributeName;
	private String orientType;
	private String attribID;
	private String subSectionID;
	private String importMsg;
	private String appFreqId;
	
	private String importType;
	
	private String exceldownload;
	
	UtilityFunctions uF = new UtilityFunctions();
	
	public String execute() throws Exception {
  
		session = request.getSession();  
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		  
//		request.setAttribute(TITLE, "");
//		request.setAttribute(PAGE, "/jsp/leave/RegularizeLeaveBalance.jsp");
//		System.out.println("getReviewId() ===> " + getReviewId());
//		System.out.println("uploadFile ===> " + uploadF);
		
		if(getImportType() != null && getImportType().equals("Section")) {
			if(uploadF != null) {
				importReviewSectionSubsection(uploadF);
				return SUCCESS;
			}
		} else {
			
			if(getExceldownload() != null && getExceldownload().equalsIgnoreCase("true")) {
				generatedRevieweePanelExcel(uF, getReviewId());
				return SUCCESS;
			}
			
			if(uploadF != null) {
				importReviewee(uploadF);
				return SUCCESS;
			}
		}
		
		return LOAD;
	}
	
	private void generatedRevieweePanelExcel(UtilityFunctions uF, String reviewId) {
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
			
			Map<String, List<String>> hmEmpData = new LinkedHashMap<String, List<String>>();
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select * from appraisal_details ad, appraisal_reviewee_details ard where ad.appraisal_details_id=ard.appraisal_id and ad.appraisal_details_id=? ");
			pst=con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(reviewId));
			System.out.println("query for download report==>"+pst);
			rs = pst.executeQuery();
			Map<String, List<String>> hmRevieweePanel = new LinkedHashMap<String, List<String>>();
			while(rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("subordinate_ids")); //0
				innerList.add(rs.getString("peer_ids")); //1
				innerList.add(rs.getString("other_peer_ids")); //2
				innerList.add(rs.getString("supervisor_ids")); //3
				innerList.add(rs.getString("grand_supervisor_ids")); //4
				innerList.add(rs.getString("hod_ids")); //5
				innerList.add(rs.getString("ceo_ids")); //6
				innerList.add(rs.getString("hr_ids")); //7
				innerList.add(rs.getString("other_ids")); //8
				innerList.add(rs.getString("ghr_ids")); //9
				innerList.add(rs.getString("recruiter_ids")); //10
				hmRevieweePanel.put(rs.getString("reviewee_id"), innerList);
			}
			rs.close();
			pst.close();
			
			try {
				
				 XSSFWorkbook workbook = new XSSFWorkbook();
				 XSSFSheet sheet = workbook.createSheet("Reviewee Feedback Panel");
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
			 	 
			 	 XSSFCellStyle headerStyleGeen = workbook.createCellStyle();
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
				
			     row=sheet.createRow(0);
			     
				 cell = row.createCell(0);
			     cell.setCellValue("Sr.No.");
			     cell.setCellStyle(subheaderStyle1);
			     sheet.autoSizeColumn(0);
			     
			     cell =row.createCell(1);
			     cell.setCellValue("Reviwee Emp Code");
			     cell.setCellStyle(subheaderStyle1);
			     sheet.autoSizeColumn(1);
			     
			     cell =row.createCell(2);
			     cell.setCellValue("Sub-ordinates Emp Code");
			     cell.setCellStyle(subheaderStyle1);
			     sheet.autoSizeColumn(2);
			     
			     cell =row.createCell(3);
			     cell.setCellValue("Peers Emp Code");
			     cell.setCellStyle(subheaderStyle1);
			     sheet.autoSizeColumn(3);
			     
			     cell = row.createCell(4);
			     cell.setCellValue("Other Peers Emp Code");
			     cell.setCellStyle(subheaderStyle1);
			     sheet.autoSizeColumn(4);
			     
			     cell =row.createCell(5);
			     cell.setCellValue("Managers Emp Code");
			     cell.setCellStyle(subheaderStyle1);
			     sheet.autoSizeColumn(5);
			     
			     cell =row.createCell(6);
			     cell.setCellValue("Grand Managers Emp Code");
			     cell.setCellStyle(subheaderStyle1);
			     sheet.autoSizeColumn(6);
			     
			     cell =row.createCell(7);
			     cell.setCellValue("HODs Emp Code");
			     cell.setCellStyle(subheaderStyle1);
			     sheet.autoSizeColumn(7);
			     
			     cell =row.createCell(8);
			     cell.setCellValue("CEOs Emp Code");
			     cell.setCellStyle(subheaderStyle1);
			     sheet.autoSizeColumn(8);
			     
			     cell =row.createCell(9);
			     cell.setCellValue("HRs Emp Code");
			     cell.setCellStyle(subheaderStyle1);
			     sheet.autoSizeColumn(9);
			     
			     cell =row.createCell(10);
			     cell.setCellValue("Others Emp Code");
			     cell.setCellStyle(subheaderStyle1);
			     sheet.autoSizeColumn(10);
			     
//			     cell =row.createCell(11);
//			     cell.setCellValue("1.Add either name or code in reporting structure"+'\n'+
//			    		 "2.Add leave type by adding leave code with separation using comma(,). For eg: (CL,PL,EL)"+'\n'+
//			    		 "3.Please mark '-' at blank spaces or where,"+'\n'+
//			    		 "  -->You leave blank spaces"+'\n'+"  -->You don't assign Supervisor code or name"+'\n'+
//			    		 "  -->You don't assign HOD code or name"+'\n'+"  -->You don't assign HR code or name");
//			     cell.setCellStyle(headerStyle1);
//			     sheet.autoSizeColumn(11);
//			     row.setHeightInPoints(70);
			     
			     Map<String, String> hmEmpCode = CF.getEmpCodeMap(con);
			     int rowCount=0;
			     Iterator<String> it = hmRevieweePanel.keySet().iterator();
					int count=0;
					while (it.hasNext()){
						rowCount++;
						count++;
						String strEmpId = it.next();
						List<String> innerList = hmRevieweePanel.get(strEmpId);
						row=sheet.createRow(rowCount);
						
						 cell=row.createCell(0);
			    		 cell.setCellValue(uF.showData(""+count, ""));
			    		 cell.setCellStyle(borderStyle1);
			    		 
			    		 cell=row.createCell(1);
			    		 cell.setCellValue(uF.showData(hmEmpCode.get(strEmpId), "N/A"));
			    		 cell.setCellStyle(borderStyle1);
			    		 
			    		 cell=row.createCell(2);
			    		 cell.setCellValue(uF.showData(uF.getAppendData(innerList.get(0), hmEmpCode), ""));
			    		 cell.setCellStyle(borderStyle1);
			    		 
			    		 cell=row.createCell(3);
			    		 cell.setCellValue(uF.showData(uF.getAppendData(innerList.get(1), hmEmpCode), ""));
			    		 cell.setCellStyle(borderStyle1);
			    		 
			    		 cell=row.createCell(4);
			    		 cell.setCellValue(uF.showData(uF.getAppendData(innerList.get(2), hmEmpCode), ""));
			    		 cell.setCellStyle(borderStyle1);
			    		 sheet.autoSizeColumn(4);
			    		 
			    		 cell=row.createCell(5);
			    		 cell.setCellValue(uF.showData(uF.getAppendData(innerList.get(3), hmEmpCode), ""));
			    		 cell.setCellStyle(borderStyle1);
			    		 sheet.autoSizeColumn(5);
						
			    		 cell=row.createCell(6);
			    		 cell.setCellValue(uF.showData(uF.getAppendData(innerList.get(4), hmEmpCode), ""));
			    		 cell.setCellStyle(borderStyle1);
			    		 
			    		 cell=row.createCell(7);
			    		 cell.setCellValue(uF.showData(uF.getAppendData(innerList.get(5), hmEmpCode), ""));
			    		 cell.setCellStyle(borderStyle1);
			    		 sheet.autoSizeColumn(7);
			    		 
			    		 cell=row.createCell(8);
			    		 cell.setCellValue(uF.showData(uF.getAppendData(innerList.get(6), hmEmpCode), ""));
			    		 cell.setCellStyle(borderStyle1);
			    		 sheet.autoSizeColumn(8);
			    		 
			    		 cell=row.createCell(9);
			    		 cell.setCellValue(uF.showData(uF.getAppendData(innerList.get(7), hmEmpCode), ""));
			    		 cell.setCellStyle(borderStyle1);
			    		 sheet.autoSizeColumn(9);
			    		 
			    		 cell=row.createCell(10);
			    		 cell.setCellValue(uF.showData(uF.getAppendData(innerList.get(8), hmEmpCode), ""));
			    		 cell.setCellStyle(borderStyle1);
			    		 sheet.autoSizeColumn(10);
					}
				
			    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
				try {
					workbook.write(buffer);
					buffer.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				response.setHeader("Content-Disposition", "attachment; filename=\"Import_Reviewee.xlsx\"");
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
				
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				db.closeConnection(con);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs1);
			db.closeResultSet(rs);
			db.closeStatements(pst1);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	private void importReviewee(File path) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs =null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		StringBuilder sbMessage = new StringBuilder("<ul style=\"margin:0px; padding-bottom: 10px;\">");
		// Create an ArrayList to store the data read from excel sheet.
        List<List<String>> outerList = new ArrayList<List<String>>();
        List<String> alErrorList = new ArrayList<String>();
        FileInputStream fis = null;
        try {
        	con = db.makeConnection(con);
        	con.setAutoCommit(false);
        	String dateFormat = "dd/MM/yyyy";
    		String timeFormat = "HH:mm:ss";
            fis = new FileInputStream(path);
            XSSFWorkbook workbook = new XSSFWorkbook(fis);
            XSSFSheet sheet = workbook.getSheetAt(0);
            //if(sheet != null) {
	            int maxNumOfCells = sheet.getRow(0).getLastCellNum(); // The the maximum number of columns
	            Iterator rows = sheet.rowIterator();
	            while (rows.hasNext()) {
	                XSSFRow row = (XSSFRow) rows.next();
	                Iterator cells = row.cellIterator();
	                
	
	                List<String> data = new ArrayList<String>();
	                for(int cellCounter = 0; cellCounter < maxNumOfCells; cellCounter ++) { // Loop through cells
	                    XSSFCell cell;
//	                    System.out.println("IRSS/406--"+row.getCell(cellCounter ));
	                    if(row.getCell(cellCounter ) == null ) {
	                        cell = row.createCell(cellCounter);
	                    } else {
	                        cell = row.getCell(cellCounter);
	                    }
//	                  System.out.println("Cell val ==> "+cell.toString());
	                    data.add(uF.getExcelImportDataString(cell, workbook));
//	                    data.add(cell.toString());
	                }
	                System.out.println("data ===> "+data);
	                outerList.add(data);
	            
	            }
	            
//	            System.out.println("IRSS/419--outerList="+outerList);
            
	            List<String> alOrientMemIds = CF.getOrientationMemberDetails(con, uF.parseToInt(getOrientation()));
            
            List<String> empCodeList=new ArrayList<String>();
            Map<String, String> hmEmpIds = new HashMap<String, String>();
			pst = con.prepareStatement("Select upper(empcode) as empcode, emp_per_id from employee_personal_details ");
			rs = pst.executeQuery();
			while (rs.next()) {
				empCodeList.add(rs.getString("empcode"));
				hmEmpIds.put(rs.getString("empcode"), rs.getString("emp_per_id"));
			}
			rs.close();
			pst.close();
			boolean flag = true;
			List<String> alRevieweeIds = new ArrayList<String>();
			StringBuilder sbNewRevieweeIds = null;
            for (int k=1;k<outerList.size();k++) {
				List<String> innerList = outerList.get(k);
//				System.out.println("innerList ===>> " + innerList);
//				System.out.println("innerList.get(1) ===>> " + innerList.get(1));
				String srNo = innerList.get(0);
				
		//===start parvez date: 14-03-2022===		
				String revieweeCode = getStringValue1(innerList.get(1));
				
//				System.out.println("IRSS/439--revieweeCode="+revieweeCode);
				System.out.println("IRSS/444--revieweeCode="+getStringValue1(innerList.get(1)));
				String subordinateCodes = getStringValue1(innerList.get(2));
				String peerCodes = getStringValue1(innerList.get(3));
				String otherPeerCodes = getStringValue1(innerList.get(4));
				String managerCodes = getStringValue1(innerList.get(5));
				String grandManagerCodes = getStringValue1(innerList.get(6));
				String hodCodes = getStringValue1(innerList.get(7));
				String ceoCodes = getStringValue1(innerList.get(8));
				String hrCodes = getStringValue1(innerList.get(9));
				String othersCodes = getStringValue1(innerList.get(10));
				
		//===end parvez date: 14-03-2022===		
				
				if(subordinateCodes!=null && subordinateCodes.trim().length()>0 && !alOrientMemIds.contains("6")) {
					alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check the orientation of Reviewee Employee Code ('"+revieweeCode+"'), subordinates are not allow for selected orientation.</li>");
					flag = false;
					break;
				}
				if(peerCodes!=null && peerCodes.trim().length()>0 && !alOrientMemIds.contains("4")) {
					alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check the orientation of Reviewee Employee Code ('"+revieweeCode+"'), peers are not allow for selected orientation.</li>");
					flag = false;
					break;
				}
				if(otherPeerCodes!=null && otherPeerCodes.trim().length()>0 && !alOrientMemIds.contains("14")) {
					alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check the orientation of Reviewee Employee Code ('"+revieweeCode+"'), other peers are not allow for selected orientation.</li>");
					flag = false;
					break;
				}
				if(managerCodes!=null && managerCodes.trim().length()>0 && !alOrientMemIds.contains("2")) {
					System.out.println("IRSS/465---revieweeCode="+revieweeCode);
					alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check the orientation of Reviewee Employee Code ('"+revieweeCode+"'), managers are not allow for selected orientation.</li>");
					flag = false;
					break;
				}
				if(grandManagerCodes!=null && grandManagerCodes.trim().length()>0 && !alOrientMemIds.contains("8")) {
					alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check the orientation of Reviewee Employee Code ('"+revieweeCode+"'), grand managers are not allow for selected orientation.</li>");
					flag = false;
					break;
				}
				if(hodCodes!=null && hodCodes.trim().length()>0 && !alOrientMemIds.contains("13")) {
					alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check the orientation of Reviewee Employee Code ('"+revieweeCode+"'), HODs are not allow for selected orientation.</li>");
					flag = false;
					break;
				}
				if(ceoCodes!=null && ceoCodes.trim().length()>0 && !alOrientMemIds.contains("5")) {
					alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check the orientation of Reviewee Employee Code ('"+revieweeCode+"'), CEOs are not allow for selected orientation.</li>");
					flag = false;
					break;
				}
				if(hrCodes!=null && hrCodes.trim().length()>0 && !alOrientMemIds.contains("7")) {
					alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check the orientation of Reviewee Employee Code ('"+revieweeCode+"'), HRs are not allow for selected orientation.</li>");
					flag = false;
					break;
				}
				if(othersCodes!=null && othersCodes.trim().length()>0 && !alOrientMemIds.contains("10")) {
					alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check the orientation of Reviewee Employee Code ('"+revieweeCode+"'), anyone/ others are not allow for selected orientation.</li>");
					flag = false;
					break;
				}
				
				String revieweeId = null;
//				System.out.println("empCodeList="+empCodeList);
				if(revieweeCode != null && !empCodeList.contains(revieweeCode.trim().toUpperCase())) {
					alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check the Reviewee Employee Code ('"+revieweeCode+"') and try again.</li>");
					flag = false;
					break;
				} else {
					if(revieweeCode==null || revieweeCode.trim().equals("")) {
						continue;
					}
					revieweeId = hmEmpIds.get(revieweeCode.trim().toUpperCase());
				}
				alRevieweeIds.add(revieweeId);
				if(sbNewRevieweeIds == null) {
					sbNewRevieweeIds = new StringBuilder();
					sbNewRevieweeIds.append(","+revieweeId+",");
				} else {
					sbNewRevieweeIds.append(revieweeId+",");
				}
//				System.out.println("revieweeId ===>> " + revieweeId);
				
				StringBuilder sbSubordinateIds = null;
				if(subordinateCodes != null) {
					List<String> alIds = Arrays.asList(subordinateCodes.split(","));
					if(alIds.contains(revieweeCode)) {
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check the subordinates of Reviewee Employee Code ('"+revieweeCode+"'), you can not align the same employee as reviewee and subordinate.</li>");
						flag = false;
						break;
					}
					for(int i=0; alIds != null && i<alIds.size(); i++) {
						if(!alIds.get(i).trim().equals("")) {
							if(!empCodeList.contains(alIds.get(i).trim().toUpperCase())) {
								alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check the Sub-ordinate Employee code ('"+alIds.get(i)+"') and try again.</li>");
								flag = false;
								break;
							} else {
								if(sbSubordinateIds == null) {
									sbSubordinateIds = new StringBuilder();
									sbSubordinateIds.append(","+hmEmpIds.get(alIds.get(i).trim().toUpperCase())+",");
								} else {
									sbSubordinateIds.append(hmEmpIds.get(alIds.get(i).trim().toUpperCase())+",");
								}
							}
						}
					}
				}
				
				StringBuilder sbPeerIds = null;
				if(peerCodes != null) {
					List<String> alIds = Arrays.asList(peerCodes.split(","));
					if(alIds.contains(revieweeCode)) {
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check the peers of Reviewee Employee Code ('"+revieweeCode+"'), you can not align the same employee as reviewee and peer.</li>");
						flag = false;
						break;
					}
					for(int i=0; alIds != null && i<alIds.size(); i++) {
						if(!alIds.get(i).trim().equals("")) {
							if(!empCodeList.contains(alIds.get(i).trim().toUpperCase())) {
								alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check the Peer Employee code ('"+alIds.get(i)+"') and try again.</li>");
								flag = false;
								break;
							} else {
								if(sbPeerIds == null) {
									sbPeerIds = new StringBuilder();
									sbPeerIds.append(","+hmEmpIds.get(alIds.get(i).trim().toUpperCase())+",");
								} else {
									sbPeerIds.append(hmEmpIds.get(alIds.get(i).trim().toUpperCase())+",");
								}
							}
						}
					}
				}
				
				StringBuilder sbOtherPeerIds = null;
				if(otherPeerCodes != null) {
					List<String> alIds = Arrays.asList(otherPeerCodes.split(","));
					if(alIds.contains(revieweeCode)) {
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check the other peers of Reviewee Employee Code ('"+revieweeCode+"'), you can not align the same employee as reviewee and other peer.</li>");
						flag = false;
						break;
					}
					for(int i=0; alIds != null && i<alIds.size(); i++) {
						if(!alIds.get(i).trim().equals("")) {
							if(!empCodeList.contains(alIds.get(i).trim().toUpperCase())) {
								alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check the Other Peer Employee code ('"+alIds.get(i)+"') and try again.</li>");
								flag = false;
								break;
							} else {
								if(sbOtherPeerIds == null) {
									sbOtherPeerIds = new StringBuilder();
									sbOtherPeerIds.append(","+hmEmpIds.get(alIds.get(i).trim().toUpperCase())+",");
								} else {
									sbOtherPeerIds.append(hmEmpIds.get(alIds.get(i).trim().toUpperCase())+",");
								}
							}
						}
					}
				}
				
				StringBuilder sbManagerIds = null;
				if(managerCodes != null) {
					List<String> alIds = Arrays.asList(managerCodes.split(","));
					if(alIds.contains(revieweeCode)) {
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check the managers of Reviewee Employee Code ('"+revieweeCode+"'), you can not align the same employee as reviewee and manager.</li>");
						flag = false;
						break;
					}
					for(int i=0; alIds != null && i<alIds.size(); i++) {
						if(!alIds.get(i).trim().equals("")) {
							if(!empCodeList.contains(alIds.get(i).trim().toUpperCase())) {
								alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check the Manager Employee code ('"+alIds.get(i)+"') and try again.</li>");
								flag = false;
								break;
							} else {
								if(sbManagerIds == null) {
									sbManagerIds = new StringBuilder();
									sbManagerIds.append(","+hmEmpIds.get(alIds.get(i).trim().toUpperCase())+",");
								} else {
									sbManagerIds.append(hmEmpIds.get(alIds.get(i).trim().toUpperCase())+",");
								}
							}
						}
					}
				}
				
				StringBuilder sbGManagerIds = null;
				if(grandManagerCodes != null) {
					List<String> alIds = Arrays.asList(grandManagerCodes.split(","));
					if(alIds.contains(revieweeCode)) {
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check the grand managers of Reviewee Employee Code ('"+revieweeCode+"'), you can not align the same employee as reviewee and grand manager.</li>");
						flag = false;
						break;
					}
					for(int i=0; alIds != null && i<alIds.size(); i++) {
						if(!alIds.get(i).trim().equals("")) {
							if(!empCodeList.contains(alIds.get(i).trim().toUpperCase())) {
								alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check the Grand Manager Employee code ('"+alIds.get(i)+"') and try again.</li>");
								flag = false;
								break;
							} else {
								if(sbGManagerIds == null) {
									sbGManagerIds = new StringBuilder();
									sbGManagerIds.append(","+hmEmpIds.get(alIds.get(i).trim().toUpperCase())+",");
								} else {
									sbGManagerIds.append(hmEmpIds.get(alIds.get(i).trim().toUpperCase())+",");
								}
							}
						}
					}
				}
				
				StringBuilder sbHODIds = null;
				if(hodCodes != null) {
					List<String> alIds = Arrays.asList(hodCodes.split(","));
					if(alIds.contains(revieweeCode)) {
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check the HODs of Reviewee Employee Code ('"+revieweeCode+"'), you can not align the same employee as reviewee and HOD.</li>");
						flag = false;
						break;
					}
					for(int i=0; alIds != null && i<alIds.size(); i++) {
						if(!alIds.get(i).trim().equals("")) {
							if(!empCodeList.contains(alIds.get(i).trim().toUpperCase())) {
								alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check the HOD Employee code ('"+alIds.get(i)+"') and try again.</li>");
								flag = false;
								break;
							} else {
								if(sbHODIds == null) {
									sbHODIds = new StringBuilder();
									sbHODIds.append(","+hmEmpIds.get(alIds.get(i).trim().toUpperCase())+",");
								} else {
									sbHODIds.append(hmEmpIds.get(alIds.get(i).trim().toUpperCase())+",");
								}
							}
						}
					}
				}
				
				StringBuilder sbCEOIds = null;
				if(ceoCodes != null) {
					List<String> alIds = Arrays.asList(ceoCodes.split(","));
					if(alIds.contains(revieweeCode)) {
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check the CEOs of Reviewee Employee Code ('"+revieweeCode+"'), you can not align the same employee as reviewee and CEO.</li>");
						flag = false;
						break;
					}
					for(int i=0; alIds != null && i<alIds.size(); i++) {
						if(!alIds.get(i).trim().equals("")) {
							if(!empCodeList.contains(alIds.get(i).trim().toUpperCase())) {
								alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check the CEO Employee code ('"+alIds.get(i)+"') and try again.</li>");
								flag = false;
								break;
							} else {
								if(sbCEOIds == null) {
									sbCEOIds = new StringBuilder();
									sbCEOIds.append(","+hmEmpIds.get(alIds.get(i).trim().toUpperCase())+",");
								} else {
									sbCEOIds.append(hmEmpIds.get(alIds.get(i).trim().toUpperCase())+",");
								}
							}
						}
					}
				}
				
				StringBuilder sbHrIds = null;
				if(hrCodes != null) {
					List<String> alIds = Arrays.asList(hrCodes.split(","));
					if(alIds.contains(revieweeCode)) {
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check the HRs of Reviewee Employee Code ('"+revieweeCode+"'), you can not align the same employee as reviewee and HR.</li>");
						flag = false;
						break;
					}
					for(int i=0; alIds != null && i<alIds.size(); i++) {
						if(!alIds.get(i).trim().equals("")) {
							if(!empCodeList.contains(alIds.get(i).trim().toUpperCase())) {
								alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check the HR Employee code ('"+alIds.get(i)+"') and try again.</li>");
								flag = false;
								break;
							} else {
								if(sbHrIds == null) {
									sbHrIds = new StringBuilder();
									sbHrIds.append(","+hmEmpIds.get(alIds.get(i).trim().toUpperCase())+",");
								} else {
									sbHrIds.append(hmEmpIds.get(alIds.get(i).trim().toUpperCase())+",");
								}
							}
						}
					}
				}
				
				StringBuilder sbOthersIds = null;
				if(othersCodes != null) {
					List<String> alIds = Arrays.asList(othersCodes.split(","));
					if(alIds.contains(revieweeCode)) {
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check the anyone/ others of Reviewee Employee Code ('"+revieweeCode+"'), you can not align the same employee as reviewee and anyone/ other.</li>");
						flag = false;
						break;
					}
					for(int i=0; alIds != null && i<alIds.size(); i++) {
						if(!alIds.get(i).trim().equals("")) {
							if(!empCodeList.contains(alIds.get(i).trim().toUpperCase())) {
								alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check the HR Employee code ('"+alIds.get(i)+"') and try again.</li>");
								flag = false;
								break;
							} else {
								if(sbOthersIds == null) {
									sbOthersIds = new StringBuilder();
									sbOthersIds.append(","+hmEmpIds.get(alIds.get(i).trim().toUpperCase())+",");
								} else {
									sbOthersIds.append(hmEmpIds.get(alIds.get(i).trim().toUpperCase())+",");
								}
							}
						}
					}
				}
				
				boolean existFlag = false;
				pst = con.prepareStatement("select * from appraisal_reviewee_details where reviewee_id=? and appraisal_id=? and appraisal_freq_id=?");
				pst.setInt(1, uF.parseToInt(revieweeId));
				pst.setInt(2, uF.parseToInt(getReviewId()));
				pst.setInt(3, uF.parseToInt(getAppFreqId()));
				rs = pst.executeQuery();
//				System.out.println("pst ===>> " + pst);
				while(rs.next()) {
					existFlag = true;
				}
				rs.close();
				pst.close();
				
				if(!existFlag) {
					pst = con.prepareStatement("insert into appraisal_reviewee_details(reviewee_id,subordinate_ids,peer_ids,other_peer_ids,supervisor_ids," +
						"grand_supervisor_ids,hod_ids,ceo_ids,hr_ids,other_ids,added_by,entry_date,appraisal_id,appraisal_freq_id) " +
						"values(?,?,?,?, ?,?,?,?, ?,?,?,? ,?,?)");
					pst.setInt(1, uF.parseToInt(revieweeId));
					pst.setString(2, sbSubordinateIds != null ? sbSubordinateIds.toString() : null);
					pst.setString(3, sbPeerIds != null ? sbPeerIds.toString() : null);
					pst.setString(4, sbOtherPeerIds != null ? sbOtherPeerIds.toString() : null);
					pst.setString(5, sbManagerIds != null ? sbManagerIds.toString() : null);
					pst.setString(6, sbGManagerIds != null ? sbGManagerIds.toString() : null);
					pst.setString(7, sbHODIds != null ? sbHODIds.toString() : null);
					pst.setString(8, sbCEOIds != null ? sbCEOIds.toString() : null);
					pst.setString(9, sbHrIds != null ? sbHrIds.toString() : null);
					pst.setString(10, sbOthersIds != null ? sbOthersIds.toString() : null);
					pst.setInt(11, uF.parseToInt(strSessionEmpId));
					pst.setDate(12, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(13, uF.parseToInt(reviewId));
					pst.setInt(14, uF.parseToInt(appFreqId));
					int x = pst.executeUpdate();
//					System.out.println("pst ===>> " + pst);
					pst.close();
				} else {
					pst = con.prepareStatement("update appraisal_reviewee_details set subordinate_ids=?,peer_ids=?,other_peer_ids=?,supervisor_ids=?," +
						"grand_supervisor_ids=?,hod_ids=?,ceo_ids=?,hr_ids=?,other_ids=?,added_by=?,entry_date=? where appraisal_id=? " +
						"and appraisal_freq_id=? and reviewee_id=?");
					pst.setString(1, sbSubordinateIds != null ? sbSubordinateIds.toString() : null);
					pst.setString(2, sbPeerIds != null ? sbPeerIds.toString() : null);
					pst.setString(3, sbOtherPeerIds != null ? sbOtherPeerIds.toString() : null);
					pst.setString(4, sbManagerIds != null ? sbManagerIds.toString() : null);
					pst.setString(5, sbGManagerIds != null ? sbGManagerIds.toString() : null);
					pst.setString(6, sbHODIds != null ? sbHODIds.toString() : null);
					pst.setString(7, sbCEOIds != null ? sbCEOIds.toString() : null);
					pst.setString(8, sbHrIds != null ? sbHrIds.toString() : null);
					pst.setString(9, sbOthersIds != null ? sbOthersIds.toString() : null);
					pst.setInt(10, uF.parseToInt(strSessionEmpId));
					pst.setDate(11, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(12, uF.parseToInt(reviewId));
					pst.setInt(13, uF.parseToInt(appFreqId));
					pst.setInt(14, uF.parseToInt(revieweeId));
					int x = pst.executeUpdate();
//					System.out.println("pst ===>> " + pst);
					pst.close();
				}
	//			System.out.println("X ===>> "+x+" -- pst Que ===>> " + pst);
			}
            
            if(flag) {
            	if(!alRevieweeIds.isEmpty()) {
            		pst = con.prepareStatement("select * from appraisal_details where appraisal_details_id=?");
            		pst.setInt(1, uF.parseToInt(reviewId));
            		rs = pst.executeQuery();
            		String self_ids = null;
            		while(rs.next()) {
            			self_ids = rs.getString("self_ids");
            		}
            		rs.close();
            		pst.close();
            		
            		List<String> alExistIds = new ArrayList<String>();
            		StringBuilder sbRevieweeIds = null;
            		if(self_ids != null) {
            			sbRevieweeIds = new StringBuilder(self_ids);
            			alExistIds = Arrays.asList(self_ids.split(","));
            			for(int i=0; i<alRevieweeIds.size(); i++) {
            				if(!alExistIds.contains(alRevieweeIds.get(i))) {
            					sbRevieweeIds.append(alRevieweeIds.get(i)+",");
            				}
            			}
            			pst = con.prepareStatement("update appraisal_details set self_ids=? where appraisal_details_id=?");
                		pst.setString(1, sbRevieweeIds.toString());
            			pst.setInt(2, uF.parseToInt(reviewId));
            			pst.executeUpdate();
            			pst.close();
            		} else {
            			pst = con.prepareStatement("update appraisal_details set self_ids=? where appraisal_details_id=?");
                		pst.setString(1, sbNewRevieweeIds.toString());
            			pst.setInt(2, uF.parseToInt(reviewId));
            			pst.executeUpdate();
            			pst.close();
            		}
            	}
				con.commit();
				session.setAttribute(MESSAGE, SUCCESSM+"Reviewee Imported Successfully!"+END);
				sbMessage.append("<li class=\"msg savesuccess\" style=\"margin:0px\">Reviewee Imported Successfully!</li>");
				sbMessage.append("</ul>");
				System.out.println("sbMessage in commit else if ===>> " + sbMessage.toString());
				session.setAttribute("sbMessage", sbMessage.toString());
			} else {
				con.rollback();
//				System.out.println("rollback==>");
				if(alErrorList.size()>0) {
					sbMessage.append(alErrorList.get(alErrorList.size()-1));
				}
				session.setAttribute(MESSAGE, ERRORM+"Reviewee not imported. Please check imported file."+END);
				sbMessage.append("</ul>");
				System.out.println("sbMessage in rollback else ===>> " + sbMessage.toString());
				session.setAttribute("sbMessage", sbMessage.toString());
			}
        } catch (Exception e) {
        	session.setAttribute(MESSAGE, IMessages.ERRORM +"Data not import some problem in file, please try with currect format."+ IMessages.END);
        	e.printStackTrace();
        } finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	

	private void importReviewSectionSubsection(File path) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs =null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		List<String> alReport = new ArrayList<String>();
		// Create an ArrayList to store the data read from excel sheet.
        List<List<String>> outerList = new ArrayList<List<String>>();
        FileInputStream fis = null;
        try {
        	con = db.makeConnection(con);
        	
            fis = new FileInputStream(path);
            XSSFWorkbook workbook = new XSSFWorkbook(fis);

            XSSFSheet sheet = workbook.getSheetAt(0);

            //if(sheet != null) {
	            int maxNumOfCells = sheet.getRow(0).getLastCellNum(); // The the maximum number of columns
	            Iterator rows = sheet.rowIterator();
	            while (rows.hasNext()) {
	                XSSFRow row = (XSSFRow) rows.next();
	                Iterator cells = row.cellIterator();
	
	                List<String> data = new ArrayList<String>();
	                for( int cellCounter = 0; cellCounter < maxNumOfCells; cellCounter ++) { // Loop through cells
	                    XSSFCell cell;
	                    if( row.getCell(cellCounter ) == null ) {
	                        cell = row.createCell(cellCounter);
	                    } else {
	                        cell = row.getCell(cellCounter);
	                    }
	//                    System.out.println("Cell val ==> "+cell.toString());
	                    data.add(cell.toString());
	                }
	//                System.out.println("data ===> "+data);
	                outerList.add(data);
	            
	            }
           // }
            
            String systemType = null;
            String answerType = null;
            String sectionAttribute = null;
            int other_question_type_id = 0;
            
//            System.out.println("outerList ====>>> " + outerList);
            
            List<String> memberList=CF.getOrientationMemberDetails(con,uF.parseToInt(getOrientation()));
            Map<String, String> hmOrientMemberID = getOrientMemberID(con);
            
            for (int k=1;k<outerList.size();k++) {
				List<String> innerList=outerList.get(k);				
				String sectionNo = innerList.get(0);
				String sectionTitle = innerList.get(1);
				String sectionShortDesc = innerList.get(2);
				String sectionLongDesc = innerList.get(3);
				String sectionWeightage = innerList.get(4);
				if(innerList.get(5) != null && !innerList.get(5).equals("")){
					sectionAttribute = innerList.get(5);
				}
//				String sectionAttribute = innerList.get(5);
				String sectionWorkflow = innerList.get(6);
				String subSectionNo = innerList.get(7);
				String subSectionTitle = innerList.get(8);
				String subSectionShortDesc = innerList.get(9);
				String subSectionLongDesc = innerList.get(10);
				String subSectionWeightage = innerList.get(11);
				if(innerList.get(12) != null && !innerList.get(12).equals("")){
					systemType = innerList.get(12);
				}
				if(innerList.get(13) != null && !innerList.get(13).equals("")){
					answerType = innerList.get(13);
				}
				String question = innerList.get(14);
				String weightage = innerList.get(15);
				String answer = innerList.get(16);
				String opt1 = innerList.get(17);
				String opt2 = innerList.get(18);
				String opt3 = innerList.get(19);
				String opt4 = innerList.get(20);

//				System.out.println("data 0= "+sectionNo+" 1= "+sectionTitle+" 2= "+sectionShortDesc+" 3= "+sectionLongDesc+" 4= "+sectionWeightage
//						+" 5= "+sectionAttribute+" 6= "+sectionWorkflow+" 7= "+subSectionNo+" 8= "+subSectionTitle+" 9= "+subSectionShortDesc
//						+" 10= "+subSectionLongDesc+" 11= "+subSectionWeightage+" 12= "+systemType+" 13= "+answerType+" 14= "+question
//						+" 15= "+weightage+" 16= "+answer);
//				System.out.println("sectionNo ===>> " + sectionNo);
//				System.out.println("subSectionNo ===>> " + subSectionNo);
//				System.out.println("question ===>> " + question);
				
				if((sectionNo != null && !sectionNo.equals("") && !sectionNo.equals("null"))) {
					orientType = getOrientationType(con, getOrientation(), uF);
					attribID = getAttributeID(con, sectionAttribute, uF);
					if(uF.parseToInt(attribID) == 0){
						continue;
					}
//					System.out.println("sectionNo in if ===>> " + sectionNo);
					String self = "0", manager = "0", HR = "0", peer = "0", subordinate = "0", groupHead = "0", vendor = "0", client = "0", ceo="0", hod="0";
					
					if(memberList != null && memberList.size()>0 && hmOrientMemberID != null && hmOrientMemberID.size()>0) {
						if(hmOrientMemberID.get("HR") !=null && memberList.contains(hmOrientMemberID.get("HR"))) {
							HR = "1";
						}
						
						if(hmOrientMemberID.get("Manager") !=null && memberList.contains(hmOrientMemberID.get("Manager"))) {
							manager = "1";
						}
						
						if(hmOrientMemberID.get("Peer") !=null && memberList.contains(hmOrientMemberID.get("Peer"))) {
							peer = "1";
						} 
						
						if(hmOrientMemberID.get("Self") !=null && memberList.contains(hmOrientMemberID.get("Self"))) {
							self = "1";
						}
						
						if(hmOrientMemberID.get("Sub-ordinate") !=null && memberList.contains(hmOrientMemberID.get("Sub-ordinate"))) {
							subordinate = "1";
						}
						
						if(hmOrientMemberID.get("GroupHead") !=null && memberList.contains(hmOrientMemberID.get("GroupHead"))) {
							groupHead = "1";
						}
						
						if(hmOrientMemberID.get("Vendor") !=null && memberList.contains(hmOrientMemberID.get("Vendor"))) {
							vendor = "1";
						} 
						
						if(hmOrientMemberID.get("Client") !=null && memberList.contains(hmOrientMemberID.get("Client"))) {
							client = "1";
						} 
						
						if(hmOrientMemberID.get("CEO") !=null && memberList.contains(hmOrientMemberID.get("CEO"))) {
							ceo = "1";
						}
						
						if(hmOrientMemberID.get("HOD") !=null && memberList.contains(hmOrientMemberID.get("HOD"))) {
							hod = "1";
						}
					}

					pst = con.prepareStatement("insert into appraisal_main_level_details(level_title,short_description,long_description,"
							+ "appraisal_id,attribute_id,section_weightage,added_by,hr,manager,peer,self,subordinate,grouphead," +
							"vendor,client,entry_date,ceo,hod) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
					pst.setString(1, sectionTitle);
					pst.setString(2, sectionShortDesc);
					pst.setString(3, sectionLongDesc);
					pst.setInt(4, uF.parseToInt(reviewId));
					pst.setInt(5, uF.parseToInt(attribID));
					pst.setString(6, sectionWeightage);
					pst.setInt(7, uF.parseToInt(strSessionEmpId));
					
					pst.setInt(8,uF.parseToInt(HR));
					pst.setInt(9, uF.parseToInt(manager));
					pst.setInt(10,uF.parseToInt(peer));
					pst.setInt(11,uF.parseToInt(self));

					pst.setInt(12,uF.parseToInt(subordinate));
					pst.setInt(13,uF.parseToInt(groupHead));
					pst.setInt(14,uF.parseToInt(vendor));
					pst.setInt(15,uF.parseToInt(client));
					pst.setTimestamp(16, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + "" + uF.getCurrentTime(CF.getStrTimeZone()), DBDATE + DBTIME));
					pst.setInt(17,uF.parseToInt(ceo));
					pst.setInt(18,uF.parseToInt(hod));
					pst.execute();
					pst.close();
//					System.out.println("pst S ===>> " + pst);
					
					int section_id = 0;
					pst = con.prepareStatement("select max(main_level_id) from appraisal_main_level_details");
					rs = pst.executeQuery();
					while (rs.next()) {
						section_id = rs.getInt(1);
					}
					rs.close();
					pst.close();
					
					setSectionId(""+section_id);
				}
				
				if(subSectionNo != null && !subSectionNo.equals("") && !subSectionNo.equals("null") && uF.parseToInt(getSectionId()) > 0){
//					attribID = getAttributeID(con, sectionAttribute, uF);
//					orientType = getOrientationType(con, getOrientation(), uF);
					String systemTypeID = null;
					if(systemType.equalsIgnoreCase("Other")){
						systemTypeID = "2";
					}else{
						systemTypeID = "2";
					}

					pst = con.prepareStatement("insert into appraisal_level_details(level_title,short_description,long_description,appraisal_system," +
						"scorecard_type,appraisal_id,main_level_id,attribute_id,subsection_weightage,added_by,entry_date) values(?,?,?,?, ?,?,?,?, ?,?,?)");
					pst.setString(1, subSectionTitle);
					pst.setString(2, subSectionShortDesc);
					pst.setString(3, subSectionLongDesc);
					pst.setInt(4, uF.parseToInt(systemTypeID));
					pst.setInt(5, uF.parseToInt(""));
					pst.setInt(6, uF.parseToInt(reviewId));
					pst.setInt(7, uF.parseToInt(getSectionId()));
					pst.setInt(8, uF.parseToInt(attribID));
					pst.setString(9, subSectionWeightage);
					pst.setInt(10, uF.parseToInt(strSessionEmpId));
					pst.setTimestamp(11, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + "" + uF.getCurrentTime(CF.getStrTimeZone()), DBDATE + DBTIME));
					pst.execute();
					pst.close();
//					System.out.println("pst subS ===>> " + pst);
					
					int subSecID = 0;
					pst = con.prepareStatement("select max(appraisal_level_id) from appraisal_level_details");
//					pst.setInt(1, uF.parseToInt(getId()));
					rs = pst.executeQuery();
					while (rs.next()) {
						subSecID = rs.getInt(1);
						setSubSectionID("" + subSecID);
					}
					rs.close();
					pst.close();
				}

				if(question != null && !question.equals("") && !question.equals("null") && uF.parseToInt(getSectionId()) > 0){
					String ansTypeID = getAnswerTypeId(con, answerType, uF);

					String systemTypeID = null;
					if(systemType != null && systemType.equalsIgnoreCase("Other")) {
						systemTypeID = "2";
					} else {
						systemTypeID = "1";
					}
					
					if(systemTypeID != null && systemTypeID.equals("2") && subSectionNo != null && !subSectionNo.equals("") && !subSectionNo.equals("null")){	
						pst = con.prepareStatement("insert into appraisal_other_question_type_details(other_question_type,is_weightage,appraisal_id,level_id)values(?,?,?,?)");
						pst.setString(1, "Without Short Description");
						pst.setBoolean(2, true);
						pst.setInt(3, uF.parseToInt(reviewId));
						pst.setInt(4, uF.parseToInt(getSubSectionID()));
						pst.execute();
						pst.close();
						
						pst = con.prepareStatement("select max(othe_question_type_id) from appraisal_other_question_type_details");
						rs = pst.executeQuery();
						while (rs.next()) {
							other_question_type_id = rs.getInt(1);
						}
						rs.close();
						pst.close();
					}
					StringBuilder sbAns = new StringBuilder();
					List<String> ansList = Arrays.asList(answer.split(","));
					for(int i=0; ansList != null && i < ansList.size(); i++){
						String strAns = ansList.get(i);
						if (strAns.contains(".")) {
							strAns = strAns.substring(0, strAns.indexOf("."));
						}
						if(ansTypeID == null || ansTypeID.equals("5") || ansTypeID.equals("6")){
							if(uF.parseToInt(strAns.trim()) == 1){
								sbAns.append("1,");
							} else if(uF.parseToInt(strAns.trim()) == 2){
								sbAns.append("0,");
							}
							opt1 = ""; opt2 = ""; opt3 = ""; opt4 = "";
						} else {
							if(uF.parseToInt(strAns.trim()) == 1){
								sbAns.append("a,");
							} else if(uF.parseToInt(strAns.trim()) == 2){
								sbAns.append("b,");
							} else if(uF.parseToInt(strAns.trim()) == 3){
								sbAns.append("c,");
							} else if(uF.parseToInt(strAns.trim()) == 4){
								sbAns.append("d,");
							}
						}
					}
					pst = con.prepareStatement("insert into question_bank(question_text,option_a,option_b,option_c,option_d,correct_ans,is_add,question_type)values(?,?,?,?,?,?,?,?)");
					pst.setString(1, question);
					pst.setString(2, opt1);
					pst.setString(3, opt2);
					pst.setString(4, opt3);
					pst.setString(5, opt4);
					pst.setString(6, sbAns.toString());
					pst.setBoolean(7, uF.parseToBoolean("false"));
					pst.setInt(8, uF.parseToInt(ansTypeID));
					pst.execute();
					pst.close();
					
					int question_id = 0;
					pst = con.prepareStatement("select max(question_bank_id) from question_bank");
					rs = pst.executeQuery();
					while (rs.next()) {
						question_id = rs.getInt(1);
					}
					rs.close();
					pst.close();
//					}

					pst = con.prepareStatement("insert into appraisal_question_details(question_id,other_id,attribute_id,weightage,appraisal_id," +
						"other_short_description,appraisal_level_id,answer_type) values(?,?,?,?, ?,?,?,?)");
					pst.setInt(1, question_id);
					pst.setInt(2, other_question_type_id);
					pst.setInt(3, uF.parseToInt(attribID));
					pst.setDouble(4, uF.parseToDouble(weightage));
					pst.setInt(5, uF.parseToInt(reviewId));
					pst.setString(14, "");
					pst.setInt(15, uF.parseToInt(getSubSectionID()));
					pst.setInt(16, uF.parseToInt(ansTypeID));
					int x = pst.executeUpdate();
					pst.close();
//					System.out.println("X ===>> "+x+" -- pst Que ===>> " + pst);
					if(x > 0) {
						session.setAttribute(MESSAGE, IMessages.SUCCESSM +"Data import successfully!"+ IMessages.END);
						StringBuilder sbMessage = new StringBuilder("<ul style=\"margin:0px; padding-bottom: 10px;\">");
						sbMessage.append("<li class=\"msg savesuccess\" style=\"margin:0px\">Reviewee section data imported successfully!</li>");
						sbMessage.append("</ul>");
						session.setAttribute("sbMessage", sbMessage.toString());
//						setImportMsg(IMessages.SUCCESSM +"Data import successfully!"+ IMessages.END);
					} else {
						session.setAttribute(MESSAGE, IMessages.ERRORM +"Data not import some problem in file, please try with currect format."+ IMessages.END);
						StringBuilder sbMessage = new StringBuilder("<ul style=\"margin:0px; padding-bottom: 10px;\">");
						sbMessage.append("<li class=\"msg savesuccess\" style=\"margin:0px\">Reviewee section data not import some problem in file, please try with currect format.</li>");
						sbMessage.append("</ul>");
						session.setAttribute("sbMessage", sbMessage.toString());
//						setImportMsg(IMessages.ERRORM +"Data not import some problem in file, please try with currect format."+ IMessages.END);
					}
				}
//				System.out.println("Section Id ===> "+getSectionId());
			}
            
        } catch (Exception e) {
        	session.setAttribute(MESSAGE, IMessages.ERRORM +"Data not import some problem in file, please try with currect format."+ IMessages.END);
        	StringBuilder sbMessage = new StringBuilder("<ul style=\"margin:0px; padding-bottom: 10px;\">");
			sbMessage.append("<li class=\"msg savesuccess\" style=\"margin:0px\">Reviewee section data not import some problem in file, please try with currect format.</li>");
			sbMessage.append("</ul>");
			session.setAttribute("sbMessage", sbMessage.toString());
        	e.printStackTrace();
        } finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
        //showExcelData(sheetData);
	}
	
	private Map<String, String> getOrientMemberID(Connection con) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> orientationMemberMp = new HashMap<String, String>();

		try {
			
			pst = con.prepareStatement("select * from orientation_member where status=true order by weightage");
			rs = pst.executeQuery();
			while (rs.next()) {
				orientationMemberMp.put(rs.getString("member_name"), rs.getString("member_id"));
			}
			rs.close();
			pst.close();

//			System.out.println("memberid=====>"+orientationMemberMp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return orientationMemberMp;
	}


	private String getAnswerTypeId(Connection con, String ansType, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs =null;
		String answerTypeID = null;
		try {
			if(ansType !=null && !ansType.trim().equals("")){
				pst = con.prepareStatement("select appraisal_answer_type_id,appraisal_answer_type_name from appraisal_answer_type " +
						"where upper(appraisal_answer_type_name) = '"+ansType.toUpperCase().trim()+"'");
				rs = pst.executeQuery();
				while (rs.next()) {
					answerTypeID = rs.getString("appraisal_answer_type_id");
				}
				rs.close();
				pst.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		
		return answerTypeID;
	}
	
	private List<String> getOrientationMemberDetails(int id) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		List<String> memberList=new ArrayList<String>();
		try {
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from orientation_details where orientation_id=?");
			pst.setInt(1,id);
			rs=pst.executeQuery();
			while(rs.next()){
				memberList.add(rs.getString("member_id"));
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return memberList;
	}
	
	private String getOrientationType(Connection con, String orientID, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs =null;
		String orientType = null;
		try {
			pst = con.prepareStatement("select apparisal_orientation_id,orientation_name from apparisal_orientation where apparisal_orientation_id = ?");
			pst.setInt(1, uF.parseToInt(orientID));
			rs = pst.executeQuery();
			while (rs.next()) {
				orientType = rs.getString("orientation_name");
			}
			rs.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		return orientType;
	}

	
	private String getAttributeID(Connection con, String attributeName, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs =null;
		String attributeID = null;
		try {
			if(attributeName !=null && !attributeName.trim().equals("")){
				pst = con.prepareStatement("select arribute_id,attribute_name from appraisal_attribute where upper(attribute_name) = '"+attributeName.toUpperCase().trim()+"'");
				rs = pst.executeQuery();
				while (rs.next()) {
					attributeID = rs.getString("arribute_id");
				}
				rs.close();
				pst.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		return attributeID;
	}
	
	
	
	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
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

	
	public File getUploadF() {
		return uploadF;
	}

	public void setUploadF(File uploadF) {
		this.uploadF = uploadF;
	}

	public String getReviewId() {
		return reviewId;
	}

	public void setReviewId(String reviewId) {
		this.reviewId = reviewId;
	}

	public String getOrientation() {
		return orientation;
	}

	public void setOrientation(String orientation) {
		this.orientation = orientation;
	}

	public String getSectionId() {
		return sectionId;
	}

	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public String getOrientType() {
		return orientType;
	}

	public void setOrientType(String orientType) {
		this.orientType = orientType;
	}

	public String getAttribID() {
		return attribID;
	}

	public void setAttribID(String attribID) {
		this.attribID = attribID;
	}

	public String getSubSectionID() {
		return subSectionID;
	}

	public void setSubSectionID(String subSectionID) {
		this.subSectionID = subSectionID;
	}

	public String getImportMsg() {
		return importMsg;
	}

	public void setImportMsg(String importMsg) {
		this.importMsg = importMsg;
	}

	public String getImportType() {
		return importType;
	}

	public void setImportType(String importType) {
		this.importType = importType;
	}

	HttpServletResponse response;
	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}
	
	private HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getAppFreqId() {
		return appFreqId;
	}

	public void setAppFreqId(String appFreqId) {
		this.appFreqId = appFreqId;
	}
	
	public String getExceldownload() {
		return exceldownload;
	}

	public void setExceldownload(String exceldownload) {
		this.exceldownload = exceldownload;
	}

	public String getStringValue(String str) {
		try {
			if(uF.parseToDouble(str)>0) {
				str = String.valueOf(Double.valueOf(str).longValue());
				System.out.println("str="+str);
			} 
		} catch(Exception ex) {
		}
		return str;
	}
	
//===start parvez date: 14-03-2022===	
	public String getStringValue1(String str) {
		try {
			
			if(str.contains(".")){
				str = str.substring(0, str.indexOf("."));
			}
			
		} catch(Exception ex) {
		}
		return str;
	}
//===end parvez date: 14-03-2022===	
	
}



