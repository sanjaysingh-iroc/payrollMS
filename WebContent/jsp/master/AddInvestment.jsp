<%@page import="com.konnect.jpms.select.FillSection"%>
<%@page import="com.konnect.jpms.select.FillLevel"%>
<%@page import="com.konnect.jpms.select.FillUserStatus"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<script>

</script>
 
<s:form theme="simple" id="formAddNewRow" action="AddInvestment" method="POST"
	cssClass="formcss" cssStyle="display: none;">

	<table border="0" class="formcss" style="width: 400px; float:left">
		 
		<tr>
			<td class="txtlabel alignRight">Section<sup>*</sup>:</td>
			<td>
				<select name="section" id="section" rel="0" onchange="getContent('GetSectionDesc.action?SID='+this.value)">
					<% java.util.List  sectionList = (java.util.List) request.getAttribute("sectionList"); %>
					<% for (int i=0; i<sectionList.size(); i++) { %>
					<option value=<%= ((FillSection)sectionList.get(i)).getSectionId() %>> <%= ((FillSection)sectionList.get(i)).getSectionCode() %></option>
					<% } %>
				</select>
			</td>
		</tr>
	
		<tr>
			<td class="txtlabel alignRight">Amount Paid<sup>*</sup>:</td>
			<td>
				<input type="text" name="amountPaid" id="amountPaid" rel="1" class="required" /> 
				<span class="hint">Designation Name<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>
		
		<tr>
			<td></td>
			<td>
				<input type="hidden" rel="2" value="" />
			</td>
		</tr>
		
		<tr>
			<td></td>
			<td>
				<s:submit cssClass="input_button" value="Ok" id="btnAddNewRowOk" /> 
				<s:submit cssClass="input_button" value="Cancel" id="btnAddNewRowCancel" />
			</td>
		</tr>


	</table>
	
	<div id="myDiv">
	</div>
	
	
</s:form>

