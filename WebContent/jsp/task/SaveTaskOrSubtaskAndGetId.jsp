<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<% 
	String type = (String)request.getAttribute("type");
	String count = (String)request.getAttribute("count");
	String taskSubTaskId = (String)request.getAttribute("taskSubTaskId");
	String taskTRId = (String)request.getAttribute("taskTRId");
	String proId = (String)request.getAttribute("proId");
	String strTaskId = (String)request.getAttribute("strTaskId");
	String freqVal = (String)request.getAttribute("freqVal");
	//System.out.println("type =====>> " + type);
	//System.out.println("taskSubTaskId =====>> " + taskSubTaskId);
%>

<% if(type != null && type.equals("Task")) { %>
	<input type="hidden" name="taskID" id="taskID<%=count %>" value="<%=taskSubTaskId %>" />
	<input type="hidden" name="<%=taskSubTaskId %>" id="<%=taskSubTaskId %>" value="<%=count %>" />
	
<% } else if(type != null && type.equals("VA_Task")) { %>
	<input type="hidden" name="taskID<%=proId %>" id="taskID<%=proId %>_<%=count %>" value="<%=taskSubTaskId %>" />
	<input type="hidden" name="<%=proId %>_<%=taskSubTaskId %>" id="<%=proId %>_<%=taskSubTaskId %>" value="<%=count %>" />

<% } else if(type != null && type.equals("MP_Task")) { %>
	<input type="hidden" name="taskID" id="taskID<%=count %>" value="<%=taskSubTaskId %>" />
	<input type="hidden" name="<%=taskSubTaskId %>" id="<%=taskSubTaskId %>" value="<%=count %>" />
	
<% } else if(type != null && type.equals("SubTask")) { %>
	<input type="hidden" name="subTaskID<%=taskTRId %>" id="subTaskID<%=count %>" value="<%=taskSubTaskId %>" />
	<input type="hidden" name="<%=taskSubTaskId %>" id="<%=taskSubTaskId %>" value="<%=count %>" />
		
<% } else if(type != null && type.equals("VA_SubTask")) { %>
	<input type="hidden" name="subTaskID<%=proId %>_<%=taskTRId %>" id="subTaskID<%=proId %>_<%=count %>" value="<%=taskSubTaskId %>" />
	<input type="hidden" name="<%=proId %>_<%=taskSubTaskId %>" id="<%=proId %>_<%=taskSubTaskId %>" value="<%=count %>" />

<% } else if(type != null && type.equals("MP_SubTask")) { %>
	<input type="hidden" name="subTaskID" id="subTaskID<%=count %>" value="<%=taskSubTaskId %>" />
	<input type="hidden" name="<%=taskSubTaskId %>" id="<%=taskSubTaskId %>" value="<%=count %>" />
	
<% } else if(type != null && type.equals("GetTasks")) { %>
	<select name="dependency" id="dependency<%=count %>" style="width: 135px !important;"> 
		<option value="">Select Dependency </option>
		<%=(String)request.getAttribute("sbTaskOptions") %>
	</select>
	
<% } else if(type != null && type.equals("VA_GetTasks")) { %>
	<select name="dependency<%=proId %>" id="dependency<%=proId %>_<%=count %>" style="width: 135px !important;"> 
		<option value="">Select Dependency </option>
		<%=(String)request.getAttribute("sbTaskOptions") %>
	</select>

<% } else if(type != null && type.equals("MP_GetTasks")) { %>
	<span name="spandependency" id="spandependency<%=count %>" style="display: none; padding: 2px 5px; cursor: pointer;" ondblclick="updateFields('spandependency<%=count %>', 'dependency<%=count %>');" onmouseover="addBgColor(this)" onmouseout="removeBgColor(this)" ></span>
	<%=(String)request.getAttribute("hiddenTaskIdName") %>
	<select name="dependency" id="dependency<%=count %>" style="width: 135px !important;" onchange="setDependencyTaskName('<%=count %>', 'T')" > <%-- onmouseout="setDependencyTaskName('<%=count %>', 'T')" --%> 
		<option value="">Select Dependency </option>
		<%=(String)request.getAttribute("sbTaskOptions") %>
	</select>::::<%=freqVal %>
		
<% } else if(type != null && type.equals("GetSubTasks")) { %>
	<select name="subDependency<%=taskTRId %>" id="subDependency<%=count %>" style="width: 135px !important;"> 
		<option value="">Select Dependency </option>
		<%=(String)request.getAttribute("sbSubTaskOptions") %>
	</select>
	
<% } else if(type != null && type.equals("VA_GetSubTasks")) { %><!-- font-size:10px; -->
	<select name="subDependency<%=proId %>_<%=taskTRId %>" id="subDependency<%=proId %>_<%=count %>" style="width: 135px !important; "> 
		<option value="">Select Dependency </option>
		<%=(String)request.getAttribute("sbSubTaskOptions") %>
	</select>

<% } else if(type != null && type.equals("MP_GetSubTasks")) { %>
	<select name="subDependency" id="subDependency<%=count %>" style="width: 135px !important; "> 
		<option value="">Select Dependency </option>
		<%=(String)request.getAttribute("sbSubTaskOptions") %>
	</select>::::<%=freqVal %>

<% } %>
