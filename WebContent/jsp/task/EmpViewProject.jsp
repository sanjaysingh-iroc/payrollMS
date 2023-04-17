
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.konnect.jpms.task.GetPriorityList"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ page buffer="16kb"%>

<!-- <link rel="stylesheet" href="css/font-awesome.min.css">

<link rel="stylesheet" href="css/data-table/bootstrap-table.css">
<link rel="stylesheet" href="css/data-table/bootstrap-editable.css"> -->
    
<style type="text/css">
	.highslide-wrapper .highslide-html-content {
	    width: 650px;
	}
	
	.vertical-menu {
  width: 50px;
  height:100px;
}

.vertical-menu a {
  background-color: #eee;
  color: black;
  display: block;
  padding: 12px;
  text-decoration: none;
}

.vertical-menu a:hover {
  background-color: #ccc;
}

.vertical-menu a.active {
  background-color: #4CAF50;
  color: white;
}
	
	
</style>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/highslide/highslide-with-html.js"> </script>
<link type="text/css" rel="stylesheet" href="<%= request.getContextPath()%>/css/highslide/highslide.css" />

<style>
.greenbox {
	height: 20px;
	background-color:#00FF00; /* the critical component */
}

#redbox {
	height: 20px;
	background-color:#FF0000; /* the critical component */
}

#yellowbox {
	height: 20px;
	background-color:#FFFF00; /* the critical component */
}

.outbox {
	height: 20px;
	width: 100%;
	background-color:#D8D8D8; /* the critical component */
}

.anaAttrib1 {
	font-size: 12px;
	font-family: digital;
	color: #3F82BF;
	font-weight: bold;
	text-align: center;
	height: 22px;
}

.divh {
	font-weight: bold;
    font-size: 12px;
    font-family: -apple-system, system-ui, BlinkMacSystemFont, Segoe UI,
		Roboto, Helvetica Neue, Fira Sans, Ubuntu, Oxygen, Oxygen Sans,
		Cantarell, Droid Sans, Apple Color Emoji, Segoe UI Emoji,
		Segoe UI Symbol, Lucida Grande, Helvetica, Arial, sans-serif;
}

.divb {
	font-size: 12px;
}
</style>


<script type="text/javascript">

hs.graphicsDir = '<%=request.getContextPath()%>/images1/highslide/graphics/';
hs.outlineType = 'rounded-white';
hs.wrapperClassName = 'draggable-header';

