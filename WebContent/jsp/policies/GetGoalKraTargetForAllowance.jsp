<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>  
	<% 
	UtilityFunctions uF = new UtilityFunctions();
	String conditionType = (String)request.getAttribute("conditionType");
	
	if(uF.parseToInt(conditionType) == IConstants.A_GOAL_KRA_TARGET_ID) { %>
		<s:select name="strLevelGoals" cssClass="validateRequired" list="goalList" listKey="paymentLogicId" 
			listValue="paymentLogicName" multiple="true" value="gktValue"></s:select> <!-- headerKey="" headerValue="Select Goal/KRA/Target" -->
	<% } else if(uF.parseToInt(conditionType) == IConstants.A_KRA_ID) { %>
		<s:select name="strLevelKras" cssClass="validateRequired" list="kraList" listKey="paymentLogicId" 
			listValue="paymentLogicName" multiple="true" value="kValue"></s:select> <!-- headerKey="" headerValue="Select Goal/KRA/Target" -->
	<% } %>  
