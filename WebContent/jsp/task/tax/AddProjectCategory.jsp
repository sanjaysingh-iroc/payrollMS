<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.select.FillUserStatus"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<script>
$(document).ready( function() {
	$("#btnOk").click(function() {
		$(".validateRequired").prop('required', true);
	});
	
});

</script>

<%
UtilityFunctions uF = new UtilityFunctions();
String proCategoryId = (String)request.getAttribute("proCategoryId"); 
String operation = (String)request.getAttribute("operation");
%>

<s:form theme="simple" id="formProjectCategory" action="AddProjectCategory" method="POST" cssClass="formcss">
	<s:hidden name="proCategoryId"></s:hidden>
	<s:hidden name="userscreen" />
	<s:hidden name="navigationId" />
	<s:hidden name="toPage" />
	
	<table class="table table_bordered">
		<tr>
			<td class="txtlabel alignRight"><label for="organisation_Name">Organisation:<sup>*</sup></label><br/></td>
			<td>
			<% if(operation != null && operation.equals("E")) { %>
				<s:hidden name="strOrg"></s:hidden>
				<%=(String)request.getAttribute("strOrgName") %>	
			<% } else { %>
			<s:select list="orgList" name="strOrg" id="strOrg" listKey="orgId" listValue="orgName" headerKey="" headerValue="Select Organisation"
			 cssClass="validateRequired"></s:select>
			 <% } %>
			 </td> 
		</tr>
		
		<tr>
			<td class="txtlabel alignRight">Category<sup>*</sup>:</td>
			<td><s:textfield name="proCategory" id="proCategory" cssClass="validateRequired" /></td>  
		</tr>
		<tr>
			<td class="txtlabel alignRight">Description:</td>
			<td><s:textarea name="proDescription" rows="2" cols="22"></s:textarea></td>  
		</tr>
	 
		<tr>
			<td colspan="2" align="center">
			<% if(uF.parseToInt(proCategoryId) > 0) { %>
				<s:submit cssClass="btn btn-primary" value="Update" id="btnOk"/>
			<% } else { %>
				<s:submit cssClass="btn btn-primary" value="Add" id="btnOk"/>
			<% } %>
			</td>
		</tr>
	</table>
	
</s:form>
