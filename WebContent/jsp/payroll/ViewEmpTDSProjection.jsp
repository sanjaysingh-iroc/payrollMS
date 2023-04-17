<%@page import="com.itextpdf.text.BaseColor"%>
<%@page import="com.itextpdf.text.Element"%>
<%@page import="com.konnect.jpms.export.DataStyle"%>
<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>

<%
	CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
	UtilityFunctions uF = new UtilityFunctions();
	String strEmpId = request.getParameter("strEmpId");
	String strCurrSymbol = (String) request.getAttribute("strCurrSymbol");
	List<Map<String, String>> alSalaryHead = (List<Map<String, String>>) request.getAttribute("alSalaryHead");
	if(alSalaryHead == null) alSalaryHead = new ArrayList<Map<String,String>>();
	Map<String, String> hmEmpSalaryHeadPaidAmt = (Map<String, String>) request.getAttribute("hmEmpSalaryHeadPaidAmt");
	if(hmEmpSalaryHeadPaidAmt == null) hmEmpSalaryHeadPaidAmt = new HashMap<String, String>();
	Map<String, String> hmProjectSalAmt = (Map<String, String>) request.getAttribute("hmProjectSalAmt");
	if(hmProjectSalAmt == null) hmProjectSalAmt = new HashMap<String, String>();
	String strTotalGross = (String) request.getAttribute("totalGrossYear");
	double totalGrossYear = uF.parseToDouble(strTotalGross);
	
	Map hmTaxInner = (Map) request.getAttribute("hmTaxInner");
	if(hmTaxInner == null) hmTaxInner = new HashMap();
	
	Map hmSectionLimitA = (Map)request.getAttribute("hmSectionLimitA");
	if(hmSectionLimitA==null)hmSectionLimitA=new HashMap();

	Map hmSectionLimitP = (Map)request.getAttribute("hmSectionLimitP");
	if(hmSectionLimitP==null)hmSectionLimitP=new HashMap();
	
	
	Map<String, Map<String, String>> hmEmpInvestment =(Map<String, Map<String, String>>)request.getAttribute("hmEmpInvestment");
	if(hmEmpInvestment==null)hmEmpInvestment=new HashMap<String, Map<String, String>>();

	Map<String, Map<String, List<Map<String, String>>>> hmEmpSubInvestment =(Map<String, Map<String, List<Map<String, String>>>>)request.getAttribute("hmEmpSubInvestment");
	if(hmEmpSubInvestment==null)hmEmpSubInvestment=new HashMap<String, Map<String, List<Map<String, String>>>>();

	Map<String, Map<String, String>> hmSectionwiseSubSecAdjusted10PerLimitAmt = (Map<String, Map<String, String>>) request.getAttribute("hmSectionwiseSubSecAdjusted10PerLimitAmt");
	if(hmSectionwiseSubSecAdjusted10PerLimitAmt==null)hmSectionwiseSubSecAdjusted10PerLimitAmt = new HashMap<String, Map<String, String>>();
		
	Map<String, Map<String, String>> hmEmpInvestment1 =(Map<String, Map<String, String>>)request.getAttribute("hmEmpInvestment1");
	if(hmEmpInvestment1==null)hmEmpInvestment1=new HashMap<String, Map<String, String>>();

	Map<String, String> hmSectionMap =(Map<String, String>)request.getAttribute("hmSectionMap");

	List<String> chapter1SectionList = (List<String>) request.getAttribute("chapter1SectionList");
	if(chapter1SectionList==null)chapter1SectionList = new ArrayList<String>();
	
	List<String> chapter2SectionList = (List<String>) request.getAttribute("chapter2SectionList");
	if(chapter2SectionList==null) chapter2SectionList = new ArrayList<String>();

	Map<String, String> hmSectionPFApplicable = (Map<String, String>) request.getAttribute("hmSectionPFApplicable");
	if(hmSectionPFApplicable == null) hmSectionPFApplicable = new HashMap<String, String>();
	
	Map<String, Map<String, String>> hmEmpActualInvestment = (Map<String, Map<String, String>>) request.getAttribute("hmEmpActualInvestment");
	if(hmEmpActualInvestment==null) hmEmpActualInvestment = new HashMap<String, Map<String, String>>();

	Map<String, Map<String, String>> hmEmpActualInvestment1 = (Map<String, Map<String, String>>) request.getAttribute("hmEmpActualInvestment1");
	if(hmEmpActualInvestment1==null) hmEmpActualInvestment1 = new HashMap<String, Map<String, String>>();
	
	Map<String, String> hmTDSRemainMonth = (Map<String, String>)request.getAttribute("hmTDSRemainMonth");
	if(hmTDSRemainMonth == null) hmTDSRemainMonth = new LinkedHashMap<String, String>();
	
	Map<String, Map<String, String>> hmEmpTDSReimbCTC = (Map<String, Map<String, String>>) request.getAttribute("hmEmpTDSReimbCTC");
	if(hmEmpTDSReimbCTC == null) hmEmpTDSReimbCTC = new HashMap<String, Map<String, String>>();
	
	Map<String, String> hmPrevOrgTDSDetails = (Map<String, String>) request.getAttribute("hmPrevOrgTDSDetails");
	if(hmPrevOrgTDSDetails==null) hmPrevOrgTDSDetails = new HashMap<String, String>();
	
	List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
	List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
	
	String strUserType = (String)session.getAttribute(IConstants.USERTYPE);
	System.out.println(strUserType);
%>

<script type="text/javascript">
function generateSalaryExcel(){
	window.location="ExportExcelReport.action";
}
</script>

