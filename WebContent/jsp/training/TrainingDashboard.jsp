 

<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<script type="text/javascript" charset="utf-8">
	
	jQuery(document).ready(function() {
		jQuery(".content1").hide();
		//toggle the componenet with class msg_body
		jQuery(".heading").click(function() {
			jQuery(this).next(".content1").slideToggle(500);
			$(this).toggleClass("heading_dash");
		});
	});
	
	jQuery(document).ready(function() {
		jQuery(".contentOngoing").show();
		//toggle the componenet with class msg_body
		jQuery(".heading").click(function() {
			jQuery(this).next(".contentOngoing").slideToggle(500);
			$(this).toggleClass("heading_dash");
		});
	});


	function showTrainees(planId) {

		var dialogEdit = '#Learner';
		dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>')
				.appendTo('body');
		$(dialogEdit).dialog({
			autoOpen : false,
			bgiframe : true,
			resizable : false,
			height : 500,
			width : 700,
			modal : true,
			title : 'Learners Information',
			open : function() {
				var xhr = $.ajax({
					url : "LearnersInfoDashboard.action?planId=" + planId,
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
	<jsp:param value="Training Dashboard" name="title" />
</jsp:include>

<div class="leftbox reportWidth">



				<%
					List<List<String>> alOngoingTraining = (List) request.getAttribute("alOngoingTraining");
					List<List<String>> alScheduledTraining = (List) request.getAttribute("alScheduledTraining");
					List<List<String>> alFinishedTraining = (List) request.getAttribute("alFinishedTraining");
				%>



		<div class="dashboard_linksholder" style="width:98%;margin-bottom:5px;">
		
		<p class="heading" style="padding-left: 60px;line-height:30px;">
		<b>Ongoing  </b>
		</p>
		
			<div class="contentOngoing">
				
				<div class="attendance">
				
				<table width="100%" cellspacing="0" cellpadding="2" align="left" style="margin:0px;">
					
						
						<tr class="darktable">

							<td style="text-align: left;" >Training Title</td>
							<td style="text-align: left;" >Training type</td>
							<td style="text-align: left;" >Start Date</td>
							<td style="text-align: left;" >End Date</td>
				
							<td style="text-align: left;">Learners</td>
							<td style="text-align: left;">Certificates given</td>

						</tr>



						<%
							for (int i = 0; i < alOngoingTraining.size(); i++) {
								List<String> alinner = alOngoingTraining.get(i);
						%>

						<tr class="lighttable">

							<td><%=alinner.get(1)%></td>
							<td><%=alinner.get(2)%></td>
							<td><%=alinner.get(5)%></td>
							<td><%=alinner.get(6)%></td>


							<td style="text-align: right"><a href="javascript:void(0)"
								onclick="showTrainees('<%=alinner.get(0)%>')"><%=alinner.get(3)%></a>
							</td>
							<td style="text-align: right"><%=alinner.get(4)%></td>


						</tr>
						<%
							}
						%>
						<%
							if (alOngoingTraining.size() == 0) {
						%>

						<tr class="lighttable">
							<td colspan="6"><div class="nodata msg">
									<span>No Training within this selection</span>
								</div></td>
						</tr>

						<%
							}
						%>
					

				</table>
			</div>
			</div>
		</div>
		
		<div class="clr"></div>

		<div class="dashboard_linksholder" style="width:98%;margin-top:10px;">
		
		<p class="heading" style="padding-left: 60px;line-height:30px;">
		<b>Scheduled </b>
		</p>
		
			<div class="content1 ">
				
				<div class="attendance">
				
				<table width="100%" cellspacing="0" cellpadding="2" align="left" style="margin:0px;">
					
						
						<tr class="darktable">

							<td style="text-align: left;" >Training Title</td>
							<td style="text-align: left;" >Training type</td>
							<td style="text-align: left;" >Start Date</td>
							<td style="text-align: left;" >End Date</td>

							<td style="text-align: left;">Learners</td>
							<td style="text-align: left;">Certificates given</td>

						</tr>


						<%
							for (int i = 0; i < alScheduledTraining.size(); i++) {
								List<String> alinner = alScheduledTraining.get(i);
						%>

						<tr class="lighttable">

							<td><%=alinner.get(1)%></td>
							<td><%=alinner.get(2)%></td>
							<td><%=alinner.get(5)%></td>
							<td><%=alinner.get(6)%></td>


							<td style="text-align: right"><a href="javascript:void(0)"
								onclick="showTrainees('<%=alinner.get(0)%>')"><%=alinner.get(3)%></a>
							</td>
							<td style="text-align: right"><%=alinner.get(4)%></td>

						</tr>
						<%
							}
						%>
						<%
							if (alScheduledTraining.size() == 0) {
						%>

						<tr class="lighttable">
							<td colspan="6"><div class="nodata msg">
									<span>No Training within this selection</span>
								</div></td>
						</tr>

						<%
							}
						%>
					

				</table>

			</div></div>
		</div>




</div>
