<%@page import="com.itextpdf.text.BaseColor"%>
<%@page import="com.itextpdf.text.Element"%>
<%@page import="com.konnect.jpms.export.DataStyle"%>
<%@page import="java.net.URLDecoder"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>

<script type="text/javascript" src="https://translate.google.com/translate_a/element.js?cb=googleTranslateElementInit"></script>

<style>
.balls {
	display: inline;
	padding-left: 5px;
	padding-right: 5px;
}

.balls img {
	padding-right: 2px !important;
	padding-top: 0px !important;
}

.table-bordered>thead>tr>th, .table-bordered>tbody>tr>th,
	.table-bordered>tfoot>tr>th, .table-bordered>thead>tr>td,
	.table-bordered>tbody>tr>td, .table-bordered>tfoot>tr>td {
	border: 1px solid #DBDBDB !important;
}

.goog-logo-link {
	display: none !important;
}

.goog-te-gadget {
	color: transparent !important;
}

.goog-te-banner-frame.skiptranslate {
    display: none !important;
    }
body {
    top: 0px !important; 
    }
    
    
/* ===start parvez date: 22-03-2022=== */
	#textlabel{
		white-space:pre-line;
	}
/* ===end parvez date: 22-03-2022=== */


</style>
<%
String fromPage = (String) request.getAttribute("fromPage");
String strSessionUserType = (String) session.getAttribute(IConstants.USERTYPE);

if ((strSessionUserType!=null && strSessionUserType.equals(IConstants.EMPLOYEE)) || fromPage == null || fromPage.equals("") || fromPage.equalsIgnoreCase("null")) {
%>
	<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<%} %>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/_rating/js/jquery.raty.js"> </script>
  <!-- created by seema -->
