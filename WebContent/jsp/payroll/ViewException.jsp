<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.select.FillUserStatus"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%
	UtilityFunctions uF = new UtilityFunctions();
	String absent = (String) request.getAttribute("absent");
	String nExceptionCnt = (String) request.getAttribute("nExceptionCnt");
%>
<table border="0" class="table">
	<tr>
		<td class="txtlabel">Leaves not applied:</td>
		<td><%=uF.parseToDouble(absent) %>&nbsp;<a target="_blank" href="EmployeeLeaveEntry.action" title="Apply Leave" style="color: green;">Apply Leave</a> </td>
	</tr>

	<tr>
		<td class="txtlabel">Exceptions not approved:</td>
		<td><%=nExceptionCnt %>&nbsp;<a target="_blank" href="UpdateClockEntries.action" title="Exceptions" style="color: green;">Exceptions</a> </td>
	</tr>

</table>
	

