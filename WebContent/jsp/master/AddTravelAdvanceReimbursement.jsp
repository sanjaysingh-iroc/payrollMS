<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="com.konnect.jpms.select.FillSalaryHeads"%>
<%@page import="com.konnect.jpms.select.FillLevel"%>
<%@page import="com.konnect.jpms.select.FillUserStatus"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<script type="text/javascript">
jQuery(document).ready(function(){
	$("#submitButton").click(function(){
    	$(".validateRequired").prop('required',true);
    });
}); 

function isNumberKey(evt){
   var charCode = (evt.which) ? evt.which : event.keyCode;
   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46){
      return false;
   }
   return true;
}

</script>

<% 
	UtilityFunctions uF = new UtilityFunctions();
%>
	
<div>
	<s:form theme="simple" name="formAddTravelAdvanceReimbursement" id="formAddTravelAdvanceReimbursement" action="AddTravelAdvanceReimbursement" method="POST" cssClass="formcss">
		<s:hidden name="reimbPolicyId"></s:hidden>
		<s:hidden name="operation"></s:hidden>
		<s:hidden name="strOrg"></s:hidden>
		<s:hidden name="strLevel"></s:hidden>
		<s:hidden name="userscreen" id="userscreen"/>
		<s:hidden name="navigationId" id="navigationId"/>
		<s:hidden name="toPage" id="toPage"/>
		
		<table class="table table_no_border">
			<tr>
				<td class="alignRight">Select Country:<sup>*</sup></td>
				<td>
					<s:select theme="simple" list="countryList" name="strCountry" id="strCountry" headerKey="" headerValue="Select Country" 
					listKey="countryId" listValue="countryName" cssClass="validateRequired form-control"/>					
				</td>
			</tr>
			
			<tr>
				<td class="alignRight">City:<sup>*</sup></td>
				<td>
					<s:textfield name="strCity" id="strCity" cssClass="validateRequired form-control"/> 
				</td>
			</tr>
			
			<tr>
				<td class="alignRight">Eligible amount:<sup>*</sup></td>
				<td><s:textfield name="eligibleAmount" id="eligibleAmount" cssStyle="width:91px !important; text-align:right;" cssClass="validateRequired" onkeypress="return isNumberKey(event)"/>&nbsp;
					per&nbsp;<s:select theme="simple" name="eligibilityType" id="eligibilityType" list="#{'1':'Day','2':'Week','3':'Month'}" 
					cssClass="validateRequired" cssStyle="width:75px !important;"/>					
				</td>
			</tr>
			
			<tr>
				<td>&nbsp;</td>
				<td>
					<s:submit value="Save" cssClass="btn btn-primary" name="submit" theme="simple" id="submitButton"/>  
				</td>
			</tr>
		</table>
	</s:form>
</div>

<script type="text/javascript">
checkLocalType();
</script>