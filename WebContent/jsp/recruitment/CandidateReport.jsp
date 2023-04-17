<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>
<%
    String strUserType = (String) session.getAttribute(IConstants.USERTYPE);
    UtilityFunctions uF = new UtilityFunctions();
    Map<String, String> hmEducationDetails = (Map)request.getAttribute("hmEducationDetails");
    Map<String, String> hmExperienceDetails = (Map)request.getAttribute("hmExperienceDetails");
    Map<String, String> hmSkillDetails = (Map)request.getAttribute("hmSkillDetails");
    %>

<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script type="text/javascript">
    $(document).ready(function() {
    	$('#lt').DataTable({
    		aLengthMenu: [
    			[25, 50, 100, 200, -1],
    			[25, 50, 100, 200, "All"]
    		],
    		iDisplayLength: -1,
    		dom: 'lBfrtip',
            buttons: [
    			'copy', 'csv', 'excel'
            ],
            order: [],
    		columnDefs: [ {
    	      "targets"  : 'no-sort',
    	      "orderable": false
    	    }]
    	});
    });  
    
    
    function getCandiApplicationsDetailsPopup(candidateId, candiName) {
    	var dialogEdit = '.modal-body';
	    $(dialogEdit).empty();
	    $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	    $("#modalInfo").show();
	    $(".modal-title").html(''+candiName +'\'s Tracker');
	    $.ajax({
		    url : "getCandidateApplicationsDetails.action?candidateId=" + candidateId,
		    cache : false,
		    success : function(data) {
		    	$(dialogEdit).html(data);
		    }
	    });
    }
  
    
    function sendOffer(candidateId) {
    	
    	var dialogEdit = '.modal-body';
    	$(dialogEdit).empty();
	    $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	    $('.modal-title').html('Send offer to Candidate');
	    $("#modalInfo").show();
	    /* if($(window).width() >= 900){
	     $(".modal-dialog").width(900);
	    } */
	    var height = $(window).height()* 0.95;
		var width = $(window).width()* 0.95;
		$(".modal-dialog").css("height", height);
		$(".modal-dialog").css("width", width);
		$(".modal-dialog").css("max-height", height);
		$(".modal-dialog").css("max-width", width);
	    $.ajax({
	    	  url : "sendCandidateOffer.action?candidateId=" + candidateId,
			    cache : false,
			    success : function(data) {
			    	$(dialogEdit).html(data);
			    }
		    });
	    
	    }
	   
    
    function addCandidateModePopup(recruitID) {
        
	     var id=document.getElementById("panelDiv");
	     if(id){
	     id.parentNode.removeChild(id);
	     }
	     var heght = '700';
	     var wdth = '96%';
	     if(recruitID == ''){
	     heght = '500';
	     wdth = '850';
	     }
	     var dialogEdit = '.modal-body';
	    $(dialogEdit).empty();
	    $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	    $("#modalInfo").show();
	    $(".modal-title").html('Add Candidate');
	    if($(window).width() >= 900){
	    	$(".modal-dialog").width(900);
	    }
	    $.ajax({
		    url :"AddCandidateModePopup.action?recruitId="+recruitID+"&fromPage=CR" ,
		    cache : false,
		    success : function(data) {
		    	$(dialogEdit).html(data);
		    }
	    });
    }
    
    
    function addCandidateShortFormPopup(recruitID) {
    
	    var heght = '700';
	    var wdth = '95%';
	    var dialogEdit = '.modal-body';
	    $(dialogEdit).empty();
	    $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	    $("#modalInfo").show();
	    $(".modal-title").html('Add Candidate In One Step');
	    if($(window).width() >= 900){
	    	$(".modal-dialog").width(900);
	    }
	    $.ajax({
		    url :"AddCandidateInOneStep.action?recruitId="+recruitID+"&fromPage=CR" ,
		    cache : false,
		    success : function(data) {
		    	$(dialogEdit).html(data);
		    }
	    });
    }
    

    function importCandidate() {
		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$("#modalInfo").show();
		$(".modal-title").html('Import Candidates');
		$(".modal-body").height('auto');
		$.ajax({
			url : "ImportCandidate.action",
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}
    
    var dialogEdit3 = '#addCandidateDiv';
    function addNewCandidate(recruitID, fromPage) {
    
    var id=document.getElementById("panelDiv");
    if(id){
    id.parentNode.removeChild(id);
    }
    var dialogEdit = '.modal-body';
    $(dialogEdit).empty();
    $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    $("#modalInfo").show();
    $(".modal-title").html('Add New Candidate');
    if($(window).width() >= 1100){
    	$(".modal-dialog").width(1100);
    }
    if($(window).height() >= 600){
    	$(".modal-body").height(600);
    }
    $.ajax({
     	url :"AddCandidate.action?recruitId="+recruitID+"&fromPage="+fromPage,
     	cache : false,
     	success : function(data) {
     		$(dialogEdit).html(data);
     	}
     });
    }
    
    
    function checkUncheckAllCandidates() {
    var allCandi = document.getElementById("allCandidates");		
    var candidate = document.getElementsByName('candidates');
    //alert("allLivePr.checked ==>> " + allLivePr.checked);
    if(allCandi.checked == true) {
    for(var i=0;i<candidate.length;i++) {
     candidate[i].checked = true;
    }
    document.getElementById("unblockedSpan").style.display = 'none';
    document.getElementById("blockedSpan").style.display = 'inline';
    } else {		
    for(var i=0; i<candidate.length; i++) {
     candidate[i].checked = false;
    }
    document.getElementById("unblockedSpan").style.display = 'inline';
    document.getElementById("blockedSpan").style.display = 'none';
    }
    }
     
    
    function checkAllCandidateCheckedUnchecked() {
    var allCandi = document.getElementById("allCandidates");
    var candidate = document.getElementsByName('candidates');
    var cnt = 0;
    var chkCnt = 0;
    for(var i=0;i<candidate.length;i++) {
    cnt++;
    if(candidate[i].checked) {
     chkCnt++;
    }
    }
    if(parseFloat(chkCnt) > 0) {
    document.getElementById("unblockedSpan").style.display = 'none';
    document.getElementById("blockedSpan").style.display = 'inline';
    } else {
    document.getElementById("unblockedSpan").style.display = 'inline';
    document.getElementById("blockedSpan").style.display = 'none';
    }
    
    if(cnt == chkCnt) {
    allCandi.checked = true;
    } else {
    allCandi.checked = false;
    }
    }
    
    function getCheckedValue() {
    //var choice = document.getElementById(checkedId);
    var candidate = document.getElementsByName('candidates');
    //alert("candidate = ==>> " + candidate.length);
    //alert("choice = ==>> " + choice.length);
    var exportchoice = "";
    for ( var i = 0, j = 0; i < candidate.length; i++) {
    if (candidate[i].checked) {
    if (j == 0) {
    	exportchoice = candidate[i].value;
    	j++;
    } else {
    	exportchoice += "," + candidate[i].value;
    	j++;
    }
    }
    }
    //alert("exportchoice ====>> " + exportchoice);
    return exportchoice;
    }
    
    function addSelectedCandidateInJob() {
    var candidate = getCheckedValue();
    
    
    var dialogEdit = '.modal-body';
    $(dialogEdit).empty();
    $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    $("#modalInfo").show();
    $(".modal-title").html('Add to Job');
    $.ajax({
    url : "AddSelectedCandidateInJob.action?candidateId=" + candidate,
    cache : false,
    success : function(data) {
    $(dialogEdit).html(data);
    }
    });
    
    }
    
    function isNumberKey(evt) {
     var charCode = (evt.which) ? evt.which : event.keyCode;
     if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46)
        return false;
    
     return true;
    }
    
    function checkCurrentCTC(value) {
    if(value > 10000000) {
    alert('Entered amount more than range.');
    document.getElementById("currCTC").value = "0";
    }
    }
    
    function checkExpectedCTC(value) {
    if(value > 10000000) {
    alert('Entered amount more than range.');
    document.getElementById("expectedCTC").value = "0";
    }
    }
    
    function checkNoticeDays(value) {
    if(value > 365) {
    alert('Entered days more than range.');
    document.getElementById("noticePeriod").value = "0";
    }
    }
    
    function exportCandidateXMLReport() {
		var f_org = document.getElementById("orgId").value;
		var f_wlocation = document.getElementById("f_wlocation").value;
		if(f_wlocation=="") {
			f_wlocation = null;
		}
	    /* window.location="CandidateXMLReport.action?f_org="+f_org+"&f_wlocation="+f_wlocation; */
	    window.location="CandidateXMLReport.action?f_org="+f_org;
    }
    
    
	function exportCandidateCSVReport() {
		var f_org = document.getElementById("orgId").value;
		var f_wlocation = document.getElementById("f_wlocation").value;
		if(f_wlocation=="") {
			f_wlocation=null;
		}
		/* window.location="CandidateCSVReport.action?f_org="+f_org+"&f_wlocation="+f_wlocation; */
		window.location="CandidateCSVReport.action?f_org="+f_org;
	}
    
    
</script>
<script type="text/javascript">
    $(function(){
    	$("#f_wlocation").multiselect().multiselectfilter();
    	$("#strMinEducation").multiselect().multiselectfilter();
    	$("#strSkills").multiselect().multiselectfilter();
    	$("#strExperience").multiselect().multiselectfilter();
    	$("#checkStatus_reportfilter").multiselect().multiselectfilter();
    	$("body").on('click','#closeButton',function(){
    		$(".modal-body").height(400);
    		$(".modal-dialog").removeAttr('style');
    		$("#modalInfo").hide();
        });
    	$("body").on('click','.close',function(){
    		$(".modal-dialog").removeAttr('style');
    		$("#modalInfo").hide();
    		$(".modal-body").height(400);
    	});
    });    
</script>
<%
    List<List<String>> reportList = (List<List<String>>) request.getAttribute("reportList");
    String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
    List<String> alNewCandidate = (List<String>) request.getAttribute("alNewCandidate");
    if(alNewCandidate == null) alNewCandidate = new ArrayList<String>();
    %>
<%-- 	<jsp:include page="../common/SubHeader.jsp">
    <jsp:param value="Candidate Database"  name="title" />
    </jsp:include> --%>
<section class="content">
    <div class="row jscroll">
        <section class="col-lg-12 connectedSortable">
            <div class="box box-primary">
                <div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
                    <div id="printDiv" class="leftbox reportWidth">
                    	<div class="box box-default collapsed-box" style="margin-top: 10px;">
			                <div class="box-header with-border">
			                     <h3 class="box-title" style="font-size: 14px;"><%=(String)request.getAttribute("selectedFilter") %></h3>
			                    <div class="box-tools pull-right">
			                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
			                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
			                    </div>
			                </div>
			                <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
			                    <s:form name="frm_candidate_report_filter" action="CandidateReport" theme="simple">
		                            <div class="row row_without_margin">
										<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
											<i class="fa fa-filter"></i>
										</div>
										<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
											<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
												<p style="padding-left: 5px;">Organization</p>
												<s:select theme="simple" name="f_org" listKey="orgId" id="orgId"
		                                            listValue="orgName" list="organisationList" key="" onchange="document.frm_candidate_report_filter.submit()" />
											</div>
											<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
												<p style="padding-left: 5px;">Location</p>
												<s:select theme="simple" name="f_wlocation" id="f_wlocation" listKey="wLocationId"
		                                            listValue="wLocationName " list="workList" key="" multiple="true"/>
											</div>
											<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
												<p style="padding-left: 5px;">Education</p>
												<s:select  name="strMinEducation" id="strMinEducation" list="eduList" theme="simple" listKey="eduId" listValue="eduName" 
		                                             multiple="true"/>
											</div>
											<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
												<p style="padding-left: 5px;">Skill</p>
												<s:select name="strSkills" id="strSkills" list="skillsList" theme="simple" listKey="skillsId" listValue="skillsName" 
		                                             multiple="true"/>
											</div>
											<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
												<p style="padding-left: 5px;">Experience</p>
												<s:select theme="simple" name="strExperience" id="strExperience"  multiple="true" 
												list="expYearsList" listKey="expYearsId" listValue="expYearsName" />
											</div>
											<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
												<p style="padding-left: 5px;">Status</p>
												<s:select theme="simple" name="checkStatus_reportfilter" id="checkStatus_reportfilter" list="statusList" 
												 listKey="statusId" listValue="statusName" multiple="true"/>
											</div>
										</div>
									</div>
		                            <div class="row row_without_margin margintop10">
										<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
											<i class="fa fa-filter visiblehide"></i>
										</div>
										<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
											<s:hidden name="recruitId"></s:hidden>
											<div class="col-lg-2 col-md-4 col-sm-6 autoWidth paddingleftright5">
		                                        <p>Current CTC</p>
		                                        <s:textfield name="currCTC" id="currCTC" readonly="true" onkeypress="return isNumberKey(event)"/>
		                                        <br/>
		                                        <div id="currCTCSlider" style="margin-top: 7px;width: 194px;"></div>
		                                        <div id="currCTCSliderMinMax" style="margin-top: 10px;">0 <span style="float:right;">1 Cr.</span></div>
		                                    	<script>
		                                            $(function() {
		                                                $( "#currCTCSlider" ).slider({
			                                                range: true,
			                                                min: 0,
			                                                max: 10000000,
			                                                step : 50000,
			                                                values: [<%=uF.parseToDouble((String)request.getAttribute("minCurrCTC")) %>, <%=uF.parseToDouble((String)request.getAttribute("maxCurrCTC")) %>],
			                                                slide: function( event, ui ) {
			                                                $( "#currCTC" ).val(ui.values[ 0 ] + " - " + ui.values[ 1 ] );
			                                                }
		                                                });
		                                                $( "#currCTC" ).val($("#currCTCSlider").slider("values", 0 ) +
		                                                " - " + $( "#currCTCSlider").slider("values", 1 ) );
		                                            });
		                                        </script>	
		                                    </div>
		                                    <div class="col-lg-2 col-md-4 col-sm-6 autoWidth paddingleftright5">
		                                        <p>Expected CTC</p>
		                                        <s:textfield name="expectedCTC" id="expectedCTC" readonly="true" onkeypress="return isNumberKey(event)"/>
		                                        <br/>
		                                        <div id="expectedCTCSlider" style="margin-top: 7px;width: 194px;"></div>
		                                        <div id="expectedCTCSliderMinMax" style="margin-top: 10px;">0 <span style="float:right;">1 Cr.</span></div>
		                                    	<script>
		                                            $(function() {
		                                            	 $( "#expectedCTCSlider" ).slider({
		                                            	 range: true,
		                                            	 min: 0,
		                                            	 max: 10000000,
		                                            	 step : 50000,
		                                            	 values: [<%=uF.parseToDouble((String)request.getAttribute("minExpectedCTC")) %>, <%=uF.parseToDouble((String)request.getAttribute("maxExpectedCTC")) %>],
		                                            	 slide: function( event, ui ) {
		                                            	 $( "#expectedCTC" ).val(ui.values[ 0 ] + " - " + ui.values[ 1 ] );
		                                            	 }
		                                            	 });
		                                            	 $( "#expectedCTC" ).val($("#expectedCTCSlider").slider("values", 0 ) +
		                                            	 " - " + $( "#expectedCTCSlider").slider("values", 1 ) );
		                                            	 });
		                                        </script>
		                                    </div>
		                                    <div class="col-lg-2 col-md-4 col-sm-6 autoWidth  paddingleftright5">
		                                        <p>Notice Period</p>
		                                        <!-- style="float: left; width: 100%; margin-bottom: 5px;" -->
		                                        <s:textfield name="noticePeriod" id="noticePeriod" readonly="true" onkeypress="return isNumberKey(event)"/>
		                                        <br/>
		                                        <div id="noticePeriodSlider" style="margin-top: 7px;width: 194px;"></div>
		                                        <div id="noticePeriodSliderMinMax" style="margin-top: 10px;">0 <span style="float:right;">365</span></div>
		                                    	<script>
		                                            $(function() {
		                                            	 $( "#noticePeriodSlider" ).slider({
		                                            	 range: true,
		                                            	 min: 0,
		                                            	 max: 365,
		                                            	 step : 1,
		                                            	 values: [<%=uF.parseToInt((String)request.getAttribute("minNoticePeriod")) %>, <%=uF.parseToInt((String)request.getAttribute("maxNoticePeriod")) %>],
		                                            	 slide: function( event, ui ) {
		                                            	 $( "#noticePeriod" ).val(ui.values[ 0 ] + " - " + ui.values[ 1 ] );
		                                            	 }
		                                            	 });
		                                            	 $( "#noticePeriod" ).val($("#noticePeriodSlider").slider("values", 0 ) +
		                                            	 " - " + $( "#noticePeriodSlider").slider("values", 1 ) );
		                                            	 });
		                                        </script>
		                                    </div>
		                                    <div class="col-lg-2 col-md-4 col-sm-6 autoWidth paddingleftright5">
		                                       <p>Month</p>
		                                       <s:select name="strMonth" headerKey="" headerValue="Select Month" listKey="monthId" listValue="monthName" list="monthList" />
		                                    </div>
		                                    <div class="col-lg-2 col-md-4 col-sm-6 autoWidth paddingleftright5">
		                                    	<p>Year</p>
		                                        <s:select name="strYear" headerKey="" headerValue="Select Year" listKey="yearsID" listValue="yearsName" list="yearList"/>
		                                    </div>
										</div>
									</div><br>
									<div class="row row_without_margin">
										<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
											&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										</div>
										<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
											<div class="col-lg-2 col-md-4 col-sm-6 autoWidth paddingleftright5">
		                                    	<s:submit cssClass="btn btn-primary" value="Submit" align="center"/>
		                                    </div>
		                                    <div class="col-lg-2 col-md-4 col-sm-6 autoWidth paddingleftright5">
		                                    	<span style="font-weight: bolder; font-family: digital; color: green; font-size: 26px;"><%=alNewCandidate.size() %></span>&nbsp;New Candidates
		                                        <span style="font-weight: bolder; font-family: digital; color: green; font-size: 26px;"><%=reportList!=null ? reportList.size() : "0" %></span>&nbsp;Candidates
		                                    </div>
		                                </div>
		                            </div>
		                        </s:form>
			                </div>
			            </div>
						<%=uF.showData((String)session.getAttribute(IConstants.MESSAGE),"") %>
						
						<% 	session.setAttribute(IConstants.MESSAGE, "");
						/* System.out.println("sbmsg in jsp "+session.getAttribute("sbMessage")); */
							if(request.getAttribute("sbMessage")!=null) {%>
								<%=(String)request.getAttribute("sbMessage")%>
								<% request.setAttribute("sbMessage","");%>	
							<% } else if(session.getAttribute("sbMessage")!=null) { %>
								<%=(String)session.getAttribute("sbMessage") %>	
								<% session.setAttribute("sbMessage",""); %>	
							<% } %>
                        <% if (strUserType != null && (strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.RECRUITER) || 
                            strUserType.equalsIgnoreCase(IConstants.CEO) || strUserType.equalsIgnoreCase(IConstants.CFO))) {
                        %>
                        <div class="row row_without_margin">
	                        <div class="col-lg-6 col-md-6 col-sm-6 autoWidth">
		                        <a href="javascript:void(0)" onclick="addCandidateModePopup('');"><i class="fa fa-plus-circle"></i> Add New Candidate(8 Step)</a>&nbsp;&nbsp;
		                        <a href="javascript:void(0)" onclick="addCandidateShortFormPopup('');"><i class="fa fa-plus-circle"></i> Add New Candidate(1 Step)</a>&nbsp;
		                        <a href="javascript:void(0)" onclick="importCandidate('');" title="Candidate Bulk Import"><i class="fa fa-upload"></i> Candidates</a> 
		                        <!-- <input type="button" onclick="addCandidateModePopup('')" value="Add New Candidate(8 Step)" class="btn btn-primary">&nbsp;&nbsp;
		                        <input type="button" onclick="addCandidateShortFormPopup('')" value="Add New Candidate(1 Step)" class="btn btn-primary">&nbsp;&nbsp; -->
	                        </div>
                            <div class="col-lg-6 col-md-6 col-sm-6 autoWidth" style="float:right;">
                                <a href="javascript:void(0)" title="Candidate Excel Report" onclick="exportCandidateCSVReport();">
                                	<i class="fa fa-file-excel-o"></i>
                                </a> 
                                <a href="javascript:void(0)" title="Candidate XML Report" onclick="exportCandidateXMLReport();">
                                	<i class="fa fa-file-text-o"></i>
                                </a>
                                <span id="unblockedSpan">
                                <input type="button" name="blocked" class="btn btn-primary disabled" value="Add to Job"/>
                                </span>
                                <span id="blockedSpan" style="display: none;">
                                <input type="button" name="blocked" class="btn btn-primary" value="Add to Job" onclick="addSelectedCandidateInJob();"/>
                                </span>
                            </div>
                        </div>
                        <% } %>
                        <div style="margin-top:30px;"></div>
                        <table id="lt" class="table table-bordered" style="width:100%;">
                            <thead>
                                <tr>
                                    <th style="text-align: center;" class="no-sort">
                                        <% if (reportList != null && reportList.size() > 0) { %>
                                        <input type="checkbox" name="allCandidates" id="allCandidates" onclick="checkUncheckAllCandidates();" />
                                        <% } %>
                                    </th>
                                    <th style="text-align: left;">Candidate Name</th>
                                    <!-- <th style="text-align: left;">Age</th>
                                        <th style="text-align: left;">Existing Location</th> -->
                                    <th style="text-align: left;">Education</th>
                                    <th style="text-align: left;">Skills</th>
                                    <th style="text-align: left;">Experience</th>
                                    <th style="text-align: left;">Current CTC</th>
                                    <th style="text-align: left;">Expected CTC</th>
                                    <th style="text-align: left;">Notice Period</th>
                                    <th style="text-align: left;">Last Applied/Updated</th>
                                    <th style="text-align: left;">Last Job Title</th>
                                    <!-- <th style="text-align: left;">Job Code</th>
                                        <th style="text-align: left;">Application Date</th> -->
                                    <th style="text-align: left;" class="no-sort">Profile</th>
                                    <!-- Started By Dattatray Date:08-10-21 -->
                                    <th style="text-align: left;" class="no-sort">Status</th>
                                    <!-- Ended By Dattatray Date:08-10-21 -->
                                    <th style="text-align: left;" class="no-sort">Action</th>
                                    
                                </tr>
                            </thead>
                            <tbody>
                                <%
                                    for (int i = 0;i<reportList.size(); i++) {
                                    List<String> innerList = (List<String>) reportList.get(i);
                                    %>
                                <tr>
                                    <td><input type="checkbox" value="<%=(String)innerList.get(2) %>" name="candidates" id="candidates" onclick="checkAllCandidateCheckedUnchecked();"/></td>
                                    <td>
                                        <span style="float:left;">
                                        <%if(docRetriveLocation == null) { %>
                                        <img height="22" width="22" border="0" class="lazy img-circle" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + innerList.get(3) %>" />
                                        <%} else { %>
                                        <img height="22" width="22" border="0" class="lazy img-circle" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_CANDIDATE+"/"+IConstants.I_IMAGE+"/"+innerList.get(2)+"/"+IConstants.I_22x22+"/"+innerList.get(3)%>" />
                                        <%} %> 
                                        <%=innerList.get(4) %> 
                                        <% if(uF.parseToBoolean(innerList.get(17))) { %>
                                        <img style="width: 14px;" src="images1/icons/hd_tick_20x20.png" title="Available">
                                        <% } else { %>
                                        <img style="width: 12px;" src="images1/icons/hd_cross_16x16.png" title="Not Available">
                                        <% } %>
                                        </span>
                                        <%if(alNewCandidate.contains(innerList.get(2).trim())) { %>
                                        <span style="float:right;">
                                        <img border="0" style="float: roght;" src="<%=request.getContextPath()%>/images1/icons/news_icon.gif"/>
                                        </span>	
                                        <%} %>
                                    </td>
                                    <%-- <td><%=innerList.get(10)%></td>
                                        <td><%=innerList.get(11)%></td> --%>
                                    <td><%=uF.showData((String)hmEducationDetails.get((String)innerList.get(2)), "N/A") %></td>
                                    <td><%=uF.showData((String)hmSkillDetails.get((String)innerList.get(2)), "N/A") %></td>
                                    <td><%=uF.showData((String)hmExperienceDetails.get((String)innerList.get(2)), "N/A") %></td>
                                    <td><%=innerList.get(14) %></td>
                                    <td><%=innerList.get(15) %></td>
                                    <td><%=innerList.get(16) %></td>
                                    <%-- <td><%=innerList.get(1)%></td>
                                        <td><%=innerList.get(3)%></td> --%>
                                    <td><%=innerList.get(13) %></td>
                                    <td><%=innerList.get(12) %></td>
                                    <td><%=innerList.get(7) %></td>
                                    <!-- Started By Dattatray Date:08-10-21 -->
                                    <td>
                                    <%
                                    	if(uF.parseToInt(innerList.get(18)) == -1){
                                    %>
                                    	Screen Rejected
                                    <%}else if(innerList.get(12) !=null && !innerList.get(12).equals("-") && (uF.parseToInt(innerList.get(18)) == 0 || innerList.get(18) == null || !innerList.get(18).equals(""))){ %>
                                    	Screened Shortlisted
                                    <%}else { %>
                                    	Unscreened
                                    <%} %>
                                    
                                    </td>
                                    <!-- Ended By Dattatray Date:08-10-21 -->
                                    <td><%=innerList.get(9) %></td>
                                    
                                </tr>
                                <% } %>
                                <% if (reportList.size() == 0 || reportList == null) { %>
                                <tr>
                                    <td>&nbsp;</td>
                                    <td>&nbsp;</td>
                                    <td>&nbsp;</td>
                                    <td>&nbsp;</td>
                                    <td>&nbsp;</td>
                                    <td>&nbsp;</td>
                                    <td>&nbsp;</td>
                                    <td>&nbsp;</td>
                                    <td>&nbsp;</td>
                                    <td>&nbsp;</td>
                                    <td>&nbsp;</td>
                                    <td>&nbsp;</td>
                                </tr>
                                <tr>
                                    <td colspan="12">
                                        <div class="nodata msg">
                                            <span>No application within selection.</span>
                                        </div>
                                    </td>
                                </tr>
                                <% } %>
                            </tbody>
                        </table>
                        <br /> <br />
                    </div>
                </div>
                <!-- /.box-body -->
            </div>
        </section>
    </div>
</section>
<script src="<%= request.getContextPath()%>/scripts/select/chosen.jquery.js" type="text/javascript"></script>
<script src="<%= request.getContextPath()%>/scripts/select/prism.js" type="text/javascript" charset="utf-8"></script>
<g:compress>
    <script type="text/javascript">
        var config = {
          '.chosen-select'           : {},
          '.chosen-select-deselect'  : {allow_single_deselect:true},
          '.chosen-select-no-single' : {disable_search_threshold:10},
          '.chosen-select-no-results': {no_results_text:'Oops, nothing found!'},
          '.chosen-select-width'     : {width:"95%"}
        }
        for (var selector in config) {
          $(selector).chosen(config[selector]);
        }
    </script>	
</g:compress>
<div id="addCandidateModeDiv"></div>
<div id="addCandidateDiv"></div>
<div id="addCandiINJob"></div>
<div id="AddCandiInOneStep"></div>
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