<%@page import="com.konnect.jpms.select.FillCountry"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<script>
	addLoadEvent(prepareInputsForHints);
</script>
 
 


     
		<s:form theme="simple" action="AddState" method="POST" cssClass="formcss" id="formAddNewRow" cssStyle="display: none;">
		
			<s:hidden name="stateId" />
			<table border="0" class="formcss" style="width:675px">
			<tr><td colspan=2><s:fielderror/></td></tr>
			
			<tr>
					<td class="txtlabel alignRight"><label for="State">State</label><br/></td>
					<td><input type="text" name="stateName" id="stateName" rel="0" class="required"/><span class="hint">Add new state.<span class="hint-pointer">&nbsp;</span></span></td> 
			</tr>
				
			<tr>
					<td class="txtlabel alignRight">Select Country<sup>*</sup>:</td>
					<td>
						<select rel="1" name="country" id="country">
								<% java.util.List  countryList = (java.util.List) request.getAttribute("countryList"); %>
								<% for (int i=0; i<countryList.size(); i++) { %>
								<option value=<%= ((FillCountry)countryList.get(i)).getCountryId() %>> <%= ((FillCountry)countryList.get(i)).getCountryName() %></option>
								<% } %>
						</select>
					<span class="hint">Select country to add.<span class="hint-pointer">&nbsp;</span></span>
					</td>
			</tr>
				
			<tr>
					<td></td>
					<td><s:submit cssClass="input_button" value="Ok" id="btnAddNewRowOk"/>
					<s:submit  cssClass="input_button" value="Cancel" id="btnAddNewRowCancel"/></td>
			</tr>
			
			</table>
		</s:form>
