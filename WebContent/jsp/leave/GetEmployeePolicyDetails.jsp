<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

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
	
	String checkAttendance = (String) request.getAttribute("checkAttendance");
	String approve_from = (String) request.getAttribute("approve_from");
	String approve_to = (String) request.getAttribute("approve_to");
	
	String isWeekOffHoliday = (String) request.getAttribute("isWeekOffHoliday");
	String msgWeekHoliday = (String) request.getAttribute("msgWeekHoliday");
	
	String isApplyLeaveFreeze = (String) request.getAttribute("isApplyLeaveFreeze");
	String msgApplyLeaveFreeze = (String) request.getAttribute("msgApplyLeaveFreeze");
	
	String isTimePeriod =(String)request.getAttribute("isTimePeriod");
	String isTimePeriodApplicable =(String)request.getAttribute("isTimePeriodApplicable");
	List<Map<String, String>> alTimePeriod = (List<Map<String,String>>)request.getAttribute("alTimePeriod");
	if(alTimePeriod == null) alTimePeriod = new ArrayList<Map<String,String>>();
	
	String isCompensate = (String)request.getAttribute("isCompensate");
	String strLeaveLabel= "Leave";
	if(uF.parseToBoolean(isCompensate)){
		strLeaveLabel= "Extra Working";
	}
	
	String isApplyLeaveLimit =(String)request.getAttribute("isApplyLeaveLimit");
	String isApplyLeaveLimitStatus =(String)request.getAttribute("isApplyLeaveLimitStatus");
	String isLongLeaveLimitExceed =(String)request.getAttribute("isLongLeaveLimitExceed");
	
//===start parvez date: 26-09-2022===	
	String isDocumentRequired =(String)request.getAttribute("isDocumentRequired");
	String isBereavementLeave =(String)request.getAttribute("isBereavementLeave");
	String isDocumentMandatory =(String)request.getAttribute("isDocumentMandatory");
//===end parvez date: 26-09-2022===
	//System.out.println("isApplyLeaveLimit --->> " + isApplyLeaveLimit + " -- isApplyLeaveLimitStatus --->> " + isApplyLeaveLimitStatus);
	
