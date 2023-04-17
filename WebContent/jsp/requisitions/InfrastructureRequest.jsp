<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>

<script>
    $(function() {
        $( "#idStartDate" ).datepicker({dateFormat: 'dd/mm/yy'});
        $( "#idEndDate" ).datepicker({dateFormat: 'dd/mm/yy'});
        $( "#idStartTime" ).timepicker({});
        $( "#idEndTime" ).timepicker({});
    });
    
	addLoadEvent(prepareInputsForHints);     
</script>


<div class="tab_pagetitle">Request for a infrastructure</div>
<div style="float:left">

	<s:form theme="simple" action="InfraRequests" method="POST" cssClass="formcss">
	<s:hidden name="strRT" value="IR"></s:hidden>
	
	<table>
		
		
		<tr>
			<td class="txtlabel alignRight" valign="top">From:</td>
			<td><s:textfield name="strFromDate" id="idStartDate" /></td>
			<td class="txtlabel alignRight" valign="top">To:</td>
			<td><s:textfield name="strToDate" id="idEndDate"/></td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight" valign="top">From:</td>
			<td><s:textfield name="strFromTime" id="idStartTime" /></td>
			<td class="txtlabel alignRight" valign="top">To:</td>
			<td><s:textfield name="strToTime" id="idEndTime"/></td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight" valign="top">Name:</td>
			<td colspan="3"><s:textfield name="strInfraName" /></td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight" valign="top">Type:</td>
			<td colspan="3">
				<s:select name="strInfraType"
					listKey="strInfraTypeId" listValue="strInfraTypeName" headerKey="0"
					headerValue="Select Type" list="strInfraTypeList" />
			</td>
		</tr>
		  
		<tr>
			<td class="txtlabel alignRight" valign="top">Location:</td>
			<td colspan="3">
				<s:select label="Select Work Location" name="wLocation"
				listKey="wLocationId" listValue="wLocationName" headerKey="0"
				headerValue="Select Location" list="wLocationList" />
			</td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight" valign="top">Purpose:</td>
			<td colspan="3"><s:textarea name="strPurpose" rows="10" cols="100"></s:textarea></td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight" valign="top">Request Mode:</td>
			<td colspan="3" class="txtlabel"><s:radio list="hmModeRequest" name="strRequestMode"></s:radio> </td>
		</tr>
		
		<tr>
			<td colspan="4"><s:submit value="Submit" cssClass="input_button"></s:submit></td>
		</tr>
		
	</table>
	
	</s:form>

</div>