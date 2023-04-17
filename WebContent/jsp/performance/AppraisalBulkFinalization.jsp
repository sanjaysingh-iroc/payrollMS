<%@page import="java.util.Iterator"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="java.util.List"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.Map"%>
<%@page import="com.konnect.jpms.select.FillWLocation"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<script type="text/javascript" src="js_bootstrap/datepicker/bootstrap-datepicker.min.js"></script>

<% 	/* Start Dattatray */
	UtilityFunctions uF = new UtilityFunctions();
	Map<String, String> hmFeatureStatus = (Map<String, String>) request.getAttribute("hmFeatureStatus");
	if(hmFeatureStatus == null) hmFeatureStatus = new HashMap<String, String>();
	/* End Dattatray */
%>
<script type="text/javascript">
    
$(function(){
	$("input[type='submit']").click(function(){
		$("#frmAppraisalBulkFinalization").find('.validateRequired').filter(':hidden').prop('required',false);
		$("#frmAppraisalBulkFinalization").find('.validateRequired').filter(':visible').prop('required',true);
	});
	/* Start Dattatray */
	if(document.getElementById("idEffectiveDate")) {
		$("#idEffectiveDate").datepicker({format: 'dd/mm/yyyy'});
	}/* End Dattatray */
});
    
    if(document.getElementById("strActivity")) {
    	var activityId = document.getElementById("strActivity").value;
		selectElements(activityId);
    }
   
    function selectElements(activityId) {
  	  disableAll();
  	  document.getElementById("strIncrementPercentage").value = "";
  	  
  	  if(activityId == <%=IConstants.ACTIVITY_OFFER_ID %>) { //  Offer 
  		  document.getElementById("tdEffectiveLbl").style.display = "table-cell";
  		  document.getElementById("idEffectiveDate").style.display = "table-cell";
  	  } else if(activityId == <%=IConstants.ACTIVITY_APPOINTMENT_ID %>) { // Appointment 
  		  document.getElementById("tdEffectiveLbl").style.display = "table-cell";
  		  document.getElementById("idEffectiveDate").style.display = "table-cell";
  	  } else if(activityId == <%=IConstants.ACTIVITY_PROBATION_ID %>) { // Probation 
  		  document.getElementById("tdEffectiveLbl").style.display = "none";
     		  document.getElementById("idEffectiveDate").style.display = "none";
  		  document.getElementById("probationTR").style.display = "table-row";
  	  } else if(activityId == <%=IConstants.ACTIVITY_EXTEND_PROBATION_ID %>) { // Extend Probation 
  		  document.getElementById("extendProbationTR").style.display = "table-row";
  		  document.getElementById("tdEffectiveLbl").style.display = "none";
  		  document.getElementById("idEffectiveDate").style.display = "none";
  	  } else if(activityId == <%=IConstants.ACTIVITY_CONFIRMATION_ID %>) { // Confirmation 
  		  document.getElementById("tdEffectiveLbl").style.display = "table-cell";
  		  document.getElementById("idEffectiveDate").style.display = "table-cell";
  		  document.getElementById("incrementPercentTR").style.display = "table-row";
  	  } else if(activityId == <%=IConstants.ACTIVITY_TEMPORARY_ID %>) { // Temporary 
  		  document.getElementById("tdEffectiveLbl").style.display = "table-cell";
  		  document.getElementById("idEffectiveDate").style.display = "table-cell";
  	  } else if(activityId == <%=IConstants.ACTIVITY_PERMANENT_ID %>) { // Permanent 
  		  document.getElementById("tdEffectiveLbl").style.display = "table-cell";
  		  document.getElementById("idEffectiveDate").style.display = "table-cell";
  		  document.getElementById("promotionTR").style.display = "table-row";  
  	  } else if(activityId == <%=IConstants.ACTIVITY_TRANSFER_ID %>) { // Transfer 
  		  document.getElementById("tranferTypeTR").style.display = "table-row";
  		  document.getElementById("tdEffectiveLbl").style.display = "table-cell";
  		  document.getElementById("idEffectiveDate").style.display = "table-cell";
  	  } else if(activityId == <%=IConstants.ACTIVITY_PROMOTION_ID %>) { // Promotion 
  		  //levelLTD levelVTD desigLTD desigVTD gradeLTD gradeVTD incrementPercentTR 
  		  document.getElementById("tdEffectiveLbl").style.display = "table-cell";
      	  document.getElementById("idEffectiveDate").style.display = "table-cell";
  		  document.getElementById("levelLTD").style.display = "table-cell";
  		  document.getElementById("levelVTD").style.display = "table-cell";
  		  
  		  document.getElementById("desigLTD").style.display = "table-cell";
  		  document.getElementById("desigVTD").style.display = "table-cell";
  		  
  		  document.getElementById("gradeLTD").style.display = "table-cell";
  		  document.getElementById("gradeVTD").style.display = "table-cell";
  		  
  		  //document.getElementById("incrementPercentTR").style.display = "table-row";
  	  } else if(activityId == <%=IConstants.ACTIVITY_INCREMENT_ID %>) { // Increment 
  		  document.getElementById("incrementTypeTR").style.display = "table-row";
  		  document.getElementById("tdEffectiveLbl").style.display = "table-cell";
  		  document.getElementById("idEffectiveDate").style.display = "table-cell";
  	  } else if(activityId == <%=IConstants.ACTIVITY_GRADE_CHANGE_ID %>) { // Grade change 
  		  document.getElementById("gradeChangeLTD").style.display = "table-cell";
  		  document.getElementById("gradeChangeVTD").style.display = "table-cell";
  		  document.getElementById("tdEffectiveLbl").style.display = "table-cell";
  		  document.getElementById("idEffectiveDate").style.display = "table-cell";
  	  } else if(activityId == <%=IConstants.ACTIVITY_TERMINATE_ID %>) { // Terminate 
  		 // document.getElementById("noticeTR").style.display = "table-row";
  		  document.getElementById("noticeTR").style.display = "none";
  		  document.getElementById("tdEffectiveLbl").style.display = "table-cell";
  		  document.getElementById("idEffectiveDate").style.display = "table-cell";
  	  } else if(activityId == <%=IConstants.ACTIVITY_NOTICE_PERIOD_ID %>) { //  Notice Period 
  		  document.getElementById("noticeTR").style.display = "table-row";
  		  document.getElementById("tdEffectiveLbl").style.display = "table-cell";
  		  document.getElementById("idEffectiveDate").style.display = "table-cell";
  	  } else if(activityId == <%=IConstants.ACTIVITY_RESIGNATION_WITHDRWAL_ID %>) { // Withdrawn Resignation 
  		  document.getElementById("tdEffectiveLbl").style.display = "table-cell";
  	      document.getElementById("idEffectiveDate").style.display = "table-cell";
  	  } else if(activityId == <%=IConstants.ACTIVITY_FULL_FINAL_ID %>) { // Full & Final 
  		  document.getElementById("tdEffectiveLbl").style.display = "table-cell";
  	      document.getElementById("idEffectiveDate").style.display = "table-cell";
  	  } else if(activityId == <%=IConstants.ACTIVITY_NEW_JOINEE_PENDING_ID %>) { // New Joinee Pending 
  		  document.getElementById("tdEffectiveLbl").style.display = "table-cell";
  	      document.getElementById("idEffectiveDate").style.display = "table-cell";
  	  }
  }
    
    
    function selectElements1(transferType) {
   	 //
   	 	disableAll1();
   	 
      	  if(transferType == 'WL') { //  Location
      		  
      		  document.getElementById("locationLTD").style.display = "table-cell";
      		  document.getElementById("locationVTD").style.display = "table-cell";
      	  } else if(transferType == 'DEPT') { // Department
      		  
      		  document.getElementById("deptLTD").style.display = "table-cell";
      		  document.getElementById("deptVTD").style.display = "table-cell";
      	  } else if(transferType == 'LE') { // Legal Entity
      		  
      		  document.getElementById("legalEntityTR").style.display = "table-row";
      		  
      		  document.getElementById("locationLTD").style.display = "none";
      		  document.getElementById("locationVTD").style.display = "none";
      		  
      		  document.getElementById("sbuLTD").style.display = "none";
      		  document.getElementById("sbuVTD").style.display = "none";
      		  
      		  document.getElementById("deptLTD").style.display = "none";
      		  document.getElementById("deptVTD").style.display = "none";
      		  
      		  document.getElementById("levelLTD").style.display = "none";
      		  document.getElementById("levelVTD").style.display = "none";
      		  
      		  document.getElementById("desigLTD").style.display = "none";
      		  document.getElementById("desigVTD").style.display = "none";
      		  
      		  document.getElementById("gradeLTD").style.display = "none";
      		  document.getElementById("gradeVTD").style.display = "none";
      		  
      		  document.getElementById("gradeChangeLTD").style.display = "none";
      		  document.getElementById("gradeChangeVTD").style.display = "none";
      	  
      	  } else { // 
      		  
      		  document.getElementById('strOrganisation').selectedIndex = 0;
          	  document.getElementById('strWLocation').selectedIndex = 0;
          	  document.getElementById('strLevel').selectedIndex = 0;
          	  document.getElementById('strDepartment').selectedIndex = 0;
          	  //document.getElementById('strDesignation').selectedIndex = 0;
   	      if(document.getElementById('strDesignation')){
       	  	document.getElementById('strDesignation').selectedIndex = 0;
       	  }
          	  document.getElementById('empGrade').selectedIndex = 0;
          	  
          	  document.getElementById("legalEntityTR").style.display = "none";
      		  
      		  document.getElementById("locationLTD").style.display = "none";
      		  document.getElementById("locationVTD").style.display = "none";
      		  
      		  document.getElementById("sbuLTD").style.display = "none";
      		  document.getElementById("sbuVTD").style.display = "none";
      		  
      		  document.getElementById("deptLTD").style.display = "none";
      		  document.getElementById("deptVTD").style.display = "none";
      		  
      		  document.getElementById("levelLTD").style.display = "none";
      		  document.getElementById("levelVTD").style.display = "none";
      		  
      		  document.getElementById("desigLTD").style.display = "none";
      		  document.getElementById("desigVTD").style.display = "none";
      		  
      		  document.getElementById("gradeLTD").style.display = "none";
      		  document.getElementById("gradeVTD").style.display = "none";
      		  
      		  document.getElementById("gradeChangeLTD").style.display = "none";
      		  document.getElementById("gradeChangeVTD").style.display = "none";
      	  }
      	  
   }
    
    function showIncrementPercent(value) {
    	  if(value == '1' || value == '2') {
    		  document.getElementById("incrementPercentTR").style.display = "table-row";
    		  document.getElementById("strIncrementPercentage").value = "";
    	  } else {
    		  document.getElementById("incrementPercentTR").style.display = "none";
    		  document.getElementById("strIncrementPercentage").value = "";
    	  }
    }
    
    function disableAll() {
    	if(document.getElementById('strIncrementType')) {
    	  document.getElementById('strIncrementType').selectedIndex = 0;
    	}
    	
    	if(document.getElementById('strTransferType')) {
    		 document.getElementById('strTransferType').selectedIndex = 0;
    	}
    	 
    	if(document.getElementById('strOrganisation')) {
    		 document.getElementById('strOrganisation').selectedIndex = 0;
    	}
    	if(document.getElementById('strWLocation')) {
    		document.getElementById('strWLocation').selectedIndex = 0;
    	}
    	if(document.getElementById('strLevel')) {
    		 document.getElementById('strLevel').selectedIndex = 0;
    	}
    	
    	if(document.getElementById('strDepartment')) {
   		 document.getElementById('strDepartment').selectedIndex = 0;
   		}
    	  
    	if(document.getElementById('strDesignation')){
      	 	document.getElementById('strDesignation').selectedIndex = 0;
      	}
    	
    	if(document.getElementById('empGrade')){
    	  document.getElementById('empGrade').selectedIndex = 0;
    	}
    	  document.getElementById("extendProbationTR").style.display = "none";
    	  document.getElementById("tranferTypeTR").style.display = "none";
    	  document.getElementById("incrementTypeTR").style.display = "none";
    	 // document.getElementById("salaryDetailsDiv").innerHTML = "";
    	  
    	  document.getElementById("incrementPercentTR").style.display = "none";
    	  document.getElementById("legalEntityTR").style.display = "none";
    	  document.getElementById("locationLTD").style.display = "none";
    	  document.getElementById("locationVTD").style.display = "none";
    	  document.getElementById("sbuLTD").style.display = "none";
    	  document.getElementById("sbuVTD").style.display = "none";
    	  document.getElementById("levelLTD").style.display = "none";
    	  document.getElementById("levelVTD").style.display = "none";
    	  document.getElementById("desigLTD").style.display = "none";
    	  document.getElementById("desigVTD").style.display = "none";
    	  
    	  document.getElementById("deptLTD").style.display = "none";
    	  document.getElementById("deptVTD").style.display = "none";
    	  
    	  document.getElementById("gradeLTD").style.display = "none";
    	  document.getElementById("gradeVTD").style.display = "none";
    	  
    	  document.getElementById("gradeChangeLTD").style.display = "none";
    	  document.getElementById("gradeChangeVTD").style.display = "none";
    	  /* document.getElementById("notifyMailTD").style.display = "none"; */
    	  
    	  /* document.getElementById("newStatusLTD").style.display = "none";
    	  document.getElementById("newStatusVTD").style.display = "none"; */
    	  
    	  document.getElementById("noticeTR").style.display = "none";
    	  document.getElementById("probationTR").style.display = "none";
    	  
    }
    
    
    function disableAll1() {
    	//alert("in disableAll1");
    	if(document.getElementById('strOrganisation')) {
    		document.getElementById('strOrganisation').selectedIndex = 0;
    	}
    	
    	if(document.getElementById('strWLocation')) {
    		document.getElementById('strWLocation').selectedIndex = 0;
    	}
    	
    	if(document.getElementById('strLevel')) {
    		document.getElementById('strLevel').selectedIndex = 0;
    	}
    	
    	if(document.getElementById('strDepartment')) {
    		document.getElementById('strDepartment').selectedIndex = 0;
    	}
    	
    	//document.getElementById('strDesignation').selectedIndex = 0;
    	if(document.getElementById('strDesignation')){
    	  	document.getElementById('strDesignation').selectedIndex = 0;
    	  }
    	
    	if(document.getElementById('empGrade')){
    		document.getElementById('empGrade').selectedIndex = 0;
    	}
    	
    	document.getElementById("legalEntityTR").style.display = "none";
    	
    	document.getElementById("locationLTD").style.display = "none";
    	document.getElementById("locationVTD").style.display = "none";
    	
    	document.getElementById("sbuLTD").style.display = "none";
    	document.getElementById("sbuVTD").style.display = "none";
    	
    	document.getElementById("deptLTD").style.display = "none";
    	document.getElementById("deptVTD").style.display = "none";
    	
    	document.getElementById("levelLTD").style.display = "none";
    	document.getElementById("levelVTD").style.display = "none";
    	
    	document.getElementById("desigLTD").style.display = "none";
    	document.getElementById("desigVTD").style.display = "none";
    	
    	document.getElementById("gradeLTD").style.display = "none";
    	document.getElementById("gradeVTD").style.display = "none";
    	
    	document.getElementById("gradeChangeLTD").style.display = "none";
    	document.getElementById("gradeChangeVTD").style.display = "none";
    	  
    }
    /* Start Dattatray */
    <% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_SHOW_ACTIVITY_REVIEW_FINALIZE))) { %>
	    disableAll();
	    disableAll1();
    <% } %>/* End Dattatray */
    function showDesignation(strLevel) {
    	var action = 'GetDesigList.action?strLevel=' + strLevel + "&type=EA";
    	getContent('desigListSpan', action);
     }
     
    function getGrades(value) {
    	var action = 'GetGradeList.action?strDesignation=' + value + "&type=EA";
    	getContent('gradeListSpan', action);
    }
    
    function getWLocSbuDeptLevelByOrg(value) {
    	  //alert("value --->>>>" + value);  
    	  if(parseInt(value) > 0){
    		 
    		  document.getElementById("locationLTD").style.display = "table-cell";
    		  document.getElementById("locationVTD").style.display = "table-cell";
    		  
    		  document.getElementById("sbuLTD").style.display = "table-cell";
    		  document.getElementById("sbuVTD").style.display = "table-cell";
    		  
    		  document.getElementById("deptLTD").style.display = "table-cell";
    		  document.getElementById("deptVTD").style.display = "table-cell";
    		  
    		  document.getElementById("levelLTD").style.display = "table-cell";
    		  document.getElementById("levelVTD").style.display = "table-cell";
    		  
    		  document.getElementById("desigLTD").style.display = "table-cell";
    		  document.getElementById("desigVTD").style.display = "table-cell";
    		  
    		  document.getElementById("gradeLTD").style.display = "table-cell";
    		  document.getElementById("gradeVTD").style.display = "table-cell";
    	  
    	  	  var action = 'GetOrgWLocationList.action?OID=' + value + "&type=EA";
    		  getContent('locationListSpan', action);
    	
    		  var action = 'GetOrgServiceList.action?OID=' + value + "&type=EA";
     		  getContent('sbuListSpan', action);
     		
     		  var action = 'GetOrgDepartmentList.action?OID=' + value + "&type=EA";
     		  getContent('deptListSpan', action);
     		
     		  var action = 'GetOrgLevelList.action?OID=' + value + "&type=EA";
     		  getContent('levelListSpan', action);
    	  } else {
    		  document.getElementById("locationLTD").style.display = "none";
    		  document.getElementById("locationVTD").style.display = "none";
    		  
    		  document.getElementById("sbuLTD").style.display = "none";
    		  document.getElementById("sbuVTD").style.display = "none";
    		  
    		  document.getElementById("deptLTD").style.display = "none";
    		  document.getElementById("deptVTD").style.display = "none";
    		  
    		  document.getElementById("levelLTD").style.display = "none";
    		  document.getElementById("levelVTD").style.display = "none";
    		  
    		  document.getElementById("desigLTD").style.display = "none";
    		  document.getElementById("desigVTD").style.display = "none";
    		  
    		  document.getElementById("gradeLTD").style.display = "none";
    		  document.getElementById("gradeVTD").style.display = "none";
    	  }
    }
    
    function getGradesByLevel() {
    	  var levelId = document.getElementById("hidelevelId").value;
    	  //alert("levelId ===>> " + levelId);
    	  var action = 'GetGradeList.action?levelId=' + levelId + "&type=EA";
    	  getContent('gradeListSpan', action);
    }	
    
    function isOnlyNumberKey(evt){
 	   var charCode = (evt.which) ? evt.which : event.keyCode;
 	   if (charCode <= 31 || (charCode >= 48 && charCode <= 57)) {
 	      return true; 
 	   }
 	   return false;
 }
 	
 function checkNoOfDays(type) {
 	var days = "";
 	if(type == 'N' ) {
 		days = document.getElementById("strNoticePeriod").value;
 		if(parseInt(days) > 180) {
 			document.getElementById("strNoticePeriod").value = "";
 			alert("No. of days should be less than or equal to 180 days!");
 		}
 	} else if(type == 'P' ) {
 		days = document.getElementById("strProbationPeriod").value;
 		if(parseInt(days) > 180) {
 			document.getElementById("strProbationPeriod").value = "";
 			alert("No. of days should be less than or equal to 180 days!");
 		}
 	} else if(type == 'E' ) {
 		days = document.getElementById("strExtendProbationDays").value;
 		if(parseInt(days) > 180) {
 			document.getElementById("strExtendProbationDays").value = "";
 			alert("No. of days should be less than or equal to 180 days!");
 		}
 	} 
 }
   
    
    function show_designation() {
    	dojo.event.topic.publish("show_designation");
    }
    
    function show_grade() {
    	dojo.event.topic.publish("show_grade");
    }
    	    	
    function show_employees() {
    	dojo.event.topic.publish("show_employees");
    }
    
