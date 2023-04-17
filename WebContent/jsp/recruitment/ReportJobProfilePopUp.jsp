<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>

<%
    UtilityFunctions uF = new UtilityFunctions(); 
    
    String recruitId = (String) request.getAttribute("recruitId");
    String fromPage = (String) request.getAttribute("fromPage");
    Map<String, String> approveMp = (Map) request.getAttribute("approveMp");
    //System.out.println("approveMp ===> "+ approveMp);
    Map<String, String> finalisedMp = (Map) request.getAttribute("finalisedMp");
    Map<String, String> denyMp = (Map) request.getAttribute("denyMp");
    
    Map<String, String> hmCandAccepted = (Map<String, String>) request.getAttribute("hmCandAccepted");
    Map<String, String> hmCandRejected = (Map<String, String>) request.getAttribute("hmCandRejected");
    
    Map<String, String> hmCandRequired = (Map<String, String>) request.getAttribute("hmCandRequired");
    
    int existing = (Integer) request.getAttribute("existing");
    int planned = (Integer) request.getAttribute("planned");
    
    List<String> jobProfileList = (List<String>) request.getAttribute("jobProfileList");
    
    if(uF.parseToInt(recruitId) > 0) {
    %>
<style>
    .txtlbl{
    color: #777777;
    font-family: verdana,arial,helvetica,sans-serif;
    font-size: 11px;
    font-style: normal;
    font-weight: 600;
    width: 190px
    }
    .intvw-status {
	font-size: 25px;
	color: gray;
	text-align: center;
	}
	.site-stats li {
	width: 42%;
	}
	
	/* ===start parvez on 03-08-2021=== */
	#textlabel{
	white-space:pre-line;
	}
	/* ===end parvez on 03-08-2021=== */
</style>


