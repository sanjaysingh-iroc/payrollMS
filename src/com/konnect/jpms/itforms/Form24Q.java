package com.konnect.jpms.itforms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class Form24Q extends ActionSupport implements ServletRequestAware, ServletResponseAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	String strUserType;
	String strSessionEmpId;
	
	String strSubmit; 
	String financialYear;
	String strSelectedEmpId;
	String strMonth;
	String strQuarter;
	
	List<FillFinancialYears> financialYearList; 
	List<FillEmployee> empNamesList;
	List<FillMonth> monthList;
	
	String f_strWLocation;
	String f_level;
	String f_org;
	
	List<FillOrganisation> orgList;	
	List<FillWLocation> wLocationList;
	List<FillLevel> levelList;
	
	String formType;
	
	public String execute() throws Exception {
		UtilityFunctions uF = new UtilityFunctions();
				
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		request.setAttribute(PAGE, "/jsp/itforms/Form24Q.jsp");
		request.setAttribute(TITLE, "e-Tds Return");
		
		if(getF_org()==null){
			setF_org((String)session.getAttribute(ORGID));
		}
		
		viewForm24Q(uF);
		
		if(getFormType()!=null && getFormType().equals("txt")){
			generateForm24TXT(uF);
			return "";
		}

		return loadForm24Q(uF);
	}
	

	private void generateForm24TXT(UtilityFunctions uF) {
		try {
			String  strFinancialYearStart = (String)request.getAttribute("strFinancialYearStart");
			String  strFinancialYearEnd = (String)request.getAttribute("strFinancialYearEnd");
			if(strFinancialYearStart!=null && strFinancialYearEnd!=null){
				strFinancialYearStart = uF.getDateFormat(strFinancialYearStart, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
				strFinancialYearEnd = uF.getDateFormat(strFinancialYearEnd, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
			}
			Map<String,String> hmOrg = (Map<String, String>)request.getAttribute("hmOrg"); 
			if(hmOrg == null) hmOrg = new HashMap<String, String>();
			
//			Set<String> empSetList = (Set<String>)request.getAttribute("empSetList");
//			if(empSetList == null) empSetList = new HashSet<String>(); 
			
			Set<String> challanSetList = (Set<String>)request.getAttribute("challanSetList");
			if(challanSetList == null) challanSetList = new HashSet<String>();
			
			Map<String, String> hmEmpProfile = (Map<String, String>) request.getAttribute("hmEmpProfile");
			if(hmEmpProfile == null) hmEmpProfile = new HashMap<String, String>();
			
			Map<String, Map<String, String>> hmEmpDetails = (Map<String, Map<String, String>>) request.getAttribute("hmEmpDetails");
			if(hmEmpDetails == null) hmEmpDetails = new HashMap<String, Map<String, String>>();
//			System.out.println("hmEmpDetails======>"+hmEmpDetails);
			
			Map<String, String> hmStates = (Map<String, String>) request.getAttribute("hmStates");
			if(hmStates == null) hmStates = new HashMap<String, String>();
			
			Map<String, String> hmChallanEmpCnt = (Map<String, String>) request.getAttribute("hmChallanEmpCnt");
			if(hmChallanEmpCnt == null) hmChallanEmpCnt = new HashMap<String, String>(); 
			Map<String, String> hmChallanDate = (Map<String, String>) request.getAttribute("hmChallanDate");
			if(hmChallanDate == null) hmChallanDate = new HashMap<String, String>();
			Map<String, Map<String, String>> hmChallanAmtDetails = (Map<String, Map<String, String>>) request.getAttribute("hmChallanAmtDetails");
			if(hmChallanAmtDetails == null) hmChallanAmtDetails = new HashMap<String, Map<String, String>>();
			
			Map<String, List<Map<String, String>>> hmEmpChallanAmtDetails = (Map<String, List<Map<String, String>>>) request.getAttribute("hmEmpChallanAmtDetails");
			if(hmEmpChallanAmtDetails == null) hmEmpChallanAmtDetails = new HashMap<String, List<Map<String, String>>>();
			
			Map<String, String> hmEmpSalaryAmtDetails = (Map<String, String>) request.getAttribute("hmEmpSalaryAmtDetails");
			if(hmEmpSalaryAmtDetails == null) hmEmpSalaryAmtDetails = new HashMap<String, String>();
			
			Map<String, String> hmEmpTotalSalaryAmt = (Map<String, String>) request.getAttribute("hmEmpTotalSalaryAmt");
			if(hmEmpTotalSalaryAmt == null) hmEmpTotalSalaryAmt = new HashMap<String, String>();
			
			Map<String, String> hmEmpAgeMap = (Map<String, String>) request.getAttribute("hmEmpAgeMap");
			if(hmEmpAgeMap == null) hmEmpAgeMap = new HashMap<String, String>();
			Map<String, String> hmEmpGenderMap = (Map<String, String>) request.getAttribute("hmEmpGenderMap");
			if(hmEmpGenderMap == null) hmEmpGenderMap = new HashMap<String, String>();
			Map<String, String> hmEmpChallanTotalAmt = (Map<String, String>) request.getAttribute("hmEmpChallanTotalAmt");
			if(hmEmpChallanTotalAmt == null) hmEmpChallanTotalAmt = new HashMap<String, String>();
			Map<String, String> hmBRCCode = (Map<String, String>) request.getAttribute("hmBRCCode");
			if(hmBRCCode == null) hmBRCCode = new HashMap<String, String>();
			
			
			String strQuarter = (String) request.getAttribute("QUARTER");
			String cntChallan = (String) request.getAttribute("cntChallan");
			String challanAmt = (String) request.getAttribute("challanAmt"); 
//			System.out.println("cntChallan=======>"+cntChallan);
			String empCount = (String) request.getAttribute("empCount"); 
			
			String strCurrDate = uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, "ddMMyyyy");
			
			String strAssessmentYr = (uF.parseToInt(uF.getDateFormat(strFinancialYearStart,CF.getStrReportDateFormat(), "yyyy"))+1) +""+ (uF.parseToInt(uF.getDateFormat(strFinancialYearEnd,CF.getStrReportDateFormat(), "yy"))+1);
			String strFinancialYr = (uF.parseToInt(uF.getDateFormat(strFinancialYearStart,CF.getStrReportDateFormat(), "yyyy"))) +""+ (uF.parseToInt(uF.getDateFormat(strFinancialYearEnd,CF.getStrReportDateFormat(), "yy")));

//			StringBuilder sb = new StringBuilder();
			StringBuffer sb = new StringBuffer();
			String TILDA  = "^";
			int cnt = 0;
			if(hmOrg.size() > 0){
				cnt++;
				
				String strOrgTanNo = uF.showData(hmOrg.get("ORG_TAN"), "");
				String strOrgPanNo = uF.showData(hmOrg.get("ORG_PAN"), "");
				/**
				 * M=Mandory, O=Optional, NA
				 * 
				 * TDS Statement for Salary category (File Header Record) start
				 * */
				sb.append(""+cnt); //M - Line Number[Running Sequence Number for each line in the file.]
				sb.append(TILDA);
				sb.append("FH"); //M -Record Type[Value should be "FH" signifying 'File Header' record]
				sb.append(TILDA);
				sb.append("SL1"); //M -File Type[Value should be  "SL1"]
				sb.append(TILDA);
				sb.append("R"); //M -Upload Type[Value should be "R"]
				sb.append(TILDA);
				sb.append(strCurrDate); //M -File Creation Date[Mention the date of creation of the file in ddmmyyyy format.]
				sb.append(TILDA);
				sb.append("1"); //M - need to discuss File Sequence No. [Indicates the running sequence number for the file. (Should be unique across all the files)]
				sb.append(TILDA); 
				sb.append("D"); //M -Uploader Type pValue should 'D']
				sb.append(TILDA);
				sb.append(strOrgTanNo.trim().toUpperCase()); //M -TAN of Employer
				sb.append(TILDA); 
				sb.append("1");  //M -Total No. of Batches [Value should be '1']
				sb.append(TILDA); 
				sb.append("");  //M -Name of Return Preparation Utility [Name of the software used for preparing the Quarterly e-TDS/TCS statement should be mentioned.]
				sb.append(TILDA); 
				sb.append("");  //NA -Record Hash (Not applicable) [No value should be specified] 
				sb.append(TILDA);
				sb.append("");  //NA -FVU Version (Not applicable) [No value should be specified] 
				sb.append(TILDA);
				sb.append("");  //NA -File Hash (Not applicable) [No value should be specified] 
				sb.append(TILDA);
				sb.append("");  //NA -Sam Version (Not applicable)[No value should be specified] 
				sb.append(TILDA);
				sb.append("");  //NA -SAM Hash (Not applicable)[No value should be specified] 
				sb.append(TILDA);
				sb.append("");  //NA -SCM Version (Not applicable)[No value should be specified] 
				sb.append(TILDA);
				sb.append("");  //NA -SCM Hash (Not applicable)[No value should be specified] 
				sb.append(TILDA);
				sb.append("");  //NA -Consolidated file hash [No value should be specified] 
				
				sb.append(System.getProperty("line.separator"));
				
				/**
				 * TDS Statement for Salary category (File Header Record) end 
				 * */
				
				/**
				 * TDS Statement for Salary category (Batch Header Record) start 
				 * */
				cnt++;
				
				sb.append(""+cnt); //M -Line Number
				sb.append(TILDA);
				sb.append("BH"); //M -Record Type [Value should be "BH"]
				sb.append(TILDA);
				sb.append("1"); //M -Batch Number
				sb.append(TILDA);
				sb.append((uF.parseToInt(cntChallan) > 0 ? cntChallan : "" )); //M -Count of Challan/transfer voucher Records [Count of total number of challans/transfer vouchers contained within the batch. Must be equal to the total number of 'Challans' included in this batch.]
				sb.append(TILDA);
				sb.append("24Q"); //M -Form Number [Value should be "24Q"]
				sb.append(TILDA);
				sb.append(""); //O -Transaction Type (Not applicable) [No value should be specified]
				sb.append(TILDA);
				sb.append(""); //O -Batch Updation Indicator (Not applicable) [No value should be specified]
				sb.append(TILDA);
				sb.append(""); //O -Original Token Number(Token Number of Regular statement) - (Not applicable) of the statement [No value should be specified]
				sb.append(TILDA);
				sb.append(""); //O -Token no. of previous regular statement (Form no. 24Q). [If value present in field no. 52 is "Y", mandatory to mention 15 digit Token number of immediate previous regular statement for Form 24Q, else no value to be provided.] 
				sb.append(TILDA);
				sb.append(""); //O -Token Number of the statement submitted - (Not applicable)  [No value should be specified]
				sb.append(TILDA);
				sb.append(""); //O -Token Number date - (Not applicable)  [No value should be specified]
				sb.append(TILDA);
				sb.append(""); //O -Last TAN of Deductor / Employer / Collector ( Used for Verification)  (Not applicable) [No value should be specified]
				sb.append(TILDA);
				sb.append(strOrgTanNo.trim().toUpperCase()); //M -TAN of Employer [Mention the 10 Character  TAN of the deductor.  Should be all CAPITALS.]
				sb.append(TILDA); 
				sb.append(""); //NA -Receipt number (eight digit) provided by TIN [No value should be specified]
				sb.append(TILDA); 
				sb.append(strOrgPanNo.trim().toUpperCase()); //M -PAN of Deductor / Employer [Mandatory to mention the PAN  of the Deductor. If deductor is not required to have a PAN mention PANNOTREQD]
				sb.append(TILDA); 
				sb.append(strAssessmentYr); //M -Assessment Yr [Assessment year e.g. value should be 200809 for Assessment Year 2008-09. Value should be greater than or equal to 200809.]
				sb.append(TILDA);
				sb.append(strFinancialYr); //M -Financial Yr [Financial year e.g. value should be 200708 for Financial Yr 2007-08. 'Assessment year' - 'Financial Year' must be = 1. The financial Year cannot be a future financial year. Value should be greater than or equal to 200708]
				sb.append(TILDA);
				sb.append(uF.showData(strQuarter, "")); //M -Period [Valid values Q1, Q2, Q3, Q4. Q1 for 1st Quarter, Q2 for 2nd Quarter, Q3 for 3rd Quarter and Q4 for 4th Quarter.]
				sb.append(TILDA);
				sb.append(uF.showData(hmOrg.get("ORG_NAME"), "")); //M -Name of Employer / Deductor [Mention the Name of the  Employer / Deductor  I.e. Employer / Deductor  who deducts tax.]
				sb.append(TILDA);
				sb.append(""); //O -Employer  / Deductor Branch/ Division [Branch/Division of Deductor.]
				sb.append(TILDA);
				sb.append(uF.showData(hmOrg.get("ORG_ADDRESS"), "")); //M -Employer / Deductor  Address1 [Mention the address line 1 of the Employer.]
				sb.append(TILDA);
				sb.append(""); //O -Employer / Deductor  Address2 [Mention the address line 2 of the Employer.]
				sb.append(TILDA);
				sb.append(""); //O -Employer / Deductor  Address3 [Mention the address line 3 of the Employer.]
				sb.append(TILDA);
				sb.append(""); //O -Employer / Deductor  Address4 [Mention the address line 4 of the Employer.]
				sb.append(TILDA);
				sb.append(""); //O -Employer / Deductor  Address5 [Mention the address line 5 of the Employer.]
				sb.append(TILDA);
				sb.append(uF.showData(hmStates.get(hmOrg.get("ORG_STATE_ID")), "")); //M -Employer  / Deductor  State [Numeric code for state. For list of State codes, refer to the Annexure 1 below.]
				sb.append(TILDA);
				sb.append(uF.showData(hmOrg.get("ORG_PINCODE"), "")); //M -Employer  / Deductor  PIN [PIN Code of Employer / Deductor.]
				sb.append(TILDA);
				sb.append(uF.showData(hmOrg.get("ORG_EMAIL"), "")); //O -Employer  / Deductor Email ID
				sb.append(TILDA);
				sb.append(""); //O -Employer  / Deductor's STD code [Mention STD code if value present in field no.30 (Employer / Deductor's Tel-Phone No.).]
				sb.append(TILDA);
				sb.append(uF.showData(hmOrg.get("ORG_CONTACT"), "")); //O -Employer  / Deductor 's Tel-Phone No.[Mention telephone number if value present in field no.29 (Employer / Deductor's STD code). Either mobile no. should be provided or Telephone no. and STD code of deductor or responsible person should be provided.]
				sb.append(TILDA);
				sb.append("N"); //M -Change of Address of employer / Deductor  since last Return ["Y" if address of employer has changed after filing last return, "N" otherwise.]
				sb.append(TILDA);
				sb.append("A"); //M -Deductor Type [Deductor category code to be mentioned as per Annexure 4] 
				sb.append(TILDA);
				sb.append(uF.showData(hmEmpProfile.get("NAME"), "")); //M -Name of Person responsible for paying salary / Deduction [Mention the Name of Person responsible for paying salary on behalf of the deductor.] 
				sb.append(TILDA);
				sb.append(uF.showData(hmEmpProfile.get("DESIGNATION_NAME"), "")); //M -Designation of the Person responsible for paying salary / Deduction [Mention the designation of Person responsible for paying salary on behalf of the deductor.] 
				sb.append(TILDA);
				sb.append(uF.showData(hmEmpProfile.get("CURRENT_ADDRESS"), "")); //M -Responsible Person's  Address1 [Mention the address line 1 of the responsible Person.]
				sb.append(TILDA);
				sb.append(""); //O -Responsible Person's  Address2 [Mention the address line 2 of the responsible Person.]
				sb.append(TILDA);
				sb.append(""); //O -Responsible Person's  Address3 [Mention the address line 3 of the responsible Person.]
				sb.append(TILDA);
				sb.append(""); //O -Responsible Person's  Address4 [Mention the address line 4 of the responsible Person.]
				sb.append(TILDA);
				sb.append(""); //O -Responsible Person's  Address5 [Mention the address line 5 of the responsible Person.]
				sb.append(TILDA);
				sb.append(uF.showData(hmEmpProfile.get("CURRENT_STATE"), "")); //M -Responsible Person's State [Numeric code for state. For list of State codes, refer to the Annexure below.]
				sb.append(TILDA);
				sb.append(uF.showData(hmEmpProfile.get("CURRENT_PINCODE"), "")); //M -Responsible Person's PIN [PIN Code of Responsible Person.]
				sb.append(TILDA);
				sb.append(uF.showData(hmEmpProfile.get("EMP_EMAIL"), "")); //O -Responsible Person's Email ID
				sb.append(TILDA);
				String strContMob = uF.showData(hmEmpProfile.get("CONTACT_MOB"), "");
				sb.append((strContMob.length() == 10 ? strContMob : "")); //O -Mobile number [Mention 10 digit mobile no. Mandatory for Deductor category other than Central Govt. and State Govt. For deductor category Central Govt. and State Govt. either mobile no. should be provided or Telephone no. and STD code of deductor or responsible person should be provided.]
				sb.append(TILDA);
				sb.append(""); //O -Responsible Person's STD Code [Mention STD code if value present in field no.45] 
				sb.append(TILDA);
				String strCont = uF.showData(hmEmpProfile.get("CONTACT"), "");
				sb.append((strCont.length() == 10 ? strCont : "")); //O -Responsible Person's Tel-Phone No. [Mention telephone number if value present in field no.44 (Responsible Person's STD code). Either mobile no. should be provided or Telephone no. and STD code of deductor or responsible person should be provided.]
				sb.append(TILDA);
				sb.append("N"); //M -Change of Address of Responsible person since last Return ["Y" if address has changed after filing last return, "N" otherwise.]
				sb.append(TILDA);
				sb.append((uF.parseToDouble(challanAmt) > 0 ? uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(challanAmt)) : "")); //M -Batch Total of - Total of Deposit Amount as per Challan [Mention the Total of Deposit Amount as per Challan.The value here should be same as sum of values in field 'Total of Deposit Amount as per Challan'  in the 'Challan Detail' record across all Challans in the batch. Only Integer values are allowed for this field. The value of 1000 should be represented as 1000.00 in this field.]
				sb.append(TILDA);
				sb.append(""); //NA -Unmatched challan count [No value to be specified.]
				sb.append(TILDA);
				sb.append((uF.parseToInt(empCount) > 0 ? empCount : "")); //M -Count of Salary Details  Records [Count of total number of Salary Detail  Records' within a batch ,Value Should be  >= 0, ]
				sb.append(TILDA);
				sb.append(""); //O -Batch Total of - Gross Total Income as per Salary Detail [No value to be specified.]
				sb.append(TILDA);
				sb.append("N"); //M -AO Approval [Value should be "N"]
				sb.append(TILDA);
				sb.append("N"); //M -Whether regular statement for Form 24Q filed for earlier period ["Y" if regular statement for Form 24Q has been filed for earlier period, else value "N" should be provided.]
				sb.append(TILDA);
				sb.append(""); //NA -Last Deductor Type [No value should be specified]
				sb.append(TILDA);
				sb.append(""); //O -State Name [Numeric code for state should be mentioned as per Annexure 5. Mandatory if deductor type is State Govt. (code S), Statutory body - State Govt. (code E), Autonomous body - State Govt. (code H) and Local Authority - State Govt. (code N). For other deductor category no value should be provided.]
				sb.append(TILDA);
				sb.append(""); //O -PAO Code [Mandatory for central govt (A). Optional for deductor type State Govt. (S), Statutory body - Central Govt. (D), Statutory body - State Govt. (E), Autonomous body - Central Govt. (G), Autonomous body - State Govt. (H), Local Authority - Central Govt. (L) & Local Authority - State Govt. (N). For other deductor type no value should be provided.]
				sb.append(TILDA);
				sb.append(""); //O -DDO Code [Mandatory for deductor type Central Government (A). Optional for deductor type State Government (S), Statutory body - Central Govt. (D), Statutory body - State Govt. (E), Autonomous body - Central Govt. (G), Autonomous body - State Govt. (H), Local Authority -Central Govt. (L) & Local Authority - State Govt. (N). For other deductor type no value should be provided.]
				sb.append(TILDA);
				sb.append(""); //O -Ministry Name [Numeric code for Ministry name should be provided. For list of Ministry name codes, refer to the Annexure 3 below. Mandatory for deductor type Central Govt (A), Statutory body - Central Govt. (D) & Autonomous body - Central Govt. (G). Optional for deductor type Statutory body - State Govt. (E), Autonomous body - State Govt. (H), Local Authority - Central Govt. (L) & Local Authority -State Govt. (N). For other deductor type no value should be provided.]
				sb.append(TILDA);
				sb.append(""); //O -Ministry Name Other [If numeric code '99' (i.e. Other) is provided in Ministry Name field then value in Ministry Name "Other" field should be provided]
				sb.append(TILDA);
				sb.append(""); //O -TAN Registration Number [Mention the TAN registration no.(if available) as generated at TIN. Value to be mentioned only for statements pertaining to FY 2013-14 onwards.]
				sb.append(TILDA);
				sb.append(""); //O -PAO Registration No [Optional for deductor type Central Govt. (A), State Govt. (S), Statutory Body - Central Govt. (D), Statutory Body - State Govt. (E), Autonomous body - Central Govt. (G), Autonomous body - State Govt. (H), Local Authority - Central Govt. (L) & Local Authority - State  Govt. (N). For other deductor type no value should be provided.]
				sb.append(TILDA);
				sb.append(""); //O -DDO Registration No [Optional for deductor type Central Govt. (A), State Govt. (S), Statutory Body - Central Govt. (D), Statutory Body - State Govt. (E), Autonomous body - Central Govt. (G), Autonomous body - State Govt. (H), Local Authority - Central Govt. (L) & Local Authority - State  Govt. (N). For other deductor type no value should be provided.]
				sb.append(TILDA);
				sb.append(""); //O -Employer  / Deductor's STD code (Alternate) [Mention STD code if value present in field no.63 (Employer / Deductor's Tel-Phone No. - Alternate). Value to be mentioned only for statements pertaining to FY 2013-14 onwards.]
				sb.append(TILDA);
				sb.append(""); //O -Employer  / Deductor 's Tel-Phone No. (Alternate) [Mention telephone number if value present in field no.62 (Employer / Deductor's STD code - Alternate). Value to be mentioned only for statements pertaining to FY 2013-14 onwards.]
				sb.append(TILDA);
				sb.append(""); //O -Employer  / Deductor Email ID (Alternate)
				sb.append(TILDA);
				sb.append(""); //O -Responsible Person's STD Code (Alternate) [Mention STD code if value present in field no.66 (Responsible Person's Tel-Phone No. -  Alternate). Value to be mentioned only for statements pertaining to FY 2013-14 onwards.]
				sb.append(TILDA);
				sb.append(""); //O -Responsible Person's Tel-Phone No. (Alternate) [Mention telephone number if value present in field no.65 (Responsible Person's STD code - Alternate). Value to be mentioned only for statements pertaining to FY 2013-14 onwards.]
				sb.append(TILDA);
				sb.append(""); //O -Responsible Person's Email ID (Alternate)
				sb.append(TILDA);
				sb.append(""); //O -Account Office Identification Number (AIN) of PAO/ TO/ CDDO [Applicable only in case the deductor category is "Central Govt." or "State Govt.". Mention AIN of the below:
								//1) Pay and Account Office (PAO)2) Treasure Office (TO)3) Cheque Drawing and Disbursing Officer (CDDO)	Mandatory to mention value for statements pertaining to FY 2013-14 onwards.]
				sb.append(TILDA);
				sb.append(""); //NA -Record Hash  (Not applicable) [No value should be specified]
				
				sb.append(System.getProperty("line.separator"));
				/**
				 * TDS Statement for Salary category (Batch Header Record) end 
				 * */
				
				/**
				 * TDS Statement for Salary category (Challan / Transfer Voucher Detail Record) start
				 * */
				Set<String> empSetList = new HashSet<String>();
				Map<String, Map<String, String>> hmEmpTotalOtherAmt = new HashMap<String, Map<String,String>>();
				Iterator<String> it = challanSetList.iterator();
				int nCD = 0;
				while(it.hasNext()){
					String strChallanNo = it.next();
					
					cnt++;
					nCD++;
					
					sb.append(""+cnt); //M -Line Number [Running sequence number for each line in the file]
					sb.append(TILDA);
					sb.append("CD"); //M -Record Type [Value should be "CD"]
					sb.append(TILDA);
					sb.append("1"); //M -Batch Number [Value should be same as 'Batch Number' field in 'Batch Header' record]
					sb.append(TILDA);
					sb.append(""+nCD); //M -301-Challan-Detail Record Number [Running serial number for 'Challan Detail' records in a batch.  Should start with 1]
					sb.append(TILDA);
					String strChallanEmpCnt = hmChallanEmpCnt.get(strChallanNo);
					sb.append((uF.parseToInt(strChallanEmpCnt) > 0 ? strChallanEmpCnt : "")); //M -Count of Deductee / Party Records [Count of total number of 'Deductee Detail Records' within e-TDS statement, Value Should be  >= 0]
					sb.append(TILDA);
					sb.append("N"); //M -NIL Challan Indicator [Value should be "N". In cases where no tax has been deposited in bank, value should be "Y" (applicable in case of NIL return)]
					sb.append(TILDA);
					sb.append(""); //O -Challan Updation Indicator(Not applicable) [No value should be specified]
					sb.append(TILDA);
					sb.append(""); //O -Filler 2  (Not applicable) [No value should be specified]
					sb.append(TILDA);
					sb.append(""); //O -Filler 3  (Not applicable)(Not applicable) [No value should be specified]
					sb.append(TILDA);
					sb.append(""); //O -Filler 4  (Not applicable)(Not applicable) [No value should be specified]
					sb.append(TILDA);
					sb.append(""); //O -Last Challan Serial No. (Used for Verification)(Not applicable) [Not applicable to regular statement]
					sb.append(TILDA);
					sb.append(strChallanNo); //O -310-Challan Serial No. [Challan Number issued by Bank . Applicable to both Govt and Non Govt, Non-Nil statements.  No value to be provided if value in field "NIL Challan Indicator" is "Y". No value to be provided if tax deposited by book entry.]
					sb.append(TILDA);
					sb.append(""); //O -Last DDO Serial No. of Form 24G ( Used for Verification)  (Not applicable) [No value should be specified]
					sb.append(TILDA);
					sb.append(""); //O -310-DDO Serial No. of Form No. 24G  [ 1) Applicable only in case of a Government deductor/collector where TDS/TCS has been deposited by Book entry.2) Quote the five digit DDO serial number provided by Accounts Officer (AO)
									//3) No value should be present in this column in case of a NIL Statement  (value in field "NIL Challan Indicator" field is "Y")]
					sb.append(TILDA);
					sb.append(""); //O -Last Bank-Branch Code/ Form 24G Receipt Number ( Used for Verification) (Not applicable) [No value should be specified]
					sb.append(TILDA);
					 
					sb.append(uF.showData(hmBRCCode.get(strChallanNo), "")); //M -309-Bank-Branch Code/ Form 24G Receipt Number [In case TDS deposited by 1) Challan:BSR Code of the receiving branch
									//2) Transfer voucher: Quote seven digit receipt number provided by AO. Applicable for govt. deductor/ collector where TDS is deposited by book entry. 
									//3) No value to be quoted in case of Nil Statement (value in field "NIL Challan Indicator" field is "Y").]
					sb.append(TILDA);
					sb.append(""); //O -Last Date of 'Bank Challan / Transfer Voucher ( Used for Verification)(Not applicable) [No value should be specified]
					sb.append(TILDA);
					sb.append(uF.showData(hmChallanDate.get(strChallanNo), "")); //M -311-Date of 'Bank Challan / Transfer Voucher' [Date of payment of tax to Govt. It can be any date on or after 1st April of immediate previous financial year for which the return is prepared. Value should be equal to last date of respective quarter if the value in field "NIL Challan Indicator" is "Y".]
					sb.append(TILDA);
					sb.append(""); //O -Filler 5  (Not applicable) [No value should be specified]
					sb.append(TILDA);
					sb.append(""); //O -Filler 6  (Not applicable) [No value should be specified]
					sb.append(TILDA);
					sb.append("92A"); //M -Section [Mention section code as per Annexure 2. Applicable for the statements upto FY 2012-13. No value to be provided for the statements from FY 2013-14 onwards.]
					sb.append(TILDA);
					
					// INTEREST_AMT PENALTY_AMT UNDER_SECTION234 SURCHARGE EDU_CESS INCOME_TAX TOTAL_AMT
					Map<String, String> hmChallanAmt = (Map<String, String>) hmChallanAmtDetails.get(strChallanNo);
					if(hmChallanAmt == null) hmChallanAmt = new HashMap<String, String>();
					
					sb.append((uF.parseToDouble(hmChallanAmt.get("INCOME_TAX")) > 0.0d ? uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmChallanAmt.get("INCOME_TAX"))) : "" )); //M -302- 'Oltas  TDS / TCS -Income Tax ' [Mention the amount of "Income Tax" out of the 'Total tax deposited' through Challan. No fractional portion is allowed in this field (value should be integer) , I.e. value "1000.50" will not be allowed, whereas value "1000.00" will be considered to be valid value.]
					sb.append(TILDA);
					sb.append((uF.parseToDouble(hmChallanAmt.get("SURCHARGE")) > 0.0d ? uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmChallanAmt.get("SURCHARGE"))) : "" )); //M - 'Oltas TDS / TCS  -Surcharge ' [Mention the amount of "Surcharge" out of the 'Total tax deposited' through Challan. No fractional portion is allowed in this field (value should be integer) , I.e. value "1000.50" will not be allowed, whereas value "1000.00" will be considered to be valid value.]
					sb.append(TILDA);
					sb.append((uF.parseToDouble(hmChallanAmt.get("EDU_CESS")) > 0.0d ? uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmChallanAmt.get("EDU_CESS"))) : "" )); //M -303- 'Oltas TDS / TCS - Cess' [Mention the amount of "Education Cess" out of the 'Total tax deposited' through Challan. No fractional portion is allowed in this field (value should be integer) , I.e. value "1000.50" will not be allowed, whereas value "1000.00" will be considered to be valid value.]
					sb.append(TILDA);
					sb.append((uF.parseToDouble(hmChallanAmt.get("INTEREST_AMT")) > 0.0d ? uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmChallanAmt.get("INTEREST_AMT"))) : "" )); //M -304-Oltas TDS / TCS - Interest Amount [Mention the amount of "Interest" out of the 'Total tax deposited' through Challan. No fractional portion is allowed in this field (value should be integer) , I.e. value "1000.50" will not be allowed, whereas value "1000.00" will be considered to be valid value.]
					sb.append(TILDA);
					double otherAmt = uF.parseToDouble(hmChallanAmt.get("PENALTY_AMT")) + uF.parseToDouble(hmChallanAmt.get("UNDER_SECTION234"));
					sb.append((otherAmt > 0.0d ? uF.formatIntoTwoDecimalWithOutComma(otherAmt) : "")); //M -306- Oltas TDS / TCS - Others (amount) [Mention the amount of "Other Amount" out of the 'Total tax deposited' through Challan. No fractional portion is allowed in this field (value should be integer) , I.e. value "1000.50" will not be allowed, whereas value "1000.00" will be considered to be valid value.]
					sb.append(TILDA);
					sb.append((uF.parseToDouble(hmChallanAmt.get("TOTAL_AMT")) > 0.0d ? uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmChallanAmt.get("TOTAL_AMT"))) : "" )); //M -307- Total of Deposit Amount as per Challan/Transfer Voucher Number  ('Oltas TDS/ TCS -Income Tax ' +  'Oltas TDS / TCS  -Surcharge ' +  'Oltas TDS/ TCS - Cess'  +  Oltas TDS/ TCS - Interest Amount + Fee + Oltas TDS/ TCS - Others (amount) [Mention the amount of 'Total tax deposited' through Challan. No fractional portion is allowed in this field (value should be integer) , I.e. value "1000.50" will not be allowed, whereas value "1000.00" will be considered to be valid value. Value in this field should be equal to total of values in fields with field nos. 22, 23, 24, 25, 39 and 26 In case of challan, value in this field should be greater than or equal to: Total tax deposited amount (field no. 19 of deductee details) + Interest amount (field no. 34 of challan details) + Others amount (field no. 35 of challan details) + Fee amount (field no. 39 of challan details) In case of transfer voucher (tax deposited by book entry), value in this field should be greater than or equal to Total tax deposited amount (field no. 19 of deductee details).]
					sb.append(TILDA);
					sb.append(""); //O -Last Total of Deposit Amount as per Challan ( Used for Verification) [No value should be specified]
					sb.append(TILDA);
					sb.append((uF.parseToDouble(hmChallanAmt.get("TOTAL_AMT")) > 0.0d ? uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmChallanAmt.get("TOTAL_AMT"))) : "" )); //M -Total Tax Deposit Amount as per deductee annexure  (Total Sum of 324) [Mention the sum of  'Deductee Deposit Amount' of the underlying Deductee Records]
					sb.append(TILDA);
					sb.append((uF.parseToDouble(hmChallanAmt.get("INCOME_TAX")) > 0.0d ? uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmChallanAmt.get("INCOME_TAX"))) : "" )); //M - 'TDS / TCS -Income Tax ' [Total sum of field no. 14 (of the deductee details) for the respective Challan]
					sb.append(TILDA);
					sb.append((uF.parseToDouble(hmChallanAmt.get("SURCHARGE")) > 0.0d ? uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmChallanAmt.get("SURCHARGE"))) : "" )); //M - 'TDS / TCS -Surcharge ' [Total sum of field no. 15 (of the deductee details) for the respective Challan.]
					sb.append(TILDA);
					sb.append((uF.parseToDouble(hmChallanAmt.get("EDU_CESS")) > 0.0d ? uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmChallanAmt.get("EDU_CESS"))) : "" )); //M - 'TDS / TCS - Cess' [Total sum of field no. 16 (of the deductee details) for the respective Challan]
					sb.append(TILDA);
					sb.append(""); //M - Sum of 'Total Income Tax Deducted at Source' (TDS/ TCS - Income Tax + TDS/TCS - Cess ) [Total sum of field no. 17 (of the deductee details) for the respective Challan]
					sb.append(TILDA);
					sb.append((uF.parseToDouble(hmChallanAmt.get("INTEREST_AMT")) > 0.0d ? uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmChallanAmt.get("INTEREST_AMT"))) : "" )); //M - TDS/ TCS - Interest Amount [Statement Interest amount as per the respective deductee Annexure. Only integer values are allowed for this field. The value of 1000 should be represented as 1000.00 in this field. Mention value as provided n field no. 25.]
					sb.append(TILDA);
					sb.append((otherAmt > 0.0d ? uF.formatIntoTwoDecimalWithOutComma(otherAmt) : "")); //M - TDS / TCS - Others (amount) [Statement Other amount as per the respective deductee Annexure. Only integer values are allowed for this field. The value of 1000 should be represented as 1000.00 in this field. Mention value as provided n field no. 26.]
					sb.append(TILDA);
					sb.append(""); //O - Cheque / DD No. (if any) [Applicable for the statements upto FY 2012-13. No value to be provided for the statements from FY 2013-14 onwards.]
					sb.append(TILDA);
					sb.append(""); //O -308- By Book entry / Cash [Allowed values - Y/N. If Transfer Voucher Number is provided this is mandatory and only allowed value is 'Y'. If Bank Challan Number is provided , then mention value as "N". For a Nil statement no value to be provided.]
					sb.append(TILDA);
					sb.append(""); //O -Remarks [No value should be specified]
					sb.append(TILDA);
					sb.append(""); //O -305-Fee [Fee paid under section 234E for late filing of TDS statement.Mention the amount of "Late filing Fee" out of the total tax deposited deposited through Challan/ Transfer Voucher. No fractional portion is allowed in this field (value should be integer) , I.e. value "1000.50" will not be allowed, whereas value "1000.00" will be considered to be valid value. Value to be mentioned only for statements pertaining to FY 2012-13 onwards. If not applicable mention "0.00".]
					sb.append(TILDA);
					sb.append(""); //O -312-Minor Head of Challan [Mention value as per Annexure 7. No value to be quoted for statements pertaining prior to FY 2013-14. Mandatory to mention value for statements pertaining to FY 2013-14 onwards, if the deposit of tax is through challan. No value to be quoted for nil challan/ transfer voucher.]
					sb.append(TILDA);
					sb.append(""); //O -Record Hash  (Not applicable) [No value should be specified]
					
					sb.append(System.getProperty("line.separator"));
					
					double dblIncomeTax = (uF.parseToDouble(hmChallanAmt.get("INCOME_TAX")) > 0 && uF.parseToInt(strChallanEmpCnt) > 0) ? (uF.parseToDouble(hmChallanAmt.get("INCOME_TAX"))/uF.parseToInt(strChallanEmpCnt)) : 0.0d ;
					double dblSurcharge = (uF.parseToDouble(hmChallanAmt.get("SURCHARGE")) > 0 && uF.parseToInt(strChallanEmpCnt) > 0) ? (uF.parseToDouble(hmChallanAmt.get("SURCHARGE"))/uF.parseToInt(strChallanEmpCnt)) : 0.0d ;
					double dblEduCess = (uF.parseToDouble(hmChallanAmt.get("EDU_CESS")) > 0 && uF.parseToInt(strChallanEmpCnt) > 0) ? (uF.parseToDouble(hmChallanAmt.get("EDU_CESS"))/uF.parseToInt(strChallanEmpCnt)) : 0.0d ;
					double dblInterestAmt = (uF.parseToDouble(hmChallanAmt.get("INTEREST_AMT")) > 0 && uF.parseToInt(strChallanEmpCnt) > 0) ? (uF.parseToDouble(hmChallanAmt.get("INTEREST_AMT"))/uF.parseToInt(strChallanEmpCnt)) : 0.0d ;
					double dblOtherAmt = ((uF.parseToDouble(hmChallanAmt.get("PENALTY_AMT")) + uF.parseToDouble(hmChallanAmt.get("UNDER_SECTION234"))) > 0 && uF.parseToInt(strChallanEmpCnt) > 0) ? ((uF.parseToDouble(hmChallanAmt.get("PENALTY_AMT")) + uF.parseToDouble(hmChallanAmt.get("UNDER_SECTION234")))/uF.parseToInt(strChallanEmpCnt)) : 0.0d ;
					
					
					/**
					 * Note: A TDS Statement corresponds to a TDS Challan I.e. 1 TDS Statement will always contain 1 Challan only
					 * TDS Statement for Salary category (Deductee Detail Record) start
					 * */
					List<Map<String, String>> empList = (List<Map<String, String>>) hmEmpChallanAmtDetails.get(strChallanNo); 
					if(empList == null) empList = new ArrayList<Map<String,String>>(); 
