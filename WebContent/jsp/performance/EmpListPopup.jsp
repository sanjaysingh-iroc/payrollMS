
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%List<String> emplist=(List<String>)request.getAttribute("emplist"); %>
	<div style="float: left;">
		<ul style="float: left; padding-left: 0px;">
			<%
				for (int i = 0; emplist != null && !emplist.isEmpty() && i < emplist.size(); i++) {
			%>

			<li class="ImageDiv" style="font-size: 4em;"><%=emplist.get(i)%></li> <!-- printli -->
			<%
				}
			%>
		</ul>
	</div>
