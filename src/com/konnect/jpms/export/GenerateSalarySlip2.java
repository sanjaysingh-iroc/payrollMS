package com.konnect.jpms.export;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class GenerateSalarySlip2 implements ServletRequestAware, ServletResponseAware, IStatements {

	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	// String strEmpID;
	// String strUserType;
	// String strSessionEmpId;
	public CommonFunctions CF = null;
	
	private static Logger log = Logger.getLogger(GenerateSalarySlip2.class);

/*	private Font heading = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
	private Font normal = new Font(Font.FontFamily.HELVETICA, 9);
	private Font bold = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
	private Font underlineEffect = new Font(Font.FontFamily.HELVETICA, 10, Font.UNDERLINE);
*/ 
	public void execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return;
		
		UtilityFunctions uF = new UtilityFunctions();

		// strUserType = (String) session.getAttribute(USERTYPE);
		// strSessionEmpId = (String) session.getAttribute(EMPID);

		if (!isAttachment()) {
			setStrEmpId((String) request.getParameter("EID"));
			// setStrServiceId((String) request.getParameter("SID"));
			setStrMonth((String) request.getParameter("M"));
			setStrPC((String) request.getParameter("PC"));
			setStrFYS((String) request.getParameter("FYS"));
			setStrFYE((String) request.getParameter("FYE"));
			setStrPD((String) request.getParameter("PD"));
		}

		try {
			Map <String, String> hmPersonalInfo = getEmpDetails(uF);
			addContent(hmPersonalInfo, uF);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		// return SUCCESS;

	}

	String strEmpId;
	String strServiceId;
	String strMonth;
	String strPC;
	String strFYS;
	String strFYE;
	String strPD;
	boolean isAttachment;

	
	
	public Map<String, String> getEmpDetails(UtilityFunctions uF){
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		Map<String, String> hmPersonalInfo = new HashMap<String, String>();
		PreparedStatement pst = null;
		ResultSet rs = null;	
		PreparedStatement pst1 = null;
		ResultSet rs1 = null;	
		try {
			
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			Map<String, String> hmDesig = CF.getDesigMap(con);
			Map<String, String> hmDepart = CF.getDepartmentMap(con,null, null);
			Map<String, Map<String, String>> hmWlocationMap = CF.getWorkLocationMap(con);
			
			
			
			
			pst1 = con.prepareStatement("select * from bank_details");
			rs1 = pst1.executeQuery();
			Map<String, String> hmBankDetails = new HashMap<String, String>();
			while(rs1.next()){
				hmBankDetails.put(rs1.getString("bank_id"), rs1.getString("bank_name"));
			}
			rs1.close();
			pst1.close();
			
			
			pst1 = con.prepareStatement("select * from payroll_generation where emp_id =? and paycycle=? and salary_head_id=?");
			pst1.setInt(1, uF.parseToInt(getStrEmpId()));
			pst1.setInt(2, uF.parseToInt(getStrPC()));
			pst1.setInt(3, BASIC);
			rs = pst1.executeQuery();
			
			double dblBasic=0;
			while(rs.next()){
				dblBasic = uF.parseToDouble(rs.getString("amount"));
			}
			rs.close();
			pst1.close();
			  
			
			pst = con.prepareStatement("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and emp_id =?");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			rs = pst.executeQuery();
			while(rs.next()){
				Map<String, String> hmEmpLocationMap = (Map)hmWlocationMap.get(rs.getString("wlocation_id"));
				if(hmEmpLocationMap==null)hmEmpLocationMap = new HashMap<String, String>();		
						
				hmPersonalInfo.put("EMPCODE",rs.getString("empcode"));
				hmPersonalInfo.put("JOINING_DATE", uF.getDateFormat(rs.getString("joining_date"), DBDATE, CF.getStrReportDateFormat()));
				hmPersonalInfo.put("PAN_NO", rs.getString("emp_pan_no"));
			
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				hmPersonalInfo.put("NAME", rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname"));
				
				hmPersonalInfo.put("PF_NO", rs.getString("emp_pf_no"));
				hmPersonalInfo.put("DESIG", hmDesig.get(rs.getString("emp_id")));
				hmPersonalInfo.put("DEPARTMENT", hmDepart.get(rs.getString("emp_id")));
				hmPersonalInfo.put("BANK_NAME", hmBankDetails.get(rs.getString("emp_bank_name")));
				hmPersonalInfo.put("WLOCATION_CITY", hmEmpLocationMap.get("WL_CITY"));
				
				hmPersonalInfo.put("BANK_ACC_NO", rs.getString("emp_bank_acct_nbr"));
				hmPersonalInfo.put("BASIC", uF.formatIntoTwoDecimal(dblBasic));
				
			}
			rs.close();
			pst.close();
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs1);
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeStatements(pst1);
			db.closeConnection(con);
		}
		return hmPersonalInfo;
		
	}
	
	
	public void addContent(Map <String, String> hmPersonalInfo, UtilityFunctions uF) {

		Document document = new Document();
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		Font heading = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
		Font normal = new Font(Font.FontFamily.HELVETICA, 9);
		Font bold = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
		Font underlineEffect = new Font(Font.FontFamily.HELVETICA, 10, Font.UNDERLINE);

		try {
			
			List<String> earningDetails = new ArrayList<String>();
			List<String> earningAmounts = new ArrayList<String>();
			List<String> deductionDetails = new ArrayList<String>();
			List<String> deductionAmounts = new ArrayList<String>();
			List<String> arrearsAndAdjustmentDetails = new ArrayList<String>();
			List<String> arrearsAndAdjustmentAmtount = new ArrayList<String>();
			Map<String, String> hmPayDaysDetails = new HashMap<String, String>();
			
			getPayrollDetails(uF, earningDetails, earningAmounts, deductionDetails, deductionAmounts, arrearsAndAdjustmentDetails, arrearsAndAdjustmentAmtount, hmPayDaysDetails);
			
			
			PdfWriter.getInstance(document, baos);
			document.open();

			
			
			Paragraph blankSpace = new Paragraph("  ");

			PdfPTable headingTable = new PdfPTable(1);
			headingTable.setWidthPercentage(100);
			PdfPCell firmName = new PdfPCell(new Paragraph(CF.getStrOrgName()+" "+CF.getStrOrgSubTitle()+"\n Pay-slip for the month of "+uF.showData(hmPayDaysDetails.get("PAY_MONTH"), "")+", "+uF.showData(hmPayDaysDetails.get("PAY_YEAR"), "-")+"\n ", heading));
			firmName.setHorizontalAlignment(Element.ALIGN_CENTER);
			headingTable.addCell(firmName);

			PdfPTable personalInfoTable = new PdfPTable(6);
			personalInfoTable.setWidthPercentage(100);

			int[] columns = { 20, 20, 20, 20, 15, 25 };
			personalInfoTable.setWidths(columns);

		

			PdfPCell empCodeDesc = new PdfPCell(new Paragraph("Employee Code", normal));
			empCodeDesc.setBorder(Rectangle.NO_BORDER);
			empCodeDesc.enableBorderSide(Rectangle.TOP);
			empCodeDesc.enableBorderSide(Rectangle.LEFT);
			personalInfoTable.addCell(empCodeDesc);

			PdfPCell empCode = new PdfPCell(new Paragraph(":  "+uF.showData(hmPersonalInfo.get("EMPCODE"), ""), normal));
			empCode.setBorder(Rectangle.NO_BORDER);
			empCode.enableBorderSide(Rectangle.TOP);
			personalInfoTable.addCell(empCode);
				
			
			PdfPCell empDOJDesc = new PdfPCell(new Paragraph("D.O.J.", normal));
			empDOJDesc.setBorder(Rectangle.NO_BORDER);
			empDOJDesc.enableBorderSide(Rectangle.TOP);
			personalInfoTable.addCell(empDOJDesc);

			PdfPCell empDOJ = new PdfPCell(new Paragraph(":  "+uF.showData(hmPersonalInfo.get("JOINING_DATE"), ""), normal));
			empDOJ.setBorder(Rectangle.NO_BORDER);
			empDOJ.enableBorderSide(Rectangle.TOP);
			personalInfoTable.addCell(empDOJ);

			PdfPCell empPANDesc = new PdfPCell(new Paragraph("PAN", normal));
			empPANDesc.setBorder(Rectangle.NO_BORDER);
			empPANDesc.enableBorderSide(Rectangle.TOP);
			personalInfoTable.addCell(empPANDesc);

			PdfPCell empPAN = new PdfPCell(new Paragraph(":  "+uF.showData(hmPersonalInfo.get("PAN_NO"), ""), normal));
			empPAN.setBorder(Rectangle.NO_BORDER);
			empPAN.enableBorderSide(Rectangle.TOP);
			empPAN.enableBorderSide(Rectangle.RIGHT);
			personalInfoTable.addCell(empPAN);

			// ---------------------------
			PdfPCell empNameDesc = new PdfPCell(new Paragraph("Name", normal));
			empNameDesc.setBorder(Rectangle.NO_BORDER);
			empNameDesc.enableBorderSide(Rectangle.LEFT);
			personalInfoTable.addCell(empNameDesc);

			PdfPCell empName = new PdfPCell(new Paragraph(":  "+uF.showData(hmPersonalInfo.get("NAME"), ""), normal));
			empName.setBorder(Rectangle.NO_BORDER);
			personalInfoTable.addCell(empName);

			PdfPCell empDivisionDesc = new PdfPCell(new Paragraph("Division", normal));
			empDivisionDesc.setBorder(Rectangle.NO_BORDER);
			personalInfoTable.addCell(empDivisionDesc);

			PdfPCell empDivision = new PdfPCell(new Paragraph(":  "+uF.showData(hmPersonalInfo.get(""), ""), normal));
			empDivision.setBorder(Rectangle.NO_BORDER);
			personalInfoTable.addCell(empDivision);

			PdfPCell empPFNODesc = new PdfPCell(new Paragraph("P.F.No.", normal));
			empPFNODesc.setBorder(Rectangle.NO_BORDER);
			personalInfoTable.addCell(empPFNODesc);

			PdfPCell empPFNo = new PdfPCell(new Paragraph(":  "+uF.showData(hmPersonalInfo.get("PF_NO"), ""), normal));
			empPFNo.setBorder(Rectangle.NO_BORDER);
			empPFNo.enableBorderSide(Rectangle.RIGHT);
			personalInfoTable.addCell(empPFNo);

			// -----------------------

			PdfPCell empDesignationDesc = new PdfPCell(new Paragraph("Designation", normal));
			empDesignationDesc.setBorder(Rectangle.NO_BORDER);
			empDesignationDesc.enableBorderSide(Rectangle.LEFT);
			personalInfoTable.addCell(empDesignationDesc);

			PdfPCell empDesignation = new PdfPCell(new Paragraph(":  "+uF.showData(hmPersonalInfo.get("DESIG"), ""), normal));
			empDesignation.setBorder(Rectangle.NO_BORDER);
			personalInfoTable.addCell(empDesignation);

			PdfPCell empDeptDesc = new PdfPCell(new Paragraph("Department", normal));
			empDeptDesc.setBorder(Rectangle.NO_BORDER);
			personalInfoTable.addCell(empDeptDesc);

			PdfPCell empDepartment = new PdfPCell(new Paragraph(":  "+uF.showData(hmPersonalInfo.get("DEPARTMENT"), ""), normal));
			empDepartment.setBorder(Rectangle.NO_BORDER);
			personalInfoTable.addCell(empDepartment);

			PdfPCell empBankDesc = new PdfPCell(new Paragraph("Bank Name", normal));
			empBankDesc.setBorder(Rectangle.NO_BORDER);
			personalInfoTable.addCell(empBankDesc);

			PdfPCell empBankName = new PdfPCell(new Paragraph(":  "+uF.showData(hmPersonalInfo.get("BANK_NAME"), ""), normal));
			empBankName.setBorder(Rectangle.NO_BORDER);
			empBankName.enableBorderSide(Rectangle.RIGHT);
			personalInfoTable.addCell(empBankName);

			// ------------------------
			
			PdfPCell empLocationDesc = new PdfPCell(new Paragraph("Location", normal));
			empLocationDesc.setBorder(Rectangle.NO_BORDER);
			empLocationDesc.enableBorderSide(Rectangle.LEFT);
			personalInfoTable.addCell(empLocationDesc);

			PdfPCell empLocation = new PdfPCell(new Paragraph(":  "+uF.showData(hmPersonalInfo.get("WLOCATION_CITY"), ""), normal));
			empLocation.setBorder(Rectangle.NO_BORDER);
			personalInfoTable.addCell(empLocation);

			PdfPCell empblank1Desc = new PdfPCell(new Paragraph(" ", normal));
			empblank1Desc.setBorder(Rectangle.NO_BORDER);
			personalInfoTable.addCell(empblank1Desc);

			PdfPCell empblank2 = new PdfPCell(new Paragraph("  ", normal));
			empblank2.setBorder(Rectangle.NO_BORDER);
			personalInfoTable.addCell(empblank2);

			PdfPCell empBankACDesc = new PdfPCell(new Paragraph("Bank A/c", normal));
			empBankACDesc.setBorder(Rectangle.NO_BORDER);
			personalInfoTable.addCell(empBankACDesc);

			PdfPCell empBankAC = new PdfPCell(new Paragraph(":  "+uF.showData(hmPersonalInfo.get("BANK_ACC_NO"), ""), normal));
			empBankAC.setBorder(Rectangle.NO_BORDER);
			empBankAC.enableBorderSide(Rectangle.RIGHT);
			personalInfoTable.addCell(empBankAC);

			// -----------------

			PdfPCell empBlank3Desc = new PdfPCell(new Paragraph(" ", normal));
			empBlank3Desc.setBorder(Rectangle.NO_BORDER);
			empBlank3Desc.enableBorderSide(Rectangle.LEFT);
			empBlank3Desc.enableBorderSide(Rectangle.BOTTOM);
			personalInfoTable.addCell(empBlank3Desc);

			PdfPCell empBlank3 = new PdfPCell(new Paragraph("  ", normal));
			empBlank3.setBorder(Rectangle.NO_BORDER);
			empBlank3.enableBorderSide(Rectangle.BOTTOM);
			personalInfoTable.addCell(empBlank3);

			PdfPCell empBlank4Desc = new PdfPCell(new Paragraph(" ", normal));
			empBlank4Desc.setBorder(Rectangle.NO_BORDER);
			empBlank4Desc.enableBorderSide(Rectangle.BOTTOM);
			personalInfoTable.addCell(empBlank4Desc);

			PdfPCell empBlank4 = new PdfPCell(new Paragraph("   ", normal));
			empBlank4.setBorder(Rectangle.NO_BORDER);
			empBlank4.enableBorderSide(Rectangle.BOTTOM);
			personalInfoTable.addCell(empBlank4);

			PdfPCell empBasicRateDesc = new PdfPCell(new Paragraph("Basic Rate", normal));
			empBasicRateDesc.setBorder(Rectangle.NO_BORDER);
			empBasicRateDesc.enableBorderSide(Rectangle.BOTTOM);
			personalInfoTable.addCell(empBasicRateDesc);

			PdfPCell empBasicRate = new PdfPCell(new Paragraph(":  "+uF.showData(hmPersonalInfo.get("BASIC"), ""), normal));
			empBasicRate.setBorder(Rectangle.NO_BORDER);
			empBasicRate.enableBorderSide(Rectangle.RIGHT);
			empBasicRate.enableBorderSide(Rectangle.BOTTOM);
			personalInfoTable.addCell(empBasicRate);

			PdfPTable attendanceTable = new PdfPTable(6);
			attendanceTable.setWidthPercentage(100);

			int[] attendanceTablecolumns = { 15, 15, 25, 15, 15, 15 };
			attendanceTable.setWidths(attendanceTablecolumns);

			PdfPCell attendance = new PdfPCell(new Paragraph("A T T E N D A N C E"));
			attendance.setColspan(6);
			attendance.setHorizontalAlignment(Element.ALIGN_CENTER);
			attendanceTable.addCell(attendance);

			// Heads of attendance details
			PdfPCell totalDayMthDesc = new PdfPCell(new Paragraph("TotdaysMth", normal));
			totalDayMthDesc.setBorder(Rectangle.NO_BORDER);
			totalDayMthDesc.enableBorderSide(Rectangle.LEFT);
			totalDayMthDesc.enableBorderSide(Rectangle.TOP);
			totalDayMthDesc.setHorizontalAlignment(Element.ALIGN_CENTER);
			attendanceTable.addCell(totalDayMthDesc);

			PdfPCell totalPaidDayshDesc = new PdfPCell(new Paragraph("Paid Days", normal));
			totalPaidDayshDesc.setBorder(Rectangle.NO_BORDER);
			totalPaidDayshDesc.enableBorderSide(Rectangle.TOP);
			totalPaidDayshDesc.setHorizontalAlignment(Element.ALIGN_CENTER);
			attendanceTable.addCell(totalPaidDayshDesc);

			PdfPCell totalBlankDesc = new PdfPCell(new Paragraph(" ", normal));
			totalBlankDesc.setBorder(Rectangle.NO_BORDER);
			totalBlankDesc.enableBorderSide(Rectangle.TOP);
			totalBlankDesc.setHorizontalAlignment(Element.ALIGN_CENTER);
			attendanceTable.addCell(totalBlankDesc);

			PdfPCell lwpDesc = new PdfPCell(new Paragraph("LWP", normal));
			lwpDesc.setBorder(Rectangle.NO_BORDER);
			lwpDesc.enableBorderSide(Rectangle.TOP);
			lwpDesc.setHorizontalAlignment(Element.ALIGN_CENTER);
			attendanceTable.addCell(lwpDesc);

			PdfPCell totalDesc = new PdfPCell(new Paragraph("Total", normal));
			totalDesc.setBorder(Rectangle.NO_BORDER);
			totalDesc.enableBorderSide(Rectangle.TOP);
			totalDesc.setHorizontalAlignment(Element.ALIGN_CENTER);
			attendanceTable.addCell(totalDesc);

			PdfPCell totalLeaveBalanceDesc = new PdfPCell(new Paragraph("Leave Bal", normal));
			totalLeaveBalanceDesc.setBorder(Rectangle.NO_BORDER);
			totalLeaveBalanceDesc.enableBorderSide(Rectangle.TOP);
			totalLeaveBalanceDesc.enableBorderSide(Rectangle.RIGHT);
			totalLeaveBalanceDesc.setHorizontalAlignment(Element.ALIGN_CENTER);
			attendanceTable.addCell(totalLeaveBalanceDesc);

			
			double dblTotalDays = uF.parseToDouble(hmPayDaysDetails.get("TOTAL_DAYS"));
			double dblPaidLeaves =  + uF.parseToDouble(hmPayDaysDetails.get("PAID_LEAVES"));
			double dblPresentDays =  uF.parseToDouble(hmPayDaysDetails.get("PRESENT_DAYS"));
			double dblPaidDays =  uF.parseToDouble(hmPayDaysDetails.get("PAID_DAYS"));
			double dblBalanceDays =  uF.parseToDouble(hmPayDaysDetails.get("BALANCE"));
			double dblLWP = dblTotalDays - dblPaidDays;
			
			
			
			
			// answer or Actual Values of attendance details table
			PdfPCell totalDayMth = new PdfPCell(new Paragraph(dblTotalDays+"", normal));
			totalDayMth.setBorder(Rectangle.NO_BORDER);
			totalDayMth.enableBorderSide(Rectangle.LEFT);
			totalDayMth.enableBorderSide(Rectangle.BOTTOM);
			totalDayMth.setHorizontalAlignment(Element.ALIGN_CENTER);
			attendanceTable.addCell(totalDayMth);

			PdfPCell totalPaidDays = new PdfPCell(new Paragraph(dblTotalDays+"", normal));
			totalPaidDays.setBorder(Rectangle.NO_BORDER);
			totalPaidDays.enableBorderSide(Rectangle.BOTTOM);
			totalPaidDays.setHorizontalAlignment(Element.ALIGN_CENTER);
			attendanceTable.addCell(totalPaidDays);

			PdfPCell totalBlank = new PdfPCell(new Paragraph(" ", normal));
			totalBlank.setBorder(Rectangle.NO_BORDER);
			totalBlank.enableBorderSide(Rectangle.BOTTOM);
			attendanceTable.addCell(totalBlank);

			
			PdfPCell lwp = new PdfPCell(new Paragraph(uF.formatIntoTwoDecimal(dblLWP), normal));
			lwp.setBorder(Rectangle.NO_BORDER);
			lwp.enableBorderSide(Rectangle.BOTTOM);
			lwp.setHorizontalAlignment(Element.ALIGN_CENTER);
			attendanceTable.addCell(lwp);

			PdfPCell total = new PdfPCell(new Paragraph(uF.formatIntoTwoDecimal(dblPaidDays), normal));
			total.setBorder(Rectangle.NO_BORDER);
			total.enableBorderSide(Rectangle.BOTTOM);
			total.setHorizontalAlignment(Element.ALIGN_CENTER);
			attendanceTable.addCell(total);

			PdfPCell totalLeaveBalance = new PdfPCell(new Paragraph(uF.formatIntoTwoDecimal(dblBalanceDays), normal));
			totalLeaveBalance.setBorder(Rectangle.NO_BORDER);
			totalLeaveBalance.enableBorderSide(Rectangle.BOTTOM);
			totalLeaveBalance.enableBorderSide(Rectangle.RIGHT);
			totalLeaveBalance.setHorizontalAlignment(Element.ALIGN_CENTER);
			attendanceTable.addCell(totalLeaveBalance);

			

			PdfPTable salaryDetails = new PdfPTable(4);
			salaryDetails.setWidthPercentage(100);
			int[] columnsForSalaryTable = { 30, 20, 30, 20 };
			salaryDetails.setWidths(columnsForSalaryTable);

			PdfPCell earning = new PdfPCell(new Paragraph("E A R N I N G S", heading));
			earning.setColspan(2);
			earning.setHorizontalAlignment(Element.ALIGN_CENTER);
			salaryDetails.addCell(earning);

			PdfPCell deductions = new PdfPCell(new Paragraph("D E D U C T I O N S", heading));
			deductions.setColspan(2);
			deductions.setHorizontalAlignment(Element.ALIGN_CENTER);
			salaryDetails.addCell(deductions);

			for (int i = 0; i < earningDetails.size(); i++) {
				PdfPCell earningCell = new PdfPCell(new Paragraph(earningDetails.get(i), normal));
				earningCell.setBorder(Rectangle.NO_BORDER);
				earningCell.enableBorderSide(Rectangle.LEFT);

				PdfPCell earningAmtCell = new PdfPCell(new Paragraph(earningAmounts.get(i), normal));
				earningAmtCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				earningAmtCell.setBorder(Rectangle.NO_BORDER);

				PdfPCell deductionCell = new PdfPCell();

				if (i < deductionDetails.size()) {
					deductionCell = new PdfPCell(new Paragraph(deductionDetails.get(i), normal));
					deductionCell.setBorder(Rectangle.NO_BORDER);
				} else {
					deductionCell = new PdfPCell(new Paragraph(" "));
					deductionCell.setBorder(Rectangle.NO_BORDER);
				}

				PdfPCell deductionAmtCell = new PdfPCell();
				if (i < deductionAmounts.size()) {
					deductionAmtCell = new PdfPCell(new Paragraph(deductionAmounts.get(i), normal));
					deductionAmtCell.setBorder(Rectangle.NO_BORDER);
					deductionAmtCell.enableBorderSide(Rectangle.RIGHT);
					deductionAmtCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				} else {
					deductionAmtCell = new PdfPCell(new Paragraph(" "));
					deductionAmtCell.setBorder(Rectangle.NO_BORDER);
					deductionAmtCell.enableBorderSide(Rectangle.RIGHT);
				}

				salaryDetails.addCell(earningCell);
				salaryDetails.addCell(earningAmtCell);
				salaryDetails.addCell(deductionCell);
				salaryDetails.addCell(deductionAmtCell);
			}

			PdfPTable ArrearsAndAdjustmentTable = new PdfPTable(4);
			ArrearsAndAdjustmentTable.setWidthPercentage(100);
			int[] columnsForArrearsTable = { 20, 50, 20, 10 };
			ArrearsAndAdjustmentTable.setWidths(columnsForArrearsTable);

			/*List<String> arrearsAndAdjDetails = getArrearsAndAdjustmentDetails();
			List<String> arrearsAndAdjAmounts = getArrearsAndAdjustmentAmounts();*/

			PdfPCell arrearsAndAdjustmentCell = new PdfPCell(new Paragraph("\n A R R E A R S & A D J U S T M E N T S ", underlineEffect));
			arrearsAndAdjustmentCell.setColspan(4);
			arrearsAndAdjustmentCell.setFixedHeight((float) 45.50);
			ArrearsAndAdjustmentTable.addCell(arrearsAndAdjustmentCell);

			for (int i = 0; i < arrearsAndAdjustmentDetails.size(); i++) {
				PdfPCell cellDesc = new PdfPCell(new Paragraph(arrearsAndAdjustmentDetails.get(i), bold));
				PdfPCell cellAmt = new PdfPCell(new Paragraph(arrearsAndAdjustmentAmtount.get(i), normal));
				ArrearsAndAdjustmentTable.addCell(cellDesc);
				ArrearsAndAdjustmentTable.addCell(cellAmt);
			}

			String powerByFilePath = request.getRealPath("/images1/powerdby.png");
//			Image powerBy = Image.getInstance("/home/konnect/Desktop/Logos/powerdby.png");
			Image powerBy = Image.getInstance(powerByFilePath);
//			powerBy.setAbsolutePosition(400f, 100f);
			powerBy.setAbsolutePosition(500, 0);
			powerBy.scaleToFit(100, 30);

			document.add(headingTable);
			document.add(personalInfoTable);
			document.add(attendanceTable);
			document.add(blankSpace);
			document.add(salaryDetails);
			document.add(ArrearsAndAdjustmentTable);
			document.add(powerBy);
			document.close();
			
			
			response.setContentType("application/pdf");
			response.setContentLength(baos.size());
			response.setHeader("Content-Disposition","attachment; filename=PaySlip"+strEmpId+"_"+strMonth+".pdf");
			ServletOutputStream out = response.getOutputStream();              
			baos.writeTo(out);
			out.flush();
			out.close();
			baos.close();
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	public void getPayrollDetails(UtilityFunctions uF, List<String> alEarningDetails, List<String> alEearningAmounts, List<String> alDeductionDetails, List<String> alDeductionAmounts, List<String> arrearsAndAdjustmentDetails, List<String> arrearsAndAdjustmentAmtount, Map<String, String> hmPayDaysDetails){
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;	
		try {
			con = db.makeConnection(con);
			
			
			double dblGrossAmount = 0;
			double dblNetAmount = 0;  
			double dblEarningAmount = 0;
			double dblDeductionAmount = 0;
			
//			Map<String, String> hmSalaryHeadMap = CF.getSalaryHeadsMap(con);
			int nEmpLevelId = CF.getEmpLevelId(getStrEmpId(), request);
			Map<String, String> hmSalaryHeadMap = CF.getSalaryHeadsMap(con, nEmpLevelId);
			
			pst = con.prepareStatement("select * from payroll_generation where emp_id =? and paycycle =? and pay_date=? ");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setInt(2, uF.parseToInt(getStrPC()));			
			pst.setDate(3, uF.getDateFormat(getStrPD(), CF.getStrReportDateFormat()));
			rs = pst.executeQuery();
			while(rs.next()){
				hmPayDaysDetails.put("TOTAL_DAYS", uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("total_days"))));
				hmPayDaysDetails.put("PAID_LEAVES", rs.getString("paid_leaves"));
				hmPayDaysDetails.put("PRESENT_DAYS", rs.getString("present_days"));
				hmPayDaysDetails.put("PAID_DAYS", rs.getString("paid_days"));
				hmPayDaysDetails.put("PAY_MONTH", uF.getDateFormat(rs.getString("month"), "MM", "MMMM"));
				hmPayDaysDetails.put("PAY_YEAR", rs.getString("year"));
			}
			rs.close();
			pst.close();
			
			
			
			pst = con.prepareStatement("select sum( (leave_carryforward - monthly_leaves + accrued_leaves)) as balance from leave_register where emp_id = ? and from_date<= ? and to_date>= ?");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(2, uF.getDateFormat(getStrPD(), CF.getStrReportDateFormat()));
			pst.setDate(3, uF.getDateFormat(getStrPD(), CF.getStrReportDateFormat()));
			rs = pst.executeQuery();
			while(rs.next()){
				hmPayDaysDetails.put("BALANCE", rs.getString("balance"));
			}
			rs.close();
			pst.close();
			
			
			pst = con.prepareStatement("select *, esd.salary_head_id as salary_head_id1, pg.amount as pay_amount from emp_salary_details esd left join payroll_generation pg on pg.salary_head_id = esd.salary_head_id and esd.earning_deduction= pg.earning_deduction and  pg.emp_id = esd.emp_id and paycycle =? and pay_date=?  where esd.effective_date= (select max(effective_date) as effective_date from emp_salary_details where effective_date<= ? and emp_id = ? and is_approved=true) and esd.emp_id = ? and esd.earning_deduction = 'E' order by esd.salary_head_id");
			pst.setInt(1, uF.parseToInt(getStrPC()));			
			pst.setDate(2, uF.getDateFormat(getStrPD(), CF.getStrReportDateFormat()));
			pst.setDate(3, uF.getDateFormat(getStrPD(), CF.getStrReportDateFormat()));
			pst.setInt(4, uF.parseToInt(getStrEmpId()));
			pst.setInt(5, uF.parseToInt(getStrEmpId()));
			rs = pst.executeQuery();
			
			while(rs.next()){
				alEarningDetails.add(hmSalaryHeadMap.get(rs.getString("salary_head_id1")));
				alEearningAmounts.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("pay_amount"))));
				
				dblEarningAmount += uF.parseToDouble(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("pay_amount"))));
			}
			rs.close();
			pst.close();
			
			
			
			pst = con.prepareStatement("select *, esd.salary_head_id as salary_head_id1, pg.amount as pay_amount from emp_salary_details esd left join payroll_generation pg on pg.salary_head_id = esd.salary_head_id and esd.earning_deduction= pg.earning_deduction and  pg.emp_id = esd.emp_id and paycycle =? and pay_date=?  where esd.effective_date= (select max(effective_date) as effective_date from emp_salary_details where effective_date<= ? and emp_id = ? and is_approved=true) and esd.emp_id = ? and esd.earning_deduction = 'D' order by esd.salary_head_id");
			pst.setInt(1, uF.parseToInt(getStrPC()));			
			pst.setDate(2, uF.getDateFormat(getStrPD(), CF.getStrReportDateFormat()));
			pst.setDate(3, uF.getDateFormat(getStrPD(), CF.getStrReportDateFormat()));
			pst.setInt(4, uF.parseToInt(getStrEmpId()));
			pst.setInt(5, uF.parseToInt(getStrEmpId()));
			rs = pst.executeQuery();
			
			while(rs.next()){
				alDeductionDetails.add(hmSalaryHeadMap.get(rs.getString("salary_head_id1")));
				alDeductionAmounts.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("pay_amount"))));
				
				dblDeductionAmount += uF.parseToDouble(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("pay_amount"))));
			}
			rs.close();
			pst.close();
			
			
			
			
			dblGrossAmount = dblEarningAmount;
			dblNetAmount = dblGrossAmount - dblDeductionAmount;
			
			arrearsAndAdjustmentDetails.add("Gross Earnings :");
			arrearsAndAdjustmentDetails.add("Gross Deductions :");
			arrearsAndAdjustmentDetails.add("Rupees :");
			arrearsAndAdjustmentDetails.add("Net Pay :");
			
			
			arrearsAndAdjustmentAmtount.add(uF.formatIntoTwoDecimal(dblGrossAmount));
			arrearsAndAdjustmentAmtount.add(uF.formatIntoTwoDecimal(dblDeductionAmount));
			arrearsAndAdjustmentAmtount.add(uF.digitsToWords((int)dblNetAmount)+((dblNetAmount>0?" Only":"Zero Only")));
			arrearsAndAdjustmentAmtount.add(uF.formatIntoTwoDecimal(dblNetAmount));
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	HttpServletResponse response;
	HttpServletRequest request;

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;

	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getStrEmpId() {
		return strEmpId;
	}

	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}

	public String getStrMonth() {
		return strMonth;
	}

	public void setStrMonth(String strMonth) {
		this.strMonth = strMonth;
	}

	public String getStrPC() {
		return strPC;
	}

	public void setStrPC(String strPC) {
		this.strPC = strPC;
	}

	public String getStrFYS() {
		return strFYS;
	}

	public void setStrFYS(String strFYS) {
		this.strFYS = strFYS;
	}

	public String getStrFYE() {
		return strFYE;
	}

	public void setStrFYE(String strFYE) {
		this.strFYE = strFYE;
	}

	public boolean isAttachment() {
		return isAttachment;
	}

	public void setAttachment(boolean isAttachment) {
		this.isAttachment = isAttachment;
	}

	public String getStrPD() {
		return strPD;
	}

	public void setStrPD(String strPD) {
		this.strPD = strPD;
	}

}