//					System.out.println("empList=====>"+empList.toString());
					int nDD = 0;
					for(int i = 0; i < empList.size(); i++){
						Map<String, String> hmEmpChallanAmt = empList.get(i); 
						Map<String, String> hmEmpInner = (Map<String, String>) hmEmpDetails.get(hmEmpChallanAmt.get("EMP_ID")); 
						if(hmEmpInner == null) hmEmpInner = new HashMap<String, String>();
						
						empSetList.add(hmEmpInner.get("EMP_ID")); 
						
						Map<String, String> hmEmpOtherInner = hmEmpTotalOtherAmt.get(hmEmpInner.get("EMP_ID"));
						if(hmEmpOtherInner == null) hmEmpOtherInner = new HashMap<String, String>();
						
						double dblEmpSurchargeTotal = uF.parseToDouble(hmEmpOtherInner.get("EMP_SURCHARGE_TOTAL"));
						dblEmpSurchargeTotal +=dblSurcharge;
						hmEmpOtherInner.put("EMP_SURCHARGE_TOTAL", ""+dblEmpSurchargeTotal);
						
						double dblEmpEduCessTotal = uF.parseToDouble(hmEmpOtherInner.get("EMP_EDU_CESS_TOTAL"));
//						dblEmpEduCessTotal +=dblEduCess;
						dblEmpEduCessTotal += uF.parseToDouble(hmEmpChallanAmt.get("EMP_EDU_CESS_AMT"));
						hmEmpOtherInner.put("EMP_EDU_CESS_TOTAL", ""+dblEmpEduCessTotal);
						
						hmEmpTotalOtherAmt.put(hmEmpInner.get("EMP_ID"), hmEmpOtherInner);
						
						cnt++;
						nDD++;
						
						sb.append(""+cnt); //M -Line Number [Running sequence number for each line in the file]
						sb.append(TILDA);
						sb.append("DD"); //M -Record Type [Value should be "DD"]
						sb.append(TILDA);
						sb.append("1"); //M -Batch Number [Value should be same as 'Batch Number' field in 'Batch Header' record]
						sb.append(TILDA);
						sb.append(""+nCD); //M -Challan-Detail Record Number [Running serial number for 'Challan Detail' records in a batch.]
						sb.append(TILDA);
						sb.append(""+nDD); //M -313-Deductee /  Detail Record No [Running serial no to indicate detail record no. Should start with 1.]
						sb.append(TILDA);
						sb.append("O"); //M -Mode [Allowed value is O.]
						sb.append(TILDA);
						sb.append(""); //O -314-Employee Reference No. (Provided by Employer) [Mandatory to mention employee reference number, in case of invalid PAN (filed no. 10 of deductee details) i.e. "PANAPPLIED", "PANINVALID" and "PANNOTAVBL"]
						sb.append(TILDA);
						sb.append(""); //O -Deductee /  Code  (Not applicable) [No value should be specified]
						sb.append(TILDA);
						sb.append(""); //O -Last Employee /   PAN ( Used for Verification)  (Not applicable) [No value should be specified]
						sb.append(TILDA);
						sb.append(uF.showData(hmEmpInner.get("PAN_NO"), "")); //M -315-Employee / PAN [PAN of the employee. If available should be Valid PAN Format. There may be deductees who have not been issued PAN however who have applied for a PAN and have given adequate declaration to the deductor indicating the same.  In such cases, deduction schedule in the statement will not reflect PAN and instead state PAN Ref. Number for the deductee.  The deductor will however have to mention ‘PANAPPLIED’ in place of PAN. If the PAN structure is not correct, deductee will have to mention 'PANINVALID'.  However if the deductee has not given any declaration, deductor will have to mention ‘PANNOTAVBL’ in place of PAN. In case tax is deducted at higher rate mention "PANNOTAVBL".]
						sb.append(TILDA);
						sb.append(""); //O -Last  Employee/ PAN Ref. No.(Not applicable) [No value should be specified]
						sb.append(TILDA);
						sb.append(""); //O -PAN Ref. No. [The PAN Ref No is a unique identifier to identify a deductee record/ transaction where PAN is not available. This is quoted by the deductor. (A deductee may have multiple entries in a Statement)]
						sb.append(TILDA);
						sb.append(uF.showData(hmEmpInner.get("NAME"), "")); //M -316-Name of Employee / [Mention the Name of the employee.]
						sb.append(TILDA);
						sb.append(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmEmpChallanAmt.get("EMP_CHALLAN_AMT")))); //M -321-TDS / TCS -Income Tax for the period [Decimal with precision value 2 is  allowed.]
						sb.append(TILDA);
						sb.append(uF.formatIntoTwoDecimalWithOutComma(dblSurcharge)); //M -TDS / TCS -Surcharge  for the period [Decimal with precision value 2 is  allowed.]
						sb.append(TILDA);
						sb.append(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmEmpChallanAmt.get("EMP_EDU_CESS_AMT")))); //dblEduCess M -322-TDS / TCS -Cess [Decimal with precision value 2 is  allowed.]
						sb.append(TILDA); 
						double dblISCAmt = uF.parseToDouble(hmEmpChallanAmt.get("EMP_CHALLAN_AMT")) + dblSurcharge + uF.parseToDouble(hmEmpChallanAmt.get("EMP_EDU_CESS_AMT")); //dblEduCess;  
						sb.append(uF.formatIntoTwoDecimalWithOutComma(dblISCAmt)); //M -323-Total Income Tax Deducted at Source (TDS / TCS Income Tax+ TDS / TCS Surcharge + TDS / TCS -Cess) [Total of field no. 14, 15 and 16. Value in this field should be less than or equal to "Amount paid" (field no. 22) field of respective deductee record.]
						sb.append(TILDA);
						sb.append(""); //O -Last Total Income Tax Deducted at Source (Income Tax +Surcharge+Cess)  ( Used for Verification)  (Not applicable) [No value should be specified]
						sb.append(TILDA);
						sb.append(uF.formatIntoTwoDecimalWithOutComma(dblISCAmt)); //M -324-Total Tax Deposited [Mention the Total Tax Deposited for the Deductee. Mention the Total Tax Deposited for the Deductee]
						sb.append(TILDA); 
						sb.append(""); //O -Last Total Tax Deposited  ( Used for Verification)  (Not applicable) [No value should be specified]
						sb.append(TILDA);
						sb.append(""); //O -Total Value of Purchase  (Not applicable) [No value should be specified]
						sb.append(TILDA);
						sb.append(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmEmpSalaryAmtDetails.get(strChallanNo+"_"+hmEmpInner.get("EMP_ID"))))); //M -320-Amount Paid / Credited (Rs.) [Mention the Amount paid to employee. Value should always be greater than 0.00 and less than or equal to 99 crores (i.e. 999999999.00). Further, this value should be greater than or equal to the value quoted in the field Total tax deducted (field no. 17) of respective deductee record.]
						sb.append(TILDA);
						sb.append(uF.showData(hmChallanDate.get(strChallanNo), "")); //M -318-Date of payment / credit [Date on which Amount paid / Credited to deductee. Date to be mentioned in DDMMYYYY format.]
						sb.append(TILDA);
						sb.append(""); //O -319-Date on which tax Deducted [Date of tax deduction. Mandatory  if 'Total Income Tax Deducted at Source' is greater than Zero (0.00) . No value needs to be specified if 'Total Income Tax Deducted at Source' is Zero (0.00) . Date to be mentioned in DDMMYYYY format. Also, this date should not be less than the relevant quarter. E.g. If the statement is being prepared for Q2 of FY 2013-14, then date of deduction should be greater than or equal to 01/07/2013.]
						sb.append(TILDA);
						sb.append(uF.showData(hmChallanDate.get(strChallanNo), "")); //M -325-Date of Deposit [Date of payment of tax to Govt.  Should be same as value in field 'Date of 'Bank Challan No' / 'Transfer Voucher No' in Challan Detail Record. Date to be mentioned in DDMMYYYY format.]
						sb.append(TILDA);
						sb.append(""); //O -Rate at which Tax Deducted / Collected (Not applicable) [Not applicable for form 24Q]
						sb.append(TILDA);
						sb.append(""); //O -Grossing up Indicator  (Not applicable) [No value should be specified]
						sb.append(TILDA);
						sb.append(""); //O -Book Entry / Cash Indicator  (Not applicable) [No value should be specified]
						sb.append(TILDA);
						sb.append(""); //O -Date of furnishing Tax Deduction Certificate  (Not applicable) [No value should be specified]
						sb.append(TILDA);
						sb.append(""); //O -326-Remarks 1 (Reason for non-deduction / lower deduction/ higher deduction) [If applicable quote value (code) as per Annexure 6 else no value to be provided.]
						sb.append(TILDA);
						sb.append(""); //O -Remarks 2 (For future use) [No value should be specified]
						sb.append(TILDA);
						sb.append(""); //O -Remarks 3 (For future use) [No value should be specified]
						sb.append(TILDA);
						sb.append(""); //O -317-Section Code under which payment made [Mention section code as per Annexure 2. No value to be quoted for statements pertaining prior to FY 2013-14. Mandatory to mention value for statements pertaining to FY 2013-14 onwards.]
						sb.append(TILDA);
						sb.append(""); //O -327-Certificate number issued by the Assessing Officer u/s 197 for non-deduction/lower deduction. [Mandatory to mention 10 digit value if, "A" or "B" is mentioned in field no. 30. Value to be mentioned for statements pertaining to FY 2013-14 onwards.]
						sb.append(TILDA);
						sb.append(""); //NA -Filler 1 [No value should be specified]
						sb.append(TILDA);
						sb.append(""); //NA -Filler 2 [No value should be specified]
						sb.append(TILDA);
						sb.append(""); //NA -Filler 3 [No value should be specified]
						sb.append(TILDA);
						sb.append(""); //NA -Filler 4 [No value should be specified]
						sb.append(TILDA);
						sb.append(""); //O -Record Hash  (Not applicable) [No value should be specified]
						
						sb.append(System.getProperty("line.separator"));
					}
					/**
					 * Note: A TDS Statement corresponds to a TDS Challan I.e. 1 TDS Statement will always contain 1 Challan only
					 * TDS Statement for Salary category (Deductee Detail Record) end
					 * */
					
					/**
					 * TDS Statement for Salary category (Challan / Transfer Voucher Detail Record) end 
					 * */
				
				}
				
				if (strQuarter.equals("Q4")){
					/**
					 * Note: Salary Details Record is Optional and the return may not contain any Salary Detail Record
					 * TDS Statement for Salary category (Salary  Details Record) start
					 * */
	//				System.out.println("empSetList=======>"+empSetList.toString());
					Iterator<String> it1 = empSetList.iterator();
					int nSD = 0;
					while(it1.hasNext()){
						String strEmpId = it1.next();
						Map<String, String> hmEmpInner = (Map<String, String>) hmEmpDetails.get(strEmpId); 
						if(hmEmpInner == null) hmEmpInner = new HashMap<String, String>();
						
						Map<String, String> hmEmpOtherInner = hmEmpTotalOtherAmt.get(hmEmpInner.get("EMP_ID"));
						if(hmEmpOtherInner == null) hmEmpOtherInner = new HashMap<String, String>();
						
						double dblEmpSurchargeTotal = uF.parseToDouble(hmEmpOtherInner.get("EMP_SURCHARGE_TOTAL"));
						double dblEmpEduCessTotal = uF.parseToDouble(hmEmpOtherInner.get("EMP_EDU_CESS_TOTAL"));
						
						/**
						 * Form 16 data start
						 * */
						Map hmSalaryHeadMap = (Map)request.getAttribute("hmSalaryHeadMap");
						if(hmSalaryHeadMap==null)hmSalaryHeadMap=new HashMap();

						Map hmPayrollDetails = (Map)request.getAttribute("hmPayrollDetails");
						if(hmPayrollDetails==null)hmPayrollDetails=new HashMap();

						Map hmEmpPayrollDetails = (Map)hmPayrollDetails.get(strEmpId);
						if(hmEmpPayrollDetails==null)hmEmpPayrollDetails=new HashMap();

						Map hmExemption = (Map)request.getAttribute("hmExemption");
						if(hmExemption==null)hmExemption=new HashMap();

						Map hmHRAExemption = (Map)request.getAttribute("hmHRAExemption");
						if(hmHRAExemption==null)hmHRAExemption=new HashMap();

						Map hmRentPaid = (Map)request.getAttribute("hmRentPaid");
						if(hmRentPaid==null)hmRentPaid=new HashMap();

						Map hmInvestment = (Map)request.getAttribute("hmInvestment");
						if(hmInvestment==null)hmInvestment=new HashMap();

						Map hmTaxLiability = (Map)request.getAttribute("hmTaxLiability");
						if(hmTaxLiability==null)hmTaxLiability=new HashMap();

						Map hmTaxInner = (Map)hmTaxLiability.get(strEmpId);
						if(hmTaxInner==null)hmTaxInner=new HashMap();


						Map hmPaidTdsMap = (Map)request.getAttribute("hmPaidTdsMap");
						if(hmPaidTdsMap==null)hmPaidTdsMap=new HashMap();

						Map<String, Map<String, String>> hmEmpInvestment =(Map<String, Map<String, String>>)request.getAttribute("hmEmpInvestment");
						if(hmEmpInvestment==null)hmEmpInvestment=new HashMap<String, Map<String, String>>();

						Map<String, Map<String, List<Map<String, String>>>> hmEmpSubInvestment =(Map<String, Map<String, List<Map<String, String>>>>)request.getAttribute("hmEmpSubInvestment");
						if(hmEmpSubInvestment==null)hmEmpSubInvestment=new HashMap<String, Map<String, List<Map<String, String>>>>();

						Map<String, Map<String, String>> hmEmpInvestment1 =(Map<String, Map<String, String>>)request.getAttribute("hmEmpInvestment1");
						if(hmEmpInvestment1==null)hmEmpInvestment1=new HashMap<String, Map<String, String>>();

						Map<String, String> hmSectionMap =(Map<String, String>)request.getAttribute("hmSectionMap");

						List<String> chapter1SectionList = (List<String>) request.getAttribute("chapter1SectionList");
						if(chapter1SectionList==null)chapter1SectionList = new ArrayList<String>();
						List<String> chapter2SectionList = (List<String>) request.getAttribute("chapter2SectionList");
						if(chapter2SectionList==null) chapter2SectionList = new ArrayList<String>();

						Map<String, Map<String, String>> hmEmpActualInvestment = (Map<String, Map<String, String>>) request.getAttribute("hmEmpActualInvestment");
						if(hmEmpActualInvestment==null) hmEmpActualInvestment = new HashMap<String, Map<String, String>>();

						Map<String, Map<String, String>> hmEmpActualInvestment1 = (Map<String, Map<String, String>>) request.getAttribute("hmEmpActualInvestment1");
						if(hmEmpActualInvestment1==null) hmEmpActualInvestment1 = new HashMap<String, Map<String, String>>();
						 
						double dblGross = uF.parseToDouble((String)hmEmpPayrollDetails.get("GROSS") );
						
						/**
						 * Form 16 data end
						 * */
						
						
						cnt++;
						nSD++;
						
						sb.append(""+cnt); //M -Line Number [Running sequence number for each line in the file]
						sb.append(TILDA);
						sb.append("SD"); //M -Record Type [Value should be "SD"]
						sb.append(TILDA);
						sb.append("1"); //M -Batch Number [Value should be same as 'Batch Number' field in 'Batch Header' record]
						sb.append(TILDA);
						sb.append(""+nSD); //M -327,354- Salary Details  Record No (Serial Number of Employee) [Running serial no to indicate detail record no. Should start with 1]
						sb.append(TILDA);
						sb.append("A"); //M -Mode [only allowed value is "A".]
						sb.append(TILDA);
						sb.append(uF.showData(hmEmpInner.get("PAN_NO"), "")); //M -328-Employee PAN [PAN of the employee. ( The deductor will however have to mention ‘PANAPPLIED’ in place Deductee has applied for PANf . If the deductee is not sure of the PAN Format he will  mention 'PANINVALID'. if the deductee has not given any declaration, deductor will have to mention ‘PANNOTAVBL’ in place of PAN.)]
						sb.append(TILDA);
						sb.append(""); //O -PAN Ref. No. [If Valid PAN is not available with the deductor, then it may (optionally) assign a unique reference number for each of such deductees. This reference number will have to be unique across all types of returns across all quarters for a given deductor.]
						sb.append(TILDA);
						sb.append(uF.showData(hmEmpInner.get("NAME"), "")); //M -329,353-Name of Employee [Mention the Name of the employee]
						sb.append(TILDA);
						
						double dblYears =uF.parseToDouble(hmEmpAgeMap.get(strEmpId));
						String residentAge="G";
						if(hmEmpGenderMap.get(strEmpId)!=null && hmEmpGenderMap.get(strEmpId).trim().equals("F")){
							residentAge = "W";
							if(dblYears>=80){
								residentAge="O";
							}else if(dblYears>80 && dblYears<=60){
								residentAge="S";
							}
						} else if(dblYears>=80){
							residentAge="O";
						}else if(dblYears>80 && dblYears<=60){
							residentAge="S";
						}
						sb.append(residentAge); //M -Category of Employee [W' for woman, 'S' for senior citizen, 'O' for super senior citizen (applicable from FY 201112 onwards) and 'G' for others] 
						sb.append(TILDA);
						sb.append(uF.showData(hmEmpInner.get("EMP_DATE_FROM"), "")); //M -330- Period of Employment From - Date [ddmmyyyy ( Date from which employed with the current Employer).]
						sb.append(TILDA);
						sb.append(uF.showData(hmEmpInner.get("EMP_DATE_UP"), "")); //M -330- Period of Employment To - Date [ddmmyyyy ( Date to which employed with the current Employer).]
						sb.append(TILDA);  
						
//						if(strEmpId.equals("654")){
//							System.out.println(strEmpId+"=========>"+uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmEmpTotalSalaryAmt.get(strEmpId))));
//						}
						
						sb.append(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmEmpTotalSalaryAmt.get(strEmpId)))); //M -(333+334)- Total amount of salary[Greater than or equal to Zero (Total of field no 34 & 35)]
						sb.append(TILDA);
						sb.append(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmEmpTotalSalaryAmt.get(strEmpId)))); //M -Count of ' Salary Details  - Section 16 Detail ' Records  associated with this Deductee [This number must be equal to the total number of  'Salary Detail - Section 16 Detail ' records]
						sb.append(TILDA);
						sb.append(uF.formatIntoTwoDecimalWithOutComma(Math.round(dblGross))); //M -Gross Total of 'Total Deduction under section 16' under associated 'Salary Details  - Section 16 Detail'[Must be equal to the total of all Deductions under Salary Detail - section 16 details]
						sb.append(TILDA);
						
						double dblConveyanceAllowanceExempt = uF.parseToDouble((String)hmTaxInner.get("dblConveyanceAllowanceExempt"));
						double dblMedicalAllowanceExempt =uF.parseToDouble((String)hmTaxInner.get("dblMedicalAllowanceExempt"));
						double dblHRAExemption = uF.parseToDouble((String)hmTaxInner.get("dblHRAExemption"));
						double dblEducationAllowanceExempt = uF.parseToDouble((String)hmTaxInner.get("dblEducationAllowanceExempt"));
						double dblLTAExempt = uF.parseToDouble((String)hmTaxInner.get("dblLTAExempt"));
						double dblOtherExemption = uF.parseToDouble((String)hmTaxInner.get("dblOtherExemption"));
						
						double dblGrossNet = Math.round(dblGross) - Math.round(dblHRAExemption) - Math.round(dblConveyanceAllowanceExempt) - Math.round(dblOtherExemption) - Math.round(dblMedicalAllowanceExempt) - Math.round(dblEducationAllowanceExempt) - Math.round(dblLTAExempt);
						
						double dblProfessionalTaxExempt = uF.parseToDouble((String)hmTaxInner.get("dblProfessionalTaxExempt"));
						double dblHomeLoanTaxExempt = uF.parseToDouble((String)hmTaxInner.get("dblHomeLoanTaxExempt"));
						
						dblGrossNet -=  (Math.round(dblProfessionalTaxExempt));
						dblGrossNet -=  Math.round(dblHomeLoanTaxExempt);
						sb.append(uF.formatIntoTwoDecimalWithOutComma(Math.round(dblGrossNet))); //M -Income chargeable under the head Salaries (335 - (336 + 337)) [Greater than or equal to Zero (Field No 13 - Field no 16)]
						sb.append(TILDA);
						sb.append("0.00"); //M -Income (including admissible loss from house property) under any head other than income under the head "salaries" offered for TDS [section 192 (2B)] [May have negative Value]
						sb.append(TILDA);
						
						double dblIncomeFromOther=uF.parseToDouble((String)hmTaxInner.get("dblIncomeFromOther"));
						dblGrossNet += Math.round(dblIncomeFromOther);
						
						sb.append(uF.formatIntoTwoDecimalWithOutComma(Math.round(dblGrossNet))); //M -Gross Total Income (338 + 339) [Greater than or equal to Zero. (Total of field no 17 & 18)]
						sb.append(TILDA);
						
						double dblInvestment = uF.parseToDouble((String)hmTaxInner.get("dblInvestment"));

						double dblChapterVIA=0.0d;
						List<String> alUnderSection=new ArrayList<String>();
						Map<String,String> hmInvest=hmEmpInvestment.get(strEmpId);
						if(hmInvest==null) hmInvest = new HashMap<String, String>();
						int ii=0;
						for(int a=0;chapter1SectionList!=null && a<chapter1SectionList.size();a++){
							String strSectionId=chapter1SectionList.get(a);
							String strAmt = uF.showData(hmInvest.get(strSectionId), "");
					  		Map<String, List<Map<String, String>>> hmSubInvestment = (Map<String, List<Map<String, String>>>) hmEmpSubInvestment.get("" + strEmpId);
					  		if(hmSubInvestment==null) hmSubInvestment= new HashMap<String, List<Map<String, String>>>();
					  		List<Map<String, String>> subInvestList = (List<Map<String, String>>) hmSubInvestment.get("" +strSectionId);
					  		String strUnderSection="";
					  		
					  		if(hmSectionMap.containsKey(strSectionId)){
					  			strAmt=""+Math.round(dblInvestment);
					  		}
					  		dblChapterVIA +=uF.parseToDouble(strAmt);
						}
						
						Map<String,String> hmInvest1=hmEmpInvestment1.get(strEmpId);
						if(hmInvest1==null) hmInvest1 = new HashMap<String, String>();
						ii=0;
						for(int a=0;chapter2SectionList!=null && a<chapter2SectionList.size();a++){
							String strSectionId=chapter2SectionList.get(a);
							String strAmt = uF.showData(hmInvest1.get(strSectionId), "");
					  		dblChapterVIA +=uF.parseToDouble(strAmt);
						}
						
						sb.append(uF.formatIntoTwoDecimalWithOutComma(Math.round(dblChapterVIA))); //M -Count of ' Salary Details  - Chapter VI-A Detail ' Records  associated with Deductee - Chapter VIA Detail [>=0, This number must be equal to the total number of   ' Salary Detail - Chapter VI-A Detail ' Records associated with this Salary Detail]
						sb.append(TILDA);
						sb.append(uF.formatIntoTwoDecimalWithOutComma(Math.round(dblChapterVIA))); //M -Gross Total of 'Amount deductible under provisions of chapter VI-A' under  associated ' Salary Details  - Chapter VIA Detail ' [Must be equal to the total of all  'Total Amount deductible under chapter VI-A' under  associated ' Salary Detail - Chapter VIA Detail ']
						sb.append(TILDA);
						
						dblGrossNet -= Math.round(dblChapterVIA);
						
						sb.append(uF.formatIntoTwoDecimalWithOutComma(Math.round(dblGrossNet))); //M- Total Taxable Income (340-343) [ This should be equal to the SUM of 'Total Taxable Income' (field 19 - field 22). Value should always be less than or equal to 99 crores (i.e. 999999999.00).]
						sb.append(TILDA);
						
						double dblTaxLiability = uF.parseToDouble((String)hmTaxInner.get("TAX_LIABILITY"));
						
						sb.append(uF.formatIntoTwoDecimalWithOutComma(Math.round(dblTaxLiability))); //M- Income Tax on Total Income [Greater than or equal to Zero.  Value must be less than or equal to 'Total Taxable Income ( 336-339)'.]
						sb.append(TILDA);
						sb.append(uF.formatIntoTwoDecimalWithOutComma(dblEmpSurchargeTotal)); //M- Surcharge
						sb.append(TILDA);
						sb.append(uF.formatIntoTwoDecimalWithOutComma(dblEmpEduCessTotal)); //M- Education Cess
						sb.append(TILDA);
						sb.append("0.00"); //M- Income Tax Relief u/s 89 when salary etc is paid in arrear or advance [Greater than or equal to Zero.  Value must be less than or equal to 'Total Taxable Income ( 336-339)'.]
						sb.append(TILDA);
						
						double dblTotalTaxLiability = uF.parseToDouble((String)hmTaxInner.get("TOTAL_TAX_LIABILITY"));
						sb.append(uF.formatIntoTwoDecimalWithOutComma(Math.round(dblTotalTaxLiability))); //M- Net Income Tax payable ((345+346) - 347) [Greater than or equal to Zero. Amount should quoted should be less than or equal to 'Total Taxable Income'.(Field No 24 + (Field 26 - Field 27)]
						sb.append(TILDA);
						
						double dblTotalTaxAmt = uF.parseToDouble((String)hmPaidTdsMap.get(strEmpId));
						sb.append(uF.formatIntoTwoDecimalWithOutComma(Math.round(dblTotalTaxAmt))); //M- Total amount of tax deducted at source for the whole year (349+350) [Aggregate of the amount in column 323 of TDS statement details for all four quarters in respect of each employee ( Total of field 36 & 37)]
						sb.append(TILDA);
						double dblShortFall = (Math.round(dblTotalTaxLiability)) + (Math.round(dblTotalTaxAmt));
						sb.append(uF.formatIntoTwoDecimalWithOutComma(Math.round(dblShortFall))); //O- Shortfall in tax deduction (+)/Excess tax deduction(-) [351 - 348] [Value may be negative (field 28 - field 29)]
						sb.append(TILDA);
						sb.append(""); //O- Aggregate amount of deductions admissible under section 80C, 80CCC and 80CCD [No value should be specified]
						sb.append(TILDA);
						sb.append(""); //O- Remarks  (For future use) [No value should be specified]
						sb.append(TILDA);
						sb.append(""); //O- Remarks  (For future use) [No value should be specified]
						sb.append(TILDA);
						sb.append(""); //O- Taxable Amount on which tax is deducted by the current employer [Greater than or equal to Zero. No value to be quoted for statements pertaining prior to FY 2013-14. Mandatory to mention value for statements pertaining to FY 2013-14 onwards.]
						sb.append(TILDA);
						sb.append(""); //O- Reported Taxable Amount on which tax is deducted by previous employer(S) [Greater than or equal to Zero. No value to be quoted for statements pertaining prior to FY 2013-14. Mandatory to mention value for statements pertaining to FY 2013-14 onwards.]
						sb.append(TILDA);
						sb.append(""); //O- Total Amount of tax deducted at source by the current employer for the whole year [aggregate of the amount in column 323 of Annexure I for all the four quarters in respect of each employee] [Greater than or equal to Zero. No value to be quoted for statements pertaining prior to FY 2013-14. Mandatory to mention value for statements pertaining to FY 2013-14 onwards.]
						sb.append(TILDA);
						sb.append(""); //O- Reported amount of Tax deducted at source by previous employer(s)/deductor(s) (income in respect of which included in computing total taxable income in column 344) [Greater than or equal to Zero. No value to be quoted for statements pertaining prior to FY 2013-14. Mandatory to mention value for statements pertaining to FY 2013-14 onwards.]
						sb.append(TILDA);
						sb.append(""); //O- Whether tax deducted at Higher rate due to non furnishing of PAN by deductee [Value "Y"(Yes) or value "N" (No). No value to be quoted for statements pertaining prior to FY 2013-14. Mandatory to mention value for statements pertaining to FY 2013-14 onwards. If Invalid PAN is mentioned in field no. 7 above, then value "Y" to be mandatorily quoted.]
						
						sb.append(System.getProperty("line.separator"));
					}
					/**
					 * Note: Salary Details Record is Optional and the return may not contain any Salary Detail Record
					 * TDS Statement for Salary category (Salary Details Record) end
					 * */
				
				}
				
			}
			
			String strData = sb.toString();
			ServletOutputStream op = response.getOutputStream();
			response.setContentType("application/octet-stream");
			response.setContentLength((int) strData.length());
			response.setHeader("Content-Disposition", "attachment; filename=\"" + "Form24Q_"+strQuarter+"_"+ strFinancialYr+".txt");
			op.write(strData.getBytes());
			op.flush();
			op.close();
			
			
			
		}catch (Exception e) {
			e.printStackTrace();
		}
}

