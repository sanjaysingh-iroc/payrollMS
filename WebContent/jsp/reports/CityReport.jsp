<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>


<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<div class="aboveform">
<h4>Suburb List</h4>
<display:table name="reportList" cellspacing="1" class="itis" export="true"
	pagesize="15" id="lt" requestURI="CityReport.action">
	
	<display:setProperty name="export.excel.filename" value="SuburbReport.xls" />
	<display:setProperty name="export.xml.filename" value="SuburbReport.xml" />
	<display:setProperty name="export.csv.filename" value="SuburbReport.csv" />
	
	<display:setProperty name="export.pdf" value="true" />
	<display:setProperty name="export.pdf.filename" value="SuburbReport.pdf" />
	
	
	<display:setProperty name="paging.banner.item_name" value="suburb" />
	<display:setProperty name="paging.banner.items_name" value="suburbs" />
	<display:setProperty name="basic.msg.empty_list" value="No suburb added till date." />	
	
	<display:column title="Suburb" sort="true"><%=((java.util.List) pageContext.getAttribute("lt")).get(0)%></display:column>
	<display:column title="State"><%=((java.util.List) pageContext.getAttribute("lt")).get(1)%></display:column>
	<display:column title="Country"><%=((java.util.List) pageContext.getAttribute("lt")).get(2)%></display:column>
	<%if(session.getAttribute(IConstants.USERTYPE)!=null && ((String)session.getAttribute(IConstants.USERTYPE)).equalsIgnoreCase(IConstants.ADMIN)){ %>	
	<display:column media="html" title="Action"><%=((java.util.List) pageContext.getAttribute("lt")).get(3)%></display:column>
	<%} %>
</display:table></div>
