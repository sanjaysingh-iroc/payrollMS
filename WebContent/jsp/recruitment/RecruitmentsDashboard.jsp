<%-- <%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%	String fromPage =(String)request.getAttribute("fromPage"); 
	String callFrom =(String)request.getAttribute("callFrom");
%>
		 		 
 --%>
 
 <style>
 element {
    padding-top: 15px;
}

 </style>
 <%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>

<script type="text/javascript" src="scripts/charts/jquery.min.js" ></script>

<script type="text/javascript" src="scripts/charts/highcharts.js"></script>
<script type="text/javascript" src="scripts/charts/exporting.js"></script>
<script type="text/javascript" src="scripts/charts/highcharts-3d.js"></script>
<script type="text/javascript" src="scripts/charts/no-data-to-display.js"></script>

<%
    UtilityFunctions uF = new UtilityFunctions(); 
    
    String fromPage = (String) request.getAttribute("fromPage");
    Map<String, String> approveMp = (Map) request.getAttribute("approveMp");
    if(approveMp == null) approveMp = new HashMap<String, String>();
    Map<String, String> finalisedMp = (Map) request.getAttribute("finalisedMp");
    if(finalisedMp == null) finalisedMp = new HashMap<String, String>();
    Map<String, String> denyMp = (Map) request.getAttribute("denyMp");
    if(denyMp == null) denyMp = new HashMap<String, String>();
    
    Map<String, String> hmCandAccepted = (Map<String, String>) request.getAttribute("hmCandAccepted");
    if(hmCandAccepted == null) hmCandAccepted = new HashMap<String, String>();
    Map<String, String> hmCandRejected = (Map<String, String>) request.getAttribute("hmCandRejected");
    if(hmCandRejected == null) hmCandRejected = new HashMap<String, String>();
    Map<String, String> hmCandOffered = (Map<String, String>) request.getAttribute("hmCandOffered");
    if(hmCandOffered == null) hmCandOffered = new HashMap<String, String>();
    /* Map<String, String> hmCandRequired = (Map<String, String>) request.getAttribute("hmCandRequired");
    if(hmCandRequired == null) hmCandRequired = new HashMap<String, String>(); */
    
    int existing = (Integer) request.getAttribute("existing");
    int planned = (Integer) request.getAttribute("planned");
    int required = (Integer) request.getAttribute("required");
    
    Map<String, String> hmSelectCount = (Map<String, String>) request.getAttribute("hmSelectCount");
    if(hmSelectCount == null) hmSelectCount = new HashMap<String, String>();
    Map<String, String> hmFinalCount = (Map<String, String>) request.getAttribute("hmFinalCount");
    if(hmFinalCount == null) hmFinalCount = new HashMap<String, String>();
    Map<String, String> hmRejectCount = (Map<String, String>) request.getAttribute("hmRejectCount");
    if(hmRejectCount == null) hmRejectCount = new HashMap<String, String>();
    
    Map<String, String> applyMp = (Map<String, String>) request.getAttribute("applyMp");
    if(applyMp == null) applyMp = new HashMap<String, String>();
    
    Map<String, String> hmScheduling = (Map<String, String>) request.getAttribute("hmScheduling");
    if(hmScheduling == null) hmScheduling = new HashMap<String, String>();
    Map<String, String> hmScheduledCandidate = (Map<String, String>) request.getAttribute("hmScheduledCandidate");
    if(hmScheduledCandidate == null) hmScheduledCandidate = new HashMap<String, String>();
    Map<String, String> hmUnderProcessCandidate = (Map<String, String>) request.getAttribute("hmUnderProcessCandidate");
    if(hmUnderProcessCandidate == null) hmUnderProcessCandidate = new HashMap<String, String>();
    
    Map<String, String> hmAppCount = (Map<String, String>) request.getAttribute("hmAppCount");
    if(hmAppCount == null) hmAppCount = new HashMap<String, String>();
    
    Map<String, String> hmToday = (Map<String, String>) request.getAttribute("hmToday");
    if(hmToday == null) hmToday = new HashMap<String, String>();
    Map<String, String> hmDayafterTommorow = (Map<String, String>) request.getAttribute("hmDayafterTommorow");
    if(hmDayafterTommorow == null) hmDayafterTommorow = new HashMap<String, String>();
    
    
    String strLiveJobs=request.getAttribute("strLiveJobs").toString();
    //System.out.println("strLiveJobs************"+strLiveJobs);
    String strApprovalPendingJobs=request.getAttribute("strApprovalPendingJobs").toString();
    System.out.println("strApprovalPendingJobs"+strApprovalPendingJobs);
    
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
</style>


