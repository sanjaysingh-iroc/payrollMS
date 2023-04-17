<%@page import="java.util.Map"%>

<%
	String taskDescription = (String)request.getAttribute("taskDescription");
	String proId = (String)request.getAttribute("proId");
	String divId = (String)request.getAttribute("divId");
	String count = (String)request.getAttribute("count");
	String fromPage = (String)request.getAttribute("fromPage");
%>

<div style="float: left; vertical-align: top;">
	<span style="vertical-align: top; margin-right: 5px;">Description:</span>
	<textarea name="description" id="description" cols="55" rows="3" <%if(fromPage != null && fromPage.equals("V")) { %> readonly="readonly" <% } %>><%=taskDescription %></textarea> 
</div>

<%if(fromPage == null || !fromPage.equals("V")) { %>
<div style="float: left;">
	<input type="button" value="Save" class="btn btn-primary" style="margin-left:30px" onclick="addTaskDescription(document.getElementById('description').value, '<%=proId %>', '<%=divId %>', '<%=count %>', '<%=fromPage %>')" />
</div>
<% } %>

