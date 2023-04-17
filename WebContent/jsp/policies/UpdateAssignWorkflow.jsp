<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<% UtilityFunctions uF = new UtilityFunctions();
String type = (String) request.getAttribute("type");
String strPeriod = (String) request.getAttribute("strPeriod");
if(type!=null && type.trim().equalsIgnoreCase("L")){%>
	<%=uF.showData((String)request.getAttribute("data"),"") %>
<%} else {
%>
<%=uF.showData((String)request.getAttribute("POLICY_NAME"), "N/A") %>
<%}%>