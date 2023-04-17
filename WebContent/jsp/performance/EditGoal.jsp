<%@page import="java.util.Iterator"%>
<%@page import="java.util.HashSet"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.konnect.jpms.select.FillEmployee"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" 
	pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.*"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<script type="text/javascript" src="scripts/customAjax.js"></script>

	<% 	String goaltype = (String)request.getAttribute("goaltype");
		String compGoalId = (String)request.getAttribute("compGoalId");
		//System.out.println("compGoalId ===>> " + compGoalId + " -- goaltype ===>> " + goaltype);
	%>

	<script type="text/javascript">
	    var effectDateString = '<%=request.getAttribute("cgoalEffectDate")%>';
	    var effectDateArray = effectDateString.split("/");
	    var effectDate = new Date(Date.parse(effectDateArray[2]+"-"+effectDateArray[0]+"-"+effectDateArray[1]));
	    
	    var dueDateString = '<%=request.getAttribute("cgoalDueDate")%>';
	    var dueDateArray = dueDateString.split("/");
	    var dueDate = new Date(Date.parse(""+dueDateArray[2]+"-"+dueDateArray[0]+"-"+dueDateArray[1]));
	    
	    $(function(){        
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
	        $("#lt").DataTable();
	        
	        $("input[type='submit']").click(function(){
	        	$("#frmEditGoal").find('.validateRequired').filter(':hidden').prop('required',false);
	        	$("#frmEditGoal").find('.validateRequired').filter(':visible').prop('required',true);
	        });
	        
	        $("#strMonths").multiselect().multiselect();
	        $("#strQuarters").multiselect().multiselect();
	        $("#strHalfYears").multiselect().multiselect();
	        $("#strYears").multiselect().multiselect();
	        
	    });

	var cxtpath='<%=request.getContextPath()%>';
	//var pcount = 0;
	var taskcount = 0;
	function addKRA(ch, count) {
		//var KRACount = document.getElementById(ch + "KRACount" + count).value;
		//var KRACountID = ch + "KRACount" + count;
		//pcount++;
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
			divtag.setAttribute("style", "width: 100%; float: left; margin-bottom: 3px;"); // border-bottom: 1px solid #E9E9E9;
			divtag.id = divid;
			var dataKRATask = '';
			var data = "<div style=\"width: 100%; float: left; margin-bottom: 3px;\"><input type=\"hidden\" name=\"KRATaskCount_"+KRACount+"\" id=\"KRATaskCount_"+KRACount+"\" value=\"0\">"
				+"<input type=\"hidden\" name=\"hideKRAId_"+KRACount+"\" id=\"hideKRAId_"+KRACount+"\"/>"
				+"<input type=\"text\" name=\""+ch+"KRA_"+KRACount+"\" id=\""+ch+"KRA\" class=\"validateRequired form-control \" />"
				+" Weightage (%): <input type=\"text\" name=\"cKRAWeightage_"+KRACount+"\" id=\"cKRAWeightage_"+KRACount+"\" style=\"width: 40px !important;\" value=\""+remainweight+"\" class=\"validateRequired form-control\" onkeyup=\"validateKRAScore(this.value,'cKRAWeightage_"+KRACount+"', '"+ch+"', '"+count+"');\" onkeypress=\"return isNumberKey(event)\" />"
				+"<a href=\"javascript:void(0)\" onclick=\"addKRA('"+ ch + "',"+ count+ ");\"><i class=\"fa fa-plus-circle\"></i>Add KRA</a>&nbsp;"
				+"<a href=\"javascript:void(0)\" onclick=\"removeKRAID('" + divid + "','0');\" class=\"close-font\">"
				+"Remove KRA</a>"
				+"</div>";
				if(goaltype != null && goaltype == '4') {
					dataKRATask = "<div style=\"width: 100%; float: left; margin-bottom: 3px;\"><span style=\"float: left; margin-left: 7px;\">"
					+"<a href=\"javascript:void(0)\" onclick=\"addKRATask('"+ ch + "',"+ count+ ", "+KRACount+");\"><i class=\"fa fa-plus-circle\"></i>Add Task</a></span>"
					/* +"<span style=\"float: left; margin-left: 27px;\"><a href=\"javascript:void(0)\" class=\"add_lvl\" style=\"color: #68AC3B;\" "
					+"onclick=\"selectKRATask('"+ ch + "', "+ count+ ", "+KRACount+");\">Select Task</a></span>" */
					+"</div><div id=\""+ch+"KRATaskdiv"+ count+"_"+KRACount+"\" style=\"float: left; margin-left: 50px; margin-bottom: 3px;\"></div>";
				}
			divtag.innerHTML = data;
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
	
	
		function removeKRAID(id, deletKRAID) {
				
			var kraCnt = document.getElementById("cKRACount0").value;
			var row_skill = document.getElementById(id);
			if (row_skill && row_skill.parentNode && row_skill.parentNode.removeChild) {
				row_skill.parentNode.removeChild(row_skill);
				if(parseInt(kraCnt) > 0) {
					kraCnt--;
				}
			}
			
			document.getElementById(ch + "KRACount" + count).value = kraCnt;
			if(parseInt(deletKRAID) > 0) {
				var cDeleteKRAIds = document.getElementById("cDeleteKRAIds").value;
				if(cDeleteKRAIds.length == 0) {
					cDeleteKRAIds = deletKRAID;
				} else {
					cDeleteKRAIds = cDeleteKRAIds+","+deletKRAID;
				}
				document.getElementById("cDeleteKRAIds").value = cDeleteKRAIds;
			}
			
		}
	
		function addKRATask(ch, count, kraCnt) {
			var taskcount = document.getElementById("KRATaskCount_"+kraCnt).value;
			taskcount++;
			//alert("add task taskcount --->>>> " + taskcount);
			var divid = ch+"KRATaskDiv"+count+"_"+kraCnt+"_"+taskcount;
			var divtag = document.createElement('div');
			divtag.setAttribute("style", "width: 100%; float: left; margin-bottom: 3px;");
			divtag.id = divid;
			var data ="<input type=\"hidden\" name=\"hideKRATask_"+kraCnt+"\" id=\"hideKRATask_"+kraCnt+"\" value=\"0\"/>"
				+"<input type=\"text\" name=\""+ch+"KRATask_"+kraCnt+"\" id=\""+ch+"KRATask_"+kraCnt+"\" class=\"validateRequired form-control \"/> "
				+"<a href=\"javascript:void(0)\"  onclick=\"addKRATask('"+ch+"', "+count+", "+kraCnt+");\"><i class=\"fa fa-plus-circle\"></i>Add Task</a>&nbsp;"
				+"<a href=\"javascript:void(0)\"  class=\"close-font\" onclick=\"removeKRATaskID('" + divid + "', '0','" +kraCnt+ "');\">"
				+"Remove Task</a>";
				//alert("data ====>>> " + data);
			divtag.innerHTML = data;
			document.getElementById(ch + "KRATaskdiv"+count + "_" + kraCnt).appendChild(divtag);
			document.getElementById("KRATaskCount_"+kraCnt).value = taskcount;
			
		}
		
		function removeKRATaskID(id, deletKRATaskID,kraCount) {
		    
			var taskCount = document.getElementById("KRATaskCount_"+kraCount).value;
			var row_skill = document.getElementById(id);
			if (row_skill && row_skill.parentNode && row_skill.parentNode.removeChild) {
				row_skill.parentNode.removeChild(row_skill);
				if(parseInt(taskCount)>0){
					taskCount--;
				}
			}
			
			document.getElementById("KRATaskCount_"+kraCount).value = taskCount;
			
			if(parseInt(deletKRATaskID) > 0) {
				var cDeleteKRATaskIds = document.getElementById("cDeleteKRATaskIds").value;
				if(cDeleteKRATaskIds.length == 0) {
					cDeleteKRATaskIds = deletKRATaskID;
				} else {
					cDeleteKRATaskIds = cDeleteKRATaskIds+","+deletKRATaskID;
				}
				document.getElementById("cDeleteKRATaskIds").value = cDeleteKRATaskIds;
			}
			
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
			document.getElementById("measureWith").style.display = "none";
						
			document.getElementById(ch + "dollarAmtid" + count).style.display = "none";
			document.getElementById(ch + "measureEffortsid" + count).style.display = "none";
		}
	}


	function addMeasureKRA(value, ch, count) {

		if (value == 'KRA') {
			document.getElementById(ch + "kraID" + count).style.display = "table-row";
			document.getElementById("measureWith").style.display = "none";
			document.getElementById(ch + "dollarAmtid" + count).style.display = "none";
			document.getElementById(ch + "measureEffortsid" + count).style.display = "none";
						
		} else if (value == 'Measure') {
			document.getElementById("measureWith").style.display = "table-row";
			document.getElementById(ch + "kraID" + count).style.display = "none";
			document.getElementById(ch + "dollarAmtid" + count).style.display = "table-row";
			document.getElementById(ch + "measureEffortsid" + count).style.display = "none";
			
		} else {
			document.getElementById(ch + "kraID" + count).style.display = "none";
			document.getElementById("measureWith").style.display = "none";
			document.getElementById(ch + "dollarAmtid" + count).style.display = "none";
						
			document.getElementById(ch + "measureEffortsid" + count).style.display = "none";
			
		}
	}
	
	
	/* function checkFrequency(value, id) {
		
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
	} */
	
	function checkFrequency(value, id) {
		//monthsTR quartersTR halfYearsTR periodTR
		if (value == '1') {
			document.getElementById("periodTR"+id).style.display = "table-row";
			document.getElementById("quartersHalfYearsTR"+id).style.display = "none";
			document.getElementById("monthsTR"+id).style.display = "none";
			document.getElementById("yearsTR"+id).style.display = "none";
			document.getElementById("cgoalEffectDate"+id).value = '';
			document.getElementById("cgoalDueDate"+id).value = '';
						
		} else if (value == '3') {
			document.getElementById("periodTR"+id).style.display = "none";
			document.getElementById("quartersHalfYearsTR"+id).style.display = "none";
			document.getElementById("monthsTR"+id).style.display = "table-row";
			document.getElementById("yearsTR"+id).style.display = "table-row";
			document.getElementById("cgoalEffectDate"+id).value = '';
			document.getElementById("cgoalDueDate"+id).value = '';
									
		} else if (value == '6') {
			document.getElementById("periodTR"+id).style.display = "none";
			document.getElementById("quartersHalfYearsTR"+id).style.display = "none";
			document.getElementById("monthsTR"+id).style.display = "none";
			document.getElementById("yearsTR"+id).style.display = "table-row";
			document.getElementById("cgoalEffectDate"+id).value = '';
			document.getElementById("cgoalDueDate"+id).value = '';
					
		} else if (value == '4') {
			document.getElementById("periodTR"+id).style.display = "none";
			document.getElementById("quartersHalfYearsTR"+id).style.display = "table-row";
			document.getElementById("quartersSpan"+id).style.display = "inline";
			document.getElementById("halfYearsSpan"+id).style.display = "none";
			document.getElementById("quartersHalfYearsLblSpan"+id).innerHTML = "Select Quarters:";
			
			document.getElementById("monthsTR"+id).style.display = "none";
			document.getElementById("yearsTR"+id).style.display = "table-row";
			document.getElementById("cgoalEffectDate"+id).value = '';
			document.getElementById("cgoalDueDate"+id).value = '';
		
		} else if (value == '5') {
			document.getElementById("periodTR"+id).style.display = "none";
			document.getElementById("quartersHalfYearsTR"+id).style.display = "table-row";
			document.getElementById("quartersSpan"+id).style.display = "none";
			document.getElementById("halfYearsSpan"+id).style.display = "inline";
			document.getElementById("quartersHalfYearsLblSpan"+id).innerHTML = "Select Half Years:";
			
			document.getElementById("monthsTR"+id).style.display = "none";
			document.getElementById("yearsTR"+id).style.display = "table-row";
			document.getElementById("cgoalEffectDate"+id).value = '';
			document.getElementById("cgoalDueDate"+id).value = '';
			
		} else {
			document.getElementById("periodTR"+id).style.display = "none";
			document.getElementById("quartersHalfYearsTR"+id).style.display = "none";
			document.getElementById("monthsTR"+id).style.display = "none";
			document.getElementById("yearsTR"+id).style.display = "none";
			document.getElementById("cgoalEffectDate"+id).value = '';
			document.getElementById("cgoalDueDate"+id).value = '';
		}
	}
	
	function checkEmpSelected() {
		
		var empselected = document.getElementById("empselected").value;
		if(empselected == '' || empselected =='0'){
			alert("Please, select the employee.");
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
		getContent('myEmployee', action);
		cnt++;
		getWLocDepartLevelDesigByOrg(strOrg,'org');
		if(parseInt(cnt) != 0){
			searchTextField();
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
	    var indiSbEmpIds = document.getElementById("indiSbEmpIds").value;
	    
		var action = 'getGoalEmployeeList.action?strOrg='+ strID + '&location=' + location + '&selectedEmp=' + selectedEmp 
				+ '&indiSbEmpIds=' + indiSbEmpIds;

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
		searchTextField();

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
	    var indiSbEmpIds = document.getElementById("indiSbEmpIds").value;
	    
		document.getElementById("strLevel").selectedIndex = 0;
		document.getElementById("desigIdV").selectedIndex = 0;
		//document.getElementById("gradeIdV").selectedIndex = 0;
		//document.getElementById("employee").selectedIndex = 0;
		
		var action = 'getGoalEmployeeList.action?depart=' + depart + '&selectedEmp=' + selectedEmp + '&indiSbEmpIds=' + indiSbEmpIds;
		
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
	    var indiSbEmpIds = document.getElementById("indiSbEmpIds").value;
	    
		document.getElementById("desigIdV").selectedIndex = 0;
		//document.getElementById("gradeIdV").selectedIndex = 0;
		//document.getElementById("employee").selectedIndex = 0;

		var action = 'getGoalEmployeeList.action?level=' + level + '&selectedEmp=' + selectedEmp + '&indiSbEmpIds=' + indiSbEmpIds;
		
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

			getContent('myEmployee', action);
			//getWLocDepartLevelDesigByOrg(level,'level');
			window.setTimeout(function() {
				getContent('myDesig', 'GetDesignationByLevel.action?strLevel=' + level+'&strOrg='+ strID); 
			}, 200); 
		}
		//searchTextField();
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
	    var indiSbEmpIds = document.getElementById("indiSbEmpIds").value;
	    
		//document.getElementById("gradeIdV").selectedIndex = 0;
		//document.getElementById("employee").selectedIndex = 0;
		
		var action = 'getGoalEmployeeList.action?design=' + design + '&selectedEmp=' + selectedEmp + '&indiSbEmpIds=' + indiSbEmpIds;
		
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
			getContent('myEmployee', action);
			/* window.setTimeout(function() {
				getContent('myGrade', 'getGrade.action?strDesignation=' + design);
			}, 200); */
		}
		searchTextField();
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

	function getAttributes(value) {
		var strID = null;
		if(document.getElementById("strOrg")){
			strID = getSelectedValue("strOrg"); 
	    }
		
	    if(document.getElementById("hideOrgid")){
	    	strID = document.getElementById("hideOrgid").value;
	    }
		   //alert("strID ==> " + strID);

	var action = 'GetAttributeList.action?elementID=' + value + '&orgId=' + strID;
	
		getContent('attributeDiv', action);

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
	
	function getGoalSelectedEmp(checked, emp, form, isInIndiGoal) {
		//alert("isInIndiGoal ==>>> " + isInIndiGoal);
		var empselect = document.getElementById("empselected").value;
		//alert("isInIndiGoal 1 ==>>> " + isInIndiGoal);
		var indiSbEmpIds = document.getElementById("indiSbEmpIds").value;
		//alert("isInIndiGoal 2 ==>>> " + isInIndiGoal);
		//updated by kalpana on 22 oct 2016....start...
		if(empselect != '' && empselect !='0') {
			document.getElementById("cmeasureEffortsHrs").value = '';
		}
		//...end...
		if(checked == false && isInIndiGoal == 'Y') {
			alert("You can not remove this employee, because you assign individual goal to this employee. ");
			document.getElementById("strGoalEmpId"+emp).checked = true;
		} else {	
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
						url : "GetSelectedEmployee.action?type=one&chboxStatus="+checked+"&selectedEmp="+emp+"&existemp="+empselect+'&form='+form
								+'&indiSbEmpIds='+indiSbEmpIds,		
						cache : false,
						success : function(data) {
							//alert("data == "+data);
		                	if(data == ""){
		                		
		                	}else{
		                		var allData = data.split("::::");
		                		if(document.getElementById("idEmployeeInfo")) {
		                			document.getElementById("idEmployeeInfo").innerHTML = allData[0];
		                		}
		                        
		                		if(document.getElementById("teamEmpDiv")) {
		                			document.getElementById("teamEmpDiv").innerHTML = allData[1];
		                		}
		                        
		                	}
		                }
					});
				}
				
				if(checked == false) {
	             	document.getElementById("strGoalEmpId"+emp).checked = false;
	             }
			} else {
				if(checked == true) {
					document.getElementById('strGoalEmpId'+emp).checked = false;
				}
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
			if(document.getElementById("managerDiv")) {
				document.getElementById("managerDiv").style.display = "block";
			}
			
		}else{
			if(document.getElementById("managerDiv")) {
				document.getElementById("managerDiv").style.display = "none";
			}
			
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

	$(document).ready(function() {
		
		var strID = null;
		if(document.getElementById("strOrg")){
		strID = getSelectedValue("strOrg"); 
	    }
		
	    if(document.getElementById("hideOrgid")){
	    	strID = document.getElementById("hideOrgid").value;
	    }
	    //var strOrg = document.getElementById("hideOrgid").value;
	    getWLocDepartLevelDesigByOrg(strID, 'org');
		/* $('#lt').dataTable({
			bJQueryUI : true,
			"sPaginationType" : "full_numbers",
			"aaSorting" : []
		}) */
		if(document.getElementById("goalCType")) {
			var value = document.getElementById("goalCType").value;
			showManagers(value);
		}
	}); 
	
	function searchTextField(){
		//alert("searchTextField");
		
	}

	
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
		   if (charCode <= 31 || (charCode >= 48 && charCode <= 57)) {
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
	List<String> innerList = (List<String>) request.getAttribute("innerList");
	String supervisorId = (String) request.getAttribute("supervisorId");

	Map<String, String> hmGoalType = (Map<String, String>) request.getAttribute("hmGoalType");
	
	String orgId = (String)request.getAttribute("strOrgVal");
	String dataType = (String)request.getAttribute("dataType");
	String currUserType = (String)request.getAttribute("currUserType");
	Map<String, List<List<String>>> hmKRA = (Map<String, List<List<String>>>) request.getAttribute("hmKRA");
	Map<String, List<List<String>>> hmKRATasks = (Map<String, List<List<String>>>) request.getAttribute("hmKRATasks");
	
	List<FillEmployee> empList = (List<FillEmployee>) request.getAttribute("empList");
	
	CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
	Map<String, String> hmCheckEmpList = (Map<String, String>) request.getAttribute("hmCheckEmpList");
	if(hmCheckEmpList==null) hmCheckEmpList=new HashMap<String, String>();
	List<String> individualEmplist = (List<String>)request.getAttribute("individualEmplist");
	Map<String, String> hmOrgName = (Map<String, String>)request.getAttribute("hmOrgName");
	
	Boolean checkClose = (Boolean) request.getAttribute("checkClose");
	Boolean processOrNotFlag = (Boolean) request.getAttribute("processOrNotFlag");
	
	List<String> indiGoalEmpIds = (List<String>) request.getAttribute("indiGoalEmpIds");
	String indiSbEmpIds = (String)request.getAttribute("indiSbEmpIds");
	String strCurrency = (String) request.getAttribute("strCurrency"); 
	String measureKravalue = (String)request.getAttribute("measureKravalue");
	
	Map<String, String> hmFeatureStatus = (Map<String, String>) request.getAttribute("hmFeatureStatus");
	Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>)request.getAttribute("hmFeatureUserTypeId");
	String strUsertypeId = (String)session.getAttribute(IConstants.BASEUSERTYPEID);
	
	if(hmFeatureStatus == null)  hmFeatureStatus = new HashMap<String,String>();
	
%>

<div class="leftbox reportWidth">
	<%
		String manager="", self="", anyone="";	
		if (innerList != null) {
			if(innerList.get(33) != null) {
			if(innerList.get(33).equals("manager")) {
				manager = "checked";
			} else if(innerList.get(33).equals("self")) {
				self = "checked";
			} else if(innerList.get(33).equals("anyone")) {
				anyone = "checked";
			}
		}
			
			//System.out.println("innerList.get(13) ============>"+innerList.get(13));
	%>
	
	<s:form id="frmEditGoal" name="frmEditGoal" theme="simple" action="EditGoal" method="POST" cssClass="formcss" >
		<input type="hidden" name="goalTitle" value="<%=(String)request.getParameter("goalTitle") %>"/>
		<input type="hidden" name="measureKravalue" id="measureKravalue" value="<%=measureKravalue %>"/>
		<table class="table table_no_border form-table">
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
				<th nowrap align="right">Organisation:<sup>*</sup></th>
				<td colspan="3"> 
				<%if(goaltype != null && goaltype.equals("1")){ %>
				<input type="hidden" name="hideOrgid" id="hideOrgid" value="<%=orgId %>">
					<%=hmOrgName.get(orgId) != null ? hmOrgName.get(orgId) : "" %>
					
				<%}else{ %>
				<input type="hidden" name="hideOrgid" id="hideOrgid" value="<%=orgId %>">
					<%=hmOrgName.get(orgId) != null ? hmOrgName.get(orgId) : "" %>
				<%} %>
				</td>
			</tr>
			<tr>
				<th nowrap align="right">Goal:<sup>*</sup></th>
				<td colspan="3"><input type="text" name="corporateGoal" id="corporateGoal" class="validateRequired form-control" style="width: 600px;" value="<%=innerList.get(3)%>" />
					<input type="hidden" name="goalCType" id="goalCType" value="<%=innerList.get(33)%>"/>
					<input type="hidden" name="goal_id" value="<%=innerList.get(0)%>"/> 
					<input type="hidden" name="goaltype" id="goaltype" value="<%=innerList.get(1)%>" />
					<input type="hidden" name="goal_parent_id" value="<%=innerList.get(2)%>" />
					<input type="hidden" name="supervisorId" id="supervisorId" value="<%=supervisorId%>" />
					<s:hidden name="fromPage" id="fromPage"></s:hidden>
				</td>
			</tr>
			<tr>
				<th nowrap align="right">Objective:</th>
				<td colspan="3"><input type="text" name="cgoalObjective" class=" form-control " id="cgoalObjective" style="width: 600px;" value="<%=innerList.get(4)%>" />
				</td>
			</tr>
			<tr>
				<th align="right" valign="top">Description:</th>
				<td colspan="3"><textarea rows="3" cols="72" name="cgoalDescription" id="cgoalDescription" class="form-control "><%=innerList.get(5)%></textarea>
					</td>
			</tr>
			
			<%if (goaltype != null && goaltype.equals("2")) { %>
				<tr>
					<th nowrap align="right">Department:<sup>*</sup>
					</th>
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
				<th nowrap align="right">Priority:<sup>*</sup></th>
				<td colspan="3"><s:select theme="simple" name="priority" headerKey="" cssClass="validateRequired  form-control "
						headerValue="Select" list="#{'1':'High', '2':'Medium', '3':'Low'}"/> 
				</td>
			</tr>
			<% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_GOAL_ATTRIBUTE_ALIGN)) && hmFeatureUserTypeId.get(IConstants.F_GOAL_ATTRIBUTE_ALIGN).contains(strUsertypeId)) { %>
			<tr>
				<th align="right">Align an Attribute:<sup>*</sup></th>
				<td colspan="3">
				<span style="float: left; margin-right: 10px;">
					<select name="goalElements" id="goalElements" class="validateRequired form-control " style="width: 130px;" onchange="getAttributes(this.value);">
					<option value="">Select</option>
					<%=request.getAttribute("elementOptions") %>
					</select>
				</span>
					
				<span id="attributeDiv" style="float: left;">
				<s:select name="cgoalAlignAttribute" list="attributeList" cssClass="validateRequired form-control " listKey="id" 
				listValue="name" headerKey="" headerValue="Select Attribute" value="goalattributevalue"></s:select>
				</span>
				</td>
			</tr>
			<% } %>
			<%-- <% if (innerList.get(1).equals(IConstants.INDIVIDUAL_GOAL+"") || innerList.get(1).equals(IConstants.INDIVIDUAL_KRA+"") || innerList.get(1).equals(IConstants.INDIVIDUAL_TARGET+"")) { %> --%>
				<tr>
					<th nowrap align="right"><%if (goaltype.equals("4")) { %> Does it have a Measure/KRA: <% } else { %> Does it have a KRA:<% } %></th>
					<td colspan="3">
						<% if(!processOrNotFlag) { %>
							<s:select theme="simple" name="cmeasureKra" id="cmeasureKra" cssClass="validateRequired form-control " headerKey="No" headerValue="No" list="#{'Yes':'Yes'}"
								onchange="getMeasureKRA(this.value,'c',0);" value="measureKravalue" />
						<% } else { %>
							<s:property value="measureKravalue"/>
						<% } %>	
					</td>
				</tr>
				
				<tr id="cMKRAID0" <%if(!uF.parseToBoolean(measureKravalue)) { %>style="display: none;" <% } %>>
					<th align="right"><%if (goaltype.equals("4")) { %>Add Measure/KRA:<% } else { %> Add KRA:<% } %><sup>*</sup></th>
					<td colspan="3">
						<% if(!processOrNotFlag) { %>
							<%if (goaltype.equals("4")) { %>
								<s:select theme="simple" cssClass=" validateRequired form-control " name="cAddMKra" id="cAddMKra" headerKey="" headerValue="Select" list="#{'KRA':'KRA', 'Measure':'Measure'}"
									onchange="addMeasureKRA(this.value,'c',0);" value="addMKravalue" />
							<% } else { %>
								<s:select theme="simple" cssClass=" validateRequired form-control " name="cAddMKra" id="cAddMKra" headerKey="" headerValue="Select" list="#{'KRA':'KRA'}"
									onchange="addMeasureKRA(this.value,'c',0);" value="addMKravalue" />
							<% } %>
						<% } else { %>
							<s:property value="addMKravalue"/>
						<% } %>		
				</tr>
			<% String iskra="none";
			   String ismeasure="none";
						
			   if((String)request.getAttribute("addMKravalue")!=null && ((String)request.getAttribute("addMKravalue")).equals("KRA")){
					iskra="table-row";
					ismeasure="none";
								
			   }else if((String)request.getAttribute("addMKravalue")!=null && ((String)request.getAttribute("addMKravalue")).equals("Measure")){
					iskra="none";
					ismeasure="table-row";
			   }
			//System.out.println("addMKravalue====>"+(String)request.getAttribute("addMKravalue"));
			//System.out.println("iskra====>"+iskra);
			//System.out.println("ismeasure====>"+ismeasure);
			%>
			<tr id="ckraID0" style="display: <%=iskra%>;">
				<th valign="top" align="right">KRA:</th>
				<td id="cKRAtdID0" colspan="3">
					<% 
					List<List<String>> goalKraList = hmKRA.get(innerList.get(0));
					String kracount="0";
					if(goalKraList!=null) {
						kracount=""+goalKraList.size();
					 for(int j=0; !goalKraList.isEmpty() && j<goalKraList.size(); j++) {
						List<String> goalkraInnerList=goalKraList.get(j);
						List<List<String>> kraTaskList = hmKRATasks.get(goalkraInnerList.get(0));
					%>
					<div id="cKRAdiv0_<%=j %>" style="width: 100%; float: left; margin-bottom: 3px;"> <!-- border-bottom: 1px solid #E9E9E9; -->
						<div style="width: 100%; float: left; margin-bottom: 3px;">
							<input type="hidden" name="KRATaskCount_<%=j %>" id="KRATaskCount_<%=j %>" value="<%=(kraTaskList != null && !kraTaskList.isEmpty()) ? kraTaskList.size() : "0" %>">
							<input type="hidden" name="hideKRAId_<%=j %>" id="hideKRAId_<%=j %>" value="<%=goalkraInnerList.get(0) %>"/>
							<input type="text" name="cKRA_<%=j %>" id="cMainKRA" class="validateRequired form-control" value="<%=goalkraInnerList.get(1) %>"/>
							Weightage (%): <input type="text" class="validateRequired form-control" style="width: 40px !important;" name="cKRAWeightage_<%=j %>" id="cKRAWeightage_0" value="<%=goalkraInnerList.get(9) %>" onkeyup="validateKRAScore(this.value,'cKRAWeightage_0', 'c', 0);" onkeypress="return isNumberKey(event)" /> 
							<a href="javascript:void(0)" onclick="addKRA('c','0');"><i class="fa fa-plus-circle"></i>Add KRA</a>
							<%
							//updated by kalpana on 18/10/2016.......start..........
							if(j>0) { %>
								<a href="javascript:void(0)" onclick="removeKRAID('cKRAdiv0_<%=j %>', '<%=goalkraInnerList.get(0) %>');" class="close-font">Remove KRA</a>
							<%
							 } 
							//........end.....
							%>
						</div>
						<% if (innerList.get(1).equals(IConstants.INDIVIDUAL_GOAL+"") || innerList.get(1).equals(IConstants.INDIVIDUAL_KRA+"")) { %>
							<div style="width: 100%; float: left; margin-bottom: 3px;">
								<span style="float: left; margin-left: 7px;"><a href="javascript:void(0)" onclick="addKRATask('c', 0, '<%=j %>');"><i class="fa fa-plus-circle"></i>Add Task</a></span>
							</div>
							<div id="cKRATaskdiv0_<%=j %>" style="float: left; margin-left: 50px; margin-bottom: 3px;">
								<% 
								if(kraTaskList != null) {
									 for(int k=0; !kraTaskList.isEmpty() && k<kraTaskList.size(); k++) {
										List<String> kraTaskInnerList = kraTaskList.get(k);
									%>
									<div id="cKRATaskdiv0_<%=k%>_<%=kraTaskInnerList.get(0) %>" style="width: 100%; float: left; margin-bottom: 3px;">
										<input type="hidden" name="hideKRATask_<%=j %>" id="hideKRATask_<%=j %>" value="<%=kraTaskInnerList.get(3) %>"/>
										<input type="text" name="cKRATask_<%=j %>" id="cKRATask_<%=j %>" class="validateRequired form-control" value="<%=kraTaskInnerList.get(1) %>"/>
										<a href="javascript:void(0)" onclick="addKRATask('c', '0', '<%=j %>');"><i class="fa fa-plus-circle"></i>Add Task</a>&nbsp;
										<%
										if(k>0) { %>
										<a href="javascript:void(0)" onclick="removeKRATaskID('cKRATaskdiv0_<%=k%>_<%=kraTaskInnerList.get(0)%>', '<%=kraTaskInnerList.get(3) %>','<%=j%>');" class="close-font">
											Remove Task
										</a>
										<% } %>
									</div>
									<% } %>	
								<% } %>	
							</div>
						<% } %>
						
					</div>
				<% } 
				} else {
				%>		
					<div id="cKRAdiv0_0" style="width: 100%; float: left; margin-bottom: 3px;">  <!-- border-bottom: 1px solid #E9E9E9; -->
						<div style="width: 100%; float: left; margin-bottom: 3px;">
							<input type="hidden" name="KRATaskCount_0" id="KRATaskCount_0" value="0">
							<input type="hidden" name="hideKRAId_0" id="hideKRAId_0"/>
							<input type="text" name="cKRA_0" id="cMainKRA" class="validateRequired form-control"/>
							Weightage (%): <input type="text" class="validateRequired form-control" style="width: 40px !important;" name="cKRAWeightage_0" id="cKRAWeightage_0" value="100" onkeyup="validateKRAScore(this.value,'cKRAWeightage_0', 'c', 0);" onkeypress="return isNumberKey(event)" />
							<a href="javascript:void(0)" onclick="addKRA('c', 0);"><i class="fa fa-plus-circle"></i>Add KRA</a>
						</div>
						<% if (innerList.get(1).equals(IConstants.INDIVIDUAL_GOAL+"") || innerList.get(1).equals(IConstants.INDIVIDUAL_KRA+"")) { %>
							<div style="width: 100%; float: left; margin-bottom: 3px;">
								<span style="float: left; margin-left: 7px;"><a href="javascript:void(0)" onclick="addKRATask('c', 0, 0);"><i class="fa fa-plus-circle"></i>Add Task</a></span>
								<%-- <span style="float: left; margin-left: 27px;"><a href="javascript:void(0)" onclick="selectKRATask('c', 0, 0);"><i class="fa fa-plus-circle"></i>Select Task</a></span> --%>
							</div>
							<div id="cKRATaskdiv0_0" style="float: left; margin-left: 50px; margin-bottom: 3px;"></div>
						<% } %>
					</div>
				<%} %>
					<input type="hidden" name="cKRACount" id="cKRACount0" value="<%=kracount %>" />
					<input type="hidden" name="cDeleteKRAIds" id="cDeleteKRAIds" />
					<input type="hidden" name="cDeleteKRATaskIds" id="cDeleteKRATaskIds" />
				</td>
			</tr>
			<tr id="measureWith" style="display: <%=ismeasure%>;">
				<th align="right">Measure with:</th>
				<td colspan="3">
					<% if(!processOrNotFlag) { %>
						<% if((String)request.getAttribute("addMKravalue")!=null && ((String)request.getAttribute("addMKravalue")).equals("Measure")) { %>
							<s:select theme="simple" name="cmeasurewith" cssClass="validateRequired form-control" headerKey="Amount" headerValue="Amount" list="#{'Effort':'Effort','Percentage':'Percentage'}" 
								onchange="getMeasureWith(this.value,'c',0);" value="measurewithvalue" />
						 <%} else { %>
							<s:select theme="simple" name="cmeasurewith" cssClass="form-control" headerKey="Amount" headerValue="Amount" list="#{'Effort':'Effort','Percentage':'Percentage'}" 
								onchange="getMeasureWith(this.value,'c',0);" value="measurewithvalue" />
							<% } %>
					<% } else { %>
						<s:property value="measurewithvalue"/>
					<% } %>		
				</td>
			</tr>
			<% String isEfforts = "none";
			String isAmt = "none";
			String isRS = "none";
			String isPERCENT = "none";
			String measurewithvalue = (String)request.getAttribute("measurewithvalue");
			
			
			if(measurewithvalue!=null && (measurewithvalue.equals("Effort"))){
				isEfforts = "table-row";
				isAmt = "none";
				
			}else if(measurewithvalue!=null && (measurewithvalue.equals("Amount") || measurewithvalue.equals("Value") || measurewithvalue.equals("Percentage"))) {
				isEfforts = "none";
				isAmt = "table-row";
				
				if(measurewithvalue!=null && measurewithvalue.equals("Amount")) {
					isRS = "block";
				} else if(measurewithvalue!=null && measurewithvalue.equals("Percentage")) {
					isPERCENT = "block";
				}
			}
			
			%>
			<tr id="cdollarAmtid0" style="display: <%=isAmt%>;">
				<th align="right"><span id="measureSpanId"></span></th>
				<td colspan="3">
				<span id="rsSpan0" style="display: <%=isRS %>; float: left;"><%=uF.showData(strCurrency,"")%>&nbsp;</span>
				<span style="float: left;">
					<% if(!processOrNotFlag) { %>
						<input type="text" name="cmeasureDollar" id="cmeasureDollar"  class="validateRequired form-control" style="width: 64px;" value="<%=innerList.get(8)!=null ? innerList.get(8) : "" %>" onkeypress="return isNumberKey(event)"/>
					<% } else { %>
						<%=innerList.get(8)!=null ? innerList.get(8) : "" %>
					<% } %>	
				</span>
				<span id="percentSpan0" style="display: <%=isPERCENT %>; float: left; margin-left: 3px;">%</span>
				</td>
			</tr>  
			<tr id="cmeasureEffortsid0" style="display: <%=isEfforts%>;"> 
				<th align="right">&nbsp;</th>
				<td colspan="3">
					<% if(!processOrNotFlag) { %>
						Days&nbsp;<input type="text" name="cmeasureEffortsDays" id="cmeasureEffortsDays" class="validateRequired form-control" style="width: 40px !important;" value="<%=innerList.get(10)%>" onkeypress="return isOnlyNumberKey(event)"/>&nbsp; 
						Hrs&nbsp;<input type="text" name="cmeasureEffortsHrs" style="width: 40px !important;" class="validateRequired form-control" id="cmeasureEffortsHrs" value="<%=innerList.get(11)%>" onkeyup="checkHrsLimit();" onkeypress="return isOnlyNumberKey(event)"/>
					<% } else { %>
						<%=innerList.get(10)%>&nbsp;Days &nbsp;<%=innerList.get(11)%>&nbsp;Hrs
					<% } %>
				</td>
			</tr>
			
			
			
			
			<%-- <% } else { %>
			
			<% String isMeasureWith="none";
			
			String measureValue = null;
			String isMeasureKra=(String)request.getAttribute("isMeasureKra");
			//System.out.println("isMeasureKra ===>> " + isMeasureKra);
			
			if(isMeasureKra!=null && (isMeasureKra.equalsIgnoreCase("t"))){
				isMeasureWith="table-row";
				measureValue="Yes";
				
			}else{
				isMeasureWith="none";
				measureValue="no";
				
			}
			%>
			<tr>
				<th nowrap align="right">Does it have a Measure:<sup>*</sup></th>
				<td colspan="3"><s:select theme="simple" cssClass=" validateREquired form-control " name="cmeasureKra" id="cmeasureKra" headerKey=""
						headerValue="Select" list="#{'Yes':'Yes', 'No':'No'}" onchange="showMeasureWith(this.value);" value="isMeasureKraVal" />  
				</td>
			</tr>
			
			<tr id="measureWith" style="display: <%=isMeasureWith%>;">
				<th align="right">Measure with:</th>
				<td colspan="3">
				  	<s:select theme="simple" cssClass="validateRequired form-control" name="cmeasurewith" id="cmeasurewith"  headerKey="Amount" headerValue="Amount"
					list="#{'Effort':'Effort','Percentage':'Percentage'}" onchange="getMeasureWith(this.value,'c',0);" value="measurewithvalue" />
				 </td>
			</tr>
			<% String isEfforts = "none";
			String isAmt = "none";
			String isRS = "none";
			String isPERCENT = "none";
			String measurewithvalue = (String)request.getAttribute("measurewithvalue");
			
			if(measurewithvalue!=null && (measurewithvalue.equals("Effort"))) {
				isEfforts="table-row";
				isAmt="none";
				
			}else if(measurewithvalue!=null && (measurewithvalue.equals("Amount") || measurewithvalue.equals("Value") || measurewithvalue.equals("Percentage"))) {
				isEfforts="none";
				isAmt="table-row";
				
				if(measurewithvalue!=null && measurewithvalue.equals("Amount")) {
					isRS = "block";
				} else if(measurewithvalue!=null && measurewithvalue.equals("Percentage")) {
					isPERCENT = "block";
				}
			}
			
			%>
			<tr id="cdollarAmtid0" style="display:<%if(isMeasureWith.equals("none")){ %>none;<%}else{ %> <%=isAmt%> <%}%>;">
				<th align="right"><span id="measureSpanId"></span></th>
				<td colspan="3">
				
				<span id="rsSpan0" style="display: <%=isRS %>; float: left;"><%=uF.showData(strCurrency,"")%>&nbsp;</span>
				<span style="float: left;"><input type="text" class="validateRequired form-control" name="cmeasureDollar" id="cmeasureDollar" style="width: 64px;" value="<%=innerList.get(8)!=null ? innerList.get(8) : "" %>" onkeypress="return isNumberKey(event)"/></span>
				<span id="percentSpan0" style="display: <%=isPERCENT %>; float: left; margin-left: 3px;">%</span>
				</td>
			</tr> 
			<tr id="cmeasureEffortsid0" style="display: <%if(isMeasureWith.equals("none")){ %>none;<%}else{ %> <%=isEfforts%> <%}%>;"> 
				<th align="right">&nbsp;</th>
				<td colspan="3">
				<!-- <input type="text" name="cMeasureDesc" id="cMeasureDesc"/>&nbsp;&nbsp;&nbsp; -->
				Days&nbsp;<input type="text" class="validateRequired form-control" name="cmeasureEffortsDays"
					id="cmeasureEffortsDays" style="width: 40px !important;"  value="<%=innerList.get(10)%>" onkeyup="checkHrsLimit();" onkeypress="return isOnlyNumberKey(event)"/>&nbsp; Hrs&nbsp;<input
					type="text" class="validateRequired form-control" name="cmeasureEffortsHrs" style="width: 40px !important;"
					id="cmeasureEffortsHrs"  value="<%=innerList.get(11)%>" onkeyup="checkHrsLimit();" onkeypress="return isOnlyNumberKey(event)"/>
				</td>
			</tr>
			<%} %> --%>
			
			
			<%-- <% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_GOAL_FREQUENCY)) && hmFeatureUserTypeId.get(IConstants.F_GOAL_FREQUENCY).contains(strUsertypeId)) { %>
				<tr>
					  <%
						String strWeek = "none";
						String strDay = "none";
						if(uF.parseToInt(innerList.get(28)) == 2) {
							strWeek = "block";
							
						} else if(uF.parseToInt(innerList.get(28)) == 3) {
							strDay = "block";
						}
					%>
				  <th valign="top">Select Frequency:</th> <!-- Select Frequency for Goal -->
					  <td colspan="5">
						  <div style="position: reletive;">
							  <span style="float: left; margin-right: 20px;">
								  <select name="frequency" id= "frequency" onchange="checkFrequency(this.value, '')">
								  <%=innerList.get(35) %>
								  </select>                            
							  </span>
						   
							   <span id="weekly" style="display: <%=strWeek %>; float: left;">Day:<sup>*</sup>
						           <select name="weekday" id="weekday" style="width:100px;" class="validateRequired form-control">
						           <option value="">Select Day</option>
						           <%=innerList.get(36) %>
						           </select>    
							   </span>  
						   
							   <span id="monthly" style="display: <%=strDay %>; float: left;">Date of Month:<sup>*</sup> 
							       <select name="day" id="day" style="width:65px;" class="validateRequired form-control">
							       <option value="">Select Date</option>
							        <%=innerList.get(37) %>
							       </select>
							   </span>
						    </div>
			   		  </td>
					</tr>
				<% } %> --%>
				
				<%
						String strMonthsTR = "none";
						String strQuartersHalfYearsTR = "none";
						String strQuartersSpan = "none";
						String strHalfYearsSpan = "none";
						String strYearsTR = "none";
						String strPeriodTR = "none";
						String quartersHalfYearsLbl = " Quarters";
						if(uF.parseToInt(innerList.get(28)) == 1) {
							strPeriodTR = "table-row";
							
						} else if(uF.parseToInt(innerList.get(28)) == 3) {
							strMonthsTR = "table-row";
							strYearsTR = "table-row";
						} else if(uF.parseToInt(innerList.get(28)) == 4) {
							strQuartersHalfYearsTR = "table-row";
							strQuartersSpan = "inline";
							strYearsTR = "table-row";
						} else if(uF.parseToInt(innerList.get(28)) == 5) {
							strQuartersHalfYearsTR = "table-row";
							strHalfYearsSpan = "inline";
							strYearsTR = "table-row";
							quartersHalfYearsLbl = " Half Years";
						} else if(uF.parseToInt(innerList.get(28)) == 6) {
							strYearsTR = "table-row";
						}
					%>
				<tr>
					<th align="right">Select Frequency:</th> 
				  	<td colspan="3">
				  		<select name="frequency" id="frequency" onchange="checkFrequency(this.value, '')">
							<%=innerList.get(35) %>
						</select>
					  	<%-- <s:select theme="simple" name="frequency" list="frequencyList" listKey="id" listValue="name" onchange="checkFrequency(this.value, '')" /> --%>
				    </td>
				</tr>
				
				<tr id="monthsTR" style="display: <%=strMonthsTR %>;">
					<th align="right">Select Months:</th> 
				  	<td colspan="3">
						<select name="strMonths" id="strMonths" multiple="multiple">
							<%=innerList.get(36) %>
						</select>
						<%-- <s:select theme="simple" name="strMonths" id="strMonths" list="monthsList" listKey="monthId" listValue="monthName" multiple="true"/> --%>
				    </td>
				</tr>
				
				<tr id="quartersHalfYearsTR" style="display: <%=strQuartersHalfYearsTR %>;">
					<th align="right"><span id="quartersHalfYearsLblSpan">Select <%=quartersHalfYearsLbl %>:</span></th> 
				  	<td colspan="3">
					  	<span id="quartersSpan" style="display: <%=strQuartersSpan %>; float: left; margin-right: 15px;">
					  		<select name="strQuarters" id="strQuarters" multiple="multiple">
								<%=innerList.get(37) %>
							</select>
					  		<%-- <s:select theme="simple" name="strQuarters" id="strQuarters" list="quartersList" listKey="monthId" listValue="monthName" multiple="true"/> --%>
				  		</span>
				  		<span id="halfYearsSpan" style="display: <%=strHalfYearsSpan %>; float: left; margin-right: 15px;">
					  		<select name="strHalfYears" id="strHalfYears" multiple="multiple">
								<%=innerList.get(38) %>
							</select>
					  		<%-- <s:select theme="simple" name="strHalfYears" id="strHalfYears" list="halfYearsList" listKey="monthId" listValue="monthName" multiple="true"/> --%>
				  		</span>
				  		<span id="yearTypeSpan" style="float: left;">
				  			<select name="strYearType" id="strYearType" style="width: 120px ! important;">
				  				<%=innerList.get(41) %>
							</select>
					  		<%-- <s:select theme="simple" name="strYearType" id="strYearType" list="yearTypeList" listKey="yearsID" listValue="yearsName"/> --%>
				  		</span>
				    </td>
				</tr>
				
				<tr id="yearsTR" style="display: <%=strYearsTR %>;">
					<th align="right">Select Recurring Years:</th> 
				  	<td colspan="3">
				  	  	<select name="strYears" id="strYears" multiple="multiple">
							<%=innerList.get(39) %>
						</select>
				  	  	<%-- <s:select theme="simple" name="strYears" id="strYears" list="yearsList" listKey="yearsID" listValue="yearsName" multiple="true"/> --%>
				    </td>
				</tr>
		
				<tr id="periodTR" style="display: <%=strPeriodTR %>;">
					<th align="right">Select Period:<sup>*</sup></th>
					<td colspan="3" width="250px"><input type="text" name="cgoalEffectDate" id="cgoalEffectDate" class="validateRequired" value="<%=innerList.get(32)%>" placeholder="Effective Date" style="width: 100px !important;" />&nbsp;&nbsp;&nbsp;&nbsp;
						<input type="text" name="cgoalDueDate" id="cgoalDueDate" class="validateRequired" value="<%=innerList.get(16)%>" placeholder="Due Date" style="width: 100px !important;" />
					</td>
				</tr>
			
			 
				<%-- <tr>
					<th align="right">Effective Date:<sup>*</sup></th>
					<td width="250px"><input type="text" name="cgoalEffectDate" id="cgoalEffectDate" class="validateRequired form-control " style="width: 70px;" value="<%=innerList.get(32)%>"/></td>
					<th class="alignRight" width="100px">Due Date:<sup>*</sup></th>
					<td><input type="text" name="cgoalDueDate" id="cgoalDueDate" class="validateRequired form-control " style="width: 70px;" value="<%=innerList.get(16)%>" /></td>
				</tr> --%>
				
			<%if(goaltype != null && goaltype.equals("4")){ %>
				
				<tr id="cOrientation0">
					<th align="right">Orientation:</th>
					<td  colspan="3">
				  		<select name="corientation">
				 			 <%=innerList.get(40) %>
				  		</select>  
					</td>
				</tr>
			<% } %>
			
			
			<tr>
				<th align="right">Weightage (%):<sup>*</sup><s:hidden name="score" id="score"/></th>
				<td colspan="3"><input type="text" name="cgoalWeightage" id="cgoalWeightage" class="validateRequired form-control"
					value="<%=innerList.get(19)%>" onkeypress="return isNumberKey(event)" onkeyup="validateScore(this.value)" /></td>
			</tr>
			
			<% if (request.getAttribute("goaltype").equals("1")) { %>
				<tr>
					<th align="right">Hierarchy:</th>
					<td colspan="3">
						<input type="checkbox" name="chkWithDepart" id="chkWithDepart" <%=(String) request.getAttribute("checkdWithDepart") %>/> For Department <br/>
						<input type="checkbox" name="chkWithTeam" id="chkWithTeam" <%=(String) request.getAttribute("checkdWithTeam") %>/> For Team
					</td>
				</tr>
			<% }%>
			
			<tr>
				<th colspan="4">Aligning employee objectives to goal: </th>
			</tr>
			
			<tr>
				<td colspan="5" style="font-weight: normal;border: 2px solid rgb(238, 238, 238);padding: 5px;">
				
				<%if(!request.getAttribute("goaltype").equals("4")){ %>
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
							<table id="lt" class="table table-bordered">
						<%
							if (empList != null && !empList.equals("") && !empList.isEmpty()) {
								Map<String, String> hmEmpLocation = (Map<String, String>)request.getAttribute("hmEmpLocation");
								Map<String, String> hmWLocation = (Map<String, String>)request.getAttribute("hmWLocation");
								Map<String, String> hmEmpCodeDesig = (Map<String, String>)request.getAttribute("hmEmpCodeDesig");
								Map<String, String> hmEmpName = (Map<String, String>)request.getAttribute("hmEmpName");
						%>
						<thead>
						<tr>
							<th width="10%"><input onclick="checkUncheckValue();"
								type="checkbox" name="allEmp" id="allEmp">
							</th>
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
									String empName = hmEmpName.get(empID);
									//String empName = ((FillEmployee) empList.get(i)).getEmployeeCode();

									String emplocationID = (empID == null || empID.equals("")) ? "" : hmEmpLocation.get(empID);
									String location = (emplocationID == null || emplocationID.equals("")) ? "" : uF.showData(hmWLocation.get(emplocationID), "");

									String desig = (empID == null || empID.equals("")) ? "": uF.showData(hmEmpCodeDesig.get(empID), "");
									
									String isInIndiGoal = "N";
									if(indiGoalEmpIds.contains(empID)) {
										isInIndiGoal = "Y";
									}
						%>
								<tr>
									<td align="center"><input type="checkbox" name="strGoalEmpId" id="strGoalEmpId<%=empID%>" onclick="getGoalSelectedEmp(this.checked,this.value, 'goalSummary', '<%=isInIndiGoal %>');"
										value="<%=empID%>" <%if (hmCheckEmpList.get(empID) != null) {%> checked="checked" <%}%>>
									</td>
									<td><a href="javascript: void(0);"  onclick="parent.openPanelEmpProfilePopup('<%=empID%>')"><%=empName%></a></td>
									<td><%=desig%></td>
									<td><%=location%></td>
								</tr>
						<% }
						} else { %>
						<tr>
							<td colspan="3"><div class="nodata msg" style="width: 88%">
									<span>No Employee Found</span>
								</div></td>
						</tr>
						<% } %>
						</tbody>
					</table>
						</div>
						<div class="col-lg-5 col_no_padding">
						<% String empids=(String)request.getAttribute("empids"); %>
							<div id="idEmployeeInfo">
						
						<%
						List<List<String>> selectEmpList = (List<List<String>>) request.getAttribute("selectEmpList");
						if (selectEmpList != null && !selectEmpList.isEmpty() && selectEmpList.size() > 0) {
						%>
							<div style="padding: 8px 20px; border: 2px solid lightgray;"><b>Employee</b></div>
								<% for (int i = 0; i < selectEmpList.size(); i++) { 
									List<String> innerList1 = (List<String>) selectEmpList.get(i);
									String isInIndiGoal = "N";
									if(indiGoalEmpIds.contains(innerList1.get(0))) {
										isInIndiGoal = "Y";
									}
								%>
									<div style="width: 100%; margin: 5px;"><strong><%=i + 1%>.</strong>&nbsp;&nbsp;<%=innerList1.get(1)%>
									<%if (!processOrNotFlag) { %>
										<a href="javascript: void(0)" onclick="getGoalSelectedEmp(false, '<%=innerList1.get(0)%>', 'goalSummary', '<%=isInIndiGoal %>');">
										<img border="0" style="width: 12px; height: 12px;" src="<%=request.getContextPath()%>/images1/arrow_reset1.png"/></a>
									<% } %>	
									</div>
								<% } %>
						<% } else { %>
						<div class="nodata msg" style="width: 85%">
							<span>No Employee selected</span>
						</div>
						<% } %>
						<input type="hidden" name="empselected" id="empselected" value="<%=empids!=null && !empids.equals("") ? empids :"0" %>"/>
						<input type="hidden" name="indiSbEmpIds" id="indiSbEmpIds" value="<%=indiSbEmpIds!=null ? indiSbEmpIds :"" %>"/>
				</div>
						</div>
					</div>
				
		<%} else { %>
			<div class="row row_without_margin">
				<div class="col-lg-7 col_no_padding" id="myEmployee">
					<table id="lt" class="table table-bordered" style="width: 100%">
						<%
						if (individualEmplist != null && !individualEmplist.equals("") && !individualEmplist.isEmpty()) {
							Map<String, String> hmEmpLocation = (Map<String, String>)request.getAttribute("hmEmpLocation");
							Map<String, String> hmWLocation = (Map<String, String>)request.getAttribute("hmWLocation");
							Map<String, String> hmEmpCodeDesig = (Map<String, String>)request.getAttribute("hmEmpCodeDesig");
							Map<String, String> hmEmpName = (Map<String, String>)request.getAttribute("hmEmpName");
						%>
						<thead>
							<tr>
								<th width="10%">
									<input onclick="checkUncheckValue();" type="checkbox" name="allEmp" id="allEmp" <%if (processOrNotFlag) {%>disabled="disabled"<% } %>>
								</th>
								<th align="center">Employee</th>
								<th align="center">Designation</th>
								<th align="center">Location</th>
								<!-- <th align="center">Factsheet</th> -->
							</tr>
						</thead>
						<tbody>
						<%
						//System.out.println("processOrNotFlag ==>>> " + processOrNotFlag);
						Set<String> setEmpList = new HashSet<String>(individualEmplist);
						Iterator<String> it1 = setEmpList.iterator();
						int i=0;
						while(it1.hasNext()){
							String val=it1.next();
							//System.out.println("Emp ID -----> "+val);
							//for (int i = 0; i < individualEmplist.size(); i++) {
							if(val != null && !val.equals("")){
								i++;										
								String empID = val;
								String empName = hmEmpName.get(empID);

								String emplocationID = (empID == null || empID.equals("")) ? "" : hmEmpLocation.get(empID);
								String location = (emplocationID == null || emplocationID.equals("")) ? "" : uF.showData(hmWLocation.get(emplocationID), "");

								String desig = (empID == null || empID.equals("")) ? "": uF.showData(hmEmpCodeDesig.get(empID), "");
						%>
						<tr>
							<td><input type="checkbox" name="strGoalEmpId" id="strGoalEmpId<%=empID%>" onclick="getGoalSelectedEmp(this.checked,this.value, 'goalSummary', '');"
								value="<%=empID%>" <%if (hmCheckEmpList.get(empID) != null) {%>
								checked="checked" <%}%> <%if (processOrNotFlag) {%>disabled="disabled"<% } %>>
							</td>
							<td><a href="javascript: void(0);" onclick="parent.openPanelEmpProfilePopup('<%=empID%>')"><%=empName%></a></td>
							<td><%=desig%></td>
							<td><%=location%></td>
						</tr>
						<% }
							}
							} else { %>
						<tr>
							<td colspan="3"><div class="nodata msg" style="width: 88%"><span>No Employee Found</span></div></td>
						</tr>
						<% } %>
						</tbody>
					</table>
			</div>
			<div class="col-lg-5 col_no_padding">
				<% String empids=(String)request.getAttribute("empids"); %>
				
				<div id="idEmployeeInfo">
					<%
					List<List<String>> selectEmpList = (List<List<String>>) request.getAttribute("selectEmpList");
					if (selectEmpList != null && !selectEmpList.isEmpty() && selectEmpList.size() > 0) {
					%>
						<div style="padding: 8px 20px; border: 2px solid lightgray;"><b>Employee</b></div>
							<% for (int i = 0; i < selectEmpList.size(); i++) { 
								List<String> innerList1 = (List<String>) selectEmpList.get(i);
							%>
								<div style="width: 100%; margin: 5px;"><strong><%=i + 1%>.</strong>&nbsp;&nbsp;<%=innerList1.get(1)%>
								<%if (!processOrNotFlag) { %>
									<a href="javascript: void(0)" onclick="getGoalSelectedEmp(false, '<%=innerList1.get(0)%>', 'goalSummary', '');">
									<img border="0" style="width: 12px; height: 12px;" src="<%=request.getContextPath()%>/images1/arrow_reset1.png"/></a>
								<% } %>	
								</div>
							<% } %>
					<% } else { %>
					<div class="nodata msg" style="width: 85%">
						<span>No Employee selected</span>
					</div>
					<% } %>
					<input type="hidden" name="empselected" id="empselected" value="<%=empids!=null && !empids.equals("") ? empids :"0" %>"/>
					<input type="hidden" name="indiSbEmpIds" id="indiSbEmpIds" value="<%=indiSbEmpIds!=null ? indiSbEmpIds :"" %>"/>
				</div>
			</div>
		</div>				
		<% } %>
				</td>
			</tr>
			
			<%if(request.getAttribute("goaltype").equals("2")){ %>
				<tr>
				
					<td colspan="5"><span style="float: left;"> <input type="radio" name="teamgoalRedio" id="teamgoalRedio0" class="validateRequired" value="manager" <%=manager %> onclick="showManagers(this.value);"> Let Manager fill the team goals:</span>
					<div id="managerDiv" style="float: left; display: none; padding-left: 10px;">
		                  <s:select theme="simple" name="managers" list="managerList" listKey="managerId" id="managersIdV" listValue="managerName"
		                      cssClass="validateRequired form-control " headerKey="" headerValue="Select Manager" cssStyle="width:150px;" value="goalCreaterId"></s:select>
		          	</div>
					 <br/>
					<span style="float: left; width: 100%;"><input type="radio" name="teamgoalRedio" id="teamgoalRedio0"  value="self" <%=self %> onclick="showManagers(this.value);"> I want to fill the team goals. </span><br/>
					<span style="float: left; width: 100%;"><input type="radio" name="teamgoalRedio" id="teamgoalRedio0" value="anyone" <%=anyone %> onclick="showManagers(this.value);"> Anyone can fill the team goals. </span><br/>
					</td>
				</tr>
				<%} %>
				
				<%if(request.getAttribute("goaltype").equals("3")){ %>
				<tr>
				<th align="right">Responsible person for this team goal:<sup>*</sup></th>
					<td colspan="4">
						<div id="teamEmpDiv">
							<select name ="teamEmpID" id ="teamEmpID" class=" validateRequired form-control " onchange="checkEmpSelected()">
								<option value="">Select</option>
								<%=request.getAttribute("SelectEmpOption") %>
							</select>
						</div> 
					</td>
				</tr>
				<%} %>

		</table>

		<div style="float: left; width: 100%; text-align: center;">
			<%if(checkClose){ %>
				<input type="button" name="submit" value="Save" class="btn btn-primary" onclick="alert('This is closed');"/>
			<%} else { %>
				<s:submit value="Save" cssClass="btn btn-primary" name="submit"></s:submit>
			<%} %>

		</div>
	</s:form>
	<% 	} %>
</div>


<div id="PanelEmpProfilePopup"></div>

<script>

$("#frmEditGoal").submit(function(event){
	event.preventDefault();
	if(checkFields()){
		var compGoalId = '<%=compGoalId %>';
		var form_data = $("#frmEditGoal").serialize();
		$.ajax({ 
			type : 'POST',
			url: "EditGoal.action",
			data: form_data+"&submit=Save",
			success: function(result){
				getGoalSummary('GoalSummary', compGoalId, '<%=dataType%>', '<%=currUserType%>'); <%-- , '<%=orgId %>' --%>
				getCorporateGoalNameList('CorporateGoalNameList', '<%=dataType %>', '<%=currUserType %>');<%-- Created by dattatray --%>
			},
			error: function(result){
				getGoalSummary('GoalSummary', compGoalId, '<%=dataType%>', '<%=currUserType%>'); <%-- , '<%=orgId %>' --%>
				getCorporateGoalNameList('CorporateGoalNameList', '<%=dataType %>', '<%=currUserType %>');<%-- Created by dattatray --%>
			}
		});
	}
	
});


</script>