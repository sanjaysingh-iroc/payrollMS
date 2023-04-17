<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<% 
	String type = (String)request.getAttribute("type");
	String count = (String)request.getAttribute("count");
	String taskTRId = (String)request.getAttribute("taskTRId");
	String proId = (String)request.getAttribute("proId");
	//System.out.println("type =====>> " + type);
	//System.out.println("taskSubTaskId =====>> " + taskSubTaskId);
%>

<% if(type != null && type.equals("Task")) { %>
	<select name="emp_id<%=count %>" id="emp_id<%=count %>" style="width:140px !important;" class="validateRequired" multiple size="3">
		<option value="">Select Employee</option>
		<%=(String)request.getAttribute("sbTaskSkillEmps") %>
	</select>				
<% } else if(type != null && type.equals("VA_Task")) { %>
	<select name="emp_id<%=proId %>_<%=count %>" id="emp_id<%=proId %>_<%=count %>" style="width:140px !important;" class="validateRequired" multiple size="3">
		<option value="">Select Employee</option>
		<%=(String)request.getAttribute("sbTaskSkillEmps") %>
	</select>	
<% } else if(type != null && type.equals("SubTask")) { %>
	<select name="sub_emp_id<%=taskTRId %>_<%=count %>" id="sub_emp_id<%=count %>" style="width:140px !important;" class="validateRequired" multiple size="3">
		<option value="">Select Employee</option>
		<%=(String)request.getAttribute("sbTaskSkillEmps") %>
	</select>	
<% } else if(type != null && type.equals("VA_SubTask")) { %>
	<select name="sub_emp_id<%=proId %>_<%=taskTRId %>_<%=count %>" id="sub_emp_id<%=proId %>_<%=count %>" style="width:140px !important;" class="validateRequired" multiple size="3">
		<option value="">Select Employee</option>
		<%=(String)request.getAttribute("sbTaskSkillEmps") %>
	</select>	
<% } else if(type != null && type.equals("EditTask")) { %>
	<select name="emp_id" id="emp_id" class="validateRequired" multiple size="3">
		<option value="">Select Employee</option>
		<%=(String)request.getAttribute("sbTaskSkillEmps") %>
	</select>	
<% } %>
