<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<script type="text/javascript" src="scripts/charts/highcharts.js"></script>
<script type="text/javascript" src="scripts/charts/exporting.js"></script>
<script type="text/javascript" src="scripts/charts/highcharts-3d.js"></script>
<script type="text/javascript" src="scripts/charts/no-data-to-display.js"></script>
<script>

	$(function(){
		
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
    		
    		/* $("body").on('click','#closeButton1',function(){
    			$(".modal-dialog1").removeAttr('style');
    			$("#modal-body1").height(400);
    			$("#modalInfo1").hide();
    	    });
    		$("body").on('click','#close1',function(){
    			$(".modal-dialog1").removeAttr('style');
    			$("#modal-body1").height(400);
    			$("#modalInfo1").hide();
    	    }); */
    		
    		/* $("body").on('click','#closeButton1',function(){
    			$(".modal-dialog1").removeAttr('style');
    			$("#modal-body1").height(400);
    			$("#modalInfo1").hide();
    	    }); */
    		
    	});
		
		$("#location").multiselect().multiselectfilter();
		$("#designation").multiselect().multiselectfilter();
		$("#appliSourceName").multiselect().multiselectfilter();
	}); 

	
	function getSelectedValue(selectId) {
		var choice = document.getElementById(selectId);
		var exportchoice = "";
		for ( var i = 0, j = 0; i < choice.options.length; i++) {
			if (choice.options[i].selected == true) {
				if (j == 0) {
					exportchoice = choice.options[i].value;
					j++;
				} else {
					exportchoice += "," + choice.options[i].value;
					j++;
				}
			}
		}
		return exportchoice;
	}
	
	function addRequestWOWorkFlow(fromPage) {
		if(fromPage == "" || fromPage == "null" || fromPage == "NULL") {
			fromPage = "JR";
		}
		var dialogEdit = '.modal-body';
		 $(dialogEdit).empty();
		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		 $("#modalInfo").show();
		 $(".modal-title").html('New Requirement ');
		 if($(window).width() >= 800){
			 $(".modal-dialog").width(800);
		 }
		 $.ajax({
			url : "RequirementRequestWithoutWorkflow.action?frmPage="+fromPage,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}


	function addRequest(fromPage) {

		var id=document.getElementById("requirementRequestDiv");
		if(id){
			id.parentNode.removeChild(id);
		}
		if(fromPage == "" || fromPage == "null" || fromPage == "NULL") {
			fromPage = "JR";
		}
		var dialogEdit = '.modal-body';
		 $(dialogEdit).empty();
		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		 $("#modalInfo").show();
		 $(".modal-title").html('New Requirement');
		 if($(window).width() >= 800){
			 $(".modal-dialog").width(800);
		 }
		 $.ajax({
			url : "RequirementRequest.action?frmPage="+fromPage,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}
	
	function importJobProfiles(orgId) {
		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$("#modalInfo").show();
		$(".modal-title").html('Import Job Profiles');
		$(".modal-body").height('auto');
		$.ajax({
			url : "ImportJobProfiles.action?orgId="+orgId,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}
	
</script>

	<%
		String strUserType = (String) session.getAttribute("USERTYPE");
		UtilityFunctions uF = new UtilityFunctions(); 
		String sbData = (String) request.getAttribute("sbData");
		String strSearchJob = (String) request.getAttribute("strSearchJob");
		String callFrom = (String) request.getAttribute("callFrom");
		String org = (String) request.getAttribute("f_org");
		String strLocation = (String) request.getAttribute("strLocation");
		
		String strDesignation = (String) request.getAttribute("strDesignation");
		String strAppliSourceName = (String) request.getAttribute("strAppliSourceName");
		String appliSourceType = (String) request.getAttribute("appliSourceType");
		String fromPage = (String) request.getAttribute("fromPage");
		String recruitId = (String) request.getAttribute("recruitId");
		
		List<String> recruitmentIDList = (List<String>) request.getAttribute("recruitmentIDList");
		
		Map<String, List<String>> hmJobReport = (Map<String, List<String>>) request.getAttribute("hmJobReport");
		if(hmJobReport == null) hmJobReport = new HashMap<String, List<String>>();
					
		int totalToday1 = 0;
		int total72hrs1 = 0;
		int totalRequired1 = 0;
		int totalOffered1 = 0;
		int totalShortlisted1 = 0;
		int totalFinalization1 = 0;
		int totalScheduled1 = 0;
		
		for (int i = 0; recruitmentIDList != null && !recruitmentIDList.isEmpty() && i < recruitmentIDList.size(); i++) {
		List<String> alinner = (List<String>) hmJobReport.get(recruitmentIDList.get(i));
		if(alinner != null && !alinner.isEmpty()) {
			totalToday1 += uF.parseToInt(alinner.get(3));
			total72hrs1 += uF.parseToInt(alinner.get(4));
			
			totalRequired1 += uF.parseToInt(alinner.get(5));
			totalOffered1 += uF.parseToInt(alinner.get(8));

			totalShortlisted1 += uF.parseToInt(alinner.get(10));
			totalFinalization1 += uF.parseToInt(alinner.get(11));
			totalScheduled1 += uF.parseToInt(alinner.get(14));
		}
	}
		
		String strClassL1 = "class=\"active\"";
		String strClassL2 = "";
		if(callFrom != null && callFrom.equals("IND")) {
			 strClassL1 = "";
			 strClassL2 = "class=\"active\"";
		}
%>
<%=uF.showData((String)session.getAttribute(IConstants.MESSAGE),"") %>
<% session.setAttribute(IConstants.MESSAGE,""); %>

<div class="col-md-12 no-padding">
   <div class="box box-primary collapsed-box" style="border-top-color: #EBEBEB;">
       <div class="box-header with-border">
            <h4 class="box-title" style="font-size: 14px;"><%=(String)request.getAttribute("selectedFilter") %></h4>
             <div class="box-tools pull-right">
                     <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
                     <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
             </div>
        </div>
        <div class="box-body" style="padding: 5px; overflow-y: auto;">
          	<%-- <s:form name="frm_RecruitmentDashboardData" action="RecruitmentDashboardData" theme="simple"> --%>
            <%-- <s:form theme="simple" name="formID" action="RecruitmentDashboardData" > --%>
	    	<s:hidden name="dataType" id="dataType" />
	    	<s:hidden name="pageNumber" id="pageNumber" />
	    	<s:hidden name="minLimit" id="minLimit" />
	    	<input type="hidden" name="usrType" id="usrType" value="<%=strUserType %>" />
    		<div class="row row_without_margin">
				<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
					<i class="fa fa-filter"></i>
				</div>
				<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
					<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
						<p style="padding-left: 5px; margin-bottom: 8px;">Organization</p>
						<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" list="organisationList" key="" 
						headerKey="" headerValue="All Organisations" onchange="submitForm('1');" />
					</div>
					<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
						<p style="padding-left: 5px;">Location</p>
						<s:select theme="simple" name="location" id="location" listKey="wLocationId" listValue="wLocationName" 
					list="workLocationList" key="0"  multiple="true" />
					</div>
					<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
						<p style="padding-left: 5px;">Designation</p>
						<s:select theme="simple" name="designation" id="designation" listKey="desigId" listValue="desigCodeName" 
						list="desigList" key="" multiple="true" />
					</div>
					<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
						<p style="padding-left: 5px;">Source Type</p>
						<s:select theme="simple" name="appliSourceType" id="appliSourceType" listKey="sourceTypeId" listValue="sourceTypeName" 
					list="sourceTypeList" key="" headerKey="" headerValue="All" onchange="submitForm('2');" />
					</div>
					<% if(uF.parseToInt(appliSourceType) != IConstants.SOURCE_WEBSITE) { %>
					<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
						<p style="padding-left: 5px;">Source Name</p>
						<s:select theme="simple" name="appliSourceName" id="appliSourceName" listKey="sourceTypeId" listValue="sourceTypeName" 
						list="sourceNameList" key="0" multiple="true" />
					</div>
					<% } %>
					<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
						<p style="padding-left: 5px;">&nbsp;</p>
						<input type="button" value="Submit" class="btn btn-primary" onclick="submitForm('2');"/>
					</div>
				</div>
			</div>
	    <%-- </s:form> --%>
	 </div>
	</div>
 </div>
 
 
 	<div style="float: left;width:100%; margin: 10px 0px;">
	<% System.out.println("sbmsg in jsp "+session.getAttribute("sbMessage"));
	if(request.getAttribute("sbMessage")!=null) {%>
		<%=(String)request.getAttribute("sbMessage")%>
		<% request.setAttribute("sbMessage","");%>	
	<% } else if(session.getAttribute("sbMessage")!=null) { %>
		<%=(String)session.getAttribute("sbMessage") %>	
		<% session.setAttribute("sbMessage",""); %>	
	<% } %>
	</div>
	
  <div class="col-md-12 no-padding" style="margin-bottom: 15px;">
  		<div class="col-md-3 no-padding">
			<%-- <span>Search:</span> --%>
			<input type="text" id="strSearchJob" class="form-control" name="strSearchJob" placeholder="Search" value="<%=uF.showData(strSearchJob, "") %>"/>
			<input type="button" value="Search" class="btn btn-primary" onclick="submitForm('2');">
		</div>
	       
		<script>
	       $(function(){
	    	   $("#strSearchJob" ).autocomplete({
					source: [ <%=uF.showData(sbData,"") %> ]
				});
	       });
			
	  	</script>
	  	<div class="col-md-9 no-padding">
	  	<% if (strUserType != null && (strUserType.equalsIgnoreCase(IConstants.MANAGER) || strUserType.equalsIgnoreCase(IConstants.ADMIN)) || strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.RECRUITER)) { %>
			<% if (strUserType != null && !strUserType.equalsIgnoreCase(IConstants.MANAGER)) { %>
				Permalink: <a href="JobOpportunities.action">All Jobs</a>
				<a href="javascript:void(0)" onclick="addRequestWOWorkFlow('<%=fromPage%>')"><input type="button" class="btn btn-primary pull-right" value="New Requirement without Approval"> </a>
			 <% } %>
			 	<a href="javascript:void(0)" onclick="addRequest('<%=fromPage%>')"><input type="button" class="btn btn-primary pull-right" value="New Requirement with Approval"> </a>
			 	<% if (strUserType != null && !strUserType.equalsIgnoreCase(IConstants.MANAGER)) { %>
			 		<a href="javascript: void(0);" title="Import Job Profiles" class="pull-right" style="margin: 5px 5px 0px 0px;" onclick="importJobProfiles('<%=org %>');"><i class="fa fa-upload"></i> Job Profiles </a>
			 	<% } %>
		<% } %>
		</div>
  </div>
 <div class="row row_without_margin">
	<div class="col-md-3" style="padding-left: 0px;">
		<div class="box box-none">
			<div class="nav-tabs-custom"> 
	             <ul class="nav nav-tabs">
	               <!-- Started By Dattatray Date:19-10-21 -->
	                 <li class="active"><a href="javascript:void(0)" onclick="getJobNamesList('JobNamesList','L','<%=org%>','<%=strLocation%>','<%=strDesignation%>','<%=strAppliSourceName%>','<%=appliSourceType%>','<%=strSearchJob%>','<%=fromPage%>', '','','0');" data-toggle="tab"  id="rDDataLCID0">Live</a></li>
	                 <li><a href="javascript:void(0)" onclick="getJobNamesList('JobNamesList','C','<%=org%>','<%=strLocation%>','<%=strDesignation%>','<%=strAppliSourceName%>','<%=appliSourceType%>','<%=strSearchJob%>','<%=fromPage%>', '','','1');" data-toggle="tab" id="rDDataLCID1">Closed</a></li>
	              <!-- Ended By Dattatray Date:19-10-21 -->
	            </ul>
	             <div class="tab-content" style=" max-height: 600px !important; overflow-y: hidden;" id="leftSectionRecruitment"><!--Created by Dattatray Note : added style and id -->
	                 <div class="active tab-pane" id="subDivResult" style="max-height: 600px;">
				
	                 </div>
	             </div>
	        </div>
		</div>
	</div>    

	<div class="col-md-9" style="padding-left: 0px; min-height: 600px;">
		<div class="box box-none" style="overflow-y: auto;">
			<div class="nav-tabs-custom">
	            <ul class="nav nav-tabs">
	              <!-- Started By Dattatray Date:19-10-21 -->
	            	<li <%=strClassL1%>><a href="javascript:void(0)" onclick="getJobProfileDataPage('ReportJobProfilePopUp', '&view=jobreport', 'Details','<%=fromPage %>','','0);" data-toggle="tab" id="rDashDataId0">Details</a></li>
	                 <!-- <li><a href="javascript:void(0)" onclick="getJobProfileDataPage('ReportJobProfilePopUp', '&view=jobreport');" data-toggle="tab">Summary</a></li> -->
	                 <li><a href="javascript:void(0)" onclick="getJobProfileDataPage('Applications', '', 'Applications','<%=fromPage %>','','1');" data-toggle="tab" id="rDashDataId1">Applications</a></li>
	                 <li><a href="javascript:void(0)" onclick="getJobProfileDataPage('Offers', '', 'Offers','<%=fromPage %>','','2');" data-toggle="tab" id="rDashDataId2">Offers</a></li>
	                 <li <%=strClassL2%>><a href="javascript:void(0)" onclick="getJobProfileDataPage('Induction', '', 'On-boards','<%=fromPage %>','','3');" data-toggle="tab" id="rDashDataId3">On-boards</a></li>
	                 <li><a href="javascript:void(0)" onclick="getJobProfileDataPage('ApplicantTracker', '', 'Applicant Tracker','<%=fromPage %>','','4');" data-toggle="tab" id="rDashDataId4">Applicant Tracker</a></li>
	             <!-- Ended By Dattatray Date:19-10-21 -->
	            </ul>
	            <div class="tab-content" style=" max-height: 600px !important; overflow-y: hidden;" id="rightSectionRecruitment"><!--Created by Dattatray Note : added style and id -->
	            	<s:hidden name="recruitId" id="recruitId"/>
	                <div class="active tab-pane" id="subSubDivResult" style="max-height: 600px;">
				
	                </div>
	            </div>
	        </div>
			 <!-- <div class="active tab-pane" id="subSubDivResult" style="min-height: 600px;">
					
		    </div> -->
		</div>
   </div>
</div>
	
<!-- <div class="modal" id="modalInfo" role="dialog">
    <div class="modal-dialog">
        Modal content
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">-</h4>
            </div>
            <div class="modal-body" id="modal-body" style="height:400px;overflow-y:auto;padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div> -->


<!-- <div class="modal" id="modalInfo1" role="dialog">
    <div class="modal-dialog modal-dialog1">
        Modal content
        <div class="modal-content">
            <div class="modal-header">
            	<button type="button" class="close1" id="close1" data-dismiss="modal">&times;</button>
                <h4 class="modal-title modal-title1"></h4>
            </div>
            <div class="modal-body" id="modal-body1" style="height:400px;overflow-y:auto;padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton1" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div> -->


 <script type="text/javascript" charset="utf-8">
	$(document).ready(function() {
		getJobNamesList('JobNamesList','L','<%=org%>','<%=strLocation%>','<%=strDesignation%>','<%=strAppliSourceName%>','<%=appliSourceType%>','<%=strSearchJob%>','<%=fromPage%>','<%=callFrom%>','<%=recruitId%>','0');
		getJobProfileDataPage('ReportJobProfilePopUp', '&view=jobreport', 'Details','<%=fromPage %>','','0');//Created By Dattatray Date:19-10-21
	});
	
	function getJobNamesList(strAction, dataType, org, loc, desig, sourceName, sourceType, search, fromPage, callFrom, recruitId,index){
		//console.log("getJobNamesList");
		disabledPointerAddAndRemove(2,'rDDataLCID',index,true);//Created By Dattatray Date:19-10-21
		$("#subDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: strAction+'.action?dataType='+dataType+'&f_org='+org+'&location='+loc+'&designation='+desig+'&appliSourceType='+sourceType+'&appliSourceName='
				+sourceName+'&strSearchJob='+search+'&fromPage='+fromPage+'&callFrom='+callFrom+'&recruitId='+recruitId,
			success: function(result){
				$("#subDivResult").html(result);
				disabledPointerAddAndRemove(2,'rDDataLCID',index,false);//Created By Dattatray Date:19-10-21
	   		}
		});
	}

	
	function getJobProfileDataPage(strAction, parameters, tabName,fromPage,rId,index) {
		
		//console.log("getJobDetails jsp strAction ===>> " + strAction+"==tabName==>"+tabName);
		disabledPointerAddAndRemove(5,'rDashDataId',index,true);//Created By Dattatray Date:19-10-21
		var recruitId = document.getElementById("recruitId").value;
		
		if(rId !== "") {
			recruitId = rId;
		}
		var form_data = $("#"+this.id).serialize();
		//alert("form_data ===>> "  +form_data);
		$("#subSubDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({ 
			type : 'POST',
			url: strAction+'.action?recruitId='+recruitId+parameters+"&fromPage="+fromPage,
			data: form_data,
			success: function(result){
				$("#subSubDivResult").html(result);
				$(".col-md-9 .nav-tabs-custom .nav-tabs").find("li").removeClass("active");
				$(".col-md-9").find('a:contains('+tabName+')').parent().addClass("active");
				disabledPointerAddAndRemove(5,'rDashDataId',index,false);//Created By Dattatray Date:19-10-21
	   		}
		});
	}
	
	
	function submitForm(type) {
		
		var org = document.getElementById("f_org").value;
		var strLocation = getSelectedValue("location");
		var strDesignation = getSelectedValue("designation");
		//alert("strLocation ===>> " + strLocation+" -- strDesignation ===>> " + strDesignation);
		var strAppliSourceType = "";
		if(document.getElementById("appliSourceType")) {
			strAppliSourceType = getSelectedValue("appliSourceType"); 
		}
		var appliSourceName = "";
		if(document.getElementById("appliSourceName")) {
			appliSourceName = getSelectedValue("appliSourceName"); 
		}
		var strSearch = document.getElementById("strSearchJob").value;
		var paramValues = "";
		if(type != "" && type == '2') {
			paramValues = '&strLocation='+strLocation+'&strDesignation='+strDesignation+'&appliSourceType='+strAppliSourceType
    		+'&appliSourceName='+appliSourceName+'&strSearchJob='+strSearch;
		}
        
    	var action = 'RecruitmentDashboardData.action?f_org='+org+paramValues;
    	//alert("action=>"+action);
    	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$.ajax({
    		type : 'POST',
    		url: action,
    		success: function(result){
            	$("#divResult").html(result);
            	$("#location").multiselect().multiselectfilter();
        		$("#designation").multiselect().multiselectfilter();
        		$("#appliSourceName").multiselect().multiselectfilter();
       		}
    	});
    	
    }
	/* Start Dattatray */
	$(window).bind('mousewheel DOMMouseScroll', function(event){
	    if (event.originalEvent.wheelDelta > 0 || event.originalEvent.detail < 0) {
	        // scroll up
	        if($(window).scrollTop() == 0 && $("#rightSectionRecruitment").scrollTop() != 0){
	        	$("#rightSectionRecruitment").scrollTop($("#rightSectionRecruitment").scrollTop() - 30);
	        }
	        if($(window).scrollTop() == 0 && $("#leftSectionRecruitment").scrollTop() != 0){
	        	$("#leftSectionRecruitment").scrollTop($("#leftSectionRecruitment").scrollTop() - 30);
	        }
	    }else {
	        // scroll down
	        if($(window).scrollTop() + $(window).height() > $(document).height() - 15) {
	    		   $("#rightSectionRecruitment").scrollTop($("#rightSectionRecruitment").scrollTop() + 30);
	   		}
	        if($(window).scrollTop() + $(window).height() > $(document).height() - 15) {
	    		   $("#leftSectionRecruitment").scrollTop($("#leftSectionRecruitment").scrollTop() + 30);
	   		}
	    }
	});
	
	$(window).keydown(function(event){
		if(event.which == 40 || event.which == 34){
			if($(window).scrollTop() + $(window).height() > $(document).height() - 15) {
	    		   $("#rightSectionRecruitment").scrollTop($("#rightSectionRecruitment").scrollTop() + 50);
	   		}
			if($(window).scrollTop() + $(window).height() > $(document).height() - 15) {
	    		   $("#leftSectionRecruitment").scrollTop($("#leftSectionRecruitment").scrollTop() + 50);
	   		}
		} else if(event.which == 38 || event.which == 33){
		   if($(window).scrollTop() == 0 && $("#rightSectionRecruitment").scrollTop() != 0){
	    		$("#rightSectionRecruitment").scrollTop($("#rightSectionRecruitment").scrollTop() - 50);
	    	}
		   if($(window).scrollTop() == 0 && $("#leftSectionRecruitment").scrollTop() != 0){
	    		$("#leftSectionRecruitment").scrollTop($("#leftSectionRecruitment").scrollTop() - 50);
	    	}
		}
	});/* End Dattatray */
</script>