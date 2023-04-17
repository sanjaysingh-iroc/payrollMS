<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<link rel="stylesheet" type="text/css" href="css/select/jquery-ui.css" />
<link rel="stylesheet" type="text/css" href="css/select/jquery.multiselect.css" />
<script type="text/javascript" src="scripts/jquery-ui.min.js"> </script>
<script type="text/javascript" src="scripts/select/jquery.multiselectfilter.js"></script>
<script type="text/javascript" src="scripts/select/jquery.multiselect.js"></script>
<script type="text/javascript">
$(document).ready( function () {
	$("#formID_0").click(function(){
		$(".validateRequired").prop('required',true);
	});
	$("#service").multiselect({
		noneSelectedText: 'Select SBU (required)'
	}).multiselectfilter();
});
</script>

<%
	String operation=(String)request.getAttribute("operation");
%>
	<s:form id="formID" action="AddTaskService" method="post" theme="simple">
		<%if(operation!=null){ %>
		<s:hidden name="operation" value="A"/>
		<s:hidden name="ID"/>
		
		<%} %> 
		<s:hidden name="userscreen" />
		<s:hidden name="navigationId" />
		<s:hidden name="toPage" />
		<table class="table table_no_border">
			<tr>
				<th class="txtlabel alignRight">Service Name:<sup>*</sup></th>
				<td><s:textfield name="serviceName" cssClass="validateRequired"></s:textfield> </td>
			</tr>
			<tr>
				<th class="txtlabel alignRight">Service Description:</th>
				<td><s:textarea name="sdesc" rows="4" cols="22"></s:textarea></td>
			</tr>
			<tr>
				<th class="txtlabel alignRight">Select SBU:<sup>*</sup></th>
				<td><s:select name="service" id="service" listKey="serviceId" headerKey="0" headerValue="All SBU" listValue="serviceName"
						list="serviceList" key="" cssStyle="width:140px" cssClass="validateRequired" multiple="true" size="3" value="serviceID"/></td>
			</tr>
			<tr>
				<td class="txtlabel alignRight">&nbsp;</td>
				<td class="txtlabel"><s:submit value="Save Project Service" cssClass="btn btn-primary"/> </td>
			</tr>
		</table>
			
	</s:form>
