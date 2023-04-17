<%@page import="com.konnect.jpms.select.FillUserStatus"%>
<%@page import="com.konnect.jpms.select.FillEmployee"%>
<%@page import="com.konnect.jpms.select.FillUserType"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

 
<s:form theme="simple"  id="formAddNewRow" action="AddUser" method="POST" cssClass="formcss" cssStyle="display: none;">

	<s:hidden name="userId" />
	<table border="0" class="formcss" style="width:675px">
		<tr><td colspan=2><s:fielderror/></td></tr>

		<tr>
			<td class="txtlabel alignRight">User Name<sup>*</sup>:</td>
			<td><input type="text" name="userName" id="userName" rel="0" class="required" onkeyup="getContent('ValidateUserName.action?userName='+this.value);" />
			<%-- <s:textfield name="userName" label="User Name" required="true" onkeyup="javascript:show_userValidation();return false;" /> --%><span class="hint">Username is required for an employee to login into the system.<span class="hint-pointer">&nbsp;</span></span>
			<s:url id="userName_url" action="ValidateUserName" /> <sx:div href="%{userName_url}" listenTopics="show_userValidation" formId="frm_user" showLoadingText=""></sx:div>
			<div id="myDiv"></div>
			
			</td>
		</tr>
		
		
		<tr>
			<td class="txtlabel alignRight">Password<sup>*</sup>:</td>
			<td><input type="password" name="password" id="password" rel="1" class="required" />
			<%-- <s:textfield name="password" label="Password" required="true"/> --%><span class="hint">Password is used to login securely.<span class="hint-pointer">&nbsp;</span></span></td>
		</tr>
			
		
		<tr>
			<td class="txtlabel alignRight">Select User Type<sup>*</sup>:</td>
			<td>
			<select rel="2" name="userType" id="userType">
							<% java.util.List  userTypeList = (java.util.List) request.getAttribute("userTypeList"); %>
							<% for (int i=0; i<userTypeList.size(); i++) { %>
							<option value=<%= ((FillUserType)userTypeList.get(i)).getUserTypeId() %>><%= ((FillUserType)userTypeList.get(i)).getUserTypeName() %></option>
							<% } %>
						</select>
			<%-- <s:select label="Select User Type" name="userType" listKey="userTypeId"
				listValue="userTypeName" headerKey="0" headerValue="Select Type"
				list="userTypeList" key="" required="true"/> --%>
				<span class="hint">User type gives the user access limitation.<br>Administrator - Full access<br>Manager - Full access with some restrictions<br>Employee - Limited access<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>	
		
			
		<tr>
			<td class="txtlabel alignRight">Select Employee Code<sup>*</sup>:</td>
			<td>
			<select name="empCode" id="empCode">
							<% java.util.List  empCodeList = (java.util.List) request.getAttribute("empCodeList"); %>
							<% for (int i=0; i<empCodeList.size(); i++) { %>
							<option value=<%= ((FillEmployee)empCodeList.get(i)).getEmployeeId() %>><%= ((FillEmployee)empCodeList.get(i)).getEmployeeCode() %></option>
							<% } %>
						</select>
			<%-- <s:select label="Select Employee Eode" name="empCode"
				listKey="employeeId" listValue="employeeCode" headerKey="0"
				headerValue="Select Employee Code" list="empCodeList" key="" required="true"/> --%><span class="hint">Description of the cost-center.<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>	
		
		<tr>
			<td class="txtlabel alignRight">User Status<sup>*</sup>:</td>
			<td>
			<select rel="3" name="userStatus" id="userStatus">
							<% java.util.List  userStatusList = (java.util.List) request.getAttribute("userStatusList"); %>
							<% for (int i=0; i<userStatusList.size(); i++) { %>
							<option value=<%= ((FillUserStatus)userStatusList.get(i)).getStatusId() %>><%= ((FillUserStatus)userStatusList.get(i)).getStatusName() %></option>
							<% } %>
						</select>
			<%-- <s:select label="User Status" name="userStatus" listKey="statusId"
				listValue="statusName" headerKey="0" headerValue="Select Status"
				list="userStatusList" key="" required="true"/> --%><span class="hint">Description of the cost-center.<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>
		
		<tr>
			<td></td>
			<td><s:submit cssClass="input_button" value="Ok" id="btnAddNewRowOk"/>
			<s:submit  cssClass="input_button" value="Cancel" id="btnAddNewRowCancel"/></td>
		</tr>


</table>
</s:form>

