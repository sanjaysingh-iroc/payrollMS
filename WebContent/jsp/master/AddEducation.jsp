<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.select.FillAmountType"%>
<%@page import="com.konnect.jpms.select.FillLevel"%>
<%@page import="com.konnect.jpms.select.FillUserStatus"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<%-- <script type="text/javascript" src="scripts/charts/jquery.min.js"></script> --%>

<script type="text/javascript">

$(document).ready( function () {
	$("#btnAddNewRowOk").click(function(){
		$(".validateRequired").prop('required',true);
	});
});	

</script>

 
<%
UtilityFunctions uF = new UtilityFunctions();
String educationId = (String)request.getAttribute("educationId"); %>

<s:form theme="simple" id="formAddNewRow" action="AddEducation" method="POST" theme="simple" cssClass="formcss">
	<s:hidden name="educationId"></s:hidden>
	<s:hidden name="userscreen" />
	<s:hidden name="navigationId" />
	<s:hidden name="toPage" />
	
	<table class="table table_no_border">
		<tr>
			<td class="txtlabel alignRight">Organisation :<sup>*</sup></td>
			<td><s:select list="orgList" name="strOrg" id="strOrg" listKey="orgId" listValue="orgName" headerKey="" headerValue="Select Organisation" cssClass="validateRequired" ></s:select></td> 
		</tr>
		
		<tr>
			<td class="txtlabel alignRight">Education :<sup>*</sup></td>
			<td><s:textfield name="educationName"  cssClass="validateRequired"/> 
				<span class="hint">Education Name<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight">Weightage :<sup>*</sup></td>
			<td><s:select theme="simple" name="strWeightage" id="strWeightage"  cssClass="validateRequired" list="weightageList" listKey="weightageId" listValue="weightageName" headerKey="" headerValue="Select Weightage" /></td>
		</tr>
	
		<tr>
			<td class="txtlabel alignRight" valign="top">Education Description :</td>
			<td><s:textarea name="educationDescription" rows="3" cols="22" /> 
				<span class="hint">Education Description<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>

		<tr>
			<td></td>
			<td>
			<% if(uF.parseToInt(educationId) > 0) { %>
				<s:submit cssClass="btn btn-primary" value="Update" id="btnAddNewRowOk" />
			<% } else { %>
				<s:submit cssClass="btn btn-primary" value="Save" id="btnAddNewRowOk" />
			<% } %> 
			</td>
		</tr>
	</table>
</s:form>

