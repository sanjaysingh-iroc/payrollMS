<%@page import="java.util.Set"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.*"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>


<%-- <%
Map<String, List<String>> hmOrientTypeAndUsers = (Map<String, List<String>>)request.getAttribute("hmOrientTypeAndUsers");
Map<String, String> orientMemberMp = (Map<String, String>)request.getAttribute("orientMemberMp");
	//System.out.println("hmOrientTypeAndUsers  " + hmOrientTypeAndUsers);
%> --%>
	<div style="width: 100%;">
	<%-- <%=request.getAttribute("memberStep") %> --%>
	<br/>
	<%=request.getAttribute("allMemberNames").toString() %>
		<%-- <table class="tb_style" style="width: 100%">
		<%
		Set set1 = hmOrientTypeAndUsers.keySet();
		Iterator it = set1.iterator();
		while(it.hasNext()){
			String key=(String)it.next();
			List<String> listUsersName = hmOrientTypeAndUsers.get(key);
		%>
			<tr>
				<th width="20%" align="right"><%=orientMemberMp.get(key) %></th>
				<td>
				<%
				for(int i =0; listUsersName != null && i<listUsersName.size();i++){
					if(i == 0){
					%>
					<%=listUsersName.get(i) %>
					<%}else{ %>
					,&nbsp;<%=listUsersName.get(i) %>
					<%
					}
				}
				%>
				</td>
			</tr>
			<%} %>
			
		</table> --%>
	</div>




