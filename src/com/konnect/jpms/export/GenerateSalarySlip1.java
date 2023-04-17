package com.konnect.jpms.export;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class GenerateSalarySlip1 implements
		ServletRequestAware, ServletResponseAware, IStatements {
	
	
	 String[] arrUnitdo ={"", " One", " Two", " Three", " Four", " Five"," Six", " Seven", " Eight", " Nine", " Ten", " Eleven", " Twelve"," Thirteen", " Fourteen", " Fifteen",  " Sixteen", " Seventeen", " Eighteen", " Nineteen"};
		    String[] arrTens =  {"", "Ten", " Twenty", " Thirty", " Forty", " Fifty"," Sixty", " Seventy", " Eighty"," Ninety"};
		    String[] arrDigit = {"", " Hundred", " Thousand", " Lakh", " Crore"};
		   int nseprate;
		         
	List<Integer> salHeadId = new ArrayList<Integer>();
	List<Double> salHeadAmt = new ArrayList<Double>();
	List<String> salaryHeadName = new ArrayList<String>();
	List<String> deductionHeadName = new ArrayList<String>();
	List<Double> salHeadAmount = new ArrayList<Double>();
	List<Double> salHeadAmountGross = new ArrayList<Double>();
	List<String> alGross = new ArrayList<String>();
	List<Double> deductionHeadAmount = new ArrayList<Double>();
	List<String> empDetails = new ArrayList<String>();
	List<String> leaveName = new ArrayList<String>();  
	List<Integer> noOfLeave = new ArrayList<Integer>();
	ArrayList<String> payEmpHead = new ArrayList<String>();
	
	
	Map hmBalanceLeave = new HashMap();
	Map hmLeaveNameMap = new HashMap();

	int nYear = 0;
	Double dblTotalAmt = 0.0;
	Double dblTotalDeduction = 0.0;
	Double dblGrossTotal = 0.0;
	Double dblNetSalary = 0.0;
	String strPayMode = null;

	String strCompanyAddress = null;
	String strCompanyLogo = null;
	String strEmpImage = null;

	private static final long serialVersionUID = 1L;
	HttpSession session;
//	String strEmpID;
//	String strUserType;
//	String strSessionEmpId;
	public CommonFunctions CF = null;
	UtilityFunctions uF = new UtilityFunctions();

	
	private Font heading = new Font(Font.FontFamily.HELVETICA, 12,Font.BOLD);
	private Font normal = new Font(Font.FontFamily.HELVETICA, 9);
	private Font bold = new Font(Font.FontFamily.HELVETICA,10,Font.BOLD);
	private Font underlineEffect = new Font(Font.FontFamily.HELVETICA,10,Font.UNDERLINE); 
	
	
	public void execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return;

//		strUserType = (String) session.getAttribute(USERTYPE);
//		strSessionEmpId = (String) session.getAttribute(EMPID);

		
		if(!isAttachment()){
			setStrEmpId((String) request.getParameter("EID"));
//			setStrServiceId((String) request.getParameter("SID"));
			setStrMonth((String) request.getParameter("M"));
			setStrPC((String) request.getParameter("PC"));
			setStrFYS((String) request.getParameter("FYS"));
			setStrFYE((String) request.getParameter("FYE"));
			setStrPD((String) request.getParameter("PD"));
		}

		try {
			addContent();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
//		return SUCCESS;

	}

	
	String strEmpId;
	String strServiceId;
	String strMonth;
	String strPC;
	String strFYS;
	String strFYE;
	String strPD;
	String strPCS;
	String strPCE;
	
	boolean isAttachment;
	
	public void addContent(){
		String FILE = "/home/konnect/Desktop/created PDF/Payslip.pdf";
		Document document = new Document();
		
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		try{
			
			con = db.makeConnection(con);
			
//				PdfWriter.getInstance(document, new FileOutputStream(FILE));
				PdfWriter.getInstance(document, baos);
				document.open();
				
				Paragraph blankSpace = new Paragraph("  ");
				Paragraph firmName = new Paragraph(CF.getStrOrgName()+"\n"+CF.getStrOrgSubTitle(),heading);
				firmName.setAlignment(Element.ALIGN_CENTER);
				Paragraph title = new Paragraph("Pay-slip for the month of "+uF.getDateFormat(getStrMonth(), "MM", "MMMM")+","+uF.getDateFormat(getStrPCE(), DATE_FORMAT, "yyyy"),heading);
				title.setAlignment(Element.ALIGN_CENTER);
				
				PdfPTable personalInfoTable = new PdfPTable(6);
				personalInfoTable.setWidthPercentage(100);
				
				int[] columns = {20,20,20,20,20,20};
				personalInfoTable.setWidths(columns);
				
				List<String> personalInfo = getPersonalInfo();
				List<String> personalDetails = getPersonalDetails(con);
				
				for(int i=0;i<personalInfo.size();i++){
					PdfPCell cellDesc = new PdfPCell(new Paragraph(personalInfo.get(i),normal));
					PdfPCell cellAns = new PdfPCell(new Paragraph(personalDetails.get(i),normal));
					personalInfoTable.addCell(cellDesc);
					personalInfoTable.addCell(cellAns);
				}
				
				PdfPTable attendanceTable = new PdfPTable(5);
				attendanceTable.setWidthPercentage(100);
				PdfPCell attendance = new PdfPCell(new Paragraph("A T T E N D A N C E"));
				attendance.setColspan(5);
				attendance.setHorizontalAlignment(Element.ALIGN_CENTER);
				attendanceTable.addCell(attendance);
				
				List<String> attendanceDetails = getAttendanceDetails(con);
				for(int i=0;i<attendanceDetails.size();i++){
					PdfPCell cell = new PdfPCell(new Paragraph(attendanceDetails.get(i),normal));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					attendanceTable.addCell(cell);
				}
				
				List<String> earningDetails = getEarningDetails(con);
				List<String> earningAmounts = getEarningAmounts(con);
				List<String> deductionDetails = getDeductionDetails(con);
				List<String> deductionAmounts = getDeductionAmounts(con);
				
				PdfPTable salaryDetails = new PdfPTable(4);
				salaryDetails.setWidthPercentage(100);
				int[] columnsForSalaryTable = {30,20,30,20};
				salaryDetails.setWidths(columnsForSalaryTable);
				
				PdfPCell earning = new PdfPCell(new Paragraph("E A R N I N G S",heading));
				earning.setColspan(2);
				earning.setHorizontalAlignment(Element.ALIGN_CENTER);
				salaryDetails.addCell(earning);
							
				PdfPCell deductions = new PdfPCell(new Paragraph("D E D U C T I O N S",heading));
				deductions.setColspan(2);
				deductions.setHorizontalAlignment(Element.ALIGN_CENTER);
				salaryDetails.addCell(deductions);
				
				for(int i=0;i<earningDetails.size();i++){
					PdfPCell earningCell = new PdfPCell(new Paragraph(earningDetails.get(i),normal));
					PdfPCell earningAmtCell = new PdfPCell(new Paragraph(earningAmounts.get(i),normal));
					earningAmtCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					
					PdfPCell deductionCell = new PdfPCell();
					if(i < deductionDetails.size()){
						System.out.println("Value of I in IF"+ i);
						deductionCell = new PdfPCell(new Paragraph(deductionDetails.get(i),normal));
					}else{
						System.out.println("Value of I in ELSE"+ i);
						deductionCell = new PdfPCell(new Paragraph(" "));
					}					
					PdfPCell deductionAmtCell = new PdfPCell();
					if(i < deductionAmounts.size()){
						deductionAmtCell = new PdfPCell(new Paragraph(deductionAmounts.get(i),normal));
						deductionAmtCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					}else{
						deductionAmtCell = new PdfPCell(new Paragraph(" "));
					}
					
					salaryDetails.addCell(earningCell);
					salaryDetails.addCell(earningAmtCell);
					salaryDetails.addCell(deductionCell);
					salaryDetails.addCell(deductionAmtCell);										
				}
				
				PdfPTable ArrearsAndAdjustmentTable = new PdfPTable(4);
				ArrearsAndAdjustmentTable.setWidthPercentage(100);
				int[] columnsForArrearsTable = {20,50,20,10};
				ArrearsAndAdjustmentTable.setWidths(columnsForArrearsTable);
				
				List<String> arrearsAndAdjDetails = getArrearsAndAdjustmentDetails();
				List<String> arrearsAndAdjAmounts = getArrearsAndAdjustmentAmounts(con);
				
				PdfPCell arrearsAndAdjustmentCell = new PdfPCell(new Paragraph("A R R E A R S & A D J U S T M E N T S ",underlineEffect));
				arrearsAndAdjustmentCell.setColspan(4);
				arrearsAndAdjustmentCell.setFixedHeight((float)40.50);
				ArrearsAndAdjustmentTable.addCell(arrearsAndAdjustmentCell);
								
				for(int i=0;i<arrearsAndAdjDetails.size();i++){
					PdfPCell cellDesc = new PdfPCell(new Paragraph(arrearsAndAdjDetails.get(i),bold));
					PdfPCell cellAmt = new PdfPCell(new Paragraph(arrearsAndAdjAmounts.get(i),normal));
					ArrearsAndAdjustmentTable.addCell(cellDesc);
					ArrearsAndAdjustmentTable.addCell(cellAmt);
				}				
				
				document.add(firmName);
				document.add(title);
				document.add(blankSpace);
				document.add(personalInfoTable);
				document.add(attendanceTable);
				document.add(blankSpace);
				document.add(salaryDetails);
				document.add(ArrearsAndAdjustmentTable);
							
				document.close();
				
				
				response.setContentType("application/pdf");
				response.setContentLength(baos.size());
				response.setHeader("Content-Disposition","attachment; filename=PaySlip"+strEmpId+"_"+strMonth+".pdf");
				ServletOutputStream out = response.getOutputStream();              
				baos.writeTo(out);
				out.flush();
				out.close();
				
				
			} catch(Exception e){
				e.printStackTrace();
			} finally{
				db.closeConnection(con);
			}
			
	}
	
	public List<String> getPersonalInfo(){
		List<String> personalData = new ArrayList<String>();
		personalData.add("Employee Code");
		personalData.add("D.O.J.");
		personalData.add("PAN");
		personalData.add("Name");
		personalData.add("Division");
		personalData.add("P.F.No.");
		personalData.add("Designation");
		personalData.add("Department");
		personalData.add("Bank Name");
		personalData.add("Location");
		personalData.add(" ");
		personalData.add("Bank A/c");
		personalData.add(" ");
		personalData.add(" ");
		personalData.add("Basic Rate");		
		return personalData;		
	}
	
	public List<String> getPersonalDetails(Connection con){
		List<String> personalDetails = new ArrayList<String>();
		PreparedStatement pst = null;
		PreparedStatement pst1 = null;
		ResultSet rs = null;
		ResultSet rs1=null;
		
		try {
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			Map<String, String> hmDesig = CF.getEmpDesigMap(con);
			Map<String, String> hmDepart = CF.getDepartmentMap(con,null, null);
			
			Map hmWlocationMap = CF.getWorkLocationMap(con);
			
			
			
			
			pst1 = con.prepareStatement("select branch_id, bd.bank_name, brd.bank_city from bank_details bd, branch_details brd where brd.bank_id = bd.bank_id");
			rs1 = pst1.executeQuery();
			Map<String, String> hmBankDetails = new HashMap();
			while(rs1.next()){
				hmBankDetails.put(rs1.getString("branch_id"), rs1.getString("bank_name")+", "+rs1.getString("bank_city"));
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
						
				personalDetails.add(rs.getString("empcode"));
				personalDetails.add(uF.getDateFormat(rs.getString("joining_date"), DBDATE, CF.getStrReportDateFormat()));
				personalDetails.add(rs.getString("emp_pan_no"));
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				personalDetails.add(rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname"));
				personalDetails.add("Employee");
				personalDetails.add(rs.getString("emp_pf_no"));
				personalDetails.add(hmDesig.get(rs.getString("emp_id")));
				personalDetails.add(hmDepart.get(rs.getString("depart_id")));
				personalDetails.add(hmBankDetails.get(rs.getString("emp_bank_name")));
				personalDetails.add(hmEmpLocationMap.get("WL_CITY"));
				personalDetails.add(" ");
				personalDetails.add(rs.getString("emp_bank_acct_nbr"));
				personalDetails.add(" ");
				personalDetails.add(" ");
				personalDetails.add(uF.formatIntoTwoDecimal(dblBasic));
				
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(rs1 !=null){
				try {
					rs1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst1 !=null){
				try {
					pst1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return personalDetails;
	}
	
	public List<String> getAttendanceDetails(Connection con){
		List<String> attendanceDetails = new ArrayList<String>();
		attendanceDetails.add("TotDaysMth");
		attendanceDetails.add("Paid Days");
		attendanceDetails.add("LWP");
		attendanceDetails.add("Total");
		attendanceDetails.add("Leave Bal");
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
		
			
			pst = con.prepareStatement("select * from payroll_generation where emp_id = ? and paycycle =?");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setInt(2, uF.parseToInt(getStrPC()));
			rs = pst.executeQuery();
			
			String strStartDate = null;
			String strEndDate = null;
			String strPaidDays=null;
			String strPresentDays = null;
			String strPaidLeaves=null;
			String strTotalDays=null;
			while(rs.next()){
				strStartDate = rs.getString("paid_from");
				strEndDate = rs.getString("paid_to");
				
				strPaidDays = rs.getString("paid_days");
				strPaidLeaves = rs.getString("paid_leaves");
				strTotalDays = rs.getString("total_days");
				strPresentDays = rs.getString("present_days");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from leave_register1 lr,(select max(_date) as _date, leave_type_id, emp_id from leave_register1 where _date<= ? and emp_id=? group by leave_type_id, emp_id ) lr1 where lr1._date= lr._date and lr.emp_id = lr1.emp_id and lr.leave_type_id = lr1.leave_type_id and lr.emp_id=? and lr1.leave_type_id in (select lt.leave_type_id from emp_leave_type elt, leave_type lt where is_holiday_compensation = false and lt.leave_type_id = elt.leave_type_id)");
			pst.setDate(1, uF.getDateFormat(strEndDate, DBDATE));
			pst.setInt(2, uF.parseToInt(getStrEmpId()));
			pst.setInt(3, uF.parseToInt(getStrEmpId()));
			rs = pst.executeQuery();
			double dblbalance = 0; 
			while(rs.next()){
				dblbalance += uF.parseToDouble(rs.getString("balance")); 
			}
			rs.close();
			pst.close();
			
			
			double dblLWP = uF.parseToDouble(strTotalDays) - uF.parseToDouble(strPresentDays) - uF.parseToDouble(strPaidLeaves);
			
			attendanceDetails.add(uF.parseToInt(strTotalDays)+"");
			attendanceDetails.add(uF.formatIntoOneDecimalWithOutComma(uF.parseToDouble(strPresentDays) + uF.parseToDouble(strPaidLeaves)));
			attendanceDetails.add(uF.formatIntoOneDecimalWithOutComma(dblLWP));
			attendanceDetails.add(uF.formatIntoOneDecimalWithOutComma(uF.parseToDouble(strPresentDays) + uF.parseToDouble(strPaidLeaves)));
			attendanceDetails.add(uF.formatIntoOneDecimalWithOutComma(dblbalance));
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return attendanceDetails;
	}
	
	public List<String> getEarningDetails(Connection con){
		List<String> earningsDetails = new ArrayList<String>();
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
		
//			Map<String, String> hmSalaryHeadMap = CF.getSalaryHeadsMap(con);
			
			int nEmpLevelId = CF.getEmpLevelId(getStrEmpId(), request);
			Map<String, String> hmSalaryHeadMap = CF.getSalaryHeadsMap(con, nEmpLevelId);
			
			pst = con.prepareStatement("select *, esd.salary_head_id as salary_head_id1 from emp_salary_details esd left join payroll_generation pg on pg.salary_head_id = esd.salary_head_id and esd.earning_deduction= pg.earning_deduction and  pg.emp_id = esd.emp_id and paycycle =? and pay_date=?  where esd.effective_date= (select max(effective_date) as effective_date from emp_salary_details where effective_date<= ? and emp_id = ? and is_approved=true) and esd.emp_id = ? and esd.earning_deduction = 'E' order by esd.salary_head_id");
			pst.setInt(1, uF.parseToInt(getStrPC()));			
			pst.setDate(2, uF.getDateFormat(getStrPD(), CF.getStrReportDateFormat()));
			pst.setDate(3, uF.getDateFormat(getStrPD(), CF.getStrReportDateFormat()));
			pst.setInt(4, uF.parseToInt(getStrEmpId()));
			pst.setInt(5, uF.parseToInt(getStrEmpId()));
			rs = pst.executeQuery();
			while(rs.next()){
				earningsDetails.add(hmSalaryHeadMap.get(rs.getString("salary_head_id1")));
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return earningsDetails;
	}
	
	public List<String> getDeductionDetails(Connection con){
		List<String> deductionDetails = new ArrayList<String>();
		PreparedStatement pst = null;
		ResultSet rs =null;
		try {
		
//			Map<String, String> hmSalaryHeadMap = CF.getSalaryHeadsMap(con);
			
			int nEmpLevelId = CF.getEmpLevelId(getStrEmpId(), request);
			Map<String, String> hmSalaryHeadMap = CF.getSalaryHeadsMap(con, nEmpLevelId);
			
			pst = con.prepareStatement("select * from payroll_generation where emp_id = ? and paycycle =? and earning_deduction = 'D' order by salary_head_id");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setInt(2, uF.parseToInt(getStrPC()));
			rs = pst.executeQuery();
			while(rs.next()){
				deductionDetails.add(hmSalaryHeadMap.get(rs.getString("salary_head_id")));
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return deductionDetails;
	}
	
	public List<String> getEarningAmounts(Connection con){
		List<String> earningsAmounts = new ArrayList<String>();
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
			/*PreparedStatement pst = con.prepareStatement("select * from payroll_generation where emp_id = ? and paycycle =? and earning_deduction = 'E' order by salary_head_id");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setInt(2, uF.parseToInt(getStrPC()));
			ResultSet rs = pst.executeQuery();*/
			
			pst = con.prepareStatement("select *, pg.amount as pay_amount from emp_salary_details esd left join payroll_generation pg on pg.salary_head_id = esd.salary_head_id and esd.earning_deduction= pg.earning_deduction and  pg.emp_id = esd.emp_id and paycycle =? and pay_date=?  where esd.effective_date= (select max(effective_date) as effective_date from emp_salary_details where effective_date<= ? and emp_id = ? and is_approved=true) and esd.emp_id = ? and esd.earning_deduction = 'E' order by esd.salary_head_id");
			pst.setInt(1, uF.parseToInt(getStrPC()));			
			pst.setDate(2, uF.getDateFormat(getStrPD(), CF.getStrReportDateFormat()));
			pst.setDate(3, uF.getDateFormat(getStrPD(), CF.getStrReportDateFormat()));
			pst.setInt(4, uF.parseToInt(getStrEmpId()));
			pst.setInt(5, uF.parseToInt(getStrEmpId()));
			rs = pst.executeQuery();

			while(rs.next()){
				earningsAmounts.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("pay_amount"))));
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return earningsAmounts;
	}
	
	public List<String> getDeductionAmounts(Connection con){
		
		List<String> deductionAmounts = new ArrayList<String>();
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
			pst = con.prepareStatement("select * from payroll_generation where emp_id = ? and paycycle =? and earning_deduction = 'D' order by salary_head_id");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setInt(2, uF.parseToInt(getStrPC()));
			rs = pst.executeQuery();
			
			while(rs.next()){
				deductionAmounts.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("amount"))));
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return deductionAmounts;
	}
	
	public List<String> getArrearsAndAdjustmentDetails(){
		List<String> arrearsAndAdjustmentDet = new ArrayList<String>();
		arrearsAndAdjustmentDet.add("Gross Earnings :");
		arrearsAndAdjustmentDet.add("Gross Deductions :");
		arrearsAndAdjustmentDet.add("Rupees :");
		arrearsAndAdjustmentDet.add("Net Pay :");
		return arrearsAndAdjustmentDet;
	}
	
	public List<String> getArrearsAndAdjustmentAmounts(Connection con){
		List<String> arrearsAndAdjustmentAmt = new ArrayList<String>();
		
		PreparedStatement pst = null;
		ResultSet rs = null;	
		
		try {
			
			pst = con.prepareStatement("select sum(amount) as amount from payroll_generation where emp_id = ? and paycycle =? and earning_deduction = 'E'");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setInt(2, uF.parseToInt(getStrPC()));
			rs = pst.executeQuery();
			
			double dblGross = 0;
			double dblDeduction = 0;
			if(rs.next()){
				arrearsAndAdjustmentAmt.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("amount"))));
				dblGross = uF.parseToDouble(rs.getString("amount"));
			}else{
				arrearsAndAdjustmentAmt.add(uF.formatIntoTwoDecimal(0));
			}
			rs.close();
			pst.close();
			
			
			pst = con.prepareStatement("select sum(amount) as amount from payroll_generation where emp_id = ? and paycycle =? and earning_deduction = 'D'");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setInt(2, uF.parseToInt(getStrPC()));
			rs = pst.executeQuery();
			
			if(rs.next()){
				arrearsAndAdjustmentAmt.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("amount"))));
				dblDeduction = uF.parseToDouble(rs.getString("amount"));
			}else{
				arrearsAndAdjustmentAmt.add(uF.formatIntoTwoDecimal(0));
			}
			rs.close();
			pst.close();
			
			arrearsAndAdjustmentAmt.add(uF.digitsToWords((int)(dblGross - dblDeduction)));
			arrearsAndAdjustmentAmt.add(uF.formatIntoTwoDecimal(dblGross - dblDeduction));
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		
		
		return arrearsAndAdjustmentAmt;
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

	public String getStrPayMode() {
		return strPayMode;
	}

	public void setStrPayMode(String strPayMode) {
		this.strPayMode = strPayMode;
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

	public String getStrPCS() {
		return strPCS;
	}

	public void setStrPCS(String strPCS) {
		this.strPCS = strPCS;
	}

	public String getStrPCE() {
		return strPCE;
	}

	public void setStrPCE(String strPCE) {
		this.strPCE = strPCE;
	}
	 
}
