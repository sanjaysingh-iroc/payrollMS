
<%@page import="java.util.*"%>
<%

List alReport = (List)request.getAttribute("alReport");
List alReportLabel = (List)request.getAttribute("alReportLabel");




%>


<table>

<%for(int i=0; i<alReportLabel.size(); i++){ %>
<th><%=(String)alReportLabel.get(i) %></th>
<%} %>

<%
for(int i=0; i<alReport.size(); i++){
	List alInner = (List)alReport.get(i);
%>
<tr>
<%for(int j=0; j<alInner.size(); j++){ %>
<td><%=(String)alInner.get(j) %></td>

<%} %>
</tr>
<%} %>
	
</table>