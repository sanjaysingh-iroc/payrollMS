<%@ taglib prefix="s" uri="/struts-tags"%>

<%
	String type = (String)request.getAttribute("type"); 
	String validReq = (String) request.getAttribute("validReq");
%>
<s:if test="stateList != null">
<% if(type == null || type.equals("PADD")) { %>
		<% if(validReq != null && !validReq.equals("")) { %>
			<s:select theme="simple" label="Select State" name="state" id="state" cssClass="validateRequired" listKey="stateId" listValue="stateName"
				headerKey="" headerValue="Select State" list="stateList" key="" required="true" />
		<% } else { %>
			<s:select theme="simple" label="Select State" name="state" id="state" listKey="stateId" listValue="stateName" headerKey="" 
				headerValue="Select State" list="stateList" key="" />
		<% } %>
	<% } else if(type != null && type.equals("TADD")) { %>
		<% if(validReq != null && !validReq.equals("")) { %>
			<s:select theme="simple" label="Select State" name="stateTmp" id="stateTmp" cssClass="validateRequired" listKey="stateId" listValue="stateName" 
				headerKey="" headerValue="Select State" list="stateList" key="" required="true" />
		<% } else { %>
			<s:select theme="simple" label="Select State" name="stateTmp" id="stateTmp" listKey="stateId" listValue="stateName" 
				headerKey="" headerValue="Select State" list="stateList" key=""/>
		<% } %>
	<% } else if(type != null && type.equals("CLIENTADDSTATE")) { %>
		<% if(validReq != null && !validReq.equals("")) { %>
			<s:select theme="simple" title="state" cssClass="validateRequired" id="strClientState" name="strClientState" listKey="stateId" 
				listValue="stateName" headerKey="" headerValue="Select State" list="stateList" key="" required="true" />
		<% } else { %>
			<s:select theme="simple" title="state" id="strClientState" name="strClientState" listKey="stateId" listValue="stateName" 
				headerKey="" headerValue="Select State" list="stateList" key="" />
		<% } %>	
	<% } else if(type != null && type.equals("CLIENTBRANDADDSTATE")) { %>
		<% if(validReq != null && !validReq.equals("")) { %>
		<s:select theme="simple" title="state" cssClass="validateRequired" id="strClientBrandState" name="strClientBrandState" listKey="stateId" 
			listValue="stateName" headerKey="" headerValue="Select State" list="stateList" key="" required="true" />
	<% } else { %>
		<s:select theme="simple" title="state" id="strClientBrandState" name="strClientBrandState" listKey="stateId" listValue="stateName" 
			headerKey="" headerValue="Select State" list="stateList" key="" />
	<% } %>	
	<% } %>
</s:if> 
  