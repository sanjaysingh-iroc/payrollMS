package com.konnect.jpms.export;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.konnect.jpms.export.payroll.ExcelSheetDesign;
import com.konnect.jpms.master.AddLevel;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class ClientDetailExcelReport implements ServletRequestAware, ServletResponseAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(AddLevel.class);
	HttpSession session;
	CommonFunctions CF;
	
	public void execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);

		UtilityFunctions uF = new UtilityFunctions();
		generateExcelSheet(uF);
		  
	}
	
	
	
	private void generateExcelSheet(UtilityFunctions uF) {
			
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from client_details order by client_id asc");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst Client Details Excel Report ===>> " + pst);
			rs = pst.executeQuery();
			Map<String, List<String>> hmClientDetails = new LinkedHashMap<String,  List<String>>();
			List<String>alInner = new ArrayList<String>(); 
			while(rs.next()) {
								
				alInner = new ArrayList<String>();
				alInner.add( rs.getString("client_id"));//0
				alInner.add( rs.getString("client_name"));//1
				alInner.add( rs.getString("client_address"));//2
				alInner.add( rs.getString("client_city"));//3
				alInner.add( rs.getString("client_comp_description"));//4
				alInner.add( rs.getString("tds_percent"));//5
				alInner.add( rs.getString("registration_no"));//6
				alInner.add(rs.getString("website"));//7
			
				
				hmClientDetails.put(rs.getString("client_id"), alInner);
				
			}
			rs.close();
			pst.close();
//		System.out.println("hmclient----->"+hmClientDetails);
			
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("Client Details");
			
			List<DataStyle> header = new ArrayList<DataStyle>();
			header.add(new DataStyle("Client Details",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			
			header.add(new DataStyle("Sr. No.",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Client Id",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Client Name",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Client Address",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Client City",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Client Comp Description",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("TDS  Percent",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Registration No",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Web Site",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));

			
		  
			
		   	List<List<DataStyle>> reportData = new ArrayList<List<DataStyle>>();
		   	Iterator<String> it1 = hmClientDetails.keySet().iterator();
		   	int cnt = 0;
		   	while(it1.hasNext()) {
		   		String strClientId = it1.next();
		   		
		   		List<String> strClientDetails = hmClientDetails.get(strClientId);
		   		cnt++;
		   		
		   		List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
				alInnerExport.add(new DataStyle(uF.showData(""+cnt, ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(uF.showData(strClientDetails.get(0), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(uF.showData(strClientDetails.get(1), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(uF.showData(strClientDetails.get(2), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(uF.showData(strClientDetails.get(3), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(uF.showData(strClientDetails.get(4), "0"),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(uF.showData(strClientDetails.get(5), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(uF.showData(strClientDetails.get(6), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(uF.showData(strClientDetails.get(7), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
			   	reportData.add(alInnerExport);
		   		
		   	}
			
			ExcelSheetDesign sheetDesign = new ExcelSheetDesign();
			sheetDesign.getExcelSheetDesignData(workbook, sheet, header, reportData);

			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			workbook.write(buffer);
			response.setContentType("application/vnd.ms-excel:UTF-8");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition", "attachment; filename=ClientDetails.xls");
			ServletOutputStream out = response.getOutputStream(); 
			buffer.writeTo(out);
			out.flush();
			buffer.close();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);			
			db.closeConnection(con);
		}
	}
	
	HttpServletRequest request;
	HttpServletResponse response;

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
	}
	

}
