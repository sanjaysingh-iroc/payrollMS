<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="java.util.List"%>
<%@page import="java.util.*"%> 
<%@page import="java.util.HashMap"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.Map"%>
<%@page import="com.konnect.jpms.select.FillWLocation"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<script type="text/javascript" src="scripts/customAjax.js"></script>

<%
UtilityFunctions uF = new UtilityFunctions();
/* EncryptionUtility eU = new EncryptionUtility(); */
CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
String currDate = uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), IConstants.DBDATE, IConstants.DATE_FORMAT);
java.util.List couterlist = (java.util.List)request.getAttribute("reportList");
if(couterlist == null) couterlist = new java.util.ArrayList();
List<List<String>> leaveTypeListWithBalance = (List<List<String>>) request.getAttribute("leaveTypeListWithBalance");

//===start parvez date: 28-06-2022===    
	Map<String, String> hmFeatureStatus = (Map<String, String>)request.getAttribute("hmFeatureStatus");
	if(hmFeatureStatus == null) hmFeatureStatus = new HashMap<String, String>();
	Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>)request.getAttribute("hmFeatureUserTypeId");
	if(hmFeatureUserTypeId == null) hmFeatureUserTypeId = new HashMap<String, List<String>>();
//===end parvez date: 28-06-2022=== 
 %>
<style>
.induction_div .box-header{
padding-top: 5px ;
padding-bottom: 5px;
}
.sk_value{
color: rgb(104, 104, 104) !important;
}

#employee_details .table>tbody>tr>td,#employee_details .table>tbody>tr>th,#employee_details .table>tfoot>tr>td,#employee_details .table>tfoot>tr>th,#employee_details .table>thead>tr>td,#employee_details .table>thead>tr>th {
padding: 2px !important;
}
</style>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script type="text/javascript">
    jQuery(document).ready(function(){ 
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
    	
    	$("body").on('click',"input[type='submit']",function(){
    		$("#frmEmpActivity").find('.validateRequired').filter(':hidden').prop('required', false);
    		$("#frmEmpActivity").find('.validateRequired').filter(':visible').prop('required', true);
    	});
    	
    	/* $("#frmEmpActivity_strUpdateDocument").click(function(){
    		$(".validateRequired").prop('required',true);
    	});
    	$("input[name='strUpdate']").click(function(){
    		$(".validateRequired").prop('required',true);
    	}); */
    	var activityId = document.getElementById("strActivity").value;
    	selectElements(activityId);
    
    });
    
    
    function selectElements(activityId) {
		disableAll();
  		var dataType = document.frmEmployeeActivity.dataType.value;
  		
	/* ===start parvez date: 28-06-2022=== */		
	 	/* if(dataType=="D" && activityId > 0) {
	 		var strOrgId = document.getElementById("f_org").value;
	 		getDocumentList(activityId, strOrgId);
			document.getElementById("documentNameTR").style.display = "table-row";
	 	} */
	 	if(dataType=="D" && activityId > 0){
	 		var featureManagement = '<%=uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_DOCUMENT_ATTACH_OFFER_LETTER)) %>';
	 		
	 		if(featureManagement === 'true' && parseInt(activityId) == parseInt('<%=IConstants.ACTIVITY_OFFER_ID %>')){
	 			document.getElementById("documentAttachTR").style.display = "table-row";
	 		} else {
	 			var strOrgId = document.getElementById("f_org").value;
		 		getDocumentList(activityId, strOrgId);
				document.getElementById("documentNameTR").style.display = "table-row";
				document.getElementById("documentAttachTR").style.display = "none";
	 		} 
	 		
	 	}
	/* ===end parvez date: 28-06-2022=== */ 	
	 	
	  document.getElementById("strIncrementPercentage").value = "";
	  document.getElementById("btnCalculateArrear").style.display = "none";
	  document.getElementById("spanApplyArrear").style.display = "none";
	
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
		  document.getElementById("btnCalculateArrear").style.display = "inline";
		  document.getElementById("spanApplyArrear").style.display = "inline";
		  var strEmpId = document.getElementById("strEmpId2").value;
		  var effectiveDate = document.getElementById("idEffectiveDate").value;
	  	  var action = 'EmpActivityEmpSalaryDetails.action?empId=' + strEmpId+'&effectiveDate='+effectiveDate;
	  	  getContent('salaryDetailsDiv', action);
	  	  	window.setTimeout(function() {
	  	  		changeLabelValuesE('1', '');
		  	}, 1500);
	  } else if(activityId == <%=IConstants.ACTIVITY_TEMPORARY_ID %>) { // Temporary 
		  document.getElementById("tdEffectiveLbl").style.display = "table-cell";
		  document.getElementById("idEffectiveDate").style.display = "table-cell";
		  
	  } else if(activityId == <%=IConstants.ACTIVITY_PERMANENT_ID %>) { // Permanent 
		  document.getElementById("tdEffectiveLbl").style.display = "table-cell";
		  document.getElementById("idEffectiveDate").style.display = "table-cell";
    	  document.getElementById("promotionTR").style.display = "table-row";    		  
	  } else if(activityId == <%=IConstants.ACTIVITY_TRANSFER_ID %>) { // Transfer 
		  document.getElementById("tdEffectiveLbl").style.display = "table-cell";
		  document.getElementById("idEffectiveDate").style.display = "table-cell";
		  document.getElementById("tranferTypeTR").style.display = "table-row";
	  } else if(activityId == <%=IConstants.ACTIVITY_PROMOTION_ID %> || activityId == <%=IConstants.ACTIVITY_DEMOTION_ID %>) { // Promotion or Demotion
		  //levelLTD levelVTD desigLTD desigVTD gradeLTD gradeVTD incrementPercentTR 
		  
		  document.getElementById("tdEffectiveLbl").style.display = "table-cell";
		  document.getElementById("idEffectiveDate").style.display = "table-cell";
		  document.getElementById("levelLTD").style.display = "table-cell";
		  document.getElementById("levelVTD").style.display = "table-cell";
		  
		  document.getElementById("desigLTD").style.display = "table-cell";
		  document.getElementById("desigVTD").style.display = "table-cell";
		  
		  document.getElementById("gradeLTD").style.display = "table-cell";
		  document.getElementById("gradeVTD").style.display = "table-cell";
		  
	  } else if(activityId == <%=IConstants.ACTIVITY_INCREMENT_ID %>) { // Increment 
		  document.getElementById("tdEffectiveLbl").style.display = "table-cell";
		  document.getElementById("idEffectiveDate").style.display = "table-cell";
		  document.getElementById("incrementTypeTR").style.display = "table-row";
		 
		  document.getElementById("btnCalculateArrear").style.display = "inline";
		  document.getElementById("spanApplyArrear").style.display = "inline";
	  		var strEmpId = document.getElementById("strEmpId2").value;
	  		var effectiveDate = document.getElementById("idEffectiveDate").value;
	  		//alert("strEmpId ===> " + strEmpId);
	  		var action = 'EmpActivityEmpSalaryDetails.action?empId=' + strEmpId+'&effectiveDate='+effectiveDate;
	  		getContent('salaryDetailsDiv', action);
	  	  	window.setTimeout(function() {
	  	  		changeLabelValuesE('1', '');
		  	}, 1500); 
	  } else if(activityId == <%=IConstants.ACTIVITY_GRADE_CHANGE_ID %>) { // Grade change 
		  document.getElementById("gradeChangeLTD").style.display = "table-cell";
		  document.getElementById("gradeChangeVTD").style.display = "table-cell";
		  document.getElementById("tdEffectiveLbl").style.display = "table-cell";
		  document.getElementById("idEffectiveDate").style.display = "table-cell";
		  //getGradesByLevel();
	  } else if(activityId == <%=IConstants.ACTIVITY_TERMINATE_ID %>) { // Terminate 
		 // document.getElementById("noticeTR").style.display = "table-row";
		  document.getElementById("tdEffectiveLbl").style.display = "table-cell";
		  document.getElementById("idEffectiveDate").style.display = "table-cell";
		  document.getElementById("noticeTR").style.display = "none";
	  } else if(activityId == <%=IConstants.ACTIVITY_NOTICE_PERIOD_ID %>) { //  Notice Period 
		  document.getElementById("tdEffectiveLbl").style.display = "none";
		  document.getElementById("idEffectiveDate").style.display = "none";
		  document.getElementById("noticeTR").style.display = "table-row";
	  } else if(activityId == <%=IConstants.ACTIVITY_RESIGNATION_WITHDRWAL_ID %>) { // Withdrawn Resignation
		  document.getElementById("tdEffectiveLbl").style.display = "table-cell";
	      document.getElementById("idEffectiveDate").style.display = "table-cell";
	  } else if(activityId == <%=IConstants.ACTIVITY_FULL_FINAL_ID %>) { // Full & Final 
		  document.getElementById("tdEffectiveLbl").style.display = "table-cell";
		  document.getElementById("idEffectiveDate").style.display = "table-cell";
	  } else if(activityId == <%=IConstants.ACTIVITY_NEW_JOINEE_PENDING_ID %>) { // New Joinee Pending 
		  document.getElementById("tdEffectiveLbl").style.display = "table-cell";
		  document.getElementById("idEffectiveDate").style.display = "table-cell";
	  } else if(activityId == <%=IConstants.ACTIVITY_LIFE_EVENT_ID %>) { // Life Event Increment 
		 
		  document.getElementById("tdEffectiveLbl").style.display = "table-cell";
		  document.getElementById("idEffectiveDate").style.display = "table-cell";
		  document.getElementById("incrementPercentTR").style.display = "table-row";
		  document.getElementById("btnCalculateArrear").style.display = "inline";
		  document.getElementById("spanApplyArrear").style.display = "inline";
		  var strEmpId = document.getElementById("strEmpId2").value;
		  var effectiveDate = document.getElementById("idEffectiveDate").value;
	  	  var action = 'EmpActivityEmpSalaryDetails.action?empId=' + strEmpId+'&effectiveDate='+effectiveDate;
	  	  getContent('salaryDetailsDiv', action);
	  	  	window.setTimeout(function() {
	  	  		changeLabelValuesE('1', '');
		  	}, 1500); 
		}
	  
	}
    
   function getDocumentList(val, strOrgId) {
	   
	 	var xmlhttp = GetXmlHttpObject();
		if (xmlhttp == null) {
			alert("Browser does not support HTTP Request");
			return;
		} else {
			var xhr = $.ajax({
				url : 'GetOrgwiseDocumentList.action?activityId='+val+'&strOrgId='+strOrgId,
				cache : false,
				success : function(data) {
					document.getElementById('documentListTD').innerHTML = data;
				}
			});
		}
	}
    
   function GetXmlHttpObject() {
	    if (window.XMLHttpRequest) {
	            // code for IE7+, Firefox, Chrome, Opera, Safari
	            return new XMLHttpRequest();
	    }
	    if (window.ActiveXObject) {
	            // code for IE6, IE5
	            return new ActiveXObject("Microsoft.XMLHTTP");
	    }
	    return null;
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
     		  document.getElementById("empTypeLTD").style.display = "none";
       		  
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
  	  
  	  <%
			for (int i=0; couterlist!=null && i<couterlist.size(); i++) {
				java.util.List cinnerlist = (java.util.List)couterlist.get(i); 
				if(cinnerlist == null) cinnerlist = new ArrayList();
				if(cinnerlist != null && cinnerlist.size()>0 && !cinnerlist.isEmpty()){
						
						if(cinnerlist.get(4)!= null && !cinnerlist.get(4).equals("") && cinnerlist.get(4).equals("P")) {
					%>
							var amt = document.getElementById("hide_"+<%=(String)cinnerlist.get(1)%>).value; 	
							document.getElementById("lblValue_"+<%=(String)cinnerlist.get(1)%>+"_"+<%=(String)cinnerlist.get(5)%>).value = amt;
					<%}else{%>
							var amt = document.getElementById("hide_"+<%=(String)cinnerlist.get(1)%>).value; 	 
							document.getElementById(""+<%=(String)cinnerlist.get(1)%>).value = amt;
					<%}
				 }	
				%>
		<% } %>
		changeLabelValuesE("1", '');
	}
    
    function disableAll() {
    	if(document.getElementById('strIncrementType')) {
    		document.getElementById('strIncrementType').selectedIndex = 0;
  	    }
      
    	if(document.getElementById('strTransferType')) {
    		 document.getElementById('strTransferType').selectedIndex = 0;
  	    }
   	 
    	if(document.getElementById('strOrganisation')) {
    		 document.getElementById('strOrganisation').selectedIndex = 0; 0;
 	    }
   	 
    	if(document.getElementById('strWLocation')) {
   		 document.getElementById('strWLocation').selectedIndex = 0; 0;
	    }
    	
    	if(document.getElementById('strDepartment')) {
      		 document.getElementById('strDepartment').selectedIndex = 0; 0;
   	    }
       	
    	if(document.getElementById('strLevel')) {
     		 document.getElementById('strLevel').selectedIndex = 0; 0;
  	    }
   	 
   	    if(document.getElementById('strDesignation')){
     	  	document.getElementById('strDesignation').selectedIndex = 0;
     	}
   	    
	   	 if(document.getElementById('empGrade')){
	  	  	document.getElementById('empGrade').selectedIndex = 0;
	  	}
   	
	   	 if(document.getElementById("promotionTR")){
	   		document.getElementById("promotionTR").style.display = "none";
	   	 }
   	  
	   	 if(document.getElementById("extendProbationTR")){
	   	  document.getElementById("extendProbationTR").style.display = "none";
	   	 }
   	 
	   	 if(document.getElementById("tranferTypeTR")){
	   		 document.getElementById("tranferTypeTR").style.display = "none";
		 }
   	 
	   	 if(document.getElementById("documentNameTR")){
	   		 document.getElementById("documentNameTR").style.display = "none";
	   	 }
		 if(document.getElementById("incrementTypeTR")){
	   		 document.getElementById("incrementTypeTR").style.display = "none";
	   	 }
   	  
	   	 if(document.getElementById("salaryDetailsDiv")){
	   		document.getElementById("salaryDetailsDiv").innerHTML = "";
	   	 }
		 
		 if(document.getElementById("incrementPercentTR")) {
			 document.getElementById("incrementPercentTR").style.display = "none";
		 } 
		  
		 if(document.getElementById("legalEntityTR")) {
			 document.getElementById("legalEntityTR").style.display = "none";
		 } 
   	  	  
		 if(document.getElementById("locationLTD")) {
			 document.getElementById("locationLTD").style.display = "none";
		 }
		 
		 if(document.getElementById("locationVTD")) {
			 document.getElementById("locationVTD").style.display = "none";
		 }
   	     
		 if(document.getElementById("empTypeLTD")) {
			 document.getElementById("empTypeLTD").style.display = "none";
		 }
		 
		 if(document.getElementById("empTypeVTD")) {
			 document.getElementById("empTypeVTD").style.display = "none";
		 }
		  	  
		 if(document.getElementById("sbuLTD")) {
			 document.getElementById("sbuLTD").style.display = "none";
		 }
		 
		 if(document.getElementById("sbuVTD")) {
			 document.getElementById("sbuVTD").style.display = "none";
		 }
		 
		 if(document.getElementById("levelLTD")) {
			 document.getElementById("levelLTD").style.display = "none";
		 }
		 
		 if(document.getElementById("levelVTD")) {
			 document.getElementById("levelVTD").style.display = "none";
		 }
		  
		 if(document.getElementById("desigLTD")) {
			 document.getElementById("desigLTD").style.display = "none";
		 }
		 
		 if(document.getElementById("desigVTD")) {
			 document.getElementById("desigVTD").style.display = "none";
		 }
		  
		 if(document.getElementById("deptLTD")) {
			  document.getElementById("deptLTD").style.display = "none";
		 } 
		  
		 if(document.getElementById("deptVTD")) {
			 document.getElementById("deptVTD").style.display = "none";
		 } 

		 if(document.getElementById("gradeLTD")) {
			 document.getElementById("gradeLTD").style.display = "none";
		 }
		  
		 if(document.getElementById("gradeVTD")) {
			 document.getElementById("gradeVTD").style.display = "none";
		 }
		
		 if(document.getElementById("gradeChangeLTD")) {
			 document.getElementById("gradeChangeLTD").style.display = "none";
		 }
		  
		 if(document.getElementById("gradeChangeVTD")) {
			 document.getElementById("gradeChangeVTD").style.display = "none";
		 }
		 
		 if(document.getElementById("noticeTR")) {
			 document.getElementById("noticeTR").style.display = "none";
		 }
		  
		 if(document.getElementById("probationTR")) {
			 document.getElementById("probationTR").style.display = "none";
		 }
		  
		  
    }
    
    function disableAll1() {
  	  //strTransferType strOrganisation strWLocation strLevel strDepartment
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
  		
  		if(document.getElementById('strDesignation')) {
	  		document.getElementById('strDesignation').selectedIndex = 0;
	    }
  		
  		if(document.getElementById('empGrade')) {
  		  document.getElementById('empGrade').selectedIndex = 0;
	    }
    	
  		if(document.getElementById('promotionTR')) {
  			document.getElementById("promotionTR").style.display = "none";
  	    }
  		
  		if(document.getElementById('legalEntityTR')) {
  			 document.getElementById("legalEntityTR").style.display = "none";
  	    }
   	   
  		if(document.getElementById('locationLTD')) {
  			 document.getElementById("locationLTD").style.display = "none";
  	    }
  		
  		if(document.getElementById('locationVTD')) {
  			 document.getElementById("locationVTD").style.display = "none";
  	    }
   	  
  		if(document.getElementById("empTypeLTD")) {
			document.getElementById("empTypeLTD").style.display = "none";
		}
		 
		if(document.getElementById("empTypeVTD")) {
			document.getElementById("empTypeVTD").style.display = "none";
		}
		 
  		if(document.getElementById('sbuLTD')) {
  			 document.getElementById("sbuLTD").style.display = "none";
 	    }
 		
 		if(document.getElementById('sbuVTD')) {
 			document.getElementById("sbuVTD").style.display = "none";
 	    }
   	   
 		if(document.getElementById('deptLTD')) {
 			document.getElementById("deptLTD").style.display = "none";
	    }
		
		if(document.getElementById('deptVTD')) {
			document.getElementById("deptVTD").style.display = "none";
	    }
   	  
		if(document.getElementById('levelLTD')) {
			 document.getElementById("levelLTD").style.display = "none";
	    }
		
		if(document.getElementById('levelVTD')) {
			 document.getElementById("levelVTD").style.display = "none";
	    }
   	  
		if(document.getElementById('desigLTD')) {
			document.getElementById("desigLTD").style.display = "none";
	    }
		
		if(document.getElementById('desigVTD')) {
			document.getElementById("desigVTD").style.display = "none";
	    }
   	    
		if(document.getElementById('gradeLTD')) {
			document.getElementById("gradeLTD").style.display = "none";
	    }
		
		if(document.getElementById('gradeVTD')) {
			 document.getElementById("gradeVTD").style.display = "none";
	    }
   	    
		if(document.getElementById('gradeChangeLTD')) {
			document.getElementById("gradeChangeLTD").style.display = "none";
	    }
		
		if(document.getElementById('gradeChangeVTD')) {
			document.getElementById("gradeChangeVTD").style.display = "none";
	    }
   	  
    }
    
    disableAll();
    disableAll1();
    
    function showDesignation(strLevel) {
    	<%if(uF.parseToInt((String)request.getAttribute("salaryStructure")) == IConstants.S_GRADE_WISE){ %>
	    	var action = 'GetDesigList.action?strLevel=' + strLevel + "&type=EA";
		    getContent('desigListSpan', action);
    	<%} else { %>
		    var strActivity = document.getElementById("strActivity").value;
		    if(strActivity !='' && (strActivity == <%=IConstants.ACTIVITY_PROMOTION_ID %> || strActivity == <%=IConstants.ACTIVITY_DEMOTION_ID %>)){
			    /* var f_org = document.frmEmployeeActivity.f_org.value; 
			    var f_strWLocation = document.frmEmployeeActivity.f_strWLocation.value;
			    var f_department = document.frmEmployeeActivity.f_department.value;
			    var f_level = document.frmEmployeeActivity.f_level.value; */ 
			   //var strEmpId = document.frmEmployeeActivity.strEmpId.value;
			    /* var idEffectiveDate = document.getElementById("idEffectiveDate").value; */
			    var strEmpId = document.getElementById("strEmpId2").value;
			    var effectiveDate = document.getElementById("idEffectiveDate").value;
				var action = 'EmpActivityEmpSalaryDetails.action?empId='+strEmpId+'&strActivity='+strActivity+'&strLevel='+strLevel+'&effectiveDate='+effectiveDate;
				getContent('salaryDetailsDiv', action);
				window.setTimeout(function() {
					changeLabelValuesE1('1');
				}, 1500); 
			    /* var action ='EmpActivityLevelSalaryDetails.action?dataType=D&strEmpId='+strEmpId+'&strActivity='+strActivity+'&effectiveDate='+idEffectiveDate+'&strLevel='+strLevel;
			    action +='&f_org='+f_org+'&f_strWLocation='+f_strWLocation+'&f_department='+f_department+'&f_level='+f_level;
			    window.location = action; */
		    
		    }/*  else { */	
		    var action = 'GetDesigList.action?strLevel=' + strLevel + "&type=EA";
		    getContent('desigListSpan', action);
	    	/* } */
    	<%} %>
    }
    
  	function getGrades(value) {
  		<%if(uF.parseToInt((String)request.getAttribute("salaryStructure")) == IConstants.S_GRADE_WISE){ %>
	  		var action = 'GetGradeList.action?strDesignation=' + value + "&type=EA&typeEA=promotion";
	    	getContent('gradeListSpan', action);
  		<%} else { %>
	  		var action = 'GetGradeList.action?strDesignation=' + value + "&type=EA";
	    	getContent('gradeListSpan', action);
	    <%} %>	
    }
  	
  	function getGradeSalaryStructure(strGrade) {
  		<%if(uF.parseToInt((String)request.getAttribute("salaryStructure")) == IConstants.S_GRADE_WISE){ %>
	  		var strActivity = document.getElementById("strActivity").value;
		    if(strActivity !='' && (strActivity == <%=IConstants.ACTIVITY_PROMOTION_ID %> || strActivity == <%=IConstants.ACTIVITY_DEMOTION_ID %> || strActivity == <%=IConstants.ACTIVITY_GRADE_CHANGE_ID %>)){
			    //var strEmpId = document.frmEmployeeActivity.strEmpId.value;
			    var strEmpId = document.getElementById("strEmpId2").value;
			    var effectiveDate = document.getElementById("idEffectiveDate").value;
				var action = 'EmpActivityEmpSalaryDetails.action?empId='+strEmpId+'&strActivity='+strActivity+'&strGrade='+strGrade+'&effectiveDate='+effectiveDate;
				getContent('salaryDetailsDiv', action);
				window.setTimeout(function() {
					changeLabelValuesE1('1');
				}, 1500); 
			    /* var action ='EmpActivityLevelSalaryDetails.action?dataType=D&strEmpId='+strEmpId+'&strActivity='+strActivity+'&effectiveDate='+idEffectiveDate+'&strLevel='+strLevel;
			    action +='&f_org='+f_org+'&f_strWLocation='+f_strWLocation+'&f_department='+f_department+'&f_level='+f_level;
			    window.location = action; */
		    
		    }
	    <%} %>	
  	}
    
    function getWLocSbuDeptLevelByOrg(value) {
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
		 var action = 'GetGradeList.action?levelId=' + levelId + "&type=EA";
    	 getContent('gradeListSpan', action);
    }
    
    function openEmpProfilePopup(empId){
    	var id=document.getElementById("panelDiv");
   		if(id) {
    		id.parentNode.removeChild(id);
    	}
    
   		var dialogEdit = '.modal-body';
	 	$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$("#modalInfo").show();
	    $(".modal-title").html('Employee Information');
	    if($(window).width() >= 900){
		  $(".modal-dialog").width(900);
	    }
	  
	    $.ajax({
		    url :"MyProfile.action?empId="+empId+"&proPopup=proPopup" ,
		    cache : false,
		    success : function(data) {
		   		 $(dialogEdit).html(data);
		    }
		 });
    }
    
    function updateApproveDenyStatus(status, approveType, offBoardId,userType) {
	    var denyApprove = "Approve";
	    if(status == '-1') {
	    denyApprove = "Deny";
    }
	    
   	 var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 $(".modal-title").html('Resignation '+ denyApprove + ' Reason');
	 $.ajax({
		    url : 'UpdateRequest.action?type=EA&S='+status+'&M='+approveType+'&RID='+offBoardId+'&T=REG&userType='+userType, 
		    cache : false,
		    success : function(data) {
		    	$(dialogEdit).html(data);
		    }
	  });
    }
    
	function exitFeedbackFormsDashboard() {
	     var dialogEdit = '.modal-body';
	   	 $(dialogEdit).empty();
	   	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	   	 $("#modalInfo").show();
	   	 $(".modal-title").html('Exit Feedback Forms');
	   	$.ajax({
	   	    url : "ExitFeedbackForms.action",
	   	    cache : false,
	   	    success : function(data) {
	   	    	$(dialogEdit).html(data);
	   	    }
	   	 });
      }
    
    function exitFeedBackPDF(id,resignId) {
    	var dialogEdit = '.modal-body';
      	 $(dialogEdit).empty();
      	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
      	 $("#modalInfo").show();
      	 $(".modal-title").html('Exit Feedback PDF');
      	$.ajax({
    		url : "ExitFeedBackPdf.action?id="+id+"&resignId="+resignId,
    		cache : false,
    		success : function(data) {
    			$(dialogEdit).html(data);
    		}
    	});
    }
    
    function getApprovalStatus(id,empname,from){
    	var dialogEdit = '.modal-body';
     	 $(dialogEdit).empty();
     	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
     	 $("#modalInfo").show();
     	 $(".modal-title").html('Work flow of '+empname);
     	$.ajax({
     	    url : "GetLeaveApprovalStatus.action?effectiveid="+id+"&type=10",
     	    cache : false,
     	    success : function(data) {
     	    $(dialogEdit).html(data);
     	    }
     	});
    }	 

    $(function() {
        $( "#idEffectiveDate").datepicker({format: 'dd/mm/yyyy'});
    });
    
    function show_designation() {
    	dojo.event.topic.publish("show_designation");
    }
    
    function show_grade() {
    	dojo.event.topic.publish("show_grade");
    }
    	
    	
    function show_employees() {
    	dojo.event.topic.publish("show_employees");
    }
    
    function isNumberKey(evt) {
       var charCode = (evt.which) ? evt.which : event.keyCode;
       if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46)
          return false;
    
       return true;
    }
    
    function isOnlyNumberKey(evt) {
  	   var charCode = (evt.which) ? evt.which : event.keyCode;
  	   if (charCode <= 31 || (charCode >= 48 && charCode <= 57)) {
  	      return true;
  	   }
  	   return false; 
  	}
    
    var roundOffCondition='<%=(String)request.getAttribute("roundOffCondition") %>';
    
    function incrementBasicAmount(id) {
		var percentAmount = document.getElementById(id).value;
		if(parseFloat(percentAmount) > parseFloat("0") ) {
			var strIncrementType = document.getElementById("strIncrementType").value;
			if(strIncrementType == '2') {
				percentAmount = parseFloat(percentAmount) * 2;
			}
			
			
			if(document.getElementById("301")) {
				var txt_amount = document.getElementById("hide_301").value;
				
				var newBasic = (parseFloat(txt_amount) * parseFloat(percentAmount)) / 100;
				
				var finalBasic = parseFloat(txt_amount) + parseFloat(newBasic);
				document.getElementById("301").value = parseFloat(finalBasic).toFixed(2);
				
				changeLabelValuesE("301", '');
			} else if(document.getElementById("1")) {
				var txt_amount = document.getElementById("hide_1").value;
				
				var newBasic = (parseFloat(txt_amount) * parseFloat(percentAmount)) / 100;
				
				var finalBasic = parseFloat(txt_amount) + parseFloat(newBasic);
				document.getElementById("1").value = parseFloat(finalBasic).toFixed(2);
				
				changeLabelValuesE("1", '');
			} else {
				<%
				for (int i=0; couterlist!=null && i<couterlist.size(); i++) {
					java.util.List cinnerlist = (java.util.List)couterlist.get(i);
					if(cinnerlist !=null && cinnerlist.size()>0 && !cinnerlist.isEmpty()){
				%>	
						var amt = document.getElementById("hide_"+<%=(String)cinnerlist.get(1)%>).value; 	
						var newAmt = (parseFloat(amt) * parseFloat(percentAmount)) / 100;
						
						var finalAmt = parseFloat(amt) + parseFloat(newAmt);
						document.getElementById(""+<%=(String)cinnerlist.get(1)%>).value = finalAmt.toFixed(2);
					<% } %>
				<% } %>
			} 
		} else {
			<%
			for (int i=0; couterlist!=null && i<couterlist.size(); i++) {
				java.util.List cinnerlist = (java.util.List)couterlist.get(i); 
				if(cinnerlist != null && !cinnerlist.isEmpty() && cinnerlist.size()>0){	
				%>
					var amt = document.getElementById("hide_"+<%=(String)cinnerlist.get(1)%>).value; 	 
					document.getElementById(""+<%=(String)cinnerlist.get(1)%>).value = amt;
				<% } %>
		<% } %>
			changeLabelValuesE('1', '');
		}
	}
    
    function checkSalaryHeadDisable() {
		changeLabelValuesE('1', '');
	}
    
    

