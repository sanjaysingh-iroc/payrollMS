<%@page import="java.util.List"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.*"%>

<%
	UtilityFunctions uF = new UtilityFunctions();
	Map<String, String> hmUsertypeUsers = (Map<String, String>) request.getAttribute("hmUsertypeUsers");
	if(hmUsertypeUsers == null) hmUsertypeUsers = new HashMap<String, String>();
	
	Map<String, String> orientationMemberMp = (Map<String, String>) request.getAttribute("orientationMemberMp");
	if(orientationMemberMp == null) orientationMemberMp = new HashMap<String, String>();
	
	Map<String, String> hmEmpName = (Map<String, String>) request.getAttribute("hmEmpName");
	if(hmEmpName == null) hmEmpName = new HashMap<String, String>();

	Map<String, Map<String, String>> hmRevieweeUsertypeUsers = (Map<String, Map<String, String>>) request.getAttribute("hmRevieweeUsertypeUsers");
	if(hmRevieweeUsertypeUsers == null) hmRevieweeUsertypeUsers = new HashMap<String, Map<String, String>>();
	
	String revieweeId = (String) request.getAttribute("revieweeId");
	
	List<String> alUserTypes = (List<String>) request.getAttribute("alUserTypes");
	
%>

	<% if(uF.parseToInt(revieweeId) == 0) { %>
		<div>
			<table class="table">
				<tr>
					<th nowrap="nowrap">Reviewee Name</th>
					<% 
						for(int i=0; alUserTypes!=null && i<alUserTypes.size(); i++) {
							String strUsertypeId = alUserTypes.get(i);
							if(uF.parseToInt(strUsertypeId) == 3) {
								continue;
							}
					%>
						<th nowrap="nowrap" class="txtlabel" valign="top" style="padding-left: 15px !important;"><%=orientationMemberMp.get(strUsertypeId) %></th>
					<% } %>
				</tr>	
				<% 
					Iterator<String> it = hmRevieweeUsertypeUsers.keySet().iterator();
					while(it.hasNext()) {
						String strRevieweeId = it.next();
						Map<String, String> hmUsrtypeUsers = hmRevieweeUsertypeUsers.get(strRevieweeId);
				%>
					<tr>
						<td nowrap="nowrap" class="txtlabel" valign="top"><%=hmEmpName.get(strRevieweeId) %></td>
						<% 
							for(int i=0; alUserTypes!=null && i<alUserTypes.size(); i++) {
								String strUsertypeId = alUserTypes.get(i);
								if(uF.parseToInt(strUsertypeId) == 3) {
									continue;
								}
						%>
							<td nowrap="nowrap" style="padding-left: 15px !important;"><%=hmUsrtypeUsers.get(strUsertypeId) %></td>
						<% } %>
					</tr>
				<% } %>
			</table>
		</div>
	<% } else { %>
		<div>
			<table class="table">
				<tr>
					<% 
						for(int i=0; alUserTypes!=null && i<alUserTypes.size(); i++) {
							String strUsertypeId = alUserTypes.get(i);
							if(uF.parseToInt(strUsertypeId) == 3) {
								continue;
							}
					%>
						<th nowrap="nowrap" class="txtlabel" valign="top" style="padding-left: 15px !important;"><%=orientationMemberMp.get(strUsertypeId) %></th>
					<% } %>
				</tr>
				<tr>
					<% 
						for(int i=0; alUserTypes!=null && i<alUserTypes.size(); i++) {
							String strUsertypeId = alUserTypes.get(i);
							if(uF.parseToInt(strUsertypeId) == 3) {
								continue;
							}
					%>
						<td nowrap="nowrap" style="padding-left: 15px !important;"><%=hmUsertypeUsers.get(strUsertypeId) %></td>
					<% } %>
				</tr>
			</table>
		</div>
	<% } %>
