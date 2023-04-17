<%@page import="java.util.Iterator"%>
<%@page import="java.util.HashSet"%>
<%@page import="java.util.Set"%>
<%@page import="com.konnect.jpms.select.FillEmployee"%>
<%@page import="java.util.HashMap"%>

<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%> 
<%@page import="com.konnect.jpms.util.*"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<script type="text/javascript" src="scripts/customAjax.js"></script>

<% 	String goaltype = (String)request.getAttribute("goaltype");
	String compGoalId = (String)request.getAttribute("compGoalId");
%>

<script type="text/javascript">

	/* function searchTextField(){
		//alert("searchTextField");
		$('#lt1').dataTable({
			bJQueryUI : true,
			"sPaginationType" : "full_numbers",
			"aaSorting" : []
		});
	} */

	var cxtpath='<%=request.getContextPath()%>';
	//var taskcount = 0;
	
	function addKRA(ch, count) {
		var goaltype = '<%=goaltype %>';
		var KRACount = document.getElementById(ch + "KRACount" + count).value;
		var KRACountID = ch + "KRACount" + count;
		var totweight=0;
		for(var i=0; i<=parseInt(KRACount); i++) {
			var weight = document.getElementById("cKRAWeightage_"+i);
			if (weight == null) {
				continue;	
			}
			weight = document.getElementById("cKRAWeightage_"+i).value;
			if(weight == undefined) {
				weight = 0;
			}
			totweight = totweight + parseFloat(weight);
		}
		var remainweight = 100 - parseFloat(totweight);
		if(remainweight <= 0) {
			alert("Unable to add KRA because of no weightage available");			
		} else {
			KRACount++;
			var divid = ch+"KRAdiv"+count+"_"+KRACount;
			var divtag = document.createElement('div');
			divtag.setAttribute("style", "width: 100%; float: left; margin-bottom: 3px;"); //border-bottom: 1px solid #CCCCCC;
			divtag.id = divid;
			var dataKRATask = '';
			var data = "<div style=\"width: 100%; float: left; margin-bottom: 3px;\"><input type=\"hidden\" name=\"KRATaskCount_"+KRACount+"\" id=\"KRATaskCount_"+KRACount+"\" value=\"0\">"
				+"<input type=\"text\" name=\""+ch+"KRA_"+KRACount+"\" id=\""+ch+"KRA_"+KRACount+"\" class=\"validateRequired \"/> "
				+" Weightage (%): <input type=\"text\" class=\"validateRequired \" style=\"width: 40px !important;\" name=\"cKRAWeightage_"+KRACount+"\" id=\"cKRAWeightage_"+KRACount+"\" value=\""+remainweight+"\" onkeyup=\"validateKRAScore(this.value,'cKRAWeightage_"+KRACount+"', '"+ch+"', '"+count+"');\" onkeypress=\"return isNumberKey(event)\" />"
				+"<a href=\"javascript:void(0)\" onclick=\"addKRA('"+ ch + "',"+ count+ "); title=\"Add KRA\"\"><i class=\"fa fa-plus-circle\"></i></a>&nbsp;"
				+"<a href=\"javascript:void(0)\" onclick=\"removeKRAID('" + divid + "','" + KRACountID + "'); title=\"Remove KRA\"\" class=\"close-font\">"
				+"Remove KRA</a>"
				+"</div>";
				if(goaltype != null && goaltype == '4') {
					dataKRATask = "<div style=\"width: 100%; float: left; margin-bottom: 3px;\"><span style=\"float: left; margin-left: 7px;\">"
					+"<a href=\"javascript:void(0)\" onclick=\"addKRATask('"+ ch + "',"+ count+ ", "+KRACount+");\"><i class=\"fa fa-plus-circle\"></i>Add Task</a></span>"
					/* +"<span style=\"float: left; margin-left: 27px;\"><a href=\"javascript:void(0)\" class=\"add_lvl\" style=\"color: #68AC3B;\" "
					+"onclick=\"selectKRATask('"+ ch + "', "+ count+ ", "+KRACount+");\">Select Task</a></span>" */
					+"</div><div id=\""+ch+"KRATaskdiv"+ count+"_"+KRACount+"\" style=\"float: left; margin-left: 50px; margin-bottom: 3px;\"></div>";
				}
			divtag.innerHTML = data+dataKRATask;
			document.getElementById(ch + "KRAtdID" + count).appendChild(divtag);
			
			document.getElementById(ch + "KRACount" + count).value = KRACount;
		}
	}
	
	
	function validateKRAScore(value, weightageId, ch, count) {
		
		var KRACount = document.getElementById(ch + "KRACount" + count).value;
		var totweight=0;
		for(var i=0; i<=parseInt(KRACount); i++) {
			var checkCurrId = "cKRAWeightage_"+i;
			var weight = document.getElementById("cKRAWeightage_"+i);
			if (weight == null) {
				continue;	
			}
			if(weightageId == checkCurrId) {
				//	alert("same id");
			} else {
				weight = document.getElementById("cKRAWeightage_"+i).value;
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
		var kraCount = document.getElementById("cKRACount0").value;
		
		var row_skill = document.getElementById(id);
		if (row_skill && row_skill.parentNode && row_skill.parentNode.removeChild) {
			row_skill.parentNode.removeChild(row_skill);
			if(parseInt(kraCount) > 0) {
				kraCount--;
			}
		}
		
	   document.getElementById("cKRACount0").value = kraCount;
	}
	
	function addKRATask(ch, count, kraCnt) {
		var taskcount = document.getElementById("KRATaskCount_"+kraCnt).value;
		taskcount++;
		var divid = ch+"KRATaskDIV"+count+"_"+kraCnt+"_"+taskcount;
		var divtag = document.createElement('div');
		divtag.setAttribute("style", "width: 100%; float: left; margin-bottom: 3px;");
		divtag.id = divid;
		var data = "<input type=\"text\" name=\""+ch+"KRATask_"+kraCnt+"\" id=\""+ch+"KRATask_"+kraCnt+"\"  class=\"validateRequired \"/> "
			+"<a href=\"javascript:void(0)\" onclick=\"addKRATask('"+ch+"', "+count+", "+kraCnt+");\"><i class=\"fa fa-plus-circle\"></i>Add Task</a>&nbsp;"
			+"<a href=\"javascript:void(0)\" onclick=\"removeKRATaskID('" + divid + "','" +kraCnt+ "');\" class=\"close-font\">"
			+"Remove Task</a>";
		divtag.innerHTML = data;
		document.getElementById(ch + "KRATaskdiv"+count + "_" + kraCnt).appendChild(divtag);
		
		document.getElementById("KRATaskCount_"+kraCnt).value = taskcount;
	}
	
	function removeKRATaskID(id,kraCnt) {
		var taskCount = document.getElementById("KRATaskCount_"+kraCnt).value;
		
		var row_skill = document.getElementById(id);
		if (row_skill && row_skill.parentNode && row_skill.parentNode.removeChild) {
			row_skill.parentNode.removeChild(row_skill);
			if(parseInt(taskCount) > 0) {
				taskCount--;
			}
		}
		
		document.getElementById("KRATaskCount_"+kraCnt).value = taskCount;
	}
	
	
	function getMeasureWith(value, ch, count) {
		
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
			document.getElementById("measureWith").style.display = "table-row";
			document.getElementById("cdollarAmtid0").style.display = "table-row";
						
		} else {
			
			document.getElementById("measureWith").style.display = "none";
			document.getElementById("cdollarAmtid0").style.display = "none";
			document.getElementById("cmeasureEffortsid0").style.display = "none";
			
		}
	}
	
	function getMeasureKRA(value, ch, count) {
		//cMKRAID0 Yes No 
		if (value == 'Yes') {
			document.getElementById(ch + "MKRAID" + count).style.display = "table-row";
			
		} else {
			document.getElementById(ch + "MKRAID" + count).style.display = "none";
			document.getElementById(ch + "kraID" + count).style.display = "none";
			
			document.getElementById(ch + "measureID" + count).style.display = "none";
			
		}
	}

	
	function addMeasureKRA(value, ch, count) {

		//KRA Measure ckraID0 cmeasureID0  dollarAmtid measureEffortsid
		if (value == 'KRA') {
			document.getElementById(ch + "kraID" + count).style.display = "table-row";
			document.getElementById(ch + "measureID" + count).style.display = "none";
			document.getElementById(ch + "dollarAmtid" + count).style.display = "none";
			document.getElementById(ch + "measureEffortsid" + count).style.display = "none";
						
		} else if (value == 'Measure') {
			document.getElementById(ch + "measureID" + count).style.display = "table-row";
			document.getElementById(ch + "kraID" + count).style.display = "none";
			document.getElementById(ch + "dollarAmtid" + count).style.display = "table-row";
			document.getElementById(ch + "measureEffortsid" + count).style.display = "none";
		} else {
			document.getElementById(ch + "kraID" + count).style.display = "none";
			document.getElementById(ch + "measureID" + count).style.display = "none";
			document.getElementById(ch + "dollarAmtid" + count).style.display = "none";
			document.getElementById(ch + "measureEffortsid" + count).style.display = "none";
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
		var cnt=0;
		document.getElementById("wlocation").selectedIndex = 0;
		document.getElementById("depart").selectedIndex = 0;
		document.getElementById("strLevel").selectedIndex = 0;
		document.getElementById("desigIdV").selectedIndex = 0;
		//document.getElementById("gradeIdV").selectedIndex = 0;
		//document.getElementById("employee").selectedIndex = 0;

		var supervisorId = document.getElementById("supervisorId").value;
		var goaltype = document.getElementById("goaltype").value;
		if (goaltype == '3' || goaltype == '4') {
			// || goaltype=='4'
			action += '&supervisor=' + supervisorId;
		}
		$("#myEmployee").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
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
	    
	    var selectedEmp = document.getElementById("empselected").value;
	    
		var action = 'getGoalEmployeeList.action?strOrg='+ strID +'&location='+ location + '&selectedEmp=' + selectedEmp;

		document.getElementById("depart").selectedIndex = 0;
		document.getElementById("strLevel").selectedIndex = 0;
		document.getElementById("desigIdV").selectedIndex = 0;
		//document.getElementById("gradeIdV").selectedIndex = 0;
		//document.getElementById("employee").selectedIndex = 0;
		
		var supervisorId = document.getElementById("supervisorId").value;
		var goaltype = document.getElementById("goaltype").value;
		if (goaltype == '3' || goaltype == '4') {
			// || goaltype=='4'
			action += '&supervisor=' + supervisorId;
		}
		$("#myEmployee").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		getContent('myEmployee', action);
		//getWLocDepartLevelDesigByOrg(location,'wloc');
	//	searchTextField();
	}

	function getEmployeebyDepart() {
		var depart = getSelectedValue("depart");
		var location = null;
		if(document.getElementById("wlocation")){
			location = getSelectedValue("wlocation"); 
	    }
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
		//document.getElementById("gradeIdV").selectedIndex = 0;
		//document.getElementById("employee").selectedIndex = 0;
		
		var action = 'getGoalEmployeeList.action?depart=' + depart + '&selectedEmp=' + selectedEmp;
		
		var supervisorId = document.getElementById("supervisorId").value;
		var goaltype = document.getElementById("goaltype").value;
		if (goaltype == '3' || goaltype == '4') {
			// || goaltype=='4'
			action += '&supervisor=' + supervisorId;
		}
		$("#myEmployee").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		if (strID == '' && location == '') {
		} else {
			if (strID != '') {
				action += '&strOrg='+ strID;
			}
			if (location != '') {
				action += '&location=' + location;
			}
			//alert("depart action ===>> " + action);
			getContent('myEmployee', action);
		}
		//searchTextField();
	}

	function getEmployeebyLevel() {
		var location = null;
		if(document.getElementById("wlocation")){
			location = getSelectedValue("wlocation"); 
	    }
		var depart = null;
		if(document.getElementById("depart")){
			depart = getSelectedValue("depart"); 
	    }
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
		//document.getElementById("gradeIdV").selectedIndex = 0;
		//document.getElementById("employee").selectedIndex = 0;

		var action = 'getGoalEmployeeList.action?level=' + level + '&selectedEmp=' + selectedEmp;
		
		var supervisorId = document.getElementById("supervisorId").value;
		var goaltype = document.getElementById("goaltype").value;
		if (goaltype == '3' || goaltype == '4') {
			// || goaltype=='4'
			action += '&supervisor=' + supervisorId;
		}
		$("#myEmployee").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
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
		//alert("level action ===>> " + action);
		getContent('myEmployee', action);
		//getWLocDepartLevelDesigByOrg(level,'level');
		window.setTimeout(function() {
			getContent('myDesig', 'GetDesignationByLevel.action?strLevel=' + level+'&strOrg='+ strID); 
		}, 200); 
		
	//	searchTextField();


	}

	function getEmployeebyDesig() {
		var location = null;
		if(document.getElementById("wlocation")){
			location = getSelectedValue("wlocation"); 
	    }
		var depart = null;
		if(document.getElementById("depart")){
			depart = getSelectedValue("depart"); 
	    }
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
	    
		//document.getElementById("gradeIdV").selectedIndex = 0;
		//document.getElementById("employee").selectedIndex = 0;
		
		var action = 'getGoalEmployeeList.action?design=' + design + '&selectedEmp=' + selectedEmp;
		
		var supervisorId = document.getElementById("supervisorId").value;
		var goaltype = document.getElementById("goaltype").value;
		if (goaltype == '3' || goaltype == '4') {
			// || goaltype=='4'
			action += '&supervisor=' + supervisorId;
		}
		$("#myEmployee").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
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
			//alert("desig action ===>> " + action);
			getContent('myEmployee', action);
			/* window.setTimeout(function() {
				getContent('myGrade', 'getGrade.action?strDesignation=' + design);
			}, 200);  */
		}
		//searchTextField();
	}
	
	
	function getAttributes(value) {
		var strID = null;
		if(document.getElementById("strOrg")){
			strID = getSelectedValue("strOrg"); 
	    }
		
	    if(document.getElementById("hideOrgid")){
	    	strID = document.getElementById("hideOrgid").value;
	    }
		//alert("strID ===>> " +strID);
		var action = 'GetAttributeList.action?elementID=' + value + '&orgId=' + strID;
		getContent('attributeDiv', action);
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

	function checkFrequency(value, id) {
		//monthsTR quartersTR halfYearsTR periodTR
		if (value == '1') {
			document.getElementById("periodTR"+id).style.display = "table-row";
			document.getElementById("quartersHalfYearsTR"+id).style.display = "none";
			/* document.getElementById("quartersTR"+id).style.display = "none"; */
			document.getElementById("monthsTR"+id).style.display = "none";
			document.getElementById("yearsTR"+id).style.display = "none";
			//document.getElementById("strMonths"+id).selectedIndex = 0;
			$("#strMonths"+id).removeAttr('selected').prop('selected', false);
			/* document.getElementById("strQuarters"+id).selectedIndex = 0;
			document.getElementById("strHalfYears"+id).selectedIndex = 0; */
			document.getElementById("cgoalEffectDate"+id).value = '';
			document.getElementById("cgoalDueDate"+id).value = '';
						
		} else if (value == '3') {
			document.getElementById("periodTR"+id).style.display = "none";
			/* document.getElementById("halfYearsTR"+id).style.display = "none"; */
			document.getElementById("quartersHalfYearsTR"+id).style.display = "none";
			document.getElementById("monthsTR"+id).style.display = "table-row";
			document.getElementById("yearsTR"+id).style.display = "table-row";
			//document.getElementById("strMonths"+id).selectedIndex = 0;
			$("#strMonths"+id).removeAttr('selected').prop('selected', false);
			/* document.getElementById("strQuarters"+id).selectedIndex = 0;
			document.getElementById("strHalfYears"+id).selectedIndex = 0; */
			document.getElementById("cgoalEffectDate"+id).value = '';
			document.getElementById("cgoalDueDate"+id).value = '';
						
		/* } else if (value == '2') {
			document.getElementById("weekly"+id).style.display = "block";
			document.getElementById("monthly"+id).style.display = "none";
			document.getElementById("weekday"+id).selectedIndex = 0;
			document.getElementById("day"+id).selectedIndex = 0; */
									
		} else if (value == '6') {
			document.getElementById("periodTR"+id).style.display = "none";
			/* document.getElementById("halfYearsTR"+id).style.display = "none"; */
			document.getElementById("quartersHalfYearsTR"+id).style.display = "none";
			document.getElementById("monthsTR"+id).style.display = "none";
			document.getElementById("yearsTR"+id).style.display = "table-row";
			//document.getElementById("strMonths"+id).selectedIndex = 0;
			$("#strMonths"+id).removeAttr('selected').prop('selected', false);
			/* document.getElementById("strQuarters"+id).selectedIndex = 0;
			document.getElementById("strHalfYears"+id).selectedIndex = 0; */
			document.getElementById("cgoalEffectDate"+id).value = '';
			document.getElementById("cgoalDueDate"+id).value = '';
					
		} else if (value == '4') {
			document.getElementById("periodTR"+id).style.display = "none";
			/* document.getElementById("halfYearsTR"+id).style.display = "none"; */
			document.getElementById("quartersHalfYearsTR"+id).style.display = "table-row";
			document.getElementById("quartersSpan"+id).style.display = "inline";
			document.getElementById("halfYearsSpan"+id).style.display = "none";
			document.getElementById("quartersHalfYearsLblSpan"+id).innerHTML = "Select Quarters:";
			
			document.getElementById("monthsTR"+id).style.display = "none";
			document.getElementById("yearsTR"+id).style.display = "table-row";
			//document.getElementById("strMonths"+id).selectedIndex = 0;
			$("#strMonths"+id).removeAttr('selected').prop('selected', false);
			/* document.getElementById("strQuarters"+id).selectedIndex = 0;
			document.getElementById("strHalfYears"+id).selectedIndex = 0; */
			document.getElementById("cgoalEffectDate"+id).value = '';
			document.getElementById("cgoalDueDate"+id).value = '';
		
		} else if (value == '5') {
			document.getElementById("periodTR"+id).style.display = "none";
			/* document.getElementById("halfYearsTR"+id).style.display = "table-row"; */
			document.getElementById("quartersHalfYearsTR"+id).style.display = "table-row";
			document.getElementById("quartersSpan"+id).style.display = "none";
			document.getElementById("halfYearsSpan"+id).style.display = "inline";
			document.getElementById("quartersHalfYearsLblSpan"+id).innerHTML = "Select Half Years:";
			
			document.getElementById("monthsTR"+id).style.display = "none";
			document.getElementById("yearsTR"+id).style.display = "table-row";
			//document.getElementById("strMonths"+id).selectedIndex = 0;
			$("#strMonths"+id).removeAttr('selected').prop('selected', false);
			/* document.getElementById("strQuarters"+id).selectedIndex = 0;
			document.getElementById("strHalfYears"+id).selectedIndex = 0; */
			document.getElementById("cgoalEffectDate"+id).value = '';
			document.getElementById("cgoalDueDate"+id).value = '';
			
		} else {
			document.getElementById("periodTR"+id).style.display = "none";
			/* document.getElementById("halfYearsTR"+id).style.display = "none"; */
			document.getElementById("quartersHalfYearsTR"+id).style.display = "none";
			document.getElementById("monthsTR"+id).style.display = "none";
			document.getElementById("yearsTR"+id).style.display = "none";
			//document.getElementById("strMonths"+id).selectedIndex = 0;
			$("#strMonths"+id).removeAttr('selected').prop('selected', false);
			/* document.getElementById("strQuarters"+id).selectedIndex = 0;
			document.getElementById("strHalfYears"+id).selectedIndex = 0; */
			document.getElementById("cgoalEffectDate"+id).value = '';
			document.getElementById("cgoalDueDate"+id).value = '';
		}
	}
	
	
	function checkUncheckValueInd() {
		var allEmp=document.getElementById("allEmpInd");		
		var strGoalEmpId = document.getElementsByName('strGoalEmpIdInd');
		//alert("strGoalEmpId ===> " + strGoalEmpId);
		var selectID="";
		var status=false;
		//alert("allEmp.checked ===> " + allEmp.checked);
		if(allEmp.checked==true){
			status=true;
			//alert("strGoalEmpId.length ===> " + strGoalEmpId.length);
			 for(var i=0;i<strGoalEmpId.length;i++){
				 strGoalEmpId[i].checked = true;
				  if(i==0){
					  selectID=strGoalEmpId[i].value;
				  }else{
					  selectID+=","+strGoalEmpId[i].value;
				  }
			 }
			 //alert("selectID ===> " + selectID);
		}else{		
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
		//alert("selectID ===> " + selectID);
		var empselect=document.getElementById("empselected").value;
		//alert("empselect ===> " + empselect);
		var xmlhttp = GetXmlHttpObject();
		if (xmlhttp == null) {
			alert("Browser does not support HTTP Request");
			return;
		} else {
			var xhr = $.ajax({
				url : "GetSelectedEmployee.action?type=one&chboxStatus="+status+"&selectedEmp="+selectID+"&existemp="+empselect,		
				cache : false,
				success : function(data) {
					//alert("data ===> "+data);
                	if(data == ""){
                	}else{
                		var allData = data.split("::::");
                        document.getElementById("idEmployeeInfo").innerHTML = allData[0];
                        document.getElementById("teamEmpDiv").innerHTML = allData[1];
                	}
                }
			});
		}
		 
	}
	
	function checkUncheckValue() {
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
		}else{		
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
		
		var empselect=document.getElementById("empselected").value;
		
		var xmlhttp = GetXmlHttpObject();
		if (xmlhttp == null) {
			alert("Browser does not support HTTP Request");
			return;
		} else {
			var xhr = $.ajax({
				//url : "ValidateTimeSheet.action?emp_id=" + emp+"&timesheet_paycycle="+cycle,
				url : "GetSelectedEmployee.action?type=one&chboxStatus="+status+"&selectedEmp="+selectID+"&existemp="+empselect,		
				cache : false,
				success : function(data) {
					//alert("data == "+data);
                	if(data == ""){
                		
                	}else{
                		var allData = data.split("::::");
                        document.getElementById("idEmployeeInfo").innerHTML = allData[0];
                        document.getElementById("teamEmpDiv").innerHTML = allData[1];
                	}
                }
			});
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
		//alert("strID ===> " + strID);
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
		                        document.getElementById("wlocationDiv").innerHTML = allData[0];
		                        document.getElementById("departDiv").innerHTML = allData[1];
		                        document.getElementById("levelDiv").innerHTML = allData[2];
		                        document.getElementById("myDesig").innerHTML = allData[3];
                		  } else if(type =='wloc'){
		                        document.getElementById("departDiv").innerHTML = allData[0];
                		  } else if(type =='level'){
		                        document.getElementById("myDesig").innerHTML = allData[0];
                		  }
                	}
                }
			});
		}
	}
	
	
	
	function getGoalSelectedEmp(checked, emp, form, isInIndiGoal) {

		var empselect=document.getElementById("empselected").value;
		if(empselect != '' && empselect !='0') {
			document.getElementById("cmeasureEffortsHrs").value = '';
		}
		
		if(checked == true) {
			alrtMsg = "Are you sure, you want to add this employee?";
		} else {
			alrtMsg = "Are you sure, you want to remove this employee?";
		}
		if(confirm(alrtMsg)) {	
			var xmlhttp = GetXmlHttpObject();
			if (xmlhttp == null) {
				alert("Browser does not support HTTP Request");
				return;
			} else {
				var xhr = $.ajax({
					//url : "ValidateTimeSheet.action?emp_id=" + emp+"&timesheet_paycycle="+cycle,
					url : "GetSelectedEmployee.action?type=one&chboxStatus="+checked+"&selectedEmp="+emp+"&existemp="+empselect,		
					cache : false,
					success : function(data) {
						//alert("data == "+data);
	                	if(data == "") {
	                		
	                	} else {
	                		var allData = data.split("::::");
	                		if(document.getElementById("idEmployeeInfo")) {
	                			 document.getElementById("idEmployeeInfo").innerHTML = allData[0];
	                		}
	                       
	                		if(document.getElementById("teamEmpDiv")) {
	                			document.getElementById("teamEmpDiv").innerHTML = allData[1];
	                		}
	                	}
	                	
	                	if(checked == false) {
	                     	//alert(checked);
	                     	document.getElementById("strGoalEmpId"+emp).checked = false;
	                     }
	                }
				});
			}
		} else {
			if(checked == true) {
				document.getElementById('strGoalEmpId'+emp).checked = false;
			}
		}
	} 
	
	function validateScore(value) {
		var actscore = document.getElementById("score").value;
		if(parseFloat(value) > parseFloat(actscore)){
			alert("Entered Value Greater Than Weightage");
			document.getElementById("cgoalWeightage").value = actscore;
		}else if(parseFloat(value) <= 0 ){
			alert("Invalid Weightage");
			document.getElementById("cgoalWeightage").value = actscore;
		}
	}
	
	function showManagers(value){
		if(value=="manager"){
			document.getElementById("managerDiv").style.display = "block";
			
		}else{
			document.getElementById("managerDiv").style.display = "none";
			
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


function checkEmpSelected() {
	
	var empselected = document.getElementById("empselected").value;
	if(empselected == '' || empselected =='0'){
		alert("Please, select the employee.");
	}
}

/* function getSelectedValue(selectId) {
	var choice = document.getElementById(selectId);
	var exportchoice = "";
	for ( var i = 0, j = 0; i < choice.options.length; i++) {
		var value = choice.options[i].value;
		if(choice.options[i].selected == true && value != "") {
			
			if (j == 0) {
				exportchoice = "," + choice.options[i].value + ",";
				j++;
			} else {
				exportchoice += choice.options[i].value + ",";
				j++;
			}
		}else if(choice.options[i].selected == true && value == ""){
			exportchoice = "";
			break;
		}
		
	}
	//alert("exportchoice==>"+exportchoice);
	return exportchoice;
} */

function checkFields() {
	
	var goaltype = '<%=goaltype %>';
	
	if(document.getElementById("empselected")) {
		var empselected = document.getElementById("empselected").value;
		if(empselected == '' || empselected =='0'){
			alert("Please, select the employee.");
			return false;
		}
	}
	

	if(document.getElementById("cAddMKra")) {
		var cAddMKra = document.getElementById("cAddMKra").value;
		if(cAddMKra == 'KRA') {
			var cMainKRA = document.getElementById("cMainKRA").value;
			if(cMainKRA == '') {
				alert("Please, fill the KRA");
				return false;
			}
			
			var KRACount = document.getElementById("cKRACount0").value;
			for(var i=0; i<=KRACount; i++) {
				if(document.getElementById("cKRA_"+i)) {
					var KRA = document.getElementById("cKRA_"+i).value;
					if(KRA == '') {
						alert("Please, fill the KRA");
						return false;
					}
				}
				if(document.getElementById("cKRATask_"+i)) {
					var KRATask = document.getElementById("cKRATask_"+i).value;
					if(KRATask == '' && goaltype!=null && goaltype=='4') {
						alert("Please, fill the Task");
						return false;
					}
				}
				if(document.getElementById("KRATaskCount_"+i)) {
					var KRATaskCount = document.getElementById("KRATaskCount_"+i).value;
					if(KRATaskCount == 0 && goaltype!=null && goaltype=='4') {
						alert("Please, add the Task");
						return false;
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
	   if (	charCode <= 31 || (charCode >= 48 && charCode <= 57)) {
	      return true; 
	   }
	   return false;
	}
	
	function checkHrsLimit(){
		var empSelected = '';
	//	var from = document.getElementById("from").value;
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
			
		} else {
			//	alert("empSelected==>"+empSelected+"==>days==>"+days+"==>hrs==>"+hrs);
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

</script>

<%
	UtilityFunctions uF = new UtilityFunctions();
	Map<String, String> hmGoalType = (Map<String, String>) request.getAttribute("hmGoalType");
	String supervisorId = (String) request.getAttribute("supervisorId");
	String score = (String) request.getAttribute("score");
	String orgName = (String) request.getAttribute("orgName");
	List<FillEmployee> empList = (List<FillEmployee>) request.getAttribute("empList");
	List<String> individualEmplist = (List<String>) request.getAttribute("individualEmplist");
	CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
	Map<String, String> hmCheckEmpList = (Map<String, String>) request.getAttribute("hmCheckEmpList");
	if (hmCheckEmpList == null) hmCheckEmpList = new HashMap<String, String>();
	Map<String, String> hmOrgName = (Map<String, String>) request.getAttribute("hmOrgName");
	String strCurrency = (String) request.getAttribute("strCurrency");

	Map<String, String> hmFeatureStatus = (Map<String, String>) request.getAttribute("hmFeatureStatus");
	Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>) request.getAttribute("hmFeatureUserTypeId");
	String strUsertypeId = (String) session.getAttribute(IConstants.BASEUSERTYPEID);
	String dataType = (String) request.getAttribute("dataType");
	String currUserType = (String) request.getAttribute("currUserType");
	if (hmFeatureStatus == null) hmFeatureStatus = new HashMap<String, String>();
%>
<div class="leftbox reportWidth">
	<s:form id="formID1" name="frmEditGoal" theme="simple" action="NewGoal" method="POST" cssClass="formcss" >

		<table class="table table_no_border">
			<tr>
				<th nowrap colspan="4">Setting up a New  
				<%if (goaltype != null && goaltype.equals("1")) { %>
					Company Objective
				<% } else if (goaltype != null && goaltype.equals("2")) { %>
					Departmental Objective
				<% } else if (goaltype != null && goaltype.equals("3")) { %>
					Team Objective
				<% } else if (goaltype != null && goaltype.equals("4")) { %>
					Individual OKR
				<% } %>
				</th>
			</tr>
			
			<tr>
				<th nowrap align="right">Organisation:</th>
				<td colspan="3">
					<%if (goaltype != null && goaltype.equals("1")) { %>
						<input type="hidden" name="hideOrgid" id="hideOrgid" value="<%=request.getAttribute("strOrg")%>"> 
						<%=hmOrgName != null ? hmOrgName.get(request.getAttribute("strOrg").toString()) : ""%>
					<%} else { %>
						 <input type="hidden" name="hideOrgid" id="hideOrgid" value="<%=request.getAttribute("orgID")%>"> 
						 <%=orgName != null ? orgName : ""%>
					<%}%>
				</td>
			</tr>
			<tr>
				<th nowrap align="right">Goal:<sup>*</sup></th>
				<td colspan="3">
					<s:textfield name="corporateGoal" cssClass="validateRequired " cssStyle="width: 600px;" />
					<input type="hidden" name="goaltype" id="goaltype" value="<s:property value="goaltype"/>" /> 
					<input type="hidden" name="goal_parent_id" value="<s:property value="goalid"/>" /> 
					<input 	type="hidden" name="supervisorId" id="supervisorId" value="<%=supervisorId%>" />
				</td>
			</tr>
			<tr>
				<th nowrap align="right">Objective:</th>
				<td colspan="3">
				<input type="text"	name="cgoalObjective" id="cgoalObjective" style="width: 600px;" />
				</td>
			</tr>
			<tr>
				<th align="right" valign="top">Description:</th>
				<td colspan="3">
					<textarea rows="3" cols="72" name="cgoalDescription" id="cgoalDescription"></textarea> 
				</td>
			</tr>

			<%if (goaltype != null && goaltype.equals("2")) { %>
				<tr>
					<th nowrap align="right">Department:<sup>*</sup></th>
					<td colspan="3">
						<s:select theme="simple" name="strDepart" list="departmentList" id="depart" listKey="deptId" listValue="deptName" headerKey=""
							headerValue="All Department" required="true" onchange="getEmployeebyDepart();"></s:select>
					</td>
				</tr>
			<% } else if (goaltype != null && (goaltype.equals("3") || goaltype.equals("4"))) { %>
				<s:hidden name="strDepart" id="depart"></s:hidden>
				<tr>
					<th nowrap align="right">Department:</th>
					<td colspan="3"><%=(String) request.getAttribute("strDepartName") %></td>
				</tr>
			<% } %>
			
			<tr>
				<th nowrap align="right">Priority:<sup>*</sup>
				</th>
				<td colspan="3">
					<s:select theme="simple" name="priority" cssClass="validateRequired " headerKey=""
						headerValue="Select" list="#{'1':'High', '2':'Medium', '3':'Low'}" />
				</td>
			</tr>

			<%
				if (uF.parseToBoolean(hmFeatureStatus .get(IConstants.F_GOAL_ATTRIBUTE_ALIGN)) && hmFeatureUserTypeId.get(IConstants.F_GOAL_ATTRIBUTE_ALIGN).contains(strUsertypeId)) {	%>
					<tr>
						<th align="right">Align an Attribute:<sup>*</sup>
						</th>
						<td colspan="3">
							<span style="float: left; margin-right: 10px;">
								<select name="goalElements" id="goalElements" class="validateRequired " style="width: 130px;" onchange="getAttributes(this.value);">
									<option value="">Select</option>
									<%=request.getAttribute("elementOptions")%>
								</select> 
							</span> 
							
						   <span id="attributeDiv" style="float: left;"> 
						      <s:select theme="simple" name="cgoalAlignAttribute" cssClass="validateRequired " list="attributeList" 
						      listKey="id" listValue="name" headerKey="" headerValue="Select Attribute" /> 
						   </span>
					  </td>
					</tr>
			<% }%>
			
			<%-- <%if (goaltype.equals("4")) { %> --%>
				<tr>
					<th nowrap align="right"><%if (goaltype.equals("4")) { %> Does it have a Measure/KRA: <% } else { %> Does it have a KRA:<% } %></th>
					<td colspan="3">
						<s:select theme="simple" name="cmeasureKra" cssClass="validateRequired " id="cmeasureKra" headerKey="No"
							headerValue="No" list="#{'Yes':'Yes'}" onchange="getMeasureKRA(this.value,'c',0);" value="measureKravalue" />
					</td>
				</tr>
				<tr id="cMKRAID0" style="display: none;">
					<th align="right"><%if (goaltype.equals("4")) { %>Add Measure/KRA:<% } else { %> Add KRA:<% } %><sup>*</sup></th>
					<td colspan="3">
					<%if (goaltype.equals("4")) { %>
						<s:select theme="simple" cssClass=" validateRequired " name="cAddMKra" id="cAddMKra" headerKey="" headerValue="Select" list="#{'KRA':'KRA', 'Measure':'Measure'}"
							onchange="addMeasureKRA(this.value,'c',0);" value="addMKravalue" />
					<% } else { %>
						<s:select theme="simple" cssClass=" validateRequired " name="cAddMKra" id="cAddMKra" headerKey="" headerValue="Select" list="#{'KRA':'KRA'}"
							onchange="addMeasureKRA(this.value,'c',0);" value="addMKravalue" />
					<% } %>
					</td>
				</tr>

				<tr id="ckraID0" style="display: none;">
					<th valign="top" align="right">KRA:</th>
					<td id="cKRAtdID0" colspan="3">
						<input type="hidden" name="cKRACount" id="cKRACount0" value="0" />
						<div id="cKRAdiv0_0" style="width: 100%; float: left; margin-bottom: 3px;">  <!-- border-bottom: 1px solid #CCCCCC; -->
							<div style="width: 100%; float: left; margin-bottom: 3px;">
								<input type="hidden" name="KRATaskCount_0" id="KRATaskCount_0" value="0"> 
								<input class="validateRequired" type="text" name="cKRA_0" id="cMainKRA" />
								Weightage (%): <input type="text" class="validateRequired" style="width: 40px !important;" name="cKRAWeightage_0" id="cKRAWeightage_0" value="100" onkeyup="validateKRAScore(this.value,'cKRAWeightage_0', 'c', 0);" onkeypress="return isNumberKey(event)" />
								<a href="javascript:void(0)" onclick="addKRA('c',0);" title="Add KRA"><i class="fa fa-plus-circle"></i></a>
							</div>
							<%if (goaltype.equals("4")) { %>
								<div style="width: 100%; float: left; margin-bottom: 3px;">
									<span style="float: left; margin-left: 7px;">
										<a href="javascript:void(0)" onclick="addKRATask('c', 0, 0);"><i class="fa fa-plus-circle"></i>Add Task</a>
									</span>
							   	</div>
								<div id="cKRATaskdiv0_0" style="float: left; margin-left: 50px; margin-bottom: 3px;"></div>
							<% } %>	
						</div> 
					</td>
				</tr>

				<tr id="cmeasureID0" style="display: none;">
					<th align="right">Measure with:</th>
					<td colspan="3">
						<s:select theme="simple" cssClass=" validateRequired " name="cmeasurewith" id="cmeasurewith" headerKey="Amount" headerValue="Amount"
						list="#{'Effort':'Effort','Percentage':'Percentage'}" onchange="getMeasureWith(this.value,'c',0);" />
					</td>
				</tr>
				
				<tr id="cdollarAmtid0" style="display: none;">
					<th align="right"><span id="measureSpanId"></span>
					</th>
					<td colspan="3">
						
						<span id="rsSpan0" style="display: block; float: left;"> <%=uF.showData(strCurrency, "")%>&nbsp;</span>
						<span style="float: left;">
							<input class="validateRequired " type="text" name="cmeasureDollar"
							id="cmeasureDollar" style="width: 176px !important;" onkeypress="return isNumberKey(event)" />
						</span> 
						<span id="percentSpan0" style="display: none; float: left;">%</span>
					</td>
				</tr>
				
				<tr id="cmeasureEffortsid0" style="display: none;">
					<th align="right">&nbsp;</th>
					<td colspan="3">
						Days&nbsp;<input class="validateRequired " type="text" name="cmeasureEffortsDays" id="cmeasureEffortsDays"
								   style="width: 40px;" onkeypress="return isOnlyNumberKey(event)" onkeyup="checkHrsLimit();"/>&nbsp;
						Hrs&nbsp;<input type="text" class="validateRequired " name="cmeasureEffortsHrs" style="width: 40px;"
						id="cmeasureEffortsHrs" onkeyup="checkHrsLimit();" onkeypress="return isOnlyNumberKey(event)" />
					</td>
				 </tr>
			<%-- <%} else {	%>
				<tr>
					<th nowrap align="right">Does it have a Measure:<sup>*</sup></th>
					<td colspan="3"><s:select theme="simple" name="cmeasureKra" headerKey="" headerValue="Select" list="#{'Yes':'Yes', 'No':'No'}"
							onchange="showMeasureWith(this.value);" cssClass=" validateRequired " />
					</td>
				</tr>
				<tr id="measureWith" style="display: none;">
					<th align="right">Measure with:</th>
					<td colspan="3">
						<s:select theme="simple" name="cmeasurewith" headerKey="Amount" headerValue="Amount" list="#{'Effort':'Effort','Percentage':'Percentage'}"
						onchange="getMeasureWith(this.value,'c',0);" cssClass=" validateRequired " />
					</td>
				</tr>
				<tr id="cdollarAmtid0" style="display: none;">
					<th align="right"><span id="measureSpanId"></span> </th>
					<td colspan="3">
						<span id="rsSpan0" style="display: block; float: left;"> <%=uF.showData(strCurrency, "")%>&nbsp;</span>
						<span style="float: left;">
							<input class="validateRequired " type="text" name="cmeasureDollar" id="cmeasureDollar" style="width: 176px !important;" onkeypress="return isNumberKey(event)" />
						</span> 
						<span id="percentSpan0" style="display: none; float: left;">&nbsp;%</span>
					</td>
				</tr>
				<tr id="cmeasureEffortsid0" style="display: none;">
					<th align="right">&nbsp;</th>
					<td colspan="3">
						
						Days&nbsp;<input class="validateRequired " type="text" name="cmeasureEffortsDays" id="cmeasureEffortsDays"
							style="width: 40px;" onkeypress="return isOnlyNumberKey(event)" onkeyup="checkHrsLimit();"/>&nbsp;
						Hrs&nbsp;<input class="validateRequired " type="text" name="cmeasureEffortsHrs" style="width: 40px;"
						id="cmeasureEffortsHrs" onkeyup="checkHrsLimit();" onkeypress="return isOnlyNumberKey(event)" />
					</td>
				</tr>
			<%}	%> --%>
				
				<%-- <% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_GOAL_FREQUENCY)) && hmFeatureUserTypeId.get(IConstants.F_GOAL_FREQUENCY).contains(strUsertypeId)) { %> --%>
					<tr>
						<th align="right">Select Frequency:</th> 
					  	<td colspan="3">
						  	<s:select theme="simple" name="frequency" list="frequencyList" listKey="id" listValue="name" onchange="checkFrequency(this.value, '')" />
					    </td>
					</tr>
					
					<tr id="monthsTR" style="display: none;">
						<th align="right">Select Months:</th> 
					  	<td colspan="3">
							<s:select theme="simple" name="strMonths" id="strMonths" list="monthsList" listKey="monthId" listValue="monthName" multiple="true"/>

							   	<%-- <span id="weekly" style="display: none; float: left;">Day:<sup>*</sup>
						           	<s:select theme="simple" name="weekday" id="weekday" cssClass="validateRequired" cssStyle="width:100px;" headerKey="" headerValue="Select Day"
										list="#{'Monday':'Monday','Tuesday':'Tuesday', 'Wednesday':'Wednesday','Thursday':'Thursday','Friday':'Friday','Saturday':'Saturday','Sunday':'Sunday'}" />
							   	</span>  
						   
						  	   	<span id="monthly" style="display: none; float: left;">Date of Month:<sup>*</sup> 
							       	<s:select theme="simple" name="day" id="day" cssStyle="width:100px;" cssClass="validateRequired" headerKey="" headerValue="Select Date"
						               	list="#{'1':'1','2':'2', '3':'3','4':'4','5':'5','6':'6','7':'7','8':'8','9':'9', '10':'10',
						               	'11':'11','12':'12', '13':'13','14':'14','15':'15','16':'16','17':'17','18':'18','19':'19', '20':'20',
						               	'21':'21','22':'22', '23':'23','24':'24','25':'25','26':'26','27':'27','28':'28','29':'29', '30':'30', '31':'31'}" />
							   	</span> --%>
					    </td>
					</tr>
					
					<tr id="quartersHalfYearsTR" style="display: none;">
						<th align="right"><span id="quartersHalfYearsLblSpan">Select Quarters:</span></th> 
					  	<td colspan="3">
						  	<span id="quartersSpan" style="display: none; float: left; margin-right: 15px;">
						  		<s:select theme="simple" name="strQuarters" id="strQuarters" list="quartersList" listKey="monthId" listValue="monthName" multiple="true"/>
					  		</span>
					  		<span id="halfYearsSpan" style="display: none; float: left; margin-right: 15px;">
						  		<s:select theme="simple" name="strHalfYears" id="strHalfYears" list="halfYearsList" listKey="monthId" listValue="monthName" multiple="true"/>
					  		</span>
					  		<span id="yearTypeSpan" style="float: left;">
						  		<s:select theme="simple" name="strYearType" id="strYearType" cssStyle="width: 120px !important;" list="yearTypeList" listKey="yearsID" listValue="yearsName"/>
					  		</span>
					    </td>
					</tr>
					
					<%-- <tr id="halfYearsTR" style="display: none;">
						<th align="right">Select Half Years:</th> 
					  	<td colspan="3">
					  	  	<s:select theme="simple" name="strHalfYears" id="strHalfYears" list="halfYearsList" listKey="monthId" listValue="monthName" multiple="true"/>
					    </td>
					</tr> --%>
					<tr id="yearsTR" style="display: none;">
						<th align="right">Select Recurring Years:</th> 
					  	<td colspan="3">
					  	  	<s:select theme="simple" name="strYears" id="strYears" list="yearsList" listKey="yearsID" listValue="yearsName" multiple="true"/>
					    </td>
					</tr>
				<%-- <% } %> --%>
			
				<tr id="periodTR">
					<th align="right">Select Period:<sup>*</sup></th>
					<td colspan="3" width="250px"><input type="text" name="cgoalEffectDate" id="cgoalEffectDate" class="validateRequired" placeholder="Effective Date" style="width: 100px !important;" />&nbsp;&nbsp;&nbsp;&nbsp;
						<input type="text" name="cgoalDueDate" id="cgoalDueDate" class="validateRequired" placeholder="Due Date" style="width: 100px !important;" />
					</td>
					<%-- <th class="alignRight" width="100px">Due Date:<sup>*</sup></th>
					<td>
						<input type="text" name="cgoalDueDate" id="cgoalDueDate" class="validateRequired" placeholder="Due Date" style="width: 100px !important;" />
					</td> --%>
				</tr>
			
				<%if(goaltype != null && goaltype.equals("4")){ %>
					<tr id="cOrientation0">
						<th align="right">Orientation:</th>
						<td  colspan="3">
							<select name="corientation" id="corientation">
								<%=request.getAttribute("orientation") %>
							</select>
						</td>
					</tr>
				<%} %>
				
				
			<tr>
				<th align="right">Weightage (%):<sup>*</sup>
				<s:hidden name="score" id="score" />
				</th>
				<td colspan="3">
				<input type="text" name="cgoalWeightage" id="cgoalWeightage" class="validateRequired "
					onkeyup="validateScore(this.value)" style="width: 40px;" onkeypress="return isNumberKey(event)" value="<%=uF.showData(score, "0") %>" />
				</td>
			</tr>

			<% if (request.getAttribute("goaltype").equals("1")) { %>
				<tr>
					<th align="right">Hierarchy:</th>
					<td colspan="3">
						<input type="checkbox" name="chkWithDepart" id="chkWithDepart" checked="checked"/> For Department <br/>
						<input type="checkbox" name="chkWithTeam" id="chkWithTeam" checked="checked"/> For Team
					</td>
				</tr>
			<% }%>
			<tr>
				<th colspan="4">Aligning employee objectives to goal: </th>
			</tr>
			
			<tr>
				<td colspan="5" style="font-weight: normal;border: 2px solid rgb(238, 238, 238);padding: 5px;">
				<%if (goaltype != null && !goaltype.equals("4")) { %>
					<table class="tb_style" style="width: 100%">
						<tr>
							<td colspan="5">
								<div class="row row_without_margin">
									<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
										<i class="fa fa-filter" aria-hidden="true"></i>
									</div>
									<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
										<%if (goaltype != null && goaltype.equals("1")) { %>
											<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
												<p style="padding-left: 5px;">Location</p>
												<s:select theme="simple" name="strWlocation" list="workList" id="wlocation" listKey="wLocationId" listValue="wLocationName" headerKey=""
					                                  headerValue="All WorkLocation" required="true" value="{userlocation}" onchange="getEmployeebyLocation();"></s:select>
											</div>
											<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
												<p style="padding-left: 5px;">Department</p>
												<s:select theme="simple" name="strDepart" list="departmentList" id="depart" listKey="deptId" listValue="deptName" headerKey=""
					                                  headerValue="All Department" required="true" onchange="getEmployeebyDepart();"></s:select>
											</div>
										<% } %>
										<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
											<p style="padding-left: 5px;">Level</p>
											<s:select theme="simple" name="strLevel" list="levelList" listKey="levelId" id="strLevel" listValue="levelCodeName" headerKey=""
				                                  headerValue="All Level" required="true" onchange="getEmployeebyLevel()"></s:select>
										</div>
										<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
											<p style="padding-left: 5px;">Designation</p>
											<div id="myDesig">
												<s:select theme="simple" name="strDesignationUpdate" list="desigList" listKey="desigId" id="desigIdV" listValue="desigCodeName"
				                                      headerKey="" headerValue="All Designation" onchange="getEmployeebyDesig();"></s:select>
				                            </div>
										</div>
									</div>
								</div>
								<div class="row row_without_margin" style="margin-top: 20px;">
									<div class="col-lg-7 col_no_padding" id="myEmployee">
										<table id="lt" class="table table-bordered" style="width: 100%">
										<%
											if (empList != null && !empList.equals("") && !empList.isEmpty()) {
												Map<String, String> hmEmpLocation = (Map<String, String>) request.getAttribute("hmEmpLocation");
												Map<String, String> hmWLocation = (Map<String, String>) request.getAttribute("hmWLocation");
												Map<String, String> hmEmpCodeDesig = (Map<String, String>) request.getAttribute("hmEmpCodeDesig");
												Map<String, String> hmEmpName = (Map<String, String>) request.getAttribute("hmEmpName");
										%>
													<thead>
														<tr>
															<th width="10%"><input onclick="checkUncheckValue();"
																type="checkbox" name="allEmp" id="allEmp"></th>
															<th align="center">Employee</th>
															<th align="center">Designation</th>
															<th align="center">Location</th>
															
														</tr>
													</thead>
													<tbody>
													<%
														for (int i = 0; i < empList.size(); i++) {
																String empID = ((FillEmployee) empList.get(i)).getEmployeeId();
																String empName = hmEmpName.get(empID);
																//String empName = ((FillEmployee) empList.get(i)).getEmployeeCode();
																String emplocationID = (empID == null || empID .equals("")) ? "" : hmEmpLocation.get(empID);
																String location = (emplocationID == null || emplocationID .equals("")) ? "" : uF.showData(hmWLocation.get(emplocationID), "");
																String desig = (empID == null || empID.equals("")) ? "" : uF.showData(hmEmpCodeDesig.get(empID), "");
													%>
																<tr>
																	<td>
																		<input type="checkbox" name="strGoalEmpId" id="strGoalEmpId<%=empID%>" onclick="getGoalSelectedEmp(this.checked,this.value, '', '');"
																		value="<%=empID%>" <%if (hmCheckEmpList.get(empID) != null) {%> checked="checked" <%}%>>
																	</td>
																	<td>
																		<a href="javascript: void(0);" onclick="openPanelEmpProfilePopup('<%=empID%>')"><%=empName%></a>
																	</td>
																	<td><%=desig%></td>
																	<td><%=location%></td>
																					
																</tr>
												<%	    }
											 } else { %>
												<tr>
													<td colspan="3"><div class="nodata msg" style="width: 88%"><span>No Employee Found</span></div></td>
												</tr>
											<%	} %>
										</tbody>
									</table>
									</div>
									<div class="col-lg-5 col_no_padding" id="idEmployeeInfo">
										<% List<String> selectEmpList = (List<String>) request.getAttribute("selectEmpList");
										if(selectEmpList == null) selectEmpList = new ArrayList<String>();
									   	if (selectEmpList != null && selectEmpList.size() > 0 && !selectEmpList.isEmpty()) {
									%>
												<div style="padding: 8px 20px; border: 2px solid lightgray;">
													<b>Employee</b>
												</div>
												
												<% for (int i = 0; i < selectEmpList.size(); i++) { %>
													<div style=" margin: 5px;">
														<strong><%=i + 1%>.</strong>&nbsp;&nbsp;<%=selectEmpList.get(i)%>
													</div>
												<% } %>
												<!-- </table> -->
									<%} else { %>
										<div class="nodata msg" style="width: 85%">
											<span>No Employee selected</span>
										</div>
									<% }%>
									<input type="hidden" name="empselected" id="empselected" value="0" />
									
									</div>
								</div> 
							</td>
						</tr>
					</table> 
			<% } else { %>
				<table class="tb_style" style="width: 100%">
					<tr>
						<td colspan="5" style="font-weight: normal;padding: 5px;">
						<div class="row row_without_margin" style="margin-top: 20px;">
							<div class="col-lg-7 col_no_padding" id="myEmployee">
								<table id="lt" class="table table-bordered" style="width: 100%">
								<% 
									if (individualEmplist != null && !individualEmplist.equals("") && !individualEmplist.isEmpty()) {
										Map<String, String> hmEmpLocation = (Map<String, String>) request.getAttribute("hmEmpLocation");
										Map<String, String> hmWLocation = (Map<String, String>) request.getAttribute("hmWLocation");
										Map<String, String> hmEmpCodeDesig = (Map<String, String>) request.getAttribute("hmEmpCodeDesig");
										Map<String, String> hmEmpName = (Map<String, String>) request.getAttribute("hmEmpName");
								%>
										<thead>
											<tr>
												<th width="10%">
												<input onclick="checkUncheckValueInd();" type="checkbox" name="allEmpInd" id="allEmpInd"></th>
												<th align="center">Employee</th>
												<th align="center">Designation</th>
												<th align="center">Location</th>
												
											</tr>
										</thead>
										<tbody>
											<%
												Set<String> setEmpList = new HashSet<String>(individualEmplist);
												Iterator<String> it1 = setEmpList.iterator();
												int i = 0;
												while (it1.hasNext()) {
													String val = it1.next();
													//System.out.println("Emp ID -----> "+val);
													//for (int i = 0; i < individualEmplist.size(); i++) {
													if (val != null && !val.equals("")) {
														i++;
														String empID = val;
														String empName = hmEmpName.get(empID);

														String emplocationID = (empID == null || empID .equals("")) ? "" : hmEmpLocation.get(empID);
														String location = (emplocationID == null || emplocationID.equals("")) ? "" : uF.showData(hmWLocation.get(emplocationID), "");
														String desig = (empID == null || empID.equals("")) ? "" : uF.showData(hmEmpCodeDesig.get(empID), "");
											%>
														<tr>
															<td align="center">
																<input type="checkbox" name="strGoalEmpIdInd" id="strGoalEmpId<%=empID%>" onclick="getGoalSelectedEmp(this.checked, this.value, '', '');"
																value="<%=empID%>" <%if (hmCheckEmpList.get(empID) != null) {%> checked="checked" <%}%>>
															</td>
															<td>
																<a href="javascript: void(0);" onclick="openPanelEmpProfilePopup('<%=empID%>')"><%=empName%></a>
															</td>
															<td><%=desig%></td>
															<td><%=location%></td>
														</tr>
													<%}
												   }
												} else { %>
													<tr>
														<td colspan="3"><div class="nodata msg" style="width: 88%"> <span>No Employee Found</span> </div></td>
													</tr>
											<% } %>
										</tbody>
									</table>
							</div>
							<div class="col-lg-5 col_no_padding" id="idEmployeeInfo">
						<%
										List<String> selectEmpList = (List<String>) request.getAttribute("selectEmpList");
										if(selectEmpList == null) selectEmpList = new ArrayList<String>();
										if (selectEmpList != null && !selectEmpList.isEmpty() && selectEmpList.size()>0) {
									%>
											<div style="border: 2px solid #ccc;">
												<div style="padding: 8px 20px; border: 2px solid lightgray;">
													<b>Employee</b>
												</div>
												<% for (int i = 0; i < selectEmpList.size(); i++) { %>
													<div style="margin: 5px;">
														<strong><%=i + 1%>.</strong>&nbsp;&nbsp;<%=selectEmpList.get(i)%>
													</div>
												<%}%>
												
											</div>
									<%} else { %>
										<div class="nodata msg" style="width: 85%">
											<span>No Employee selected</span>
										</div>
									<%}%>
									<input type="hidden" name="empselected" id="empselected" value="0" />
						</div>
						</div>
						
							</td>
						</tr>
					</table>
		     <%	}%>
				</td>
			</tr>
			
			<% if (request.getAttribute("goaltype").equals("2")) { %>
				<tr>
					<td colspan="5">
						<span style="float: left;"> 
							<input type="radio" name="teamgoalRedio" id="teamgoalRedio0" class="validateRequired" value="manager" onclick="showManagers(this.value);"> Let Manager fill the team goals:
						</span>
						<div id="managerDiv" style="float: left; display: none; padding-left: 10px;">
							<s:select theme="simple" name="managers" list="managerList" listKey="managerId" id="managersIdV" listValue="managerName"
								cssClass=" validateRequired " headerKey="" headerValue="Select Manager" cssStyle="width:150px;"></s:select><!-- id="managers" -->
						</div> 
						<br /> 
						<span style="float: left; width: 100%;">
							<input type="radio" name="teamgoalRedio" id="teamgoalRedio0"  value="self" checked="checked" onclick="showManagers(this.value);"> I want to fill the team goals.
					   	</span>
					   	<br /> 
					   	<span style="float: left; width: 100%;">
							<input type="radio" name="teamgoalRedio" id="teamgoalRedio0"  value="anyone" onclick="showManagers(this.value);"> Anyone can fill the team goals. 
					   	</span>
					  <br />
				  </td>
				</tr>
			<% }%>

			<% if (request.getAttribute("goaltype").equals("3")) { %>
				<tr>
					<th align="right">Responsible person for this team goal:<sup>*</sup>
					</th>
					<td colspan="4">
						<div id="teamEmpDiv">
							<select name="teamEmpID" id="teamEmpID" class="validateRequired " onchange="checkEmpSelected()">
								<option value="">Select</option> 
							</select>
						</div> 
					</td>
				</tr>
			<% }%>
			
		</table>

		<div style="float: left; width: 100%; text-align: center;">
			<s:submit value="Save" cssClass="btn btn-primary" name="submit"></s:submit>
		</div>
	</s:form>
	
</div>


<script>
$(function() {
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
    $('#lt').DataTable({});
    
    $("#formID1_submit").click(function(){
		//$(".validateRequired").prop('required',true);
		$("#formID1").find(".validateRequired").filter(':hidden').prop('required',false);
		$("#formID1").find(".validateRequired").filter(':visible').prop('required',true);
	});
    
    $("#strMonths").multiselect().multiselect();
    $("#strQuarters").multiselect().multiselect();
    $("#strHalfYears").multiselect().multiselect();
    $("#strYears").multiselect().multiselect();
});


$("#formID1").submit(function(event) {
	event.preventDefault();
	if(checkFields()){
		var compGoalId = '<%=compGoalId %>';
		var hideOrgid = document.getElementById("hideOrgid").value;
		var form_data = $("#formID1").serialize();
		$.ajax({ 
			type : 'POST',
			url: "NewGoal.action",
			data: form_data+"&submit=Save",
			success: function(result) {
				<%-- getGoalDashboardData('GoalDashboardData','<%=dataType%>','<%=currUserType%>'); --%>
				if(compGoalId==null || parseInt(compGoalId)==0) {
					getCorporateGoalNameList('CorporateGoalNameList', '<%=dataType %>', '<%=currUserType %>');
				} else {
					getGoalSummary('GoalSummary', compGoalId, '<%=dataType%>', '<%=currUserType%>'); /* , hideOrgid */
				}
			},
			error: function(result){
				<%-- getGoalDashboardData('GoalDashboardData','<%=dataType%>','<%=currUserType%>'); --%>
				if(compGoalId==null || parseInt(compGoalId)==0) {
					getCorporateGoalNameList('CorporateGoalNameList', '<%=dataType %>', '<%=currUserType %>');
				} else {
					getGoalSummary('GoalSummary', compGoalId, '<%=dataType%>', '<%=currUserType%>'); /* , hideOrgid */
				}
			}
		});
	}
});

</script>
