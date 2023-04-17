<%@page import="java.util.HashMap"%>
<%@page import="com.konnect.jpms.select.FillEmployee"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<script type="text/javascript" src="scripts/customAjax.js"></script>

<!-- <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.10.0/css/bootstrap-select.min.css" /> -->
<%
	UtilityFunctions uF=new UtilityFunctions(); //Created by Dattatray Date:05-July-21
%>
<script>
	$(function(){
		
		$("input[type='submit']").click(function(){
			$('.validateRequired').filter(':hidden').prop('required',false);
			$('.validateRequired').filter(':visible').prop('required',true);
		});
		
		/* $("body").on('click',"input[type='submit']",function(){
			$("#formID").find('.validateRequired').filter(':hidden').prop('required', false);
			$("#formID").find('.validateRequired').filter(':visible').prop('required', true);
		}); */
		
	    /* $("#rdate").datepicker({
	    	format : 'dd/mm/yyyy'
	    });
	    
	    $("#targetdeadline").datepicker({
	    	format : 'dd/mm/yyyy'
	    }); */
	    
	    /* ===start parvez date: 14-10-2021=== */
		$("#rdate").datepicker({
	        format: 'dd/mm/yyyy',
	        autoclose: true
	    }).on('changeDate', function (selected) {
	        var minDate = new Date(selected.date.valueOf());
	        $('#targetdeadline').datepicker('setStartDate', minDate);
	    });
	    
	    $("#targetdeadline").datepicker({
	    	format: 'dd/mm/yyyy',
	    	autoclose: true
	    }).on('changeDate', function (selected) {
	            var minDate = new Date(selected.date.valueOf());
	            $('#rdate').datepicker('setEndDate', minDate);
	    });
		/* ===end parvez date: 14-10-2021=== */
	    
	    //$("#strDesignationUpdate").multiselect().multiselectfilter();
	    $("#formID_skills").multiselect().multiselectfilter();
	    $("#hiringManager").multiselect().multiselectfilter();
	    $("#formID_essentialSkills").multiselect().multiselectfilter();
	  
    	
    	$("#lt1").DataTable();
    	
	   	onloadFilterByOrg();
	   	onloadFilterByOrg1();
	   	
	   	$( document ).on('ajaxComplete',function() {
	   		$("select[multiple=multiple]").multiselect().multiselectfilter();
    	});
	   	
   		var recruitmentID = document.getElementById("recruitmentID").value;
   		if(parseInt(recruitmentID) > 0) {
   			var strOrg = document.getElementById("organisation").value;
   			var strWlocation = document.getElementById("locationid").value;
   			var level = document.getElementById("strLevel").value;
   			window.setTimeout(function() {
   	    		var action1='GetRecruitmentPolicyDetails.action?strLevel='+level+'&strOrg='+strOrg+'&strWlocation='+strWlocation+'&recruitmentID='+recruitmentID;
   				$.ajax({
   	    			url : action1,
   	    			success : function(data) {
   	    				//alert("data ===>> " + data);
   	    				$("#policyid").nextAll('tr').remove();
   	    				$(data).insertAfter("#policyid");
   	    			}
   	    		});
   			 }, 300);
   		}
   	});
    	
   	function getGradebyDesig(desig,type){
     		//alert("desig " + desig + " type " + type);
   		getContent('myGrade1','GetGradefromDesig.action?strDesignation='+desig);
   		window.setTimeout(function() {
   			getPlannedbyDesig(desig,type);
   		}, 200);
   		//alert("desig 1 " + desig + " type 1 " + type);
   		if(type=="add" && desig == "0"){
   			//alert("desig if " + desig + " type if " + type);
   			//alert(document.getElementById("strDesignationUpdate").length);
   			document.getElementById("strDesignationUpdate").disabled=true;
   			document.getElementById("addDesigDiv").style.display="none";
   			document.getElementById("resetDesigDiv").style.display="block";
   			$("#custumdesig").addClass("validateRequired");
   			//alert("desig if 1 " + desig + " type if 1 " + type);
   			var desigLen = document.getElementById("strDesignationUpdate").length;
   			//alert("desigLen ===> " + desigLen);
   			document.getElementById("strDesignationUpdate").selectedIndex = parseInt(desigLen)-1;
   			
   			document.getElementById("customDesigTR").style.display="table-row";
   			document.getElementById("customGradeTR").style.display="table-row";
   			$("#custumgrade").addClass("validateRequired");
   			document.getElementById("existGradeTR").style.display="none";
   			document.getElementById("custumdesig").value='';
   			document.getElementById("custumgrade").value='';
   		}else{
   			//alert("desig else " + desig + " type else " + type);
   			if(type=="reset"){
   				document.getElementById("strDesignationUpdate").disabled=false;	
   				document.getElementById("strDesignationUpdate").selectedIndex = 0;	
   			}
   			document.getElementById("addDesigDiv").style.display="block";
   			document.getElementById("resetDesigDiv").style.display="none";
   			$("#custumdesig").removeClass("validateRequired");
   			$("#custumdesig").prop('required',false);
   			document.getElementById("customDesigTR").style.display="none";
   			document.getElementById("customGradeTR").style.display="none";
   			$("#custumgrade").prop('required',false);
   			document.getElementById("existGradeTR").style.display="table-row";
   			document.getElementById("custumdesig").value='';
   			document.getElementById("custumdesig").value='';
   			document.getElementById("desigValidatorMessege").innerHTML = "";
   			//alert("desig else end " + desig + " type else end " + type);
   		}
   	/* 	document.getElementById("gradeIdV").selectedIndex = 0;	 */
   
   		window.setTimeout(function() {
   			getDesigRequiredAttribute(desig, type);
   		}, 300);
   		
   	}
   	
   	
   	function getDesigRequiredAttribute(desigID, type) {
   		//alert("value ===> "+value);
   	     xmlhttp = GetXmlHttpObject();
   	     if (xmlhttp == null) {
   	             alert("Browser does not support HTTP Request");
   	             return;
   	     } else {
   	             var xhr = $.ajax({
   	               url : "DesigRequiredAttribute.action?desigID=" + desigID+ "&type=" + type,
   	               cache : false,
   	               success : function(data) {
   	               	//alert("data ===> "+data);
               	   if(data == "") {
                      	} else {
                      		var allData = data.split("::::");
	   	               		document.getElementById("genderTD").innerHTML = allData[0];
	   	                    document.getElementById("minAgeSpan").innerHTML = allData[1];
	   	                 	document.getElementById("jobDescription").value = allData[2];
	   	                 	document.getElementById("idealCandidate").value = allData[3];
                      	}
   	               }
   	             });
   	     	}
   		}
   	
   				
   	function getDesig(level){
   		getContent('myDesig1','GetDesigfromLevel.action?pagefrom=RRequest&strLevel='+level);
   		/* window.setTimeout(function() {
   			$("#strDesignationUpdate").multiselect().multiselectfilter();
		}, 500); */
   /* 		document.getElementById("strDesignationUpdate").selectedIndex = 0;	 */
   		var recruitmentID = document.getElementById("recruitmentID").value;
   		//alert(recruitmentID);
   		//Created by Dattatray Date : 12-July-2021 Note : recruitmentID
   	   	if(parseInt(level) > 0) { // && (recruitmentID=='' || parseInt(recruitmentID) == 0)  && parseInt(recruitmentID) > 0 
   			var strOrg=document.getElementById("organisation").value;
   			var strWlocation=document.getElementById("locationid").value;
   			//alert("strOrg ===>> " + strOrg);
   			//alert("strWlocation ===>> " + strWlocation);
   			window.setTimeout(function() {
   	    		var action1='GetRecruitmentPolicyDetails.action?strLevel='+level+'&strOrg='+strOrg+'&strWlocation='+strWlocation+'&recruitmentID='+recruitmentID;
   	    		$.ajax({
   	    			url : action1,
   	    			success : function(data) {
   	    				//alert("data ===>> " + data);
   	    				$("#policyid").nextAll('tr').remove();
   	    				$(data).insertAfter("#policyid");
   	    			}
   	    		});
   	    		/* $( "#strDesignationUpdate" ).combobox(); */
   			 }, 700);
   		}
   	}
   	
   	/* function customDesigFill(){
   		getContent('myGrade','GetGradefromDesig.action?strDesignation=');
   		document.getElementById("strDesignationUpdate").selectedIndex=0;
   	} */
   	
   	function getPlannedbyDesig(desig){
   		
   		var date=document.getElementById("rdate").value;
   		var action='GetPlannedAjax.action?designation='+desig+'&date='+date;
   		getContent('Planned', action);
   		getDesigMinAndMaxCTC(desig);
   	}
   	
   	function getPlannedbyDate(date){
   		var desig=document.getElementById("strDesignationUpdate").value;
   		var action='GetPlannedAjax.action?designation='+desig+'&date='+date;
   		getContent('Planned', action);
    		
    }
    	
    	function getLocationOrg(orgid){
    		var action='GetLocationOrg.action?strOrg='+orgid;
    		getContent('locationdivid', action);
    		getLevelByOrg(orgid);
    		
    		/* window.setTimeout(function() {
    			getSkillsByOrg(orgid);
    		}, 300);*/
    		
    	}
    	
    	function getServicesByOrg(orgid){
    		var action='GetServicesByOrg.action?strOrg='+orgid ;
    		getContent('servicedivid', action);
    		
    	}
    	
    	function getLevelByOrg(orgid){
    		//alert("orgid== "+ orgid);
    		var action='GetLevelByOrg.action?strOrg='+orgid ;
    		getContent('leveldivid', action);
    		
    		window.setTimeout(function() {
    			getSkillsByOrg(orgid);
    		}, 300);		
    	}
    	
    	function getSkillsByOrg(orgid){
    		//alert("abcd");
    		var action='GetSkillsByOrg.action?strOrg='+orgid ;
    		//getContent('skilldivid', action);
    		//alert("action====>"+action);
    		 xmlhttp = GetXmlHttpObject();
    	     if (xmlhttp == null) {
                 alert("Browser does not support HTTP Request");
                 return;
    	     } else {
                 var xhr = $.ajax({
                   url : 'GetSkillsByOrg.action?strOrg='+orgid,
                   cache : false,
                   success : function(data) {
                   	//alert("data ===> "+data);
    	           	   if(data == "") {
    	               } else {
    	               		var allData = data.split("::::");    
    	               		document.getElementById("essentialskilldivid").innerHTML = allData[0];
    	                    document.getElementById("skilldivid").innerHTML = allData[1];
    	               }
                  }
                });
    	     }
    	}
    	
    	
    		
    	
    	function getIdealCandidateDetails(desigid){
    		var action='GetIdealCandidateDesig.action?strDesig='+desigid ;
    		getContent('idealdivid', action);
    	}
    
    	
    	function checkExistDesig(value) {
    		//alert("value ===> "+value);
    		var strLevel = document.getElementById("strLevel").value;
    	     xmlhttp = GetXmlHttpObject();
    	     if (xmlhttp == null) {
    	             alert("Browser does not support HTTP Request");
    	             return;
    	     } else {
    	             var xhr = $.ajax({
    	               url : "DesigValidation.action?desigName=" + value+"&strLevel="+strLevel,
    	               cache : false,
    	               success : function(data) {
    	               	//alert("data.length ===> "+data.length + "  data ===> "+data);
    	               	if(data.length > 1){
    	               		document.getElementById("custumdesig").value = "";
    	                    document.getElementById("desigValidatorMessege").innerHTML = data;
    	               	}else{
    	               		document.getElementById("desigValidatorMessege").innerHTML = data;
    	               	}
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
    	
    	
    	function showTempCasualJastification(value) {
    		//alert("value ===> "+value);
    	     if (value == "2" || value == "3") { //addtionalVacancyTR replacementVacancyTR
    	    	 document.getElementById("typeOfEmploymentTR").style.display = "table-row";
    	     } else {
    	    	 document.getElementById("typeOfEmploymentTR").style.display = "none";
    	     	}
    		}
    	
    	
    	function changeVacancyType(value) {
    		//alert("value ===> "+value);
    	     if (value == "1" || value == "3") { //addtionalVacancyTR replacementVacancyTR
    	    	 document.getElementById("replacementVacancyTR").style.display = "none";
    	    	 document.getElementById("replacementVacancyTR1").style.display = "none";
    	    	 //document.getElementById("addtionalVacancyTR").style.display = "table-row";
    	     } else {
    	    	 document.getElementById("replacementVacancyTR").style.display = "table-row";
    	    	 document.getElementById("replacementVacancyTR1").style.display = "table-row";
    	    	 //document.getElementById("addtionalVacancyTR").style.display = "none";
    	     	}
    		}
    	
    	 
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
    	
    	 
    	function checkUncheckValue() {
    		var allEmp=document.getElementById("allEmp");		
    		var strHiringEmpId = document.getElementsByName('strHiringEmpId');
    		var selectID="";
    		var status=false;
    		if(allEmp.checked==true){
    			status=true;
    			 for(var i=0;i<strHiringEmpId.length;i++){
    				 strHiringEmpId[i].checked = true;
    				  if(i==0){
    					  selectID=strHiringEmpId[i].value;
    				  }else{
    					  selectID+=","+strHiringEmpId[i].value;
    				  }
    			 }
    		}else{		
    			status=false;
    			 for(var i=0;i<strHiringEmpId.length;i++){
    				 strHiringEmpId[i].checked = false;
    				  if(i==0){
    					  selectID=strHiringEmpId[i].value;
    				  }else{
    					  selectID+=","+strHiringEmpId[i].value;
    				  }
    			 }
    		}
    		
    		var empselect=document.getElementById("empselected").value;
    		
    		var xmlhttp = GetXmlHttpObject();
    		if (xmlhttp == null) {
    			alert("Browser does not support HTTP Request");
    			return;
    		} else {
    			var xhr = $.ajax({
    				//url : "ValidateTimeSheet.action?emp_id=" + emp+"&timesheet_paycycle="+cycle,
    				url : "GetSelectedEmployeeForHiring.action?type=one&chboxStatus="+status+"&selectedEmp="+selectID+"&existemp="+empselect,		
    				cache : false,
    				success : function(data) {
    					//alert("data == "+data);
                    	if(data == ""){
                    		
                    	}else{
                    		var allData = data.split("::::");
                            document.getElementById("idEmployeeInfo").innerHTML = allData[0];
                    	}
                    }
    			});
    		}
    		//var action='GetSelectedEmployee.action?type=all&chboxStatus='+status+'&selectedEmp='+selectID+'&existemp='+empselect;
    		//getContent('idEmployeeInfo',action); 
    		 
    	}
     
    	function onloadFilterByOrg(){
    		//alert("cbcbdf dfgd ");
    		var strID = null;
    		if(document.getElementById("strOrg")){
    		strID = getSelectedValue("strOrg"); 
    	    }
    		
    	    if(document.getElementById("hideOrgid")){
    	    	strID = document.getElementById("hideOrgid").value;
    	    }
    		getWLocDepartLevelDesigByOrg(strID, 'org');
    		var typeofEmp = document.getElementById("strEmploymentType").value;
    		showTempCasualJastification(typeofEmp);
    
    		var vacancy = document.getElementById("vacancy").value;
    		//alert(vacancyHidden);
    		changeVacancyType(vacancy);
    	}
    	
    	
    	function getWLocDepartLevelDesigByOrg(strID, type){
    		//alert("strID ===> " + strID);
    		var xmlhttp = GetXmlHttpObject();
    		if (xmlhttp == null) {
    			alert("Browser does not support HTTP Request");
    			return;
    		} else {
    			var xhr = $.ajax({
    				//url : "ValidateTimeSheet.action?emp_id=" + emp+"&timesheet_paycycle="+cycle,
    				url : "GetHiringFilters.action?strOrg="+strID+"&type="+type ,		
    				cache : false,
    				success : function(data) {
    					//alert("data == "+data);
                    	if(data == "") {
                    		
                    	} else {
                    		//alert("data ==>    "+data);
                    		var allData = data.split("::::");
                    		  if(type =='org'){
    		                        document.getElementById("wlocationDiv").innerHTML = allData[0];
    		                        document.getElementById("departDiv").innerHTML = allData[1];
    		                        document.getElementById("levelDiv").innerHTML = allData[2];
    		                        document.getElementById("myDesig").innerHTML = allData[3];
                    		  } else if(type =='wloc') {
    		                        document.getElementById("departDiv").innerHTML = allData[0];
                    		  } else if(type =='level') {
    		                        document.getElementById("myDesig").innerHTML = allData[0];
                    		  }
                    	}
                    }
    			});
    		}
    	}
    	
    	 
    	function getEmployeebyOrg(){
    		var strID = null;
    		if(document.getElementById("strOrg")){
    		strID = getSelectedValue("strOrg"); 
    	    }
    		
    	    if(document.getElementById("hideOrgid")){
    	    	strID = document.getElementById("hideOrgid").value;
    	    }
    	    //alert("strID ===> " +  strID);
    		var action = 'GetHiringEmployeeList.action?strOrg=' + strID;
    		var cnt=0;
    	
    		var rslt = getContent('myEmployee', action);
    		cnt++;
    		getWLocDepartLevelDesigByOrg(strOrg,'org');
    		//alert("rslt ===> " + rslt);
    		if(parseInt(cnt) != 0){
    		//searchTextField();
    		}
    	} 
    	 
    	function getEmployeebyLocation() {
    		var location = getSelectedValue("wlocation");
    		var strID = null;
    		if(document.getElementById("strOrg")){
    		 strID = getSelectedValue("strOrg"); 
    	    }
    	    if(document.getElementById("hideOrgid")){
    	    	strID = document.getElementById("hideOrgid").value;
    	    }
    		var action = 'GetHiringEmployeeList.action?strOrg='+ strID +'&location='+ location;
    
    	  	getContent('myEmployee', action);
    		getWLocDepartLevelDesigByOrg(location,'wloc');
    		//searchTextField();
    	}
    
    	function getEmployeebyDepart() {
    		var location = getSelectedValue("wlocation");
    		var depart = getSelectedValue("depart");
    		
    		var strID = null;
    		if(document.getElementById("strOrg")){
    		 strID = getSelectedValue("strOrg"); 
    	    }
    	    if(document.getElementById("hideOrgid")){
    	    	strID = document.getElementById("hideOrgid").value;
    	    }
      		
    		var action = 'GetHiringEmployeeList.action?depart=' + depart;
    		
    		/* if (location == '') {
    		} else { */
    			if (strID != '') {
    				action += '&strOrg='+ strID;
    			}
    			if (location != '') {
    				action += '&location=' + location;
    			}
    			getContent('myEmployee', action);
    		/* } */
    		//searchTextField();
    	}
    
    	function getEmployeebyLevel() {
    		var location = getSelectedValue("wlocation");
    		var depart = getSelectedValue("depart");
    		var level = getSelectedValue("strLevel1");
    		
    		var strID = null;
    		if(document.getElementById("strOrg")){
    		 strID = getSelectedValue("strOrg"); 
    	    }
    	    if(document.getElementById("hideOrgid")){
    	    	strID = document.getElementById("hideOrgid").value;
    	    }
    		
    		var action = 'GetHiringEmployeeList.action?level=' + level;
    		
    		if (strID == '' && location == '' && depart == '') {
    		} else {
    			if (strID != '') {
    				action += '&strOrg='+ strID;
    			}
    			if (location != '') {
    				action += '&location=' + location;
    			}
    			if (depart != '') {
    				action += '&depart=' + depart;
    			}
    		}
    			getContent('myEmployee', action);
    			getWLocDepartLevelDesigByOrg(level,'level');
    			
    			//searchTextField();
    
    	}
    
    	function getEmployeebyDesig() {
    		var location = getSelectedValue("wlocation");
    		var depart = getSelectedValue("depart");
    		var Level = getSelectedValue("strLevel1");
    		var design = getSelectedValue("desigIdV");
    		var strID = null;
    		if(document.getElementById("strOrg")){
    		 	strID = getSelectedValue("strOrg"); 
    	    }
    	    if(document.getElementById("hideOrgid")){
    	    	strID = document.getElementById("hideOrgid").value;
    	    }
    
    		//document.getElementById("gradeIdV").selectedIndex = 0;
    		//document.getElementById("employee").selectedIndex = 0;
    		//alert("getEmployeebyDesig ... ");
    		var action = 'GetHiringEmployeeList.action?design=' + design;
    		
    		if (strID == '' && location == '' && depart == '' && Level == '') {
    
    		} else {
    			if (strID != '') {
    				action += '&strOrg='+ strID;
    			}
    			if (location != '') {
    				action += '&location=' + location;
    			}
    			if (depart != '') {
    				action += '&depart=' + depart;
    			}
    			if (Level != '') {
    				action += '&level=' + Level;
    			}
    		}
    			//alert("action ==> " + action); 
    			getContent('myEmployee', action);
    			window.setTimeout(function() {
    				getContent('myGrade', 'getGrade.action?strDesignation=' + design);
    			}, 200);
    			//searchTextField();
    	}
    	
    	 
    	function getEmployeebyGrade() {
    		var location = getSelectedValue("wlocation");
    		var depart = getSelectedValue("depart");
    		var Level = getSelectedValue("strLevel1");
    		var design = getSelectedValue("desigIdV");
    		var grade = getSelectedValue("gradeIdV");
    		
    		var strID = null;
    		if(document.getElementById("strOrg")){
    		 	strID = getSelectedValue("strOrg"); 
    	    }
    	    if(document.getElementById("hideOrgid")){
    	    	strID = document.getElementById("hideOrgid").value;
    	    }
    
    		var action = 'GetHiringEmployeeList.action?grade=' + grade;
    
    		//document.getElementById("employee").selectedIndex = 0;
    	
    			if (strID != '') {
    				action += '&strOrg='+ strID;
    			}
    			if (location != '')  {
    				action += '&location=' + location;
    			}if (depart != '')  {
    				action += '&depart=' + depart;
    			}if (Level != '')  {
    				action += '&level=' + Level;
    			}if (design != '')  {
    				action += '&design=' + design;
    			}
    			
    			getContent('myEmployee', action);
    			//searchTextField();
    	}
    	
    	
    function getHiringSelectedEmp(checked,emp){
    		
    	if(checked == true) {
    		alrtMsg = "Are you sure, you want to add this employee?";
    	} else {
    		alrtMsg = "Are you sure, you want to remove this employee?";
    	}
    	if(confirm(alrtMsg)) {
    		
    		var empselect=document.getElementById("empselected").value;
    		
    		var xmlhttp = GetXmlHttpObject();
    		if (xmlhttp == null) {
    			alert("Browser does not support HTTP Request");
    			return;
    		} else {
    			var xhr = $.ajax({
    				//url : "ValidateTimeSheet.action?emp_id=" + emp+"&timesheet_paycycle="+cycle,
    				url : "GetSelectedEmployeeForHiring.action?type=one&chboxStatus="+checked+"&selectedEmp="+emp+"&existemp="+empselect,		
    				cache : false,
    				success : function(data) {
    					//alert("data == "+data);
                    	if(data == ""){
                    		
                    	}else{
                    		var allData = data.split("::::");
                            document.getElementById("idEmployeeInfo").innerHTML = allData[0];
                    	}
                    }
    			});
    		} 
    	}
    }
    
    
    	function openPanelEmpProfilePopup(empId) {
    		
    		var dialogEdit = '#modal-body1';
    		 $(dialogEdit).empty();
    		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    		 $("#modalInfo1").show();
    		 $(".modal-title1").html('Employee Information');
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
    	
    	
    	
    	
    	function checkUncheckValue1() {
    		var allEmp=document.getElementById("allEmp1");		
    		var strHiringEmpId1 = document.getElementsByName('strHiringEmpId1');
    		var selectID="";
    		var status=false;
    		if(allEmp.checked==true){
    			status=true;
    			 for(var i=0;i<strHiringEmpId1.length;i++){
    				 strHiringEmpId1[i].checked = true;
    				  if(i==0){
    					  selectID=strHiringEmpId1[i].value;
    				  }else{
    					  selectID+=","+strHiringEmpId1[i].value;
    				  }
    			 }
    		}else{		
    			status=false;
    			 for(var i=0;i<strHiringEmpId1.length;i++){
    				 strHiringEmpId1[i].checked = false;
    				  if(i==0){
    					  selectID=strHiringEmpId1[i].value;
    				  }else{
    					  selectID+=","+strHiringEmpId1[i].value;
    				  }
    			 }
    		}
    		
    		var empselect1=document.getElementById("empselected1").value;
    		
    		var xmlhttp = GetXmlHttpObject();
    		if (xmlhttp == null) {
    			alert("Browser does not support HTTP Request");
    			return;
    		} else {
    			var xhr = $.ajax({
    				//url : "ValidateTimeSheet.action?emp_id=" + emp+"&timesheet_paycycle="+cycle,
    				url : "GetSelectedEmployeeForHiring1.action?type=one&chboxStatus="+status+"&selectedEmp="+selectID+"&existemp="+empselect1,		
    				cache : false,
    				success : function(data) {
    					//alert("data == "+data);
                    	if(data == ""){
                    		
                    	}else{
                    		var allData = data.split("::::");
                            document.getElementById("idEmployeeInfo1").innerHTML = allData[0];
                    	}
                    }
    			});
    		}
    		
    	}
     
    	function onloadFilterByOrg1(){
    		//alert("cbcbdf dfgd ");
    		var strID = null;
    		if(document.getElementById("strOrg1")){
    			//strID = getSelectedValue("strOrg1");
    			strID = document.getElementById("strOrg1").value; 
    	    }
    	    getWLocDepartLevelDesigByOrg1(strID);
    	    
    	    var reporttoHidden = document.getElementById("reporttoHidden").value;
    	    changeReportType(reporttoHidden);
    	}
    	 
    	
    	function getWLocDepartLevelDesigByOrg1(strID){
    		//alert("strID ===> " + strID);
    		var xmlhttp = GetXmlHttpObject();
    		if (xmlhttp == null) {
    			alert("Browser does not support HTTP Request");
    			return;
    		} else {
    			var xhr = $.ajax({
    				//url : "ValidateTimeSheet.action?emp_id=" + emp+"&timesheet_paycycle="+cycle,
    				url : "GetHiringFilters1.action?strOrg="+strID ,		
    				cache : false,
    				success : function(data) {
    					//alert("data == "+data);
                    	if(data == ""){
                    		
                    	}else{
                    		//alert("data ==>    "+data);
                    		var allData = data.split("::::");
                            document.getElementById("wlocationDiv1").innerHTML = allData[0];
                            document.getElementById("departDiv1").innerHTML = allData[1];
                            document.getElementById("levelDiv1").innerHTML = allData[2];
                            document.getElementById("myDesig11").innerHTML = allData[3];
                    		  
                    	}
                    }
    			});
    		}
    		
    		window.setTimeout(function() {  
    			getEmployeebyOrg1();
    		}, 200); 
    		
    	}
    	
    	 
    	function getEmployeebyOrg1(){
    		var strID = null;
    		if(document.getElementById("strOrg1")){
    			//strID = getSelectedValue("strOrg1");
    			strID = document.getElementById("strOrg1").value; 
    	    }
    		
    		var action = 'GetHiringEmployeeList1.action?strOrg=' + strID;
    		var cnt=0;
    	
    		var rslt = getContent('myEmployee1', action);
    		cnt++;
    	
    		if(parseInt(cnt) != 0){
    			//searchTextField();
    		}
    	} 
    	 
    	function getEmployeebyLocation1() {
    		var location = getSelectedValue("wlocation1");
    		var strID = null;
    		if(document.getElementById("strOrg1")){
    			//strID = getSelectedValue("strOrg1");
    			strID = document.getElementById("strOrg1").value; 
    	    }
    		document.getElementById("depart1").selectedIndex = 0;
    		document.getElementById("strLevel11").selectedIndex = 0;
    		document.getElementById("desigIdV1").selectedIndex = 0;
    	    var action = 'GetHiringEmployeeList1.action?strOrg='+ strID +'&location='+ location;
       		getContent('myEmployee1', action);
    		
       	//searchTextField();
    	}
    
    	function getEmployeebyDepart1() {
    		var strID = null;
    		if(document.getElementById("strOrg1")){
    			//strID = getSelectedValue("strOrg");
    			strID = document.getElementById("strOrg1").value; 
    	    }
    		var location = getSelectedValue("wlocation1");
    		var depart = getSelectedValue("depart1");
    
    		document.getElementById("strLevel11").selectedIndex = 0;
    		document.getElementById("desigIdV1").selectedIndex = 0;
    	
    		var action = 'GetHiringEmployeeList.action?depart=' + depart+'&strOrg='+ strID +'&location='+ location;
    		
    		getContent('myEmployee1', action);
    		//searchTextField();
    	}
    
    	function getEmployeebyLevel1() {
    		var strID = null;
    		if(document.getElementById("strOrg1")){
    			//strID = getSelectedValue("strOrg");
    			strID = document.getElementById("strOrg1").value; 
    	    }
    		
    		var location = getSelectedValue("wlocation1");
    		var depart = getSelectedValue("depart1");
    		var level = getSelectedValue("strLevel11");
    		var action = 'GetHiringEmployeeList1.action?level=' + level;
    		
    		if (location == '' && depart == '' && strID == '') {
    		} else {
    			if (strID != '') {
    				action += '&strOrg=' + strID;
    			}
    			if (location != '') {
    				action += '&location=' + location;
    			}
    			if (depart != '') {
    				action += '&depart=' + depart;
    			}
    		}
    		getContent('myEmployee1', action);
    		//getWLocDepartLevelDesigByOrg1(level,'level');
    		window.setTimeout(function() {  
    			getContent('myDesig11', 'GetDesigfromLevel.action?pagefrom=RR&strLevel=' + level);
    		}, 200);
    			
    		//searchTextField();
    
    	}
    
    	function getEmployeebyDesig1() {
    		var strID = null;
    		if(document.getElementById("strOrg1")){
    			//strID = getSelectedValue("strOrg");
    			strID = document.getElementById("strOrg1").value; 
    	    }
    		var location = getSelectedValue("wlocation1");
    		var depart = getSelectedValue("depart1");
    		var Level = getSelectedValue("strLevel11");
    		var design = getSelectedValue("desigIdV1");
    
    		//alert("getEmployeebyDesig ... ");
    		var action = 'GetHiringEmployeeList1.action?design=' + design;
    		
    		if (location == '' && depart == '' && Level == '' && strID == '') {
    
    		} else {
    			if (strID != '') {
    				action += '&strOrg=' + strID;
    			}
    			if (location != '') {
    				action += '&location=' + location;
    			}
    			if (depart != '') {
    				action += '&depart=' + depart;
    			}
    			if (Level != '') {
    				action += '&level=' + Level;
    			}
    		}
    		//alert("action ==> " + action); 
    		getContent('myEmployee1', action);
    			/* window.setTimeout(function() {
    				getContent('myGrade', 'getGrade.action?strDesignation=' + design);
    			}, 200); */
    			//searchTextField();
    	}
    	
    	 
    	function getEmployeebyGrade1() {
    		var location = getSelectedValue("wlocation1");
    		var depart = getSelectedValue("depart1");
    		var Level = getSelectedValue("strLevel11");
    		var design = getSelectedValue("desigIdV1");
    		var grade = getSelectedValue("gradeIdV1");
    
    		var action = 'GetHiringEmployeeList1.action?grade=' + grade;
      			if (location != '') {
    				action += '&location=' + location;
    			} if (depart != '') {
    				action += '&depart=' + depart;
    			} if (Level != '') {
    				action += '&level=' + Level;
    			} if (design != '') {
    				action += '&design=' + design;
    			}
    			
    			getContent('myEmployee1', action);
    			//searchTextField();
    	}
    	
    	
    function getHiringSelectedEmp1(checked,emp){
    	
    	if(checked == true) {
    		alrtMsg = "Are you sure, you want to add this employee?";
    	} else {
    		alrtMsg = "Are you sure, you want to remove this employee?";
    	}
    	if(confirm(alrtMsg)) {
    		var empselect=document.getElementById("empselected1").value;
    		
    		var xmlhttp = GetXmlHttpObject();
    		if (xmlhttp == null) {
    			alert("Browser does not support HTTP Request");
    			return;
    		} else {
    			var xhr = $.ajax({
    				//url : "ValidateTimeSheet.action?emp_id=" + emp+"&timesheet_paycycle="+cycle,
    				url : "GetSelectedEmployeeForHiring1.action?type=one&chboxStatus="+checked+"&selectedEmp="+emp+"&existemp="+empselect,		
    				cache : false,
    				success : function(data) {
    					//alert("data == "+data);
                    	if(data == ""){
                    		
                    	}else{
                    		var allData = data.split("::::");
                            document.getElementById("idEmployeeInfo1").innerHTML = allData[0];
                    	}
                    }
    			});
    		} 
    	}
    }
    
   	function changeReportType(value) {
   		if (value == "Other") { 
   	    	document.getElementById("reportingToTR").style.display = "table-row";
   	    } else {
       	 	document.getElementById("reportingToTR").style.display = "none";
        }
   	}
    
    	
   	function getDesigMinAndMaxCTC(value) {
   		//alert("value ===> "+value);
   	     xmlhttp = GetXmlHttpObject();
   	     if (xmlhttp == null) {
                alert("Browser does not support HTTP Request");
                return;
   	     } else {
               var xhr = $.ajax({
   	        url : "GetMinMaxCTCOfDesig.action?strDesig=" + value,
   	        cache : false,
   	        success : function(data) {
   	        //alert("data.length ===> "+data.length + "  data ===> "+data);
   		        if(data == ""){
   		               		
   		        } else {
   		         	var allData = data.split("::::");
   					document.getElementById("strMinCTC").value = allData[0];
   					document.getElementById("strMaxCTC").value = allData[1];
   				}
   	        }
   			});
   		}
   	}
    	
   	function checkValue(postionNo) {
   		var val = document.getElementById("position").value;
   		if(parseInt(val) <= 0){
   			alert("Invalid value!");
   			document.getElementById("position").value="";
   		}
   		
   		// Start Dattatray Date:05-July-21
   		<%-- var outputCount = <%=uF.showData((String)request.getAttribute("Output"),"0")%>; --%>
 		// Start Dattatray Date:12-July-21 Note : Updated
   	 	var plannedCount = document.getElementById("intPlannedCount").value; 
   	 	/* console.log('outputCount : '+outputCount); */
   		var exceedPlan = 0;
   		if(parseInt(plannedCount) > 0){
   			exceedPlan =plannedCount;
   			console.log('plannedCount : '+exceedPlan);
   		}
		
   		if(parseInt(val) > parseInt(exceedPlan)){
			alert("No. of Positions requested exceeds Planned positions.");
		}// End Dattatray Date:05-July-21
   	}
    	 
   	function checkOtherCustomer(val) {
   		if(parseInt(val) == 0) {
   			document.getElementById("otherCustomerDiv").style.display = "block";
   		} else {
   			document.getElementById("otherCustomerDiv").style.display = "none";
   		}
   		
   		
		
   	}
   	
    	
   	function getClient(divid, val) {
   		var xmlhttp = GetXmlHttpObject();
   		if (xmlhttp == null) {
   			alert("Browser does not support HTTP Request");
   			return;
   		} else {
   			var xhr = $.ajax({
   				url : "GetClientName.action?strClientName="+ val,
   				cache : false,
   				success : function(data) {
   					document.getElementById(divid).innerHTML=data;
   					if(data.trim().length >1){
   						document.getElementById("strOtherCustomer").value='';
   					}
   				}
   			});
   		}
   	 }
   	
   	
   	function addEssentialSkillTextarea() {
   		document.getElementById("divEssentialSkillTextarea").style.display = "block";
   	}
   	function removeEssentialSkillTextarea() {
   		document.getElementById("divEssentialSkillTextarea").style.display = "none";
   	}
   	
   	function addDesirableSkillTextarea() {
   		document.getElementById("divDesirableSkillTextarea").style.display = "block";
   	}
   	function removeDesirableSkillTextarea() {
   		document.getElementById("divDesirableSkillTextarea").style.display = "none";
   	}
   	
   	
</script>
<div  id="requirementRequestDiv">
 
    <%
       
        String strUserType = (String) session.getAttribute("USERTYPE");
        String strBaseUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
        String strSessionEmpId = (String) session.getAttribute(IConstants.EMPID);
        Map<String, String> hmFeatureStatus = (Map<String, String>) request.getAttribute("hmFeatureStatus");
        if(hmFeatureStatus==null) hmFeatureStatus = new HashMap<String, String>();
        
        Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>) request.getAttribute("hmFeatureUserTypeId");
        if(hmFeatureUserTypeId==null) hmFeatureUserTypeId = new HashMap<String, List<String>>();
        
        Map<String, String> hmEmpName = (Map<String, String>) request.getAttribute("hmEmpName");
        //System.out.println("strUserType ===>> " + strUserType);
        
        List<FillEmployee> empList = (List<FillEmployee>) request.getAttribute("empList");
        List<FillEmployee> empList1 = (List<FillEmployee>) request.getAttribute("empList1");
        //System.out.println("RR/1106--empList1 : "+empList1.toString());
       // System.out.println("RR/1107--empList : "+empList);
        Map<String, String> hmCheckEmpList = (Map<String, String>) request.getAttribute("hmCheckEmpList");
        //System.out.println("RR/1109--hmCheckEmpList : "+hmCheckEmpList);
        Map<String, String> hmCheckEmpList1 = (Map<String, String>) request.getAttribute("hmCheckEmpList1");
        String frmPage = (String) request.getAttribute("frmPage");
        //System.out.println("frmPage : "+frmPage);
        String currUserType = (String) request.getAttribute("currUserType");
        
        Map<String, List<String>> hmValidationFields = (Map<String, List<String>> ) request.getAttribute("hmValidationFields");
        //System.out.println("hmValidationFields ===>> " + hmValidationFields);
        
        String validReqOpt = "";
		String validAsterix = "";
		//EncryptionUtils EU = new EncryptionUtils();// Created By Dattatray Date:21-July-2021
        %>
    <s:form id="formID" name="frmRequirementRequest" theme="simple" action="RequirementRequest" method="POST" cssClass="formcss" enctype="multipart/form-data">
        <s:hidden name="recruitmentID" id="recruitmentID"></s:hidden>
        <s:hidden name="orgID" id="orgID" />
        <s:hidden name="wlocID" id="wlocID" />
        <s:hidden name="desigID" id="desigID" />
        <s:hidden name="checkStatus" id="checkStatus" />
        <s:hidden name="fdate" id="fdate" />
        <s:hidden name="tdate" id="tdate" />
        <s:hidden name="frmPage" id="frmPage" />
        <s:hidden name="currUserType" id="currUserType" />
        <input type="hidden" name="strBaseUserType" id="strBaseUserType" value="<%=strBaseUserType %>"/>
        <table class="table table_no_border">
            <s:fielderror />
            <s:hidden name="insertRecruitReq" value="insert" />
            <tr>
            <% 
	            List<String> reqJobTitle = hmValidationFields.get("REQ_JOB_TITLE"); 
				validAsterix = "";
				validReqOpt = "";
				if(reqJobTitle != null && uF.parseToBoolean(reqJobTitle.get(0))) {
					validAsterix = "<sup>*</sup>";
					validReqOpt = "validateRequired";
				}
            %>
                <td class="txtlabel alignRight" style="width: 35%;">Job Title:<%=validAsterix %></td>
                <td>
                	<% if(reqJobTitle != null && uF.parseToBoolean(reqJobTitle.get(0))) { %>
                    	<s:textfield name="jobTitle" id="jobTitle" cssClass="validateRequired" />
                    <% } else { %>
                    	<s:textfield name="jobTitle" id="jobTitle" />
                    <% } %>
                </td>
            </tr>
            <tr>
			<% 
	            List<String> reqOrg = hmValidationFields.get("REQ_ORGANIZATION"); 
				validAsterix = "";
				validReqOpt = "";
				if(reqOrg != null && uF.parseToBoolean(reqOrg.get(0))) {
					validAsterix = "<sup>*</sup>";
					validReqOpt = "validateRequired";
				}
            %>
                <td class="txtlabel alignRight">Organisation:<%=validAsterix %></td>
                <td>
                	<% if(reqOrg != null && uF.parseToBoolean(reqOrg.get(0))) { %>
	                    <% if(strUserType != null && strUserType.equals(IConstants.MANAGER)) { %>
	                    <s:select theme="simple" name="organisation" id="organisation" listKey="orgId" listValue="orgName" list="organisationList" 
	                        key="" onchange="getLocationOrg(this.value);" disabled="true" value="strOrg"  cssClass="validateRequired"/>
	                    	<input type="hidden" name="organisation" value="<%=(String)request.getAttribute("strOrg") %>">
	                    <% } else { %>
	                    <s:select theme="simple" name="organisation" id="organisation" listKey="orgId" listValue="orgName" list="organisationList" 
	                        key="" onchange="getLocationOrg(this.value);" value="strOrg" cssClass="validateRequired"/>
	                    <% } %>
					<% } else { %>
						<% if(strUserType != null && strUserType.equals(IConstants.MANAGER)) { %>
	                    <s:select theme="simple" name="organisation" id="organisation" listKey="orgId" listValue="orgName" list="organisationList" 
	                        key="" onchange="getLocationOrg(this.value);" disabled="true" value="strOrg"/>
	                    	<input type="hidden" name="organisation" value="<%=(String)request.getAttribute("strOrg") %>">
	                    <% } else { %>
	                    <s:select theme="simple" name="organisation" id="organisation" listKey="orgId" listValue="orgName" list="organisationList" 
	                        key="" onchange="getLocationOrg(this.value);" value="strOrg"/>
	                    <% } %>
					<% } %>
                </td>
                <!-- getServicesByOrg(this.value); -->
            </tr>
            <tr>
            <% 
	            List<String> reqLoc = hmValidationFields.get("REQ_LOCATION"); 
				validAsterix = "";
				validReqOpt = "";
				if(reqLoc != null && uF.parseToBoolean(reqLoc.get(0))) {
					validAsterix = "<sup>*</sup>";
					validReqOpt = "validateRequired";
				}
            %>
                <td class="txtlabel alignRight">Location:<%=validAsterix %></td>
                <td>
                    <div id="locationdivid">
						<% if(reqLoc != null && uF.parseToBoolean(reqLoc.get(0))) { %>
                        <s:select cssClass="validateRequired" name="location" id="locationid" theme="simple" listKey="wLocationId" listValue="wLocationName" 
                        	headerKey="" headerValue="Select Location" list="workLocationList" value="{manlocation}" />
                        <% } else { %>
                        <s:select name="location" id="locationid" theme="simple" listKey="wLocationId" listValue="wLocationName" 
                        	headerKey="" headerValue="Select Location" list="workLocationList" value="{manlocation}" />
                        <% } %>	
                    </div>
                </td>
            </tr>
            <tr>
            <% 
	            List<String> reqLevel = hmValidationFields.get("REQ_LEVEL"); 
				validAsterix = "";
				validReqOpt = "";
				if(reqLevel != null && uF.parseToBoolean(reqLevel.get(0))) {
					validAsterix = "<sup>*</sup>";
					validReqOpt = "validateRequired";
				}
            %>
                <td class="txtlabel alignRight">Level:<%=validAsterix %></td>
                <td>
                    <div id="leveldivid">
                        <% if(reqLevel != null && uF.parseToBoolean(reqLevel.get(0))) { %>
                        <s:select cssClass="validateRequired" name="strLevel" id="strLevel" theme="simple" listKey="levelId" listValue="levelCodeName" 
                        	headerKey="" headerValue="Select Level" list="levelslist" required="true" onchange="getDesig(this.value);" />
                        <% } else { %>
                        <s:select name="strLevel" id="strLevel" theme="simple" listKey="levelId" listValue="levelCodeName" 
                        	headerKey="" headerValue="Select Level" list="levelslist" required="true" onchange="getDesig(this.value);" />
                        <% } %>	
                    </div>
                </td>
            </tr>
            <tr>
            <% 
	            List<String> reqDesig = hmValidationFields.get("REQ_DESIGNATION"); 
				validAsterix = "";
				validReqOpt = "";
				if(reqDesig != null && uF.parseToBoolean(reqDesig.get(0))) {
					validAsterix = "<sup>*</sup>";
					validReqOpt = "validateRequired";
				}
            %>
                <td class="txtlabel alignRight">Designation:<%=validAsterix %></td>
                <td>
                    <div id="myDesig1" style="float: left;">
                    <% if(reqDesig != null && uF.parseToBoolean(reqDesig.get(0))) { %>
                        <s:select theme="simple" name="strDesignationUpdate" id="strDesignationUpdate" listKey="desigId" listValue="desigCodeName" 
							headerKey="" headerValue="Select Designation" list="desigList" key="" cssClass="validateRequired" value="strDesignationUpdate" onchange="getGradebyDesig(this.value,'add');" />
					<% } else { %>
						<s:select theme="simple" name="strDesignationUpdate" id="strDesignationUpdate" listKey="desigId" listValue="desigCodeName" 
                            headerKey="" headerValue="Select Designation" list="desigList" key="" value="strDesignationUpdate" onchange="getGradebyDesig(this.value,'add');" />
					<% } %>
                    </div>
                    <div style="float: left; margin-left: 20px;">
                        <span id="addDesigDiv">
                        <a href="javascript: void(0);" onclick="getGradebyDesig('0','add');"><u>Add New Designation</u></a>
                        </span>
                        <span id="resetDesigDiv" style="display: none;">
                        <a href="javascript: void(0);" onclick="getGradebyDesig('0','reset');"><u>Reset</u></a>
                        </span>
                    </div>
                </td>
            </tr>
            <tr id="customDesigTR"
                <% if(request.getAttribute("custumdesignation")==null || request.getAttribute("custumdesignation").equals("")) { %>
                style="display: none;"
                <% } else { %>
                style="display: table-row;"
                <% } %>
                >
                <%
                    String custumDesig="";
                    if(request.getAttribute("custumdesignation")==null) {
                    	custumDesig="";
                    } else {
                    	custumDesig=(String)request.getAttribute("custumdesignation");
                    }
				%>			
                <td class="txtlabel alignRight">Custom Designation:<sup>*</sup></td>
                <!-- onkeyup="customDesigFill() -->
                <td><input type="text" name="custumdesignation" id="custumdesig" value="<%=custumDesig%>" onchange="checkExistDesig(this.value);" class="validateRequired"/></td>
            </tr>
            <tr id="existDesigMsgTR">
                <td></td>
                <td>
                    <div id="desigValidatorMessege" style="font-size: 12px;"></div>
                </td>
            </tr>
            <tr id="customGradeTR" <%if(request.getAttribute("custumgrade")==null || request.getAttribute("custumgrade").equals("")) { %>
                style="display:none;"
                <% } else { %>
                style="display:table-row;"
                <% } %>
                >
                <%
                    String custumGrade="";
                    if(request.getAttribute("custumgrade")==null) {
                    	custumGrade="";
                    } else {
                    	custumGrade=(String)request.getAttribute("custumgrade");
                    }
				%>	
                <td class="txtlabel alignRight">Custom Grade:<sup>*</sup></td>
                <td><input type="text" name="custumgrade" id="custumgrade" value="<%=custumGrade%>" class="validateRequired"/>
                </td>
            </tr>
            <tr id="existGradeTR" id="customGradeTR" <%if(request.getAttribute("custumgrade")!=null && !request.getAttribute("custumgrade").equals("")) { %>
                style="display:none;"
                <% } else { %>
                style="display:table-row;"
                <% } %>
                >
                <% 
		            List<String> reqGrade = hmValidationFields.get("REQ_GRADE"); 
					validAsterix = "";
					validReqOpt = "";
					if(reqGrade != null && uF.parseToBoolean(reqGrade.get(0))) {
						validAsterix = "<sup>*</sup>";
						validReqOpt = "validateRequired";
					}
	            %>
                <td class="txtlabel alignRight">Grade:<%=validAsterix %></td>
                <td>
                    <div id="myGrade1">
                    <% if(reqGrade != null && uF.parseToBoolean(reqGrade.get(0))) { %>
                        <s:select  name="strGrade" id="empGrade" theme="simple" listKey="gradeId" listValue="gradeCode" cssClass="validateRequired"
                            headerKey="" headerValue="Select Grade" list="gradeList" value="strGrade"/>
					<% } else { %>
						<s:select  name="strGrade" id="empGrade" theme="simple" listKey="gradeId" listValue="gradeCode"
                            headerKey="" headerValue="Select Grade" list="gradeList" value="strGrade"/>
					<% } %>
                    </div>
                </td>
            </tr>
            <tr>
            <tr>
            <% 
	            List<String> reqPriority = hmValidationFields.get("REQ_PRIORITY"); 
				validAsterix = "";
				validReqOpt = "";
				if(reqPriority != null && uF.parseToBoolean(reqPriority.get(0))) {
					validAsterix = "<sup>*</sup>";
					validReqOpt = "validateRequired";
				}
            %>
                <td class="txtlabel alignRight">Priority:<%=validAsterix %></td>
                <td>
                    <div id="myPriority">
                    <% if(reqPriority != null && uF.parseToBoolean(reqPriority.get(0))) { %>
                        <s:select cssClass="validateRequired" name="priority" headerKey="" headerValue="Select Priority" theme="simple"
                            list="#{'0':'Low', '2':'Medium', '1':'High' }" />
					<% } else { %>
						<s:select name="priority" headerKey="" headerValue="Select Priority" theme="simple" list="#{'0':'Low', '2':'Medium', '1':'High' }"   />
					<% } %>
                    </div>
                </td>
            </tr>
            
            <tr>
			<%
	            List<String> reqJobDescription = hmValidationFields.get("REQ_JOB_DESCRIPTION"); 
				validAsterix = "";
				validReqOpt = "";
				if(reqJobDescription != null && uF.parseToBoolean(reqJobDescription.get(0))) {
					validAsterix = "<sup>*</sup>";
					validReqOpt = "validateRequired";
				}
           	%>
				<td class="txtlabel alignRight" valign="top">Job Description:<%=validAsterix %></td>
				<td valign="top">
					<% if(reqJobDescription != null && uF.parseToBoolean(reqJobDescription.get(0))) { %>
                        <s:textarea name="jobDescription" id="jobDescription" cols="30" rows="4" cssClass="validateRequired"></s:textarea>
					<% } else { %>
						<s:textarea name="jobDescription" id="jobDescription" cols="30" rows="4"></s:textarea>
					<% } %>
			    </td>
			</tr>
			
            <tr>
            <% 
	            List<String> reqEssentialSkills = hmValidationFields.get("REQ_ESSENTIAL_SKILLS"); 
				validAsterix = "";
				validReqOpt = "";
				if(reqEssentialSkills != null && uF.parseToBoolean(reqEssentialSkills.get(0))) {
					validAsterix = "<sup>*</sup>";
					validReqOpt = "validateRequired";
				}
            %>
                <td class="txtlabel alignRight" valign="top">Essential Skills:<%=validAsterix %></td>
                <td><div style=" float: left; width: 100%;">
	                    <div id="essentialskilldivid" style="float: left;">
	                    <% if(reqEssentialSkills != null && uF.parseToBoolean(reqEssentialSkills.get(0))) { %>
	                        <s:select name="essentialSkills" theme="simple" listKey="skillsId" listValue="skillsName" list="essentialSkillsList" multiple="true" cssClass="validateRequired"/>
						<% } else { %>
							<s:select name="essentialSkills" theme="simple" listKey="skillsId" listValue="skillsName" list="essentialSkillsList" multiple="true"/>
						<% } %>
	                    </div>
	                    <div style="float: left; margin-left: 5px;"><a href="javascript:void(0)" onclick="addEssentialSkillTextarea();" class="add-font"></a></div>
                    </div>
                    <% String essentialSkillsText = (String) request.getAttribute("essentialSkillsText"); %>
                    <div id="divEssentialSkillTextarea" style="display: <%=(essentialSkillsText!=null && essentialSkillsText.trim().length()>0) ? "block" : "none" %>; float: left; width: 100%; margin-top: 5px;">
                    	<div style="float: left;"><s:textarea name="essentialSkillsText" id="essentialSkillsText"></s:textarea></div>
                    	<div style="float: left; margin-left: 5px;"><a href="javascript:void(0)" onclick="removeEssentialSkillTextarea();" class="remove-font"></a></div>
                    </div>
                </td>
            </tr>
            <tr>
            <% 
	            List<String> reqDesirableSkills = hmValidationFields.get("REQ_DESIRABLE_SKILLS"); 
				validAsterix = "";
				validReqOpt = "";
				if(reqDesirableSkills != null && uF.parseToBoolean(reqDesirableSkills.get(0))) {
					validAsterix = "<sup>*</sup>";
					validReqOpt = "validateRequired";
				}
            %>
                <td class="txtlabel alignRight" valign="top">Desirable Skills:<%=validAsterix %></td>
                <td><div style="float: left; width: 100%;">
	                    <div id="skilldivid" style="float: left;">
	                    <% if(reqDesirableSkills != null && uF.parseToBoolean(reqDesirableSkills.get(0))) { %>
	                        <s:select name="skills" theme="simple" listKey="skillsId" listValue="skillsName" list="skillslist" multiple="true" cssClass="validateRequired"/>
						<% } else { %>
							<s:select name="skills" theme="simple" listKey="skillsId" listValue="skillsName" list="skillslist" multiple="true"/>
						<% } %>
	                    </div>
	                    <div style="float: left; margin-left: 5px;"><a href="javascript:void(0)" onclick="addDesirableSkillTextarea();" class="add-font"></a></div>
                    </div>
                    <% String desirableSkillsText = (String) request.getAttribute("desirableSkillsText"); %>
                    <div id="divDesirableSkillTextarea" style="display: <%=(desirableSkillsText!=null && desirableSkillsText.trim().length()>0) ? "block" : "none" %>; float: left; width: 100%; margin-top: 5px;">
                    	<div style="float: left;"><s:textarea name="desirableSkillsText" id="desirableSkillsText"></s:textarea></div>
                    	<div style="float: left; margin-left: 5px;"><a href="javascript:void(0)" onclick="removeDesirableSkillTextarea();" class="remove-font"></a></div>
                    </div>
                </td>
            </tr>
            
            <tr>
                <%
                    String idealCandidate="";
                    if(request.getAttribute("idealCandidate")!=null)
                    	idealCandidate=(String)request.getAttribute("idealCandidate");
                %>
                <%
		            List<String> reqIdealCandidate = hmValidationFields.get("REQ_IDEAL_CANDIDATE"); 
					validAsterix = "";
					validReqOpt = "";
					if(reqIdealCandidate != null && uF.parseToBoolean(reqIdealCandidate.get(0))) {
						validAsterix = "<sup>*</sup>";
						validReqOpt = "validateRequired";
					}
            	%>	
                <td class="txtlabel alignRight" valign="top">Ideal Candidate:<%=validAsterix %></td>
                <td valign="top">
                    <div id="idealdivid">
                    <% if(reqIdealCandidate != null && uF.parseToBoolean(reqIdealCandidate.get(0))) { %>
                        <s:textarea name="idealCandidate" id="idealCandidate" cols="30" rows="4" cssClass="validateRequired"></s:textarea>
                    <% } else { %>
                    	<s:textarea name="idealCandidate" id="idealCandidate" cols="30" rows="4"></s:textarea>
                    <% } %>
                    </div>
                </td>
            </tr>
            
            <tr>
				<th class="txtlabel alignRight">Min. Experience:<%=validAsterix %></th>
				<td>
					Year&nbsp;&nbsp;<select name="minYear" id="minYear" class="autoWidth">
						<% for (int i = 0; i <= 20; i++) {
							if (i == uF.parseToInt((String)request.getAttribute("minYear"))) {
						%>
						<option value="<%=i%>" selected="selected"><%=i%></option>
						<% } else if (i == 0) { %>
						<option value="<%=i%>" selected="selected"><%=i%></option>
						<% } else { %>
						<option value="<%=i%>"><%=i%></option>
						<% } } %>
				</select>&nbsp;&nbsp;Month &nbsp;&nbsp;<select name="minMonth" id="minMonth" class="autoWidth">
						<% for (int i = 0; i < 12; i++) {
							if (i == uF.parseToInt((String)request.getAttribute("minMonth"))) {
						%>
						<option value="<%=i%>" selected="selected"><%=i%></option>
						<% } else if (i == 0) { %>
						<option value="<%=i%>" selected="selected"><%=i%></option>
						<% } else { %>
						<option value="<%=i%>"><%=i%></option>
						<% } } %>
				</select> 
				</td>
			</tr>
			<tr>
				<th class="txtlabel alignRight">Max. Experience:<%=validAsterix %></th>
				<td>
					Year&nbsp;&nbsp;<select name="maxYear" id="maxYear" class="autoWidth">
						<% for (int i = 0; i <= 20; i++) {
							if (i == uF.parseToInt((String)request.getAttribute("maxYear"))) {
						%>
						<option value="<%=i%>" selected="selected"><%=i%></option>
						<% } else if (i == 0) { %>
						<option value="<%=i%>" selected="selected"><%=i%></option>
						<% } else { %>
						<option value="<%=i%>"><%=i%></option>
						<% } } %>
				</select>&nbsp;&nbsp;Month &nbsp;&nbsp;<select name="maxMonth" id="maxMonth" class="autoWidth">
						<% for (int i = 0; i < 12; i++) {
							if (i == uF.parseToInt((String)request.getAttribute("maxMonth"))) {
						%>
						<option value="<%=i%>" selected="selected"><%=i%></option>
						<% } else if (i == 0) { %>
						<option value="<%=i%>" selected="selected"><%=i%></option>
						<% } else { %>
						<option value="<%=i%>"><%=i%></option>
						<% } } %>
				</select> 
				</td>
			</tr>
			<!-- Start Dattatray Date : 05-July-21  Note : Position Changed-->
			<tr>
                <%
                    String effectiveDate="";
                    if(request.getAttribute("rdate")==null) {
                    	effectiveDate="";
                    } else {
                    	effectiveDate=(String)request.getAttribute("rdate");
                    }
				%>
				<%
		            List<String> reqEffectiveDate = hmValidationFields.get("REQ_EFFECTIVE_DATE"); 
					validAsterix = "";
					validReqOpt = "";
					if(reqEffectiveDate != null && uF.parseToBoolean(reqEffectiveDate.get(0))) {
						validAsterix = "<sup>*</sup>";
						validReqOpt = "validateRequired";
					}
	           	%>
                <td class="txtlabel alignRight">Position Open Date:<%=validAsterix %></td>
                <td><input type="text" name="rdate" id="rdate" onchange="getPlannedbyDate(this.value);" class="<%=validReqOpt %>" value="<%=effectiveDate%>"/></td>
            </tr>
            <tr>
                <%
                    String targetDead="";
                    if(request.getAttribute("targetdeadline")==null){
                    	targetDead="";
                    } else {
                    	targetDead=(String)request.getAttribute("targetdeadline");
                    }
				%>
				<%
		            List<String> reqTargetDeadline = hmValidationFields.get("REQ_TARGET_DEADLINE"); 
					validAsterix = "";
					validReqOpt = "";
					if(reqTargetDeadline != null && uF.parseToBoolean(reqTargetDeadline.get(0))) {
						validAsterix = "<sup>*</sup>";
						validReqOpt = "validateRequired";
					}
	           	%>	
                <td class="txtlabel alignRight">Target Deadline:<%=validAsterix %></td>
                <td><input type="text" name="targetdeadline" id="targetdeadline" class="<%=validReqOpt %>" value="<%=targetDead%>"/></td>
            </tr>
            <!-- End Dattatray Date : 05-July-21 -->
            <tr>
                <%
                    String noOfPosition="";
                    if(request.getAttribute("position")==null) {
                    	noOfPosition="";
                    } else {
                    	noOfPosition=(String)request.getAttribute("position");
                    }
				%>
				<%
		            List<String> reqNoOfPosition = hmValidationFields.get("REQ_NO_OF_POSITIONS"); 
					validAsterix = "";
					validReqOpt = "";
					if(reqNoOfPosition != null && uF.parseToBoolean(reqNoOfPosition.get(0))) {
						validAsterix = "<sup>*</sup>";
						validReqOpt = "validateRequired";
					}
            	%>
                <td class="txtlabel alignRight">No. of Position(s):<%=validAsterix %></td>
                <td width = "400px;">
                <% if(reqNoOfPosition != null && uF.parseToBoolean(reqNoOfPosition.get(0))) { %>
               
                    <s:textfield name="position" id="position" cssClass="validateRequired" onkeyup="checkValue();" onkeypress="return isOnlyNumberKey(event)"/>
				<% } else { %>
					<s:textfield name="position" id="position" onkeyup="checkValue();" onkeypress="return isOnlyNumberKey(event)"/>
				<% } %>
                    <br/>
                    <div id="Planned" >
                        <%if(request.getAttribute("recruitmentID")!=null) { %>
                        <div class="skill_div" style="width: 80px;">
                         	
                            <p class="sk_value" style="text-align:center;padding-bootom: 0px;"><%=uF.showData((String)request.getAttribute("Output"),"0")%></p>
                            <p class="sk_name" style="text-align:center;padding-bootom: 0px;">Planned</p>
                        </div>
                        <div class="skill_div" style="width: 80px;">
                            <p class="sk_value" style="text-align:center;padding-bootom: 0px;"><%=uF.showData((String)request.getAttribute("strExistCount"),"0")%></p>
                            <p class="sk_name" style="text-align:center;padding-bootom: 0px;">Existing</p>
                        </div>
                        <%} %>
                    </div>
                </td>
            </tr>
            <tr>
            <%
	            List<String> reqGender = hmValidationFields.get("REQ_GENDER"); 
				validAsterix = "";
				validReqOpt = "";
				if(reqGender != null && uF.parseToBoolean(reqGender.get(0))) {
					validAsterix = "<sup>*</sup>";
					validReqOpt = "validateRequired";
				}
           	%>
                <td class="txtlabel alignRight">Gender:<%=validAsterix %></td>
                <td id="genderTD">
                    <select name="gender" id="gender" class="<%=validReqOpt %>">
                        <%
                            String gender = (String) request.getAttribute("strSex");
                            if(gender != null && !gender.equals("")) {
						%>
                        	<%=gender %>
                        <% } else { %>
                        <option value="0">Any</option>
                        <option value="M">Male</option>
                        <option value="F">Female</option>
                        <% } %>
                    </select>
                </td>
            </tr>
            <tr>
            <%
	            List<String> reqMinAge = hmValidationFields.get("REQ_MIN_AGE"); 
				validAsterix = "";
				validReqOpt = "";
				if(reqMinAge != null && uF.parseToBoolean(reqMinAge.get(0))) {
					validAsterix = "<sup>*</sup>";
					validReqOpt = "validateRequired";
				}
           	%>
                <td class="txtlabel alignRight">Min. Age:<%=validAsterix %></td>
                <td>
                    <span id="minAgeSpan">
                        <select name="minAge" id="minAge" class="<%=validReqOpt %>">
                            <%
                                String minAge = (String) request.getAttribute("strAge");
                                if(minAge != null && !minAge.equals("")) {
                                %>
                            <%=minAge %>
                            <% } else { %>
                            <option value="0">Select Age</option>
                            <% for (int i = 0; i <=42; i++) {
                                int intMinAge = 18;
                                intMinAge += i;
                                %>
                            <option value="<%=intMinAge %>"><%=intMinAge %></option>
                            <% }
                                } %>
                        </select>
                    </span>
                </td>
            </tr>
            <tr>
            <%
	            List<String> reqMinCTC = hmValidationFields.get("REQ_MIN_CTC"); 
				validAsterix = "";
				validReqOpt = "";
				if(reqMinCTC != null && uF.parseToBoolean(reqMinCTC.get(0))) {
					validAsterix = "<sup>*</sup>";
					validReqOpt = "validateRequired";
				}
           	%>
                <td class="txtlabel alignRight">Min. CTC:<%=validAsterix %></td>
                <td>
                <% if(reqMinCTC != null && uF.parseToBoolean(reqMinCTC.get(0))) { %>
                    <s:textfield name="strMinCTC" id="strMinCTC" cssClass="validateRequired" onkeypress="return isNumberKey(event)"/>
				<% } else { %>
					<s:textfield name="strMinCTC" id="strMinCTC" onkeypress="return isNumberKey(event)"/>
				<% } %>
                </td>
            </tr>
            <tr>
            <%
	            List<String> reqMaxCTC = hmValidationFields.get("REQ_MAX_CTC"); 
				validAsterix = "";
				validReqOpt = "";
				if(reqMaxCTC != null && uF.parseToBoolean(reqMaxCTC.get(0))) {
					validAsterix = "<sup>*</sup>";
					validReqOpt = "validateRequired";
				}
           	%>
                <td class="txtlabel alignRight">Max. CTC:<%=validAsterix %></td>
                <td>
                <% if(reqMaxCTC != null && uF.parseToBoolean(reqMaxCTC.get(0))) { %>
                    <s:textfield name="strMaxCTC" id="strMaxCTC" cssClass="validateRequired" onkeypress="return isNumberKey(event)"/>
                <% } else { %>
                	<s:textfield name="strMaxCTC" id="strMaxCTC" onkeypress="return isNumberKey(event)"/>
                <% } %>
                </td>
            </tr>
            <tr>
            <%
	            List<String> reqTypeofEmployment = hmValidationFields.get("REQ_TYPE_OF_EMPLOYMENT"); 
				validAsterix = "";
				validReqOpt = "";
				if(reqTypeofEmployment != null && uF.parseToBoolean(reqTypeofEmployment.get(0))) {
					validAsterix = "<sup>*</sup>";
					validReqOpt = "validateRequired";
				}
           	%>
                <td class="txtlabel alignRight">Type of Employment:<%=validAsterix %></td>
                <td>
                    <div id="typeOfEmployment">
                    <% if(reqTypeofEmployment != null && uF.parseToBoolean(reqTypeofEmployment.get(0))) { %>
                        <s:select cssClass="validateRequired" name="strEmploymentType" id="strEmploymentType" theme="simple" listKey="empTypeId" listValue="empTypeName" headerKey="" 
                        	headerValue="Select Employment Type" list="employmentList" value="strEmployment" onchange="showTempCasualJastification(this.value);"/>
					<% } else { %>
						<s:select name="strEmploymentType" id="strEmploymentType" theme="simple" listKey="empTypeId" listValue="empTypeName" headerKey="" 
							headerValue="Select Employment Type" list="employmentList" value="strEmployment" onchange="showTempCasualJastification(this.value);"/>
					<% } %>
                    </div>
                </td>
            </tr>
            <tr id="typeOfEmploymentTR" style="display: none;">
                <td class="txtlabel alignRight">If Temporary/Casual,please give justification & period required for:</td>
                <td valign="top">
                    <div id="tempOrCasualDiv">
                        <s:textarea name="tempOrCasualJastification" cols="30" rows="4"></s:textarea>
                    </div>
                </td>
            </tr>
            <tr>
                <td class="txtlabel alignRight">Vacancy:</td>
                <td>
                	<s:select cssClass="validateRequired" name="vacancy" id="vacancy" headerKey="1" headerValue="New Requirement" theme="simple"
						list="#{'2':'Replacement', '3':'Staffing Requirement' }" onchange="changeVacancyType(this.value);" />
                    <%-- <%
                        String vacancyTypeR="", vacancyTypeA="checked";
                        if(request.getAttribute("vacancy") != null && uF.parseToInt((String)request.getAttribute("vacancy")) == 0){
                        	vacancyTypeA="";
                        	vacancyTypeR="checked";
                        } else if(request.getAttribute("vacancy") != null && uF.parseToInt((String)request.getAttribute("vacancy")) == 1){
                        	vacancyTypeA="checked";
                        	vacancyTypeR="";
                        }
                        %>
                    <input type="hidden" name="vacancyHidden" id="vacancyHidden" value="<%=uF.showData((String)request.getAttribute("vacancy"), "1") %>">	
                    <input type="radio" name="vacancy" value="1" onclick="changeVacancyType(this.value);" <%=vacancyTypeA %> required> Additional &nbsp;&nbsp; --%>
                </td>
            </tr>
            <tr id="addtionalVacancyTR">
            <%
	            List<String> reqGiveJustification = hmValidationFields.get("REQ_GIVE_JUSTIFICATION"); 
				validAsterix = "";
				validReqOpt = "";
				if(reqGiveJustification != null && uF.parseToBoolean(reqGiveJustification.get(0))) {
					validAsterix = "<sup>*</sup>";
					validReqOpt = "validateRequired";
				}
           	%>
                <td class="txtlabel alignRight" valign="top">Give Justification:<%=validAsterix %></td>
                <td valign="top">
                    <div id="addtionalvacancydivid">
                    <% if(reqGiveJustification != null && uF.parseToBoolean(reqGiveJustification.get(0))) { %>
                        <s:textarea name="addtionalJastification" cols="30" rows="4" cssClass="validateRequired"></s:textarea>
					<% } else { %>
						<s:textarea name="addtionalJastification" cols="30" rows="4"></s:textarea>
					<% } %>
                    </div>
                </td>
            </tr>
            <tr id="replacementVacancyTR" style="display: none;">
                <td  class="txtlabel alignRight">Name of person to be replaced with:</td>
                <td></td>
            </tr>
            <tr  id="replacementVacancyTR1" style="display: none;">
                <td colspan="3">
                    <table class="table table-bordered">
                        <tr>
                            <td colspan="5">
                                <div class="filter_div">
                                    <div class="row row_without_margin">
                                        <div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
                                       		<i class="fa fa-filter"></i>
                                        </div>
                                        <div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
                                            <div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5" style="margin: 5px;">
                                                <s:select theme="simple" name="strOrg" list="orgList" id="strOrg" listKey="orgId" listValue="orgName"
                                                    required="true"  onchange="getWLocDepartLevelDesigByOrg(this.value);"></s:select>
                                            </div>
                                            <div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5" id="wlocationDiv" style="margin: 5px;">
                                                <s:select theme="simple" name="strWlocation" list="workList" id="wlocation" listKey="wLocationId" listValue="wLocationName" headerKey=""
                                                    headerValue="All WorkLocation" required="true" value="{strEmpWLocId}" onchange="getEmployeebyLocation();"></s:select>
                                            </div>
                                            <div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5" id="departDiv" style="margin: 5px;">
                                                <s:select theme="simple" name="strDepart" list="departmentList" id="depart" listKey="deptId" listValue="deptName" headerKey=""
                                                    headerValue="All Department" required="true" onchange="getEmployeebyDepart();"></s:select>
                                            </div>
                                            <div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5" id="levelDiv" style="margin: 5px;">
                                                <s:select theme="simple" name="strLevel1" list="levelList" listKey="levelId" id="strLevel1" listValue="levelCodeName" headerKey=""
                                                    headerValue="All Level" required="true" onchange="getEmployeebyLevel()"></s:select>
                                            </div>
                                            <div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5" id="myDesig" style="margin: 5px;">
                                                <s:select theme="simple" name="strDesignation" list="designationList" listKey="desigId" id="desigIdV" listValue="desigCodeName"
                                                    headerKey="" headerValue="All Designation" onchange="getEmployeebyDesig();"></s:select>
                                            </div>
                                        </div>
                                    </div>
                                    <br>	
                                </div>
                                <div class="row row_without_margin">
                                    <div class="col-lg-5 col-md-5 col-sm-12" id="myEmployee" style="height:250px;overflow-y:auto;border: 2px solid rgb(238, 238, 238);padding: 5px;margin: 5px;">
                                        <table id="lt" class="table table-bordered">
                                            <%
                                                if (empList != null && !empList.equals("") && !empList.isEmpty()) {
                                                	Map<String, String> hmEmpLocation = (Map<String, String>)request.getAttribute("hmEmpLocation");
                                                	Map<String, String> hmWLocation = (Map<String, String>)request.getAttribute("hmWLocation");
                                                	Map<String, String> hmEmpCodeDesig = (Map<String, String>)request.getAttribute("hmEmpCodeDesig");
                                                	
                                                %>
                                            <thead>
                                                <tr>
                                                    <th align="center"><input onclick="checkUncheckValue();"
                                                        type="checkbox" name="allEmp" id="allEmp">
                                                    </th>
                                                    <th align="center">Employee</th>
                                                    <th align="center">Designation</th>
                                                    <th align="center">Location</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <%
                                                    for (int i = 0; i < empList.size(); i++) {
                                                    
                                            				String empID = ((FillEmployee) empList.get(i)).getEmployeeId();
                                            				String empName = ((FillEmployee) empList.get(i)).getEmployeeCode();
                                            
                                            				String emplocationID = (empID == null || empID.equals("")) ? "" : hmEmpLocation.get(empID);
                                            				String location = (emplocationID == null || emplocationID.equals("")) ? "" : uF.showData(hmWLocation.get(emplocationID), "");
                                            
                                            				String desig = (empID == null || empID.equals("")) ? "" : uF.showData(hmEmpCodeDesig.get(empID), "");
                                            				//System.out.println("RR/1839--empID ===>> " + empID);
                                            				%>
                                                <tr>
                                                    <td><!-- Created By Dattatray Date : 09-July-2021 Note: strHiringEmpId_-->
                                                    <td><input type="checkbox" name="strHiringEmpId" id="strHiringEmpId_<%=i%>" onclick="getHiringSelectedEmp(this.checked,this.value);"
                                                        value="<%=empID%>" <%if (hmCheckEmpList != null && hmCheckEmpList.get(empID) != null) {%>
                                                        checked="checked" <%}%>><%--  --%>
                                                    </td>
                                                    <!-- Created By Dattatray Date:22-July-2021 Note: empId encrypt  -->
                                                    <td nowrap="nowrap"><a href="javascript: void(0);" onclick="openPanelEmpProfilePopup('<%=empID %>')"><%=empName%></a></td>
                                                    <td nowrap="nowrap"><%=desig%></td>
                                                    <td nowrap="nowrap"><%=location%></td>
                                                </tr>
                                                <%
                                                    }
                                                    		} else {
                                                    %>
                                                <tr>
                                                    <td colspan="3">
                                                        <div class="nodata msg">
                                                            <span>No Employee Found</span>
                                                        </div>
                                                    </td>
                                                </tr>
                                                <%
                                                    }
                                                    %>
                                            </tbody>
                                        </table>
                                    </div>
                                    <div class="col-lg-5 col-md-5 col-sm-12" id="idEmployeeInfo" style="height:250px;overflow-y:auto;border: 2px solid rgb(238, 238, 238);padding: 5px;margin: 5px;">
                                        <%
                                            List<String> selectEmpList = (List<String>) request.getAttribute("selectEmpList");
                                            		if (selectEmpList != null) {
                                            %>
                                        <div style="border: 2px solid #ccc;">
                                            <div style="padding: 8px 20px; border: 2px solid lightgray;"><b>Employee</b></div>
                                            <%
                                                for (int i = 0; i < selectEmpList.size(); i++) {
                                                %>
                                            <div style="float: left; width: 100%; margin: 5px;"><strong><%=i + 1%>.</strong>&nbsp;&nbsp;<%=selectEmpList.get(i)%></div>
                                            <% } %>
                                        </div>
                                        <% } else {
                                            %>
                                        <div class="nodata msg" style="width: 85%">
                                            <span>No Employee selected</span>
                                        </div>
                                        <%	} %>
                                        <input type="hidden" name="empselected" id="empselected" value="<%=uF.showData((String)request.getAttribute("empselected"), "0") %>"/>
                                    </div>
                                </div>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
            <tr>
                <td class="txtlabel alignRight">Reporting to:</td>
                <td>
                    <% String reportToType = (String)request.getAttribute("reportToType");
                        String managerName = "";
                        if(strUserType != null && strUserType.equals(IConstants.MANAGER)) {
                        	managerName = "("+hmEmpName.get(strSessionEmpId)+")"; 
                        }
                        %>
                    <input type="hidden" name="reporttoHidden" id="reporttoHidden" value="<%=uF.showData((String)request.getAttribute("reportToType"), "Myself")%>">
                    <select name="reportToType" onchange="changeReportType(this.value);">
                        <option value="Myself">Myself <%=managerName %></option>
                        <% if(strUserType != null && !strUserType.equals(IConstants.MANAGER) && !strUserType.equals(IConstants.EMPLOYEE)) { %>
	                        <% if(reportToType != null && reportToType.equals("Other")) { %>
	                        <option value="Other" selected="selected">Other</option>
	                        <% } else { %>
	                        <option value="Other">Other</option>
	                        <% } %>
                        <% } %>
                    </select>
                    <%-- <s:select cssClass="validateRequired" name="reportToType" headerKey="Myself" headerValue="Myself" theme="simple"
                        list="#{'Other':'Other'}" value="reportToType" onchange="changeReportType(this.value);"/> --%>
                </td>
            </tr>
            <tr id="reportingToTR" style="display: none;">
                <!-- <td class="txtlabel alignRight">Reporting to:</td> -->
                <td colspan="2">
                    <table class="table table-bordered">
                        <tr>
                            <td colspan="5">
                                <div class="row row_without_margin">
                                    <div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
                                    	<i class="fa fa-filter"></i>
                                    </div>
                                    <div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
                                        <div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5" style="margin: 5px;">
                                            <s:select theme="simple" name="strOrg1" list="orgList1" id="strOrg1" listKey="orgId" listValue="orgName"
                                                required="true"  onchange="getWLocDepartLevelDesigByOrg1(this.value);"></s:select>
                                        </div>
                                        <div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5" id="wlocationDiv1" style="margin: 5px;">
                                            <s:select theme="simple" name="strWlocation1" list="workList1" id="wlocation1" listKey="wLocationId" listValue="wLocationName" headerKey=""
                                                headerValue="All WorkLocation" required="true" value="{strEmpWLocId}" onchange="getEmployeebyLocation1();"></s:select>
                                        </div>
                                        <div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5" id="departDiv1" style="margin: 5px;">
                                            <s:select theme="simple" name="strDepart1" list="departmentList1" id="depart1" listKey="deptId" listValue="deptName" headerKey=""
                                                headerValue="All Department" required="true" onchange="getEmployeebyDepart1();"></s:select>
                                        </div>
                                        <div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5" id="levelDiv1" style="margin: 5px;">
                                            <s:select theme="simple" name="strLevel11" list="levelList1" listKey="levelId" id="strLevel11" listValue="levelCodeName" headerKey=""
                                                headerValue="All Level" required="true" onchange="getEmployeebyLevel1()"></s:select>
                                        </div>
                                        <div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5" id="myDesig11" style="margin: 5px;">
                                            <s:select theme="simple" name="strDesignation1" list="designationList1" listKey="desigId" id="desigIdV1" listValue="desigCodeName"
                                                headerKey="" headerValue="All Designation" onchange="getEmployeebyDesig1();"></s:select>
                                        </div>
                                    </div>
                                </div>
                                <br>	
                                <div class="row row_without_margin">
									<div class="col-lg-6 col-md-6 col-sm-12" id="myEmployee1"
										style="border: 2px solid rgb(238, 238, 238); padding: 5px; margin: 5px; height: 250px; overflow-y: auto;">
										<table id="lt1" class="table table-bordered">
											<%
                                                //System.out.println("empList1 ===> " + empList1);
                                                if (empList1 != null && !empList1.equals("") && !empList1.isEmpty()) {
                                                	Map<String, String> hmEmpLocation = (Map<String, String>)request.getAttribute("hmEmpLocation");
                                                	Map<String, String> hmWLocation = (Map<String, String>)request.getAttribute("hmWLocation");
                                                	Map<String, String> hmEmpCodeDesig = (Map<String, String>)request.getAttribute("hmEmpCodeDesig");
                                               
                                                %>
											<thead>
												<tr>
													<th><input
														onclick="checkUncheckValue1();" type="checkbox"
														name="allEmp1" id="allEmp1">
													</th>
													<th align="center">Employee</th>
													<th align="center">Designation</th>
													<th align="center">Location</th>
												</tr>
											</thead>
											<tbody>
												<%
                                                    for (int i = 0; i < empList1.size(); i++) {
                                                    
                                                    				String empID = ((FillEmployee) empList1.get(i)).getEmployeeId();
                                                    				String empName = ((FillEmployee) empList1.get(i)).getEmployeeCode();
                                                    
                                                    				String emplocationID = (empID == null || empID.equals("")) ? "" : hmEmpLocation.get(empID);
                                                    				String location = (emplocationID == null || emplocationID.equals("")) ? "" : uF.showData(hmWLocation.get(emplocationID), "");
                                                    
                                                    				String desig = (empID == null || empID.equals("")) ? "" : uF.showData(hmEmpCodeDesig.get(empID), "");
                                                    				//System.out.println("FE/3463--empID ===>> " + empID);
                                                    %>
                                                    
												<tr>
													<td><input type="checkbox" name="strHiringEmpId1"
														id="strHiringEmpId1<%=i%>"
														onclick="getHiringSelectedEmp1(this.checked,this.value);"
														value="<%=empID%>"
														<%if (hmCheckEmpList1 != null && hmCheckEmpList1.get(empID) != null) {%>
														checked="checked" <%}%>> <%--  --%>
													</td>
													<!-- Created By Dattatray Date:22-July-2021 Note: empId encrypt  -->
													<td nowrap="nowrap"><a href="javascript: void(0);" onclick="openPanelEmpProfilePopup('<%=empID %>')"><%=empName%></a>
													</td>
													<td nowrap="nowrap"><%=desig%></td>
													<td nowrap="nowrap"><%=location%></td>
												</tr>
												<%
                                                    }
                                                    		} else {
                                                    %>
												<tr>
													<td colspan="3">
														<div class="nodata msg">
															<span>No Employee Found</span>
														</div>
													</td>
												</tr>
												<%
                                                    }
                                                    %>
											</tbody>
										</table>
									</div>
									<div class="col-lg-5 col-md-5 col-sm-12" id="idEmployeeInfo1" style="border: 2px solid rgb(238, 238, 238);padding: 5px;margin: 5px;height:250px;overflow-y:auto;">
                                        <%
                                            List<String> selectEmpList1 = (List<String>) request.getAttribute("selectEmpList1");
                                            		if (selectEmpList1 != null) {
                                            %>
                                        <div style="border: 2px solid #ccc;">
                                            <div style="padding: 8px 20px; border: 2px solid lightgray;"><b>Employee</b></div>
                                            <%
                                                for (int i = 0; i < selectEmpList1.size(); i++) {
                                                %>
                                            <div style="float: left; width: 100%; margin: 5px;"><strong><%=i + 1%>.</strong>&nbsp;&nbsp;<%=selectEmpList1.get(i)%></div>
                                            <% } %>
                                        </div>
                                        <% } else {
                                            %>
                                        <div class="nodata msg" style="width: 85%">
                                            <span>No Employee selected</span>
                                        </div>
                                        <%	} %>
                                        <input type="hidden" name="empselected1" id="empselected1" value="<%=uF.showData((String)request.getAttribute("empselected1"), "0") %>"/>
                                    </div>
                                </div>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
            
            <tr>
                <%
                    String notes="";
                    if(request.getAttribute("notes")!=null){
                    	notes=(String)request.getAttribute("notes");
                    }
				%>
				<%
		            List<String> reqBusinessBenefits = hmValidationFields.get("REQ_BUSINESS_BENEFITS"); 
					validAsterix = "";
					validReqOpt = "";
					if(reqBusinessBenefits != null && uF.parseToBoolean(reqBusinessBenefits.get(0))) {
						validAsterix = "<sup>*</sup>";
						validReqOpt = "validateRequired";
					}
	           	%>	
                <td class="txtlabel alignRight" valign="top">Business Benefits:<%=validAsterix %></td>
                <td><textarea name="notes" id="notes" cols="30" rows="4" class="<%=validReqOpt %>"><%=notes%></textarea>
                </td>
            </tr>
            
            <tr>
            <%
	            List<String> reqHiringManager = hmValidationFields.get("REQ_HIRING_MANAGER"); 
				validAsterix = "";
				validReqOpt = "";
				if(reqHiringManager != null && uF.parseToBoolean(reqHiringManager.get(0))) {
					validAsterix = "<sup>*</sup>";
					validReqOpt = "validateRequired";
				}
           	%>
                <td class="txtlabel alignRight" style="padding-top: 22px;" valign="top">Hiring Manager/ Recruiter:<%=validAsterix %></td>
                <td style="padding-top: 14px;">
	                <% if(reqHiringManager != null && uF.parseToBoolean(reqHiringManager.get(0))) { %>
	                    <s:select theme="simple" name="hiringManager" cssClass="validateRequired" list="hrAndGlobalHrList" listKey="employeeId" id="hiringManager" listValue="employeeCode"
	                        size="5" multiple="true"></s:select>
					<% } else { %>
						<s:select theme="simple" name="hiringManager" list="hrAndGlobalHrList" listKey="employeeId" id="hiringManager" listValue="employeeCode" size="5" multiple="true"></s:select>
					<% } %>
                </td>
            </tr>
            <!-- Created By Dattatray Date:26-08-21 Note:postion changed down to up -->
             <% 
            //System.out.println("hmFeatureStatus.get(IConstants.F_INSTANCE_NAMEWISE_FEATURE) : "+hmFeatureStatus.get(IConstants.F_INSTANCE_NAMEWISE_FEATURE));
            if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_INSTANCE_NAMEWISE_FEATURE)) && hmFeatureUserTypeId.get(IConstants.F_INSTANCE_NAMEWISE_FEATURE+"_USER_IDS")!=null && hmFeatureUserTypeId.get(IConstants.F_INSTANCE_NAMEWISE_FEATURE+"_USER_IDS").contains(IConstants.INTELIMENT)) { %>
	            <!-- Start Dattatray Date:21-08-21 -->
	            <tr id="jd_category_id">
	            <%
		            List<String> reqJDCategory = hmValidationFields.get("REQ_JD_CATEGORY"); 
					validAsterix = "";
					validReqOpt = "";
					if(reqJDCategory != null && uF.parseToBoolean(reqJDCategory.get(0))) {
						validAsterix = "<sup>*</sup>";
						validReqOpt = "validateRequired";
					}
	           	%>
	                <td class="txtlabel alignRight">Category:<%=validAsterix %></td>
	                <td>
	                <!-- Start Dattatray Date:25-08-21  -->
                <%
                if(reqJDCategory != null && uF.parseToBoolean(reqJDCategory.get(0))) {
                %>
                    <s:select theme="simple" name="strCategory" id="strCategory" cssClass="validateRequired" headerKey="" headerValue="Select Category" list="#{'1':'Technical','2':'Functional'}"/>
               <% } else { %>
               		<s:select theme="simple" name="strCategory" id="strCategory" cssClass="" headerKey="" headerValue="Select Category" list="#{'1':'Technical','2':'Functional'}"/>
               <% } %>
                <!-- End Dattatray Date:25-08-21  -->
	                   
	                </td>
	            </tr>
	            
	            <tr>
	            <%
		            List<String> reqTechnology = hmValidationFields.get("REQ_TECHNOLOGY"); 
					validAsterix = "";
					validReqOpt = "";
					if(reqTechnology != null && uF.parseToBoolean(reqTechnology.get(0))) {//Created Dattatray Date:25-08-21 Note:reqTechnology
						validAsterix = "<sup>*</sup>";
						validReqOpt = "validateRequired";
					}
	           	%>
	                <td class="txtlabel alignRight">Technology:<%=validAsterix %></td>
	                <td>
	                    <!-- Start Dattatray Date:25-08-21  -->
                 <%
                	if(reqTechnology != null && uF.parseToBoolean(reqTechnology.get(0))) {
                  %>
                    <s:select theme="simple" name="strTechnology" id="strTechnology" listKey="technologyId" listValue="technologyName" list="technologyList" cssClass="validateRequired"  headerKey="" headerValue="Select Technology"/>
                 <% } else { %>
                 	<s:select theme="simple" name="strTechnology" id="strTechnology" listKey="technologyId" listValue="technologyName" list="technologyList" cssClass=""  headerKey="" headerValue="Select Technology"/>
                 <% } %>
                 <!-- End Dattatray Date:25-08-21  -->
	                </td>
	            </tr>
	             <!-- End Dattatray Date:21-08-21 -->
             <% } %>
             
            <tr id="policyid">
            <%
	            List<String> reqCustomerName = hmValidationFields.get("REQ_CUSTOMER_NAME"); 
				validAsterix = "";
				validReqOpt = "";
				if(reqCustomerName != null && uF.parseToBoolean(reqCustomerName.get(0))) {
					validAsterix = "<sup>*</sup>";
					validReqOpt = "validateRequired";
				}
           	%>
                <td class="txtlabel alignRight">Customer Name:<%=validAsterix %></td>
                <td>
                <% if(reqCustomerName != null && uF.parseToBoolean(reqCustomerName.get(0))) { %>
                    <s:select theme="simple" name="strCustomer" id="strCustomer" cssClass="validateRequired" listKey="clientId" listValue="clientName" list="clientList" onchange="checkOtherCustomer(this.value);"/>
				<% } else { %>
					<s:select theme="simple" name="strCustomer" id="strCustomer" listKey="clientId" listValue="clientName" list="clientList" onchange="checkOtherCustomer(this.value);"/>
				<% } %>
				<div id="otherCustomerDiv" style="margin-top: 5px;">
				<% if(reqCustomerName != null && uF.parseToBoolean(reqCustomerName.get(0))) { %>
						<s:textfield name="strOtherCustomer" id="strOtherCustomer" cssClass="validateRequired" onblur="getClient('otherCustomerMsgDiv', this.value)" onchange ="getClient('otherCustomerMsgDiv', this.value)"/>
					<% } else { %>
						<s:textfield name="strOtherCustomer" id="strOtherCustomer" onblur="getClient('otherCustomerMsgDiv', this.value)" onchange ="getClient('otherCustomerMsgDiv', this.value)"/>
					<% } %>	
					<div id="otherCustomerMsgDiv"></div>
				</div>
                </td>
            </tr>
           
            <tr>
            	<td></td>
            	<td>
                    <s:if test="recruitmentID==null">
                        <s:submit cssClass="btn btn-primary" id ="strInsert" name="strInsert" value="Send Job Requirement Request"/>
                    </s:if>
                    <s:else>
                        <s:submit cssClass="btn btn-primary" id="strInsert" name="strInsert" value="Update" align="center" />
                    </s:else>
                </td>
            </tr>
        </table>
    </s:form>
</div>

<script>
$(function(){
	

	<%if(frmPage != null){ %>
	$("#formID").submit(function(event){
		event.preventDefault();
		var from = '<%=frmPage%>';
        
		var form_data = $("#formID").serialize();
		if(from != null && (from == "RAD" || from == "JR" || from == "WF")) {
			var divResult = 'divResult';
			var strBaseUserType = document.getElementById("strBaseUserType").value;
			var strCEO = '<%=IConstants.CEO %>';
			var strHOD = '<%=IConstants.HOD %>';
			
			if(strBaseUserType == strCEO || strBaseUserType == strHOD) {
				divResult = 'subDivResult';
			}
			//alert("divResult ---------- " + divResult);
			var recruitmentID = document.getElementById("recruitmentID").value;
			var orgID = document.getElementById("orgID").value;
			var wlocID = document.getElementById("wlocID").value;
			var desigID = document.getElementById("desigID").value;
			var checkStatus = document.getElementById("checkStatus").value;
			var fdate = document.getElementById("fdate").value;
			var tdate = document.getElementById("tdate").value;
			var currUserType = document.getElementById("currUserType").value;
			
			var strInsert = "Send Job Requirement Request";
			if(parseInt(recruitmentID) > 0) {
				strInsert = "Update";
			}
			$.ajax({
				type :'POST',
				url  :'RequirementRequest.action',
				data :form_data+"&strInsert="+strInsert,
				cache:true,
				success : function(result) {
					if(from != null && (from == "JR" || from == "WF")) {
						$.ajax({
							url: 'RecruitmentDashboard.action?fromPage='+from,
							cache: true,
							success: function(result){
								$("#"+divResult).html(result);
					   		}
						});
					} else {
						$.ajax({
							url: 'RequirementApproval.action?f_org='+orgID+'&location1='+wlocID+'&designation='+desigID+'&checkStatus='+checkStatus
								+'&fdate='+fdate+'&tdate='+tdate+'&currUserType='+currUserType,
							cache: true,
							success: function(result){
								$("#divResult").html(result);
					   		}
						});
					}
				},
				error : function(error) {
					if(from != null && (from == "JR" || from == "WF")) {
						$.ajax({
							url: 'RecruitmentDashboard.action?fromPage='+from,
							cache: true,
							success: function(result){
								$("#"+divResult).html(result);
					   		}
						});
					} else {
						$.ajax({
							url: 'RequirementApproval.action?f_org='+orgID+'&location1='+wlocID+'&designation='+desigID+'&checkStatus='+checkStatus
									+'&fdate='+fdate+'&tdate='+tdate+'&currUserType='+currUserType,
							cache: true,
							success: function(result){
								$("#divResult").html(result);
					   		}
						});
					}
				}
			});
			
			
		}
	});
	<% } %>
});


</script>

<%-- <script>
$( function() {
    $.widget( "custom.combobox", {
      _create: function() {
        this.wrapper = $( "<span>" ).addClass( "custom-combobox" ).insertAfter( this.element );
        this.element.hide();
        this._createAutocomplete();
        this._createShowAllButton();
      },
 
      _createAutocomplete: function() {
        var selected = this.element.children( ":selected" ),
			value = selected.val() ? selected.text() : "";
 
        this.input = $( "<input>" ).appendTo( this.wrapper ).val( value ).attr( "title", "" )
          .addClass( "custom-combobox-input ui-widget ui-widget-content ui-state-default ui-corner-left" )
          .autocomplete({
            delay: 0,
            minLength: 0,
            source: $.proxy( this, "_source" )
          })
          .tooltip({
            classes: {
              "ui-tooltip": "ui-state-highlight"
            }
          });
 
        this._on( this.input, {
          autocompleteselect: function( event, ui ) {
            ui.item.option.selected = true;
            this._trigger( "select", event, {
              item: ui.item.option
            });
          },
 
          autocompletechange: "_removeIfInvalid"
        });
      },
 
      _createShowAllButton: function() {
        var input = this.input,
          wasOpen = false;
 
        $( "<a>" ).attr( "tabIndex", -1 ).attr( "title", "Show All Items" ).tooltip().appendTo( this.wrapper ).button({
            icons: {
              primary: "ui-icon-triangle-1-s"
            },
            text: false
          }).removeClass( "ui-corner-all" ).addClass( "custom-combobox-toggle ui-corner-right" )
          .on( "mousedown", function() {
            wasOpen = input.autocomplete( "widget" ).is( ":visible" );
          })
          .on( "click", function() {
            input.trigger( "focus" );
 
            // Close if already visible
            if ( wasOpen ) {
              return;
            }
 
            // Pass empty string as value to search for, displaying all results
            input.autocomplete( "search", "" );
          });
      },
 
      _source: function( request, response ) {
        var matcher = new RegExp( $.ui.autocomplete.escapeRegex(request.term), "i" );
        response( this.element.children( "option" ).map(function() {
          var text = $( this ).text();
          if ( this.value && ( !request.term || matcher.test(text) ) )
            return {
              label: text,
              value: text,
              option: this
            };
        }) );
      },
 
      _removeIfInvalid: function( event, ui ) {
 
        // Selected an item, nothing to do
        if ( ui.item ) {
          return;
        }
 
        // Search for a match (case-insensitive)
        var value = this.input.val(),
          valueLowerCase = value.toLowerCase(),
          valid = false;
        this.element.children( "option" ).each(function() {
          if ( $( this ).text().toLowerCase() === valueLowerCase ) {
            this.selected = valid = true;
            return false;
          }
        });
 
        // Found a match, nothing to do
        if ( valid ) {
          return;
        }
 
        // Remove invalid value
        this.input
          .val( "" )
          .attr( "title", value + " didn't match any item" )
          .tooltip( "open" );
        this.element.val( "" );
        this._delay(function() {
          this.input.tooltip( "close" ).attr( "title", "" );
        }, 2500 );
        this.input.autocomplete( "instance" ).term = "";
      },
 
      _destroy: function() {
        this.wrapper.remove();
        this.element.show();
      }
    });
 
    $( "#strDesignationUpdate" ).combobox();
    
  } );
	
</script> --%>

<%-- <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.10.0/js/bootstrap-select.min.js"></script> --%>