<g:compress>
    <script type="text/javascript">
    	
	    $(function() {
	    	
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
	    	
	    	//console.log("loaded");
	    	//console.log($(".col-md-9 .nav-tabs-custom .nav-tabs").find("li"));
	    	//console.log($(".col-md-9").find('a:contains(Details)').parent());
	    	$(".col-md-9 .nav-tabs-custom .nav-tabs").find("li").removeClass("active");
	    	$(".col-md-9").find('a:contains(Details)').parent().addClass("active");
	    	//alert("(:P)");

        	var chart;
        	var chart1;
        	var chart2;
        
        if(document.getElementById('ApplicationStats')!=null){
           chart = new Highcharts.Chart({
           chart: {
                  renderTo: 'ApplicationStats',
                  defaultSeriesType: 'column',
                	 plotBorderWidth: 1
               },
               credits: {
                   enabled: false
               }, 
               title: {
                  text: ' '
               },
               
               xAxis: {
                  categories: ['Applications']
               },      
               
               yAxis: {
             	  
             	  lineWidth: 2,	//y axis itself
                   title: {
                      text: ' '
         	        }
               },
               credits: {
                	enabled: false
         	   },
         	   title: {
         	 	  		text : '',
         	     		floating: true
         	   },
               plotOptions: {
             	  bar: {
                   pointPadding: 0.2,
                   borderWidth: 0
                }
               },           
               
               series: [{
                   name: 'Application Accepted',
                   data: [<%=uF.parseToInt(uF.showData(approveMp.get(recruitId), "0"))%>]
                }, {
                   name: 'Application Rejected',
                   data: [<%=uF.parseToInt(uF.showData(denyMp.get(recruitId), "0"))%>]
                },{
                   name: 'Finalised',
                   data: [<%=uF.parseToInt(uF.showData(finalisedMp.get(recruitId), "0"))%>]
                }]
        	});
           
        }
        
        
        if(document.getElementById('CandidateStats')!=null){
        
           chart1 = new Highcharts.Chart({
              
           	      chart: {
           	          renderTo: 'CandidateStats',
           	          defaultSeriesType: 'column',
           	        	 plotBorderWidth: 1
           	       },
           	       credits: {
           	           enabled: false
           	       }, 
           	       title: {
           	          text: ' '
           	       },
           	       
           	       xAxis: {
           	          categories: ['Candidates']
           	       },      
           	       
           	       yAxis: {
           	     	  
           	     	  lineWidth: 2,	//y axis itself
           	           title: {
           	              text: ' '
           	 	        }
           	       },
           	       credits: {
           	        	enabled: false
           	 	   },
           	 	   title: {
           	 	 	  		text : '',
           	 	     		floating: true
           	 	   },
           	       plotOptions: {
           	     	  bar: {
           	           pointPadding: 0.2,
           	           borderWidth: 0
           	        }
           	       },           
           	       
           	       series: [{
           	           name: 'Shortlisted',
           	           data: [<%=uF.parseToInt(uF.showData(approveMp.get(recruitId), "0"))%>]
           	     
           	        }, {
           	           name: 'Offered',
           	           data: [<%=uF.parseToInt(uF.showData(finalisedMp.get(recruitId), "0"))%>]
           	        },{
           	           name: 'Accepted',
           	           data: [<%=uF.parseToInt(uF.showData(hmCandAccepted.get(recruitId), "0"))%>]
           	        }]
           });
        
        }
        
        
           chart2 = new Highcharts.Chart({
        	   chart: {
           	          renderTo: 'EmployeeStats',
           	          defaultSeriesType: 'column',
           	        	 plotBorderWidth: 1
           	       },
           	       credits: {
           	           enabled: false
           	       }, 
           	       title: {
           	          text: ' '
           	       },
           	       
           	       xAxis: {
           	          categories: ['Workforce']
           	       },      
           	       
           	       yAxis: {
           	     	  
           	     	  lineWidth: 2,	//y axis itself
           	           title: {
           	              text: ' '
           	 	        }
           	       },
           	       credits: {
           	        	enabled: false
           	 	   },
           	 	   title: {
           	 	 	  		text : '',
           	 	     		floating: true
           	 	   },
           	       plotOptions: {
           	     	  bar: {
           	           pointPadding: 0.2,
           	           borderWidth: 0
           	        }
           	       },           
           	       
           	       series: [{
           	           name: 'Planned',
           	           data: [<%=planned%>]
           	     
           	        }, {
           	           name: 'Existing',
           	           data: [<%=existing%>]
           	        },{
           	           name: 'Requested',
           	           data: [<%=uF.parseToInt(uF.showData(jobProfileList.get(4), "0"))%>]
           	        }]
           });
        });
        
        
	    function alignResumeShortlistWorkflowMember(recruitId, formName) {
			var dialogEdit = '.modal-body';
			$(dialogEdit).empty();
			$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$('.modal-title').html('Align Resume Shortlisting Workflow');
			$("#modalInfo").show();
			$.ajax({
				url :"AlignResumeShortlistWorkflowMember.action?recruitmentID="+recruitId+'&formName='+formName,
				cache : false,
				success : function(data) {
					$(dialogEdit).html(data);
				}
		    });
	    
		}
	    
        function addpanel(recruitId,fromPage) {
        	if(fromPage == "" || fromPage == "null" || fromPage == "NULL") {
        		fromPage = "JR";
        	}
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
       			url :'AddCriteriaPanelPopUp.action?type=popup&recruitId='+recruitId+'&formName='+fromPage ,
       			cache : false,
       			success : function(data) {
       				$(dialogEdit).html(data);
       			}
       		});
         }
        
        
        function closeJob(recruitmentId, type,fromPage) {
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
    				url : "CloseJob.action?recruitmentId="+recruitmentId+"&from="+fromPage+"&frmPage=RecruitmentDashboard",
    				cache : false,
    				success : function(data) {
    					$(dialogEdit).html(data);
    				}
    			});
    		}
    	
    	function editRequestWOWorkflow(RID,fromPage) {
    		if(fromPage == "" || fromPage == "null" || fromPage == "NULL") {
				fromPage = "JR";
    		}
    		var dialogEdit = '.modal-body';
    		$(dialogEdit).empty();
    		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    		$("#modalInfo").show();
    		$(".modal-title").html('Edit Request');
    		$.ajax({
    				url : "RequirementRequestWithoutWorkflow.action?frmPage="+fromPage+"&recruitmentID="+RID,
    				cache : false,
    				success : function(data) {
    					$(dialogEdit).html(data);
    				}
    			});
    	}
    	
    	function editRequest(RID,fromPage) {
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
    		 if(fromPage == "" || fromPage == "null" || fromPage == "NULL") {
   			  fromPage = "JR";
   		  }
    		var dialogEdit = '.modal-body';
    		 $(dialogEdit).empty();
    		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    		 $("#modalInfo").show();
    		 $(".modal-title").html('Edit Request');
    		 $.ajax({
   				url : "RequirementRequest.action?frmPage="+fromPage+"&recruitmentID="+RID,
   				cache : false,
   				success : function(data) {
   					$(dialogEdit).html(data);
   				}
   			});
    	}
    	
    	function deleteJob(op,recruitId,fromPage) {
    		if(confirm('Are you sure you wish to delete Job?')) {
    			$.ajax({
    				type :'POST',
    				url  :'JobList.action?operation='+op+'&recruitId='+recruitId+'&fromPage='+fromPage, 
    				cache:false/* ,
    				success : function(result) {
    					$("#divWFResult").html(result);
    				} */
    			});
    			
    			$.ajax({
    				url: 'RecruitmentDashboard.action?fromPage='+fromPage,
    				cache: true,
    				success: function(result){
    					$("#divWFResult").html(result);
    		   		}
    			});
    		}
    		
    	}
    	
    	
    	
   /* ===start parvez date: 17-01-2022=== */ 	
    	function reOpenJob(recruitmentId,fromPage) {
    		
    		var pageTitle = 'Re-Open Job';
    		
    		var dialogEdit = '.modal-body';
    		 $(dialogEdit).empty();
    		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    		 $("#modalInfo").show();
    		 $(".modal-title").html(''+pageTitle);
    		 $.ajax({
    				url : "CloseJob.action?recruitmentId="+recruitmentId+"&from="+fromPage+"&fromPage=RecruitmentDashboard&dataType=reopen",
    				cache : false,
    				success : function(data) {
    					$(dialogEdit).html(data);
    				}
    			});
    	}
   /* ===end parvez date: 17-01-2022=== */
    	
    </script>
    
