<%@page import="com.konnect.jpms.select.FillUserStatus"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script>
$(document).ready( function () {
	$("#btnAddNewRowOk").click(function(){
		$(".validateRequired").prop('required', true);
	});
});
</script> 


<s:form theme="simple" id="formAddNewRow" action="AddWlocationType" method="POST" cssClass="formcss">
<s:hidden name="wlocationTypeId"></s:hidden>
<s:hidden name="orgId"></s:hidden>
<s:hidden name="userscreen"></s:hidden>
<s:hidden name="navigationId"></s:hidden>
<s:hidden name="toPage"></s:hidden>
<%-- <input type="hidden" name="param" value="<%=request.getParameter("param")%>"/> --%>
	<table class="table table_no_border">
		
		<tr>
			<th class="txtlabel alignRight">Office Code:<sup>*</sup></th>
			<td>
				<s:textfield name="wlocationTypeCode" id="wlocationTypeCode" cssClass="validateRequired"/> 
				<span class="hint">Office Code<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>
	 
		<tr>
			<th class="txtlabel alignRight">Office Name:<sup>*</sup></th>
			<td> 
				<s:textfield name="wlocationTypeName" id="wlocationTypeName" cssClass="validateRequired"/> 
				<span class="hint">Office Name<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>
		
		<tr>
			<th class="txtlabel alignRight">Description:</th>
			<td>
				<s:textarea name="wlocationTypeDesc" id="wlocationTypeDesc" cols="22" /> 
				<span class="hint">Description<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>

		<tr>
			<td colspan="2" align="center">
				<s:submit cssClass="btn btn-primary" value="Submit" id="btnAddNewRowOk" /> 
			</td>
		</tr>

	</table>
	
</s:form>

