<%@page import="java.net.URLEncoder"%>
<%@page import="java.util.HashSet"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
 <%  
	UtilityFunctions uF =  new UtilityFunctions(); 
 	CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
	 String strTaskList = (String)request.getAttribute("strTaskList"); 
	 String strUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
	 String strSessionEmpId = (String)session.getAttribute(IConstants.EMPID);
	 String currDate = (String)request.getAttribute("currDate");
	 
	Map hmReimbursementDetails = (Map)request.getAttribute("hmReimbursementDetails");
	
	Map<String, Map<String, String>> hmEmployee = (Map<String, Map<String, String>>)request.getAttribute("hmEmployee"); 
	Map<String, Map<String, String>> hmEmployeeBillableHrs = (Map<String, Map<String, String>>)request.getAttribute("hmEmployeeBillableHrs");
	
	Map<String, Map<String, String>> hmEmployeeHrsIsApproved = (Map<String, Map<String, String>>) request.getAttribute("hmEmployeeHrsIsApproved");
	Map<String, Map<String, String>> hmEmployeeBillableHrsIsApproved = (Map<String, Map<String, String>>) request.getAttribute("hmEmployeeBillableHrsIsApproved");
	
	Map<String, Map<String, String>> hmEmployeeTaskDescri = (Map<String, Map<String, String>>)request.getAttribute("hmEmployeeTaskDescri");
	
	Map<String, Map<String, String>> hmEmployeeTaskId = (Map<String, Map<String, String>>) request.getAttribute("hmEmployeeTaskId");
	
	Map<String, String> hmTaskIsBillable = (Map<String, String>) request.getAttribute("hmTaskIsBillable");
	
	Map hmEmployeeTasks = (Map)request.getAttribute("hmEmployeeTasks");
	 
	Map<String, String> hmProjectResourceMap = (Map<String, String>)request.getAttribute("hmProjectResourceMap");
	Map<String, String> hmProjectResourceWLocationMap = (Map<String, String>)request.getAttribute("hmProjectResourceWLocationMap");
	//Map hmClientMap = (Map)request.getAttribute("hmClientMap");
	//Map hmProjectMap = (Map)request.getAttribute("hmProjectMap");
	Map hmEmployeeCount = (Map)request.getAttribute("hmEmployeeCount");
	Map hmEmployeeBillCount = (Map)request.getAttribute("hmEmployeeBillCount");
	
	
	List alDates = (List)request.getAttribute("alDates");
	
	Map<String, Map<String, String>> hmLeaveDays = (Map<String, Map<String, String>>)request.getAttribute("hmLeaveDays");
	
	Map hmLeaveCode = (Map)request.getAttribute("hmLeaveCode");
	Map<String, Set<String>> hmWeekendMap = (Map<String, Set<String>>)request.getAttribute("hmWeekendMap");
	if(hmWeekendMap == null) hmWeekendMap = new HashMap<String, Set<String>>();
	Map<String, Set<String>> hmRosterWeekEndDates = (Map<String, Set<String>>)request.getAttribute("hmRosterWeekEndDates");
	if(hmRosterWeekEndDates == null) hmRosterWeekEndDates = new HashMap<String, Set<String>>();
	List<String> alEmpCheckRosterWeektype = (List<String>)request.getAttribute("alEmpCheckRosterWeektype");
	if(alEmpCheckRosterWeektype == null) alEmpCheckRosterWeektype = new ArrayList<String>();
	
	Map hmHolidayDates = (Map)request.getAttribute("hmHolidayDates");
	Map hmLeavesColour = (Map)request.getAttribute("hmLeavesColour");
	
	Map<String, String> hmCheckUserApproval =(Map<String, String>)request.getAttribute("hmCheckUserApproval");
	
	Map<String, String> hmCheckTaskStatus =(Map<String, String>)request.getAttribute("hmCheckTaskStatus");	
	
	List<String> unlockList=(List<String>)request.getAttribute("unlockList");
	if(unlockList==null) unlockList=new ArrayList<String>();
	
	boolean pendingProBillableApprovedFlag = (Boolean)request.getAttribute("pendingProBillableApprovedFlag");
	boolean pendingCustApprovedFlag = (Boolean)request.getAttribute("pendingCustApprovedFlag");
	
	Boolean checkPayroll=(Boolean)request.getAttribute("checkPayroll"); 
	
	Map<String, Map<String, String>> hmEmployeeTasksT = (Map<String, Map<String, String>>)request.getAttribute("hmEmployeeTasksT");
	Map<String, String> hmDateT = (Map<String, String>)request.getAttribute("hmDateT");
	Map<String, String> hmDateTaskIdT = (Map<String, String>)request.getAttribute("hmDateTaskIdT");
	Map<String, String> hmDateBillableHrsT = (Map<String, String>)request.getAttribute("hmDateBillableHrsT");
	Map<String, Map<String, String>> hmEmployeeT = (Map<String, Map<String, String>>)request.getAttribute("hmEmployeeT");
	Map<String, Map<String, String>> hmEmployeeBillableHrsT = (Map<String, Map<String, String>>)request.getAttribute("hmEmployeeBillableHrsT");
	Map<String, String> hmTaskIsBillableT = (Map<String, String>)request.getAttribute("hmTaskIsBillableT");
	
	Map<String, String> hmEmployeeCountT = (Map<String, String>)request.getAttribute("hmEmployeeCountT");
	Map<String, String> hmEmployeeBillCountT = (Map<String, String>)request.getAttribute("hmEmployeeBillCountT");
	
	List<String> alTaskIds = (List<String>) request.getAttribute("alTaskIds");
	List<String> alProResources = (List<String>) request.getAttribute("alProResources");
	
	Map<String, List<String>> hmTaskAndSubTaskIds = (Map<String, List<String>>) request.getAttribute("hmTaskAndSubTaskIds");
	
%>

 
<script>

var cxtpath = '<%=request.getContextPath()%>';
function submitForm() {
		
	var isHoliday = false;
	var isWeekend = false;
	var x=document.getElementsByName("holiday");
	var y=document.getElementsByName("weekend");
	
	for (var i=0;i<x.length;i++) {
	  	if(x[i].value=='H' && !isHoliday) {
	  		isHoliday = true;
	  		if(confirm('Are you sure you want to enter the time sheet on holidays?')) {
	  			return true;
	  		} else {
	  			return false;
	  		}
	  	}
	}
	for (var i=0;i<y.length;i++) {
	  	if(y[i].value=='WH' && !isWeekend && !isHoliday) {
	  		isWeekend = true;
	  		if(confirm('Are you sure you want to enter the time sheet on weekends?')) {
	  			return true;
	  		} else {
	  			return false;
	  		}
	  	}
	} 
}


	$(function() { 
		$("#idStdTimeStart").timepicker({});
		$("#idStdTimeEnd").timepicker({});
		
		var arrStrPaycycle = new Array();
		arrStrPaycycle = document.getElementById("strPaycycle").value.split("-"); 
		
		var paycycleStDt = arrStrPaycycle[0];
		var paycycleEndDt = arrStrPaycycle[1];
		
		$("#idStdDateEnd").datepicker({
			dateFormat : 'dd/mm/yy', minDate: paycycleStDt, maxDate: paycycleEndDt, 
			onClose: function(selectedDate){
				$("#idStdDateStart").datepicker("option", "maxDate", selectedDate);
			}
		});
		$("#idStdDateStart").datepicker({
			dateFormat : 'dd/mm/yy', minDate: paycycleStDt, maxDate: paycycleEndDt, 
			onClose: function(selectedDate){
				$("#idStdDateEnd").datepicker("option", "minDate", selectedDate);
			}
		});
		
	});


	
	jQuery(document).ready(function(){
        // binds form submission and fields to the validation engine
        $("body").on('click','#closeButton',function() {
    		$(".modal-dialog").removeAttr('style');
    		$(".modal-body").height(400);
    		$("#modalInfo").hide();
        });
    	$("body").on('click','.close',function() {
    		$(".modal-dialog").removeAttr('style');
    		$(".modal-body").height(400);
    		$("#modalInfo").hide();
    	});
    	
        /* jQuery("#formID1").validationEngine(); 
        jQuery("#formID2").validationEngine(); */
    });
	
 
	
	function removeTask(divElement, removeId, taskId) {
		if(confirm('Are you sure you want to delete this entry?')) {
			getContent('divElement', 'CustomerProjectTimesheet.action?D=D&strTaskId='+taskId);
			var remove_elem = removeId;
			var row_skill = document.getElementById(remove_elem);
			document.getElementById(divElement).removeChild(row_skill);
		}
	}
	
	var cnt=<%=alDates.size()%>;
	var checkboxcnt = 0;
	
