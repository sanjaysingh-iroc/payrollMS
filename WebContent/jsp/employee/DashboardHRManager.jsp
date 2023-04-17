<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="java.util.*, ChartDirector.*"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>



<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
 

<%  	Map<String,Integer> hmChart1=(Map)request.getAttribute("hmchart1");
		if(hmChart1==null)hmChart1 = new HashMap();
		Map<String,Integer> hmChart2=(Map)request.getAttribute("hmchart2");
		if(hmChart2==null) hmChart2 = new HashMap();
		List alUserModulesList = (List)request.getAttribute("alUserModulesList");
		List alProbationEndDate = (List)request.getAttribute("alProbationEndDate");
		UtilityFunctions uF = new UtilityFunctions();
%>

   

<script type="text/javascript">
jQuery(document).ready(function() { 
  jQuery(".content1").show();
  //toggle the componenet with class msg_body
  jQuery(".heading").click(function()
  {
    jQuery(this).next(".content1").slideToggle(500);
  });
});




<%if(alUserModulesList!=null && alUserModulesList.contains(IConstants.MODULE_ONBOARDING)){ %>

Highcharts.setOptions({
    colors: ['#00FF00','#FF6633']
});

$(document).ready(function() {
	var chart = new Highcharts.Chart({
	      
		chart: { 
				renderTo: 'applicationFinalstats',
	        	plotBackgroundColor: null,
	        	plotBorderWidth: null,
	        	plotShadow: false
		     },
     	credits: {
		         	enabled: false
		     },
		title: {
		   	  		text : '',
		       		floating: true
		     },
			tooltip: {
		   	   formatter: function() {
		           return '<b>'+ this.point.name +'</b>: '+ this.percentage +' %';
		        }
		  	},
		 	legend: {
		        enabled: true
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
	    	  
	          type: 'pie',
	          name: 'Browser share',
	          data:[
		             ['Accepted', <%= hmChart1.get("acceptedAppl")!=null && !hmChart1.get("acceptedAppl").equals("") ? hmChart1.get("acceptedAppl") : "0"  %>],
		             ['Rejected', <%= hmChart1.get("rejectedAppl")!=null && !hmChart1.get("rejectedAppl").equals("") ? hmChart1.get("rejectedAppl") : "0"  %>],
		             ['Application', <%= hmChart1.get("underprocessAppl")!=null && !hmChart1.get("underprocessAppl").equals("") ? hmChart1.get("underprocessAppl") : "0"  %>]
	         ]
	       }]
	   });
});



$(document).ready(function() {
	   var chart = new Highcharts.Chart({
	      
		   chart: { 
				renderTo: 'offerFinalStats',
	        	plotBackgroundColor: null,
	        	plotBorderWidth: null,
	        	plotShadow: false
		     },
    	credits: {
		         	enabled: false
		     },
		title: {
		   	  		text : '',
		       		floating: true
		     },
			tooltip: {
		   	   formatter: function() {
		           return '<b>'+ this.point.name +'</b>: '+ this.percentage +' %';
		        }
		  	},
		 	legend: {
		        enabled: true
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
	    	  
	          type: 'pie',
	          name: 'Browser share',
	          data: [
	
		             ['Offer Rejected', <%= hmChart2.get("rejectedCand")!=null && !hmChart2.get("rejectedCand").equals("") ? hmChart2.get("rejectedCand") : "0"%>],
		             ['Offer Accepted ', <%= hmChart2.get("acceptedCand")!=null && !hmChart2.get("acceptedCand").equals("") ? hmChart2.get("acceptedCand") : "0"%>],
		             ['Offer Under Process', <%=hmChart2.get("underprocessCand")!=null && !hmChart2.get("underprocessCand").equals("") ? hmChart2.get("underprocessCand") : "0" %>]
	         ]
	       }]
	   });
});

<%}%>


