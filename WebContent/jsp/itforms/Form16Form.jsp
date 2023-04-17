<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>


<%

CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
UtilityFunctions uF = new UtilityFunctions();
String strEmpId = request.getParameter("strEmpId");
String slabType = request.getParameter("slabType");
String strFinancialYearStart = request.getParameter("strFinancialYearStart");
String strFinancialYearEnd = request.getParameter("strFinancialYearEnd");
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

List<List<String>> chapter1SectionList = (List<List<String>>) request.getAttribute("chapter1SectionList");
if(chapter1SectionList==null)chapter1SectionList = new ArrayList<List<String>>();
List<List<String>> chapter2SectionList = (List<List<String>>) request.getAttribute("chapter2SectionList");
if(chapter2SectionList==null) chapter2SectionList = new ArrayList<List<String>>();

Map<String, String> hmSectionPFApplicable = (Map<String, String>) request.getAttribute("hmSectionPFApplicable");
if(hmSectionPFApplicable == null) hmSectionPFApplicable = new HashMap<String, String>();

Map<String, Map<String, String>> hmEmpActualInvestment = (Map<String, Map<String, String>>) request.getAttribute("hmEmpActualInvestment");
if(hmEmpActualInvestment==null) hmEmpActualInvestment = new HashMap<String, Map<String, String>>();

Map<String, Map<String, String>> hmEmpActualInvestment1 = (Map<String, Map<String, String>>) request.getAttribute("hmEmpActualInvestment1");
if(hmEmpActualInvestment1==null) hmEmpActualInvestment1 = new HashMap<String, Map<String, String>>();
 
double dblGross = uF.parseToDouble((String)hmEmpPayrollDetails.get("GROSS") );

//System.out.println("slabType ===>>>> " + slabType);

%>



