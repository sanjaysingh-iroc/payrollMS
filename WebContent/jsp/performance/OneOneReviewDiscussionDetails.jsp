<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>

<%
	CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
	UtilityFunctions uF=new UtilityFunctions();
	
	String orientation = (String)request.getAttribute("oreinted");
	String fromPage = (String)request.getParameter("fromPage");
	Map<String,List<String>> hmAppraisalDetails = (Map<String, List<String>>) request.getAttribute("hmAppraisalDetails");
	if(hmAppraisalDetails == null) hmAppraisalDetails = new HashMap<String, List<String>>();
	
	Map<String,List<String>> hmDiscussionDetails = (Map<String, List<String>>) request.getAttribute("hmDiscussionDetails");
	if(hmDiscussionDetails == null) hmDiscussionDetails = new HashMap<String, List<String>>();
	
	String dataType = (String) request.getAttribute("dataType");
	//System.out.println(dataType);
    String currUserType = (String) request.getAttribute("currUserType");
    String strEmpId = (String)request.getParameter("strEmpId");
    String appId = (String)request.getParameter("appId");
    //System.out.println(appId);
    String appFreqId = (String)request.getParameter("appFreqId");
    
    Map<String, String> hmAppraisalFinalScore = (Map<String, String>)request.getAttribute("hmAppraisalFinalScore");
    if(hmAppraisalFinalScore == null) hmAppraisalFinalScore = new HashMap<String, String>();
    
    Map<String, String> hmFeatureStatus = (Map<String, String>) request.getAttribute("hmFeatureStatus");
	if(hmFeatureStatus == null) hmFeatureStatus = new HashMap<String, String>();
%>

<style type="text/css">
.zoom:hover {
  -ms-transform: scale(1.5); /* IE 9 */
  -webkit-transform: scale(1.5); /* Safari 3-8 */
  transform: scale(1.5); 
}

	#textlabel{
		white-space:pre-line;
	}
</style>

