<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>


<%
	String strSessionUserType = (String) session.getAttribute(IConstants.USERTYPE);
%>
 
<script type="text/javascript" charset="utf-8">
	
function openAppraisalPreview(id) {
	//alert("openQuestionBank id "+ id)
	//removeLoadingDiv('the_div');
	 var dialogEdit = '#appraisalPreviewDiv';
	 dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
			$(dialogEdit).dialog(
				{
				autoOpen : false,
				bgiframe : true,
				resizable : false,
				height : 800,
				width : 1100,
				modal : true,
				title : 'Review Preview',
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
	}
	
	
	
/* function closeReview(reviewId, type) {
	//alert("openQuestionBank id "+ id)
	var pageTitle = 'Close Review';
	if(type=='view') {
		pageTitle = 'Close Review Reason';
	}
	removeLoadingDiv('the_div');
	 var dialogEdit = '#closeReviewDiv';
	 dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
			$(dialogEdit).dialog(
				{
				autoOpen : false,
				bgiframe : true,
				resizable : false,
				height : 250,
				width : 360,
				modal : true,
				title : ''+pageTitle,
				open : function() {
					var xhr = $.ajax({
						url : "CloseReview.action?reviewId="+reviewId,
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
	
	</script>

<%-- <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Reviews" name="title" />
</jsp:include> --%>


<%-- <div style="float: left; width: 100%; margin-bottom: -7px; margin-left: 10px;">
		<%
		String dataType = (String) request.getAttribute("dataType");
		if(dataType != null && dataType.equals("L")) { %>
			<a href="Reviews.action?callFrom=Dash" class="all_dull">All</a>
			<a href="Reviews.action?callFrom=Dash&dataType=L" class="live">Live</a>
			<a href="Reviews.action?callFrom=Dash&dataType=C" class="close_dull">Closed</a> 
		<% } else if(dataType != null && dataType.equals("C")) { %>
			<a href="Reviews.action?callFrom=Dash" class="all_dull">All</a>
			<a href="Reviews.action?callFrom=Dash&dataType=L" class="live_dull">Live</a>
			<a href="Reviews.action?callFrom=Dash&dataType=C" class="close">Closed</a>
		<% } else { %>
			<a href="Reviews.action?callFrom=Dash" class="all">All</a>
			<a href="Reviews.action?callFrom=Dash&dataType=L" class="live_dull">Live</a>
			<a href="Reviews.action?callFrom=Dash&dataType=C" class="close_dull">Closed</a>
		<% } %>	
	</div> --%>

<div class="leftbox reportWidth" style="font-size: 12px;">


	<%
		UtilityFunctions uF = new UtilityFunctions();
		List<List<String>> allExitFeedbackForms = (List<List<String>>) request.getAttribute("allExitFeedbackForms");
		
		/* int totalAppraisal1 = 0;
		int totalEmps1 = 0;
		int totalAprPending1 = 0;
		int totalAprUnderReview1 = 0;
		int totalAprFinalised1 = 0;
		
		for (int i = 0; i < allExitFeedbackForms.size(); i++) {
			List<String> alinner = (List<String>) allExitFeedbackForms.get(i);

			totalAppraisal1 = allExitFeedbackForms.size();
			totalEmps1 += uF.parseToInt(alinner.get(7));
			
			totalAprPending1 += uF.parseToInt(alinner.get(8));
			totalAprUnderReview1 += uF.parseToInt(alinner.get(9));
			totalAprFinalised1 += uF.parseToInt(alinner.get(10)); 
		} */
	%>

<%=uF.showData((String)session.getAttribute(IConstants.MESSAGE),"") %>

	<%-- <% if (strSessionUserType != null &&  (strSessionUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strSessionUserType.equalsIgnoreCase(IConstants.ADMIN))) {%> --%>
	
		<%-- <div class="filter_div">
			<div class="skill_div" style="width:12%">
                    <p class="sk_value" style="text-align:center"><%=totalAppraisal1 %></p>
                    <p class="sk_name" style="text-align:center">Reviews</p>
               </div>
               <div class="skill_div" style="width:12%">
                    <p class="sk_value" style="text-align:center"><%=totalEmps1 %></p>
                    <p class="sk_name" style="text-align:center">Reviewee</p>
               </div>
               <div class="skill_div" style="width:12%">
                    <p class="sk_value" style="text-align:center"><%=totalAprPending1 %></p>
                    <p class="sk_name" style="text-align:center">Pending</p>
               </div> 
               <div class="skill_div" style="width:14%">
                    <p class="sk_value" style="text-align:center"><%=totalAprUnderReview1 %></p>
                    <p class="sk_name" style="text-align:center">Under Review</p>
               </div>  
               <div class="skill_div" style="width:12%">
                    <p class="sk_value" style="text-align:center"><%=totalAprFinalised1 %></p>
                    <p class="sk_name" style="text-align:center">Finalised</p>
               </div>
		</div> --%>
	
	<%-- <% } %> --%>

	<div class="attendance">

		<!-- <table class="display tb_style" >  -->
	<%-- <%if(uF.parseToBoolean((String)request.getAttribute(IConstants.ADD_ACCESS))){ %> --%>
	<%-- <% if (strSessionUserType != null &&  (strSessionUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strSessionUserType.equalsIgnoreCase(IConstants.ADMIN))) {%>
	<div style="float: right; margin-bottom: 10px">
		<a href="CreateAppraisal.action"><input type="button" class="input_button" value="Add New Review"> </a>
	</div>
	<%} %> --%>
		<table width="100%" cellspacing="0" cellpadding="2" align="left" class="table table-bordered">
			<tbody>
				<tr class="darktable">
					<th style="width: 80%; text-align: center;" colspan="2">Forms Name</th>
					<!-- <th style="width: 7%;text-align: center;" rowspan="2">Deadline</th>
					<th style="width: 7%;text-align: center;" rowspan="2">Reviewees</th>
					<th style="width: 30%;text-align: center;" colspan="3">Reviews Status</th> -->
					<th style="width: 20%; text-align: center;">Actions</th>
				</tr>

				<!-- <tr class="darktable">
					<th style="width: 10%">Pending</th>
					<th style="width: 10%">Under Review</th>
					<th style="width: 10%">Finalised</th>
				</tr> -->

				<%
					//int totalExitFeedbackForms = 0;
					/* int totalEmps = 0;
					int totalAprPending = 0;
					int totalAprUnderReview = 0;
					int totalAprFinalised = 0; */
					
					for (int i = 0; i < allExitFeedbackForms.size(); i++) {
						List<String> alinner = (List<String>) allExitFeedbackForms.get(i);
						//totalExitFeedbackForms = allExitFeedbackForms.size();
						/* totalEmps += uF.parseToInt(alinner.get(7));
						totalAprPending += uF.parseToInt(alinner.get(8));

						totalAprUnderReview += uF.parseToInt(alinner.get(9));
						totalAprFinalised += uF.parseToInt(alinner.get(10)); */
						//totalAppraisal += uF.parseToInt(alinner.get(7)); 
				
				%>
				<tr class="lighttable">
					<td width="2%">
						<b><%=alinner.get(1)%></b>
						<p style="padding-left:20px;"><%=alinner.get(2)%>, <%=alinner.get(3)%>, <%=alinner.get(4)%></p>
					</td>
					
					<%-- <td align="center"><%=alinner.get(5)%></td>
					<td class="blueColor" align="center"><strong> <%=alinner.get(7)%></strong></td>

					<td align="center"><%=alinner.get(8)%></td>
					<td align="center"><%=alinner.get(9)%></td>
					<td align="center"><%=alinner.get(10)%></td> --%>
					<td><%=alinner.get(6)%></td>

				</tr>
				<% } %>
				
				<% if (allExitFeedbackForms.size() == 0) { %>

				<tr class="lighttable">
				<td colspan="3"><div class="nodata msg" style="width: 96%;"> <span> No Data Available. </span> </div> </td>
				</tr>
				<% } %>
			</tbody>
		</table>


		<%-- <div style="display: none">
			<div id="center">
				<div class="KPI">

					<p class="past close_div">Applications</p>
					<div class="content1">

						<div style="padding: 15px;">

							<b> Accepted </b> :
							<%=hmChart1.get("acceptedAppl")%>
							<br> <b> Rejected </b> :
							<%=hmChart1.get("rejectedAppl")%>
							<br> <b> Underprocess </b> :
							<%=hmChart1.get("underprocessAppl")%>

						</div>
						<div class="holder">
									
								             ['Accepted Applications', <%= hmChart1.get("acceptedAppl")%>],
		             ['Rejected Applications', <%= hmChart1.get("rejectedAppl")%>],
		             ['Application UnderProcess', <%= hmChart1.get("underprocessAppl")%>]

							<div id="applicationFinalstats"
								style="height: 200px; width: 100%"></div>
							<!-- <div class="viewmore"><a href="ClockEntries.action?T=T">Know more..</a></div> -->

						</div>
					</div>
				</div>
			</div>

			<div id="right">
				<div class="KPI">

					<p class="past close_div">Candidate Joining</p>
					<div class="content1">



						<div style="padding: 15px;">
							<b> Accepted</b> :
							<%=hmChart2.get("acceptedCand")%>
							<br> <b> Rejected</b> :
							<%=hmChart2.get("rejectedCand")%>
							<br> <b> Underprocess</b> :
							<%=hmChart2.get("underprocessCand")%>

						</div>
						<div class="holder">


							['Offer Rejected', <%= hmChart2.get("rejectedCand")%>],
		             ['Offer Accepted ', <%= hmChart2.get("acceptedCand")%>],
		             ['Offer Under Process', <%= hmChart2.get("underprocessCand")%>]	
					
							<div id="offerFinalStats" style="height: 200px; width: 100%"></div>

						</div>
					</div>
				</div>
			</div>
		</div> --%>


	</div>


	<div style="float: left; width: 100%">
		<div class="fieldset">
			<fieldset>
				<legend>Status</legend>
				
				<div> <%-- <img style="padding: 5px 5px 0 5px;" border="0" src="<%=request.getContextPath()%>/images1/icons/pullout.png"> --%>
				<i class="fa fa-circle" aria-hidden="true" style="color:#ea9900"></i>
				Published </div>
				
				 <div> <%-- <img style="padding: 5px 5px 0 5px;" border="0" src="<%=request.getContextPath()%>/images1/icons/pending.png">Unpublished  --%>
				 <i class="fa fa-circle" aria-hidden="true" style="color:#b71cc5"></i>Unpublished 
				 </div>
				
			</fieldset>
		</div>
	</div>
	
</div>

<div id="appraisalPreviewDiv"></div>
<div id="closeReviewDiv"></div>