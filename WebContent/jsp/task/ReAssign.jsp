<%@taglib uri="/struts-tags" prefix="s"%>

<s:form name="frm" action="ReAssign" theme="simple">
	
	<s:hidden name="strTaskId"></s:hidden>
	<table>
		<tr>
			<td>Completion Status (%)</td>
			<td><s:textfield name="strReAssign"></s:textfield></td>
		</tr>
		
		<tr>
			<td colspan="2"><s:submit cssClass="input_button" value="Re-Assign Task" ></s:submit></td>
		</tr>
	</table>

</s:form>