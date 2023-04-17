<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Iterator"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="java.util.List"%>
<%@taglib uri="/struts-tags" prefix="s"%>

	<%
		List<List<String>> alTaskActivity = (List<List<String>>) request.getAttribute("alTaskActivity");
		String strUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
		UtilityFunctions uF = new UtilityFunctions();
	
		int proId = (Integer)request.getAttribute("proId");
		String proType = (String)request.getAttribute("proType");
		String taskId = (String)request.getAttribute("taskId");
		String fromPage = (String)request.getAttribute("fromPage");
		String percent = (String)request.getAttribute("percent");
	%>

		<div style="float: left; width: 100%;">
		
			<ul style="padding-left: 0px;">
			<!-- <li><h3>Task Activity History</h3></li> -->
			<%
				if (alTaskActivity != null) {
					//System.out.println("strTaskId --> "+strTaskId+" alActivities --->> " + alActivities);
					for (int i = 0; i<alTaskActivity.size(); i++) {
						List<String> innerList = alTaskActivity.get(i);
			%>
				<li><%=innerList.get(1)%> <%=innerList.get(2)%> to <%=uF.showData((String) innerList.get(3), " working ")%>, 
					<strong><%=innerList.get(4)%></strong> actual hrs, <%=innerList.get(5)%>, with <strong><%=innerList.get(6)%></strong> billable hrs, was <%=innerList.get(7)%>
					<%
						if(uF.parseToDouble(percent) < 100) {
							if (innerList.get(3) == null) {
					%>
					<% if(proType == null || proType.equals("null") || proType.equals("") || proType.equals("L")) { %>	
						<a onclick="endTask('<%=taskId%>', '<%=proId%>', '<%=innerList.get(0) %>')" href="javascript:void(0);">End this task</a>
					<% } %>
					<% } %>
				</li>

				<% } } } %>
				
				<% if (alTaskActivity == null || alTaskActivity.isEmpty() || alTaskActivity.size() == 0) { %>
					<li><div class="nodata msg"><span>No activity history.</span></div></li>
				<% } %>
			</ul>
			
		</div>