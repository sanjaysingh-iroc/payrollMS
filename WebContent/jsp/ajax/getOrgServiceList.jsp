<%@ taglib prefix="s" uri="/struts-tags"%>

<% String type = (String) request.getAttribute("type"); %>

<% if(type != null && type.equals("EA")) { %>
	<s:select name="strSBU" id="strSBU" theme="simple" listKey="serviceId" cssClass="validateRequired" listValue="serviceName" headerKey="" 
			headerValue="Select SBU" list="serviceList" key="" />
<% } else { %>
	<s:select label="SBU" name="service" listKey="serviceId" cssClass="validateRequired" theme="simple" headerKey="" headerValue="Select SBU"
			listValue="serviceName" list="serviceList" key="" required="true" /> <!-- multiple="true" size="3" -->
	<% } %>
	