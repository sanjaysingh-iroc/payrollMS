<%@taglib uri="/struts-tags" prefix="s"%>

<%-- <s:form name="frm" action="ReAssign" theme="simple"> --%>
	<table>
		<tr>
			<td class="txtlabel alignRight" style="width: 110px;">Username:</td>
			<td><%=(String)request.getAttribute("username") %></td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight" style="width: 110px;">Password:</td>
			<td><%=(String)request.getAttribute("password") %></td>
		</tr>
	</table>

<%-- </s:form> --%>