<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="java.util.*,ChartDirector.*"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<style>
    .sk_name,.sk_value{
    margin-bottom: 0px;
    }
</style>
<%
    UtilityFunctions uF = new UtilityFunctions();
    CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
    
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
    
    
    /* Map hmSkillsEmployeeCount = (Map)request.getAttribute("hmSkillsEmployeeCount");
    if(hmSkillsEmployeeCount==null)hmSkillsEmployeeCount=new HashMap<String, Map<String, String>>(); */
    List<List<String>> skillwiseEmpCountList = (List<List<String>>) request.getAttribute("skillwiseEmpCountList");
    	
    Map hmServicesEmployeeCount = (Map)request.getAttribute("hmServicesEmployeeCount");
    if(hmServicesEmployeeCount==null)hmServicesEmployeeCount=new HashMap<String, Map<String, String>>();
    
    Map hmWLocationEmployeeCount = (Map)request.getAttribute("hmWLocationEmployeeCount");
    if(hmWLocationEmployeeCount==null)hmWLocationEmployeeCount=new HashMap<String, Map<String, String>>();
    
    
    %>
<link href="js/jvectormap/jquery-jvectormap-1.2.2.css" rel='stylesheet' />
<script src='scripts/charts/jquery.min.js'></script>


<style>
    .counterLabel {
    font-family: Verdana;
    font-size: 20px;
    text-align: right;
    padding-right: 10px;
    }
    .counterText {
    font-family: Digital;
    font-size: 20px;
    text-align: right;
    padding-right: 10px;
    }
    .site-stats li {
	width: 45%;
	}
</style>
<script type="text/javascript">
var cities = [];
    <%-- var chartAttendance;
    var chartAttendance1;
    $(document)
    		.ready(
    				function() {
    
    					chartAttendance = new Highcharts.Chart(
    							{
    
    								chart : {
    									renderTo : 'container_Attendance',
    									type : 'column'
    								},
    								title : {
    									text : 'In (Hours)'
    								},
    								xAxis : {
    									categories : [
    <%=request.getAttribute("sbDatesAttendanceDate")%>
    ],
    									labels : {
    										rotation : -45,
    										align : 'right',
    										style : {
    											font : 'normal 10px Verdana, sans-serif'
    										}
    									},
    									title : {
    										text : 'Date'
    									}
    								},
    								credits : {
    									enabled : false
    								},
    								yAxis : {
    									min : 0,
    									title : {
    										text : 'Attendance'
    									}
    								},
    								plotOptions : {
    									column : {
    										pointPadding : 0.2,
    										borderWidth : 0
    									}
    								},
    								series : [
    <%=request.getAttribute("sbDatesAttendance")%>
    ]
    							});
    
    					chartAttendance1 = new Highcharts.Chart(
    							{
    
    								chart : {
    									renderTo : 'container_Attendance1',
    									type : 'pie'
    								},
    								title : {
    									text : null
    								},
    								tooltip : {
    									formatter : function() {
    										return '<b>' + this.point.name
    												+ '</b>: '
    												+ this.percentage + ' %';
    									}
    								},
    								legend : {
    									enabled : true
    								},
    								plotOptions : {
    									pie : {
    										allowPointSelect : true,
    										cursor : 'pointer',
    										dataLabels : {
    											enabled : false
    										},
    										showInLegend : true
    									}
    								},
    								series : [ {
    									type : 'pie',
    									name : 'Attendance',
    									data : [
    <%=request.getAttribute("CHART_WORKED_ABSENT_C")%>
    ]
    								} ]
    
    							});
    
    				}); --%>
    
    function approveDenyLeave(apStatus, leaveId, levelId, compensatory) {
    	var status = '';
    	if (apStatus == '1') {
    		status = 'approve';
    	} else if (apStatus == '-1') {
    		status = 'deny';
    	}
    	if (confirm('Are you sure, do you want to ' + status + ' this request?')) {
    		var reason = window.prompt("Please enter your " + status
    				+ " reason.");
    		if (reason != null) {
    			var action = 'ManagerLeaveApproval.action?type=type&apType=auto&apStatus='
    					+ apStatus
    					+ '&E='
    					+ leaveId
    					+ '&LID='
    					+ levelId
    					+ '&strCompensatory='
    					+ compensatory
    					+ '&mReason='
    					+ reason;
    			//alert(action); 
    			window.location = action;
    		}
    	}
    }
    
    function approveDenyRembursement(apStatus, reimbId) {
    	var status = '';
    	if (apStatus == '1') {
    		status = 'approve';
    	} else if (apStatus == '-1') {
    		status = 'deny';
    	}
    	if (confirm('Are you sure, do you want to ' + status + ' this request?')) {
    		var reason = window.prompt("Please enter your " + status
    				+ " reason.");
    		if (reason != null) {
    			var action = 'UpdateReimbursements.action?type=type&S='
    					+ apStatus + '&RID=' + reimbId + '&T=RIM&M=AA&mReason='
    					+ reason;
    			//alert(action); 
    			window.location = action;
    		}
    	}
    }
    
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
<%-- <jsp:include page="../common/SubHeader.jsp">
    <jsp:param value="Accountant's Dashboard" name="title"/>
    </jsp:include> --%>
