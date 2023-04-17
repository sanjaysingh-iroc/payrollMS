<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>


<%
	UtilityFunctions uF = new UtilityFunctions();
	String learningVideoId = (String)request.getParameter("learningVideoId");
	String fromPage = (String)request.getParameter("fromPage");
	String lPlanId = (String)request.getParameter("lPlanId");
	//System.out.println("LVD.jsp--lPlanId="+request.getParameter("lPlanId"));
	List<List<String>> alVideoDetails = (List) request.getAttribute("alVideoDetails");
	if(alVideoDetails == null) alVideoDetails = new ArrayList<List<String>>();
	
	List<List<String>> alSubVideoDetails = (List) request.getAttribute("alSubVideoDetails");
	if(alSubVideoDetails == null) alSubVideoDetails = new ArrayList<List<String>>();
	
	List<String> alAssignVideo = (List<String>) request.getAttribute("alAssignVideo");
	if(alAssignVideo == null) alAssignVideo = new ArrayList<String>();
	
%>

		      	<div class="box box-body" style="border-top: 3px solid #d2d6de;">
					<%if(alVideoDetails != null && alVideoDetails.size()>0 ) { 
						for (int i = 0; i < alVideoDetails.size(); i++) {
							List<String> alInner = alVideoDetails.get(i);
							if(alInner!= null && alInner.size()>0) {
					%>
				 			   <div class="box-header with-border">
				                   <%if(fromPage != null && fromPage.equalsIgnoreCase("LD")) { %>
				                    <h3 class="box-title">
				                    <% if(!alAssignVideo.contains(alInner.get(0))){ %>
										<a onclick="deleteVideo('<%=alInner.get(0) %>')" href="javascript:void(0)" class="del" style="color:rgb(221, 0, 0);"><i class="fa fa-trash" aria-hidden="true"></i></a>
									<%} %>
										<a onclick="editVideo('<%=alInner.get(0) %>','LD')" href="javascript:void(0)"  style="padding:0"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
										<%-- <a href="javascript:void(0);" onclick="trainingCalendar('<%=alInner.get(0) %>')" style=" margin-right: 3px;"><i class="fa fa-calendar" aria-hidden="true"></i></a>
										<a href="javascript:void(0);" onclick="trainingScheduleAllDayDetails('<%=alInner.get(0) %>')" style=" margin-right: 5px; margin-top: 2px;" title="List"><i class="fa fa-list" aria-hidden="true"></i></a> --%>
				                    	
				                    	<div style="display:inline;"><%=uF.showData(alInner.get(1), "")%></div>
									</h3>
									<% } %>
									 <div class="box-body" style="padding: 5px; overflow-y: auto;">
									    
									    <div style="width:100%;">
					                    <table class="table" width="100%">
											<tr>
												<th width="15%" align="right" style="border-top-color: #FFF;"> Video Title:</th>
												<td style="border-top-color: #FFF;"><%=uF.showData(alInner.get(1),"")%></td>
											</tr>
											<tr>
												<th valign="top" align="right">Video Link:</th>
												
												<td colspan="1"><%=uF.showData(alInner.get(4),"")%>
												<%System.out.println("alInner.get(4)="+alInner.get(4)); %>
												<%if(fromPage == null || (fromPage != null && !fromPage.equalsIgnoreCase("AL")) && alInner.get(4) != null) { 
												%>
													<% if(fromPage !=null && fromPage.equalsIgnoreCase("MyHr")){ %>
														<a href="<%=uF.showData(alInner.get(2),"")%>" target="_blank" style="padding-left:20px;" onclick="watchVideo('<%=fromPage %>','<%=alInner.get(0) %>','<%=lPlanId %>','')"><input type="button" class="btn btn-primary" value="Show"></a>
													<%} else{%>
												<!-- ===start parvez date: 11-11-2021=== -->
														<a href="<%=uF.showData(alInner.get(2),"")%>" target="_blank" style="padding-left:20px;"><input type="button" class="btn btn-primary" value="Preview"></a>
												<!-- ===end parvez date: 11-11-2021=== -->	
													<%} %>
													<%-- <a href="<%=uF.showData(alInner.get(2),"")%>" target="_blank" style="padding-left:20px;"><input type="button" class="btn btn-primary" value="Show"></a> --%>
												<%} %>
												</td>
											</tr>
											<tr>
												<th valign="top" align="right">Description:</th>
												<td colspan="1"><%=uF.showData(alInner.get(3),"-")%></td>
											</tr>
											<%if(alSubVideoDetails != null && alSubVideoDetails.size()>0 ) { 
												for (int j = 0; j < alSubVideoDetails.size(); j++) {
													List<String> alSubInner = alSubVideoDetails.get(j);
													if(alSubInner!= null && alSubInner.size()>0) {
											%>
												<tr>
													<th width="15%" align="right" style="border-top-color: #FFF;"> Sub Title:</th>
													<td style="border-top-color: #FFF;"><%=uF.showData(alSubInner.get(1),"")%></td>
												</tr>
												<tr>
													<th valign="top" align="right">Video Link:</th>
													
													<td colspan="1"><%=uF.showData(alSubInner.get(4),"")%>
													<%if(fromPage == null || (fromPage != null && !fromPage.equalsIgnoreCase("AL")) ) { 
													%>
														<% if(fromPage !=null && fromPage.equalsIgnoreCase("MyHr")){ %>
															<a href="<%=uF.showData(alSubInner.get(2),"")%>" target="_blank" style="padding-left:20px;" onclick="watchVideo('<%=fromPage %>','<%=alInner.get(0) %>','<%=lPlanId %>','<%=alSubInner.get(0) %>')"><input type="button" class="btn btn-primary" value="Show"></a>
														<%} else { %>
													<!-- ===start parvez date: 11-11-2021=== -->
															<a href="<%=uF.showData(alSubInner.get(2),"")%>" target="_blank" style="padding-left:20px;"><input type="button" class="btn btn-primary" value="Preview"></a>
													<!-- ===end parvez date: 11-11-2021=== -->
														<%} %>
														
														<%-- <a href="<%=uF.showData(alSubInner.get(2),"")%>" target="_blank" style="padding-left:20px;"><input type="button" class="btn btn-primary" value="Show"></a> --%>
													<%} %>
													</td>
												</tr>
												<tr>
													<th valign="top" align="right">Description:</th>
													<td colspan="1"><%=uF.showData(alSubInner.get(3),"-")%></td>
												</tr>
												<%	}
												}
											} %>
										</table>
										</div>
					                </div>
							   </div>	
					 <%		}
						} %>
					<%} else { %>
						<div class="nodata msg">No Videos summary.</div>
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
	function deleteVideo(videoId) {
		/* $("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>'); */
		$("#divCDResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		if(confirm('Are you sure, you want to delete this plan?')) {
			$.ajax({
				type:'GET',
				url:'AddNewVideo.action?frmpage=LD&operation=D&ID='+videoId,
				success:function(result){
					/* $("#divResult").html(result); */
					$("#divCDResult").html(result);
				}
			});
		}
	}
	
	 function editVideo(videoId,fromPage) {
		 	
			/* $("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>'); */
			$("#divCDResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$.ajax({ 
				type : 'POST',
				url: 'AddNewVideo.action?operation=E&frompage='+fromPage+'&ID='+videoId,
				cache: true,
				success: function(result){
					//alert("addTrainingplan result1==>"+result);
					/* $("#divResult").html(result); */
					$("#divCDResult").html(result);
		   		}
			});
	 }
	 
	 
	/*  function updateVideoStatus(videoId, lPlanId, count, videoName, fromPage) {
		
		var action = "ViewVideo.action?dataType=V&videoId=" + videoId + "&lPlanId=" + lPlanId;
		showVideo(videoId,videoName,fromPage,lPlanId);
		getContent('videoActionDiv'+count, action);
		
	 } */
	 
	function watchVideo(fromPage,videoId,lPlanId,subVideoId){
		
		$("#divMyHRData").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({ 
			type : 'POST',
			url : "ViewVideo.action?dataType=I&videoId=" + videoId + "&lPlanId=" + lPlanId + "&subVideoId=" + subVideoId,
			cache: true,
			success: function(result){
				//alert("addTrainingplan result1==>"+result);
				//$("#divMyHRData").html(result); 
				$.ajax({ 
					type : 'POST',
					url : "MyLearningPlan.action?dataType=L&fromPage="+fromPage,
					cache: true,
					success: function(result){
						//alert("addTrainingplan result1==>"+result);
						
						$("#divMyHRData").html(result);
				   	}
				});
		   	}
		});
		
		
	 } 
</script>
