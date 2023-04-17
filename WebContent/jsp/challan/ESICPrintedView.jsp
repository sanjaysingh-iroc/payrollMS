<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@taglib uri="/struts-tags" prefix="s"%>

	<%
		UtilityFunctions uF = new UtilityFunctions();
		CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
		Map hmEmpName = (Map)request.getAttribute("hmEmpName");
		Map<String,Map<String,String>> hmMap = (Map<String,Map<String,String>>)request.getAttribute("hmMap");
		Map<String,Map<String,String>> hmMap1 = (Map<String,Map<String,String>>)request.getAttribute("hmMap1");
		List<String> empList=(List<String>)request.getAttribute("empList");
		String totalEEunpaidamount=(String)request.getAttribute("totalEEunpaidamount");
		String totalERunpaidamount=(String)request.getAttribute("totalERunpaidamount");
		String orgId = (String) request.getAttribute("f_org");
		double totalunpaidamount=uF.parseToDouble(totalEEunpaidamount)+uF.parseToDouble(totalERunpaidamount);
		String strCurrency = (String) request.getAttribute("strCurrency"); 
	%>
			<input type="hidden" name="orgid" value="<%=orgId%>"/>
			<s:hidden name="f_strWLocation"></s:hidden>
			<table border="0" class="table table-bordered">
				<tr>
					<td><strong>Challan printed but not paid for <%=uF.showData(strCurrency,"")%> <%=uF.showData(uF.formatIntoComma(totalunpaidamount)+"", "0") %></strong></td>
				</tr>
			</table>
			<table border="0" class="table table-bordered">
				<tr>
					<th>Name</th>
					<th>Employee Contribution</th>
					<th>Employer Contribution</th>
				</tr>
				<%if(empList!=null) {
				for(int i=0;i<empList.size();i++) {
					Map<String,String> hmInner =hmMap.get(empList.get(i));
					Map<String,String> hmInner1 =hmMap1.get(empList.get(i));
				%>
					<tr>
	 					<td class="txtlabel alignLeft"><%=uF.showData((String)hmEmpName.get(hmInner.get("EMP_ID")), "")%></td> 
	
						<td class="txtlabel alignRight"><%=hmInner1.get(""+IConstants.EMPLOYEE_ESI) %></td>
						<td class="txtlabel alignRight"><%=hmInner.get(""+IConstants.EMPLOYER_ESI) %></td>
					</tr>
				<%} 
				} %>
				<tr>
 					<td class="txtlabel alignLeft"><b>Total</b></td> 
					<td class="txtlabel alignRight"><b><%=uF.formatIntoComma(uF.parseToDouble(totalEEunpaidamount)) %></b></td>
					<td class="txtlabel alignRight"><b><%=uF.formatIntoComma(uF.parseToDouble(totalERunpaidamount)) %></b></td>
				</tr>
			</table>
			
			<% if (empList != null && empList.size() == 0) { %>
			<div class="msg nodata"><span>No more challan printed.</span></div>
			<% } %>

	