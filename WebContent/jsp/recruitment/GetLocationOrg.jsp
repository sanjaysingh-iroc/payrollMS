<%@ taglib prefix="s" uri="/struts-tags"%>
<Script>
$(function(){
	$("#f_strWLocation").multiselect().multiselectfilter();
	//$("#locationid").multiselect().multiselectfilter();
});
</Script>
<% 
	String fromPage = (String) request.getAttribute("fromPage");
	//System.out.println("fromPage in getLocationByOrg "+fromPage);
	if(fromPage != null && fromPage.endsWith("LAFORM")) {
%>
	<s:select name="f_strWLocation" id="f_strWLocation" theme="simple" listKey="wLocationId" listValue="wLocationName" 
		headerKey="" headerValue="Select Location" list="workLocationList" onchange="getEmployeeList();"/>
<% } else if(fromPage != null && fromPage.equals("AddEmp")){%>
	<s:select  name="f_strWLocation" id="f_strWLocation" theme="simple" 
		listKey="wLocationId" listValue="wLocationName" headerKey="" list="workLocationList" multiple="true" />

<%}else if(fromPage != null && fromPage.equals("EL")) {%>
	<s:select name="f_strWLocation" id="f_strWLocation" theme="simple" listKey="wLocationId" listValue="wLocationName" 
		headerKey="" headerValue="Select Location" list="workLocationList" onchange="getEmployeeList();"/>

<% } else { %>
	<s:select cssClass="validateRequired" name="location" id="locationid" theme="simple" 
		listKey="wLocationId" listValue="wLocationName" headerKey="" headerValue="Select Location" list="workLocationList" />
<% } %>
