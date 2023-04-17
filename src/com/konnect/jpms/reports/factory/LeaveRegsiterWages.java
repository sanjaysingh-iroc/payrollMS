package com.konnect.jpms.reports.factory;

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
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Years;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class LeaveRegsiterWages extends ActionSupport implements ServletRequestAware,ServletResponseAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(ChildWorkerReport.class);
	CommonFunctions CF=null;
	HttpSession session;
	
	public String execute() throws Exception {
		
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		request.setAttribute(PAGE, PLeaveRegisterWages);
		request.setAttribute(TITLE, TLeaveRegsiterWages);

		viewLeaveRegsiterWages();
		return loadLeaveRegsiterWages();

	}

	public String loadLeaveRegsiterWages() {

		return LOAD;
	}

	public String viewLeaveRegsiterWages() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			List<List<String>> al = new ArrayList<List<String>>();
			List<String> alInner = new ArrayList<String>();

			
			List<List<String>> alPdf = new ArrayList<List<String>>();
			List<String> alInnerPdf = new ArrayList<String>();
			
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			Map<String, String> hmFamilyInfo = new HashMap<String, String>();
			pst = con.prepareStatement("select * from emp_family_members where member_type in ('SPOUSE', 'FATHER')");
			rs = pst.executeQuery();
			while (rs.next()) {
				hmFamilyInfo.put(rs.getString("emp_id") + "_" + rs.getString("member_type"), rs.getString("member_name"));
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select * from employee_personal_details order by emp_fname, emp_lname");
			rs = pst.executeQuery();

			int nCount = 0;
			DateTime now = new DateTime();
			while (rs.next()) {

				int years = 0;
				if (rs.getString("emp_date_of_birth") != null) {
					DateMidnight birthdate = new DateMidnight(uF.parseToInt(uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, "yyyy")), uF.parseToInt(uF.getDateFormat(
							rs.getString("emp_date_of_birth"), DBDATE, "MM")), uF.parseToInt(uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, "dd")));
					Years age = Years.yearsBetween(birthdate, now);
					years = age.getYears();
				}

				if (years >= 18) {
					continue;
				}

				alInner = new ArrayList<String>();

				alInner.add("");
				alInner.add("" + ++nCount);
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				alInner.add(uF.showData(rs.getString("emp_fname"), "") +strEmpMName+ " " + uF.showData(rs.getString("emp_lname"), ""));
				alInner.add(uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, DATE_FORMAT));
				alInner.add(uF.showData(rs.getString("emp_gender"), ""));
				alInner.add(uF.showData(rs.getString("emp_address1"), "") + ", " + uF.showData(rs.getString("emp_city_id"), ""));

				if (rs.getString("emp_gender") != null && rs.getString("emp_gender").equalsIgnoreCase("M")) {
					alInner.add(uF.showData(hmFamilyInfo.get(rs.getString("emp_per_id") + "_FATHER"), ""));
				} else if (rs.getString("emp_gender") != null && rs.getString("emp_gender").equalsIgnoreCase("F") && rs.getString("marital_status") != null
						&& rs.getString("marital_status").equalsIgnoreCase("M")) {
					alInner.add(uF.showData(hmFamilyInfo.get(rs.getString("emp_per_id") + "_SPOUSE"), ""));
				} else {
					alInner.add(uF.showData(hmFamilyInfo.get(rs.getString("emp_per_id") + "_FATHER"), ""));
				}

				alInner.add(uF.getDateFormat(rs.getString("joining_date"), DBDATE, DATE_FORMAT));
				alInner.add("");
				alInner.add("");
				alInner.add("");
				alInner.add("");
				alInner.add("");
				alInner.add("");

				al.add(alInner);
				
				
				alInnerPdf = new ArrayList<String>();

				alInnerPdf.add("");
				alInnerPdf.add("" +nCount);
				alInnerPdf.add(uF.showData(rs.getString("emp_fname"), "") +strEmpMName+ " " + uF.showData(rs.getString("emp_lname"), ""));
				
				alInnerPdf.add(uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, DATE_FORMAT));
				alInnerPdf.add(uF.showData(rs.getString("emp_gender"), ""));
				alInnerPdf.add(uF.showData(rs.getString("emp_address1"), "") + ", " + uF.showData(rs.getString("emp_city_id"), ""));

				if (rs.getString("emp_gender") != null && rs.getString("emp_gender").equalsIgnoreCase("M")) {
					alInnerPdf.add(uF.showData(hmFamilyInfo.get(rs.getString("emp_per_id") + "_FATHER"), ""));
				} else if (rs.getString("emp_gender") != null && rs.getString("emp_gender").equalsIgnoreCase("F") && rs.getString("marital_status") != null
						&& rs.getString("marital_status").equalsIgnoreCase("M")) {
					alInnerPdf.add(uF.showData(hmFamilyInfo.get(rs.getString("emp_per_id") + "_SPOUSE"), ""));
				} else {
					alInnerPdf.add(uF.showData(hmFamilyInfo.get(rs.getString("emp_per_id") + "_FATHER"), ""));
				}

				alInnerPdf.add(uF.getDateFormat(rs.getString("joining_date"), DBDATE, DATE_FORMAT));
				alInnerPdf.add("");
				alInnerPdf.add("");
				alInnerPdf.add("");
				alInnerPdf.add("");
				alInnerPdf.add("");
				alInnerPdf.add("");

				alPdf.add(alInner);
			}
			rs.close();
			pst.close();

			request.setAttribute("reportList", al);

			
			if(getPdfGeneration()!=null && getPdfGeneration().equalsIgnoreCase("true")){
				
				LeaveRegisterWages objPdf = new LeaveRegisterWages(alPdf,response);
				objPdf.exportPdf();
				
				}
			

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " + e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

		return SUCCESS;

	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
	
	String pdfGeneration;

	public String getPdfGeneration() {
		return pdfGeneration;
	}


	public void setPdfGeneration(String pdfGeneration) {
		this.pdfGeneration = pdfGeneration;
	}

	HttpServletResponse response;
	@Override
	public void setServletResponse(HttpServletResponse response) {
		// 
		this.response=response;
		
	}

}