<table width="600px">
	<%-- <tr>
		<td class="reportLabel">Total salary for year <%=strFinancialYearStart%>-<%=strFinancialYearEnd%></td>
		<td class="reportLabel">&nbsp;</td>
		<td class="reportLabel alignRight"><%=""+Math.round(dblGross) %></td>
	</tr> --%>
	<tr>
		<td class="reportLabel">Details of salary paid and and other tax deducted for year <%=strFinancialYearStart%>-<%=strFinancialYearEnd%></td>
		<td class="reportLabel">&nbsp;</td>
		<td class="reportLabel alignRight">&nbsp;</td>
	</tr>
	<tr>
		<td class="reportLabel"><strong>1. Gross Salary</strong></td>
		<td class="reportLabel">&nbsp;</td>
		<td class="reportLabel alignRight"><%=""+Math.round(dblGross) %></td>
	</tr>
	
	<tr>
		<td class="reportLabel">  a) Salary as per provisions contained in sc 17 (1)</td>
		<td class="reportLabel alignRight">&nbsp;</td>
		<td class="reportLabel alignRight">0</td>
	</tr>
	
	<tr>
		<td class="reportLabel">  b) Value of perquisites u/s 17(2) (as per Form No. 12BA, wherever applicable)</td>
		<td class="reportLabel alignRight">&nbsp;</td>
		<td class="reportLabel alignRight">0</td>
	</tr>
	
	<tr>
		<td class="reportLabel">  c) Profits in Lieu of salary under 17(3) (as per Form No. 12BA, wherever applicable)</td>
		<td class="reportLabel alignRight">&nbsp;</td>
		<td class="reportLabel alignRight">0</td>
	</tr>
	
	<tr>
		<td class="reportLabel"><strong>Total</strong></td>
		<td class="reportLabel alignRight">&nbsp;</td>
		<td class="reportLabel alignRight"><%=""+Math.round(dblGross) %></td>
	</tr>

	<tr>
		<td colspan="3"><hr style="color:#ccc"/></td>
	</tr>

	
	<tr>
		<td class="reportLabel"><strong>2. Less: allowance to the extent exempt u/s 10</strong></td>
		<td colspan="2" class="reportLabel">&nbsp;</td>
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
		if(uF.parseToInt(innerList.get(8)) == uF.parseToInt(slabType) || uF.parseToInt(innerList.get(8)) == 2) {
			double dblAmtPaid = uF.parseToDouble(hmUS10_16InnerPaid.get(innerList.get(6)));
			double dblAmtExempt = uF.parseToDouble(hmUS10_16Inner.get(innerList.get(6)));
			if(uF.parseToInt(innerList.get(6)) == IConstants.HRA || uF.parseToInt(innerList.get(6)) == IConstants.CONVEYANCE_ALLOWANCE || uF.parseToInt(innerList.get(6)) == IConstants.EDUCATION_ALLOWANCE) {
				dblAmtPaid = uF.parseToDouble(hmUS10_16_SalHeadData.get(innerList.get(6)+"_PAID"));
				dblAmtExempt = uF.parseToDouble(hmUS10_16_SalHeadData.get(innerList.get(6)+"_EXEMPT"));
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
			</tr>
	
	<% exempCnt++; } } %>
	
	<tr>
		<td colspan="3"><hr style="color:#ccc"/></td>
	</tr>
	
	<% double dblGrossNet = Math.round(dblGross) - Math.round(dblTotUS10ExemptAmt); 
		if(dblGrossNet<0) {
	    	dblGrossNet=0;
	    }
	%>
	
	<tr>
		<td class="reportLabel"><strong>3. Balance (1-2)</strong></td>
		<td class="reportLabel alignRight"></td>
		<td class="reportLabel alignRight"><%=""+Math.round(dblGrossNet) %></td>
	</tr>
	
	<tr>
		<td colspan="3"><hr style="color:#ccc"/></td>
	</tr>
	
	<tr>
		<td class="reportLabel"><strong>4. Less: allowance to the extent exempt u/s 16</strong></td>
		<td colspan="2" class="reportLabel">&nbsp;</td>
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
		if(uF.parseToInt(innerList.get(8)) == uF.parseToInt(slabType) || uF.parseToInt(innerList.get(8)) == 2) {
			double dblAmtPaid = uF.parseToDouble(hmUS10_16InnerPaid.get(innerList.get(6)));
			double dblAmtExempt = uF.parseToDouble(hmUS10_16Inner.get(innerList.get(6)));
	
			if(uF.parseToInt(innerList.get(6)) == 0) {
				dblAmtExempt = uF.parseToDouble(innerList.get(3));
			}
			dblTotUS16ExemptAmt += dblAmtExempt;
		%>
			<tr>
				<td class="reportLabel"><%="  "+exempSerialsUS16[exempCntUS16]+") "+innerList.get(1) %></td>
				<td class="reportLabel alignRight"><%=Math.round(dblAmtExempt)%></td>
				<td class="reportLabel alignRight"><%="" %></td>
			</tr>
	<% exempCntUS16++; } } %>
	
	<tr>
		<td class="reportLabel"><strong>5. Aggregate of 4(a) and (b)</strong></td>
		<td class="reportLabel alignRight">&nbsp;</td>
		<td class="reportLabel alignRight"><%=(Math.round(dblTotUS16ExemptAmt)) %></td> 
	</tr>
	
	<tr>
		<td colspan="3"><hr style="color:#ccc"/></td>
	</tr>
	<%-- <%double dblHomeLoanTaxExempt = uF.parseToDouble((String)hmTaxInner.get("dblHomeLoanTaxExempt")); %>
	<tr>
		<td class="reportLabel"><strong>6. Home Loan Interest</strong></td>
		<td class="reportLabel alignRight"></td>
		<td class="reportLabel alignRight"><%=""+Math.round(dblHomeLoanTaxExempt)%></td>
	</tr> --%>
	
	<%
	//dblGrossNet -=  (Math.round(dblProfessionalTaxExempt)+Math.round(dblEducationAllowanceExempt)); 
	 dblGrossNet -=  (Math.round(dblTotUS16ExemptAmt));
	 if(dblGrossNet<0) {
     	dblGrossNet=0;
     }
	//dblGrossNet -=  Math.round(dblHomeLoanTaxExempt);
	%>
	
	<tr>
		<!-- <td class="reportLabel"><strong>7. Income Chargeable under the head 'salaries ((3-5)-6)</strong></td> -->
		<td class="reportLabel"><strong>6. Income Chargeable under the head 'salaries (3-5)</strong></td>
		<td class="reportLabel alignRight"></td>
		<td class="reportLabel alignRight"><%=""+Math.round(dblGrossNet) %></td>
	</tr>
	
	<tr>
		<td colspan="3"><hr style="color:#ccc"/></td>
	</tr>
	<%double dblIncomeFromOther=uF.parseToDouble((String)hmTaxInner.get("dblIncomeFromOther")); %>
	<tr>
		<td class="reportLabel"><strong>7.a). + Any other income reported by the employee</strong></td>
		<td class="reportLabel alignRight"></td>
		<td class="reportLabel alignRight"><%=""+Math.round(dblIncomeFromOther) %></td>
	</tr>
	
