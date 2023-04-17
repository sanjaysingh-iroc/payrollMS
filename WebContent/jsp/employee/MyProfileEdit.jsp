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
function show_validation() 
{
	//dojo.event.topic.publish("show_validation");

	

	    var count=document.getElementById('empcodetext').value.length;
		//alert(count);
	    if(count!=0)
	    {
	    	document.getElementById("valid").style.display="block";
	    	dojo.event.topic.publish("show_validation");
	            
	    }

			    else
			    {
			    	document.getElementById("valid").style.display="none";
			    }
	    
	    }
	
   


function show_userValidation() {
	
    var count=document.getElementById('usrname').value.length;
	//alert(count);
    if(count!=0)
    {
    	document.getElementById("valid_usrname").style.display="block";
    	dojo.event.topic.publish("show_userValidation");
            
    }

		    else
		    {
		    	document.getElementById("valid_usrname").style.display="none";
		    }

}


	addLoadEvent(prepareInputsForHints);
</script>

<div class="aboveform">
<h4><%=(request.getParameter("E")!=null)?"Edit":"Enter" %> Employee Details</h4>





<%
	String strEmpType = (String) session.getAttribute("USERTYPE");
	String strMessage = (String) request.getAttribute("MESSAGE");
	if (strMessage == null) {
		strMessage = "";
	}
%>
<p class="message"><%=strMessage%></p>
	
	

<s:form theme="simple"	action="AddEmployee" id="frm_emp" method="POST" cssClass="formcss" enctype="multipart/form-data">


<table border="0" class="formcss">


<tr><td colspan=2><s:fielderror/></td></tr>

<tr><td><s:hidden name="empPerId" /></td></tr>
<tr><td class="tdLabelheadingBg alignCenter" colspan="2">Employee Personal Information</td></tr>
<tr><td height="10px">&nbsp;</td></tr>
<tr><td class="txtlabel alignRight">Employee Code<sup>*</sup>:</td><td><s:textfield name="empCode" label="Employee Code" required="true" onkeyup="javascript:show_validation();return false;" id="empcodetext"/><span class="hint">Employee Code represents the employee in a company. Code can be in any format e.g. KT001, E01, etc<span class="hint-pointer">&nbsp;</span></span>

<div id="valid" style="display:none">
<s:url id="empCode_url" action="ValidateEmpCode" />
 <sx:div href="%{empCode_url}" listenTopics="show_validation" formId="frm_emp" showLoadingText=""></sx:div>
</div>
</td></tr>
<tr><td class="txtlabel alignRight">Employee First Name<sup>*</sup>:</td><td><s:textfield name="empFname" label="Employee First Name" required="true" /><span class="hint">Employee's first name.<span class="hint-pointer">&nbsp;</span></span></td></tr>
<tr><td class="txtlabel alignRight">Employee Last Name<sup>*</sup>:</td><td><s:textfield name="empLname" label="Employee Last Name" required="true" /><span class="hint">Employee's last name.<span class="hint-pointer">&nbsp;</span></span></td></tr>

<tr><td class="txtlabel alignRight">User Name<sup>*</sup>:</td><td><s:textfield name="userName" label="User Name" required="true" onkeyup="javascript:show_userValidation();return false;" id="usrname"/><span class="hint">Username is required for an employee to login into the system.<span class="hint-pointer">&nbsp;</span></span>

<div id="valid_usrname" style="display:none">
<s:url id="userName_url" action="ValidateUserName" /> 
  <sx:div href="%{userName_url}" listenTopics="show_userValidation" formId="frm_emp" showLoadingText=""></sx:div>
</div>
</td></tr>
<tr><td class="txtlabel alignRight">Password<sup>*</sup>:</td><td><s:textfield name="empPassword" label="Password" required="true" /><span class="hint">Password is used to login securely.<span class="hint-pointer">&nbsp;</span></span><s:hidden name="empUserTypeId"/></td></tr>


