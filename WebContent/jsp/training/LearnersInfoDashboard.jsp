

<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

	
	
	<script type="text/javascript" charset="utf-8">
	

	function trainerComment(empId,planId) {

		var dialogEdit = '#trainerCommentPOPUP';
		dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
		$(dialogEdit).dialog({
			autoOpen : false,
			bgiframe : true,
			resizable : false,
			height : 300,
			width :	500,
			modal : true,
			title : 'Training Comments ' ,
			open : function() {
				var xhr = $.ajax({
					url : "InsertTrainerComment.action?empID="+empId+"&planID="+planId ,
					cache : false,
					success : function(data) {
						$(dialogEdit).html(data);
					}
				});
				xhr = null;

			},
			overlay : {
				backgroundColor : '#000',
				opacity : 0.5
			}
		});

		$(dialogEdit).dialog('open');
	}
	
	  
	function generateCertificate(empId,planId){
		
		if(confirm("Are you sure, you want to generate Certificate?")){
			
			var action = "AssignCertificate.action?empID="+empId+"&planID="+planId;
			getContent('certificateTD', action);
		}
		
		
	}
	


</script>

<%-- 
<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Trainer Info" name="title" />
</jsp:include>
 --%>

			<%
				UtilityFunctions uF=new UtilityFunctions();
			%>

<div class="leftbox reportWidth" style="padding:10px 5px 10px 8px ;">

<%-- 
<s:form name="frm_TrainingDashboard" action="TrainingDashboard" theme="simple">

 		<div class="filter_div">
			<div class="filter_caption">Filter</div>

		<s:select theme="simple" name="strLocation" listKey="wLocationId"
				listValue="wLocationName" headerKey="" headerValue="All Locations"
				list="wLocationList" key="0" />
				
				<s:select name="strLevel" list="levelList"
									listKey="levelId" id="strLevel" listValue="levelCodeName"
									headerKey="" headerValue="Select Level" ></s:select>
			

 		<s:select headerKey="" headerValue="Ongoing trainings"  
 		list="#{'1':'Completed Trainings' }" name="filterTraining" onchange="document.frm_TrainingDashboard.submit()" ></s:select>


		</div>
	</s:form> --%>



		<table class="tb_style" style="width:100%">
		<thead>
			<tr>
							
				<th style="text-align: left;">Learner Name</th>
				<th style="text-align: left;">Attendance(%)</th>
				<th style="text-align: left;">Completed</th>
				<th style="text-align: left;">Certificate</th>
				<th style="text-align: left;">Trainer Comments</th>
		<!-- 		<th style="text-align: left;">Learners</th>
				<th style="text-align: left;">Certificates given</th>
				<th style="text-align: left;">Mobile No</th>
				<th style="text-align: left;">Factsheet</th>
				<th style="text-align: left;">Edit Info</th>
	 -->


			</tr>
		</thead>
		
		<tbody>
			<%
				List<List<String>> alLearnersInfoDashboard = (List) request.getAttribute("alLearnersInfoDashboard");
			%>

			<%
				for (int i = 0; i < alLearnersInfoDashboard.size(); i++) {
					List<String> alinner = alLearnersInfoDashboard.get(i);
			%>

			<tr>

				<td><%=alinner.get(1)%></td>
				<td ><%=uF.showData(alinner.get(2),"0")%> %</td>
				<td><%=alinner.get(3)%></td>
				<%if(alinner.get(4).equals("1")){ %>
				<td>Certificate Given</td>
				<%}else if(alinner.get(4).equals("0")){ %>
				<td id="certificateTD"><a href="javascript:void(0)" onclick="generateCertificate(<%=alinner.get(0)%>,<%=alinner.get(6)%>);">Generate Certificate</a></td>
				<%}else{ %>
				<td>NA</td>
				<%} %>
				
				<% if(alinner.get(5).equals("")){%>
				<td id="commentID"><a href="javascript:void(0)" onclick="trainerComment(<%=alinner.get(0)%>,<%=alinner.get(6)%>);">Click here to Add comment</a></td>
				<%}else{ %>
				<td><%=alinner.get(5)%></td>
				<%} %>
			</tr>
			<%
				}
			%>

			<%
				if (alLearnersInfoDashboard.size() == 0) {
			%>

			<tr>
				<td colspan="5"><div class="nodata msg">
						<span>No Learner Added</span>
					</div></td>
			</tr>

			<%
				}
			%> 
		</tbody>
	</table>


</div>
