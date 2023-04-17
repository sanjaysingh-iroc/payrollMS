<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<%
	UtilityFunctions uF = new UtilityFunctions();
	CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
%>

<div class="leftbox reportWidth">
	<s:action name="EmployeeSalaryDetails" executeResult="true">
			<s:param name="empId"><s:property value="emp_id"/></s:param>
			<s:param name="CCID">0</s:param>
			<s:param name="step">9</s:param>
			<s:param name="mode">A</s:param>
			<s:param name="oldGradeId"><s:property value="grade_from"/></s:param>
			<s:param name="basic">basic</s:param>
	</s:action>
	
</div>