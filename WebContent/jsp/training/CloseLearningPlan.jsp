<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.*"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>


<%
	UtilityFunctions uF = new UtilityFunctions();
	String closeReason = (String) request.getAttribute("closeReason");
	//String view = (String) request.getAttribute("view");
	String isClose = (String) request.getAttribute("IS_CLOSE");
%>
<div id="closeLearningPlanDiv">
<% if(uF.parseToBoolean(isClose)) { %>
	<table border="0" class="formcss" style="width: 97%; float: left">
			<tr>
				<td colspan=2><s:fielderror />
				</td>
			</tr>
			<tr>
				<td class="txtlabel alignRight" style="width: 50px;" valign="top">Reason:</td>
				<td><%=(closeReason==null ? "" : closeReason)%></td>
			</tr>
		</table>
	<% } else { %>
	<s:form id="formID" name="frmCloseLearningPlan" theme="simple" action="CloseLearningPlan" method="POST" cssClass="formcss" enctype="multipart/form-data">

		<s:hidden name="lPlanId"></s:hidden> 
		<s:hidden name="orgID"></s:hidden> 
       	<s:hidden name="wlocID"></s:hidden>
       	<s:hidden name="desigID"></s:hidden>
       	<s:hidden name="checkStatus"></s:hidden>
       	<s:hidden name="fdate"></s:hidden>
       	<s:hidden name="tdate"></s:hidden>
       	<s:hidden name="frmPage"></s:hidden>
       	<s:hidden name="operation" value="update"></s:hidden>
       
		<table border="0" class="formcss" style="width: 97%; float: left">
			<tr>
				<td colspan=2><s:fielderror />
				</td>
			</tr>
			<tr>
				<td class="txtlabel alignRight" valign="top">Reason:<sup>*</sup></td>
				<td><textarea name="closeReason" id="closeReason" cols="26" class="validateRequired" rows="4"><%=(closeReason==null ? "" : closeReason)%></textarea></td>
			</tr>
			<tr>
				<td colspan="2" align="center">
					 <s:submit cssClass="btn btn-primary" value="Submit" align="center" />
				</td>
			</tr>
		</table>
	</s:form>
	<% } %>
</div>
<script>
$(function(){
	
	$("#formID").find('.validateRequired').filter(':hidden').prop('required',false);
    $("#formID").find('.validateRequired').filter(':visible').prop('required',true);
});
	$("#formID").submit(function(event){
		event.preventDefault()
		var form_data = $("#formID").serialize();
		$.ajax({
			type:'POST',
			url:'CloseLearningPlan.action',
			data:form_data,
			success:function(result){
				 $("#divResult").html(result);
			}
		});
	});
</script>


