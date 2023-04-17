<%@page contentType="text/html" pageEncoding="UTF-8"%>

<script src="<%= request.getContextPath()%>/scripts/submenu/subMenuEmployee.js" type="text/javascript"></script>
<%
	if (session.getAttribute("USERTYPE") != null) {
%>
<div id="menuLink"></div>


<%
	}
%>