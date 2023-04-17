<%@page import="com.konnect.jpms.select.FillEmployee"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.*"%>
 
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<style>
.datepicker>div {
	display: block;
}
</style> 
 
<script src='scripts/customAjax.js'></script>
<script type="text/javascript">
	
	var cxtpath='<%=request.getContextPath()%>';
	//var pcount = 0;
	//var taskcount = 0;
	
	function checkUncheckValue(form) {
		var allEmp=document.getElementById("allEmp");		
		var strGoalEmpId = document.getElementsByName('strGoalEmpId');
		var selectID="";
		var status=false;
		if(allEmp.checked==true){
			status=true;
			 for(var i=0;i<strGoalEmpId.length;i++){
				 strGoalEmpId[i].checked = true;
				  if(i==0){
					  selectID=strGoalEmpId[i].value;
				  }else{
					  selectID+=","+strGoalEmpId[i].value;
				  }
			 }
		}else {		
			status=false;
			 for(var i=0;i<strGoalEmpId.length;i++){
				 strGoalEmpId[i].checked = false;
				  if(i==0){
					  selectID=strGoalEmpId[i].value;
				  }else{
					  selectID+=","+strGoalEmpId[i].value;
				  }
			 }
		}
		//alert(selectID);
		var empselect=document.getElementById("empselected").value;
		var action='GetSelectedEmployee.action?type=all&chboxStatus='+status+'&selectedEmp='+selectID+'&existemp='+empselect+'&form='+form;
		getContent('idEmployeeInfo',action); 
	}
	
	
	function getGoalSelectedEmp(checked, emp, form, isInIndiGoal) {
				//updated by kalpana on 22 oct 2016
		var empselect=document.getElementById("empselected").value;
		if(empselect != '' && empselect !='0') {
			if(document.getElementById("cmeasureEffortsHrs")) {
				document.getElementById("cmeasureEffortsHrs").value = '';
			}
		}
		
		if(checked == true) {
			alrtMsg = "Are you sure, you want to add this employee?";
		} else {
			alrtMsg = "Are you sure, you want to remove this employee?";
		}
		if(confirm(alrtMsg)) {
			var empselect=document.getElementById("empselected").value;
			var action='GetSelectedEmployee.action?type=one&chboxStatus='+checked+'&selectedEmp='+emp+'&existemp='+empselect+'&form='+form;
			getContent('idEmployeeInfo',action);
			
			if(checked == false) {
             	//alert(checked);
             	document.getElementById("strGoalEmpId"+emp).checked = false;
			}
		} else {
			if(checked == true) {
				document.getElementById('strGoalEmpId'+emp).checked = false;
			}
		}
	}
	
	function addKRA(ch, count, goalCnt) {
		var KRACount = document.getElementById(ch + "KRACount" + count+goalCnt).value;
		var KRACountID = ch + "KRACount" + count+goalCnt;
		
		 var totweight=0;
			for(var i=0; i<=parseInt(KRACount); i++) {
				var weight = document.getElementById("cKRAWeightage_"+i+goalCnt);
				if (weight == null) {
					continue;	
				}
				weight = document.getElementById("cKRAWeightage_"+i+goalCnt).value;
				if(weight == undefined) {
					weight = 0;
				}
				totweight = totweight + parseFloat(weight);
			}
			var remainweight = 100 - parseFloat(totweight);
			if(remainweight <= 0) {
				alert("Unable to add KRA because of no weightage available");			
			} else {
				//pcount++;
				KRACount++;
				var divid = ch+"KRAdiv"+count+"_"+KRACount+goalCnt;
				var divtag = document.createElement('div');
				divtag.setAttribute("style", "width: 100%; float: left; margin-bottom: 3px; border-bottom: 1px solid #F1F1F1;");
				divtag.id = divid;
							
				var data = "<div style=\"width: 100%; float: left; margin-bottom: 3px;\"><input type=\"hidden\" name=\"KRATaskCount_"+KRACount+goalCnt+"\" id=\"KRATaskCount_"+KRACount+goalCnt+"\" value=\"0\">"
					+"<input type=\"text\" name=\""+ch+"KRA_"+KRACount+goalCnt+"\" id=\""+ch+"KRA_"+KRACount+goalCnt+"\" class=\"validateRequired form-control \"/> "
					+" Weightage (%): <input type=\"text\" class=\"validateRequired form-control \" style=\"width: 40px !important;\" name=\"cKRAWeightage_"+KRACount+goalCnt+"\" id=\"cKRAWeightage_"+KRACount+goalCnt+"\" value=\""+remainweight+"\" style=\"width: 40px !important;\" onkeyup=\"validateKRAScore(this.value,'cKRAWeightage_"+KRACount+goalCnt+"', '"+ch+"', '"+count+"', '"+goalCnt+"');\" onkeypress=\"return isNumberKey(event)\" />"
					+"<a href=\"javascript:void(0)\" onclick=\"addKRA('"+ ch + "',"+ count+ ", '"+goalCnt+"');\"><i class=\"fa fa-plus-circle\"></i>Add Initiative</a>&nbsp;"
					+"<a href=\"javascript:void(0)\" onclick=\"removeKRAID('" + divid + "','" + KRACountID + "');\" class=\"close-font\">"
					+"Remove KRA</a>"
					+"</div><div style=\"width: 100%; float: left; margin-bottom: 3px;\"><span style=\"float: left; margin-left: 7px;\">"
					+"<a href=\"javascript:void(0)\" onclick=\"addKRATask('"+ ch + "',"+ count+ ", "+KRACount+", '"+goalCnt+"');\"><i class=\"fa fa-plus-circle\"></i>Add Task</a></span>"
					/* +"<span style=\"float: left; margin-left: 27px;\"><a href=\"javascript:void(0)\" class=\"\" style=\"color: #68AC3B;\" "
					+"onclick=\"selectKRATask('"+ ch + "', "+ count+ ", "+KRACount+", '"+goalCnt+"');\">Select Task</a></span>" */
					+"</div><div id=\""+ch+"KRATaskdiv"+ count+"_"+KRACount+goalCnt+"\" style=\"float: left; margin-left: 50px; margin-bottom: 3px;\"></div>";
				divtag.innerHTML = data;
				document.getElementById(ch + "KRAtdID" + count+goalCnt+"").appendChild(divtag);
				
				document.getElementById(ch + "KRACount" + count+goalCnt+"").value = KRACount;
				
			}
	}
	
	
	
	function validateKRAScore(value, weightageId, ch, count, goalCnt) {
		
		var KRACount = document.getElementById(ch + "KRACount" + count+goalCnt).value;
		var totweight=0;
		for(var i=0; i<=parseInt(KRACount); i++) {
			var checkCurrId = "cKRAWeightage_"+i+goalCnt;
			var weight = document.getElementById("cKRAWeightage_"+i+goalCnt);
			if (weight == null) {
				continue;	
			}
			if(weightageId == checkCurrId) {
				//	alert("same id");
			} else {
				weight = document.getElementById("cKRAWeightage_"+i+goalCnt).value;
				if(weight == undefined) {
					weight = 0;
				}
				totweight = totweight + parseFloat(weight);
			}
		}
		
		var remainweight = 100 - parseFloat(totweight);
       
		if(parseFloat(value) > parseFloat(remainweight)) {
			alert("Entered value greater than Weightage");
			document.getElementById(weightageId).value = remainweight;
		} else if(parseFloat(value) <= 0) {
			alert("Invalid Weightage");
			document.getElementById(weightageId).value = remainweight;
		}
		
	}
	
	
	function removeKRAID(id, KRACountID) {
		var kraCnt = document.getElementById(KRACountID).value;
		
		var row_skill = document.getElementById(id);
		if (row_skill && row_skill.parentNode && row_skill.parentNode.removeChild) {
			row_skill.parentNode.removeChild(row_skill);
			if(parseInt(kraCnt) > 0) {
				kraCnt--;
			}
			document.getElementById(KRACountID).value = kraCnt;
		}
	}
	
		function addKRATask(ch, count, kraCnt, goalCnt) {
			//alert("count --->>>> " + count+" -- kraCnt--->>>> " + kraCnt);
			var taskcount = document.getElementById("KRATaskCount_"+kraCnt+goalCnt).value;
			taskcount++;
			//alert("taskcount --->>>> " + taskcount);
				
			var divid = ch+"KRATaskDIV"+count+"_"+kraCnt+"_"+taskcount+goalCnt;
			var divtag = document.createElement('div');
			divtag.setAttribute("style", "width: 100%; float: left; margin-bottom: 3px;");
			divtag.id = divid;
			var data = "<input type=\"text\" name=\""+ch+"KRATask_"+kraCnt+goalCnt+"\" id=\""+ch+"KRATask_"+kraCnt+goalCnt+"\" class=\"validateRequired form-control \"/> "
				+"<a href=\"javascript:void(0)\" onclick=\"addKRATask('"+ch+"', '"+count+"', '"+kraCnt+"', '"+goalCnt+"');\"><i class=\"fa fa-plus-circle\"></i>Add Task</a>&nbsp;"
				+"<a href=\"javascript:void(0)\"  class=\"close-font\" onclick=\"removeKRATaskID('" + divid + "','" + kraCnt + "','" + goalCnt + "');\">"
				+"Remove Task</a>";
				//alert("data ====>>> " + data);
			divtag.innerHTML = data;
			document.getElementById(ch + "KRATaskdiv"+count+"_"+kraCnt+goalCnt).appendChild(divtag);

			document.getElementById("KRATaskCount_"+kraCnt+goalCnt).value = taskcount;
		}
	
		function removeKRATaskID(id,kraCnt,goalCnt) {
			var taskcount = document.getElementById("KRATaskCount_"+kraCnt+goalCnt).value;
		//	alert("divId==>"+id);
			var row_skill = document.getElementById(id);
			if (row_skill && row_skill.parentNode && row_skill.parentNode.removeChild) {
				row_skill.parentNode.removeChild(row_skill);
				if(parseInt(taskcount)>0) {
					taskcount--;
				}
			}
			document.getElementById("KRATaskCount_"+kraCnt+goalCnt).value = taskcount;
			
		}

	function getMeasureWith(value, ch, count) {
		//$ Effort cdollarAmtid0 cmeasureEffortsid0  
		//cmeasureEffortsHrs cmeasureEffortsDays cmeasureDollar
		/*  document.getElementById("cmeasureEffortsHrs").value="";
		 document.getElementById("cmeasureEffortsDays").value="";
		 document.getElementById("cmeasureDollar").value=""; 
		 Amount Percentage
		 */
		if (value == 'Amount' || value == 'Percentage') {
			if (value == 'Amount') {
				document.getElementById("percentSpan" + count).style.display = "none";
				document.getElementById("rsSpan" + count).style.display = "block";
		    }else{
					document.getElementById("percentSpan" + count).style.display = "block";
					document.getElementById("rsSpan" + count).style.display = "none";
			}
			document.getElementById(ch + "dollarAmtid" + count).style.display = "table-row";
			document.getElementById(ch + "measureEffortsid" + count).style.display = "none";
		
		} else if (value == 'Effort') {
			document.getElementById(ch + "dollarAmtid" + count).style.display = "none";
			document.getElementById(ch + "measureEffortsid" + count).style.display = "table-row";
			
		} else {
			document.getElementById(ch + "measureEffortsid" + count).style.display = "none";
			document.getElementById(ch + "dollarAmtid" + count).style.display = "none";
			document.getElementById("measureSpanId").innerHTML="";
		}
	}

	
	function showMeasureWith(value) {
		//cMKRAID0 Yes No 
		if (value == 'Yes') {
			document.getElementById("cAddMKra0").value = "Measure";
			document.getElementById("measureWith").style.display = "table-row";
			document.getElementById("cdollarAmtid0").style.display = "table-row";
						
		} else {
			document.getElementById("cAddMKra0").value = "";
			document.getElementById("measureWith").style.display = "none";
			document.getElementById("cdollarAmtid0").style.display = "none";
			//updated by kalpana on 17/10/2016 added below line
			document.getElementById("cmeasureEffortsid0").style.display = "none";
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
		var action = 'getGoalEmployeeList.action?strOrg=' + strID;
		
		document.getElementById("wlocation").selectedIndex = 0;
		document.getElementById("depart").selectedIndex = 0;
		document.getElementById("strLevel").selectedIndex = 0;
		document.getElementById("desigIdV").selectedIndex = 0;
	
		var supervisorId = document.getElementById("supervisorId").value;
		var goaltype = document.getElementById("goaltype").value;
		if (goaltype == '3' || goaltype == '4') {
			// || goaltype=='4'
			action += '&supervisor=' + supervisorId;
		}
		$("#myEmployee").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		getContent('myEmployee', action);
		getWLocDepartLevelDesigByOrg(strID,'org');
		
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
	    
	    var selectedEmp = document.getElementById("empselected").value;
	    
		var action = 'getGoalEmployeeList.action?strOrg='+ strID +'&location='+ location + '&selectedEmp=' + selectedEmp;

		document.getElementById("depart").selectedIndex = 0;
		document.getElementById("strLevel").selectedIndex = 0;
		document.getElementById("desigIdV").selectedIndex = 0;
	
		var supervisorId = document.getElementById("supervisorId").value;
		var goaltype = document.getElementById("goaltype").value;
		if (goaltype == '3' || goaltype == '4') {
			// || goaltype=='4'
			action += '&supervisor=' + supervisorId;
		}
		$("#myEmployee").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		getContent('myEmployee', action);
		
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

	    var selectedEmp = document.getElementById("empselected").value;
	    
		document.getElementById("strLevel").selectedIndex = 0;
		document.getElementById("desigIdV").selectedIndex = 0;
	
		var action = 'getGoalEmployeeList.action?depart=' + depart + '&selectedEmp=' + selectedEmp;
		
		var supervisorId = document.getElementById("supervisorId").value;
		var goaltype = document.getElementById("goaltype").value;
		if (goaltype == '3' || goaltype == '4') {
			// || goaltype=='4'
			action += '&supervisor=' + supervisorId;
		}
		
		if (strID == '' && location == '') {
		} else {
			if (strID != '') {
				action += '&strOrg='+ strID;
			}
			if (location != '') {
				action += '&location=' + location;
			}
			$("#myEmployee").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			getContent('myEmployee', action);
		}
		//searchTextField();
		
	}

	function getEmployeebyLevel() {
		var location = getSelectedValue("wlocation");
		var depart = getSelectedValue("depart");
		var level = getSelectedValue("strLevel");
		
		var strID = null;
		if(document.getElementById("strOrg")){
			strID = getSelectedValue("strOrg"); 
	    }
		
	    if(document.getElementById("hideOrgid")){
	    	strID = document.getElementById("hideOrgid").value;
	    }

	    var selectedEmp = document.getElementById("empselected").value;
	    
		document.getElementById("desigIdV").selectedIndex = 0;
		var action = 'getGoalEmployeeList.action?level=' + level + '&selectedEmp=' + selectedEmp;
		
		var supervisorId = document.getElementById("supervisorId").value;
		var goaltype = document.getElementById("goaltype").value;
		if (goaltype == '3' || goaltype == '4') {
			// || goaltype=='4'
			action += '&supervisor=' + supervisorId;
		}
		goalCnt
		
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
		$("#myEmployee").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			getContent('myEmployee', action);
			window.setTimeout(function() {
				getContent('myDesig', 'GetDesignationByLevel.action?strLevel=' + level+'&strOrg='+ strID); 
			}, 200); 
		
		//searchTextField()

	}

	function getEmployeebyDesig() {
		var location = getSelectedValue("wlocation");
		var depart = getSelectedValue("depart");
		var Level = getSelectedValue("strLevel");
		var design = getSelectedValue("desigIdV");
		
		var strID = null;
		if(document.getElementById("strOrg")){
			strID = getSelectedValue("strOrg"); 
	    }
		
	    if(document.getElementById("hideOrgid")){
	    	strID = document.getElementById("hideOrgid").value;
	    }

	    var selectedEmp = document.getElementById("empselected").value;
	    
		var action = 'getGoalEmployeeList.action?design=' + design + '&selectedEmp=' + selectedEmp;
		
		var supervisorId = document.getElementById("supervisorId").value;
		var goaltype = document.getElementById("goaltype").value;
		if (goaltype == '3' || goaltype == '4') {
			// || goaltype=='4'
			action += '&supervisor=' + supervisorId;
		}

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
			//alert("action======>"+action);
			$("#myEmployee").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			getContent('myEmployee', action);
			
		}
		
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
	    //alert("strID ==> " + strID);
		//var strID = getSelectedValue("strOrg");
		getWLocDepartLevelDesigByOrg(strID, 'org');
	}
	
	function getWLocDepartLevelDesigByOrg(strID, type){
		//alert("strID ===> " + strID + " type ===> " + type);
		var xmlhttp = GetXmlHttpObject();
		if (xmlhttp == null) {
			alert("Browser does not support HTTP Request");
			return;
		} else {
			var xhr = $.ajax({
				//url : "ValidateTimeSheet.action?emp_id=" + emp+"&timesheet_paycycle="+cycle,
				url : "GetGoalFilters.action?strOrg="+strID+"&type="+type ,		
				cache : false,
				success : function(data) {
					//alert("data == "+data);
                	if(data == ""){
                		
                	}else{
                		//alert("data ==>    "+data);
                		var allData = data.split("::::");
                		  if(type =='org'){
                			   if(document.getElementById("wlocationDiv")) {
                				   document.getElementById("wlocationDiv").innerHTML = allData[0];
                			   }
		                        
                			   if(document.getElementById("departDiv")) {
                				   document.getElementById("departDiv").innerHTML = allData[1];
                			   }
                			   
                			   if(document.getElementById("levelDiv")) {
                				   document.getElementById("levelDiv").innerHTML = allData[2];
                			   }
		                        
                			   if(document.getElementById("levelDiv")) { 
		                        document.getElementById("myDesig").innerHTML = allData[3];
                			   }
                		  } else if(type =='wloc'){
                			  if(document.getElementById("departDiv")) {
		                        document.getElementById("departDiv").innerHTML = allData[0];
                			  }
                		  } else if(type =='level'){
                			  if(document.getElementById("myDesig")) {
		                        document.getElementById("myDesig").innerHTML = allData[0];
                			  }
                		  }
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
	

function getAttributes(value, goalCnt) {

	 var strID = null;
	 if(document.getElementById("hideOrgid")){
	    	strID = document.getElementById("hideOrgid").value;
	 }
	 //alert("strID ===>> " + strID);
	 var action = 'GetAttributeList.action?elementID='+value+'&orgId='+strID+'&goalCnt='+goalCnt+'&type=MULTIKRA';
	 getContent('attributeDiv'+goalCnt, action);

 }

	function getSelectedValue(selectId) {
		//alert("selectId======>"+selectId);
		var choice = document.getElementById(selectId);
		var exportchoice = "";
		for ( var i = 0, j = 0; i < choice.options.length; i++) {
			if (choice.options[i].selected == true) {
			//	alert("choice.options[i].value======>"+choice.options[i].value);
				if (j == 0) {
					exportchoice = choice.options[i].value;
					j++;
				} else {
					exportchoice += "," + choice.options[i].value;
					j++;
				}
			}
		}
		//alert("exportchoice======>"+exportchoice);
		return exportchoice;
	}

	
	function checkFrequency(value, id) {
		if (value == '3') {
			
			document.getElementById("weekly"+id).style.display = "none";
			document.getElementById("monthly"+id).style.display = "block";
			
			document.getElementById("weekday"+id).selectedIndex = 0;
			document.getElementById("day"+id).selectedIndex = 0;
						
		} else if (value == '2') {
			document.getElementById("weekly"+id).style.display = "block";
			document.getElementById("monthly"+id).style.display = "none";
			
			document.getElementById("weekday"+id).selectedIndex = 0;
			document.getElementById("day"+id).selectedIndex = 0;
								
		}else if (value == '6') {
			document.getElementById("weekly"+id).style.display = "none";
			document.getElementById("monthly"+id).style.display = "none";
			
			document.getElementById("weekday"+id).selectedIndex = 0;
			document.getElementById("day"+id).selectedIndex = 0;
			
						
		}else if (value == '4' || value == '5') {
			document.getElementById("weekly"+id).style.display = "none";
			document.getElementById("monthly"+id).style.display = "none";
						
			document.getElementById("weekday"+id).selectedIndex = 0;
			document.getElementById("day"+id).selectedIndex = 0;
			
		} else {
			document.getElementById("weekly"+id).style.display = "none";
			document.getElementById("monthly"+id).style.display = "none";
			
			document.getElementById("weekday"+id).selectedIndex = 0;
			document.getElementById("day"+id).selectedIndex = 0;
			
		}
	}
	
	
	function validateScore(value, weightageId) {
		var goalCnt = document.getElementById("goalCnt").value;
		//alert("goalCnt ===>> " + goalCnt);
		var totweight=0;
		if(weightageId == 'cgoalWeightage') {
		} else {
			 //alert("in cgoalWeightage weightageId ===>> " + weightageId);
			var weight = document.getElementById("cgoalWeightage").value;
			if(weight == undefined) {
				weight = 0;
			}
			totweight = totweight + parseFloat(weight);
		 }
		 	//alert("goalCnt ===>> " + goalCnt);
		 
			for(var i=1; i <= parseInt(goalCnt); i++) {
				var checkCurrId = "cgoalWeightage_"+i;
				var weight = document.getElementById("cgoalWeightage_"+i);
				if (weight == null) {
					continue;	
				}
				if(weightageId == checkCurrId) {
					//	alert("same id");
				} else {
					weight = document.getElementById("cgoalWeightage_"+i).value;
					if(weight == undefined) {
						weight = 0;
					}
					totweight = totweight + parseFloat(weight);
				}
			}
			var remainweight = 100 - parseFloat(totweight);

			if(parseFloat(value) > parseFloat(remainweight)) {
				alert("Entered value greater than Weightage");
				document.getElementById(weightageId).value = remainweight;
			} else if(parseFloat(value) <= 0 ) {
				alert("Invalid Weightage");
				document.getElementById(weightageId).value = remainweight;
			}
	}
	
	function openPanelEmpProfilePopup(empId) {
		var dialogEdit = '.modal-body1';
    	$(dialogEdit).empty();
    	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$('.modal-title1').html('Employee Information');
    	$("#profileInfo").show();
    	if($(window).width() >= 900) {
    		$(".proDialog").width(900);
    	}
    	$.ajax({
    		url :"MyProfile.action?empId="+empId+"&proPopup=proPopup",
    		cache : false,
    		success : function(data) {
    			$(dialogEdit).html(data);
    		}
    	});
    	
			/* var id=document.getElementById("panelDiv");
			if(id){
				id.parentNode.removeChild(id);
			}
			var dialogEdit = '#proBody';
			$(dialogEdit).empty();
			$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$("#profileInfo").show();
			if($(window).width() >= 900){
				$(".proDialog").width(900);
			}
			$.ajax({
				url :"MyProfile.action?empId="+empId+"&proPopup=proPopup",
				cache : false,
				success : function(data) {
					$(dialogEdit).html(data);
				}
			}); */
	}
	
	function showTeamGoals(value) {
		if(value == 'Yes') {
			document.getElementById("teamgoalDiv").style.display = "block";
		} else {
			document.getElementById("teamgoalDiv").style.display = "none";
		}
	}

	function showPerspective(value) {
		if(value == 'Yes') {
			document.getElementById("perspectiveDiv").style.display = "block";
		} else {
			document.getElementById("perspectiveDiv").style.display = "none";
		}
	}
	
	function checkFields() {
		if(document.getElementById("corporateGoal")) {
			if(document.getElementById("corporateGoal").value == "") { 
				return false;
			}
		} 
		
		if(document.getElementById("priority")) {
			if(document.getElementById("priority").value == "") { 
				return false;
			}
		}
		
		if(document.getElementById("goalElements")) {
			if(document.getElementById("goalElements").value == "") { 
				return false;
			}
		}
		if(document.getElementById("cgoalAlignAttribute")) {
			if(document.getElementById("cgoalAlignAttribute").value == "") { 
				return false;
			}
		}
		
		if(document.getElementById("cgoalEffectDate")) {
			if(document.getElementById("cgoalEffectDate").value == "") { 
				return false;
			}
		}
		
		if(document.getElementById("cgoalDueDate")) {
			if(document.getElementById("cgoalDueDate").value == "") { 
				return false;
			}
		}
		
		
		if(document.getElementById("cgoalWeightage")) {
			if(document.getElementById("cgoalWeightage").value == "") { 
				return false;
			}
		}
		
		if(document.getElementById("empselected")) {
			var empselected = document.getElementById("empselected").value;
			if(empselected == '' || empselected =='0'){
				alert("Please, select the employee.");
				return false;
			}
			
		}
		
		if(document.getElementById("typeas")) {
			var typeas = document.getElementById("typeas").value;
		}
		
		
		if(document.getElementById("cMainKRA")) {
			var cMainKRA = document.getElementById("cMainKRA").value;
			if(cMainKRA == ''){
				alert("Please, fill the KRA");
				return false;
			}
		}
				
		if(document.getElementById("goalCnt")) {
			var goalCnt = document.getElementById("goalCnt").value;
			for(var j=0; j<=goalCnt; j++) {
				if(document.getElementById("cKRACount0_"+j)) {
					var KRACount = document.getElementById("cKRACount0_"+j).value;
					for(var i=0; i<=KRACount; i++) {
						if(document.getElementById("cKRA_"+i+"_"+j)) {
							var KRA = document.getElementById("cKRA_"+i+"_"+j).value;
							if(KRA == '') {
								alert("Please, fill the KRA");
								return false;
							}
						}
						if(document.getElementById("cKRATask_"+i+"_"+j)) {
							var KRATask = document.getElementById("cKRATask_"+i+"_"+j).value;
							if(KRATask == '') {
								alert("Please, fill the Task");
								return false;
							}
						}
						
						if(document.getElementById("KRATaskCount_"+i+"_"+j)) {
							var KRATaskCount = document.getElementById("KRATaskCount_"+i+"_"+j).value;
							if(KRATaskCount == 0) {
								alert("Please, add the Task");
								return false;
							}
						}
					}
				}
			}
		}
		
		return true;
	}
	
	function isNumberKey(evt){
	   var charCode = (evt.which) ? evt.which : event.keyCode;
	   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46){
	      return false;
	   }
	   return true;
	}
	
	function isOnlyNumberKey(evt){
		   var charCode = (evt.which) ? evt.which : event.keyCode;
		   if (charCode <= 31 || (charCode >= 48 && charCode <= 57)) {
		      return true; 
		   }
		   return false;
		}
	
		
	function checkHrsLimit() {
		var empSelected = '';
		var from = document.getElementById("fromPage").value;
		var strEmp = '<%=(String) session.getAttribute(IConstants.EMPID)%>';
		if(document.getElementById("empselected")) {
			empSelected = document.getElementById("empselected").value;
		}
		
		var days = document.getElementById("cmeasureEffortsDays").value;
		var hrs = document.getElementById("cmeasureEffortsHrs").value;
		
		if(parseInt(days) == 0 && parseInt(hrs) == 0) {
			alert("Invalid data!");
			document.getElementById(cnt+"mDays"+goalCnt).value = '';
			document.getElementById(cnt+"msHrs"+goalCnt).value = '';
		
		}else {
			
				if(from == 'KT') {
					empSelected = strEmp;
				}
						
				var typeas = document.getElementById("typeas").value;
				
				if(typeas == 'goal') {
					empSelected = strEmp+",";
				}
		
				//alert("empSelected==>"+empSelected+"==>typeas==>"+typeas);
				if(empSelected == '' || empSelected =='0') {
					alert("Please, select the employee.");
					document.getElementById("cmeasureEffortsHrs").value = '';
					
				} else {
						var xmlhttp;
					if (window.XMLHttpRequest) {
			            // code for IE7+, Firefox, Chrome, Opera, Safari
			            xmlhttp = new XMLHttpRequest();
			    	}
				    if (window.ActiveXObject) {
				        // code for IE6, IE5
				    	xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
				    }
				    if (xmlhttp == null) {
				            alert("Browser does not support HTTP Request");
				            return;
				    } else {
				    	var xhr = $.ajax({
			                url : 'GetEmpMaxWorkingHrs.action?empselected='+empSelected+'&hrs='+ hrs, 
			                		
			                cache : false,
			                success : function(data) {
			                	//alert("data==>"+data);
			                	if(data.trim() == '1') {
			                		document.getElementById("cmeasureEffortsHrs").value = '';
			                	}
			                }
			            });
					}
				}
		   }
	}  
		
	function removeGoal(divId) {
		var row_skill = document.getElementById(divId);
		var goalCnt = document.getElementById("goalCnt").value;
		if (row_skill && row_skill.parentNode && row_skill.parentNode.removeChild) {
			row_skill.parentNode.removeChild(row_skill);
			goalCnt--;
		}
		document.getElementById("goalCnt").value = goalCnt ;
	}
	
	function addNewGoal(isFrequency, isAttribute) {
		var strElement = '<%=(String)request.getAttribute("elementOptionsAjax")%>';
		var strOrientation = '<%=(String)request.getAttribute("orientation")%>';
		var strPerspective = '<%=(String)request.getAttribute("perspective")%>';
		var strFrequency = '<%=(String)request.getAttribute("frequencyOption")%>';
		var strDates = '<%=(String)request.getAttribute("datesOption")%>';
		//alert("strElement ===>> " + strElement);
		var goalCnt = document.getElementById("goalCnt").value;
		
		 var totweight=0;
			var weight = document.getElementById("cgoalWeightage").value;
				if(weight == undefined) {
					weight = 0;
				}
				totweight = totweight + parseFloat(weight);
				
			for(var i=1; i <= parseInt(goalCnt); i++) {
				var weight = document.getElementById("cgoalWeightage_"+i);
				if (weight == null) {
					continue;	
				}
				weight = document.getElementById("cgoalWeightage_"+i).value;
				if(weight == undefined) {
					weight = 0;
				}
				totweight = totweight + parseFloat(weight);
			}
				var remainweight = 100 - parseFloat(totweight);
			if(remainweight <= 0) {
				alert("Unable to add goal because of no weightage available");			
			} else {
				//alert("isFrequency==>"+isFrequency+"==>isAttribute==>"+isAttribute);
				goalCnt++;
				//alert("goalCnt ++ ===>> " + goalCnt);
				var divid = "goalDiv_"+goalCnt;
				var divtag = document.createElement('div');
				divtag.setAttribute("style", "padding:5px;margin-top: 10px;box-shadow: rgba(0, 0, 0, 0.180392) 0px 2px 18px 0px;");
				divtag.id = divid;
				//alert("divid ===>> " + divid);
				
				var data = "<table class=\"table table_no_border\" style=\"width: 100%;\"><tr><th nowrap align=\"right\" width=\"20%\">Objective:<sup>*</sup></th>"
							+"<td  colspan=\"3\"><input type=\"text\" name=\"corporateGoal_"+goalCnt+"\" id=\"corporateGoal_"+goalCnt+"\" class=\"validateRequired form-control\" style=\"width: 600px;\"/>"
							+"<a href=\"javascript:void(0)\" class=\"close-font pull-right\" onclick=\"removeGoal('" + divid + "');\">"
							+"</a>"
							+"</td></tr>"
							+"<tr><th nowrap align=\"right\">Goal:</th><td colspan=\"3\"><input type=\"text\" name=\"cgoalObjective_"+goalCnt+"\" id=\"cgoalObjective_"+goalCnt+"\"style=\"width: 600px;\" class=\" form-control \"/></td></tr>"
							+"<tr><th align=\"right\" valign=\"top\">Description:</th><td colspan=\"3\"><textarea rows=\"3\" cols=\"72\" name=\"cgoalDescription_"+goalCnt+"\" id=\"cgoalObjective_"+goalCnt+"\" class=\" form-control \"></textarea></td></tr>"
							+"<tr><th nowrap align=\"right\">Priority:<sup>*</sup></th><td colspan=\"3\"><select name=\"priority_"+goalCnt+"\" id=\"priority_"+goalCnt+"\" class=\"validateRequired form-control \">"
							+"<option value=''>Select</option><option value=\"1\">High</option><option value=\"2\">Medium</option><option value=\"3\">Low</option></select></td></tr>";
							
							if(isAttribute == 1) {	
								data = data +"<tr><th align=\"right\">Align an Attribute:<sup>*</sup></th><td colspan=\"3\"><span style=\"float: left; margin-right: 10px;\">"
								+"<select name=\"goalElements_"+goalCnt+"\" id=\"goalElements_"+goalCnt+"\" class=\"validateRequired\" onchange=\"getAttributes(this.value, '_"+goalCnt+"');\">"
								+"<option value=''>Select</option>"+ strElement +"</select></span>"
								+"<span id=\"attributeDiv_"+goalCnt+"\" style=\"float: left;\"><select name=\"cgoalAlignAttribute_"+goalCnt+"\" id=\"_"+goalCnt+"\" class=\"validateRequired\"><option value=''>Select</option></select></span>"
								+"</td></tr>";
							}
							
							data = data +"<tr id=\"ckraID0_"+goalCnt+"\" style=\"display: table-row;\"><th valign=\"top\" align=\"right\">Initiative:</th><td id=\"cKRAtdID0_"+goalCnt+"\" colspan=\"3\">"
							+"<input type=\"hidden\" name=\"cKRACount_"+goalCnt+"\" id=\"cKRACount0_"+goalCnt+"\" value=\"0\" /><input type=\"hidden\" name=\"cAddMKra_"+goalCnt+"\" id=\"cAddMKra0_"+goalCnt+"\" value=\"KRA\"/>"
							+"<div id=\"cKRAdiv0_0_"+goalCnt+"\" style=\"width: 100%; float: left; margin-bottom: 3px; border-bottom: 1px solid #F1F1F1;\">"
								+"<div style=\"width: 100%; float: left; margin-bottom: 3px;\">"
									+"<input type=\"hidden\" name=\"KRATaskCount_0_"+goalCnt+"\" id=\"KRATaskCount_0_"+goalCnt+"\" value=\"0\">"
									+"<input type=\"text\" name=\"cKRA_0_"+goalCnt+"\" id=\"cMainKRA_"+goalCnt+"\" class=\"validateRequired form-control \" />"
									+" Weightage (%): <input type=\"text\" style=\"width: 40px !important;\" name=\"cKRAWeightage_0_"+goalCnt+"\" id=\"cKRAWeightage_0_"+goalCnt+"\" value=\"100\" style=\"width: 40px !important;\" onkeyup=\"validateKRAScore(this.value,'cKRAWeightage_0_"+goalCnt+"', 'c', 0, '_"+goalCnt+"');\" onkeypress=\"return isNumberKey(event)\" class=\"validateRequired form-control \" />"
									+"<a href=\"javascript:void(0)\" class=\"\" onclick=\"addKRA('c',0, '_"+goalCnt+"');\"><i class=\"fa fa-plus-circle\"></i>Add Initiative</a>"
								+"</div>"
								+"<div style=\"width: 100%; float: left; margin-bottom: 3px;\">"
									+"<span style=\"float: left; margin-left: 7px;\"><a href=\"javascript:void(0)\" onclick=\"addKRATask('c', 0, 0, '_"+goalCnt+"');\"><i class=\"fa fa-plus-circle\"></i>Add Task</a></span>"
									//+"<span style=\"float: left; margin-left: 27px;\"><a href=\"javascript:void(0)\" class=\"\" style=\"color: #68AC3B;\" onclick=\"selectKRATask('c', 0, 0, '_"+goalCnt+"');\">Select Task</a></span>"
								+"</div>"
								+"<div id=\"cKRATaskdiv0_0_"+goalCnt+"\" style=\"float: left; margin-left: 50px; margin-bottom: 3px;\"></div>"
							+"</div></td></tr>"
							//updated by kalpana on 18/10/2016 start
							+"<tr><th align=\"right\">Effective Date:<sup>*</sup></th><td><input type=\"text\" name=\"cgoalEffectDate_"+goalCnt+"\" id=\"cgoalEffectDate_"+goalCnt+"\" class=\"duedatepick validateRequired \"  /></td>"
							+"<th class=\"alignRight\">Due Date:<sup>*</sup></th><td><input type=\"text\" name=\"cgoalDueDate_"+goalCnt+"\" id=\"cgoalDueDate_"+goalCnt+"\" class=\"duedatepick validateRequired \"  /></td></tr>"
							//..end..
							+"<tr><th align=\"right\">Orientation:</th><td><select name=\"corientation_"+goalCnt+"\">"+ strOrientation +"</select></td></tr>";
												
							if(isFrequency == 1) {
								data = data +"<tr><th  valign=\"top\">Select Frequency:</th><td colspan=\"5\">"
								+"<div style=\"position:reletive;\">"
								+"<span style=\"float: left; margin-right: 20px;\">"
								+"<select name=\"frequency_"+goalCnt+"\"  onchange=\"checkFrequency(this.value, '_"+goalCnt+"')\">"+ strFrequency +"</select>"
								+"</span>"
								+"<span id=\"weekly_"+goalCnt+"\" style=\"display: none; float: left;\">Day:<sup>*<sup><select name=\"weekday_"+goalCnt+"\" id=\"weekday_"+goalCnt+"\" class=\"form-control \"  style=\"width:100px;\"> <option value=\"\">Select Day</option> <option value=\"Monday\">Monday</option> <option value=\"Tuesday\">Tuesday</option> <option value=\"Wednesday\">Wednesday</option> <option value=\"Thursday\">Thursday</option> <option value=\"Friday\">Friday</option> <option value=\"Saturday\">Saturday</option> <option value=\"Sunday\">Sunday</option></select></span>"
								+"<span id=\"monthly_"+goalCnt+"\" style=\"display: none; float: left;\">Date of Month:<sup>*</sup><select name=\"day_"+goalCnt+"\" id=\"day_"+goalCnt+"\" style=\"width:65px;\" class=\" form-control \">"  
								+"<option value=\"\">Date</option>"+ strDates +"</select></span>"
								+"</div>"+
								"</td></tr>";
							}
							
							data = data +"<tr><th align=\"right\">Weightage (%):<sup>*</sup></th><td colspan=\"3\"><input type=\"text\" name=\"cgoalWeightage_"+goalCnt+"\" id=\"cgoalWeightage_"+goalCnt+"\" class=\"validateRequired\" value=\""+remainweight+"\" onkeyup=\"validateScore(this.value,'cgoalWeightage_"+goalCnt+"');\" onkeypress=\"return isNumberKey(event)\"/></td></tr>";
							
							/* data = data +"<tr><th align=\"right\"><span>Do you want to align Perspective:<sup>*</sup></span></th><td colspan=\"3\"><span>"
								+"<select class=\"validateRequired\" name=\"perspectiveYesno_"+goalCnt+"\" id=\"perspectiveYesno_"+goalCnt+"\" style=\"float: left;\" onclick=\"showPerspective(this.value);\">"
								+"<option value=\"\">Select</option>"
								+"<option value=\"Yes\">Yes</option>"
								+"<option value=\"No\">No</option>"
								+"</select></span>"
								+"<span id=\"perspectiveDiv\" style=\"display: none; float: left; padding-left: 10px;\">"
								+"<select name=\"strPerspective_"+goalCnt+"\" id=\"strPerspective_"+goalCnt+"\" class=\"form-control\"/>"
								+ strOrientation
								+"</select></span></td></tr>"; */
						data = data +"</table>";
				divtag.innerHTML = data;
				
				document.getElementById("newGoalDiv").appendChild(divtag);
				
				document.getElementById("goalCnt").value = goalCnt;
				
				//updated by kalpana on 18/10/2016..............start
				var strFromDate = document.getElementById("cgoalEffectDate_"+goalCnt+"").value;
				var strToDate = document.getElementById("cgoalDueDate_"+goalCnt+"").value;
				
				$("#cgoalEffectDate_"+goalCnt).datepicker({
				    format: 'dd/mm/yyyy',
				    autoclose: true
				}).on('changeDate', function (selected) {
				    var minDate = new Date(selected.date.valueOf());
				    $("#cgoalDueDate_"+goalCnt).datepicker('setStartDate', minDate);
				});

				$("#cgoalDueDate_"+goalCnt).datepicker({
					format: 'dd/mm/yyyy',
					autoclose: true
				}).on('changeDate', function (selected) {
				    var minDate = new Date(selected.date.valueOf());
				    $("#cgoalEffectDate_"+goalCnt).datepicker('setEndDate', minDate);
				});
				/* $("#cgoalEffectDate_"+goalCnt+"").datepicker({format : 'dd/mm/yyyy'});
	
    			$( "#cgoalDueDate_"+goalCnt+"").datepicker({format: 'dd/mm/yyyy'});  */
				
			}
	}

	$(function() {
		onloadFilterByOrg();
		$('#lt').DataTable({});
		$("#cgoalEffectDate").datepicker({
		    format: 'dd/mm/yyyy',
		    autoclose: true
		}).on('changeDate', function (selected) {
		    var minDate = new Date(selected.date.valueOf());
		    $('#cgoalDueDate').datepicker('setStartDate', minDate);
		});

		$("#cgoalDueDate").datepicker({
			format: 'dd/mm/yyyy',
			autoclose: true
		}).on('changeDate', function (selected) {
		    var minDate = new Date(selected.date.valueOf());
		    $('#cgoalEffectDate').datepicker('setEndDate', minDate);
		});

		$("input[type='submit']").click(function(){
			$("#frmEditGoal").find('.validateRequired').filter(':hidden').prop('required',false);
			$("#frmEditGoal").find('.validateRequired').filter(':visible').prop('required',true);
			
			
		});
		
	});
	
</script>
<%
	UtilityFunctions uF = new UtilityFunctions();
	String typeas = (String)request.getAttribute("typeas");
	String currUserType = (String)request.getAttribute("currUserType");
	String fromPage = (String)request.getAttribute("fromPage");
	Map<String, String> hmGoalType = (Map<String, String>) request.getAttribute("hmGoalType");
	String supervisorId = (String) request.getAttribute("supervisorId");
	String goaltype = request.getParameter("goaltype");
	
	
	List<FillEmployee> empList = (List<FillEmployee>) request.getAttribute("empList");
	
	CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
	String strUserType = (String) session.getAttribute(IConstants.USERTYPE);
	Map<String, String> hmCheckEmpList = (Map<String, String>) request.getAttribute("hmCheckEmpList");
	if(hmCheckEmpList==null) hmCheckEmpList=new HashMap<String, String>();
	String orgName = (String)request.getAttribute("orgName");
	Map<String, String> hmFeatureStatus = (Map<String, String>) request.getAttribute("hmFeatureStatus");
	Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>)request.getAttribute("hmFeatureUserTypeId");
	String strUsertypeId = (String)session.getAttribute(IConstants.BASEUSERTYPEID);
	String currDate = uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()),IConstants.DBDATE,IConstants.DATE_FORMAT);
	if(hmFeatureStatus == null)  hmFeatureStatus = new HashMap<String,String>();
	int isAttribute = 0;
	int isFrequency = 0;
	if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_GOAL_FREQUENCY)) && hmFeatureUserTypeId.get(IConstants.F_GOAL_FREQUENCY).contains(strUsertypeId)) {
		isFrequency = 1;
	}
	
	if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_GOAL_ATTRIBUTE_ALIGN)) && hmFeatureUserTypeId.get(IConstants.F_GOAL_ATTRIBUTE_ALIGN).contains(strUsertypeId)) {
		isAttribute = 1;
	}
	//EncryptionUtils EU = new EncryptionUtils();//Created by Dattatray Date:21-07-21 Note:Encryption
