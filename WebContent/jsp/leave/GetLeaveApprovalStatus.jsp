<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<div style="margin-left: 60px;">
	
	<%
		Map<String,String> hmApprovalStatus =(Map<String,String>)request.getAttribute("hmApprovalStatus");  
		if(hmApprovalStatus == null) hmApprovalStatus = new HashMap<String,String>();
		String effectiveid=(String)request.getAttribute("effectiveid");
		UtilityFunctions uF = new UtilityFunctions();
		String strReason=(String)request.getAttribute("strReason");
	%>
	<div style="text-align: left; margin-top: 10px;"><%=uF.showData(hmApprovalStatus.get(effectiveid.trim()),"") %></div>
	<%if(strReason!=null && !strReason.trim().equals("") && !strReason.trim().equalsIgnoreCase("NULL")){ %>
		<div style="text-align: left; margin-top: 10px;border-top: 1px solid gray;"><%=strReason %></div>
	<%} %>
</div>
  




				