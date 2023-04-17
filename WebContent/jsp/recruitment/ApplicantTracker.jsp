<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>
<%@page import="java.text.DateFormat"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Map.Entry"%>
<%@page import="com.konnect.jpms.util.IConstants"%> 
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.*"%>
<g:compress>
    <style type="text/css" media="all">
        /* fix rtl for demo */
        .chosen-rtl .chosen-drop { left: -9000px; }
        .status_default {
        background-color: #DDDDDD;
        color: black;
        }
        .status_pendding {
        background-color: #FFB500;
        color: white;
        }
        .status_accept {
        background-color: #20B2AA;
        color: white;
        }
        .status_reject {
        background-color: #FF0000;
        color: white;
        }
    </style>
</g:compress>
<%-- <script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script type="text/javascript" src="scripts/jquery-ui.min.js"> </script> --%>

<script type="text/javascript">
     function openCandidateProfilePopup(CandID,recruitId,apptype) {
    		var id=document.getElementById("panelDiv");
    		if(id) {
    			id.parentNode.removeChild(id);
    		}
    		var dialogEdit = '.modal-body';
    		$(dialogEdit).empty();
    		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    		$("#modalInfo").show();
    		$(".modal-title").html('Candidate Information');
    		if($(window).width() >= 900){
    			 $(".modal-dialog").width(900);
    		 }
    		$.ajax({
    			url :"CandidateMyProfilePopup.action?CandID="+CandID+"&recruitId="+recruitId+"&form=A&apptype="+apptype,
    			cache : false,
    			success : function(data) {
    				$(dialogEdit).html(data);
    			}
    		});
    	 }
     
    	
	/*  function closeJob(recruitmentId) {
    	var dialogEdit = '.modal-body';
    	$(dialogEdit).empty();
    	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$("#modalInfo").show();
    	$(".modal-title").html('Close Job');
    	$.ajax({
    		url : "CloseJob.action?recruitmentId="+recruitmentId+"&fromPage=Application",
    		cache : false,
    		success : function(data) {
    			$(dialogEdit).html(data);
    		}
    	});
    } */ 
    
    
   /*  function submitForm(){
    	document.frmApplicantTracker.proPage.value = '';
    	document.frmApplicantTracker.minLimit.value = '';
    	document.frmApplicantTracker.submit();
    }
    
    
    function loadMore(proPage, minLimit) {
    	document.frmApplicantTracker.proPage.value = proPage;
    	document.frmApplicantTracker.minLimit.value = minLimit;
    	document.frmApplicantTracker.submit();
    } */
    
    function generateReportExcel() {
    	window.location = "ExportExcelReport.action?excelType=STANDARD";
    }
    
    
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
    
