<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Map"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.List"%>
<%

UtilityFunctions uF = (UtilityFunctions)request.getAttribute("uF");
%>

<script type="text/javascript">
	function cancelLeave(leaveId,leaveTypeId,isLeaveCompensatory){
		
		if(confirm('Are you sure you want to cancel this leave?')){ 
			var reason = window.prompt("Please enter cancel reason.");
			if (reason != null) {
				var action = 'ModifyLeave.action?leaveId='+leaveId+'&modify=modify&leaveTypeId='+leaveTypeId+'&isCompensate='+isLeaveCompensatory;
				action += '&cancelReason='+reason;
				
				getContent('myDiv_'+leaveId, action);
			}
		}
	}
	
</script>

<table class="tb_style" style="width:100%">
	<tr>
		<th>Leave From Date</th>
		<th>Leave To Date</th>
		<th>Leave Type</th>
		<th>No of days</th>
		<th>Cancel</th>
	</tr>


<%
List<Map<String,String>> alReport = (List<Map<String,String>>) request.getAttribute("alReport");
if(alReport == null) alReport = new ArrayList<Map<String,String>>();
for(int i=0; i<alReport.size(); i++){
	Map<String, String> hmInner = (Map<String, String>)alReport.get(i); 
	%>
	
	<tr>
		<td valign="top"><%=uF.showData(hmInner.get("LEAVE_FROM"),"") %></td>
		<td valign="top"><%=uF.showData(hmInner.get("LEAVE_TO"),"")  %></td>
		<td valign="top"><%=uF.showData(hmInner.get("LEAVE_TYPE_NAME"),"")  %></td>
		<td align="center" valign="top"><%=uF.showData(hmInner.get("LEAVE_NO"),"") %></td>
		<%if(uF.parseToBoolean(hmInner.get("LEAVE_MODIFY"))){%>
			<td valign="top">Cancelled on <%=uF.showData(hmInner.get("LEAVE_MODIFY_DATE"),"") %> by <%=uF.showData(hmInner.get("LEAVE_MODIFY_BY"),"") %>. Reason:[<%=uF.showData(hmInner.get("LEAVE_MODIFY_REASON"),"") %>]</td>
		<%}else{ %>
		<td valign="top">
			<div id="myDiv_<%=hmInner.get("LEAVE_ID") %>">
				<input type="button" onclick="cancelLeave('<%=hmInner.get("LEAVE_ID") %>','<%=hmInner.get("LEAVE_TYPE_ID") %>','<%=hmInner.get("LEAVE_IS_COMPENSATORY") %>');" name="cancel" class="cancel_button" value="Cancel"/>
			</div>
		</td>
		<%}%>
		
	</tr>
	<%
	}
	%>
</table>