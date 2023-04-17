<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script type="text/javascript" src="scripts/charts/highcharts.js"></script>




<%
String strUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
String strTitle = "Project Performance (Realtime)";

if(strUserType!=null && strUserType.equalsIgnoreCase(IConstants.MANAGER)) {
	strTitle = "My Project Performance";
}
%>

<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="<%=strTitle%>" name="title"/>
</jsp:include>

 
<div class="leftbox reportWidth">
       
       
<%
String strAction = (String)request.getAttribute("javax.servlet.forward.request_uri");
String strQuery = (String)request.getAttribute("javax.servlet.forward.query_string");


if(strAction!=null){
	strAction = strAction.replace(request.getContextPath()+"/","");
	
	if(strQuery!=null && strQuery.indexOf("NN")>=0){
		strAction = strAction+"?"+strQuery;
	}
}

%>
<div style="margin-bottom: 20px">
  <a class="<%=((strAction.equalsIgnoreCase("ProjectPerformanceWP.action")?"current":"next")) %>" href="ProjectPerformanceWP.action">Working Projects</a> |      
  <a class="<%=((strAction.equalsIgnoreCase("ProjectPerformanceCP.action")?"current":"next")) %>" href="ProjectPerformanceCP.action">Completed Projects</a>
</div>
        

<display:table name="alOuter" cellspacing="1" class="tb_style" export="true"
	pagesize="50" id="lt1" requestURI="ProjectPerformanceCP.action" width="100%">
	
	<display:setProperty name="export.excel.filename" value="ProjectPerformance.xls" />
	<display:setProperty name="export.xml.filename" value="ProjectPerformance.xml" />
	<display:setProperty name="export.csv.filename" value="ProjectPerformance.csv" />
	
	<display:column nowrap="nowrap" title="Project Name"><%=((java.util.List) pageContext.getAttribute("lt1")).get(1)%></display:column>
	<display:column title="Budgeted Cost" styleClass="alignRight padRight20"><%=((java.util.List) pageContext.getAttribute("lt1")).get(2)%></display:column>	
	<display:column nowrap="nowrap" title="Actual Cost" styleClass="alignRight padRight20"><%=((java.util.List) pageContext.getAttribute("lt1")).get(3)%></display:column>
	<display:column nowrap="nowrap" title="Billable Amount" styleClass="alignRight padRight20"><%=((java.util.List) pageContext.getAttribute("lt1")).get(4)%></display:column>
	<display:column nowrap="nowrap" title="Profit Margin<br/>(%)" styleClass="alignRight padRight20"><%=((java.util.List) pageContext.getAttribute("lt1")).get(5)%>%</display:column>
	<display:column nowrap="nowrap" title="Indicator" align="center"><%=((java.util.List) pageContext.getAttribute("lt1")).get(6)%></display:column>
	
</display:table>


<div style="margin:2px;float:left;width:100%">
    <div style="width: 20px;float:left"><!-- <img src="images1/icons/denied.png" width="17"> --><i class="fa fa-circle" aria-hidden="true" style="color:#e22d25"></i></div> 
    <div style="float:left;padding-left:5px">Actual &gt; Billable</div>
</div>

<div style="margin:2px;float:left;width:100%">
    <div style="width: 20px;float:left"> <!-- <img src="images1/icons/re_submit.png" width="17"> --><i class="fa fa-circle" aria-hidden="true" style="color:#f7ee1d"></i></div> 
    <div style="float:left;padding-left:5px">Actual &gt; Budgeted and Actual &lt; Billable</div>
</div>


<div style="margin:2px;float:left;width:100%">
    <div style="width: 20px;float:left"> <!-- <img src="images1/icons/approved.png" width="17"> --><i class="fa fa-circle" aria-hidden="true" style="color:#54aa0d"></i></div> 
    <div style="float:left;padding-left:5px">Actual &lt; Budgeted</div>
</div>

<div>
<jsp:include page="/jsp/task/ProjectPerformanceChart.jsp"></jsp:include>
</div>

</div>

