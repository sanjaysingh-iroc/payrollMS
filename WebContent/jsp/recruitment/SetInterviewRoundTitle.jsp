<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.*"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>


<%
	UtilityFunctions uF = new UtilityFunctions();
	String operation = (String) request.getAttribute("operation");
	String roundName = (String) request.getAttribute("roundName");
%>

<%if(operation != null && operation.equals("A")){ %>

<%=uF.showData(roundName, "") %>

<%} else { %>
<div>
	<form id="frmSetInterviewRoundTitle" name="frmSetInterviewRoundTitle">

		<table class="table table_no_border">
			<tr>
				<th width="30%" align="right">Title:</th>
				<td>
				<s:hidden name="interviewRoundId" id="interviewRoundId"></s:hidden>
				<s:hidden name="operation" id="operation"></s:hidden>
				<s:hidden name="recruitId" id="recruitId"></s:hidden>
				<input type="text" name="roundName" id="roundName" value="<%=uF.showData(roundName, "") %>" class="no-press-enter"/>
				<%-- <select name="questionSelect" id="questionSelect" style="width: 80%;"><option value="">Select Question</option><%=opt %></select> --%>
				</td>
			</tr>
			
		</table>
		<div align="center">
			<input type="button" value="Ok" class="btn btn-primary" name="ok" onclick="setInterviewRoundTitle();" />
		</div>
	</form>
</div>
<% } %>