<script type="text/javascript" src="scripts/jquery.shorten.1.0.js"></script>
  <!-- created by seema -->
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
    	
    	/* $('#lt').DataTable({
    		aLengthMenu: [
    			[25, 50, 100, 200, -1],
    			[25, 50, 100, 200, "All"]
    		],
    		iDisplayLength: -1,
    		dom: 'lBfrtip',
            buttons: [
    			'copy', 'csv', 'excel', 'pdf', 'print'
            ],
            order: [],
    		columnDefs: [ {
    	      "targets"  : 'no-sort',
    	      "orderable": false
    	    }]
    	}); */
    
    	// created by seema 
    	$(".description").shorten({
			"showChars" : 50,
			"moreText" : "See More",
			"lessText" : "Less"
		});

		$(".instruction").shorten({
			"showChars" : 50,
			"moreText" : "See More",
			"lessText" : "Less"
		});
		// created by seema 
    });
    
    function getData(empId, id,appFreqId) {
    	var dialogEdit = '.modal-body';
    	$(dialogEdit).empty();
    	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$("#modalInfo").show();
    	$('.modal-title').html('See Score');
    	$.ajax({
    		url : "AppraisalScoreStatus.action?id=" + id+ "&empid=" + empId + "&type=popup&appFreqId="+appFreqId,
    		cache : false,
    		success : function(data) {
    			$(dialogEdit).html(data);
    		}
    	});
    
    }
        
    function getBalancedScoreData(id, appFreqId,empId,fromPage,empName) {
        var title = 'Balanced Score for '+ empName;
    	var dialogEdit = '.modal-body';
    	$(dialogEdit).empty();
    	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$("#modalInfo").show();
    	$('.modal-title').html(title);
    	if($(window).width() >= 900) {
    		$('.modal-dialog').width(1100);
    	}
    	$.ajax({
    		url : "AppraisalSummary.action?id="+id+"&appFreqId="+appFreqId+"&empId="+empId+"&fromPage="+fromPage,
    		cache : false,
    		success : function(data) {
    			$(dialogEdit).html(data);
    		}
    	});
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
    
    /* function getAppraisalDetail(empId, id,appFreqId) {
    	var dialogEdit = '.modal-body';
    	$(dialogEdit).empty();
    	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$("#modalInfo").show();
    	$('.modal-title').html('See Score');
    	$.ajax({
    		url : "FullAppraisalDetails.action?id="+id+"&empid="+empId+"&appFreqId="+appFreqId,
    		cache : false,
    		success : function(data) {
    			$(dialogEdit).html(data);
    		}
    	});
    } */

    
    /* function getCustomerFactSheet(empId) {
    	var dialogEdit = '.modal-body';
    	$(dialogEdit).empty();
    	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$("#modalInfo").show();
    	$('.modal-title').html('See Score');
    	$.ajax({
    		url : "MyProfile.action?empId=" + empId + "&popup=popup",
    		cache : false,
    		success : function(data) {
    			$(dialogEdit).html(data);
    		}
    	});
    } */
    
    function giveRemark(id, empId, thumbsFlag,appraisal_freq,remarktype,appFreqId,fromPage) {
    	/* if(remarktype == 1){
    		window.location = "AppraisalRemark.action?id=" + id + "&empid=" + empId + "&thumbsFlag=" + thumbsFlag + "&appraisal_freq="+ appraisal_freq +"&remarktype=" + remarktype+"&appFreqId="+appFreqId;
    	} else {*/
    		var dialogEdit = '.modal-body';
    		$(dialogEdit).empty();
    		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    		$("#modalInfo").show();
    		$('.modal-title').html('Review Summary');
    		if($(window).width() >= 900) {
        		$('.modal-dialog').width(900);
        	}
    		$.ajax({
    			url : "AppraisalRemark.action?id=" + id + "&empid=" + empId + "&thumbsFlag=" + thumbsFlag + "&appraisal_freq="+ appraisal_freq +"&remarktype=" + remarktype
    					+"&appFreqId="+appFreqId+"&fromPage="+fromPage,
    			cache : false,
    			success : function(data) {
    				$(dialogEdit).html(data);
    			}
    		});
    	//} 
    }
    
    function showAnnualAppraisalReport(empId) {
   		var dialogEdit = '.modal-body';
   		$(dialogEdit).empty();
   		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
   		$("#modalInfo").show();
   		$('.modal-title').html('Annual Appraisal Report');
   		if($(window).width() >= 900) {
       		$('.modal-dialog').width(900);
       	}
   		$.ajax({
   			url : "AnnualAppraisalReport.action?empId="+empId,
   			cache : false,
   			success : function(data) {
   				$(dialogEdit).html(data);
   			}
   		});
	}
    
    function giveRecommendation(id, empId, thumbsFlag, appraisal_freq, remarktype, appFreqId, fromPage) {
    	/* if(remarktype == 1){
    		window.location = "AppraisalRemark.action?id=" + id + "&empid=" + empId + "&thumbsFlag=" + thumbsFlag + "&appraisal_freq="+ appraisal_freq +"&remarktype=" + remarktype+"&appFreqId="+appFreqId;
    	} else {*/
    		var dialogEdit = '.modal-body';
    		$(dialogEdit).empty();
    		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    		$("#modalInfo").show();
    		$('.modal-title').html('Review Summary');
    		if($(window).width() >= 900) {
        		$('.modal-dialog').width(900);
        	}
    		$.ajax({
    			url : "AppraisalRemark.action?id=" + id + "&empid=" + empId + "&thumbsFlag=" + thumbsFlag + "&appraisal_freq="+ appraisal_freq +"&remarktype=" + remarktype
    					+"&appFreqId="+appFreqId+"&fromPage="+fromPage+"&recommendationOrFinalization=RECOMMENDATION",
    			cache : false,
    			success : function(data) {
    				$(dialogEdit).html(data);
    			}
    		});
    	//} 
    }
    
    
    function seeEmpList(empId,aid,appFreqId) {
    	var dialogEdit = '.modal-body';
    	$(dialogEdit).empty();
    	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$("#modalInfo").show();
    	$('.modal-title').html('Employee List');
    	$.ajax({
    		url : "AppraisalApproveMembers.action?empID="+empId+"&id="+aid+"&appFreqId="+appFreqId,
    		cache : false,
    		success : function(data) {
    			$(dialogEdit).html(data);
    		}
    	});
    }
    
    
    var dialogEdit = '#ShowQueDiv';
    function showAllQuestion(appid, empId, usertypeId, readstatus, appFreqId, fromPage) {
    	var dialogEdit = '.modal-body';
    	$(dialogEdit).empty();
    	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$("#modalInfo").show();
    	$('.modal-title').html('Reviews');
    	$.ajax({
    		url : "ShowAllSingleOpenWithoutMarkQue.action?appid="+appid+"&empId="+empId+"&usertypeId="+usertypeId+"&readstatus="+readstatus
    				+"&appFreqId="+appFreqId+"&fromPage="+fromPage,
    		cache : false,
    		success : function(data) {
    			$(dialogEdit).html(data);
    		}
    	});
    }
    
    function closePopup(){
    	$("#modalInfo").hide();
    }	
    
    
    function openEmployeeProfilePopup(empId) {
    	var dialogEdit = '.modal-body';
    	$(dialogEdit).empty();
    	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$("#modalInfo").show();
    	$('.modal-title').html('Employee Information');
    	if($(window).width() >= 900) {
    		$('.modal-dialog').css('width', 900);
    	}
    	$.ajax({
    		url :"MyProfile.action?empId="+empId+"&proPopup=proPopup" ,
    		cache : false,
    		success : function(data) {
    			$(dialogEdit).html(data);
    		}
    	});
     }
    
    
    function getEmpProfile(val, empName){    
    var dialogEdit = '.modal-body';
    $(dialogEdit).empty();
    $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    $("#modalInfo").show();
    $('.modal-title').html(''+empName+'');
    $.ajax({
    	//url : "AppraisalDetail.action?id="+id+"&empId="+empId, 
    	url : "AppraisalEmpProfile.action?empId="+val ,
    	cache : false,
    	success : function(data) {
    		$(dialogEdit).html(data);
    	}
    });
    }
    
    function getRevieweeAppraisers(reviewId, revieweeId, revieweeName) {
		var pageTitle = "All Reviewee's Appraisers";
		if(revieweeName != '') {
			pageTitle = revieweeName+"'s Appraisers";
		}
		var dialogEdit = '.modal-body';
		 $(dialogEdit).empty();
		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		 $("#modalInfo").show();
		 $(".modal-title").html(pageTitle);
		 $.ajax({
			url : "RevieweeAppraisers.action?reviewId="+reviewId+"&revieweeId="+revieweeId,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	 
	}
    
    
    function generateBalanceScorecardExcel(){
    	window.location = "ExportExcelReportReview.action";
    }
    
    function sendReminderForFillPendingFedback(appId) {
    	
    	if(confirm("Are you sure, you want to send reminder mails?")) {
	    	var action = 'AppraisalStatus.action?type=REMINDER&id=' + appId;
	    	getContent("mailReminderStatus", action);
    	}
    }
    
    function generateFeedbackPdf(appId, appFreqId, strEmpId) {
		var url = 'GenerateReviewPdfReport.action?appId='+appId+'&appFreqId='+appFreqId+'&strEmpId='+strEmpId;
		window.location = url;
	}
    
    function generatePendingReviewerExcel(appId, appFreqId) {
		var url = 'GenerateReviewPdfReport.action?type=EXCEL&appId='+appId+'&appFreqId='+appFreqId;
		window.location = url;
	}
    
    function generateCohortsReportManager(appId, appFreqId) {
		var url = 'CohortsReportManager.action?type=EXCEL&appId='+appId+'&appFreqId='+appFreqId;
		window.location = url;
	}
    
    function generateEmpwiseCohortsReportManager(appId, appFreqId) {
		var url = 'CohortsReportManager.action?type=EXCEL&cohortsType=EMPWISE&appId='+appId+'&appFreqId='+appFreqId;
		window.location = url;
	}
   
 /* ===start parvez date: 19-04-2022=== */   
    function generateReportExcel() {
		window.location = "ExportExcelReport.action";
	}
 /* ===end parvez date: 19-04-2022=== */
    
</script>
<script type="text/javascript">
    function selectall(x, strEmpId) {
    	var status = x.checked; 
    	var arr = document.getElementsByName(strEmpId);
    	for(i=0; i<arr.length; i++) {
      		arr[i].checked = status;
     	}
    }
</script>

<%
		List<String> empList = (List<String>) request.getAttribute("empList");
		String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
		
		String oriented_type = (String) request.getAttribute("oriented_type");
		UtilityFunctions uF = new UtilityFunctions();

		Map<String, String> hmFeatureStatus = (Map<String, String>) request.getAttribute("hmFeatureStatus"); 
		if(hmFeatureStatus == null) hmFeatureStatus = new HashMap<String, String>();
		
		//System.out.println("hmFeatureStatus ===>> " + hmFeatureStatus);
		
		Map<String, String> appraisalMp = (Map<String, String>) request.getAttribute("appraisalMp");
		
		Map<String, String> hmEmpCode = (Map<String, String>) request.getAttribute("hmEmpCode");
		Map<String, String> hmEmpCodeDesig = (Map<String, String>) request.getAttribute("hmEmpCodeDesig"); 
		Map<String, String> hmEmpName = (Map<String, String>) request.getAttribute("hmEmpName");
		
		List<String> memberList = (List<String>) request.getAttribute("memberList");
		System.out.println(memberList);
		Map<String, String> orientationMemberMp = (Map<String, String>) request.getAttribute("orientationMemberMp");
		Map<String, String> empImageMap = (Map<String, String>) request.getAttribute("empImageMap");
		Map<String, String> locationMp = (Map<String, String>) request.getAttribute("locationMp");
		
		
		String strSessionEmpId = (String) session.getAttribute(IConstants.EMPID);
		Map<String,String> hmEmpCount = (Map<String,String>)request.getAttribute("hmEmpCount");
		//int memberCount=(Integer)request.getAttribute("memberCount");
		Map<String, String> hmRevieweewiseMemberCount = (Map<String, String>) request.getAttribute("hmRevieweewiseMemberCount");
		if(hmRevieweewiseMemberCount == null) hmRevieweewiseMemberCount = new HashMap<String, String>();
		
		Map<String,String> hmRemark=(Map<String,String>)request.getAttribute("hmRemark");
		Map<String,String> hmManagerRecommendation = (Map<String,String>)request.getAttribute("hmManagerRecommendation");
		
		Map<String,String> hmEmpSuperVisor=(Map<String,String>)request.getAttribute("hmEmpSuperVisor");
		
		String apid=request.getParameter("id");
		String appFreqId=request.getParameter("appFreqId");
		
		Map<String,String> hmMemberMP = (Map<String,String>)request.getAttribute("hmMemberMP");
		Map<String, String> hmReadUnreadCount = (Map<String, String>)request.getAttribute("hmReadUnreadCount");
		
		String strMessage = (String)request.getAttribute("strMessage");
		
		
		if(strMessage == null) {
			strMessage = "";
		}
		strMessage = URLDecoder.decode(strMessage);
		    %>
 <% if(appraisalMp != null && !appraisalMp.isEmpty()  && appraisalMp.size()>0) { %>
 <%if((strSessionUserType!=null && strSessionUserType.equals(IConstants.EMPLOYEE)) || fromPage == null || fromPage.equals("") || fromPage.equalsIgnoreCase("null")) { %>
	 <section class="content">
	    <div class="row jscroll">
	        <section class="col-lg-12 connectedSortable"> 
	            <div class="box box-body">
     <%} %>
               <!-- created by seema -->
                <%-- <div class="box-header with-border" <%if(strSessionUserType!=null && !strSessionUserType.equals(IConstants.EMPLOYEE)) { %>style="padding: 0px;" <% } %>> --%> <!-- background-color:#d2d6de; -->
                <div class="box-header with-border" style="padding: 0px;">
                    <h3 class="box-title" style="width: 100%;">
                        <%=strMessage %>
                        <div id="mailReminderStatus"></div>
                        <div style="float: left; width: 100%;">
                            <div style="float: left;margin: 0px;"><%=appraisalMp.get("APPRAISAL") %></div> 
                            <div style="float: right;">
                            	<% if (strSessionUserType != null && (strSessionUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strSessionUserType.equalsIgnoreCase(IConstants.ADMIN) || strSessionUserType.equalsIgnoreCase(IConstants.RECRUITER))) {%>
                            		<input type="button" class="btn btn-primary" value="Reminder" name="btnReminder" onclick="sendReminderForFillPendingFedback('<%=apid %>');" />
                            	<% } %>
                            	<a onclick="generateBalanceScorecardExcel();" title="Balanced Score Status" href="javascript:void(0)"><i class="fa fa-file-excel-o"></i></a>
                            	<%if(strSessionUserType!=null && strSessionUserType.equals(IConstants.EMPLOYEE)) { %>
	                            	<div id="translateDiv" class="box-body" style="float: right; padding: 0px 10px;">
										<div id="google_translate_element" style="float: right; height: 30px;"></div>
										<a onclick="" href="javascript:void(0)"><i class="fa fa-file-pdf-o"></i></a>
										<!-- <div style="float: left;"><input type="button" class="notranslate btn btn-info btn-lg" onclick="getPDF();" value="Download PDF" id="download"></div> -->
									</div>
								<% } %>	
                            </div>
                        </div>
                    </h3>  
                </div>
                <!-- /.box-header -->
                <div style="overflow-y: hidden; max-height: 600px !important;" id="secAppraisalStatus"><!-- @uthor : Dattatray  -->
                    <div class="leftbox reportWidth">
						<div class="col-lg-12 col-md-12 col-sm-12" style="margin: 0px;">
							<div style="float: left; width: 100%;">
								<span style="float: left; font-size: 12px; line-height: 32px;">
									<span title="Review Type"><%=appraisalMp.get("APPRAISALTYPE")%>,</span>
									<span title="Frequency"><%=appraisalMp.get("FREQUENCY")%>,</span>
									<span title="Effective Date"><%=appraisalMp.get("APP_FREQ_FROM")%>,</span>
									<span title="Due Date"><%=appraisalMp.get("APP_FREQ_TO")%>,</span>
									<span title="Orientation"><%=appraisalMp.get("ORIENT")%></span>
								</span>
							</div>
							<div style="float: left; width: 100%;">
								<span style="float: left; font-size: 12px; line-height: 32px;"><b>Description:&nbsp;&nbsp;
								</b><span class="description" id="textlabel" ><%=appraisalMp.get("DESCRIPTION")%></span></span>
							</div>

							<div style="float: left; width: 100%;">
								<span style="float: left; font-size: 12px; line-height: 32px;"><b>Instruction:&nbsp;&nbsp;
								</b><span class="instruction" id="textlabel" ><%=appraisalMp.get("INSTRUCTION")%></span></span>
							</div>
							<div style="float: left; width: 100%;">
								<span style="float: left; font-size: 12px; line-height: 32px;"><b>Appraiser&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>:</b>&nbsp;&nbsp;&nbsp;
								</b>
								<%-- <%=appraisalMp.get("APPRAISER")%> --%>
									<%
									if (strSessionUserType == IConstants.EMPLOYEE) {
									%> <a href="javascript:void(0);" onclick="getRevieweeAppraisers('<%=apid%>', '<%=strSessionEmpId%>', '<%=hmEmpName.get(strSessionEmpId)%>');">Click Here</a> 
									<% } else { %> 
									<a href="javascript:void(0);" onclick="getRevieweeAppraisers('<%=apid%>', '', '');">Click Here</a> 
									<% } %>
								</span>

							</div>
							<div style="float: left; width: 100%;">
								<span style="float: left; font-size: 12px; line-height: 32px;"><b>Reviewer&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>:</b>&nbsp;&nbsp;&nbsp;								</b><span><%=appraisalMp.get("REVIEWER")%></span></span>
							</div>

						</div>

               <!-- created by seema -->
						<!-- Legends -->
                    
                        <s:form action="AppraisalBulkFinalization" id="formID" method="POST" theme="simple">
                            <s:hidden name="id"></s:hidden>
                             <input type="hidden" name="fromPage" id ="fromPage" value="<%=fromPage%>"/>
                            <input type="hidden" name="appFreqId" id="appFreqId" value="<%=appFreqId%>"/>
	                         <!-- created by seema -->
	                        <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12" style="margin-bottom: 5px; padding-left: 0px;margin-top:1%;">
	                          <!-- created by seema -->
	                            <!-- <h4 style="float: left; font-weight: bold; width: 100%;"> -->
	                            <span style="font-weight: bold;">Score Cards</span>
	                              <% if (strSessionUserType != null && (strSessionUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strSessionUserType.equalsIgnoreCase(IConstants.ADMIN) || strSessionUserType.equalsIgnoreCase(IConstants.RECRUITER))) {%>
		                            	<%-- <a onclick="generateEmpwiseCohortsReportManager('<%=apid %>', '<%=appFreqId %>');" href="javascript:void(0)" title="Cohorts Report"><i class="fa fa-download"></i>Empwise Cohorts</a>
		                            	<a onclick="generateCohortsReportManager('<%=apid %>', '<%=appFreqId %>');" href="javascript:void(0)" title="Cohorts Report"><i class="fa fa-download"></i> Cohorts</a> --%>
		                            	<a onclick="generatePendingReviewerExcel('<%=apid %>', '<%=appFreqId %>');" href="javascript:void(0)" title="Pending Reviewer"><i class="fa fa-download"></i> Pending Reviewer</a>
		                            	<input type="submit" style="float: right; margin-top: 0px;" value="Bulk Finalization" class="btn btn-primary" />
		                            	
			                      <%} %>
			                      <!-- </h4> -->
			                      
			            <!-- ===start parvez date: 19-04-2022=== -->          
			                      <div style="float: right;">
			                      	 <a onclick="generateReportExcel();" title="Score Status" href="javascript:void(0)"><i class="fa fa-file-excel-o"></i></a>
		                          </div>
		               <!-- ===end parvez date: 19-04-2022=== -->           
	                        </div>
                            <div style=" width:100%;">
                            <%
                            %>
                                <table class="table table-bordered" cellpadding="0" cellspacing="0" width="100%">  <!-- id="lt" -->
                                	<thead>
	                                    <tr>
	                                        <th align="left" style="text-align: center;"><input type="checkbox" onclick="selectall(this,'strIsAssigActivity')" checked="checked"/></th>
	                                        <th>Reviewee Name
	                                        </th>
	                                        <%
	                                        int addOneCnt = 0;
	                                        for (int i=0; i<memberList.size(); i++) { %>
	                                      	  <th>
	                                      	  <% 
		                                      	  String strLabel = "Appraiser";
		                                      	  if(uF.parseToInt(memberList.get(i)) == 3) {
		                                      		strLabel = "Appraisee";
		                                      		addOneCnt=1;
		                                      	  }
	                                      	  %>
	                                      	  <%=strLabel %> <span style="font-weight: normal;"> (<%=orientationMemberMp.get(memberList.get(i))%>)</span>
	                                      	 </th>
	                                        <% } %>
	                                        <!-- <th>Reviewer </th> -->
	                                        <%if(hmFeatureStatus == null || (hmFeatureStatus!=null && !uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_TEN_STAR_RATING_FOR_REVIEW)))){ %>
	                                        	<th>Reviewer </th>
	                                        <%} %>
	                                        <th width="10%">Balanced Score</th>
	                                        <th>Finalize</th>
	                                    </tr>
									</thead>
									
									<% 
									StringBuilder sbGapEmp = null;
									if(empList != null && empList.size()>0) { %>
									<tbody>
                                    <%
                                        Map<String, Map<String, String>> outerMp = (Map<String, Map<String, String>>) request.getAttribute("outerMp");
                                    	Map<String, Map<String, String>> reviewerOutMp = (Map<String, Map<String, String>>) request.getAttribute("reviewerOutMp");
                                    	//System.out.println("outerMp ===> "+outerMp);
                                    	for (int i = 0; empList != null && i < empList.size(); i++) {
	                                       	if(uF.parseToInt(empList.get(i))>0) {
	                                       		int memberCount = uF.parseToInt(hmRevieweewiseMemberCount.get(empList.get(i)));
	                                       		//System.out.println("reviewerOutMp ===> "+reviewerOutMp);
	                                       		Map<String, String> value = outerMp.get(empList.get(i).trim());
	                                       		
	                                       		if (value == null)
	                                       			value = new HashMap<String, String>();
	                                       		Map<String, String> valueReviewer = reviewerOutMp.get(empList.get(i).trim());
	                                       		if (valueReviewer == null)
	                                       			valueReviewer = new HashMap<String, String>();
	                                       		double total = 0.0;
	                                       		String remark = hmRemark.get(apid+"_"+empList.get(i).trim());
	                                       		String managerRecommendation = hmManagerRecommendation.get(apid+"_"+empList.get(i).trim());
	                                        %>
	                                    <tr>
	                                        <td style="text-align: center;">
	                                         <% if(appraisalMp != null && appraisalMp.get("APP_FREQ_CLOSE") != null && !uF.parseToBoolean(appraisalMp.get("APP_FREQ_CLOSE"))) { %>
	                                            <%if(hmEmpCount != null && ((memberCount+addOneCnt)==uF.parseToInt(hmEmpCount.get(empList.get(i).trim())))){ 
	                                            	if (strSessionUserType != null && (strSessionUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strSessionUserType.equalsIgnoreCase(IConstants.ADMIN) || strSessionUserType.equalsIgnoreCase(IConstants.RECRUITER))) {
	                                                	if(remark ==null) {
	                                                %> 
	                                           				 <input type="checkbox" name="strIsAssigActivity" value="<%=empList.get(i).trim()%>" checked/> 
	                                           		 <% } else { %>
	                                           				 &nbsp;
	                                           	 <%     }
	                                                } else {
	                                                	if(remark !=null) {
	                                                %>
	                                           				 &nbsp;
	                                            	 <% }
	                                                }
	                                             } else {%>
	                                           			 &nbsp;
	                                           <%} %>
	                                        <% } else { %>
	                                            	&nbsp;
	                                        <% } %>
	                                        </td>
	                                        <td>
	                                            <div style="float: left; width: 100%;">
	                                                <div style="float: left; width: 21px; height: 21px; margin-right:10px;">
	                                                    <%if(uF.parseToInt(hmEmpCount.get(empList.get(i).trim()))==0){ %>
	                                                    	<i class="fa fa-circle" aria-hidden="true" style="color:#b71cc5" title="Not filled yet"></i>
	                                                    <%}else if((memberCount+addOneCnt)==uF.parseToInt(hmEmpCount.get(empList.get(i).trim()))) { 
	                                                        if(remark==null){ %>
	                                                   		 <i class="fa fa-circle" aria-hidden="true" style="color:#ea9900"></i>
	                                                      <%}else { %>
	                                                   		 <i class="fa fa-circle" aria-hidden="true" style="color:#54aa0d"></i>
	                                                    <%}
	                                                    }else if((memberCount+addOneCnt)>uF.parseToInt(hmEmpCount.get(empList.get(i).trim()))){ %>
	                                                    	<i class="fa fa-circle" aria-hidden="true" style="color:#f7ee1d;padding: 5px 5px 0 5px;" title="Waiting for completion"></i>
	                                                  <%} %>
	                                                    	&nbsp;&nbsp;
	                                                </div>
	                                                <div style="float: left; width: 21px; height: 21px;">
	                                                    <img height="21" width="21" class="lazy img-circle" style="border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+empList.get(i).trim()+"/"+IConstants.I_22x22+"/"+empImageMap.get(empList.get(i).trim())%>" />
	                                                </div>
	                                                <div style="margin-left: 60px;">
	                                                    <a href="javascript: void(0)" onclick="openEmployeeProfilePopup('<%=empList.get(i) %>');"><%=hmEmpName.get(empList.get(i).trim())%></a>
	                                                    <%=uF.showData(hmEmpCodeDesig.get(empList.get(i).trim()),"")%>
	                                                    working at
	                                                    <%=uF.showData(locationMp.get(empList.get(i).trim()),"")%>
	                                                </div>
	                                            </div>
	                                            <%-- <div style="float: left; margin-top: 3px; padding-left: 60px;">
	                                                <%=hmMemberMP!=null && hmMemberMP.get(empList.get(i).trim())!=null ? hmMemberMP.get(empList.get(i).trim()) : "" %>
	                                            </div> --%>
	                                        </td>
	                                        <%
	                                            for (int j = 0; memberList != null && !memberList.isEmpty() && j < memberList.size(); j++) {
	                                            	total += uF.parseToDouble(value.get(memberList.get(j).trim()));
	                                            			//System.out.println("memberList.get(j).trim() :::::::::: "+memberList.get(j).trim());
	                                            			//System.out.println("value.get(memberList.get(j).trim()) :::::::::: "+value.get(memberList.get(j).trim()));
	                                            			//System.out.println("Created Id :::::::::: "+memberList.get(j)+"_"+empList.get(i)+"_0");
	                                            %>
	                                        <td align="right">
	                                        	<div>
		                                            <%if(!uF.showData(hmReadUnreadCount.get(memberList.get(j).trim()+"_"+empList.get(i).trim()+"_0"),"0").equals("0") || !uF.showData(hmReadUnreadCount.get(memberList.get(j).trim()+"_"+empList.get(i).trim()+"_1"),"0").equals("0")){ %>
		                                            <span style="float: left; margin-left: 10px;"> <a href="javascript: void(0)" onclick="showAllQuestion('<%=apid%>','<%=empList.get(i).trim()%>','<%=memberList.get(j).trim() %>','RUR','<%=appFreqId %>','<%=fromPage %>')" title="Unread Single Open without marks question">[<%=uF.showData(hmReadUnreadCount.get(memberList.get(j).trim()+"_"+empList.get(i).trim()+"_0"),"0") %>]</a>
		                                            <a href="javascript: void(0)" onclick="showAllQuestion('<%=apid%>','<%=empList.get(i).trim()%>','<%=memberList.get(j).trim() %>','R','<%=appFreqId %>','<%=fromPage %>')" title="Read Single Open without marks question">[<%=uF.showData(hmReadUnreadCount.get(memberList.get(j).trim()+"_"+empList.get(i).trim()+"_1"),"0") %>]</a> </span>
		                                            <%} %>
		                                            <%if(value.get(memberList.get(j).trim())!=null){ %>
		                                            <a href="javascript:void(0)" onclick="getMemberData('<%=memberList.get(j)%>','<%=empList.get(i).trim()%>','<s:property value="id" />', '<%=hmEmpName.get(empList.get(i).trim())%>', '<%=orientationMemberMp.get(memberList.get(j).trim())%>','<%=appFreqId %>')">
		                                       <!-- ===start parvez date: 28-02-2023=== -->  
		                                            <%-- <%=uF.showData(value.get(memberList.get(j).trim()), "0")%>% --%>
		                                            <%if(value.get("CalculationBasisOn")!=null && uF.parseToBoolean(value.get("CalculationBasisOn"))){ %>
		                                            	<%=value.get(memberList.get(j).trim()) != null ? uF.formatIntoOneDecimal(uF.parseToDouble(value.get(memberList.get(j).trim())) / 20) : "0"%>
		                                            <%}else{ %>
		                                            	<%=uF.showData(value.get(memberList.get(j).trim()), "0")%>%
		                                            <%} %>
		                                        
		                                            </a>
		                                            <% } else { %>
		                                            <%=value.get("CalculationBasisOn")!=null && uF.parseToBoolean(value.get("CalculationBasisOn"))?"" : "0%" %>
		                                            <% } %>
		                                       <!-- ===end parvez date: 28-02-2023=== -->     
		                                            <div id="starPrimary<%=memberList.get(j).trim()+"_"+empList.get(i).trim()%>"></div>
		                                  <!-- ===start parvez date: 10-03-2023=== -->          
		                                            <%-- <input type="hidden" id="gradewithrating<%=memberList.get(j).trim()+"_"+empList.get(i).trim()%>"
	                                                value="<%=value.get(memberList.get(j).trim()) != null ? uF.parseToDouble(value.get(memberList.get(j).trim())) / 20 + "" : "0"%>"
	                                                name="gradewithrating<%=empList.get(i).trim()%>" /> 
	                                                <%System.out.println("ApS.jsp/695--rating=="+(uF.parseToDouble(value.get(memberList.get(j).trim())) / 10)); %>
		                                            <script type="text/javascript">
		                                                $('#starPrimary<%=memberList.get(j).trim()+"_"+empList.get(i).trim()%>').raty({
		                                                	readOnly: true,
		                                                	start: <%=value.get(memberList.get(j).trim()) != null ? uF.parseToDouble(value.get(memberList.get(j).trim())) / 20 + "" : "0"%>,
		                                                	half: true,
		                                                	targetType: 'number',
		                                                	click: function(score, evt) {
		                                                		$('#gradewithrating<%=memberList.get(j).trim()+"_"+empList.get(i).trim()%>').val(score);
		                                                	}
		                                                });
		                                            </script> --%>
		                                            
		                                            <%if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_TEN_STAR_RATING_FOR_REVIEW))){ %>
		                                            	<input type="hidden" id="gradewithrating<%=memberList.get(j).trim()+"_"+empList.get(i).trim()%>"
		                                                value="<%=value.get(memberList.get(j).trim()) != null ? uF.parseToDouble(value.get(memberList.get(j).trim())) / 10 + "" : "0"%>"
		                                                name="gradewithrating<%=empList.get(i).trim()%>" /> 
		                                                <script type="text/javascript">
			                                                $('#starPrimary<%=memberList.get(j).trim()+"_"+empList.get(i).trim()%>').raty({
			                                                	readOnly: true,
			                                                	start: <%=value.get(memberList.get(j).trim()) != null ? uF.parseToDouble(value.get(memberList.get(j).trim())) / 10 + "" : "0"%>,
			                                                	number: 10,
			                                                	half: false,
			                                                	targetType: 'number',
			                                                	click: function(score, evt) {
			                                                		$('#gradewithrating<%=memberList.get(j).trim()+"_"+empList.get(i).trim()%>').val(score);
			                                                	}
			                                                });
			                                            </script>
		                                            <%} else{ %>
			                                            <input type="hidden" id="gradewithrating<%=memberList.get(j).trim()+"_"+empList.get(i).trim()%>"
		                                                value="<%=value.get(memberList.get(j).trim()) != null ? uF.parseToDouble(value.get(memberList.get(j).trim())) / 20 + "" : "0"%>"
		                                                name="gradewithrating<%=empList.get(i).trim()%>" />
			                                            <script type="text/javascript">
			                                                $('#starPrimary<%=memberList.get(j).trim()+"_"+empList.get(i).trim()%>').raty({
			                                                	readOnly: true,
			                                                	start: <%=value.get(memberList.get(j).trim()) != null ? uF.parseToDouble(value.get(memberList.get(j).trim())) / 20 + "" : "0"%>,
			                                                	half: true,
			                                                	targetType: 'number',
			                                                	click: function(score, evt) {
			                                                		$('#gradewithrating<%=memberList.get(j).trim()+"_"+empList.get(i).trim()%>').val(score);
			                                                	}
			                                                });
			                                            </script>
		                                            <%} %>
		                                      <!-- ===end parvez date: 10-03-2023=== -->      
		                                         </div>
		                                            
	                                            <%if(uF.parseToDouble(value.get(memberList.get(j).trim()+"_REVIEWER"))>0) { %>
		                                          	<div>
		                                            <%if(value.get(memberList.get(j).trim()+"_REVIEWER") != null) { %>
		                                            	Reviewer: <%=uF.showData(value.get(memberList.get(j).trim()+"_REVIEWER"), "0")%>%
		                                            <% } else { %>
		                                            	0%
		                                            <% } %>
		                                            <div id="starPrimary<%=memberList.get(j).trim()+"_"+empList.get(i).trim()+"_REVIEWER" %>"></div>
		                                    <!-- ===start parvez date: 10-03-2023=== -->        
		                                            <%-- <input type="hidden" name="gradewithrating<%=empList.get(i).trim()%>" id="gradewithrating<%=memberList.get(j).trim()+"_"+empList.get(i).trim()+"_REVIEWER"%>"
		                                                value="<%=value.get(memberList.get(j).trim()+"_REVIEWER") != null ? uF.parseToDouble(value.get(memberList.get(j).trim()+"_REVIEWER")) / 20 + "" : "0"%>" /> 
			                                            <script type="text/javascript">
			                                                $('#starPrimary<%=memberList.get(j).trim()+"_"+empList.get(i).trim()+"_REVIEWER"%>').raty({
			                                                	readOnly: true,
			                                                	start: <%=value.get(memberList.get(j).trim()+"_REVIEWER") != null ? uF.parseToDouble(value.get(memberList.get(j).trim()+"_REVIEWER")) / 20 + "" : "0"%>,
			                                                	half: true,
			                                                	targetType: 'number',
			                                                	click: function(score, evt) {
			                                                		$('#gradewithrating<%=memberList.get(j).trim()+"_"+empList.get(i).trim()+"_REVIEWER"%>').val(score);
			                                                	}
			                                                });
			                                            </script> --%>
			                                            <%if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_TEN_STAR_RATING_FOR_REVIEW))){ %>
				                                            <input type="hidden" name="gradewithrating<%=empList.get(i).trim()%>" id="gradewithrating<%=memberList.get(j).trim()+"_"+empList.get(i).trim()+"_REVIEWER"%>"
			                                                value="<%=value.get(memberList.get(j).trim()+"_REVIEWER") != null ? uF.parseToDouble(value.get(memberList.get(j).trim()+"_REVIEWER")) / 10 + "" : "0"%>" /> 
				                                            <script type="text/javascript">
				                                                $('#starPrimary<%=memberList.get(j).trim()+"_"+empList.get(i).trim()+"_REVIEWER"%>').raty({
				                                                	readOnly: true,
				                                                	start: <%=value.get(memberList.get(j).trim()+"_REVIEWER") != null ? uF.parseToDouble(value.get(memberList.get(j).trim()+"_REVIEWER")) / 10 + "" : "0"%>,
				                                                	number: 10,
				                                                	half: false,
				                                                	targetType: 'number',
				                                                	click: function(score, evt) {
				                                                		$('#gradewithrating<%=memberList.get(j).trim()+"_"+empList.get(i).trim()+"_REVIEWER"%>').val(score);
				                                                	}
				                                                });
				                                            </script>
			                                            <%} else{ %>
				                                            <input type="hidden" name="gradewithrating<%=empList.get(i).trim()%>" id="gradewithrating<%=memberList.get(j).trim()+"_"+empList.get(i).trim()+"_REVIEWER"%>"
			                                                value="<%=value.get(memberList.get(j).trim()+"_REVIEWER") != null ? uF.parseToDouble(value.get(memberList.get(j).trim()+"_REVIEWER")) / 20 + "" : "0"%>" /> 
				                                            <script type="text/javascript">
				                                                $('#starPrimary<%=memberList.get(j).trim()+"_"+empList.get(i).trim()+"_REVIEWER"%>').raty({
				                                                	readOnly: true,
				                                                	start: <%=value.get(memberList.get(j).trim()+"_REVIEWER") != null ? uF.parseToDouble(value.get(memberList.get(j).trim()+"_REVIEWER")) / 20 + "" : "0"%>,
				                                                	half: true,
				                                                	targetType: 'number',
				                                                	click: function(score, evt) {
				                                                		$('#gradewithrating<%=memberList.get(j).trim()+"_"+empList.get(i).trim()+"_REVIEWER"%>').val(score);
				                                                	}
				                                                });
				                                            </script>
			                                            <%} %>
			                                      <!-- ===end parvez date: 10-03-2023=== -->      
		                                            </div>
	                                            <% } %>
	                                            <div style="float: left; padding-left: 6px;">
	                                                <%=hmMemberMP!=null && hmMemberMP.get(empList.get(i).trim()+"_"+memberList.get(j).trim())!=null ? hmMemberMP.get(empList.get(i).trim()+"_"+memberList.get(j).trim()) : "" %>
	                                            </div>
	                                        </td>
	                                        <% } %>
	                                        
	                                     <%if(hmFeatureStatus == null || (hmFeatureStatus!=null && !uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_TEN_STAR_RATING_FOR_REVIEW)))){ %>   
	                                        <td align="right">
	                                           <%if(!uF.showData(hmReadUnreadCount.get("reviewer_"+empList.get(i).trim()+"_0"),"0").equals("0") || !uF.showData(hmReadUnreadCount.get("reviewer_"+empList.get(i).trim()+"_1"),"0").equals("0")) { %>
	                                            <span style="float: left; margin-left: 10px;">
	                                            	<a href="javascript: void(0)" onclick="showAllQuestion('<%=apid%>','<%=empList.get(i).trim()%>','<%=valueReviewer.get("REVIEWER_USERTYPE") %>','RUR','<%=appFreqId %>','<%="" %>')" title="Unread Single Open without marks question">[<%=uF.showData(hmReadUnreadCount.get(valueReviewer.get("REVIEWER_USERTYPE")+"_"+empList.get(i).trim()+"_0"),"0") %>]</a>
	                                            	<a href="javascript: void(0)" onclick="showAllQuestion('<%=apid%>','<%=empList.get(i).trim()%>','<%=valueReviewer.get("REVIEWER_USERTYPE") %>','R','<%=appFreqId %>','<%="" %>')" title="Read Single Open without marks question">[<%=uF.showData(hmReadUnreadCount.get(valueReviewer.get("REVIEWER_USERTYPE")+"_"+empList.get(i).trim()+"_1"),"0") %>]</a>
	                                            </span>
	                                           <% } %>
	                                           <%if(valueReviewer.get("REVIEWER")!=null) { %>
	                                         <!-- ===start parvez date: 02-03-2023=== -->  
	                                         	<%-- <a href="javascript:void(0)" onclick="getMemberData('<%=valueReviewer.get("REVIEWER_USERTYPE") %>', '<%=empList.get(i).trim()%>', '<s:property value="id" />', '<%=hmEmpName.get(empList.get(i).trim())%>', '<%="Reviewer" %>', '<%=appFreqId %>')">
	                                           	<%=uF.showData(valueReviewer.get("REVIEWER"), "0")%>%</a> --%>
	                                           	<%if(valueReviewer.get("ACTUAL_CAL_BASIS")!=null && uF.parseToBoolean(valueReviewer.get("ACTUAL_CAL_BASIS"))){ %>
	                                           		<a href="javascript:void(0)" onclick="getMemberData('<%=valueReviewer.get("REVIEWER_USERTYPE") %>', '<%=empList.get(i).trim()%>', '<s:property value="id" />', '<%=hmEmpName.get(empList.get(i).trim())%>', '<%="Reviewer" %>', '<%=appFreqId %>')">
	                                           			<%=valueReviewer.get("REVIEWER")!=null ? uF.formatIntoOneDecimal(uF.parseToDouble(valueReviewer.get("REVIEWER"))/20) : "0" %></a>
	                                           	<% } else { %>
		                                           	<a href="javascript:void(0)" onclick="getMemberData('<%=valueReviewer.get("REVIEWER_USERTYPE") %>', '<%=empList.get(i).trim()%>', '<s:property value="id" />', '<%=hmEmpName.get(empList.get(i).trim())%>', '<%="Reviewer" %>', '<%=appFreqId %>')">
		                                           		<%=uF.showData(valueReviewer.get("REVIEWER"), "0")%>%</a>
	                                           	<%} %>
	                                           
	                                           <% } else { %>
	                                           <!-- 0% -->
	                                           	<%=valueReviewer.get("ACTUAL_CAL_BASIS")!=null && uF.parseToBoolean(valueReviewer.get("ACTUAL_CAL_BASIS"))?"" : "0%" %>
	                                         <!-- ===end parvez date: 02-03-2023=== --> 
	                                           <% } %>
	                                       
	                                   <!-- ===start parvez date: 10-03-2023=== -->        
	                                           <%-- <div id="starPrimary<%="reviewer_"+empList.get(i).trim()%>"></div>
	                                           <input type="hidden" name="gradewithrating<%=empList.get(i).trim()%>" id="gradewithrating<%="reviewer_"+empList.get(i).trim()%>"
	                                               value="<%=valueReviewer.get("REVIEWER") != null ? uF.parseToDouble(valueReviewer.get("REVIEWER")) / 20 + "" : "0"%>" /> 
	                                            <script type="text/javascript">
	                                                $('#starPrimary<%="reviewer_"+empList.get(i).trim()%>').raty({
	                                                	readOnly: true,
	                                                	start: <%=valueReviewer.get("REVIEWER") != null ? uF.parseToDouble(valueReviewer.get("REVIEWER")) / 20 + "" : "0"%>,
	                                                	half: true,
	                                                	targetType: 'number',
	                                                	click: function(score, evt) {
	                                                		$('#gradewithrating<%="reviewer_"+empList.get(i).trim()%>').val(score);
	                                                	}
	                                                });
	                                            </script> --%>
	                                            
	                                            <%if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_TEN_STAR_RATING_FOR_REVIEW))){ %>
		                                            <div id="starPrimary<%="reviewer_"+empList.get(i).trim()%>"></div>
		                                           	<input type="hidden" name="gradewithrating<%=empList.get(i).trim()%>" id="gradewithrating<%="reviewer_"+empList.get(i).trim()%>"
		                                               value="<%=valueReviewer.get("REVIEWER") != null ? uF.parseToDouble(valueReviewer.get("REVIEWER")) / 10 + "" : "0"%>" /> 
		                                            <script type="text/javascript">
		                                                $('#starPrimary<%="reviewer_"+empList.get(i).trim()%>').raty({
		                                                	readOnly: true,
		                                                	start: <%=valueReviewer.get("REVIEWER") != null ? uF.parseToDouble(valueReviewer.get("REVIEWER")) / 10 + "" : "0"%>,
		                                                	number: 10,
		                                                	half: false,
		                                                	targetType: 'number',
		                                                	click: function(score, evt) {
		                                                		$('#gradewithrating<%="reviewer_"+empList.get(i).trim()%>').val(score);
		                                                	}
		                                                });
		                                            </script>
	                                            <%} else{ %>
		                                            <div id="starPrimary<%="reviewer_"+empList.get(i).trim()%>"></div>
		                                           	<input type="hidden" name="gradewithrating<%=empList.get(i).trim()%>" id="gradewithrating<%="reviewer_"+empList.get(i).trim()%>"
		                                               value="<%=valueReviewer.get("REVIEWER") != null ? uF.parseToDouble(valueReviewer.get("REVIEWER")) / 20 + "" : "0"%>" /> 
		                                            <script type="text/javascript">
		                                                $('#starPrimary<%="reviewer_"+empList.get(i).trim()%>').raty({
		                                                	readOnly: true,
		                                                	start: <%=valueReviewer.get("REVIEWER") != null ? uF.parseToDouble(valueReviewer.get("REVIEWER")) / 20 + "" : "0"%>,
		                                                	half: true,
		                                                	targetType: 'number',
		                                                	click: function(score, evt) {
		                                                		$('#gradewithrating<%="reviewer_"+empList.get(i).trim()%>').val(score);
		                                                	}
		                                                });
		                                            </script>
	                                            <%} %>
	                                            
	                                   <!-- ===end parvez date: 10-03-2023=== -->         
	                                       	</td>
	                                     <%} %>   
	                                        
	                                        <td align="right">
	                                            <%
	                                                Map<String, String> hmAttributeThreshhold = (Map<String, String>) request.getAttribute("hmAttributeThreshhold");
	                                                List<String> attribIdList = (List<String>) request.getAttribute("attribIdList");
	                                                Map<String, String> hmScoreAggregateMap = (Map<String, String>) request.getAttribute("hmScoreAggregateMap");
	                                                boolean flag = false;
	                                                StringBuilder attribIds = new StringBuilder();
	                                                //System.out.println("attribIdList ===>> " + attribIdList);
	                                                //System.out.println("empList.get(i) ===>> " + empList.get(i).trim());
	                                                int attribCnt = 0;
	                                                int aggregateCnt = 0;
	                                                for(int a=0; attribIdList != null && !attribIdList.isEmpty() && a<attribIdList.size(); a++) {
	                                                	double aggregate = uF.parseToDouble(hmScoreAggregateMap.get(empList.get(i).trim()+"_"+attribIdList.get(a)));
	                                                	//System.out.println("aggregate ===>> " + aggregate + " uF.parseToDouble(hmAttributeThreshhold.get(attribIdList.get(a))) ===>> " + uF.parseToDouble(hmAttributeThreshhold.get(attribIdList.get(a))));
	                                                	if(aggregate < uF.parseToDouble(hmAttributeThreshhold.get(attribIdList.get(a)))) {
	                                                		//System.out.println("aggregate ===>> " + aggregate);
	                                                		attribIds.append(attribIdList.get(a)+"::");
	                                                		aggregateCnt++;
	                                                	}
	                                                	attribCnt++;
	                                                }
	                                                //System.out.println("attribCnt ===>> " + attribCnt +" aggregateCnt ===>> " + aggregateCnt);
	                                                if(attribCnt == aggregateCnt) {
	                                                	flag = true;
	                                                }
	                                                %>
	                                            <div style="float: left; width: 100%;">
	                                                <%
	                                                    String aggregate = "0.0";
	                                                	String strAggregate = "0.0";
	                                                	
	                                                	//System.out.println("memberCount ===>> " + memberCount +"----- addOneCnt ===>> " + addOneCnt+"---hmEmpCount.get(empList.get(i).trim()="+hmEmpCount.get(empList.get(i)));
	                                                	//System.out.println("empList.get(i) ===>> " + empList.get(i));
	                                                	if((memberCount+addOneCnt) == uF.parseToInt(hmEmpCount.get(empList.get(i).trim()))) { // && valueReviewer.get("REVIEWER")!=null
	                                                   //===start parvez date: 10-03-2023=== 	
	                                                		if(valueReviewer.get("AGGREGATE") != null && uF.parseToDouble(valueReviewer.get("AGGREGATE"))>0) {
	                                                    		//aggregate = valueReviewer.get("AGGREGATE") != null ? uF.parseToDouble(valueReviewer.get("AGGREGATE")) / 20 + "" : "0";
	                                                    		//strAggregate = valueReviewer.get("AGGREGATE");
	                                                    		if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_TEN_STAR_RATING_FOR_REVIEW))){
	                                                    			aggregate = valueReviewer.get("AGGREGATE") != null ? uF.parseToDouble(valueReviewer.get("AGGREGATE")) / 10 + "" : "0";
		                                                    		strAggregate = valueReviewer.get("AGGREGATE");
	                                                    		}else{
	                                                    			aggregate = valueReviewer.get("AGGREGATE") != null ? uF.parseToDouble(valueReviewer.get("AGGREGATE")) / 20 + "" : "0";
		                                                    		strAggregate = valueReviewer.get("AGGREGATE");
	                                                    		}
	                                                    		
	                                                    		//System.out.println("ApS/815--strAggregate="+valueReviewer.get("AGGREGATE"));
	                                                    		
	                                                    	} else {
	                                                    		//aggregate = value.get("AGGREGATE") != null ? uF.parseToDouble(value.get("AGGREGATE")) / 20 + "" : "0";
	                                                    		//strAggregate = value.get("AGGREGATE");
	                                                    		if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_TEN_STAR_RATING_FOR_REVIEW))){
	                                                    			aggregate = value.get("AGGREGATE") != null ? uF.parseToDouble(value.get("AGGREGATE")) / 10 + "" : "0";
		                                                    		strAggregate = value.get("AGGREGATE");
	                                                    		}else{
	                                                    			aggregate = value.get("AGGREGATE") != null ? uF.parseToDouble(value.get("AGGREGATE")) / 20 + "" : "0";
		                                                    		strAggregate = value.get("AGGREGATE");
	                                                    		}
	                                                    		//System.out.println("ApS/820--strAggregate="+value.get("AGGREGATE"));
	                                                    	}
	                                                	//===end parvez date: 10-03-2023===
	                                                    %>
	                                                <% if(!flag) { %>
	                                                 <span style="float: left; margin-left: 10px; margin-top: 5px;"><i class="fa fa-thumbs-up" style="color:#68ac3b;height: 16px; width: 16px;" aria-hidden="true"></i></span>
	                                                 
	                                                <% } else {
	                                                    if(sbGapEmp == null) {
	                                                    	sbGapEmp = new StringBuilder();
	                                                    	sbGapEmp.append(","+empList.get(i).trim()+",");
	                                                    } else {
	                                                    	sbGapEmp.append(empList.get(i).trim()+",");
	                                                    }
	                                                    %>
	                                                <span style="float: left; margin-left: 10px; margin-top: 5px;"><img style="height: 16px; width: 16px;" src="images1/thumbs_down_red.png"></span>
	                                                <% } %>
	                                                <span style="float: right;">
	                                                <%//System.out.println("ApS/788--strAggregate="+strAggregate); %>
	                                                <a onclick="getBalancedScoreData('<%=apid%>','<%=appFreqId%>','<%=empList.get(i).trim()%>','AD','<%=hmEmpName.get(empList.get(i).trim())%>')" href="javascript:void(0);">
	                                                <%-- <%=uF.showData(uF.getRoundOffValue(2, uF.parseToDouble(strAggregate)), "NA")%>% --%>
	                                        <!-- ===start parvez date: 28-02-2023=== -->  
		                                            <%-- <%=uF.showData(value.get(memberList.get(j).trim()), "0")%>% --%>
		                                            <%if(value.get("CalculationBasisOn")!=null && uF.parseToBoolean(value.get("CalculationBasisOn"))){ %>
		                                            	<%=uF.showData(uF.formatIntoOneDecimal(uF.parseToDouble(aggregate)), "NA")%>
		                                            <%}else{ %>
		                                            	<%=uF.showData(uF.getRoundOffValue(2, uF.parseToDouble(strAggregate)), "NA")%>%
		                                            <%} %>
			                                        </a>
	                                         <!-- ===end parvez date: 28-02-2023=== -->
	                                                </span>
	                                                <% } else { %>
	                                                <span style="float: right;">NA</span>
	                                                <%} %>
	                                            </div>
	                                            <div id="starPrimary_BS<%=empList.get(i).trim()%>"></div>
	                                            <input type="hidden" id="gradewithrating_BS<%=empList.get(i).trim()%>" value="<%=aggregate%>" name="gradewithrating<%=empList.get(i).trim()%>" /> 
	                                            <script type="text/javascript">
	                                                $('#starPrimary_BS<%=empList.get(i).trim()%>').raty({
	                                                	readOnly: true,
	                                                	start: <%= aggregate %>,
	                                                	number: <%=hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_TEN_STAR_RATING_FOR_REVIEW)) ? 10 : 5 %>,
	                                                	half: <%=hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_TEN_STAR_RATING_FOR_REVIEW)) ? false : true %>,
	                                                	targetType: 'number',
	                                                	click: function(score, evt) {
	                                                		$('#gradewithrating_BS<%=empList.get(i).trim() %>').val(score);
	                                                	}
	                                                });
	                                            </script>
	                                        </td>
	                                        <td align="center">
	                                        	<% // System.out.println("IsFreqClose==>"+appraisalMp.get("APP_FREQ_CLOSE")+"==>memberCount==>"+memberCount+"==>empId==>"+empList.get(i) +" -- hmEmpCount.get(empList.get(i).trim()) ===>> " + hmEmpCount.get(empList.get(i).trim())); %>
	                                            <% if(!uF.parseToBoolean(appraisalMp.get("APP_FREQ_CLOSE"))) { %>
	                                            <%if((memberCount+addOneCnt) == uF.parseToInt(hmEmpCount.get(empList.get(i).trim()))) { // && valueReviewer.get("REVIEWER")!=null
													if (strSessionUserType != null && (strSessionUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strSessionUserType.equalsIgnoreCase(IConstants.ADMIN) || strSessionUserType.equalsIgnoreCase(IConstants.RECRUITER))) {
														//System.out.println("remark==>"+remark);
														if(remark ==null) {
	                                                %> 
	                                           			<a href="javascript:void(0)" onclick="giveRemark('<s:property value="id" />','<%=empList.get(i).trim()%>','<%=flag %>','<%=appraisalMp.get("FREQUENCY")%>',1,'<%=appFreqId %>','AD')" >Finalize</a>
	                                           			<% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_SHOW_REVIEW_FEEDBACK_IN_PDF_FORMAT))) { %>
	                                           				<a href="javascript:void(0)" title="Download Feedback" class="fa fa-file-pdf-o"  onclick="generateFeedbackPdf('<%=apid %>', '<%=appFreqId %>', '<%=empList.get(i).trim()%>')"></a>
	                                           			<% } %> 
	                                            	<% } else { %>
	                                           			<a href="javascript:void(0)" onclick="giveRemark('<s:property value="id" />','<%=empList.get(i).trim()%>','<%=flag %>','<%=appraisalMp.get("FREQUENCY")%>',2,'<%=appFreqId %>','AD')" ><%="Finalized by "+remark %></a>
	                                           			<% 
		                                          	  	//System.out.println("apid ===>> " + apid);
		                                          	  	if(uF.parseToInt(apid)==32) { %>
		                                          	  	<br/>
		                                          	  	<a href="javascript:void(0)" onclick="showAnnualAppraisalReport('<%=empList.get(i).trim()%>')" data-toggle="tab">Annual Appraisal Report</a>
		                                          	  	
		                                          	  	<% } %>
	                                           			<% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_SHOW_REVIEW_FEEDBACK_IN_PDF_FORMAT))) { %>
	                                           				<a href="javascript:void(0)" title="Download Feedback" class="fa fa-file-pdf-o"  onclick="generateFeedbackPdf('<%=apid %>', '<%=appFreqId %>', '<%=empList.get(i).trim()%>')"></a>
	                                           			<% } %>
	                                            	<% }
	                                                } else if (strSessionUserType != null && (strSessionUserType.equalsIgnoreCase(IConstants.MANAGER)) && uF.parseToInt(hmEmpSuperVisor.get(empList.get(i).trim())) == uF.parseToInt(strSessionEmpId)) {
														if(managerRecommendation ==null) {
			                                                %> 
			                                           			<a href="javascript:void(0)" onclick="giveRecommendation('<s:property value="id" />','<%=empList.get(i).trim()%>','<%=flag %>','<%=appraisalMp.get("FREQUENCY")%>',1,'<%=appFreqId %>','AD')" >Recommendation</a>
			                                           			<%-- <% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_SHOW_REVIEW_FEEDBACK_IN_PDF_FORMAT))) { %>
			                                           				<a href="javascript:void(0)" title="Download Feedback" class="fa fa-file-pdf-o"  onclick="generateFeedbackPdf('<%=apid %>', '<%=appFreqId %>', '<%=empList.get(i).trim()%>')"></a>
			                                           			<% } %> --%> 
			                                            	<% } else { %>
			                                           			<a href="javascript:void(0)" onclick="giveRecommendation('<s:property value="id" />','<%=empList.get(i).trim()%>','<%=flag %>','<%=appraisalMp.get("FREQUENCY")%>',2,'<%=appFreqId %>','AD')" ><%="Recommended by "+managerRecommendation %></a>
			                                           			<%-- <% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_SHOW_REVIEW_FEEDBACK_IN_PDF_FORMAT))) { %>
			                                           				<a href="javascript:void(0)" title="Download Feedback" class="fa fa-file-pdf-o"  onclick="generateFeedbackPdf('<%=apid %>', '<%=appFreqId %>', '<%=empList.get(i).trim()%>')"></a>
			                                           			<% } %> --%>
			                                            	<% }
			                                        } else {
	                                                	if(remark !=null) {
	                                                %>
	                                           			<a href="javascript:void(0)" onclick="giveRemark('<s:property value="id" />','<%=empList.get(i).trim()%>','<%=flag %>','<%=appraisalMp.get("FREQUENCY")%>',2,'<%=appFreqId %>','AD')" ><%="Finalized by "+remark %></a>
	                                           			<% 
		                                          	  	//System.out.println("apid ===>> " + apid);
		                                          	  	if(uF.parseToInt(apid)==32) { %>
			                                          	  	<br/>
			                                          	  	<a href="javascript:void(0)" onclick="showAnnualAppraisalReport('<%=empList.get(i).trim()%>')" data-toggle="tab">Annual Appraisal Report</a>
		                                          	  	
		                                          	  	<% } %>
	                                          	  <% } %>
	                                          	  	
													<% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_SHOW_REVIEW_FEEDBACK_IN_PDF_FORMAT))) { %>
                                           				<a href="javascript:void(0)" title="Download Feedback" class="fa fa-file-pdf-o"  onclick="generateFeedbackPdf('<%=apid %>', '<%=appFreqId %>', '<%=empList.get(i).trim()%>')"></a>
                                           			<% } %>
	                                          	  <%
	                                                }
	                                                } else {
	                                              %>
	                                            -
	                                            <% } %>
	                                            <% } else { %>
	                                            -
	                                            <% } %>
	                                        </td>
	                                    </tr>
										<% 
	                                       	} } %>
									</tbody> 
								<% } 
									if(sbGapEmp==null){ 
                                    	sbGapEmp = new StringBuilder();
                                    }
								%>
                                </table>
                                
                                <input type="hidden" name="strGapEmp" id="strGapEmp" value="<%=sbGapEmp.toString()  %>"/>
                            </div>
                        </s:form>
                    </div>
                    
                    <div class="custom-legends">
					  <div class="custom-legend pullout"><div class="legend-info">Completed</div></div>
					  <div class="custom-legend pending"><div class="legend-info">Not filled yet</div></div>
					  <div class="custom-legend approved"><div class="legend-info">Finalized</div></div>
					  <div class="custom-legend re_submit"><div class="legend-info">Waiting for completion</div></div>
					</div>
					
                </div>
                <!-- /.box-body -->
	<%if((strSessionUserType!=null && strSessionUserType.equals(IConstants.EMPLOYEE)) || fromPage == null || fromPage.equals("") || fromPage.equalsIgnoreCase("null") ) { %>
		            </div>
		        </section>
		    </div>
		</section> 
	<% } %>
	