function addTask(divElement) {
		
	var divE = document.getElementById(divElement).firstChild;
	var strDate = '';
	var strTime = '';
	checkboxcnt++;
	if(divE.hasChildNodes()){
		var divE1 = divE.childNodes[0];
		var divE2 = divE1.childNodes[1];
		strDate = divE2.value;

		divE1 = divE.childNodes[2];
		divE2 = divE1.childNodes[0];
		strTime = divE2.value;
	}
		
		cnt++;
		var divTag = document.createElement("div");
	    divTag.id = "row_task_"+cnt;
	    divTag.setAttribute("style", "float:left;width:750px;padding-bottom:10px;");
		divTag.innerHTML = 
			"<div style=\"float:left;width:80px;\"><input type=\"hidden\" name=\"taskId\" value=\"0\" ><input type=\"text\" style=\"width:62px\" name=\"strDate\" id=\"strDate_"+cnt+"\" value=\""+strDate+"\"></div>"+
			"<div style=\"float:left;width:225px;\">"+
				"<select name=\"strTask\" class=\"validateRequired\">"+
				"<%=strTaskList%>"+
				"</select>"+
			"</div>"+
			
			/* "<div style=\"float:left;width:150px;\"><input type=\"text\" style=\"width:62px\" name=\"strTime\"  value=\""+strTime+"\" onblur=\"checkHours();\"></div>"+ */
			"<div style=\"float:left;width:120px;\"><input type=\"text\" style=\"width:62px\" name=\"strTime\" id=\"strTime_"+cnt+"\" value=\"0.0\" onkeyup=\"checkAndAddBillableTime('"+cnt+"','"+checkboxcnt+"');\" onblur=\"checkHours('"+cnt+"');\"></div>"+
			"<div style=\"float: left; width: 55px; text-align: center;\"><input type=\"hidden\" style=\"width: 30px;\"  id=\"strBillableYesNoT_"+checkboxcnt+"\" name=\"strBillableYesNoT\" value=\"1\"><input type=\"checkbox\" id=\"strBillableYesNo_"+checkboxcnt+"\" name=\"strBillableYesNo\" onchange=\"setBillableValue('"+cnt+"','"+checkboxcnt+"')\" value=\"0\" checked></div>"+
			"<div style=\"float:left;width:85px;\"><input type=\"text\" style=\"width:62px\" name=\"strBillableTime\" id=\"strBillableTime_"+cnt+"\" value=\"0.0\" onblur=\"checkBillHours('"+cnt+"');\"></div>"+
			"<div style=\"float: left; width: 50px; text-align: center;\"><input type=\"hidden\" style=\"width: 30px;\"  id=\"strTaskOnOffSiteT_"+checkboxcnt+"\" name=\"strTaskOnOffSiteT\" value=\"1\"><input type=\"checkbox\" id=\"strTaskOnOffSite_"+checkboxcnt+"\" name=\"strTaskOnOffSite\" onchange=\"setValue('"+checkboxcnt+"')\" value=\"0\" checked></div>"+
			/* "<div style=\"float:left;width:100px;\"><a href=\"javascript:void(0)\" onclick=\"addTask('"+divElement+"_"+cnt+"')\">Add New Task</a></div>"+ */
			"<div style=\"float:left;width:20px;\">&nbsp;</div>"+
			 /* "<div style=\"float:left;\"><img border=\"0\" style=\"height: 16px; width: 16px;\" src=\""+cxtpath+"/images1/icons/icons/close_button_icon.png\" title=\"Remove Task\" onclick=\"removeTask('"+divElement+"','row_task_"+cnt+"',0)\" /></div>"+ */
			 "<div style=\"float:left;\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\" title=\"Remove Task\" onclick=\"removeTask('"+divElement+"','row_task_"+cnt+"',0)\"></i></div>"+
			 

			"<div style=\"float:left;width:20px; padding-left: 10px;\"><input type=\"hidden\" id=\"hidetaskDescription_"+cnt+"\" name=\"hidetaskDescription\" value=\"0\">"
			+"<a href=\"javascript: void(0);\" onclick=\"showHideDescription('"+cnt+"');\">"+
			"<span id=\"PdownarrowSpan_"+cnt+"\" style=\"float: left; margin-right: 3px;\"><i class=\"fa fa-angle-down\" aria-hidden=\"true\" style=\"width: 12px;\" title=\"Click here to add task description\"></i></span>"+
			"<span id=\"PuparrowSpan_"+cnt+"\" style=\"display: none; float: left; margin-right: 3px;\"><i class=\"fa fa-angle-up\" aria-hidden=\"true\" style=\"width: 12px;\" title=\"Click here to hide task description\"></i></span>"+
			"</a></div>"+
			
			"<div id=\"taskDescriptionDIV_"+cnt+"\" style=\"float: left; width: 100%; display: none;\">"+
			"<div style=\"float: left; padding: 10px 0px;\"><textarea name=\"taskDescription\" style=\"width: 390px;\" rows=\"2\" cols=\"50\" class=\"validateRequired\"></textarea></div>"+
			"</div>";
			
	    document.getElementById(divElement).appendChild(divTag);
	    callDatePicker();
	}
	

	function checkAndAddBillableTime(cnt, checkboxcnt) {
		//alert("cnt :::: "+cnt +"   checkboxcnt ::: " +checkboxcnt);
		var checkboxval = document.getElementById("strBillableYesNoT_"+checkboxcnt).value;
		//alert("checkboxval :::: "+checkboxval);
		if(checkboxval == '1') {
			document.getElementById("strBillableTime_"+cnt).value = document.getElementById("strTime_"+cnt).value;
		} else {
			document.getElementById("strBillableTime_"+cnt).value = '0.00';
		}
	}
	
	
	function showHideDescription(cnt) {
		var status = document.getElementById("hidetaskDescription_"+cnt).value;
		if(status == '0') {
			document.getElementById("taskDescriptionDIV_"+cnt).style.display = 'block';
			document.getElementById("PuparrowSpan_"+cnt).style.display = 'block';
			document.getElementById("PdownarrowSpan_"+cnt).style.display = 'none';
			document.getElementById("hidetaskDescription_"+cnt).value = '1';
		} else {
			document.getElementById("taskDescriptionDIV_"+cnt).style.display = 'none';
			document.getElementById("PuparrowSpan_"+cnt).style.display = 'none';
			document.getElementById("PdownarrowSpan_"+cnt).style.display = 'block';
			document.getElementById("hidetaskDescription_"+cnt).value = '0';
		}
	}
	
	
	function showHideTasks() {
		var status = document.getElementById("hidetaskTasks").value;
		if(status == '0') {
			document.getElementById("AllTasksDiv").style.display = 'block';
			document.getElementById("PuparrowSpan_Tasks").style.display = 'block';
			document.getElementById("PdownarrowSpan_Tasks").style.display = 'none';
			document.getElementById("hidetaskTasks").value = '1';
		} else {
			document.getElementById("AllTasksDiv").style.display = 'none';
			document.getElementById("PuparrowSpan_Tasks").style.display = 'none';
			document.getElementById("PdownarrowSpan_Tasks").style.display = 'block';
			document.getElementById("hidetaskTasks").value = '0';
		}
	}

	
	function setBillableValue(cnt, checkboxcnt){
		if (document.getElementById("strBillableYesNo_"+checkboxcnt).checked == 1) {
	          document.getElementById("strBillableYesNoT_"+checkboxcnt).value='1';
		} else {
			document.getElementById("strBillableYesNoT_"+checkboxcnt).value='0';
		}
		checkAndAddBillableTime(cnt, checkboxcnt);
	}

	
	function setValue(id) {
		if (document.getElementById("strTaskOnOffSite_"+id).checked == 1) {
	          document.getElementById("strTaskOnOffSiteT_"+id).value='1';
		} else {
			document.getElementById("strTaskOnOffSiteT_"+id).value='0';
		}
	}
	
	
	function checkTimeSheet(dates) {
		//alert(dates);
		if(dates != '0') { 
			if(confirm('KINDLY CHECK YOUR TIMESHEET. \n BLANK ENTRIES ' + dates+'')) {
				if(confirm('Are you sure you want to submit your whole timesheet? \n You will be unable to modify it once you submit. \n Click Ok to submit and Cancel to modify your timesheet.')) {
					return true;
				} else {
					return false;
				}
			}
		} else {
			if(confirm('Are you sure you want to submit your whole timesheet? \n You will be unable to modify it once you submit. \n Click Ok to submit and Cancel to modify your timesheet.')) {
				return true;
			} else {
				return false;
			}
		}
	}
	
