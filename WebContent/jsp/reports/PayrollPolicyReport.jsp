<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>


<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<div class="aboveform">
<h4>Payroll Policy List</h4>

<display:table name="reportList" cellspacing="1" class="itis" export="true"
	pagesize="15" id="lt" requestURI="PayrollPolicyReport.action">
	
	<display:setProperty name="export.excel.filename" value="PayrollPolicyReport.xls" />
	<display:setProperty name="export.xml.filename" value="PayrollPolicyReport.xml" />
	<display:setProperty name="export.csv.filename" value="PayrollPolicyReport.csv" />

	
	<display:column title="Employee Type"><%=((java.util.List) pageContext.getAttribute("lt")).get(0)%></display:column>
	<display:column title="Designation"><%=((java.util.List) pageContext.getAttribute("lt")).get(1)%></display:column>
	<display:column title="Cost-Centre"><%=((java.util.List) pageContext.getAttribute("lt")).get(2)%></display:column>	
	<display:column title="Paymode"><%=((java.util.List) pageContext.getAttribute("lt")).get(3)%></display:column>
	<display:column title="Frequency"><%=((java.util.List) pageContext.getAttribute("lt")).get(4)%></display:column>
	<display:column title="Fixed" class="alignRight"><%=((java.util.List) pageContext.getAttribute("lt")).get(5)%></display:column>
	<display:column title="Mon" class="alignRight"><%=((java.util.List) pageContext.getAttribute("lt")).get(6)%></display:column>
	<display:column title="Tue" class="alignRight"><%=((java.util.List) pageContext.getAttribute("lt")).get(7)%></display:column>
	<display:column title="Wed" class="alignRight"><%=((java.util.List) pageContext.getAttribute("lt")).get(8)%></display:column>
	<display:column title="Thu" class="alignRight"><%=((java.util.List) pageContext.getAttribute("lt")).get(9)%></display:column>
	<display:column title="Fri" class="alignRight"><%=((java.util.List) pageContext.getAttribute("lt")).get(10)%></display:column>
	<display:column title="Sat" class="alignRight"><%=((java.util.List) pageContext.getAttribute("lt")).get(11)%></display:column>
	<display:column title="Sun" class="alignRight"><%=((java.util.List) pageContext.getAttribute("lt")).get(12)%></display:column>
	<display:column title="Loading" class="alignRight"><%=((java.util.List) pageContext.getAttribute("lt")).get(13)%></display:column>
	<%if(session.getAttribute(IConstants.USERTYPE)!=null && ((String)session.getAttribute(IConstants.USERTYPE)).equalsIgnoreCase(IConstants.ADMIN)){ %>
	<display:column media="html" title="Action"><%=((java.util.List) pageContext.getAttribute("lt")).get(14)%></display:column>
	<%} %>
</display:table></div>
