<%@page import="com.konnect.jpms.select.FillGender"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.*"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<script type="text/javascript">
$(document).ready( function () {
	jQuery("#formAddNewRow").validationEngine();
});

	addLoadEvent(prepareInputsForHints);
	
	function isNumberKey(evt){
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

 

<s:form theme="simple" action="AddIncrement" method="POST" cssClass="formcss" id="formAddNewRow">

	<s:hidden name="incrementId" />
	<s:hidden name="orgId" />

	<table border="0" class="formcss" style="width:675px">

		<tr>
			<td colspan=2><s:fielderror /></td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight">Income from<sup>*</sup>:</td>
			<td class="label"><s:textfield name="incomeFrom" id="incomeFrom" cssClass="validateRequired" onkeypress="return isNumberKey(event)"/>
			<span class="hint">This field is used to calculate the increment amount. Income from is the lower slab.<span class="hint-pointer">&nbsp;</span></span></td>
		</tr>

		<tr>
			<td class="txtlabel alignRight">Income to<sup>*</sup>:</td>
			<td class="label"><s:textfield name="incomeTo" id="incomeTo"  cssClass="validateRequired" onkeypress="return isNumberKey(event)"/>
			<span class="hint">This field is used to calculate the increment amount. Income to is the upper slab.<span class="hint-pointer">&nbsp;</span></span></td>
		</tr>

		<tr>
			<td class="txtlabel alignRight">Increment amount<sup>*</sup>:</td>
			<td class="label"><s:textfield name="incrementAmount" id="incrementAmount"  cssClass="validateRequired" onkeypress="return isNumberKey(event)"/>
			<span class="hint">This is the actual amount which will be deducted from net income based on the slabs.<span class="hint-pointer">&nbsp;</span></span></td>
		</tr>
		
		
		<tr>
			<td class="txtlabel alignRight">Payable Month<sup>*</sup>:</td>
			<td>
				<s:select list="monthList" listKey="monthId" listValue="monthName" name="dueMonth" cssClass="validateRequired"></s:select>
			</td>
		</tr>
		
		
		<tr>
			<td></td>
			<td>
			<s:submit cssClass="input_button" value="Submit" id="btnAddNewRowOk"/> 
			<%-- <s:submit  cssClass="input_button" value="Cancel" id="btnAddNewRowCancel"/> --%>
			</td>
		</tr>
		
	</table>
</s:form>
