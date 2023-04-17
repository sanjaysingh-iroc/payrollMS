<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>



<div class="tab_pagetitle">Other request</div>
<div style="float:left">

	<s:form theme="simple" action="EmpRequests" method="POST" cssClass="formcss">
	<s:hidden name="strRT" value="IR"></s:hidden>
	
	<table>
		
		
		<tr>
			<td class="txtlabel alignRight" valign="top">From:</td>
			<td><s:textfield name="strFromDate" /></td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight" valign="top">To:</td>
			<td><s:textfield name="strToDate" /></td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight" valign="top">From:</td>
			<td><s:textfield name="strFromTime" /></td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight" valign="top">To:</td>
			<td><s:textfield name="strToTime" /></td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight" valign="top">Name:</td>
			<td><s:textfield name="strInfraName" /></td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight" valign="top">Type:</td>
			<td><s:textfield name="strInfraType" /></td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight" valign="top">Location:</td>
			<td>
				<s:select label="Select Work Location" name="wLocation"
				listKey="wLocationId" listValue="wLocationName" headerKey="0"
				headerValue="Select Location" list="wLocationList" />
			</td>
		</tr>
		
		
		
		
		<tr>
			<td class="txtlabel alignRight" valign="top">Purpose:</td>
			<td><s:textarea name="strPurpose" rows="10" cols="100"></s:textarea></td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight" valign="top">Request Mode:</td>
			<td class="txtlabel"><s:radio list="hmModeRequest" name="strRequestMode"></s:radio> </td>
		</tr>
		
		<tr>
			<td colspan="2"><s:submit value="Submit" cssClass="input_button"></s:submit></td>
		</tr>
		
	</table>
	
	</s:form>

</div>