<tr><td class="txtlabel alignRight">Email Id<sup>*</sup>:</td><td><s:textfield name="empEmail" label="Email Id" required="true" /><span class="hint">Email id is required as the user will received all information on this id.<span class="hint-pointer">&nbsp;</span></span></td></tr>
<tr><td class="txtlabel alignRight">Employee Address1<sup>*</sup>:</td><td><s:textfield name="empAddress1" label="Employee Address1" required="true" /><span class="hint">Employee current address.<span class="hint-pointer">&nbsp;</span></span></td></tr>
<tr><td class="txtlabel alignRight">Employee Address2:</td><td><s:textfield name="empAddress2" label="Employee Address2" /><span class="hint">Employee current address. (optional)<span class="hint-pointer">&nbsp;</span></span></td></tr>

<tr><td class="txtlabel alignRight">Suburb<sup>*</sup>:</td><td><s:textfield name="city" label="Suburb" required="true"/><span class="hint">Add suburb.<span class="hint-pointer">&nbsp;</span></span></td></tr>
<tr><td class="txtlabel alignRight">Select State<sup>*</sup>:</td><td><s:select theme="simple" label="Select State" name="state" listKey="stateId"
		listValue="stateName" headerKey="0" headerValue="Select State"		
		list="stateList" key="" required="true" /><span class="hint">Select state.<span class="hint-pointer">&nbsp;</span></span>
</td></tr>
<tr><td class="txtlabel alignRight">Select Country<sup>*</sup>:</td><td><s:select label="Select Country" name="country" listKey="countryId" 
		listValue="countryName" headerKey="0" headerValue="Select Country"
		list="countryList" key="" required="true" /><span class="hint">Select country.<span class="hint-pointer">&nbsp;</span></span></td></tr>

<!-- ===start parvez date: 30-07-2022=== -->
<%-- <tr><td class="txtlabel alignRight">Employee Postcode:</td><td><s:textfield name="empPincode" label="Employee Pincode" /><span class="hint">Employee's residential pincode/zipcode.<span class="hint-pointer">&nbsp;</span></span></td></tr> --%>
<tr><td class="txtlabel alignRight">Employee Pincode:</td><td><s:textfield name="empPincode" label="Employee Pincode" /><span class="hint">Employee's residential pincode/zipcode.<span class="hint-pointer">&nbsp;</span></span></td></tr>
<!-- ===end parvez date: 30-07-2022=== -->
<tr><td class="txtlabel alignRight">Employee Contact Number:</td><td><s:textfield name="empContactno" label="Employee Contact Number" /><span class="hint">Employee's contact no. (optional but recommended)<span class="hint-pointer">&nbsp;</span></span></td></tr>