$(function () {
	/* $("input[name='btnSubmit']").click(function(){
		$("#"+ this.form.id ).find('.validateRequired').filter(':hidden').prop('required',false);
		$("#"+ this.form.id ).find('.validateRequired').filter(':visible').prop('required',true);
	}); */
	
	$("input[type='submit']").click(function(){
		$(".validateRequired").prop('required',true);
	});
	
});

	function viewTaskActHistory(taskId, proId, proType) {
		//alert("taskId ===>> " + taskId);    
		var status = document.getElementById('taskActHistoryStatus'+taskId).value;
		if(status == '0') {
			document.getElementById('taskAllEventDiv').style.display = 'block';
			
			document.getElementById('proDocsSpanStatus'+taskId).value = '0';
			document.getElementById('proDocsDownarrowSpan'+taskId).style.display = 'block';
			document.getElementById('proDocsUparrowSpan'+taskId).style.display = 'none';
			
			document.getElementById('taskActHistoryStatus'+taskId).value = '1';
			document.getElementById('taskActHistDownarrowSpan'+taskId).style.display = 'none';
			document.getElementById('taskActHistUparrowSpan'+taskId).style.display = 'block';
			
			document.getElementById('taskActHistory_'+taskId).style.display = 'block';
			document.getElementById('editTaskSubtaskDiv_'+taskId).style.display = 'none';
			document.getElementById('proDocsDiv_'+taskId).style.display = 'none';
			document.getElementById('reassignTaskSubTaskDiv_'+taskId).style.display = 'none';
			document.getElementById('rescheduleTaskSubTaskDiv_'+taskId).style.display = 'none';
			document.getElementById('newTaskDiv').style.display = 'none';
			document.getElementById('newSubTaskDiv').style.display = 'none';
			
	    	$('<img src=\"images1/ajax-loading-1.gif\"/>').appendTo("#taskActHistory_"+taskId);
			//alert("taskId 000 ===>> " + taskId);
			getContent('taskActHistory_'+taskId, 'EmpTaskActivityHistory.action?proId='+ proId +'&taskId='+ taskId +'&proType='+ proType);
		} else {
			document.getElementById('taskAllEventDiv').style.display = 'none';
			if(document.getElementById('taskActHistoryStatus'+taskId)) {
				document.getElementById('taskActHistoryStatus'+taskId).value = '0';
				document.getElementById('taskActHistDownarrowSpan'+taskId).style.display = 'block';
				document.getElementById('taskActHistUparrowSpan'+taskId).style.display = 'none';
			}
			document.getElementById('taskActHistory_'+taskId).innerHTML = '';
			document.getElementById('taskActHistory_'+taskId).style.display = 'none';
		}		
	}
	
	
	
	function endTask(tid, pid, taskId) {
		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$("#modalInfo").show();
		$(".modal-title").html('End Task');
		$.ajax({
			url : 'EndTaskPopup.action?id='+tid+'&taskId='+taskId+'&pro_id='+pid+'&fromPage=MyProject',
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}
	
	
	function SendProId(id) {
		if(id!=null) {
			/* var proType = document.getElementById("proType").value;
			window.location ='EmpViewProject.action?pro_id='+id+'&proType='+proType; */
			$("#mainDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$.ajax({
				type : 'POST',
				url: 'EmpViewProject.action?pro_id='+id+'&proType='+proType,
				success: function(result){
					$("#mainDivResult").html(result);
				},
				error: function(result){
					$.ajax({
						url: 'MyTasks.action?proType='+proType,
						cache: true,
						success: function(result){
							$("#mainDivResult").html(result);
				   		}
					});
				}
			});
		}
	}
	
	
		function startManually(tid, pid) {
			var dialogEdit = '.modal-body';
			$(dialogEdit).empty();
			$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$("#modalInfo").show();
			$(".modal-title").html('Start Manually');
			$.ajax({
				url : 'StartTaskManually.action?id='+tid+'&pro_id='+pid,
				cache : false,
				success : function(data) {
					$(dialogEdit).html(data);
				}
			});
		}
		
		
		function viewAndAddNewDocuments(proId, taskId, taskName, proType, fromPage) {
			var dialogEdit = '.modal-body';
			$(dialogEdit).empty();
			$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$("#modalInfo").show();
			var height = $(window).height()* 0.80;
			var width = $(window).width()* 0.90;
			$(".modal-dialog").css("height", height);
			$(".modal-dialog").css("width", width);
			$(".modal-title").html('Documents of '+taskName);
			$.ajax({
				url : 'ProjectDocuments.action?proId='+proId+'&taskId='+taskId+'&proType='+proType+'&fromPage='+fromPage,
				cache : false,
				success : function(data) {
					$(dialogEdit).html(data);
				}
			});
		}
		
		
		/* getContent('modalBody', 'IFrameStartTaskManually.action?id='+tid+'&pro_id='+pid);
		document.getElementById("modalTitle").innerHTML = "Start Manually";
		$("#myModal").modal('show'); */
	
	
	function viewSummary(id) {
		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$("#modalInfo").show();
		$(".modal-title").html('Project Summary');
		$.ajax({
			url : 'ProjectSummaryView.action?pro_id='+id,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}
	

function addActivity(id) {
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$(".modal-title").html('Add New Task');
	$.ajax({
		url : 'AddNewActivityPopup.action?pro_id='+id+'&type=Task&fromPage=MP',
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}



function editActivity(id, pid) {
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$(".modal-title").html('Edit Task');
	$.ajax({
		url : 'AddNewActivityPopup.action?operation=E&task_id='+id+'&pro_id='+pid+'&type=Task&fromPage=MP',
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}



	function executeTaskActivities(val, taskId, proId, activityId, activityRunningFlag, timeApproveFlag, stCnt, workedTime, proType) {
		//alert("timeApproveFlag ==>> "+ timeApproveFlag +"  == activityRunningFlag ===>> " + activityRunningFlag);
		document.getElementById('editTaskSubtaskDiv_'+taskId).style.display = 'none';
		if(val == '1') {
			if(timeApproveFlag == 'true') {
				alert('Timesheet for this date is already approved.');
			} else {
				if(activityRunningFlag == 'true') {
					if(confirm('You are already working on one activity, would you like to finish that activity by starting the new activity?')) {
						start123(taskId, proId);
					}
				} else {
					if(confirm('Are you sure, you want to start this task?')) {
						start123(taskId, proId);
					}
				}
			}
		} else if(val == '2') {
				if(activityRunningFlag == 'true') {
					if(confirm('You are already working on one activity, would you like to finish that activity by starting the new activity?')) {
						startManually(taskId, proId);
					}
				} else {
					startManually(taskId, proId);
				}
			/* } */
		} else if(val == '3') {
			endTask(taskId, proId, activityId);
		} else if(val == '4') {
			if(confirm('Are you sure, you want to complete this task?')) {
				$("#mainDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
				$.ajax({
					type : 'POST',
					url: 'EmpViewProject.action?taskId='+taskId+'&completeTask=C',
					success: function(result){
						$("#mainDivResult").html(result);
					},
					error: function(result){
						$.ajax({
							url: 'MyTasks.action',
							cache: true,
							success: function(result){
								$("#mainDivResult").html(result);
					   		}
						});
					}
				});
				/* window.location = 'EmpViewProject.action?taskId='+taskId+'&completeTask=C'; */
			}
		}
		document.getElementById('actions'+taskId).selectedIndex = '0';
		
	}

	
	function start123(id, pid) {
		$("#subSubDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: 'TaskUpdateTime.action?type=start&id='+id+'&pro_id='+pid,
			success: function(result){
				$("#subSubDivResult").html(result);
			},
			error: function(result){
				$.ajax({
					url: 'EmpViewProject.action?taskId='+id,
					cache: true,
					success: function(result){
						$("#subSubDivResult").html(result);
			   		}
				});
			}
		});
	}


	function executeTaskAction(val, taskId, proId, activityId, activityRunningFlag, timeApproveFlag, stCnt, workedTime, proType) {
		//alert("timeApproveFlag ==>> "+ timeApproveFlag +"  == activityRunningFlag ===>> " + activityRunningFlag);
		document.getElementById('editTaskSubtaskDiv_'+taskId).style.display = 'none';
		document.getElementById('editTaskSubtaskDiv_'+taskId).style.display = 'none';
		if(val == '5') {
			if(parseFloat(workedTime)>0) {
				alert('You can not delete this task as user has already booked the time against this task.');
			} else {
				if(parseFloat(stCnt)>0) {
					alert('Please first delete sub task of this task then delete this task.');
				} else {
					$("#mainDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
					$.ajax({
						type : 'POST',
						url: 'EmpViewProject.action?addTask=Delete&taskId='+taskId+'&proType='+proType,
						success: function(result){
							$("#mainDivResult").html(result);
						},
						error: function(result){
							$.ajax({
								url: 'MyTasks.action?proType='+proType,
								cache: true,
								success: function(result){
									$("#mainDivResult").html(result);
						   		}
							});
						}
					});
					/* var action = 'EmpViewProject.action?addTask=Delete&taskId='+taskId+'&proType='+proType;
					window.location = action; */
				}
			}
		} else if(val == '6') {
			document.getElementById('taskAllEventDiv').style.display = 'block';
			
			document.getElementById('editTaskSubtaskDiv_'+taskId).style.display = 'block';

			document.getElementById('proDocsSpanStatus'+taskId).value = '0';
			document.getElementById('proDocsDownarrowSpan'+taskId).style.display = 'block';
			document.getElementById('proDocsUparrowSpan'+taskId).style.display = 'none';
			
			if(document.getElementById('taskActHistoryStatus'+taskId)) {
				document.getElementById('taskActHistoryStatus'+taskId).value = '0';
				document.getElementById('taskActHistDownarrowSpan'+taskId).style.display = 'block';
				document.getElementById('taskActHistUparrowSpan'+taskId).style.display = 'none';
			}
			document.getElementById('taskActHistory_'+taskId).style.display = 'none';
			document.getElementById('proDocsDiv_'+taskId).style.display = 'none';
			document.getElementById('reassignTaskSubTaskDiv_'+taskId).style.display = 'none';
			document.getElementById('rescheduleTaskSubTaskDiv_'+taskId).style.display = 'none';
			document.getElementById('newTaskDiv').style.display = 'none';
			document.getElementById('newSubTaskDiv').style.display = 'none';
		}
		document.getElementById('action'+taskId).selectedIndex = '0';
	}
	
	
	function executeNewTaskActions(val, taskId, t_st_Type, proType) {
		//alert("timeApproveFlag ==>> "+ timeApproveFlag +"  == activityRunningFlag ===>> " + activityRunningFlag);
		if(val == '1') {
			var sttName = 'task';
			if(t_st_Type == 'ST') {
				sttName = 'sub-task';
			}
			if(confirm('Are you sure, you want to accept this '+sttName+'?')) {
				$("#mainDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
				$.ajax({
					type : 'POST',
					url: 'EmpViewProject.action?taskAcceptStatus=1&taskId='+taskId+'&proType='+proType,
					success: function(result){
						$("#mainDivResult").html(result);
					},
					error: function(result){
						$.ajax({
							url: 'MyTasks.action?proType='+proType,
							cache: true,
							success: function(result){
								$("#mainDivResult").html(result);
					   		}
						});
					}
				});
				/* var action = 'EmpViewProject.action?taskAcceptStatus=1&taskId='+taskId+'&proType='+proType;
				window.location = action; */
			}
		} else if(val == '3') {
			document.getElementById('taskAllEventDiv').style.display = 'block';
			document.getElementById('reassignTaskSubTaskDiv_'+taskId).style.display = 'block';
			document.getElementById('rescheduleTaskSubTaskDiv_'+taskId).style.display = 'none';
			
			document.getElementById('editTaskSubtaskDiv_'+taskId).style.display = 'none';
			document.getElementById('proDocsSpanStatus'+taskId).value = '0';
			document.getElementById('proDocsDownarrowSpan'+taskId).style.display = 'block';
			document.getElementById('proDocsUparrowSpan'+taskId).style.display = 'none';
			
			if(document.getElementById('taskActHistoryStatus'+taskId)) {
				document.getElementById('taskActHistoryStatus'+taskId).value = '0';
				document.getElementById('taskActHistDownarrowSpan'+taskId).style.display = 'block';
				document.getElementById('taskActHistUparrowSpan'+taskId).style.display = 'none';
			}
			document.getElementById('taskActHistory_'+taskId).style.display = 'none';
			document.getElementById('proDocsDiv_'+taskId).style.display = 'none';
			document.getElementById('newTaskDiv').style.display = 'none';
			document.getElementById('newSubTaskDiv').style.display = 'none';
		} else if(val == '2') {
			document.getElementById('taskAllEventDiv').style.display = 'block';
			document.getElementById('reassignTaskSubTaskDiv_'+taskId).style.display = 'none';
			document.getElementById('rescheduleTaskSubTaskDiv_'+taskId).style.display = 'block';
			
			document.getElementById('editTaskSubtaskDiv_'+taskId).style.display = 'none';
			document.getElementById('proDocsSpanStatus'+taskId).value = '0';
			document.getElementById('proDocsDownarrowSpan'+taskId).style.display = 'block';
			document.getElementById('proDocsUparrowSpan'+taskId).style.display = 'none';
			
			if(document.getElementById('taskActHistoryStatus'+taskId)) {
				document.getElementById('taskActHistoryStatus'+taskId).value = '0';
				document.getElementById('taskActHistDownarrowSpan'+taskId).style.display = 'block';
				document.getElementById('taskActHistUparrowSpan'+taskId).style.display = 'none';
			}
			document.getElementById('taskActHistory_'+taskId).style.display = 'none';
			document.getElementById('proDocsDiv_'+taskId).style.display = 'none';
			document.getElementById('newTaskDiv').style.display = 'none';
			document.getElementById('newSubTaskDiv').style.display = 'none';
		}
		document.getElementById('newTRactions'+taskId).selectedIndex = '0';
	}


	function updateStatus(taskId, proId, divId) {
		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$("#modalInfo").show();
		$(".modal-title").html('Update Task Status');
		$.ajax({
			url : 'UpdateTaskPercentage.action?taskId='+taskId+'&proId='+proId+'&divId='+divId,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}


	function saveStatus(taskId, percent, divId, proId, complete) {
		
		if(complete == 'complete') {
			$("#mainDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$.ajax({
				type : 'POST',
				url: 'UpdateTaskPercentage.action?ID='+taskId+'&percent='+percent+'&complete='+complete,
				success: function(result){
					$("#mainDivResult").html(result);
				},
				error: function(result){
					$.ajax({
						url: 'MyTasks.action?proType=C',
						cache: true,
						success: function(result){
							$("#mainDivResult").html(result);
				   		}
					});
				}
			});
			
			/* var action = 'UpdateTaskPercentage.action?ID='+taskId+'&percent='+percent+'&complete='+complete;
			window.location = action; */
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
	                url : 'UpdateTaskPercentage.action?ID='+taskId+'&percent='+percent+'&complete='+complete,
	                cache : false,
	                success : function(data) {
	                	var allData = data.split("::::");
	                	document.getElementById(divId+'Percent_'+proId+'_'+taskId).innerHTML = allData[0] +'%';
	                	document.getElementById(divId+'_'+proId+'_'+taskId).innerHTML = allData[1];
	                }
	            });
		    }
		    $("#modalInfo").hide();
			//$(dialogEdit).dialog('close');
		}
	}


	function viewDocuments(taskId, proId, proType) {
		//alert("taskId ===>> " + taskId);    
		var status = document.getElementById('proDocsSpanStatus'+taskId).value;
		//alert("status ===>> " + status);
		if(status == '0') {
			document.getElementById('proDocsSpanStatus'+taskId).value = '1';
			document.getElementById('proDocsDownarrowSpan'+taskId).style.display = 'none';
			document.getElementById('proDocsUparrowSpan'+taskId).style.display = 'block';
			//alert("status ===>> 1");
			if(document.getElementById('taskActHistoryStatus'+taskId)) {
				document.getElementById('taskActHistoryStatus'+taskId).value = '0';
				document.getElementById('taskActHistDownarrowSpan'+taskId).style.display = 'block';
				document.getElementById('taskActHistUparrowSpan'+taskId).style.display = 'none';
			}
			//alert("status ===>> 2");
			document.getElementById('editTaskSubtaskDiv_'+taskId).style.display = 'none';
			document.getElementById('taskActHistory_'+taskId).style.display = 'none';
			document.getElementById('reassignTaskSubTaskDiv_'+taskId).style.display = 'none';
			document.getElementById('rescheduleTaskSubTaskDiv_'+taskId).style.display = 'none';
			document.getElementById('newTaskDiv').style.display = 'none';
			document.getElementById('newSubTaskDiv').style.display = 'none';
			//alert("status ===>> 3");
			document.getElementById("taskAllEventDiv").style.display = 'block';
			document.getElementById('proDocsDiv_'+taskId).style.display = 'block';
	    	$('<img src=\"images1/ajax-loading-1.gif\"/>').appendTo("#proDocsDiv_"+taskId);
	    	//alert("status ===>> 4");
			getContent('proDocsDiv_'+taskId, 'ProjectDocuments.action?proId='+ proId +'&taskId='+ taskId +'&proType='+ proType +'&fromPage=MyProject');
			//alert("status ===>> 5");
		} else {
			document.getElementById('proDocsSpanStatus'+taskId).value = '0';
			document.getElementById('proDocsDownarrowSpan'+taskId).style.display = 'block';
			document.getElementById('proDocsUparrowSpan'+taskId).style.display = 'none';
			
			document.getElementById('proDocsDiv_'+taskId).innerHTML = '';
			document.getElementById('proDocsDiv_'+taskId).style.display = 'none';
			document.getElementById("taskAllEventDiv").style.display = 'none';
		}		
	}
	
	
	function repeatTask(tCnt, strTaskId, strProId, t_st_Type) {
		//alert("tCnt ===>> " + tCnt);
		var strTaskDepend = document.getElementById("strProTaskDependency_"+strTaskId).value;
		//var strEmpSkills = document.getElementById("strProEmpSkills_"+strTaskId).value;
		//var strTeamEmp = document.getElementById("strProTeamEmp_"+strTaskId).value;
		
		var taskCnt = document.getElementById("taskcount_"+strTaskId).value;
		var cnt=(parseInt(taskCnt)+1);
	    //alert("taskCnt ===>> " + taskCnt);
	    var table = document.getElementById("taskTable_"+strTaskId);
	    var rowCount = table.rows.length;
	    var row = table.insertRow(rowCount);
	    
	    //alert("cnt ===>> " + cnt);
	    row.id="task_TR"+strTaskId+"_"+cnt;
	    /* var cell0 = row.insertCell(0);
		cell0.innerHTML = "&nbsp;"; */
		
		var tstType = "Task";
		if(t_st_Type == 'ST') {
			tstType = "Sub-task";
		}
		
	    var cell1 = row.insertCell(0);
		cell1.setAttribute("valign","top");
	    //cell1.setAttribute('style', 'border-bottom: 1px solid #B6B6B6' );
	    cell1.innerHTML = "<input type=\"hidden\" name=\"taskTRId"+strTaskId+"\" id=\"taskTRId"+strTaskId+"_"+cnt+"\" value=\""+cnt+"\">"+
	    "<input type=\"hidden\" name=\"taskDescription"+strTaskId+"\" id=\"taskDescription"+strTaskId+"_"+cnt+"\">"+
	    "<input type=\"text\" name=\"taskname"+strTaskId+"\" id=\"taskname"+strTaskId+"_"+cnt+"\" class=\"validateRequired\" style=\"width:160px !important; font-size:12px; height: 16px;\" onchange=\"saveTaskAndGetTaskId('"+cnt+"', '"+strProId+"' , '"+strTaskId+"', '"+t_st_Type+"')\">"+
	    "<span id=\"addTaskSpan"+strTaskId+"_"+cnt+"\"><input type=\"hidden\" name=\"taskID"+strTaskId+"\" id=\"taskID"+strTaskId+"_"+cnt+"\" value=\"\"></span>"+
	    "<div><a href=\"javascript:void(0)\" onclick=\"updateTaskDescription('"+strTaskId+"', '"+cnt+"', 'taskDescription', 'T');\">D</a>"+
	    "&nbsp;<input type=\"checkbox\" name=\"recurringTask"+strTaskId+"\" id=\"recurringTask"+strTaskId+"_"+cnt+"\" onclick=\"setValue('isRecurringTask"+strTaskId+"_"+cnt+"');\" title=\"Add task to recurring in next frequency\"/>Recurr "+tstType+
	    "<input type=\"hidden\" name=\"isRecurringTask"+strTaskId+"\" id=\"isRecurringTask"+strTaskId+"_"+cnt+"\" value=\"0\"/></div>";
	    
	    var cell2 = row.insertCell(1);
	    cell2.setAttribute("valign","top");
		cell2.innerHTML = "<span id=\"dependencySpan"+strTaskId+"_"+cnt+"\"> <select name=\"dependency"+strTaskId+"\" id=\"dependency"+strTaskId+"_"+cnt+"\" style=\"width:135px !important; font-size:12px;\" ><option value=\"\">Select Dependency</option>"+strTaskDepend+"</select></span>"
		    +"<select name=\"dependencyType"+strTaskId+"\" id=\"dependencyType"+strTaskId+"_"+cnt+"\" style=\"width:135px !important; font-size:12px; margin-left:10px;\" onchange=\"setDependencyPeriod(this.value, '"+strTaskId+"', '"+cnt+"', 'Task');\"><option value=\"\">Select Dependency Type</option>"
		    +"<option value=\"0\">Start-Start</option><option value=\"1\">Finish-Start</option></select>";
		
	    var cell3 = row.insertCell(2);
		cell3.setAttribute("valign","top");
		cell3.innerHTML = "<select name=\"priority"+strTaskId+"\" id=\"priority"+strTaskId+"_"+cnt+"\" style=\"width:85px !important;\"><option value=\"0\">Low</option>"
		    +"<option value=\"1\">Medium</option><option value=\"2\">High</option></select>";
		
		var cell5 = row.insertCell(3);
		cell5.setAttribute("valign","top");
		cell5.innerHTML = "<span id=\"empSpan"+strTaskId+"_"+cnt+"\">Myself</span>";
		
		var cell6 = row.insertCell(4);
		cell6.setAttribute("valign","top");
		cell6.innerHTML = "<input type=\"text\" id=\"startDate"+strTaskId+"_"+cnt+"\" name=\"startDate"+strTaskId+"\" class=\"validateRequired\" style=\"width: 85px !important; font-size:12px; height: 16px;\">";

		var cell7 = row.insertCell(5);
		cell7.setAttribute("valign","top");
		cell7.innerHTML = "<input type=\"text\" id=\"deadline1"+strTaskId+"_"+cnt+"\" class=\"validateRequired\" name=\"deadline1"+strTaskId+"\" style=\"width: 85px !important; font-size:12px; height: 16px;\">";
		
		var cell8 = row.insertCell(6);
		cell8.setAttribute("valign","top");
		cell8.innerHTML = "<input type=\"text\" id=\"idealTime"+strTaskId+"_"+cnt+"\" name=\"idealTime"+strTaskId+"\" onkeypress=\"return isNumberKey(event)\" class=\"validateRequired\" style=\"width:30px !important; font-size:12px; height: 16px; text-align:right;\">";
		
		var cell10 = row.insertCell(7);
		cell10.setAttribute("valign","top");
		cell10.innerHTML = "<input type=\"text\" name=\"colourCode"+strTaskId+"\" id=\"colourCode"+strTaskId+"_"+cnt+"\" class=\"validateRequired\" style=\"width:7px !important; font-size:12px; height: 16px;\"/>"+
			"<img align=\"left\" style=\"cursor: pointer;position:absolute; padding:5px 0 0 5px\" src=\"images1/color_palate.png\""+ 
			"id=\"pick1\" onclick=\"cp2.select(document.getElementById('frm_ViewAllProjects_"+strTaskId+"').colourCode"+strTaskId+"_"+cnt+",'pick1'); return false;\"/>"+
			"<span class=\"hint ml_25\">Choose a colour for this subtask. This colour will be marked in project grant chart."+
			"<span class=\"hint-pointer\">&nbsp;</span></span>";
				
		var cell11 = row.insertCell(8);
		cell11.setAttribute("nowrap","nowrap");
		cell11.setAttribute("valign","top");
		cell11.innerHTML = "<select name=\"taskActions"+strTaskId+"_"+cnt+"\" id=\"taskActions"+strTaskId+"_"+cnt+"\" style=\"width: 100px !important;\" onchange=\"executeTaskActions(this.value, this.parentNode.parentNode.rowIndex, '"+cnt+"', '"+strProId+"', '', '"+strTaskId+"', '"+t_st_Type+"');\">"+
		"<option value=\"\">Actions</option><option value=\"1\">Delete</option><option value=\"3\">Repeat "+tstType+"</option>"+
		"</select>";
       
	    document.getElementById("taskcount_"+strTaskId).value = cnt;
	    
	    //alert("taskname 5 === " + document.getElementById('taskname'+tCnt).value);
	    document.getElementById('taskname'+strTaskId+'_'+cnt).value = document.getElementById('taskname'+strTaskId+'_'+tCnt).value;
	    document.getElementById('taskDescription'+strTaskId+'_'+cnt).value = document.getElementById('taskDescription'+strTaskId+'_'+tCnt).value;
	    
	    getTasksForDependency(cnt, strTaskId, strProId, t_st_Type);
	    document.getElementById('dependency'+strTaskId+'_'+cnt).selectedIndex = document.getElementById('dependency'+strTaskId+'_'+tCnt).selectedIndex;
	    document.getElementById('dependencyType'+strTaskId+'_'+cnt).selectedIndex = document.getElementById('dependencyType'+strTaskId+'_'+tCnt).selectedIndex;
	    document.getElementById('priority'+strTaskId+'_'+cnt).selectedIndex = document.getElementById('priority'+strTaskId+'_'+tCnt).selectedIndex;
	    
    	document.getElementById('startDate'+strTaskId+'_'+cnt).value = document.getElementById('startDate'+strTaskId+'_'+tCnt).value;
    	document.getElementById('deadline1'+strTaskId+'_'+cnt).value = document.getElementById('deadline1'+strTaskId+'_'+tCnt).value;
    	
    	document.getElementById('idealTime'+strTaskId+'_'+cnt).value = document.getElementById('idealTime'+strTaskId+'_'+tCnt).value;
    	
    	document.getElementById('colourCode'+strTaskId+'_'+cnt).value = document.getElementById('colourCode'+strTaskId+'_'+tCnt).value;
    	document.getElementById('colourCode'+strTaskId+'_'+cnt).style.backgroundColor = document.getElementById('colourCode'+strTaskId+'_'+tCnt).value;

	    setDate(cnt, strTaskId);
	}

		
	function addNewTask(t_st_Type) { //  
		var taskCnt = document.getElementById("taskcount").value;
		var strMyProjects = document.getElementById("strMyProjects").value;
			var cnt=(parseInt(taskCnt)+1);
		    var table = document.getElementById("taskTable");
		    var rowCount = table.rows.length;
		    if(parseFloat(rowCount)==1) {
		    	deleteAllSubTasksCloseDiv();
		    	if(document.getElementById("taskAllEventDiv")) {
		    		document.getElementById("taskAllEventDiv").style.display = 'block';
		    	}
				document.getElementById("newTaskDiv").style.display = 'block';
			}
		    var row = table.insertRow(rowCount);
		    var myColor = '#'+(Math.random()*0xFFFFFF<<0).toString(16);
		    row.id="task_TR"+cnt;
		    var tstType = "Task";
			if(t_st_Type == 'ST') {
				tstType = "Sub-task";
			}
			//alert("taskCnt 5 ===>> " + taskCnt);
		    var cell1 = row.insertCell(0);
			cell1.setAttribute("valign","top");
		    cell1.innerHTML = "<input type=\"hidden\" name=\"taskTRId\" id=\"taskTRId"+cnt+"\" value=\""+cnt+"\">"+
		    "<input type=\"hidden\" name=\"taskDescription\" id=\"taskDescription"+cnt+"\">"+
		    "<span name=\"spantaskname\" id=\"spantaskname"+cnt+"\" style=\"display: none; padding: 2px 5px; cursor: pointer;\" ondblclick=\"updateFields('spantaskname"+cnt+"', 'taskname"+cnt+"');\" onmouseover=\"addBgColor(this)\" onmouseout=\"removeBgColor(this)\" ></span>"+
		    "<input type=\"text\" name=\"taskname\" id=\"taskname"+cnt+"\" class=\"validateRequired\" style=\"width:200px !important;\" onchange=\"saveTaskAndGetTaskId('"+cnt+"', '"+t_st_Type+"', 'onchange')\" onmouseout=\"saveTaskAndGetTaskId('"+cnt+"', '"+t_st_Type+"', 'onmouseout')\" required>"+ //onblur=\"saveTaskAndGetTaskId('"+cnt+"', '"+t_st_Type+"')\" 
		    "<span id=\"myprojectSpan"+cnt+"\">"
		    +"<span name=\"spanstrProjects\" id=\"spanstrProjects"+cnt+"\" style=\"display: none; padding: 2px 5px; cursor: pointer;\" ondblclick=\"updateFields('spanstrProjects"+cnt+"', 'strProjects"+cnt+"');\" onmouseover=\"addBgColor(this)\" onmouseout=\"removeBgColor(this)\" ></span>"
		    +"<select name=\"strProjects\" id=\"strProjects"+cnt+"\" style=\"width:175px !important; margin: 7px 0px 0px 10px;\" onchange=\"getTasksForDependency(this.value, '"+cnt+"', '"+t_st_Type+"');\" required>"
		    +"<option value=\"\">Select Project</option>"+strMyProjects+"</select></span>"+ // onmouseout=\"setProjectName('"+cnt+"', '"+t_st_Type+"');\" 
		    "<span id=\"addTaskSpan"+cnt+"\"><input type=\"hidden\" name=\"taskID\" id=\"taskID"+cnt+"\" value=\"\"></span>"+
		    "<div><a href=\"javascript:void(0)\" onclick=\"updateTaskDescription('"+cnt+"', 'taskDescription', '"+t_st_Type+"');\">D</a>"+
		    "&nbsp;<span id=\"recurrSpan"+cnt+"\" style=\"margin-left:5px; display:none\"><input type=\"checkbox\" name=\"recurringTask\" id=\"recurringTask"+cnt+"\" onclick=\"setValue('isRecurringTask"+cnt+"');\" title=\"Add task to recurring in next frequency\"/>Recurr "+tstType+"</span>"+
		    "<input type=\"hidden\" name=\"isRecurringTask\" id=\"isRecurringTask"+cnt+"\" value=\"0\"/></div>";
		    
		    var cell2 = row.insertCell(1);
		    cell2.setAttribute("valign","top");
		    cell2.innerHTML = "<span id=\"dependencySpan"+cnt+"\">"
		    	+"<span name=\"spandependency\" id=\"spandependency"+cnt+"\" style=\"display: none; padding: 2px 5px; cursor: pointer;\" ondblclick=\"updateFields('spandependency"+cnt+"', 'dependency"+cnt+"');\" onmouseover=\"addBgColor(this)\" onmouseout=\"removeBgColor(this)\" ></span>"
		    	+"<select name=\"dependency\" id=\"dependency"+cnt+"\" style=\"width:135px !important;\" onchange=\"setDependencyTaskName('"+cnt+"', '"+t_st_Type+"')\"><option value=\"\">Select Dependency</option></select></span>"
				+"<span name=\"spandependencyType\" id=\"spandependencyType"+cnt+"\" style=\"display: none; padding: 2px 5px; cursor: pointer;\" ondblclick=\"updateFields('spandependencyType"+cnt+"', 'dependencyType"+cnt+"');\" onmouseover=\"addBgColor(this)\" onmouseout=\"removeBgColor(this)\" ></span>"    
				+"<select name=\"dependencyType\" id=\"dependencyType"+cnt+"\" style=\"width:135px !important; margin-top: 7px;\" onchange=\"setDependencyType('"+cnt+"', '"+t_st_Type+"')\" ><option value=\"\">Select Dependency Type</option>"
				+"<option value=\"0\">Start-Start</option><option value=\"1\">Finish-Start</option></select>"; //onchange=\"setDependencyPeriod(this.value, '"+cnt+"', 'Task');\" onmouseout=\"setDependencyType('"+cnt+"', '"+t_st_Type+"')\" 
			
			var cell3 = row.insertCell(2);
			cell3.setAttribute("valign","top");
			cell3.innerHTML = "<span name=\"spanpriority\" id=\"spanpriority"+cnt+"\" style=\"display: none; padding: 2px 5px; cursor: pointer;\" ondblclick=\"updateFields('spanpriority"+cnt+"', 'priority"+cnt+"');\" onmouseover=\"addBgColor(this)\" onmouseout=\"removeBgColor(this)\" ></span>"
				+"<select name=\"priority\" id=\"priority"+cnt+"\" style=\"width:85px !important;\" onchange=\"setPriority('"+cnt+"', '"+t_st_Type+"')\" ><option value=\"0\">Low</option>"
				+"<option value=\"1\">Medium</option><option value=\"2\">High</option></select>"; //onmouseout=\"setPriority('"+cnt+"', '"+t_st_Type+"')\"
			
			var cell6 = row.insertCell(3);
			cell6.setAttribute("valign","top");
			cell6.innerHTML = "<span name=\"spanstartDate\" id=\"spanstartDate"+cnt+"\" style=\"display: none; padding: 2px 5px; cursor: pointer;\" ondblclick=\"updateFields('spanstartDate"+cnt+"', 'startDate"+cnt+"');\" onmouseover=\"addBgColor(this)\" onmouseout=\"removeBgColor(this)\" ></span>"
				+"<input type=\"text\" id=\"startDate"+cnt+"\" name=\"startDate\" class=\"validateRequired\" style=\"width:85px !important;\" onchange=\"setStartDate('"+cnt+"', '"+t_st_Type+"')\" onmouseout=\"setStartDate('"+cnt+"', '"+t_st_Type+"')\">";

			var cell7 = row.insertCell(4);
			cell7.setAttribute("valign","top");
			cell7.innerHTML = "<span name=\"spandeadline1\" id=\"spandeadline1"+cnt+"\" style=\"display: none; padding: 2px 5px; cursor: pointer;\" ondblclick=\"updateFields('spandeadline1"+cnt+"', 'deadline1"+cnt+"');\" onmouseover=\"addBgColor(this)\" onmouseout=\"removeBgColor(this)\" ></span>"
				+"<input type=\"text\" id=\"deadline1"+cnt+"\" class=\"validateRequired\" name=\"deadline1\" style=\"width:85px !important;\" onchange=\"setDeadline('"+cnt+"', '"+t_st_Type+"')\" onmouseout=\"setDeadline('"+cnt+"', '"+t_st_Type+"')\">";
			
			var cell8 = row.insertCell(5);
			cell8.setAttribute("valign","top");
			cell8.innerHTML = "<span name=\"spanidealTime\" id=\"spanidealTime"+cnt+"\" style=\"display: none; padding: 2px 5px; cursor: pointer;\" ondblclick=\"updateFields('spanidealTime"+cnt+"', 'idealTime"+cnt+"');\" onmouseover=\"addBgColor(this)\" onmouseout=\"removeBgColor(this)\" ></span>"
				+"<input type=\"text\" min=\"0\" id=\"idealTime"+cnt+"\" name=\"idealTime\" onkeypress=\"return isNumberKey(event)\" onblur=\"setIdealTime('"+cnt+"', '"+t_st_Type+"')\" onchange=\"setIdealTime('"+cnt+"', '"+t_st_Type+"')\" onmouseout=\"setIdealTime('"+cnt+"', '"+t_st_Type+"')\" class=\"validateRequired\" style=\"width:45px !important; text-align:right;\">"+
				"<input type=\"hidden\" name=\"colourCode\" id=\"colourCode"+cnt+"\" value=\""+myColor+"\" />";
			
			var cell11 = row.insertCell(6);
			cell11.setAttribute("nowrap","nowrap");
			cell11.setAttribute("valign","top");
/* 				cell11.innerHTML = "<img border=\"0\" onclick=\"executeTaskActions('1', '"+cnt+"', '"+t_st_Type+"');\" src=\"images1/icons/icons/close_button_icon.png\" style=\"padding: 5px 5px 0pt;\">"; */
			cell11.innerHTML = "<a href=\"javascript:void(0)\" onclick=\"executeTaskActions('1', '"+cnt+"', '"+t_st_Type+"');\"> <i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"></i></a>";
			
		    document.getElementById("taskcount").value = cnt;
		    setDate(cnt);
		    $("input[name='startDate']").on('keydown', function() {
			    return false;
			});
			
			$("input[name='deadline1']").on('keydown', function() {
			    return false;
			});
		}
	
		
	function addNewSubTask(t_st_Type) { 
		var taskCnt = document.getElementById("subtaskcount").value;
		var strMyTasks = document.getElementById("strMyTasks").value;
		//alert("taskCnt ===>> " + taskCnt);
			var cnt=(parseInt(taskCnt)+1);

			var table = document.getElementById("subtaskTable");
		    var rowCount = table.rows.length;

		    if(parseFloat(rowCount)==1) {
		    	deleteAllTasksCloseDiv();
		    	if(document.getElementById("taskAllEventDiv")) {
		    		document.getElementById("taskAllEventDiv").style.display = 'block';
		    	}
				document.getElementById("newSubTaskDiv").style.display = 'block';
			}
		    
		    var row = table.insertRow(rowCount);
		    var myColor = '#'+(Math.random()*0xFFFFFF<<0).toString(16);
		    
		    row.id="subtask_TR"+cnt;
		    
		    var cell1 = row.insertCell(0);
		    cell1.setAttribute("valign","top");
		    cell1.innerHTML = "<input type=\"hidden\" name=\"subTaskTRId\" id=\"subTaskTRId"+cnt+"\" value=\""+cnt+"\">"+
		    "<input type=\"hidden\" name=\"subTaskDescription\" id=\"subTaskDescription"+cnt+"\">"+
		    "<input type=\"text\" name=\"subtaskname\" id=\"subtaskname"+cnt+"\" class=\"validateRequired\" style=\"width:200px !important;\" onchange=\"saveTaskAndGetTaskId('"+cnt+"', '"+t_st_Type+"', '')\" required>"+
		    "<span id=\"myTaskSpan"+cnt+"\"><select name=\"strTasks\" id=\"strTasks"+cnt+"\" style=\"width:175px !important; margin: 7px 0px 0px 10px;\" onchange=\"getTasksForDependency(this.value, '"+cnt+"', '"+t_st_Type+"');\" required>"+
		    ""+strMyTasks+"</select></span>"+
		    "<span id=\"addSubTaskSpan"+cnt+"\"><input type=\"hidden\" name=\"subTaskID\" id=\"subTaskID"+cnt+"\" value=\"\"></span>"+
		    "<div><a href=\"javascript:void(0)\" onclick=\"updateTaskDescription('"+cnt+"', 'subTaskDescription', '"+t_st_Type+"');\">D</a>"+
		    "&nbsp;<span id=\"recurrSubSpan"+cnt+"\" style=\"margin-left:5px; display:none\"><input type=\"checkbox\" name=\"recurringSubTask\" id=\"recurringSubTask"+cnt+"\" onclick=\"setValue('isRecurringSubTask"+cnt+"');\" title=\"Add sub task to recurring in next frequency\"/>Recurr Sub-task</span>"+
		    "<input type=\"hidden\" name=\"isRecurringSubTask\" id=\"isRecurringSubTask"+cnt+"\" value=\"0\"/></div>";
		    
		    var cell2 = row.insertCell(1);
		    cell2.setAttribute("valign","top");
		    cell2.innerHTML = "<span id=\"subDependencySpan"+cnt+"\"><select name=\"subDependency\" id=\"subDependency"+cnt+"\" style=\"width:135px !important;\"><option value=\"\">Select Dependency</option></select></span><br/>"+
			    "<select name=\"subDependencyType\" id=\"subDependencyType"+cnt+"\" style=\"width:135px !important; margin-top: 7px;\" onchange=\"setDependencyPeriod(this.value, '"+cnt+"', 'SubTask');\"><option value=\"\">Select Dependency Type</option>"
			    +"<option value=\"0\">Start-Start</option><option value=\"1\">Finish-Start</option></select>";
			
		    var cell3 = row.insertCell(2);
		    cell3.setAttribute("valign","top");
			cell3.innerHTML = "<select name=\"subpriority\" id=\"subpriority"+cnt+"\" style=\"width:85px !important;\"><option value=\"0\">Low</option>"
		    +"<option value=\"1\">Medium</option><option value=\"2\">High</option></select>";
			
			var cell6 = row.insertCell(3);
			cell6.setAttribute("valign","top");
			cell6.innerHTML = "<input type=\"text\" name=\"substartDate\" id=\"substartDate"+cnt+"\" class=\"validateRequired\" style=\"width:85px !important;\">";

			var cell7 = row.insertCell(4);
			cell7.setAttribute("valign","top");
			cell7.innerHTML = "<input type=\"text\" name=\"subdeadline1\" id=\"subdeadline1"+cnt+"\" class=\"validateRequired\" style=\"width:85px !important;\">";
			
			var cell8 = row.insertCell(5);
			cell8.setAttribute("valign","top");
			cell8.innerHTML = "<input type=\"number\" min=\"0\" name=\"subidealTime\" id=\"subidealTime"+cnt+"\" onkeypress=\"return isNumberKey(event)\" class=\"validateRequired\" style=\"width:45px !important; text-align:right;\">"+
				"<input type=\"hidden\" name=\"subcolourCode\" id=\"subcolourCode"+cnt+"\" value=\""+myColor+"\" />";
			
			var cell11 = row.insertCell(6);
			cell11.setAttribute("nowrap","nowrap");
			cell11.setAttribute("valign","top");
			/* cell11.innerHTML = "<img border=\"0\" onclick=\"executeSubTaskActions('1', '"+cnt+"', '"+t_st_Type+"');\" src=\"images1/icons/icons/close_button_icon.png\" style=\"padding: 5px 5px 0pt;\">"; */
			cell11.innerHTML = "<i class=\"fa fa-times-circle cross\" aria-hidden=\"true\" style=\"padding: 5px 5px 0pt;\"></i>";
			
			document.getElementById("subtaskcount").value = cnt;
		    setSubDate(cnt);
		    $("input[name='substartDate']").on('keydown', function() {
			    return false;
			});
			
			$("input[name='subdeadline1']").on('keydown', function() {
			    return false;
			});
		}
		
		
	function setValue(strName) {
		var val = document.getElementById(strName).value;
		if(val == '0') {
			document.getElementById(strName).value = '1';
		} else {
			document.getElementById(strName).value = '0';
		}
	}
		

	
		
	function setSubDate(id) {
		var proStDt = '';
		var proEndDt = '';
		$("#subdeadline1"+id).datepicker({
			format : 'dd/mm/yyyy', minDate: proStDt, maxDate: proEndDt, 
			onClose: function(selectedDate){
				$("#substartDate"+id).datepicker("option", "maxDate", selectedDate);
			}
		});	
		$("#substartDate"+id).datepicker({
			format : 'dd/mm/yyyy', minDate: proStDt, maxDate: proEndDt, 
			onClose: function(selectedDate){
				$("#subdeadline1"+id).datepicker("option", "minDate", selectedDate);
			}
		});	
	}
		
		
	function setDependencyTaskName(cnt, t_st_Type) {
		var dependency = document.getElementById("dependency"+cnt).value;
		document.getElementById("dependency"+cnt).style.display = 'none';
		document.getElementById("spandependency"+cnt).style.display = 'block';
		var strdependency = 'Not Aligned';
		if(document.getElementById(dependency)) {
			strdependency = document.getElementById(dependency).value;
		}
		var taskID = document.getElementById("taskID"+cnt).value;
		getContent("", 'SaveTaskOrSubtaskAndGetId.action?operation=OTHER_FIELDS&fieldType=DEPENDTASK&fieldValue='+dependency+'&taskId='+taskID+'&type=MP_Task');
		document.getElementById("spandependency"+cnt).innerHTML = strdependency;
	}
	
	function setDependencyType(cnt, t_st_Type) {
		var dependencyType = document.getElementById("dependencyType"+cnt).value;
		document.getElementById("dependencyType"+cnt).style.display = 'none';
		document.getElementById("spandependencyType"+cnt).style.display = 'block';
		var strdependencyType = 'No Dependency';
		if(dependencyType=='0') {
			strdependencyType = 'Start-Start';
		} else if(dependencyType=='1') {
			strdependencyType = 'Finish-Start';
		}
		var taskID = document.getElementById("taskID"+cnt).value;
		getContent("", 'SaveTaskOrSubtaskAndGetId.action?operation=OTHER_FIELDS&fieldType=DEPENDTYPE&fieldValue='+dependencyType+'&taskId='+taskID+'&type=MP_Task');
		document.getElementById("spandependencyType"+cnt).innerHTML = strdependencyType;
	}
	
	function addBgColor(x) {
		x.style.backgroundColor = "lightgray";
	}

	function removeBgColor(x) {
		x.style.backgroundColor = "";
	}
	
	function getTasksForDependency(strProId, cnt, t_st_Type) {
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
	    	if(t_st_Type == 'T') {
	    		var xhr = $.ajax({
	                url : 'SaveTaskOrSubtaskAndGetId.action?proId='+strProId+"&count="+cnt+'&type=MP_GetTasks',
	                cache : false,
	                success : function(data) {
	                	var allData = data.split("::::");
	                	//alert("allData ==>> " + allData);
	                	
	                	document.getElementById('dependencySpan'+cnt).innerHTML = allData[0];
	                	//alert("parseFloat(allData[1].trim()) ==>> " + parseFloat(allData[1].trim()));
						if(parseFloat(allData[1].trim()) == 1) {
							//alert("allData[1] ==>> " + allData[1]);
							//document.getElementById('recurrSpan'+cnt).style.display = "inline";
	                	} else {
	                		//document.getElementById('recurrSpan'+cnt).style.display = "none";
	                	}
	                }
	            });
	    	} else {
	    		var xhr = $.ajax({
	                url : 'SaveTaskOrSubtaskAndGetId.action?count='+cnt+'&taskId='+strProId+'&type=MP_GetSubTasks',
	                cache : false,
	                success : function(data) {
	                	var allData = data.split("::::");
	                	document.getElementById('subDependencySpan'+cnt).innerHTML = allData[0];
	                	if(parseFloat(allData[1].trim()) == 1) {
	                		//document.getElementById('recurrSubSpan'+cnt).style.display = "inline";
	                	} else {
	                		//document.getElementById('recurrSubSpan'+cnt).style.display = "none";
	                	}
	                	
	                }
	            });
	    	}
	    	setProjectName(cnt, t_st_Type);
	    }
		//$(dialogEdit).dialog('close');
	}
		
		
	function executeTaskActions(val, cnt, t_st_Type) {
		if(val == '1') {
			deleteTask(cnt, t_st_Type);
		} else if(val == '3') {
			var tstType = "task";
			if(t_st_Type == 'ST') {
				tstType = "sub-task";
			}
			if(confirm('Are you sure, you wish to repeat this '+tstType+'?')) {
				repeatTask(cnt, t_st_Type);
			}
		}
		document.getElementById("taskActions"+cnt).selectedIndex = '0';
	}
	
	
	function executeSubTaskActions(val, cnt, t_st_Type) {
		if(val == '1') {
			deleteTask(cnt, t_st_Type);
		} else if(val == '3') {
			var tstType = "task";
			if(t_st_Type == 'ST') {
				tstType = "sub-task";
			}
			if(confirm('Are you sure, you wish to repeat this '+tstType+'?')) {
				repeatSubTask(cnt, t_st_Type);
			}
		} 
		document.getElementById("subtaskActions"+cnt).selectedIndex = '0';
	}
		
		
	function deleteTask(count, t_st_Type) {
		//alert("count ==========>> " + count);
		var tstType = "task";
		if(t_st_Type == 'ST') {
			tstType = "sub-task";
			if(confirm('Are you sure, you want to cancel this '+tstType+'?')) {
				deleteTaskFromDB(count, t_st_Type);
				var trIndex = document.getElementById("subtask_TR"+count).rowIndex;
			    document.getElementById("subtaskTable").deleteRow(trIndex);
			}
			 var table = document.getElementById("subtaskTable");
			 var rowCount = table.rows.length;
			 if(parseFloat(rowCount)==1) {
				 document.getElementById("newSubTaskDiv").style.display = 'none';
				 if(document.getElementById("taskAllEventDiv")) {
		    		document.getElementById("taskAllEventDiv").style.display = 'none';
		    	}
			 }
		} else {
			if(confirm('Are you sure, you want to cancel this '+tstType+'?')) {
				deleteTaskFromDB(count, t_st_Type);
				var trIndex = document.getElementById("task_TR"+count).rowIndex;
			    document.getElementById("taskTable").deleteRow(trIndex);
			}
			 var table = document.getElementById("taskTable");
			 var rowCount = table.rows.length;
			 if(parseFloat(rowCount)==1) {
				 document.getElementById("newTaskDiv").style.display = 'none';
				 if(document.getElementById("taskAllEventDiv")) {
			    		document.getElementById("taskAllEventDiv").style.display = 'none';
			    	}
			 }
		}
	}
	
	
	function deleteAllTasksCloseDiv() {
		var taskcount = document.getElementById("taskcount").value;
		for(var i=1; i<= parseFloat(taskcount); i++) {
			if(document.getElementById("task_TR"+i)) {
				deleteTaskFromDB(i, 'T');
			 	var trIndex = document.getElementById("task_TR"+i).rowIndex;
			    document.getElementById("taskTable").deleteRow(trIndex);
			}
		}
		document.getElementById("newTaskDiv").style.display = 'none';
		if(document.getElementById("taskAllEventDiv")) {
    		document.getElementById("taskAllEventDiv").style.display = 'none';
    	}
	}
	
	
	function deleteAllSubTasksCloseDiv() {
		var taskcount = document.getElementById("subtaskcount").value;
		 for(var i=1; i<= parseFloat(taskcount); i++) {
			 if(document.getElementById("subtask_TR"+i)) {
				 deleteTaskFromDB(i, 'ST');
			 	var trIndex = document.getElementById("subtask_TR"+i).rowIndex;
			    document.getElementById("subtaskTable").deleteRow(trIndex);
			 }
		 }
		 document.getElementById("newSubTaskDiv").style.display = 'none';
		 if(document.getElementById("taskAllEventDiv")) {
    		document.getElementById("taskAllEventDiv").style.display = 'none';
    	}
	}

	
	function hideRescheduleTaskSubTaskDiv(taskId) {
		if(document.getElementById("taskAllEventDiv")) {
    		document.getElementById("taskAllEventDiv").style.display = 'none';
    	}
		document.getElementById('rescheduleTaskSubTaskDiv_'+taskId).style.display = 'none';
	}
	
	function hideReassignTaskSubTaskDiv(taskId) {
		if(document.getElementById("taskAllEventDiv")) {
    		document.getElementById("taskAllEventDiv").style.display = 'none';
    	}
		document.getElementById('reassignTaskSubTaskDiv_'+taskId).style.display = 'none';
	}
		
		
	function hideUpdateTaskSubTaskDiv(taskId) {
		document.getElementById('editTaskSubtaskDiv_'+taskId).style.display = 'none';
		document.getElementById('action'+taskId).selectedIndex = '0';
		if(document.getElementById("taskAllEventDiv")) {
    		document.getElementById("taskAllEventDiv").style.display = 'none';
    	}
	}
	
	

	/* ************************************************************ Start Add Document Script ********************************************** */

		function openCloseDocs(strProTaskId, folderCnt,subFolderSize,docFolderSize) {
			if(document.getElementById("hideFolder_"+strProTaskId+"_"+folderCnt)) {
				var status = document.getElementById("hideFolder_"+strProTaskId+"_"+folderCnt).value;
				if(status == '0') {
					for(var i=0; i< parseInt(subFolderSize);i++){
						document.getElementById("folderTR_"+strProTaskId+"_"+folderCnt+"_"+i).style.display = "block";
					}
					for(var i=0; i< parseInt(docFolderSize);i++){
						document.getElementById("docFolderTR_"+strProTaskId+"_"+folderCnt+"_"+i).style.display = "block";
					}
					
					document.getElementById("hideFolder_"+strProTaskId+"_"+folderCnt).value = '1';
					if(document.getElementById("FDDownarrowSpan_"+strProTaskId+"_"+folderCnt)) {
						document.getElementById("FDDownarrowSpan_"+strProTaskId+"_"+folderCnt).style.display = 'none';
						document.getElementById("FDUparrowSpan_"+strProTaskId+"_"+folderCnt).style.display = 'block';
					}
				} else {
					for(var i=0; i< parseInt(subFolderSize);i++){
						document.getElementById("folderTR_"+strProTaskId+"_"+folderCnt+"_"+i).style.display = "none";
					}
					for(var i=0; i< parseInt(docFolderSize);i++){
						document.getElementById("docFolderTR_"+strProTaskId+"_"+folderCnt+"_"+i).style.display = "none";
					}
					
					document.getElementById("hideFolder_"+strProTaskId+"_"+folderCnt).value = '0';
					if(document.getElementById("FDDownarrowSpan_"+strProTaskId+"_"+folderCnt)) {
						document.getElementById("FDDownarrowSpan_"+strProTaskId+"_"+folderCnt).style.display = 'block';
						document.getElementById("FDUparrowSpan_"+strProTaskId+"_"+folderCnt).style.display = 'none';
					}
				}
			}
		}
		
		
		function openCloseDocs1(strProTaskId, folderCnt, subFolderCnt, subDocSize) {
			if(document.getElementById("hideFolder_"+strProTaskId+"_"+folderCnt+"_"+subFolderCnt)) {
				var status = document.getElementById("hideFolder_"+strProTaskId+"_"+folderCnt+"_"+subFolderCnt).value;
				if(status == '0') {
					for(var i=0; i< parseInt(subDocSize);i++) {
						document.getElementById("folderTR_"+strProTaskId+"_"+folderCnt+"_"+subFolderCnt+"_"+i).style.display = "block";
					}
					
					document.getElementById("hideFolder_"+strProTaskId+"_"+folderCnt+"_"+subFolderCnt).value = '1';
					if(document.getElementById("FDDownarrowSpan_"+strProTaskId+"_"+folderCnt+"_"+subFolderCnt)) {
						document.getElementById("FDDownarrowSpan_"+strProTaskId+"_"+folderCnt+"_"+subFolderCnt).style.display = 'none';
						document.getElementById("FDUparrowSpan_"+strProTaskId+"_"+folderCnt+"_"+subFolderCnt).style.display = 'block';
					}
				} else {
					for(var i=0; i< parseInt(subDocSize);i++) {
						document.getElementById("folderTR_"+strProTaskId+"_"+folderCnt+"_"+subFolderCnt+"_"+i).style.display = "none";
					}
					
					document.getElementById("hideFolder_"+strProTaskId+"_"+folderCnt+"_"+subFolderCnt).value = '0';
					if(document.getElementById("FDDownarrowSpan_"+strProTaskId+"_"+folderCnt+"_"+subFolderCnt)) {
						document.getElementById("FDDownarrowSpan_"+strProTaskId+"_"+folderCnt+"_"+subFolderCnt).style.display = 'block';
						document.getElementById("FDUparrowSpan_"+strProTaskId+"_"+folderCnt+"_"+subFolderCnt).style.display = 'none';
					}
				}
			}
		}


		
		function deleteProjectDocs(type, divName, folderName, proDocID, mainPath) {
			var msg ="Are you sure, you wish to delete this Folder?";
			if(type != 'F' && type != 'SF') {
				msg ="Are you sure, you wish to delete this File?";
			}
			if(confirm(msg)) {
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
		                url : 'DeleteProjectDocuments.action?operation=D&type='+type+'&proDocID='+proDocID +'&mainPath='+encodeURIComponent(mainPath),
		                cache : false,
		                success : function(data) {
							if(data = 'yes') {
								document.getElementById(divName).innerHTML = '';
							}
		                }
		            });
			    }
			}
		}
		
		
		
	function executeFolderActions(taskId, val, clientId, proId, folderName, proFolderId, type, filePath, fileDir, divName, savePath) {
		//alert("strId ===>> " + strId);
			if(val == '1') {
				if(type == 'F' || type == 'SF') {
					editFolder(taskId, clientId, proId, folderName, proFolderId, type);
				} else {
					editDoc(taskId, clientId, proId, folderName, proFolderId, type, filePath, fileDir);
				}
			} else if(val == '2') {
				deleteProjectDocs(type, divName, folderName, proFolderId, savePath);
			}
		}
		

	function editFolder(taskId, clientId, proId, folderName, proFolderId, type) {
		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$("#modalInfo").show();
		$(".modal-title").html('Update '+folderName+' Folder');
		$.ajax({
			url : 'UpdateProjectDocumentFolder.action?clientId='+clientId+'&proId='+proId+'&folderName='+folderName
				+'&proFolderId='+proFolderId+'&taskId='+taskId+'&type='+type+'&fromPage=MP',
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}


	function editDoc(taskId, clientId, proId, docName, proFolderId, type, filePath, fileDir) {
		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$("#modalInfo").show();
		$(".modal-title").html('Update '+docName+' file');
		$.ajax({
			url : 'UpdateProjectDocumentFile.action?clientId='+clientId+'&proId='+proId+'&folderName='+docName+'&proFolderId='+proFolderId
				+'&taskId='+taskId+'&type='+type+'&filePath='+encodeURIComponent(filePath)+'&fileDir='+encodeURIComponent(fileDir)+'&fromPage=MP',
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
		
	}


	function projectDocFact(clientId, proId, docName, proFolderId, type, filePath, fileDir) {
		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$("#modalInfo").show();
		$(".modal-title").html('View '+docName+' file');
		$.ajax({
			url : 'ProjectDocumentFact.action?clientId='+clientId+'&proId='+proId+'&folderName='+docName+'&proFolderId='+proFolderId+'&type='+type
				+'&filePath='+encodeURIComponent(filePath)+'&fileDir='+encodeURIComponent(fileDir),
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
		
	}

	function checkStatus(strCheckBox, hideId) {
		if(strCheckBox.checked == true) {
			document.getElementById(hideId).value = '1';
		} else {
			document.getElementById(hideId).value = '0';
		}
	}

	function clearData(elementId){
		var val = document.getElementById(elementId).value;
		if(val=="Description" || val=="Folder Name" || val=="Document Scope" || val=="Sub Folder Name" ){
			document.getElementById(elementId).value = '';
			document.getElementById(elementId).style.color = '';
		}
	}
	
	
	function fillData(elementId, num) {
		if(document.getElementById(elementId).value=='' && num==1){
			document.getElementById(elementId).value="Description";
			document.getElementById(elementId).style.color = 'gray';
		} else if(document.getElementById(elementId).value=='' && num==2){
			document.getElementById(elementId).value="Folder Name";
			document.getElementById(elementId).style.color = 'gray';
		} else if(document.getElementById(elementId).value=='' && num==3){
			document.getElementById(elementId).value="Document Scope";
			document.getElementById(elementId).style.color = 'gray';
		} else if(document.getElementById(elementId).value=='' && num==4){
			document.getElementById(elementId).value="Sub Folder Name";
			document.getElementById(elementId).style.color = 'gray';
		}
	}

	
	function addNewFolder(strProId, strTaskId, tableName, rowCountName) { 
		var proTasks = document.getElementById("projectTasks"+strProId+"_"+strTaskId).value;
		var proEmployee = document.getElementById("resourceIds"+strProId+"_"+strTaskId).value;
		var proCategory = document.getElementById("projectCategoryType"+strProId+"_"+strTaskId).value;
		var proPoc = document.getElementById("projectPoc"+strProId+"_"+strTaskId).value;
		
		if(document.getElementById("buttonDiv"+strProId+"_"+strTaskId)) {
			document.getElementById("buttonDiv"+strProId+"_"+strTaskId).style.display = 'block';
		}
		
		var fdCnt = document.getElementById(rowCountName).value;
			var cnt=(parseInt(fdCnt)+1);
		    var table = document.getElementById(tableName);
		    var rowCount = table.rows.length;
		    var row = table.insertRow(rowCount);
		    
		    row.id="folderTR"+strProId+"_"+strTaskId+"_"+cnt;
		    var cell0 = row.insertCell(0);
		    cell0.setAttribute('style','width: 550px;');
		    cell0.setAttribute('class','tdBorderLine');
		    cell0.innerHTML = "<span style=\"float:left; margin-right: 9px;\"><input type=\"hidden\" name=\"folderTRId"+strProId+"_"+strTaskId+"\" id=\"folderTRId"+strProId+"_"+strTaskId+"_"+cnt+"\" value=\""+cnt+"\">"+
		    "<input type=\"text\" name=\"strFolderName"+strProId+"_"+strTaskId+"\" id=\"strFolderName"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"width:200px !important; color: gray;\" onclick=\"clearData(this.id);\" onblur=\"fillData(this.id, 2);\" value=\"Folder Name\"/></span>"
	        +"<span style=\"float:left;\"><a href=\"javascript:void(0)\" onclick=\"addNewFolder('"+strProId+"','"+strTaskId+"', '"+tableName+"' ,'"+rowCountName+"')\" class=\"fa fa-fw fa-plus\" title=\"Create New Folder \">&nbsp;</a>"
	        +"<a href=\"javascript:void(0)\" onclick=\"deleteFolder('"+strProId+"','"+strTaskId+"','"+cnt+"', '"+tableName+"')\" id=\""+cnt+"\" class=\"fa fa-fw fa-remove\" title=\"Remove Folder\">&nbsp;</a></span>"
	        +"<span style=\"float:left; width: 100%; margin-top: 5px;\"><textarea rows=\"3\" name=\"strFolderDescription"+strProId+"_"+strTaskId+"\" id=\"strFolderDescription"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"width: 330px !important; color: gray;\" onclick=\"clearData(this.id);\" onblur=\"fillData(this.id, 1);\">Description</textarea></span>"
	        +"<span style=\"float:left; width: 100%;\"><a href=\"javascript:void(0);\" style=\"margin: 0px 0px 0px 50px;\" onclick=\"addNewSubFolder('"+strProId+"','"+strTaskId+"','"+cnt+"', this.parentNode.parentNode.parentNode.rowIndex, '"+tableName+"' ,'"+rowCountName+"');\"> +Add Folder</a>"
	        +"<a href=\"javascript:void(0);\" style=\"margin: 0px 0px 0px 15px;\" onclick=\"addNewFolderDocs('"+strProId+"','"+strTaskId+"','"+cnt+"', this.parentNode.parentNode.parentNode.rowIndex, '"+tableName+"' ,'"+rowCountName+"');\"> +Add Document</a></span>";
	        
	        var cell1 = row.insertCell(1);
		    cell1.setAttribute("valign","top");
		    cell1.setAttribute('class','tdBorderLine');
		    cell1.innerHTML = "<span style=\"float: left;\"><select name=\"proCategoryTypeFolder"+strProId+"_"+strTaskId+"\" id=\"proCategoryTypeFolder"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"width:100px !important;\" onchange=\"changeCategoryType(this.value, 'proCatagorySpan"+strProId+"_"+strTaskId+"_"+cnt+"', 'proTaskSpan"+strProId+"_"+strTaskId+"_"+cnt+"');\">"+
		    "<option value=\"1\" selected>Project</option><option value=\"2\">Category</option></select></span>";
		    
		    var cell2 = row.insertCell(2);
		    cell2.setAttribute("valign","top");
		    cell2.setAttribute('class','tdBorderLine');
		    cell2.innerHTML = "<span id=\"proCatagorySpan"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"float: left; display: none\"><select name=\"proFolderCategory"+strProId+"_"+strTaskId+"_"+cnt+"\" id=\"proFolderCategory"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"width:100px !important;\">"+
		     	""+proCategory+"</select></span><span id=\"proTaskSpan"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"float: left;\"><select name=\"proFolderTasks"+strProId+"_"+strTaskId+"_"+cnt+"\" id=\"proFolderTasks"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"width:100px !important;\">"+
		     	""+proTasks+"</select></span>";
		     	
		    var cell3 = row.insertCell(3);
		    cell3.setAttribute("valign","top");
		    cell3.setAttribute('class','tdBorderLine');
		    cell3.innerHTML = "<div style=\"float: left; width: 100%;\"><span style=\"float: left;\"><select name=\"folderSharingType"+strProId+"_"+strTaskId+"\" id=\"folderSharingType"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"width:100px !important;\" class=\"validateRequired\" onchange=\"showHideResources('"+strProId+"' ,'"+strTaskId+"', this.value, '"+cnt+"')\">"
			    +"<option value=\"0\">Public</option><option value=\"1\">Private Team</option><option value=\"2\">Individual Resource</option></select></span>"
			    +"<span id=\"proResourceSpan"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"display: none; float: left; margin-left: 9px;\"><select name=\"proFolderEmployee"+strProId+"_"+strTaskId+"_"+cnt+"\" id=\"proFolderEmployee"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"width:100px !important;\" multiple size=\"4\">"+proEmployee+"</select></span>"
			    +"</div>"
			    +"<div style=\"float: left; width: 100%; margin-top: 5px;\"><span style=\"float: left;\"><a href=\"javascript:void(0);\" style=\"font-weight:normal;\" id=\"sharePoc"+strProId+"_"+strTaskId+"_"+cnt+"\" onclick=\"showPoc('"+strProId+"' ,'"+strTaskId+"','"+cnt+"')\">share customer</a></span>"
			    +"<input type=\"hidden\" name=\"showPocType"+strProId+"_"+strTaskId+"_"+cnt+"\" id=\"showPocType"+strProId+"_"+strTaskId+"_"+cnt+"\" value=\"1\">"
			    +"<span id=\"proPocSpan"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"display: none; float: left; margin-left: 9px;\"><select name=\"proFolderPoc"+strProId+"_"+strTaskId+"_"+cnt+"\" id=\"proFolderPoc"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"width:100px !important;\" multiple size=\"4\">"+proPoc+"</select></span>"
			    +"</div>";
			
		    var cell4 = row.insertCell(4);
		    cell4.setAttribute("valign","top");
		    cell4.setAttribute('class','tdBorderLine');
		    cell4.setAttribute('nowrap','nowrap');
		    cell4.innerHTML = "<span id=\"isFolderEditSpan"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"float: left; width: 100%;\"><input type=\"hidden\" name=\"isFolderEdit"+strProId+"_"+strTaskId+"\" id=\"isFolderEdit"+strProId+"_"+strTaskId+"_"+cnt+"\" value=\"1\">"+
		    	"<input type=\"checkbox\" name=\"folderEdit"+strProId+"_"+strTaskId+"\" id=\"folderEdit"+strProId+"_"+strTaskId+"_"+cnt+"\" onclick=\"checkStatus(this, 'isFolderEdit"+strProId+"_"+strTaskId+"_"+cnt+"');\" checked/>Edit</span>"+
		    	"<span id=\"isFolderDeleteSpan"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"float: left; width: 100%;\"><input type=\"hidden\" name=\"isFolderDelete"+strProId+"_"+strTaskId+"\" id=\"isFolderDelete"+strProId+"_"+strTaskId+"_"+cnt+"\" value=\"0\">"+
		    	"<input type=\"checkbox\" name=\"folderDelete"+strProId+"_"+strTaskId+"\" id=\"folderDelete"+strProId+"_"+strTaskId+"_"+cnt+"\" onclick=\"checkStatus(this, 'isFolderDelete"+strProId+"_"+strTaskId+"_"+cnt+"');\"/>Delete</span>";
			
		    document.getElementById(rowCountName).value = cnt;
		} 


		function deleteFolder(strProId, strTaskId, count, tableName) {
			if(confirm('Are you sure, you want to delete this folder?')) {
				var trIndex = document.getElementById("folderTR"+strProId+"_"+strTaskId+"_"+count).rowIndex;
			    document.getElementById(tableName).deleteRow(trIndex);
			    
			    var table = document.getElementById(tableName);
			    var rowCount = table.rows.length;
			    if(parseInt(rowCount) == 1) {
				    if(document.getElementById("buttonDiv"+strProId+"_"+strTaskId)) {
						document.getElementById("buttonDiv"+strProId+"_"+strTaskId).style.display = 'none';
					}
			    }
			}
		}
	 
	 
	 	function changeCategoryType(val, categorySpan, projectSpan) {
			if(val == '1') {
				document.getElementById(categorySpan).style.display = "none";
				document.getElementById(projectSpan).style.display = "block";
			} else if(val == '2') {
				document.getElementById(categorySpan).style.display = "block";
				document.getElementById(projectSpan).style.display = "none";
			}
		}
	 

	function addNewSubFolder(strProId,strTaskId,folderTRId, rwIndex, tableName, rowCountName) { 
			var proTasks = document.getElementById("projectTasks"+strProId+"_"+strTaskId).value;
			var proEmployee = document.getElementById("resourceIds"+strProId+"_"+strTaskId).value;
			var proCategory = document.getElementById("projectCategoryType"+strProId+"_"+strTaskId).value;
			var proPoc = document.getElementById("projectPoc"+strProId+"_"+strTaskId).value;
			
			var fdCnt = document.getElementById(rowCountName).value;
			var cnt=(parseInt(fdCnt)+1);
			var val=(parseInt(rwIndex)+1);
		    var table = document.getElementById(tableName);
		    var rowCount = table.rows.length;
		    var row = table.insertRow(val);
		    
		    row.id="SubFolderTR"+strProId+"_"+strTaskId+"_"+cnt;
		    var cell0 = row.insertCell(0);
		    //cell1.setAttribute('style', 'border-bottom: 1px solid #B6B6B6' );
		    cell0.setAttribute('style','width: 550px;');
		    cell0.setAttribute('class','tdBorderLine');
		    cell0.innerHTML = "<span style=\"float:left; margin-left: 50px; margin-right: 9px;\"><input type=\"hidden\" name=\"SubFolderTR"+strProId+"_"+strTaskId+"_"+folderTRId+"\" id=\"SubFolderTR"+strProId+"_"+strTaskId+"_"+cnt+"\" value=\""+cnt+"\">"+
		    "<input type=\"text\" name=\"strSubFolderName"+strProId+"_"+strTaskId+"_"+folderTRId+"\" id=\"strSubFolderName"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"width:200px !important; color: gray;\" onclick=\"clearData(this.id);\" onblur=\"fillData(this.id, 4);\" value=\"Sub Folder Name\"/></span>"
		    +"<span style=\"float:left;\"><a href=\"javascript:void(0);\" style=\"float: left; margin: 0px 0px 0px 0px;\" onclick=\"addNewSubFolder('"+strProId+"','"+strTaskId+"','"+folderTRId+"', '"+rwIndex+"', '"+tableName+"' ,'"+rowCountName+"');\" class=\"fa fa-fw fa-plus\" title=\"Create New Folder\">&nbsp;</a>"
		    +"<a href=\"javascript:void(0)\" style=\"float: left; margin: -4px 0px 14px 0px;\" onclick=\"deleteSubFolder('"+strProId+"','"+strTaskId+"','"+cnt+"', '"+tableName+"')\" id=\""+cnt+"\" class=\"fa fa-fw fa-remove\" title=\"Remove Folder\">&nbsp;</a></span>"
	        +"<span style=\"float:left; width: 100%; margin-left: 50px; margin-top: 5px;\"><textarea rows=\"3\" name=\"strSubFolderDescription"+strProId+"_"+strTaskId+"_"+folderTRId+"\" id=\"strSubFolderDescription"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"width: 330px !important; color: gray;\" onclick=\"clearData(this.id);\" onblur=\"fillData(this.id, 1);\">Description</textarea></span>"
	        +"<a href=\"javascript:void(0);\" style=\"float:left; width:86%; margin: 0px 0px 0px 70px;\" onclick=\"addNewSubFolderDocs('"+strProId+"','"+strTaskId+"','"+cnt+"', this.parentNode.parentNode.rowIndex, '"+tableName+"' ,'"+rowCountName+"');\"> +Add Document</a>";
		    
	        var cell1 = row.insertCell(1);
		    cell1.setAttribute("valign","top");
		    cell1.setAttribute('class','tdBorderLine');
		    cell1.innerHTML = "<span style=\"float: left;\"><select name=\"proCategoryTypeSubFolder"+strProId+"_"+strTaskId+"_"+folderTRId+"\" id=\"proCategoryTypeSubFolder"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"width:100px !important;\" onchange=\"changeCategoryType(this.value, 'proSubFolderCatagorySpan"+strProId+"_"+strTaskId+"_"+cnt+"', 'proSubFolderTaskSpan"+strProId+"_"+strTaskId+"_"+cnt+"');\">"+
		    "<option value=\"1\" selected>Project</option><option value=\"2\">Category</option></select></span>";
		    
		    var cell2 = row.insertCell(2);
		    cell2.setAttribute("valign","top");
		    cell2.setAttribute('class','tdBorderLine');
		    cell2.innerHTML = "<span id=\"proSubFolderCatagorySpan"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"float: left; display: none\"><select name=\"proSubFolderCategory"+strProId+"_"+strTaskId+"_"+folderTRId+"_"+cnt+"\" id=\"proSubFolderTasksCategory"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"width:100px !important;\">"+
		     	""+proCategory+"</select></span><span id=\"proSubFolderTaskSpan"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"float: left;\"><select name=\"proSubFolderTasks"+strProId+"_"+strTaskId+"_"+folderTRId+"_"+cnt+"\" id=\"proSubFolderTasks"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"width:100px !important;\">"+
		     	""+proTasks+"</select></span>";
		    
		    var cell3 = row.insertCell(3);
		    cell3.setAttribute("valign","top");
		    cell3.setAttribute('class','tdBorderLine');
		    cell3.innerHTML = "<div style=\"float: left; width: 100%;\"><span style=\"float: left;\"><select name=\"SubfolderSharingType"+strProId+"_"+strTaskId+"_"+folderTRId+"\" id=\"SubfolderSharingType"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"width:100px !important;\" class=\"validateRequired\" onchange=\"showHideResources('"+strProId+"', '"+strTaskId+"', this.value, '"+cnt+"')\">"
			    +"<option value=\"0\">Public</option><option value=\"1\">Private Team</option><option value=\"2\">Individual Resource</option></select></span>"
			    +"<span id=\"proResourceSpan"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"display: none; float: left; margin-left: 9px;\"><select name=\"proSubFolderEmployee"+strProId+"_"+strTaskId+"_"+folderTRId+"_"+cnt+"\" id=\"proSubFolderEmployee"+strProId+"_"+cnt+"\" style=\"width:100px !important;\" multiple size=\"4\">"+proEmployee+"</select></span>"
			    +"</div>"
			    +"<div style=\"float: left; width: 100%; margin-top: 5px;\"><span style=\"float: left;\"><a href=\"javascript:void(0);\" style=\"font-weight:normal;\" id=\"sharePoc"+strProId+"_"+strTaskId+"_"+cnt+"\" onclick=\"showPoc('"+strProId+"','"+strTaskId+"','"+cnt+"')\">share customer</a></span>"
			    +"<input type=\"hidden\" name=\"showPocType"+strProId+"_"+strTaskId+"_"+cnt+"\" id=\"showPocType"+strProId+"_"+strTaskId+"_"+cnt+"\" value=\"1\">"
			    +"<span id=\"proPocSpan"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"display: none; float: left; margin-left: 9px;\"><select name=\"proSubFolderPoc"+strProId+"_"+strTaskId+"_"+folderTRId+"_"+cnt+"\" id=\"proSubFolderPoc"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"width:100px !important;\" multiple size=\"4\">"+proPoc+"</select></span>"
			    +"</div>";
			
		    var cell4 = row.insertCell(4);
		    cell4.setAttribute("valign","top");
		    cell4.setAttribute('class','tdBorderLine');
		    cell4.setAttribute('nowrap','nowrap');
		    cell4.innerHTML = "<span id=\"isSubFolderEditSpan"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"float: left; width: 100%;\"><input type=\"hidden\" name=\"isSubFolderEdit"+strProId+"_"+strTaskId+"_"+folderTRId+"\" id=\"isSubFolderEdit"+strProId+"_"+strTaskId+"_"+cnt+"\" value=\"1\">"+
		    	"<input type=\"checkbox\" name=\"subFolderEdit"+strProId+"_"+strTaskId+"_"+folderTRId+"\" id=\"subFolderEdit"+strProId+"_"+strTaskId+"_"+cnt+"\" onclick=\"checkStatus(this, 'isSubFolderEdit"+strProId+"_"+strTaskId+"_"+cnt+"');\" checked/>Edit</span>"+
		    	"<span id=\"isSubFolderDeleteSpan"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"float: left; width: 100%;\"><input type=\"hidden\" name=\"isSubFolderDelete"+strProId+"_"+strTaskId+"_"+folderTRId+"\" id=\"isSubFolderDelete"+strProId+"_"+strTaskId+"_"+cnt+"\" value=\"0\">"+
		    	"<input type=\"checkbox\" name=\"subFolderDelete"+strProId+"_"+strTaskId+"_"+folderTRId+"\" id=\"subFolderDelete"+strProId+"_"+strTaskId+"_"+cnt+"\" onclick=\"checkStatus(this, 'isSubFolderDelete"+strProId+"_"+strTaskId+"_"+cnt+"');\"/>Delete</span>";
			
		    document.getElementById(rowCountName).value = cnt;
		}

	function deleteSubFolder(strProId, strTaskId, count, tableName) {
		if(confirm('Are you sure, you want to delete this folder?')) {
			var trIndex = document.getElementById("SubFolderTR"+strProId+"_"+strTaskId+"_"+count).rowIndex;
		    document.getElementById(tableName).deleteRow(trIndex);
		}
	}

	function addNewSubFolderDocs(strProId, strTaskId, folderTRId, rwIndex, tableName, rowCountName) {
			var proTasks = document.getElementById("projectTasks"+strProId+"_"+strTaskId).value;
			var proEmployee = document.getElementById("resourceIds"+strProId+"_"+strTaskId).value;
			var proCategory = document.getElementById("projectCategoryType"+strProId+"_"+strTaskId).value;
			var proPoc = document.getElementById("projectPoc"+strProId+"_"+strTaskId).value;
			
			var fdCnt = document.getElementById(rowCountName).value;
			var cnt=(parseInt(fdCnt)+1);
			var val=(parseInt(rwIndex)+1);
		    
		    var table = document.getElementById(tableName);
		    var rowCount = table.rows.length;
		    var row = table.insertRow(val);
		    
		    row.id="SubfolderDocTR"+strProId+"_"+strTaskId+"_"+cnt;
		    var cell0 = row.insertCell(0);
		    //cell1.setAttribute('style', 'border-bottom: 1px solid #B6B6B6' );
		    cell0.setAttribute('style','width: 550px;');
		    cell0.setAttribute('class','tdBorderLine');
		    cell0.innerHTML = "<span style=\"float:left; margin-left: 100px; margin-right: 9px;\"><input type=\"hidden\" name=\"SubfolderDocsTRId"+strProId+"_"+strTaskId+"_"+folderTRId+"\" id=\"folderDocsTRId"+strProId+"_"+strTaskId+"_"+cnt+"\" value=\""+cnt+"\">"+
		    "<input type=\"text\" name=\"strSubFolderScopeDoc"+strProId+"_"+strTaskId+"_"+folderTRId+"\" id=\"strSubFolderScopeDoc"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"width:150px !important; color: gray; margin-bottom: 5px;\" onclick=\"clearData(this.id);\" onblur=\"fillData(this.id, 3);\" value=\"Document Scope\"/> "+
		    "<input type=\"file\" name=\"strSubFolderDoc"+strProId+"_"+strTaskId+"_"+folderTRId+"\" id=\"strSubFolderDoc"+strProId+"_"+strTaskId+"_"+cnt+"\" size=\"5\"/></span>"
		    +"<a href=\"javascript:void(0)\" onclick=\"addNewSubFolderDocs('"+strProId+"','"+strTaskId+"','"+folderTRId+"', this.parentNode.parentNode.rowIndex, '"+tableName+"' ,'"+rowCountName+"')\" class=\"fa fa-fw fa-plus\" title=\"Add Document\">&nbsp;</a>"
	        +"<a href=\"javascript:void(0)\" onclick=\"deleteSubFolderDocs('"+strProId+"','"+strTaskId+"','"+cnt+"', '"+tableName+"')\" id=\""+cnt+"\" class=\"fa fa-fw fa-remove\" title=\"Remove Document\">&nbsp;</a>"
	        +"<span style=\"float:left; width: 100%; margin-left: 100px; margin-top: 5px;\"><textarea rows=\"3\" name=\"strSubFolderDocDescription"+strProId+"_"+strTaskId+"_"+folderTRId+"\" id=\"strSubFolderDocDescription"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"width: 330px !important; color: gray;\" onclick=\"clearData(this.id);\" onblur=\"fillData(this.id, 1);\">Description</textarea></span>"
	        ;
		    
	        var cell1 = row.insertCell(1);
		    cell1.setAttribute("valign","top");
		    cell1.setAttribute('class','tdBorderLine');
		    cell1.innerHTML = "<span style=\"float: left;\"><select name=\"proCategoryTypeSubFolderDoc"+strProId+"_"+strTaskId+"_"+folderTRId+"\" id=\"proCategoryTypeSubFolderDoc"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"width:100px !important;\" onchange=\"changeCategoryType(this.value, 'proSubFolderDocCatagorySpan"+strProId+"_"+strTaskId+"_"+cnt+"', 'proSubFolderDocTaskSpan"+strProId+"_"+strTaskId+"_"+cnt+"');\">"+
		    "<option value=\"1\" selected>Project</option><option value=\"2\">Category</option></select></span>";
		    
		    var cell2 = row.insertCell(2);
		    cell2.setAttribute("valign","top");
		    cell2.setAttribute('class','tdBorderLine');
		    cell2.innerHTML = "<span id=\"proSubFolderDocCatagorySpan"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"float: left; display: none\"><select name=\"proSubFolderDocCategory"+strProId+"_"+strTaskId+"_"+folderTRId+"_"+cnt+"\" id=\"proSubFolderDocCategory"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"width:100px !important;\">"+
		     	""+proCategory+"</select></span><span id=\"proSubFolderDocTaskSpan"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"float: left;\"><select name=\"proSubFolderDocTasks"+strProId+"_"+strTaskId+"_"+folderTRId+"_"+cnt+"\" id=\"proSubFolderDocTasks"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"width:100px !important;\">"+
		     	""+proTasks+"</select></span>";
		     	
		    var cell3 = row.insertCell(3);
		    cell3.setAttribute("valign","top");
		    cell3.setAttribute('class','tdBorderLine');
		    cell3.innerHTML = "<div style=\"float: left; width: 100%;\"><span style=\"float: left;\"><select name=\"SubfolderDocDharingType"+strProId+"_"+strTaskId+"_"+folderTRId+"\" id=\"SubfolderDocDharingType"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"width:100px !important;\" class=\"validateRequired\" onchange=\"showHideResources('"+strProId+"', '"+strTaskId+"', this.value, '"+cnt+"')\">"
			    +"<option value=\"0\">Public</option><option value=\"1\">Private Team</option><option value=\"2\">Individual Resource</option></select></span>"
			    +"<span id=\"proResourceSpan"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"display: none; float: left; margin-left: 9px;\"><select name=\"proSubFolderDocEmployee"+strProId+"_"+strTaskId+"_"+folderTRId+"_"+cnt+"\" id=\"proSubFolderDocEmployee"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"width:100px !important;\" multiple size=\"4\">"+proEmployee+"</select></span>"
			    +"</div>"
			    +"<div style=\"float: left; width: 100%; margin-top: 5px;\"><span style=\"float: left;\"><a href=\"javascript:void(0);\" style=\"font-weight:normal;\" id=\"sharePoc"+strProId+"_"+strTaskId+"_"+cnt+"\" onclick=\"showPoc('"+strProId+"','"+strTaskId+"','"+cnt+"')\">share customer</a></span>"
			    +"<input type=\"hidden\" name=\"showPocType"+strProId+"_"+strTaskId+"_"+cnt+"\" id=\"showPocType"+strProId+"_"+strTaskId+"_"+cnt+"\" value=\"1\">"
			    +"<span id=\"proPocSpan"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"display: none; float: left; margin-left: 9px;\"><select name=\"proSubFolderDocPoc"+strProId+"_"+strTaskId+"_"+folderTRId+"_"+cnt+"\" id=\"proSubFolderDocPoc"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"width:100px !important;\" multiple size=\"4\">"+proPoc+"</select></span>"
			    +"</div>";
		
		    var cell4 = row.insertCell(4);
		    cell4.setAttribute("valign","top");
		    cell4.setAttribute('class','tdBorderLine');
		    cell4.setAttribute('nowrap','nowrap');
		    cell4.innerHTML = "<span id=\"isSubFolderDocEditSpan"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"float: left; width: 100%;\"><input type=\"hidden\" name=\"isSubFolderDocEdit"+strProId+"_"+strTaskId+"_"+folderTRId+"\" id=\"isSubFolderDocEdit"+strProId+"_"+strTaskId+"_"+cnt+"\" value=\"1\">"+
		    	"<input type=\"checkbox\" name=\"subFolderDocEdit"+strProId+"_"+strTaskId+"_"+folderTRId+"\" id=\"subFolderDocEdit"+strProId+"_"+strTaskId+"_"+cnt+"\" onclick=\"checkStatus(this, 'isSubFolderDocEdit"+strProId+"_"+strTaskId+"_"+cnt+"');\" checked/>Edit</span>"+
		    	"<span id=\"isSubFolderDocDeleteSpan"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"float: left; width: 100%;\"><input type=\"hidden\" name=\"isSubFolderDocDelete"+strProId+"_"+strTaskId+"_"+folderTRId+"\" id=\"isSubFolderDocDelete"+strProId+"_"+strTaskId+"_"+cnt+"\" value=\"0\">"+
		    	"<input type=\"checkbox\" name=\"subFolderDocDelete"+strProId+"_"+strTaskId+"_"+folderTRId+"\" id=\"subFolderDocDelete"+strProId+"_"+strTaskId+"_"+cnt+"\" onclick=\"checkStatus(this, 'isSubFolderDocDelete"+strProId+"_"+strTaskId+"_"+cnt+"');\"/>Delete</span>";

		    document.getElementById(rowCountName).value = cnt;
		}


	function deleteSubFolderDocs(strProId, strTaskId, count, tableName) {
		if(confirm('Are you sure, you want to delete this document?')) {
			var trIndex = document.getElementById("SubfolderDocTR"+strProId+"_"+strTaskId+"_"+count).rowIndex;
		    document.getElementById(tableName).deleteRow(trIndex);
		}
	}


	function addNewDocs(strProId, strTaskId, tableName, rowCountName) { 
		var proTasks = document.getElementById("projectTasks"+strProId+"_"+strTaskId).value;
		var proEmployee = document.getElementById("resourceIds"+strProId+"_"+strTaskId).value;
		var proCategory = document.getElementById("projectCategoryType"+strProId+"_"+strTaskId).value;
		var proPoc = document.getElementById("projectPoc"+strProId+"_"+strTaskId).value;
		
		if(document.getElementById("buttonDiv"+strProId+"_"+strTaskId)) {
			document.getElementById("buttonDiv"+strProId+"_"+strTaskId).style.display = 'block';
		}
			var fdCnt = document.getElementById(rowCountName).value;
			var cnt=(parseInt(fdCnt)+1);
			
		    var table = document.getElementById(tableName);
		    var rowCount = table.rows.length;
		    var row = table.insertRow(rowCount);
		    
		    row.id="docTR"+strProId+"_"+strTaskId+"_"+cnt;
		    var cell0 = row.insertCell(0);
		    //cell1.setAttribute('style', 'border-bottom: 1px solid #B6B6B6' );
		    cell0.setAttribute('style','width: 550px;');
		    cell0.setAttribute('class','tdBorderLine');
		    cell0.innerHTML = "<span style=\"float:left; margin-right: 9px;\"><input type=\"hidden\" name=\"docsTRId"+strProId+"_"+strTaskId+"\" id=\"docsTRId"+strProId+"_"+strTaskId+"_"+cnt+"\" value=\""+cnt+"\">"+
		    "<input type=\"text\" name=\"strScopeDoc"+strProId+"_"+strTaskId+"\" id=\"strScopeDoc"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"width:150px !important; color: gray; margin-bottom: 5px;\" onclick=\"clearData(this.id);\" onblur=\"fillData(this.id, 3);\" value=\"Document Scope\"/> "
		    +"<input type=\"file\" name=\"strDoc"+strProId+"_"+strTaskId+"\" id=\"strDoc"+strProId+"_"+strTaskId+"_"+cnt+"\" size=\"5\"/></span>"
		    +"<span style=\"float:left;\"><a href=\"javascript:void(0)\" onclick=\"addNewDocs('"+strProId+"','"+strTaskId+"', '"+tableName+"' ,'"+rowCountName+"')\" class=\"fa fa-fw fa-plus\" title=\"Add Document\">&nbsp;</a>"
	        +"<a href=\"javascript:void(0)\" onclick=\"deleteDocs('"+strProId+"','"+strTaskId+"','"+cnt+"', '"+tableName+"')\" id=\""+cnt+"\" class=\"fa fa-fw fa-remove\" title=\"Remove Document\">&nbsp;</a></span>"
	        +"<span style=\"float:left; width: 100%; margin-top: 5px;\"><textarea rows=\"3\" name=\"strDocDescription"+strProId+"_"+strTaskId+"\" id=\"strDocDescription"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"width: 330px !important; color: gray;\" onclick=\"clearData(this.id);\" onblur=\"fillData(this.id, 1);\">Description</textarea></span>"
	        ;
		    
	        var cell1 = row.insertCell(1);
		    cell1.setAttribute("valign","top");
		    cell1.setAttribute('class','tdBorderLine');
		    cell1.innerHTML = "<span style=\"float: left;\"><select name=\"proCategoryTypeDoc"+strProId+"_"+strTaskId+"\" id=\"proCategoryTypeDoc"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"width:100px !important;\" onchange=\"changeCategoryType(this.value, 'proDocCatagorySpan"+strProId+"_"+strTaskId+"_"+cnt+"', 'proDocTaskSpan"+strProId+"_"+strTaskId+"_"+cnt+"');\">"+
		    "<option value=\"1\" selected>Project</option><option value=\"2\">Category</option></select></span>";
		    
		    var cell2 = row.insertCell(2);
		    cell2.setAttribute("valign","top");
		    cell2.setAttribute('class','tdBorderLine');
		    cell2.innerHTML = "<span id=\"proDocCatagorySpan"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"float: left; display: none\"><select name=\"proDocCategory"+strProId+"_"+strTaskId+"_"+cnt+"\" id=\"proDocCategory"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"width:100px !important;\">"+
		     	""+proCategory+"</select></span><span id=\"proDocTaskSpan"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"float: left;\"><select name=\"proDocTasks"+strProId+"_"+strTaskId+"_"+cnt+"\" id=\"proDocTasks"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"width:100px !important;\">"+
		     	""+proTasks+"</select></span>";
		     	
		    var cell3 = row.insertCell(3);
		    cell3.setAttribute("valign","top");
		    cell3.setAttribute('class','tdBorderLine');
		    cell3.innerHTML = "<div style=\"float: left; width: 100%;\"><span style=\"float: left;\"><select name=\"docSharingType"+strProId+"_"+strTaskId+"\" id=\"docSharingType"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"width:100px !important;\" class=\"validateRequired\" onchange=\"showHideResources('"+strProId+"', '"+strTaskId+"', this.value, '"+cnt+"')\">"
			    +"<option value=\"0\">Public</option><option value=\"1\">Private Team</option><option value=\"2\">Individual Resource</option></select></span>"
			    +"<span id=\"proResourceSpan"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"display: none; float: left; margin-left: 9px;\"><select name=\"proDocEmployee"+strProId+"_"+strTaskId+"_"+cnt+"\" id=\"proDocEmployee"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"width:100px !important;\" multiple size=\"4\">"+proEmployee+"</select></span>"
			    +"</div>"
			    +"<div style=\"float: left; width: 100%; margin-top: 5px;\"><span style=\"float: left;\"><a href=\"javascript:void(0);\" style=\"font-weight:normal;\" id=\"sharePoc"+strProId+"_"+strTaskId+"_"+cnt+"\" onclick=\"showPoc('"+strProId+"','"+strTaskId+"','"+cnt+"')\">share customer</a></span>"
			    +"<input type=\"hidden\" name=\"showPocType"+strProId+"_"+strTaskId+"_"+cnt+"\" id=\"showPocType"+strProId+"_"+strTaskId+"_"+cnt+"\" value=\"1\">"
			    +"<span id=\"proPocSpan"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"display: none; float: left; margin-left: 9px;\"><select name=\"proDocPoc"+strProId+"_"+strTaskId+"_"+cnt+"\" id=\"proDocPoc"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"width:100px !important;\" multiple size=\"4\">"+proPoc+"</select></span>"
			    +"</div>";
	        
		    var cell4 = row.insertCell(4);
		    cell4.setAttribute("valign","top");
		    cell4.setAttribute('class','tdBorderLine');
		    cell4.setAttribute('nowrap','nowrap');
		    cell4.innerHTML = "<span id=\"isDocEditSpan"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"float: left; width: 100%;\"><input type=\"hidden\" name=\"isDocEdit"+strProId+"_"+strTaskId+"\" id=\"isDocEdit"+strProId+"_"+strTaskId+"_"+cnt+"\" value=\"1\">"+
		    	"<input type=\"checkbox\" name=\"docEdit"+strProId+"_"+strTaskId+"\" id=\"docEdit"+strProId+"_"+strTaskId+"_"+cnt+"\" onclick=\"checkStatus(this, 'isDocEdit"+strProId+"_"+strTaskId+"_"+cnt+"');\" checked/>Edit</span>"+
		    	"<span id=\"isDocDeleteSpan"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"float: left; width: 100%;\"><input type=\"hidden\" name=\"isDocDelete"+strProId+"_"+strTaskId+"\" id=\"isDocDelete"+strProId+"_"+strTaskId+"_"+cnt+"\" value=\"0\">"+
		    	"<input type=\"checkbox\" name=\"docDelete"+strProId+"_"+strTaskId+"\" id=\"docDelete"+strProId+"_"+strTaskId+"_"+cnt+"\" onclick=\"checkStatus(this, 'isDocDelete"+strProId+"_"+strTaskId+"_"+cnt+"');\"/>Delete</span>";

		    document.getElementById(rowCountName).value = cnt;
		}


	function deleteDocs(strProId, strTaskId, count, tableName) {
		if(confirm('Are you sure, you want to delete this document?')) {
			var trIndex = document.getElementById("docTR"+strProId+"_"+strTaskId+"_"+count).rowIndex;
		    document.getElementById(tableName).deleteRow(trIndex);
		    
		    var table = document.getElementById(tableName);
		    var rowCount = table.rows.length;
		    if(parseInt(rowCount) == 1) {
			    if(document.getElementById("buttonDiv"+strProId+"_"+strTaskId)) {
					document.getElementById("buttonDiv"+strProId+"_"+strTaskId).style.display = 'none';
				}
		    }
		}
	}


	function addNewFolderDocs(strProId, strTaskId, folderTRId, rwIndex, tableName, rowCountName) {
		var proTasks = document.getElementById("projectTasks"+strProId+"_"+strTaskId).value;
		var proEmployee = document.getElementById("resourceIds"+strProId+"_"+strTaskId).value;
		var proCategory = document.getElementById("projectCategoryType"+strProId+"_"+strTaskId).value;
		var proPoc = document.getElementById("projectPoc"+strProId+"_"+strTaskId).value;
		
		//alert("rwIndex ===>> " + rwIndex);
		var fdCnt = document.getElementById(rowCountName).value;
			var cnt=(parseInt(fdCnt)+1);
			var val=(parseInt(rwIndex)+1);
		    
		    var table = document.getElementById(tableName);
		    var rowCount = table.rows.length;
		    var row = table.insertRow(val);
		    
		    row.id="folderDocTR"+strProId+"_"+strTaskId+"_"+cnt;
		    var cell0 = row.insertCell(0);
		    //cell1.setAttribute('style', 'border-bottom: 1px solid #B6B6B6' );
		    cell0.setAttribute('style','width: 550px;');
		    cell0.setAttribute('class','tdBorderLine');
		    cell0.innerHTML = "<span style=\"float:left; margin-left: 50px; margin-right: 9px;\"><input type=\"hidden\" name=\"folderDocsTRId"+strProId+"_"+strTaskId+"_"+folderTRId+"\" id=\"folderDocsTRId"+strProId+"_"+strTaskId+"_"+cnt+"\" value=\""+cnt+"\">"+
		    "<input type=\"text\" name=\"strFolderScopeDoc"+strProId+"_"+strTaskId+"_"+folderTRId+"\" id=\"strFolderScopeDoc"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"width:150px !important; color: gray; margin-bottom: 5px;\" onclick=\"clearData(this.id);\" onblur=\"fillData(this.id, 3);\" value=\"Document Scope\"/> "+
		    "<input type=\"file\" name=\"strFolderDoc"+strProId+"_"+strTaskId+"_"+folderTRId+"\" id=\"strFolderDoc"+strProId+"_"+strTaskId+"_"+cnt+"\" size=\"5\"/></span>"
		    +"<a href=\"javascript:void(0)\" onclick=\"addNewFolderDocs('"+strProId+"','"+strTaskId+"','"+folderTRId+"', this.parentNode.parentNode.rowIndex, '"+tableName+"' ,'"+rowCountName+"')\" class=\"fa fa-fw fa-plus\" title=\"Add Document\">&nbsp;</a>"
	        +"<a href=\"javascript:void(0)\" onclick=\"deleteFolderDocs('"+strProId+"','"+strTaskId+"','"+cnt+"', '"+tableName+"')\" id=\""+cnt+"\" class=\"fa fa-fw fa-remove\" title=\"Remove Document\">&nbsp;</a>"
	        +"<span style=\"float:left; width: 100%; margin-left: 50px; margin-top: 5px;\"><textarea rows=\"3\" name=\"strFolderDocDescription"+strProId+"_"+strTaskId+"_"+folderTRId+"\" id=\"strFolderDocDescription"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"width: 330px !important; color: gray;\" onclick=\"clearData(this.id);\" onblur=\"fillData(this.id, 1);\">Description</textarea></span>"
	        ;
		    
	        var cell1 = row.insertCell(1);
		    cell1.setAttribute("valign","top");
		    cell1.setAttribute('class','tdBorderLine');
		    cell1.innerHTML = "<span style=\"float: left;\"><select name=\"proCategoryTypeFolderDoc"+strProId+"_"+strTaskId+"_"+folderTRId+"\" id=\"proCategoryTypeFolderDoc"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"width:100px !important;\" onchange=\"changeCategoryType(this.value, 'proFolderDocCatagorySpan"+strProId+"_"+strTaskId+"_"+cnt+"', 'proFolderDocTaskSpan"+strProId+"_"+strTaskId+"_"+cnt+"');\">"+
		    "<option value=\"1\" selected>Project</option><option value=\"2\">Category</option></select></span>";
		    
		    var cell2 = row.insertCell(2);
		    cell2.setAttribute("valign","top");
		    cell2.setAttribute('class','tdBorderLine');
		    cell2.innerHTML = "<span id=\"proFolderDocCatagorySpan"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"float: left; display: none\"><select name=\"proFolderDocCategory"+strProId+"_"+strTaskId+"_"+folderTRId+"_"+cnt+"\" id=\"proFolderDocCategory"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"width:100px !important;\">"+
		     	""+proCategory+"</select></span><span id=\"proFolderDocTaskSpan"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"float: left;\"><select name=\"proFolderDocTasks"+strProId+"_"+strTaskId+"_"+folderTRId+"_"+cnt+"\" id=\"proFolderDocTasks"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"width:100px !important;\">"+
		     	""+proTasks+"</select></span>";
		     	
		    var cell3 = row.insertCell(3);
		    cell3.setAttribute("valign","top");
		    cell3.setAttribute('class','tdBorderLine');
		    cell3.innerHTML = "<div style=\"float: left; width: 100%;\"><span style=\"float: left;\"><select name=\"folderDocDharingType"+strProId+"_"+strTaskId+"_"+folderTRId+"\" id=\"folderDocDharingType"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"width:100px !important;\" class=\"validateRequired\" onchange=\"showHideResources('"+strProId+"', '"+strTaskId+"', this.value, '"+cnt+"')\">"
			    +"<option value=\"0\">Public</option><option value=\"1\">Private Team</option><option value=\"2\">Individual Resource</option></select></span>"
			    +"<span id=\"proResourceSpan"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"display: none; float: left; margin-left: 9px;\"><select name=\"proFolderDocEmployee"+strProId+"_"+strTaskId+"_"+folderTRId+"_"+cnt+"\" id=\"proFolderDocEmployee"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"width:100px !important;\" multiple size=\"4\">"+proEmployee+"</select></span>"
			    +"</div>"
			    +"<div style=\"float: left; width: 100%; margin-top: 5px;\"><span style=\"float: left;\"><a href=\"javascript:void(0);\" style=\"font-weight:normal;\" id=\"sharePoc"+strProId+"_"+strTaskId+"_"+cnt+"\" onclick=\"showPoc('"+strProId+"','"+strTaskId+"','"+cnt+"')\">share customer</a></span>"
			    +"<input type=\"hidden\" name=\"showPocType"+strProId+"_"+strTaskId+"_"+cnt+"\" id=\"showPocType"+strProId+"_"+strTaskId+"_"+cnt+"\" value=\"1\">"
			    +"<span id=\"proPocSpan"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"display: none; float: left; margin-left: 9px;\"><select name=\"proFolderDocPoc"+strProId+"_"+strTaskId+"_"+folderTRId+"_"+cnt+"\" id=\"proFolderDocPoc"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"width:100px !important;\" multiple size=\"4\">"+proPoc+"</select></span>"
			    +"</div>";
		    
		    var cell4 = row.insertCell(4);
		    cell4.setAttribute("valign","top");
		    cell4.setAttribute('class','tdBorderLine');
		    cell4.setAttribute('nowrap','nowrap');
		    cell4.innerHTML = "<span id=\"isFolderDocEditSpan"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"float: left; width: 100%;\"><input type=\"hidden\" name=\"isFolderDocEdit"+strProId+"_"+strTaskId+"_"+folderTRId+"\" id=\"isFolderDocEdit"+strProId+"_"+strTaskId+"_"+cnt+"\" value=\"1\">"+
		    	"<input type=\"checkbox\" name=\"folderDocEdit"+strProId+"_"+strTaskId+"_"+folderTRId+"\" id=\"folderDocEdit"+strProId+"_"+strTaskId+"_"+cnt+"\" onclick=\"checkStatus(this, 'isFolderDocEdit"+strProId+"_"+strTaskId+"_"+cnt+"');\" checked/>Edit</span>"+
		    	"<span id=\"isFolderDocDeleteSpan"+strProId+"_"+strTaskId+"_"+cnt+"\" style=\"float: left; width: 100%;\"><input type=\"hidden\" name=\"isFolderDocDelete"+strProId+"_"+strTaskId+"_"+folderTRId+"\" id=\"isFolderDocDelete"+strProId+"_"+strTaskId+"_"+cnt+"\" value=\"0\">"+
		    	"<input type=\"checkbox\" name=\"folderDocDelete"+strProId+"_"+strTaskId+"_"+folderTRId+"\" id=\"folderDocDelete"+strProId+"_"+strTaskId+"_"+cnt+"\" onclick=\"checkStatus(this, 'isFolderDocDelete"+strProId+"_"+strTaskId+"_"+cnt+"');\"/>Delete</span>";

		    document.getElementById(rowCountName).value = cnt;
		}


	function deleteFolderDocs(strProId, strTaskId, count, tableName) {
		if(confirm('Are you sure, you want to delete this document?')) {
			var trIndex = document.getElementById("folderDocTR"+strProId+"_"+strTaskId+"_"+count).rowIndex;
		    document.getElementById(tableName).deleteRow(trIndex);
		}
	}


	function showHideResources(strProId,strTaskId, val, count) {
		if(val == '2') {
			document.getElementById("proResourceSpan"+strProId+"_"+strTaskId+"_"+count).style.display = 'block';
		} else {
			document.getElementById("proResourceSpan"+strProId+"_"+strTaskId+"_"+count).style.display = 'none';
		}
	}

	function showPoc(strProId,strTaskId,count) {
		var val = document.getElementById("showPocType"+strProId+"_"+strTaskId+"_"+count).value;
		if(val == '1') {
			document.getElementById("proPocSpan"+strProId+"_"+strTaskId+"_"+count).style.display = 'block';
			document.getElementById("showPocType"+strProId+"_"+strTaskId+"_"+count).value = '0';
		} else {
			document.getElementById("proPocSpan"+strProId+"_"+strTaskId+"_"+count).style.display = 'none';
			document.getElementById("showPocType"+strProId+"_"+strTaskId+"_"+count).value = '1';
		}
	}

	function showTblHeader(strProId, taskId) {
		document.getElementById("folderTR"+strProId+'_'+taskId+"_0").style.display = 'table-row';   
	}

	function viewVersionHistory(strProDocId,docStatusId,docVersionDivId,downSpanId,upSpanId,type,filePath,fileDir){
		var status = document.getElementById(docStatusId).value;
		if(status == '0') {
			document.getElementById(docStatusId).value = '1';
			document.getElementById(downSpanId).style.display = 'none';
			document.getElementById(upSpanId).style.display = 'block';
			
			document.getElementById(docVersionDivId).style.display = 'block';
	    	$('<img src=\"images1/ajax-loading-1.gif\"/>').appendTo("#"+docVersionDivId);
	    	var action = 'ProDocVersionHistory.action?proDocumentId='+ strProDocId+'&type='+type+'&filePath='+encodeURIComponent(filePath)+'&fileDir='+encodeURIComponent(fileDir);
	    	getContent(docVersionDivId, action);
			
		} else {
			document.getElementById(docStatusId).value = '0';
			document.getElementById(downSpanId).style.display = 'block';
			document.getElementById(upSpanId).style.display = 'none';
			
			document.getElementById(docVersionDivId).innerHTML = '';
			document.getElementById(docVersionDivId).style.display = 'none';
		}
	}
	
	function verticalMenuClick(event)
	{
		debugger;
		var innerDiv ;
		
		if(event.target.localName == 'i'){
		 innerDiv = event.target.parentNode.id;
		}
		else if(event.target.localName == 'a'){
			innerDiv = event.target.id;
		}
			
		$("#divContent").scrollTop(Math.ceil($(innerDiv).position().top));
     	return false;
	}

	/* ************************************************************ End Add Document Script ********************************************** */	

	
</script>

    <%
    System.out.println("EVP.jsp");
	    UtilityFunctions uF = new UtilityFunctions(); 
		String proType = (String)request.getAttribute("proType");
		String singleTaskData = (String)request.getAttribute("singleTaskData");
		String singleTaskId = (String)request.getAttribute("singleTaskId");
		
		String taskCount = (String)request.getAttribute("taskCount");
	
		List<List<String>> proTaskList = (List<List<String>>) request.getAttribute("proTaskList");
		Map<String, List<String>> hmTaskData = (Map<String, List<String>>) request.getAttribute("hmTaskData");
	
		Map<String, List<List<String>>> hmActivities = (Map<String, List<List<String>>>) request.getAttribute("hmActivities");
		Map<String, String> hmTodayApprovedActivity = (Map<String, String>)request.getAttribute("hmTodayApprovedActivity");
		boolean timeApproveFlag = (Boolean) request.getAttribute("timeApproveFlag");
		Map<String, String> hmProTaskDependency = (Map<String, String>)request.getAttribute("hmProTaskDependency");
		if(hmProTaskDependency == null) hmProTaskDependency = new HashMap<String, String>();
		List<GetPriorityList> priorityList = (List<GetPriorityList>)request.getAttribute("priorityList");
		
		String tskCnt = (String)request.getAttribute("tskCnt");
		String subtskCnt = (String)request.getAttribute("subtskCnt");
		
	%>
	
			<div class="row">
				<%boolean activityRunningFlag = (Boolean) request.getAttribute("flag");
                for(int i=0; proTaskList != null && !proTaskList.isEmpty() && i<proTaskList.size(); i++) {
					List<String> innerList = proTaskList.get(i);
					String sTTType = "T";
					if(uF.parseToInt(innerList.get(21))>0) { 
						sTTType = "ST";
					}
				%>
				<div class="col-lg-12 col-md-12 col-sm-12">
              	<!-- TABLE: LATEST ORDERS -->
	              <div class="box box">
	                <div class="box-header with-border">
	                  <h3 class="box-title"><%=innerList.get(3) %></h3>
	                  <div class="box-tools pull-right">
	                    <button data-widget="collapse" class="btn btn-box-tool"><i class="fa fa-minus"></i></button>
	                    <button data-widget="remove" class="btn btn-box-tool"><i class="fa fa-times"></i></button>
	                  </div>
	                </div><!-- /.box-header -->
	                <div class="box-body" style="height: 350px;">
	                  <!-- <div class="table-responsive"> -->
	                  
	                  <div class="vertical-menu" style="display:none;float:left;position: sticky;top: 0;">
  <a href="#" onclick="return verticalMenuClick(event);" id="#divStatus" title="Status"><i class="fa fa-calendar-check-o" aria-hidden="true"></i></a>
  <a href="#" onclick="return verticalMenuClick(event);" id="#Priority" title="Priority"><i class="fa fa-exclamation-triangle" aria-hidden="true"></i></a>
  <a href="#" onclick="return verticalMenuClick(event);" id="#divPlannedComplation" title="Planned Complation"><i class="fa fa-line-chart" aria-hidden="true"></i></a>
  <a href="#" onclick="return verticalMenuClick(event);" id="#divDuration" title="Duration"><i class="fa fa-clock-o" aria-hidden="true"></i></a>
  <a href="#" onclick="return verticalMenuClick(event);" id="#divDocuments" title="Documents"><i class="fa fa-file-word-o" aria-hidden="true"></i></a>
  <a href="#" onclick="return verticalMenuClick(event);" id="#divActivityOrAction" title="Activity Or Action"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
</div>
	                  
	                  <div id="divContent" style="float:left;height: 245px !important;overflow-y: unset;">
						<div id ="row1" style="float: left;width: 100%;">
						<div id="divStatus" style="float: left;">
							<div class="divh">Status</div>
							<div>
								<% if(proType != null && proType.equals("MR")) { %>
									<div>
				                    	<% if(uF.parseToInt(innerList.get(37)) ==0) { %>
				                    	<span style="float:left; width: 100%; background-image: url(&quot;images1/icons/new2.gif&quot;); background-repeat: no-repeat; background-position: right top;">
				                    		Request for Task sent, awaiting response
				                    	</span>	
				                    	<% } else if(uF.parseToInt(innerList.get(37)) == -1) { %>
				                    	<span style="width: 100%;">
				                    		Your request has been denied
				                    	</span>
				                    	<% } else if(uF.parseToInt(innerList.get(37)) ==2) { %>
				                    	<span style="float:left; width: 100%; background-image: url(&quot;images1/icons/new2.gif&quot;); background-repeat: no-repeat; background-position: right top;">
				                    		Requested for reschedule, awaiting response
				                    	</span>
				                    	<% } else if(uF.parseToInt(innerList.get(37)) ==3) { %>
				                    	<span style="float:left; width: 100%; background-image: url(&quot;images1/icons/new2.gif&quot;); background-repeat: no-repeat; background-position: right top;">
				                    		Requested for re-assign, awaiting response
				                    	</span>
				                    	<% } %>
										<span style="width: 100%; margin-top: -10px;"><%=innerList.get(38) %></span>
									</div>
			                    <% } else if(proType != null && proType.equals("TR")) { %>
									<div>
			                    		<span style="float:left; width: 100%; background-image: url(&quot;images1/icons/new2.gif&quot;); background-repeat: no-repeat; background-position: center;">&nbsp;</span>
			                    	</div>
								<% } else { %>
									<div>
				                    	<span style="float: left; width: 100%;"><%=innerList.get(1) %></span>
										<span style="float: left; width: 100%;"><%=innerList.get(2) %> <%=innerList.get(8) %> <%=innerList.get(17) %></span>
									</div>
			                	<% } %>
							</div>
						</div>
						<div id="divPriority" style="padding-left: 45% !important;">
							<div class="divh">Priority</div>
							<div><%=innerList.get(4) %></div>
						</div>
						</div>
						<div id ="row2" style="float: left;padding-top: 3%;width: 100%;">
						<div id="divPlannedComplation">
							<div class="divh">Planned Complation</div>
							<div>
								<div><%=innerList.get(6) %></div>
                               <div>
			                    	<div class="anaAttrib1" style="margin-bottom: -5px;"><span id="TcompletePercent_<%=innerList.get(18) %>_<%=innerList.get(0)%>"><%=uF.formatIntoOneDecimalWithOutComma(uF.parseToDouble(innerList.get(9))) %>%</span>
			                    	<% if(proType ==null || proType.equals("null") || proType.equals("") || proType.equals("L")) { %>
			                    		<a href="javascript:void(0)" onclick="updateStatus(<%=innerList.get(0) %>,<%=innerList.get(18) %>, 'Tcomplete')"><img src="images1/icons/popup_arrow.gif" title="Update Completion Status" height="8px"/></a>
			                    	<% } %>
			                    	</div>
									<div id="Tcomplete_<%=innerList.get(18) %>_<%=innerList.get(0) %>" class="outbox">
										<div class="greenbox" style="width: <%=uF.formatIntoOneDecimalWithOutComma(uF.parseToDouble(innerList.get(9))) %>%;"></div>
									</div>
								</div>
							</div>
						</div>
						<div id="divDuration" style="padding-top: 2%;">
							<div class="divh">Duration</div>
							<div><%=innerList.get(7) %> <%=innerList.get(17) %></div>
						</div>
						</div>
						<div id ="row3" style="float: left;padding-top: 3%;width: 100%;">
						<div id="divDocuments" style="float: left;">
							<div class="divh">Documents</div>
							<div>
								<div ><span class="anaAttrib1"><%=uF.parseToInt(innerList.get(16)) > 0 ? innerList.get(16) : "0"%></span> docs</div>
		                    	<div>
		                    		<span style="float: left;">all docs </span>
									<input type="hidden" name="proDocsSpanStatus" id="proDocsSpanStatus<%=innerList.get(0) %>" value = "0"/>
									<%-- <a href="javascript:void(0)" onclick="viewDocuments('<%=innerList.get(0) %>', '<%=innerList.get(18) %>', '<%=proType %>')">
										<span id="proDocsDownarrowSpan<%=innerList.get(0) %>" style="float: left; margin-left: 2px;"> 
											<i class="fa fa-chevron-down" style="padding: 0px;"></i>
										</span>
										<span id="proDocsUparrowSpan<%=innerList.get(0) %>" style="float: left; margin-left: 2px; display: none;">
											<i class="fa fa-chevron-up" style="padding: 0px;"></i>
										</span>
									</a> --%>
									
									
									<!-- <a href="javascript:void(0)" onclick="return hs.htmlExpand(this);"> View</a> -->
									<a href="javascript:void(0)" onclick="viewAndAddNewDocuments('<%=innerList.get(0) %>', '<%=innerList.get(18) %>', '<%=innerList.get(3) %>', '<%=proType %>', 'MyProject');"> View</a>
									<%-- <div class="highslide-maincontent" style="padding-top: 0px;">
										<h5>Documents of <%=innerList.get(3) %></h5>
										<s:action name="ProjectDocuments" executeResult="true">
											<s:param name="proId"><%=innerList.get(0) %></s:param>
											<s:param name="taskId"><%=innerList.get(18) %></s:param>
											<s:param name="proType"><%=proType %></s:param>
											<s:param name="fromPage">MyProject</s:param>
										</s:action>
									</div> --%>
		                    	</div>
							</div>
						</div>
						<div id="divActivityOrAction" style="padding-left: 45% !important;">
							<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equals("C") || proType.equals("L") ) { %>
								<div class="divh">Activity & History</div>
							<% } %>
							<% if(proType != null && (proType.equals("TR") || (proType.equals("MR") && uF.parseToBoolean(innerList.get(13)) ) ) ) { %>
								<div class="divh">Action</div>
							<% } %>
							<div>
								<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equals("C") || proType.equals("L")) { %>
		                          <% if(proType == null || proType.equals("") || proType.equals("null") || proType.equals("L")) { %>
				                    <div>
					                    <select name="actions<%=innerList.get(0) %>" id="actions<%=innerList.get(0) %>" style="width: 100px !important;" onchange="executeTaskActivities(this.value, '<%=innerList.get(0) %>', '<%=innerList.get(18) %>', '<%=innerList.get(19) %>', '<%=activityRunningFlag %>', '<%=timeApproveFlag %>', '0', '<%=innerList.get(12) %>', '<%=proType %>');">
					                    	<option value="">Activities</option>
					                    	<% if(uF.parseToInt(innerList.get(19)) == 0) { %>
						                    	<option value="1">Start Task</option>
						                    	<option value="2">Add Time </option>
					                    	<% } else { %>
					                    		<option value="3">End Task </option>
					                    	<% } %>
					                    	<% if(uF.parseToInt(innerList.get(18)) == 0) { %>
					                    		<option value="4">Complete</option>
					                    	<% } %>
					                    </select>
				                    </div>
				                   <% } %>
				                   
				                   <div>
		                    		<span style="float: left;padding-right: 5%;">History</span>
									<input type="hidden" name="taskActHistoryStatus" id="taskActHistoryStatus<%=innerList.get(0) %>" value = "0"/>
									<%-- <a href="javascript:void(0)" onclick="viewTaskActHistory('<%=innerList.get(0) %>', '<%=innerList.get(18) %>', '<%=proType %>')">
										<span id="taskActHistDownarrowSpan<%=innerList.get(0) %>" style="float: left; margin-left: 2px;"> 
											<i class="fa fa-chevron-down" style="padding: 0px;"></i>
										</span>
										<span id="taskActHistUparrowSpan<%=innerList.get(0) %>" style="float: left; margin-left: 2px; display: none;">
											<i class="fa fa-chevron-up" style="padding: 0px;"></i>
										</span>
									</a> --%>
									
									<%-- <ul>
										<li class="dropdown">
											<a href="#" class="dropdown-toggle faa-parent animated-hover" data-toggle="dropdown" class="grow">
												View
											</a>	
											<ul class="dropdown-menu">
												<li class="header"><h4>Timesheet history of <%=innerList.get(3) %></h4></li>
												<li>
													<ul class="menu grow" style="width: 100%; height: 200px;">
														<s:action name="EmpTaskActivityHistory" executeResult="true">
															<s:param name="proId"><%=innerList.get(0) %></s:param>
															<s:param name="taskId"><%=innerList.get(18) %></s:param>
															<s:param name="proType"><%=proType %></s:param>
														</s:action>
													</ul>
												</li>
											</ul>
										</li>
									</ul> --%>
									
									<a href="javascript:void(0)" onclick="return hs.htmlExpand(this);"> View</a>
									<div class="highslide-maincontent">
										<h5>Timesheet history of <%=innerList.get(3) %></h5>
										<s:action name="EmpTaskActivityHistory" executeResult="true">
											<s:param name="proId"><%=innerList.get(0) %></s:param>
											<s:param name="taskId"><%=innerList.get(18) %></s:param>
											<s:param name="proType"><%=proType %></s:param>
										</s:action>
									</div>
		                    	</div>
	                          <% } %>
	                          
	                          <% if(proType != null && (proType.equals("TR") || (proType.equals("MR") && uF.parseToBoolean(innerList.get(13)) ) ) ) { %>
	                          	<% if(proType != null && proType.equals("TR")) { %>
				                    <select name="newTRactions<%=innerList.get(0) %>" id="newTRactions<%=innerList.get(0) %>" style="width: 100px !important;" onchange="executeNewTaskActions(this.value, '<%=innerList.get(0) %>', '<%=sTTType %>', '<%=proType %>');">
				                    	<option value="">Action</option>
				                    	<option value="1">Accept</option>
				                    	<option value="2">Reschedule</option>
				                    	<option value="3">Reassign</option> 
				                    </select>
				                <% } %>
				                <% if(proType != null && proType.equals("MR")) { %>
			                    	<% if(uF.parseToBoolean(innerList.get(13))) { %>
				                    <select name="action<%=innerList.get(0) %>" id="action<%=innerList.get(0) %>" style="width: 100px !important;" onchange="executeTaskAction(this.value, '<%=innerList.get(0) %>', '<%=innerList.get(18) %>', '<%=innerList.get(19) %>', '<%=activityRunningFlag %>', '<%=timeApproveFlag %>', '0', '<%=innerList.get(12) %>', '<%=proType %>');">
				                    	<option value="">Action</option>
				                    	<option value="5">Delete</option>
				                    	<% if(uF.parseToInt(innerList.get(37)) == 0 || uF.parseToInt(innerList.get(37)) == 1) { %>
				                    		<option value="6">Edit</option>
				                    	<% } %>
				                    </select>
				                    <% } %>
				                <% } %>
	                          <% } %>
							</div>
						</div>
						</div>
						<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equals("L")) { %>
							<% if(innerList.get(35) != null && !innerList.get(35).equals("O")) { %>
							<div id="row4" style="float: left;padding-top: 5%;width: 100%;">
							<div id="divTimesheet">
								<div class="divh">Timesheet</div>
								<div>
									<div>
				                    	Submit in <%=innerList.get(36) %> days
			                    	</div>
			                    	<div><a href="AddProjectActivity1.action">Submit</a></div>
								</div>
							</div>
							</div>
							<% } %>
						<% } %>
						</div>
						<%-- closing content div --%>
	                    <%-- <table class="table no-margin">
	                      <thead>
	                        <tr>
	                          <th>Status</th>
	                          <th>Priority</th>
	                          <th>Deadline</th>
	                          <th nowrap="nowrap">Est. Man <%=innerList.get(17) %></th>
	                          <th>Docs</th>
	                          <% if(proType == null || proType.equals("") || proType.equals("null") || proType.equals("C") || proType.equals("L") ) { %>
	                          <th>Activity</th>
	                          <% } %>
	                          <% if(proType == null || proType.equals("") || proType.equals("null") || proType.equals("L")) { %>
		                           <% if(innerList.get(35) != null && !innerList.get(35).equals("O")) { %>
		                          	<th>Timesheet</th>
		                          <% } %>
	                          <% } %>
	                          <% if(proType != null && (proType.equals("TR") || (proType.equals("MR") && uF.parseToBoolean(innerList.get(13)) ) ) ) { %>
	                          	<th>Action</th>
	                          <% } %>
	                        </tr>
	                      </thead>
	                      <tbody>
	                        <tr>
	                          <td>
	                          	<% if(proType != null && proType.equals("MR")) { %>
									<div style="float: left; width: 120px;">
				                    	<% if(uF.parseToInt(innerList.get(37)) ==0) { %>
				                    	<span style="float: left; width: 100%; background-image: url(&quot;images1/icons/new2.gif&quot;); background-repeat: no-repeat; background-position: right top;">
				                    		Request for Task sent, awaiting response
				                    	</span>	
				                    	<% } else if(uF.parseToInt(innerList.get(37)) == -1) { %>
				                    	<span style="float: left; width: 100%;">
				                    		Your request has been denied
				                    	</span>
				                    	<% } else if(uF.parseToInt(innerList.get(37)) ==2) { %>
				                    	<span style="float: left; width: 100%; background-image: url(&quot;images1/icons/new2.gif&quot;); background-repeat: no-repeat; background-position: right top;">
				                    		Requested for reschedule, awaiting response
				                    	</span>
				                    	<% } else if(uF.parseToInt(innerList.get(37)) ==3) { %>
				                    	<span style="float: left; width: 100%; background-image: url(&quot;images1/icons/new2.gif&quot;); background-repeat: no-repeat; background-position: right top;">
				                    		Requested for re-assign, awaiting response
				                    	</span>
				                    	<% } %>
										<span style="float: left; width: 100%; margin-top: -10px;"><%=innerList.get(38) %></span>
									</div>
			                    <% } else if(proType != null && proType.equals("TR")) { %>
									<div style="float: left; width: 100%;">
			                    		<span style="float: left; width: 100%; background-image: url(&quot;images1/icons/new2.gif&quot;); background-repeat: no-repeat; background-position: center;">&nbsp;</span>
			                    	</div>
								<% } else { %>
									<div style="float: left; width: 120px;">
				                    	<span style="float: left; width: 100%;"><%=innerList.get(1) %></span>
										<span style="float: left; width: 100%;"><%=innerList.get(2) %> <%=innerList.get(8) %> <%=innerList.get(17) %></span>
									</div>
			                	<% } %>
	                		</td>
                          
                          <td><%=innerList.get(4) %></td>
                          
                          <td>
                          	<div style="width: 100%;"><%=innerList.get(6) %></div>
                               <div style="width: 100px;">
			                    	<div class="anaAttrib1" style="margin-bottom: -5px;"><span id="TcompletePercent_<%=innerList.get(18) %>_<%=innerList.get(0)%>"><%=uF.formatIntoOneDecimalWithOutComma(uF.parseToDouble(innerList.get(9))) %>%</span>
			                    	<% if(proType ==null || proType.equals("null") || proType.equals("") || proType.equals("L")) { %>
			                    		<a href="javascript:void(0)" onclick="updateStatus(<%=innerList.get(0) %>,<%=innerList.get(18) %>, 'Tcomplete')"><img src="images1/icons/popup_arrow.gif" title="Update Completion Status" height="8px"/></a>
			                    	<% } %>
			                    	</div>
									<div id="Tcomplete_<%=innerList.get(18) %>_<%=innerList.get(0) %>" class="outbox">
										<div class="greenbox" style="width: <%=uF.formatIntoOneDecimalWithOutComma(uF.parseToDouble(innerList.get(9))) %>%;"></div>
									</div>
								</div>
							</td>
							
	                          <td><span class="anaAttrib1"><%=innerList.get(7) %></span></td>
	                          
	                          <td>
	                          	<div style="float: left; width: 100%;"><span class="anaAttrib1"><%=uF.parseToInt(innerList.get(16)) > 0 ? innerList.get(16) : "0"%></span> docs</div>
		                    	<div style="float: left; width: 100%;">
		                    		<span style="float: left;">all docs</span>
									<input type="hidden" name="proDocsSpanStatus" id="proDocsSpanStatus<%=innerList.get(0) %>" value = "0"/>
									<a href="javascript:void(0)" onclick="viewDocuments('<%=innerList.get(0) %>', '<%=innerList.get(18) %>', '<%=proType %>')">
										<span id="proDocsDownarrowSpan<%=innerList.get(0) %>" style="float: left; margin-left: 2px;"> 
											<i class="fa fa-chevron-down" style="padding: 0px;"></i>
										</span>
										<span id="proDocsUparrowSpan<%=innerList.get(0) %>" style="float: left; margin-left: 2px; display: none;">
											<i class="fa fa-chevron-up" style="padding: 0px;"></i>
										</span>
									</a>
		                    	</div>
	                          </td>
	                          <% if(proType == null || proType.equals("") || proType.equals("null") || proType.equals("C") || proType.equals("L")) { %>
		                          <td>
			                          <% if(proType == null || proType.equals("") || proType.equals("null") || proType.equals("L")) { %>
					                    <div style="float: left; width: 100%;">
						                    <select name="actions<%=innerList.get(0) %>" id="actions<%=innerList.get(0) %>" style="width: 100px !important;" onchange="executeTaskActivities(this.value, '<%=innerList.get(0) %>', '<%=innerList.get(18) %>', '<%=innerList.get(19) %>', '<%=activityRunningFlag %>', '<%=timeApproveFlag %>', '0', '<%=innerList.get(12) %>', '<%=proType %>');">
						                    	<option value="">Activities</option>
						                    	<% if(uF.parseToInt(innerList.get(19)) == 0) { %>
							                    	<option value="1">Start Task</option>
							                    	<option value="2">Add Time </option>
						                    	<% } else { %>
						                    		<option value="3">End Task </option>
						                    	<% } %>
						                    	<% if(uF.parseToInt(innerList.get(18)) == 0) { %>
						                    		<option value="4">Complete</option>
						                    	<% } %>
						                    </select>
					                    </div>
					                   <% } %>
					                   
					                   <div style="float: left; width: 100%;">
			                    		<span style="float: left;">History</span>
										<input type="hidden" name="taskActHistoryStatus" id="taskActHistoryStatus<%=innerList.get(0) %>" value = "0"/>
										<a href="javascript:void(0)" onclick="viewTaskActHistory('<%=innerList.get(0) %>', '<%=innerList.get(18) %>', '<%=proType %>')">
											<span id="taskActHistDownarrowSpan<%=innerList.get(0) %>" style="float: left; margin-left: 2px;"> 
												<i class="fa fa-chevron-down" style="padding: 0px;"></i>
											</span>
											<span id="taskActHistUparrowSpan<%=innerList.get(0) %>" style="float: left; margin-left: 2px; display: none;">
												<i class="fa fa-chevron-up" style="padding: 0px;"></i>
											</span>
										</a>
			                    	</div>
		                          </td>
	                          <% } %>
	                          
	                          
	                          <% if(proType == null || proType.equals("") || proType.equals("null") || proType.equals("L")) { %>
	                          <% if(innerList.get(35) != null && !innerList.get(35).equals("O")) { %>
		                          <td>	
			                    	<div style="float: left; width: 100%;">
				                    	Submit in <%=innerList.get(36) %> days
			                    	</div>
			                    	<div style="float: left; width: 100%;"><a href="AddProjectActivity1.action">Submit</a></div>
								  </td>
			                    <% } %>	
			                  <% } %>
	                          
	                          <% if(proType != null && (proType.equals("TR") || (proType.equals("MR") && uF.parseToBoolean(innerList.get(13)) ) ) ) { %>
	                          <td>
	                          	<% if(proType != null && proType.equals("TR")) { %>
				                    <select name="newTRactions<%=innerList.get(0) %>" id="newTRactions<%=innerList.get(0) %>" style="width: 100px !important;" onchange="executeNewTaskActions(this.value, '<%=innerList.get(0) %>', '<%=sTTType %>', '<%=proType %>');">
				                    	<option value="">Action</option>
				                    	<option value="1">Accept</option>
				                    	<option value="2">Reschedule</option>
				                    	<option value="3">Reassign</option> 
				                    </select>
				                <% } %>
				                <% if(proType != null && proType.equals("MR")) { %>
			                    	<% if(uF.parseToBoolean(innerList.get(13))) { %>
				                    <select name="action<%=innerList.get(0) %>" id="action<%=innerList.get(0) %>" style="width: 100px !important;" onchange="executeTaskAction(this.value, '<%=innerList.get(0) %>', '<%=innerList.get(18) %>', '<%=innerList.get(19) %>', '<%=activityRunningFlag %>', '<%=timeApproveFlag %>', '0', '<%=innerList.get(12) %>', '<%=proType %>');">
				                    	<option value="">Action</option>
				                    	<option value="5">Delete</option>
				                    	<% if(uF.parseToInt(innerList.get(37)) == 0 || uF.parseToInt(innerList.get(37)) == 1) { %>
				                    		<option value="6">Edit</option>
				                    	<% } %>
				                    </select>
				                    <% } %>
				                <% } %>
	                          </td>
	                          <% } %>
	                        </tr>
	                      </tbody>
	                    </table> --%>
	                    
	                    
	                  <!-- </div> --><!-- /.table-responsive -->
	                </div><!-- /.box-body -->
	              </div><!-- /.box -->
            	</div>
            	
            	
            	<div id="taskAllEventDiv" class="col-lg-12 col-md-12 col-sm-12" style="display: none;">
		           	<div class="box box-body">
		            	<div id="proDocsDiv_<%=innerList.get(0) %>" style="display: none;"></div>
						<div id="taskActHistory_<%=innerList.get(0) %>" style="display: none;"></div>
						
						
						<div class="box-body table-responsive no-padding" id="rescheduleTaskSubTaskDiv_<%=innerList.get(0) %>" style="float: left; width: 100%; display: none;">
							<s:form action="EmpViewProject" name="frm_RTEmpViewProject" id="frm_RTEmpViewProject" method="post" theme="simple">
				            <input type="hidden" name="proId" id="proId" value="<%=innerList.get(18) %>" />
				            <input type="hidden" name="proType" id="proType" value="<%=proType %>" />
				            <input type="hidden" name="taskId" id="taskId" value="<%=innerList.get(0) %>" />
				            <input type="hidden" name="parentTaskId" id="parentTaskId_<%=innerList.get(0) %>" value="<%=innerList.get(21) %>" />
				            <s:hidden name="singleTaskData"></s:hidden>
							<s:hidden name="singleTaskId"></s:hidden>
							<s:hidden name="proPage"></s:hidden>
							<s:hidden name="minLimit"></s:hidden>
							<script type="text/javascript">
								jQuery(document).ready(function() {
									$(function() {
										
										$("#rDeadline"+<%=innerList.get(0) %>).datepicker({
											format : 'dd/mm/yyyy', 
											onClose: function(selectedDate){
												$("#rStartDate"+<%=innerList.get(0) %>).datepicker("option", "maxDate", selectedDate);
											}
										});
										
										$("#rStartDate"+<%=innerList.get(0) %>).datepicker({
											format : 'dd/mm/yyyy', 
											onClose: function(selectedDate){
												$("#rDeadline"+<%=innerList.get(0) %>).datepicker("option", "minDate", selectedDate);
											}
										});
									});
								});
							</script>
								<table class="table table-hover" id="RescheduleTaskTable_<%=innerList.get(0) %>">
									<tr>
										<td align="right">Start Date*: </td>
										<td><input type="text" name="rStartDate" id="rStartDate<%=innerList.get(0) %>" style="width: 85px !important;" class="validateRequired" required/> </td>
										<td align="right">Deadline*: </td>
										<td><input type="text" name="rDeadline" id="rDeadline<%=innerList.get(0) %>" style="width: 85px !important;" class="validateRequired" required/> </td>
									</tr>
									<tr>
										<td align="right" valign="top">Comment*: </td>
										<td  colspan="3"><textarea rows="3" cols="50" name="taskrescheduleComment" id="taskrescheduleComment<%=innerList.get(0) %>" required></textarea></td>
									</tr>
								</table>
									<div style="text-align: center;">
										<s:submit name="addTask" cssClass="btn btn-primary" cssStyle="margin-right: 5px; padding: 3px;" value="Reschedule"/>
										<input type="button" name="cancelTask" class="btn btn-danger" style="margin-right: 5px; padding: 3px;" value="Cancel" onclick="hideRescheduleTaskSubTaskDiv('<%=innerList.get(0) %>');"/>
									</div>
							</s:form>
						</div>
						
						<div class="box-body table-responsive no-padding" id="reassignTaskSubTaskDiv_<%=innerList.get(0) %>" style="float: left; width: 100%; display: none;">
							<s:form action="EmpViewProject" name="frm_RTEmpViewProject" id="frm_RTEmpViewProject" method="post" theme="simple">
				            <input type="hidden" name="proId" id="proId" value="<%=innerList.get(18) %>" />
				            <input type="hidden" name="proType" id="proType" value="<%=proType %>" />
				            <input type="hidden" name="taskId" id="taskId" value="<%=innerList.get(0) %>" />
				            <input type="hidden" name="parentTaskId" id="parentTaskId_<%=innerList.get(0) %>" value="<%=innerList.get(21) %>" />
				            <s:hidden name="singleTaskData"></s:hidden>
							<s:hidden name="singleTaskId"></s:hidden>
							<s:hidden name="proPage"></s:hidden>
							<s:hidden name="minLimit"></s:hidden>
								<table class="table table-hover" id="ReassignTaskTable_<%=innerList.get(0) %>">
									<tr>
										<td align="right" valign="top">Comment*: </td>
										<td><textarea rows="3" cols="50" name="taskreassignComment" id="taskreassignComment<%=innerList.get(0) %>" required></textarea></td>
									</tr>
								</table>
									<div style="text-align: center;">
										<s:submit name="addTask" cssClass="btn btn-primary" cssStyle="margin-right: 5px; padding: 3px;" value="Reassign"/>
										<input type="button" name="cancelTask" class="btn btn-danger" style="margin-right: 5px; padding: 3px;" value="Cancel" onclick="hideReassignTaskSubTaskDiv('<%=innerList.get(0) %>');"/>
									</div>
							</s:form>
						</div>
			
			
						<div class="box-body table-responsive no-padding" id="editTaskSubtaskDiv_<%=innerList.get(0) %>" style="float: left; width: 100%; display: none;">
							<s:form action="EmpViewProject" name="frm_EditEmpViewProject_<%=innerList.get(0) %>" id="frm_EditEmpViewProject_<%=innerList.get(0) %>" method="post" onsubmit="return checkTimeFilledEmpOfAllTasks('<%=innerList.get(0) %>')" theme="simple">
				            <input type="hidden" name="proId" id="proId" value="<%=innerList.get(18) %>" />
				            <input type="hidden" name="proType" id="proType" value="<%=proType %>" />
				            <input type="hidden" name="taskId" id="taskId" value="<%=innerList.get(0) %>" />
				            <input type="hidden" name="parentTaskId" id="parentTaskId_<%=innerList.get(0) %>" value="<%=innerList.get(21) %>" />
							<s:hidden name="singleTaskData"></s:hidden>
							<s:hidden name="singleTaskId"></s:hidden>
							<s:hidden name="proPage"></s:hidden>
							<s:hidden name="minLimit"></s:hidden>
								<table class="table table-hover" id="editTaskTable_<%=innerList.get(0) %>">
									<tr>
										<th>
										<% 
										String sttType = "Task";
										if(uF.parseToInt(innerList.get(21))>0) { 
											sttType = "Sub-task";
										%>Sub <% } %>
										Task Name<sup>*</sup> </th>
										<th>Dependency & Dependency Type</th>
										<th>Priority</th>
										<th>Start Date<sup>*</sup></th>
										<th>Deadline<sup>*</sup></th>
										<th><%="Est. man-"+innerList.get(17) %><sup>*</sup></th>
									</tr>
									
									<tr id="task_TR<%=innerList.get(0) %>">
									<td valign="top">
									<input type="hidden" name="tstFilledEmp<%=innerList.get(0) %>" id="tstFilledEmp<%=innerList.get(0) %>_<%=0 %>" value="<%=innerList.get(24) %>" /> 
									<input type="hidden" name="taskDescription<%=innerList.get(0) %>" id="taskDescription<%=innerList.get(0) %>_<%=0 %>" value="<%=innerList.get(24) %>"/>
									<input type="text" name="taskname<%=innerList.get(0) %>" id="taskname<%=innerList.get(0) %>" value="<%=innerList.get(3) %>" class="validateRequired" style="width: 220px !important;"/>
										<div id="addTaskSpan<%=innerList.get(0) %>" style="display: block;">
											<input type="hidden" name="taskID<%=innerList.get(0) %>" id="taskID<%=innerList.get(0) %>" value="<%=innerList.get(0)%>" />
										</div>
									<div style="margin-bottom: -3px; font-style: italic; color: gray;"><%=innerList.get(25) %> </div>
									<div><a href="javascript:void(0)" onclick="updateTaskDescription1(<%=innerList.get(0) %>, '<%=0 %>', 'taskDescription', '<%=sTTType %>', 'U')">D</a>
										<% 
										String strChecked = "";
											if(uF.parseToInt(innerList.get(26)) == 1) { 
												strChecked = "checked";
											}
										%>
										<% if(innerList.get(35) != null && !innerList.get(35).equals("O")) { %>
											&nbsp;<input type="checkbox" name="recurringTask<%=innerList.get(0) %>" id="recurringTask<%=innerList.get(0) %>" <%=strChecked %> onclick="setValue('isRecurringTask<%=innerList.get(0) %>');" title="Add task to recurring in next frequency"/>Recurr <%=sttType %>
										<% } %>	
											<input type="hidden" name="isRecurringTask<%=innerList.get(0) %>" id="isRecurringTask<%=innerList.get(0) %>" value="<%=innerList.get(26) %>"/>
									</div>
									<div style="margin-top: -5px; margin-bottom: -10px; font-style: italic; color: gray;">assigned by: <%=innerList.get(15) %> </div>	 
									</td>
			
									<td valign="top">
										<select name="dependency<%=innerList.get(0) %>" id="dependency<%=innerList.get(0) %>" style="width: 135px !important;">
											<option value="">Select Dependency</option>
											<%=innerList.get(27)%>
										</select> <br/>
										<select name="dependencyType<%=innerList.get(0) %>" id="dependencyType<%=innerList.get(0) %>" style="width: 135px !important; margin-top: 7px;" onchange="setDependencyPeriod(this.value, '<%=innerList.get(0) %>', '0', 'Task');">
											<option value="">Select Dependency Type</option>
											<option value="0"
												<%if(innerList.get(28) != null && innerList.get(28).equals("0")) { %>
												selected <% } %>>Start-Start</option>
											<option value="1"
												<%if(innerList.get(28) != null && innerList.get(28).equals("1")) { %>
												selected <% } %>>Finish-Start</option>
										</select>
									</td>
			
									<td valign="top"><select name="priority<%=innerList.get(0) %>" id="priority<%=innerList.get(0) %>" style="width:85px !important;" class="validateRequired">
											<% for(GetPriorityList getPriorityList:priorityList) { %>
											<option value="<%=getPriorityList.getPriId() %>"
												<%if(innerList.get(29) != null && getPriorityList.getPriId().equals(innerList.get(29))) { %>
												selected <%} %>>
												<%=getPriorityList.getProName() %></option>
											<% } %>
									</select></td>
									<td valign="top"><input type="text" id="startDate<%=innerList.get(0) %>" name="startDate<%=innerList.get(0) %>" style="width: 85px !important;" class="validateRequired" value="<%=innerList.get(31)%>">
									</td>
									<td valign="top">
										<input type="text" id="deadline1<%=innerList.get(0) %>" name="deadline1<%=innerList.get(0) %>" value="<%=innerList.get(32)%>" class="validateRequired" style="width: 85px !important;">
									</td>
									<td valign="top">
										<input type="text" name="idealTime<%=innerList.get(0) %>" id="idealTime<%=innerList.get(0) %>" onkeypress="return isNumberKey(event)" value="<%=innerList.get(33)%>" class="validateRequired" style="width: 45px !important; text-align: right;">
										<input type="hidden" name="colourCode<%=innerList.get(0) %>" value="<%=innerList.get(34) %>" id="colourCode<%=innerList.get(0) %>"/>
									</td>
								</tr>
								<script type="text/javascript">
									$("#deadline1"+<%=innerList.get(0) %>).datepicker({
										format : 'dd/mm/yyyy', minDate:"<%=(String)innerList.get(22) %>", maxDate: "<%=(String)innerList.get(23) %>", 
										onClose: function(selectedDate){
											$("#startDate"+<%=innerList.get(0) %>).datepicker("option", "maxDate", selectedDate);
										}
									});
									
									$("#startDate"+<%=innerList.get(0) %>).datepicker({
										format : 'dd/mm/yyyy', minDate:"<%=(String)innerList.get(22) %>", maxDate: "<%=(String)innerList.get(23) %>", 
										onClose: function(selectedDate){
											$("#deadline1"+<%=innerList.get(0) %>).datepicker("option", "minDate", selectedDate);
										}
									});
								</script>
								</table>
									<div id="editBtn_<%=innerList.get(0) %>" style="text-align: center;">
										<s:submit name="addTask" cssClass="btn btn-primary" cssStyle="margin-right: 5px; padding: 3px;" value="Update"/>
										<input type="button" name="cancelUpdateTask" class="btn btn-danger" style="margin-right: 5px; padding: 3px;" value="Cancel" onclick="hideUpdateTaskSubTaskDiv('<%=innerList.get(0) %>');"/>
									</div>
							</s:form>
						</div>
						
					</div>
				</div>
            <% } %>
			</div>



	<script>
	$(document).ready(function(){
		$("input[name='startDate']").on('keydown', function() {
		    return false;
		});
		
		$("input[name='deadline1']").on('keydown', function() {
		    return false;
		});
		$("input[name='substartDate']").on('keydown', function() {
		    return false;
		});
		
		$("input[name='subdeadline1']").on('keydown', function() {
		    return false;
		});
	});
	</script>
	
	<%-- <script src="js/data-table/bootstrap-table.js"></script>
    <script src="js/data-table/tableExport.js"></script>
    <script src="js/data-table/data-table-active.js"></script>
    <script src="js/data-table/bootstrap-table-editable.js"></script>
    <script src="js/data-table/bootstrap-editable.js"></script>
    <script src="js/data-table/bootstrap-table-resizable.js"></script>
    <script src="js/data-table/colResizable-1.5.source.js"></script>
    <script src="js/data-table/bootstrap-table-export.js"></script> --%>