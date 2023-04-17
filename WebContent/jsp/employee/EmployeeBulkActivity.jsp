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
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script> 
<% 
	java.util.List couterlist = (java.util.List)request.getAttribute("reportList"); 
	List<List<String>> leaveTypeListWithBalance = (List<List<String>>) request.getAttribute("leaveTypeListWithBalance");
%>
<script type="text/javascript">  
    function isNumberKey(evt){
       var charCode = (evt.which) ? evt.which : event.keyCode;
       if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46)
          return false;
    
       return true;
    }
    
    function isOnlyNumberKey(evt){
 	   var charCode = (evt.which) ? evt.which : event.keyCode;
 	   if (charCode <= 31 || (charCode >= 48 && charCode <= 57)) {
 	      return true;
 	   }
 	   return false; 
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
  		  document.getElementById("probationTR").style.display = "table-row";
  		  document.getElementById("tdEffectiveLbl").style.display = "none";
     		  document.getElementById("idEffectiveDate").style.display = "none";
  	  } else if(activityId == <%=IConstants.ACTIVITY_EXTEND_PROBATION_ID %>) { // Extend Probation 
  		  document.getElementById("extendProbationTR").style.display = "table-row";
  		  document.getElementById("tdEffectiveLbl").style.display = "none";
     		 document.getElementById("idEffectiveDate").style.display = "none";
  	  } else if(activityId == <%=IConstants.ACTIVITY_CONFIRMATION_ID %>) { // Confirmation 
  		  document.getElementById("tdEffectiveLbl").style.display = "table-cell";
  		  document.getElementById("idEffectiveDate").style.display = "table-cell";
  		  document.getElementById("incrementPercentTR").style.display = "table-row";

  	  } else if(activityId == <%=IConstants.ACTIVITY_TEMPORARY_ID %>) { // Temporary 
  		  /* document.getElementById("idLevelL").style.display = "table-cell";
  		  document.getElementById("idLevelV").style.display = "table-cell";
  		  document.getElementById("idDesigL").style.display = "table-cell";
  		  document.getElementById("idDesigV").style.display = "table-cell";
  		  document.getElementById("idGradeL").style.display = "table-cell";
  		  document.getElementById("gradeVTD").style.display = "table-cell"; */
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
  		  document.getElementById("tdEffectiveLbl").style.display = "none";
     		  document.getElementById("idEffectiveDate").style.display = "none";
  	  } else if(activityId == <%=IConstants.ACTIVITY_RESIGNATION_WITHDRWAL_ID %>) { // Withdrawn Resignation 
  		  document.getElementById("tdEffectiveLbl").style.display = "table-cell";
  	      document.getElementById("idEffectiveDate").style.display = "table-cell";
  	  } else if(activityId == <%=IConstants.ACTIVITY_FULL_FINAL_ID %>) { // Full & Final 
  		  document.getElementById("tdEffectiveLbl").style.display = "table-cell";
  	      document.getElementById("idEffectiveDate").style.display = "table-cell";
  	  } else if(activityId == <%=IConstants.ACTIVITY_NEW_JOINEE_PENDING_ID %>) { // New Joinee Pending 
  		  document.getElementById("tdEffectiveLbl").style.display = "table-cell";
  	      document.getElementById("idEffectiveDate").style.display = "table-cell";
  	  }else{
  		  document.getElementById("tdEffectiveLbl").style.display = "table-cell";
		  document.getElementById("idEffectiveDate").style.display = "table-cell";
  	  }
    }
    
    
    function selectElements1(transferType) {
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
     		  
     		  document.getElementById("empTypeLTD").style.display = "none";
    		  document.getElementById("empTypeVTD").style.display = "none";
    		  
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
     	  
     	  } else if(transferType == 'ET') { // Employee Type
       		  
       		  document.getElementById("legalEntityTR").style.display = "none";
       		  document.getElementById("locationLTD").style.display = "none";
       		  document.getElementById("locationVTD").style.display = "none";
       		  
       		  document.getElementById("empTypeLTD").style.display = "table-cell";
     		  document.getElementById("empTypeVTD").style.display = "table-cell";
       		
       		  document.getElementById("sbuLTD").style.display = "table-cell";
       		  document.getElementById("sbuVTD").style.display = "table-cell";
       		  
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
     		  
     		 document.getElementById("empTypeLTD").style.display = "none";
   		  	 document.getElementById("empTypeVTD").style.display = "none";
   		  
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
    	
      document.getElementById('strIncrementType').selectedIndex = 0;
  	  document.getElementById('strTransferType').selectedIndex = 0;
  	  document.getElementById('strOrganisation').selectedIndex = 0;
  	  document.getElementById('strWLocation').selectedIndex = 0;
  	  document.getElementById('strLevel').selectedIndex = 0;
  	  document.getElementById('strDepartment').selectedIndex = 0;
//  	  document.getElementById('strDesignation').selectedIndex = 0;
  	  if(document.getElementById('strDesignation')){
    	  	document.getElementById('strDesignation').selectedIndex = 0;
    	  }
  	  document.getElementById('empGrade').selectedIndex = 0;
  	  document.getElementById("extendProbationTR").style.display = "none";
  	  document.getElementById("tranferTypeTR").style.display = "none";
  	  document.getElementById("incrementTypeTR").style.display = "none";
  	  //document.getElementById("salaryDetailsDiv").innerHTML = "";
  	  
  	  document.getElementById("incrementPercentTR").style.display = "none";
  	  document.getElementById("legalEntityTR").style.display = "none";
  	  document.getElementById("locationLTD").style.display = "none";
  	  document.getElementById("locationVTD").style.display = "none";
  	  
  	  document.getElementById("empTypeLTD").style.display = "none";
	  document.getElementById("empTypeVTD").style.display = "none";
	
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
    	
		document.getElementById("empTypeLTD").style.display = "none";
		document.getElementById("empTypeVTD").style.display = "none";
		 
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
    
    disableAll();
    
    disableAll1();
    
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
    
    function checkNoOfDays(type) {
    	//alert("type==>"+type);''
    	var days = "";
    	if(type == 'N' ) {
    		days = document.getElementById("strNoticePeriod").value;
    		if(parseInt(days) > 180 || parseInt(days) == 0 ) {
    			document.getElementById("strNoticePeriod").value = "";
    			alert("No. of days should be less than or equal to 180 days!");
    		}
    	} else if(type == 'P' ) {
    		days = document.getElementById("strProbationPeriod").value;
    		if(parseInt(days) > 180 || parseInt(days) == 0) {
    			document.getElementById("strProbationPeriod").value = "";
    			alert("No. of days should be less than or equal to 180 days!");
    		}
    	} else if(type == 'E' ) {
    		days = document.getElementById("strExtendProbationDays").value;
    		if(parseInt(days) > 180 || parseInt(days) == 0) {
    			document.getElementById("strExtendProbationDays").value = "";
    			alert("No. of days should be less than or equal to 180 days!");
    		}
    	} 
        	
    }
</script>


<link rel="stylesheet" type="text/css" href="css/select/jquery.multiselect.css" />
<script type="text/javascript" src="scripts/select/jquery.multiselect.js"></script>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script>    	
    $(function() {
        $( "#idEffectiveDate").datepicker({format: 'dd/mm/yyyy'});
    	$("#f_strWLocation").multiselect().multiselectfilter();
    	$("#f_department").multiselect().multiselectfilter();
    	$("#f_service").multiselect().multiselectfilter();
    	$("#f_level").multiselect().multiselectfilter(); 
    	$("#strSelectedEmpId").multiselect().multiselectfilter();
    	
    });  
    $(document).ready(function(){
    	$("#strUpdate").click(function(){
    		//$(".validateRequired").prop('required',true);
    		$( "#frmEmpActivity" ).find('input[type=text],textarea,select').filter(':hidden').prop('required',false);
    		$( "#frmEmpActivity" ).find('input[type=text],textarea,select').filter(':visible').prop('required',true);
    	});
    	$("#strUpdateDocument").click(function(){
    		//$(".validateRequired").prop('required',true);
    		$( "#frmEmpActivity" ).find('input[type=text],textarea,select').filter(':hidden').prop('required',false);
    		$( "#frmEmpActivity" ).find('input[type=text],textarea,select').filter(':visible').prop('required',true);
    	});
    });
</script> 
<% 
	UtilityFunctions uF = new UtilityFunctions();
	CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
    String strMessage = (String)request.getAttribute(IConstants.MESSAGE);
    if(strMessage == null) {
    	strMessage = "";
    }
    %>
<%-- <jsp:include page="../common/SubHeader.jsp">
    <jsp:param value="Employee Bulk Activity" name="title"/>
    </jsp:include> --%>
<section class="content">
    <div class="row jscroll">
        <section class="col-lg-12 connectedSortable">
            <div class="box box-primary">
                <div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
                    <div class="leftbox reportWidth">
                        <%= strMessage%>
                        <div class="desgn" style="margin-bottom: 5px;background:#f5f5f5; color:#232323;">
                            <div class="box box-primary" style="border-top-color: #EEEEEE; margin-top: 10px;">  <!-- collapsed-box -->
                                <%-- <div class="box-header with-border">
                                    <h3 class="box-title" style="font-size: 14px;"s><%=(String)request.getAttribute("selectedFilter") %></h3>
                                    <div class="box-tools pull-right">
                                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
                                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                                    </div>
                                </div> --%>
                                <!-- /.box-header -->
                                <div class="box-body" style="padding: 5px; overflow-y: auto;">
                                	<s:form name="frmEmployeeActivity" action="EmployeeBulkActivity" theme="simple">
                                		<input type="hidden" name="strEmpIds" id="strEmpIds" value="<%=(String) request.getAttribute("sbEmp")  %>"/>
	                                	<div class="row row_without_margin">
											<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
												<i class="fa fa-filter"></i>
											</div>
											<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
												<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
													<p style="padding-left: 5px;">Organization</p>
													<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName"
                                                        onchange="document.frmEmployeeActivity.submit();" 
                                                        list="orgList" key=""/>
												</div>
												<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
													<p style="padding-left: 5px;">Location</p>
													<s:select name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName"
                                                        list="wLocationList" key="" multiple="true"/>
												</div>
												<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
													<p style="padding-left: 5px;">Department</p>
													<s:select name="f_department" id="f_department" listKey="deptId" listValue="deptName" 
                                                        list="departmentList" key="" multiple="true"/>
												</div>
												<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
													<p style="padding-left: 5px;">SBU</p>
													<s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId"  
                                                        listValue="serviceName" key="" multiple="true"/>
												</div>
												<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
													<p style="padding-left: 5px;">Level</p>
													<s:select name="f_level" id="f_level" listKey="levelId" listValue="levelCodeName" 
                                                        multiple="true" list="levelList" key=""/>
												</div>
												<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
													<p style="padding-left: 5px;">Employee</p>
													<s:select name="strSelectedEmpId" id="strSelectedEmpId" listKey="employeeId" listValue="employeeCode" 
                                                        list="empList" key="" multiple="true"/>
												</div>
												<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5" id="submitDIV">
													<p style="padding-left: 5px;">&nbsp;</p>
													<s:submit name="Submit" cssClass="btn btn-primary" value="Submit" />
												</div>
											</div>
										</div>
									</s:form>
                                </div>
                                <!-- /.box-body -->
                            </div>
                        </div>
                        <%
                            String sbEmpList = (String) request.getAttribute("sbEmpList");
                            if(sbEmpList !=null){
                            %>
                        <div class="tableblock"  style="padding:5px; border: solid 2px #E2E2E2; width: 80%; margin-top: 10px; height: auto;">
                            <table width="100%" class="table">
                                <tr>
                                    <th align="left" style="padding-left: 20px">
                                        <span style="float: left;">Current Information</span>
                                        <span style="float: right; margin-right: 10px;">
                                        <a href="javascript:void(0);" onclick="if(confirm('Are you sure, do you want to remove all these employees?')) window.location='EmployeeBulkActivity.action?status=1'">Clear All</a>
                                        </span>
                                    </th>
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
                        
                        <div class="nav-tabs-custom margintop20">
                        	<ul class="nav nav-tabs">
                            <%
                                String dataType = (String) request.getAttribute("dataType");
                                String strLabel = "";
                                if(dataType == null || dataType.equals("A")) { 
                                	strLabel ="Activity";
                                %>
                                <li class="active"><a href="EmployeeBulkActivity.action?dataType=A&strEmpIds=<%=URLEncoder.encode((String) request.getAttribute("sbEmp"))  %>">Activity</a></li>
                                <li><a href="EmployeeBulkActivity.action?dataType=D&strEmpIds=<%=URLEncoder.encode((String) request.getAttribute("sbEmp"))  %>">Activity W/Doc</a></li>
                            <% } else if(dataType != null && dataType.equals("D")) { 
                                strLabel ="Activity W/Doc";
                                %>
                                <li><a href="EmployeeBulkActivity.action?dataType=A&strEmpIds=<%=URLEncoder.encode((String) request.getAttribute("sbEmp"))  %>">Activity</a></li>
                                <li class="active"><a href="EmployeeBulkActivity.action?dataType=D&strEmpIds=<%=URLEncoder.encode((String) request.getAttribute("sbEmp"))  %>">Activity W/Doc</a></li>
                            <% } %>	
                            </ul>
	                        <div class="tab-content">
	                        <s:form theme="simple" action="EmployeeBulkActivity" method="POST" id="frmEmpActivity">
	                            <input type="hidden" name="empIds" id="empIds" value="<%=(String) request.getAttribute("sbEmp")  %>"/> 
	                            <s:hidden name="dataType"></s:hidden>
	                            <div>
	                            	New <%=strLabel %> Information<br/>
	                                <table class="table table_no_border autoWidth">
	                                    <s:fielderror />
	                                    <tr>
	                                        <td class="txtlabel alignRight">Activity:<sup>*</sup></td>
	                                        <td>
	                                            <s:select name="strActivity" id="strActivity" listKey="activityId" theme="simple" cssClass="validateRequired" listValue="activityName" headerKey="" 
	                                                headerValue="Select Activity" list="activityList" key="" onchange="selectElements(this.value)" />
	                                            <span class="hint">Select an activity from the list.<span class="hint-pointer">&nbsp;</span></span>
	                                        </td>
	                                        <td class="txtlabel alignRight" id = "tdEffectiveLbl">Effective Date:<sup>*</sup></td>
	                                        <td>
	                                            <s:textfield name="effectiveDate" id="idEffectiveDate" cssClass="validateRequired" />
	                                        </td>
	                                    </tr>
	                                    <tr id="extendProbationTR">
	                                        <td class="txtlabel alignRight">No. of Days:<sup>*</sup></td>
	                                        <td >
	                                            <s:textfield name="strExtendProbationDays" id="strExtendProbationDays" cssClass="validateRequired" onkeypress="return isOnlyNumberKey(event)" onkeyup="checkNoOfDays('E')"/>
	                                        </td>
	                                        <td class="txtlabel alignRight"></td>
	                                        <td></td>
	                                    </tr>
	                                    <tr id="tranferTypeTR">
	                                        <td class="txtlabel alignRight">Select Transfer Type:<sup>*</sup></td>
	                                        <td >
	                                            <s:select name="strTransferType" id="strTransferType" theme="simple" cssClass="validateRequired" headerKey="" headerValue="Select Transfer Type" 
	                                                list="#{'WL':'Work Location', 'DEPT':'Department', 'LE':'Legal Entity', 'ET':'Employee Type'}" onchange="selectElements1(this.value)" />
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
	                                        </td>
	                                        <td class="txtlabel alignRight" id="empTypeLTD">Employee Type:<sup>*</sup></td>
		                                    <td id="empTypeVTD">
		                                        <span id="empTypeListSpan">
		                                            <s:select name="employmentType" cssClass="validateRequired form-control" listKey="empTypeId" listValue="empTypeName" headerKey="" headerValue="Select Employee Type" list="empTypeList"/>
		                                        </span>
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
	                                        </td>
	                                        <td class="txtlabel alignRight" id="levelLTD">Level:<sup>*</sup></td>
	                                        <td id="levelVTD">
	                                            <span id="levelListSpan">
	                                                <s:select name="strLevel" id="strLevel" theme="simple" listKey="levelId" cssClass="validateRequired" listValue="levelCodeName" headerKey="" 
	                                                    headerValue="Select Level" onchange="showDesignation(this.value);" list="levelList1" key="" />
	                                            </span>
	                                            <!-- onchange="javascript:show_designation();return false;" --> 
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
	                                                <sx:div theme="simple" href="%{desigList_url}" listenTopics="show_designation" formId="frmEmpActivity" showLoadingText="true"></sx:div> --%>	
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
	                                                <sx:div href="%{gradeList_url}" listenTopics="show_grade" formId="frmEmpActivity" showLoadingText="true"></sx:div> --%>
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
	                                            <s:textfield name="strNoticePeriod" id="strNoticePeriod" cssClass="validateRequired" onkeypress="return isOnlyNumberKey(event)" onkeyup="checkNoOfDays('N')"/>
	                                        </td>
	                                        <td class="txtlabel alignRight"></td>
	                                        <td></td>
	                                    </tr>
	                                    <tr id="probationTR" style="display: none">
	                                        <td class="txtlabel alignRight" id="idPeriodL">No. of Days:<sup>*</sup></td>
	                                        <td id="idPeriodV">
	                                            <s:textfield name="strProbationPeriod" id= "strProbationPeriod" cssClass="validateRequired" onkeypress="return isOnlyNumberKey(event)" onkeyup="checkNoOfDays('P')"/>
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
	                                    <%-- <tr>
	                                        <td class="txtlabel alignRight" id="newStatusLTD">New Status<sup>*</sup>:</td>
	                                        <td id="newStatusVTD"><s:select name="strNewStatus" listKey="empStatusId" cssClass="validateRequired" listValue="empStatusName" 
	                                        	headerValue="Select Status" headerKey="" list="empStatusList" required="true" /> 
	                                        	<span class="hint">Add the department name. This will be displayed in time-sheets and clock entries.<span class="hint-pointer">&nbsp;</span></span>
	                                        </td>
	                                        <td>&nbsp;</td>
	                                        <td class="txtlabel alignRight" id="notifyMailTD">
	                                        	<s:checkbox name="emailNotification" cssStyle="width: 10px; height: 10px;" />Notify through Email
	                                        </td>
	                                        </tr> --%>
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
	                                            <s:submit name="strUpdate" cssClass="btn btn-primary" value="Update" id= "strUpdate" />
	                                            <% if(dataType != null && dataType.equals("D")){ %>
	                                            <s:submit name="strUpdateDocument" id="strUpdateDocument" cssClass="btn btn-primary" value="Update & Send Document" />
	                                            <%} %>
	                                        </td>
	                                    </tr>
	                                </table>
	                            </div>
	                        </s:form>
	                        </div>
                        </div>
                        <% } else { %>
                        <div class="nodata msg"><span> Please select employees from the employee field.</span></div>
                        <% } %>
                    </div>
                </div>
                <!-- /.box-body -->
            </div>
        </section>
    </div>
</section>
<script type="text/javascript">
    disableAll();
    disableAll1();
</script>