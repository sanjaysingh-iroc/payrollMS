package com.konnect.jpms.offboarding;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.SessionAware;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.Font.FontStyle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.konnect.jpms.document.HeaderFooterPageEvent;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
public class ExportPDF implements SessionAware, ServletResponseAware, ServletRequestAware, IStatements {

	HttpServletResponse response;
	String emp_id;
	HttpSession session;
	String strSessionUserType;
	CommonFunctions CF;
	HttpServletRequest request;

	public String getEmp_id() {
		return emp_id;
	}
	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}
	public int getResignId() {
		return resignId;
	}
	public void setResignId(int resignId) {
		this.resignId = resignId;
	}

	int resignId;

	public void execute() {
		session = request.getSession();
		strSessionUserType = (String) session.getAttribute(USERTYPE);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);

		OffboardSalaryPreview offboardSalaryPreview = new OffboardSalaryPreview();
		offboardSalaryPreview.setEmp_id(getEmp_id());
		offboardSalaryPreview.setResignId(getResignId());
		offboardSalaryPreview.setServletRequest(request);
		offboardSalaryPreview.session = session;
		offboardSalaryPreview.CF = CF;
		offboardSalaryPreview.strSessionUserType = strSessionUserType;
		// strSessionUserType
		offboardSalaryPreview.fullSalaryPreview(getEmp_id(), CF);
		addContent();
	}

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
		// TODO Auto-generated method stub

	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
		// TODO Auto-generated method stub

	}

	private Font normal = new Font(Font.FontFamily.TIMES_ROMAN, 10);