function getRoundOffValue(val) {
	var roundOffVal = 0;
	if(parseInt(roundOffCondition) == 1){
		roundOffVal = parseFloat(val).toFixed(1);
	} else if(parseInt(roundOffCondition) == 2){
		roundOffVal = parseFloat(val).toFixed(2);
	} else {
		roundOffVal = Math.round(parseFloat(val));
	}
	
	return roundOffVal;
}

    
    <%  
        String CCID= (String) request.getAttribute("CCID");
        String ccName = (String) request.getAttribute("CCNAME");
        String EMPNAME = (String)session.getAttribute("EMPNAME_P");
        
        String totalInduction = (String)request.getAttribute("totalInduction");
        String totalConfirmation = (String)request.getAttribute("totalConfirmation");
        String totalRetirements = (String)request.getAttribute("totalRetirements");
        String totalResignation = (String)request.getAttribute("totalResignation");
        String totalFD = (String)request.getAttribute("totalFD");
        
        if(EMPNAME==null)
        	EMPNAME = (String)request.getAttribute("EMPNAMEFORCC");
        
        %>	
    
    //var cnt;
   
    
    function submitDoc(dataType){
    document.frmEmployeeActivity.dataType.value=dataType;
    document.frmEmployeeActivity.submit();
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
    
    
    function deleteNewEmployee(pageType, operation, empId) {
// 		alert(date); window.location='AddEmployeeInOneStep.action?pageType=PE&operation=D&empId=
		//$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
   	   	$.ajax({
   			url : 'AddEmployeeInOneStep.action?pageType='+pageType+'&operation='+operation+'&empId='+empId,
   			cache : false,
   			success : function(res) {
   				//$("#divResult").html(res);
   				window.location='EmployeeActivity.action';
   			},
			error: function(result){
				window.location='EmployeeActivity.action';
			}
   		});
	}
    
    
	function calculateAndGetArrearAmount(empId) {
		var dialogEdit = '.modal-body';
    	$(dialogEdit).empty();
    	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$("#modalInfo").show();
    	$('.modal-title').html('Calculated Arrear');
    	if($(window).width() >= 900) {
  		  $(".modal-dialog").width(900);
  	    }
    	var form_data = $("form[name='frmEmpActivity']").serialize();
    	//var effectiveDate = document.getElementById("idEffectiveDate").value; &effectiveDate='+effectiveDate+'
    	//alert("form_data ===>> " + form_data);
    	var strEmpId = document.getElementById("strEmpId2").value;
    	$.ajax({
    		url : 'CalculateAndGetArrearAmount.action?pageType=EMPACTIVITY&empId='+strEmpId,
    		data: form_data,
 			cache : false,
 			type: 'POST',
    		success : function(data) {
    			$(dialogEdit).html(data);
    		}
    	});
	}
	
/* ===start parvez date: 28-06-2022=== */	
	function readFileURL(input, targetDiv) {
    	//alert(input);
        if (input.files && input.files[0]) {
            var reader = new FileReader();
            reader.onload = function (e) {
                $('#'+targetDiv).attr('path', e.target.result);
            };
            reader.readAsDataURL(input.files[0]);
        }
    }
/* ===end parvez date: 28-06-2022=== */    
	
</script>
<% 
    String strMessage = (String)session.getAttribute(IConstants.MESSAGE);
    if(strMessage == null) {
    	strMessage = "";
    }
    
    String empType = (String)request.getAttribute("empType");
    String strEmpId = (String)request.getAttribute("strEmpId");
    String strActivity = (String)request.getAttribute("strActivity");
    
    //System.out.println("empType==>"+empType+"==>strEmpId==>"+strEmpId+"==>strActivity==>"+strActivity);
    Map hmEmpActivityDetails = (Map)request.getAttribute("hmEmpActivityDetails");
    
    Map<String, String> hmEmpProfile = (Map<String, String>) request.getAttribute("hmEmpProfile");
    if (hmEmpProfile == null) {
    	hmEmpProfile = new HashMap<String, String>();
    }
    String probationRemaining = (String) request.getAttribute("PROBATION_REMAINING");
    String noticePeriod = (String) request.getAttribute("NOTICE_PERIOD");
    String totalExp = (String) request.getAttribute("TOTAL_EXP");
    
    String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
    List alSkills = (List) request.getAttribute("alSkills");
   	
    //EncryptionUtils EU = new EncryptionUtils();
    %>
    
<%--  <jsp:include page="../common/SubHeader.jsp">
    <jsp:param value="Employee Activity" name="title"/>
    </jsp:include> --%>
    
     
<section class="content">
    <div class="row jscroll">
    <section class="col-lg-12 connectedSortable">
        <div class="box box-primary">
            <div class="box-body" style="padding: 5px;min-height: 600px;">
                <div class="leftbox reportWidth">
                    <%= strMessage%>
                    <div style="float:left;width:100%;">
                        <div style="padding: 8px 11px; width:97%;color: #346897; font-size: 16px; font-weight: bold; margin: 7px 0px 0px 0px; ">
                            <!-- #5a87b4 -->
                            Life Cycle Activity
                        </div>
                        <div class="cat_heading" style="width:98%;color: #777777;margin-left: 10px;">
                            <p style="background-color: #FFFF96; padding: 1px; border: 1px solid #cccccc;">
                                <strong>Note:</strong>
                                Please use filter and select an employee to new activity.
                            </p>
                        </div>
                    </div>
                    <div class="row row_without_margin">
                    	<s:form name="frmEmployeeActivity" id="frmEmployeeActivity" theme="simple" action="EmployeeActivity" method="POST">
                            <s:hidden name="dataType" id="dataType" ></s:hidden>
							<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
								<i class="fa fa-filter" aria-hidden="true"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Organization</p>
									<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" cssClass="form-control " listValue="orgName" onchange="document.frmEmployeeActivity.submit();" list="organisationList" key=""/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Location</p>
									<s:select theme="simple" name="f_strWLocation" id="f_strWLocation" listKey="wLocationId"  listValue="wLocationName" headerKey="" headerValue="All Locations" 
										onchange="getContent('empDiv', 'GetLiveEmployeeList.action?f_level='+document.frmEmployeeActivity.f_level.options[document.frmEmployeeActivity.f_level.selectedIndex].value+'&f_department='+document.frmEmployeeActivity.f_department.options[document.frmEmployeeActivity.f_department.selectedIndex].value+'&f_strWLocation='+document.frmEmployeeActivity.f_strWLocation.options[document.frmEmployeeActivity.f_strWLocation.selectedIndex].value)" 		
	                                    list="wLocationList" key="" />
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Department</p>
									<s:select name="f_department" id="f_department" list="departmentList" listKey="deptId"  listValue="deptName" 
	                                    headerKey="" headerValue="All Departments" onchange="getContent('empDiv', 'GetLiveEmployeeList.action?f_level='+document.frmEmployeeActivity.f_level.options[document.frmEmployeeActivity.f_level.selectedIndex].value+'&f_department='+document.frmEmployeeActivity.f_department.options[document.frmEmployeeActivity.f_department.selectedIndex].value+'&f_strWLocation='+document.frmEmployeeActivity.f_strWLocation.options[document.frmEmployeeActivity.f_strWLocation.selectedIndex].value)" />
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Level</p>
									<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" headerValue="All Levels"  
	                                    listValue="levelCodeName" headerKey="" onchange="getContent('empDiv', 'GetLiveEmployeeList.action?f_level='+document.frmEmployeeActivity.f_level.options[document.frmEmployeeActivity.f_level.selectedIndex].value+'&f_department='+document.frmEmployeeActivity.f_department.options[document.frmEmployeeActivity.f_department.selectedIndex].value+'&f_strWLocation='+document.frmEmployeeActivity.f_strWLocation.options[document.frmEmployeeActivity.f_strWLocation.selectedIndex].value)"
	                                    list="levelList" key="" required="true" />
								</div>
								<div id="empDiv" class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Employee</p>
										<s:select name="strEmpId" id="strEmpId" listKey="employeeId" listValue="employeeName" headerKey="" headerValue="Select Employee"
		                                    list="empList" key="" required="true" onchange="document.frmEmployeeActivity.submit();" />
	                                
								</div>
							</div>
						</s:form>
					</div>
                    <% if(hmEmpActivityDetails!=null) { 
                        %>
                    <div class="tableblock"  style="background:#efefef; padding:5px; border:solid 1px #d4d4d4; margin-top: 10px;">
                        <table width="100%" class="table" style="margin-bottom: 0px;">
                            <tr>
                                <th colspan="4" align="left" style="padding-left: 20px">Current Information</th>
                            </tr>
                            <tr>
                                <td valign="top" style="width:30%;">
                                    <div class="box box-widget widget-user widget-user1">
						            <!-- Add the bg color to the header using any of the bg-* classes -->
							            <!-- <div class="widget-user-header bg-aqua-active"> -->
							             <!-- ====start parvez on 27-10-2022===== --> 	
							              	<%-- <img class="lazy" src="images1/user-background-photo.jpg" data-original='<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE_COVER+"/"+hmEmpProfile.get("EMP_ID")+"/"+hmEmpProfile.get("COVER_IMAGE")%>'> --%>
							              	<%if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_USER_BACKGROUND_PHOTO)) && hmFeatureUserTypeId.get(IConstants.F_USER_BACKGROUND_PHOTO)!=null && hmFeatureUserTypeId.get(IConstants.F_USER_BACKGROUND_PHOTO).size()>0){ 
												List<String> alPhotoInner = hmFeatureUserTypeId.get(IConstants.F_USER_BACKGROUND_PHOTO);
											%>
											<div class="widget-user-header bg-aqua-active" style="height: 130px !important">
												<img class="lazy" src="images1/exusia_banner_<%=alPhotoInner.get(1) %>.png" data-original='<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE_COVER+"/"+hmEmpProfile.get("EMP_ID")+"/"+hmEmpProfile.get("COVER_IMAGE")%>' style="height: auto !important">
											<% } else{ %>
											<div class="widget-user-header bg-aqua-active">
												<img class="lazy" src="images1/user-background-photo.jpg" data-original='<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE_COVER+"/"+hmEmpProfile.get("EMP_ID")+"/"+hmEmpProfile.get("COVER_IMAGE")%>'>
											<% } %>
							             <!-- ====end parvez on 27-10-2022===== --> 	
							              	<h3 class="widget-user-username" style="color: #fff;font-weight: 600; margin-top: 0px; font-size: 16px;"><span><%=uF.showData((String) hmEmpProfile.get("NAME"), "-")%></span> <!-- margin-top: 0px; -->
							              	<span style="float: right;"><a href="MyProfile.action?empId=<%=hmEmpProfile.get("EMP_ID") %>" title="Go to My FactSheet.."><i class="fa fa-address-card-o" style="color: #fff;"></i></a></span>
							              	</h3>
							              	<h5 class="widget-user-desc"><%=uF.showData((String) hmEmpProfile.get("EMPCODE"), "-")%></h5>
							            </div>
						            <div class="widget-user-image">
						            	<%if(docRetriveLocation==null) { %>
						              	<img class="lazy img-circle" id="profilecontainerimg" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + hmEmpProfile.get("IMAGE")%>">
						              	<%} else { %>
						              	<img class="lazy img-circle" id="profilecontainerimg" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+hmEmpProfile.get("EMP_ID")+"/"+IConstants.I_100x100+"/"+hmEmpProfile.get("IMAGE")%>">
						              	<%} %>
						            </div>
						            <div class="box-footer">
						              	<div class="row">
						                	<div class="col-sm-12">
						                 		<div class="description-block">
						                    		<h5 class="description-header"><%=uF.showData((String) hmEmpProfile.get("DESIGNATION_NAME"), "-")%> [<%=uF.showData((String) hmEmpProfile.get("LEVEL_NAME"), "-")%>] [<%=uF.showData((String) hmEmpProfile.get("GRADE_NAME"), "-")%>]</h5>
						                    		<span class="description-text"><%=uF.showData((String) hmEmpProfile.get("DEPARTMENT_NAME"), "-")%> [<%=uF.showData((String) hmEmpProfile.get("SBU_NAME"), "-")%>] [<%=uF.showData((String) hmEmpProfile.get("WLOCATION_NAME"), "-")%>]</span>
						                    		<p class="description-text"><%=uF.showData((String) hmEmpProfile.get("ORG_NAME"), "-")%></p>
						                  		</div>
						                  		<!-- /.description-block -->
						                	</div>
						            
      									</div>
						              <!-- /.row -->
						            </div>
						          </div>
                                </td>
                                <td valign="top">
                                	<div class="box box-default">
						                <div class="box-body" style="padding: 5px; overflow-y: auto;padding: 6px;">
						                    <div class="trow" id="employee_details">
		                                        <table style="margin-left: 10px;margin-bottom: 0px;" class="table table_no_border autoWidth">
		                                            <tr>
		                                                <td class="alignRight">Employee Type:</td>
		                                                <td class="textblue"> <%=uF.showData((String) hmEmpProfile.get("EMP_TYPE"), "-")%></td>
		                                            </tr>
		                                            <tr>
		                                                <td class="alignRight">Date of Joining:</td>
		                                                <td class="textblue"> <%=uF.showData((String) hmEmpProfile.get("JOINING_DATE"), "-")%></td>
		                                            </tr>
		                                            <tr>
		                                                <td class="alignRight">Probation Status:</td>
		                                                <td class="textblue"> 
		                                                <%     
		                                                	   String empStatus = (String)request.getAttribute("EMPSTATUS");

		                                                                          if(empStatus != null && !empStatus.equalsIgnoreCase("PERMANENT")) {
		                                                		if(probationRemaining != null) {
		                                                       		 if(uF.parseToInt(probationRemaining) > 0) {
		                                                      		  %>
		                                                   				 <%=probationRemaining+ " days remaining." %>
		                                                  		  <% } else { %>
		                                                   				 Probation completed.
		                                                   		  <% } %>	
		                                                   	   <%}else { %>
		                                             					 No probation.
		                                                      <% } 
		                                                	} else { %>
		                                                   			 No probation.
		                                                 <% } %>
		                                                </td>
		                                            </tr>
		                                            <tr>
		                                            	<td class="alignRight">Notice Period:</td>
		                                            	<td class="textblue"> <%=uF.showData(noticePeriod, "0")%> days</td>
		                                            </tr>
													
													<tr>
														<td class="alignRight">&nbsp;</td>
														<td class="textblue"></td>
													</tr>
													
													<tr>
														<td class="alignRight">Total Experience:</td>
														<td class="textblue"> <%= uF.showData(totalExp, "-") %></td>
													</tr>
									
    				
													<tr>
														<td class="alignRight">Exp with Current Org:</td>
														<td class="textblue"> <%= uF.showData((String)request.getAttribute("TIME_DURATION"), "-") %></td>
												    </tr>
													
													<tr>
														<td class="alignRight">Education Qualification:</td>
														<td class="textblue"> <%= uF.showData((String)request.getAttribute("educationsName"), "-") %></td>
													</tr>
													
													<tr>
														<td class="alignRight">Skills:</td>
														<td class="textblue"> <%=uF.showData((String) hmEmpProfile.get("SKILLS_NAME"), "-")%></td>
													</tr>
		                                           
		                                       </table>
		                                       <%--  <%if(!uF.parseToBoolean(hmEmpProfile.get("OFFICIAL_FILLED_STATUS"))) {%>
		                                        <div style="float:left;margin-top:60px;margin-left:60px "><img src="images1/warning.png" /> </div>
		                                        <%} %> --%>
		                                    </div>
						                </div>
						                <!-- /.box-body -->
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
	                        <li class="active"><a href="javascript:void(0)" onclick="submitDoc('A');" data-toggle="tab">Activity</a></li>
                            <li><a href="javascript:void(0)" onclick="submitDoc('D');" data-toggle="tab">Activity W/Doc</a></li>
	                        <% } else if(dataType != null && dataType.equals("D")) { 
	                            strLabel ="Activity W/Doc";
	                            %>
	                        <li><a href="javascript:void(0)" onclick="submitDoc('A');" data-toggle="tab">Activity</a></li>
                            <li class="active"><a href="javascript:void(0)" onclick="submitDoc('D');" data-toggle="tab">Activity W/Doc</a></li>
                        <% } %>
                        </ul>	
                    	<div class="tab-content">
             <!-- ===start parvez date: 28-09-2022=== -->       	
                    <%-- <s:form theme="simple" action="EmployeeActivity" method="POST" name="frmEmpActivity" id="frmEmpActivity"> --%>
                    <s:form theme="simple" action="EmployeeActivity" method="POST" name="frmEmpActivity" id="frmEmpActivity" enctype="multipart/form-data">
            <!-- ===end parvez date: 28-06-2022=== -->            
                        <s:hidden name="strEmpId2" id="strEmpId2"/>
                        <input type="hidden" id="currDate" name="currDate" value="<%=currDate%>"/>
						<input type = "hidden" id= "empStatus" name="empStatus" value="<%=(String)hmEmpProfile.get("EMP_STATUS")%>"/>
						<input type = "hidden" id= "empName" name="empName" value="<%=(String)hmEmpProfile.get("NAME")%>"/>
                        <s:hidden name="dataType"></s:hidden>
                        <div style="float: left;">
                            <table class="table table_no_border autoWidth" style="margin-top: 20px;">
                                <tr>
                                    <td class="txtlabel" colspan="2">New <%=strLabel %> Information</td>
                                </tr>
                                <tr>
                                    <td colspan=4>
                                        <s:fielderror />
                                    </td>
                                </tr>
                                <tr>
                                    <td class="txtlabel alignRight">Activity:<sup>*</sup></td>
                                    <td>
                                        <s:select name="strActivity" id="strActivity" listKey="activityId" theme="simple" cssClass="validateRequired form-control autoWidth " listValue="activityName" headerKey="" 
                                            headerValue="Select Activity" list="activityList" key="" onchange="selectElements(this.value)" />
                                        <span class="hint">Select an activity from the list.<span class="hint-pointer">&nbsp;</span></span>
                                    </td>
                                    <td class="txtlabel alignRight" id = "tdEffectiveLbl">Effective Date:<sup>*</sup></td>
                                    <td>
                                        <s:textfield name="effectiveDate" id="idEffectiveDate" cssClass="validateRequired " />
                                    </td>
                                </tr>
                               
                                <tr id="promotionTR" >
                                    <td class="txtlabel alignRight" valign="top">Leave Balance:</td>
                                    <td colspan="3">
                                        <table class="table table-bordered">
                                            <% for(int i=0; leaveTypeListWithBalance != null && !leaveTypeListWithBalance.isEmpty() && i<leaveTypeListWithBalance.size(); i++) {
												List<String> innerList = leaveTypeListWithBalance.get(i);
											%>
												<tr>
													<td class="txtlabel alignLeft">
														<input type="checkbox" name="leaveTypeId" id="leaveTypeId" value="<%=innerList.get(6)%>" />
														<%=innerList.get(0) %>:
													</td>
													<td class="txtlabel alignLeft"> 
														<input type="text" name="leaveBal_<%=innerList.get(6) %>" id="leaveBal_<%=innerList.get(6) %>" value="<%=innerList.get(5) %>" style="width: 41px; text-align: right;"/>
													</td>
												</tr>
											<%} %>
                                        </table>
                                    </td>
                                </tr>
                                <tr id="extendProbationTR">
                                    <td class="txtlabel alignRight">No. of Days:<sup>*</sup></td>
                                    <td>
                                        <s:textfield name="strExtendProbationDays" id= "strExtendProbationDays" cssClass="validateRequired " onkeypress="return isOnlyNumberKey(event)" onkeyup="checkNoOfDays('E')"/>
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
                                
                         <!-- ===start parvez date: 28-06-2022=== -->    
                           		<tr id="documentNameTR" style="display: none">
                                	<td class="txtlabel alignRight">Document Name:</td>
	                              	<td id="documentListTD">
		                              	<s:select name="strDocumentName" id="strDocumentName" listKey="documentId" theme="simple" cssClass="validateRequired" listValue="documentName"  
		                              		headerKey="" headerValue="Select Document Name" list="documentList" key=""/>
		                                <span class="hint">Select Document Name from the list.<span class="hint-pointer">&nbsp;</span></span>
		                            </td>
	                            </tr>
	                            
	                            <tr id="documentAttachTR" style="display: none">
	                            	<td class="txtlabel alignRight">Attach Document:</td>
                           			<td>
                           				<div id="docfile"></div>
                           				<input type="file" name="strDocumentFile" id="strDocumentFile"  onchange="readFileURL(this, 'docfile');" class="validateRequired" />
                           				<span class="hint">Select the Document.<span class="hint-pointer">&nbsp;</span></span>
                           			</td>
                           		</tr>
                         <!-- ===end parvez date: 28-06-2022=== -->    
                                
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
                                    <td class="txtlabel alignRight" id="gradeLTD">
                                        Grade:<sup>*</sup>
                                        <s:hidden name="strGrade"/>
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
                                    <td class="txtlabel alignRight" id="gradeChangeLTD">
                                        Grade:<sup>*</sup>
                                        <s:hidden name="strGrade"/>
                                    </td>
                                    <td id="gradeChangeVTD">
                                        <span id="gradeChangeListSpan">
                                            <s:select theme="simple" name="empChangeGrade" id="empChangeGrade" cssClass="validateRequired" list="gradeChangeList" 
                                                listKey="gradeId" listValue="gradeCode" headerKey="" headerValue="Select Grade" required="true" onchange="getGradeSalaryStructure(this.value)"/>
                                        </span>
                                    </td>
                                </tr>
                                <tr id="noticeTR" style="display: none">
                                    <td class="txtlabel alignRight">No. of Days:<sup>*</sup></td>
                                    <td>
                                        <s:textfield name="strNoticePeriod" id="strNoticePeriod" cssClass="validateRequired " onkeypress="return isOnlyNumberKey(event)" onkeyup="checkNoOfDays('N')"/>
                                    </td>
                                    <td class="txtlabel alignRight"></td>
                                    <td></td>
                                </tr>
                                <tr id="probationTR" style="display: none">
                                    <td class="txtlabel alignRight" id="idPeriodL">No. of Days:<sup>*</sup></td>
                                    <td id="idPeriodV">
                                        <s:textfield name="strProbationPeriod" id= "strProbationPeriod" cssClass="validateRequired " onkeypress="return isOnlyNumberKey(event)" onkeyup="checkNoOfDays('P')"/>
                                    </td>
                                    <td class="txtlabel alignRight"></td>
                                    <td></td>
                                </tr>
                                <tr id="incrementPercentTR" style="display: none">
                                    <td class="txtlabel alignRight" id="idPeriodL">Increment Percentage:<sup>*</sup></td>
                                    <td id="idPeriodV">
                                        <s:textfield name="strIncrementPercentage" id="strIncrementPercentage" cssClass="validateRequired " maxlength="5" 
                                            onchange="incrementBasicAmount(this.id)" onblur="incrementBasicAmount(this.id)" onkeypress="return isNumberKey(event)" cssStyle="width:50px;"/>
                                    </td>
                                    <td class="txtlabel alignRight"></td>
                                    <td></td>
                                </tr>
                                <tr>
                                    <td class="txtlabel alignRight" valign="top" id="reasonLTD">Reason:<sup>*</sup></td>
                                    <td colspan="3" id="reasonVTD">
                                        <s:textarea name="strReason" rows="10" cssClass="validateRequired " cols="60" ></s:textarea>
                                        <span class="hint">Add the reason for this activity for reference.<span class="hint-pointer">&nbsp;</span></span>
                                    </td>
                                </tr>
                                <tr>
									<td></td>
									<td colspan="3">
										<%-- <input type="button" name="strUpdate" class="input_button" value="Update" onclick="checkEmpStatus('<%=(String)hmEmpProfile.get("EMPLOYMENT_TYPE")%>','<%=(String)hmEmpProfile.get("NAME")%>','A');"/> --%>
										<span id="spanApplyArrear"><input type="checkbox" name="strApplyArrear" id="strApplyArrear" /> Apply Arrear &nbsp;</span>
										<s:submit name="strUpdate" cssClass="btn btn-primary" value="Update" onclick="return confirm('Are you sure, you want to change this Activity?\nPLEASE NOTE THAT THIS CAN IMPACT THE LIFECYCLE OF THE EMPLOYEE')"/> 
										<% if(dataType != null && dataType.equals("D")) { %>
											<s:submit name="strUpdateDocument" cssClass="btn btn-primary" value="Update & Send Document" onclick="return confirm('Are you sure, you want to change this Activity?\nPLEASE NOTE THAT THIS CAN IMPACT THE LIFECYCLE OF THE EMPLOYEE.')"/>
										<%} %>
										<input type="button" name="btnCalculateArrear" id="btnCalculateArrear" class="btn btn-primary" value="Calculate Arrear" onclick="calculateAndGetArrearAmount();"/>
									</td>
								</tr>
								
                               <%--  <tr>
                                    <td></td>
                                    <td colspan="3">
                                        <input type="button" name="strUpdate" class="btn btn-primary" value="Update" onclick="checkEmpStatus('<%=(String)hmEmpProfile.get("EMPLOYMENT_TYPE")%>','<%=(String)hmEmpProfile.get("NAME")%>','A');"/>
                                        <s:submit name="strUpdate" cssClass="btn btn-primary" value="Update" onclick="return confirm('Are you sure, you want to change this Activity?\nPLEASE NOTE THAT THIS CAN IMPACT THE LIFECYCLE OF THE EMPLOYEE')"/>
                                        <% if(dataType != null && dataType.equals("D")){ %>
                                        <s:submit name="strUpdateDocument" cssClass="btn btn-primary" value="Update & Send Document" onclick="return confirm('Are you sure, you want to change this Activity?\nPLEASE NOTE THAT THIS CAN IMPACT THE LIFECYCLE OF THE EMPLOYEE.')"/>
                                        <%} %>
                                    </td>
                                </tr> --%>
                            </table>
                        </div>
                        <div id="salaryDetailsDiv" style="float: left; width: 44%;"></div>
                    </s:form>
                    	</div>
                    </div>
                    <% } %>
                    
                    <div style="margin-top: 30px;" class="clr">
                        <div>
                            <div style="padding: 8px 11px;width:98%; color: #346897; font-size: 16px; font-weight: bold; margin: 7px 0px 0px 0px; border-top: 1px solid #CCCCCC;">
                                <!-- #5a87b4 -->
                                Daily Activity  		
                            </div>
                            <div class="cat_heading" style="width:99%;color: #777777;margin-left: 10px;">
                                <p style="background-color: #FFFF96; padding: 1px; border: 1px solid #cccccc;">
                                    <strong>Note:</strong>
                                    These are system driven activities, that you can take up any time.
                                </p>
                            </div>
                        </div>
                        <%-- System.out.println("strOrg == >"+request.getAttribute("f_org")); --%>
                    </div>
                    
                    <div class="box box-none nav-tabs-custom">
		            	<ul class="nav nav-tabs">
							<%=((empType == null || empType.equalsIgnoreCase("I")) ? "<li class='active'>" : "<li>") %><a href="javascript:void(0)" onclick='window.location="EmployeeActivity.action?empType=I&f_org=<%=request.getAttribute("f_org")%>"' data-toggle="tab">Induction <span class="sk_value" style="font-size: 15px;"><%=uF.showData(totalInduction,"") %></span></a></li>
                       		<%=((empType != null && empType.equalsIgnoreCase("C")) ? "<li class='active'>" : "<li>") %><a href="javascript:void(0)" onclick='window.location="EmployeeActivity.action?empType=C&f_org=<%=request.getAttribute("f_org")%>"' data-toggle="tab">Confirmation <span class="sk_value" style="font-size: 15px;"><%=uF.showData(totalConfirmation,"") %></span></a></li>
                       		<%=((empType != null && empType.equalsIgnoreCase("RETIRE")) ? "<li class='active'>" : "<li>") %><a href="javascript:void(0)" onclick='window.location="EmployeeActivity.action?empType=RETIRE&f_org=<%=request.getAttribute("f_org")%>"' data-toggle="tab">Retirement <span class="sk_value" style="font-size: 15px;"><%=uF.showData(totalRetirements,"")%></span></a></li>
                       		<%=((empType != null && empType.equalsIgnoreCase("R")) ? "<li class='active'>" : "<li>") %><a href="javascript:void(0)" onclick='window.location="EmployeeActivity.action?empType=R&f_org=<%=request.getAttribute("f_org")%>"' data-toggle="tab">Resignation <span class="sk_value" style="font-size: 15px;"><%=uF.showData(totalResignation,"")%></span></a></li>
                       		<%=((empType != null && empType.equalsIgnoreCase("FD")) ? "<li class='active'>" : "<li>") %><a href="javascript:void(0)" onclick='window.location="EmployeeActivity.action?empType=FD&f_org=<%=request.getAttribute("f_org")%>"' data-toggle="tab">Final Day <span class="sk_value" style="font-size: 15px;"><%=uF.showData(totalFD,"")%></span></a></li>
						</ul> 
					
                    <%if(empType==null || empType.equals("I")) { %>
                    <div class="induction_div tab-content">
                        <% 
                            Map<String,List<String>> hmTodaysInduction = (Map<String,List<String>>)request.getAttribute("todaysInduction");
                            Map<String,List<String>> hmTomorrowInduction = (Map<String,List<String>>)request.getAttribute("tomorrowInduction");
                            Map<String,List<String>> hmDayAfterTomorrowInduction = (Map<String,List<String>>)request.getAttribute("dayAfterTomorrowInduction");
                            Map<String,List<String>> hmPendingInduction = (Map<String,List<String>>)request.getAttribute("pendingInduction");
                            %>
                        <div style="float:left;width:100%">
                            <%
                                int todayInd = 0;
                                if(hmTodaysInduction != null && !hmTodaysInduction.isEmpty() && hmTodaysInduction.size()>0){
                                	todayInd = hmTodaysInduction.size();
                                }
                                
                                %>
                            <div style="margin: 10px 0px 0px 0px; float: left; width: 100%; line-height:30px;">
                                <div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE;">
                                    <div class="box-header with-border">
                                        <h3 class="box-title" style="font-size: 14px;">Today(<%=todayInd%>)</h3>
                                        <div class="box-tools pull-right">
                                            <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
                                            <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                                        </div>
                                    </div>
                                    <!-- /.box-header -->
                                    <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
                                        <div class="inductionContent">
                                            <div class="attendance">
                                                <% 
                                                    if(hmTodaysInduction != null && !hmTodaysInduction.isEmpty() && hmTodaysInduction.size()>0) {
                                                    	Set todayIndIdSet = hmTodaysInduction.keySet();
                                                    	Iterator<String> it = todayIndIdSet.iterator();
                                                    %>
                                                <table class="table table-bordered" width="100%" cellspacing="0" cellpadding="2" align="left" style="margin-bottom :15px;">
                                                    <tr class="darktable">
                                                        <td style="text-align: center;">Name</td>
                                                        <td style="text-align: center;">Designation</td>
                                                        <td style="text-align: center;">Work Location</td>
                                                        <td style="text-align: center;">Reporting Manager </td>
                                                        <td style="text-align: center;">Joining Date</td>
                                                        <td style="text-align: center;">Action- Onboarding Form</td>
                                                    </tr>
                                                    <%
                                                        while(it.hasNext()){
                                                        	String empId = it.next();
                                                        	List<String> alInductionToday = hmTodaysInduction.get(empId);
                                                        	if(alInductionToday == null) alInductionToday = new ArrayList<String>();
                                                        	if(alInductionToday!=null && alInductionToday.size()>0 && !alInductionToday.isEmpty()){
                                                        %>
                                                    <tr>
                                                        <td style="text-align: left; vertical-align: text-top;">
                                                            <script type="text/javascript">
                                                                $(function() {
                                                                $('#starPrimaryS'+'<%=empId %>').raty({
                                                                      readOnly: true,
                                                                      start:1.0 ,
                                                                      half: true,
                                                                      targetType: 'number'
                                                                });
                                                                  });
                                                            </script>
                                                            <div style="float: left; margin: 2px 10px 0px 0px;">
                                                                <!-- border: 1px solid #000; -->
                                                                <%if(docRetriveLocation == null) { %>
                                                                <img height="40" width="40" border="0" class="lazy img-circle" id="profilecontainerimg<%=empId%>" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + alInductionToday.get(3)%>" />
                                                                <%} else { %>
                                                                <img height="40" width="40" border="0" class="lazy img-circle" id="profilecontainerimg<%=empId%>" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+empId+"/"+IConstants.I_60x60+"/"+alInductionToday.get(3)%>" />
                                                                <%} %> 
                                                            </div>
                                                            <div style="float: left;">
                                                            <!-- Created by Dattatray Date : 21-07-21 Note: empId encrypt -->
                                                                <a href="javascript:void(0);" onclick="openEmpProfilePopup('<%=empId%>')"> <%=alInductionToday.get(0)%> </a>
                                                                <br/>
                                                                <div style="font-size: 11px; line-height: 22px;"><strong style="color: gray;">Added By:</strong>&nbsp;<%=uF.showData(alInductionToday.get(2),"-") %></div>
                                                            </div>
                                                        </td>
                                                        <td><%=alInductionToday.get(7)%></td>
                                                        <td><%=alInductionToday.get(5)%></td>
                                                        <td><%=alInductionToday.get(8)%></td>
                                                        <td><%=alInductionToday.get(1)%></td>
                                                        <td>
                                                            <div style="float:left;width:98%;">
                                                            <a class="fa fa-trash" style="margin-left: 4px;" href="javascript:void(0)" onclick="(confirm('Are you sure, you want to delete this employee?') ? window.location='AddEmployeeInOneStep.action?pageType=PE&operation=D&empId=<%=empId %>' : '')"> </a>
                                                            <%=alInductionToday.get(4)%></div>
                                                            <div style="float:left;width:98%;"><%=alInductionToday.get(9)%></div>
                                                        </td>
                                                    </tr>
                                                    <%			
                                                        }
                                                        }
                                                        %>
                                                </table>
                                                <%
                                                    }else{%>
                                                <div class="nodata msg"><span>No Inductions For Today</span></div>
                                                <% }%>
                                            </div>
                                        </div>
                                    </div>
                                    <!-- /.box-body -->
                                </div>
                            </div>
                            <div style="margin: 10px 0px 0px 0px; float: left; width: 100%; line-height:30px;">
                                <%
                                    int tommInd = 0;
                                    if(hmTomorrowInduction != null && !hmTomorrowInduction.isEmpty() && hmTomorrowInduction.size()>0){
                                    	tommInd = hmTomorrowInduction.size();
                                    }
                                    
                                    %>
                                <div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE;">
                                    <div class="box-header with-border">
                                        <h3 class="box-title" style="font-size: 14px;">Tomorrow(<%=tommInd%>)</h3>
                                        <div class="box-tools pull-right">
                                            <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
                                            <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                                        </div>
                                    </div>
                                    <!-- /.box-header -->
                                    <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
                                        <div class="inductionContent">
                                            <div class="attendance">
                                                <%
                                                    if(hmTomorrowInduction != null && !hmTomorrowInduction.isEmpty() && hmTomorrowInduction.size()>0) {
                                                    	Set indTommIdSet = hmTomorrowInduction.keySet();
                                                    	Iterator<String> it = indTommIdSet.iterator();
                                                    %>
                                                <table class="table table-bordered" width="100%" cellspacing="0" cellpadding="2" align="left" style="margin-bottom :15px;">
                                                    <!-- class="display tb_style" -->
                                                    <tbody>
                                                        <tr class="darktable">
                                                            <td style="text-align: center;">Name</td>
                                                            <td style="text-align: center;">Designation</td>
                                                            <td style="text-align: center;">Work Location</td>
                                                            <td style="text-align: center;">Reporting Manager </td>
                                                            <td style="text-align: center;">Joining Date</td>
                                                            <td style="text-align: center;">Action- Onboarding Form</td>
                                                        </tr>
                                                        <%
                                                            while(it.hasNext()){
                                                            	String empId = it.next();
                                                            	List<String> indTomorrow = hmTomorrowInduction.get(empId);
                                                            	if(indTomorrow == null) indTomorrow = new ArrayList<String>();
                                                            	if(indTomorrow!=null && indTomorrow.size()>0 && !indTomorrow.isEmpty()){
                                                            %>
                                                        <tr>
                                                            <td style="text-align: left; vertical-align: text-top;">
                                                                <script type="text/javascript">
                                                                    $(function() {
                                                                    $('#starPrimaryS'+'<%=empId %>').raty({
                                                                          readOnly: true,
                                                                          start:1.0 ,
                                                                          half: true,
                                                                          targetType: 'number'
                                                                    });
                                                                      });
                                                                </script>
                                                                <div style="float: left; margin: 2px 10px 0px 0px;">
                                                                    <!-- border: 1px solid #000; -->
                                                                    <%if(docRetriveLocation == null) { %>
                                                                    <img height="40" width="40" border="0" class="lazy img-circle" id="profilecontainerimg<%=empId%>" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + indTomorrow.get(3)%>" />
                                                                    <%} else { %>
                                                                    <img height="40" width="40" border="0" class="lazy img-circle" id="profilecontainerimg<%=empId%>" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+empId+"/"+IConstants.I_60x60+"/"+indTomorrow.get(3)%>" />
                                                                    <%} %> 
                                                                </div>
                                                                <div style="float: left;">
                                                                <!-- Created by Dattatray Date : 21-07-21 Note: empId encrypt -->
                                                                    <a href="javascript:void(0);" onclick="openEmpProfilePopup('<%=empId%>')"> <%=indTomorrow.get(0)%> </a>
                                                                    <br/>
                                                                    <div style="font-size: 11px; line-height: 22px;"><strong style="color: gray;">Added By:</strong>&nbsp;<%=uF.showData(indTomorrow.get(2),"-") %></div>
                                                                </div>
                                                            </td>
                                                            <td><%=indTomorrow.get(7)%></td>
                                                            <td><%=indTomorrow.get(5)%></td>
                                                            <td><%=indTomorrow.get(8)%></td>
                                                            <td><%=indTomorrow.get(1)%></td>
                                                            <td>
                                                                <div style="float:left;width:98%;">
                                                                <a class="fa fa-trash" style="margin-left: 4px;" href="javascript:void(0)" onclick="(confirm('Are you sure, you want to delete this employee?') ? window.location='AddEmployeeInOneStep.action?pageType=PE&operation=D&empId=<%=empId %>' : '')"> </a>
                                                                <%=indTomorrow.get(4)%></div>
                                                                <div style="float:left; width: 98%;"><%=indTomorrow.get(9)%></div>
                                                            </td>
                                                        </tr>
                                                        <%			
                                                            }
                                                            }
                                                            %>
                                                    </tbody>
                                                </table>
                                                <%
                                                    }else{
                                                    %>
                                                <div class="nodata msg"><span>No Inductions For Tomorrow</span></div>
                                                <% }%>
                                            </div>
                                        </div>
                                    </div>
                                    <!-- /.box-body -->
                                </div>
                            </div>
                            <div style="margin: 10px 0px 0px 0px; float: left; width: 100%; line-height:30px;">
                                <%
                                    int datInd = 0;
                                    if(hmDayAfterTomorrowInduction != null && !hmDayAfterTomorrowInduction.isEmpty() && hmDayAfterTomorrowInduction.size()>0){
                                    	datInd = hmDayAfterTomorrowInduction.size();
                                    }
                                    
                                    %>
                                <div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE;">
                                    <div class="box-header with-border">
                                        <h3 class="box-title" style="font-size: 14px;">Day After Tomorrow(<%=datInd%>)</h3>
                                        <div class="box-tools pull-right">
                                            <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
                                            <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                                        </div>
                                    </div>
                                    <!-- /.box-header -->
                                    <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
                                        <div class="inductionContent">
                                            <div class="attendance">
                                                <%
                                                    if(hmDayAfterTomorrowInduction != null && !hmDayAfterTomorrowInduction.isEmpty() && hmDayAfterTomorrowInduction.size()>0) {
                                                    	Set indDATIdSet = hmDayAfterTomorrowInduction.keySet();
                                                    	Iterator<String> it = indDATIdSet.iterator();
                                                    %>
                                                <table class="table table-bordered" width="100%" cellspacing="0" cellpadding="2" align="left" style="margin-bottom :15px;">
                                                    <!-- class="display tb_style" -->
                                                    <tbody>
                                                        <tr class="darktable">
                                                            <td style="text-align: center;">Name</td>
                                                            <td style="text-align: center;">Designation</td>
                                                            <td style="text-align: center;">Work Location</td>
                                                            <td style="text-align: center;">Reporting Manager </td>
                                                            <td style="text-align: center;">Joining Date</td>
                                                            <td style="text-align: center;">Action- Onboarding Form</td>
                                                        </tr>
                                                        <%
                                                            while(it.hasNext()){
                                                            	String empId = it.next();
                                                            	List<String>  alInnerDAT= hmDayAfterTomorrowInduction.get(empId);
                                                            	if(alInnerDAT == null) alInnerDAT = new ArrayList<String>();
                                                            	if(alInnerDAT!=null && alInnerDAT.size()>0 && !alInnerDAT.isEmpty()){
                                                            %>
                                                        <tr>
                                                            <td style="text-align: left; vertical-align: text-top;">
                                                                <script type="text/javascript">
                                                                    $(function() {
                                                                    $('#starPrimaryS'+'<%=empId %>').raty({
                                                                          readOnly: true,
                                                                          start:1.0 ,
                                                                          half: true,
                                                                          targetType: 'number'
                                                                    });
                                                                      });
                                                                </script>
                                                                <div style="float: left; margin: 2px 10px 0px 0px;">
                                                                    <!-- border: 1px solid #000; -->
                                                                    <%if(docRetriveLocation == null) { %>
                                                                    <img height="40" width="40" border="0" class="lazy img-circle" id="profilecontainerimg<%=empId%>" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + alInnerDAT.get(3)%>" />
                                                                    <%} else { %>
                                                                    <img height="40" width="40" border="0" class="lazy img-circle" id="profilecontainerimg<%=empId%>" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+empId+"/"+IConstants.I_60x60+"/"+alInnerDAT.get(3)%>" />
                                                                    <%} %> 
                                                                </div>
                                                                <div style="float: left;">
                                                                <!-- Created by Dattatray Date : 21-07-21 Note: empId encrypt -->
                                                                    <a href="javascript:void(0);" onclick="openEmpProfilePopup('<%=empId%>')"> <%=alInnerDAT.get(0)%> </a>
                                                                    <br/>
                                                                    <div style="font-size: 11px; line-height: 22px;"><strong style="color: gray;">Added By:</strong>&nbsp;<%=uF.showData(alInnerDAT.get(2),"-") %></div>
                                                                </div>
                                                            </td>
                                                            <td><%=alInnerDAT.get(7)%></td>
                                                            <td><%=alInnerDAT.get(5)%></td>
                                                            <td><%=alInnerDAT.get(8)%></td>
                                                            <td><%=alInnerDAT.get(1)%></td>
                                                            <td>
                                                                <div style="float:left;width:98%;">
                                                                <a class="fa fa-trash" style="margin-left: 4px;" href="javascript:void(0)" onclick="(confirm('Are you sure, you want to delete this employee?') ? window.location='AddEmployeeInOneStep.action?pageType=PE&operation=D&empId=<%=empId %>' : '')"> </a>
                                                                <%=alInnerDAT.get(4)%></div>
                                                                <div style="float:left;width:98%;"><%=alInnerDAT.get(9)%></div>
                                                            </td>
                                                        </tr>
                                                        <%			
                                                            }
                                                            }
                                                            %>
                                                    </tbody>
                                                </table>
                                                <%		
                                                    }else{
                                                    %>
                                                <div class="nodata msg"><span>No Future Inductions</span></div>
                                                <% }%>
                                            </div>
                                        </div>
                                    </div>
                                    <!-- /.box-body -->
                                </div>
                            </div>
                            <div style="margin: 10px 0px 0px 0px; float: left; width: 100%; line-height:30px;">
                                <%
                                    int pendingInd = 0;
                                    if(hmPendingInduction != null && !hmPendingInduction.isEmpty() && hmPendingInduction.size()>0){
                                    	pendingInd = hmPendingInduction.size();
                                    }
                                    
                                    %>
                                <div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE;">
                                    <div class="box-header with-border">
                                        <h3 class="box-title" style="font-size: 14px;">Pending(<%=pendingInd%>)</h3>
                                        <div class="box-tools pull-right">
                                            <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
                                            <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                                        </div>
                                    </div>
                                    <!-- /.box-header -->
                                    <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
                                        <div class="inductionContent">
                                            <div class="attendance">
                                                <%
                                                    if(hmPendingInduction != null && !hmPendingInduction.isEmpty() && hmPendingInduction.size()>0) {
                                                    	Set indPendingSet= hmPendingInduction.keySet();
                                                    	Iterator<String> it = indPendingSet.iterator();
                                                    %>
                                                <table class="table table-bordered" width="100%" cellspacing="0" cellpadding="2" align="left" style="margin-bottom :15px;">
                                                    <!-- class="display tb_style" -->
                                                    <tbody>
                                                        <tr class="darktable" style="font-weight: 700;background-color: aliceblue;
                                                        ">
                                                            <td style="text-align: center;">Name</td>
                                                            <td style="text-align: center;">Designation</td>
                                                            <td style="text-align: center;">Work Location</td>
                                                            <td style="text-align: center;">Reporting Manager </td>
                                                            <td style="text-align: center;">Joining Date</td>
                                                            <td style="text-align: center;">Action- Onboarding Form</td>
                                                        </tr>
                                                        <%	
                                                            while(it.hasNext()){
                                                            	String empId = it.next();
                                                            	List<String> inductionPending = hmPendingInduction.get(empId);
                                                            	if(inductionPending == null) inductionPending = new ArrayList<String>();
                                                            	if(inductionPending!=null && inductionPending.size()>0 && !inductionPending.isEmpty()){
                                                            %>
                                                        <tr>
                                                            <td style="text-align: left; vertical-align: text-top;">
                                                                <script type="text/javascript">
                                                                    $(function() {
                                                                    $('#starPrimaryS'+'<%=empId %>').raty({
                                                                          readOnly: true,
                                                                          start:1.0 ,
                                                                          half: true,
                                                                          targetType: 'number'
                                                                    });
                                                                      });
                                                                </script>
                                                                <div style="float: left; margin: 2px 10px 0px 0px;">
                                                                    <!-- border: 1px solid #000; -->
                                                                    <%if(docRetriveLocation == null) { %>
                                                                    <img height="40" width="40" border="0" class="lazy img-circle" id="profilecontainerimg<%=empId%>" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + inductionPending.get(3)%>" />
                                                                    <%} else { %>
                                                                    <img height="40" width="40" border="0" class="lazy img-circle" id="profilecontainerimg<%=empId%>" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+empId+"/"+IConstants.I_60x60+"/"+inductionPending.get(3)%>" />
                                                                    <%} %> 
                                                                </div>
                                                                <div style="float: left;">
                                                                <!-- Created by Dattatray Date : 21-07-21 Note: empId encrypt -->
                                                                    <a href="javascript:void(0);" onclick="openEmpProfilePopup('<%=empId%>')"> <%=inductionPending.get(0)%> </a>
                                                                    <br/>
                                                                    <div style="font-size: 11px; line-height: 22px;"><strong style="color: gray;">Added By:</strong>&nbsp;<%=uF.showData(inductionPending.get(2),"-") %></div>
                                                                </div>
                                                            </td>
                                                            <td><%=inductionPending.get(7)%></td>
                                                            <td><%=inductionPending.get(5)%></td>
                                                            <td><%=inductionPending.get(8)%></td>
                                                            <td><%=inductionPending.get(1)%></td>
                                                            <td>
                                                                <div style="float:left;width:98%;">
                                                                <a class="fa fa-trash" style="margin-left: 4px;" href="javascript:void(0)" onclick="(confirm('Are you sure, you want to delete this employee?') ? window.location='AddEmployeeInOneStep.action?pageType=PE&operation=D&empId=<%=empId %>' : '')"> </a>
                                                                <%=inductionPending.get(4)%></div>
                                                                <div style="float:left;width:98%;"><%=inductionPending.get(9)%></div>
                                                            </td>
                                                        </tr>
                                                        <%			
                                                            }
                                                            }
                                                            %>
                                                    </tbody>
                                                </table>
                                                <%
                                                    }else{
                                                    %>
                                                <div class="nodata msg"><span>No Pending Inductions</span></div>
                                                <% }%>
                                            </div>
                                        </div>
                                    </div>
                                    <!-- /.box-body -->
                                </div>
                            </div>
                        </div>
                    </div>
                    <% } else if(empType!=null && empType.equals("C")) { %>
                    <div class="induction_div  tab-content">
                        <% 
                            Map<String,List<String>> hmTodaysConf = (Map<String,List<String>>)request.getAttribute("todaysConfirmation");
                            Map<String,List<String>> hmTomorrowConf = (Map<String,List<String>>)request.getAttribute("tomorrowConfirmation");
                            Map<String,List<String>> hmDayAfterTomorrowConf = (Map<String,List<String>>)request.getAttribute("dayAfterTomorrowConfirmation");
                            Map<String,List<String>> hmPendingConf = (Map<String,List<String>>)request.getAttribute("pendingConfirmation");
                            %>
                        <div style="float:left;width:100%">
                            <%
                                int todayConf = 0;
                                if(hmTodaysConf != null && !hmTodaysConf.isEmpty() && hmTodaysConf.size()>0){
                                	todayConf = hmTodaysConf.size();
                                }
                                
                                %>
                            <div style="margin: 10px 0px 0px 0px; float: left; width: 100%; line-height:30px;">
                                <div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE;">
                                    <div class="box-header with-border">
                                        <h3 class="box-title" style="font-size: 14px;">Today(<%=todayConf%>)</h3>
                                        <div class="box-tools pull-right">
                                            <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
                                            <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                                        </div>
                                    </div>
                                    <!-- /.box-header -->
                                    <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
                                        <div class="inductionContent">
                                            <div class="attendance">
                                                <%
                                                    if(hmTodaysConf != null && !hmTodaysConf.isEmpty() && hmTodaysConf.size()>0) {
                                                    	Set todayConfIdSet = hmTodaysConf.keySet();
                                                    	Iterator<String> it = todayConfIdSet.iterator();
                                                    %>
                                                <table class="table table-bordered" width="100%" cellspacing="0" cellpadding="2" align="left" style="margin-bottom :15px;">
                                                    <!-- class="display tb_style" -->
                                                    <tbody>
                                                        <tr class="darktable">
                                                            <td style="text-align: center;">Name</td>
                                                            <td style="text-align: center;">Designation</td>
                                                            <td style="text-align: center;">Work Location</td>
                                                            <td style="text-align: center;">Department</td>
                                                            <td style="text-align: center;">Reporting Manager </td>
                                                            <td style="text-align: center;">Confirmation Date</td>
                                                            <td style="text-align: center;">Action- Take Action</td>
                                                        </tr>
                                                        <%
                                                            while(it.hasNext()){
                                                            	String empId = it.next();
                                                            	List<String> alConfToday = hmTodaysConf.get(empId);
                                                            	if(alConfToday == null) alConfToday = new ArrayList<String>();
                                                            	if(alConfToday!=null && alConfToday.size()>0 && !alConfToday.isEmpty()){
                                                            %>
                                                        <tr>
                                                            <td style="text-align: left; vertical-align: text-top;">
                                                                <script type="text/javascript">
                                                                    $(function() {
                                                                    $('#starPrimaryS'+'<%=empId %>').raty({
                                                                          readOnly: true,
                                                                          start:1.0 ,
                                                                          half: true,
                                                                          targetType: 'number'
                                                                    });
                                                                      });
                                                                </script>
                                                                <div style="float: left; margin: 2px 10px 0px 0px;">
                                                                    <!-- border: 1px solid #000; -->
                                                                    <%if(docRetriveLocation == null) { %>
                                                                    <img height="40" width="40" border="0" class="lazy img-circle" id="profilecontainerimg<%=empId%>" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + alConfToday.get(5)%>" />
                                                                    <%} else { %>
                                                                    <img height="40" width="40" border="0" class="lazy img-circle" id="profilecontainerimg<%=empId%>" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+empId+"/"+IConstants.I_60x60+"/"+alConfToday.get(5)%>" />
                                                                    <%} %> 
                                                                </div>
                                                                <div style="float: left;">
                                                                <!-- Created by Dattatray Date : 21-07-21 Note: empId encrypt -->
                                                                    <a href="javascript:void(0);" onclick="openEmpProfilePopup('<%=empId%>')"> <%=alConfToday.get(1)%> </a>
                                                                    <br/>
                                                                    <div style="font-size: 11px; line-height: 22px;"><strong style="color: gray;">Added By:</strong>&nbsp;<%=uF.showData(alConfToday.get(4),"-") %></div>
                                                                </div>
                                                            </td>
                                                            <td><%=alConfToday.get(8)%></td>
                                                            <td><%=alConfToday.get(6)%></td>
                                                            <td><%=alConfToday.get(7)%></td>
                                                            <td><%=alConfToday.get(9)%></td>
                                                            <td><%=alConfToday.get(10)%></td>
                                                            <td><%=alConfToday.get(11)%></td>
                                                        </tr>
                                                        <%			
                                                            }
                                                            }
                                                            %>
                                                    </tbody>
                                                </table>
                                                <%
                                                    }else{
                                                    %>
                                                <div class="nodata msg"><span>No Confirmations For Today</span></div>
                                                <% }%>
                                            </div>
                                        </div>
                                    </div>
                                    <!-- /.box-body -->
                                </div>
                                <div style="margin: 10px 0px 0px 0px; float: left; width: 100%; line-height:30px;">
                                    <%
                                        int tommConf = 0;
                                        if(hmTomorrowConf != null && !hmTomorrowConf.isEmpty() && hmTomorrowConf.size()>0){
                                        	tommConf = hmTomorrowConf.size();
                                        }
                                        
                                        %>
                                    <div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE;">
                                        <div class="box-header with-border">
                                            <h3 class="box-title" style="font-size: 14px;">Tomorrow(<%=tommConf%>)</h3>
                                            <div class="box-tools pull-right">
                                                <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
                                                <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                                            </div>
                                        </div>
                                        <!-- /.box-header -->
                                        <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
                                            <div class="inductionContent">
                                                <div class="attendance">
                                                    <%
                                                        if(hmTomorrowConf != null && !hmTomorrowConf.isEmpty() && hmTomorrowConf.size()>0) {
                                                        	Set confTommIdSet = hmTomorrowConf.keySet();
                                                        	Iterator<String> it = confTommIdSet.iterator();
                                                        %>
                                                    <table class="table table-bordered" width="100%" cellspacing="0" cellpadding="2" align="left" style="margin-bottom :15px;">
                                                        <!-- class="display tb_style" -->
                                                        <tbody>
                                                            <tr class="darktable">
                                                                <td style="text-align: center;">Name</td>
                                                                <td style="text-align: center;">Designation</td>
                                                                <td style="text-align: center;">Work Location</td>
                                                                <td style="text-align: center;">Department</td>
                                                                <td style="text-align: center;">Reporting Manager </td>
                                                                <td style="text-align: center;">Confirmation Date</td>
                                                                <td style="text-align: center;">Action- Take Action</td>
                                                            </tr>
                                                            <%
                                                                while(it.hasNext()){
                                                                	String empId = it.next();
                                                                	List<String> confTomorrow = hmTomorrowConf.get(empId);
                                                                	if(confTomorrow == null) confTomorrow = new ArrayList<String>();
                                                                	if(confTomorrow!=null && confTomorrow.size()>0 && !confTomorrow.isEmpty()){
                                                                %>
                                                            <tr>
                                                                <td style="text-align: left; vertical-align: text-top;">
                                                                    <script type="text/javascript">
                                                                        $(function() {
                                                                        $('#starPrimaryS'+'<%=empId %>').raty({
                                                                              readOnly: true,
                                                                              start:1.0 ,
                                                                              half: true,
                                                                              targetType: 'number'
                                                                        });
                                                                          });
                                                                    </script>
                                                                    <div style="float: left; margin: 2px 10px 0px 0px;">
                                                                        <!-- border: 1px solid #000; -->
                                                                        <%if(docRetriveLocation == null) { %>
                                                                        <img height="40" width="40" border="0" class="lazy img-circle" id="profilecontainerimg<%=empId%>" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + confTomorrow.get(5)%>" />
                                                                        <%} else { %>
                                                                        <img height="40" width="40" border="0" class="lazy img-circle" id="profilecontainerimg<%=empId%>" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+empId+"/"+IConstants.I_60x60+"/"+confTomorrow.get(5)%>" />
                                                                        <%} %> 
                                                                    </div>
                                                                    <div style="float: left;">
                                                                    <!-- Created by Dattatray Date : 21-07-21 Note: empId encrypt -->
                                                                        <a href="javascript:void(0);" onclick="openEmpProfilePopup('<%=empId%>')"> <%=confTomorrow.get(1)%> </a>
                                                                        <br/>
                                                                        <div style="font-size: 11px; line-height: 22px;"><strong style="color: gray;">Added By:</strong>&nbsp;<%=uF.showData(confTomorrow.get(4),"-") %></div>
                                                                    </div>
                                                                </td>
                                                                <td><%=confTomorrow.get(8)%></td>
                                                                <td><%=confTomorrow.get(6)%></td>
                                                                <td><%=confTomorrow.get(7)%></td>
                                                                <td><%=confTomorrow.get(9)%></td>
                                                                <td><%=confTomorrow.get(10)%></td>
                                                                <td><%=confTomorrow.get(11)%></td>
                                                            </tr>
                                                            <%			
                                                                }
                                                                }
                                                                %>
                                                        </tbody>
                                                    </table>
                                                    <%
                                                        }else{
                                                        %>
                                                    <div class="nodata msg"><span>No Confirmations For Tomorrow</span></div>
                                                    <% }%>
                                                </div>
                                            </div>
                                        </div>
                                        <!-- /.box-body -->
                                    </div>
                                </div>
                                <div style="margin: 10px 0px 0px 0px; float: left; width: 100%; line-height:30px;">
                                    <%
                                        int datConf = 0;
                                        if(hmDayAfterTomorrowConf != null && !hmDayAfterTomorrowConf.isEmpty() && hmDayAfterTomorrowConf.size()>0){
                                        datConf = hmDayAfterTomorrowConf.size();
                                        }
                                        
                                        %>
                                    <p class="heading_dash" style="padding-left: 60px;">
                                        <span style="width: 100%;"><b></b></span>
                                    </p>
                                    <div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE;">
                                        <div class="box-header with-border">
                                            <h3 class="box-title" style="font-size: 14px;">Day After Tomorrow(<%=datConf%>)</h3>
                                            <div class="box-tools pull-right">
                                                <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
                                                <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                                            </div>
                                        </div>
                                        <!-- /.box-header -->
                                        <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
                                            <div class="inductionContent">
                                                <div class="attendance">
                                                    <%
                                                        if(hmDayAfterTomorrowConf != null && !hmDayAfterTomorrowConf.isEmpty() && hmDayAfterTomorrowConf.size()>0) {
                                                        	Set confDATIdSet = hmDayAfterTomorrowConf.keySet();
                                                        	Iterator<String> it = confDATIdSet.iterator();
                                                        %>
                                                    <table class="table table-bordered" width="100%" cellspacing="0" cellpadding="2" align="left" style="margin-bottom :15px;">
                                                        <!-- class="display tb_style" -->
                                                        <tbody>
                                                            <tr class="darktable">
                                                                <td style="text-align: center;">Name</td>
                                                                <td style="text-align: center;">Designation</td>
                                                                <td style="text-align: center;">Work Location</td>
                                                                <td style="text-align: center;">Department</td>
                                                                <td style="text-align: center;">Reporting Manager </td>
                                                                <td style="text-align: center;">Confirmation Date</td>
                                                                <td style="text-align: center;">Action- Take Action</td>
                                                            </tr>
                                                            <%
                                                                while(it.hasNext()){
                                                                	String empId = it.next();
                                                                	List<String>  alInnerDAT= hmDayAfterTomorrowConf.get(empId);
                                                                	if(alInnerDAT == null) alInnerDAT = new ArrayList<String>();
                                                                	if(alInnerDAT!=null && alInnerDAT.size()>0 && !alInnerDAT.isEmpty()){
                                                                %>
                                                            <tr>
                                                                <td style="text-align: left; vertical-align: text-top;">
                                                                    <script type="text/javascript">
                                                                        $(function() {
                                                                        $('#starPrimaryS'+'<%=empId %>').raty({
                                                                              readOnly: true,
                                                                              start:1.0 ,
                                                                              half: true,
                                                                              targetType: 'number'
                                                                        });
                                                                          });
                                                                    </script>
                                                                    <div style="float: left; margin: 2px 10px 0px 0px;">
                                                                        <!-- border: 1px solid #000; -->
                                                                        <%if(docRetriveLocation == null) { %>
                                                                        <img height="40" width="40" border="0" class="lazy img-circle" id="profilecontainerimg<%=empId%>" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + alInnerDAT.get(5)%>" />
                                                                        <%} else { %>
                                                                        <img height="40" width="40" border="0" class="lazy img-circle" id="profilecontainerimg<%=empId%>" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+empId+"/"+IConstants.I_60x60+"/"+alInnerDAT.get(5)%>" />
                                                                        <%} %> 
                                                                    </div>
                                                                    <div style="float: left;">
                                                                    <!-- Created by Dattatray Date : 21-07-21 Note: empId encrypt -->
                                                                        <a href="javascript:void(0);" onclick="openEmpProfilePopup('<%=empId%>')"> <%=alInnerDAT.get(1)%> </a>
                                                                        <br/>
                                                                        <div style="font-size: 11px; line-height: 22px;"><strong style="color: gray;">Added By:</strong>&nbsp;<%=uF.showData(alInnerDAT.get(4),"-") %></div>
                                                                    </div>
                                                                </td>
                                                                <td><%=alInnerDAT.get(8)%></td>
                                                                <td><%=alInnerDAT.get(6)%></td>
                                                                <td><%=alInnerDAT.get(7)%></td>
                                                                <td><%=alInnerDAT.get(9)%></td>
                                                                <td><%=alInnerDAT.get(10)%></td>
                                                                <td><%=alInnerDAT.get(11)%></td>
                                                            </tr>
                                                            <%			
                                                                }
                                                                }
                                                                %>
                                                        </tbody>
                                                    </table>
                                                    <%		
                                                        }else{
                                                        %>
                                                    <div class="nodata msg"><span>No Future Confirmations</span></div>
                                                    <% }%>
                                                </div>
                                            </div>
                                        </div>
                                        <!-- /.box-body -->
                                    </div>
                                </div>
                                <div style="margin: 10px 0px 0px 0px; float: left; width: 100%; line-height:30px;">
                                    <%
                                        int pendingConf = 0;
                                        if(hmPendingConf != null && !hmPendingConf.isEmpty() && hmPendingConf.size()>0){
                                        pendingConf = hmPendingConf.size();
                                        }
                                        
                                        %>
                                    <div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE;">
                                        <div class="box-header with-border">
                                            <h3 class="box-title" style="font-size: 14px;">Pending(<%=pendingConf%>)</h3>
                                            <div class="box-tools pull-right">
                                                <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
                                                <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                                            </div>
                                        </div>
                                        <!-- /.box-header -->
                                        <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
                                            <div class="inductionContent">
                                                <div class="attendance">
                                                    <%
                                                        if(hmPendingConf != null && !hmPendingConf.isEmpty() && hmPendingConf.size()>0) {
                                                        	Set pendingConfSet= hmPendingConf.keySet();
                                                        	Iterator<String> it = pendingConfSet.iterator();
                                                        %>
                                                    <table class="table table-bordered" width="100%" cellspacing="0" cellpadding="2" align="left" style="margin-bottom :15px;">
                                                        <!-- class="display tb_style" -->
                                                        <tbody>
                                                            <tr class="darktable">
                                                                <td style="text-align: center;">Name</td>
                                                                <td style="text-align: center;">Designation</td>
                                                                <td style="text-align: center;">Work Location</td>
                                                                <td style="text-align: center;">Department</td>
                                                                <td style="text-align: center;">Reporting Manager </td>
                                                                <td style="text-align: center;">Confirmation Date</td>
                                                                <td style="text-align: center;">Action- Take Action</td>
                                                            </tr>
                                                            <%	
                                                                while(it.hasNext()){
                                                                	String empId = it.next();
                                                                	List<String> confPending = hmPendingConf.get(empId);
                                                                	if(confPending == null) confPending = new ArrayList<String>();
                                                                	if(confPending!=null && confPending.size()>0 && !confPending.isEmpty()){
                                                                %>
                                                            <tr>
                                                                <td style="text-align: left; vertical-align: text-top;">
                                                                    <script type="text/javascript">
                                                                        $(function() {
                                                                        $('#starPrimaryS'+'<%=empId %>').raty({
                                                                              readOnly: true,
                                                                              start:1.0 ,
                                                                              half: true,
                                                                              targetType: 'number'
                                                                        });
                                                                          });
                                                                    </script>
                                                                    <div style="float: left; margin: 2px 10px 0px 0px;">
                                                                        <!-- border: 1px solid #000; -->
                                                                        <%if(docRetriveLocation == null) { %>
                                                                        <img height="40" width="40" border="0" class="lazy img-circle" id="profilecontainerimg<%=empId%>" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + confPending.get(5)%>" />
                                                                        <%} else { %>
                                                                        <img height="40" width="40" border="0" class="lazy img-circle" id="profilecontainerimg<%=empId%>" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+empId+"/"+IConstants.I_60x60+"/"+confPending.get(5)%>" />
                                                                        <%} %> 
                                                                    </div>
                                                                    <div style="float: left;">
                                                                    <!-- Created by Dattatray Date : 21-07-21 Note: empId encrypt -->
                                                                        <a href="javascript:void(0);" onclick="openEmpProfilePopup('<%=empId%>')"> <%=confPending.get(1)%> </a>
                                                                        <br/>
                                                                        <div style="font-size: 11px; line-height: 22px;"><strong style="color: gray;">Added By:</strong>&nbsp;<%=uF.showData(confPending.get(4),"-") %></div>
                                                                    </div>
                                                                </td>
                                                                <td><%=confPending.get(8)%></td>
                                                                <td><%=confPending.get(6)%></td>
                                                                <td><%=confPending.get(7)%></td>
                                                                <td><%=confPending.get(9)%></td>
                                                                <td><%=confPending.get(10)%></td>
                                                                <td><%=confPending.get(11)%></td>
                                                            </tr>
                                                            <%			
                                                                }
                                                                }
                                                                %>
                                                        </tbody>
                                                    </table>
                                                    <%
                                                        }else{
                                                        %>
                                                    <div class="nodata msg"><span>No Pending Confirmations</span></div>
                                                    <% }%>
                                                </div>
                                            </div>
                                        </div>
                                        <!-- /.box-body -->
                                    </div>
                                </div>
                            </div>
                        </div>
                        <% } else if(empType!=null && empType.equals("RETIRE")) { %>
                    <div class="induction_div  tab-content">
                        <% 
                            Map<String,List<String>> hmTodaysRetirements = (Map<String,List<String>>)request.getAttribute("todaysRetirements");
                            Map<String,List<String>> hmTomorrowRetirements = (Map<String,List<String>>)request.getAttribute("tomorrowRetirements");
                            Map<String,List<String>> hmDayAfterTomorrowRetirements = (Map<String,List<String>>)request.getAttribute("dayAfterTomorrowRetirements");
                            Map<String,List<String>> hmPendingRetirements = (Map<String,List<String>>)request.getAttribute("pendingRetirements");
                            %>
                        <div style="float:left;width:100%">
                            <%
                                int todayRetirements = 0;
                                if(hmTodaysRetirements != null && !hmTodaysRetirements.isEmpty() && hmTodaysRetirements.size()>0){
                                	todayRetirements = hmTodaysRetirements.size();
                                }
                                
                                %>
                            <div style="margin: 10px 0px 0px 0px; float: left; width: 100%; line-height:30px;">
                                <div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE;">
                                    <div class="box-header with-border">
                                        <h3 class="box-title" style="font-size: 14px;">Today(<%=todayRetirements%>)</h3>
                                        <div class="box-tools pull-right">
                                            <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
                                            <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                                        </div>
                                    </div>
                                    <!-- /.box-header -->
                                    <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
                                        <div class="inductionContent">
                                            <div class="attendance">
                                                <%
                                                    if(hmTodaysRetirements != null && !hmTodaysRetirements.isEmpty() && hmTodaysRetirements.size()>0) {
                                                    	Set todayRetirementsIdSet = hmTodaysRetirements.keySet();
                                                    	Iterator<String> it = todayRetirementsIdSet.iterator(); 
                                                    %>
                                                <table class="table table-bordered" width="100%" cellspacing="0" cellpadding="2" align="left" style="margin-bottom :15px;">
                                                    <!-- class="display tb_style" -->
                                                    <tbody>
                                                        <tr class="darktable">
                                                            <td style="text-align: center;">Name</td>
                                                            <td style="text-align: center;">Designation</td>
                                                            <td style="text-align: center;">Work Location</td>
                                                            <td style="text-align: center;">Department</td>
                                                            <td style="text-align: center;">Reporting Manager </td>
                                                            <td style="text-align: center;">Confirmation Date</td>
                                                            <td style="text-align: center;">Action- Take Action</td>
                                                        </tr>
                                                        <%
                                                            while(it.hasNext()){
                                                            	String empId = it.next();
                                                            	List<String> alRetirementsToday = hmTodaysRetirements.get(empId);
                                                            	if(alRetirementsToday == null) alRetirementsToday = new ArrayList<String>();
                                                            	if(alRetirementsToday!=null && alRetirementsToday.size()>0 && !alRetirementsToday.isEmpty()){
                                                            %>
                                                        <tr>
                                                            <td style="text-align: left; vertical-align: text-top;">
                                                                <script type="text/javascript">
                                                                    $(function() {
                                                                    $('#starPrimaryS'+'<%=empId %>').raty({
                                                                          readOnly: true,
                                                                          start:1.0 ,
                                                                          half: true,
                                                                          targetType: 'number'
                                                                    });
                                                                      });
                                                                </script>
                                                                <div style="float: left; margin: 2px 10px 0px 0px;">
                                                                    <!-- border: 1px solid #000; -->
                                                                    <%if(docRetriveLocation == null) { %>
                                                                    <img height="40" width="40" border="0" class="lazy img-circle" id="profilecontainerimg<%=empId%>" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + alRetirementsToday.get(5)%>" />
                                                                    <%} else { %>
                                                                    <img height="40" width="40" border="0" class="lazy img-circle" id="profilecontainerimg<%=empId%>" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+empId+"/"+IConstants.I_60x60+"/"+alRetirementsToday.get(5)%>" />
                                                                    <%} %> 
                                                                </div>
                                                                <div style="float: left;">
                                                                <!-- Created by Dattatray Date : 21-07-21 Note: empId encrypt -->
                                                                    <a href="javascript:void(0);" onclick="openEmpProfilePopup('<%=empId%>')"> <%=alRetirementsToday.get(1)%> </a>
                                                                    <br/>
                                                                    <div style="font-size: 11px; line-height: 22px;"><strong style="color: gray;">Added By:</strong>&nbsp;<%=uF.showData(alRetirementsToday.get(4),"-") %></div>
                                                                </div>
                                                            </td>
                                                            <td><%=alRetirementsToday.get(8)%></td>
                                                            <td><%=alRetirementsToday.get(6)%></td>
                                                            <td><%=alRetirementsToday.get(7)%></td>
                                                            <td><%=alRetirementsToday.get(9)%></td>
                                                            <td><%=alRetirementsToday.get(10)%></td>
                                                            <td><%=alRetirementsToday.get(11)%></td>
                                                        </tr>
                                                        <%			
                                                            }
                                                            }
                                                            %>
                                                    </tbody>
                                                </table>
                                                <%
                                                    }else{
                                                    %>
                                                <div class="nodata msg"><span>No Retirements For Today</span></div>
                                                <% }%>
                                            </div>
                                        </div>
                                    </div>
                                    <!-- /.box-body -->
                                </div>
                                <div style="margin: 10px 0px 0px 0px; float: left; width: 100%; line-height:30px;">
                                    <%
                                        int tommRetirements = 0;
                                        if(hmTomorrowRetirements != null && !hmTomorrowRetirements.isEmpty() && hmTomorrowRetirements.size()>0){
                                        	tommRetirements = hmTomorrowRetirements.size();
                                        }
                                        
                                        %>
                                    <div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE;">
                                        <div class="box-header with-border">
                                            <h3 class="box-title" style="font-size: 14px;">Tomorrow(<%=tommRetirements%>)</h3>
                                            <div class="box-tools pull-right">
                                                <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
                                                <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                                            </div>
                                        </div>
                                        <!-- /.box-header -->
                                        <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
                                            <div class="inductionContent">
                                                <div class="attendance">
                                                    <%
                                                        if(hmTomorrowRetirements != null && !hmTomorrowRetirements.isEmpty() && hmTomorrowRetirements.size()>0) {
                                                        	Set retireTommIdSet = hmTomorrowRetirements.keySet();
                                                        	Iterator<String> it = retireTommIdSet.iterator();
                                                        %>
                                                    <table class="table table-bordered" width="100%" cellspacing="0" cellpadding="2" align="left" style="margin-bottom :15px;">
                                                        <!-- class="display tb_style" -->
                                                        <tbody>
                                                            <tr class="darktable">
                                                                <td style="text-align: center;">Name</td>
                                                                <td style="text-align: center;">Designation</td>
                                                                <td style="text-align: center;">Work Location</td>
                                                                <td style="text-align: center;">Department</td>
                                                                <td style="text-align: center;">Reporting Manager </td>
                                                                <td style="text-align: center;">Confirmation Date</td>
                                                                <td style="text-align: center;">Action- Take Action</td>
                                                            </tr>
                                                            <%
                                                                while(it.hasNext()){
                                                                	String empId = it.next();
                                                                	List<String> retireTomorrow = hmTomorrowRetirements.get(empId);
                                                                	if(retireTomorrow == null) retireTomorrow = new ArrayList<String>();
                                                                	if(retireTomorrow!=null && retireTomorrow.size()>0 && !retireTomorrow.isEmpty()){
                                                                %>
                                                            <tr>
                                                                <td style="text-align: left; vertical-align: text-top;">
                                                                    <script type="text/javascript">
                                                                        $(function() {
                                                                        $('#starPrimaryS'+'<%=empId %>').raty({
                                                                              readOnly: true,
                                                                              start:1.0 ,
                                                                              half: true,
                                                                              targetType: 'number'
                                                                        });
                                                                          });
                                                                    </script>
                                                                    <div style="float: left; margin: 2px 10px 0px 0px;">
                                                                        <!-- border: 1px solid #000; -->
                                                                        <%if(docRetriveLocation == null) { %>
                                                                        <img height="40" width="40" border="0" class="lazy img-circle" id="profilecontainerimg<%=empId%>" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + retireTomorrow.get(5)%>" />
                                                                        <%} else { %>
                                                                        <img height="40" width="40" border="0" class="lazy img-circle" id="profilecontainerimg<%=empId%>" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+empId+"/"+IConstants.I_60x60+"/"+retireTomorrow.get(5)%>" />
                                                                        <%} %> 
                                                                    </div>
                                                                    <div style="float: left;">
                                                                    <!-- Created by Dattatray Date : 21-07-21 Note: empId encrypt -->
                                                                        <a href="javascript:void(0);" onclick="openEmpProfilePopup('<%=empId%>')"> <%=retireTomorrow.get(1)%> </a>
                                                                        <br/>
                                                                        <div style="font-size: 11px; line-height: 22px;"><strong style="color: gray;">Added By:</strong>&nbsp;<%=uF.showData(retireTomorrow.get(4),"-") %></div>
                                                                    </div>
                                                                </td>
                                                                <td><%=retireTomorrow.get(8)%></td>
                                                                <td><%=retireTomorrow.get(6)%></td>
                                                                <td><%=retireTomorrow.get(7)%></td>
                                                                <td><%=retireTomorrow.get(9)%></td>
                                                                <td><%=retireTomorrow.get(10)%></td>
                                                                <td><%=retireTomorrow.get(11)%></td>
                                                            </tr>
                                                            <%			
                                                                }
                                                                }
                                                                %>
                                                        </tbody>
                                                    </table>
                                                    <%
                                                        }else{
                                                        %>
                                                    <div class="nodata msg"><span>No Retirements For Tomorrow</span></div>
                                                    <% }%>
                                                </div>
                                            </div>
                                        </div>
                                        <!-- /.box-body -->
                                    </div>
                                </div>
                                <div style="margin: 10px 0px 0px 0px; float: left; width: 100%; line-height:30px;">
                                    <%
                                        int datRetirements = 0;
                                        if(hmDayAfterTomorrowRetirements != null && !hmDayAfterTomorrowRetirements.isEmpty() && hmDayAfterTomorrowRetirements.size()>0){
                                        datRetirements = hmDayAfterTomorrowRetirements.size();
                                        }
                                        
                                        %>
                                    <p class="heading_dash" style="padding-left: 60px;">
                                        <span style="width: 100%;"><b></b></span>
                                    </p>
                                    <div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE;">
                                        <div class="box-header with-border">
                                            <h3 class="box-title" style="font-size: 14px;">Day After Tomorrow(<%=datRetirements%>)</h3>
                                            <div class="box-tools pull-right">
                                                <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
                                                <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                                            </div>
                                        </div>
                                        <!-- /.box-header -->
                                        <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
                                            <div class="inductionContent">
                                                <div class="attendance">
                                                    <%
                                                        if(hmDayAfterTomorrowRetirements != null && !hmDayAfterTomorrowRetirements.isEmpty() && hmDayAfterTomorrowRetirements.size()>0) {
                                                        	Set retireDATIdSet = hmDayAfterTomorrowRetirements.keySet();
                                                        	Iterator<String> it = retireDATIdSet.iterator();
                                                        %>
                                                    <table class="table table-bordered" width="100%" cellspacing="0" cellpadding="2" align="left" style="margin-bottom :15px;">
                                                        <!-- class="display tb_style" -->
                                                        <tbody>
                                                            <tr class="darktable">
                                                                <td style="text-align: center;">Name</td>
                                                                <td style="text-align: center;">Designation</td>
                                                                <td style="text-align: center;">Work Location</td>
                                                                <td style="text-align: center;">Department</td>
                                                                <td style="text-align: center;">Reporting Manager </td>
                                                                <td style="text-align: center;">Confirmation Date</td>
                                                                <td style="text-align: center;">Action- Take Action</td>
                                                            </tr>
                                                            <%
                                                                while(it.hasNext()){
                                                                	String empId = it.next();
                                                                	List<String>  alInnerDAT= hmDayAfterTomorrowRetirements.get(empId);
                                                                	if(alInnerDAT == null) alInnerDAT = new ArrayList<String>();
                                                                	if(alInnerDAT!=null && alInnerDAT.size()>0 && !alInnerDAT.isEmpty()){
                                                                %>
                                                            <tr>
                                                                <td style="text-align: left; vertical-align: text-top;">
                                                                    <script type="text/javascript">
                                                                        $(function() {
                                                                        $('#starPrimaryS'+'<%=empId %>').raty({
                                                                              readOnly: true,
                                                                              start:1.0 ,
                                                                              half: true,
                                                                              targetType: 'number'
                                                                        });
                                                                          });
                                                                    </script>
                                                                    <div style="float: left; margin: 2px 10px 0px 0px;">
                                                                        <!-- border: 1px solid #000; -->
                                                                        <%if(docRetriveLocation == null) { %>
                                                                        <img height="40" width="40" border="0" class="lazy img-circle" id="profilecontainerimg<%=empId%>" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + alInnerDAT.get(5)%>" />
                                                                        <%} else { %>
                                                                        <img height="40" width="40" border="0" class="lazy img-circle" id="profilecontainerimg<%=empId%>" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+empId+"/"+IConstants.I_60x60+"/"+alInnerDAT.get(5)%>" />
                                                                        <%} %> 
                                                                    </div>
                                                                    <div style="float: left;">
                                                                    <!-- Created by Dattatray Date : 21-07-21 Note: empId encrypt -->
                                                                        <a href="javascript:void(0);" onclick="openEmpProfilePopup('<%=empId%>')"> <%=alInnerDAT.get(1)%> </a>
                                                                        <br/>
                                                                        <div style="font-size: 11px; line-height: 22px;"><strong style="color: gray;">Added By:</strong>&nbsp;<%=uF.showData(alInnerDAT.get(4),"-") %></div>
                                                                    </div>
                                                                </td>
                                                                <td><%=alInnerDAT.get(8)%></td>
                                                                <td><%=alInnerDAT.get(6)%></td>
                                                                <td><%=alInnerDAT.get(7)%></td>
                                                                <td><%=alInnerDAT.get(9)%></td>
                                                                <td><%=alInnerDAT.get(10)%></td>
                                                                <td><%=alInnerDAT.get(11)%></td>
                                                            </tr>
                                                            <%			
                                                                }
                                                                }
                                                                %>
                                                        </tbody>
                                                    </table>
                                                    <%		
                                                        }else{
                                                        %>
                                                    <div class="nodata msg"><span>No Future Retirements</span></div>
                                                    <% }%>
                                                </div>
                                            </div>
                                        </div>
                                        <!-- /.box-body -->
                                    </div>
                                </div>
                                <div style="margin: 10px 0px 0px 0px; float: left; width: 100%; line-height:30px;">
                                    <%
                                        int pendingRetirements = 0;
                                        if(hmPendingRetirements != null && !hmPendingRetirements.isEmpty() && hmPendingRetirements.size()>0){
                                        pendingRetirements = hmPendingRetirements.size();
                                        }
                                        
                                        %>
                                    <div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE;">
                                        <div class="box-header with-border">
                                            <h3 class="box-title" style="font-size: 14px;">Pending(<%=pendingRetirements%>)</h3>
                                            <div class="box-tools pull-right">
                                                <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
                                                <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                                            </div>
                                        </div>
                                        <!-- /.box-header -->
                                        <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
                                            <div class="inductionContent">
                                                <div class="attendance">
                                                    <%
                                                        if(hmPendingRetirements != null && !hmPendingRetirements.isEmpty() && hmPendingRetirements.size()>0) {
                                                        	Set pendingRetirementsSet= hmPendingRetirements.keySet();
                                                        	Iterator<String> it = pendingRetirementsSet.iterator();
                                                        %>
                                                    <table class="table table-bordered" width="100%" cellspacing="0" cellpadding="2" align="left" style="margin-bottom :15px;">
                                                        <!-- class="display tb_style" -->
                                                        <tbody>
                                                            <tr class="darktable">
                                                                <td style="text-align: center;">Name</td>
                                                                <td style="text-align: center;">Designation</td>
                                                                <td style="text-align: center;">Work Location</td>
                                                                <td style="text-align: center;">Department</td>
                                                                <td style="text-align: center;">Reporting Manager </td>
                                                                <td style="text-align: center;">Confirmation Date</td>
                                                                <td style="text-align: center;">Action- Take Action</td>
                                                            </tr>
                                                            <%	
                                                                while(it.hasNext()){
                                                                	String empId = it.next();
                                                                	List<String> retirePending = hmPendingRetirements.get(empId);
                                                                	if(retirePending == null) retirePending = new ArrayList<String>();
                                                                	if(retirePending!=null && retirePending.size()>0 && !retirePending.isEmpty()){
                                                                %>
                                                            <tr>
                                                                <td style="text-align: left; vertical-align: text-top;">
                                                                    <script type="text/javascript">
                                                                        $(function() {
                                                                        $('#starPrimaryS'+'<%=empId %>').raty({
                                                                              readOnly: true,
                                                                              start:1.0 ,
                                                                              half: true,
                                                                              targetType: 'number'
                                                                        });
                                                                          });
                                                                    </script>
                                                                    <div style="float: left; margin: 2px 10px 0px 0px;">
                                                                        <!-- border: 1px solid #000; -->
                                                                        <%if(docRetriveLocation == null) { %>
                                                                        <img height="40" width="40" border="0" class="lazy img-circle" id="profilecontainerimg<%=empId%>" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + retirePending.get(5)%>" />
                                                                        <%} else { %>
                                                                        <img height="40" width="40" border="0" class="lazy img-circle" id="profilecontainerimg<%=empId%>" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+empId+"/"+IConstants.I_60x60+"/"+retirePending.get(5)%>" />
                                                                        <%} %> 
                                                                    </div>
                                                                    <div style="float: left;">
                                                                    <!-- Created by Dattatray Date : 21-07-21 Note: empId encrypt -->
                                                                        <a href="javascript:void(0);" onclick="openEmpProfilePopup('<%=empId%>')"> <%=retirePending.get(1)%> </a>
                                                                        <br/>
                                                                        <div style="font-size: 11px; line-height: 22px;"><strong style="color: gray;">Added By:</strong>&nbsp;<%=uF.showData(retirePending.get(4),"-") %></div>
                                                                    </div>
                                                                </td>
                                                                <td><%=retirePending.get(8)%></td>
                                                                <td><%=retirePending.get(6)%></td>
                                                                <td><%=retirePending.get(7)%></td>
                                                                <td><%=retirePending.get(9)%></td>
                                                                <td><%=retirePending.get(10)%></td>
                                                                <td><%=retirePending.get(11)%></td>
                                                            </tr>
                                                            <%			
                                                                }
                                                                }
                                                                %>
                                                        </tbody>
                                                    </table>
                                                    <%
                                                        }else{
                                                        %>
                                                    <div class="nodata msg"><span>No Pending Retirements</span></div>
                                                    <% }%>
                                                </div>
                                            </div>
                                        </div>
                                        <!-- /.box-body -->
                                    </div>
                                </div>
                            </div>
                        </div>
                        <%} else if(empType!=null && empType.equals("R")) { %>
                        <div class="induction_div  tab-content">
                            <% 
                                Map<String,List<String>> hmTodaysResig = (Map<String,List<String>>)request.getAttribute("todaysResignation");
                                Map<String,List<String>> hmTomorrowResig = (Map<String,List<String>>)request.getAttribute("tomorrowResignation");
                                Map<String,List<String>> hmDayAfterTomorrowResig = (Map<String,List<String>>)request.getAttribute("dayAfterTomorrowResignation");
                                Map<String,List<String>> hmPendingResig = (Map<String,List<String>>)request.getAttribute("pendingResignation");
                               // System.out.println("EA.jsp/2675--hmPendingResig="+hmPendingResig);
                                %>
                            <div style="float:left;width:100%">
                                <%
                                    int todayResig = 0;
                                    if(hmTodaysResig != null && !hmTodaysResig.isEmpty() && hmTodaysResig.size()>0){
                                    	todayResig = hmTodaysResig.size();
                                    }
                                    
                                    %>
                                <div style="margin: 10px 0px 0px 0px; float: left; width: 100%; line-height:30px;">
                                    <div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE;">
                                        <div class="box-header with-border">
                                            <h3 class="box-title" style="font-size: 14px;">Today(<%=todayResig%>)</h3>
                                            <div class="box-tools pull-right">
                                                <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
                                                <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                                            </div>
                                        </div>
                                        <!-- /.box-header -->
                                        <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
                                            <div class="inductionContent">
                                                <div class="attendance">
                                                    <%
                                                        if(hmTodaysResig != null && !hmTodaysResig.isEmpty() && hmTodaysResig.size()>0) {
                                                        	Set todayResigIdSet = hmTodaysResig.keySet();
                                                        	Iterator<String> it = todayResigIdSet.iterator();
                                                        %>
                                                    <table class="table table-bordered" width="100%" cellspacing="0" cellpadding="2" align="left" style="margin-bottom :15px;">
                                                        <!-- class="display tb_style" -->
                                                        <tbody>
                                                            <tr class="darktable">
                                                                <td style="text-align: center;">Name</td>
                                                                <td style="text-align: center;">Designation</td>
                                                                <td style="text-align: center;">Work Location</td>
                                                                <td style="text-align: center;">Department</td>
                                                                <td style="text-align: center;">Reporting Manager </td>
                                                                <td style="text-align: center;">Resignation Date</td>
                                                                <td style="text-align: center;">Reason</td>
                                                                <td style="text-align: center;">Notice</td>
                                                                <td style="text-align: center;">Status</td>
                                                                <td style="text-align: center;">Work Flow</td>
                                                                <td style="text-align: center;">Action- Full & Final</td>
                                                            </tr>
                                                            <%
                                                                while(it.hasNext()){
                                                                	String offBoardId = it.next();
                                                                	List<String> alResigToday = hmTodaysResig.get(offBoardId);
                                                                	if(alResigToday == null) alResigToday = new ArrayList<String>();
                                                                	if(alResigToday!=null && alResigToday.size()>0 && !alResigToday.isEmpty()){
                                                                %>
                                                            <tr>
                                                                <td style="text-align: left; vertical-align: text-top;">
                                                                    <script type="text/javascript">
                                                                        $(function() {
                                                                        $('#starPrimaryS'+'<%=alResigToday.get(18) %>').raty({
                                                                              readOnly: true,
                                                                              start:1.0 ,
                                                                              half: true,
                                                                              targetType: 'number'
                                                                        });
                                                                          });
                                                                    </script>
                                                                    <div style="float: left; margin: 2px 10px 0px 0px;">
                                                                        <!-- border: 1px solid #000; -->
                                                                        <%if(docRetriveLocation == null) { %>
                                                                        <img height="40" width="40" border="0" class="lazy img-circle" id="profilecontainerimg<%=alResigToday.get(18)%>" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + alResigToday.get(15)%>" />
                                                                        <%} else { %>
                                                                        <img height="40" width="40" border="0" class="lazy img-circle" id="profilecontainerimg<%=alResigToday.get(18)%>" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+alResigToday.get(18)+"/"+IConstants.I_60x60+"/"+alResigToday.get(15)%>" />
                                                                        <%} %> 
                                                                    </div>
                                                                    <div style="float: left;">
                                                                    <!-- Created by Dattatray Date : 21-07-21 Note: empId encrypt -->
                                                                        <a href="javascript:void(0);" onclick="openEmpProfilePopup('<%=alResigToday.get(18)%>')"> <%=alResigToday.get(1)%> </a>
                                                                        <br/>
                                                                        <div style="font-size: 11px; line-height: 22px;"><strong style="color: gray;">Added By:</strong>&nbsp;<%=uF.showData(alResigToday.get(16),"-") %></div>
                                                                    </div>
                                                                </td>
                                                                <td><%=alResigToday.get(14)%></td>
                                                                <td><%=alResigToday.get(12)%></td>
                                                                <td><%=alResigToday.get(13)%></td>
                                                                <td><%=alResigToday.get(17)%></td>
                                                          <!-- ===start parvez date: 27-10-2021=== -->
                                                                <%-- <td><%=alResigToday.get(3)%></td> --%>
                                                                <td><%=alResigToday.get(19)%></td>
                                                          <!-- ===end parvez date: 27-10-2021=== -->
                                                                <td><%=alResigToday.get(4)%></td>
                                                                <td><%=alResigToday.get(5)%></td>
                                                                <td><%=alResigToday.get(7)%></td>
                                                                <td><%=alResigToday.get(8)%></td>
                                                                <td><%=alResigToday.get(9)%></td>
                                                                <%-- <% System.out.println("resigDate="+alResigToday.get(19));%> --%>
                                                            </tr>
                                                            <%			
                                                                }
                                                                }
                                                                %>
                                                        </tbody>
                                                    </table>
                                                    <%
                                                        }else{
                                                        %>
                                                    <div class="nodata msg"><span>No Resignations For Today</span></div>
                                                    <% }%>
                                                </div>
                                            </div>
                                        </div>
                                        <!-- /.box-body -->
                                    </div>
                                    <div style="margin: 10px 0px 0px 0px; float: left; width: 100%; line-height:30px;">
                                        <%
                                            int pendingConf = 0;
                                            if(hmPendingResig != null && !hmPendingResig.isEmpty() && hmPendingResig.size()>0){
                                            pendingConf = hmPendingResig.size();
                                            }
                                            
                                            %>
                                        <div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE;">
                                            <div class="box-header with-border">
                                                <h3 class="box-title" style="font-size: 14px;">Pending(<%=pendingConf%>)</h3>
                                                <div class="box-tools pull-right">
                                                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
                                                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                                                </div>
                                            </div>
                                            <!-- /.box-header -->
                                            <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
                                                <div class="inductionContent">
                                                    <div class="attendance">
                                                        <%
                                                            if(hmPendingResig != null && !hmPendingResig.isEmpty() && hmPendingResig.size()>0) {
                                                            	Set pendingResigSet= hmPendingResig.keySet();
                                                            	Iterator<String> it = pendingResigSet.iterator();
                                                            %>
                                                        <table class="table table-bordered" width="100%" cellspacing="0" cellpadding="2" align="left" style="margin-bottom :15px;">
                                                            <tbody>
                                                                <tr class="darktable">
                                                                    <td style="text-align: center;">Name</td>
                                                                    <td style="text-align: center;">Designation</td>
                                                                    <td style="text-align: center;">Work Location</td>
                                                                    <td style="text-align: center;">Department</td>
                                                                    <td style="text-align: center;">Reporting Manager </td>
                                                                    <td style="text-align: center;">Resignation Date</td>
                                                                    <td style="text-align: center;">Reason</td>
                                                                    <td style="text-align: center;">Notice</td>
                                                                    <td style="text-align: center;">Status</td>
                                                                    <td style="text-align: center;">Work Flow</td>
                                                                    <td style="text-align: center;">Action- Full & Final</td>
                                                                </tr>
                                                                <%	
                                                                    while(it.hasNext()){
                                                                    	String offBoardId = it.next();
                                                                    	List<String> resigPending = hmPendingResig.get(offBoardId);
                                                                    	if(resigPending == null) resigPending = new ArrayList<String>();
                                                                    	if(resigPending!=null && resigPending.size()>0 && !resigPending.isEmpty()){
                                                                    %>
                                                                <tr>
                                                                    <td style="text-align: left; vertical-align: text-top;">
                                                                        <script type="text/javascript">
                                                                            $(function() {
                                                                            $('#starPrimaryS'+'<%=resigPending.get(18) %>').raty({
                                                                                  readOnly: true,
                                                                                  start:1.0 ,
                                                                                  half: true,
                                                                                  targetType: 'number'
                                                                            });
                                                                              });
                                                                        </script>
                                                                        <div style="float: left; margin: 2px 10px 0px 0px;">
                                                                            <!-- border: 1px solid #000; -->
                                                                            <%if(docRetriveLocation == null) { %>
                                                                            <img height="40" width="40" border="0" class="lazy img-circle" id="profilecontainerimg<%=resigPending.get(18)%>" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + resigPending.get(15)%>" />
                                                                            <%} else { %>
                                                                            <img height="40" width="40" border="0" class="lazy img-circle" id="profilecontainerimg<%=resigPending.get(18)%>" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+resigPending.get(18)+"/"+IConstants.I_60x60+"/"+resigPending.get(15)%>" />
                                                                            <%} %> 
                                                                        </div>
                                                                        <div style="float: left;">
                                                                        <!-- Created by Dattatray Date : 21-07-21 Note: empId encrypt -->
                                                                            <a href="javascript:void(0);" onclick="openEmpProfilePopup('<%=resigPending.get(18)%>')"> <%=resigPending.get(1)%> </a>
                                                                            <br/>
                                                                            <div style="font-size: 11px; line-height: 22px;"><strong style="color: gray;">Added By:</strong>&nbsp;<%=uF.showData(resigPending.get(16),"-") %></div>
                                                                        </div>
                                                                    </td>
                                                                    <td><%=resigPending.get(14)%></td>
                                                                    <td><%=resigPending.get(12)%></td>
                                                                    <td><%=resigPending.get(13)%></td>
                                                                    <td><%=resigPending.get(17)%></td>
                                                             <!-- ===start parvez date: 27-10-2021=== -->
                                                                    <%-- <td><%=resigPending.get(3)%></td> --%>
                                                                    <td><%=resigPending.get(19)%></td>
                                                            <!-- ===end parvez date: 27-10-2021=== -->
                                                                    <td><%=resigPending.get(4)%></td>
                                                                    <td><%=resigPending.get(5)%></td>
                                                                    <td><%=resigPending.get(7)%></td>
                                                                    <td><%=resigPending.get(8)%></td>
                                                                    <td><%=resigPending.get(9)%></td>
                                                                </tr>
                                                                <%			
                                                                    }
                                                                    }
                                                                    %>
                                                            </tbody>
                                                        </table>
                                                        <%
                                                            }else{
                                                            %>
                                                        <div class="nodata msg"><span>No Pending Resignations</span></div>
                                                        <% }%>
                                                    </div>
                                                </div>
                                            </div>
                                            <!-- /.box-body -->
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <% } else if(empType!=null && empType.equals("FD")) { %>
                            <div class="induction_div tab-content">
                                <% 
                                    Map<String,List<String>> hmTodaysFD = (Map<String,List<String>>)request.getAttribute("todaysFD");
                                    Map<String,List<String>> hmTomorrowFD = (Map<String,List<String>>)request.getAttribute("tomorrowFD");
                                    Map<String,List<String>> hmDayAfterTomorrowFD = (Map<String,List<String>>)request.getAttribute("dayAfterTomorrowFD");
                                    Map<String,List<String>> hmPendingFD = (Map<String,List<String>>)request.getAttribute("pendingFD");
                                    %>
                                <div style="float:left;width:100%">
                                    <%
                                        int todayFD = 0;
                                        if(hmTodaysFD != null && !hmTodaysFD.isEmpty() && hmTodaysFD.size()>0){
                                        	todayFD = hmTodaysFD.size();
                                        }
                                        
                                        %>
                                    <div style="margin: 10px 0px 0px 0px; float: left; width: 100%; line-height:30px;">
                                        <div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE;">
                                            <div class="box-header with-border">
                                                <h3 class="box-title" style="font-size: 14px;">Today(<%=todayFD%>)</h3>
                                                <div class="box-tools pull-right">
                                                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
                                                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                                                </div>
                                            </div>
                                            <!-- /.box-header -->
                                            <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
                                                <div class="inductionContent">
                                                    <div class="attendance">
                                                        <% 
                                                            if(hmTodaysFD != null && !hmTodaysFD.isEmpty() && hmTodaysFD.size()>0) {
                                                            	Set todayFDIdSet = hmTodaysFD.keySet();
                                                            	Iterator<String> it = todayFDIdSet.iterator();
                                                            %>
                                                        <table class="table table-bordered" width="100%" cellspacing="0" cellpadding="2" align="left" style="margin-bottom :15px;">
                                                            <tr class="darktable">
                                                                <td style="text-align: center;">Name</td>
                                                                <td style="text-align: center;">Designation</td>
                                                                <td style="text-align: center;">Work Location</td>
                                                                <td style="text-align: center;">Department</td>
                                                                <td style="text-align: center;">Reporting Manager </td>
                                                                <td style="text-align: center;">Resignation Date</td>
                                                                <td style="text-align: center;">Reason</td>
                                                                <td style="text-align: center;">Last Date</td>
                                                                <td style="text-align: center;">Status</td>
                                                                <td style="text-align: center;">Work Flow</td>
                                                                <td style="text-align: center;">Action</td>
                                                            </tr>
                                                            <%
                                                                while(it.hasNext()){
                                                                	String offBoardId = it.next();
                                                                	List<String> alFDToday = hmTodaysFD.get(offBoardId);
                                                                	if(alFDToday == null) alFDToday = new ArrayList<String>();
                                                                	if(alFDToday!=null && alFDToday.size()>0 && !alFDToday.isEmpty()){
                                                                %>
                                                            <tr>
                                                                <td style="text-align: left; vertical-align: text-top;">
                                                                    <script type="text/javascript">
                                                                        $(function() {
                                                                        $('#starPrimaryS'+'<%=alFDToday.get(18) %>').raty({
                                                                              readOnly: true,
                                                                              start:1.0 ,
                                                                              half: true,
                                                                              targetType: 'number'
                                                                        });
                                                                          });
                                                                    </script>
                                                                    <div style="float: left; margin: 2px 10px 0px 0px;">
                                                                        <!-- border: 1px solid #000; -->
                                                                        <%if(docRetriveLocation == null) { %>
                                                                        <img height="40" width="40" border="0" class="lazy img-circle" id="profilecontainerimg<%=alFDToday.get(18)%>" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + alFDToday.get(15)%>" />
                                                                        <%} else { %>
                                                                        <img height="40" width="40" border="0" class="lazy img-circle" id="profilecontainerimg<%=alFDToday.get(18)%>" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+alFDToday.get(18)+"/"+IConstants.I_60x60+"/"+alFDToday.get(15)%>" />
                                                                        <%} %> 
                                                                    </div>
                                                                    <div style="float: left;">
                                                                    <!-- Created by Dattatray Date : 21-07-21 Note: empId encrypt -->
                                                                        <a href="javascript:void(0);" onclick="openEmpProfilePopup('<%=alFDToday.get(18)%>')"> <%=alFDToday.get(1)%> </a>
                                                                        <br/>
                                                                        <div style="font-size: 11px; line-height: 22px;"><strong style="color: gray;">Added By:</strong>&nbsp;<%=uF.showData(alFDToday.get(16),"-") %></div>
                                                                    </div>
                                                                </td>
                                                                <td><%=alFDToday.get(14)%></td>
                                                                <td><%=alFDToday.get(12)%></td>
                                                                <td><%=alFDToday.get(13)%></td>
                                                                <td><%=alFDToday.get(17)%></td>
                                                           <!-- ===start parvez date: 27-10-2021=== -->
                                                                <%-- <td><%=alFDToday.get(3)%></td> --%>
                                                                <td><%=alFDToday.get(19)%></td>
                                                           <!-- ===end parvez date: 27-10-2021=== -->
                                                                <td><%=alFDToday.get(4)%></td>
                                                                <td><%=alFDToday.get(6)%></td>
                                                                <td><%=alFDToday.get(7)%></td>
                                                                <td><%=alFDToday.get(8)%></td>
                                                                <td><%=alFDToday.get(9)%></td>
                                                            </tr>
                                                            <%			
                                                                }
                                                                }
                                                                %>
                                                        </table>
                                                        <%
                                                            }else{%>
                                                        <div class="nodata msg"><span>No FNF For Today</span></div>
                                                        <% }%>
                                                    </div>
                                                </div>
                                            </div>
                                            <!-- /.box-body -->
                                        </div>
                                    </div>
                                    <div style="margin: 10px 0px 0px 0px; float: left; width: 100%; line-height:30px;">
                                        <%
                                            int tommFD = 0;
                                            if(hmTomorrowFD != null && !hmTomorrowFD.isEmpty() && hmTomorrowFD.size()>0){
                                            tommFD = hmTomorrowFD.size();
                                            }
                                            
                                            %>
                                        <div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE;">
                                            <div class="box-header with-border">
                                                <h3 class="box-title" style="font-size: 14px;">Tomorrow(<%=tommFD%>)</h3>
                                                <div class="box-tools pull-right">
                                                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
                                                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                                                </div>
                                            </div>
                                            <!-- /.box-header -->
                                            <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
                                                <div class="inductionContent">
                                                    <div class="attendance">
                                                        <%
                                                            if(hmTomorrowFD != null && !hmTomorrowFD.isEmpty() && hmTomorrowFD.size()>0) {
                                                            	Set fdTommIdSet = hmTomorrowFD.keySet();
                                                            	Iterator<String> it = fdTommIdSet.iterator();
                                                            %>
                                                        <table class="table table-bordered" width="100%" cellspacing="0" cellpadding="2" align="left" style="margin-bottom :15px;">
                                                            <!-- class="display tb_style" -->
                                                            <tbody>
                                                                <tr class="darktable">
                                                                    <td style="text-align: center;">Name</td>
                                                                    <td style="text-align: center;">Designation</td>
                                                                    <td style="text-align: center;">Work Location</td>
                                                                    <td style="text-align: center;">Department</td>
                                                                    <td style="text-align: center;">Reporting Manager </td>
                                                                    <td style="text-align: center;">Resignation Date</td>
                                                                    <td style="text-align: center;">Reason</td>
                                                                    <td style="text-align: center;">Last Date</td>
                                                                    <td style="text-align: center;">Status</td>
                                                                    <td style="text-align: center;">Work Flow</td>
                                                                    <td style="text-align: center;">Action</td>
                                                                </tr>
                                                                <%
                                                                    while(it.hasNext()){
                                                                    	String offBoardId = it.next();
                                                                    	List<String> fdTomorrow = hmTomorrowFD.get(offBoardId);
                                                                    	if(fdTomorrow == null) fdTomorrow = new ArrayList<String>();
                                                                    	if(fdTomorrow!=null && fdTomorrow.size()>0 && !fdTomorrow.isEmpty()){
                                                                    %>
                                                                <tr>
                                                                    <td style="text-align: left; vertical-align: text-top;">
                                                                        <script type="text/javascript">
                                                                            $(function() {
                                                                            $('#starPrimaryS'+'<%=fdTomorrow.get(18) %>').raty({
                                                                                  readOnly: true,
                                                                                  start:1.0 ,
                                                                                  half: true,
                                                                                  targetType: 'number'
                                                                            });
                                                                              });
                                                                        </script>
                                                                        <div style="float: left; margin: 2px 10px 0px 0px;">
                                                                            <!-- border: 1px solid #000; -->
                                                                            <%if(docRetriveLocation == null) { %>
                                                                            <img height="40" width="40" border="0" class="lazy img-circle" id="profilecontainerimg<%=fdTomorrow.get(18)%>" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + fdTomorrow.get(15)%>" />
                                                                            <%} else { %>
                                                                            <img height="40" width="40" border="0" class="lazy img-circle" id="profilecontainerimg<%=fdTomorrow.get(18)%>" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+fdTomorrow.get(18)+"/"+IConstants.I_60x60+"/"+fdTomorrow.get(15)%>" />
                                                                            <%} %> 
                                                                        </div>
                                                                        <div style="float: left;">
                                                                        <!-- Created by Dattatray Date : 21-07-21 Note: empId encrypt -->
                                                                            <a href="javascript:void(0);" onclick="openEmpProfilePopup('<%=fdTomorrow.get(18)%>')"> <%=fdTomorrow.get(1)%> </a>
                                                                            <br/>
                                                                            <div style="font-size: 11px; line-height: 22px;"><strong style="color: gray;">Added By:</strong>&nbsp;<%=uF.showData(fdTomorrow.get(16),"-") %></div>
                                                                        </div>
                                                                    </td>
                                                                    <td><%=fdTomorrow.get(14)%></td>
                                                                    <td><%=fdTomorrow.get(12)%></td>
                                                                    <td><%=fdTomorrow.get(13)%></td>
                                                                    <td><%=fdTomorrow.get(17)%></td>
                                                               <!-- ===start parvez date: 27-10-2021=== -->
                                                                    <%-- <td><%=fdTomorrow.get(3)%></td> --%>
                                                                    <td><%=fdTomorrow.get(19)%></td>
                                                               <!-- ===end parvez date: 27-10-2021=== -->
                                                                    <td><%=fdTomorrow.get(4)%></td>
                                                                    <td><%=fdTomorrow.get(6)%></td>
                                                                    <td><%=fdTomorrow.get(7)%></td>
                                                                    <td><%=fdTomorrow.get(8)%></td>
                                                                    <td><%=fdTomorrow.get(9)%></td>
                                                                </tr>
                                                                <%			
                                                                    }
                                                                    }
                                                                    %>
                                                            </tbody>
                                                        </table>
                                                        <%
                                                            }else{
                                                            %>
                                                        <div class="nodata msg"><span>No FNF For Tomorrow</span></div>
                                                        <% }%>
                                                    </div>
                                                </div>
                                            </div>
                                            <!-- /.box-body -->
                                        </div>
                                    </div>
                                    <div style="margin: 10px 0px 0px 0px; float: left; width: 100%; line-height:30px;">
                                        <%
                                            int datFD = 0;
                                            if(hmDayAfterTomorrowFD != null && !hmDayAfterTomorrowFD.isEmpty() && hmDayAfterTomorrowFD.size()>0){
                                            datFD = hmDayAfterTomorrowFD.size();
                                            }
                                            %>
                                        <div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE;">
                                            <div class="box-header with-border">
                                                <h3 class="box-title" style="font-size: 14px;">Day After Tomorrow(<%=datFD%>)</h3>
                                                <div class="box-tools pull-right">
                                                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
                                                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                                                </div>
                                            </div>
                                            <!-- /.box-header -->
                                            <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
                                                <div class="inductionContent">
                                                    <div class="attendance">
                                                        <%
                                                            if(hmDayAfterTomorrowFD != null && !hmDayAfterTomorrowFD.isEmpty() && hmDayAfterTomorrowFD.size()>0) {
                                                            	Set fdDATIdSet = hmDayAfterTomorrowFD.keySet();
                                                            	Iterator<String> it = fdDATIdSet.iterator();
                                                            %>
                                                        <table class="table table-bordered" width="100%" cellspacing="0" cellpadding="2" align="left" style="margin-bottom :15px;">
                                                            <!-- class="display tb_style" -->
                                                            <tbody>
                                                                <tr class="darktable">
                                                                    <td style="text-align: center;">Name</td>
                                                                    <td style="text-align: center;">Designation</td>
                                                                    <td style="text-align: center;">Work Location</td>
                                                                    <td style="text-align: center;">Department</td>
                                                                    <td style="text-align: center;">Reporting Manager </td>
                                                                    <td style="text-align: center;">Resignation Date</td>
                                                                    <td style="text-align: center;">Reason</td>
                                                                    <td style="text-align: center;">Last Date</td>
                                                                    <td style="text-align: center;">Status</td>
                                                                    <td style="text-align: center;">Work Flow</td>
                                                                    <td style="text-align: center;">Action</td>
                                                                </tr>
                                                                <%
                                                                    while(it.hasNext()){
                                                                    	String offBoardId = it.next();
                                                                    	List<String>  alInnerDAT= hmDayAfterTomorrowFD.get(offBoardId);
                                                                    	if(alInnerDAT == null) alInnerDAT = new ArrayList<String>();
                                                                    	if(alInnerDAT!=null && alInnerDAT.size()>0 && !alInnerDAT.isEmpty()){
                                                                    %>
                                                                <tr>
                                                                    <td style="text-align: left; vertical-align: text-top;">
                                                                        <script type="text/javascript">
                                                                            $(function() {
                                                                            $('#starPrimaryS'+'<%=alInnerDAT.get(18) %>').raty({
                                                                                  readOnly: true,
                                                                                  start:1.0 ,
                                                                                  half: true,
                                                                                  targetType: 'number'
                                                                            });
                                                                              });
                                                                        </script>
                                                                        <div style="float: left; margin: 2px 10px 0px 0px;">
                                                                            <!-- border: 1px solid #000; -->
                                                                            <%if(docRetriveLocation == null) { %>
                                                                            <img height="40" width="40" border="0" class="lazy img-circle" id="profilecontainerimg<%=alInnerDAT.get(18)%>" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + alInnerDAT.get(15)%>" />
                                                                            <%} else { %>
                                                                            <img height="40" width="40" border="0" class="lazy img-circle" id="profilecontainerimg<%=alInnerDAT.get(18)%>" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+alInnerDAT.get(18)+"/"+IConstants.I_60x60+"/"+alInnerDAT.get(15)%>" />
                                                                            <%} %> 
                                                                        </div>
                                                                        <div style="float: left;">
                                                                        <!-- Created by Dattatray Date : 21-07-21 Note: empId encrypt -->
                                                                            <a href="javascript:void(0);" onclick="openEmpProfilePopup('<%=alInnerDAT.get(18)%>')"> <%=alInnerDAT.get(1)%> </a>
                                                                            <br/>
                                                                            <div style="font-size: 11px; line-height: 22px;"><strong style="color: gray;">Added By:</strong>&nbsp;<%=uF.showData(alInnerDAT.get(16),"-") %></div>
                                                                        </div>
                                                                    </td>
                                                                    <td><%=alInnerDAT.get(14)%></td>
                                                                    <td><%=alInnerDAT.get(12)%></td>
                                                                    <td><%=alInnerDAT.get(13)%></td>
                                                                    <td><%=alInnerDAT.get(17)%></td>
                                                              <!-- ===start parvez date: 27-10-2021=== -->
                                                                    <%-- <td><%=alInnerDAT.get(3)%></td> --%>
                                                                    <td><%=alInnerDAT.get(19)%></td>
                                                              <!-- ===end parvez date: 27-10-2021=== -->
                                                                    <td><%=alInnerDAT.get(4)%></td>
                                                                    <td><%=alInnerDAT.get(6)%></td>
                                                                    <td><%=alInnerDAT.get(7)%></td>
                                                                    <td><%=alInnerDAT.get(8)%></td>
                                                                    <td><%=alInnerDAT.get(9)%></td>
                                                                </tr>
                                                                <%			
                                                                    }
                                                                    }
                                                                    %>
                                                            </tbody>
                                                        </table>
                                                        <%		
                                                            }else{
                                                            %>
                                                        <div class="nodata msg"><span>No Future FNF</span></div>
                                                        <% }%>
                                                    </div>
                                                </div>
                                            </div>
                                            <!-- /.box-body -->
                                        </div>
                                    </div>
                                    <div style="margin: 10px 0px 0px 0px; float: left; width: 100%; line-height:30px;">
                                        <%
                                            int pendingFD = 0;
                                            if(hmPendingFD != null && !hmPendingFD.isEmpty() && hmPendingFD.size()>0){
                                            pendingFD = hmPendingFD.size();
                                            }
                                            %>
                                        <div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE;">
                                            <div class="box-header with-border">
                                                <h3 class="box-title" style="font-size: 14px;">Pending(<%=pendingFD%>)</h3>
                                                <div class="box-tools pull-right">
                                                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
                                                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                                                </div>
                                            </div>
                                            <!-- /.box-header -->
                                            <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
                                                <div class="inductionContent">
                                                    <div class="attendance">
                                                        <%
                                                            if(hmPendingFD != null && !hmPendingFD.isEmpty() && hmPendingFD.size()>0) {
                                                            	Set fdPendingSet= hmPendingFD.keySet();
                                                            	Iterator<String> it = fdPendingSet.iterator();
                                                            %>
                                                        <table class="table table-bordered" width="100%" cellspacing="0" cellpadding="2" align="left" style="margin-bottom :15px;">
                                                            <!-- class="display tb_style" -->
                                                            <tbody>
                                                                <tr class="darktable">
                                                                    <td style="text-align: center;">Name</td>
                                                                    <td style="text-align: center;">Designation</td>
                                                                    <td style="text-align: center;">Work Location</td>
                                                                    <td style="text-align: center;">Department</td>
                                                                    <td style="text-align: center;">Reporting Manager </td>
                                                                    <td style="text-align: center;">Resignation Date</td>
                                                                    <td style="text-align: center;">Reason</td>
                                                                    <td style="text-align: center;">Last Date</td>
                                                                    <td style="text-align: center;">Status</td>
                                                                    <td style="text-align: center;">Work Flow</td>
                                                                    <td style="text-align: center;width:80px;">Action</td>
                                                                </tr>
                                                                <%	
                                                                    while(it.hasNext()){
                                                                    	String offBoardId = it.next();
                                                                    	List<String> fdPending = hmPendingFD.get(offBoardId);
                                                                    	if(fdPending == null) fdPending = new ArrayList<String>();
                                                                    	if(fdPending!=null && fdPending.size()>0 && !fdPending.isEmpty()){
                                                                    %>
                                                                <tr>
                                                                    <td style="text-align: left; vertical-align: text-top;">
                                                                        <script type="text/javascript">
                                                                            $(function() {
                                                                            $('#starPrimaryS'+'<%=fdPending.get(18) %>').raty({
                                                                                  readOnly: true,
                                                                                  start:1.0 ,
                                                                                  half: true,
                                                                                  targetType: 'number'
                                                                            });
                                                                              });
                                                                        </script>
                                                                        <div style="float: left; margin: 2px 10px 0px 0px;">
                                                                            <!-- border: 1px solid #000; -->
                                                                            <%if(docRetriveLocation == null) { %>
                                                                            <img height="40" width="40" border="0" class="lazy img-circle" id="profilecontainerimg<%=fdPending.get(18)%>" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + fdPending.get(15)%>" />
                                                                            <%} else { %>
                                                                            <img height="40" width="40" border="0" class="lazy img-circle" id="profilecontainerimg<%=fdPending.get(18)%>" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+fdPending.get(18)+"/"+IConstants.I_60x60+"/"+fdPending.get(15)%>" />
                                                                            <%} %> 
                                                                        </div>
                                                                        <div style="float: left;">
                                                                       		<!-- Created by Dattatray Date : 21-07-21 Note: empId encrypt -->
                                                                            <a href="javascript:void(0);" onclick="openEmpProfilePopup('<%=fdPending.get(18) %>')"> <%=fdPending.get(1)%> </a>
                                                                            <br/>
                                                                            <div style="font-size: 11px; line-height: 22px;"><strong style="color: gray;">Added By:</strong>&nbsp;<%=uF.showData(fdPending.get(16),"-") %></div>
                                                                        </div>
                                                                    </td>
                                                                    <td><%=fdPending.get(14)%></td>
                                                                    <td><%=fdPending.get(12)%></td>
                                                                    <td><%=fdPending.get(13)%></td>
                                                                    <td><%=fdPending.get(17)%></td>
                                                             <!-- ===start parvez date: 27-10-2021=== -->
                                                                    <%-- <td><%=fdPending.get(3)%></td> --%>
                                                                    <td><%=fdPending.get(19)%></td>
                                                             <!-- ===end parvez date: 27-10-2021=== -->
                                                                    <td><%=fdPending.get(4)%></td>
                                                                    <td><%=fdPending.get(6)%></td>
                                                                    <td><%=fdPending.get(7)%></td>
                                                                    <td><%=fdPending.get(8)%></td>
                                                                    <td><%=fdPending.get(9)%></td>
                                                                    <% System.out.println("EA.jsp/3288--fdPending="+fdPending.get(19)+"--empId="+fdPending.get(18)); %>
                                                                </tr>
                                                                <%			
                                                                    }
                                                                    }
                                                                    %>
                                                            </tbody>
                                                        </table>
                                                        <% } else { %>
                                                        <div class="nodata msg"><span>No Pending FNF</span></div>
                                                        <% } %>
                                                    </div>
                                                </div>
                                            </div>
                                            <!-- /.box-body -->
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <% } %>
                            </div>
                        </div>
                    </div>
                </div>
                <!-- /.box-body -->
            </div>
    </section>
    </div>
</section>
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
<div id="PanelEmpProfilePopup"></div>

	
		
<script>
    disableAll();
    
    disableAll1();
    
</script>
<script>
    //$("img.lazy").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10}); 
    $("img.lazy").lazyload({event : "sporty", threshold : 200,effect : "fadeIn",failure_limit : 10});
    
    $(window).bind("load", function() {
        var timeout = setTimeout(function() { $("img.lazy").trigger("sporty")}, 1000);
    }); 
</script>