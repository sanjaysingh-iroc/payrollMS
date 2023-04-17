<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@taglib uri="/struts-tags" prefix="s"%>
<script>
	addLoadEvent(prepareInputsForHints);
</script>


<div class="aboveform">
<h4><%=(request.getParameter("E")!=null)?"Edit":"Add" %> User Type</h4>
<%
	String strMessage = (String) request.getAttribute("MESSAGE");
	if (strMessage == null) {
		strMessage = "";
	}
%>


<p class="message"><%=strMessage%></p>


<s:form action="AddUserType" method="POST" cssClass="formcss">

	<s:hidden name="userTypeId" />
	<s:textfield name="userType" label="User Type" /><span class="hint">User type gives the user access limitation.<br>Administrator - Full access<br>Manager - Full access with some restrictions<br>Employee - Limited access.<span class="hint-pointer">&nbsp;</span></span>


	<%
		if (request.getParameter("E") != null) {
	%>
	<s:submit  cssClass="input_button" value="Update User Type" align="center" />
	<%
		} else {
	%>
	<s:submit  cssClass="input_button" value="Add User Type" align="center" />
	<%
		}
	%>


</s:form>

</div>

