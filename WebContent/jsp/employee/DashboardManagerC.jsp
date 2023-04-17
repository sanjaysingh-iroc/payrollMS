<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.ArrayUtils"%>
<%@page import="java.util.*, ChartDirector.*"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>


<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

     
   
<%
UtilityFunctions uF = new UtilityFunctions();
AngularMeter semiWorkedAbsent = (AngularMeter)request.getAttribute("KPI_BEST");
String semiBestKPI1URL = (String)semiWorkedAbsent.makeSession(request, "KPI_BEST");
Map hmEmployeeMap = (Map)request.getAttribute("hmEmployeeMap");
Map hmEmpDesigMap = (Map)request.getAttribute("hmEmpDesigMap");
Map hmEmpProfileImageMap = (Map)request.getAttribute("hmEmpProfileImageMap"); 
Map hmServicesMap = (Map)request.getAttribute("hmServicesMap"); 
Map hmServicesDescMap = (Map)request.getAttribute("hmServicesDescMap");
Map hmWorkLocationMap = (Map)request.getAttribute("hmWorkLocationMap");
List alResignedEmployees = (List)request.getAttribute("alResignedEmployees");
if(alResignedEmployees==null)alResignedEmployees=new ArrayList();

List alTaskList = (List) request.getAttribute("alTaskList");
if(alTaskList==null)alTaskList=new ArrayList();

List alProjectSummary = (List) request.getAttribute("alOuter");
if(alProjectSummary==null)alProjectSummary=new ArrayList();

Map hmTopEmployees = (Map)request.getAttribute("hmTopEmployees");
if(hmTopEmployees==null)hmTopEmployees=new HashMap<String, Map<String, String>>();

/* Map hmSkillsEmployeeCount = (Map)request.getAttribute("hmSkillsEmployeeCount");
if(hmSkillsEmployeeCount==null)hmSkillsEmployeeCount=new HashMap<String, Map<String, String>>(); */
List<List<String>> skillwiseEmpCountList = (List<List<String>>) request.getAttribute("skillwiseEmpCountList");

String []arrEnabledModules = (String[])request.getAttribute("arrEnabledModules");
List alAchievements = (List)request.getAttribute("alAchievements");

List alBirthDays = (List)request.getAttribute("alBirthDays");
if(alBirthDays==null)alBirthDays=new ArrayList();

List alEvents = (List)request.getAttribute("alEvents");
if(alEvents==null)alEvents=new ArrayList();

List alAttendance = (List)request.getAttribute("alAttendance");
List alRecruitment = (List)request.getAttribute("alRecruitment");
List<List<String>> trainingDetails = (List<List<String>>)request.getAttribute("trainingDetails");
List<List<String>> appraisalDetails = (List<List<String>>)request.getAttribute("appraisalDetails");

%>


<g:compress>
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
var chartCEStatus;
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
	
	
	
	chartCEStatus = new Highcharts.Chart({
		chart: {
			renderTo: 'CEStatus',
			type: 'column'
		},
		title: {
			text: ''
		},
		
		xAxis: {
			categories: [
<%=request.getAttribute("pro_name")%>
			]
		},
		yAxis: {
			min: 0,
			title: {
				text: 'Rs.'
			}
		},
	      credits: {
	         	enabled: false
	  	   },
		legend: {
			layout: 'vertical',
			backgroundColor: '#FFFFFF',
			align: 'left',
			verticalAlign: 'top',
			x: 100,
			y: 70,
			floating: true,
			shadow: true
		},
		tooltip: {
			formatter: function() {
				return ''+
					this.x +': '+ this.y +'';
			}
		},
		plotOptions: {
			column: {
				pointPadding: 0.2,
				borderWidth: 0
			}
		},
			series: [ { name: 'Billable Cost',
		          data: [<%=request.getAttribute("billable_amount")%>]
		    
		       }, { 
		          name: 'Budgeted Cost',
		          data: [<%=request.getAttribute("budgeted_cost")%>]
		       
		       }, { 
		           name: 'Actual Cost',
		           data: [<%=request.getAttribute("actual_amount")%>]
		        
		        }]
	});
	
});
</script>
</g:compress>
<script>
function showClockMessage() {
	dojo.event.topic.publish("showClockMessage");
}
function showClockLabel() {
	dojo.event.topic.publish("showClockLabel");
}
 
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


