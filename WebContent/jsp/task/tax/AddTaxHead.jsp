<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.select.FillUserStatus"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<script>

$(document).ready( function () {
	$("#btnOk").click(function(){
		$(".validateRequired").prop('required',true);
	});
});	

$(function() {
	$("#effectiveDate").datepicker({
		format : 'dd/mm/yyyy'
	});
	
});


function isNumberKey(evt) {
   var charCode = (evt.which) ? evt.which : event.keyCode;
   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46) {
      return false;
   }
   return true;
}

</script>

<%
UtilityFunctions uF = new UtilityFunctions();
String taxHeadId = (String)request.getAttribute("taxHeadId"); %>

<s:form theme="simple" id="formAddTaxHead" action="AddTaxHead" method="POST" cssClass="formcss">
	<s:hidden name="taxHeadId"></s:hidden>
	<s:hidden name="userscreen" />
	<s:hidden name="navigationId" />
	<s:hidden name="toPage" />
<%-- <s:hidden name="orgId"></s:hidden> --%>

	<table class="table table_bordered">
		
		<tr>
			<td class="txtlabel alignRight"><label for="organisation_Name">Organisation:<sup>*</sup></label><br/></td>
			<td><s:select list="orgList" name="strOrg" id="strOrg" listKey="orgId" listValue="orgName" headerKey="" headerValue="Select Organisation"
			 cssClass="validateRequired"></s:select></td> 
		</tr>
		
		<tr>
			<td class="txtlabel alignRight">Effective Date:<sup>*</sup></td>
			<td><s:textfield name="effectiveDate" id="effectiveDate" cssClass="validateRequired" /></td>  
		</tr>
		
		<tr>
			<td class="txtlabel alignRight">Tax Head:<sup>*</sup></td>
			<td><s:textfield name="taxHead" id="taxHead" cssClass="validateRequired" /></td>  
		</tr>
		
		<tr>
			<td class="txtlabel alignRight">Tax Head Label:<sup>*</sup></td>
			<td><s:textfield name="taxHeadLabel" id="taxHeadLabel" cssClass="validateRequired" /></td>  
		</tr>
	 
		<tr>
			<td class="txtlabel alignRight">Tax %:<sup>*</sup></td>
			<td><s:textfield name="taxPercent" id="taxPercent" cssClass="validateRequired" onkeypress="return isNumberKey(event)"/></td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight">Deduction Type:<sup>*</sup></td>
			<td>
			<s:select name="invoiceOrCustomer" id="invoiceOrCustomer" cssClass="validateRequired" headerKey="" headerValue="Select Deduction Type"
				cssStyle="width: 150px;" listKey="headId" listValue="headName" list="taxDeductionTypeList"/>
			<%-- <s:select name="invoiceOrCustomer" id="invoiceOrCustomer" headerKey="" headerValue="Select Type" list="#{'1':'Invoice', '2':'Customer Deduction'}" /> --%>
			</td>
		</tr>
		
		<%-- <tr>
			<td class="txtlabel alignRight" valign="top">Level Description:</td>
			<td>
				<s:textarea name="levelDesc" id="levelDesc" rows="3" cols="22"/> 
				<span class="hint">Level Description<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr> --%>

				
		<tr>
			<td colspan="2" align="center">
			<% if(uF.parseToInt(taxHeadId) > 0) { %>
				<s:submit cssClass="btn btn-primary" value="Update" name="btnOk" id="btnOk" />
				<s:submit cssClass="btn btn-primary" value="Change" name="btnOk" id="btnOk" />
			<% } else { %>
				<s:submit cssClass="btn btn-primary" value="Add" name="btnOk" id="btnOk" />
			<% } %>	
			</td>
		</tr>
	</table>
	
</s:form>