</script>
        		<%
                UtilityFunctions uF = new UtilityFunctions();
                String dataType = (String) request.getAttribute("dataType");
                %>
                <div class="box-body" style="padding: 0px; overflow-y: auto; min-height: 600px;">

                    <%
                        String strUserType = (String) session.getAttribute(IConstants.USERTYPE);
                        String proCount = (String)request.getAttribute("proCount");
                        
                        List<String> recruitmentIdList = (List<String>)request.getAttribute("recruitmentIdList");
                        
                        Map<String, String> hmRecruitmentData = (Map<String, String>)request.getAttribute("hmRecruitmentData");
                        if(hmRecruitmentData == null) hmRecruitmentData = new HashMap<String, String>();
                        
                        Map<String, Map<String, Map<String, String>>> hmRecruitwiseCandiData = (Map<String, Map<String, Map<String, String>>>)request.getAttribute("hmRecruitwiseCandiData");
                        if(hmRecruitwiseCandiData == null) hmRecruitwiseCandiData = new HashMap<String, Map<String, Map<String, String>>>();
                        
                        Map<String, Map<String, String>> hmcandiStarRecruitwise = (Map<String, Map<String, String>>)request.getAttribute("hmcandiStarRecruitwise");
                        if(hmcandiStarRecruitwise==null) hmcandiStarRecruitwise = new HashMap<String, Map<String, String>>();
                        
                        String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
                        
                        Map<String, String> hmCandToEmp = (Map<String, String>) request.getAttribute("hmCandToEmp");
                        if(hmCandToEmp == null) hmCandToEmp = new HashMap<String, String>();
                        
                        Map<String, List<String>> hmRecruitwiseCandiStageStatus = (Map<String, List<String>>)request.getAttribute("hmRecruitwiseCandiStageStatus");
                        if(hmRecruitwiseCandiStageStatus == null) hmRecruitwiseCandiStageStatus = new HashMap<String, List<String>>();
                        
                        Map<String, Map<String, String>> hmRecruitAndCandiwiseStatusData = (Map<String, Map<String, String>>)request.getAttribute("hmRecruitAndCandiwiseStatusData");
                        if(hmRecruitAndCandiwiseStatusData == null) hmRecruitAndCandiwiseStatusData = new HashMap<String, Map<String, String>>();
                        
                        Map<String, Map<String, String>> hmRecruitAndCandiwiseCurrentRoundDetails = (Map<String, Map<String, String>>)request.getAttribute("hmRecruitAndCandiwiseCurrentRoundDetails");
                        if(hmRecruitAndCandiwiseCurrentRoundDetails == null) hmRecruitAndCandiwiseCurrentRoundDetails = new HashMap<String, Map<String, String>>();
                        
                        Map<String, Map<String, Map<String, String>>> hmSource = (Map<String, Map<String, Map<String, String>>>) request.getAttribute("hmSource");
                        if(hmSource == null) hmSource = new HashMap<String, Map<String,Map<String,String>>>();
                        
                        %>
                    <%=uF.showData((String)session.getAttribute(IConstants.MESSAGE), "") %>
                    <%		
                        for(int r=0; recruitmentIdList!= null && r<recruitmentIdList.size(); r++) {
                        	String recruitId = recruitmentIdList.get(r);
                        	Map<String, String> hmCandiStars = hmcandiStarRecruitwise.get(recruitId);
                        	
                        	Map<String, Map<String, String>> hmCandidate = hmSource.get(recruitId);
                        	if(hmCandidate == null) hmCandidate = new HashMap<String, Map<String,String>>();
                        	
                        	
                        	String priorityClass = "";
                        	if (uF.parseToInt(hmRecruitmentData.get(recruitId+"_PRIORITY")) == 1) {
                        		priorityClass ="high";
                        	}else if (uF.parseToInt(hmRecruitmentData.get(recruitId+"_PRIORITY")) == 2) {
                        		priorityClass ="medium";
                        	}else{
                        		priorityClass ="low";
                        	}
                        	boolean closeFlag = false;
                        	if(uF.parseToBoolean(hmRecruitmentData.get(recruitId+"_JOB_STATUS"))){ 
                        		closeFlag = true;
                        	}
                        	Map<String, String> hmRecrAndCandiwiseStatusData = hmRecruitAndCandiwiseStatusData.get(recruitId);
                        	if(hmRecrAndCandiwiseStatusData == null) hmRecrAndCandiwiseStatusData = new HashMap<String, String>();
                        	
                        	List<String> alCandiApplicationIds = hmRecruitwiseCandiStageStatus.get(recruitId+"_C_APPLICATIONS");
                        	List<String> alCandiAppShortlistIds = hmRecruitwiseCandiStageStatus.get(recruitId+"_C_APP_SHORTLIST");
                        	List<String> alCandiAppRejectIds = hmRecruitwiseCandiStageStatus.get(recruitId+"_C_APP_REJECT");
                        	List<String> alCandiOfferOnHoldIds = hmRecruitwiseCandiStageStatus.get(recruitId+"_C_OFFER_ONHOLD");
                        	List<String> alCandiUnderInterviewIds = hmRecruitwiseCandiStageStatus.get(recruitId+"_C_UNDER_INTERVIEW");
                        	List<String> alCandiRejectInterviewIds = hmRecruitwiseCandiStageStatus.get(recruitId+"_C_REJECT_INTERVIEW");
                        	List<String> alCandiFinalInterviewIds = hmRecruitwiseCandiStageStatus.get(recruitId+"_C_FINAL_INTERVIEW");
                        	List<String> alCandiOfferedIds = hmRecruitwiseCandiStageStatus.get(recruitId+"_C_OFFERED");
                        	List<String> alCandiOfferAcceptIds = hmRecruitwiseCandiStageStatus.get(recruitId+"_C_OFFER_ACCEPT");
                        	List<String> alCandiOfferRejectIds = hmRecruitwiseCandiStageStatus.get(recruitId+"_C_OFFER_REJECT");
                        	List<String> alCandiOnboardPendingIds = hmRecruitwiseCandiStageStatus.get(recruitId+"_C_ONBOARD_PENDING");
                        	List<String> alCandiOnboardIds = hmRecruitwiseCandiStageStatus.get(recruitId+"_C_ONBOARD");
                        	
                        	Map<String, String> hmRecrAndCandiwiseCurrentRoundDetails = hmRecruitAndCandiwiseCurrentRoundDetails.get(recruitId);
                        	if(hmRecrAndCandiwiseCurrentRoundDetails == null) hmRecrAndCandiwiseCurrentRoundDetails = new HashMap<String, String>();
                        	//
                        %>
                        <div class="col-md-12" style="margin: 0px 0px 10px;">   		
							<a onclick="generateReportExcel();" href="javascript:void(0)" style="background-repeat: no-repeat;float: right;"><i class="fa fa-file-excel-o" aria-hidden="true"></i></a>
						</div>
                   <div class="col-md-12" style="margin: 0px;">
                        <!-- <div class="box box-default collapsed-box" style="margin-top: 10px;"> -->
                            <div class="box-header with-border" style="padding: 0px;">
                                <h3 class="box-title <%=priorityClass %>" style="font-size: 14px;padding-right: 10px;">
                                    <div class="row row_without_margin">
                                        <div class="col-lg-12">
                                            All Applications <span style="font-family: Digital; font-size: 22px;"><%=(alCandiApplicationIds != null && !alCandiApplicationIds.isEmpty()) ? alCandiApplicationIds.size() : "0" %></span>
                                            &nbsp;&nbsp;Application Shortlisted <span style="font-family: Digital; font-size: 22px;"><%=(alCandiAppShortlistIds != null && !alCandiAppShortlistIds.isEmpty()) ? alCandiAppShortlistIds.size() : "0" %></span>
                                            &nbsp;&nbsp;Application Rejected <span style="font-family: Digital; font-size: 22px;"><%=(alCandiAppRejectIds != null && !alCandiAppRejectIds.isEmpty()) ? alCandiAppRejectIds.size() : "0" %></span>
                                            &nbsp;&nbsp;Interview <span style="font-family: Digital; font-size: 22px;"><%=(alCandiUnderInterviewIds != null && !alCandiUnderInterviewIds.isEmpty()) ? alCandiUnderInterviewIds.size() : "0" %></span>
                                            &nbsp;&nbsp;Rejected <span style="font-family: Digital; font-size: 22px;"><%=(alCandiRejectInterviewIds != null && !alCandiRejectInterviewIds.isEmpty()) ? alCandiRejectInterviewIds.size() : "0" %></span>
                                            &nbsp;&nbsp;Offer Hold <span style="font-family: Digital; font-size: 22px;"><%=(alCandiOfferOnHoldIds != null && !alCandiOfferOnHoldIds.isEmpty()) ? alCandiOfferOnHoldIds.size() : "0" %></span>
                                            &nbsp;&nbsp;Finalisation <span style="font-family: Digital; font-size: 22px;"><%=(alCandiFinalInterviewIds != null && !alCandiFinalInterviewIds.isEmpty()) ? alCandiFinalInterviewIds.size() : "0" %></span>
                                            &nbsp;&nbsp;Offered <span style="font-family: Digital; font-size: 22px;"><%=(alCandiOfferedIds != null && !alCandiOfferedIds.isEmpty()) ? alCandiOfferedIds.size() : "0" %></span>
                                            &nbsp;&nbsp;Offer Accepted <span style="font-family: Digital; font-size: 22px;"><%=(alCandiOfferAcceptIds != null && !alCandiOfferAcceptIds.isEmpty()) ? alCandiOfferAcceptIds.size() : "0" %></span>
                                            &nbsp;&nbsp;Offer Rejected <span style="font-family: Digital; font-size: 22px;"><%=(alCandiOfferRejectIds != null && !alCandiOfferRejectIds.isEmpty()) ? alCandiOfferRejectIds.size() : "0" %></span>
                                            &nbsp;&nbsp;Onboard Pending <span style="font-family: Digital; font-size: 22px;"><%=(alCandiOnboardPendingIds != null && !alCandiOnboardPendingIds.isEmpty()) ? alCandiOnboardPendingIds.size() : "0" %></span>
                                            &nbsp;&nbsp;Onboarded <span style="font-family: Digital; font-size: 22px;"><%=(alCandiOnboardIds != null && !alCandiOnboardIds.isEmpty()) ? alCandiOnboardIds.size() : "0" %></span>
                                        </div>
                                    </div>
                                </h3>
                                
                                
                                 	<h4>
                                        <b>Applicants By Stage: </b><%-- (<%=uF.showData(hmSelectCount.get(recruitId), "0")%>) --%>
                                    </h4>
                                    <div>
                                        <% 
                                            //System.out.println("hmRecruitwiseCandiData ===>> " + hmRecruitwiseCandiData);
                                            Map<String, Map<String, String>> hmCandiwiseData = hmRecruitwiseCandiData.get(recruitId);
                                            //System.out.println("recruitId ===>> "+ recruitId +" -- hmCandiwiseData ===>> " + hmCandiwiseData);
                                            if(hmCandiwiseData != null && !hmCandiwiseData.isEmpty()) {
                                            	Iterator<String> it = hmCandiwiseData.keySet().iterator();
                                            	while(it.hasNext()) {
                                            		String candidateId = it.next();
                                            		String clsShortlist = "status_pendding";
                                            		String clsInterview = "status_default";
                                            		String clsOffer = "status_default";
                                            		String clsOnboard = "status_default";
                                            		
                                            		String strStars = hmCandiStars.get(candidateId);
                                            		Map<String, String> hmSourceDetails = hmCandidate.get(candidateId);
                                                    if(hmSourceDetails == null) hmSourceDetails = new HashMap<String, String>();
                                                    
                                            		String txtApply = "Applied at "+ (hmRecrAndCandiwiseStatusData.get(candidateId+"_APPLY_DATE") != null ? hmRecrAndCandiwiseStatusData.get(candidateId+"_APPLY_DATE") : "");
                                            		String txtInterview = "";
                                            		String txtOffer = "";
                                            		String txtOnboard = "";
                                            		
                                            		Map<String, String> hmCandiInner = hmCandiwiseData.get(candidateId);
                                            
                                            		if(alCandiAppShortlistIds != null && alCandiAppShortlistIds.contains(candidateId)) {
                                            			clsShortlist = "status_accept";
                                            			txtApply = "Shortlisted at "+ (hmRecrAndCandiwiseStatusData.get(candidateId+"_APPLICATION_S_R_DATE") != null ? hmRecrAndCandiwiseStatusData.get(candidateId+"_APPLICATION_S_R_DATE") : "");
                                            			txtInterview = "Scheduled";
                                            		}
                                            		if(alCandiAppRejectIds != null && alCandiAppRejectIds.contains(candidateId)) {
                                            			clsShortlist = "status_reject";
                                            			txtApply = "Rejected at "+ (hmRecrAndCandiwiseStatusData.get(candidateId+"_APPLICATION_S_R_DATE") != null ? hmRecrAndCandiwiseStatusData.get(candidateId+"_APPLICATION_S_R_DATE") : "");
                                            		}
                                            		
                                            		if(alCandiUnderInterviewIds != null && alCandiUnderInterviewIds.contains(candidateId)) {
                                            			clsInterview = "status_pendding";
                                            			txtInterview = "Next Round: <br/>" + uF.showData(hmRecrAndCandiwiseCurrentRoundDetails.get(candidateId+"_NEXT_ROUND_DATA"), "");
                                            		}
                                            		
                                            		if(alCandiOfferOnHoldIds != null && alCandiOfferOnHoldIds.contains(candidateId)) {
                                            			clsInterview = "status_pendding";
                                            			txtInterview = "Offer on hold";
                                            		}
                                            		
                                            		if((alCandiOfferOnHoldIds == null || !alCandiOfferOnHoldIds.contains(candidateId)) && alCandiFinalInterviewIds != null && alCandiFinalInterviewIds.contains(candidateId)) {
                                            			clsInterview = "status_accept";
                                            			txtInterview = "Finalization at "+ (hmRecrAndCandiwiseStatusData.get(candidateId+"_FINALIZE_DATE") != null ? hmRecrAndCandiwiseStatusData.get(candidateId+"_FINALIZE_DATE") : "");
                                            		}
                                            		if(alCandiRejectInterviewIds != null && alCandiRejectInterviewIds.contains(candidateId)) {
                                            			clsInterview = "status_reject";
                                            			txtInterview = "Rejected at "+ (hmRecrAndCandiwiseStatusData.get(candidateId+"_FINALIZE_DATE") != null ? hmRecrAndCandiwiseStatusData.get(candidateId+"_FINALIZE_DATE") : "");
                                            		}
                                            		
                                            		if(alCandiOfferedIds != null && alCandiOfferedIds.contains(candidateId)) {
                                            			clsOffer = "status_pendding";
                                            			txtOffer = "Offered at "+ (hmRecrAndCandiwiseStatusData.get(candidateId+"_FINALIZE_DATE") != null ? hmRecrAndCandiwiseStatusData.get(candidateId+"_FINALIZE_DATE") : "")
                                            			+"<br/> CTC: "+ (hmRecrAndCandiwiseStatusData.get(candidateId+"_OFFERED_AMOUNT") != null ? hmRecrAndCandiwiseStatusData.get(candidateId+"_OFFERED_AMOUNT") : "0")
                                            			+"<br/>"+ (hmRecrAndCandiwiseStatusData.get(candidateId+"_HR_COMMENT") != null ? hmRecrAndCandiwiseStatusData.get(candidateId+"_HR_COMMENT") : "0");
                                            		}
                                            		if(alCandiOfferAcceptIds != null && alCandiOfferAcceptIds.contains(candidateId)) {
                                            			clsOffer = "status_accept";
                                            			txtOffer = "Accepted at "+ (hmRecrAndCandiwiseStatusData.get(candidateId+"_OFFER_A_R_DATE") != null ? hmRecrAndCandiwiseStatusData.get(candidateId+"_OFFER_A_R_DATE") : "")
                                            			+"<br/> CTC: "+ (hmRecrAndCandiwiseStatusData.get(candidateId+"_OFFERED_AMOUNT") != null ? hmRecrAndCandiwiseStatusData.get(candidateId+"_OFFERED_AMOUNT") : "0")
                                            			+"<br/>"+ (hmRecrAndCandiwiseStatusData.get(candidateId+"_OFFER_A_R_COMMENT") != null ? hmRecrAndCandiwiseStatusData.get(candidateId+"_OFFER_A_R_COMMENT") : "0");
                                            		}
                                            		if(alCandiOfferRejectIds != null && alCandiOfferRejectIds.contains(candidateId)) {
                                            			clsOffer = "status_reject";
                                            			txtOffer = "Rejected at "+ (hmRecrAndCandiwiseStatusData.get(candidateId+"_OFFER_A_R_DATE") != null ? hmRecrAndCandiwiseStatusData.get(candidateId+"_OFFER_A_R_DATE") : "")
                                            			+"\n CTC: "+ (hmRecrAndCandiwiseStatusData.get(candidateId+"_OFFERED_AMOUNT") != null ? hmRecrAndCandiwiseStatusData.get(candidateId+"_OFFERED_AMOUNT") : "0")
                                            			+"\n"+ (hmRecrAndCandiwiseStatusData.get(candidateId+"_OFFER_A_R_COMMENT") != null ? hmRecrAndCandiwiseStatusData.get(candidateId+"_OFFER_A_R_COMMENT") : "0");
                                            		}
                                            		
                                            		if(alCandiOnboardPendingIds != null && alCandiOnboardPendingIds.contains(candidateId)) {
                                            			clsOnboard = "status_pendding";
                                            			txtOnboard = "Ready to Onboard for " + (hmRecrAndCandiwiseStatusData.get(candidateId+"_JOINING_DATE") != null ? hmRecrAndCandiwiseStatusData.get(candidateId+"_JOINING_DATE") : "");
                                            		}
                                            		if(alCandiOnboardIds != null && alCandiOnboardIds.contains(candidateId)) {
                                            			clsOnboard = "status_accept";
                                            			txtOnboard = "Onboarded at " + (hmRecrAndCandiwiseStatusData.get(candidateId+"_JOINING_DATE") != null ? hmRecrAndCandiwiseStatusData.get(candidateId+"_JOINING_DATE") : "");
                                            		}
                                            		
                                            %>
                                        <div class="row row_without_margin clr">
                                            <div class="col-lg-3">
                                                <div style="float: left;padding: 9px 5px 0px;vertical-align: top;">
	                                                <div style="float: left;">
	                                                    <%if(docRetriveLocation == null) { %>
	                                                    <img height="30" width="30" border="0" class="lazy img-circle" id="profilecontainerimg<%=recruitId+"_"+candidateId %>" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + hmCandiInner.get("CANDI_IMAGE") %>" />
	                                                    <%} else { %>
	                                                    <img height="30" width="30" border="0" class="lazy img-circle" id="profilecontainerimg<%=recruitId+"_"+candidateId %>" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_CANDIDATE+"/"+IConstants.I_IMAGE+"/"+candidateId+"/"+IConstants.I_100x100+"/"+hmCandiInner.get("CANDI_IMAGE") %>" />
	                                                    <%} %> 
	                                                </div>
	                                                <div style="float: left;padding-top: 7px;">
	                                                    <%if(closeFlag) { %>
	                                                    <%=hmCandiInner.get("CANDI_NAME") %>
	                                                    <%} else { %>
	                                                    <a href="javascript: void(0)" onclick="<%if(hmCandToEmp.containsKey(candidateId.trim())){%>alert('<%=hmCandToEmp.get(candidateId.trim()) %>');<%} else {%>openCandidateProfilePopup('<%=candidateId%>','<%=recruitId %>','')<%}%>"> <%=hmCandiInner.get("CANDI_NAME") %></a>
	                                                    <%} %>
	                                                    <br/>
	                                                    <div style="line-height: 16px;">
                                                            <span id="Reting<%=candidateId %>" style="margin-right: 5px; line-height: 18px;"><%=strStars %>/5</span>
                                                            <span id="starPrimaryS<%=recruitId+"_"+candidateId %>" style="margin-left: 5px; line-height: 12px;"></span>
                                                        </div>
                                                        <script type="text/javascript">
	                                                         $('#starPrimaryS'+'<%=recruitId+"_"+candidateId %>').raty({
	                                                               readOnly: true,
	                                                               start:	<%=strStars %> ,
	                                                               half: true,
	                                                               targetType: 'number'
	                                                         });
	                                                    </script>
                                                        <div style="font-size: 11px; line-height: 22px;"><strong style="color: gray;">Source:</strong>&nbsp;<%=uF.showData(hmSourceDetails.get("SOURCE_TYPE"),"-") %></div>
                                                        <div style="font-size: 11px; line-height: 15px;"><strong style="color: gray;">Source Name:</strong>&nbsp;<%=uF.showData(hmSourceDetails.get("SOURCE_NAME"),"-") %></div>
	                                                </div>
                                                </div>
                                            </div>
                                            <div class="col-lg-9" style="padding: 0px;">
                                                <div style="padding: 9px 5px 0px;vertical-align: top; width: 24%;" class="inline-block">
                                                    <div class="<%=clsShortlist %>" style="text-align: center; width: 80%; line-height: 27px;">SHORTLISTED</div>
                                                    <div style="line-height: 12px; width: 100%; font-style: italic; color: gray; padding-top: 5px;"><%=txtApply %></div>
                                                </div>
                                                <div style="padding: 9px 5px 0px;vertical-align: top; width: 24%;" class="inline-block">
                                                    <div class="<%=clsInterview %>" style="text-align: center; width: 80%; line-height: 27px;">INTERVIEW</div>
                                                    <div style="line-height: 12px; width: 100%; font-style: italic; color: gray; padding-top: 5px;"><%=txtInterview %></div>
                                                </div>
                                                <div style="padding: 9px 5px 0px;vertical-align: top; width: 24%;" class="inline-block">
                                                    <div class="<%=clsOffer %>" style="text-align: center; width: 80%; line-height: 27px;">OFFER</div>
                                                    <div style="line-height: 12px; width: 100%; font-style: italic; color: gray; padding-top: 5px;"><%=txtOffer %></div>
                                                </div>
                                                <div style="padding: 9px 5px 0px;vertical-align: top; width: 24%;" class="inline-block">
                                                    <div class="<%=clsOnboard %>" style="text-align: center; width: 80%; line-height: 27px;">JOINING</div>
                                                    <div style="line-height: 12px; width: 100%; font-style: italic; color: gray; padding-top: 5px;"><%=txtOnboard %></div>
                                                </div>
                                            </div>
                                        </div>
                                        <% } %>
                                        <% } else { %>
                                        <div class="nodata msg"> <span>No Applicants available.</span> </div>
                                        <% } %>
                                    </div>
                                    
                                    
                                    
                                <!-- <div class="box-tools pull-right">
                                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
                                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                                </div> -->
                            </div>
                            </div>
                            <!--  -->
                           <!-- </div> -->
                            <!-- /.box-header -->
                            <!-- <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
                                <div class="attendance">
                                   
                                </div>
                            </div> -->
                            <!-- /.box-body -->
                        
                    
                    <% } %>
                    <% if(recruitmentIdList.size() == 0) { %>
                    <div class="nodata msg">
                        <span>No Data Available.</span>
                    </div>
                    <% } %>
                </div>
                <!-- /.box-body -->


<div class="modal" id="modalInfo" role="dialog">
    <div class="modal-dialog">
        Modal content
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


<script>
    //$("img.lazy").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10});
    $("img.lazy").lazyload({event : "sporty", threshold : 200,effect : "fadeIn",failure_limit : 10});
    
    $(window).bind("load", function() {
        var timeout = setTimeout(function() { $("img.lazy").trigger("sporty") }, 1000);
    });
</script>