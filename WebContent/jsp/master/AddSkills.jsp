<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.select.FillAmountType"%>
<%@page import="com.konnect.jpms.select.FillLevel"%>
<%@page import="com.konnect.jpms.select.FillUserStatus"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script type="text/javascript">

$(document).ready( function () {
	$("#btnAddNewRowOk").click(function(){
		$(".validateRequired").prop('required',true);
	});
});	
</script>

<%
UtilityFunctions uF = new UtilityFunctions();
String skillId = (String)request.getAttribute("skillId"); %>

<s:form theme="simple" id="formAddNewRow" action="AddSkills" method="POST" theme="simple" cssClass="formcss">
	<s:hidden name="skillId"></s:hidden>
	<s:hidden name="userscreen" />
	<s:hidden name="navigationId" />
	<s:hidden name="toPage" />
	<table border="0" class="table">
		<tr>
			<td class="txtlabel alignRight">Organisation<sup>*</sup>:</td>
			<td><s:select list="orgList" name="strOrg" id="strOrg" listKey="orgId" listValue="orgName" headerKey="" headerValue="Select Organisation"
			 cssClass="validateRequired" ></s:select></td> 
		</tr>
		
		<tr>
			<td class="txtlabel alignRight">Skill Name<sup>*</sup>:</td>
			<td>
				<s:textfield name="skilName"  cssClass="validateRequired"/> 
				<span class="hint">Skill Name<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>
	
		<tr>
			<td class="txtlabel alignRight" valign="top">Skill Description:</td>
			<td>
				<s:textarea name="skilDescription" rows="3" cols="22" /> 
				<span class="hint">Skill Description<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>

		<tr>
			<td colspan="2" align="center">
			<% if(uF.parseToInt(skillId) > 0) { %>			
				<s:submit cssClass="btn btn-primary" value="Update" id="btnAddNewRowOk" />
			<%} else { %>
				<s:submit cssClass="btn btn-primary" value="Save" id="btnAddNewRowOk" />
			<% } %> 
			</td>
		</tr>

	</table>
</s:form>

