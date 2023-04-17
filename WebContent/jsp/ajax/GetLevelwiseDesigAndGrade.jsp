<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<% if(request.getAttribute("page") != null && request.getAttribute("page").equals("SOrient")) { %>
		<s:select name="strDesignationUpdate" list="desigList" listKey="desigId" id="desigIdVOrient" listValue="desigCodeName"
			 onchange="getEmployeebyDesigOrient();" value="desigvalue" cssStyle="width:150px;" multiple="true" size="4"/>
::::
		<s:select name="empGrade" list="gradeList" listKey="gradeId" listValue="gradeCode"  id="gradeIdVOrient"
			 onchange="getEmployeebyGradeOrient();" value="gradevalue" cssStyle="width:150px;" multiple="true" size="4"/>
              
<% } else { %>
		<s:select name="strDesignationUpdate" list="desigList" listKey="desigId" id="desigIdV" listValue="desigCodeName" 
			headerValue="All Designations" onchange="getEmployeebyDesig();" value="desigvalue" cssStyle="width:150px;" multiple="true" size="4"/>
::::
		<s:select name="empGrade" list="gradeList" listKey="gradeId" listValue="gradeCode" id="gradeIdV"
		onchange="getEmployeebyGrade();" value="gradevalue" cssStyle="width:150px;" multiple="true" size="4"/>
 <% } %> 