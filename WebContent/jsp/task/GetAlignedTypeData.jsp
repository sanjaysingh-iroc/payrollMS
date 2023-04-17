<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<% 
	UtilityFunctions uF = new UtilityFunctions();
	String alignedType = (String) request.getAttribute("alignedType");
	String postId = (String) request.getAttribute("postId");
	if(uF.parseToInt(postId) == 0) {
%>
	
	<% if(uF.parseToInt(alignedType) == IConstants.PROJECT) { %>
		<div style="float: left; font-size: 10px; color: gray; font-style: italic;"> Align with</div> <br/>
		<s:select name="strAlignWithIds" list="projectList" listKey="projectID"
			listValue="projectName" title="Select align data for your post" cssStyle="width: 80px; font-size: 10px;" /> <!-- headerKey="" headerValue="Select" -->
	<% } else if(uF.parseToInt(alignedType) == IConstants.TASK) { %>
		<div style="float: left; font-size: 10px; color: gray; font-style: italic;"> Align with</div> <br/>
		<s:select name="strAlignWithIds" list="taskList" listKey="taskId"
			listValue="taskName" title="Select align data for your post" cssStyle="width: 80px; font-size: 10px;" />
	<% } else if(uF.parseToInt(alignedType) == IConstants.DOCUMENT) { %>
		<div style="float: left; font-size: 10px; color: gray; font-style: italic;"> Align with</div> <br/>
		<s:select name="strAlignWithIds" list="documentList" listKey="documentId"
			listValue="documentName" title="Select align data for your post" cssStyle="width: 80px; font-size: 10px;" />
	<% } else if(uF.parseToInt(alignedType) == IConstants.PRO_TIMESHEET) { %>
		<div style="float: left; font-size: 10px; color: gray; font-style: italic;"> Align with</div> <br/>
		<s:select name="strAlignWithIds" list="projectFreqList" listKey="projectID"
			listValue="projectName" title="Select align data for your post" cssStyle="width: 80px; font-size: 10px;" />
	<% } else if(uF.parseToInt(alignedType) == IConstants.INVOICE) { %>
		<div style="float: left; font-size: 10px; color: gray; font-style: italic;"> Align with</div> <br/>
		<s:select name="strAlignWithIds" list="invoiceList" listKey="invoiceId"
			listValue="invoiceCode" title="Select align data for your post" cssStyle="width: 80px; font-size: 10px;" />		
	<% } %>
<% } else { %>
	
	<% if(uF.parseToInt(alignedType) == IConstants.PROJECT) { %>
		<div style="float: left; font-size: 10px; color: gray; font-style: italic;"> Align with</div> <br/>
		<select name="strAlignWithIds" id="strAlignWithIds_<%=postId %>" title="Select align data for your post" style="width: 80px; font-size: 10px;">
			<!-- <option value="">Select Project</option> -->
			<%=(String)request.getAttribute("sbProjectsOption") %>
		</select>
	<% } else if(uF.parseToInt(alignedType) == IConstants.TASK) { %>
		<div style="float: left; font-size: 10px; color: gray; font-style: italic;"> Align with</div> <br/>
		<select name="strAlignWithIds" id="strAlignWithIds_<%=postId %>" title="Select align data for your post" style="width: 80px; font-size: 10px;">
			<!-- <option value="">Select Task</option> -->
			<%=(String)request.getAttribute("sbTaskOption") %>
		</select>
	<% } else if(uF.parseToInt(alignedType) == IConstants.PRO_TIMESHEET) { %>
		<div style="float: left; font-size: 10px; color: gray; font-style: italic;"> Align with</div> <br/>
		<select name="strAlignWithIds" id="strAlignWithIds_<%=postId %>" title="Select align data for your post" style="width: 80px; font-size: 10px;">
			<!-- <option value="">Select Timesheet</option> -->
			<%=(String)request.getAttribute("sbProTimesheetOption") %>
		</select>
	<% } else if(uF.parseToInt(alignedType) == IConstants.DOCUMENT) { %>
		<div style="float: left; font-size: 10px; color: gray; font-style: italic;"> Align with</div> <br/>
		<select name="strAlignWithIds" id="strAlignWithIds_<%=postId %>" title="Select align data for your post" style="width: 80px; font-size: 10px;">
			<!-- <option value="">Select Document</option> -->
			<%=(String)request.getAttribute("sbDocumentsOption") %>
		</select>
	<% } else if(uF.parseToInt(alignedType) == IConstants.INVOICE) { %>
		<div style="float: left; font-size: 10px; color: gray; font-style: italic;"> Align with</div> <br/>
		<select name="strAlignWithIds" id="strAlignWithIds_<%=postId %>" title="Select align data for your post" style="width: 80px; font-size: 10px;">
			<!-- <option value="">Select Invoice</option> -->
			<%=(String)request.getAttribute("sbProInvoiceOption") %>
		</select>
	<% } %>
<% } %>
	