function addTaskOther(divElement) {
	
	var divE = document.getElementById(divElement).firstChild;
	var strDate = '';
	var strTime = '';
	
	if(divE.hasChildNodes()) {
		var divE1 = divE.childNodes[0];
		var divE2 = divE1.childNodes[1];
		strDate = divE2.value;

		divE1 = divE.childNodes[2];
		divE2 = divE1.childNodes[0];
		strTime = divE2.value;
	}
		
		cnt++;
		var divTag = document.createElement("div");
	    divTag.id = "row_task_"+cnt;
	    divTag.setAttribute("style", "float:left;width:750px;padding:2px;");
		divTag.innerHTML = 
			"<div style=\"float:left;width:80px;\"><input type=\"hidden\" name=\"taskId\" value=\"0\" ><input type=\"text\" style=\"width:62px\" name=\"strDate\"  value=\""+strDate+"\"></div>"+
			"<div style=\"float:left;width:225px;\">"+
				"<select name=\"strTask\" class=\"validateRequired\">"+
				"<%=strTaskList%>"+
				"</select>"+
			"</div>"+
			
			"<div style=\"float:left;width:120px;\"><input type=\"text\" style=\"width:62px\" name=\"strTime\"  value=\"0.0\" onblur=\"checkHours();\"></div>"+
			"<div style=\"float: left; width: 55px; text-align: center;\">&nbsp;</div>"+
			"<div style=\"float:left;width:85px;\"><input type=\"text\" style=\"width:62px\" name=\"strBillableTime\"  value=\"0.0\" onblur=\"checkBillHours();\"></div>"+
			"<div style=\"float: left; width: 50px; text-align: center;\">&nbsp;</div>"+
			"<div style=\"float:left;width:20px;\">&nbsp;</div>"+
			/* "<div style=\"float:left;\"><img border=\"0\" style=\"height: 16px; width: 16px;\" src=\""+cxtpath+"/images1/icons/icons/close_button_icon.png\" title=\"Remove Task\" onclick=\"removeTask('"+divElement+"','row_task_"+cnt+"',0)\" /></div>"+ */
			"<div style=\"float:left;\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\" title=\"Remove Task\" onclick=\"removeTask('"+divElement+"','row_task_"+cnt+"',0)\"></i></div>"+
			
			"<p style=\"width:100%;float:left;\"><a href=\"javasript:void(0)\" onclick=\"showHideDescription('taskDescriptionDIV"+cnt+"');\"> Click to add description</a></p>"+
			"<div id=\"taskDescriptionDIV"+cnt+"\" style=\"display: none;\">"+
			"<div style=\"float: left; padding: 10px 0px;\"><textarea name=\"taskDescription\" style=\"width: 390px;\" rows=\"2\" cols=\"50\" class=\"validateRequired\"></textarea></div>"+
			"</div>";
			
	    document.getElementById(divElement).appendChild(divTag);
	    
	    callDatePicker();
	}
	
	function copyTaskId(val, cnt){
		document.frmProjectActivity1.strTaskOnOffSiteT[cnt].value = val;
	}
	

	function getReimbursementDetails(pro_id, emp_id,d1,d2) {
		var dialogEdit = '#addproject'; 
		dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
		$(dialogEdit).dialog({
			autoOpen : false,
			bgiframe : true,
			resizable : false, 
			height : 350,
			width : 800, 
			modal : true,
			title : 'Reimbursement Summary',
			open : function() {
				var xhr = $.ajax({
					url : "ProReimbursementSummary.action?pro_id="+pro_id+"&emp_id="+emp_id+"&strD1="+d1+"&strD2="+d2,
					cache : false,
					success : function(data) {
						$(dialogEdit).html(data);
					}
				});
				xhr = null;
			},
			overlay : {
				backgroundColor : '#000',
				opacity : 0.5
			}
		});
		$(dialogEdit).dialog('open');
	}  
	
	
	
jQuery(document).ready(function() {
		
		  jQuery(".content1").hide();
		  //toggle the componenet with class msg_body
		  jQuery(".aa").click(function()
		  {
		    jQuery(this).next(".content1").slideToggle(500);
			$(this).toggleClass(""); 
		  });
		});


function checkBillHours(count) {
	var arrDates = document.getElementsByName('strDate');
	var arrTime = document.getElementsByName('strTime');
	var arrBillableTime = document.getElementsByName('strBillableTime');
	var currDate = document.getElementById('strDate_'+count).value;
	var arrTotal = {}; 
	
	for(i=0; i<arrDates.length; i++) {
		
		var time = arrTotal["\""+arrDates[i].value+"\""];
		var totalTime = 0;
		
		if(time!=undefined) {
			totalTime = parseFloat(time) + parseFloat(arrBillableTime[i].value);	
		} else {
			totalTime = parseFloat(arrBillableTime[i].value);
		}
		
		var strbilltime = arrBillableTime[i].value;
		var strtime = arrTime[i].value;
		
		if(parseFloat(strbilltime) > parseFloat(strtime)) {
			alert('Billing Time is exceeding total hours.\nPlease ensure time does not exceed '+strtime+' hours.');
			document.getElementById('strBillableTime_'+count).value = '0.00';
		} else if(parseFloat(totalTime) > 24) {
			alert('Time is exceeding 24 hours for '+arrDates[i].value+".\nPlease ensure time does not exceed 24 hours.");
			if(currDate == arrDates[i].value) {
				document.getElementById('strBillableTime_'+count).value = '0.00';
			}
			return;
		}
		arrTotal["\""+arrDates[i].value+"\""] = totalTime;
    }
	
}

		
function checkHours(count) {
	var arrDates = document.getElementsByName('strDate');
	var arrTime = document.getElementsByName('strTime');
	var arrBillableTime = document.getElementsByName('strBillableTime');
	var currDate = document.getElementById('strDate_'+count).value;
	var arrTotal = {}; 
	
	for(i=0; i<arrDates.length; i++) {
		var time = arrTotal["\""+arrDates[i].value+"\""];
		var totalTime = 0;
		
		if(time!=undefined) {
			totalTime = parseFloat(time) + parseFloat(arrTime[i].value);	
		} else {
			totalTime = parseFloat(arrTime[i].value);
		}
		if(parseFloat(totalTime) > 24) {
			alert('Time is exceeding 24 hours for '+arrDates[i].value+".\nPlease ensure time does not exceed 24 hours.");
			if(currDate == arrDates[i].value) {
				document.getElementById('strTime_'+count).value = '0.00';
				document.getElementById('strBillableTime_'+count).value = '0.00';
			}
			return;
		}
		arrTotal["\""+arrDates[i].value+"\""] = totalTime;
    }
}


$(document).ready(function() {

    $('a.poplight[href^=#]').click(function() {
        var popID = $(this).attr('rel'); //Get Popup Name 
        var popURL = $(this).attr('href'); //Get Popup href to define size 

        //Pull Query & Variables from href URL
        var query= popURL.split('?');
        var dim= query[1].split('&');
        var popWidth = dim[0].split('=')[1]; //Gets the first query string value

        //Fade in the Popup and add close button
        $('#' + popID).fadeIn().css({ 'width': Number( popWidth ) }).prepend('<a href="#" class="close"><img src="<%=request.getContextPath()%>/images/close_pop.png" class="btn_close" title="Close Window" alt="Close" /></a>');

        //Define margin for center alignment (vertical   horizontal) - we add 80px to the height/width to accomodate for the padding  and border width defined in the css
        var popMargTop = ($('#' + popID).height() + 80) / 2;
        var popMargLeft = ($('#' + popID).width() + 80) / 2;

        //Apply Margin to Popup
        $('#' + popID).css({
            'margin-top' : -popMargTop,
            'margin-left' : -popMargLeft
        });

        //Fade in Background
        $('body').append('<div id="fade"></div>'); //Add the fade layer to bottom of the body tag.
        $('#fade').css({'filter' : 'alpha(opacity=80)'}).fadeIn(); //Fade in the fade layer - .css({'filter' : 'alpha(opacity=80)'}) is used to fix the IE Bug on fading transparencies

        return false;
    });

    //Close Popups and Fade Layer
    $('a.close, #fade').live('click', function() { //When clicking on the close or fade layer...
        $('#fade , .popup_block').fadeOut(function() {
            $('#fade, a.close').remove();  //fade them both out
        });
        return false;
    });

});



