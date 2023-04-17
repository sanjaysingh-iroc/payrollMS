<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.select.FillWLocation"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script type="text/javascript">
	$(document).ready( function () {
		$("#formAddNewRow").click(function(){
			$(".validateRequired").prop('required',true);
		});
	});
	
</script>

<%
	String operation=(String)request.getAttribute("operation");
%>

<s:form theme="simple" action="AddDomain" method="POST" cssClass="formcss" id="formAddNewRow">

	<%if(operation!=null){ %>
		<s:hidden name="operation" value="A"/>
		<s:hidden name="ID"/>
		
	<%} %>
	<s:hidden name="userscreen" />
	<s:hidden name="navigationId" />
	<s:hidden name="toPage" />
	
	<table class="table table_no_border">
		<tr>
			<th class="txtlabel alignRight">Domain Code:<sup>*</sup></th>
			<td><s:textfield name="domainCode" cssClass="validateRequired"/></td> 
		</tr>
		<tr>
			<th class="txtlabel alignRight">Domain Name:<sup>*</sup></th>
			<td><s:textfield name="domainName" cssClass="validateRequired"></s:textfield> </td>
		</tr>
		<tr>
			<th class="txtlabel alignRight">Domain Description:</th>
			<td><s:textarea name="domainDesc" rows="4" cols="22"></s:textarea></td>
		</tr>
		<tr>
			<td class="txtlabel alignRight">&nbsp;</td>
			<td class="txtlabel"><s:submit value="Save" cssClass="btn btn-primary"/> </td>
		</tr>
	</table>
	
</s:form>