<%
		if (strEmpType.equalsIgnoreCase(IConstants.ADMIN)) {
	%>
	
<tr><td class="tdLabelheadingBg alignCenter" colspan="2">Employee Official Information</td></tr>
	
<tr><td class="txtlabel alignRight">Employee Joining Date:</td><td><s:datetimepicker name="empStartDate" label="Employee Joining Date"  type="date" displayFormat="dd/MM/yyyy" required="true"></s:datetimepicker><span class="hint">Employee's date of joining.<span class="hint-pointer">&nbsp;</span></span></td></tr>
	
<tr><td class="txtlabel alignRight">Select Employment Type<sup>*</sup>:</td><td><s:select label="Select Employment Type" name="empType"
		listKey="empTypeId" listValue="empTypeName" headerKey="0"
		headerValue="Select Emp Type" list="empTypeList" key=""
		required="true" /><span class="hint">Employment type as part time or full time. It will be used while calculating payroll.<span class="hint-pointer">&nbsp;</span></span></td></tr>
<tr><td class="txtlabel alignRight">Select Work Location<sup>*</sup>:</td><td><s:select label="Select Work Location" name="wLocation"
		listKey="wLocationId" listValue="wLocationName" headerKey="0"
		headerValue="Select Location" list="wLocationList" key=""
		onchange="javascript:show_department();return false;"
		required="true" /><span class="hint">Employee's work location.<span class="hint-pointer">&nbsp;</span></span></td></tr>
<tr><td class="txtlabel alignRight">Select Department<sup>*</sup>:</td><td><s:url id="department_url" action="GetDepartment" /> <sx:div href="%{department_url}" listenTopics="show_department" formId="frm_emp" showLoadingText=""></sx:div></td></tr>
<%-- <tr><td class="txtlabel alignRight">Supervisor<sup>*</sup>:</td><td><s:select label="Supervisor" name="supervisor" listKey="employeeId"
		listValue="employeeCode" headerKey="0" headerValue="Select Supervisor"
		list="supervisorList" key="" required="true" /><span class="hint">Employee's manager/supervisor as he/she will also get updates about the team.<span class="hint-pointer">&nbsp;</span></span></td></tr> --%>
		
<tr><td class="txtlabel alignRight">Manager<sup>*</sup>:</td><td><s:select label="Supervisor" name="supervisor" listKey="employeeId"
		listValue="employeeCode" headerKey="0" headerValue="Select Manager"
		list="supervisorList" key="" required="true" /><span class="hint">Employee's manager/supervisor as he/she will also get updates about the team.<span class="hint-pointer">&nbsp;</span></span></td></tr>
<tr><td class="txtlabel alignRight">SBU/Cost-centre<sup>*</sup>:</td><td class=""><s:select label="Cost Centre" name="service" listKey="serviceId"
		listValue="serviceName" multiple="true" size="3"
		list="serviceList" key=""
		required="true" /><span class="hint">The cost centres where the employee is supposed to work. This field will be used while calculating roster.<span class="hint-pointer">&nbsp;</span></span>
		<br/>Press ctrl for multiple selections</td></tr>
<tr><td class="txtlabel alignRight">Available From<sup>*</sup>:</td><td><s:textfield name="availFrom" label="Available From" value="00:00AM" required="true"/><span class="hint">Employee's start time. This field will be used while calculating roster. Please specify the in hh:mmAM/PM format only, e.g.12:00PM<span class="hint-pointer">&nbsp;</span></span></td></tr>
<tr><td class="txtlabel alignRight">Available To<sup>*</sup>:</td><td><s:textfield name="availTo" label="Available To" value="00:00AM" required="true"/><span class="hint">Employee's end time. This field will be used while calculating roster.<span class="hint-pointer">&nbsp;</span></span></td></tr>
<tr><td class="txtlabel alignRight">Roster Dependency<sup>*</sup>:</td><td><s:select label="Roster Dependency" name="rosterDependency"
		listKey="approvalId" listValue="approvalName" headerKey="0"
		headerValue="Select Dependency" list="rosterDependencyList" key=""
		required="true" /><span class="hint">Do you want this employee be dependent on roster entries?<span class="hint-pointer">&nbsp;</span></span></td></tr>
<tr><td class="txtlabel alignRight">First Aid Allowance:</td><td><s:checkbox cssStyle="width:20px" cssClass="tdLabel" name="isFirstAidAllowance" label="First Aid Allowance" /><span class="hint">This field is used to calculate the deduction amount. Income from is the lower slab.<span class="hint-pointer">&nbsp;</span></span></td></tr>


<%
		}
	%>


<%
		if (request.getParameter("E") != null) {
	%>
	<tr><td colspan="2" align="center">
	<s:submit cssClass="input_button" value="Update Employee"
		align="center" />
		</td></tr>
	<%
		} else {
	%>
	<tr><td colspan="2" align="center">
	<s:submit cssClass="input_button" cssStyle="width:200px" value="Go Next to add Rates" align="center" />
	</td></tr>
	<%
		}
	%>

</table>




	
</s:form>


</div>