//	private Font bold = new Font(Font.FontFamily.TIMES_ROMAN, 10).setStyle(FontStyle.BOLD);
//	(Font.FontFamily.TIMES_ROMAN, 10,Font.FontStyle.BOLD)
	public void addContent() {
		//Started By Dattatray Date:09-12-21
		PreparedStatement pst = null;
		Database dB = new Database();
		dB.setRequest(request);
		ResultSet rs = null;
		Connection con = null;
		con = dB.makeConnection(con);
		UtilityFunctions uF = new UtilityFunctions();
		//Ended By Dattatray Date:09-12-21
		Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(request);//Created By Dattatray Date:09-12-21
		
		Map<String, String> empMap = (Map<String, String>) request.getAttribute("empDetailsMp");
//		System.out.println("empMap : "+empMap);
		Map<String, String> hmEmpProfile = (Map<String, String>) request.getAttribute("hmEmpProfile");
//		System.out.println("hmEmpProfile : "+hmEmpProfile);
		
		Map<String, String> hmOrgDetails = CF.getOrgDetails(uF, hmEmpProfile.get("ORG_ID"), request);//Created By Dattatray Date:09-12-21
		Map<String, String> hmCurrencyDetails = CF.getCurrencyDetailsById(con, uF, hmOrgDetails.get("ORG_CURRENCY"));//Created By Dattatray Date:09-12-21
		
		String probationRemaining = (String) request.getAttribute("PROBATION_REMAINING");
		String noticePeriod = (String) request.getAttribute("NOTICE_PERIOD");

		request.getAttribute("Months");
		request.getAttribute("totalWorkingDays");
		request.getAttribute("reason");
		Map<String, String> hmSalaryDetails = (Map<String, String>) request.getAttribute("hmSalaryDetails");
		List<String> alEmpSalaryDetailsEarning = (List<String>) request.getAttribute("alEmpSalaryDetailsEarning");
		List<String> alEmpSalaryDetailsDeduction = (List<String>) request.getAttribute("alEmpSalaryDetailsDeduction");

		List<String> alEarningSalaryDuplicationTracer = (List<String>) request.getAttribute("alEarningSalaryDuplicationTracer");
		List<String> alDeductionSalaryDuplicationTracer = (List<String>) request.getAttribute("alDeductionSalaryDuplicationTracer");

		Map<String, Double> hmSalaryAmt = (Map<String, Double>) request.getAttribute("hmSalaryAmt");
		
		double earningTotal = 0.0;
		double deductionTotal = 0.0;

		Document document = new Document();
		Font title = new Font(Font.FontFamily.TIMES_ROMAN, 12); // Font.FontFamily.TIMES_ROMAN,
																// 12,Font.ITALIC
		// title.setFamily(Font.FontFamily.TIMES_ROMAN);
		title.setSize(12);
		title.setStyle(Font.ITALIC);
		title.setStyle(Font.BOLD);
		
		// Started By Dattatray Date:09-12-21
		Font bold = new Font(Font.FontFamily.TIMES_ROMAN, 10); 
		bold.setStyle(Font.BOLD);
		// Ended By Dattatray Date:09-12-21
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		try {
			//Started By Dattatray Date:09-12-21
			Map<String, String> hmOrganisationDetails = new HashMap<String, String>();
			pst = con.prepareStatement("select * from org_details where org_id = ?");
			pst.setInt(1, uF.parseToInt(hmEmpProfile.get("ORG_ID")));
			rs = pst.executeQuery();
			while(rs.next()){
				hmOrganisationDetails.put("ORG_ID", rs.getString("org_id"));
				hmOrganisationDetails.put("ORG_LOGO", rs.getString("org_logo"));
				hmOrganisationDetails.put("ORG_NAME", rs.getString("org_name"));
				hmOrganisationDetails.put("PDF_TYPE", rs.getString("payslip_format"));
			}
			rs.close();
			pst.close();
			
			System.out.println("hmOrganisationDetails : "+hmOrganisationDetails);
			String strCompanyLogo=hmOrganisationDetails.get("ORG_LOGO");
			String strCompanyName=hmOrganisationDetails.get("ORG_NAME");
			String orgId =hmOrganisationDetails.get("ORG_ID");

			String filePath = null;
			String filePathCompanyLOgo = null;
			String filePathCompanyLOgodefault=null;
			String filePathproductLogo= null;
			
			if(CF.getStrDocSaveLocation()!=null){
				System.out.println("if");
				filePathCompanyLOgo = CF.getStrDocSaveLocation()+I_ORGANISATION+"/"+I_IMAGE+"/"+strCompanyLogo;
				filePathCompanyLOgodefault=request.getRealPath("/userImages/Konnect_technologies.png");
				filePathproductLogo= request.getRealPath("/userImages/logo_new.png");
			}
			else{
				System.out.println("else");
				filePathCompanyLOgo = request.getRealPath("/userImages/"+strCompanyLogo+"");
				filePathCompanyLOgodefault=request.getRealPath("/userImages/Konnect_technologies.png");
				filePathproductLogo= request.getRealPath("/userImages/logo_new.png");
			}  
			Image imageLogo=null;
			
			try{
				
			System.out.println("filePathCompanyLOgo : "+filePathCompanyLOgo);
				FileInputStream fileInputStream1=null;
		        File file1 = new File(filePathCompanyLOgo);
		        byte[] bFile1 = new byte[(int) file1.length()];
		        fileInputStream1 = new FileInputStream(file1);
			    fileInputStream1.read(bFile1);
			    fileInputStream1.close();
		        imageLogo = Image.getInstance(bFile1);
		        
			}catch(FileNotFoundException e){
				
				imageLogo = Image.getInstance(filePathCompanyLOgodefault);
				e.printStackTrace();
			}
			
			//Ended By Dattatray Date:09-12-21
			
			
			 PdfWriter.getInstance(document, buffer);
			document.open();

			Paragraph blankSpace = new Paragraph(" ");

			//Started By Dattatray Date:09-12-21
			PdfPTable header = new PdfPTable(2);
			header.setWidths(new int[]{70, 15});
            header.setWidthPercentage(100);
          
//            Paragraph sheetTitle = new Paragraph("Full & Final Settlement Sheet/Clearance Slip", title);
//			sheetTitle.setAlignment(Element.ALIGN_CENTER);
            
            PdfPCell texth = new PdfPCell(new Paragraph("Full & Final Settlement Sheet Clearance Slip", title));
            texth.setHorizontalAlignment(Element.ALIGN_CENTER);
            texth.setBorder(Rectangle.NO_BORDER);
            texth.setFixedHeight(40);
            header.addCell(texth);
            
           
            
            PdfPCell text1 = new PdfPCell();
            text1.setBorder(Rectangle.NO_BORDER);
            text1.setFixedHeight(40);
            text1.addElement(imageLogo);
            header.addCell(text1);

          // Ended By Dattatray Date:09-12-21
            
			PdfPTable personalInfoTable = new PdfPTable(4);
			personalInfoTable.setWidthPercentage(100);
			int[] colWidhts = {30, 25, 30, 25};//Created By Dattatray Date:09-12-21
			personalInfoTable.setWidths(colWidhts);

			/*
			 * List<String> hedingValue=new ArrayList<String>();
			 * hedingValue.add("EMP_FNAME"); hedingValue.add("WLOCATION_NAME");
			 * hedingValue.add("EMP_CODE"); hedingValue.add("DESIGNATION_NAME");
			 * hedingValue.add("DEPART_NAME"); hedingValue.add("JOINING_DATE");
			 * hedingValue.add("ENTRY_DATE"); hedingValue.add("MONTHS");
			 * hedingValue.add("LAST_WORKING_DATE");
			 * hedingValue.add("TOTALWORKINGDAYS");
			 * hedingValue.add("NOTICE_PERIOD");
			 * hedingValue.add("TOTALWORKINGDAYS"); hedingValue.add("REASON");
			 * 
			 * List<String> personalHeading = getPersonalHeadings();
			 * System.out.println(personalHeading);
			 * 
			 * for(int j=0;j<personalHeading.size();j++){
			 * if(j==personalHeading.size()-1){ PdfPCell cell=new PdfPCell(new
			 * Paragraph(personalHeading.get(j),normal));
			 * personalInfoTable.addCell(cell);
			 * 
			 * cell = new PdfPCell(new
			 * Paragraph(empMap.get(hedingValue.get(j)),normal));
			 * cell.setColspan(4); personalInfoTable.addCell(cell);
			 * 
			 * }else{ PdfPCell cell=new PdfPCell(new
			 * Paragraph(personalHeading.get(j),normal));
			 * personalInfoTable.addCell(cell);
			 * 
			 * cell = new PdfPCell(new
			 * Paragraph(empMap.get(hedingValue.get(j)),normal));
			 * personalInfoTable.addCell(cell); }
			 * 
			 * 
			 * }
			 */

			PdfPCell cell = new PdfPCell(new Paragraph("Name of Employee", normal));
			personalInfoTable.addCell(cell);

			cell = new PdfPCell(new Paragraph(hmEmpProfile.get("NAME"), normal));
			personalInfoTable.addCell(cell);

			cell = new PdfPCell(new Paragraph("Employee Code No.", normal));
			personalInfoTable.addCell(cell);

			cell = new PdfPCell(new Paragraph(hmEmpProfile.get("EMPCODE"), normal));
			personalInfoTable.addCell(cell);

			cell = new PdfPCell(new Paragraph("Designation", normal));
			personalInfoTable.addCell(cell);

			cell = new PdfPCell(new Paragraph(hmEmpProfile.get("DESIGNATION_NAME"), normal));
			personalInfoTable.addCell(cell);

			cell = new PdfPCell(new Paragraph("Location", normal));
			personalInfoTable.addCell(cell);

			cell = new PdfPCell(new Paragraph(hmEmpProfile.get("WLOCATION_NAME"), normal));
			personalInfoTable.addCell(cell);

			cell = new PdfPCell(new Paragraph("Department", normal));
			personalInfoTable.addCell(cell);

			cell = new PdfPCell(new Paragraph(hmEmpProfile.get("DEPARTMENT_NAME"), normal));
			personalInfoTable.addCell(cell);

			cell = new PdfPCell(new Paragraph("Date of joining", normal));
			personalInfoTable.addCell(cell);

			cell = new PdfPCell(new Paragraph(hmEmpProfile.get("JOINING_DATE"), normal));
			personalInfoTable.addCell(cell);

			cell = new PdfPCell(new Paragraph("Date of Resignation approved", normal));
			personalInfoTable.addCell(cell);

			cell = new PdfPCell(new Paragraph(empMap.get("ACCEPTED_DATE"), normal));
			personalInfoTable.addCell(cell);

			// cell=new PdfPCell(new Paragraph("Salary for month of",normal));
			// personalInfoTable.addCell(cell);
			//
			// cell = new PdfPCell(new Paragraph("-",normal));
			// //empMap.get("MONTHS")
			// personalInfoTable.addCell(cell);

			cell = new PdfPCell(new Paragraph("Date of Relieving (after notice)", normal));
			personalInfoTable.addCell(cell);

			cell = new PdfPCell(new Paragraph(empMap.get("LAST_DAY_DATE"), normal));
			personalInfoTable.addCell(cell);

			/*
			 * cell=new PdfPCell(new
			 * Paragraph("Total Days of Salary month",normal));
			 * personalInfoTable.addCell(cell);
			 * 
			 * cell = new PdfPCell(new
			 * Paragraph(empMap.get("TOTALWORKINGDAYS"),normal));
			 * personalInfoTable.addCell(cell);
			 */

			cell = new PdfPCell(new Paragraph("Notice Period", normal));
			personalInfoTable.addCell(cell);

			cell = new PdfPCell(new Paragraph(uF.showData(noticePeriod, "0") + " days", normal));
			personalInfoTable.addCell(cell);

			cell = new PdfPCell(new Paragraph("Total Years of Service", normal));
			personalInfoTable.addCell(cell);

			cell = new PdfPCell(new Paragraph(uF.showData((String) request.getAttribute("totalService"), ""), normal));
			personalInfoTable.addCell(cell);

			//Started By Dattatray Date:09-12-21
			if (!uF.parseToBoolean(hmFeatureStatus.get(F_DISABLE_RESIGNATION_APPROVAL_REASON_FULL_AND_FIANL_PDF))) {

				cell = new PdfPCell(new Paragraph("Reason for Leaving Service", normal));
				personalInfoTable.addCell(cell);

				cell = new PdfPCell(new Paragraph(empMap.get("EMP_RESIGN_REASON"), normal));
				cell.setColspan(3);
				personalInfoTable.addCell(cell);

				cell = new PdfPCell(new Paragraph("Approval Reason of Manager", normal));
				personalInfoTable.addCell(cell);

				cell = new PdfPCell(new Paragraph(empMap.get("MANAGER_APPROVE_REASON"), normal));
				cell.setColspan(3);
				personalInfoTable.addCell(cell);

				cell = new PdfPCell(new Paragraph("Approval Reason of HR Manager", normal));
				personalInfoTable.addCell(cell);

				cell = new PdfPCell(new Paragraph(empMap.get("HR_MANAGER_APPROVE_REASON"), normal));
				cell.setColspan(3);
				personalInfoTable.addCell(cell);
			}//Ended By Dattatray Date:09-12-21

			// cell=new PdfPCell(new Paragraph("Basic Salary (from Salary
			// Structure)",normal));
			// personalInfoTable.addCell(cell);
			//
			// cell = new PdfPCell(new Paragraph("NA",normal));
			// personalInfoTable.addCell(cell);
			//
			// cell=new PdfPCell(new Paragraph("Leaves Available",normal));
			// personalInfoTable.addCell(cell);
			//
			// cell = new PdfPCell(new Paragraph("0",normal));
			// personalInfoTable.addCell(cell);
			//
			// cell=new PdfPCell(new Paragraph("Salary for month for F &
			// F",normal));
			// personalInfoTable.addCell(cell);
			//
			// cell = new PdfPCell(new
			// Paragraph(uF.showData(empMap.get("MONTHS"), ""),normal));
			// personalInfoTable.addCell(cell);
			//
			// cell=new PdfPCell(new Paragraph("Total days Payable
			// Salary",normal));
			// personalInfoTable.addCell(cell);
			//
			// cell = new PdfPCell(new
			// Paragraph(uF.showData(empMap.get("TOTALWORKINGDAYS"),
			// ""),normal));
			// personalInfoTable.addCell(cell);

			PdfPTable salaryDetailTable = new PdfPTable(4);
			salaryDetailTable.setWidthPercentage(100);
			int[] colWidhtsForSalary = {45, 15, 35, 15};
			salaryDetailTable.setWidths(colWidhtsForSalary);

			PdfPCell earningCell = new PdfPCell(new Paragraph("E A R N I N G S", title));
			earningCell.setColspan(2);
			// earningCell.set
			earningCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			// earningCell.set
			salaryDetailTable.addCell(earningCell);

			PdfPCell deductionCell = new PdfPCell(new Paragraph("D E D U C T I O N S", title));
			deductionCell.setColspan(2);

			deductionCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			salaryDetailTable.addCell(deductionCell);

			for (int i = 0; i < alEmpSalaryDetailsDeduction.size() || i < alEmpSalaryDetailsEarning.size(); i++) {

				if (i < alEmpSalaryDetailsEarning.size()) {

					PdfPCell cell1 = new PdfPCell(new Paragraph(uF.showData(hmSalaryDetails.get(alEmpSalaryDetailsEarning.get(i)), "-"), normal));
					cell1.setHorizontalAlignment(Element.ALIGN_LEFT);//Created By Dattatray Date:09-12-21
					cell1.setPaddingLeft(5);//Created By Dattatray Date:09-12-21
					salaryDetailTable.addCell(cell1);
					double salHeadAmt = 0;
					if (hmSalaryAmt.get(alEmpSalaryDetailsEarning.get(i)) != null) {
						salHeadAmt = hmSalaryAmt.get(alEmpSalaryDetailsEarning.get(i));
						earningTotal += hmSalaryAmt.get(alEmpSalaryDetailsEarning.get(i));
					}

					PdfPCell cell2 = new PdfPCell(new Paragraph(uF.formatIntoOneDecimal(salHeadAmt), normal));
					cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
					salaryDetailTable.addCell(cell2);

				} else {
					PdfPCell cell1 = new PdfPCell(new Paragraph("", normal));
					cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
					salaryDetailTable.addCell(cell1);

					PdfPCell cell2 = new PdfPCell(new Paragraph("", normal));
					cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
					salaryDetailTable.addCell(cell2);
				}

				if (i < alEmpSalaryDetailsDeduction.size()) {

					PdfPCell cell1 = new PdfPCell(new Paragraph(uF.showData(hmSalaryDetails.get(alEmpSalaryDetailsDeduction.get(i)), "-"), normal));
					cell1.setHorizontalAlignment(Element.ALIGN_LEFT);//Created By Dattatray Date:09-12-21
					cell1.setPaddingLeft(5);//Created By Dattatray Date:09-12-21
					salaryDetailTable.addCell(cell1);
					double salHeadAmt = 0;
					if (hmSalaryAmt.get(alEmpSalaryDetailsDeduction.get(i)) != null) {
						salHeadAmt = hmSalaryAmt.get(alEmpSalaryDetailsDeduction.get(i));
						deductionTotal += hmSalaryAmt.get(alEmpSalaryDetailsDeduction.get(i));
					}
					//Created By Dattatray Date:14-12-21
					PdfPCell cell2 = new PdfPCell(new Paragraph(uF.formatIntoComma(uF.parseToDouble(uF.formatIntoZeroWithOutComma(salHeadAmt))), normal));
					cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
					salaryDetailTable.addCell(cell2);

				} else {
					PdfPCell cell1 = new PdfPCell(new Paragraph("", normal));
					cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
					salaryDetailTable.addCell(cell1);

					PdfPCell cell2 = new PdfPCell(new Paragraph("", normal));
					cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
					salaryDetailTable.addCell(cell2);
				}
			}

			double netSalaryTotal = earningTotal - deductionTotal;

			PdfPCell cell1 = new PdfPCell(new Paragraph("Gross Salary Earning Amount", bold));
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);//Created By Dattatray Date:09-12-21
			cell1.setPaddingLeft(5);//Created By Dattatray Date:09-12-21
			PdfPCell cell2 = new PdfPCell(new Paragraph(uF.formatIntoOneDecimal(earningTotal), bold));
			cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
			PdfPCell cell3 = new PdfPCell(new Paragraph("Gross Salary Deductions Amount", bold));
			cell3.setHorizontalAlignment(Element.ALIGN_LEFT);//Created By Dattatray Date:09-12-21
			cell3.setPaddingLeft(5);//Created By Dattatray Date:09-12-21
			PdfPCell cell4 = new PdfPCell(new Paragraph(uF.formatIntoComma(uF.parseToDouble(uF.formatIntoZeroWithOutComma(deductionTotal))), bold));//Created By Dattatray Date:14-12-21
			cell4.setHorizontalAlignment(Element.ALIGN_RIGHT);
			PdfPCell cell5 = new PdfPCell(new Paragraph("NET SALARY TOTAL:", bold));
			cell5.setPaddingLeft(5);
			PdfPCell cell6 = new PdfPCell(new Paragraph(uF.formatIntoComma(uF.parseToDouble(uF.formatIntoZeroWithOutComma(netSalaryTotal))), bold));
			cell6.setColspan(3);
			cell6.setHorizontalAlignment(Element.ALIGN_RIGHT);

			salaryDetailTable.addCell(cell1);
			salaryDetailTable.addCell(cell2);
			salaryDetailTable.addCell(cell3);
			salaryDetailTable.addCell(cell4);
			salaryDetailTable.addCell(cell5);
			salaryDetailTable.addCell(cell6);

			/*
			 * PdfPTable allowanceTable = new PdfPTable(4);
			 * allowanceTable.setWidthPercentage(100);
			 */

			double dblOtherEarningTotal = 0.0d;
			double dblOtherDeductionTotal = 0.0d;

			double reimbursement = (Double) request.getAttribute("Reimbursement");
			dblOtherEarningTotal += reimbursement;
			double gratuity = (Double) request.getAttribute("gratuity");
			dblOtherEarningTotal += gratuity;
			double LTAAmt = (Double) request.getAttribute("LTAAmt");
			dblOtherEarningTotal += LTAAmt;
			double PerkAmt = (Double) request.getAttribute("PerkAmt");
			dblOtherEarningTotal += PerkAmt;

			double deductAmt = (Double) request.getAttribute("deductAmt");
			dblOtherDeductionTotal += deductAmt;

			// List<String> otherHeadList = getOtherHeadsDetails();
			List<String> otherHeadList = new ArrayList<String>();
			otherHeadList.add("Reimbursement");
			otherHeadList.add("Other Deduction");
			otherHeadList.add("Gratuity");
			otherHeadList.add("");
			otherHeadList.add("LTA");
			otherHeadList.add("");
			otherHeadList.add("Perk");
			otherHeadList.add("");
			otherHeadList.add("Gross Other Earnings");
			otherHeadList.add("Gross Other Deductions");

			Map<String, String> hmOtherHeadAmt = new LinkedHashMap<String, String>();
			hmOtherHeadAmt.put("Reimbursement", uF.formatIntoOneDecimal(reimbursement));
			hmOtherHeadAmt.put("Other Deduction", uF.formatIntoOneDecimal(deductAmt));
			hmOtherHeadAmt.put("Gratuity", uF.formatIntoOneDecimal(gratuity));
			hmOtherHeadAmt.put("", "");
			hmOtherHeadAmt.put("LTA", uF.formatIntoOneDecimal(LTAAmt));
			hmOtherHeadAmt.put("", "");
			hmOtherHeadAmt.put("Perk", uF.formatIntoOneDecimal(PerkAmt));
			hmOtherHeadAmt.put("", "");
			hmOtherHeadAmt.put("Gross Other Earnings", uF.formatIntoOneDecimal(dblOtherEarningTotal));
			hmOtherHeadAmt.put("Gross Other Deductions", uF.formatIntoOneDecimal(dblOtherDeductionTotal));

//			System.out.println("hmOtherHeadAmt : "+hmOtherHeadAmt);
//			System.out.println("otherHeadList : "+otherHeadList);
//			if (!uF.parseToBoolean(hmFeatureStatus.get(F_DISABLE_RESIGNATION_APPROVAL_REASON_FULL_AND_FIANL_PDF))) {

				PdfPCell othersCell = new PdfPCell(new Paragraph("O T H E R S", title));
				othersCell.setColspan(4);
				othersCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				salaryDetailTable.addCell(othersCell);
				for (int i = 0; i < otherHeadList.size(); i++) {

					PdfPCell cellAllowanceDesc = new PdfPCell(new Paragraph(otherHeadList.get(i), normal));
					cellAllowanceDesc.setHorizontalAlignment(Element.ALIGN_LEFT);//Created By Dattatray Date:09-12-21
					cellAllowanceDesc.setPaddingLeft(5);//Created By Dattatray Date:09-12-21
					salaryDetailTable.addCell(cellAllowanceDesc);

					//Started By Dattatray Date:09-12-21
					if(!otherHeadList.get(i).isEmpty()) {
						PdfPCell cellAllowanceAmt = new PdfPCell(new Paragraph(uF.showData(hmOtherHeadAmt.get(otherHeadList.get(i)), "-"), normal));
						cellAllowanceAmt.setHorizontalAlignment(Element.ALIGN_RIGHT);
						salaryDetailTable.addCell(cellAllowanceAmt);
					}else {
						PdfPCell cellAllowanceAmt = new PdfPCell(new Paragraph("", normal));
						cellAllowanceAmt.setHorizontalAlignment(Element.ALIGN_RIGHT);
						salaryDetailTable.addCell(cellAllowanceAmt);
					}//Ended By Dattatray Date:09-12-21
					

				}
//			}
			double netOtherTotal = dblOtherEarningTotal - dblOtherDeductionTotal;
			double settlementAmount = netSalaryTotal + netOtherTotal;

			String amountInWords = "";
			String strTotalAmt = "" + settlementAmount;
			if (strTotalAmt.contains(".")) {
				strTotalAmt = strTotalAmt.replace(".", ",");
				String[] temp = strTotalAmt.split(",");
				amountInWords = uF.digitsToWords(uF.parseToInt(temp[0]));
				if (uF.parseToInt(temp[1]) > 0) {
					int pamt = 0;
					if (temp[1].length() == 1) {
						pamt = uF.parseToInt(temp[1] + "0");
					} else {
						pamt = uF.parseToInt(temp[1]);
					}
					amountInWords += " and " + uF.digitsToWords(pamt) + " paise only";
				}
			} else {
				int totalAmt1 = (int) settlementAmount;
				amountInWords = uF.digitsToWords(totalAmt1) + " only";
			}

			PdfPCell netOthrTotalLbl = new PdfPCell(new Paragraph("NET OTHER TOTAL:", bold));
			PdfPCell netOthrTotalAmt = new PdfPCell(new Paragraph(uF.formatIntoOneDecimal(netOtherTotal), bold));
			netOthrTotalAmt.setColspan(3);
			netOthrTotalAmt.setHorizontalAlignment(Element.ALIGN_RIGHT);
			netOthrTotalLbl.setPaddingLeft(5);//Created By Dattatray Date:09-12-21
			salaryDetailTable.addCell(netOthrTotalLbl);
			salaryDetailTable.addCell(netOthrTotalAmt);

			PdfPCell totSettlementLbl = new PdfPCell(new Paragraph("Total Settlement Amount ("+hmCurrencyDetails.get("SHORT_CURR")+"):", bold));//Created By Dattatray Date:09-12-21
			PdfPCell totSettlementAmt = new PdfPCell(new Paragraph(uF.formatIntoComma(uF.parseToDouble(uF.formatIntoZeroWithOutComma(settlementAmount))), bold));//Created By Dattatray Date:14-12-21
			totSettlementAmt.setColspan(3);
			totSettlementLbl.setPaddingLeft(5);//Created By Dattatray Date:09-12-21
			totSettlementAmt.setHorizontalAlignment(Element.ALIGN_RIGHT);
			salaryDetailTable.addCell(totSettlementLbl);
			salaryDetailTable.addCell(totSettlementAmt);

			PdfPCell totSettlementWordsLbl = new PdfPCell(new Paragraph("Total Settlement Amount ("+hmCurrencyDetails.get("SHORT_CURR")+"):", bold));//Created By Dattatray Date:09-12-21
			PdfPCell totSettlementWordsAmt = new PdfPCell(new Paragraph(amountInWords, bold));//Created By Dattatray Date:09-12-21
			totSettlementWordsLbl.setPaddingLeft(5);//Created By Dattatray Date:09-12-21
			totSettlementWordsAmt.setColspan(3);
			salaryDetailTable.addCell(totSettlementWordsLbl);
			salaryDetailTable.addCell(totSettlementWordsAmt);

			PdfPTable authorityAndSignTable = new PdfPTable(1);
			authorityAndSignTable.setWidthPercentage(100);

			PdfPCell dues = new PdfPCell(new Paragraph("No Dues Clearance- Approved by all Departments.", normal));

			PdfPCell preparedBy = new PdfPCell(new Paragraph("Prepared By :" + (String) session.getAttribute(EMPNAME), normal));

			PdfPCell hrDepart = new PdfPCell(new Paragraph("HR Department", normal));

			PdfPCell agreement = new PdfPCell(new Paragraph(
					"I hereby agree and confirm having received the above amount before signing this settlement paper. There is nothing due on either side.",
					normal));

			authorityAndSignTable.addCell(dues);
			authorityAndSignTable.addCell(preparedBy);
			authorityAndSignTable.addCell(hrDepart);
			authorityAndSignTable.addCell(agreement);

			/*
			 * PdfPCell checkedBy = new PdfPCell(new
			 * Paragraph("Checked By :",normal));
			 * checkedBy.disableBorderSide(Rectangle.TOP);
			 * checkedBy.disableBorderSide(Rectangle.LEFT);
			 * checkedBy.disableBorderSide(Rectangle.BOTTOM);
			 * checkedBy.setHorizontalAlignment(Element.ALIGN_CENTER);
			 * authorityAndSignTable.addCell(checkedBy);
			 */

			// PdfPCell blankAfterCheckBy1 = new PdfPCell(new Paragraph(" "));
			// blankAfterCheckBy1.disableBorderSide(Rectangle.TOP);
			// blankAfterCheckBy1.disableBorderSide(Rectangle.BOTTOM);
			// authorityAndSignTable.addCell(blankAfterCheckBy1);
			//
			// PdfPCell blankAfterCheckBy2 = new PdfPCell(new Paragraph(" "));
			// blankAfterCheckBy2.disableBorderSide(Rectangle.TOP);
			// blankAfterCheckBy2.disableBorderSide(Rectangle.BOTTOM);
			// authorityAndSignTable.addCell(blankAfterCheckBy2);

			// for(int i=0;i<4;i++){
			// cell = new PdfPCell(new Paragraph(" "));
			// if(i==0){
			// cell.disableBorderSide(Rectangle.TOP);
			// cell.disableBorderSide(Rectangle.RIGHT);
			// cell.disableBorderSide(Rectangle.BOTTOM);
			// }
			// if(i==1){
			// cell.disableBorderSide(Rectangle.TOP);
			// cell.disableBorderSide(Rectangle.LEFT);
			// cell.disableBorderSide(Rectangle.BOTTOM);
			// }
			// if(i==2 || i==3){
			// cell.disableBorderSide(Rectangle.TOP);
			// cell.disableBorderSide(Rectangle.BOTTOM);
			// }
			//
			// authorityAndSignTable.addCell(cell);
			// }

			// PdfPCell personalDept = new PdfPCell(new Paragraph("Personnel
			// Department",normal));
			// personalDept.setHorizontalAlignment(Element.ALIGN_RIGHT);
			// personalDept.disableBorderSide(Rectangle.TOP);
			// personalDept.disableBorderSide(Rectangle.RIGHT);
			// authorityAndSignTable.addCell(personalDept);
			//
			// PdfPCell blankAfterpersonalDept = new PdfPCell(new Paragraph("
			// "));
			// blankAfterpersonalDept.disableBorderSide(Rectangle.TOP);
			// blankAfterpersonalDept.disableBorderSide(Rectangle.LEFT);
			// authorityAndSignTable.addCell(blankAfterpersonalDept);
			//
			// PdfPCell blankAfterpersonalDept1 = new PdfPCell(new Paragraph("
			// "));
			// blankAfterpersonalDept1.disableBorderSide(Rectangle.TOP);
			// blankAfterpersonalDept1.disableBorderSide(Rectangle.BOTTOM);
			// authorityAndSignTable.addCell(blankAfterpersonalDept1);
			//
			// PdfPCell blankAfterpersonalDept2 = new PdfPCell(new Paragraph("
			// "));
			// blankAfterpersonalDept2.disableBorderSide(Rectangle.TOP);
			// blankAfterpersonalDept2.disableBorderSide(Rectangle.BOTTOM);
			// authorityAndSignTable.addCell(blankAfterpersonalDept2);

			// for(int i=0;i<4;i++){
			// cell=new PdfPCell(new Paragraph(" "));
			// if(i==0){
			// //cell.disableBorderSide(Rectangle.TOP);
			// cell.disableBorderSide(Rectangle.RIGHT);
			// cell.setColspan(2);
			//
			// }
			// if(i==1){
			// continue;
			// }
			// if(i==2){
			// cell.disableBorderSide(Rectangle.LEFT);
			// cell.disableBorderSide(Rectangle.TOP);
			// cell.disableBorderSide(Rectangle.BOTTOM);
			// }
			// if(i==3){
			// cell.disableBorderSide(Rectangle.BOTTOM);
			// cell.disableBorderSide(Rectangle.TOP);
			// }
			//
			// authorityAndSignTable.addCell(cell);
			// }

			PdfPTable accountDepartTable = new PdfPTable(2);
			accountDepartTable.setWidthPercentage(100);

			PdfPCell accDepartLbl = new PdfPCell(new Paragraph("--For Account Department Only--", title));
			accDepartLbl.setHorizontalAlignment(Element.ALIGN_CENTER);
			accDepartLbl.setColspan(2);

			PdfPCell paymentvideChqNo = new PdfPCell(new Paragraph("Payment vide Cheque no.", normal));
			paymentvideChqNo.setColspan(2);

			PdfPCell dateOfPayment = new PdfPCell(new Paragraph("Date of Payment", normal));
			dateOfPayment.setColspan(2);

			PdfPCell nameOfBank = new PdfPCell(new Paragraph("Name of Bank", normal));
			nameOfBank.setColspan(2);

			PdfPCell dateLbl = new PdfPCell(new Paragraph("Date", normal));
			dateLbl.setColspan(2);

			PdfPCell forLbl = new PdfPCell(new Paragraph("For", normal));
			forLbl.setColspan(2);

			PdfPCell managerHRLbl = new PdfPCell(new Paragraph("Manager-HR \n (Authorized signatory)", normal));

			PdfPCell accountDepartLbl = new PdfPCell(new Paragraph("Account Department \n (Authorized signatory)", normal));

			accountDepartTable.addCell(accDepartLbl);
			accountDepartTable.addCell(paymentvideChqNo);
			accountDepartTable.addCell(dateOfPayment);
			accountDepartTable.addCell(nameOfBank);
			accountDepartTable.addCell(dateLbl);
			accountDepartTable.addCell(forLbl);
			accountDepartTable.addCell(managerHRLbl);
			accountDepartTable.addCell(accountDepartLbl);

			// for(int i=0;i<4;i++){
			// cell = new PdfPCell();
			// if(i==0){
			// cell = new PdfPCell(new Paragraph(" -- For Accounts Department
			// Only -- ",normal));
			// cell.disableBorderSide(Rectangle.BOTTOM);
			// cell.disableBorderSide(Rectangle.RIGHT);
			// cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			// cell.setColspan(2);
			// }
			// if(i==1){
			// continue;
			// }
			// if(i==2){
			// cell.disableBorderSide(Rectangle.TOP);
			// cell.disableBorderSide(Rectangle.BOTTOM);
			// }
			// if(i==3){
			// cell = new PdfPCell(new Paragraph("(Signature of
			// Employee)",normal));
			// cell.disableBorderSide(Rectangle.TOP);
			//
			// cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			// }
			// authorityAndSignTable.addCell(cell);
			// }

			// for(int i=0;i<4;i++){
			// cell = new PdfPCell(new Paragraph(" "));
			// if(i==0){
			// cell.setColspan(2);
			// cell.disableBorderSide(Rectangle.TOP);
			// cell.disableBorderSide(Rectangle.BOTTOM);
			// }
			// if(i==1){
			// continue;
			// }
			// if(i==2){
			// cell.disableBorderSide(Rectangle.TOP);
			// cell.disableBorderSide(Rectangle.BOTTOM);
			// cell.disableBorderSide(Rectangle.RIGHT);
			// }
			// if(i==3){
			// cell.disableBorderSide(Rectangle.TOP);
			// cell.disableBorderSide(Rectangle.LEFT);
			// }
			// authorityAndSignTable.addCell(cell);
			// }

			// for(int i=0;i<4;i++){
			// cell = new PdfPCell();
			// if(i==0){
			// cell = new PdfPCell(new Paragraph("Payment vide Cheque No.
			// :......................................",normal));
			// cell.setColspan(2);
			// cell.disableBorderSide(Rectangle.TOP);
			// cell.disableBorderSide(Rectangle.BOTTOM);
			// }
			// if(i==1){
			// continue;
			// }
			// if(i==2){
			// cell.disableBorderSide(Rectangle.TOP);
			// cell.disableBorderSide(Rectangle.BOTTOM);
			// }
			// if(i==3){
			// cell = new PdfPCell(new Paragraph("For",normal));
			// cell.disableBorderSide(Rectangle.BOTTOM);
			// }
			// authorityAndSignTable.addCell(cell);
			// }

			// for(int i=0;i<4;i++){
			// cell = new PdfPCell();
			// if(i==0){
			// cell = new PdfPCell(new Paragraph("Date of Payment
			// :......................................",normal));
			// cell.setColspan(2);
			// cell.disableBorderSide(Rectangle.TOP);
			// cell.disableBorderSide(Rectangle.BOTTOM);
			// }
			// if(i==1){
			// continue;
			// }
			// if(i==2){
			// cell.disableBorderSide(Rectangle.TOP);
			// cell.disableBorderSide(Rectangle.BOTTOM);
			// }
			// if(i==3){
			// cell = new PdfPCell(new Paragraph("co.",normal));
			// cell.disableBorderSide(Rectangle.TOP);
			// cell.disableBorderSide(Rectangle.BOTTOM);
			// }
			// authorityAndSignTable.addCell(cell);
			// }

			// for(int i=0;i<4;i++){
			// cell = new PdfPCell();
			// if(i==0){
			// cell = new PdfPCell(new Paragraph("Name of Bank
			// :......................................",normal));
			// cell.setColspan(2);
			// cell.disableBorderSide(Rectangle.TOP);
			// cell.disableBorderSide(Rectangle.BOTTOM);
			// }
			// if(i==1){
			// continue;
			// }
			// if(i==2){
			// cell.disableBorderSide(Rectangle.TOP);
			// cell.disableBorderSide(Rectangle.BOTTOM);
			// }
			// if(i==3){
			// //cell = new PdfPCell(new Paragraph("co.",normal));
			// cell.disableBorderSide(Rectangle.TOP);
			// cell.disableBorderSide(Rectangle.BOTTOM);
			// }
			// authorityAndSignTable.addCell(cell);
			// }

			// for(int i=0;i<4;i++){
			// cell = new PdfPCell(new Paragraph(" "));
			// if(i==0){
			// cell.setColspan(2);
			// cell.disableBorderSide(Rectangle.TOP);
			// cell.disableBorderSide(Rectangle.BOTTOM);
			// }
			// if(i==1){
			// continue;
			// }
			// if(i==2){
			// cell.disableBorderSide(Rectangle.TOP);
			// cell.disableBorderSide(Rectangle.BOTTOM);
			// }
			// if(i==3){
			// cell.disableBorderSide(Rectangle.TOP);
			// cell.disableBorderSide(Rectangle.BOTTOM);
			// }
			// authorityAndSignTable.addCell(cell);
			// }

			// for(int i=0;i<4;i++){
			// cell = new PdfPCell();
			// if(i==0){
			// cell = new PdfPCell(new Paragraph("Date:____________",normal));
			// cell.disableBorderSide(Rectangle.TOP);
			// cell.disableBorderSide(Rectangle.BOTTOM);
			// cell.disableBorderSide(Rectangle.RIGHT);
			// }
			// if(i==1){
			// cell = new PdfPCell(new Paragraph("Account Department",normal));
			// cell.disableBorderSide(Rectangle.TOP);
			// cell.disableBorderSide(Rectangle.LEFT);
			// cell.disableBorderSide(Rectangle.BOTTOM);
			// }
			// if(i==2){
			// cell.disableBorderSide(Rectangle.TOP);
			//
			// cell.disableBorderSide(Rectangle.BOTTOM);
			// }
			// if(i==3){
			// cell = new PdfPCell(new Paragraph("Manager - HR",normal));
			// cell.disableBorderSide(Rectangle.TOP);
			// cell.disableBorderSide(Rectangle.BOTTOM);
			// }
			// authorityAndSignTable.addCell(cell);
			// }

			// for(int i=0;i<4;i++){
			// cell = new PdfPCell();
			// if(i==0){
			// cell.disableBorderSide(Rectangle.TOP);
			// cell.disableBorderSide(Rectangle.RIGHT);
			// }
			// if(i==1){
			// cell = new PdfPCell(new Paragraph("(Authorized
			// Signatory)",normal));
			// cell.disableBorderSide(Rectangle.TOP);
			// cell.disableBorderSide(Rectangle.LEFT);
			// //cell.disableBorderSide(Rectangle.BOTTOM);
			// }
			// if(i==2){
			// cell.disableBorderSide(Rectangle.TOP);
			// //cell.disableBorderSide(Rectangle.BOTTOM);
			// }
			// if(i==3){
			// cell.disableBorderSide(Rectangle.TOP);
			// //cell.disableBorderSide(Rectangle.BOTTOM);
			// }
			// authorityAndSignTable.addCell(cell);
			// }

			document.add(header);//Created By Dattatray Date:09-12-21
//			document.add(sheetTitle);
			document.add(blankSpace);
			document.add(personalInfoTable);
			document.add(blankSpace);
			document.add(salaryDetailTable);
			// document.add(blankSpace);
			// document.add(salaryDetailTable);
			document.add(blankSpace);
			document.add(authorityAndSignTable);
			document.add(blankSpace);
			document.add(accountDepartTable);

			document.close();
			response.setContentType("application/pdf");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition", "attachment; filename=FullandFinal.pdf");
			ServletOutputStream out = response.getOutputStream();
			buffer.writeTo(out);
			response.getOutputStream().flush();
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// public List<String> getPersonalHeadings () {
	// List<String> personalHeadings = new ArrayList<String>();
	// personalHeadings.add("Name of the Employee :-");
	// personalHeadings.add("Location :-");
	// personalHeadings.add("Employee Code Number :-");
	// personalHeadings.add("Designation :-");
	// personalHeadings.add("Department :-");
	// personalHeadings.add("Date of Joining :-");
	// personalHeadings.add("Date of resignation received/termination");
	// personalHeadings.add("Salary for the Month of :-");
	// personalHeadings.add("Date of Relieving (after working hour)");
	// personalHeadings.add("Total Days of Salary Month :-");
	// personalHeadings.add("If Notice not given (Deduction / Waived Off)");
	// personalHeadings.add("Total days payable (Salary) :-");
	// personalHeadings.add("Reason for leaving service :-");
	// return personalHeadings;
	// }

	// public List<String> getEarningDetails() {
	// List<String> earningHeadings = new ArrayList<String>();
	// earningHeadings.add("Basic + Per Bonus");
	// earningHeadings.add("DA");
	// earningHeadings.add("Basic + DA");
	// earningHeadings.add("H.R.A.");
	// earningHeadings.add("Medical");
	// earningHeadings.add("Extra");
	// earningHeadings.add("Incentive");
	// earningHeadings.add("Conveyance");
	// earningHeadings.add("Performance Bonus");
	// //earningHeadings.add("EL Encash");
	// earningHeadings.add("Security Deposit");
	// earningHeadings.add("July 09 per. Bonus");
	// earningHeadings.add("ROUND UP");
	// earningHeadings.add("Total");
	// earningHeadings.add("AMOUNT PAYABLE (Earning - Deductions):");
	// //earningHeadings.add(" ");
	// // earningHeadings.add(" ");
	// return earningHeadings;
	// }

	// public List<String> getDeductionDetails() {
	// List<String> deductionHeadings = new ArrayList<String>();
	// deductionHeadings.add("P.F");
	// deductionHeadings.add("Prof. Tax");
	// deductionHeadings.add("Income Tax");
	// deductionHeadings.add("E.S.I");
	// deductionHeadings.add("Loans/Advances");
	// deductionHeadings.add("Society");
	// deductionHeadings.add("Other");
	// deductionHeadings.add("Other");
	// deductionHeadings.add("Total");
	//
	// return deductionHeadings;
	// }

	public List<String> getOtherHeadsDetails() {
		List<String> reiumbursementDetails = new ArrayList<String>();
		reiumbursementDetails.add("Reimbursement");
		reiumbursementDetails.add("Loan Amount");
		reiumbursementDetails.add("Gratuity");
		reiumbursementDetails.add("Travel Advance");
		reiumbursementDetails.add("LTA");
		reiumbursementDetails.add("Other Advance deductions");
		reiumbursementDetails.add("Perks");
		reiumbursementDetails.add("Other Deductions (Manual)");
		reiumbursementDetails.add("Gross Other Earnings");
		reiumbursementDetails.add("Gross Other Deductions");

		return reiumbursementDetails;
	}

	@Override
	public void setSession(Map arg0) {
		// TODO Auto-generated method stub

	}

}