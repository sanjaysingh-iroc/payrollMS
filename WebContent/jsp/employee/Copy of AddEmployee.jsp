<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.*"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>


 
<script>
function show_states() {
	dojo.event.topic.publish("show_states");
}
</script>


<div class="aboveform">
<h4>Enter Employee Details</h4>





<%
	String strEmpType = (String) session.getAttribute("USERTYPE");
	String strMessage = (String) request.getAttribute("MESSAGE");
	if (strMessage == null) {
		strMessage = "";
	}
%> <%!String showMessage(String str) {
		if (str != null) {
			return str;
		} else {
			return "";
		}
	}%> <%=showMessage((String) request.getAttribute("MESSAGE"))%> 
	
	<s:form	action="AddEmployee" id="frm_emp" method="POST" cssClass="formcss" enctype="multipart/form-data">



	<s:hidden name="empPerId" />
	<s:textfield name="empCode" label="Employee Code" required="true" />
	<s:textfield name="empFname" label="Employee First Name"
		required="true" />
	<s:textfield name="empLname" label="Employee Last Name" required="true" />
	<s:textfield name="empEmail" label="Email Id" required="true" />
	<s:textfield name="empAddress1" label="Employee Address1"
		required="true" />
	<s:textfield name="empAddress2" label="Employee Address2" />

	<s:select label="Select Country" name="country" listKey="countryId"
		listValue="countryName" headerKey="0" headerValue="Select Country"
		onchange="javascript:show_states();return false;"
		list="countryList" key="" required="true" />

	<s:url id="d_url" action="DetailAction" /> <sx:div id="details" href="%{d_url}" listenTopics="show_states" formId="frm_emp" showLoadingText=""></sx:div> 



	<s:select label="Select State" name="state" listKey="stateId"
		listValue="stateName" headerKey="0" headerValue="Select State"
		list="stateList" key="" required="true" />

	<s:select label="Select City" name="city" listKey="cityId"
		listValue="cityName" headerKey="0" headerValue="Select City"
		list="cityList" key="" required="true" />

	<s:textfield name="empPincode" label="Employee Pincode" />
	<s:textfield name="empContactno" label="Employee Contact Number" />
	  
	
	
	
	



	<%
		if (strEmpType.equalsIgnoreCase(IConstants.ADMIN)) {
	%>

	<s:select label="Select Employment Type" name="empType"
		listKey="empTypeId" listValue="empTypeName" headerKey="0"
		headerValue="Select Emp Type" list="empTypeList" key=""
		required="true" />


	<s:select label="Select Work Location" name="wLocation"
		listKey="wLocationId" listValue="wLocationName" headerKey="0"
		headerValue="Select Location" list="wLocationList" key=""
		required="true" />

	<s:select label="Select Department" name="department" listKey="deptId"
		listValue="deptName" headerKey="0" headerValue="Select Department"
		list="deptList" key="" required="true" />

<!-- 
	<s:select label="Select Designation" name="designation"
		listKey="desigId" listValue="desigName" headerKey="0"
		headerValue="Select Designation" list="desigList" key=""
		required="true" />
 -->
 
	<%-- <s:select label="Supervisor" name="supervisor" listKey="employeeId"
		listValue="employeeCode" headerKey="0" headerValue="Select Supervisor"
		list="supervisorList" key="" required="true" /> --%>
		
	<s:select label="Supervisor" name="supervisor" listKey="employeeId"
		listValue="employeeCode" headerKey="0" headerValue="Select Manager"
		list="supervisorList" key="" required="true" />

	<s:select label="Cost Centre" name="service" listKey="serviceId"
		listValue="serviceName" headerKey="0" multiple="true" size="3"
		headerValue="Choose Cost Centres" list="serviceList" key=""
		required="true" />

<!-- 
	<s:datetimepicker name="availFrom" label="Available From" type="time"
		displayFormat="HH:mm:ss" required="true"></s:datetimepicker>
	<s:datetimepicker name="availTo" label="Available To" type="time"
		displayFormat="HH:mm:ss" required="true"></s:datetimepicker>
 -->
 <s:textfield name="availFrom" label="Available From" value="00:00:00" required="true"/>
 <s:textfield name="availTo" label="Available To" value="00:00:00" required="true"/>
 
	<s:select label="Roster Dependency" name="rosterDependency"
		listKey="approvalId" listValue="approvalName" headerKey="0"
		headerValue="Select Dependency" list="rosterDependencyList" key=""
		required="true" />
		
	<s:checkbox cssStyle="tdLabel" name="isFirstAidAllowance" label="First Aid Allowance" />





	<%
		}
	%>


	<%
		if (request.getParameter("E") != null) {
	%>
	<s:submit cssClass="input_button" value="Update Employee"
		align="center" />
	<%
		} else {
	%>
	<s:submit cssClass="input_button" value="Add Employee" align="center" />
	<%
		}
	%>


