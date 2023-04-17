<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>


<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Employee Report" name="title"/>
</jsp:include>
  

 
  

<div id="printDiv" class="leftbox reportWidth">

<display:table name="reportList" cellspacing="1" class="itis" export="true"
	pagesize="15" id="lt" requestURI="EmployeeRReport.action" width="100%">
	
	<display:setProperty name="export.excel.filename" value="EmployeeReport.xls" />
	<display:setProperty name="export.xml.filename" value="EmployeeReport.xml" />
	<display:setProperty name="export.csv.filename" value="EmployeeReport.csv" />

	<display:setProperty name="paging.banner.item_name" value="employee" />
	<display:setProperty name="paging.banner.items_name" value="employees" />
	<display:setProperty name="basic.msg.empty_list" value="No employee added till date." />
		
	<display:column title="First Name" sort="true"><%=((java.util.List) pageContext.getAttribute("lt")).get(1)%></display:column>
	<display:column title="Last Name" sort="true"><%=((java.util.List) pageContext.getAttribute("lt")).get(2)%></display:column>
	<display:column title="Joining Date" sort="true"><%=((java.util.List) pageContext.getAttribute("lt")).get(3)%></display:column>
	<display:column title="Designation" sort="true"><%=((java.util.List) pageContext.getAttribute("lt")).get(4)%></display:column>
	<display:column title="Department" sort="true"><%=((java.util.List) pageContext.getAttribute("lt")).get(5)%></display:column>
	<display:column title="W.Location" sort="true"><%=((java.util.List) pageContext.getAttribute("lt")).get(6)%></display:column>
	<display:column title="Status" sort="true"><%=((java.util.List) pageContext.getAttribute("lt")).get(7)%></display:column>
	<display:column title="Employment Type" sort="true"><%=((java.util.List) pageContext.getAttribute("lt")).get(8)%></display:column>
	
</display:table>
</div>



<a href="#" class="report_trigger"> Reports </a>
   <div class="report_panel">
		<jsp:include page="../reports/ReportNavigation.jsp"></jsp:include>
   </div>