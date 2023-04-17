<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags"%>

<% 
	String type = (String)request.getAttribute("type");
	String strPage = (String)request.getAttribute("page");
	if(strPage == null || !strPage.equals("SOrient")) {
	if(type != null && type.equals("single")) {
%>
		<s:select name="strWlocation" list="workList" id="wlocation" listKey="wLocationId" listValue="wLocationName"
            headerKey="" headerValue="All Work Locations" required="true" value="wlocationvalue" onchange="getEmployeebyLocation();" cssStyle="width:150px;"></s:select>
::::
          <s:select name="strDepart" list="departmentList" id="depart" listKey="deptId" listValue="deptName" 
           headerKey="" headerValue="All Departments"  required="true" onchange="getEmployeebyDepart();" value="departmentvalue" cssStyle="width:150px;"></s:select>
::::
          <s:select name="strLevel" list="levelList" listKey="levelId" id="strLevel" listValue="levelCodeName" 
           headerKey="" headerValue="All Levels"  required="true" onchange="getEmployeebyLevel()" value="levelvalue" cssStyle="width:150px;"></s:select>
::::
          <s:select name="strDesignationUpdate" list="desigList" listKey="desigId" id="desigIdV" listValue="desigCodeName"
           headerKey="" headerValue="All Designations" onchange="getEmployeebyDesig();" value="desigvalue" cssStyle="width:150px;"></s:select>
::::
          <s:select name="empGrade" list="gradeList" listKey="gradeId" listValue="gradeCode"  id="gradeIdV"
           headerKey="" headerValue="All Grades"  onchange="getEmployeebyGrade();" value="gradevalue" cssStyle="width:150px;"></s:select>
	     
<% } else { %>
		<s:select name="strWlocation" list="workList" id="wlocation" listKey="wLocationId" listValue="wLocationName"   
	     	value="wlocationvalue" onchange="getEmployeebyLocation();" multiple="true" size="4"></s:select> <!-- headerKey="" headerValue="All Work Locations" -->
::::
          <s:select name="strDepart" list="departmentList" id="depart" listKey="deptId" listValue="deptName" 
          	onchange="getEmployeebyDepart();" value="departmentvalue" multiple="true" size="4"></s:select>
::::
          <s:select name="strLevel" list="levelList" listKey="levelId" id="strLevel" listValue="levelCodeName" 
          	onchange="getEmployeebyLevel()" value="levelvalue" multiple="true" size="4"></s:select>
::::
          <s:select name="strDesignationUpdate" list="desigList" listKey="desigId" id="desigIdV" listValue="desigCodeName" 
          	 onchange="getEmployeebyDesig();" value="desigvalue" multiple="true" size="4"></s:select>
::::
          <s:select name="empGrade" list="gradeList" listKey="gradeId" listValue="gradeCode"  id="gradeIdV" headerKey="" 
          onchange="getEmployeebyGrade();" value="gradevalue" multiple="true" size="4"></s:select>
         
<% } %>

<% } else { %>
		
		<s:select name="strWlocation" list="workList" id="wlocationOrient" listKey="wLocationId" listValue="wLocationName"
	     	value="wlocationvalue" onchange="getEmployeebyLocationOrient();" multiple="true" size="4"></s:select>
::::
          <s:select name="strDepart" list="departmentList" id="departOrient" listKey="deptId" listValue="deptName"  
          	onchange="getEmployeebyDepartOrient();" value="departmentvalue" multiple="true" size="4"></s:select>
::::
          <s:select name="strLevel" list="levelList" listKey="levelId" id="strLevelOrient" listValue="levelCodeName"  
          	onchange="getEmployeebyLevelOrient()" value="levelvalue" multiple="true" size="4"></s:select>
::::
          <s:select name="strDesignationUpdate" list="desigList" listKey="desigId" id="desigIdVOrient" listValue="desigCodeName"  
          	 onchange="getEmployeebyDesigOrient();" value="desigvalue" multiple="true" size="4"></s:select>
::::
          <s:select name="empGrade" list="gradeList" listKey="gradeId" listValue="gradeCode"  id="gradeIdVOrient"  
          onchange="getEmployeebyGradeOrient();" value="gradevalue" multiple="true" size="4"></s:select>
          
<% } %>