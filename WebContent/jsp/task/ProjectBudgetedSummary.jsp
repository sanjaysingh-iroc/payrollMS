<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<%
CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
List<List<String>> alReport = (List<List<String>>)request.getAttribute("alReport");
Map<String, List<List<String>>> hmProjectSummarySubTaskReport = (Map<String, List<List<String>>>) request.getAttribute("hmProjectSummarySubTaskReport");

String strBillingType = (String) request.getAttribute("strBillingType");
String strProjectName = (String) request.getAttribute("PROJECT_NAME");

%>

<h4>Budgeted Summary for <%=((strProjectName != null) ? strProjectName : "") %></h4>

<table class="table table-bordered">

<tr>
	<th>Task Name</th>
	<th>Resource Name</th>
	<th>Service</th>
	<th>Estimated Time<br/>(<%if(strBillingType != null && strBillingType.equals("H")) { %>
	hrs <% } else { %>
	days
	<% } %>)</th>
	<th>Cost/
	<%if(strBillingType != null && strBillingType.equals("H")) { %>
	Hr <% } else { %>
	Day
	<% } %><br/>(<%=request.getAttribute("SHORT_CURR")%>)</th>
	<th>Budgeted Cost<br/>(<%=request.getAttribute("SHORT_CURR")%>)</th>
</tr>


	<% for(int i=0; i<alReport.size(); i++) { 
		List<String> alInner = (List<String>)alReport.get(i);
	%>
	
	<%if(i==alReport.size()-1) { %>
	<tr style="border-top:2px solid #000fff">
	<% } else { %>
	<tr>
	<% } %>
	
		<td><%=alInner.get(1) %></td>
		<td><%=alInner.get(2) %></td>
		<td><%=alInner.get(3) %></td>
		<td class="alignRight padRight20" style="background-color: lightgreen"><%=alInner.get(4) %></td>
		<td class="alignRight padRight20" style="background-color: lightgreen"><%=alInner.get(5) %></td>
		<td class="alignRight padRight20" style="background-color: lightgreen"><%=alInner.get(6) %></td>
	</tr>
			<% 
			if(hmProjectSummarySubTaskReport != null) {
				List<List<String>> subTaskList = hmProjectSummarySubTaskReport.get(alInner.get(0));
			
			for(int j=0; subTaskList!=null && j<subTaskList.size(); j++) {
				List<String> innerList = subTaskList.get(j);
			%>
				<tr>
					<td><%=innerList.get(1) %> [ST]</td>
					<td><%=innerList.get(2) %></td>
					<td><%=innerList.get(3) %></td>
					<td class="alignRight padRight20" style="background-color: lightgreen">&nbsp;</td>
					<td class="alignRight padRight20" style="background-color: lightgreen">&nbsp;</td>
					<td class="alignRight padRight20" style="background-color: lightgreen">&nbsp;</td>
				</tr>
				<% } } %>
	<%} %>

</table>



