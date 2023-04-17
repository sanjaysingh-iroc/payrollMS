<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>


<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<div class="aboveform">
<h4>Timesheet Approval/Denial List</h4>

<% 
String str = (String)request.getParameter("q");
%>


<%-- <div class="link1 <%=(((str==null || ( str!=null && str.equalsIgnoreCase("1")))?"link2":"")) %>"><a href="?q=1">Approved</a></div>
<div class="link1 <%=(((str!=null && str.equalsIgnoreCase("-1"))?"link2":"")) %>"><a href="?q=-1">Denied</a></div>  --%> 
<div class="link1 <%=(((str==null || ( str!=null && str.equalsIgnoreCase("1")))?"link2":"")) %>"><a href="?q=1"><input type="button" class="input_button"  value="Approved"></a></div>
<div class="link1 <%=(((str!=null && str.equalsIgnoreCase("-1"))?"link2":"")) %>"><a href="?q=-1"><input type="button" class="input_button"  value="Denied"></a></div>


<display:table name="reportList" cellspacing="1" class="itis" export="true" pagesize="15" id="lt" requestURI="ClockEntriesReport.action">
	
	<display:setProperty name="export.excel.filename" value="TimeSheetApprovalReport.xls" />
	<display:setProperty name="export.xml.filename" value="TimeSheetApproval.xml" />
	<display:setProperty name="export.csv.filename" value="TimeSheetApproval.csv" />
	
	
	<display:column title="Emp Name" sort="true"><%=((java.util.List) pageContext.getAttribute("lt")).get(0)%></display:column>
	<display:column title="Date"><%=((java.util.List) pageContext.getAttribute("lt")).get(1)%></display:column>
	<display:column title="Day"><%=((java.util.List) pageContext.getAttribute("lt")).get(2)%></display:column>	
	<display:column title="In/Out"><%=((java.util.List) pageContext.getAttribute("lt")).get(3)%></display:column>
	<display:column title="Emp Reason" sort="true"><%=((java.util.List) pageContext.getAttribute("lt")).get(4)%></display:column>
	<display:column title="Manager's Comment"><%=((java.util.List) pageContext.getAttribute("lt")).get(5)%></display:column>
	<display:column title="Early/Late"><%=((java.util.List) pageContext.getAttribute("lt")).get(6)%></display:column>	
	
</display:table></div>

