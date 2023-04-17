<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.*"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

 
 
<script>
function show_states() {
	dojo.event.topic.publish("show_states");
}
function show_cities() {
	dojo.event.topic.publish("show_cities");
}
function show_department() {
	dojo.event.topic.publish("show_department");
}
function show_validation() {
	dojo.event.topic.publish("show_validation");
}

addLoadEvent(prepareInputsForHints);
</script>

<%
	String strEmpType = (String) session.getAttribute("USERTYPE");
	String strMessage = (String) request.getAttribute("MESSAGE");
	if (strMessage == null) {
		strMessage = "";
	}
%>

<div id="shadow">

    <div class="pagetitle">
      <span><%=(request.getParameter("E")!=null)?"Edit":"Enter" %> Employee Details</span>
    </div>


    <div class="leftbox">
    	<p class="message"><%=strMessage%></p>
	
		<s:form theme="simple"	action="AddEmployeeE" id="frm_emp" method="POST" cssClass="formcss" enctype="multipart/form-data">
		
		
		<table border="0" cellspacing="2" cellpadding="2" >
		
		
		<tr><td colspan=2><s:fielderror/></td></tr>
		
		<tr><td><s:hidden name="empId" /></td></tr>
		<tr><td><s:hidden name="empPerId" /></td></tr>
		<tr><td class="txtlabel alignRight">Employee Code<sup>*</sup>:</td><td class="txtlabel alignLeft"><s:property value="empCode" /><s:hidden name="empCode" />
		
		</td></tr>
		<tr><td class="txtlabel alignRight">Employee First Name<sup>*</sup>:</td><td><s:textfield name="empFname" label="Employee First Name" required="true" /><span class="hint">Please enter your first name.<span class="hint-pointer">&nbsp;</span></span></td></tr>
		<tr><td class="txtlabel alignRight">Employee Last Name<sup>*</sup>:</td><td><s:textfield name="empLname" label="Employee Last Name" required="true" /><span class="hint">Please enter your last name.<span class="hint-pointer">&nbsp;</span></span></td></tr>
		<tr><td class="txtlabel alignRight">User Name<sup>*</sup>:</td><td class="txtlabel alignLeft"><s:property value="userName"/><s:hidden name="userName"/>
		</td></tr>
		<tr><td class="txtlabel alignRight">Password<sup>*</sup>:</td><td><s:textfield name="empPassword" label="Password" required="true" /><span class="hint">Password is used to login securely.<span class="hint-pointer">&nbsp;</span></span></td></tr>
		
		<tr><td class="txtlabel alignRight">Email Id<sup>*</sup>:</td><td><s:textfield name="empEmail" label="Email Id" required="true" /><span class="hint">Please enter your valid email address. All communication will be sent on this email address.<span class="hint-pointer">&nbsp;</span></span></td></tr>
		<tr><td class="txtlabel alignRight">Employee Address1<sup>*</sup>:</td><td><s:textfield name="empAddress1" label="Employee Address1" required="true" /><span class="hint">Please enter you address.<span class="hint-pointer">&nbsp;</span></span></td></tr>
		<tr><td class="txtlabel alignRight">Employee Address2:</td><td><s:textfield name="empAddress2" label="Employee Address2" /></td></tr>
		
		<tr><td class="txtlabel alignRight">Suburb<sup>*</sup>:</td><td><s:textfield name="city" label="Suburb" required="true"/><span class="hint">Add suburb.<span class="hint-pointer">&nbsp;</span></span></td></tr>
		<tr><td class="txtlabel alignRight">Select State<sup>*</sup>:</td><td><s:select theme="simple" label="Select State" name="state" listKey="stateId"
				listValue="stateName" headerKey="0" headerValue="Select State"		
				list="stateList" key="" required="true" /><span class="hint">Select state.<span class="hint-pointer">&nbsp;</span></span>
		</td></tr>
		<tr><td class="txtlabel alignRight">Select Country<sup>*</sup>:</td><td><s:select label="Select Country" name="country" listKey="countryId"
				listValue="countryName" headerKey="0" headerValue="Select Country"
				list="countryList" key="" required="true" /><span class="hint">Select country.<span class="hint-pointer">&nbsp;</span></span></td></tr>
		
		
		<%-- <tr><td class="txtlabel alignRight">Employee Postcode<sup>*</sup>:</td><td><s:textfield name="empPincode" label="Employee Pincode" /><span class="hint">Please enter your post code.<span class="hint-pointer">&nbsp;</span></span></td></tr> --%>
		<tr><td class="txtlabel alignRight">Employee Pincode<sup>*</sup>:</td><td><s:textfield name="empPincode" label="Employee Pincode" /><span class="hint">Please enter your post code.<span class="hint-pointer">&nbsp;</span></span></td></tr>
		<tr><td class="txtlabel alignRight">Employee Contact Number:</td><td><s:textfield name="empContactno" label="Employee Contact Number" /><span class="hint">Please mention your contact number.<span class="hint-pointer">&nbsp;</span></span></td></tr>
		<tr><td class="txtlabel alignRight">Image:</td><td><s:file name="empImage"/></td></tr>
		
		
		
			<tr><td colspan="2" align="center">
			<s:submit cssClass="input_button" value="Update Employee"
				align="center" />
				</td></tr>
			
			
		
		</table>
			
		
		</s:form>


 </div>
</div>