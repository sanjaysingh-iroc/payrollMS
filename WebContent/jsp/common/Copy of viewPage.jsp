<%@page import="com.opensymphony.xwork2.ActionContext,java.util.Map,java.util.HashMap"%>
<%

	String strPage = (String)request.getAttribute("PAGE");
	String strMenu = (String)session.getAttribute("MENU");
	String strTitle = (String)request.getAttribute("TITLE");
	

	
	if (strPage == null) {
		strPage = "Login.jsp";
	}
	
	if (strTitle == null) {
		strTitle = "Payroll Management System";
	}
	if (strMenu == null) {
		strMenu = "PreMenu.jsp";
		strPage = "Login.jsp";
	}
%>


<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<jsp:include page="../common/Links.jsp" flush="true" />
<title><%=strTitle%></title>
</head>
<body>



<table cellpadding="0" cellspacing="0"  width="100%">
	<tr>
		<td><jsp:include page="Header.jsp" flush="true" /></td>
	</tr>

	<tr>
		<td align="center"><jsp:include page="<%=strMenu%>" flush="true" /></td>
	</tr>
	
	<tr>
		<td align="center"><jsp:include page="<%=strPage%>" flush="true" /></td>
	</tr>

	<tr>
		<td><jsp:include page="Footer.jsp" flush="true" /></td>
	</tr>
</table>

</body>

</html>