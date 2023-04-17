
<div id="divResult">
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
<%@page import="com.konnect.jpms.util.UtilityFunctions" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>



<%	String submitType = (String)request.getAttribute("submitType"); 
	System.out.println("submitType ===>> " + submitType);
	if(submitType == null || submitType.equalsIgnoreCase("null") || submitType.equals("")) {
%>
	<script type="text/javascript" src="scripts/charts/jquery.min.js"> </script>
<% } %>

 <%
	UtilityFunctions uF = new UtilityFunctions(); 
	CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
	
	boolean claimExtraWorkFlag = (Boolean)request.getAttribute("claimExtraWorkFlag");
	
	String strTaskList = (String)request.getAttribute("strTaskList");
	String pageType = (String)request.getAttribute("pageType");
	
	String fillUserType = (String) request.getAttribute("fillUserType");
	
	String strUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
	String strSessionUserType = (String)session.getAttribute(IConstants.USERTYPE);
	String strSessionEmpId = (String)session.getAttribute(IConstants.EMPID);
	String currDate = (String)request.getAttribute("currDate");
	 
	Map hmReimbursementDetails = (Map)request.getAttribute("hmReimbursementDetails");
	
	Map<String, Map<String, String>> hmProjects = (Map<String, Map<String, String>>)request.getAttribute("hmProjects"); 
	Map<String, Map<String, String>> hmProjectsBillableHrs = (Map<String, Map<String, String>>)request.getAttribute("hmProjectsBillableHrs");
	Map<String, Map<String, String>> hmProjectsHrsIsApproved = (Map<String, Map<String, String>>) request.getAttribute("hmProjectsHrsIsApproved");
	Map<String, Map<String, String>> hmProjectsTaskDescri = (Map<String, Map<String, String>>)request.getAttribute("hmProjectsTaskDescri");
	
	Map<String, Map<String, String>> hmProjectsTaskId = (Map<String, Map<String, String>>) request.getAttribute("hmProjectsTaskId");
	
	Map<String, String> hmTaskIsBillable = (Map<String, String>) request.getAttribute("hmTaskIsBillable");
	
	Map hmProjectTasks = (Map)request.getAttribute("hmProjectTasks");
	 
	Map hmClientMap = (Map)request.getAttribute("hmClientMap");
	Map hmProjectMap = (Map)request.getAttribute("hmProjectMap");
	Map hmProjectCount = (Map)request.getAttribute("hmProjectCount");
	Map hmProjectBillCount = (Map)request.getAttribute("hmProjectBillCount");
	
	List alDates = (List)request.getAttribute("alDates");
	
	Map hmLeaves = (Map)request.getAttribute("hmLeaves");
	//System.out.println("hmLeaves ===>> " + hmLeaves);
	
	Map hmLeaveCode = (Map)request.getAttribute("hmLeaveCode");
	Map<String, Set<String>> hmWeekendMap = (Map<String, Set<String>>)request.getAttribute("hmWeekendMap");
	if(hmWeekendMap == null) hmWeekendMap = new HashMap<String, Set<String>>();
	Map<String, Set<String>> hmRosterWeekEndDates = (Map<String, Set<String>>)request.getAttribute("hmRosterWeekEndDates");
	if(hmRosterWeekEndDates == null) hmRosterWeekEndDates = new HashMap<String, Set<String>>();
	List<String> alEmpCheckRosterWeektype = (List<String>)request.getAttribute("alEmpCheckRosterWeektype");
	if(alEmpCheckRosterWeektype == null) alEmpCheckRosterWeektype = new ArrayList<String>();
	
	Map hmHolidayDates = (Map)request.getAttribute("hmHolidayDates");
	Map hmLeavesColour = (Map)request.getAttribute("hmLeavesColour");
	String strWLocationId = (String)request.getAttribute("strWLocationId");
	Map<String,String> hmEmpEdit=(Map<String,String>)request.getAttribute("hmEmpEdit");
	Map<String, String> hmActivityIDByDate =(Map<String, String>)request.getAttribute("hmActivityIDByDate");
	Map<String, String> hmActivityProjectID =(Map<String, String>)request.getAttribute("hmActivityProjectID");
	Map<String, String> hmProjectColor =(Map<String, String>)request.getAttribute("hmProjectColor");
	//Map<String, String> hmProject =(Map<String, String>)request.getAttribute("hmProject");
	
	
	Map<String, String> hmApprovedDate =(Map<String, String>)request.getAttribute("hmApprovedDate");
	
	Map<String, List<String>> hmCheckEmp =(Map<String, List<String>>)request.getAttribute("hmCheckEmp");
	
	if(hmCheckEmp == null) hmCheckEmp = new HashMap<String, List<String>>();
		
	
	Map<String, String> hmEmpByLocation =(Map<String, String>)request.getAttribute("hmEmpByLocation");
	
	Map<String, String> hmCheckUserApproval =(Map<String, String>)request.getAttribute("hmCheckUserApproval");
	
	//Map<String, String> hmEmpLeave =(Map<String, String>)request.getAttribute("hmEmpLeave");
	//Map<String, String> hmLeaveTypeCode =(Map<String, String>)request.getAttribute("hmLeaveTypeCode");
	
	Map<String, String> hmCheckTaskStatus =(Map<String, String>)request.getAttribute("hmCheckTaskStatus");	
	
	/* List<String> approvedList=(List<String>)request.getAttribute("approvedList");
	if(approvedList==null) approvedList=new ArrayList<String>(); */
	
	List<String> unlockList=(List<String>)request.getAttribute("unlockList");
	if(unlockList==null) unlockList=new ArrayList<String>();
	
	String empApproved = (String)request.getAttribute("empApproved");
	String nApproved = (String)request.getAttribute("nApproved");
	String nApproved1 = (String)request.getAttribute("nApproved1");
	String nSubmited = (String)request.getAttribute("nSubmited");
	String tsApproved = (String)request.getAttribute("tsApproved");
	
	Boolean checkPayroll=(Boolean)request.getAttribute("checkPayroll"); 
	List<String> compLeaveList=(List<String>)request.getAttribute("compLeaveList");
	if(compLeaveList==null) compLeaveList=new ArrayList<String>(); 
	//System.out.println("compLeaveList ===>> " + compLeaveList);
	
	List<String> denyList=(List<String>)request.getAttribute("denyList");
	if(denyList==null) denyList=new ArrayList<String>(); 
	//System.out.println("denyList ===>> " + denyList);
	
	Map hmLeaveConstant = (Map)request.getAttribute("hmLeaveConstant");
	
	List<String> applyLeaveList=(List<String>)request.getAttribute("applyLeaveList");
	if(applyLeaveList==null) applyLeaveList=new ArrayList<String>();
	//System.out.println("applyLeaveList ===>> " + applyLeaveList);
	
	int checkboxcnt =(Integer)request.getAttribute("i");
	
	Map<String, String> hmCheckLeaveType=(Map<String, String>)request.getAttribute("hmCheckLeaveType");
	if(hmCheckLeaveType==null) hmCheckLeaveType=new HashMap<String, String>();
	
	//System.out.println("hmCheckLeaveType ===>> " + hmCheckLeaveType);
	
	String maxTaskDate=(String)request.getAttribute("maxTaskDate");
	List<String> holidayCountList=(List<String>) request.getAttribute("holidayCountList");
	if(holidayCountList==null) holidayCountList=new ArrayList<String>();
	
	String minTaskDate=(String)request.getAttribute("minTaskDate");
	
	Map<String, Map<String, String>> hmProjectTasksT = (Map<String, Map<String, String>>)request.getAttribute("hmProjectTasksT");
	Map<String, String> hmDateT = (Map<String, String>)request.getAttribute("hmDateT");
	Map<String, String> hmDateTaskIdT = (Map<String, String>)request.getAttribute("hmDateTaskIdT");
	Map<String, String> hmDateBillableHrsT = (Map<String, String>)request.getAttribute("hmDateBillableHrsT");
	Map<String, Map<String, String>> hmProjectsT = (Map<String, Map<String, String>>)request.getAttribute("hmProjectsT");
	Map<String, Map<String, String>> hmProjectsBillableHrsT = (Map<String, Map<String, String>>)request.getAttribute("hmProjectsBillableHrsT");
	Map<String, String> hmTaskIsBillableT = (Map<String, String>)request.getAttribute("hmTaskIsBillableT");
	
	Map<String, String> hmProjectCountT = (Map<String, String>)request.getAttribute("hmProjectCountT");
	Map<String, String> hmProjectBillCountT = (Map<String, String>)request.getAttribute("hmProjectBillCountT");
	
	List<String> alTaskIds = (List<String>) request.getAttribute("alTaskIds");
	Map<String, List<String>> hmTaskAndSubTaskIds = (Map<String, List<String>>) request.getAttribute("hmTaskAndSubTaskIds");
	
	List<String> filledDateList = (List<String>) request.getAttribute("filledDateList");
	if(filledDateList == null) filledDateList = new ArrayList<String>();
	
	Set<String> stExtraWorkDates = (Set<String>)request.getAttribute("stExtraWorkDates");
	if(stExtraWorkDates == null) stExtraWorkDates = new HashSet<String>();
%>

<style>
.ui-state-default, .ui-multiselect-menu{
	width: 175px;
	font-size: 12PX !important;
}

/* .modal1 {
    bottom: 0;
    display: none;
    left: 0;
    outline: 0 none;
    overflow: hidden;
    position: fixed;
    right: 0;
    top: 0;
    z-index: 1050;
} */

/* .modal-header1 h4 {
    font-size: 16px;
} */

.modal-title1 {
    line-height: 1.42857;
    margin: 0;
    padding-left: 31px;
    font-weight: bold;
}

/* .modal-header1 {
    background-color: #6495c0;
    border-color: #6495c0;
    color: #fff;
} */

.modal-header1, .modal-body1, .modal-footer1 {
    padding-bottom: 10px;
    padding-top: 10px;
}

.modal-body1 {
    min-height: 175px;
    padding: 15px;
    position: relative;
}

/* .modal-content1 {
    box-shadow: 0 5px 15px rgba(0, 0, 0, 0.5);
    background-clip: padding-box;
    background-color: #fff;
    border: 1px solid rgba(0, 0, 0, 0.2);
    border-radius: 6px;
    box-shadow: 0 3px 9px rgba(0, 0, 0, 0.5);
    outline: 0 none;
    position: relative;
} */

</style>


<script>

	<%-- var cxtpath = '<%=request.getContextPath()%>';
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

 --%>
 
	$(document).ready(function() {
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
	});
 
	function callDatePicker() {
		//$("input[name=strDate]").datepicker({dateFormat: 'dd/mm/yy'});
		$("input[name=strDate]").datepicker({
			format : 'dd/mm/yyyy', minDate:"<%=request.getAttribute("frmDate")%>", maxDate: "<%=request.getAttribute("toDate")%>" 
		});
	}


	function removeTask(divElement, removeId, taskId) {
		if(confirm('Are you sure you want to delete this entry?')) {
			getContent('divElement', 'DailyTimesheet.action?D=D&strTaskId='+taskId);
			var remove_elem = removeId;
			var row_skill = document.getElementById(remove_elem);
			document.getElementById(divElement).removeChild(row_skill);
		}
	}
	
	var cnt=<%=alDates.size()%>;
	var checkboxcnt=<%=checkboxcnt%>;
	
	
