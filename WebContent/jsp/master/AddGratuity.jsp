<%@page import="com.konnect.jpms.select.FillPerkPaymentCycle"%>
<%@page import="com.konnect.jpms.select.FillPerkType"%>
<%@page import="com.konnect.jpms.select.FillDesig"%>
<%@page import="com.konnect.jpms.select.FillLevel"%>
<%@page import="com.konnect.jpms.select.FillUserStatus"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<script>
addLoadEvent(prepareInputsForHints);
</script>
 
<s:form theme="simple" id="formAddNewRow" action="AddGratuity" method="POST" cssClass="formcss" cssStyle="display: none;">

	<table border="0" class="formcss" style="width: 675px">
		
		<tr>
			<td class="txtlabel alignRight">Service From<sup>*</sup>:</td>
			<td>
				<input type="text" name="strServiceFrom" rel="0" class="required" /> 
				<span class="hint">Service From<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>
	
		<tr>
			<td class="txtlabel alignRight">Service To<sup>*</sup>:</td>
			<td>
				<input type="text" name="strServiceTo" rel="1" class="required" /> 
				<span class="hint">Service To<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight">Gratuity Days:</td>
			<td>
				<input type="text" name="strGratuityDays" rel="2" /> 
				<span class="hint">Gratuity Days<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight">Max Gratuity Amount</td>
			<td>
				<input type="text" name="strMaxGratuityAmount" rel="3" /> 
				<span class="hint">Max Gratuity Amount<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>
		
		<tr>
			<td></td>
			<td>
				<s:submit cssClass="input_button" value="Ok" id="btnAddNewRowOk" /> 
				<s:submit cssClass="input_button" value="Cancel" id="btnAddNewRowCancel" />
			</td>
		</tr>


	</table>
</s:form>

