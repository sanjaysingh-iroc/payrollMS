<%@taglib uri="/struts-tags" prefix="s"%>

<% String operation = (String)request.getAttribute("operation"); %>

<s:form name="frm" action="ProjectTimesheetDenyByCustomer" theme="simple">
	
	<s:hidden name="timesheetType"></s:hidden>
	<s:hidden name="proID"></s:hidden>
	<s:hidden name="proFreqID"></s:hidden>
	
	<table>
		<tr>
			<td valign="top">Comment:</td>
			<td>
			<% if(operation != null && operation.equals("V")) { %>
				<%=(String)request.getAttribute("strComment") %>
			<% } else { %>
				<s:textarea name="strComment" cols="40" rows="5"></s:textarea>
			<% } %>	
			</td>
		</tr>
		<% if(operation == null || !operation.equals("V")) { %>
		<s:hidden name="operation" value="A"></s:hidden>
		<tr>
			<td colspan="2" align="center"><s:submit cssClass="cancel_button" value="Deny" ></s:submit></td>
		</tr>
		<% } %>
	</table>

</s:form>