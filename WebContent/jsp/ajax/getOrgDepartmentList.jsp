<%@ taglib prefix="s" uri="/struts-tags"%>

<% String type = (String) request.getAttribute("type");
	//System.out.println("type in getorgDepartmentList"+type);
%>

<% if(type != null && type.equals("LAFORM")) { %>
	<s:select name="f_department" id="f_department" theme="simple" listKey="deptId" listValue="deptName" 
		headerKey="" headerValue="Select Department" list="deptList" onchange="getEmployeeList();"/>
<% } else if(type != null && type.equals("EA")) { %>
	<s:select name="strDepartment" id="strDepartment" theme="simple" listKey="deptId" cssClass="validateRequired" listValue="deptName" headerKey="" 
			headerValue="Select Department" list="deptList" key="" required="true" />

<% }else if(type != null && type.equals("EL")) { %>
	<s:select name="f_department" id="f_department" theme="simple" listKey="deptId" listValue="deptName" 
		headerKey="" headerValue="Select Department" list="deptList" onchange="getEmployeeList();"/>

<% } else { %>
<s:select theme="simple" label="Select Department" name="department" listKey="deptId" cssClass="validateRequired" listValue="deptName"
	headerKey="" headerValue="Select Department" list="deptList" key="" required="true" />
<% } %>