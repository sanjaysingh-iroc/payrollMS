<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>



<div class="tab_pagetitle">Request for a bondfide certificate</div>
<div style="float:left">

	<s:form theme="simple" action="BonafideRequests" method="POST" cssClass="formcss">
	<s:hidden name="strRT" value="BF"></s:hidden>
	
	<table>
		<tr>
			<td colspan="2"></td>
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