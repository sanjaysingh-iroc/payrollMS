<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.*" %>
<%@taglib uri="/struts-tags" prefix="s"%>

<script>
	addLoadEvent(prepareInputsForHints);
</script>

 
<%	CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions); 
String EMPNAME = (String)session.getAttribute("EMPNAME_P");
%>

<SCRIPT LANGUAGE="javascript">

	function fun(val) {
		
		if (val == 'PT') {

			document.getElementById('mon').disabled = true;
			document.getElementById('mon').value = '';
			document.getElementById('tues').disabled = true;
			document.getElementById('tues').value = '';
			document.getElementById('wed').disabled = true;
			document.getElementById('wed').value = '';
			document.getElementById('thrus').disabled = true;
			document.getElementById('thrus').value = '';
			document.getElementById('fri').disabled = true;
			document.getElementById('fri').value = '';
			document.getElementById('sat').disabled = true;
			document.getElementById('sat').value = '';
			document.getElementById('sun').disabled = true;
			document.getElementById('sun').value = '';

		} else {
			document.getElementById('mon').disabled = false;
			document.getElementById('tues').disabled = false;
			document.getElementById('wed').disabled = false;
			document.getElementById('thrus').disabled = false;
			document.getElementById('fri').disabled = false;
			document.getElementById('sat').disabled = false;
			document.getElementById('sun').disabled = false;
		}

	}
	 
	
function callPayMode(val) {
		
		if (val == 'H') {

			document.getElementById('fxdAmt').disabled = true;
			document.getElementById('fxdAmt').value = '0';
			document.getElementById('idHolidayLoading').disabled = true;
			document.getElementById('idHolidayLoading').value = '0';
			
			document.getElementById('mon').disabled = false;
			document.getElementById('tues').disabled = false;
			document.getElementById('wed').disabled = false;
			document.getElementById('thrus').disabled = false;
			document.getElementById('fri').disabled = false;
			document.getElementById('sat').disabled = false;
			document.getElementById('sun').disabled = false;
			
			document.getElementById('idMonHolidayLoading').disabled = false;
			document.getElementById('idTueHolidayLoading').disabled = false;
			document.getElementById('idWedHolidayLoading').disabled = false;
			document.getElementById('idThursHolidayLoading').disabled = false;
			document.getElementById('idFriHolidayLoading').disabled = false;
			document.getElementById('idSatHolidayLoading').disabled = false;
			document.getElementById('idSunHolidayLoading').disabled = false;
			
			

		} else {
			document.getElementById('fxdAmt').disabled = false;
			
			document.getElementById('mon').disabled = true;
			document.getElementById('mon').value = '0';
			document.getElementById('tues').disabled = true;
			document.getElementById('tues').value = '0';
			document.getElementById('wed').disabled = true;
			document.getElementById('wed').value = '0';
			document.getElementById('thrus').disabled = true;
			document.getElementById('thrus').value = '0';
			document.getElementById('fri').disabled = true;
			document.getElementById('fri').value = '0';
			document.getElementById('sat').disabled = true;
			document.getElementById('sat').value = '0';
			document.getElementById('sun').disabled = true;
			document.getElementById('sun').value = '0';
			

			document.getElementById('idHolidayLoading').disabled = false;
			document.getElementById('idHolidayLoading').value = '0';			
			document.getElementById('idMonHolidayLoading').disabled = true;
			document.getElementById('idMonHolidayLoading').value = '0';
			document.getElementById('idTueHolidayLoading').disabled = true;
			document.getElementById('idTueHolidayLoading').value = '0';
			document.getElementById('idWedHolidayLoading').disabled = true;
			document.getElementById('idWedHolidayLoading').value = '0';
			document.getElementById('idThursHolidayLoading').disabled = true;
			document.getElementById('idThursHolidayLoading').value = '0';
			document.getElementById('idFriHolidayLoading').disabled = true;
			document.getElementById('idFriHolidayLoading').value = '0';
			document.getElementById('idSatHolidayLoading').disabled = true;
			document.getElementById('idSatHolidayLoading').value = '0';
			document.getElementById('idSunHolidayLoading').disabled = true;
			document.getElementById('idSunHolidayLoading').value = '0';
		}

	}
</SCRIPT>





<div class="aboveform">
<h4><%=(request.getParameter("E")!=null)?"Edit":"Add" %> Rates <%= ((EMPNAME!=null)?" for "+EMPNAME:"") %></h4>
<h5>Please add rates for each day. Specify the rate amount in numbers only, with two decimal points. Do not include a sign. For example: 10.00</h5>

