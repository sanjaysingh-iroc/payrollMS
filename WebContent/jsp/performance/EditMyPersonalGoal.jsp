<%@page import="java.util.Iterator"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.konnect.jpms.select.FillEmployee"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<style>
#lt_wrapper .row{
margin-left: 0px;
margin-right: 0px;
}
#lt_wrapper{padding: 5px;}
</style> 
<script src='scripts/customAjax.js'></script>
<script>

	$(function(){
		
		$("input[type='submit']").click(function(){
			$("#frmEditGoal").find('.validateRequired').filter(':hidden').prop('required',false);
			$("#frmEditGoal").find('.validateRequired').filter(':visible').prop('required',true);
		});
		
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
	    
	    if(document.getElementById('lt')) {
			$('#lt').DataTable();
	    }
	 
	    $("#strMonths").multiselect().multiselect();
	    $("#strQuarters").multiselect().multiselect();
	    $("#strHalfYears").multiselect().multiselect();
	    $("#strYears").multiselect().multiselect();
	    
	});

	$(function(){
		onloadFilterByOrg();
		// binds form submission and fields to the validation engine
	    var typeAs1 = '<%=request.getAttribute("typeas") %>';
	    if(typeAs1 == 'goal') {
	    	showTeamGoalsOnload();
	    }
	});

	var cxtpath='<%=request.getContextPath()%>';
	//var taskcount = 0;
	 function addKRA(ch, count, goalCnt) {
		var KRACount = document.getElementById(ch + "KRACount0" +goalCnt).value;
		
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
			
			var divid = ch+"KRAdiv0"+"_"+KRACount+goalCnt;
			var divtag = document.createElement('div');
			divtag.setAttribute("style", "width: 100%; float: left; margin: 3px opx; border-bottom: 1px solid #F3F3F3;");
			divtag.id = divid;
			var data = "<div style=\"width: 100%; float: left; margin-bottom: 3px;\"><input type=\"hidden\" name=\"KRATaskCount_"+KRACount+goalCnt+"\" id=\"KRATaskCount_"+KRACount+goalCnt+"\" value=\"0\">"
				+"<input type=\"text\" name=\""+ch+"KRA_"+KRACount+goalCnt+"\" id=\""+ch+"KRA_"+KRACount+goalCnt+"\"  class=\"validateRequired   form-control \"/> "
				+" Weightage (%): <input type=\"text\" name=\"cKRAWeightage_"+KRACount+goalCnt+"\" id=\"cKRAWeightage_"+KRACount+goalCnt+"\" value=\""+remainweight+"\" class=\"validateRequired   form-control \" style=\"width: 40px !important;\" onkeyup=\"validateKRAScore(this.value,'cKRAWeightage_"+KRACount+goalCnt+"', '"+ch+"', '"+count+"', '"+goalCnt+"');\" onkeypress=\"return isNumberKey(event)\" />"
				+"<a href=\"javascript:void(0)\" onclick=\"addKRA('"+ ch + "',"+ count+ ", '"+goalCnt+"');\"><i class=\"fa fa-plus-circle\"></i>Add KRA</a>&nbsp;"
				+"<a href=\"javascript:void(0)\" style=\"color: red\" onclick=\"removeKRAID('" + divid + "','" + KRACountID + "','" + goalCnt + "');\">"
				/* +"<img border=\"0\" style=\"height: 16px; width: 16px;\" src=\""+cxtpath+"/images1/icons/icons/close_button_icon.png\"/>&nbsp;Remove KRA</a>" */
				+"<i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"></i>&nbsp;Remove KRA</a>"
				
				+"</div><div style=\"width: 100%; float: left; margin-bottom: 3px;\"><span style=\"float: left; margin-left: 7px;\">"
				+"<a href=\"javascript:void(0)\" onclick=\"addKRATask('"+ ch + "',"+ count+ ", "+KRACount+", '"+goalCnt+"');\"><i class=\"fa fa-plus-circle\"></i>Add Task</a></span>"
				/* +"<span style=\"float: left; margin-left: 27px;\"><a href=\"javascript:void(0)\" class=\"add_lvl\" style=\"color: #68AC3B;\" "
				+"onclick=\"selectKRATask('"+ ch + "', "+ count+ ", "+KRACount+", '"+goalCnt+"');\">Select Task</a></span>" */
				+"</div><div id=\""+ch+"KRATaskdiv"+ count+"_"+KRACount+goalCnt+"\" style=\"float: left; margin-left: 50px;\"></div>";
			divtag.innerHTML = data;
			document.getElementById(ch + "KRAtdID" + count+goalCnt+"").appendChild(divtag);
			document.getElementById(ch + "KRACount0"+goalCnt+"").value = KRACount;
		}
	}
		
	 function validateKRAScore(value, weightageId, ch, count, goalCnt) {

			var KRACount = document.getElementById(ch + "KRACount0" +goalCnt).value;
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
			} else if(parseFloat(value) <= 0 ) {
				alert("Invalid Weightage");
				document.getElementById(weightageId).value = remainweight;
			}
		}
	
	 function removeKRAID(id, KRACountID,goalCnt) {
			var cKRACount = document.getElementById("cKRACount0"+goalCnt).value;
			var row_skill = document.getElementById(id);
				if (row_skill && row_skill.parentNode && row_skill.parentNode.removeChild) {
					row_skill.parentNode.removeChild(row_skill);
					if(cKRACount > 0) {
						cKRACount--;	
					}
					document.getElementById("cKRACount0"+goalCnt).value = cKRACount;
				}
				
				//alert("KRACountID ===>> " + KRACountID);
				if(parseInt(KRACountID) > 0) {
					var cDeleteKRAIds = document.getElementById("cDeleteKRAIds").value;
					if(cDeleteKRAIds.length == 0) {
						cDeleteKRAIds = KRACountID;
					} else {
						cDeleteKRAIds = cDeleteKRAIds+","+KRACountID;
					}
					//alert("cDeleteKRAIds ===>> " + cDeleteKRAIds);
					document.getElementById("cDeleteKRAIds").value = cDeleteKRAIds;
				}
			}  
	
	 function addKRATask(ch, count, kraCnt, goalCnt) {
			//alert("ch ===>> " + ch + " -- count ===>> " + count + " -- kraCnt ===>> " + kraCnt + " -- goalCnt ===>> " + goalCnt);
			var taskcount = document.getElementById("KRATaskCount_"+kraCnt+goalCnt).value;
			taskcount++;
			
			var divid = ch+"KRATaskDIV"+count+"_"+kraCnt+"_"+taskcount+goalCnt;
		
			var divtag = document.createElement('div');
			divtag.setAttribute("style", "width: 100%; float: left; margin-bottom: 5px;");
			divtag.id = divid;
			//alert("divid ===>> " + divid);
			var data = "<input type=\"hidden\" name=\"hideKRATaskId_"+kraCnt+goalCnt+"\" id=\"hideKRATaskId_"+kraCnt+goalCnt+"\" value=\"0\"/>"
			+"<input type=\"text\" name=\""+ch+"KRATask_"+kraCnt+goalCnt+"\" id=\""+ch+"KRATask_"+kraCnt+goalCnt+"\"  class=\"validateRequired   form-control \"/> "
				+"<a href=\"javascript:void(0)\" onclick=\"addKRATask('"+ch+"', '"+count+"', '"+kraCnt+"', '"+goalCnt+"');\"><i class=\"fa fa-plus-circle\"></i>Add Task</a>&nbsp;"
				+"<a href=\"javascript:void(0)\" style=\"color: red\" onclick=\"removeKRATaskID('" + divid + "','','" + kraCnt + "','" + goalCnt + "');\">"
				/* +"<img border=\"0\" style=\"height: 16px; width: 16px;\" src=\""+cxtpath+"/images1/icons/icons/close_button_icon.png\"/>&nbsp;Remove Task</a>"; */
				+"<i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"></i>&nbsp;Remove Task</a>";
				
				//alert("data ====>>> " + data);
			divtag.innerHTML = data;
			document.getElementById(ch + "KRATaskdiv"+count+"_"+kraCnt+goalCnt).appendChild(divtag);
			document.getElementById("KRATaskCount_"+kraCnt+goalCnt).value = taskcount;
		}
	
	 function removeKRATaskID(id,deletKRATaskID,kraCount,goalCnt) {
			//	alert("kraCnt==>"+kraCount+"==>goalCnt==>"+goalCnt+"==>deletKRATaskID==>"+deletKRATaskID);
				var taskCount = document.getElementById("KRATaskCount_"+kraCount+goalCnt).value;
				var row_skill = document.getElementById(id);
				if (row_skill && row_skill.parentNode && row_skill.parentNode.removeChild) {
					row_skill.parentNode.removeChild(row_skill);
					if(parseInt(taskCount) > 0) {
						taskCount--;
					}
				}
				
				document.getElementById("KRATaskCount_"+kraCount+goalCnt).value = taskCount;
				
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
	
		
	function removeGoal(divId) {
		var row_skill = document.getElementById(divId);
		var goalCnt = document.getElementById("goalCnt").value;
		if (row_skill && row_skill.parentNode && row_skill.parentNode.removeChild) {
			row_skill.parentNode.removeChild(row_skill);
			if(parseInt(goalCnt)>0) {
				goalCnt--;
			}
		}
		document.getElementById("goalCnt").value = goalCnt ;
	}
		
	function addNewGoal(isFrequency, isAttribute) {
		var strElement = '<%=(String)request.getAttribute("elementOptionsAjax") %>';
		var strOrientation = '<%=(String)request.getAttribute("orientation") %>';
		var strFrequency = '<%=(String)request.getAttribute("frequencyOption") %>';
		var strDates = '<%=(String)request.getAttribute("datesOption") %>';
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
				divtag.setAttribute("style", "width: 100%; float: left; margin-top: 10px;");
				divtag.id = divid;
				//alert("divid ===>> " + divid); "+typeas+"
				var data = "<table class=\"table table_no_border\" style=\"width: 100%;\"><tr><th nowrap align=\"right\" width=\"20%\">Goal:<sup>*</sup></th>"
					+"<td  colspan=\"3\"><input type=\"hidden\" name=\"goalId_"+goalCnt+"\" id=\"goalId_"+goalCnt+"\" value=\"0\"/><input type=\"text\" name=\"corporateGoal_"+goalCnt+"\" id=\"corporateGoal_"+goalCnt+"\" class=\"validateRequired   form-control \" style=\"width: 600px;\"/>"
					+"<a href=\"javascript:void(0)\" style=\"float:right; color: red;\" onclick=\"removeGoal('" + divid + "');\">"
					/* +"<img border=\"0\" style=\"height: 16px; width: 16px;\" src=\""+cxtpath+"/images1/icons/icons/close_button_icon.png\"/>&nbsp;</a>" */
					+"<i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"></i>&nbsp;</a>"
					
					+"</td></tr>"
					+"<tr><th nowrap align=\"right\">Objective:</th><td colspan=\"3\"><input type=\"text\" name=\"cgoalObjective_"+goalCnt+"\"  /></td></tr>"
					+"<tr><th align=\"right\" valign=\"top\">Description:</th><td colspan=\"3\"><textarea rows=\"3\" cols=\"72\" name=\"cgoalDescription_"+goalCnt+"\" class=\"form-control \"></textarea></td></tr>"
					+"<tr><th nowrap align=\"right\">Priority:<sup>*</sup></th><td colspan=\"3\"><select name=\"priority_"+goalCnt+"\" id=\"priority_"+goalCnt+"\" class=\"validateRequired   form-control \">"
					+"<option value=''>Select</option><option value=\"1\">High</option><option value=\"2\">Medium</option><option value=\"3\">Low</option></select></td></tr>";
			   
					if(isAttribute == 1) {
						data = data	+"<tr><th align=\"right\">Align an Attribute:<sup>*</sup></th><td colspan=\"3\"><span style=\"float: left; margin-right: 10px;\">"
						+"<select name=\"goalElements_"+goalCnt+"\" id=\"goalElements_"+goalCnt+"\" class=\"validateRequired   form-control \" style=\"width: 130px;\" onchange=\"getAttributes(this.value, '_"+goalCnt+"');\">"
						+"<option value=''>Select</option>"+ strElement +"</select></span>"
						+"<span id=\"attributeDiv_"+goalCnt+"\" style=\"float: left;\"><select name=\"cgoalAlignAttribute_"+goalCnt+"\" id=\"cgoalAlignAttribute_"+goalCnt+"\" class=\"validateRequired   form-control \"><option value=''>Select</option></select></span>"
						+"</td></tr>";
			       }
					
			     	data = data+"<tr id=\"ckraID0_"+goalCnt+"\" style=\"display: table-row;\"><th valign=\"top\" align=\"right\">KRA:</th><td id=\"cKRAtdID0_"+goalCnt+"\" colspan=\"3\">"
					+"<input type=\"hidden\" name=\"cKRACount_"+goalCnt+"\" id=\"cKRACount0_"+goalCnt+"\" value=\"0\" /><input type=\"hidden\" name=\"cAddMKra_"+goalCnt+"\" id=\"cAddMKra0_"+goalCnt+"\" value=\"KRA\"/>"
					+"<div id=\"cKRAdiv0_0_"+goalCnt+"\" style=\"width: 100%; float: left; margin-bottom: 3px; border-bottom: 1px solid #F3F3F3;\">"
						+"<div style=\"width: 100%; float: left; margin-bottom: 3px;\">"
							+"<input type=\"hidden\" name=\"KRATaskCount_0_"+goalCnt+"\" id=\"KRATaskCount_0_"+goalCnt+"\" value=\"0\">"
							+"<input type=\"text\" name=\"cKRA_0_"+goalCnt+"\" id=\"cMainKRA_"+goalCnt+"\"  class=\"validateRequired form-control\"/>"
							+" Weightage (%): <input type=\"text\" name=\"cKRAWeightage_0_"+goalCnt+"\" id=\"cKRAWeightage_0_"+goalCnt+"\" class=\"validateRequired\" value=\"100\" style=\"width: 40px !important;\" onkeyup=\"validateKRAScore(this.value,'cKRAWeightage_0_"+goalCnt+"', 'c', 0, '_"+goalCnt+"');\" onkeypress=\"return isNumberKey(event)\" />"
							+"<a href=\"javascript:void(0)\" onclick=\"addKRA('c',0, '_"+goalCnt+"');\"><i class=\"fa fa-plus-circle\"></i>Add KRA</a>"
						+"</div>"
						+"<div style=\"width: 100%; float: left; margin-bottom: 3px;\">"
							+"<span style=\"float: left; margin-left: 7px;\"><a href=\"javascript:void(0)\" onclick=\"addKRATask('c', 0, 0, '_"+goalCnt+"');\"><i class=\"fa fa-plus-circle\"></i>Add Task</a></span>"
							//+"<span style=\"float: left; margin-left: 27px;\"><a href=\"javascript:void(0)\" class=\"add_lvl\" style=\"color: #68AC3B;\" onclick=\"selectKRATask('c', 0, 0, '_"+goalCnt+"');\">Select Task</a></span>"
						+"</div>"
						+"<div id=\"cKRATaskdiv0_0_"+goalCnt+"\" style=\"float: left; margin-left: 50px; margin-bottom: 3px;\"></div>"
					+"</div></td></tr>"
					
					//updated by kalpana on 18/10/2016 start
					+"<tr><th align=\"right\">Effective Date:<sup>*</sup></th><td colspan=\"3\"><input type=\"text\" name=\"cgoalEffectDate_"+goalCnt+"\" id=\"cgoalEffectDate_"+goalCnt+"\"  class=\"duedatepick validateRequired  marginright10\" />"
					+"Due Date:<sup>*</sup><span class=\"marginright10\"></span><input type=\"text\" name=\"cgoalDueDate_"+goalCnt+"\" id=\"cgoalDueDate_"+goalCnt+"\"  class=\"duedatepick validateRequired  marginright10\" /></td></tr>"
					//end
					+"<tr><th align=\"right\">Orientation:</th><td><select name=\"corientation_"+goalCnt+"\">"+ strOrientation +"</select></td></tr>";
					
					if(isFrequency == 1) {
						
					 data = data +"<tr><th align=\"right\">Select Frequency:</th><td colspan=\"5\">"
					+"<div style=\"position:relative;\">"
					+"<span style=\"float: left; margin-right: 20px;\">"
					+"<select name=\"frequency_"+goalCnt+"\" onchange=\"checkFrequency(this.value, '_"+goalCnt+"')\">"+ strFrequency +"</select>"
					+"</span>"
					+"<span id=\"weekly_"+goalCnt+"\" style=\"display: none; float: left;\"><select name=\"weekday_"+goalCnt+"\" id=\"weekday_"+goalCnt+"\"  class=\"validateRequired   form-control \" style=\"width:100px;\"> <option value=\"\">Select Day</option> <option value=\"Monday\">Monday</option> <option value=\"Tuesday\">Tuesday</option> <option value=\"Wednesday\">Wednesday</option> <option value=\"Thursday\">Thursday</option> <option value=\"Friday\">Friday</option> <option value=\"Saturday\">Saturday</option> <option value=\"Sunday\">Sunday</option></select></span>"
					+"<span id=\"monthly_"+goalCnt+"\" style=\"display: none; float: left;\"><select name=\"day_"+goalCnt+"\" id=\"day_"+goalCnt+"\"  class=\"validateRequired   form-control \" style=\"width:110px;\">"
					+"<option value=\"\">Select Date</option>"+ strDates +"</select></span>"
					+"</div>"+
					"</td></tr>";
					}
					
					data = data+"<tr><th align=\"right\">Weightage (%):<sup>*</sup></th><td colspan=\"3\"><input type=\"text\" name=\"cgoalWeightage_"+goalCnt+"\" id=\"cgoalWeightage_"+goalCnt+"\" class=\"validateRequired\" value=\""+remainweight+"\" onkeyup=\"validateScore(this.value,'cgoalWeightage_"+goalCnt+"');\"/></td></tr>"
						   +"</table>"; 
					
				divtag.innerHTML = data ;
				
				document.getElementById("newGoalDiv").appendChild(divtag);
				
				document.getElementById("goalCnt").value = goalCnt;
				
				//updated by kalpana on 18/10/2016..............start
				
				/* var strFromDate = document.getElementById("cgoalEffectDate_"+goalCnt+"").value;
				var strToDate = document.getElementById("cgoalDueDate_"+goalCnt+"").value; */
				//.......end............
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
			}
	}	
	
	function getMeasureWith(value, ch, count) {
		
		if (value == 'Value'|| value == 'Amount' || value == 'Percentage') {
			if (value == 'Amount') {
				document.getElementById("percentSpan" + count).style.display = "none";
				document.getElementById("rsSpan" + count).style.display = "block";
				} else {
					document.getElementById("percentSpan" + count).style.display = "block";
					document.getElementById("rsSpan" + count).style.display = "none";
				}
			// alert(value);
			document.getElementById(ch + "dollarAmtid" + count).style.display = "table-row";
			document.getElementById(ch + "measureEffortsid" + count).style.display = "none";
			
		} else if (value == 'Effort') {
			//alert(value);
			document.getElementById(ch + "dollarAmtid" + count).style.display = "none";
			document.getElementById(ch + "measureEffortsid" + count).style.display = "table-row";
			
		} else {
			//alert(value);
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
			document.getElementById("cmeasureEffortsid0").style.display = "none";
			
		} else {
			document.getElementById("cAddMKra0").value = "";
			document.getElementById("cmeasureEffortsid0").style.display = "none";
			document.getElementById("measureWith").style.display = "none";
			document.getElementById("cdollarAmtid0").style.display = "none";
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
							
		} else if (value == '6') {
			document.getElementById("weekly"+id).style.display = "none";
			document.getElementById("monthly"+id).style.display = "none";
						
			document.getElementById("weekday"+id).selectedIndex = 0;
			document.getElementById("day"+id).selectedIndex = 0;
			
						
		} else if (value == '4' || value == '5') {
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
		//alert("value ===>> " + value);
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
		//document.getElementById("gradeIdV").selectedIndex = 0;
		//document.getElementById("employee").selectedIndex = 0;

		var supervisorId = document.getElementById("supervisorId").value;
		var goaltype = document.getElementById("goaltype").value;
		if (goaltype == '3' || goaltype == '4') {
			// || goaltype=='4'
			action += '&supervisor=' + supervisorId;
		}
		getContent('myEmployee', action);
		getWLocDepartLevelDesigByOrg(strID,'org');
		//searchTextField();
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
	    
		var action = 'getGoalEmployeeList.action?strID='+ strID+'&location='+ location + '&selectedEmp=' + selectedEmp;

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
		getContent('myEmployee', action);
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
		var location = getSelectedValue("wlocation");
		var depart = getSelectedValue("depart");
		var Level = getSelectedValue("strLevel");
		
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

		var action = 'getGoalEmployeeList.action?level=' + Level + '&selectedEmp=' + selectedEmp;
		
		var supervisorId = document.getElementById("supervisorId").value;
		var goaltype = document.getElementById("goaltype").value;
		if (goaltype == '3' || goaltype == '4') {
			// || goaltype=='4'
			action += '&supervisor=' + supervisorId;
		}
		
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
			window.setTimeout(function() {
				//getContent('myDesig', 'getDesignation.action?strLevel=' + Level);
				getContent('myDesig', 'GetDesignationByLevel.action?strLevel=' + Level+'&strOrg='+ strID); 
			}, 200);
		}
		//searchTextField();
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
	    
		//document.getElementById("gradeIdV").selectedIndex = 0;
		//document.getElementById("employee").selectedIndex = 0;
		
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
			getContent('myEmployee', action);
			/* window.setTimeout(function() {
				getContent('myGrade', 'getGrade.action?strDesignation=' + design);
			}, 200); */
		}
		//searchTextField();
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
		
		var empselect=document.getElementById("empselected").value;
		//alert("empselected "+document.getElementById("empselected").value);
		//alert(empselect);
		var action='GetSelectedEmployee.action?type=all&chboxStatus='+status+'&selectedEmp='+selectID+'&existemp='+empselect;
		getContent('idEmployeeInfo',action); 
	}
	
	function getGoalSelectedEmp(checked, emp, form, isInIndiGoal) {
		var empselect = document.getElementById("empselected").value;
		//updated by kalpana on 22 oct 2016....start...
		if(empselect != '' && empselect !='0') {
			if(document.getElementById("cmeasureEffortsHrs")) {
				document.getElementById("cmeasureEffortsHrs").value = '';
			}
		}
		//...end...

		if(checked == true) {
			alrtMsg = "Are you sure, you want to add this employee?";
		} else {
			alrtMsg = "Are you sure, you want to remove this employee?";
		}
		if(confirm(alrtMsg)) {	
			var empselect=document.getElementById("empselected").value;
			var action='GetSelectedEmployee.action?type=all&chboxStatus='+checked+'&selectedEmp='+emp+'&existemp='+empselect+'&form='+form;
			getContent('idEmployeeInfo',action);
			
			if(checked == false) {
             	document.getElementById("strGoalEmpId"+emp).checked = false;
             }
		} else {
			if(checked == true) {
				document.getElementById('strGoalEmpId'+emp).checked = false;
			}
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
		if($(window).width() >= 1100){
			$(".proDialog").width(1100);
		}
		$.ajax({
			//url : "ApplyLeavePopUp.action",  
			url :"MyProfile.action?empId="+empId+"&proPopup=proPopup" ,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		}); */
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
	
	
	function showTeamGoalsOnload(){
		var value = document.getElementById("goalalignYesno").value;
		if(value == 'Yes'){
			document.getElementById("teamgoalDiv").style.display = "block";
		} else {
			document.getElementById("teamgoalDiv").style.display = "none";
		}
	}

	function showPerspective(value) {
		if(value == 'Yes') {
			document.getElementById("perspectiveDiv").style.display = "inline";
		} else {
			document.getElementById("perspectiveDiv").style.display = "none";
		}
	}
	
	function showTeamGoals(value){
		if(value == 'Yes'){
			document.getElementById("teamgoalDiv").style.display = "block";
		} else {
			document.getElementById("teamgoalDiv").style.display = "none";
		}
	}

	function getAttributes(value, goalCnt) {
		 var strID = null;
		    if(document.getElementById("hideOrgid")){
		    	strID = document.getElementById("hideOrgid").value;
		    }
		var action = 'GetAttributeList.action?elementID=' + value + '&orgId=' + strID+'&goalCnt='+goalCnt+'&type=MULTIKRA';
		getContent('attributeDiv'+goalCnt, action);
	}

	
	function checkHrsLimit(){
		var empSelected = '';
		var from = document.getElementById("from").value;
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

	
	function checkFields() {
	      
		if(document.getElementById("empselected")) {
			var empselected = document.getElementById("empselected").value;
			if(empselected == '' || empselected =='0'){
				alert("Please, select the employee.");
				return false;
			}
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
	
/* ===start parvez date: 03-02-2021=== */
	function editPersonalGoal(){
	
		var form_data = $("#frmEditGoal").serialize();
		$("#edit_goal").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		xmlhttp = GetXmlHttpObject();
        if (xmlhttp == null) {
                alert("Browser does not support HTTP Request");
                return;
        } else{
        	var xhr = $.ajax({ 
    			type : 'POST',
    			url: "EditMyPersonalGoal.action",
    			data: form_data+"&submit=Save&fromPage=review",
    			success: function(data){
    				if(data != "" && data.trim().length > 0){
                    	document.getElementById("edit_goal").innerHTML = data;
                   	}
    	   		},
    	   		error: function(data){
    	   			if(data != "" && data.trim().length > 0){
                    	document.getElementById("edit_goal").innerHTML = data;
                   	}
    	   		}
    		});
        }
		
		
		$(".modal-dialog1").removeAttr('style');
	  	$("#modal-body1").height(400);
	    $("#modalInfo1").hide();
	
	}

/* ===end parvez date: 03-01-2022=== */
	
</script>



<%
	UtilityFunctions uF = new UtilityFunctions();
	CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
	String typeas = (String)request.getAttribute("typeas");
	//System.out.println("EMPG.jsp/1112--typeas="+typeas);
	String proPage = (String)request.getParameter("proPage");
	String minLimit = (String)request.getParameter("minLimit");
	String empId = (String)request.getParameter("empId");
	System.out.println("empId ===>> " + empId);
	String goalTitle = (String)request.getParameter("goalTitle");
	String strUserType = (String) session.getAttribute(IConstants.USERTYPE);
	String from = (String)request.getAttribute("from");
	String currUserType = (String)request.getAttribute("currUserType");
	//List<String> innerList = (List<String>) request.getAttribute("innerList");
	Map<String, List<String>> hmFirstGoal = (Map<String, List<String>>) request.getAttribute("hmFirstGoal");
	Map<String, List<String>> hmOtherGoals = (Map<String, List<String>>) request.getAttribute("hmOtherGoals");
	
	String supervisorId = (String) request.getAttribute("supervisorId");

	Map<String, String> hmGoalType = (Map<String, String>) request.getAttribute("hmGoalType");
	String goaltype = request.getParameter("goaltype");
	//System.out.println("goaltype : "+goaltype);
	String goalTypePG = (String)request.getAttribute("goalTypePG"); /*Created By Dattatray Date :13-09-21 Note: Added goalTypePG  */
	//System.out.println("goalTypePG Edit : "+goalTypePG);
	Map<String, List<List<String>>> hmKRA = (Map<String, List<List<String>>>) request.getAttribute("hmKRA");
	Map<String, List<List<String>>> hmKRATasks = (Map<String, List<List<String>>>) request.getAttribute("hmKRATasks");
	
	List<FillEmployee> empList = (List<FillEmployee>) request.getAttribute("empList");
	String currDate = uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()),IConstants.DBDATE,IConstants.DATE_FORMAT);
	
	Map<String, String> hmCheckEmpList = (Map<String, String>) request.getAttribute("hmCheckEmpList");
	if(hmCheckEmpList==null) hmCheckEmpList=new HashMap<String, String>();
	
	Boolean checkTarget = (Boolean) request.getAttribute("checkTarget");
	Boolean checkClose = (Boolean) request.getAttribute("checkClose");
	/* Boolean processOrNotFlag = (Boolean) request.getAttribute("processOrNotFlag"); */
	
	Map<String, String> hmFeatureStatus = (Map<String, String>) request.getAttribute("hmFeatureStatus");
	Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>)request.getAttribute("hmFeatureUserTypeId");
	String strUsertypeId = (String)session.getAttribute(IConstants.BASEUSERTYPEID);
	
	if(hmFeatureStatus == null)  hmFeatureStatus = new HashMap<String,String>();
	
	int isAttribute = 0;
	int isFrequency = 0;
	if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_GOAL_FREQUENCY)) && hmFeatureUserTypeId.get(IConstants.F_GOAL_FREQUENCY).contains(strUsertypeId)) {
		isFrequency = 1;
	}
	
	if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_GOAL_ATTRIBUTE_ALIGN)) && hmFeatureUserTypeId.get(IConstants.F_GOAL_ATTRIBUTE_ALIGN).contains(strUsertypeId)) {
		isAttribute = 1;
	}
	
	//===start parvez date: 04-09-2021===
	Map<String, String> hmMemberOption = (Map<String, String>) request.getAttribute("hmMemberOption");
	String policy_id = (String) request.getAttribute("policy_id");
	//===end parvez date: 04-09-2021===
	//EncryptionUtils EU = new EncryptionUtils();//Created by Dattatray Date 21-07-21 Note: encryption