if (uF.parseToInt(leavetype)>0) {
	%>	
<!-- ===start parvez date: 27-09-2022=== -->	
	<% if(uF.parseToBoolean(isBereavementLeave)){ %>
		<tr>
     		<td class="txtlabel alignRight" >Select Relation:<sup>*</sup> </td>
     		<td>
     			<%-- <s:select theme="simple" name="appraisalSystem" headerKey="" headerValue="Select Relation" 
     			    list="#{'1':'Parents','2':'Spouse','3':'Spouse Parents','4':'Siblings','5':'Children','6':'Grandparents','7':'Grandchildren','8':'Other'}"
                    cssClass="validateRequired"/> --%>
                <s:select theme="simple" name="empRelation" headerKey="" headerValue="Select Relation" 
     			    list="#{'Parents':'Parents','Spouse':'Spouse','Spouse Parents':'Spouse Parents','Siblings':'Siblings','Children':'Children','Grandparents':'Grandparents','Grandchildren':'Grandchildren','Other':'Other'}"
                    cssClass="validateRequired"/>
     		</td>
     	</tr>
	<% } %>
	
	<% if(uF.parseToBoolean(isDocumentRequired)){ %>
		<% if(uF.parseToBoolean(isDocumentMandatory)){ %>
			<tr>
	     		<td class="txtlabel alignRight" >Select Document:<sup>*</sup> </td>
	     		<!-- <td><input name="docFileUpload" size="20" value="" id="docFileUpload" type="file" accept=".jpeg,.jpg,.png,.tif,.svg,.svgz,.doc,.docs,.docx,.pdf"></td> -->
	     		<td>
	     			<input type="file" name="requiredDoc" size="20" value="" id="requiredDoc" accept=".jpeg,.jpg,.png,.tif,.svg,.svgz,.doc,.docs,.docx,.pdf" required="true" />
	     		</td>
	     	</tr>
		<% } else{ %>
			<tr>
	     		<td class="txtlabel alignRight" >Select Document: </td>
	     		<!-- <td><input name="docFileUpload" size="20" value="" id="docFileUpload" type="file" accept=".jpeg,.jpg,.png,.tif,.svg,.svgz,.doc,.docs,.docx,.pdf"></td> -->
	     		<td>
	     			<input type="file" name="requiredDoc" size="20" value="" id="requiredDoc" accept=".jpeg,.jpg,.png,.tif,.svg,.svgz,.doc,.docs,.docx,.pdf" />
	     		</td>
	     	</tr>
		<% } %>
	<% } %>
	
	<% 
	if(uF.parseToBoolean(isTimePeriod) && !uF.parseToBoolean(isTimePeriodApplicable)){ 
		
	
	
		int nAlTimePeriod = alTimePeriod.size();
		if(nAlTimePeriod > 0){
%>
<!-- ===end parvez date: 27-09-2022=== -->
			<div style="margin-left: 144px;">
					<p>This leave is only applicable for below dates.</p>
<%			
			for(int j = 0; j < nAlTimePeriod; j++){
				Map<String, String> hmTimePeriod = alTimePeriod.get(j);
%>
					<p><%=(j+1) %>. <%=hmTimePeriod.get("TIME_FROM") %> to <%=hmTimePeriod.get("TIME_TO") %>
<%			}
%>
			</div>
<%		} else {
%>
			<div style="margin-left: 144px;">This leave is not applicable for leave from <%=approve_from %> to <%=approve_to %></div>
<%	
		}
	} else if (uF.parseToBoolean(isApplyLeaveLimit) && uF.parseToBoolean(isApplyLeaveLimitStatus)) {
%>
		<tr>
     		<td colspan="2">You can not apply more than balance.</td>
     	</tr>
<%
	} else if(uF.parseToBoolean(isLongLeaveLimitExceed))	{ %>
	 
	  <tr>
	    <td colspan="2">You can not apply more than long leave limit.</td>
	</tr>
<% }else if (uF.parseToBoolean(checkLeave)) { %>
		<tr>
     		<td colspan="2">You have already applied for these dates.</td>
     	</tr>
<%
} else if (uF.parseToBoolean(checkPayroll)) {
%>
		<tr>
     		<td colspan="2">Payroll has been processed for these dates.<br/> You can not applied for leave from <%=paid_from %> to <%=paid_to %></td>
     	</tr>
<%
	} else if (uF.parseToBoolean(checkAttendance)) {
%>
		<tr>
     		<td colspan="2">Clock entries has been approved for these dates.<br/> You can not applied for leave from <%=approve_from %> to <%=approve_to %></td>
     	</tr>
<%
	} else if (uF.parseToBoolean(isWeekOffHoliday)) {
%>
		<tr>
     		<td colspan="2"><%=msgWeekHoliday %></td>
     	</tr>
<%
	} else if (uF.parseToBoolean(isApplyLeaveFreeze)) {
%>
		<tr>
     		<td colspan="2"><%=msgApplyLeaveFreeze %></td>
     	</tr>
<%
	} else if (uF.parseToBoolean(CF.getIsWorkFlow())) {
		if (!isApproval){
%>
		<tr>
     		<td>&nbsp;</td>
     		<td>
				<input type="hidden" name="policy_id" id="policy_id" value="<%=policy_id%>"/>
				<!-- <input id="formID_0" class="input_button" type="button" onclick="getLeaveValidation()" value="Apply Leave"> -->
				<input id="submitButton" class="btn btn-primary" type="submit" value="Apply Leave"/> <!-- onclick="getLeaveValidation()"  -->
			</td>
		</tr>
		
		<%} else if (hmMemberOption != null && !hmMemberOption.isEmpty()) { %>
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
					<!-- <input id="submitButton" class="btn btn-primary" type="submit" value="Apply Leave"/> --> <!-- onclick="getLeaveValidation()" -->
				<!-- Started by Ajinkya Date on 13-09-2022 --> 
					<% if(uF.parseToBoolean(isCompensate)){%>
						<input id="submitButton" class="btn btn-primary" type="submit" value="Apply Extra Working"/> <!-- onclick="getLeaveValidation()" -->
					<%}else {%> 
						<input id="submitButton" class="btn btn-primary" type="submit" value="Apply Leave"/> <!-- onclick="getLeaveValidation()" -->
					<%} %>
				<!-- Ended by Ajinkya Date on 13-09-2022 -->
				</td>
			</tr>
		<%} else {%>
			<tr>
     			<td colspan="2">Your work flow is not defined.Please, speak to your HR for your work flow.</td>
     		</tr>
<%
		}
	} else {
%>
	<tr>
     	<td></td>
     	<td>
			<input type="hidden" name="policy_id" id="policy_id" value="<%=policy_id%>" />
			<input id="submitButton" class="btn btn-primary" type="submit" value="Apply <%=strLeaveLabel %>"> <!-- onclick="getLeaveValidation()" -->
		</td>
	</tr>
<%
	}
} else {
%>
	<tr>
     	<td></td>
     	<td><input class="btn btn-default" id="submitButton" type="button" value="Apply <%=strLeaveLabel %>"/></td>
    </tr>
<%}%>