//	sb.append(""+cnt); //M -Line Number [Running sequence number for each line in the file]
//	sb.append(TILDA);
//	sb.append("SD"); //M -Record Type [Value should be "SD"]
//	sb.append(TILDA);
//	sb.append("1"); //M -Batch Number [Value should be same as 'Batch Number' field in 'Batch Header' record]
//	sb.append(TILDA);
//	sb.append(""+nSD); //M -327,354- Salary Details  Record No (Serial Number of Employee) [Running serial no to indicate detail record no. Should start with 1]
//	sb.append(TILDA);
//	sb.append("A"); //M -Mode [only allowed value is "A".]
//	sb.append(TILDA);
//	sb.append(""); //O -Filler7 [No value should be specified]
//	sb.append(TILDA);
//	sb.append(uF.showData(hmEmpInner.get("PAN_NO"), "")); //M -328-Employee PAN [PAN of the employee. ( The deductor will however have to mention ‘PANAPPLIED’ in place Deductee has applied for PANf . If the deductee is not sure of the PAN Format he will  mention 'PANINVALID'. if the deductee has not given any declaration, deductor will have to mention ‘PANNOTAVBL’ in place of PAN.)]
//	sb.append(TILDA);
//	sb.append(""); //O -PAN Ref. No. [If Valid PAN is not available with the deductor, then it may (optionally) assign a unique reference number for each of such deductees. This reference number will have to be unique across all types of returns across all quarters for a given deductor.]
//	sb.append(TILDA);
//	sb.append(uF.showData(hmEmpInner.get("NAME"), "")); //M -329,353-Name of Employee [Mention the Name of the employee]
//	sb.append(TILDA);
//	sb.append(uF.showData(hmEmpInner.get("DESIGNATION_NAME"), "")); //M -Designation of Employee [Mention the Designation of Employee ] 
//	sb.append(TILDA);
//	sb.append(uF.showData(hmEmpInner.get("EMP_DATE_FROM"), "")); //M -330- Period of Employment From - Date [ddmmyyyy ( Date from which employed with the current Employer).]
//	sb.append(TILDA);
//	sb.append(uF.showData(hmEmpInner.get("EMP_DATE_UP"), "")); //M -330- Period of Employment To - Date [ddmmyyyy ( Date to which employed with the current Employer).]
//	sb.append(TILDA);  
//	sb.append(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmEmpTotalSalaryAmt.get(strEmpId)))); //M -331- Total amount of salary , excluding amount required  to shown in columns 332 and 333. [Greater than or equal to Zero]
//	sb.append(TILDA);
//	sb.append("0.00"); //M -332- House Rent Allowance and Other Allowances to the extent Chargeable to Tax (see sec 10(13A) read with rule 2A) [Greater than or equal to Zero]
//	sb.append(TILDA);
//	sb.append("0.00"); //M -333,368- Value of pequisites and amount accretion to Employees PF Account(Total 333=368=361+362+363+364+365+366+367) [Greater than or equal to Zero]
//	sb.append(TILDA);
//	sb.append(""); //O - Filler 8 [For future use]
//	sb.append(TILDA);
//	sb.append(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmEmpTotalSalaryAmt.get(strEmpId)))); //M -(334)- Count of ' Salary Details - Section 10 Detail ' Records  associated with this Salary Detail [>=0, This number must be equal to the total number of  ' Salary Detail - Section 10 Detail '  records associated with this Salary Detail]
//	sb.append(TILDA);
//	sb.append("0.00"); //M -334- Gross Total of 'Total Exemption  under section 10' under  associated ' Salary Details - Section 10 Detail '  [>=0 Must be equal to the total of all Deductions under Salary Detail - section 10 Details ]
//	sb.append(TILDA);
//	sb.append(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmEmpTotalSalaryAmt.get(strEmpId)))); //M -335- Total Salary (335=331+332+333) [Greater than or equal to Zero]
//	sb.append(TILDA);
//	sb.append("0.00"); //M -(336)- Count of ' Salary Details  - Section 16 Detail ' Records  associated with this Deductee [ This number must be equal to the total number of  ' Salary Detail - Section 16 Detail ']
//	sb.append(TILDA);
//	sb.append("0.00"); //M -336- Gross Total of 'Total Deduction under section 16' under  associated ' Salary Details  - Section 16 Detail ' [Must be equal to the total of all Deductions under Salary Detail  ]
//	sb.append(TILDA);
//	sb.append("0.00"); //M -337- Income chargeable under the head Salaries (335-336) [Greater than or equal to Zero]
//	sb.append(TILDA);
//	sb.append("0.00"); //M -338- Income ( including loss from house property) under any head other than income under the head "salaries" offered for TDS [May have negative Value]
//	sb.append(TILDA);
//	sb.append(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmEmpTotalSalaryAmt.get(strEmpId)))); //M -339- Gross Total Income (337+338) [Greater than or equal to Zero.]
//	sb.append(TILDA);
//	sb.append("0.00"); //O- Last Gross Total Income  ( Used for Verification)  (Not applicable) [No value should be specified]
//	sb.append(TILDA);
//	sb.append("0.00"); //M -Count of ' Salary Details  - Chapter VI-A Detail ' Records  associated with Deductee - Chapter VIA Detail [>=0, This number must be equal to the total number of   ' Salary Detail - Chapter VI-A Detail ' Records associated with this Salary Detail]
//	sb.append(TILDA);
//	sb.append("0.00"); //M -(343 = 340 + 341 + 342)- Gross Total of 'Amount deductible under provisions of chapter VI-A' under  associated ' Salary Details  - Chapter VIA Detail ' [Must be equal to the total of all  'Total Amount deductible under chapter VI-A' under  associated ' Salary Detail - Chapter VIA Detail ']
//	sb.append(TILDA);
//	sb.append(""); //M -344- Total Taxable Income ( 339-343) [This should be equal to the SUM of 'Total Taxable Income'.]
//	sb.append(TILDA);
//	sb.append(""); //M -345- Income Tax on Total Income [Greater than or equal to Zero.  Value must be less than or equal to 'Total Taxable Income ( 339-343)' .]
//	sb.append(TILDA);
//	sb.append(""); //M - Count of ' Salary Details  -Under Section 88 Records  associated with this 'Deductee - Section 88 detail' [This number must be equal to the total number of  ' Salary Detail - Detail Section 88 '  records associated with this Deductee ]
//	sb.append(TILDA);
//	sb.append(""); //M - (346 + 347 + 348 + 349)- Gross Total of - 'TOTAL Income Tax Rebate  Under Section 88 field for all sections mentioned under 'Salary Details  - Section 88 detail' [ Must be equal to the total of all  'Total Amount deductible under Section 88 ' under  associated ' Salary Detail - Section 88 detail'.]
//	sb.append(TILDA);
//	sb.append(""); //M - 350- Total Income Tax Payable [Greater than or equal to Zero.] 
//	sb.append(TILDA);
//	sb.append(""); //M - 351- Income Tax Relief u/s 89 when salary etc is paid in arrear or advance [Greater than or equal to Zero] 
//	sb.append(TILDA);
//	sb.append(""); //M - 352- Net Income Tax payable (350-351) [Greater than or equal to Zero. ] 
//	sb.append(TILDA);
//	sb.append(""); //M - 355- Perk- Where accomodation is un furnished [Greater than or equal to Zero] 
//	sb.append(TILDA);
//	sb.append(""); //M - 356- Perk-Furnished-Value as if accomodation is unfurnished [Greater than or equal to Zero] 
//	sb.append(TILDA);
//	sb.append(""); //M - 357- Perk-Furnished-Cost of furniture [Greater than or equal to Zero] 
//	sb.append(TILDA);
//	sb.append(""); //M - 358- Perk-Furnished-Perqusite value of furniture (10% of 357) [Greater than or equal to Zero.] 
//	sb.append(TILDA);
//	sb.append(""); //M - 359- Perk-Furnished-Total(356+358) [Greater than or equal to Zero.] 
//	sb.append(TILDA);
//	sb.append(""); //M - 360- Rent, if any, paid by employee [Greater than or equal to Zero] 
//	sb.append(TILDA);
//	sb.append(""); //M - 361- Value of perquisite column(355-360) or column(359-360) [Greater than or equal to Zero.] 
//	sb.append(TILDA);
//	sb.append(""); //M - 362- Perqusite value of conveyance/car [Greater than or equal to Zero] 
//	sb.append(TILDA);
//	sb.append(""); //M - 363- Remuneration paid by employer for domestic and personal services provided to the employee [Greater than or equal to Zero] 
//	sb.append(TILDA);
//	sb.append(""); //M - 364- Value of free or concessional passages on home leave and other travelling to the extent chargeable to tax [Greater than or equal to Zero] 
//	sb.append(TILDA);
//	sb.append(""); //M - 365- Estimated value of any other benefit or amenity provided by the employer free of cost or at concessional rate not included in the preceeding columns [Greater than or equal to Zero] 
//	sb.append(TILDA);
//	sb.append(""); //M - 366- Employer's contribution to recognised provident fund in excess of 12% of employee's salary [Greater than or equal to Zero] 
//	sb.append(TILDA);
//	sb.append(""); //M - 367- Interest credited to the assessee's account in recognised PF Fund in excess of the rate fixed by Central Govt. [Greater than or equal to Zero] 
//	sb.append(TILDA);
//	sb.append(""); //O - Sum Total of Other Recoveries from Employee (For future use) [No value should be specified] 
//	sb.append(TILDA);
//	sb.append(""); //O - Remarks1 (For future use) [No value should be specified] 
//	sb.append(TILDA);
//	sb.append(""); //O - Remarks2 (For future use) [No value should be specified] 
//	sb.append(TILDA);
//	sb.append(""); //O - Remarks3 (For future use) [No value should be specified] 
//	sb.append(TILDA);
//	sb.append(""); //O - Record Hash  (Not applicable) [No value should be specified] 
//	
//	sb.append(System.getProperty("line.separator"));


	public Map<String, String> getOtherDetailsMap(Connection con,int empid) {
		Map<String, String> hmOtherDetailsMap = new HashMap<String, String>();

		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
			pst = con.prepareStatement("select org_tan_no,org_pan_no from org_details where org_id in (select org_id from employee_official_details where emp_id=?)");
			pst.setInt(1,empid);
//			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				hmOtherDetailsMap.put("DEDUCTOR_TAN", rs.getString("org_tan_no"));
				hmOtherDetailsMap.put("DEDUCTOR_PAN", rs.getString("org_pan_no"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select emp_pan_no from employee_personal_details where emp_per_id=?");
			pst.setInt(1, empid);
			rs = pst.executeQuery();
			while (rs.next()) {
				hmOtherDetailsMap.put("EMPLOYEE_PAN",rs.getString("emp_pan_no"));

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
		return hmOtherDetailsMap;
	} 

	public String loadForm24Q(UtilityFunctions uF){
		
		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		monthList = new FillMonth().fillQuarterlyMonth();
		
		orgList = new FillOrganisation(request).fillOrganisation();
		wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		
		empNamesList=getEmployeeList(uF);
		
		getSelectedFilter(uF);
		
		
		return LOAD;
	}
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();

		alFilter.add("FINANCIALYEAR");
		String[] strFinancialYears = null;
		if (getFinancialYear() != null) {
			strFinancialYears = getFinancialYear().split("-");
			setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
		} else {
			strFinancialYears = CF.getFinancialYear(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), CF, uF);
			setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
		}
		hmFilter.put("FINANCIALYEAR", uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(strFinancialYears[1], DATE_FORMAT, CF.getStrReportDateFormat()));
		
		alFilter.add("MONTH");
		if(getStrMonth()!=null) {
			String strMonth="";
			for(int i=0;monthList!=null && i<monthList.size();i++) {
				if(getStrMonth().equals(monthList.get(i).getMonthId())) {
					strMonth=monthList.get(i).getMonthName();
				}
			}
			if(strMonth!=null && !strMonth.equals("")) {
				hmFilter.put("MONTH", strMonth);
			} else {
				hmFilter.put("MONTH", "Select Month");
			}
		} else {
			hmFilter.put("MONTH", "Select Month");
		}
		
		alFilter.add("ORGANISATION");
		if(getF_org()!=null)  {
			String strOrg="";
			for(int i=0;orgList!=null && i<orgList.size();i++){
				if(getF_org().equals(orgList.get(i).getOrgId())) {
					strOrg=orgList.get(i).getOrgName();
				}
			}
			if(strOrg!=null && !strOrg.equals("")) {
				hmFilter.put("ORGANISATION", strOrg);
			} else {
				hmFilter.put("ORGANISATION", "All Organisation");
			}
			
		} else {
			hmFilter.put("ORGANISATION", "All Organisation");
		}
		
		alFilter.add("EMP");
		if(getStrSelectedEmpId()!=null) {
			String strEmpName="";
			for(int i=0;empNamesList!=null && i<empNamesList.size();i++) {
				if(getStrSelectedEmpId().equals(empNamesList.get(i).getEmployeeId())) {
					strEmpName=empNamesList.get(i).getEmployeeCode();
				}
			}
			if(strEmpName!=null && !strEmpName.equals("")) {
				hmFilter.put("EMP", strEmpName);
			} else {
				hmFilter.put("EMP", "Select Employee");
			}
		} else {
			hmFilter.put("EMP", "Select Employee");
		}
		
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	
	private List<FillEmployee> getEmployeeList(UtilityFunctions uF) {
		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpCode = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);

			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id ");
			
			if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and eod.org_id="+uF.parseToInt(getF_org()));
			}
			if(uF.parseToInt(getF_strWLocation())>0){
				sbQuery.append(" and eod.wlocation_id = "+uF.parseToInt(getF_strWLocation()));
			}
			
			if(uF.parseToInt(getF_level())>0){
				sbQuery.append(" and eod.grade_id in (select grade_id from designation_details dd, level_details ld, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id = "+uF.parseToInt(getF_level())+")");
			}			
			sbQuery.append(" order by epd.emp_fname");
			pst = con.prepareStatement(sbQuery.toString());
			rsEmpCode = pst.executeQuery();
			while (rsEmpCode.next()) {
				
				String strEmpMName = "";
				
				if(flagMiddleName) {
					if(rsEmpCode.getString("emp_mname") != null && rsEmpCode.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rsEmpCode.getString("emp_mname");
					}
				}
				
				
				al.add(new FillEmployee(rsEmpCode.getString("emp_per_id"), rsEmpCode.getString("emp_fname") +strEmpMName+ " " + rsEmpCode.getString("emp_lname") + " ["
						+ rsEmpCode.getString("empcode") + "]"));
			}
			rsEmpCode.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rsEmpCode);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}
	
	
	private void viewForm24Q(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
		
			String[] strPayCycleDates = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;

			if (getFinancialYear() != null) {
				
				strPayCycleDates = getFinancialYear().split("-");
				strFinancialYearStart = strPayCycleDates[0];
				strFinancialYearEnd = strPayCycleDates[1];
			
			} else {
				
				strPayCycleDates = new FillFinancialYears(request).fillLatestFinancialYears();
				setFinancialYear(strPayCycleDates[0] + "-" + strPayCycleDates[1]);
				
				strFinancialYearStart = strPayCycleDates[0];
				strFinancialYearEnd = strPayCycleDates[1];
				 
			}
			String months = null;
			String stQr = "";
			if(getStrMonth()!=null){
				if(getStrMonth().equals("1,2,3")){
					months = "1,2,3,4,5,6,7,8,9,10,11,12";
					setStrQuarter("01/01/"+uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "yyyy"))+" to 31/03/"+uF.parseToInt(uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT, "yyyy"))+"");
					stQr = "Q4";
				} else if(getStrMonth().equals("4,5,6")){
					months = "4,5,6";
					setStrQuarter("01/04/"+uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "yyyy"))+" to 30/06/"+uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "yyyy"))+"");
					stQr = "Q1";
				} else if(getStrMonth().equals("7,8,9")){
					months = "7,8,9";
					setStrQuarter("01/07/"+uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "yyyy"))+" to 31/09/"+uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "yyyy"))+"");
					stQr = "Q2";
				} else {
					months = "10,11,12";
					setStrQuarter("01/10/"+uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "yyyy"))+" to 31/12/"+uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "yyyy"))+"");
					stQr = "Q3";
				}
			} else {
				months = "1,2,3,4,5,6,7,8,9,10,11,12";
				setStrQuarter("01/01/"+uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "yyyy"))+" to 31/03/"+uF.parseToInt(uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT, "yyyy"))+"");
				stQr = "Q4";
			}			
			request.setAttribute("QUARTER", stQr);
			
			
			con = db.makeConnection(con);  
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			Map<String, String> hmEmpProfile = CF.getEmpProfileDetail(con, request, session, CF, uF, null, getStrSelectedEmpId());
			request.setAttribute("hmEmpProfile", hmEmpProfile);
			
			Map<String, String> hmStates = CF.getStateMap(con);
			Map<String, String> hmCountry = CF.getCountryMap(con);
			if(hmCountry == null) hmCountry = new HashMap<String, String>();CF.getCountryMap(con);
			Map<String, String> hmEmpCodeDesig =CF.getEmpDesigMap(con);			
			Map<String, String> hmOtherDetailsMap = getOtherDetailsMap(con,uF.parseToInt(getStrSelectedEmpId()));
			String orgId = CF.getEmpOrgId(con, uF, getStrSelectedEmpId());
			
			Map<String, String> hmEmpDesig = CF.getEmpDesigMap(con);
			if(hmEmpDesig == null) hmEmpDesig = new HashMap<String, String>();
			Map<String, String> hmEmpAgeMap =CF.getEmpAgeMap(con, CF);
			Map<String, String> hmEmpGenderMap = CF.getEmpGenderMap(con);
			
			pst = con.prepareStatement("select * from org_details where org_id = ? ");
			pst.setInt(1, uF.parseToInt(orgId));
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmOrg=new HashMap<String, String>();
			while (rs.next()) {
				hmOrg.put("ORG_ID", rs.getString("org_id"));
				hmOrg.put("ORG_NAME", rs.getString("org_name"));
				hmOrg.put("ORG_LOGO", rs.getString("org_logo"));
				hmOrg.put("ORG_ADDRESS", rs.getString("org_address"));
				hmOrg.put("ORG_PINCODE", rs.getString("org_pincode"));
				hmOrg.put("ORG_CONTACT", rs.getString("org_contact1"));
				hmOrg.put("ORG_EMAIL", rs.getString("org_email"));
				hmOrg.put("ORG_STATE_ID", rs.getString("org_state_id"));
				hmOrg.put("ORG_COUNTRY_ID", rs.getString("org_country_id"));
				hmOrg.put("ORG_CITY", rs.getString("org_city"));
				hmOrg.put("ORG_CODE", rs.getString("org_code"));
				hmOrg.put("ORG_DISPLAY_PAYCYCLE", rs.getString("display_paycycle"));
				hmOrg.put("ORG_DURATION_PAYCYCLE", rs.getString("duration_paycycle"));
				hmOrg.put("ORG_SALARY_CAL_BASIS", rs.getString("salary_cal_basis"));
				hmOrg.put("ORG_START_PAYCYCLE",uF.getDateFormat(rs.getString("start_paycycle"), DBDATE, DATE_FORMAT) );
				hmOrg.put("ORG_TAN", rs.getString("org_tan_no"));
				hmOrg.put("ORG_PAN", rs.getString("org_pan_no"));
			}
			rs.close();
			pst.close();  
			
			pst = con.prepareStatement("select * from employee_personal_details where emp_per_id=?");
			pst.setInt(1, uF.parseToInt(getStrSelectedEmpId()));
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmEmp = new HashMap<String, String>();
			while(rs.next()){
				hmEmp.put("EMP_ID", rs.getString("emp_per_id"));
				
				String strMiddleName = "";
				
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strMiddleName = " "+rs.getString("emp_mname");
					}
				}
				
				hmEmp.put("EMP_NAME", rs.getString("emp_fname")+strMiddleName+" "+rs.getString("emp_lname"));
				hmEmp.put("EMP_ADDRESS", uF.showData(rs.getString("emp_address1"), ""));
				hmEmp.put("EMP_CITY_ID", rs.getString("emp_city_id")); 
				hmEmp.put("EMP_PIN_CODE", rs.getString("emp_pincode")); 
				if(rs.getString("emp_email_sec")!=null && rs.getString("emp_email_sec").indexOf("@")>0){
					hmEmp.put("EMP_EMAIL", rs.getString("emp_email_sec"));
				}else{
					hmEmp.put("EMP_EMAIL", rs.getString("emp_email"));
				}
				hmEmp.put("EMP_STATE_ID", rs.getString("emp_state_id_tmp")); 
				hmEmp.put("EMP_CONTACT_NO", rs.getString("emp_contactno")); 
			}
			rs.close();
			pst.close();
			
			String[] tmpMonths1 = months.split(",");
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select month from challan_details where financial_year_from_date = ? and financial_year_to_date = ? and challan_type=? ");
			sbQuery.append(" and (");
            for(int i=0; i<tmpMonths1.length; i++){
                sbQuery.append(" month like '%,"+tmpMonths1[i]+",%'");
                
                if(i<tmpMonths1.length-1){
                    sbQuery.append(" OR "); 
                }
            }
            sbQuery.append(" ) ");
			sbQuery.append(" and is_paid=true and emp_id in (select eod.emp_id from employee_personal_details epd,employee_official_details eod " +
					"where eod.emp_id = epd.emp_per_id and org_id=?)");
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, TDS);
			pst.setInt(4, uF.parseToInt(orgId));
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			Set<String> monthSet = new HashSet<String>();
			while(rs.next()){
				String[] tempMth = rs.getString("month").split(",");
				for(int i=0; i<tempMth.length; i++){
					if(uF.parseToInt(tempMth[i].trim()) > 0){
						monthSet.add(tempMth[i].trim());
					}
				}
			}
			rs.close();
			pst.close(); 
			
			String strTempMonth = null;
			int x = 0;
			Iterator<String> it = monthSet.iterator();
			while(it.hasNext()){
				String stMth = it.next();
				if(x == 0){
					strTempMonth = stMth;
				} else {
					strTempMonth +=","+ stMth;
				}
				x++;
			}
			
			String[] tmpMonths = strTempMonth!=null ? strTempMonth.split(",") : null;
			
			int empCount = 0;
			String challanAmt = "0";
			
			String paidAmt = "0";
			String paidTDSAmt = "0";
			double paidOtherCharges = 0.0d;
			
			String cntChallan = "";
			
			Set<String> challanSetList = new HashSet<String>();
			Map<String, String> hmChallanEmpCnt = new HashMap<String, String>();
			Map<String, String> hmChallanDate = new HashMap<String, String>();
			Map<String, Map<String, String>> hmChallanAmtDetails = new HashMap<String, Map<String, String>>();
			Map<String, List<Map<String, String>>> hmEmpChallanAmtDetails = new HashMap<String, List<Map<String, String>>>();
			Map<String, Map<String, String>> hmEmpDetails = new HashMap<String, Map<String, String>>();
			Map<String, String> hmEmpSalaryAmtDetails = new HashMap<String, String>();
			Map<String, String> hmEmpTotalSalaryAmt =  new HashMap<String, String>();
			Map<String, String> hmEmpChallanTotalAmt =  new HashMap<String, String>();
			Map<String, String> hmBRCCode =  new HashMap<String, String>();
			
			if(tmpMonths !=null && tmpMonths.length>0){
			
				sbQuery = new StringBuilder();
				sbQuery.append("select emp_id from challan_details where financial_year_from_date = ? and financial_year_to_date = ? and challan_type=? ");
				sbQuery.append(" and (");
	            for(int i=0; i<tmpMonths.length; i++){
	                sbQuery.append(" month like '%,"+tmpMonths[i]+",%'");
	                
	                if(i<tmpMonths.length-1){
	                    sbQuery.append(" OR "); 
	                }
	            }
	            sbQuery.append(" ) ");
				sbQuery.append(" and is_paid=true and emp_id in (select eod.emp_id from employee_personal_details epd,employee_official_details eod " +
						"where eod.emp_id = epd.emp_per_id and org_id=?)");
				
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(3, TDS);
				pst.setInt(4, uF.parseToInt(orgId));
	//			System.out.println("pst====>"+pst);
				rs = pst.executeQuery();
				String empIds = null;
				while(rs.next()){
					if(empIds == null){
						empIds = rs.getString("emp_id");
					} else {
						empIds += ","+rs.getString("emp_id");
					}
				}
				rs.close();
				pst.close(); 
				
	//			System.out.println("emp_ids====>"+empIds);
				
				sbQuery = new StringBuilder();
				sbQuery.append("select count(challan_no) as challan_no from ( select distinct(challan_no) as challan_no from challan_details where " +
						"financial_year_from_date = ? and financial_year_to_date = ? and challan_type=?  and ( ");
	            for(int i=0; i<tmpMonths.length; i++){
	                sbQuery.append(" month like '%,"+tmpMonths[i]+",%'");
	                
	                if(i<tmpMonths.length-1){
	                    sbQuery.append(" OR "); 
	                }
	            }
	            sbQuery.append(" ) ");
				sbQuery.append(" and is_paid=true and emp_id in (select eod.emp_id from employee_personal_details epd,employee_official_details eod " +
						"where eod.emp_id = epd.emp_per_id and org_id=?)) as a");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(3, TDS);
				pst.setInt(4, uF.parseToInt(orgId));
	//			System.out.println("pst====>"+pst);
				rs = pst.executeQuery();
				while(rs.next()){
					cntChallan = rs.getString("challan_no");
				}
				rs.close();
				pst.close();
				
				sbQuery = new StringBuilder();
				sbQuery.append("select count(emp_id) as cnt, sum(amount) as amount from challan_details where financial_year_from_date = ? and financial_year_to_date = ? and challan_type=? ");
				sbQuery.append(" and (");
	            for(int i=0; i<tmpMonths.length; i++){
	                sbQuery.append(" month like '%,"+tmpMonths[i]+",%'");
	                
	                if(i<tmpMonths.length-1){
	                    sbQuery.append(" OR "); 
	                }
	            }
	            sbQuery.append(" ) ");
				sbQuery.append(" and is_paid=true and emp_id in (select eod.emp_id from employee_personal_details epd,employee_official_details eod " +
						"where eod.emp_id = epd.emp_per_id and org_id=?)");
				
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(3, TDS);
				pst.setInt(4, uF.parseToInt(orgId));
	//			System.out.println("pst====>"+pst);
				rs = pst.executeQuery();
				while(rs.next()){
					empCount = rs.getInt("cnt");
					challanAmt = rs.getString("amount");
				}
				rs.close();
				pst.close(); 
							
				if(empIds !=null){
					sbQuery = new StringBuilder();
					sbQuery.append("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id=eod.emp_id " +
							"and eod.emp_id in("+empIds+")");
					pst = con.prepareStatement(sbQuery.toString());
	//				System.out.println("pst====>"+pst);
					rs = pst.executeQuery();
					while(rs.next()){
						Map<String, String> hmEmpInner = (Map<String, String>) hmEmpDetails.get(rs.getString("emp_id")); 
						if(hmEmpInner == null) hmEmpInner = new HashMap<String, String>();
	
						hmEmpInner.put("EMP_ID", rs.getString("emp_per_id"));
						hmEmpInner.put("EMPCODE", rs.getString("empcode"));
						
						String strMiddleName = "";
						
						if(flagMiddleName) {
							if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
								strMiddleName = " "+rs.getString("emp_mname");
							}
						}
						
						hmEmpInner.put("NAME", rs.getString("emp_fname") +strMiddleName+ " "+ rs.getString("emp_lname")); 
						hmEmpInner.put("CURRENT_ADDRESS", uF.showData(rs.getString("emp_address1_tmp"),"") + " "+ uF.showData(rs.getString("emp_address2_tmp"), ""));
						hmEmpInner.put("CURRENT_CITY", rs.getString("emp_city_id_tmp"));
						hmEmpInner.put("CURRENT_STATE", hmStates.get(rs.getString("emp_state_id_tmp")));
						hmEmpInner.put("CURRENT_COUNTRY", hmCountry.get(rs.getString("emp_country_id_tmp")));
						hmEmpInner.put("CURRENT_PINCODE", rs.getString("emp_pincode_tmp"));
						hmEmpInner.put("UAN_NO", rs.getString("uan_no"));
						hmEmpInner.put("UID_NO", rs.getString("uid_no"));
						hmEmpInner.put("PAN_NO", rs.getString("emp_pan_no"));
						hmEmpInner.put("PF_NO", rs.getString("emp_pf_no"));
						hmEmpInner.put("ESIC_NO", rs.getString("emp_esic_no"));
						hmEmpInner.put("GPF_ACC_NO", rs.getString("emp_gpf_no"));
						hmEmpInner.put("DESIGNATION_NAME", uF.showData(hmEmpDesig.get("emp_per_id"), ""));
						
						String joiningDate = uF.getDateFormat(rs.getString("joining_date"), DBDATE, DATE_FORMAT);
						String empDateFrom=strFinancialYearStart;
						if(uF.getDateFormat(joiningDate, DATE_FORMAT).after(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT))){
							empDateFrom=joiningDate;
						}
						hmEmpInner.put("EMP_DATE_FROM", uF.getDateFormat(empDateFrom, DATE_FORMAT, "ddMMyyyy"));
						
						String endDate=uF.getDateFormat(rs.getString("employment_end_date"), DBDATE, DATE_FORMAT);
						String empDateUp=strFinancialYearEnd;
						if(endDate!=null && !endDate.equals("") && !endDate.equals("-") && uF.getDateFormat(endDate, DATE_FORMAT).before(uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT))){
							empDateUp=endDate;
						}
						hmEmpInner.put("EMP_DATE_UP", uF.getDateFormat(empDateUp, DATE_FORMAT, "ddMMyyyy"));
						
						hmEmpDetails.put(rs.getString("emp_id"), hmEmpInner);
					}
					rs.close();
					pst.close(); 
					
					sbQuery = new StringBuilder();
					sbQuery.append("select distinct(cd.challan_no),cd.brc_code from challan_details cd where cd.financial_year_from_date = ? " +
							"and cd.financial_year_to_date = ? and cd.challan_type=? and (");
		            for(int i=0; i<tmpMonths.length; i++){
		                sbQuery.append(" month like '%,"+tmpMonths[i]+",%'");
		                
		                if(i<tmpMonths.length-1){
		                    sbQuery.append(" OR "); 
		                }
		            }
		            sbQuery.append(" ) ");
					sbQuery.append("and cd.is_paid=true order by cd.challan_no");
					pst = con.prepareStatement(sbQuery.toString());
					pst.setDate(1,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(2,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					pst.setInt(3, TDS);
	//				System.out.println("pst====>"+pst);
					rs = pst.executeQuery();
					while(rs.next()){
						hmBRCCode.put(rs.getString("challan_no"), rs.getString("brc_code"));
					}
					rs.close();
					pst.close();
					
					
					sbQuery = new StringBuilder();
					sbQuery.append("select distinct(challan_no) as challan_no,under_section234,interest_amt,penalty_amt,surcharge,edu_cess,income_tax from challan_details where " +
							"financial_year_from_date=? and financial_year_to_date=? and challan_type=? and (");
		            for(int i=0; i<tmpMonths.length; i++){
		                sbQuery.append(" month like '%,"+tmpMonths[i]+",%'");
		                
		                if(i<tmpMonths.length-1){
		                    sbQuery.append(" OR "); 
		                }
		            }
		            sbQuery.append(" ) ");
					sbQuery.append(" and is_paid=true and emp_id in (select eod.emp_id from employee_personal_details epd,employee_official_details eod " +
							"where eod.emp_id = epd.emp_per_id and org_id=?)");
					pst = con.prepareStatement(sbQuery.toString());
					pst.setDate(1,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(2,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					pst.setInt(3, TDS);
					pst.setInt(4, uF.parseToInt(orgId));
	//				System.out.println("pst====>"+pst);
					rs = pst.executeQuery();
					while(rs.next()){
						paidOtherCharges += uF.parseToDouble(rs.getString("under_section234"))+uF.parseToDouble(rs.getString("interest_amt")) 
						+ uF.parseToDouble(rs.getString("penalty_amt")) + uF.parseToDouble(rs.getString("surcharge"));
						
						challanSetList.add(rs.getString("challan_no"));
						
						Map<String, String> hmChallanAmt = (Map<String, String>) hmChallanAmtDetails.get(rs.getString("challan_no"));
						if(hmChallanAmt == null) hmChallanAmt = new HashMap<String, String>();
						
						hmChallanAmt.put("INTEREST_AMT", rs.getString("interest_amt"));
						hmChallanAmt.put("PENALTY_AMT", rs.getString("penalty_amt"));
						hmChallanAmt.put("UNDER_SECTION234", rs.getString("under_section234"));
						hmChallanAmt.put("SURCHARGE", rs.getString("surcharge"));
						hmChallanAmt.put("EDU_CESS", rs.getString("edu_cess"));
						hmChallanAmt.put("INCOME_TAX", rs.getString("income_tax"));
						
						double totalAmt = uF.parseToDouble(rs.getString("under_section234"))+uF.parseToDouble(rs.getString("interest_amt")) 
						+ uF.parseToDouble(rs.getString("penalty_amt")) + uF.parseToDouble(rs.getString("surcharge"))
						+ uF.parseToDouble(rs.getString("edu_cess")) + uF.parseToDouble(rs.getString("income_tax"));
						
						hmChallanAmt.put("TOTAL_AMT", ""+totalAmt);
						
						hmChallanAmtDetails.put(rs.getString("challan_no"), hmChallanAmt);
						
					}
					rs.close();
					pst.close(); 
					
					sbQuery = new StringBuilder();
					sbQuery.append("select challan_no,paid_date,count(emp_id) as cnt from challan_details where " +
							"financial_year_from_date=? and financial_year_to_date=? and challan_type=? and (");
		            for(int i=0; i<tmpMonths.length; i++){
		                sbQuery.append(" month like '%,"+tmpMonths[i]+",%'");
		                
		                if(i<tmpMonths.length-1){
		                    sbQuery.append(" OR "); 
		                }
		            }
		            sbQuery.append(" ) ");
					sbQuery.append(" and is_paid=true and emp_id in (select eod.emp_id from employee_personal_details epd,employee_official_details eod " +
							"where eod.emp_id = epd.emp_per_id and org_id=?) group by challan_no,paid_date");
					pst = con.prepareStatement(sbQuery.toString());
					pst.setDate(1,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(2,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					pst.setInt(3, TDS);
					pst.setInt(4, uF.parseToInt(orgId));
	//				System.out.println("pst====>"+pst);
					rs = pst.executeQuery();
					while(rs.next()){
						hmChallanEmpCnt.put(rs.getString("challan_no"),rs.getString("cnt"));
						hmChallanDate.put(rs.getString("challan_no"),uF.getDateFormat(rs.getString("paid_date"), DBDATE, "ddMMyyyy"));
					}
					rs.close();
					pst.close();
					
					String strMonth = "";
		            for(int i=0; i<tmpMonths.length; i++){
		                if(i==0){
		                	strMonth = tmpMonths[i];
		                } else {
		                	strMonth += ","+ tmpMonths[i];
		                }
		            }
		            
					sbQuery = new StringBuilder();
					sbQuery.append("select a.amount,a.emp_id,cd.challan_no,a.educess from (select sum(etd.actual_tds_amount) as amount,sum(etd.edu_tax_amount+etd.std_tax_amount) as educess,etd.emp_id,etd._month from emp_tds_details etd " +
							"where etd.financial_year_start = ? and etd.financial_year_end = ? and etd._month  in ("+strMonth+") and etd.emp_id " +
							"in (select eod.emp_id from employee_personal_details epd,employee_official_details eod where eod.emp_id = epd.emp_per_id and org_id=?)" +
							" group by etd.emp_id,etd._month) a,challan_details cd where cd.emp_id=a.emp_id and cd.financial_year_from_date = ? " +
							"and cd.financial_year_to_date = ? and cd.challan_type=? and (");
		            for(int i=0; i<tmpMonths.length; i++){
		                sbQuery.append(" cd.month like '%,"+tmpMonths[i]+",%'");
		                
		                if(i<tmpMonths.length-1){
		                    sbQuery.append(" OR "); 
		                }
		            }
		            sbQuery.append(" ) ");
					sbQuery.append(" and cd.is_paid=true and cd.month like '%,'||a._month||',%' order by emp_id");
					pst = con.prepareStatement(sbQuery.toString());
					pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					pst.setInt(3, uF.parseToInt(orgId));
					pst.setDate(4, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(5, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					pst.setInt(6, TDS);
	//				System.out.println("pst====>"+pst);
					rs = pst.executeQuery();
					while(rs.next()){
						List<Map<String, String>> empList = (List<Map<String, String>>) hmEmpChallanAmtDetails.get(rs.getString("challan_no")); 
						if(empList == null) empList = new ArrayList<Map<String,String>>(); 
						
						Map<String, String> hmChallanAmt = new HashMap<String, String>();
						hmChallanAmt.put("EMP_ID", rs.getString("emp_id"));
						double totalAmt = Math.round(uF.parseToDouble(rs.getString("amount")));					
						hmChallanAmt.put("EMP_CHALLAN_AMT", ""+totalAmt);
						
						double dblEduCess = Math.round(uF.parseToDouble(rs.getString("educess")));					
						hmChallanAmt.put("EMP_EDU_CESS_AMT", ""+dblEduCess);
						
						empList.add(hmChallanAmt);
						
						hmEmpChallanAmtDetails.put(rs.getString("challan_no"), empList);
						
						double dblEmpTotalAmt = uF.parseToDouble(hmEmpChallanTotalAmt.get(rs.getString("emp_id")));
						dblEmpTotalAmt += Math.round(uF.parseToDouble(rs.getString("amount")));
						hmEmpChallanTotalAmt.put(rs.getString("emp_id"), ""+dblEmpTotalAmt);
					}
					rs.close();
					pst.close();
									
					sbQuery = new StringBuilder(); 
					sbQuery.append("select a.amount,a.emp_id,cd.challan_no from ( select sum(pg.amount) as amount,pg.emp_id,pg.month from payroll_generation pg " +
							"where pg.financial_year_from_date=? and pg.financial_year_to_date =? and pg.is_paid = true " +
							"and pg.salary_head_id not in("+REIMBURSEMENT+","+MOBILE_REIMBURSEMENT+","+TRAVEL_REIMBURSEMENT+","+OTHER_REIMBURSEMENT+","+SERVICE_TAX+","+SWACHHA_BHARAT_CESS+","+KRISHI_KALYAN_CESS+","+CGST+","+SGST+") " +
							"and pg.month  in ("+strMonth+") and pg.earning_deduction='E' and pg.emp_id in (select eod.emp_id from employee_personal_details epd," +
							"employee_official_details eod where eod.emp_id = epd.emp_per_id and org_id=?) group by pg.emp_id,pg.month) a,challan_details cd " +
							"where cd.emp_id=a.emp_id and cd.financial_year_from_date = ? and cd.financial_year_to_date = ? and cd.challan_type=? and (");
		            for(int i=0; i<tmpMonths.length; i++){
		                sbQuery.append(" cd.month like '%,"+tmpMonths[i]+",%'");
		                
		                if(i<tmpMonths.length-1){
		                    sbQuery.append(" OR "); 
		                }
		            }
		            sbQuery.append(" ) ");
					sbQuery.append(" and cd.is_paid=true and cd.month like '%,'||a.month||',%' order by emp_id");
					pst = con.prepareStatement(sbQuery.toString());
					pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					pst.setInt(3, uF.parseToInt(orgId));
					pst.setDate(4, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(5, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					pst.setInt(6, TDS);
	//				System.out.println("pst====>"+pst);
					rs = pst.executeQuery();
					while(rs.next()){
						hmEmpSalaryAmtDetails.put(rs.getString("challan_no")+"_"+rs.getString("emp_id"), uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("amount"))));
						
						double dblTotal = uF.parseToDouble(hmEmpTotalSalaryAmt.get(rs.getString("emp_id")));
						dblTotal += uF.parseToDouble(rs.getString("amount"));
						
						hmEmpTotalSalaryAmt.put(rs.getString("emp_id"), ""+dblTotal);
					}
					rs.close();
					pst.close();
				} 
				challanAmt = ""+(uF.parseToDouble(challanAmt) + paidOtherCharges);
				
				if(stQr.equals("Q4")){
					Form16 form16 = new Form16();
					form16.request = request;
					form16.CF = CF;
					form16.financialYear = getFinancialYear();
					form16.viewForm16(CF);
				}
			}
			
//			System.out.println("empCount====>"+empCount);
//			System.out.println("challanAmt====>"+challanAmt);
//			System.out.println("paidAmt====>"+paidAmt);
//			System.out.println("paidTDSAmt====>"+paidTDSAmt);
//			System.out.println("hmEmpChallanAmtDetails====>"+hmEmpChallanAmtDetails);
//			System.out.println("hmChallanAmtDetails====>"+hmChallanAmtDetails);
//			System.out.println("hmEmpSalaryAmtDetails====>"+hmEmpSalaryAmtDetails);
//			System.out.println("hmEmpChallanTotalAmt========>"+hmEmpChallanTotalAmt);
			
			request.setAttribute("empCount", ""+empCount);
			request.setAttribute("challanAmt", challanAmt);
			request.setAttribute("paidAmt", paidAmt);
			request.setAttribute("paidTDSAmt", paidTDSAmt);
//			request.setAttribute("empSetList", empSetList);
			request.setAttribute("cntChallan", ""+cntChallan);
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("hmOrg", hmOrg);
			request.setAttribute("hmEmp", hmEmp);
			request.setAttribute("hmStates", hmStates);
			request.setAttribute("hmEmpCodeDesig", hmEmpCodeDesig);
			request.setAttribute("hmOtherDetailsMap", hmOtherDetailsMap);
			request.setAttribute("challanSetList", challanSetList);
			request.setAttribute("hmChallanEmpCnt", hmChallanEmpCnt);
			request.setAttribute("hmChallanDate", hmChallanDate);
			request.setAttribute("hmChallanAmtDetails", hmChallanAmtDetails);
			request.setAttribute("hmEmpChallanAmtDetails", hmEmpChallanAmtDetails); 
			request.setAttribute("hmEmpDetails", hmEmpDetails);
			request.setAttribute("hmEmpSalaryAmtDetails", hmEmpSalaryAmtDetails);
			request.setAttribute("hmEmpTotalSalaryAmt", hmEmpTotalSalaryAmt);
			request.setAttribute("hmEmpAgeMap", hmEmpAgeMap);
			request.setAttribute("hmEmpGenderMap", hmEmpGenderMap);
			request.setAttribute("hmEmpChallanTotalAmt", hmEmpChallanTotalAmt);
			request.setAttribute("hmBRCCode", hmBRCCode);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}






	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getStrSubmit() {
		return strSubmit;
	}

	public void setStrSubmit(String strSubmit) {
		this.strSubmit = strSubmit;
	}

	public String getFinancialYear() {
		return financialYear;
	}

	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}

	public List<FillFinancialYears> getFinancialYearList() {
		return financialYearList;
	}


	public void setFinancialYearList(List<FillFinancialYears> financialYearList) {
		this.financialYearList = financialYearList;
	}


	public String getStrSelectedEmpId() {
		return strSelectedEmpId;
	}


	public void setStrSelectedEmpId(String strSelectedEmpId) {
		this.strSelectedEmpId = strSelectedEmpId;
	}


	public List<FillEmployee> getEmpNamesList() {
		return empNamesList;
	}


	public void setEmpNamesList(List<FillEmployee> empNamesList) {
		this.empNamesList = empNamesList;
	}


	public String getStrMonth() {
		return strMonth;
	}


	public void setStrMonth(String strMonth) {
		this.strMonth = strMonth;
	}


	public List<FillMonth> getMonthList() {
		return monthList;
	}


	public void setMonthList(List<FillMonth> monthList) {
		this.monthList = monthList;
	}


	public String getStrQuarter() {
		return strQuarter;
	}


	public void setStrQuarter(String strQuarter) {
		this.strQuarter = strQuarter;
	}


	public String getF_strWLocation() {
		return f_strWLocation;
	}


	public void setF_strWLocation(String f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
	}


	public String getF_level() {
		return f_level;
	}


	public void setF_level(String f_level) {
		this.f_level = f_level;
	}


	public String getF_org() {
		return f_org;
	}


	public void setF_org(String f_org) {
		this.f_org = f_org;
	}


	public List<FillOrganisation> getOrgList() {
		return orgList;
	}


	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
	}


	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}


	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}


	public List<FillLevel> getLevelList() {
		return levelList;
	}


	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}


	public String getFormType() {
		return formType;
	}


	public void setFormType(String formType) {
		this.formType = formType;
	}

	private HttpServletResponse response;
	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}


}
