<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<% UtilityFunctions uF = new UtilityFunctions();%>
<%=uF.showData((String)request.getAttribute("AMOUNT"), "0") %>