<%
	String strMessage = (String) request.getAttribute("MESSAGE");
	if (strMessage == null) {
		strMessage = "";
	}
%>


<p class="message"><%=strMessage%></p>

<s:form action="PayrollPolicy1" theme="simple"  method="POST" cssClass="formcss">

	<s:hidden name="payrollPolicyId" />
	
<!-- 
	<s:select label="Frequency" name="frequencyType"
		listKey="frequencyTypeId" listValue="frequencyTypeName" headerKey="0"
		headerValue="Select Frequency" list="frequencyTypeList" key=""
		required="true" />

	<s:textfield name="payDay" label="Pay Day" required="true" />

	<s:select label="Select Employment Type" name="empType"
		listKey="empTypeId" listValue="empTypeName" headerKey="0"
		headerValue="Select Emp Type" list="empTypeList" key=""
		required="true"  value="" onchange="fun(this.value);"/>


 
	<s:select cssStyle="display:none" label="Select Employee" name="designation"
		listKey="desigId" listValue="desigName" headerKey="0"
		headerValue="Select Employee" list="desigList" key=""
		required="true" />

	<s:select cssStyle="display:none" label="Cost Centre" name="service" listKey="serviceId"
		listValue="serviceName" headerKey="0"
		headerValue="Choose Cost Centres" list="serviceList" key=""
		required="true" />
 -->
 
 <s:hidden name="designation"/>
 <s:hidden name="service" />
 <s:hidden name="strP" />


 
<table class="formcss">


<tr><td colspan=2><s:fielderror/></td></tr>


<tr>
	<td class="label alignRight">Pay Mode:</td>
	<td>
	<s:select cssStyle="width:107px" label="Pay Mode" name="payMode" listKey="payModeId"  onchange="callPayMode(this.value);"
		listValue="payModeName" headerKey="0" headerValue="Select Mode"
		list="payModeList" key="" required="true" /><span class="hint">Select pay mode of an employee.<span class="hint-pointer">&nbsp;</span></span>
	</td>
</tr>

<tr>
	<td class="label alignRight">Wage Amount:</td>
	<td width="120" class="label"><s:textfield cssStyle="width:100px" name="fxdAmount" label="Amount" required="true" id="fxdAmt" /><span class="hint">Fixed amount for an employee for any paycycle. Specify the rate amount in numbers only, with two decimal points. Do not include a sign. For example: 10.00.<span class="hint-pointer">&nbsp;</span></span></td>
	<td class="label alignRight">Loading Amount:</td>
	<td width="120" class="label"><s:textfield cssStyle="width:100px" name="holidayLoading" label="Amount" required="true" id="idHolidayLoading" /><span class="hint">Fixed amount for an employee for any paycycle. Specify amount in %.<span class="hint-pointer">&nbsp;</span></span>%</td>
</tr>

<tr>
	<td class="label alignRight">Monday:</td>
	<td class="label"><s:textfield cssStyle="width:100px" name="monAmount" label="Monday" required="true" id="mon"/><span class="hint">Hourly rate for Monday. Specify the rate amount in numbers only, with two decimal points. Do not include a sign. For example: 10.00.<span class="hint-pointer">&nbsp;</span></span></td>
	<td class="label alignRight">Loading Amount:</td>
	<td class="label"><s:textfield cssStyle="width:100px" name="monHolidayLoading" label="Amount" required="true" id="idMonHolidayLoading" /><span class="hint">Loading amount for Monday of an employee for any paycycle. Specify amount in %.<span class="hint-pointer">&nbsp;</span></span>%</td>
	
</tr>

<tr>
	<td class="label alignRight">Tuesday:</td>
	<td class="label"><s:textfield cssStyle="width:100px" name="tuesAmount" label="Tuesday" required="true" id="tues"/><span class="hint">Hourly rate for Tuesday. Specify the rate amount in numbers only, with two decimal points. Do not include a sign. For example: 10.00.<span class="hint-pointer">&nbsp;</span></span></td>
	<td class="label alignRight">Loading Amount:</td>
	<td class="label"><s:textfield cssStyle="width:100px" name="tueHolidayLoading" label="Amount" required="true" id="idTueHolidayLoading" /><span class="hint">Loading amount for Tuesday of an employee for any paycycle. Specify amount in %.<span class="hint-pointer">&nbsp;</span></span>%</td>
