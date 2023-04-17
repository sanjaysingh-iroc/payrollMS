<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.*"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

	<% 
		UtilityFunctions uF = new UtilityFunctions();
		String assessmentId = (String) request.getAttribute("assessmentId");
		String assessmentName = (String) request.getAttribute("assessmentName");
		String operation = (String) request.getAttribute("operation");
		String roundId = (String) request.getAttribute("roundId");
		String recruitId = (String) request.getAttribute("recruitId");
	%>
	
	<% if(operation != null && operation.equals("add")) { %>
		<span>
			<a href="javascript: void(0);" onclick="viewAllAssessment('<%=roundId %>', '<%=assessmentId %>')">Assessment:</a> &nbsp;
		</span>
		<p id="roundAssessLblSpan_<%=roundId %>"p> <%=uF.showData(assessmentName, "") %> </p>
	<% } else { %>
		<span>
			<a href="javascript: void(0);" onclick="viewAllAssessment('<%=roundId %>', '<%=assessmentId %>')">Assessment:</a> &nbsp;
		</span>
		<p id="roundAssessLblSpan_<%=roundId %>"> 
			<select name="strAssessment" id="strAssessment_<%=roundId %>">
				<option value="">Select Assessment</option>
				<%=(String)request.getAttribute("sbOption") %>
			</select>&nbsp;&nbsp;&nbsp;
			<input type="button" class="btn btn-primary" onclick="setAssessmentToRound('<%=recruitId %>', '<%=roundId %>');" value="Add"> 
		</p>
	<% } %>
	
	








<%-- 
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.*"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

	<% 
		UtilityFunctions uF = new UtilityFunctions();
		String assessmentId = (String) request.getAttribute("assessmentId");
		String assessmentName = (String) request.getAttribute("assessmentName");
		String operation = (String) request.getAttribute("operation");
		String roundId = (String) request.getAttribute("roundId");
		List<List<String>> assessmentList = (List<List<String>>) request.getAttribute("assessmentList");
	%>
	
	<% if(operation != null && operation.equals("add")) { %>
		<a href="javascript: void(0);" onclick="viewAllAssessment('<%=roundId %>', '<%=assessmentId %>')">Assessment:</a> &nbsp;<%=uF.showData(assessmentName, "") %>
	<% } else { %>
	<div style="float: left; padding: 35px 50px;">
		
		<div style="float: left; width: 100%;"> Assessment: &nbsp;
			<select name="strAssessment" id="strAssessment">
				<option value="">Select Assessment</option>
				<%=(String)request.getAttribute("sbOption") %>
			</select>
		</div>
		<div style="float: left; width: 100%; text-align: center; margin-top: 25px;"><input type="button" class="input_button" 
			onclick="setAssessmentToRound('<%=(String)request.getAttribute("recruitId") %>', '<%=(String)request.getAttribute("roundId") %>', document.getElementById('strAssessment').value);" value="Add"></div>
	</div>
	<% } %> --%>
	
	