function approveDenyLeave(apStatus,leaveId,levelId,compensatory){
	var status = '';
	if(apStatus == '1'){
		status='approve';
	} else if(apStatus == '-1'){
		status='deny';
	}
	if(confirm('Are you sure, do you want to '+status+' this request?')){
		var reason = window.prompt("Please enter your "+status+" reason.");
		if (reason != null) {
			var action = 'ManagerLeaveApproval.action?type=type&apType=auto&apStatus='+apStatus+'&E='+leaveId+'&LID='+levelId+'&strCompensatory='+compensatory+'&mReason='+reason;
			//alert(action); 
			window.location = action;
		}
	}
}

function approveDenyRembursement(apStatus,reimbId){
	var status = '';
	if(apStatus == '1'){
		status='approve';
	} else if(apStatus == '-1'){
		status='deny';
	}
	if(confirm('Are you sure, do you want to '+status+' this request?')){
		var reason = window.prompt("Please enter your "+status+" reason.");
		if (reason != null) {
			var action = 'UpdateReimbursements.action?type=type&S='+apStatus+'&RID='+ reimbId +'&T=RIM&M=AA&mReason='+reason; 
			//alert(action); 
			window.location = action;
		}
	}
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


<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Manager's Dashboard" name="title"/>
</jsp:include>


<div class="leftbox reportWidth">
	<div id="left">
		
		<div id="clockcontainer">
			<p class="past heading">Upcoming Events and Birthdays</p>
				<div class="content1">
				<%if(request.getAttribute("DAY_THOUGHT_TEXT")!=null){ %>
                <p class="thought">
                  <span> <%=request.getAttribute("DAY_THOUGHT_TEXT") %></span> 
                  <br/>
                   <span style="float:right;font-style:italic">- <strong><%=request.getAttribute("DAY_THOUGHT_BY") %></strong></span>
                </p>
                <%} %>
                
				<%for(int i=0; i<alEvents.size(); i++){ %>
                	<div class="thought">
						<%=(String)alEvents.get(i) %>
					</div>
				<%} %>
				
				<%for(int i=0; i<alBirthDays.size(); i++){ %>
                	<div class="repeat_row" style="width:90%;">
						<%=(String)alBirthDays.get(i) %>
					</div>
				<%} %>
				</div>
			</div>
			
		
		<div id="clockcontainer">
			<p class="past">Team Attendance</p>
			<div id="container_Attendance" style="height: 300px; width:95%; margin:10px 0 0 0px;"></div>
		</div>
		
		
		<%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT+"")>=0){ %>
			<div id="details">
				<p class="past heading">Team Performance</p>
				<div class="content1">
					<ul class="issuereasons">
					<%int iA=0;
					for(iA=0; appraisalDetails!= null && !appraisalDetails.isEmpty() && iA<appraisalDetails.size(); iA++){ 
						List<String> appInner = appraisalDetails.get(iA);
					%>
						<li style="float:left;width:93%">
							<div style="float:left;width:45%;margin-right:5px;"><%=appInner.get(0)%> : <p style="font-size:10px;color:#666;">(<%=appInner.get(1)%>)</p></div>
							<div style="float:left;width:15%;margin-right:5px;text-align: right;color:#00FF00;font-weight: bold;"><%=appInner.get(2)%></div>
							<div style="float:left;width:15%;margin-right:5px;text-align: right;color:#FF6633;font-weight: bold;"><%=appInner.get(3)%></div>
							<div style="float:left;width:15%;margin-right:5px;text-align: right;color:#000099;font-weight: bold;"><%=appInner.get(4)%></div>
						</li>
						<%} if(iA==0){ %>
						<li style="float:left;width:93%" class="tdDashLabel">No appraisals available for you.</li>
						<%} %>
					</ul>
					<%if(iA>0){ %>
					<div style="padding-left:10px">
						<span style="font-size:10px;color:#00FF00">Action Required</span> <span style="font-size:10px;">|</span>
						<span style="font-size:10px;color:#FF6633">Completed</span> <span style="font-size:10px;">|</span>
						<span style="font-size:10px;color:#000099">Remaining</span> <span style="font-size:10px;">|</span>
					</div>
					<%} %>
				</div>
			</div>
		<%} %>	
		
		
		<%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_CAREER_DEV_PLANNING+"")>=0){ %>
			<div id="details">
				<p class="past heading">Team Learning</p>
				<div class="content1">
					<ul class="issuereasons">
					
						<%int iC=0;
						for(iC=0; trainingDetails!= null && !trainingDetails.isEmpty() && iC<trainingDetails.size(); iC++){ 
						List<String> trainInner = trainingDetails.get(iC);
					%>
						<li style="float:left;width:93%">
							<div style="float:left;width:60%;margin-right:5px;"><%=trainInner.get(0)%>(<%=trainInner.get(1)%>):</div>
							<div style="float:left;width:15%;margin-right:5px;text-align: right;color:#00FF00;font-weight: bold;"><%=trainInner.get(2)%></div>
							<div style="float:left;width:15%;margin-right:5px;text-align: right;color:#FF6633;font-weight: bold;"><%=trainInner.get(3)%></div>
						</li>
						<%} if(iC==0){ %>
						<li style="float:left;width:93%" class="tdDashLabel">No trainings or learning created.</li>
						<%} %>
					</ul>
					<%if(iC>0){ %>
					<div style="padding-left:10px">
						<span style="font-size:10px;color:#00FF00">Invited Participants</span> <span style="font-size:10px;">|</span>
						<span style="font-size:10px;color:#FF6633">Participating</span> <span style="font-size:10px;">|</span>
					</div>
					<%} %>
				</div>
			</div>
		<%} %>
		
		<% 
		if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_CAREER_DEV_PLANNING+"")>=0){
			List<Map<String,String>> learnGapList = (List<Map<String,String>>) request.getAttribute("trainingGapList");
		%>
			<div id="details">
				<p class="past heading">Learning Gaps: [<%=uF.parseToInt((String)request.getAttribute("schedule")) %>]</p>
				<div class="content1">
					<ul class="issuereasons">
					
						<%int iC=0;
						for(iC=0; learnGapList!= null && !learnGapList.isEmpty() && iC<learnGapList.size(); iC++){ 
							Map<String, String> hmLearnGap= learnGapList.get(iC);
					%>
						<li style="float:left;width:93%">
							<div style="float:left;width:60%;margin-right:5px;"><%=(iC+1) %>. <%=hmLearnGap.get("TRAINING_TITLE")%></div>
							<div style="float:left;width:15%;margin-right:5px;text-align: right;color:#00FF00;font-weight: bold;"><%=hmLearnGap.get("NO_OF_PARTICIPANT")%></div>
						</li>
						<%} if(iC==0){ %>
						<li style="float:left;width:93%" class="tdDashLabel">No Upcoming Learning.</li>
						<%} %>
					</ul>
					
				</div>
			</div>
		<%} %>
			
		 
		<%-- <%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PROJECT_MANAGEMENT+"")>=0){%>
		<div id="clockcontainer">
			<p class="past">Team's Project</p>
			<table width="100%">
                 <tr>
                     <td class="tdDashLabelheading">Project Name</td>
                     <td class="tdDashLabelheading">Status</td>
                     <td class="tdDashLabelheading">Deadline</td>
                     <td class="tdDashLabelheading">Time Spent<br/>(hrs)</td>
                     <!-- <td class="tdDashLabelheading">TL</td> -->
                 </tr>	
                 <%
                     int countTask=0;
                    for(countTask=0; alTaskList!=null && countTask<alTaskList.size(); countTask++){ 
                    		List alInner = (List)alTaskList.get(countTask);
                         %>
                 <tr>
                     <td class="tdDashLabel"><%=uF.showData((String)alInner.get(0),"N/A") %></td>
                     <td class="tdDashLabel"><%=uF.showData((String)alInner.get(1),"N/A") %></td>
                     <td class="tdDashLabel"><%=uF.showData((String)alInner.get(2),"N/A") %></td>
                     <td align="right" style="padding-right:20px" class="tdDashLabel"><%=uF.showData((String)alInner.get(3),"N/A") %></td>
                     <td class="tdDashLabel"><%=uF.showData((String)alInner.get(4),"N/A") %></td>
                 </tr>		
                     <%
                 }
                 if(countTask==0){
                     %> 
                 <tr>
                     <td colspan="4" class="tdDashLabel">
                     Your have not assigned any task to your team mates.
                     </td>
                 </tr>		
                      <%
                 }
             %> 
             
             </table>
		</div>
		<%} %> --%>
		
	</div>


	<div id="center">
		
		<div id="details">
			<div><p class="past">Pending Counters</p></div>
			<div class="content1">
				<div class="skill_div">
                    <p class="sk_value"><%=uF.parseToInt((String)session.getAttribute("PENDING_EXCEPTION_COUNT"))%></p>             
                    <p class="sk_name">Attendance</p>                
               </div>  
               <div class="skill_div">
                    <p class="sk_value"><%=uF.parseToInt((String)session.getAttribute("PENDING_LEAVE_COUNT"))%></p>             
                    <p class="sk_name">Leave</p>                
               </div>
               <div class="skill_div">
                    <p class="sk_value"><%=uF.parseToInt((String)session.getAttribute("PENDING_REIMBURSEMENT_COUNT"))%></p>             
                    <p class="sk_name">Reimbursements</p>                
               </div>
               <div class="skill_div">
                    <p class="sk_value"><%=uF.parseToInt((String)session.getAttribute("PENDING_REQUISITION_COUNT"))%></p>             
                    <p class="sk_name">Others</p>                
               </div>
               
			</div>
		</div>
	
	
		<%if(alResignedEmployees.size()>0){ %>
				<div id="rostercontainer">
				<p class="past">Pending Resignation Requests</p> 
					<div class="holder">
					<%
					int i=0;
					for(i=0; i< ((alResignedEmployees.size()<10)?alResignedEmployees.size():10); i++){
						%>
						<div class="issues"><%= (String)alResignedEmployees.get(i)%></div>
						<%
					}
					%>
		           </div> 
				</div>
			<%} %>
		
		<div id="details">
			<div><p class="past" style="width:100%">Attendance<span style="float:right;margin-right:10px"><a href="UpdateClockEntries.action">[<%=((alAttendance!=null)?alAttendance.size():0) %>]</a></span></p></div>
			<div class="content1">
				<ul class="issuereasons">
					<%
					int iA = 0;
					for(iA=0; alAttendance!=null && iA<alAttendance.size(); iA++){
						List alInner = (List)alAttendance.get(iA);
					%>
					<li style="float:left;width:94%;margin:0px;">
					Exception pending from <%=alInner.get(0)%> (<%=alInner.get(1)%>) for <%=alInner.get(2)%>
					<p style="font-size: 10px;color: #666;"><%=((alInner.get(3)!=null)?alInner.get(3):"")%></p> 
					</li>
					<%}if(iA==0){%>
					<li style="float:left;width:94%" class="tdDashLabel">No pending exception</li>
					<%} %>
				</ul>
			</div>
		</div>
	  
	
		<%-- <div id="details"> 
			<div><p class="past" style="width:100%">Leave<span style="float:right;margin-right:10px"><a href="ManagerLeaveApprovalReport.action">[<%=alLeaveRequest.size() %>]</a></span></p></div>
			<div class="content1">
				<ul class="issuereasons">
				<%
				int i=0;
				for(i=0; i< alLeaveRequest.size(); i++){	
				%>
				<li style="float:left;width:94%"><%= (String)alLeaveRequest.get(i)%></li>
				<%
				}if(i==0){ %>
				<li style="float:left;width:94%" class="tdDashLabel">No one has applied leave for next one month. </li>
				<%} %>
				</ul>
			</div>
		</div> --%>
		
		<%-- <div id="details">
			<div><p class="past" style="width:100%">Reimbursements <span style="float:right;margin-right:10px"><a href="Reimbursements.action">[<%=alReimbursementRequest.size() %>]</a></span></p></div>
			<div class="content1">
				<ul class="issuereasons">
					<%
					i=0;
					for(i=0; alReimbursementRequest!=null && i< alReimbursementRequest.size(); i++){
					%>
					<li style="float:left;width:94%"><%= (String)alReimbursementRequest.get(i)%></li>
					<%
					}if(i==0){ %>
					<li style="float:left;width:94%" class="tdDashLabel">No one has applied for any reimbursement.</li>
					<%} %>
				</ul>
			</div>
		</div> --%>
		
	
		<%-- <div id="details">
			<div><p class="past" style="width:100%">Other Requests <span style="float:right;margin-right:10px"><a href="Requisitions.action">[0]</a></span></p></div>
			<div class="content1">
				<ul class="issuereasons">
					<li style="float:left;width:94%" class="tdDashLabel">There is no other request pending</li>
				</ul>
			</div>
		</div> --%>
	
	</div>


	<div id="right">
	
		<div id="details">
        <p class="past">Employees per skill set</p>
        
        <div class="content1">
               <%
				for(int j=0; skillwiseEmpCountList != null && !skillwiseEmpCountList.isEmpty() && j<skillwiseEmpCountList.size(); j++) {
					List<String> innerList = skillwiseEmpCountList.get(j);
			%>
               <div class="skill_div">
                    <p class="sk_value"><%=innerList.get(2) %></p>             
                    <p class="sk_name"><%=innerList.get(1) %></p>                
               </div>  
			<%
				}if(skillwiseEmpCountList == null || skillwiseEmpCountList.size()==0){
					%>
					<div><p style="padding:10px">No skills defined for the employees working under you.</p></div>
				<%
				}
				%>
			</div>
			
			<%-- <div>
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
				}if(hmSkillsEmployeeCount.size()==0){
					%>
						<div><p style="padding:10px">No skills defined for the employees working under you.</p></div>
					<%
				}
			%>
			</div> --%>
		</div>
		
		<div id="details">
			<div><p class="past">Awarded Employees</p></div>
			<div class="content1">
				<%	iA = 0;
					for(iA=0; alAchievements!=null && iA<alAchievements.size(); iA++){
				%>
				<div style="float: left; width:100%;border-bottom: 1px solid #eee;">
					<div style="float:left;margin-right:5px;margin-top:5px"><img src="images1/trophy.png"></div><div style="float:left; width:90%"><%=alAchievements.get(iA) %></div>
				</div>
				<%}if(iA==0){ %>
				<div style="float:left;margin-right:5px" class="tdDashLabel">No employee awarded yet.</div>
				<%}%>
			</div>
		</div>
		
	
		<div id="details">
		<p class="past heading">Performing Employees</p>
			<div class="content1">
			
               <%
               	Set setTopEmployees = hmTopEmployees.keySet();
				Iterator itTopEmployees = setTopEmployees.iterator();
				int nCount = 0;
				while(itTopEmployees.hasNext()){
					nCount++;
					String strEmpId = (String)itTopEmployees.next();
					Map hmInner = (Map)hmTopEmployees.get(strEmpId);
			%>
               <div class="skill_div" style="width:92%">
                    <div style="float:left; margin:0px 5px; width:15%">
                      <img class="lazy img-circle" src="userImages/avatar_photo.png" style="border:1px solid #CCCCCC;" data-original="userImages/<%=(String)hmEmpProfileImageMap.get(strEmpId) %>" alt="Profile pic" width="52px" height="50px"/>                
                 </div>
                      <div style="float: left; width: 80%;">
                        <p class="sk_value"><%=uF.showData((String)hmInner.get("WORKED_HRS"), "0")%></p>             
                        <p class="sk_name"><%=uF.showData((String)hmEmployeeMap.get(strEmpId), "")%></p>  
                        <p class="sk_info"><%=uF.showData((String)hmEmpDesigMap.get(strEmpId), "")%></p>               
                      </div>                  
               </div>  
			<%
				}if(nCount==0){
			%> 
			<div style="float:left;margin-left:15px" class="tdDashLabel">No ratings updated yet </div>
			<%} %>
			</div>
		</div>
	
	
		
	
	
	</div>
</div>