%>
<div class="leftbox reportWidth">
	<s:form id="frmEditGoal" name="frmEditGoal" theme="simple" action="MyPersonalGoal" method="POST" cssClass="formcss"> <!-- onselect="return checkFields();" -->
		<s:hidden name="operation"></s:hidden>
		<input type="hidden" id="currDate" name="currDate" value="<%=currDate%>"/>

		<input type="hidden" name="goaltype" id="goaltype" value="<s:property value="goaltype"/>"/> 
		<input type="hidden" name="goal_parent_id" value="<s:property value="goalid"/>"/> 
		<input type="hidden" name="supervisorId" id="supervisorId" value="<%=supervisorId%>"/>
		<input type="hidden" name="typeas" id="typeas" value="<%=typeas%>"/>
		<input type="hidden" id="fromPage" name="fromPage" value="<%=fromPage%>"/>
		<input type="hidden" id="currUserType" name="currUserType" value="<%=currUserType%>"/>
		
		<s:hidden name="type"></s:hidden>
		<table class="table table_no_border" style="width: 100%">
			<%if(typeas != null && !typeas.equals("goal")) { %>
			<tr>
				<th nowrap class="alignRight">Organisation:</th>
				<td colspan="3">
				<input type="hidden" name="hideOrgid" id="hideOrgid" value="<%=request.getAttribute("strID") %>">
					<%=orgName != null ? orgName : "" %>
					
				</td>
			</tr>
			<% } %>
			<%if(typeas !=null && !typeas.equals("goal")) { %>
			<tr>
				<td colspan="5">
					<% if(strUserType != null && (strUserType.equals(IConstants.HRMANAGER) || strUserType.equals(IConstants.ADMIN))) { %>
						<div class="row row_without_margin">
							<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
								<i class="fa fa-filter" aria-hidden="true"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Location</p>
									<s:select name="strWlocation" list="workList" id="wlocation" listKey="wLocationId" listValue="wLocationName" headerKey=""
	                                   headerValue="All WorkLocation" cssClass="form-control " required="true" value="{userlocation}" onchange="getEmployeebyLocation();"></s:select>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Department</p>
									<s:select name="strDepart" list="departmentList" id="depart" listKey="deptId" listValue="deptName" headerKey=""
	                                   headerValue="All Department" cssClass="form-control " required="true" onchange="getEmployeebyDepart();"></s:select>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Level</p>
									<s:select name="strLevel" list="levelList" listKey="levelId" id="strLevel" listValue="levelCodeName" headerKey=""
	                                   headerValue="All Level" cssClass="form-control " required="true" onchange="getEmployeebyLevel()"></s:select>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Designation</p>
									<s:select name="strDesignationUpdate" list="desigList" listKey="desigId" id="desigIdV" listValue="desigCodeName"
	                                       headerKey="" headerValue="All Designation" cssClass="form-control " onchange="getEmployeebyDesig();"></s:select>
								</div>
							</div>
						</div><br>
						<div class="paddingtop20 clr">
					<% } %>
					<div class="row row_without_margin">
						<div class="col-lg-7 col_no_padding" id="myEmployee">
							<table id="lt" class="table table-bordered">
								<%
									if (empList != null && !empList.equals("") && !empList.isEmpty()) {
											Map<String, String> hmEmpLocation = (Map<String, String>)request.getAttribute("hmEmpLocation");
											Map<String, String> hmWLocation = (Map<String, String>)request.getAttribute("hmWLocation");
											Map<String, String> hmEmpCodeDesig = (Map<String, String>)request.getAttribute("hmEmpCodeDesig");
								%>
										<thead>
											<tr>
												<th width="10%" align="center"><input onclick="checkUncheckValue('frmKRA');" type="checkbox" name="allEmp" id="allEmp"></th>
												<th align="center">Employee</th>
												<th align="center">Designation</th>
												<th align="center">Location</th>
											<!-- <th align="center">Factsheet</th> -->
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
								%>
										<tr>
											 <td align=""><input type="checkbox" name="strGoalEmpId" id="strGoalEmpId<%=empID%>" onclick="getGoalSelectedEmp(this.checked, this.value, 'frmKRA', '');"
												value="<%=empID%>" <%if (hmCheckEmpList.get(empID) != null) {%> checked="checked" <%}%>>
											</td>
											<!-- Created by Dattatray Date:21-07-21 Note:empId encrypt -->
											<td><a href="javascript: void(0);" onclick="openPanelEmpProfilePopup('<%=empID %>')"><%=empName%></a></td>
											<td><%=desig%></td>
											<td><%=location%></td>
										</tr>
								<% }
									} else {
								%>
										<tr>
											<td colspan="3"><div class="nodata msg" style="width: 88%"><span>No Employee Found</span></div></td>
										</tr>
								<% } %>
								</tbody>
							</table>
						</div>
						<div class="col-lg-5 col_no_padding">
							<div id="idEmployeeInfo" style="padding: 5px;overflow-y: auto;border: 2px solid #F0F0F0;">
								<%
									List<String> selectEmpList = (List<String>) request.getAttribute("selectEmpList");
									if (selectEmpList != null) {
								%>
										<div style="border: 2px solid #ccc;">
											<div style="padding: 8px 20px; border: 2px solid lightgray;"><b>Employee</b></div>
											
											<table border="0" class="table" width="100%">
												<%
													for (int i = 0; i < selectEmpList.size(); i++) {
												%>
												<tr>
													<td nowrap="nowrap" style="font-weight: bold;"><%=i + 1%></td>
													<td align="left">
														<%=selectEmpList.get(i)%>									
													</td>
												</tr>
												<% } %>
											</table>
										</div>
								<%
									} else {
								%>
										<div class="nodata msg" style="width: 94%">
											<span>No Employee selected</span>
										</div>
								<% } %>
								<input type="hidden" name="empselected" id="empselected" value="0"/>
							</div>
						</div>
					</div>
					
					</div>
				<!-- </td>
				<td> -->
				</td>
			</tr>
			<%} %>
			<tr>
				<%
					//System.out.println("typeas==>"+typeas);
					String header = "";
					if(typeas!= null && typeas.equals("target")){
						header = "Target";
					}else if(typeas != null && typeas.equals("KRA")){
						header = "KRA";
					}else{
						header = "Objective";
					} 
				%>
				<th nowrap align="right" width="20%">Objective:<sup>*</sup></th>
				<td  colspan="3">
					<%if(typeas != null && typeas.equals("goal")){ %>
						<input type="hidden" name="hideOrgid" id="hideOrgid" value="<%=request.getAttribute("strID") %>">
					<% } %>
					<input type="text" name="corporateGoal" id="corporateGoal" class="validateRequired form-control " style="width: 600px;" required/>
				</td>
			</tr>
			<tr>
				<th nowrap align="right">Goal:</th>
				<td  colspan="3"><input type="text" name="cgoalObjective" id="cgoalObjective" style="width: 600px;" class=" form-control " />
				</td>
			</tr>
			<tr>
				<th align="right" valign="top">Description:</th>
				<td  colspan="3"><textarea rows="3" cols="72" name="cgoalDescription" id="cgoalDescription" class=" form-control "></textarea>
				</td>
			</tr>
			
			<tr>
				<th nowrap align="right">Priority:<sup>*</sup></th>  
				<td  colspan="3"><s:select theme="simple" name="priority" id="priority" headerKey="" cssClass="validateRequired  form-control " 
						headerValue="Select" list="#{'1':'High', '2':'Medium', '3':'Low'}"/> 
				</td>
			</tr>
			
			<% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_GOAL_ATTRIBUTE_ALIGN)) && hmFeatureUserTypeId.get(IConstants.F_GOAL_ATTRIBUTE_ALIGN).contains(strUsertypeId)) { %>
			<tr>
				<th align="right">Align an Attribute:<sup>*</sup></th>
				
				<td colspan="3">
				<span style="float: left; margin-right: 10px;"> 
					<select name="goalElements" id="goalElements" class="validateRequired" onchange="getAttributes(this.value, '');" required>
					<option value="">Select</option>
					<%=request.getAttribute("elementOptions") %>
					</select>
				</span>
					
				<span id="attributeDiv" style="float: left;">  
					<s:select theme="simple" name="cgoalAlignAttribute" id="cgoalAlignAttribute" cssClass="validateRequired  " 
					list="attributeList" listKey="id" listValue="name" headerKey="" headerValue="Select Attribute"/>
				</span>
				</td>
							
			</tr>
			<% } %>
			
		<%if(typeas != null && typeas.equals("KRA")){ %>
			<tr id="ckraID0" style="display: table-row;">
				<th valign="top" align="right">Initiative:</th>
				<td id="cKRAtdID0_0"  colspan="3">
					<input type="hidden" name="cKRACount" id="cKRACount0_0" value="0" />
					<input type="hidden" name="cAddMKra" id="cAddMKra0" value="KRA" />
					<div id="cKRAdiv0_0_0" style="width: 100%; float: left; margin-bottom: 3px; border-bottom: 1px solid #F1F1F1;">
						<div style="width: 100%; float: left; margin-bottom: 3px;">
							<input type="hidden" name="KRATaskCount_0_0" id="KRATaskCount_0_0" value="0">
							<input type="text" name="cKRA_0_0" id="cMainKRA" class="validateRequired form-control " />
							 Weightage (%): <input type="text" class="validateRequired form-control "  style="width: 40px !important;" name="cKRAWeightage_0_0" id="cKRAWeightage_0_0" value="100" onkeyup="validateKRAScore(this.value,'cKRAWeightage_0_0', 'c', 0, '_0');" onkeypress="return isNumberKey(event)" />
							<a href="javascript:void(0)" class="" onclick="addKRA('c',0, '_0');"><i class="fa fa-plus-circle"></i>Add Initiative</a>
						</div>
						<div style="width: 100%; float: left; margin-bottom: 3px;">
							<span style="float: left; margin-left: 7px;"><a href="javascript:void(0)"  onclick="addKRATask('c', 0, 0, '_0');"><i class="fa fa-plus-circle"></i>Add Task</a></span>
						</div>
						<div id="cKRATaskdiv0_0_0" style="float: left; margin-left: 50px; margin-bottom: 3px;"></div>
					</div>
				</td>
			</tr>
			<%} %>
			<%if(typeas != null && typeas.equals("goal")) { %>
			<tr>
				<th nowrap align="right">Does it have a Measure:<sup>*</sup></th>
				<td colspan="3"><s:select theme="simple" name="cmeasureKra" id="cmeasureKra"  headerKey=""
						headerValue="Select" list="#{'Yes':'Yes', 'No':'No'}"
						onchange="showMeasureWith(this.value);" cssClass="validateRequired  form-control "/>  
				</td>
			</tr>
			
			<tr id="measureWith" style="display: none;">
				<th align="right">Measure with:</th>
				<td  colspan="3">
					<input type="hidden" name="cAddMKra" id="cAddMKra0" />
					<s:select theme="simple" name="cmeasurewith" headerKey="Amount" headerValue="Amount" list="#{'Effort':'Effort','Percentage':'Percentage'}"
					onchange="getMeasureWith(this.value,'c',0);" cssClass="validateRequired  form-control "/>
				</td>
			</tr>
			
			<tr id="cdollarAmtid0" style="display: none;"> 
				<th align="right"><span id="measureSpanId"></span></th>
				<td colspan="3">
					<span id="rsSpan0" style="display: block; float: left;">&nbsp;</span>
					<span style="float: left;"><input type="text" name="cmeasureDollar" id="cmeasureDollar" class="validateRequired form-control "/></span>
					<span id="percentSpan0" style="display: none; float: left;">%</span>
				</td>
			</tr>
			<%} %>
			
			<%if(typeas != null && typeas.equals("target")) { %>
					
				<tr id="measureWith" style="display: table-row;">
					<th align="right">Measure with:</th>
					<td  colspan="3">
						<input type="hidden" name="cAddMKra" id="cAddMKra0" value="Measure"/>
						<input type="hidden" name="cmeasureKra" id="cmeasureKra0" value="Yes"/>
						<s:select theme="simple" name="cmeasurewith" headerKey="Amount" headerValue="Amount" list="#{'Effort':'Effort','Percentage':'Percentage'}"
							onchange="getMeasureWith(this.value,'c',0);" cssClass="validateRequired  form-control "/>
					</td>
				</tr>
			
				<tr id="cdollarAmtid0" style="display: table-row;"> 
					<th align="right"><span id="measureSpanId"></span></th>
					<td colspan="3">
					<span id="rsSpan0" style="display: block; float: left;">&nbsp;</span>
					<span style="float: left;">
					<input type="text" class="validateRequired form-control " name="cmeasureDollar" id="cmeasureDollar" style="width: 64px;" onkeypress="return isNumberKey(event)"/></span>
					<span id="percentSpan0" style="display: none; float: left;">%</span>
					</td>
				</tr>
			<%} %>
			<tr id="cmeasureEffortsid0" style="display: none;">
				<th align="right">&nbsp;</th>
				<td  colspan="3">
				<!-- <input type="text" name="cMeasureDesc" id="cMeasureDesc"/>&nbsp;&nbsp;&nbsp; -->
					Days&nbsp;<input type="text" name="cmeasureEffortsDays" id="cmeasureEffortsDays" style="width: 40px !important;" onkeyup="checkHrsLimit();" onkeypress="return isOnlyNumberKey(event)" class="validateRequired form-control "/>&nbsp;
					Hrs&nbsp;<input type="text" name="cmeasureEffortsHrs" style="width: 40px !important;" id="cmeasureEffortsHrs" 
					onkeyup="checkHrsLimit();" class="validateRequired form-control "  onkeypress="return isOnlyNumberKey(event)"/>
				</td> 
			</tr>
			
			<tr>
				<th align="right">Effective Date:<sup>*</sup></th> 
				<td width="250px"><input type="text" name="cgoalEffectDate" id="cgoalEffectDate" class="duedatepick validateRequired  form-control " required/></td>
				<th align="right" width="100px" class="alignRight">Due Date:<sup>*</sup></th>
				<td><input type="text" name="cgoalDueDate" id="cgoalDueDate" class="duedatepick validateRequired  form-control "  required/></td>
			</tr>
			
			<tr id="cOrientation0">
				<th align="right">Orientation:</th>
				<td  colspan="3"><select name="corientation" id="corientation" class="validateRequired  form-control ">
					<%=request.getAttribute("orientation")%>
				</select></td>
			</tr>
			<% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_GOAL_FREQUENCY)) && hmFeatureUserTypeId.get(IConstants.F_GOAL_FREQUENCY).contains(strUsertypeId)) { %>
				<tr>
					  <th valign="top">Select Frequency:</th> <!-- Select Frequency for Goal -->
					  <td colspan="5">
						  <div style="position:reletive;">
							  <span style="float: left; margin-right: 20px"><s:select theme="simple" name="frequency" id="frequency" list="frequencyList"
									listKey="id" listValue="name" onchange="checkFrequency(this.value, '')" />                            
							  </span>
						   
							   <span id="weekly" style="display: none; float: left;"><%-- Day:<sup>*</sup> --%>
							           <s:select theme="simple" name="weekday" id="weekday" cssStyle="width:100px;" cssClass="validateRequired " headerKey="" headerValue="Select Day" 
							               list="#{'Monday':'Monday','Tuesday':'Tuesday', 'Wednesday':'Wednesday','Thursday':'Thursday','Friday':'Friday','Saturday':'Saturday','Sunday':'Sunday'}"  />
							   </span>  
						   
							   <span id="monthly" style="display: none; float: left;"><%-- Date of Month: <sup>*</sup> --%>
							       <s:select theme="simple" cssClass="validateRequired form-control " name="day" id="day" cssStyle="width:110px;" headerKey="" headerValue="Select Date"
						               list="#{'1':'1','2':'2', '3':'3','4':'4','5':'5','6':'6','7':'7','8':'8','9':'9', '10':'10',
						               '11':'11','12':'12', '13':'13','14':'14','15':'15','16':'16','17':'17','18':'18','19':'19', '20':'20',
						               '21':'21','22':'22', '23':'23','24':'24','25':'25','26':'26','27':'27','28':'28','29':'29', '30':'30', '31':'31'}" />
							   </span>
						     </div>
					    </td>
					</tr>
			<% } %>
			 <tr>
				<th align="right">Weightage (%):<sup>*</sup></th> 
				<td colspan="3"><input type="number" name="cgoalWeightage" id="cgoalWeightage" class="validateRequired form-control "
					 value="100" onkeyup="validateScore(this.value, 'cgoalWeightage');" onkeypress="return isNumberKey(event)" required/></td>
			</tr>
			<%if(typeas !=null && typeas.equals("goal")){ %>
				<tr>
					<th align="right"><span>Do you want to align this goal with team goal:<sup>*</sup></span></th>
					<td colspan="3">
						<span>
							<s:select theme="simple" cssClass="validateRequired  form-control " name="goalalignYesno" headerKey="" headerValue="Select" id="goalalignYesno" cssStyle="float: left;"
		                     list="#{'Yes':'Yes','No':'No'}" onclick="showTeamGoals(this.value);"/>
		                </span>
	                     <span id="teamgoalDiv" style="display: none; float: left; padding-left: 10px;">
		                     <select name="teamGoalList" id="teamGoalList" class=" form-control ">
		                    	 <option value="">Select</option>
		                    	 <%=request.getAttribute("optionTeamGoals") %>
		                     </select>
	                     </span>
					</td>
				</tr>
			<%} %>
			<%if(typeas !=null && typeas.equals("KRA")){ %>
				<%-- <tr>
					<th align="right"><span>Do you want to align Perspective:<sup>*</sup></span></th>
					<td colspan="3">
						<span>
							<s:select theme="simple" cssClass="validateRequired" name="perspectiveYesno" headerKey="" headerValue="Select" id="perspectiveYesno" cssStyle="float: left;"
								list="#{'Yes':'Yes','No':'No'}" onclick="showPerspective(this.value);"/>
		                </span>
	                    <span id="perspectiveDiv" style="display: none; float: left; padding-left: 10px;">
		                    <s:select name="strPerspective" list="perspectiveList" id="strPerspective" listKey="perspectiveId" listValue="perspectiveName" headerKey=""
								headerValue="All Perspective" cssClass="form-control" required="true" />
	                     </span>
					</td>
				</tr> --%>
			<%} %>
		</table>
		
		<div id="newGoalDiv" class="clr" style="padding: 5px;">
			<input type="hidden" name="goalCnt" id="goalCnt" value="0">
		</div>
		<%if(typeas != null && typeas.equals("KRA")) { %>
			<div>
			<a href="javascript:void(0)" onclick="addNewGoal('<%=isFrequency%>','<%=isAttribute%>')"><i class="fa fa-plus-circle" aria-hidden="true"></i>Add New Objective</a> </div>
		<% } %>
		<div style="text-align: center; ">
		 	<s:submit value="Save" cssClass="btn btn-primary" name="submit"></s:submit> 
		</div>
	</s:form>
