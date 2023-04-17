package com.konnect.jpms.requsitions;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.konnect.jpms.charts.BarChart;
import com.konnect.jpms.export.GeneratePdfReports;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GeneratePdf extends ActionSupport implements ServletRequestAware,ServletResponseAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(GeneratePdfReports.class);
	private int requisitionId;
	private String requisitionType;
	private String mode;
	HttpSession session;
	HttpServletRequest request;
	HttpServletResponse response;
	CommonFunctions CF;
	
	String strUserType;
	String strSessionEmpId;
	
	public String execute() throws Exception {
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null) return LOGIN;
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		generatePdf();
		return SUCCESS;
		
	}

	public void generatePdf() {

		PdfWriter writer = null;
		Document document = new Document(PageSize.A4.rotate());
		BarChart bc = new BarChart();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		try {
			
			writer = PdfWriter.getInstance(document, baos);
			document.open();
			addCompanyInfo(document);
			setTitle(document);
			setContent(document);
			document.close();
			
			if(getMode()!=null && getMode().equals("SC")) {
				
				response.setContentType("application/pdf");
				response.setContentLength(baos.size());
				response.setHeader("Content-Disposition", "attachment; filename=" + "Requisition.pdf");
				ServletOutputStream out = response.getOutputStream();
				baos.writeTo(out);
				out.flush();
				
			}else if(getMode()!=null && getMode().equals("HC")) {
				
//				Notifications nF = new Notifications(N_NEW_EMPLOYEE);
//				nF.setStrEmailTo(getEmpEmail());
//				nF.setStrEmpMobileNo(getEmpContactno());
//				nF.setStrEmpCode(getEmpCode());
//				nF.setStrEmpFname(getEmpFname());
//				nF.setStrEmpLname(getEmpLname());
//				nF.setStrUserName(getUserName());
//				nF.setStrPassword(getEmpPassword());
//				nF.setEmailTemplate(true);
//				nF.sendNotifications();
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " + e.getMessage(), e);
		}
		document.close();

	}

	
	Map<String, String> hmEmpNameMap;
	Map<String, String> hmEmpDesigMap;
	Map<String, String> hmEmpJoiningDateMap;
	
	private void setContent(Document document) {
		
		Paragraph paragraph = null;
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs =null;
		Database dB = new Database();
		dB.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try { 
			
			
			con = dB.makeConnection(con);
			
			
			hmEmpNameMap = CF.getEmpNameMap(con, null, null);
			hmEmpDesigMap = CF.getEmpDesigMap(con);
			hmEmpJoiningDateMap = CF.getEmpJoiningDateMap(con, uF);
			
			
			String currentDate = uF.getCurrentDate(CF.getStrTimeZone()).toString();
			
			if(getRequisitionType()!=null && getRequisitionType().equals("BF")) {
				
				pst = con.prepareStatement("SELECT * FROM requisition_bonafide rb, requisition_details rd where rd.requisition_id = rb.requisition_id and rb.requisition_id = ?");
				pst.setInt(1, getRequisitionId());
				
				
				rs = pst.executeQuery();
				String strEmpId = null;
				
				while(rs.next()) {
					strEmpId = rs.getString("emp_id");
				}
				rs.close();
				pst.close();
				
				paragraph = new Paragraph(addBonafideContent(strEmpId));
				
				
			}else if(getRequisitionType()!=null && getRequisitionType().equals("IR")) {
				
				pst = con.prepareStatement("SELECT * FROM (SELECT * FROM (SELECT * FROM requisition_infrastructure " +
						"WHERE requisition_id = ?) ard LEFT JOIN requisition_details rd ON rd.requisition_id = ard.requisition_id ) aepd " +
						"LEFT JOIN employee_personal_details epd ON aepd.emp_id = epd.emp_per_id");
				
				pst.setInt(1, getRequisitionId());
				System.out.println("pst==>"+pst);
				rs = pst.executeQuery();
				String strEmpId = "";
				String strInfrastructureName = "";
				String strInfrastructureType = "";
				String strFrom = "";
				String strTo = "";
				String strPurpose = "";
				
				while(rs.next()) {
					strEmpId = rs.getString("emp_id");
					strInfrastructureName = uF.showData(rs.getString("infrastructure_name"),"");
					strInfrastructureType = uF.showData(rs.getString("infrastructure_type"),"");
					strPurpose =  uF.showData(rs.getString("purpose"),"");
					strFrom = uF.getDateFormat(rs.getString("from_date"), DBDATE, CF.getStrReportDateFormat()) + 
								uF.showData(rs.getString("from_time"),"");
					strTo = uF.getDateFormat(rs.getString("to_date"), DBDATE, CF.getStrReportDateFormat()) + 
								uF.showData(rs.getString("to_time"),"");
				}
				rs.close();
				pst.close();
				
				
				
				
				paragraph = new Paragraph(addInfrastructureContent(strEmpId, strInfrastructureName, strInfrastructureType, strFrom, strTo, strPurpose));
				
			}else {
				
				pst = con.prepareStatement("SELECT * FROM (SELECT * FROM (SELECT * FROM requisition_infrastructure " +
						"WHERE requisition_id = ?) ard LEFT JOIN requisition_details rd ON rd.requisition_id = ard.requisition_id ) aepd " +
						"LEFT JOIN employee_personal_details epd ON aepd.emp_id = epd.emp_per_id");
				
				pst.setInt(1, getRequisitionId());
				
				
				rs = pst.executeQuery();
				String strEmpId = "";
				String strInfrastructureName = "";
				String strInfrastructureType = "";
				String strFrom = "";
				String strTo = "";
				String strPurpose = "";
				
				while(rs.next()) {
					strEmpId = rs.getString("emp_id");
					strInfrastructureName = uF.showData(rs.getString("infrastructure_name"),"");
					strInfrastructureType = uF.showData(rs.getString("infrastructure_type"),"");
					strPurpose =  uF.showData(rs.getString("purpose"),"");
					strFrom = uF.getDateFormat(rs.getString("from_date"), DBDATE, CF.getStrReportDateFormat()) + 
								uF.showData(rs.getString("from_time"),"");
					strTo = uF.getDateFormat(rs.getString("to_date"), DBDATE, CF.getStrReportDateFormat()) + 
								uF.showData(rs.getString("to_time"),"");
				}
				rs.close();
				pst.close();
				
				paragraph = new Paragraph(addOthersContent(strEmpId, strInfrastructureName, strInfrastructureType, strFrom, strTo, strPurpose));
				
			}
			
			paragraph.setAlignment(Element.ALIGN_CENTER);
			document.add(paragraph);
			document.add(new Paragraph(" "));
			document.add(new Paragraph(" "));
			document.add(new Paragraph(" "));
			document.add(new Paragraph(" "));
			document.add(new Paragraph(" "));
			document.add(new Paragraph(" "));
			document.add(new Paragraph("Date", FontFactory.getFont("Verdana", "sans-serif", 15)));
			document.add(new Paragraph(currentDate));
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " + e.getMessage(), e);
		}finally{
			dB.closeResultSet(rs);
			dB.closeStatements(pst);
			dB.closeConnection(con);
		}
	}

	private String addBonafideContent(String strEmpId) {
		
		StringBuilder sb = new StringBuilder();
		
		
		sb.append("This is to certify that "+hmEmpNameMap.get(strEmpId)+" is a bonafied employee of this institute of designation " +
				hmEmpDesigMap.get(strEmpId) +" from "+hmEmpJoiningDateMap.get(strEmpId)+" till date. ");
		
		
		
		return sb.toString();
	}
	
	private String addInfrastructureContent(String strEmpId, String infrastructureName, String infrastructureType, String strFrom, String strTo, String strPurpose) {
		
		StringBuilder sb = new StringBuilder();
		
		
		sb.append("This is to certify that "+hmEmpNameMap.get(strEmpId)+" has requested for Infrastructure Development " +
						infrastructureName+" of type "+infrastructureType+" from "+strFrom+" to "+strTo+" for "+strPurpose);
		
		
		return sb.toString();
	}
	
	private String addOthersContent(String strEmpId, String infrastructureName, String infrastructureType, String strFrom, String strTo, String strPurpose) {
		
		StringBuilder sb = new StringBuilder();
		
		
		sb.append("This is to certify that "+hmEmpNameMap.get(strEmpId)+" has requested for Infrastructure Development " +
				infrastructureName+" of type "+infrastructureType+" from "+strFrom+" to "+strTo+" for "+strPurpose);

		
		return sb.toString();
		
	}
	
	private void addCompanyInfo(Document document) {
		
		Image logoImage = null;
		
		try {
			
			logoImage = Image.getInstance(request.getRealPath("/userImages/logo_new.png"));
			
			
			
//			logoImage = Image.getInstance(request.getRealPath("/images1/"+CF.getStrOrgLogo()));
			
			logoImage.scaleToFit(200, 100);
			document.add(logoImage);
		}catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " + e.getMessage(), e);
		}
		
		try {
			
			Paragraph paragraph = new Paragraph(CF.getStrOrgName());
			paragraph.setAlignment(Element.ALIGN_CENTER);
			document.add(paragraph);
			
		}catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " + e.getMessage(), e);
		}
		
		
	}

	private void setTitle(Document document) {
		
		Paragraph paragraph = null;
		
		try	{
			
			if(getRequisitionType()!=null && getRequisitionType().equals("BF")) {
				paragraph = new Paragraph("BONAFIED CERTIFICATE", FontFactory.getFont("Verdana", "sans-serif", 20));
			
			}else if(getRequisitionType()!=null && getRequisitionType().equals("IR")) {
				paragraph = new Paragraph("INFRASTRUCTURE REQUEST", FontFactory.getFont("Verdana", "sans-serif", 20));
				
			}else {
				paragraph = new Paragraph("OTHER REQUISITION", FontFactory.getFont("Verdana", "sans-serif", 20));
			}
			
			paragraph.setAlignment(Element.ALIGN_CENTER);
			
			document.add(paragraph);
			paragraph = new Paragraph("		");
			document.add(paragraph);
			
		}catch(Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " + e.getMessage(), e);
		}
	}

	public void setServletResponse(HttpServletResponse response) {
		this.response = response;		
	}

	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
	}

	public int getRequisitionId() {
		return requisitionId;
	}

	public void setRequisitionId(int requisitionId) {
		this.requisitionId = requisitionId;
	}

	public String getRequisitionType() {
		return requisitionType;
	}

	public void setRequisitionType(String requisitionType) {
		this.requisitionType = requisitionType;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

}