<script type="text/javascript" charset="utf-8">
	function insertOneOneRatingAndComment(empId, appid, appFreqId, operation) {
	    
	    var userRating = 0;
	    var strComment = '';
	    var chTaskRating = 0;
	    var chStrComment = '';
	    var strDiscussionId = '';
	    
	    var startDate;
		var strTime;
		var edTime;
		var totalTime;
		
		if(document.getElementById('dateOfDiscussion'+appid+'_'+appFreqId)){
			startDate = document.getElementById('dateOfDiscussion'+appid+'_'+appFreqId).value;
		}
		
		if(document.getElementById('discussionStartTime'+appid+'_'+appFreqId)){
			strTime = document.getElementById('discussionStartTime'+appid+'_'+appFreqId).value;
		}
		
		if(document.getElementById('discussionEndTime'+appid+'_'+appFreqId)){
			edTime = document.getElementById('discussionEndTime'+appid+'_'+appFreqId).value;
		}
		
		if(document.getElementById('totalTimeSpent'+appid+'_'+appFreqId)){
			totalTime = document.getElementById('totalTimeSpent'+appid+'_'+appFreqId).value;
		}
	    
	    /* userRating = document.getElementById("hideUserRating"+appid+"_"+appFreqId).value; */
	    strComment = document.getElementById("strComment"+appid+"_"+appFreqId).value;
	    if(document.getElementById("discussionId_"+appid+"_"+appFreqId)){
	    	strDiscussionId = document.getElementById("discussionId_"+appid+"_"+appFreqId).value;
	    }
	    
	    if(strComment == ""){
			alert("Please Enter Reason");
		} else{ 
	         xmlhttp = GetXmlHttpObject();
	         if (xmlhttp == null) {
	            alert("Browser does not support HTTP Request");
	            return;
	         } else {
	            var xhr = $.ajax({
		            url : 'OneOneReviewDiscussionDetails.action?strEmpId='+empId+'&appId='+appid+'&appFreqId='+appFreqId+'&strComment='+encodeURIComponent(strComment)+
		            		'&userRating='+userRating+'&operation='+operation+'&discussionId='+strDiscussionId+'&strStartTime='+strTime+'&strEndTime='+edTime+'&strStartDate='+startDate+'&strTotalTimeSpent='+totalTime,
		            cache : false,
		            success : function(data) {
		                if(data == "") {
		                } else if(data.length > 1) {
		                     var allData = data.split("::::");
		                     getOneOneDiscussionDetails('OneOneReviewDiscussionDetails','<%=strEmpId %>','<%=dataType %>','<%=currUserType %>','OOD');
		                }
		          	}
	            });
	        }
	    }
    }
	
	function updateCommentRating(appId,freqId,discriptionId,empId){
		
		$.ajax({ 
			type : 'POST',
			url: 'OneOneReviewDiscussionDetails.action?strEmpId='+empId+'&dataType=update'+'&appId='+appId+'&appFreqId='+freqId,
			//data: form_data,
			cache: true,
			success: function(result){
				//alert("result2==>"+result);
				$("#reviewDiscussionDetails").html(result);
	   		}
		});
	}
	
	function discussionSignOff(apStatus,empId, appid, appFreqId, dicussionId, operation){
		
		var strComment = '';
		strComment = document.getElementById("strComment"+appid+"_"+appFreqId).value;
		
		var startDate;
		var strTime;
		var edTime;
		var totalTime;
		
		if(document.getElementById('dateOfDiscussion'+appid+'_'+appFreqId)){
			startDate = document.getElementById('dateOfDiscussion'+appid+'_'+appFreqId).value;
		}
		
		if(document.getElementById('discussionStartTime'+appid+'_'+appFreqId)){
			strTime = document.getElementById('discussionStartTime'+appid+'_'+appFreqId).value;
		}
		
		if(document.getElementById('discussionEndTime'+appid+'_'+appFreqId)){
			edTime = document.getElementById('discussionEndTime'+appid+'_'+appFreqId).value;
		}
		
		if(document.getElementById('totalTimeSpent'+appid+'_'+appFreqId)){
			totalTime = document.getElementById('totalTimeSpent'+appid+'_'+appFreqId).value;
		}
		
		if(strComment == ""){
			alert("Please Enter Reason");
		} else{ 
	         xmlhttp = GetXmlHttpObject();
	         if (xmlhttp == null) {
	            alert("Browser does not support HTTP Request");
	            return;
	         } else {
	            var xhr = $.ajax({
		            url : 'OneOneReviewDiscussionDetails.action?appId='+appid+'&appFreqId='+appFreqId+'&operation='+operation+'&strComment='+encodeURIComponent(strComment)
		            		+'&discussionId='+dicussionId+'&strEmpId='+empId+'&apStatus='+apStatus+'&strStartTime='+strTime+'&strEndTime='+edTime+'&strStartDate='+startDate+'&strTotalTimeSpent='+totalTime,
		            cache : false,
		            success : function(data) {
		            	//getMyHRData('KRATarget','L','','');
		            	if(data == "") {
		                } else if(data.length > 1) {
		                     var allData = data.split("::::");
		                     getOneOneDiscussionDetails('OneOneReviewDiscussionDetails','<%=strEmpId %>','<%=dataType %>','<%=currUserType %>','OOD');
		                }
		          	}
	            });
	        }
	    }
    }
	
	
/* ===start parvez date: 22-03-2023=== */	
	function convertTimeFormat(appId, freqId) { 
		var startDate = document.getElementById('dateOfDiscussion'+appId+'_'+freqId).value;
		var strTime = document.getElementById('discussionStartTime'+appId+'_'+freqId).value;
		var edTime = document.getElementById('discussionEndTime'+appId+'_'+freqId).value;
		 
		if(startDate != null && startDate != '' && strTime != null && strTime != '' && edTime != null && edTime != '') {
			var tempDate = startDate.split('/');
			startDate = tempDate[2]+"/"+tempDate[1]+"/"+tempDate[0];
			var strtTime = new Date(startDate+" "+strTime+":00");
			var endTime = new Date(startDate+" "+edTime+":00");
	
			 /* alert("startDate ===>> " + startDate); */
			var minute = 60 * 1000,
	        hour = minute * 60,
	        day = hour * 24,
	        month = day * 30,
	        ms = Math.abs(endTime - strtTime);
			
			
		    var months = parseInt(ms / month, 10);
		        ms -= months * month;
		
		    var days = parseInt(ms / day, 10);
		        ms -= days * day;
		
		    var hours = parseInt(ms / hour, 10);
				ms -= hours * hour;
		    var minutes = parseInt(ms / minute, 10);
		    if(parseInt(minutes) < 10) {
		    	minutes = "0"+minutes;
		    }
		    
		    var totTime = hours+"."+minutes;
			if(parseFloat(totTime) < 0) {
				totTime = 0;
			}
			document.getElementById("totalTimeSpent"+appId+'_'+freqId).value = totTime;
			
		}
	}
