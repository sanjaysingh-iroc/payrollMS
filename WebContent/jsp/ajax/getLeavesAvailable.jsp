<%@ taglib prefix="s" uri="/struts-tags"%>

<%String leavesValidReqOpt = (String) request.getAttribute("leavesValidReqOpt");%>

<% if(leavesValidReqOpt != null && !leavesValidReqOpt.equals("")) { %>
	<s:select theme="simple" name="probationLeaves" id="probationLeaves" multiple="true" size="3"  cssClass="validateRequired"  
	listKey="leaveTypeId" listValue="leavetypeName" list="leaveTypeList" key=""  onchange="getEmpLeaveBalance();"/>
<% } else { %>
	<s:select theme="simple" name="probationLeaves" id="probationLeaves" multiple="true" size="3" 
	listKey="leaveTypeId" listValue="leavetypeName" list="leaveTypeList" key=""  onchange="getEmpLeaveBalance();"/>
<% } %>