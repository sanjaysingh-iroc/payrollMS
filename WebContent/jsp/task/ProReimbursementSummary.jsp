<%@page import="java.util.List"%>
<% List alReport = (List)request.getAttribute("alReport"); %>



<table class="tb_style"  width="100%">

<%
	for(int i=0; alReport!=null && i<alReport.size(); i++){
		List alInner = (List)alReport.get(i);
		%>
		<tr>
			<td><%=alInner.get(0) %></td>
		</tr>
		<%
	}
%>


<%
if(alReport!=null && alReport.size()==0){
	%>

<tr>
<td> <div style="width:96%" class="msg nodata"><span>No expenses reported as of yet.</span></div></td>
</tr>	
	<%
}
%>

</table>