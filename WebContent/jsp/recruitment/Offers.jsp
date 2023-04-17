<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%> 


<style>

 .label 
    {
    	font-size: 13px;
	}
	
	</style>
	
	
	<script type="text/javascript"> 
    function panelcomment(panelid, candidateid, recruitId) { 
		
	    	var dialogEdit = '.modal-body';
	    	$(dialogEdit).empty();
	    	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	    	$('.modal-title').html('Panel Comments');
	    	$("#modalInfo").show();
	    	$.ajax({
				url : "PopupPanelcomments.action?panelid="+panelid+"&candidateid="+candidateid +"&recruitId="+recruitId,
				cache : false,
				success : function(data) {
					$(dialogEdit).html(data);
				}
			});
    	}
    		
    
    function salaryRenegotiatePopup(candidateID,recruitId,tableMode) { 
		
	    	var dialogEdit = '.modal-body';
	    	$(dialogEdit).empty();
	    	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	    	$('.modal-title').html('Candidate Salary Renegotiation');
	    	$("#modalInfo").show();
	    	if($(window).width() >= 900){
		   		$(".modal-dialog").width(900);
		   	}
	    	$.ajax({
				url : "SalaryRenegotiatePopup.action?candidateID="+candidateID+"&recruitId="+recruitId
						+"&tableMode="+tableMode,
				cache : false,
				success : function(data) {
					$(dialogEdit).html(data);
				}
			});
    	}		
     
    
	function offerRejectAfterAcceptPopup(candidateID,recruitId) { 
    	
    	var dialogEdit = '.modal-body';
    	$(dialogEdit).empty();
    	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$('.modal-title').html('Candidate Offer Back Out');
    	$("#modalInfo").show();
    	$.ajax({
			url : "OfferAcceptAndRenegotiate.action?candidateID="+candidateID+"&recruitId="+recruitId+"&rejectType=CANDIBACKOUT",
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
    }
    
    
    function offerAcceptPopup(candidateID,recruitId) { 
    	
    	var dialogEdit = '.modal-body';
    	$(dialogEdit).empty();
    	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$('.modal-title').html('Candidate Offer Accept/ Reject');
    	$("#modalInfo").show();
    	$.ajax({
			url : "OfferAcceptAndRenegotiate.action?candidateID="+candidateID+"&recruitId="+recruitId,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
    }		
    
    
  //====start parvez on 09-07-2021==== 
    function openCandidateProfilePopup(CandID,recruitId,apptype) {
    //function openCandidateProfilePopup(CandID,recruitId) { 
    //====end parvez on 09-07-2021==== 
    	
    		var id=document.getElementById("panelDiv");
    		if(id){
    			id.parentNode.removeChild(id);
    				}
    		var dialogEdit = '.modal-body';
        	$(dialogEdit).empty();
        	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
        	$('.modal-title').html('Candidate Information');
        	$("#modalInfo").show();
        	if($(window).width() >= 1100){
        		$(".modal-dialog").width(1100);
        	}
        	$.ajax({
				//url : "ApplyLeavePopUp.action", 
				//====start parvez on 09-07-2021==== 
				//url :"CandidateMyProfilePopup.action?CandID="+CandID+"&recruitId="+recruitId, 
				url :"CandidateMyProfilePopup.action?CandID="+CandID+"&recruitId="+recruitId+"&form=A&apptype="+apptype,
				//====end parvez on 09-07-2021==== 
				cache : false,
				success : function(data) {
					$(dialogEdit).html(data);
				}
			});
    
    	 }
    
    
    function resendOfferLetterToCandidate(candidateId, recruitId) {
    	//alert("candidateId ===> "+ candidateId);
    		getContent('resendOfferLatterSpan_'+recruitId+'_'+candidateId, "ResendOfferLetterToCandidate.action?candidateId="+candidateId+"&recruitId="+recruitId);
    }
    
    
    /* function loadMore(proPage, minLimit) {
    	document.frmOffers.proPage.value = proPage;
    	document.frmOffers.minLimit.value = minLimit;
    	document.frmOffers.submit();
    } */
    
    
    function viewAssessmentDetail(assessmentId, assessmentName) {
        var dialogEdit = '.modal-body';
    	$(dialogEdit).empty();
    	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$('.modal-title').html(''+assessmentName+'');
    	$("#modalInfo").show();
    	$.ajax({
    		url : "ViewAssessmentDetails.action?assessmentId="+assessmentId,
    		cache : false,
    		success : function(data) {
    			$(dialogEdit).html(data);
    		}
    	});
     }	
    
    
    function downloadOfferLetter(candidateID, recruitId) {
    	//alert("candidateID ===>> " + candidateID+" -- recruitId ===>> " + recruitId);
    	window.location = "CandiOfferLetterPreview.action?candidateId="+candidateID+"&recruitId="+recruitId;
    }
    
    function previewOfferLetter(candidateID, recruitId) {
    	//alert("candidateID ===>> " + candidateID+" -- recruitId ===>> " + recruitId);
    	window.location = "CandiOfferLetterPreview.action?operation=preview&candidateId="+candidateID+"&recruitId="+recruitId;
    }
</script>

<%-- <script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script type="text/javascript" src="scripts/jquery-ui.min.js"> </script> --%>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/_rating/js/jquery.raty.js"> </script>

<script type="text/javascript">
    $(function() {
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
</script>

<%
    CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
    UtilityFunctions uF=new UtilityFunctions();
    
    String strUserType = (String) session.getAttribute(IConstants.USERTYPE);
    String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
    
    Map<String,List<String>> hmHeaderInfo=(Map<String,List<String>>)request.getAttribute("hmHeaderInfo");
    
    //Map<String,Map<String,Map<String,List<String>>>> hmCandidateRating=(Map<String,Map<String,Map<String,List<String>>>>)request.getAttribute("hmCandidateRating");
    Map<String,String>	hmCandidateEducation=(Map<String,String>)request.getAttribute("hmCandidateEducation");
    Map<String,String>  hmCandidateSkill=(Map<String,String>)request.getAttribute("hmCandidateSkill");
    Map<String,String>  hmCandidateExperience=(Map<String,String>)request.getAttribute("hmCandidateExperience");
    
    Map<String, Map<String, String>> hmcandiStarRecruitwise = (Map<String, Map<String, String>>)request.getAttribute("hmcandiStarRecruitwise");
    if(hmcandiStarRecruitwise == null) hmcandiStarRecruitwise = new HashMap<String, Map<String, String>>();
    
    Map<String, String> hmCommentsHR = (Map) request.getAttribute("hmCommentsHR");
    Map<String, Map<String, String>> hmPanelRatingAndComments = (Map<String, Map<String, String>>) request.getAttribute("hmPanelRatingAndComments");
    Map<String, Map<String, Map<String, String>>> hmRecruitWiseRoundId = (Map<String, Map<String, Map<String, String>>>) request.getAttribute("hmRecruitWiseRoundId");
    Map<String, String> hmEmpName = (Map<String, String>) request.getAttribute("hmEmpName");
    
    Map<String,Map<String,List<String>>> hmAcceptedName=(Map<String,Map<String,List<String>>>)request.getAttribute("hmAcceptedName");
    System.out.println("offers.jsp/211--hmAcceptedName="+hmAcceptedName);
    Map<String,Map<String,List<String>>> hmOfferedName=(Map<String,Map<String,List<String>>>)request.getAttribute("hmOfferedName");
    Map<String,Map<String,List<String>>> hmRejectedName=(Map<String,Map<String,List<String>>>)request.getAttribute("hmRejectedName");
    
    Map<String, String> hmJobStatus = (Map<String, String>)request.getAttribute("hmJobStatus");
    if(hmJobStatus == null) hmJobStatus = new HashMap<String, String>();
    
    Map<String, String> hmCandToEmp = (Map<String, String>) request.getAttribute("hmCandToEmp");
    if(hmCandToEmp == null) hmCandToEmp = new HashMap<String, String>();
    
    Map<String, String> hmCandiImage = (Map<String, String>)request.getAttribute("hmCandiImage");
    if(hmCandiImage == null) hmCandiImage = new HashMap<String, String>();
    
    Map<String, String> hmRoundAssessment = (Map<String, String>) request.getAttribute("hmRoundAssessment");
    if(hmRoundAssessment == null) hmRoundAssessment = new HashMap<String, String>();
    
    Map<String, String> hmAssessRateRecruitAndRoundIdWise = (Map<String, String>) request.getAttribute("hmAssessRateRecruitAndRoundIdWise");
    if(hmAssessRateRecruitAndRoundIdWise == null) hmAssessRateRecruitAndRoundIdWise = new HashMap<String, String>();
    
    Map<String, Map<String, Map<String, String>>> hmSource = (Map<String, Map<String, Map<String, String>>>) request.getAttribute("hmSource");
    if(hmSource == null) hmSource = new HashMap<String, Map<String,Map<String,String>>>();
    
    String proCount = (String)request.getAttribute("proCount");
    String strVm = (String)request.getAttribute("strVm");
    
    //===start parvez date: 20-10-2021===
    	List<String> onBoardedEmpList = (List<String>) request.getAttribute("onBoardedEmpList");
    //===end parvez date: 20-10-2021===
    
    %>			

                <div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
                        
                        <%
                            Iterator<String> itr=hmHeaderInfo.keySet().iterator();
                            
                            while(itr.hasNext()){	
                            	String recruitId=itr.next();
                            
                            	Map<String, String> hmCandiStars = hmcandiStarRecruitwise.get(recruitId);
                            	if(hmCandiStars == null) hmCandiStars = new HashMap<String, String>();
                            	
                            	List<String> alHeader=hmHeaderInfo.get(recruitId);
                            
                            	String priorityClass = "";
                            	if (uF.parseToInt(alHeader.get(2)) == 1) {
                            		priorityClass ="high";
                            	}else if (uF.parseToInt(alHeader.get(2)) == 2) {
                            		priorityClass ="medium";
                            	}else{
                            		priorityClass ="low";
                            	}
                            	
                            	boolean closeFlag = false;
                            	if(uF.parseToBoolean(hmJobStatus.get(recruitId))){ 
                            		closeFlag = true;
                            	}
                            	
                            	Map<String,List<String>> acceptedNameMap=(Map<String,List<String>>)hmAcceptedName.get(recruitId);
                            	if(acceptedNameMap==null) acceptedNameMap=new HashMap<String,List<String>>();
                            	
                            	Map<String,List<String>>  rejectedNameMap=(Map<String,List<String>>)hmRejectedName.get(recruitId);
                            	if(rejectedNameMap==null) rejectedNameMap=new HashMap<String,List<String>>();
                            	
                            	Map<String,List<String>>  offeredNameMap=(Map<String,List<String>>)hmOfferedName.get(recruitId);
                            	if(offeredNameMap==null) offeredNameMap=new HashMap<String,List<String>>();
                            	
                            	Map<String, Map<String, String>> hmCandidate = hmSource.get(recruitId);
                            	if(hmCandidate == null) hmCandidate = new HashMap<String, Map<String,String>>();
                            %>
                        <!-- <div class="dashboard_linksholder " style="float: none; width: 98%; line-height:30px;"> -->
                        <%-- <div style="margin: 10px 0px 0px 0px; float: left; width: 100%; line-height:30px;">
                            <div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE; margin-top: 10px; margin: 10px 0px 0px 0px">
                                <div class="box-header with-border" style="padding-top: 5px;padding-bottom: 5px;">
                                   <h3 class="box-title" style="width: 96%; font-size: 14px;">
                                        <div class="heading_dash <%=priorityClass %>" style="float:left;width:100%;">
                                            <div style="width:90%; float:left;">
                                                Accepted(<%=uF.showData(""+acceptedNameMap.keySet().size(),"") %>),
                                                Rejected(<%=uF.showData(""+rejectedNameMap.keySet().size(),"") %>), 
                                                Offered(<%=uF.showData(""+offeredNameMap.keySet().size(),"") %>),
                                                Required(<%=uF.showData(alHeader.get(1),"")%>), 
                                                Shortfall(<%=(uF.parseToInt(uF.showData(alHeader.get(1),""))-uF.parseToInt(uF.showData(""+acceptedNameMap.keySet().size(),"")) > 0) ? uF.parseToInt(uF.showData(alHeader.get(1),""))-uF.parseToInt(uF.showData(""+acceptedNameMap.keySet().size(),"")) : "0" %>) 
                                            </div>
                                        </div>
                                    </h3>
                                    
                                    <!-- <div class="box-tools pull-right">
                                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
                                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                                    </div> -->
                                </div>
                                <!-- /.box-header -->
                                
                                <!-- /.box-body -->
                            </div>
                        </div> --%>
                         			<div class="box-tools pull-right">
				  						<span class="label label-warning">Accepted(<%=uF.showData(""+acceptedNameMap.keySet().size(),"") %>)</span>		
				  						<span class="label label-danger">Rejected(<%=uF.showData(""+rejectedNameMap.keySet().size(),"") %>)</span>
				  						<span class="label label-info">Offered(<%=uF.showData(""+offeredNameMap.keySet().size(),"") %>)</span>
				  						<span class="label label-success">Required(<%=uF.showData(alHeader.get(1),"")%>)</span>
										<span class="label label-success">Shortfall(<%=(uF.parseToInt(uF.showData(alHeader.get(1),""))-uF.parseToInt(uF.showData(""+acceptedNameMap.keySet().size(),"")) > 0) ? uF.parseToInt(uF.showData(alHeader.get(1),""))-uF.parseToInt(uF.showData(""+acceptedNameMap.keySet().size(),"")) : "0" %>)</span>
						            </div>
                        			<div class="offerContent" style="padding: 20px; height:auto;">
                                        <div class="attendance">
                                        
                                            <h4 style="line-height: 20px;">Accepted: (<%=uF.showData(""+acceptedNameMap.keySet().size(), "0")%>)</h4>
                                            <table width="100%" cellspacing="0" cellpadding="2" style="margin:0px;" class="table table-bordered">
                                                <tbody>
                                                    <tr class="darktable">
                                                        <td style="text-align: center;">Name</td>
                                                        <td style="text-align: center;">Experience</td>
                                                        <td style="text-align: center;">Education</td>
                                                        <td style="text-align: center;">Skills</td>
                                                        <td style="text-align: center;">Panel Rating</td>
                                                        <td style="text-align: center;">HR Report</td>
                                                        <td style="text-align: center;">Offer</td>
                                                        <td style="text-align: center;">Date</td>
                                                    </tr>
                                                    <%   	
                                                        Iterator<String> itr1=acceptedNameMap.keySet().iterator();
                                                        /* int x = 1; */
                                                            while(itr1.hasNext()){
                                                          	 /* if(x == 11 && uF.parseToInt(strVm) == 0){
                                                        	break;
                                                         } */
                                                          	 String candidateID=itr1.next();
                                                        		 List<String> alInner=acceptedNameMap.get(candidateID);
                                                        		 
                                                        		String strStars = hmCandiStars.get(candidateID);
                                                        		Map<String, String> hmSourceDetails = hmCandidate.get(candidateID);
                                                        if(hmSourceDetails == null) hmSourceDetails = new HashMap<String, String>();
                                                        %>
                                                    <tr class="lighttable">
                                                        <td style="text-align: left;">
                                                            <div style="float: left; margin: 2px 10px 0px 0px">
                                                                <!-- border: 1px solid #000; -->
                                                                <%if(docRetriveLocation == null) { %>
                                                                <img height="40" width="40" border="0" class="lazy img-circle" id="profilecontainerimg<%=recruitId+"_"+candidateID %>" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + hmCandiImage.get(candidateID) %>" />
                                                                <%} else { %>
                                                                <img height="40" width="40" border="0" class="lazy img-circle" id="profilecontainerimg<%=recruitId+"_"+candidateID %>" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_CANDIDATE+"/"+IConstants.I_IMAGE+"/"+candidateID+"/"+IConstants.I_100x100+"/"+hmCandiImage.get(candidateID)%>" />
                                                                <%} %> 
                                                            </div>
                                                            <div style="float: left;">
                                                                <%-- <%if(!closeFlag){ %>
                                                                <!-- start parvez on 09-07-2021==== -->
                                                                <a href="javascript:void(0);" onclick="openCandidateProfilePopup('<%=candidateID %>','<%=recruitId %>','accept')">  <%=alInner.get(1) %> </a>
                                                                <!-- end parvez on 09-07-2021==== -->
                                                                <%} else { %>
                                                                <%=alInner.get(1) %>
                                                                <%} %> --%>
                                                           
                                                           <!-- start parvez on 21-10-2021==== -->
                                                                <a href="javascript:void(0);" onclick="openCandidateProfilePopup('<%=candidateID %>','<%=recruitId %>','accept')">  <%=alInner.get(1) %> </a>
                                                           <!-- end parvez on 21-10-2021==== -->
                                                                <br/>
                                                                <div style="line-height: 16px;">
                                                                    <span id="Reting<%=candidateID %>" style="margin-right: 5px; line-height: 18px;"><%=strStars %>/5</span>
                                                                    <span id="starPrimaryS<%=recruitId+"_"+candidateID %>" style="margin-left: 5px; line-height: 12px;"></span>
                                                                </div>
                                                                <script type="text/javascript">
	                                                                $('#starPrimaryS'+'<%=recruitId+"_"+candidateID %>').raty({
	                                                                      readOnly: true,
	                                                                      start:	<%=strStars %> ,
	                                                                      half: true,
	                                                                      targetType: 'number'
	                                                                });
	                                                            </script>
                                                                <div style="font-size: 11px; line-height: 22px;"><strong style="color: gray;">Source:</strong>&nbsp;<%=uF.showData(hmSourceDetails.get("SOURCE_TYPE"),"-") %></div>
                                                                <div style="font-size: 11px; line-height: 15px;"><strong style="color: gray;">Source Name:</strong>&nbsp;<%=uF.showData(hmSourceDetails.get("SOURCE_NAME"),"-") %></div>
                                                            </div>
                                                        </td>
                                                        <td style="text-align: left;"> <%=uF.showData(hmCandidateExperience.get(candidateID),"") %></td>
                                                        <td style="text-align: left;"> <%=uF.showData(hmCandidateEducation.get(candidateID),"") %></td>
                                                        <td style="text-align: left;"> <%=uF.showData(hmCandidateSkill.get(candidateID),"") %></td>
                                                        <%
                                                            //System.out.println("Accepted  hmPanelRatingAndComments ----> " + hmPanelRatingAndComments);
                                                            	Map<String, String> hmPanelRatingCommentMap = new HashMap<String, String>();
                                                            	if(hmPanelRatingAndComments != null) {
                                                            		hmPanelRatingCommentMap = hmPanelRatingAndComments.get(recruitId +"_"+ candidateID);
                                                            	}
                                                            	//System.out.println("Accepted  hmPanelRatingCommentMap ----> " + hmPanelRatingCommentMap);
                                                            	 Map<String, Map<String, String>> hmRoundIds = new HashMap<String, Map<String, String>>();
                                                            	if(hmRecruitWiseRoundId != null) {
                                                            		hmRoundIds = hmRecruitWiseRoundId.get(recruitId);
                                                            	}
                                                            	if(hmRoundIds == null) {
                                                            		hmRoundIds = new HashMap<String, Map<String, String>>();
                                                            	}
                                                            %>
                                                        <td valign="top" style="text-align: left;">
                                                            <%
                                                                if(hmRoundIds != null && !hmRoundIds.isEmpty()) {
                                                                int count = 0;
                                                                Set keys=hmRoundIds.keySet();
                                                                Iterator it=keys.iterator();
                                                                while(it.hasNext()) {	
                                                                	String roundId = (String)it.next();
                                                                	Map<String, String> panelUserIds = hmRoundIds.get(roundId);
                                                                %>
                                                            <div style="float: left;">
                                                                <%-- <%if(!closeFlag){ %>
                                                                <a href="javascript:void(0)" style="line-height: 10px;" onclick="panelcomment(<%=roundId%>,<%=candidateID%>,<%=recruitId%>);">Round <%=roundId %></a>
                                                                <% if(uF.parseToInt(hmRoundAssessment.get(recruitId+"_"+roundId+"_ID")) > 0) { %>
                                                                <div>
                                                                    <span style="color: gray;">Assessment:</span> &nbsp;
                                                                    <span>
                                                                    <a href="javascript:void(0);" style="font-weight: normal;" onclick="viewAssessmentDetail('<%=hmRoundAssessment.get(recruitId+"_"+roundId+"_ID") %>', '<%=uF.showData(hmRoundAssessment.get(recruitId+"_"+roundId+"_NAME"), "") %>')"><%=uF.showData(hmRoundAssessment.get(recruitId+"_"+roundId+"_NAME"), "") %></a>
                                                                    </span>
                                                                    <% if(uF.parseToDouble(hmAssessRateRecruitAndRoundIdWise.get(recruitId+"_"+candidateID+"_"+ roundId+"_"+hmRoundAssessment.get(recruitId+"_"+roundId+"_ID"))) > 0) { %>
                                                                    <span id="assessScoreCard_<%=recruitId %><%=candidateID %>_<%=roundId %>" style="margin-left: 7px;">
                                                                    <a href="javascript:void(0);" onclick="viewAssessmentScoreSummary('<%=hmRoundAssessment.get(recruitId+"_"+roundId+"_ID") %>', '<%=candidateID%>', '<%=recruitId %>', '<%=roundId %>');"><%=uF.showData(hmAssessRateRecruitAndRoundIdWise.get(recruitId+"_"+candidateID+"_"+ roundId+"_"+hmRoundAssessment.get(recruitId+"_"+roundId+"_ID")), "NA") %>%</a>
                                                                    </span>
                                                                    <% } %>
                                                                </div>
                                                                <% } %>
                                                                <%} else { %>
                                                                Round <%=roundId %>
                                                                <% if(uF.parseToInt(hmRoundAssessment.get(recruitId+"_"+roundId+"_ID")) > 0) { %>
                                                                <div>
                                                                    <span style="color: gray;">Assessment:</span> &nbsp;
                                                                    <span>
                                                                    <%=uF.showData(hmRoundAssessment.get(recruitId+"_"+roundId+"_NAME"), "") %>
                                                                    </span>
                                                                    <% if(uF.parseToDouble(hmAssessRateRecruitAndRoundIdWise.get(recruitId+"_"+candidateID+"_"+ roundId+"_"+hmRoundAssessment.get(recruitId+"_"+roundId+"_ID"))) > 0) { %>
                                                                    <span id="assessScoreCard_<%=recruitId %><%=candidateID %>_<%=roundId %>" style="margin-left: 7px;">
                                                                    <%=uF.showData(hmAssessRateRecruitAndRoundIdWise.get(recruitId+"_"+candidateID+"_"+ roundId+"_"+hmRoundAssessment.get(recruitId+"_"+roundId+"_ID")), "NA") %>%
                                                                    </span>
                                                                    <% } %>
                                                                </div>
                                                                <%} %>	
                                                                <%} %> --%> 
                                                                
                                                           <!-- start parvez on 21-10-2021==== -->
                                                                <a href="javascript:void(0)" style="line-height: 10px;" onclick="panelcomment(<%=roundId%>,<%=candidateID%>,<%=recruitId%>);">Round <%=roundId %></a>
                                                                <% if(uF.parseToInt(hmRoundAssessment.get(recruitId+"_"+roundId+"_ID")) > 0) { %>
                                                                <div>
                                                                    <span style="color: gray;">Assessment:</span> &nbsp;
                                                                    <span>
                                                                    <a href="javascript:void(0);" style="font-weight: normal;" onclick="viewAssessmentDetail('<%=hmRoundAssessment.get(recruitId+"_"+roundId+"_ID") %>', '<%=uF.showData(hmRoundAssessment.get(recruitId+"_"+roundId+"_NAME"), "") %>')"><%=uF.showData(hmRoundAssessment.get(recruitId+"_"+roundId+"_NAME"), "") %></a>
                                                                    </span>
                                                                    <% if(uF.parseToDouble(hmAssessRateRecruitAndRoundIdWise.get(recruitId+"_"+candidateID+"_"+ roundId+"_"+hmRoundAssessment.get(recruitId+"_"+roundId+"_ID"))) > 0) { %>
                                                                    <span id="assessScoreCard_<%=recruitId %><%=candidateID %>_<%=roundId %>" style="margin-left: 7px;">
                                                                    <a href="javascript:void(0);" onclick="viewAssessmentScoreSummary('<%=hmRoundAssessment.get(recruitId+"_"+roundId+"_ID") %>', '<%=candidateID%>', '<%=recruitId %>', '<%=roundId %>');"><%=uF.showData(hmAssessRateRecruitAndRoundIdWise.get(recruitId+"_"+candidateID+"_"+ roundId+"_"+hmRoundAssessment.get(recruitId+"_"+roundId+"_ID")), "NA") %>%</a>
                                                                    </span>
                                                                    <% } %>
                                                                </div>
                                                                <% } %>
                                                          <!-- end parvez on 21-10-2021==== -->
                                                                
                                                            </div>
                                                            <%
                                                                Set keysEmpID = panelUserIds.keySet();
                                                                Iterator itEmpID = keysEmpID.iterator();
                                                                while(itEmpID.hasNext()) {	
                                                                	String panelEmpId = (String)itEmpID.next();
                                                                	//System.out.println("Accepted  hmPanelRatingCommentMap.get(roundId + panelEmpId + _RATING) ----> " + hmPanelRatingCommentMap.get(roundId +"_" + panelEmpId +"_RATING"));
                                                                	if (count > 0) {
                                                                %> <br style="line-height: 0px;"> <% } %>
                                                            
                                                            <div style="float: left; width: 100%;">
                                                                <div style="float: left; margin-right: 10px;"> <%=hmEmpName.get(panelEmpId) %> </div>
                                                                <div style="float: left;" id="starPrimary<%=recruitId + roundId + panelEmpId + candidateID%>" style="line-height: 20px;"></div>
                                                            </div>
                                                            <script type="text/javascript">
                                                                $('#starPrimary<%=recruitId + roundId + panelEmpId + candidateID%>').raty({
                                                                    readOnly: true,
                                                                    start: <%=uF.parseToDouble(hmPanelRatingCommentMap != null ? hmPanelRatingCommentMap.get(roundId +"_" + panelEmpId +"_RATING") : "")%> ,
                                                                    half: true,
                                                                    targetType: 'number'
                                                           		});
                                                                                         
                                                            </script>
                                                            <%
                                                                count++;
                                                                } 			
                                                                }
                                                                 }
                                                                %>
                                                        </td>
                                                        <td style="text-align: left;">
                                                            <%=uF.showData(hmCommentsHR.get(recruitId +"_"+ candidateID), "")%>
                                                        </td>
                                                        <td style="vertical-align: text-top;">
                                                            <% if (strUserType != null && (strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.RECRUITER))) { %>
		                                                        <div id="downloadOfferLatterSpan_<%=recruitId +"_"+ candidateID %>" style="float: left;width:97%;margin:2px 0px 0px 0px;">
	                                                                <a href="javascript:void(0);" onclick="downloadOfferLetter('<%=candidateID%>', '<%=recruitId %>');" >Download offer letter</a>
	                                                            </div>
	                                                            <div>
	                                                            <!-- ===start parvez date: 20-10-2021=== -->
		                                                        <%-- <% if(!closeFlag){ %>  
		                                                             
			                                                            	<a href="javascript: void(0);" onclick="<%if(hmCandToEmp.containsKey(candidateID.trim())){%>alert('<%=hmCandToEmp.get(candidateID.trim()) %>');<%} else {%>offerRejectAfterAcceptPopup('<%=candidateID %>','<%=recruitId %>');<%}%>"><i class="fa fa-play" aria-hidden="true"></i>Offer Back Out</a><br> 
			                                                         
			                                                    <% } else { %>
			                                                            <i class="fa fa-play" aria-hidden="true"></i>Candidate acceptance &nbsp;&nbsp;&nbsp;
			                                                            
		                                                        <% } %> --%>
		                                                        
		                                                   <!-- ===start parvez date: 20-10-2021=== -->
		                                                       <% if(!onBoardedEmpList.contains(alInner.get(0))){ %>
			                                                           <a href="javascript: void(0);" onclick="<%if(hmCandToEmp.containsKey(candidateID.trim())){%>alert('<%=hmCandToEmp.get(candidateID.trim()) %>');<%} else {%>offerRejectAfterAcceptPopup('<%=candidateID %>','<%=recruitId %>');<%}%>"><i class="fa fa-play" aria-hidden="true"></i>Offer Back Out</a><br>
			                                                    <% } %>
		                                                   <!-- ===end parvez date: 20-10-2021=== -->
                                                            	</div>
                                                            <% } %>
                                                            <div style="float:left;width:97%;"><%=alInner.get(2) %></div>
                                                        </td>
                                                        <td style="text-align: left;"><%=alInner.get(3) %></td>
                                                    </tr>
                                                    <%
                                                        /* x++; */
                                                        } %>
                                                    <% if(acceptedNameMap.keySet().size()==0) { %>
                                                    <tr class="lighttable">
                                                        <td colspan="8">
                                                            <div class="nodata msg"> <span>No Offer Accepted</span> </div>
                                                        </td>
                                                        <%-- <%	} else {
                                                            	if(x == 11) {
                                                            %>
                                                    <tr class="lighttable">
                                                        <td colspan="8" style="text-align: right;"><a style="padding-right:5px" href="Offers.action?strVm=1&strDashboardRequest=<%=recruitId%>" >view more....</a></td>
                                                    </tr>
                                                    <% }
                                                        }
                                                        %> --%>
                                                   <% } %>
                                                </tbody>
                                            </table>
                                            <h4 style="line-height: 20px;margin-top: 40px;">Rejected: (<%=uF.showData(""+rejectedNameMap.keySet().size(), "0")%>)</h4>
                                            <table width="100%" cellspacing="0" cellpadding="2" style="margin:0px;" class="table table-bordered">
                                                <tbody>
                                                    <tr class="darktable">
                                                        <td style="text-align: center;">Name</td>
                                                        <td style="text-align: center;">Experience</td>
                                                        <td style="text-align: center;">Education</td>
                                                        <td style="text-align: center;">Skills</td>
                                                        <td style="text-align: center;">Panel Rating</td>
                                                        <td style="text-align: center;">HR Report</td>
                                                        <td style="text-align: center;">Offer</td>
                                                        <td style="text-align: center;">Date</td>
                                                    </tr>
                                                    <%   	
                                                        Iterator<String> itrRejected=rejectedNameMap.keySet().iterator();
                                                        int y = 1;	
                                                         while(itrRejected.hasNext()){
                                                         	/* if(y == 11 && uF.parseToInt(strVm) == 0){
                                                        break;
                                                        } */
                                                         	String candidateID=itrRejected.next();
                                                           	List<String> alInner=rejectedNameMap.get(candidateID);
                                                           	
                                                           	String strStars = hmCandiStars.get(candidateID);
                                                           	Map<String, String> hmSourceDetails = hmCandidate.get(candidateID);
                                                        if(hmSourceDetails == null) hmSourceDetails = new HashMap<String, String>();
                                                        %>
                                                    <tr class="lighttable">
                                                        <td style="text-align: left;">
                                                            <div style="float: left; margin: 2px 10px 0px 0px">
                                                                <!-- border: 1px solid #000; -->
                                                                <%if(docRetriveLocation == null) { %>
                                                                <img height="40" width="40" border="0" class="lazy img-circle" id="profilecontainerimg<%=recruitId+"_"+candidateID %>" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + hmCandiImage.get(candidateID) %>" />
                                                                <%} else { %>
                                                                <img height="40" width="40" border="0" class="lazy img-circle" id="profilecontainerimg<%=recruitId+"_"+candidateID %>" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_CANDIDATE+"/"+IConstants.I_IMAGE+"/"+candidateID+"/"+IConstants.I_100x100+"/"+hmCandiImage.get(candidateID)%>" />
                                                                <%} %> 
                                                            </div>
                                                            <div style="float: left;">
                                                                <%-- <%if(!closeFlag){ %>
                                                                <!-- start parvez on 09-07-2021==== -->
                                                                <a href="javascript:void(0);" onclick="openCandidateProfilePopup('<%=candidateID %>','<%=recruitId %>','reject')">  <%=alInner.get(1) %> </a>
                                                                <!-- end parvez on 09-07-2021==== -->
                                                                <%} else { %>
                                                                <%=alInner.get(1) %>
                                                                <%} %> --%>
                                                                
                                                            <!-- start parvez on 21-10-2021==== -->
                                                                <a href="javascript:void(0);" onclick="openCandidateProfilePopup('<%=candidateID %>','<%=recruitId %>','reject')">  <%=alInner.get(1) %> </a>
                                                            <!-- end parvez on 21-10-2021==== -->
                                                                <br/>
                                                                <div style="line-height: 16px;">
                                                                    <span id="Reting<%=candidateID %>" style="margin-right: 5px; line-height: 18px;"><%=strStars %>/5</span>
                                                                    <span id="starPrimaryF<%=recruitId+"_"+candidateID %>" style="margin-left: 5px; line-height: 12px;"></span>
                                                                </div>
                                                                <script type="text/javascript">
	                                                                $('#starPrimaryF'+'<%=recruitId+"_"+candidateID %>').raty({
	                                                                      readOnly: true,
	                                                                      start:	<%=strStars %> ,
	                                                                      half: true,
	                                                                      targetType: 'number'
	                                                                });
	                                                            </script>
                                                                <div style="font-size: 11px; line-height: 22px;"><strong style="color: gray;">Source:</strong>&nbsp;<%=uF.showData(hmSourceDetails.get("SOURCE_TYPE"),"-") %></div>
                                                                <div style="font-size: 11px; line-height: 15px;"><strong style="color: gray;">Source Name:</strong>&nbsp;<%=uF.showData(hmSourceDetails.get("SOURCE_NAME"),"-") %></div>
                                                            </div>
                                                        </td>
                                                        <td style="text-align: left;"><%=uF.showData(hmCandidateExperience.get(candidateID),"") %></td>
                                                        <td style="text-align: left;"><%=uF.showData(hmCandidateEducation.get(candidateID),"") %></td>
                                                        <td style="text-align: left;"><%=uF.showData(hmCandidateSkill.get(candidateID),"") %></td>
                                                        <%
                                                            //System.out.println("Rejected  hmPanelRatingAndComments ----> " + hmPanelRatingAndComments);
                                                            	Map<String, String> hmPanelRatingCommentMap = new HashMap<String, String>();
                                                            	if(hmPanelRatingAndComments != null) {
                                                            		hmPanelRatingCommentMap = hmPanelRatingAndComments.get(recruitId +"_"+ candidateID);
                                                            	}
                                                            	//System.out.println("Rejected  hmPanelRatingCommentMap ----> " + hmPanelRatingCommentMap);
                                                            	 Map<String, Map<String, String>> hmRoundIds = new HashMap<String, Map<String, String>>();
                                                            	if(hmRecruitWiseRoundId != null) {
                                                            		hmRoundIds = hmRecruitWiseRoundId.get(recruitId);
                                                            	} 
                                                            %>
                                                        <td valign="top" style="text-align: left;">
                                                            <%
                                                                if(hmRoundIds != null && !hmRoundIds.isEmpty()) {
                                                                	int count = 0;
                                                                	Set keys=hmRoundIds.keySet();
                                                                	Iterator it=keys.iterator();
                                                                	while(it.hasNext()) {
                                                                		String roundId = (String)it.next();
                                                                		Map<String, String> panelUserIds = hmRoundIds.get(roundId);
                                                                %>
                                                            <div style="float: left;">
                                                               <%--  <%if(!closeFlag){ %>
                                                                <a href="javascript:void(0);" style="line-height: 10px;" onclick="panelcomment(<%=roundId%>,<%=candidateID%>,<%=recruitId%>);">Round <%=roundId %> </a>
                                                                <% if(uF.parseToInt(hmRoundAssessment.get(recruitId+"_"+roundId+"_ID")) > 0) { %>
                                                                <div>
                                                                    <span style="color: gray;">Assessment:</span> &nbsp;
                                                                    <span>
                                                                    <a href="javascript:void(0);" style="font-weight: normal;" onclick="viewAssessmentDetail('<%=hmRoundAssessment.get(recruitId+"_"+roundId+"_ID") %>', '<%=uF.showData(hmRoundAssessment.get(recruitId+"_"+roundId+"_NAME"), "") %>')"><%=uF.showData(hmRoundAssessment.get(recruitId+"_"+roundId+"_NAME"), "") %></a>
                                                                    </span>
                                                                    <% if(uF.parseToDouble(hmAssessRateRecruitAndRoundIdWise.get(recruitId+"_"+candidateID+"_"+ roundId+"_"+hmRoundAssessment.get(recruitId+"_"+roundId+"_ID"))) > 0) { %>
                                                                    <span id="assessScoreCard_<%=recruitId %><%=candidateID %>_<%=roundId %>" style="margin-left: 7px;">
                                                                    <a href="javascript:void(0);" onclick="viewAssessmentScoreSummary('<%=hmRoundAssessment.get(recruitId+"_"+roundId+"_ID") %>', '<%=candidateID%>', '<%=recruitId %>', '<%=roundId %>');"><%=uF.showData(hmAssessRateRecruitAndRoundIdWise.get(recruitId+"_"+candidateID+"_"+ roundId+"_"+hmRoundAssessment.get(recruitId+"_"+roundId+"_ID")), "NA") %>%</a>
                                                                    </span>
                                                                    <% } %>
                                                                </div>
                                                                <% } %>
                                                                <%} else { %>
                                                                Round <%=roundId %>
                                                                <% if(uF.parseToInt(hmRoundAssessment.get(recruitId+"_"+roundId+"_ID")) > 0) { %>
                                                                <div>
                                                                    <span style="color: gray;">Assessment:</span> &nbsp;
                                                                    <span>
                                                                    <%=uF.showData(hmRoundAssessment.get(recruitId+"_"+roundId+"_NAME"), "") %>
                                                                    </span>
                                                                    <% if(uF.parseToDouble(hmAssessRateRecruitAndRoundIdWise.get(recruitId+"_"+candidateID+"_"+ roundId+"_"+hmRoundAssessment.get(recruitId+"_"+roundId+"_ID"))) > 0) { %>
                                                                    <span id="assessScoreCard_<%=recruitId %><%=candidateID %>_<%=roundId %>" style="margin-left: 7px;">
                                                                    <%=uF.showData(hmAssessRateRecruitAndRoundIdWise.get(recruitId+"_"+candidateID+"_"+ roundId+"_"+hmRoundAssessment.get(recruitId+"_"+roundId+"_ID")), "NA") %>%
                                                                    </span>
                                                                    <% } %>
                                                                </div>
                                                                <%} %>
                                                                <%} %> --%>
                                                                
                                                        <!-- ===start parvez date: 20-10-2021=== -->
                                                                <a href="javascript:void(0);" style="line-height: 10px;" onclick="panelcomment(<%=roundId%>,<%=candidateID%>,<%=recruitId%>);">Round <%=roundId %> </a>
                                                                <% if(uF.parseToInt(hmRoundAssessment.get(recruitId+"_"+roundId+"_ID")) > 0) { %>
                                                                <div>
                                                                    <span style="color: gray;">Assessment:</span> &nbsp;
                                                                    <span>
                                                                    <a href="javascript:void(0);" style="font-weight: normal;" onclick="viewAssessmentDetail('<%=hmRoundAssessment.get(recruitId+"_"+roundId+"_ID") %>', '<%=uF.showData(hmRoundAssessment.get(recruitId+"_"+roundId+"_NAME"), "") %>')"><%=uF.showData(hmRoundAssessment.get(recruitId+"_"+roundId+"_NAME"), "") %></a>
                                                                    </span>
                                                                    <% if(uF.parseToDouble(hmAssessRateRecruitAndRoundIdWise.get(recruitId+"_"+candidateID+"_"+ roundId+"_"+hmRoundAssessment.get(recruitId+"_"+roundId+"_ID"))) > 0) { %>
                                                                    <span id="assessScoreCard_<%=recruitId %><%=candidateID %>_<%=roundId %>" style="margin-left: 7px;">
                                                                    <a href="javascript:void(0);" onclick="viewAssessmentScoreSummary('<%=hmRoundAssessment.get(recruitId+"_"+roundId+"_ID") %>', '<%=candidateID%>', '<%=recruitId %>', '<%=roundId %>');"><%=uF.showData(hmAssessRateRecruitAndRoundIdWise.get(recruitId+"_"+candidateID+"_"+ roundId+"_"+hmRoundAssessment.get(recruitId+"_"+roundId+"_ID")), "NA") %>%</a>
                                                                    </span>
                                                                    <% } %>
                                                                </div>
                                                                <% } %>
                                                       <!-- ===end parvez date: 20-10-2021=== -->
                                                                
                                                            </div>
                                                            <%
                                                                Set keysEmpID = panelUserIds.keySet();
                                                                Iterator itEmpID = keysEmpID.iterator();
                                                                while(itEmpID.hasNext()) {	
                                                                	String panelEmpId = (String)itEmpID.next();	
                                                                	//System.out.println("Rejected  hmPanelRatingCommentMap.get(roundId + panelEmpId + _RATING) ----> " + hmPanelRatingCommentMap.get(roundId +"_" + panelEmpId +"_RATING"));
                                                                	if (count > 0) {
                                                                %> <br style="line-height: 0px;"> <% } %>
                                                           
                                                            <div style="float: left; width: 100%;">
                                                                <div style="float: left; margin-right: 10px;"> <%=hmEmpName.get(panelEmpId) %> </div>
                                                                <div style="float: left;" id="starPrimary<%=recruitId + roundId + panelEmpId + candidateID%>" style="line-height: 20px;"></div>
                                                            </div>
                                                            <script type="text/javascript">
                                                                $('#starPrimary<%=recruitId + roundId + panelEmpId + candidateID%>').raty({
							                                       readOnly: true,
							                                       start: <%=uF.parseToDouble(hmPanelRatingCommentMap != null ? hmPanelRatingCommentMap.get(roundId +"_" + panelEmpId +"_RATING") : "")%> ,
							                                       half: true,
							                                       targetType: 'number'
							                              		});
                                                                                         
                                                            </script>
                                                            <%
                                                                count++;
                                                                }	 			
                                                                }
                                                                }
                                                                %>
                                                        </td>
                                                        <td style="text-align: left;">
                                                            <%=uF.showData(hmCommentsHR.get(recruitId +"_"+ candidateID), "")%>		
                                                        </td>
                                                        <td width="22%" style="vertical-align: text-top; padding-right: 10px;">
	                                                        	<% if (strUserType != null && (strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.RECRUITER))) { %>
	                                                  
	                                                  				<%-- <%if(!closeFlag){ %>
			                                                            <div style="float:left;width:97%;">
			                                                       			<a href="javascript: void(0);" onclick="salaryRenegotiatePopup('<%=candidateID %>','<%=recruitId %>','reject');">Re-negotiation</a> &nbsp;&nbsp;&nbsp;
			                                                            </div>
			                                                            <%} else {%>
			                                                            <div style="float:left;width:97%;">
			                                                                Re-negotiation &nbsp;&nbsp;&nbsp;
			                                                            </div>
			                                                            
		                                                            <%} %> --%>
		                                                            
		                                                      <!-- ===start parvez date: 20-10-2021=== -->
		                                                            <div style="float:left;width:97%;">
			                                                       		<% if(!onBoardedEmpList.contains(alInner.get(0))){ %>
			                                                               <a href="javascript: void(0);" onclick="salaryRenegotiatePopup('<%=candidateID %>','<%=recruitId %>','reject');">Re-negotiation</a> &nbsp;&nbsp;&nbsp;
			                                                            <%} %>
			                                                        </div>
			                                                 <!-- ===end parvez date: 20-10-2021=== -->
		                                             
		                                                            <div id="downloadOfferLatterSpan_<%=recruitId +"_"+ candidateID %>" style="float: left;width:97%;margin:2px 0px 0px 0px;">
		                                                                <%-- <input type="button" value="download" class="btn btn-primary" onclick="CandiOfferLetterPreview.action?candidateId=<%=candidateID%>&recruitId=<%=recruitId %>" /> --%>
		                                                                <a href="javascript:void(0);" onclick="downloadOfferLetter('<%=candidateID%>', '<%=recruitId %>');" >Download offer letter</a>
		                                                                <%-- <a href="CandiOfferLetterPreview.action?candidateId=<%=candidateID%>&recruitId=<%=recruitId %>">Download offer letter</a> --%>
		                                                            </div>
	                                                            <% } %>
	                                                            <div style="float:left;width:97%;"> <%=alInner.get(2) %></div>
                                                        </td>
                                                        <td style="text-align: left;"><%=alInner.get(3) %></td>
                                                    </tr>
                                                    <%//y++;
                                                        } %>
                                                    <%if(rejectedNameMap.keySet().size()==0){ %>
                                                    <tr class="lighttable">
                                                        <td colspan="8">
                                                            <div class="nodata msg">
                                                                <span>No Offer Rejected</span>
                                                            </div>
                                                        </td>
                                                       <%--  <%
                                                            } else{
                                                            	if(y == 11){
                                                            %>
                                                    <tr class="lighttable">
                                                        <td colspan="8" style="text-align: right;"><a style="padding-right:5px" href="Offers.action?strVm=1&strDashboardRequest=<%=recruitId%>" >view more....</a></td>
                                                    </tr>
                                                    <%}
                                                        }
                                                        %> --%>
                                                        
                                                  <% } %>
                                                </tbody>
                                            </table>
                                            &nbsp;
                                            <h4>Offered: (<%=uF.showData(""+offeredNameMap.keySet().size(), "0")%>)</h4>
                                            <table width="100%" cellspacing="0" cellpadding="2" style="margin:0px;" class="table table-bordered">
                                                <tbody>
                                                    <tr class="darktable">
                                                        <td style="text-align: center;">Name</td>
                                                        <td style="text-align: center;">Experience</td>
                                                        <td style="text-align: center;">Education</td>
                                                        <td style="text-align: center;">Skills</td>
                                                        <td style="text-align: center;">Panel Rating</td>
                                                        <td style="text-align: center;">HR Report</td>
                                                        <td style="text-align: center;">Offer</td>
                                                    </tr>
                                                    <%   	
                                                        Iterator<String> itrOffered=offeredNameMap.keySet().iterator();
                                                        int z = 1;
                                                         while(itrOffered.hasNext()){
                                                         	/* if(z == 11 && uF.parseToInt(strVm) == 0){
                                                        break;
                                                        } */
                                                         	String candidateID=itrOffered.next();
                                                       		List<String> alInner=offeredNameMap.get(candidateID);
                                                       		
                                                       		String strStars = hmCandiStars.get(candidateID);
                                                       		Map<String, String> hmSourceDetails = hmCandidate.get(candidateID);
                                                        if(hmSourceDetails == null) hmSourceDetails = new HashMap<String, String>();
                                                        %>
                                                    <tr class="lighttable">
                                                        <td style="text-align: left;">
                                                            <div style="float: left; margin: 2px 10px 0px 0px">
                                                                <!-- border: 1px solid #000; -->
                                                                <%if(docRetriveLocation == null) { %>
                                                                <img height="40" width="40" border="0" class="lazy img-circle" id="profilecontainerimg<%=recruitId+"_"+candidateID %>" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + hmCandiImage.get(candidateID) %>" />
                                                                <%} else { %>
                                                                <img height="40" width="40" border="0" class="lazy img-circle" id="profilecontainerimg<%=recruitId+"_"+candidateID %>" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_CANDIDATE+"/"+IConstants.I_IMAGE+"/"+candidateID+"/"+IConstants.I_100x100+"/"+hmCandiImage.get(candidateID)%>" />
                                                                <%} %> 
                                                            </div>
                                                            <div style="float: left;">
                                                                <%-- <%if(!closeFlag){ %>
                                                                <!-- ====start parvez on 09-07-2021==== -->
                                                                <a href="javascript:void(0);" onclick="openCandidateProfilePopup('<%=candidateID %>','<%=recruitId %>','offer')">  <%=alInner.get(1) %> </a>
                                                                <!-- end parvez on 09-07-2021==== -->
                                                                <%} else { %>
                                                                <%=alInner.get(1) %>
                                                                <%} %> --%>
                                                                
                                                            <!-- ===start parvez date: 20-10-2021=== -->
                                                                <a href="javascript:void(0);" onclick="openCandidateProfilePopup('<%=candidateID %>','<%=recruitId %>','offer')">  <%=alInner.get(1) %> </a>
                                                            <!-- ===end parvez date: 20-10-2021=== --> 
                                                                <br/>
                                                                <div style="line-height: 16px;">
                                                                    <span id="Reting<%=candidateID%>" style="margin-right: 5px; line-height: 18px;"><%=strStars %>/5</span>
                                                                    <span id="starPrimaryR<%=recruitId+"_"+candidateID %>" style="margin-left: 5px; line-height: 12px;"></span>
                                                                </div>
                                                                <script type="text/javascript">
	                                                                $('#starPrimaryR'+'<%=recruitId+"_"+candidateID %>').raty({
	                                                                      readOnly: true,
	                                                                      start:	<%=strStars %> ,
	                                                                      half: true,
	                                                                      targetType: 'number'
	                                                                });
	                                                            </script>
                                                                <div style="font-size: 11px; line-height: 22px;"><strong style="color: gray;">Source:</strong>&nbsp;<%=uF.showData(hmSourceDetails.get("SOURCE_TYPE"),"-") %></div>
                                                                <div style="font-size: 11px; line-height: 15px;"><strong style="color: gray;">Source Name:</strong>&nbsp;<%=uF.showData(hmSourceDetails.get("SOURCE_NAME"),"-") %></div>
                                                            </div>
                                                        </td>
                                                        <td style="text-align: left;"><%=uF.showData(hmCandidateExperience.get(candidateID),"") %></td>
                                                        <td style="text-align: left;"><%=uF.showData(hmCandidateEducation.get(candidateID),"") %></td>
                                                        <td style="text-align: left;"><%=uF.showData(hmCandidateSkill.get(candidateID),"") %></td>
                                                        <%
                                                            //System.out.println("Offered  hmPanelRatingAndComments ----> " + hmPanelRatingAndComments);
                                                            	Map<String, String> hmPanelRatingCommentMap = new HashMap<String, String>();
                                                            	if(hmPanelRatingAndComments != null) {
                                                            		hmPanelRatingCommentMap = hmPanelRatingAndComments.get(recruitId +"_"+ candidateID);
                                                            	}
                                                            	//System.out.println("Offered  hmPanelRatingCommentMap ----> " + hmPanelRatingCommentMap);
                                                            	Map<String, Map<String, String>> hmRoundIds = new HashMap<String, Map<String, String>>();
                                                            	if(hmRecruitWiseRoundId != null) {
                                                            		hmRoundIds = hmRecruitWiseRoundId.get(recruitId);
                                                            	} 
                                                            %>
                                                        <td valign="top" style="text-align: left;">
                                                            <%
                                                                if(hmRoundIds != null && !hmRoundIds.isEmpty()) {
                                                                	int count = 0;
                                                                	Set keys=hmRoundIds.keySet();
                                                                	Iterator it=keys.iterator();
                                                                	while(it.hasNext()) {	
                                                                		String roundId = (String)it.next();
                                                                		Map<String, String> panelUserIds = hmRoundIds.get(roundId);
                                                                %>
                                                            <div style="float: left;">
                                                                <%-- <%if(!closeFlag){ %>
                                                                <a href="javascript:void(0)" style="line-height: 10px;" onclick="panelcomment(<%=roundId%>,<%=candidateID%>,<%=recruitId%>);">Round <%=roundId %> </a>
                                                                <% if(uF.parseToInt(hmRoundAssessment.get(recruitId+"_"+roundId+"_ID")) > 0) { %>
                                                                <div>
                                                                    <span style="color: gray;">Assessment:</span> &nbsp;
                                                                    <span>
                                                                    <a href="javascript:void(0);" style="font-weight: normal;" onclick="viewAssessmentDetail('<%=hmRoundAssessment.get(recruitId+"_"+roundId+"_ID") %>', '<%=uF.showData(hmRoundAssessment.get(recruitId+"_"+roundId+"_NAME"), "") %>')"><%=uF.showData(hmRoundAssessment.get(recruitId+"_"+roundId+"_NAME"), "") %></a>
                                                                    </span>
                                                                    <% if(uF.parseToDouble(hmAssessRateRecruitAndRoundIdWise.get(recruitId+"_"+candidateID+"_"+ roundId+"_"+hmRoundAssessment.get(recruitId+"_"+roundId+"_ID"))) > 0) { %>
                                                                    <span id="assessScoreCard_<%=recruitId %><%=candidateID %>_<%=roundId %>" style="margin-left: 7px;">
                                                                    <a href="javascript:void(0);" onclick="viewAssessmentScoreSummary('<%=hmRoundAssessment.get(recruitId+"_"+roundId+"_ID") %>', '<%=candidateID%>', '<%=recruitId %>', '<%=roundId %>');"><%=uF.showData(hmAssessRateRecruitAndRoundIdWise.get(recruitId+"_"+candidateID+"_"+ roundId+"_"+hmRoundAssessment.get(recruitId+"_"+roundId+"_ID")), "NA") %>%</a>
                                                                    </span>
                                                                    <% } %>
                                                                </div>
                                                                <% } %>
                                                                <%} else { %>
                                                                Round <%=roundId %>
                                                                <% if(uF.parseToInt(hmRoundAssessment.get(recruitId+"_"+roundId+"_ID")) > 0) { %>
                                                                <div>
                                                                    <span style="color: gray;">Assessment:</span> &nbsp;
                                                                    <span>
                                                                    <%=uF.showData(hmRoundAssessment.get(recruitId+"_"+roundId+"_NAME"), "") %>
                                                                    </span>
                                                                    <% if(uF.parseToDouble(hmAssessRateRecruitAndRoundIdWise.get(recruitId+"_"+candidateID+"_"+ roundId+"_"+hmRoundAssessment.get(recruitId+"_"+roundId+"_ID"))) > 0) { %>
                                                                    <span id="assessScoreCard_<%=recruitId %><%=candidateID %>_<%=roundId %>" style="margin-left: 7px;">
                                                                    <%=uF.showData(hmAssessRateRecruitAndRoundIdWise.get(recruitId+"_"+candidateID+"_"+ roundId+"_"+hmRoundAssessment.get(recruitId+"_"+roundId+"_ID")), "NA") %>%
                                                                    </span>
                                                                    <% } %>
                                                                </div>
                                                                <%} %>
                                                                <%} %> --%>
                                                                
                                                          <!-- ===start parvez date: 20-10-2021=== -->
                                                                <a href="javascript:void(0)" style="line-height: 10px;" onclick="panelcomment(<%=roundId%>,<%=candidateID%>,<%=recruitId%>);">Round <%=roundId %> </a>
                                                                <% if(uF.parseToInt(hmRoundAssessment.get(recruitId+"_"+roundId+"_ID")) > 0) { %>
                                                                <div>
                                                                    <span style="color: gray;">Assessment:</span> &nbsp;
                                                                    <span>
                                                                    <a href="javascript:void(0);" style="font-weight: normal;" onclick="viewAssessmentDetail('<%=hmRoundAssessment.get(recruitId+"_"+roundId+"_ID") %>', '<%=uF.showData(hmRoundAssessment.get(recruitId+"_"+roundId+"_NAME"), "") %>')"><%=uF.showData(hmRoundAssessment.get(recruitId+"_"+roundId+"_NAME"), "") %></a>
                                                                    </span>
                                                                    <% if(uF.parseToDouble(hmAssessRateRecruitAndRoundIdWise.get(recruitId+"_"+candidateID+"_"+ roundId+"_"+hmRoundAssessment.get(recruitId+"_"+roundId+"_ID"))) > 0) { %>
                                                                    <span id="assessScoreCard_<%=recruitId %><%=candidateID %>_<%=roundId %>" style="margin-left: 7px;">
                                                                    <a href="javascript:void(0);" onclick="viewAssessmentScoreSummary('<%=hmRoundAssessment.get(recruitId+"_"+roundId+"_ID") %>', '<%=candidateID%>', '<%=recruitId %>', '<%=roundId %>');"><%=uF.showData(hmAssessRateRecruitAndRoundIdWise.get(recruitId+"_"+candidateID+"_"+ roundId+"_"+hmRoundAssessment.get(recruitId+"_"+roundId+"_ID")), "NA") %>%</a>
                                                                    </span>
                                                                    <% } %>
                                                                </div>
                                                                <% } %>
                                                         <!-- ===end parvez date: 20-10-2021=== -->
                                                                
                                                            </div>
                                                            <%
                                                                Set keysEmpID = panelUserIds.keySet();
                                                                Iterator itEmpID = keysEmpID.iterator();
                                                                while(itEmpID.hasNext()) {	
                                                                	String panelEmpId = (String)itEmpID.next();
                                                                	//System.out.println("Offered  roundId ----> " + roundId+" recruitId ---> " + recruitId);
                                                                	//System.out.println("Offered  panelEmpId ----> " + panelEmpId);
                                                                	//System.out.println("Offered  hmPanelRatingCommentMap.get(roundId + _ + panelEmpId + _RATING) ----> " + hmPanelRatingCommentMap != null ? hmPanelRatingCommentMap.get(roundId +"_" + panelEmpId +"_RATING") : "");
                                                                	
                                                                	if (count > 0) {
                                                                %> <br style="line-height: 0px;"> <% } %>
                                                            
                                                            <div style="float: left; width: 100%;">
                                                                <div style="float: left; margin-right: 10px;"> <%=hmEmpName.get(panelEmpId) %> </div>
                                                                <div style="float: left; line-height: 20px;" id="starPrimary<%=recruitId + roundId + panelEmpId + candidateID%>"></div>
                                                            </div>
                                                            <script type="text/javascript">
                                                                $('#starPrimary<%=recruitId + roundId + panelEmpId + candidateID%>').raty({
                                                                    readOnly: true,
                                                                    start: <%=uF.parseToDouble(hmPanelRatingCommentMap != null ? hmPanelRatingCommentMap.get(roundId +"_" + panelEmpId +"_RATING") : "")%> ,
                                                                    half: true,
                                                                    targetType: 'number'
                                                           		});
                                                            </script>
                                                            <%
                                                                count++;
                                                                }	 			
                                                                }
                                                                }
                                                                %>
                                                        </td>
                                                        <td style="text-align: left;">
                                                            <%=uF.showData(hmCommentsHR.get(recruitId +"_"+ candidateID), "")%>	 
                                                        </td>
                                                        <td width="22%" style="vertical-align: text-top; padding-right: 10px;">
	                                                    	<% if (strUserType != null && (strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.RECRUITER))) { %>	
	                                                    		<span id="previewOfferLatterSpan_<%=recruitId +"_"+ candidateID %>">
		                                                            <%-- <%if(!closeFlag){ %> 
		                                                            <a href="javascript:void(0);" onclick="previewOfferLetter('<%=candidateID%>', '<%=recruitId %>');" ><i class="fa fa-play" aria-hidden="true"></i>Preview offer letter</a>
		                                                            <a href="CandiOfferLetterPreview.action?operation=preview&candidateId=<%=candidateID%>&recruitId=<%=recruitId %>"><i class="fa fa-play" aria-hidden="true"></i>Preview offer letter</a> commented line
		                                                            <%} else { %>
		                                                            <i class="fa fa-play" aria-hidden="true"></i>Preview offer letter
		                                                            <%} %> --%>
		                                                            
		                                                       <!-- ===start parvez date: 20-10-2021=== -->
		                                                            <a href="javascript:void(0);" onclick="previewOfferLetter('<%=candidateID%>', '<%=recruitId %>');" ><i class="fa fa-play" aria-hidden="true"></i>Preview offer letter</a>
	                                                           <!-- ===end parvez date: 20-10-2021=== -->
	                                                            
	                                                            </span>
	                                                            <br/>
	                                                            <span id="resendOfferLatterSpan_<%=recruitId +"_"+ candidateID %>">
		                                                            <%-- <%if(!closeFlag){ %>  
		                                                            <a href="javascript:void(0)" onclick="<%if(hmCandToEmp.containsKey(candidateID.trim())){%>alert('<%=hmCandToEmp.get(candidateID.trim()) %>');<%} else {%>resendOfferLetterToCandidate('<%=candidateID %>','<%=recruitId %>');<%}%>"><i class="fa fa-play" aria-hidden="true"></i>Resend offer letter to candidate</a>
		                                                            <%} else { %>
		                                                            <i class="fa fa-play" aria-hidden="true"></i>Resend offer letter to candidate
		                                                            <%} %> --%>
		                                                            
		                                                       <!-- ===start parvez date: 20-10-2021=== -->
		                                                            <a href="javascript:void(0)" onclick="<%if(hmCandToEmp.containsKey(candidateID.trim())){%>alert('<%=hmCandToEmp.get(candidateID.trim()) %>');<%} else {%>resendOfferLetterToCandidate('<%=candidateID %>','<%=recruitId %>');<%}%>"><i class="fa fa-play" aria-hidden="true"></i>Resend offer letter to candidate</a>
	                                                           <!-- ===end parvez date: 20-10-2021=== -->
	                                                            </span>
	                                                            <br/> 
	                                                            <span>
		                                                            <%-- <%if(!closeFlag){ %>  
		                                                            <a href="javascript: void(0);" onclick="<%if(hmCandToEmp.containsKey(candidateID.trim())){%>alert('<%=hmCandToEmp.get(candidateID.trim()) %>');<%} else {%>salaryRenegotiatePopup('<%=candidateID %>','<%=recruitId %>','accept');<%}%>"><i class="fa fa-play" aria-hidden="true"></i>Re-negotiation</a><br>
		                                                            <a href="javascript: void(0);" onclick="javascript: window.location='OfferAcceptAndRenegotiate.action?candidateID=<%=candidateID %>&strDashboardRequest=<%=recruitId %>';">Accept</a> &nbsp;&nbsp;&nbsp;
		                                                            <a href="javascript: void(0);" onclick="<%if(hmCandToEmp.containsKey(candidateID.trim())){%>alert('<%=hmCandToEmp.get(candidateID.trim()) %>');<%} else {%>offerAcceptPopup('<%=candidateID %>','<%=recruitId %>');<%}%>"><i class="fa fa-play" aria-hidden="true"></i>Candidate acceptance</a><br> 
		                                                            <%} else { %>
		                                                            <i class="fa fa-play" aria-hidden="true"></i>Re-negotiation &nbsp;&nbsp;&nbsp;
		                                                            <i class="fa fa-play" aria-hidden="true"></i>Candidate acceptance &nbsp;&nbsp;&nbsp;
		                                                            <%} %> --%>
		                                                            
		                                                    <!-- ===start parvez date: 20-10-2021=== -->
		                                                            	 <%-- <% if(!onBoardedEmpList.contains(alInner.get(0))){ %> --%> 
				                                                            <a href="javascript: void(0);" onclick="<%if(hmCandToEmp.containsKey(candidateID.trim())){%>alert('<%=hmCandToEmp.get(candidateID.trim()) %>');<%} else {%>salaryRenegotiatePopup('<%=candidateID %>','<%=recruitId %>','accept');<%}%>"><i class="fa fa-play" aria-hidden="true"></i>Re-negotiation</a><br>
		                                                            		<a href="javascript: void(0);" onclick="<%if(hmCandToEmp.containsKey(candidateID.trim())){%>alert('<%=hmCandToEmp.get(candidateID.trim()) %>');<%} else {%>offerAcceptPopup('<%=candidateID %>','<%=recruitId %>');<%}%>"><i class="fa fa-play" aria-hidden="true"></i>Candidate acceptance</a><br>
		                                                            	<%-- <%} %> --%>
		                                                    <!-- ===end parvez date: 20-10-2021=== -->
	                                                            </span>
	                                                            <br/>
                                                            <% } %>
                                                            
                                                            <span><%=alInner.get(2) %></span>
                                                        </td>
                                                    </tr>
                                                    <%//z++;
                                                        } %>
                                                    <%if(offeredNameMap.keySet().size()==0) { %>
                                                    <tr class="lighttable">
                                                        <td colspan="8">
                                                            <div class="nodata msg">
                                                                <span>No Offer Offered</span>
                                                            </div>
                                                        </td>
                                                    </tr>
                                                    <%-- <%} else{
                                                        if(z == 11){
                                                        %>
                                                    <tr class="lighttable">
                                                        <td colspan="8" style="text-align: right;"><a style="padding-right:5px" href="Offers.action?strVm=1&strDashboardRequest=<%=recruitId%>" >view more....</a></td>
                                                    </tr>
                                                    <%}
                                                        }
                                                        %> --%>
                                                 
                                                 <% } %>
                                                </tbody>
                                            </table>
                                        </div>
                                    </div> 
                                    
                                    
                        <div class="clr"></div>
                        <%} %>
                        <%if(hmHeaderInfo.keySet().size()==0){ %>
                        <div class="nodata msg">
                            <span>No Data Available</span>
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
    //$("img.lazy").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10});
    $("img.lazy").lazyload({event : "sporty", threshold : 200,effect : "fadeIn",failure_limit : 10});
    
    $(window).bind("load", function() {
        var timeout = setTimeout(function() { $("img.lazy").trigger("sporty") }, 1000);
    });
</script>