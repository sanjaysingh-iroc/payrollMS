<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>

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
		
		$("body").on('click','#closeButton1',function(){
			$(".modal-dialog1").removeAttr('style');
			$("#modal-body1").height(400);
			$("#modalInfo1").hide();
	    });
	});
    
    function staffReviewPoup(id, empID, userType, currentLevel, role, appFreqId) {
    	var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$("#modalInfo").show();
		$(".modal-title").html('Review Form');
		if($(window).width() >= 900){
    		$(".modal-dialog").width(900);
    	}
		$.ajax({
			//url : "AppraisalDetail.action?id="+id+"&empId="+empId, 
			url : "StaffAppraisal.action?id=" + id + "&empID=" + empID + "&userType=" + userType + "&currentLevel=" + currentLevel
					+ "&role=" + role+"&appFreqId="+appFreqId,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		}); 
	}
    
    
	function reviewerViewPoup(id, empID, userType, currentLevel, role, appFreqId, dataType) {
    	var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$("#modalInfo").show();
		$(".modal-title").html('Review Form');
		if($(window).width() >= 900){
    		$(".modal-dialog").width(900);
    	}
		$.ajax({
			//url : "AppraisalDetail.action?id="+id+"&empId="+empId, 
			url : "ReviewerViewStatus.action?id="+id+"&empID="+empID+"&userType="+userType+"&currentLevel="+currentLevel
				+ "&role="+role+"&appFreqId="+appFreqId+"&dataType="+dataType,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		}); 
	}
    
    
	function staffReviewSummaryPoup(id, empID, userType, currentLevel, role, appFreqId, dataType) {
    	var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$("#modalInfo").show();
		$(".modal-title").html('Review Summary Form');
		if($(window).width() >= 900){
	    	$(".modal-dialog").width(900);
	    }
		$.ajax({
			//url : "AppraisalDetail.action?id="+id+"&empId="+empId, 
			url : "EmpAppraisalSummary.action?id="+id+"&empID="+empID+"&userType="+userType+"&currentLevel="+currentLevel
				+ "&role="+role+"&appFreqId="+appFreqId+"&dataType="+dataType,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}
    
	function viewReopenComments(reopenComments) {
		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$("#modalInfo").show();
		$(".modal-title").html('Review Summary Form');
		$(dialogEdit).html(reopenComments);
	}
			
	/* id, empID, userId, userType, currentLevel, role, appFreqId */
	function staffReviewCorrectionPoup(id, empID, userId, userType, currentLevel, role, appFreqId) {
    	var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$("#modalInfo").show();
		$(".modal-title").html('Review Summary Form');
		if($(window).width() >= 900) {
	    	$(".modal-dialog").width(900);
	    }
		$.ajax({
			url : "StaffAppraisalCorrection.action?id="+id+"&empID="+empID+"&userId="+userId+"&userType="+userType+"&currentLevel="+currentLevel
				+ "&role="+role+"&appFreqId="+appFreqId,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}
	
			

	function seeUserTypeList(id, empId, sectionId, memberIds,appFreqId) {
		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$("#modalInfo").show();
		$(".modal-title").html('Workflow Preview');
		$.ajax({
			url : "UserTypeListPopUp.action?id=" + id + "&empId=" + empId+ "&sectionId=" + sectionId+ "&memberIds=" + memberIds+"&appFreqId="+appFreqId,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}
    
    
    function staffAppraisal(id, empId, appFreqId) {
    	window.location = "StaffAppraisal.action?id="+id+"&empID="+empId+"&appFreqId="+appFreqId;
    }
    
    
    function getMemberData(memberId, empId, id, empName, role, appFreqId) {
    	var dialogEdit = '.modal-body';
    	$(dialogEdit).empty();
    	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$("#modalInfo").show();
    	$('.modal-title').html('See Score');
    	if($(window).width() >= 900) {
    		$('.modal-dialog').width(900);
    	}
    	$.ajax({
    		url : "AppraisalScoreStatus.action?id="+id+"&empid="+empId+"&type=popup&memberId="+memberId+"&appFreqId="+appFreqId+"&role="+role,
    		cache : false,
    		success : function(data) {
    			$(dialogEdit).html(data);
    		}
    	});
    }
    
/* ===start parvez date: 12-07-2022=== */    
    function staffAppraisalReviewPoup(id, empID, userType, currentLevel, role, appFreqId) {
    	var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$("#modalInfo").show();
		$(".modal-title").html('Review Form');
		if($(window).width() >= 900){
    		$(".modal-dialog").width(900);
    	}
		$.ajax({ 
			/* url : "StaffAppraisalPreview.action?id=" + id + "&empID=" + empID + "&userType=" + userType
					+ "&role=" + role+"&appFreqId="+appFreqId+"&dataType=Reviewer Feedback", */
			url : "StaffAppraisal.action?id=" + id + "&empID=" + empID + "&userType=" + userType
					+ "&role=" + role+"&appFreqId="+appFreqId+"&dataType=Reviewer Feedback",		
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		}); 
	}
	
/* ===end parvez date: 12-07-2022=== */	
    
        
</script>

<%

/* ===start parvez date: 11-04-2022=== */  
	
	String strManager = "MYTEAM";
//===end parvez date: 11-04-2022===

    UtilityFunctions uF = new UtilityFunctions();
	String strUserType = (String) session.getAttribute("USERTYPE");
	String dataType = (String) request.getAttribute("dataType");
	String currUserType = (String) request.getAttribute("currUserType");
	String strBaseUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
	//System.out.println("Appr.jsp/216--strBaseUserTypeID="+session.getAttribute("BASEUSERTYPEID"));
	
	String sbData = (String) request.getAttribute("sbData");
	String strSearchJob = (String) request.getAttribute("strSearchJob");
	String proCount = (String)request.getAttribute("proCount");
	String proCountReviewer = (String)request.getAttribute("proCountReviewer");
	//System.out.println("Appr.jsp/222--proCountReviewer="+proCountReviewer);
	
	Map<String, String> hmEmpName = (Map<String, String>)request.getAttribute("hmEmpName");	
	//System.out.println("Appr.jsp/225--hmEmpName="+hmEmpName);
	
	Map<String, String> hmEmpCodeDesig = (Map<String, String>)request.getAttribute("hmEmpCodeDesig");
	if(hmEmpCodeDesig == null) hmEmpCodeDesig = new HashMap<String, String>();
 	
	Map<String, String> orientationMemberMp = (Map<String, String>)request.getAttribute("orientationMemberMp");
	if(orientationMemberMp == null) orientationMemberMp = new HashMap<String, String>();
	
	Map<String, Map<String, String>> appraisalDetails = (Map<String, Map<String, String>>)request.getAttribute("appraisalDetails");
//===start parvez date: 16-03-2022===	
	if(appraisalDetails == null) appraisalDetails = new HashMap<String, Map<String, String>>();
//===end parvez date: 16-03-2022===
	
	List<String> appraisalIdList = (List<String>) request.getAttribute("appraisalIdList");
//===start parvez date: 16-03-2022===	
	if(appraisalIdList == null) appraisalIdList = new ArrayList<String>();
//===end parvez date: 16-03-2022===
	
	//System.out.println("Appr.jsp/236--appraisalDetails="+appraisalDetails);
	
	Map<String, Map<String, List<String>>> empMpDetails = (Map<String, Map<String, List<String>>>)request.getAttribute("empMpDetails");
	//System.out.println("Ap.jsp/228---empMpDetails="+empMpDetails);
	Map<String, Map<String, Map<String, String>>> appraisalStatusMp = (Map<String, Map<String, Map<String, String>>>)request.getAttribute("appraisalStatusMp");
	
//===start parvez date: 16-03-2022===	
	if(appraisalStatusMp == null) appraisalStatusMp = new HashMap<String, Map<String, Map<String, String>>>();
//===end parvez date: 16-03-2022===	
	
	Map<String, List<List<String>>> hmAppraisalSectins = (Map<String, List<List<String>>>)request.getAttribute("hmAppraisalSectins");
//===start parvez date: 21-03-2022===	
	if(hmAppraisalSectins == null) hmAppraisalSectins = new HashMap<String, List<List<String>>>();

	Map<String, Map<String, List<String>>> hmRemainOrientDetailsAppWise = (Map<String, Map<String, List<String>>>)request.getAttribute("hmRemainOrientDetailsAppWise");
	if(hmRemainOrientDetailsAppWise == null) hmRemainOrientDetailsAppWise = new HashMap<String, Map<String, List<String>>>();
		
	Map<String, Map<String, List<String>>> hmRemainOrientDetailsForSelfAppWise = (Map<String, Map<String, List<String>>>)request.getAttribute("hmRemainOrientDetailsForSelfAppWise");
	if(hmRemainOrientDetailsForSelfAppWise == null) hmRemainOrientDetailsForSelfAppWise = new HashMap<String, Map<String, List<String>>>();
	
	Map<String, Map<String, List<String>>> hmRemainOrientDetailsForPeerAppWise = (Map<String, Map<String, List<String>>>)request.getAttribute("hmRemainOrientDetailsForPeerAppWise");	
	if(hmRemainOrientDetailsForPeerAppWise == null) hmRemainOrientDetailsForPeerAppWise = new HashMap<String, Map<String, List<String>>>();
//===end parvez date: 21-03-2022===
	
	Map<String, List<String>> hmExistUsersAQA = (Map<String, List<String>>)request.getAttribute("hmExistUsersAQA");
	
	if(hmExistUsersAQA == null) hmExistUsersAQA = new HashMap<String, List<String>>();
	
	Map<String, List<String>> hmOrientTypewiseID = (Map<String, List<String>>)request.getAttribute("hmOrientTypewiseID");
	if(hmOrientTypewiseID == null)hmOrientTypewiseID = new HashMap<String, List<String>>();
	
	Map<String, List<String>> hmExistSectionID = (Map<String, List<String>>)request.getAttribute("hmExistSectionID");
	if(hmExistSectionID == null)hmExistSectionID = new HashMap<String, List<String>>();
	//if(hmRemainOrientDetailsForPeerAppWise==null)hmRemainOrientDetailsForPeerAppWise=new HashMap<String, Map<String, List<String>>>();
	//Map<String, List<String>> hmExistOrientTypeAQA = (Map<String, List<String>>)request.getAttribute("hmExistOrientTypeAQA");
	Map<String, Map<String, List<String>>> hmExistOrientTypeAQAAppWise = (Map<String, Map<String, List<String>>>)request.getAttribute("hmExistOrientTypeAQAAppWise");
	if(hmExistOrientTypeAQAAppWise == null) hmExistOrientTypeAQAAppWise= new HashMap<String, Map<String, List<String>>>();
	
	//System.out.println("hmRemainOrientDetailsForPeerAppWise in JSP ::: "+ hmRemainOrientDetailsForPeerAppWise);
	Map<String, String> hmSectionwiseWorkflow = (Map<String, String>) request.getAttribute("hmSectionwiseWorkflow");
	if(hmSectionwiseWorkflow == null) hmSectionwiseWorkflow = new HashMap<String, String>();
	//System.out.println("hmSectionwiseWorkflow in JSP ::: "+ hmSectionwiseWorkflow);
	
	Map<String, String> hmScoreAggregateMap = (Map<String, String>) request.getAttribute("hmScoreAggregateMap");
	if(hmScoreAggregateMap == null) hmScoreAggregateMap = new HashMap<String, String>();
	
	
	Map<String, String> hmSectionGivenQueCnt = (Map<String, String>) request.getAttribute("hmSectionGivenQueCnt");
	if(hmSectionGivenQueCnt == null) hmSectionGivenQueCnt = new HashMap<String, String>();
	
	Map<String, String> hmSectionQueCnt = (Map<String, String>) request.getAttribute("hmSectionQueCnt");
	if(hmSectionQueCnt == null) hmSectionQueCnt = new HashMap<String, String>();
	
	Map<String, String> hmUsersFeedbackReopenComment = (Map<String, String>) request.getAttribute("hmUsersFeedbackReopenComment");
	if(hmUsersFeedbackReopenComment == null) hmUsersFeedbackReopenComment = new HashMap<String, String>();
		
	Map<String, String> hmNewGoalMap = (Map<String, String>) request.getAttribute("hmNewGoalMap");
	if(hmNewGoalMap == null) hmNewGoalMap = new HashMap<String, String>();

	CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
	Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(request);

	
	String strUserTypeId = (String)session.getAttribute(IConstants.USERTYPEID);
//System.out.println("strUserTypeId="+strUserTypeId);
	String strBaseUserTypeId = (String)session.getAttribute(IConstants.BASEUSERTYPEID);

//===start parvez date: 15-07-2022===	
	Map<String, String> hmApprovedFeedback = (Map<String, String>) request.getAttribute("hmApprovedFeedback");
	if(hmApprovedFeedback == null) hmApprovedFeedback = new HashMap<String, String>();
//===end parvez date: 15-07-2022===	
	%>
    
                <div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
                
                    <div class="leftbox reportWidth">
                    	<%=uF.showData((String) session.getAttribute("sbMessage"), "") %>
						<% session.setAttribute("sbMessage", ""); %>
                        <center>
                            <s:form name="frm_Appraisal" action="Appraisal" theme="simple">  <!-- cssStyle="margin-top: 30px;" -->
                                <s:hidden name="proPage" id="proPage" />
                                <s:hidden name="minLimit" id="minLimit" />
                                <s:hidden name="currUserType" id="currUserType" />
                               
                                <input type="text" id="strSearchJob" class="form-control" name="strSearchJob" style="margin-left: 0px; width: 282px; box-shadow: 0px 0px 0px #ccc;display:inline" value="<%=uF.showData(strSearchJob, "")%>" />
                                <input type="button" value="Search" class="btn btn-primary" onclick="submitForm();" />
                                <script>
                                    $( "#strSearchJob" ).autocomplete({
                                    	source: [ <%=uF.showData(sbData, "")%> ]
                                    });
                                </script>
                            </s:form>
                        </center>
                        <div style="font-size: 16px; font-weight: bold; border-bottom: 1px solid lightgray;">Appraiser:</div> <!-- margin: 20px 0px 10px 20px;  -->
                        
                        <div>
                            <ul style="margin-bottom: 50px">
                                <%
                                	boolean rateFlag = false;
                                    String strSessionEmpId = (String) session.getAttribute(IConstants.EMPID);
                                  //  System.out.println("Appraisal.jsp/353--appraisalIdList="+appraisalIdList);
                                    for (int j = 0; appraisalIdList != null && j<appraisalIdList.size(); j++) {
                                    	//System.out.println("Appraisal.jsp/329--appraisalStatusMp="+appraisalStatusMp+"---j="+j);
                                    	Map<String, Map<String, String>> userTypeMp = appraisalStatusMp.get(appraisalIdList.get(j));
                                    	if (userTypeMp == null)
                                    		userTypeMp = new HashMap<String, Map<String, String>>();
                                    	Map<String, String> hmAppraisalMp = appraisalDetails.get(appraisalIdList.get(j));
                                    	
                                    //	System.out.println("Appraisal.jsp/382--hmAppraisalMp="+hmAppraisalMp);
                                    	Map<String, List<String>> empMp = empMpDetails.get(appraisalIdList.get(j));
                                    	String appId = hmAppraisalMp.get("ID");
                                    	//System.out.println("App.jsp/383--empMp="+empMp);
                            			String appFreqId = hmAppraisalMp.get("APP_FREQ_ID");
                            			//System.out.println("appFreqId="+appFreqId);
                                    	List<List<String>> listAppSections = hmAppraisalSectins.get(appId);
                                    	//System.out.println("hmAppraisalSectins ::: "+hmAppraisalSectins);
                                    	Set<String> keys = empMp.keySet();
                                    	Iterator<String> it = keys.iterator();
                                    	//System.out.println("keys ::: "+keys);
                                    	while (it.hasNext()) {
                                    		String key = it.next();
                                    		
                                    		/* System.out.println("key ::: "+ key);
                                    		System.out.println("userTypeMp.get(key) ::: "+userTypeMp.get(key)); */
                                    		Map<String, String> empstatusMp = userTypeMp.get(key);
                                    		if (empstatusMp == null)
                                    			empstatusMp = new HashMap<String, String>();
                                    		//System.out.println("App.jsp/405--empstatusMp ::: "+ empstatusMp);
                                    		List<String> employeeList = empMp.get(key);
                                    		//System.out.println("employeeList ::: "+ employeeList);
                                    		for (int i = 0; employeeList != null && i < employeeList.size(); i++) {
                                    			List<String> sectionIDList = hmExistSectionID.get(employeeList.get(i)+"_"+key + "_"+ appraisalIdList.get(j));
                                    			Map<String, List<String>> hmRemainOrientDetails = hmRemainOrientDetailsAppWise.get(appraisalIdList.get(j));
                                    			if (hmRemainOrientDetails == null)
                                    				hmRemainOrientDetails = new HashMap<String, List<String>>();
                                    
                                    			Map<String, List<String>> hmRemainOrientDetailsForSelf = hmRemainOrientDetailsForSelfAppWise.get(appraisalIdList.get(j));
                                    			if (hmRemainOrientDetailsForSelf == null)
                                    				hmRemainOrientDetailsForSelf = new HashMap<String, List<String>>();
                                    
                                    			Map<String, List<String>> hmRemainOrientDetailsForPeer = hmRemainOrientDetailsForPeerAppWise.get(appraisalIdList.get(j));
                                    			if (hmRemainOrientDetailsForPeer == null)
                                    				hmRemainOrientDetailsForPeer = new HashMap<String, List<String>>();
                                    
                                    			Map<String, List<String>> hmExistOrientTypeAQA = hmExistOrientTypeAQAAppWise.get(appraisalIdList.get(j));
                                    			if (hmExistOrientTypeAQA == null)
                                    				hmExistOrientTypeAQA = new HashMap<String, List<String>>();
                                    			//System.out.println("listExistOrientTypeInAQA ::: "+ hmExistOrientTypeAQA);
                                    			String role = orientationMemberMp.get(key);
                                    			//System.out.println("role ::: "+ role);
                                    			
                                    			double dblRate = uF.parseToDouble(hmScoreAggregateMap.get(appraisalIdList.get(j) + "_" + employeeList.get(i))) / 20; %>
                                <li class="list">
                                	<div class="row row_without_margin">
                                		<% if(rateFlag) { %>
	                                		<div class="col-lg-1 col_no_padding ">
	                                			<%if (dblRate >= 3.5) {%>
		                                       		 <div style="background-color: #00FF00; padding: 0px 3px; margin: 5px; font-family: digital; font-size: 14px; border-radius: 4px 4px 4px 4px; float: left;">
		                                       		 	<%=("" + dblRate != null) ? uF.formatIntoOneDecimal(dblRate) : ""%>/5
		                                       		 </div>
	                                        	<%} else if (dblRate < 3.5 && dblRate >= 2.5) {%>
			                                        <div style="background-color: #FFFF00; padding: 0px 3px; margin: 5px; font-family: digital; font-size: 14px; border-radius: 4px 4px 4px 4px; float: left;">
			                                        	<%=("" + dblRate != null) ? uF.formatIntoOneDecimal(dblRate) : ""%>/5
			                                        </div>
	                                        	<%} else if (dblRate > 0) { //#FF0000;%>
			                                        <div style="background-color: #FF0000;color: #fff; padding: 0px 3px; margin: 5px; font-family: digital; font-size: 14px; border-radius: 4px 4px 4px 4px; float: left;">
			                                        	<%=("" + dblRate != null) ? uF.formatIntoOneDecimal(dblRate) : ""%>/5
			                                        </div>
	                                        	<%}%>
	                                		</div>
                                		<% } %>
                                		<div class="col-lg-9 col_no_padding">
                                			<span><b><%=hmAppraisalMp.get("APPRAISAL")%></b>
                                        for <%
                                            if (strSessionEmpId.equals(employeeList.get(i))) {
                                            %> You
                                        <%
                                            } else {
                                            %> <%=hmEmpName.get(employeeList.get(i))%> <%
                                            }
                                            %>
                                        [Role-<%=role%>] (<%=uF.showData(hmAppraisalMp.get("APP_FREQ_NAME"),"-")%>) <br/>
                                         <i><%=hmAppraisalMp.get("REVIEW_TYPE")%>,<%=hmAppraisalMp.get("ORIENT")%>&deg;<%-- , <%=hmAppraisalMp.get("FREQUENCY")%>,<%=hmAppraisalMp.get("FREQ_TO")%> --%></i> </span>
                                		</div>
                                		<div class="col-lg-2 col_no_padding pull-right">
                                			<% if(hmUsersFeedbackReopenComment !=null && hmUsersFeedbackReopenComment.get(appraisalIdList.get(j)+"_"+key+"_"+employeeList.get(i)) !=null) { %>
                                				<a href="javascript:void(0);" onclick="viewReopenComments('<%=hmUsersFeedbackReopenComment.get(appraisalIdList.get(j)+"_"+key+"_"+employeeList.get(i)) %>');"> <i class="fa fa-hand-o-right" aria-hidden="true"></i>Reopen Comment</a>
                                				<br/>
                                			<% } %>
                                			
                                	<!-- ===start parvez date: 15-07-2022=== -->		
                                			<% if(uF.parseToInt(key) == 13 && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_HOD_FEEDBACK_FOR_FINAL_RATING_AND_COMMENT))){ %>
                                				<%-- <% System.out.println("hmApprovedFeedback="+hmApprovedFeedback);
                                				System.out.println("appraisalId="+appraisalIdList.get(j)+"--empId="+employeeList.get(i));
                                				%> --%>
                                				<% if(hmApprovedFeedback.get(appraisalIdList.get(j)+"_"+employeeList.get(i)) == null){ %>
                                					<i class="fa fa-circle" aria-hidden="true" style="color:#ea9900" title="Waiting for Approval"></i>
                                				<% } else if(hmApprovedFeedback.get(appraisalIdList.get(j)+"_"+employeeList.get(i)) != null && !uF.parseToBoolean(hmApprovedFeedback.get(appraisalIdList.get(j)+"_"+employeeList.get(i)))){ %>
                                					<i class="fa fa-circle" aria-hidden="true" style="color:#b71cc5" title="Waiting"></i>
                                				<% } else{ %>
                                					<% if(dataType != null && dataType.equals("C")) { %>
                                						<i class="fa fa-circle" aria-hidden="true" style="color:#54aa0d" title="Completed"></i>
                                					<% } else{ %>
                                						<i class="fa fa-circle" aria-hidden="true" style="color:#b71cc5" title="Waiting"></i>
                                					<% } %>
                                				<% } %>
                                				
                                				<% if(dataType != null && dataType.equalsIgnoreCase("L")) { %>
                                       				<% if(uF.parseToBoolean(hmApprovedFeedback.get(appraisalIdList.get(j)+"_"+employeeList.get(i)))) { %>	
	                                       				<a href="javascript: void(0);" onclick="staffAppraisalReviewPoup('<%=hmAppraisalMp.get("ID")%>','<%=employeeList.get(i)%>','<%=key %>','','<%=role%>','<%=hmAppraisalMp.get("APP_FREQ_ID")%>')"><%=hmAppraisalMp.get("APPRAISAL")%> </a>
	                                       		 	<% } else { %>
	                                       		 		<%=hmAppraisalMp.get("APPRAISAL")%>
	                                       			<% } %>
	                                        	<% } else { %>
                                       				<a href="javascript: void(0);" onclick="staffReviewSummaryPoup('<%=hmAppraisalMp.get("ID")%>','<%=employeeList.get(i)%>','<%=key%>','','<%=role%>','<%=hmAppraisalMp.get("APP_FREQ_ID")%>','Reviewer Feedback')"><%=hmAppraisalMp.get("APPRAISAL")%> </a>
                                       			<% } %>
                                				
                                			<% } else { %>
                                	<!-- ===end parvez date: 15-07-2022=== -->		
                                			<%
                                            //System.out.println("listAppSections ::: "+listAppSections);
                                           // System.out.println("appid==>"+appId+"==>listAppSections ::: "+listAppSections);
                                            
                                           
                                           	int secCnt = 0;
                                            boolean sectionFilled = false;
                                  			for (int k = 0; listAppSections != null && !listAppSections.isEmpty() && k < listAppSections.size(); k++) {
                                  				List<String> innerList = listAppSections.get(k);
                                  				//System.out.println("App.jsp/476---key="+key);
                                  			//===start parvez date: 16-03-2023===	
                                  				if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_GOAL_NOT_SHOWING_IN_PEER_AND_HOD)) 
                                  						&& uF.parseToInt(innerList.get(2))!=2 && uF.parseToInt(key) == 13){
                                  					continue;
                                  				}
                                  			//===end parvez date: 16-03-2023===
                                  				List<String> listRemainOrientName = hmRemainOrientDetails.get(employeeList.get(i)+"_"+innerList.get(0) + "NAME");
                                  				List<String> listRemainOrientID = hmRemainOrientDetails.get(employeeList.get(i)+"_"+innerList.get(0) + "ID");
                                  				/* if(uF.parseToInt(hmAppraisalMp.get("ID"))==26 && uF.parseToInt(employeeList.get(i))==138){
                                  					System.out.println("App.jsp/476---listRemainOrientID="+listRemainOrientID);
                                  				} */
                                  				
                                  				List<String> listRemainOrientNameForSelf = hmRemainOrientDetailsForSelf.get(innerList.get(0) + "NAME");
                                  				List<String> listRemainOrientIDForSelf = hmRemainOrientDetailsForSelf.get(innerList.get(0) + "ID");
                                  
                                  				List<String> listRemainOrientNameForPeer = hmRemainOrientDetailsForPeer.get(innerList.get(0) + "NAME");
                                  				List<String> listRemainOrientIDForPeer = hmRemainOrientDetailsForPeer.get(innerList.get(0) + "ID");
                                  				List<String> listExistOrientTypeInAQA = hmExistOrientTypeAQA.get(innerList.get(0) + "_"+ employeeList.get(i));
                                  
                                  				List<String> listRemainOrientType = new ArrayList<String>();
                                  				List<String> listRemainOrientTypeForSelf = new ArrayList<String>();
                                  				List<String> listRemainOrientTypeForPeer = new ArrayList<String>();
                                  				StringBuilder sbRemainOrientTypeID = new StringBuilder();
                                  				StringBuilder sbRemainOrientTypeIDForSelf = new StringBuilder();
                                  				StringBuilder sbRemainOrientTypeIDForPeer = new StringBuilder();
                                  				
                                  				for (int b = 0; listRemainOrientID != null && b < listRemainOrientID.size(); b++) {
                                  					
                                  					if (listExistOrientTypeInAQA != null) {
                                  						
                                  					//===start parvez date: 16-03-2023===
                                  						/* if (!listRemainOrientID.get(b).trim().equals("") && !listExistOrientTypeInAQA.contains(listRemainOrientID.get(b).trim())) { */
                                  					
                                  						if (!listRemainOrientID.get(b).trim().equals("") && !listExistOrientTypeInAQA.contains(listRemainOrientID.get(b).trim())
                                  								&& hmFeatureStatus!=null && (!uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_GOAL_NOT_SHOWING_IN_PEER_AND_HOD)) || (uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_GOAL_NOT_SHOWING_IN_PEER_AND_HOD)) 
                                  								&& (uF.parseToInt(listRemainOrientID.get(b))==4 || uF.parseToInt(listRemainOrientID.get(b))==14 || uF.parseToInt(listRemainOrientID.get(b))==13)&& uF.parseToInt(innerList.get(2))==2))) { 
                                  					//===end parvez date: 16-03-2023===
                                  							listRemainOrientType.add(listRemainOrientName.get(b));
                                  							sbRemainOrientTypeID.append(listRemainOrientID.get(b) + ",");
                                  							/* if(uF.parseToInt(hmAppraisalMp.get("ID"))==26 && uF.parseToInt(employeeList.get(i))==138){
                                              					System.out.println("App.jsp/482---sectionId=="+innerList.get(0)+"---systemType=="+innerList.get(2)+"---listRemainOrientID="+listRemainOrientID);
                                              					System.out.println("App.jsp/483---listExistOrientTypeInAQA=="+listExistOrientTypeInAQA);
                                              				} */
                                  							
                                  						} else if (!listRemainOrientID.get(b).trim().equals("3")) {
                                  							List<String> listExistUserInAQA = hmExistUsersAQA.get(employeeList.get(i)+ "_"+ innerList.get(0)+ "_"+ listRemainOrientID.get(b));
                                  							List<String> listIds = hmOrientTypewiseID.get(appraisalIdList.get(j)+ "_"+ listRemainOrientID.get(b));
                                  							
                                  							boolean flag = false;
                                  							for (int a = 0; listIds != null && a < listIds.size(); a++) {
                                  								if (listExistUserInAQA != null) {
                                  									if (!listIds.get(a).trim() .equals("") && uF.parseToInt(listIds.get(a).trim()) > 0 && !listExistUserInAQA.contains(listIds.get(a).trim())) {
                                  										flag = true;
                                  									} else {
                                  										flag = false;
                                  										break;
                                  									}
                                  								}
                                  							}
                                  							if (flag == true) {
                                  								listRemainOrientType.add(listRemainOrientName.get(b));
                                  								sbRemainOrientTypeID.append(listRemainOrientID.get(b) + ",");
                                  								
                                  							}
                                  							/* if(uF.parseToInt(hmAppraisalMp.get("ID"))==26 && uF.parseToInt(employeeList.get(i))==138){
                                              					System.out.println("App.jsp/507---SectionID=="+innerList.get(0)+"---listRemainOrientType="+listRemainOrientType);
                                              				} */
                                  							
                                  						}
                                  					} else {
                                  						listRemainOrientType.add(listRemainOrientName.get(b));
                                  						sbRemainOrientTypeID.append(listRemainOrientID.get(b)+ ",");
                                  						/* if(uF.parseToInt(hmAppraisalMp.get("ID"))==26 && uF.parseToInt(employeeList.get(i))==138){
                                          					System.out.println("App.jsp/515---listRemainOrientType="+listRemainOrientType);
                                          				} */
                                  						
                                  					}
                                  				}
                                  
                                  				for (int b = 0; listRemainOrientIDForSelf != null && b < listRemainOrientIDForSelf.size(); b++) {
                                  					if (listExistOrientTypeInAQA != null) {
                                  						if (!listRemainOrientIDForSelf.get(b).trim().equals("")&& !listExistOrientTypeInAQA.contains(listRemainOrientIDForSelf.get(b).trim())) {
                                  							listRemainOrientTypeForSelf.add(listRemainOrientNameForSelf.get(b));
                                  							sbRemainOrientTypeIDForSelf.append(listRemainOrientIDForSelf.get(b) + ",");
                                  						} else {
                                  							List<String> listExistUserInAQA = hmExistUsersAQA.get(employeeList.get(i)+ "_"+ innerList.get(0)+ "_"+ listRemainOrientIDForSelf.get(b));
                                  							List<String> listIds = hmOrientTypewiseID.get(appraisalIdList.get(j)+ "_"+ listRemainOrientIDForSelf.get(b));
                                  							//System.out.println("listExistUserInAQA 2 ===> " + listExistUserInAQA);
                                  							//System.out.println("listIds 2 ===> " + listIds);
                                  							boolean flag = false;
                                  							for (int a = 0; listIds != null && a < listIds.size(); a++) {
                                  								if (listExistUserInAQA != null) {
                                  									if (!listIds.get(a).trim().equals("")&& uF.parseToInt(listIds.get(a).trim()) > 0&& !listExistUserInAQA.contains(listIds.get(a).trim())) {
                                  										flag = true;
                                  									} else {
                                  										flag = false;
                                  										break;
                                  									}
                                  								}
                                  							}
                                  							if (flag == true) {
                                  								listRemainOrientTypeForSelf.add(listRemainOrientNameForSelf.get(b));
                                  								sbRemainOrientTypeIDForSelf.append(listRemainOrientIDForSelf.get(b) + ",");
                                  							}
                                  						}
                                  					} else {
                                  						listRemainOrientTypeForSelf.add(listRemainOrientNameForSelf.get(b));
                                  						sbRemainOrientTypeIDForSelf.append(listRemainOrientIDForSelf.get(b) + ",");
                                  					}
                                  				}
                                  
                                  				for (int b = 0; listRemainOrientIDForPeer != null && b < listRemainOrientIDForPeer.size(); b++) {
                                  					if (listExistOrientTypeInAQA != null) {
                                  						if (!listRemainOrientIDForPeer.get(b).trim().equals("")&& !listExistOrientTypeInAQA.contains(listRemainOrientIDForPeer.get(b).trim())) {
                                  							listRemainOrientTypeForPeer.add(listRemainOrientNameForPeer.get(b));
                                  							sbRemainOrientTypeIDForPeer.append(listRemainOrientIDForPeer.get(b) + ",");
                                  						} else if (!listRemainOrientIDForPeer.get(b).trim().equals("3")) {
                                  							List<String> listExistUserInAQA = hmExistUsersAQA.get(employeeList.get(i)+ "_"+ innerList.get(0)+ "_"+ listRemainOrientIDForPeer.get(b));
                                  							List<String> listIds = hmOrientTypewiseID.get(appraisalIdList.get(j)+ "_"+ listRemainOrientIDForPeer.get(b));
                                  							//System.out.println("listExistUserInAQA 3 ===> " + listExistUserInAQA);
                                  							//System.out.println("listIds 3 ===> " + listIds);
                                  							boolean flag = false;
                                  							for (int a = 0; listIds != null && a < listIds.size(); a++) {
                                  								if (listExistUserInAQA != null) {
                                  									if (!listIds.get(a).trim().equals("")&& uF.parseToInt(listIds.get(a).trim()) > 0&& !listExistUserInAQA.contains(listIds.get(a).trim())) {
                                  										flag = true;
                                  									} else {
                                  										flag = false;
                                  										break;
                                  									}
                                  								}
                                  							}
                                  							if (flag == true) {
                                  								listRemainOrientTypeForPeer.add(listRemainOrientNameForPeer.get(b));
                                  								sbRemainOrientTypeIDForPeer.append(listRemainOrientIDForPeer.get(b) + ",");
                                  							}
                                  						}
                                  					} else {
                                  						listRemainOrientTypeForPeer.add(listRemainOrientNameForPeer.get(b));
                                  						sbRemainOrientTypeIDForPeer.append(listRemainOrientIDForPeer.get(b) + ",");
                                  					}
                                  				}
                                            %>
                             <!-- ===start parvez date: 15-04-2022=== -->
                                        <% if (empstatusMp.get(employeeList.get(i)+"_"+strSessionEmpId) != null && sectionIDList != null && sectionIDList.contains(innerList.get(0))) {%>
                                        		<!-- <img src="images1/icons/re_submit.png"title="Waiting for Approval"> -->
                                        		<i class="fa fa-circle" aria-hidden="true" style="color:#f7ee1d"></i>
                                        		<%-- <% if(uF.parseToInt(key) == 13 && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_HOD_FEEDBACK_FOR_FINAL_RATING_AND_COMMENT))){ %>
                                        			<% if(k==0){ %>
                                        				<i class="fa fa-circle" aria-hidden="true" style="color:#f7ee1d"></i>
                                        			<% } %>
                                        		<% } else{ %>
                                        			<i class="fa fa-circle" aria-hidden="true" style="color:#f7ee1d"></i>
                                        		<% } %> --%>
                                        		
                                        <% } else if (listRemainOrientType != null && !listRemainOrientType.isEmpty() && !role.equalsIgnoreCase("Self") && !role.equalsIgnoreCase("Peer")) {%>
                                        		<%-- <img src="images1/icons/pullout.png" title="Waiting for <%=listRemainOrientType%> Approval"> --%>
                                        		<i class="fa fa-circle" aria-hidden="true" style="color:#ea9900" title="Waiting for Approval"></i>
                         						<%-- <% if(uF.parseToInt(key) == 13 && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_HOD_FEEDBACK_FOR_FINAL_RATING_AND_COMMENT))){ %>
                                        			<% if(k==0){ %>
                                        				<i class="fa fa-circle" aria-hidden="true" style="color:#ea9900" title="Waiting for Approval"></i>
                                        			<% } %>
                                        		<% } else{ %>
                                        			<i class="fa fa-circle" aria-hidden="true" style="color:#ea9900" title="Waiting for Approval"></i>
                                        		<% } %> --%>
                                        <% } else if (listRemainOrientTypeForSelf != null && !listRemainOrientTypeForSelf.isEmpty() && role.equalsIgnoreCase("Self")) {%>
                                       		 	<%-- <img src="images1/icons/pullout.png" title="Waiting for <%=listRemainOrientTypeForSelf%> Approval"> --%>
                                       		 	<i class="fa fa-circle" aria-hidden="true" style="color:#ea9900" title="Waiting for Approval"></i>
                                       		 	<%-- <% if(uF.parseToInt(key) == 13 && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_HOD_FEEDBACK_FOR_FINAL_RATING_AND_COMMENT))){ %>
                                        			<% if(k==0){ %>
                                        				<i class="fa fa-circle" aria-hidden="true" style="color:#ea9900" title="Waiting for Approval"></i>
                                        			<% } %>
                                        		<% } else{ %>
                                        			<i class="fa fa-circle" aria-hidden="true" style="color:#ea9900" title="Waiting for Approval"></i>
                                        		<% } %> --%>
                                       		 	
                                        <% } else if (listRemainOrientTypeForPeer != null && !listRemainOrientTypeForPeer.isEmpty() && role.equalsIgnoreCase("Peer")) {%>
                                        		<%-- <img src="images1/icons/pullout.png" title="Waiting for <%=listRemainOrientTypeForPeer%> Approval"> --%>
                                        		<i class="fa fa-circle" aria-hidden="true" style="color:#ea9900" title="Waiting for Approval"></i>
                                        		<%-- <% if(uF.parseToInt(key) == 13 && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_HOD_FEEDBACK_FOR_FINAL_RATING_AND_COMMENT))){ %>
                                        			<% if(k==0){ %>
                                        				<i class="fa fa-circle" aria-hidden="true" style="color:#ea9900" title="Waiting for Approval"></i>
                                        			<% } %>
                                        		<% } else{ %>
                                        			<i class="fa fa-circle" aria-hidden="true" style="color:#ea9900" title="Waiting for Approval"></i>
                                        		<% } %> --%>
                                        		
                                        <% } else { %>
                                             <% if(dataType != null && dataType.equals("C")) { %>
                                        		<!-- <img src="images1/icons/approved.png" title="Waiting"> -->
                                        		<i class="fa fa-circle" aria-hidden="true" style="color:#54aa0d" title="Waiting"></i>
                                        		<%-- <% if(uF.parseToInt(key) == 13 && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_HOD_FEEDBACK_FOR_FINAL_RATING_AND_COMMENT))){ %>
                                        			<% if(k==0){ %>
                                        				<i class="fa fa-circle" aria-hidden="true" style="color:#54aa0d" title="Waiting"></i>
                                        			<% } %>
                                        		<% } else{ %>
                                        			<i class="fa fa-circle" aria-hidden="true" style="color:#54aa0d" title="Waiting"></i>
                                        		<% } %> --%>
                                        		
                                        	<% } else { %>
                                        		<!-- <img src="images1/icons/pending.png" title="Waiting"> -->
                                        		<i class="fa fa-circle" aria-hidden="true" style="color:#b71cc5" title="Waiting"></i>
                                        		<%-- <% if(uF.parseToInt(key) == 13 && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_HOD_FEEDBACK_FOR_FINAL_RATING_AND_COMMENT))){ %>
                                        			<% if(k==0){ %>
                                        				<i class="fa fa-circle" aria-hidden="true" style="color:#b71cc5" title="Waiting"></i>
                                        			<% } %>
                                        		<% } else { %>
                                        			<i class="fa fa-circle" aria-hidden="true" style="color:#b71cc5" title="Waiting"></i>
                                        		<% } %> --%>
                                        	<% } %>
                                        <% } %>
                                        
                                        <% 
                                        /* if(uF.parseToInt(employeeList.get(i))==661 && uF.parseToInt(hmAppraisalMp.get("ID"))==32) {
                                        	System.out.println(employeeList.get(i)+"_"+strSessionEmpId +" -- " + empstatusMp.get(employeeList.get(i)+"_"+strSessionEmpId));
                                        	System.out.println(" listRemainOrientType --->> " + listRemainOrientType +" --- listRemainOrientTypeForSelf ===>> " + listRemainOrientTypeForSelf + " --- listRemainOrientTypeForPeer ===>> " + listRemainOrientTypeForPeer);
                                        } */
                                        if (empstatusMp.get(employeeList.get(i)+"_"+strSessionEmpId) != null && sectionIDList != null && sectionIDList.contains(innerList.get(0))) { %>
                                        	
                                        	<% if(dataType != null && dataType.equalsIgnoreCase("L")) { %>
                                        		<a href="javascript: void(0);" onclick="staffReviewPoup('<%=hmAppraisalMp.get("ID")%>','<%=employeeList.get(i)%>','<%=key %>','<%=innerList.get(0)%>','<%=role%>','<%=hmAppraisalMp.get("APP_FREQ_ID")%>')"><%=innerList.get(1) %> </a>
                                        		<%-- <% if(uF.parseToInt(key) == 13 && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_HOD_FEEDBACK_FOR_FINAL_RATING_AND_COMMENT))){ %>
                                        			<% if(k==0){ %>
                                        				<a href="javascript: void(0);" onclick="staffReviewPoup('<%=hmAppraisalMp.get("ID")%>','<%=employeeList.get(i)%>','<%=key %>','<%=innerList.get(0)%>','<%=role%>','<%=hmAppraisalMp.get("APP_FREQ_ID")%>')"><%=hmAppraisalMp.get("APPRAISAL")%> </a>
                                        			<% } %>
                                        		<% } else { %>
                                        			<a href="javascript: void(0);" onclick="staffReviewPoup('<%=hmAppraisalMp.get("ID")%>','<%=employeeList.get(i)%>','<%=key %>','<%=innerList.get(0)%>','<%=role%>','<%=hmAppraisalMp.get("APP_FREQ_ID")%>')"><%=innerList.get(1) %> </a>
                                        		<% } %> --%>
                                        	<% } else { %>
                                        		<a href="javascript: void(0);" onclick="staffReviewSummaryPoup('<%=hmAppraisalMp.get("ID")%>','<%=employeeList.get(i)%>','<%=key%>','<%=innerList.get(0)%>','<%=role%>','<%=hmAppraisalMp.get("APP_FREQ_ID")%>','')"><%=innerList.get(1) %></a>  <%-- Section(<%=k + 1%>)  --%>
                                        		<%-- <% if(uF.parseToInt(key) == 13 && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_HOD_FEEDBACK_FOR_FINAL_RATING_AND_COMMENT))){ %>
                                        			<% if(k==0){ %>
                                        				<a href="javascript: void(0);" onclick="staffReviewSummaryPoup('<%=hmAppraisalMp.get("ID")%>','<%=employeeList.get(i)%>','<%=key%>','<%=innerList.get(0)%>','<%=role%>','<%=hmAppraisalMp.get("APP_FREQ_ID")%>','')"><%=hmAppraisalMp.get("APPRAISAL")%></a>
                                        			<% } %>
                                        		<% } else { %>
                                        			<a href="javascript: void(0);" onclick="staffReviewSummaryPoup('<%=hmAppraisalMp.get("ID")%>','<%=employeeList.get(i)%>','<%=key%>','<%=innerList.get(0)%>','<%=role%>','<%=hmAppraisalMp.get("APP_FREQ_ID")%>','')"><%=innerList.get(1) %></a>
                                        		<% } %> --%>
                                        	<% } %>	
                                        <%
                                        } else if (listRemainOrientType != null && !listRemainOrientType.isEmpty() && !role.equalsIgnoreCase("Self") && !role.equalsIgnoreCase("Peer")) {%>
                                        		
                                        		<a href="javascript: void(0)" onclick="seeUserTypeList('<%=hmAppraisalMp.get("ID")%>','<%=employeeList.get(i)%>','<%=innerList.get(0)%>','<%=sbRemainOrientTypeID.toString()%>','<%=hmAppraisalMp.get("APP_FREQ_ID")%>');"><%=innerList.get(1) %></a>
                                        		<%-- <% if(uF.parseToInt(key) == 13 && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_HOD_FEEDBACK_FOR_FINAL_RATING_AND_COMMENT))){ %>
                                        			<% if(k==0){ %>
                                        				<a href="javascript: void(0)" onclick="seeUserTypeList('<%=hmAppraisalMp.get("ID")%>','<%=employeeList.get(i)%>','<%=innerList.get(0)%>','<%=sbRemainOrientTypeID.toString()%>','<%=hmAppraisalMp.get("APP_FREQ_ID")%>');"><%=hmAppraisalMp.get("APPRAISAL")%></a>
                                        			<% } %>
                                        		<% } else { %>
                                        			<a href="javascript: void(0)" onclick="seeUserTypeList('<%=hmAppraisalMp.get("ID")%>','<%=employeeList.get(i)%>','<%=innerList.get(0)%>','<%=sbRemainOrientTypeID.toString()%>','<%=hmAppraisalMp.get("APP_FREQ_ID")%>');"><%=innerList.get(1) %></a>
                                        		<% } %> --%>
                                        <% } else if (listRemainOrientTypeForSelf != null && !listRemainOrientTypeForSelf.isEmpty() && role.equalsIgnoreCase("Self")) {%>
                                        		
                                        		<a href="javascript: void(0)" onclick="seeUserTypeList('<%=hmAppraisalMp.get("ID")%>','<%=employeeList.get(i)%>','<%=innerList.get(0)%>','<%=sbRemainOrientTypeIDForSelf.toString()%>','<%=hmAppraisalMp.get("APP_FREQ_ID")%>');"><%=innerList.get(1) %></a>
                                        		<%-- <% if(uF.parseToInt(key) == 13 && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_HOD_FEEDBACK_FOR_FINAL_RATING_AND_COMMENT))){ %>
                                        			<% if(k==0){ %>
                                        				<a href="javascript: void(0)" onclick="seeUserTypeList('<%=hmAppraisalMp.get("ID")%>','<%=employeeList.get(i)%>','<%=innerList.get(0)%>','<%=sbRemainOrientTypeIDForSelf.toString()%>','<%=hmAppraisalMp.get("APP_FREQ_ID")%>');"><%=hmAppraisalMp.get("APPRAISAL")%></a>
                                        			<% } %>
                                        		<% } else { %>
                                        			<a href="javascript: void(0)" onclick="seeUserTypeList('<%=hmAppraisalMp.get("ID")%>','<%=employeeList.get(i)%>','<%=innerList.get(0)%>','<%=sbRemainOrientTypeIDForSelf.toString()%>','<%=hmAppraisalMp.get("APP_FREQ_ID")%>');"><%=innerList.get(1) %></a>
                                        		<% } %> --%>
                                        <% } else if (listRemainOrientTypeForPeer != null && !listRemainOrientTypeForPeer.isEmpty() && role.equalsIgnoreCase("Peer")) {%>
                                        		
                                        		<a href="javascript: void(0)"  onclick="seeUserTypeList('<%=hmAppraisalMp.get("ID")%>','<%=employeeList.get(i)%>','<%=innerList.get(0)%>','<%=sbRemainOrientTypeIDForPeer.toString()%>','<%=hmAppraisalMp.get("APP_FREQ_ID")%>');"><%=innerList.get(1) %></a>
                                        		<%-- <% if(uF.parseToInt(key) == 13 && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_HOD_FEEDBACK_FOR_FINAL_RATING_AND_COMMENT))){ %>
                                        			<% if(k==0){ %>
                                        				<a href="javascript: void(0)"  onclick="seeUserTypeList('<%=hmAppraisalMp.get("ID")%>','<%=employeeList.get(i)%>','<%=innerList.get(0)%>','<%=sbRemainOrientTypeIDForPeer.toString()%>','<%=hmAppraisalMp.get("APP_FREQ_ID")%>');"><%=hmAppraisalMp.get("APPRAISAL")%></a>
                                        			<% } %>
                                        		<% } else { %>
                                        			<a href="javascript: void(0)"  onclick="seeUserTypeList('<%=hmAppraisalMp.get("ID")%>','<%=employeeList.get(i)%>','<%=innerList.get(0)%>','<%=sbRemainOrientTypeIDForPeer.toString()%>','<%=hmAppraisalMp.get("APP_FREQ_ID")%>');"><%=innerList.get(1) %></a>
                                        		<% } %> --%>
                                        <% 
                                        /* if(uF.parseToInt(employeeList.get(i))==661 && uF.parseToInt(hmAppraisalMp.get("ID"))==32) {
                                        	System.out.println(" -- 4");
                                        } */
                                        } else { %>
                                        	
                                       		<% if(dataType != null && dataType.equalsIgnoreCase("L")) { %>
                                       			<% if(secCnt == 0 || sectionFilled) { %>	
	                                       			<%-- <% System.out.println("userType="+key+"---role="+role); %> --%>
	                                       			<a href="javascript: void(0);" onclick="staffReviewPoup('<%=hmAppraisalMp.get("ID")%>','<%=employeeList.get(i)%>','<%=key %>','<%=innerList.get(0)%>','<%=role%>','<%=hmAppraisalMp.get("APP_FREQ_ID")%>')"><%=innerList.get(1) %> </a>
	                                       			<%-- <% if(uF.parseToInt(key) == 13 && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_HOD_FEEDBACK_FOR_FINAL_RATING_AND_COMMENT))){ %>
	                                       				<a href="javascript: void(0);" onclick="staffAppraisalReviewPoup('<%=hmAppraisalMp.get("ID")%>','<%=employeeList.get(i)%>','<%=key %>','<%=innerList.get(0)%>','<%=role%>','<%=hmAppraisalMp.get("APP_FREQ_ID")%>')"><%=innerList.get(1) %> </a>
	                                       				<a href="javascript: void(0);" onclick="staffAppraisalReviewPoup('<%=hmAppraisalMp.get("ID")%>','<%=employeeList.get(i)%>','<%=key %>','<%=innerList.get(0)%>','<%=role%>','<%=hmAppraisalMp.get("APP_FREQ_ID")%>')"><%=hmAppraisalMp.get("APPRAISAL")%> </a>
	                                       			<% } else{ %>
	                                       				<a href="javascript: void(0);" onclick="staffReviewPoup('<%=hmAppraisalMp.get("ID")%>','<%=employeeList.get(i)%>','<%=key %>','<%=innerList.get(0)%>','<%=role%>','<%=hmAppraisalMp.get("APP_FREQ_ID")%>')"><%=innerList.get(1) %> </a>
	                                       			<% } %> --%>	
	                                       		 <% 
	                                       		/* if(uF.parseToInt(employeeList.get(i))==661 && uF.parseToInt(hmAppraisalMp.get("ID"))==32) {
	                                            	System.out.println(" -- 5");
	                                            } */ 
                                       			} else { %>
	                                       		 	<%-- <a href="javascript: void(0);"><%=innerList.get(1) %> </a> --%>
	                                       		 	<%=innerList.get(1) %>
	                                       		 	<%-- <% if(uF.parseToInt(key) == 13 && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_HOD_FEEDBACK_FOR_FINAL_RATING_AND_COMMENT))){ %>
	                                       		 		<% if(k==0){ %>
	                                       		 			<%=hmAppraisalMp.get("APPRAISAL")%>
	                                       		 		<% } %>
	                                       		 	<% } else { %>
	                                       		 		<%=innerList.get(1) %>
	                                       		 	<% } %> --%>
	                                       		<% } %>
	                                        <% 
                                       		} else { %>
                                       			
                                       			<a href="javascript: void(0);" onclick="staffReviewSummaryPoup('<%=hmAppraisalMp.get("ID")%>','<%=employeeList.get(i)%>','<%=key%>','<%=innerList.get(0)%>','<%=role%>','<%=hmAppraisalMp.get("APP_FREQ_ID")%>')"><%=innerList.get(1) %> </a>
                                       			<%-- <% if(uF.parseToInt(key) == 13 && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_HOD_FEEDBACK_FOR_FINAL_RATING_AND_COMMENT))){ %>
                                       				<a href="javascript: void(0);" onclick="staffReviewSummaryPoup('<%=hmAppraisalMp.get("ID")%>','<%=employeeList.get(i)%>','<%=key%>','<%=innerList.get(0)%>','<%=role%>','<%=hmAppraisalMp.get("APP_FREQ_ID")%>','Reviewer Feedback')"><%=hmAppraisalMp.get("APPRAISAL")%> </a>
                                       			<% } else { %>
                                       				<a href="javascript: void(0);" onclick="staffReviewSummaryPoup('<%=hmAppraisalMp.get("ID")%>','<%=employeeList.get(i)%>','<%=key%>','<%=innerList.get(0)%>','<%=role%>','<%=hmAppraisalMp.get("APP_FREQ_ID")%>','')"><%=innerList.get(1) %> </a>
                                       			<% } %> --%>
               
	                                        	 
	                                        <% } %>
                                        <% } %>
                                        <br />
                                        <% secCnt++; 
	                                        if(uF.parseToInt(hmSectionQueCnt.get(hmAppraisalMp.get("ID")+"_"+employeeList.get(i)+"_"+innerList.get(0))) == uF.parseToInt(hmSectionGivenQueCnt.get(key+"_"+hmAppraisalMp.get("ID")+"_"+hmAppraisalMp.get("APP_FREQ_ID")+"_"+"0"+"_"+employeeList.get(i)+"_"+innerList.get(0)))) {
	                                        	sectionFilled = true;
	                                        } else {
	                                        	sectionFilled = false;
	                                        }
                                  	} %>
                                  	<% } %>
                            <!-- ===end parvez date: 15-04-2022=== -->      	
                                		</div>
                                				
	                               <%--  <div class="high" style="float: left; line-height: 18px;">
	                                    <% System.out.println("Appr.jsp/634--appraisalIdList.get(j)="+appraisalIdList.get(j));
	                                    String tempId = (String)appraisalIdList.get(j)+""; %>
	                                    <b>Goal: </b><%=hmNewGoalMap.get(tempId) %>
	                               	</div> --%>
	                               			
                                	</div>
                                	
                  		<% 
                  			Map<String,List<String>> hmAlMember = (Map<String,List<String>>) request.getAttribute("hmAlMember");
                  			if(hmAlMember == null) hmAlMember = new HashMap<String,List<String>>();
                  			
                  			List<String> memberList = new ArrayList<String>(); 
                  			memberList = hmAlMember.get(appId);
                  			//System.out.println("memberList="+memberList);
                  			
                  			Map<String,String> hmPriorUserType = (Map<String,String>) request.getAttribute("hmPriorUserType");
                  			if(hmPriorUserType == null) hmPriorUserType = new HashMap<String,String>();
                  			
                  			Map<String,String> existFeedback = (Map<String,String>) request.getAttribute("existFeedback");
                  			if(existFeedback == null) existFeedback = new HashMap<String,String>();
                  			//System.out.println("App_Id="+appId+"empId="+employeeList.get(i));
                  		%>
                  			<%-- <% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_SHOW_SELF_MANAGER_REVIEW_FEEDBACK_TO_HOD)) && ((memberList.contains("13") && strBaseUserType.equals(IConstants.HOD)) 
                  					|| (memberList.contains("13") && uF.parseToInt(strUserTypeId) == uF.parseToInt(hmPriorUserType.get(appId+"_"+employeeList.get(i).trim()))))){ %> --%>
                  				<% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_SHOW_SELF_MANAGER_REVIEW_FEEDBACK_TO_HOD))){ %>	
				               	<table class="table table_no_border" cellpadding="0" cellspacing="0" style="width:20% !important">  <!-- id="lt" -->
				               		
					                	<tr>
						                <%
						                   int addOneCnt = 0;
						                   for (int m=0; m<memberList.size(); m++) { 
						                	   
						                	   String MemberLabel = "";
						                   		if(orientationMemberMp.get(memberList.get(m)).equals("Manager")){
						                   			MemberLabel = strManager;
						                   		} else{
						                   			MemberLabel = orientationMemberMp.get(memberList.get(m));
						                   		}
						                   		//System.out.println("App/737---MemberLabel="+MemberLabel+"---currUserType="+currUserType+"----Mem="+orientationMemberMp.get(memberList.get(m)));
						                   		//System.out.println("App/656--PriorUserType="+hmPriorUserType.get(appId+"_"+employeeList.get(i).trim()));
						                   %>
						                   
						                   
								                   <% if(!orientationMemberMp.get(memberList.get(m)).equals(strBaseUserType) && !orientationMemberMp.get(memberList.get(m)).equals(strUserType) && !MemberLabel.equals(currUserType)
								                		    && uF.parseToInt(memberList.get(m)) != uF.parseToInt(hmPriorUserType.get(appId+"_"+employeeList.get(i).trim()))){ %>
								                   <%-- <% if(uF.parseToInt(memberList.get(m)) != uF.parseToInt(strBaseUserTypeId) && !orientationMemberMp.get(memberList.get(m)).equals(currUserType) && uF.parseToInt(memberList.get(m)) != uF.parseToInt(hmPriorUserType.get(appId+"_"+employeeList.get(i).trim()))){ %> --%>
								                   
								                  
								                   <%-- <% System.out.println("App/750---memberList="+memberList.get(m)+"--strBaseUserTypeId="+strBaseUserTypeId+"---currUserType="+currUserType+"---Role="+orientationMemberMp.get(memberList.get(m))); %> --%>
								                    <td>
								       <!-- ===start parvez date: 02-03-2023=== -->             
								                <% 
								                    String strRole = orientationMemberMp.get(memberList.get(m).trim()).equalsIgnoreCase("Reviewer")?"1" :"0";	
								                   
								                	if(existFeedback.get(appId+"_"+memberList.get(m)+"_"+employeeList.get(i).trim()+"_"+strRole) != null){ %>
								                    <a href="javascript:void(0)" onclick="getMemberData('<%=memberList.get(m)%>','<%=employeeList.get(i).trim()%>','<%=appId%>', '<%=hmEmpName.get(employeeList.get(i).trim())%>', '<%=orientationMemberMp.get(memberList.get(m).trim())%>','<%=appFreqId %>')">
				                                        <span style="font-weight: normal;"> <%=orientationMemberMp.get(memberList.get(m))%> Appraisal</span></a>
								                  <% } else { %>  
								                  		<span style="font-weight: normal;"> <%=orientationMemberMp.get(memberList.get(m))%> Appraisal</span>
								                  <% } %>
								        <!-- ===end parvez date: 02-03-2023=== -->            
								                     </td>
								                    <% } %>
								               <% } %>
							                   </tr>
											
		                                </table>
		                           <% } %>          	
                                	
                                </li>            
                                <%
                                    }
                                 }
                                %>
                                <%-- <div class="high" style="float: left; line-height: 18px;">
                                    <b>Goal: </b><%=hmNewGoalMap.get(appraisalIdList.get(j)) %>
                               	</div> --%>    	
                            <%    
                              }
                              %>
                               
                              <% if (appraisalIdList == null || (appraisalIdList.isEmpty() || appraisalIdList.size() == 0)) {%>
                                <li>
                                    <div class="nodata msg">
                                        <span> No Review Available.</span>
                                    </div>
                                </li>
                                <% } %>               
                            </ul>
                        </div>
                                 
                        
                        <%if (appraisalIdList != null && !appraisalIdList.isEmpty()&& appraisalIdList.size() == 10) { %>
							<div style="text-align: center;clear: both;width: 100%;">

						<%
						//System.out.println("Appraisal.jsp/646--proCount="+proCount);
							int intproCnt = uF.parseToInt(proCount);
							int pageCnt = 0;
							int minLimit = 0;

							for (int i = 1; i <= intproCnt; i++) {
								minLimit = pageCnt * 10;
								pageCnt++;
						%>
						<%
							if (i == 1) {
								String strPgCnt = (String) request.getAttribute("proPage");
								String strMinLimit = (String) request.getAttribute("minLimit");
								if (uF.parseToInt(strPgCnt) > 1) {
									strPgCnt = (uF.parseToInt(strPgCnt) - 1) + "";
									strMinLimit = (uF.parseToInt(strMinLimit) - 10)
											+ "";
								}
								if (strMinLimit == null) {
									strMinLimit = "0";
								}
								if (strPgCnt == null) {
									strPgCnt = "1";
								}
						%>
								<span style="color: lightgray;"> 
								<% if (uF.parseToInt((String) request.getAttribute("proPage")) > 1) { %>
									<a href="javascript:void(0);" onclick="loadMore('<%=strPgCnt%>','<%=strMinLimit%>','<%=request.getAttribute("dataType")%>','<%=request.getAttribute("currUserType")%>');"> <%="< Prev"%></a>
								<% } else { %>
									<b><%="< Prev"%></b>
								<% } %> 
								</span> 
							<span>
								<a href="javascript:void(0);" onclick="loadMore('<%=pageCnt%>','<%=minLimit%>','<%=request.getAttribute("dataType")%>','<%=request.getAttribute("currUserType")%>');"
								<%if (((String) request.getAttribute("proPage") == null && pageCnt == 1) || uF.parseToInt((String) request.getAttribute("proPage")) == pageCnt) {%>
									style="color: black;" <%}%>><%=pageCnt%></a>
							</span>
						<% 	if ((uF.parseToInt((String) request.getAttribute("proPage")) - 3) > 1) {%>
								<b>...</b>
						<%	} %>
					<%	} %>

					<%if (i > 1 && i < intproCnt) { %>
						<% if (pageCnt >= (uF.parseToInt((String) request.getAttribute("proPage")) - 2) && pageCnt <= (uF.parseToInt((String) request.getAttribute("proPage")) + 2)) {%>
							<span><a href="javascript:void(0);" onclick="loadMore('<%=pageCnt%>','<%=minLimit%>','<%=request.getAttribute("dataType")%>','<%=request.getAttribute("currUserType")%>');"
								<%if (((String) request.getAttribute("proPage") == null && pageCnt == 1) || uF.parseToInt((String) request.getAttribute("proPage")) == pageCnt) {%>
								style="color: black;" <%}%>><%=pageCnt%></a>
							</span>
						<% 	} %>
					<% } %>

						<%if (i == intproCnt && intproCnt > 1) {
								String strPgCnt = (String) request.getAttribute("proPage");
								String strMinLimit = (String) request.getAttribute("minLimit");
								strPgCnt = (uF.parseToInt(strPgCnt) + 1) + "";
								strMinLimit = (uF.parseToInt(strMinLimit) + 10)
										+ "";
								if (strMinLimit == null) {
									strMinLimit = "0";
								}
								if (strPgCnt == null) {
									strPgCnt = "1";
								}
						%>
						<%if ((uF.parseToInt((String) request.getAttribute("proPage")) + 3) < intproCnt) {%>
							<b>...</b>
						<% } %>
						<span>
							<a href="javascript:void(0);" onclick="loadMore('<%=pageCnt%>','<%=minLimit%>','<%=request.getAttribute("dataType")%>','<%=request.getAttribute("currUserType")%>');"
							<%if (uF.parseToInt((String) request.getAttribute("proPage")) == pageCnt) {%>
									style="color: black;" <%}%>><%=pageCnt%></a>
						</span> 
						<span style="color: lightgray;"> 
							<%if (uF.parseToInt((String) request.getAttribute("proPage")) < pageCnt) {%>
								<a href="javascript:void(0);" onclick="loadMore('<%=strPgCnt%>','<%=strMinLimit%>','<%=request.getAttribute("dataType")%>','<%=request.getAttribute("currUserType")%>');"><%="Next >"%></a>
							<%} else { %> 
								<b><%="Next >"%></b> 
							<%}%> 
						</span>
						<% } %>
						<% } %>
					</div>
					<% } %>
				</div>
                      
	
				<div style="font-size: 16px; font-weight: bold; border-bottom: 1px solid lightgray;">Reviewer:</div> <!-- margin: 20px 0px 10px 20px;  -->
				
                     <div class="leftbox reportWidth">
                     <%
	                    Map<String, Map<String, String>> appraisalDetailsReviewer = (Map<String, Map<String, String>>)request.getAttribute("appraisalDetailsReviewer");
	                 	List<String> appraisalIdListReviewer = (List<String>) request.getAttribute("appraisalIdListReviewer");
	                 	Map<String, List<String>> hmReviewerEmp = (Map<String, List<String>>)request.getAttribute("hmReviewerEmp");
	                 	//System.out.println("Appr.jsp/773--appraisalDetailsReviewer="+appraisalDetailsReviewer);
	                 	//System.out.println("Appr.jsp/773--appraisalIdListReviewer="+appraisalIdListReviewer);
	                 	//System.out.println("Appr.jsp/773--hmReviewerEmp="+hmReviewerEmp);
					%>
                     
                       <div>
                           <ul style="margin-bottom: 50px">
                               <%
                               	int empCnt = 0;
                                   //String strSessionEmpId = (String) session.getAttribute(IConstants.EMPID);
                                 // System.out.println("Appr.jsp/783--appraisalIdListReviewer ::: "+appraisalIdListReviewer);
                                 // System.out.println("Appr.jsp/783--appraisalStatusMp ::: "+appraisalStatusMp);
                                   for (int j = 0; j < appraisalIdListReviewer.size(); j++) {
                                   	
                                	   
                                	Map<String, Map<String, String>> userTypeMp = appraisalStatusMp.get(appraisalIdListReviewer.get(j));
                                   	if (userTypeMp == null){
                                   		userTypeMp = new HashMap<String, Map<String, String>>();
                                   	}	
                                   	
                                   //	System.out.println("Appr.jsp/789--hmAppraisalMp ::: "+appraisalDetailsReviewer.get(appraisalIdListReviewer.get(j)));	
                                   	Map<String, String> hmAppraisalMp = appraisalDetailsReviewer.get(appraisalIdListReviewer.get(j));
                                   	List<String> employeeList = hmReviewerEmp.get(appraisalIdListReviewer.get(j));
                                   	String appId = hmAppraisalMp.get("ID");
                           			String appFreqId = hmAppraisalMp.get("APP_FREQ_ID");
                                   	List<List<String>> listAppSections = hmAppraisalSectins.get(appId);
                                   //	System.out.println("Appr.jsp/795--hmAppraisalSectins ::: "+hmAppraisalSectins);
                                   		for (int i = 0; employeeList != null && i < employeeList.size(); i++) {
                                   			//List<String> sectionIDList = hmExistSectionID.get(employeeList.get(i) + "_"+ appraisalIdListReviewer.get(j));
                                   		//	System.out.println("Appr.jsp/8111--appraisalIdListReviewer ::: "+appraisalIdListReviewer.get(j));
                                   			Map<String, List<String>> hmRemainOrientDetails = hmRemainOrientDetailsAppWise.get(appraisalIdListReviewer.get(j));
                                   			if (hmRemainOrientDetails == null)
                                   				hmRemainOrientDetails = new HashMap<String, List<String>>();
                                   
                                   			Map<String, List<String>> hmRemainOrientDetailsForSelf = hmRemainOrientDetailsForSelfAppWise.get(appraisalIdListReviewer.get(j));
                                   			if (hmRemainOrientDetailsForSelf == null)
                                   				hmRemainOrientDetailsForSelf = new HashMap<String, List<String>>();
                                   
                                   			Map<String, List<String>> hmRemainOrientDetailsForPeer = hmRemainOrientDetailsForPeerAppWise.get(appraisalIdListReviewer.get(j));
                                   			if (hmRemainOrientDetailsForPeer == null)
                                   				hmRemainOrientDetailsForPeer = new HashMap<String, List<String>>();
                                   
                                   			Map<String, List<String>> hmExistOrientTypeAQA = hmExistOrientTypeAQAAppWise.get(appraisalIdListReviewer.get(j));
                                   			if (hmExistOrientTypeAQA == null)
                                   				hmExistOrientTypeAQA = new HashMap<String, List<String>>();
                                   			//System.out.println("App/826--listExistOrientTypeInAQA ::: "+ hmExistOrientTypeAQA);
                                   			String role = "Reviewer";
                                   
                                   			double dblRate = uF.parseToDouble(hmScoreAggregateMap.get(appraisalIdListReviewer.get(j) + "_" + employeeList.get(i))) / 20; %>
                               <li class="list">
                               	<div class="row row_without_margin">
                               		<div class="col-lg-1 col_no_padding ">
                               			<% if (dblRate >= 3.5) {%>
                                       		 <div style="background-color: #00FF00; padding: 0px 3px; margin: 5px; font-family: digital; font-size: 14px; border-radius: 4px 4px 4px 4px; float: left;">
                                       		 	<%=("" + dblRate != null) ? uF.formatIntoOneDecimal(dblRate) : ""%>/5
                                       		 </div>
                                       <% } else if (dblRate < 3.5 && dblRate >= 2.5) {%>
	                                        <div style="background-color: #FFFF00; padding: 0px 3px; margin: 5px; font-family: digital; font-size: 14px; border-radius: 4px 4px 4px 4px; float: left;">
	                                        	<%=("" + dblRate != null) ? uF.formatIntoOneDecimal(dblRate) : ""%>/5
	                                        </div>
                                       <% } else if (dblRate > 0) { //#FF0000;%>
	                                        <div style="background-color: #FF0000;color: #fff; padding: 0px 3px; margin: 5px; font-family: digital; font-size: 14px; border-radius: 4px 4px 4px 4px; float: left;">
	                                        		<%=("" + dblRate != null) ? uF.formatIntoOneDecimal(dblRate) : ""%>/5
	                                        </div>
                                       <% } %>
                               		</div>
                               		<div class="col-lg-9 col_no_padding">
                               			<span><b><%=hmAppraisalMp.get("APPRAISAL")%></b>
                                       for <%
                                           if (strSessionEmpId.equals(employeeList.get(i))) {
                                           %> You
                                       <%
                                           } else {
                                           %> <%=hmEmpName.get(employeeList.get(i))%> <%
                                           }
                                           %>
                                       [Role-<%=role%>] (<%=uF.showData(hmAppraisalMp.get("APP_FREQ_NAME"),"-")%>) <br/>
                                        <i><%=hmAppraisalMp.get("REVIEW_TYPE")%>,<%=hmAppraisalMp.get("ORIENT")%>&deg;<%-- , <%=hmAppraisalMp.get("FREQUENCY")%>,<%=hmAppraisalMp.get("FREQ_TO")%> --%></i> </span>
                               		</div>
                               		<div class="col-lg-2 col_no_padding pull-right">
                                       	<%if(dataType != null && dataType.equals("C")) { %>
                                       		<i class="fa fa-circle" aria-hidden="true" style="color: #54aa0d;" title="Waiting"></i>
                                       	<% } else { %>
											<i class="fa fa-circle" aria-hidden="true" style="color: #b71cc5;"></i>
                                       	<% } %>
                                       
                                       	<a href="javascript: void(0);" onclick="reviewerViewPoup('<%=hmAppraisalMp.get("ID")%>','<%=employeeList.get(i)%>','<%=""%>','<%=""%>','<%=role%>','<%=hmAppraisalMp.get("APP_FREQ_ID")%>', '<%=dataType %>')">View </a>
    
                               		</div>
                               		
                               	</div>
                               </li>
                               <%
                               empCnt++;
                                }
                             }
                             
                                   //System.out.println("App.jsp/882--appraisalIdListReviewer ===>> " + appraisalIdListReviewer);
                                   //System.out.println("appraisalIdListReviewer ===>> " + appraisalIdListReviewer.size());
                              if (empCnt == 0 || appraisalIdListReviewer == null || appraisalIdListReviewer.isEmpty() || appraisalIdListReviewer.size() == 0) { %>
                               <li>
                                   <div class="nodata msg">
                                       <span> No Review Available.</span>
                                   </div>
                               </li>
                               <% } %>
                           </ul>
                       </div>
                       
                       <%
                      // System.out.println("APP.jsp/895--appraisalIdListReviewer="+appraisalIdListReviewer);
                       if (appraisalIdListReviewer != null && !appraisalIdListReviewer.isEmpty()&& appraisalIdListReviewer.size() == 10) { %>
						<div style="text-align: center;clear: both;width: 100%;">

					<%
						int intproCnt = uF.parseToInt(proCountReviewer);
						int pageCnt = 0;
						int minLimit = 0;

						for (int i = 1; i <= intproCnt; i++) {
							minLimit = pageCnt * 10;
							pageCnt++;
					%>
					<%
						if (i == 1) {
							String strPgCnt = (String) request.getAttribute("proPageReviewer");
							String strMinLimit = (String) request.getAttribute("minLimitReviewer");
							if (uF.parseToInt(strPgCnt) > 1) {
								strPgCnt = (uF.parseToInt(strPgCnt) - 1) + "";
								strMinLimit = (uF.parseToInt(strMinLimit) - 10)
										+ "";
							}
							if (strMinLimit == null) {
								strMinLimit = "0";
							}
							if (strPgCnt == null) {
								strPgCnt = "1";
							}
					%>
							<span style="color: lightgray;"> 
							<% if (uF.parseToInt((String) request.getAttribute("proPageReviewer")) > 1) { %>
								<a href="javascript:void(0);" onclick="loadMoreReviewer('<%=strPgCnt%>','<%=strMinLimit%>','<%=request.getAttribute("dataType")%>','<%=request.getAttribute("currUserType")%>');"> <%="< Prev"%></a>
							<% } else { %>
								<b><%="< Prev"%></b>
							<% } %> 
							</span> 
						<span>
							<a href="javascript:void(0);" onclick="loadMoreReviewer('<%=pageCnt%>','<%=minLimit%>','<%=request.getAttribute("dataType")%>','<%=request.getAttribute("currUserType")%>');"
							<%if (((String) request.getAttribute("proPageReviewer") == null && pageCnt == 1) || uF.parseToInt((String) request.getAttribute("proPageReviewer")) == pageCnt) {%>
								style="color: black;" <%}%>><%=pageCnt%></a>
						</span>
					<% 	if ((uF.parseToInt((String) request.getAttribute("proPageReviewer")) - 3) > 1) {%>
							<b>...</b>
					<%	} %>
				<%	} %>

				<%if (i > 1 && i < intproCnt) { %>
					<% if (pageCnt >= (uF.parseToInt((String) request.getAttribute("proPageReviewer")) - 2) && pageCnt <= (uF.parseToInt((String) request.getAttribute("proPageReviewer")) + 2)) {%>
						<span><a href="javascript:void(0);" onclick="loadMoreReviewer('<%=pageCnt%>','<%=minLimit%>','<%=request.getAttribute("dataType")%>','<%=request.getAttribute("currUserType")%>');"
							<%if (((String) request.getAttribute("proPageReviewer") == null && pageCnt == 1) || uF.parseToInt((String) request.getAttribute("proPageReviewer")) == pageCnt) {%>
							style="color: black;" <%}%>><%=pageCnt%></a>
						</span>
					<% 	} %>
				<% } %>

					<%if (i == intproCnt && intproCnt > 1) {
							String strPgCnt = (String) request.getAttribute("proPageReviewer");
							String strMinLimit = (String) request.getAttribute("minLimitReviewer");
							strPgCnt = (uF.parseToInt(strPgCnt) + 1) + "";
							strMinLimit = (uF.parseToInt(strMinLimit) + 10)
									+ "";
							if (strMinLimit == null) {
								strMinLimit = "0";
							}
							if (strPgCnt == null) {
								strPgCnt = "1";
							}
					%>
					<%if ((uF.parseToInt((String) request.getAttribute("proPageReviewer")) + 3) < intproCnt) {%>
						<b>...</b>
					<% } %>
					<span>
						<a href="javascript:void(0);" onclick="loadMoreReviewer('<%=pageCnt%>','<%=minLimit%>','<%=request.getAttribute("dataType")%>','<%=request.getAttribute("currUserType")%>');"
						<%if (uF.parseToInt((String) request.getAttribute("proPageReviewer")) == pageCnt) {%>
								style="color: black;" <%}%>><%=pageCnt%></a>
					</span> 
					<span style="color: lightgray;"> 
						<%if (uF.parseToInt((String) request.getAttribute("proPageReviewer")) < pageCnt) {%>
							<a href="javascript:void(0);" onclick="loadMoreReviewer('<%=strPgCnt%>','<%=strMinLimit%>','<%=request.getAttribute("dataType")%>','<%=request.getAttribute("currUserType")%>');"><%="Next >"%></a>
						<%} else { %> 
							<b><%="Next >"%></b> 
						<% } %> 
					</span>
					<% } %>
					<% } %>
				</div>
				<% } %>
			</div>
			
                        
				<div class="custom-legends">
					<div class="custom-legend pullout"><div class="legend-info">Waiting for workflow(Workflow)</div></div>
					<div class="custom-legend pending"><div class="legend-info">Not filled yet(Myself)</div></div>
					<div class="custom-legend approved"><div class="legend-info">Completed(Myself)</div></div>
					<div class="custom-legend denied"><div class="legend-info">Expired</div></div>
					<div class="custom-legend re_submit"><div class="legend-info">Waiting for completion(Workflow)</div></div>
				</div>
				
			<!-- </div> -->
		</div>
	
