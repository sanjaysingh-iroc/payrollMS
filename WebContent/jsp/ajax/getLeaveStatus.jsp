<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%
	UtilityFunctions uF = new UtilityFunctions();
	String strStatus  = (String) request.getAttribute("status");
	String leaveStatus  = (String) request.getAttribute("leaveStatus");
%>

<%if(uF.parseToBoolean(leaveStatus)){ %>
	<table>
	    <%if(strStatus!=null) {%>
		    <tr>       
		        <td class="redColor" colspan="2"><%=strStatus %></td>
		    </tr>
	    <%} %>
	    
	    <tr>
	        <td class="blueColor" width="150px">Available leaves:</td>
	        <td class="blueColor"><%=uF.showData((String)request.getAttribute("dblAccruedLeaves"),"") %></td>
	    </tr>
	     
	    <tr>
	        <td class="greenColor">Approved leaves:</td>
	        <td class="greenColor"><%=uF.showData((String)request.getAttribute("dblApprovedLeaves"),"") %></td>
	    </tr>
	    
	    <tr>
	        <td class="orangeColor">Pending Leaves:</td>
	        <td class="orangeColor"><%=uF.showData((String)request.getAttribute("dblPendingLeaves"),"") %></td>
	    </tr>
	</table>
<%}%>