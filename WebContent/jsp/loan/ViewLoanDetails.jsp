<%@page import="java.util.*"%>
<% 
List alLoanDetails = (List)request.getAttribute("alLoanDetails");
Map hmLoanDetails = (Map)request.getAttribute("hmLoanDetails");
if(hmLoanDetails==null)hmLoanDetails=new HashMap();

%>

<%
if(hmLoanDetails.get("STATUS")!=null){
	out.println(hmLoanDetails.get("STATUS"));
}
%>



<div class="title">
	<h4>Loan Details</h4>
</div>


<table class="table table-bordered" style="width:100%">
<tr>
	<th>Loan Type</th>
	<td><%=hmLoanDetails.get("LOAN_CODE") %></td> 
	<th>Principal Amount</th>
	<td><%=hmLoanDetails.get("LOAN_AMOUNT") %></td>
</tr> 

<tr> 
	<th>Applied On</th>
	<td><%=hmLoanDetails.get("APPLIED_ON") %></td>
	<th>TDS</th>
	<td><%=hmLoanDetails.get("TDS") %></td>
</tr>

<tr>
	<th>Approved On</th>
	<td><%=hmLoanDetails.get("APPROVED_ON") %></td>
	<th>Total Paid Amount</th>
	<td><%=hmLoanDetails.get("PAID_AMOUNT") %></td>
</tr>

<tr>
	<td colspan="2">&nbsp;</td>
	<th>Balance Amount</th>
	<td><%=hmLoanDetails.get("BALANCE_AMOUNT") %></td>
</tr>


<tr>
	<td colspan="2">&nbsp;</td>
	<th>ROI</th>
	<td><%=hmLoanDetails.get("ROI") %>%</td>
</tr>

<tr>
	<td colspan="2">&nbsp;</td>
	<th>Duration</th>
	<td><%=hmLoanDetails.get("DURATION") %> months</td>
</tr>


</table>


<div class="title">
	<h4>Payment Details</h4>
</div>


<table class="table table-bordered" style="width:100%">


<tr>
	<th>Date</th>
	<th>Amount Paid</th>
	<th>Source</th>
</tr>


<% 
for(int i=0; alLoanDetails!=null && i<alLoanDetails.size(); i++){
List alInner = (List)alLoanDetails.get(i);

%>

<tr>

	<td align="center"><%=alInner.get(0) %></td>
	<td class="padRight20 alignRight"><%=alInner.get(1) %></td>
	<td><%=alInner.get(2) %></td>
</tr>


<%}%>


<%if(alLoanDetails!=null && alLoanDetails.size()==0){ %>
<tr>
	<td class="" colspan="3"><div class="msg nodata" style="width:95%"><span>No payments made yet</span></div></td>
</tr>
<%} %>
</table>