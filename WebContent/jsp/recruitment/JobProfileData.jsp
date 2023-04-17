<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<style>
.skill_div{
	width:auto; 
}

.site-stats li{
	width: 12%;
}

</style>
 
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script type="text/javascript" charset="utf-8">
$(document).ready(function(){
	$("body").on('click','#closeButton',function(){
		$(".modal-dialog").removeAttr('style');
		$(".modal-body").height(400);
		$("#modalInfo").hide();
    });
	$("body").on('click','.close',function(){
		$(".modal-dialog").removeAttr('style');
		$(".modal-body").height(400);
		$("#modalInfo").hide();
	});
});


function addpanel(recruitID) {
	
	var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 $(".modal-title").html('Round & Panel Information');
	 if($(window).width() >= 900){
		$(".modal-dialog").width(900);
	}
	 $.ajax({
		//url : "ApplyLeavePopUp.action", 
		url :"AddCriteriaPanelPopUp.action?type=popup&recruitID="+recruitID+'&formName=JR' ,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
 }
 
 
	function closeJob(recruitmentId, type) {
		//alert("openQuestionBank id "+ id)
		var pageTitle = 'Close Job';
		if(type=='view') {
			pageTitle = 'Close Job Reason';
		}
		var dialogEdit = '.modal-body';
		 $(dialogEdit).empty();
		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		 $("#modalInfo").show();
		 $(".modal-title").html(''+pageTitle);
		 $.ajax({
			url : "CloseJob.action?recruitmentId="+recruitmentId+"&fromPage=RecruitmentDashboard",
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}
	
	</script>

				
	<div class="box-body" style="padding: 5px; overflow-y: auto;min-height: 600px;">
		<%
			String strUserType = (String) session.getAttribute("USERTYPE");
			UtilityFunctions uF = new UtilityFunctions(); 
			//List<List<String>> aljobreport = (List<List<String>>) request.getAttribute("job_code_info");
			Map<String, List<String>> hmJobReport = (Map<String, List<String>>) request.getAttribute("hmJobReport");
			if(hmJobReport == null) hmJobReport = new HashMap<String, List<String>>();
			
			String recruitId = (String) request.getAttribute("recruitId");
			Map<String, String> hmAppCount = (Map<String, String>)request.getAttribute("hmAppCount");
		%>

		<div class="attendance clr margintop20">
		<%
			List<String> alinner = (List<String>) hmJobReport.get(recruitId);
			if(alinner != null && !alinner.isEmpty()) {
			boolean closeFlag = false;
			if(uF.parseToBoolean(alinner.get(17))) {
				closeFlag = true;
			}	
		%>
		<table class="table table-bordered">
			<tbody>
				<tr class="darktable">
					<th style="text-align: center;">Panelist & Rounds</th>
					<th style="text-align: center;">Actions</th>
				</tr>

				<tr class="lighttable">
					<td valign="top" rowspan="1" style="width: 15%">
					<%if(alinner.get(2) == null || alinner.get(2).equals("")) {
						if (strUserType != null && (strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.ADMIN))) {
					%>
						<%if(!closeFlag) { %>
							<a href="javascript:void(0)" title="Add Panel" onclick="addpanel('<%=alinner.get(0)%>')"><i class="fa fa-plus-circle" aria-hidden="true"></i></a>
						<% } %>
					<% } else { %>
						No panel list created
					<% } %>
					<% } else { %>
						<%=alinner.get(2)%>&nbsp;&nbsp;
						<% if (strUserType != null && (strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.ADMIN))) { %>
							<%if(!closeFlag) { %>
								<a href="javascript:void(0)" title="Modify Panel" onclick="addpanel('<%=alinner.get(0)%>')"><i class="fa fa-plus-circle" aria-hidden="true"></i></a>
							<% } %>
						<% } 
					} %>
					</td>

					<td style="width: 5%;" valign="top">
						<%if(!closeFlag){ %>
							<%if(!uF.parseToBoolean(alinner.get(17))){ %>
								<a onclick="closeJob('<%=alinner.get(0) %>','close');" style="float: left;color:#F02F37;" href="javascript:void(0)" title="Close Job" ><i class="fa fa-times-circle-o" aria-hidden="true"></i></a>
							<% } else { %>
								<a onclick="closeJob('<%=alinner.get(0) %>','view');" style="float: left;color:#F02F37;" href="javascript:void(0)" title="Close Job Reason"><i class="fa fa-times-circle-o" aria-hidden="true"></i></a>
							<% } %>
							
							<%if(!uF.parseToBoolean(alinner.get(19))){ %>
								<a href="JobList.action?operation=D&recruitId=<%=recruitId %>" style="float: left;color:#F02F37;" class="del" onclick="return confirm('Are you sure you wish to delete Job?')" ><i class="fa fa-trash" aria-hidden="true"></i></a>
							<% } else { %>
								<a href="javascript:void(0)" style="float: left;color:#F02F37;" class="del" onclick="alert('You can not delete this job, Please delete candidates first.');" ><i class="fa fa-trash" aria-hidden="true"></i></a>
							<% } %>
							<% if (strUserType != null && !strUserType.equalsIgnoreCase(IConstants.MANAGER) && !uF.parseToBoolean(alinner.get(17))) { %>
								<%if(uF.parseToInt(alinner.get(20)) > 0) { %>
									<a href="javascript:void(0)" onclick="editRequestWOWorkflow('<%=recruitId %>');"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
								<% } else { %>
									<a href="javascript:void(0)" onclick="editRequest('<%=recruitId %>');"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
								<% } %>
							<% } %>
						<%} %>
					</td>
				</tr>
			</tbody>
		</table>
		
		
		<table class="table table-bordered">
			<tbody>
				<tr class="darktable">
					<th style="width: 75px;text-align: center;" colspan="4">Application Status</th>
					<th style="width: 75px;text-align: center;" colspan="2">Interview Status</th>
				</tr>

				<tr class="darktable">

					<th style="width: 75px">Applications</th>
					<th style="width: 75px">Shortlisted</th>
					<th style="width: 75px">Finalization</th>
					<th style="width: 75px">Rejected</th>
					
					<th style="width: 75px">Scheduling</th>
					<th style="width: 75px">Scheduled</th>
				</tr>

				<tr class="lighttable">

					<td style="width: 5%;" valign="top">
						<div class="blueColor" style="width: 100%; height: 100%; background-color: #90EE90; text-align: right;">
							<% if (strUserType != null && (!strUserType.equalsIgnoreCase(IConstants.RECRUITER))) { %>
								<%if(!closeFlag) { %>
									<a style="padding-right:5px" href="Applications.action?strDashboardRequest=<%=alinner.get(0)%>" ><%=alinner.get(9)%></a>
								<% } else { %>
									<%=alinner.get(9)%>
								<% } %>
							<% } else { %> 
								<span style="padding-right:5px"><%=alinner.get(9)%></span> 
							<% } %>
						</div>
					</td>
					<td style="width: 5%;" valign="top">
						<div class="blueColor" style="width: 100%; height: 100%; background-color: #90EE90; text-align: right;">
							<% if (strUserType != null && (!strUserType.equalsIgnoreCase(IConstants.RECRUITER))) { %>
								<%if(!closeFlag) { %>
									<a style="padding-right:5px" href="Applications.action?strDashboardRequest=<%=alinner.get(0)%>" ><%=alinner.get(10)%></a>
								<% } else { %>
									<%=alinner.get(10)%>
								<% } %>
							<% } else { %> 
								<span style="padding-right:5px"><%=alinner.get(10)%></span> 
							<% } %>
						</div>
					</td>
					<td style="width: 5%;" valign="top">
						<div class="blueColor" style="width: 100%; height: 100%; background-color: #90EE90; text-align: right;">
							<% if (strUserType != null && (!strUserType.equalsIgnoreCase(IConstants.RECRUITER))) { %>
								<%if(!closeFlag) { %>
									<a style="padding-right:5px" href="Applications.action?strDashboardRequest=<%=alinner.get(0)%>" ><%=alinner.get(11)%></a>
								<% }else { %>
									<%=alinner.get(11)%>
								<% } %>
							<% } else { %> 
								<span style="padding-right:5px"><%=alinner.get(11)%></span> 
							<% } %>
						</div>
					</td>
					<td style="width: 5%;" valign="top">
						<div class="blueColor" style="width: 100%; height: 100%; background-color: #90EE90; text-align: right;">
							<% if (strUserType != null && (!strUserType.equalsIgnoreCase(IConstants.RECRUITER))) { %>
								<%if(!closeFlag) { %>
									<a style="padding-right:5px" href="Applications.action?strDashboardRequest=<%=alinner.get(0)%>" ><%=alinner.get(12)%></a>
								<% } else { %>
									<%=alinner.get(12)%>
								<% } %>
							<% } else { %> 
								<span style="padding-right:5px"><%=alinner.get(12)%></span> 
							<% } %>
						</div>
					</td>
    
    				<td style="width: 5%;" valign="top">
						<div class="blueColor" style="width: 100%; height: 100%; background-color: #00FFFF; text-align: right;">
							<span style="padding-right:5px"><%=alinner.get(13)%></span>
						</div>
					</td>
					<td style="width: 5%;" valign="top">
						<div class="blueColor" style="width: 100%; height: 100%; background-color: #00FFFF; text-align: right;">
							<span style="padding-right:5px"><%=alinner.get(14)%></span>
						</div>
					</td>
					
				</tr>
			</tbody>
		</table>
		
		
		<table class="table table-bordered">
			<tbody>
				<tr class="darktable">
					<th style="width: 75px;text-align: center;" colspan="2">Induction</th>
					<th style="width: 75px;text-align: center;" colspan="4">Offers</th>
				</tr>

				<tr class="darktable">

					<th style="width: 75px">Today</th>
					<th style="width: 75px">72 hrs</th>

					<th style="width: 75px">Required</th>
					<th style="width: 75px">Accepted</th>
					<th style="width: 75px">Rejected</th>
					<th style="width: 75px">Offered</th>
					
				</tr>

				<tr class="lighttable">

					<td style="width:4.5%;" valign="top">
						<div style="text-align: right;">
							<% if (strUserType != null &&  (!strUserType.equalsIgnoreCase(IConstants.RECRUITER))) { %>
								<%if(!closeFlag) { %>
									<a style="padding-right:5px" href="Induction.action?strDashboardRequest=<%=alinner.get(0)%>" ><%=alinner.get(3)%></a>
								<% } else { %>
									<%=alinner.get(3)%>
								<% } %>
							<% } else { %> 
								<span style="padding-right:5px"><%=alinner.get(3)%></span> 
							<% } %>
						</div>
					</td>
					<td style="width:4.5%;" valign="top">
						<div style="text-align: right;">
							<% if (strUserType != null && (!strUserType.equalsIgnoreCase(IConstants.RECRUITER))) { %>
								<%if(!closeFlag) { %>
									<a style="padding-right:5px" href="Induction.action?strDashboardRequest=<%=alinner.get(0)%>" ><%=alinner.get(4)%></a>
								<% } else { %>
									<%=alinner.get(4)%>
								<% } %>
							<% } else { %> 
							<span style="padding-right:5px"><%=alinner.get(4)%></span> 
							<% } %>
						</div>
					</td>
 
					<td style="width: 5%;" valign="top">
						<div class="blueColor" style="width: 100%; height: 100%; background-color: #a9cfff; text-align: right;">
							<% if (strUserType != null && (!strUserType.equalsIgnoreCase(IConstants.RECRUITER))) { %>
								<%if(!closeFlag) { %>
									<a style="padding-right:5px" href="Offers.action?strDashboardRequest=<%=alinner.get(0)%>" ><%=alinner.get(5)%></a>
								<% } else { %>
									<%=alinner.get(5)%>
								<% } %>
							<% } else { %> 
								<span style="padding-right:5px"><%=alinner.get(5)%></span> 
							<% } %>
						</div>
					</td>
					<td style="width: 5%;" valign="top">
						<div class="blueColor" style="width: 100%; height: 100%; background-color: #a9cfff; text-align: right;">
							<% if (strUserType != null && (!strUserType.equalsIgnoreCase(IConstants.RECRUITER))) { %>
								<%if(!closeFlag) { %>
									<a style="padding-right:5px" href="Offers.action?strDashboardRequest=<%=alinner.get(0)%>" ><%=alinner.get(6)%></a>
								<% } else {%>
									<%=alinner.get(6)%>
								<% } %>
							<% } else { %> 
								<span style="padding-right:5px"><%=alinner.get(6)%></span> 
							<% } %>
						</div>
					</td>
					<td style="width: 5%;" valign="top">
						<div class="blueColor" style="width: 100%; height: 100%; background-color: #a9cfff; text-align: right;">
							<% if (strUserType != null && (!strUserType.equalsIgnoreCase(IConstants.RECRUITER))) { %>
								<%if(!closeFlag) { %>
									<a style="padding-right:5px" href="Offers.action?strDashboardRequest=<%=alinner.get(0)%>" ><%=alinner.get(7)%></a>
								<%} else { %>
									<%=alinner.get(7)%>
								<% } %>
							<% } else { %> 
								<span style="padding-right:5px"><%=alinner.get(7)%></span> 
							<% } %>
						</div>
					</td>
					<td style="width: 5%;" valign="top">
						<div class="blueColor" style="width: 100%; height: 100%; background-color: #a9cfff; text-align: right;">
							<% if (strUserType != null && (!strUserType.equalsIgnoreCase(IConstants.RECRUITER))) { %>
								<%if(!closeFlag){ %>
									<a style="padding-right:5px" href="Offers.action?strDashboardRequest=<%=alinner.get(0)%>" ><%=alinner.get(8)%></a>
								<%} else { %>
									<%=alinner.get(8)%>
								<%} %>
							<% } else { %> 
								<span style="padding-right:5px"><%=alinner.get(8)%></span> 
							<% } %>
						</div>
					</td>
				</tr>
			</tbody>
		</table>
			<% } %>
			<% if(uF.parseToInt(recruitId) == 0) { %>
			<table class="table table-bordered">
				<tbody>
					<tr class="lighttable">
						<td colspan="15"><div class="nodata msg"><span> No data available.</span></div></td>
					</tr>
				</tbody>
			</table>	
			<%} %>

			

	</div>

</div>


<div class="modal" id="modalInfo" role="dialog">
    <div class="modal-dialog">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title"></h4>
            </div>
            <div class="modal-body" style="height:400px;overflow-y:auto;padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>

