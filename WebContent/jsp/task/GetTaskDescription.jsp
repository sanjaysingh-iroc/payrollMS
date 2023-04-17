<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="java.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags"%>


<% 
	UtilityFunctions uF = new UtilityFunctions();
	String taskId = (String) request.getAttribute("taskId");
	if(uF.parseToInt(taskId) > 0) {
		boolean billableFlag = (Boolean)request.getAttribute("isBillable");
		String strIsBillable = "NO";
		if(billableFlag) {
			strIsBillable = "YES";
		}
%>
<%=strIsBillable %>
<% } else { %>
	<div class="leftbox reportWidth" style="min-height: 200px; width: 90%;">
		<div style="width: 100%;"><%=(String) request.getAttribute("taskDescription") %></div>
	</div>
<% } %>