<%int nSalaryHeadSize = alSalaryHead.size();
if(nSalaryHeadSize > 0) { %>
		<!-- ===start parvez date: 24-02-2023=== -->
			<%if(strUserType != null && (strUserType.equals(IConstants.ADMIN) || strUserType.equals(IConstants.ACCOUNTANT))){ %>
		<!-- ===end parvez date: 24-02-2023=== -->	
			<div class="row row_without_margin paddingtopbottom10" style="padding-top: 0px;">
				<div class="col-lg-12 col-md-12 col-sm-12" style="text-align: right;">
					<a onclick="generateSalaryExcel();" href="javascript:void(0)" class="excel"></a>
				</div>
			</div>
			<%} %>
	<table class="table table-bordered">
		<tr>
			<th class="reportLabel">Particulars</th>
			<th class="reportLabel" align="center">Salary Paid</th>
			<th class="reportLabel" align="center">Projected</th>
			<th class="reportLabel" align="center">Applied Amount</th>
			<th class="reportLabel" align="center">Amount</th>
			<%
			alInnerExport.add(new DataStyle("Employee TDS Projection Report",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
			reportListExport.add(alInnerExport);
			alInnerExport = new ArrayList<DataStyle>();
			alInnerExport.add(new DataStyle("Particulars ",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Salary Paid ",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Projected ",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Applied Amount ",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Amount ",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
			%>
		</tr>
		<% reportListExport.add(alInnerExport); %>
	<%	
		double dblSalPaidAmtTotal = 0.0d;
		double dblSalProjectedAmtTotal = 0.0d;
		for(int i = 0; i < nSalaryHeadSize; i++){ 
			Map<String, String> hmSalaryHead = alSalaryHead.get(i);
			if(hmSalaryHead == null) hmSalaryHead = new HashMap<String, String>();
			String strSalaryHeadId = hmSalaryHead.get("SALARY_HEAD_ID");
			dblSalPaidAmtTotal += uF.parseToDouble(hmEmpSalaryHeadPaidAmt.get(strSalaryHeadId));
			dblSalProjectedAmtTotal += uF.parseToDouble(uF.formatIntoZeroWithOutComma(uF.parseToDouble(hmProjectSalAmt.get(strSalaryHeadId))));
			double dblTotal = uF.parseToDouble(hmEmpSalaryHeadPaidAmt.get(strSalaryHeadId)) + uF.parseToDouble(hmProjectSalAmt.get(strSalaryHeadId)); 
		%>
			<tr>
				<td><%=uF.showData(hmSalaryHead.get("SALARY_HEAD_NAME"),"") %></td>
				<td class="alignRight"><%=uF.showData(strCurrSymbol,"")+ uF.formatIntoOneDecimal(uF.parseToDouble(hmEmpSalaryHeadPaidAmt.get(strSalaryHeadId))) %></td>
				<td class="alignRight"><%=uF.showData(strCurrSymbol,"")+ uF.formatIntoOneDecimal(uF.parseToDouble(hmProjectSalAmt.get(strSalaryHeadId))) %></td>
				<td class="alignRight">&nbsp;</td>
				<td class="alignRight"><%=uF.showData(strCurrSymbol,"")+uF.formatIntoOneDecimal(dblTotal) %></td>
			<%
			alInnerExport = new ArrayList<DataStyle>();
			alInnerExport.add(new DataStyle(uF.showData(hmSalaryHead.get("SALARY_HEAD_NAME"),""),Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle(uF.formatIntoOneDecimal(uF.parseToDouble(hmEmpSalaryHeadPaidAmt.get(strSalaryHeadId))),Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle(uF.formatIntoOneDecimal(uF.parseToDouble(hmProjectSalAmt.get(strSalaryHeadId))),Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle(uF.formatIntoOneDecimal(dblTotal),Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
			%>
			</tr>
			<% reportListExport.add(alInnerExport); %>
		<% } %>
		
		<%
		Iterator<String> itRCTC = hmEmpTDSReimbCTC.keySet().iterator();
		while(itRCTC.hasNext()){
			String strReimCTCId = itRCTC.next(); 
			Map<String, String> hmEmpTDSReimbCTCInner = hmEmpTDSReimbCTC.get(strReimCTCId);
			if(hmEmpTDSReimbCTCInner == null) hmEmpTDSReimbCTCInner = new HashMap<String, String>();
		%>
			<tr>
				<td><%=uF.showData(hmEmpTDSReimbCTCInner.get("REIMBURSEMENT_NAME"),"") %></td>
				<td class="alignRight">&nbsp;</td>
				<td class="alignRight">&nbsp;</td>
				<td class="alignRight">&nbsp;</td>
				<td class="alignRight"><%=uF.showData(strCurrSymbol,"")+uF.formatIntoOneDecimal(uF.parseToDouble(hmEmpTDSReimbCTCInner.get("REIMBURSEMENT_TDS_AMOUNT"))) %></td>
			<%
			alInnerExport = new ArrayList<DataStyle>();
			alInnerExport.add(new DataStyle(uF.showData(hmEmpTDSReimbCTCInner.get("REIMBURSEMENT_NAME"),""),Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle(uF.formatIntoOneDecimal(uF.parseToDouble(hmEmpTDSReimbCTCInner.get("REIMBURSEMENT_TDS_AMOUNT"))),Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
			%>
			</tr>
			<% reportListExport.add(alInnerExport); %>
		<%} %>
		
		<tr>
			<td class="alignLeft"><strong>1. Gross Salary</strong></td>
			<td class="alignRight"><%=uF.showData(strCurrSymbol,"")+uF.formatIntoOneDecimal(dblSalPaidAmtTotal) %></td>
			<td class="alignRight"><%=uF.showData(strCurrSymbol,"")+uF.formatIntoOneDecimal(dblSalProjectedAmtTotal) %></td>
			<td class="alignRight">&nbsp;</td>
			<td class="alignRight"><%=uF.showData(strCurrSymbol,"")+Math.round(totalGrossYear) %></td>
			<%
			alInnerExport = new ArrayList<DataStyle>();
			alInnerExport.add(new DataStyle("1. Gross Salary",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle(uF.formatIntoOneDecimal(dblSalPaidAmtTotal),Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle(uF.formatIntoOneDecimal(dblSalProjectedAmtTotal),Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle(""+Math.round(totalGrossYear),Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
			%>
			<% reportListExport.add(alInnerExport); %>
		</tr>
		<tr>
			<td class="alignLeft" colspan="5"><strong>2. Less: allowance to the extent exempt u/s 10</strong></td>
			<%
			alInnerExport = new ArrayList<DataStyle>();
			alInnerExport.add(new DataStyle("2. Less: allowance to the extent exempt u/s 10",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
			reportListExport.add(alInnerExport);
			%>
		</tr>
		
		<% 
		Map<String, Map<String, String>> hmUnderSection10_16Map = (Map<String, Map<String, String>>)request.getAttribute("hmUnderSection10_16Map");
		if(hmUnderSection10_16Map == null) hmUnderSection10_16Map = new HashMap<String, Map<String, String>>();
		Map<String, Map<String, String>> hmUnderSection10_16PaidMap = (Map<String, Map<String, String>>)request.getAttribute("hmUnderSection10_16PaidMap");
		if(hmUnderSection10_16PaidMap == null) hmUnderSection10_16PaidMap = new HashMap<String, Map<String, String>>();
		Map<String, Map<String, List<String>>> hmExemptionDataUnderSection = (Map<String, Map<String, List<String>>>)request.getAttribute("hmExemptionDataUnderSection");
		if(hmExemptionDataUnderSection == null) hmExemptionDataUnderSection = new HashMap<String, Map<String, List<String>>>();
		
		Map<String, String> hmUS10_16_SalHeadData = (Map<String, String>)hmTaxInner.get("hmUS10_16_SalHeadData");
		if(hmUS10_16_SalHeadData == null) hmUS10_16_SalHeadData = new HashMap<String, String>();
		
		Map<String, String> hmUS10_16Inner= (Map<String, String>)hmUnderSection10_16Map.get(strEmpId);
	    if(hmUS10_16Inner==null) hmUS10_16Inner=new HashMap<String, String>();
	    //System.out.println("hmUS10_16Inner ===>> " + hmUS10_16Inner);
	    
	    Map<String, String> hmUS10_16InnerPaid= (Map<String, String>)hmUnderSection10_16PaidMap.get(strEmpId);
		if(hmUS10_16InnerPaid==null) hmUS10_16InnerPaid=new HashMap<String, String>();
		
		//System.out.println("hmExemptionDataUnderSection ===>> " + hmExemptionDataUnderSection);
		
	    Map<String, List<String>> hmExemptionData = hmExemptionDataUnderSection.get("4"); //US10
	    if(hmExemptionData == null) hmExemptionData = new HashMap<String, List<String>>();
	    
	    Iterator<String> itExamption = hmExemptionData.keySet().iterator();
	    double dblTotUS10ExemptAmt = 0.0d;
	    int exempCnt = 0;
	    String[] exempSerials = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
	    while (itExamption.hasNext()) {
			String exemptionId = itExamption.next();
			List<String> innerList = hmExemptionData.get(exemptionId);
			double dblAmtPaid = uF.parseToDouble(hmUS10_16InnerPaid.get(innerList.get(6)));
			double dblAmtExempt = uF.parseToDouble(hmUS10_16Inner.get(innerList.get(6)));
			if(uF.parseToInt(innerList.get(6)) == IConstants.HRA || uF.parseToInt(innerList.get(6)) == IConstants.CONVEYANCE_ALLOWANCE || uF.parseToInt(innerList.get(6)) == IConstants.EDUCATION_ALLOWANCE || uF.parseToInt(innerList.get(6)) == IConstants.PROFESSIONAL_TAX || uF.parseToInt(innerList.get(6)) == IConstants.LTA) {
				dblAmtPaid = uF.parseToDouble(hmUS10_16_SalHeadData.get(innerList.get(6)+"_PAID"));
				dblAmtExempt = uF.parseToDouble(hmUS10_16_SalHeadData.get(innerList.get(6)+"_EXEMPT"));
				//System.out.println("Amount="+dblAmtExempt+"---Salary Head Id=="+innerList.get(6));
			}
			if(uF.parseToInt(innerList.get(6)) == 0) {
				dblAmtExempt = uF.parseToDouble(innerList.get(3));
			}
			dblTotUS10ExemptAmt += dblAmtExempt;
			
		%>
			<tr>
				<td class="reportLabel"><%="  "+exempSerials[exempCnt]+") "+innerList.get(1) %></td>
				<td class="reportLabel alignRight"><%=""+Math.round(dblAmtPaid) %></td>
				<td class="reportLabel alignRight"><%=""+Math.round(dblAmtExempt) %></td>
				<%
				alInnerExport = new ArrayList<DataStyle>();
				alInnerExport.add(new DataStyle("  "+exempSerials[exempCnt]+") "+innerList.get(1),Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(""+Math.round(dblAmtPaid),Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(""+Math.round(dblAmtExempt),Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				
				reportListExport.add(alInnerExport);
				%>
			</tr>
		
		<% exempCnt++; } %>
		
		<% 
		
		//System.out.println("dblSalPaidAmtTotal "+dblSalPaidAmtTotal);
		//System.out.println("totalGrossYear "+totalGrossYear);
		//System.out.println("dblTotUS10ExemptAmt "+dblTotUS10ExemptAmt);
		
		//Created by Dattatray 08-06-2022 Note : totalGrossYear to dblSalPaidAmtTotal
		//Created by Dattatray 10-06-2022
		double dblGrossNet = Math.round(totalGrossYear) - Math.round(dblTotUS10ExemptAmt); %>
		
		<tr>
			<td class="alignLeft" colspan="4"><strong>3. Balance (1-2)</strong></td>
			<td class="alignRight"><%=uF.showData(strCurrSymbol,"")+Math.round(dblGrossNet) %></td>
			<%
			alInnerExport = new ArrayList<DataStyle>();
			alInnerExport.add(new DataStyle("3. Balance (1-2)",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle(""+Math.round(dblGrossNet),Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
			reportListExport.add(alInnerExport);
			%>
		</tr>
		
		<tr>
			<td class="alignLeft" colspan="5"><strong>4. Less: allowance to the extent exempt u/s 16</strong></td>
			<%
			alInnerExport = new ArrayList<DataStyle>();
			alInnerExport.add(new DataStyle("4. Less: allowance to the extent exempt u/s 16",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
			reportListExport.add(alInnerExport);
			%>
		</tr>
		
		<% 
		Map<String, List<String>> hmExemptionDataUS16 = hmExemptionDataUnderSection.get("5"); //US16
		if(hmExemptionDataUS16 == null) hmExemptionDataUS16 = new HashMap<String, List<String>>();
		
	    Iterator<String> itExamptionUS16 = hmExemptionDataUS16.keySet().iterator();
	    double dblTotUS16ExemptAmt = 0.0d;
	    int exempCntUS16 = 0;
	    String[] exempSerialsUS16 = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
	    while (itExamptionUS16.hasNext()) {
			String exemptionId = itExamptionUS16.next();
			List<String> innerList = hmExemptionDataUS16.get(exemptionId);
			double dblAmtPaid = uF.parseToDouble(hmUS10_16InnerPaid.get(innerList.get(6)));
			double dblAmtExempt = uF.parseToDouble(hmUS10_16Inner.get(innerList.get(6)));
			if(uF.parseToInt(innerList.get(6)) == IConstants.PROFESSIONAL_TAX) {
				dblAmtPaid = uF.parseToDouble(hmUS10_16_SalHeadData.get(innerList.get(6)+"_PAID"));
				dblAmtExempt = uF.parseToDouble(hmUS10_16_SalHeadData.get(innerList.get(6)+"_EXEMPT"));
			}
			if(uF.parseToInt(innerList.get(6)) == 0) {
				dblAmtExempt = uF.parseToDouble(innerList.get(3));
			}
			dblTotUS16ExemptAmt += dblAmtExempt;
		%>
			<tr>
				<td class="reportLabel"><%="  "+exempSerialsUS16[exempCntUS16]+") "+innerList.get(1) %></td>
				<td class="reportLabel alignRight"><%=Math.round(dblAmtExempt)%></td>
				<td class="reportLabel alignRight"><%="" %></td>
				<%
				alInnerExport = new ArrayList<DataStyle>();
				alInnerExport.add(new DataStyle("  "+exempSerialsUS16[exempCntUS16]+") "+innerList.get(1),Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(Math.round(dblAmtExempt)+"",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				reportListExport.add(alInnerExport);
				%>
			</tr>
		<% exempCntUS16++; } %>
		
		
		<tr>
			<td class="alignLeft" colspan="4"><strong>5. Aggregate of 4(a) and (b)</strong></td>
			<td class="alignRight"><%=(Math.round(dblTotUS16ExemptAmt)) %>
			</td> 
			<%
				alInnerExport = new ArrayList<DataStyle>();
				alInnerExport.add(new DataStyle("5. Aggregate of 4(a) and (b)",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(""+(Math.round(dblTotUS16ExemptAmt)),Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				reportListExport.add(alInnerExport);
				%>
		</tr>
		<%-- <%System.out.println("VETDSP.jsp/333---homeLoan=="+uF.parseToDouble((String)hmTaxInner.get("dblHomeLoanTaxExempt"))); %> --%>
		<%-- <%double dblHomeLoanTaxExempt = uF.parseToDouble((String)hmTaxInner.get("dblHomeLoanTaxExempt")); %>
		<tr>
			<td class="alignLeft" colspan="4"><strong>6. Home Loan Interest</strong></td>
			<td class="alignRight"><%=uF.showData(strCurrSymbol, "")+Math.round(dblHomeLoanTaxExempt) %></td>
		</tr> --%>
		
		<%
			dblGrossNet -= (Math.round(dblTotUS16ExemptAmt));
		    //dblGrossNet -= Math.round(dblHomeLoanTaxExempt);
		 	/* dblGrossNet -= uF.parseToDouble((String)hmTaxInner.get("dblProfessionalTaxExempt"));
	    	dblGrossNet -= dblHomeLoanTaxExempt; */
		%>
		
		<tr>
			<td class="alignLeft" colspan="4"><strong>6. Income Chargeable under the head 'salaries (3-5)</strong></td>
			<td class="alignRight"><%=uF.showData(strCurrSymbol,"")+Math.round(dblGrossNet) %></td>
			<%
				alInnerExport = new ArrayList<DataStyle>();
				alInnerExport.add(new DataStyle("6. Income Chargeable under the head 'salaries (3-5)",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(""+Math.round(dblGrossNet),Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				reportListExport.add(alInnerExport);
			%>
		</tr>
		
		<% double dblIncomeFromOther=uF.parseToDouble((String)hmTaxInner.get("dblIncomeFromOther")); %>
		<tr>
			<td class="alignLeft" colspan="4"><strong>7. a) Any other income reported by the employee</strong></td>
			<td class="alignRight"><%=uF.showData(strCurrSymbol,"")+Math.round(dblIncomeFromOther) %></td>
			<%
				alInnerExport = new ArrayList<DataStyle>();
				alInnerExport.add(new DataStyle("7. a) Any other income reported by the employee",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(""+Math.round(dblIncomeFromOther),Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				reportListExport.add(alInnerExport);
			%>
		</tr>
		<tr>
			<td class="alignLeft" colspan="4"><strong>&nbsp;&nbsp;&nbsp; b) Income from previous Organization by the employee</strong></td>
			<td class="alignRight"><%=uF.showData(strCurrSymbol,"")+Math.round(uF.parseToDouble(hmPrevOrgTDSDetails.get(strEmpId+"_GROSS_AMT"))) %></td>
			<%
				alInnerExport = new ArrayList<DataStyle>();
				alInnerExport.add(new DataStyle("b) Income from previous Organization by the employee",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(""+Math.round(uF.parseToDouble(hmPrevOrgTDSDetails.get(strEmpId+"_GROSS_AMT"))),Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				reportListExport.add(alInnerExport);
			%>
		</tr>
		
		<%dblGrossNet += dblIncomeFromOther + uF.parseToDouble(hmPrevOrgTDSDetails.get(strEmpId+"_GROSS_AMT")); %>
		<tr>
			<td class="alignLeft" colspan="4"><strong>8. Gross Total Income (6+7)</strong></td>
			<td class="alignRight"><%=uF.showData(strCurrSymbol,"")+Math.round(dblGrossNet) %></td>
			<%
				alInnerExport = new ArrayList<DataStyle>();
				alInnerExport.add(new DataStyle("8. Gross Total Income (6+7)",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(""+Math.round(dblGrossNet),Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				reportListExport.add(alInnerExport);
			%>
		</tr>
		
		<tr>
			<td class="alignLeft" colspan="5"><strong>9. Less: Deductions under Chapter VI-A</strong></td>
			<%
				alInnerExport = new ArrayList<DataStyle>();
				alInnerExport.add(new DataStyle("9. Less: Deductions under Chapter VI-A",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				
				reportListExport.add(alInnerExport);
			%>
		</tr>
		
		<%
		double dblChapterVIA=0.0d;
		double dblInvestmentLimit=0.0d;
		List<String> alUnderSection=new ArrayList<String>();
		Map<String,String> hmInvest=hmEmpInvestment.get(strEmpId);
		if(hmInvest==null) hmInvest = new HashMap<String, String>();
			int ii=0;
			for(int a=0;chapter1SectionList!=null && a<chapter1SectionList.size();a++) {
				String strSectionId = chapter1SectionList.get(a);
				double strAmt = 0.0d;
				String strSectionAmt = uF.showData(hmInvest.get(strSectionId), "");
				Map<String, String> hmSubSecAdjusted10PerLimitAmt = hmSectionwiseSubSecAdjusted10PerLimitAmt.get(strSectionId);
				if(hmSubSecAdjusted10PerLimitAmt==null) hmSubSecAdjusted10PerLimitAmt = new HashMap<String, String>();
				//System.out.println("hmSubSecAdjusted10PerLimitAmt ===>> " + hmSubSecAdjusted10PerLimitAmt);
				
		  		Map<String, List<Map<String, String>>> hmSubInvestment = (Map<String, List<Map<String, String>>>) hmEmpSubInvestment.get("" + strEmpId);
		  		if(hmSubInvestment==null) hmSubInvestment= new HashMap<String, List<Map<String, String>>>();
		  		List<Map<String, String>> subInvestList = (List<Map<String, String>>) hmSubInvestment.get("" +strSectionId);
		  		String strUnderSection="";
		  		
		  		if(hmSectionLimitA.containsKey(strSectionId)) {
					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId));
				} else {
					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId));
					double dblVIA1Invest = uF.parseToDouble(hmInvest.get(strSectionId));
					dblInvestmentLimit = dblVIA1Invest * dblInvestmentLimit / 100;
				}
		  		
		  		strAmt = uF.parseToDouble(strSectionAmt);
		  		//System.out.println(hmSectionMap.get(strSectionId) + " --- dblInvestmentLimit ===>> " + dblInvestmentLimit+" --- strSectionAmt ===>> " + strSectionAmt);
		  		if(dblInvestmentLimit>=0) {
		  			strAmt = Math.min(strAmt, dblInvestmentLimit);
		  		}
		  		//System.out.println(hmSectionMap.get(strSectionId) + " --- strAmt ===>> " + strAmt);
		  		//dblChapterVIA+=uF.parseToDouble(strSectionAmt);
			  	if(ii==0) { 
			%>
			<tr>  
				<td class="reportLabel" nowrap><strong>    A). Sections 80C, 80CCC and  80CCD</strong> </td>
				<td class="reportLabel alignRight">&nbsp;</td>
				<td class="reportLabel alignRight">&nbsp;</td>
				<%
				alInnerExport = new ArrayList<DataStyle>();
				alInnerExport.add(new DataStyle("A). Sections 80C, 80CCC and  80CCD",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				reportListExport.add(alInnerExport);
				%>
			</tr>
			<%	
				} 
		  		ii++;
		  		
		  		Map<String,String> hmAcutualInvest=hmEmpActualInvestment.get(strEmpId);
		        if(hmAcutualInvest==null) hmAcutualInvest= new HashMap<String, String>();
			%>
			<tr>  
				<td class="reportLabel" nowrap><strong>       <%=ii %>. <%=uF.showData(hmSectionMap.get(strSectionId), "")%></strong> </td>
				<td class="reportLabel alignRight"><%=(subInvestList==null ? ""+Math.round(uF.parseToDouble(hmAcutualInvest.get(strSectionId))) : "")%></td>
				<td class="reportLabel alignRight"><%=(subInvestList==null && !hmSectionMap.containsKey(strSectionId) ? ""+Math.round(strAmt) : "")%></td>
			<%
				alInnerExport = new ArrayList<DataStyle>();
				alInnerExport.add(new DataStyle(ii+". "+uF.showData(hmSectionMap.get(strSectionId), ""),Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle((subInvestList==null ? ""+Math.round(uF.parseToDouble(hmAcutualInvest.get(strSectionId))) : ""),Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle((subInvestList==null && !hmSectionMap.containsKey(strSectionId) ? ""+Math.round(strAmt) : ""),Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				reportListExport.add(alInnerExport);
				%>
			</tr>
			<%
			int k=0;
		    if(hmSectionMap.containsKey(strSectionId) && uF.parseToBoolean(hmSectionPFApplicable.get(strSectionId))) {
		    	k++;
		    	strAmt += uF.parseToDouble(""+hmTaxInner.get("dblEmpPF"));
		  		//dblChapterVIA += uF.parseToDouble(""+hmTaxInner.get("dblEmpPF"));
		  		if(dblInvestmentLimit>=0) {
		  			strAmt = Math.min(strAmt, dblInvestmentLimit);
		  		}
		  		//dblChapterVIA = Math.min(dblChapterVIA, dblInvestmentLimit);
		    	%>
		    	<tr>  
					<td class="reportLabel" nowrap><strong>         <%=k %>. PF</strong> </td>
					<td class="reportLabel alignRight"><%=""+Math.round(uF.parseToDouble(""+hmTaxInner.get("dblEmpPF")) ) %></td>
					<td class="reportLabel alignRight">&nbsp;</td>
					<%
					alInnerExport = new ArrayList<DataStyle>();
					alInnerExport.add(new DataStyle(k+". PF",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(""+Math.round(uF.parseToDouble(""+hmTaxInner.get("dblEmpPF")) ),Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
					reportListExport.add(alInnerExport);
					%>
				</tr>
		    	
			<%
		    }
		    	double totSubSecAmtOfSection = 0;
		    	
		    	//System.out.println("subInvestList ===>> " + subInvestList);
				for (int j = 0; subInvestList != null && j < subInvestList.size(); j++) {
					k++;
					Map<String, String> hm = (Map<String, String>) subInvestList.get(j);
					String strSubSecNo = hm.get("SUB_SEC_NO");
					//System.out.println("strSubSecNo ===>> " + strSubSecNo+ " -- hmSubSecAdjusted10PerLimitAmt.get(strSubSecNo) ===>> " + hmSubSecAdjusted10PerLimitAmt.get(strSubSecNo));
					double dblSubSec10PerAdjLimitAmt = uF.parseToDouble(hmSubSecAdjusted10PerLimitAmt.get(strSubSecNo));
					double dblPaidAmt = uF.parseToDouble(uF.showData(hm.get("PAID_AMOUNT"), ""));
					double dblSubSecAmt = uF.parseToDouble(uF.showData(hm.get("SUB_SECTION_AMOUNT"), ""));
					String strSubSecLimitType = uF.showData(hm.get("SUB_SECTION_LIMIT_TYPE"), "");
					if(dblSubSecAmt>0) {
						if(strSubSecLimitType != null && strSubSecLimitType.equals("%")) {
							dblSubSecAmt = (dblPaidAmt * dblSubSecAmt) / 100;
						} else {
							//dblSubSecAmt = dblPaidAmt;
							dblSubSecAmt = Math.min(dblPaidAmt, dblSubSecAmt);
						}
						if(dblSubSec10PerAdjLimitAmt>0) {
							dblSubSecAmt = Math.min(dblSubSecAmt, dblSubSec10PerAdjLimitAmt);
						} else if(dblSubSec10PerAdjLimitAmt<0) {
							dblSubSecAmt = 0;
						}
					} else {
						dblSubSecAmt = dblPaidAmt;
						dblSubSecAmt = Math.min(dblPaidAmt, dblSubSecAmt);
						if(dblSubSec10PerAdjLimitAmt>0) {
							dblSubSecAmt = Math.min(dblSubSecAmt, dblSubSec10PerAdjLimitAmt);
						} else if(dblSubSec10PerAdjLimitAmt<0) {
							dblSubSecAmt = 0;
						}
					}
					totSubSecAmtOfSection += dblSubSecAmt;
			%>
			<tr>
				<td class="reportLabel" nowrap><%=(k) %>. <%=uF.showData(hm.get("SECTION_NAME"), "")%></td>
				<td class="reportLabel alignRight"><%=""+Math.round(dblPaidAmt)%></td>
				<td class="reportLabel alignRight">&nbsp;</td>
					<%
					alInnerExport = new ArrayList<DataStyle>();
					alInnerExport.add(new DataStyle(k+". "+uF.showData(hm.get("SECTION_NAME"), ""),Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(""+Math.round(dblPaidAmt),Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
					reportListExport.add(alInnerExport);
					%>
			</tr>
			<%
				}
				if(subInvestList!=null || hmSectionMap.containsKey(strSectionId)) {
					if(totSubSecAmtOfSection>0 && strAmt>totSubSecAmtOfSection) {
		  				strAmt = totSubSecAmtOfSection;
		  				if(hmSectionMap.containsKey(strSectionId) && uF.parseToBoolean(hmSectionPFApplicable.get(strSectionId))) {
		  					strAmt += uF.parseToDouble(""+hmTaxInner.get("dblEmpPF"));
		  					//System.out.println(hmSectionMap.get(strSectionId)+" strAmt 5-> "+strAmt);
		  				}
		  				if(dblInvestmentLimit>=0) {
		  					//System.out.println(" strAmt 6-> "+strAmt+" dblInvestmentLimit "+dblInvestmentLimit);
		  					strAmt = Math.min(strAmt, dblInvestmentLimit);
		  					//System.out.println(hmSectionMap.get(strSectionId)+" strAmt 6-> "+strAmt);
		  				}
						dblChapterVIA += strAmt;
						//System.out.println(" strAmt 4-> "+strAmt);
		  			} else {
			  			dblChapterVIA += strAmt;
			  			//System.out.println(" strAmt 3--> "+strAmt);
			  		}
			%>
			<tr>
				<td class="reportLabel" nowrap>&nbsp;</td>
				<td class="reportLabel alignRight">&nbsp;</td>
				<td class="reportLabel alignRight"><%=Math.round(strAmt) %></td>
					<%
					alInnerExport = new ArrayList<DataStyle>();
					alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(Math.round(strAmt)+"",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
					reportListExport.add(alInnerExport);
					%>
			</tr>
			<%} else {
				dblChapterVIA += strAmt;
				
			}
				//System.out.println(" strAmt 2--> "+strAmt);
			   	}
			   %>
	   
		<%
		Map<String,String> hmInvest1=hmEmpInvestment1.get(strEmpId);
		if(hmInvest1==null) hmInvest1 = new HashMap<String, String>();
			ii=0;
			//System.out.println("dblInvestmentLimit before =====>> " + dblInvestmentLimit);
			for(int a=0;chapter2SectionList!=null && a<chapter2SectionList.size();a++){
				String strSectionId = chapter2SectionList.get(a);
				Map<String, String> hmSubSecAdjusted10PerLimitAmt = hmSectionwiseSubSecAdjusted10PerLimitAmt.get(strSectionId);
				if(hmSubSecAdjusted10PerLimitAmt==null) hmSubSecAdjusted10PerLimitAmt = new HashMap<String, String>();
				
				if(hmSectionLimitA.containsKey(strSectionId)) {
					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId));
				} else {
					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId));
					double dblVIA1Invest = uF.parseToDouble(hmInvest1.get(strSectionId));
					//System.out.println("dblVIA1Invest =====>> " + dblVIA1Invest +" -- dblInvestmentLimit =====>> " + dblInvestmentLimit);
					dblInvestmentLimit = dblVIA1Invest * dblInvestmentLimit / 100;
				}
				String strAmt = uF.showData(hmInvest1.get(strSectionId), "");
				//System.out.println(hmSectionMap.get(strSectionId) +" -- strAmt =====>> " + strAmt);
				//System.out.println("dblInvestmentLimit =====>> " + dblInvestmentLimit);
				if(dblInvestmentLimit>=0) {
					strAmt = ""+Math.min(uF.parseToDouble(strAmt), dblInvestmentLimit);
				}
		  		//dblChapterVIA += uF.parseToDouble(strAmt);
		  		
		  		Map<String, List<Map<String, String>>> hmSubInvestment = (Map<String, List<Map<String, String>>>) hmEmpSubInvestment.get("" + strEmpId);
		  		if(hmSubInvestment==null) hmSubInvestment= new HashMap<String, List<Map<String, String>>>();
		  		List<Map<String, String>> subInvestList = (List<Map<String, String>>) hmSubInvestment.get("" +strSectionId);
		  		String strUnderSection="";
		  		
		  		if(ii==0) {
		  			%>
		  			<tr>  
		  				<td class="reportLabel" nowrap><strong>B). Other sections (eg. 80E, 80G, 80TTA, etc.) under VI-A</strong> </td>
		  				<td class="reportLabel alignRight">&nbsp;</td>
		  				<td class="reportLabel alignRight">&nbsp;</td>
		  				<%
						alInnerExport = new ArrayList<DataStyle>();
						alInnerExport.add(new DataStyle("B). Other sections (eg. 80E, 80G, 80TTA, etc.) under VI-A",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
						alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
						alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
						reportListExport.add(alInnerExport);
						%>
		  			</tr>
		  			<%	} 
		  			  	ii++;
		  			  	Map<String,String> hmAcutualInvest=hmEmpActualInvestment1.get(strEmpId);
				        if(hmAcutualInvest==null) hmAcutualInvest= new HashMap<String, String>();
		  			%>
				<tr>  
					<td class="reportLabel" nowrap><strong><%=ii %>. <%=uF.showData(hmSectionMap.get(strSectionId), "")%></strong> </td>
					<td class="reportLabel alignRight"><%=(subInvestList==null ? ""+Math.round(uF.parseToDouble(hmAcutualInvest.get(strSectionId))) : "") %></td>
					<td class="reportLabel alignRight"><%=(subInvestList==null ? ""+Math.round(uF.parseToDouble(strAmt)) : "") %></td>
						<%
						alInnerExport = new ArrayList<DataStyle>();
						alInnerExport.add(new DataStyle(ii+". "+uF.showData(hmSectionMap.get(strSectionId), ""),Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
						alInnerExport.add(new DataStyle((subInvestList==null ? ""+Math.round(uF.parseToDouble(hmAcutualInvest.get(strSectionId))) : ""),Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
						alInnerExport.add(new DataStyle((subInvestList==null ? ""+Math.round(uF.parseToDouble(strAmt)) : ""),Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
						reportListExport.add(alInnerExport);
						%>
				</tr>
				<%
				
				double totSubSecAmtOfSection = 0;
				//System.out.println("subInvestList ===>> " + subInvestList);
				for (int j = 0; subInvestList != null && j < subInvestList.size(); j++) {
					Map<String, String> hm = (Map<String, String>) subInvestList.get(j);
					String strSubSecNo = hm.get("SUB_SEC_NO");
					double dblSubSec10PerAdjLimitAmt = uF.parseToDouble(hmSubSecAdjusted10PerLimitAmt.get(strSubSecNo));
					double dblPaidAmt = uF.parseToDouble(uF.showData(hm.get("PAID_AMOUNT"), ""));
					double dblSubSecAmt = uF.parseToDouble(uF.showData(hm.get("SUB_SECTION_AMOUNT"), ""));
					String strSubSecLimitType = uF.showData(hm.get("SUB_SECTION_LIMIT_TYPE"), "");
					if(dblSubSecAmt>0) {
						if(strSubSecLimitType != null && strSubSecLimitType.equals("%")) {
							dblSubSecAmt = (dblPaidAmt * dblSubSecAmt) / 100;
						} else {
							//dblSubSecAmt = dblPaidAmt;
							dblSubSecAmt = Math.min(dblPaidAmt, dblSubSecAmt);
						}
						if(dblSubSec10PerAdjLimitAmt>0) {
							dblSubSecAmt = Math.min(dblSubSecAmt, dblSubSec10PerAdjLimitAmt);
						} else if(dblSubSec10PerAdjLimitAmt<0) {
							dblSubSecAmt = 0;
						}
					} else {
						//dblSubSecAmt = dblPaidAmt;
						dblSubSecAmt = Math.min(dblPaidAmt, dblSubSecAmt);
						if(dblSubSec10PerAdjLimitAmt>0) {
							dblSubSecAmt = Math.min(dblSubSecAmt, dblSubSec10PerAdjLimitAmt);
						} else if(dblSubSec10PerAdjLimitAmt<0) {
							dblSubSecAmt = 0;
						}
					}
					totSubSecAmtOfSection += dblSubSecAmt;
					//System.out.println("totSubSecAmtOfSection ===>> " + totSubSecAmtOfSection);
				%>
				<tr>
					<td class="reportLabel" nowrap><%=(j+1) %> <%=uF.showData(hm.get("SECTION_NAME"), "")%></td>
					<td class="reportLabel alignRight"><%=""+Math.round(dblPaidAmt) %></td>
					<td class="reportLabel alignRight">&nbsp;</td>
						<%
						alInnerExport = new ArrayList<DataStyle>();
						alInnerExport.add(new DataStyle((j+1)+" "+uF.showData(hm.get("SECTION_NAME"), ""),Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
						alInnerExport.add(new DataStyle(""+Math.round(dblPaidAmt),Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
						alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
						reportListExport.add(alInnerExport);
						%>
				</tr>
				<%
					}
					if(subInvestList!=null) {
						if(totSubSecAmtOfSection>0 && uF.parseToDouble(strAmt)>totSubSecAmtOfSection) {
			  				strAmt = ""+totSubSecAmtOfSection;
			  				if(dblInvestmentLimit>=0) {
			  					strAmt = ""+Math.min(uF.parseToDouble(strAmt), dblInvestmentLimit);
			  				}
			  			}
			  			dblChapterVIA+=uF.parseToDouble(strAmt);
				%>
				<tr>
					<td class="reportLabel" nowrap>&nbsp;</td>
					<td class="reportLabel alignRight">&nbsp;</td>
					<td class="reportLabel alignRight"><%=Math.round(uF.parseToDouble(strAmt))%></td>
					<%
						alInnerExport = new ArrayList<DataStyle>();
						alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
						alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
						alInnerExport.add(new DataStyle(Math.round(uF.parseToDouble(strAmt))+"",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
						reportListExport.add(alInnerExport);
						%>
				</tr>
			<%} else {
				dblChapterVIA+=uF.parseToDouble(strAmt);
				
			}
			   	}
			   %>
		<tr>
			<td class="alignLeft" colspan="4"><strong>10. Aggregate of deductible Amount under Chapter VI-A</strong></td>
			<td class="alignRight"><strong><%=uF.showData(strCurrSymbol,"")+Math.round(dblChapterVIA) %></strong></td>
			<%
				alInnerExport = new ArrayList<DataStyle>();
				alInnerExport.add(new DataStyle("10. Aggregate of deductible Amount under Chapter VI-A",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(""+Math.round(dblChapterVIA),Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				reportListExport.add(alInnerExport);
			%>
		</tr>
		
		<%
			//System.out.println(" 11. Total Income dblChapterVIA --> "+dblChapterVIA);
			//System.out.println(" round dblChapterVIA --> "+Math.round(dblChapterVIA));
			//System.out.println(" dblGrossNet --> "+dblGrossNet);
			dblGrossNet -= Math.round(dblChapterVIA); 
		%>
		<tr>
			<td class="alignLeft" colspan="4"><strong>11. Total Income (8-10) [Round off u/s 288B]</strong></td>
			<td class="alignRight"><strong><%=uF.showData(strCurrSymbol,"")+Math.round(dblGrossNet) %></strong></td>
			<%
				alInnerExport = new ArrayList<DataStyle>();
				alInnerExport.add(new DataStyle("11. Total Income (8-10) [Round off u/s 288B]",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(""+Math.round(dblGrossNet),Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				reportListExport.add(alInnerExport);
			%>
		</tr>
		
		<tr>
			<td colspan="3"><hr style="color:#ccc"/></td>
			<%
				alInnerExport = new ArrayList<DataStyle>();
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				reportListExport.add(alInnerExport);
			%>
		</tr>
	
		<%double dblTaxLiability = uF.parseToDouble((String)hmTaxInner.get("TAX_LIABILITY")); %>
		<tr>
			<td class="alignLeft" colspan="4"><strong>12. Tax on Total Income</strong></td>
			<td class="alignRight"><%=uF.showData(strCurrSymbol,"")+Math.round(dblTaxLiability) %></td>
			<%
				alInnerExport = new ArrayList<DataStyle>();
				alInnerExport.add(new DataStyle("12. Tax on Total Income",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(""+Math.round(dblTaxLiability),Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				reportListExport.add(alInnerExport);
			%>
		</tr>
		
		<%double dblRebate = uF.parseToDouble((String)hmTaxInner.get("TAX_REBATE")); %>
		<tr>
			<td class="alignLeft" colspan="4"><strong>13. a) less Tax Rebate u/s 87 A</strong></td>
			<td class="alignRight"><%=uF.showData(strCurrSymbol,"")+Math.round(dblRebate) %></td>
			<%
				alInnerExport = new ArrayList<DataStyle>();
				alInnerExport.add(new DataStyle("13. a) less Tax Rebate u/s 87 A",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(""+Math.round(dblRebate),Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				reportListExport.add(alInnerExport);
			%>
		</tr>
		<tr>
			<td class="alignLeft" colspan="4"><strong>&nbsp;&nbsp;&nbsp;&nbsp; b) less Tax paid in previous Organization</strong></td>
			<td class="alignRight"><%=uF.showData(strCurrSymbol,"")+Math.round(uF.parseToDouble(hmPrevOrgTDSDetails.get(strEmpId+"_TDS_AMT"))) %></td>
			<%
				alInnerExport = new ArrayList<DataStyle>();
				alInnerExport.add(new DataStyle("b) less Tax paid in previous Organization",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(""+Math.round(uF.parseToDouble(hmPrevOrgTDSDetails.get(strEmpId+"_TDS_AMT"))),Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				reportListExport.add(alInnerExport);
			%>
		</tr>
		
		<%double dblCess1 = uF.parseToDouble((String)hmTaxInner.get("CESS1"));
		double dblCess1Amount = uF.parseToDouble((String)hmTaxInner.get("CESS1_AMOUNT")); %>
		<tr>
			<td class="alignLeft" colspan="4"><strong>14. Add: Ed. Cess @ <%=dblCess1%>%</strong></td>
			<td class="alignRight"><%=uF.showData(strCurrSymbol,"")+Math.round(dblCess1Amount)%></td>
			<%
				alInnerExport = new ArrayList<DataStyle>();
				alInnerExport.add(new DataStyle("14. Add: Ed. Cess @ "+dblCess1+"%",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(""+Math.round(dblCess1Amount),Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				reportListExport.add(alInnerExport);
			%>
		</tr>
		
		<%double dblCess2 = uF.parseToDouble((String)hmTaxInner.get("CESS2")) ;
		double dblCess2Amount = uF.parseToDouble((String)hmTaxInner.get("CESS2_AMOUNT")); %>
		<tr>
			<td class="alignLeft" colspan="4"><strong>15. Add: She Cess @ <%=dblCess2%>%</strong></td>
			<td class="alignRight"><%=uF.showData(strCurrSymbol,"")+Math.round(dblCess2Amount) %></td>
			<%
				alInnerExport = new ArrayList<DataStyle>();
				alInnerExport.add(new DataStyle("14. Add: Ed. Cess @ "+dblCess2+"%",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(""+Math.round(dblCess2Amount),Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				reportListExport.add(alInnerExport);
			%>
		</tr>
		<%double dblTotalTaxLiability = uF.parseToDouble((String)hmTaxInner.get("TOTAL_TAX_LIABILITY")); %>
		<tr>
			<td class="alignLeft" colspan="4"><strong>16. Tax Payable ((12-13)+14+15)</strong></td>
			<td class="alignRight"><strong><%=uF.showData(strCurrSymbol,"")+Math.round(dblTotalTaxLiability) %></strong></td>
			<%
				alInnerExport = new ArrayList<DataStyle>();
				alInnerExport.add(new DataStyle("16. Tax Payable ((12-13)+14+15)",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(""+Math.round(dblTotalTaxLiability),Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				reportListExport.add(alInnerExport);
			%>
		</tr>
		
		<tr>
			<td class="alignLeft" colspan="4"><strong>17. Less: relief under section 89 (attach form 10E)</strong></td>
			<td class="alignRight"><strong>0</strong></td>
			<%
				alInnerExport = new ArrayList<DataStyle>();
				alInnerExport.add(new DataStyle("17. Less: relief under section 89 (attach form 10E)",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("0",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				reportListExport.add(alInnerExport);
			%>
		</tr>
		
		<tr>
			<td class="alignLeft" colspan="4"><strong>18. Tax Payable (16+17)  [Round off u/s 288B]</strong></td>
			<td class="alignRight"><strong><%=""+Math.round(dblTotalTaxLiability) %></strong></td>
			<%
				alInnerExport = new ArrayList<DataStyle>();
				alInnerExport.add(new DataStyle("18. Tax Payable (16+17)  [Round off u/s 288B]",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(""+Math.round(dblTotalTaxLiability),Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				reportListExport.add(alInnerExport);
			%>
		</tr>
	
		<tr>
			<td class="alignLeft" colspan="4"><strong>19. Tax deducted at source u/s 192 (1)</strong></td>
			<td class="alignRight"><strong><%=uF.showData(strCurrSymbol,"") + Math.round(uF.parseToDouble((String)hmTaxInner.get("TOTAL_TDS_PAID"))) %></strong></td>
			<%
				alInnerExport = new ArrayList<DataStyle>();
				alInnerExport.add(new DataStyle("19. Tax deducted at source u/s 192 (1)",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("" + Math.round(uF.parseToDouble((String)hmTaxInner.get("TOTAL_TDS_PAID"))),Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				reportListExport.add(alInnerExport);
			%>
		</tr>
		
		<%double dblRemainTds = Math.round(Math.round(dblTotalTaxLiability) - Math.round(uF.parseToDouble((String)hmTaxInner.get("TOTAL_TDS_PAID")))); %>
		<%-- <%System.out.println("VETDSP.jsp/597---dblRemainTds="+dblRemainTds+"---TOTAL_TDS_PAID="+hmTaxInner.get("TOTAL_TDS_PAID")+"---dblTotalTaxLiability="+dblTotalTaxLiability); %> --%>
		<tr>
			<td class="alignLeft" colspan="4"><strong>20. Tax Payable (18-19)</strong></td>
			<td class="alignRight"><strong><%=uF.showData(strCurrSymbol,"") + Math.round(dblRemainTds) %></strong></td>
			<%
				alInnerExport = new ArrayList<DataStyle>();
				alInnerExport.add(new DataStyle("20. Tax Payable (18-19)",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("" + Math.round(dblRemainTds),Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				reportListExport.add(alInnerExport);
			%>
		</tr>
		
		<%if(hmTDSRemainMonth != null && !hmTDSRemainMonth.isEmpty() && hmTDSRemainMonth.size() > 0){ %>
			<tr>
				<td class="alignLeft" colspan="5">&nbsp;</td>
			<%
				alInnerExport = new ArrayList<DataStyle>();
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				reportListExport.add(alInnerExport);
			%>
			</tr>
			<tr>
				<td class="alignLeft" colspan="5">&nbsp;</td>
				<%
				alInnerExport = new ArrayList<DataStyle>();
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
				reportListExport.add(alInnerExport);
			%>
			</tr>
			
			<tr>
				<td class="alignLeft" colspan="5"><strong>Tax To be Deduct</strong></td>
				<%
					alInnerExport = new ArrayList<DataStyle>();
					alInnerExport.add(new DataStyle("Tax To be Deduct",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
					reportListExport.add(alInnerExport);
				%>
			</tr>
			<%
				Iterator<String> it = hmTDSRemainMonth.keySet().iterator();
				int cnt = 0;
				double dblTotalRemainTDS = 0.0d;
				while(it.hasNext()){
					String strMonth = it.next();
					String strRemainTDS = hmTDSRemainMonth.get(strMonth);
					cnt++;
					dblTotalRemainTDS += uF.parseToDouble(strRemainTDS); 
			%>
				<tr>
					<td class="alignRight" colspan="3"><strong><%=strMonth %></strong></td>
					<td class="alignRight"><strong><%=uF.showData(strCurrSymbol,"")+Math.round(uF.parseToDouble(strRemainTDS)) %></strong></td>
					<td class="alignRight">&nbsp;</td>
					<%
					alInnerExport = new ArrayList<DataStyle>();
					alInnerExport.add(new DataStyle(strMonth,Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(""+Math.round(uF.parseToDouble(strRemainTDS)),Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
					reportListExport.add(alInnerExport);
				%>
				</tr>
			<%
				}
				if(cnt > 0){
			%>
				<tr>
					<td class="alignRight" colspan="4">&nbsp;</td>
					<td class="alignRight"><strong><%=uF.showData(strCurrSymbol,"")+Math.round(dblTotalRemainTDS) %></strong></td>
					<%
					alInnerExport = new ArrayList<DataStyle>();
					alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(""+Math.round(dblTotalRemainTDS),Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
					reportListExport.add(alInnerExport);
					%>
				</tr>
			<%	} %>
		<%} %>
	</table>
		
<%} else { %>
	<div class="msg nodata"><span>No Data Found.</span></div>
<%} %>
<% session.setAttribute("reportListExport", reportListExport); %>
