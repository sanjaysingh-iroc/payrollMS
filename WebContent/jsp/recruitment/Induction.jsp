<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>


<style>
	.label {
		font-size: 13px;
	}
	
</style>
	
	<script type="text/javascript" charset="utf-8">
    function openOnboardingForm(depart_id,candidateId,recruitId) {
    //alert("candidateId ===> "+ candidateId);
    	window.location = "OnboardingFromCandidate.action?depart_id="+ depart_id +"&candidateId="+candidateId+"&recruitId="+recruitId;
    }
    
    function resendOnboardFormToCandidate(depart_id,candidateId,recruitId) {
    	//alert("candidateId ===> "+ candidateId);
    		getContent('resendOnboardSpan_'+recruitId+'_'+candidateId, "ResendOnboardingFormToCandidate.action?depart_id="+ depart_id +"&candidateId="+candidateId+"&recruitId="+recruitId);
    }
    
     
  //====start parvez on 09-07-2021==== 
    //function openCandidateProfilePopup(CandID,recruitId) { 
	  function openCandidateProfilePopup(CandID,recruitId,apptype) {
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
        	if($(window).width() >= 900){
           		$(".modal-dialog").width(900);
           	} 
        	$.ajax({
    			//url : "ApplyLeavePopUp.action", 
    			//====start parvez on 09-07-2021===== 
    			//url :"CandidateMyProfilePopup.action?CandID="+CandID+"&recruitId="+recruitId, 
    			url :"CandidateMyProfilePopup.action?CandID="+CandID+"&recruitId="+recruitId+"&form=A&apptype="+apptype,
    			//====end parvez on 09-07-2021===== 
    			cache : false,
    			success : function(data) {
    				$(dialogEdit).html(data);
    			}
    		});
    
    	 }
     
   /*   function submitForm(){
    	document.frmInduction.proPage.value = '';
    	document.frmInduction.minLimit.value = '';
    	document.frmInduction.submit();
    }
    
    function loadMore(proPage, minLimit) {
    	document.frmInduction.proPage.value = proPage;
    	document.frmInduction.minLimit.value = minLimit;
    	document.frmInduction.submit();
    } */
     
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
<%-- <jsp:include page="../common/SubHeader.jsp">
    <jsp:param value="On-board" name="title" /> 
    </jsp:include> --%>

			    <%
			        UtilityFunctions uF=new UtilityFunctions();
			        String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
			    %>	 
                <div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
                        <%-- <%
                            String sbData = (String) request.getAttribute("sbData");
                            String strSearchJob = (String) request.getAttribute("strSearchJob");
                            String appliSourceType = (String) request.getAttribute("appliSourceType");
                        %> --%>

                        <%
                            List<String> recruitmentIdList = (List<String>)request.getAttribute("recruitmentIdList");
                            
                            Map<String, Map<String, String>> hmcandiStarRecruitwise = (Map<String, Map<String, String>>)request.getAttribute("hmcandiStarRecruitwise");
                            if(hmcandiStarRecruitwise == null) hmcandiStarRecruitwise = new HashMap<String, Map<String, String>>();	
                            
                            Map<String, String> hmJobCodeName = (Map<String, String>)request.getAttribute("hmJobCodeName");
                            Map<String, String> hmJobPriority = (Map<String, String>)request.getAttribute("hmJobPriority");
                            Map<String, List<List<String>>> hmTodayInduction = (Map<String, List<List<String>>>)request.getAttribute("hmTodayInduction");
                            Map<String, List<List<String>>> hmTomorrowInduction = (Map<String, List<List<String>>>)request.getAttribute("hmTomorrowInduction");
                            Map<String, List<List<String>>> hmDayAfterTomorrowInduction = (Map<String, List<List<String>>>)request.getAttribute("hmDayAfterTomorrowInduction");
                            Map<String, List<List<String>>> hmPendingInduction = (Map<String, List<List<String>>>)request.getAttribute("hmPendingInduction");
                            Map<String, List<List<String>>> hmOnBoardedInduction = (Map<String, List<List<String>>>)request.getAttribute("hmOnBoardedInduction");// Created By Dattatray Date:05-July-2021
                            Map<String, String> hmTodayIndCount = (Map<String, String>)request.getAttribute("hmTodayIndCount");
                            Map<String, String> hmTomorrowIndCount = (Map<String, String>)request.getAttribute("hmTomorrowIndCount");
                            Map<String, String> hmDayAfterTomorrowIndCount = (Map<String, String>)request.getAttribute("hmDayAfterTomorrowIndCount");
                            Map<String, String> hmPendingIndCount = (Map<String, String>)request.getAttribute("hmPendingIndCount");
                            Map<String, String> hmOnBoardedCount = (Map<String, String>)request.getAttribute("hmOnBoardedCount");// Created By Dattatray Date:28-June-2021
                            
                            Map<String, String> hmJobStatus = (Map<String, String>)request.getAttribute("hmJobStatus");
                            if(hmJobStatus == null) hmJobStatus = new HashMap<String, String>();
                            Map<String, String> hmJobTitle = (Map<String, String>) request.getAttribute("hmJobTitle");
                            if(hmJobTitle == null) hmJobTitle = new HashMap<String, String>();
                            
                            Map<String, String> hmCandiImage = (Map<String, String>) request.getAttribute("hmCandiImage");
                            if(hmJobTitle == null) hmCandiImage = new HashMap<String, String>();
                            String strVm = (String)request.getAttribute("strVm");
                            Map<String, Map<String, Map<String, String>>> hmSource = (Map<String, Map<String, Map<String, String>>>) request.getAttribute("hmSource");
                            if(hmSource == null) hmSource = new HashMap<String, Map<String,Map<String,String>>>();
                            %>
                        <% for(int a=0; recruitmentIdList != null && a<recruitmentIdList.size(); a++){
                            String recruitId = recruitmentIdList.get(a);
                            
                            Map<String, String> hmCandiStars = hmcandiStarRecruitwise.get(recruitId);
                            if(hmCandiStars == null) hmCandiStars = new HashMap<String, String>();
                            
                            String priorityClass = "";
                            if (uF.parseToInt(hmJobPriority.get(recruitId)) == 1) {
                            	priorityClass ="high";
                            } else if (uF.parseToInt(hmJobPriority.get(recruitId)) == 2) {
                            	priorityClass ="medium";
                            } else {
                            	priorityClass ="low";
                            }
                            
                            boolean closeFlag = false;
                            if(uF.parseToBoolean(hmJobStatus.get(recruitId))){
                            	closeFlag = true;
                            }
                            Map<String, Map<String, String>> hmCandidate = hmSource.get(recruitId);
                            if(hmCandidate == null) hmCandidate = new HashMap<String, Map<String,String>>();
                            %>
                            
                            
                        <!-- <div class="dashboard_linksholder " style="float: none; width: 98%; line-height:30px;"> -->
                       <!--  <div style="margin: 10px 0px 0px 0px; float: left; width: 100%; line-height:30px;">
                            <div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE; margin-top: 10px;margin: 10px 0px 0px 0px;">
                                
                                /.box-header
                                <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
                                    <div class="inductionContent" style="height:auto;">
                                        
                                    </div>
                                </div>
                                /.box-body
                            </div>
                        </div> -->
                        <div class="box-tools pull-right">
	  						<span class="label label-warning">Today(<%=uF.showData(hmTodayIndCount.get(recruitId),"0")%>)</span>		
	  						<span class="label label-info">Tomorrow(<%=uF.showData(hmTomorrowIndCount.get(recruitId),"0")%>)</span>
	  						<span class="label label-success">Day after tomorrow(<%=uF.showData(hmDayAfterTomorrowIndCount.get(recruitId),"0")%>)</span>
	  						<span class="label label-success">Pending(<%=uF.showData(hmPendingIndCount.get(recruitId),"0")%>)</span>
	  						<span class="label label-success">Onboarded(<%=uF.showData(hmOnBoardedCount.get(recruitId),"0")%>)</span><!-- Created By Dattatray Date:28-June-2021 -->
			            </div><div class="box-header " style="padding-top: 5px;padding-bottom: 5px;">
						            
                                 <div class="attendance">
                                 	
                                            <h4 style="padding-top: 20px;">Today</h4>
                                            <table class="table table-bordered" width="100%" cellspacing="0" cellpadding="2" style="margin-bottom :15px;">
                                                <!-- class="display tb_style" -->
                                                <tbody>
                                                    <tr class="darktable">
                                                        <td style="text-align: center;">Candidate Name</td>
                                                        <td style="text-align: center;">Job Designation</td>
                                                        <td style="text-align: center;">Work Location</td>
                                                        <td style="text-align: center;">Department </td>
                                                        <td style="text-align: center;">Supervisor</td>
                                                        <td style="text-align: center;">Joining Date</td>
                                                        <td style="text-align: center;">Onboarding</td>
                                                    </tr>
                                                    <%
                                                        //int w = 1;
                                                        if(hmTodayInduction != null && !hmTodayInduction.isEmpty()) {
                                                        	List<List<String>> alInductionToday = hmTodayInduction.get(recruitId);
                                                        		for (int i = 0; alInductionToday != null && i < alInductionToday.size(); i++) {
                                                        			/* if(w == 11 && uF.parseToInt(strVm) == 0){
                                                        				break;
                                                        			} */
                                                        			List<String> innerList = alInductionToday.get(i);
                                                        			
                                                        			String candidateID = innerList.get(7);
                                                        			String strStars = hmCandiStars.get(candidateID);
                                                        			Map<String, String> hmSourceDetails = hmCandidate.get(candidateID);
                                                        			if(hmSourceDetails == null) hmSourceDetails = new HashMap<String, String>();
                                                        %>
                                                        
                                                    <tr>
                                                        <td style="text-align: left; vertical-align: text-top;">
                                                            <div style="float: left; margin: 2px 10px 0px 0px">  
                                                                <%if(docRetriveLocation == null) { %>
                                                                <img height="40" width="40" border="0" class="lazy img-circle" id="profilecontainerimg<%=recruitId+"_"+innerList.get(7) %>" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + hmCandiImage.get(innerList.get(7)) %>" />
                                                                <%} else { %>
                                                                <img height="40" width="40" border="0" class="lazy img-circle" id="profilecontainerimg<%=recruitId+"_"+innerList.get(7) %>" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_CANDIDATE+"/"+IConstants.I_IMAGE+"/"+innerList.get(7)+"/"+IConstants.I_100x100+"/"+hmCandiImage.get(innerList.get(7))%>" />
                                                                <%} %> 
                                                            </div>
                                                            <div style="float: left;line-height: 2.3;">
                                                                <%-- <%if(!closeFlag){ %>
                                                                <div style="float:left;">
                                                                    <!-- ====start parvez on 09-07-2021===== -->
                                                                    <a href="javascript:void(0);" onclick="openCandidateProfilePopup('<%=innerList.get(7) %>','<%=recruitId %>','today')"> <%=innerList.get(0)%> </a>
                                                                   <a href="javascript:void(0);" onclick="openCandidateProfilePopup('<%=innerList.get(7) %>','<%=recruitId %>',')"> <%=innerList.get(0)%> </a> 
                                                               <!-- ====end parvez on 09-07-2021===== -->
                                                                </div>
                                                                <div id="downloadOfferLatterSpan_<%=recruitId +"_"+ candidateID %>" style="float: left;margin:2px 0px 0px 0px;">
                                                                    <a class="pdf" title="Download Offer letter" href="CandiOfferLetterPreview.action?candidateId=<%=candidateID%>&recruitId=<%=recruitId %>">Pdf</a>
                                                                </div>
                                                                <%} else { %>
                                                                <%=innerList.get(0)%>
                                                                <%} %> --%>
                                                           
                                                           <!-- ====start parvez on 21-10-2021===== -->     
                                                                <div style="float:left;">
                                                                    <a href="javascript:void(0);" onclick="openCandidateProfilePopup('<%=innerList.get(7) %>','<%=recruitId %>','today')"> <%=innerList.get(0)%> </a>
                                                                   <%-- <a href="javascript:void(0);" onclick="openCandidateProfilePopup('<%=innerList.get(7) %>','<%=recruitId %>',')"> <%=innerList.get(0)%> </a> --%> 
                                                                </div>
                                                                <div id="downloadOfferLatterSpan_<%=recruitId +"_"+ candidateID %>" style="float: left;margin:2px 0px 0px 0px;">
                                                                    <a class="pdf" title="Download Offer letter" href="CandiOfferLetterPreview.action?candidateId=<%=candidateID%>&recruitId=<%=recruitId %>">Pdf</a>
                                                                </div>
                                                          <!-- ====end parvez on 21-10-2021===== -->
                                                                
                                                                <br/>
                                                                <div style="float:left; margin-top: -6px;line-height: 16px;">
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
                                                        <td><%=innerList.get(1)%></td>
                                                        <td><%=innerList.get(2)%></td>
                                                        <td><%=innerList.get(3)%></td>
                                                        <td><%=innerList.get(4)%></td>
                                                        <td><%=innerList.get(5)%></td>
                                                        <td><%=innerList.get(6)%></td>
                                                    </tr>
                                                    <%
                                                        } }
                                                        
                                                        if(hmTodayInduction.get(recruitId) == null || hmTodayInduction.get(recruitId).isEmpty()) {
                                                        %>
                                                    <tr>
                                                        <td colspan="7">
                                                            <div class="nodata msg"> <span>No Applications for Today </span> </div>
                                                        </td>
                                                    </tr>
                                                    <%-- <%
                                                        } else{
                                                        	if(w == 11){
                                                        %>
                                                    <tr class="lighttable">
                                                        <td colspan="7" style="text-align: right;"><a style="padding-right:5px" href="Induction.action?strVm=1&strDashboardRequest=<%=recruitId%>" >view more....</a></td>
                                                    </tr>
                                                    <%	}
                                                        }
                                                        %> --%>
                                                   
                                                   <% } %>
                                                </tbody>
                                            </table>
                                            <h4 style="">Tomorrow </h4>
                                            <table class="table table-bordered" width="100%" cellspacing="0" cellpadding="2" style="margin-bottom :15px;">
                                                <!-- class="display tb_style" -->
                                                <tbody>
                                                    <tr class="darktable">
                                                        <td style="text-align: center;">Candidate Name</td>
                                                        <td style="text-align: center;">Job Designation</td>
                                                        <td style="text-align: center;">Work Location</td>
                                                        <td style="text-align: center;">Department </td>
                                                        <td style="text-align: center;">Supervisor</td>
                                                        <td style="text-align: center;">Joining Date</td>
                                                        <td style="text-align: center;">Onboarding</td>
                                                    </tr>
                                                    <%
                                                        //int x = 1;
                                                        if(hmTomorrowInduction != null && !hmTomorrowInduction.isEmpty()){
                                                        	List<List<String>> alInductionTomorrow = hmTomorrowInduction.get(recruitId);
                                                        		for (int i = 0; alInductionTomorrow != null && i < alInductionTomorrow.size(); i++) {
                                                        			/* if(x == 11 && uF.parseToInt(strVm) == 0){
                                                        				break;
                                                        			} */
                                                        			List<String> innerList = alInductionTomorrow.get(i);
                                                        			
                                                        			String candidateID = innerList.get(7);
                                                        			String strStars = hmCandiStars.get(candidateID);
                                                        			Map<String, String> hmSourceDetails = hmCandidate.get(candidateID);
                                                        			if(hmSourceDetails == null) hmSourceDetails = new HashMap<String, String>();
                                                        %>
                                                    <tr>
                                                        <td style="text-align: left; vertical-align: text-top;">
                                                            <div style="float: left; margin: 2px 10px 0px 0px">
                                                                <!-- border: 1px solid #000; -->
                                                                <%if(docRetriveLocation == null) { %>
                                                                <img height="40" width="40" border="0" class="lazy img-circle" id="profilecontainerimg<%=recruitId+"_"+innerList.get(7) %>" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + hmCandiImage.get(innerList.get(7)) %>" />
                                                                <%} else { %>
                                                                <img height="40" width="40" border="0" class="lazy img-circle" id="profilecontainerimg<%=recruitId+"_"+innerList.get(7) %>" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_CANDIDATE+"/"+IConstants.I_IMAGE+"/"+innerList.get(7)+"/"+IConstants.I_100x100+"/"+hmCandiImage.get(innerList.get(7))%>" />
                                                                <%} %> 
                                                            </div>
                                                            <div style="float: left;line-height: 2.3;">
                                                                <%-- <%if(!closeFlag){ %>
                                                                <div style="float:left;">
                                                                    <!-- ====start parvez on 09-07-2021===== -->
                                                                    <a href="javascript:void(0);" onclick="openCandidateProfilePopup('<%=innerList.get(7) %>','<%=recruitId %>')"> <%=innerList.get(0)%> </a>
                                                                    <a href="javascript:void(0);" onclick="openCandidateProfilePopup('<%=innerList.get(7) %>','<%=recruitId %>','tomorrow')"> <%=innerList.get(0)%> </a>
                                                                <!-- ====end parvez on 09-07-2021===== -->
                                                                </div>
                                                                <div id="downloadOfferLatterSpan_<%=recruitId +"_"+ candidateID %>" style="float: left;margin:2px 0px 0px 0px;">
                                                                    <a class="fa fa-file-pdf-o" title="Download Offer letter" href="CandiOfferLetterPreview.action?candidateId=<%=candidateID%>&recruitId=<%=recruitId %>"></a>
                                                                </div>
                                                                <%} else { %>
                                                                <%=innerList.get(0)%>
                                                                <%} %> --%>
                                                             
                                                           <!-- ====start parvez on 21-10-2021===== --> 
                                                                <div style="float:left;">
                                                                    
                                                                    <%-- <a href="javascript:void(0);" onclick="openCandidateProfilePopup('<%=innerList.get(7) %>','<%=recruitId %>')"> <%=innerList.get(0)%> </a> --%>
                                                                    <a href="javascript:void(0);" onclick="openCandidateProfilePopup('<%=innerList.get(7) %>','<%=recruitId %>','tomorrow')"> <%=innerList.get(0)%> </a>
                                                                
                                                                </div>
                                                                <div id="downloadOfferLatterSpan_<%=recruitId +"_"+ candidateID %>" style="float: left;margin:2px 0px 0px 0px;">
                                                                    <a class="fa fa-file-pdf-o" title="Download Offer letter" href="CandiOfferLetterPreview.action?candidateId=<%=candidateID%>&recruitId=<%=recruitId %>"></a>
                                                                </div>
                                                          <!-- ====end parvez on 21-10-2021===== -->
                                                                <br/>
                                                                <div style="float:left; margin-top: -6px;line-height:16px;">
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
                                                        <td><%=innerList.get(1)%></td>
                                                        <td><%=innerList.get(2)%></td>
                                                        <td><%=innerList.get(3)%></td>
                                                        <td><%=innerList.get(4)%></td>
                                                        <td><%=innerList.get(5)%></td>
                                                        <td><%=innerList.get(6)%></td>
                                                    </tr>
                                                    <%
                                                        } 
                                                        }
                                                        if(hmTomorrowInduction.get(recruitId) == null || hmTomorrowInduction.get(recruitId).isEmpty()){
                                                        %>
                                                    <tr>
                                                        <td colspan="7">
                                                            <div class="nodata msg"><span>No Applications for Tomorrow </span> </div>
                                                        </td>
                                                    </tr>
                                                   <%--  <%
                                                        } else{
                                                        	if(x == 11){
                                                        %>
                                                    <tr class="lighttable">
                                                        <td colspan="7" style="text-align: right;"><a style="padding-right:5px" href="Induction.action?strVm=1&strDashboardRequest=<%=recruitId%>" >view more....</a></td>
                                                    </tr>
                                                    <%	}
                                                        }
                                                        %> --%>
                                                  
                                                  <% } %>
                                                </tbody>
                                            </table>
                                            <h4 style="">Days after tomorrow</h4>
                                            <table class="table table-bordered" width="100%" cellspacing="0" cellpadding="2" style="margin-bottom :15px;">
                                                <!-- class="display tb_style" -->
                                                <tbody>
                                                    <tr class="darktable">
                                                        <td style="text-align: center;">Candidate Name</td>
                                                        <td style="text-align: center;">Job Designation</td>
                                                        <td style="text-align: center;">Work Location</td>
                                                        <td style="text-align: center;">Department </td>
                                                        <td style="text-align: center;">Supervisor</td>
                                                        <td style="text-align: center;">Joining Date</td>
                                                        <td style="text-align: center;">Onboarding</td>
                                                    </tr>
                                                    <%
                                                        //int y = 1;
                                                        if(hmDayAfterTomorrowInduction != null && !hmDayAfterTomorrowInduction.isEmpty()){
                                                        	List<List<String>> alInductionRest = hmDayAfterTomorrowInduction.get(recruitId);
                                                        	for (int i = 0; alInductionRest != null && i < alInductionRest.size(); i++) {
                                                        		/* if(y == 11 && uF.parseToInt(strVm) == 0){
                                                        			break;
                                                        		} */
                                                        		List<String> innerList = alInductionRest.get(i);
                                                        		
                                                        		String candidateID = innerList.get(7);
                                                        		String strStars = hmCandiStars.get(candidateID);
                                                        		Map<String, String> hmSourceDetails = hmCandidate.get(candidateID);
                                                        		if(hmSourceDetails == null) hmSourceDetails = new HashMap<String, String>();
                                                        %>
                                                    <tr>
                                                        <td style="text-align: left; vertical-align: text-top;">
                                                            <div style="float: left; margin: 2px 10px 0px 0px">
                                                                <!-- border: 1px solid #000; -->
                                                                <%if(docRetriveLocation == null) { %>
                                                                <img height="40" width="40" border="0" class="lazy img-circle" id="profilecontainerimg<%=recruitId+"_"+innerList.get(7) %>" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + hmCandiImage.get(innerList.get(7)) %>" />
                                                                <%} else { %>
                                                                <img height="40" width="40" border="0" class="lazy img-circle" id="profilecontainerimg<%=recruitId+"_"+innerList.get(7) %>" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_CANDIDATE+"/"+IConstants.I_IMAGE+"/"+innerList.get(7)+"/"+IConstants.I_100x100+"/"+hmCandiImage.get(innerList.get(7))%>" />
                                                                <%} %> 
                                                            </div>
                                                            <div style="float: left;line-height: 2.3;">
                                                                <%-- <%if(!closeFlag){ %>
                                                                <div style="float:left;">
                                                                    <!-- ====start parvez on 09-07-2021===== -->
                                                                    <a href="javascript:void(0);" onclick="openCandidateProfilePopup('<%=innerList.get(7) %>','<%=recruitId %>')"> <%=innerList.get(0)%> </a>
                                                                    <a href="javascript:void(0);" onclick="openCandidateProfilePopup('<%=innerList.get(7) %>','<%=recruitId %>','daysAfterTomorrow')"> <%=innerList.get(0)%> </a>
                                                                <!-- ====end parvez on 09-07-2021===== -->
                                                                </div>
                                                                <div id="downloadOfferLatterSpan_<%=recruitId +"_"+ candidateID %>" style="float: left;margin:2px 0px 0px 0px;">
                                                                    <a class="fa fa-file-pdf-o" title="Download Offer letter" href="CandiOfferLetterPreview.action?candidateId=<%=candidateID%>&recruitId=<%=recruitId %>"></a>
                                                                </div>
                                                                <%} else { %>
                                                                <%=innerList.get(0)%>
                                                                <%} %> --%>
                                                                
                                                         <!-- ====start parvez on 21-10-2021===== -->
                                                                <div style="float:left;">
                                                                    <%-- <a href="javascript:void(0);" onclick="openCandidateProfilePopup('<%=innerList.get(7) %>','<%=recruitId %>')"> <%=innerList.get(0)%> </a> --%>
                                                                    <a href="javascript:void(0);" onclick="openCandidateProfilePopup('<%=innerList.get(7) %>','<%=recruitId %>','daysAfterTomorrow')"> <%=innerList.get(0)%> </a>
                                                                </div>
                                                                <div id="downloadOfferLatterSpan_<%=recruitId +"_"+ candidateID %>" style="float: left;margin:2px 0px 0px 0px;">
                                                                    <a class="fa fa-file-pdf-o" title="Download Offer letter" href="CandiOfferLetterPreview.action?candidateId=<%=candidateID%>&recruitId=<%=recruitId %>"></a>
                                                                </div>
                                                        <!-- ====end parvez on 21-10-2021===== -->
                                                                
                                                                <br/>
                                                                <div style="float:left; margin-top: -6px;line-height: 16px;">
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
                                                        <td><%=innerList.get(1)%></td>
                                                        <td><%=innerList.get(2)%></td>
                                                        <td><%=innerList.get(3)%></td>
                                                        <td><%=innerList.get(4)%></td>
                                                        <td><%=innerList.get(5)%></td>
                                                        <td><%=innerList.get(6)%></td>
                                                    </tr>
                                                    <%
                                                        } 
                                                        }
                                                        if (hmDayAfterTomorrowInduction.get(recruitId) == null || hmDayAfterTomorrowInduction.get(recruitId).isEmpty()) {
                                                        %>
                                                    <tr>
                                                        <td colspan="7">
                                                            <div class="nodata msg"><span>No Future Applications</span> </div>
                                                        </td>
                                                    </tr>
                                                   <%--  <%
                                                        } else{
                                                        	if(y == 11){
                                                        %>
                                                    <tr class="lighttable">
                                                        <td colspan="7" style="text-align: right;"><a style="padding-right:5px" href="Induction.action?strVm=1&strDashboardRequest=<%=recruitId%>" >view more....</a></td>
                                                    </tr>
                                                    <%	}
                                                        }
                                                        %> --%>
                                                        
                                                   <% } %>
                                                </tbody>
                                            </table>
                                            <h4 style="">Pending</h4>
                                            <table class="table table-bordered" width="100%" cellspacing="0" cellpadding="2" style="margin-bottom :15px;">
                                                <!-- class="display tb_style" -->
                                                <tbody>
                                                    <tr class="darktable">
                                                        <td style="text-align: center;">Candidate Name</td>
                                                        <td style="text-align: center;">Job Designation</td>
                                                        <td style="text-align: center;">Work Location</td>
                                                        <td style="text-align: center;">Department </td>
                                                        <td style="text-align: center;">Supervisor</td>
                                                        <td style="text-align: center;">Joining Date</td>
                                                        <td style="text-align: center;">Onboarding</td>
                                                    </tr>
                                                    <%
                                                        //int z = 1;
                                                        if(hmPendingInduction != null && !hmPendingInduction.isEmpty()) {
                                                        	List<List<String>> alexportInductionPending = hmPendingInduction.get(recruitId);
                                                        	for (int i = 0; alexportInductionPending != null && i < alexportInductionPending.size(); i++) {
                                                        		/* if(z == 11 && uF.parseToInt(strVm) == 0){
                                                        			break;
                                                        		} */
                                                        		List<String> innerList = alexportInductionPending.get(i);
                                                        		
                                                        		String candidateID = innerList.get(7);
                                                        		String strStars = hmCandiStars.get(candidateID);
                                                        		Map<String, String> hmSourceDetails = hmCandidate.get(candidateID);
                                                        		if(hmSourceDetails == null) hmSourceDetails = new HashMap<String, String>();
                                                        %>
                                                      
                                                    <tr>
                                                        <td style="text-align: left; vertical-align: text-top;"> 
                                                            <div style="float: left; margin: 2px 10px 0px 0px">
                                                                <!-- border: 1px solid #000; -->
                                                                <%if(docRetriveLocation == null) { %>
                                                                <img height="40" width="40" border="0" class="lazy img-circle" id="profilecontainerimg<%=recruitId+"_"+innerList.get(7) %>" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + hmCandiImage.get(innerList.get(7)) %>" />
                                                                <%} else { %>
                                                                <img height="40" width="40" border="0" class="lazy img-circle" id="profilecontainerimg<%=recruitId+"_"+innerList.get(7) %>" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_CANDIDATE+"/"+IConstants.I_IMAGE+"/"+innerList.get(7)+"/"+IConstants.I_100x100+"/"+hmCandiImage.get(innerList.get(7))%>" />
                                                                <%} %> 
                                                            </div>
                                                            <div style="float: left;line-height: 2.3;">
                                                                <%-- <%if(!closeFlag){ %>
                                                                <div style="float:left;">
                                                                    <!-- ====start parvez on 09-07-2021===== -->
                                                                    <a href="javascript:void(0);" onclick="openCandidateProfilePopup('<%=innerList.get(7) %>','<%=recruitId %>')"> <%=innerList.get(0)%> </a>
                                                                    <a href="javascript:void(0);" onclick="openCandidateProfilePopup('<%=innerList.get(7) %>','<%=recruitId %>','pending')"> <%=innerList.get(0)%> </a>
                                                                <!-- ====end parvez on 09-07-2021===== -->
                                                                </div>
                                                                
                                                                <div id="downloadOfferLatterSpan_<%=recruitId +"_"+ candidateID %>" style="float: left;margin:2px 0px 0px 0px;">
                                                                    <a class="fa fa-file-pdf-o" title="Download Offer letter" href="CandiOfferLetterPreview.action?candidateId=<%=candidateID%>&recruitId=<%=recruitId %>"></a>
                                                                </div>
                                                                <%} else { %>
                                                                <%=innerList.get(0)%>
                                                                <%} %> --%>
                                                                
                                                       <!-- ====start parvez on 21-10-2021===== -->
                                                                <div style="float:left;">
                                                                    <%-- <a href="javascript:void(0);" onclick="openCandidateProfilePopup('<%=innerList.get(7) %>','<%=recruitId %>')"> <%=innerList.get(0)%> </a> --%>
                                                                    <a href="javascript:void(0);" onclick="openCandidateProfilePopup('<%=innerList.get(7) %>','<%=recruitId %>','pending')"> <%=innerList.get(0)%> </a>
                                                                </div>
                                                                
                                                                <div id="downloadOfferLatterSpan_<%=recruitId +"_"+ candidateID %>" style="float: left;margin:2px 0px 0px 0px;">
                                                                    <a class="fa fa-file-pdf-o" title="Download Offer letter" href="CandiOfferLetterPreview.action?candidateId=<%=candidateID%>&recruitId=<%=recruitId %>"></a>
                                                                </div>
                                                      <!-- ====end parvez on 21-10-2021===== -->
                                                      
                                                                <br/>
                                                                <div style="float:left; margin-top: -6px;line-height: 16px;">
                                                                    <span id="Reting<%=candidateID%>" style="margin-right: 5px; line-height: 18px;"><%=strStars %>/5</span>
                                                                    <span id="starPrimaryP<%=recruitId+"_"+candidateID %>" style="margin-left: 5px; line-height: 12px;"></span>
                                                                </div>
                                                                <script type="text/javascript">
                                                                    $('#starPrimaryP'+'<%=recruitId+"_"+candidateID %>').raty({
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
                                                        <td><%=innerList.get(1)%></td>
                                                        <td><%=innerList.get(2)%></td>
                                                        <td><%=innerList.get(3)%></td>
                                                        <td><%=innerList.get(4)%></td>
                                                        <td><%=innerList.get(5)%></td>
                                                        <td><%=innerList.get(6)%></td>
                                                    </tr>
                                                  
                                                    <%
                                                        } }
                                                        if (hmPendingInduction.get(recruitId) == null || hmPendingInduction.get(recruitId).isEmpty()) {
                                                        %>
                                                    <tr>
                                                        <td colspan="7">
                                                            <div class="nodata msg"> <span>No Pending Applications </span> </div>
                                                        </td>
                                                    </tr>
                                                    <%-- <%
                                                        } else{
                                                        	if(z == 11){
                                                        %>
                                                    <tr class="lighttable">
                                                        <td colspan="7" style="text-align: right;"><a style="padding-right:5px" href="Induction.action?strVm=1&strDashboardRequest=<%=recruitId%>" >view more....</a></td>
                                                    </tr>
                                                    <%}
                                                        }
                                                        %> --%>
                                                        
                                                   <% } %>
                                                </tbody>
                                            </table>
                                            <!-- Created By Dattatray Date:28-June-2021 Note : added onboarded section -->
                                            <h4 style="">Onboarded</h4>
                                            <table class="table table-bordered" width="100%" cellspacing="0" cellpadding="2" style="margin-bottom :15px;">
                                                <!-- class="display tb_style" -->
                                                <tbody>
                                                    <tr class="darktable">
                                                        <td style="text-align: center;">Candidate Name</td>
                                                        <td style="text-align: center;">Job Designation</td>
                                                        <td style="text-align: center;">Work Location</td>
                                                        <td style="text-align: center;">Department </td>
                                                        <td style="text-align: center;">Supervisor</td>
                                                        <td style="text-align: center;">Joining Date</td>
                                                    </tr>
                                                    <%
                                                        //int z1 = 1;
                                                   // System.out.print("hmOnBoardedInduction" +hmOnBoardedInduction);
                                                        if(hmOnBoardedInduction != null && !hmOnBoardedInduction.isEmpty()) {
                                                        	List<List<String>> alexportOnBoardedInduction = hmOnBoardedInduction.get(recruitId);
                                                        	for (int i = 0; alexportOnBoardedInduction != null && i < alexportOnBoardedInduction.size(); i++) {
                                                        		/* if(z1 == 11 && uF.parseToInt(strVm) == 0){
                                                        			break;
                                                        		} */
                                                        		List<String> innerList = alexportOnBoardedInduction.get(i);
                                                        		
                                                        		String candidateID = innerList.get(7);
                                                        		//System.out.print("hmCandiStars : "+hmCandiStars);
                                                        		String strStars = hmCandiStars.get(candidateID);
                                                        		Map<String, String> hmSourceDetails = hmCandidate.get(candidateID);
                                                        		if(hmSourceDetails == null) hmSourceDetails = new HashMap<String, String>();
                                                        %>
                                                       
                                                    <tr>
                                                        <td style="text-align: left; vertical-align: text-top;">
                                                            <div style="float: left; margin: 2px 10px 0px 0px">
                                                                <!-- border: 1px solid #000; -->
                                                                <%if(docRetriveLocation == null) { %>
                                                                <img height="40" width="40" border="0" class="lazy img-circle" id="profilecontainerimg<%=recruitId+"_"+innerList.get(7) %>" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + hmCandiImage.get(innerList.get(7)) %>" />
                                                                <%} else { %>
                                                                <img height="40" width="40" border="0" class="lazy img-circle" id="profilecontainerimg<%=recruitId+"_"+innerList.get(7) %>" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_CANDIDATE+"/"+IConstants.I_IMAGE+"/"+innerList.get(7)+"/"+IConstants.I_100x100+"/"+hmCandiImage.get(innerList.get(7))%>" />
                                                                <%} %> 
                                                            </div>
                                                            <div style="float: left;line-height: 2.3;">
                                                                <%-- <%if(!closeFlag){ %>
                                                                <div style="float:left;">
                                                                    <!-- ====start parvez on 09-07-2021===== -->
                                                                    <a href="javascript:void(0);" onclick="openCandidateProfilePopup('<%=innerList.get(7) %>','<%=recruitId %>')"> <%=innerList.get(0)%> </a>
                                                                    <a href="javascript:void(0);" onclick="openCandidateProfilePopup('<%=innerList.get(7) %>','<%=recruitId %>','onboarded')"> <%=innerList.get(0)%> </a>
                                                                <!-- ====end parvez on 09-07-2021===== -->
                                                                </div>
                                                                
                                                                <div id="downloadOfferLatterSpan_<%=recruitId +"_"+ candidateID %>" style="float: left;margin:2px 0px 0px 0px;">
                                                                    <a class="fa fa-file-pdf-o" title="Download Offer letter" href="CandiOfferLetterPreview.action?candidateId=<%=candidateID%>&recruitId=<%=recruitId %>"></a>
                                                                </div>
                                                                <%} else { %>
                                                                <%=innerList.get(0)%>
                                                                <%} %> --%>
                                                                
                                                         <!-- ====start parvez on 21-10-2021===== -->
                                                                <div style="float:left;">
                                                                    <%-- <a href="javascript:void(0);" onclick="openCandidateProfilePopup('<%=innerList.get(7) %>','<%=recruitId %>')"> <%=innerList.get(0)%> </a> --%>
                                                                    <a href="javascript:void(0);" onclick="openCandidateProfilePopup('<%=innerList.get(7) %>','<%=recruitId %>','onboarded')"> <%=innerList.get(0)%> </a>
                                                                </div>
                                                                
                                                                <div id="downloadOfferLatterSpan_<%=recruitId +"_"+ candidateID %>" style="float: left;margin:2px 0px 0px 0px;">
                                                                    <a class="fa fa-file-pdf-o" title="Download Offer letter" href="CandiOfferLetterPreview.action?candidateId=<%=candidateID%>&recruitId=<%=recruitId %>"></a>
                                                                </div>
                                                         <!-- ====end parvez on 21-10-2021===== -->
                                                         
                                                                <br/>
                                                                <div style="float:left; margin-top: -6px;line-height: 16px;">
                                                                    <span id="Reting<%=candidateID%>" style="margin-right: 5px; line-height: 18px;"><%=uF.showData(strStars, "0") %>/5</span><!-- Created By Dattatray Date:05-July-21 Note : If start rating null then displayed 0  -->
                                                                    <span id="starPrimaryO<%=recruitId+"_"+candidateID %>" style="margin-left: 5px; line-height: 12px;"></span>
                                                                </div>
                                                                <script type="text/javascript">
                                                                    $('#starPrimaryO'+'<%=recruitId+"_"+candidateID %>').raty({
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
                                                        <td><%=innerList.get(1)%></td>
                                                        <td><%=innerList.get(2)%></td>
                                                        <td><%=innerList.get(3)%></td>
                                                        <td><%=innerList.get(4)%></td>
                                                        <td><%=innerList.get(5)%></td>
                                                       <%--  <td><%=innerList.get(6)%></td> --%>
                                                    </tr>
                                                    
                                                    <%
                                                        } }
                                                     //   System.out.println("hmOnBoardedInduction.get(recruitId)==="+hmOnBoardedInduction.get(recruitId));
                                                      //  System.out.println("recruitId==="+recruitId);
                                                        if (hmOnBoardedInduction!=null && (hmOnBoardedInduction.get(recruitId) == null || hmOnBoardedInduction.get(recruitId).isEmpty())) {
                                                      //  System.out.println("hmOnBoardedInduction.get(recruitId)==="+hmOnBoardedInduction.get(recruitId));%>
                                                    <tr>
                                                        <td colspan="7">
                                                            <div class="nodata msg"> <span>No Onboarded Applications </span> </div>
                                                        </td>
                                                    </tr>
                                                    <%-- <%
                                                        } else{
                                                        	if(z == 11){
                                                        %>
                                                    <tr class="lighttable">
                                                        <td colspan="7" style="text-align: right;"><a style="padding-right:5px" href="Induction.action?strVm=1&strDashboardRequest=<%=recruitId%>" >view more....</a></td>
                                                    </tr>
                                                    <%}
                                                        }
                                                        %> --%>
                                                        
                                                  <% } %>
                                                </tbody>
                                            </table>
                                            
                                        </div>
                    
                                          <%--  <h3 class="box-title" style="width: 96%; font-size: 14px;">
                                        <p class="heading_dash <%=priorityClass %>" style="padding-left: 60px;">
                                            <span style="float: left; width: 90%;"> 
                                            Today(<%=uF.showData(hmTodayIndCount.get(recruitId),"0")%>),&nbsp;&nbsp;
                                            Tomorrow(<%=uF.showData(hmTomorrowIndCount.get(recruitId),"0")%>),&nbsp;&nbsp;
                                            Day after tomorrow(<%=uF.showData(hmDayAfterTomorrowIndCount.get(recruitId),"0")%>),&nbsp;&nbsp;
                                            Pending(<%=uF.showData(hmPendingIndCount.get(recruitId),"0")%>)
                                            </span>
                                        </p>
                                    </h3>
                                    <div class="box-tools pull-right">
                                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
                                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                                    </div> --%>
                                </div>
                        <%} %>
                        <!-- <div class="clr"></div> -->
                        <%if(recruitmentIdList.size()==0){ %>
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