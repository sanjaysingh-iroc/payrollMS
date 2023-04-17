<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.*"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>


<%java.util.List couterlist = (java.util.List)request.getAttribute("reportList"); %>

<script type="text/javascript">
$(function(){ 
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
	/* $("#frmEmpActivity_strUpdateDocument").click(function(){
		$(".validateRequired").prop('required',true);
	});
	$("input[name='strUpdate']").click(function(){
		$(".validateRequired").prop('required',true);
	}); */
	if(document.getElementById("strActivity")) {
		var activityId = document.getElementById("strActivity").value;
		selectElements(activityId);
	}
	

});
      
      function selectElements(activityId) { 
    	  disableAll();
    	  //alert("activityId ===>>>> " + activityId);
    	 
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
    		  var strEmpId = document.getElementById("strEmpId2").value;
    	  	  var action = 'EmpActivityEmpSalaryDetails.action?empId=' + strEmpId;
    	  	  getContent('salaryDetailsDiv', action);
    	  	  
    	  	  	window.setTimeout(function() {
    	  	  		changeLabelValuesE('1');
    		  	}, 700); 
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
    		  
    	  } else if(activityId == <%=IConstants.ACTIVITY_INCREMENT_ID %>) { // Increment 
    		  document.getElementById("tdEffectiveLbl").style.display = "table-cell";
    		  document.getElementById("idEffectiveDate").style.display = "table-cell";
    		  document.getElementById("incrementTypeTR").style.display = "table-row";
    	  		var strEmpId = document.getElementById("strEmpId2").value;
    	  		//alert("strEmpId ===> " + strEmpId);
    	  		var action = 'EmpActivityEmpSalaryDetails.action?empId=' + strEmpId;
    	  		getContent('salaryDetailsDiv', action);
    	  		
    	  	  	window.setTimeout(function() {
    	  	  		changeLabelValuesE('1');
    		  	}, 700); 
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
    	  
    	  <%
			for (int i=0; couterlist!=null && i<couterlist.size(); i++) {
				java.util.List cinnerlist = (java.util.List)couterlist.get(i); 
				if(cinnerlist.get(4).equals("P")) {
			%>
					var amt = document.getElementById("hide_"+<%=(String)cinnerlist.get(1)%>).value; 	
					document.getElementById("lblValue_"+<%=(String)cinnerlist.get(1)%>+"_"+<%=(String)cinnerlist.get(5)%>).value = amt;
			<%}else{%>
					var amt = document.getElementById("hide_"+<%=(String)cinnerlist.get(1)%>).value; 	 
					document.getElementById(""+<%=(String)cinnerlist.get(1)%>).value = amt;
			<%}%>
		<%}%>
			if(document.getElementById("1")) {
				changeLabelValuesE("1");
			}
    	  
      }
      
      
      function disableAll() {
    	  
    	  //strTransferType strOrganisation strWLocation strLevel strDepartment strDesignation empGrade
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
	  		 
	  		if(document.getElementById('empGrade')) {
	  			 document.getElementById('empGrade').selectedIndex = 0;
	  		 }
	  		
	  		if(document.getElementById('extendProbationTR')) {
	  			document.getElementById("extendProbationTR").style.display = "none";
	  		 }
    	  
	  		 if(document.getElementById('tranferTypeTR')) {
	  		 	 document.getElementById("tranferTypeTR").style.display = "none";
	  		 }
    	 
	  		if(document.getElementById('incrementTypeTR')) {
	  		 	 document.getElementById("incrementTypeTR").style.display = "none";
	  		 }
    	 
	  		if(document.getElementById('salaryDetailsDiv')) {
	  			document.getElementById("salaryDetailsDiv").innerHTML = "";
	  		 }
  		
	  		if(document.getElementById('incrementPercentTR')) {
	  			document.getElementById("incrementPercentTR").style.display = "none";
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
		     
	  		if(document.getElementById('sbuLTD')) {
	  			document.getElementById("sbuLTD").style.display = "none";
	  		 }
		  
	  		if(document.getElementById('sbuVTD')) {
	  			 document.getElementById("sbuVTD").style.display = "none";
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
		  
	  		if(document.getElementById('deptLTD')) {
	  			document.getElementById("deptLTD").style.display = "none";
	  		 }
		  
	  		if(document.getElementById('deptVTD')) {
	  			document.getElementById("deptVTD").style.display = "none";
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
		  
	  		if(document.getElementById('noticeTR')) {
	  			document.getElementById("noticeTR").style.display = "none";
		  	}
	  		
	  		if(document.getElementById('probationTR')) {
	  			document.getElementById("probationTR").style.display = "none";
		  	}
		  
      }
      
function disableAll1() {
    	  //strTransferType strOrganisation strWLocation strLevel strDepartment
    	  if(document.getElementById('strOrganisation')){
    		  document.getElementById('strOrganisation').selectedIndex = 0;
    	  }
	
    	  if(document.getElementById('strWLocation')){
    		  document.getElementById('strWLocation').selectedIndex = 0;;
    	  }
    	  
    	  if(document.getElementById('strLevel')){
    		  document.getElementById('strLevel').selectedIndex = 0;
    	  }
	 
    	  if(document.getElementById('strDepartment')){
    		  document.getElementById('strDepartment').selectedIndex = 0;
    	  }
	  
    	  if(document.getElementById('strDesignation')){
    	  	  	document.getElementById('strDesignation').selectedIndex = 0;
    	  }
    	  
    	  if(document.getElementById('empGrade')){
    		  document.getElementById('empGrade').selectedIndex = 0;
    	  }
    	  
    	  if(document.getElementById('legalEntityTR')){
    		  document.getElementById("legalEntityTR").style.display = "none";
    	  }
	      
    	  if(document.getElementById('locationLTD')){
    		  document.getElementById("locationLTD").style.display = "none";
    	  }
    	  
    	  if(document.getElementById('locationVTD')){
    		  document.getElementById("locationVTD").style.display = "none";
    	  }
	  
    	  if(document.getElementById('sbuLTD')){
    		  document.getElementById("sbuLTD").style.display = "none";
    	  }
    	  
    	  if(document.getElementById('sbuVTD')){
    		  document.getElementById("sbuVTD").style.display = "none";
    	  }
    	  
    	  if(document.getElementById('deptLTD')){
    		  document.getElementById("deptLTD").style.display = "none";
    	  }
	   
    	  if(document.getElementById('deptVTD')){
    		  document.getElementById("deptVTD").style.display = "none";
    	  }
	      
    	  if(document.getElementById('levelLTD')){
    		  document.getElementById("levelLTD").style.display = "none";
    	  }
    	  
    	  if(document.getElementById('levelVTD')){
    		  document.getElementById("levelVTD").style.display = "none";
    	  }
    	  
    	  if(document.getElementById('desigLTD')){
    		  document.getElementById("desigLTD").style.display = "none";
    	  }
	  
    	  if(document.getElementById('desigVTD')){
    		  document.getElementById("desigVTD").style.display = "none";
    	  }
	  
    	  if(document.getElementById('gradeLTD')){
    		  document.getElementById("gradeLTD").style.display = "none";
    	  }
	      
    	  if(document.getElementById('gradeVTD')){
    		  document.getElementById("gradeVTD").style.display = "none";
    	  }
	  
    	  if(document.getElementById('gradeChangeLTD')){
    		  document.getElementById("gradeChangeLTD").style.display = "none";
    	  }
	
    	  if(document.getElementById('gradeChangeVTD')){
    		  document.getElementById("gradeChangeVTD").style.display = "none";
    	  }
	  
      }
      
      
      disableAll();
      
      disableAll1();
      
	function showDesignation(strLevel) {
		var strActivity = document.getElementById("strActivity").value;
		
		if(strActivity !='' && strActivity == <%=IConstants.ACTIVITY_PROMOTION_ID %>){
			var strEmpId = '<%=(String) request.getAttribute("empid")%>';			
			var idEffectiveDate = document.getElementById("idEffectiveDate").value; 
			var learningId = '<%=(String) request.getAttribute("learningId")%>';
			var empid = '<%=(String) request.getAttribute("empid")%>';
			var trainingId = '<%=(String) request.getAttribute("trainingId")%>';
			var assessmentId = '<%=(String) request.getAttribute("assessmentId")%>';
			var remarktype = '<%=(String) request.getAttribute("remarktype")%>';
			
			var action ='LearningEmpActivityLevelSalary.action?dataType=D&strEmpId='+strEmpId+'&strActivity='+strActivity+'&idEffectiveDate='+idEffectiveDate+'&strLevel='+strLevel;
			action +='&learningId='+learningId+ '&empid=' + empid + "&trainingId=" + trainingId + "&assessmentId=" + assessmentId + "&remarktype=" + remarktype;
			$("#formBody").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$.ajax({ 
				type : 'GET',
				url: action,
				cache: true,
				success: function(result){
					$("#formBody").html(result);
		   		}
			});
				
		} else {	
			 var action = 'GetDesigList.action?strLevel=' + strLevel + "&type=EA";
			getContent('desigListSpan', action);
		}
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
      
</script>

<script>
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

</script>




<script type="text/javascript">


	function isNumberKey(evt)
	{
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
	
	function incrementBasicAmount(id) {
		var percentAmount = document.getElementById(id).value;
		if(parseFloat(percentAmount) > parseFloat("0") ){
			var strIncrementType = document.getElementById("strIncrementType").value;
			if(strIncrementType == '2'){
				percentAmount = parseFloat(percentAmount) * 2;
			}
			
			
			if(document.getElementById("1")) {
				var txt_amount = document.getElementById("hide_1").value;
				
				var newBasic = (parseFloat(txt_amount) * parseFloat(percentAmount)) / 100;
				
				var finalBasic = parseFloat(txt_amount) + parseFloat(newBasic);
				document.getElementById("1").value = parseFloat(finalBasic).toFixed(2);
				
				changeLabelValuesE("1");
			} else{
				<%  
				for (int i=0; couterlist!=null && i<couterlist.size(); i++) {
					java.util.List cinnerlist = (java.util.List)couterlist.get(i); 
					
					if(cinnerlist.get(4).equals("P")) {
				%>
						var amt = document.getElementById("hide_"+<%=(String)cinnerlist.get(1)%>).value; 	
						var newAmt = (parseFloat(amt) * parseFloat(percentAmount)) / 100;
						
						var finalAmt = parseFloat(amt) + parseFloat(newAmt);
						document.getElementById("lblValue_"+<%=(String)cinnerlist.get(1)%>+"_"+<%=(String)cinnerlist.get(5)%>).value = finalAmt.toFixed(2);
				<%}else{%>
						var amt = document.getElementById("hide_"+<%=(String)cinnerlist.get(1)%>).value; 	
						var newAmt = (parseFloat(amt) * parseFloat(percentAmount)) / 100;
						
						var finalAmt = parseFloat(amt) + parseFloat(newAmt);
						document.getElementById(""+<%=(String)cinnerlist.get(1)%>).value = finalAmt.toFixed(2);
				<%}%>
			<%}%>
							
			} 
		} else {
			<%
			for (int i=0; couterlist!=null && i<couterlist.size(); i++) {
				java.util.List cinnerlist = (java.util.List)couterlist.get(i); 
				if(cinnerlist.get(4).equals("P")) {
			%>
					var amt = document.getElementById("hide_"+<%=(String)cinnerlist.get(1)%>).value; 	
					document.getElementById("lblValue_"+<%=(String)cinnerlist.get(1)%>+"_"+<%=(String)cinnerlist.get(5)%>).value = amt;
			<%}else{%>
					var amt = document.getElementById("hide_"+<%=(String)cinnerlist.get(1)%>).value; 	 
					document.getElementById(""+<%=(String)cinnerlist.get(1)%>).value = amt;
			<%}%>
		<%}%>
			if(document.getElementById("1")) {
				changeLabelValuesE("1");
			}
		}  
	}
	
	function changeLabelValuesE(id1)  {
		//alert("id1 000 --->> " + id1);
		//alert("id1.indexOf --->> " + id1.indexOf("lblValue_"));
		if(id1.indexOf("lblValue_") != -1) {	//contains lblValue words 
			document.getElementById("tempValue_"+(id1.split("lblValue_")[1])).innerHTML = document.getElementById(id1).value;
		
			editId = (id1.split("_")[1]);
			var editId1 = (id1.split("_")[2]);
			var txt_amount = document.getElementById(id1).value;
			
			if(txt_amount.length == 0)
				txt_amount = 0;
			
			//alert("editId ===>> " + editId);
			//alert("editId1 ===>> " + editId1);
			//alert("txt_amount ===>> " + txt_amount);
			
			document.getElementById("tempValue_"+id1.split("lblValue_")[1]).innerHTML = txt_amount;
			
			<% // java.util.List couterlist = (java.util.List)request.getAttribute("reportList");
				for (int i=0; couterlist!=null && i<couterlist.size(); i++) {
					java.util.List cinnerlist = (java.util.List)couterlist.get(i); 
			%>
			
					var findId = "lbl_amount_"+<%=cinnerlist.get(1)%>+"_"+editId;
					//alert("findId ===>> " + findId);
					 
					<%-- alert("findId ==> "+findId+ " editId ==> "+editId+ " editId1 ==> "+editId1+ " cinnerlist.get(1) ==> "+<%=cinnerlist.get(1)%>); --%>
					if(document.getElementById(findId)) {
					
						//alert("findId== IN ==>"+findId);
						
						var amount = document.getElementById(findId).innerHTML;
						var result = parseFloat(txt_amount) * parseFloat(amount) / 100;
					
						document.getElementById("lblValue_"+<%=cinnerlist.get(1)%>+"_"+editId).value = result;
						document.getElementById("tempValue_"+<%=cinnerlist.get(1)%>+"_"+editId).innerHTML = result;
						
						changeLabelValuesE("lblValue_"+<%=cinnerlist.get(1)%>+"_"+editId);
						
					}
					
			<%}%>
			
		} else {
			var txt_amount = document.getElementById("1").value;
			
			if(txt_amount.length == 0)
				txt_amount = 0;
			document.getElementById("tempValue_1").innerHTML = txt_amount;
			//alert("txt_amount else ===>> " + txt_amount);
			<%  
				for (int i=0; couterlist!=null && i<couterlist.size(); i++) {
					java.util.List cinnerlist = (java.util.List)couterlist.get(i); 
			%>
			
					var findId = "lbl_amount_"+<%=cinnerlist.get(1)%>+"_"+<%=cinnerlist.get(5)%>;
					
					//alert("findId else ==>"+findId); 
					var txt_amount2;
					if(document.getElementById("2")){
						txt_amount2 = document.getElementById("2").value;
					}else if(document.getElementById("lblValue_2_1")){
						txt_amount2 = document.getElementById("lblValue_2_1").value;
					}else{
						txt_amount2 = 0;
					}
					
					
					if(document.getElementById(findId)) {
						//alert("findId else == IN ==>"+findId);
						var amount = document.getElementById(findId).innerHTML;
						
						var txt_amount20001 = 0;
	
						var result = ( parseFloat(txt_amount) + parseFloat(txt_amount2)) * parseFloat(amount) / 100;
						
						if(<%=cinnerlist.get(1)%>==2){
							result = parseFloat(txt_amount) * parseFloat(amount) / 100;
						}else{
							result = ( parseFloat(txt_amount) + parseFloat(txt_amount2) + parseFloat(txt_amount20001)) * parseFloat(amount) / 100;
						}
						
						
						if(<%=cinnerlist.get(1)%>==3 && result>500){
							//result = 500; 			 				
						} 
						
						document.getElementById("lblValue_"+<%=cinnerlist.get(1)%>+"_"+<%=cinnerlist.get(5)%>).value = parseFloat(result).toFixed(2); 
						document.getElementById("tempValue_"+<%=cinnerlist.get(1)%>+"_"+<%=cinnerlist.get(5)%>).innerHTML = parseFloat(result).toFixed(2);
						
						changeLabelValuesE("lblValue_"+<%=cinnerlist.get(1)%>+"_"+<%=cinnerlist.get(5)%>);
					} else {
						if(document.getElementById(id1)) {
							document.getElementById("tempValue_"+id1).innerHTML = document.getElementById(id1).value;
						}
					}
					
			<% } %>
			
		}
	
		var total = 0;
		var elem = document.getElementById("div_earning").getElementsByTagName("span");
		for(var i = 0; i < elem.length; i++)
        {	
			if(elem[i].innerHTML != ''){
				total =  parseFloat(total) + parseFloat(parseFloat(elem[i].innerHTML).toFixed(2));				
			}
        }
		
		document.getElementById("total_earning_value").innerHTML = parseFloat(total);
		
		//Change Net Ammount 
		
		var netAmmount = parseInt(document.getElementById("total_earning_value").innerHTML) - 
						 parseInt(document.getElementById("total_deduction_value").innerHTML);
			
		if (netAmmount < 0 ) {
			document.getElementById("lbl_net_amount").innerHTML = 0;
		}
		
		else {
			document.getElementById("lbl_net_amount").innerHTML = netAmmount;
		}
		
		var total = 0;
		var elem = document.getElementById("div_deduction").getElementsByTagName("span");
		for(var i = 0; i < elem.length; i++)
        {	
			if(elem[i].innerHTML != '')
				total =  parseFloat(total) + parseFloat(elem[i].innerHTML);
        }
		
		document.getElementById("total_deduction_value").innerHTML = parseFloat(total);
   	
	}
	
	function changeLabelValuesD(id1)  {
		
		if(id1.indexOf("lblValue_") != -1) {
			document.getElementById("tempValue_"+(id1.split("lblValue_")[1])).innerHTML = document.getElementById(id1).value;
		
		}else{
		
			var txt_amount = document.getElementById(id1).value;
			if(txt_amount.length == 0)
				txt_amount = 0;
			document.getElementById("tempValue_"+id1).innerHTML = txt_amount;
			
			<%  for (int i=0;  couterlist!=null && i<couterlist.size(); i++) {
					java.util.List cinnerlist = (java.util.List)couterlist.get(i); 
			%>
			
			var findId = "lbl_amount_"+<%=cinnerlist.get(1)%>+id1;
			if(document.getElementById(findId)) {
				var amount = document.getElementById(findId).innerHTML;
				var result = parseFloat(txt_amount) * parseFloat(amount) / 100;
				document.getElementById("lblValue_"+<%=cinnerlist.get(1)%>+id1).value = result;
				document.getElementById("tempValue_"+<%=cinnerlist.get(1)%>+id1).innerHTML = result;
			}
				
			<%}%>
		
		}
		
		var total = 0;
		var elem = document.getElementById("div_deduction").getElementsByTagName("span");
		for(var i = 0; i < elem.length; i++)
        {	
			if(elem[i].innerHTML != '')
				total =  parseFloat(total) + parseFloat(elem[i].innerHTML);
        }
		 
		
		document.getElementById("total_deduction_value").innerHTML = parseFloat(total);
		
		//Change Net Ammount 
		
		var netAmmount = parseInt(document.getElementById("total_earning_value").innerHTML) - 
			parseInt(document.getElementById("total_deduction_value").innerHTML);
			
			if (netAmmount < 0 ) {
			
				document.getElementById("lbl_net_amount").innerHTML = 0;
			}
			
			else {
				
				document.getElementById("lbl_net_amount").innerHTML = netAmmount;
			}
		
   	}
	
	function removeField(id1) {
		
		if(confirm('Are you sure, you want to remove this field?', 'Please Confirm!')) {
			window.location="EmployeeSalaryDetails.action?id=" +id1; 
  			return true;
  		}
  		else {
  			return false;
  		}
	}
	
	
	<%  
	String CCID= (String) request.getAttribute("CCID");
	String ccName = (String) request.getAttribute("CCNAME");
	String EMPNAME = (String)session.getAttribute("EMPNAME_P");
	
	if(EMPNAME==null)
		EMPNAME = (String)request.getAttribute("EMPNAMEFORCC");
	
	UtilityFunctions uF = new UtilityFunctions();
	%>	
	
	//var cnt;
	
	var oldValues = new Array();
	
	<%  for (int i=0;  couterlist!=null && i<couterlist.size(); i++) {
			java.util.List cinnerlist = (java.util.List)couterlist.get(i); 
	%>
			oldValues[<%=cinnerlist.get(1)%>] = "<%=cinnerlist.get(8)%>"; 
			
	<%}%>
	
	function makeZeroOnUncheck(displayId) {
		//alert("sjdhdjgfdh");
		var headId = displayId.substring(displayId.indexOf("_")+1, displayId.length);
		
		if(!document.getElementById(displayId).checked) {
			
			if(document.getElementById("lblValue_"+headId)) {
				oldValues[headId.split("_")[0]] = document.getElementById("lblValue_"+headId).value;
				document.getElementById("lblValue_"+headId).value = 0;
				changeLabelValuesE("lblValue_"+headId);
			}
			else if(document.getElementById(headId)) {
				oldValues[headId] = document.getElementById(headId).value;
				document.getElementById(headId).value = 0;
				changeLabelValuesE(headId);
			}
		
		}else {
			
			if(document.getElementById("lblValue_"+headId)) {
				document.getElementById("lblValue_"+headId).value = oldValues[headId.split("_")[0]];
				changeLabelValuesE("lblValue_"+headId);
			}
			
			else if(document.getElementById(headId)) {
				document.getElementById(headId).value = oldValues[headId];
				changeLabelValuesE(headId);
			}
			
		}
		
	}
	
	</script>

<%
		
	//String if_approved = (String) request.getAttribute("if_approved");
	boolean flag = (Boolean) request.getAttribute("flag");
	String finalizedBy = (String)request.getAttribute("finalizedBy");
	Map<String, String> hmActivityMap = (Map<String, String>) request.getAttribute("hmActivityMap"); 
	
	if(hmActivityMap == null) hmActivityMap = new HashMap<String, String>();
	
%>
		
<div class="leftbox reportWidth" id="formBody">
	<s:form method="POST" action="LearningPlanAssessmentFinalize" theme="simple" name="frmLearningPlanAssessmentFinalize" id="frmLearningPlanAssessmentFinalize">
		<s:hidden name="learningId" id="learningId"></s:hidden>
		<s:hidden name="trainingId"></s:hidden>
		<s:hidden name="assessmentId"></s:hidden>
		<s:hidden name="empid"></s:hidden>
		<s:hidden name="remarktype"/>
		
		<%if (flag && hmActivityMap!=null && !hmActivityMap.isEmpty()) {%>
			<div style="float: left; width: 100%; margin-bottom: -7px;">
				<h4>Activity: </h4>
				<table style="width:100%;" class="table table-striped" cellpadding="0" cellspacing="0">
					<tr>
						<th style="text-align: right;" nowrap="nowrap">Activity:</th>
						<td nowrap="nowrap"><%=uF.showData(hmActivityMap.get("ACTIVITY_NAME"),"") %></td>
					</tr>	
					<tr>
						<th style="text-align: right;" nowrap="nowrap">Effective Date:</th>
						<td nowrap="nowrap"><%=uF.showData(hmActivityMap.get("EFFECTIVE_DATE"),"") %></td>
					</tr>
					<tr>
						<th style="text-align: right;" nowrap="nowrap">Reason:</th>
						<td><%=uF.showData(hmActivityMap.get("REASON"),"") %></td>
					</tr>	
					<%if(uF.parseToInt(hmActivityMap.get("ACTIVITY_ID")) == uF.parseToInt(IConstants.ACTIVITY_EXTEND_PROBATION_ID) || uF.parseToInt(hmActivityMap.get("ACTIVITY_ID")) == uF.parseToInt(IConstants.ACTIVITY_NOTICE_PERIOD_ID) || uF.parseToInt(hmActivityMap.get("ACTIVITY_ID")) == uF.parseToInt(IConstants.ACTIVITY_PROBATION_ID)){ %>
						<tr>
							<th style="text-align: right;" nowrap="nowrap">No. Of Days:</th>
							<td nowrap="nowrap"><%=uF.showData(hmActivityMap.get("NO_OF_DAYS"),"") %></td>
						</tr>
					<%} else if(uF.parseToInt(hmActivityMap.get("ACTIVITY_ID")) == uF.parseToInt(IConstants.ACTIVITY_CONFIRMATION_ID)){ %>
						<tr>
							<th style="text-align: right;" nowrap="nowrap">Increment Percentage:</th>
							<td nowrap="nowrap"><%=uF.showData(hmActivityMap.get("INCREMENT_PERCENTAGE"),"") %></td>
						</tr>
					<%} else if(uF.parseToInt(hmActivityMap.get("ACTIVITY_ID")) == uF.parseToInt(IConstants.ACTIVITY_GRADE_CHANGE_ID)){ %>
						<tr>
							<th style="text-align: right;" nowrap="nowrap">Grade:</th>
							<td nowrap="nowrap"><%=uF.showData(hmActivityMap.get("GRADE_NAME"),"") %></td>
						</tr>
					<%} else if(uF.parseToInt(hmActivityMap.get("ACTIVITY_ID")) == uF.parseToInt(IConstants.ACTIVITY_INCREMENT_ID)){ %>
						<tr>
							<th style="text-align: right;" nowrap="nowrap">Increment Type:</th>
							<td nowrap="nowrap"><%=uF.showData(hmActivityMap.get("INCREMENT_TYPE"),"") %></td>
						</tr>
						<tr>
							<th style="text-align: right;" nowrap="nowrap">Increment Percentage:</th>
							<td nowrap="nowrap"><%=uF.showData(hmActivityMap.get("INCREMENT_PERCENTAGE"),"") %></td>
						</tr>
					<%} else if(uF.parseToInt(hmActivityMap.get("ACTIVITY_ID")) == uF.parseToInt(IConstants.ACTIVITY_PROMOTION_ID)){ %>
						<tr>
							<th style="text-align: right;" nowrap="nowrap">Level:</th>
							<td nowrap="nowrap"><%=uF.showData(hmActivityMap.get("LEVEL_NAME"),"") %></td>
						</tr>
						<tr>
							<th style="text-align: right;" nowrap="nowrap">Designation:</th>
							<td nowrap="nowrap"><%=uF.showData(hmActivityMap.get("DESIG_NAME"),"") %></td>
						</tr>
						<tr>
							<th style="text-align: right;" nowrap="nowrap">Grade:</th>
							<td nowrap="nowrap"><%=uF.showData(hmActivityMap.get("GRADE_NAME"),"") %></td>
						</tr>
					<%} else if(uF.parseToInt(hmActivityMap.get("ACTIVITY_ID")) == uF.parseToInt(IConstants.ACTIVITY_TRANSFER_ID)){ 
							if(hmActivityMap.get("TRANSFER_TYPE") != null && hmActivityMap.get("TRANSFER_TYPE").equals("WL")){
					%>
								<tr>
									<th style="text-align: right;" nowrap="nowrap">Transfer Type:</th>
									<td nowrap="nowrap"><%=uF.showData(hmActivityMap.get("TRANSFER_TYPE_NAME"),"") %></td>
								</tr>
								<tr>
									<th style="text-align: right;" nowrap="nowrap">Work Location:</th>
									<td nowrap="nowrap"><%=uF.showData(hmActivityMap.get("WORK_LOCATION_NAME"),"") %></td>
								</tr>
							<%} else if(hmActivityMap.get("TRANSFER_TYPE") != null && hmActivityMap.get("TRANSFER_TYPE").equals("DEPT")){%>
								<tr>
									<th style="text-align: right;" nowrap="nowrap">Transfer Type:</th>
									<td nowrap="nowrap"><%=uF.showData(hmActivityMap.get("TRANSFER_TYPE_NAME"),"") %></td>
								</tr>
								<tr>
									<th style="text-align: right;" nowrap="nowrap">Department:</th>
									<td nowrap="nowrap"><%=uF.showData(hmActivityMap.get("DEPARTMENT_NAME"),"") %></td>
								</tr>
							<%} else if(hmActivityMap.get("TRANSFER_TYPE") != null && hmActivityMap.get("TRANSFER_TYPE").equals("LE")){%>
								<tr>
									<th style="text-align: right;" nowrap="nowrap">Transfer Type:</th>
									<td nowrap="nowrap"><%=uF.showData(hmActivityMap.get("TRANSFER_TYPE_NAME"),"") %></td>
								</tr>
								<tr>
									<th style="text-align: right;" nowrap="nowrap">Organisation:</th>
									<td nowrap="nowrap"><%=uF.showData(hmActivityMap.get("ORG_NAME"),"") %></td>
								</tr>
								<tr>
									<th style="text-align: right;" nowrap="nowrap">Work Location:</th>
									<td nowrap="nowrap"><%=uF.showData(hmActivityMap.get("WORK_LOCATION_NAME"),"") %></td>
								</tr>
								<tr>
									<th style="text-align: right;" nowrap="nowrap">SBU:</th>
									<td nowrap="nowrap"><%=uF.showData(hmActivityMap.get("SERVICE_NAME"),"") %></td>
								</tr>
								<tr>
									<th style="text-align: right;" nowrap="nowrap">Department:</th>
									<td nowrap="nowrap"><%=uF.showData(hmActivityMap.get("DEPARTMENT_NAME"),"") %></td>
								</tr>
								<tr>
									<th style="text-align: right;" nowrap="nowrap">Level:</th>
									<td nowrap="nowrap"><%=uF.showData(hmActivityMap.get("LEVEL_NAME"),"") %></td>
								</tr>
								<tr>
									<th style="text-align: right;" nowrap="nowrap">Designation:</th>
									<td nowrap="nowrap"><%=uF.showData(hmActivityMap.get("DESIG_NAME"),"") %></td>
								</tr>
								<tr>
									<th style="text-align: right;" nowrap="nowrap">Grade:</th>
									<td nowrap="nowrap"><%=uF.showData(hmActivityMap.get("GRADE_NAME"),"") %></td>
								</tr>
							<%} %>
						<%} %>
					</table>
				</div>
			<%}%>
			
		
		<div style="float: left; width: 100%; margin-bottom: -7px; margin-top: 20px;">
			<table style="width:100%;" class="table table_no_border">
				<%
					if (!flag) {
				%>
						<tr style="height: 10px;"> <!-- class="txtlabel alignRight" -->
							<td colspan="2"><div style="float: left;">Send to Gap <s:checkbox name="sendtoGapStatus" id="sendtoGapStatus"/></div> </td>
						</tr>
						<tr style="height: 10px;">
							<td colspan="2"><div style="float: left;">Award Certificate <s:checkbox name="certificateStatus" id="certificateStatus"/></div> </td>
						</tr>
						<tr style="height: 10px;">
							<td colspan="2"><div style="float: left;"> Award Thumbsup <s:checkbox name="thumbsupStatus" id="thumbsupStatus"/> </div></td>
						</tr>
						<tr>
							<td style="text-align: right" valign="top" width="10%"><b>Remark:<sup>*</sup></b>&nbsp;</td>
							<td><s:textarea name="finalizeRemark" rows="7" cssClass="validateRequired" cols="80" ></s:textarea></td>
						</tr>
				
				<%} else {
						String finalizeRemark = (String) request.getAttribute("finalizeRemark");
						String sendtoGapStatus = (String)request.getAttribute("sendtoGapStatus");
						String certificateStatus = (String)request.getAttribute("certificateStatus");
						String thumbsupStatus = (String)request.getAttribute("thumbsupStatus");
				%>
						<tr style="height: 10px;">
							<td colspan="2"><div style="float: left;">Send to Gap <%-- <s:checkbox name="sendtoGapStatus" id="sendtoGapStatus" disabled="true" <%=uF.parseToBoolean(sendtoGapStatus) == true ? "checked" : "" %> /> --%>
							<input type="checkbox" name="sendtoGapStatus" id="sendtoGapStatus" disabled="disabled" <%=uF.parseToBoolean(sendtoGapStatus) == true ? "checked" : "" %> />
							</div> </td>
						</tr>
						<tr style="height: 10px;">
							<td colspan="2"><div style="float: left;">Award Certificate <input type="checkbox" name="certificateStatus" id="certificateStatus" disabled="disabled" <%=uF.parseToBoolean(certificateStatus) == true ? "checked" : "" %> /></div> </td>
						</tr>
						<tr style="height: 10px;">
							<td colspan="2"><div style="float: left;">Award Thumbsup <input type="checkbox" name="thumbsupStatus" id="thumbsupStatus" disabled="disabled" <%=uF.parseToBoolean(thumbsupStatus) == true ? "checked" : "" %> /></div> </td>
						</tr>
						<tr>
							<td style="text-align: right" valign="top" width="10%"><b>Remark:</b>&nbsp;</td>
							<td>
								<div style="margin-right: 10%; float: left;"><%=finalizeRemark != null ? finalizeRemark : ""%></div>
							</td>
						</tr>
						
						<tr>
							<td colspan="2" align="right">
							<div style="float: right; margin-right: 10%;">Finalised by- <strong><i> <%=finalizedBy%></i></strong></div>
							</td>
						</tr>			
				<%}%>
			</table>
		</div>
		
		<% if ( hmActivityMap == null || hmActivityMap.isEmpty()) {%> 
			<div class="box box-none nav-tabs-custom clr">
			<%	String dataType = (String) request.getAttribute("dataType");
				String strLabel = "";
				String urlA = "LearningPlanAssessmentFinalize.action?dataType=A&learningId=" + (String) request.getAttribute("learningId") + "&empid=" + (String) request.getAttribute("empid") + "&trainingId=" + (String) request.getAttribute("trainingId")+ "&assessmentId=" + (String) request.getAttribute("assessmentId") + "&remarktype=" + (String) request.getAttribute("remarktype");
				String urlD = "LearningPlanAssessmentFinalize.action?dataType=D&learningId=" + (String) request.getAttribute("learningId") + "&empid=" + (String) request.getAttribute("empid") + "&trainingId=" + (String) request.getAttribute("trainingId")+ "&assessmentId=" + (String) request.getAttribute("assessmentId") + "&remarktype=" + (String) request.getAttribute("remarktype");
				
				if (!flag) { 
			%>
				
					<%if(dataType == null || dataType.equals("A")) { 
						strLabel ="Activity";
					%>
						<ul class="nav nav-tabs" >
							<li class="active"><a href="javascript:void(0)"
								onclick="getTabContent('activity')" data-toggle="tab">Activity</a>
							</li>
							<li><a href="javascript:void(0)"
								onclick="getTabContent('activitywdoc')" data-toggle="tab">Activity W/Doc</a>
							</li>
						</ul>
					<% } else if(dataType != null && dataType.equals("D")) { 
						   strLabel ="Activity W/Doc";
								%>
							<ul class="nav nav-tabs">
								<li><a href="javascript:void(0)"
									onclick="getTabContent('activity')" data-toggle="tab">Activity</a>
								</li>
								<li class="active"><a href="javascript:void(0)"
									onclick="getTabContent('activitywdoc')" data-toggle="tab">Activity
										W/Doc</a>
								</li>
							</ul>
							
					<% }%>
					<script>
						function getTabContent(condition){
							
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
			<%  } %>

			
		
			<s:hidden name="strEmpId2" id="strEmpId2"/>
			<s:hidden name="dataType"></s:hidden>
			<div id="activity_div" style=" width:100%;" class="tab-content">
				<table class="table table_no_border autoWidth">
				<tr>
			    	<td class="txtlabel" colspan="2"><strong>New <%=strLabel %> Information</strong></td>
			    	
			    </tr>
				
				<tr>
					<td colspan=4><s:fielderror />
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
					<td><s:textfield name="effectiveDate" id="idEffectiveDate" cssClass="validateRequired" /><span class="hint">Add the effective date. This will be displayed in time-sheets and clock entries.<span class="hint-pointer">&nbsp;</span></span></td>
				</tr>
				
				<tr id="extendProbationTR">
					<td class="txtlabel alignRight">No. of Days:<sup>*</sup></td>
					<td><s:textfield name="strExtendProbationDays" id= "strExtendProbationDays" cssClass="validateRequired" onkeypress="return isOnlyNumberKey(event)" onkeyup="checkNoOfDays('E')"/> </td>
					<td class="txtlabel alignRight"></td>
					<td></td>
				</tr>
				
				<tr id="tranferTypeTR"> 
					<td class="txtlabel alignRight">Select Transfer Type:<sup>*</sup></td>
					<td ><s:select name="strTransferType" id="strTransferType" theme="simple" cssClass="validateRequired" headerKey="" headerValue="Select Transfer Type" 
					list="#{'WL':'Work Location', 'DEPT':'Department', 'LE':'Legal Entity'}" onchange="selectElements1(this.value)" />  </td>
					<td class="txtlabel alignRight"></td>
					<td></td>
				</tr>
				
				<tr id="incrementTypeTR">
					<td class="txtlabel alignRight">Select Increment Type:<sup>*</sup></td>
					<td ><s:select name="strIncrementType" id="strIncrementType" theme="simple" cssClass="validateRequired" headerKey="" headerValue="Select Increment Type" 
					list="#{'1':'Single', '2':'Double'}" onchange="showIncrementPercent(this.value)" />  </td>
					<td class="txtlabel alignRight"></td>
					<td></td>
				</tr>
				
				<tr id="legalEntityTR">
					<td class="txtlabel alignRight">Legal Entity:<sup>*</sup></td>
					<td><s:select name="strOrganisation" id="strOrganisation" theme="simple" listKey="orgId" cssClass="validateRequired" listValue="orgName" 
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
					<td id="desigVTD"> <span id="desigListSpan">
					<s:select theme="simple" name="strDesignation" id="strDesignation" listKey="desigId" cssClass="validateRequired" listValue="desigCodeName" 
						headerKey="" headerValue="Select Designation" list="desigList" key="" required="true" onchange="getGrades(this.value);" />
						</span>
					</td>
					
					<td class="txtlabel alignRight" id="gradeLTD">Grade:<sup>*</sup>
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
					
					<td class="txtlabel alignRight" id="gradeChangeLTD">Grade:<sup>*</sup>
					<s:hidden name="strGrade"/>
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
					<td> <s:textfield name="strNoticePeriod" id="strNoticePeriod" cssClass="validateRequired" onkeypress="return isOnlyNumberKey(event)" onkeyup="checkNoOfDays('N')"/> </td>
					
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
						<s:textfield name="strIncrementPercentage" id="strIncrementPercentage" cssClass="validateRequired" maxlength="5" 
						onkeyup="incrementBasicAmount(this.id)" onkeypress="return isNumberKey(event)" cssStyle="width:50px;"/>
					</td>
					
					<td class="txtlabel alignRight"></td>
					<td></td>
				</tr>
			
				
				
				<tr>
					<td class="txtlabel alignRight" valign="top" id="reasonLTD">Reason:<sup>*</sup></td>
					<td colspan="3" id="reasonVTD">
					<s:textarea name="strReason" rows="10" cssClass="validateRequired" cols="60" ></s:textarea><span class="hint">Add the reason for this activity for reference.<span class="hint-pointer">&nbsp;</span></span>
					</td>
					
				</tr>
					<tr>
						<td>&nbsp;</td>
						<td colspan="3">
							<s:submit name="submit" value="Finalise" cssClass="btn btn-primary"></s:submit> 
						</td>
					</tr>
				</table>
			</div>
			</div>
			<div id="salaryDetailsDiv" ></div>
		<%} %>
		
	</s:form>
</div>

<script>
disableAll();

disableAll1();

$("input[type = 'submit']").click(function(){
	$("#"+this.form.id).find('.validateRequired').filter(':hidden').prop('required',false);
	$("#"+this.form.id).find('.validateRequired').filter(':visible').prop('required',true);
});

$("#frmLearningPlanAssessmentFinalize").submit(function(event){
	event.preventDefault();
	var learningId = document.getElementById("learningId").value;
	var form_data = $("#frmLearningPlanAssessmentFinalize").serialize();
	 $("#divLPDetailsResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({ 
		type : 'POST',
	//	url: strAction+'.action?appId='+appId+'&appFreqId='+appFreqId+'&fromPage='+fromPage+'&appsystem='+appsystem,
		url: "LearningPlanAssessmentFinalize.action",
		data: form_data+"&submit=Finalise",
		cache: true /* ,
		success: function(result){
			 $("#divLPDetailsResult").html(result);
   		} */
	});
	
	$.ajax({
		url: 'LearningPlanAssessmentStatus.action?lPlanId='+learningId,
		cache: true,
		success: function(result){
			$("#divLPDetailsResult").html(result);
   		}
	});
	
});
</script>