function addTask(divElement) {
	var divE = document.getElementById(divElement).firstChild;
	var strDate = '';
	var strTime = '';
	checkboxcnt++;
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
    divTag.setAttribute("style", "float:left;width:750px;padding-bottom:10px;");
	divTag.innerHTML = 
		"<div style=\"float:left;width:90px;\"><input type=\"hidden\" name=\"taskId\" value=\"0\" ><input type=\"text\" style=\"width:82px !important;\" name=\"strDate\" id=\"strDate_"+cnt+"\" value=\""+strDate+"\"></div>"+
		"<div style=\"float:left;width:225px;\">"+
			"<select name=\"strTask\" class=\"validateRequired\" onchange=\"checkIsBillable(this.value, '"+checkboxcnt+"','"+cnt+"');\">"+
			"<%=strTaskList%>"+
			"</select>"+
		"</div>"+
		
		"<div style=\"float:left;width:120px;\"><input type=\"text\" style=\"width:62px !important;\" name=\"strTime\" id=\"strTime_"+cnt+"\" value=\"0.0\" onkeyup=\"checkAndAddBillableTime('"+cnt+"','"+checkboxcnt+"');\" onblur=\"checkHours('"+cnt+"');\"></div>"+
		"<div style=\"float: left; width: 55px; text-align: center;\"><input type=\"hidden\" style=\"width: 30px;\"  id=\"strBillableYesNoT_"+checkboxcnt+"\" name=\"strBillableYesNoT\" value=\"1\">"+
		"<span id=\"isBillableSapn"+checkboxcnt+"\"><input type=\"checkbox\" id=\"strBillableYesNo_"+checkboxcnt+"\" name=\"strBillableYesNo\" onchange=\"setBillableValue('"+cnt+"','"+checkboxcnt+"')\" value=\"0\" checked></span></div>"+
		"<div style=\"float:left;width:85px;\"><input type=\"text\" style=\"width:62px !important;\" name=\"strBillableTime\" id=\"strBillableTime_"+cnt+"\" value=\"0.0\" onblur=\"checkBillHours('"+cnt+"');\"></div>"+
		"<div style=\"float: left; width: 50px; text-align: center;\"><input type=\"hidden\" style=\"width: 30px;\"  id=\"strTaskOnOffSiteT_"+checkboxcnt+"\" name=\"strTaskOnOffSiteT\" value=\"1\"><input type=\"checkbox\" id=\"strTaskOnOffSite_"+checkboxcnt+"\" name=\"strTaskOnOffSite\" onchange=\"setValue('"+checkboxcnt+"')\" value=\"0\" checked></div>"+
		"<div style=\"float:left;width:20px;\">&nbsp;</div>"+
		"<div style=\"float:left;\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\" title=\"Remove Task\" onclick=\"removeTask('"+divElement+"','row_task_"+cnt+"',0)\" ></i></div>"+

		"<div style=\"float:left;width:20px; padding-left: 10px;\"><input type=\"hidden\" id=\"hidetaskDescription_"+cnt+"\" name=\"hidetaskDescription\" value=\"0\">"
		+"<a href=\"javascript: void(0);\" onclick=\"showHideDescription('"+cnt+"');\">"+
		"<span id=\"PdownarrowSpan_"+cnt+"\" style=\"float: left; margin-right: 3px;\"><i class=\"fa fa-angle-down\" aria-hidden=\"true\" title=\"Click here to add task description\"/></i></span>"+
		"<span id=\"PuparrowSpan_"+cnt+"\" style=\"display: none; float: left; margin-right: 3px;\"><i class=\"fa fa-angle-up\" aria-hidden=\"true\" title=\"Click here to hide task description\"/></i></span>"+
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

	
	function setBillableValue(cnt, checkboxcnt) {
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
		var checkboxes = document.getElementsByName("compOff");
		var extraDate = "";
		var checkCnt = 0;
		var extraWorkCnt = '<%=stExtraWorkDates.size() %>';
		//alert("extraWorkCnt ===>> " + extraWorkCnt);
		for (var i=0, n=checkboxes.length;i<n;i++) {
		    if (checkboxes[i].checked) {
		    	extraDate = checkboxes[i].value;
		    	<% Iterator<String> it = stExtraWorkDates.iterator();
		    		while(it.hasNext()) {
		    			String extraDts = it.next();
		    	%>
		    	var extraDateCheck = '<%=extraDts %>';
		    		if(extraDate == extraDateCheck) {
		    			checkCnt++;
		    		}
		    	<% } %>
		    }
		}
		//alert("checkCnt ===>> " + checkCnt);
		
		if(extraWorkCnt != checkCnt) {
			//alert("Please select checkbox for apply extra work where as you fill the timesheet on holiday/ weekly off.");
			if(confirm('Please select checkbox for apply extra work where as you fill the timesheet on holiday/ weekly off.')) {
	 			if(dates != '0') { 
					if(confirm('KINDLY CHECK YOUR TIMESHEET. \n BLANK ENTRIES ' + dates+'')) {
						if(confirm('Are you sure you want to submit your whole timesheet? \n You will be unable to modify it once you submit. \n Click Ok to submit and Cancel to modify your timesheet.')) {
							//return true;
							$("#myModal").modal('show');
						} else {
							return false;
						}
					}
				} else {
					if(confirm('Are you sure you want to submit your whole timesheet? \n You will be unable to modify it once you submit. \n Click Ok to submit and Cancel to modify your timesheet.')) {
						//return true;
						$("#myModal").modal('show');
					} else {
						return false;
					}
				}
			} else {
				return false;
			}
		} else {
			if(dates != '0') { 
				if(confirm('KINDLY CHECK YOUR TIMESHEET. \n BLANK ENTRIES ' + dates+'')) {
					if(confirm('Are you sure you want to submit your whole timesheet? \n You will be unable to modify it once you submit. \n Click Ok to submit and Cancel to modify your timesheet.')) {
						//return true;
						$("#myModal").modal('show');
					} else {
						return false;
					}
				}
			} else {
				if(confirm('Are you sure you want to submit your whole timesheet? \n You will be unable to modify it once you submit. \n Click Ok to submit and Cancel to modify your timesheet.')) {
					//return true;
					$("#myModal").modal('show');
				} else {
					return false;
				}
			}
		}
	}
	
	
	function copyTaskId(val, cnt) {
		//alert(val+" "+cnt);
		document.frmDailyTimesheet.strTaskOnOffSiteT[cnt].value = val;
	}
	

	function getReimbursementDetails(pro_id, emp_id,d1,d2) {
		//alert("d1 "+d1);
		//alert("d2 "+d2);
		/* var dialogEdit = '#addproject'; 
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
		$(dialogEdit).dialog('open'); */
		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$("#modalInfo").show();
		$(".modal-title").html('Reimbursement Summary');
		$.ajax({
			url : 'ProReimbursementSummary.action?pro_id='+pro_id+'&emp_id='+emp_id+'&strD1='+d1+'&strD2='+d2,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}  
	
	
function checkBillHours(count) {
	var arrDates = document.getElementsByName('strDate');
	var arrTime = document.getElementsByName('strTime');
	var arrBillableTime = document.getElementsByName('strBillableTime');
	var currDate = document.getElementById('strDate_'+count).value;
	var arrTotal = {}; 
	
	//alert("currDate ===>> " + currDate);
	
	for(i=0; i<arrDates.length; i++) {
		//alert("arrDates[i].value ===>> " + arrDates[i].value);
		var time = arrTotal["\""+arrDates[i].value+"\""];
		var totalTime = 0;
		//alert("time ===>> " + time);
		if(time!=undefined) {
			totalTime = parseFloat(time) + parseFloat(arrBillableTime[i].value);	
		} else {
			totalTime = parseFloat(arrBillableTime[i].value);
		}
		
		//alert("totalTime ===>> " + totalTime);
		var strbilltime = arrBillableTime[i].value;
		//alert("strbilltime ===>> " + strbilltime);
		var strtime = arrTime[i].value;
		//alert("strtime ===>> " + strtime);
		
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
	
	//alert("arrDates.length ==>>> " + arrDates.length);
	for(i=0; i<arrDates.length; i++) {
		//alert("arrDates[i].value ==>>> " + arrDates[i].value);
		var time = arrTotal["\""+arrDates[i].value+"\""];
		var totalTime = 0;
		//alert("time ==>>> " + time);
		
		if(time!=undefined) {
			if(arrTime[i].value == '') {
				
			} else {
				totalTime = parseFloat(time) + parseFloat(arrTime[i].value);
			}
		} else {
		if(arrTime[i].value == '') {
				
			} else {
				totalTime = parseFloat(arrTime[i].value);
			}
		}
		//alert("totalTime ==>>> " + totalTime);
		
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
	      	
	        //Close Popups and Fade Layer
	        $('a.close, #fade').live('click', function() { // When clicking on the close or fade layer...
	            $('#fade , .popup_block').fadeOut(function() {
	                $('#fade, a.close').remove();  //fade them both out
	            });
	            return false;
	        });
	        return false;
	    });

	});


	function executeActions(value) {
		var strPaycycle = document.getElementById('strPaycycle').value;
		if(value == 1) {
			window.location = 'Reimbursements.action?pageType=TS&strPaycycle='+strPaycycle;
		} else if(value == 2) {
			window.location = 'LeaveEncashment.action?pageType=TS&strPaycycle='+strPaycycle;
		} else if(value == 3) {
			//alert("Kindly apply leave/ client weekly off through Workrig. Work in progress to upgrade this functionality in taskrig.");
			addLeave('false', strPaycycle);
		} else if(value == 4) {
			//alert("Kindly apply leave/ client weekly off through Workrig. Work in progress to upgrade this functionality in taskrig.");
			addLeave('true', strPaycycle);
		}
	}

	
	function addLeave(isConstant, strPaycycle) {
		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$("#modalInfo").show();
		$(".modal-title").html('Add New Leave');
		$.ajax({
			url : 'ApplyLeavePopUp.action?isCompensate=false&isConstant='+isConstant+'&type=timesheet&strPaycycle='+strPaycycle,
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
			url : 'GetTaskDescription.action?taskDescription='+value,
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
		var strEmpId = document.getElementById("strEmpId").value;
		//alert("strEmpId ===>> " + strEmpId);
		if(client_id.length > 0) {
			var action = 'GetProjectClientTask.action?client_id=' + client_id+'&strEmpId='+strEmpId;
			getContent('myProject', action);
			setTimeout(function(){ 
				$("#strProject").multiselect().multiselectfilter(); 
			}, 1000);
		}
	}
	

	function getTaskList() {
		var project_id = getSelectedValue("strProject");
		var strEmpId = document.getElementById("strEmpId").value;
		if(client_id.length > 0) {
			var action = 'GetProjectClientTask.action?project_id=' + project_id+'&strEmpId='+strEmpId;
			getContent('myTask', action);
		}
	}


	function isNumberKey(evt) {
	   var charCode = (evt.which) ? evt.which : event.keyCode;
	   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46)
	      return false;
	
	   return true;
	}
	
	
function sortByEmpTimesheets(val) { 
	//document.frmTimesheet.sortBy.value = document.getElementById('sortBy1').value;
	//alert("val ===>> " + val);
	if(val != '') {
		document.frmTimesheet.filterBy.value = val;
	}
	var cnt = 0;
	if(val == 'O') {
		var strYear = document.getElementById('strYear').value;
		var strMonth = document.getElementById('strMonth').value;
		if(strYear == '' && strMonth == '') {
			alert("Please, select year and month.");
			cnt = 1;
		} else if(strYear == '') {
			alert("Please, select year.");
			cnt = 1;
		} else if(strMonth == '') {
			alert("Please, select month.");
			cnt = 1;
		}
	}
	if(parseInt(cnt) == 0) {
		var form_data = $("#frmTimesheet").serialize();
		$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: 'DailyTimesheet.action?submitType=LOAD',
			data: form_data,
			success: function(result) {
				$("#divResult").html(result);
	   		}
		});
	}
}


function addTaskActivity() {
	var form_data = $("#frmTimesheet").serialize();
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'DailyTimesheet.action?submitType=LOAD&submit1=Add Activity',
		data: form_data,
		success: function(result) {
			$("#divResult").html(result);
   		}
	});
}


function taskDaywiseTimeUpdate(strPaycycle, strEmpId, frmDate, toDate, strProject, strActivity, strActivityTaskId, filterBy, strYear, strMonth, pageType, fillUserType) {
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		url: 'DailyTimesheet.action?strPaycycle='+strPaycycle+'&strEmpId='+strEmpId+'&frmDate='+frmDate+'&toDate='+toDate+'&strProject='+strProject
				+'&strActivity='+strActivity+'&strActivityTaskId='+strActivityTaskId+'&filterBy='+filterBy+'&strYear='+strYear+'&strMonth='+strMonth
				+'&activityType=E&submit1=&pageType='+pageType+'&fillUserType='+fillUserType+'&submitType=LOAD',
		success: function(result) {
			$("#divResult").html(result);
   		}
	});
}


function saveTimesheet() { 
	var form_data = $("#frmDailyTimesheet").serialize();
	//alert("form_data ========>> " + form_data);
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'DailyTimesheet.action?submitType=LOAD&save=Save Timesheet',
		data: form_data,
		success: function(result) {
			$("#divResult").html(result);
   		}
	});
}


function checkIsBillable(taskId, checkboxcnt, cnt) {
	$.ajax({
		url : 'GetTaskDescription.action?taskId='+taskId,
		cache : false,
		success : function(data) {
			var fromData = data.trim();
			//alert("fromData ===>> " + fromData);
			if(fromData == 'NO') {
				document.getElementById("strBillableYesNo_"+checkboxcnt).disabled = 'true';
				document.getElementById("strBillableYesNo_"+checkboxcnt).checked = '';
				//document.getElementById("isBillableSapn"+checkboxcnt).style.display = 'none';
				document.getElementById("strBillableYesNoT_"+checkboxcnt).value = "0";
				document.getElementById("strBillableTime_"+cnt).value = "0";
			} else {
				//document.getElementById("isBillableSapn"+checkboxcnt).style.display = 'inline';
				document.getElementById("strBillableYesNo_"+checkboxcnt).disabled = '';
				document.getElementById("strBillableYesNo_"+checkboxcnt).checked = 'true';
				document.getElementById("strBillableYesNoT_"+checkboxcnt).value = "1";
				document.getElementById("strBillableTime_"+cnt).value = "0";
			}
			
		}
	});
}


</script>

