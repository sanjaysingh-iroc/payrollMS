<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<%
    String fromPage = (String) request.getAttribute("fromPage");
	Map<String, Integer> hmChart1 = (Map<String, Integer>) request.getAttribute("hmchart1");
	Map<String, Integer> hmChart2 = (Map<String, Integer>) request.getAttribute("hmchart2");
%>
<style>
.skill_div{
width:auto; 
} 
.site-stats li{
width: 12%;
}
</style> 
<%if(fromPage == null || fromPage.equals("") || fromPage.equalsIgnoreCase("null")) { %>
		<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<% }%>
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
	$("body").on('click','#closeButton1',function(){
		$(".modal-dialog1").removeAttr('style');
		$("#modal-body1").height(400);
		$("#modalInfo1").hide();
    });
	$("#location").multiselect().multiselectfilter();
	$("#designation").multiselect().multiselectfilter();
	$("#appliSourceName").multiselect().multiselectfilter();
});

function jobDetails(recruitid,fromPage) {

	var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 $(".modal-title").html('Job Status');
	 if($(window).width() >= 900){
		 $(".modal-dialog").width(900);
	 }
	 $.ajax({
			url : "ReportJobProfilePopUp.action?view=jobreport&recruitID="+recruitid+'&fromPage='+fromPage  ,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
}

function addpanel(recruitID,fromPage) {
	
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
			url :"AddCriteriaPanelPopUp.action?type=popup&recruitID="+recruitID+'&fromPage='+fromPage+'&formName=JR' ,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
 }
 
 
function addRequestWOWorkFlow(from) {
	
	var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 $(".modal-title").html('Requirement');
	 $.ajax({
			url : 'RequirementRequestWithoutWorkflow.action?frmPage='+from,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
}


function addRequest(from) {

	var id=document.getElementById("requirementRequestDiv");
	if(id){
		id.parentNode.removeChild(id);
	}
	
	var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 $(".modal-title").html('Requirement');
	 if($(window).width() >= 900){
		 $(".modal-dialog").width(900);
	 }
	 $.ajax({
			url : "RequirementRequest.action?frmPage="+from,
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
	
	function editRequestWOWorkflow(RID) {
		  
		var dialogEdit = '.modal-body';
		 $(dialogEdit).empty();
		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		 $("#modalInfo").show();
		 $(".modal-title").html('Edit Request');
		 $.ajax({
				url : "RequirementRequestWithoutWorkflow.action?frmPage=JR&recruitmentID="+RID,
				cache : false,
				success : function(data) {
					$(dialogEdit).html(data);
				}
			});
	}
	
	function editRequest(RID) {
		/* if(document.getElementById("f_org"))
			var orgID = document.getElementById("f_org").value;
		if(document.getElementById("location"))
			var wlocID = document.getElementById("location").value;
		if(document.getElementById("designation"))
		var desigID = document.getElementById("designation").value;
		var checkStatus = document.getElementById("checkStatus").value;
		var fdate = document.getElementById("fdate").value;
		var tdate = document.getElementById("tdate").value; 
		
		+"&orgID="+orgID+"&wlocID="+wlocID+"&desigID="+desigID+"&checkStatus="+checkStatus+"&fdate="+fdate+"&tdate="+tdate
		*/
		  
		var dialogEdit = '.modal-body';
		 $(dialogEdit).empty();
		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		 $("#modalInfo").show();
		 $(".modal-title").html('Edit Request');
		 $.ajax({
				url : "RequirementRequest.action?recruitmentID="+RID+"&frmPage=JR",
				cache : false,
				success : function(data) {
					$(dialogEdit).html(data);
				}
			});
	}
	
	
	function loadMoreRecruitment(pageNumber, minLimit,dataType,from) {
		  if(from != "" && from == "WF") {
			  $("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
				$.ajax({
					type : 'POST',
					url: 'JobList.action?dataType='+dataType+'&fromPage='+from+'&pageNumber='+pageNumber+'&minLimit='+minLimit,
					data: $("#"+this.id).serialize(),
					success: function(result){
						$("#divResult").html(result);
			   		}
				});
		  }else {
			  document.frm_joblist_view.pageNumber.value = pageNumber;
			  document.frm_joblist_view.minLimit.value = minLimit;
			  document.frm_joblist_view.submit();
		  }
			
	}
	
	function getJobReportAjax(dataType){
		//alert("service ===>> " + service);
		var fromPage = document.getElementById("fromPage").value;
		$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: 'JobList.action?dataType='+dataType+'&fromPage='+fromPage,
			data: $("#"+this.id).serialize(),
			success: function(result){
				$("#divResult").html(result);
	   		}
		});
	}

	function submitForm(){
		var org = document.getElementById("f_org").value;
		var dataType = document.getElementById("dataType").value;
		var fromPage = document.getElementById("fromPage").value;
		var appliSourceType = document.getElementById("appliSourceType").value;
		var strSearchJob = document.getElementById("strSearchJob").value;
		
		var location = getSelectedValue("location");
		var designation = getSelectedValue("designation");
		var appliSourceName = "";
		if(document.getElementById("appliSourceName")) {
			appliSourceName = getSelectedValue("appliSourceName");
		}
		
		var paramValues = '&strLocation='+location+'&strDesignation='+designation+'&appliSourceType='+appliSourceType+'&strAppliSourceName='+appliSourceName
						+'&fromPage='+fromPage+'&strSearchJob='+strSearchJob;
		
	//	alert("paramValues ===>> " + paramValues);
		$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: 'JobList.action?f_org='+org+'&dataType='+dataType+paramValues,
			cache: false,
			success: function(result){
	        	$("#divResult").html(result);
	   		}
		});
	}

	function getSelectedValue(selectId) {
		var choice = document.getElementById(selectId);
		var exportchoice = "";
		for ( var i = 0, j = 0; i < choice.options.length; i++) {
			if (choice.options[i].selected == true) {
				if (j == 0) {
					exportchoice = choice.options[i].value;
					j++;
				} else {
					exportchoice += "," + choice.options[i].value;
					j++;
				}
			}
		}
		return exportchoice;
	}
	
	function deleteJob(op,recruitId) {
		if(confirm('Are you sure you wish to delete Job?')) {
			$.ajax({
				type :'POST',
				url  :'JobList.action?operation='+op+'&recruitId='+recruitId, 
				cache:false,
				success : function(result) {
					$("#divResult").html(result);
				}
			});
		}
		
	}
</script>

<%if(fromPage == null || fromPage.equals("") || fromPage.equalsIgnoreCase("null")) { %>
<section class="content">
    <div class="row jscroll">
        <section class="col-lg-12 connectedSortable">
 <%} %>
        	<div class="box box-none nav-tabs-custom">
        		<ul class="nav nav-tabs">
				<%
				String strUserType = (String) session.getAttribute("USERTYPE");
				
				UtilityFunctions uF = new UtilityFunctions(); 
				
				String sbData = (String) request.getAttribute("sbData");
				String strSearchJob = (String) request.getAttribute("strSearchJob");
				
				String appliSourceType = (String) request.getAttribute("appliSourceType");
				
				String dataType = (String) request.getAttribute("dataType");
				if(fromPage != null && fromPage.equals("WF")) {
					if(dataType != null && dataType.equals("L")) { %>
					<li class="active"><a href="javascript:void(0)" onclick="getJobReportAjax('L')" data-toggle="tab">Live</a></li>
					<li><a href="javascript:void(0)" onclick="getJobReportAjax('C')" data-toggle="tab">Closed</a></li>
				<% } else if(dataType != null && dataType.equals("C")) { %>
					<li><a href="javascript:void(0)" onclick="getJobReportAjax('L')" data-toggle="tab">Live</a></li>
					<li class="active"><a href="javascript:void(0)" onclick="getJobReportAjax('C')" data-toggle="tab">Closed</a></li>
				<% } 
				} else {
				   if(dataType != null && dataType.equals("L")) { %>
					<li class="active"><a href="javascript:void(0)" onclick="window.location='JobList.action?dataType=L'" data-toggle="tab">Live</a></li>
					<li><a href="javascript:void(0)" onclick="window.location='JobList.action?dataType=C'" data-toggle="tab">Closed</a></li>
				<% } else if(dataType != null && dataType.equals("C")) { %>
					<li><a href="javascript:void(0)" onclick="window.location='JobList.action?dataType=L'" data-toggle="tab">Live</a></li>
					<li class="active"><a href="javascript:void(0)" onclick="window.location='JobList.action?dataType=C'" data-toggle="tab">Closed</a></li>
				<% } 
				}%>	
				</ul> 
                <div class="box-body" style="padding: 5px; overflow-y: auto;min-height: 600px;">
                    <div class="leftbox reportWidth">
				<%
				//List<List<String>> aljobreport = (List<List<String>>) request.getAttribute("job_code_info");
				List<String> recruitmentIDList = (List<String>) request.getAttribute("recruitmentIDList");
				Map<String, List<String>> hmJobReport = (Map<String, List<String>>) request.getAttribute("hmJobReport");
				if(hmJobReport == null) hmJobReport = new HashMap<String, List<String>>();
				
				Map<String, String> hmAppCount = (Map<String, String>)request.getAttribute("hmAppCount");
				
				/* Map<String, String> hmJobStatus = (Map<String, String>)request.getAttribute("hmJobStatus");
				if(hmJobStatus == null) hmJobStatus = new HashMap<String, String>(); */
				
				int totalToday1 = 0;
				int total72hrs1 = 0;
				int totalRequired1 = 0;
				int totalOffered1 = 0;
				int totalShortlisted1 = 0;
				int totalFinalization1 = 0;
				int totalScheduled1 = 0;
				
				//for (int i = 0; i < aljobreport.size(); i++) {
					//System.out.println("recruitmentIDList ===>> " + recruitmentIDList.size());
					
					for (int i = 0; recruitmentIDList != null && !recruitmentIDList.isEmpty() && i < recruitmentIDList.size(); i++) {
					List<String> alinner = (List<String>) hmJobReport.get(recruitmentIDList.get(i));
					if(alinner != null && !alinner.isEmpty()) {
						//System.out.println("alinner 17 ---> "+alinner.get(17));
						totalToday1 += uF.parseToInt(alinner.get(3));
						total72hrs1 += uF.parseToInt(alinner.get(4));
						
						totalRequired1 += uF.parseToInt(alinner.get(5));
						totalOffered1 += uF.parseToInt(alinner.get(8));
	
						totalShortlisted1 += uF.parseToInt(alinner.get(10));
						totalFinalization1 += uF.parseToInt(alinner.get(11));
						totalScheduled1 += uF.parseToInt(alinner.get(14));
				/* 		totalUnderProcess += uF.parseToInt(alinner.get(15)); */
					}
				}
				%>

	<%=uF.showData((String)session.getAttribute(IConstants.MESSAGE),"") %>

<div>
		<div class="filter_div row line_height_1" style="margin-right: 0px;margin-left: 0px;">
			<ul class="site-stats-new">
				<li class="bg_lh"><strong><%=totalToday1 %></strong> <small>Today's Induction</small></li>
				<li class="bg_lh"><strong><%=total72hrs1 %></strong> <small>72 hrs Induction</small></li>
				<li class="bg_lh"><strong><%=totalRequired1 %></strong> <small>Required Offers</small></li>
				<li class="bg_lh"><strong><%=totalOffered1 %></strong> <small>Offered Offers</small></li>
				<li class="bg_lh"><strong><%=totalScheduled1 %></strong> <small>Scheduled Interview</small></li>
				<li class="bg_lh"><strong><%=totalShortlisted1 %></strong> <small>Shortlisted Application</small></li>
				<li class="bg_lh"><strong><%=totalFinalization1 %></strong> <small>Finalization Application</small></li>
			</ul>
		</div>
	</div>
	<br/>
	
	<div class="box box-primary collapsed-box" style="border-top-color: #EBEBEB;">
                
       <div class="box-header with-border">
            <h4 class="box-title" style="font-size: 14px;"><%=(String)request.getAttribute("selectedFilter") %></h4>
              <div class="box-tools pull-right">
                      <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
                      <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                  </div>
              </div>
             
              <div class="box-body" style="padding: 5px; overflow-y: auto;display:none;">
                  <s:form name="frm_joblist_view" action="JobList" theme="simple">
			    	<s:hidden name="dataType" id="dataType" />
			    	<s:hidden name="pageNumber" id="pageNumber" />
			    	<s:hidden name="minLimit" id="minLimit" />
			    	<input type="hidden" name="usrType" id="usrType" value="<%=strUserType %>" />
			    	<input type="hidden" name="fromPage" id="fromPage" value="<%=fromPage %>" />
			    		<div class="row row_without_margin">
							<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
								<i class="fa fa-filter"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px; margin-bottom: 8px;">Organization</p>
									<%if(fromPage != null && fromPage.equals("WF")) { %>
										<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" list="organisationList" key="" 
										headerKey="" headerValue="All Organisations" onchange="submitForm()"/>
									<%} else { %>
										<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" list="organisationList" key="" 
										headerKey="" headerValue="All Organisations" onchange="frm_joblist_view.submit()"/>
									<%} %>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Location</p>
									<s:select theme="simple" name="location" id="location" listKey="wLocationId" listValue="wLocationName" 
									list="workLocationList" key="0"  multiple="true"/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Designation</p>
									<s:select theme="simple" name="designation" id="designation" listKey="desigId" listValue="desigCodeName" 
									list="desigList" key=""  multiple="true"/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Source Type</p>
									<%if(fromPage != null && fromPage.equals("WF")) { %>
										<s:select theme="simple" name="appliSourceType" id="appliSourceType" listKey="sourceTypeId" listValue="sourceTypeName" 
										list="sourceTypeList" key="" headerKey="" headerValue="All" onchange="submitForm()"/>
									<%}else { %>
										<s:select theme="simple" name="appliSourceType" id="appliSourceType" listKey="sourceTypeId" listValue="sourceTypeName" 
										list="sourceTypeList" key="" headerKey="" headerValue="All" onchange="frm_joblist_view.submit();"/>
								    <%} %>
								</div>
								<% if(uF.parseToInt(appliSourceType) != IConstants.SOURCE_WEBSITE) { %>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Source Name</p>
									<s:select theme="simple" name="appliSourceName" id="appliSourceName" listKey="sourceTypeId" listValue="sourceTypeName" 
									list="sourceNameList" key="0" multiple="true"/>
								</div>
								<% } %>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">&nbsp;</p>
									<%if(fromPage != null && fromPage.equals("WF")) { %>
										<input type="button" name="submit" id = "submit" value="Submit" class="btn btn-primary" onclick="submitForm();"/>
									<%}else { %>
										<s:submit value="Submit" name="submit" id = "submit" cssClass="btn btn-primary"/>
									<%} %>
								</div>
							</div>
						</div>
			</s:form>
		</div>
    </div>
	
	
	
		<div class="alignCenter">
			<span>Search :</span>
			<input type="text" id="strSearchJob" class="form-control" name="strSearchJob"  value="<%=uF.showData(strSearchJob,"") %>"/>
			<%if(fromPage != null && fromPage.equals("WF")) { %>
				<input type="button" name="Search" id = "Search" value="Search" class="btn btn-primary" onclick="submitForm();"/>
			<%}else { %>
				<input type="submit" value="Search"  class="btn btn-primary" >
			<%} %>
		</div>
		       
		<script>
			$("#strSearchJob" ).autocomplete({
				source: [ <%=uF.showData(sbData,"") %> ]
			});
		  </script>
	
	<% if (strUserType != null && (strUserType.equalsIgnoreCase(IConstants.MANAGER) || strUserType.equalsIgnoreCase(IConstants.ADMIN)) || strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.RECRUITER)) { %>
		<div class="row row_without_margin margintop20">
			<div class="col-lg-12">
				<a href="javascript:void(0)" onclick="addRequest('<%=fromPage%>')"><input type="button" class="btn btn-primary pull-right" value="Add New Requirement"> </a>
				<% if (strUserType != null && !strUserType.equalsIgnoreCase(IConstants.MANAGER)) { %>
					<a href="javascript:void(0)" onclick="addRequestWOWorkFlow('<%=fromPage%>')"><input type="button" class="btn btn-primary pull-right" value="Add New Requirement W/O Workflow"> </a>
				<% } %>
			</div>
		</div>
	<% } %>


	<div class="attendance clr margintop20">
		<table class="table table-bordered">
			<tbody>
				<tr class="darktable">
					<th rowspan="2">&nbsp;</th>
					<th rowspan="2">Job code <br/> (Job Title)</th>
					<th style="text-align: center;" rowspan="2">Panelist & Rounds</th>
					<th style="width: 75px;text-align: center;" colspan="2">Induction</th>
					<th style="width: 75px;text-align: center;" colspan="4">Offers</th>
					
					<th style="width: 75px;text-align: center;" colspan="2">Interview Status</th>
					<th style="width: 75px;text-align: center;" colspan="4">Application Status</th>
					<th style="text-align: center;" rowspan="2">Actions</th>
				</tr>

				<tr class="darktable">

					<th style="width: 75px">Today</th>
					<th style="width: 75px">72 hrs</th>

					<th style="width: 75px">Required</th>
					<th style="width: 75px">Accepted</th>
					<th style="width: 75px">Rejected</th>
					<th style="width: 75px">Offered</th>
					
					<th style="width: 75px">Scheduling</th>
					<th style="width: 75px">Scheduled</th>

					<th style="width: 75px">Applications</th>
					<th style="width: 75px">Shortlisted</th>
					<th style="width: 75px">Finalization</th>
					<th style="width: 75px">Rejected</th>

				</tr>


				<%
					/* UtilityFunctions uF = new UtilityFunctions();
					List<List<String>> aljobreport = (List<List<String>>) request.getAttribute("job_code_info");
					List alapplicationstatus = (List) request.getAttribute("alapplicationstatus");
 */
					int totalToday = 0;
					int total72hrs = 0;
					int totalRequired = 0;
					int totalAccepted = 0;
					int totalRejected = 0;
					int totalOffered = 0;
					int totalApplications = 0;
					int totalShortlisted = 0;
					int totalFinalization = 0;
					int totalRejectedAppl = 0;
					int totalScheduling = 0;
					int totalScheduled = 0;
					int totalUnderProcess = 0;

					for (int i = 0; recruitmentIDList != null && !recruitmentIDList.isEmpty() && i < recruitmentIDList.size(); i++) {
						List<String> alinner = (List<String>) hmJobReport.get(recruitmentIDList.get(i));
						if(alinner != null && !alinner.isEmpty()) {
						String priorityClass = "";
						if (uF.parseToInt(alinner.get(18)) == 1) {
							priorityClass ="high";
						} else if (uF.parseToInt(alinner.get(18)) == 2) {
							priorityClass ="medium";
						} else {
							priorityClass ="low";
						}
						
						boolean closeFlag = false;
						if(uF.parseToBoolean(alinner.get(17))) {
							closeFlag = true;
						}	
							
						totalToday += uF.parseToInt(alinner.get(3));
						total72hrs += uF.parseToInt(alinner.get(4));

						totalRequired += uF.parseToInt(alinner.get(5));
						totalAccepted += uF.parseToInt(alinner.get(6));
						totalRejected += uF.parseToInt(alinner.get(7));
						totalOffered += uF.parseToInt(alinner.get(8));

						totalApplications += uF.parseToInt(alinner.get(9));
						totalShortlisted += uF.parseToInt(alinner.get(10));
						totalFinalization += uF.parseToInt(alinner.get(11));
						totalRejectedAppl += uF.parseToInt(alinner.get(12));

						totalScheduling += uF.parseToInt(alinner.get(13));
						totalScheduled += uF.parseToInt(alinner.get(14));
				/* 		totalUnderProcess += uF.parseToInt(alinner.get(15)); */
				%>
				<tr class="lighttable">
					<td style="width: 1%;" valign="top">
						<div style="float: left; padding-right: 3px;">
							<%if(alinner.get(17).equalsIgnoreCase("false")) { %>
								<%-- <img border="0" style="width: 16px;" src="<%=request.getContextPath()%>/images1/icons/approved.png">  --%>
								<i class="fa fa-circle" aria-hidden="true" style="color:#54aa0d"></i>
							<% } else { %>
								<%-- <img border="0" style="width: 16px;" src="<%=request.getContextPath()%>/images1/icons/denied.png"> --%>
								<i class="fa fa-circle" aria-hidden="true" style="color:#e22d25"></i>
							<% } %>
						</div>
					</td>
					<td style="width: 14%;" valign="top" class="<%=priorityClass %>">
						<div style="float: left;"> <b><a href="javascript:void(0)" onclick="jobDetails('<%=alinner.get(0)%>','<%=fromPage%>')"><%=alinner.get(1)%></a></b>
						<%if(hmAppCount == null || uF.parseToInt(hmAppCount.get(alinner.get(0)))==0 && alinner.get(17).equalsIgnoreCase("false")) { %>
							<img border="0" src="<%=request.getContextPath()%>/images1/icons/news_icon.gif"/>
						<% } %>
						</div>
						<br/><div style="float: left;">(<%=alinner.get(21)%>)</div>
					</td>
					<td valign="top" rowspan="1" style="width: 15%">
					<%if(alinner.get(2) == null || alinner.get(2).equals("")) {
						if (strUserType != null && (strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.ADMIN))) {
					%>
							<%if(!closeFlag) { %>
								<a href="javascript:void(0)" title="Add Panel" onclick="addpanel('<%=alinner.get(0)%>','<%=fromPage%>')"><i class="fa fa-plus-circle" aria-hidden="true"></i></a>
							<% } %>
						<% } else { %>
							No panel list created
						<% } %>
					<% } else { %>
						<%=alinner.get(2)%>&nbsp;&nbsp;
						<% if (strUserType != null && (strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.ADMIN))) { %>
							<%if(!closeFlag) { %>
								<a href="javascript:void(0)" title="Modify Panel" onclick="addpanel('<%=alinner.get(0)%>','<%=fromPage%>')"><i class="fa fa-plus-circle" aria-hidden="true"></i></a>
							<% } %>
					<%
					}
						} %>
					</td>

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
						<%if(!closeFlag){ %>
							<%if(!uF.parseToBoolean(alinner.get(17))){ %>
								
								<a onclick="closeJob('<%=alinner.get(0) %>','close','<%=fromPage%>');" style="float: left;color:#F02F37;" href="javascript:void(0)" title="Close Job" ><i class="fa fa-times-circle-o" aria-hidden="true"></i></a>
							<% } else { %>
								<a onclick="closeJob('<%=alinner.get(0) %>','view','<%=fromPage%>');" style="float: left;color:#F02F37;" href="javascript:void(0)" title="Close Job Reason"><i class="fa fa-times-circle-o" aria-hidden="true"></i></a>
							<% } %>
							
							<%if(!uF.parseToBoolean(alinner.get(19))){ %>
							      <%if(fromPage != null && fromPage .equalsIgnoreCase("WF")) { %>
							   		<a href="javascript:void(0)" onclick="deleteJob('D','<%=recruitmentIDList.get(i)%>');" style="float: left;color:#F02F37;" class="del"><i class="fa fa-trash" aria-hidden="true"></i></a>
							   <%} else { %>
									<a href="JobList.action?operation=D&recruitId=<%=recruitmentIDList.get(i) %>" style="float: left;color:#F02F37;" class="del" onclick="return confirm('Are you sure you wish to delete Job?')" ><i class="fa fa-trash" aria-hidden="true"></i></a>
								<%} %>
								
							<% } else { %>
								<a href="javascript:void(0)" style="float: left;color:#F02F37;" class="del" onclick="alert('You can not delete this job, Please delete candidates first.');" ><i class="fa fa-trash" aria-hidden="true"></i></a>
							<% } %>
							<% if (strUserType != null && !strUserType.equalsIgnoreCase(IConstants.MANAGER) && !uF.parseToBoolean(alinner.get(17))) { %>
								<%if(uF.parseToInt(alinner.get(20)) > 0) { %>
									<a href="javascript:void(0)" onclick="editRequestWOWorkflow('<%=recruitmentIDList.get(i) %>');"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
								<% } else { %>
									<a href="javascript:void(0)" onclick="editRequest('<%=recruitmentIDList.get(i) %>');"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
								<% } %>
							<% } %>
						<%} %>
					</td>
				</tr>
				<%
					} }
				%>
					<% if (recruitmentIDList == null|| recruitmentIDList.isEmpty() || recruitmentIDList.size() == 0) { %>

				<tr class="lighttable">

					<td colspan="15"><div class="nodata msg">
							<span> No Data Available</span>
						</div>
					</td>
				</tr>
				<% } else { %>

				<tr class="table_result">
					<td style="text-align: center; " colspan="3"><b>Total</b> </td>

					<td style="text-align: right;padding-right:7px"><b><%=totalToday%></b>
					</td>
					<td style="text-align: right;padding-right:7px"><b><%=total72hrs%></b>
					</td>

					<td style="text-align: right;padding-right:7px"><b><%=totalRequired%></b>
					</td>
					<td style="text-align: right;padding-right:7px"><b><%=totalAccepted%></b>
					</td>
					<td style="text-align: right;padding-right:7px"><b><%=totalRejected%></b>
					</td>
					<td style="text-align: right;padding-right:7px"><b><%=totalOffered%></b>
					</td>

					<td style="text-align: right;padding-right:7px"><b><%=totalScheduling%></b>
					</td>
					<td style="text-align: right;padding-right:7px"><b><%=totalScheduled%></b>
					</td>

					<td style="text-align: right;padding-right:7px"><b><%=totalApplications%></b>
					</td>
					<td style="text-align: right;padding-right:7px"><b><%=totalShortlisted%></b>
					</td>
					<td style="text-align: right;padding-right:7px"><b><%=totalFinalization%></b>
					</td>
					<td style="text-align: right;padding-right:7px"><b><%=totalRejectedAppl%></b>
					</td>

				</tr>

               <%} %>

				
			</tbody>
		</table>


	<div style="text-align: center; width: 100%; clear: both;">
		 
		<% 
		String pageCount = (String)request.getAttribute("pageCount");
		int intproCnt = uF.parseToInt(pageCount);
			int pageCnt = 0;
			int minLimit = 0;
			
			for(int i=1; i<=intproCnt; i++) {
					minLimit = pageCnt * 10;
					pageCnt++;
		%>
		<% if(i ==1) {
			String strPgCnt = (String)request.getAttribute("pageNumber");
			String strMinLimit = (String)request.getAttribute("minLimit");
			if(uF.parseToInt(strPgCnt) > 1) {
				 strPgCnt = (uF.parseToInt(strPgCnt)-1) + "";
				 strMinLimit = (uF.parseToInt(strMinLimit)-10) + "";
			}
			if(strMinLimit == null) {
				strMinLimit = "0";
			}
			if(strPgCnt == null) {
				strPgCnt = "1";
			}
		%>
			<span style="color: lightgray;">
			<% if(uF.parseToInt((String)request.getAttribute("pageNumber")) > 1) { %>
			
				<a href="javascript:void(0);" onclick="loadMoreRecruitment('<%=strPgCnt %>','<%=strMinLimit %>','<%=uF.showData((String)request.getAttribute("dataType"), "L") %>','<%=fromPage%>');">
				<%="< Prev" %></a>
			<% } else { %>
				<b><%="< Prev" %></b>
			<% } %>
			</span>
			<span><a href="javascript:void(0);" onclick="loadMoreRecruitment('<%=pageCnt %>','<%=minLimit %>','<%=uF.showData((String)request.getAttribute("dataType"), "L") %>','<%=fromPage%>');"
			<% if(((String)request.getAttribute("pageNumber") == null && pageCnt == 1) || uF.parseToInt((String)request.getAttribute("pageNumber")) == pageCnt) { %>
			style="color: black;"
			<% } %>
			><%=pageCnt %></a></span>
			
			<% if((uF.parseToInt((String)request.getAttribute("pageNumber"))-3) > 1) { %>
				<b>...</b>
			<% } %>
		
		<% } %>
		
		<% if(i > 1 && i < intproCnt) { %>
		<% if(pageCnt >= (uF.parseToInt((String)request.getAttribute("pageNumber"))-2) && pageCnt <= (uF.parseToInt((String)request.getAttribute("pageNumber"))+2)) { %>
			<span><a href="javascript:void(0);" onclick="loadMoreRecruitment('<%=pageCnt %>','<%=minLimit %>','<%=uF.showData((String)request.getAttribute("dataType"), "L") %>','<%=fromPage%>');"
			<% if(((String)request.getAttribute("pageNumber") == null && pageCnt == 1) || uF.parseToInt((String)request.getAttribute("pageNumber")) == pageCnt) { %>
			style="color: black;"
			<% } %>
			><%=pageCnt %></a></span>
		<% } %>
		<% } %>
		
		<% if(i == intproCnt && intproCnt > 1) {
			String strPgCnt = (String)request.getAttribute("pageNumber");
			String strMinLimit = (String)request.getAttribute("minLimit");
			 strPgCnt = (uF.parseToInt(strPgCnt)+1) + "";
			 strMinLimit = (uF.parseToInt(strMinLimit)+10) + "";
			 if(strMinLimit == null) {
				strMinLimit = "0";
			}
			if(strPgCnt == null) {
				strPgCnt = "1";
			}
			%>
			<% if((uF.parseToInt((String)request.getAttribute("pageNumber"))+3) < intproCnt) { %>
				<b>...</b>
			<% } %>
		
			<span><a href="javascript:void(0);" onclick="loadMoreRecruitment('<%=pageCnt %>','<%=minLimit %>','<%=uF.showData((String)request.getAttribute("dataType"), "L") %>','<%=fromPage%>');"
			<% if(uF.parseToInt((String)request.getAttribute("pageNumber")) == pageCnt) { %>
			style="color: black;"
			<% } %>
			><%=pageCnt %></a></span>
			<span style="color: lightgray;">
			<% if(uF.parseToInt((String)request.getAttribute("pageNumber")) < pageCnt) { %>
				<a href="javascript:void(0);" onclick="loadMoreRecruitment('<%=strPgCnt %>','<%=strMinLimit %>','<%=uF.showData((String)request.getAttribute("dataType"), "L") %>','<%=fromPage%>');"><%="Next >" %></a>
			<% } else { %>
				<b><%="Next >" %></b>
			<% } %>
			</span>
		<% } %>
		<%} %>
		</div>
		<div class="custom-legends">
		  <div class="custom-legend approved">
		    <div class="legend-info">Live Jobs</div>
		  </div>
		  <div class="custom-legend denied">
		    <div class="legend-info">Closed Jobs</div>
		  </div>
		</div>
		<div style="display: none">
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
							<div id="applicationFinalstats" style="height: 200px; width: 100%"></div>
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
							<div id="offerFinalStats" style="height: 200px; width: 100%"></div>
						</div>
					</div>
				</div>
			</div>
		</div>

	</div>

</div>
                </div>
            </div>
   <%if(fromPage == null || fromPage.equals("") || fromPage.equalsIgnoreCase("null")) { %>
        </section>
    </div>
</section>	
<%} %>

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
<div class="modal" id="modalInfo1" role="dialog">
    <div class="modal-dialog modal-dialog1">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title modal-title1"></h4>
            </div>
            <div class="modal-body" id="modal-body1" style="height:400px;overflow-y:auto;padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton1" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>
