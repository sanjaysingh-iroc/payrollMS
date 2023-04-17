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
$("#submitButton").click(function(){
	$("#formAddReimbursementPartOfCTC").find('.validateRequired').filter(':hidden').prop('required',false);
    $("#formAddReimbursementPartOfCTC").find('.validateRequired').filter(':visible').prop('required',true);
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
	<s:form  theme="simple" name="formAddReimbursementPartOfCTC" id="formAddReimbursementPartOfCTC" action="AddReimbursementPartOfCTC" method="POST" cssClass="formcss">
		<s:hidden name="reimbCTCId"></s:hidden>
		<s:hidden name="operation"></s:hidden>
		<s:hidden name="strOrg"></s:hidden>
		<s:hidden name="strLevel"></s:hidden>
		<s:hidden name="userscreen" id="userscreen"/>
		<s:hidden name="navigationId" id="navigationId"/>
		<s:hidden name="toPage" id="toPage"/>
		
		<table class="table table_no_border">
			<tr>
				<td class="alignRight">Reimbursement Code:<sup>*</sup></td> 
				<td><s:textfield name="reimbCode" id="reimbCode" cssClass="validateRequired"></s:textfield></td>
			</tr>
			<tr>
				<td class="alignRight">Reimbursement Name:<sup>*</sup></td> 
				<td><s:textfield name="reimbName" id="reimbName" cssClass="validateRequired"></s:textfield></td>
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

