package com.konnect.jpms.performance;

import java.io.FileOutputStream;
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

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class GenerateReviewPdfReport implements ServletRequestAware, ServletResponseAware, IStatements {

	private static final long serialVersionUID = 1L;
	private HttpServletResponse response;
	HttpSession session;
	String strEmpId;
	String appId;
	String appFreqId;
	
	String sessionEmpId;
	CommonFunctions CF; 
	String type;

	HSSFSheet firstSheet;	
	//Collection<File> files;
	HSSFWorkbook workbook;
	//File exactFile;
	HSSFCellStyle cellStyleForHeader;
	HSSFCellStyle cellStyleForData;
	HSSFCellStyle cellStyleForReportName;	

	{
		workbook = new HSSFWorkbook();
		firstSheet = workbook.createSheet("Reports");
//		Row headerRow = firstSheet.createRow(0);
//		headerRow.setHeightInPoints(40);
		HSSFPalette pallet = workbook.getCustomPalette();
		pallet.setColorAtIndex(HSSFColor.GREY_25_PERCENT.index, (byte)230, (byte)225, (byte)225);
	}
	
	public void execute() {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return;

		sessionEmpId = (String) session.getAttribute(EMPID);

		UtilityFunctions uF = new UtilityFunctions();
		if(getType() != null && getType().equals("EXCEL")) {
			try {
				createExcelFile(workbook);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (uF.parseToInt(getStrEmpId()) > 0) {
			generateProjectPdfReport();
		}
		session.removeAttribute("pro_id");
	}
	

	public void createExcelFile(HSSFWorkbook workbook) throws Exception {

		FileOutputStream fileOut = null;
		
		generatePendingReviewerExcel();
		
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			try {
				workbook.write(buffer);
				buffer.close();
				
			} catch (IOException e1) {
				e1.printStackTrace();
			}
	
			response.setHeader("Content-Disposition", "attachment; filename=\"PendingReviewsReviewerList.xls\"");
			response.setContentType("application/vnd.ms-excel:UTF-8");
			response.setContentLength(buffer.size());
			
	
			try {
				ServletOutputStream op = response.getOutputStream();
				op = response.getOutputStream();
				op.write(buffer.toByteArray());
				op.flush();
				op.close();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
	}

	private void generatePendingReviewerExcel() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			Map<String, String> hmExistQueAns = new HashMap<String, String>();
			pst = con.prepareStatement("select user_type_id,user_id,emp_id from appraisal_question_answer where appraisal_id=? and appraisal_freq_id=? and is_submit=true group by user_type_id,user_id,emp_id");
			pst.setInt(1, uF.parseToInt(getAppId()));
			pst.setInt(2, uF.parseToInt(getAppFreqId()));
			rs = pst.executeQuery();
//			System.out.println("pst ============ > "+ pst);
			while (rs.next()) {
				hmExistQueAns.put(rs.getString("user_type_id")+"_"+rs.getString("user_id")+"_"+rs.getString("emp_id"), rs.getString("emp_id"));
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmExistQueAns ===>> " + hmExistQueAns);
			String strDomain = request.getServerName().split("\\.")[0];
//			setDomain(strDomain);
//			Thread th = new Thread(this);
//			th.start();
			
			Map<String, Map<String, String>> hmRevieweewiseAppraiser = getRevieweewiseAppraiser(con);
			Map<String, Map<String, String>> hmEmpInfo = CF.getEmpInfoMap(con, false);
			Map<String, String> hmOrientData = CF.getOrientMemberID(con);
			
			
			
			Row headingRowDesc = firstSheet.createRow(0);
			headingRowDesc.setHeight((short) 500);
			HSSFCellStyle headingStyle = workbook.createCellStyle();
			headingStyle.setBorderLeft(CellStyle.BORDER_THIN);
			headingStyle.setBorderRight(CellStyle.BORDER_THIN);
			headingStyle.setBorderTop(CellStyle.BORDER_THIN);
			headingStyle.setAlignment(CellStyle.ALIGN_CENTER);
			headingStyle.setWrapText(true);
			headingStyle.setVerticalAlignment(CellStyle.VERTICAL_TOP);
			HSSFFont headingFont = workbook.createFont();
			headingFont.setBoldweight((short) 1000);
			headingStyle.setFont(headingFont);

			firstSheet.setColumnWidth(0, 1000);
			Cell srNoCell = headingRowDesc.createCell(0);
			srNoCell.setCellValue(" Sr. ");
			srNoCell.setCellStyle(headingStyle);

			Cell nameOfReviewee = headingRowDesc.createCell(1);
			nameOfReviewee.setCellValue("  Name of Reviewee ");
			firstSheet.autoSizeColumn((short) 1);
			nameOfReviewee.setCellStyle(headingStyle);

			Cell nameOfRevieweeSelf = headingRowDesc.createCell(2);
			nameOfRevieweeSelf.setCellValue(" Self ");
			firstSheet.autoSizeColumn((short) 2);
			nameOfRevieweeSelf.setCellStyle(headingStyle);
			
			Cell nameOfManager = headingRowDesc.createCell(3);
			nameOfManager.setCellValue(" Manager ");
			firstSheet.autoSizeColumn((short) 3);
			nameOfManager.setCellStyle(headingStyle);
			
			Cell nameOfPeer = headingRowDesc.createCell(4);
			nameOfPeer.setCellValue(" Peer ");
			firstSheet.autoSizeColumn((short) 4);
			nameOfPeer.setCellStyle(headingStyle);
			
			
			Cell nameOfSubOrdinate = headingRowDesc.createCell(5);
			nameOfSubOrdinate.setCellValue(" Sub-ordinate ");
			firstSheet.autoSizeColumn((short) 5);
			nameOfSubOrdinate.setCellStyle(headingStyle);

			Cell nameOfOtherPeer = headingRowDesc.createCell(6);
			nameOfOtherPeer.setCellValue(" Other Peer ");
			firstSheet.autoSizeColumn((short) 6);
			nameOfOtherPeer.setCellStyle(headingStyle);

			Cell nameOfGroupHead = headingRowDesc.createCell(7);
			nameOfGroupHead.setCellValue(" GroupHead ");
			firstSheet.autoSizeColumn((short) 7);
			nameOfGroupHead.setCellStyle(headingStyle);
			
			Cell nameOfHOD = headingRowDesc.createCell(8);
			nameOfHOD.setCellValue(" HOD ");
			firstSheet.autoSizeColumn((short) 8);
			nameOfHOD.setCellStyle(headingStyle);
			
			Cell nameOfCEO = headingRowDesc.createCell(9);
			nameOfCEO.setCellValue(" CEO ");
			firstSheet.autoSizeColumn((short) 9);
			nameOfCEO.setCellStyle(headingStyle);
			
			Cell nameOfHR = headingRowDesc.createCell(10);
			nameOfHR.setCellValue(" HR ");
			firstSheet.autoSizeColumn((short) 10);
			nameOfHR.setCellStyle(headingStyle);
			
			
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Iterator<String> it = hmRevieweewiseAppraiser.keySet().iterator();
			int srCnt=0;
			while(it.hasNext()) {
				srCnt++;
				headingRowDesc = firstSheet.createRow(srCnt);
				String strRevieweeId = it.next();
				Map<String, String> hmRevieweeNameData = hmEmpInfo.get(strRevieweeId);
				
				Map<String, String> hmRevieweeData = hmRevieweewiseAppraiser.get(strRevieweeId);
				List<List<String>> allIdList = new ArrayList<List<String>>();
				/*if(hmRevieweeData.get("REVIEW_SELFID") != null && !hmRevieweeData.get("REVIEW_SELFID").equals("")) {
					List<String> selfID = Arrays.asList(hmRevieweeData.get("REVIEW_SELFID").split(",")); 
					for (int i = 0; selfID != null && i < selfID.size(); i++) {
						if(selfID.get(i) != null && !selfID.get(i).equals("")) {
							List<String> innerList = new ArrayList<String>();
							innerList.add(selfID.get(i));
							innerList.add("Self");
							allIdList.add(innerList);
						}
					}
				}*/
				firstSheet.setColumnWidth(0, 1000);
				Cell srNoCell1 = headingRowDesc.createCell(0);
				srNoCell1.setCellValue(""+srCnt);
				srNoCell1.setCellStyle(headingStyle);

				Cell nameOfReviewee1 = headingRowDesc.createCell(1);
				nameOfReviewee1.setCellValue(hmRevieweeNameData.get("FNAME")+" " +hmRevieweeNameData.get("LNAME"));
				firstSheet.autoSizeColumn((short) 1);
//				nameOfReviewee1.setCellStyle(headingStyle);

				StringBuilder strSelfName = null;
				if(hmRevieweeData.get("REVIEW_SELFID") != null && !hmRevieweeData.get("REVIEW_SELFID").equals("")) {
					List<String> selfID = Arrays.asList(hmRevieweeData.get("REVIEW_SELFID").split(",")); 
					for (int i = 0; selfID != null && i < selfID.size(); i++) {
						if(selfID.get(i) != null && !selfID.get(i).equals("")) {
							if(hmExistQueAns == null || hmExistQueAns.get(hmOrientData.get("Self")+"_"+selfID.get(i)+"_"+strRevieweeId) == null || uF.parseToInt(hmExistQueAns.get(hmOrientData.get("Self")+"_"+selfID.get(i)+"_"+strRevieweeId)) != uF.parseToInt(strRevieweeId)) {
								if(strSelfName == null) { 
									strSelfName = new StringBuilder();
									strSelfName.append(hmEmpName.get(selfID.get(i)));
								} else {
									strSelfName.append(", "+ hmEmpName.get(selfID.get(i)));
								}
							}
						}
					}
				}
				Cell nameOfRevieweeSelf1 = headingRowDesc.createCell(2);
				nameOfRevieweeSelf1.setCellValue((strSelfName != null) ? strSelfName.toString() : "");
				firstSheet.autoSizeColumn((short) 2);
				
				StringBuilder strManagersName = null;
				if(hmRevieweeData.get("REVIEW_MANAGERID") != null && !hmRevieweeData.get("REVIEW_MANAGERID").equals("")) {
					List<String> managerID = Arrays.asList(hmRevieweeData.get("REVIEW_MANAGERID").split(",")); 
					for (int i = 0; managerID != null && i < managerID.size(); i++) {
						if(managerID.get(i) != null && !managerID.get(i).equals("")) {
							if(hmExistQueAns == null || hmExistQueAns.get(hmOrientData.get("Manager")+"_"+managerID.get(i)+"_"+strRevieweeId) == null || uF.parseToInt(hmExistQueAns.get(hmOrientData.get("Manager")+"_"+managerID.get(i)+"_"+strRevieweeId)) != uF.parseToInt(strRevieweeId)) {
								if(strManagersName == null) { 
									strManagersName = new StringBuilder();
									strManagersName.append(hmEmpName.get(managerID.get(i)));
								} else {
									strManagersName.append(", "+ hmEmpName.get(managerID.get(i)));
								}
							}
						}
					}
				}
				Cell nameOfManager1 = headingRowDesc.createCell(3);
				nameOfManager1.setCellValue((strManagersName != null) ? strManagersName.toString() : "");
				firstSheet.autoSizeColumn((short) 3);
//				nameOfManager1.setCellStyle(headingStyle);
				
				
				StringBuilder strPeersName = null;
				if(hmRevieweeData.get("REVIEW_PEERID") != null && !hmRevieweeData.get("REVIEW_PEERID").equals("")) {
					List<String> peerID = Arrays.asList(hmRevieweeData.get("REVIEW_PEERID").split(",")); 
					for (int i = 0; peerID != null && i < peerID.size(); i++) {
						if(peerID.get(i) != null && !peerID.get(i).equals("")) {
							if(hmExistQueAns == null || hmExistQueAns.get(hmOrientData.get("Peer")+"_"+peerID.get(i)+"_"+strRevieweeId) == null || uF.parseToInt(hmExistQueAns.get(hmOrientData.get("Peer")+"_"+peerID.get(i)+"_"+strRevieweeId)) != uF.parseToInt(strRevieweeId)) {
								if(strPeersName == null) { 
									strPeersName = new StringBuilder();
									strPeersName.append(hmEmpName.get(peerID.get(i)));
								} else {
									strPeersName.append(", "+ hmEmpName.get(peerID.get(i)));
								}
							}
						}
					}
				}
				Cell nameOfPeer1 = headingRowDesc.createCell(4);
				nameOfPeer1.setCellValue((strPeersName != null) ? strPeersName.toString() : "");
				firstSheet.autoSizeColumn((short) 4);
//				nameOfPeer1.setCellStyle(headingStyle);
				
				StringBuilder strSubOrdinateName = null;
				if(hmRevieweeData.get("REVIEW_SUBORDINATEID") != null && !hmRevieweeData.get("REVIEW_SUBORDINATEID").equals("")) {
					List<String> subordinateID = Arrays.asList(hmRevieweeData.get("REVIEW_SUBORDINATEID").split(",")); 
					for (int i = 0; subordinateID != null && i < subordinateID.size(); i++) {
						if(subordinateID.get(i) != null && !subordinateID.get(i).equals("")) {
							if(hmExistQueAns == null || hmExistQueAns.get(hmOrientData.get("Sub-ordinate")+"_"+subordinateID.get(i)+"_"+strRevieweeId) == null || uF.parseToInt(hmExistQueAns.get(hmOrientData.get("Sub-ordinate")+"_"+subordinateID.get(i)+"_"+strRevieweeId)) != uF.parseToInt(strRevieweeId)) {
								if(strSubOrdinateName == null) { 
									strSubOrdinateName = new StringBuilder();
									strSubOrdinateName.append(hmEmpName.get(subordinateID.get(i)));
								} else {
									strSubOrdinateName.append(", "+ hmEmpName.get(subordinateID.get(i)));
								}
							}
						}
					}
				}
				Cell nameOfSubOrdinate1 = headingRowDesc.createCell(5);
				nameOfSubOrdinate1.setCellValue((strSubOrdinateName != null) ? strSubOrdinateName.toString() : "");
				firstSheet.autoSizeColumn((short) 5);
//				nameOfSubOrdinate1.setCellStyle(headingStyle);

				
				StringBuilder strOtherPeersName = null;
				if(hmRevieweeData.get("REVIEW_OTHERPEERID") != null && !hmRevieweeData.get("REVIEW_OTHERPEERID").equals("")) {
					List<String> otherPeerID = Arrays.asList(hmRevieweeData.get("REVIEW_OTHERPEERID").split(",")); 
					for (int i = 0; otherPeerID != null && i < otherPeerID.size(); i++) {
						if(otherPeerID.get(i) != null && !otherPeerID.get(i).equals("")) {
							if(hmExistQueAns == null || hmExistQueAns.get(hmOrientData.get("Other Peer")+"_"+otherPeerID.get(i)+"_"+strRevieweeId) == null || uF.parseToInt(hmExistQueAns.get(hmOrientData.get("Other Peer")+"_"+otherPeerID.get(i)+"_"+strRevieweeId)) != uF.parseToInt(strRevieweeId)) {
								if(strOtherPeersName == null) { 
									strOtherPeersName = new StringBuilder();
									strOtherPeersName.append(hmEmpName.get(otherPeerID.get(i)));
								} else {
									strOtherPeersName.append(", "+ hmEmpName.get(otherPeerID.get(i)));
								}
							}
						}
					}
				}
				Cell nameOfOtherPeer1 = headingRowDesc.createCell(6);
				nameOfOtherPeer1.setCellValue((strOtherPeersName != null) ? strOtherPeersName.toString() : "");
				firstSheet.autoSizeColumn((short) 6);
//				nameOfOtherPeer1.setCellStyle(headingStyle);
				
				
				StringBuilder strGroupHeadName = null;
				if(hmRevieweeData.get("REVIEW_GRANDSUPERVISORID") != null && !hmRevieweeData.get("REVIEW_GRANDSUPERVISORID").equals("")) {
					List<String> gSupervisorID = Arrays.asList(hmRevieweeData.get("REVIEW_GRANDSUPERVISORID").split(",")); 
					for (int i = 0; gSupervisorID != null && i < gSupervisorID.size(); i++) {
						if(gSupervisorID.get(i) != null && !gSupervisorID.get(i).equals("")) {
							if(hmExistQueAns == null || hmExistQueAns.get(hmOrientData.get("GroupHead")+"_"+gSupervisorID.get(i)+"_"+strRevieweeId) == null || uF.parseToInt(hmExistQueAns.get(hmOrientData.get("GroupHead")+"_"+gSupervisorID.get(i)+"_"+strRevieweeId)) != uF.parseToInt(strRevieweeId)) {
								if(strGroupHeadName == null) { 
									strGroupHeadName = new StringBuilder();
									strGroupHeadName.append(hmEmpName.get(gSupervisorID.get(i)));
								} else {
									strGroupHeadName.append(", "+ hmEmpName.get(gSupervisorID.get(i)));
								}
							}
						}
					}
				}
				Cell nameOfGroupHead1 = headingRowDesc.createCell(7);
				nameOfGroupHead1.setCellValue((strGroupHeadName != null) ? strGroupHeadName.toString() : "");
				firstSheet.autoSizeColumn((short) 7);
//				nameOfGroupHead1.setCellStyle(headingStyle);
				
				StringBuilder strHODName = null;
				if(hmRevieweeData.get("REVIEW_HODID") != null && !hmRevieweeData.get("REVIEW_HODID").equals("")) {
					List<String> hodID = Arrays.asList(hmRevieweeData.get("REVIEW_HODID").split(",")); 
					for (int i = 0; hodID != null && i < hodID.size(); i++) {
						if(hodID.get(i) != null && !hodID.get(i).equals("")) {
							if(hmExistQueAns == null || hmExistQueAns.get(hmOrientData.get("HOD")+"_"+hodID.get(i)+"_"+strRevieweeId) == null || uF.parseToInt(hmExistQueAns.get(hmOrientData.get("HOD")+"_"+hodID.get(i)+"_"+strRevieweeId)) != uF.parseToInt(strRevieweeId)) {
								if(strHODName == null) { 
									strHODName = new StringBuilder();
									strHODName.append(hmEmpName.get(hodID.get(i)));
								} else {
									strHODName.append(", "+ hmEmpName.get(hodID.get(i)));
								}
							}
						}
					}
				}
				Cell nameOfHOD1 = headingRowDesc.createCell(8);
				nameOfHOD1.setCellValue((strHODName != null) ? strHODName.toString() : "");
				firstSheet.autoSizeColumn((short) 8);
				
				StringBuilder strCEOName = null;
				if(hmRevieweeData.get("REVIEW_CEOID") != null && !hmRevieweeData.get("REVIEW_CEOID").equals("")) {
					List<String> ceoID = Arrays.asList(hmRevieweeData.get("REVIEW_CEOID").split(",")); 
					for (int i = 0; ceoID != null && i < ceoID.size(); i++) {
						if(ceoID.get(i) != null && !ceoID.get(i).equals("")) {
							if(hmExistQueAns == null || hmExistQueAns.get(hmOrientData.get("CEO")+"_"+ceoID.get(i)+"_"+strRevieweeId) == null || uF.parseToInt(hmExistQueAns.get(hmOrientData.get("CEO")+"_"+ceoID.get(i)+"_"+strRevieweeId)) != uF.parseToInt(strRevieweeId)) {
								if(strCEOName == null) { 
									strCEOName = new StringBuilder();
									strCEOName.append(hmEmpName.get(ceoID.get(i)));
								} else {
									strCEOName.append(", "+ hmEmpName.get(ceoID.get(i)));
								}
							}
						}
					}
				}
				Cell nameOfCEO1 = headingRowDesc.createCell(9);
				nameOfCEO1.setCellValue((strCEOName != null) ? strCEOName.toString() : "");
				firstSheet.autoSizeColumn((short) 9);
				
				StringBuilder strHRName = null;
				if(hmRevieweeData.get("REVIEW_HRID") != null && !hmRevieweeData.get("REVIEW_HRID").equals("")) {
					List<String> hrID = Arrays.asList(hmRevieweeData.get("REVIEW_HRID").split(",")); 
					for (int i = 0; hrID != null && i < hrID.size(); i++) {
						if(hrID.get(i) != null && !hrID.get(i).equals("")) {
							if(hmExistQueAns == null || hmExistQueAns.get(hmOrientData.get("HR")+"_"+hrID.get(i)+"_"+strRevieweeId) == null || uF.parseToInt(hmExistQueAns.get(hmOrientData.get("HR")+"_"+hrID.get(i)+"_"+strRevieweeId)) != uF.parseToInt(strRevieweeId)) {
								if(strHRName == null) { 
									strHRName = new StringBuilder();
									strHRName.append(hmEmpName.get(hrID.get(i)));
								} else {
									strHRName.append(", "+ hmEmpName.get(hrID.get(i)));
								}
							}
						}
					}
				}
				Cell nameOfHR1 = headingRowDesc.createCell(10);
				nameOfHR1.setCellValue((strHRName != null) ? strHRName.toString() : "");
				firstSheet.autoSizeColumn((short) 10);
				
				
				StringBuilder strOthersName = null;
				if(hmRevieweeData.get("REVIEW_OTHERID") != null && !hmRevieweeData.get("REVIEW_OTHERID").equals("")) {
					List<String> otherID = Arrays.asList(hmRevieweeData.get("REVIEW_OTHERID").split(",")); 
					for (int i = 0; otherID != null && i < otherID.size(); i++) {
						if(otherID.get(i) != null && !otherID.get(i).equals("")) {
							if(hmExistQueAns == null || hmExistQueAns.get(hmOrientData.get("Anyone")+"_"+otherID.get(i)+"_"+strRevieweeId) == null || uF.parseToInt(hmExistQueAns.get(hmOrientData.get("Anyone")+"_"+otherID.get(i)+"_"+strRevieweeId)) != uF.parseToInt(strRevieweeId)) {
								if(strOthersName == null) { 
									strOthersName = new StringBuilder();
									strOthersName.append(hmEmpName.get(otherID.get(i)));
								} else {
									strOthersName.append(", "+ hmEmpName.get(otherID.get(i)));
								}
							}
						}
					}
				}
				
				
//				for (int i = 0; allIdList != null && !allIdList.isEmpty() && i < allIdList.size(); i++) {
//					List<String> innerList = allIdList.get(i);
//					if(innerList.get(0) != null && !innerList.get(0).equals("")) {
//						Map<String, String> hmEmpInner = hmEmpInfo.get(innerList.get(0));
//						Notifications nF = new Notifications(N_PENDING_REVIEW_REMINDER, CF);
//						nF.setDomain(strDomain);
//						nF.request = request;
//						nF.setStrEmpId(innerList.get(0));
//						nF.setStrHostAddress(CF.getStrEmailLocalHost());
//						nF.setStrHostPort(CF.getStrHostPort());
//						nF.setStrContextPath(request.getContextPath());
//						nF.setStrRevieweeName(hmRevieweeNameData.get("FNAME")+" " +hmRevieweeNameData.get("LNAME"));
//						nF.setStrRoleType(innerList.get(1));
//						nF.setStrReviewName(hmReviewData.get("REVIEW_NAME"));
//						nF.setStrReviewStartdate(uF.getDateFormat(hmReviewData.get("REVIEW_STARTDATE"), DBDATE, CF.getStrReportDateFormat()));
//						nF.setStrReviewEnddate(uF.getDateFormat(hmReviewData.get("REVIEW_ENDDATE"), DBDATE, CF.getStrReportDateFormat()));
//			
//						nF.setStrEmpFname(hmEmpInner.get("FNAME"));
//						nF.setStrEmpLname(hmEmpInner.get("LNAME"));
//						nF.setEmailTemplate(true);				
//						nF.sendNotifications();
//					}
//				}
				request.setAttribute("STATUS_MSG", SUCCESSM+"Reminder mails sent successfully."+END);
			}
			
			
			
//			System.out.println("hmReadUnreadCount =====> "+ hmReadUnreadCount);
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("STATUS_MSG", ERRORM+"Reminder mails not sent, Please try again."+END);
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst); 
			db.closeConnection(con);
		}
	}

	
	private Map<String, Map<String, String>> getRevieweewiseAppraiser(Connection con) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		Map<String, Map<String, String>> hmRevieweewiseAppraiser = new HashMap<String, Map<String, String>>();
		try {
			pst = con.prepareStatement("select * from appraisal_reviewee_details where appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getAppId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				Map<String, String> hmRevieweeData = new HashMap<String, String>();	
				
				hmRevieweeData.put("REVIEW_SELFID", rs.getString("reviewee_id"));
				hmRevieweeData.put("REVIEW_PEERID", rs.getString("peer_ids"));
				hmRevieweeData.put("REVIEW_MANAGERID", rs.getString("supervisor_ids"));
				hmRevieweeData.put("REVIEW_HRID", rs.getString("hr_ids"));
				hmRevieweeData.put("REVIEW_OTHERID", rs.getString("other_ids"));
				hmRevieweeData.put("REVIEW_CEOID", rs.getString("ceo_ids"));
				hmRevieweeData.put("REVIEW_HODID", rs.getString("hod_ids"));
				hmRevieweeData.put("REVIEW_SUBORDINATEID", rs.getString("subordinate_ids"));
				hmRevieweeData.put("REVIEW_GRANDSUPERVISORID", rs.getString("grand_supervisor_ids"));
				hmRevieweeData.put("REVIEW_OTHERPEERID", rs.getString("other_peer_ids"));
				
				hmRevieweewiseAppraiser.put(rs.getString("reviewee_id"), hmRevieweeData);
			}
			rs.close();
			pst.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return hmRevieweewiseAppraiser;
	}


	private void generateProjectPdfReport() {

		UtilityFunctions uF = new UtilityFunctions();

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
			con = db.makeConnection(con);
			
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			String strEmpName = CF.getEmpNameMapByEmpId(con, getStrEmpId());
			
			List<String> empDetails = new ArrayList<String>();
			pst = con.prepareStatement("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and epd.emp_per_id=?");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			rs = pst.executeQuery();

			while (rs.next()) {
				empDetails.add(rs.getString("emp_per_id"));
				empDetails.add(uF.showData(rs.getString("empcode"), ""));
				empDetails.add(hmEmpName.get(rs.getString("emp_per_id")));
//				empDetails.add(hmEmpName.get(rs.getString("supervisor_emp_id")));
			}
			rs.close();
			pst.close();
			
			
			List<String> appraisalList = new ArrayList<String>();
			pst = con.prepareStatement("select * from appraisal_details where appraisal_details_id=?");
			pst.setInt(1, uF.parseToInt(getAppId()));
			rs = pst.executeQuery();
			String strUserTypesForFeedback = null;
			while (rs.next()) {
				appraisalList.add(uF.showData(rs.getString("appraisal_details_id"), ""));
				appraisalList.add(uF.showData(rs.getString("appraisal_name"), ""));
				appraisalList.add(uF.showData(rs.getString("appraisal_description"), ""));
				appraisalList.add(uF.showData(rs.getString("oriented_type"), ""));
				appraisalList.add(uF.getDateFormat(rs.getString("to_date"), DBDATE, CF.getStrReportDateFormat()));
				appraisalList.add(uF.showData(rs.getString("appraisal_instruction"), ""));
				appraisalList.add(uF.showData(rs.getString("user_types_for_feedback"), "")); //6
				strUserTypesForFeedback = rs.getString("user_types_for_feedback");
			}
			rs.close();
			pst.close();
			request.setAttribute("appraisalList", appraisalList);
			
			
			String strEmpSupervisor = null;
			List<String> revieweeData = new ArrayList<String>();
			pst = con.prepareStatement("select * from appraisal_reviewee_details where appraisal_id=? and reviewee_id=?");
			pst.setInt(1, uF.parseToInt(getAppId()));
			pst.setInt(2, uF.parseToInt(getStrEmpId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				List<String> al = new ArrayList<String>();
				if(rs.getString("grand_supervisor_ids") != null) {
					al = Arrays.asList(rs.getString("grand_supervisor_ids").split(","));
				}
				List<String> alSupervisor = new ArrayList<String>();
				if(rs.getString("supervisor_ids") != null) {
					alSupervisor = Arrays.asList(rs.getString("supervisor_ids").split(","));
				}
				if(alSupervisor.size() > 1 && uF.parseToInt(alSupervisor.get(1).trim())>0) {
					strEmpSupervisor = hmEmpName.get(alSupervisor.get(1));
				}
//				System.out.println("alSupervisor ===>> " + alSupervisor);
//				System.out.println("strEmpSupervisor ===>> " + strEmpSupervisor);
				StringBuilder gSupervisorName = null;
				for(int i=0; al != null && i<al.size(); i++) {
					if(uF.parseToInt(al.get(i))>0) {
						if(gSupervisorName ==null) {
							gSupervisorName = new StringBuilder();
							gSupervisorName.append(hmEmpName.get(al.get(i)));
						} else {
							gSupervisorName.append(", "+hmEmpName.get(al.get(i)));
						}
					}
				}
				revieweeData.add(gSupervisorName!=null ? gSupervisorName.toString() : " ");
			}
			rs.close();
			pst.close();
			
			empDetails.add(strEmpSupervisor); //3
			
			request.setAttribute("empDetails", empDetails);
			request.setAttribute("revieweeData", revieweeData);
			
			
			// mainLevelList.add("0");
			Map<String, List<List<String>>> hmMainLevelFeedbackCommentsData = new HashMap<String, List<List<String>>>();
			List<List<String>> alMainLevelFeedbackCommentsData = new ArrayList<List<String>>();
			pst = con.prepareStatement("select * from appraisal_question_answer where appraisal_id=? and emp_id=? and (question_id is null or question_id = 0) and is_submit=true order by section_id");
			pst.setInt(1, uF.parseToInt(getAppId()));
			pst.setInt(2, uF.parseToInt(getStrEmpId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				alMainLevelFeedbackCommentsData = hmMainLevelFeedbackCommentsData.get(rs.getString("user_type_id")+"_"+rs.getString("section_id"));
				if(alMainLevelFeedbackCommentsData == null)alMainLevelFeedbackCommentsData = new ArrayList<List<String>>();
				
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("section_id"));
				innerList.add(rs.getString("section_comment"));
				alMainLevelFeedbackCommentsData.add(innerList);
				hmMainLevelFeedbackCommentsData.put(rs.getString("user_type_id")+"_"+rs.getString("section_id"), alMainLevelFeedbackCommentsData);
			}
			rs.close();
			pst.close();
			
			
			// mainLevelList.add("0");
			Map<String, List<String>> hmMainLevelData = new LinkedHashMap<String, List<String>>();
			pst = con.prepareStatement("select * from appraisal_main_level_details where appraisal_id=? order by main_level_id");
			pst.setInt(1, uF.parseToInt(getAppId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("main_level_id"));
				innerList.add(rs.getString("level_title"));
				innerList.add(rs.getString("short_description"));
				innerList.add(rs.getString("long_description"));
				innerList.add(rs.getString("attribute_id"));
				innerList.add(rs.getString("section_weightage"));
				
				hmMainLevelData.put(rs.getString("main_level_id"), innerList);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmMainLevelData", hmMainLevelData);
//			System.out.println("hmMainLevelData ===>> " + hmMainLevelData);
			
			Map<String, List<List<String>>> hmAppSubSectionData = new LinkedHashMap<String, List<List<String>>>();
			List<List<String>> alAppSubSectionData = new ArrayList<List<String>>();
			pst = con.prepareStatement("select * from appraisal_level_details where appraisal_id=? order by appraisal_level_id");
			pst.setInt(1, uF.parseToInt(getAppId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				alAppSubSectionData = hmAppSubSectionData.get(rs.getString("main_level_id"));
				if(alAppSubSectionData == null)alAppSubSectionData = new ArrayList<List<String>>();
				
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("appraisal_level_id"));
				innerList.add(rs.getString("level_title"));
				innerList.add(rs.getString("short_description"));
				innerList.add(rs.getString("long_description"));
				innerList.add(rs.getString("subsection_weightage"));
				alAppSubSectionData.add(innerList);
				hmAppSubSectionData.put(rs.getString("main_level_id"), alAppSubSectionData);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmAppSubSectionData", hmAppSubSectionData);
//			System.out.println("hmAppSubSectionData ===>> " + hmAppSubSectionData);
			
			Map<String, List<List<String>>> hmAppCompetencyData = new LinkedHashMap<String, List<List<String>>>();
			List<List<String>> alAppCompetencyData = new ArrayList<List<String>>();
			pst = con.prepareStatement("select * from appraisal_scorecard_details where appraisal_id=? order by scorecard_id");
			pst.setInt(1, uF.parseToInt(getAppId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				alAppCompetencyData = hmAppCompetencyData.get(rs.getString("level_id"));
				if(alAppCompetencyData == null)alAppCompetencyData = new ArrayList<List<String>>();
				
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("scorecard_id"));
				innerList.add(rs.getString("scorecard_section_name"));
				innerList.add(rs.getString("scorecard_description"));
				innerList.add(rs.getString("scorecard_weightage"));
				alAppCompetencyData.add(innerList);
				hmAppCompetencyData.put(rs.getString("level_id"), alAppCompetencyData);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmAppCompetencyData", hmAppCompetencyData);
//			System.out.println("hmAppCompetencyData ===>> " + hmAppCompetencyData);
			
			Map<String, List<List<String>>> hmAppMeasureData = new LinkedHashMap<String, List<List<String>>>();
			List<List<String>> alAppMeasureData = new ArrayList<List<String>>();
			pst = con.prepareStatement("select * from appraisal_measure_details where appraisal_id=? order by measure_id");
			pst.setInt(1, uF.parseToInt(getAppId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				alAppMeasureData = hmAppMeasureData.get(rs.getString("scorecard_id"));
				if(alAppMeasureData == null)alAppMeasureData = new ArrayList<List<String>>();
				
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("measure_id"));
				innerList.add(rs.getString("measure_section_name"));
				innerList.add(rs.getString("measure_description"));
				innerList.add(rs.getString("weightage"));
				alAppMeasureData.add(innerList);
				hmAppMeasureData.put(rs.getString("scorecard_id"), alAppMeasureData);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmAppMeasureData", hmAppMeasureData);
//			System.out.println("hmAppMeasureData ===>> " + hmAppMeasureData);
			
			
			Map<String, List<List<String>>> hmAppQuestionData = new LinkedHashMap<String, List<List<String>>>();
			List<List<String>> alAppQuestionData = new ArrayList<List<String>>();
			pst = con.prepareStatement("select * from appraisal_question_details aqd, question_bank qb where qb.question_bank_id = aqd.question_id " +
				"and appraisal_id=? order by appraisal_question_details_id");
			pst.setInt(1, uF.parseToInt(getAppId()));
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				alAppQuestionData = hmAppQuestionData.get(rs.getString("measure_id"));
				if(alAppQuestionData == null)alAppQuestionData = new ArrayList<List<String>>();
				
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("appraisal_question_details_id"));
				innerList.add(rs.getString("question_id"));
				innerList.add(rs.getString("question_text"));
				innerList.add(rs.getString("weightage"));
				alAppQuestionData.add(innerList);
				hmAppQuestionData.put(rs.getString("measure_id"), alAppQuestionData);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmAppQuestionData", hmAppQuestionData);
//			System.out.println("hmAppQuestionData ===>> " + hmAppQuestionData);

			Map<String, List<List<String>>> hmAppQueAnsDataUserTypewise = new LinkedHashMap<String, List<List<String>>>();
			List<List<String>> alAppQueAnsDataUserTypewise = new ArrayList<List<String>>();
			pst = con.prepareStatement("select * from appraisal_question_answer aqw, question_bank qb where qb.question_bank_id = aqw.question_id and " +
				"aqw.appraisal_id=? and emp_id=? and is_submit=true order by reviewer_or_appraiser,user_type_id");
			pst.setInt(1, uF.parseToInt(getAppId()));
			pst.setInt(2, uF.parseToInt(getStrEmpId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				alAppQueAnsDataUserTypewise = hmAppQueAnsDataUserTypewise.get(rs.getString("appraisal_question_details_id")+"_"+rs.getString("user_type_id"));
				if(alAppQueAnsDataUserTypewise == null)alAppQueAnsDataUserTypewise = new ArrayList<List<String>>();
				
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("appraisal_question_answer_id"));
				innerList.add(rs.getString("appraisal_question_details_id"));
				innerList.add(rs.getString("question_text"));
				String strAnswer = null;
				if(rs.getString("answer") != null && rs.getString("answer").length()>1 && ",".equals(rs.getString("answer").substring(rs.getString("answer").length()-1, rs.getString("answer").length())))
					strAnswer = rs.getString("answer").substring(0, rs.getString("answer").length()-1);
				if(strAnswer != null && strAnswer.equals("a")) {
					innerList.add(rs.getString("option_a"));
				} else if(strAnswer != null && strAnswer.equals("b")) {
					innerList.add(rs.getString("option_b"));
				} else if(strAnswer != null && strAnswer.equals("c")) {
					innerList.add(rs.getString("option_c"));
				} else if(strAnswer != null && strAnswer.equals("d")) {
					innerList.add(rs.getString("option_d"));
				} else if(strAnswer != null && strAnswer.equals("e")) {
					innerList.add(rs.getString("option_e"));
				} else {
					innerList.add("");
				}
				innerList.add(rs.getString("answer"));
				innerList.add(rs.getString("section_comment"));
				alAppQueAnsDataUserTypewise.add(innerList);
				hmAppQueAnsDataUserTypewise.put(rs.getString("appraisal_question_details_id")+"_"+rs.getString("user_type_id"), alAppQueAnsDataUserTypewise);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmAppQueAnsDataUserTypewise", hmAppQueAnsDataUserTypewise);
//			System.out.println("hmAppQueAnsDataUserTypewise ===>> " + hmAppQueAnsDataUserTypewise);
			

			ByteArrayOutputStream buffer = generatePdfDocument(con, uF, revieweeData, empDetails, appraisalList, hmMainLevelData, hmAppSubSectionData,
				hmAppCompetencyData, hmAppMeasureData, hmAppQuestionData, hmAppQueAnsDataUserTypewise, strUserTypesForFeedback, hmMainLevelFeedbackCommentsData);

			strEmpName = strEmpName.replace(" ", "_");
			response.setContentType("application/pdf");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition", "attachment; filename=Feedback_of_"+strEmpName+".pdf");
			ServletOutputStream out = response.getOutputStream();
			buffer.writeTo(out);
			out.flush();
				
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	
	public ByteArrayOutputStream generatePdfDocument(Connection con, UtilityFunctions uF, List<String> revieweeData, List<String> empDetails, List<String> appraisalList, Map<String, List<String>> hmMainLevelData, 
		Map<String, List<List<String>>> hmAppSubSectionData, Map<String, List<List<String>>> hmAppCompetencyData, Map<String, List<List<String>>> hmAppMeasureData, 
		Map<String, List<List<String>>> hmAppQuestionData, Map<String, List<List<String>>> hmAppQueAnsDataUserTypewise, String strUserTypesForFeedback, Map<String, List<List<String>>> hmMainLevelFeedbackCommentsData) {
		//System.out.println("generatePdfDocument =======");
		Font heading = new Font(Font.FontFamily.TIMES_ROMAN, 13);
		Font normal = new Font(Font.FontFamily.TIMES_ROMAN, 11);
		Font normal1 = new Font(Font.FontFamily.TIMES_ROMAN, 9);
		Font normalwithbold = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
		Font small = new Font(Font.FontFamily.TIMES_ROMAN, 8);
		Font small1 = new Font(Font.FontFamily.TIMES_ROMAN, 8);
		Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.BOLD);
		Font italicEffect = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.ITALIC);

		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		try {

			Document document = new Document(PageSize.A3);
			PdfWriter.getInstance(document, buffer);
			document.open();

			PdfPTable table = new PdfPTable(8);
			table.setWidthPercentage(100);
//			table.setFooterRows(25);

			PdfPCell row1 = new PdfPCell(new Paragraph(appraisalList.get(1), small));
			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
			row1.setBorder(Rectangle.LEFT | Rectangle.TOP | Rectangle.RIGHT);
			row1.setColspan(8);
			row1.setPadding(2.5f);
			table.addCell(row1);

			//	New Row
			row1 = new PdfPCell(new Paragraph(" ", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT | Rectangle.TOP | Rectangle.RIGHT);
			row1.setColspan(8);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			
			// New Row
//			String strelement = uF.showData("Reviewee Code", "");
//			List<Element> al = HTMLWorker.parseToList(new StringReader(strelement), null);
//			Paragraph pr = new Paragraph("", small);
//			pr.addAll(al);
			row1 =new PdfPCell(new Paragraph(uF.showData("Reviewee Code", ""), small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT | Rectangle.TOP | Rectangle.RIGHT);
//			row1.setColspan(2);
//			row1.setPadding(2.5f);
			table.addCell(row1);
			
//			List<Element> al2 =HTMLWorker.parseToList(new StringReader(uF.showData(empDetails.get(1), "")), null);
//			pr = new Paragraph("", small);
//			pr.addAll(al2);
			row1 =new PdfPCell(new Paragraph(uF.showData(empDetails.get(1), ""), small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT | Rectangle.TOP | Rectangle.RIGHT);
//			row1.setColspan(2);
//			row1.setPadding(2.5f);
			table.addCell(row1);

//			List<Element> al3 =HTMLWorker.parseToList(new StringReader(uF.showData("Reviewee Name", "")), null);
//			pr = new Paragraph("", small);
//			pr.addAll(al3);
			row1 =new PdfPCell(new Paragraph(uF.showData("Reviewee Name", ""), small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT | Rectangle.TOP | Rectangle.RIGHT);
//			row1.setColspan(2);
//			row1.setPadding(2.5f);
			table.addCell(row1);
			
//			List<Element> al4 =HTMLWorker.parseToList(new StringReader(uF.showData(empDetails.get(2), "")), null);
//			pr = new Paragraph("", small);
//			pr.addAll(al4);
			row1 =new PdfPCell(new Paragraph(uF.showData(empDetails.get(2), ""), small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT | Rectangle.TOP | Rectangle.RIGHT);
			row1.setColspan(2);
//			row1.setPadding(2.5f);
			table.addCell(row1);
			
//			List<Element> al5 =HTMLWorker.parseToList(new StringReader(uF.showData("Supervisor: "+empDetails.get(3), "")), null);
//			pr = new Paragraph("", small);
//			pr.addAll(al5);
			row1 =new PdfPCell(new Paragraph("Supervisor: "+uF.showData(empDetails.get(3), ""), small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT | Rectangle.TOP | Rectangle.RIGHT);
//			row1.setColspan(2);
//			row1.setPadding(2.5f);	
			table.addCell(row1);
			
//			List<Element> al6 =HTMLWorker.parseToList(new StringReader(uF.showData("Grand Supervisor: ", "")), null);
//			pr = new Paragraph("", small);
//			pr.addAll(al6);
			row1 =new PdfPCell(new Paragraph("Grand Supervisor: "+uF.showData(revieweeData.get(0), ""), small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT | Rectangle.TOP | Rectangle.RIGHT);
//			row1.setColspan(2);
//			row1.setPadding(2.5f);
			table.addCell(row1);
			
			row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT | Rectangle.TOP | Rectangle.RIGHT);
//			row1.setColspan(2);
//			row1.setPadding(2.5f);
			table.addCell(row1);
			
			
//			New Row
			row1 = new PdfPCell(new Paragraph(" ", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT | Rectangle.TOP | Rectangle.RIGHT);
			row1.setColspan(8);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			
			// New Row
			row1 =new PdfPCell(new Paragraph("Sr. No.", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT | Rectangle.TOP | Rectangle.RIGHT);
//			row1.setColspan(2);
//			row1.setPadding(2.5f);
			table.addCell(row1);
			
			row1 =new PdfPCell(new Paragraph("Category", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT | Rectangle.TOP | Rectangle.RIGHT);
//			row1.setColspan(2);
//			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 =new PdfPCell(new Paragraph("Compentency", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT | Rectangle.TOP | Rectangle.RIGHT);
//			row1.setColspan(2);
//			row1.setPadding(2.5f);
			table.addCell(row1);
			
			row1 =new PdfPCell(new Paragraph("Element", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT | Rectangle.TOP | Rectangle.RIGHT);
//			row1.setColspan(2);
//			row1.setPadding(2.5f);
			table.addCell(row1);
			
			row1 =new PdfPCell(new Paragraph("Team Assessment", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT | Rectangle.TOP | Rectangle.RIGHT);
//			row1.setColspan(2);
//			row1.setPadding(2.5f);	
			table.addCell(row1);
			
			row1 =new PdfPCell(new Paragraph("Functional Peer Assessment", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT | Rectangle.TOP | Rectangle.RIGHT);
//			row1.setColspan(2);
//			row1.setPadding(2.5f);
			table.addCell(row1);
			
			row1 = new PdfPCell(new Paragraph("Cross Functional Peer Assessment", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT | Rectangle.TOP | Rectangle.RIGHT);
//			row1.setColspan(2);
//			row1.setPadding(2.5f);
			table.addCell(row1);
			
			row1 = new PdfPCell(new Paragraph("Supervisor Assessment", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT | Rectangle.TOP | Rectangle.RIGHT);
//			row1.setColspan(2);
//			row1.setPadding(2.5f);
			table.addCell(row1);
			
//			System.out.println("hmMainLevelData pdf ===>> " + hmMainLevelData);
			Iterator<String> it = hmMainLevelData.keySet().iterator();
			while(it.hasNext()) {
				String strMainLevelId = it.next();
//				System.out.println("strMainLevelId ===>> " + strMainLevelId);
				List<String> innerList = hmMainLevelData.get(strMainLevelId);
				int measureCnt=0;
				StringBuilder sbSectionComment1 = null;
				StringBuilder sbSectionComment2 = null;
				StringBuilder sbSectionComment3 = null;
				StringBuilder sbSectionComment4 = null;
				int secCommentCnt1=0;
				int secCommentCnt2=0;
				int secCommentCnt3=0;
				int secCommentCnt4=0;
				List<List<String>> alAppSubSectionData = hmAppSubSectionData.get(strMainLevelId);
				for(int i=0; alAppSubSectionData != null && i<alAppSubSectionData.size(); i++) {
					List<String> innerList1 = alAppSubSectionData.get(i);
//					System.out.println("innerList1 ===>> " + innerList1);
					List<List<String>> alAppCompetencyData = hmAppCompetencyData.get(innerList1.get(0));
//					System.out.println("alAppCompetencyData ===>> " + alAppCompetencyData);
					for(int j=0; alAppCompetencyData != null && j<alAppCompetencyData.size(); j++) {
						List<String> innerList2 = alAppCompetencyData.get(j);
//						System.out.println("innerList2 ===>> " + innerList2);
						List<List<String>> alAppMeasureData = hmAppMeasureData.get(innerList2.get(0));
						for(int k=0; alAppMeasureData != null && k<alAppMeasureData.size(); k++) {
							List<String> innerList3 = alAppMeasureData.get(k);
							int queCnt=0;
							measureCnt++;
//							System.out.println("innerList3 ===>> " + innerList3);
							List<List<String>> alAppQuestionData = hmAppQuestionData.get(innerList3.get(0));
							for(int l=0; alAppQuestionData != null && l<alAppQuestionData.size(); l++) {
								queCnt++;
								List<String> innerList4 = alAppQuestionData.get(l);
//								System.out.println("innerList4 ===>> " + innerList4);
								// New Row
								row1 =new PdfPCell(new Paragraph(innerList.get(1).subSequence(0, 1)+""+measureCnt+"."+queCnt, small));
								row1.setHorizontalAlignment(Element.ALIGN_LEFT);
								row1.setBorder(Rectangle.BOX);
//								row1.setColspan(2);
//								row1.setPadding(2.5f);
								table.addCell(row1);
								
								row1 =new PdfPCell(new Paragraph(uF.showData(innerList.get(1), ""), small));
								row1.setHorizontalAlignment(Element.ALIGN_LEFT);
								row1.setBorder(Rectangle.BOX);
//								row1.setColspan(2);
//								row1.setPadding(2.5f);
								table.addCell(row1);

								row1 =new PdfPCell(new Paragraph(uF.showData(innerList3.get(1), ""), small));
								row1.setHorizontalAlignment(Element.ALIGN_LEFT);
								row1.setBorder(Rectangle.BOX);
//								row1.setColspan(2);
//								row1.setPadding(2.5f);
								table.addCell(row1);
								
								row1 =new PdfPCell(new Paragraph(uF.showData(innerList4.get(2), ""), small));
								row1.setHorizontalAlignment(Element.ALIGN_LEFT);
								row1.setBorder(Rectangle.BOX);
//								row1.setColspan(2);
//								row1.setPadding(2.5f);
								table.addCell(row1);
								
								
								List<List<String>> alAppQueAnsDataUserTypewise = hmAppQueAnsDataUserTypewise.get(innerList4.get(0)+"_6"); // Sub-ordinate
								StringBuilder sbUserAnswers = null;
								for(int a=0; alAppQueAnsDataUserTypewise!= null && a<alAppQueAnsDataUserTypewise.size(); a++) {
									List<String> innerList5 = alAppQueAnsDataUserTypewise.get(a);
									if(sbUserAnswers == null) {
										sbUserAnswers = new StringBuilder();
										sbUserAnswers.append(innerList5.get(3));
									} else {
										sbUserAnswers.append("\n\n\n"+innerList5.get(3));
									}
									if(secCommentCnt1 == 0 && innerList5.get(5) != null && !innerList5.get(5).trim().equals("")) {
										if(sbSectionComment1 == null) {
											sbSectionComment1 = new StringBuilder();
											sbSectionComment1.append(innerList5.get(5));
										} else {
											sbSectionComment1.append("\n\n\n"+innerList5.get(5));
										}
									}
								}
								row1 =new PdfPCell(new Paragraph(sbUserAnswers != null ? sbUserAnswers.toString() : " ", small));
								row1.setHorizontalAlignment(Element.ALIGN_LEFT);
								row1.setBorder(Rectangle.BOX);
//								row1.setColspan(2);
//								row1.setPadding(2.5f);	
								table.addCell(row1);
								
								
								alAppQueAnsDataUserTypewise = hmAppQueAnsDataUserTypewise.get(innerList4.get(0)+"_4"); // peer
								sbUserAnswers = null;
								for(int a=0; alAppQueAnsDataUserTypewise!= null && a<alAppQueAnsDataUserTypewise.size(); a++) {
									List<String> innerList5 = alAppQueAnsDataUserTypewise.get(a);
									if(sbUserAnswers == null) {
										sbUserAnswers = new StringBuilder();
										sbUserAnswers.append(innerList5.get(3));
									} else {
										sbUserAnswers.append("\n\n\n"+innerList5.get(3));
									}
									if(secCommentCnt2 == 0 && innerList5.get(5) != null && !innerList5.get(5).trim().equals("")) {
										if(sbSectionComment2 == null) {
											sbSectionComment2 = new StringBuilder();
											sbSectionComment2.append(innerList5.get(5));
										} else {
											sbSectionComment2.append("\n\n\n"+innerList5.get(5));
										}
									}
								}
								row1 =new PdfPCell(new Paragraph(sbUserAnswers != null ? sbUserAnswers.toString() : " ", small));
								row1.setHorizontalAlignment(Element.ALIGN_LEFT);
								row1.setBorder(Rectangle.BOX);
//								row1.setColspan(2);
//								row1.setPadding(2.5f);
								table.addCell(row1);
								
								
								alAppQueAnsDataUserTypewise = hmAppQueAnsDataUserTypewise.get(innerList4.get(0)+"_14"); // other peer
								sbUserAnswers = null;
								for(int a=0; alAppQueAnsDataUserTypewise!= null && a<alAppQueAnsDataUserTypewise.size(); a++) {
									List<String> innerList5 = alAppQueAnsDataUserTypewise.get(a);
									if(sbUserAnswers == null) {
										sbUserAnswers = new StringBuilder();
										sbUserAnswers.append(innerList5.get(3));
									} else {
										sbUserAnswers.append("\n\n\n"+innerList5.get(3));
									}
									if(secCommentCnt3 == 0 && innerList5.get(5) != null && !innerList5.get(5).trim().equals("")) {
										if(sbSectionComment3 == null) {
											sbSectionComment3 = new StringBuilder();
											sbSectionComment3.append(innerList5.get(5));
										} else {
											sbSectionComment3.append("\n\n\n"+innerList5.get(5));
										}
									}
								}
								row1 =new PdfPCell(new Paragraph(sbUserAnswers != null ? sbUserAnswers.toString() : " ", small));
								row1.setHorizontalAlignment(Element.ALIGN_LEFT);
								row1.setBorder(Rectangle.BOX);
//								row1.setColspan(2);
//								row1.setPadding(2.5f);
								table.addCell(row1);
								
								alAppQueAnsDataUserTypewise = hmAppQueAnsDataUserTypewise.get(innerList4.get(0)+"_2"); // Manager
								sbUserAnswers = null;
								for(int a=0; alAppQueAnsDataUserTypewise!= null && a<alAppQueAnsDataUserTypewise.size(); a++) {
									List<String> innerList5 = alAppQueAnsDataUserTypewise.get(a);
									if(sbUserAnswers == null) {
										sbUserAnswers = new StringBuilder();
										sbUserAnswers.append(innerList5.get(3));
									} else {
										sbUserAnswers.append("\n\n\n"+innerList5.get(3));
									}
									if(secCommentCnt4 == 0 && innerList5.get(5) != null && !innerList5.get(5).trim().equals("")) {
										if(sbSectionComment4 == null) {
											sbSectionComment4 = new StringBuilder();
											sbSectionComment4.append(innerList5.get(5));
										} else {
											sbSectionComment4.append("\n\n\n"+innerList5.get(5));
										}
									}
								}
								row1 =new PdfPCell(new Paragraph(sbUserAnswers != null ? sbUserAnswers.toString() : " ", small));
								row1.setHorizontalAlignment(Element.ALIGN_LEFT);
								row1.setBorder(Rectangle.BOX);
//								row1.setColspan(2);
//								row1.setPadding(2.5f);
								table.addCell(row1);
								
								secCommentCnt1++;
								secCommentCnt2++;
								secCommentCnt3++;
								secCommentCnt4++;
							}
						}
					}
				}
				
				// New Row
				row1 =new PdfPCell(new Paragraph("Q"+innerList.get(1).toUpperCase().subSequence(0, 1), small));
				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
				row1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				row1.setBorder(Rectangle.BOX);
				table.addCell(row1);
				
				row1 =new PdfPCell(new Paragraph("Qualitative Inputs on "+uF.showData(innerList.get(1), ""), small));
				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
				row1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				row1.setBorder(Rectangle.BOX);
				row1.setColspan(3);
				table.addCell(row1);

				row1 =new PdfPCell(new Paragraph(sbSectionComment1 != null ? sbSectionComment1.toString() : " ", small));
				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
				row1.setVerticalAlignment(Element.ALIGN_TOP);
				row1.setBorder(Rectangle.BOX);
				table.addCell(row1);
				
				row1 =new PdfPCell(new Paragraph(sbSectionComment2 != null ? sbSectionComment2.toString() : " ", small));
				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
				row1.setVerticalAlignment(Element.ALIGN_TOP);
				row1.setBorder(Rectangle.BOX);
				table.addCell(row1);
				
				row1 =new PdfPCell(new Paragraph(sbSectionComment3 != null ? sbSectionComment3.toString() : " ", small));
				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
				row1.setVerticalAlignment(Element.ALIGN_TOP);
				row1.setBorder(Rectangle.BOX);
				table.addCell(row1);
				
				row1 =new PdfPCell(new Paragraph(sbSectionComment4 != null ? sbSectionComment4.toString() : " ", small));
				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
				row1.setVerticalAlignment(Element.ALIGN_TOP);
				row1.setBorder(Rectangle.BOX);
				table.addCell(row1);
				
			}
			
			
			// New Row
			row1 =new PdfPCell(new Paragraph("Grand Supervisor Inputs", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			row1.setRowspan(hmMainLevelData.size());
			row1.setBorder(Rectangle.BOX);
			table.addCell(row1);
			
			Iterator<String> it1 = hmMainLevelData.keySet().iterator();
			while(it1.hasNext()) {
				String strMainLevelId = it1.next();
//				System.out.println("strMainLevelId ===>> " + strMainLevelId);
				List<String> innerList = hmMainLevelData.get(strMainLevelId);
				// New Row
//				row1 =new PdfPCell(new Paragraph("Grand Supervisor Inputs", small));
//				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
//				row1.setVerticalAlignment(Element.ALIGN_MIDDLE);
//				row1.setBorder(Rectangle.BOX);
//				table.addCell(row1);
				
				row1 =new PdfPCell(new Paragraph(uF.showData(innerList.get(1), ""), small));
				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
				row1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				row1.setBorder(Rectangle.BOX);
				row1.setColspan(3);
				table.addCell(row1);
				
				List<String> al = new ArrayList<String>();
				if(strUserTypesForFeedback != null) {
					al = Arrays.asList(strUserTypesForFeedback.split(","));
				}
				StringBuilder sbFeedbackComment = null;
				for(int i=0; al!=null && i<al.size(); i++) {
					if(uF.parseToInt(al.get(i))>0) {
						List<List<String>> alMainLevelFeedbackCommentsData = hmMainLevelFeedbackCommentsData.get(al.get(i)+"_"+strMainLevelId);
						for(int j=0; alMainLevelFeedbackCommentsData!=null && j<alMainLevelFeedbackCommentsData.size(); j++) {
							List<String> innList = alMainLevelFeedbackCommentsData.get(j);
							if(sbFeedbackComment == null) {
								sbFeedbackComment = new StringBuilder();
								sbFeedbackComment.append(innList.get(1));
							} else {
								sbFeedbackComment.append(", "+innList.get(1));
							}
						}
					}
				}
				row1 =new PdfPCell(new Paragraph(sbFeedbackComment != null ? sbFeedbackComment.toString() : " ", small));
				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
				row1.setVerticalAlignment(Element.ALIGN_TOP);
				row1.setBorder(Rectangle.BOX);
				row1.setColspan(4);
				table.addCell(row1);
				
			}
			
			
			//	New Row
//			row1 = new PdfPCell(new Paragraph("", small));
//			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
//			row1.setBorder(Rectangle.LEFT | Rectangle.TOP | Rectangle.RIGHT);
//			row1.setColspan(8);
////			row1.setPadding(2.5f);
//			table.addCell(row1);
			
			
			
//			// New Row
//			String strelement = uF.showData("", "");
//			List<Element> al = HTMLWorker.parseToList(new StringReader(strelement), null);
//			Paragraph pr = new Paragraph("",small);
//			pr.addAll(al);
//			row1 =new PdfPCell(new Paragraph(pr));
//			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
//			row1.setBorder(Rectangle.LEFT);
//			row1.setColspan(2);
//			row1.setPadding(2.5f);
//			table.addCell(row1);
//			
//			List<Element> al2 =HTMLWorker.parseToList(new StringReader(uF.showData("", "")), null);
//			pr = new Paragraph("",small);
//			pr.addAll(al2);
//			row1 =new PdfPCell(new Paragraph(pr));
//			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
//			row1.setBorder(Rectangle.NO_BORDER);
//			row1.setColspan(2);
//			row1.setPadding(2.5f);
//			table.addCell(row1);
//
//			List<Element> al3 =HTMLWorker.parseToList(new StringReader(uF.showData("", "")), null);
//			pr = new Paragraph("",small);
//			pr.addAll(al3);
//			row1 =new PdfPCell(new Paragraph(pr));
//			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
//			row1.setBorder(Rectangle.RIGHT);
//			row1.setColspan(2);
//			row1.setPadding(2.5f);
//			table.addCell(row1);
//			
//			// New Row
//			List<Element> al4 =HTMLWorker.parseToList(new StringReader(uF.showData("", "")), null);
//			pr = new Paragraph("",small);
//			pr.addAll(al4);
//			row1 =new PdfPCell(new Paragraph(pr));
//			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
//			row1.setBorder(Rectangle.LEFT);
//			row1.setColspan(2);
//			row1.setPadding(2.5f);
//			table.addCell(row1);
//			
//			List<Element> al5 =HTMLWorker.parseToList(new StringReader(uF.showData("", "")), null);
//			pr = new Paragraph("",small);
//			pr.addAll(al5);
//			row1 =new PdfPCell(new Paragraph(pr));
//			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
//			row1.setBorder(Rectangle.NO_BORDER);
//			row1.setColspan(2);
//			row1.setPadding(2.5f);
//			table.addCell(row1);
//
//			List<Element> al6 =HTMLWorker.parseToList(new StringReader(uF.showData("", "")), null);
//			pr = new Paragraph("",small);
//			pr.addAll(al6);
//			row1 =new PdfPCell(new Paragraph(pr));
//			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
//			row1.setBorder(Rectangle.RIGHT);
//			row1.setColspan(2);
//			row1.setPadding(2.5f);
//			table.addCell(row1);
			

			 
			
//			// New Row
//			List<Element> al11 =HTMLWorker.parseToList(new StringReader(uF.showData("", "")), null);
//			pr = new Paragraph("",small);
//			pr.addAll(al11);
//			row1 =new PdfPCell(new Paragraph(pr));
//			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
////			row1.setBorder(Rectangle.LEFT);
//			row1.setColspan(6);
//			row1.setPadding(2.5f);
//			row1.setFixedHeight(0f);
//			table.addCell(row1);
			
			document.add(table);

			document.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return buffer;
	}

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	private HttpServletRequest request;

	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStrEmpId() {
		return strEmpId;
	}

	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppFreqId() {
		return appFreqId;
	}

	public void setAppFreqId(String appFreqId) {
		this.appFreqId = appFreqId;
	}

}
