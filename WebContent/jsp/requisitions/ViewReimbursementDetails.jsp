<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<%  
	UtilityFunctions uF = new UtilityFunctions();
	Map<String, String> hmReimbursement = (Map<String, String>)request.getAttribute("hmReimbursement") ;
	if (hmReimbursement == null) hmReimbursement = new HashMap<String, String>();
%>
	
<div>
	<table border="0" class="table" style="width: 505px;">
		<tr>
			<th class="txtlabel alignRight">Employee:</th>
			<td><%=uF.showData(hmReimbursement.get("EMP_NAME"),"") %></td>
		</tr>
		<tr>
			<th class="txtlabel alignRight">PayCycle:</th>
			<td><%=uF.showData(hmReimbursement.get("PAYCYCLE"),"") %></td>
		</tr>
		<tr>
			<th class="txtlabel alignRight">Applied Date:</th>
			<td><%=uF.showData(hmReimbursement.get("APPLIED_DATE"),"") %></td>
		</tr>	
		<% if(uF.showData(hmReimbursement.get("REIMB_FROM_DATE"),"").length()>0){ %>
		<tr>
			<th class="txtlabel alignRight">Reimbursement Date:</th>
			<%-- <td><%=uF.showData(hmReimbursement.get("REIMB_FROM_DATE"),"") %> to <%=uF.showData(hmReimbursement.get("REIMB_TO_DATE"),"") %> </td> --%>
			<td><%=uF.showData(hmReimbursement.get("REIMB_FROM_DATE"),"") %></td>
		</tr> 
		<%}%>	 
		<tr>
			<th class="txtlabel alignRight">Reimbursement Type:</th>
		 	<td><%=uF.showData(hmReimbursement.get("REIMBURSEMENT_TYPE"),"") %></td>
		</tr>
		<%if(hmReimbursement.get("REIMBURSEMENT_APPLY_TYPE") != null && hmReimbursement.get("REIMBURSEMENT_APPLY_TYPE").trim().equalsIgnoreCase("T")){ %>
			<tr>
				<th class="txtlabel alignRight">Travel Plan</th>
			 	<td><%=uF.showData(hmReimbursement.get("REIMBURSEMENT_TRAVEL_PLAN"),"") %></td>
			</tr>
			<tr>
				<th class="txtlabel alignRight">Number of persons:</th>
			 	<td><%=uF.showData(hmReimbursement.get("NO_OF_PERSON"),"0") %></td>
			</tr>
			<tr>
				<th class="txtlabel alignRight">Transportation Type:</th>
			 	<td><%=uF.showData(hmReimbursement.get("TRANSPORT_TYPE"),"") %></td>
			</tr>
			<tr>
				<th class="txtlabel alignRight">Transportation Mode:</th>
			 	<td><%=uF.showData(hmReimbursement.get("TRANSPORT_MODE"),"") %></td>
			</tr>
			<tr>
				<th class="txtlabel alignRight">Transport Amount:</th>
			 	<td><%=uF.showData(hmReimbursement.get("TRANSPORT_AMOUNT"),"0") %></td>
			</tr>
			<tr>
				<th class="txtlabel alignRight">Lodging Type:</th>
			 	<td><%=uF.showData(hmReimbursement.get("LODGING_TYPE"),"") %></td>
			</tr>
			<tr>
				<th class="txtlabel alignRight">Lodging Amount:</th>
			 	<td><%=uF.showData(hmReimbursement.get("LODGING_AMOUNT"),"0") %></td>
			</tr>
			<tr>
				<th class="txtlabel alignRight">Local Conveyance Type:</th>
			 	<td><%=uF.showData(hmReimbursement.get("LOCAL_CONVEYANCE_TYPE"),"") %></td>
			</tr>
			<tr>
				<th class="txtlabel alignRight">Total KM:</th>
			 	<td>
			 		<%=uF.showData(hmReimbursement.get("LOCAL_CONVEYANCE_KM"),"0") %>&nbsp;&nbsp;
			 		Rate/KM:&nbsp;<%=uF.showData(hmReimbursement.get("LOCAL_CONVEYANCE_RATE"),"0") %>
			 	</td>
			</tr>
			<tr>
				<th class="txtlabel alignRight">Local Conveyance Amount:</th>
			 	<td><%=uF.showData(hmReimbursement.get("LOCAL_CONVEYANCE_AMOUNT"),"0") %></td>
			</tr>
			<tr>
				<th class="txtlabel alignRight">Food &amp; Beverage:</th>
			 	<td><%=uF.showData(hmReimbursement.get("FOOD_BEVERAGE_AMOUNT"),"0") %></td>
			</tr>
			<tr>
				<th class="txtlabel alignRight">Laundry:</th>
			 	<td><%=uF.showData(hmReimbursement.get("LAUNDRY_AMOUNT"),"0") %></td>
			</tr>
			<tr>
				<th class="txtlabel alignRight">Sundry:</th>
			 	<td><%=uF.showData(hmReimbursement.get("SUNDRY_AMOUNT"),"0") %></td>
			</tr>
			
		<%} else if(hmReimbursement.get("REIMBURSEMENT_APPLY_TYPE") != null && hmReimbursement.get("REIMBURSEMENT_APPLY_TYPE").trim().equalsIgnoreCase("L")){ %>
			<tr>
				<th class="txtlabel alignRight">Type:</th>
			 	<td><%=uF.showData(hmReimbursement.get("REIMBURSEMENT_INFO"),"") %></td>
			</tr>
			<tr>
				<th class="txtlabel alignRight">Number of persons:</th>
			 	<td><%=uF.showData(hmReimbursement.get("NO_OF_PERSON"),"0") %></td>
			</tr>
			<%if(hmReimbursement.get("REIMBURSEMENT_INFO") != null && hmReimbursement.get("REIMBURSEMENT_INFO").trim().equalsIgnoreCase("Conveyance Bill")){ %>
				<tr>
					<th class="txtlabel alignRight">Mode of Travel:</th>
				 	<td><%=uF.showData(hmReimbursement.get("TRAVEL_MODE"),"") %></td>
				</tr>
				<tr>
					<th class="txtlabel alignRight">Place From:</th>
				 	<td><%=uF.showData(hmReimbursement.get("TRAVEL_FROM"),"") %></td>
				</tr>
				<tr>
					<th class="txtlabel alignRight">Place To:</th>
				 	<td><%=uF.showData(hmReimbursement.get("TRAVEL_TO"),"") %></td>
				</tr>
				<tr>
					<th class="txtlabel alignRight">No of Days:</th>
				 	<td><%=uF.showData(hmReimbursement.get("NO_OF_DAYS"),"") %></td>
				</tr>
				<tr>
					<th class="txtlabel alignRight">Total KM:</th>
				 	<td>
				 		<%=uF.showData(hmReimbursement.get("TRAVEL_DISTANCE"),"0") %>&nbsp;&nbsp;
				 		Rate/KM:&nbsp;<%=uF.showData(hmReimbursement.get("TRAVEL_RATE"),"0") %>
				 	</td>
				</tr>
			<%} %>
		<%} else if(hmReimbursement.get("REIMBURSEMENT_APPLY_TYPE") != null && hmReimbursement.get("REIMBURSEMENT_APPLY_TYPE").trim().equalsIgnoreCase("M")){ %>
			<tr>
				<th class="txtlabel alignRight">Number of persons:</th>
			 	<td><%=uF.showData(hmReimbursement.get("NO_OF_PERSON"),"0") %></td>
			</tr>	
		<%} else if(hmReimbursement.get("REIMBURSEMENT_APPLY_TYPE") != null && hmReimbursement.get("REIMBURSEMENT_APPLY_TYPE").trim().equalsIgnoreCase("P")){ %>
			<tr>
				<th class="txtlabel alignRight">Client:</th>
			 	<td><%=uF.showData(hmReimbursement.get("CLIENT"),"") %></td>
			</tr>
			<tr>
				<th class="txtlabel alignRight">Chargeable to client:</th>
			 	<td><%=uF.showData(hmReimbursement.get("IS_BILLABLE"),"") %></td>
			</tr>
			<tr>
				<th class="txtlabel alignRight">Project:</th>
			 	<td><%=uF.showData(hmReimbursement.get("PROJECT"),"") %></td>
			</tr>
			<tr>
				<th class="txtlabel alignRight">Type:</th>
			 	<td><%=uF.showData(hmReimbursement.get("REIMBURSEMENT_INFO"),"") %></td>
			</tr>
			<tr>
				<th class="txtlabel alignRight">Number of persons:</th>
			 	<td><%=uF.showData(hmReimbursement.get("NO_OF_PERSON"),"0") %></td>
			</tr>
			<%if(hmReimbursement.get("REIMBURSEMENT_INFO") != null && hmReimbursement.get("REIMBURSEMENT_INFO").trim().equalsIgnoreCase("Conveyance Bill")){ %>
				<tr>
					<th class="txtlabel alignRight">Mode of Travel:</th>
				 	<td><%=uF.showData(hmReimbursement.get("TRAVEL_MODE"),"") %></td>
				</tr>
				<tr>
					<th class="txtlabel alignRight">Place From:</th>
				 	<td><%=uF.showData(hmReimbursement.get("TRAVEL_FROM"),"") %></td>
				</tr>
				<tr>
					<th class="txtlabel alignRight">Place To:</th>
				 	<td><%=uF.showData(hmReimbursement.get("TRAVEL_TO"),"") %></td>
				</tr>
				<tr>
					<th class="txtlabel alignRight">No of Days:</th>
				 	<td><%=uF.showData(hmReimbursement.get("NO_OF_DAYS"),"") %></td>
				</tr>
				<tr>
					<th class="txtlabel alignRight">Total KM:</th>
				 	<td>
				 		<%=uF.showData(hmReimbursement.get("TRAVEL_DISTANCE"),"0") %>&nbsp;&nbsp;
				 		Rate/KM:&nbsp;<%=uF.showData(hmReimbursement.get("TRAVEL_RATE"),"0") %>
				 	</td>
				</tr>
			<%} %>	
		<%} %>
		
		<tr>
			<th class="txtlabel alignRight">Currency:</th>
		 	<td><%=uF.showData(hmReimbursement.get("REIMBURSEMENT_CURRENCY"),"-") %></td>
		</tr>
		<tr>
			<th class="txtlabel alignRight">Total Amount:</th>
		 	<td><%=uF.showData(hmReimbursement.get("REIMBURSEMENT_AMOUNT"),"0") %></td>
		</tr>
		<tr>
			<th class="txtlabel alignRight">Payment Mode:</th>
		 	<td><%=uF.showData(hmReimbursement.get("REIMBURSEMENT_PAYMENT_MODE"),"-") %></td>
		</tr>
		<tr>
			<th class="txtlabel alignRight">Vendor:</th>
		 	<td><%=uF.showData(hmReimbursement.get("VENDOR"),"") %></td>
		</tr>
		<tr>
			<th class="txtlabel alignRight">Receipt No:</th>
		 	<td><%=uF.showData(hmReimbursement.get("RECEIPT_NO"),"") %></td>
		</tr>
		<tr>
			<th class="txtlabel alignRight">Purpose:</th>
		 	<td><%=uF.showData(hmReimbursement.get("REIMBURSEMENT_PURPOSE"),"") %></td>
		</tr>
		<tr>
			<th class="txtlabel alignRight">Attach Document:</th>
		 	<td><%=uF.showData(hmReimbursement.get("ATTACH_DOCUMENT"),"") %></td>
		</tr>
	</table>
</div>