</tr>

<tr>
	<td class="label alignRight">Wednesday:</td>
	<td class="label"><s:textfield cssStyle="width:100px" name="wedAmount" label="Wednesday" required="true" id="wed" /><span class="hint">Hourly rate for Wednesday. Specify the rate amount in numbers only, with two decimal points. Do not include a sign. For example: 10.00.<span class="hint-pointer">&nbsp;</span></span></td>
	<td class="label alignRight">Loading Amount:</td>
	<td class="label"><s:textfield cssStyle="width:100px" name="wedHolidayLoading" label="Amount" required="true" id="idWedHolidayLoading" /><span class="hint">Loading amount for Wednesday of an employee for any paycycle. Specify amount in %.<span class="hint-pointer">&nbsp;</span></span>%</td>
</tr>

<tr>
	<td class="label alignRight">Thursday:</td>
	<td class="label"><s:textfield cssStyle="width:100px" name="thursAmount" label="Thursday" required="true" id="thrus"/><span class="hint">Hourly rate for Thursday. Specify the rate amount in numbers only, with two decimal points. Do not include a sign. For example: 10.00.<span class="hint-pointer">&nbsp;</span></span></td>
	<td class="label alignRight">Loading Amount:</td>
	<td class="label"><s:textfield cssStyle="width:100px" name="thursHolidayLoading" label="Amount" required="true" id="idThursHolidayLoading" /><span class="hint">Loading amount for Thursday of an employee for any paycycle. Specify amount in %.<span class="hint-pointer">&nbsp;</span></span>%</td>
</tr>

<tr>
	<td class="label alignRight">Friday:</td>
	<td class="label"><s:textfield cssStyle="width:100px" name="friAmount" label="Friday" required="true" id="fri"/><span class="hint">Hourly rate for Friday. Specify the rate amount in numbers only, with two decimal points. Do not include a sign. For example: 10.00.<span class="hint-pointer">&nbsp;</span></span></td>
	<td class="label alignRight">Loading Amount:</td>
	<td class="label"><s:textfield cssStyle="width:100px" name="friHolidayLoading" label="Amount" required="true" id="idFriHolidayLoading" /><span class="hint">Loading amount for Friday of an employee for any paycycle. Specify amount in %.<span class="hint-pointer">&nbsp;</span></span>%</td>
</tr>

<tr>
	<td class="label alignRight">Saturday:</td>
	<td class="label"><s:textfield cssStyle="width:100px" name="satAmount" label="Saturday" required="true" id="sat"/><span class="hint">Hourly rate for Saturday. Specify the rate amount in numbers only, with two decimal points. Do not include a sign. For example: 10.00.<span class="hint-pointer">&nbsp;</span></span></td>
	<td class="label alignRight">Loading Amount:</td>
	<td class="label"><s:textfield cssStyle="width:100px" name="satHolidayLoading" label="Amount" required="true" id="idSatHolidayLoading" /><span class="hint">Loading amount for Saturday of an employee for any paycycle. Specify amount in %.<span class="hint-pointer">&nbsp;</span></span>%</td>
</tr>

<tr>
	<td class="label alignRight">Sunday:</td>
	<td class="label"><s:textfield cssStyle="width:100px" name="sunAmount" label="Sunday" required="true" id="sun"/><span class="hint">Hourly rate for Sunday. Specify the rate amount in numbers only, with two decimal points. Do not include a sign. For example: 10.00.<span class="hint-pointer">&nbsp;</span></span></td>
	<td class="label alignRight">Loading Amount:</td>
	<td class="label"><s:textfield cssStyle="width:100px" name="sunHolidayLoading" label="Amount" required="true" id="idSunHolidayLoading" /><span class="hint">Loading amount for Sunday of an employee for any paycycle. Specify amount in %.<span class="hint-pointer">&nbsp;</span></span>%</td>
</tr>


<%
		if (request.getParameter("E") != null) {
	%>
	<tr>
	<td colspan="2" align="center">
		<s:submit cssClass="input_button" value="Update Rates" align="center" />
	</td>
	</tr>
	<%
		} else {
	%>
	<tr>
	<td colspan="2" align="center">
		<s:submit cssClass="input_button" value="Add Rates" align="center" />
	</td>
	</tr>
	<%
		}
	%>

</table>


</s:form>

</div>