</script>

<% 
    
	String id = (String)request.getParameter("id");
	String appFreqId = (String)request.getParameter("appFreqId"); 
	String fromPage = (String)request.getParameter("fromPage");
    String strMessage = (String)request.getAttribute(IConstants.MESSAGE);
    if(strMessage == null) {
    	strMessage = "";
    }
    String strTitle = (String)request.getAttribute(IConstants.TITLE);
    if(strTitle == null) {
    	strTitle = "";
    }
    
    
	
	//System.out.println("id : "+id+" appFreqId : "+appFreqId+" fromPage : "+fromPage+" strMessage : "+strMessage);
    %>

<section class="content">
    <div class="row jscroll">
        <section class="col-lg-12 connectedSortable">
            <div class="box box-primary">
                <div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
                    <div class="leftbox reportWidth">
                        <%=strMessage %>
                        <%
                            String sbEmpList = (String) request.getAttribute("sbEmpList");
                            if(sbEmpList !=null) {
                        %>
                        <s:form theme="simple" action="AppraisalBulkFinalization" method="POST" id="frmAppraisalBulkFinalization" name="frmAppraisalBulkFinalization">
                            <s:hidden name="id"></s:hidden>
                            <s:hidden name="strGapEmp"></s:hidden>
                            <s:hidden name="fromPage"></s:hidden>
                            <input type="hidden" name="appFreqId" id = "appFreqId" value="<%=appFreqId %>" />
                            <input type="hidden" name="empIds" id="empIds" value="<%=(String) request.getAttribute("sbEmp")  %>"/> 
                            <s:hidden name="dataType"></s:hidden>
                            <div class="tableblock"  style="padding:5px; border:solid 1px #F0F0F0; width: 80%; margin-top: 10px; height: auto;">
                                <table class="table">
                                    <tr style="background-color: rgb(248, 248, 248);">
                                        <th align="left" style="padding-left: 20px">Current Information</th>
                                    </tr>
                                    <tr>
                                        <td valign="top">
                                            <div class="trow" style="background:#fff; margin:0px; width:100%; height: auto;">
                                                <div style="float:left; border:0px #ccc solid">
                                                    <%=sbEmpList %>         
                                                </div>
                                            </div>
                                        </td>
                                    </tr>
                                </table>
                            </div>
                            <div style="margin-top: 20px;">
                                <table class="table table-bordered">
                                <!-- Author : Dattatray Note: if condition check -->
                                <%if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_DISABLE_LEARNING_GAP_IN_REVIEW_FINALIZATION))) {  %>
                                <tr>
									<td colspan="2">
										<%
										String thumbsFlag = (String) request.getAttribute("thumbsFlag");
										if(!uF.parseToBoolean(thumbsFlag)) { %> 
											<%-- <span style="float: left; margin-left: 10px; margin-top: 5px;"><img style="height: 16px; width: 16px;" src="images1/thumbs_up_green.png"> </span>  --%>
											<span style="float: left; margin-left: 10px; margin-top: 5px;"><i class="fa fa-thumbs-up" style="color:#68ac3b;height: 16px; width: 16px;" aria-hidden="true"></i> </span> 
											
										<% } else { %>
											<%-- <span style="float: left; margin-left: 10px; margin-top: 5px;"> <img style="height: 16px; width: 16px;" src="images1/thumbs_down_red.png"></span> --%> 
											<span style="float: left; margin-left: 10px; margin-top: 5px;"> <i class="fa fa-thumbs-down" style="color:#e22d2c;height: 16px; width: 16px;" aria-hidden="true"></i></span>
											
										<% } %>
										<%-- <% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_DISABLE_LEARNING_GAP_IN_REVIEW_FINALIZATION))) { %> --%>
											<span style="float: left; margin-left: 10px; margin-top: 5px;">Send to Learning Gap <s:checkbox name="sendtoGapStatus" id="sendtoGapStatus" onclick="checkGapStatus(this)"/> </span>
											<span id="learningPlanSpan" style="display: none; float: left; margin-left: 20px; margin-top: 5px;"> 
												<select name="learningIds" id="learningIds" multiple="multiple"><%=(String)request.getAttribute("sbOptions") %></select>
											</span>
										<%-- <% } %> --%>
									</td>
								</tr>
								<tr>
									<td style="text-align: right;" class="txtlabel" valign="top"><b>Performance Summary:<sup>*</sup></b>&nbsp;</td>
									<td><s:textarea name="remark" cssClass="validateRequired" cssStyle="width: 500px !important; height: 70px;"></s:textarea></td>
								</tr>
								
								<tr>
									<td style="text-align: right;" class="txtlabel" valign="top"><b>Areas of Strength:</b>&nbsp;</td>
									<td><s:textarea name="areasOfStrength" cssStyle="width: 500px !important; height: 70px;"></s:textarea></td>
								</tr>
								<tr>
									<td style="text-align: right;" class="txtlabel" valign="top"><b>Areas of Development:</b>&nbsp;</td>
									<td><s:textarea name="areasOfDevelopment" cssStyle="width: 500px !important; height: 70px;"></s:textarea></td>
								</tr>
								<% } %>
                                   <%--  <%
                                        String thumbsFlag = (String) request.getAttribute("thumbsFlag");
                                        if(uF.parseToBoolean(thumbsFlag)) { %>
                                    <tr>
                                        <td colspan="2">
                                            <span style="float: left; margin-left: 10px; margin-top: 5px;">
                                                <img style="height: 16px; width: 16px;" src="images1/thumbs_down_red.png">
                                                Send to Learning Gap 
                                                <s:checkbox name="sendtoGapStatus" id="sendtoGapStatus"/>
                                            </span>
                                        </td>
                                    </tr>
                                    <% } %>
                                    <tr>
                                        <td style="text-align: right;" class="txtlabel" valign="top"><b>Remark:<sup>*</sup></b>&nbsp;</td>
                                        <td colspan="3">
                                            <s:textarea name="remark" rows="7" cssClass="validateRequired" cols="100" ></s:textarea>
                                        </td>
                                    </tr> --%>
                                </table>
                            </div>
                           
                           <% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_SHOW_ACTIVITY_REVIEW_FINALIZE))) { %>
                           
                            <div class="nav-tabs-custom">
                                <%
                                    String dataType = (String) request.getAttribute("dataType");
                                    String strLabel = "";
                                    String urlA = "AppraisalBulkFinalization.action?dataType=A&strEmpIds="+URLEncoder.encode((String) request.getAttribute("sbEmp"))+"&fromPage="+fromPage
                                    			 +"&id=" +(String) request.getAttribute("id")+"&strGapEmp=" +URLEncoder.encode((String) request.getAttribute("strGapEmp"))+"&appFreqId=" +(String) request.getAttribute("appFreqId");
                                    String urlD = "AppraisalBulkFinalization.action?dataType=D&strEmpIds="+URLEncoder.encode((String) request.getAttribute("sbEmp"))+"&fromPage="+fromPage 
                                    			+"&id=" +(String) request.getAttribute("id")+"&strGapEmp=" +URLEncoder.encode((String) request.getAttribute("strGapEmp"))+"&appFreqId=" +(String) request.getAttribute("appFreqId");
                                    if(dataType == null || dataType.equals("A")) { 
                                    	strLabel ="Activity";
                                    %>
                                    <ul class="nav nav-tabs">
										<li class="active"><a href="javascript:void(0)"
											onclick="getTabContent('activity')" data-toggle="tab">Activity</a>
										</li>
										<li><a href="javascript:void(0)"
											onclick="getTabContent('activitywdoc')" data-toggle="tab">Activity
												W/Doc</a>
										</li>
									</ul>
                                
                                <% } else if(dataType != null && dataType.equals("D")) {
                                    strLabel ="Activity W/Doc";
                                    %>
                                    <ul class="nav nav-tabs">
										<li><a href="javascript:void(0)" onclick="getTabContent('activity')" data-toggle="tab">Activity</a>
										</li>
										<li class="active"><a href="javascript:void(0)" onclick="getTabContent('activitywdoc')" data-toggle="tab">Activity W/Doc</a>
										</li>
									</ul>
                                <% } %>	
                                	<script>
										function getTabContent(condition) {
											$(".modal-body").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
											if(condition === 'activity'){
												var action = '<%=urlA  %>';
											}else{ 
												var action = '<%=urlD  %>';
											}
											$.ajax({ 
												type : 'GET',
												url: action,
												cache: true,
												success: function(result){
													$(".modal-body").html(result);
										   		}
											});
										}
									</script>
                            </div>
                            <div class="tab-content">
                                <table class="table table_no_border autoWidth">
                                    <tr>
                                        <td class="txtlabel" colspan="2">New <%=strLabel %> Information</td>
                                    </tr>
                                    <tr>
                                        <td colspan="4">
                                            <hr width="100%" style="border: 1px solid rgb(238, 238, 238);">
                                        </td>
                                    </tr>
                                    <tr>
                                        <td colspan=4>
                                            <s:fielderror />
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="txtlabel alignRight">Activity:<sup>*</sup></td>
                                        <td>
                                            <s:select name="strActivity" id="strActivity" listKey="activityId" theme="simple" cssClass="validateRequired" listValue="activityName" headerKey="" 
                                                headerValue="Select Activity" list="activityList" key="" onchange="selectElements(this.value)" />
                                            <span class="hint">Select an activity from the list.<span class="hint-pointer">&nbsp;</span></span>
                                        </td>
                                        <td class="txtlabel alignRight" id = "tdEffectiveLbl">Effective Date:<sup>*</sup></td>
                                        <td>
                                            <s:textfield name="effectiveDate" id="idEffectiveDate" cssClass="validateRequired text-input" />
                                            <span class="hint">Add the effective date.<span class="hint-pointer">&nbsp;</span></span>
                                        </td>
                                    </tr>
                                    <tr id="extendProbationTR">
                                        <td class="txtlabel alignRight">No. of Days:<sup>*</sup></td>
                                        <td>
                                            <s:textfield name="strExtendProbationDays" id="strExtendProbationDays" cssClass="validateRequired"  onkeypress="return isOnlyNumberKey(event)" onkeyup="checkNoOfDays('E')"/>
                                        </td>
                                        <td class="txtlabel alignRight"></td>
                                        <td></td>
                                    </tr>
                                    <tr id="tranferTypeTR">
                                        <td class="txtlabel alignRight">Select Transfer Type:<sup>*</sup></td>
                                        <td>
                                            <s:select name="strTransferType" id="strTransferType" theme="simple" cssClass="validateRequired" headerKey="" headerValue="Select Transfer Type" 
                                                list="#{'WL':'Work Location', 'DEPT':'Department', 'LE':'Legal Entity'}" onchange="selectElements1(this.value)" />
                                        </td>
                                        <td class="txtlabel alignRight"></td>
                                        <td></td>
                                    </tr>
                                    <tr id="incrementTypeTR">
                                        <td class="txtlabel alignRight">Select Increment Type:<sup>*</sup></td>
                                        <td >
                                            <s:select name="strIncrementType" id="strIncrementType" theme="simple" cssClass="validateRequired" headerKey="" headerValue="Select Increment Type" 
                                                list="#{'1':'Single', '2':'Double'}" onchange="showIncrementPercent(this.value)" />
                                        </td>
                                        <td class="txtlabel alignRight"></td>
                                        <td></td>
                                    </tr>
                                    <tr id="legalEntityTR">
                                        <td class="txtlabel alignRight">Legal Entity:<sup>*</sup></td>
                                        <td>
                                            <s:select name="strOrganisation" id="strOrganisation" theme="simple" listKey="orgId" cssClass="validateRequired" listValue="orgName" 
                                                headerKey="" headerValue="Select Legal Entity" list="organisationList1" key="" required="true" onchange="getWLocSbuDeptLevelByOrg(this.value);"/>
                                        </td>
                                        <td class="txtlabel alignRight"></td>
                                        <td></td>
                                    </tr>
                                    <tr>
                                        <td class="txtlabel alignRight" id="locationLTD">Location:<sup>*</sup></td>
                                        <td id="locationVTD">
                                            <span id="locationListSpan">
                                                <s:select name="strWLocation" id="strWLocation" theme="simple" listKey="wLocationId" cssClass="validateRequired" listValue="wLocationName" 
                                                    headerKey="" headerValue="Select Work Location" list="wLocationList1" key="" required="true" />
                                            </span>
                                            <span class="hint">Add the work location name. This will be displayed in time-sheets and clock entries.<span class="hint-pointer">&nbsp;</span></span>
                                        </td>
                                        <td class="txtlabel alignRight" id="sbuLTD">SBU:<sup>*</sup></td>
                                        <td id="sbuVTD">
                                            <div id="sbuListSpan">
                                                <s:select name="strSBU" id="strSBU" theme="simple" listKey="serviceId" cssClass="validateRequired" listValue="serviceName" headerKey="" 
                                                    headerValue="Select SBU" list="serviceList1" key="" />
                                            </div>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="txtlabel alignRight" id="deptLTD">Department:<sup>*</sup></td>
                                        <td id="deptVTD">
                                            <div id="deptListSpan">
                                                <s:select name="strDepartment" id="strDepartment" theme="simple" listKey="deptId" cssClass="validateRequired" listValue="deptName" headerKey="" 
                                                    headerValue="Select Department" list="departmentList1" key="" required="true" />
                                            </div>
                                            <span class="hint">Add the department name. This will be displayed in time-sheets and clock entries.<span class="hint-pointer">&nbsp;</span></span>
                                        </td>
                                        <td class="txtlabel alignRight" id="levelLTD">Level:<sup>*</sup></td>
                                        <td id="levelVTD">
                                            <span id="levelListSpan">
                                                <s:select name="strLevel" id="strLevel" theme="simple" listKey="levelId" cssClass="validateRequired" listValue="levelCodeName" headerKey="" 
                                                    headerValue="Select Level" onchange="showDesignation(this.value);" list="levelList1" key="" />
                                            </span>
                                            <!-- onchange="javascript:show_designation();return false;" --> 
                                            <span class="hint">Add the level name. This will be displayed in time-sheets and clock entries.<span class="hint-pointer">&nbsp;</span></span>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="txtlabel alignRight" id="desigLTD">Designation:<sup>*</sup>
                                            <%-- <s:hidden name="strDesignation"/> --%>
                                        </td>
                                        <td id="desigVTD">
                                            <span id="desigListSpan">
                                                <s:select theme="simple" name="strDesignation" id="strDesignation" listKey="desigId" cssClass="validateRequired" listValue="desigCodeName" 
                                                    headerKey="" headerValue="Select Designation" list="desigList" key="" required="true" onchange="getGrades(this.value);" />
                                            </span>
                                            <%-- <s:url id="desigList_url" action="GetDesigList" />
                                                <sx:div theme="simple" href="%{desigList_url}" listenTopics="show_designation" formId="frmAppraisalBulkFinalization" showLoadingText="true"></sx:div> --%>	
                                        </td>
                                        <td class="txtlabel alignRight" id="gradeLTD">Grade:<sup>*</sup>
                                            <%-- <s:hidden name="strGrade"/> --%>
                                        </td>
                                        <td id="gradeVTD">
                                            <span id="gradeListSpan">
                                                <%-- <s:select theme="simple" name="empGrade" id="empGrade" cssClass="validateRequired" list="gradeList" 
                                                    listKey="gradeId" listValue="gradeCode" headerKey="" headerValue="Select Grade" required="true" /> --%>
                                                <s:select theme="simple" name="empGrade" id="empGrade" cssClass="validateRequired" list="gradeChangeList" 
                                                    listKey="gradeId" listValue="gradeCode" headerKey="" headerValue="Select Grade" required="true" />
                                            </span>
                                            <%-- <s:url id="gradeList_url" action="GetGradeList" />
                                                <sx:div href="%{gradeList_url}" listenTopics="show_grade" formId="frmAppraisalBulkFinalization" showLoadingText="true"></sx:div> --%>
                                        </td>
                                        <td class="txtlabel alignRight" id="gradeChangeLTD">Grade:<sup>*</sup>
                                            <%-- <s:hidden name="strGrade"/> --%>
                                        </td>
                                        <td id="gradeChangeVTD">
                                            <span id="gradeChangeListSpan">
                                                <s:select theme="simple" name="empChangeGrade" id="empChangeGrade" cssClass="validateRequired" list="gradeChangeList" 
                                                    listKey="gradeId" listValue="gradeCode" headerKey="" headerValue="Select Grade" required="true" />
                                            </span>
                                        </td>
                                    </tr>
                                    <tr id="noticeTR" style="display: none">
                                        <td class="txtlabel alignRight">No. of Days:<sup>*</sup></td>
                                        <td>
                                            <s:textfield name="strNoticePeriod" id="strNoticePeriod" cssClass="validateRequired" onkeypress="return isOnlyNumberKey(event)" onkeyup="checkNoOfDays('N')"/> </td>
                                        </td>
                                        <td class="txtlabel alignRight"></td>
                                        <td></td>
                                    </tr>
                                    <tr id="probationTR" style="display: none">
                                        <td class="txtlabel alignRight" id="idPeriodL">No. of Days:<sup>*</sup></td>
                                        <td id="idPeriodV">
                                            <s:textfield name="strProbationPeriod" id="strProbationPeriod" cssClass="validateRequired" onkeypress="return isOnlyNumberKey(event)" onkeyup="checkNoOfDays('P')"/>
                                        </td>
                                        <td class="txtlabel alignRight"></td>
                                        <td></td>
                                    </tr>
                                    <tr id="incrementPercentTR" style="display: none">
                                        <td class="txtlabel alignRight" id="idPeriodL">Increment Percentage:<sup>*</sup></td>
                                        <td id="idPeriodV">
                                            <s:textfield name="strIncrementPercentage" id="strIncrementPercentage" cssClass="validateRequired" maxlength="5" onkeypress="return isNumberKey(event)" cssStyle="width:50px;"/>
                                        </td>
                                        <td class="txtlabel alignRight"></td>
                                        <td></td>
                                    </tr>
                                  
                                    <tr>
                                        <td class="txtlabel alignRight" valign="top" id="reasonLTD">Reason:<sup>*</sup></td>
                                        <td colspan="3" id="reasonVTD">
                                            <s:textarea name="strReason" rows="5" cssClass="validateRequired" cols="60" ></s:textarea>
                                            <span class="hint">Add the reason for this activity for reference.<span class="hint-pointer">&nbsp;</span></span>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td></td>
                                        <td colspan="3">
                                            <s:submit name="strUpdate" cssClass="btn btn-primary" value="Update" />
                                            <% if(dataType != null && dataType.equals("D")) { %>
                                            <s:submit name="strUpdateDocument" cssClass="btn btn-primary" value="Update & Send Document" />
                                            <% } %>
                                        </td>
                                    </tr>
                                </table>
                            </div>
                            <% } else { %>
                            	<div>
									<s:submit name="submit" cssClass="btn btn-primary" value="Finalise" />
								</div>
                            <% } %>
                        </s:form>
                        <% } else { %>
                        <div class="nodata msg"><span>No Employees Selected</span></div>
                        <% } %>
                    </div>
                </div>
                <!-- /.box-body -->
            </div>
        </section>
    </div>
</section>


<script>

$("#frmAppraisalBulkFinalization").submit(function(event){
	//alert("1");
	event.preventDefault();
	var appId = '<%=id%>';
	var appFreqId = '<%=appFreqId %>';
	var fromPage = '<%=fromPage %>';
	var strUpdate = $("input[name='strUpdate']").val();
	var strUpdateDocument = $("input[name='strUpdateDocument']").val();
	var form_data = $("#frmAppraisalBulkFinalization").serialize();
		$.ajax({ 
			type : 'POST',
			url: "AppraisalBulkFinalization.action",
			data: form_data+"&strUpdate="+strUpdate+"&strUpdateDocument="+strUpdateDocument,
			success: function(result){
				getReviewStatus('AppraisalStatus',appId,appFreqId,fromPage);
			},
			error: function(result){
				console.log("error result");
				getReviewStatus('AppraisalStatus',appId,appFreqId,fromPage);
			}
	});
		
});
	/* Start Dattatray */
	<% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_SHOW_ACTIVITY_REVIEW_FINALIZE))) { %>
		disableAll();
		disableAll1();
    <% } %>
    /* End Dattatray */

</script>