</div>
<script>

<%if(fromPage != null) { %>
<%-- ,
success: function(result){
	if(from != "" && from == "GKT") {
		getGoalKRAEmpList('GoalKRAEmpList','L','','<%=(String)request.getAttribute("strID")%>','','','','');	
	} else if(from != "" && from == "KT") {
		$("#divMyHRData").html(result);
	}
} --%>
  $("#frmEditGoal").submit(function(e) {
		e.preventDefault();
		var from = document.getElementById("fromPage").value;
		if(checkFields()) { 
		var form_data = $(this).serialize();
	 $("#divMyHRData").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({ 
			type : 'POST',
			url: "MyPersonalGoal_1.action",
			data: form_data+"&submit=Save",
			success: function(result){
				if(from != "" && from == "GKT") {
					<%-- getGoalKRAEmpList('GoalKRAEmpList','L','','<%=(String)request.getAttribute("strID")%>','','','',''); --%>
					getGoalKRATargetDashboardData('GoalKRATargetDashboardData_1','L','<%=currUserType %>');
				} else if(from != "" && from == "KT") {
					$("#divMyHRData").html(result);
				}
	   		},
			error: function(result){
				$.ajax({ 
					url: 'KRATarget.action',
					cache: true,
					success: function(result){
						if(from != "" && from == "GKT") {
							<%-- getGoalKRAEmpList('GoalKRAEmpList','L','','<%=(String)request.getAttribute("strID")%>','','','',''); --%>
							getGoalKRATargetDashboardData('GoalKRATargetDashboardData_1','L','<%=currUserType %>');
						} else if(from != "" && from == "KT") {
							$("#divMyHRData").html(result);
						}
			   		}
				});
			}
		});
		} 
    });
<%}%>

</script>