<% } else { %>
	<div class="nodata msg">No Review Status.</div>
<% } %>

<div class="modal" id="modalInfo" role="dialog">
    <div class="modal-dialog">
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
 $("#formID").submit(function(event){
	event.preventDefault();
	var form_data = $("#formID").serialize();
	var title = 'Appraisal Bulk Finalization';
  	var dialogEdit = '.modal-body';
  	$(dialogEdit).empty();
  	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
  	$("#modalInfo").show();
  	$('.modal-title').html(title);
  	if($(window).width() >= 900) {
  		$('.modal-dialog').width(900);
  	}
  	$.ajax({
  		type: 'POST',
		 url:"AppraisalBulkFinalization.action",
	     data:form_data,
	     success:function(result){
	    	 $(dialogEdit).html(result);
	     }
  	});
	
 });

 	function googleTranslateElementInit() {
		new google.translate.TranslateElement({
			pageLanguage : 'en',
			includedLanguages : 'en,kn,mr,ta,te,hi'
		}, 'google_translate_element');
	}
 
 	/* @uthor : Dattatray */
 	$(window).bind('mousewheel DOMMouseScroll', function(event){
 	    
 	    if (event.originalEvent.wheelDelta > 0 || event.originalEvent.detail < 0) {
 	        // scroll up
 	        if($(window).scrollTop() == 0 && $("#secAppraisalStatus").scrollTop() != 0){
 	        	$("#secAppraisalStatus").scrollTop($("#secAppraisalStatus").scrollTop() - 30);
 	        }
 	    }
 	    else {
 	        // scroll down
 	        if($(window).scrollTop() + $(window).height() > $(document).height() - 15) {
 	    		   $("#secAppraisalStatus").scrollTop($("#secAppraisalStatus").scrollTop() + 30);
 	   		}
 	    }
 	});

 	/* @uthor : Dattatray */
 	$(window).keydown(function(event){
 	   
		if(event.which == 40 || event.which == 34) {
			if($(window).scrollTop() + $(window).height() > $(document).height() - 15) {
    		   $("#secAppraisalStatus").scrollTop($("#secAppraisalStatus").scrollTop() + 50);
   			}
		} else if(event.which == 38 || event.which == 33) {
		   if($(window).scrollTop() == 0 && $("#secAppraisalStatus").scrollTop() != 0){
	    		$("#secAppraisalStatus").scrollTop($("#secAppraisalStatus").scrollTop() - 50);
	    	}
		}
 	});
</script>