<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%
List<List<String>> leaveTypeListWithBalance = (List<List<String>>) request.getAttribute("leaveTypeListWithBalance");
Map<String, String> hmEmpLeaveBalance = (Map<String, String>) request.getAttribute("hmEmpLeaveBalance");
List<String> alAccrueLeave = (List<String>)request.getAttribute("alAccrueLeave");
if(alAccrueLeave == null) alAccrueLeave = new ArrayList<String>();
//System.out.println("GELB.jsp/10--leaveTypeListWithBalance="+leaveTypeListWithBalance);
for(int i=0; leaveTypeListWithBalance != null && !leaveTypeListWithBalance.isEmpty() && i<leaveTypeListWithBalance.size(); i++) {
	List<String> innerList = leaveTypeListWithBalance.get(i);
//===start parvez date: 22-11-2021===	
	/* if(alAccrueLeave.contains(innerList.get(0))){
		continue;
	} */
//===end parvez date: 22-11-2021===
%>
	<div style="float: left; width: 100%;">
		<div style="float: left; width: 31%;"><%=innerList.get(1) %>:</div>
		<div style="float: left; width: 58%; text-align: left; margin-left: 10px;">
			<% if(hmEmpLeaveBalance != null && hmEmpLeaveBalance.get(innerList.get(0)) != null) { %>
					<%=hmEmpLeaveBalance.get(innerList.get(0)) %>
			<% } else { %>
				<input type="hidden" name="<%=innerList.get(0) %>" value="1" />
				<span style="float: left; margin-top: 7px;"> <input type="text" name="leaveBal<%=innerList.get(0) %>" id="leaveBal<%=innerList.get(0) %>" value="<%=innerList.get(2) %>" style="text-align: right;"/> </span>
				<span style="float: left; margin-left: 10px;"><i> If edited, it will replace the current balance.<br/>This edition is one time only. </i></span>
			<% } %>
		</div>
	</div> 
<% } %>