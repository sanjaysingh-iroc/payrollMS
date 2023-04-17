<jsp:include page="../../common/Links.jsp" flush="true"></jsp:include>
<%@page import="java.util.*"%>
<%

List alReport = (List)request.getAttribute("alReport");
List alReportLabel = (List)request.getAttribute("alReportLabel");
String []currentPayCycle = (String[])request.getAttribute("currentPayCycle");
String []prevPayCycle = (String[])request.getAttribute("prevPayCycle");
String strColspan = (String)request.getAttribute("strColspan");


%>
Time Chart