<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

	<%
		UtilityFunctions uF = new UtilityFunctions();
		String taggedResource = (String)request.getAttribute("taggedResource");
		String strFeedId = (String)request.getAttribute("strFeedId");
	%>
	<%=uF.showData(taggedResource, "") %>
	
	<s:select theme="simple" name="strTaggedWith" id="strTaggedWith" list="resourceList" listKey="employeeId" listValue="employeeName" 
		title="Tag resources for your document" size="3" multiple="true" value="taggedRes"/>
		<i>Hold CTRL for multiple selection</i>
		<input type="button" name="btnTagged" class="input_button" value="Click for Tag" onclick="addGetTaggedWith('<%=strFeedId %>');"/>