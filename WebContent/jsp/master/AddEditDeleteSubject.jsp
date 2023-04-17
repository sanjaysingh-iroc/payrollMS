<%@page import="java.util.Iterator"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s" %>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>

<script>
	$(document).ready( function () {
		$("#btnAddNewRowOk").click(function(){
			$(".validateRequired").prop('required', true);
		});
	});
	
</script>


	<% 
		UtilityFunctions uF = new UtilityFunctions();
		String classId = (String)request.getAttribute("classId");
		String sbDivLevelList = (String)request.getAttribute("sbDivLevelList");
	%>

		<s:form theme="simple" action="AddEditDeleteSubject" method="POST" cssClass="formcss" id="formAddEditDeleteSubject" name="formAddEditDeleteSubject">
			<s:hidden name="subjectId" />
			<s:hidden name="userscreen"></s:hidden>
			<s:hidden name="navigationId"></s:hidden>
			<s:hidden name="toPage"></s:hidden>

			<table class="table table_no_border">
				<tr><td colspan=2><s:fielderror/></td></tr>
				<tr>
					<th class="txtlabel alignRight">Select Organization:<sup>*</sup></th>
					<td><s:select list="orgList" name="strOrg" listKey="orgId" listValue="orgName" headerKey="" headerValue="Select Organisation"
					 cssClass="validateRequired"></s:select></td> 
				</tr>
				
				<tr>
					<th class="txtlabel alignRight">Subject Name:<sup>*</sup></th>
					<td><s:textfield name="subjectName" cssClass="validateRequired"/><span class="hint">Name of the subject.<span class="hint-pointer">&nbsp;</span></span></td> 
				</tr>
				
				<tr>
					<th class="txtlabel alignRight" valign="top">Description:</th>
					<td><s:textarea name="subjectDescription" rows="3" cols="22"/><span class="hint">Description of the subject.<span class="hint-pointer">&nbsp;</span></span></td> 
				</tr>

				<tr>
					<td colspan="2" align="center">
					<% if(uF.parseToInt(classId) > 0) { %>
						<s:submit cssClass="btn btn-primary" value="Update" id="btnAddNewRowOk"/>
					<% } else { %>
						<s:submit cssClass="btn btn-primary" value="Save" id="btnAddNewRowOk"/>
					<% } %>	
					</td>
				</tr>
				
			</table>
		
		</s:form>

