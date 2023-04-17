<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@taglib uri="/struts-tags" prefix="s"%>

	<%
		UtilityFunctions uF = new UtilityFunctions();
		CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
		Map hmEmpName = (Map)request.getAttribute("hmEmpName");
		Map<String,Map<String,String>> hmMap = (Map<String,Map<String,String>>)request.getAttribute("hmMap");
		List<String> empList=(List<String>)request.getAttribute("empList");
		String totalPaidamount=(String)request.getAttribute("totalPaidamount");
	%> 

		<table class="table table_no_border form-table">
			<tr>
				<td><strong>Rs.<%=uF.showData(uF.formatIntoComma(uF.parseToDouble(totalPaidamount)), "0")%>, challan amount paid.</strong></td>
			</tr>
		</table>
		
		<table class="table table_no_border form-table">
			<tr>
				<th>Name</th>
				<th>Amount</th>
			</tr>
			<%if(empList!=null) {
			for(int i=0;i<empList.size();i++) {
				Map<String,String> hmInner =hmMap.get(empList.get(i));
			%>
			<tr>
				<td class="txtlabel alignLeft"><%=uF.showData((String)hmEmpName.get(hmInner.get("EMP_ID")), "")%></td> 
				<td class="txtlabel alignRight"><%=hmInner.get("AMOUNT") %></td>
			</tr>
			<%} } %>
			<tr>
				<td class="txtlabel alignLeft"><b>Total</b></td> 
				<td class="txtlabel alignRight"><b><%=uF.showData(uF.formatIntoComma(uF.parseToDouble(totalPaidamount)), "0")%></b></td>
			</tr>
		</table>
		<% if (empList != null && empList.size() == 0) { %>
			<div class="msg nodata"><span>Amount not paid till date.</span></div>
		<% } %>
	
