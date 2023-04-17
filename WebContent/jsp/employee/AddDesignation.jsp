<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<div class="aboveform">
<h4><%=(request.getParameter("E")!=null)?"Edit":"Add" %> Designation</h4>
<%
	String strMessage = (String) request.getAttribute("MESSAGE");
	if (strMessage == null) {
		strMessage = "";
	}
%>

<script>

$(document).ready(function() {
    $("input#desig_autocomplete").autocomplete({
    source: ["c++", "java", "php", "coldfusion", "javascript", "asp", "ruby"]
});
  });
  
</script>

<input id="desig_autocomplete" />



<p class="message"><%=strMessage%></p>

<s:form action="AddDesignation" method="POST" cssClass="formcss">

	<s:hidden name="desigId" />
	<s:textfield name="desigName" label="Designtion Name" required="true"/>

 
	<%
		if (request.getParameter("E") != null) {
	%>
	
	<s:submit  cssClass="input_button" value="Update Designtion" align="center" />
	
	<%
		} else {
	%>
	<s:submit  cssClass="input_button" value="Add Designtion" align="center" />
	<%
		}
	%>




</s:form>
</div>
