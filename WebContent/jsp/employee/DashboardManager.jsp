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

Map hmSkillsEmployeeCount = (Map)request.getAttribute("hmSkillsEmployeeCount");
if(hmSkillsEmployeeCount==null)hmSkillsEmployeeCount=new HashMap<String, Map<String, String>>();

Map hmServicesEmployeeCount = (Map)request.getAttribute("hmServicesEmployeeCount");
if(hmServicesEmployeeCount==null)hmServicesEmployeeCount=new HashMap<String, Map<String, String>>();

Map hmWLocationEmployeeCount = (Map)request.getAttribute("hmWLocationEmployeeCount");
if(hmWLocationEmployeeCount==null)hmWLocationEmployeeCount=new HashMap<String, Map<String, String>>();


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
<script type="text/javascript">
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
			<p class="past">Employee Attendance Summary</p>
			<div id="container_Attendance" style="height: 300px; width:95%; margin:10px 0 0 0px;"></div>
		</div>
		
		
		
		<div id="clockcontainer">
			<p class="past">My staff's Project Summary</p>
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
	                                
	                               for(countTask=0; countTask<alTaskList.size(); countTask++){ 
	                               		List alInner = (List)alTaskList.get(countTask);
	                                    %>
	                                    
	                            <tr>
	                                <td class="tdDashLabel"><%=uF.showData((String)alInner.get(0),"N/A") %></td>
	                                <td class="tdDashLabel"><%=uF.showData((String)alInner.get(1),"N/A") %></td>
	                                <td class="tdDashLabel"><%=uF.showData((String)alInner.get(2),"N/A") %></td>
	                                <td align="right" style="padding-right:20px" class="tdDashLabel"><%=uF.showData((String)alInner.get(3),"N/A") %></td>
	                                <%-- <td class="tdDashLabel"><%=uF.showData((String)alInner.get(4),"N/A") %></td> --%>
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
		
		
		<div id="clockcontainer">
			<p class="past">Project Performance</p>
			<div style="height: 200px; width:95%; margin:10px 0 0 0px;">
			<div id="CEStatus" style="height: 200px;width:100%;float:left"></div>
			</div>
		</div> 
		
		
		 <div id="clockcontainer">
			<p class="past">Project Performance</p>
			<table width="100%">
	                        
               <tr>
                   <td class="tdDashLabelheading">Project Name</td>
                   <td class="tdDashLabelheading">Budgeted</td>
                   <td class="tdDashLabelheading">Actual</td>
                   <td class="tdDashLabelheading">Status</td>
               </tr>	
               
               <%
                  for(countTask=0; countTask<alProjectSummary.size(); countTask++){ 
                  		List alInner = (List)alProjectSummary.get(countTask);
                       %>
               <tr>
                   <td class="tdDashLabel"><%=uF.showData((String)alInner.get(1),"0") %></td>
                   <td class="tdDashLabel alignRight padRight20"><%=uF.showData((String)alInner.get(3),"0") %></td>
                   <td class="tdDashLabel alignRight padRight20"><%=uF.showData((String)alInner.get(4),"0") %></td>
                   <td class="tdDashLabel"><%=uF.showData((String)alInner.get(7),"0") %></td>
               </tr>		
               
               
                   <%
               }
               if(countTask==0){
                   %> 
               <tr>
                   <td colspan="4" class="tdDashLabel">
                   Your have not any ongoing project.
                   </td>
               </tr>		
                    <%
               }else{
           %> 
           <tr>
			<td colspan="4" align="right"><a href="ProjectPerformanceReport.action">Know More</a></td>
			</tr>
			<%} %>
           </table>
		</div> 
		
		
		
		<div id="clockcontainer">
			<p class="past">My staff's attendance this month):</p>
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
 


	</div>


	<div id="center">
	
		<div id="rostercontainer" style="display: none">
		<p class="past">Employee Issue and Approvals</p>
		<table cellpadding="0" cellspacing="0" width="90%" align="center">
		
			<tr>
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
			</tr>
			
			
			<tr>
				<td colspan="2" align="center">
					
					<div id="container_IssuesApprovals" style="height: 300px; width:95%; margin:10px 0 0 0px;"></div>
				</td>
			</tr>
			
			
		
		</table>
		
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
		
		
		<div id="rostercontainer">
		<p class="past">Pending Attendance Issues</p> 
			<div class="holder">
			<%
			int i=0;
			for(i=0; i< ((alReasons.size()<10)?alReasons.size():10); i++){
				%>
				<p class="issues"><%= (String)alReasons.get(i)%></p>
				<%
			}
			%>
		
		<% if(i==0){ %>  
			<p>&nbsp;</p>
			<p class="tdDashLabel fontBold alignCenter">No Pending Attendence Issues</p>
		<%} %>
          
           </div>
		</div>
	
		<div id="details">
		<p class="past">Up coming Staff Leaves</p>
			<div>
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
			<div><p class="past">New Leave Requests</p></div>
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
				<p class="tdDashLabel fontBold alignCenter">No one has applied for new leave. </p>
				<%} %>
			</div>
		</div>
	
	
		<div id="details">
			<div><p class="past">New Reimbursement Requests</p></div>
			<div>
				<ul class="issuereasons">
				<%
				i=0;
				for(i=0; i< ((alReimbursementRequest.size()<10) ? alReimbursementRequest.size():10); i++){
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
		
		
		
		 <%
		List alRquirementRequest = (List) request.getAttribute("ManagerRquirementRequest");
		if (alRquirementRequest == null) {
			alRquirementRequest = new ArrayList();
		} 
		%>
		<div id="clockcontainer">
			<p class="past">New Requirement Requests</p>
			<div>
				<ul class="issuereasons">
					<%
						int y=0;
						for (y = 0; y < alRquirementRequest.size(); y++) {
					%>
					<li style="float: left; width: 94%"><%=(String) alRquirementRequest.get(y)%></li>
					<%
						}
					%>
				</ul>
				<%
					if (y == 0) {
				%>
				<p>&nbsp;</p>
				<p class="tdDashLabel fontBold alignCenter">No one has applied
					for any requirement request.</p>
				<%
					}     
				%>
			</div>
		</div>	
		
		
		<%-- <div id="details">
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
		</div> --%>
	
	</div>


	<div id="right">
	
		<div id="details">
        <p class="past">Employees per skill set</p>
			<div>
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
			</div>
		</div>
		
		<div id="details">
		<p class="past">Employees per Service</p>
			<div>
			<%
				Set setServiceEmployeeCount = hmServicesEmployeeCount.keySet();
				Iterator itServiceEmployeeCount = setServiceEmployeeCount.iterator();
				while(itServiceEmployeeCount.hasNext()){
					String strServiceId = (String)itServiceEmployeeCount.next();
			%>
               <div class="skill_div">
                    <p class="sk_value"><%=uF.showData((String)hmServicesEmployeeCount.get(strServiceId),"0") %></p>             
                    <p class="sk_name"><%=uF.showData((String)hmServicesMap.get(strServiceId),"") %></p> 
                    <p class="sk_info"><%=uF.showData((String)hmServicesDescMap.get(strServiceId),"") %></p>                           
               </div>  
			<%
				}if(hmServicesEmployeeCount.size()==0){
					%>
					<div><p style="padding:10px">No services defined for the employees working under you.</p></div>
				<%
				}
			%>
			
			
			</div>
		</div>
		
		<div id="details">
		<p class="past">Employees for geographies</p>
			<div>
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
                    <p class="sk_name"><%=uF.showData((String)hmWLocation.get("WL_CITY"),"")+", "+uF.showData((String)hmWLocation.get("WL_COUNTRY"),"") %></p>                
               </div>  
			<%
				}if(hmWLocationEmployeeCount.size()==0){
					%>
					<div><p style="padding:10px">No work location defined for the employees working under you.</p></div>
				<%
				}
			%> 
			</div>
		</div>
		
	
		<div id="details">
		<p class="past">Best Employee of this month</p>
			<div style="width:100%; text-align:center">
               <img src='<%=response.encodeURL(request.getContextPath()+"/jsp/chart/getChart.jsp?"+semiBestKPI1URL)%>' border="0">  
			</div>
		</div>
		
		<div id="details">
		<p class="past">Top 5 Employees of this month</p>
			<div>
               <%
               	Set setTopEmployees = hmTopEmployees.keySet();
				Iterator itTopEmployees = setTopEmployees.iterator();
				while(itTopEmployees.hasNext()){
					String strEmpId = (String)itTopEmployees.next();
					Map hmInner = (Map)hmTopEmployees.get(strEmpId);
			%>
               <div class="skill_div emp1" style="width:92%">
               
                 <div style="float:left; margin:0px 5px; width:15%">
                      <img src="images1/<%=(String)hmEmpProfileImageMap.get(strEmpId) %>" alt="Profile pic" width="52px" height="50px"/>                
                 </div>
                      <div style="float: left; width: 80%;">
                        <p class="sk_value"><%=uF.showData((String)hmInner.get("WORKED_HRS"), "0")%></p>             
                        <p class="sk_name"><%=uF.showData((String)hmEmployeeMap.get(strEmpId), "")%></p>  
                        <p class="sk_info"><%=uF.showData((String)hmEmpDesigMap.get(strEmpId), "")%></p>               
                      </div>  
               </div>  
			<% 
				}if(hmTopEmployees.size()==0){
					%>
					<div><p style="padding:10px">No employees found working under you.</p></div>
				<%
				}
			%> 
			</div>
		</div>
	
	
		
	
	
	</div>
</div>

<div id="approveLeaveDiv"></div>



