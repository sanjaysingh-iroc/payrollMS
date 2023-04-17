<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<%
UtilityFunctions uF = new UtilityFunctions();
CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
//List alEPFSettings = (List)request.getAttribute("alESISettings");
//if(alEPFSettings==null)alEPFSettings=new ArrayList();
LinkedHashMap hmMiscSettings = (LinkedHashMap)request.getAttribute("hmMiscSettings");
if(hmMiscSettings==null)hmMiscSettings=new LinkedHashMap();

%>

 
<script>

jQuery(document).ready(function() {
	// binds form submission and fields to the validation engine
	jQuery("#idFrmMiscSetting").validationEngine();
});	

	addLoadEvent(prepareInputsForHints);     
</script>

<jsp:include page="../../common/SubHeader.jsp">
	<jsp:param value="Other Settings" name="title"/>
</jsp:include>
 

    <div class="leftbox reportWidth">

<%=uF.showData((String) session.getAttribute("MESSAGE"), "")%>


<s:form name="frm_miscSetting" id="idFrmMiscSetting" theme="simple" action="MiscSetting">
<!-- <div class="pagetitle" style="margin:0px 0px 10px 0px;float:left">Other tax administration for FY </div> -->
<p style="font-size: 10px; padding-left: 42px;padding-right: 10px; font-style: italic;float:right">Last updated by <%=uF.showData((String)request.getAttribute("UPDATED_NAME"), "N/A") %> on <%=uF.showData((String)request.getAttribute("UPDATED_DATE"), "N/A") %></p>
<s:hidden name="strMiscId"></s:hidden>
<table width="30%" style="float:left">
	
	<tr>	
		<td class="label" valign="top" style="padding-left:10px">
			<s:select list="countryList" listValue="countryName" listKey="countryId" headerKey="" headerValue="Select Country" 
				cssClass = "validateRequired" name="country" onchange="getContentAcs('stateTD','GetStates.action?country='+this.value);"/>
		</td>
		
		<td id="stateTD" class="label" valign="top" style="padding-left:10px">
			<s:select list="stateList" listValue="stateName" listKey="stateId" headerKey="" headerValue="Select State" 
				cssClass = "validateRequired" name="state"/>
		</td>
		
		<td class="label" valign="top" style="padding-left:10px">
			<s:select list="financialYearList" listValue="financialYearName" listKey="financialYearId" name="financialYear"/>
		</td>
	</tr>
	
	
	<tr>	
		<%-- <td class="label" colspan="2"><strong>Other Tax Information</strong></td> --%>
	</tr>
	<tr>	
		<td class="label alignRight">Flat TDS: </td>
		<td class="label"><s:textfield name="flatTds" cssStyle="width:50px;text-align:right" cssClass = "validateRequired"></s:textfield> %<span class="hint">Add new leave type here.<span class="hint-pointer">&nbsp;</span></span></td>
	</tr>
	
	<tr>	
		<td class="label alignRight">Service Tax: </td>
		<td class="label"><s:textfield name="serviceTax" cssStyle="width:50px;text-align:right" cssClass = "validateRequired"></s:textfield> %</td>
	</tr>
	
	<tr>	
		<td class="label alignRight">Standard Cess: </td>
		<td class="label"><s:textfield name="standardCess" cssStyle="width:50px;text-align:right" cssClass = "validateRequired"></s:textfield> %</td>
	</tr>
	
	<tr>	
		<td class="label alignRight">Education Cess: </td>
		<td class="label"><s:textfield name="educationCess" cssStyle="width:50px;text-align:right" cssClass = "validateRequired"></s:textfield> %</td>
	</tr>
	<%-- <%if(uF.parseToBoolean((String)request.getAttribute(IConstants.UPDATE_ACCESS))){ %> --%>
	<%if(true){ %>
	<tr>	
		<td colspan="2" align="center"><input type="submit" class="input_button" name="miscUpdate" value="Update" onclick="return confirm('Are you sure you want to update these settings?')"/></td>
	</tr>
	<%} %>
</table>


</s:form>










			<div class="pagetitle" style="margin:10px 0px 10px 0px">Previous Year Misc Tax Calculations</div>
			<table width="100%" class="tb_style">
			
				<tr>
					<th class="">FYI</th>
					<th class="">Flat TDS<br/>(%)</th>
					<th class="">Service Tax<br/>(%)</th>
					<th class="">Standard Cess<br/>(%)</th>
					<th class="">Education Cess<br/>(%)</th>
				</tr>
				


<%

Set set = hmMiscSettings.keySet();
Iterator it = set.iterator();
while(it.hasNext()){
	String strStateId = (String)it.next();
	List alEPFSettings = (List)hmMiscSettings.get(strStateId);
	if(alEPFSettings==null)alEPFSettings=new ArrayList();
	
	
	%>
	<tr>
		<th colspan="5" class="alignLeft"><%= strStateId%></th>
	</tr>
	
	<%
	
	
	
	
	for(int i=0; i<alEPFSettings.size(); i++) {
		List alInner = (List)alEPFSettings.get(i);
		if(alInner==null)alInner=new ArrayList();
			%>
				<tr>
					<td class="alignCenter"><%=uF.showData((String)alInner.get(0), "")%></td>
					<td class="alignRight padRight20"><%=uF.showData((String)alInner.get(1), "0")%></td>
					<td class="alignRight padRight20"><%=uF.showData((String)alInner.get(2), "0")%></td>
					<td class="alignRight padRight20"><%=uF.showData((String)alInner.get(3), "0")%></td>
					<td class="alignRight padRight20"><%=uF.showData((String)alInner.get(4), "0")%></td>
				</tr>
			
			<%
	}
}
%>
</table>


</div>