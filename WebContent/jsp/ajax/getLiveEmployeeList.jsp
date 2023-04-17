<%@ taglib prefix="s" uri="/struts-tags"%>

<script type="text/javascript">
$(function(){
	<%String type = (String)request.getAttribute("multiple");
		//System.out.println("type==>"+type);
			if(type != null && !type.equals("") && !type.equalsIgnoreCase("null")) {%>
			$("#strEmpId").multiselect().multiselectfilter(); 
		<%}%>
});
	
</script>
<% 
	String fromPage = (String) request.getAttribute("fromPage");
	//System.out.println("fromPage in getLiveEmployeeList==>"+fromPage);

	if(fromPage != null && fromPage.endsWith("LAFORM")) {%>
	<s:select name="strEmpId" id="strEmpId" listKey="employeeId" theme="simple" listValue="employeeName" headerKey="" 
			headerValue="Select Employee" list="empList" onchange="getLoanTypeByEmp(this.value);" />

<% } else if(fromPage != null && fromPage.equals("EL")) {%>
	<s:select name="strEmpId" id="strEmpId" listKey="employeeId" theme="simple" listValue="employeeName" headerKey="" 
			headerValue="Select Employee" list="empList" onchange="getTypeOFLeave();"/>

<% } else {%>
	<%if(request.getAttribute("multiple")!=null) { %>
		<s:select name="strEmpId" id="strEmpId" listKey="employeeId" cssStyle="float:left;" theme="simple" multiple="true" size="6" listValue="employeeName" 
			list="empList" onchange="document.frmEmployeeActivity.submit();" />
	
	<% } else if(request.getAttribute("validate")!=null) { %>
		<s:select name="strEmpId" id="strEmpId" listKey="employeeId"  cssStyle="float:left;" theme="simple" listValue="employeeName" headerKey="" 
			headerValue="Select Employee" list="empList" cssClass="validateRequired" />					
	<% } else {%>
		<p style="padding-left: 5px;">Employee</p>	
		<s:select name="strEmpId" id="strEmpId" listKey="employeeId" listValue="employeeName" headerKey="" 
			headerValue="Select Employee" list="empList" onchange="document.frmEmployeeActivity.submit();" />
	<% } %>
<%} %>
 