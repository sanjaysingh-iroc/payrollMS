<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>


<%
	UtilityFunctions uF = new UtilityFunctions();
	String planId = (String)request.getParameter("planId");
	String fromPage = (String)request.getParameter("fromPage");
	List<String> alAssignTrainPlan = (List<String>) request.getAttribute("alAssignTrainPlan");
	if(alAssignTrainPlan == null) alAssignTrainPlan = new ArrayList<String>();
	
	List<List<String>> alTrainingPlan = (List) request.getAttribute("alTrainingPlan");
	if(alTrainingPlan == null) alTrainingPlan = new ArrayList<List<String>>();
%>

		      	<div class="box box-body" style="border-top: 3px solid #d2d6de;">
					<%if(alTrainingPlan != null && alTrainingPlan.size()>0 ) { 
						for (int i = 0; i < alTrainingPlan.size(); i++) {
							List<String> alInner = alTrainingPlan.get(i);
							if(alInner!= null && alInner.size()>0) {
					%>
				 			   <div class="box-header with-border">
				                    <h3 class="box-title">
				                    	<% if(!alAssignTrainPlan.contains(alInner.get(0))){ %>
												<a onclick="deleteTrainingPlan('<%=alInner.get(0) %>')" href="javascript:void(0)" class="del" style="color:rgb(221, 0, 0);"><i class="fa fa-trash" aria-hidden="true"></i></a>
										<%} %>
										<a onclick="editTrainingPlan('<%=alInner.get(0) %>','LD')" href="javascript:void(0)"  style="padding:0"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
										<a href="javascript:void(0);" onclick="trainingCalendar('<%=alInner.get(0) %>')" style=" margin-right: 3px;"><i class="fa fa-calendar" aria-hidden="true"></i></a>
										<a href="javascript:void(0);" onclick="trainingScheduleAllDayDetails('<%=alInner.get(0) %>')" style=" margin-right: 5px; margin-top: 2px;" title="List"><i class="fa fa-list" aria-hidden="true"></i></a>
				                    	
				                    	<div style="display:inline;"><%=uF.showData(alInner.get(1), "")%></div>
									</h3>
									 <div class="box-body" style="padding: 5px; overflow-y: auto;">
									    <!-- <div id="close_divTraining_cal" style="display:none;">
								    	   <div class="box-tools pull-right">
												<a href="javascript:void(0);" onclick="closeCalender()" class="close-font" style="margin-right:20px;"/>
										  </div>
								    	</div>
									    <div style="width:100%;display:none;" id ="divTraining_cal">
									    	
									    </div> -->
									    <div style="width:100%;">
					                    <table class="table" width="100%">
											<tr><th width="15%" align="right" style="border-top-color: #FFF;"> Organization:</th><td style="border-top-color: #FFF;"><%=uF.showData(alInner.get(7),"")%></td></tr>
											<tr><th valign="top" align="right">Location:</th><td colspan="1"><%=uF.showData(alInner.get(8),"")%></td></tr>
											<tr><th valign="top" align="right">Duration:</th><td colspan="1"><%=uF.showData(alInner.get(3),"-")%></td></tr>
											<tr><th align="right">Start Date:</th><td><%=uF.showData(alInner.get(4),"-")%></td></tr>
											<tr><th align="right">End Date:</th><td><%=uF.showData(alInner.get(5),"-")%></td></tr>
											<tr><th align="right">Certifications:</th>	
												<td><%=alInner.get(6)%>
													<%if(alInner.get(6).equals("YES")) { %>
														<a href="javascript:void(0)" onclick="previewcertificate('<%=alInner.get(9)%>','<%=alInner.get(10)%>');">Preview</a>
													<%} %>
												</td>
											</tr>
											
										</table>
										</div>
					                </div>
							   </div>	
					 <%		}
						} %>
					<%} else { %>
						<div class="nodata msg">No training plan summary.</div>
					<%} %>	               
             	</div>
    

<div class="modal" id="modalInfo" role="dialog">
    <div class="modal-dialog modal-dialog1">
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
<script>
	function deleteTrainingPlan(planId) {
		$("#divTPNamesResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		if(confirm('Are you sure, you want to delete this plan?')) {
			$.ajax({
				type:'GET',
				url:'AddTrainingPlan.action?frmpage=LD&operation=D&ID='+planId,
				success:function(result){
					$("#divTPNamesResult").html(result);
				}
			});
		}
	}
	
	 function editTrainingPlan(planId,fromPage) {
		 	
			$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$.ajax({ 
				type : 'POST',
				url: 'AddTrainingPlan.action?operation=E&frmpage='+fromPage+'&ID='+planId,
				cache: true,
				success: function(result){
					//alert("addTrainingplan result1==>"+result);
					$("#divResult").html(result);
		   		}
			});
	 }
</script>

