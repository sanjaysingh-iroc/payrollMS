<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="java.util.*, ChartDirector.*"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<%-- <script type="text/javascript" src="scripts/charts/jquery.min.js"></script> --%>
<script src="scripts/D3/amcharts/amcharts.js" type="text/javascript"></script>
<script src="scripts/D3/amcharts/serial.js" type="text/javascript"></script>
<script src="scripts/D3/amcharts/pie.js" type="text/javascript"></script>
<script src="scripts/D3/amcharts/themes/light.js" type="text/javascript"></script>

<script type="text/javascript" src="js/jquery.sparkline.js"></script>
<script src="scripts/charts/justgage/raphael-2.1.4.min.js" type="text/javascript"></script>
<script src="scripts/charts/justgage/justgage.js" type="text/javascript"></script>

<%
	UtilityFunctions uF = new UtilityFunctions();
	String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
	
	String strTitle = (String)request.getAttribute(IConstants.TITLE); 
	String strUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
	
	boolean poFlag = (Boolean) request.getAttribute("poFlag");
	boolean tlFlag = (Boolean) request.getAttribute("tlFlag");
	
	/* Map<String, String> hmFeatureStatus = (Map<String, String>) request.getAttribute("hmFeatureStatus");
	Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>)request.getAttribute("hmFeatureUserTypeId"); */
	
	Map<String, String> hmEmpProfile = (Map<String, String>) request.getAttribute("hmEmpProfile");
   	if (hmEmpProfile == null)  hmEmpProfile = new HashMap<String, String>();
%>

<style>
.greenbox {
	height: 11px;
	background-color:#00FF00; /* the critical component */
}

.outbox {
	height: 11px;
	width: 100%;
	background-color:#D8D8D8; /* the critical component */
}

</style>
<script>
$(document).ready(function() {
	$("body").on('click','#closeButton',function() {
		$(".modal-dialog").removeAttr('style');
		$(".modal-body").height(400);
		$("#modalInfo").hide();
    });
	$("body").on('click','.close',function() {
		$(".modal-dialog").removeAttr('style');
		$(".modal-body").height(400);
		$("#modalInfo").hide();
	});

});


