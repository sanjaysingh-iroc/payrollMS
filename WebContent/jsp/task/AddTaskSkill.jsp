<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script type="text/javascript">
$(document).ready( function () {
	$("#formID_0").click(function(){
		$(".validateRequired").prop('required',true);
	});
});
</script>

	<%
		String operation = (String) request.getAttribute("operation");
	%>
		<s:form id="formID" action="AddTaskSkill" method="post" theme="simple">
			<s:hidden name="service_porject_id" />
			<s:hidden name="ID" />
			<s:hidden name="userscreen" />
			<s:hidden name="navigationId" />
			<s:hidden name="toPage" />
			<% if (operation != null) { %>
			<s:hidden name="operation" value="A" />
			<% } %>
			<table border="0" class="table table_no_border">
				<tr>
					<th class="txtlabel alignRight">Skill Name:<sup>*</sup></th>
					<td>
						<s:select name="skill" list="skillList" listKey="skillsId" listValue="skillsName"
							headerKey="" headerValue="Select Skill" cssClass="validateRequired"/>
					</td>
				</tr>
				<tr>	
					<th class="txtlabel alignRight">Skill Description:<sup>*</sup></th>
					<td><s:textfield name="skilldesc" cssClass="validateRequired"/></td>
				</tr>	
				<tr>
					<td colspan="4" align="center"><s:submit value="Save Skill" cssClass="btn btn-primary" /></td>
				</tr>
			</table>

		</s:form>
	