<%-- <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Timesheet" name="title" />
</jsp:include> --%>


        <!-- Main content -->
        <section class="content" style="padding-top: 0px !important;" >
          <!-- title row -->
	        <div class="row">
           <%
				String filterBy = (String) request.getAttribute("filterBy");
				String strYear = (String) request.getAttribute("strYear");
				String strMonth = (String) request.getAttribute("strMonth");
				
				Map<String, String> hmFeatureStatus = (Map<String, String>)request.getAttribute("hmFeatureStatus");
			%>
           <% if(strSessionUserType!=null && !strSessionUserType.equalsIgnoreCase(IConstants.EMPLOYEE)) { %>
			<div class="col-md-12">
				<div class="box box-body" style="margin-bottom: 0px;">
					<span style="float: left; font-weight: 600;"><%=uF.showData((String)request.getAttribute("empNameTitle"), "-") %></span>
					<a class="fa fa-file-excel-o" style="float: right; margin-right: 30px;" href="GenerateTimeSheet1.action?mailAction=sendMail&empid=<%=request.getAttribute("empid")%>&datefrom=<%=request.getAttribute("datefrom")%>&dateto=<%=request.getAttribute("dateto")%>&downloadSubmit=0">&nbsp;</a>
				</div>
			</div>
			<% } %>
			
			<%=uF.showData((String)session.getAttribute(IConstants.MESSAGE), "") %>
			<% session.removeAttribute(IConstants.MESSAGE); %>
			<s:form name="frmTimesheet" id="frmTimesheet" action="DailyTimesheet" theme="simple">
				<div class="col-md-12">
					<div class="box box-warning direct-chat direct-chat-warning collapsed-box" style="margin-bottom: 0px;">
						<div class="box-header with-border">
							<h4 class="box-title" style="font-size: 14px;"><%=(String)request.getAttribute("selectedFilter") %></h4>
							<div class="box-tools pull-right">
								<button data-widget="collapse" class="btn btn-box-tool"><i class="fa fa-plus"></i></button>
							</div>
						</div><!-- /.box-header -->
						<div class="box-body">
					    	<s:hidden name="emp_id" id="emp_id" />
					    	<s:hidden name="strEmpId" id="strEmpId" />
					    	<s:hidden name="filterBy" id="filterBy" />
					    	<s:hidden name="strMinDate" id="strMinDate" />
					    	<s:hidden name="strMaxDate" id="strMaxDate" />
					    	<s:hidden name="pageType" id="pageType" />
					    	<s:hidden name="fillUserType" id="fillUserType" />
							
									    	
							<div class="row row_without_margin" style="margin-bottom: 10px;">
								<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
									<i class="fa fa-filter"></i>
								</div>
								<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Paycycle</p>
										<s:select name="strPaycycle" id="strPaycycle" listKey="paycycleId" listValue="paycycleName" list="paycycleList" key="" onchange="sortByEmpTimesheets('P');"/>
									</div>
									<% if(hmFeatureStatus != null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_FILTER_PAYCYCLE_OR_MONTH))) { %>
										<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
											<p style="padding-left: 5px;">OR</p>
										</div>
										
										<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
											<p style="padding-left: 5px;">Year</p>
											<s:select theme="simple" name="strYear" id="strYear" cssStyle="width: 120px !important;" headerKey="" headerValue="Select Year" listKey="yearsID" listValue="yearsName" list="yearList" />
										</div>
										
										<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
											<p style="padding-left: 5px;">Month</p>
											<s:select theme="simple" name="strMonth" id="strMonth" cssStyle="width: 120px !important;" headerKey="" headerValue="Select Month" listKey="monthId" listValue="monthName" list="monthList"/>
										</div>
										
										<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
											<p style="padding-left: 5px;">&nbsp;</p>
											<input type="button" value="Submit" class="btn btn-primary" style="padding: 3px;" onclick="sortByEmpTimesheets('O');"/>
										</div>
									<% } %>
								</div>
							</div>
						</div>
						
					</div>
				</div>
				
				<div class="col-md-12">
					<div class="box box-warning direct-chat direct-chat-warning">
						<div class="box-header with-border">
							<h4 class="box-title" style="font-size: 14px; font-weight: bold;">Selections for Adding:</h4>
							<div class="box-tools pull-right">
								<button data-widget="collapse" class="btn btn-box-tool"><i class="fa fa-minus"></i></button>
							</div>
						</div><!-- /.box-header -->
						<div class="box-body" style="display: block;">
						
							<div class="row row_without_margin" style="margin-bottom: 10px;">
								<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
									<%-- <div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Select Client/ Other</p>
										<s:select name="strClient" id="strClient" listKey="clientId" listValue="clientName" list="clientlist" 
											multiple="true" onchange="getProjectList();"/>
									</div>
									
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Select Project/ Other Activity</p>
										<div id="myProject">
										<s:select name="strProject" id="strProject" listKey="projectID" listValue="projectName"  list="projectdetailslist"
											multiple="true" onchange="getTaskList();"/>
										</div>	
									</div> --%>
									
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">From Date</p> 
										<s:textfield name="frmDate" id="idStdDateStart" cssClass="validateRequired" cssStyle="width:90px  !important;"/>
									</div>
									
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">To Date</p>
										<s:textfield name="toDate" id="idStdDateEnd" cssClass="validateRequired" cssStyle="width:90px !important;"/>
									</div>
									
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">&nbsp;</p>
										<input type="button" name="submit1" value="Add Activity" class="btn btn-primary" style="padding: 3px;" onclick="addTaskActivity();"/>
										<%-- <s:submit cssClass="btn btn-primary" cssStyle="padding: 3px;" value="Add Activity" name="submit1"/> --%>
									</div>
								</div>
							</div>
						</div> <!-- /.box-body -->
					</div>
				</div>
			</s:form>

	<div class="col-lg-12 col-md-12 col-sm-12">
		<% //empApproved!=null
			if(uF.parseToInt((String)request.getAttribute("nApproved"))==0 && strUserType!=null && (strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) || 
				strUserType.equalsIgnoreCase(IConstants.MANAGER) || strUserType.equalsIgnoreCase(IConstants.HRMANAGER)  
				|| strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.ACCOUNTANT) 
				|| strUserType.equalsIgnoreCase(IConstants.CEO) || strUserType.equalsIgnoreCase(IConstants.HOD))) {
		%>
			<s:form id="frmDailyTimesheet" action="DailyTimesheet" name="frmDailyTimesheet" cssClass="formcss" method="post" theme="simple">		
				<input type="hidden" name="strEmpId" value="<%=request.getParameter("strEmpId")%>"/>
				<input type="hidden" name="frmDate" value="<%=request.getParameter("frmDate")%>"/>
				<input type="hidden" name="toDate" value="<%=request.getParameter("toDate")%>"/>
				<input type="hidden" name="strProject" value="<%=request.getParameter("strProject")%>"/>
				<input type="hidden" name="strActivity" value="<%=request.getParameter("strActivity")%>"/>
				<input type="hidden" name="strPaycycle" id="strPaycycle" value="<%=request.getParameter("strPaycycle")%>"/> 
				<input type="hidden" name="strClient" value="<%=request.getParameter("strClient")%>"/>
				<s:hidden name="filterBy" id="filterBy" /> 
				<%-- <s:hidden name="strPaycycle" id="strPaycycle" /> --%>
			    <s:hidden name="strYear" id="strYear" />
				<s:hidden name="strMonth" id="strMonth" />
				<s:hidden name="pageType" id="pageType" />
				<s:hidden name="fillUserType" id="fillUserType" />
			
			<%
			//System.out.println("sbTasks === >>>> " + (String)request.getAttribute("sbTasks"));
			if(request.getAttribute("sbTasks") != null && !request.getAttribute("sbTasks").equals("")) { %>
			
				<div class="box box-body" style="overflow-x: auto; max-height: 350px;">	
					<div style="float:left;" id="div_tasks">
						<div style="float:left; padding:2px; width: 100%; boder-bottom: 1px solid #999999">
							<%=request.getAttribute("sbTaskStatus") %>
						 </div>
						
					    <div style="float:left; width: 100%; padding:2px;">
						    <!-- <div style="float:left;width:92px;font-weight:bold;">Date</div> -->
						    <div style="float:left;width:235px;font-weight:bold;">Task</div>
						    <div style="float:left;width:120px;font-weight:bold;">Total Hrs</div>
						    <div style="float:left;width:55px;font-weight:bold;">Billable</div>
						    <div style="float:left;width:85px;font-weight:bold;">Bill Hrs</div>
						    <div style="float:left;width:60px;font-weight:bold;">On-Site</div>
						    <div style="float:left;font-weight:bold;">&nbsp;</div>
						    <div style="float:left;font-weight:bold;">&nbsp;</div>
						    <div style="float:left;font-weight:bold;">&nbsp;</div>
						 </div>
						 
						 <%=request.getAttribute("sbTasks") %>
						 
						 <div style="float:left; width: 100%; padding:2px;">
						    <!-- <div style="float:left;width:85px;">&nbsp;</div> -->
						    <div style="float:left;width:235px;">&nbsp;</div>
						    <div style="float:left;width:120px;">&nbsp;</div>
						    <div style="float:left;width:55px;">&nbsp;</div>
						    <div style="float:left;width:85px;">&nbsp;</div>
						    <!-- <div style="float:left;width:50px;">&nbsp;</div> -->
						    <div style="float:left;"><input type="button" name="save" value="Save Timesheet" class="btn btn-primary" style="padding: 3px;" onclick="saveTimesheet();"/></div>
						    <div style="float:left;">&nbsp;</div>
						    <div style="float:left;">&nbsp;</div>
						    <div style="float:left;">&nbsp;</div>
						 </div>
			    	</div>
			
				</div>
			 
				<% } %>
				
				<% if(request.getAttribute("sbTasks") == null || request.getAttribute("sbTasks").equals("")) { %>
					<%=(String)request.getAttribute("sbNoTasks") %>
				<% } %>
			</s:form>		
		<% } %>
	</div>
	
	
		<%-- <div class="col-md-5">
		<% if(strUserType != null ) { //&& strUserType.equalsIgnoreCase(IConstants.EMPLOYEE)%>
			<div style="margin-bottom: 5px" class="box box-body">
			Apply for: <s:select theme="simple" name="strProType" id="strProType" headerKey="" headerValue="Select Action" 
				list="#{'3':'Leave', '4':'Client Weekly Off'}" onchange="executeActions(this.value);"/>
			</div>
		<% } %>
		
			<div class="box box-warning direct-chat direct-chat-warning">
				<div class="box-header with-border">
					<h4 class="box-title" style="font-size: 14px; font-weight: bold;">Approval History</h4>
					<div class="box-tools pull-right">
						<button data-widget="collapse" class="btn btn-box-tool"><i class="fa fa-minus"></i></button>
					</div>
				</div><!-- /.box-header -->
				<div class="box-body" style="display: block;">
					<%	List<List<String>> mOuterList = (List<List<String>>)request.getAttribute("mOuterList"); %>
					
					<% if(mOuterList != null && !mOuterList.isEmpty()) { %>	
						<table class="table table-hover">
						<tr>
							<th>Sr.No.</th>
							<th width="40%">Submitted to</th>
							<th width="25%">Submitted On</th>
							<th width="25%">Approved On</th>
						</tr>
						<%
							if(unlockList == null) unlockList = new ArrayList<String>();
							int ii=0;
							for(ii=0; mOuterList!=null && ii<mOuterList.size(); ii++) {
								List<String> innerList = mOuterList.get(ii);
							//String color=hmProjectColor.get(innerList.get(1));
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
						<% } %>
						</table>
					<% } else { %>
						<div class="alert" style="background-color: #FCF8E3 !important; border-color: #FAEBCC; color: #8A6D3B !important; padding: 10px; margin: 10px;">No Approval Status.</div>
					<% } %>
				</div><!-- /.box-body -->
			</div>
		</div> --%>


	<s:form name="timesheet" id="ftimesheet" action="DailyTimesheet" theme="simple">
		<div class="col-md-12">
		
			<div class="box box-body">
				<div class="box-header with-border">  <!-- style="padding: 5px;" -->
					<h4 class="box-title" style="font-size: 14px; font-weight: bold;"><%=request.getAttribute("timesheet_title") %></h4>
					<div class="box-tools pull-right">
						<button data-widget="collapse" class="btn btn-box-tool"><i class="fa fa-minus"></i></button>
					</div>
				</div><!-- /.box-header -->
					<%
					//System.out.println("hmWeekendMap --->> " + hmWeekendMap);
					Set<String> weeklyOffSet= hmWeekendMap.get(strWLocationId);
					if(weeklyOffSet==null) weeklyOffSet=new HashSet<String>();
					
					Set<String> rosterWeeklyOffSet = hmRosterWeekEndDates.get((String)request.getAttribute("strEmpId"));
					if(rosterWeeklyOffSet == null) rosterWeeklyOffSet = new HashSet<String>();
					
					String activityType = (String)request.getAttribute("activityType");
					String frmDate = (String)request.getAttribute("frmDate");
					String toDate = (String)request.getAttribute("toDate");
					String strActivity = (String)request.getAttribute("strActivity");
					
					Date frmDt = uF.getDateFormat(frmDate, IConstants.DATE_FORMAT);
					Date toDt = uF.getDateFormat(toDate, IConstants.DATE_FORMAT);
					
					%>
					<div class="table-responsive no-padding box-body" style="display: block;">
					<table class="table table-hover">
						<thead>
							<tr>
								<th>Client</th>
								<th>Project</th>
								<th>Task</th>
								<th>Hrs</th>
								<th>On/Off Site</th>
								<%
								for(int i=0; i<alDates.size(); i++) {
								String strText = "-";
								String strBgColor = null;
								
								if(strBgColor==null) {
									strBgColor = (String) hmLeavesColour.get((String)hmLeaves.get(alDates.get(i)));
									strText = (String)hmLeaveCode.get((String)hmLeaves.get(alDates.get(i))); 
								}  
								
								if(strBgColor==null) {
									if(alEmpCheckRosterWeektype.contains((String)request.getAttribute("strEmpId"))) {
										if(rosterWeeklyOffSet.contains(alDates.get(i))) {
											strBgColor =IConstants.WEEKLYOFF_COLOR;
											strText = "W/O";
										}
									} else if(weeklyOffSet.contains(alDates.get(i))) {
										strBgColor =IConstants.WEEKLYOFF_COLOR;
										strText = "W/O";
									}
								}
								
								String bgcolor=null;
								if(strBgColor==null) {
									strBgColor = (String)hmHolidayDates.get(uF.getDateFormat((String) alDates.get(i), IConstants.DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId);
									if(strBgColor!=null) {
										strText = "H";
									}
								}
								if(strText==null) {
									strText = "-";
								}
							%>
								<th style="background-color: <%=bgcolor==null ? strBgColor:bgcolor%>"><%=uF.getDateFormat((String)alDates.get(i), IConstants.DATE_FORMAT, "dd")%></th>
							<%} %>
						</tr>
					</thead>
			
					<tbody>
					<%
					String strEmpId=(String)request.getAttribute("strEmpId");
					
					Map hmTotal = new HashMap();
					Map hmDayTotalHrs = new HashMap();
					
					Map hmTotalBillableHrs = new HashMap();
					Map hmDayTotalBillableHrs = new HashMap();
					
					StringBuilder sb = new StringBuilder();
					
					String strProjectIdNew = null;
					String strProjectIdOld = null;
					int nCount = 0;
					boolean flag=false;
					
					//System.out.println("hmProjectTasks ===>> " + hmProjectTasks);
					
					for(int a=0; alTaskIds != null&& alTaskIds.size()>0 && a<alTaskIds.size(); a++) {
			
						String strActivityId = alTaskIds.get(a);
						
						List<String> alSubTaskIds = new ArrayList<String>();
						
						if(hmTaskAndSubTaskIds != null) {
							alSubTaskIds = hmTaskAndSubTaskIds.get(strActivityId);
						}
						
						Map hmTasks = (Map)hmProjectTasks.get(strActivityId);
						if(hmTasks == null)hmTasks = new HashMap();
						
						if((hmTasks == null || hmTasks.isEmpty()) && (alSubTaskIds ==null || alSubTaskIds.size()==0)) {
							//System.out.println("strActivityId empty ===>> " + strActivityId);
						} else {
							if(hmTasks == null || hmTasks.isEmpty()) {

								Map hmTasksT = (Map)hmProjectTasksT.get(strActivityId);
								if(hmTasksT == null)hmTasksT = new HashMap();
								
								if(hmTasksT != null && !hmTasksT.isEmpty()) {
								String strProjectId = (String)hmTasksT.get(strActivityId+"_P");
								strProjectIdNew = (String)hmTasksT.get(strActivityId+"_P");
								//System.out.println("strActivityId hmTasks empty ===>> " + strActivityId);
								int nProCount = uF.parseToInt((String)hmProjectCount.get(strProjectId));
								int nProBillCount = uF.parseToInt((String)hmProjectBillCount.get(strProjectId));
								int nProCountT = uF.parseToInt((String)hmProjectCountT.get(strProjectId));
								int nProBillCountT = uF.parseToInt((String)hmProjectBillCountT.get(strProjectId));
								
								//System.out.println("strProjectIdNew ===>> " +strProjectIdNew + " strProjectIdOld ===>> " + strProjectIdOld);
								if(strProjectIdNew!=null && !strProjectIdNew.equalsIgnoreCase(strProjectIdOld)) {
									nCount = 0;
								} else if(strProjectIdNew == null) {
									strProjectIdNew = "";
									if(!strProjectIdNew.equalsIgnoreCase(strProjectIdOld)) {
										nCount = 0;
									}
								}
								nCount++;
								
								Map<String, String> hmDatesT = (Map<String, String>)hmProjectsT.get(strActivityId);
								if(hmDatesT == null)hmDatesT = new HashMap<String, String>();
								
								Map<String, String> hmDatesBillableHrsT = (Map<String, String>)hmProjectsBillableHrsT.get(strActivityId);
								if(hmDatesBillableHrsT == null) hmDatesBillableHrsT = new HashMap<String, String>();
								
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
								//System.out.println(strProjectId+ " --- nProCount ==>> "+ nProCount+" rwspn ==>> "+ rwspn +" (nProCount * rwspn) ===>> " + (nProCount * rwspn) + " billrwspn ===> " + billrwspn + " nProCountT ===>> " + nProCountT + " rwspn ===> " + rwspn + " (nProCountT * rwspn) ===>> " + (nProCountT * rwspn) + " billrwspnT ===> " + billrwspnT);
								//System.out.println(strProjectId+ " --- (nProCount * rwspn) + billrwspn + (nProCountT * rwspn) + billrwspnT ==>> " + ((nProCount * rwspn) + billrwspn + (nProCountT * rwspn) + billrwspnT));
								%>
								
									<td rowspan="<%=(nProCount * rwspn) + billrwspn + (nProCountT * rwspn) + billrwspnT %>"><%=uF.showData((String)hmClientMap.get(strProjectId), "")%></td>
									<td rowspan="<%=(nProCount * rwspn) + billrwspn + (nProCountT * rwspn) + billrwspnT %>"><%=uF.showData((String)hmProjectMap.get(strProjectId), "")%>  <br/> 
									<%=uF.showData((String)hmReimbursementDetails.get(strProjectId), "")%>  </td>
								<% } %>
									<% for(int i=0; i<alDates.size(); i++) { %>
			
									<% String strText = "-";
										
										//String isApproved = uF.showData((String)hmDateHrsIsApproved.get((String)alDates.get(i)+"_"+sb.toString()), strText);
										String strBgColor = null;
										
										if(strBgColor==null) {
											strBgColor = (String) hmLeavesColour.get((String)hmLeaves.get(alDates.get(i)));
											strText = (String)hmLeaveCode.get((String)hmLeaves.get(alDates.get(i))); 
										}  
										
										if(strBgColor==null) {
											if(alEmpCheckRosterWeektype.contains((String)request.getAttribute("strEmpId"))) {
												if(rosterWeeklyOffSet.contains(alDates.get(i))){
													strBgColor =IConstants.WEEKLYOFF_COLOR;
													strText = "W/O";
												}
											} else if(weeklyOffSet.contains(alDates.get(i))) {
												strBgColor =IConstants.WEEKLYOFF_COLOR;
												strText = "W/O";
											}
										}
										
										if(strBgColor==null) {
											strBgColor = (String)hmHolidayDates.get(uF.getDateFormat((String) alDates.get(i), IConstants.DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId);
											if(strBgColor!=null) {
												strText = "H";
											}
										}
										
										
										if(uF.getDateFormat(currDate, IConstants.DATE_FORMAT).equals(uF.getDateFormat(alDates.get(i).toString(), IConstants.DATE_FORMAT))) {
											strBgColor = "#EFEFEF";
										}
										
										if(strText==null) {
											strText = "-";
										}
										
										if(i==0 && j==0) {
											int tskRwSpan = 2;
											if(hmTaskIsBillableT != null && uF.parseToBoolean(hmTaskIsBillableT.get(strActivityId))) {
												tskRwSpan += 2;
											}
										%>
										<td rowspan="<%=tskRwSpan %>"><strong><%=uF.showData((String)hmTasksT.get(strActivityId+"_T"), "-") %></strong>
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
										<td align="right" style="background-color: <%=bgcolor==null ? strBgColor:bgcolor%>">
											<%=dayStatus%>
										</td>
									<% } %>
								</tr>
								
							<% } %>
								
								<% if(hmTaskIsBillableT != null && uF.parseToBoolean(hmTaskIsBillableT.get(strActivityId))) { %>
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
										
										//String isApproved = uF.showData((String)hmDateHrsIsApproved.get((String)alDates.get(i)+"_"+sb.toString()), strText);
										
										String strBgColor = null;
										
										if(strBgColor==null) {
											strBgColor = (String) hmLeavesColour.get((String)hmLeaves.get(alDates.get(i)));
											strText = (String)hmLeaveCode.get((String)hmLeaves.get(alDates.get(i))); 
										}
										if(strBgColor==null) {
											if(alEmpCheckRosterWeektype.contains((String)request.getAttribute("strEmpId"))) {
												if(rosterWeeklyOffSet.contains(alDates.get(i))) {
													strBgColor =IConstants.WEEKLYOFF_COLOR;
													strText = "W/O";
												}
											} else if(weeklyOffSet.contains(alDates.get(i))) {
												strBgColor =IConstants.WEEKLYOFF_COLOR;
												strText = "W/O";
											}
										}
										
										if(strBgColor==null) {
											strBgColor = (String)hmHolidayDates.get(uF.getDateFormat((String) alDates.get(i), IConstants.DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId);
											if(strBgColor!=null) {
												strText = "H";
											}
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
							
								strProjectIdOld = strProjectIdNew;
							%>
								
							<% }
							
							} else {
								String strProjectId = (String)hmTasks.get(strActivityId+"_P");
								strProjectIdNew = (String)hmTasks.get(strActivityId+"_P");
								//System.out.println("strActivityId hmTasks ===>> " + strActivityId);
						int nProCount = uF.parseToInt((String)hmProjectCount.get(strProjectId));
						int nProBillCount = uF.parseToInt((String)hmProjectBillCount.get(strProjectId));
						int nProCountT = uF.parseToInt((String)hmProjectCountT.get(strProjectId));
						int nProBillCountT = uF.parseToInt((String)hmProjectBillCountT.get(strProjectId));
						
						
						//System.out.println("strActivityId ===>> " +strActivityId );
						List<String> checkEmpList = null;
						if( hmCheckEmp != null && hmCheckEmp.size()>0 && strActivityId != null && !strActivityId.equals("")) {
							hmCheckEmp.get(strActivityId.trim());
						}
						
						if(checkEmpList == null) checkEmpList = new ArrayList<String>();
						// && hmEmpByLocation.get(request.getAttribute("strEmpId"))==null
						//System.out.println("checkEmpList ===>> " + checkEmpList);
						if(!checkEmpList.contains(strSessionEmpId) && strUserType.equalsIgnoreCase(IConstants.HRMANAGER) && (pageType == null || !pageType.equals("MP"))) {
							flag = true;
						}
						//System.out.println("flag ===>> " + flag);
						
						//System.out.println("strProjectIdNew ===>> " +strProjectIdNew + " strProjectIdOld ===>> " + strProjectIdOld);
					
						if(strProjectIdNew!=null && !strProjectIdNew.equalsIgnoreCase(strProjectIdOld)) {
							nCount = 0;
						} else if(strProjectIdNew == null) {
							strProjectIdNew = "";
							if(!strProjectIdNew.equalsIgnoreCase(strProjectIdOld)) {
								nCount = 0;
							}
						}
						nCount++;
						
						Map<String, String> hmDates = (Map<String, String>)hmProjects.get(strActivityId);
						if(hmDates==null)hmDates = new HashMap<String, String>();
						
						Map<String, String> hmDatesBillableHrs = (Map<String, String>)hmProjectsBillableHrs.get(strActivityId);
						if(hmDatesBillableHrs == null) hmDatesBillableHrs = new HashMap<String, String>();
						
						Map<String, String> hmDateHrsIsApproved = (Map<String, String>)hmProjectsHrsIsApproved.get(strActivityId);
						if(hmDateHrsIsApproved == null) hmDateHrsIsApproved = new HashMap<String, String>();
						
						Map<String, String> hmTaskDescri = (Map<String, String>)hmProjectsTaskDescri.get(strActivityId);
						if(hmTaskDescri == null)hmTaskDescri = new HashMap<String, String>();
						
						
						Map<String, String> hmDateTaskId = (Map<String, String>)hmProjectsTaskId.get(strActivityId);
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
						//System.out.println(strProjectId+ " --- nProCount ==>> "+ nProCount+" rwspn ==>> "+ rwspn +" (nProCount * rwspn) ===>> " + (nProCount * rwspn) + " billrwspn ===> " + billrwspn + " nProCountT ===>> " + nProCountT + " rwspn ===> " + rwspn + " (nProCountT * rwspn) ===>> " + (nProCountT * rwspn) + " billrwspnT ===> " + billrwspnT);
						//System.out.println(strProjectId+ " --- (nProCount * rwspn) + billrwspn + (nProCountT * rwspn) + billrwspnT ==>> " + ((nProCount * rwspn) + billrwspn + (nProCountT * rwspn) + billrwspnT));
						%>
						
							<td rowspan="<%=(nProCount * rwspn) + billrwspn + (nProCountT * rwspn) + billrwspnT %>"><%=uF.showData((String)hmClientMap.get(strProjectId), "")%></td>
							<td rowspan="<%=(nProCount * rwspn) + billrwspn + (nProCountT * rwspn) + billrwspnT %>"><%=uF.showData((String)hmProjectMap.get(strProjectId), "")%>  <br/> 
							<%=uF.showData((String)hmReimbursementDetails.get(strProjectId), "")%>  </td>
						<% } %>
							<% for(int i=0; i<alDates.size(); i++) { %>
			
							<% String strText = "-";
								
								String isApproved = uF.showData((String)hmDateHrsIsApproved.get((String)alDates.get(i)+"_"+sb.toString()), strText);
								String strBgColor = null;
								
								if(strBgColor==null) {
									strBgColor = (String) hmLeavesColour.get((String)hmLeaves.get(alDates.get(i)));
									strText = (String)hmLeaveCode.get((String)hmLeaves.get(alDates.get(i))); 
								}
								
								if(strBgColor==null) {
									if(alEmpCheckRosterWeektype.contains((String)request.getAttribute("strEmpId"))) {
										if(rosterWeeklyOffSet.contains(alDates.get(i))){
											strBgColor =IConstants.WEEKLYOFF_COLOR;
											strText = "W/O";
										}
									} else if(weeklyOffSet.contains(alDates.get(i))) {
										strBgColor =IConstants.WEEKLYOFF_COLOR;
										strText = "W/O";
									}
								}
								if(strBgColor==null) {
									strBgColor = (String)hmHolidayDates.get(uF.getDateFormat((String) alDates.get(i), IConstants.DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId);
									if(strBgColor!=null) {
										strText = "H";
									}
								}
								
								if(uF.getDateFormat(currDate, IConstants.DATE_FORMAT).equals(uF.getDateFormat(alDates.get(i).toString(), IConstants.DATE_FORMAT))) {
									strBgColor = "#EFEFEF";
								}
								
								if(strText==null) {
									strText = "-";
								}
								
								if(i==0 && j==0) {
									int tskRwSpan = 2;
									if(hmTaskIsBillable != null && uF.parseToBoolean(hmTaskIsBillable.get(strActivityId))) {
										tskRwSpan += 2;
									}
								%>
								<td rowspan="<%=tskRwSpan %>">
								<% if(uF.parseToInt(strActivityId) > 0) { %>
									<strong><%=uF.showData((String)hmTasks.get(strActivityId+"_T"), "-") %></strong>
								<% } else { %>
									<%=uF.showData((String)hmTasks.get(strActivityId+"_T"), "-") %>
								<% } %>
								<%
								if((hmCheckTaskStatus!=null && hmCheckTaskStatus.get(hmTasks.get(strActivityId+"_T"))!=null) && strSessionEmpId!=null && strEmpId!=null 
									&& ((!strSessionEmpId.equals(strEmpId) && (strUserType.equalsIgnoreCase(IConstants.MANAGER) || strUserType.equalsIgnoreCase(IConstants.CEO) ||
									strUserType.equalsIgnoreCase(IConstants.HOD) || strUserType.equalsIgnoreCase(IConstants.ACCOUNTANT))) || strUserType.equalsIgnoreCase(IConstants.ADMIN) 
									|| (strUserType.equalsIgnoreCase(IConstants.HRMANAGER) && pageType != null && pageType.equals("MP")) ) 
									&& (fillUserType == null || !fillUserType.equals("MY")) ) {
								/* if((hmCheckTaskStatus!=null && hmCheckTaskStatus.get(hmTasks.get(strActivityId+"_T"))!=null) && strSessionEmpId!=null && strEmpId!=null 
									&& ((!strSessionEmpId.equals(strEmpId) && strUserType.equalsIgnoreCase(IConstants.MANAGER)) || strUserType.equalsIgnoreCase(IConstants.ADMIN) || (strUserType.equalsIgnoreCase(IConstants.HRMANAGER) && pageType != null && pageType.equals("MP")) ) 
									&& (fillUserType == null || !fillUserType.equals("MY")) ) { */ %>
								<input type="checkbox" name="checkTaskId" id="checkTaskId" value="<%=strActivityId%>" />
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
									
									String dayStatus = "";
									dayStatus=uF.showData((String)hmDates.get((String)alDates.get(i)+"_"+sb.toString()), strText);
								if(uF.parseToDouble(dayStatus) > 0) {
									dayStatus = uF.getTotalTimeMinutes100To60(dayStatus);
								}
							//	}
								
								%>
								<td align="right" style="background-color: <%=bgcolor==null ? strBgColor:bgcolor %>">
									
									<% 
									String taskId = hmDateTaskId.get((String)alDates.get(i)+"_"+sb.toString());
									
									//System.out.println(" hmCheckUserApproval.get(taskId_strSessionEmpId) ===>> " + hmCheckUserApproval != null ? hmCheckUserApproval.get(taskId+"_"+strSessionEmpId): "---");
									
									if(hmCheckUserApproval!=null && uF.parseToBoolean(hmCheckUserApproval.get(taskId+"_"+strSessionEmpId))==true) {  %>
				
									<% if(hmTaskDescri != null && hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()) != null && !hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()).equals("") && uF.parseToDouble(dayStatus) > 0) { %>
										<a href="javascript:void(0);" onclick="showTaskDescription('<%=URLEncoder.encode(hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()), "UTF-8") %>');"> <span class="worklate" style="height: 10px;">&nbsp;</span></a>
									<% } %>
									<%=dayStatus %>
									
									<% } else if(flag && strUserType.equalsIgnoreCase(IConstants.HRMANAGER) && (pageType == null || !pageType.equals("MP"))) { %>
				
									<% if(hmTaskDescri != null && hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()) != null && !hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()).equals("") && uF.parseToDouble(dayStatus) > 0) { %>
										<a href="javascript:void(0);" onclick="showTaskDescription('<%=URLEncoder.encode(hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()), "UTF-8") %>');"> <span class="worklate" style="height: 10px;">&nbsp;</span></a>
									<% } %>
										<%=dayStatus %>
										
									<% } else {
										if(dayStatus != null && (dayStatus.trim().equals("0.00") || uF.parseToDouble(dayStatus) > 0)) {
											//System.out.println("isApproved ===>> " + isApproved);
											if(isApproved!=null && isApproved.equals("0") && strSessionEmpId!=null && strEmpId!=null && strSessionEmpId.equals(strEmpId) && fillUserType != null && fillUserType.equals("MY")) {
									%>
									
									<% if(hmTaskDescri != null && hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()) != null && !hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()).equals("") && uF.parseToDouble(dayStatus) > 0) { %>
										<a href="javascript:void(0);" onclick="showTaskDescription('<%=URLEncoder.encode(hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()), "UTF-8") %>');"> <span class="worklate" style="height: 10px;">&nbsp;</span></a>
									<% } %>
									<a href="javascript:void(0);" onclick="taskDaywiseTimeUpdate('<%=request.getAttribute("strPaycycle") %>', '<%=request.getAttribute("strEmpId") %>', '<%=(String)alDates.get(i) %>', '<%=(String)alDates.get(i) %>', '<%=(strProjectId != null && !strProjectId.equals("null") && !strProjectId.equals("")) ? strProjectId : ""%>', '<%=strActivityId%>', '<%=taskId%>', '<%=filterBy %>', '<%=strYear %>', '<%=strMonth %>', '<%=pageType %>', '<%=fillUserType %>')">
										<%=dayStatus %>
									</a>	
									<% } else if(isApproved!=null && (isApproved.equals("1") || isApproved.equals("2")) && strSessionEmpId!=null && strEmpId!=null && strSessionEmpId.equals(strEmpId) && fillUserType != null && fillUserType.equals("MY")) { %>
									
									<% if(hmTaskDescri != null && hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()) != null && !hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()).equals("") && uF.parseToDouble(dayStatus) > 0) { %>
										<a href="javascript:void(0);" onclick="showTaskDescription('<%=URLEncoder.encode(hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()), "UTF-8") %>');"> <span class="worklate" style="height: 10px;">&nbsp;</span></a>
									<% } %>	
										<%=dayStatus %>
									
									<% } else if(isApproved!=null && isApproved.equals("0")) { %>
										<% if(hmTaskDescri != null && hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()) != null && !hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()).equals("") && uF.parseToDouble(dayStatus) > 0) { %>
											<a href="javascript:void(0);" onclick="showTaskDescription('<%=URLEncoder.encode(hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()), "UTF-8") %>');"> <span class="worklate" style="height: 10px;">&nbsp;</span></a>
										<% } %>
										<%=dayStatus %>
										
									<% } else if(isApproved!=null && isApproved.equals("2")) { %>
									
									<% if(hmTaskDescri != null && hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()) != null && !hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()).equals("") && uF.parseToDouble(dayStatus) > 0) { %>
										<a href="javascript:void(0);" onclick="showTaskDescription('<%=URLEncoder.encode(hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()), "UTF-8") %>');"> <span class="worklate" style="height: 10px;">&nbsp;</span></a>
									<% } %>
										<%=dayStatus %>
									
									<% } else { %>
									
									<% if(hmTaskDescri != null && hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()) != null && !hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()).equals("") && uF.parseToDouble(dayStatus) > 0) { %>
										<a href="javascript:void(0);" onclick="showTaskDescription('<%=URLEncoder.encode(hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()), "UTF-8") %>');"> <span class="worklate" style="height: 10px;">&nbsp;</span></a>
									<% } %>
									<a href="javascript:void(0);" onclick="taskDaywiseTimeUpdate('<%=request.getAttribute("strPaycycle") %>', '<%=request.getAttribute("strEmpId") %>', '<%=(String)alDates.get(i)%>', '<%=(String)alDates.get(i)%>', '<%=(strProjectId != null && !strProjectId.equals("null") && !strProjectId.equals("")) ? strProjectId : ""%>', '<%=strActivityId%>', '<%=taskId%>', '<%=filterBy %>', '<%=strYear %>', '<%=strMonth %>', '<%=pageType %>', '<%=fillUserType %>')">
										<%=dayStatus %>
									</a>	
									<%}
									} else {
										out.println(dayStatus);
									}
										} %>
								</td>
							<%
							double dblHrs = uF.parseToDouble((String)hmDates.get((String)alDates.get(i)+"_"+sb.toString()));
							dblHrs += uF.parseToDouble((String)hmTotal.get((String)alDates.get(i)));
							hmTotal.put((String)alDates.get(i), uF.formatIntoTwoDecimal(dblHrs)+"");
							
							Date currDt = uF.getDateFormat((String)alDates.get(i), IConstants.DATE_FORMAT);
							if(activityType == null || (activityType != null && activityType.equals("E") && (currDt.before(frmDt) || currDt.after(toDt)) || !strActivity.equals(strActivityId))) {
								double dblDayHrs = uF.parseToDouble((String)hmDates.get((String)alDates.get(i)+"_"+sb.toString()));
								dblDayHrs += uF.parseToDouble((String)hmDayTotalHrs.get((String)alDates.get(i)));
								hmDayTotalHrs.put((String)alDates.get(i), uF.formatIntoTwoDecimal(dblDayHrs)+"");
							}
							%>
							
							<% } %>
						</tr>
						
					<% } %>
						
						
						<% if(hmTaskIsBillable != null && uF.parseToBoolean(hmTaskIsBillable.get(strActivityId))) { %>
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
								
								String strBgColor = null;
								
								if(strBgColor==null) {
									strBgColor = (String) hmLeavesColour.get((String)hmLeaves.get(alDates.get(i)));
									strText = (String)hmLeaveCode.get((String)hmLeaves.get(alDates.get(i))); 
								}
								
								if(strBgColor==null) {
									if(alEmpCheckRosterWeektype.contains((String)request.getAttribute("strEmpId"))) {
										if(rosterWeeklyOffSet.contains(alDates.get(i))) {
											strBgColor =IConstants.WEEKLYOFF_COLOR;
											strText = "W/O";
										}
									} else if(weeklyOffSet.contains(alDates.get(i))) {
										strBgColor =IConstants.WEEKLYOFF_COLOR;
										strText = "W/O";
									}
								}
								
								if(strBgColor==null) {
									strBgColor = (String)hmHolidayDates.get(uF.getDateFormat((String) alDates.get(i), IConstants.DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId);
									if(strBgColor!=null) {
										strText = "H";
									}
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
								
								dayStatus=uF.showData((String)hmDatesBillableHrs.get((String)alDates.get(i)+"_"+sb.toString()), strText);
								if(uF.parseToDouble(dayStatus) > 0) {
									dayStatus = uF.getTotalTimeMinutes100To60(dayStatus);
								}
								
								%>
								<td align="right" style="background-color: <%=bgcolor==null ? strBgColor:bgcolor%>">
									
									<% 
									String taskId = hmDateTaskId.get((String)alDates.get(i)+"_"+sb.toString());
									
									if(hmCheckUserApproval!=null && uF.parseToBoolean(hmCheckUserApproval.get(taskId+"_"+strSessionEmpId))==true) { %>
								
									<% if(hmTaskDescri != null && hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()) != null && !hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()).equals("") && uF.parseToDouble(dayStatus) > 0) { %>
										<a href="javascript:void(0);" onclick="showTaskDescription('<%=URLEncoder.encode(hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()), "UTF-8") %>');"> <span class="worklate" style="height: 10px;">&nbsp;</span></a>
									<% } %>
									<%=dayStatus%>
									
									<% } else if(flag && strUserType.equalsIgnoreCase(IConstants.HRMANAGER) && (pageType == null || !pageType.equals("MP"))) { %>
									
									<% if(hmTaskDescri != null && hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()) != null && !hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()).equals("") && uF.parseToDouble(dayStatus) > 0) { %>
										<a href="javascript:void(0);" onclick="showTaskDescription('<%=URLEncoder.encode(hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()), "UTF-8") %>');"> <span class="worklate" style="height: 10px;">&nbsp;</span></a>
									<% } %>
										<%=dayStatus%>
										
									<% } else {
										if(dayStatus != null && (dayStatus.trim().equals("0.00") || uF.parseToDouble(dayStatus) > 0)) {	
											//System.out.println("isApproved ===>> " + isApproved);
											if((isApproved!=null && isApproved.equals("0")) && strSessionEmpId!=null && strEmpId!=null  && strSessionEmpId.equals(strEmpId) && fillUserType != null && fillUserType.equals("MY")) {
									%>
									
									<% if(hmTaskDescri != null && hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()) != null && !hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()).equals("") && uF.parseToDouble(dayStatus) > 0) { %>
										<a href="javascript:void(0);" onclick="showTaskDescription('<%=URLEncoder.encode(hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()), "UTF-8") %>');"> <span class="worklate" style="height: 10px;">&nbsp;</span></a>
									<% } %>
									<a href="javascript:void(0);" onclick="taskDaywiseTimeUpdate('<%=request.getAttribute("strPaycycle") %>', '<%=request.getAttribute("strEmpId") %>', '<%=(String)alDates.get(i)%>', '<%=(String)alDates.get(i)%>', '<%=(strProjectId != null && !strProjectId.equals("null") && !strProjectId.equals("")) ? strProjectId : ""%>', '<%=strActivityId%>', '<%=taskId%>', '<%=filterBy %>', '<%=strYear %>', '<%=strMonth %>', '<%=pageType %>', '<%=fillUserType %>')">
										<%=dayStatus%>
									</a>	
									<% } else if(isApproved!=null && (isApproved.equals("1") || isApproved.equals("2")) && strSessionEmpId!=null && strEmpId!=null && strSessionEmpId.equals(strEmpId) && fillUserType != null && fillUserType.equals("MY")) { %>
									
									<% if(hmTaskDescri != null && hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()) != null && !hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()).equals("") && uF.parseToDouble(dayStatus) > 0) { %>
										<a href="javascript:void(0);" onclick="showTaskDescription('<%=URLEncoder.encode(hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()), "UTF-8") %>');"> <span class="worklate" style="height: 10px;">&nbsp;</span></a>
									<% } %>	
										<%=dayStatus%>
										
									<% } else if(isApproved!=null && isApproved.equals("0")) { %>
										<% if(hmTaskDescri != null && hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()) != null && !hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()).equals("") && uF.parseToDouble(dayStatus) > 0) { %>
											<a href="javascript:void(0);" onclick="showTaskDescription('<%=URLEncoder.encode(hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()), "UTF-8") %>');"> <span class="worklate" style="height: 10px;">&nbsp;</span></a>
										<% } %>
										<%=dayStatus %>
											
									<% } else if(isApproved!=null && isApproved.equals("2")) { %>
									
									<% if(hmTaskDescri != null && hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()) != null && !hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()).equals("") && uF.parseToDouble(dayStatus) > 0) { %>
										<a href="javascript:void(0);" onclick="showTaskDescription('<%=URLEncoder.encode(hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()), "UTF-8") %>');"> <span class="worklate" style="height: 10px;">&nbsp;</span></a>
									<% } %>
										<%=dayStatus%>
									
									<% } else { %>
									
									<% if(hmTaskDescri != null && hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()) != null && !hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()).equals("") && uF.parseToDouble(dayStatus) > 0) { %>
										<a href="javascript:void(0);" onclick="showTaskDescription('<%=URLEncoder.encode(hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()), "UTF-8") %>');"> <span class="worklate" style="height: 10px;">&nbsp;</span></a>
									<% } %>
									<a href="javascript:void(0);" onclick="taskDaywiseTimeUpdate('<%=request.getAttribute("strPaycycle") %>', '<%=request.getAttribute("strEmpId") %>', '<%=(String)alDates.get(i)%>', '<%=(String)alDates.get(i)%>', '<%=(strProjectId != null && !strProjectId.equals("null") && !strProjectId.equals("")) ? strProjectId : ""%>', '<%=strActivityId%>', '<%=taskId%>', '<%=filterBy %>', '<%=strYear %>', '<%=strMonth %>', '<%=pageType %>', '<%=fillUserType %>')">
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
							
							Date currDt = uF.getDateFormat((String)alDates.get(i), IConstants.DATE_FORMAT);
							if(activityType == null || (activityType != null && activityType.equals("E") && (currDt.before(frmDt) || currDt.after(toDt)) || !strActivity.equals(strActivityId))) {
								double dblDayBillableHrs = uF.parseToDouble((String)hmDatesBillableHrs.get((String)alDates.get(i)+"_"+sb.toString()));
								dblDayBillableHrs += uF.parseToDouble((String)hmDayTotalBillableHrs.get((String)alDates.get(i)));
								hmDayTotalBillableHrs.put((String)alDates.get(i), uF.formatIntoTwoDecimal(dblDayBillableHrs)+"");
							}
							%>
							
							<% } %>
						</tr>
						<% } %>
					<% } %>
						
					<% 
						strProjectIdOld = strProjectIdNew;
						} %>
						
						
						
					<%
					String strProjectIdNewST = null;
					String strProjectIdOldST = null;
					int nCountST = 0;
					boolean flagST = false;
					
					for(int st=0; alSubTaskIds != null && st<alSubTaskIds.size(); st++) {
						String strSubTaskId = alSubTaskIds.get(st);
						//System.out.println("strSubTaskId ===>> " + strSubTaskId);
						
						hmTasks = (Map)hmProjectTasks.get(strSubTaskId);
						if(hmTasks == null) hmTasks = new HashMap();
						
						if(hmTasks != null && !hmTasks.isEmpty()) {
						String strProjectIdST = (String)hmTasks.get(strSubTaskId+"_P");
						strProjectIdNewST = (String)hmTasks.get(strSubTaskId+"_P");
						int nProCountST = uF.parseToInt((String)hmProjectCount.get(strProjectIdST));
						int nProBillCountST = uF.parseToInt((String)hmProjectBillCount.get(strProjectIdST));
						
						List<String> checkEmpListST = hmCheckEmp.get(strSubTaskId.trim());
						if(checkEmpListST == null) checkEmpListST = new ArrayList<String>();
						// && hmEmpByLocation.get(request.getAttribute("strEmpId"))==null
						if(!checkEmpListST.contains(strSessionEmpId) && strUserType.equalsIgnoreCase(IConstants.HRMANAGER) && (pageType == null || !pageType.equals("MP"))) { 
							flagST=true;
						}
						
						
						if(strProjectIdNewST!=null && !strProjectIdNewST.equalsIgnoreCase(strProjectIdOldST)) {
							nCountST = 0;
						} else if(strProjectIdNewST==null) {
							strProjectIdNewST = "";
							if(!strProjectIdNewST.equalsIgnoreCase(strProjectIdOldST)) {
								nCountST = 0;	
							}
						}
						nCountST++;
						
						//System.out.println("nCountST ===>> " + nCountST);
						//System.out.println("nCount ===>> " + nCount);
						
						Map<String, String> hmDates = (Map<String, String>)hmProjects.get(strSubTaskId);
						if(hmDates==null)hmDates = new HashMap<String, String>();
						
						Map<String, String> hmDatesBillableHrs = (Map<String, String>)hmProjectsBillableHrs.get(strSubTaskId);
						if(hmDatesBillableHrs == null)hmDatesBillableHrs = new HashMap<String, String>();
						
						Map<String, String> hmDateHrsIsApproved = (Map<String, String>)hmProjectsHrsIsApproved.get(strSubTaskId);
						if(hmDateHrsIsApproved == null)hmDateHrsIsApproved = new HashMap<String, String>();
						
						Map<String, String> hmTaskDescri = (Map<String, String>)hmProjectsTaskDescri.get(strSubTaskId);
						if(hmTaskDescri == null)hmTaskDescri = new HashMap<String, String>();
						
						Map<String, String> hmDateTaskId = (Map<String, String>)hmProjectsTaskId.get(strSubTaskId);
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
								String strBgColor = null;
								
								if(strBgColor==null) {
									strBgColor = (String) hmLeavesColour.get((String)hmLeaves.get(alDates.get(i)));
									strText = (String)hmLeaveCode.get((String)hmLeaves.get(alDates.get(i))); 
								}
								
								if(strBgColor==null) {
									if(alEmpCheckRosterWeektype.contains((String)request.getAttribute("strEmpId"))) {
										if(rosterWeeklyOffSet.contains(alDates.get(i))){
											strBgColor =IConstants.WEEKLYOFF_COLOR;
											strText = "W/O";
										}
									} else if(weeklyOffSet.contains(alDates.get(i))) {
										strBgColor =IConstants.WEEKLYOFF_COLOR;
										strText = "W/O";
									}
								}
								
								if(strBgColor==null) {
									strBgColor = (String)hmHolidayDates.get(uF.getDateFormat((String) alDates.get(i), IConstants.DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId);
									if(strBgColor!=null) {
										strText = "H";
									}
								}
								
								if(uF.getDateFormat(currDate, IConstants.DATE_FORMAT).equals(uF.getDateFormat(alDates.get(i).toString(), IConstants.DATE_FORMAT))) {
									strBgColor = "#EFEFEF";
								}
								
								if(strText==null) {
									strText = "-";
								}
								
								if(i==0 && j==0) {
									int tskRwSpan = 2;
									if(hmTaskIsBillable != null && uF.parseToBoolean(hmTaskIsBillable.get(strSubTaskId))) {
										tskRwSpan += 2;
									}
								%>
								<td rowspan="<%=tskRwSpan %>"><%=uF.showData((String)hmTasks.get(strSubTaskId+"_T"), "-") %> [ST]
								<%
								if((hmCheckTaskStatus!=null && hmCheckTaskStatus.get(hmTasks.get(strSubTaskId+"_T"))!=null) && strSessionEmpId!=null && strEmpId!=null 
									&& ((!strSessionEmpId.equals(strEmpId) && (strUserType.equalsIgnoreCase(IConstants.MANAGER) || strUserType.equalsIgnoreCase(IConstants.CEO) ||
									strUserType.equalsIgnoreCase(IConstants.HOD) || strUserType.equalsIgnoreCase(IConstants.ACCOUNTANT))) || strUserType.equalsIgnoreCase(IConstants.ADMIN) 
									|| (strUserType.equalsIgnoreCase(IConstants.HRMANAGER) && pageType != null && pageType.equals("MP")) ) 
									&& (fillUserType == null || !fillUserType.equals("MY")) ) { %>
								<input type="checkbox" name="checkTaskId" id="checkTaskId" value="<%=strSubTaskId%>" />
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
									dayStatus=uF.showData((String)hmDates.get((String)alDates.get(i)+"_"+sb.toString()), strText);
								if(uF.parseToDouble(dayStatus) > 0) {
									dayStatus = uF.getTotalTimeMinutes100To60(dayStatus);
								}
								
								%>
								<td align="right" style="background-color: <%=bgcolor==null ? strBgColor : bgcolor%>">
									
									<% 
									String taskId = hmDateTaskId.get((String)alDates.get(i)+"_"+sb.toString());
									
									if(hmCheckUserApproval!=null && uF.parseToBoolean(hmCheckUserApproval.get(taskId+"_"+strSessionEmpId))==true) {  %>
				
									<% if(hmTaskDescri != null && hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()) != null && !hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()).equals("") && uF.parseToDouble(dayStatus) > 0) { %>
										<a href="javascript:void(0);" onclick="showTaskDescription('<%=URLEncoder.encode(hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()), "UTF-8") %>');"> <span class="worklate" style="height: 10px;">&nbsp;</span></a>
									<% } %>
									<%=dayStatus%>
									
									<% } else if(flagST && strUserType.equalsIgnoreCase(IConstants.HRMANAGER) && (pageType == null || !pageType.equals("MP"))) { %>
				
									<% if(hmTaskDescri != null && hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()) != null && !hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()).equals("") && uF.parseToDouble(dayStatus) > 0) { %>
										<a href="javascript:void(0);" onclick="showTaskDescription('<%=URLEncoder.encode(hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()), "UTF-8") %>');"> <span class="worklate" style="height: 10px;">&nbsp;</span></a>
									<% } %>
										<%=dayStatus%>
										
									<% } else {
										if(dayStatus != null && (dayStatus.trim().equals("0.00") || uF.parseToDouble(dayStatus) > 0)) {
											//System.out.println("Sub Task isApproved ===>> " + isApproved);
											if((isApproved!=null && isApproved.equals("0")) && strSessionEmpId!=null && strEmpId!=null  && strSessionEmpId.equals(strEmpId) && fillUserType != null && fillUserType.equals("MY")) {
									%>
									
									<% if(hmTaskDescri != null && hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()) != null && !hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()).equals("") && uF.parseToDouble(dayStatus) > 0) { %>
										<a href="javascript:void(0);" onclick="showTaskDescription('<%=URLEncoder.encode(hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()), "UTF-8") %>');"> <span class="worklate" style="height: 10px;">&nbsp;</span></a>
									<% } %>
									<a href="DailyTimesheet.action?strPaycycle=<%=request.getAttribute("strPaycycle") %>&strEmpId=<%=request.getAttribute("strEmpId") %>&frmDate=<%=(String)alDates.get(i)%>&toDate=<%=(String)alDates.get(i)%>&strProject=<%=(strProjectIdST != null && !strProjectIdST.equals("null") && !strProjectIdST.equals("")) ? strProjectIdST : ""%>&strActivity=<%=strSubTaskId%>&strActivityTaskId=<%=taskId%>&filterBy=<%=filterBy %>&strYear=<%=strYear %>&strMonth=<%=strMonth %>&activityType=E&submit1=&pageType=<%=pageType %>&fillUserType=<%=fillUserType %>">
										<%=dayStatus%>
									</a>	
									<% } else if(isApproved!=null && (isApproved.equals("1") || isApproved.equals("2")) && strSessionEmpId!=null && strEmpId!=null && strSessionEmpId.equals(strEmpId) && fillUserType != null && fillUserType.equals("MY")){%>
									
									<% if(hmTaskDescri != null && hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()) != null && !hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()).equals("") && uF.parseToDouble(dayStatus) > 0) { %>
										<a href="javascript:void(0);" onclick="showTaskDescription('<%=URLEncoder.encode(hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()), "UTF-8") %>');"> <span class="worklate" style="height: 10px;">&nbsp;</span></a>
									<% } %>	
										<%=dayStatus%>
										
									<% } else if(isApproved!=null && isApproved.equals("0")) { %>
										<% if(hmTaskDescri != null && hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()) != null && !hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()).equals("") && uF.parseToDouble(dayStatus) > 0) { %>
											<a href="javascript:void(0);" onclick="showTaskDescription('<%=URLEncoder.encode(hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()), "UTF-8") %>');"> <span class="worklate" style="height: 10px;">&nbsp;</span></a>
										<% } %>
										<%=dayStatus %>
										
									<% } else if(isApproved!=null && isApproved.equals("2")) { %>
									
									<% if(hmTaskDescri != null && hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()) != null && !hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()).equals("") && uF.parseToDouble(dayStatus) > 0) { %>
										<a href="javascript:void(0);" onclick="showTaskDescription('<%=URLEncoder.encode(hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()), "UTF-8") %>');"> <span class="worklate" style="height: 10px;">&nbsp;</span></a>
									<% } %>
										<%=dayStatus%>
									
									<% } else { %>
									
									<% if(hmTaskDescri != null && hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()) != null && !hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()).equals("") && uF.parseToDouble(dayStatus) > 0) { %>
										<a href="javascript:void(0);" onclick="showTaskDescription('<%=URLEncoder.encode(hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()), "UTF-8") %>');"> <span class="worklate" style="height: 10px;">&nbsp;</span></a>
									<% } %>
									<a href="DailyTimesheet.action?strPaycycle=<%=request.getAttribute("strPaycycle") %>&strEmpId=<%=request.getAttribute("strEmpId") %>&frmDate=<%=(String)alDates.get(i)%>&toDate=<%=(String)alDates.get(i)%>&strProject=<%=(strProjectIdST != null && !strProjectIdST.equals("null") && !strProjectIdST.equals("")) ? strProjectIdST : ""%>&strActivity=<%=strSubTaskId%>&strActivityTaskId=<%=taskId%>&filterBy=<%=filterBy %>&strYear=<%=strYear %>&strMonth=<%=strMonth %>&activityType=E&submit1=&pageType=<%=pageType %>&fillUserType=<%=fillUserType %>">
										<%=dayStatus%>
									</a>	
									<%}
									} else {
										out.println(dayStatus);
									}
										} %>
								</td>
							<%
							double dblHrs = uF.parseToDouble((String)hmDates.get((String)alDates.get(i)+"_"+sb.toString()));
							dblHrs += uF.parseToDouble((String)hmTotal.get((String)alDates.get(i)));
							hmTotal.put((String)alDates.get(i), uF.formatIntoTwoDecimal(dblHrs)+"");
							
							Date currDt = uF.getDateFormat((String)alDates.get(i), IConstants.DATE_FORMAT);
							if(activityType == null || (activityType != null && activityType.equals("E") && (currDt.before(frmDt) || currDt.after(toDt)) || !strActivity.equals(strSubTaskId))) {
								double dblDayHrs = uF.parseToDouble((String)hmDates.get((String)alDates.get(i)+"_"+sb.toString()));
								dblDayHrs += uF.parseToDouble((String)hmDayTotalHrs.get((String)alDates.get(i)));
								hmDayTotalHrs.put((String)alDates.get(i), uF.formatIntoTwoDecimal(dblDayHrs)+"");
							}
							%>
							
							<% } %>
						</tr>
						
					<% } %>
						
						
						<% if(hmTaskIsBillable != null && uF.parseToBoolean(hmTaskIsBillable.get(strSubTaskId))) { %>
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
								
								String strBgColor = null;
								
								if(strBgColor==null) {
									strBgColor = (String) hmLeavesColour.get((String)hmLeaves.get(alDates.get(i)));
									strText = (String)hmLeaveCode.get((String)hmLeaves.get(alDates.get(i))); 
								}
								
								if(strBgColor==null) {
									if(alEmpCheckRosterWeektype.contains((String)request.getAttribute("strEmpId"))) {
										if(rosterWeeklyOffSet.contains(alDates.get(i))) {
											strBgColor =IConstants.WEEKLYOFF_COLOR;
											strText = "W/O";
										}
									} else if(weeklyOffSet.contains(alDates.get(i))) {
										strBgColor =IConstants.WEEKLYOFF_COLOR;
										strText = "W/O";
									}
								}
								
								if(strBgColor==null) {
									strBgColor = (String)hmHolidayDates.get(uF.getDateFormat((String) alDates.get(i), IConstants.DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId);
									if(strBgColor!=null) {
										strText = "H";
									}
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
									
									if(hmCheckUserApproval!=null && uF.parseToBoolean(hmCheckUserApproval.get(taskId+"_"+strSessionEmpId))==true) { %>
								
									<% if(hmTaskDescri != null && hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()) != null && !hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()).equals("") && uF.parseToDouble(dayStatus) > 0) { %>
										<a href="javascript:void(0);" onclick="showTaskDescription('<%=URLEncoder.encode(hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()), "UTF-8") %>');"> <span class="worklate" style="height: 10px;">&nbsp;</span></a>
									<% } %>
									<%=dayStatus%>
									
									<% } else if(flagST && strUserType.equalsIgnoreCase(IConstants.HRMANAGER) && (pageType == null || !pageType.equals("MP"))) { %>
									
									<% if(hmTaskDescri != null && hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()) != null && !hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()).equals("") && uF.parseToDouble(dayStatus) > 0) { %>
										<a href="javascript:void(0);" onclick="showTaskDescription('<%=URLEncoder.encode(hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()), "UTF-8") %>');"> <span class="worklate" style="height: 10px;">&nbsp;</span></a>
									<% } %>
										<%=dayStatus %>
										
									<% } else {
										if(dayStatus != null && (dayStatus.trim().equals("0.00") || uF.parseToDouble(dayStatus) > 0)) {	
											if((isApproved!=null && isApproved.equals("0")) && strSessionEmpId!=null && strEmpId!=null  && strSessionEmpId.equals(strEmpId) && fillUserType != null && fillUserType.equals("MY")) {
									%>
									
									<% if(hmTaskDescri != null && hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()) != null && !hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()).equals("") && uF.parseToDouble(dayStatus) > 0) { %>
										<a href="javascript:void(0);" onclick="showTaskDescription('<%=URLEncoder.encode(hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()), "UTF-8") %>');"> <span class="worklate" style="height: 10px;">&nbsp;</span></a>
									<% } %>
									<a href="DailyTimesheet.action?strPaycycle=<%=request.getAttribute("strPaycycle") %>&strEmpId=<%=request.getAttribute("strEmpId") %>&frmDate=<%=(String)alDates.get(i)%>&toDate=<%=(String)alDates.get(i)%>&strProject=<%=(strProjectIdST != null && !strProjectIdST.equals("null") && !strProjectIdST.equals("")) ? strProjectIdST : ""%>&strActivity=<%=strSubTaskId%>&strActivityTaskId=<%=taskId%>&filterBy=<%=filterBy %>&strYear=<%=strYear %>&strMonth=<%=strMonth %>&activityType=E&submit1=&pageType=<%=pageType %>&fillUserType=<%=fillUserType %>">
										<%=dayStatus%>
									</a>	
									<% } else if(isApproved!=null && (isApproved.equals("1") || isApproved.equals("2")) && strSessionEmpId!=null && strEmpId!=null && strSessionEmpId.equals(strEmpId) && fillUserType != null && fillUserType.equals("MY")) { %>
									
									<% if(hmTaskDescri != null && hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()) != null && !hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()).equals("") && uF.parseToDouble(dayStatus) > 0) { %>
										<a href="javascript:void(0);" onclick="showTaskDescription('<%=URLEncoder.encode(hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()), "UTF-8") %>');"> <span class="worklate" style="height: 10px;">&nbsp;</span></a>
									<% } %>	
										<%=dayStatus %>
										
									<% } else if(isApproved!=null && isApproved.equals("0")) { %>
										<% if(hmTaskDescri != null && hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()) != null && !hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()).equals("") && uF.parseToDouble(dayStatus) > 0) { %>
											<a href="javascript:void(0);" onclick="showTaskDescription('<%=URLEncoder.encode(hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()), "UTF-8") %>');"> <span class="worklate" style="height: 10px;">&nbsp;</span></a>
										<% } %>
										<%=dayStatus %>
										
									<% } else if(isApproved!=null && isApproved.equals("2")) { %>
									
									<% if(hmTaskDescri != null && hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()) != null && !hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()).equals("") && uF.parseToDouble(dayStatus) > 0) { %>
										<a href="javascript:void(0);" onclick="showTaskDescription('<%=URLEncoder.encode(hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()), "UTF-8") %>');"> <span class="worklate" style="height: 10px;">&nbsp;</span></a>
									<% } %>
										<%=dayStatus %>
									
									<% } else { %>
									
									<% if(hmTaskDescri != null && hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()) != null && !hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()).equals("") && uF.parseToDouble(dayStatus) > 0) { %>
										<a href="javascript:void(0);" onclick="showTaskDescription('<%=URLEncoder.encode(hmTaskDescri.get((String)alDates.get(i)+"_"+sb.toString()), "UTF-8") %>');"> <span class="worklate" style="height: 10px;">&nbsp;</span></a>
									<% } %>
									<a href="DailyTimesheet.action?strPaycycle=<%=request.getAttribute("strPaycycle") %>&strEmpId=<%=request.getAttribute("strEmpId") %>&frmDate=<%=(String)alDates.get(i)%>&toDate=<%=(String)alDates.get(i)%>&strProject=<%=(strProjectIdST != null && !strProjectIdST.equals("null") && !strProjectIdST.equals("")) ? strProjectIdST : ""%>&strActivity=<%=strSubTaskId%>&strActivityTaskId=<%=taskId%>&filterBy=<%=filterBy %>&strYear=<%=strYear %>&strMonth=<%=strMonth %>&activityType=E&submit1=&pageType=<%=pageType %>&fillUserType=<%=fillUserType %>">
										<%=dayStatus %>
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
							
							Date currDt = uF.getDateFormat((String)alDates.get(i), IConstants.DATE_FORMAT);
							if(activityType == null || (activityType != null && activityType.equals("E") && (currDt.before(frmDt) || currDt.after(toDt)) || !strActivity.equals(strSubTaskId))) {
								double dblDayBillableHrs = uF.parseToDouble((String)hmDatesBillableHrs.get((String)alDates.get(i)+"_"+sb.toString()));
								dblDayBillableHrs += uF.parseToDouble((String)hmDayTotalBillableHrs.get((String)alDates.get(i)));
								hmDayTotalBillableHrs.put((String)alDates.get(i), uF.formatIntoTwoDecimal(dblDayBillableHrs)+"");
							}
							%>
							
							<% } %>
						</tr>
						<% } %>
					<% } %>
					<% 
					strProjectIdOldST = strProjectIdNewST;
					} }
					%>
						
					<% } } %>
						
						<tr>
							<td></td>
							<td colspan="2">Total Hrs.</td>
							<td></td>
							<td></td>
							<%for(int i=0; i<alDates.size(); i++) {
								String strText = "-";
								
								String strBgColor = null;
								String bgcolor=null;
								
								if(strBgColor==null) {
									strBgColor = (String) hmLeavesColour.get((String)hmLeaves.get(alDates.get(i)));
									strText = (String)hmLeaveCode.get((String)hmLeaves.get(alDates.get(i))); 
								}
								
								if(strBgColor==null) {
									if(alEmpCheckRosterWeektype.contains((String)request.getAttribute("strEmpId"))) {
										if(rosterWeeklyOffSet.contains(alDates.get(i))){
											strBgColor =IConstants.WEEKLYOFF_COLOR;
											strText = "W/O";
										}
									} else if(weeklyOffSet.contains(alDates.get(i))) {
										strBgColor =IConstants.WEEKLYOFF_COLOR;
										strText = "W/O";
									}
								}
								
								
								if(strBgColor==null) {
									strBgColor = (String)hmHolidayDates.get(uF.getDateFormat((String) alDates.get(i), IConstants.DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId);
									if(strBgColor!=null) {
										strText = "H";
									}
								}
								
								if(uF.getDateFormat(currDate, IConstants.DATE_FORMAT).equals(uF.getDateFormat(alDates.get(i).toString(), IConstants.DATE_FORMAT))) {
									strBgColor = "#EFEFEF";
								}
								
								if(strText==null) {
									strText = "-";
								}
								String dayTotStatus = uF.showData((String)hmTotal.get((String)alDates.get(i)), strText);
								if(uF.parseToDouble(dayTotStatus) > 0) {
									dayTotStatus = uF.getTotalTimeMinutes100To60(dayTotStatus);
								}
							%>
								<td align="right" style="background-color: <%=bgcolor==null ? strBgColor:bgcolor%>">
									<input type="hidden" name="strDate" value="<%=(String)alDates.get(i)%>" />
									<input type="hidden" name="strTime" value="<%=uF.showData((String)hmDayTotalHrs.get((String)alDates.get(i)), "0")%>" />
									
									<%=dayTotStatus %>
								</td>
							<% } %>
						</tr>
						
						<% if(hmProjectsBillableHrs != null && !hmProjectsBillableHrs.isEmpty() && hmProjectsBillableHrs.size()>0) { %>
						<tr>
							<td></td>
							<td colspan="2">Total Billable Hrs.</td>
							<td></td>
							<td></td>
							<%for(int i=0; i<alDates.size(); i++) { 
								String strText = "-";
								String strBgColor = null;
								String bgcolor=null;
								
								if(strBgColor==null) {
									strBgColor = (String) hmLeavesColour.get((String)hmLeaves.get(alDates.get(i)));
									strText = (String)hmLeaveCode.get((String)hmLeaves.get(alDates.get(i))); 
								}
								
								if(strBgColor==null) {
									if(alEmpCheckRosterWeektype.contains((String)request.getAttribute("strEmpId"))){
										if(rosterWeeklyOffSet.contains(alDates.get(i))){
											strBgColor =IConstants.WEEKLYOFF_COLOR;
											strText = "W/O";
										}
									} else if(weeklyOffSet.contains(alDates.get(i))) {
										strBgColor =IConstants.WEEKLYOFF_COLOR;
										strText = "W/O";
									}
								}
			
								if(strBgColor==null) {
									strBgColor = (String)hmHolidayDates.get(uF.getDateFormat((String) alDates.get(i), IConstants.DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId);
									if(strBgColor!=null) {
										strText = "H";
									}
								}
								
								if(currDate.equals(alDates.get(i).toString())) {
									strBgColor = "#EFEFEF";
								}
								
								if(strText==null) {
									strText = "-";
								}
								String dayBillTotStatus = uF.showData((String)hmTotalBillableHrs.get((String)alDates.get(i)), strText);
								if(uF.parseToDouble(dayBillTotStatus) > 0) {
									dayBillTotStatus = uF.getTotalTimeMinutes100To60(dayBillTotStatus);
								}
							%>
								<td align="right" style="background-color: <%=bgcolor==null ? strBgColor:bgcolor%>">
									<input type="hidden" name="strBillableTime" value="<%=uF.showData((String)hmDayTotalBillableHrs.get((String)alDates.get(i)), "0")%>" />
									<%=dayBillTotStatus%>
								</td>
							<% } %>
						</tr>
						<% } %>
						
						<% if(claimExtraWorkFlag) { %>
							<tr>
								<td></td>
								<td colspan="2">Claim Extra Working</td>
								<td></td>
								<td></td>
								<%for(int i=0; i<alDates.size(); i++) {
									String strText = "-";
									
									String strBgColor = null;
									String bgcolor=null;
									
									if(strBgColor==null) {
										strBgColor = (String) hmLeavesColour.get((String)hmLeaves.get(alDates.get(i)));
										strText = (String)hmLeaveCode.get((String)hmLeaves.get(alDates.get(i))); 
									}
									
									if(strBgColor==null) {
										if(alEmpCheckRosterWeektype.contains((String)request.getAttribute("strEmpId"))) {
											if(rosterWeeklyOffSet.contains(alDates.get(i))) {
												strBgColor =IConstants.WEEKLYOFF_COLOR;
												strText = "W/O";
											}
										} else if(weeklyOffSet.contains(alDates.get(i))) {
											strBgColor =IConstants.WEEKLYOFF_COLOR;
											strText = "W/O";
										}
									}
									
									if(strBgColor==null) {
										strBgColor = (String)hmHolidayDates.get(uF.getDateFormat((String) alDates.get(i), IConstants.DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId);
										if(strBgColor!=null) {
											strText = "H";
										}
									}
									
									if(currDate.equals(alDates.get(i).toString())) {
										strBgColor = "#EFEFEF";
									}
									
									if(strText==null) {
										strText = "-";
									}
								%>
									<td align="center" style="background-color: <%=bgcolor==null ? strBgColor:bgcolor%>">
									<%
									//if(((String) hmWeekendMap.get((String) alDates.get(i)+"_"+strWLocationId)!=null || (String)hmHolidayDates.get(uF.getDateFormat((String) alDates.get(i), IConstants.DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId)!=null || (String)hmLeaveConstant.get(uF.getDateFormat((String) alDates.get(i), IConstants.DATE_FORMAT, CF.getStrReportDateFormat()))!=null ) && uF.parseToDouble((String)hmTotal.get((String)alDates.get(i)))>0){
									if(!hmCheckLeaveType.containsKey((String)hmLeaves.get(alDates.get(i)))) {
										String disabled="";
										String checked="";
										if(denyList.contains((String)alDates.get(i))) {
											disabled="disabled=\"disabled\"";
											checked="";
										} else if(compLeaveList.contains((String)alDates.get(i))) {
											disabled="disabled=\"disabled\"";
											checked="checked";
										} else if(applyLeaveList.contains((String)alDates.get(i))) {
											disabled="";
											checked="checked";
										}
									%>
									<input type="checkbox" name="compOff"  value="<%=(String)alDates.get(i)%>" <%=disabled %> <%=checked %> />
									<input type="hidden" name="compOffDate" value="<%=(String)alDates.get(i)%>" />
									<% } %>					
									
									</td>
								<% } %>
							</tr>
						<% } %>
						
						</tbody>
						<thead>
							<tr>
								<th>Client</th>
								<th>Project</th>
								<th>Task</th>
								<th>Hrs</th>
								<th>On/Off Site</th>
								<%
								for(int i=0; i<alDates.size(); i++) {
								String strText = "-";
								String strBgColor = null;
								
								if(strBgColor==null) {
									strBgColor = (String) hmLeavesColour.get((String)hmLeaves.get(alDates.get(i)));
									strText = (String)hmLeaveCode.get((String)hmLeaves.get(alDates.get(i))); 
								}  
								
								if(strBgColor==null) {
									if(alEmpCheckRosterWeektype.contains((String)request.getAttribute("strEmpId"))) {
										if(rosterWeeklyOffSet.contains(alDates.get(i))) {
											strBgColor =IConstants.WEEKLYOFF_COLOR;
											strText = "W/O";
										}
									} else if(weeklyOffSet.contains(alDates.get(i))) {
										strBgColor =IConstants.WEEKLYOFF_COLOR;
										strText = "W/O";
									}
								}
								
								String bgcolor=null;
								if(strBgColor==null) {
									strBgColor = (String)hmHolidayDates.get(uF.getDateFormat((String) alDates.get(i), IConstants.DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId);
									if(strBgColor!=null) {
										strText = "H";
									}
								}
								if(strText==null) {
									strText = "-";
								}
								%>
									<th style="background-color: <%=bgcolor==null ? strBgColor:bgcolor%>"><%=uF.getDateFormat((String)alDates.get(i), IConstants.DATE_FORMAT, "dd")%></th>
								<% } %>
							</tr>
						</thead>
					</table>
				</div> <!-- table-responsive -->
		
		
				<%
				String joiningDate=(String)request.getAttribute("joiningDate");
				if(maxTaskDate!=null && strUserType!=null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE)) { 
					Date maxTDate=uF.getDateFormat(maxTaskDate,IConstants.DATE_FORMAT);
					Date minTDate=uF.getDateFormat(minTaskDate,IConstants.DATE_FORMAT);
					Date joinDate=uF.getDateFormat(joiningDate,IConstants.DATE_FORMAT);
					for(int i=0; i<alDates.size(); i++) {
						
						Date tdate = uF.getDateFormat((String)alDates.get(i),IConstants.DATE_FORMAT);
						
						if(((maxTDate != null && maxTDate.before(tdate)) || (minTDate != null && minTDate.after(tdate))) && joinDate.before(tdate) && (hmWeekendMap.containsKey((String) alDates.get(i)+"_"+strWLocationId) || hmHolidayDates.containsKey(uF.getDateFormat((String) alDates.get(i), IConstants.DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId))) {
					%>
						<input type="hidden" name="unPaidHolidaysDate" value="<%=(String)alDates.get(i)%>" />
					<%		}
						}
					} %> 
		
		
					<input type="hidden" name="strPaycycle" value="<%=request.getAttribute("strPaycycle") %>" />
					<input type="hidden" name="checkTask" id="checkTask" value=""/>
					<div style="float:right; margin: 9px;">
						<input type="hidden" name="policy_id" id="policy_id" value="<%=request.getAttribute("policy_id") %>"/>
						<% 
							//System.out.println("nApproved1 ===>> " + nApproved1);
							//System.out.println("nSubmited ===>> " + uF.parseToInt(nSubmited));
							//System.out.println("strUserType ===>> " + strUserType);
							//System.out.println("fillUserType ===>> " + fillUserType);
							if(nApproved1.equals("0")  && strSessionEmpId!=null && strUserType!=null && (strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) || (fillUserType != null && fillUserType.equals("MY"))) ) { 
							//System.out.println("unlockList ===>> " + unlockList);
							//System.out.println("divpopup ===>> " + request.getAttribute("divpopup"));
						%>
						<span style="margin-left: 120px;">
						<%if(unlockList==null || unlockList.isEmpty() ||  unlockList.contains("0")) { %>
						<%if(request.getAttribute("divpopup") != null) { %>
							<%=request.getAttribute("divpopup") %>
						<% } %>
						<% } else { %>
							<input type="submit" name="submit1" value="Submit your timesheet" class="btn btn-primary" style="padding: 3px;" onclick="return confirm('Are you sure you want to submit your whole timesheet?\nYou will be unable to modify it once you submit.\nClick Ok to submit and Cancel to modify your timesheet.')"/>
						<% } %>
						</span>
						
						<%-- <div id="myModal" class="modal fade">
							<div class="modal-dialog">
								<div class="modal-content">
									<div class="modal-header">
										<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
										<h4 class="modal-title"><%=(String)request.getAttribute("modalTitle") %></h4>
									</div>
									<div class="modal-body">
										<%=(String)request.getAttribute("modalBody") %>
									</div>
								</div>
							</div>
						</div> --%>
						
						<div id="myModal" class="modal fade">
							<div class="modal-dialog">
								<div class="modal-content">
									<div class="modal-header">
										<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
										<h4 class="modal-title1"><%=(String)request.getAttribute("modalTitle") %></h4>
									</div>
									<div class="modal-body1">
										<%=(String)request.getAttribute("modalBody") %>
									</div>
									<div class="modal-footer">
						                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
						            </div>
								</div>
							</div>
						</div>
						
						<s:hidden name="filterBy" id="filterBy" />
						<%-- <s:hidden name="strPaycycle" id="strPaycycle" /> --%>
					    <s:hidden name="strYear" id="strYear" />
						<s:hidden name="strMonth" id="strMonth" />
						<s:hidden name="pageType" id="pageType" />
						<s:hidden name="fillUserType" id="fillUserType" />
						
						<input type="hidden" name="type" value="submit"/>
						<input type="hidden" name="timesheetId" value="<%=request.getAttribute("timesheetId")%>"/>
						<% 	//System.out.println("uF.parseToInt(nSubmited) ====>> " + uF.parseToInt(nSubmited));
							//System.out.println("strUserType ====>> " + strUserType);
							//System.out.println("fillUserType ====>> " + fillUserType);
							//System.out.println("pageType ====>> " + pageType);
							} else if(strUserType!=null && (strUserType.equalsIgnoreCase(IConstants.MANAGER) || 
								strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.CEO) ||
								strUserType.equalsIgnoreCase(IConstants.HOD) || strUserType.equalsIgnoreCase(IConstants.ACCOUNTANT) ||
								(strUserType.equalsIgnoreCase(IConstants.HRMANAGER) && pageType != null && pageType.equals("MP"))) 
								&& (fillUserType ==null || !fillUserType.equals("MY")) && uF.parseToInt(nSubmited)==0) { //empApproved!=null
						%>
						<input type="hidden" name="type" value="approve"/>
						<input type="hidden" name="strEmpId" value="<%=request.getParameter("strEmpId")%>"/>
						<input type="hidden" name="frmDate" value="<%=request.getParameter("frmDate")%>"/>
						<input type="hidden" name="toDate" value="<%=request.getParameter("toDate")%>"/>
						<input type="hidden" name="strProject" value="<%=request.getParameter("strProject")%>"/>
						<input type="hidden" name="strActivity" value="<%=request.getParameter("strActivity")%>"/>
						<input type="hidden" name="timesheetId" value="<%=request.getAttribute("timesheetId")%>"/>
						<s:hidden name="filterBy" id="filterBy" />
						<%-- <s:hidden name="strPaycycle" id="strPaycycle" /> --%>
					    <s:hidden name="strYear" id="strYear" />
						<s:hidden name="strMonth" id="strMonth" />
						<s:hidden name="pageType" id="pageType" />
						<s:hidden name="fillUserType" id="fillUserType" />
					
						<%
						//System.out.println(" before if flag ===>> " + flag);
						if(flag==false) {
						String remainDateCnt = (String) request.getAttribute("remainDateCnt");
						%>
						<input type="button" align="right" value="Approve Timesheet" class="btn btn-primary" style="padding: 3px;" onclick="checkApprovalStatus('<%=remainDateCnt %>');">
						<% } %>
						<input type="submit" align="right" name="unlock" value="Unlock Timesheet" class="btn btn-primary" style="padding: 3px;" onclick="return confirm('Are you sure you want to unlock this timesheet for the employee to modify it?')">
						<%
					 	} else if(uF.parseToInt(tsApproved) > 0) { //
					 		if(strUserType!=null && (strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.ADMIN)) && (pageType == null || !pageType.equals("MP"))  && (fillUserType ==null || !fillUserType.equals("MY")) && !checkPayroll) { // 
					 	%>
					 	<input type="hidden" name="type" value="approve"/>
						<input type="hidden" name="strEmpId" value="<%=request.getParameter("strEmpId")%>"/>
						<input type="hidden" name="frmDate" value="<%=request.getParameter("frmDate")%>"/>
						<input type="hidden" name="toDate" value="<%=request.getParameter("toDate")%>"/>
						<input type="hidden" name="strProject" value="<%=request.getParameter("strProject")%>"/>
						<input type="hidden" name="strActivity" value="<%=request.getParameter("strActivity")%>"/>
						<input type="hidden" name="timesheetId" value="<%=request.getAttribute("timesheetId")%>"/>
						<s:hidden name="filterBy" id="filterBy" />
						<%-- <s:hidden name="strPaycycle" id="strPaycycle" /> --%>
					    <s:hidden name="strYear" id="strYear" />
						<s:hidden name="strMonth" id="strMonth" />
						<s:hidden name="pageType" id="pageType" />
						<s:hidden name="fillUserType" id="fillUserType" />
						
					 	<input type="submit" align="right" name="unlock" value="Unlock Timesheet" class="btn btn-primary" style="padding: 3px;" onclick="return confirm('Are you sure you want to unlock this timesheet for the employee to modify it?')">
						<% } } %>
					</div>
				
				<div style="float:left; width: 100%; margin: 7px 0px 0px;">
					<div>AH:- Actual Hours</div>
					<div>BH:- Billable Hours</div>
					<div>ONS:- Onsite</div>
					<div>OFS:- Offsite</div>
				</div>
				<br /> <br />
				
			</div>	
		</div>
	</s:form>

	</div>
</section>


<script type="text/javascript">
	$(document).ready(function(){
		$(function() { 
			var strMinDate = document.getElementById("strMinDate").value;
			var strMaxDate = document.getElementById("strMaxDate").value;
			
			$("#idStdDateStart").datepicker({
				format : 'dd/mm/yyyy',
				startDate : new Date(strMinDate), 
			    endDate : new Date(strMaxDate),
				autoclose: true
			}).on('changeDate', function (selected) {
		        var minDate = new Date(selected.date.valueOf());
		        $('#idStdDateEnd').datepicker('setStartDate', minDate);
		    });
			$("#idStdDateEnd").datepicker({
				format : 'dd/mm/yyyy',
				startDate : new Date(strMinDate), 
			    endDate : new Date(strMaxDate),
				autoclose: true
			}).on('changeDate', function (selected) {
		        var minDate = new Date(selected.date.valueOf());
		        $('#idStdDateStart').datepicker('setEndDate', minDate);
		    });
			
			//$("input[name=strDate]").datepicker({dateFormat: 'dd/mm/yy'});
			$("input[name=strDate]").datepicker({
				format : 'dd/mm/yyyy',
				startDate : new Date(strMinDate), 
			    endDate : new Date(strMaxDate),
				autoclose: true
			});
		});
	});
	
	$(function(){
		$("#strClient").multiselect().multiselectfilter();
		$("#strProject").multiselect().multiselectfilter();
	}); 
</script>

<script type="text/javascript">
	var approvalUSer=<%=request.getAttribute("cnt")%>;
	var cycle=<%=request.getAttribute("cycle")%>;
	var emp=<%=request.getAttribute("emp") %>;
	
	function checkApprovalStatus(dates) {
		
		var xmlhttp = GetXmlHttpObject();
		if (xmlhttp == null) {
			alert("Browser does not support HTTP Request");
			return;
		} else {
			var xhr = $.ajax({
				url : "ValidateTimeSheet.action?emp_id=" + emp+"&timesheet_paycycle="+cycle,
				cache : false,
				success : function(data) {
					var checkTaskId = document.getElementsByName('checkTaskId');
					var selectID="";
					var j=0;
					//alert(checkTaskId.length);
					
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
					//alert("selectID   "+selectID);
					//alert(data);
					if(approvalUSer-data==1) {
						if(j!=checkTaskId.length) {
							alert("You are the last approver So Please, select all tasks.");
							return false;
						}
					}
					
					document.getElementById("checkTask").value=selectID;
					//alert(document.getElementById("checkTask").value);
					if(selectID=="") {
						alert("Please, select the task?");
					} else {
						if(dates != '0') { 
							//var varDates = dates.bold();
							if(confirm('KINDLY CHECK YOUR TIMESHEET. \n BLANK ENTRIES ' + dates+'')) {
								if(confirm('Are you sure, you want to approve this timesheet?')) {
									var form_data = $("#ftimesheet").serialize();
									//alert("form_data ========>> " + form_data);
									$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
									$.ajax({
										type : 'POST',
										url: 'DailyTimesheet.action?submitType=LOAD',
										data: form_data,
										success: function(result) {
											$("#divResult").html(result);
								   		}
									});
							}	
						}
							//alert(""+dates+"");
						} else {
							if(confirm('Are you sure, you want to approve this timesheet?')) {
								var form_data = $("#ftimesheet").serialize();
								//alert("form_data ========>> " + form_data);
								$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
								$.ajax({
									type : 'POST',
									url: 'DailyTimesheet.action?submitType=LOAD',
									data: form_data,
									success: function(result) {
										$("#divResult").html(result);
							   		}
								});
							}
						}
					}
				}
			});

		}
	}
	
	function checkApprovedUser(timesheet_paycycle,emp_id){

		var xmlhttp = GetXmlHttpObject();
		if (xmlhttp == null) {
			alert("Browser does not support HTTP Request");
			return;
		} else {
			var xhr = $.ajax({
				url : "ValidateTimeSheet.action?emp_id=" + emp_id+"&timesheet_paycycle="+timesheet_paycycle,
				cache : false,
				success : function(data) {
					return data;
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
	
		function unLockStatus() {
			//checkTaskId 
			var checkTaskId = document.getElementsByName('checkTaskId');
			var selectID="";
			var j=0;
			//alert(checkTaskId.length);
		
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
			//alert("selectID   "+selectID);
			document.getElementById("checkTask").value=selectID;
			alert(document.getElementById("checkTask").value);
			if(selectID=="") {
				alert("Please, select the task?");
			} else {
				if(confirm(' Are you sure you want to unlock this timesheet for the employee to modify it?')){
					
					document.getElementById("unlock").value="unlock";
					var form_data = $("#ftimesheet").serialize();
					$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
					$.ajax({
						type : 'POST',
						url: 'DailyTimesheet.action?submitType=LOAD&unlock=Unlock Timesheet',
						data: form_data,
						success: function(result) {
							$("#divResult").html(result);
				   		}
					});
				}
			}
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
            <div class="modal-body" style="height:400px;overflow-y:auto;padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>

		
</div>