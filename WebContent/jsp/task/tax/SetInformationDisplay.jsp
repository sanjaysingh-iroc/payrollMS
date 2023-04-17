<%@page import="com.konnect.jpms.util.IConstants" %>
<%@page import="com.konnect.jpms.util.UtilityFunctions" %>
<%@page import="com.konnect.jpms.select.FillUserStatus" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<script>

$(document).ready( function () {
	jQuery("#formSetInformationDisplay").validationEngine();
});	

</script>

<%
	UtilityFunctions uF = new UtilityFunctions();
	String strAttendFromTimesheetDetail = (String)request.getAttribute("strAttendFromTimesheetDetail");
	String strAttendFromTimesheetDetailCheck = "";
	if(uF.parseToBoolean(strAttendFromTimesheetDetail)) {
		strAttendFromTimesheetDetailCheck = "checked";
	}
	String strAttendFromAttendDetail = (String)request.getAttribute("strAttendFromAttendDetail");
	String strAttendFromAttendDetailCheck = "";
	if(uF.parseToBoolean(strAttendFromAttendDetail)) {
		strAttendFromAttendDetailCheck = "checked";
	}
%>

<s:form theme="simple" id="formSetInformationDisplay" action="SetInformationDisplay" method="POST" cssClass="formcss">
	<s:hidden name="operation" value="U"></s:hidden>
	<s:hidden name="dataType"></s:hidden>
	<s:hidden name="userscreen" />
	<s:hidden name="navigationId" />
	<s:hidden name="toPage" />
		
	<table class="table table_bordered" style="width: 95%; font-size: 12px;">
	<s:if test="dataType!=null && dataType=='InfoDisp'">
		<tr>
			<td class="txtlabel alignRight" style="width: 250px;"> Display only Team Assigned:</td>
			<td><s:select name="strOnlyTeam" id="strOnlyTeam" list="#{'1':'Yes', '0':'No'}" cssStyle="width: 70px !important;" /></td>  
		</tr>
	 
	 	<tr>
			<td class="txtlabel alignRight" style="width: 250px;"> Display Resource Cost in Project: </td>
			<td><s:select name="strCost" id="strCost" list="#{'1':'Yes', '0':'No'}" cssStyle="width: 70px !important;" /></td>  
		</tr>
		
		<tr>
			<td class="txtlabel alignRight" style="width: 250px;"> Display Resource Rate in Project: </td>
			<td><s:select name="strRate" id="strRate" list="#{'1':'Yes', '0':'No'}" cssStyle="width: 70px !important;" /></td>
		</tr>
		</s:if>
		<s:elseif test="dataType!=null && dataType=='SnapTime'">
		<tr>
			<td class="txtlabel alignRight" style="width: 250px;"> Snapshot Time Frequency: </td>
			<td><s:select name="strFreq" id="strFreq" list="#{'10':'10 minutes', '20':'20 minutes','40':'40 minutes','60':'60 minutes','80':'80 minutes','100':'100 minutes','120':'120 minutes'}" cssStyle="width: 100px !important;" /></td>
		</tr>
		</s:elseif>
		<s:elseif test="dataType!=null && dataType=='Attendance'">
		<tr>
			<td class="txtlabel alignRight" style="width: 250px;"> Attendance data from attendance details: </td>
			<td>
				<input type="radio" name="strAttendFromAttendDetail" id="strAttendFromAttendDetail" value="AD" <%=strAttendFromAttendDetailCheck %>>
			</td>
		</tr>
		<tr>
			<td class="txtlabel alignRight"> OR </td>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td class="txtlabel alignRight" style="width: 250px;"> Attendance data from timesheet details: </td>
			<td>
				<input type="radio" name="strAttendFromAttendDetail" id="strAttendFromAttendDetail" value="TD" <%=strAttendFromTimesheetDetailCheck %>>
			</td>
		</tr>
		</s:elseif>
		<tr>
			<td colspan="4" align="center">
				<s:submit cssClass="btn btn-primary" value="Update" id="btnOk"/>
			</td>
		</tr>
	</table>
	
</s:form>
