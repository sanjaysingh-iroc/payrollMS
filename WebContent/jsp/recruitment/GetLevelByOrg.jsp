<%@ taglib prefix="s" uri="/struts-tags"%>

<% 
	String fromPage = (String) request.getAttribute("fromPage");
	//System.out.println("fromPage in getLevelByOrg==>"+fromPage);

	if(fromPage != null && fromPage.endsWith("LAFORM")) {
%>
	<s:select name="f_level" id="f_level" theme="simple" listKey="levelId" listValue="levelCodeName" headerKey=""
		headerValue="Select Level" list="levelslist" required="true" onchange="getEmployeeList();" />

<%} else if(fromPage != null && fromPage.equals("EL")) {%>
	<s:select name="f_level" id="f_level" theme="simple" listKey="levelId" listValue="levelCodeName" headerKey=""
		headerValue="Select Level" list="levelslist" required="true" onchange="getEmployeeList();" />

<% } else { %>
	<s:select cssClass="validateRequired" name="strLevel" theme="simple" listKey="levelId" listValue="levelCodeName" headerKey=""
		headerValue="Select Level" list="levelslist" required="true" onchange="getDesig(this.value);" />
<% } %>	