<!-- ===start parvez date: 01-04-2022=== -->	
	<%double dblPrevOrgGross=uF.parseToDouble((String)hmTaxInner.get("dblPrevOrgGross")); %>
	<tr>
		<td class="reportLabel"><strong>&nbsp;&nbsp;b). Income from previous Organization by the employee</strong></td>
		<td class="reportLabel alignRight"></td>
		<td class="reportLabel alignRight"><%=""+Math.round(dblPrevOrgGross) %></td>
	</tr>
<!-- ===end parvez date: 01-04-2022=== -->	
	
	<%	//dblGrossNet += Math.round(dblIncomeFromOther);
		dblGrossNet += Math.round(dblIncomeFromOther)+Math.round(dblPrevOrgGross); 
		if(dblGrossNet<0) {
	    	dblGrossNet=0;
	    }
	%>
	
	<tr>
		<td class="reportLabel"><strong>8. Gross Total Income (6+7)</strong></td>
		<td class="reportLabel alignRight"></td>
		<td class="reportLabel alignRight"><%=""+Math.round(dblGrossNet) %></td>
	</tr>
	
	<tr>
		<td colspan="3"><hr style="color:#ccc"/></td>
	</tr>
		
	<tr>
		<td class="reportLabel" nowrap><strong>9. Less: Deductions under Chapter VI-A</strong></td>
		<td class="reportLabel alignRight">&nbsp;</td>
		<td class="reportLabel alignRight">&nbsp;</td>
	</tr>
	 