//===start parvez date: 29-12-2021===	
	String dataType = (String) request.getParameter("dataType");
	String fromPage1 = (String) request.getParameter("fromPage");
//===end parvez date: 29-12-2021===	
%>

<!-- ===start parvez date: 31-12-2021=== -->
<% if(dataType!= null && dataType.equalsIgnoreCase("NewGoal") && fromPage1 != null && fromPage1.contains("review")){ %>
<div style="width:50%;">
	<%=request.getParameter("goalTitle") %>
</div>
<!-- ===end parvez date: 31-12-2021=== -->
<% } %>
<div class="leftbox reportWidth" style="font-size: 12px;"> 
	<%
		if (hmFirstGoal != null) {
			Iterator<String> it = hmFirstGoal.keySet().iterator();
			while(it.hasNext()) {
				String fGoalId = it.next();
				List<String> innerList = hmFirstGoal.get(fGoalId);
				Boolean processOrNotFlag = (Boolean) request.getAttribute(fGoalId+"_processOrNotFlag");
	%>
	<s:form id="frmEditGoal" name="frmEditGoal" theme="simple" action="EditMyPersonalGoal" method="POST" cssClass="formcss" >
		<input type="hidden" name="goal_id" value="<%=innerList.get(0)%>"/> 
		<input type="hidden" name="goaltype" id="goaltype" value="<%=innerList.get(1)%>"/> 
		<input type="hidden" name="goal_parent_id" value="<%=innerList.get(2)%>"/>
		<input type="hidden" name="supervisorId" id="supervisorId" value="<%=supervisorId%>"/>
		<input type="hidden" name="typeas" id="typeas" value="<%=typeas%>"/>
		<input type="hidden" name="proPage" id="proPage" value="<%=proPage%>"/>
		<input type="hidden" name="minLimit" id="minLimit" value="<%=minLimit%>"/>
		<input type="hidden" name="empId" id="empId" value="<%=empId%>"/>
		<input type="hidden" id="currDate" name="currDate" value="<%=currDate%>"/>
		<input type="hidden" id="from" name="from" value="<%=from%>"/>
	<!-- ===start parvez date: 29-12-2021=== -->
		<input type="hidden" id="dataType" name="dataType" value="<%=dataType%>"/>
	<!-- ===end parvez date: 29-12-2021=== -->	
		<input type="hidden" id="currUserType" name="currUserType" value="<%=currUserType%>"/>
		<input type="hidden" name="measureKravalue" id = "measureKravalue" value="<%=(String)request.getAttribute("measureKravalue") %>"/>
		<s:hidden name="type"></s:hidden>
		<s:hidden name="goalTitle"></s:hidden>
		<s:hidden name="superId"></s:hidden>
		<s:hidden name="fromPage"></s:hidden>
		<table class="table table_no_border">
			
			<% 
			System.out.println("EMPG.jsp/1196--typeas="+typeas);
			if(typeas !=null && !typeas.equals("goal")) { 
			%>
		
			<tr>
				<td colspan="5">
				<% if(strUserType != null && (strUserType.equals(IConstants.HRMANAGER) || strUserType.equals(IConstants.ADMIN)) && !processOrNotFlag) { %>
					<div class="filter_div" style="float: left; width: 97.7%;">
						<div style="float: left; padding-left: 10px;"><strong>Filter:</strong> </div>
				 		 <div id="wlocationDiv" style="float: left; padding-left: 10px;">
                        	<s:select theme="simple" cssClass=" form-control " name="strWlocation" list="workList" id="wlocation" listKey="wLocationId" listValue="wLocationName" headerKey=""
                            	headerValue="All WorkLocation" required="true" value="{userlocation}" onchange="getEmployeebyLocation();" cssStyle="width:150px;"></s:select>
                      	</div>
                      	<div id="departDiv" style="float: left; padding-left: 10px;">
                         <s:select theme="simple" cssClass=" form-control " name="strDepart" list="departmentList" id="depart" listKey="deptId" listValue="deptName" headerKey=""
                             headerValue="All Department" required="true" onchange="getEmployeebyDepart();" cssStyle="width:150px;"></s:select>
						</div>
						<div id="levelDiv" style="float: left; padding-left: 10px;">
                         <s:select theme="simple" cssClass=" form-control " name="strLevel" list="levelList" listKey="levelId" id="strLevel" listValue="levelCodeName" headerKey=""
                             headerValue="All Level" required="true" onchange="getEmployeebyLevel()" cssStyle="width:150px;"></s:select>
						</div>
                     	<div id="myDesig" style="float: left; padding-left: 10px;">
                             <s:select theme="simple" cssClass=" form-control " name="strDesignationUpdate" list="desigList" listKey="desigId" id="desigIdV" listValue="desigCodeName"
                                 headerKey="" headerValue="All Designation" onchange="getEmployeebyDesig();" cssStyle="width:150px;"></s:select>
                        </div>
					</div>
				<% } %>
				<div id="myEmployee" style="float: left; width: 60%; overflow-y: auto; height: 300px; padding:10px;margin-top: 10px; margin-left: 4px; border: 2px solid #F3F3F3;">
					<table id="lt" class="table table-bordered" style="width: 100%">
						<%
							if (empList != null && !empList.equals("") && !empList.isEmpty()) {
								Map<String, String> hmEmpLocation = (Map<String, String>)request.getAttribute("hmEmpLocation");
								Map<String, String> hmWLocation = (Map<String, String>)request.getAttribute("hmWLocation");
								Map<String, String> hmEmpCodeDesig = (Map<String, String>)request.getAttribute("hmEmpCodeDesig");
						%>
						<thead>
							<tr>
								<th width="10%"><input onclick="checkUncheckValue();" type="checkbox" name="allEmp" id="allEmp" <%if (processOrNotFlag) {%>disabled="disabled"<% } %>>
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
								String location = (emplocationID == null || emplocationID.equals("")) ? "" : hmWLocation.get(emplocationID);

								String desig = (empID == null || empID.equals("")) ? "" : hmEmpCodeDesig.get(empID);
						%>
						
						<tr>
							<td><input type="checkbox" name="strGoalEmpId" id="strGoalEmpId<%=empID%>" onclick="getGoalSelectedEmp(this.checked, this.value, 'frmKRA', '');"
								value="<%=empID%>" <%if (hmCheckEmpList.get(empID) != null) {%>
								checked="checked" <%}%> <%if (processOrNotFlag) {%>disabled="disabled"<% } %>>
							</td>
							<!-- Created by  Dattatray Date : 21-07-21 Note:empId encryption  -->
							<td><a href="javascript: void(0);" onclick="openPanelEmpProfilePopup('<%=empID %>')"><%=empName%></a></td>
							<td><%=desig%></td>
							<td><%=location%></td>
						</tr>
						<%
							}
								} else {
						%>
						<tr>
							<td colspan="3"><div class="nodata msg" style="width: 88%">
									<span>No Employee Found</span>
								</div></td>
						</tr>
						<%
							}
						%>
						</tbody>
					</table>
					</div>
				
				
				<% String empids=(String)request.getAttribute("empids"); %>
					
					<div id="idEmployeeInfo" style="float: left; left: 70%; top: 46px; width: 350px; padding: 5px; overflow-y: auto; border: 2px solid #F3F3F3; margin-left: 20px; margin-top: 10px; height: 300px;">
					<%
					List<List<String>> selectEmpList = (List<List<String>>) request.getAttribute("selectEmpList");
					if (selectEmpList != null && !selectEmpList.isEmpty() && selectEmpList.size() > 0) {
					%>
					<div style="border: 2px solid #ccc;">
						<div style="padding: 8px 20px; border: 2px solid lightgray;"><b>Employee</b></div>
							<% for (int i = 0; i < selectEmpList.size(); i++) { 
								List<String> innerList1 = (List<String>) selectEmpList.get(i);
							%>
								<div style="float: left; width: 100%; margin: 5px;"><strong><%=i + 1%>.</strong>&nbsp;&nbsp;<%=innerList1.get(1)%>
								<%if (!processOrNotFlag) { %>
									<a href="javascript: void(0)" onclick="getGoalSelectedEmp(false,'<%=innerList1.get(0)%>','frmKRA', '');">
									<img border="0" style="width: 12px; height: 12px;" src="<%=request.getContextPath()%>/images1/arrow_reset1.png"/></a>
								<% } %>	
								</div>
							<% } %>
					</div>
					<% } else { %>
					<div class="nodata msg" style="width: 85%">
						<span>No Employee selected</span>
					</div>
					<% } %>
					<input type="hidden" name="empselected" id="empselected" value="<%=empids!=null && !empids.equals("") ? empids :"0" %>"/>
				</div>
				
				</td>
			</tr>
			<%} %>
		
			<tr>
				<th nowrap align="right" width="20%">Goal:<sup>*</sup></th>
				<td colspan="3">
					<input type="hidden" name="hideOrgid" id="hideOrgid" value="<%=request.getAttribute("strID") %>">
			<!-- ===start parvez date: 29-12-2021=== -->		
					<input type="text" id="corporateGoal" name="corporateGoal" class="validateRequired    form-control " style="width: 600px;" value="<%=innerList.get(3)%>" />
			<!-- ===end parvez date: 29-12-2021=== -->	
				</td>
			</tr>
			<tr>
				<th nowrap align="right">Objective:</th>
				<td colspan="3"><input type="text" name="cgoalObjective" class=" form-control " style="width: 600px;" value="<%=innerList.get(4)%>" /></td>
			</tr>
			<tr>
				<th align="right" valign="top">Description:</th>
				<td colspan="3"><textarea rows="3" cols="72" name="cgoalDescription" class="form-control "><%=innerList.get(5)%></textarea></td>
			</tr>
			
			<tr>
				<th nowrap align="right">Priority:<sup>*</sup></th>
				<td colspan="3">
						<s:select theme="simple" name="priority" headerKey="" cssClass="validateRequired form-control" headerValue="Select" 
							list="#{'1':'High', '2':'Medium', '3':'Low'}"/> 
				</td>
			</tr>
	<!-- ===start parvez date: 10-12-2021=== -->		
			<%-- <% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_GOAL_ATTRIBUTE_ALIGN)) && hmFeatureUserTypeId.get(IConstants.F_GOAL_ATTRIBUTE_ALIGN).contains(strUsertypeId)) { %> --%>
			<% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_GOAL_ATTRIBUTE_ALIGN)) && hmFeatureUserTypeId.get(IConstants.F_GOAL_ATTRIBUTE_ALIGN).contains(strUsertypeId) 
					&& !uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_EDIT_PERSONAL_GOAL_BY_MANAGER)) && !uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_APPROVE_DENY_PERSONAL_GOAL_BY_MANAGER))) { %>
	<!-- ===end parvez date: 10-12-2021=== -->			
				
				<tr>
					<th align="right">Align an Attribute:<sup>*</sup></th>
					<td colspan="3">
						<span style="float: left; margin-right: 10px;">
						<!-- Created by Dattatray Date:08-09-21 -->
						<%// Created By Dattatray date:13-09-21 Note : PERSONAL_GOAL Condition checked
						if(uF.parseToInt(goalTypePG) == IConstants.PERSONAL_GOAL){
							if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_INSTANCE_NAMEWISE_FEATURE)) && hmFeatureUserTypeId.get(IConstants.F_INSTANCE_NAMEWISE_FEATURE+"_USER_IDS")!=null && hmFeatureUserTypeId.get(IConstants.F_INSTANCE_NAMEWISE_FEATURE+"_USER_IDS").contains(IConstants.QULOI)) {
						%>
							<input type="hidden" name="goalElements" value="<%=request.getAttribute("strAttributeId") %>" />
							<input type="hidden" name="cgoalAlignAttribute" value="<%=request.getAttribute("strAttributeElementId") %>" />
							<span><%=request.getAttribute("strAttributeName") %></span>
							<span> - </span>
							<span><%=request.getAttribute("strAttributeElementName") %></span>
						<% }else{ %>
							<select name="goalElements" id="goalElements" class="validateRequired " onchange="getAttributes(this.value, '');">
							<option value="">Select</option>
								<%=innerList.get(31) %>
							</select>
							</span>
								<span id="attributeDiv" style="float: left;">
							<select name="cgoalAlignAttribute" id="cgoalAlignAttribute" class="validateRequired">
								<option value="">Select</option>
									<%=innerList.get(32) %>
							</select>
						</span>
						<% } %>
						<% }else{ %>
							<select name="goalElements" id="goalElements" class="validateRequired " onchange="getAttributes(this.value, '');">
							<option value="">Select</option>
								<%=innerList.get(31) %>
							</select>
							</span>
								<span id="attributeDiv" style="float: left;">
							<select name="cgoalAlignAttribute" id="cgoalAlignAttribute" class="validateRequired">
								<option value="">Select</option>
									<%=innerList.get(32) %>
							</select>
						</span>
						<% } %>	
						
							
					
					</td>
				</tr>
			<% } %>
			
			<% String iskra="none";
			String ismeasure="none";
			String validReqKra = "form-control ";
			
			//System.out.println("addMKravalue ===>> " + (String)request.getAttribute("addMKravalue"));
			if((String)request.getAttribute("addMKravalue")!=null && ((String)request.getAttribute("addMKravalue")).equals("KRA")) {
				iskra="table-row";
				ismeasure="none";
				validReqKra = "validateRequired form-control ";
				
			}else if((String)request.getAttribute("addMKravalue")!=null && ((String)request.getAttribute("addMKravalue")).equals("Measure")) {
				iskra="none";
				ismeasure="table-row";
				validReqKra = " form-control ";
				
			}
			//System.out.println("ismeasure ===>> " + ismeasure);
			%>
			
			<%if(typeas != null && typeas.trim().equals("KRA")) { %>
			<tr id="ckraID0" style="display: <%=iskra%>;">
			<th valign="top" align="right">Initiative</th>
			<td id="cKRAtdID0_0" colspan="3">
			<% 
			List<List<String>> goalKraList = hmKRA.get(innerList.get(0));
			String kracount="0";
			
			if(goalKraList!=null) {
				kracount=""+goalKraList.size();
				
			 for(int j=0; !goalKraList.isEmpty() && j<goalKraList.size(); j++) {
				List<String> goalkraInnerList=goalKraList.get(j);
				List<List<String>> kraTaskList = hmKRATasks.get(goalkraInnerList.get(0));
				
			%>
			<%--updated by kalpana on 15/10/2016 by kalpana added validations and changes start --%>
			<div id="cKRAdiv0_<%=j %>_0" style="width: 100%; float: left; margin-bottom: 3px; border-bottom: 1px solid #F3F3F3;">
				<div style="width: 100%; float: left; margin-bottom: 3px;">
					<input type="hidden" name="KRATaskCount_<%=j %>_0" id="KRATaskCount_<%=j %>_0" value="<%=(kraTaskList != null && !kraTaskList.isEmpty()) ? kraTaskList.size() : "0" %>">
					<input type="hidden" name="hideKRAId_<%=j %>_0" id="hideKRAId_<%=j %>_0" value="<%=goalkraInnerList.get(0) %>"/>
					<input type="text" name="cKRA_<%=j %>_0" id="cMainKRA" value="<%=goalkraInnerList.get(7) %>" class="validateRequired form-control"/>
					 Weightage (%): <input type="text" name="cKRAWeightage_<%=j %>_0" id="cKRAWeightage_<%=j %>_0" value="<%=goalkraInnerList.get(9) %>" style="width: 40px !important;" onkeyup="validateKRAScore(this.value,'cKRAWeightage_<%=j %>_0', 'c', '<%=j %>', '_0');" class="<%=validReqKra%>" onkeypress="return isNumberKey(event)" /> 
					<a href="javascript:void(0)" onclick="addKRA('c', '0', '_0');"><i class="fa fa-plus-circle"></i>Add Initiative</a>
					<%if(j > 0) { %>
						<%-- <a href="javascript:void(0)" style="color: red;" onclick="removeKRAID('cKRAdiv0_<%=j %>_0', '<%=goalkraInnerList.get(0) %>','_0');"><img border="0" style="height: 16px; width: 16px;" src="<%=request.getContextPath()%>/images1/icons/icons/close_button_icon.png"/>&nbsp;Remove KRA</a> --%>
						<a href="javascript:void(0)" style="color: red;" onclick="removeKRAID('cKRAdiv0_<%=j %>_0', '<%=goalkraInnerList.get(0) %>','_0');"><i class="fa fa-times-circle cross" aria-hidden="true"></i>&nbsp;Remove Initiative</a>
					<% } %>
				</div>
				<div style="width: 100%; float: left; margin-bottom: 3px;">
					<span style="float: left; margin-left: 7px;"><a href="javascript:void(0)" onclick="addKRATask('c', '0', '<%=j %>', '_0');"><i class="fa fa-plus-circle"></i>Add Task</a></span>
				
				</div>
				<div id="cKRATaskdiv0_<%=j %>_0" style="float: left; margin-left: 50px; margin-bottom: 3px;">
					<% 
					if(kraTaskList != null) {
					 for(int k=0; !kraTaskList.isEmpty() && k<kraTaskList.size(); k++) {
						List<String> kraTaskInnerList = kraTaskList.get(k);
						//System.out.println("tasks k==>"+k);
					%>
					<div id="cKRATaskdiv0_<%=k%>_0_<%=kraTaskInnerList.get(0) %>" style="width: 100%; float: left; margin-bottom: 5px;">
						<input type="hidden" name="hideKRATaskId_<%=k%>_0" id="hideKRATaskId_<%=k%>_0" value="<%=kraTaskInnerList.get(3) %>"/>
						<input type="text" name="cKRATask_<%=k%>_0" id="cKRATask_<%=k%>_0" value="<%=kraTaskInnerList.get(1) %>" class="validateRequired form-control"/> 
						<a href="javascript:void(0)" onclick="addKRATask('c', '0', '<%=j%>', '_0');"><i class="fa fa-plus-circle"></i>Add Task</a>&nbsp;
						<% if (k>0) { %>
							<a href="javascript:void(0)" style="color: red;" onclick="removeKRATaskID('cKRATaskdiv0_<%=k%>_0_<%=kraTaskInnerList.get(0) %>', '<%=kraTaskInnerList.get(3) %>','<%=j%>','_0');">
								<%-- <img border="0" style="height: 16px; width: 16px;" src="<%=request.getContextPath()%>/images1/icons/icons/close_button_icon.png"/>&nbsp;Remove Task --%>
								<i class="fa fa-times-circle cross" aria-hidden="true" ></i>&nbsp;Remove Task
								
							</a>
						<% } %>
					</div>
					<% } %>	
					<% } %>	
				</div>
			</div>
		<!-- end -->
				
		<% } 
		} else {
			//System.out.println("EMPG/1461--F_EDIT_PERSONAL_GOAL_BY_MANAGER="+IConstants.F_EDIT_PERSONAL_GOAL_BY_MANAGER);
		%>		
			<div id="cKRAdiv0_0_0" style="width: 100%; float: left; margin-bottom: 3px; border-bottom: 1px solid #F3F3F3;">
				<div style="width: 100%; float: left; margin-bottom: 3px;">
					<input type="hidden" name="KRATaskCount_0_0" id="KRATaskCount_0_0" value="0">
					<input type="hidden" name="hideKRAId_0_0" id="hideKRAId_0_0"/>
					<input type="text" name="cKRA_0_0" id="cMainKRA" class="validateRequired form-control"/>
					 Weightage (%): <input type="text" name="cKRAWeightage_0_0" id="cKRAWeightage_0_0" value="100" style="width: 40px !important;" onkeyup="validateKRAScore(this.value,'cKRAWeightage_0_0', 'c', '0', '_0');" class="<%=validReqKra%>" onkeypress="return isNumberKey(event)" />
					<a href="javascript:void(0)" onclick="addKRA('c','0', '_0');"><i class="fa fa-plus-circle"></i>Add Initiative</a>
				</div>
				<div style="width: 100%; float: left; margin-bottom: 3px;">
					<span style="float: left; margin-left: 7px;"><a href="javascript:void(0)" onclick="addKRATask('c', '0', '0', '_0');"><i class="fa fa-plus-circle"></i>Add Task</a></span>
					
				</div>
				<div id="cKRATaskdiv0_0_0" style="float: left; margin-left: 50px; margin-bottom: 3px;"></div>
			</div>

			<%} %>
			<input type="hidden" name="cKRACount" id="cKRACount0_0" value="<%=kracount %>" />
			<input type="hidden" name="cDeleteKRAIds" id="cDeleteKRAIds" />
			<input type="hidden" name="cDeleteKRATaskIds" id="cDeleteKRATaskIds" />
			</td>
		</tr>
		<% } %>
			
			<%
			//System.out.println("typeas ====>>> " + typeas);
			if(typeas != null && (typeas.equalsIgnoreCase("goal") || typeas.equalsIgnoreCase("target"))) { %>
			
			<%if(typeas != null && typeas.equalsIgnoreCase("goal")) { %>
				<tr>
					
					<th nowrap align="right">Does it have a Measure:<sup>*</sup></th>
					<td colspan="3">
					<%if(!checkTarget) { %>
						<s:select theme="simple" name="cmeasureKra" headerKey="" headerValue="Select" list="#{'Yes':'Yes', 'No':'No'}" cssClass="validateRequired   form-control "
						value="measureKravalue" onchange="showMeasureWith(this.value);" />
						
					<% } else { %>
						<input type="hidden" name="cmeasureKra" id="cmeasureKra0" value="<%=(String)request.getAttribute("measureKravalue")%>"/>
						<s:property value="measureKravalue"/>
					<% } %>		  
					</td>
				</tr>
			<%}%>
			
			
			<tr id="measureWith" style="display: <%=ismeasure %>;">
				<th align="right">Measure with:</th>
				<td colspan="3">
					<input type="hidden" name="cAddMKra" id="cAddMKra0" value="<%=innerList.get(13)%>"/>
					<%if(typeas != null && typeas.equalsIgnoreCase("Target")) { %> 
						<input type="hidden" name="cmeasureKra" id="cmeasureKra0" value="Yes"/>
					<% } %>
					<%if(!checkTarget) { %>
					  	<s:select theme="simple" name="cmeasurewith" headerKey="Amount" headerValue="Amount" list="#{'Effort':'Effort','Percentage':'Percentage'}" 
							onchange="getMeasureWith(this.value,'c',0);" value="measurewithvalue" cssClass="validateRequired form-control"/>
					  
					<%} else {%>
						<input type="hidden" name="cmeasurewith" id="cmeasurewith" value="<%=(String)request.getAttribute("measurewithvalue")%>"/>
						<s:property value="measurewithvalue"/> 
					<%} %>
				</td>
			</tr>
			<% 
			String isEfforts = "none";
			String isAmt = "none";
			String isRS = "none";
			String isPERCENT = "none";
			String measurewithvalue = (String)request.getAttribute("measurewithvalue");
						
			if(measurewithvalue!=null && (measurewithvalue.equals("Effort"))){
				isEfforts = "table-row";
				isAmt = "none";
								
			} else if(measurewithvalue!=null && (measurewithvalue.equals("Amount") || measurewithvalue.equals("Value") || measurewithvalue.equals("Percentage"))) {
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
				<span id="rsSpan0" style="display: <%=isRS %>; float: left;">&nbsp;</span>
				<span style="float: left;">
					<%if(!checkTarget) { %>
						<input type="text" name="cmeasureDollar" id="cmeasureDollar" style="width: 64px;" class="validateRequired form-control" value="<%=innerList.get(8)%>" onkeypress="return isNumberKey(event)"/>
					<%} else { %>
						<%=innerList.get(8)%>
						<input type="hidden" name="cmeasureDollar" id="cmeasureDollar" value="<%=innerList.get(8)%>"/>
					<%} %>
				</span>
				<span id="percentSpan0" style="display: <%=isPERCENT %>; float: left;margin-left:5px;"> %</span>
				</td>
			</tr>  
		
			<tr id="cmeasureEffortsid0" style="display: <%=isEfforts%>;"> 
				<th align="right">&nbsp;</th>
				<td colspan="3">
					<%if(!checkTarget) { %>
						Days&nbsp;<input type="text" name="cmeasureEffortsDays"
							id="cmeasureEffortsDays" style="width: 40px;" class="validateRequired form-control"  value="<%=innerList.get(10)%>" onkeyup="checkHrsLimit();" onkeypress="return isOnlyNumberKey(event)"/>&nbsp; 
						Hrs&nbsp;<input type="text" name="cmeasureEffortsHrs" class="validateRequired form-control" style="width: 40px;" id="cmeasureEffortsHrs" value="<%=innerList.get(11)%>" onkeyup="checkHrsLimit();" onkeypress="return isOnlyNumberKey(event)"/>
					<%} else { %>
						<input type="hidden" name="cmeasureEffortsDays" id="cmeasureEffortsDays" value="<%=innerList.get(10)%>"/>
						<input type="hidden" name="cmeasureEffortsHrs" id="cmeasureEffortsHrs" value="<%=innerList.get(11)%>"/>
						<%=innerList.get(10)%>&nbsp;Days &nbsp;<%=innerList.get(11)%>&nbsp;Hrs
					<%} %>
				</td>
			</tr>
			
		<%} %>
		
		
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
				  	<!-- Created by Dattatray Date:08-09-21 -->
				  		<%// Created By Dattatray date:13-09-21 Note : PERSONAL_GOAL Condition checked
				  		if(uF.parseToInt(goalTypePG) == IConstants.PERSONAL_GOAL){
							if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_INSTANCE_NAMEWISE_FEATURE)) && hmFeatureUserTypeId.get(IConstants.F_INSTANCE_NAMEWISE_FEATURE+"_USER_IDS")!=null && hmFeatureUserTypeId.get(IConstants.F_INSTANCE_NAMEWISE_FEATURE+"_USER_IDS").contains(IConstants.QULOI)) {
						%>
							<input type="hidden" name="frequency" value="<%=request.getAttribute("strFeqId") %>" />
							<span><%=request.getAttribute("strFeqName") %></span>
							<% }else{ %>
							<select name="frequency" id="frequency" onchange="checkFrequency(this.value, '')">
								<%=innerList.get(37) %>
							</select>
							<% } %>
						<% }else{ %>
						<select name="frequency" id="frequency" onchange="checkFrequency(this.value, '')">
							<%=innerList.get(37) %>
						</select>
						<% } %>
				  		
				    </td>
				</tr>
				
				<tr id="monthsTR" style="display: <%=strMonthsTR %>;">
					<th align="right">Select Months:</th> 
				  	<td colspan="3">
						<select name="strMonths" id="strMonths" multiple="multiple">
							<%=innerList.get(38) %>
						</select>
				    </td>
				</tr>
				
				<tr id="quartersHalfYearsTR" style="display: <%=strQuartersHalfYearsTR %>;">
					<th align="right"><span id="quartersHalfYearsLblSpan">Select <%=quartersHalfYearsLbl %>:</span></th> 
				  	<td colspan="3">
					  	<span id="quartersSpan" style="display: <%=strQuartersSpan %>; float: left; margin-right: 15px;">
					  		<select name="strQuarters" id="strQuarters" multiple="multiple">
								<%=innerList.get(39) %>
							</select>
				  		</span>
				  		<span id="halfYearsSpan" style="display: <%=strHalfYearsSpan %>; float: left; margin-right: 15px;">
					  		<select name="strHalfYears" id="strHalfYears" multiple="multiple">
								<%=innerList.get(40) %>
							</select>
				  		</span>
				  		<span id="yearTypeSpan" style="float: left;">
				  			<select name="strYearType" id="strYearType" style="width: 120px ! important;">
				  				<%=innerList.get(41) %>
							</select>
				  		</span>
				    </td>
				</tr>
				
				<tr id="yearsTR" style="display: <%=strYearsTR %>;">
					<th align="right">Select Recurring Years:</th> 
				  	<td colspan="3">
				  	  	<select name="strYears" id="strYears" multiple="multiple">
							<%=innerList.get(42) %>
						</select>
				    </td>
				</tr>
		
				<tr id="periodTR" style="display: <%=strPeriodTR %>;">
					<th align="right">Select Period:<sup>*</sup></th>
					<td colspan="3" width="250px"><input type="text" name="cgoalEffectDate" id="cgoalEffectDate" class="validateRequired" value="<%=innerList.get(29)%>" placeholder="Effective Date" style="width: 100px !important;" />&nbsp;&nbsp;&nbsp;&nbsp;
						<input type="text" name="cgoalDueDate" id="cgoalDueDate" class="validateRequired" value="<%=innerList.get(16)%>" placeholder="Due Date" style="width: 100px !important;" />
					</td>
				</tr>
				
				
				
			<%-- <tr>
				<th align="right">Effective Date:<sup>*</sup></th>
				<td colspan="3">
					<input type="text" name="cgoalEffectDate" id="cgoalEffectDate" class="duedatepick validateRequired marginright10" value="<%=innerList.get(29)%>"/>
					Due Date:<sup>*</sup><span class="marginright10"></span>
					<input type="text" name="cgoalDueDate" id="cgoalDueDate" class="duedatepick validateRequired marginright10" value="<%=innerList.get(16)%>" />
				</td>
			</tr> --%>
			
			
			<tr id="cOrientation0">
				<th align="right">Orientation:</th>
				<td  colspan="3">
				<!-- Created by Dattatray Date:08-09-21 -->
				  		<%// Created By Dattatray date:13-09-21 Note : PERSONAL_GOAL Condition checked
				  		//System.out.println("EMPG/1711--PERSONAL_GOAL="+uF.parseToInt(goalTypePG));
				  		if(uF.parseToInt(goalTypePG) == IConstants.PERSONAL_GOAL){
							if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_INSTANCE_NAMEWISE_FEATURE)) && hmFeatureUserTypeId.get(IConstants.F_INSTANCE_NAMEWISE_FEATURE+"_USER_IDS")!=null && hmFeatureUserTypeId.get(IConstants.F_INSTANCE_NAMEWISE_FEATURE+"_USER_IDS").contains(IConstants.QULOI)) {
						%>
						<input type="hidden" name="corientation" value="<%=request.getAttribute("strOrientationId") %>" />
							<span><%=request.getAttribute("strOrientationName") %></span>
						<% }else{ %>
						 	<select name="corientation" id="corientation" class=" form-control ">
						  		<%=innerList.get(36) %>
						 	</select>  
						<% } %>
						<% }else{ %>
							<select name="corientation" id="corientation" class=" form-control ">
						  		<%=innerList.get(36) %>
						 	</select>  
						<% } %>
				 
				</td>
			</tr>
			
			<%-- <% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_GOAL_FREQUENCY)) && hmFeatureUserTypeId.get(IConstants.F_GOAL_FREQUENCY).contains(strUsertypeId)) { %>
				<tr>
					<%
					String strWeek = "none";
					String strDay = "none";
					
					if(uF.parseToInt(innerList.get(40)) == 2) {
						strWeek = "block";
						
					} else if(uF.parseToInt(innerList.get(40)) == 3) {
						strDay = "block";
						
					}
					%>
					<th align="right">Select Frequency:</th>
				    <td colspan="5">
					 	<div style="position:reletive;">
						  <span style="float: left; margin-right: 20px">
							  <select name="frequency" id="frequency" onchange="checkFrequency(this.value, '')" class=" validateRequired">
							  <%=innerList.get(37) %>
							  </select>                            
						  </span>
						   
						   <span id="weekly" style="display: <%=strWeek %>; float: left;">Day:<sup>*</sup>
					           <select name="weekday" id="weekday" style="width:100px;" class="validateRequired   form-control">
					          	   <option value="">Select Day</option>
					           		<%=innerList.get(38) %>
					           </select>    
						   </span>  
						   
						   <span id="monthly" style="display: <%=strDay %>; float: left;">Date of Month:<sup>*</sup> 
						       <select name="day" id="day" style="width:65px;" class=" validateRequired   form-control ">
						       		<option value="">Select Day</option>
						       		 <%=innerList.get(39) %>
						       </select>
						   </span>
					   	</div>
					</td>
				</tr>
			<% } %> --%>
			
			<tr>
				<th align="right">Weightage(%):<sup>*</sup></th>
				<td colspan="3"><input type="text" name="cgoalWeightage" id="cgoalWeightage" class="validateRequired form-control"
					value="<%=innerList.get(19)%>" onkeypress="return isNumberKey(event)" onkeyup="validateScore(this.value, 'cgoalWeightage');"/></td>
			</tr>
			
			<%if(typeas !=null && typeas.equals("KRA")){ %>
				<%-- <tr>
					<th align="right"><span> align with Perspective:<sup>*</sup></span></th>
					<td colspan="3">
						<span style="float: left;">
						<select name="perspectiveYesno" id="perspectiveYesno" class="validateRequired"  onclick="showPerspective(this.value);">
							  <%=innerList.get(43) %>
							  </select> 
		                </span>
					 <span id="perspectiveDiv" style="display:none; float: left; padding-left: 10px;">
		                  	<select name="strPerspective" id="strPerspective" class="validateRequired">
							  <%=innerList.get(44) %>
							  </select> 
	                     </span>
					</td>
				</tr> --%>
				
				<tr>
					<th align="right"><span>Do you want to reivew this KRA:<sup>*</sup></span></th>
					<td colspan="3">
						<s:select theme="simple" cssClass="validateRequired" name="createReviewYesno" headerKey="No" headerValue="No" id="createReviewYesno"
							list="#{'Yes':'Yes'}"/>
					</td>
				</tr>
				
			<%} %>
			
			
			<%-- <%if(typeas !=null && typeas.equals("goal")) { %> --%>
			<%if(typeas !=null && typeas.equals("goal") && dataType == null) { %>
			<tr>
				<th><span style="float: right; padding-left: 10px;">Do you want to align this goal with team goal:</span></th>
				<td colspan="3">
				<span style="float: left;">
					<s:select theme="simple" cssClass="form-control" name="goalalignYesno" headerKey="" headerValue="Select" id="goalalignYesno" cssStyle="width:75px;"
						list="#{'Yes':'Yes','No':'No'}" onclick="showTeamGoals(this.value);" value="goalalignStatus"/>
                </span>
                     <span id="teamgoalDiv" style="display: none; float: left; padding-left: 10px;">
	                     <select name="teamGoalList" id="teamGoalList" class=" form-control ">
	                     <option value="">Select</option>
	                     <%=request.getAttribute("optionTeamGoals") %>
	                     </select>
                     </span>
				</td>
			</tr>
			<!-- start s Date : 04-09-21 -->
				<%
				if(uF.parseToInt(goalTypePG) == IConstants.PERSONAL_GOAL){/*Created By Dattatray Date :09-09-21 Note: checked goalTypePG  */
				if (hmMemberOption != null && !hmMemberOption.isEmpty()) { %>
				<%
					Iterator<String> it1 = hmMemberOption.keySet().iterator();
					while (it1.hasNext()) {
						String memPosition = it1.next();
						String optiontr = hmMemberOption.get(memPosition);
				%>
					<%=optiontr%>
				<%}%><%}}%>
			<!-- End Parvez Date : 04-09-21 -->
			<% } %>
		</table>


		<% if(hmOtherGoals != null) { %>
		<div id="newGoalDiv" style="float: left; width: 100%;">
			<input type="hidden" name="goalCnt" id="goalCnt" value="<%=(hmOtherGoals != null && !hmOtherGoals.isEmpty()) ? hmOtherGoals.size() : "0" %>">
			<% 
				Iterator<String> itOther = hmOtherGoals.keySet().iterator();
				int gCnt = 0;
				while(itOther.hasNext()) {
					String ogoalId = itOther.next();
					List<String> otherInnerList = hmOtherGoals.get(ogoalId);
					gCnt++;
			%>
					<div style="width: 100%; float: left; margin-top: 10px;">
						<table class="table table_no_border" style="width: 100%;">
						<tr>
							<th nowrap align="right" width="20%"><%=typeas%>:<sup>*</sup></th>
							<td colspan="3">
								<input type="hidden" name="goalId_<%=gCnt %>" id="goalId_<%=gCnt %>" value="<%=ogoalId %>"/>
								<input type="text" name="corporateGoal_<%=gCnt %>" class="validateRequired   form-control " style="width: 600px;" value="<%=otherInnerList.get(3)%>" />
							</td>
						</tr>
						<tr>
							<th nowrap align="right">Objective:</th>
							<td colspan="3"><input type="text" class=" form-control " name="cgoalObjective_<%=gCnt %>" style="width: 600px;" value="<%=otherInnerList.get(4)%>" /></td>
						</tr>
						<tr>
							<th align="right" valign="top">Description:</th>
							<td colspan="3"><textarea rows="3" cols="72" name="cgoalDescription_<%=gCnt %>" class="form-control "><%=otherInnerList.get(5)%></textarea></td>
						</tr>
						
						<tr>
							<th nowrap align="right">Priority:<sup>*</sup></th>
							<td colspan="3">
								<select name="priority_<%=gCnt %>" class="validateRequired   form-control ">
									<!-- <option value=""></option> -->
									<%=otherInnerList.get(33) %>
								</select>
							</td>
						</tr>
						<% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_GOAL_ATTRIBUTE_ALIGN)) && hmFeatureUserTypeId.get(IConstants.F_GOAL_ATTRIBUTE_ALIGN).contains(strUsertypeId)) { %>
							
							<tr>
								<th align="right">Align an Attribute:<sup>*</sup></th>
								<td colspan="3">
									<span style="float: left; margin-right: 10px;">
										<select name="goalElements_<%=gCnt %>" id="goalElements_<%=gCnt %>" class="validateRequired   form-control " style="width: 130px;" onchange="getAttributes(this.value, '_<%=gCnt %>');">
										<option value="">Select</option>
											<%=otherInnerList.get(31) %>
										</select>
									</span>
										
									<span id="attributeDiv_<%=gCnt %>" style="float: left;">
										<select name="cgoalAlignAttribute_<%=gCnt %>" id="cgoalAlignAttribute_<%=gCnt %>" class="validateRequired   form-control " style="width: 130px;">
											<option value="">Select</option>
												<%=otherInnerList.get(32) %>
										</select>
									</span>
								</td>
							</tr>
						<% } %>
						<tr id="ckraID0">
								<th valign="top" align="right">Initiative</th>
							<td id="cKRAtdID0_<%=gCnt %>" colspan="3">
							<%
							List<List<String>> goalKraList = hmKRA.get(otherInnerList.get(0));
							String kracount="0";
							if(goalKraList!=null) {
								kracount=""+goalKraList.size();
							 for(int j=0; !goalKraList.isEmpty() && j<goalKraList.size(); j++) {
								List<String> goalkraInnerList=goalKraList.get(j);
								List<List<String>> kraTaskList = hmKRATasks.get(goalkraInnerList.get(0));
							%>
							<div id="cKRAdiv0_<%=j %>_<%=gCnt %>" style="width: 100%; float: left; margin-bottom: 3px; border-bottom: 1px solid #F3F3F3;">
								<div style="width: 100%; float: left; margin-bottom: 3px;">
									<input type="hidden" name="KRATaskCount_<%=j %>_<%=gCnt %>" id="KRATaskCount_<%=j %>_<%=gCnt %>" value="<%=(kraTaskList != null && !kraTaskList.isEmpty()) ? kraTaskList.size() : "0" %>">
									<input type="hidden" name="hideKRAId_<%=j %>_<%=gCnt %>" id="hideKRAId_<%=j %>_<%=gCnt %>" value="<%=goalkraInnerList.get(0) %>"/>
									<input type="text" name="cKRA_<%=j %>_<%=gCnt %>" id="cMainKRA_<%=gCnt %>" class="validateRequired   form-control " value="<%=goalkraInnerList.get(7) %>"/>
									 Weightage (%): <input type="text"  name="cKRAWeightage_<%=j %>_<%=gCnt %>" id="cKRAWeightage_<%=j %>_<%=gCnt %>" value="<%=goalkraInnerList.get(9) %>" class="validateRequired form-control" style="width: 40px !important;" onkeyup="validateKRAScore(this.value,'cKRAWeightage_<%=j %>_<%=gCnt %>', 'c', '<%=j %>', '_<%=gCnt %>');" onkeypress="return isNumberKey(event)" /> 
									<a href="javascript:void(0)" onclick="addKRA('c', '0', '_<%=gCnt %>');"><i class="fa fa-plus-circle"></i>Add KRA</a>
									<% if(j > 0) { %>
										<%-- <a href="javascript:void(0)" style="color: red;" onclick="removeKRAID('cKRAdiv0_<%=j %>_<%=gCnt %>', '<%=goalkraInnerList.get(0) %>','_<%=gCnt%>');"><img border="0" style="height: 16px; width: 16px;" src="<%=request.getContextPath()%>/images1/icons/icons/close_button_icon.png"/>&nbsp;Remove KRA</a> --%>
										<a href="javascript:void(0)" style="color: red;" onclick="removeKRAID('cKRAdiv0_<%=j %>_<%=gCnt %>', '<%=goalkraInnerList.get(0) %>','_<%=gCnt%>');"><i class="fa fa-times-circle cross" aria-hidden="true" ></i>&nbsp;Remove KRA</a>
										
									<% } %>
								</div>
								<div style="width: 100%; float: left; margin-bottom: 3px;">
									<span style="float: left; margin-left: 7px;"><a href="javascript:void(0)" onclick="addKRATask('c', '0', '<%=j %>', '_<%=gCnt %>');"><i class="fa fa-plus-circle"></i>Add Task</a></span>
									
								</div>
								<div id="cKRATaskdiv0_<%=j %>_<%=gCnt %>" style="float: left; margin-left: 50px; margin-bottom: 3px;">
									<% 
									
									if(kraTaskList != null) {
									 for(int k=0; !kraTaskList.isEmpty() && k<kraTaskList.size(); k++) {
										List<String> kraTaskInnerList = kraTaskList.get(k);
									%>
									<div id="cKRATaskdiv0_<%=k%>_0_<%=kraTaskInnerList.get(0) %>" style="width: 100%; float: left; margin-bottom: 3px;">
										<input type="hidden" name="hideKRATaskId_<%=k %>_<%=gCnt %>" id="hideKRATaskId_<%=k %>_<%=gCnt %>" value="<%=kraTaskInnerList.get(3) %>"/>
										<input type="text" name="cKRATask_<%=k%>_<%=gCnt %>" id="cKRATask_<%=k%>_<%=gCnt %>" value="<%=kraTaskInnerList.get(1) %>" class="validateRequired   form-control "/> 
										<a href="javascript:void(0)" onclick="addKRATask('c', '0', '<%=j %>', '_<%=gCnt %>');"><i class="fa fa-plus-circle"></i>Add Task</a>&nbsp;
										<%if(k > 0) { %>
											<a href="javascript:void(0)" style="color: red;" onclick="removeKRATaskID('cKRATaskdiv0_<%=k %>_0_<%=kraTaskInnerList.get(0) %>', '<%=kraTaskInnerList.get(3) %>','<%=j%>','_<%=gCnt%>');">
												<%-- <img border="0" style="height: 16px; width: 16px;" src="<%=request.getContextPath()%>/images1/icons/icons/close_button_icon.png"/>&nbsp;Remove Task --%>
												<i class="fa fa-times-circle cross" aria-hidden="true" ></i>&nbsp;Remove Task
												
											</a>
										<% } %>
									</div>
									<% } %>	
									<% } %>	
								</div>
							</div>
								
						<% } 
						} else {
						%>		
							<div id="cKRAdiv0_0_<%=gCnt %>" style="width: 100%; float: left; margin-bottom: 3px; border-bottom: 1px solid #F3F3F3;">
								<div style="width: 100%; float: left; margin-bottom: 3px;">
									<input type="hidden" name="KRATaskCount_0_<%=gCnt %>" id="KRATaskCount_0_<%=gCnt %>" value="0">
									<input type="hidden" name="hideKRAId_0_<%=gCnt %>" id="hideKRAId_0_<%=gCnt %>"/>
									<input type="text" name="cKRA_0_<%=gCnt %>" id="cMainKRA_<%=gCnt %>" class="validateRequired   form-control "/>
									 Weightage (%): <input type="text" name="cKRAWeightage_0>_<%=gCnt %>" id="cKRAWeightage_0_<%=gCnt %>" class="validateRequired   form-control " value="100" style="width: 40px !important;" onkeyup="validateKRAScore(this.value,'cKRAWeightage_0_<%=gCnt %>', 'c', '0', '_<%=gCnt %>');" onkeypress="return isNumberKey(event)" />
									<a href="javascript:void(0)" onclick="addKRA('c','0', '_<%=gCnt %>');"><i class="fa fa-plus-circle"></i>Add KRA</a>
								</div>
								<div style="width: 100%; float: left; margin-bottom: 3px;">
									<span style="float: left; margin-left: 7px;"><a href="javascript:void(0)" onclick="addKRATask('c', '0', '0', '_<%=gCnt %>');"><i class="fa fa-plus-circle"></i>Add Task</a></span>
								</div>
								<div id="cKRATaskdiv0_0_<%=gCnt %>" style="float: left; margin-left: 50px; margin-bottom: 5px;"></div>
							</div>
			
							<% } %>
							<input type="hidden" name="cKRACount_<%=gCnt %>" id="cKRACount0_<%=gCnt %>" value="<%=kracount %>" />
						</td>
					</tr>
						
						<tr>
							<th align="right">Effective Date:<sup>*</sup></th>
							<td colspan="3">
								<input type="text" name="cgoalEffectDate_<%=gCnt %>" id="cgoalEffectDate_<%=gCnt %>" class="duedatepick validateRequired marginright10" value="<%=otherInnerList.get(29)%>"/>
								Due Date:<sup>*</sup>
								<span class="marginright10"></span>
								<input type="text" name="cgoalDueDate_<%=gCnt %>" id="cgoalDueDate_<%=gCnt %>" class="duedatepick validateRequired marginright10" value="<%=otherInnerList.get(16)%>" />
							</td>
						</tr>
					
						<script type="text/javascript">
							$("#cgoalEffectDate_"+<%=gCnt %>).datepicker({
					            format: 'dd/mm/yyyy',
					            autoclose: true
					        }).on('changeDate', function (selected) {
					            var minDate = new Date(selected.date.valueOf());
					            $("#cgoalDueDate_"+<%=gCnt %>).datepicker('setStartDate', minDate);
					        });
					        
					        $("#cgoalDueDate_"+<%=gCnt %>).datepicker({
					        	format: 'dd/mm/yyyy',
					        	autoclose: true
					        }).on('changeDate', function (selected) {
					                var minDate = new Date(selected.date.valueOf());
					                $("#cgoalEffectDate_"+<%=gCnt %>).datepicker('setEndDate', minDate);
					        });
						</script>
					
						<tr id="cOrientation0">
							<th align="right">Orientation:</th>
							<td  colspan="3">
							  <select name="corientation_<%=gCnt %>" class=" form-control ">
							  <%=otherInnerList.get(36) %>
							  </select>  
							</td>
						</tr>
						<% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_GOAL_FREQUENCY)) && hmFeatureUserTypeId.get(IConstants.F_GOAL_FREQUENCY).contains(strUsertypeId)) { %>
						<tr>
						<%
						String strWeekOther = "none";
						String strDayOther = "none";
						String validReqStrWeek = "form-control ";
						String validReqStrDay = "form-control ";
						if(uF.parseToInt(otherInnerList.get(40)) == 2) {
							strWeekOther = "block";
							validReqStrWeek = "validateRequired   form-control ";
							
						} else if(uF.parseToInt(otherInnerList.get(40)) == 3) {
							strDayOther = "block";
							validReqStrDay = "validateRequired   form-control ";
						}
						%>
						
						  <th align="right" >Select Frequency:</th> <!-- Select Frequency for Goal -->
						  <td colspan="5">
						  <div style="position:reletive;">
						  <span style="float: left; margin-right: 20px">
						  <select name="frequency_<%=gCnt %>" id="frequency_<%=gCnt %>" onchange="checkFrequency(this.value, '_<%=gCnt %>')">
						  <%=otherInnerList.get(37) %>
						  </select>
						  </span>
						   
						   <span id="weekly_<%=gCnt %>" style="display: <%=strWeekOther %>; float: left;">Day:
					           <select name="weekday_<%=gCnt %>" class="<%=validReqStrWeek %>" id="weekday_<%=gCnt %>" style="width:100px;">
					           <option value="">Select Day</option>
					           <%=otherInnerList.get(38) %>
					           </select>    
						   </span>
						   
						   <span id="monthly_<%=gCnt %>" style="display: <%=strDayOther %>; float: left;">Date of Month: 
						       <select name="day_<%=gCnt %>" id="day_<%=gCnt %>" style="width:65px;" class="<%=validReqStrDay %>">
						       <option value="">Select Day</option>
						        <%=otherInnerList.get(39) %>
						       </select>
						   </span>
						    </div>
						    </td>
						</tr>
				       <% } %>
						<tr>
							<th align="right">Weightage (%):<sup>*</sup></th>
							<td colspan="3"><input type="number" name="cgoalWeightage_<%=gCnt %>" id="cgoalWeightage_<%=gCnt %>" class="validateRequired    form-control "
								value="<%=otherInnerList.get(19)%>" onkeypress="return isNumberKey(event)" onkeyup="validateScore(this.value, 'cgoalWeightage_<%=gCnt %>');"/></td>
						</tr>
					</table>
				</div>
			<% } %>
		</div>
		<% } %>
		
		<% if(typeas != null && typeas.equals("KRA")) { %>
			<%-- <div style="float: left; width: 100%; margin-left: 20px;"><a href="javascript:void(0)" onclick="addNewGoal('<%=isFrequency%>', '<%=isAttribute%>')">+Add New KRAs</a> </div> --%>
		<% } %>
		<div style="float: left; width: 100%; text-align: center;">
			<% if(typeas == null || !typeas.equals("KRA") || !typeas.equals("target")) { %>
				<!-- added parvez on date: 04-09-2021=== -->
				<!-- start -->	
					<input type="hidden" name="policy_id" id="policy_id" value="<%=policy_id%>" />	
				<!-- end -->
			<% } %>	
			<%if(checkClose) { %>
				<input type="button" name="submit" value="Save" class="btn btn-primary" onclick="alert('This is closed');"/>
			<%} else { %>
				
		<!-- ===start parvez date: 30-12-2021=== -->		
				<%-- <s:submit value="Save" cssClass="btn btn-primary" name="submit"></s:submit> --%>
				<%if(dataType != null && dataType.equalsIgnoreCase("NewGoal")) { %>
					<input type="button" value="Save" class="btn btn-primary" name="submit" onclick="editPersonalGoal();">
				<% } else { %>
					<s:submit value="Save" cssClass="btn btn-primary" name="submit"></s:submit> 
				<%} %>
		<!-- ===end parvez date: 30-12-2021=== -->		
			<%} %>
		</div>
	</s:form>
	<%
			}
		}
	%>
