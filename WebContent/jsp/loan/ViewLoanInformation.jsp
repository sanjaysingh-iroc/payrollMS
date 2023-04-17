<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.*"%>
<%
UtilityFunctions uF = new UtilityFunctions();
Map hmLoanDetails = (Map)request.getAttribute("hmLoanDetails");
%>


<div class="title">
	<h4>Loan Details</h4>
</div>

<%if(hmLoanDetails!=null && hmLoanDetails.size()>0){ %>
<table class="table table-bordered" style="width:100%">
<tr>
	<th>Loan Type</th>
	<td><%=uF.showData((String)hmLoanDetails.get("LOAN_CODE"), "") %></td>
</tr>

<tr>
	<th>Description</th>
	<td><%=uF.showData((String)hmLoanDetails.get("DESCRIPTION"),"") %></td>
</tr>

<tr>
	<th>ROI</th>
	<td><%=uF.showData((String)hmLoanDetails.get("ROI"),"") %></td> 
</tr>

<tr>
	<th>Loan Amount</th>
	<td><%=uF.showData((String)hmLoanDetails.get("LOAN_AMOUNT"),"") %> (<%=uF.showData((String)hmLoanDetails.get("TIMES_SALARY"),"0") %> times Earning)
	<input type="hidden" id="loanAmountLimit" value="<%=uF.showData((String)hmLoanDetails.get("LOAN_AMOUNT"),"") %>"/></td>
</tr>

<tr>
	<th>Min. Service </th>
	<td><%=uF.showData((String)hmLoanDetails.get("MIN_SERVICE"),"") %> Year(s)</td>
</tr>
<tr>
	<th>Fine Amount</th>
	<td><%=uF.showData((String)hmLoanDetails.get("FINE_AMOUNT"),"") %></td>
</tr>

</table>
<%}else{ %>
<div class="msg nodata" style="width:96%"><span>Please select the loan type</span></div>
<%}%>
::::
<%=uF.showData((String)request.getAttribute("IS_PREVIOUS_LOAN"),"false") %>