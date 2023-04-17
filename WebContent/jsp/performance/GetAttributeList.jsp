<%@ taglib prefix="s" uri="/struts-tags"%>

	<%
		String type = (String)request.getAttribute("type");
		String goalCnt = (String)request.getAttribute("goalCnt");
		String sbOption = (String)request.getAttribute("sbOption");
		if(type != null && !type.equals("") && type.equals("MULTIKRA")) {
	%>
	<select name="cgoalAlignAttribute<%=goalCnt %>" class="validateRequired">
		<option value="">Select Attribute</option>
		<%=sbOption %>
	</select>
	<% } else { %> 
	<s:select theme="simple" name="cgoalAlignAttribute" cssClass="validateRequired" list="attributeList" listKey="id" 
		listValue="name" headerKey="" headerValue="Select Attribute"/>
		<% } %>