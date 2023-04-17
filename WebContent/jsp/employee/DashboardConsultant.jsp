<%@page import="com.konnect.jpms.util.ArrayUtils"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.*, ChartDirector.*"%>

<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

 

<script type="text/javascript">
jQuery(document).ready(function() {
	
	jQuery("#frmClockEntries").validationEngine();
	 
  jQuery(".content1").show();
  //toggle the componenet with class msg_body
  jQuery(".close_div").click(function()
  { 
    jQuery(this).next(".content1").slideToggle(500);
	$(this).toggleClass("heading_dash"); 
  });
});
</script>

<script>
function validateNewTimeNotification(){
	
	if(document.frmClockEntries.strNotify.checked){
		//document.getElementById("newTimeNotificationS").style.display="table-row";
	//	document.getElementById("newTimeNotificationE").style.display="table-row";
		document.getElementById("newTimeNotification").style.display="table-row";
	}else{
	//	document.getElementById("newTimeNotificationS").style.display="none";
	//	document.getElementById("newTimeNotificationE").style.display="none";
		document.getElementById("newTimeNotification").style.display="none";
	}
}


function validateForm(){
	var re5digit=/^\d{2}:\d{2}$/;
	
	if(document.frmClockEntries.strNotify.checked){
		if(document.frmClockEntries.strNewTime.value==""){
			alert('Please enter time in given format (HH:mm). \ne.g. for 3:00AM enter 03:00 and for 3:00PM enter 15:00'); 
			return false;
		}else if(document.frmClockEntries.strNewTime.value.search(re5digit)==-1){
			alert('Please enter time in given format (HH:mm). \ne.g. for 3:00AM enter 03:00 and for 3:00PM enter 15:00'); 
			return false;
		}
	}
	
	
}

