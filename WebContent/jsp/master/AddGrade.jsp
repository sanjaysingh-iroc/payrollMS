<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.select.FillDesig"%>
<%@page import="com.konnect.jpms.select.FillLevel"%>
<%@page import="com.konnect.jpms.select.FillUserStatus"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>




 


<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script>

$(document).ready( function () {
	$("#btnAddNewGradeOk").click(function(){
		$(".validateRequired").prop('required',true);
	});
});	
</script>

<%
UtilityFunctions uF = new UtilityFunctions();
String gradeId = (String)request.getAttribute("gradeId"); %>
<s:form theme="simple" name="formAddNewGrade" id="formAddNewGrade" action="AddGrade" method="POST" cssClass="formcss">

	<input type="hidden" name="gradeDesig" value="<%=request.getParameter("param") %>" />
	<s:hidden name="gradeId"></s:hidden>
	<s:hidden name="orgId" />
	<s:hidden name="userscreen" />
	<s:hidden name="navigationId" />
	<s:hidden name="toPage" />
	
		<table class="table table_no_border">
		
		<tr>
			<th class="txtlabel alignRight">Grade Code:<sup>*</sup></th>
			<td>
				<s:textfield name="gradeCode" id="gradeCode" cssClass="validateRequired" /> 
				<span class="hint">Grade Code<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>
	 
		<tr>
			<th class="txtlabel alignRight">Grade Name:<sup>*</sup></th>
			<td>
				<s:textfield name="gradeName" id="gradeName" cssClass="validateRequired" /> 
				<span class="hint">Grade Name<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>
		
		<tr>
			<th class="txtlabel alignRight" valign="top">Grade Description:</th>
			<td>
				<s:textarea name="gradeDesc" id="gradeDesc" rows="3" cols="22"/> 
				<span class="hint">Grade Description<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>

		

		<tr>
			<td></td>
			<td>
			<% if(uF.parseToInt(gradeId) > 0) { %>
				<s:submit cssClass="btn btn-primary" value="Update" id="btnAddNewGradeOk" />
			<% } else { %>
				<s:submit cssClass="btn btn-primary" value="Save" id="btnAddNewGradeOk" />
			<% } %>	 
			</td>
		</tr>


	</table>
</s:form>

