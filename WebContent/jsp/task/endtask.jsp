<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>


<script type="text/javascript">

function setBillableValue(){
	if (document.getElementById("strBillableYesNo_0").checked == 1) {
          document.getElementById("strBillableYesNoT_0").value='1';
	} else {
		document.getElementById("strBillableYesNoT_0").value='0';
	}
}


function setValue() {
	if (document.getElementById("strTaskOnOffSite_0").checked == 1) {
          document.getElementById("strTaskOnOffSiteT_0").value='1';
	} else {
		document.getElementById("strTaskOnOffSiteT_0").value='0';
	}
}

</script>

<form action="TaskUpdateTime.action" name="formEndTask" id="formEndTask" method="post" >
	<s:hidden name="type" value="end" />
	<input type="hidden" name="id" id="tid" value="${id}" />
	<s:hidden name="fromPage" />
	<% 
		UtilityFunctions uF = new UtilityFunctions();
		int proid=(Integer)request.getAttribute("pro_id"); 
		String taskId = (String)request.getAttribute("taskId");
		String isBillable = (String)request.getAttribute("isBillable");
		String taskLocation = (String)request.getAttribute("taskLocation");
		String taskDescription = (String)request.getAttribute("taskDescription");
	%>
	<input type="hidden" name="pro_id" value="<%=proid %>"/>
	<input type="hidden" name="taskId" value="<%=taskId %>"/>
	<table class="table">
		<%-- <tr>
			<td> <s:textfield name="per" label="Enter completion status of task (%)"/> </td>
		</tr> --%>
		
		<tr>
			<td>Billable:</td>
			<td><input type="hidden" style="width: 30px;"  id="strBillableYesNoT_0" name="strBillableYesNoT" value="<%=uF.parseToBoolean(isBillable) == true ? "1" : "0" %>">
				<input type="checkbox" name="strBillableYesNo" id="strBillableYesNo_0" onchange="setBillableValue('0','0')" style="width: 30px;" <%=uF.parseToBoolean(isBillable) == true ? "checked" : "" %>>
			</td>
		</tr>
		
		<!-- <tr>
			<td>Billable Hours:</td>
			<td>
			<input type="text" value="0" id="strBillableTime_0" name="strBillableTime" style="width:62px" onblur="checkBillHours('0');">
			</td>
		</tr> -->
		
		<tr>
			<td>On-Site:</td>
			<td>
			<% if(taskLocation != null && taskLocation.equals("OFS")) { %>
				<input type="hidden" style="width: 30px;"  id="strTaskOnOffSiteT_0" name="strTaskOnOffSiteT" value="0">
				<input type="checkbox" name="strTaskOnOffSite" id="strTaskOnOffSite_0" onchange="setValue()" style="width: 30px;">
			<% } else { %>	
				<input type="hidden" style="width: 30px;"  id="strTaskOnOffSiteT_0" name="strTaskOnOffSiteT" value="1">
				<input type="checkbox" name="strTaskOnOffSite" id="strTaskOnOffSite_0" onchange="setValue()" style="width: 30px;" checked="checked">
			<% } %>	
			</td>
		</tr>
		
		<tr>
			<td>Description:</td>
			<td>
				<textarea cols="50" rows="2" style="width: 220px !important;" name="taskDescription"><%=taskDescription != null ? taskDescription : "" %> </textarea>
			</td>
		</tr>
		
		<tr>
			<td>Completion Status (%):</td>
			<td><input type="text" name="per" style="width:100px !important"/>
				<%-- <s:textfield name="per" cssStyle="width:100px !important"/> --%>
			</td>
		</tr>
		
		<tr>
			<td colspan="2" align="center"><input type="submit" class="btn btn-primary" value="Submit"/></td>
		</tr>
	</table>

</form>


<script type="text/javascript">

$("#formEndTask").submit(function(e){
	var taskId = document.getElementById("tid").value;
	e.preventDefault();
	var form_data = $("form[name='formEndTask']").serialize();
 	  $.ajax({
		url : "TaskUpdateTime.action",
		data: form_data,
		cache : false,
		success: function(result){
			$("#subSubDivResult").html(result);
		},
		error: function(result){
			$.ajax({
				url: 'EmpViewProject.action?taskId='+taskId,
				cache: true,
				success: function(result){
					$("#subSubDivResult").html(result);
		   		}
			});
		}
	});
 	$("#modalInfo").hide();
 	
});

</script>
