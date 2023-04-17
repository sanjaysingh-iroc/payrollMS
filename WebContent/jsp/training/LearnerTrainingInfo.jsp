

<%@page import="java.util.List"%>
<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib uri="http://granule.com/tags" prefix="g"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>


<script type="text/javascript">
/* 	$(function() {
		$("#strDate").datepicker({
			dateFormat : 'dd/mm/yy'
		});
		// $( "#leaveToDate" ).datepicker({dateFormat: 'dd/mm/yy'});
	});

	jQuery(document).ready(function() {

		jQuery(".content1").hide();
		//toggle the componenet with class msg_body
		jQuery(".heading_dash").click(function() {
			jQuery(this).next(".content1").slideToggle(500);
			$(this).toggleClass("close_div");
		});
	}); */
</script>




<% List<String> alTrainingInfo=(List<String>)request.getAttribute("alTrainingInfo"); %>

<table border="0" class="display tb_style"
		style="float: left; width: 98%">

		<tr>
			<td class="txtlabel alignRight" style="width:30%"><b>Training Title :</b></td>
			<td> (<%=alTrainingInfo.get(1)%>)</td>
		</tr>


		<tr>
			<td class="txtlabel alignRight" style="width:30%"><b>Training Objective :</b></td>
			<td><%=alTrainingInfo.get(2)%></td>
		</tr>

		<tr>
			<td class="txtlabel alignRight" style="width:30%"><b>Training Type :</b></td>
			<td><%=alTrainingInfo.get(3)%></td>
		</tr>
		<tr>

		</tr>
		<tr>
			<td class="txtlabel alignRight" style="width:30%" ><b>Certification : </b></td>
			<td><%=alTrainingInfo.get(4)%></td>
		</tr>

		<tr>
			<td class="txtlabel alignRight" style="width:30%"><b>Attribute :</b></td>
			<td><%=alTrainingInfo.get(5)%>
			</td>
		</tr>
		<%-- <tr>
			<td class="txtlabel alignRight"><b>Max. Experience:</b></td>
			<td><%=uF.parseToInt(alTrainingInfo.get(10))%>Years and <%=uF.parseToInt(alTrainingInfo.get(11))%>Months
			</td>
		</tr>
		<tr>
			<td class="txtlabel alignRight"><b>Education:</b></td>
			<td><%=uF.showData(alTrainingInfo.get(12), "NO EDUCATION SPECIFIED")%>
			</td>
		</tr>

		<tr>
			<td class="txtlabel" valign="top" colspan="2">Job
				Description:</td>
		</tr>

		<tr>
			<td colspan="2"><%=alTrainingInfo.get(7)%></td>
		</tr>

		<tr>
			<td class="txtlabel" valign="top" colspan="2">Candidate
				Profile:</td>
		</tr>

		<tr>
			<td colspan="2"><%=alTrainingInfo.get(13)%></td>
		</tr> --%>


	</table>



	<s:form theme="simple" id="formID" name="formID" action="TakeLearningPlan" method="POST" cssClass="formcss" enctype="multipart/form-data">
		
		<input type="hidden" name="planId" value="<%=alTrainingInfo.get(0)%>"/>
	
	<div style="float:right; margin-top:20px;margin-right:10px">
	
   <s:submit name="takeLearning" cssClass="btn btn-primary" value="Take"></s:submit> 
	
	</div>
	</s:form>
<script>
$("#formID").submit(function(event){
	event.preventDefault();
	var form_data = $("#formID").serialize();
	$("#divMyHRData").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 	$.ajax({ 
		type : 'POST',
		url: 'TakeLearningPlan.action',
		data: form_data,
		cache: true/* ,
		success: function(result){
			$("#divMyHRData").html(result);
		} */
	});
	 	
	 	$.ajax({
			url: 'MyLearningPlan.action',
			cache: true,
			success: function(result){
				$("#divMyHRData").html(result);
	   		}
		});
});
</script>


