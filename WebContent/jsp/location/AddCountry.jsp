<%@taglib uri="/struts-tags" prefix="s"%>

<script>
	addLoadEvent(prepareInputsForHints);
</script>
 

 

		<s:form theme="simple" action="AddCountry" method="POST" cssClass="formcss" id="formAddNewRow" cssStyle="display: none;">
		
			<s:hidden name="countryId" />
			<table border="0" class="formcss" style="width:675px">
			
				<tr><td colspan=2><s:fielderror/></td></tr>
				
				<tr>
					<td class="txtlabel alignRight">Country<sup>*</sup>:</td>
					<td><input type="text" name="country" id="country" rel="0" class="required" />
					<span class="hint">Add new country here.<span class="hint-pointer">&nbsp;</span></span></td>
				</tr>
				
				<tr>
					<td></td>
					<td><s:submit cssClass="input_button" value="Ok" id="btnAddNewRowOk" />
					<s:submit  cssClass="input_button" value="Cancel" id="btnAddNewRowCancel" /></td>
				</tr>
			
			</table>
			
		</s:form>

