<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%
	Map<String, String> hmMemberOption = (Map<String, String>) request.getAttribute("hmMemberOption");
	String policy_id = (String) request.getAttribute("policy_id");
	String recruitmentID = (String) request.getAttribute("recruitmentID");
	
	CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);   
	UtilityFunctions uF = new UtilityFunctions();
	
	String strValue = "Send Job Requirement Request"; 
	if(uF.parseToInt(recruitmentID) > 0){
		strValue = "Update";
	}
	
	if (uF.parseToBoolean(CF.getIsWorkFlow())) {
		if (hmMemberOption != null && !hmMemberOption.isEmpty()) {%>
				<%
					Iterator<String> it = hmMemberOption.keySet().iterator();
					while (it.hasNext()) {
						String memPosition = it.next();
						String optiontr = hmMemberOption.get(memPosition);
				%>
					<%=optiontr%>
				<%}%>
			<tr>
				<td></td>
				<td>
					<input type="hidden" name="policy_id" id="policy_id" value="<%=policy_id%>" />
					<input class="btn btn-primary" name="strInsert" type="submit" value="<%=strValue %>">
				</td>
			</tr>
			
		<%} else {%>
			<tr>
				<td></td>
				<td>
					Your work flow is not defined.Please, speak to your HR for your work flow.
				</td>
			</tr>
<%
		}
	} else {
%>
	<tr>
		<td></td>
		<td>
			<input type="hidden" name="policy_id" id="policy_id" value="<%=policy_id%>" />
			<input class="btn btn-primary" name="strInsert" type="submit" value="<%=strValue %>">
		</td>
	</tr>
<%
	}
%>