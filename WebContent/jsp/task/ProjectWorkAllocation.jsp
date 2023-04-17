<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<%
	CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
	List<List<String>> alOuter = (List<List<String>>)request.getAttribute("alOuter");
%>

<h4>Task Summary for <%=((request.getAttribute("strEmpName")!=null) ? request.getAttribute("strEmpName") : "") %></h4>

<table class="table table-bordered">
	<tr>
		<th style="width:40%">Task Name</th>
		<th style="width:14%">Start Date</th>
		<th style="width:14%">Deadline</th>
		<th style="width:11%">Estimated Time</th>
		<th style="width:11%">Actual Time</th>
		<th style="width:10%">Completion Status</th>
	</tr>


	<%for(int i=0; i<alOuter.size(); i++) { 
		List<String> alInner = (List<String>)alOuter.get(i);
	%>
	<tr>
		<td>
		<%=alInner.get(0) %><br>
		<span style="font-size: 12px; font-style: italic; font-weight: bold;">Project: </span><span style="font-size: 10px; font-style: italic;"><%=alInner.get(1) %></span><br/>
		<span style="font-size: 12px; font-style: italic; font-weight: bold;">Client: </span><span style="font-size: 10px; font-style: italic;"><%=alInner.get(2) %></span></td>
		<td class="alignRight" valign="top"><%=alInner.get(3) %></td>
		<td class="alignRight" valign="top"><%=alInner.get(4) %></td>
		<td class="alignRight" valign="top"><%=alInner.get(5) %></td>
		<td class="alignRight" valign="top"><%=alInner.get(6) %></td>
		<td class="alignRight" valign="top"><%=alInner.get(7) %></td>
	</tr>
	<% } %>

</table>

