
<%@page import="com.konnect.jpms.select.FillUserStatus"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<script>
$(function(){
	$("body").on("click","#btnAddNewRowOk",function(){
    	$('.validateRequired').filter(':hidden').prop('required', false);
		$('.validateRequired').filter(':visible').prop('required', true);
    });
});

</script>


	<s:form theme="simple" id="formAddNewRow" action="AddUpdateRetirementPolicy" method="POST" cssClass="formcss">
		<table class="table">
			<s:hidden name="strOrgId" id="strOrgId"></s:hidden>
			<s:hidden name="userscreen" />
			<s:hidden name="navigationId" />
			<s:hidden name="toPage" />
			<s:hidden name="operation" />
			<s:hidden name="strVdaRateId" />
			<tr>
				<td valign="top" class="txtlabel alignRight">Organization:</td>
				<td>
				<% String strOrgName = (String)request.getAttribute("strOrgName");%>
					<%=strOrgName %>
				</td>
			</tr>
			<tr>
				<td valign="top" class="txtlabel alignRight">Retirement Age:<sup>*</sup></td>
				<td><s:textfield name="retirementAge" id="retirementAge" cssClass="validateRequired"/></td>
			</tr>
			
			<tr>
				<td colspan="2" align="center">
					<s:submit cssClass="btn btn-primary" value="Submit" id="btnAddNewRowOk" /> 
				</td>
			</tr>
		</table>
		
	</s:form>