</g:compress>


	<%
	    String view = (String) request.getAttribute("view");
	    StringBuilder sbApproveDeny = new StringBuilder();
	//===start parvez date: 17-01-2022===
		CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
		boolean isEnableHiringInCloseJd = CF.getFeatureManagementStatus(request, uF, IConstants.F_ENABLE_HIRING_PROCEDURE_IN_CLOSED_JOB_REQUIREMENT);
	//===end parvez date: 17-01-2022===
	%>
    
<div  id="popupAjaxLoad row">

	<s:if test="view=='jobreport'">
		<div class="col-lg-12 col-md-12 col-sm-12">
			<%
				String strUserType = (String) session.getAttribute("USERTYPE");
				Map<String, List<String>> hmJobReport = (Map<String, List<String>>) request.getAttribute("hmJobReport");
				if(hmJobReport == null) hmJobReport = new HashMap<String, List<String>>();
				
				//String recruitId = (String) request.getAttribute("recruitId");
				Map<String, String> hmAppCount = (Map<String, String>)request.getAttribute("hmAppCount");
			%>
			<div class="attendance clr ">
			<%
				List<String> alinner = (List<String>) hmJobReport.get(recruitId);
				if(alinner != null && !alinner.isEmpty()) {
				boolean closeFlag = false;
				if(uF.parseToBoolean(alinner.get(17))) {
					closeFlag = true;
				}	
				
				String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"+ "0123456789" + "abcdefghijklmnopqrstuvxyz";
				int n=25;
				
				StringBuilder sb = new StringBuilder(n); 
		        for (int a=0; a<n; a++) {
		            int index = (int)(AlphaNumericString.length() * Math.random()); 
		            sb.append(AlphaNumericString.charAt(index)); 
		        } 
			%>
			
				<table class="table table-bordered">
					<tbody>
						<tr class="darktable">
							<td style="text-align: left;"><b>Candidate shortlisting workflow:</b>
							<% if (strUserType != null && (strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.RECRUITER))) { %>
								<a href="javascript:void(0)" title="Align Workflow" onclick="alignResumeShortlistWorkflowMember('<%=recruitId%>','<%=fromPage%>')"><i class="fa fa-plus-circle" aria-hidden="true"></i></a>
							<% } %>
							</td>
							<td style="text-align: right;">Permalink: <a href="JobOpportunities.action?RecrtNm=<%=sb.toString() %>&strRecruitId=<%=alinner.get(0) %>"><%=jobProfileList.get(39)%></a></td>
						</tr>
						<tr class="darktable">
							<th>Panelist & Rounds</th>
							<th>Actions</th>
						</tr>
		
						<tr class="lighttable">
							<td valign="top" rowspan="1" style="width: 15%">
							<%if(alinner.get(2) == null || alinner.get(2).equals("")) {
								if (strUserType != null && (strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.RECRUITER))) {
							System.out.println("RJPPU.jsp/443---");
							%>
							
					<!-- ===start parvez date: 17-01-2022=== -->		
								<%if(!closeFlag) { %>
								<%-- <%if(!closeFlag || isEnableHiringInCloseJd) { %> --%>
					<!-- ===end parvez date: 17-01-2022=== -->			
									<a href="javascript:void(0)" title="Add Panel" onclick="addpanel('<%=alinner.get(0)%>','<%=fromPage%>')"><i class="fa fa-plus-circle" aria-hidden="true"></i></a>
								<% } %>
							<% } else { %>
								No panel list created
							<% } %>
							<% } else { %>
								<%=alinner.get(2)%>&nbsp;&nbsp;
								<% if (strUserType != null && (strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.RECRUITER))) { %>
						<!-- ===start parvez date: 17-01-2022=== -->			
									<%-- <%if(!closeFlag) { %> --%>
									<%if(!closeFlag || isEnableHiringInCloseJd) { %>
						<!-- ===end parvez date: 17-01-2022=== -->			
										<a href="javascript:void(0)" title="Modify Panel" onclick="addpanel('<%=alinner.get(0)%>','<%=fromPage%>')"><i class="fa fa-plus-circle" aria-hidden="true"></i></a>
									<% } %>
								<% } 
							} %>
							</td>
		
							<td style="width: 5%;" valign="top">
								<% if (strUserType != null && (strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.RECRUITER))) { %>
									<%if(!closeFlag){ %>
										<%if(!uF.parseToBoolean(alinner.get(17))){ %>
											<a onclick="closeJob('<%=alinner.get(0) %>','close','<%=fromPage%>');" style="float: left;color:#F02F37;" href="javascript:void(0)" title="Close Job" ><i class="fa fa-times-circle-o" aria-hidden="true"></i></a>
										<% } else { %>
											<a onclick="closeJob('<%=alinner.get(0) %>','view','<%=fromPage%>');" style="float: left;color:#F02F37;" href="javascript:void(0)" title="Close Job Reason"><i class="fa fa-times-circle-o" aria-hidden="true"></i></a>
										<% } %>
										
										<%if(!uF.parseToBoolean(alinner.get(19))){ %>
										   <%if(fromPage != null && fromPage .equalsIgnoreCase("WF")) { %>
										   		<a href="javascript:void(0)" onclick="deleteJob('D','<%=recruitId %>','<%=fromPage%>');" style="float: left;color:#F02F37;" class="del"><i class="fa fa-trash" aria-hidden="true"></i></a>
										   <%} else { %>
												<a href="JobList.action?operation=D&recruitId=<%=recruitId %>" style="float: left;color:#F02F37;" class="del" onclick="return confirm('Are you sure you wish to delete Job?')" ><i class="fa fa-trash" aria-hidden="true"></i></a>
											<%} %>
										<% } else { %>
											<a href="javascript:void(0)" style="float: left;color:#F02F37;" class="del" onclick="alert('You can not delete this job, Please delete candidates first.');" ><i class="fa fa-trash" aria-hidden="true"></i></a>
										<% } %>
										<% if (strUserType != null && !strUserType.equalsIgnoreCase(IConstants.MANAGER) && !uF.parseToBoolean(alinner.get(17))) { %>
											<%if(uF.parseToInt(alinner.get(20)) > 0) { %>
												<a href="javascript:void(0)" onclick="editRequestWOWorkflow('<%=recruitId %>','<%=fromPage%>');"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
											<% } else { %>
												<a href="javascript:void(0)" onclick="editRequest('<%=recruitId %>','<%=fromPage%>');"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
											<% } %>
										<% } %>
									<%} %>
							<!-- ===start parvez date: 18-01-2022=== -->		
									<% if(closeFlag){ %>
										<a onclick="reOpenJob('<%=alinner.get(0) %>','<%=fromPage%>');" style="float: left;" href="javascript:void(0)" title="Re-Open Job" >Re-Open Requirement</a>
									<% } %>
							<!-- ===end parvez date: 18-01-2022=== -->		
								<% } %>
							</td>
						</tr>
					</tbody>
				</table>
				
				<div class="row row_without_margin">
					<div class="col-lg-6 col-md-6 col-sm-12 paddingleft0">
						<div class="box box-default">
			                <div class="box-header with-border" data-widget="collapse-full">
			                    <h3 class="box-title">Induction</h3> 
			                </div>
			                <div class="box-body" style="padding: 5px; overflow-y: auto; max-height: 250px;">
			                    <div style="width:100%;">
									<ul class="site-stats">
										<a href="javascript:void(0)" onclick="getJobProfileDataPage('Induction', '', 'On-boards');">
										<li class="bg_lh">
											<!-- <i class="fa fa-calendar-o" aria-hidden="true"></i> -->
											<strong>
													<% if (strUserType != null &&  (!strUserType.equalsIgnoreCase(IConstants.RECRUITER))) { %>
											<!-- ===start parvez date: 17-01-2022=== -->			
														<% if(!closeFlag) { %>
														<%-- <% if(!closeFlag || isEnableHiringInCloseJd) { %> --%>
											<!-- ===end parvez date: 17-01-2022=== -->			
															<%=alinner.get(3)%>
														<% } else { %>
															<%=alinner.get(3)%>
														<% } %>
													<% } else { %> 
														<%=alinner.get(3)%>
													<% } %>
											</strong> 
											<small>Today</small>
										</li>
										</a>
										<a href="javascript:void(0)" onclick="getJobProfileDataPage('Induction', '', 'On-boards');">
											<li class="bg_lh">
												<!-- <i class="fa fa-clock-o" aria-hidden="true"></i> -->
												<strong>
													<% if (strUserType != null && (!strUserType.equalsIgnoreCase(IConstants.RECRUITER))) { %>
											<!-- ===start parvez date: 17-01-2022=== -->			
														<%if(!closeFlag) { %>
														<%-- <%if(!closeFlag || isEnableHiringInCloseJd) { %> --%>
											<!-- ===end parvez date: 17-01-2022=== -->			
															<%=alinner.get(4)%>
														<% } else { %>
															<%=alinner.get(4)%>
														<% } %>
													<% } else { %> 
													<%=alinner.get(4)%>
													<% } %>
												</strong> <small>72 hrs</small>
											</li>
										</a>
									</ul>
		                    	</div>
			                </div>
			            </div>
					</div>
					<div class="col-lg-6 col-md-6 col-sm-12 paddingright0">
						<div class="intvw-status">
		                    Please schedule <span class="intvw-status" style="font-family:Digital;"><%=alinner.get(13)%></span> candidates<br/>
		                    <span class="intvw-status" style="font-family:Digital;"><%=alinner.get(14)%></span> candidates scheduled
	                    </div>
					</div>
				</div>
				
				<div class="row row_without_margin">
					<div class="col-lg-6 col-md-6 col-sm-12 paddingleft0">
						<div id="container1"></div>
						<script>
						$(document).ready(function () {
						    Highcharts.chart('container1', {
						        chart: {
						            plotBackgroundColor: null,
						            plotBorderWidth: null,
						            plotShadow: false,
						            type: 'pie'
						        },
						        title: {
						            text: 'Offers'
						        },
						        tooltip: {
						            pointFormat: '<b>{point.name}</b>: {point.y}'
						        },
						        plotOptions: {
						            pie: {
						                allowPointSelect: true,
						                cursor: 'pointer',
						                dataLabels: {
						                    enabled: false
						                },
						                showInLegend: true
						            }
						        },
						        series: [{
						        	innerSize: '50%',
						            name: 'Brands',
						            colorByPoint: true,
						            data: [ {
						                name: 'Required',
						                y: <%=alinner.get(5)%>
						            }, {
						                name: 'Offered',
						                y: <%=alinner.get(8)%>
						            }, {
						                name: 'Accepted',
						                y: <%=alinner.get(6)%>,
						                sliced: true,
						                selected: true
						            }, {
						                name: 'Rejected',
						                y: <%=alinner.get(7)%>
						            }]
						        }]
						    });
						});
						</script>
					</div>
					<div class="col-lg-6 col-md-6 col-sm-12 paddingright0">
						<div id="container2"></div>
						<script>
						$(document).ready(function () {
						    Highcharts.chart('container2', {
						        chart: {
						            plotBackgroundColor: null,
						            plotBorderWidth: null,
						            plotShadow: false,
						            type: 'pie',
						            options3d: {
						                enabled: true,
						                alpha: 45,
						                beta: 0
						            }
						        },
						        title: {
						            text: 'Applications'
						        },
						        tooltip: {
						            pointFormat: '<b>{point.name}</b>: {point.y}'
						        },
						        plotOptions: {
						            pie: {
						                allowPointSelect: true,
						                cursor: 'pointer',
						                depth: 35,
						                dataLabels: {
						                    enabled: false
						                },
						                showInLegend: true
						            }
						        },
						        series: [{
						            name: 'Brands',
						            colorByPoint: true,
						            data: [ {
						                name: 'New Applications',
						                y: <%=alinner.get(22)%>
						            }, {
						                name: 'Shortlisted',
						                y: <%=alinner.get(10)%>
						            }, {
						                name: 'Finalization',
						                y: <%=alinner.get(11)%>,
						                sliced: true,
						                selected: true
						            }, {
						                name: 'Rejected',
						                y: <%=alinner.get(12)%>
						            }]
						        }]
						    });
						});
						</script>
					</div>
				</div>
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
	</s:if>


	<br/>
	<div class="clr margintop20"></div>
    <div class="col-lg-6 col-md-6 col-sm-12">
        <div class="box box-primary">
			<div class="box-header with-border">
				<h3 class="box-title">Job Description and Specification</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
					<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body" style="padding: 5px; overflow-y: auto;">
		        <table border="0" class="table table_no_border">
		            <tr>
		                <td class="txtlabel alignRight"><b>Job Title<br/>(Job Code):</b></td>
		                <td><b><%=jobProfileList.get(39)%></b> <br/>(<%=jobProfileList.get(6)%>)</td>
		            </tr>
		            <tr>
		                <td class="txtlabel alignRight"><b>Organisation:</b></td>
		                <td><%=jobProfileList.get(17)%></td>
		            </tr>
		            <tr>
		                <td class="txtlabel alignRight"><b>Work Location:</b></td>
		                <td><%=jobProfileList.get(3)%></td>
		            </tr>
		            <tr>
		                <td class="txtlabel alignRight"><b>Level:</b></td>
		                <td><%=jobProfileList.get(16)%></td>
		            </tr>
		            <tr>
		                <td class="txtlabel alignRight"><b>Designation:</b></td>
		                <td><%=jobProfileList.get(1)%></td>
		            </tr>
		            <tr>
		                <td class="txtlabel alignRight"><b> Grade:</b></td>
		                <td><%=jobProfileList.get(2)%></td>
		            </tr>
		            <tr>
		                <td class="txtlabel alignRight"><b>Priority:</b></td>
		                <td><%=jobProfileList.get(20)%></td>
		            </tr>
		            <tr>
		                <td class="txtlabel alignRight"><b>Essential Skills:</b></td>
		                <td><%=jobProfileList.get(23)%></td>
		            </tr>
		            <tr>
		                <td class="txtlabel alignRight"><b>Desirable Skills:</b></td>
		                <td><%=jobProfileList.get(18)%></td>
		            </tr>
		            <tr>
		                <td class="txtlabel alignRight"><b>Ideal Candidate:</b></td>
		                <!-- ===start parvez on 03-08-2021=== -->
		                <td><div id="textlabel"><%=jobProfileList.get(19)%></div></td>
		                <!-- ===end parvez on 03-08-2021=== -->
		            </tr>
		            <tr>
		                <td class="txtlabel alignRight"><b>No. of Position(s): </b></td>
		                <td><%=jobProfileList.get(4)%></td>
		            </tr>
		            <tr>
		                <td class="txtlabel alignRight"><b>Type of Employment: </b></td>
		                <td><%=jobProfileList.get(33)%></td>
		            </tr>
		            <%
		                if(jobProfileList.get(34) != null && !jobProfileList.get(34).equals("") && (jobProfileList.get(34).equals("2") || jobProfileList.get(34).equals("3"))) { %>
		            <tr>
		                <td class="txtlabel alignRight">If Temporary/Casual,please give justification & period required for:</td>
		                <td><%=jobProfileList.get(30)%></td>
		            </tr>
		            <% } %>
		            <tr>
		                <td class="txtlabel alignRight"><b>Gender: </b></td>
		                <td><%=jobProfileList.get(31) %></td>
		            </tr>
		            <tr>
		                <td class="txtlabel alignRight"><b>Age: </b></td>
		                <td><%=jobProfileList.get(25) %> Years</td>
		            </tr>
		            <tr>
		                <td class="txtlabel alignRight"><b>Min. CTC: </b></td>
		                <td><%=jobProfileList.get(42) %></td>
		            </tr>
		            <tr>
		                <td class="txtlabel alignRight"><b>Max. CTC: </b></td>
		                <td><%=jobProfileList.get(43) %></td>
		            </tr>
		            <tr>
		                <td class="txtlabel alignRight"><b>Vacancy: </b></td>
		                <td><%=jobProfileList.get(32)%></td>
		            </tr>
		            <%if(jobProfileList.get(26) != null && !jobProfileList.get(26).equals("") && jobProfileList.get(26).equals("1")) { %>
		            <tr>
		                <td class="txtlabel alignRight">Give Justification:</td>
		                <!-- ===start parvez on 03-08-2021=== -->
		                <td><div id="textlabel"><%=jobProfileList.get(27)%></div></td>
		                <!-- ===end parvez on 03-08-2021=== -->
		            </tr>
		            <% } %>
		            <%if(jobProfileList.get(26) != null && !jobProfileList.get(26).equals("") && jobProfileList.get(26).equals("0")) { %>
		            <tr>
		                <td class="txtlabel alignRight">Name of person to be replaced with:</td>
		                <td><%=jobProfileList.get(28)%></td>
		            </tr>
		            <% } %>
		            <tr>
		                <td class="txtlabel alignRight">Reporting to:</td>
		                <td><%=jobProfileList.get(29)%></td>
		            </tr>
		            <tr>
		                <td class="txtlabel alignRight">Effective Date:</td>
		                <td><%=jobProfileList.get(37)%></td>
		            </tr>
		            <tr>
		                <td class="txtlabel alignRight">Target Deadline:</td>
		                <td><%=jobProfileList.get(38)%></td>
		            </tr>
		            <tr>
		                <td class="txtlabel alignRight">Business Benefits:</td>
		                <!-- ===start parvez on 03-08-2021=== -->
		                <td><div id="textlabel"><%=jobProfileList.get(36)%></div></td>
		                <!-- ===end parvez on 03-08-2021=== -->
		            </tr>
		            <tr>
		                <td class="txtlabel alignRight" valign="top">Hiring Manager/ Recruiter:</td>
		                <td><%=jobProfileList.get(40)%></td>
		            </tr>
		            <tr>
		                <td class="txtlabel alignRight">Customer Name:</td>
		                <td><%=jobProfileList.get(41)%></td>
		            </tr>
		            <tr>
		                <td class="txtlabel alignRight"><b>Min. Experience:</b></td>
		                <td>
		                    <%if(uF.parseToInt(jobProfileList.get(8)) == 0 && uF.parseToInt(jobProfileList.get(9)) == 0){ %>
		                    No Experience Required.
		                    <%}else{ %>
		                    <%=uF.parseToInt(jobProfileList.get(8))%>Years and <%=uF.parseToInt(jobProfileList.get(9))%>Months
		                    <%} %>
		                </td>
		            </tr>
		            <tr>
		                <td class="txtlabel alignRight"><b>Max. Experience:</b></td>
		                <td>
		                    <%if(uF.parseToInt(jobProfileList.get(10)) == 0 && uF.parseToInt(jobProfileList.get(11)) == 0){ %>
		                    No Experience Required.
		                    <%}else{ %>
		                    <%=uF.parseToInt(jobProfileList.get(10))%>Years and <%=uF.parseToInt(jobProfileList.get(11))%>Months
		                    <%} %>
		                </td>
		            </tr>
		            <tr>
		                <td class="txtlabel alignRight"><b>Essential Qualification:</b></td>
		                <td><%=uF.showData(jobProfileList.get(21), "No Qualification Specified")%>
		                </td>
		            </tr>
		            <tr>
		                <td class="txtlabel alignRight"><b>Desirable Qualification:</b></td>
		                <td><%=uF.showData(jobProfileList.get(12), "No Qualification Specified")%>
		                </td>
		            </tr>
		            <tr>
		                <td class="txtlabel alignRight"><b>Alternate Qualification:</b></td>
		                <td><%=uF.showData(jobProfileList.get(22), "No Qualification Specified")%>
		                </td>
		            </tr>
		            <tr>
		                <td colspan="2">
		                    <div class="txtlbl">Job Description:</div>
		                    <!-- ===start parvez on 03-08-2021=== -->
		                    <div id="textlabel"><%= uF.showData(jobProfileList.get(7),"Not Added Yet")%></div>
		                    <!-- ===end parvez on 03-08-2021=== -->
		                </td>
		            </tr>
		            <tr>
		                <td colspan="2">
		                    <div class="txtlbl">Candidate Profile:</div>
		                    <%= uF.showData(jobProfileList.get(13),"Not Added Yet")%>
		                </td>
		            </tr>
		            <tr>
		                <td colspan="2">
		                    <div class="txtlbl">Additional Information:</div>
		                    <%= uF.showData(jobProfileList.get(14),"Not Added Yet")%>
		                </td>
		            </tr>
		        </table>
			</div>
		</div>
    </div>
    <div class="col-lg-6 col-md-6 col-sm-12">
        <s:if test="view=='jobreport'">
            <div class="KPI">
                <div class="box box-primary">
                    <div class="box-header with-border">
                        <h3 class="box-title">Candidate Status</h3>
                        <div class="box-tools pull-right">
                            <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
                            <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                        </div>
                    </div>
                    <!-- /.box-header -->
                    <div class="box-body" style="padding: 5px; overflow-y: auto;">
                        <div class="content1">
                            <div style="padding: 15px;">
                                <%-- <br> <b> Rejected</b>:<%=uF.parseToInt(uF.showData(hmCandRejected.get(recruitid), "0"))%> --%>
                                <b> Shortlisted</b>: <span><%=uF.parseToInt(uF.showData(approveMp.get(recruitId), "0"))%></span><br>
                                <b> Offered</b>: <span><%=uF.parseToInt(uF.showData(finalisedMp.get(recruitId), "0"))%></span><br>
                                <b> Accepted</b>: <span><%=uF.parseToInt(uF.showData(hmCandAccepted.get(recruitId), "0"))%></span>
                            </div>
                            <div class="holder">
                                <div id="CandidateStats" style="height: 200px; width: 100%">
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </s:if>
        <s:elseif test="view=='openjobreport' || view=='jobreport'">
            <div class="KPI">
                <div class="box box-primary">
                    <div class="box-header with-border">
                        <h3 class="box-title">Applications</h3>
                        <div class="box-tools pull-right">
                            <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
                            <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                        </div>
                    </div>
                    <!-- /.box-header -->
                    <div class="box-body" style="padding: 5px; overflow-y: auto;">
                        <div class="content1">
                            <div style="padding: 15px;">
                                <b> Application Accepted</b>: <span><%=uF.parseToInt(uF.showData(approveMp.get(recruitId), "0"))%></span>
                                <br> <b>Application Rejected</b>: <span><%=uF.parseToInt(uF.showData(denyMp.get(recruitId), "0"))%></span>
                                <br> <b>Finalised</b>: <span><%=uF.parseToInt(uF.showData(finalisedMp.get(recruitId), "0"))%></span>
                            </div>
                            <div class="holder">
                                <div id="ApplicationStats" style="height: 200px; width: 100%"></div>
                            </div>
                        </div>
                    </div>
                    <!-- /.box-body -->
                </div>
            </div>
        </s:elseif>
        <div class="KPI">
            <div class="box box-primary">
                <div class="box-header with-border">
                    <h3 class="box-title">Workforce Status</h3>
                    <div class="box-tools pull-right">
                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                    </div>
                </div>
                <!-- /.box-header -->
                <div class="box-body" style="padding: 5px; overflow-y: auto;">
                    <div class="content1">
                        <div style="padding: 15px;">
                            <b> Planned</b>: <span><%=planned%></span><br>
                            <b>Existing</b>: <span><%=existing%></span><br>
                            <b>Requested</b>: <span><%=uF.parseToInt(uF.showData(jobProfileList.get(4), "0"))%></span>
                        </div>
                        <div class="holder">
                            <div id="EmployeeStats" style="height: 200px; width: 100%"></div>
                        </div>
                    </div>
                </div>
                <!-- /.box-body -->
            </div>
        </div>
    </div>
	<div class="clr"></div>
</div>

<% } else { %>
	<div class="col-lg-12 col-md-12 col-sm-12">
		<table class="table table-bordered">
			<tbody>
				<tr class="lighttable">
					<td colspan="15"><div class="nodata msg"><span> No data available.</span></div></td>
				</tr>
			</tbody>
		</table>
	</div>
<% } %>


<div class="modal" id="modalInfo" role="dialog">
    <div class="modal-dialog">
        Modal content
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">-</h4>
            </div>
            <div class="modal-body" style="height:400px;overflow-y:auto;padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div> 