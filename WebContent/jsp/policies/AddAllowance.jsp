<%@ page import="com.konnect.jpms.select.FillDesignation"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ page import="com.konnect.jpms.util.*"%>
<%@ taglib uri="/struts-tags" prefix="s"%>

<script>
	addLoadEvent(prepareInputsForHints);
</script>


<%
	String strEMPID = (String) request.getParameter("EMPID");
	String strE = (String) request.getParameter("E");
	boolean isEParam = false;

	if (strEMPID != null || strE != null) {
		isEParam = true;
	}

	String strEmpType = (String) session.getAttribute("USERTYPE");
	
%>

<s:form theme="simple" action="AddAllowance.action" method="POST" id="formAddNewRow" cssClass="formcss" cssStyle="display: none;">

	<s:hidden name="allowanceId" />
	<s:hidden name="empID" />
	<table border="0" class="formcss" style="width:675px">
		<tr>
			<td colspan=2><s:fielderror /></td>
		</tr>

		<tr>
			<td class="txtlabel alignRight">Select Employee<sup>*</sup>:</td>
			<td>
			<%
				if (isEParam) {
			%> 
			
			<select rel="2" name="designation" id="designation">
						<% java.util.List  desigList = (java.util.List) request.getAttribute("desigList"); %>
						<% for (int i=0; i<desigList.size(); i++) { %>
						<option value=<%= ((FillDesignation)desigList.get(i)).getDesigId() %> > <%= ((FillDesignation)desigList.get(i)).getDesigName() %></option>
						<% } %>
			</select>
			
				<%
 	} else {
 %> 			<select rel="2" name="designation" id="designation">
						<% java.util.List  desigList = (java.util.List) request.getAttribute("desigList"); %>
						<% for (int i=0; i<desigList.size(); i++) { %>
						<option value=<%= ((FillDesignation)desigList.get(i)).getDesigId() %> > <%= ((FillDesignation)desigList.get(i)).getDesigName() %></option>
						<% } %>
				</select>
				
				<%
 	}
 %>
			</td>
		</tr>

		<tr>
			<td class="txtlabel alignRight">Allowance Hours<sup>*</sup>:</td>
			<td><input type="text" name="allowanceHours" id="textMsg" rel="0" class="required"/>
			<span class="hint">Add hours in number
			format. On completion of these hours desired employee will get the
			allowance.<span class="hint-pointer">&nbsp;</span></span></td>
		</tr>

		<tr>
			<td class="txtlabel alignRight">Allowance Amount (%)<sup>*</sup>:</td>
			<td><input type="text" name="allowanceAmount" id="allowanceAmount" rel="1" class="required"/>
			<span class="hint">Enter
			allowance amount(%) in number format and will be calculated on the
			net income.<span class="hint-pointer">&nbsp;</span></span></td>
		</tr>

		<tr>
			<td></td>
			<td><s:submit cssClass="input_button" value="Ok" id="btnAddNewRowOk"/>
			<s:submit  cssClass="input_button" value="Cancel" id="btnAddNewRowCancel"/></td>
		</tr>

	</table>

</s:form>