<div class="leftbox reportWidth">
    <section class="content">
        <div class="row jscroll">
        	<section class="col-lg-7 connectedSortable" style="padding-right:0px;">
	        	<div class="box box-primary">
	        		<%String wLocationCount = (String) request.getAttribute("wLocationCount"); %>
	                <div class="box-header with-border">
	                    <h3 class="box-title">Geographical Spread</h3>
	                    <div class="box-tools pull-right">
	                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
	                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
	                    </div>
	                </div>
	                <div class="box-body" style="padding: 5px; overflow-y: auto;">
	                    <div class="pad">
		                    <!-- Map will be created here -->
		                    <div id="world-map" style="height: 312px;"></div>
		                  </div>
	                </div>
	                <div class="box-footer">
		              
	              		<table class="table table_no_border" style="margin-bottom: 0px;">
	              			<tr>
			              	<script src="js/jvectormap/jquery-jvectormap-1.2.2.min.js"></script>
							<script src="js/jvectormap/jquery-jvectormap-world-mill-en.js"></script>		
			              	<% 
                            Set<String> setWLocationEmployeeCount = hmWLocationEmployeeCount.keySet();
                            Iterator<String> itWLocationEmployeeCount = setWLocationEmployeeCount.iterator();
                            while(itWLocationEmployeeCount.hasNext()) {
                            	String strWLocationId = itWLocationEmployeeCount.next();
                            	
                            	Map<String, String> hmWLocation = (Map<String, String>)hmWorkLocationMap.get(strWLocationId);
                            	if(hmWLocation == null) hmWLocation = new HashMap<String, String>();
                            	if((String)hmWLocation.get("WL_CITY") == null || hmWLocation.get("WL_CITY").equals("")) {
                            		continue;
                            	}
							%>	
							<td>
		              			<div class="description-block border-right">
				                    <span class="description-percentage text-green"><b><%=(String)hmWLocationEmployeeCount.get(strWLocationId) %></b></span>
				                    <h5 class="description-header"><%=(String)hmWLocation.get("WL_CITY")+", "+(String)hmWLocation.get("WL_COUNTRY") %></h5>
				                  </div>	
		              		</td>
			               <script>
			               
			            	   $.ajax({
			            	   url: "https://maps.googleapis.com/maps/api/geocode/json?address="+encodeURIComponent('<%=(String)hmWLocation.get("WL_CITY")%>'),
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
								
							%> 
							<script>
								$('#world-map').vectorMap({
								    map: 'world_mill_en',
								    backgroundColor: "transparent",
								    regionStyle: {
								      initial: {
								        fill: '#e4e4e4',
								        "fill-opacity": 1,
								        stroke: 'none',
								        "stroke-width": 0,
								        "stroke-opacity": 1
								      }
								    },
								  	
								    markerStyle: {
								        initial: {
								          fill: '#00a65a',
								          stroke: '#111'
								        }
								      },
								      markers: cities
								  });
								
							</script>
							</tr>
	              		</table>
		              <!-- /.row -->
		            </div>
	                <!-- /.box-body -->
	            </div>
	        	<div class="box box-primary">
                    <div class="box-header with-border">
                        <h3 class="box-title">Upcoming events and birthdays</h3>
                        <div class="box-tools pull-right">
                            <button class="btn btn-box-tool" data-widget="collapse">
                            <i class="fa fa-minus"></i>
                            </button>
                            <button class="btn btn-box-tool" data-widget="remove">
                            <i class="fa fa-times"></i>
                            </button>
                        </div>
                    </div>
                    <!-- /.box-header -->
                    <div class="box-body" style="padding: 5px; overflow-y: auto; max-height: 300px;">
                    	<div class="widget-content nopadding updates" id="collapseG3" style="height: auto;">
                    		<%if(request.getAttribute("DAY_THOUGHT_TEXT")!=null){ %>
                               <div class="new-update clearfix">
                                  	<i class="fa fa-lightbulb-o"></i>
				              		<div class="update-done"><%=request.getAttribute("DAY_THOUGHT_TEXT") %><span><strong>- <%=request.getAttribute("DAY_THOUGHT_BY") %></strong></span> </div>
				              		<%-- <div class="update-date"><span class="update-day">20</span>jan</div> --%>
				               </div>
                            <%} %>
                            <%for(int i=0; i<alEvents.size(); i++){ %>
                               <div class="new-update clearfix">
	                                <i class="fa fa-lightbulb-o"></i>
				              		<div class="update-done"><%=(String)alEvents.get(i) %></div>
				               </div>
                            <%	} %>
                            <% Map<String,List<String>> hmEventUpdates = (Map<String,List<String>>) request.getAttribute("eventUpdates");
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
                                <div class="new-update clearfix">
                                  	<i class="fa fa-lightbulb-o"></i>
				              		<div class="update-done"><%=quoteList.get(1) %><span><strong>- <%=quoteList.get(2) %></strong></span> </div>
				              		<%-- <div class="update-date"><span class="update-day">20</span>jan</div> --%>
				               </div>
                                <%
                                    }
                                    }
                                    }%>
                                <%for(int i=0; i<alBirthDays.size(); i++){ %>
                                <div class="new-update clearfix">
                                  	<i class="fa fa-birthday-cake"></i>
				              		<div class="update-done"><%=(String)alBirthDays.get(i) %></div>
				              		<%-- <div class="update-date"><span class="update-day">20</span>jan</div> --%>
				               </div>
                                <%}if(hmNoticeUpdates != null && hmNoticeUpdates.size()>0){	
                                	Set<String> noticeSet = hmNoticeUpdates.keySet();
                                	Iterator<String> nit = noticeSet.iterator();
                                	while(nit.hasNext()){
                                		String noticeId = nit.next();
                                		List<String> noticeList  = hmNoticeUpdates.get(noticeId);  
                                		if(noticeList == null ) noticeList = new ArrayList<String>();
                                		if(noticeList != null && noticeList.size()>0){
                                %>
                            <div class="new-update clearfix">
                              	<i class="fa fa-bullhorn"></i>
			              		<div class="update-done"><%=noticeList.get(2) %></div>
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
                                <div class="new-update clearfix">
                                  	<i class="fa fa-lightbulb-o"></i>
				              		<div class="update-done"><%=eventList.get(2) %>&nbsp;organised at&nbsp;
                                        <%=eventList.get(6) %>&nbsp;from&nbsp;
                                        <%=eventList.get(4)%>&nbsp;to&nbsp;
                                        <%=eventList.get(5)%>
                                    </div>
				               </div>
				               <a href="<%=eventList.get(0) %>"
                                        style="float: right; margin-top: 3px;">
                                        <i class="fa fa-forward" aria-hidden="true" style=" color: #68ac3b;" title="Go to My Reviews.."></i>
                                        
                                </a>
                                        
                                <%
                                    }
                                    }
                                    }
                                    if(holidayList != null && holidayList.size()>0){
                                    Iterator hit  = holidayList.iterator();
                                    while(hit.hasNext()){
                                    	String holidayData = (String) hit.next();	
                                    
                                    %>
                                 <div class="new-update clearfix">
                                  	<i class="fa fa-lightbulb-o"></i>
				              		<div class="update-done"><%=holidayData%></div>
				              		<%-- <div class="update-date"><span class="update-day">20</span>jan</div> --%>
				               </div>
                                <%
                                    }
                                    }
                                    %>   
                    	</div>
                    </div>
                    <!-- /.box-body -->
                </div>
	        	<div class="box box-primary"> 
                    <div class="box-header with-border">
                        <h3 class="box-title">Up coming Staff Leaves</h3>
                        <div class="box-tools pull-right">
                            <button class="btn btn-box-tool" data-widget="collapse">
                            <i class="fa fa-minus"></i>
                            </button>
                            <button class="btn btn-box-tool" data-widget="remove">
                            <i class="fa fa-times"></i>
                            </button>
                        </div>
                    </div>
                    <!-- /.box-header -->
                    <div class="box-body"
                        style="padding: 5px; overflow-y: auto; max-height: 300px;">
                        <div class="content1">
                            <ul class="issuereasons">
                                <%
                                    int i=0;
                                    for(i=0; i< ((alLeaves.size()<10)?alLeaves.size():10); i++){
                                    	%>
                                <li><%= (String)alLeaves.get(i)%></li>
                                <%
                                    } 
                                    %>
                            </ul>
                            <% if(i==0){ %>
                            <p>&nbsp;</p>
                            <p class="tdDashLabel fontBold alignCenter">No one has approved
                                leave for next one month.
                            </p>
                            <%} %>
                        </div>
                    </div>
                    <!-- /.box-body -->
                </div>
	        </section>
            <section class="col-lg-5 connectedSortable">
                <div class="box box-primary">
                    <div class="box-header with-border">
                        <h3 class="box-title">Employees per skill set</h3>
                        <div class="box-tools pull-right">
                            <button class="btn btn-box-tool" data-widget="collapse">
                            <i class="fa fa-minus"></i>
                            </button>
                            <button class="btn btn-box-tool" data-widget="remove">
                            <i class="fa fa-times"></i>
                            </button>
                        </div>
                    </div>
                    <!-- /.box-header -->
                    <div class="box-body" style="padding: 5px; overflow-y: auto;">
                        <ul class="site-stats">
							
                            <%
                                for(int j=0; skillwiseEmpCountList != null && !skillwiseEmpCountList.isEmpty() && j<skillwiseEmpCountList.size(); j++) {
                                	List<String> innerList = skillwiseEmpCountList.get(j);
                                %>
                           	<li class="bg_lh"><strong><%=innerList.get(2) %></strong> <small><%=innerList.get(1) %></small></li>
                            <%
                                } if(skillwiseEmpCountList == null || skillwiseEmpCountList.size()==0){
                                	%>
                            <div>
                                <p style="padding: 10px">No skills defined for the employees
                                    working under you.
                                </p>
                            </div>
                            <%
                                }
                                %>
                         </ul>
                    </div>
                    <!-- /.box-body -->
                </div>
                <div class="box box-primary">
                    <div class="box-header with-border">
                        <h3 class="box-title">Employees per Service</h3>
                        <div class="box-tools pull-right">
                            <button class="btn btn-box-tool" data-widget="collapse">
                            <i class="fa fa-minus"></i>
                            </button>
                            <button class="btn btn-box-tool" data-widget="remove">
                            <i class="fa fa-times"></i>
                            </button>
                        </div>
                    </div>
                    <!-- /.box-header -->
                    <div class="box-body" style="padding: 5px; overflow-y: auto;">
                        <ul class="site-stats">
                            <%
                                Set<String> setServiceEmployeeCount = hmServicesEmployeeCount.keySet();
                                Iterator<String> itServiceEmployeeCount = setServiceEmployeeCount.iterator();
                                while(itServiceEmployeeCount.hasNext()){
                                	String strServiceId = itServiceEmployeeCount.next();
                                	if((String)hmServicesMap.get(strServiceId) == null){
                                		continue;
                                	}
                                %>
                                <li class="bg_lh"><strong><%=(String)hmServicesEmployeeCount.get(strServiceId) %></strong> <small><%=(String)hmServicesMap.get(strServiceId) %></small></li>
                            <%
                                }
                                %>
                         </ul>
                    </div>
                    <!-- /.box-body -->
                </div>
            </section>
        </div>
    </section>
</div>
