<%@page import="com.konnect.jpms.util.ArrayUtils"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="java.util.*, ChartDirector.*"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
	
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<%
UtilityFunctions uF = new UtilityFunctions();
String []arrEnabledModules = (String[])request.getAttribute("arrEnabledModules");  

List<List<String>> skillwiseEmpCountGraphList = (List<List<String>>) request.getAttribute("skillwiseEmpCountGraphList");
if(skillwiseEmpCountGraphList == null) skillwiseEmpCountGraphList = new ArrayList<List<String>>();

List<List<String>> leaveSummaryList = (List<List<String>>) request.getAttribute("leaveSummaryList");
if(leaveSummaryList == null) leaveSummaryList = new ArrayList<List<String>>();
List<List<String>> compensationSummaryList = (List<List<String>>) request.getAttribute("compensationSummaryList");
if(compensationSummaryList == null) compensationSummaryList = new ArrayList<List<String>>();
String compensationDate = (String) request.getAttribute("compensationDate");
compensationDate = uF.showData(compensationDate,"");

String isEmpLimit = (String)request.getAttribute("isEmpLimit");
String strEmpLimit = (String)request.getAttribute("strEmpLimit");
 
%>
<script src="scripts/charts/highcharts.js" type="text/javascript"></script>
<script src="scripts/D3/amcharts/amcharts.js" type="text/javascript"></script>
<script src="scripts/D3/amcharts/serial.js" type="text/javascript"></script> 
<script src="scripts/D3/amcharts/pie.js" type="text/javascript"></script>
<script src="scripts/D3/amcharts/themes/light.js" type="text/javascript"></script>
<link href="js/jvectormap/jquery-jvectormap-1.2.2.css" rel='stylesheet' />

<style>
    
    .imgA1 { top: 0px; left: 0px; z-index: 1; max-height: 100px; overflow: hidden; } 
	.imgB1 { position:absolute; top: 0px; left: 0px; z-index: 3; max-height: 100px; overflow: hidden; }
	
	 .gender-divs{
	 display: inline-block;
	 position: relative;
	 border-right: 4px solid rgb(231, 231, 231);
	 }
	 
	 .gender-divs i{
	 font-size: 100px;
	 }
	 
	 .imgA1 i { 
	 color: rgb(29, 132, 180)
	 }
	 
	 .gender-info{
	 display: inline-block;vertical-align: top;font-size: 18px;padding-left: 5px;
	 }
	 
	 .gender-perc{
		margin-left: 30px;
		margin-top: 10px;
		font-size: 22px;
		color: rgb(27, 102, 162);
	 }
</style>


<style> 
 

#greenbox {
height: 11px;
background-color:#00a65a; /* the critical component */
}

#redbox {
height: 11px;
background-color:#E25948; /* the critical component */
}

#yellowbox {
height: 11px;
background-color:#f39c12; /* the critical component */
}

#outbox {
height: 11px;
width: 100%;
background-color:#D8D8D8; /* the critical component */
}

.anaAttrib1 {
font-size: 14px;
font-family: digital;
color: #3F82BF;
font-weight: bold;
}
.site-stats li {
width: 29%;
}

.highcharts-legend-item text{
font-weight: 300 !important; 
}
</style>

<script>

var cities = [];
var departments = [];
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

function viewAllActivities() {
	
	var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 $(".modal-title").html('Employee Activity List');
	 if($(window).width() >= 900){
		 $(".modal-dialog").width(900);
	 }
	 $.ajax({
			url : "EmployeeActivityList.action",
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
	});
}


var chartAttendance;

/* Highcharts.setOptions({
    colors: ['#00FF00','#FF6633']
}); */

$(document).ready(function() {
	
	chartAttendance = new Highcharts.Chart({
   		
      chart: {
         renderTo: 'container_Attendance',
        	type: 'column'
      },
      title: {
         text: 'In (Hours)'
      },
      xAxis: {
         categories: [<%=request.getAttribute("sbDatesAttendanceDate")%>],
         labels: {
             rotation: -45,
             align: 'right',
             style: {
                 font: 'normal 10px Verdana, sans-serif'
             }
          },
         title: {
	            text: 'Date'
	         }
      },
      credits: {
       	enabled: false
   	  },
      yAxis: {
         min: 0,
         title: {
            text: 'Attendance'
         }
      },
      plotOptions: {
         column: {
            pointPadding: 0.2,
            borderWidth: 0
         }
      },
     series: [<%=request.getAttribute("sbDatesAttendance")%>]
   });
	
});



