<%@page import="com.konnect.jpms.task.GetPriorityList"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.itextpdf.text.Utilities"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ page buffer="16kb"%>
<%@ page import="java.util.*"%>
<%
	int proid = (Integer) request.getAttribute("pro_id");
%>

<style>
 
.greenbox {
	height: 11px;
	background-color:#00FF00; /* the critical component */
}

#redbox {
	height: 11px;
	background-color:#FF0000; /* the critical component */
}

#yellowbox {
	height: 11px;
	background-color:#FFFF00; /* the critical component */
}

.outbox {
	height: 11px;
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
</style>

<script type="text/javascript">

hs.graphicsDir = '<%=request.getContextPath()%>/images1/highslide/graphics/';
hs.outlineType = 'rounded-white';
hs.wrapperClassName = 'draggable-header';

</script>


<script language="javascript" type="text/javascript">   
         
var cp = new ColorPicker('window'); 
var cp2 = new ColorPicker('window');

	function start123(id, pid) {
		var proid = document.getElementsByName("pro_id");
		var url = 'TaskUpdateTime.action?type=start&id=' + id;
		url += '&pro_id=' + pid;
		window.location = url;
	}
	
	
function endTask(tid, pid, taskId) {
		
		var dialogEdit = '#endTask';
		var url1='EndTaskPopup.action?id='+tid+'&taskId='+taskId;
		url1+='&pro_id='+pid+'&fromPage=MyProject';
		
		$(dialogEdit).dialog({
			autoOpen : false,
			bgiframe : true,
			resizable : false,
			height : 'auto',
			width : 'auto',
			modal : true,
			title : 'End Task',
			open : function() {
				var xhr = $.ajax({
					url : url1,
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
	
	
	function SendProId(id) {
		if(id!=null) {
			var proType = document.getElementById("proType").value;
			window.location ='EmpViewProject.action?pro_id='+id+'&proType='+proType;
		}
	}
	
	
	function startManually(tid, pid) {
		
		var dialogEdit = '#startManually';
		var url1='StartTaskManually.action?id='+tid;
		url1+='&pro_id='+pid;
		
		$(dialogEdit).dialog({
			autoOpen : false,
			bgiframe : true,
			resizable : false,
			height : 'auto',
			width : 'auto',
			modal : true,
			title : 'Start Manually',
			open : function() {
				var xhr = $.ajax({
					url : url1,
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
	
	
	/* function viewDocuments(id) {
		var dialogEdit = '#viewdocuments';
		
		$(dialogEdit).dialog({
			autoOpen : false,
			bgiframe : true,
			resizable : false,
			height : 500,
			width : '85%',
			modal : true,
			title : 'Project Documents',
			open : function() {
				var xhr = $.ajax({
					url : "ProjectDocumentView.action?pro_id="+id+"&type=MyProject",
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
	} */
	
	
	function viewSummary(id) {

		var dialogEdit = '#viewsummary';
		$(dialogEdit).dialog({
			autoOpen : false,
			bgiframe : true,
			resizable : false,
			height : 1120,
			width : 1200,
			modal : true,
			title : 'Project Summary',
			open : function() {
				var xhr = $.ajax({
					url : "ProjectSummaryView.action?pro_id="+id,
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
	
	
/* 	var dialogEdit = '#updateStatus';
	function updateStatus(taskId, proId) {
		removeLoadingDiv('the_div');
//		var dialogEdit = '#updateStatus'; 
		dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
		$(dialogEdit).dialog({
			autoOpen : false,
			bgiframe : true,
			resizable : false, 
			height : 250,
			width : 400,    
			modal : true,
			title : 'Update Task Status',
			open : function() {
				var xhr = $.ajax({
					url : "UpdateTaskPercentage.action?taskId="+taskId+'&proId='+proId,
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
	
	
	function saveStatus(taskId, percent, divId) {
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
	                url : 'UpdateTaskPercentage.action?ID='+taskId+'&percent='+percent,
	                cache : false,
	                success : function(data) {
	                	$(divId).html(data);
	                }
	            });
	    }
		$(dialogEdit).dialog('close');
	} */
	
	
jQuery(document).ready(function() {
		
		//	jQuery("#frmClockEntries").validationEngine();
			
		  /* jQuery(".content1").hide();  */
		  jQuery(".content1").show();
		  //toggle the componenet with class msg_body
		  jQuery(".heading").click(function()
		  {
		    jQuery(this).next(".content1").slideToggle(500);
			$(this).toggleClass("heading_dash"); 
		  });
		  
		
		  jQuery(".content2").hide();
		  //toggle the componenet with class msg_body
		  jQuery(".heading_dash").click(function()
		  {
		    jQuery(this).next(".content2").slideToggle(500);
			$(this).toggleClass("close_div"); 
		  });
		});



function addActivity(id) {
	removeLoadingDiv('the_div');
	var dialogEdit = '#addtasksubtask';
	dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
	$(dialogEdit).dialog({
		autoOpen : false,
		bgiframe : true,
		resizable : false,
		height : 650,
		width : 800,
		modal : true,
		title : 'Add New Task',
		open : function() {
			var xhr = $.ajax({
				url : "AddNewActivityPopup.action?pro_id=" + id +'&type=Task&fromPage=MP',
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



function editActivity(id, pid) {
	removeLoadingDiv('the_div');
	var dialogEdit = '#edittasksubtask';
	var url1='AddNewActivityPopup.action?operation=E&task_id='+ id +'&pro_id='+ pid +'&type=Task&fromPage=MP';
	//url1+='&pro_id='+pid;
	dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
	$(dialogEdit).dialog({
		autoOpen : false,
		bgiframe : true,
		resizable : false,
		height : 700,
		width : '70%',
		modal : true,
		title : 'Edit Task',
		open : function() {
			var xhr = $.ajax({
				url : url1,
				cache : false ,
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


function addSubTask(proId, taskId) {
	removeLoadingDiv('the_div');
	var dialogEdit = '#addsubtask';
	dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
	$(dialogEdit).dialog({
		autoOpen : false,
		bgiframe : true,
		resizable : false,
		height : 650,
		width : 800,
		modal : true,
		title : 'Add New Sub Task',
		open : function() {
			var xhr = $.ajax({
				url : 'AddNewActivityPopup.action?pro_id='+ proId +'&task_id='+ taskId +'&type=SubTask&fromPage=MP',
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



function editSubTask(proId, taskId, subTaskId) {
	removeLoadingDiv('the_div');
	var dialogEdit = '#editsubtask';
	var url1='AddNewActivityPopup.action?operation=E&task_id='+ taskId +'&pro_id='+ proId +'&sub_task_id='+ subTaskId +'&type=SubTask&fromPage=MP';
	//url1+='&pro_id='+pid;
	dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
	$(dialogEdit).dialog({
		autoOpen : false,
		bgiframe : true,
		resizable : false,
		height : 700,
		width : '70%',
		modal : true,
		title : 'Edit Sub Task',
		open : function() {
			var xhr = $.ajax({
				url : url1,
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


function executeTaskActivities(val, taskId, proId, activityId, activityRunningFlag, timeApproveFlag, stCnt, workedTime) {
	//alert("timeApproveFlag ==>> "+ timeApproveFlag +"  == activityRunningFlag ===>> " + activityRunningFlag);
	document.getElementById('editTaskSubtaskDiv_'+taskId).style.display = 'none';
	if(val == '1') {
		if(timeApproveFlag == 'true') {
			alert('Timesheet for this date is already approved.')
		} else {
			if(activityRunningFlag == 'true') {
				if(confirm('You are already working on one activity, would you like to finish that activity by starting the new activity?')) {
					start123(taskId, proId);
				}
			} else {
				start123(taskId, proId);
			}
		}
	} else if(val == '2') {
		if(timeApproveFlag == 'true') {
			alert('Timesheet for this date is already approved.');
		} else {
			if(activityRunningFlag == 'true') {
				if(confirm('You are already working on one activity, would you like to finish that activity by starting the new activity?')) {
					startManually(taskId, proId);
				}
			} else {
				startManually(taskId, proId);
			}
		}
	} else if(val == '3') {
		endTask(taskId, proId, activityId);
	}
	document.getElementById('actions'+taskId).selectedIndex = '0';
	
}


function executeTaskAction(val, taskId, proId, activityId, activityRunningFlag, timeApproveFlag, stCnt, workedTime) {
	//alert("timeApproveFlag ==>> "+ timeApproveFlag +"  == activityRunningFlag ===>> " + activityRunningFlag);
	document.getElementById('editTaskSubtaskDiv_'+taskId).style.display = 'none';
	if(val == '5') {
		if(parseFloat(workedTime)>0) {
			alert('You can not delete this task as user has already booked the time against this task.');
		} else {
			if(parseFloat(stCnt)>0) {
				alert('Please first delete sub task of this task then delete this task.');
			} else {
				var action = 'EmpViewProject.action?addTask=Delete&taskId='+taskId;
				window.location = action;
			}
		}
	} else if(val == '6') {
		
		document.getElementById('editTaskSubtaskDiv_'+taskId).style.display = 'block';

		/* deleteAddTaskAllRow(taskId);
		document.getElementById("taskTable_"+taskId).style.display = 'none';
		document.getElementById("addBtn_"+taskId).style.display = 'none'; */
		
		document.getElementById('proDocsSpanStatus'+taskId).value = '0';
		document.getElementById('proDocsDownarrowSpan'+taskId).style.display = 'block';
		document.getElementById('proDocsUparrowSpan'+taskId).style.display = 'none';
		
		document.getElementById('taskActHistoryStatus'+taskId).value = '0';
		document.getElementById('taskActHistDownarrowSpan'+taskId).style.display = 'block';
		document.getElementById('taskActHistUparrowSpan'+taskId).style.display = 'none';
		
		document.getElementById('taskActHistory_'+taskId).style.display = 'none';
		document.getElementById('proDocsDiv_'+taskId).style.display = 'none';
		//editActivity(taskId, proId);
	}
	document.getElementById('action'+taskId).selectedIndex = '0';
	
}


var dialogEdit = '#updateStatus';
function updateStatus(taskId, proId, divId) {
	removeLoadingDiv('the_div');
//	var dialogEdit = '#updateStatus'; 
	dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
	$(dialogEdit).dialog({
		autoOpen : false,
		bgiframe : true,
		resizable : false, 
		height : 250,
		width : 400,    
		modal : true,
		title : 'Update Task Status',
		open : function() {
			var xhr = $.ajax({
				url : "UpdateTaskPercentage.action?taskId="+taskId+'&proId='+proId+"&divId="+divId,
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


function saveStatus(taskId, percent, divId, proId) {
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
                url : 'UpdateTaskPercentage.action?ID='+taskId+'&percent='+percent,
                cache : false,
                success : function(data) {
                	var allData = data.split("::::");
                	document.getElementById(divId+'Percent_'+proId+'_'+taskId).innerHTML = allData[0] +'%';
                	document.getElementById(divId+'_'+proId+'_'+taskId).innerHTML = allData[1];
                }
            });
    }
	$(dialogEdit).dialog('close');
}


function viewDocuments(taskId, proId, proType) {
	//alert("taskId ===>> " + taskId);    
	var status = document.getElementById('proDocsSpanStatus'+taskId).value;
	//alert("status ===>> " + status);
	if(status == '0') {
		document.getElementById('proDocsSpanStatus'+taskId).value = '1';
		document.getElementById('proDocsDownarrowSpan'+taskId).style.display = 'none';
		document.getElementById('proDocsUparrowSpan'+taskId).style.display = 'block';
		
		document.getElementById('taskActHistoryStatus'+taskId).value = '0';
		document.getElementById('taskActHistDownarrowSpan'+taskId).style.display = 'block';
		document.getElementById('taskActHistUparrowSpan'+taskId).style.display = 'none';
		
		
		document.getElementById('editTaskSubtaskDiv_'+taskId).style.display = 'none';
		/* deleteAddTaskAllRow(taskId);
		document.getElementById('addBtn_'+taskId).style.display = 'none';
		document.getElementById('taskTable_'+taskId).style.display = 'none'; */
		document.getElementById('taskActHistory_'+taskId).style.display = 'none';
		document.getElementById('proDocsDiv_'+taskId).style.display = 'block';
    	$('<img src=\"images1/ajax-loading-1.gif\"/>').appendTo("#proDocsDiv_"+taskId);
    	
    	/* document.getElementById('taskSubTaskDiv_'+taskId).style.display = 'none'; */
		getContent('proDocsDiv_'+taskId, 'ProjectDocuments.action?proId='+ proId +'&taskId='+ taskId +'&proType='+ proType +'&fromPage=MyProject');
	} else {
		document.getElementById('proDocsSpanStatus'+taskId).value = '0';
		document.getElementById('proDocsDownarrowSpan'+taskId).style.display = 'block';
		document.getElementById('proDocsUparrowSpan'+taskId).style.display = 'none';
		
		document.getElementById('proDocsDiv_'+taskId).innerHTML = '';
		document.getElementById('proDocsDiv_'+taskId).style.display = 'none';
	}		
}



function viewTaskActHistory(taskId, proId, proType) {
	//alert("taskId ===>> " + taskId);    
	var status = document.getElementById('taskActHistoryStatus'+taskId).value;
	if(status == '0') {
		document.getElementById('proDocsSpanStatus'+taskId).value = '0';
		document.getElementById('proDocsDownarrowSpan'+taskId).style.display = 'block';
		document.getElementById('proDocsUparrowSpan'+taskId).style.display = 'none';
		
		document.getElementById('taskActHistoryStatus'+taskId).value = '1';
		document.getElementById('taskActHistDownarrowSpan'+taskId).style.display = 'none';
		document.getElementById('taskActHistUparrowSpan'+taskId).style.display = 'block';
		
		document.getElementById('editTaskSubtaskDiv_'+taskId).style.display = 'none';
		/* deleteAddTaskAllRow(taskId);
		document.getElementById('addBtn_'+taskId).style.display = 'none';
		document.getElementById('taskTable_'+taskId).style.display = 'none'; */
		document.getElementById('taskActHistory_'+taskId).style.display = 'block';
		document.getElementById('proDocsDiv_'+taskId).style.display = 'none';
    	$('<img src=\"images1/ajax-loading-1.gif\"/>').appendTo("#taskActHistory_"+taskId);
    	
    	/* document.getElementById('taskSubTaskDiv_'+taskId).style.display = 'none'; */
		getContent('taskActHistory_'+taskId, 'EmpTaskActivityHistory.action?proId='+ proId +'&taskId='+ taskId +'&proType='+ proType);
	} else {
		document.getElementById('taskActHistoryStatus'+taskId).value = '0';
		document.getElementById('taskActHistDownarrowSpan'+taskId).style.display = 'block';
		document.getElementById('taskActHistUparrowSpan'+taskId).style.display = 'none';
		
		document.getElementById('taskActHistory_'+taskId).innerHTML = '';
		document.getElementById('taskActHistory_'+taskId).style.display = 'none';
	}		
}

/* ************************************************************ Start Add Document Script ********************************************** */

function openCloseDocs(strProId, taskId, folderCnt) {
	//alert("strProId ===>> " + strProId);
	var status = document.getElementById("hideFolder"+strProId+"_"+taskId+"_"+folderCnt).value;
	if(status == '0') {
		document.getElementById("hideFolder"+strProId+"_"+taskId+"_"+folderCnt).value = '1';
		document.getElementById("folderFileTR_"+strProId+"_"+taskId+"_"+folderCnt).style.display = "table-row";
	} else {
		document.getElementById("hideFolder"+strProId+"_"+taskId+"_"+folderCnt).value = '0';
		document.getElementById("folderFileTR_"+strProId+"_"+taskId+"_"+folderCnt).style.display = "none";
	}
}


function deleteProjectDocs(strProId, taskId, type1, type, divName, folderCnt, fileCnt, folderName, proDocID, mainPath) {
	var msg ="Are you sure, you wish to delete this Folder?";
	if(type == 'file') {
		msg ="Are you sure, you wish to delete this File?";
	}
	if(confirm(msg)) {
		getContent(strProId+"_"+divName, 'DeleteProjectDocuments.action?operation=D&type='+type+'&folder_name='+folderName+'&proDocID='+proDocID +'&mainPath='+mainPath);
		//alert("type1 ===>> " + type1);
		if(type1 == 'FD') {
			//alert(type1);
			removeInnerTableRow(strProId, taskId, divName, folderCnt, fileCnt);
		} else {
			removeTableRow(strProId, taskId, divName, folderCnt);
		}
	}
}


function removeInnerTableRow(strProId, taskId, divName, folderCnt, fileCnt) {
	var trIndex = document.getElementById(divName+strProId+'_'+taskId+'_'+folderCnt+'_'+fileCnt).rowIndex;
	//alert("trIndex ==>> " + trIndex);
	document.getElementById("folderFileTBL_"+strProId+'_'+taskId+'_'+folderCnt).deleteRow(trIndex);
}


function removeTableRow(strProId, taskId, divName, folderCnt) {
	//alert("divName === "+divName+" folderCnt === " +folderCnt);
	var trIndex = document.getElementById(divName+'_'+strProId+'_'+taskId+'_'+folderCnt).rowIndex;
	//alert("trIndex === " +trIndex);
	
	document.getElementById("proFolderTBL"+strProId+'_'+taskId).deleteRow(trIndex);
}



function editFolder(strProId, taskId, clientId, proId, folderName, proFolderId) {
	removeLoadingDiv("the_div");
	var dialogEdit = '#editFolder';
	dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
	$(dialogEdit).dialog({
		autoOpen : false,
		bgiframe : true,
		resizable : false,  
		height : 350,
		width : '80%', 
		modal : true,
		title : 'Update '+folderName+' Folder',
		open : function() {
			var xhr = $.ajax({
				url : "UpdateProjectDocumentFolder.action?clientId="+clientId+"&proId="+proId+"&folderName="+folderName+"&proFolderId="+proFolderId,
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




function addNewFolder(strProId, taskId) {
	var proTasks = document.getElementById("projectTasks"+strProId+'_'+taskId).value;
	var proEmployee = document.getElementById("resourceIds"+strProId+'_'+taskId).value;
	
	var fdCnt = document.getElementById("folderDocscount"+strProId+'_'+taskId).value;
		var cnt=(parseInt(fdCnt)+1);
	    //alert("addNewFolder cnt ===>> " + cnt + "  strProId ===> " + strProId);
	    var table = document.getElementById("documentTable"+strProId+'_'+taskId);
	    var rowCount = table.rows.length;
	    var row = table.insertRow(rowCount);
	    
	    row.id="folderTR"+strProId+'_'+taskId+'_'+cnt;
	    var cell0 = row.insertCell(0);
	    //cell1.setAttribute('style', 'border-bottom: 1px solid #B6B6B6' );
	    cell0.innerHTML = "<span style=\"float:left; margin-right: 9px;\"><input type=\"hidden\" name=\"folderTRId"+strProId+"\" id=\"folderTRId"+strProId+"_"+cnt+"\" value=\""+cnt+"\">"+
	    "<input type=\"text\" name=\"strFolderName"+strProId+"\" id=\"strFolderName"+strProId+"_"+cnt+"\" style=\"width:200px;\"/></span>"
        +"<span style=\"float:left;\"><a href=\"javascript:void(0)\" onclick=\"addNewFolder('"+strProId+"', '"+taskId+"')\" class=\"add\" title=\"Create New Folder \">Add</a>"
        +"<a href=\"javascript:void(0)\" onclick=\"deleteFolder('"+strProId+"', '"+taskId+"', '"+cnt+"')\" id=\""+cnt+"\" class=\"remove\">Remove Folder</a></span>"
        +"<a href=\"javascript:void(0);\" style=\"float:left; width:86%; margin: -15px 0px 15px 70px;\" onclick=\"addNewFolderDocs('"+strProId+"', '"+taskId+"', '"+cnt+"', this.parentNode.parentNode.rowIndex);\"> +Add Document</a>";
	    
	    var cell1 = row.insertCell(1);
	    cell1.setAttribute("valign","top");
	    cell1.innerHTML = "<select name=\"proTasks\" id=\"proTasks\" style=\"width:140px\"><option value=\"0\">Full Project</option>"+
	    	proTasks+"</select>";
		
	    var cell2 = row.insertCell(2);
	    cell2.setAttribute("valign","top");
	    cell2.innerHTML = "<span style=\"float: left;\"><select name=\"folderSharingType"+strProId+"\" id=\"folderSharingType"+strProId+"_"+cnt+"\" style=\"width:135px\" class=\"validateRequired\" onchange=\"showHideResources('"+strProId+"', this.value, '"+cnt+"')\"><option value=\"\">Select Sharing Type</option>"
		    +"<option value=\"0\">Public</option><option value=\"1\">Private Team</option><option value=\"2\">Myself</option></select></span>"
		    +"<span id=\"proResourceSpan"+strProId+"_"+cnt+"\" style=\"display: none; float: left; margin-left: 9px;\">"+
		    "<select name=\"proEmployee\" id=\"proEmployee\" style=\"width:160px\" multiple size=\"3\"><option value=\"\">Select Resource</option>"+
		    proEmployee+
		    "</select></span>";
		
		document.getElementById("proTasks").name = "proFolderTasks"+strProId;
		document.getElementById("proTasks").id = "proFolderTasks"+strProId+"_"+cnt;
		    
		document.getElementById("proEmployee").name = "proFolderEmployee"+strProId+"_"+cnt;
		document.getElementById("proEmployee").id = "proFolderEmployee"+strProId+"_"+cnt;
		    
	    document.getElementById("folderDocscount"+strProId+'_'+taskId).value = cnt;
	}

function deleteFolder(strProId, taskId, count) {
	if(confirm('Are you sure, you want to delete this folder?')) {
		var trIndex = document.getElementById("folderTR"+strProId+'_'+taskId+"_"+count).rowIndex;
	    document.getElementById("documentTable"+strProId+'_'+taskId).deleteRow(trIndex);
	}
}


function addNewDocs(strProId, taskId) { 
	var proTasks = document.getElementById("projectTasks"+strProId+'_'+taskId).value;
	var proEmployee = document.getElementById("resourceIds"+strProId+'_'+taskId).value;
	
	var fdCnt = document.getElementById("folderDocscount"+strProId+'_'+taskId).value;
		var cnt=(parseInt(fdCnt)+1);
		//alert("addNewDocs cnt ===>> " + cnt + "  strProId ===> " + strProId);
	    var table = document.getElementById("documentTable"+strProId+'_'+taskId);
	    var rowCount = table.rows.length;
	    var row = table.insertRow(rowCount);
	    
	    row.id="docTR"+strProId+'_'+taskId+"_"+cnt;
	    var cell0 = row.insertCell(0);
	    //cell1.setAttribute('style', 'border-bottom: 1px solid #B6B6B6' );
	    cell0.innerHTML = "<span style=\"float:left; margin-right: 9px;\"><input type=\"hidden\" name=\"docsTRId"+strProId+"\" id=\"docsTRId"+strProId+"_"+cnt+"\" value=\""+cnt+"\">"+
	    "<input type=\"file\" name=\"strDoc"+strProId+"\" id=\"strDoc"+strProId+"_"+cnt+"\"/></span>"
        +"<span style=\"float:left;\"><a href=\"javascript:void(0)\" onclick=\"addNewDocs('"+strProId+"', '"+taskId+"')\" class=\"add\" title=\"Add \">Add Document</a>"
        +"<a href=\"javascript:void(0)\" onclick=\"deleteDocs('"+strProId+"', '"+taskId+"', '"+cnt+"')\" id=\""+cnt+"\" class=\"remove\">Remove Document</a></span>";
	    
	    var cell1 = row.insertCell(1);
	    cell1.setAttribute("valign","top");
	    cell1.innerHTML = "<select name=\"proTasks\" id=\"proTasks\" style=\"width:140px\"><option value=\"0\">Full Project</option>"+
	    	proTasks+"</select>";
		
	    var cell2 = row.insertCell(2);
	    cell2.setAttribute("valign","top");
	    cell2.innerHTML = "<span style=\"float: left;\"><select name=\"docSharingType"+strProId+"\" id=\"docSharingType"+strProId+"_"+cnt+"\" style=\"width:135px\" class=\"validateRequired\" onchange=\"showHideResources('"+strProId+"', this.value, '"+cnt+"')\"><option value=\"\">Select Sharing Type</option>"
		    +"<option value=\"0\">Public</option><option value=\"1\">Private Team</option><option value=\"2\">Myself</option></select></span>"
		    +"<span id=\"proResourceSpan"+strProId+"_"+cnt+"\" style=\"display: none; float: left; margin-left: 9px;\">"+
		    "<select name=\"proEmployee\" id=\"proEmployee\" style=\"width:160px\" multiple size=\"3\"><option value=\"\">Select Resource</option>"+
		    proEmployee+
		    "</select></span>";
	
		document.getElementById("proTasks").name = "proDocTasks"+strProId;
	    document.getElementById("proTasks").id = "proDocTasks"+strProId+"_"+cnt;
	    
	    document.getElementById("proEmployee").name = "proDocEmployee"+strProId+"_"+cnt;
	    document.getElementById("proEmployee").id = "proDocEmployee"+strProId+"_"+cnt;
		    
	    document.getElementById("folderDocscount"+strProId+'_'+taskId).value = cnt;
	}

function deleteDocs(strProId, taskId, count) {
	if(confirm('Are you sure, you want to delete this document?')) {
		var trIndex = document.getElementById("docTR"+strProId+'_'+taskId+"_"+count).rowIndex;
	    document.getElementById("documentTable"+strProId+'_'+taskId).deleteRow(trIndex);
	}
}


function addNewFolderDocs(strProId, taskId, folderTRId, rwIndex) {
	//alert("rwIndex ===>> " + rwIndex);
	var proTasks = document.getElementById("projectTasks"+strProId+'_'+taskId).value;
	var proEmployee = document.getElementById("resourceIds"+strProId+'_'+taskId).value;
	
	var fdCnt = document.getElementById("folderDocscount"+strProId+'_'+taskId).value;
		var cnt=(parseInt(fdCnt)+1);
		var val=(parseInt(rwIndex)+1);
		//alert("addNewDocs cnt ===>> " + cnt + "  strProId ===> " + strProId);
		
	    var table = document.getElementById("documentTable"+strProId+'_'+taskId);
	    var rowCount = table.rows.length;
	    var row = table.insertRow(val);
	    
	    row.id="folderDocTR"+strProId+'_'+taskId+"_"+cnt;
	    var cell0 = row.insertCell(0);
	    //cell1.setAttribute('style', 'border-bottom: 1px solid #B6B6B6' );
	    cell0.innerHTML = "<span style=\"float:left; margin-left: 50px; margin-right: 9px;\"><input type=\"hidden\" name=\"folderDocsTRId"+strProId+"_"+folderTRId+"\" id=\"folderDocsTRId"+strProId+"_"+cnt+"\" value=\""+cnt+"\">"+
	    "<input type=\"file\" name=\"strFolderDoc"+strProId+"\" id=\"strFolderDoc"+strProId+"_"+cnt+"\"/></span>"
        +"<a href=\"javascript:void(0)\" onclick=\"addNewFolderDocs('"+strProId+"', '"+taskId+"', '"+folderTRId+"', this.parentNode.parentNode.rowIndex)\" class=\"add\" title=\"Add \">Add Document</a>"
        +"<a href=\"javascript:void(0)\" style=\"float:left;\" onclick=\"deleteFolderDocs('"+strProId+"', '"+taskId+"', '"+cnt+"')\" id=\""+cnt+"\" class=\"remove\">Remove Document</a>";
	    
        var cell1 = row.insertCell(1);
	    cell1.setAttribute("valign","top");
	    cell1.innerHTML = "<select name=\"proTasks\" id=\"proTasks\" style=\"width:140px\"><option value=\"0\">Full Project</option>"+
		    proTasks+"</select>";
		
	    var cell2 = row.insertCell(2);
	    cell2.setAttribute("valign","top");
	    cell2.innerHTML = "<span style=\"float: left;\"><select name=\"folderDocDharingType"+strProId+"_"+folderTRId+"\" id=\"folderDocDharingType"+strProId+"_"+cnt+"\" style=\"width:135px\" class=\"validateRequired\" onchange=\"showHideResources('"+strProId+"', this.value, '"+cnt+"')\"><option value=\"\">Select Sharing Type</option>"
		    +"<option value=\"0\">Public</option><option value=\"1\">Private Team</option><option value=\"2\">Myself</option></select></span>"
		    +"<span id=\"proResourceSpan"+strProId+"_"+cnt+"\" style=\"display: none; float: left; margin-left: 9px;\">"+
		    "<select name=\"proEmployee\" id=\"proEmployee\" style=\"width:160px\" multiple size=\"3\"><option value=\"\">Select Resource</option>"+
		    proEmployee+
		    "</select></span>";
	
	    document.getElementById("proTasks").name = "proFolderDocTasks"+strProId+"_"+folderTRId;
	    document.getElementById("proTasks").id = "proFolderDocTasks"+strProId+"_"+cnt;
	    
	    document.getElementById("proEmployee").name = "proFolderDocEmployee"+strProId+"_"+folderTRId+"_"+cnt;
	    document.getElementById("proEmployee").id = "proFolderDocEmployee"+strProId+"_"+cnt;
	    
	    document.getElementById("folderDocscount"+strProId+'_'+taskId).value = cnt;
	}

function deleteFolderDocs(strProId, taskId, count) {
	if(confirm('Are you sure, you want to delete this document?')) {
		var trIndex = document.getElementById("folderDocTR"+strProId+'_'+taskId+"_"+count).rowIndex;
	    document.getElementById("documentTable"+strProId+'_'+taskId).deleteRow(trIndex);
	}
}

/* function showHideResources(strProId, taskId, val, count) {
	if(val == '2') {
		document.getElementById("proResourceSpan"+strProId+'_'+taskId+"_"+count).style.display = 'block';
	} else {
		document.getElementById("proResourceSpan"+strProId+'_'+taskId+"_"+count).style.display = 'none';
	}
} */

function showTblHeader(strProId, taskId) {
	document.getElementById("folderTR"+strProId+'_'+taskId+"_0").style.display = 'table-row';
}


/* ************************************************************ End Add Document Script ********************************************** */	


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
		    "<input type=\"text\" name=\"taskname"+strTaskId+"\" id=\"taskname"+strTaskId+"_"+cnt+"\" class=\"validateRequired\" style=\"width:160px; font-size:10px; height: 16px;\" onchange=\"saveTaskAndGetTaskId('"+cnt+"', '"+strProId+"' , '"+strTaskId+"', '"+t_st_Type+"')\">"+
		    "<span id=\"addTaskSpan"+strTaskId+"_"+cnt+"\"><input type=\"hidden\" name=\"taskID"+strTaskId+"\" id=\"taskID"+strTaskId+"_"+cnt+"\" value=\"\"></span>"+
		    "<div><a href=\"javascript:void(0)\" onclick=\"updateTaskDescription('"+strTaskId+"', '"+cnt+"', 'taskDescription', 'T');\">D</a>"+
		    "&nbsp;<input type=\"checkbox\" name=\"recurringTask"+strTaskId+"\" id=\"recurringTask"+strTaskId+"_"+cnt+"\" onclick=\"setValue('isRecurringTask"+strTaskId+"_"+cnt+"');\" title=\"Add task to recurring in next frequency\"/>Recurr "+tstType+
		    "<input type=\"hidden\" name=\"isRecurringTask"+strTaskId+"\" id=\"isRecurringTask"+strTaskId+"_"+cnt+"\" value=\"0\"/></div>";
		    
		    var cell2 = row.insertCell(1);
		    cell2.setAttribute("valign","top");
		    cell2.innerHTML = "<span id=\"dependencySpan"+strTaskId+"_"+cnt+"\"> <select name=\"dependency"+strTaskId+"\" id=\"dependency"+strTaskId+"_"+cnt+"\" style=\"width:135px; font-size:10px;\" ><option value=\"\">Select Dependency</option>"+strTaskDepend+"</select></span>"
			    +"<select name=\"dependencyType"+strTaskId+"\" id=\"dependencyType"+strTaskId+"_"+cnt+"\" style=\"width:135px; font-size:10px; margin-left:10px;\" onchange=\"setDependencyPeriod(this.value, '"+strTaskId+"', '"+cnt+"', 'Task');\"><option value=\"\">Select Dependency Type</option>"
			    +"<option value=\"0\">Start-Start</option><option value=\"1\">Finish-Start</option></select>";
			
		    /* var cell2 = row.insertCell(2);
		    cell2.innerHTML = "<select name=\"dependencyType\" id=\"dependencyType"+cnt+"\" style=\"width:135px; font-size:10px;\"><option value=\"\">Select Dependency Type</option>"
			    +"<option value=\"0\">Start-Start</option><option value=\"1\">Finish-Start</option></select>"; */
			
		    var cell3 = row.insertCell(2);
			cell3.setAttribute("valign","top");
			cell3.innerHTML = "<select name=\"priority"+strTaskId+"\" id=\"priority"+strTaskId+"_"+cnt+"\" style=\"width:70px; font-size:10px;\"><option value=\"0\">Low</option>"
			    +"<option value=\"1\">Medium</option><option value=\"2\">High</option></select>";
			
			/* var cell4 = row.insertCell(4);
			cell4.innerHTML = "<select name=\"empSkills"+strTaskId+"\" id=\"empSkills"+strTaskId+"_"+cnt+"\" style=\"width:85px; font-size: 10px;\" onchange=\"getSkillwiseEmployee(this.value, '"+cnt+"', '"+strTaskId+"');\">"
				+"<option value=\"\">Select Skill</option>"+strEmpSkills+"</select>"; */
			//cell4.innerHTML = optSkills;	
			
			var cell5 = row.insertCell(3);
			cell5.setAttribute("valign","top");
			/* cell5.innerHTML = "<span id=\"empSpan"+strTaskId+"_"+cnt+"\"><select name=\"emp_id"+strTaskId+"_"+cnt+"\" id=\"emp_id"+strTaskId+"_"+cnt+"\" style=\"width:140px; font-size: 10px;\" class=\"validateRequired\" multiple size=\"3\">"
				+"<option value=\"\">Select Employee</option>"+strTeamEmp+"</select></span>"; */
			cell5.innerHTML = "<span id=\"empSpan"+strTaskId+"_"+cnt+"\">Myself</span>";
			
			var cell6 = row.insertCell(4);
			cell6.setAttribute("valign","top");
			cell6.innerHTML = "<input type=\"text\" id=\"startDate"+strTaskId+"_"+cnt+"\" name=\"startDate"+strTaskId+"\" class=\"validateRequired\" style=\"width:55px; font-size:10px; height: 16px;\">";

			var cell7 = row.insertCell(5);
			cell7.setAttribute("valign","top");
			cell7.innerHTML = "<input type=\"text\" id=\"deadline1"+strTaskId+"_"+cnt+"\" class=\"validateRequired\" name=\"deadline1"+strTaskId+"\" style=\"width:55px; font-size:10px; height: 16px;\">";
			
			var cell8 = row.insertCell(6);
			cell8.setAttribute("valign","top");
			cell8.innerHTML = "<input type=\"text\" id=\"idealTime"+strTaskId+"_"+cnt+"\" name=\"idealTime"+strTaskId+"\" onkeypress=\"return isNumberKey(event)\" class=\"validateRequired\" style=\"width:30px; font-size:10px; height: 16px; text-align:right;\">";
			
			/* var cell9 = row.insertCell(8);
			cell9.setAttribute("valign","top");
			cell9.innerHTML = "&nbsp;"; */
			
			var cell10 = row.insertCell(7);
			cell10.setAttribute("valign","top");
			cell10.innerHTML = "<input type=\"text\" name=\"colourCode"+strTaskId+"\" id=\"colourCode"+strTaskId+"_"+cnt+"\" class=\"validateRequired\" style=\"width:7px; font-size:10px; height: 16px;\"/>"+
				"<img align=\"left\" style=\"cursor: pointer;position:absolute; padding:5px 0 0 5px\" src=\"images1/color_palate.png\""+ 
				"id=\"pick1\" onclick=\"cp2.select(document.getElementById('frm_ViewAllProjects_"+strTaskId+"').colourCode"+strTaskId+"_"+cnt+",'pick1'); return false;\"/>"+
				"<span class=\"hint ml_25\">Choose a colour for this subtask. This colour will be marked in project grant chart."+
				"<span class=\"hint-pointer\">&nbsp;</span></span>";
					
			var cell11 = row.insertCell(8);
			cell11.setAttribute("nowrap","nowrap");
			cell11.setAttribute("valign","top");
			cell11.innerHTML = "<select name=\"taskActions"+strTaskId+"_"+cnt+"\" id=\"taskActions"+strTaskId+"_"+cnt+"\" style=\"width: 100px;\" onchange=\"executeTaskActions(this.value, this.parentNode.parentNode.rowIndex, '"+cnt+"', '"+strProId+"', '', '"+strTaskId+"', '"+t_st_Type+"');\">"+
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
		    
		    //document.getElementById('empSkills'+strTaskId+'_'+cnt).selectedIndex = document.getElementById('empSkills'+strTaskId+'_'+tCnt).selectedIndex;
	    	//document.getElementById('emp_id'+strTaskId+'_'+cnt).selectedIndex = document.getElementById('emp_id'+strTaskId+'_'+tCnt).selectedIndex;
	    	
	    	document.getElementById('startDate'+strTaskId+'_'+cnt).value = document.getElementById('startDate'+strTaskId+'_'+tCnt).value;
	    	document.getElementById('deadline1'+strTaskId+'_'+cnt).value = document.getElementById('deadline1'+strTaskId+'_'+tCnt).value;
	    	
	    	document.getElementById('idealTime'+strTaskId+'_'+cnt).value = document.getElementById('idealTime'+strTaskId+'_'+tCnt).value;
	    	
	    	document.getElementById('colourCode'+strTaskId+'_'+cnt).value = document.getElementById('colourCode'+strTaskId+'_'+tCnt).value;
	    	document.getElementById('colourCode'+strTaskId+'_'+cnt).style.backgroundColor = document.getElementById('colourCode'+strTaskId+'_'+tCnt).value;

		    setDate(cnt, strTaskId);
		}


	/* function addNewTask(strTaskId, strProId, t_st_Type) { //  
		var taskCnt = document.getElementById("taskcount_"+strTaskId).value;
		
		//alert("taskCnt ===>> " + taskCnt);
		var strTaskDepend = document.getElementById("strProTaskDependency_"+strTaskId).value;
		//var strEmpSkills = document.getElementById("strProEmpSkills_"+strTaskId).value;
		//var strTeamEmp = document.getElementById("strProTeamEmp_"+strTaskId).value;
		
			var cnt=(parseInt(taskCnt)+1);
		    
		    var table = document.getElementById("taskTable_"+strTaskId);
		    var rowCount = table.rows.length;
		    
		    if(parseFloat(rowCount)==1) {
				document.getElementById("taskTable_"+strTaskId).style.display = 'block';
				document.getElementById("addBtn_"+strTaskId).style.display = 'block';
				
				document.getElementById('proDocsSpanStatus'+strTaskId).value = '0';
				document.getElementById('proDocsDownarrowSpan'+strTaskId).style.display = 'block';
				document.getElementById('proDocsUparrowSpan'+strTaskId).style.display = 'none';
				
				document.getElementById('taskActHistoryStatus'+strTaskId).value = '0';
				document.getElementById('taskActHistDownarrowSpan'+strTaskId).style.display = 'block';
				document.getElementById('taskActHistUparrowSpan'+strTaskId).style.display = 'none';
				
				document.getElementById('editTaskSubtaskDiv_'+strTaskId).style.display = 'none';
				document.getElementById('taskActHistory_'+strTaskId).style.display = 'none';
				document.getElementById('proDocsDiv_'+strTaskId).style.display = 'none';
			}
		    
		    var row = table.insertRow(rowCount);
		    var myColor = '#'+(Math.random()*0xFFFFFF<<0).toString(16);
		    //alert("myColor ===>> " + myColor);
		    row.id="task_TR"+strTaskId+"_"+cnt;
		    //var cell0 = row.insertCell(0);
			//cell0.innerHTML = "&nbsp;";
			var tstType = "Task";
			if(t_st_Type == 'ST') {
				tstType = "Sub-task";
			}
			
		    var cell1 = row.insertCell(0);
			cell1.setAttribute("valign","top");
		    //cell1.setAttribute('style', 'border-bottom: 1px solid #B6B6B6' );
		    cell1.innerHTML = "<input type=\"hidden\" name=\"taskTRId"+strTaskId+"\" id=\"taskTRId"+strTaskId+"_"+cnt+"\" value=\""+cnt+"\">"+
		    "<input type=\"hidden\" name=\"taskDescription"+strTaskId+"\" id=\"taskDescription"+strTaskId+"_"+cnt+"\">"+
		    "<input type=\"text\" name=\"taskname"+strTaskId+"\" id=\"taskname"+strTaskId+"_"+cnt+"\" class=\"validateRequired\" style=\"width:160px; font-size:10px; height: 16px;\" onchange=\"saveTaskAndGetTaskId('"+cnt+"', '"+strProId+"' , '"+strTaskId+"', '"+t_st_Type+"')\">"+
		    "<span id=\"addTaskSpan"+strTaskId+"_"+cnt+"\"><input type=\"hidden\" name=\"taskID"+strTaskId+"\" id=\"taskID"+strTaskId+"_"+cnt+"\" value=\"\"></span>"+
		    "<div><a href=\"javascript:void(0)\" onclick=\"updateTaskDescription('"+strTaskId+"', '"+cnt+"', 'taskDescription', 'T');\">D</a>"+
		    "&nbsp;<input type=\"checkbox\" name=\"recurringTask"+strTaskId+"\" id=\"recurringTask"+strTaskId+"_"+cnt+"\" onclick=\"setValue('isRecurringTask"+strTaskId+"_"+cnt+"');\" title=\"Add task to recurring in next frequency\"/>Recurr "+tstType+
		    "<input type=\"hidden\" name=\"isRecurringTask"+strTaskId+"\" id=\"isRecurringTask"+strTaskId+"_"+cnt+"\" value=\"0\"/></div>";
		    
		    var cell2 = row.insertCell(1);
		    cell2.setAttribute("valign","top");
		    cell2.innerHTML = "<span id=\"dependencySpan"+strTaskId+"_"+cnt+"\"> <select name=\"dependency"+strTaskId+"\" id=\"dependency"+strTaskId+"_"+cnt+"\" style=\"width:135px; font-size:10px;\" ><option value=\"\">Select Dependency</option>"+strTaskDepend+"</select></span>"
			    +"<select name=\"dependencyType"+strTaskId+"\" id=\"dependencyType"+strTaskId+"_"+cnt+"\" style=\"width:135px; font-size:10px; margin-left:10px;\" onchange=\"setDependencyPeriod(this.value, '"+strTaskId+"', '"+cnt+"', 'Task');\"><option value=\"\">Select Dependency Type</option>"
			    +"<option value=\"0\">Start-Start</option><option value=\"1\">Finish-Start</option></select>";
			
		    var cell3 = row.insertCell(2);
		    cell3.setAttribute("valign","top");
			cell3.innerHTML = "<select name=\"priority"+strTaskId+"\" id=\"priority"+strTaskId+"_"+cnt+"\" style=\"width:70px; font-size:10px;\"><option value=\"0\">Low</option>"
			    +"<option value=\"1\">Medium</option><option value=\"2\">High</option></select>";
			
			//var cell4 = row.insertCell(4);
			//cell4.innerHTML = "<select name=\"empSkills"+strTaskId+"\" id=\"empSkills"+strTaskId+"_"+cnt+"\" style=\"width:85px; font-size: 10px;\" onchange=\"getSkillwiseEmployee(this.value, '"+cnt+"', '"+strTaskId+"');\">"
			//	+"<option value=\"\">Select Skill</option>"+strEmpSkills+"</select>";
			//cell4.innerHTML = optSkills;	
			
			var cell5 = row.insertCell(3);
			cell5.setAttribute("valign","top");
			//cell5.innerHTML = "<span id=\"empSpan"+strTaskId+"_"+cnt+"\"><select name=\"emp_id"+strTaskId+"_"+cnt+"\" id=\"emp_id"+strTaskId+"_"+cnt+"\" style=\"width:140px; font-size: 10px;\" class=\"validateRequired\" multiple size=\"3\">"
			//	+"<option value=\"\">Select Employee</option>"+strTeamEmp+"</select></span>";
			cell5.innerHTML = "<span id=\"empSpan"+strTaskId+"_"+cnt+"\">Myself</span>";
			
			var cell6 = row.insertCell(4);
			cell6.setAttribute("valign","top");
			cell6.innerHTML = "<input type=\"text\" id=\"startDate"+strTaskId+"_"+cnt+"\" name=\"startDate"+strTaskId+"\" class=\"validateRequired\" style=\"width:55px; font-size:10px; height: 16px;\">";

			var cell7 = row.insertCell(5);
			cell7.setAttribute("valign","top");
			cell7.innerHTML = "<input type=\"text\" id=\"deadline1"+strTaskId+"_"+cnt+"\" class=\"validateRequired\" name=\"deadline1"+strTaskId+"\" style=\"width:55px; font-size:10px; height: 16px;\">";
			
			var cell8 = row.insertCell(6);
			cell8.setAttribute("valign","top");
			cell8.innerHTML = "<input type=\"text\" id=\"idealTime"+strTaskId+"_"+cnt+"\" name=\"idealTime"+strTaskId+"\" onkeypress=\"return isNumberKey(event)\" class=\"validateRequired\" style=\"width:30px; font-size:10px; height: 16px; text-align:right;\">";
			
			//var cell9 = row.insertCell(7);
			//cell9.setAttribute("valign","top");
			//cell9.innerHTML = "&nbsp;";
			
			var cell10 = row.insertCell(7);
			cell10.setAttribute("valign","top");
			cell10.innerHTML = "<input type=\"text\" name=\"colourCode"+strTaskId+"\" id=\"colourCode"+strTaskId+"_"+cnt+"\" class=\"validateRequired\" style=\"width:7px; font-size:10px; height: 16px; background-color: "+myColor+"\" value=\""+myColor+"\"/>"+
					"<img align=\"left\" style=\"cursor: pointer;position:absolute; padding:5px 0 0 5px\" src=\"images1/color_palate.png\""+ 
					"id=\"pick1\" onclick=\"cp2.select(document.getElementById('frm_ViewAllProjects_"+strTaskId+"').colourCode"+cnt+",'pick1'); return false;\"/>"+
					"<span class=\"hint ml_25\">Choose a colour for this subtask. This colour will be marked in project grant chart."+
					"<span class=\"hint-pointer\">&nbsp;</span></span>";
				
			
			var cell11 = row.insertCell(8);
			cell11.setAttribute("nowrap","nowrap");
			cell11.setAttribute("valign","top");
			cell11.innerHTML = "<select name=\"taskActions"+strTaskId+"_"+cnt+"\" id=\"taskActions"+strTaskId+"_"+cnt+"\" style=\"width: 100px;\" onchange=\"executeTaskActions(this.value, this.parentNode.parentNode.rowIndex, '"+cnt+"', '"+strProId+"', '', '"+strTaskId+"', '"+t_st_Type+"');\">"+
			"<option value=\"\">Actions</option><option value=\"1\">Delete</option><option value=\"3\">Repeat "+tstType+" </option>"+
			"</select>";
			
		    document.getElementById("taskcount_"+strTaskId).value = cnt;
		    getTasksForDependency(cnt, strTaskId, strProId, t_st_Type);
		    setDate(cnt, strTaskId);
		} */
		
		
		
		function addNewTask(t_st_Type) { //  
			var taskCnt = document.getElementById("taskcount").value;
			//alert("taskCnt ===>> " + taskCnt);
			//var strTaskDepend = document.getElementById("strProTaskDependency").value;
			var strMyProjects = document.getElementById("strMyProjects").value;
				var cnt=(parseInt(taskCnt)+1);
			    
			    var table = document.getElementById("taskTable");
			    var rowCount = table.rows.length;
			    
			    if(parseFloat(rowCount)==1) {
			    	deleteAllSubTasksCloseDiv();
					document.getElementById("newTaskDiv").style.display = 'block';
				}
			    
			    var row = table.insertRow(rowCount);
			    var myColor = '#'+(Math.random()*0xFFFFFF<<0).toString(16);
			    row.id="task_TR"+cnt;
			    var tstType = "Task";
				if(t_st_Type == 'ST') {
					tstType = "Sub-task";
				}
				
			    var cell1 = row.insertCell(0);
				cell1.setAttribute("valign","top");
			    cell1.innerHTML = "<input type=\"hidden\" name=\"taskTRId\" id=\"taskTRId"+cnt+"\" value=\""+cnt+"\">"+
			    "<input type=\"hidden\" name=\"taskDescription\" id=\"taskDescription"+cnt+"\">"+
			    "<input type=\"text\" name=\"taskname\" id=\"taskname"+cnt+"\" class=\"validateRequired\" style=\"width:160px; font-size:10px; height: 16px;\" onchange=\"saveTaskAndGetTaskId('"+cnt+"', '"+t_st_Type+"')\">"+
			    "<span id=\"myprojectSpan"+cnt+"\"><select name=\"strProjects\" id=\"strProjects"+cnt+"\" style=\"width:135px; font-size:10px; margin-left:10px;\" onchange=\"getTasksForDependency(this.value, '"+cnt+"', '"+t_st_Type+"');\">"
			    +"<option value=\"\">Select Project</option>"+strMyProjects+"</select></span>"+
			    "<span id=\"addTaskSpan"+cnt+"\"><input type=\"hidden\" name=\"taskID\" id=\"taskID"+cnt+"\" value=\"\"></span>"+
			    "<div><a href=\"javascript:void(0)\" onclick=\"updateTaskDescription('"+cnt+"', 'taskDescription', '"+t_st_Type+"');\">D</a>"+
			    "&nbsp;<span id=\"recurrSpan"+cnt+"\" style=\"margin-left:5px; display:none\"><input type=\"checkbox\" name=\"recurringTask\" id=\"recurringTask"+cnt+"\" onclick=\"setValue('isRecurringTask"+cnt+"');\" title=\"Add task to recurring in next frequency\"/>Recurr "+tstType+"</span>"+
			    "<input type=\"hidden\" name=\"isRecurringTask\" id=\"isRecurringTask"+cnt+"\" value=\"0\"/></div>";
			    
			    var cell2 = row.insertCell(1);
			    cell2.setAttribute("valign","top");
			    cell2.innerHTML = "<span id=\"dependencySpan"+cnt+"\"><select name=\"dependency\" id=\"dependency"+cnt+"\" style=\"width:135px; font-size:10px;\" ><option value=\"\">Select Dependency</option></select></span><br/>"
				    +"<select name=\"dependencyType\" id=\"dependencyType"+cnt+"\" style=\"width:135px; font-size:10px; margin-top: 7px;\" onchange=\"setDependencyPeriod(this.value, '"+cnt+"', 'Task');\"><option value=\"\">Select Dependency Type</option>"
				    +"<option value=\"0\">Start-Start</option><option value=\"1\">Finish-Start</option></select>";
				
			    var cell3 = row.insertCell(2);
			    cell3.setAttribute("valign","top");
				cell3.innerHTML = "<select name=\"priority\" id=\"priority"+cnt+"\" style=\"width:70px; font-size:10px;\"><option value=\"0\">Low</option>"
				    +"<option value=\"1\">Medium</option><option value=\"2\">High</option></select>";
				
				/* var cell5 = row.insertCell(3);
				cell5.setAttribute("valign","top");
				cell5.innerHTML = "<span id=\"empSpan"+cnt+"\">Myself</span>"; */
				
				var cell6 = row.insertCell(3);
				cell6.setAttribute("valign","top");
				cell6.innerHTML = "<input type=\"text\" id=\"startDate"+cnt+"\" name=\"startDate\" class=\"validateRequired\" style=\"width:55px; font-size:10px; height: 16px;\">";

				var cell7 = row.insertCell(4);
				cell7.setAttribute("valign","top");
				cell7.innerHTML = "<input type=\"text\" id=\"deadline1"+cnt+"\" class=\"validateRequired\" name=\"deadline1\" style=\"width:55px; font-size:10px; height: 16px;\">";
				
				var cell8 = row.insertCell(5);
				cell8.setAttribute("valign","top");
				cell8.innerHTML = "<input type=\"text\" id=\"idealTime"+cnt+"\" name=\"idealTime\" onkeypress=\"return isNumberKey(event)\" class=\"validateRequired\" style=\"width:30px; font-size:10px; height: 16px; text-align:right;\">"+
				"<input type=\"hidden\" name=\"colourCode\" id=\"colourCode"+cnt+"\" value=\""+myColor+"\" />";
				
				/* var cell10 = row.insertCell(7);
				cell10.setAttribute("valign","top");
				cell10.innerHTML = "<input type=\"text\" name=\"colourCode\" id=\"colourCode"+cnt+"\" class=\"validateRequired\" style=\"width:7px; font-size:10px; height: 16px; background-color: "+myColor+"\" value=\""+myColor+"\"/>"+
						"<img align=\"left\" style=\"cursor: pointer;position:absolute; padding:5px 0 0 5px\" src=\"images1/color_palate.png\""+ 
						"id=\"pick1\" onclick=\"cp2.select(document.getElementById('frm_EmpViewProjectAdd').colourCode"+cnt+",'pick1'); return false;\"/>"+
						"<span class=\"hint ml_25\">Choose a colour for this subtask. This colour will be marked in project grant chart."+
						"<span class=\"hint-pointer\">&nbsp;</span></span>"; */
				
				var cell11 = row.insertCell(6);
				cell11.setAttribute("nowrap","nowrap");
				cell11.setAttribute("valign","top");
				 /* cell11.innerHTML = "<img border=\"0\" onclick=\"executeTaskActions('1', '"+cnt+"', '"+t_st_Type+"');\" src=\"images1/icons/icons/close_button_icon.png\" style=\"padding: 5px 5px 0pt; height: 18px; width: 18px;\">"; */
				cell11.innerHTML = "<i class=\"fa fa-times-circle cross\" onclick=\"executeTaskActions('1', '"+cnt+"', '"+t_st_Type+"');\" aria-hidden=\"true\" style=\"padding: 5px 5px 0pt; height: 18px; width: 18px;\"></i>";
				
				/* cell11.innerHTML = "<select name=\"taskActions"+cnt+"\" id=\"taskActions"+cnt+"\" style=\"width: 100px;\" onchange=\"executeTaskActions(this.value, '"+cnt+"', '"+t_st_Type+"');\">"+
				"<option value=\"\">Actions</option><option value=\"1\">Delete</option></select>"; */
				
			    document.getElementById("taskcount").value = cnt;
			    //getTasksForDependency(cnt, strTaskId, strProId, t_st_Type); <option value=\"3\">Repeat "+tstType+" </option>
			    setDate(cnt);
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
					document.getElementById("newSubTaskDiv").style.display = 'block';
				}
			    
			    var row = table.insertRow(rowCount);
			    var myColor = '#'+(Math.random()*0xFFFFFF<<0).toString(16);
			    
			    row.id="subtask_TR"+cnt;
			    
			    var cell1 = row.insertCell(0);
			    cell1.setAttribute("valign","top");
			    //cell1.setAttribute('style', 'text-align: right;');
			    cell1.innerHTML = "<input type=\"hidden\" name=\"subTaskTRId\" id=\"subTaskTRId"+cnt+"\" value=\""+cnt+"\">"+
			    "<input type=\"hidden\" name=\"subTaskDescription\" id=\"subTaskDescription"+cnt+"\">"+
			    "<input type=\"text\" name=\"subtaskname\" id=\"subtaskname"+cnt+"\" class=\"validateRequired\" style=\"width:160px; font-size:10px; height: 16px;\" onchange=\"saveTaskAndGetTaskId('"+cnt+"', '"+t_st_Type+"')\">"+
			    "<span id=\"myTaskSpan"+cnt+"\"><select name=\"strTasks\" id=\"strTasks"+cnt+"\" style=\"width:135px; font-size:10px; margin-left:10px;\" onchange=\"getTasksForDependency(this.value, '"+cnt+"', '"+t_st_Type+"');\">"+
			    ""+strMyTasks+"</select></span>"+
			    "<span id=\"addSubTaskSpan"+cnt+"\"><input type=\"hidden\" name=\"subTaskID\" id=\"subTaskID"+cnt+"\" value=\"\"></span>"+
			    "<div><a href=\"javascript:void(0)\" onclick=\"updateTaskDescription('"+cnt+"', 'subTaskDescription', '"+t_st_Type+"');\">D</a>"+
			    "&nbsp;<span id=\"recurrSubSpan"+cnt+"\" style=\"margin-left:5px; display:none\"><input type=\"checkbox\" name=\"recurringSubTask\" id=\"recurringSubTask"+cnt+"\" onclick=\"setValue('isRecurringSubTask"+cnt+"');\" title=\"Add sub task to recurring in next frequency\"/>Recurr Sub-task</span>"+
			    "<input type=\"hidden\" name=\"isRecurringSubTask\" id=\"isRecurringSubTask"+cnt+"\" value=\"0\"/></div>";
			    
			    var cell2 = row.insertCell(1);
			    cell2.setAttribute("valign","top");
			    cell2.innerHTML = "<span id=\"subDependencySpan"+cnt+"\"><select name=\"subDependency\" id=\"subDependency"+cnt+"\" style=\"width:135px; font-size:10px;\"><option value=\"\">Select Dependency</option></select></span><br/>"+
				    "<select name=\"subDependencyType\" id=\"subDependencyType"+cnt+"\" style=\"width:135px; font-size:10px; margin-top: 7px;\" onchange=\"setDependencyPeriod(this.value, '"+cnt+"', 'SubTask');\"><option value=\"\">Select Dependency Type</option>"
				    +"<option value=\"0\">Start-Start</option><option value=\"1\">Finish-Start</option></select>";
				
			    var cell3 = row.insertCell(2);
			    cell3.setAttribute("valign","top");
				cell3.innerHTML = "<select name=\"subpriority\" id=\"subpriority"+cnt+"\" style=\"width:70px; font-size:10px;\"><option value=\"0\">Low</option>"
			    +"<option value=\"1\">Medium</option><option value=\"2\">High</option></select>";
				
				/* var cell5 = row.insertCell(3);
				cell5.setAttribute("valign","top");
				cell5.innerHTML = "<span id=\"subEmpSpan"+cnt+"\">Myself</span>"; */
					
				var cell6 = row.insertCell(3);
				cell6.setAttribute("valign","top");
				cell6.innerHTML = "<input type=\"text\" name=\"substartDate\" id=\"substartDate"+cnt+"\" class=\"validateRequired\" style=\"width:55px; font-size:10px; height: 16px;\">";

				var cell7 = row.insertCell(4);
				cell7.setAttribute("valign","top");
				cell7.innerHTML = "<input type=\"text\" name=\"subdeadline1\" id=\"subdeadline1"+cnt+"\" class=\"validateRequired\" style=\"width:55px; font-size:10px; height: 16px;\">";
				
				var cell8 = row.insertCell(5);
				cell8.setAttribute("valign","top");
				cell8.innerHTML = "<input type=\"text\" name=\"subidealTime\" id=\"subidealTime"+cnt+"\" onkeypress=\"return isNumberKey(event)\" class=\"validateRequired\" style=\"width:30px; font-size:10px; height: 16px; text-align:right;\">"+
					"<input type=\"hidden\" name=\"subcolourCode\" id=\"subcolourCode"+cnt+"\" value=\""+myColor+"\" />";
				
				/* var cell10 = row.insertCell(7);
				cell10.setAttribute("valign","top");
				cell10.innerHTML = "<input type=\"text\" name=\"subcolourCode\" id=\"subcolourCode"+cnt+"\" class=\"validateRequired\" style=\"width:7px; font-size:10px; height: 16px; background-color: "+myColor+"\" value=\""+myColor+"\"/>"+
						"<img align=\"left\" style=\"cursor: pointer;position:absolute; padding:5px 0 0 5px\" src=\"images1/color_palate.png\""+ 
							"id=\"pick1\" onclick=\"cp2.select(document.getElementById('frm_EmpViewProjectAddSubTask').subcolourCode"+cnt+",'pick1'); return false;\"/>"+
							"<span class=\"hint ml_25\">Choose a colour for this subtask. This colour will be marked in project grant chart."+
							"<span class=\"hint-pointer\">&nbsp;</span></span>"; */
				
				var cell11 = row.insertCell(6);
				cell11.setAttribute("nowrap","nowrap");
				cell11.setAttribute("valign","top");
				/* cell11.innerHTML = "<img border=\"0\" onclick=\"executeSubTaskActions('1', '"+cnt+"', '"+t_st_Type+"');\" src=\"images1/icons/icons/close_button_icon.png\" style=\"padding: 5px 5px 0pt; height: 18px; width: 18px;\">"; */
				cell11.innerHTML = "<i class=\"fa fa-times-circle cross\" aria-hidden=\"true\" onclick=\"executeSubTaskActions('1', '"+cnt+"', '"+t_st_Type+"');\" style=\"padding: 5px 5px 0pt; height: 18px; width: 18px;\"></i>";
				
				
				/* cell11.innerHTML = "<select name=\"subtaskActions"+cnt+"\" id=\"subtaskActions"+cnt+"\" style=\"width: 100px;\" onchange=\"executeSubTaskActions(this.value, '"+cnt+"', '"+t_st_Type+"');\">"+
				"<option value=\"\">Actions</option><option value=\"1\">Delete</option></select>"; */
				
				document.getElementById("subtaskcount").value = cnt;
			   // alert("taskTRId ===>>" + taskTRId + " cnt ===>> " + cnt); <option value=\"3\">Repeat Sub-task</option>
			    //getSubTasksForDependency(cnt, taskTRId, strProId);
			    setSubDate(cnt);

			}
		
		
		function setValue(strName) {
			var val = document.getElementById(strName).value;
			if(val == '0') {
				document.getElementById(strName).value = '1';
			} else {
				document.getElementById(strName).value = '0';
			}
		}
		
		/* function setDate(id, strTaskId) {
			
			var proStDt = document.getElementById("proStartDate_"+strTaskId).value;
			var proEndDt = document.getElementById("proEndDate_"+strTaskId).value;
			
			jQuery("#frm_ViewAllProjects_"+strTaskId).validationEngine();
			$("#deadline1"+strTaskId+"_"+id).datepicker({
				dateFormat : 'dd/mm/yy', minDate: proStDt, maxDate: proEndDt, 
				onClose: function(selectedDate){
					$("#startDate"+strTaskId+"_"+id).datepicker("option", "maxDate", selectedDate);
				}
			});	
			$("#startDate"+strTaskId+"_"+id).datepicker({
				dateFormat : 'dd/mm/yy', minDate: proStDt, maxDate: proEndDt, 
				onClose: function(selectedDate){
					$("#deadline1"+strTaskId+"_"+id).datepicker("option", "minDate", selectedDate);
				}
			});	
		} */

		function setDate(id) {
			/* var proStDt = document.getElementById("proStartDate").value;
			var proEndDt = document.getElementById("proEndDate").value; */
			var proStDt = '';
			var proEndDt = '';
			$("#deadline1"+id).datepicker({
				dateFormat : 'dd/mm/yy', minDate: proStDt, maxDate: proEndDt, 
				onClose: function(selectedDate){
					$("#startDate"+id).datepicker("option", "maxDate", selectedDate);
				}
			});	
			$("#startDate"+id).datepicker({
				dateFormat : 'dd/mm/yy', minDate: proStDt, maxDate: proEndDt, 
				onClose: function(selectedDate){
					$("#deadline1"+id).datepicker("option", "minDate", selectedDate);
				}
			});	
		}
		
		function setSubDate(id) {
			/* var proStDt = document.getElementById("proStartDate").value;
			var proEndDt = document.getElementById("proEndDate").value; */
			var proStDt = '';
			var proEndDt = '';
			$("#subdeadline1"+id).datepicker({
				dateFormat : 'dd/mm/yy', minDate: proStDt, maxDate: proEndDt, 
				onClose: function(selectedDate){
					$("#substartDate"+id).datepicker("option", "maxDate", selectedDate);
				}
			});	
			$("#substartDate"+id).datepicker({
				dateFormat : 'dd/mm/yy', minDate: proStDt, maxDate: proEndDt, 
				onClose: function(selectedDate){
					$("#subdeadline1"+id).datepicker("option", "minDate", selectedDate);
				}
			});	
		}
		
		
		function saveTaskAndGetTaskId(cnt, t_st_Type) {
			/* if(confirm('Are you sure, you want to add this task?')) { */
				if(t_st_Type == 'T') {
					var proId = document.getElementById("strProjects"+cnt).value;
					//alert("proId ===>>> " + proId);
					var taskName = document.getElementById("taskname"+cnt).value;
					var taskID = document.getElementById("taskID"+cnt).value;
					//alert("taskName ===>>> " + taskName);
					getContent('addTaskSpan'+cnt, 'SaveTaskOrSubtaskAndGetId.action?proId='+proId+'&taskId='+taskID+'&taskName='+encodeURIComponent(taskName)
						+"&count="+cnt+'&type=MP_Task');
				} else {
					//var proId = strProId;
					var taskId = document.getElementById("strTasks"+cnt).value;
					var subTaskName = document.getElementById("subtaskname"+cnt).value;
					var subTaskID = document.getElementById("subTaskID"+cnt).value;
					if(parseFloat(taskId) > 0) {
						getContent('addSubTaskSpan'+cnt, 'SaveTaskOrSubtaskAndGetId.action?subTaskId='+subTaskID+'&subTaskName='+encodeURIComponent(subTaskName)
							+'&taskId='+taskId+"&count="+cnt+'&type=MP_SubTask');
					} else {
						alert('No task available for this sub task, Please add task.');
					}
				}
			/* } */
		}
		
		/* function getTasksForDependency(cnt, strTaskId, strProId, t_st_Type) {
			if(t_st_Type == 'T') {
				getContent('dependencySpan'+strTaskId+'_'+cnt, 'SaveTaskOrSubtaskAndGetId.action?proId='+strProId+"&strTaskId="+strTaskId+"&count="+cnt+'&type=MP_GetTasks');
			} else {
				var taskId = document.getElementById("parentTaskId_"+strTaskId).value;
				getContent('dependencySpan'+strTaskId+"_"+cnt, 'SaveTaskOrSubtaskAndGetId.action?proId='+strProId+"&strTaskId="+strTaskId+"&count="+cnt+"&taskId="+taskId+'&type=MP_GetSubTasks');
			}
			
		} */
		
		
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
								document.getElementById('recurrSpan'+cnt).style.display = "inline";
		                	} else {
		                		document.getElementById('recurrSpan'+cnt).style.display = "none";
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
		                		document.getElementById('recurrSubSpan'+cnt).style.display = "inline";
		                	} else {
		                		document.getElementById('recurrSubSpan'+cnt).style.display = "none";
		                	}
		                	
		                }
		            });
		    	}
		    }
			$(dialogEdit).dialog('close');
		}
		
		/* function getTasksForDependency(strProId, cnt, t_st_Type) {
			if(t_st_Type == 'T') {
				getContent('dependencySpan'+cnt, 'SaveTaskOrSubtaskAndGetId.action?proId='+strProId+"&count="+cnt+'&type=MP_GetTasks');
			} else {
				getContent('subDependencySpan'+cnt, 'SaveTaskOrSubtaskAndGetId.action?count='+cnt+'&taskId='+strProId+'&type=MP_GetSubTasks');
			}
		} */
		
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
		
		
		/* function deleteTask(count, strProId, strTaskId, t_st_Type) {
			//alert("count ==========>> " + count);
			 if(document.getElementById("TskTRId"+strTaskId+"_"+count)) {
				alert("Please first delete sub task of this task then delete this task.");
			} else {
				var tstType = "task";
				if(t_st_Type == 'ST') {
					tstType = "sub-task";
				}
				if(confirm('Are you sure, you want to delete this '+tstType+'?')) {
					deleteTaskFromDB(count, strProId, strTaskId);
					var trIndex = document.getElementById("task_TR"+strTaskId+"_"+count).rowIndex;
				    document.getElementById("taskTable_"+strTaskId).deleteRow(trIndex);
				}
			}
			 var table = document.getElementById("taskTable_"+strTaskId);
			 var rowCount = table.rows.length;
			 //alert("rowCount ===>> " + rowCount);
			 if(parseFloat(rowCount)==1) {
				 document.getElementById("taskTable_"+strTaskId).style.display = 'none';
				 document.getElementById("addBtn_"+strTaskId).style.display = 'none';
			 }
		}
		
		
		function deleteTaskFromDB(taskTRId, strProId, strTaskId) {
			var proId = strProId;
			var taskId = document.getElementById("taskID"+strTaskId+"_"+taskTRId).value;
			getContent('addTaskSpan'+strTaskId+"_"+taskTRId, 'SaveTaskOrSubtaskAndGetId.action?proId='+proId+'&taskId='+taskId+'&type=DelTask');
		} */
		
		
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
				 }
			}
		}
		
		
		function deleteTaskFromDB(taskTRId, t_st_Type) {
			if(t_st_Type == 'ST') {
				//var proId = document.getElementById("strTasks"+taskTRId).value;
				var taskId = document.getElementById("subTaskID"+taskTRId).value;
				getContent('addSubTaskSpan'+taskTRId, 'SaveTaskOrSubtaskAndGetId.action?taskId='+taskId+'&type=DelSubTask');
			} else {
				var proId = document.getElementById("strProjects"+taskTRId).value;
				var taskId = document.getElementById("taskID"+taskTRId).value;
				getContent('addTaskSpan'+taskTRId, 'SaveTaskOrSubtaskAndGetId.action?proId='+proId+'&taskId='+taskId+'&type=DelTask');
			}
		}
		
		
		/* function deleteAddTaskAllRow(strTaskId) {
			//alert("strTaskId ==========>> " + strTaskId);
			 var taskcount = document.getElementById("taskcount_"+strTaskId).value;
			 for(var i=1; i<= parseFloat(taskcount); i++) {
				 if(document.getElementById("task_TR"+strTaskId+"_"+i)) {
				 	var trIndex = document.getElementById("task_TR"+strTaskId+"_"+i).rowIndex;
				    document.getElementById("taskTable_"+strTaskId).deleteRow(trIndex);
				 }
			 }
		} */

		
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
		}

		
		var dialogEdit1 = '#addTaskDescription';
		function updateTaskDescription(cnt, divId, type) {
			removeLoadingDiv('the_div');
			var strTitle = "Task";
			if(type == 'ST') {
				strTitle = "Sub Task";
			}
			//taskDescription
			var taskDescription = document.getElementById(divId+cnt).value;
			dialogEdit1 = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
			$(dialogEdit1).dialog({
				autoOpen : false,
				bgiframe : true,
				resizable : false, 
				height : 250,
				width : 400,    
				modal : true,
				title : strTitle+' Description',
				open : function() {
					var xhr = $.ajax({
						url : "AddTaskDescription.action?divId="+divId+"&count="+cnt+"&taskDescription="+encodeURIComponent(taskDescription),
						cache : false,
						success : function(data) {
							$(dialogEdit1).html(data);
						}
					});
					xhr = null;
				},
				overlay : {
					backgroundColor : '#000',
					opacity : 0.5
				}
			});
			$(dialogEdit1).dialog('open');
		}
		
				
		function addTaskDescription(description, strTaskId, divId, cnt, fromPage) {
			
			if(fromPage == 'U') {
				document.getElementById(divId+strTaskId+'_'+cnt).value = description;
			} else {
				document.getElementById(divId+cnt).value = description;
			}
	    	$(dialogEdit1).dialog('close');
		}
		
		
		//var dialogEdit1 = '#addTaskDescription';
		function updateTaskDescription1(strTaskId, cnt, divId, type, fromPage) {
			removeLoadingDiv('the_div');
			var strTitle = "Task";
			if(type == 'ST') {
				strTitle = "Sub Task";
			}
			//taskDescription
			var taskDescription = document.getElementById(divId+strTaskId+'_'+cnt).value;
			dialogEdit1 = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
			$(dialogEdit1).dialog({
				autoOpen : false,
				bgiframe : true,
				resizable : false, 
				height : 250,
				width : 400,    
				modal : true,
				title : strTitle+' Description',
				open : function() {
					var xhr = $.ajax({
						url : "AddTaskDescription.action?proId="+strTaskId+"&divId="+divId+"&count="+cnt+"&taskDescription="+encodeURIComponent(taskDescription)+"&fromPage="+fromPage,
						cache : false,
						success : function(data) {
							$(dialogEdit1).html(data);
						}
					});
					xhr = null;
				},
				overlay : {
					backgroundColor : '#000',
					opacity : 0.5
				}
			});
			$(dialogEdit1).dialog('open');
		}
		
		
		function hideUpdateTaskSubTaskDiv(taskId) {
			document.getElementById('editTaskSubtaskDiv_'+taskId).style.display = 'none';
		}
		
</script>


<style>
.tb_style tr td {
	padding: 5px;
	border: solid 1px #c5c5c5;
}

.tb_style tr th {
	padding: 5px;
	border: solid 1px #c5c5c5;
	background: #efefef
}

ul li.desgn { padding:0px ; border:solid 1px #ccc}

.close_div
{
  cursor:pointer;
	text-shadow: 0 1px 0 #FFFFFF;
	background-image:url(images1/minus_sign.png);
	background-repeat:no-repeat;
	background-position:10px 6px;
	background-color:#efefef;
}

</style>
	
	<% 
		UtilityFunctions uF = new UtilityFunctions(); 
		String proType = (String)request.getAttribute("proType");
		//System.out.println("EVP1.jsp/1703--proType ===>> " + proType);
	%>

<% if(proType == null || proType.equals("L")) { %>
	<jsp:include page="../common/SubHeader.jsp">
		<jsp:param value="Existing Tasks" name="title" />
	</jsp:include>

<% } else if(proType != null && proType.equals("TR")) { %>
	<jsp:include page="../common/SubHeader.jsp">
		<jsp:param value="Task Requests" name="title" />
	</jsp:include>
<% } else if(proType != null && proType.equals("MR")) { %>
	<jsp:include page="../common/SubHeader.jsp">
		<jsp:param value="My Requests" name="title" />
	</jsp:include>
<% } else if(proType != null && proType.equals("C")) { %>
	<jsp:include page="../common/SubHeader.jsp">
		<jsp:param value="Completed Tasks" name="title" />
	</jsp:include>
<% } %>



<div class="leftbox reportWidth">
	
	<div style="margin-bottom: 20px">
	  <a class="<%=((proType == null || proType.equalsIgnoreCase("L")) ? "current" : "next") %>" href="EmpViewProject.action?proType=L">Existing Tasks</a> |
	  <a class="<%=((proType == null || proType.equalsIgnoreCase("TR")) ? "current" : "next") %>" href="EmpViewProject.action?proType=TR">Task Requests</a> |
	  <a class="<%=((proType == null || proType.equalsIgnoreCase("MR")) ? "current" : "next") %>" href="EmpViewProject.action?proType=MR">My Requests</a> | 
	  <a class="<%=((proType != null && proType.equalsIgnoreCase("C")) ? "current" : "next") %>" href="EmpViewProject.action?proType=C">Completed Tasks</a> 
	</div>
	
	<%-- <% if(proType == null || proType.equals("L")) { %> --%>
	<div style="width: 100%; text-align: center"><%=uF.showData((String) session.getAttribute("MESSAGE"), "")%></div>

	<%
		List<List<String>> proTaskList = (List<List<String>>) request.getAttribute("proTaskList");
	
		//Map<String, List<List<String>>> hmTasks = (Map<String, List<List<String>>>)request.getAttribute("hmTasks");
		//Map<String, List<List<String>>> hmSubTasks = (Map<String, List<List<String>>>) request.getAttribute("hmSubTasks");
		//if(hmTasks == null) hmTasks = new HashMap<String, List<List<String>>>();
		//Map<String, List<String>> hmProject = (Map<String, List<String>>)request.getAttribute("hmProject");
		//if(hmProject == null) hmProject = new HashMap();
		//Map<String, String> hmPMilestoneSize = (Map<String, String>)request.getAttribute("hmPMilestoneSize");
		//Map<String, String> hmPDocumentCounter = (Map<String, String>)request.getAttribute("hmPDocumentCounter");
	
		Map<String, List<List<String>>> hmActivities = (Map<String, List<List<String>>>) request.getAttribute("hmActivities");
		Map<String, String> hmTodayApprovedActivity = (Map<String, String>)request.getAttribute("hmTodayApprovedActivity");
		boolean timeApproveFlag = (Boolean) request.getAttribute("timeApproveFlag");
		Map<String, String> hmProTaskDependency = (Map<String, String>)request.getAttribute("hmProTaskDependency");
		if(hmProTaskDependency == null) hmProTaskDependency = new HashMap<String, String>();
		List<GetPriorityList> priorityList = (List<GetPriorityList>)request.getAttribute("priorityList");
		
	%>

	<!-- <div style="float:left; margin: 10px 0px 0px 0px"> <a href="AddLevel.action"  class="add_lvl" onclick="return hs.htmlExpand(this, { objectType: 'ajax' })">Add Project</a></div> -->

	<s:form action="EmpViewProject" name="frm_empproject_view" theme="simple" method="post">

		<div class="filter_div">
		<div class="filter_caption">Filter</div>
			<s:select label="Select Project" name="pro_id" listKey="projectID" headerKey="" headerValue="All Projects" listValue="projectName"
				list="projectdetailslist" key="" onchange="SendProId(this.value);" />
		</div>
		<input type="hidden" name="proType" id="proType" value="<%=proType != null ? proType : "L" %>"/>
		<div style="float: right; width: auto;"><%=uF.showData((String) request.getAttribute("MESSAGE"), "")%></div>

		<div class="clr"></div>
		<div style="margin: 10px 0px 0px 0px; float: left; width: 100%">
		<ul class="level_list">
		<li style="float: left; width: 100%;">
		<% if(proType == null || !proType.equals("C")) { %> 
      		<span style="float: left; "><a href="javascript:viod(0);" onclick="addNewTask('T');">+Add new task</a></span>
      		<span style="float: left; margin-left: 20px;"><a href="javascript:viod(0);" onclick="addNewSubTask('ST');">+Add new subtask</a></span>
         <% } %>
         <div id="newTaskDiv" style="display: none; float: left; width: 100%;">
			<s:form action="EmpViewProject" name="frm_EmpViewProjectAddTask" id="frm_EmpViewProjectAddTask" method="post" onsubmit="return checkTimeFilledEmpOfAllTasks('')" theme="simple">
				<input type="hidden" name="taskcount" id="taskcount" value="0" />
				<input type="hidden" name="strMyProjects" id="strMyProjects" value="<%=(String)request.getAttribute("sbMyProjectList") %>"/>
					<table class="tb_style" id="taskTable" style="font-size: 10px;" width="100%;">
						<tr>
							<th>
							Task Name & Project Name<sup>*</sup> </th>
							<th>Dependency & Dependency Type</th>
							<th>Priority</th>
							<%-- <th>Resources<sup>*</sup></th> --%>
							<th>Start Date<sup>*</sup></th>
							<th>Deadline<sup>*</sup></th>
							<th><%="Est. man-h" %><sup>*</sup></th>
							<!-- <th>Complete</th> -->
							<%-- <th>Color<sup>*</sup></th> --%>
							<th>Actions</th>
						</tr>
					</table>
					<span style="float: left; "><a href="javascript:viod(0);" onclick="addNewTask('T');">+Add new task</a></span>
					<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equals("L")) { %>
					<div id="addBtn" style="text-align: center;">
						<s:submit name="addTask" cssClass="input_button" value="Add" />
						<input type="button" name="cancelTask" class="cancel_button" value="Cancel" onclick="deleteAllTasksCloseDiv();" />
					</div>
				<% } %>
			</s:form>
		</div>
         
         <div id="newSubTaskDiv" style="display: none; float: left; width: 100%;">
			<s:form action="EmpViewProject" name="frm_EmpViewProjectAddSubTask" id="frm_EmpViewProjectAddSubTask" method="post" onsubmit="return checkTimeFilledEmpOfAllTasks('')" theme="simple">
				<input type="hidden" name="subtaskcount" id="subtaskcount" value="0" />
				<input type="hidden" name="strMyTasks" id="strMyTasks" value="<%=(String)request.getAttribute("sbMyAssignedTasks") %>"/>
					<table class="tb_style" id="subtaskTable" style="font-size: 10px;" width="100%;">
						<tr>
							<th>Sub Task Name & Task Name<sup>*</sup> </th>
							<th>Dependency & Dependency Type</th>
							<th>Priority</th>
							<%-- <th>Resources<sup>*</sup></th> --%>
							<th>Start Date<sup>*</sup></th>
							<th>Deadline<sup>*</sup></th>
							<th><%="Est. man-h" %><sup>*</sup></th>
							<!-- <th>Complete</th> -->
							<%-- <th>Color<sup>*</sup></th> --%>
							<th>Actions</th>
						</tr>
					</table>
					<span style="float: left; "><a href="javascript:viod(0);" onclick="addNewSubTask('ST');">+Add new subtask</a></span>
					<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equals("L")) { %>
					<div id="addSubBtn" style="text-align: center;">
						<s:submit name="addSubTask" cssClass="input_button" value="Add"/>
						<input type="button" name="cancelSubTask" class="cancel_button" value="Cancel" onclick="deleteAllSubTasksCloseDiv();"/>
					</div>
				<% } %>
			</s:form>
		</div>
		
		  		
			</li>
			<%
			boolean activityRunningFlag = (Boolean) request.getAttribute("flag");
			for(int i=0; proTaskList!=null && !proTaskList.isEmpty() && i<proTaskList.size(); i++) {
				List<String> innerList = proTaskList.get(i);
				/* List<List<String>> alSubTasks = new ArrayList<List<String>>();
                   if(hmSubTasks != null) {
   					alSubTasks = hmSubTasks.get(innerList.get(0));
                   }
                   if(alSubTasks == null) 
                	   alSubTasks = new ArrayList<List<String>>(); */
			%>
				<li class="post" style="float: left;">
					<div style="float: left; font-size: 11px;">
						<div style="float: left; margin-right: 10px; width: 95px;">	
							<div style="float: left; width: 100%;">
		                    	<span style="float: left; width: 100%;"> <%=innerList.get(1) %> </span>
								<span style="float: left; width: 100%; margin-top: -10px;"><%=innerList.get(2) %> <%=innerList.get(8) %> <%=innerList.get(17) %></span>
							</div>
	                    </div>
	                
	                    <div style="float: left; margin-right: 10px; width: 320px;">
		                    <div style="float: left; width: 100%;">
		                    	<span style="float: left; width: 100%; <%=(uF.parseToInt((String)innerList.get(11))==1) ? "background-image: url(&quot;images1/icons/new2.gif&quot;);":""%> background-repeat: no-repeat; background-position: right top;">
		                    		<strong><%=innerList.get(3) %></strong>
		                    	</span>
		                    	<% if(innerList.get(20) != null && !innerList.get(20).equals("")) { %>
		                    		<span style="float: left; width: 100%; margin-top: -10px; color: gray; font-style: italic;">(<%=innerList.get(20) %>)</span>
		                    	<% } %>
								<span style="float: left; width: 100%; margin-top: -10px; color: gray;"><%=innerList.get(14) %> </span>
								<span style="float: left; width: 100%; margin-top: -11px;"> assigned by: <%=innerList.get(15) %> </span>
								<span style="float: left; width: 100%; margin-top: -11px;"><%=(innerList.get(24)!=null && innerList.get(24).length() > 50) ? innerList.get(24).subSequence(0, 50) :innerList.get(24) %> 
								<% if(innerList.get(24)!=null && innerList.get(24).length() > 50) { %>
								<% 
								String sTTType = "T";
								if(uF.parseToInt(innerList.get(21))>0) { 
									sTTType = "ST";
								} %>
								<a href="javascript:void(0)" onclick="updateTaskDescription1(<%=innerList.get(0) %>, '<%=i %>', 'taskDescription', '<%=sTTType %>', 'V')">...</a>
								<% } %>
								</span>
								<%-- <% if(uF.parseToInt(innerList.get(21))>0) { %>
		                    		<span style="float: left; width: 100%; margin-top: -10px;"><a href="javascript:viod(0);" onclick="addNewTask('<%=innerList.get(0) %>', '<%=innerList.get(18) %>', 'ST');">+Add new subtask</a></span>
		                    	<% } else { %>
		                    		<span style="float: left; width: 100%; margin-top: -10px;"><a href="javascript:viod(0);" onclick="addNewTask('<%=innerList.get(0) %>', '<%=innerList.get(18) %>', 'T');">+Add new task</a></span>
		                    	<% } %> --%>
							</div>
	                    </div>
	                    
	                    <div style="float: left; margin-right: 10px; width: 60px;">
	                    	<div style="float: left; width: 100%;"><strong>Priority</strong></div>
	                    	<div style="float: left; width: 100%; margin-top: -10px;"><%=innerList.get(4) %></div>
	                    </div>
	                    
	                    <div style="float: left; margin-right: 10px; width: 125px;">
		                    <div style="float: left; width: 100%;"><label title="<%=innerList.get(5) %> to <%=innerList.get(6) %>">Deadline<strong> <%=innerList.get(6) %></strong></label></div>
		                    <div style="float: left; width: 50%; margin-top: -10px;">
		                    	<div class="anaAttrib1"><span id="TcompletePercent_<%=innerList.get(18) %>_<%=innerList.get(0)%>"><%=uF.formatIntoOneDecimalWithOutComma(uF.parseToDouble(innerList.get(9))) %>%</span>
		                    	
		                    	<a href="javascript:void(0)" onclick="updateStatus(<%=innerList.get(0) %>,<%=innerList.get(18) %>, 'Tcomplete')"><img src="images1/icons/popup_arrow.gif" title="Update Completion Status" height="8px"/></a>
		                    	</div>
								<div id="Tcomplete_<%=innerList.get(18) %>_<%=innerList.get(0) %>" class="outbox">
									<div class="greenbox" style="width: <%=uF.formatIntoOneDecimalWithOutComma(uF.parseToDouble(innerList.get(9))) %>%;"></div>
								</div>
							</div>
	                    </div>
	                    
	                    
	                    <div style="float: left; margin-right: 10px; width: 85px;">
	                    	<div style="float: left; width: 100%;"><strong>Est. Man <%=innerList.get(17) %></strong> </div>
	                    	<div style="float: left; width: 100%;">
	                    		<span style="float:left; width: 100%; margin-top: -7px;"><span class="anaAttrib1" style="font-size: 20px"><%=innerList.get(7) %></span></span>
	                    	</div>
	                    </div>
	                    
	                    <div style="float: left; margin-right: 10px; width: 80px;">
	                    	<div style="float: left; width: 100%;"><strong>Docs</strong></div>
	                    	<div style="float: left; width: 100%; margin-top: -10px"><span class="anaAttrib1" style="font-size: 20px"><%=uF.parseToInt(innerList.get(16)) > 0 ? innerList.get(16) : "0"%></span> docs</div>
	                    	<div style="float: left; width: 100%;">
	                    		<span style="float:left; width: 100%; margin-top: -11px;">
	                    		<span style="float: left;">all docs</span>
								<input type="hidden" name="proDocsSpanStatus" id="proDocsSpanStatus<%=innerList.get(0) %>" value = "0"/>
								<a href="javascript:void(0)" onclick="viewDocuments('<%=innerList.get(0) %>', '<%=innerList.get(18) %>', '<%=proType %>')">
									<span id="proDocsDownarrowSpan<%=innerList.get(0) %>" style="float: left; margin-left: 2px; margin-top: 5px;"> 
										<i class="fa fa-angle-down" aria-hidden="true" style="width: 14px;"></i>
									</span>
									<span id="proDocsUparrowSpan<%=innerList.get(0) %>" style="float: left; margin-left: 2px; margin-top: 5px; display: none;">
										<i class="fa fa-angle-up" aria-hidden="true" style="width: 14px;"></i>
									</span>
								</a>
	                    		</span>
	                    	</div>
	                    </div>
	                    <div style="float: left; width: 100px; margin-right: 10px;">
	                    	<!-- <div style="float: left; width: 100%;">&nbsp;</div> -->
	                    	<div style="float: left; width: 100%; margin-top: 9px;">
	                    	<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equals("L")) { %>
		                    <select name="actions<%=innerList.get(0) %>" id="actions<%=innerList.get(0) %>" style="width: 100px;" onchange="executeTaskActivities(this.value, '<%=innerList.get(0) %>', '<%=innerList.get(18) %>', '<%=innerList.get(19) %>', '<%=activityRunningFlag %>', '<%=timeApproveFlag %>', '0', '<%=innerList.get(12) %>');">
		                    	<option value="">Activities</option>
		                    	<% if(uF.parseToInt(innerList.get(19)) == 0) { %>
			                    	<option value="1">Start Task</option>
			                    	<option value="2">Add Time </option>
		                    	<% } else { %>
		                    		<option value="3">End Task </option>
		                    	<% } %>
		                    </select>
		                   <% } %>
	                    </div>
	                    <div style="float: left; width: 100%;">
	                    		<span style="float:left; width: 100%; margin-top: -3px;">
	                    		<span style="float: left;">Activity History</span>
								<input type="hidden" name="taskActHistoryStatus" id="taskActHistoryStatus<%=innerList.get(0) %>" value = "0"/>
								<a href="javascript:void(0)" onclick="viewTaskActHistory('<%=innerList.get(0) %>', '<%=innerList.get(18) %>', '<%=proType %>')">
									<span id="taskActHistDownarrowSpan<%=innerList.get(0) %>" style="float: left; margin-left: 2px; margin-top: 5px;"> 
										<i class="fa fa-angle-down" aria-hidden="true" style="width: 14px;"></i> 
									</span>
									<span id="taskActHistUparrowSpan<%=innerList.get(0) %>" style="float: left; margin-left: 2px; margin-top: 5px; display: none;">
										<i class="fa fa-angle-up" aria-hidden="true" style="width: 14px;"></i>
									</span>
								</a>
	                    		</span>
	                    	</div>
	                    	<div style="float: left; width: 100%;"></div>
	                    </div>
	                    
	                    
	                    <div style="float: left; width: 100px; margin-right: 10px;">
	                    <% if(innerList.get(35) != null && !innerList.get(35).equals("O")) { %>
	                    	<div style="float: left; width: 100%;"><strong>Timesheet</strong></div>
	                    	<div style="float: left; width: 100%; margin-top: -10px;">
	                    	<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equals("L")) { %>
		                    Submit in <%=innerList.get(36) %> days
		                   <% } %>
	                    	</div>
	                    	<div style="float: left; width: 100%; margin-top: -10px;"><a href="AddProjectActivity1.action">Submit</a></div>
	                    <% } else { %>
	                    	&nbsp;
	                    <% } %>	
	                    </div>
	                   
                    <!-- </div>
                    
                    <div style="float:right;"> -->
                    
                    <div style="float: left; width: 100px; margin-right: 10px;">
	                    	<div style="float: left; width: 100%; margin-top: 9px;">
	                    	<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equals("L")) { %>
	                    	<% if(uF.parseToBoolean(innerList.get(13))) { %>
		                    <select name="action<%=innerList.get(0) %>" id="actions<%=innerList.get(0) %>" style="width: 100px;" onchange="executeTaskAction(this.value, '<%=innerList.get(0) %>', '<%=innerList.get(18) %>', '<%=innerList.get(19) %>', '<%=activityRunningFlag %>', '<%=timeApproveFlag %>', '0', '<%=innerList.get(12) %>');">
		                    	<option value="">Action</option>
		                    	<option value="5">Delete</option>
		                    	<option value="6">Edit</option>
		                    </select>
		                    <% } %>
		                   <% } %>
	                    </div>
	                </div>
	                 
                    <% if(proType == null || proType.equals("") || proType.equals("null") || proType.equals("L")) { %>
                    <%-- <div style="float: left;">
	                    	<div style="float: left; width: 100%;"><strong>Actions</strong></div>
	                    	<div style="float: left; width: 100%; margin-top: -10px;">
	                    	<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equals("L")) { %>
		                    	Remark
		                   <% } %>
	                    	</div>
	                    </div> --%>
                   <%--  <select name="actions<%=innerList.get(0) %>" id="actions<%=innerList.get(0) %>" style="width: 100px;" onchange="executeActions(this.value, '<%=innerList.get(0) %>');">
                    	<option value="">Actions</option>
                    	<option value="1">Edit Project</option>
                    	<option value="2">Delete Project</option>
                    	<option value="3">Mark as Completed </option>
                    	<option value="4">Mark as Blocked </option>
                    </select> --%>
                   <% } %>
                    
                    </div>
			
			<div id="proDocsDiv_<%=innerList.get(0) %>" style="display: none;"></div>
			<div id="taskActHistory_<%=innerList.get(0) %>" style="display: none;"></div>
			
			<%-- <ul>
			<%
			for(int j=0; alSubTasks!=null && !alSubTasks.isEmpty() && j<alSubTasks.size(); j++) {
				List<String> innerSubList = alSubTasks.get(j);
			%>
				<li class="post" style="float: left; border-bottom: 1px solid #CCCCCC; width: 96%;">
					<div style="float: left; font-size: 11px;">
						<div style="float: left; margin-right: 5px; width: 70px;">	
							<div style="float: left; width: 100%;">
		                    	<span style="float: left; width: 100%;"> <%=innerSubList.get(1) %> </span>
								<span style="float: left; width: 100%; margin-top: -10px; color: gray;"><%=innerSubList.get(8) %> <%=innerSubList.get(17) %></span>
							</div>
	                    </div>
	                
	                    <div style="float: left; margin-right: 10px; width: 289px;">
		                    <div style="float: left; width: 100%;">
		                    	<span style="float: left; width: 100%; <%=(uF.parseToInt((String)innerSubList.get(11))==1) ? "background-image: url(&quot;images1/icons/new2.gif&quot;);":""%> background-repeat: no-repeat; background-position: right top;">
		                    		<strong><%=innerSubList.get(2) %></strong>
		                    	</span>
								<span style="float: left; width: 100%; margin-top: -10px; color: gray;"><%=innerSubList.get(14) %> </span>
								<span style="float: left; width: 100%; margin-top: -11px;"> assigned by: <%=innerSubList.get(15) %> </span>
							</div>
	                    </div>
	                    
	                    <div style="float: left; margin-right: 10px; width: 60px;">
	                    	<div style="float: left; width: 100%;"><strong>Priority</strong></div>
	                    	<div style="float: left; width: 100%; margin-top: -10px;"><%=innerSubList.get(3) %></div>
	                    </div>
	                    
	                    <div style="float: left; margin-right: 10px; width: 125px;">
		                    <div style="float: left; width: 100%;"><label title="<%=innerSubList.get(5) %> to <%=innerSubList.get(6) %>">Deadline<strong> <%=innerSubList.get(6) %></strong></label></div>
		                    <div style="float: left; width: 50%; margin-top: -10px;">
		                    	<div class="anaAttrib1"><%=uF.formatIntoOneDecimalWithOutComma(uF.parseToDouble(innerSubList.get(9))) %>%</div>
								<div id="Pcomplete_<%=innerSubList.get(0) %>" class="outbox">
									<div class="greenbox" style="width: <%=uF.formatIntoOneDecimalWithOutComma(uF.parseToDouble(innerSubList.get(9))) %>%;"></div>
								</div>
							</div>
	                    </div>
	                    
	                    
	                    <div style="float: left; margin-right: 10px; width: 85px;">
	                    	<div style="float: left; width: 100%;"><strong>Est. Man <%=innerSubList.get(17) %></strong> </div>
	                    	<div style="float: left; width: 100%;">
	                    		<span style="float:left; width: 100%; margin-top: -7px;"><span class="anaAttrib1" style="font-size: 20px"><%=innerSubList.get(7) %></span></span>
	                    	</div>
	                    </div>
	                    
	                    <div style="float: left; margin-right: 10px; width: 80px;">
	                    	<div style="float: left; width: 100%;"><strong>Docs</strong></div>
	                    	<div style="float: left; width: 100%; margin-top: -10px"><span class="anaAttrib1" style="font-size: 20px"><%=uF.parseToInt(innerSubList.get(16)) > 0 ? innerSubList.get(16) : "0"%></span> docs</div>
	                    	<div style="float: left; width: 100%;">
	                    		<span style="float:left; width: 100%; margin-top: -11px;">
	                    		<span style="float: left;">all docs</span>
								<input type="hidden" name="proDocsSpanStatus" id="proDocsSpanStatus<%=innerSubList.get(0) %>" value = "0"/>
								<a href="javascript:void(0)" onclick="viewDocuments('<%=innerSubList.get(0) %>', '<%=proType %>')">
									<span id="proDocsDownarrowSpan<%=innerSubList.get(0) %>" style="float: left; margin-left: 2px; margin-top: 5px;"> 
										<img src="images1/icons/icons/downarrow.png" style="width: 14px;"/> 
									</span>
									<span id="proDocsUparrowSpan<%=innerSubList.get(0) %>" style="float: left; margin-left: 2px; margin-top: 5px; display: none;">
										<img src="images1/icons/icons/uparrow.png" style="width: 14px;"/> 
									</span>
								</a>
	                    		</span>
	                    	</div>
	                    </div>
	                    <div style="float: left; width: 100px; margin-right: 10px;">
	                    	<div style="float: left; width: 100%; margin-top: 9px;">
	                    	<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equals("L")) { %>
		                    <select name="actions<%=innerSubList.get(0) %>" id="actions<%=innerSubList.get(0) %>" style="width: 100px;" onchange="executeSubTaskActions(this.value, '<%=innerSubList.get(0) %>', '<%=innerSubList.get(18) %>', '<%=innerSubList.get(19) %>', '<%=activityRunningFlag %>', '<%=timeApproveFlag %>', '<%=innerList.get(0) %>', '<%=innerSubList.get(12) %>');">
		                    	<option value="">Actions</option>
		                    	<% if(alSubTasks != null && !alSubTasks.isEmpty() && alSubTasks.size()>0) { %>
			                    	<% if(uF.parseToInt(innerSubList.get(19)) == 0) { %>
				                    	<option value="1">Start Task</option>
				                    	<option value="2">Add Time </option>
			                    	<% } else { %>
			                    		<option value="3">End Task </option>
			                    	<% } %>
			                    	<option value="4">Activity History</option>
		                    	<% } %>
		                    	<option value="5">Delete</option>
		                    	<option value="6">Edit</option>
		                    </select>
		                   <% } %>
	                    </div>
	                    	<div style="float: left; width: 100%;"></div>
	                    </div>
	                    
	                    <div style="float: left; width: 100px; margin-right: 10px;">
	                    	<div style="float: left; width: 100%;"><strong>Timesheet</strong></div>
	                    	<div style="float: left; width: 100%; margin-top: -10px;">
	                    	<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equals("L")) { %>
		                    Submit in -- days
		                   <% } %>
	                    	</div>
	                    	<div style="float: left; width: 100%; margin-top: -10px;"><a href="javascript:void(0);">Submit</a></div>
	                    </div>
                    
                    <% if(proType == null || proType.equals("") || proType.equals("null") || proType.equals("L")) { %>
                    <div style="float: left;">
	                    	<div style="float: left; width: 100%;"><strong>Actions</strong></div>
	                    	<div style="float: left; width: 100%; margin-top: -10px;">
	                    	<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equals("L")) { %>
		                    	Remark
		                   <% } %>
	                    	</div>
	                    </div>
                   <% } %>
                    
                    </div>
			</li>
			<% } %>
			</ul> --%>
			<div id="editTaskSubtaskDiv_<%=innerList.get(0) %>" style="float: left; display: none;">
				<s:form action="EmpViewProject" name="frm_EditEmpViewProject_<%=innerList.get(0) %>" id="frm_EditEmpViewProject_<%=innerList.get(0) %>" method="post" onsubmit="return checkTimeFilledEmpOfAllTasks('<%=innerList.get(0) %>')" theme="simple">
	            <input type="hidden" name="proId" id="proId" value="<%=innerList.get(18) %>" />
	            <input type="hidden" name="taskId" id="taskId" value="<%=innerList.get(0) %>" />
	            <input type="hidden" name="parentTaskId" id="parentTaskId_<%=innerList.get(0) %>" value="<%=innerList.get(21) %>" />
					<table class="tb_style" id="editTaskTable_<%=innerList.get(0) %>" style="font-size: 10px; width="100%;">
						<tr>
							<th>
							<% 
							String sttType = "Task";
							String sTTType = "T";
							if(uF.parseToInt(innerList.get(21))>0) { 
								sttType = "Sub-task";
								sTTType = "ST";
							%>Sub <% } %>
							Task Name<sup>*</sup> </th>
							<th>Dependency & Dependency Type</th>
							<th>Priority</th>
							<th>Resources<sup>*</sup></th>
							<th>Start Date<sup>*</sup></th>
							<th>Deadline<sup>*</sup></th>
							<th><%="Est. man-"+innerList.get(17) %><sup>*</sup></th>
							<!-- <th>Complete</th> -->
							<th>Color<sup>*</sup></th>
							<!-- <th>Actions</th> -->
						</tr>
						<script type="text/javascript">
							$(function() {
								
								$("#deadline1"+<%=innerList.get(0) %>).datepicker({
									dateFormat : 'dd/mm/yy', minDate:"<%=(String)innerList.get(22) %>", maxDate: "<%=(String)innerList.get(23) %>", 
									onClose: function(selectedDate){
										$("#startDate"+<%=innerList.get(0) %>).datepicker("option", "maxDate", selectedDate);
									}
								});
								
								$("#startDate"+<%=innerList.get(0) %>).datepicker({
									dateFormat : 'dd/mm/yy', minDate:"<%=(String)innerList.get(22) %>", maxDate: "<%=(String)innerList.get(23) %>", 
									onClose: function(selectedDate){
										$("#deadline1"+<%=innerList.get(0) %>).datepicker("option", "minDate", selectedDate);
									}
								});
						
							});
							</script>
						<tr id="task_TR<%=innerList.get(0) %>">
						<td valign="top">
						<input type="hidden" name="tstFilledEmp<%=innerList.get(0) %>" id="tstFilledEmp<%=innerList.get(0) %>_<%=i %>" value="<%=innerList.get(24) %>" /> 
						<input type="hidden" name="taskDescription<%=innerList.get(0) %>" id="taskDescription<%=innerList.get(0) %>_<%=i %>" value="<%=innerList.get(24) %>"/>
						<input type="text" name="taskname<%=innerList.get(0) %>" id="taskname<%=innerList.get(0) %>" value="<%=innerList.get(3) %>" class="validateRequired" style="width: 160px; font-size: 10px; height: 16px;"/>
							<div id="addTaskSpan<%=innerList.get(0) %>" style="display: block;">
								<input type="hidden" name="taskID<%=innerList.get(0) %>" id="taskID<%=innerList.get(0) %>" value="<%=innerList.get(0)%>" />
							</div>
						<div style="margin-top: -5px; margin-bottom: -11px; font-style: italic; color: gray;"><%=innerList.get(25) %> </div>
						<div><a href="javascript:void(0)" onclick="updateTaskDescription1(<%=innerList.get(0) %>, '<%=i %>', 'taskDescription', '<%=sTTType %>', 'U')">D</a>
							<%-- <%if(alProjects.get(21) != null && !alProjects.get(21).equals("O")) { %> --%>
							<% 
							String strChecked = "";
								if(uF.parseToInt(innerList.get(26)) == 1) { 
									strChecked = "checked";
								}
							%>
								&nbsp;<input type="checkbox" name="recurringTask<%=innerList.get(0) %>" id="recurringTask<%=innerList.get(0) %>" <%=strChecked %> onclick="setValue('isRecurringTask<%=innerList.get(0) %>');" title="Add task to recurring in next frequency"/>Recurr <%=sttType %>
								<input type="hidden" name="isRecurringTask<%=innerList.get(0) %>" id="isRecurringTask<%=innerList.get(0) %>" value="<%=innerList.get(26) %>"/>
							<%-- <% } %> --%>
						</div>
						<div style="margin-top: -11px; margin-bottom: -10px; font-style: italic; color: gray;">assigned by: <%=innerList.get(15) %> </div>	 
						</td>

						<td valign="top">
							<select name="dependency<%=innerList.get(0) %>" id="dependency<%=innerList.get(0) %>" style="width:135px; font-size:10px;">
								<option value="">Select Dependency</option>
								<%=innerList.get(27)%>
							</select> <br/>
							<select name="dependencyType<%=innerList.get(0) %>" id="dependencyType<%=innerList.get(0) %>" style="width: 135px; font-size: 10px; margin-top: 7px;" onchange="setDependencyPeriod(this.value, '<%=innerList.get(0) %>', '0', 'Task');">
								<option value="">Select Dependency Type</option>
								<option value="0"
									<%if(innerList.get(28) != null && innerList.get(28).equals("0")) { %>
									selected <% } %>>Start-Start</option>
								<option value="1"
									<%if(innerList.get(28) != null && innerList.get(28).equals("1")) { %>
									selected <% } %>>Finish-Start</option>
							</select>
						</td>

						<td valign="top"><select name="priority<%=innerList.get(0) %>" id="priority<%=innerList.get(0) %>" style="width: 70px; font-size: 10px;" class="validateRequired">
								<% for(GetPriorityList getPriorityList:priorityList) { %>
								<option value="<%=getPriorityList.getPriId() %>"
									<%if(innerList.get(29) != null && getPriorityList.getPriId().equals(innerList.get(29))) { %>
									selected <%} %>>
									<%=getPriorityList.getProName() %></option>
								<% } %>
						</select></td>

						<%-- <td valign="top"><select name="empSkills<%=strProjectId %>" id="empSkills<%=strProjectId %>_<%=i %>" style="width: 85px; font-size: 10px;" onchange="getSkillwiseEmployee(this.value, '<%=i %>', '<%=strProjectId %>');">
								<option value="">Select Skill</option>
								<%=alInner.get(7)%>
						</select></td> --%>

						<td valign="top"><span id="empSpan<%=innerList.get(0) %>"> <select name="emp_id<%=innerList.get(0) %>" id="emp_id<%=innerList.get(0) %>" style="width: 140px; font-size: 10px;" class="validateRequired" multiple size="3">
								<option value="">Select Employee</option>
								<%=innerList.get(30)%>
							</select> </span></td>
						<td valign="top"><input type="text" id="startDate<%=innerList.get(0) %>" name="startDate<%=innerList.get(0) %>" style="width: 55px; font-size: 10px; height: 16px;" class="validateRequired" value="<%=innerList.get(31)%>">
						</td>
						<td valign="top">
							<input type="text" id="deadline1<%=innerList.get(0) %>" name="deadline1<%=innerList.get(0) %>" value="<%=innerList.get(32)%>" class="validateRequired" style="width: 55px; font-size: 10px; height: 16px;">
						</td>
						<td valign="top">
							<input type="text" name="idealTime<%=innerList.get(0) %>" id="idealTime<%=innerList.get(0) %>" onkeypress="return isNumberKey(event)" value="<%=innerList.get(33)%>" class="validateRequired" style="width: 30px; font-size: 10px; height: 16px; text-align: right;">
						</td>

						<td valign="top"><input type="text" name="colourCode<%=innerList.get(0) %>" value="<%=innerList.get(34) %>" id="colourCode<%=innerList.get(0) %>" class="validateRequired" style="width:7px; font-size: 10px; height: 16px; background-color: <%=innerList.get(34) %>" readonly="readonly"/> 
							<img align="left" style="cursor: pointer; position: absolute; padding: 5px 0 0 5px" src="images1/color_palate.png" id="pick1" onclick="cp2.select(document.getElementById('frm_EditEmpViewProject_<%=innerList.get(0) %>').colourCode<%=innerList.get(0) %>,'pick1'); return false;" />
						</td>
					</tr>
					
					</table>
					<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equals("L")) { %>
						<div id="editBtn_<%=innerList.get(0) %>" style="text-align: center;">
							<s:submit name="addTask" cssClass="input_button" value="Update"/>
							<input type="button" name="cancelUpdateTask" class="cancel_button" value="Cancel" onclick="hideUpdateTaskSubTaskDiv('<%=innerList.get(0) %>');"/>
						</div>
					<% } %>
				</s:form>
			</div>
			
			
			<%-- <% 
				String strProTaskDependency = hmProTaskDependency.get(innerList.get(0)); 
			%>
			<div style="float: left; width: 100%;">
			<s:form action="EmpViewProject" name="frm_EmpViewProject_<%=innerList.get(0) %>" id="frm_EmpViewProject_<%=innerList.get(0) %>" method="post" onsubmit="return checkTimeFilledEmpOfAllTasks('<%=innerList.get(0) %>')" theme="simple">
            <input type="hidden" name="proId" id="proId" value="<%=innerList.get(18) %>" />
            <input type="hidden" name="taskId" id="taskId" value="<%=innerList.get(0) %>" />
            <input type="hidden" name="parentTaskId" id="parentTaskId_<%=innerList.get(0) %>" value="<%=innerList.get(21) %>" />
            <input type="hidden" name="proStartDate_<%=innerList.get(0) %>" id="proStartDate_<%=innerList.get(0) %>" value="<%=innerList.get(22) %>"/>
			<input type="hidden" name="proEndDate_<%=innerList.get(0) %>" id="proEndDate_<%=innerList.get(0) %>" value="<%=innerList.get(23) %>"/>
			<input type="hidden" name="taskcount_<%=innerList.get(0) %>" id="taskcount_<%=innerList.get(0) %>" value="0" />
			<input type="hidden" name="strProTaskDependency" id="strProTaskDependency_<%=innerList.get(0) %>" value="<%=strProTaskDependency %>"/>
			
				<table class="tb_style" id="taskTable_<%=innerList.get(0) %>" style="font-size: 10px;  display: none;" width="100%;">
					<tr>
						<th>
						<% if(uF.parseToInt(innerList.get(21))>0) { %>Sub <% } %>
						Task Name<sup>*</sup> </th>
						<th>Dependency & Dependency Type</th>
						<th>Priority</th>
						<th>Resources<sup>*</sup></th>
						<th>Start Date<sup>*</sup></th>
						<th>Deadline<sup>*</sup></th>
						<th><%="Est. man-"+innerList.get(17) %><sup>*</sup></th>
						<th>Color<sup>*</sup></th>
						<th>Actions</th>
					</tr>
				</table>
				<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equals("L")) { %>
					<div id="addBtn_<%=innerList.get(0) %>" style="text-align: center; display: none;"><s:submit name="addTask" cssClass="input_button" value="Save"/></div>
				<% } %>
			</s:form>
		</div> --%>
		
		</li>	
			<% } %>
			</ul>

			<% if (proTaskList != null && proTaskList.size() == 0) { %>
			<div class="msg nodata"><span>No task is assigned to you.</span></div>
			<% } %>

		</div>

	</s:form>

</div>

<div id="startManually"></div>
<div id="viewsummary"></div>
<div id="viewdocuments"></div>
<div id="updateStatus"></div>
<div id="endTask"></div>








<%-- <s:form action="EmpViewProject" name="frm_empproject_view" theme="simple" method="post">

		<div class="filter_div">
		<div class="filter_caption">Filter</div>
			<s:select label="Select Project" name="pro_id" listKey="projectID" headerKey="" headerValue="All Projects" listValue="projectName"
				list="projectdetailslist" key="" onchange="SendProId(this.value);" />
		</div>
		<input type="hidden" name="proType" id="proType" value="<%=proType != null ? proType : "L" %>"/>
		<div style="float: right; width: auto;"><%=uF.showData((String) request.getAttribute("MESSAGE"), "")%></div>

		<div class="clr"></div>
		<div style="margin: 10px 0px 0px 0px; float: left; width: 100%">
			<ul class="level_list">


		<%
			Set<String> setProjectMap = hmProject.keySet();
			Iterator<String> it = setProjectMap.iterator();
			
			while(it.hasNext()) {
				String strProjectId = it.next();
				List<String> alProjects = hmProject.get(strProjectId);
				if(alProjects == null) alProjects = new ArrayList<String>();
				
				List<List<String>> alTasks = (List<List<String>>) hmTasks.get(strProjectId);
					if(alTasks == null) alTasks = new ArrayList<List<String>>();
					
					%>
					
					<li class="post">
					<div style="float: left; <%=(uF.parseToInt((String)alProjects.get(11))==1)?"background-image: url(&quot;images1/icons/new2.gif&quot;);":""%> background-repeat: no-repeat; background-position: right top;">
					<p>
	                    <span style="float: left; width: 20px; height: 20px; margin-right: 5px; font-weight: bold; background-color: lightpink; text-align: center; font-size: 16px;" id="proLogoDiv">
	                    <% 
	                    	String proLogo = "";
	                    	if(alProjects.get(1) != null && alProjects.get(1).toString().length()>0) {
								proLogo = alProjects.get(1).toString().substring(0, 1);
		                    }
	                   	%> 
	                   	<span><%=proLogo %></span>
	                     </span>
	                    <strong> <%=alProjects.get(1) %>, <%=alProjects.get(10) %></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	                    <label title="<%=alProjects.get(4) %> to <%=alProjects.get(5) %>">Deadline<strong> <%=alProjects.get(5) %></strong></label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	                    Ideal Time:<strong> <%=alProjects.get(6) %></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	                    <strong> <%=uF.formatIntoComma(alTasks.size()) %></strong> Tasks&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	                    <strong> <%=uF.parseToInt(hmPDocumentCounter.get(strProjectId)) > 0 ? hmPDocumentCounter.get(strProjectId) : "No"%></strong> Documents&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
  					</p>
  
                    </div>
                    <div style="float:right;">
	                    <a class="viewdocticon" href="javascript:void(0)" onclick="viewDocuments(<%=strProjectId %>)" title="View Project Documents">View Project Documents</a>
                    </div>
                    <div class="clr"></div>
					<ul>
					<% if(proType == null || proType.equals("") || proType.equals("L")) { %>
						<li class="addnew"><a href="javascript:void(0)" onclick="addActivity(<%=strProjectId %>)">+ Add New Task</a> | <a href="javascript:void(0)" onclick="addMilestone(<%=strProjectId%>)">+ Add New Milestone</a> </li>
					<% } %>		
					<li class="desgn"> 
					<p class="past heading_dash" style="text-align:left; padding-left:35px;">Task List (click to expand) <span style="padding-left:10%">No of tasks: <%=uF.formatIntoComma(alTasks.size()) %></span></p>
					<div class="content2">
					<ul>
					<%
						for(int d=0; d<alTasks.size(); d++) {
						List<String> alInner = alTasks.get(d);	
						String strTaskId = alInner.get(0);
						String per = alInner.get(9); 
                        double percent = uF.parseToDouble(per);
                        List<List<String>> alSubTasks = new ArrayList<List<String>>();
                        if(hmSubTasks != null) {
        					alSubTasks = hmSubTasks.get(strTaskId);
                        }	
                    %>
						
                    <li style="border-bottom: 1px solid #CCCCCC;">
                    <% if((proType == null || proType.equals("") || proType.equals("L")) && uF.parseToBoolean(alInner.get(13))) { %>  
	                  <div style="float:right">
	                     <%if(alInner.get(10) != null && !alInner.get(10).equals("n") && percent >= 100) { %>
	                     <div class="cnfrmd"></div>  
	                     <%}else if(alInner.get(10) != null && alInner.get(10).equals("n") && percent >= 100) { %>
	                     <a href="javascript:void(0)" onclick="reAssign(<%=strTaskId%>)" title="Click to re-assign"><div class="completed"></div></a>
	                     <%} else if(alInner.get(11) != null && alInner.get(11).equals("New Task")) { %>
	                     <div></div>
	                     <%} else { %>
	                     <div class="nt_cnfrmd"></div>
	                     <%} %>
	                     
	                    
	                    <%if(uF.parseToDouble(alInner.get(12))>0) { %>
	                    	<a href="javascript:void(0)" class="del" title="Delete Task" onclick="alert('You can not delete this task as user has already booked the time against this task.')"> - </a>
	                    <% } else { %>
		                    <% if(alSubTasks != null && !alSubTasks.isEmpty() && alSubTasks.size()>0) { %>
		                    	<a href="javascript:void(0)" class="del" title="Delete Task" onclick="alert('Please first delete sub task of this task then delete this task.')"> - </a>
		                    <% } else { %>
		                    	<a href="AddNewActivity.action?operation=D&task_id=<%=alInner.get(0)%>" class="del" title="Delete Task" onclick="return confirm('Are you sure, you wish to delete this Task?')"> - </a>
		                    <% } %>
	                    <% } %> 
	                    
	                 	<a href="javascript:void(0)" class="edit_lvl" onclick="editActivity(<%=alInner.get(0)%>,<%=strProjectId%>);" title="Edit Task">Edit</a>
	                 </div> 
					<% } %>
					<%=alInner.get(1)%>
					
					<%
						String strColour = null;
						if(uF.parseToInt(alInner.get(4))==2) {
							strColour = "red";
						} else if(uF.parseToInt(alInner.get(4))==1) {
							strColour = "yellow";
						} else if(uF.parseToInt(alInner.get(4))==0) {
							strColour = "green";
						} else {
							strColour = "";
						}
					%>
					
                    Task Name: <strong><%=alInner.get(2)%></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    <span style="padding:3px; background-color:<%=strColour%>">Priority: <strong><%=alInner.get(3)%></strong></span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    Assigned To: <strong><%=alInner.get(5)%></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                   <p style="margin:0px 0px 0px 60px">
	                    Deadline: <strong><%=alInner.get(6)%></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	                    Estimated Time: <strong><%=alInner.get(7)%> </strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	                    Worked: <strong><%=alInner.get(8)%></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	                    Completion Status: <strong><span id="myPer<%=alInner.get(0)%>"><%=alInner.get(9)%></span> % </strong>
	                    <% if(proType == null || proType.equals("") || proType.equals("L")) { %>
		                    <% if(alSubTasks == null || alSubTasks.isEmpty() || alSubTasks.size() == 0) { %>
		                   		<a href="javascript:void(0)" onclick="updateStatus(<%=alInner.get(0)%>,<%=strProjectId %>)"><img src="images1/icons/popup_arrow.gif" title="Update Completion Status" height="8px"/></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		                    <% } %>
	                    <% } %>
                   </p>
                   
                   <% if(alSubTasks == null || alSubTasks.isEmpty() || alSubTasks.size() == 0) { %>
                   <ul>
						<li class="desgn" style="padding-left: 0px;">
						<p class="past heading" style="text-align:left;padding-left:35px;">Activities (click to collapse)</p>
					   <div class="content1">
							<ul>

							<%
								List<List<String>> alActivities = hmActivities.get(strTaskId);
								boolean isEnd = false;		
								if (alActivities != null) {
									//System.out.println("strTaskId --> "+strTaskId+" alActivities --->> " + alActivities);
									for (int i = 0; i<alActivities.size(); i++) {
										List<String> innerList = alActivities.get(i);
							%>
								<li><%=innerList.get(1)%> <%=innerList.get(2)%> to <%=uF.showData((String) innerList.get(3), " working ")%>, 
									<strong><%=innerList.get(4)%></strong> actual hrs, <%=innerList.get(5)%>, with <strong><%=innerList.get(6)%></strong> billable hrs, was <%=innerList.get(7)%>
								
								Date: <strong><%=innerList.get(1)%></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								Start Time: <strong><%=innerList.get(2)%></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								<% if ((String) innerList.get(3) == null) { %>
								
								<% session.setAttribute("TaskID", strTaskId); %>
								<% } %>
								End Time: <strong><%=uF.showData((String) innerList.get(3), " working ")%></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								Total Time: <strong><%=innerList.get(4)%></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;

								<%
									if (percent < 100) {
										if (innerList.get(3) == null) {
											isEnd = true;
								%>
							<% if(proType == null || proType.equals("") || proType.equals("L")) { %>	
								<a onclick="endTask('<%=strTaskId%>', '<%=strProjectId%>', '<%=innerList.get(0) %>')" href="javascript:void(0);">End this task</a>
							<% } %>
								<a onclick="return hs.htmlExpand(this, { objectType: 'ajax',width:700 })" href="EndTaskPopup.action?id=<%=strTaskId%>&pro_id=<%=strProjectId%>">End this task</a>
							<% } %>
								</li>

								<% } } } %>

								<%
									if (percent < 100) {
										if (!isEnd) {
										if ((Boolean) request.getAttribute("flag")) {
								%>
								<% if(proType == null || proType.equals("") || proType.equals("L")) { %>
									<% if(alSubTasks == null || alSubTasks.isEmpty() || alSubTasks.size() == 0) { %>
										<%if(hmTodayApprovedActivity != null && hmTodayApprovedActivity.get(strTaskId) != null && uF.parseToInt(hmTodayApprovedActivity.get(strTaskId)) == 2) { %>
										<%if(timeApproveFlag) { %>
											<li><a href="javascript:void(0)" onclick="alert('Timesheet for this date is already approved.') ">Start this task</a> |
											<a href="javascript:void(0)"onclick="if(confirm('You are already working on one activity, would you like to finish that activity by starting the new activity?')) {startManually(<%=strTaskId%>,<%=strProjectId%>);} ">Start Manually</a></li>
										<% } else { %>
											<li><a href="javascript:void(0)" onclick="if(confirm('You are already working on one activity, would you like to finish that activity by starting the new activity?')) {start123(<%=strTaskId%>,<%=strProjectId%>);} ">Start this task</a> | 
												<a href="javascript:void(0)"onclick="if(confirm('You are already working on one activity, would you like to finish that activity by starting the new activity?')) {startManually(<%=strTaskId%>,<%=strProjectId%>);} ">Start Manually</a>
											</li>
										<% } %>
									<% } %>	
								<% } %>
								<% } else { %>
									<% if(proType == null || proType.equals("") || proType.equals("L")) { %>
										<% if(alSubTasks == null || alSubTasks.isEmpty() || alSubTasks.size() == 0) { %>
											<%if(hmTodayApprovedActivity != null && hmTodayApprovedActivity.get(strTaskId) != null && uF.parseToInt(hmTodayApprovedActivity.get(strTaskId)) == 2) { %>
											<%if(timeApproveFlag) { %>
												<li><a href="javascript:void(0)" onclick="alert('Timesheet for this date is already approved.') ">Start this task</a> |
												<a href="javascript:void(0)" onclick="startManually(<%=strTaskId%>,<%=strProjectId%>);">Start Manually</a></li>
											<% } else { %>
												<li><a href="javascript:void(0)" onclick="start123(<%=strTaskId%>,<%=strProjectId%>);">Start this task</a> |
													<a href="javascript:void(0)" onclick="startManually(<%=strTaskId%>,<%=strProjectId%>);">Start Manually</a>	
												</li>
											<% } %>	
										<% } %>
									<% } %>
								<% } %>

								<li><a href="javascript:void(0)" onclick="start123(<%=strTaskId%>);">Start this task</a></li>

								<% } } %>
							</ul>
							
							</div>
							
							</li>
					</ul>
					<% } %>
					
					</li>
				
					
				<% if(alSubTasks != null) { %> 
				
				<% if(proType == null || proType.equals("") || proType.equals("L")) { %>
					<li class="addnew"><a href="javascript:void(0)" onclick="addSubTask(<%=strProjectId %>, <%=strTaskId %>);">+ Add New Sub Task</a></li>
				<% } %>
				
				<%	
					for(int j=0; alSubTasks != null && j<alSubTasks.size(); j++) {
						List<String> innerList = alSubTasks.get(j);
						String strSubTaskId = innerList.get(0);
						String subPer = innerList.get(9); 
                        double subPercent = uF.parseToDouble(subPer);
				%>
				<li style="border-bottom: 1px solid #CCCCCC; margin-left: 100px;">
				
				<% if((proType == null || proType.equals("") || proType.equals("L")) && uF.parseToBoolean(innerList.get(13))) { %>  
	                  <div style="float: right;">
	                     <%if(innerList.get(10) != null && !innerList.get(10).equals("n") && subPercent >= 100) { %>
	                     <div class="cnfrmd"></div>  
	                     <% } else if(innerList.get(10) != null && innerList.get(10).equals("n") && subPercent >= 100) { %>
	                     <a href="javascript:void(0)" onclick="reAssign(<%=strSubTaskId %>)" title="Click to re-assign"><div class="completed"></div></a>
	                     <% } else if(innerList.get(11) != null && innerList.get(11).equals("New Task")) { %>
	                     <div></div>
	                     <% } else { %>
	                     <div class="nt_cnfrmd"></div>
	                     <% } %>
	                    
	                     
	                    <%if(uF.parseToDouble(innerList.get(12))>0) { %>
	                    	<a href="javascript:void(0)" class="del" title="Delete Sub Task" onclick="alert('You can not delete this task as user has already booked the time against this sub task.')"> - </a>
	                    <% } else { %>
	                    	<a href="AddNewActivity.action?operation=D&task_id=<%=innerList.get(0)%>" class="del" title="Delete Sub Task" onclick="return confirm('Are you sure, you wish to delete this sub task?')"> - </a>
	                    <% } %> 
	                 		<a href="javascript:void(0)" class="edit_lvl" onclick="editSubTask(<%=strProjectId %>, <%=alInner.get(0) %>, <%=innerList.get(0) %>);" title="Edit Sub Task">Edit</a>
	                 </div> 
						<%=innerList.get(1) %>
					<% } %>
					
					<%
						if(uF.parseToInt(innerList.get(4))==2) {
							strColour = "red";
						} else if(uF.parseToInt(innerList.get(4))==1) {
							strColour = "yellow";
						} else if(uF.parseToInt(innerList.get(4))==0) {
							strColour = "green";
						} else {
							strColour = "";
						}
					%>
					
                    Sub Task Name: <strong><%=innerList.get(2)%></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    <span style="padding:3px;background-color:<%=strColour%>">Priority: <strong><%=innerList.get(3)%></strong></span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    Assigned To: <strong><%=innerList.get(5)%></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                   	<p style="margin:0px 0px 0px 60px">
	                    Deadline: <strong><%=innerList.get(6)%></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	                    Estimated Time: <strong><%=innerList.get(7)%> </strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	                    Worked: <strong><%=innerList.get(8)%></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	                    Completion Status: <strong><span id="myPer<%=innerList.get(0)%>"><%=innerList.get(9)%></span> % </strong>
	                    <% if(proType == null || proType.equals("") || proType.equals("L")) { %>
	                    	<a href="javascript:void(0)" onclick="updateStatus(<%=innerList.get(0)%>)"><img src="images1/icons/popup_arrow.gif" title="Update Completion Status" height="8px"/></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	                    <% } %>
                   </p>
                   
                   
                   <ul>
						<li class="desgn" style="padding-left: 0px;">
						<p class="past heading" style="text-align:left;padding-left:35px;">Activities (click to collapse)</p>
					   <div class="content1">
							<ul>

							<%
								List<List<String>> alSubTaskActivities = hmActivities.get(strSubTaskId);
								boolean subTaskIsEnd = false;		
								if (alSubTaskActivities != null) {
									//System.out.println("strSubTaskId --> "+strSubTaskId+" alSubTaskActivities --->> " + alSubTaskActivities);
									for (int i = 0; i<alSubTaskActivities.size(); i++) {
										List<String> subTaskInnerList = alSubTaskActivities.get(i);
							%>
								<li><%=subTaskInnerList.get(1)%> <%=subTaskInnerList.get(2)%> to <%=uF.showData((String) subTaskInnerList.get(3), " working ")%>, 
									<strong><%=subTaskInnerList.get(4)%></strong> actual hrs, <%=subTaskInnerList.get(5)%>, with <strong><%=subTaskInnerList.get(6)%></strong> billable hrs, was <%=subTaskInnerList.get(7)%>.
								Date: <strong><%=subTaskInnerList.get(1)%></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								Start Time: <strong><%=subTaskInnerList.get(2)%></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								<% if ((String) subTaskInnerList.get(3) == null) { %>
								
								<% session.setAttribute("TaskID", strTaskId); %>
								<% } %>
								End Time: <strong><%=uF.showData((String) subTaskInnerList.get(3), " working ")%></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								Total Time: <strong><%=subTaskInnerList.get(4)%></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;

								<%
									if (subPercent < 100) {
										if (subTaskInnerList.get(3) == null) {
											subTaskIsEnd = true;
								%>
								<% if(proType == null || proType.equals("") || proType.equals("L")) { %>
									<a onclick="endTask('<%=strSubTaskId%>', '<%=strProjectId%>', '<%=subTaskInnerList.get(0) %>')" href="javascript:void(0);">End this task</a>
								<% } %>
								<a onclick="return hs.htmlExpand(this, { objectType: 'ajax',width:700 })" href="EndTaskPopup.action?id=<%=strSubTaskId%>&pro_id=<%=strProjectId%>">End this task</a>
							<% } %>
								</li>

								<% } } } %>

								<%
									if (percent < 100) {
										if (!subTaskIsEnd) {
										if ((Boolean) request.getAttribute("flag")) {
								%>
								<% if(proType == null || proType.equals("") || proType.equals("L")) { %>
									<%if(hmTodayApprovedActivity != null && hmTodayApprovedActivity.get(strSubTaskId) != null && uF.parseToInt(hmTodayApprovedActivity.get(strSubTaskId)) == 2) { %>
									<%if(timeApproveFlag) { %>
										<li><a href="javascript:void(0)" onclick="alert('Timesheet for this date is already approved.') ">Start this sub task</a> |
										<a href="javascript:void(0)"onclick="if(confirm('You are already working on one activity, would you like to finish that activity by starting the new activity?')) {startManually(<%=strSubTaskId%>,<%=strProjectId%>);} ">Start Manually</a></li>
									<% } else { %>
										<li><a href="javascript:void(0)" onclick="if(confirm('You are already working on one activity, would you like to finish that activity by starting the new activity?')) {start123(<%=strSubTaskId%>,<%=strProjectId%>);} ">Start this sub task</a> | 
											<a href="javascript:void(0)"onclick="if(confirm('You are already working on one activity, would you like to finish that activity by starting the new activity?')) {startManually(<%=strSubTaskId%>,<%=strProjectId%>);} ">Start Manually</a>
										</li>
									<% } %>
								<% } %>
								<% } else { %>
									<% if(proType == null || proType.equals("") || proType.equals("L")) { %>
										<%if(hmTodayApprovedActivity != null && hmTodayApprovedActivity.get(strSubTaskId) != null && uF.parseToInt(hmTodayApprovedActivity.get(strSubTaskId)) == 2) { %>
										<%if(timeApproveFlag) { %>
												<li><a href="javascript:void(0)" onclick="alert('Timesheet for this date is already approved.') ">Start this sub task</a> |
												<a href="javascript:void(0)" onclick="startManually(<%=strSubTaskId%>,<%=strProjectId%>);">Start Manually</a></li>
											<% } else { %>
												<li><a href="javascript:void(0)" onclick="start123(<%=strSubTaskId%>,<%=strProjectId%>);">Start this sub task</a> |
													<a href="javascript:void(0)" onclick="startManually(<%=strSubTaskId%>,<%=strProjectId%>);">Start Manually</a>	
												</li>
											<% } %>	
										
									<% } %>
								<% } %>
								<li><a href="javascript:void(0)" onclick="start123(<%=strSubTaskId%>);">Start this task</a></li>

								<% } } %>
							</ul>
							
							</div>
							
							</li>
					</ul>
					</li>
					
					<% } } %>
					
				<% } %>	
				<% if(alTasks == null || alTasks.size() == 0) { %>
				<li><span>No task is assigned to you.</span> </li>
				<% } %>
							</ul>
							
							</div>
							
						</li>
					</ul>
                 </li> 
		<% } %>
		
				<%
					Set setTaskMap = hmTasks.keySet();
					Iterator itTask = setTaskMap.iterator();

					while (itTask.hasNext()) {
						List alTasks = (List) hmTasks.get(itTask.next());
						if (alTasks == null)
							alTasks = new ArrayList();

						for (int d = 0; d < alTasks.size(); d += 15) {
							String strTaskId = (String) alTasks.get(d);
							String strProjectId = (String) alTasks.get(d + 1);
				%>

				<li>
					<%
						String strColour = null;
							if (uF.parseToInt((String) alTasks.get(d + 6)) == 2) {
								strColour = "red";
							} else if (uF.parseToInt((String) alTasks.get(d + 6)) == 1) {
								strColour = "yellow";
							} else if (uF.parseToInt((String) alTasks.get(d + 6)) == 0) {
								strColour = "green";
							} else {
								strColour = "";
							}
					%> 
					<div style="float: left; <%=(uF.parseToInt((String)alTasks.get(d+12))==1)?"background-image: url(&quot;images1/icons/new2.gif&quot;);":""%> background-repeat: no-repeat; background-position: right top;">
					<p>
					<%=alTasks.get(d + 2)%> <%=alTasks.get(d+2)%> 
					Task Name:<strong><%=alTasks.get(d + 4)%></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					<span style="padding:3px;background-color:<%=strColour%>">
					Priority:<strong><%=alTasks.get(d + 5)%></strong></span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
					Assigned To: <strong><%=alTasks.get(d + 7)%></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					Project: <strong><%=alTasks.get(d + 13)%></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					Client: <strong><%=alTasks.get(d + 14)%></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				</p>
				<p style="margin:0px 0px 0px 50px">   
					Deadline: <strong><%=alTasks.get(d + 8)%></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					Estimated Time: <strong><%=alTasks.get(d + 9)%></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					Worked: <strong><%=alTasks.get(d + 10)%></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					Completion Status: <strong><%=alTasks.get(d + 11)%> %</strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				</p>
				</div>
				<div style="float:right">
					<a class="viewdocticon" href="javascript:void(0)" onclick="viewDocuments(<%=strProjectId%>)" title="View Documents">View Project Documents</a> 
				</div>	
				<div class="clr"></div>	
		<%
 			String per = (String) alTasks.get(d + 11);
 			double percent = uF.parseToDouble(per);
 		%>

					<ul>
						<li class="desgn">
						<p class="past heading" style="text-align:left;padding-left:35px;">Activities(click to collapse)</p>
					   <div class="content1">
							<ul>

							<%
								List<List<String>> alActivities = hmActivities.get(strTaskId);
								boolean isEnd = false;		
								if (alActivities != null) { 
									for (int i = 0; i<alActivities.size(); i++) {
										List<String> innerList = alActivities.get(i);
							%>
								<li>
								
								Date: <strong><%=innerList.get(1)%></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								Start Time: <strong><%=innerList.get(2)%></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								<% if ((String) innerList.get(3) == null) { %>
								
								<% session.setAttribute("TaskID", strTaskId); %>
								<% } %>
								End Time: <strong><%=uF.showData((String) innerList.get(3), " working ")%></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								Total Time: <strong><%=innerList.get(4)%></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;

								<%
									if (percent < 100) {
										if (innerList.get(3) == null) {
											isEnd = true;
								%>
								<a onclick="return hs.htmlExpand(this, { objectType: 'ajax',width:700 })" href="EndTaskPopup.action?id=<%=strTaskId%>&pro_id=<%=strProjectId%>">End this task</a>
							<% } %>
								</li>

								<% } } } %>

								<%
									if (percent < 100) {
										if (!isEnd) {
										if ((Boolean) request.getAttribute("flag")) {
								%>
								<li><a href="javascript:void(0)" onclick="if(confirm('You are already working on one activity, would you like to finish that activity by starting the new activity?')) {start123(<%=strTaskId%>,<%=strProjectId%>);} ">Start this task</a> | 
									<a href="javascript:void(0)"onclick="if(confirm('You are already working on one activity, would you like to finish that activity by starting the new activity?')) {startManually(<%=strTaskId%>,<%=strProjectId%>);} ">Start Manually</a>
								</li>

								<% } else { %>
								<li><a href="javascript:void(0)" onclick="start123(<%=strTaskId%>,<%=strProjectId%>);">Start this task</a> |
									<a href="javascript:void(0)" onclick="startManually(<%=strTaskId%>,<%=strProjectId%>);">Start Manually</a>	
								</li>
								<% } %>

								<li><a href="javascript:void(0)" onclick="start123(<%=strTaskId%>);">Start this task</a></li>

								<% } } %>
							</ul>
							
							</div>
							
							</li>
					</ul></li>
				<% } } %>
			</ul>

			<% if (hmTasks != null && hmTasks.size() == 0) { %>
			<div class="msg nodata"><span>No task is assigned to you.</span></div>
			<% } %>

		</div>

	</s:form> --%>