function addLeave(isConstant) {

	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$(".modal-title").html('Apply New Leave');
	$.ajax({
		url : 'ApplyLeavePopUp.action?isCompensate=false&isConstant='+isConstant+'&type=timesheet',
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}  


function showTaskDescription(value) {
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$(".modal-title").html('Task Description');
	$.ajax({
		url : 'GetTaskDescription.action?taskDescription='+encodeURIComponent(value),
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
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


function getProjectList() {
	var client_id = getSelectedValue("strClient");
	if(client_id.length > 0) {
		var action = 'GetProjectClientTask.action?client_id=' + client_id;
		getContent('myProject', action);
	}
}

function getTaskList() {
	var project_id = getSelectedValue("strProject");
	if(client_id.length > 0) {
		var action = 'GetProjectClientTask.action?project_id=' + project_id;
		getContent('myTask', action);
	}
}



function customerTimesheetDeny(proID, proFreqID) {
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$(".modal-title").html('Deny Timesheet');
	$.ajax({
		url : 'ProjectTimesheetDenyByCustomer.action?proID='+proID+'&proFreqID='+proFreqID,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

</script>

<script type="text/javascript">
$(function(){
	$("#strClient").multiselect().multiselectfilter();
	$("#strProject").multiselect().multiselectfilter();
});    
</script>


<section class="content">
	<div class="row">
	<section class="content">
		<div class="box box-body col-md-12">
		<%=session.getAttribute(IConstants.MESSAGE) %>
		<% session.setAttribute(IConstants.MESSAGE, ""); %>

	<%if(request.getAttribute("PROJECT_NAME") != null && !request.getAttribute("PROJECT_NAME").equals("")) { %>
	<div style="font-size: 15px;"><b><%=request.getAttribute("PROJECT_NAME") %> Project Timesheet</b> 
	
	<a class="fa fa-file-excel-o" style="float: right; padding-right: 10px;" href="GenerateProjectwiseTimesheet.action?proId=<%=request.getAttribute("proId") %>&proFreqId=<%=request.getAttribute("proFreqId") %>&frmDate=<%=request.getAttribute("frmDate") %>&toDate=<%=request.getAttribute("toDate") %>&downloadSubmit=0">&nbsp;</a>
	</div>
	<%} %>
		
<% if(uF.parseToInt((String)request.getAttribute("nApproved"))==0 && strUserType!=null && (strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) || strUserType.equalsIgnoreCase(IConstants.MANAGER) || strUserType.equalsIgnoreCase(IConstants.HRMANAGER)  || strUserType.equalsIgnoreCase(IConstants.ADMIN))) { %>
		
	<s:form id="formID2" action="CustomerProjectTimesheet" name="frmCustomerProjectTimesheet" cssClass="formcss" method="post" theme="simple" onsubmit="return submitForm();">		
		<s:hidden name="proId" id="proId"></s:hidden>
		<s:hidden name="proFreqId" id="proFreqId"></s:hidden>
		<input type="hidden" name="strEmpId" value="<%=request.getParameter("strEmpId")%>"/>
		<input type="hidden" name="frmDate" value="<%=request.getParameter("frmDate")%>"/>
		<input type="hidden" name="toDate" value="<%=request.getParameter("toDate")%>"/>
		<input type="hidden" name="strProject" value="<%=request.getParameter("strProject")%>"/>
		<input type="hidden" name="strActivity" value="<%=request.getParameter("strActivity")%>"/>
		<input type="hidden" name="strPaycycle" value="<%=request.getParameter("strPaycycle")%>"/> 
		<input type="hidden" name="strClient" value="<%=request.getParameter("strClient")%>"/> 
		<s:hidden name="pageType" id="pageType" />
		<% if(request.getAttribute("sbTasks") != null && !request.getAttribute("sbTasks").equals("")) { %>
			<div style="float:left;width:750px;" id="div_tasks">
			
		    <div style="float:left;width:750px;padding:2px;">
			    <div style="float:left;width:80px;font-weight:bold;">Date</div>
			    <div style="float:left;width:225px;font-weight:bold;">Task</div>
			    <div style="float:left;width:120px;font-weight:bold;">Total Hrs</div>
			    <div style="float:left;width:55px;font-weight:bold;">Billable</div>
			    <div style="float:left;width:85px;font-weight:bold;">Bill Hrs</div>
			    <div style="float:left;width:50px;font-weight:bold;">On-Site</div>
			    <div style="float:left;font-weight:bold;">&nbsp;</div>
			    <div style="float:left;font-weight:bold;">&nbsp;</div>
			    <div style="float:left;font-weight:bold;">&nbsp;</div>
			 </div>
			 
			 <%=request.getAttribute("sbTasks") %>
			 
			 <div style="float:left;width:750px;padding:2px;">
			    <div style="float:left;width:80px;">&nbsp;</div>
			    <div style="float:left;width:225px;">&nbsp;</div>
			    <div style="float:left;width:120px;">&nbsp;</div>
			    <div style="float:left;width:55px;">&nbsp;</div>
			    <div style="float:left;width:85px;">&nbsp;</div>
			    <div style="float:left;width:50px;">&nbsp;</div>
			    <div style="float:left;"><input type="submit" name="save" value="Save Timesheet" class="btn btn-primary"></div>
			    <div style="float:left;">&nbsp;</div>
			    <div style="float:left;">&nbsp;</div>
			 </div>
	    </div>
		<% } %>
		
		<% if(request.getAttribute("sbTasks") == null || request.getAttribute("sbTasks").equals("")) { %>
			<%=(String)request.getAttribute("sbNoTasks") %>
		<% } %>
	</s:form>		
		
	<% } %>
	
	<div style="float:left; width: 600px;">
		<div style="width: 100%; float: left;">
			<div style="float: left; ">Customer Name (SPOC): <b><%=(String)request.getAttribute("PRO_CUSTOMER_NAME") %> (<%=(String)request.getAttribute("PRO_CUST_SPOC_NAME") %>)</b></div>
			<div style="float: right;">Billable Efforts: <b><%=(String)request.getAttribute("strBillEfforts") %></b></div>
		</div>
		<div style="width: 100%; float: left;">
			<div style="float: left;">Project Owner: <b><%=(String)request.getAttribute("PRO_OWNER_NAME") %></b></div>
			<div style="float: right;">Total Efforts: <b><%=(String)request.getAttribute("strActEfforts") %></b></div>
		</div>
		<div style="width: 100%; float: left;">
			<div style="float: left;">Project Type: <b><%=(String)request.getAttribute("PRO_BILLING_FREQUENCY") %></b></div>
		</div>
		<div style="width: 100%; float: left;">
			<div style="float: left;">Period: <b><%=(String)request.getAttribute("PRO_PERIOD") %></b></div>
			<div style="float: right;"><span style="float: left; margin-right: 7px;">Submissions:</span> <b><%=(String)request.getAttribute("strSubmitions") %></b></div>
		</div>
	</div>

	<div style="float:right; margin-bottom: 7px; margin-top: 5px;">
		<% if(strUserType != null && strUserType.equalsIgnoreCase(IConstants.EMPLOYEE)) { %>
			<div style="margin-bottom: 10px; margin-left: 100px;">
				<a href="javascript:void(0)" onclick="addLeave('false')"><input type="button" class="btn btn-primary" value="Apply Leave"></a> 
				<a href="javascript:void(0)" onclick="addLeave('true')"><input type="button" class="btn btn-primary" value="Apply Client Weekly Off"></a>
			</div>
		<% } %>
		<div id="idTeamInfo" style="border: 2px solid #ccc; width: 350px;padding:5px;">
			<b>Approval History</b>
			<table class="table form-table">
				<tr>
					<th>Sr.No.</th>
					<th width="40%">Submitted to</th>
					<th width="25%">Submitted On</th>
					<th width="25%">Approved On</th>
				</tr>
				<%	List<List<String>> mOuterList = (List<List<String>>)request.getAttribute("mOuterList");
					if(unlockList == null) unlockList = new ArrayList<String>();
					int ii=0;
					for(ii=0; mOuterList!=null && ii<mOuterList.size(); ii++) {
						List<String> innerList = mOuterList.get(ii);
				%>
				<tr>
					<td style="width:15%;" align="center"><%=ii+1 %></td>
					<td align="left"><%=innerList.get(0) %>
						<%if(unlockList.contains(innerList.get(1))) { %>
							<p>unlocked</p>
						<% } %>
					</td>
					<td align="center"><%=uF.showData(innerList.get(2),"-") %></td>
					<td align="center"><%=uF.showData(innerList.get(3),"-") %></td>
				</tr>
				
				<% } if(ii==0) { %>
				<tr><td colspan="4"><div class="nodata msg"><span>No Approval Status</span></div></td></tr>
				<% } %>
			</table>
		</div>
	</div>

<s:form name="timesheet" id="ftimesheet" action="CustomerProjectTimesheet" theme="simple">
	<div style="float: left; overflow: scroll; width: 100%;">

	<table class="table form-table">
		<tr>
			<s:hidden name="proId" id="proId"></s:hidden>
			<s:hidden name="proFreqId" id="proFreqId"></s:hidden>
			<th colspan="36" style="text-align: center;"><%=request.getAttribute("timesheet_title") %></th>
		</tr>
	
		<tr>
			<th>Resources</th>
			<th>Task</th>
			<th>Hrs</th>
			<th>ONS/OFS</th>
			<%
			for(int i=0; i<alDates.size(); i++) {
				String strText = "-";
				String strBgColor = null;
			%>
				<th><%=uF.getDateFormat((String)alDates.get(i), IConstants.DATE_FORMAT, "dd")%></th>
			<%} %>
		</tr>

		<%
		
		Map hmTotal = new HashMap();
		Map hmTotalBillableHrs = new HashMap();
		StringBuilder sb = new StringBuilder();
		
		String strResourcIdNew = null;
		String strResourcIdOld = null;
		int nCount = 0;
		
		for(int b=0; alProResources != null && b<alProResources.size(); b++) {
			String strResourceId = alProResources.get(b);
			String strWLocationId = hmProjectResourceWLocationMap.get(strResourceId);
			Set<String> weeklyOffSet = hmWeekendMap.get(strWLocationId);
			if(weeklyOffSet==null) weeklyOffSet=new HashSet<String>();
			
			Set<String> rosterWeeklyOffSet = hmRosterWeekEndDates.get(strResourceId);
			if(rosterWeeklyOffSet == null) rosterWeeklyOffSet = new HashSet<String>();
			
			Map<String, String> hmLeaves = hmLeaveDays.get(strResourceId);
			if(hmLeaves == null) hmLeaves = new HashMap<String, String>();
			
		for(int a=0; alTaskIds != null && a<alTaskIds.size(); a++) {

			String strActivityId = alTaskIds.get(a);
			//System.out.println("strActivityId ===>> " + strActivityId);
			List<String> alSubTaskIds = new ArrayList<String>();
			
			if(hmTaskAndSubTaskIds != null) {
				alSubTaskIds = hmTaskAndSubTaskIds.get(strActivityId);
			}
			
			Map hmTasks = (Map)hmEmployeeTasks.get(strResourceId+"_"+strActivityId);
			if(hmTasks == null)hmTasks = new HashMap();
			
			
			if((hmTasks == null || hmTasks.isEmpty()) && (alSubTaskIds ==null || alSubTaskIds.size()==0)) {

			} else {
				
				if(hmTasks == null || hmTasks.isEmpty()) {
					
					Map hmTasksT = (Map)hmEmployeeTasksT.get(strResourceId+"_"+strActivityId);
					if(hmTasksT == null)hmTasksT = new HashMap();
					
					if(hmTasksT != null && !hmTasksT.isEmpty()) {
					String strResourcId = (String)hmTasksT.get(strResourceId+"_"+strActivityId+"_E");
					strResourcIdNew = (String)hmTasksT.get(strResourceId+"_"+strActivityId+"_E");
					int nProCount = uF.parseToInt((String)hmEmployeeCount.get(strResourcId));
					int nProBillCount = uF.parseToInt((String)hmEmployeeBillCount.get(strResourcId));
					int nProCountT = uF.parseToInt((String)hmEmployeeCountT.get(strResourcId));
					int nProBillCountT = uF.parseToInt((String)hmEmployeeCountT.get(strResourcId));
					
					if(strResourcIdNew!=null && !strResourcIdNew.equalsIgnoreCase(strResourcIdOld)) {
						nCount = 0;
					} else if(strResourcIdNew == null) {
						strResourcIdNew = "";
						if(!strResourcIdNew.equalsIgnoreCase(strResourcIdOld)) {
							nCount = 0;
						}
					}
					nCount++;
					
					Map<String, String> hmDatesT = (Map<String, String>)hmEmployeeT.get(strResourceId+"_"+strActivityId);
					if(hmDatesT == null)hmDatesT = new HashMap<String, String>();
					
					Map<String, String> hmDatesBillableHrsT = (Map<String, String>)hmEmployeeBillableHrsT.get(strResourceId+"_"+strActivityId);
					if(hmDatesBillableHrsT == null) hmDatesBillableHrsT = new HashMap<String, String>();
					
					%>
					
					<% for(int j=0; j<2; j++) {
						sb.replace(0, sb.length(), "");
						if(j==0) {
							sb.append("OFS"); 
						} else {
							sb.append("ONS");
						}
					%>
					
					<tr>
					
					<%if(j==0 && nCount==1) { 
					int rwspn = 2;
					int billrwspn = 0;
					if(nProBillCount>0) {
						billrwspn = nProBillCount * 2;
					}
					int billrwspnT = 0;
					if(nProBillCountT>0) {
						billrwspnT = nProBillCountT * 2;
					}
					//System.out.println(strProjectId+ " --- nProCount ==>> "+ nProCount+" rwspn ==>> "+ rwspn +" (nProCount * rwspn) ===>> " + (nProCount * rwspn) + " billrwspn ===> " + billrwspn + " nProCountT ===>> " + nProCountT + " rwspn ===> " + rwspn + " (nProCountT * rwspn) ===>> " + (nProCountT * rwspn) + " billrwspnT ===> " + billrwspnT);
					//System.out.println(strProjectId+ " --- (nProCount * rwspn) + billrwspn + (nProCountT * rwspn) + billrwspnT ==>> " + ((nProCount * rwspn) + billrwspn + (nProCountT * rwspn) + billrwspnT));
					%>
					
						<td rowspan="<%=(nProCount * rwspn) + billrwspn + (nProCountT * rwspn) + billrwspnT %>"><%=uF.showData((String)hmProjectResourceMap.get(strResourcId), "")%>  <br/></td>
					<% } %>
						<% for(int i=0; i<alDates.size(); i++) { %>
						<% String strText = "-";
							String strBgColor = null;
							
							if(alEmpCheckRosterWeektype.contains(strResourceId)) {
								if(rosterWeeklyOffSet.contains(alDates.get(i))) {
									strBgColor =IConstants.WEEKLYOFF_COLOR;
								}
							} else if(weeklyOffSet.contains(alDates.get(i))) {
								strBgColor =IConstants.WEEKLYOFF_COLOR;
							}
							if(strBgColor!=null) {
								strText = "W/O";
							}
							
							if(strBgColor==null) {
								strBgColor = (String)hmHolidayDates.get(uF.getDateFormat((String) alDates.get(i), IConstants.DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId);
								strText = "H";
							}
							if(strBgColor==null) {
								strBgColor = (String) hmLeavesColour.get((String)hmLeaves.get(alDates.get(i)));
								strText = (String)hmLeaveCode.get((String)hmLeaves.get(alDates.get(i))); 
							}
							
							if(uF.getDateFormat(currDate, IConstants.DATE_FORMAT).equals(uF.getDateFormat(alDates.get(i).toString(), IConstants.DATE_FORMAT))) {
								strBgColor = "#EFEFEF";
							}
							
							if(strText==null) {
								strText = "-";
							}
							
							if(i==0 && j==0) {
								int tskRwSpan = 2;
								if(hmTaskIsBillableT != null && uF.parseToBoolean(hmTaskIsBillableT.get(strResourceId+"_"+strActivityId))) {
									tskRwSpan += 2;
								}
							%>
							<td rowspan="<%=tskRwSpan %>"><strong><%=uF.showData((String)hmTasksT.get(strResourceId+"_"+strActivityId+"_T"), "-") %></strong>
							</td>
							
							<td rowspan="2">AH</td>
							<% } %>
							
							<%if(i==0) { %>
							<td><%=sb.toString()%></td>
							<% } %>
							
							<%
							String activity_id=null;
							String prod_id=null;
							String bgcolor=null;
								String dayStatus="";
								dayStatus=uF.showData((String)hmDatesT.get((String)alDates.get(i)+"_"+sb.toString()), strText);
								if(uF.parseToDouble(dayStatus) > 0) {
									dayStatus = uF.getTotalTimeMinutes100To60(dayStatus);
								}
							%>
							<td align="right" style="background-color: <%=bgcolor==null ? strBgColor:bgcolor%>"><%=dayStatus%></td>
						
						<% } %>
					</tr>
					
				<% } %>
					
					<% if(hmTaskIsBillableT != null && uF.parseToBoolean(hmTaskIsBillableT.get(strResourceId+"_"+strActivityId))) { %>
						<%for(int j=0; j<2; j++) {
							sb.replace(0, sb.length(), "");
							if(j==0) {
								sb.append("OFS"); 
							} else {
								sb.append("ONS");
							}
						%> 
					
					<tr>
						<% for(int i=0; i<alDates.size(); i++) { %>

						<% 	String strText = "-";
							
							String strBgColor = null;
							if(alEmpCheckRosterWeektype.contains((String)request.getAttribute("strEmpId"))) {
								if(rosterWeeklyOffSet.contains(alDates.get(i))) {
									strBgColor =IConstants.WEEKLYOFF_COLOR;
								}
							} else if(weeklyOffSet.contains(alDates.get(i))) {
								strBgColor =IConstants.WEEKLYOFF_COLOR;
							}
							if(strBgColor!=null) {
								strText = "W/O";
							}
							
							if(strBgColor==null) {
								strBgColor = (String)hmHolidayDates.get(uF.getDateFormat((String) alDates.get(i), IConstants.DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId);
								strText = "H";
							}
							if(strBgColor==null) {
								strBgColor = (String) hmLeavesColour.get((String)hmLeaves.get(alDates.get(i)));
								//strText = (String)hmLeaves.get((String)alDates.get(i));
								strText = (String)hmLeaveCode.get((String)hmLeaves.get(alDates.get(i))); 
							}
							
							if(uF.getDateFormat(currDate, IConstants.DATE_FORMAT).equals(uF.getDateFormat(alDates.get(i).toString(), IConstants.DATE_FORMAT))) {
								strBgColor = "#EFEFEF";
							}
							
							if(strText==null) {
								strText = "-";
							}
							
							if(i==0 && j==0) {
							%>
							<td rowspan="2">BH</td>
							<% } %>

							<%if(i==0) { %>
							<td><%=sb.toString()%></td>
							<% } %>
							
							<%
							String activity_id=null;
							String prod_id=null;
							String bgcolor=null;

							String dayStatus="";
							
							dayStatus=uF.showData((String)hmDatesBillableHrsT.get((String)alDates.get(i)+"_"+sb.toString()), strText);
							if(uF.parseToDouble(dayStatus) > 0) {
								dayStatus = uF.getTotalTimeMinutes100To60(dayStatus);
							}
							%>
							<td align="right" style="background-color: <%=bgcolor == null ? strBgColor : bgcolor %>">
								<%=dayStatus %>
							</td>
						<% } %>
					</tr>
					<% } %>
					
				<% }
					strResourcIdOld = strResourcIdNew;
				%>
					
				<% }
				
				} else {
					String strResourcId = (String)hmTasks.get(strResourceId+"_"+strActivityId+"_E");
					strResourcIdNew = (String)hmTasks.get(strResourceId+"_"+strActivityId+"_E");
					//System.out.println("strActivityId hmTasks ===>> " + strActivityId);
					//System.out.println("strResourcId hmTasks ===>> " + strResourcId);
					
			int nProCount = uF.parseToInt((String)hmEmployeeCount.get(strResourcId));
			int nProBillCount = uF.parseToInt((String)hmEmployeeBillCount.get(strResourcId));
			int nProCountT = uF.parseToInt((String)hmEmployeeCountT.get(strResourcId));
			int nProBillCountT = uF.parseToInt((String)hmEmployeeBillCountT.get(strResourcId));
			
			//System.out.println("strProjectIdNew ===>> " +strProjectIdNew + " strProjectIdOld ===>> " + strProjectIdOld);
			if(strResourcIdNew!=null && !strResourcIdNew.equalsIgnoreCase(strResourcIdOld)) {
				nCount = 0;
			} else if(strResourcIdNew == null) {
				strResourcIdNew = "";
				if(!strResourcIdNew.equalsIgnoreCase(strResourcIdOld)) {
					nCount = 0;
				}
			}
			nCount++;
			
			Map<String, String> hmDates = (Map<String, String>)hmEmployee.get(strResourceId+"_"+strActivityId);
			if(hmDates==null)hmDates = new HashMap<String, String>();
			
			Map<String, String> hmDatesBillableHrs = (Map<String, String>)hmEmployeeBillableHrs.get(strResourceId+"_"+strActivityId);
			if(hmDatesBillableHrs == null) hmDatesBillableHrs = new HashMap<String, String>();
			
			Map<String, String> hmDateHrsIsApproved = (Map<String, String>)hmEmployeeHrsIsApproved.get(strResourceId+"_"+strActivityId);
			if(hmDateHrsIsApproved == null) hmDateHrsIsApproved = new HashMap<String, String>();
			
			Map<String, String> hmDateBillableHrsIsApproved = (Map<String, String>)hmEmployeeBillableHrsIsApproved.get(strResourceId+"_"+strActivityId);
			if(hmDateBillableHrsIsApproved == null) hmDateBillableHrsIsApproved = new HashMap<String, String>();
			
			Map<String, String> hmTaskDescri = (Map<String, String>)hmEmployeeTaskDescri.get(strResourceId+"_"+strActivityId);
			if(hmTaskDescri == null)hmTaskDescri = new HashMap<String, String>();
			
			
			Map<String, String> hmDateTaskId = (Map<String, String>)hmEmployeeTaskId.get(strResourceId+"_"+strActivityId);
			if(hmDateTaskId == null)hmDateTaskId = new HashMap<String, String>();
			
			%>
			
			<%for(int j=0; j<2; j++) {
				sb.replace(0, sb.length(), "");
				if(j==0) {
					sb.append("OFS"); 
				} else {
					sb.append("ONS");
				}
			%>
			
			<tr>
			<%if(j==0 && nCount==1) { 
			int rwspn = 2;
			int billrwspn = 0;
			if(nProBillCount>0) {
				billrwspn = nProBillCount * 2;
			}
			
			int billrwspnT = 0;
			if(nProBillCountT>0) {
				billrwspnT = nProBillCountT * 2;
			}
			%>
			
				<td rowspan="<%=(nProCount * rwspn) + billrwspn + (nProCountT * rwspn) + billrwspnT %>"><%=uF.showData((String)hmProjectResourceMap.get(strResourcId), "")%>  <br/></td>
			<% } %>
				<% for(int i=0; i<alDates.size(); i++) { %>

				<% String strText = "-";
					String isApproved = uF.showData((String)hmDateHrsIsApproved.get((String)alDates.get(i)+"_"+sb.toString()), strText);
					String isBillableApproved = uF.showData((String)hmDateBillableHrsIsApproved.get((String)alDates.get(i)+"_"+sb.toString()), strText);
					String strBgColor = null;
					
					if(alEmpCheckRosterWeektype.contains((String)request.getAttribute("strEmpId"))) {
						if(rosterWeeklyOffSet.contains(alDates.get(i))){
							strBgColor =IConstants.WEEKLYOFF_COLOR;
						}
					} else if(weeklyOffSet.contains(alDates.get(i))) {
						strBgColor =IConstants.WEEKLYOFF_COLOR;
					}
					if(strBgColor!=null) {
						strText = "W/O";
					}
					
					if(strBgColor==null) {
						strBgColor = (String)hmHolidayDates.get(uF.getDateFormat((String) alDates.get(i), IConstants.DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId);
						strText = "H";
					}
					if(strBgColor==null) {
						strBgColor = (String) hmLeavesColour.get((String)hmLeaves.get(alDates.get(i)));
						strText = (String)hmLeaveCode.get((String)hmLeaves.get(alDates.get(i))); 
					}
					
					if(uF.getDateFormat(currDate, IConstants.DATE_FORMAT).equals(uF.getDateFormat(alDates.get(i).toString(), IConstants.DATE_FORMAT))) {
						strBgColor = "#EFEFEF";
					}
					
					if(strText==null) {
						strText = "-";
					}
					
					if(i==0 && j==0) {
						int tskRwSpan = 2;
						if(hmTaskIsBillable != null && uF.parseToBoolean(hmTaskIsBillable.get(strResourceId+"_"+strActivityId))) {
							tskRwSpan += 2;
						}
					%>
					<td rowspan="<%=tskRwSpan %>">
					<% if(uF.parseToInt(strActivityId) > 0) { %>
						<strong><%=uF.showData((String)hmTasks.get(strResourceId+"_"+strActivityId+"_T"), "-") %></strong>
					<% } else { %>
						<%=uF.showData((String)hmTasks.get(strResourceId+"_"+strActivityId+"_T"), "-") %>
					<% } %>
					<% if((hmCheckTaskStatus!=null && hmCheckTaskStatus.get(strResourceId+"_"+strActivityId) != null) && strUserType !=null && (strUserType.equalsIgnoreCase(IConstants.MANAGER) || strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.ADMIN))) { %>
					<input type="checkbox" name="checkTaskId" id="checkTaskId" value="<%=strResourceId+":_:"+strActivityId%>" />
					<% } %>
					
					</td>
					<td rowspan="2">AH</td>
					<% } %>
					
					<%if(i==0) { %>
					<td><%=sb.toString()%></td>
					<% } %>
					
					<%
					String activity_id=null;
					String prod_id=null;
					String bgcolor=null;
					String dayStatus="";
					dayStatus = uF.showData((String)hmDates.get((String)alDates.get(i)+"_"+sb.toString()), strText);
					if(uF.parseToDouble(dayStatus) > 0) {
						dayStatus = uF.getTotalTimeMinutes100To60(dayStatus);
					}
					%>
					<td align="right" style="background-color: <%=bgcolor==null ? strBgColor:bgcolor %>">
						<%=dayStatus %>
					</td>
				<%
				double dblHrs = uF.parseToDouble((String)hmDates.get((String)alDates.get(i)+"_"+sb.toString()));
				dblHrs += uF.parseToDouble((String)hmTotal.get((String)alDates.get(i)));
				hmTotal.put((String)alDates.get(i), uF.formatIntoTwoDecimal(dblHrs)+"");
				%>
				
				<% } %>
			</tr>
			
		<% } %>
			
			
			<% if(hmTaskIsBillable != null && uF.parseToBoolean(hmTaskIsBillable.get(strResourceId+"_"+strActivityId))) { %>
				<%for(int j=0; j<2; j++) {
					sb.replace(0, sb.length(), "");
					if(j==0) {
						sb.append("OFS"); 
					} else {
						sb.append("ONS");
					}
				%> 
			
			<tr>
				<% for(int i=0; i<alDates.size(); i++) { %>

				<% 	String strText = "-";
					
					String isApproved = uF.showData((String)hmDateHrsIsApproved.get((String)alDates.get(i)+"_"+sb.toString()), strText);
					String isBillableApproved = uF.showData((String)hmDateBillableHrsIsApproved.get((String)alDates.get(i)+"_"+sb.toString()), strText);
					
					String strBgColor = null;
					if(alEmpCheckRosterWeektype.contains((String)request.getAttribute("strEmpId"))) {
						if(rosterWeeklyOffSet.contains(alDates.get(i))) {
							strBgColor =IConstants.WEEKLYOFF_COLOR;
						}
					} else if(weeklyOffSet.contains(alDates.get(i))) {
						strBgColor =IConstants.WEEKLYOFF_COLOR;
					}
					if(strBgColor!=null) {
						strText = "W/O";
					}
					
					if(strBgColor==null) {
						strBgColor = (String)hmHolidayDates.get(uF.getDateFormat((String) alDates.get(i), IConstants.DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId);
						strText = "H";
					}
					if(strBgColor==null) {
						strBgColor = (String) hmLeavesColour.get((String)hmLeaves.get(alDates.get(i)));
						strText = (String)hmLeaveCode.get((String)hmLeaves.get(alDates.get(i))); 
					}
					
					if(uF.getDateFormat(currDate, IConstants.DATE_FORMAT).equals(uF.getDateFormat(alDates.get(i).toString(), IConstants.DATE_FORMAT))) {
						strBgColor = "#EFEFEF";
					}
					
					if(strText==null) {
						strText = "-";
					}
					
					if(i==0 && j==0) {
					%>
					<td rowspan="2">BH</td>
					<% } %>
					
					<%if(i==0) { %>
					<td><%=sb.toString()%></td>
					<% } %>
					
					<%
					String activity_id = null;
					String prod_id = null;
					String bgcolor = null;

					String dayStatus = "";
					
					dayStatus = uF.showData((String)hmDatesBillableHrs.get((String)alDates.get(i)+"_"+sb.toString()), strText);
					if(uF.parseToDouble(dayStatus) > 0) {
						dayStatus = uF.getTotalTimeMinutes100To60(dayStatus);
					}
					%>
					<td align="right" style="background-color: <%=bgcolor==null ? strBgColor:bgcolor%>">
						
						<% 
						String taskId = hmDateTaskId.get((String)alDates.get(i)+"_"+sb.toString());
						
						if(isBillableApproved != null && (isBillableApproved.equals("1") || isBillableApproved.equals("2"))) {  //hmCheckUserApproval!=null && uF.parseToBoolean(hmCheckUserApproval.get(taskId+"_"+strSessionEmpId))==true &&  %>
							<%=dayStatus%>
						<% } else {
							if(dayStatus != null && (dayStatus.trim().equals("0.00") || uF.parseToDouble(dayStatus) > 0)) {
							if(isApproved!=null && (isApproved.equals("1") || isApproved.equals("2")) && isBillableApproved != null && isBillableApproved.equals("0") && strUserType != null && !strUserType.equals(IConstants.CUSTOMER)) { %>
								<%=dayStatus %>
						<% } else { %>
							<a href="CustomerProjectTimesheet.action?strResourceId=<%=strResourceId %>&frmDate=<%=(String)alDates.get(i)%>&toDate=<%=(String)alDates.get(i)%>&proId=<%=(String)request.getAttribute("proId") %>&proFreqId=<%=(String)request.getAttribute("proFreqId") %>&strActivity=<%=strActivityId %>&strActivityTaskId=<%=taskId %>&submit1=">
								<%=dayStatus%>
							</a>	
						<%}
						} else {
							out.println(dayStatus);
						}
							} %>
					</td>
				<%
				double dblBillableHrs = uF.parseToDouble((String)hmDatesBillableHrs.get((String)alDates.get(i)+"_"+sb.toString()));
				dblBillableHrs += uF.parseToDouble((String)hmTotalBillableHrs.get((String)alDates.get(i)));
				hmTotalBillableHrs.put((String)alDates.get(i), uF.formatIntoTwoDecimal(dblBillableHrs)+"");
				%>
				
				<% } %>
			</tr>
			<% } %>
		<% } %>
			
		<% 
		strResourcIdOld = strResourcIdNew;
		} %>
			
			
			<%
		
		String strEmployeeIdNewST = null;
		String strEmployeeIdOldST = null;
		int nCountST = 0;
		
		for(int st=0; alSubTaskIds != null && st<alSubTaskIds.size(); st++) {
			String strSubTaskId = alSubTaskIds.get(st);
			//System.out.println("strSubTaskId ===>> " + strSubTaskId);
			
			hmTasks = (Map)hmEmployeeTasks.get(strResourceId+"_"+strSubTaskId);
			if(hmTasks == null) hmTasks = new HashMap();
			
			if(hmTasks != null && !hmTasks.isEmpty()) {
			String strEmployeeIdST = (String)hmTasks.get(strResourceId+"_"+strSubTaskId+"_E");
			strEmployeeIdNewST = (String)hmTasks.get(strResourceId+"_"+strSubTaskId+"_E");
			int nProCountST = uF.parseToInt((String)hmEmployeeCount.get(strEmployeeIdST));
			int nProBillCountST = uF.parseToInt((String)hmEmployeeBillCount.get(strEmployeeIdST));
			
			if(strEmployeeIdNewST!=null && !strEmployeeIdNewST.equalsIgnoreCase(strEmployeeIdOldST)) {
				nCountST = 0;
			} else if(strEmployeeIdNewST==null) {
				strEmployeeIdNewST = "";
				if(!strEmployeeIdNewST.equalsIgnoreCase(strEmployeeIdOldST)) {
					nCountST = 0;	
				}
			}
			nCountST++;
			
			Map<String, String> hmDates = (Map<String, String>)hmEmployee.get(strResourceId+"_"+strSubTaskId);
			if(hmDates==null)hmDates = new HashMap<String, String>();
			
			Map<String, String> hmDatesBillableHrs = (Map<String, String>)hmEmployeeBillableHrs.get(strResourceId+"_"+strSubTaskId);
			if(hmDatesBillableHrs == null)hmDatesBillableHrs = new HashMap<String, String>();
			
			Map<String, String> hmDateHrsIsApproved = (Map<String, String>)hmEmployeeHrsIsApproved.get(strResourceId+"_"+strSubTaskId);
			if(hmDateHrsIsApproved == null)hmDateHrsIsApproved = new HashMap<String, String>();
			
			Map<String, String> hmDateBillableHrsIsApproved = (Map<String, String>)hmEmployeeBillableHrsIsApproved.get(strResourceId+"_"+strSubTaskId);
			if(hmDateBillableHrsIsApproved == null) hmDateBillableHrsIsApproved = new HashMap<String, String>();
			
			//System.out.println(" hmDateBillableHrsIsApproved ===>>>> " + hmDateBillableHrsIsApproved);
			
			Map<String, String> hmTaskDescri = (Map<String, String>)hmEmployeeTaskDescri.get(strResourceId+"_"+strSubTaskId);
			if(hmTaskDescri == null)hmTaskDescri = new HashMap<String, String>();
			
			Map<String, String> hmDateTaskId = (Map<String, String>)hmEmployeeTaskId.get(strResourceId+"_"+strSubTaskId);
			if(hmDateTaskId == null)hmDateTaskId = new HashMap<String, String>();
			
			%>
			
			<%for(int j=0; j<2; j++) {
				sb.replace(0, sb.length(), "");
				if(j==0) {
					sb.append("OFS"); 
				} else {
					sb.append("ONS");
				}
			%>
			
			
			<tr>
			
				<% for(int i=0; i<alDates.size(); i++) { %>
				<% String strText = "-";
					
					String isApproved = uF.showData((String)hmDateHrsIsApproved.get((String)alDates.get(i)+"_"+sb.toString()), strText);
					String isBillableApproved = uF.showData((String)hmDateBillableHrsIsApproved.get((String)alDates.get(i)+"_"+sb.toString()), strText);
					
					String strBgColor = null;
					
					if(alEmpCheckRosterWeektype.contains((String)request.getAttribute("strEmpId"))) {
						if(rosterWeeklyOffSet.contains(alDates.get(i))){
							strBgColor =IConstants.WEEKLYOFF_COLOR;
						}
					} else if(weeklyOffSet.contains(alDates.get(i))) {
						strBgColor =IConstants.WEEKLYOFF_COLOR;
					}
					if(strBgColor!=null) {
						strText = "W/O";
					}
					
					if(strBgColor==null) {
						strBgColor = (String)hmHolidayDates.get(uF.getDateFormat((String) alDates.get(i), IConstants.DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId);
						strText = "H";
					}
					if(strBgColor==null) {
						strBgColor = (String) hmLeavesColour.get((String)hmLeaves.get(alDates.get(i)));
						strText = (String)hmLeaveCode.get((String)hmLeaves.get(alDates.get(i))); 
					}
					
					if(uF.getDateFormat(currDate, IConstants.DATE_FORMAT).equals(uF.getDateFormat(alDates.get(i).toString(), IConstants.DATE_FORMAT))) {
						strBgColor = "#EFEFEF";
					}
					
					if(strText==null) {
						strText = "-";
					}
					
					if(i==0 && j==0) {
						int tskRwSpan = 2;
						if(hmTaskIsBillable != null && uF.parseToBoolean(hmTaskIsBillable.get(strResourceId+"_"+strSubTaskId))) {
							tskRwSpan += 2;
						}
					%>
					<td rowspan="<%=tskRwSpan %>"><%=uF.showData((String)hmTasks.get(strResourceId+"_"+strSubTaskId+"_T"), "-") %> [ST]
					<% if((hmCheckTaskStatus!=null && hmCheckTaskStatus.get(strResourceId+"_"+strSubTaskId) != null) && strUserType !=null && (strUserType.equalsIgnoreCase(IConstants.MANAGER) || strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.ADMIN))) { %>
					<input type="checkbox" name="checkTaskId" id="checkTaskId" value="<%=strResourceId+":_:"+strSubTaskId%>" />
					<% } %>
					
					</td>
					<td rowspan="2">AH</td>
					<% } %>
					
					<%if(i==0) { %>
					<td><%=sb.toString()%></td>
					<% } %>
					
					<%
					String activity_id = null;
					String prod_id = null;
					String bgcolor = null;
					String dayStatus="";
					dayStatus = uF.showData((String)hmDates.get((String)alDates.get(i)+"_"+sb.toString()), strText);
					if(uF.parseToDouble(dayStatus) > 0) {
						dayStatus = uF.getTotalTimeMinutes100To60(dayStatus);
					}
					%>
					<td align="right" style="background-color: <%=bgcolor==null ? strBgColor : bgcolor%>">
						<%=dayStatus%>
					</td>
				<%
				double dblHrs = uF.parseToDouble((String)hmDates.get((String)alDates.get(i)+"_"+sb.toString()));
				dblHrs += uF.parseToDouble((String)hmTotal.get((String)alDates.get(i)));
				hmTotal.put((String)alDates.get(i), uF.formatIntoTwoDecimal(dblHrs)+"");
				%>
				
				<% } %>
			</tr>
			
		<% } %>
			
			
			<% if(hmTaskIsBillable != null && uF.parseToBoolean(hmTaskIsBillable.get(strResourceId+"_"+strSubTaskId))) { %>
				<%for(int j=0; j<2; j++) {
					sb.replace(0, sb.length(), "");
					if(j==0) {
						sb.append("OFS"); 
					} else {
						sb.append("ONS");
					}
				%> 
			
			<tr>
				<% for(int i=0; i<alDates.size(); i++) { %>

				<% 	String strText = "-";
					
					String isApproved = uF.showData((String)hmDateHrsIsApproved.get((String)alDates.get(i)+"_"+sb.toString()), strText);
					String isBillableApproved = uF.showData((String)hmDateBillableHrsIsApproved.get((String)alDates.get(i)+"_"+sb.toString()), strText);
					
					String strBgColor = null;
					if(alEmpCheckRosterWeektype.contains((String)request.getAttribute("strEmpId"))) {
						if(rosterWeeklyOffSet.contains(alDates.get(i))) {
							strBgColor =IConstants.WEEKLYOFF_COLOR;
						}
					} else if(weeklyOffSet.contains(alDates.get(i))) {
						strBgColor =IConstants.WEEKLYOFF_COLOR;
					}
					if(strBgColor!=null) {
						strText = "W/O";
					}
					
					if(strBgColor==null) {
						strBgColor = (String)hmHolidayDates.get(uF.getDateFormat((String) alDates.get(i), IConstants.DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId);
						strText = "H";
					}
					if(strBgColor==null) {
						strBgColor = (String) hmLeavesColour.get((String)hmLeaves.get(alDates.get(i)));
						//strText = (String)hmLeaves.get((String)alDates.get(i));
						strText = (String)hmLeaveCode.get((String)hmLeaves.get(alDates.get(i))); 
					}
					
					if(uF.getDateFormat(currDate, IConstants.DATE_FORMAT).equals(uF.getDateFormat(alDates.get(i).toString(), IConstants.DATE_FORMAT))) {
						strBgColor = "#EFEFEF";
					}
					
					if(strText==null) {
						strText = "-";
					}
					
					if(i==0 && j==0) {
					%>
					<td rowspan="2">BH</td>
					<% } %>
					
					<%if(i==0) { %>
					<td><%=sb.toString()%></td>
					<% } %>
					
					<%
					String activity_id = null;
					String prod_id = null;
					String bgcolor = null;

					String dayStatus="";
					
					dayStatus=uF.showData((String)hmDatesBillableHrs.get((String)alDates.get(i)+"_"+sb.toString()), strText);
					if(uF.parseToDouble(dayStatus) > 0) {
						dayStatus = uF.getTotalTimeMinutes100To60(dayStatus);
					}
					%>
					<td align="right" style="background-color: <%=bgcolor==null ? strBgColor:bgcolor%>">
						
						<% 
						String taskId = hmDateTaskId.get((String)alDates.get(i)+"_"+sb.toString());
						
						if(isBillableApproved != null && (isBillableApproved.equals("1") || isBillableApproved.equals("2"))) { //hmCheckUserApproval!=null && uF.parseToBoolean(hmCheckUserApproval.get(taskId+"_"+strSessionEmpId))==true &&  %>
							<%=dayStatus%>
						<% } else {
							/* if(uF.parseToDouble(dayStatus) > 0) { */
							if(dayStatus != null && (dayStatus.trim().equals("0.00") || uF.parseToDouble(dayStatus) > 0)) {
								//System.out.println("Sub Task isApproved bill hrs ===>> " + isApproved);
							if(isApproved!=null && (isApproved.equals("1") || isApproved.equals("2")) && isBillableApproved != null && isBillableApproved.equals("0") && strUserType != null && !strUserType.equals(IConstants.CUSTOMER)) { %>
								<%=dayStatus %>
						<% } else { %>
							<a href="CustomerProjectTimesheet.action?strResourceId=<%=strResourceId %>&frmDate=<%=(String)alDates.get(i)%>&toDate=<%=(String)alDates.get(i)%>&proId=<%=(String)request.getAttribute("proId") %>&proFreqId=<%=(String)request.getAttribute("proFreqId") %>&strActivity=<%=strSubTaskId %>&strActivityTaskId=<%=taskId %>&submit1=">
								<%=dayStatus %>
							</a>	
						<% }
						} else {
							out.println(dayStatus);
						}
							} %>
					</td>
				<%
				double dblBillableHrs = uF.parseToDouble((String)hmDatesBillableHrs.get((String)alDates.get(i)+"_"+sb.toString()));
				dblBillableHrs += uF.parseToDouble((String)hmTotalBillableHrs.get((String)alDates.get(i)));
				hmTotalBillableHrs.put((String)alDates.get(i), uF.formatIntoTwoDecimal(dblBillableHrs)+"");
				%>
				
				<% } %>
			</tr>
			<% } %>
			<% } %>
			<%
			strEmployeeIdOldST = strEmployeeIdNewST;
				}
			}
			%>
			
				
			<% }
				}
			}
			%>
			
			<tr>
				<td colspan="2">Total Hrs.</td>
				<td></td>
				<td></td>
				<%for(int i=0; i<alDates.size(); i++) {
					String strText = "-";

					String dayTotalStatus = uF.showData((String)hmTotal.get((String)alDates.get(i)), "-");
						if(uF.parseToDouble(dayTotalStatus) > 0) {
							dayTotalStatus = uF.getTotalTimeMinutes100To60(dayTotalStatus);
						}
				%>
					<td align="right">
						<input type="hidden" name="strDate" value="<%=(String)alDates.get(i)%>" />
						<input type="hidden" name="strTime" value="<%=uF.showData((String)hmTotal.get((String)alDates.get(i)), "0")%>" />
						
						<%=dayTotalStatus %>
					</td>
				<% } %>
			</tr>
			
			<% if(hmEmployeeBillableHrs != null && !hmEmployeeBillableHrs.isEmpty() && hmEmployeeBillableHrs.size()>0) { %>
			<tr>
				<td colspan="2">Total Billable Hrs.</td>
				<td></td>
				<td></td>
				<%for(int i=0; i<alDates.size(); i++) { 
					String dayTotalStatus = uF.showData((String)hmTotalBillableHrs.get((String)alDates.get(i)), "-");
					if(uF.parseToDouble(dayTotalStatus) > 0) {
						dayTotalStatus = uF.getTotalTimeMinutes100To60(dayTotalStatus);
					}
				%>
					<%-- <td align="right" style="background-color: <%=bgcolor==null ? strBgColor:bgcolor%>"> --%>
					<td align="right">
						<input type="hidden" name="strBillableTime" value="<%=uF.showData((String)hmTotalBillableHrs.get((String)alDates.get(i)), "0")%>" />
						
						<%=dayTotalStatus %>
					</td>
				<% } %>
			</tr>
			<% } %>
			
		</table>
		</div>


		<input type="hidden" name="strPaycycle" value="<%=request.getAttribute("strPaycycle") %>" />
		<input type="hidden" name="checkTask" id="checkTask" value=""/>
		<div style="float:right">
			<input type="hidden" name="policy_id" id="policy_id" value="<%=request.getAttribute("policy_id") %>"/>
		<% if(strUserType!=null && (strUserType.equalsIgnoreCase(IConstants.MANAGER) || strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.CUSTOMER))) { //empApproved!=null  && uF.parseToInt(nSubmited)==0 %>
			<input type="hidden" name="type" id="type"/>
			<input type="hidden" name="strEmpId" value="<%=request.getParameter("strEmpId") %>"/>
			<input type="hidden" name="frmDate" value="<%=request.getParameter("frmDate") %>"/>
			<input type="hidden" name="toDate" value="<%=request.getParameter("toDate") %>"/>
			<input type="hidden" name="strProject" value="<%=request.getParameter("strProject") %>"/>
			<input type="hidden" name="strActivity" value="<%=request.getParameter("strActivity") %>"/>
			<s:hidden name="pageType" id="pageType" />
			
			<% String usrType = "E";
				if(strUserType!=null && strUserType.equalsIgnoreCase(IConstants.CUSTOMER)) {
					usrType = "C";
				}
			%>
			<% if((pendingProBillableApprovedFlag && !pendingCustApprovedFlag) || (pendingProBillableApprovedFlag && pendingCustApprovedFlag)) { %>
				<% if(strUserType!=null && strUserType.equalsIgnoreCase(IConstants.CUSTOMER)) { %>
					<input type="button" align="right" value="Deny" class="cancel_button" onclick="customerTimesheetDeny('<%=(String)request.getAttribute("proId") %>', '<%=(String)request.getAttribute("proFreqId") %>');">
				<% } %>
				<% if(strUserType!=null && !strUserType.equalsIgnoreCase(IConstants.CUSTOMER)) { %>	
					<input type="button" align="right" value="Approve & Send to Customer" class="btn btn-primary" onclick="checkApprovalStatus('approveAndSendToCustomer', '<%=usrType %>');">
				<% } %>	
					<input type="button" align="right" value="Approve for Billing" class="btn btn-primary" onclick="checkApprovalStatus('approveForBilling', '<%=usrType %>');">
				<% } else if(!pendingProBillableApprovedFlag && pendingCustApprovedFlag) { %>
					<% if(strUserType!=null && strUserType.equalsIgnoreCase(IConstants.CUSTOMER)) { %>
						<input type="button" align="right" value="Deny" class="cancel_button" onclick="customerTimesheetDeny('<%=(String)request.getAttribute("proId") %>', '<%=(String)request.getAttribute("proFreqId") %>');">
					<% } %>
					<input type="button" align="right" value="Approve for Billing" class="btn btn-primary" onclick="checkApprovalStatus('approveForBilling', '<%=usrType %>');">
				<% } %>	
			<% } %>
		</div>
	
	</s:form>

		<div style="float:left; width: 100%;">
		<div>AH:- Actual Hours</div>
		<div>BH:- Billable Hours</div>
		<div>ONS:- Onsite</div>
		<div>OFS:- Offsite</div>
		</div>
		<br /><br />
		
	</div>
	</section>
	</div>
</section>

<script type="text/javascript">

	function checkApprovalStatus(type, usrType) {
			
		var checkTaskId = document.getElementsByName('checkTaskId');
		var selectID="";
		var j=0;
		for(var i=0;i<checkTaskId.length;i++) {
			
			if(checkTaskId[i].checked ==true) {
			  if(j==0) {
				  selectID=checkTaskId[i].value;
				  j++;
			  } else {
				  selectID+=","+checkTaskId[i].value;
				  j++;
			  }
			}
		}
		
		document.getElementById("checkTask").value=selectID;
		//alert(document.getElementById("checkTask").value);
		if(selectID=="" && usrType == 'E') {
			alert("Please, select the task?");
		} else {
			var strMsg = 'Are you sure, you want to approve this timesheet for billing?';
			if(type == '') {
				strMsg = 'Are you sure, you want to approve this timesheet and send to customer?';
			}
			if(confirm(strMsg)) {
				document.getElementById("type").value = type;
				document.getElementById("ftimesheet").submit();
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
	
</script>

<div class="modal" id="modalInfo" role="dialog">
    <div class="modal-dialog">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">-</h4>
            </div>
            <div class="modal-body" style="height:auto;overflow-y:auto;padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>

<div id="addLeave"></div>
<div id="taskDescritopnDiv"></div>
<div id="denyTimesheetDiv"></div>

