<%@ taglib prefix="s" uri="/struts-tags"%>

<s:if test="desigList != null">
<% 
//System.out.println("Page =======>"+request.getAttribute("page"));
if(request.getAttribute("page") != null && request.getAttribute("page").equals("SOrient")){ %>
	<s:select theme="simple" name="strDesignationUpdate" listKey="desigId" listValue="desigCodeName"  id="desigIdVOrient" 
	  list="desigList" key="" onchange="getEmployeebyDesigOrient();" multiple="true" size="4"  cssStyle="width:150px;"/>
	<% }else { %>
	<s:select theme="simple" name="strDesignationUpdate" listKey="desigId" listValue="desigCodeName"  id="desigIdV"  
	 list="desigList" key="" onchange="getEmployeebyDesig();" multiple="true" size="4"  cssStyle="width:150px;"/>
	<% } %>	
</s:if> 

<s:if test="gradeList != null">

<%
//System.out.println("Page =======>"+request.getAttribute("page"));
if(request.getAttribute("page") != null && request.getAttribute("page").equals("SOrient")){ %>
	<s:select name="empGrade" list="gradeList" listKey="gradeId" listValue="gradeCode" id="gradeIdVOrient"
				 onchange="getEmployeebyGradeOrient();" multiple="true" size="4" cssStyle="width:150px;"></s:select>
	<% }else{ %>
	<s:select name="empGrade" list="gradeList" listKey="gradeId" listValue="gradeCode" id="gradeIdV"
				 onchange="getEmployeebyGrade();" multiple="true" size="4" cssStyle="width:150px;"></s:select>
	<% } %>
</s:if>