<g:compress>
    <script type="text/javascript">
    	
	    $(function() {
	    	
	    	/* $(".col-md-9 .nav-tabs-custom .nav-tabs").find("li").removeClass("active");
	    	$(".col-md-9").find('a:contains(Details)').parent().addClass("active"); */
	    	
        	var chart;
        	var chart1;
        	var chart2;
        
        if(document.getElementById('ApplicationStats')!=null) {
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
                   data: [<%=uF.parseToInt(uF.showData(approveMp.get("ALL"), "0"))%>]
                }, {
                   name: 'Application Rejected',
                   data: [<%=uF.parseToInt(uF.showData(denyMp.get("ALL"), "0"))%>]
                },{
                   name: 'Finalised',
                   data: [<%=uF.parseToInt(uF.showData(finalisedMp.get("ALL"), "0"))%>]
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
           	           data: [<%=uF.parseToInt(uF.showData(approveMp.get("ALL"), "0"))%>]
           	     
           	        }, {
           	           name: 'Offered',
           	           data: [<%=uF.parseToInt(uF.showData(finalisedMp.get("ALL"), "0"))%>]
           	        },{
           	           name: 'Accepted',
           	           data: [<%=uF.parseToInt(uF.showData(hmCandAccepted.get("ALL"), "0"))%>]
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
          	           data: [<%=required %>]
          	        }]
           });
        });
        
        
    </script>
    
