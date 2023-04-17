<%@page import="com.konnect.jpms.util.IConstants"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@page import="java.util.*"%>

<script type="text/javascript" charset="utf-8">

$(document).ready(function() {
	$( "#startDate" ).datepicker({format: 'dd/mm/yyyy'});
	$( "#DeadlineDate" ).datepicker({format: 'dd/mm/yyyy'});
});
$(function(){
    $("body").on("click","#submitButton",function(){
    	$('.validateRequired').filter(':hidden').prop('required',false);
		$('.validateRequired').filter(':visible').prop('required',true);
    });
});
</script>

<html>
<head>
</head>
<body>
<% List<List<String>> alOuter =(List<List<String>>)request.getAttribute("alOuter"); 
   List<String> innerList;
   for(int i=0;i<alOuter.size();i++){
	innerList = new ArrayList<String>(alOuter.get(i));
	String taskId = innerList.get(0);
	String resourceId = innerList.get(1);
	String pro_id = innerList.get(5);
	String parentId = innerList.get(6);
	String activityName = innerList.get(7);
	
	if(innerList.get(3)!=null && !innerList.get(3).equals("") && innerList.get(4)!=null && !innerList.get(4).equals("")){
%>		
<s:form name="RescheduleReassignFrm" id="RescheduleReassignFrm" action="ConfirmRescheduleReassign" method="post" theme="simple">
<input type="hidden" name="pro_id" value="<%=pro_id %>"/>
<input type="hidden" name="parentId" value="<%=parentId %>"/>
<input type="hidden" name="resourceId" value="<%=resourceId %>"/>
<input type="hidden" name="taskId" value="<%=taskId %>"/>
<input type="hidden" name="activityName" value="<%=activityName %>"/>

<table class="table">
<tr>
<td>Start Date*:</td> 
<td><s:textfield name="startDate" id="startDate" cssClass="validateRequired" cssStyle="width:90px !important;" required="true"></s:textfield></td>
</tr>
<tr>
<td>Deadline*:</td> 
<td><s:textfield name="DeadlineDate" id="DeadlineDate" cssClass="validateRequired" cssStyle="width:90px !important;" required="true"></s:textfield></td>
</tr>
<tr>
<td>Comment:</td>  
<td><s:textarea name="comment" id="comment" rows="2" cols="50"></s:textarea></td>
</tr>
<tr>
<td><s:submit name="submitButton" id="submitButton" value="Submit" /></td>
</tr>
</table>
</s:form>		
<%}
else if((innerList.get(3)==null || innerList.get(3).equals("")) && (innerList.get(4)==null || innerList.get(4).equals(""))){
%>
<s:form name="RescheduleReassignFrm" id="RescheduleReassignFrm" action="ConfirmRescheduleReassign" method="post" theme="simple">
<input type="hidden" name="pro_id" value="<%=pro_id %>"/>
<input type="hidden" name="parentId" value="<%=parentId %>"/>
<input type="hidden" name="resourceId" value="<%=resourceId %>"/>
<input type="hidden" name="taskId" value="<%=taskId %>"/>
<input type="hidden" name="activityName" value="<%=activityName %>"/>
<table class="table">
<tr>
<td>Comment:</td><td><s:textarea name="comment" id="comment" rows="2" cols="50"></s:textarea></td>
</tr>
<tr>
<td><s:submit name="submitButton" id="submitButton" value="Submit" /></td>
</tr>
</table>
</s:form>
<%} }%>

</body>
</html>