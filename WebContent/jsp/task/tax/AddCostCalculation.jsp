<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.select.FillUserStatus"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<script>

$(document).ready( function () {
	jQuery("#formAddCostCal").validationEngine();
	
	var val = '<%=(String)request.getAttribute("costCalType") %>';
	checkCalType(val, 'OL');
	
});	


	function checkCalType(val, type) {
		if(val == '3') {
			document.getElementById("fixDaysTR").style.display = "table-row";
			document.getElementById("fixArticalDaysTR").style.display = "table-row";
			if(type == '') {
				document.getElementById("fixdays").value = "";
				document.getElementById("fixArticaldays").value = "";
			}
		} else {
			if(type == '') {
				document.getElementById("fixdays").value = "";
				document.getElementById("fixArticaldays").value = "";
			}
			document.getElementById("fixDaysTR").style.display = "none";
			document.getElementById("fixArticalDaysTR").style.display = "table-row";
		}
	}

	function isNumberKey(evt) {
	   var charCode = (evt.which) ? evt.which : event.keyCode;
	   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46)
	      return false;
	
	   return true;
	}

</script>

<%
UtilityFunctions uF = new UtilityFunctions();
String costCalId = (String)request.getAttribute("costCalId"); 
String costCalType = (String)request.getAttribute("costCalType");
String operation = (String)request.getAttribute("operation");
String type1 = "checked";
String type2 = "";
if(uF.parseToInt(costCalType) == 2) {
	type1 = "";
	type2 = "checked";
}
%>

<s:form theme="simple" id="formAddCostCal" action="AddCostCalculation" method="POST" cssClass="formcss">
	<s:hidden name="costCalId"></s:hidden>
	<s:hidden name="userscreen" />
	<s:hidden name="navigationId" />
	<s:hidden name="toPage" />
	
	<table class="table table_bordered" style="width: 95%;">
		<tr>
			<td class="txtlabel alignRight"><label for="organisation_Name">Organisation:<sup>*</sup></label><br/></td>
			<td>
			<% if(operation != null && operation.equals("E")) { %>
				<s:hidden name="strOrg"></s:hidden>
				<%=(String)request.getAttribute("strOrgName") %>	
			<% } else { %>
			<s:select list="orgList" name="strOrg" id="strOrg" listKey="orgId" listValue="orgName" headerKey="" headerValue="Select Organisation"
			 cssClass="validateRequired"></s:select>
			 <% } %>
			 </td> 
		</tr>
		
		<tr>
			<td class="txtlabel alignRight">Basis of Cost Calculation:</td>
			<td>
				<input type="hidden" name="monthday" value="Actual month days" />
				<input type="hidden" name="workingday" value="Actual working days" />
				<input type="hidden" name="fixedday" value="Fixed days" />
				<select name="costCalType" id="costCalType" onchange="checkCalType(this.value, '');">
					<option value="1" <%=(uF.parseToInt(costCalType) == 1) ? "selected" : "" %>>Actual month days</option>
					<option value="2" <%=(uF.parseToInt(costCalType) == 2) ? "selected" : "" %>>Actual working days</option>
					<option value="3" <%=(uF.parseToInt(costCalType) == 3) ? "selected" : "" %>>Fixed days</option>
				</select>
			</td>
		</tr>
		<tr id="fixDaysTR" style="display: <%=(uF.parseToInt(costCalType) == 3) ? "table-row" : "none" %>;">
			<td class="txtlabel alignRight">Enter Days:</td>
			<td><s:textfield name="fixdays" id="fixdays" onkeypress="return isNumberKey(event)" /></td>
		</tr>
		<tr id="fixArticalDaysTR" style="display: <%=(uF.parseToInt(costCalType) == 3) ? "table-row" : "none" %>;">
			<td class="txtlabel alignRight">Enter Artical Days:</td>
			<td><s:textfield name="fixArticaldays" id="fixArticaldays" onkeypress="return isNumberKey(event)" /></td>
		</tr>
	 
		<tr>
			<td colspan="2" align="center">
			<% if(uF.parseToInt(costCalId) > 0) { %>
				<s:submit cssClass="btn btn-primary" value="Update" id="btnOk"/>
			<% } else { %>
				<s:submit cssClass="btn btn-primary" value="Add" id="btnOk"/>
			<% } %>
			</td>
		</tr>
	</table>
	
</s:form>
