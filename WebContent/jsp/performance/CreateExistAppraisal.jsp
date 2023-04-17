
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<script type="text/javascript">
	$(function() {
		var a = '#from';
		$("#from").datepicker({
			format : 'dd/mm/yyyy'
		});
		$("#to").datepicker({
			format : 'dd/mm/yyyy'
		}); 
	});

	jQuery(document).ready(function() {
		jQuery("#formID").validationEngine();

	}); 
	
jQuery(document).ready(function() {
		
		//	jQuery("#frmClockEntries").validationEngine();
			
		  jQuery(".content1").hide();
		  //toggle the componenet with class msg_body
		  jQuery(".heading_dash").click(function()
		  {
		    jQuery(this).next(".content1").slideToggle(500);
			$(this).toggleClass("close_div"); 
		  });
		});
</script>
<%
	List<String> appraisalList = (List<String>) request.getAttribute("appraisalList");
	Map hmScoreDetailsMap = (Map) request.getAttribute("hmScoreDetailsMap");
	Map<String, List<List<String>>> hmKRA1 =(Map<String, List<List<String>>>)request.getAttribute("hmKRA1");
	Map<String, List<List<String>>> hmKRA =(Map<String, List<List<String>>>)request.getAttribute("hmKRA");
	Map<String, String> hmMesures = (Map<String, String>) request.getAttribute("hmMesures");
System.out.println("in new apprasail");
UtilityFunctions uF=new UtilityFunctions();

%>

	<jsp:include page="../common/SubHeader.jsp">
		<jsp:param value="Create Review From Template" name="title" />
	</jsp:include>


<div class="leftbox reportWidth">
<%=uF.showData((String)session.getAttribute(IConstants.MESSAGE), "") %>
	<s:form action="#" id="formID" method="POST" theme="simple">
		
<h2><b><%=appraisalList.get(1)%>&nbsp;&nbsp;<%=appraisalList.get(5)%>&nbsp;&nbsp;<%=appraisalList.get(9)%>&nbsp;&nbsp;<%=appraisalList.get(8)%></b></h2>
<br/>
		<table class="tb_style" width="98%">
			<%-- <tr>
				<th width="15%" align="right">
				<a href="EditAppraisal.action?id=<%=appraisalList.get(0) %>&appsystem=appraisal" class="edit_lvl"  title="Edit"></a>
				Appraisal Name</th>
				<td colspan="1"><b><%=appraisalList.get(1)%>&nbsp;&nbsp;<%=appraisalList.get(5)%>&nbsp;&nbsp;<%=appraisalList.get(9)%>&nbsp;&nbsp;<%=appraisalList.get(8)%></b>
				</td>
			</tr> --%>
			<tr>
				<th width="15%" align="right">Review Type</th>
				<td><%=appraisalList.get(14)%></td>
			</tr>
			<tr>	
				<th align="right">Review Frequency</th>
				<td><%=appraisalList.get(7)%></td>
			</tr>
			
			<tr>
				<th align="right">Orientation</th>
				<td colspan="1"><%=appraisalList.get(2)%></td>
			</tr>
			<tr>
				<th valign="top" align="right">Reviewee</th>
				<td colspan="1"><%=appraisalList.get(12)%></td>
			</tr>
			<tr>
				<th valign="top" align="right">Description</th>
				<td colspan="1"><%=appraisalList.get(15)%></td>
			</tr>
		</table>
		
		
		
		
<br/><br/><br/><br/><br/>
<h2>Appraisal Forms</h2>
<p style="margin: 0px 0px 0px 15px" class="addnew desgn">
	<a href="javascript:void(0)" class="add_lvl" onclick="window.location.href ='AddAppraisalLevel.action?id=<%=appraisalList.get(0) %>';" title="Add New Level">Add New Level</a>
	
						</p>
