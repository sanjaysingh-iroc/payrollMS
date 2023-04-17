

<%@page import="org.apache.struts2.components.Param"%>
<%@page import="java.util.List"%>
<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib uri="http://granule.com/tags" prefix="g"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>


<link rel="stylesheet" href="js_bootstrap/datepicker/bootstrap-datepicker3.css">
<script type="text/javascript" src="js_bootstrap/datepicker/bootstrap-datepicker.min.js"></script>
<script type="text/javascript">
	$(function() {
		$("#strDate").datepicker({
			format : 'dd/mm/yyyy'
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
	});
</script>
<% String isCompleted = (String) request.getAttribute("isCompleted");
   String fromPage = (String) request.getAttribute("fromPage");
%>

<div>


	<s:if test="mode!='view'">


	<%if(isCompleted != null && isCompleted.equals("1")) { %>
	<div> Training Completed </div>
	<%} else { %>
	<s:form theme="simple" id = "frmTrainingStatus" name="frmTrainingStatus" action="TrainingStatus" method="POST"
		cssClass="formcss" enctype="multipart/form-data">
		<div>
			<s:hidden name="trainingId" id="trainingId" />
			<s:hidden name="lPlanId" id="lPlanId" />
			<input type="hidden" name="fromPage" id="fromPage" value="<%=fromPage%>"/>
			<table border="0" class="table">

				<tr>
					<td>Date:</td>
					<td><s:textfield name="statusDate" cssStyle="width:100px"
							id="strDate"></s:textfield></td>
				</tr>

				<tr>
					<td>Status:</td>
					<td><s:textfield name="percStatus" cssStyle="width:50px"></s:textfield>%</td>

				</tr>

				<tr>
					<td>Training Completed (Y/N):</td>
					<td><s:checkbox name="iscompleted"></s:checkbox></td>
				</tr>

				<tr>
					<td valign="top">Notes:</td>
					<td><textarea name="trainingNotes" id="notes" cols="35"
							rows="4"></textarea></td>
				</tr>
				<tr>
					<td>&nbsp;</td>
					<td><s:submit cssClass="btn btn-primary" cssStyle="width:125px; float:right;" value="Update Status"
					name="updateStatusSubmit" align="center"></s:submit></td>
				</tr>

			</table>

		</div>
	</s:form>
	
	<%} %>
</s:if>

	<br> <br>

	<div>
		<%
			List<List<String>> alStatusInfo = (List<List<String>>) request.getAttribute("alStatusInfo");

			for (int i = 0; alStatusInfo != null && i < alStatusInfo.size(); i++) {
				List<String> alInner = alStatusInfo.get(i);
		%>

		<div>
			<p class="past heading_dash"
				style="text-align: left; padding-left: 35px;">
				<b> <%=alInner.get(1)%>
				</b>
			</p>
			<div class="content1">

				<p>
					Status :
					<%=alInner.get(0)%>
				</p>
				<p>
					Notes :
					<%=alInner.get(2)%>
				</p>


			</div>
		</div>

		<%
			}
		%>


	</div>
</div>
<script>
<%if(fromPage != null) {%>
	$("#frmTrainingStatus").submit(function(event){
		event.preventDefault();
		var from = '<%=fromPage%>';
		var lPlanId = document.getElementById("lPlanId").value;
		
		var form_data = $("#frmTrainingStatus").serialize();
		var submitBtn = $('input[name = "updateStatusSubmit"]').val();
		$.ajax({
			type:'POST',
			url:'TrainingStatus.action',
			data:form_data+"&updateStatusSubmit="+submitBtn/* ,
			success:function(result){
				if(from != "" && from == "MyHR") {
					$("#divMyHRData").html(result);
				} else {
					$("#divLPDetailsResult").html(result);
				}
			} */
		});
		
		if(from != "" && from == "MyHR") {
			$.ajax({
				url: 'MyLearningPlan.action?fromPage=MyHR',
				cache: true,
				success: function(result){
					$("#divMyHRData").html(result);
		   		}
			});
		} else {
			$.ajax({
				url: 'LearningPlanAssessmentStatus.action?lPlanId='+lPlanId,
				cache: true,
				success: function(result){
					$("#divLPDetailsResult").html(result);
		   		}
			});
		}
	});

<%}%>
</script>