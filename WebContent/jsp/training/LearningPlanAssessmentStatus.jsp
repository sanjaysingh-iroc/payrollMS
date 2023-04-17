<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>


<style>
 
#greenbox {
height: 11px;
background-color:#00FF00; /* the critical component */
}
#redbox {
height: 11px;
background-color:#FF0000; /* the critical component */
}
#yellowbox {
height: 11px;
background-color:#FFFF00; /* the critical component */
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

</style>

<script type="text/javascript">
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
	
	function getMemberData(memberId, empId, id, empName, role) {
		var dialogEdit = '.modal-body';
		 $(dialogEdit).empty();
		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		 $("#modalInfo").show();
		 $(".modal-title").html('Score Summary of '+empName+((role=='')?'[Aggregate]':' [Role: '+role+']'));
		 $.ajax({
			url : "AppraisalScoreStatus.action?id=" + id + "&empid=" + empId + "&type=popup&memberId=" + memberId,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});

	} 


	function showAllQuestion(appid,empId,usertypeId,readstatus) {
		var dialogEdit = '.modal-body';
		 $(dialogEdit).empty();
		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		 $("#modalInfo").show();
		 $(".modal-title").html('Reviews');
		 $.ajax({
				url : "ShowAllSingleOpenWithoutMarkQue.action?fromPage=LD&appid="+appid+"&empId="+empId+"&usertypeId="+usertypeId+"&readstatus="+readstatus,
				cache : false,
				success : function(data) {
					$(dialogEdit).html(data);
				}
			});
	}
	
	function closePopup(){
		$(dialogEdit).dialog('close');
	}	
	
	