<%
//double dblInvestment = uF.parseToDouble((String)hmTaxInner.get("dblInvestment"));

	double dblChapterVIA = 0.0d;
	double dblInvestmentLimit = 0.0d;
	List<String> alUnderSection = new ArrayList<String>();
	Map<String,String> hmInvest = hmEmpInvestment.get(strEmpId);
	if(hmInvest==null) hmInvest = new HashMap<String, String>();
	int ii=0;
	for(int a=0; chapter1SectionList!=null && a<chapter1SectionList.size(); a++) {
		List<String> innnList = chapter1SectionList.get(a);
		String strSectionId = innnList.get(0);
		String strSlabType = innnList.get(1);
		if(uF.parseToInt(slabType)!= uF.parseToInt(strSlabType) && uF.parseToInt(strSlabType)!=2) {
			continue;
		}
		double strAmt = 0.0d;
		String strSectionAmt = uF.showData(hmInvest.get(strSectionId), "");
		Map<String, String> hmSubSecAdjusted10PerLimitAmt = hmSectionwiseSubSecAdjusted10PerLimitAmt.get(strSectionId);
		if(hmSubSecAdjusted10PerLimitAmt==null) hmSubSecAdjusted10PerLimitAmt = new HashMap<String, String>();
		
  		Map<String, List<Map<String, String>>> hmSubInvestment = (Map<String, List<Map<String, String>>>) hmEmpSubInvestment.get("" + strEmpId);
  		if(hmSubInvestment==null) hmSubInvestment= new HashMap<String, List<Map<String, String>>>();
  		List<Map<String, String>> subInvestList = (List<Map<String, String>>) hmSubInvestment.get("" +strSectionId);
  		String strUnderSection="";
  		
  		if(hmSectionLimitA.containsKey(strSectionId+"_"+slabType)) {
			dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId+"_"+slabType));
		} else {
			dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId+"_"+slabType));
			double dblVIA1Invest = uF.parseToDouble(hmInvest.get(strSectionId));
			dblInvestmentLimit = dblVIA1Invest * dblInvestmentLimit / 100;
		}
  		
  		strAmt = uF.parseToDouble(strSectionAmt);
  		if(dblInvestmentLimit>0) {
  			strAmt = Math.min(strAmt, dblInvestmentLimit);
  		}
  		//dblChapterVIA+=uF.parseToDouble(strSectionAmt);
	  	if(ii==0) { 
	%>
	<tr>  
		<td class="reportLabel" nowrap><strong>    A). Sections 80C, 80CCC and  80CCD</strong> </td>
		<td class="reportLabel alignRight">&nbsp;</td>
		<td class="reportLabel alignRight">&nbsp;</td>
	</tr>
	<%	
		} 
  		ii++;
  		
  		Map<String,String> hmAcutualInvest = hmEmpActualInvestment.get(strEmpId);
        if(hmAcutualInvest==null) hmAcutualInvest = new HashMap<String, String>();
	%>
	<tr>  
		<td class="reportLabel" nowrap><strong>       <%=ii %>. <%=uF.showData(hmSectionMap.get(strSectionId), "")%></strong> </td>
		<td class="reportLabel alignRight"><%=(subInvestList==null ? ""+Math.round(uF.parseToDouble(hmAcutualInvest.get(strSectionId))) : "")%></td>
		<%-- <td class="reportLabel alignRight"><%=(subInvestList==null && !hmSectionMap.get(strSectionId).equals("80C and 80CCC") ? ""+Math.round(uF.parseToDouble(strAmt)) : "")%></td> --%>
		<td class="reportLabel alignRight"><%=(subInvestList==null && !hmSectionMap.containsKey(strSectionId) ? ""+Math.round(strAmt) : "")%></td>
	</tr>
	<%
	int k=0;
    //if(hmSectionMap.get(strSectionId).equals("80C and 80CCC")){
    if(hmSectionMap.containsKey(strSectionId) && uF.parseToBoolean(hmSectionPFApplicable.get(strSectionId+"_"+slabType))){
    	k++;
    	strAmt += uF.parseToDouble(""+hmTaxInner.get("dblEmpPF"));
  		//dblChapterVIA += uF.parseToDouble(""+hmTaxInner.get("dblEmpPF"));
  		if(dblInvestmentLimit>0) {
  			strAmt = Math.min(strAmt, dblInvestmentLimit);
  		}
  		//dblChapterVIA = Math.min(dblChapterVIA, dblInvestmentLimit);
    	%>
    	<tr>  
		<td class="reportLabel" nowrap><strong>         <%=k %>. PF</strong> </td>
		<td class="reportLabel alignRight"><%=""+Math.round(uF.parseToDouble(""+hmTaxInner.get("dblEmpPF")) ) %></td>
		<td class="reportLabel alignRight">&nbsp;</td>
	</tr>
    	
	<%
    }
    
    	double totSubSecAmtOfSection = 0;
		for (int j = 0; subInvestList != null && j < subInvestList.size(); j++) {
			k++;
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
					dblSubSecAmt = Math.min(dblPaidAmt, dblSubSecAmt);
				}
				if(dblSubSec10PerAdjLimitAmt>0) {
					dblSubSecAmt = Math.min(dblSubSecAmt, dblSubSec10PerAdjLimitAmt);
				} else if(dblSubSec10PerAdjLimitAmt<0) {
					dblSubSecAmt = 0;
				}
			
				/* if(strSubSecLimitType != null && strSubSecLimitType.equals("%")) {
					dblSubSecAmt = (dblPaidAmt * dblSubSecAmt) / 100;
				} else {
					dblSubSecAmt = dblPaidAmt;
					dblSubSecAmt = Math.min(dblPaidAmt, dblSubSecAmt);
				} */
			} else {
				dblSubSecAmt = dblPaidAmt;
				dblSubSecAmt = Math.min(dblPaidAmt, dblSubSecAmt);
				if(dblSubSec10PerAdjLimitAmt>0) {
					dblSubSecAmt = Math.min(dblSubSecAmt, dblSubSec10PerAdjLimitAmt);
				} else if(dblSubSec10PerAdjLimitAmt<0) {
					dblSubSecAmt = 0;
				}
				/* dblSubSecAmt = dblPaidAmt;
				dblSubSecAmt = Math.min(dblPaidAmt, dblSubSecAmt); */
			}
			totSubSecAmtOfSection += dblSubSecAmt;
			
	%>
	<tr>
		<td class="reportLabel" nowrap><%=(k) %>. <%=uF.showData(hm.get("SECTION_NAME"), "")%></td>
		<td class="reportLabel alignRight"><%=""+Math.round(dblPaidAmt) %></td>
		<td class="reportLabel alignRight">&nbsp;</td>
	</tr>
	<%
		}
	
		//if(subInvestList!=null || hmSectionMap.get(strSectionId).equals("80C and 80CCC")){
		if(subInvestList!=null || hmSectionMap.containsKey(strSectionId)){
			if(totSubSecAmtOfSection>0 && strAmt>totSubSecAmtOfSection) {
  				strAmt = totSubSecAmtOfSection;
  				if(hmSectionMap.containsKey(strSectionId) && uF.parseToBoolean(hmSectionPFApplicable.get(strSectionId+"_"+slabType))) {
  					strAmt += uF.parseToDouble(""+hmTaxInner.get("dblEmpPF"));
  				}
  				if(dblInvestmentLimit>0) {
  					strAmt = Math.min(strAmt, dblInvestmentLimit);
  				}
				dblChapterVIA += strAmt;
  			} else {
	  			dblChapterVIA += strAmt;
	  		}
	%>
	<tr>
		<td class="reportLabel" nowrap>&nbsp;</td>
		<td class="reportLabel alignRight">&nbsp;</td>
		<td class="reportLabel alignRight"><%=Math.round(strAmt) %></td>
	</tr>
	<%} else {
			dblChapterVIA += strAmt;
		}
		
	   	}
	   %>
	   
	   
	    
	<%
	Map<String,String> hmInvest1=hmEmpInvestment1.get(strEmpId);
	if(hmInvest1==null) hmInvest1 = new HashMap<String, String>();
	ii=0;
	for(int a=0; chapter2SectionList!=null && a<chapter2SectionList.size(); a++) {
		List<String> innnList = chapter2SectionList.get(a);
		String strSectionId = innnList.get(0);
		String strSlabType = innnList.get(1);
		if(uF.parseToInt(slabType)!= uF.parseToInt(strSlabType) && uF.parseToInt(strSlabType)!=2) {
			continue;
		}
		Map<String, String> hmSubSecAdjusted10PerLimitAmt = hmSectionwiseSubSecAdjusted10PerLimitAmt.get(strSectionId);
		if(hmSubSecAdjusted10PerLimitAmt==null) hmSubSecAdjusted10PerLimitAmt = new HashMap<String, String>();
		
		if(hmSectionLimitA.containsKey(strSectionId+"_"+slabType)) {
			dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId+"_"+slabType));
		} else {
			dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId+"_"+slabType));
			double dblVIA1Invest = uF.parseToDouble(hmInvest1.get(strSectionId));
			dblInvestmentLimit = dblVIA1Invest * dblInvestmentLimit / 100;
		}
		String strAmt = uF.showData(hmInvest1.get(strSectionId), "");
		if(dblInvestmentLimit>0) {
			strAmt = ""+Math.min(uF.parseToDouble(strAmt), dblInvestmentLimit);
		}
		
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
  			</tr>
  			<%	
  				} 
 			  		ii++;
  			  	Map<String,String> hmAcutualInvest=hmEmpActualInvestment1.get(strEmpId);
		        if(hmAcutualInvest==null) hmAcutualInvest= new HashMap<String, String>();
  			%>
	<tr>  
		<td class="reportLabel" nowrap><strong><%=ii %>. <%=uF.showData(hmSectionMap.get(strSectionId), "")%></strong> </td>
		<td class="reportLabel alignRight"><%=(subInvestList==null ? ""+Math.round(uF.parseToDouble(hmAcutualInvest.get(strSectionId))) : "") %></td>
		<td class="reportLabel alignRight"><%=(subInvestList==null ? ""+Math.round(uF.parseToDouble(strAmt)) : "") %></td>
	</tr>
	<%
		
		double totSubSecAmtOfSection = 0;
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
					dblSubSecAmt = Math.min(dblPaidAmt, dblSubSecAmt);
				}
				if(dblSubSec10PerAdjLimitAmt>0) {
					dblSubSecAmt = Math.min(dblSubSecAmt, dblSubSec10PerAdjLimitAmt);
				} else if(dblSubSec10PerAdjLimitAmt<0) {
					dblSubSecAmt = 0;
				}
				
				/* if(strSubSecLimitType != null && strSubSecLimitType.equals("%")) {
					dblSubSecAmt = (dblPaidAmt * dblSubSecAmt) / 100;
				} else {
					dblSubSecAmt = dblPaidAmt;
					dblSubSecAmt = Math.min(dblPaidAmt, dblSubSecAmt);
				} */
			} else {
				dblSubSecAmt = Math.min(dblPaidAmt, dblSubSecAmt);
				if(dblSubSec10PerAdjLimitAmt>0) {
					dblSubSecAmt = Math.min(dblSubSecAmt, dblSubSec10PerAdjLimitAmt);
				} else if(dblSubSec10PerAdjLimitAmt<0) {
					dblSubSecAmt = 0;
				}
				
				/* dblSubSecAmt = dblPaidAmt;
				dblSubSecAmt = Math.min(dblPaidAmt, dblSubSecAmt); */
			}
			totSubSecAmtOfSection += dblSubSecAmt;
			
	%>
	<tr>
		<td class="reportLabel" nowrap><%=(j+1) %> <%=uF.showData(hm.get("SECTION_NAME"), "")%></td>
		<td class="reportLabel alignRight"><%=""+Math.round(dblPaidAmt) %></td>
		<td class="reportLabel alignRight">&nbsp;</td>
	</tr>
	<%
		}
		if(subInvestList!=null){
			if(totSubSecAmtOfSection>0 && uF.parseToDouble(strAmt)>totSubSecAmtOfSection) {
  				strAmt = ""+totSubSecAmtOfSection;
  				if(dblInvestmentLimit>0) {
  					strAmt = ""+Math.min(uF.parseToDouble(strAmt), dblInvestmentLimit);
  				}
  			}
  			dblChapterVIA+=uF.parseToDouble(strAmt);
	%>
	<tr>
		<td class="reportLabel" nowrap>&nbsp;</td>
		<td class="reportLabel alignRight">&nbsp;</td>
		<td class="reportLabel alignRight"><%=Math.round(uF.parseToDouble(strAmt))%></td>
	</tr>
	<%} else {
		dblChapterVIA+=uF.parseToDouble(strAmt);
	}
	   	}
	   %>
	   
	<tr>
		<td class="reportLabel"><strong>10. Aggregate of deductible Amount under Chapter VI-A</strong></td>
		<td class="reportLabel"></td>
		<td class="reportLabel alignRight"><strong><%=""+Math.round(dblChapterVIA) %></strong></td>
	</tr>
	
	<% 	dblGrossNet -= Math.round(dblChapterVIA); 
		if(dblGrossNet<0) {
	    	dblGrossNet=0;
	    }
	%>
	
	<tr>
		<td class="reportLabel"><strong>11. Total Income (8-10) [Round off u/s 288B]</strong></td>
		<td class="reportLabel"></td>
		<td class="reportLabel alignRight"><strong><%=""+Math.round(dblGrossNet) %></strong></td>
	</tr>
	
	<tr>
		<td colspan="3"><hr style="color:#ccc"/></td>
	</tr>
	
	<%double dblTaxLiability = uF.parseToDouble((String)hmTaxInner.get("TAX_LIABILITY")); %>
	<tr>
		<td class="reportLabel"><strong>12. Tax on Total Income</strong></td>
		<td class="reportLabel">&nbsp;</td>
		<td class="reportLabel alignRight"><%=""+Math.round(dblTaxLiability) %></td>
	</tr>
	
	<%double dblRebate = uF.parseToDouble((String)hmTaxInner.get("TAX_REBATE")); %>
	<tr>
		<td class="reportLabel">&nbsp;</td>
		<td class="reportLabel"><strong>13. a). less Tax Rebate u/s 87 A</strong></td>
		<td class="reportLabel alignRight"><%=""+Math.round(dblRebate) %></td>
	</tr>
	