</div>

<script>
<%if(from != null) {%>
<%-- ,
success: function(result){
	if(fromPage != "" && fromPage == "GKT") {
		getGoalKRADetails('GoalKRATarget','<%=empId%>','L','<%=currUserType%>','GKT');
	} else if(fromPage != "" && fromPage == "KT") {
		$("#divMyHRData").html(result);
	}
} --%>

$("#frmEditGoal").submit(function(e){
	e.preventDefault();
	var fromPage = '<%=from%>';
	//alert(fromPage);
//	if(checkFields)
		var form_data = $("#frmEditGoal").serialize();
		$("#divMyHRData").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({ 
			type : 'POST',
			url: "EditMyPersonalGoal.action",
			data: form_data+"&submit=Save",
			success: function(result){
				if(fromPage != "" && fromPage == "GKT") {
					<%-- getGoalKRAEmpList('GoalKRAEmpList','L','','<%=(String)request.getAttribute("strID")%>','','','',''); --%>
					getGoalKRATargetDashboardData('GoalKRATargetDashboardData','L','<%=currUserType %>');
				} else if(fromPage != "" && fromPage == "KT") {
					$("#divMyHRData").html(result);
				}
				
	   		},
			error: function(result){
				$.ajax({
					url: 'KRATarget.action',
					cache: true,
					success: function(result){
						if(fromPage != "" && fromPage == "GKT") {
							<%-- getGoalKRAEmpList('GoalKRAEmpList','L','','<%=(String)request.getAttribute("strID")%>','','','',''); --%>
							getGoalKRATargetDashboardData('GoalKRATargetDashboardData','L','<%=currUserType %>');
						} else if(fromPage != "" && fromPage == "KT") {
							$("#divMyHRData").html(result);
						}
					}
				});
			}
		});
	
});
<%} %>



</script>