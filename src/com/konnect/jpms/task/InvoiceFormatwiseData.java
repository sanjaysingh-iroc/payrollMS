package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.itextpdf.text.Image;
import com.konnect.jpms.select.FillBank;
import com.konnect.jpms.select.FillClientAddress;
import com.konnect.jpms.select.FillCurrency;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.UtilityFunctions;

public class InvoiceFormatwiseData implements InvoiceFormatIConstants,IConstants,ServletRequestAware {
	 
	HttpSession session;
	CommonFunctions CF;
	UtilityFunctions uF;
	Connection con;
	HttpServletRequest request; 
	String strProId;
	
	public InvoiceFormatwiseData(HttpServletRequest request, HttpSession session, CommonFunctions CF, UtilityFunctions uF, Connection con, String strProId) {
		super();
		this.request = request;
		this.session = session;
		this.CF = CF;
		this.uF = uF;
		this.con = con;
		this.strProId = strProId;
	}
	
	String invoiceFormatId;
	String strSection1;	
	String strSection2;
	String strSection3;
	String strSection4;
	String strSection5;
	String strSection6;
	String strSection7;
	String strSection8;
	String strSection9;
	String strSection10;
	String strSection11;
	
	
public Map<String, String> getInvoiceFormatDataPDF(String invoiceId) {
		
		PreparedStatement pst = null;
		ResultSet rst = null;
		int x= 0;
		try {
			
			pst = con.prepareStatement("select * from projectmntnc where pro_id = ?");
			pst.setInt(1, uF.parseToInt(strProId));
//			System.out.println("pst ===>>>> " + pst);
			rst = pst.executeQuery();
			Map<String, String> hmProjectData = new HashMap<String, String>();
			while(rst.next()) {
				hmProjectData.put("CLIENT_ID", rst.getString("client_id"));
				hmProjectData.put("ORG_ID", rst.getString("org_id"));
				hmProjectData.put("WLOCATION_ID", rst.getString("wlocation_id"));
				hmProjectData.put("PROJECT_OWNER", rst.getString("project_owner"));
				hmProjectData.put("PROJECT_CURRENCY", rst.getString("billing_curr_id"));
				hmProjectData.put("CLIENT_SPOC", rst.getString("poc"));
				hmProjectData.put("INVOICE_FORMAT_ID", rst.getString("invoice_template_type"));
			}
			rst.close();
			pst.close();
			
			Map<String, String> hmSettingData = CF.getSettingsMap(con);
			if(hmSettingData != null) {
				setStrOrgLogo(hmSettingData.get(O_ORG_LOGO));
				setStrOrgName(hmSettingData.get(O_ORG_FULL_NAME));
				setStrOrgSubTitle(hmSettingData.get(O_ORG_SUB_TITLE));
				setStrOrgAddress(hmSettingData.get(O_ORG_FULL_ADDRESS));
				setStrOrgContactNo(hmSettingData.get(O_ORG_CONTACT_NO));
				setStrOrgFaxNo(hmSettingData.get(O_ORG_FAX_NO));
				setStrOrgEmailId(hmSettingData.get(O_ORG_EMAIL));
			}
			
			
			pst = con.prepareStatement("select * from client_details where client_id = ?");
			pst.setInt(1, uF.parseToInt(hmProjectData.get("CLIENT_ID")));
			rst = pst.executeQuery();
			while(rst.next()) {
				setStrCustomerName(rst.getString("client_name"));
			}
			rst.close();
			pst.close();
			
			pst = con.prepareStatement("select * from client_poc where poc_id = ?");
			pst.setInt(1, uF.parseToInt(hmProjectData.get("CLIENT_SPOC")));
			rst = pst.executeQuery();
			while(rst.next()) {
				setStrCustomerSPOC(uF.showData(rst.getString("contact_fname"), "") + " " + uF.showData(rst.getString("contact_lname"), ""));
				setStrCustomerContactNo(uF.showData(rst.getString("contact_number"), ""));
				setStrCustomerEmailId(uF.showData(rst.getString("contact_email"), ""));
			}
			rst.close();
			pst.close();
			
			
			StringBuilder sbWLocations = null;
			pst = con.prepareStatement("select * from work_location_info where org_id = ? order by wlocation_name");
			pst.setInt(1, uF.parseToInt(hmProjectData.get("ORG_ID")));
			rst = pst.executeQuery();
			while(rst.next()) {
				if(sbWLocations == null) {
					sbWLocations = new StringBuilder();
					sbWLocations.append(rst.getString("wlocation_name"));
				} else {
					sbWLocations.append(", "+rst.getString("wlocation_name"));
				}
			}
			if(sbWLocations == null) {
				sbWLocations = new StringBuilder();
			}	
			rst.close();
			pst.close();
			
			setStrWorkLocationsInLegalEntity(sbWLocations.toString());
			
			Map<String, String> hmOrgData = CF.getOrgDetails(con, uF, hmProjectData.get("ORG_ID"));
			if(hmOrgData != null && !hmOrgData.isEmpty()) {
				setStrLegalEntityName(hmOrgData.get("ORG_NAME"));
				setStrLegalEntitySubTitle(hmOrgData.get("ORG_SUB_TITLE"));
				setStrLegalEntityLogo(hmOrgData.get("ORG_LOGO"));
				setStrLegalEntityAddress(hmOrgData.get("ORG_ADDRESS"));
				setStrLegalEntityContactNo(hmOrgData.get("ORG_CONTACT"));
				setStrLegalEntityEmailId(hmOrgData.get("ORG_EMAIL"));
				setStrLegalEntityPanNo(hmOrgData.get("ORG_PAN_NO"));
				setStrLegalEntityTanNo(hmOrgData.get("ORG_TAN_NO"));
				setStrLegalEntityRegNo(hmOrgData.get("ORG_REG_NO"));
			}
//			
//			setStrLegalEntityECCNo();
			
			Map<String, Map<String, String>> hmWorkLocation = CF.getWorkLocationMap(con, uF.parseToInt(hmProjectData.get("ORG_ID")));
			Map<String, String> hmWLocationData = hmWorkLocation.get(hmProjectData.get("WLOCATION_ID"));
			
			if(hmWLocationData != null && !hmWLocationData.isEmpty()) {
				setStrWorkLocation(hmWLocationData.get("WL_NAME"));
				setStrWorkLocationAddress(hmWLocationData.get("WL_ADDRESS"));
				setStrWorkLocationContactNo(hmWLocationData.get("WL_CONTACT_NO"));
				setStrWorkLocationEmailId(hmWLocationData.get("WL_EMAIL_ID"));
				setStrWorkLocationFaxNo(hmWLocationData.get("WL_FAX_NO"));
				setStrWorkLocationECCNo1(hmWLocationData.get("WL_ECC1_NO"));
				setStrWorkLocationECCNo2(hmWLocationData.get("WL_ECC2_NO"));
			}

			Map<String, String> hmProOwnerData = CF.getEmpProfileDetail(con, request, session, CF, uF, null, hmProjectData.get("PROJECT_OWNER"));
			setStrProjectOwnerId(hmProjectData.get("PROJECT_OWNER"));
			setStrProjectOwnerName(hmProOwnerData.get("NAME"));
			setStrProjectOwnerDesig(hmProOwnerData.get("DESIGNATION_NAME"));
			setStrProjectOwnerContactNo(hmProOwnerData.get("CONTACT_MOB"));
			setStrProjectOwnerEmailId(hmProOwnerData.get("EMAIL_SEC"));
			
			setStrInvoiceGenerationDate(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, CF.getStrReportDateFormat()));
			setStrInvoiceNo("");
			
			
			pst = con.prepareStatement(" select * from promntc_invoice_details where pro_id = ? and promntc_invoice_id=?");
			pst.setInt(1, uF.parseToInt(strProId));
			pst.setInt(2, uF.parseToInt(invoiceId));
			rst = pst.executeQuery();
			Map<String, String> hmInvoiceDetails = new HashMap<String, String>();
			while (rst.next()) {
				hmInvoiceDetails.put("PROJECT_INVOICE_ID", rst.getString("promntc_invoice_id"));
				hmInvoiceDetails.put("INVOICE_DATE", uF.getDateFormat(rst.getString("invoice_generated_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInvoiceDetails.put("INVOICE_CODE", rst.getString("invoice_code"));
				hmInvoiceDetails.put("SPOC_ID", rst.getString("spoc_id"));
				hmInvoiceDetails.put("ADDRESS_ID", rst.getString("address_id"));
				hmInvoiceDetails.put("PRO_OWNER_ID", rst.getString("pro_owner_id"));
				hmInvoiceDetails.put("BANK_BRANCH_ID", rst.getString("bank_branch_id"));
				hmInvoiceDetails.put("CURR_ID", rst.getString("curr_id"));
			}
			rst.close();
			pst.close();
			
			setStrInvoiceNo(hmInvoiceDetails.get("INVOICE_CODE"));
			setStrInvoiceGenerationDate(hmInvoiceDetails.get("INVOICE_DATE"));
			
			Map<String, Map<String, String>> hmCurrencyDetails = CF.getCurrencyDetailsForPDF(con);
			Map<String, String> hmCurr = hmCurrencyDetails.get(hmInvoiceDetails.get("CURR_ID"));
			if (hmCurr == null)
				hmCurr = new HashMap<String, String>();
			String currency = hmCurr.get("SHORT_CURR") != null && !hmCurr.get("SHORT_CURR").equals("") ? " (" + hmCurr.get("SHORT_CURR") + ")" : "";
			
			setStrProjectCurrency(currency);
			
			pst = con.prepareStatement("select * from client_address where client_address_id = ?");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("ADDRESS_ID")));
			rst = pst.executeQuery();
			while(rst.next()) {
				setStrCustomerAddress(rst.getString("client_address"));
			}
			rst.close();
			pst.close();
			
			pst = con.prepareStatement("select bd1.bank_name,bd.branch_id,bd.branch_code,bd.bank_description,bd.bank_address,bd.bank_city,bd.bank_state_id,"
					+ "bd.bank_country_id,bd.bank_branch,bd.bank_email,bd.bank_fax,bd.bank_contact,bd.bank_ifsc_code,bd.bank_account_no,"
					+ " bd.bank_pincode,bd.bank_id,bd.swift_code from bank_details bd1, branch_details bd where bd1.bank_id = bd.bank_id "
					+ " and bd.branch_id=? order by bd1.bank_name,bd.branch_code");
			pst.setInt(1, uF.parseToInt(hmInvoiceDetails.get("BANK_BRANCH_ID")));
//			System.out.println("pst ==========>> " + pst);
			rst = pst.executeQuery();
			Map<String, String> hmBankBranch = new HashMap<String, String>();
			while (rst.next()) {
				hmBankBranch.put("BRANCH_BANK_NAME", rst.getString("bank_name"));
				hmBankBranch.put("BRANCH_ID", rst.getString("branch_id"));
				hmBankBranch.put("BRANCH_CODE", rst.getString("branch_code"));
				hmBankBranch.put("BRANCH_DESCRIPTION", rst.getString("bank_description"));
				hmBankBranch.put("BRANCH_ADDRESS", rst.getString("bank_address"));
				hmBankBranch.put("BRANCH_CITY", rst.getString("bank_city"));
				hmBankBranch.put("BRANCH_STATE_ID", rst.getString("bank_state_id"));
				hmBankBranch.put("BRANCH_COUNTRY_ID", rst.getString("bank_country_id"));
				hmBankBranch.put("BRANCH_BRANCH", rst.getString("bank_branch"));
				hmBankBranch.put("BRANCH_EMAIL", rst.getString("bank_email"));
				hmBankBranch.put("BRANCH_FAX", rst.getString("bank_fax"));
				hmBankBranch.put("BRANCH_CONTACT", rst.getString("bank_contact"));
				hmBankBranch.put("BRANCH_IFSC_CODE", rst.getString("bank_ifsc_code"));
				hmBankBranch.put("BRANCH_ACCOUNT_NO", rst.getString("bank_account_no"));
				hmBankBranch.put("BRANCH_PINCODE", rst.getString("bank_pincode"));
				hmBankBranch.put("BRANCH_BANK_ID", rst.getString("bank_id"));
				hmBankBranch.put("BRANCH_SWIFT_CODE", rst.getString("swift_code"));
			}
			rst.close();
			pst.close();
			
			setStrBankName(hmBankBranch.get("BRANCH_BANK_NAME"));
			setStrBranchName(hmBankBranch.get("BRANCH_BRANCH"));
			setStrBranchContactNo(hmBankBranch.get("BRANCH_CONTACT"));
			setStrBranchFaxNo(hmBankBranch.get("BRANCH_FAX"));
			setStrBranchEmailId(hmBankBranch.get("BRANCH_EMAIL"));
			setStrBranchACNo(hmBankBranch.get("BRANCH_ACCOUNT_NO"));
			setStrBranchIFSCCode(hmBankBranch.get("BRANCH_IFSC_CODE"));
			setStrBranchSwiftCode(hmBankBranch.get("BRANCH_SWIFT_CODE"));
			
			
			pst = con.prepareStatement("select * from invoice_formats where invoice_formats_id = ?");
			pst.setInt(1, uF.parseToInt(hmProjectData.get("INVOICE_FORMAT_ID")));
			rst = pst.executeQuery();
			while(rst.next()) {
				setStrSection1(rst.getString("section_1"));
				setStrSection2(rst.getString("section_2"));
				setStrSection3(rst.getString("section_3"));
				setStrSection4(rst.getString("section_4"));
				setStrSection5(rst.getString("section_5"));
				setStrSection6(rst.getString("section_6"));
				setStrSection7(rst.getString("section_7"));
				setStrSection8(rst.getString("section_8"));
				setStrSection9(rst.getString("section_9"));
				setStrSection10(rst.getString("section_10"));
				setStrSection11(rst.getString("section_11"));
				x++;
			}
			rst.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rst !=null){
				try {
					rst.close();
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
			if(con !=null) {
				try {
					if(!con.getAutoCommit()){
						con.commit();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		/*if(x == 0) {
			System.out.println("Notification constant not defined..");
			return x;
		}*/
		Map<String, String> hmInvoiceData = new LinkedHashMap<String, String>();
		if(x > 0) {
			hmInvoiceData = parseInvoiceFormatContentPDF(getStrSection1(), getStrSection2(), getStrSection3(), getStrSection4(), getStrSection5(), getStrSection6(), 
				getStrSection7(), getStrSection8(), getStrSection9(), getStrSection10(), getStrSection11());
		}
		return hmInvoiceData;
	}



	public Map<String, String> getInvoiceFormatData() {
		PreparedStatement pst = null;
		ResultSet rst = null;
		int x= 0;
		try {
			currencyList = new FillCurrency(request).fillCurrency();
			bankList = new FillBank(request).fillBankAccNo();
			
			pst = con.prepareStatement("select * from projectmntnc where pro_id = ?");
			pst.setInt(1, uF.parseToInt(strProId));
//			System.out.println("pst ===>>>> " + pst);
			rst = pst.executeQuery();
			Map<String, String> hmProjectData = new HashMap<String, String>();
			while(rst.next()) {
				hmProjectData.put("CLIENT_ID", rst.getString("client_id"));
				hmProjectData.put("ORG_ID", rst.getString("org_id"));
				hmProjectData.put("WLOCATION_ID", rst.getString("wlocation_id"));
				hmProjectData.put("PROJECT_OWNER", rst.getString("project_owner"));
				hmProjectData.put("PROJECT_CURRENCY", rst.getString("billing_curr_id"));
				hmProjectData.put("CLIENT_SPOC", rst.getString("poc"));
				hmProjectData.put("INVOICE_FORMAT_ID", rst.getString("invoice_template_type"));
			}
			rst.close();
			pst.close();
			
			clientAddressList = new FillClientAddress(request).fillClientAddress(hmProjectData.get("CLIENT_ID"), uF);
			
			Map<String, String> hmSettingData = CF.getSettingsMap(con);
			if(hmSettingData != null) {
				setStrOrgLogo(hmSettingData.get(O_ORG_LOGO));
				setStrOrgName(hmSettingData.get(O_ORG_FULL_NAME));
				setStrOrgSubTitle(hmSettingData.get(O_ORG_SUB_TITLE));
				setStrOrgAddress(hmSettingData.get(O_ORG_FULL_ADDRESS));
				setStrOrgContactNo(hmSettingData.get(O_ORG_CONTACT_NO));
				setStrOrgFaxNo(hmSettingData.get(O_ORG_FAX_NO));
				setStrOrgEmailId(hmSettingData.get(O_ORG_EMAIL));
			}
			
			
			pst = con.prepareStatement("select * from client_details where client_id = ?");
			pst.setInt(1, uF.parseToInt(hmProjectData.get("CLIENT_ID")));
			rst = pst.executeQuery();
			while(rst.next()) {
				setStrCustomerName(rst.getString("client_name"));
			}
			rst.close();
			pst.close();
			
			pst = con.prepareStatement("select * from client_poc where poc_id = ?");
			pst.setInt(1, uF.parseToInt(hmProjectData.get("CLIENT_SPOC")));
			rst = pst.executeQuery();
			while(rst.next()) {
				setStrCustomerSPOC(uF.showData(rst.getString("contact_fname"), "") + " " + uF.showData(rst.getString("contact_lname"), ""));
				setStrCustomerContactNo(uF.showData(rst.getString("contact_number"), ""));
				setStrCustomerEmailId(uF.showData(rst.getString("contact_email"), ""));
			}
			rst.close();
			pst.close();
			
			
			StringBuilder sbWLocations = null;
			pst = con.prepareStatement("select * from work_location_info where org_id = ? order by wlocation_name");
			pst.setInt(1, uF.parseToInt(hmProjectData.get("ORG_ID")));
			rst = pst.executeQuery();
			while(rst.next()) {
				if(sbWLocations == null) {
					sbWLocations = new StringBuilder();
					sbWLocations.append(rst.getString("wlocation_name"));
				} else {
					sbWLocations.append(", "+rst.getString("wlocation_name"));
				}
			}
			if(sbWLocations == null) {
				sbWLocations = new StringBuilder();
			}	
			rst.close();
			pst.close();
			
			setStrWorkLocationsInLegalEntity(sbWLocations.toString());
			
			Map<String, String> hmOrgData = CF.getOrgDetails(con, uF, hmProjectData.get("ORG_ID"));
			if(hmOrgData != null && !hmOrgData.isEmpty()) {
				setStrLegalEntityName(hmOrgData.get("ORG_NAME"));
				setStrLegalEntitySubTitle(hmOrgData.get("ORG_SUB_TITLE"));
				setStrLegalEntityLogo(hmOrgData.get("ORG_LOGO"));
				setStrLegalEntityAddress(hmOrgData.get("ORG_ADDRESS"));
				setStrLegalEntityContactNo(hmOrgData.get("ORG_CONTACT"));
				setStrLegalEntityEmailId(hmOrgData.get("ORG_EMAIL"));
				setStrLegalEntityPanNo(hmOrgData.get("ORG_PAN_NO"));
				setStrLegalEntityTanNo(hmOrgData.get("ORG_TAN_NO"));
				setStrLegalEntityRegNo(hmOrgData.get("ORG_REG_NO"));
			}
//			
//			setStrLegalEntityECCNo();
			
			Map<String, Map<String, String>> hmWorkLocation = CF.getWorkLocationMap(con, uF.parseToInt(hmProjectData.get("ORG_ID")));
			Map<String, String> hmWLocationData = hmWorkLocation.get(hmProjectData.get("WLOCATION_ID"));
			
			if(hmWLocationData != null && !hmWLocationData.isEmpty()) {
				setStrWorkLocation(hmWLocationData.get("WL_NAME"));
				setStrWorkLocationAddress(hmWLocationData.get("WL_ADDRESS"));
				setStrWorkLocationContactNo(hmWLocationData.get("WL_CONTACT_NO"));
				setStrWorkLocationEmailId(hmWLocationData.get("WL_EMAIL_ID"));
				setStrWorkLocationFaxNo(hmWLocationData.get("WL_FAX_NO"));
				setStrWorkLocationECCNo1(hmWLocationData.get("WL_ECC1_NO"));
				setStrWorkLocationECCNo2(hmWLocationData.get("WL_ECC2_NO"));
			}

			Map<String, String> hmProOwnerData = CF.getEmpProfileDetail(con, request, session, CF, uF, null, hmProjectData.get("PROJECT_OWNER"));
			setStrProjectOwnerId(hmProjectData.get("PROJECT_OWNER"));
			setStrProjectOwnerName(hmProOwnerData.get("NAME"));
			setStrProjectOwnerDesig(hmProOwnerData.get("DESIGNATION_NAME"));
			setStrProjectOwnerContactNo(hmProOwnerData.get("CONTACT_MOB"));
			setStrProjectOwnerEmailId(hmProOwnerData.get("EMAIL_SEC"));
			
			setStrProjectCurrency(hmProjectData.get("PROJECT_CURRENCY"));
			setStrInvoiceGenerationDate(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, CF.getStrReportDateFormat()));
			setStrInvoiceNo("");
			
			pst = con.prepareStatement("select * from invoice_formats where invoice_formats_id = ?");
			pst.setInt(1, uF.parseToInt(hmProjectData.get("INVOICE_FORMAT_ID")));
			rst = pst.executeQuery();
			while(rst.next()) {
				setStrSection1(rst.getString("section_1"));
				setStrSection2(rst.getString("section_2"));
				setStrSection3(rst.getString("section_3"));
				setStrSection4(rst.getString("section_4"));
				setStrSection5(rst.getString("section_5"));
				setStrSection6(rst.getString("section_6"));
				setStrSection7(rst.getString("section_7"));
				setStrSection8(rst.getString("section_8"));
				setStrSection9(rst.getString("section_9"));
				setStrSection10(rst.getString("section_10"));
				setStrSection11(rst.getString("section_11"));
				x++;
			}
			rst.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*if(x == 0) {
			System.out.println("Notification constant not defined..");
			return x;
		}*/
		Map<String, String> hmInvoiceData = new LinkedHashMap<String, String>();
		if(x > 0) {
			hmInvoiceData = parseInvoiceFormatContent(getStrSection1(), getStrSection2(), getStrSection3(), getStrSection4(), getStrSection5(), getStrSection6(), 
				getStrSection7(), getStrSection8(), getStrSection9(), getStrSection10(), getStrSection11());
		}
		return hmInvoiceData;
	}

	
	public Map<String, String> parseInvoiceFormatContentPDF(String strSection1, String strSection2, String strSection3, String strSection4, 
			String strSection5, String strSection6, String strSection7, String strSection8, String strSection9, String strSection10, 
			String strSection11) {
		
			if(getStrOrgLogo() != null) {
				Image imageLogo=null;
				String orgLogo = CF.getStrDocRetriveLocation()+I_COMPANY+"/"+I_IMAGE+"/"+getStrOrgLogo();
//				imageLogo = Image.getInstance(orgLogo);
				try{
					imageLogo = Image.getInstance(orgLogo);
				} catch(Exception e) {
				}
				strSection1 = strSection1.replace(IF_ORG_LOGO, imageLogo+"");
				strSection2 = strSection2.replace(IF_ORG_LOGO, imageLogo+"");
				strSection3 = strSection3.replace(IF_ORG_LOGO, imageLogo+"");
				strSection4 = strSection4.replace(IF_ORG_LOGO, imageLogo+"");
				strSection5 = strSection5.replace(IF_ORG_LOGO, imageLogo+"");
				strSection6 = strSection6.replace(IF_ORG_LOGO, imageLogo+"");
				strSection7 = strSection7.replace(IF_ORG_LOGO, imageLogo+"");
				strSection8 = strSection8.replace(IF_ORG_LOGO, imageLogo+"");
				strSection9 = strSection9.replace(IF_ORG_LOGO, imageLogo+"");
				strSection10 = strSection10.replace(IF_ORG_LOGO, imageLogo+"");
				strSection11 = strSection11.replace(IF_ORG_LOGO, imageLogo+"");
			}
			
			if(getStrLegalEntityLogo() != null) {
//				Image lEntityLogo=null;
//				String orgLogo = CF.getStrDocRetriveLocation()+I_ORGANISATION+"/"+I_IMAGE+"/"+getStrLegalEntityLogo();
//				try{
//					lEntityLogo = Image.getInstance(orgLogo);
//				} catch(Exception e) {
//				}
				String lEntityLogo = "<img src='"+CF.getStrDocRetriveLocation()+I_ORGANISATION+"/"+I_IMAGE+"/"+getStrLegalEntityLogo() +"' height=\"40\">";
				strSection1 = strSection1.replace(IF_LEGAL_ENTITY_LOGO, lEntityLogo+"");
				strSection2 = strSection2.replace(IF_LEGAL_ENTITY_LOGO, lEntityLogo+"");
				strSection3 = strSection3.replace(IF_LEGAL_ENTITY_LOGO, lEntityLogo+"");
				strSection4 = strSection4.replace(IF_LEGAL_ENTITY_LOGO, lEntityLogo+"");
				strSection5 = strSection5.replace(IF_LEGAL_ENTITY_LOGO, lEntityLogo+"");
				strSection6 = strSection6.replace(IF_LEGAL_ENTITY_LOGO, lEntityLogo+"");
				strSection7 = strSection7.replace(IF_LEGAL_ENTITY_LOGO, lEntityLogo+"");
				strSection8 = strSection8.replace(IF_LEGAL_ENTITY_LOGO, lEntityLogo+"");
				strSection9 = strSection9.replace(IF_LEGAL_ENTITY_LOGO, lEntityLogo+"");
				strSection10 = strSection10.replace(IF_LEGAL_ENTITY_LOGO, lEntityLogo+"");
				strSection11 = strSection11.replace(IF_LEGAL_ENTITY_LOGO, lEntityLogo+"");
			}
			
			if(getStrOrgName() != null) {
				strSection1 = strSection1.replace(IF_ORG_NAME, getStrOrgName());
				strSection2 = strSection2.replace(IF_ORG_NAME, getStrOrgName());
				strSection3 = strSection3.replace(IF_ORG_NAME, getStrOrgName());
				strSection4 = strSection4.replace(IF_ORG_NAME, getStrOrgName());
				strSection5 = strSection5.replace(IF_ORG_NAME, getStrOrgName());
				strSection6 = strSection6.replace(IF_ORG_NAME, getStrOrgName());
				strSection7 = strSection7.replace(IF_ORG_NAME, getStrOrgName());
				strSection8 = strSection8.replace(IF_ORG_NAME, getStrOrgName());
				strSection9 = strSection9.replace(IF_ORG_NAME, getStrOrgName());
				strSection10 = strSection10.replace(IF_ORG_NAME, getStrOrgName());
				strSection11 = strSection11.replace(IF_ORG_NAME, getStrOrgName());
			}
			
			if(getStrOrgSubTitle() != null) {
				strSection1 = strSection1.replace(IF_ORG_SUB_TITLE, getStrOrgSubTitle());
				strSection2 = strSection2.replace(IF_ORG_SUB_TITLE, getStrOrgSubTitle());
				strSection3 = strSection3.replace(IF_ORG_SUB_TITLE, getStrOrgSubTitle());
				strSection4 = strSection4.replace(IF_ORG_SUB_TITLE, getStrOrgSubTitle());
				strSection5 = strSection5.replace(IF_ORG_SUB_TITLE, getStrOrgSubTitle());
				strSection6 = strSection6.replace(IF_ORG_SUB_TITLE, getStrOrgSubTitle());
				strSection7 = strSection7.replace(IF_ORG_SUB_TITLE, getStrOrgSubTitle());
				strSection8 = strSection8.replace(IF_ORG_SUB_TITLE, getStrOrgSubTitle());
				strSection9 = strSection9.replace(IF_ORG_SUB_TITLE, getStrOrgSubTitle());
				strSection10 = strSection10.replace(IF_ORG_SUB_TITLE, getStrOrgSubTitle());
				strSection11 = strSection11.replace(IF_ORG_SUB_TITLE, getStrOrgSubTitle());
			}
			
			if(getStrOrgAddress() != null) {
				strSection1 = strSection1.replace(IF_ORG_ADDRESS, getStrOrgAddress());
				strSection2 = strSection2.replace(IF_ORG_ADDRESS, getStrOrgAddress());
				strSection3 = strSection3.replace(IF_ORG_ADDRESS, getStrOrgAddress());
				strSection4 = strSection4.replace(IF_ORG_ADDRESS, getStrOrgAddress());
				strSection5 = strSection5.replace(IF_ORG_ADDRESS, getStrOrgAddress());
				strSection6 = strSection6.replace(IF_ORG_ADDRESS, getStrOrgAddress());
				strSection7 = strSection7.replace(IF_ORG_ADDRESS, getStrOrgAddress());
				strSection8 = strSection8.replace(IF_ORG_ADDRESS, getStrOrgAddress());
				strSection9 = strSection9.replace(IF_ORG_ADDRESS, getStrOrgAddress());
				strSection10 = strSection10.replace(IF_ORG_ADDRESS, getStrOrgAddress());
				strSection11 = strSection11.replace(IF_ORG_ADDRESS, getStrOrgAddress());
			}
			
			if(getStrOrgContactNo() != null) {
				strSection1 = strSection1.replace(IF_ORG_CONTACT_NO, getStrOrgContactNo());
				strSection2 = strSection2.replace(IF_ORG_CONTACT_NO, getStrOrgContactNo());
				strSection3 = strSection3.replace(IF_ORG_CONTACT_NO, getStrOrgContactNo());
				strSection4 = strSection4.replace(IF_ORG_CONTACT_NO, getStrOrgContactNo());
				strSection5 = strSection5.replace(IF_ORG_CONTACT_NO, getStrOrgContactNo());
				strSection6 = strSection6.replace(IF_ORG_CONTACT_NO, getStrOrgContactNo());
				strSection7 = strSection7.replace(IF_ORG_CONTACT_NO, getStrOrgContactNo());
				strSection8 = strSection8.replace(IF_ORG_CONTACT_NO, getStrOrgContactNo());
				strSection9 = strSection9.replace(IF_ORG_CONTACT_NO, getStrOrgContactNo());
				strSection10 = strSection10.replace(IF_ORG_CONTACT_NO, getStrOrgContactNo());
				strSection11 = strSection11.replace(IF_ORG_CONTACT_NO, getStrOrgContactNo());
			}
			
			if(getStrOrgFaxNo() != null) {
				strSection1 = strSection1.replace(IF_ORG_FAX_NO, getStrOrgFaxNo());
				strSection2 = strSection2.replace(IF_ORG_FAX_NO, getStrOrgFaxNo());
				strSection3 = strSection3.replace(IF_ORG_FAX_NO, getStrOrgFaxNo());
				strSection4 = strSection4.replace(IF_ORG_FAX_NO, getStrOrgFaxNo());
				strSection5 = strSection5.replace(IF_ORG_FAX_NO, getStrOrgFaxNo());
				strSection6 = strSection6.replace(IF_ORG_FAX_NO, getStrOrgFaxNo());
				strSection7 = strSection7.replace(IF_ORG_FAX_NO, getStrOrgFaxNo());
				strSection8 = strSection8.replace(IF_ORG_FAX_NO, getStrOrgFaxNo());
				strSection9 = strSection9.replace(IF_ORG_FAX_NO, getStrOrgFaxNo());
				strSection10 = strSection10.replace(IF_ORG_FAX_NO, getStrOrgFaxNo());
				strSection11 = strSection11.replace(IF_ORG_FAX_NO, getStrOrgFaxNo());
			}
			
			if(getStrOrgEmailId() != null) {
				strSection1 = strSection1.replace(IF_ORG_EMAIL_ID, getStrOrgEmailId());
				strSection2 = strSection2.replace(IF_ORG_EMAIL_ID, getStrOrgEmailId());
				strSection3 = strSection3.replace(IF_ORG_EMAIL_ID, getStrOrgEmailId());
				strSection4 = strSection4.replace(IF_ORG_EMAIL_ID, getStrOrgEmailId());
				strSection5 = strSection5.replace(IF_ORG_EMAIL_ID, getStrOrgEmailId());
				strSection6 = strSection6.replace(IF_ORG_EMAIL_ID, getStrOrgEmailId());
				strSection7 = strSection7.replace(IF_ORG_EMAIL_ID, getStrOrgEmailId());
				strSection8 = strSection8.replace(IF_ORG_EMAIL_ID, getStrOrgEmailId());
				strSection9 = strSection9.replace(IF_ORG_EMAIL_ID, getStrOrgEmailId());
				strSection10 = strSection10.replace(IF_ORG_EMAIL_ID, getStrOrgEmailId());
				strSection11 = strSection11.replace(IF_ORG_EMAIL_ID, getStrOrgEmailId());
			}
			
			if(getStrLegalEntityName() != null) {
				strSection1 = strSection1.replace(IF_LEGAL_ENTITY_NAME, getStrLegalEntityName());
				strSection2 = strSection2.replace(IF_LEGAL_ENTITY_NAME, getStrLegalEntityName());
				strSection3 = strSection3.replace(IF_LEGAL_ENTITY_NAME, getStrLegalEntityName());
				strSection4 = strSection4.replace(IF_LEGAL_ENTITY_NAME, getStrLegalEntityName());
				strSection5 = strSection5.replace(IF_LEGAL_ENTITY_NAME, getStrLegalEntityName());
				strSection6 = strSection6.replace(IF_LEGAL_ENTITY_NAME, getStrLegalEntityName());
				strSection7 = strSection7.replace(IF_LEGAL_ENTITY_NAME, getStrLegalEntityName());
				strSection8 = strSection8.replace(IF_LEGAL_ENTITY_NAME, getStrLegalEntityName());
				strSection9 = strSection9.replace(IF_LEGAL_ENTITY_NAME, getStrLegalEntityName());
				strSection10 = strSection10.replace(IF_LEGAL_ENTITY_NAME, getStrLegalEntityName());
				strSection11 = strSection11.replace(IF_LEGAL_ENTITY_NAME, getStrLegalEntityName());
			}
			
			if(getStrLegalEntitySubTitle() != null) {
				strSection1 = strSection1.replace(IF_LEGAL_ENTITY_SUB_TITLE, getStrLegalEntitySubTitle());
				strSection2 = strSection2.replace(IF_LEGAL_ENTITY_SUB_TITLE, getStrLegalEntitySubTitle());
				strSection3 = strSection3.replace(IF_LEGAL_ENTITY_SUB_TITLE, getStrLegalEntitySubTitle());
				strSection4 = strSection4.replace(IF_LEGAL_ENTITY_SUB_TITLE, getStrLegalEntitySubTitle());
				strSection5 = strSection5.replace(IF_LEGAL_ENTITY_SUB_TITLE, getStrLegalEntitySubTitle());
				strSection6 = strSection6.replace(IF_LEGAL_ENTITY_SUB_TITLE, getStrLegalEntitySubTitle());
				strSection7 = strSection7.replace(IF_LEGAL_ENTITY_SUB_TITLE, getStrLegalEntitySubTitle());
				strSection8 = strSection8.replace(IF_LEGAL_ENTITY_SUB_TITLE, getStrLegalEntitySubTitle());
				strSection9 = strSection9.replace(IF_LEGAL_ENTITY_SUB_TITLE, getStrLegalEntitySubTitle());
				strSection10 = strSection10.replace(IF_LEGAL_ENTITY_SUB_TITLE, getStrLegalEntitySubTitle());
				strSection11 = strSection11.replace(IF_LEGAL_ENTITY_SUB_TITLE, getStrLegalEntitySubTitle());
			}
			
			if(getStrLegalEntityAddress() != null) {
				strSection1 = strSection1.replace(IF_LEGAL_ENTITY_ADDRESS, getStrLegalEntityAddress());
				strSection2 = strSection2.replace(IF_LEGAL_ENTITY_ADDRESS, getStrLegalEntityAddress());
				strSection3 = strSection3.replace(IF_LEGAL_ENTITY_ADDRESS, getStrLegalEntityAddress());
				strSection4 = strSection4.replace(IF_LEGAL_ENTITY_ADDRESS, getStrLegalEntityAddress());
				strSection5 = strSection5.replace(IF_LEGAL_ENTITY_ADDRESS, getStrLegalEntityAddress());
				strSection6 = strSection6.replace(IF_LEGAL_ENTITY_ADDRESS, getStrLegalEntityAddress());
				strSection7 = strSection7.replace(IF_LEGAL_ENTITY_ADDRESS, getStrLegalEntityAddress());
				strSection8 = strSection8.replace(IF_LEGAL_ENTITY_ADDRESS, getStrLegalEntityAddress());
				strSection9 = strSection9.replace(IF_LEGAL_ENTITY_ADDRESS, getStrLegalEntityAddress());
				strSection10 = strSection10.replace(IF_LEGAL_ENTITY_ADDRESS, getStrLegalEntityAddress());
				strSection11 = strSection11.replace(IF_LEGAL_ENTITY_ADDRESS, getStrLegalEntityAddress());
			}
			
			if(getStrLegalEntityContactNo() != null) {
				strSection1 = strSection1.replace(IF_LEGAL_ENTITY_CONTACT_NO, getStrLegalEntityContactNo());
				strSection2 = strSection2.replace(IF_LEGAL_ENTITY_CONTACT_NO, getStrLegalEntityContactNo());
				strSection3 = strSection3.replace(IF_LEGAL_ENTITY_CONTACT_NO, getStrLegalEntityContactNo());
				strSection4 = strSection4.replace(IF_LEGAL_ENTITY_CONTACT_NO, getStrLegalEntityContactNo());
				strSection5 = strSection5.replace(IF_LEGAL_ENTITY_CONTACT_NO, getStrLegalEntityContactNo());
				strSection6 = strSection6.replace(IF_LEGAL_ENTITY_CONTACT_NO, getStrLegalEntityContactNo());
				strSection7 = strSection7.replace(IF_LEGAL_ENTITY_CONTACT_NO, getStrLegalEntityContactNo());
				strSection8 = strSection8.replace(IF_LEGAL_ENTITY_CONTACT_NO, getStrLegalEntityContactNo());
				strSection9 = strSection9.replace(IF_LEGAL_ENTITY_CONTACT_NO, getStrLegalEntityContactNo());
				strSection10 = strSection10.replace(IF_LEGAL_ENTITY_CONTACT_NO, getStrLegalEntityContactNo());
				strSection11 = strSection11.replace(IF_LEGAL_ENTITY_CONTACT_NO, getStrLegalEntityContactNo());
			}
			
			
			if(getStrLegalEntityFaxNo() != null) {
				strSection1 = strSection1.replace(IF_LEGAL_ENTITY_FAX_NO, getStrLegalEntityFaxNo());
				strSection2 = strSection2.replace(IF_LEGAL_ENTITY_FAX_NO, getStrLegalEntityFaxNo());
				strSection3 = strSection3.replace(IF_LEGAL_ENTITY_FAX_NO, getStrLegalEntityFaxNo());
				strSection4 = strSection4.replace(IF_LEGAL_ENTITY_FAX_NO, getStrLegalEntityFaxNo());
				strSection5 = strSection5.replace(IF_LEGAL_ENTITY_FAX_NO, getStrLegalEntityFaxNo());
				strSection6 = strSection6.replace(IF_LEGAL_ENTITY_FAX_NO, getStrLegalEntityFaxNo());
				strSection7 = strSection7.replace(IF_LEGAL_ENTITY_FAX_NO, getStrLegalEntityFaxNo());
				strSection8 = strSection8.replace(IF_LEGAL_ENTITY_FAX_NO, getStrLegalEntityFaxNo());
				strSection9 = strSection9.replace(IF_LEGAL_ENTITY_FAX_NO, getStrLegalEntityFaxNo());
				strSection10 = strSection10.replace(IF_LEGAL_ENTITY_FAX_NO, getStrLegalEntityFaxNo());
				strSection11 = strSection11.replace(IF_LEGAL_ENTITY_FAX_NO, getStrLegalEntityFaxNo());
			}
			
			if(getStrLegalEntityEmailId() != null) {
				strSection1 = strSection1.replace(IF_LEGAL_ENTITY_EMAIL_ID, getStrLegalEntityEmailId());
				strSection2 = strSection2.replace(IF_LEGAL_ENTITY_EMAIL_ID, getStrLegalEntityEmailId());
				strSection3 = strSection3.replace(IF_LEGAL_ENTITY_EMAIL_ID, getStrLegalEntityEmailId());
				strSection4 = strSection4.replace(IF_LEGAL_ENTITY_EMAIL_ID, getStrLegalEntityEmailId());
				strSection5 = strSection5.replace(IF_LEGAL_ENTITY_EMAIL_ID, getStrLegalEntityEmailId());
				strSection6 = strSection6.replace(IF_LEGAL_ENTITY_EMAIL_ID, getStrLegalEntityEmailId());
				strSection7 = strSection7.replace(IF_LEGAL_ENTITY_EMAIL_ID, getStrLegalEntityEmailId());
				strSection8 = strSection8.replace(IF_LEGAL_ENTITY_EMAIL_ID, getStrLegalEntityEmailId());
				strSection9 = strSection9.replace(IF_LEGAL_ENTITY_EMAIL_ID, getStrLegalEntityEmailId());
				strSection10 = strSection10.replace(IF_LEGAL_ENTITY_EMAIL_ID, getStrLegalEntityEmailId());
				strSection11 = strSection11.replace(IF_LEGAL_ENTITY_EMAIL_ID, getStrLegalEntityEmailId());
			}
			
			if(getStrLegalEntityRegNo() != null) {
				strSection1 = strSection1.replace(IF_LEGAL_ENTITY_REG_NO, getStrLegalEntityRegNo());
				strSection2 = strSection2.replace(IF_LEGAL_ENTITY_REG_NO, getStrLegalEntityRegNo());
				strSection3 = strSection3.replace(IF_LEGAL_ENTITY_REG_NO, getStrLegalEntityRegNo());
				strSection4 = strSection4.replace(IF_LEGAL_ENTITY_REG_NO, getStrLegalEntityRegNo());
				strSection5 = strSection5.replace(IF_LEGAL_ENTITY_REG_NO, getStrLegalEntityRegNo());
				strSection6 = strSection6.replace(IF_LEGAL_ENTITY_REG_NO, getStrLegalEntityRegNo());
				strSection7 = strSection7.replace(IF_LEGAL_ENTITY_REG_NO, getStrLegalEntityRegNo());
				strSection8 = strSection8.replace(IF_LEGAL_ENTITY_REG_NO, getStrLegalEntityRegNo());
				strSection9 = strSection9.replace(IF_LEGAL_ENTITY_REG_NO, getStrLegalEntityRegNo());
				strSection10 = strSection10.replace(IF_LEGAL_ENTITY_REG_NO, getStrLegalEntityRegNo());
				strSection11 = strSection11.replace(IF_LEGAL_ENTITY_REG_NO, getStrLegalEntityRegNo());
			}
			
			if(getStrLegalEntityPanNo() != null) {
				strSection1 = strSection1.replace(IF_LEGAL_ENTITY_PAN_NO, getStrLegalEntityPanNo());
				strSection2 = strSection2.replace(IF_LEGAL_ENTITY_PAN_NO, getStrLegalEntityPanNo());
				strSection3 = strSection3.replace(IF_LEGAL_ENTITY_PAN_NO, getStrLegalEntityPanNo());
				strSection4 = strSection4.replace(IF_LEGAL_ENTITY_PAN_NO, getStrLegalEntityPanNo());
				strSection5 = strSection5.replace(IF_LEGAL_ENTITY_PAN_NO, getStrLegalEntityPanNo());
				strSection6 = strSection6.replace(IF_LEGAL_ENTITY_PAN_NO, getStrLegalEntityPanNo());
				strSection7 = strSection7.replace(IF_LEGAL_ENTITY_PAN_NO, getStrLegalEntityPanNo());
				strSection8 = strSection8.replace(IF_LEGAL_ENTITY_PAN_NO, getStrLegalEntityPanNo());
				strSection9 = strSection9.replace(IF_LEGAL_ENTITY_PAN_NO, getStrLegalEntityPanNo());
				strSection10 = strSection10.replace(IF_LEGAL_ENTITY_PAN_NO, getStrLegalEntityPanNo());
				strSection11 = strSection11.replace(IF_LEGAL_ENTITY_PAN_NO, getStrLegalEntityPanNo());
			}
			
			if(getStrLegalEntityTanNo() != null) {
				strSection1 = strSection1.replace(IF_LEGAL_ENTITY_TAN_NO, getStrLegalEntityTanNo());
				strSection2 = strSection2.replace(IF_LEGAL_ENTITY_TAN_NO, getStrLegalEntityTanNo());
				strSection3 = strSection3.replace(IF_LEGAL_ENTITY_TAN_NO, getStrLegalEntityTanNo());
				strSection4 = strSection4.replace(IF_LEGAL_ENTITY_TAN_NO, getStrLegalEntityTanNo());
				strSection5 = strSection5.replace(IF_LEGAL_ENTITY_TAN_NO, getStrLegalEntityTanNo());
				strSection6 = strSection6.replace(IF_LEGAL_ENTITY_TAN_NO, getStrLegalEntityTanNo());
				strSection7 = strSection7.replace(IF_LEGAL_ENTITY_TAN_NO, getStrLegalEntityTanNo());
				strSection8 = strSection8.replace(IF_LEGAL_ENTITY_TAN_NO, getStrLegalEntityTanNo());
				strSection9 = strSection9.replace(IF_LEGAL_ENTITY_TAN_NO, getStrLegalEntityTanNo());
				strSection10 = strSection10.replace(IF_LEGAL_ENTITY_TAN_NO, getStrLegalEntityTanNo());
				strSection11 = strSection11.replace(IF_LEGAL_ENTITY_TAN_NO, getStrLegalEntityTanNo());
			}
			
//			if(getStrLegalEntityECCNo() != null) {
//				strSection1 = strSection1.replace(IF_LEGAL_ENTITY_ECC_NO, getStrLegalEntityECCNo());
//				strSection2 = strSection2.replace(IF_LEGAL_ENTITY_ECC_NO, getStrLegalEntityECCNo());
//				strSection3 = strSection3.replace(IF_LEGAL_ENTITY_ECC_NO, getStrLegalEntityECCNo());
//				strSection4 = strSection4.replace(IF_LEGAL_ENTITY_ECC_NO, getStrLegalEntityECCNo());
//				strSection5 = strSection5.replace(IF_LEGAL_ENTITY_ECC_NO, getStrLegalEntityECCNo());
//				strSection6 = strSection6.replace(IF_LEGAL_ENTITY_ECC_NO, getStrLegalEntityECCNo());
//				strSection7 = strSection7.replace(IF_LEGAL_ENTITY_ECC_NO, getStrLegalEntityECCNo());
//				strSection8 = strSection8.replace(IF_LEGAL_ENTITY_ECC_NO, getStrLegalEntityECCNo());
//				strSection9 = strSection9.replace(IF_LEGAL_ENTITY_ECC_NO, getStrLegalEntityECCNo());
//				strSection10 = strSection10.replace(IF_LEGAL_ENTITY_ECC_NO, getStrLegalEntityECCNo());
//				strSection11 = strSection11.replace(IF_LEGAL_ENTITY_ECC_NO, getStrLegalEntityECCNo());
//			}
			
			if(getStrWorkLocation() != null) {
				strSection1 = strSection1.replace(IF_WORK_LOCATION, getStrWorkLocation());
				strSection2 = strSection2.replace(IF_WORK_LOCATION, getStrWorkLocation());
				strSection3 = strSection3.replace(IF_WORK_LOCATION, getStrWorkLocation());
				strSection4 = strSection4.replace(IF_WORK_LOCATION, getStrWorkLocation());
				strSection5 = strSection5.replace(IF_WORK_LOCATION, getStrWorkLocation());
				strSection6 = strSection6.replace(IF_WORK_LOCATION, getStrWorkLocation());
				strSection7 = strSection7.replace(IF_WORK_LOCATION, getStrWorkLocation());
				strSection8 = strSection8.replace(IF_WORK_LOCATION, getStrWorkLocation());
				strSection9 = strSection9.replace(IF_WORK_LOCATION, getStrWorkLocation());
				strSection10 = strSection10.replace(IF_WORK_LOCATION, getStrWorkLocation());
				strSection11 = strSection11.replace(IF_WORK_LOCATION, getStrWorkLocation());
			}
			
			if(getStrWorkLocationAddress() != null) {
				strSection1 = strSection1.replace(IF_WORK_LOCATION_ADDRESS, getStrWorkLocationAddress());
				strSection2 = strSection2.replace(IF_WORK_LOCATION_ADDRESS, getStrWorkLocationAddress());
				strSection3 = strSection3.replace(IF_WORK_LOCATION_ADDRESS, getStrWorkLocationAddress());
				strSection4 = strSection4.replace(IF_WORK_LOCATION_ADDRESS, getStrWorkLocationAddress());
				strSection5 = strSection5.replace(IF_WORK_LOCATION_ADDRESS, getStrWorkLocationAddress());
				strSection6 = strSection6.replace(IF_WORK_LOCATION_ADDRESS, getStrWorkLocationAddress());
				strSection7 = strSection7.replace(IF_WORK_LOCATION_ADDRESS, getStrWorkLocationAddress());
				strSection8 = strSection8.replace(IF_WORK_LOCATION_ADDRESS, getStrWorkLocationAddress());
				strSection9 = strSection9.replace(IF_WORK_LOCATION_ADDRESS, getStrWorkLocationAddress());
				strSection10 = strSection10.replace(IF_WORK_LOCATION_ADDRESS, getStrWorkLocationAddress());
				strSection11 = strSection11.replace(IF_WORK_LOCATION_ADDRESS, getStrWorkLocationAddress());
			}
			
			if(getStrWorkLocationContactNo() != null) {
				strSection1 = strSection1.replace(IF_WORK_LOCATION_CONTACT_NO, getStrWorkLocationContactNo());
				strSection2 = strSection2.replace(IF_WORK_LOCATION_CONTACT_NO, getStrWorkLocationContactNo());
				strSection3 = strSection3.replace(IF_WORK_LOCATION_CONTACT_NO, getStrWorkLocationContactNo());
				strSection4 = strSection4.replace(IF_WORK_LOCATION_CONTACT_NO, getStrWorkLocationContactNo());
				strSection5 = strSection5.replace(IF_WORK_LOCATION_CONTACT_NO, getStrWorkLocationContactNo());
				strSection6 = strSection6.replace(IF_WORK_LOCATION_CONTACT_NO, getStrWorkLocationContactNo());
				strSection7 = strSection7.replace(IF_WORK_LOCATION_CONTACT_NO, getStrWorkLocationContactNo());
				strSection8 = strSection8.replace(IF_WORK_LOCATION_CONTACT_NO, getStrWorkLocationContactNo());
				strSection9 = strSection9.replace(IF_WORK_LOCATION_CONTACT_NO, getStrWorkLocationContactNo());
				strSection10 = strSection10.replace(IF_WORK_LOCATION_CONTACT_NO, getStrWorkLocationContactNo());
				strSection11 = strSection11.replace(IF_WORK_LOCATION_CONTACT_NO, getStrWorkLocationContactNo());
			}
			
			if(getStrWorkLocationFaxNo() != null) {
				strSection1 = strSection1.replace(IF_WORK_LOCATION_FAX_NO, getStrWorkLocationFaxNo());
				strSection2 = strSection2.replace(IF_WORK_LOCATION_FAX_NO, getStrWorkLocationFaxNo());
				strSection3 = strSection3.replace(IF_WORK_LOCATION_FAX_NO, getStrWorkLocationFaxNo());
				strSection4 = strSection4.replace(IF_WORK_LOCATION_FAX_NO, getStrWorkLocationFaxNo());
				strSection5 = strSection5.replace(IF_WORK_LOCATION_FAX_NO, getStrWorkLocationFaxNo());
				strSection6 = strSection6.replace(IF_WORK_LOCATION_FAX_NO, getStrWorkLocationFaxNo());
				strSection7 = strSection7.replace(IF_WORK_LOCATION_FAX_NO, getStrWorkLocationFaxNo());
				strSection8 = strSection8.replace(IF_WORK_LOCATION_FAX_NO, getStrWorkLocationFaxNo());
				strSection9 = strSection9.replace(IF_WORK_LOCATION_FAX_NO, getStrWorkLocationFaxNo());
				strSection10 = strSection10.replace(IF_WORK_LOCATION_FAX_NO, getStrWorkLocationFaxNo());
				strSection11 = strSection11.replace(IF_WORK_LOCATION_FAX_NO, getStrWorkLocationFaxNo());
			}
			
			if(getStrWorkLocationECCNo1() != null) {
				strSection1 = strSection1.replace(IF_WORK_LOCATION_ECC_NO1, getStrWorkLocationECCNo1());
				strSection2 = strSection2.replace(IF_WORK_LOCATION_ECC_NO1, getStrWorkLocationECCNo1());
				strSection3 = strSection3.replace(IF_WORK_LOCATION_ECC_NO1, getStrWorkLocationECCNo1());
				strSection4 = strSection4.replace(IF_WORK_LOCATION_ECC_NO1, getStrWorkLocationECCNo1());
				strSection5 = strSection5.replace(IF_WORK_LOCATION_ECC_NO1, getStrWorkLocationECCNo1());
				strSection6 = strSection6.replace(IF_WORK_LOCATION_ECC_NO1, getStrWorkLocationECCNo1());
				strSection7 = strSection7.replace(IF_WORK_LOCATION_ECC_NO1, getStrWorkLocationECCNo1());
				strSection8 = strSection8.replace(IF_WORK_LOCATION_ECC_NO1, getStrWorkLocationECCNo1());
				strSection9 = strSection9.replace(IF_WORK_LOCATION_ECC_NO1, getStrWorkLocationECCNo1());
				strSection10 = strSection10.replace(IF_WORK_LOCATION_ECC_NO1, getStrWorkLocationECCNo1());
				strSection11 = strSection11.replace(IF_WORK_LOCATION_ECC_NO1, getStrWorkLocationECCNo1());
			}
			
			if(getStrWorkLocationECCNo2() != null) {
				strSection1 = strSection1.replace(IF_WORK_LOCATION_ECC_NO2, getStrWorkLocationECCNo2());
				strSection2 = strSection2.replace(IF_WORK_LOCATION_ECC_NO2, getStrWorkLocationECCNo2());
				strSection3 = strSection3.replace(IF_WORK_LOCATION_ECC_NO2, getStrWorkLocationECCNo2());
				strSection4 = strSection4.replace(IF_WORK_LOCATION_ECC_NO2, getStrWorkLocationECCNo2());
				strSection5 = strSection5.replace(IF_WORK_LOCATION_ECC_NO2, getStrWorkLocationECCNo2());
				strSection6 = strSection6.replace(IF_WORK_LOCATION_ECC_NO2, getStrWorkLocationECCNo2());
				strSection7 = strSection7.replace(IF_WORK_LOCATION_ECC_NO2, getStrWorkLocationECCNo2());
				strSection8 = strSection8.replace(IF_WORK_LOCATION_ECC_NO2, getStrWorkLocationECCNo2());
				strSection9 = strSection9.replace(IF_WORK_LOCATION_ECC_NO2, getStrWorkLocationECCNo2());
				strSection10 = strSection10.replace(IF_WORK_LOCATION_ECC_NO2, getStrWorkLocationECCNo2());
				strSection11 = strSection11.replace(IF_WORK_LOCATION_ECC_NO2, getStrWorkLocationECCNo2());
			}
			
			
			if(getStrWorkLocationEmailId() != null) {
				strSection1 = strSection1.replace(IF_WORK_LOCATION_EMAIL_ID, getStrWorkLocationEmailId());
				strSection2 = strSection2.replace(IF_WORK_LOCATION_EMAIL_ID, getStrWorkLocationEmailId());
				strSection3 = strSection3.replace(IF_WORK_LOCATION_EMAIL_ID, getStrWorkLocationEmailId());
				strSection4 = strSection4.replace(IF_WORK_LOCATION_EMAIL_ID, getStrWorkLocationEmailId());
				strSection5 = strSection5.replace(IF_WORK_LOCATION_EMAIL_ID, getStrWorkLocationEmailId());
				strSection6 = strSection6.replace(IF_WORK_LOCATION_EMAIL_ID, getStrWorkLocationEmailId());
				strSection7 = strSection7.replace(IF_WORK_LOCATION_EMAIL_ID, getStrWorkLocationEmailId());
				strSection8 = strSection8.replace(IF_WORK_LOCATION_EMAIL_ID, getStrWorkLocationEmailId());
				strSection9 = strSection9.replace(IF_WORK_LOCATION_EMAIL_ID, getStrWorkLocationEmailId());
				strSection10 = strSection10.replace(IF_WORK_LOCATION_EMAIL_ID, getStrWorkLocationEmailId());
				strSection11 = strSection11.replace(IF_WORK_LOCATION_EMAIL_ID, getStrWorkLocationEmailId());
			}
			
			if(getStrWorkLocationsInLegalEntity() != null) {
				strSection1 = strSection1.replace(IF_WORK_LOCATIONS_IN_LEGAL_ENTITY, getStrWorkLocationsInLegalEntity());
				strSection2 = strSection2.replace(IF_WORK_LOCATIONS_IN_LEGAL_ENTITY, getStrWorkLocationsInLegalEntity());
				strSection3 = strSection3.replace(IF_WORK_LOCATIONS_IN_LEGAL_ENTITY, getStrWorkLocationsInLegalEntity());
				strSection4 = strSection4.replace(IF_WORK_LOCATIONS_IN_LEGAL_ENTITY, getStrWorkLocationsInLegalEntity());
				strSection5 = strSection5.replace(IF_WORK_LOCATIONS_IN_LEGAL_ENTITY, getStrWorkLocationsInLegalEntity());
				strSection6 = strSection6.replace(IF_WORK_LOCATIONS_IN_LEGAL_ENTITY, getStrWorkLocationsInLegalEntity());
				strSection7 = strSection7.replace(IF_WORK_LOCATIONS_IN_LEGAL_ENTITY, getStrWorkLocationsInLegalEntity());
				strSection8 = strSection8.replace(IF_WORK_LOCATIONS_IN_LEGAL_ENTITY, getStrWorkLocationsInLegalEntity());
				strSection9 = strSection9.replace(IF_WORK_LOCATIONS_IN_LEGAL_ENTITY, getStrWorkLocationsInLegalEntity());
				strSection10 = strSection10.replace(IF_WORK_LOCATIONS_IN_LEGAL_ENTITY, getStrWorkLocationsInLegalEntity());
				strSection11 = strSection11.replace(IF_WORK_LOCATIONS_IN_LEGAL_ENTITY, getStrWorkLocationsInLegalEntity());
			}
			
			if(getStrProjectOwnerName() != null) {
//				String projectOwner = getStrProjectOwnerName()+"<input type=\"hidden\" name=\"strProjectOwner\" value=\""+getStrProjectOwnerId()+"\">";
				strSection1 = strSection1.replace(IF_PROJECT_OWNER_NAME, getStrProjectOwnerName());
				strSection2 = strSection2.replace(IF_PROJECT_OWNER_NAME, getStrProjectOwnerName());
				strSection3 = strSection3.replace(IF_PROJECT_OWNER_NAME, getStrProjectOwnerName());
				strSection4 = strSection4.replace(IF_PROJECT_OWNER_NAME, getStrProjectOwnerName());
				strSection5 = strSection5.replace(IF_PROJECT_OWNER_NAME, getStrProjectOwnerName());
				strSection6 = strSection6.replace(IF_PROJECT_OWNER_NAME, getStrProjectOwnerName());
				strSection7 = strSection7.replace(IF_PROJECT_OWNER_NAME, getStrProjectOwnerName());
				strSection8 = strSection8.replace(IF_PROJECT_OWNER_NAME, getStrProjectOwnerName());
				strSection9 = strSection9.replace(IF_PROJECT_OWNER_NAME, getStrProjectOwnerName());
				strSection10 = strSection10.replace(IF_PROJECT_OWNER_NAME, getStrProjectOwnerName());
				strSection11 = strSection11.replace(IF_PROJECT_OWNER_NAME, getStrProjectOwnerName());
			}
			
			if(getStrProjectOwnerDesig() != null) {
				strSection1 = strSection1.replace(IF_PROJECT_OWNER_DESIG, getStrProjectOwnerDesig());
				strSection2 = strSection2.replace(IF_PROJECT_OWNER_DESIG, getStrProjectOwnerDesig());
				strSection3 = strSection3.replace(IF_PROJECT_OWNER_DESIG, getStrProjectOwnerDesig());
				strSection4 = strSection4.replace(IF_PROJECT_OWNER_DESIG, getStrProjectOwnerDesig());
				strSection5 = strSection5.replace(IF_PROJECT_OWNER_DESIG, getStrProjectOwnerDesig());
				strSection6 = strSection6.replace(IF_PROJECT_OWNER_DESIG, getStrProjectOwnerDesig());
				strSection7 = strSection7.replace(IF_PROJECT_OWNER_DESIG, getStrProjectOwnerDesig());
				strSection8 = strSection8.replace(IF_PROJECT_OWNER_DESIG, getStrProjectOwnerDesig());
				strSection9 = strSection9.replace(IF_PROJECT_OWNER_DESIG, getStrProjectOwnerDesig());
				strSection10 = strSection10.replace(IF_PROJECT_OWNER_DESIG, getStrProjectOwnerDesig());
				strSection11 = strSection11.replace(IF_PROJECT_OWNER_DESIG, getStrProjectOwnerDesig());
			}
			
			if(getStrProjectOwnerContactNo() != null) {
				strSection1 = strSection1.replace(IF_PROJECT_OWNER_CONTACT_NO, getStrProjectOwnerContactNo());
				strSection2 = strSection2.replace(IF_PROJECT_OWNER_CONTACT_NO, getStrProjectOwnerContactNo());
				strSection3 = strSection3.replace(IF_PROJECT_OWNER_CONTACT_NO, getStrProjectOwnerContactNo());
				strSection4 = strSection4.replace(IF_PROJECT_OWNER_CONTACT_NO, getStrProjectOwnerContactNo());
				strSection5 = strSection5.replace(IF_PROJECT_OWNER_CONTACT_NO, getStrProjectOwnerContactNo());
				strSection6 = strSection6.replace(IF_PROJECT_OWNER_CONTACT_NO, getStrProjectOwnerContactNo());
				strSection7 = strSection7.replace(IF_PROJECT_OWNER_CONTACT_NO, getStrProjectOwnerContactNo());
				strSection8 = strSection8.replace(IF_PROJECT_OWNER_CONTACT_NO, getStrProjectOwnerContactNo());
				strSection9 = strSection9.replace(IF_PROJECT_OWNER_CONTACT_NO, getStrProjectOwnerContactNo());
				strSection10 = strSection10.replace(IF_PROJECT_OWNER_CONTACT_NO, getStrProjectOwnerContactNo());
				strSection11 = strSection11.replace(IF_PROJECT_OWNER_CONTACT_NO, getStrProjectOwnerContactNo());
			}
			
			if(getStrProjectOwnerEmailId() != null) {
				strSection1 = strSection1.replace(IF_PROJECT_OWNER_EMAIL_ID, getStrProjectOwnerEmailId());
				strSection2 = strSection2.replace(IF_PROJECT_OWNER_EMAIL_ID, getStrProjectOwnerEmailId());
				strSection3 = strSection3.replace(IF_PROJECT_OWNER_EMAIL_ID, getStrProjectOwnerEmailId());
				strSection4 = strSection4.replace(IF_PROJECT_OWNER_EMAIL_ID, getStrProjectOwnerEmailId());
				strSection5 = strSection5.replace(IF_PROJECT_OWNER_EMAIL_ID, getStrProjectOwnerEmailId());
				strSection6 = strSection6.replace(IF_PROJECT_OWNER_EMAIL_ID, getStrProjectOwnerEmailId());
				strSection7 = strSection7.replace(IF_PROJECT_OWNER_EMAIL_ID, getStrProjectOwnerEmailId());
				strSection8 = strSection8.replace(IF_PROJECT_OWNER_EMAIL_ID, getStrProjectOwnerEmailId());
				strSection9 = strSection9.replace(IF_PROJECT_OWNER_EMAIL_ID, getStrProjectOwnerEmailId());
				strSection10 = strSection10.replace(IF_PROJECT_OWNER_EMAIL_ID, getStrProjectOwnerEmailId());
				strSection11 = strSection11.replace(IF_PROJECT_OWNER_EMAIL_ID, getStrProjectOwnerEmailId());
			}
			
			if(getStrHRName() != null) {
				strSection1 = strSection1.replace(IF_HR, getStrHRName());
				strSection2 = strSection2.replace(IF_HR, getStrHRName());
				strSection3 = strSection3.replace(IF_HR, getStrHRName());
				strSection4 = strSection4.replace(IF_HR, getStrHRName());
				strSection5 = strSection5.replace(IF_HR, getStrHRName());
				strSection6 = strSection6.replace(IF_HR, getStrHRName());
				strSection7 = strSection7.replace(IF_HR, getStrHRName());
				strSection8 = strSection8.replace(IF_HR, getStrHRName());
				strSection9 = strSection9.replace(IF_HR, getStrHRName());
				strSection10 = strSection10.replace(IF_HR, getStrHRName());
				strSection11 = strSection11.replace(IF_HR, getStrHRName());
			}
			
			if(getStrHRContactNo() != null) {
				strSection1 = strSection1.replace(IF_HR_CONTACT_NO, getStrHRContactNo());
				strSection2 = strSection2.replace(IF_HR_CONTACT_NO, getStrHRContactNo());
				strSection3 = strSection3.replace(IF_HR_CONTACT_NO, getStrHRContactNo());
				strSection4 = strSection4.replace(IF_HR_CONTACT_NO, getStrHRContactNo());
				strSection5 = strSection5.replace(IF_HR_CONTACT_NO, getStrHRContactNo());
				strSection6 = strSection6.replace(IF_HR_CONTACT_NO, getStrHRContactNo());
				strSection7 = strSection7.replace(IF_HR_CONTACT_NO, getStrHRContactNo());
				strSection8 = strSection8.replace(IF_HR_CONTACT_NO, getStrHRContactNo());
				strSection9 = strSection9.replace(IF_HR_CONTACT_NO, getStrHRContactNo());
				strSection10 = strSection10.replace(IF_HR_CONTACT_NO, getStrHRContactNo());
				strSection11 = strSection11.replace(IF_HR_CONTACT_NO, getStrHRContactNo());
			}
			
			if(getStrHREmailId() != null) {
				strSection1 = strSection1.replace(IF_HR_EMAIL_ID, getStrHREmailId());
				strSection2 = strSection2.replace(IF_HR_EMAIL_ID, getStrHREmailId());
				strSection3 = strSection3.replace(IF_HR_EMAIL_ID, getStrHREmailId());
				strSection4 = strSection4.replace(IF_HR_EMAIL_ID, getStrHREmailId());
				strSection5 = strSection5.replace(IF_HR_EMAIL_ID, getStrHREmailId());
				strSection6 = strSection6.replace(IF_HR_EMAIL_ID, getStrHREmailId());
				strSection7 = strSection7.replace(IF_HR_EMAIL_ID, getStrHREmailId());
				strSection8 = strSection8.replace(IF_HR_EMAIL_ID, getStrHREmailId());
				strSection9 = strSection9.replace(IF_HR_EMAIL_ID, getStrHREmailId());
				strSection10 = strSection10.replace(IF_HR_EMAIL_ID, getStrHREmailId());
				strSection11 = strSection11.replace(IF_HR_EMAIL_ID, getStrHREmailId());
			}
			
			if(getStrCustomerName() != null) {
				strSection1 = strSection1.replace(IF_CUSTOMER_NAME, getStrCustomerName());
				strSection2 = strSection2.replace(IF_CUSTOMER_NAME, getStrCustomerName());
				strSection3 = strSection3.replace(IF_CUSTOMER_NAME, getStrCustomerName());
				strSection4 = strSection4.replace(IF_CUSTOMER_NAME, getStrCustomerName());
				strSection5 = strSection5.replace(IF_CUSTOMER_NAME, getStrCustomerName());
				strSection6 = strSection6.replace(IF_CUSTOMER_NAME, getStrCustomerName());
				strSection7 = strSection7.replace(IF_CUSTOMER_NAME, getStrCustomerName());
				strSection8 = strSection8.replace(IF_CUSTOMER_NAME, getStrCustomerName());
				strSection9 = strSection9.replace(IF_CUSTOMER_NAME, getStrCustomerName());
				strSection10 = strSection10.replace(IF_CUSTOMER_NAME, getStrCustomerName());
				strSection11 = strSection11.replace(IF_CUSTOMER_NAME, getStrCustomerName());
			}
			
			if(getStrCustomerAddress() != null) {
//				StringBuilder custAddress = new StringBuilder();
//				custAddress.append("<select name=\"clientAddress\" id=\"clientAddress\" style=\"width: 200px; margin-bottom: 5px;\" class=\"validate[required]\">" +
//						"<option value=\"\">Select Address</option>");
//				for(int i=0; clientAddressList != null && !clientAddressList.isEmpty() && i<clientAddressList.size(); i++) {
//					if(i==0) {
//						custAddress.append("<option value="+clientAddressList.get(i).getClientAddressId()+" selected>"+clientAddressList.get(i).getClientAddress()+"</option>");
//					} else {
//						custAddress.append("<option value="+clientAddressList.get(i).getClientAddressId()+">"+clientAddressList.get(i).getClientAddress()+"</option>");
//					}
//				}
//				custAddress.append("</select>");
				strSection1 = strSection1.replace(IF_CUSTOMER_ADDRESS, getStrCustomerAddress());
				strSection2 = strSection2.replace(IF_CUSTOMER_ADDRESS, getStrCustomerAddress());
				strSection3 = strSection3.replace(IF_CUSTOMER_ADDRESS, getStrCustomerAddress());
				strSection4 = strSection4.replace(IF_CUSTOMER_ADDRESS, getStrCustomerAddress());
				strSection5 = strSection5.replace(IF_CUSTOMER_ADDRESS, getStrCustomerAddress());
				strSection6 = strSection6.replace(IF_CUSTOMER_ADDRESS, getStrCustomerAddress());
				strSection7 = strSection7.replace(IF_CUSTOMER_ADDRESS, getStrCustomerAddress());
				strSection8 = strSection8.replace(IF_CUSTOMER_ADDRESS, getStrCustomerAddress());
				strSection9 = strSection9.replace(IF_CUSTOMER_ADDRESS, getStrCustomerAddress());
				strSection10 = strSection10.replace(IF_CUSTOMER_ADDRESS, getStrCustomerAddress());
				strSection11 = strSection11.replace(IF_CUSTOMER_ADDRESS, getStrCustomerAddress());
			}
			
			if(getStrCustomerSPOC() != null) {
				strSection1 = strSection1.replace(IF_CUSTOMER_SPOC, getStrCustomerSPOC());
				strSection2 = strSection2.replace(IF_CUSTOMER_SPOC, getStrCustomerSPOC());
				strSection3 = strSection3.replace(IF_CUSTOMER_SPOC, getStrCustomerSPOC());
				strSection4 = strSection4.replace(IF_CUSTOMER_SPOC, getStrCustomerSPOC());
				strSection5 = strSection5.replace(IF_CUSTOMER_SPOC, getStrCustomerSPOC());
				strSection6 = strSection6.replace(IF_CUSTOMER_SPOC, getStrCustomerSPOC());
				strSection7 = strSection7.replace(IF_CUSTOMER_SPOC, getStrCustomerSPOC());
				strSection8 = strSection8.replace(IF_CUSTOMER_SPOC, getStrCustomerSPOC());
				strSection9 = strSection9.replace(IF_CUSTOMER_SPOC, getStrCustomerSPOC());
				strSection10 = strSection10.replace(IF_CUSTOMER_SPOC, getStrCustomerSPOC());
				strSection11 = strSection11.replace(IF_CUSTOMER_SPOC, getStrCustomerSPOC());
			}
			
			if(getStrCustomerContactNo() != null) {
				strSection1 = strSection1.replace(IF_CUSTOMER_CONTACT_NO, getStrCustomerContactNo());
				strSection2 = strSection2.replace(IF_CUSTOMER_CONTACT_NO, getStrCustomerContactNo());
				strSection3 = strSection3.replace(IF_CUSTOMER_CONTACT_NO, getStrCustomerContactNo());
				strSection4 = strSection4.replace(IF_CUSTOMER_CONTACT_NO, getStrCustomerContactNo());
				strSection5 = strSection5.replace(IF_CUSTOMER_CONTACT_NO, getStrCustomerContactNo());
				strSection6 = strSection6.replace(IF_CUSTOMER_CONTACT_NO, getStrCustomerContactNo());
				strSection7 = strSection7.replace(IF_CUSTOMER_CONTACT_NO, getStrCustomerContactNo());
				strSection8 = strSection8.replace(IF_CUSTOMER_CONTACT_NO, getStrCustomerContactNo());
				strSection9 = strSection9.replace(IF_CUSTOMER_CONTACT_NO, getStrCustomerContactNo());
				strSection10 = strSection10.replace(IF_CUSTOMER_CONTACT_NO, getStrCustomerContactNo());
				strSection11 = strSection11.replace(IF_CUSTOMER_CONTACT_NO, getStrCustomerContactNo());
			}
			
			if(getStrCustomerFaxNo() != null) {
				strSection1 = strSection1.replace(IF_CUSTOMER_FAX_NO, getStrCustomerFaxNo());
				strSection2 = strSection2.replace(IF_CUSTOMER_FAX_NO, getStrCustomerFaxNo());
				strSection3 = strSection3.replace(IF_CUSTOMER_FAX_NO, getStrCustomerFaxNo());
				strSection4 = strSection4.replace(IF_CUSTOMER_FAX_NO, getStrCustomerFaxNo());
				strSection5 = strSection5.replace(IF_CUSTOMER_FAX_NO, getStrCustomerFaxNo());
				strSection6 = strSection6.replace(IF_CUSTOMER_FAX_NO, getStrCustomerFaxNo());
				strSection7 = strSection7.replace(IF_CUSTOMER_FAX_NO, getStrCustomerFaxNo());
				strSection8 = strSection8.replace(IF_CUSTOMER_FAX_NO, getStrCustomerFaxNo());
				strSection9 = strSection9.replace(IF_CUSTOMER_FAX_NO, getStrCustomerFaxNo());
				strSection10 = strSection10.replace(IF_CUSTOMER_FAX_NO, getStrCustomerFaxNo());
				strSection11 = strSection11.replace(IF_CUSTOMER_FAX_NO, getStrCustomerFaxNo());
			}
			
			if(getStrCustomerEmailId() != null) {
				strSection1 = strSection1.replace(IF_CUSTOMER_EMAIL_ID, getStrCustomerEmailId());
				strSection2 = strSection2.replace(IF_CUSTOMER_EMAIL_ID, getStrCustomerEmailId());
				strSection3 = strSection3.replace(IF_CUSTOMER_EMAIL_ID, getStrCustomerEmailId());
				strSection4 = strSection4.replace(IF_CUSTOMER_EMAIL_ID, getStrCustomerEmailId());
				strSection5 = strSection5.replace(IF_CUSTOMER_EMAIL_ID, getStrCustomerEmailId());
				strSection6 = strSection6.replace(IF_CUSTOMER_EMAIL_ID, getStrCustomerEmailId());
				strSection7 = strSection7.replace(IF_CUSTOMER_EMAIL_ID, getStrCustomerEmailId());
				strSection8 = strSection8.replace(IF_CUSTOMER_EMAIL_ID, getStrCustomerEmailId());
				strSection9 = strSection9.replace(IF_CUSTOMER_EMAIL_ID, getStrCustomerEmailId());
				strSection10 = strSection10.replace(IF_CUSTOMER_EMAIL_ID, getStrCustomerEmailId());
				strSection11 = strSection11.replace(IF_CUSTOMER_EMAIL_ID, getStrCustomerEmailId());
			}
			
			if(getStrBankName() != null) {
//				StringBuilder bankNames = new StringBuilder();
//				bankNames.append("<select name=\"clientAddress\" id=\"clientAddress\" style=\"width: 200px;\" class=\"validate[required]\">" +
//						"<option value=\"\">Select Address</option>");
//				for(int i=0; bankList != null && !bankList.isEmpty() && i<bankList.size(); i++) {
//					if(i==0) {
//						bankNames.append("<option value="+bankList.get(i).getBankId()+" selected>"+bankList.get(i).getBankName()+"</option>");
//					} else {
//						bankNames.append("<option value="+bankList.get(i).getBankId()+">"+bankList.get(i).getBankName()+"</option>");
//					}
//				}
//				bankNames.append("</select>");
				strSection1 = strSection1.replace(IF_BANK_NAME, getStrBankName());
				strSection2 = strSection2.replace(IF_BANK_NAME, getStrBankName());
				strSection3 = strSection3.replace(IF_BANK_NAME, getStrBankName());
				strSection4 = strSection4.replace(IF_BANK_NAME, getStrBankName());
				strSection5 = strSection5.replace(IF_BANK_NAME, getStrBankName());
				strSection6 = strSection6.replace(IF_BANK_NAME, getStrBankName());
				strSection7 = strSection7.replace(IF_BANK_NAME, getStrBankName());
				strSection8 = strSection8.replace(IF_BANK_NAME, getStrBankName());
				strSection9 = strSection9.replace(IF_BANK_NAME, getStrBankName());
				strSection10 = strSection10.replace(IF_BANK_NAME, getStrBankName());
				strSection11 = strSection11.replace(IF_BANK_NAME, getStrBankName());
			}
			
			if(getStrBranchName() != null) {
				strSection1 = strSection1.replace(IF_BRANCH_NAME, getStrBranchName());
				strSection2 = strSection2.replace(IF_BRANCH_NAME, getStrBranchName());
				strSection3 = strSection3.replace(IF_BRANCH_NAME, getStrBranchName());
				strSection4 = strSection4.replace(IF_BRANCH_NAME, getStrBranchName());
				strSection5 = strSection5.replace(IF_BRANCH_NAME, getStrBranchName());
				strSection6 = strSection6.replace(IF_BRANCH_NAME, getStrBranchName());
				strSection7 = strSection7.replace(IF_BRANCH_NAME, getStrBranchName());
				strSection8 = strSection8.replace(IF_BRANCH_NAME, getStrBranchName());
				strSection9 = strSection9.replace(IF_BRANCH_NAME, getStrBranchName());
				strSection10 = strSection10.replace(IF_BRANCH_NAME, getStrBranchName());
				strSection11 = strSection11.replace(IF_BRANCH_NAME, getStrBranchName());
			}
			
			if(getStrBranchAddress() != null) {
				strSection1 = strSection1.replace(IF_BRANCH_ADDRESS, getStrBranchAddress());
				strSection2 = strSection2.replace(IF_BRANCH_ADDRESS, getStrBranchAddress());
				strSection3 = strSection3.replace(IF_BRANCH_ADDRESS, getStrBranchAddress());
				strSection4 = strSection4.replace(IF_BRANCH_ADDRESS, getStrBranchAddress());
				strSection5 = strSection5.replace(IF_BRANCH_ADDRESS, getStrBranchAddress());
				strSection6 = strSection6.replace(IF_BRANCH_ADDRESS, getStrBranchAddress());
				strSection7 = strSection7.replace(IF_BRANCH_ADDRESS, getStrBranchAddress());
				strSection8 = strSection8.replace(IF_BRANCH_ADDRESS, getStrBranchAddress());
				strSection9 = strSection9.replace(IF_BRANCH_ADDRESS, getStrBranchAddress());
				strSection10 = strSection10.replace(IF_BRANCH_ADDRESS, getStrBranchAddress());
				strSection11 = strSection11.replace(IF_BRANCH_ADDRESS, getStrBranchAddress());
			}
			
			if(getStrBranchContactNo() != null) {
				strSection1 = strSection1.replace(IF_BRANCH_CONTACT_NO, getStrBranchContactNo());
				strSection2 = strSection2.replace(IF_BRANCH_CONTACT_NO, getStrBranchContactNo());
				strSection3 = strSection3.replace(IF_BRANCH_CONTACT_NO, getStrBranchContactNo());
				strSection4 = strSection4.replace(IF_BRANCH_CONTACT_NO, getStrBranchContactNo());
				strSection5 = strSection5.replace(IF_BRANCH_CONTACT_NO, getStrBranchContactNo());
				strSection6 = strSection6.replace(IF_BRANCH_CONTACT_NO, getStrBranchContactNo());
				strSection7 = strSection7.replace(IF_BRANCH_CONTACT_NO, getStrBranchContactNo());
				strSection8 = strSection8.replace(IF_BRANCH_CONTACT_NO, getStrBranchContactNo());
				strSection9 = strSection9.replace(IF_BRANCH_CONTACT_NO, getStrBranchContactNo());
				strSection10 = strSection10.replace(IF_BRANCH_CONTACT_NO, getStrBranchContactNo());
				strSection11 = strSection11.replace(IF_BRANCH_CONTACT_NO, getStrBranchContactNo());
			}
			
			if(getStrBranchFaxNo() != null) {
				strSection1 = strSection1.replace(IF_BRANCH_FAX_NO, getStrBranchFaxNo());
				strSection2 = strSection2.replace(IF_BRANCH_FAX_NO, getStrBranchFaxNo());
				strSection3 = strSection3.replace(IF_BRANCH_FAX_NO, getStrBranchFaxNo());
				strSection4 = strSection4.replace(IF_BRANCH_FAX_NO, getStrBranchFaxNo());
				strSection5 = strSection5.replace(IF_BRANCH_FAX_NO, getStrBranchFaxNo());
				strSection6 = strSection6.replace(IF_BRANCH_FAX_NO, getStrBranchFaxNo());
				strSection7 = strSection7.replace(IF_BRANCH_FAX_NO, getStrBranchFaxNo());
				strSection8 = strSection8.replace(IF_BRANCH_FAX_NO, getStrBranchFaxNo());
				strSection9 = strSection9.replace(IF_BRANCH_FAX_NO, getStrBranchFaxNo());
				strSection10 = strSection10.replace(IF_BRANCH_FAX_NO, getStrBranchFaxNo());
				strSection11 = strSection11.replace(IF_BRANCH_FAX_NO, getStrBranchFaxNo());
			}
			
			if(getStrBranchEmailId() != null) {
				strSection1 = strSection1.replace(IF_BRANCH_EMAIL_ID, getStrBranchEmailId());
				strSection2 = strSection2.replace(IF_BRANCH_EMAIL_ID, getStrBranchEmailId());
				strSection3 = strSection3.replace(IF_BRANCH_EMAIL_ID, getStrBranchEmailId());
				strSection4 = strSection4.replace(IF_BRANCH_EMAIL_ID, getStrBranchEmailId());
				strSection5 = strSection5.replace(IF_BRANCH_EMAIL_ID, getStrBranchEmailId());
				strSection6 = strSection6.replace(IF_BRANCH_EMAIL_ID, getStrBranchEmailId());
				strSection7 = strSection7.replace(IF_BRANCH_EMAIL_ID, getStrBranchEmailId());
				strSection8 = strSection8.replace(IF_BRANCH_EMAIL_ID, getStrBranchEmailId());
				strSection9 = strSection9.replace(IF_BRANCH_EMAIL_ID, getStrBranchEmailId());
				strSection10 = strSection10.replace(IF_BRANCH_EMAIL_ID, getStrBranchEmailId());
				strSection11 = strSection11.replace(IF_BRANCH_EMAIL_ID, getStrBranchEmailId());
			}
			
			
			if(getStrBranchACNo() != null) {
				strSection1 = strSection1.replace(IF_BRANCH_AC_NO, getStrBranchACNo());
				strSection2 = strSection2.replace(IF_BRANCH_AC_NO, getStrBranchACNo());
				strSection3 = strSection3.replace(IF_BRANCH_AC_NO, getStrBranchACNo());
				strSection4 = strSection4.replace(IF_BRANCH_AC_NO, getStrBranchACNo());
				strSection5 = strSection5.replace(IF_BRANCH_AC_NO, getStrBranchACNo());
				strSection6 = strSection6.replace(IF_BRANCH_AC_NO, getStrBranchACNo());
				strSection7 = strSection7.replace(IF_BRANCH_AC_NO, getStrBranchACNo());
				strSection8 = strSection8.replace(IF_BRANCH_AC_NO, getStrBranchACNo());
				strSection9 = strSection9.replace(IF_BRANCH_AC_NO, getStrBranchACNo());
				strSection10 = strSection10.replace(IF_BRANCH_AC_NO, getStrBranchACNo());
				strSection11 = strSection11.replace(IF_BRANCH_AC_NO, getStrBranchACNo());
			}
			
			if(getStrBranchIFSCCode() != null) {
				strSection1 = strSection1.replace(IF_BRANCH_IFSC_CODE, getStrBranchIFSCCode());
				strSection2 = strSection2.replace(IF_BRANCH_IFSC_CODE, getStrBranchIFSCCode());
				strSection3 = strSection3.replace(IF_BRANCH_IFSC_CODE, getStrBranchIFSCCode());
				strSection4 = strSection4.replace(IF_BRANCH_IFSC_CODE, getStrBranchIFSCCode());
				strSection5 = strSection5.replace(IF_BRANCH_IFSC_CODE, getStrBranchIFSCCode());
				strSection6 = strSection6.replace(IF_BRANCH_IFSC_CODE, getStrBranchIFSCCode());
				strSection7 = strSection7.replace(IF_BRANCH_IFSC_CODE, getStrBranchIFSCCode());
				strSection8 = strSection8.replace(IF_BRANCH_IFSC_CODE, getStrBranchIFSCCode());
				strSection9 = strSection9.replace(IF_BRANCH_IFSC_CODE, getStrBranchIFSCCode());
				strSection10 = strSection10.replace(IF_BRANCH_IFSC_CODE, getStrBranchIFSCCode());
				strSection11 = strSection11.replace(IF_BRANCH_IFSC_CODE, getStrBranchIFSCCode());
			}
			
			if(getStrBranchSwiftCode() != null) {
				strSection1 = strSection1.replace(IF_BRANCH_SWIFT_CODE, getStrBranchSwiftCode());
				strSection2 = strSection2.replace(IF_BRANCH_SWIFT_CODE, getStrBranchSwiftCode());
				strSection3 = strSection3.replace(IF_BRANCH_SWIFT_CODE, getStrBranchSwiftCode());
				strSection4 = strSection4.replace(IF_BRANCH_SWIFT_CODE, getStrBranchSwiftCode());
				strSection5 = strSection5.replace(IF_BRANCH_SWIFT_CODE, getStrBranchSwiftCode());
				strSection6 = strSection6.replace(IF_BRANCH_SWIFT_CODE, getStrBranchSwiftCode());
				strSection7 = strSection7.replace(IF_BRANCH_SWIFT_CODE, getStrBranchSwiftCode());
				strSection8 = strSection8.replace(IF_BRANCH_SWIFT_CODE, getStrBranchSwiftCode());
				strSection9 = strSection9.replace(IF_BRANCH_SWIFT_CODE, getStrBranchSwiftCode());
				strSection10 = strSection10.replace(IF_BRANCH_SWIFT_CODE, getStrBranchSwiftCode());
				strSection11 = strSection11.replace(IF_BRANCH_SWIFT_CODE, getStrBranchSwiftCode());
			}
			
			if(getStrProjectCurrency() != null) {
//				StringBuilder projectCurrency = new StringBuilder();
//				projectCurrency.append("<div style=\"float: left;\"><input type=\"hidden\" name=\"proCurrency\" id=\"proCurrency\" value=\""+getStrProjectCurrency()+"\"/>" +
//					"<select onchange=\"getExchangeValue(this.value);\" style=\"width: 200px;\" class=\"validate[required]\" id=\"strCurrency\" name=\"strCurrency\">" +
//					"<option value=\"\">Select Currency</option>");
//				for(int i=0; currencyList != null && !currencyList.isEmpty() && i<currencyList.size(); i++) {
//					if(currencyList.get(i).getCurrencyId().equals(getStrProjectCurrency())) {
//						projectCurrency.append("<option value="+currencyList.get(i).getCurrencyId()+" selected>"+currencyList.get(i).getCurrencyName()+"</option>");
//					} else {
//						projectCurrency.append("<option value="+currencyList.get(i).getCurrencyId()+">"+currencyList.get(i).getCurrencyName()+"</option>");
//					}
//				}
//				projectCurrency.append("</select></div>");
//				projectCurrency.append("<div id=\"exchangeValDIV\" style=\"float: left; margin-left: 10px; margin-top: -10px;\"> </div>");
				
				strSection1 = strSection1.replace(IF_PROJECT_CURRENCY, "");
				strSection2 = strSection2.replace(IF_PROJECT_CURRENCY, "");
				strSection3 = strSection3.replace(IF_PROJECT_CURRENCY, "");
				strSection4 = strSection4.replace(IF_PROJECT_CURRENCY, "");
				strSection5 = strSection5.replace(IF_PROJECT_CURRENCY, "");
				strSection6 = strSection6.replace(IF_PROJECT_CURRENCY, "");
				strSection7 = strSection7.replace(IF_PROJECT_CURRENCY, "");
				strSection8 = strSection8.replace(IF_PROJECT_CURRENCY, "");
				strSection9 = strSection9.replace(IF_PROJECT_CURRENCY, "");
				strSection10 = strSection10.replace(IF_PROJECT_CURRENCY, "");
				strSection11 = strSection11.replace(IF_PROJECT_CURRENCY, "");
			}
			
			if(getStrInvoiceNo() != null) {
				strSection1 = strSection1.replace(IF_INVOICE_NO, getStrInvoiceNo());
				strSection2 = strSection2.replace(IF_INVOICE_NO, getStrInvoiceNo());
				strSection3 = strSection3.replace(IF_INVOICE_NO, getStrInvoiceNo());
				strSection4 = strSection4.replace(IF_INVOICE_NO, getStrInvoiceNo());
				strSection5 = strSection5.replace(IF_INVOICE_NO, getStrInvoiceNo());
				strSection6 = strSection6.replace(IF_INVOICE_NO, getStrInvoiceNo());
				strSection7 = strSection7.replace(IF_INVOICE_NO, getStrInvoiceNo());
				strSection8 = strSection8.replace(IF_INVOICE_NO, getStrInvoiceNo());
				strSection9 = strSection9.replace(IF_INVOICE_NO, getStrInvoiceNo());
				strSection10 = strSection10.replace(IF_INVOICE_NO, getStrInvoiceNo());
				strSection11 = strSection11.replace(IF_INVOICE_NO, getStrInvoiceNo());
			}
			
			if(getStrInvoiceGenerationDate() != null) {
				strSection1 = strSection1.replace(IF_INVOICE_GENERATION_DATE, getStrInvoiceGenerationDate());
				strSection2 = strSection2.replace(IF_INVOICE_GENERATION_DATE, getStrInvoiceGenerationDate());
				strSection3 = strSection3.replace(IF_INVOICE_GENERATION_DATE, getStrInvoiceGenerationDate());
				strSection4 = strSection4.replace(IF_INVOICE_GENERATION_DATE, getStrInvoiceGenerationDate());
				strSection5 = strSection5.replace(IF_INVOICE_GENERATION_DATE, getStrInvoiceGenerationDate());
				strSection6 = strSection6.replace(IF_INVOICE_GENERATION_DATE, getStrInvoiceGenerationDate());
				strSection7 = strSection7.replace(IF_INVOICE_GENERATION_DATE, getStrInvoiceGenerationDate());
				strSection8 = strSection8.replace(IF_INVOICE_GENERATION_DATE, getStrInvoiceGenerationDate());
				strSection9 = strSection9.replace(IF_INVOICE_GENERATION_DATE, getStrInvoiceGenerationDate());
				strSection10 = strSection10.replace(IF_INVOICE_GENERATION_DATE, getStrInvoiceGenerationDate());
				strSection11 = strSection11.replace(IF_INVOICE_GENERATION_DATE, getStrInvoiceGenerationDate());
			}
			
//			strSection1 = strSection1.replace("\n", "<br/>");
//			strSection2 = strSection2.replace("\n", "<br/>");
//			strSection3 = strSection3.replace("\n", "<br/>");
//			strSection4 = strSection4.replace("\n", "<br/>");
//			strSection5 = strSection5.replace("\n", "<br/>");
//			strSection6 = strSection6.replace("\n", "<br/>");
//			strSection7 = strSection7.replace("\n", "<br/>");
//			strSection8 = strSection8.replace("\n", "<br/>");
//			strSection9 = strSection9.replace("\n", "<br/>");
//			strSection10 = strSection10.replace("\n", "<br/>");
//			strSection11 = strSection11.replace("\n", "<br/>");
			
			setStrSection1(strSection1);
			setStrSection2(strSection2);
			setStrSection3(strSection3);
			setStrSection4(strSection4);
			setStrSection5(strSection5);
			setStrSection6(strSection6);
			setStrSection7(strSection7);
			setStrSection8(strSection8);
			setStrSection9(strSection9);
			setStrSection10(strSection10);
			setStrSection11(strSection11);
			
//		}


		Map<String, String> hmParsedContent = new LinkedHashMap<String, String>();
		hmParsedContent.put("SECTION_1", strSection1);
		hmParsedContent.put("SECTION_2", strSection2);
		hmParsedContent.put("SECTION_3", strSection3);
		hmParsedContent.put("SECTION_4", strSection4);
		hmParsedContent.put("SECTION_5", strSection5);
		hmParsedContent.put("SECTION_6", strSection6);
		hmParsedContent.put("SECTION_7", strSection7);
		hmParsedContent.put("SECTION_8", strSection8);
		hmParsedContent.put("SECTION_9", strSection9);
		hmParsedContent.put("SECTION_10", strSection10);
		hmParsedContent.put("SECTION_11", strSection11);
		
//		System.out.println("hmParsedContent ===>> " + hmParsedContent);
		return hmParsedContent;
	}
	
	
	public Map<String, String> parseInvoiceFormatContent(String strSection1, String strSection2, String strSection3, String strSection4, 
			String strSection5, String strSection6, String strSection7, String strSection8, String strSection9, String strSection10, 
			String strSection11) {
		
			if(getStrOrgLogo() != null) {
				String orgLogo = "<img src='"+CF.getStrDocRetriveLocation()+I_COMPANY+"/"+I_IMAGE+"/"+getStrOrgLogo() +"' height=\"40\">"; //style='height:60px' 
				strSection1 = strSection1.replace(IF_ORG_LOGO, orgLogo);
				strSection2 = strSection2.replace(IF_ORG_LOGO, orgLogo);
				strSection3 = strSection3.replace(IF_ORG_LOGO, orgLogo);
				strSection4 = strSection4.replace(IF_ORG_LOGO, orgLogo);
				strSection5 = strSection5.replace(IF_ORG_LOGO, orgLogo);
				strSection6 = strSection6.replace(IF_ORG_LOGO, orgLogo);
				strSection7 = strSection7.replace(IF_ORG_LOGO, orgLogo);
				strSection8 = strSection8.replace(IF_ORG_LOGO, orgLogo);
				strSection9 = strSection9.replace(IF_ORG_LOGO, orgLogo);
				strSection10 = strSection10.replace(IF_ORG_LOGO, orgLogo);
				strSection11 = strSection11.replace(IF_ORG_LOGO, orgLogo);
			}
			
			if(getStrLegalEntityLogo() != null) {
				String lEntityLogo = "<img src='"+CF.getStrDocRetriveLocation()+I_ORGANISATION+"/"+I_IMAGE+"/"+getStrLegalEntityLogo() +"' height=\"40\">";
				strSection1 = strSection1.replace(IF_LEGAL_ENTITY_LOGO, lEntityLogo);
				strSection2 = strSection2.replace(IF_LEGAL_ENTITY_LOGO, lEntityLogo);
				strSection3 = strSection3.replace(IF_LEGAL_ENTITY_LOGO, lEntityLogo);
				strSection4 = strSection4.replace(IF_LEGAL_ENTITY_LOGO, lEntityLogo);
				strSection5 = strSection5.replace(IF_LEGAL_ENTITY_LOGO, lEntityLogo);
				strSection6 = strSection6.replace(IF_LEGAL_ENTITY_LOGO, lEntityLogo);
				strSection7 = strSection7.replace(IF_LEGAL_ENTITY_LOGO, lEntityLogo);
				strSection8 = strSection8.replace(IF_LEGAL_ENTITY_LOGO, lEntityLogo);
				strSection9 = strSection9.replace(IF_LEGAL_ENTITY_LOGO, lEntityLogo);
				strSection10 = strSection10.replace(IF_LEGAL_ENTITY_LOGO, lEntityLogo);
				strSection11 = strSection11.replace(IF_LEGAL_ENTITY_LOGO, lEntityLogo);
			}
			
			if(getStrOrgName() != null) {
				strSection1 = strSection1.replace(IF_ORG_NAME, getStrOrgName());
				strSection2 = strSection2.replace(IF_ORG_NAME, getStrOrgName());
				strSection3 = strSection3.replace(IF_ORG_NAME, getStrOrgName());
				strSection4 = strSection4.replace(IF_ORG_NAME, getStrOrgName());
				strSection5 = strSection5.replace(IF_ORG_NAME, getStrOrgName());
				strSection6 = strSection6.replace(IF_ORG_NAME, getStrOrgName());
				strSection7 = strSection7.replace(IF_ORG_NAME, getStrOrgName());
				strSection8 = strSection8.replace(IF_ORG_NAME, getStrOrgName());
				strSection9 = strSection9.replace(IF_ORG_NAME, getStrOrgName());
				strSection10 = strSection10.replace(IF_ORG_NAME, getStrOrgName());
				strSection11 = strSection11.replace(IF_ORG_NAME, getStrOrgName());
			}
			
			if(getStrOrgSubTitle() != null) {
				strSection1 = strSection1.replace(IF_ORG_SUB_TITLE, getStrOrgSubTitle());
				strSection2 = strSection2.replace(IF_ORG_SUB_TITLE, getStrOrgSubTitle());
				strSection3 = strSection3.replace(IF_ORG_SUB_TITLE, getStrOrgSubTitle());
				strSection4 = strSection4.replace(IF_ORG_SUB_TITLE, getStrOrgSubTitle());
				strSection5 = strSection5.replace(IF_ORG_SUB_TITLE, getStrOrgSubTitle());
				strSection6 = strSection6.replace(IF_ORG_SUB_TITLE, getStrOrgSubTitle());
				strSection7 = strSection7.replace(IF_ORG_SUB_TITLE, getStrOrgSubTitle());
				strSection8 = strSection8.replace(IF_ORG_SUB_TITLE, getStrOrgSubTitle());
				strSection9 = strSection9.replace(IF_ORG_SUB_TITLE, getStrOrgSubTitle());
				strSection10 = strSection10.replace(IF_ORG_SUB_TITLE, getStrOrgSubTitle());
				strSection11 = strSection11.replace(IF_ORG_SUB_TITLE, getStrOrgSubTitle());
			}
			
			if(getStrOrgAddress() != null) {
				strSection1 = strSection1.replace(IF_ORG_ADDRESS, getStrOrgAddress());
				strSection2 = strSection2.replace(IF_ORG_ADDRESS, getStrOrgAddress());
				strSection3 = strSection3.replace(IF_ORG_ADDRESS, getStrOrgAddress());
				strSection4 = strSection4.replace(IF_ORG_ADDRESS, getStrOrgAddress());
				strSection5 = strSection5.replace(IF_ORG_ADDRESS, getStrOrgAddress());
				strSection6 = strSection6.replace(IF_ORG_ADDRESS, getStrOrgAddress());
				strSection7 = strSection7.replace(IF_ORG_ADDRESS, getStrOrgAddress());
				strSection8 = strSection8.replace(IF_ORG_ADDRESS, getStrOrgAddress());
				strSection9 = strSection9.replace(IF_ORG_ADDRESS, getStrOrgAddress());
				strSection10 = strSection10.replace(IF_ORG_ADDRESS, getStrOrgAddress());
				strSection11 = strSection11.replace(IF_ORG_ADDRESS, getStrOrgAddress());
			}
			
			if(getStrOrgContactNo() != null) {
				strSection1 = strSection1.replace(IF_ORG_CONTACT_NO, getStrOrgContactNo());
				strSection2 = strSection2.replace(IF_ORG_CONTACT_NO, getStrOrgContactNo());
				strSection3 = strSection3.replace(IF_ORG_CONTACT_NO, getStrOrgContactNo());
				strSection4 = strSection4.replace(IF_ORG_CONTACT_NO, getStrOrgContactNo());
				strSection5 = strSection5.replace(IF_ORG_CONTACT_NO, getStrOrgContactNo());
				strSection6 = strSection6.replace(IF_ORG_CONTACT_NO, getStrOrgContactNo());
				strSection7 = strSection7.replace(IF_ORG_CONTACT_NO, getStrOrgContactNo());
				strSection8 = strSection8.replace(IF_ORG_CONTACT_NO, getStrOrgContactNo());
				strSection9 = strSection9.replace(IF_ORG_CONTACT_NO, getStrOrgContactNo());
				strSection10 = strSection10.replace(IF_ORG_CONTACT_NO, getStrOrgContactNo());
				strSection11 = strSection11.replace(IF_ORG_CONTACT_NO, getStrOrgContactNo());
			}
			
			if(getStrOrgFaxNo() != null) {
				strSection1 = strSection1.replace(IF_ORG_FAX_NO, getStrOrgFaxNo());
				strSection2 = strSection2.replace(IF_ORG_FAX_NO, getStrOrgFaxNo());
				strSection3 = strSection3.replace(IF_ORG_FAX_NO, getStrOrgFaxNo());
				strSection4 = strSection4.replace(IF_ORG_FAX_NO, getStrOrgFaxNo());
				strSection5 = strSection5.replace(IF_ORG_FAX_NO, getStrOrgFaxNo());
				strSection6 = strSection6.replace(IF_ORG_FAX_NO, getStrOrgFaxNo());
				strSection7 = strSection7.replace(IF_ORG_FAX_NO, getStrOrgFaxNo());
				strSection8 = strSection8.replace(IF_ORG_FAX_NO, getStrOrgFaxNo());
				strSection9 = strSection9.replace(IF_ORG_FAX_NO, getStrOrgFaxNo());
				strSection10 = strSection10.replace(IF_ORG_FAX_NO, getStrOrgFaxNo());
				strSection11 = strSection11.replace(IF_ORG_FAX_NO, getStrOrgFaxNo());
			}
			
			if(getStrOrgEmailId() != null) {
				strSection1 = strSection1.replace(IF_ORG_EMAIL_ID, getStrOrgEmailId());
				strSection2 = strSection2.replace(IF_ORG_EMAIL_ID, getStrOrgEmailId());
				strSection3 = strSection3.replace(IF_ORG_EMAIL_ID, getStrOrgEmailId());
				strSection4 = strSection4.replace(IF_ORG_EMAIL_ID, getStrOrgEmailId());
				strSection5 = strSection5.replace(IF_ORG_EMAIL_ID, getStrOrgEmailId());
				strSection6 = strSection6.replace(IF_ORG_EMAIL_ID, getStrOrgEmailId());
				strSection7 = strSection7.replace(IF_ORG_EMAIL_ID, getStrOrgEmailId());
				strSection8 = strSection8.replace(IF_ORG_EMAIL_ID, getStrOrgEmailId());
				strSection9 = strSection9.replace(IF_ORG_EMAIL_ID, getStrOrgEmailId());
				strSection10 = strSection10.replace(IF_ORG_EMAIL_ID, getStrOrgEmailId());
				strSection11 = strSection11.replace(IF_ORG_EMAIL_ID, getStrOrgEmailId());
			}
			
			if(getStrLegalEntityName() != null) {
				strSection1 = strSection1.replace(IF_LEGAL_ENTITY_NAME, getStrLegalEntityName());
				strSection2 = strSection2.replace(IF_LEGAL_ENTITY_NAME, getStrLegalEntityName());
				strSection3 = strSection3.replace(IF_LEGAL_ENTITY_NAME, getStrLegalEntityName());
				strSection4 = strSection4.replace(IF_LEGAL_ENTITY_NAME, getStrLegalEntityName());
				strSection5 = strSection5.replace(IF_LEGAL_ENTITY_NAME, getStrLegalEntityName());
				strSection6 = strSection6.replace(IF_LEGAL_ENTITY_NAME, getStrLegalEntityName());
				strSection7 = strSection7.replace(IF_LEGAL_ENTITY_NAME, getStrLegalEntityName());
				strSection8 = strSection8.replace(IF_LEGAL_ENTITY_NAME, getStrLegalEntityName());
				strSection9 = strSection9.replace(IF_LEGAL_ENTITY_NAME, getStrLegalEntityName());
				strSection10 = strSection10.replace(IF_LEGAL_ENTITY_NAME, getStrLegalEntityName());
				strSection11 = strSection11.replace(IF_LEGAL_ENTITY_NAME, getStrLegalEntityName());
			}
			
			if(getStrLegalEntitySubTitle() != null) {
				strSection1 = strSection1.replace(IF_LEGAL_ENTITY_SUB_TITLE, getStrLegalEntitySubTitle());
				strSection2 = strSection2.replace(IF_LEGAL_ENTITY_SUB_TITLE, getStrLegalEntitySubTitle());
				strSection3 = strSection3.replace(IF_LEGAL_ENTITY_SUB_TITLE, getStrLegalEntitySubTitle());
				strSection4 = strSection4.replace(IF_LEGAL_ENTITY_SUB_TITLE, getStrLegalEntitySubTitle());
				strSection5 = strSection5.replace(IF_LEGAL_ENTITY_SUB_TITLE, getStrLegalEntitySubTitle());
				strSection6 = strSection6.replace(IF_LEGAL_ENTITY_SUB_TITLE, getStrLegalEntitySubTitle());
				strSection7 = strSection7.replace(IF_LEGAL_ENTITY_SUB_TITLE, getStrLegalEntitySubTitle());
				strSection8 = strSection8.replace(IF_LEGAL_ENTITY_SUB_TITLE, getStrLegalEntitySubTitle());
				strSection9 = strSection9.replace(IF_LEGAL_ENTITY_SUB_TITLE, getStrLegalEntitySubTitle());
				strSection10 = strSection10.replace(IF_LEGAL_ENTITY_SUB_TITLE, getStrLegalEntitySubTitle());
				strSection11 = strSection11.replace(IF_LEGAL_ENTITY_SUB_TITLE, getStrLegalEntitySubTitle());
			}
			
			if(getStrLegalEntityAddress() != null) {
				strSection1 = strSection1.replace(IF_LEGAL_ENTITY_ADDRESS, getStrLegalEntityAddress());
				strSection2 = strSection2.replace(IF_LEGAL_ENTITY_ADDRESS, getStrLegalEntityAddress());
				strSection3 = strSection3.replace(IF_LEGAL_ENTITY_ADDRESS, getStrLegalEntityAddress());
				strSection4 = strSection4.replace(IF_LEGAL_ENTITY_ADDRESS, getStrLegalEntityAddress());
				strSection5 = strSection5.replace(IF_LEGAL_ENTITY_ADDRESS, getStrLegalEntityAddress());
				strSection6 = strSection6.replace(IF_LEGAL_ENTITY_ADDRESS, getStrLegalEntityAddress());
				strSection7 = strSection7.replace(IF_LEGAL_ENTITY_ADDRESS, getStrLegalEntityAddress());
				strSection8 = strSection8.replace(IF_LEGAL_ENTITY_ADDRESS, getStrLegalEntityAddress());
				strSection9 = strSection9.replace(IF_LEGAL_ENTITY_ADDRESS, getStrLegalEntityAddress());
				strSection10 = strSection10.replace(IF_LEGAL_ENTITY_ADDRESS, getStrLegalEntityAddress());
				strSection11 = strSection11.replace(IF_LEGAL_ENTITY_ADDRESS, getStrLegalEntityAddress());
			}
			
			if(getStrLegalEntityContactNo() != null) {
				strSection1 = strSection1.replace(IF_LEGAL_ENTITY_CONTACT_NO, getStrLegalEntityContactNo());
				strSection2 = strSection2.replace(IF_LEGAL_ENTITY_CONTACT_NO, getStrLegalEntityContactNo());
				strSection3 = strSection3.replace(IF_LEGAL_ENTITY_CONTACT_NO, getStrLegalEntityContactNo());
				strSection4 = strSection4.replace(IF_LEGAL_ENTITY_CONTACT_NO, getStrLegalEntityContactNo());
				strSection5 = strSection5.replace(IF_LEGAL_ENTITY_CONTACT_NO, getStrLegalEntityContactNo());
				strSection6 = strSection6.replace(IF_LEGAL_ENTITY_CONTACT_NO, getStrLegalEntityContactNo());
				strSection7 = strSection7.replace(IF_LEGAL_ENTITY_CONTACT_NO, getStrLegalEntityContactNo());
				strSection8 = strSection8.replace(IF_LEGAL_ENTITY_CONTACT_NO, getStrLegalEntityContactNo());
				strSection9 = strSection9.replace(IF_LEGAL_ENTITY_CONTACT_NO, getStrLegalEntityContactNo());
				strSection10 = strSection10.replace(IF_LEGAL_ENTITY_CONTACT_NO, getStrLegalEntityContactNo());
				strSection11 = strSection11.replace(IF_LEGAL_ENTITY_CONTACT_NO, getStrLegalEntityContactNo());
			}
			
			
			if(getStrLegalEntityFaxNo() != null) {
				strSection1 = strSection1.replace(IF_LEGAL_ENTITY_FAX_NO, getStrLegalEntityFaxNo());
				strSection2 = strSection2.replace(IF_LEGAL_ENTITY_FAX_NO, getStrLegalEntityFaxNo());
				strSection3 = strSection3.replace(IF_LEGAL_ENTITY_FAX_NO, getStrLegalEntityFaxNo());
				strSection4 = strSection4.replace(IF_LEGAL_ENTITY_FAX_NO, getStrLegalEntityFaxNo());
				strSection5 = strSection5.replace(IF_LEGAL_ENTITY_FAX_NO, getStrLegalEntityFaxNo());
				strSection6 = strSection6.replace(IF_LEGAL_ENTITY_FAX_NO, getStrLegalEntityFaxNo());
				strSection7 = strSection7.replace(IF_LEGAL_ENTITY_FAX_NO, getStrLegalEntityFaxNo());
				strSection8 = strSection8.replace(IF_LEGAL_ENTITY_FAX_NO, getStrLegalEntityFaxNo());
				strSection9 = strSection9.replace(IF_LEGAL_ENTITY_FAX_NO, getStrLegalEntityFaxNo());
				strSection10 = strSection10.replace(IF_LEGAL_ENTITY_FAX_NO, getStrLegalEntityFaxNo());
				strSection11 = strSection11.replace(IF_LEGAL_ENTITY_FAX_NO, getStrLegalEntityFaxNo());
			}
			
			if(getStrLegalEntityEmailId() != null) {
				strSection1 = strSection1.replace(IF_LEGAL_ENTITY_EMAIL_ID, getStrLegalEntityEmailId());
				strSection2 = strSection2.replace(IF_LEGAL_ENTITY_EMAIL_ID, getStrLegalEntityEmailId());
				strSection3 = strSection3.replace(IF_LEGAL_ENTITY_EMAIL_ID, getStrLegalEntityEmailId());
				strSection4 = strSection4.replace(IF_LEGAL_ENTITY_EMAIL_ID, getStrLegalEntityEmailId());
				strSection5 = strSection5.replace(IF_LEGAL_ENTITY_EMAIL_ID, getStrLegalEntityEmailId());
				strSection6 = strSection6.replace(IF_LEGAL_ENTITY_EMAIL_ID, getStrLegalEntityEmailId());
				strSection7 = strSection7.replace(IF_LEGAL_ENTITY_EMAIL_ID, getStrLegalEntityEmailId());
				strSection8 = strSection8.replace(IF_LEGAL_ENTITY_EMAIL_ID, getStrLegalEntityEmailId());
				strSection9 = strSection9.replace(IF_LEGAL_ENTITY_EMAIL_ID, getStrLegalEntityEmailId());
				strSection10 = strSection10.replace(IF_LEGAL_ENTITY_EMAIL_ID, getStrLegalEntityEmailId());
				strSection11 = strSection11.replace(IF_LEGAL_ENTITY_EMAIL_ID, getStrLegalEntityEmailId());
			}
			
			if(getStrLegalEntityRegNo() != null) {
				strSection1 = strSection1.replace(IF_LEGAL_ENTITY_REG_NO, getStrLegalEntityRegNo());
				strSection2 = strSection2.replace(IF_LEGAL_ENTITY_REG_NO, getStrLegalEntityRegNo());
				strSection3 = strSection3.replace(IF_LEGAL_ENTITY_REG_NO, getStrLegalEntityRegNo());
				strSection4 = strSection4.replace(IF_LEGAL_ENTITY_REG_NO, getStrLegalEntityRegNo());
				strSection5 = strSection5.replace(IF_LEGAL_ENTITY_REG_NO, getStrLegalEntityRegNo());
				strSection6 = strSection6.replace(IF_LEGAL_ENTITY_REG_NO, getStrLegalEntityRegNo());
				strSection7 = strSection7.replace(IF_LEGAL_ENTITY_REG_NO, getStrLegalEntityRegNo());
				strSection8 = strSection8.replace(IF_LEGAL_ENTITY_REG_NO, getStrLegalEntityRegNo());
				strSection9 = strSection9.replace(IF_LEGAL_ENTITY_REG_NO, getStrLegalEntityRegNo());
				strSection10 = strSection10.replace(IF_LEGAL_ENTITY_REG_NO, getStrLegalEntityRegNo());
				strSection11 = strSection11.replace(IF_LEGAL_ENTITY_REG_NO, getStrLegalEntityRegNo());
			}
			
			if(getStrLegalEntityPanNo() != null) {
				strSection1 = strSection1.replace(IF_LEGAL_ENTITY_PAN_NO, getStrLegalEntityPanNo());
				strSection2 = strSection2.replace(IF_LEGAL_ENTITY_PAN_NO, getStrLegalEntityPanNo());
				strSection3 = strSection3.replace(IF_LEGAL_ENTITY_PAN_NO, getStrLegalEntityPanNo());
				strSection4 = strSection4.replace(IF_LEGAL_ENTITY_PAN_NO, getStrLegalEntityPanNo());
				strSection5 = strSection5.replace(IF_LEGAL_ENTITY_PAN_NO, getStrLegalEntityPanNo());
				strSection6 = strSection6.replace(IF_LEGAL_ENTITY_PAN_NO, getStrLegalEntityPanNo());
				strSection7 = strSection7.replace(IF_LEGAL_ENTITY_PAN_NO, getStrLegalEntityPanNo());
				strSection8 = strSection8.replace(IF_LEGAL_ENTITY_PAN_NO, getStrLegalEntityPanNo());
				strSection9 = strSection9.replace(IF_LEGAL_ENTITY_PAN_NO, getStrLegalEntityPanNo());
				strSection10 = strSection10.replace(IF_LEGAL_ENTITY_PAN_NO, getStrLegalEntityPanNo());
				strSection11 = strSection11.replace(IF_LEGAL_ENTITY_PAN_NO, getStrLegalEntityPanNo());
			}
			
			if(getStrLegalEntityTanNo() != null) {
				strSection1 = strSection1.replace(IF_LEGAL_ENTITY_TAN_NO, getStrLegalEntityTanNo());
				strSection2 = strSection2.replace(IF_LEGAL_ENTITY_TAN_NO, getStrLegalEntityTanNo());
				strSection3 = strSection3.replace(IF_LEGAL_ENTITY_TAN_NO, getStrLegalEntityTanNo());
				strSection4 = strSection4.replace(IF_LEGAL_ENTITY_TAN_NO, getStrLegalEntityTanNo());
				strSection5 = strSection5.replace(IF_LEGAL_ENTITY_TAN_NO, getStrLegalEntityTanNo());
				strSection6 = strSection6.replace(IF_LEGAL_ENTITY_TAN_NO, getStrLegalEntityTanNo());
				strSection7 = strSection7.replace(IF_LEGAL_ENTITY_TAN_NO, getStrLegalEntityTanNo());
				strSection8 = strSection8.replace(IF_LEGAL_ENTITY_TAN_NO, getStrLegalEntityTanNo());
				strSection9 = strSection9.replace(IF_LEGAL_ENTITY_TAN_NO, getStrLegalEntityTanNo());
				strSection10 = strSection10.replace(IF_LEGAL_ENTITY_TAN_NO, getStrLegalEntityTanNo());
				strSection11 = strSection11.replace(IF_LEGAL_ENTITY_TAN_NO, getStrLegalEntityTanNo());
			}
			
//			if(getStrLegalEntityECCNo() != null) {
//				strSection1 = strSection1.replace(IF_LEGAL_ENTITY_ECC_NO, getStrLegalEntityECCNo());
//				strSection2 = strSection2.replace(IF_LEGAL_ENTITY_ECC_NO, getStrLegalEntityECCNo());
//				strSection3 = strSection3.replace(IF_LEGAL_ENTITY_ECC_NO, getStrLegalEntityECCNo());
//				strSection4 = strSection4.replace(IF_LEGAL_ENTITY_ECC_NO, getStrLegalEntityECCNo());
//				strSection5 = strSection5.replace(IF_LEGAL_ENTITY_ECC_NO, getStrLegalEntityECCNo());
//				strSection6 = strSection6.replace(IF_LEGAL_ENTITY_ECC_NO, getStrLegalEntityECCNo());
//				strSection7 = strSection7.replace(IF_LEGAL_ENTITY_ECC_NO, getStrLegalEntityECCNo());
//				strSection8 = strSection8.replace(IF_LEGAL_ENTITY_ECC_NO, getStrLegalEntityECCNo());
//				strSection9 = strSection9.replace(IF_LEGAL_ENTITY_ECC_NO, getStrLegalEntityECCNo());
//				strSection10 = strSection10.replace(IF_LEGAL_ENTITY_ECC_NO, getStrLegalEntityECCNo());
//				strSection11 = strSection11.replace(IF_LEGAL_ENTITY_ECC_NO, getStrLegalEntityECCNo());
//			}
			
			if(getStrWorkLocation() != null) {
				strSection1 = strSection1.replace(IF_WORK_LOCATION, getStrWorkLocation());
				strSection2 = strSection2.replace(IF_WORK_LOCATION, getStrWorkLocation());
				strSection3 = strSection3.replace(IF_WORK_LOCATION, getStrWorkLocation());
				strSection4 = strSection4.replace(IF_WORK_LOCATION, getStrWorkLocation());
				strSection5 = strSection5.replace(IF_WORK_LOCATION, getStrWorkLocation());
				strSection6 = strSection6.replace(IF_WORK_LOCATION, getStrWorkLocation());
				strSection7 = strSection7.replace(IF_WORK_LOCATION, getStrWorkLocation());
				strSection8 = strSection8.replace(IF_WORK_LOCATION, getStrWorkLocation());
				strSection9 = strSection9.replace(IF_WORK_LOCATION, getStrWorkLocation());
				strSection10 = strSection10.replace(IF_WORK_LOCATION, getStrWorkLocation());
				strSection11 = strSection11.replace(IF_WORK_LOCATION, getStrWorkLocation());
			}
			
			if(getStrWorkLocationAddress() != null) {
				strSection1 = strSection1.replace(IF_WORK_LOCATION_ADDRESS, getStrWorkLocationAddress());
				strSection2 = strSection2.replace(IF_WORK_LOCATION_ADDRESS, getStrWorkLocationAddress());
				strSection3 = strSection3.replace(IF_WORK_LOCATION_ADDRESS, getStrWorkLocationAddress());
				strSection4 = strSection4.replace(IF_WORK_LOCATION_ADDRESS, getStrWorkLocationAddress());
				strSection5 = strSection5.replace(IF_WORK_LOCATION_ADDRESS, getStrWorkLocationAddress());
				strSection6 = strSection6.replace(IF_WORK_LOCATION_ADDRESS, getStrWorkLocationAddress());
				strSection7 = strSection7.replace(IF_WORK_LOCATION_ADDRESS, getStrWorkLocationAddress());
				strSection8 = strSection8.replace(IF_WORK_LOCATION_ADDRESS, getStrWorkLocationAddress());
				strSection9 = strSection9.replace(IF_WORK_LOCATION_ADDRESS, getStrWorkLocationAddress());
				strSection10 = strSection10.replace(IF_WORK_LOCATION_ADDRESS, getStrWorkLocationAddress());
				strSection11 = strSection11.replace(IF_WORK_LOCATION_ADDRESS, getStrWorkLocationAddress());
			}
			
			if(getStrWorkLocationContactNo() != null) {
				strSection1 = strSection1.replace(IF_WORK_LOCATION_CONTACT_NO, getStrWorkLocationContactNo());
				strSection2 = strSection2.replace(IF_WORK_LOCATION_CONTACT_NO, getStrWorkLocationContactNo());
				strSection3 = strSection3.replace(IF_WORK_LOCATION_CONTACT_NO, getStrWorkLocationContactNo());
				strSection4 = strSection4.replace(IF_WORK_LOCATION_CONTACT_NO, getStrWorkLocationContactNo());
				strSection5 = strSection5.replace(IF_WORK_LOCATION_CONTACT_NO, getStrWorkLocationContactNo());
				strSection6 = strSection6.replace(IF_WORK_LOCATION_CONTACT_NO, getStrWorkLocationContactNo());
				strSection7 = strSection7.replace(IF_WORK_LOCATION_CONTACT_NO, getStrWorkLocationContactNo());
				strSection8 = strSection8.replace(IF_WORK_LOCATION_CONTACT_NO, getStrWorkLocationContactNo());
				strSection9 = strSection9.replace(IF_WORK_LOCATION_CONTACT_NO, getStrWorkLocationContactNo());
				strSection10 = strSection10.replace(IF_WORK_LOCATION_CONTACT_NO, getStrWorkLocationContactNo());
				strSection11 = strSection11.replace(IF_WORK_LOCATION_CONTACT_NO, getStrWorkLocationContactNo());
			}
			
			if(getStrWorkLocationFaxNo() != null) {
				strSection1 = strSection1.replace(IF_WORK_LOCATION_FAX_NO, getStrWorkLocationFaxNo());
				strSection2 = strSection2.replace(IF_WORK_LOCATION_FAX_NO, getStrWorkLocationFaxNo());
				strSection3 = strSection3.replace(IF_WORK_LOCATION_FAX_NO, getStrWorkLocationFaxNo());
				strSection4 = strSection4.replace(IF_WORK_LOCATION_FAX_NO, getStrWorkLocationFaxNo());
				strSection5 = strSection5.replace(IF_WORK_LOCATION_FAX_NO, getStrWorkLocationFaxNo());
				strSection6 = strSection6.replace(IF_WORK_LOCATION_FAX_NO, getStrWorkLocationFaxNo());
				strSection7 = strSection7.replace(IF_WORK_LOCATION_FAX_NO, getStrWorkLocationFaxNo());
				strSection8 = strSection8.replace(IF_WORK_LOCATION_FAX_NO, getStrWorkLocationFaxNo());
				strSection9 = strSection9.replace(IF_WORK_LOCATION_FAX_NO, getStrWorkLocationFaxNo());
				strSection10 = strSection10.replace(IF_WORK_LOCATION_FAX_NO, getStrWorkLocationFaxNo());
				strSection11 = strSection11.replace(IF_WORK_LOCATION_FAX_NO, getStrWorkLocationFaxNo());
			}
			
			if(getStrWorkLocationECCNo1() != null) {
				strSection1 = strSection1.replace(IF_WORK_LOCATION_ECC_NO1, getStrWorkLocationECCNo1());
				strSection2 = strSection2.replace(IF_WORK_LOCATION_ECC_NO1, getStrWorkLocationECCNo1());
				strSection3 = strSection3.replace(IF_WORK_LOCATION_ECC_NO1, getStrWorkLocationECCNo1());
				strSection4 = strSection4.replace(IF_WORK_LOCATION_ECC_NO1, getStrWorkLocationECCNo1());
				strSection5 = strSection5.replace(IF_WORK_LOCATION_ECC_NO1, getStrWorkLocationECCNo1());
				strSection6 = strSection6.replace(IF_WORK_LOCATION_ECC_NO1, getStrWorkLocationECCNo1());
				strSection7 = strSection7.replace(IF_WORK_LOCATION_ECC_NO1, getStrWorkLocationECCNo1());
				strSection8 = strSection8.replace(IF_WORK_LOCATION_ECC_NO1, getStrWorkLocationECCNo1());
				strSection9 = strSection9.replace(IF_WORK_LOCATION_ECC_NO1, getStrWorkLocationECCNo1());
				strSection10 = strSection10.replace(IF_WORK_LOCATION_ECC_NO1, getStrWorkLocationECCNo1());
				strSection11 = strSection11.replace(IF_WORK_LOCATION_ECC_NO1, getStrWorkLocationECCNo1());
			}
			
			if(getStrWorkLocationECCNo2() != null) {
				strSection1 = strSection1.replace(IF_WORK_LOCATION_ECC_NO2, getStrWorkLocationECCNo2());
				strSection2 = strSection2.replace(IF_WORK_LOCATION_ECC_NO2, getStrWorkLocationECCNo2());
				strSection3 = strSection3.replace(IF_WORK_LOCATION_ECC_NO2, getStrWorkLocationECCNo2());
				strSection4 = strSection4.replace(IF_WORK_LOCATION_ECC_NO2, getStrWorkLocationECCNo2());
				strSection5 = strSection5.replace(IF_WORK_LOCATION_ECC_NO2, getStrWorkLocationECCNo2());
				strSection6 = strSection6.replace(IF_WORK_LOCATION_ECC_NO2, getStrWorkLocationECCNo2());
				strSection7 = strSection7.replace(IF_WORK_LOCATION_ECC_NO2, getStrWorkLocationECCNo2());
				strSection8 = strSection8.replace(IF_WORK_LOCATION_ECC_NO2, getStrWorkLocationECCNo2());
				strSection9 = strSection9.replace(IF_WORK_LOCATION_ECC_NO2, getStrWorkLocationECCNo2());
				strSection10 = strSection10.replace(IF_WORK_LOCATION_ECC_NO2, getStrWorkLocationECCNo2());
				strSection11 = strSection11.replace(IF_WORK_LOCATION_ECC_NO2, getStrWorkLocationECCNo2());
			}
			
			
			if(getStrWorkLocationEmailId() != null) {
				strSection1 = strSection1.replace(IF_WORK_LOCATION_EMAIL_ID, getStrWorkLocationEmailId());
				strSection2 = strSection2.replace(IF_WORK_LOCATION_EMAIL_ID, getStrWorkLocationEmailId());
				strSection3 = strSection3.replace(IF_WORK_LOCATION_EMAIL_ID, getStrWorkLocationEmailId());
				strSection4 = strSection4.replace(IF_WORK_LOCATION_EMAIL_ID, getStrWorkLocationEmailId());
				strSection5 = strSection5.replace(IF_WORK_LOCATION_EMAIL_ID, getStrWorkLocationEmailId());
				strSection6 = strSection6.replace(IF_WORK_LOCATION_EMAIL_ID, getStrWorkLocationEmailId());
				strSection7 = strSection7.replace(IF_WORK_LOCATION_EMAIL_ID, getStrWorkLocationEmailId());
				strSection8 = strSection8.replace(IF_WORK_LOCATION_EMAIL_ID, getStrWorkLocationEmailId());
				strSection9 = strSection9.replace(IF_WORK_LOCATION_EMAIL_ID, getStrWorkLocationEmailId());
				strSection10 = strSection10.replace(IF_WORK_LOCATION_EMAIL_ID, getStrWorkLocationEmailId());
				strSection11 = strSection11.replace(IF_WORK_LOCATION_EMAIL_ID, getStrWorkLocationEmailId());
			}
			
			if(getStrWorkLocationsInLegalEntity() != null) {
				strSection1 = strSection1.replace(IF_WORK_LOCATIONS_IN_LEGAL_ENTITY, getStrWorkLocationsInLegalEntity());
				strSection2 = strSection2.replace(IF_WORK_LOCATIONS_IN_LEGAL_ENTITY, getStrWorkLocationsInLegalEntity());
				strSection3 = strSection3.replace(IF_WORK_LOCATIONS_IN_LEGAL_ENTITY, getStrWorkLocationsInLegalEntity());
				strSection4 = strSection4.replace(IF_WORK_LOCATIONS_IN_LEGAL_ENTITY, getStrWorkLocationsInLegalEntity());
				strSection5 = strSection5.replace(IF_WORK_LOCATIONS_IN_LEGAL_ENTITY, getStrWorkLocationsInLegalEntity());
				strSection6 = strSection6.replace(IF_WORK_LOCATIONS_IN_LEGAL_ENTITY, getStrWorkLocationsInLegalEntity());
				strSection7 = strSection7.replace(IF_WORK_LOCATIONS_IN_LEGAL_ENTITY, getStrWorkLocationsInLegalEntity());
				strSection8 = strSection8.replace(IF_WORK_LOCATIONS_IN_LEGAL_ENTITY, getStrWorkLocationsInLegalEntity());
				strSection9 = strSection9.replace(IF_WORK_LOCATIONS_IN_LEGAL_ENTITY, getStrWorkLocationsInLegalEntity());
				strSection10 = strSection10.replace(IF_WORK_LOCATIONS_IN_LEGAL_ENTITY, getStrWorkLocationsInLegalEntity());
				strSection11 = strSection11.replace(IF_WORK_LOCATIONS_IN_LEGAL_ENTITY, getStrWorkLocationsInLegalEntity());
			}
			
			if(getStrProjectOwnerName() != null) {
				String projectOwner = getStrProjectOwnerName()+"<input type=\"hidden\" name=\"strProjectOwner\" value=\""+getStrProjectOwnerId()+"\">";
				strSection1 = strSection1.replace(IF_PROJECT_OWNER_NAME, projectOwner);
				strSection2 = strSection2.replace(IF_PROJECT_OWNER_NAME, projectOwner);
				strSection3 = strSection3.replace(IF_PROJECT_OWNER_NAME, projectOwner);
				strSection4 = strSection4.replace(IF_PROJECT_OWNER_NAME, projectOwner);
				strSection5 = strSection5.replace(IF_PROJECT_OWNER_NAME, projectOwner);
				strSection6 = strSection6.replace(IF_PROJECT_OWNER_NAME, projectOwner);
				strSection7 = strSection7.replace(IF_PROJECT_OWNER_NAME, projectOwner);
				strSection8 = strSection8.replace(IF_PROJECT_OWNER_NAME, projectOwner);
				strSection9 = strSection9.replace(IF_PROJECT_OWNER_NAME, projectOwner);
				strSection10 = strSection10.replace(IF_PROJECT_OWNER_NAME, projectOwner);
				strSection11 = strSection11.replace(IF_PROJECT_OWNER_NAME, projectOwner);
			}
			
			if(getStrProjectOwnerDesig() != null) {
				strSection1 = strSection1.replace(IF_PROJECT_OWNER_DESIG, getStrProjectOwnerDesig());
				strSection2 = strSection2.replace(IF_PROJECT_OWNER_DESIG, getStrProjectOwnerDesig());
				strSection3 = strSection3.replace(IF_PROJECT_OWNER_DESIG, getStrProjectOwnerDesig());
				strSection4 = strSection4.replace(IF_PROJECT_OWNER_DESIG, getStrProjectOwnerDesig());
				strSection5 = strSection5.replace(IF_PROJECT_OWNER_DESIG, getStrProjectOwnerDesig());
				strSection6 = strSection6.replace(IF_PROJECT_OWNER_DESIG, getStrProjectOwnerDesig());
				strSection7 = strSection7.replace(IF_PROJECT_OWNER_DESIG, getStrProjectOwnerDesig());
				strSection8 = strSection8.replace(IF_PROJECT_OWNER_DESIG, getStrProjectOwnerDesig());
				strSection9 = strSection9.replace(IF_PROJECT_OWNER_DESIG, getStrProjectOwnerDesig());
				strSection10 = strSection10.replace(IF_PROJECT_OWNER_DESIG, getStrProjectOwnerDesig());
				strSection11 = strSection11.replace(IF_PROJECT_OWNER_DESIG, getStrProjectOwnerDesig());
			}
			
			if(getStrProjectOwnerContactNo() != null) {
				strSection1 = strSection1.replace(IF_PROJECT_OWNER_CONTACT_NO, getStrProjectOwnerContactNo());
				strSection2 = strSection2.replace(IF_PROJECT_OWNER_CONTACT_NO, getStrProjectOwnerContactNo());
				strSection3 = strSection3.replace(IF_PROJECT_OWNER_CONTACT_NO, getStrProjectOwnerContactNo());
				strSection4 = strSection4.replace(IF_PROJECT_OWNER_CONTACT_NO, getStrProjectOwnerContactNo());
				strSection5 = strSection5.replace(IF_PROJECT_OWNER_CONTACT_NO, getStrProjectOwnerContactNo());
				strSection6 = strSection6.replace(IF_PROJECT_OWNER_CONTACT_NO, getStrProjectOwnerContactNo());
				strSection7 = strSection7.replace(IF_PROJECT_OWNER_CONTACT_NO, getStrProjectOwnerContactNo());
				strSection8 = strSection8.replace(IF_PROJECT_OWNER_CONTACT_NO, getStrProjectOwnerContactNo());
				strSection9 = strSection9.replace(IF_PROJECT_OWNER_CONTACT_NO, getStrProjectOwnerContactNo());
				strSection10 = strSection10.replace(IF_PROJECT_OWNER_CONTACT_NO, getStrProjectOwnerContactNo());
				strSection11 = strSection11.replace(IF_PROJECT_OWNER_CONTACT_NO, getStrProjectOwnerContactNo());
			}
			
			if(getStrProjectOwnerEmailId() != null) {
				strSection1 = strSection1.replace(IF_PROJECT_OWNER_EMAIL_ID, getStrProjectOwnerEmailId());
				strSection2 = strSection2.replace(IF_PROJECT_OWNER_EMAIL_ID, getStrProjectOwnerEmailId());
				strSection3 = strSection3.replace(IF_PROJECT_OWNER_EMAIL_ID, getStrProjectOwnerEmailId());
				strSection4 = strSection4.replace(IF_PROJECT_OWNER_EMAIL_ID, getStrProjectOwnerEmailId());
				strSection5 = strSection5.replace(IF_PROJECT_OWNER_EMAIL_ID, getStrProjectOwnerEmailId());
				strSection6 = strSection6.replace(IF_PROJECT_OWNER_EMAIL_ID, getStrProjectOwnerEmailId());
				strSection7 = strSection7.replace(IF_PROJECT_OWNER_EMAIL_ID, getStrProjectOwnerEmailId());
				strSection8 = strSection8.replace(IF_PROJECT_OWNER_EMAIL_ID, getStrProjectOwnerEmailId());
				strSection9 = strSection9.replace(IF_PROJECT_OWNER_EMAIL_ID, getStrProjectOwnerEmailId());
				strSection10 = strSection10.replace(IF_PROJECT_OWNER_EMAIL_ID, getStrProjectOwnerEmailId());
				strSection11 = strSection11.replace(IF_PROJECT_OWNER_EMAIL_ID, getStrProjectOwnerEmailId());
			}
			
			if(getStrHRName() != null) {
				strSection1 = strSection1.replace(IF_HR, getStrHRName());
				strSection2 = strSection2.replace(IF_HR, getStrHRName());
				strSection3 = strSection3.replace(IF_HR, getStrHRName());
				strSection4 = strSection4.replace(IF_HR, getStrHRName());
				strSection5 = strSection5.replace(IF_HR, getStrHRName());
				strSection6 = strSection6.replace(IF_HR, getStrHRName());
				strSection7 = strSection7.replace(IF_HR, getStrHRName());
				strSection8 = strSection8.replace(IF_HR, getStrHRName());
				strSection9 = strSection9.replace(IF_HR, getStrHRName());
				strSection10 = strSection10.replace(IF_HR, getStrHRName());
				strSection11 = strSection11.replace(IF_HR, getStrHRName());
			}
			
			if(getStrHRContactNo() != null) {
				strSection1 = strSection1.replace(IF_HR_CONTACT_NO, getStrHRContactNo());
				strSection2 = strSection2.replace(IF_HR_CONTACT_NO, getStrHRContactNo());
				strSection3 = strSection3.replace(IF_HR_CONTACT_NO, getStrHRContactNo());
				strSection4 = strSection4.replace(IF_HR_CONTACT_NO, getStrHRContactNo());
				strSection5 = strSection5.replace(IF_HR_CONTACT_NO, getStrHRContactNo());
				strSection6 = strSection6.replace(IF_HR_CONTACT_NO, getStrHRContactNo());
				strSection7 = strSection7.replace(IF_HR_CONTACT_NO, getStrHRContactNo());
				strSection8 = strSection8.replace(IF_HR_CONTACT_NO, getStrHRContactNo());
				strSection9 = strSection9.replace(IF_HR_CONTACT_NO, getStrHRContactNo());
				strSection10 = strSection10.replace(IF_HR_CONTACT_NO, getStrHRContactNo());
				strSection11 = strSection11.replace(IF_HR_CONTACT_NO, getStrHRContactNo());
			}
			
			if(getStrHREmailId() != null) {
				strSection1 = strSection1.replace(IF_HR_EMAIL_ID, getStrHREmailId());
				strSection2 = strSection2.replace(IF_HR_EMAIL_ID, getStrHREmailId());
				strSection3 = strSection3.replace(IF_HR_EMAIL_ID, getStrHREmailId());
				strSection4 = strSection4.replace(IF_HR_EMAIL_ID, getStrHREmailId());
				strSection5 = strSection5.replace(IF_HR_EMAIL_ID, getStrHREmailId());
				strSection6 = strSection6.replace(IF_HR_EMAIL_ID, getStrHREmailId());
				strSection7 = strSection7.replace(IF_HR_EMAIL_ID, getStrHREmailId());
				strSection8 = strSection8.replace(IF_HR_EMAIL_ID, getStrHREmailId());
				strSection9 = strSection9.replace(IF_HR_EMAIL_ID, getStrHREmailId());
				strSection10 = strSection10.replace(IF_HR_EMAIL_ID, getStrHREmailId());
				strSection11 = strSection11.replace(IF_HR_EMAIL_ID, getStrHREmailId());
			}
			
			if(getStrCustomerName() != null) {
				strSection1 = strSection1.replace(IF_CUSTOMER_NAME, getStrCustomerName());
				strSection2 = strSection2.replace(IF_CUSTOMER_NAME, getStrCustomerName());
				strSection3 = strSection3.replace(IF_CUSTOMER_NAME, getStrCustomerName());
				strSection4 = strSection4.replace(IF_CUSTOMER_NAME, getStrCustomerName());
				strSection5 = strSection5.replace(IF_CUSTOMER_NAME, getStrCustomerName());
				strSection6 = strSection6.replace(IF_CUSTOMER_NAME, getStrCustomerName());
				strSection7 = strSection7.replace(IF_CUSTOMER_NAME, getStrCustomerName());
				strSection8 = strSection8.replace(IF_CUSTOMER_NAME, getStrCustomerName());
				strSection9 = strSection9.replace(IF_CUSTOMER_NAME, getStrCustomerName());
				strSection10 = strSection10.replace(IF_CUSTOMER_NAME, getStrCustomerName());
				strSection11 = strSection11.replace(IF_CUSTOMER_NAME, getStrCustomerName());
			}
			
			if(clientAddressList != null && !clientAddressList.isEmpty()) {
				StringBuilder custAddress = new StringBuilder();
				custAddress.append("<select name=\"clientAddress\" id=\"clientAddress\" style=\"width: 200px; margin-bottom: 5px;\" class=\"validateRequired\">" +
						"<option value=\"\">Select Address</option>");
				for(int i=0; clientAddressList != null && !clientAddressList.isEmpty() && i<clientAddressList.size(); i++) {
					if(i==0) {
						custAddress.append("<option value="+clientAddressList.get(i).getClientAddressId()+" selected>"+clientAddressList.get(i).getClientAddress()+"</option>");
					} else {
						custAddress.append("<option value="+clientAddressList.get(i).getClientAddressId()+">"+clientAddressList.get(i).getClientAddress()+"</option>");
					}
				}
				custAddress.append("</select>");
				strSection1 = strSection1.replace(IF_CUSTOMER_ADDRESS, custAddress);
				strSection2 = strSection2.replace(IF_CUSTOMER_ADDRESS, custAddress);
				strSection3 = strSection3.replace(IF_CUSTOMER_ADDRESS, custAddress);
				strSection4 = strSection4.replace(IF_CUSTOMER_ADDRESS, custAddress);
				strSection5 = strSection5.replace(IF_CUSTOMER_ADDRESS, custAddress);
				strSection6 = strSection6.replace(IF_CUSTOMER_ADDRESS, custAddress);
				strSection7 = strSection7.replace(IF_CUSTOMER_ADDRESS, custAddress);
				strSection8 = strSection8.replace(IF_CUSTOMER_ADDRESS, custAddress);
				strSection9 = strSection9.replace(IF_CUSTOMER_ADDRESS, custAddress);
				strSection10 = strSection10.replace(IF_CUSTOMER_ADDRESS, custAddress);
				strSection11 = strSection11.replace(IF_CUSTOMER_ADDRESS, custAddress);
			}
			
			if(getStrCustomerSPOC() != null) {
				strSection1 = strSection1.replace(IF_CUSTOMER_SPOC, getStrCustomerSPOC());
				strSection2 = strSection2.replace(IF_CUSTOMER_SPOC, getStrCustomerSPOC());
				strSection3 = strSection3.replace(IF_CUSTOMER_SPOC, getStrCustomerSPOC());
				strSection4 = strSection4.replace(IF_CUSTOMER_SPOC, getStrCustomerSPOC());
				strSection5 = strSection5.replace(IF_CUSTOMER_SPOC, getStrCustomerSPOC());
				strSection6 = strSection6.replace(IF_CUSTOMER_SPOC, getStrCustomerSPOC());
				strSection7 = strSection7.replace(IF_CUSTOMER_SPOC, getStrCustomerSPOC());
				strSection8 = strSection8.replace(IF_CUSTOMER_SPOC, getStrCustomerSPOC());
				strSection9 = strSection9.replace(IF_CUSTOMER_SPOC, getStrCustomerSPOC());
				strSection10 = strSection10.replace(IF_CUSTOMER_SPOC, getStrCustomerSPOC());
				strSection11 = strSection11.replace(IF_CUSTOMER_SPOC, getStrCustomerSPOC());
			}
			
			if(getStrCustomerContactNo() != null) {
				strSection1 = strSection1.replace(IF_CUSTOMER_CONTACT_NO, getStrCustomerContactNo());
				strSection2 = strSection2.replace(IF_CUSTOMER_CONTACT_NO, getStrCustomerContactNo());
				strSection3 = strSection3.replace(IF_CUSTOMER_CONTACT_NO, getStrCustomerContactNo());
				strSection4 = strSection4.replace(IF_CUSTOMER_CONTACT_NO, getStrCustomerContactNo());
				strSection5 = strSection5.replace(IF_CUSTOMER_CONTACT_NO, getStrCustomerContactNo());
				strSection6 = strSection6.replace(IF_CUSTOMER_CONTACT_NO, getStrCustomerContactNo());
				strSection7 = strSection7.replace(IF_CUSTOMER_CONTACT_NO, getStrCustomerContactNo());
				strSection8 = strSection8.replace(IF_CUSTOMER_CONTACT_NO, getStrCustomerContactNo());
				strSection9 = strSection9.replace(IF_CUSTOMER_CONTACT_NO, getStrCustomerContactNo());
				strSection10 = strSection10.replace(IF_CUSTOMER_CONTACT_NO, getStrCustomerContactNo());
				strSection11 = strSection11.replace(IF_CUSTOMER_CONTACT_NO, getStrCustomerContactNo());
			}
			
			if(getStrCustomerFaxNo() != null) {
				strSection1 = strSection1.replace(IF_CUSTOMER_FAX_NO, getStrCustomerFaxNo());
				strSection2 = strSection2.replace(IF_CUSTOMER_FAX_NO, getStrCustomerFaxNo());
				strSection3 = strSection3.replace(IF_CUSTOMER_FAX_NO, getStrCustomerFaxNo());
				strSection4 = strSection4.replace(IF_CUSTOMER_FAX_NO, getStrCustomerFaxNo());
				strSection5 = strSection5.replace(IF_CUSTOMER_FAX_NO, getStrCustomerFaxNo());
				strSection6 = strSection6.replace(IF_CUSTOMER_FAX_NO, getStrCustomerFaxNo());
				strSection7 = strSection7.replace(IF_CUSTOMER_FAX_NO, getStrCustomerFaxNo());
				strSection8 = strSection8.replace(IF_CUSTOMER_FAX_NO, getStrCustomerFaxNo());
				strSection9 = strSection9.replace(IF_CUSTOMER_FAX_NO, getStrCustomerFaxNo());
				strSection10 = strSection10.replace(IF_CUSTOMER_FAX_NO, getStrCustomerFaxNo());
				strSection11 = strSection11.replace(IF_CUSTOMER_FAX_NO, getStrCustomerFaxNo());
			}
			
			if(getStrCustomerEmailId() != null) {
				strSection1 = strSection1.replace(IF_CUSTOMER_EMAIL_ID, getStrCustomerEmailId());
				strSection2 = strSection2.replace(IF_CUSTOMER_EMAIL_ID, getStrCustomerEmailId());
				strSection3 = strSection3.replace(IF_CUSTOMER_EMAIL_ID, getStrCustomerEmailId());
				strSection4 = strSection4.replace(IF_CUSTOMER_EMAIL_ID, getStrCustomerEmailId());
				strSection5 = strSection5.replace(IF_CUSTOMER_EMAIL_ID, getStrCustomerEmailId());
				strSection6 = strSection6.replace(IF_CUSTOMER_EMAIL_ID, getStrCustomerEmailId());
				strSection7 = strSection7.replace(IF_CUSTOMER_EMAIL_ID, getStrCustomerEmailId());
				strSection8 = strSection8.replace(IF_CUSTOMER_EMAIL_ID, getStrCustomerEmailId());
				strSection9 = strSection9.replace(IF_CUSTOMER_EMAIL_ID, getStrCustomerEmailId());
				strSection10 = strSection10.replace(IF_CUSTOMER_EMAIL_ID, getStrCustomerEmailId());
				strSection11 = strSection11.replace(IF_CUSTOMER_EMAIL_ID, getStrCustomerEmailId());
			}
			
			if(bankList != null && !bankList.isEmpty()) {
				StringBuilder bankNames = new StringBuilder();
				bankNames.append("<select name=\"bankName\" id=\"bankName\" style=\"width: 200px;\" class=\"validateRequired\">" +
						"<option value=\"\">Select Bank</option>");
				for(int i=0; bankList != null && !bankList.isEmpty() && i<bankList.size(); i++) {
					bankNames.append("<option value="+bankList.get(i).getBankId()+">"+bankList.get(i).getBankName()+"</option>");
				}
				bankNames.append("</select>");
				strSection1 = strSection1.replace(IF_BANK_NAME, bankNames);
				strSection2 = strSection2.replace(IF_BANK_NAME, bankNames);
				strSection3 = strSection3.replace(IF_BANK_NAME, bankNames);
				strSection4 = strSection4.replace(IF_BANK_NAME, bankNames);
				strSection5 = strSection5.replace(IF_BANK_NAME, bankNames);
				strSection6 = strSection6.replace(IF_BANK_NAME, bankNames);
				strSection7 = strSection7.replace(IF_BANK_NAME, bankNames);
				strSection8 = strSection8.replace(IF_BANK_NAME, bankNames);
				strSection9 = strSection9.replace(IF_BANK_NAME, bankNames);
				strSection10 = strSection10.replace(IF_BANK_NAME, bankNames);
				strSection11 = strSection11.replace(IF_BANK_NAME, bankNames);
			}
			
			if(getStrBranchName() != null) {
				strSection1 = strSection1.replace(IF_BRANCH_NAME, getStrBranchName());
				strSection2 = strSection2.replace(IF_BRANCH_NAME, getStrBranchName());
				strSection3 = strSection3.replace(IF_BRANCH_NAME, getStrBranchName());
				strSection4 = strSection4.replace(IF_BRANCH_NAME, getStrBranchName());
				strSection5 = strSection5.replace(IF_BRANCH_NAME, getStrBranchName());
				strSection6 = strSection6.replace(IF_BRANCH_NAME, getStrBranchName());
				strSection7 = strSection7.replace(IF_BRANCH_NAME, getStrBranchName());
				strSection8 = strSection8.replace(IF_BRANCH_NAME, getStrBranchName());
				strSection9 = strSection9.replace(IF_BRANCH_NAME, getStrBranchName());
				strSection10 = strSection10.replace(IF_BRANCH_NAME, getStrBranchName());
				strSection11 = strSection11.replace(IF_BRANCH_NAME, getStrBranchName());
			}
			
			if(getStrBranchAddress() != null) {
				strSection1 = strSection1.replace(IF_BRANCH_ADDRESS, getStrBranchAddress());
				strSection2 = strSection2.replace(IF_BRANCH_ADDRESS, getStrBranchAddress());
				strSection3 = strSection3.replace(IF_BRANCH_ADDRESS, getStrBranchAddress());
				strSection4 = strSection4.replace(IF_BRANCH_ADDRESS, getStrBranchAddress());
				strSection5 = strSection5.replace(IF_BRANCH_ADDRESS, getStrBranchAddress());
				strSection6 = strSection6.replace(IF_BRANCH_ADDRESS, getStrBranchAddress());
				strSection7 = strSection7.replace(IF_BRANCH_ADDRESS, getStrBranchAddress());
				strSection8 = strSection8.replace(IF_BRANCH_ADDRESS, getStrBranchAddress());
				strSection9 = strSection9.replace(IF_BRANCH_ADDRESS, getStrBranchAddress());
				strSection10 = strSection10.replace(IF_BRANCH_ADDRESS, getStrBranchAddress());
				strSection11 = strSection11.replace(IF_BRANCH_ADDRESS, getStrBranchAddress());
			}
			
			if(getStrBranchContactNo() != null) {
				strSection1 = strSection1.replace(IF_BRANCH_CONTACT_NO, getStrBranchContactNo());
				strSection2 = strSection2.replace(IF_BRANCH_CONTACT_NO, getStrBranchContactNo());
				strSection3 = strSection3.replace(IF_BRANCH_CONTACT_NO, getStrBranchContactNo());
				strSection4 = strSection4.replace(IF_BRANCH_CONTACT_NO, getStrBranchContactNo());
				strSection5 = strSection5.replace(IF_BRANCH_CONTACT_NO, getStrBranchContactNo());
				strSection6 = strSection6.replace(IF_BRANCH_CONTACT_NO, getStrBranchContactNo());
				strSection7 = strSection7.replace(IF_BRANCH_CONTACT_NO, getStrBranchContactNo());
				strSection8 = strSection8.replace(IF_BRANCH_CONTACT_NO, getStrBranchContactNo());
				strSection9 = strSection9.replace(IF_BRANCH_CONTACT_NO, getStrBranchContactNo());
				strSection10 = strSection10.replace(IF_BRANCH_CONTACT_NO, getStrBranchContactNo());
				strSection11 = strSection11.replace(IF_BRANCH_CONTACT_NO, getStrBranchContactNo());
			}
			
			if(getStrBranchFaxNo() != null) {
				strSection1 = strSection1.replace(IF_BRANCH_FAX_NO, getStrBranchFaxNo());
				strSection2 = strSection2.replace(IF_BRANCH_FAX_NO, getStrBranchFaxNo());
				strSection3 = strSection3.replace(IF_BRANCH_FAX_NO, getStrBranchFaxNo());
				strSection4 = strSection4.replace(IF_BRANCH_FAX_NO, getStrBranchFaxNo());
				strSection5 = strSection5.replace(IF_BRANCH_FAX_NO, getStrBranchFaxNo());
				strSection6 = strSection6.replace(IF_BRANCH_FAX_NO, getStrBranchFaxNo());
				strSection7 = strSection7.replace(IF_BRANCH_FAX_NO, getStrBranchFaxNo());
				strSection8 = strSection8.replace(IF_BRANCH_FAX_NO, getStrBranchFaxNo());
				strSection9 = strSection9.replace(IF_BRANCH_FAX_NO, getStrBranchFaxNo());
				strSection10 = strSection10.replace(IF_BRANCH_FAX_NO, getStrBranchFaxNo());
				strSection11 = strSection11.replace(IF_BRANCH_FAX_NO, getStrBranchFaxNo());
			}
			
			if(getStrBranchEmailId() != null) {
				strSection1 = strSection1.replace(IF_BRANCH_EMAIL_ID, getStrBranchEmailId());
				strSection2 = strSection2.replace(IF_BRANCH_EMAIL_ID, getStrBranchEmailId());
				strSection3 = strSection3.replace(IF_BRANCH_EMAIL_ID, getStrBranchEmailId());
				strSection4 = strSection4.replace(IF_BRANCH_EMAIL_ID, getStrBranchEmailId());
				strSection5 = strSection5.replace(IF_BRANCH_EMAIL_ID, getStrBranchEmailId());
				strSection6 = strSection6.replace(IF_BRANCH_EMAIL_ID, getStrBranchEmailId());
				strSection7 = strSection7.replace(IF_BRANCH_EMAIL_ID, getStrBranchEmailId());
				strSection8 = strSection8.replace(IF_BRANCH_EMAIL_ID, getStrBranchEmailId());
				strSection9 = strSection9.replace(IF_BRANCH_EMAIL_ID, getStrBranchEmailId());
				strSection10 = strSection10.replace(IF_BRANCH_EMAIL_ID, getStrBranchEmailId());
				strSection11 = strSection11.replace(IF_BRANCH_EMAIL_ID, getStrBranchEmailId());
			}
			
			
			if(getStrBranchACNo() != null) {
				strSection1 = strSection1.replace(IF_BRANCH_AC_NO, getStrBranchACNo());
				strSection2 = strSection2.replace(IF_BRANCH_AC_NO, getStrBranchACNo());
				strSection3 = strSection3.replace(IF_BRANCH_AC_NO, getStrBranchACNo());
				strSection4 = strSection4.replace(IF_BRANCH_AC_NO, getStrBranchACNo());
				strSection5 = strSection5.replace(IF_BRANCH_AC_NO, getStrBranchACNo());
				strSection6 = strSection6.replace(IF_BRANCH_AC_NO, getStrBranchACNo());
				strSection7 = strSection7.replace(IF_BRANCH_AC_NO, getStrBranchACNo());
				strSection8 = strSection8.replace(IF_BRANCH_AC_NO, getStrBranchACNo());
				strSection9 = strSection9.replace(IF_BRANCH_AC_NO, getStrBranchACNo());
				strSection10 = strSection10.replace(IF_BRANCH_AC_NO, getStrBranchACNo());
				strSection11 = strSection11.replace(IF_BRANCH_AC_NO, getStrBranchACNo());
			}
			
			if(getStrBranchIFSCCode() != null) {
				strSection1 = strSection1.replace(IF_BRANCH_IFSC_CODE, getStrBranchIFSCCode());
				strSection2 = strSection2.replace(IF_BRANCH_IFSC_CODE, getStrBranchIFSCCode());
				strSection3 = strSection3.replace(IF_BRANCH_IFSC_CODE, getStrBranchIFSCCode());
				strSection4 = strSection4.replace(IF_BRANCH_IFSC_CODE, getStrBranchIFSCCode());
				strSection5 = strSection5.replace(IF_BRANCH_IFSC_CODE, getStrBranchIFSCCode());
				strSection6 = strSection6.replace(IF_BRANCH_IFSC_CODE, getStrBranchIFSCCode());
				strSection7 = strSection7.replace(IF_BRANCH_IFSC_CODE, getStrBranchIFSCCode());
				strSection8 = strSection8.replace(IF_BRANCH_IFSC_CODE, getStrBranchIFSCCode());
				strSection9 = strSection9.replace(IF_BRANCH_IFSC_CODE, getStrBranchIFSCCode());
				strSection10 = strSection10.replace(IF_BRANCH_IFSC_CODE, getStrBranchIFSCCode());
				strSection11 = strSection11.replace(IF_BRANCH_IFSC_CODE, getStrBranchIFSCCode());
			}
			
			if(getStrBranchSwiftCode() != null) {
				strSection1 = strSection1.replace(IF_BRANCH_SWIFT_CODE, getStrBranchSwiftCode());
				strSection2 = strSection2.replace(IF_BRANCH_SWIFT_CODE, getStrBranchSwiftCode());
				strSection3 = strSection3.replace(IF_BRANCH_SWIFT_CODE, getStrBranchSwiftCode());
				strSection4 = strSection4.replace(IF_BRANCH_SWIFT_CODE, getStrBranchSwiftCode());
				strSection5 = strSection5.replace(IF_BRANCH_SWIFT_CODE, getStrBranchSwiftCode());
				strSection6 = strSection6.replace(IF_BRANCH_SWIFT_CODE, getStrBranchSwiftCode());
				strSection7 = strSection7.replace(IF_BRANCH_SWIFT_CODE, getStrBranchSwiftCode());
				strSection8 = strSection8.replace(IF_BRANCH_SWIFT_CODE, getStrBranchSwiftCode());
				strSection9 = strSection9.replace(IF_BRANCH_SWIFT_CODE, getStrBranchSwiftCode());
				strSection10 = strSection10.replace(IF_BRANCH_SWIFT_CODE, getStrBranchSwiftCode());
				strSection11 = strSection11.replace(IF_BRANCH_SWIFT_CODE, getStrBranchSwiftCode());
			}
			
			if(getStrProjectCurrency() != null) {
//				<div style="float: left;">
//	        	<s:hidden name="proCurrency" id="proCurrency"/>
//		        	<s:select id="strCurrency" cssClass="validate[required]" name="strCurrency" listKey="currencyId" listValue="currencyName" headerKey="" 
//		        		headerValue="Select Currency" list="currencyList" key="" required="true" cssStyle="width: 200px;" onchange="getExchangeValue(this.value);"/>
//	        	</div>
	        	
//	        	<div id="exchangeValDIV" style="float: left; margin-left: 10px; margin-top: -10px;"> </div>
				StringBuilder projectCurrency = new StringBuilder();
				projectCurrency.append("<div style=\"float: left;\"><input type=\"hidden\" name=\"proCurrency\" id=\"proCurrency\" value=\""+getStrProjectCurrency()+"\"/>" +
					"<select onchange=\"getExchangeValue(this.value);\" style=\"width: 200px;\" class=\"validateRequired\" id=\"strCurrency\" name=\"strCurrency\">" +
					"<option value=\"\">Select Currency</option>");
				for(int i=0; currencyList != null && !currencyList.isEmpty() && i<currencyList.size(); i++) {
					if(currencyList.get(i).getCurrencyId().equals(getStrProjectCurrency())) {
						projectCurrency.append("<option value="+currencyList.get(i).getCurrencyId()+" selected>"+currencyList.get(i).getCurrencyName()+"</option>");
					} else {
						projectCurrency.append("<option value="+currencyList.get(i).getCurrencyId()+">"+currencyList.get(i).getCurrencyName()+"</option>");
					}
				}
				projectCurrency.append("</select></div>");
				projectCurrency.append("<div id=\"exchangeValDIV\" style=\"float: left; margin-top: 5px;\"> </div>");
				
				strSection1 = strSection1.replace(IF_PROJECT_CURRENCY, projectCurrency.toString());
				strSection2 = strSection2.replace(IF_PROJECT_CURRENCY, projectCurrency.toString());
				strSection3 = strSection3.replace(IF_PROJECT_CURRENCY, projectCurrency.toString());
				strSection4 = strSection4.replace(IF_PROJECT_CURRENCY, projectCurrency.toString());
				strSection5 = strSection5.replace(IF_PROJECT_CURRENCY, projectCurrency.toString());
				strSection6 = strSection6.replace(IF_PROJECT_CURRENCY, projectCurrency.toString());
				strSection7 = strSection7.replace(IF_PROJECT_CURRENCY, projectCurrency.toString());
				strSection8 = strSection8.replace(IF_PROJECT_CURRENCY, projectCurrency.toString());
				strSection9 = strSection9.replace(IF_PROJECT_CURRENCY, projectCurrency.toString());
				strSection10 = strSection10.replace(IF_PROJECT_CURRENCY, projectCurrency.toString());
				strSection11 = strSection11.replace(IF_PROJECT_CURRENCY, projectCurrency.toString());
			}
			
			if(getStrInvoiceNo() != null) {
				strSection1 = strSection1.replace(IF_INVOICE_NO, getStrInvoiceNo());
				strSection2 = strSection2.replace(IF_INVOICE_NO, getStrInvoiceNo());
				strSection3 = strSection3.replace(IF_INVOICE_NO, getStrInvoiceNo());
				strSection4 = strSection4.replace(IF_INVOICE_NO, getStrInvoiceNo());
				strSection5 = strSection5.replace(IF_INVOICE_NO, getStrInvoiceNo());
				strSection6 = strSection6.replace(IF_INVOICE_NO, getStrInvoiceNo());
				strSection7 = strSection7.replace(IF_INVOICE_NO, getStrInvoiceNo());
				strSection8 = strSection8.replace(IF_INVOICE_NO, getStrInvoiceNo());
				strSection9 = strSection9.replace(IF_INVOICE_NO, getStrInvoiceNo());
				strSection10 = strSection10.replace(IF_INVOICE_NO, getStrInvoiceNo());
				strSection11 = strSection11.replace(IF_INVOICE_NO, getStrInvoiceNo());
			}
			
			if(getStrInvoiceGenerationDate() != null) {
				strSection1 = strSection1.replace(IF_INVOICE_GENERATION_DATE, getStrInvoiceGenerationDate());
				strSection2 = strSection2.replace(IF_INVOICE_GENERATION_DATE, getStrInvoiceGenerationDate());
				strSection3 = strSection3.replace(IF_INVOICE_GENERATION_DATE, getStrInvoiceGenerationDate());
				strSection4 = strSection4.replace(IF_INVOICE_GENERATION_DATE, getStrInvoiceGenerationDate());
				strSection5 = strSection5.replace(IF_INVOICE_GENERATION_DATE, getStrInvoiceGenerationDate());
				strSection6 = strSection6.replace(IF_INVOICE_GENERATION_DATE, getStrInvoiceGenerationDate());
				strSection7 = strSection7.replace(IF_INVOICE_GENERATION_DATE, getStrInvoiceGenerationDate());
				strSection8 = strSection8.replace(IF_INVOICE_GENERATION_DATE, getStrInvoiceGenerationDate());
				strSection9 = strSection9.replace(IF_INVOICE_GENERATION_DATE, getStrInvoiceGenerationDate());
				strSection10 = strSection10.replace(IF_INVOICE_GENERATION_DATE, getStrInvoiceGenerationDate());
				strSection11 = strSection11.replace(IF_INVOICE_GENERATION_DATE, getStrInvoiceGenerationDate());
			}
			
			strSection1 = strSection1.replace("\n", "<br/>");
			strSection2 = strSection2.replace("\n", "<br/>");
			strSection3 = strSection3.replace("\n", "<br/>");
			strSection4 = strSection4.replace("\n", "<br/>");
			strSection5 = strSection5.replace("\n", "<br/>");
			strSection6 = strSection6.replace("\n", "<br/>");
			strSection7 = strSection7.replace("\n", "<br/>");
			strSection8 = strSection8.replace("\n", "<br/>");
			strSection9 = strSection9.replace("\n", "<br/>");
			strSection10 = strSection10.replace("\n", "<br/>");
			strSection11 = strSection11.replace("\n", "<br/>");
			
			setStrSection1(strSection1);
			setStrSection2(strSection2);
			setStrSection3(strSection3);
			setStrSection4(strSection4);
			setStrSection5(strSection5);
			setStrSection6(strSection6);
			setStrSection7(strSection7);
			setStrSection8(strSection8);
			setStrSection9(strSection9);
			setStrSection10(strSection10);
			setStrSection11(strSection11);
			
//		}

		Map<String, String> hmParsedContent = new LinkedHashMap<String, String>();
		hmParsedContent.put("SECTION_1", strSection1);
		hmParsedContent.put("SECTION_2", strSection2);
		hmParsedContent.put("SECTION_3", strSection3);
		hmParsedContent.put("SECTION_4", strSection4);
		hmParsedContent.put("SECTION_5", strSection5);
		hmParsedContent.put("SECTION_6", strSection6);
		hmParsedContent.put("SECTION_7", strSection7);
		hmParsedContent.put("SECTION_8", strSection8);
		hmParsedContent.put("SECTION_9", strSection9);
		hmParsedContent.put("SECTION_10", strSection10);
		hmParsedContent.put("SECTION_11", strSection11);
		
//		System.out.println("hmParsedContent ===>> " + hmParsedContent);
		return hmParsedContent;
	}
	
	
	String strOrgLogo;
	String strLegalEntityLogo;
	String strOrgName;
	String strOrgSubTitle;
	String strOrgAddress;
	String strOrgContactNo;
	String strOrgFaxNo;
	String strOrgEmailId;
	String strLegalEntityName;
	String strLegalEntitySubTitle;
	String strLegalEntityAddress;
	String strLegalEntityContactNo;
	
	String strLegalEntityFaxNo;
	String strLegalEntityEmailId;
	String strLegalEntityRegNo;
	String strLegalEntityPanNo;
	String strLegalEntityTanNo;
//	String strLegalEntityECCNo;
	String strWorkLocation;
	String strWorkLocationAddress;
	String strWorkLocationContactNo;
	String strWorkLocationFaxNo;
	String strWorkLocationECCNo1;
	String strWorkLocationECCNo2;
	
	String strWorkLocationEmailId;
	String strWorkLocationsInLegalEntity;
	String strProjectOwnerId;
	String strProjectOwnerName;
	String strProjectOwnerDesig;
	String strProjectOwnerContactNo;
	String strProjectOwnerEmailId;
	String strHRName;
	String strHRContactNo;
	String strHREmailId;
	String strCustomerName;
	String strCustomerAddress;
	
	String strCustomerSPOC;
	String strCustomerContactNo;
	String strCustomerFaxNo;
	String strCustomerEmailId;
	String strBankName;
	String strBranchName;
	String strBranchAddress;
	String strBranchContactNo;
	String strBranchFaxNo;
	String strBranchEmailId;
	
	String strBranchACNo;
	String strBranchIFSCCode;
	String strBranchSwiftCode;
	String strProjectCurrency;
	String strInvoiceNo;
	String strInvoiceGenerationDate;
	List<FillCurrency> currencyList;
	List<FillClientAddress> clientAddressList;
	List<FillBank> bankList;
	
	private void setEmpDetails() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setDomain(strDomain);
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		ResultSet rs=null;

		try {
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public String getStrLegalEntityLogo() {
		return strLegalEntityLogo;
	}

	public void setStrLegalEntityLogo(String strLegalEntityLogo) {
		this.strLegalEntityLogo = strLegalEntityLogo;
	}

	public String getStrOrgName() {
		return strOrgName;
	}

	public void setStrOrgName(String strOrgName) {
		this.strOrgName = strOrgName;
	}

	public String getStrOrgAddress() {
		return strOrgAddress;
	}

	public void setStrOrgAddress(String strOrgAddress) {
		this.strOrgAddress = strOrgAddress;
	}

	public String getStrOrgContactNo() {
		return strOrgContactNo;
	}

	public void setStrOrgContactNo(String strOrgContactNo) {
		this.strOrgContactNo = strOrgContactNo;
	}

	public String getStrOrgFaxNo() {
		return strOrgFaxNo;
	}

	public void setStrOrgFaxNo(String strOrgFaxNo) {
		this.strOrgFaxNo = strOrgFaxNo;
	}

	public String getStrOrgEmailId() {
		return strOrgEmailId;
	}

	public void setStrOrgEmailId(String strOrgEmailId) {
		this.strOrgEmailId = strOrgEmailId;
	}

	public String getStrLegalEntityName() {
		return strLegalEntityName;
	}

	public void setStrLegalEntityName(String strLegalEntityName) {
		this.strLegalEntityName = strLegalEntityName;
	}

	public String getStrLegalEntityAddress() {
		return strLegalEntityAddress;
	}

	public void setStrLegalEntityAddress(String strLegalEntityAddress) {
		this.strLegalEntityAddress = strLegalEntityAddress;
	}

	public String getStrLegalEntityContactNo() {
		return strLegalEntityContactNo;
	}

	public void setStrLegalEntityContactNo(String strLegalEntityContactNo) {
		this.strLegalEntityContactNo = strLegalEntityContactNo;
	}

	public String getStrLegalEntityFaxNo() {
		return strLegalEntityFaxNo;
	}

	public void setStrLegalEntityFaxNo(String strLegalEntityFaxNo) {
		this.strLegalEntityFaxNo = strLegalEntityFaxNo;
	}

	public String getStrLegalEntityEmailId() {
		return strLegalEntityEmailId;
	}

	public void setStrLegalEntityEmailId(String strLegalEntityEmailId) {
		this.strLegalEntityEmailId = strLegalEntityEmailId;
	}

	public String getStrLegalEntityRegNo() {
		return strLegalEntityRegNo;
	}

	public void setStrLegalEntityRegNo(String strLegalEntityRegNo) {
		this.strLegalEntityRegNo = strLegalEntityRegNo;
	}

	public String getStrLegalEntityPanNo() {
		return strLegalEntityPanNo;
	}

	public void setStrLegalEntityPanNo(String strLegalEntityPanNo) {
		this.strLegalEntityPanNo = strLegalEntityPanNo;
	}

	public String getStrLegalEntityTanNo() {
		return strLegalEntityTanNo;
	}

	public void setStrLegalEntityTanNo(String strLegalEntityTanNo) {
		this.strLegalEntityTanNo = strLegalEntityTanNo;
	}

	public String getStrWorkLocation() {
		return strWorkLocation;
	}

	public void setStrWorkLocation(String strWorkLocation) {
		this.strWorkLocation = strWorkLocation;
	}

	public String getStrWorkLocationAddress() {
		return strWorkLocationAddress;
	}

	public void setStrWorkLocationAddress(String strWorkLocationAddress) {
		this.strWorkLocationAddress = strWorkLocationAddress;
	}

	public String getStrWorkLocationContactNo() {
		return strWorkLocationContactNo;
	}

	public void setStrWorkLocationContactNo(String strWorkLocationContactNo) {
		this.strWorkLocationContactNo = strWorkLocationContactNo;
	}

	public String getStrWorkLocationFaxNo() {
		return strWorkLocationFaxNo;
	}

	public void setStrWorkLocationFaxNo(String strWorkLocationFaxNo) {
		this.strWorkLocationFaxNo = strWorkLocationFaxNo;
	}

	public String getStrWorkLocationECCNo1() {
		return strWorkLocationECCNo1;
	}

	public void setStrWorkLocationECCNo1(String strWorkLocationECCNo1) {
		this.strWorkLocationECCNo1 = strWorkLocationECCNo1;
	}

	public String getStrWorkLocationECCNo2() {
		return strWorkLocationECCNo2;
	}

	public void setStrWorkLocationECCNo2(String strWorkLocationECCNo2) {
		this.strWorkLocationECCNo2 = strWorkLocationECCNo2;
	}

	public String getStrWorkLocationEmailId() {
		return strWorkLocationEmailId;
	}

	public void setStrWorkLocationEmailId(String strWorkLocationEmailId) {
		this.strWorkLocationEmailId = strWorkLocationEmailId;
	}

	public String getStrWorkLocationsInLegalEntity() {
		return strWorkLocationsInLegalEntity;
	}

	public void setStrWorkLocationsInLegalEntity(String strWorkLocationsInLegalEntity) {
		this.strWorkLocationsInLegalEntity = strWorkLocationsInLegalEntity;
	}

	public String getStrProjectOwnerId() {
		return strProjectOwnerId;
	}

	public void setStrProjectOwnerId(String strProjectOwnerId) {
		this.strProjectOwnerId = strProjectOwnerId;
	}

	public String getStrProjectOwnerName() {
		return strProjectOwnerName;
	}

	public void setStrProjectOwnerName(String strProjectOwnerName) {
		this.strProjectOwnerName = strProjectOwnerName;
	}

	public String getStrProjectOwnerDesig() {
		return strProjectOwnerDesig;
	}

	public void setStrProjectOwnerDesig(String strProjectOwnerDesig) {
		this.strProjectOwnerDesig = strProjectOwnerDesig;
	}

	public String getStrProjectOwnerContactNo() {
		return strProjectOwnerContactNo;
	}

	public void setStrProjectOwnerContactNo(String strProjectOwnerContactNo) {
		this.strProjectOwnerContactNo = strProjectOwnerContactNo;
	}

	public String getStrProjectOwnerEmailId() {
		return strProjectOwnerEmailId;
	}

	public void setStrProjectOwnerEmailId(String strProjectOwnerEmailId) {
		this.strProjectOwnerEmailId = strProjectOwnerEmailId;
	}

	public String getStrHRName() {
		return strHRName;
	}

	public void setStrHRName(String strHRName) {
		this.strHRName = strHRName;
	}

	public String getStrHRContactNo() {
		return strHRContactNo;
	}

	public void setStrHRContactNo(String strHRContactNo) {
		this.strHRContactNo = strHRContactNo;
	}

	public String getStrHREmailId() {
		return strHREmailId;
	}

	public void setStrHREmailId(String strHREmailId) {
		this.strHREmailId = strHREmailId;
	}

	public String getStrCustomerName() {
		return strCustomerName;
	}

	public void setStrCustomerName(String strCustomerName) {
		this.strCustomerName = strCustomerName;
	}

	public String getStrCustomerAddress() {
		return strCustomerAddress;
	}

	public void setStrCustomerAddress(String strCustomerAddress) {
		this.strCustomerAddress = strCustomerAddress;
	}

	public String getStrCustomerSPOC() {
		return strCustomerSPOC;
	}

	public void setStrCustomerSPOC(String strCustomerSPOC) {
		this.strCustomerSPOC = strCustomerSPOC;
	}

	public String getStrCustomerContactNo() {
		return strCustomerContactNo;
	}

	public void setStrCustomerContactNo(String strCustomerContactNo) {
		this.strCustomerContactNo = strCustomerContactNo;
	}

	public String getStrCustomerFaxNo() {
		return strCustomerFaxNo;
	}

	public void setStrCustomerFaxNo(String strCustomerFaxNo) {
		this.strCustomerFaxNo = strCustomerFaxNo;
	}

	public String getStrCustomerEmailId() {
		return strCustomerEmailId;
	}

	public void setStrCustomerEmailId(String strCustomerEmailId) {
		this.strCustomerEmailId = strCustomerEmailId;
	}

	public String getStrBankName() {
		return strBankName;
	}

	public void setStrBankName(String strBankName) {
		this.strBankName = strBankName;
	}

	public String getStrBranchName() {
		return strBranchName;
	}

	public void setStrBranchName(String strBranchName) {
		this.strBranchName = strBranchName;
	}

	public String getStrBranchAddress() {
		return strBranchAddress;
	}

	public void setStrBranchAddress(String strBranchAddress) {
		this.strBranchAddress = strBranchAddress;
	}

	public String getStrBranchContactNo() {
		return strBranchContactNo;
	}

	public void setStrBranchContactNo(String strBranchContactNo) {
		this.strBranchContactNo = strBranchContactNo;
	}

	public String getStrBranchFaxNo() {
		return strBranchFaxNo;
	}

	public void setStrBranchFaxNo(String strBranchFaxNo) {
		this.strBranchFaxNo = strBranchFaxNo;
	}

	public String getStrBranchEmailId() {
		return strBranchEmailId;
	}

	public void setStrBranchEmailId(String strBranchEmailId) {
		this.strBranchEmailId = strBranchEmailId;
	}

	public String getStrBranchACNo() {
		return strBranchACNo;
	}

	public void setStrBranchACNo(String strBranchACNo) {
		this.strBranchACNo = strBranchACNo;
	}

	public String getStrBranchIFSCCode() {
		return strBranchIFSCCode;
	}

	public void setStrBranchIFSCCode(String strBranchIFSCCode) {
		this.strBranchIFSCCode = strBranchIFSCCode;
	}

	public String getStrBranchSwiftCode() {
		return strBranchSwiftCode;
	}

	public void setStrBranchSwiftCode(String strBranchSwiftCode) {
		this.strBranchSwiftCode = strBranchSwiftCode;
	}

	public String getStrProjectCurrency() {
		return strProjectCurrency;
	}

	public void setStrProjectCurrency(String strProjectCurrency) {
		this.strProjectCurrency = strProjectCurrency;
	}

	public String getStrInvoiceNo() {
		return strInvoiceNo;
	}

	public void setStrInvoiceNo(String strInvoiceNo) {
		this.strInvoiceNo = strInvoiceNo;
	}

	public String getStrInvoiceGenerationDate() {
		return strInvoiceGenerationDate;
	}

	public void setStrInvoiceGenerationDate(String strInvoiceGenerationDate) {
		this.strInvoiceGenerationDate = strInvoiceGenerationDate;
	}

	public String getStrOrgLogo() {
		return strOrgLogo;
	}

	public void setStrOrgLogo(String strOrgLogo) {
		this.strOrgLogo = strOrgLogo;
	}

	public String getStrSection1() {
		return strSection1;
	}

	public void setStrSection1(String strSection1) {
		this.strSection1 = strSection1;
	}

	public String getStrSection2() {
		return strSection2;
	}

	public void setStrSection2(String strSection2) {
		this.strSection2 = strSection2;
	}

	public String getStrSection3() {
		return strSection3;
	}

	public void setStrSection3(String strSection3) {
		this.strSection3 = strSection3;
	}

	public String getStrSection4() {
		return strSection4;
	}

	public void setStrSection4(String strSection4) {
		this.strSection4 = strSection4;
	}

	public String getStrSection5() {
		return strSection5;
	}

	public void setStrSection5(String strSection5) {
		this.strSection5 = strSection5;
	}

	public String getStrSection6() {
		return strSection6;
	}

	public void setStrSection6(String strSection6) {
		this.strSection6 = strSection6;
	}

	public String getStrSection7() {
		return strSection7;
	}

	public void setStrSection7(String strSection7) {
		this.strSection7 = strSection7;
	}

	public String getStrSection8() {
		return strSection8;
	}

	public void setStrSection8(String strSection8) {
		this.strSection8 = strSection8;
	}

	public String getStrSection9() {
		return strSection9;
	}

	public void setStrSection9(String strSection9) {
		this.strSection9 = strSection9;
	}

	public String getStrSection10() {
		return strSection10;
	}

	public void setStrSection10(String strSection10) {
		this.strSection10 = strSection10;
	}

	public String getStrSection11() {
		return strSection11;
	}

	public void setStrSection11(String strSection11) {
		this.strSection11 = strSection11;
	}

	public String getInvoiceFormatId() {
		return invoiceFormatId;
	}

	public void setInvoiceFormatId(String invoiceFormatId) {
		this.invoiceFormatId = invoiceFormatId;
	}

	public String getStrOrgSubTitle() {
		return strOrgSubTitle;
	}

	public void setStrOrgSubTitle(String strOrgSubTitle) {
		this.strOrgSubTitle = strOrgSubTitle;
	}

	public String getStrLegalEntitySubTitle() {
		return strLegalEntitySubTitle;
	}

	public void setStrLegalEntitySubTitle(String strLegalEntitySubTitle) {
		this.strLegalEntitySubTitle = strLegalEntitySubTitle;
	}


	public List<FillCurrency> getCurrencyList() {
		return currencyList;
	}

	public void setCurrencyList(List<FillCurrency> currencyList) {
		this.currencyList = currencyList;
	}

	public List<FillClientAddress> getClientAddressList() {
		return clientAddressList;
	}

	public void setClientAddressList(List<FillClientAddress> clientAddressList) {
		this.clientAddressList = clientAddressList;
	}

	public List<FillBank> getBankList() {
		return bankList;
	}

	public void setBankList(List<FillBank> bankList) {
		this.bankList = bankList;
	}


	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
	}
	
	String strDomain;
	public void setDomain(String strDomain) {
		this.strDomain=strDomain;
	}
	
	
}
