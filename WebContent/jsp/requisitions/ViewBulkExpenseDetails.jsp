<%@page import="java.util.List"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<%  
	UtilityFunctions uF = new UtilityFunctions();
	Map<String, List<Map<String, String>>> hmBulkExpenseData = (Map<String, List<Map<String, String>>>)request.getAttribute("hmBulkExpenseData") ;
	if (hmBulkExpenseData == null) hmBulkExpenseData = new HashMap<String, List<Map<String, String>>>();
%>
	
<div style="padding: 5px; overflow-y: auto;">
<% 
	
	List<Map<String, String>> alBulkExpenseData = hmBulkExpenseData.get("T");
	if(alBulkExpenseData != null && alBulkExpenseData.size()>0) {
%>
	<table border="0" class="table pdzn_tbl1">
		<tr>
			<th  class="txtlabel alignCenter">Employee:</th>
			<th  class="txtlabel alignCenter">PayCycle:</th>
			<th  class="txtlabel alignCenter">Applied Date:</th>
			<%-- <% if(uF.showData(hmReimbursement.get("REIMB_FROM_DATE"),"").length()>0) { %>
			<th class="txtlabel alignCenter">Reimbursement Date:</th>
			<% } %> --%>
			<th  class="txtlabel alignCenter">Reimbursement Type:</th>
			
			<th  class="txtlabel alignCenter">Travel Plan</th>
			<th  class="txtlabel alignCenter">Number of persons:</th>
			<th  class="txtlabel alignCenter">Transportation Type:</th>
			<th  class="txtlabel alignCenter">Transportation Mode:</th>
			<th  class="txtlabel alignCenter">Transport Amount:</th>
			<th  class="txtlabel alignCenter">Lodging Type:</th>
			<th  class="txtlabel alignCenter">Lodging Amount:</th>
			<th  class="txtlabel alignCenter">Local Conveyance Type:</th>
			<th  class="txtlabel alignCenter">Total KM:</th>
			<th  class="txtlabel alignCenter">Local Conveyance Amount:</th>
			<th  class="txtlabel alignCenter">Food &amp; Beverage:</th>
			<th  class="txtlabel alignCenter">Laundry:</th>
			<th  class="txtlabel alignCenter">Sundry:</th>
			
			<th  class="txtlabel alignCenter">Total Amount:</th>
			<th  class="txtlabel alignCenter">Vendor:</th>
			<th  class="txtlabel alignCenter">Receipt No:</th>
			<th  class="txtlabel alignCenter">Purpose:</th>
			<th  class="txtlabel alignCenter">Attach Document:</th>
			
		</tr>
		<% 
		for(int i=0; alBulkExpenseData != null && i<alBulkExpenseData.size(); i++) {
		Map<String, String> hmReimbursement = alBulkExpenseData.get(i); %>
		
		<tr style="text-align: center;">
			<td><%=uF.showData(hmReimbursement.get("EMP_NAME"),"") %></td>
			<td><%=uF.showData(hmReimbursement.get("PAYCYCLE"),"") %></td>
			<td><%=uF.showData(hmReimbursement.get("APPLIED_DATE"),"") %></td>
			<%-- <% if(uF.showData(hmReimbursement.get("REIMB_FROM_DATE"),"").length()>0) { %>
				<td><%=uF.showData(hmReimbursement.get("REIMB_FROM_DATE"),"") %></td>
			<% } %> --%>
			<td><%=uF.showData(hmReimbursement.get("REIMBURSEMENT_TYPE"),"") %></td>
			
			<td><%=uF.showData(hmReimbursement.get("REIMBURSEMENT_TRAVEL_PLAN"),"") %></td>
			<td><%=uF.showData(hmReimbursement.get("NO_OF_PERSON"),"0") %></td>
			<td><%=uF.showData(hmReimbursement.get("TRANSPORT_TYPE"),"") %></td>
			<td><%=uF.showData(hmReimbursement.get("TRANSPORT_MODE"),"") %></td>
			<td><%=uF.showData(hmReimbursement.get("TRANSPORT_AMOUNT"),"0") %></td>
			<td><%=uF.showData(hmReimbursement.get("LODGING_TYPE"),"") %></td>
			<td><%=uF.showData(hmReimbursement.get("LODGING_AMOUNT"),"0") %></td>
			<td><%=uF.showData(hmReimbursement.get("LOCAL_CONVEYANCE_TYPE"),"") %></td>
			<td><%=uF.showData(hmReimbursement.get("LOCAL_CONVEYANCE_KM"),"0") %>&nbsp;&nbsp;
		 		Rate/KM:&nbsp;<%=uF.showData(hmReimbursement.get("LOCAL_CONVEYANCE_RATE"),"0") %>
		 	</td>
		 	<td><%=uF.showData(hmReimbursement.get("LOCAL_CONVEYANCE_AMOUNT"),"0") %></td>
		 	<td><%=uF.showData(hmReimbursement.get("FOOD_BEVERAGE_AMOUNT"),"0") %></td>
		 	<td><%=uF.showData(hmReimbursement.get("LAUNDRY_AMOUNT"),"0") %></td>
		 	<td><%=uF.showData(hmReimbursement.get("SUNDRY_AMOUNT"),"0") %></td>
			
			<td><%=uF.showData(hmReimbursement.get("REIMBURSEMENT_CURRENCY"),"-") %></td>
			<td><%=uF.showData(hmReimbursement.get("REIMBURSEMENT_AMOUNT"),"0") %></td>
			<td><%=uF.showData(hmReimbursement.get("REIMBURSEMENT_PAYMENT_MODE"),"-") %></td>
			
			<td><%=uF.showData(hmReimbursement.get("VENDOR"),"") %></td>
			<td><%=uF.showData(hmReimbursement.get("RECEIPT_NO"),"") %></td>
			<td><%=uF.showData(hmReimbursement.get("REIMBURSEMENT_PURPOSE"),"") %></td>
			<td><%=uF.showData(hmReimbursement.get("ATTACH_DOCUMENT"),"") %></td>
		</tr>
		<% } %>
	</table>
	<% } %>
	
	
	<% 
	alBulkExpenseData = hmBulkExpenseData.get("L");
	if(alBulkExpenseData != null && alBulkExpenseData.size()>0) {
%>
	<table border="0" class="table pdzn_tbl1">
		<tr>
			<th  class="txtlabel alignCenter">Employee:</th>
			<th  class="txtlabel alignCenter">PayCycle:</th>
			<th  class="txtlabel alignCenter">Applied Date:</th>
			<%-- <% if(uF.showData(hmReimbursement.get("REIMB_FROM_DATE"),"").length()>0) { %>
			<th class="txtlabel alignCenter">Reimbursement Date:</th>
			<% } %> --%>
			<th  class="txtlabel alignCenter">Reimbursement Type:</th>
			
			<th  class="txtlabel alignCenter">Type:</th>
			<th  class="txtlabel alignCenter">Number of persons:</th>

			<th class="txtlabel alignCenter">Mode of Travel:</th>
			<th  class="txtlabel alignCenter">Place From:</th>
			<th  class="txtlabel alignCenter">Place To:</th>
			<th  class="txtlabel alignCenter">No of Days:</th>
			<th  class="txtlabel alignCenter">Total KM:</th>
			
			<th  class="txtlabel alignCenter">Currency:</th>
			<th  class="txtlabel alignCenter">Total Amount:</th>
			<th  class="txtlabel alignCenter">Payment Mode:</th>
			<th class="txtlabel alignCenter">Vendor:</th>
			<th  class="txtlabel alignCenter">Receipt No:</th>
			<th  class="txtlabel alignCenter">Purpose:</th>
			<th  class="txtlabel alignCenter">Attach Document:</th>
			
		</tr>
		<% 
		for(int i=0; alBulkExpenseData != null && i<alBulkExpenseData.size(); i++) {
		Map<String, String> hmReimbursement = alBulkExpenseData.get(i); %>
		
		<tr style="text-align: center;">
			<td><%=uF.showData(hmReimbursement.get("EMP_NAME"),"") %></td>
			<td><%=uF.showData(hmReimbursement.get("PAYCYCLE"),"") %></td>
			<td><%=uF.showData(hmReimbursement.get("APPLIED_DATE"),"") %></td>
			<%-- <% if(uF.showData(hmReimbursement.get("REIMB_FROM_DATE"),"").length()>0) { %>
				<td><%=uF.showData(hmReimbursement.get("REIMB_FROM_DATE"),"") %></td>
			<% } %> --%>
			<td><%=uF.showData(hmReimbursement.get("REIMBURSEMENT_TYPE"),"") %></td>
			
			 	<td><%=uF.showData(hmReimbursement.get("REIMBURSEMENT_INFO"),"") %></td>
			 	<td><%=uF.showData(hmReimbursement.get("NO_OF_PERSON"),"0") %></td>
			 	<%if(hmReimbursement.get("REIMBURSEMENT_INFO") != null && hmReimbursement.get("REIMBURSEMENT_INFO").trim().equalsIgnoreCase("Conveyance Bill")) { %>
			 		<td><%=uF.showData(hmReimbursement.get("TRAVEL_MODE"),"") %></td>
			 		<td><%=uF.showData(hmReimbursement.get("TRAVEL_FROM"),"") %></td>
			 		<td><%=uF.showData(hmReimbursement.get("TRAVEL_TO"),"") %></td>
			 		<td><%=uF.showData(hmReimbursement.get("NO_OF_DAYS"),"") %></td>
			 		<td><%=uF.showData(hmReimbursement.get("TRAVEL_DISTANCE"),"0") %>&nbsp;&nbsp;
				 		Rate/KM:&nbsp;<%=uF.showData(hmReimbursement.get("TRAVEL_RATE"),"0") %>
				 	</td>
			 		
			 	<% } else { %>
			 		<td>&nbsp;</td>
			 		<td>&nbsp;</td>
			 		<td>&nbsp;</td>
			 		<td>&nbsp;</td>
			 		<td>&nbsp;</td>
			 	<% } %>
			
			<td><%=uF.showData(hmReimbursement.get("REIMBURSEMENT_CURRENCY"),"-") %></td>
			<td><%=uF.showData(hmReimbursement.get("REIMBURSEMENT_AMOUNT"),"0") %></td>
			<td><%=uF.showData(hmReimbursement.get("REIMBURSEMENT_PAYMENT_MODE"),"-") %></td>
			<td><%=uF.showData(hmReimbursement.get("VENDOR"),"") %></td>
			<td><%=uF.showData(hmReimbursement.get("RECEIPT_NO"),"") %></td>
			<td><%=uF.showData(hmReimbursement.get("REIMBURSEMENT_PURPOSE"),"") %></td>
			<td><%=uF.showData(hmReimbursement.get("ATTACH_DOCUMENT"),"") %></td>
		</tr>
		<% } %>
	</table>
	<% } %>
	
	
	<% 
	alBulkExpenseData = hmBulkExpenseData.get("M");
	if(alBulkExpenseData != null && alBulkExpenseData.size()>0) {
	%>
	<table border="0" class="table pdzn_tbl1">
		<tr>
			<th  class="txtlabel alignCenter">Employee:</th>
			<th  class="txtlabel alignCenter">PayCycle:</th>
			<th " class="txtlabel alignCenter">Applied Date:</th>
			<%-- <% if(uF.showData(hmReimbursement.get("REIMB_FROM_DATE"),"").length()>0) { %>
			<th class="txtlabel alignCenter">Reimbursement Date:</th>
			<% } %> --%>
			<th class="txtlabel alignCenter">Reimbursement Type:</th>
			
			<th style="text-align:center" class="txtlabel alignCenter">Number of persons:</th>
			
			<th  class="txtlabel alignCenter">Currency:</th>
			<th  class="txtlabel alignCenter">Total Amount:</th>
			<th  class="txtlabel alignCenter">Payment Mode:</th>
			<th  class="txtlabel alignCenter">Vendor:</th>
			<th  class="txtlabel alignCenter">Receipt No:</th>
			<th  class="txtlabel alignCenter">Purpose:</th>
			<th  class="txtlabel alignCenter">Attach Document:</th>
			
		</tr>
		<% 
		for(int i=0; alBulkExpenseData != null && i<alBulkExpenseData.size(); i++) {
		Map<String, String> hmReimbursement = alBulkExpenseData.get(i); %>
		
		<tr style="text-align: center;">
			<td><%=uF.showData(hmReimbursement.get("EMP_NAME"),"") %></td>
			<td><%=uF.showData(hmReimbursement.get("PAYCYCLE"),"") %></td>
			<td><%=uF.showData(hmReimbursement.get("APPLIED_DATE"),"") %></td>
			<%-- <% if(uF.showData(hmReimbursement.get("REIMB_FROM_DATE"),"").length()>0) { %>
				<td><%=uF.showData(hmReimbursement.get("REIMB_FROM_DATE"),"") %></td>
			<% } %> --%>
			<td><%=uF.showData(hmReimbursement.get("REIMBURSEMENT_TYPE"),"") %></td>
			
			<td><%=uF.showData(hmReimbursement.get("NO_OF_PERSON"),"0") %></td>
			 	
			<td><%=uF.showData(hmReimbursement.get("REIMBURSEMENT_CURRENCY"),"-") %></td>
			<td><%=uF.showData(hmReimbursement.get("REIMBURSEMENT_AMOUNT"),"0") %></td>
			<td><%=uF.showData(hmReimbursement.get("REIMBURSEMENT_PAYMENT_MODE"),"-") %></td>
			<td><%=uF.showData(hmReimbursement.get("VENDOR"),"") %></td>
			<td><%=uF.showData(hmReimbursement.get("RECEIPT_NO"),"") %></td>
			<td><%=uF.showData(hmReimbursement.get("REIMBURSEMENT_PURPOSE"),"") %></td>
			<td><%=uF.showData(hmReimbursement.get("ATTACH_DOCUMENT"),"") %></td>
		</tr>
		<% } %>
	</table>
	<% } %>
	
	
	<% 
	alBulkExpenseData = hmBulkExpenseData.get("P");
	if(alBulkExpenseData != null && alBulkExpenseData.size()>0) {
%>
	<table border="0" class="table pdzn_tbl1">
		<tr>
			<th  class="txtlabel alignCenter">Employee:</th>
			<th  class="txtlabel alignCenter">PayCycle:</th>
			<th  class="txtlabel alignCenter">Applied Date:</th>
			<%-- <% if(uF.showData(hmReimbursement.get("REIMB_FROM_DATE"),"").length()>0) { %>
			<th class="txtlabel alignCenter">Reimbursement Date:</th>
			<% } %> --%>
			<th  class="txtlabel alignCenter">Reimbursement Type:</th>
			
			<th  class="txtlabel alignCenter">Client:</th>
			<th  class="txtlabel alignCenter">Chargeable to client:</th>
			<th  class="txtlabel alignCenter">Project:</th>
			<th  class="txtlabel alignCenter">Type:</th>
			<th  class="txtlabel alignCenter">Number of persons:</th>
			<th  class="txtlabel alignCenter">Mode of Travel:</th>
			<th  class="txtlabel alignCenter">Place From:</th>
			<th  class="txtlabel alignCenter">Place To:</th>
			<th  class="txtlabel alignCenter">No of Days:</th>
			<th  class="txtlabel alignCenter">Total KM:</th>
			
			<th  class="txtlabel alignCenter">Currency:</th>
			<th class="txtlabel alignCenter">Total Amount:</th>
			<th  class="txtlabel alignCenter">Payment Mode:</th>
			<th  class="txtlabel alignCenter">Vendor:</th>
			<th  class="txtlabel alignCenter">Receipt No:</th>
			<th  class="txtlabel alignCenter">Purpose:</th>
			<th  class="txtlabel alignCenter">Attach Document:</th>
			
		</tr>
		<% 
		for(int i=0; alBulkExpenseData != null && i<alBulkExpenseData.size(); i++) {
		Map<String, String> hmReimbursement = alBulkExpenseData.get(i); %>
		
		<tr style="text-align: center;">
			<td><%=uF.showData(hmReimbursement.get("EMP_NAME"),"") %></td>
			<td><%=uF.showData(hmReimbursement.get("PAYCYCLE"),"") %></td>
			<td><%=uF.showData(hmReimbursement.get("APPLIED_DATE"),"") %></td>
			<%-- <% if(uF.showData(hmReimbursement.get("REIMB_FROM_DATE"),"").length()>0) { %>
				<td><%=uF.showData(hmReimbursement.get("REIMB_FROM_DATE"),"") %></td>
			<% } %> --%>
			<td><%=uF.showData(hmReimbursement.get("REIMBURSEMENT_TYPE"),"") %></td>
			
		 	<td><%=uF.showData(hmReimbursement.get("CLIENT"),"") %></td>
		 	<td><%=uF.showData(hmReimbursement.get("IS_BILLABLE"),"") %></td>
		 	<td><%=uF.showData(hmReimbursement.get("PROJECT"),"") %></td>
		 	<td><%=uF.showData(hmReimbursement.get("REIMBURSEMENT_INFO"),"") %></td>
		 	<td><%=uF.showData(hmReimbursement.get("NO_OF_PERSON"),"0") %></td>
		 	<%if(hmReimbursement.get("REIMBURSEMENT_INFO") != null && hmReimbursement.get("REIMBURSEMENT_INFO").trim().equalsIgnoreCase("Conveyance Bill")) { %>
		 		<td><%=uF.showData(hmReimbursement.get("TRAVEL_MODE"),"") %></td>
		 		<td><%=uF.showData(hmReimbursement.get("TRAVEL_FROM"),"") %></td>
		 		<td><%=uF.showData(hmReimbursement.get("TRAVEL_TO"),"") %></td>
		 		<td><%=uF.showData(hmReimbursement.get("NO_OF_DAYS"),"") %></td>
		 		<td>
			 		<%=uF.showData(hmReimbursement.get("TRAVEL_DISTANCE"),"0") %>&nbsp;&nbsp;
			 		Rate/KM:&nbsp;<%=uF.showData(hmReimbursement.get("TRAVEL_RATE"),"0") %>
			 	</td>
		 	<% } else { %>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
			<% } %>
			<td><%=uF.showData(hmReimbursement.get("REIMBURSEMENT_CURRENCY"),"-") %></td>
			<td><%=uF.showData(hmReimbursement.get("REIMBURSEMENT_AMOUNT"),"0") %></td>
			<td><%=uF.showData(hmReimbursement.get("REIMBURSEMENT_PAYMENT_MODE"),"-") %></td>
			<td><%=uF.showData(hmReimbursement.get("VENDOR"),"") %></td>
			<td><%=uF.showData(hmReimbursement.get("RECEIPT_NO"),"") %></td>
			<td><%=uF.showData(hmReimbursement.get("REIMBURSEMENT_PURPOSE"),"") %></td>
			<td><%=uF.showData(hmReimbursement.get("ATTACH_DOCUMENT"),"") %></td>
		</tr>
		<% } %>
	</table>
	<% } %>
	
</div>