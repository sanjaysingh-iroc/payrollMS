<%@page import="java.util.Map"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	UtilityFunctions uF = new UtilityFunctions();
	String policyid=(String)request.getAttribute("policyid");
	Map<String, String> hmRegularPolicy = (Map<String, String>) request.getAttribute("hmRegularPolicy");
	Map<String, String> hmContengencyPolicy = (Map<String, String>) request.getAttribute("hmContengencyPolicy");
%>

<%if(policyid!=null && !policyid.equals("")){ %>
<p style="font-size: 10px; padding-left: 66px;">Regular     : <strong><%=hmRegularPolicy.get(policyid.trim()) %></strong></p>
<p style="font-size: 10px; padding-left: 66px;">Contengency : <strong><%=hmContengencyPolicy.get(policyid.trim()) %></strong></p>
<%}%>