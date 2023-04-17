
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Iterator"%>
<%@page import="com.konnect.jpms.select.FillUserStatus"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<script>
$(function(){
	$("body").on("click","#btnAddNewRowOk",function(){
    	$('.validateRequired').filter(':hidden').prop('required', false);
		$('.validateRequired').filter(':visible').prop('required', true);
    });
});

</script>

	<% String operation = (String)request.getAttribute("operation");%>
	
	<% if(operation != null && operation.equals("VIEW")) { %>
		<s:form theme="simple" id="formAddNewRow" action="UpdateVDAIndex" method="POST" cssClass="formcss">
			<table class="table">
				<s:hidden name="strOrgId" id="strOrgId"></s:hidden>
				<s:hidden name="userscreen" />
				<s:hidden name="navigationId" />
				<s:hidden name="toPage" />
				<s:hidden name="operation" />
				<tr>
					<td valign="top" class="txtlabel alignRight">Organization:</td>
					<td colspan="3">
					<% String strOrgName = (String)request.getAttribute("strOrgName");%>
						<%=strOrgName %>
					</td>
				</tr>
				<tr>
					<th>Designation Name</th>
					<th>Probation</th>
					<th>Permanent</th>
					<th>Temporary</th>
				</tr>
				<% 
				Map<String, Map<String, String>> hmVDAPaycycleAmountData = (Map<String, Map<String, String>>) request.getAttribute("hmVDAPaycycleAmountData");
				if(hmVDAPaycycleAmountData == null) hmVDAPaycycleAmountData = new HashMap<String, Map<String, String>>();
				
				Iterator<String> itVDAAmt = hmVDAPaycycleAmountData.keySet().iterator(); 
				while(itVDAAmt.hasNext()) {
					String strDesigId = (String)itVDAAmt.next();
					Map<String, String> hmInner = (Map<String, String>)hmVDAPaycycleAmountData.get(strDesigId);
				%>
					<tr>
	                   <td><%=hmInner.get("DESIG_NAME") %></td>
	                   <td><%=hmInner.get("VDA_AMOUNT_PROBATION") %></td>
	                   <td><%=hmInner.get("VDA_AMOUNT_PERMANENT") %></td>
	                   <td><%=hmInner.get("VDA_AMOUNT_TEMPORARY") %></td>
					</tr>
				<%} %>
			                     
				<tr>
					<td colspan="4" align="center">
						<s:submit cssClass="btn btn-primary" value="Submit" id="btnAddNewRowOk" /> 
					</td>
				</tr>
			</table>
			
		</s:form>
	<% } else { %>
		<s:form theme="simple" id="formAddNewRow" action="UpdateVDAIndex" method="POST" cssClass="formcss">
			<table class="table">
				<s:hidden name="strOrgId" id="strOrgId"></s:hidden>
				<s:hidden name="userscreen" />
				<s:hidden name="navigationId" />
				<s:hidden name="toPage" />
				<s:hidden name="operation" />
				<tr>
					<td valign="top" class="txtlabel alignRight">Organization:</td>
					<td colspan="3">
					<% String strOrgName = (String)request.getAttribute("strOrgName");%>
						<%=strOrgName %>
					</td>
				</tr>
				<tr>
					<th>Designation Name</th>
					<th>Probation</th>
					<th>Permanent</th>
					<th>Temporary</th>
				</tr>
				<% 
				Map<String, Map<String, String>> hmVDAIndexData = (Map<String, Map<String, String>>) request.getAttribute("hmVDAIndexData");
				if(hmVDAIndexData == null) hmVDAIndexData = new HashMap<String, Map<String, String>>();
				
				Iterator<String> itVDAIndex = hmVDAIndexData.keySet().iterator(); 
				while(itVDAIndex.hasNext()) {
					String strDesigId = (String)itVDAIndex.next();
					Map<String, String> hmInner = (Map<String, String>)hmVDAIndexData.get(strDesigId);
				%>
					<tr>
	                   <td>
	                   <input type="hidden" name="strDesigIds" value="<%=strDesigId %>" />
	                   <%=hmInner.get("DESIG_NAME") %></td>
	                   <td><input type="text" name="<%=strDesigId %>_vdaRateProbation" id="<%=strDesigId %>_vdaRateProbation" style="width: 90px !important;" value="<%=hmInner.get("VDA_INDEX_PROBATION") %>" /></td>
	                   <td><input type="text" name="<%=strDesigId %>_vdaRatePermanent" id="<%=strDesigId %>_vdaRatePermanent" style="width: 90px !important;" value="<%=hmInner.get("VDA_INDEX_PERMANENT") %>" /></td>
	                   <td><input type="text" name="<%=strDesigId %>_vdaRateTemporary" id="<%=strDesigId %>_vdaRateTemporary" style="width: 90px !important;" value="<%=hmInner.get("VDA_INDEX_TEMPORARY") %>" /></td>
					</tr>
				<%} %>
			                     
				<tr>
					<td colspan="4" align="center">
						<s:submit cssClass="btn btn-primary" value="Submit" id="btnAddNewRowOk" /> 
					</td>
				</tr>
			</table>
			
		</s:form>
	<% } %>
