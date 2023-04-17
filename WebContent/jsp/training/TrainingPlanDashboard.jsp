<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%
	String strSessionUserType = (String) session.getAttribute(IConstants.USERTYPE);
%>


<script type="text/javascript" charset="utf-8">
	
/* function openAppraisalPreview(id) {
	//alert("openQuestionBank id "+ id)
	removeLoadingDiv('the_div');
	 var dialogEdit = '#trainingPreviewDiv';
	 dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
			$(dialogEdit).dialog(
				{
				autoOpen : false,
				bgiframe : true,
				resizable : false,
				height : 800,
				width : 1100,
				modal : true,
				title : 'Training Preview',
				open : function() {
					var xhr = $.ajax({
						url : "AppraisalPreview.action?id="+id,
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
	} */
	
	
function addRequest(operation,id) {

	var action="AddTrainingPlan.action?operation="+operation;
	if(id=='')
	window.location=action;
	else {
		action+="&ID="+id;
		window.location=action;
	}
}

function previewcertificate(id,viewMode){
	  
	  
	  var dialogEdit = '#ViewCertificate';
		dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>')
				.appendTo('body');
		$(dialogEdit).dialog({
			autoOpen : false,
			bgiframe : true,
			resizable : false,
			/* height : 500,
			width :  550, */
			height : viewMode==0 ? 860 : 603,
			width : viewMode==0 ? 603 : 860,
			
			modal : true,
			title : 'View Certificate',
			open : function() {
				var xhr = $.ajax({
					url : "ViewCertificate.action?ID="+id,
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
  
	</script>

<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Reviews" name="title" />
</jsp:include>


<div class="leftbox reportWidth">


<%
					UtilityFunctions uF = new UtilityFunctions();
					List<List<String>> alTrainingPlan = (List<List<String>>) request.getAttribute("alTrainingPlan");
					Map<String,String> hmCertificatePrintMode=(Map<String,String>)request.getAttribute("hmCertificatePrintMode");
					//List<List<String>> allAppraisalreport = (List<List<String>>) request.getAttribute("allAppraisalreport");
					
					int totalTrainings1 = 0;
					int totalEmps1 = 0;
					int totalTraineePending1 = 0;
					int totalTraineeUnderTrn1 = 0;
					//int totalAprFinalised1 = 0;
					
					for (int i = 0; i < alTrainingPlan.size(); i++) {
						List<String> alinner = (List<String>) alTrainingPlan.get(i);

						totalTrainings1 = alTrainingPlan.size();
						totalEmps1 += uF.parseToInt(alinner.get(2));
						
						totalTraineePending1 += uF.parseToInt(alinner.get(11));
						totalTraineeUnderTrn1 += uF.parseToInt(alinner.get(12));
						
						//totalAprFinalised1 += uF.parseToInt(alinner.get(10)); 
					}
				%>

<div>
		<div class="filter_div">
			<div class="skill_div" style="width:12%">
                    <p class="sk_value" style="text-align:center"><%=totalTrainings1 %></p>
                    <p class="sk_name" style="text-align:center">Trainings</p>
               </div>
               <div class="skill_div" style="width:12%">
                    <p class="sk_value" style="text-align:center"><%=totalEmps1 %></p>
                    <p class="sk_name" style="text-align:center">Trainee</p>
               </div>
               <div class="skill_div" style="width:12%">
                    <p class="sk_value" style="text-align:center"><%=totalTraineePending1 %></p>
                    <p class="sk_name" style="text-align:center">Pending</p>
               </div> 
               <div class="skill_div" style="width:14%">
                    <p class="sk_value" style="text-align:center"><%=totalTraineeUnderTrn1 %></p>
                    <p class="sk_name" style="text-align:center">Under Training</p>
               </div>  
              <%--  <div class="skill_div" style="width:12%">
                    <p class="sk_value" style="text-align:center"><%=totalAprFinalised1 %></p>
                    <p class="sk_name" style="text-align:center">Finalised</p>
               </div> --%>
		</div>
	</div>


	<div class="attendance">

		<!-- <table class="display tb_style" >  -->
	<%-- <%if(uF.parseToBoolean((String)request.getAttribute(IConstants.ADD_ACCESS))){ %> --%>
	<% if (strSessionUserType != null &&  (strSessionUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strSessionUserType.equalsIgnoreCase(IConstants.MANAGER) || strSessionUserType.equalsIgnoreCase(IConstants.ADMIN))) {%>
	<div style="float: right; margin-bottom: 10px">
		<a href="AddTrainingPlan.action?operation=A"><input type="button" class="input_button" value="Add New Training Plan"> </a>
	</div>
	<%} %>
		<table width="100%" cellspacing="0" cellpadding="2" align="left" class="tb_style">
			<tbody>
				<tr class="darktable">
					<th style="width: 40%;text-align: center;" rowspan="2">Trainings Name</th>
					<th style="width: 10%;text-align: center;" rowspan="2">Deadline</th>
					<th style="width: 10%;text-align: center;" rowspan="2">Trainees</th>
					<th style="width: 20%;text-align: center;" colspan="2">Trainings Status</th>
					<th style="width: 10%;text-align: center;" rowspan="2">Certifications</th>
					<th style="width: 10%;text-align: center;" rowspan="2">Actions</th>
				</tr>

				<tr class="darktable">
					<th style="width: 10%">Pending</th>
					<th style="width: 10%">Under Training</th>
					<!-- <th style="width: 10%">Finalised</th> -->
				</tr>

				<%
					int totalTrainings = 0;
					int totalEmps = 0;
					int totalTraineePending = 0;
					int totalTraineeUnderTrn = 0;
					//int totalAprFinalised = 0;
					
					for (int i = 0; i < alTrainingPlan.size(); i++) {
						List<String> alinner = (List<String>) alTrainingPlan.get(i);
						totalTrainings = alTrainingPlan.size();
						totalEmps += uF.parseToInt(alinner.get(2));
						totalTraineePending += uF.parseToInt(alinner.get(11));
						totalTraineeUnderTrn += uF.parseToInt(alinner.get(12));
						
						//totalAprFinalised += uF.parseToInt(alinner.get(10));
				%>
				<tr class="lighttable">
					<td>
						<b><%=alinner.get(1)%></b>
						<p style="padding-left:20px"><%=alinner.get(3)%>, <%=alinner.get(4)%></p>
					</td>
					
					<td align="center"><%=alinner.get(6)%></td>
					<td class="blueColor" align="center"><strong> <%=alinner.get(2)%></strong></td>

					<td align="center"><%=alinner.get(11)%></td>
					<td align="center"><%=alinner.get(12)%></td>
					<%-- <td align="center"><%=alinner.get(10)%></td> --%>
					<td><%=alinner.get(7)%>&nbsp;
						<%if(alinner.get(7).equals("YES")){
							String printmode=hmCertificatePrintMode.get(alinner.get(10).trim()); %>
							<a href="javascript:void(0)" onclick="previewcertificate('<%=alinner.get(10)%>','<%=printmode%>');">Preview</a>
						<%} %>
					</td>
					<td>
						<a href="AddTrainingPlan.action?operation=D&ID=<%=alinner.get(0)%>" class="edit" style="text-indent:-99999px; padding:0; float: left;">Edit</a>
						<a onclick="return confirm('Are you sure, you want to delete this Plan?')" href="AddTrainingPlan.action?operation=D&ID=<%=alinner.get(0)%>" class="del">Delete</a>
					</td>

				</tr>
				<%
					}
				%>
					<%
					if (alTrainingPlan.size() == 0) {
					%>

				<tr class="lighttable">

					<td colspan="7"><div class="nodata msg">
							<span> No Data Available</span>
						</div>
					</td>
				</tr>
				<%
					}else{
				%>

				<tr class="table_result">
					
					<td style="text-align: center;" colspan="2"><b>Total<b>
					</td>

					<td style="text-align: center;padding-right:7px"><b><%=totalEmps%></b>
					</td>
					
					<td style="text-align: center;padding-right:7px"><b><%=totalTraineePending%></b>
					</td>
					<td style="text-align: center;padding-right:7px"><b><%=totalTraineeUnderTrn%></b>
					</td>
					<%-- <td style="text-align: center;padding-right:7px"><b><%=totalAprFinalised%></b> --%>
					</td>
					<td>&nbsp;</td>
					
				</tr>

               <%} %>

				
			</tbody>
		</table>

	</div>

</div>

<div id="trainingPreviewDiv"></div>