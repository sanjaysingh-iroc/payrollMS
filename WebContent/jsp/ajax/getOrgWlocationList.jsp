<%@ taglib prefix="s" uri="/struts-tags"%>

<% String type = (String) request.getAttribute("type"); %>

<% if(type != null && type.equals("EA")) { %>
	<s:select name="strWLocation" id="strWLocation" theme="simple" listKey="wLocationId" cssClass="validateRequired" listValue="wLocationName" 
		headerKey="" headerValue="Select Work Location" list="wLocationList" key="" required="true" />
<% } else if(type != null && type.equals("UTChange")) { %>
	<s:select theme="simple" name="wLocation" id="wLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" 
		multiple="true" size="3" value="wLocValue"/>
<% } else if(type != null && type.equalsIgnoreCase("people")) { %>
	<s:select theme="simple" name="wLocation1" id="wLocation1" listKey="wLocationId" listValue="wLocationName" list="wLocationList1" 
		multiple="true" size="3" value="wLocValue"/>
<% } else if(type != null && type.equalsIgnoreCase("VC")) {%>
     <s:select  name="location" id="locationid" theme="simple" listKey="wLocationId" listValue="wLocationName" headerKey="" 
     	headerValue="Select Location" list="wLocationList" cssClass="validateRequired" />
<% } else if(type != null && type.equalsIgnoreCase("CXOLOCATION")) {%>
	<s:select  name="locationCXO" id="locationCXO" theme="simple" listKey="wLocationId" listValue="wLocationName" list="wLocationList" 
		cssClass="validateRequired" multiple="true"/>
<% } else { %>
	<s:select name="wLocation" cssClass="validateRequired" theme="simple" listKey="wLocationId" listValue="wLocationName" 
		headerKey="" headerValue="Select Location" list="wLocationList" key="" required="true" />			
<% } %>
