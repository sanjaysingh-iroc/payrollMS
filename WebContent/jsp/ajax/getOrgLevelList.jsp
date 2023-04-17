<%@ taglib prefix="s" uri="/struts-tags"%>

<% String type = (String) request.getAttribute("type"); %>

<% if(type != null && type.equals("EA")) { %>
	<s:select name="strLevel" id="strLevel" theme="simple" listKey="levelId" cssClass="validateRequired" listValue="levelCodeName" headerKey="" 
		headerValue="Select Level" onchange="showDesignation(this.value);" list="levelList" key="" />
<% } else { %>
	<s:select name="strLevel" list="levelList" listKey="levelId" id="levelIdV" listValue="levelCodeName" headerKey="" theme="simple" 
	headerValue="Select Level" cssClass="validateRequired" onchange="getDesigAndLeave(this.value);" />
<% } %>