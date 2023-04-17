<%@page import="com.konnect.jpms.select.FillLevel"%>
<%@page import="com.konnect.jpms.select.FillUserStatus"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<script type="text/javascript">
$(function() {
    $( "#ltaFrom" ).datepicker();
    $( "#ltaTo" ).datepicker();
});
addLoadEvent(prepareInputsForHints);
</script>

<s:form theme="simple" id="formAddNewRow" action="AddLTA" method="POST"
	cssClass="formcss" cssStyle="display: none;">

	<table border="0" class="formcss" style="width: 675px">
		
		<tr>
			<td class="txtlabel alignRight">LTA From<sup>*</sup>:</td>
			<td>
				<input type="text" name="ltaFrom" id="ltaFrom" rel="0" class="required" /> 
				<span class="hint">LTA Code<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>
	
		<tr>
			<td class="txtlabel alignRight">LTA To<sup>*</sup>:</td>
			<td>
				<input type="text" name="ltaTo" id="ltaTo" rel="1" class="required" /> 
				<span class="hint">LTA Name<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight">LTA Limit<sup>*</sup>:</td>
			<td>
				<input type="text" name="ltaLimit" id="ltaLimit" rel="2" class="required"/> 
				<span class="hint">LTA Description<span class="hint-pointer">&nbsp;</span></span>
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