$(document).ready(function() {
	
	var chartSkill;

	chartSkill = new Highcharts.Chart({
	      chart: {
	          renderTo: 'containerForLeaveSumaryCharts',
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
	          categories: ['Leave Summary']
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
	       
	       series: [
	       <%for(int i=0; leaveSummaryList!=null && i<leaveSummaryList.size(); i++){
	    	   List<String> innerList = leaveSummaryList.get(i);
	    	   if(innerList !=null && innerList.size() > 1) {
	    	   if(i==0){ 
           	%>
           	{
           		name: '<%=innerList.get(0)%>',
	           	data: [<%=uF.parseToInt(uF.showData(innerList.get(1), "0"))%>]
           	}
           	<%}else{%>
           	,{
           		name: '<%=innerList.get(0)%>',
	           	data: [<%=uF.parseToInt(uF.showData(innerList.get(1), "0"))%>]
           	}
           	<%}%>   
           	<%}%>
           <%}%>]
   });
});

$(document).ready(function() {
	
	var chartSkill;

	chartSkill = new Highcharts.Chart({
	      chart: {
	          renderTo: 'containerForCompensationSumaryCharts',
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
	          categories: ['<%=compensationDate %>']
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
	       
	       series: [
	       <%for(int i=0; compensationSummaryList!=null && i<compensationSummaryList.size(); i++){
	    	   List<String> innerList = compensationSummaryList.get(i);
	    	   if(i==0){ 
           	%>
           	{
           		name: '<%=innerList.get(0)%>',
	           	data: [<%=uF.parseToDouble(innerList.get(1))%>]
           	}
           	<%}else{%>
           	,{
           		name: '<%=innerList.get(0)%>',
	           	data: [<%=uF.parseToDouble(innerList.get(1))%>]
           	}
           	<%}%>   
           <%}%>]
   });
});


	$(document).ready(function() {
		
		var chartSkill;
		chartSkill = new Highcharts.Chart({
		      chart: {
		          renderTo: 'containerForSkillCharts',
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
		          categories: ['Skills']
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
		       
		       series: [
		       <%for(int i=0; skillwiseEmpCountGraphList!=null && i<skillwiseEmpCountGraphList.size(); i++){
		    	   List<String> innerList = skillwiseEmpCountGraphList.get(i);
		    	   if(innerList !=null && innerList.size() > 1) {
		    	   if(i==0){ 
	           	%>
	           	{
	           		name: '<%=innerList.get(0)%>',
		           	data: [<%=uF.parseToInt(uF.showData(innerList.get(1), "0"))%>]
	           	}
	           	<%}else{%>
	           	,{
	           		name: '<%=innerList.get(0)%>',
		           	data: [<%=uF.parseToInt(uF.showData(innerList.get(1), "0"))%>]
	           	}
	           	<%}%>   
	           	<%}%>
	           <%}%>]
	   });
	});

	
</script> 

<%
	String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
	String strUserType = (String)session.getAttribute(IConstants.USERTYPE);
	String strBaseUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
	String strTitle = (String) request.getAttribute(IConstants.TITLE);
%>


<%-- <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="<%=strTitle %>" name="title"/>
</jsp:include> --%>

	
	
<%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT+"")>=0 && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_ONBOARDING+"")>=0) { %>
<div class="leftbox reportWidth">
		<section class="content">
			<div class="row jscroll">
			<% if(uF.parseToBoolean(isEmpLimit)) { %>
				<div class="callout callout-warning" style="float: left; margin: 0px 15px 15px; padding: 7px 10px; font-weight: 600;">
					Dear Customer, You have exceeded your employee limit of <%=strEmpLimit %> employees. We have extended a grace period. Please contact <a href="mailto:accounts@workrig.com">accounts@workrig.com</a> to add additional slab for continued usage.
				</div>
			<% } %>
				<% if(strUserType != null && (strBaseUserType.equals(IConstants.CEO) || strUserType.equals(IConstants.HRMANAGER) || strUserType.equals(IConstants.ADMIN))) { %>
		        <section class="col-lg-8 connectedSortable" style="padding-right: 0px;">
		        <% } else if(strUserType != null && strUserType.equals(IConstants.MANAGER) && strBaseUserType != null && strBaseUserType.equals(IConstants.CEO)) { %>
		        <section class="col-lg-7 connectedSortable" style="padding-right: 0px;">
		        <% } else { %>
		        <section class="col-lg-12 connectedSortable">
		        <% } %>
		        	<div class="box box-primary">
		        		<%String wLocationCount = (String) request.getAttribute("wLocationCount"); %>
		                <div class="box-header with-border">
		                    <h3 class="box-title">Geographical Spread</h3>
		                    <div class="box-tools pull-right">
		                    	<span class="badge bg-blue"><%=wLocationCount %></span>
		                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
		                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
		                    </div>
		                </div>
		                
		                <!-- /.box-header -->
		                <div class="box-body" style="padding: 5px; overflow-y: auto;">
		                    <div class="pad">
			                    <!-- Map will be created here -->
			                    <div id="world-map" style="height: 312px;"></div>
			                  </div>
		                </div>
		                
		                <div class="box-footer">
			              <div class="row">
			              	<div class="col-sm-12 col-xs-12 col-md-12 col-lg-12">
				              	<div style="overflow-x: auto;">
				              		<table class="table table_no_border" style="margin-bottom: 0px;">
				              			<tr>
					              	<script src="js/jvectormap/jquery-jvectormap-1.2.2.min.js"></script>
									<%-- <script src="js/jvectormap/jquery-jvectormap-in-mill.js"></script> --%>
									<script src="js/jvectormap/jquery-jvectormap-world-mill-en.js"></script>		
					              	<% 
						               Map<String, String> hmWLocOrgName = (Map<String, String>) request.getAttribute("hmWLocOrgName");
						               Map hmWLocationEmployeeCount = (Map)request.getAttribute("hmWLocationEmployeeCount");
						               if(hmWLocationEmployeeCount==null)hmWLocationEmployeeCount=new HashMap<String, Map<String, String>>();
						               Map hmWorkLocationMap = (Map)request.getAttribute("hmWorkLocationMap");
						               
										Set setWLocationEmployeeCount = hmWLocationEmployeeCount.keySet();
										Iterator itWLocationEmployeeCount = setWLocationEmployeeCount.iterator();
										while(itWLocationEmployeeCount.hasNext()){
											String strWLocationId = (String)itWLocationEmployeeCount.next();
											
											Map hmWLocation = (Map)hmWorkLocationMap.get(strWLocationId);
											if(hmWLocation==null)hmWLocation=new HashMap();
											if(hmWLocation != null && !hmWLocation.isEmpty()) {
												
									%>	
									<td>
				              			<div class="description-block border-right">
						                    <span class="description-percentage text-green"><b><%=(String)hmWLocationEmployeeCount.get(strWLocationId) %></b></span>
						                    <h5 class="description-header"><%=(String)hmWLocation.get("WL_CITY")+", "+(String)hmWLocation.get("WL_COUNTRY") %></h5>
						                    <span class="description-text"><%=(String)hmWLocOrgName.get(strWLocationId) %></span>
						                  </div>	
				              		</td>
					               <script>
					               //alert("strLoc ===>> " + strLoc);
					               $.ajax({
					            	   url:"https://maps.googleapis.com/maps/api/geocode/json?address="+encodeURIComponent('<%=(String)hmWLocation.get("WL_CITY")%>')+"&key=AIzaSyBIl1nxilzv8l1GDupibf73zUxnnNBjnic",
					            	   dataType: 'json',
					            	   async: false,
					            	   success: function(val) {
					            		   if(val.results.length) {
						            	      var location = val.results[0].geometry.location;
						            	      cities.push({latLng:[location.lat,location.lng], name: '<%=(String)hmWLocation.get("WL_CITY")%>'});
						            	   }
					            	   }
					            	 });
					               		
					               </script>
									<%   }
										}
									%> 
									<script>
									$('#world-map').vectorMap({
											    map: 'world_mill_en',
											    backgroundColor: "transparent",
											    regionStyle: {
											    	initial: {
												        fill: '#e4e4e4',
												        "fill-opacity": 1,
												        "stroke-width": 1,
												        "stroke-opacity": 1
												      }
											    },
											    normalizeFunction: 'polynomial',  
											    markerStyle: {
											        initial: {
											            fill: '#F8E23B',
											            stroke: '#383f47'
											        }
											    },
											    markers:cities.map(function(h) {
											        return {
											           	latLng: h.latLng,
											            name: h.name
											        }
											    })
											  });
										</script>
										</tr>
				              		</table>
				              	</div>
				              </div>	
			              </div>
			              <!-- /.row -->
			            </div>
		                <!-- /.box-body -->
		            </div>
		        </section>
		        <% if(strUserType != null && (strBaseUserType.equals(IConstants.CEO) || strUserType.equals(IConstants.HRMANAGER) || strUserType.equals(IConstants.ADMIN))) { %>
		        <section class="col-lg-4 connectedSortable">
					<% if(arrEnabledModules!=null && (ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PEOPLE_MANAGEMENT+"")>=0 || ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT+"")>=0 || ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_ONBOARDING+"")>=0)) {
						String gapCount = (String) request.getAttribute("gapCount");
						String strCurrJobProfiles = (String) request.getAttribute("strCurrJobProfiles");
						String strNewRequisitons = (String) request.getAttribute("strNewRequisitons");
						String totalInduction = (String) request.getAttribute("totalInduction");
						String allResigCnt = (String) request.getAttribute("allResigCnt");
						String retirementCnt = (String) request.getAttribute("retirementCnt");
						String allFinalDayCnt = (String) request.getAttribute("allFinalDayCnt");
						String confirmCnt = (String) request.getAttribute("confirmCnt");
					//	System.out.println("totalInduction==>"+totalInduction+"==>confirmCnt==>"+confirmCnt+"==>allResigCnt==>"+allResigCnt+"==>allFinalDayCnt==>"+allFinalDayCnt);
						int totActivityCnt = uF.parseToInt(totalInduction) + uF.parseToInt(retirementCnt) + uF.parseToInt(allResigCnt) + uF.parseToInt(allFinalDayCnt) + uF.parseToInt(confirmCnt);
					//	System.out.println("totActivityCnt==>"+totActivityCnt);
						int totAlertCount = uF.parseToInt(gapCount) + uF.parseToInt(strCurrJobProfiles) + uF.parseToInt(strNewRequisitons) + totActivityCnt;
					%> 
		            <div class="box box-info">
                
		                <div class="box-header with-border">
		                    <h3 class="box-title">HR Alerts</h3>
		                    <div class="box-tools pull-right">
		                    	<span class="badge bg-blue"><%=totAlertCount %></span>
		                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
		                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
		                    </div>
		                </div>
		                <!-- /.box-header -->
		                <div class="box-body" style="padding: 5px; overflow-y: auto; max-height: 250px;">
		                    <div style="width:100%;">
								<%-- <div style="padding: 3px; margin: 3px; width: 97%; border: 1px solid #CCCCCC; border-radius: 5px; text-align: center; background-color: #FCFCFC;">
									<div style="font-size: 30px; line-height: 23px;"><%=feedCount %></div>
									<div style="font-weight: bold; color: gray;">UNREAD FEED</div>   
								</div> --%>
								<ul class="site-stats">
									<% if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT+"")>=0) { %>
										<a href="Learnings.action?callFrom=LDash">
											<li class="bg_lh"><i class="fa fa-leanpub" aria-hidden="true"></i><strong><%=gapCount %></strong> <small>Learning Gaps</small></li>
										</a>
									<% } %>	
									<% if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_RECRUITMENT_MANAGEMENT+"")>=0) { %>
										<a href="RequirementDashboard.action">
											<li class="bg_lh"><i class="fa fa-outdent" aria-hidden="true"></i><strong><%=strNewRequisitons %></strong> <small>New Requisitons</small></li>
										</a>
									<% } %>
									<% if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_RECRUITMENT_MANAGEMENT+"")>=0) { %>
										<a href="RequirementDashboard.action?callFrom=HRDashJobApproval">
											<li class="bg_lh"><i class="fa fa-briefcase" aria-hidden="true"></i><strong><%=strCurrJobProfiles %></strong> <small>Job Profiles</small></li>
										</a>
									<% } %>
									
								</ul>
								<ul class="site-stats">
									<%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PEOPLE_MANAGEMENT+"")>=0){ %>
										<%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_RECRUITMENT_MANAGEMENT+"")>=0){ %>
											<a href="javascript:void(0);" onclick="viewAllActivities();">
												<li class="bg_lh"><i class="fa fa-calendar-o" aria-hidden="true"></i><strong><%=totActivityCnt %></strong> <small>Activities <br/>(All Legal Entity)</small></li>
											</a>
										<% } else { %>
											<li class="bg_lh"><i class="fa fa-calendar-o" aria-hidden="true"></i><strong><%=totActivityCnt %></strong> <small>Activities <br/>(All Legal Entity)</small></li>
										<% } %>
									<% } %> 
								</ul>
	                    	</div>
		                </div>
		                <!-- /.box-body -->
		            </div>
		            <% } %>
		            <% if(arrEnabledModules!=null && (ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT+"")>=0 || ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_ONBOARDING+"")>=0)) {
							String reviewEmpCount = (String) request.getAttribute("reviewEmpCount");
							String kraFormCount = (String) request.getAttribute("kraFormCount");
							String interviewCount = (String) request.getAttribute("interviewCount");
							
							int totFormCount = uF.parseToInt(reviewEmpCount) + uF.parseToInt(kraFormCount) + uF.parseToInt(interviewCount);
						%> 
		            <div class="box box-success">
		                <div class="box-header with-border">
		                    <h3 class="box-title">HR Forms</h3>
		                    <div class="box-tools pull-right">
		                    	<span class="badge bg-green"><%=totFormCount %></span>
		                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
		                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
		                    </div>
		                </div>
		                <!-- /.box-header -->
		                <div class="box-body" style="padding: 5px; overflow-y: auto; max-height: 250px;">

	                    	<ul class="site-stats">
									<% if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT+"")>=0) { %>
										<a href="Reviews.action?callFrom=FDash">
											<li class="bg_lh"><i class="fa fa-signal" aria-hidden="true"></i><strong><%=reviewEmpCount %></strong> <small>HR Reviews</small></li>
										</a>
										<a href="GoalKRATargets.action?callFrom=KTDash">
											<li class="bg_lh"><i class="fa fa-signal" aria-hidden="true"></i><strong><%=kraFormCount %></strong> <small>KRA Reviews</small></li>
										</a>
									<% } %>	
									<% if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_RECRUITMENT_MANAGEMENT+"")>=0) { %>
										<a href="Login.action?role=3&userscreen=interviews">
											<li class="bg_lh"><i class="fa fa-calendar" aria-hidden="true"></i><strong><%=interviewCount %></strong> <small>Interviews (1w)</small></li>
										</a>
									<% } %>
								</ul>
		                </div>
		                <!-- /.box-body -->
		            </div>
		            <% } %>
		            <%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_TIME_AND_ATTENDANCE+"")>=0){ 
			
						String exceptionCount = (String) request.getAttribute("exceptionCount");
						
						int totExceptionCount = uF.parseToInt(exceptionCount);
					%> 
		            <div class="box box-warning">
		                <div class="box-header with-border">
		                    <h3 class="box-title">Time Exceptions</h3>
		                    <div class="box-tools pull-right">
		                    	<span class="badge bg-yellow"><%=totExceptionCount %></span>
		                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
		                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
		                    </div>
		                </div>
		                <!-- /.box-header -->
		                <div class="box-body" style="padding: 5px; overflow-y: auto; max-height: 250px;">
		                    <ul class="site-stats">
								<a href="TimeApprovals.action?callFrom=HRDashTimeExceptions">
									<li class="bg_lh" style="width: 92%;"><i class="fa fa-exclamation-circle" aria-hidden="true"></i><strong><%=uF.parseToInt(exceptionCount) %></strong> <small>Time Exceptions</small></li>
								</a>
	                    	</ul>
		                </div>
		                <!-- /.box-body -->
		            </div>
		            <% } %>
		            
		        </section>
		        <% } %>
		        
		        <% if(strUserType != null && strUserType.equals(IConstants.MANAGER) && strBaseUserType != null && strBaseUserType.equals(IConstants.CEO)) { 
		        	int totMaleEmp = 0;
		            int totFemaleEmp = 0;
		            int totOtherEmp = 0;
		        	List<List<String>> allGenderPeopleReport = (List<List<String>>) request.getAttribute("allGenderPeopleReport");
		        	for (int i = 0; i < allGenderPeopleReport.size(); i++) {
		            	List<String> innerList = (List<String>) allGenderPeopleReport.get(i);
		            
		            	totMaleEmp += uF.parseToInt(innerList.get(2));
		            	totFemaleEmp += uF.parseToInt(innerList.get(3));
		            	totOtherEmp += uF.parseToInt(innerList.get(4));
		            }
		        %>
			        <section class="col-lg-5 connectedSortable">
	        		<div class="box box-primary">
			            <div class="box-header with-border">
			                <h3 class="box-title">Gender wise</h3>
			                <div class="box-tools pull-right">
			                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
			                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
			                </div>
			            </div>
			            <!-- /.box-header -->
			            <div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 200px; min-height: 400px;">
			                <div class="attendance row row_without_margin" style="clear: both;padding-top: 20px;">
			                    <div class="col-lg-12 col-md-12 col-sm-12 col_no_padding">
			                    	<div class="col-lg-4 col-md-4 col-sm-4 col_no_padding">
				                        <div class="gender-divs">
										    <div class="imgA1">
										    	<i class="fa fa-female" aria-hidden="true" style="color:rgb(255, 116, 238);"></i>
										    </div>
										    <div id="femaleGreyImg" class="imgB1">
										    	<i class="fa fa-female" aria-hidden="true"></i>
										    </div>
										</div>
										<div class="gender-info"><strong><%=totFemaleEmp %></strong><p>Females</p></div>
										<div class="gender-perc" id="female-perc"></div>
									</div>
									<div class="col-lg-4 col-md-4 col-sm-4 col_no_padding">
										<div class="gender-divs">
										    <div class="imgA1">
										    	<i class="fa fa-male" aria-hidden="true"></i>
										    </div>
										    <div id="maleGreyImg" class="imgB1">
										    	<i class="fa fa-male" aria-hidden="true"></i>
										    </div>
										</div>
										<div class="gender-info"><strong><%=totMaleEmp %></strong> <p>Males</p></div>
										<div class="gender-perc" id="male-perc"></div>
									</div>
									<div class="col-lg-4 col-md-4 col-sm-4 col_no_padding">
										<div class="gender-divs">
										    <div class="imgA1">
										    	<i class="fa fa-exclamation" aria-hidden="true" style="color: rgb(213, 213, 213);"></i>
										    </div>
										    <div id="otherGreyImg" class="imgB1">
										    	<i class="fa fa-exclamation" aria-hidden="true"></i>
										    </div>
										</div>
										<div class="gender-info"><strong><%=totOtherEmp %></strong> <p>Other</p></div>
										<div class="gender-perc" id="other-perc"></div>
									</div>
									<script>
									var female_cnt = parseFloat('<%=totFemaleEmp%>');
									var male_cnt = parseFloat('<%=totMaleEmp%>');
									var other_cnt = parseFloat('<%=totOtherEmp%>');
									
									var sum_of_all = female_cnt + male_cnt + other_cnt;
									var female_perc = ((female_cnt/sum_of_all)*100).toFixed(2);
									var male_perc = ((male_cnt/sum_of_all)*100).toFixed(2);
									var other_perc = ((other_cnt/sum_of_all)*100).toFixed(2);
									
									document.getElementById('male-perc').innerHTML = +male_perc+"%" ;
									document.getElementById('female-perc').innerHTML = female_perc+"%" ;
									document.getElementById('other-perc').innerHTML = other_perc+"%" ;
									document.getElementById('femaleGreyImg').setAttribute('style', 'max-height: ' + (100-female_perc) + 'px; overflow: hidden;');
									document.getElementById('maleGreyImg').setAttribute('style', 'max-height: ' + (100-male_perc) + 'px; overflow: hidden;');
									document.getElementById('otherGreyImg').setAttribute('style', 'max-height: ' + (100-other_perc) + 'px; overflow: hidden;');
									</script>
			                    </div>
			                </div>
			            </div>
			            <!-- /.box-body -->
			        </div>	
	        	</section>
			<% } %>
		    </div>
		    
		    <% if(strUserType != null && strUserType.equals(IConstants.MANAGER) && strBaseUserType != null && strBaseUserType.equals(IConstants.CEO)) { %>
				<%
		            List<List<String>> allWorkingPeopleReport = (List<List<String>>) request.getAttribute("allWorkingPeopleReport");
		            List<List<String>> allEmploymentTypeReport = (List<List<String>>) request.getAttribute("allEmploymentTypeReport");
		            
		            String unassignCandiCnt = (String) request.getAttribute("unassignCandiCnt");
		            Map<String, String> hmFeatureStatus = (Map<String, String>) request.getAttribute("hmFeatureStatus");
		            
		            /* int totPendingEmp1 = 0;
		            int totworkingEmp1 = 0; */
		            int totPendingEmp = 0;
		            int totProbationEmp = 0;
		            int totPermanentEmp = 0;
		            int totTemporaryEmp = 0;
		            int totResignedEmp = 0;
		            int totExEmp = 0;
		            int totUsers = 0;
		            
		            /* Employment Typewise Total */
		            int totFullTimeEmp = 0;
		            int totPartTimeEmp = 0;
		            int totConsultantEmp = 0;
		            int totContractualEmp = 0;
		            int totInternEmp = 0;
		            
		            int totRegularEmp = 0;
		            int totContractEmp = 0;
		            int totProfessionalEmp = 0;
		            int totStipendEmp = 0;
		            int totScholarshipEmp = 0;
		            
		            int totTempEmp = 0;
		            int totArticleEmp = 0;
		            int totPartnerEmp = 0;
		            
		            
		            // Employee leaveDeatils
		            
		            int totalLeaveCount = 0;
		            int totalPresentCount = 0;
		            int totalAbsentCount = 0;
		            int totalODCount = 0;
		            int totalTravelCount = 0;
		            int totalExtraWorkingCount = 0;
		            
		            
		            //int totCandidates = 0;
		            %>
		        <%
		            for (int i = 0; i < allWorkingPeopleReport.size(); i++) {
		            	List<String> alinner = (List<String>) allWorkingPeopleReport.get(i);
		            
		            	totPendingEmp += uF.parseToInt(alinner.get(2));
		            	totProbationEmp += uF.parseToInt(alinner.get(3));
		            	totPermanentEmp += uF.parseToInt(alinner.get(4));
		            	totTemporaryEmp += uF.parseToInt(alinner.get(5));
		            	totResignedEmp += uF.parseToInt(alinner.get(6));
		            	//totworkingEmp1  += uF.parseToInt(alinner.get(2));
		            	totExEmp  += uF.parseToInt(alinner.get(7));
		            	totUsers += uF.parseToInt(alinner.get(8));
		            	//totCandidates += uF.parseToInt(alinner.get(6));
		            	//totalAprFinalised1 += uF.parseToInt(alinner.get(10)); 
		            }
		     
		       	 	
		            StringBuilder sbEmpStatusPie = new StringBuilder();
		            sbEmpStatusPie.append("{'Status':'New Joinee', 'cnt': "+totPendingEmp+"},");
		            sbEmpStatusPie.append("{'Status':'Probation', 'cnt': "+totProbationEmp+"},");
		            sbEmpStatusPie.append("{'Status':'Permanent', 'cnt': "+totPermanentEmp+"},");
		            sbEmpStatusPie.append("{'Status':'Temporary', 'cnt': "+totTemporaryEmp+"},");
		            sbEmpStatusPie.append("{'Status':'Resigned', 'cnt': "+totResignedEmp+"},");
		            
					if(sbEmpStatusPie.length()>1) {
		            	sbEmpStatusPie.replace(0, sbEmpStatusPie.length(), sbEmpStatusPie.substring(0, sbEmpStatusPie.length()-1));
					}
		            
		            %>
		        <%			
		            for (int i = 0; i < allEmploymentTypeReport.size(); i++) {
		            	List<String> innerList = (List<String>) allEmploymentTypeReport.get(i);
		            	if(hmFeatureStatus == null || (hmFeatureStatus.get(IConstants.F_USERTYPE_GENERAL) != null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_USERTYPE_GENERAL)))) {
		            		totFullTimeEmp += uF.parseToInt(innerList.get(2));
		                 	totPartTimeEmp += uF.parseToInt(innerList.get(3));
		                 	totConsultantEmp += uF.parseToInt(innerList.get(4));
		                 	totContractualEmp += uF.parseToInt(innerList.get(5));
		                 	totInternEmp += uF.parseToInt(innerList.get(6));
		                 	
		                } else if(hmFeatureStatus.get(IConstants.F_USERTYPE_HEALTH_CARE) != null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_USERTYPE_HEALTH_CARE))) {
		                	totRegularEmp += uF.parseToInt(innerList.get(2));
		                 	totContractEmp += uF.parseToInt(innerList.get(3));
		                 	totProfessionalEmp += uF.parseToInt(innerList.get(4));
		                 	totStipendEmp += uF.parseToInt(innerList.get(5));
		                 	totScholarshipEmp += uF.parseToInt(innerList.get(6));
		                 	
		                } else if(hmFeatureStatus.get(IConstants.F_USERTYPE_FINANCE) != null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_USERTYPE_FINANCE))) {
		                	totFullTimeEmp += uF.parseToInt(innerList.get(2));
		                 	totPartTimeEmp += uF.parseToInt(innerList.get(3));
		                 	totConsultantEmp += uF.parseToInt(innerList.get(4));
		                 	totTempEmp += uF.parseToInt(innerList.get(5));
		                 	totArticleEmp += uF.parseToInt(innerList.get(6));
		                 	totPartnerEmp += uF.parseToInt(innerList.get(7));
		                }
		            	
		            }
		        	
		            
		            StringBuilder sbEmpTypePie = new StringBuilder();
		            if(hmFeatureStatus == null || (hmFeatureStatus.get(IConstants.F_USERTYPE_GENERAL) != null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_USERTYPE_GENERAL)))) {
			            sbEmpTypePie.append("{'Status':'Full Time', 'cnt': "+totFullTimeEmp+"},");
			            sbEmpTypePie.append("{'Status':'Part Time', 'cnt': "+totPartTimeEmp+"},");
			            sbEmpTypePie.append("{'Status':'Consultant', 'cnt': "+totConsultantEmp+"},");
			            sbEmpTypePie.append("{'Status':'Contractual', 'cnt': "+totContractualEmp+"},");
			            sbEmpTypePie.append("{'Status':'Intern', 'cnt': "+totInternEmp+"},");
		            } else if(hmFeatureStatus.get(IConstants.F_USERTYPE_HEALTH_CARE) != null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_USERTYPE_HEALTH_CARE))) {
		            	sbEmpTypePie.append("{'Status':'Regular', 'cnt': "+totRegularEmp+"},");
			            sbEmpTypePie.append("{'Status':'Contract', 'cnt': "+totContractEmp+"},");
			            sbEmpTypePie.append("{'Status':'Professional', 'cnt': "+totProfessionalEmp+"},");
			            sbEmpTypePie.append("{'Status':'Stipend', 'cnt': "+totStipendEmp+"},");
			            sbEmpTypePie.append("{'Status':'Scholarship', 'cnt': "+totScholarshipEmp+"},");
		            } else if(hmFeatureStatus.get(IConstants.F_USERTYPE_FINANCE) != null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_USERTYPE_FINANCE))) {
		            	sbEmpTypePie.append("{'Status':'Full Time', 'cnt': "+totFullTimeEmp+"},");
			            sbEmpTypePie.append("{'Status':'Part Time', 'cnt': "+totPartTimeEmp+"},");
			            sbEmpTypePie.append("{'Status':'Consultant', 'cnt': "+totConsultantEmp+"},");
			            sbEmpTypePie.append("{'Status':'Temporary', 'cnt': "+totTempEmp+"},");
			            sbEmpTypePie.append("{'Status':'Article', 'cnt': "+totArticleEmp+"},");
			            sbEmpTypePie.append("{'Status':'Partner', 'cnt': "+totPartnerEmp+"},");
		            }
		            
		            if(sbEmpTypePie.length()>1) {
		            	sbEmpTypePie.replace(0, sbEmpTypePie.length(), sbEmpTypePie.substring(0, sbEmpTypePie.length()-1));
					}
		            
		       	 	totalLeaveCount = uF.parseToInt((String)request.getAttribute("allLeaveEmpCount"));
		       		 totalTravelCount = uF.parseToInt((String)request.getAttribute("totalTravelCount"));


		            StringBuilder sbEmpLeavePie = new StringBuilder();
		            sbEmpLeavePie.append("{'Status':'Present', 'cnt': "+totalPresentCount+"},");
		            sbEmpLeavePie.append("{'Status':'Absent', 'cnt': "+totalAbsentCount+"},");
		            sbEmpLeavePie.append("{'Status':'Leave', 'cnt': "+totalLeaveCount+"},");
		            sbEmpLeavePie.append("{'Status':'OD', 'cnt': "+totalODCount+"},");
		            sbEmpLeavePie.append("{'Status':'Travel', 'cnt': "+totalTravelCount+"},");
		            sbEmpLeavePie.append("{'Status':'Extra Working', 'cnt': "+totalExtraWorkingCount+"},");
		            
		            %>
		            
		            <div class="row jscroll">
		           
		             
		            <section class="col-lg-6 connectedSortable" style="padding-right:0px;">
		        		<div class="box box-primary">
				            <div class="box-header with-border">
				                <h3 class="box-title">Employee Status wise</h3>
				            </div>
				            <!-- /.box-header -->
				            <div class="box-body" style="padding: 5px; overflow-y: auto;">
				                <div class="attendance row row_without_margin clr margintop20">
				                    <div class="col-lg-12 col-md-12 col-sm-12 col_no_padding">
				                        <div id="chartEmpStatusdiv" style="height:300px;"></div>
				                        <script>
				                            var chart2;
				                            var chartData2 = [<%=sbEmpStatusPie %>];
				                            var legend2;
				                            var chart = AmCharts.makeChart( "chartEmpStatusdiv", {
				                              "type": "pie",
				                              "theme": "light",
				                              "dataProvider": chartData2,
				                              "valueField": "cnt",
				                              "titleField": "Status",
				                              "outlineAlpha": 0.4,
				                              "depth3D": 15,
				                              "balloonText": "[[title]]<br><span style='font-size:14px'><b>[[value]]</b> ([[percents]]%)</span>",
				                              "angle": 30,
				                              "export": {
				                                "enabled": true
				                              },
				                              "legend":{
				                            	  "position":"right",
						                            "marginRight":100,
						                            "autoMargins":false
						                            },
						                     "labelsEnabled" : false
				                            } );
				                               
				                        </script>
				                    </div>
				                </div>
				            </div>
				            <!-- /.box-body -->
				        </div>
		        	</section>
		        	<section class="col-lg-6 connectedSortable">
		        		<div class="box box-primary">
				            <div class="box-header with-border">
				                <h3 class="box-title">Employee Type wise</h3>
				            </div>
				            <!-- /.box-header -->
				            <div class="box-body" style="padding: 5px; overflow-y: auto;">
				                <div class="attendance row row_without_margin clr margintop20">
				                    <div class="col-lg-12 col-md-12 col-sm-12 col_no_padding">
				                        <div id="chartEmpTypediv" style="width:100%; height:300px;"></div>
				                        <script>
				                            var chart3;
				                            var chartData3 = [<%=sbEmpTypePie %>];
				                            var legend3;
				                            var chart = AmCharts.makeChart( "chartEmpTypediv", {
				                             "type": "pie",
				                             "theme": "light",
				                             "dataProvider": chartData3,
				                             "valueField": "cnt",
				                             "titleField": "Status",
				                             "outlineAlpha": 0.4,
				                             "depth3D": 15,
				                             "balloonText": "[[title]]<br><span style='font-size:14px'><b>[[value]]</b> ([[percents]]%)</span>",
				                             "angle": 30,
				                             "export": {
				                               "enabled": true
				                             },
				                            "legend":{
				                            "position":"right",
				                            "marginRight":100,
				                            "autoMargins":false
				                            },
				                            "labelsEnabled" : false
				                            });
				                        </script>
				                    </div>
				                </div>
				            </div>
				            <!-- /.box-body -->
				        </div>
		        	</section>
		        </div>
			<% } %>	
		    
		    <div class="row jscroll">
		    
		    <% if(strUserType != null && strUserType.equals(IConstants.MANAGER) && strBaseUserType != null && strBaseUserType.equals(IConstants.CEO)) { 
		    	if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_LEAVE_MANAGEMENT+"")>=0){
		    %>
				<section class="col-lg-6 connectedSortable">
					<div class="box box-default">
				 		<div class="box-header with-border">
		                    <h3 class="box-title">Employee Leave Details</h3>
		                    <%//System.out.println("sbLeaveDateMonth:"+request.getAttribute("sbLeaveDateMonth")); %>
	                    	<div class="box-tools pull-right">
		                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
		                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
		                    </div>
		            	</div>
		            
				         <div class="box-body" style="padding: 5px; overflow-y: auto;">
				            <div class="content1">
								<div id="container_leavestatus" style="height: 300px; width:95%; margin:10px 0 0 0px;">&nbsp;</div>
							</div>
				         </div>
				         <script type="text/javascript">
							$(document).ready(function() {
								
								chartAttendance = new Highcharts.Chart({
							   		
							      chart: {
							         renderTo: 'container_leavestatus',
							        	type: 'column'
							      },
							      title: {
							         text: 'Counter'
							      },
							      xAxis: {
							         categories: [<%=request.getAttribute("sbLeaveDateMonth")%>],
							         labels: {
							             rotation: -45,
							             align: 'right',
							             style: {
							                 font: 'normal 10px Verdana, sans-serif'
							             }
							          },
							         title: {
								            text: 'Date'
								         }
							      },
							      credits: {
							       	enabled: false
							   	  },
							      yAxis: {
							         min: 0,
							         title: {
							            text: 'Leaves'
							         }
							      },
							      plotOptions: {
							         column: {
							            pointPadding: 0.2,
							            borderWidth: 0
							         }
							      },
							     series:[
							        {
				                	 	name: 'Leave',
					                  	data: <%=request.getAttribute("sbLeaveCount")%>

							    	},
							    	{
					                    name: 'Travel',
					                    data: <%=request.getAttribute("sbTravelCount")%>
					                },
					                {
					                    name: 'Present',
					                    data: <%=request.getAttribute("sbPresentCount")%>
					                },
					                {
					                    name: 'Absent',
					                    data: <%=request.getAttribute("sbAbsentCount")%>
					                },
					                {
					                    name: 'OD',
					                    data: ''
					                },
					                {
					                    name: 'Extra Working',
					                    data: <%=request.getAttribute("sbExtaWorkingCount")%>
					                }
					              ]
							     
							   });
								
							});
							
						</script>
						
				          </div>
				   </section>         
				<% } } %>
				
		        <section class="col-lg-6 connectedSortable" style="padding-right: 0px;">
		        	<div class="box box-default">
                		<% String departCount = (String) request.getAttribute("departCount"); %>
		                <div class="box-header with-border">
		                    <h3 class="box-title">Departmental Spread</h3>
		                    <div class="box-tools pull-right">
		                    	<span class="badge bg-gray"><%=departCount %></span>
		                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
		                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
		                    </div>
		                </div>
		                <!-- /.box-header -->
		                <div class="box-body" style="padding: 5px; overflow-y: auto;">
		                	<div id="chartdiv" style="width:100%; height:300px;"></div>
				               <%
					               //Map<String, String> hmDepartOrgName = (Map<String, String>) request.getAttribute("hmDepartOrgName");
					               Map hmDepartmentEmployeeCount = (Map)request.getAttribute("hmDepartmentEmployeeCount");
					               if(hmDepartmentEmployeeCount==null)hmDepartmentEmployeeCount=new HashMap<String, Map<String, String>>();
					               Map hmDepartmentMap = (Map)request.getAttribute("hmDepartmentMap");
				               
								Set setDepartmentEmployeeCount = hmDepartmentEmployeeCount.keySet();
								Iterator itDepartmentEmployeeCount = setDepartmentEmployeeCount.iterator();
								while(itDepartmentEmployeeCount.hasNext()){
									String strDepartmentId = (String)itDepartmentEmployeeCount.next();
									if(hmDepartmentMap.get(strDepartmentId) != null) {
										/* String strDepart = (String)hmDepartmentMap.get(strDepartmentId);
										strDepart = strDepart.replace("'", "");
										System.out.println("strDepart ===>> " + strDepart); */
								%>
				               <%-- <div class="skill_div">
				                    <span class="sk_value" style="float: right;"><%=(String)hmDepartmentEmployeeCount.get(strDepartmentId) %></span>             
				                    <span class="sk_name" style="float: left; width: 79%;">
				                    <span style="float: left;"><%=(String)hmDepartmentMap.get(strDepartmentId) %></span>
				                    <br/>
				                    <span style="float: left; font-style: italic; font-size: 11px; margin-top: -4px;"><%=(String)hmDepartOrgName.get(strDepartmentId) %></span>
				                    </span>                
				               </div> --%> 
				               <script>
				               	   departments.push({"name": '<%=(String)hmDepartmentMap.get(strDepartmentId) %>',"count": '<%=(String)hmDepartmentEmployeeCount.get(strDepartmentId) %>'});
				               </script> 
								<% }
									}   
								%>
								<script>
								var chart = AmCharts.makeChart("chartdiv", {
									  "type": "pie",
									  "startDuration": 0,
									   "theme": "light",
									  "addClassNames": true,
									  "legend":{
									   	"position":"right",
									    "marginRight":100,
									    "autoMargins":false
									  },
									  "labelsEnabled": false,
									  "innerRadius": "30%",
									  "defs": {
									    "filter": [{
									      "id": "shadow",
									      "width": "200%",
									      "height": "200%",
									      "feOffset": {
									        "result": "offOut",
									        "in": "SourceAlpha",
									        "dx": 0,
									        "dy": 0
									      },
									      "feGaussianBlur": {
									        "result": "blurOut",
									        "in": "offOut",
									        "stdDeviation": 5
									      },
									      "feBlend": {
									        "in": "SourceGraphic",
									        "in2": "blurOut",
									        "mode": "normal"
									      }
									    }]
									  },
									  "dataProvider": departments,
									  "valueField": "count",
									  "titleField": "name",
									  "export": {
									    "enabled": true
									  }
									});
								</script>
		                </div>
		                <!-- /.box-body -->
		            </div>
		        </section>
		        <section class="col-lg-6 connectedSortable">
		        	<div class="box box-primary">
                	<% String skillCount = (String) request.getAttribute("skillCount"); %>
		                <div class="box-header with-border">
		                    <h3 class="box-title">Skills Spread</h3>
		                   <div class="box-tools pull-right">
		                    	<span class="badge bg-gray"><%=skillCount %></span>
		                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
		                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
		                    </div>
		                    
		                     
		                </div>
		                <!-- /.box-header -->
		                <div class="box-body" style="padding: 5px; overflow-y: auto;">
							<div id="chartSkillPiediv" style="width:100%; height:300px;"></div>
							<script>
					            var chart4;
					            var chartData4 = [<%=request.getAttribute("sbSkillwisePie") %>];
					            var legend4;
								
					            AmCharts.ready(function () {
					                // PIE CHART
					                chart4 = new AmCharts.AmPieChart(AmCharts.themes.light);
					                chart4.dataProvider = chartData4;
					                chart4.titleField = "Skill";
					                chart4.valueField = "cnt";
					                chart4.labelRadius = -30;
					                chart4.labelText = "";
					                chart4.balloonText = "[[title]]<br><span style='font-size:14px'><b>[[value]]</b> ([[percents]]%)</span>";
					 
					                // LEGEND
					               legend4 = new AmCharts.AmLegend();
					               legend4.align = "center";
					               legend4.markerType = "circle";
					               legend4.position = "right";
					               chart4.addLegend(legend4);
					               
					                
					                // WRITE
					                chart4.write("chartSkillPiediv");
					            });
					        </script>
		                </div>
		                <!-- /.box-body -->
		            </div>
		        </section>
		    </div>
		    <div class="row jscroll">
		        <section class="col-lg-4 connectedSortable">
		        	<%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT+"")>=0) { %>
		        	<div class="box box-info">
                		<% String corpGoalCount = (String) request.getAttribute("corpGoalCount"); %>
		                <div class="box-header with-border">
		                    <h3 class="box-title">Goals</h3>
		                    <div class="box-tools pull-right">
		                    	<span class="badge bg-blue"><%=corpGoalCount %></span>
		                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
		                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
		                    </div>
		                </div>
		                <!-- /.box-header -->
		                <div class="box-body" style="padding: 5px; overflow-y: auto; max-height: 250px;">
		                    <div class="content1">
								<ul class="issuereasons">
								<%
									String alltwoDeciTotProgressAvgCorporate = (String)request.getAttribute("alltwoDeciTotProgressAvgCorporate");
									String alltotal100Corporate = (String)request.getAttribute("alltotal100Corporate");
									String strtwoDeciTotCorporate = (String)request.getAttribute("strtwoDeciTotCorporate");
									
									String avgCorpGoalRating = (String)request.getAttribute("avgCorpGoalRating");
									
									double dblAvgCorpGoalRating = uF.parseToDouble(avgCorpGoalRating);
									//System.out.println("dblAvgCorpGoalRating ===>> " + dblAvgCorpGoalRating);
								%>
									<li>
										<%-- <div>
											<div id="starPrimaryCorpAllGoal" align="center"></div>
											<script type="text/javascript">
												$(function(){
													$('#starPrimaryCorpAllGoal').raty({
										        		readOnly: true,
										        		start: 0,
										        		half: true,
										        		targetType: 'number'
									        		});
												});
											</script>
										</div> --%>
										
										<div>
											<div class="progress" style="margin-right: 20px;margin-top: 20px;">
												<%if(uF.parseToDouble(alltwoDeciTotProgressAvgCorporate) < 33.33){ %> 
												<div class="progress-bar progress-bar-red" role="progressbar" style="width: <%=alltwoDeciTotProgressAvgCorporate %>%;">
												<%}else if(uF.parseToDouble(alltwoDeciTotProgressAvgCorporate) >= 33.33 && uF.parseToDouble(alltwoDeciTotProgressAvgCorporate) < 66.67){ %>
												<div class="progress-bar progress-bar-yellow" role="progressbar" style="width: <%=alltwoDeciTotProgressAvgCorporate %>%;">
												<%}else if(uF.parseToDouble(alltwoDeciTotProgressAvgCorporate) >= 66.67){ %>
												<div class="progress-bar progress-bar-green" role="progressbar" style="width: <%=alltwoDeciTotProgressAvgCorporate %>%;">
												<%} %>
									        	</div>
								            </div>
								            <%if(uF.parseToDouble(alltwoDeciTotProgressAvgCorporate) < 33.33){ %> 
												<span class="badge bg-red"><%=alltwoDeciTotProgressAvgCorporate %>% achieved</span>
												<%}else if(uF.parseToDouble(alltwoDeciTotProgressAvgCorporate) >= 33.33 && uF.parseToDouble(alltwoDeciTotProgressAvgCorporate) < 66.67){ %>
												<span class="badge bg-yellow"><%=alltwoDeciTotProgressAvgCorporate %>% achieved</span>
												<%}else if(uF.parseToDouble(alltwoDeciTotProgressAvgCorporate) >= 66.67){ %>
												<span class="badge bg-green"><%=alltwoDeciTotProgressAvgCorporate %>% achieved</span>
												<%} %>
										</div>
									</li>
								</ul>
								<div class="viewmore" style="clear: both;float: right;">
								
								<a href="GoalKRATargets.action?callFrom=GDash">
			           				<i class="fa fa-forward" aria-hidden="true" title="Go to Corporate Goals.."></i>
			           			</a>
			           			
			           			
			           			</div>	
							</div>
		                </div>
		                <!-- /.box-body -->
		            </div>
		        	<div class="box box-default">
                
		                <div class="box-header with-border">
		                    <h3 class="box-title">KRAs</h3>
		                    <div class="box-tools pull-right">
		                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
		                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
		                    </div>
		                </div>
		                <!-- /.box-header -->
		                <div class="box-body" style="padding: 5px; overflow-y: auto;">
		                    <div class="content1" style="padding: 5px;">
								<div id="chartPiediv" style="width:100%; height:350px;"></div>
								<div class="viewmore" ><a href="GoalKRATargets.action?callFrom=KTDash">
			           				<%--<img src="<%=request.getContextPath()%>/images1/icons/icons/forward_icon.png" title=""/> --%>
			           				
			           				<i class="fa fa-forward" aria-hidden="true" title="Go to KRAs.."></i>
			           			
			           			</a></div>
								<script>
						            var chart2;
						            var chartData2 = [<%=request.getAttribute("sbSlowSteadyMomentumPie") %>];
						            var legend2;
			
						            AmCharts.ready(function () {
						                // PIE CHART
						                chart2 = new AmCharts.AmPieChart(AmCharts.themes.light);
						                chart2.dataProvider = chartData2;
						                chart2.titleField = "Status";
						                chart2.valueField = "cnt";
						                chart2.labelRadius = -30;
						                chart2.labelText = "";
						                //chart2.innerRadius = '30%';
						                chart2.balloonText = "[[title]]<br><span style='font-size:14px'><b>[[value]]</b> ([[percents]]%)</span>";
						                // LEGEND
						               legend2 = new AmCharts.AmLegend();
						               legend2.align = "center";
						               legend2.markerType = "circle";
						               chart2.addLegend(legend2);
						                
						                // WRITE
						                chart2.write("chartPiediv");
						            });
						        </script>
							</div>
		                </div>
		                <!-- /.box-body -->
		            </div>
		            <div class="box box-success">
		                <% String myTargetsCnt = (String) request.getAttribute("myTargetsCnt"); %>
		                <div class="box-header with-border">
		                    <h3 class="box-title">Targets</h3>
		                    <div class="box-tools pull-right">
		                    	<span class="badge bg-green"><%=myTargetsCnt %></span>
		                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
		                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
		                    </div>
		                    
		                </div>
		                <!-- /.box-header -->
		                <div class="box-body" style="padding: 5px; overflow-y: auto;">
		                    <div class="content1" style="padding: 5px;">
								<div id="chartTargetDonutdiv" style="width:100%; height:300px;"></div>
								<script>
						            var chart5;
						            var chartData5 = [<%=request.getAttribute("sbTargetMissedAchieved")%>];
						          	  var legend5;
						
						            AmCharts.ready(function () {
						                // PIE CHART
						                chart5 = new AmCharts.AmPieChart(AmCharts.themes.light);
				
						                // title of the chart
						               // chart.addTitle("Collections", 14);
						                chart5.allLabels=[{
					                        "text": "Total Target",
					                        "align": "center",
					                        "bold": true,
					                        "y": 100
					                    },{
					                        "text": "targets",
					                        "align": "center",
					                        "bold": true,
					                        "size": 16,
					                        "y": 130
					                    }];
						                
						                chart5.theme="none";
						                chart5.dataProvider = chartData5;
						                chart5.titleField = "target";
						                chart5.valueField = "targetAmt";
						                chart5.sequencedAnimation = true;
						              	chart5.startEffect = "elastic";
						                chart5.innerRadius = "60%";
						                chart5.radius= "42%",
						                chart5.startDuration = 2;
						                chart5.labelRadius = -100; 
						                chart5.labelText = "";
						                chart5.balloonText = "[[title]]<br><span style='font-size:14px'><b>[[value]]</b> ([[percents]]%)</span>";
						 
						                // LEGEND
						                legend5 = new AmCharts.AmLegend();
						                legend5.align = "center";
						                legend5.markerType = "circle";
						                chart5.addLegend(legend5);
						                
						                // WRITE
						                chart5.write("chartTargetDonutdiv");
						            });
						        </script>
						        
						        <div class="viewmore"><a href="GoalKRATargets.action?callFrom=KTDash">
				       				<%-- <img src="<%=request.getContextPath()%>/images1/icons/icons/forward_icon.png" title="Go to Targets.."/>--%>
				       				<i class="fa fa-forward" aria-hidden="true" title="Go to Targets.."></i>
				       				
				       				
				       				
				       			</a></div>
							</div>
		                </div>
		                <!-- /.box-body -->
		            </div>
		            <% } %>
		        </section>
		        <section class="col-lg-4 connectedSortable col_no_padding">
		    		
		            
		            <%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT+"")>=0) { %>
		            <div class="box box-primary">
                
		                <div class="box-header with-border">
		                    <h3 class="box-title">Lifecycle Gaps</h3>
		                    <div class="box-tools pull-right">
		                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
		                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
		                    </div>
		                </div>
		                <!-- /.box-header -->
		                <div class="box-body" style="padding: 5px; overflow-y: auto;">
								<div id="lifecycleGapDiv" style="width:100%; height:300px;"></div>
								<script>
						            Highcharts.chart('lifecycleGapDiv', {
						                chart: {
						                    type: 'column'
						                },
						                title:{
						                    text: ''
						                },
						                xAxis: {
						                    type: 'category'
						                },
						                yAxis: {
						                    title: {
						                        text: 'No. of Employees'
						                    }
						                },
						                legend: {
						                    enabled: false
						                },
						                plotOptions: {
						                    series: {
						                        borderWidth: 0,
						                        dataLabels: {
						                            enabled: true,
						                            format: '{point.y:.1f}%'
						                        }
						                    }
						                },

						                tooltip: {
						                    headerFormat: '<span style="font-size:11px">{series.name}</span><br>',
						                    pointFormat: '<span style="color:{point.color}">{point.name}</span>: <b>{point.y:.2f}%</b> of total<br/>'
						                },

						                series: [{
						                    name: 'Brands',
						                    colorByPoint: true,
						                    data: [<%=request.getAttribute("sbLifecycleGaps")%>]
						                }]
						                
						            });
						        </script>
						        
						        <div class="viewmore">
						        	<a href="Learnings.action?callFrom=LDash">
				           				
				           				<i class="fa fa-forward" aria-hidden="true" title="Go to Lifecycle Gaps.."></i>
				           				
				           			</a>
				           		</div>
		                </div>
		                <!-- /.box-body -->
		            </div>
		            <% } %>
		            <%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_ONBOARDING+"")>=0) { %>
		            <div class="box box-warning">
		                <div class="box-header with-border">
		                    <h3 class="box-title">Hires vs Exits</h3>
		                    <div class="box-tools pull-right">
		                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
		                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
		                    </div>
		                </div>
		                <!-- /.box-header -->
		                <div class="box-body" style="padding: 5px; overflow-y: auto;">
								<div id="chartdiv3" style="width:100%; height:350px;"></div>
								<%//System.out.println("sbYearMonths:"+request.getAttribute("sbYearMonths")); %>
								<script>					            
						            
						            Highcharts.chart('chartdiv3', {
						                chart: {
						                    type: 'spline'
						                },
						                title: {
						                    text: ''
						                },
						                xAxis: {
						                    categories: <%=request.getAttribute("sbYearMonths")%>
						                },
						                yAxis: {
						                    title: {
						                        text: ''
						                    },
						                    labels: {
						                        formatter: function () {
						                            return this.value;
						                        }
						                    }
						                },
						                tooltip: {
						                    crosshairs: true,
						                    shared: true
						                },
						                plotOptions: {
						                    spline: {
						                        marker: {
						                            radius: 4,
						                            lineColor: '#666666',
						                            lineWidth: 1
						                        }
						                    }
						                },
						                series: [{
						                    name: 'Hires',
						                    data: <%=request.getAttribute("sbHires")%>

						                }, {
						                    name: 'Terminations',
						                    data: <%=request.getAttribute("sbTerminations")%>
						                }]
						            });
						        </script>
						        <%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_RECRUITMENT_MANAGEMENT+"")>=0){ %>
							        <div class="viewmore">
							        	<a href="EmployeeActivity.action">
					           				<%--<img src="<%=request.getContextPath()%>/images1/icons/icons/forward_icon.png" title="Go to Hires vs Exits.."/> --%>
					           				<i class="fa fa-forward" aria-hidden="true" title="Go to Hires vs Exits.."></i>
					           			</a>
					           		</div>
				           		<% } %>
		                </div>
		                <!-- /.box-body -->
		            </div>
		            <div class="box box-danger">
                		<% 	String strRequiredPosition = (String) request.getAttribute("strRequiredPosition");
							String strFulfilledPosition = (String) request.getAttribute("strFulfilledPosition");
							//System.out.println("strFulfilledPosition ===>> " + strFulfilledPosition);
							double dblFulfilledPercent = 0;
							if(uF.parseToInt(strRequiredPosition) > 0) {
								dblFulfilledPercent = (uF.parseToDouble(strFulfilledPosition) / uF.parseToDouble(strRequiredPosition)) * 100;
							}
						%>
		                <div class="box-header with-border">
		                    <h3 class="box-title">Recruitments</h3>
		                    <div class="box-tools pull-right">
		                    	<span class="badge bg-red"><%=uF.showData(strRequiredPosition, "0") %></span>
		                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
		                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
		                    </div>
		                </div>
		                <!-- /.box-header -->
		                <div class="box-body" style="padding: 5px; overflow-y: auto; max-height: 250px;">
		                    	<div style="color: gray;"> This is a progress bar of requirement fulfilled.</div>
		                    	<div class="progress" style="margin-left: 20px;margin-right: 20px;margin-top: 20px;">
		                    		<%if(dblFulfilledPercent < 33.33){ %> 
		                    			<div class="progress-bar progress-bar-red" role="progressbar" aria-valuemin="0" aria-valuemax="100" style="width: <%=uF.formatIntoOneDecimal(dblFulfilledPercent) %>%;" title="<%=uF.showData(strFulfilledPosition, "0") %> Candidates">
						                  <span><%=uF.formatIntoOneDecimal(dblFulfilledPercent) %>% Complete</span>
						                </div>
		                    		<%}else if(dblFulfilledPercent >= 33.33 && dblFulfilledPercent < 66.67){ %>
		                    			<div class="progress-bar progress-bar-yellow" role="progressbar" aria-valuemin="0" aria-valuemax="100" style="width: <%=uF.formatIntoOneDecimal(dblFulfilledPercent) %>%;" title="<%=uF.showData(strFulfilledPosition, "0") %> Candidates">
						                  <span><%=uF.formatIntoOneDecimal(dblFulfilledPercent) %>% Complete</span>
						                </div>
		                    		<%}else if(dblFulfilledPercent >= 66.67){ %>
		                    			<div class="progress-bar progress-bar-green" role="progressbar" aria-valuemin="0" aria-valuemax="100" style="width: <%=uF.formatIntoOneDecimal(dblFulfilledPercent) %>%;" title="<%=uF.showData(strFulfilledPosition, "0") %> Candidates">
						                  <span><%=uF.formatIntoOneDecimal(dblFulfilledPercent) %>% Complete</span>
						                </div>
		                    		<%} %>
									
					            </div>
					            <div style="width: 100%; margin-left: 25px;margin-top: 0px;">
									<div style="float: left; width: 35%; margin: 3px;"><span style="float: left; width: 20px; height: 20px; background-color: #EEEEEE;"></span><span style="float: left; margin-left: 10px;">Requirement</span></div>
									<div style="float: left; width: 35%; margin: 3px;">
									<span style="float: left; width: 6%; height: 20px; background-color: #dd4b39;"></span>
									<span style="float: left; width: 16px; height: 20px; background-color: #00a65a;">
									<span style="float: left; width: 40%; height: 20px; background-color: #FFE96F;"></span>
									</span><span style="float: left; margin-left: 10px;">Achieved</span>
									</div>
								</div>
	
				                <div class="viewmore clr" style="float: right;">
					                <a href="JobList.action">
				           				<%--<img src="<%=request.getContextPath()%>/images1/icons/icons/forward_icon.png" title="Go to Recruitments.."> --%>
				           				
				           				<i class="fa fa-forward" aria-hidden="true" title="Go to Recruitments.."></i>
				           				
				           				
				           				
				           			</a>
			           			</div>	
		                </div>
		                <!-- /.box-body -->
		            </div>
		            <% } %>
		            <%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_CAREER_DEV_PLANNING+"")>=0) { %>
		            	<div class="box box-success">
                			<% 	String learningCount = (String) request.getAttribute("learningCount");
								String assessCourseTrainingCount = (String) request.getAttribute("assessCourseTrainingCount");
								//System.out.println("strFulfilledPosition ===>> " + strFulfilledPosition);
								double dblTakenPercent = 0;
								if(uF.parseToInt(learningCount) > 0) {
									dblTakenPercent = (uF.parseToDouble(assessCourseTrainingCount) / uF.parseToDouble(learningCount)) * 100;
								}
							%>
			                <div class="box-header with-border">
			                    <h3 class="box-title">Learnings</h3>
			                    <div class="box-tools pull-right">
			                    	<span class="badge bg-green"><%=uF.showData(learningCount, "0") %></span>
			                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
			                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
			                    </div>
			                </div>
			                <!-- /.box-header -->
			                <div class="box-body" style="padding: 5px; overflow-y: auto; max-height: 250px;">
			                	<div style="color: gray;">Information about learning taken/attended.</div>
		                    	<div class="progress" style="margin-left: 20px;margin-right: 20px;margin-top: 20px;">
		                    		<%if(dblTakenPercent < 33.33){ %> 
		                    			<div class="progress-bar progress-bar-red" role="progressbar" aria-valuemin="0" aria-valuemax="100" style="width: <%=uF.formatIntoOneDecimal(dblTakenPercent) %>%;" title="<%=uF.showData(assessCourseTrainingCount, "0") %> Learnings">
						                  <span> <%=uF.formatIntoOneDecimal(dblTakenPercent) %>% Complete</span>
						                </div>
		                    		<%}else if(dblTakenPercent >= 33.33 && dblTakenPercent < 66.67){ %>
		                    			<div class="progress-bar progress-bar-yellow" role="progressbar" aria-valuemin="0" aria-valuemax="100" style="width: <%=uF.formatIntoOneDecimal(dblTakenPercent) %>%;" title="<%=uF.showData(assessCourseTrainingCount, "0") %> Learnings">
						                  <span> <%=uF.formatIntoOneDecimal(dblTakenPercent) %>% Complete</span>
						                </div>
		                    		<%}else if(dblTakenPercent >= 66.67){ %>
		                    			<div class="progress-bar progress-bar-green" role="progressbar" aria-valuemin="0" aria-valuemax="100" style="width: <%=uF.formatIntoOneDecimal(dblTakenPercent) %>%;" title="<%=uF.showData(assessCourseTrainingCount, "0") %> Learnings">
						                  <span> <%=uF.formatIntoOneDecimal(dblTakenPercent) %>% Complete</span>
						                </div>
		                    		<%} %>
									
					            </div>
					            <div style="width: 100%; margin-left: 25px;margin-top: 0px;">
									<div style="float: left; width: 35%; margin: 3px;"><span style="float: left; width: 20px; height: 20px; background-color: #EEEEEE;"></span><span style="float: left; margin-left: 10px;">Learnings</span></div>
									<div style="float: left; width: 35%; margin: 3px;">
									<span style="float: left; width: 6%; height: 20px; background-color: #dd4b39;"></span>
									<span style="float: left; width: 16px; height: 20px; background-color: #00a65a;">
									<span style="float: left; width: 40%; height: 20px; background-color: #FFE96F;"></span>
									</span><span style="float: left; margin-left: 10px;">Achieved</span>
									</div>
								</div>
	
				                <div class="viewmore clr" style="float: right;">
					                <a href="Learnings.action?callFrom=Dash">
				           				<%--<img src="<%=request.getContextPath()%>/images1/icons/icons/forward_icon.png" title="Go to Learnings.."> --%>
				           			<i class="fa fa-forward" aria-hidden="true" title="Go to Learnings.."></i>
				           			
				           			</a>
			           			</div>
			                </div>
			                <!-- /.box-body -->
			            </div>
		            <% } %>
		    	</section>
		        <section class="col-lg-4 connectedSortable">
		        	
		        	
		        	<%
					if(strUserType != null && (strUserType.equals(IConstants.HRMANAGER) || strUserType.equals(IConstants.ADMIN))){
			        	if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_TIME_AND_ATTENDANCE+"")>=0){ %>
			        	<div class="box box-danger">
			                <div class="box-header with-border">
			                    <h3 class="box-title">Attendance Summary</h3>
			                    <div class="box-tools pull-right">
			                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
			                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
			                    </div>
			                </div>
			                <!-- /.box-header -->
			                <div class="box-body" style="padding: 5px; overflow-y: auto;">
			                    <div class="content1">
									<div id="container_Attendance" style="height: 300px; width:95%; margin:10px 0 0 0px;"></div>
								</div>
			                </div>
			                <!-- /.box-body -->
			            </div>
			            <% } %>
			            <%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_LEAVE_MANAGEMENT+"")>=0){ %>
			            	<div class="box box-success">
				                <div class="box-header with-border">
				                    <h3 class="box-title">Leave Summary</h3>
				                    <div class="box-tools pull-right">
				                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
				                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
				                    </div>
				                </div>
				                <!-- /.box-header -->
				                <div class="box-body" style="padding: 5px; overflow-y: auto;">
				                    <div class="content1">
										<div id="containerForLeaveSumaryCharts" style="height: 250px; width:95%; margin:10px 0 0 0px;">&nbsp;</div>
									</div>
				                </div>
				                <!-- /.box-body -->
				            </div>
			            <% } %>	
		            	<%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_COMPENSATION_MANAGEMENT+"")>=0){ %>
			            	<div class="box box-info">
				                <div class="box-header with-border">
				                    <h3 class="box-title">Compensation Summary</h3>
				                    <div class="box-tools pull-right">
				                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
				                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
				                    </div>
				                </div>
				                <!-- /.box-header -->
				                <div class="box-body" style="padding: 5px; overflow-y: auto;">
				                    <div class="content1">
										<div id="containerForCompensationSumaryCharts" style="height: 250px; width:95%; margin:10px 0 0 0px;">&nbsp;</div>
									</div>
				                </div>
				                <!-- /.box-body -->
				            </div>
			            <% } %>
		            <% } %>
		            
		            <%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_CAREER_DEV_PLANNING+"")>=0) { 
						List<List<String>> recentAwardedEmpList = (List<List<String>>) request.getAttribute("recentAwardedEmpList");
					%>
						<div class="box box-default">
			                <div class="box-header with-border">
			                    <h3 class="box-title">Awarded Employees</h3>
			                    <div class="box-tools pull-right">
			                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
			                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
			                    </div>
			                </div>
			                <!-- /.box-header -->
			                <div class="box-body" style="padding: 5px; overflow-y: auto; max-height: 250px;">
			                    <div class="content1">
										<div class="holder" id="idCertificates">
										<ul class="issuereasons" style="float:left; margin: 9px 0px;">
								
										<%	for(int x=0; recentAwardedEmpList!=null && x<recentAwardedEmpList.size(); x++) { %>
												<li style="float:left;"><%=recentAwardedEmpList.get(x) %></li>
											<% } if(recentAwardedEmpList==null || recentAwardedEmpList.isEmpty() || recentAwardedEmpList.size()==0) { %>
												<li style="float:left;" class="nodata msg"> No one has earned any certificate from last one month. </li>
											<% } %>
										</ul>
										<%-- <% if(recentAwardedEmpList != null && !recentAwardedEmpList.isEmpty() && recentAwardedEmpList.size()>0) { %>
										<div class="viewmore">
							                <a href="MyHR.action?callFrom=LPDash">
						           				<img src="<%=request.getContextPath()%>/images1/icons/icons/forward_icon.png" title="Go to My Certificates.."/>
						           			</a>
					           			</div>
					           			<% } %> --%>
				                    </div>
				                </div>
			                </div>
			                <!-- /.box-body -->
			            </div>
					<% } %>
		        </section>
		    </div>
		</section>