function validateService(){	
	
	var re5digit=/^\d{2}:\d{2}$/;
	
	if(document.frmClockEntries.strRosterStartTime.value==""){
		alert('Please enter roster start time in given format (HH:mm). \ne.g. for 3:00AM enter 03:00 and for 3:00PM enter 15:00'); 
		return false;
	}else if(document.frmClockEntries.strRosterStartTime.value.search(re5digit)==-1){
		alert('Please enter time in given format (HH:mm). \ne.g. for 3:00AM enter 03:00 and for 3:00PM enter 15:00'); 
		return false;
	}
	
	if(document.frmClockEntries.strRosterEndTime.value==""){
		alert('Please enter roster end time in given format (HH:mm). \ne.g. for 3:00AM enter 03:00 and for 3:00PM enter 15:00'); 
		return false;
	}else if(document.frmClockEntries.strRosterEndTime.value.search(re5digit)==-1){
		alert('Please enter time in given format (HH:mm). \ne.g. for 3:00AM enter 03:00 and for 3:00PM enter 15:00'); 
		return false;
	}
	
	if(document.frmClockEntries.service.options[0].selected){
		alert('Please choose the cost centre.'); 
		return false;
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
UtilityFunctions uF = new UtilityFunctions();
List alSkills = (List) request.getAttribute("alSkills");
CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
String []arrEnabledModules = CF.getArrEnabledModules();
%>

<script type="text/javascript">
			$(function() {
			  
				$('#default').raty(); 

				 <%	if(alSkills!=null && alSkills.size()!=0) { 
                	for(int i=0; alSkills!=null && i< (alSkills.size()>0?1:0); i++) {
                		List alInner = (List)alSkills.get(i); %>
				
					$('#starPrimary').raty({
						  readOnly: true,
						  start:    <%=uF.parseToDouble((String)alInner.get(2))/2%>,
						  half: true
						});
				
				<%}
				}%>
			});
		</script>
<%


AngularMeter semiWorkedAbsent = (AngularMeter)request.getAttribute("KPI");
String semiWorkedAbsent1URL = (String)semiWorkedAbsent.makeSession(request, "chart3");


LinearMeter marker = (LinearMeter)request.getAttribute("KPIZ");
String marker1URL = (String)marker.makeSession(request, "chart4");


PieChart pieWorkedAbsent = (PieChart)request.getAttribute("CHART_WORKED_ABSENT");
String piWorkedAbsent1URL = (String)pieWorkedAbsent.makeSession(request, "chart1");
String piWorkedAbsent1URLMap1 = pieWorkedAbsent.getHTMLImageMap("", "", "title='{xLabel}: {value}'");


XYChart xyApprovals = (XYChart)request.getAttribute("CHART_APPROVALS");
String xyApprovals1URL = (String)xyApprovals.makeSession(request, "chart2");
String xyApprovalsURLMap1 = xyApprovals.getHTMLImageMap("", "", "title='{xLabel}: {value}'");


%>


<%
	Map hmRoster = (LinkedHashMap)request.getAttribute("hmRoster");
	Map hmRoster1 = (LinkedHashMap)request.getAttribute("hmRoster1");
	Map hmMyAttendence = (HashMap) request.getAttribute("hmMyAttendence");
	Map hmMyAttendence1 = (HashMap) request.getAttribute("hmMyAttendence1");
	List alReasons = (List) request.getAttribute("alReasons");
	List alTaskList = (List) request.getAttribute("alTaskList");
	
	java.util.List alNOTICE =  (java.util.ArrayList)request.getAttribute("NOTICE");
	
	if (hmRoster == null) {
		hmRoster = new HashMap();
	}
	if (hmMyAttendence == null) {
		hmMyAttendence = new HashMap();
	}
	if (alReasons == null) {
		alReasons = new ArrayList();
	}
%>
 
 <%String strImage = (String) request.getAttribute("IMAGE"); %>
 

<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="My Dashboard" name="title"/>
</jsp:include>

 

 
    <div class="leftbox reportWidth">
        
        <div id="left">
			<div id="profilecontainer">
               <div class="holder">
                    <div class="profileborder">
                    <img height="82" width="84" id="profilecontainerimg" src="userImages/<%=strImage %>"/>
                    </div>
                    <div>
                    <p class="welcome">Hi, <strong><%=uF.showData((String)request.getAttribute("EMPNAME"),"")%> [<%=uF.showData((String)request.getAttribute("EMPCODE"),"")%>]</strong></p>
                    <p class="monday"> <%=uF.showData((String)request.getAttribute("DESIG_NAME"),"")%></p>
                    <p class="monday"> <strong><%=request.getAttribute("strSkills")%> </strong></p>
                    
                    <p class="monday"><%=uF.showData((String)request.getAttribute("DEPT"), "")%>, <%=uF.showData((String)request.getAttribute("WL_NAME"),"")%></p>
                    <%if(request.getAttribute("MANAGER")!=null){%>
                    <p class="monday">You report to <strong><%=request.getAttribute("MANAGER")%></strong> <a href="MyMail.action">Send</a> </p>
                    <%} %>
                    <div id="fixed" class="monday"></div> 
                    <div id="starPrimary" class="monday"></div>
                    <p class="reportingMessage"><%=request.getAttribute("EMAIL")%></p>
                    
                    </div>
                                      
                    <!-- <div class="viewmore"><a href="MyProfile.action">Know more..</a></div> -->
               </div>     
			</div>
            
             
             <%
             	int nMailCount = uF.parseToInt((String)request.getAttribute("MAIL_COUNT"));
             	int nApprovalCount = uF.parseToInt((String)request.getAttribute("EXCEP_APPROVED_COUNT"));
             	int nPendingCount = uF.parseToInt((String)request.getAttribute("PENDING_EXCEPTION_COUNT"));
             	int nWaitingCount = uF.parseToInt((String)request.getAttribute("WAITING_EXCEPTION_COUNT"));
             	
             	int nRIMApprovalCount = uF.parseToInt((String)request.getAttribute("REIMB_APPROVED_COUNT"));
             	int nRIMPendingCount = uF.parseToInt((String)request.getAttribute("REIMB_WAITING_COUNT"));
             	int nRIMDeniedCount = uF.parseToInt((String)request.getAttribute("REIMB_DENIED_COUNT"));
             
             	int nLeaveApprovalCount = uF.parseToInt((String)request.getAttribute("LEAVE_APPROVED_COUNT"));
             	int nLeavePendingCount = uF.parseToInt((String)request.getAttribute("LEAVE_PENDING_COUNT"));
             	int nLeaveDeniedCount = uF.parseToInt((String)request.getAttribute("LEAVE_DENIED_COUNT"));
             	
    			
             	List alBirthDays = (List)request.getAttribute("alBirthDays");
             	if(alBirthDays==null)alBirthDays=new ArrayList();
             %>
             
             <div id="profilecontainer">
				<p class="past close_div">My updates</p>
				<div class="content1">
					<div class="holder">
	                
	                <%if(request.getAttribute("DAY_THOUGHT_TEXT")!=null){ %>
	                <p class="thought">
	                  <span> <%=request.getAttribute("DAY_THOUGHT_TEXT") %></span> 
	                  <br/>
	                   <span style="float:right;font-style:italic">- <strong><%=request.getAttribute("DAY_THOUGHT_BY") %></strong></span>
	                </p>
	                <%} %>
	                
	                <p class="mail">You have <a href="MyMail.action" title="My Mail"><strong><%=nMailCount%> new</strong></a> mails.</p>
	                
	                <%if(nApprovalCount>0 || nPendingCount>0 || nWaitingCount>0){ %>
					<p class="notify">Your <strong><%=nApprovalCount%></strong> exceptions have been <font color="green">approved</font> 
					<%if(nPendingCount>0){ %>
	                and <a href="UpdateClockEntries.action"><strong><%=nPendingCount%></strong> <%=((nPendingCount==1)?"is":"are") %> <font color="orange">waiting for approval</font></a> 
	                <%}if(nWaitingCount>0){ %>
	                and <a href="UpdateClockEntries.action"><strong><%=nWaitingCount%></strong> <%=((nWaitingCount==1)?"is":"are") %> <font color="orange">waiting for you to enter reasons.</font></a>
					<%}
	                %>
	                </p>
	                <%
					}%>


					<%if(nLeaveApprovalCount>0 || nLeaveDeniedCount>0 || nLeavePendingCount>0){ %>
						<p class="notify">
						Your 
							<%if(nLeaveApprovalCount>0){ %>
								<a href="EmployeeLeaveEntryReport.action"><strong><%=nLeaveApprovalCount%></strong> leaves have been <font color="green">approved</font></a>
							<%}else if(nLeaveDeniedCount>0){ %>
								<a href="EmployeeLeaveEntryReport.action"><strong><%=nLeaveDeniedCount%></strong> leaves are  <font color="red">denied</font></a>
							<%}else if(nLeavePendingCount>0){ %>
			                	<a href="EmployeeLeaveEntryReport.action"><strong><%=nLeavePendingCount%></strong> leaves are <font color="orange">waiting for approval</font></a> 
			                <%} %>
		                </p>
	                <%} %>
	                
	                
	                <%if(nRIMApprovalCount>0 || nRIMDeniedCount>0 || nRIMPendingCount>0){ %>
	                
					<p class="notify">Your <strong><%=nRIMApprovalCount%></strong> reimbursements have been <font color="green">approved</font>, 
					<strong><%=nRIMDeniedCount%></strong> were  <font color="red">denied</font>  
	                and <a href="Reimbursements.action"><strong><%=nRIMPendingCount%></strong> are <font color="orange">waiting for approval</font></a> </p>

	                <%} %>
					<%for(int i=0; i<alBirthDays.size(); i++){ %>
	                <div class="repeat_row">
						<%=(String)alBirthDays.get(i) %>
						</div>
					<%} %>
	                </div>
                </div>
			</div>
             
           <%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PROJECT_MANAGEMENT+"")>=0){ %>  
             		
			<div id="profilecontainer">
				<p class="past close_div">My Tasks Summary</p>
			   <div class="content1">
				   <div class="holder">
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
	                                Your have not been assigned any task. Please speak to your manager for allocation of tasks.
	                                </td>
	                            </tr>		
	                                 <%
	                            }
	                        %> 
	                         
	                        </table>
				
				      <!-- <div class="viewmore"><a href="RosterReport.action">Know more..</a></div> -->
	            </div>
            </div>
			</div>
			<%} %>
           
           
           	<%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_SELF_SERVICE+"")>=0){ %>  
			<div id="profilecontainer">
			<p class="past close_div">My Issues and Approvals</p>
			<div class="content1">
	                <div class="holder">     
	                    <table cellpadding="0" cellspacing="0" width="90%" align="center">
	                    <tr>
	                        <td colspan="2">
	                            <div style="width:100%; height: 200px">
	                                    <jsp:include page="/jsp/chart/ApprovalsBarChart.jsp" />
	                            </div>
	                            
	                        </td>
	                    </tr>
	                    </table>
				
	                    <!-- <div class="viewmore"><a href="UpdateClockEntries.action?PAY=Y">Know more..</a></div> -->
	                </div>
                </div>     
			</div>
			
			<%} %>
			
			
			<%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_ROSTER+"")>=0){ %>
			<div id="profilecontainer">
				<p class="past close_div">My Upcoming Roster Summary</p>
			   <div class="content1">
				   <div class="holder">
	                            <table width="100%">
	                        
	                            <tr>
	                                <td class="tdDashLabelheading">Date</td>
	                                <td class="tdDashLabelheading">From</td>
	                                <td class="tdDashLabelheading">To</td>
	                                <td class="tdDashLabelheading">Cost center</td>
	                            </tr>	
	                            
	                            <%
	                                Set set = hmRoster1.keySet();
	                                Iterator it = set.iterator();
	                                int i=0;
	                                
	                                
	                                while(it.hasNext()){
	                                    
	                                    i++;
	                                    String strDate = (String)it.next();
	                            //		Map hm = (Map)hmRoster.get(strDate);
	                                    List alService = (List)hmRoster1.get(strDate);
	                                    for(int j=0; j<alService.size(); j++){
	                                        Map hm = (Map)hmRoster.get(strDate+"_"+(String)alService.get(j));
	                                    
	                                    %>
	                                    
	                            <tr>
	                                <td class="tdDashLabel"><%=uF.showData(strDate,"N/A") %></td>
	                                <td class="tdDashLabel"><%=uF.showData((String)hm.get("FROM"),"N/A") %></td>
	                                <td class="tdDashLabel"><%=uF.showData((String)hm.get("TO"),"N/A") %></td>
	                                <td class="tdDashLabel"><%=uF.showData((String)hm.get("SERVICE"),"N/A") %></td>
	                            </tr>		
	                                <%
	                                
	                                }
	                            }
	                            if(i==0){
	                                %>
	                            <tr>
	                                <td colspan="4" class="tdDashLabel">
	                                Your next week roster is not updated, please send a request/ reminder to your manager
	                                </td>
	                            </tr>		
	                                <%
	                            }
	                        %>
	                        
	                        </table>
				
				      <!-- <div class="viewmore"><a href="RosterReport.action">Know more..</a></div> -->
	            </div>
            </div>
			</div>
			<%} %>
		</div>
			
			
			
			
			





		<div id="center">
		
		
		<%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_TIME_ATTENDANCE+"")>=0){ %>
		
			<div id="involmentcontainer">
			<s:form id="frmClockEntries" name="frmClockEntries" theme="simple" action="ClockOnOffEntry">
				
				
			    <h2>
			     <s:url id="clockLabelUrl" action="GetClockLabel" /><sx:div onclick="return confirmMsg(this.innerHTML);" href="%{clockLabelUrl}" listenTopics="showClockLabel" formId="frmClockEntries" showLoadingText=""></sx:div>
			    </h2>
			
			<s:hidden name="strClock"></s:hidden>
			
			<div class="clr"></div>
			<div class="clockon_content">
			       
			            <div class="clockon_content">It is <%= request.getAttribute("CURRENT_DATE") %></div>
			            <div class="clockon_content_time"><%-- <%= request.getAttribute("CURRENT_TIME") %> --%><div id="myTime" style="text-align:left;margin-left:15%"></div> </div>
			            
				        <div class="clockon_content"><%= (request.getAttribute("ROSTER_TIME")!=null)?request.getAttribute("ROSTER_TIME"):"" %></div>
			<s:url id="clockMessageUrl" action="GetClockEntryMessage"  /> <sx:div href="%{clockMessageUrl}" listenTopics="showClockMessage" formId="frmClockEntries" showLoadingText=""></sx:div>
			
						         
				        </div>
				   
			</s:form>
			
			</div>
            <div class="clr"></div>
		<%} %>
        
        
        
        <%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_LEAVE+"")>=0){ %>
            
             <div class="rosterweek">
                    <h2 class="past close_div">Leave Summary</h2>
                    <div class="content1">
	                     <div class="holder">
	                            <div style="width:95%; height: 200px;float:left">
	                            	<jsp:include page="/jsp/chart/LeaveApprovalsBarChart.jsp" />
	                    		</div>
	                    
	                          <div class="viewmore">
	                           <!-- <a href="EmployeeLeaveEntryReport.action">Know More..</a> -->
	                          </div>
	                      </div>
                      </div>
                 </div>
        	<div class="clr"></div> 
        <%} %>         
                 
            
         <%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_TIME_ATTENDANCE+"")>=0){ %>   

            	<div class="rosterweek">
                    <h2 class="past close_div">Service Working Hours</h2>
                     <div class="content1">
	                     <div class="holder">
	                            <div style="width:100%; height: 200px;float:left">
	                            	<jsp:include page="/jsp/chart/EmpServiceWorkingHoursChart.jsp" />
	                    		</div>
	                            
	                         <!-- <div class="viewmore"><a href="ClockEntries.action?T=C">Know more..</a></div> -->   
	                     </div>
                 	</div>
                 </div>
            	<div class="clr"></div>
          <%} %>
          
         <%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_ROSTER+"")>=0){ %>   	
            	<div class="rosterweek">
                    <h2 class="past close_div">Standard/ Rostered Vs Actual hrs</h2>
                     <div class="content1">
	                     <div class="holder">
	                            <div style="width:100%; height: 200px;float:left">
	                            	<jsp:include page="/jsp/chart/EmpRosterVsActualHoursChart.jsp" />
	                    		</div>
	                            <!-- <div class="viewmore"><a href="ClockEntries.action?T=C">Know more..</a></div> -->
	                     </div>
                     </div>
                 </div>
            	<div class="clr"></div> 
		<%} %>
			
	</div>

    	<div id="right">
   	
   	<%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_HCM+"")>=0){ %>
   			<div class="KPI"> 
	               <p class="past close_div">My experience:</p>
					<div class="content1">
						<div class="holder">							
							<%-- <div class="time_since">Since <span><%=request.getAttribute("JOINING_DATE") %></span></div> --%>
							<div class="time_spent_duration" style="text-align:left;line-height:30px">
							<%if(((String)request.getAttribute("TIME_DURATION"))!=null && ((String)request.getAttribute("TIME_DURATION")).length()>0){ %>
							Since <span><%=request.getAttribute("JOINING_DATE") %>, </span>
							you have worked <%=request.getAttribute("TIME_DURATION") %>
							for <span><%=request.getAttribute("HRS_WORKED") %></span> hrs
							<%}else{ %>
							Your working hours have not been calcualated, yet.
							<%} %>
							
							
							
							</div>
							
	                    </div>
                    </div>
				</div>
			<div class="clr"></div>
   	<%} %>
   	
   	<%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_HCM+"")>=0){ %>
   			<div class="KPI"> 
	               <p class="past close_div">My attendance KPI:</p>
	               <div class="content1">
						<table cellpadding="0" cellspacing="0"  align="center">
							<tr>
								<td colspan="2">
									<img src='<%=response.encodeURL(request.getContextPath()+"/jsp/chart/getChart.jsp?"+semiWorkedAbsent1URL)%>'
									    usemap="#map1" border="0">
								</td>
							</tr>
							<tr>
								<td colspan="2">
									<img src='<%=response.encodeURL(request.getContextPath()+"/jsp/chart/getChart.jsp?"+marker1URL)%>'
									    usemap="#map1" border="0">
								</td>
							</tr>
						</table>
					</div>
				</div>
			<div class="clr"></div> 
	<%} %>
	
	<%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_TIME_ATTENDANCE+"")>=0){ %>			
              <div class="KPI"> 
                
				<p class="past close_div">My Attendance Summary (last 3 days)</p>
			       <div class="content1">
	                   	<div class="holder">
						<table cellpadding="0" cellspacing="0" width="90%" align="center">
							<tr>
								<td class="tdDashLabelheading">Date</td>
								<td class="tdDashLabelheading">From</td>
								<td class="tdDashLabelheading">To</td>
								<td class="tdDashLabelheading">Cost centre</td>
							</tr>
							<%
								Set set1 = hmMyAttendence1.keySet();
								Iterator it1 = set1.iterator();
								Map hm = null;
								int count = 0;
								while (it1.hasNext()) {
									String strDate = (String) it1.next();
								//	hm = (HashMap) hmMyAttendence1.get(strDate);
									if(count++>3)continue;
									
									List alService = (List)hmMyAttendence1.get(strDate);
									for(int j=0; j<alService.size(); j++){
										hm = (Map)hmMyAttendence.get(strDate+"_"+(String)alService.get(j));
										
										if(hm==null){
											hm = new HashMap();
										}
										
							%>
						
							<tr>
								<td class="tdDashLabel"><%=strDate%></td>
								<td class="tdDashLabel"><%=uF.showData((String) hm.get("IN"),"-")%></td>
								<td class="tdDashLabel"><%=uF.showData((String) hm.get("OUT"),"-")%></td>
								<td class="tdDashLabel"><%=uF.showData((String) hm.get("SERVICE"),"-")%></td>
							</tr>
						
							<%
								}
									}
								
							%>
						</table>
	                    
	                    <!-- <div class="viewmore"><a href="ClockEntries.action?T=T">Know more..</a></div> -->
	                    </div>
                    </div>
			
			</div>
			  <div class="clr"></div>    		
     <%} %>
               
     <%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_TIME_ATTENDANCE+"")>=0){ %>          
			<div class="KPI"> 
                
				<p class="past close_div">My Attendance Summary (this month)</p>
				  <div class="content1">
					  <div class="holder">	
						
						<div style="width:100%; height: 200px">
								<jsp:include page="/jsp/chart/AttendancePieChart.jsp" />
						</div>
						
						<!-- <div class="viewmore"><a href="ClockEntries.action?T=T">Know more..</a></div> -->
	                    
	                   </div>
                   </div>  
				</div>
			<div class="clr"></div>     
     <%} %>     
				
                
              </div>
              
	 </div>  
		
	
<script>
function getTime(){
	getContent('myTime','GetServerTime.action');	
}

setInterval ( "getTime()", 1000 );

</script>