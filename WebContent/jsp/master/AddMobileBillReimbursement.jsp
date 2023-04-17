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

function isNumberKey(evt){
   var charCode = (evt.which) ? evt.which : event.keyCode;
   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46){
      return false;
   }
   return true;
}

function checkMobileLimitType(){
	var mobileLimitType = document.getElementById("mobileLimitType").value;
	if(parseInt(mobileLimitType) == 2){
		document.getElementById("trMobileLimit").style.display = 'table-row';
	}else {
		document.getElementById("trMobileLimit").style.display = 'none';
	}
}

jQuery(document).ready(function(){
    $("#submitButton").click(function(){
    	$(".validateRequired").prop('required',true);
    });
}); 

</script>

<% 
	UtilityFunctions uF = new UtilityFunctions();
	String mobileLimitType=(String)request.getAttribute("mobileLimitType");
	String strDisplay = "none";
	if(uF.parseToInt(mobileLimitType) == 2){
		strDisplay = "table-row"; 
	}
	
%>
	
<div>
	<s:form  theme="simple" name="formAddMobileBillReimbursement" id="formAddMobileBillReimbursement" action="AddMobileBillReimbursement" method="POST" cssClass="formcss">
		<s:hidden name="reimbPolicyId"></s:hidden>
		<s:hidden name="operation"></s:hidden>
		<s:hidden name="strOrg"></s:hidden>
		<s:hidden name="strLevel"></s:hidden>
		<s:hidden name="userscreen" id="userscreen"/>
		<s:hidden name="navigationId" id="navigationId"/>
		<s:hidden name="toPage" id="toPage"/>
		
		<table class="table table_no_border">
			<tr>
				<td class="alignRight" style="width: 40%;">Default Policy:</td> 
				<td><s:checkbox name="mobileDefaultPolicy" value="mobileDefaultPolicyValue"></s:checkbox></td>
			</tr>
			<tr>
				<td class="alignRight">Limit Type:<sup>*</sup></td>
				<td>
					<s:select theme="simple" name="mobileLimitType" id="mobileLimitType" list="#{'1':'No Limit','2':'Actual'}" cssClass="validateRequired form-control" onchange="checkMobileLimitType();"/>
				</td>
			</tr>
			<tr id="trMobileLimit" style="display: <%=strDisplay %>;">
				<td class="alignRight">Limit:<sup>*</sup></td> 
				<td><s:textfield name="mobileLimit" id="mobileLimit" cssClass="validateRequired form-control" onkeypress="return isNumberKey(event)" cssStyle="text-align:right; width:72px;"></s:textfield></td>
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