function openEmployeeProfilePopup(empId) {
	
	var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 $(".modal-title").html('Employee Information');
	 if($(window).width() >= 1100){
		 $(".modal-dialog").width(1100);
	 }
	 $.ajax({
			//url : "ApplyLeavePopUp.action",  
			url :"MyProfile.action?empId="+empId+"&proPopup=proPopup" ,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	 }


function learningPlanAssessmentFinalize(learningId, empId, trainingId, assessmentId,remarktype) {
	/* if(remarktype == 1){
		window.location = "LearningPlanAssessmentFinalize.action?learningId=" + learningId + "&empid=" + empId + "&trainingId=" + trainingId
		+ "&assessmentId=" + assessmentId + "&remarktype=" + remarktype;
	} else { */
		var dialogEdit = '.modal-body';
		 $(dialogEdit).empty();
		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		 $("#modalInfo").show();
		 $(".modal-title").html('Finalisation');
		 if($(window).width() > 900){
			 $(".modal-dialog").width(900);
		 }
		 $.ajax({
				url : "LearningPlanAssessmentFinalize.action?learningId=" + learningId + "&empid=" + empId + "&trainingId=" + trainingId
				+ "&assessmentId=" + assessmentId+ "&remarktype=" + remarktype,
				cache : false,
				success : function(data) {
					$(dialogEdit).html(data);
				}
			});
	//} 
}


function addTrainingFeedback(trainingId, lPlanId, empID) {
	var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 $(".modal-title").html("Training Feedback Questions");
	 $.ajax({
			url : "AddTrainingFeedbackByTrainerPopup.action?fromPage=LD&trainingId="+trainingId+"&lPlanId="+lPlanId+"&empID="+empID,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}


	function trainingFeedbackPreview(trainingId, lPlanId, empID, type) {
		var dialogEdit = '.modal-body';
	 	$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 	$("#modalInfo").show();
	 	$(".modal-title").html("Training Feedback Summary");
		
	 	$.ajax({
			url : "TrainingFeedbackSummary.action?trainingId="+trainingId+"&lPlanId="+lPlanId+"&empID="+empID+"&type="+type,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}


	function addTrainingStatus(trainingId, lPlanId) {

		var dialogEdit = '.modal-body';
	 	$(dialogEdit).empty();
	 	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 	$("#modalInfo").show();
	 	$(".modal-title").html("Training Status");
	 	$.ajax({
			url : 'TrainingStatus.action?trainingId='+trainingId+'&lPlanId='+lPlanId+'&fromPage=LD',
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}

	function getLearningPlanAssesmentScoreSummary(lPlanId,empID) {
	
		var dialogEdit = '.modal-body';
		 $(dialogEdit).empty();
		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		 $("#modalInfo").show();
		 $(".modal-title").html("Learning Assessment Score Summary");
		 if($(window).width() > 900){
			 $(".modal-dialog").width(900);
		 }
		 $.ajax({
				url : "LearningPlanAssessmentScoreSummary.action?lPlanId="+lPlanId+"&empId="+empID,
				cache : false,
				success : function(data) {
					$(dialogEdit).html(data);
				}
			});
	}

/* created by parvez date: 15-02-2023 */
	function viewCourseReadStatus(courseId, lPlanId, courseName, empId) {
	
		var dialogEdit = '.modal-body';
		 $(dialogEdit).empty();
		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		 $("#modalInfo").show();
		 /* var height = $(window).height()* 0.95;
		var width = $(window).width()* 0.95;
		$(".modal-dialog").css("height", height);
		$(".modal-dialog").css("width", width);
		$(".modal-dialog").css("max-height", height);
		$(".modal-dialog").css("max-width", width); */
		 $(".modal-title").html(''+courseName+' Status');
		 $.ajax({
				url : "CourseReadStatusUpdatePopup.action?courseId="+courseId+"&lPlanId="+lPlanId+"&empId="+empId,
				cache : false,
				success : function(data) {
					$(dialogEdit).html(data);
				}
			});
	}

</script>


<%
	List<String> empList = (List<String>) request.getAttribute("empList");
	UtilityFunctions uF = new UtilityFunctions();
	String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
	
	Map<String, String> learningPlanMp = (Map<String, String>) request.getAttribute("learningPlanMp");
	Map<String, String> hmEmpCodeDesig = (Map<String, String>) request.getAttribute("hmEmpCodeDesig"); 
	Map<String, String> hmEmpName = (Map<String, String>) request.getAttribute("hmEmpName");
	//List<String> memberList = (List<String>) request.getAttribute("memberList");
	Map<String, String> empImageMap = (Map<String, String>) request.getAttribute("empImageMap");
	Map<String, String> locationMp = (Map<String, String>) request.getAttribute("locationMp");

	String strSessionUserType = (String) session.getAttribute(IConstants.USERTYPE);
	Map<String,String> hmEmpCount=(Map<String,String>)request.getAttribute("hmEmpCount");
	if(hmEmpCount == null){ hmEmpCount = new HashMap<String,String>(); }
	int memberCount=(Integer)request.getAttribute("memberCount");
	
	Map<String,String> hmRemark=(Map<String,String>)request.getAttribute("hmRemark");
	
	String lPlanId=request.getParameter("lPlanId");
	Map<String, String> hmReadUnreadCount = (Map<String, String>)request.getAttribute("hmReadUnreadCount");
	Map<String, String> hmAssessmentTakePercent = (Map<String, String>) request.getAttribute("hmAssessmentTakePercent");
	//System.out.println("learningPlanMp==>"+learningPlanMp);
	boolean isClose = uF.parseToBoolean(learningPlanMp.get("IS_CLOSE"));
	
	//EncryptionUtils EU =new EncryptionUtils();//Created by Dattatray Date:21-07-21 Note: Encryption
%>


        	<div>
                <%if(learningPlanMp != null && learningPlanMp.size()>0 && !learningPlanMp.isEmpty()) { %>
                <div class="box-header with-border">
                    <h3 class="box-title"><%=learningPlanMp.get("LEARNING_PLAN_NAME")%></h3>
                </div>
                <!-- /.box-header -->
                <div class="box-body" style="padding: 5px; overflow-y: auto;min-height: 600px;">
                    <div class="leftbox reportWidth">
						<table class="table table-striped" cellpadding="0" cellspacing="0" width="100%">
							<tr>
								<th width="15%" align="right">Learning Objective</th>
								<td><%=learningPlanMp.get("OBJECTIVE")%></td>
							</tr>
							<tr>
								<th valign="top" align="right">Aligned with</th>
								<td><%=learningPlanMp.get("ALIGNED_WITH")%></td>
							</tr>
							<tr>
								<th valign="top" align="right">Certificate</th>
								<td><%=learningPlanMp.get("CERTIFICATE")%></td>
							</tr>
							<tr>
								<th align="right">Effective Date</th>
								<td><%=learningPlanMp.get("FROM")%></td>
							</tr>
							<tr>
								<th align="right">Due Date</th>
								<td><%=learningPlanMp.get("TO")%></td>
							</tr>
							<tr>
								<th align="right">Attributes</th>
								<td><%=learningPlanMp.get("ATTRIBUTE")%></td>
							</tr>
							<tr>
								<th align="right">Skills</th>
								<td><%=learningPlanMp.get("SKILLS")%></td>
							</tr>
						</table>
				
				<br/>
				<div class="row row_without_margin">
					<div class="col-lg-2" style="width: auto;"><i class="fa fa-circle" aria-hidden="true" style="color:#54aa0d"></i> <%-- <img border="0" src="<%=request.getContextPath()%>/images1/icons/approved.png"> --%>Finalized</div>
					<div class="col-lg-2" style="width: auto;"><i class="fa fa-circle" aria-hidden="true" style="color:#ea9900"></i><%-- <img border="0" src="<%=request.getContextPath()%>/images1/icons/pullout.png"> --%>Completed</div>
					<div class="col-lg-3" style="width: auto;"><i class="fa fa-circle" aria-hidden="true" style="color:#f7ee1d"></i><%-- <img border="0" src="<%=request.getContextPath()%>/images1/icons/re_submit.png"> --%>Waiting for completion</div>
					<div class="col-lg-2" style="width: auto;"><i class="fa fa-circle" aria-hidden="true" style="color:#b71cc5"></i><%-- <img border="0" src="<%=request.getContextPath()%>/images1/icons/pending.png"> --%>Not filled yet</div>
				</div>
				
				
				<!-- ===start parvez date: 14-02-2023=== -->
				<%
					List<List<String>> courseIdList = (List<List<String>>)request.getAttribute("courseIdList");
					Map<String, String> hmChapterCount = (Map<String, String>) request.getAttribute("hmChapterCount");
					Map<String, String> hmChapterReadCount = (Map<String, String>) request.getAttribute("hmChapterReadCount");
				%>
				
					<% if(courseIdList != null && !courseIdList.isEmpty()) { 
						for(int j=0; j<courseIdList.size(); j++) { 
							List<String> innerList = courseIdList.get(j);
					%>
					<div style="float: left; margin-top:20px; margin-bottom: 10px;">
						<span style="float: left; font-size: 16px; font-weight: bold; margin-right: 7px;"><%=innerList.get(1) %></span> 
						<span style="float: left; font-size: 16px;">(Course) </span>
					</div>
					
					<!-- Legends -->
					
					<table class="table table-bordered" cellpadding="0" cellspacing="0" width="100%">
						<tr>
							<th>Employee Name</th>
							<th width="10%">Status(bar)</th>
							<th width="10%">Read Status</th>
							<th width="10%">Result</th>
							<th>Finalize</th>
						</tr>
						
						<%
							for (int i = 0; empList != null && i < empList.size(); i++) {
								String remark=hmRemark.get(lPlanId+empList.get(i).trim()+innerList.get(0));
								String chapterCount = hmChapterCount.get(innerList.get(0));
                            	String chapterReadCount = hmChapterReadCount.get(innerList.get(0)+"_"+empList.get(i).trim());
                            	double readPercant = 0;
                            
                            	if(chapterReadCount != null  && chapterCount != null ) {
                            		readPercant = (uF.parseToDouble(chapterReadCount)* 100) / uF.parseToDouble(chapterCount);	
                            	}
                            	
                            	if(readPercant > 100) {
                            		readPercant = 100;
                            	}
						%>
						<tr>
							<td>
								<div style="float: left; width: 100%;">
									<div style="float: left; width: 21px; height: 21px; padding-right:10px;">
										<%if(uF.parseToInt(hmEmpCount.get(empList.get(i).trim()))==0){ %>
											 <i class="fa fa-circle" aria-hidden="true" style="color:#b71cc5"  title="Not filled yet"></i><%-- <img style="padding: 5px 5px 0 5px;" border="0" src="<%=request.getContextPath()%>/images1/icons/pending.png" title="Not filled yet"/> --%>
										<%}else if(memberCount==uF.parseToInt(hmEmpCount.get(empList.get(i).trim()))){ 
											if(remark==null){
										%>
												<i class="fa fa-circle" aria-hidden="true" style="color:#ea9900"></i>
										  <%}else{%>
												<i class="fa fa-circle" aria-hidden="true" style="color:#54aa0d"></i>
										<%	}
										  } else if(memberCount>uF.parseToInt(hmEmpCount.get(empList.get(i).trim()))){ %>
											<img style="padding: 5px 5px 0 5px;" border="0" src="<%=request.getContextPath()%>/images1/icons/re_submit.png" title="Waiting for completion"/>
										<%} %>
										&nbsp;&nbsp;
									</div>
									<div style="float: left; width: 21px; height: 21px;margin-left: 5px;">
											<%if(docRetriveLocation==null) { %>
													<img height="21" width="21" class="lazy img-circle" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + empImageMap.get(empList.get(i).trim())%>" />
											<%} else { %>
					                            	<img height="21" width="21" class="lazy img-circle" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+empList.get(i).trim()+"/"+IConstants.I_22x22+"/"+empImageMap.get(empList.get(i).trim())%>" />
					                         <%} %> 
									</div>
									<div style="margin-left: 60px;">
										<a href="javascript: void(0)" onclick="openEmployeeProfilePopup('<%=empList.get(i) %>');"><%=hmEmpName.get(empList.get(i).trim())%></a>
										<%=uF.showData(hmEmpCodeDesig.get(empList.get(i).trim()),"")%>
										working at
										<%=uF.showData(locationMp.get(empList.get(i).trim()),"")%>
									</div>
								</div>	
									
							</td>
							
							<td>
								<div style="width: 85%;">
									<div class="anaAttrib1"><span style="margin-left:<%=readPercant > 90 ? readPercant-10 : readPercant-5 %>%;"><%=readPercant%>%</span></div>
									<div id="outbox">
										<%if(readPercant < 33.33){ %>
										<div id="redbox" style="width: <%=readPercant %>%;"></div>
										<%}else if(readPercant >= 33.33 && readPercant < 66.67){ %>
										<div id="yellowbox" style="width: <%=readPercant %>%;"></div>
										<%}else if(readPercant >= 66.67){ %>
										<div id="greenbox" style="width: <%=readPercant %>%;"></div>
										<%} %>
									</div>
								</div>
							</td>
							<td align="right">
								<a onclick="viewCourseReadStatus('<%=innerList.get(0)%>','<%=lPlanId%>','<%=innerList.get(1) %>','<%=empList.get(i) %>');" href="javascript:void(0)">View</a>
							</td>
							<td align="right">&nbsp;</td>
							<td align="center">
								<%
								if(readPercant >= 100) {
									if (strSessionUserType != null && (strSessionUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strSessionUserType.equalsIgnoreCase(IConstants.ADMIN))) {
										if(remark ==null){
								%> 			<a href="javascript:void(0)" onclick="<%if(isClose){%>alert('This plan is already closed.');<%} else {%>learningPlanAssessmentFinalize('<s:property value="lPlanId" />','<%=empList.get(i).trim()%>','','<%=innerList.get(0).trim()%>',1)<%} %>" >Finalize</a> 
									<%}else{%>
											<a href="javascript:void(0)" onclick="learningPlanAssessmentFinalize('<s:property value="lPlanId" />','<%=empList.get(i).trim()%>','','<%=innerList.get(0).trim()%>',2)" ><%="Finalized by "+remark %></a>
									<%
										}
				 					}else{
				 						if(remark != null) {
				 							%>
				 							<a href="javascript:void(0)" onclick="learningPlanAssessmentFinalize('<s:property value="lPlanId" />','<%=empList.get(i).trim()%>','','<%=innerList.get(0).trim()%>',2)" ><%="Finalized by "+remark %></a>
				 							<%
				 						}
				 					}
								} else{
				 				%>
				 				-
				 				<%} %>
							</td>
							
						</tr>
						
						<%} %>
					</table>
				
					<% }
					}
					%>
				<!-- ===end parvez date: 14-02-2023=== -->
				
					<%
					Map<String, String> hmEmpIdwiseResult = (Map<String, String>) request.getAttribute("hmEmpIdwiseResult");
					List<List<String>> assessmentIdList = (List<List<String>>)request.getAttribute("assessmentIdList");
					Map<String, String> hmAssessRateEmpAndAssessIdWise = (Map<String, String>)request.getAttribute("hmAssessRateEmpAndAssessIdWise");
						//Map<String, Map<String, String>> outerMp = (Map<String, Map<String, String>>) request.getAttribute("outerMp");
					%>
				
					<% if(assessmentIdList != null && !assessmentIdList.isEmpty()) { 
						for(int j=0; j<assessmentIdList.size(); j++) { 
							List<String> innerList = assessmentIdList.get(j);
					%>
					
				<div style="float: left; margin-top:20px; margin-bottom: 10px;">
				<span style="float: left; font-size: 16px; font-weight: bold; margin-right: 7px;"><%=innerList.get(1) %></span> 
				<span style="float: left; font-size: 16px;">(Assessment) </span>
				</div>
				
				<!-- Legends -->
				
						<table class="table table-bordered" cellpadding="0" cellspacing="0" width="100%">
							<tr>
								<th>Employee Name</th>
								<th width="10%">Status(bar)</th>
								<th width="10%">Balanced Score</th>
								<th width="10%">Result</th>
								<th>Finalize</th>
							</tr>
							<%
								//Map<String, Map<String, String>> outerMp = (Map<String, Map<String, String>>) request.getAttribute("outerMp");
									for (int i = 0; empList != null && i < empList.size(); i++) {
										/* Map<String, String> value = outerMp.get(empList.get(i).trim());
										if (value == null)
											value = new HashMap<String, String>(); */
										//double total = 0.0;
										String remark=hmRemark.get(lPlanId+empList.get(i).trim()+innerList.get(0));
										String takeAttemptPercent = hmAssessmentTakePercent.get(empList.get(i).trim());
										if(uF.parseToDouble(takeAttemptPercent) > 100) {
											takeAttemptPercent = "100";
										}
										
							%>
							<tr>
								<td>
								<div style="float: left; width: 100%;">
								<div style="float: left; width: 21px; height: 21px; padding-right:10px;">
									<%if(uF.parseToInt(hmEmpCount.get(empList.get(i).trim()))==0){ %>
										 <i class="fa fa-circle" aria-hidden="true" style="color:#b71cc5"  title="Not filled yet"></i><%-- <img style="padding: 5px 5px 0 5px;" border="0" src="<%=request.getContextPath()%>/images1/icons/pending.png" title="Not filled yet"/> --%>
									<%}else if(memberCount==uF.parseToInt(hmEmpCount.get(empList.get(i).trim()))){ 
										if(remark==null){
									%>
											<%-- <img border="0" style="padding: 5px 5px 0pt;" src="<%=request.getContextPath()%>/images1/icons/pullout.png"> --%>
											<i class="fa fa-circle" aria-hidden="true" style="color:#ea9900"></i>
									  <%}else{%>
											<%-- <img border="0" style="padding: 5px 5px 0pt;" src="<%=request.getContextPath()%>/images1/icons/approved.png"> --%>
											<i class="fa fa-circle" aria-hidden="true" style="color:#54aa0d"></i>
									<%	}
									  } else if(memberCount>uF.parseToInt(hmEmpCount.get(empList.get(i).trim()))){ %>
										<img style="padding: 5px 5px 0 5px;" border="0" src="<%=request.getContextPath()%>/images1/icons/re_submit.png" title="Waiting for completion"/>
									<%} %>
									&nbsp;&nbsp;
								</div>
								<div style="float: left; width: 21px; height: 21px;margin-left: 5px;">
										<%-- <img height="21" width="21" class="lazy" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+empList.get(i).trim()+"/"+IConstants.I_22x22+"/"+empImageMap.get(empList.get(i).trim())%>" /> --%>
										<%if(docRetriveLocation==null) { %>
												<img height="21" width="21" class="lazy img-circle" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + empImageMap.get(empList.get(i).trim())%>" />
										<%} else { %>
				                            	<img height="21" width="21" class="lazy img-circle" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+empList.get(i).trim()+"/"+IConstants.I_22x22+"/"+empImageMap.get(empList.get(i).trim())%>" />
				                         <%} %> 
									</div>
									<!-- <div style="float: left; margin-left: 5px; width:80%;"> -->
									<div style="margin-left: 60px;">
									
										<!-- Created by Dattatray Date:21-07-21 Note: empId Encrypt -->
										<a href="javascript: void(0)" onclick="openEmployeeProfilePopup('<%=empList.get(i) %>');"><%=hmEmpName.get(empList.get(i).trim())%></a>
										<%=uF.showData(hmEmpCodeDesig.get(empList.get(i).trim()),"")%>
										working at
										<%=uF.showData(locationMp.get(empList.get(i).trim()),"")%>
									</div>
								</div>	
									
							</td>
								
								<td>
								<div style="width: 85%;">
									<div class="anaAttrib1"><span style="margin-left:<%=uF.parseToInt(takeAttemptPercent) > 90 ? uF.parseToInt(takeAttemptPercent)-10 : uF.parseToInt(takeAttemptPercent)-5 %>%;"><%=takeAttemptPercent%>%</span></div>
									<div id="outbox">
										<%if(uF.parseToDouble(takeAttemptPercent) < 33.33){ %>
										<div id="redbox" style="width: <%=uF.parseToDouble(takeAttemptPercent) %>%;"></div>
										<%}else if(uF.parseToDouble(takeAttemptPercent) >= 33.33 && uF.parseToDouble(takeAttemptPercent) < 66.67){ %>
										<div id="yellowbox" style="width: <%=uF.parseToDouble(takeAttemptPercent) %>%;"></div>
										<%}else if(uF.parseToDouble(takeAttemptPercent) >= 66.67){ %>
										<div id="greenbox" style="width: <%=uF.parseToDouble(takeAttemptPercent) %>%;"></div>
										<%} %>
									</div>
								</div>
								</td>
								<td align="right">
									<%
									String aggregate="0.0";
									if(uF.parseToDouble(hmAssessRateEmpAndAssessIdWise.get(empList.get(i).trim()+"_"+innerList.get(0))) >= 0) { 
										aggregate=hmAssessRateEmpAndAssessIdWise.get(empList.get(i).trim()+"_"+innerList.get(0)) != null ? uF.parseToDouble(hmAssessRateEmpAndAssessIdWise.get(empList.get(i).trim()+"_"+innerList.get(0))) / 20 + "" : "0";
									%>
										<%--<a href="LearningPlanAssessmentScoreSummary.action?lPlanId=<s:property value="lPlanId" />&empId=<%=empList.get(i).trim()%>"><%=uF.showData(hmAssessRateEmpAndAssessIdWise.get(empList.get(i).trim()+"_"+innerList.get(0)), "NA")%>%</a>--%>
											 <a href="javascript:void(0);" onclick="getLearningPlanAssesmentScoreSummary('<%=lPlanId%>','<%=empList.get(i).trim()%>')"><%=uF.showData(hmAssessRateEmpAndAssessIdWise.get(empList.get(i).trim()+"_"+innerList.get(0)), "NA")%>%</a> 
									<%}else{ //&assessmentId=16%>
										NA
									<%} %>
									
									<div id="starPrimary<%=empList.get(i).trim()%>_<%=j %>"></div>
											<script type="text/javascript">
										        $(function() {
										        	$('#starPrimary<%=empList.get(i).trim()%>_<%=j %>').raty({
										        		readOnly: true,
										        		start: <%=aggregate%>,
										        		half: true,
										        		targetType: 'number'
										        	});
										        });
										   </script>
									
								</td>
								<td align="right"><%=uF.showData(hmEmpIdwiseResult.get(empList.get(i).trim()+"_"+innerList.get(0)), "-") %></td>
								<td align="center">
								<%
								//if(uF.parseToDouble(hmAssessRateEmpAndAssessIdWise.get(empList.get(i).trim()+"_"+innerList.get(0)))> 0) {}
								
								if(hmAssessRateEmpAndAssessIdWise.containsKey(empList.get(i).trim()+"_"+innerList.get(0))) {
									if (strSessionUserType != null && (strSessionUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strSessionUserType.equalsIgnoreCase(IConstants.ADMIN))) {
										if(remark ==null){
								%>		
									<a href="javascript:void(0)" onclick="<%if(isClose){%>alert('This plan is already closed.');<%} else {%>learningPlanAssessmentFinalize('<s:property value="lPlanId" />','<%=empList.get(i).trim()%>','','<%=innerList.get(0).trim()%>',1)<%} %>" >Finalize</a> 
									<%}else{ %>
											<a href="javascript:void(0)" onclick="learningPlanAssessmentFinalize('<s:property value="lPlanId" />','<%=empList.get(i).trim()%>','','<%=innerList.get(0).trim()%>',2)" ><%="Finalized by "+remark %></a>
									<%
										}
				 					}else{
				 						if(remark != null) {
				 							%>
				 							<a href="javascript:void(0)" onclick="learningPlanAssessmentFinalize('<s:property value="lPlanId" />','<%=empList.get(i).trim()%>','','<%=innerList.get(0).trim()%>',2)" ><%="Finalized by "+remark %></a>
				 							<%
				 						}
				 					}
								}else{
				 				%>
				 				-
				 				<%} %>
							</td>
							</tr>
							<%
								}
							%>
						</table>
						<%
					}
							}
						%>
				
				
					<%
					
					List<List<String>> trainingIdList = (List<List<String>>) request.getAttribute("trainingIdList");
					Map<String, String> hmTrainerRateEmpAndTrainingIdWise = (Map<String, String>) request.getAttribute("hmTrainerRateEmpAndTrainingIdWise");
					Map<String, String> hmLearnerRateTrainerAndTrainingIdWise = (Map<String, String>) request.getAttribute("hmLearnerRateTrainerAndTrainingIdWise");
					Map<String, String> hmTrainingStatus = (Map<String, String>) request.getAttribute("hmTrainingStatus");
					Map<String, String> hmTrainingCompletedStatus = (Map<String, String>) request.getAttribute("hmTrainingCompletedStatus");
					Map<String, String> hmTrainingTotDays = (Map<String, String>) request.getAttribute("hmTrainingTotDays");
					Map<String, String> hmTrainingCompletedDays = (Map<String, String>) request.getAttribute("hmTrainingCompletedDays");
					
					if(trainingIdList != null && !trainingIdList.isEmpty()) { 
					
					%>
					
				<%for(int j=0; j<trainingIdList.size(); j++) {
					List<String> innerList = trainingIdList.get(j);
					
					double trainingCompletedDaysPercent = 0.0d;
					double trainingCompletedDays = uF.parseToDouble(hmTrainingCompletedDays.get(innerList.get(0)));
					double trainingTotDays = uF.parseToDouble(hmTrainingTotDays.get(innerList.get(0)));
					if(trainingTotDays > 0) {
						trainingCompletedDaysPercent = (trainingCompletedDays / trainingTotDays) * 100;
					}
				/* System.out.println("innerList.get(0) ---> " + innerList.get(0));
				System.out.println("trainingCompletedDays ---> " + trainingCompletedDays);
				System.out.println("trainingTotDays ---> " + trainingTotDays);
				System.out.println("trainingCompletedDaysPercent ---> " + trainingCompletedDaysPercent);
				System.out.println("uF.parseToInt(trainingCompletedDaysPercent) ---> " + uF.parseToInt(trainingCompletedDaysPercent+""));
				 */
				String status = hmTrainingStatus.get(innerList.get(0));
				if(uF.parseToDouble(status) > 100){
					status = "100";
				}
				%>	
				<div class="row" style="margin-left: 0px;margin-right: 0px;">
					<div class="col-lg-6">
						<div style="float: left; margin-top:40px;">
						<span style="float: left; font-size: 14px; font-weight: bold; margin-right: 7px;"><%=innerList.get(1) %></span> 
						<span style="float: left; font-size: 14px;">(Training) </span>
						</div>
					</div>
					<div class="col-lg-6">
						<div style="float: right; margin-left:20px; margin-bottom:10px; margin-top: 10px;">
						<div style="float: left; width: 100%;">
							<div style="float: left; width: 40px; margin-top: 14px;"> Sys: </div>		
									<div style="float: left; width: 100px; margin-left: 10px;">
										<div class="anaAttrib1"><span style="margin-left:<%=(int)(trainingCompletedDaysPercent) > 94 ? (int)(trainingCompletedDaysPercent)-6 : (int)(trainingCompletedDaysPercent)-2.5 %>%;"><%=uF.formatIntoOneDecimal(trainingCompletedDaysPercent) %>%</span></div>
										<div id="outbox">
											<%if(trainingCompletedDaysPercent < 33.33) { %>
											<div id="redbox" style="width: <%=trainingCompletedDaysPercent %>%;"></div>
											<%} else if(trainingCompletedDaysPercent >= 33.33 && trainingCompletedDaysPercent < 66.67) { %>
											<div id="yellowbox" style="width: <%=trainingCompletedDaysPercent %>%;"></div>
											<%} else if(trainingCompletedDaysPercent >= 66.67) { %>
											<div id="greenbox" style="width: <%=trainingCompletedDaysPercent %>%;"></div>
											<% } %>
										</div>	
									</div>
						</div>				
						<div style="float: left; width: 100%;margin-top: 7px;"> 
							<div style="float: left; width: 40px; margin-top: 14px;"> Trainer: </div>		
									<div style="float: left; width: 100px; margin-left: 10px;">
										<div class="anaAttrib1"><span style="margin-left:<%=uF.parseToInt(status) > 94 ? uF.parseToInt(status)-6 : uF.parseToInt(status)-2.5 %>%;"><%=uF.formatIntoOneDecimal(uF.parseToDouble(status)) %>%</span></div>
										<div id="outbox">
											<%if(uF.parseToDouble(status) < 33.33){ %>
											<div id="redbox" style="width: <%=uF.parseToDouble(status) %>%;"></div>
											<%}else if(uF.parseToDouble(status) >= 33.33 && uF.parseToDouble(status) < 66.67){ %>
											<div id="yellowbox" style="width: <%=uF.parseToDouble(status) %>%;"></div>
											<%}else if(uF.parseToDouble(status) >= 66.67){ %>
											<div id="greenbox" style="width: <%=uF.parseToDouble(status) %>%;"></div>
											<%} %>
										</div>
									</div>
							<div id="trainingStatusDiv<%=innerList.get(0) %>" style="float: left; margin-left: 15px; margin-top: 14px;">
							<a onclick="addTrainingStatus('<%=innerList.get(0).trim()%>','<%=lPlanId%>');" href="javascript:void(0)">
								<% if(hmTrainingCompletedStatus != null && !hmTrainingCompletedStatus.isEmpty()) {
									if(hmTrainingCompletedStatus.get(innerList.get(0)) != null && hmTrainingCompletedStatus.get(innerList.get(0)).equals("1")) {
								%>
									View
								<% } else { %>
									Update
									<% } } else { %>
									Update
								<% } %>
							</a></div>		
						</div>
						</div>
					</div>
				</div>
				
				<!-- Legends -->
				
						<table class="table table-bordered" cellpadding="0" cellspacing="0" width="100%">
							<tr>
								<th>Employee Name</th>
								<!-- <th width="10%">Status(bar)</th> -->
								<th width="10%">Feedback <br/>(Learners)</th>
								<th width="10%">Trainer Rating</th>
								<th width="10%">Learner Rating</th>
								<th width="10%">Result</th>
								<th>Finalize</th>
							</tr>
							<%
								//Map<String, Map<String, String>> outerMp = (Map<String, Map<String, String>>) request.getAttribute("outerMp");
									for (int i = 0; empList != null && i < empList.size(); i++) {
										String remark=hmRemark.get(lPlanId+empList.get(i).trim()+innerList.get(0));
										
							%>
							<tr>
								<td>
								<div style="float: left; width: 100%;">
								<div style="float: left; width: 21px; height: 21px; padding-right:10px;">
								<%if(uF.parseToInt(hmEmpCount.get(empList.get(i).trim()))==0){ %>
									<%-- <img style="padding: 5px 5px 0 5px;" border="0" src="<%=request.getContextPath()%>/images1/icons/pending.png" title="Not filled yet"/> --%>
									<i class="fa fa-circle" aria-hidden="true" style="color:#b71cc5" title="Not filled yet"></i>
									<%}else if(memberCount==uF.parseToInt(hmEmpCount.get(empList.get(i).trim()))){ 
										if(remark==null) {
									%>
									<%-- <img border="0" style="padding: 5px 5px 0pt;" src="<%=request.getContextPath()%>/images1/icons/pullout.png"> --%>
									<i class="fa fa-circle" aria-hidden="true" style="color:#ea9900"></i>
									<%}else{
										%>
										<%-- <img border="0" style="padding: 5px 5px 0pt;" src="<%=request.getContextPath()%>/images1/icons/approved.png"> --%>
										<i class="fa fa-circle" aria-hidden="true" style="color:#54aa0d"></i>
										<%
									}
									} else if(memberCount>uF.parseToInt(hmEmpCount.get(empList.get(i).trim()))){ %>
									<img style="padding: 5px 5px 0 5px;" border="0" src="<%=request.getContextPath()%>/images1/icons/re_submit.png" title="Waiting for completion"/>
									<%} %>
									&nbsp;&nbsp;
								</div>
								<div style="float: left; width: 21px; height: 21px;margin-left: 5px;">
										<%-- <img src="userImages/<%=uF.showData(empImageMap.get(empList.get(i).trim()), "avatar_photo.png")%>" border="0" height="21px" /> --%>
										<%if(docRetriveLocation==null) { %>
												<img height="21" width="21" class="lazy img-circle" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + empImageMap.get(empList.get(i).trim())%>" />
										<%} else { %>
				                            	<img height="21" width="21" class="lazy img-circle" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+empList.get(i).trim()+"/"+IConstants.I_22x22+"/"+empImageMap.get(empList.get(i).trim())%>" />
				                         <%} %> 
									</div>
									<!-- <div style="float: left; margin-left: 5px; width:80%;"> -->
									<div style="margin-left: 60px;">
									
										<a href="javascript: void(0)" onclick="openEmployeeProfilePopup('<%=empList.get(i) %>');"><%=hmEmpName.get(empList.get(i).trim())%></a>
										<%=uF.showData(hmEmpCodeDesig.get(empList.get(i).trim()),"")%>
										working at
										<%=uF.showData(locationMp.get(empList.get(i).trim()),"")%>
									</div>
								</div>	
									
							</td>
								<td>
								<% if(uF.parseToDouble(hmTrainerRateEmpAndTrainingIdWise.get(empList.get(i).trim()+"_"+innerList.get(0).trim()))> 0) { %>
									<a onclick="trainingFeedbackPreview('<%=innerList.get(0).trim()%>','<%=lPlanId%>','<%=empList.get(i) %>','T');" href="javascript:void(0)" title="Feedback Summary">Feedback</a>
								<% } else { %>
								<a onclick="addTrainingFeedback('<%=innerList.get(0).trim()%>','<%=lPlanId%>','<%=empList.get(i) %>');" href="javascript:void(0)">Feedback</a> 
								<% } %>
								</td>
																
								<td align="right">
									<%
									String aggregate1="0.0";
									if(uF.parseToDouble(hmTrainerRateEmpAndTrainingIdWise.get(empList.get(i).trim()+"_"+innerList.get(0).trim()))> 0) { 
										aggregate1=hmTrainerRateEmpAndTrainingIdWise.get(empList.get(i).trim()+"_"+innerList.get(0).trim()) != null ? uF.parseToDouble(hmTrainerRateEmpAndTrainingIdWise.get(empList.get(i).trim()+"_"+innerList.get(0).trim())) / 20 + "" : "0";
									%>
										<a onclick="trainingFeedbackPreview('<%=innerList.get(0).trim()%>','<%=lPlanId%>','<%=empList.get(i) %>','T');" href="javascript:void(0)"><%=uF.showData(hmTrainerRateEmpAndTrainingIdWise.get(empList.get(i).trim()+"_"+innerList.get(0).trim()), "NA")%>%</a>
										<%}else{ //&assessmentId=16%>
										NA
										<%} %>
									
									<div id="starPrimaryT<%=empList.get(i).trim()+"_"+j%>"></div> 
											<script type="text/javascript">
										        $(function() {
										        	$('#starPrimaryT<%=empList.get(i).trim()%>_<%=j %>').raty({
										        		readOnly: true,
										        		start: <%=aggregate1%>,
										        		half: true,
										        		targetType: 'number'
										        	});
										        	});
										        </script>
									
								</td>
								
								<td align="right">
									<%
									String aggregate="0.0";
									if(uF.parseToDouble(hmLearnerRateTrainerAndTrainingIdWise.get(empList.get(i).trim()+"_"+innerList.get(0).trim()))> 0) { 
										aggregate=hmLearnerRateTrainerAndTrainingIdWise.get(empList.get(i).trim()+"_"+innerList.get(0).trim()) != null ? uF.parseToDouble(hmLearnerRateTrainerAndTrainingIdWise.get(empList.get(i).trim()+"_"+innerList.get(0).trim())) / 20 + "" : "0";
									%>
										<a onclick="trainingFeedbackPreview('<%=innerList.get(0).trim()%>','<%=lPlanId%>','<%=empList.get(i) %>','L');" href="javascript:void(0)"><%=uF.showData(hmLearnerRateTrainerAndTrainingIdWise.get(empList.get(i).trim()+"_"+innerList.get(0).trim()), "NA")%>%</a>
										<%}else{ //&assessmentId=16%>
										NA
										<%} %>
									
									<div id="starPrimaryL<%=empList.get(i).trim()+"_"+j%>"></div> 
											<script type="text/javascript">
										        $(function() {
										        	$('#starPrimaryL<%=empList.get(i).trim()%>_<%=j %>').raty({
										        		readOnly: true,
										        		start: <%=aggregate%>,
										        		half: true,
										        		targetType: 'number'
										        	});
										        	});
										        </script>
									
								</td>
								<td align="right">&nbsp;</td>
								<td align="center">
								<%
								//if(uF.parseToDouble(hmLearnerRateTrainerAndTrainingIdWise.get(empList.get(i).trim()+"_"+innerList.get(0).trim()))>0 || uF.parseToDouble(hmTrainerRateEmpAndTrainingIdWise.get(empList.get(i).trim()+"_"+innerList.get(0).trim()))>0) {
								if(hmLearnerRateTrainerAndTrainingIdWise.containsKey(empList.get(i).trim()+"_"+innerList.get(0).trim()) || hmTrainerRateEmpAndTrainingIdWise.containsKey(empList.get(i).trim()+"_"+innerList.get(0).trim())) {
									if (strSessionUserType != null && (strSessionUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strSessionUserType.equalsIgnoreCase(IConstants.ADMIN))) {
										if(remark ==null) {
								%> <a href="javascript:void(0)" onclick="<%if(isClose){%>alert('This plan is already closed.');<%} else {%>learningPlanAssessmentFinalize('<s:property value="lPlanId" />','<%=empList.get(i).trim()%>','<%=innerList.get(0).trim()%>','',1);<%} %>" >Finalize</a> 
								<%}else{
									%>
									<a href="javascript:void(0)" onclick="learningPlanAssessmentFinalize('<s:property value="lPlanId" />','<%=empList.get(i).trim()%>','<%=innerList.get(0).trim()%>','',2)" ><%="Finalized by "+remark %></a>
									<%
								}
				 					}else{
				 						if(remark != null) {
				 							%>
				 							<a href="javascript:void(0)" onclick="learningPlanAssessmentFinalize('<s:property value="lPlanId" />','<%=empList.get(i).trim()%>','<%=innerList.get(0).trim()%>','',2)" ><%="Finalized by "+remark %></a>
				 							<%
				 						}
				 					}
								}else{
				 				%>
				 				-
				 				<%} %>
							</td>
								<%-- <td align="center">
								<%if(uF.parseToDouble(value.get("AGGREGATE"))> 0) { 
									if (strSessionUserType != null && strSessionUserType.equalsIgnoreCase(IConstants.HRMANAGER)) {
										if(remark ==null){
								%> <a href="javascript:void(0)" onclick="learningPlanAssessmentFinalize('<s:property value="lPlanId" />','<%=empList.get(i).trim()%>')" >Finalize</a> 
								<%}else{
									%>
									<a href="javascript:void(0)" onclick="learningPlanAssessmentFinalize('<s:property value="lPlanId" />','<%=empList.get(i).trim()%>')" ><%="Finalized by "+remark %></a>
									<%
								}
 					}else{
 						if(remark != null){
 							%>
 							<a href="javascript:void(0)" onclick="learningPlanAssessmentFinalize('<s:property value="lPlanId" />','<%=empList.get(i).trim()%>')" ><%="Finalized by "+remark %></a>
 							<%
 						}
 					}
				}else{
 				%>
 				-
 				<%} %>
			</td> --%>
			</tr>
			<%
				}
			%>
		</table>
		<%
			}
			}
		%>
	
       </div>
                </div>
                <!-- /.box-body -->
                <%}else { %>
                	<div class="nodata msg">No Learning Plan Status.</div>
                <%} %>
            </div>

<div class="modal" id="modalInfo" role="dialog">
    <div class="modal-dialog ">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">View Information</h4>
            </div>
            <div class="modal-body" style="height:400px;overflow-y:auto;padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>