</s:form>


<s:form theme="simple"	action="AddEmployee" id="frm_emp" method="POST" cssClass="formcss" enctype="multipart/form-data">


<table border="0">
<tr><td><s:hidden name="empPerId" /></td></tr>
<tr><td><s:textfield name="empCode" label="Employee Code" required="true" /></td></tr>
<tr><td><s:textfield name="empCode" label="Employee Code" required="true" /></td></tr>
<tr><td><s:textfield name="empLname" label="Employee Last Name" required="true" /></td></tr>
<tr><td><s:textfield name="empEmail" label="Email Id" required="true" /></td></tr>
<tr><td><s:textfield name="empAddress1" label="Employee Address1" required="true" /></td></tr>
<tr><td><s:textfield name="empAddress2" label="Employee Address2" /></td></tr>
<tr><td><s:select label="Select Country" name="country" listKey="countryId"
		listValue="countryName" headerKey="0" headerValue="Select Country"
		onchange="javascript:show_states();return false;"
		list="countryList" key="" required="true" /></td></tr>
<tr><td><s:url id="d_url" action="DetailAction" /> <sx:div id="details" href="%{d_url}" listenTopics="show_states" formId="frm_emp" showLoadingText=""></sx:div></td></tr>
<tr><td><s:select label="Select State" name="state" listKey="stateId"
		listValue="stateName" headerKey="0" headerValue="Select State"
		list="stateList" key="" required="true" /></td></tr>
<tr><td><s:select label="Select City" name="city" listKey="cityId"
		listValue="cityName" headerKey="0" headerValue="Select City"
		list="cityList" key="" required="true" /></td></tr>

<tr><td><s:textfield name="empPincode" label="Employee Pincode" /></td></tr>
<tr><td><s:textfield name="empContactno" label="Employee Contact Number" /></td></tr>
<tr><td></td></tr>
<tr><td></td></tr>
<tr><td></td></tr>
<tr><td></td></tr>


</table>

	
	
	  
	
	
	
	



	<%
		if (strEmpType.equalsIgnoreCase(IConstants.ADMIN)) {
	%>

	<s:select label="Select Employment Type" name="empType"
		listKey="empTypeId" listValue="empTypeName" headerKey="0"
		headerValue="Select Emp Type" list="empTypeList" key=""
		required="true" />


	<s:select label="Select Work Location" name="wLocation"
		listKey="wLocationId" listValue="wLocationName" headerKey="0"
		headerValue="Select Location" list="wLocationList" key=""
		required="true" />

	<s:select label="Select Department" name="department" listKey="deptId"
		listValue="deptName" headerKey="0" headerValue="Select Department"
		list="deptList" key="" required="true" />

<!-- 
	<s:select label="Select Designation" name="designation"
		listKey="desigId" listValue="desigName" headerKey="0"
		headerValue="Select Designation" list="desigList" key=""
		required="true" />
 -->
 
	<%-- <s:select label="Supervisor" name="supervisor" listKey="employeeId"
		listValue="employeeCode" headerKey="0" headerValue="Select Supervisor"
		list="supervisorList" key="" required="true" /> --%>
		
	<s:select label="Supervisor" name="supervisor" listKey="employeeId"
		listValue="employeeCode" headerKey="0" headerValue="Select Manager"
		list="supervisorList" key="" required="true" />	

	<s:select label="Cost Centre" name="service" listKey="serviceId"
		listValue="serviceName" headerKey="0" multiple="true" size="3"
		headerValue="Choose Cost Centres" list="serviceList" key=""
		required="true" />

<!-- 
	<s:datetimepicker name="availFrom" label="Available From" type="time"
		displayFormat="HH:mm:ss" required="true"></s:datetimepicker>
	<s:datetimepicker name="availTo" label="Available To" type="time"
		displayFormat="HH:mm:ss" required="true"></s:datetimepicker>
 -->
 <s:textfield name="availFrom" label="Available From" value="00:00:00" required="true"/>
 <s:textfield name="availTo" label="Available To" value="00:00:00" required="true"/>
 
	<s:select label="Roster Dependency" name="rosterDependency"
		listKey="approvalId" listValue="approvalName" headerKey="0"
		headerValue="Select Dependency" list="rosterDependencyList" key=""
		required="true" />
		
	<s:checkbox cssStyle="tdLabel" name="isFirstAidAllowance" label="First Aid Allowance" />





	<%
		}
	%>


	<%
		if (request.getParameter("E") != null) {
	%>
	<s:submit cssClass="input_button" value="Update Employee"
		align="center" />
	<%
		} else {
	%>
	<s:submit cssClass="input_button" value="Add Employee" align="center" />
	<%
		}
	%>


</s:form>


</div>