function getTeamMembers() {
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$(".modal-title").html('Team Members');
	$.ajax({
		url : 'TeamMembers.action',
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}


function getEmpProfile(val) {
   	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$(".modal-title").html('Resource Profile');
	$.ajax({
	     url : "AppraisalEmpProfile.action?empId=" + val,
	     cache : false,
	     success : function(data) {
	     $(dialogEdit).html(data);
	     }
    });
}
	
</script>

<section class="content">
	<div class="row jscroll">
		<section class="col-lg-4 col-md-6 col-sm-12 connectedSortable paddingright0">
			<div class="box box-primary">
                <div class="box-header with-border">
                <% List<Map<String, String>> alMyTask = (List<Map<String, String>>) request.getAttribute("alMyTask"); 
                	String strCurr = (String) request.getAttribute("strCurr");
                %>
                    <h3 class="box-title">My Tasks</h3>
                    <div class="box-tools pull-right">
                    	<span class="label label-danger"><%=alMyTask.size() %></span>
                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                    </div>
                </div>
                <!-- /.box-header -->
                <div class="box-body" style="padding: 5px;  overflow: auto;"> <!-- overflow-y: auto; max-height: 250px; -->
                    <div class="rosterweek" >
						<table class="table table-striped table-bordered">
						<% if(alMyTask != null && !alMyTask.isEmpty()) { %>
							<tr>
								<th>Task Name</th>
								<th>Completion Status</th>  
								<th>Estimated Time<br/>(hrs)</th>
								<th>Time Spent<br/>(hrs)</th>
								<th>Indicator</th>
							</tr>
						<%
							for(int i = 0; i < alMyTask.size(); i++){
								Map<String, String> hmMyTask = (Map<String, String>) alMyTask.get(i);
								String strTaskBullet = uF.showData(hmMyTask.get("TASK_SPENT_TIME"),"0")+","+uF.showData(hmMyTask.get("TASK_EST_TIME"),"0");
								//String strTaskCompleted = uF.showData(hmMyTask.get("TASK_COMPLETED"),"0");
						%>
								<tr>
									<td><%=hmMyTask.get("TASK_NAME") %></td>
									<td>
											<%-- <div class="outbox">
												<div class="greenbox" title="<%=strTaskCompleted+"%" %>" style="width: <%=strTaskCompleted %>%;"></div>
											</div> --%>
										<span id="myTaskbullet<%=hmMyTask.get("TASK_ID")%>">Loading..</span>
										<script type="text/javascript">
										    //$(function() {
										    	 $('#myTaskbullet<%=hmMyTask.get("TASK_ID")%>').sparkline(new Array(<%=strTaskBullet %>), {type: 'bullet',targetColor: '#b2b2b2',performanceColor: '#9acd32'} );
										    //});
									    </script>
									</td>
									<td class="alignRight padRight20"><%=hmMyTask.get("TASK_EST_TIME") %></td>
									<td class="alignRight padRight20"><%=hmMyTask.get("TASK_SPENT_TIME") %></td>
									<td align="center"><%=hmMyTask.get("TASK_TIME_INDICATOR") %></td>
								</tr>
							<% } %> 
						<% } else { %>
							<tr><td colspan="4"><div class="msg nodata" style="width: 94%;"><span>Tasks not available.</span></div> </td></tr>
						<% } %>
						</table>
						
						<div class="viewmore">
				        	<a href="MyWork.action">
		           				<%--<img src="<%=request.getContextPath()%>/images1/icons/icons/forward_icon.png" title="Go to My Tasks.."/> --%>
		           				<i class="fa fa-forward" aria-hidden="true" title="Go to My Tasks.."></i>
		           				
		           			</a>
		           		</div>
                    </div>
                </div>
                <!-- /.box-body -->
            </div>
            <%if(poFlag || tlFlag){ %>
            <div class="box box-primary">
                <div class="box-header with-border">
                    <h3 class="box-title">My Project Performance</h3>
                    <div class="box-tools pull-right">
                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                    </div>
                </div>
                <!-- /.box-header -->
                <div class="box-body" style="padding: 5px; overflow: auto;"> <!-- overflow-y: auto; max-height: 250px; -->
                    <div class="rosterweek" style="width: 100%;">
							<%
								Map hmProPerformaceProjectName = (Map)request.getAttribute("hmProPerformaceProjectName");
								Map hmProPerformaceProjectTimeIndicator = (Map)request.getAttribute("hmProPerformaceProjectTimeIndicator");
								List alProjectId = (List)request.getAttribute("alProjectId");
								Map<String, String> hmProOwner = (Map<String, String>)request.getAttribute("hmProOwner");
								if(hmProOwner == null) hmProOwner = new HashMap<String, String>();
								Map<String, String> hmProActIdealTimeHRS = (Map<String, String>)request.getAttribute("hmProActIdealTimeHRS");
								if(hmProActIdealTimeHRS == null) hmProActIdealTimeHRS = new HashMap<String, String>();
							%>
							<table class="table table-striped table-bordered">
							<% if(alProjectId != null && !alProjectId.isEmpty()) { %>
								<tr>
									<th>Project Name</th>
									<th>Project Owner</th>
									<th>Completion Status</th> 
									<th>Estimated Time<br/>(hrs)</th>
									<th>Time Spent<br/>(hrs)</th>
									<th>Indicator</th>
								</tr>
							<%
								for(int i=0; alProjectId != null && !alProjectId.isEmpty() && i<alProjectId.size(); i++){
									String strBullet = uF.showData(hmProActIdealTimeHRS.get((String)alProjectId.get(i)+"_ACT_TIME_HRS"),"0")+","+uF.showData(hmProActIdealTimeHRS.get((String)alProjectId.get(i)+"_IDEAL_TIME_HRS"),"0");
									//String strProCompleted = uF.showData(hmProActIdealTimeHRS.get((String)alProjectId.get(i)+"_COMPLETED_SATUS"),"0");
							%>
									<tr>
										<td><%=hmProPerformaceProjectName.get((String)alProjectId.get(i)) %></td>
										<td> <%=hmProOwner.get((String)alProjectId.get(i)) %></td>
										<td>
											<%-- <div class="outbox">
												<div class="greenbox" title="<%=strProCompleted+"%" %>" style="width: <%=strProCompleted %>%;"></div>
											</div> --%>
											<span id="bullet<%=(String)alProjectId.get(i)%>">Loading..</span>
											<script type="text/javascript">
										    	 $('#bullet<%=(String)alProjectId.get(i)%>').sparkline(new Array(<%=strBullet %>), {type: 'bullet',targetColor: '#b2b2b2',performanceColor: '#9acd32'} );
										    </script>
										</td>
										<td class="alignRight padRight20" ><%=hmProActIdealTimeHRS.get((String)alProjectId.get(i)+"_IDEAL_TIME_HRS") %></td>
										<td class="alignRight padRight20" ><%=hmProActIdealTimeHRS.get((String)alProjectId.get(i)+"_ACT_TIME_HRS") %></td>
										<td align="center"><%=hmProPerformaceProjectTimeIndicator.get((String)alProjectId.get(i)) %></td>
									</tr>
								<%} %> 
							<%} else { %>
								<tr><td colspan="5"><div class="msg nodata" style="width: 94%;"><span>Projects not available.</span></div> </td></tr>
							<% } %>
							</table>
							
							<div class="viewmore">
					        	<a href="ProjectPerformanceReportWP.action">
			           				<%--<img src="<%=request.getContextPath()%>/images1/icons/icons/forward_icon.png" title="Go to Project Performance.."/> --%>
			           				<i class="fa fa-forward" aria-hidden="true" title="Go to Project Performance.."></i>
			           			</a>
			           		</div>
                    </div>
                </div>
                <!-- /.box-body -->
            </div>
            <% } %>
            <%if(poFlag || tlFlag){ %>
            <div class="box box-info">
                <div class="box-header with-border">
                    <h3 class="box-title">Team Work Efforts</h3>
                    <div class="box-tools pull-right">
                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                    </div>
                </div>
                <!-- /.box-header -->
                <div class="box-body" style="padding: 5px; overflow-y: auto; max-height: 250px;">
                    <div class="rosterweek" style="width: 100%;">
						<%
							List<Map<String, String>> alPeople = (List<Map<String, String>>)request.getAttribute("alPeople");
							if(alPeople == null) alPeople = new ArrayList<Map<String,String>>();
						 	Map<String, String> hmBillable = (Map<String, String>) request.getAttribute("hmBillable");
						 	if (hmBillable == null)hmBillable = new HashMap<String, String>();
						 	Map<String, String> hmNonBillable = (Map<String, String>) request.getAttribute("hmNonBillable");
						 	if (hmNonBillable == null)hmNonBillable = new HashMap<String, String>();
						 	Map<String, String> hmOther = (Map<String, String>) request.getAttribute("hmOther");
						 	if (hmOther == null)hmOther = new HashMap<String, String>(); 
					 	%>
					 	<table class="table table-striped table-bordered">
							<%if (alPeople != null && alPeople.size() > 0) {%>
								<tr>
									<th width="35%">Resource</th>
									<th>Billable</th>
									<th>Non-Billable</th>
									<th>Other</th>
								</tr>
								<%
									for(int j =0; j<alPeople.size(); j++){
										Map<String, String> hmPeople = alPeople.get(j);
										String strEmpId = hmPeople.get("EMP_ID");
								%>
								<tr>
									<td>
										<%if(docRetriveLocation == null) { %>
											<img height="20" width="20" border="0" class="lazy" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + hmPeople.get("EMP_IMAGE") %>" />
										<%} else { %>
						                 	<img height="20" width="20" border="0" class="lazy" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+strEmpId+"/"+IConstants.I_22x22+"/"+hmPeople.get("EMP_IMAGE")%>" />
						                <%} %> 
										<%=hmPeople.get("EMP_NAME") %> 
									</td>
									<td style="text-align: center;"><span id="billablebar<%=strEmpId%>">Loading..</span><span style="margin-left: 7px;"><%=uF.showData(hmBillable.get(strEmpId + "_BILLABLE_COUNT"),"0")%></span></td>
									<td style="text-align: center;"><span id="nonBillablebar<%=strEmpId%>">Loading..</span><span style="margin-left: 7px;"><%=uF.showData(hmNonBillable.get(strEmpId + "_NON_BILLABLE_COUNT"), "0")%></span></td>
									<td style="text-align: center;"><span id="otherbar<%=strEmpId%>">Loading..</span><span style="margin-left: 7px;"><%=uF.showData(hmOther.get(strEmpId + "_OTHER_COUNT"), "0")%></span>
										<script type="text/javascript">
										    //$(function() {
										    	 $('#billablebar<%=strEmpId%>').sparkline(new Array(<%=hmBillable.get(strEmpId + "_BILLABLE")%>), {type: 'bar', barColor: '#9ACD32'} );
											     $('#nonBillablebar<%=strEmpId%>').sparkline(new Array(<%=hmNonBillable.get(strEmpId + "_NON_BILLABLE")%>), {type: 'bar', barColor: '#4682B4'} );
											     $('#otherbar<%=strEmpId%>').sparkline(new Array(<%=hmOther.get(strEmpId + "_OTHER")%>), {type: 'bar', barColor: '#B22222'} );
										    //});
									    </script>
									</td>
								</tr>
								<%}%>
							<%} else {%>
								<tr><td colspan="3"><div class="msg nodata" style="width: 94%;"><span>Resources not available.</span></div> </td></tr>
							<%}%>	
						</table>
						
						<div class="viewmore">
				        	<a href="WorkEffort.action">
		           				<%--<img src="<%=request.getContextPath()%>/images1/icons/icons/forward_icon.png" title="Go to Work Effort.."/> --%>
		           				<i class="fa fa-forward" aria-hidden="true" title="Go to Work Effort.."></i>
		           				
		           				
		           			</a>
		           		</div>
                    </div>
                </div>
                <!-- /.box-body -->
            </div>
            <% } %>
            <%if(poFlag){%>
            <div class="box box-info">
                <div class="box-header with-border">
                    <h3 class="box-title">Project KPI</h3>
                    <div class="box-tools pull-right">
                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                    </div>
                </div>
                <!-- /.box-header -->
                <div class="box-body" style="padding: 5px;">
						<div style="float:left; width:100%;">
			             	<s:form theme="simple" name="frmProKpi" action="MyProjectDashboard" id="frmProKpi" method="POST" cssClass="formcss">
			                 	<div>
			                 		<s:hidden name="empId"></s:hidden>
				                 	<s:select theme="simple" name="proWorking" id="proWorking" headerKey="1" headerValue="Since last 1 Year"
			                            list="#{'2':'Since last 6 months','3':'Since last 3 months','4':'Since last 1 month'}" 
			                            onchange="document.frmProKpi.submit();" cssStyle="width: 155px !important;"/>
		                        </div>
	                        </s:form>
		                </div>
						<div style="float:left; width:100%;">
			             	<div style="float:left;width:49%; min-height:120px; text-align: center; margin-bottom: 15px;">
		                      <div id="guageProTimeKpi" class="gauge"></div>
		                    </div>
		                    <div style="float:left;width:49%; min-height:120px; text-align: center; margin-bottom: 15px;">
		                      <div id="guageProMoneyKpi" class="gauge"></div>
		                    </div>
		                    <script>
							    document.addEventListener("DOMContentLoaded", function(event) {
							        var g1 = new JustGage({
							            id: "guageProTimeKpi",
							            title: "",
							            label: "Time",
							            value: <%=uF.parseToDouble((String)request.getAttribute("PRO_ACTUAL_TIME_KPI"))%>,
							            min: 0,
							            max: <%=uF.parseToDouble((String)request.getAttribute("PRO_BUDGET_TIME_KPI"))%>,
							            decimals: 0,
							            gaugeWidthScale: 0.6,
							            levelColors: [
				                          "#FF0000",
				                          "#FFFF00",
				                          "#008000"
				                        ]
							        });
							        
							        var g2 = new JustGage({
							            id: "guageProMoneyKpi",
							            title: "",
							            label: "Money",
							            value: <%=uF.parseToDouble((String)request.getAttribute("PRO_ACTUAL_MONEY_KPI"))%>,
							            min: 0,
							            max: <%=uF.parseToDouble((String)request.getAttribute("PRO_BUDGET_MONEY_KPI"))%>,
							            decimals: 0,
							            gaugeWidthScale: 0.6,
							            levelColors: [
				                          "#FF0000",
				                          "#FFFF00",
				                          "#008000"
				                        ]
							        });
							        
							    });
						    </script>
			             </div>
                </div>
                <!-- /.box-body -->
            </div>
           <% } %>
           
           	<%-- <div class="box box-info">
                <div class="box-header with-border">
                    <h3 class="box-title">My Work KPI</h3>
                    <div class="box-tools pull-right">
                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                    </div>
                </div>
                <!-- /.box-header -->
                <div class="box-body" style="padding: 5px;">
						<div style="float:left; width:100%;">
		                	<s:form theme="simple" name="frmWorkKpi" action="MyProjectDashboard" id="frmWorkKpi" method="POST" cssClass="formcss">
			                	<div>
			                 		<s:hidden name="empId"></s:hidden>
				                 	<s:select theme="simple" name="taskWorking" id="taskWorking" headerKey="1" headerValue="Since last 1 Year"
			                            list="#{'2':'Since last 6 months','3':'Since last 3 months','4':'Since last 1 month'}"
				                        onchange="document.frmWorkKpi.submit();" cssStyle="width: 155px !important;"/>
		                        </div>
	                        </s:form>
		                 </div>
			             <div style="float:left; width:100%;">
			             	 <div style="float:left; width:49%; min-height:120px; text-align: center; margin-bottom: 15px;">
		                       <div id="guageTaskTimeKpi" class="gauge"></div>
		                     </div>
		                     <div style="float:left; width:49%; min-height:120px; text-align: center; margin-bottom: 15px;">
		                       <div id="guageTaskMoneyKpi" class="gauge"></div>
		                     </div>
		                     
		                     <script>
							    document.addEventListener("DOMContentLoaded", function(event) {
						        var g1 = new JustGage({
						            id: "guageTaskTimeKpi",
						            title: "",
						            label: "Time",
						            value: <%=uF.parseToDouble((String)request.getAttribute("TASK_ACTUAL_TIME_KPI"))%>,
						            min: 0,
						            max: <%=uF.parseToDouble((String)request.getAttribute("TASK_BUDGET_TIME_KPI"))%>,
						            decimals: 0,
						            gaugeWidthScale: 0.6,
						            levelColors: [
			                          "#FF0000",
			                          "#FFFF00",
			                          "#008000"
			                        ]
						        });
						        
						        var g2 = new JustGage({
						            id: "guageTaskMoneyKpi",
						            title: "",
						            label: "Money",
						            value: <%=uF.parseToDouble((String)request.getAttribute("TASK_ACTUAL_MONEY_KPI"))%>,
						            min: 0,
						            max: <%=uF.parseToDouble((String)request.getAttribute("TASK_BUDGET_MONEY_KPI"))%>,
						            decimals: 0,
						            gaugeWidthScale: 0.6,
						            levelColors: [
			                          "#FF0000",
			                          "#FFFF00",
			                          "#008000"
			                        ]
						        });
						        
						    	});
					    	</script>
			             </div>
                </div>
                <!-- /.box-body -->
            </div> --%>
		</section>
        	
        <section class="col-lg-4 col-md-6 col-sm-12 connectedSortable">
        
        	<div class="box box-info">
                <div class="box-body" style="padding: 5px; overflow-y: auto; max-height: 250px;">
					<ul class="site-stats-new" style="text-align: center;">
						<%if(poFlag || tlFlag) { %>
						<a href="MyWork.action?callFrom=MyProjects">
							<li class="bg_lh">
								<i class="fa fa-flag" aria-hidden="true"></i><strong><%=uF.parseToInt((String)request.getAttribute("nProject")) %></strong> <small>Projects</small>
							</li>
						</a>
						<% } %>
						<a href="MyWork.action">
							<li class="bg_lh">
								<i class="fa fa-tasks" aria-hidden="true"></i><strong><%=uF.parseToInt((String)request.getAttribute("nTask")) %></strong> <small>Tasks</small>
							</li>
						</a>
					</ul>
                </div>
            </div>
	        
	        <%-- <%if(poFlag) { %>    
		        <div class="box box-info">
	                <!-- /.box-header -->
	                <div class="box-body" style="padding: 5px; overflow-y: auto; max-height: 250px;">
						<ul class="site-stats-new">
							<% 	String stFYear = (String) request.getAttribute("stFYear");
								String endFYear = (String) request.getAttribute("endFYear");
							%>
							<li class="bg_lh"><i class="fa fa-handshake-o" aria-hidden="true"></i><strong><%=uF.parseToInt((String)request.getAttribute("totalCommitmentAmt")) %></strong> <small>My Commitments(FY: <%=stFYear %>-<%=endFYear %>)</small></li>
						</ul>
	                </div>
	                <!-- /.box-body -->
	            </div>
            <% } %> --%>
            
            <div class="box box-info">
	            <%
				List<Map<String, String>> alTeamMember = (List<Map<String, String>>) request.getAttribute("alTeamMember");
				if(alTeamMember == null) alTeamMember = new ArrayList<Map<String, String>>(); 
				%>
                <div class="box-header with-border">
                    <h3 class="box-title">Project Team Members </h3>
                    <div class="box-tools pull-right">
                    	<span class="label label-danger"><%=alTeamMember.size() %> members</span>
                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                    </div>
                </div>
                <!-- /.box-header -->
                <div class="box-body" style="padding: 5px; overflow-y: auto; max-height: 250px;">
                	<ul class="users-list clearfix">
							<%
								
								for(int i = 0; i < alTeamMember.size(); i++){
									if(i > 9){
										continue;
									}
									Map<String, String> hmInner = (Map<String, String>) alTeamMember.get(i);
							%>
							<li>
	                            <a href="javascript:void(0);" onclick="getEmpProfile('<%=hmInner.get("EMP_ID") %>');" title="<%=hmInner.get("EMP_NAME") %>">
	                            <%if(docRetriveLocation==null) { %>
	                            <img class="lazy img-circle" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + hmInner.get("EMP_IMAGE")%>">
	                            <%} else { %>
	                            <img class="lazy img-circle" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+hmInner.get("EMP_ID")+"/"+IConstants.I_60x60+"/"+hmInner.get("EMP_IMAGE")%>">
	                            <%} %> 
	                            <span class="users-list-name"><%=hmInner.get("EMP_NAME") %></span>
	                            </a>
		                    </li> 
							<%} %>
							<% if(alTeamMember == null || alTeamMember.isEmpty() || alTeamMember.size() == 0) { %>
								<li><span>No team available.</span></li>
							<%} %> 
							<%if(alTeamMember.size() > 10){ %>
								 <div class="viewmore">
						        	<a href="javascript:void(0);" onclick="getTeamMembers();">
				           				<%--<img src="<%=request.getContextPath()%>/images1/icons/icons/forward_icon.png" title="View Team Members...."/> --%>
				           				<i class="fa fa-forward" aria-hidden="true" title="View Team Members...."></i>
				           			</a>
				           		</div>
							<%} %> 
                    </ul>
                </div>
                <!-- /.box-body -->
            </div>
	            
			<div class="box box-info">
                <!-- /.box-header -->
                <div class="box-body" style="padding: 5px;">
                    <div class="rosterweek" style="width: 100%;">
						<div style="width: 98%;float: left;">
				          <div style="width: 45%; height: auto; float: left; padding: 5px;">
				               <p style="font-family: Digital; margin: 0px; font-weight: bold; font-size: 22px; text-align: right; "><%=alTeamMember.size() %></p>             
				               <p style="font-size: 14px; margin: 0px; text-align: right;">Team Members: <%=alTeamMember.size() %></p>                
				          </div>
				          
				          <div style="width: 45%; height: auto; float: right; padding: 5px;">
				               <p style="font-family: Digital; margin: 0px; font-weight: bold; font-size: 22px; text-align: right; "><%=uF.showData((String) request.getAttribute("nMemAssigned"),"0") %></p>             
				               <p style="font-size: 14px; margin: 0px; text-align: right;">Task Assigned: <%=uF.showData((String) request.getAttribute("nMemAssigned"),"0") %></p>                
				          </div>
				          
				          <div style="width: 45%; height: auto; float: right; padding: 5px;">
				               <p style="font-family: Digital; margin: 0px; font-weight: bold; font-size: 22px; text-align: right; "><%=uF.showData((String) request.getAttribute("nMemUnAssigned"),"0") %></p>             
				               <p style="font-size: 14px; margin: 0px; text-align: right;">Task Un-Assigned: <%=uF.showData((String) request.getAttribute("nMemUnAssigned"),"0") %></p>                
				          </div>
			          	</div>
			          	
			          	<div style="width: 100%; border-bottom: 1px solid #C4C4C4; float: left;"></div>
			          	
			          	<div style="width: 98%;float: left;">
				          <div style="width: 45%; height: auto; float: left; padding: 5px;">
				               <p style="font-family: Digital; margin: 0px; font-weight: bold; font-size: 22px; text-align: right; "><%=uF.showData((String) request.getAttribute("nTotalTask"),"0") %></p>             
				               <p style="font-size: 14px; margin: 0px; text-align: right;">Tasks/ Issues: <%=uF.showData((String) request.getAttribute("nTotalTask"),"0") %></p>                
				          </div>
				          
				          <div style="width: 45%; height: auto; float: right; padding: 5px;">
				               <p style="font-family: Digital; margin: 0px; font-weight: bold; font-size: 22px; text-align: right; "><%=uF.showData((String) request.getAttribute("nTaskAssigned"),"0") %></p>             
				               <p style="font-size: 14px; margin: 0px; text-align: right;">Assigned: <%=uF.showData((String) request.getAttribute("nTaskAssigned"),"0") %></p>                
				          </div>
				          
				          <div style="width: 45%; height: auto; float: right; padding: 5px;">
				               <p style="font-family: Digital; margin: 0px; font-weight: bold; font-size: 22px; text-align: right; "><%=uF.showData((String) request.getAttribute("nTaskUnAssigned"),"0") %></p>             
				               <p style="font-size: 14px; margin: 0px; text-align: right;">Un-Assigned: <%=uF.showData((String) request.getAttribute("nTaskUnAssigned"),"0") %></p>                
				          </div>
			          	</div>
					</div>
                </div>
                <!-- /.box-body -->
            </div>
            
            <!-- Temparary  -->
            <div class="box box-info">
                <div class="box-header with-border">
                    <h3 class="box-title">My Work KPI</h3>
                    <div class="box-tools pull-right">
                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                    </div>
                </div>
                <!-- /.box-header -->
                <div class="box-body" style="padding: 5px;">
						<div style="float:left; width:100%;">
		                	<s:form theme="simple" name="frmWorkKpi" action="MyProjectDashboard" id="frmWorkKpi" method="POST" cssClass="formcss">
			                	<div>
			                 		<s:hidden name="empId"></s:hidden>
				                 	<s:select theme="simple" name="taskWorking" id="taskWorking" headerKey="1" headerValue="Since last 1 Year"
			                            list="#{'2':'Since last 6 months','3':'Since last 3 months','4':'Since last 1 month'}"
				                        onchange="document.frmWorkKpi.submit();" cssStyle="width: 155px !important;"/>
		                        </div>
	                        </s:form>
		                 </div>
			             <div style="float:left; width:100%;">
			             	 <div style="float:left; width:49%; min-height:120px; text-align: center; margin-bottom: 15px;">
		                       <div id="guageTaskTimeKpi" class="gauge"></div>
		                     </div>
		                     <div style="float:left; width:49%; min-height:120px; text-align: center; margin-bottom: 15px;">
		                       <div id="guageTaskMoneyKpi" class="gauge"></div>
		                     </div>
		                     
		                     <script>
							    document.addEventListener("DOMContentLoaded", function(event) {
						        var g1 = new JustGage({
						            id: "guageTaskTimeKpi",
						            title: "",
						            label: "Time",
						            value: <%=uF.parseToDouble((String)request.getAttribute("TASK_ACTUAL_TIME_KPI"))%>,
						            min: 0,
						            max: <%=uF.parseToDouble((String)request.getAttribute("TASK_BUDGET_TIME_KPI"))%>,
						            decimals: 0,
						            gaugeWidthScale: 0.6,
						            levelColors: [
			                          "#FF0000",
			                          "#FFFF00",
			                          "#008000"
			                        ]
						        });
						        
						        var g2 = new JustGage({
						            id: "guageTaskMoneyKpi",
						            title: "",
						            label: "Money",
						            value: <%=uF.parseToDouble((String)request.getAttribute("TASK_ACTUAL_MONEY_KPI"))%>,
						            min: 0,
						            max: <%=uF.parseToDouble((String)request.getAttribute("TASK_BUDGET_MONEY_KPI"))%>,
						            decimals: 0,
						            gaugeWidthScale: 0.6,
						            levelColors: [
			                          "#FF0000",
			                          "#FFFF00",
			                          "#008000"
			                        ]
						        });
						        
						    	});
					    	</script>
			             </div>
                </div>
                <!-- /.box-body -->
            </div>
            <!-- Temparary  -->
            
            
            <%-- <div class="box box-info">
                <div class="box-header with-border">
                    <h3 class="box-title">Projects(Tasks) </h3>
                    <div class="box-tools pull-right">
                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                    </div>
                </div>
                <!-- /.box-header -->
                <div class="box-body" style="padding: 5px;">
                    <div class="rosterweek" style="width: 100%;">
						<div id="chartPiediv" style="width:100%; height:400px;"></div>
						<script>
				            var chart2;
				            var chartData2 = [<%=request.getAttribute("sbProTaskPie")%>];
				            var legend2;
				
				            AmCharts.ready(function () {
				                // PIE CHART
				                chart2 = new AmCharts.AmPieChart();
				                chart2.dataProvider = chartData2;
				                chart2.titleField = "Project";
				                chart2.valueField = "cnt";
				                chart2.labelRadius = -30;
				                chart2.labelText = "";
				                chart2.balloonText = "[[title]]<br><span style='font-size:14px'><b>[[value]]</b> ([[percents]]%)</span>";
				 				chart2.legend = {
				 					   	"position":"bottom"
				 					  },
				                chart2.write("chartPiediv");
				            });
				        </script>
					</div>
                </div>
                <!-- /.box-body -->
            </div> --%>
            
            
        </section>
        <section class="col-lg-4 col-md-6 col-sm-12 connectedSortable paddingleft0">
            <%if(poFlag || tlFlag){ %>
            <div class="box box-info">
                <div class="box-header with-border">
                    <h3 class="box-title">Team Weekly Work Progress(Overall)</h3>
                    <div class="box-tools pull-right">
                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                    </div>
                </div>
                <!-- /.box-header -->
                <div class="box-body" style="padding: 5px;">
					<div id="chartWeeklydiv" style="width:100%; height:400px;"></div>
					<script>
			            var chart1;
			            var chartData1 = [<%=request.getAttribute("sbWork")%>];
			            
			            AmCharts.ready(function () {
			                // SERIAL CHART
			                chart1 = new AmCharts.AmSerialChart();
			
			                chart1.dataProvider = chartData1;
			                chart1.marginTop = 10;
			                chart1.categoryField = "week";
			
			                // AXES
			                // Category
			                var categoryAxis1 = chart1.categoryAxis;
			                categoryAxis1.gridAlpha = 0.07;
			                categoryAxis1.axisColor = "#DADADA";
			                categoryAxis1.startOnAxis = true;
			
			                // Value
			                var valueAxis1 = new AmCharts.ValueAxis();
			                valueAxis1.stackType = "regular"; // this line makes the chart "stacked"
			                valueAxis1.gridAlpha = 0.07;
			                valueAxis1.title = "Number of Tasks";
			                chart1.addValueAxis(valueAxis1);
			
			                // GRAPHS
			                // first graph
			                var graph1 = new AmCharts.AmGraph();
			                graph1.type = "line"; // it's simple line graph
			                graph1.title = "Completed";
			                graph1.valueField = "completed";
			                graph1.lineAlpha = 0;
			                graph1.fillAlphas = 0.6; // setting fillAlphas to > 0 value makes it area graph
			                graph1.balloonText = "<span style='font-size:14px; color:#000000;'>Completed: <b>[[value]]</b></span>";
			               // graph1.hidden = true;
			               	graph1.fillColors = "#9ACD32";
			                chart1.addGraph(graph1);
			
			                // second graph
			                graph1 = new AmCharts.AmGraph();
			                graph1.type = "line";
			                graph1.title = "Active";
			                graph1.valueField = "active";
			                graph1.lineAlpha = 0;
			                graph1.fillAlphas = 0.6;
			                graph1.balloonText = "<span style='font-size:14px; color:#000000;'>Active: <b>[[value]]</b></span>";
			               	graph1.fillColors = "#4682B4";
			                chart1.addGraph(graph1);
			
			                // third graph
			                graph1 = new AmCharts.AmGraph();
			                graph1.type = "line";
			                graph1.title = "Overdue";
			                graph1.valueField = "overdue";
			                graph1.lineAlpha = 0;
			                graph1.fillAlphas = 0.6;
			                graph1.balloonText = "<span style='font-size:14px; color:#000000;'>Overdue: <b>[[value]]</b></span>";
			               	graph1.fillColors = "#B22222";
			                chart1.addGraph(graph1);
			
			                // LEGEND
			                var legend1 = new AmCharts.AmLegend();
			                legend1.position = "top";
			                legend1.valueText = "[[value]]";
			                legend1.valueWidth = 100;
			                legend1.valueAlign = "left";
			                legend1.equalWidths = false;
			                legend1.periodValueText = "total: [[value.sum]]"; // this is displayed when mouse is not over the chart.
			                chart1.addLegend(legend1);
			
			                // CURSOR
			                var chartCursor1 = new AmCharts.ChartCursor();
			                chartCursor1.cursorAlpha = 0;
			                chart1.addChartCursor(chartCursor1);
			
			                // SCROLLBAR
			                var chartScrollbar1 = new AmCharts.ChartScrollbar();
			                chartScrollbar1.color = "#FFFFFF";
			                chart1.addChartScrollbar(chartScrollbar1);
			
			                // WRITE
			                chart1.write("chartWeeklydiv");
			            });
			        </script>
			        
			         <div class="viewmore">
			        	<a href="WorkProgress.action">
	           				<%--<img src="<%=request.getContextPath()%>/images1/icons/icons/forward_icon.png" title="Go to Weekly Work Progress.."/> --%>
	           				<i class="fa fa-forward" aria-hidden="true" title="Go to Weekly Work Progress.."></i>
	           			</a>
	           		</div>
                </div>
                <!-- /.box-body -->
            </div>
            <% } %>
            
            <% if(poFlag || tlFlag) { %>
            <div class="box box-info">
                <div class="box-header with-border">
                    <h3 class="box-title">Team Work Progress</h3>
                    <div class="box-tools pull-right">
                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                    </div>
                </div>
                <!-- /.box-header -->
                <div class="box-body" style="padding: 5px; overflow-y: auto; max-height: 250px;">
                    <div class="rosterweek" style="width: 100%;">
						<%
							Map<String, String> hmProject = (Map<String, String>) request.getAttribute("hmProject");
						 	if (hmProject == null)hmProject = new LinkedHashMap<String, String>();
						 	Map<String, String> hmCompleteTask = (Map<String, String>) request.getAttribute("hmCompleteTask");
						 	if (hmCompleteTask == null)hmCompleteTask = new HashMap<String, String>();
						 	Map<String, String> hmActiveTask = (Map<String, String>) request.getAttribute("hmActiveTask");
						 	if (hmActiveTask == null)hmActiveTask = new HashMap<String, String>();
						 	Map<String, String> hmOverdueTask = (Map<String, String>) request.getAttribute("hmOverdueTask");
						 	if (hmOverdueTask == null)hmOverdueTask = new HashMap<String, String>(); 
					 	%>
					 	<table class="table table-striped table-bordered">
						<%
							if (hmProject != null && hmProject.size() > 0) {
						%>
							<tr>
								<th width="35%">Project Name</th>
								<th>Completed</th>
								<th>Active</th>
								<th>Overdue</th>
							</tr>
							
							<%
								Iterator<String> it = hmProject.keySet().iterator();
								while (it.hasNext()) {
									String strProId = it.next();
									String strProName = hmProject.get(strProId);
							%>
							<tr>
								<td><%=strProName%></td>
								<td style="text-align: center;"><span id="completedbar<%=strProId%>">Loading..</span><span style="margin-left: 7px;"><%=uF.showData(hmCompleteTask.get(strProId + "_COMPLETE_COUNT"),"0")%></span></td>
								<td style="text-align: center;"><span id="activebar<%=strProId%>">Loading..</span><span style="margin-left: 7px;"><%=uF.showData(hmActiveTask.get(strProId + "_ACTIVE_COUNT"), "0")%></span></td>
								<td style="text-align: center;"><span id="overduebar<%=strProId%>">Loading..</span><span style="margin-left: 7px;"><%=uF.showData(hmOverdueTask.get(strProId + "_OVERDUE_COUNT"), "0")%></span>
									<script type="text/javascript">
									   //$(function() {
									    	 $('#completedbar<%=strProId%>').sparkline(new Array(<%=hmCompleteTask.get(strProId + "_COMPLETE")%>), {type: 'bar', barColor: '#9ACD32'} );
										     $('#activebar<%=strProId%>').sparkline(new Array(<%=hmActiveTask.get(strProId + "_ACTIVE")%>), {type: 'bar', barColor: '#4682B4'} );
										     $('#overduebar<%=strProId%>').sparkline(new Array(<%=hmOverdueTask.get(strProId + "_OVERDUE")%>), {type: 'bar', barColor: '#B22222'} );
									    //});
								    </script>
								</td>
							</tr>
							
							<%}%>
						<% } else { %>
							<tr><td colspan="3"><div class="msg nodata" style="width: 94%;"><span>Projects not available.</span></div> </td></tr>
						<% } %>		
						</table>
						
						 <div class="viewmore">
				        	<a href="WorkProgress.action">
		           				<%--<img src="<%=request.getContextPath()%>/images1/icons/icons/forward_icon.png" title="Go to Weekly Work Progress.."/> --%>
		           				<i class="fa fa-forward" aria-hidden="true" title="Go to Weekly Work Progress.."></i>
		           				
		           				
		           			</a>
		           		</div>					
                    </div>
                </div>
                <!-- /.box-body -->
            </div>
            <% } %>
            
            <%-- <%if(poFlag) { %>
            <div class="box box-info">
                <div class="box-header with-border">
                    <h3 class="box-title">Project &amp; Business Snapshot </h3>
                    <div class="box-tools pull-right">
                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                    </div>
                </div>
                <!-- /.box-header -->
                <div class="box-body" style="padding: 5px;">
                    <div class="rosterweek" style="width: 100%;">
						<div style="float:left; width: 100%;">
			             	<s:form theme="simple" name="frmProBusinessSnapshot" action="MyProjectDashboard" id="frmProBusinessSnapshot" method="POST" cssClass="formcss">
			                 	<div>
			                 		<s:hidden name="empId"></s:hidden>
				                 	<s:select theme="simple" name="proBusinessSnapshot" id="proBusinessSnapshot" headerKey="1" headerValue="Since last 1 Year"
			                            list="#{'2':'Since last 6 months', '3':'Since last 3 months', '4':'Since last 1 month'}" onchange="document.frmProBusinessSnapshot.submit();" cssStyle="width: 155px !important;"/>
		                        </div>
	                        </s:form>
		                </div>
		                
						<div style="width: 98%;float: left;">
				          <div style="width: 45%; height: auto; float: left; padding: 5px;">
				               <p style="font-family: Digital; margin: 0px; font-weight: bold; font-size: 22px; text-align: right;"><%=uF.showData((String) request.getAttribute("nLivePro"),"0") %></p>             
				               <p style="font-size: 14px; margin: 0px; text-align: right;">Live Projects</p>                
				          </div>
				          
				          <div style="width: 45%; height: auto; float: right; padding: 5px;">
				               <p style="font-family: Digital; margin: 0px; font-weight: bold; font-size: 22px; text-align: right;"><%=uF.showData((String) request.getAttribute("nNewPro"),"0") %></p>             
				               <p style="font-size: 14px; margin: 0px; text-align: right;">New Projects</p>                
				          </div>
				          
				          <div style="width: 45%; height: auto; float: left; padding: 5px;">
				               <p style="font-family: Digital; margin: 0px; font-weight: bold; font-size: 22px; text-align: right;"><%=uF.showData((String) request.getAttribute("nOnTrackPro"),"0") %></p>             
				               <p style="font-size: 14px; margin: 0px; text-align: right;">On-Track</p>                
				          </div>
				          
				          <div style="width: 45%; height: auto; float: right; padding: 5px;">
				               <p style="font-family: Digital; margin: 0px; font-weight: bold; font-size: 22px; text-align: right;"><%=uF.showData((String) request.getAttribute("nDelayedPro"),"0") %></p>             
				               <p style="font-size: 14px; margin: 0px; text-align: right;">Delayed</p>                
				          </div>
			          	</div>
			          	
			          	<div style="width: 100%; border-bottom: 1px solid #C4C4C4; float: left;"></div>
			          	
			          	<div style="width: 98%;float: left;">
				          <div style="width: 45%; height: auto; float: left; padding: 5px;">
				               <p style="font-family: Digital; margin: 0px; font-weight: bold; font-size: 22px; text-align: right;"><%=strCurr %> <%=uF.showData((String) request.getAttribute("dblProfit"),"0") %></p>             
				               <p style="font-size: 14px; margin: 0px; text-align: right;">Profit</p>                
				          </div>
				          
				          <div style="width: 45%; height: auto; float: right; padding: 5px;">
				               <p style="font-family: Digital; margin: 0px; font-weight: bold; font-size: 22px; text-align: right;"><%=uF.showData((String) request.getAttribute("dblProfitMargin"),"0") %>%</p>             
				               <p style="font-size: 14px; margin: 0px; text-align: right;">Profit Margin</p>                
				          </div>
				          
				          <div style="width: 45%; height: auto; float: left; padding: 5px;">
				               <p style="font-family: Digital; margin: 0px; font-weight: bold; font-size: 22px; text-align: right;"><%=strCurr %> <%=uF.showData((String) request.getAttribute("dblBugedtedAmt"),"0") %></p>             
				               <p style="font-size: 14px; margin: 0px; text-align: right;">Budgeted</p>                
				          </div>
				          
				          <div style="width: 45%; height: auto; float: right; padding: 5px;">
				               <p style="font-family: Digital; margin: 0px; font-weight: bold; font-size: 22px; text-align: right;"><%=strCurr %> <%=uF.showData((String) request.getAttribute("dblActualAmt"),"0") %></p>             
				               <p style="font-size: 14px; margin: 0px; text-align: right;">Actuals</p>                
				          </div>
				          
				          <div style="width: 45%; height: auto; float: left; padding: 5px;">
				               <p style="font-family: Digital; margin: 0px; font-weight: bold; font-size: 22px; text-align: right;"><%=strCurr %> <%=uF.showData((String) request.getAttribute("dblBilled"),"0") %></p>             
				               <p style="font-size: 14px; margin: 0px; text-align: right;">Billed</p>                
				          </div>
				          
				          <div style="width: 45%; height: auto; float: right; padding: 5px;">
				               <p style="font-family: Digital; margin: 0px; font-weight: bold; font-size: 22px; text-align: right;"><%=strCurr %> <%=uF.showData((String) request.getAttribute("dblReceived"),"0") %></p>             
				               <p style="font-size: 14px; margin: 0px; text-align: right;">Received</p>                
				          </div>
			          	</div>
			          	<div id="chartDonutdiv" style="height: 300px; font-size: 11px;" align="center"></div>
			          	<script>
				            var chartData3 = [<%=request.getAttribute("sbBillsDonut")%>];
			            	var chart3 = AmCharts.makeChart( "chartDonutdiv", {
			            	  "theme": "light",
			            	  "type": "serial",
			            	  "depth3D": 100,
			            	  "angle": 30,
			            	  "autoMargins": true,
			            	  "dataProvider": chartData3,
			            	  "valueAxes": [ {
			            	    "stackType": "100%", 
			            	    "gridAlpha": 0
			            	  } ],
			            	  "graphs": [ {
			            	    "type": "column",
			            	    "topRadius": 1,
			            	    "columnWidth": 1,
			            	    "showOnAxis": true,
			            	    "lineThickness": 2,
			            	    "lineAlpha": 0.5,
			            	    "lineColor": "#FFFFFF",
			            	    "fillColors": "#8d003b",
			            	    "fillAlphas": 0.8,
			            	    "valueField": "Received",
			            	    "balloonText" : "<span style='font-size:14px; color:#000000;'>Received: <b>[[value]]%</b></span>"
			            	  }, { 
			            	    "type": "column",
			            	    "topRadius": 1,
			            	    "columnWidth": 1,
			            	    "showOnAxis": true,
			            	    "lineThickness": 2,
			            	    "lineAlpha": 0.5, 
			            	    "lineColor": "#cdcdcd",
			            	    "fillColors": "#cdcdcd",
			            	    "fillAlphas": 0.5,
			            	    "valueField": "Pending",
			            	    "balloonText" : "<span style='font-size:14px; color:#000000;'>Pending: <b>[[value]]%</b></span>"
			            	  } ],
		
			            	  "categoryField": "category",
			            	  "categoryAxis": {
			            	    "axisAlpha": 0,
			            	    "labelOffset": 40,
			            	    "gridAlpha": 0
			            	  },
			            	  "export": {
			            	    "enabled": true
			            	  }
			            	} );
				            
				        </script>
					</div>
                </div>
                <!-- /.box-body -->
            </div>
            <% } %> --%>
            
            <%-- <div class="box box-info">
                <div class="box-header with-border">
                    <h3 class="box-title">Requests &amp; Alerts</h3>
                    <div class="box-tools pull-right">
                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                    </div>
                </div>
                <!-- /.box-header -->
                <div class="box-body" style="padding: 5px; overflow-y: auto; max-height: 250px;">
                	<div class="leaves-div">
						<%
						List<String> alNotifications = (List<String>) request.getAttribute("alNotifications");
						if (alNotifications == null)alNotifications = new ArrayList<String>();
						
						int nNotificationCnt = 0;
						if(alNotifications!=null && !alNotifications.isEmpty() && alNotifications.size() > 0) {
							for(int i = 0; i < alNotifications.size(); i++) {
								nNotificationCnt++;
					 	%>
					 			<div class="issues"><%=alNotifications.get(i) %></div>
					 		<% } %>
					 		<% if (nNotificationCnt == 25) { %>
								<div class="issues"><a href="Notifications.action">See All </a></div>
							<% } %>
					 	<% } else { %>
								<div class="issues">Requests &amp; Alerts not available.</div>
					 	<% } %>
				 	</div>
                </div>
                <!-- /.box-body -->
            </div> --%>
	            
        </section>	
	</div>
</section>


	<div class="modal" id="modalInfo" role="dialog">
	    <div class="modal-dialog">
	        <!-- Modal content-->
	        <div class="modal-content">
	            <div class="modal-header">
	                <button type="button" class="close" data-dismiss="modal">&times;</button>
	                <h4 class="modal-title">-</h4>
	            </div>
	            <div class="modal-body" style="height:auto;overflow-y:auto;padding-left: 25px;">
	            </div>
	            <div class="modal-footer">
	                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
	            </div>
	        </div>
	    </div>
	</div>
