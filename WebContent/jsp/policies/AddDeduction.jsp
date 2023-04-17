<%@page import="com.konnect.jpms.select.FillGender"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.*"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<script>
	addLoadEvent(prepareInputsForHints);
	
	function isNumberKey(evt)
	{
	   var charCode = (evt.which) ? evt.which : event.keyCode;
	   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46)
	      return false;
	
	   return true;
	}
	
</script>

<%
	CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
	if(CF==null)return;
	String strEmpType = (String) session.getAttribute("USERTYPE");
%>

 
<div id="addForm1" style="display: none;">
<s:form theme="simple" action="AddDeductionIndia" method="POST" cssClass="formcss" id="formAddNewRow" cssStyle="display: none;">

	<s:hidden name="deductionId" />

	<table border="0" class="formcss" style="width:675px">

		<tr>
			<td colspan=2><s:fielderror /></td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight">Income from<sup>*</sup>:</td>
			<td class="label"><input type="text" name="incomeFrom" id="incomeFrom" rel="0" class="required" onkeypress="return isNumberKey(event)"/>
			<span class="hint">This field is used to calculate the deduction amount. Income from is the lower slab.<span class="hint-pointer">&nbsp;</span></span></td>
		</tr>

		<tr>
			<td class="txtlabel alignRight">Income to<sup>*</sup>:</td>
			<td class="label"><input type="text" name="incomeTo" id="incomeTo" rel="1" class="required" onkeypress="return isNumberKey(event)"/>
			<span class="hint">This field is used to calculate the deduction amount. Income to is the upper slab.<span class="hint-pointer">&nbsp;</span></span></td>
		</tr>

		<tr>
			<td class="txtlabel alignRight">Deduction amount<sup>*</sup>:</td>
			<td class="label"><input type="text" name="deductionAmount" id="deductionAmount" rel="2" class="required" onkeypress="return isNumberKey(event)"/>
			<span class="hint">This is the actual amount which will be deducted from net income based on the slabs.<span class="hint-pointer">&nbsp;</span></span></td>
		</tr>
		
		<tr>
			<td></td>
			<td><s:submit cssClass="input_button" value="Ok" id="btnAddNewRowOk"/> 
			<s:submit  cssClass="input_button" value="Cancel" id="btnAddNewRowCancel"/></td>
		</tr>
		
	</table>
</s:form>
</div>