<div class="modal" id="modalInfo" role="dialog">
    <div class="modal-dialog">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title"></h4>
            </div>
            <div class="modal-body" style="height:400px;overflow-y:auto;padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>

<div class="modal" id="modalInfo1" role="dialog">
    <div class="modal-dialog modal-dialog1">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title modal-title1"></h4>
            </div>
            <div class="modal-body" id="modal-body1" style="height:400px;overflow-y:auto;padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton1" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>


<script>

function submitForm() {
	var dataType = '<%=dataType%>';
	var currUserType = '<%=currUserType%>';
	var strSearch = document.getElementById("strSearchJob").value;
	var action = 'Appraisal.action?strSearchJob='+strSearch+'&dataType='+dataType+'&currUserType='+currUserType;
	$("#appraisalResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: action,
		success: function(result){
        	$("#appraisalResult").html(result);
   		}
	});
}


function loadMore(proPage, minLimit,dataType,currUserType) {
	
	//alert("loadMore ==>dataType==>"+dataType+"==>proPage==>"+proPage+"==>minLimit==>"+minLimit);
	$("#appraisalResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({ 
		type : 'POST',
		url: 'Appraisal.action?dataType='+dataType+'&currUserType='+currUserType+"&proPage="+proPage+"&minLimit="+minLimit,
		cache: true,
		success: function(result){
			//alert("result1==>"+result);
			$("#appraisalResult").html(result);
   		}
	});
}


function loadMoreReviewer(proPageReviewer, minLimitReviewer, dataType, currUserType) {
	
	//alert("loadMore ==>dataType==>"+dataType+"==>proPage==>"+proPage+"==>minLimit==>"+minLimit);
	$("#appraisalResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({ 
		type : 'POST',
		url: 'Appraisal.action?dataType='+dataType+'&currUserType='+currUserType+"&proPageReviewer="+proPageReviewer+"&minLimitReviewer="+minLimitReviewer,
		cache: true,
		success: function(result){
			//alert("result1==>"+result);
			$("#appraisalResult").html(result);
   		}
	});
}

</script>