<br/>

		<%
			List<List<String>> outerList1 = (List<List<String>>) request.getAttribute("outerList1");

				Map<String, List<Map<String, List<List<String>>>>> levelMp = (Map<String, List<Map<String, List<List<String>>>>>) request.getAttribute("levelMp");
				
		%>

		<!-- <div
			style="overflow: hidden; float: left; border: 1px solid rgb(0, 0, 0); width: 100%;"> -->

			<%
				for (int i = 0; outerList1 != null && i < outerList1.size(); i++) {
						List<String> innerList1 = outerList1.get(i);
						if (uF.parseToInt(innerList1.get(3)) == 2) {
							List<Map<String, List<List<String>>>> list = levelMp.get(innerList1.get(0));
							Map<String, List<List<String>>> scoreMp = list.get(0);
			%>

			<div style="overflow: hidden; float: left; border: 1px solid #eee; width: 100%;  text-align: left; margin-bottom:10px;">
			<div class="past heading_dash" style="text-align:left;padding-left:50px; height:auto;">
			<%=innerList1.get(1)%> 
				
				(
				<%=uF.showData(innerList1.get(6), "")%>)
				<p style="font-weight: normal; font-size: 10px;">
				<%=uF.showData(innerList1.get(4), "")%>
				</p>
			</div>

			<div class="content1">
			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 100%;">
				
					<table class="tb_style" style="width: 100%; float: left;">
						<tr>
							<th width="90%">Question</th>
							<th>Weightage</th>
							<!-- <th>Description</th> -->
						</tr>
						<%
							List<List<String>> goalList = scoreMp.get(innerList1.get(0));
										for (int k = 0; goalList != null && k < goalList.size(); k++) {
											List<String> goalinnerList = goalList.get(k);
						%>
						<tr>
							<td>
							<a  style="height:0px; width:0px;" href="javascript:void(0);" class="add" onclick="window.location.href ='AddLevelData.action?id=<%=appraisalList.get(0) %>&appsystem=<%=innerList1.get(3) %>&scoreType=<%=innerList1.get(2) %>&UID=<%=innerList1.get(0) %>&type=quest';" title="Add">Add</a>
							<a href="javascript:void(0)" class="edit_lvl" onclick="window.location.href ='EditAppraisal.action?id=<%=appraisalList.get(0) %>&appsystem=<%=innerList1.get(3) %>&scoreType=<%=innerList1.get(2) %>&editID=<%=goalinnerList.get(3) %>&type=quest';" title="Edit">Edit</a>
							<%=goalinnerList.get(0)%></td>							
							<td style="text-align: right"><%=goalinnerList.get(1)%>%</td>
							<%-- <td style="text-align: center"><%=goalinnerList.get(2)%></td> --%>
						</tr>

						<%
							}
						%>
					</table>
				
			</div>
			</div>
			</div>

			<%
				} else if (uF.parseToInt(innerList1.get(3)) == 1){
							if (uF.parseToInt(innerList1.get(2)) == 1) {
								List<Map<String, List<List<String>>>> list = levelMp.get(innerList1.get(0));
								Map<String, List<List<String>>> scoreMp = list.get(0);
								Map<String, List<List<String>>> measureMp = list.get(1);
								Map<String, List<List<String>>> questionMp = list.get(2);
								Map<String, List<List<String>>> GoalMp = list.get(3);
								Map<String, List<List<String>>> objectiveMp = list.get(4);
			%>

			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 100%;  text-align: left; margin-bottom:10px;">
				<div class="past heading_dash" style="text-align:left;padding-left:50px; height:auto;">
				<%=innerList1.get(1)%>
				<p style="font-weight: normal; font-size: 10px;">
				<%=innerList1.get(4)%>
				</p>
				</div>
				
				<div class="content1">
			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 14%;  text-align: center;">
				<!-- <b>Score Card</b> -->
				<b>Competencies</b>
			</div>
			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 7%;  text-align: center;">
				<b>Weightage</b>
			</div>

			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 14%;  text-align: center;">
				<b>Goal </b>
			</div>
			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 7%;  text-align: center;">
				<b>Weightage</b>
			</div>
			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 14%;  text-align: center;">
				<b>Objective </b>
			</div>
			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 7%;  text-align: center;">
				<b>Weightage</b>
			</div>
			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 7%;  text-align: center;">
				<b>Measure</b>
			</div>
			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 6.9%;  text-align: center;">
				<b>Weightage</b>
			</div>
			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 14.5%;  text-align: center;">
				<b>Question</b>
			</div>
			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 6.9%;  text-align: center;">
				<b>Weightage</b>
			</div>
			<!-- <div style="overflow: hidden; float: left; width: 100%;"> -->
				<%
					List<List<String>> scoreList = scoreMp.get(innerList1.get(0));
									for (int j = 0; scoreList != null && j < scoreList.size(); j++) {

										List<String> innerList = scoreList.get(j);
				%>

				<div style="overflow: hidden; float: left; width: 100%;">
					<div
						style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 14.2%;">
						<p style="padding-left:10px">
						<a style="height:0px; width:0px;" href="javascript:void(0);" class="add" onclick="window.location.href ='AddLevelData.action?id=<%=appraisalList.get(0) %>&appsystem=<%=innerList1.get(3) %>&scoreType=<%=innerList1.get(2) %>&UID=<%=innerList1.get(0) %>&type=score';" title="Add">Add</a>
						<a href="javascript:void(0)" class="edit_lvl" 
						onclick="window.location.href ='EditAppraisal.action?id=<%=appraisalList.get(0) %>&appsystem=<%=innerList1.get(3) %>&scoreType=<%=innerList1.get(2) %>&editID=<%=innerList.get(0) %>&type=score';" title="Edit">Edit</a>						
						<%=innerList.get(1)%></p>					
						
					</div>
					<div
						style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 7.1%; text-align: right;">
						<p style="margin: 0px 10px 0px 0px;"><%=innerList.get(2)%>%</p>
					</div>

					<div
						style="overflow: hidden; float: left; border-left: 1px solid #eee; width: 78.6%;">
						<%
							List<List<String>> goalList = GoalMp.get(innerList.get(0));
												for (int k = 0; goalList != null && k < goalList.size(); k++) {
													List<String> goalinnerList = goalList.get(k);
						%>



						<div style="overflow: hidden; float: left; width: 100%;">
							<div
								style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 18%;">
								
									<p style="padding-left:10px">
									<a style="height:0px; width:0px;" href="javascript:void(0);" class="add" onclick="window.location.href ='AddLevelData.action?id=<%=appraisalList.get(0) %>&appsystem=<%=innerList1.get(3) %>&scoreType=<%=innerList1.get(2) %>&UID=<%=innerList.get(0) %>&type=goal';" title="Add">Add</a>
									<a href="javascript:void(0)" class="edit_lvl" onclick="window.location.href ='EditAppraisal.action?id=<%=appraisalList.get(0) %>&appsystem=<%=innerList1.get(3) %>&scoreType=<%=innerList1.get(2) %>&editID=<%=goalinnerList.get(0) %>&type=goal';" title="Edit">Edit</a>
									<%=goalinnerList.get(1)%>
									</p>
								
							</div>
							<div
								style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 9.1%; text-align: right;">
								<p style="margin: 0px 10px 0px 0px;"><%=goalinnerList.get(2)%>%</p>
							</div>

							<div
								style="overflow: hidden; float: left; border-left: 1px solid #eee; width: 72.7%;">
								<%
									List<List<String>> objectiveList = objectiveMp.get(goalinnerList.get(0));
															for (int l = 0; objectiveList != null && l < objectiveList.size(); l++) {
																List<String> objectivelinnerList = objectiveList.get(l);
								%>


								<div style="overflow: hidden; float: left; width: 100%;">
									<div
										style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 24.6%;"> 
										<p style="padding-left:10px">
										<a style="height:0px; width:0px;" href="javascript:void(0);" class="add" onclick="window.location.href ='AddLevelData.action?id=<%=appraisalList.get(0) %>&appsystem=<%=innerList1.get(3) %>&scoreType=<%=innerList1.get(2) %>&UID=<%=goalinnerList.get(0) %>&type=objective';" title="Add">Add</a>
										<a href="javascript:void(0)" class="edit_lvl" onclick="window.location.href ='EditAppraisal.action?id=<%=appraisalList.get(0) %>&appsystem=<%=innerList1.get(3) %>&scoreType=<%=innerList1.get(2) %>&editID=<%=objectivelinnerList.get(0) %>&type=objective';" title="Edit">Edit</a>
										<%=objectivelinnerList.get(1)%>
										</p>										
									</div>
									<div
										style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 12.5%; text-align: right;">
										<p style="margin: 0px 10px 0px 0px;"><%=objectivelinnerList.get(2)%>%</p>
									</div>
									<div
										style="overflow: hidden; float: left; border-left: 1px solid #eee; width: 62.7%;">
										<%
											List<List<String>> measureList = measureMp.get(objectivelinnerList.get(0));
																		for (int m = 0; measureList != null && m < measureList.size(); m++) {
																			List<String> measureinnerList = measureList.get(m);
										%>


										<div style="overflow: hidden; float: left; width: 100%;">
											<div
												style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 19.5%;">
												<p style="padding-left:10px">
												<a style="height:0px; width:0px;" href="javascript:void(0);" class="add" onclick="window.location.href ='AddLevelData.action?id=<%=appraisalList.get(0) %>&appsystem=<%=innerList1.get(3) %>&scoreType=<%=innerList1.get(2) %>&UID=<%=objectivelinnerList.get(0) %>&type=measure';" title="Add">Add</a>
												<a href="javascript:void(0)" class="edit_lvl" onclick="window.location.href ='EditAppraisal.action?id=<%=appraisalList.get(0) %>&appsystem=<%=innerList1.get(3) %>&scoreType=<%=innerList1.get(2) %>&editID=<%=measureinnerList.get(0) %>&type=measure';" title="Edit">Edit</a>
												<%=measureinnerList.get(1)%>
												</p>
											</div>

											<div
												style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 19.5%; text-align: right;">
												<p style="margin: 0px 10px 0px 0px;"><%=measureinnerList.get(2)%>%</p>
											</div>

											<div style="overflow: hidden; float: left; width: 60.6%;">
												<%
													List<List<String>> questionList = questionMp.get(measureinnerList.get(0));
																					for (int n = 0; questionList != null && n < questionList.size(); n++) {
																						List<String> question1List = questionList.get(n);
												%>
												<div style="overflow: hidden; float: left; width: 100%;">
													<div
														style="overflow: hidden; float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 67%;">
															<p style="padding-left:10px">
															<a style="height:0px; width:0px;" href="javascript:void(0);" class="add" onclick="window.location.href ='AddLevelData.action?id=<%=appraisalList.get(0) %>&appsystem=<%=innerList1.get(3) %>&scoreType=<%=innerList1.get(2) %>&UID=<%=measureinnerList.get(0) %>&type=quest';" title="Add">Add</a>
															<a href="javascript:void(0)" class="edit_lvl" onclick="window.location.href ='EditAppraisal.action?id=<%=appraisalList.get(0) %>&appsystem=<%=innerList1.get(3) %>&scoreType=<%=innerList1.get(2) %>&editID=<%=question1List.get(3) %>&type=quest';" title="Edit">Edit</a>
															<%=question1List.get(0)%>
															</p>
													</div>
													<div
														style="overflow: hidden; float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 32.2%; text-align: right;">
														<p style="margin: 0px 10px 0px 0px;"><%=question1List.get(1)%>%</p>
													</div>
													
												</div>
												<%
													}
												%>
											</div>
										</div>
										<%
											}
										%>
									</div>
								</div>

								<%
									}
								%>
							</div>
						</div>
						<%
							}
						%>					
				</div>
				</div>				
				<%
					}
				%>
			</div>
			</div>

			<%
				} else if (uF.parseToInt(innerList1.get(2)) == 2) {

								List<Map<String, List<List<String>>>> list = levelMp.get(innerList1.get(0));
								Map<String, List<List<String>>> scoreMp = list.get(0);
								Map<String, List<List<String>>> measureMp = list.get(1);
								Map<String, List<List<String>>> questionMp = list.get(2);
			%>
			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 100%;  text-align: left; margin-bottom:10px;">
				<div class="past heading_dash" style="text-align:left;padding-left:50px; height:auto;">
				
				<%=innerList1.get(1)%>
				<p style="font-weight: normal; font-size: 10px;">
				<%=innerList1.get(4)%>
				</p>
			</div>

			<div class="content1">
			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 22.9%;  text-align: center;">
				<!-- <b>Score Card</b> -->
				<b>Competencies</b>
			</div>
			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 14%;  text-align: center;">
				<b>Weightage</b>
			</div>

			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 13.3%;  text-align: center;">
				<b>Measure</b>
			</div>
			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 13.3%;  text-align: center;">
				<b>Weightage</b>
			</div>
			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 22.9%;  text-align: center;">
				<b>Question</b>
			</div>
			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 12.6%;  text-align: center;">
				<b>Question Weightage</b>
			</div>

			<div style="overflow: hidden; float: left; width: 100%;">
				<%
					List<List<String>> scoreList = scoreMp.get(innerList1.get(0));
									for (int j = 0; scoreList != null && j < scoreList.size(); j++) {
										List<String> innerList = scoreList.get(j);
				%>

				<div style="overflow: hidden; float: left; width: 100%;">
					<div
						style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 23.1%;">
							<p style="padding-left:10px">
							<a style="height:0px; width:0px;" href="javascript:void(0);" class="add" onclick="window.location.href ='AddLevelData.action?id=<%=appraisalList.get(0) %>&appsystem=<%=innerList1.get(3) %>&scoreType=<%=innerList1.get(2) %>&UID=<%=innerList1.get(0) %>&type=score';" title="Add">Add</a>
							<a href="javascript:void(0)" class="edit_lvl" onclick="window.location.href ='EditAppraisal.action?id=<%=appraisalList.get(0) %>&appsystem=<%=innerList1.get(3) %>&scoreType=<%=innerList1.get(2) %>&editID=<%=innerList.get(0) %>&type=score';" title="Edit">Edit</a>
							<%=innerList.get(1)%>							
							</p>
					</div>
					<div
						style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 14.1%; text-align: right;">

						<p style="margin: 0px 10px 0px 0px;"><%=innerList.get(2)%>%</p>

					</div>

					<div
						style="overflow: hidden; float: left; border-left: 1px solid #eee; width: 62.7%;">
						<%
							List<List<String>> measureList = measureMp.get(innerList.get(0));
												for (int k = 0; measureList != null && k < measureList.size(); k++) {
													List<String> measureinnerList = measureList.get(k);
						%>

						<div
							style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 21.3%;">
								<p style="padding-left:10px">
								<a style="height:0px; width:0px;" href="javascript:void(0);" class="add" onclick="window.location.href ='AddLevelData.action?id=<%=appraisalList.get(0) %>&appsystem=<%=innerList1.get(3) %>&scoreType=<%=innerList1.get(2) %>&UID=<%=innerList.get(0) %>&type=measure';" title="Add">Add</a>
								<a href="javascript:void(0)" class="edit_lvl" onclick="window.location.href ='EditAppraisal.action?id=<%=appraisalList.get(0) %>&appsystem=<%=innerList1.get(3) %>&scoreType=<%=innerList1.get(2) %>&editID=<%=measureinnerList.get(0) %>&type=measure';" title="Edit">Edit</a>
								<%=measureinnerList.get(1)%>
								</p>
						</div>

						<div
							style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 21.3%; text-align: right;">
							<p style="margin: 0px 10px 0px 0px;"><%=measureinnerList.get(2)%>%</p>
						</div>

						<div
							style="overflow: hidden; float: left; border-left: 1px solid #eee; width: 57.1%;">

							<%
								List<List<String>> questionList = questionMp.get(measureinnerList.get(0));
														for (int l = 0; questionList != null && l < questionList.size(); l++) {
															List<String> question1List = questionList.get(l);
							%>
							<div style="overflow: hidden; float: left; width: 100%;">
								<div
									style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 64.2%;">
										<p style="padding-left:10px">
										<a style="height:0px; width:0px;" href="javascript:void(0);" class="add" onclick="window.location.href ='AddLevelData.action?id=<%=appraisalList.get(0) %>&appsystem=<%=innerList1.get(3) %>&scoreType=<%=innerList1.get(2) %>&UID=<%=measureinnerList.get(0) %>&type=quest';" title="Add">Add</a>
										<a href="javascript:void(0)" class="edit_lvl" onclick="window.location.href ='EditAppraisal.action?id=<%=appraisalList.get(0) %>&appsystem=<%=innerList1.get(3) %>&scoreType=<%=innerList1.get(2) %>&editID=<%=question1List.get(3) %>&type=quest';" title="Edit">Edit</a>
										<%=question1List.get(0)%>
										</p>
								</div>
								<div
									style="overflow: hidden; float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 35.4%; text-align: right;">
									<p style="margin: 0px 10px 0px 0px;"><%=question1List.get(1)%></p>
								</div>
							</div>
							<%
								}
							%>
						</div>

						<%
							}
						%>
					</div>
				</div>
				</div>
				<%
					}
				%>

			</div>
			</div>

			<%
				} else {
								List<Map<String, List<List<String>>>> list = levelMp.get(innerList1.get(0));
								Map<String, List<List<String>>> scoreMp = list.get(0);
								Map<String, List<List<String>>> measureMp = list.get(1);
								Map<String, List<List<String>>> questionMp = list.get(2);
								Map<String, List<List<String>>> GoalMp = list.get(3);
			%>


			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 100%;  text-align: left; margin-bottom:10px;">
				<div class="past heading_dash" style="text-align:left;padding-left:50px; height:auto;">
				<%=innerList1.get(1)%>
				<p style="font-weight: normal; font-size: 10px;">
				<%=innerList1.get(4)%>
				</p>
				</div>
			<div class="content1">
			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 10%;  text-align: center;">
				<!-- <b>Score Card</b> -->
				<b>Competencies</b>
			</div>
			<div 
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 14%;  text-align: center;">
				<b>Weightage</b>
			</div>

			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 16.5%;  text-align: center;">
				<b>Goal </b>
			</div>
			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 10%;  text-align: center;">
				<b>Weightage</b>
			</div>

			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 8.2%;  text-align: center;">
				<b>Measure</b>
			</div>
			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 8.2%;  text-align: center;">
				<b>Weightage</b>
			</div>
			<div
				style="overflow: hidden; float: left;  border: 1px solid #eee; width: 19%; text-align: center;">
				<b>Question</b>
			</div>
			<div
				style="overflow: hidden; float: left;  border: 1px solid #eee; width: 12.8%; text-align: center;">
				<b>Weightage</b>
			</div>

			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 100%;">
				<%
					List<List<String>> scoreList = scoreMp.get(innerList1.get(0));
									for (int j = 0; scoreList != null && j < scoreList.size(); j++) {

										List<String> innerList = scoreList.get(j);
				%>

				<div style="overflow: hidden; float: left; width: 100%;">
					<div
						style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 10%;">
						<p style="padding-left:10px">
						<a style="height:0px; width:0px;" href="javascript:void(0);" class="add" onclick="window.location.href ='AddLevelData.action?id=<%=appraisalList.get(0) %>&appsystem=<%=innerList1.get(3) %>&scoreType=<%=innerList1.get(2) %>&UID=<%=innerList1.get(0) %>&type=score';" title="Add">Add</a>
						<a href="javascript:void(0)" class="edit_lvl" onclick="window.location.href ='EditAppraisal.action?id=<%=appraisalList.get(0) %>&appsystem=<%=innerList1.get(3) %>&scoreType=<%=innerList1.get(2) %>&editID=<%=innerList.get(0) %>&type=score';" title="Edit">Edit</a>
						<%=innerList.get(1)%>()
						</p>						
					</div>
					<div
						style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 14.2%; text-align: right;">

						<p style="margin: 0px 10px 0px 0px;"><%=innerList.get(2)%>%</p>

					</div>

					<div
						style="overflow: hidden; float: left; border-left: 1px solid #eee; width: 75.6%;">
						<%
							List<List<String>> goalList = GoalMp.get(innerList.get(0));
												for (int k = 0; goalList != null && k < goalList.size(); k++) {
													List<String> goalinnerList = goalList.get(k);
						%>

						<div style="overflow: hidden; float: left; width: 100%;">
							<div
								style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 22.1%;">
								<p style="padding-left:10px">
								<a style="height:0px; width:0px;" href="javascript:void(0);" class="add" onclick="window.location.href ='AddLevelData.action?id=<%=appraisalList.get(0) %>&appsystem=<%=innerList1.get(3) %>&scoreType=<%=innerList1.get(2) %>&UID=<%=innerList.get(0) %>&type=goal';" title="Add">Add</a>
								<a href="javascript:void(0)" class="edit_lvl" onclick="window.location.href ='EditAppraisal.action?id=<%=appraisalList.get(0) %>&appsystem=<%=innerList1.get(3) %>&scoreType=<%=innerList1.get(2) %>&editID=<%=goalinnerList.get(0) %>&type=goal';" title="Edit">Edit</a>
								<%=goalinnerList.get(1)%>
								</p>
							</div>
							<div
								style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 13.3%; text-align: right;">

								<p style="margin: 0px 10px 0px 0px;"><%=goalinnerList.get(2)%>%</p>
							</div>
							<div
								style="overflow: hidden; float: left; border-left: 1px solid #eee; width: 64.4%;">
								<%
									List<List<String>> measureList = measureMp.get(goalinnerList.get(0));
															for (int l = 0; measureList != null && l < measureList.size(); l++) {
																List<String> measureinnerList = measureList.get(l);
								%>


								<div style="overflow: hidden; float: left; width: 100%;">
									<div
										style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 17%;">
											<p style="padding-left:10px">
											<a style="height:0px; width:0px;" href="javascript:void(0);" class="add" onclick="window.location.href ='AddLevelData.action?id=<%=appraisalList.get(0) %>&appsystem=<%=innerList1.get(3) %>&scoreType=<%=innerList1.get(2) %>&UID=<%=goalinnerList.get(0) %>&type=measure';" title="Add">Add</a>
											<a href="javascript:void(0)" class="edit_lvl" onclick="window.location.href ='EditAppraisal.action?id=<%=appraisalList.get(0) %>&appsystem=<%=innerList1.get(3) %>&scoreType=<%=innerList1.get(2) %>&editID=<%=measureinnerList.get(0) %>&type=measure';" title="Edit">Edit</a>
											<%=measureinnerList.get(1)%>
											</p>
									</div>
									<div
										style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 17%; text-align: right;">
										<p style="margin: 0px 10px 0px 0px;"><%=measureinnerList.get(2)%>%</p>
									</div>
									<div style="overflow: hidden; float: left; width: 65.8%;">
										<%
											List<List<String>> questionList = questionMp.get(measureinnerList.get(0));
																		for (int m = 0; questionList != null && m < questionList.size(); m++) {
																			List<String> question1List = questionList.get(m);
										%>
										<div style="overflow: hidden; float: left; width: 100%;">
											<div
												style="overflow: hidden; float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 59.6%;">
													<p style="padding-left:10px">
													<a style="height:0px; width:0px;" href="javascript:void(0);" class="add" onclick="window.location.href ='AddLevelData.action?id=<%=appraisalList.get(0) %>&appsystem=<%=innerList1.get(3) %>&scoreType=<%=innerList1.get(2) %>&UID=<%=measureinnerList.get(0) %>&type=quest';" title="Add">Add</a>
													<a href="javascript:void(0)" class="edit_lvl" onclick="window.location.href ='EditAppraisal.action?id=<%=appraisalList.get(0) %>&appsystem=<%=innerList1.get(3) %>&scoreType=<%=innerList1.get(2) %>&editID=<%=question1List.get(3) %>&type=quest';" title="Edit">Edit</a>
													<%=question1List.get(0)%>
													</p>
											</div>
											<div
												style="overflow: hidden; float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 39.8%; text-align: right;">
												<p style="margin: 0px 10px 0px 0px;"><%=question1List.get(1)%>%</p>
											</div>
										</div>
										<%
											}
										%>
									</div>
								</div>
								<%
									}
								%>
							</div>
						</div>
						</div>
						<%
							}
						%>
			</div>		
				</div>
			
				<%
					}
				%>

			</div>
			</div>
			
			<%
				}
						}else if (uF.parseToInt(innerList1.get(3)) == 4){
							
			%>

			<div style="overflow: hidden; float: left; border: 1px solid #eee; width: 100%;  text-align: left; margin-bottom:10px;">
			<div class="past heading_dash" style="text-align:left;padding-left:50px; height:auto;">
			<%=innerList1.get(1)%>
			</div>

			<div class="content1">
			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 100%;">
				
					<table class="tb_style" style="width: 100%; float: left;">
						<tr>
							<th width="90%" align="center">KRA</th>
							<th>Weightage</th>
							<!-- <th>Description</th> -->
						</tr>
						<%
						Iterator<String> it = hmKRA1.keySet().iterator();
						int count=0;
						while(it.hasNext()){
							String key = it.next();
							List<List<String>> outerList = hmKRA1.get(key);
							System.out.println("outerList===>"+outerList);
							for(int aa=0;outerList!=null && aa<outerList.size();aa++){
								List<String> innerList=outerList.get(aa);
						%>
						<tr>
							<td><%=innerList.get(7)%></td>							
							<td style="text-align: right"><%=innerList.get(9)%>%</td>
						</tr>

						<%
							}
						}
						%>
					</table>
				
			</div>
			</div>
			</div>

			<%
				
						}else if (uF.parseToInt(innerList1.get(3)) == 3 || uF.parseToInt(innerList1.get(3)) == 5){
							
							%>

							<div style="overflow: hidden; float: left; border: 1px solid #eee; width: 100%;  text-align: left; margin-bottom:10px;">
							<div class="past heading_dash" style="text-align:left;padding-left:50px; height:auto;">
							<%=innerList1.get(1)%>
							</div>

							<div class="content1">
							<div
								style="overflow: hidden; float: left; border: 1px solid #eee; width: 100%;">
								
									<table class="tb_style" style="width: 100%; float: left;">
										<tr>
											<th width="90%" align="center">Target</th>
											<th>Measures</th>
										</tr>
										<%
										Iterator<String> it = hmKRA.keySet().iterator();
										int count=0;
										while(it.hasNext()){
											String key = it.next();
											List<List<String>> outerList = hmKRA.get(key);
											System.out.println("outerList===>"+outerList);
											for(int aa=0;outerList!=null && aa<outerList.size();aa++){
												List<String> innerList=outerList.get(aa);
										%>
										<tr>
											<td><%=innerList.get(7)%></td>							
											<td style="text-align: right"><%=hmMesures.get(innerList.get(1))%></td>
										</tr>

										<%
											}
										}
										%>
									</table>
								
							</div>
							</div>
							</div>

							<%
								
										}
					}
			%>

	</s:form>
</div>
