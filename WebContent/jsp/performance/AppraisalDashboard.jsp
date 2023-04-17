<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<% String strSessionUserType = (String) session.getAttribute(IConstants.USERTYPE);
   String fromPage = (String) request.getAttribute("fromPage"); 
   String callFrom = (String) request.getAttribute("callFrom");
   String alertID = (String) request.getAttribute("alertID");
%>
<%-- <%if((fromPage == null || fromPage.equals("") || fromPage.equalsIgnoreCase("null" )) && (callFrom == null || callFrom.equals("") || callFrom.equalsIgnoreCase("null"))){ %>
	<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
    <script type="text/javascript" src="scripts/jquery-ui.min.js"> </script>
<%} %> --%>


<script type="text/javascript" charset="utf-8"> 
$(document).ready(function(){
	
	$("body").on('click','#closeButton',function(){
		$(".modal-dialog").removeAttr('style');
		$("#modalInfo").hide();
    });
	$("body").on('click','.close',function(){
		$(".modal-dialog").removeAttr('style');
		$("#modalInfo").hide();
	});
});	
function openAppraisalPreview(id,appFreqId) {
	
	var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 $(".modal-title").html('Review Preview');
	 if($(window).width() >= 900){
		 $(".modal-dialog").width(900);
	 }
	 $.ajax({
			url : "AppraisalPreview.action?id="+id+"&appFreqId="+appFreqId,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}
	
	
	
function closeReview(reviewId, type,appFreqId) {
	var pageTitle = 'Close Review';
	if(type=='view') {
		pageTitle = 'Close Review Reason';
	}
	var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 $(".modal-title").html(''+pageTitle);
	 $.ajax({
			url : "CloseReview.action?reviewId="+reviewId+"&appFreqId="+appFreqId,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}
		
	
	function getPublishAppraisal(id, dcount, empId,appFreqId) {
		
		//alert("empId==>"+empId);
		xmlhttp = GetXmlHttpObject();
	    if (xmlhttp == null) {
			alert("Browser does not support HTTP Request");
			return;
	    } else {
            var xhr = $.ajax({
               url : "PublishAppraisal.action?id=" + id + '&dcount=' +dcount + '&empId='+empId+"&appFreqId="+appFreqId,
               cache : false,
               success : function(data) {
            	   //alert("data ===>> " + data);
               	               		var allData = data.split("::::");
               		document.getElementById("myDivM"+dcount).innerHTML = allData[0];
                    document.getElementById("myDivE"+dcount).innerHTML = allData[1];
                    document.getElementById("myDivS"+dcount).innerHTML = allData[2];
               	}
               });
            }
	    
	    //$(dialogEdit).dialog('close');
	}
	
	function GetXmlHttpObject() {
	    if (window.XMLHttpRequest) {
	            return new XMLHttpRequest();
	    }
	    if (window.ActiveXObject) {
	            return new ActiveXObject("Microsoft.XMLHTTP");
	    }
	    return null;
	}	



	/* function submitForm(){
		document.frmAppraisalDashboard.proPage.value = '';
		document.frmAppraisalDashboard.minLimit.value = '';
		document.frmAppraisalDashboard.submit();
	} */
	
	function loadMore(proPage, minLimit) {
		document.frmAppraisalDashboard.proPage.value = proPage;
		document.frmAppraisalDashboard.minLimit.value = minLimit;
		document.frmAppraisalDashboard.submit();
	}
	
	
	function getApprovalStatus(reviewId, empname,appFreqId) {
		var dialogEdit = '.modal-body';
		 $(dialogEdit).empty();
		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		 $("#modalInfo").show();
		 $(".modal-title").html('Work flow of '+empname);
		 $.ajax({
				url : "GetLeaveApprovalStatus.action?effectiveid="+reviewId+"&type=12&appFreqId="+appFreqId,
				cache : false,
				success : function(data) {
					$(dialogEdit).html(data);
				}
			});
	}
	
	
	function approveDenyRequest(reviewId, strStatus, nCount, userType) {
	//	alert("reviewId==>"+reviewId+"==>strStatus==>"+strStatus+"==>nCount==>"+nCount+"==>userType==>"+userType);
		var status = '';
		if(strStatus == '1'){
			status='approve';
		} else if(strStatus == '-1'){
			status='deny';
		}
		if(confirm('Are you sure, do you want to '+status+' this request?')) {
			var reason = window.prompt("Please enter your "+status+" reason.");
			if (reason != null) {
				var action = 'SelfReviewRequestApproveDeny.action?reviewId='+reviewId+'&strStatus='+strStatus+'&strReason='+reason+'&userType='+userType;
				//alert(action); 
				getContent('myDivStatus'+ nCount, action);
				document.getElementById("myDivM"+nCount).style.display= 'none';
			}
		}
	}
	
	</script>

<%-- <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Reviews" name="title" />
</jsp:include>  --%>

<%
	UtilityFunctions uF=new UtilityFunctions();
	String strUserType = (String) session.getAttribute("USERTYPE");
	String strReviewId = (String) request.getAttribute("strReviewId");
	String dataType = (String) request.getAttribute("dataType");
	String sbData = (String) request.getAttribute("sbData");
	String strSearchJob = (String) request.getAttribute("strSearchJob");
	String currUserType = (String) request.getAttribute("currUserType");
	
	String reviewId = (String) request.getAttribute("reviewId");
	String appFreqId = (String) request.getAttribute("appFreqId");
	
	String strBaseUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
	List<List<String>> allAppraisalreport = (List<List<String>>) request.getAttribute("allAppraisalreport");
	if(allAppraisalreport == null) allAppraisalreport = new ArrayList<List<String>>(); 
	%>
	
<%if((fromPage == null || fromPage.equals("") || fromPage.equalsIgnoreCase("null" )) && (callFrom == null || callFrom.equals("") || callFrom.equalsIgnoreCase("null"))){ %>
 <section class="content">
    <div class="row jscroll">
        <section class="col-lg-12 connectedSortable">  
<%}%>
        	
        	<% if(callFrom == null || callFrom.equals("") || callFrom.equalsIgnoreCase("null")) { %>
				<div class="notranslate col-md-12">
					<div><!-- class="nav-tabs-custom -->
					<%
						int totalAppraisal1 = 0;
						int totalEmps1 = 0;
						int totalAprPending1 = 0;
						int totalAprUnderReview1 = 0;
						int totalAprFinalised1 = 0;

						for (int i = 0; i < allAppraisalreport.size(); i++) {
							List<String> alinner = (List<String>) allAppraisalreport.get(i);

							totalAppraisal1 = allAppraisalreport.size();
							totalEmps1 += uF.parseToInt(alinner.get(1));

							totalAprPending1 += uF.parseToInt(alinner.get(2));
							totalAprUnderReview1 += uF.parseToInt(alinner.get(3));
							totalAprFinalised1 += uF.parseToInt(alinner.get(4));
						}
					%>
				       	<ul class="site-stats-new marginbottom0">
							<li class="bg_lh" style="cursor: unset;"><strong><%=totalAppraisal1%></strong> <small>Reviews</small></li>
							<li class="bg_lh" style="cursor: unset;"><strong><%=totalEmps1%></strong> <small>Reviewee</small></li>
							<li class="bg_lh" style="cursor: unset;"><strong><%=totalAprPending1%></strong> <small>Pending</small></li>		 
							<li class="bg_lh" style="cursor: unset;"><strong><%=totalAprUnderReview1%></strong> <small>Under Review</small></li>	
							<li class="bg_lh" style="cursor: unset;"><strong><%=totalAprFinalised1%></strong> <small>Finalised</small></li>			
						</ul>
					</div>
				</div> 
				<div class="notranslate col-lg-12 col-md-12" style="margin: 0px 0px 10px 0px; text-align: right;">
				<div class="col-lg-3 col-md-3 no-padding">
					<input type="text" id="strSearchJob" class="form-control" name="strSearchJob" placeholder="Search" value="<%=uF.showData(strSearchJob,"") %>"/>
					<input type="button" value="Search" class="btn btn-primary" onclick="submitForm();">
				</div>
                      <%-- <div style="float: left;line-height: 22px; width: 514px; margin-left: 350px;">
                          <span style="float: left; display: block; width: 78px;">Search:</span>
                          <div style="margin: 0px 0px 0px 16px; float: left; -moz-border-radius: 3px; -webkit-border-radius: 3px; border-radius: 3px;">
                              <div style="float: left">
                                  <input type="text" id="strSearchJob" class="form-control" name="strSearchJob"
                                      style="margin-left: 0px; width: 250px; box-shadow: 0px 0px 0px #ccc" value="<%=uF.showData(strSearchJob,"") %>" />
                              </div>
                              <div style="float: right">
                                  <input type="button" value="Search" class="btn btn-primary" name="submit" onclick="submitForm();" style="margin-left: 10px;"/>
                              </div>
                          </div>
                      </div> --%>
                      
                      <script>
                          $( "#strSearchJob" ).autocomplete({
                          	source: [ <%=uF.showData(sbData, "") %> ]
                          });
                      </script>
                      
                     <%if (strSessionUserType != null && (strSessionUserType .equalsIgnoreCase(IConstants.MANAGER) || strSessionUserType .equalsIgnoreCase(IConstants.HRMANAGER) || strSessionUserType .equalsIgnoreCase(IConstants.ADMIN) || strSessionUserType .equalsIgnoreCase(IConstants.RECRUITER))) { %>
					    <div class="col-lg-9 col-md-9 pull-right ">
							<a href="javascript:void(0)" onclick="createAppraisal()" class="pull-right">
								<input type="button" class="btn btn-primary" value="Add New Review">
							</a>
					    </div>
				  <% } %>
                </div>
				
			<% } %>
			
			<div class="col-md-12">
				<%=uF.showData((String)session.getAttribute(IConstants.MESSAGE), "") %>
				<% session.removeAttribute(IConstants.MESSAGE); %>
			</div>
					
			<div class="notranslate row row_without_margin">
				<div class="col-md-3" style=" max-height: 680px !important; overflow-y: hidden;" id="sectionRight"><!-- @uthor : Dattatray Note: added style and id style=" max-height: 680px !important; overflow-y: hidden;" id="sectionRight"-->
					<div class="box box-none">
						<div class="nav-tabs-custom">
				             <ul class="nav nav-tabs" >
					             <% if(callFrom == null || callFrom.equals("") || callFrom.equalsIgnoreCase("null")) { %>
					               <!-- Started By Dattatray Date:19-10-21  Note: Id applied on all tab-->
					                 <li class="active"><a href="javascript:void(0)" onclick="getReviewList('ReviewNamesList','L','<%=currUserType %>','<%=strSearchJob%>','','',0);" data-toggle="tab" id="aDashId0">Live</a></li>
					                 <li><a href="javascript:void(0)" onclick="getReviewList('ReviewNamesList','C','<%=currUserType %>','<%=strSearchJob%>','','',1);" data-toggle="tab" id="aDashId1">Closed</a></li>
					                 <li><a href="javascript:void(0)" onclick="getReviewList('ReviewNamesList','SRR','<%=currUserType %>','<%=strSearchJob%>','','',2);" data-toggle="tab" id="aDashId2">Self Review Request</a></li>
				                 <% } else { %>
				                 	 <li class="active"><a href="javascript:void(0)" onclick="getReviewList('ReviewNamesList','SRR','<%=currUserType %>','<%=strSearchJob%>','','',2);" data-toggle="tab" id="aDashId2">Self Review Request</a></li>
				                 <% } %>
				                 <!-- Ended By Dattatray Date:19-10-21  Note: Id applied on all tab-->
				             </ul>
				             <div class="tab-content" >
				                 <div class="active tab-pane" id="reviewResult" style="min-height: 600px;">
							
				                 </div>
				             </div>
				        </div>
					</div>
				</div>    
	   
				<div class="col-md-9" style="padding-left: 0px; min-height: 600px;"> <!-- max-height: 450px !important; overflow-y: hidden;" id="sectionLeft" -->
					<div class="box box-none" style="overflow-y: auto; min-height: 600px;" id="actionResult">
		                 <div class="active tab-pane" id="reviewDetails" style="min-height: 600px;">
					
						</div>
					</div>
				</div>
			</div>
	  <%if((fromPage == null || fromPage.equals("") || fromPage.equalsIgnoreCase("null" )) && (callFrom == null || callFrom.equals("") || callFrom.equalsIgnoreCase("null"))){ %>
	   </section>
    </div>
</section> 
<%}%>
 
<div class="modal" id="modalInfo" role="dialog">
    <div class="modal-dialog">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">-</h4>
            </div>
            <div class="modal-body" style="height:400px;overflow-y:auto;padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript" charset="utf-8">
$(document).ready(function() {
	<% if(callFrom == null || callFrom.equals("") || callFrom.equalsIgnoreCase("null")) { %>
		getReviewList('ReviewNamesList','L','<%=currUserType %>','<%=strSearchJob%>','<%=reviewId%>','<%=appFreqId%>','0');//Created By Dattatray Date:19-10-21
	<% } else { %>
		getReviewList('ReviewNamesList','SRR','<%=currUserType %>','<%=strSearchJob%>','<%=reviewId%>','<%=appFreqId%>','2');//Created By Dattatray Date:19-10-21
	<% } %>
});

function getReviewList(strAction, dataType, currUserType, strSearch, reviewId, appFreqId,index) {
	var callFrom = '<%=callFrom%>';
	var alertID = '<%=alertID%>';
	disabledPointerAddAndRemove(3,'aDashId',index,true);//Created By Dattatray Date:19-10-21
	$("#reviewResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({ 
		type : 'POST',
		url: strAction+'.action?dataType='+dataType+'&currUserType='+currUserType+'&strSearchJob ='+strSearch+'&callFrom='+callFrom
				+'&alertID='+alertID+'&strReviewId='+reviewId+'&appFreqId='+appFreqId,
		cache: true,
		success: function(result){
			$("#reviewResult").html(result);
			disabledPointerAddAndRemove(3,'aDashId',index,false);//Created By Dattatray Date:19-10-21
   		}
	});
}

function createAppraisal(){
	$("#divReviewsResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({ 
		type : 'POST',
		url: 'CreateAppraisal.action',
		cache: true,
		success: function(result){
			//alert("result1==>"+result);
			$("#divReviewsResult").html(result);
   		}
	});
}

function submitForm(){
	
	var strSearch = "";
	if(document.getElementById("strSearchJob")) {
		strSearch = document.getElementById("strSearchJob").value;
	}
	//alert("strSearch==>"+strSearch);
	$("#divReviewsResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({ 
		type : 'POST',
		url: 'AppraisalDashboard.action?strSearchJob ='+strSearch,
		cache: true,
		success: function(result){
			//alert("result1==>"+result);
			$("#divReviewsResult").html(result);
   		}
	});
}
/* @uthor : Dattatray */
$(window).bind('mousewheel DOMMouseScroll', function(event){
    
    if (event.originalEvent.wheelDelta > 0 || event.originalEvent.detail < 0) {
        // scroll up
        if($(window).scrollTop() == 0 && $("#sectionRight").scrollTop() != 0){
        	$("#sectionRight").scrollTop($("#sectionRight").scrollTop() - 30);
        }
       /*  if($(window).scrollTop() == 0 && $("#sectionLeft").scrollTop() != 0){
        	$("#sectionLeft").scrollTop($("#sectionLeft").scrollTop() - 30);
        } */
    }
    else {
        // scroll down
        if($(window).scrollTop() + $(window).height() > $(document).height() - 15) {
    		   $("#sectionRight").scrollTop($("#sectionRight").scrollTop() + 30);
   		}
        
        /* if($(window).scrollTop() + $(window).height() > $(document).height() - 15) {
 		   $("#sectionLeft").scrollTop($("#sectionLeft").scrollTop() + 30);
		} */
    }
});

/* @uthor : Dattatray */
$(window).keydown(function(event){
   
		if(event.which == 40 || event.which == 34)
		{
			if($(window).scrollTop() + $(window).height() > $(document).height() - 15) {
    		   $("#sectionRight").scrollTop($("#sectionRight").scrollTop() + 50);
   			}
			/* if($(window).scrollTop() + $(window).height() > $(document).height() - 15) {
	    		$("#sectionLeft").scrollTop($("#sectionLeft").scrollTop() + 50);
	   		} */
		}
		else if(event.which == 38 || event.which == 33)
		{
		   if($(window).scrollTop() == 0 && $("#sectionRight").scrollTop() != 0){
	    		$("#sectionRight").scrollTop($("#sectionRight").scrollTop() - 50);
	    	}
		   
		   /* if($(window).scrollTop() == 0 && $("#sectionLeft").scrollTop() != 0){
	    		$("#sectionLeft").scrollTop($("#sectionLeft").scrollTop() - 50);
	    	} */
		}
}); 
</script>