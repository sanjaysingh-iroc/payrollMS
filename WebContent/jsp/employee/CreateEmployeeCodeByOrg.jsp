<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<% 
	UtilityFunctions uF = new UtilityFunctions();
	//boolean autoGenerate = (Boolean)request.getAttribute("autoGenerate");
	//String empCodeAlphabet = uF.showData((String)request.getAttribute("empCodeAlphabet"), "");
	//String empCodeNumber = uF.showData((String)request.getAttribute("empCodeNumber"), "");
	String validReqOpt = uF.showData((String)request.getAttribute("validReqOpt"), "");
%>

	<s:hidden name="autoGenerate"/>
	<s:if test="autoGenerate==true">
		<input type="text" name="empCodeAlphabet" id="empCodeAlphabetDis" style="width:98px !important;" value="<%=uF.showData((String)request.getAttribute("empCodeAlphabet"), "") %>" disabled="disabled"/> <%-- class="<%=validReqOpt %>" --%>
		<s:hidden name="empCodeAlphabet" id="empCodeAlphabet"/>
		<input type="text" name="empCodeNumber" id="empCodeNumber" onchange="checkCodeValidation()" class="<%=validReqOpt %>" style="width:98px !important;" value="<%=uF.showData((String)request.getAttribute("empCodeNumber"), "") %>" onkeypress="return isOnlyNumberKey(event)"/>
		<div id="empCodeMessege"></div>
	</s:if>
	<s:else>
		<input type="text" name="empCodeAlphabet" id="empCodeAlphabet" onchange="checkCodeValidation()" style="width:98px !important;" class="<%=validReqOpt %>" value="<%=uF.showData((String)request.getAttribute("empCodeAlphabet"), "") %>"/>
		<input type="text" name="empCodeNumber" id="empCodeNumber" onchange="checkCodeValidation()" style="width:98px !important;" class="<%=validReqOpt %>" value="<%=uF.showData((String)request.getAttribute("empCodeNumber"), "") %>" onkeypress="return isOnlyNumberKey(event)"/>
		<div id="empCodeMessege"></div>
	</s:else>
<%-- <%=autoGenerate %>::::<%=empCodeAlphabet %>::::<%=empCodeNumber %> --%>