</div>
<% } else { %>
<div class="leftbox reportWidth">
		<section class="content">
			<div class="row jscroll">
			<% if(uF.parseToBoolean(isEmpLimit)) { %>
				<div class="callout callout-warning" style="float: left; margin: 0px 15px 15px; padding: 7px 10px; font-weight: 600;">
					Dear Customer, You have exceeded your employee limit of <%=strEmpLimit %> employees. We have extended a grace period. Please contact <a href="mailto:accounts@workrig.com">accounts@workrig.com</a> to add additional slab for continued usage.
				</div>
			<% } %>
				<% if(strUserType != null && (strBaseUserType.equals(IConstants.CEO) || strUserType.equals(IConstants.HRMANAGER) || strUserType.equals(IConstants.ADMIN))) { %>
		        <section class="col-lg-8 connectedSortable" style="padding-right: 0px;"> 
		        <%}else{ %>
		        <section class="col-lg-12 connectedSortable">
		        <%} %>
		        	<div class="box box-primary">
		        		<%String wLocationCount = (String) request.getAttribute("wLocationCount"); %>
		                <div class="box-header with-border">
		                    <h3 class="box-title">Geographical Spread</h3>
		                    <div class="box-tools pull-right">
		                    	<span class="badge bg-blue"><%=wLocationCount %></span>
		                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
		                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
		                    </div>
		                </div>
		                
		                <!-- /.box-header -->
		                <div class="box-body" style="padding: 5px; overflow-y: auto;">
		                    <div class="pad">
			                    <!-- Map will be created here -->
			                    <div id="world-map" style="height: 312px;"></div>
			                  </div>
		                </div>
		                
		                <div class="box-footer">
			              <div class="row">
			              	<div class="col-sm-12 col-xs-12 col-md-12 col-lg-12">
			              		<table class="table table_no_border" style="margin-bottom: 0px;">
			              			<tr>
				              	<script src="js/jvectormap/jquery-jvectormap-1.2.2.min.js"></script>
								<%-- <script src="js/jvectormap/jquery-jvectormap-in-mill.js"></script> --%>
								<script src="js/jvectormap/jquery-jvectormap-world-mill-en.js"></script>		
				              	<% 
					               Map<String, String> hmWLocOrgName = (Map<String, String>) request.getAttribute("hmWLocOrgName");
					               Map hmWLocationEmployeeCount = (Map)request.getAttribute("hmWLocationEmployeeCount");
					               if(hmWLocationEmployeeCount==null)hmWLocationEmployeeCount=new HashMap<String, Map<String, String>>();
					               Map hmWorkLocationMap = (Map)request.getAttribute("hmWorkLocationMap");
					               
									Set setWLocationEmployeeCount = hmWLocationEmployeeCount.keySet();
									Iterator itWLocationEmployeeCount = setWLocationEmployeeCount.iterator();
									while(itWLocationEmployeeCount.hasNext()){
										String strWLocationId = (String)itWLocationEmployeeCount.next();
										
										Map hmWLocation = (Map)hmWorkLocationMap.get(strWLocationId);
										if(hmWLocation==null)hmWLocation=new HashMap();
										if(hmWLocation != null && !hmWLocation.isEmpty()) {
								%>	
								<td>
			              			<div class="description-block border-right">
					                    <span class="description-percentage text-green"><b><%=(String)hmWLocationEmployeeCount.get(strWLocationId) %></b></span>
					                    <h5 class="description-header"><%=(String)hmWLocation.get("WL_CITY")+", "+(String)hmWLocation.get("WL_COUNTRY") %></h5>
					                    <span class="description-text"><%=(String)hmWLocOrgName.get(strWLocationId) %></span>
					                  </div>	
			              		</td>
				               <script>
				               $.ajax({
				            	   url:"https://maps.googleapis.com/maps/api/geocode/json?address="+encodeURIComponent('<%=(String)hmWLocation.get("WL_CITY")%>')+"&key=AIzaSyBIl1nxilzv8l1GDupibf73zUxnnNBjnic",
								  dataType: 'json',
				            	   async: false,
				            	   success: function(val) {
				            		   if(val.results.length) {
						            	      var location = val.results[0].geometry.location;
						            	      cities.push({latLng:[location.lat,location.lng], name: '<%=(String)hmWLocation.get("WL_CITY")%>'});
						            	    }
				            	   }
				            	 });
				               		
				               </script>
								<%   }
									}
								%> 
								<script>
											$('#world-map').vectorMap({
											    map: 'world_mill_en',
											    backgroundColor: "transparent",
											    regionStyle: {
											    	initial: {
												        fill: '#e4e4e4',
												        "fill-opacity": 1,
												        "stroke-width": 1,
												        "stroke-opacity": 1
												      }
											    },
											    normalizeFunction: 'polynomial',  
											    markerStyle: {
											        initial: {
											            fill: '#F8E23B',
											            stroke: '#383f47'
											        }
											    },
											    markers:cities.map(function(h) {
											        return {
											           	latLng: h.latLng,
											            name: h.name
											        }
											    })
											  });
										</script>
									</tr>
			              		</table>
			              	</div>
			              </div>
			              <!-- /.row -->
			            </div>
		                <!-- /.box-body -->
		            </div>
		        </section>
		        <% if(strUserType != null && (strBaseUserType.equals(IConstants.CEO) || strUserType.equals(IConstants.HRMANAGER) || strUserType.equals(IConstants.ADMIN))) { %>
		        <section class="col-lg-4 connectedSortable">
		        	
					<% if(arrEnabledModules!=null && (ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PEOPLE_MANAGEMENT+"")>=0  || ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT+"")>=0 || ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_ONBOARDING+"")>=0)) {
						String gapCount = (String) request.getAttribute("gapCount");
						String strCurrJobProfiles = (String) request.getAttribute("strCurrJobProfiles");
						String strNewRequisitons = (String) request.getAttribute("strNewRequisitons");
						String totalInduction = (String) request.getAttribute("totalInduction");
						String allResigCnt = (String) request.getAttribute("allResigCnt");
						String retirementCnt = (String) request.getAttribute("retirementCnt");
						String allFinalDayCnt = (String) request.getAttribute("allFinalDayCnt");
						String confirmCnt = (String) request.getAttribute("confirmCnt");
					//	System.out.println("totalInduction==>"+totalInduction+"==>confirmCnt==>"+confirmCnt+"==>allResigCnt==>"+allResigCnt+"==>allFinalDayCnt==>"+allFinalDayCnt);
						int totActivityCnt = uF.parseToInt(totalInduction) + uF.parseToInt(retirementCnt) + uF.parseToInt(allResigCnt) + uF.parseToInt(allFinalDayCnt) + uF.parseToInt(confirmCnt);
					//	System.out.println("totActivityCnt==>"+totActivityCnt);
						int totAlertCount = uF.parseToInt(gapCount) + uF.parseToInt(strCurrJobProfiles) + uF.parseToInt(strNewRequisitons) + totActivityCnt;
					%> 
		            <div class="box box-info">
                
		                <div class="box-header with-border">
		                    <h3 class="box-title">HR Alerts</h3>
		                    <div class="box-tools pull-right">
		                    	<span class="badge bg-blue"><%=totAlertCount %></span>
		                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
		                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
		                    </div>
		                </div>
		                <!-- /.box-header -->
		                <div class="box-body" style="padding: 5px; overflow-y: auto; max-height: 250px;">
		                    <div style="width:100%;">
							
								<%-- <div style="padding: 3px; margin: 3px; width: 97%; border: 1px solid #CCCCCC; border-radius: 5px; text-align: center; background-color: #FCFCFC;">
									<div style="font-size: 30px; line-height: 23px;"><%=feedCount %></div>
									<div style="font-weight: bold; color: gray;">UNREAD FEED</div>   
								</div> --%>
								<ul class="site-stats">
									<% if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT+"")>=0) { %>
										<a href="Learnings.action?callFrom=LDash">
											<li class="bg_lh"><i class="fa fa-leanpub" aria-hidden="true"></i><strong><%=gapCount %></strong> <small>Learning Gaps</small></li>
										</a>
									<% } %>	
									<% if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_RECRUITMENT_MANAGEMENT+"")>=0) { %>
										<a href="RequirementDashboard.action">
											<li class="bg_lh"><i class="fa fa-outdent" aria-hidden="true"></i><strong><%=strNewRequisitons %></strong> <small>New Requisitons</small></li>
										</a>
									<% } %>
									<% if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_RECRUITMENT_MANAGEMENT+"")>=0) { %>
										<a href="RequirementDashboard.action?callFrom=HRDashJobApproval">
											<li class="bg_lh"><i class="fa fa-briefcase" aria-hidden="true"></i><strong><%=strCurrJobProfiles %></strong> <small>Job Profiles</small></li>
										</a>
									<% } %>
								</ul>
								
								<ul class="site-stats">
									<%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PEOPLE_MANAGEMENT+"")>=0){ %>
										<a href="javascript:void(0);" onclick="viewAllActivities();">
											<li class="bg_lh"><i class="fa fa-calendar-o" aria-hidden="true"></i><strong><%=totActivityCnt %></strong> <small>Activities</small></li>
										</a>
									<% } %> 
								</ul>
								
	                    	</div>
		                </div>
		                <!-- /.box-body -->
		            </div>
		            <% } %>
		            <% if(arrEnabledModules!=null && (ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT+"")>=0 || ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_ONBOARDING+"")>=0)) {
							String reviewEmpCount = (String) request.getAttribute("reviewEmpCount");
							String kraFormCount = (String) request.getAttribute("kraFormCount");
							String interviewCount = (String) request.getAttribute("interviewCount");
							
							int totFormCount = uF.parseToInt(reviewEmpCount) + uF.parseToInt(kraFormCount) + uF.parseToInt(interviewCount);
						%> 
		            <div class="box box-success">
		                <div class="box-header with-border">
		                    <h3 class="box-title">HR Forms</h3>
		                    <div class="box-tools pull-right">
		                    	<span class="badge bg-green"><%=totFormCount %></span>
		                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
		                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
		                    </div>
		                </div>
		                <!-- /.box-header -->
		                <div class="box-body" style="padding: 5px; overflow-y: auto; max-height: 250px;">
	                    	<ul class="site-stats">
								<% if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT+"")>=0) { %>
									<a href="Reviews.action?callFrom=FDash">
										<li class="bg_lh"><i class="fa fa-signal" aria-hidden="true"></i><strong><%=reviewEmpCount %></strong> <small>HR Reviews</small></li>
									</a>
									<a href="GoalKRATargets.action?callFrom=KTDash">
										<li class="bg_lh"><i class="fa fa-signal" aria-hidden="true"></i><strong><%=kraFormCount %></strong> <small>KRA Reviews</small></li>
									</a>
								<% } %>	
								<% if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_RECRUITMENT_MANAGEMENT+"")>=0) { %>
									<a href="Login.action?role=3&userscreen=interviews">
										<li class="bg_lh"><i class="fa fa-calendar" aria-hidden="true"></i><strong><%=interviewCount %></strong> <small>Interviews (1w)</small></li>
									</a>
								<% } %>
							</ul>
		                </div>
		                <!-- /.box-body -->
		            </div>
		            <% } %>
		            <%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_TIME_AND_ATTENDANCE+"")>=0){ 
			
						String exceptionCount = (String) request.getAttribute("exceptionCount");
						
						int totExceptionCount = uF.parseToInt(exceptionCount);
					%> 
		            <div class="box box-warning">
		                <div class="box-header with-border">
		                    <h3 class="box-title">Time Exceptions</h3>
		                    <div class="box-tools pull-right">
		                    	<span class="badge bg-yellow"><%=totExceptionCount %></span>
		                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
		                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
		                    </div>
		                </div>
		                <!-- /.box-header -->
		                <div class="box-body" style="padding: 5px; overflow-y: auto; max-height: 250px;">
		                    <ul class="site-stats">
								<a href="TimeApprovals.action?callFrom=HRDashTimeExceptions">
									<li class="bg_lh" style="width: 92%;"><i class="fa fa-exclamation-circle" aria-hidden="true"></i><strong><%=uF.parseToInt(exceptionCount) %></strong> <small>Time Exceptions</small></li>
								</a>
	                    	</ul>
		                </div>
		                <!-- /.box-body -->
		            </div>
		            <% } %>
		            
		        </section>
		        <% } %>
		    </div>
		    <div class="row jscroll">
		    <% if(strUserType != null && strUserType.equals(IConstants.MANAGER) && strBaseUserType != null && strBaseUserType.equals(IConstants.CEO)) { 
		    	if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_LEAVE_MANAGEMENT+"")>=0){
		    %>
		    	<section class="col-lg-6 connectedSortable">
					<div class="box box-default">
				 		<div class="box-header with-border">
		                    <h3 class="box-title">Employee Leave Details</h3>
		                    <%//System.out.println("sbLeaveDateMonth:"+request.getAttribute("sbLeaveDateMonth")); %>
	                    	<div class="box-tools pull-right">
		                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
		                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
		                    </div>
		            	</div>
		            
				         <div class="box-body" style="padding: 5px; overflow-y: auto;">
				            <div class="content1">
								<div id="container_leavestatus" style="height: 300px; width:95%; margin:10px 0 0 0px;">&nbsp;</div>
							</div>
				         </div>
				         <script type="text/javascript">
							$(document).ready(function() {
								
								chartAttendance = new Highcharts.Chart({
							      chart: {
							         renderTo: 'container_leavestatus',
							        	type: 'column'
							      },
							      title: {
							         text: 'Counter'
							      },
							      xAxis: {
							         categories: [<%=request.getAttribute("sbLeaveDateMonth")%>],
							         labels: {
							             rotation: -45,
							             align: 'right',
							             style: {
							                 font: 'normal 10px Verdana, sans-serif'
							             }
							          },
							         title: {
								            text: 'Date'
								         }
							      },
							      credits: {
							       	enabled: false
							   	  },
							      yAxis: {
							         min: 0,
							         title: {
							            text: 'Leaves'
							         }
							      },
							      plotOptions: {
							         column: {
							            pointPadding: 0.2,
							            borderWidth: 0
							         }
							      },
							     series:[{
				                	 	name: 'Leave',
					                  	data: <%=request.getAttribute("sbLeaveCount")%>

							    	},
							    	{
					                    name: 'Travel',
					                    data: <%=request.getAttribute("sbTravelCount")%>
					                },
					                {
					                    name: 'Present',
					                    data: <%=request.getAttribute("sbPresentCount")%>
					                },
					                {
					                    name: 'Absent',
					                    data: <%=request.getAttribute("sbAbsentCount")%>
					                },
					                {
					                    name: 'OD',
					                    data: ''
					                },
					                {
					                    name: 'Extra Working',
					                    data: <%=request.getAttribute("sbExtaWorkingCount")%>
					                }]
							     
							   });
								
							});
							
						</script>
						
				          </div>
				   </section>
				<% } } %>
				
		        <section class="col-lg-6 connectedSortable" style="padding-right: 0px;">
		        	<div class="box box-default">
                		<% String departCount = (String) request.getAttribute("departCount"); %>
		                <div class="box-header with-border">
		                    <h3 class="box-title">Departmental Spread</h3>
		                    <div class="box-tools pull-right">
		                    	<span class="badge bg-gray"><%=departCount %></span>
		                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
		                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
		                    </div>
		                </div>
		                <!-- /.box-header -->
		                <div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 400px;">
		                	<% if(departCount.equals(0)){ %>
		                		<div style="padding: 20px;">No Data To Display</div>
		                	<%}else{ %>
		                	<div id="chartdiv" style="width:100%; height:300px;"></div>
				               <%
					               //Map<String, String> hmDepartOrgName = (Map<String, String>) request.getAttribute("hmDepartOrgName");
					               Map hmDepartmentEmployeeCount = (Map)request.getAttribute("hmDepartmentEmployeeCount");
					               if(hmDepartmentEmployeeCount==null)hmDepartmentEmployeeCount=new HashMap<String, Map<String, String>>();
					               Map hmDepartmentMap = (Map)request.getAttribute("hmDepartmentMap");
				               
								Set setDepartmentEmployeeCount = hmDepartmentEmployeeCount.keySet();
								Iterator itDepartmentEmployeeCount = setDepartmentEmployeeCount.iterator();
								while(itDepartmentEmployeeCount.hasNext()){
									String strDepartmentId = (String)itDepartmentEmployeeCount.next();
									if(hmDepartmentMap.get(strDepartmentId) != null) {
								%>
				               <script>
				               	   departments.push({"name": '<%=(String)hmDepartmentMap.get(strDepartmentId) %>',"count": '<%=(String)hmDepartmentEmployeeCount.get(strDepartmentId) %>'});
				               </script> 
								<% }
									}   
								%>
								<script>
								var chart = AmCharts.makeChart("chartdiv", {
									  "type": "pie",
									  "startDuration": 0,
									   "theme": "light",
									  "addClassNames": true,
									  "legend":{
									   	"position":"right",
									    "marginRight":100,
									    "autoMargins":false
									  },
									  "labelsEnabled": false,
									  "innerRadius": "30%",
									  "defs": {
									    "filter": [{
									      "id": "shadow",
									      "width": "200%",
									      "height": "200%",
									      "feOffset": {
									        "result": "offOut",
									        "in": "SourceAlpha",
									        "dx": 0,
									        "dy": 0
									      },
									      "feGaussianBlur": {
									        "result": "blurOut",
									        "in": "offOut",
									        "stdDeviation": 5
									      },
									      "feBlend": {
									        "in": "SourceGraphic",
									        "in2": "blurOut",
									        "mode": "normal"
									      }
									    }]
									  },
									  "dataProvider": departments,
									  "valueField": "count",
									  "titleField": "name",
									  "export": {
									    "enabled": true
									  }
									});
								</script>
							<% } %>
		                </div>
		                <!-- /.box-body -->
		            </div>
		        </section>
		        <section class="col-lg-6 connectedSortable">
		        	<div class="box box-primary">
                	<% String skillCount = (String) request.getAttribute("skillCount"); %>
		                <div class="box-header with-border">
		                    <h3 class="box-title">Skills Spread</h3>
		                    <span class="badge bg-gray"><%=skillCount%></span>
		                    <div class="box-tools pull-right">
		                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
		                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
		                    </div>
		                </div>
		                <!-- /.box-header -->
		                <div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 400px;">
		                		<% if(skillCount.equals(0)){ %>
		                		<div style="padding: 20px;">No Data To Display</div>
		                		<%}else{ %>
								<div id="chartSkillPiediv" style="width:100%; height:336px;"></div>
								<script>
						            var chart4;
						            var chartData4 = [<%=request.getAttribute("sbSkillwisePie") %>];
						            var legend4;
									
						            AmCharts.ready(function () {
						                // PIE CHART
						                chart4 = new AmCharts.AmPieChart(AmCharts.themes.light);
						                chart4.dataProvider = chartData4;
						                chart4.titleField = "Skill";
						                chart4.valueField = "cnt";
						                chart4.labelRadius = -30;
						                chart4.labelText = "";
						                chart4.balloonText = "[[title]]<br><span style='font-size:14px'><b>[[value]]</b> ([[percents]]%)</span>";
						 
						                // LEGEND
						               legend4 = new AmCharts.AmLegend();
						               legend4.align = "center";
						               legend4.markerType = "circle";
						               chart4.addLegend(legend4);
						                
						                // WRITE
						                chart4.write("chartSkillPiediv");
						            });
						        </script>
						        <% } %>
		                </div>
		                <!-- /.box-body -->
		            </div>
		        </section>
		    </div>
		    <div class="row jscroll">
		        <section class="col-lg-4 connectedSortable">
		        	<%
					if(strUserType != null && (strUserType.equals(IConstants.HRMANAGER) || strUserType.equals(IConstants.ADMIN))){
			        	if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_TIME_AND_ATTENDANCE+"")>=0){ %>
			        	<div class="box box-danger">
			                <div class="box-header with-border">
			                    <h3 class="box-title">Attendance Summary</h3>
			                    <div class="box-tools pull-right">
			                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
			                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
			                    </div>
			                </div>
			                <!-- /.box-header -->
			                <div class="box-body" style="padding: 5px; overflow-y: auto;">
			                    <div class="content1">
									<div id="container_Attendance" style="height: 300px; width:95%; margin:10px 0 0 0px;"></div>
								</div>
			                </div>
			                <!-- /.box-body -->
			            </div>
			            <% }
			        	}%>
		        </section>
		        <section class="col-lg-4 connectedSortable col_no_padding">
		            <%
					if(strUserType != null && (strUserType.equals(IConstants.HRMANAGER) || strUserType.equals(IConstants.ADMIN))){
			            if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_LEAVE_MANAGEMENT+"")>=0){ %>
			            	<div class="box box-success">
				                <div class="box-header with-border">
				                    <h3 class="box-title">Leave Summary</h3>
				                    <div class="box-tools pull-right">
				                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
				                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
				                    </div>
				                </div>
				                <!-- /.box-header -->
				                <div class="box-body" style="padding: 5px; overflow-y: auto;">
				                    <div class="content1">
										<div id="containerForLeaveSumaryCharts" style="height: 250px; width:95%; margin:10px 0 0 0px;">&nbsp;</div>
									</div>
				                </div>
				                <!-- /.box-body -->
				            </div>
			            <% } 
			            }%>
		    	</section>
		        <section class="col-lg-4 connectedSortable">
		        	
		        </section>
		    </div>
		</section>
</div>
<% } %>
<div class="modal" id="modalInfo" role="dialog">
    <div class="modal-dialog">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">Candidate Information</h4>
            </div>
            <div class="modal-body" style="height:400px;overflow-y:auto;padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>


