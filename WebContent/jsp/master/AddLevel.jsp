<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.select.FillUserStatus"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script>

$(document).ready( function () {
	$("#btnAddNewLevelOk").click(function(){
		$(".validateRequired").prop('required',true);
	});
});	

</script>

<%
UtilityFunctions uF = new UtilityFunctions();
String levelId = (String)request.getAttribute("levelId"); %>

	<s:form theme="simple" id="formAddNewLevel" action="AddLevel" method="POST" cssClass="formcss">
	<input type="hidden" name="URI" value="<%=request.getParameter("URI") %>" />
	<s:hidden name="levelId"></s:hidden>
	<s:hidden name="userscreen" />
	<s:hidden name="navigationId" />
	<s:hidden name="toPage" />
	
	<table class="table table_no_border">
		<tr>
			<th class="txtlabel alignRight"><label for="organisation_Name">Organisation:<sup>*</sup></label><br/></th>
			<td><s:select list="orgList" name="strOrg" id="strOrg" listKey="orgId" listValue="orgName" headerKey="" headerValue="Select Organisation"
			 cssClass="validateRequired" ></s:select></td> 
		</tr>
		
		<tr>
			<th class="txtlabel alignRight">Level Code:<sup>*</sup></th>
			<td>
				<s:textfield name="levelCode" id="levelCode" cssClass="validateRequired" /> 
				<span class="hint">Level Code<span class="hint-pointer">&nbsp;</span></span>
			</td>  
		</tr>
	 
		<tr>
			<th class="txtlabel alignRight">Level Name:<sup>*</sup></th>
			<td>
				<s:textfield name="levelName" id="levelName" cssClass="validateRequired" /> 
				<span class="hint">Level Name<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>
		
		<tr>
			<th class="txtlabel alignRight">Weightage:</th>
			<td>
				<s:select theme="simple" name="strWeightage" id="strWeightage" list="weightageList" listKey="weightageId" listValue="weightageName" headerKey="" headerValue="Select Weightage" />
			</td>
		</tr>
		
		<tr>
			<th class="txtlabel alignRight" valign="top">Level Description:</th>
			<td>
				<s:textarea name="levelDesc" id="levelDesc" rows="3" cols="22" /> 
				<span class="hint">Level Description<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>

		<tr>
			<th class="txtlabel alignRight">Flat TDS Deduction:</th>
			<td>
				<s:checkbox name="isFlatTDSDeduction" /> 
				<span class="hint">Flat TDS Deduction<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>
		
		<tr>
			<td colspan="2" align="center">
			<% if(uF.parseToInt(levelId) > 0) { %>
				<s:submit cssClass="btn btn-primary" value="Update" id="btnAddNewLevelOk" />
			<% } else { %>
				<s:submit cssClass="btn btn-primary" value="Save" id="btnAddNewLevelOk" />
			<% } %>	
			</td>
		</tr>
	</table>
	
</s:form>
