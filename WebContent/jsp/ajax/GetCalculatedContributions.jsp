<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<% UtilityFunctions uF = new UtilityFunctions(); %>
<%=(String)request.getAttribute("dblERPF") %>::::<%=uF.parseToDouble(((String)request.getAttribute("dblERESI"))) %>::::<%=uF.parseToDouble(((String)request.getAttribute("dblERLWF"))) %>
