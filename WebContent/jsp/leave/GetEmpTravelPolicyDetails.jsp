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
	String leavetype = (String) request.getAttribute("leavetype");
	String checkLeave = (String) request.getAttribute("checkLeave");
	int i = 0;
	CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);   
	UtilityFunctions uF = new UtilityFunctions();
	Boolean isApproval =(Boolean)request.getAttribute("isApproval");
	
	String checkPayroll = (String) request.getAttribute("checkPayroll");
	String paid_from = (String) request.getAttribute("paid_from");
	String paid_to = (String) request.getAttribute("paid_to");
	String travelType = (String) request.getAttribute("travelType");// Start Dattatray
	
	if (uF.parseToBoolean(checkLeave)) {
%>
		<tr>
     	<td colspan="2">
    		You have already applied for these dates.
     	</td>
     </tr>
<%
	}else if (uF.parseToBoolean(checkPayroll)) {
%>
		<tr>
	     	<td colspan="2">
	    		Payroll has been processed for these dates.<br/> You can not applied for travel from <%=paid_from %> to <%=paid_to %>
	     	</td>
	     </tr>
<%
	} else if (uF.parseToBoolean(CF.getIsWorkFlow())) {
		 if (hmMemberOption != null && !hmMemberOption.isEmpty()) {%>
				<%
					Iterator<String> it = hmMemberOption.keySet().iterator();
					while (it.hasNext()) {
						i++;
						String memPosition = it.next();
						String optiontr = hmMemberOption.get(memPosition);
				%>
					<%=optiontr%>
				<%}%>
			<tr>
		     	<td></td>
		     	<td>
		     		<input type="hidden" name="policy_id" id="policy_id" value="<%=policy_id%>" />
				<!-- Start Dattatray -->
		     		<%
		     			if(travelType !=null && travelType.equalsIgnoreCase("OD")){
		     		%>
		    			<input id="submitButton" class="btn btn-primary" type="submit" name="submit" value="Apply On Duty"/>
		    		<% } else { %>
		    			<input id="submitButton" class="btn btn-primary" type="submit" name="submit" value="Apply Travel"/>
		    		<% } %>
				<!-- End Dattatray -->
					<!-- <input id="submitButton1" class="btn btn-primary" type="submit" name="addAnother" value="Submit & Add Another"/> -->
		     	</td>
		     </tr>
		<%} else {%>
		<tr>
	     	<td colspan="2">
	    		Your work flow is not defined. Please, speak to your HR for your work flow.
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
    		<input id="submitButton" class="btn btn-primary" type="submit" name="submit" value="Apply Travel 2">
     	</td>
     </tr>
<%
	}
%>