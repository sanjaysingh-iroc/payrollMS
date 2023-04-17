<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>


<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<div class="aboveform">
<h4>Designation List</h4>
<display:table name="reportList" cellspacing="1" class="itis" export="true"
	pagesize="15" id="lt" requestURI="DesignationReport.action">
	
	<display:setProperty name="export.excel.filename" value="DesignationReport.xls" />
	<display:setProperty name="export.xml.filename" value="DesignationReport.xml" />
	<display:setProperty name="export.csv.filename" value="DesignationReport.csv" />
	
	<display:column title="Designation" sort="true"><%=((java.util.List) pageContext.getAttribute("lt")).get(0)%></display:column>
	<%if(session.getAttribute(IConstants.USERTYPE)!=null && ((String)session.getAttribute(IConstants.USERTYPE)).equals(IConstants.ADMIN)){ %>
	<display:column media="html" title="Action"><%=((java.util.List) pageContext.getAttribute("lt")).get(1)%></display:column>
	<%} %>
</display:table></div>
