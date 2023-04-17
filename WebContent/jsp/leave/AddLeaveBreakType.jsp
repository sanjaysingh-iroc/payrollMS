<%@page import="com.konnect.jpms.select.FillColour"%>
<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<div class="aboveform">

<s:form theme="simple" action="AddLeaveBreakType" method="POST" id="formAddNewRow" cssClass="formcss">

<s:token></s:token> 

	<s:hidden name="breakTypeId" />
	<s:hidden name="orgId" />
	<s:hidden name="strLocation" />
	
	<table class="formcss" style="width:370px">
		<tr><td colspan=2><s:fielderror/></td></tr>
		<tr>
			<td class="txtlabel alignRight">Break Type:<sup>*</sup></td>
			<td>
				<s:textfield name="breakType" id="leaveType" cssClass="required"/>
				<span class="hint">Add new break type here.<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight">Break Code:<sup>*</sup></td>
			<td>
				<s:textfield name="breakCode" id="leaveCode" cssClass="required"/>
				<span class="hint">Add new break type here.<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight">Choose Colour<sup>*</sup>:</td>
			<td>
			
			<%-- <select rel="1" name="strColour" id="strColour">
					<% java.util.List  colourList = (java.util.List) request.getAttribute("colourList"); %>
					<% for (int i=0; colourList!=null && i<colourList.size(); i++) { %>
					<option value=<%= ((FillColour)colourList.get(i)).getColourValue() %>> <%= ((FillColour)colourList.get(i)).getColourName() %></option>
					<% } %>
			</select> --%>
						
			<s:select list="colourList" name="strColour" listKey="colourValue" listValue="colourName"></s:select> 			
						
						
			<span class="hint ml_25">Choose a colour for this roster. This colour will be marked in timesheets and clock entries.
				<span class="hint-pointer">&nbsp;</span>
			</span> </td>
		</tr>
		
		
		<tr>
			<td colspan="2" align="center"><s:submit cssClass="input_button" value="Save" id="btnAddNewRowOk"/></td>
		</tr>
	
	</table>
</s:form>

</div>
