<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<script type="text/javascript">

$( function () {
	$("#submitButton").click(function(){
		$(".validateRequired").prop('required',true);
	});
});	
	
function showHideSystemInfo(value) {
	if(value == '1') {
		document.getElementById("systemInfoTR").style.display = 'table-row';
		$("#systemInfo").prop('required',true);
	} else {
		document.getElementById("systemInfo").selectedIndex = '0';
		document.getElementById("systemInfoTR").style.display = 'none';
		$("#systemInfo").prop('required',false);
		$("#systemInfo").removeClass("validateRequired");
	}
}
</script>

<div>   

	<s:form id="frmId11" method="POST" theme="simple" action="AddAppraisalAttribute">
		<s:hidden name="elementid"></s:hidden>
		<s:hidden name="operation"></s:hidden> 
		<s:hidden name="attributeid"></s:hidden>
		<s:hidden name="strOrg"></s:hidden>
		<s:hidden name="userscreen" />
		<s:hidden name="navigationId" />
		<s:hidden name="toPage" />
					
		<table class="table table_no_border">
			<tr>
				<td nowrap="nowrap" align="right">Attribute Name<sup>*</sup></td>
				<td><s:textfield name="attributeName" cssClass="validateRequired" cssStyle="width: 98%;"/></td>
			</tr>

			<tr>
				<td valign="top" nowrap="nowrap" align="right">Attribute Description<sup>*</sup></td>
				<td>
					<s:textarea name="attributeDesc" cssClass="validateRequired" id="attributeDesc" rows="9" cols="64"></s:textarea>
				</td>
			</tr>
			<tr>
				<td nowrap="nowrap" align="right">Align to System Information<sup>*</sup></td>
				<td>
					<s:radio name="isSystemInfo" id="isSystemInfo" list="#{'1':'Yes','0':'No'}" value="isSystemInfo" onclick="showHideSystemInfo(this.value);"/>
				</td>
			</tr>
			<% String isSystemInfo = (String)request.getAttribute("isSystemInfo");
				String trStatus = "none";
				if(isSystemInfo != null && isSystemInfo.equals("1")) {
					trStatus = "table-row";
				}
			%>
			<tr id="systemInfoTR" style="display: <%=trStatus %>;">
				<td nowrap="nowrap" align="right">System Information<sup>*</sup></td>
				<td>
					<%-- <s:select name="systemInfo" id="systemInfo" headerKey="" headerValue="Select Information" cssClass="validateRequired"
					list="#{'1':'Punctuality KPI', '2':'Attendance KPI', '3':'Efforts KPI',
					 '4':'Work Performance KPI', '5':'Quality of Work KPI'}" /> --%>
					 <%if(isSystemInfo != null && isSystemInfo.equals("1")) { %>
						 <s:select name="systemInfo" id="systemInfo" headerKey="" headerValue="Select Information" cssClass="validateRequired"
						list="#{'1':'Punctuality KPI', '2':'Attendance KPI', '3':'Efforts KPI'}" />
					 <% }else{ %>
						<s:select name="systemInfo" id="systemInfo" headerKey="" headerValue="Select Information"
						list="#{'1':'Punctuality KPI', '2':'Attendance KPI', '3':'Efforts KPI'}" />
					 <%} %>
				</td>
			</tr>
			
			<tr>
				<td colspan="2">
					<div style="float: left; width: 100%; text-align: center;">
						<s:submit name="submit" value="Save" cssClass="btn btn-primary" id="submitButton"></s:submit>
					</div>
				</td>
			</tr>

		</table>

	</s:form>
</div>