/* ===end parvez date: 22-03-2023=== */	
	
</script>


<!-- <div class="col-md-12 col_no_padding"> -->
	<div class="box box-none">
		
<p class="message"><%=uF.showData((String)session.getAttribute(IConstants.MESSAGE), "") %></p>
<% session.removeAttribute(IConstants.MESSAGE); %>
	
		<% if(hmAppraisalDetails != null && !hmAppraisalDetails.isEmpty()){ %>
		<ul>
		<% 
				Iterator<String> itr = hmAppraisalDetails.keySet().iterator();
				while(itr.hasNext()){
					String key = itr.next();
					List<String> appraisalList = hmAppraisalDetails.get(key);
					if(appraisalList != null && !appraisalList.isEmpty()  && appraisalList.size()>0) {
						List<String> discussionList = hmDiscussionDetails.get(appraisalList.get(0)+"_"+appraisalList.get(12));
						//System.out.println(discussionList);
						
		%>
					<li>
						<div class="box-body " style="padding: 5px; overflow-y: auto; display: block;">
							<div class="box-header with-border">
								<h4 class="box-title" style="width: 100%;">
									<div style="display: inline;"><%=uF.showData(appraisalList.get(1), "")%></div>
								</h4>
							</div>
						<!-- </div> -->
						
							<div class="box-body" style="padding: 5px; overflow-y: auto;">
							<!-- ===start parvez date: 22-03-2023=== -->
								<% if(discussionList != null && !discussionList.isEmpty() && discussionList.size()>0){ %>
									<div class="row row_without_margin">
										<div class="col-lg-12 col-md-12 col-sm-12" style="margin-top:1%;">
										<table class="table_no_border table">
											<tr>
												<td>Date of Discussion </td>
												<td>Discussion Time </td>
												<td>Total Time Spent </td>
											</tr>
											<tr>
											<td>
												<input type="text" id="dateOfDiscussion<%=appraisalList.get(0)%>_<%=appraisalList.get(12)%>" name="dateOfDiscussion" style="width:100px !important;" value="<%=uF.showData(discussionList.get(8),"")%>" />
											</td>
											<td>
												<div>Start Time: <input type="text" id="discussionStartTime<%=appraisalList.get(0)%>_<%=appraisalList.get(12)%>" name="discussionStartTime" style="width:95px !important;" value="<%=uF.showData(discussionList.get(6),"")%>" /></div>
												<br/>
												<div>End Time: <input type="text" id="discussionEndTime<%=appraisalList.get(0)%>_<%=appraisalList.get(12)%>" name="discussionEndTime" style="width:95px !important;" value="<%=uF.showData(discussionList.get(7),"")%>" />
													<a href="javascript:void(0);" onclick="convertTimeFormat('<%=appraisalList.get(0)%>','<%=appraisalList.get(12)%>');">Get Total Hrs.</a>
												</div><!-- onclick="convertTimeFormat()" -->
												
											 </td>
											 <td><input type="text" id="totalTimeSpent<%=appraisalList.get(0)%>_<%=appraisalList.get(12)%>" name="totalTimeSpent" style="width:95px !important;" value="<%=uF.showData(discussionList.get(9),"")%>" /></td>
											</tr>
										</table>
										</div>
									</div>
								<%} else{ %>
									<div class="row row_without_margin">
										<div class="col-lg-12 col-md-12 col-sm-12" style="margin-top:1%;">
										<table class="table_no_border table">
											<tr>
												<td>Date of Discussion </td>
												<td>Discussion Time </td>
												<td>Total Time Spent </td>
											</tr>
											<tr>
											<td><input type="text" id="dateOfDiscussion<%=appraisalList.get(0)%>_<%=appraisalList.get(12)%>" name="dateOfDiscussion" style="width:100px !important;" /></td>
											<td>
												<div>Start Time: <input type="text" id="discussionStartTime<%=appraisalList.get(0)%>_<%=appraisalList.get(12)%>" name="discussionStartTime" style="width:95px !important;" /></div>
												<br/>
												<div>End Time: <input type="text" id="discussionEndTime<%=appraisalList.get(0)%>_<%=appraisalList.get(12)%>" name="discussionEndTime" style="width:95px !important;" />
													<a href="javascript:void(0);" onclick="convertTimeFormat('<%=appraisalList.get(0)%>','<%=appraisalList.get(12)%>');">Get Total Hrs.</a>
												</div><!-- onclick="convertTimeFormat()" -->
												
											 </td>
											 <td><input type="text" id="totalTimeSpent<%=appraisalList.get(0)%>_<%=appraisalList.get(12)%>" name="totalTimeSpent" style="width:95px !important;" /></td>
											</tr>
										</table>
										</div>
									</div>
								<%} %>
								
							<!-- ===end parvez date: 22-03-2023=== -->	
								<% if(discussionList != null && !discussionList.isEmpty() && discussionList.size()>0){ %>
									<div class="row row_without_margin">
										<div class="col-lg-12 col-md-12 col-sm-12" style="margin-top:1%;">
											<ul>
											<!-- <table class="table-bordered table" > -->
												<%-- <tr>
													<th><%=discussionList.get(3)%></th>
												</tr> --%>
												<!-- <tr>
													<td > -->
													<li>
														<%-- <div style="width: 100%;">
															<div class="col-lg-6 col-md-6 col-sm-12">
															<div class="notranslate" id="starPrimary<%=appraisalList.get(0)%>_<%=appraisalList.get(12)%>"></div>
															<input type="hidden" id="gradewithrating<%=appraisalList.get(0)%>_<%=appraisalList.get(12)%>" value="<%=discussionList.get(1) != null ? uF.parseToDouble(discussionList.get(1)) / 20 + "" : "0"%>" name="gradewithrating<%=appraisalList.get(0)%>_<%=appraisalList.get(12)%>" />
								
															<script type="text/javascript">
																$(function() {
																	$('#starPrimary<%=appraisalList.get(0)%>_<%=appraisalList.get(12)%>').raty({
																		readOnly: true,
																		start: <%=discussionList.get(1) != null ? uF.parseToDouble(discussionList.get(1)) / 20 + "" : "0"%>,
																		half: true,
																		targetType: 'number',
																		click: function(score, evt) {
																			$('#gradewithrating<%=appraisalList.get(0)%>_<%=appraisalList.get(12)%>').val(score);
																		}
																	});
																});
																
															</script>
															</div>
															<div class="col-lg-6 col-md-6 col-sm-12">
															<% if(discussionList!=null && (discussionList.get(3)!=null && !discussionList.get(3).isEmpty() || discussionList.get(4)!=null && uF.parseToBoolean(discussionList.get(4)))){ %>
																<div style="float: right;">
																	<a href="javascript:void(0);" title="Update"><i class="fa fa-pencil-square-o" aria-hidden="true"></i>
																	</a>
																</div>
															<% } else{ %>
																<div style="float: right;">
																	<a onclick="updateCommentRating('<%=appraisalList.get(0)%>','<%=appraisalList.get(12)%>','<%=discussionList.get(0)%>','<%=strEmpId %>');"
																		href="javascript:void(0);" title="Update"><i class="fa fa-pencil-square-o" aria-hidden="true"></i>
																	</a>
																	
																</div>
															<% } %>
															</div>
														</div> --%>
														
														<div style="float: left; width: 100%;">
															<span style="float: left; font-size: 12px; line-height: 32px;"><b>Manager Comment:&nbsp;&nbsp;		
																</b><span class="description" id="textlabel" ><%=uF.showData(discussionList.get(2),"") %></span>
															</span>
														</div>
													</li>	
													<!-- </td>
												</tr>
												<tr> -->
													<!-- <td> -->
													<li>
														<div style="float: left; width: 100%;">
															<span style="float: left; font-size: 12px; line-height: 32px;"><b>Employee Comment:&nbsp;&nbsp;		
																</b><span class="description" id="textlabel" ><%=uF.showData(discussionList.get(3),"")%></span>
															</span>
															<%-- <% if(discussionList!=null && discussionList.get(4) != null && uF.parseToBoolean(discussionList.get(4)) && discussionList.get(5) != null && !uF.parseToBoolean(discussionList.get(5))){ %>
																<span>
																	<a href="javascript:void(0);" onclick="discussionSignOff('1','<%=strEmpId %>', '<%=appraisalList.get(0) %>', '<%=appraisalList.get(12) %>','<%=discussionList.get(0) %>', 'SO')">
																		<i class="fa fa-check-circle checknew" aria-hidden="true"  title="Approve"></i></a>&nbsp;
																	<a href="javascript:void(0);" onclick="discussionSignOff('-1','<%=strEmpId %>', '<%=appraisalList.get(0) %>', '<%=appraisalList.get(12) %>','<%=discussionList.get(0) %>', 'SO')">
																		<i class="fa fa-times-circle cross" aria-hidden="true"  title="Deny"></i></a>&nbsp;
																</span>
															<%} %> --%>
														</div>
													</li>
													<!-- </td>
												</tr>
											</table> -->	
											</ul>
										</div>
									</div>
								<% } %>
								<div class="row row_without_margin">
								
									<% if(dataType!=null && dataType.equals("update") && uF.parseToInt(appraisalList.get(0)) == uF.parseToInt(appId) && uF.parseToInt(appraisalList.get(12)) == uF.parseToInt(appFreqId)){ %>
									<div class="col-lg-12 col-md-12 col-sm-12" style="margin-top:1%;" id="insertDiv">
										
											<%-- <div>
												<script type="text/javascript">
				                                  	$(function() {
				                                  		$('#starUserRating<%=appraisalList.get(0)%>_<%=appraisalList.get(12)%>').raty({
						                                    readOnly: false,
						                                    start: <%=discussionList!=null && discussionList.get(1) != null ? uF.parseToDouble(discussionList.get(1)) / 20 + "" : "0"%>,
						                                    half: true,
						                                    targetType: 'number',
						                                    click: function(score, evt) {
						                                    	$('#hideUserRating<%=appraisalList.get(0)%>_<%=appraisalList.get(12)%>').val(score);
						                                    }
					                                     });
				                                 	});
				                                </script>
												
												<input type="hidden" name="hideUserRating<%=appraisalList.get(0)%>_<%=appraisalList.get(12)%>" id="hideUserRating<%=appraisalList.get(0)%>_<%=appraisalList.get(12)%>"
												 value="<%=discussionList!=null && discussionList.get(1) != null ? uF.parseToDouble(discussionList.get(1)) / 20 + "" : "0"%>" name="gradewithrating<%=appraisalList.get(0)%>_<%=appraisalList.get(12)%>" >
												<div id="starUserRating<%=appraisalList.get(0)%>_<%=appraisalList.get(12)%>" style="float: left; margin: 5px 0px; width: 110px;"></div>
											</div>
											<br /> --%>
											<div style="width: 90%; padding-top: 20px">
												<textarea rows="2" cols="40" style="width: 90% !important;" name="strComment<%=appraisalList.get(0)%>_<%=appraisalList.get(12)%>" id="strComment<%=appraisalList.get(0)%>_<%=appraisalList.get(12)%>"><%=discussionList.get(2)%></textarea>
											</div>
											
											<div style="float: right; margin: 0px 0px 5px 7px; padding-top: 20px; width: 40%;">
												<input type="hidden" name="dicussionId<%=appraisalList.get(0)%>_<%=appraisalList.get(12)%>" id="discussionId_<%=appraisalList.get(0)%>_<%=appraisalList.get(12)%>" value="<%=discussionList.get(0) %>" />
													<a href="javascript:void(0);" onclick="insertOneOneRatingAndComment('<%=strEmpId %>', '<%=appraisalList.get(0) %>', '<%=appraisalList.get(12) %>', 'update')">
														<input type="button" class="btn btn-primary" name="submit" value="Update" id="submit">
													</a>
											</div>
											
										</div>
											
									<% } else{ %>
										<div class="col-lg-12 col-md-12 col-sm-12" style="margin-top:1%;" id="insertDiv">
										<% 
											String empSignOff = "false";
											if(discussionList !=null && discussionList.get(4)!=null && uF.parseToBoolean(discussionList.get(4))){ 
												empSignOff = "true";
											} 
											
										%>
											<div>
												<%
												String marks = null;
												if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_TEN_STAR_RATING_FOR_REVIEW))){
													marks = hmAppraisalFinalScore.get(appraisalList.get(0))!=null ? uF.parseToDouble(hmAppraisalFinalScore.get(appraisalList.get(0))) / 10 +"" : "0" ;
												%>
													<script type="text/javascript">
					                                  	$(function() {
					                                  		$('#starUserRating<%=appraisalList.get(0)%>_<%=appraisalList.get(12)%>').raty({
							                                    readOnly: true,
							                                    start: <%=uF.showData(marks,"0")%>,
							                                    number: 10,
							                                    half: false,
							                                    targetType: 'number',
							                                    click: function(score, evt) {
							                                    	$('#hideUserRating<%=appraisalList.get(0)%>_<%=appraisalList.get(12)%>').val(score);
							                                    }
						                                     });
					                                 	});
					                                </script>
												<%} else{ %>
												<script type="text/javascript">
				                                  	$(function() {
				                                  		$('#starUserRating<%=appraisalList.get(0)%>_<%=appraisalList.get(12)%>').raty({
						                                    /* readOnly: false, */
						                                    readOnly: <%=empSignOff %>,
						                                    start: 0,
						                                    half: true,
						                                    targetType: 'number',
						                                    click: function(score, evt) {
						                                    	$('#hideUserRating<%=appraisalList.get(0)%>_<%=appraisalList.get(12)%>').val(score);
						                                    }
					                                     });
				                                 	});
				                                </script>
												<%} %>
												
												
												<input type="hidden" name="hideUserRating<%=appraisalList.get(0)%>_<%=appraisalList.get(12)%>" id="hideUserRating<%=appraisalList.get(0)%>_<%=appraisalList.get(12)%>">
												<div id="starUserRating<%=appraisalList.get(0)%>_<%=appraisalList.get(12)%>" style="float: left; margin: 5px 0px; width: 110px;"></div>&nbsp;<%=uF.showData(marks,"0")%>
											</div>
											<br />
											
											<%-- <div style="width: 90%; padding-top: 20px">
												<textarea rows="2" cols="40" style="width: 90% !important;" name="strComment<%=appraisalList.get(0)%>_<%=appraisalList.get(12)%>" id="strComment<%=appraisalList.get(0)%>_<%=appraisalList.get(12)%>" <%=uF.parseToBoolean(empSignOff)?"readonly":"" %>></textarea>
											</div> --%>
											<% if(discussionList != null && discussionList.get(5) != null && uF.parseToBoolean(discussionList.get(5))){ %>
												<div style="width: 90%; padding-top: 20px">
													<textarea rows="2" cols="40" style="width: 90% !important;" name="strComment<%=appraisalList.get(0)%>_<%=appraisalList.get(12)%>" id="strComment<%=appraisalList.get(0)%>_<%=appraisalList.get(12)%>" readonly></textarea>
												</div>
											<% } else{ %>
												<div style="width: 90%; padding-top: 20px">
													<textarea rows="2" cols="40" style="width: 90% !important;" name="strComment<%=appraisalList.get(0)%>_<%=appraisalList.get(12)%>" id="strComment<%=appraisalList.get(0)%>_<%=appraisalList.get(12)%>" ></textarea>
												</div>
											<% } %>
											
											<div style="float: right; margin: 0px 0px 5px 7px; padding-top: 20px; width: 40%;">
												<%-- <%if(uF.parseToBoolean(empSignOff)){ %>
													<input type="button" class="btn btn-primary" name="submit" value="Submit" disabled = "disabled" >&nbsp;&nbsp;
												<%} else{ %>
													<a href="javascript:void(0);" onclick="insertOneOneRatingAndComment('<%=strEmpId %>', '<%=appraisalList.get(0) %>', '<%=appraisalList.get(12) %>', 'insert')">
														<input type="button" class="btn btn-primary" name="submit" value="Submit">
													</a>&nbsp;&nbsp;
												<%} %> --%>
												<%-- <%System.out.println("OORD.jsp/453---empSignOff="+discussionList.get(4)+"---userSignOf="+discussionList.get(5)); %> --%>
												<%-- <%if(discussionList!=null && discussionList.get(4) != null && uF.parseToBoolean(discussionList.get(4)) && discussionList.get(5) != null && !uF.parseToBoolean(discussionList.get(5))){ %>	
													<a href="javascript:void(0);" onclick="discussionSignOff('1','<%=strEmpId %>', '<%=appraisalList.get(0) %>', '<%=appraisalList.get(12) %>','<%=discussionList.get(0) %>', 'SO')">
														<input type="button" class="btn btn-primary" name="submit" value="Sign Off">
													</a>&nbsp;&nbsp;
												<%} else if(discussionList != null && discussionList.get(5) != null && uF.parseToBoolean(discussionList.get(5))){ %>	
													<input type="button" class="btn btn-primary" name="submit" value="Sign Off" disabled = "disabled" >&nbsp;&nbsp;
												<%} %> --%>
												
												<%if(discussionList != null && discussionList.get(5) != null && uF.parseToBoolean(discussionList.get(5))){ %>
													<input type="button" class="btn btn-primary" name="submit" value="Sign Off" disabled = "disabled" >&nbsp;&nbsp;
													<input type="button" class="btn btn-primary" name="submit" value="Deny" disabled = "disabled" >&nbsp;&nbsp;
												<%} else{ %>
													<%if(discussionList!=null && discussionList.get(3)!=null && !discussionList.get(3).equals("")){ %>
														<a href="javascript:void(0);" onclick="discussionSignOff('1','<%=strEmpId %>', '<%=appraisalList.get(0) %>', '<%=appraisalList.get(12) %>','<%=discussionList.get(0) %>', 'SO')">
															<input type="button" class="btn btn-primary" name="submit" value="Sign Off">
														</a>&nbsp;&nbsp;
														<a href="javascript:void(0);" onclick="insertOneOneRatingAndComment('<%=strEmpId %>', '<%=appraisalList.get(0) %>', '<%=appraisalList.get(12) %>', 'insert')">
															<input type="button" class="btn btn-primary" name="submit" value="Deny">
														</a>
													<%}else{ %>
														<%if(discussionList==null){ %>
														<a href="javascript:void(0);" onclick="insertOneOneRatingAndComment('<%=strEmpId %>', '<%=appraisalList.get(0) %>', '<%=appraisalList.get(12) %>', 'insert')">
															<input type="button" class="btn btn-primary" name="submit" value="Submit">
														</a>
														<%} %>
													<%} %>
												<%} %>
											</div>
											
										</div>
									<% } %>
									
								</div>
												
							</div>
						</div>	
					</li>
				<% } %>
			<% } %>
			</ul>
		<% } else{ %>
			<div class="nodata msg" style="width: 96%;">No data available.</div>
		<% } %>
		
	</div>
	
	<div class="modal" id="modalInfo" role="dialog">
	<div class="modal-dialog">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">&times;</button>
				<h4 class="modal-title">View Information</h4>
			</div>
			<div class="modal-body"
				style="height: 400px; overflow-y: auto; padding-left: 25px;">
			</div>
			<div class="modal-footer">
				<button type="button" id="closeButton" class="btn btn-default"
					data-dismiss="modal">Close</button>
			</div>
		</div>
	</div>
</div>
<!-- </div> -->
<script>
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
		
		/* $("#dateOfDiscussion").datepicker({ format : 'dd/mm/yyyy' });
		
		$('#discussionStartTime').datetimepicker({
			format: 'HH:mm'
	    }).on('dp.change', function(e){ 
	    	$('#discussionEndTime').data("DateTimePicker").minDate(e.date);
	    });
		
		$('#discussionEndTime').datetimepicker({
			format: 'HH:mm'
	    }).on('dp.change', function(e){ 
	    	$('#discussionStartTime').data("DateTimePicker").maxDate(e.date);
	    }); */
		$("input[name = dateOfDiscussion]").datepicker({ format : 'dd/mm/yyyy' });
		
		$('input[name = discussionStartTime]').datetimepicker({
			format: 'HH:mm'
	    }).on('dp.change', function(e){ 
	    	$('input[name = discussionEndTime]').data("DateTimePicker").minDate(e.date);
	    });
		
		$('input[name = discussionEndTime]').datetimepicker({
			format: 'HH:mm'
	    }).on('dp.change', function(e){ 
	    	$('input[name = discussionStartTime]').data("DateTimePicker").maxDate(e.date);
	    });
	});
</script>