</g:compress>


	<%
	    String view = (String) request.getAttribute("view");
	    StringBuilder sbApproveDeny = new StringBuilder();
	%>
    
  <section class="content">
    <div class="row jscroll">
        <section class="connectedSortable">
        
		<div class="col-lg-12 col-md-12 col-sm-12">
			<%
				String strUserType = (String) session.getAttribute("USERTYPE");
			%>
			<div class="row row_without_margin">
			
					<div class="col-lg-4 col-md-6 col-sm-12 paddingleft0">
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
						                y: <%=required %>
						            }, {
						                name: 'Offered',
						                y: <%=uF.parseToInt(uF.showData(hmCandOffered.get("ALL"), "0")) %>
						            }, {
						                name: 'Accepted',
						                y: <%=uF.parseToInt(uF.showData(hmCandAccepted.get("ALL"), "0")) %>,
						                sliced: true,
						                selected: true
						            }, {
						                name: 'Rejected',
						                y: <%=uF.parseToInt(uF.showData(hmCandRejected.get("ALL"), "0")) %>,
						            }]
						        }]
						    });
						});
						</script>
					</div>
					
					<div class="col-lg-4 col-md-6 col-sm-12 paddingright0">
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
						                y: <%=uF.parseToInt(uF.showData(hmAppCount.get("ALL"), "0")) %>
						            }, {
						                name: 'Shortlisted',
						                y: <%=uF.parseToInt(uF.showData(approveMp.get("ALL"), "0"))%>
						            }, {
						                name: 'Finalization',
						                y: <%=uF.parseToInt(uF.showData(finalisedMp.get("ALL"), "0")) %>,
						                sliced: true,
						                selected: true
						            }, {
						                name: 'Rejected',
						                y: <%=uF.parseToInt(uF.showData(denyMp.get("ALL"), "0")) %>
						            }]
						        }]
						    });
						});
						</script>
					</div>
					
					<div class="col-lg-4 col-md-6 col-sm-12 paddingleft20">
					<!--1st block-->
					
					<div class="box box-default">
			                <div class="box-header with-border" data-widget="collapse-full">
			                    <h3 class="box-title">Induction</h3> 
			                </div>
			                <div class="box-body" style="padding: 5px; overflow-y: auto; max-height: 250px;">
			                    <div style="width:100%;">
									<ul class="site-stats">
										<li class="bg_lh" style="cursor: unset;">
											<strong><%=uF.showData(hmToday.get("ALL"), "0") %>
											</strong> 
											<small>Today</small>
										</li>
											<li class="bg_lh" style="cursor: unset;">
												<strong><%=uF.showData(hmDayafterTommorow.get("ALL"), "0") %></strong> 
												<small>72 hrs</small>
											</li>
									</ul>
		                    	</div>
			                </div>
			            </div>
					
					
					<!--2nd block-->
						<div class="box intvw-status" style="padding: 10px 5px;">
		                    Please schedule <span class="intvw-status" style="font-family: Digital;"><%=uF.showData(hmScheduling.get("ALL"), "0") %></span> candidates<br/>
		                    <span class="intvw-status" style="font-family: Digital;"><%=uF.showData(hmScheduledCandidate.get("ALL"), "0") %></span> candidates scheduled
	                    </div>
	                  
	                  <!--3rd block-->  
	                    <div class="box box-default">
			                <div class="box-header with-border" data-widget="collapse-full">
			                    <h3 class="box-title">Jobs</h3> 
			                </div>
			                <div class="box-body" style="padding: 5px; overflow-y: auto; max-height: 250px;">
			                    <div style="width:100%;">
									<ul class="site-stats">
										<li class="bg_lh" style="cursor: unset;">
											<strong><%=strLiveJobs%></strong> 
											<small>Live Jobs</small>
										</li>
											<li class="bg_lh" style="cursor: unset;">
												<strong><%=strApprovalPendingJobs%></strong>
												<small>Job Appprovals</small>
											</li>
									</ul>
		                    	</div>
			                </div>
			            </div>
	                    
					</div>
					
	    	<div class="col-lg-4 col-md-6 col-sm-12 paddingleft0">
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
	                            <b>Planned</b>: <span><%=planned %></span><br>
	                            <b>Existing</b>: <span><%=existing %></span><br>
	                            <b>Requested</b>: <span><%=required %></span>
	                        </div>
	                        <div class="holder">
	                            <div id="EmployeeStats" style="height: 200px; width: 100%"></div>
	                        </div>
	                    </div>
	                </div>
	                <!-- /.box-body -->
	            </div>
            </div>
            
            <div class="col-lg-4 col-md-6 col-sm-12 paddingright0">
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
	                            <b> Shortlisted</b>: <span><%=uF.parseToInt(uF.showData(approveMp.get("ALL"), "0"))%></span><br>
	                            <b> Offered</b>: <span><%=uF.parseToInt(uF.showData(finalisedMp.get("ALL"), "0"))%></span><br>
	                            <b> Accepted</b>: <span><%=uF.parseToInt(uF.showData(hmCandAccepted.get("ALL"), "0"))%></span>
	                        </div>
	                        <div class="holder">
	                            <div id="CandidateStats" style="height: 200px; width: 100%">
	                            </div>
	                        </div>
	                    </div>
	                </div>
	            </div>
           	</div>
  
	 <div class="col-lg-4 col-md-6 col-sm-12">
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
	                            <b> Application Accepted</b>: <span><%=uF.parseToInt(uF.showData(approveMp.get("ALL"), "0"))%></span>
	                            <br> <b>Application Rejected</b>: <span><%=uF.parseToInt(uF.showData(denyMp.get("ALL"), "0"))%></span>
	                            <br> <b>Finalised</b>: <span><%=uF.parseToInt(uF.showData(finalisedMp.get("ALL"), "0"))%></span>
	                        </div>
	                        <div class="holder">
	                            <div id="ApplicationStats" style="height: 200px; width: 100%"></div>
	                        </div>
	                    </div>
	                </div>
	                <!-- /.box-body -->
	            </div>
	    	</div>
		</div>		
	</div><!--end of 12 colm div  -->
	</section>
</div>
</section>
