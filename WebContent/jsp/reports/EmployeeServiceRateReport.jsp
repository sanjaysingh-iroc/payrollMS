<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>


<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<div class="aboveform">
<h4>Rate / Cost center</h4>
<display:table name="reportList" cellspacing="1" class="itis" export="true"
	pagesize="15" id="lt" requestURI="EmployeeServiceRateReport.action">
	
	<display:setProperty name="export.excel.filename" value="EmployeeServiceRateReport.xls" />
	<display:setProperty name="export.xml.filename" value="EmployeeServiceRateReport.xml" />
	<display:setProperty name="export.csv.filename" value="EmployeeServiceRateReport.csv" />
	
	
	<display:column title="Emp Name" sort="true"><%=((java.util.List) pageContext.getAttribute("lt")).get(0)%></display:column>
	<display:column title="Service"><%=((java.util.List) pageContext.getAttribute("lt")).get(1)%></display:column>
	<display:column title="Emp Type"><%=((java.util.List) pageContext.getAttribute("lt")).get(2)%></display:column>
	<display:column styleClass="alignRight" title="Fixed"><%=((java.util.List) pageContext.getAttribute("lt")).get(3)%></display:column>		
	<display:column styleClass="alignRight" title="Monday"><%=((java.util.List) pageContext.getAttribute("lt")).get(4)%></display:column>
	<display:column styleClass="alignRight" title="Tuesday"><%=((java.util.List) pageContext.getAttribute("lt")).get(5)%></display:column>
	<display:column styleClass="alignRight" title="Wednesday"><%=((java.util.List) pageContext.getAttribute("lt")).get(6)%></display:column>
	<display:column styleClass="alignRight" title="Thursday"><%=((java.util.List) pageContext.getAttribute("lt")).get(7)%></display:column>
	<display:column styleClass="alignRight" title="Friday"><%=((java.util.List) pageContext.getAttribute("lt")).get(8)%></display:column>
	<display:column styleClass="alignRight" title="Saturday"><%=((java.util.List) pageContext.getAttribute("lt")).get(9)%></display:column>
	<display:column styleClass="alignRight" title="Sunday" sort="true"><%=((java.util.List) pageContext.getAttribute("lt")).get(10)%></display:column>
</display:table>
</div>