class LeaveRegisterWages {
	

	
	List reportList;
	LeaveRegisterWages(List reportList, HttpServletResponse response){
		this.reportList = reportList;
		this.response=response;
	}
	

	public void exportPdf(){
		
		
		
		 ByteArrayOutputStream bos = new ByteArrayOutputStream();
	
		 Document document = new Document();
		
		 try{
				PdfWriter.getInstance(document, bos);
				
/*					document.open();
					
				Paragraph blankSpace = new Paragraph(" ");
				Paragraph title = new Paragraph("FORM -14",heading);
				title.setAlignment(Element.ALIGN_CENTER);
				Paragraph subTitle = new Paragraph("(Prescribed under Rule 113)",heading);
				subTitle.setAlignment(Element.ALIGN_CENTER);
				Paragraph registerName = new Paragraph("REGISTER OF CHILD WORKERS",normalwithbold);
				registerName.setAlignment(Element.ALIGN_CENTER);
				
				PdfPTable table = new PdfPTable(11);
				table.setWidthPercentage(100);
				
				int[] cols = {4,7,10,10,8,8,10,15,7,10,8};
				table.setWidths(cols);
				
				List<String> heading = getHeadings();
				
				for(int i=0;i<heading.size();i++){
					PdfPCell cell1 = new PdfPCell(new Paragraph(heading.get(i),normal));
					table.addCell(cell1);
				}
				
			  
			  	for(int j=1;j<=11;j++){
				PdfPCell cell2 = new PdfPCell(new Paragraph(""+j,normal));
				cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(cell2);
				
				
				
			
				// filling data here *****
				
				for(int a=0;a<reportList.size();a++){
				
					List<String> aInner=(List<String>)reportList.get(a);
					
				for(int j=1;j<=11;j++){
					PdfPCell cell2 = new PdfPCell(new Paragraph(""+aInner.get(j),normal));
					cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(cell2);
				}
				
				}
				
				for(int k=0;k<220;k++){
					PdfPCell cell3 = new PdfPCell(new Paragraph(" ",normal));
					cell3.disableBorderSide(Rectangle.BOTTOM);
					cell3.disableBorderSide(Rectangle.TOP);
					table.addCell(cell3);
				}
				
				for(int l=0;l<11;l++){
					PdfPCell cell4 = new PdfPCell(new Paragraph(" ",normal));
					cell4.disableBorderSide(Rectangle.TOP);
					table.addCell(cell4);
				}
				
				document.add(title);
				document.add(subTitle);
				document.add(registerName);
				document.add(blankSpace);
				document.add(table);
				
				
				document.close();*/
				
				
				response.setContentType("application/pdf");         
				 response.setContentLength(bos.size());
				 response.setHeader("Content-Disposition", "attachment; filename=LeaveRegisterWages.pdf");
				
				 ServletOutputStream out = response.getOutputStream();         
				 bos.writeTo(out);         
				 out.flush();      
				 bos.close();
				 out.close();
					
		}catch(Exception e){
			e.printStackTrace();
		}
	
		
	}
	
	private HttpServletResponse response;
	


}