$(document).ready(function() {
var chart = new Highcharts.Chart({
    
	   chart: {
      renderTo: 'containerForCharts',
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
      categories: ['Exceptions', 'Reimbursements', 'Leaves']
   },      
   yAxis: {
 	  min: 0,
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
 	  column: {
       pointPadding: 0.2,
       borderWidth: 0
    }
   },
   
   series: [{
       name: 'Pending for approval',
       data: [<%=uF.showData((String)request.getAttribute("EXCEP_WAITING_COUNT"),"0")%>,<%=uF.showData((String)request.getAttribute("REIMB_WAITING_COUNT"),"0")%>,<%=uF.showData((String)request.getAttribute("LEAVE_PENDING_COUNT"),"0")%>] 
		 
 
    }]
});
});


</script>
 <script type="text/javascript">
 function approveLeave(E,LID) {
		removeLoadingDiv('the_div');
		
		var dialogEdit = '#approveLeaveDiv';
		var data1 = "<div id=\"the_div\"><div id=\"ajaxLoadImage\"></div></div>";
		dialogEdit = $(data1).appendTo('body');
		$(dialogEdit).dialog(
				{
					autoOpen : false,
					bgiframe : true,
					resizable : false,
					height : 650,
					width : 800,
					modal : true,
					title : 'Approve Leave',
					open : function() {
						var xhr = $.ajax({
							url : "ManagerLeaveApproval.action?type=type&E="+E+"&LID="+LID,
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
 
<%

AngularMeter semiWorkedAbsent = (AngularMeter)request.getAttribute("KPI_BEST");
String semiBestKPI1URL = (String)semiWorkedAbsent.makeSession(request, "KPI_BEST");
Map hmEmployeeMap = (Map)request.getAttribute("hmEmployeeMap");
Map hmEmpDesigMap = (Map)request.getAttribute("hmEmpDesigMap");
Map hmEmpProfileImageMap = (Map)request.getAttribute("hmEmpProfileImageMap");
Map hmServicesMap = (Map)request.getAttribute("hmServicesMap");
Map hmWorkLocationMap = (Map)request.getAttribute("hmWorkLocationMap");

List alBirthDays = (List)request.getAttribute("alBirthDays");
if(alBirthDays==null)alBirthDays=new ArrayList();

List alEvents = (List)request.getAttribute("alEvents");
if(alEvents==null)alEvents=new ArrayList();



Map hmTopEmployees = (Map)request.getAttribute("hmTopEmployees");
if(hmTopEmployees==null)hmTopEmployees=new HashMap<String, Map<String, String>>();


Map hmSkillsEmployeeCount = (Map)request.getAttribute("hmSkillsEmployeeCount");
if(hmSkillsEmployeeCount==null)hmSkillsEmployeeCount=new HashMap<String, Map<String, String>>();

Map hmServicesEmployeeCount = (Map)request.getAttribute("hmServicesEmployeeCount");
if(hmServicesEmployeeCount==null)hmServicesEmployeeCount=new HashMap<String, Map<String, String>>();

Map hmWLocationEmployeeCount = (Map)request.getAttribute("hmWLocationEmployeeCount");
if(hmWLocationEmployeeCount==null)hmWLocationEmployeeCount=new HashMap<String, Map<String, String>>();


%>



<script type="text/javascript" src="scripts/chart/jquery.min.js"></script>
<script type="text/javascript" src="scripts/chart/highcharts.js"></script>

<style>
.counterLabel{
	font-family:Verdana;
	font-size:20px; 
	text-align:right; 
	padding-right:10px;
}

.counterText{
	font-family:Digital;
	font-size:20px; 
	text-align:right; 
	padding-right:10px;
}
</style>
 
<script type="text/javascript" >
var chartAttendance;
var chartAttendance1;
var chart1;
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
	
	
	chartAttendance1 = new Highcharts.Chart({
   		
	      chart: {
	         renderTo: 'container_Attendance1',
	        	type: 'pie'
	      },
	      title: {
	         text: null
	      },
	      tooltip: {
	          formatter: function() {
	             return '<b>'+ this.point.name +'</b>: '+ this.percentage +' %';
	          }
	       },
	       legend: {
		        enabled: true
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
	          type: 'pie',
	          name: 'Attendance',
	          data: [<%=request.getAttribute("CHART_WORKED_ABSENT_C")%> ]
	       }]
	     
	   });
	
	
	
	
	
	
});





</script>

<script>
function showClockMessage() {
	dojo.event.topic.publish("showClockMessage");
}
function showClockLabel() {
	dojo.event.topic.publish("showClockLabel");
}
 
</script>

<%
	XYChart xyApprovals = (XYChart)request.getAttribute("CHART_APPROVALS");
	String xyApprovals1URL = (String)xyApprovals.makeSession(request, "chart2");
	String xyApprovalsURLMap1 = xyApprovals.getHTMLImageMap("", "", "title='{xLabel}: {value}'");

	Map<String,List<String>> hmThoughts = (Map<String, List<String>>) request.getAttribute("hmthoughts");
	Map hmMyAttendence = (HashMap) request.getAttribute("hmMyAttendence");
	List alReasons = (List)request.getAttribute("alReasons");
	List alLeaves = (List)request.getAttribute("alLeaves");
	List alLeaveRequest = (List)request.getAttribute("alLeaveRequest");
	List alRequisitionRequest = (List)request.getAttribute("alRequisitionRequest");
	List alReimbursementRequest = (List)request.getAttribute("alReimbursementRequest");
	
	if (hmMyAttendence == null) {
		hmMyAttendence = new HashMap();
	}
	if(alReasons==null){
		alReasons = new ArrayList();
	}
	if(alLeaves==null){
		alLeaves = new ArrayList();
	}
	if(alLeaveRequest==null){
		alLeaveRequest = new ArrayList();
	}
	if(alRequisitionRequest==null){
		alRequisitionRequest = new ArrayList();
	}
	if(alReimbursementRequest==null){
		alReimbursementRequest = new ArrayList();
	}
%>


<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="HR Manager's Dashboard" name="title"/>
</jsp:include>


<div class="leftbox reportWidth">
	<div id="left">
		<div id="clockcontainer">
			<p class="past heading">Employee Attendance Summary</p>
			<div class="content1">
				<div id="container_Attendance" style="height: 300px; width:95%; margin:10px 0 0 0px;"></div>
			</div>
		</div>
		
		<%-- <div id="clockcontainer">
			<p class="past heading">My staff's attendance (last 1 month)</p>
			<div class="content1">
				<table cellpadding="0" cellspacing="0" width="90%" align="center">
			      
			          <tr>
			              <td width="170px" class="tdDashLabel">Worked</td>
			              <td class="tdDashValue"><%=request.getAttribute("PRESENT_COUNT")%></td>
			          </tr>
			      
			          <tr>
			              <td class="tdDashLabel">Absent</td>
			              <td class="tdDashValue"><%=request.getAttribute("ABSENT_COUNT")%></td>
			          </tr>
			      
			      <tr>
			      <td colspan="2">
			      
			      	<div id="container_Attendance1" style="width:300px;height:200px"></div>
			      
			      </td>
			      </tr>
			      
			      </table>
		      </div>
		</div> --%>
		
		
		
		<div id="clockcontainer">
			<p class="past heading">Upcoming events and birthdays</p>
				<div class="content1">
				<%if(request.getAttribute("DAY_THOUGHT_TEXT")!=null){ %>
                <p class="thought">
                  <span> <%=request.getAttribute("DAY_THOUGHT_TEXT") %></span> 
                  <br/>
                   <span style="float:right;font-style:italic">- <strong><%=request.getAttribute("DAY_THOUGHT_BY") %></strong></span>
                </p>
                <%} 
			
					Map<String,List<String>> hmEventUpdates = (Map<String,List<String>>) request.getAttribute("eventUpdates");
					Map<String,List<String>> hmQuoteUpdates = (Map<String,List<String>>) request.getAttribute("quoteUpdates");
					Map<String,List<String>> hmNoticeUpdates = (Map<String,List<String>>) request.getAttribute("noticeUpdates");
					List<String> holidayList = (List<String>) request.getAttribute("holidays");
										
					if(holidayList == null ) holidayList = new ArrayList<String>();
					if(hmEventUpdates == null){
						hmEventUpdates = new LinkedHashMap<String,List<String>>();
					}
					
					if(hmQuoteUpdates == null){
						hmQuoteUpdates = new LinkedHashMap<String,List<String>>();
					}
					if(hmNoticeUpdates == null){
						hmNoticeUpdates = new LinkedHashMap<String,List<String>>();
					}
					
					if(hmQuoteUpdates != null && hmQuoteUpdates.size()>0){
						Set<String> quoteSet = hmQuoteUpdates.keySet();
						Iterator<String> qit = quoteSet.iterator();
						while(qit.hasNext()){
							String quoteId = qit.next();
							List<String> quoteList  = hmQuoteUpdates.get(quoteId);  
							if(quoteList == null ) quoteList = new ArrayList<String>();
							if(quoteList != null && quoteList.size()>0){
								
					%>
							<p class="empthought">
				          		<span> <%=quoteList.get(1) %></span> 
				          		<br/>
				          		<span style="float:right;font-style:italic"> <strong><%=quoteList.get(2) %></strong></span>
				        	</p>
					<%
							}
						}
					}
				for(int i=0; i<alBirthDays.size(); i++){ %>
                	<div class="repeat_row" style="width:90%;">
						<%=(String)alBirthDays.get(i) %>
					</div>
				<%}
					
					if(hmNoticeUpdates != null && hmNoticeUpdates.size()>0){	
						Set<String> noticeSet = hmNoticeUpdates.keySet();
						Iterator<String> nit = noticeSet.iterator();
						while(nit.hasNext()){
							String noticeId = nit.next();
							List<String> noticeList  = hmNoticeUpdates.get(noticeId);  
							if(noticeList == null ) noticeList = new ArrayList<String>();
							if(noticeList != null && noticeList.size()>0){
					%>
							<div style="float: left; width: 100%; line-height: 16px; margin: 5px 0px 0px 7px;">
								<div style="float:left;font-style:bold;font-size:12px;"><%=noticeList.get(2) %></div>
								<a href="<%=noticeList.get(0) %>" style="float: right; margin-top: 3px;">
								
								<!-- <img title="Go to Announcements.." src="images1/icons/icons/forward_icon.png"> -->
								<i class="fa fa-forward" aria-hidden="true" title="Go to Announcements.."></i>
								
								
								</a> 
							</div>
					<%
							}
						}
					}
					%>
					<%
					
					if(hmEventUpdates != null && hmEventUpdates.size()>0){	
						Set<String> eventSet = hmEventUpdates.keySet();
						Iterator<String> eit = eventSet.iterator();
						while(eit.hasNext()){
							String eventId = eit.next();
							List<String> eventList  = hmEventUpdates.get(eventId);  
							if(eventList == null ) eventList = new ArrayList<String>();
							if(eventList != null && eventList.size()>0){
					%>
							<div style="float: left; width: 100%; line-height: 16px; margin: 5px 0px 0px 7px;">
							   <div style="float:left;font-size:12px;"><%=eventList.get(2) %></div> 
							 	<div style="float:left;margin-left:3px;font-size:12px;"> organised at <%=eventList.get(6) %></div> 
							 	<div style="margin-left:3px;float:left;font-size:12px;">from <%=eventList.get(4)%></div> 
							 	<div style="margin-left:3px;float:left;font-size:12px;">to <%=eventList.get(5)%> </div> 
							 	<a href="<%=eventList.get(0) %>" style="float: right; margin-top: 3px;">
							 	<%--<img title="Go to Events.." src="images1/icons/icons/forward_icon.png"> --%>
							 	
							 	<i class="fa fa-forward" aria-hidden="true" title="Go to Employee Dashboard.."></i>
							 	
							 	
							 	</a> 
							 	
							 	
							</div>
					<%
							}
						}
					}
						if(holidayList != null && holidayList.size()>0){
							Iterator hit  = holidayList.iterator();
							while(hit.hasNext()){
								String holidayData = (String) hit.next();	
							
					%>			
							<div style="float: left; width: 100%; line-height: 16px; margin: 5px 0px 0px 7px;"><%=holidayData%> </div>
					<%
							}
						}
					%>
				
				</div>
			</div>
			
			
			
			<%if(alUserModulesList!=null && alUserModulesList.contains(IConstants.MODULE_ONBOARDING)){ %>
			
			<div id="clockcontainer">
				<p class="past heading">Joinings </p>
				  <div class="content1">
					<div style="padding: 15px;">
						<b>	Accepted</b> : <%= hmChart2.get("acceptedCand")!=null && !hmChart2.get("acceptedCand").equals("") ? hmChart2.get("acceptedCand") : "0"%>
						<br>				
						<b>	Rejected</b> : <%= hmChart2.get("rejectedCand")!=null && !hmChart2.get("rejectedCand").equals("") ? hmChart2.get("rejectedCand") : "0"%>
						<br>
						<b>	Underprocess</b> : <%=hmChart2.get("underprocessCand")!=null && !hmChart2.get("underprocessCand").equals("") ? hmChart2.get("underprocessCand") : "0" %>
					</div>
				  <div class="holder">
		  			<div id="offerFinalStats" style="height: 200px; width:100%"></div>
	              </div>
                </div>  
			</div>
		
		
		
		
			<div id="clockcontainer">
				<p class="past heading">Applications </p>
				  <div class="content1">
					<div style="padding: 15px;">
					
					
						<b>  Accepted </b> : <%= hmChart1.get("acceptedAppl")!=null && !hmChart1.get("acceptedAppl").equals("") ? hmChart1.get("acceptedAppl") : "0"  %>
					  	<br>
						<b>  Rejected </b> : <%= hmChart1.get("rejectedAppl")!=null && !hmChart1.get("rejectedAppl").equals("") ? hmChart1.get("rejectedAppl") : "0"  %>
					  	<br>
						<b>  Underprocess </b> : <%= hmChart1.get("underprocessAppl")!=null && !hmChart1.get("underprocessAppl").equals("") ? hmChart1.get("underprocessAppl") : "0"  %>
					</div>  
					  <div class="holder">	
				  		<div id="applicationFinalstats" style="height: 200px; width:100%"></div>
	                   </div>
                   </div>  
			</div>

			<%} %>

		
	</div>


	<div id="center">
	
		<div id="rostercontainer">
		<p class="past heading">Employee Issue and Approvals (This month)</p>
		<div class="content1">
		
			<table cellpadding="0" cellspacing="0" width="90%" align="center">
			
				<%-- <tr>
					<td width="170px" class="tdDashLabel">Pending Issues</td>
					<td class="tdDashValue"><%=uF.showData((String)request.getAttribute("EMP_WAITING_COUNT"), "0")%></td>
				</tr>
				<tr>
					<td class="tdDashLabel">Approvals</td>
					<td class="tdDashValue"><%=uF.showData((String)request.getAttribute("EMP_APPROVED_COUNT"), "0")%></td>
				</tr>
			
				<tr>
					<td class="tdDashLabel">Denials</td>
					<td class="tdDashValue"><%=uF.showData((String)request.getAttribute("EMP_DENIED_COUNT"), "0")%></td>
				</tr>
			
				<tr>
					<td class="tdDashLabel fontBold">Total</td>
					<td class="tdDashValue fontBold"><%=uF.showData((String)request.getAttribute("TOTAL"), "0")%></td>
				</tr>
				<tr>
					<td colspan="2" align="center">
						<img src='<%=response.encodeURL(request.getContextPath()+"/jsp/chart/getChart.jsp?"+xyApprovals1URL)%>'
						    usemap="#map1" border="0">
						<map name="map1"><%=xyApprovalsURLMap1%></map>
					</td>
				</tr> --%>
				
					<tr>
					<td colspan="2" align="center">
						<%-- <jsp:include page="../chart/ApprovalsBarChart.jsp"></jsp:include> --%>
						<div id="containerForCharts" style="height: 200px; width:100%"></div>
					</td>
				</tr>
			
			</table>
		</div>
		
		</div>
		
		<div id="rostercontainer">
		<p class="past heading">Pending Attendance Issues (Last 1 Week)</p>
			<div class="content1"> 
				<!-- <ul class="issuereasons"> -->
				<%
				int i=0;
				for(i=0; i< ((alReasons.size()<10)?alReasons.size():10); i++){
					%>
					<p class="issues"><%= (String)alReasons.get(i)%></p>
					<%
				}
				%>
				<!-- </ul> -->
			
			<% if(i==0){ %>
				<p>&nbsp;</p>
				<p class="tdDashLabel fontBold alignCenter">No Pending Attendence Issues</p>
			<%} %>
			</div>
		</div>
	
		<div id="details">
		<p class="past heading">Up coming Staff Leaves</p>
			<div class="content1">
					<ul class="issuereasons">
					<%
					i=0;
					for(i=0; i< ((alLeaves.size()<10)?alLeaves.size():10); i++){
						%>
						<li><%= (String)alLeaves.get(i)%></li>
						<%
					} 
					%>
					</ul>
					<% if(i==0){ %>
					<p>&nbsp;</p>
					<p class="tdDashLabel fontBold alignCenter">No one has approved leave for next one month. </p>
					<%} %>
			</div>
		</div>
		
		
		<div id="details">
			<p class="past heading">New Leave Requests</p>
			<div class="content1">
				<div>
				<ul class="issuereasons">
				<%
				i=0;
				for(i=0; i< ((alLeaveRequest.size()<10)?alLeaveRequest.size():10); i++){
					%>
					<li style="float:left;width:94%"><%= (String)alLeaveRequest.get(i)%></li>
					<%
				}
				%>
				</ul>
				<% if(i==0){ %>
				<p>&nbsp;</p>
				<p class="tdDashLabel fontBold alignCenter">No one has applied leave for next one month. </p>
				<%} %>
				</div>
			</div>
		</div>
		
		<div id="details">
			<div><p class="past">New Reimbursement Requests</p></div>
			<div>
				<ul class="issuereasons">
				<%
				i=0;
				for(i=0; i< ((alReimbursementRequest.size()<10)?alReimbursementRequest.size():10); i++){
					%>
					<li style="float:left;width:94%"><%= (String)alReimbursementRequest.get(i)%></li>
					<%
				}
				%>
				</ul>
				<% if(i==0){ %>
				<p>&nbsp;</p>
				<p class="tdDashLabel fontBold alignCenter">No one has applied for any reimbursement. </p>
				<%} %>
			</div>
		</div>
		
		
		<div id="details">
			<div><p class="past">New Requisition Requests</p></div>
			<div>
				<ul class="issuereasons">
				<%
				i=0;
				for(i=0; i<alRequisitionRequest.size(); i++){
					%>
					<li style="float:left;width:94%"><%= (String)alRequisitionRequest.get(i)%></li>
					<%
				}
				%>
				</ul>
				<% if(i==0){ %>
				<p>&nbsp;</p>
				<p class="tdDashLabel fontBold alignCenter">No one has applied for any requisition. </p>
				<%} %>
			</div>
		</div>
	
		<div id="details">
			<div><p class="past">Probation Approvals</p></div>
			<div>
				<ul class="issuereasons">
				<%
				i=0;
				for(i=0; i<alProbationEndDate.size(); i++){
					%>
					<li style="float:left;width:94%"><%= (String)alProbationEndDate.get(i)%></li>
					<%
				}
				%>
				</ul>
				<% if(i==0){ %>
				<p>&nbsp;</p>
				<p class="tdDashLabel fontBold alignCenter">No one's probations is ending in the next 30days. </p>
				<%} %>
			</div>
		</div>
	
	</div>







	<div id="right">
	
		<div id="details">
		<p class="past heading">Employees per skill set</p>
			<div class="content1">
               <%
				Set setSkillsEmployeeCount = hmSkillsEmployeeCount.keySet();
				Iterator itSkillsEmployeeCount = setSkillsEmployeeCount.iterator();
				while(itSkillsEmployeeCount.hasNext()){
					String strSkillName = (String)itSkillsEmployeeCount.next();
			%>
               <div class="skill_div">
                    <p class="sk_value"><%=(String)hmSkillsEmployeeCount.get(strSkillName) %></p>             
                    <p class="sk_name"><%=strSkillName %></p>                
               </div>  
			<%
				}
			%>
			</div>
		</div>
		
		<div id="details">
		<p class="past heading">Employees per SBU</p> 
			<div class="content1">
			<%
				Set setServiceEmployeeCount = hmServicesEmployeeCount.keySet();
				Iterator itServiceEmployeeCount = setServiceEmployeeCount.iterator();
				while(itServiceEmployeeCount.hasNext()){
					String strServiceId = (String)itServiceEmployeeCount.next();
			%>
               <div class="skill_div">
                    <p class="sk_value"><%=(String)hmServicesEmployeeCount.get(strServiceId) %></p>             
                    <p class="sk_name"><%=(String)hmServicesMap.get(strServiceId) %></p>                
               </div>  
			<%
				}
			%>
			
			
			</div>
		</div>
		
		<div id="details">
		<p class="past heading">Employees for geographies</p>
			<div class="content1">
               <%
               	
				Set setWLocationEmployeeCount = hmWLocationEmployeeCount.keySet();
				Iterator itWLocationEmployeeCount = setWLocationEmployeeCount.iterator();
				while(itWLocationEmployeeCount.hasNext()){
					String strWLocationId = (String)itWLocationEmployeeCount.next();
					
					Map hmWLocation = (Map)hmWorkLocationMap.get(strWLocationId);
					if(hmWLocation==null)hmWLocation=new HashMap();
			%>
               <div class="skill_div">
                    <p class="sk_value"><%=(String)hmWLocationEmployeeCount.get(strWLocationId) %></p>             
                    <p class="sk_name"><%=(String)hmWLocation.get("WL_CITY")+", "+(String)hmWLocation.get("WL_COUNTRY") %></p>                
               </div>  
			<%   
				}
			%> 
			</div>
		</div>
		
	
		<div id="details">
		<p class="past heading">Best Employee of this month</p>
			<div class="content1" style="width:100%; text-align:center">
               <img src='<%=response.encodeURL(request.getContextPath()+"/jsp/chart/getChart.jsp?"+semiBestKPI1URL)%>' border="0">  
			</div>
		</div>
		
		<div id="details">
		<p class="past heading">Top 5 Employees of this month</p>
			<div class="content1">
               <%
               	Set setTopEmployees = hmTopEmployees.keySet();
				Iterator itTopEmployees = setTopEmployees.iterator();
				while(itTopEmployees.hasNext()){
					String strEmpId = (String)itTopEmployees.next();
					Map hmInner = (Map)hmTopEmployees.get(strEmpId);
			%>
               <div class="skill_div" style="width:92%">
                    <div style="float:left; margin:0px 5px; width:15%">
                      <img src="images1/<%=(String)hmEmpProfileImageMap.get(strEmpId) %>" style="border:1px solid #CCCCCC;" alt="Profile pic" width="52px" height="50px"/>                
                 </div>
                      <div style="float: left; width: 80%;">
                        <p class="sk_value"><%=uF.showData((String)hmInner.get("WORKED_HRS"), "0")%></p>             
                        <p class="sk_name"><%=uF.showData((String)hmEmployeeMap.get(strEmpId), "")%></p>  
                        <p class="sk_info"><%=uF.showData((String)hmEmpDesigMap.get(strEmpId), "")%></p>               
                      </div>                  
               </div>  
			<%
				}
			%> 
			</div>
		</div>
	
	
		
	
	
	</div>
</div>





