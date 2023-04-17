<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script type="text/javascript" src="scripts/charts/highcharts.js"></script>



<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Project Performance Time Report" name="title"/>
</jsp:include>

 

    <div class="leftbox reportWidth">
        
<!-- Place holder where add and delete buttons will be generated -->
<div class="add_delete_toolbar"></div>

<display:table name="alOuter" cellspacing="1" class="tb_style" export="true"
	pagesize="50" id="lt1" requestURI="ProjectPerformanceTime.action" width="100%">
	
	<display:setProperty name="export.excel.filename" value="ProjectPerformance.xls" />
	<display:setProperty name="export.xml.filename" value="ProjectPerformance.xml" />
	<display:setProperty name="export.csv.filename" value="ProjectPerformance.csv" />
	
	<display:column nowrap="nowrap" title="Project Name"><%=((java.util.List) pageContext.getAttribute("lt1")).get(1)%></display:column>
	<display:column nowrap="nowrap" title="Estimated Time"  styleClass="alignRight padRight20"><%=((java.util.List) pageContext.getAttribute("lt1")).get(2)%></display:column>
	<display:column title="Time Spent" styleClass="alignRight padRight20"><%=((java.util.List) pageContext.getAttribute("lt1")).get(3)%></display:column>	
	<display:column nowrap="nowrap" title="Completed"  styleClass="alignRight padRight20"><%=((java.util.List) pageContext.getAttribute("lt1")).get(4)%></display:column>
	<display:column nowrap="nowrap" title="Deadline" media="html"><%=((java.util.List) pageContext.getAttribute("lt1")).get(5)%></display:column>
	
</display:table>



</div>