<!-- ===start parvez date: 01-04-2022=== -->	
	<%double dblPrevOrgTDSAmount = uF.parseToDouble(hmTaxInner.get("dblPrevOrgTDSAmount")+""); %>
	<tr>
		<td class="reportLabel">&nbsp;</td>
		<td class="reportLabel"><strong>    b). less Tax paid in previous Organization</strong></td>
		<td class="reportLabel alignRight"><%=""+Math.round(dblPrevOrgTDSAmount) %></td>
	</tr>
<!-- ===end parvez date: 01-04-2022=== -->	
	
	<%double dblCess1 = uF.parseToDouble((String)hmTaxInner.get("CESS1"));
	double dblCess1Amount = uF.parseToDouble((String)hmTaxInner.get("CESS1_AMOUNT")); %>
	<tr>
		<td class="reportLabel"></td>
		<td class="reportLabel"><strong>14. Add: Ed. Cess @ <%=dblCess1%>%</strong></td>
		<td class="reportLabel alignRight"><%=""+Math.round(dblCess1Amount)%></td>
	</tr>
	
	<%double dblCess2 = uF.parseToDouble((String)hmTaxInner.get("CESS2")) ;
	double dblCess2Amount = uF.parseToDouble((String)hmTaxInner.get("CESS2_AMOUNT")); %>
	<tr>
		<td class="reportLabel"></td>
		<td class="reportLabel" nowrap><strong>15. Add: She Cess @ <%=dblCess2%>%</strong></td>
		<td class="reportLabel alignRight"><%=""+Math.round(dblCess2Amount) %></td>
	</tr>
	<%double dblTotalTaxLiability = uF.parseToDouble((String)hmTaxInner.get("TOTAL_TAX_LIABILITY")); %>
	<tr>
		<td class="reportLabel"></td>
		<td class="reportLabel"><strong>16. Tax Payable ((12-13)+14+15)</strong></td>
		<td class="reportLabel alignRight"><strong><%=""+Math.round(dblTotalTaxLiability) %></strong></td>
	</tr>
	
	<tr>
		<td class="reportLabel"></td>
		<td class="reportLabel"><strong>17. Less: relief under section 89 (attach form 10E)</strong></td>
		<td class="reportLabel alignRight"><strong>0</strong></td>
	</tr>
	
	<tr>
		<td class="reportLabel"></td>
		<td class="reportLabel"><strong>18. Tax Payable (16+17)  [Round off u/s 288B]</strong></td>
		<td class="reportLabel alignRight"><strong><%=""+Math.round(dblTotalTaxLiability) %></strong></td>
	</tr>
		
	<tr>
		<td class="reportLabel"></td>
		<td class="reportLabel"><strong>19. Tax deducted at source u/s 192 (1)</strong></td>
		<td class="reportLabel alignRight"><strong><%=""+Math.round(uF.parseToDouble((String)hmPaidTdsMap.get(strEmpId))) %></strong></td>
	</tr>
	
	<tr>
		<td class="reportLabel"></td>
		<td class="reportLabel"><strong>20. Tax Payable/ Refundable (18-19)</strong></td>
		<td class="reportLabel alignRight"><strong><%=""+Math.round(Math.round(dblTotalTaxLiability) - Math.round(uF.parseToDouble((String)hmPaidTdsMap.get(strEmpId)))) %></strong></td>
	</tr>
</table>