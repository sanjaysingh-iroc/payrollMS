
<%@page import="com.konnect.jpms.select.FillSkills"%>
<%@page import="com.konnect.jpms.task.FillTaskEmpList"%>
<%@page import="com.konnect.jpms.task.GetPriorityList"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@page import="java.util.*"%>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>

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

<g:compress>

<script> 
	function addProject() {

		removeLoadingDiv('the_div');
		
		var dialogEdit = '#addproject'; 
		dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
		$(dialogEdit).dialog({
			autoOpen : false,
			bgiframe : true,
			resizable : false, 
			height : 650,
			width : 800,  
			modal : true,
			title : 'Add New Project',
			open : function() {
				var xhr = $.ajax({
					url : "PreAddNewProjectPopup.action",
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
	
	
	var dialogEdit = '#updateStatus';
	function updateStatus(taskId, proId, divId) {
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
	                	//alert("data ===>> " + data+" divId ==>> " + divId+ " proId ===>> " + proId);
	                	document.getElementById(divId+'Percent_'+proId+'_'+taskId).innerHTML = allData[0] +'%';
	                	document.getElementById(divId+'_'+proId+'_'+taskId).innerHTML = allData[1];
	                	//document.getElementById(divId+'_'+proId).style.width = data +'%;';
	                	//$(divId).html(data);
	                }
	            });
	    }
		$(dialogEdit).dialog('close');
	}
	
	
	
	var dialogEdit1 = '#addTaskDescription';
	function updateTaskDescription(proId, cnt, divId, type) {
		removeLoadingDiv('the_div');
		var strTitle = "Task";
		if(type == 'ST') {
			strTitle = "Sub Task";
		}
		//taskDescription
		var taskDescription = document.getElementById(divId+proId+'_'+cnt).value;
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
					url : "AddTaskDescription.action?proId="+proId+"&divId="+divId+"&count="+cnt+"&taskDescription="+encodeURIComponent(taskDescription),
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
			
			
	function addTaskDescription(description, proId, divId, cnt, fromPage) {
		document.getElementById(divId+proId+'_'+cnt).value = description;

    	$(dialogEdit1).dialog('close');
	}
			
			
			
	function viewSummary(id, proType, pageType) {
		//  
		var status = document.getElementById('proSummarySpanStatus'+id).value;
		if(status == '0') {
			document.getElementById('proSummarySpanStatus'+id).value = '1';
			document.getElementById('proSummaryDownarrowSpan'+id).style.display = 'none';
			document.getElementById('proSummaryUparrowSpan'+id).style.display = 'block';
			
			document.getElementById('hideResourceSpanStatus'+id).value = '0';
			document.getElementById('resourceDownarrowSpan'+id).style.display = 'block';
			document.getElementById('resourceUparrowSpan'+id).style.display = 'none';
			
			document.getElementById('proTaskSpanStatus'+id).value = '0';
			document.getElementById('proTaskDownarrowSpan'+id).style.display = 'block';
			document.getElementById('proTaskUparrowSpan'+id).style.display = 'none';
			
			if(document.getElementById('proProfitSpanStatus'+id)) {
				document.getElementById('proProfitSpanStatus'+id).value = '0';
				document.getElementById('proProfitDownarrowSpan'+id).style.display = 'block';
				document.getElementById('proProfitUparrowSpan'+id).style.display = 'none';
			}
			document.getElementById('proDocsSpanStatus'+id).value = '0';
			document.getElementById('proDocsDownarrowSpan'+id).style.display = 'block';
			document.getElementById('proDocsUparrowSpan'+id).style.display = 'none';
			
			if(document.getElementById('proMilesSpanStatus'+id)) {
				document.getElementById('proMilesSpanStatus'+id).value = '0';
				document.getElementById('proMilesDownarrowSpan'+id).style.display = 'block';
				document.getElementById('proMilesUparrowSpan'+id).style.display = 'none';
			}
			
			document.getElementById('proUL_'+id).style.display = 'block';
           	document.getElementById('proSummaryDiv_'+id).style.display = 'block';
           	$('<img src=\"images1/ajax-loading-1.gif\"/>').appendTo("#proSummaryDiv_"+id);
           	
           	document.getElementById('proResourcesDiv_'+id).style.display = 'none';
			document.getElementById('proMilestoneDiv_'+id).style.display = 'none';
           	if(document.getElementById('proCostSummaryDiv_'+id)) {
				document.getElementById('proCostSummaryDiv_'+id).style.display = 'none';
           	}
           	document.getElementById('proDocsDiv_'+id).style.display = 'none';
           	document.getElementById('taskSubTaskDiv_'+id).style.display = 'none';
           	
           	getContent('proSummaryDiv_'+id, 'ProjectSummaryView.action?pro_id='+id+'&proType='+proType+'&pageType='+pageType);
		} else {
			document.getElementById('proSummarySpanStatus'+id).value = '0';
			document.getElementById('proSummaryDownarrowSpan'+id).style.display = 'block';
			document.getElementById('proSummaryUparrowSpan'+id).style.display = 'none';
			
			document.getElementById('proSummaryDiv_'+id).innerHTML = '';
			document.getElementById('proSummaryDiv_'+id).style.display = 'none';
			document.getElementById('proUL_'+id).style.display = 'none';
		}
	}
	
	
	function getProjectResources(id, pageType) {
		//alert("proId ===>> " + proId);   
		var status = document.getElementById('hideResourceSpanStatus'+id).value;
		if(status == '0') {
			
			/* $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('#proResourcesDiv_'+id); */
			document.getElementById('hideResourceSpanStatus'+id).value = '1';
			document.getElementById('resourceDownarrowSpan'+id).style.display = 'none';
			document.getElementById('resourceUparrowSpan'+id).style.display = 'block';
			
			document.getElementById('proTaskSpanStatus'+id).value = '0';
			document.getElementById('proTaskDownarrowSpan'+id).style.display = 'block';
			document.getElementById('proTaskUparrowSpan'+id).style.display = 'none';
			
			if(document.getElementById('proProfitSpanStatus'+id)) {
				document.getElementById('proProfitSpanStatus'+id).value = '0';
				document.getElementById('proProfitDownarrowSpan'+id).style.display = 'block';
				document.getElementById('proProfitUparrowSpan'+id).style.display = 'none';
			}
			
			document.getElementById('proDocsSpanStatus'+id).value = '0';
			document.getElementById('proDocsDownarrowSpan'+id).style.display = 'block';
			document.getElementById('proDocsUparrowSpan'+id).style.display = 'none';
			
			document.getElementById('proSummarySpanStatus'+id).value = '0';
			document.getElementById('proSummaryDownarrowSpan'+id).style.display = 'block';
			document.getElementById('proSummaryUparrowSpan'+id).style.display = 'none';
			
			if(document.getElementById('proMilesSpanStatus'+id)) {
				document.getElementById('proMilesSpanStatus'+id).value = '0';
				document.getElementById('proMilesDownarrowSpan'+id).style.display = 'block';
				document.getElementById('proMilesUparrowSpan'+id).style.display = 'none';
			}
			
			document.getElementById('proUL_'+id).style.display = 'block';
			document.getElementById('proResourcesDiv_'+id).style.display = 'block';
			
			$('<img src=\"images1/ajax-loading-1.gif\"/>').appendTo("#proResourcesDiv_"+id);
			/* document.getElementById('proResourcesDiv_'+id).style.backgroundImage = 'url("../images1/ajax-loading-1.gif") no-repeat scroll 0 0 transparent'; */
			
			document.getElementById('proMilestoneDiv_'+id).style.display = 'none';
			if(document.getElementById('proCostSummaryDiv_'+id)) {
				document.getElementById('proCostSummaryDiv_'+id).style.display = 'none';
			}
	    	document.getElementById('proSummaryDiv_'+id).style.display = 'none';
	    	document.getElementById('proDocsDiv_'+id).style.display = 'none';
	    	document.getElementById('taskSubTaskDiv_'+id).style.display = 'none';
			getContent('proResourcesDiv_'+id, 'GetTeamInfoAjax.action?proId='+ id +'&fromPage=VAP&pageType='+pageType);
		} else {
			document.getElementById('hideResourceSpanStatus'+id).value = '0';
			document.getElementById('resourceDownarrowSpan'+id).style.display = 'block';
			document.getElementById('resourceUparrowSpan'+id).style.display = 'none';
			
			document.getElementById('proResourcesDiv_'+id).innerHTML = '';
			document.getElementById('proResourcesDiv_'+id).style.display = 'none';
			document.getElementById('proUL_'+id).style.display = 'none';
		}
	}
	
	
	function viewGanttChart(id) {
		removeLoadingDiv('the_div');
		var dialogEdit = '#viewganttchart';
		dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
		$(dialogEdit).dialog({
			autoOpen : false,
			bgiframe : true,
			resizable : false,
			height : 520,
			width : '95%',
			modal : true,
			title : 'Project Gantt Chart',
			open : function() {
				var xhr = $.ajax({
					url : "ProjectGanttChart.action?pro_id="+id,
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
	
	
	/* function viewSummary(id) {
		removeLoadingDiv('the_div');
		var dialogEdit = '#viewsummary';
		dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
		$(dialogEdit).dialog({
			autoOpen : false,
			bgiframe : true,
			resizable : false,
			height : 1020,
			width : '95%',
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
	} */
	
	
	function viewCostSummary(id, pageType) {
		//   
		var status = document.getElementById('proProfitSpanStatus'+id).value;
		if(status == '0') {
			document.getElementById('proProfitSpanStatus'+id).value = '1';
			document.getElementById('proProfitDownarrowSpan'+id).style.display = 'none';
			document.getElementById('proProfitUparrowSpan'+id).style.display = 'block';
			
			document.getElementById('proDocsSpanStatus'+id).value = '0';
			document.getElementById('proDocsDownarrowSpan'+id).style.display = 'block';
			document.getElementById('proDocsUparrowSpan'+id).style.display = 'none';
			
			document.getElementById('proTaskSpanStatus'+id).value = '0';
			document.getElementById('proTaskDownarrowSpan'+id).style.display = 'block';
			document.getElementById('proTaskUparrowSpan'+id).style.display = 'none';
			
			document.getElementById('hideResourceSpanStatus'+id).value = '0';
			document.getElementById('resourceDownarrowSpan'+id).style.display = 'block';
			document.getElementById('resourceUparrowSpan'+id).style.display = 'none';
			
			document.getElementById('proSummarySpanStatus'+id).value = '0';
			document.getElementById('proSummaryDownarrowSpan'+id).style.display = 'block';
			document.getElementById('proSummaryUparrowSpan'+id).style.display = 'none';
			
			if(document.getElementById('proMilesSpanStatus'+id)) {
				document.getElementById('proMilesSpanStatus'+id).value = '0';
				document.getElementById('proMilesDownarrowSpan'+id).style.display = 'block';
				document.getElementById('proMilesUparrowSpan'+id).style.display = 'none';
			}
			
			document.getElementById('proUL_'+id).style.display = 'block';
			if(document.getElementById('proCostSummaryDiv_'+id)) {
				document.getElementById('proCostSummaryDiv_'+id).style.display = 'block';
			}
           	$('<img src=\"images1/ajax-loading-1.gif\"/>').appendTo("#proCostSummaryDiv_"+id);
           	
           	document.getElementById('proResourcesDiv_'+id).style.display = 'none';
			document.getElementById('proMilestoneDiv_'+id).style.display = 'none';
           	document.getElementById('proSummaryDiv_'+id).style.display = 'none';
           	document.getElementById('proDocsDiv_'+id).style.display = 'none';
           	document.getElementById('taskSubTaskDiv_'+id).style.display = 'none';
           	
           	getContent('proCostSummaryDiv_'+id, 'ProjectCostSummary.action?pro_id='+id+'&pageType='+pageType);
		} else {
			if(document.getElementById('proProfitSpanStatus'+id)) {
				document.getElementById('proProfitSpanStatus'+id).value = '0';
				document.getElementById('proProfitDownarrowSpan'+id).style.display = 'block';
				document.getElementById('proProfitUparrowSpan'+id).style.display = 'none';
			}
			
			if(document.getElementById('proCostSummaryDiv_'+id)) {
				document.getElementById('proCostSummaryDiv_'+id).innerHTML = '';
				document.getElementById('proCostSummaryDiv_'+id).style.display = 'none';
			}
			document.getElementById('proUL_'+id).style.display = 'none';
		}   	
		
	}
	
	
	function viewProTasks(id) {
		//  
		var status = document.getElementById('proTaskSpanStatus'+id).value;
		if(status == '0') {
			document.getElementById('proTaskSpanStatus'+id).value = '1';
			document.getElementById('proTaskDownarrowSpan'+id).style.display = 'none';
			document.getElementById('proTaskUparrowSpan'+id).style.display = 'block';
			
			if(document.getElementById('proProfitSpanStatus'+id)) {
				document.getElementById('proProfitSpanStatus'+id).value = '0';
				document.getElementById('proProfitDownarrowSpan'+id).style.display = 'block';
				document.getElementById('proProfitUparrowSpan'+id).style.display = 'none';
			}
			
			document.getElementById('proDocsSpanStatus'+id).value = '0';
			document.getElementById('proDocsDownarrowSpan'+id).style.display = 'block';
			document.getElementById('proDocsUparrowSpan'+id).style.display = 'none';
			
			document.getElementById('hideResourceSpanStatus'+id).value = '0';
			document.getElementById('resourceDownarrowSpan'+id).style.display = 'block';
			document.getElementById('resourceUparrowSpan'+id).style.display = 'none';
			
			document.getElementById('proSummarySpanStatus'+id).value = '0';
			document.getElementById('proSummaryDownarrowSpan'+id).style.display = 'block';
			document.getElementById('proSummaryUparrowSpan'+id).style.display = 'none';
			
			if(document.getElementById('proMilesSpanStatus'+id)) {
				document.getElementById('proMilesSpanStatus'+id).value = '0';
				document.getElementById('proMilesDownarrowSpan'+id).style.display = 'block';
				document.getElementById('proMilesUparrowSpan'+id).style.display = 'none';
			}
			
			document.getElementById('proUL_'+id).style.display = 'block';
			document.getElementById('proResourcesDiv_'+id).style.display = 'none';
			document.getElementById('proMilestoneDiv_'+id).style.display = 'none';
			if(document.getElementById('proCostSummaryDiv_'+id)) {
				document.getElementById('proCostSummaryDiv_'+id).style.display = 'none';
			}
	    	document.getElementById('proSummaryDiv_'+id).style.display = 'none';
	    	document.getElementById('proDocsDiv_'+id).style.display = 'none';
	    	document.getElementById('taskSubTaskDiv_'+id).style.display = 'block';
	    	//$('<img src=\"images1/ajax-loading-1.gif\"/>').appendTo("#taskSubTaskDiv_"+id);
		} else {
			document.getElementById('proTaskSpanStatus'+id).value = '0';
			document.getElementById('proTaskDownarrowSpan'+id).style.display = 'block';
			document.getElementById('proTaskUparrowSpan'+id).style.display = 'none';
			
			//document.getElementById('taskSubTaskDiv_'+id).innerHTML = '';
			document.getElementById('taskSubTaskDiv_'+id).style.display = 'none';
			document.getElementById('proUL_'+id).style.display = 'none';
		}
	}
	
	
	function viewAllProjectsSummary() {
		removeLoadingDiv('the_div');
		var dialogEdit = '#viewprojectsummary';
		dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
		$(dialogEdit).dialog({
			autoOpen : false,
			bgiframe : true,
			resizable : false,
			height : 600,
			width : 800,
			modal : true,
			title : 'Gantt Chart of all Projects',
			open : function() {
				var xhr = $.ajax({
					url : "AllProjectsSummaryView.action?operation=All",
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
	
	
	/* function viewAllProjectsSummarySeperate() {

		var dialogEdit = '#viewsummary';
		dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
		$(dialogEdit).dialog({
			autoOpen : false,
			bgiframe : true,
			resizable : false,
			height : 1000,
			width : 1000,
			modal : true,
			title : 'Gantt Chart of all Projects',
			open : function() {
				var xhr = $.ajax({
					url : "AllProjectsSummaryView.action?operation=seperate",
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

	
	/* function viewDocuments(id) {
		removeLoadingDiv('the_div');
		var dialogEdit = '#viewdocuments';
		dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
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
					url : "ProjectDocumentView.action?pro_id="+id+"&type=Project",
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
	
	
	function viewDocuments(id, proType, pageType) {
		//alert("proId ===>> " + id);    
		var status = document.getElementById('proDocsSpanStatus'+id).value;
		if(status == '0') {
			document.getElementById('proDocsSpanStatus'+id).value = '1';
			document.getElementById('proDocsDownarrowSpan'+id).style.display = 'none';
			document.getElementById('proDocsUparrowSpan'+id).style.display = 'block';
			
			document.getElementById('proTaskSpanStatus'+id).value = '0';
			document.getElementById('proTaskDownarrowSpan'+id).style.display = 'block';
			document.getElementById('proTaskUparrowSpan'+id).style.display = 'none';
			
			document.getElementById('hideResourceSpanStatus'+id).value = '0';
			document.getElementById('resourceDownarrowSpan'+id).style.display = 'block';
			document.getElementById('resourceUparrowSpan'+id).style.display = 'none';
			
			document.getElementById('proSummarySpanStatus'+id).value = '0';
			document.getElementById('proSummaryDownarrowSpan'+id).style.display = 'block';
			document.getElementById('proSummaryUparrowSpan'+id).style.display = 'none';
			
			if(document.getElementById('proMilesSpanStatus'+id)) {
				document.getElementById('proMilesSpanStatus'+id).value = '0';
				document.getElementById('proMilesDownarrowSpan'+id).style.display = 'block';
				document.getElementById('proMilesUparrowSpan'+id).style.display = 'none';
			}
			
			document.getElementById('proUL_'+id).style.display = 'block';
			document.getElementById('proResourcesDiv_'+id).style.display = 'none';
			document.getElementById('proMilestoneDiv_'+id).style.display = 'none';
			if(document.getElementById('proCostSummaryDiv_'+id)) {
				document.getElementById('proCostSummaryDiv_'+id).style.display = 'none';
			}
	    	document.getElementById('proSummaryDiv_'+id).style.display = 'none';
	    	document.getElementById('proDocsDiv_'+id).style.display = 'block';
	    	$('<img src=\"images1/ajax-loading-1.gif\"/>').appendTo("#proDocsDiv_"+id);
	    	
	    	document.getElementById('taskSubTaskDiv_'+id).style.display = 'none';
			getContent('proDocsDiv_'+id, 'ProjectDocuments.action?proId='+ id+'&proType='+proType+'&pageType='+pageType);
		} else {
			document.getElementById('proDocsSpanStatus'+id).value = '0';
			document.getElementById('proDocsDownarrowSpan'+id).style.display = 'block';
			document.getElementById('proDocsUparrowSpan'+id).style.display = 'none';
			
			document.getElementById('proDocsDiv_'+id).innerHTML = '';
			document.getElementById('proDocsDiv_'+id).style.display = 'none';
			document.getElementById('proUL_'+id).style.display = 'none';
		}		
	}
	
	
	function viewMilestones(id, pageType) {
		//alert("proId ===>> " + id);    
		var status = document.getElementById('proMilesSpanStatus'+id).value;
		if(status == '0') {
			document.getElementById('proMilesSpanStatus'+id).value = '1';
			document.getElementById('proMilesDownarrowSpan'+id).style.display = 'none';
			document.getElementById('proMilesUparrowSpan'+id).style.display = 'block';
			
			document.getElementById('proDocsSpanStatus'+id).value = '0';
			document.getElementById('proDocsDownarrowSpan'+id).style.display = 'block';
			document.getElementById('proDocsUparrowSpan'+id).style.display = 'none';
			
			document.getElementById('proTaskSpanStatus'+id).value = '0';
			document.getElementById('proTaskDownarrowSpan'+id).style.display = 'block';
			document.getElementById('proTaskUparrowSpan'+id).style.display = 'none';
			
			document.getElementById('hideResourceSpanStatus'+id).value = '0';
			document.getElementById('resourceDownarrowSpan'+id).style.display = 'block';
			document.getElementById('resourceUparrowSpan'+id).style.display = 'none';
			
			document.getElementById('proSummarySpanStatus'+id).value = '0';
			document.getElementById('proSummaryDownarrowSpan'+id).style.display = 'block';
			document.getElementById('proSummaryUparrowSpan'+id).style.display = 'none';
			
			document.getElementById('proUL_'+id).style.display = 'block';
			document.getElementById('proResourcesDiv_'+id).style.display = 'none';
			document.getElementById('proMilestoneDiv_'+id).style.display = 'block';
			$('<img src=\"images1/ajax-loading-1.gif\"/>').appendTo("#proMilestoneDiv_"+id);
			
			if(document.getElementById('proCostSummaryDiv_'+id)) {
				document.getElementById('proCostSummaryDiv_'+id).style.display = 'none';
			}
	    	document.getElementById('proSummaryDiv_'+id).style.display = 'none';
	    	document.getElementById('proDocsDiv_'+id).style.display = 'none';
	    	document.getElementById('taskSubTaskDiv_'+id).style.display = 'none';
			getContent('proMilestoneDiv_'+id, 'ProjectMilestones.action?proId='+ id+'&pageType='+pageType);
		} else {
			document.getElementById('proMilesSpanStatus'+id).value = '0';
			document.getElementById('proMilesDownarrowSpan'+id).style.display = 'block';
			document.getElementById('proMilesUparrowSpan'+id).style.display = 'none';
			
			document.getElementById('proMilestoneDiv_'+id).innerHTML = '';
			document.getElementById('proMilestoneDiv_'+id).style.display = 'none';
			document.getElementById('proUL_'+id).style.display = 'none';
		}		
	}
	

/* ************************************************************ Start Add Document Script ********************************************** */
	
	function openCloseDocs(strProId, folderCnt, subFolderSize, docFolderSize) {
		if(document.getElementById("hideFolder_"+strProId+"_"+folderCnt)) {
			var status = document.getElementById("hideFolder_"+strProId+"_"+folderCnt).value;
			if(status == '0') {
				for(var i=0; i< parseInt(subFolderSize);i++){
					document.getElementById("folderTR_"+strProId+"_"+folderCnt+"_"+i).style.display = "block";
				}
				for(var i=0; i< parseInt(docFolderSize);i++){
					document.getElementById("docFolderTR_"+strProId+"_"+folderCnt+"_"+i).style.display = "block";
				}
				
				document.getElementById("hideFolder_"+strProId+"_"+folderCnt).value = '1';
				if(document.getElementById("FDDownarrowSpan_"+strProId+"_"+folderCnt)) {
					document.getElementById("FDDownarrowSpan_"+strProId+"_"+folderCnt).style.display = 'none';
					document.getElementById("FDUparrowSpan_"+strProId+"_"+folderCnt).style.display = 'block';
				}
			} else {
				for(var i=0; i< parseInt(subFolderSize);i++){
					document.getElementById("folderTR_"+strProId+"_"+folderCnt+"_"+i).style.display = "none";
				}
				for(var i=0; i< parseInt(docFolderSize);i++){
					document.getElementById("docFolderTR_"+strProId+"_"+folderCnt+"_"+i).style.display = "none";
				}
				
				document.getElementById("hideFolder_"+strProId+"_"+folderCnt).value = '0';
				if(document.getElementById("FDDownarrowSpan_"+strProId+"_"+folderCnt)) {
					document.getElementById("FDDownarrowSpan_"+strProId+"_"+folderCnt).style.display = 'block';
					document.getElementById("FDUparrowSpan_"+strProId+"_"+folderCnt).style.display = 'none';
				}
			}
		}
	}
	
	
	function openCloseDocs1(strProId, folderCnt, subFolderCnt, subDocSize) {
		if(document.getElementById("hideFolder_"+strProId+"_"+folderCnt+"_"+subFolderCnt)) {
			var status = document.getElementById("hideFolder_"+strProId+"_"+folderCnt+"_"+subFolderCnt).value;
			if(status == '0') {
				for(var i=0; i< parseInt(subDocSize);i++) {
					document.getElementById("folderTR_"+strProId+"_"+folderCnt+"_"+subFolderCnt+"_"+i).style.display = "block";
				}
				
				document.getElementById("hideFolder_"+strProId+"_"+folderCnt+"_"+subFolderCnt).value = '1';
				if(document.getElementById("FDDownarrowSpan_"+strProId+"_"+folderCnt+"_"+subFolderCnt)) {
					document.getElementById("FDDownarrowSpan_"+strProId+"_"+folderCnt+"_"+subFolderCnt).style.display = 'none';
					document.getElementById("FDUparrowSpan_"+strProId+"_"+folderCnt+"_"+subFolderCnt).style.display = 'block';
				}
			} else {
				for(var i=0; i< parseInt(subDocSize);i++) {
					document.getElementById("folderTR_"+strProId+"_"+folderCnt+"_"+subFolderCnt+"_"+i).style.display = "none";
				}
				
				document.getElementById("hideFolder_"+strProId+"_"+folderCnt+"_"+subFolderCnt).value = '0';
				if(document.getElementById("FDDownarrowSpan_"+strProId+"_"+folderCnt+"_"+subFolderCnt)) {
					document.getElementById("FDDownarrowSpan_"+strProId+"_"+folderCnt+"_"+subFolderCnt).style.display = 'block';
					document.getElementById("FDUparrowSpan_"+strProId+"_"+folderCnt+"_"+subFolderCnt).style.display = 'none';
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
	

	function executeFolderActions(val, clientId, proId, folderName, proFolderId, type, filePath, fileDir, divName, savePath, pageType) {
	//alert("strId ===>> " + strId);
		if(val == '1') {
			if(type == 'F' || type == 'SF') {
				editFolder(clientId, proId, folderName, proFolderId, type, pageType);
			} else {
				editDoc(clientId, proId, folderName, proFolderId, type, filePath, fileDir, pageType);
			}
		} else if(val == '2') {
			deleteProjectDocs(type, divName, folderName, proFolderId, savePath, pageType);
		}
	}
	
	
	function editFolder(clientId, proId, folderName, proFolderId, type, pageType) {
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
					url : "UpdateProjectDocumentFolder.action?clientId="+clientId+"&proId="+proId+"&folderName="+folderName
							+"&proFolderId="+proFolderId+"&type="+type+"&fromPage=VAP&pageType="+pageType,
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
	
	
	function editDoc(clientId, proId, docName, proFolderId, type, filePath, fileDir, pageType) {
		removeLoadingDiv("the_div");
		var dialogEdit = '#editFolder';
		dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
		$(dialogEdit).dialog({
			autoOpen : false,
			bgiframe : true,
			resizable : false,  
			height : 350,
			width : '87%', 
			modal : true,
			title : 'Update '+docName+' file',
			open : function() {
				var xhr = $.ajax({
					url : "UpdateProjectDocumentFile.action?clientId="+clientId+"&proId="+proId+"&folderName="+docName+"&proFolderId="+proFolderId
							+"&type="+type+"&filePath="+encodeURIComponent(filePath)+"&fileDir="+encodeURIComponent(fileDir)
							+"&fromPage=VAP&pageType="+pageType,
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
	
	
	function projectDocFact(clientId, proId, docName, proFolderId,type,filePath,fileDir) {
		removeLoadingDiv("the_div");   
		var action = "ProjectDocumentFact.action?clientId="+clientId+"&proId="+proId+"&folderName="+docName+"&proFolderId="+proFolderId+"&type="+type+"&filePath="+encodeURIComponent(filePath)+"&fileDir="+encodeURIComponent(fileDir);
		var dialogEdit = '#projectDocFactDiv';
		dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
		$(dialogEdit).dialog({
			autoOpen : false,
			bgiframe : true,
			resizable : false,  
			height : 550,
			width : '70%', 
			modal : true,
			title : 'View '+docName+' file',
			open : function() {
				var xhr = $.ajax({
					url : action,
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
	function fillData(elementId, num){
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
	
	 function addNewFolder(strProId, tableName, rowCountName) {
		// alert("strProId====>"+strProId);
		var proTasks = document.getElementById("projectTasks"+strProId).value;
		var proEmployee = document.getElementById("resourceIds"+strProId).value;
		var proCategory = document.getElementById("projectCategoryType"+strProId).value;
		var proPoc = document.getElementById("projectPoc"+strProId).value;
		
		if(document.getElementById("buttonDiv"+strProId)) {
			document.getElementById("buttonDiv"+strProId).style.display = 'block';
		}
		var fdCnt = document.getElementById(rowCountName).value;
			var cnt=(parseInt(fdCnt)+1);
		   // alert("addNewFolder cnt ===>> " + cnt);
		    var table = document.getElementById(tableName);
		    var rowCount = table.rows.length;
		    var row = table.insertRow(rowCount);
		    
		    row.id="folderTR"+strProId+"_"+cnt;
		    var cell0 = row.insertCell(0);
		    //cell1.setAttribute('style', 'border-bottom: 1px solid #B6B6B6' );
		    cell0.setAttribute('style','width: 550px;');
		    cell0.setAttribute('class','tdBorderLine');
		    cell0.innerHTML = "<span style=\"float:left; margin-right: 9px;\"><input type=\"hidden\" name=\"folderTRId"+strProId+"\" id=\"folderTRId"+strProId+"_"+cnt+"\" value=\""+cnt+"\">"+
		    "<input type=\"text\" name=\"strFolderName"+strProId+"\" id=\"strFolderName"+strProId+"_"+cnt+"\" style=\"width:200px; color: gray;\" onclick=\"clearData(this.id);\" onblur=\"fillData(this.id, 2);\" value=\"Folder Name\"/></span>"
	        +"<span style=\"float:left;\"><a href=\"javascript:void(0)\" onclick=\"addNewFolder('"+strProId+"', '"+tableName+"', '"+rowCountName+"')\" class=\"add\" title=\"Create New Folder \">Add</a>"
	        +"<a href=\"javascript:void(0)\" onclick=\"deleteFolder('"+strProId+"','"+cnt+"', '"+tableName+"')\" id=\""+cnt+"\" class=\"remove\">Remove Folder</a></span>"
	        +"<span style=\"float:left; width: 100%;\"><textarea rows=\"3\" name=\"strFolderDescription"+strProId+"\" id=\"strFolderDescription"+strProId+"_"+cnt+"\" style=\"width: 330px; color: gray;\" onclick=\"clearData(this.id);\" onblur=\"fillData(this.id, 1);\">Description</textarea></span>"
	        +"<span style=\"float:left; width: 100%;\"><a href=\"javascript:void(0);\" style=\"margin: -15px 0px 15px 50px; color: #68AC3B;\" onclick=\"addNewSubFolder('"+strProId+"','"+cnt+"', this.parentNode.parentNode.parentNode.rowIndex, '"+tableName+"', '"+rowCountName+"');\"> +Add Folder</a>"
	        +"<a href=\"javascript:void(0);\" style=\"margin: -15px 0px 15px 15px; color: #68AC3B;\" onclick=\"addNewFolderDocs('"+strProId+"','"+cnt+"', this.parentNode.parentNode.parentNode.rowIndex, '"+tableName+"', '"+rowCountName+"');\"> +Add Document</a></span>";
	        
	        var cell1 = row.insertCell(1);
		    cell1.setAttribute("valign","top");
		    cell1.setAttribute('class','tdBorderLine');
		    cell1.innerHTML = "<span style=\"float: left;\"><select name=\"proCategoryTypeFolder"+strProId+"\" id=\"proCategoryTypeFolder"+strProId+"_"+cnt+"\" style=\"width:100px\" onchange=\"changeCategoryType(this.value, 'proCatagorySpan"+strProId+"_"+cnt+"', 'proTaskSpan"+strProId+"_"+cnt+"');\">"+
		    "<option value=\"1\" selected>Project</option><option value=\"2\">Category</option></select></span>";
		    
		    var cell2 = row.insertCell(2);
		    cell2.setAttribute("valign","top");
		    cell2.setAttribute('class','tdBorderLine');
		    cell2.innerHTML = "<span id=\"proCatagorySpan"+strProId+"_"+cnt+"\" style=\"float: left; display: none\"><select name=\"proFolderCategory"+strProId+"_"+cnt+"\" id=\"proFolderCategory"+strProId+"_"+cnt+"\" style=\"width:100px;\">"+
		     	""+proCategory+"</select></span><span id=\"proTaskSpan"+strProId+"_"+cnt+"\" style=\"float: left;\"><select name=\"proFolderTasks"+strProId+"_"+cnt+"\" id=\"proFolderTasks"+strProId+"_"+cnt+"\" style=\"width:100px;\">"+
		     	""+proTasks+"</select></span>";
		     	
		    var cell3 = row.insertCell(3);
		    cell3.setAttribute("valign","top");
		    cell3.setAttribute('class','tdBorderLine');
		    cell3.innerHTML = "<div style=\"float: left; width: 100%;\"><span style=\"float: left;\"><select name=\"folderSharingType"+strProId+"\" id=\"folderSharingType"+strProId+"_"+cnt+"\" style=\"width:100px\" class=\"validate[required]\" onchange=\"showHideResources('"+strProId+"', this.value, '"+cnt+"')\">"
			    +"<option value=\"0\">Public</option><option value=\"1\">Private Team</option><option value=\"2\">Individual Resource</option></select></span>"
			    +"<span id=\"proResourceSpan"+strProId+"_"+cnt+"\" style=\"display: none; float: left; margin-left: 9px;\"><select name=\"proFolderEmployee"+strProId+"_"+cnt+"\" id=\"proFolderEmployee"+strProId+"_"+cnt+"\" style=\"width:100px;\" multiple size=\"4\">"+proEmployee+"</select></span>"
			    +"</div>"
			    +"<div style=\"float: left; width: 100%; margin-top: 5px;\"><span style=\"float: left;\"><a href=\"javascript:void(0);\" style=\"font-weight:normal; color: #68AC3B;\" id=\"sharePoc"+strProId+"_"+cnt+"\" onclick=\"showPoc('"+strProId+"','"+cnt+"')\">share customer</a></span>"
			    +"<input type=\"hidden\" name=\"showPocType"+strProId+"_"+cnt+"\" id=\"showPocType"+strProId+"_"+cnt+"\" value=\"1\">"
			    +"<span id=\"proPocSpan"+strProId+"_"+cnt+"\" style=\"display: none; float: left; margin-left: 9px;\"><select name=\"proFolderPoc"+strProId+"_"+cnt+"\" id=\"proFolderPoc"+strProId+"_"+cnt+"\" style=\"width:100px;\" multiple size=\"4\">"+proPoc+"</select></span>"
			    +"</div>";
			
		    var cell4 = row.insertCell(4);
		    cell4.setAttribute("valign","top");
		    cell4.setAttribute('class','tdBorderLine');
		    cell4.innerHTML = "<span id=\"isFolderEditSpan"+strProId+"_"+cnt+"\" style=\"float: left; width: 100%;\"><input type=\"hidden\" name=\"isFolderEdit"+strProId+"\" id=\"isFolderEdit"+strProId+"_"+cnt+"\" value=\"0\">"+
		    	"<input type=\"checkbox\" name=\"folderEdit"+strProId+"\" id=\"folderEdit"+strProId+"_"+cnt+"\" onclick=\"checkStatus(this, 'isFolderEdit"+strProId+"_"+cnt+"');\" checked/>Edit</span>"+
		    	"<span id=\"isFolderDeleteSpan"+strProId+"_"+cnt+"\" style=\"float: left; width: 100%;\"><input type=\"hidden\" name=\"isFolderDelete"+strProId+"\" id=\"isFolderDelete"+strProId+"_"+cnt+"\" value=\"0\">"+
		    	"<input type=\"checkbox\" name=\"folderDelete"+strProId+"\" id=\"folderDelete"+strProId+"_"+cnt+"\" onclick=\"checkStatus(this, 'isFolderDelete"+strProId+"_"+cnt+"');\"/>Delete</span>";
			
		    document.getElementById(rowCountName).value = cnt;
		    
		} 
	
	 function deleteFolder(strProId, count, tableName) {
		if(confirm('Are you sure, you want to delete this folder?')) {
			var trIndex = document.getElementById("folderTR"+strProId+"_"+count).rowIndex;
		    document.getElementById(tableName).deleteRow(trIndex);
		    var table = document.getElementById(tableName);
		    var rowCount = table.rows.length;
		    if(parseInt(rowCount) == 1) {
			    if(document.getElementById("buttonDiv"+strProId)) {
					document.getElementById("buttonDiv"+strProId).style.display = 'none';
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
	 
	 
	function addNewSubFolder(strProId, folderTRId, rwIndex, tableName, rowCountName) { 
			var proTasks = document.getElementById("projectTasks"+strProId).value;
			var proEmployee = document.getElementById("resourceIds"+strProId).value;
			var proCategory = document.getElementById("projectCategoryType"+strProId).value;
			var proPoc = document.getElementById("projectPoc"+strProId).value;
			
			var fdCnt = document.getElementById(rowCountName).value;
			var cnt=(parseInt(fdCnt)+1);
			var val=(parseInt(rwIndex)+1);
		    var table = document.getElementById(tableName);
		    var rowCount = table.rows.length;
		    var row = table.insertRow(val);
		    
		    row.id="SubFolderTR"+strProId+"_"+cnt;
		    var cell0 = row.insertCell(0);
		    //cell1.setAttribute('style', 'border-bottom: 1px solid #B6B6B6' );
		    cell0.setAttribute('style','width: 550px;');
		    cell0.setAttribute('class','tdBorderLine');
		    cell0.innerHTML = "<span style=\"float:left; margin-left: 50px; margin-right: 9px;\"><input type=\"hidden\" name=\"SubFolderTR"+strProId+"_"+folderTRId+"\" id=\"SubFolderTR"+strProId+"_"+cnt+"\" value=\""+cnt+"\">"+
		    "<input type=\"text\" name=\"strSubFolderName"+strProId+"_"+folderTRId+"\" id=\"strSubFolderName"+strProId+"_"+cnt+"\" style=\"width:200px; color: gray;\" onclick=\"clearData(this.id);\" onblur=\"fillData(this.id, 4);\" value=\"Sub Folder Name\"/></span>"
	        +"<a href=\"javascript:void(0)\"style=\"float: left; margin-bottom: 14px; margin-top: -4px;\" onclick=\"deleteSubFolder('"+strProId+"','"+cnt+"', '"+tableName+"')\" id=\""+cnt+"\" class=\"remove\">Remove Folder</a>"
	        +"<span style=\"float:left; width: 100%; margin-left: 50px; margin-right: 9px;\"><textarea rows=\"3\" name=\"strSubFolderDescription"+strProId+"_"+folderTRId+"\" id=\"strSubFolderDescription"+strProId+"_"+cnt+"\" style=\"width: 330px; color: gray;\" onclick=\"clearData(this.id);\" onblur=\"fillData(this.id, 1);\">Description</textarea></span>"
	        +"<a href=\"javascript:void(0);\" style=\"float:left; width:86%; margin: 4px 0px 15px 70px; color: #68AC3B;\" onclick=\"addNewSubFolderDocs('"+strProId+"','"+cnt+"', this.parentNode.parentNode.rowIndex, '"+tableName+"', '"+rowCountName+"');\"> +Add Document</a>";
		    
	        var cell1 = row.insertCell(1);
		    cell1.setAttribute("valign","top");
		    cell1.setAttribute('class','tdBorderLine');
		    cell1.innerHTML = "<span style=\"float: left;\"><select name=\"proCategoryTypeSubFolder"+strProId+"_"+folderTRId+"\" id=\"proCategoryTypeSubFolder"+strProId+"_"+cnt+"\" style=\"width:100px\" onchange=\"changeCategoryType(this.value, 'proSubFolderCatagorySpan"+strProId+"_"+cnt+"', 'proSubFolderTaskSpan"+strProId+"_"+cnt+"');\">"+
		    "<option value=\"1\" selected>Project</option><option value=\"2\">Category</option></select></span>";
		    
		    var cell2 = row.insertCell(2);
		    cell2.setAttribute("valign","top");
		    cell2.setAttribute('class','tdBorderLine');
		    cell2.innerHTML = "<span id=\"proSubFolderCatagorySpan"+strProId+"_"+cnt+"\" style=\"float: left; display: none\"><select name=\"proSubFolderCategory"+strProId+"_"+folderTRId+"_"+cnt+"\" id=\"proSubFolderTasksCategory"+strProId+"_"+cnt+"\" style=\"width:100px;\">"+
		     	""+proCategory+"</select></span><span id=\"proSubFolderTaskSpan"+strProId+"_"+cnt+"\" style=\"float: left;\"><select name=\"proSubFolderTasks"+strProId+"_"+folderTRId+"_"+cnt+"\" id=\"proSubFolderTasks"+strProId+"_"+cnt+"\" style=\"width:100px;\">"+
		     	""+proTasks+"</select></span>";
		    
		    var cell3 = row.insertCell(3);
		    cell3.setAttribute("valign","top");
		    cell3.setAttribute('class','tdBorderLine');
		    cell3.innerHTML = "<div style=\"float: left; width: 100%;\"><span style=\"float: left;\"><select name=\"SubfolderSharingType"+strProId+"_"+folderTRId+"\" id=\"SubfolderSharingType"+strProId+"_"+cnt+"\" style=\"width:100px\" class=\"validate[required]\" onchange=\"showHideResources('"+strProId+"', this.value, '"+cnt+"')\">"
			    +"<option value=\"0\">Public</option><option value=\"1\">Private Team</option><option value=\"2\">Individual Resource</option></select></span>"
			    +"<span id=\"proResourceSpan"+strProId+"_"+cnt+"\" style=\"display: none; float: left; margin-left: 9px;\"><select name=\"proSubFolderEmployee"+strProId+"_"+folderTRId+"_"+cnt+"\" id=\"proSubFolderEmployee"+strProId+"_"+cnt+"\" style=\"width:100px;\" multiple size=\"4\">"+proEmployee+"</select></span>"
			    +"</div>"
			    +"<div style=\"float: left; width: 100%; margin-top: 5px;\"><span style=\"float: left;\"><a href=\"javascript:void(0);\" style=\"font-weight:normal; color: #68AC3B;\" id=\"sharePoc"+strProId+"_"+cnt+"\" onclick=\"showPoc('"+strProId+"','"+cnt+"')\">share customer</a></span>"
			    +"<input type=\"hidden\" name=\"showPocType"+strProId+"_"+cnt+"\" id=\"showPocType"+strProId+"_"+cnt+"\" value=\"1\">"
			    +"<span id=\"proPocSpan"+strProId+"_"+cnt+"\" style=\"display: none; float: left; margin-left: 9px;\"><select name=\"proSubFolderPoc"+strProId+"_"+folderTRId+"_"+cnt+"\" id=\"proSubFolderPoc"+strProId+"_"+cnt+"\" style=\"width:100px;\" multiple size=\"4\">"+proPoc+"</select></span>"
			    +"</div>";
			
		    var cell4 = row.insertCell(4);
		    cell4.setAttribute("valign","top");
		    cell4.setAttribute('class','tdBorderLine');
		    cell4.innerHTML = "<span id=\"isSubFolderEditSpan"+strProId+"_"+cnt+"\" style=\"float: left; width: 100%;\"><input type=\"hidden\" name=\"isSubFolderEdit"+strProId+"_"+folderTRId+"\" id=\"isSubFolderEdit"+strProId+"_"+cnt+"\" value=\"0\">"+
		    	"<input type=\"checkbox\" name=\"subFolderEdit"+strProId+"_"+folderTRId+"\" id=\"subFolderEdit"+strProId+"_"+cnt+"\" onclick=\"checkStatus(this, 'isSubFolderEdit"+strProId+"_"+cnt+"');\" checked/>Edit</span>"+
		    	"<span id=\"isSubFolderDeleteSpan"+strProId+"_"+cnt+"\" style=\"float: left; width: 100%;\"><input type=\"hidden\" name=\"isSubFolderDelete"+strProId+"_"+folderTRId+"\" id=\"isSubFolderDelete"+strProId+"_"+cnt+"\" value=\"0\">"+
		    	"<input type=\"checkbox\" name=\"subFolderDelete"+strProId+"_"+folderTRId+"\" id=\"subFolderDelete"+strProId+"_"+cnt+"\" onclick=\"checkStatus(this, 'isSubFolderDelete"+strProId+"_"+cnt+"');\"/>Delete</span>";
			
		    document.getElementById(rowCountName).value = cnt;
		}
	
	function deleteSubFolder(strProId, count, tableName) {
		if(confirm('Are you sure, you want to delete this folder?')) {
			var trIndex = document.getElementById("SubFolderTR"+strProId+"_"+count).rowIndex;
		    document.getElementById(tableName).deleteRow(trIndex);
		}
	}
	
	function addNewSubFolderDocs(strProId, folderTRId, rwIndex, tableName, rowCountName) {
		//alert("addNewSubFolderDocs rwIndex ===>> " + rwIndex);
			var proTasks = document.getElementById("projectTasks"+strProId).value;
			var proEmployee = document.getElementById("resourceIds"+strProId).value;
			var proCategory = document.getElementById("projectCategoryType"+strProId).value;
			var proPoc = document.getElementById("projectPoc"+strProId).value;
			
			var fdCnt = document.getElementById(rowCountName).value;
			var cnt=(parseInt(fdCnt)+1);
			var val=(parseInt(rwIndex)+1);
		    
		    var table = document.getElementById(tableName);
		    var rowCount = table.rows.length;
		    var row = table.insertRow(val);
		    
		    row.id="SubfolderDocTR"+strProId+"_"+cnt;
		    var cell0 = row.insertCell(0);
		    //cell1.setAttribute('style', 'border-bottom: 1px solid #B6B6B6' );
		    cell0.setAttribute('style','width: 550px;');
		    cell0.setAttribute('class','tdBorderLine');
		    cell0.innerHTML = "<span style=\"float:left; margin-left: 100px; margin-right: 9px;\"><input type=\"hidden\" name=\"SubfolderDocsTRId"+strProId+"_"+folderTRId+"\" id=\"folderDocsTRId"+strProId+"_"+cnt+"\" value=\""+cnt+"\">"+
		    "<input type=\"text\" name=\"strSubFolderScopeDoc"+strProId+"_"+folderTRId+"\" id=\"strSubFolderScopeDoc"+strProId+"_"+cnt+"\" style=\"width:150px; color: gray;\" onclick=\"clearData(this.id);\" onblur=\"fillData(this.id, 3);\" value=\"Document Scope\"/> "+
		    "<input type=\"file\" name=\"strSubFolderDoc"+strProId+"_"+folderTRId+"\" id=\"strSubFolderDoc"+strProId+"_"+cnt+"\" size=\"5\"/></span>"
	        +"<span style=\"float:left; width: 100%; margin-left: 100px; margin-top: 10px; margin-right: 9px;\"><span style=\"float:left;\"><textarea rows=\"3\" name=\"strSubFolderDocDescription"+strProId+"_"+folderTRId+"\" id=\"strSubFolderDocDescription"+strProId+"_"+cnt+"\" style=\"width: 330px; color: gray;\" onclick=\"clearData(this.id);\" onblur=\"fillData(this.id, 1);\">Description</textarea></span>"
	        +"<a href=\"javascript:void(0)\" onclick=\"addNewSubFolderDocs('"+strProId+"','"+folderTRId+"', this.parentNode.parentNode.parentNode.rowIndex, '"+tableName+"', '"+rowCountName+"')\" class=\"add\" title=\"Add \">Add Document</a>"
	        +"<a href=\"javascript:void(0)\" onclick=\"deleteSubFolderDocs('"+strProId+"','"+cnt+"', '"+tableName+"')\" id=\""+cnt+"\" style=\"float:left;\" class=\"remove\">Remove Document</a></span>";
		    
	        var cell1 = row.insertCell(1);
		    cell1.setAttribute("valign","top");
		    cell1.setAttribute('class','tdBorderLine');
		    cell1.innerHTML = "<span style=\"float: left;\"><select name=\"proCategoryTypeSubFolderDoc"+strProId+"_"+folderTRId+"\" id=\"proCategoryTypeSubFolderDoc"+strProId+"_"+cnt+"\" style=\"width:100px\" onchange=\"changeCategoryType(this.value, 'proSubFolderDocCatagorySpan"+strProId+"_"+cnt+"', 'proSubFolderDocTaskSpan"+strProId+"_"+cnt+"');\">"+
		    "<option value=\"1\" selected>Project</option><option value=\"2\">Category</option></select></span>";
		    
		    var cell2 = row.insertCell(2);
		    cell2.setAttribute("valign","top");
		    cell2.setAttribute('class','tdBorderLine');
		    cell2.innerHTML = "<span id=\"proSubFolderDocCatagorySpan"+strProId+"_"+cnt+"\" style=\"float: left; display: none\"><select name=\"proSubFolderDocCategory"+strProId+"_"+folderTRId+"_"+cnt+"\" id=\"proSubFolderDocCategory"+strProId+"_"+cnt+"\" style=\"width:100px;\">"+
		     	""+proCategory+"</select></span><span id=\"proSubFolderDocTaskSpan"+strProId+"_"+cnt+"\" style=\"float: left;\"><select name=\"proSubFolderDocTasks"+strProId+"_"+folderTRId+"_"+cnt+"\" id=\"proSubFolderDocTasks"+strProId+"_"+cnt+"\" style=\"width:100px;\">"+
		     	""+proTasks+"</select></span>";
		     	
		    var cell3 = row.insertCell(3);
		    cell3.setAttribute("valign","top");
		    cell3.setAttribute('class','tdBorderLine');
		    cell3.innerHTML = "<div style=\"float: left; width: 100%;\"><span style=\"float: left;\"><select name=\"SubfolderDocDharingType"+strProId+"_"+folderTRId+"\" id=\"SubfolderDocDharingType"+strProId+"_"+cnt+"\" style=\"width:100px\" class=\"validate[required]\" onchange=\"showHideResources('"+strProId+"', this.value, '"+cnt+"')\">"
			    +"<option value=\"0\">Public</option><option value=\"1\">Private Team</option><option value=\"2\">Individual Resource</option></select></span>"
			    +"<span id=\"proResourceSpan"+strProId+"_"+cnt+"\" style=\"display: none; float: left; margin-left: 9px;\"><select name=\"proSubFolderDocEmployee"+strProId+"_"+folderTRId+"_"+cnt+"\" id=\"proSubFolderDocEmployee"+strProId+"_"+cnt+"\" style=\"width:100px;\" multiple size=\"4\">"+proEmployee+"</select></span>"
			    +"</div>"
			    +"<div style=\"float: left; width: 100%; margin-top: 5px;\"><span style=\"float: left;\"><a href=\"javascript:void(0);\" style=\"font-weight:normal; color: #68AC3B;\" id=\"sharePoc"+strProId+"_"+cnt+"\" onclick=\"showPoc('"+strProId+"','"+cnt+"')\">share customer</a></span>"
			    +"<input type=\"hidden\" name=\"showPocType"+strProId+"_"+cnt+"\" id=\"showPocType"+strProId+"_"+cnt+"\" value=\"1\">"
			    +"<span id=\"proPocSpan"+strProId+"_"+cnt+"\" style=\"display: none; float: left; margin-left: 9px;\"><select name=\"proSubFolderDocPoc"+strProId+"_"+folderTRId+"_"+cnt+"\" id=\"proSubFolderDocPoc"+strProId+"_"+cnt+"\" style=\"width:100px;\" multiple size=\"4\">"+proPoc+"</select></span>"
			    +"</div>";
		
		    var cell4 = row.insertCell(4);
		    cell4.setAttribute("valign","top");
		    cell4.setAttribute('class','tdBorderLine');
		    cell4.innerHTML = "<span id=\"isSubFolderDocEditSpan"+strProId+"_"+cnt+"\" style=\"float: left; width: 100%;\"><input type=\"hidden\" name=\"isSubFolderDocEdit"+strProId+"_"+folderTRId+"\" id=\"isSubFolderDocEdit"+strProId+"_"+cnt+"\" value=\"0\">"+
		    	"<input type=\"checkbox\" name=\"subFolderDocEdit"+strProId+"_"+folderTRId+"\" id=\"subFolderDocEdit"+strProId+"_"+cnt+"\" onclick=\"checkStatus(this, 'isSubFolderDocEdit"+strProId+"_"+cnt+"');\" checked/>Edit</span>"+
		    	"<span id=\"isSubFolderDocDeleteSpan"+strProId+"_"+cnt+"\" style=\"float: left; width: 100%;\"><input type=\"hidden\" name=\"isSubFolderDocDelete"+strProId+"_"+folderTRId+"\" id=\"isSubFolderDocDelete"+strProId+"_"+cnt+"\" value=\"0\">"+
		    	"<input type=\"checkbox\" name=\"subFolderDocDelete"+strProId+"_"+folderTRId+"\" id=\"subFolderDocDelete"+strProId+"_"+cnt+"\" onclick=\"checkStatus(this, 'isSubFolderDocDelete"+strProId+"_"+cnt+"');\"/>Delete</span>";

		    document.getElementById(rowCountName).value = cnt;
		}
	
	function deleteSubFolderDocs(strProId, count, tableName) {
		if(confirm('Are you sure, you want to delete this document?')) {
			var trIndex = document.getElementById("SubfolderDocTR"+strProId+"_"+count).rowIndex;
		    document.getElementById(tableName).deleteRow(trIndex);
		}
	}
	
	
	function addNewDocs(strProId, tableName, rowCountName) { 
		var proTasks = document.getElementById("projectTasks"+strProId).value;
		var proEmployee = document.getElementById("resourceIds"+strProId).value;
		var proCategory = document.getElementById("projectCategoryType"+strProId).value;
		var proPoc = document.getElementById("projectPoc"+strProId).value;
		
		if(document.getElementById("buttonDiv"+strProId)) {
			document.getElementById("buttonDiv"+strProId).style.display = 'block';
		}
			var fdCnt = document.getElementById(rowCountName).value;
			var cnt=(parseInt(fdCnt)+1);
			//alert("addNewDocs cnt ===>> " + cnt);
		    //alert("taskCnt ===>> " + taskCnt);
		    var table = document.getElementById(tableName);
		    var rowCount = table.rows.length;
		    var row = table.insertRow(rowCount);
		    
		    row.id="docTR"+strProId+"_"+cnt;
		    var cell0 = row.insertCell(0);
		    //cell1.setAttribute('style', 'border-bottom: 1px solid #B6B6B6' );
		    cell0.setAttribute('style','width: 400px;');
		    cell0.setAttribute('class','tdBorderLine');
		    cell0.innerHTML = "<span style=\"float:left; margin-right: 9px;\"><input type=\"hidden\" name=\"docsTRId"+strProId+"\" id=\"docsTRId"+strProId+"_"+cnt+"\" value=\""+cnt+"\">"+
		    "<input type=\"text\" name=\"strScopeDoc"+strProId+"\" id=\"strScopeDoc"+strProId+"_"+cnt+"\" style=\"width:150px; color: gray;\" onclick=\"clearData(this.id);\" onblur=\"fillData(this.id, 3);\" value=\"Document Scope\"/> "+
		    "<input type=\"file\" name=\"strDoc"+strProId+"\" id=\"strDoc"+strProId+"_"+cnt+"\" size=\"5\"/></span>"
	        +"<span style=\"float:left; width: 100%; margin-top: 10px; margin-right: 9px;\"><span style=\"float:left;\"><textarea rows=\"3\" name=\"strDocDescription"+strProId+"\" id=\"strDocDescription"+strProId+"_"+cnt+"\" style=\"width: 330px; color: gray;\" onclick=\"clearData(this.id);\" onblur=\"fillData(this.id, 1);\">Description</textarea></span>"
	        +"<a href=\"javascript:void(0)\" onclick=\"addNewDocs('"+strProId+"', '"+tableName+"', '"+rowCountName+"')\" class=\"add\" title=\"Add \">Add Document</a>"
	        +"<a href=\"javascript:void(0)\" onclick=\"deleteDocs('"+strProId+"','"+cnt+"', '"+tableName+"')\" id=\""+cnt+"\" style=\"float:left;\" class=\"remove\">Remove Document</a></span>";
		    
	        var cell1 = row.insertCell(1);
		    cell1.setAttribute("valign","top");
		    cell1.setAttribute('class','tdBorderLine');
		    cell1.innerHTML = "<span style=\"float: left;\"><select name=\"proCategoryTypeDoc"+strProId+"\" id=\"proCategoryTypeDoc"+strProId+"_"+cnt+"\" style=\"width:100px\" onchange=\"changeCategoryType(this.value, 'proDocCatagorySpan"+strProId+"_"+cnt+"', 'proDocTaskSpan"+strProId+"_"+cnt+"');\">"+
		    "<option value=\"1\" selected>Project</option><option value=\"2\">Category</option></select></span>";
		    
		    var cell2 = row.insertCell(2);
		    cell2.setAttribute("valign","top");
		    cell2.setAttribute('class','tdBorderLine');
		    cell2.innerHTML = "<span id=\"proDocCatagorySpan"+strProId+"_"+cnt+"\" style=\"float: left; display: none\"><select name=\"proDocCategory"+strProId+"_"+cnt+"\" id=\"proDocCategory"+strProId+"_"+cnt+"\" style=\"width:100px;\">"+
		     	""+proCategory+"</select></span><span id=\"proDocTaskSpan"+strProId+"_"+cnt+"\" style=\"float: left;\"><select name=\"proDocTasks"+strProId+"_"+cnt+"\" id=\"proDocTasks"+strProId+"_"+cnt+"\" style=\"width:100px;\">"+
		     	""+proTasks+"</select></span>";
		     	
		    var cell3 = row.insertCell(3);
		    cell3.setAttribute("valign","top");
		    cell3.setAttribute('class','tdBorderLine');
		    cell3.innerHTML = "<div style=\"float: left; width: 100%;\"><span style=\"float: left;\"><select name=\"docSharingType"+strProId+"\" id=\"docSharingType"+strProId+"_"+cnt+"\" style=\"width:100px\" class=\"validate[required]\" onchange=\"showHideResources('"+strProId+"', this.value, '"+cnt+"')\">"
			    +"<option value=\"0\">Public</option><option value=\"1\">Private Team</option><option value=\"2\">Individual Resource</option></select></span>"
			    +"<span id=\"proResourceSpan"+strProId+"_"+cnt+"\" style=\"display: none; float: left; margin-left: 9px;\"><select name=\"proDocEmployee"+strProId+"_"+cnt+"\" id=\"proDocEmployee"+strProId+"_"+cnt+"\" style=\"width:100px;\" multiple size=\"4\">"+proEmployee+"</select></span>"
			    +"</div>"
			    +"<div style=\"float: left; width: 100%; margin-top: 5px;\"><span style=\"float: left;\"><a href=\"javascript:void(0);\" style=\"font-weight:normal; color: #68AC3B;\" id=\"sharePoc"+strProId+"_"+cnt+"\" onclick=\"showPoc('"+strProId+"','"+cnt+"')\">share customer</a></span>"
			    +"<input type=\"hidden\" name=\"showPocType"+strProId+"_"+cnt+"\" id=\"showPocType"+strProId+"_"+cnt+"\" value=\"1\">"
			    +"<span id=\"proPocSpan"+strProId+"_"+cnt+"\" style=\"display: none; float: left; margin-left: 9px;\"><select name=\"proDocPoc"+strProId+"_"+cnt+"\" id=\"proDocPoc"+strProId+"_"+cnt+"\" style=\"width:100px;\" multiple size=\"4\">"+proPoc+"</select></span>"
			    +"</div>";
		
		    var cell4 = row.insertCell(4);
		    cell4.setAttribute("valign","top");
		    cell4.setAttribute('class','tdBorderLine');
		    cell4.innerHTML = "<span id=\"isDocEditSpan"+strProId+"_"+cnt+"\" style=\"float: left; width: 100%;\"><input type=\"hidden\" name=\"isDocEdit"+strProId+"\" id=\"isDocEdit"+strProId+"_"+cnt+"\" value=\"0\">"+
		    	"<input type=\"checkbox\" name=\"docEdit"+strProId+"\" id=\"docEdit"+strProId+"_"+cnt+"\" onclick=\"checkStatus(this, 'isDocEdit"+strProId+"_"+cnt+"');\" checked/>Edit</span>"+
		    	"<span id=\"isDocDeleteSpan"+strProId+"_"+cnt+"\" style=\"float: left; width: 100%;\"><input type=\"hidden\" name=\"isDocDelete"+strProId+"\" id=\"isDocDelete"+strProId+"_"+cnt+"\" value=\"0\">"+
		    	"<input type=\"checkbox\" name=\"docDelete"+strProId+"\" id=\"docDelete"+strProId+"_"+cnt+"\" onclick=\"checkStatus(this, 'isDocDelete"+strProId+"_"+cnt+"');\"/>Delete</span>";

		    document.getElementById(rowCountName).value = cnt; 
		}
	
	function deleteDocs(strProId, count, tableName) {
		if(confirm('Are you sure, you want to delete this document?')) {
			var trIndex = document.getElementById("docTR"+strProId+"_"+count).rowIndex;
		    document.getElementById(tableName).deleteRow(trIndex);
		    var table = document.getElementById(tableName);
		    var rowCount = table.rows.length;
		    if(parseInt(rowCount) == 1) {
			    if(document.getElementById("buttonDiv"+strProId)) {
					document.getElementById("buttonDiv"+strProId).style.display = 'none';
				}
		    }
		}
	}
	
	
	function addNewFolderDocs(strProId, folderTRId, rwIndex, tableName, rowCountName) {
		var proTasks = document.getElementById("projectTasks"+strProId).value;
		var proEmployee = document.getElementById("resourceIds"+strProId).value;
		var proCategory = document.getElementById("projectCategoryType"+strProId).value;
		var proPoc = document.getElementById("projectPoc"+strProId).value;
		//alert("rwIndex ===>> " + rwIndex);
		var fdCnt = document.getElementById(rowCountName).value;
			var cnt=(parseInt(fdCnt)+1);
			var val=(parseInt(rwIndex)+1);
			//alert("addNewFolderDocs cnt ===>> " + cnt);
		    //alert("folderTRId ===>> " + folderTRId);
		    //alert("val ===>> " + val);
		    
		    var table = document.getElementById(tableName);
		    var rowCount = table.rows.length;
		    var row = table.insertRow(val);
		    
		    row.id="folderDocTR"+strProId+"_"+cnt;
		    var cell0 = row.insertCell(0);
		    //cell1.setAttribute('style', 'border-bottom: 1px solid #B6B6B6' );
		    cell0.setAttribute('style','width: 400px;');
		    cell0.setAttribute('class','tdBorderLine');
		    cell0.innerHTML = "<span style=\"float:left; margin-left: 50px; margin-right: 9px;\"><input type=\"hidden\" name=\"folderDocsTRId"+strProId+"_"+folderTRId+"\" id=\"folderDocsTRId"+strProId+"_"+cnt+"\" value=\""+cnt+"\">"+
		    "<input type=\"text\" name=\"strFolderScopeDoc"+strProId+"_"+folderTRId+"\" id=\"strFolderScopeDoc"+strProId+"_"+cnt+"\" style=\"width:150px; color: gray;\" onclick=\"clearData(this.id);\" onblur=\"fillData(this.id, 3);\" value=\"Document Scope\"/> "+
		    "<input type=\"file\" name=\"strFolderDoc"+strProId+"_"+folderTRId+"\" id=\"strFolderDoc"+strProId+"_"+cnt+"\" size=\"5\"/></span>"
	        +"<span style=\"float:left; width: 100%; margin-left: 50px; margin-top: 10px; margin-right: 9px;\"><span style=\"float:left;\"><textarea rows=\"3\" name=\"strFolderDocDescription"+strProId+"_"+folderTRId+"\" id=\"strFolderDocDescription"+strProId+"_"+cnt+"\" style=\"width: 330px; color: gray;\" onclick=\"clearData(this.id);\" onblur=\"fillData(this.id, 1);\">Description</textarea></span>"
	        +"<a href=\"javascript:void(0)\" onclick=\"addNewFolderDocs('"+strProId+"','"+folderTRId+"', this.parentNode.parentNode.parentNode.rowIndex, '"+tableName+"', '"+rowCountName+"')\" class=\"add\" title=\"Add \">Add Document</a>"
	        +"<a href=\"javascript:void(0)\" style=\"float:left;\" onclick=\"deleteFolderDocs('"+strProId+"','"+cnt+"', '"+tableName+"')\" id=\""+cnt+"\" class=\"remove\">Remove Document</a></span>";
		    
	        var cell1 = row.insertCell(1);
		    cell1.setAttribute("valign","top");
		    cell1.setAttribute('class','tdBorderLine');
		    cell1.innerHTML = "<span style=\"float: left;\"><select name=\"proCategoryTypeFolderDoc"+strProId+"_"+folderTRId+"\" id=\"proCategoryTypeFolderDoc"+strProId+"_"+cnt+"\" style=\"width:100px\" onchange=\"changeCategoryType(this.value, 'proFolderDocCatagorySpan"+strProId+"_"+cnt+"', 'proFolderDocTaskSpan"+strProId+"_"+cnt+"');\">"+
		    "<option value=\"1\" selected>Project</option><option value=\"2\">Category</option></select></span>";
		    
		    var cell2 = row.insertCell(2);
		    cell2.setAttribute("valign","top");
		    cell2.setAttribute('class','tdBorderLine');
		    cell2.innerHTML = "<span id=\"proFolderDocCatagorySpan"+strProId+"_"+cnt+"\" style=\"float: left; display: none\"><select name=\"proFolderDocCategory"+strProId+"_"+folderTRId+"_"+cnt+"\" id=\"proFolderDocCategory"+strProId+"_"+cnt+"\" style=\"width:100px;\">"+
		     	""+proCategory+"</select></span><span id=\"proFolderDocTaskSpan"+strProId+"_"+cnt+"\" style=\"float: left;\"><select name=\"proFolderDocTasks"+strProId+"_"+folderTRId+"_"+cnt+"\" id=\"proFolderDocTasks"+strProId+"_"+cnt+"\" style=\"width:100px;\">"+
		     	""+proTasks+"</select></span>";
		     	
		    var cell3 = row.insertCell(3);
		    cell3.setAttribute("valign","top");
		    cell3.setAttribute('class','tdBorderLine');
		    cell3.innerHTML = "<div style=\"float: left; width: 100%;\"><span style=\"float: left;\"><select name=\"folderDocDharingType"+strProId+"_"+folderTRId+"\" id=\"folderDocDharingType"+strProId+"_"+cnt+"\" style=\"width:100px\" class=\"validate[required]\" onchange=\"showHideResources('"+strProId+"', this.value, '"+cnt+"')\">"
			    +"<option value=\"0\">Public</option><option value=\"1\">Private Team</option><option value=\"2\">Individual Resource</option></select></span>"
			    +"<span id=\"proResourceSpan"+strProId+"_"+cnt+"\" style=\"display: none; float: left; margin-left: 9px;\"><select name=\"proFolderDocEmployee"+strProId+"_"+folderTRId+"_"+cnt+"\" id=\"proFolderDocEmployee"+strProId+"_"+cnt+"\" style=\"width:100px;\" multiple size=\"4\">"+proEmployee+"</select></span>"
			    +"</div>"
			    +"<div style=\"float: left; width: 100%; margin-top: 5px;\"><span style=\"float: left;\"><a href=\"javascript:void(0);\" style=\"font-weight:normal; color: #68AC3B;\" id=\"sharePoc"+strProId+"_"+cnt+"\" onclick=\"showPoc('"+strProId+"','"+cnt+"')\">share customer</a></span>"
			    +"<input type=\"hidden\" name=\"showPocType"+strProId+"_"+cnt+"\" id=\"showPocType"+strProId+"_"+cnt+"\" value=\"1\">"
			    +"<span id=\"proPocSpan"+strProId+"_"+cnt+"\" style=\"display: none; float: left; margin-left: 9px;\"><select name=\"proFolderDocPoc"+strProId+"_"+folderTRId+"_"+cnt+"\" id=\"proFolderDocPoc"+strProId+"_"+cnt+"\" style=\"width:100px;\" multiple size=\"4\">"+proPoc+"</select></span>"
			    +"</div>";
		
		    var cell4 = row.insertCell(4);
		    cell4.setAttribute("valign","top");
		    cell4.setAttribute('class','tdBorderLine');
		    cell4.innerHTML = "<span id=\"isFolderDocEditSpan"+strProId+"_"+cnt+"\" style=\"float: left; width: 100%;\"><input type=\"hidden\" name=\"isFolderDocEdit"+strProId+"_"+folderTRId+"\" id=\"isFolderDocEdit"+strProId+"_"+cnt+"\" value=\"0\">"+
		    	"<input type=\"checkbox\" name=\"folderDocEdit"+strProId+"_"+folderTRId+"\" id=\"folderDocEdit"+strProId+"_"+cnt+"\" onclick=\"checkStatus(this, 'isFolderDocEdit"+strProId+"_"+cnt+"');\" checked/>Edit</span>"+
		    	"<span id=\"isFolderDocDeleteSpan"+strProId+"_"+cnt+"\" style=\"float: left; width: 100%;\"><input type=\"hidden\" name=\"isFolderDocDelete"+strProId+"_"+folderTRId+"\" id=\"isFolderDocDelete"+strProId+"_"+cnt+"\" value=\"0\">"+
		    	"<input type=\"checkbox\" name=\"folderDocDelete"+strProId+"_"+folderTRId+"\" id=\"folderDocDelete"+strProId+"_"+cnt+"\" onclick=\"checkStatus(this, 'isFolderDocDelete"+strProId+"_"+cnt+"');\"/>Delete</span>";

		    document.getElementById(rowCountName).value = cnt;
		}
	
	function deleteFolderDocs(strProId, count, tableName) {
		if(confirm('Are you sure, you want to delete this document?')) {
			var trIndex = document.getElementById("folderDocTR"+strProId+"_"+count).rowIndex;
		    document.getElementById(tableName).deleteRow(trIndex);
		}
	} 
	
	function showHideResources(strProId, val, count) {
		if(val == '2') {
			document.getElementById("proResourceSpan"+strProId+"_"+count).style.display = 'block';
		} else {
			document.getElementById("proResourceSpan"+strProId+"_"+count).style.display = 'none';
		}
	}
	
	function showPoc(strProId,count) {
		var val = document.getElementById("showPocType"+strProId+"_"+count).value;
		if(val == '1') {
			document.getElementById("proPocSpan"+strProId+"_"+count).style.display = 'block';
			document.getElementById("showPocType"+strProId+"_"+count).value = '0';
		} else {
			document.getElementById("proPocSpan"+strProId+"_"+count).style.display = 'none';
			document.getElementById("showPocType"+strProId+"_"+count).value = '1';
		}
	}
	
	function showTblHeader(strProId) {
		//alert("showTblHeader========>"+strProId);
		document.getElementById("folderTR"+strProId+"_0").style.display = 'table-row';
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
	
/* ************************************************************ End Add Document Script ********************************************** */	
	
	
	function editProject(id){
		removeLoadingDiv('the_div');
		var dialogEdit = '#editproject';
		dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
		$(dialogEdit).dialog({
			autoOpen : false,
			bgiframe : true,
			resizable : false,
			height : 580,
			width : 800,
			modal : true,
	 		show : 'slow',
			title : 'Edit Project Details',
			open : function() {
				var xhr = $.ajax({
					url : "AddNewProject.action?operation=E&pro_id="+id,
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
	
	
	/* function editTask(id) {
		
		var dialogEdit = '#edittask';
		dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
		$(dialogEdit).dialog({
			autoOpen : false,
			bgiframe : true,
			resizable : false,
			height : 580,
			width : 800,
			modal : true,
	 		show : 'slow',
			title : 'Edit Task Details',
			open : function() {
				var xhr = $.ajax({
					url : "AddNewActivity.action?operation=E&task_id="+id,
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
		 
		
	function view(id) {
		//removeLoadingDiv('the_div');
		var dialogEdit = '#view';
		dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
		$(dialogEdit).dialog({
			autoOpen : false,
			bgiframe : true,
			resizable : false,
			height : 200,
			width : 400,
			modal : true,
			title : 'Description',
			bold : true,
			open : function() {
				$(dialogEdit).html(id);
			},
			overlay : { 
				backgroundColor : '#000',
				opacity : 0.5
			}
		});
		$(dialogEdit).dialog('open');
	}
	
	
	/* function addMilestone(id) {

		var dialogEdit = '#addproject';
		dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
		$(dialogEdit).dialog({
			autoOpen : false,
			bgiframe : true,
			resizable : false,
			height : 450,
			width : 800,
			modal : true,
			title : 'Add Milestone',
			open : function() {
				var xhr = $.ajax({
					url : "AddNewMilestonePopup.action?pro_id=" + id,
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
	
	/* function addActivity(id) {
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
					url : "AddNewActivityPopup.action?pro_id=" + id +'&type=Task',
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
	
	
	function addActivity(proId) {
		//alert("proId ===>> " + proId);
		var action = 'AddNewTaskOrSubtask.action?pro_id='+proId;
		getContent('newTaskDiv_'+proId, action);
	}
	
	
	function editActivity(id, pid) {
		removeLoadingDiv('the_div');
		var dialogEdit = '#edittasksubtask';
		var url1='AddNewActivityPopup.action?operation=E&task_id='+ id +'&pro_id='+ pid +'&type=Task';
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
					url : 'AddNewActivityPopup.action?pro_id='+ proId +'&task_id='+ taskId +'&type=SubTask',
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
		var url1='AddNewActivityPopup.action?operation=E&task_id='+ taskId +'&pro_id='+ proId +'&sub_task_id='+ subTaskId +'&type=SubTask';
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
	
	
	function reAssign(taskId) {
		removeLoadingDiv('the_div');
		var dialogEdit = '#reAssign';
		var url1='ReAssign.action?taskId='+taskId;
		dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
		$(dialogEdit).dialog({
			autoOpen : false,
			bgiframe : true,
			resizable : false,
			height : 200,
			width : 400,
			modal : true,
			title : 'Re-Assign Task',
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
	
	
	function SendProId(id)
	{
		if(id!=null)
			{
		window.location ='ViewAllProjects.action?singleProid='+id ;
			}
	}
	
	
	jQuery(document).ready(function() {
    	jQuery(".content1").hide();
    	//toggle the componenet with class msg_body
    	jQuery(".heading_dash").click(function() {
    		jQuery(this).next(".content1").slideToggle(500);
    		$(this).toggleClass("filter_close");
    	});
    });
	
	
	
	$(document).ready(function() {

        $('a.poplight[href^=#]').click(function() {
            var popID = $(this).attr('rel'); //Get Popup Name
            var popURL = $(this).attr('href'); //Get Popup href to define size

            //Pull Query & Variables from href URL
            var query= popURL.split('?');
            var dim= query[1].split('&');
            var popWidth = dim[0].split('=')[1]; //Gets the first query string value

            //Fade in the Popup and add close button
            $('#' + popID).fadeIn().css({ 'width': Number( popWidth ) }).prepend('<a href="#" class="close"><img src="images/close_pop.png" class="btn_close" title="Close Window" alt="Close" /></a>');

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

	
	function checkUncheckAllProject() {
		var allLivePr = document.getElementById("allLivePr");		
		var approvePr = document.getElementsByName('approvePr');
		//alert("allLivePr.checked ==>> " + allLivePr.checked);
		if(allLivePr.checked == true) {
			 for(var i=0;i<approvePr.length;i++) {
				 approvePr[i].checked = true;
			 }
			 document.getElementById("unblockedSpan").style.display = 'none';
			 document.getElementById("blockedSpan").style.display = 'block';
		} else {		
			 for(var i=0; i<approvePr.length; i++) {
				 approvePr[i].checked = false;
			 }
			 document.getElementById("unblockedSpan").style.display = 'block';
			 document.getElementById("blockedSpan").style.display = 'none';
		}
	}


	function checkAllProjectCheckedUnchecked() {
		var allLivePr = document.getElementById("allLivePr");		
		var approvePr = document.getElementsByName('approvePr');
		var cnt = 0;
		var chkCnt = 0;
		for(var i=0;i<approvePr.length;i++) {
			cnt++;
			 if(approvePr[i].checked) {
				 chkCnt++;
			 }
		 }
		if(parseFloat(chkCnt) > 0) {
			document.getElementById("unblockedSpan").style.display = 'none';
			document.getElementById("blockedSpan").style.display = 'block';
		} else {
			document.getElementById("unblockedSpan").style.display = 'block';
			document.getElementById("blockedSpan").style.display = 'none';
		}
		
		if(cnt == chkCnt) {
			allLivePr.checked = true;
		} else {
			allLivePr.checked = false;
		}
	}
	
	<%-- function loadMoreProjects(strDivCount, strLimit, proType) {
		//alert("zczczc");
		//alert(strDivCount +" strLimit -->>> " + strLimit);
		
		var service_id = getSelectedValue("f_service");
		var project_id = getSelectedValue("pro_id");
		var manager_id = getSelectedValue("managerId");
		var client_id = getSelectedValue("client");
		//var dialogEdit = 'moreProject_'+strDivCount;
		//dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
		var action = 'GetMoreProjects.action?strLimit='+strLimit+'&strDivCount='+strDivCount+'&proType='+proType+'&service_id='+service_id
				+'&project_id='+project_id+'&manager_id='+manager_id+'&client_id='+client_id;
			getContent('moreProject_'+strDivCount, action);
	} --%>
	
	function loadMoreProjects(proPage, minLimit, proType) {
		//alert("zczczc");
		//alert(strDivCount +" strLimit -->>> " + strLimit);
		
		/* var service_id = getSelectedValue("f_service");
		var project_id = getSelectedValue("pro_id");
		var manager_id = getSelectedValue("managerId");
		var client_id = getSelectedValue("client");
		
		var action = 'ViewAllProjects.action?proPage='+proPage+'&minLimit='+minLimit+'&proType='+proType+'&service_id='+service_id
				+'&pro_id='+project_id+'&managerId='+manager_id+'&client='+client_id+'&loadMore=YES';
			window.location = action; */
			
			document.frm_adminproject_view.proPage.value = proPage;
			document.frm_adminproject_view.minLimit.value = minLimit;
			//document.frm_adminproject_view.proPage.value = proPage;
			document.frm_adminproject_view.submit();
	}
	
	/* function getSelectedValue(selectId) {
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
	} */
	
	/* function hideShowDiv(projectId) {
		//alert("projectId ===>> " + projectId);
		var status = document.getElementById("divStatus"+projectId).value;
		
		if(status == '0') {
			//alert("status ===>> " + status);	
			document.getElementById("taskDiv"+projectId).style.display = 'block';
			//alert("taskDiv ===>> " + document.getElementById("taskDiv"+projectId));
			document.getElementById("divStatus"+projectId).value = '1';
			document.getElementById("AllTaskP"+projectId).className= 'past close_div';
			//alert("divStatus ===>> " + document.getElementById("divStatus"+projectId));
		} else {
			document.getElementById("AllTaskP"+projectId).className= 'past heading_dash';
			document.getElementById("taskDiv"+projectId).style.display = 'none';
			document.getElementById("divStatus"+projectId).value = '0';
		}
	} */
	
	
	
 	var cp = new ColorPicker('window'); 
	var cp2 = new ColorPicker('window');
	
	function isNumberKey(evt) {
	   var charCode = (evt.which) ? evt.which : event.keyCode;
	   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46)
	      return false;

	   return true;
	}

	<%

	Map hmProjectDocuments = (Map)request.getAttribute("hmProjectDocuments");
	if(hmProjectDocuments==null)hmProjectDocuments=new HashMap();
	%>

	function saveTaskAndGetTaskId(cnt, strProId) {
		/* if(confirm('Are you sure, you want to add this task?')) { */
			var proId = strProId;
			//alert("proId ===>>> " + proId);
			var taskName = document.getElementById("taskname"+strProId+"_"+cnt).value;
			var taskID = document.getElementById("taskID"+strProId+"_"+cnt).value;
			//alert("taskName ===>>> " + taskName);
			getContent('addTaskSpan'+strProId+"_"+cnt, 'SaveTaskOrSubtaskAndGetId.action?proId='+proId+'&taskId='+taskID+'&taskName='+encodeURIComponent(taskName)+"&count="+cnt+'&type=VA_Task');
		/* } */
	}


	function saveSubTaskAndGetSubTaskId(cnt, taskTRId, strProId) {
		/* if(confirm('Are you sure, you want to add this sub task?')) { */
			var proId = strProId;
			var taskId = document.getElementById("taskID"+strProId+"_"+taskTRId).value;
			var subTaskName = document.getElementById("subtaskname"+strProId+"_"+cnt).value;
			var subTaskID = document.getElementById("subTaskID"+strProId+"_"+cnt).value;
			if(parseFloat(taskId) > 0) {
				getContent('addSubTaskSpan'+strProId+"_"+cnt, 'SaveTaskOrSubtaskAndGetId.action?proId='+proId+'&subTaskId='+subTaskID+'&subTaskName='+encodeURIComponent(subTaskName)+'&taskTRId='+taskTRId+'&taskId='+taskId+"&count="+cnt+'&type=VA_SubTask');
			} else {
				alert('No task available for this sub task, Please add task.');
			}
		/* } */
	}


	function deleteTaskFromDB(taskTRId, strProId) {
		var proId = strProId;
		var taskId = document.getElementById("taskID"+strProId+"_"+taskTRId).value;
		getContent('addTaskSpan'+strProId+"_"+taskTRId, 'SaveTaskOrSubtaskAndGetId.action?proId='+proId+'&taskId='+taskId+'&type=DelTask');
	}


	function deleteSubTaskFromDB(subtaskTRId, strProId) {
		var proId = strProId;
		var subTaskId = document.getElementById("subTaskID"+strProId+"_"+subtaskTRId).value;
		getContent('addSubTaskSpan'+strProId+"_"+subtaskTRId, 'SaveTaskOrSubtaskAndGetId.action?proId='+proId+'&taskId='+subTaskId+'&type=DelSubTask');
	}


	function getSkillwiseEmployee(skillId, cnt, strProId) {
		//checkTimeFilledEmpOfAllTasks();
		//var proId = document.getElementById("pro_id").value;
		getContent('empSpan'+strProId+'_'+cnt, 'GetSkillwiseEmployee.action?proId='+strProId+'&skillId='+skillId+'&count='+cnt+'&type=VA_Task');
	}


	function getSubSkillwiseEmployee(skillId, cnt, taskTRId, strProId) {
		//var proId = document.getElementById("pro_id").value;
		getContent('subEmpSpan'+strProId+'_'+cnt, 'GetSkillwiseEmployee.action?proId='+strProId+'&skillId='+skillId+'&taskTRId='+taskTRId+'&count='+cnt+'&type=VA_SubTask');
	}


	function repeatTask(tCnt, strProId, isRecurr, isCustAdd, pageType) {
		//alert("tCnt ===>> " + tCnt);
		var usrType = document.getElementById("usrType").value;
		
		var strTaskDepend = document.getElementById("strProTaskDependency_"+strProId).value;
		//var strEmpSkills = document.getElementById("strProEmpSkills_"+strProId).value;
		var strTeamEmp = document.getElementById("strProTeamEmp_"+strProId).value;
		
		var taskCnt = document.getElementById("taskcount_"+strProId).value;
			var cnt=(parseInt(taskCnt)+1);
		    //alert("taskCnt ===>> " + taskCnt);
		    var table = document.getElementById("taskTable_"+strProId);
		    var rowCount = table.rows.length;
		    var row = table.insertRow(rowCount);
		    
		    //alert("cnt ===>> " + cnt);
		    row.id="task_TR"+strProId+"_"+cnt;
		    var cell0 = row.insertCell(0);
			cell0.innerHTML = "&nbsp;";
			
			var recurrChechbox = "";
			if(isRecurr == 'Y') {
				recurrChechbox = "<input type=\"checkbox\" name=\"recurringTask"+strProId+"\" id=\"recurringTask"+strProId+"_"+cnt+"\" onclick=\"setValue('isRecurringTask"+strProId+"_"+cnt+"');\" title=\"Add task to recurring in next frequency\"/>Recurr Task";
			}
			
		    var cell1 = row.insertCell(1);
		    //cell1.setAttribute('style', 'border-bottom: 1px solid #B6B6B6' );
		    cell1.innerHTML = "<input type=\"hidden\" name=\"taskTRId"+strProId+"\" id=\"taskTRId"+strProId+"_"+cnt+"\" value=\""+cnt+"\">"+
		    "<input type=\"hidden\" name=\"taskByCust"+strProId+"\" id=\"taskByCust"+strProId+"_"+cnt+"\" value=\""+isCustAdd+"\" />"+
		    "<input type=\"hidden\" name=\"taskDescription"+strProId+"\" id=\"taskDescription"+strProId+"_"+cnt+"\">"+
		    "<input type=\"text\" name=\"taskname"+strProId+"\" id=\"taskname"+strProId+"_"+cnt+"\" class=\"validate[required]\" style=\"width:160px; font-size:10px; height: 16px;\" onchange=\"saveTaskAndGetTaskId('"+cnt+"', '"+strProId+"')\">"+
		    "<span id=\"addTaskSpan"+strProId+"_"+cnt+"\"><input type=\"hidden\" name=\"taskID"+strProId+"\" id=\"taskID"+strProId+"_"+cnt+"\" value=\"\"></span>"+
		    "<div><a href=\"javascript:void(0)\" onclick=\"updateTaskDescription('"+strProId+"', '"+cnt+"', 'taskDescription', 'T');\">D</a>"+
		    "&nbsp;"+recurrChechbox+
		    "<input type=\"hidden\" name=\"isRecurringTask"+strProId+"\" id=\"isRecurringTask"+strProId+"_"+cnt+"\" value=\"0\"/></div>";
		    
		    var cell2 = row.insertCell(2);
		    cell2.innerHTML = "<span id=\"dependencySpan"+strProId+"_"+cnt+"\"> <select name=\"dependency"+strProId+"\" id=\"dependency"+strProId+"_"+cnt+"\" style=\"width:135px; font-size:10px;\" ><option value=\"\">Select Dependency</option>"+strTaskDepend+"</select></span>"
			    +"<select name=\"dependencyType"+strProId+"\" id=\"dependencyType"+strProId+"_"+cnt+"\" style=\"width:135px; font-size:10px;\" onchange=\"setDependencyPeriod(this.value, '"+strProId+"', '"+cnt+"', 'Task');\"><option value=\"\">Select Dependency Type</option>"
			    +"<option value=\"0\">Start-Start</option><option value=\"1\">Finish-Start</option></select>";
			
		    /* var cell2 = row.insertCell(2);
		    cell2.innerHTML = "<select name=\"dependencyType\" id=\"dependencyType"+cnt+"\" style=\"width:135px; font-size:10px;\"><option value=\"\">Select Dependency Type</option>"
			    +"<option value=\"0\">Start-Start</option><option value=\"1\">Finish-Start</option></select>"; */
			
		    var cell3 = row.insertCell(3);
			cell3.innerHTML = "<select name=\"priority"+strProId+"\" id=\"priority"+strProId+"_"+cnt+"\" style=\"width:70px; font-size:10px;\"><option value=\"0\">Low</option>"
			    +"<option value=\"1\">Medium</option><option value=\"2\">High</option></select>";
			
			/* var cell4 = row.insertCell(4);
			cell4.innerHTML = "<select name=\"empSkills"+strProId+"\" id=\"empSkills"+strProId+"_"+cnt+"\" style=\"width:85px; font-size: 10px;\" onchange=\"getSkillwiseEmployee(this.value, '"+cnt+"', '"+strProId+"');\">"
				+"<option value=\"\">Select Skill</option>"+strEmpSkills+"</select>"; */
			//cell4.innerHTML = optSkills;	
			
			if(usrType == '<%=IConstants.CUSTOMER %>') {
				var cell5 = row.insertCell(4);
				cell5.innerHTML = "<input type=\"hidden\" name=\"emp_id"+strProId+"_"+cnt+"\" id=\"hide_emp_id"+strProId+"_"+cnt+"\" />";
			} else { 
				var cell5 = row.insertCell(4);
				cell5.innerHTML = "<span id=\"empSpan"+strProId+"_"+cnt+"\"><select name=\"emp_id"+strProId+"_"+cnt+"\" id=\"emp_id"+strProId+"_"+cnt+"\" style=\"width:140px; font-size: 10px;\" class=\"validate[required]\" multiple size=\"3\">"
					+"<option value=\"\">Select Employee</option>"+strTeamEmp+"</select></span>";
			}	
			//cell5.innerHTML = "<span id=\"empSpan"+cnt+"\">"+opt2+"</span>";
			
			var cell6 = row.insertCell(5);
			cell6.innerHTML = "<input type=\"text\" id=\"startDate"+strProId+"_"+cnt+"\" name=\"startDate"+strProId+"\" class=\"validate[required]\" style=\"width:55px; font-size:10px; height: 16px;\">";

			var cell7 = row.insertCell(6);
			cell7.innerHTML = "<input type=\"text\" id=\"deadline1"+strProId+"_"+cnt+"\" class=\"validate[required]\" name=\"deadline1"+strProId+"\" style=\"width:55px; font-size:10px; height: 16px;\">";
			
			var cell8 = row.insertCell(7);
			cell8.innerHTML = "<input type=\"text\" id=\"idealTime"+strProId+"_"+cnt+"\" name=\"idealTime"+strProId+"\" onkeypress=\"return isNumberKey(event)\" class=\"validate[required]\" style=\"width:30px; font-size:10px; height: 16px; text-align:right;\">";
			
			var cell9 = row.insertCell(8);
			cell9.innerHTML = "&nbsp;";
			
			var cell10 = row.insertCell(9);
			cell10.innerHTML = "<input type=\"text\" name=\"colourCode"+strProId+"\" id=\"colourCode"+strProId+"_"+cnt+"\" class=\"validate[required]\" style=\"width:7px; font-size:10px; height: 16px;\" readonly=\"readonly\"/>";
				//+"<img align=\"left\" style=\"cursor: pointer;position:absolute; padding:5px 0 0 5px\" src=\"images1/color_palate.png\""+ 
				//"id=\"pick1\" onclick=\"cp2.select(document.getElementById('frm_ViewAllProjects_"+strProId+"').colourCode"+strProId+"_"+cnt+",'pick1'); return false;\"/>"+
				//"<span class=\"hint ml_25\">Choose a colour for this subtask. This colour will be marked in project grant chart."+
				//"<span class=\"hint-pointer\">&nbsp;</span></span>";
			
			var cell11 = row.insertCell(10);
			cell11.setAttribute("nowrap","nowrap");
			cell11.setAttribute("valign","top");
			cell11.innerHTML = "<select name=\"taskActions"+strProId+"_"+cnt+"\" id=\"taskActions"+strProId+"_"+cnt+"\" style=\"width: 100px;\" onchange=\"executeTaskActions(this.value, this.parentNode.parentNode.rowIndex, '"+cnt+"', '"+strProId+"', '', '', '"+isRecurr+"', '"+isCustAdd+"', '"+pageType+"');\">"+
			"<option value=\"\">Actions</option><option value=\"1\">Delete</option><option value=\"3\">Repeat Task </option><option value=\"4\">Add Sub-task </option>"+
			"</select>";
        
			/* cell11.innerHTML = "<a name=\"addName\" href=\"javascript:void(0)\" onclick=\"repeatTask('"+cnt+"', '"+strProId+"')\" title=\"Repeat this task\" ><img src=\"images1/icons/icons/repeat_task.png\" style=\"width: 16px; height: 16px;\" /></a>"+
					"<a href=\"javascript:void(0)\" onclick=\"addNewTask('"+strProId+"');\" title=\"Add new task\" ><img src=\"images1/icons/icons/add_task.png\" style=\"width: 16px; height: 16px;\" /></a>"+
					"<a href=\"javascript:void(0)\" class=\"del\" onclick=\"deleteTask('"+cnt+"', '"+strProId+"')\" id=\""+cnt+"\" title=\"Remove this task\"></a>"+ //<img src=\"images1/icons/icons/close_button_icon.png\" />
					"<a href=\"javascript:void(0)\" onclick=\"addNewSubTask('"+cnt+"',this.parentNode.parentNode.rowIndex, '"+strProId+"')\" title=\"Add new sub task\" ><img src=\"images1/icons/icons/add_sub_task.png\" style=\"width: 16px; height: 16px;\" /></a>";
			 */
		    document.getElementById("taskcount_"+strProId).value = cnt;
		    
		    //alert("taskname 5 === " + document.getElementById('taskname'+tCnt).value);
		    document.getElementById('taskname'+strProId+'_'+cnt).value = document.getElementById('taskname'+strProId+'_'+tCnt).value;
		    document.getElementById('taskDescription'+strProId+'_'+cnt).value = document.getElementById('taskDescription'+strProId+'_'+tCnt).value;
		    
		    getTasksForDependency(cnt, strProId);
		    document.getElementById('dependency'+strProId+'_'+cnt).selectedIndex = document.getElementById('dependency'+strProId+'_'+tCnt).selectedIndex;
		    document.getElementById('dependencyType'+strProId+'_'+cnt).selectedIndex = document.getElementById('dependencyType'+strProId+'_'+tCnt).selectedIndex;
		    document.getElementById('priority'+strProId+'_'+cnt).selectedIndex = document.getElementById('priority'+strProId+'_'+tCnt).selectedIndex;
		    
		    //document.getElementById('empSkills'+strProId+'_'+cnt).selectedIndex = document.getElementById('empSkills'+strProId+'_'+tCnt).selectedIndex;
	    	document.getElementById('emp_id'+strProId+'_'+cnt).selectedIndex = document.getElementById('emp_id'+strProId+'_'+tCnt).selectedIndex;
	    	
	    	document.getElementById('startDate'+strProId+'_'+cnt).value = document.getElementById('startDate'+strProId+'_'+tCnt).value;
	    	document.getElementById('deadline1'+strProId+'_'+cnt).value = document.getElementById('deadline1'+strProId+'_'+tCnt).value;
	    	
	    	document.getElementById('idealTime'+strProId+'_'+cnt).value = document.getElementById('idealTime'+strProId+'_'+tCnt).value;
	    	
	    	document.getElementById('colourCode'+strProId+'_'+cnt).value = document.getElementById('colourCode'+strProId+'_'+tCnt).value;
	    	document.getElementById('colourCode'+strProId+'_'+cnt).style.backgroundColor = document.getElementById('colourCode'+strProId+'_'+tCnt).value;

		    setDate(cnt, strProId);
		}
		

	function addNewTask(strProId, isRecurr, isCustAdd, pageType) { //
		
		var usrType = document.getElementById("usrType").value;
	
		var taskCnt = document.getElementById("taskcount_"+strProId).value;
		//alert("taskCnt ===>> " + taskCnt);
		var strTaskDepend = document.getElementById("strProTaskDependency_"+strProId).value;
		//var strEmpSkills = document.getElementById("strProEmpSkills_"+strProId).value;
		var strTeamEmp = document.getElementById("strProTeamEmp_"+strProId).value;
		
			var cnt=(parseInt(taskCnt)+1);
		    //alert("taskCnt ===>> " + taskCnt);
		    var table = document.getElementById("taskTable_"+strProId);
		    var rowCount = table.rows.length;
		    var row = table.insertRow(rowCount);
		    var myColor = '#'+(Math.random()*0xFFFFFF<<0).toString(16);
		    //alert("myColor ===>> " + myColor);
		    row.id="task_TR"+strProId+"_"+cnt;
		    var cell0 = row.insertCell(0);
			cell0.innerHTML = "&nbsp;";
			
			var recurrChechbox = "";
			if(isRecurr == 'Y') {
				recurrChechbox = "<input type=\"checkbox\" name=\"recurringTask"+strProId+"\" id=\"recurringTask"+strProId+"_"+cnt+"\" onclick=\"setValue('isRecurringTask"+strProId+"_"+cnt+"');\" title=\"Add task to recurring in next frequency\"/>Recurr Task";
			}
		    var cell1 = row.insertCell(1);
		    //cell1.setAttribute('style', 'border-bottom: 1px solid #B6B6B6' );
		    cell1.innerHTML = "<input type=\"hidden\" name=\"taskTRId"+strProId+"\" id=\"taskTRId"+strProId+"_"+cnt+"\" value=\""+cnt+"\">"+
		    "<input type=\"hidden\" name=\"taskByCust"+strProId+"\" id=\"taskByCust"+strProId+"_"+cnt+"\" value=\""+isCustAdd+"\" />"+
		    "<input type=\"hidden\" name=\"taskDescription"+strProId+"\" id=\"taskDescription"+strProId+"_"+cnt+"\">"+
		    "<input type=\"text\" name=\"taskname"+strProId+"\" id=\"taskname"+strProId+"_"+cnt+"\" class=\"validate[required]\" style=\"width:160px; font-size:10px; height: 16px;\" onchange=\"saveTaskAndGetTaskId('"+cnt+"', '"+strProId+"')\">"+
		    "<span id=\"addTaskSpan"+strProId+"_"+cnt+"\"><input type=\"hidden\" name=\"taskID"+strProId+"\" id=\"taskID"+strProId+"_"+cnt+"\" value=\"\"></span>"+
		    "<div><a href=\"javascript:void(0)\" onclick=\"updateTaskDescription('"+strProId+"', '"+cnt+"', 'taskDescription', 'T');\">D</a>"+
		    "&nbsp;"+recurrChechbox+
		    "<input type=\"hidden\" name=\"isRecurringTask"+strProId+"\" id=\"isRecurringTask"+strProId+"_"+cnt+"\" value=\"0\"/></div>";
		    
		    var cell2 = row.insertCell(2);
		    cell2.innerHTML = "<span id=\"dependencySpan"+strProId+"_"+cnt+"\"> <select name=\"dependency"+strProId+"\" id=\"dependency"+strProId+"_"+cnt+"\" style=\"width:135px; font-size:10px;\" ><option value=\"\">Select Dependency</option>"+strTaskDepend+"</select></span>"
			    +"<select name=\"dependencyType"+strProId+"\" id=\"dependencyType"+strProId+"_"+cnt+"\" style=\"width:135px; font-size:10px; margin-top:7px;\" onchange=\"setDependencyPeriod(this.value, '"+strProId+"', '"+cnt+"', 'Task');\"><option value=\"\">Select Dependency Type</option>"
			    +"<option value=\"0\">Start-Start</option><option value=\"1\">Finish-Start</option></select>";
			
		    /* var cell2 = row.insertCell(2);
		    cell2.innerHTML = "<select name=\"dependencyType\" id=\"dependencyType"+cnt+"\" style=\"width:135px; font-size:10px;\"><option value=\"\">Select Dependency Type</option>"
			    +"<option value=\"0\">Start-Start</option><option value=\"1\">Finish-Start</option></select>"; */
			
		    var cell3 = row.insertCell(3);
			cell3.innerHTML = "<select name=\"priority"+strProId+"\" id=\"priority"+strProId+"_"+cnt+"\" style=\"width:70px; font-size:10px;\"><option value=\"0\">Low</option>"
			    +"<option value=\"1\">Medium</option><option value=\"2\">High</option></select>";
			
			/* var cell4 = row.insertCell(4);
			cell4.innerHTML = "<select name=\"empSkills"+strProId+"\" id=\"empSkills"+strProId+"_"+cnt+"\" style=\"width:85px; font-size: 10px;\" onchange=\"getSkillwiseEmployee(this.value, '"+cnt+"', '"+strProId+"');\">"
				+"<option value=\"\">Select Skill</option>"+strEmpSkills+"</select>"; */
			//cell4.innerHTML = optSkills;	
			
			if(usrType == '<%=IConstants.CUSTOMER %>') {
				var cell5 = row.insertCell(4);
				cell5.innerHTML = "<input type=\"hidden\" name=\"emp_id"+strProId+"_"+cnt+"\" id=\"hide_emp_id"+strProId+"_"+cnt+"\" />";
			} else { 
				var cell5 = row.insertCell(4);
				cell5.innerHTML = "<span id=\"empSpan"+strProId+"_"+cnt+"\"><select name=\"emp_id"+strProId+"_"+cnt+"\" id=\"emp_id"+strProId+"_"+cnt+"\" style=\"width:140px; font-size: 10px;\" class=\"validate[required]\" multiple size=\"3\">"
					+"<option value=\"\">Select Employee</option>"+strTeamEmp+"</select></span>";
			}	
			//cell5.innerHTML = "<span id=\"empSpan"+cnt+"\">"+opt2+"</span>";
			
			var cell6 = row.insertCell(5);
			cell6.innerHTML = "<input type=\"text\" id=\"startDate"+strProId+"_"+cnt+"\" name=\"startDate"+strProId+"\" class=\"validate[required]\" style=\"width:55px; font-size:10px; height: 16px;\">";

			var cell7 = row.insertCell(6);
			cell7.innerHTML = "<input type=\"text\" id=\"deadline1"+strProId+"_"+cnt+"\" class=\"validate[required]\" name=\"deadline1"+strProId+"\" style=\"width:55px; font-size:10px; height: 16px;\">";
			
			var cell8 = row.insertCell(7);
			cell8.innerHTML = "<input type=\"text\" id=\"idealTime"+strProId+"_"+cnt+"\" name=\"idealTime"+strProId+"\" onkeypress=\"return isNumberKey(event)\" class=\"validate[required]\" style=\"width:30px; font-size:10px; height: 16px; text-align:right;\">";
			
			var cell9 = row.insertCell(8);
			cell9.innerHTML = "&nbsp;";
			
			var cell10 = row.insertCell(9);
			cell10.innerHTML = "<input type=\"text\" name=\"colourCode"+strProId+"\" id=\"colourCode"+strProId+"_"+cnt+"\" class=\"validate[required]\" style=\"width:7px; font-size:10px; height: 16px; background-color: "+myColor+"\" value=\""+myColor+"\" readonly=\"readonly\"/>";
				//+"<img align=\"left\" style=\"cursor: pointer;position:absolute; padding:5px 0 0 5px\" src=\"images1/color_palate.png\""+ 
				//"id=\"pick1\" onclick=\"cp2.select(document.getElementById('frm_ViewAllProjects_"+strProId+"').colourCode"+cnt+",'pick1'); return false;\"/>"+
				//"<span class=\"hint ml_25\">Choose a colour for this subtask. This colour will be marked in project grant chart."+
				//"<span class=\"hint-pointer\">&nbsp;</span></span>";
						
			var cell11 = row.insertCell(10);
			cell11.setAttribute("nowrap","nowrap");
			cell11.setAttribute("valign","top");
			cell11.innerHTML = "<select name=\"taskActions"+strProId+"_"+cnt+"\" id=\"taskActions"+strProId+"_"+cnt+"\" style=\"width: 100px;\" onchange=\"executeTaskActions(this.value, this.parentNode.parentNode.rowIndex, '"+cnt+"', '"+strProId+"', '', '', '"+isRecurr+"', '"+isCustAdd+"', '"+pageType+"');\">"+
			"<option value=\"\">Actions</option><option value=\"1\">Delete</option><option value=\"3\">Repeat Task </option><option value=\"4\">Add Sub-task </option>"+
			"</select>";
			
			/* cell11.innerHTML = "<a name=\"addName\" href=\"javascript:void(0)\" onclick=\"repeatTask('"+cnt+"', '"+strProId+"')\" title=\"Repeat this task\" ><img src=\"images1/icons/icons/repeat_task.png\" style=\"width: 16px; height: 16px;\" /></a>"+
					"<a href=\"javascript:void(0)\" onclick=\"addNewTask('"+strProId+"');\" title=\"Add new task\" ><img src=\"images1/icons/icons/add_task.png\" style=\"width: 16px; height: 16px;\" /></a>"+
					"<a href=\"javascript:void(0)\" class=\"del\" onclick=\"deleteTask('"+cnt+"', '"+strProId+"')\" id=\""+cnt+"\" title=\"Remove this task \"></a>"+ //<img src=\"images1/icons/icons/close_button_icon.png\" />
					"<a href=\"javascript:void(0)\" onclick=\"addNewSubTask('"+cnt+"',this.parentNode.parentNode.rowIndex, '"+strProId+"')\" title=\"Add new sub task\" ><img src=\"images1/icons/icons/add_sub_task.png\" style=\"width: 16px; height: 16px;\" /></a>"; */
					
		    document.getElementById("taskcount_"+strProId).value = cnt;
		    
		    getTasksForDependency(cnt, strProId);
		    
		    setDate(cnt, strProId);
		   
		}
		
		
		function getTasksForDependency(cnt, strProId) {
			//var proId = document.getElementById("pro_id").value;
			getContent('dependencySpan'+strProId+'_'+cnt, 'SaveTaskOrSubtaskAndGetId.action?proId='+strProId+"&count="+cnt+'&type=VA_GetTasks');
		}

		
	function deleteTask(count, strProId) {
		//alert("count ==========>> " + count);
		 if(document.getElementById("TskTRId"+strProId+"_"+count)) {
			/*  alert("in subTaskTRId --->> ");
		 }
		if(parseFloat(subTaskCnt) > 0) { */
			alert("Please first delete sub task of this task then delete this task.");
		} else {
			if(confirm('Are you sure, you want to delete this task?')) {
				deleteTaskFromDB(count, strProId);
				var trIndex = document.getElementById("task_TR"+strProId+"_"+count).rowIndex;
			    document.getElementById("taskTable_"+strProId).deleteRow(trIndex);
			}
		}
	}
	
		
	function repeatSubTask(stCnt, taskTRId, rwIndex, strProId, isRecurr, isCustAdd, pageType) {
		var usrType = document.getElementById("usrType").value;
		
		//alert("stCnt ===>> " + stCnt + "  taskTRId ===>> " + taskTRId + "  rwIndex ===>> " + rwIndex);
		var taskCnt = document.getElementById("taskcount_"+strProId).value;
		var strTaskDepend = "";
		if(document.getElementById("strProSubTaskDependency_"+strProId+"_"+taskTRId)) {
			strTaskDepend = document.getElementById("strProSubTaskDependency_"+strProId+"_"+taskTRId).value;
		}
		//var strEmpSkills = document.getElementById("strProEmpSkills_"+strProId).value;
		var strTeamEmp = document.getElementById("strProTeamEmp_"+strProId).value;
		
			var cnt=(parseInt(taskCnt)+1);
			var val=(parseInt(rwIndex)+1);
		    //alert("val ===>> " + val);
		    var table = document.getElementById("taskTable_"+strProId);
		    var rowCount = table.rows.length;
		    var row = table.insertRow(val);
		    
		    //alert("cnt ===>> " + cnt);
		    row.id="task_TR"+strProId+"_"+cnt;
		    var cell0 = row.insertCell(0);
			cell0.innerHTML = "&nbsp;";
			
			var recurrChechbox = "";
			if(isRecurr == 'Y') {
				recurrChechbox = "<input type=\"checkbox\" name=\"recurringSubTask"+strProId+"_"+taskTRId+"\" id=\"recurringSubTask"+strProId+"_"+cnt+"\" onclick=\"setValue('isRecurringSubTask"+strProId+"_"+cnt+"');\" title=\"Add sub task to recurring in next frequency\"/>Recurr Subtask";
			}
			
		    var cell1 = row.insertCell(1);
		    cell1.setAttribute('style', 'text-align: right;');
		    cell1.innerHTML = "<input type=\"hidden\" name=\"TskTRId"+strProId+"_"+taskTRId+"\" id=\"TskTRId"+strProId+"_"+taskTRId+"\" value=\""+taskTRId+"\"><input type=\"hidden\" name=\"subTaskTRId"+strProId+"_"+taskTRId+"\" id=\"subTaskTRId"+strProId+"_"+cnt+"\" value=\""+cnt+"\">"+
		    "<input type=\"hidden\" name=\"subTaskByCust"+strProId+"_"+taskTRId+"\" id=\"subTaskByCust"+strProId+"_"+cnt+"\" value=\""+isCustAdd+"\" />"+
		    "<input type=\"hidden\" name=\"subTaskDescription"+strProId+"_"+taskTRId+"\" id=\"subTaskDescription"+strProId+"_"+cnt+"\">"+
		    "<input type=\"text\" name=\"subtaskname"+strProId+"_"+taskTRId+"\" id=\"subtaskname"+strProId+"_"+cnt+"\" class=\"validate[required]\" style=\"width:120px; font-size:10px; height: 16px;\" onchange=\"saveSubTaskAndGetSubTaskId('"+cnt+"', '"+taskTRId+"', '"+strProId+"')\">"+
		    "<span id=\"addSubTaskSpan"+strProId+"_"+cnt+"\"><input type=\"hidden\" name=\"subTaskID"+strProId+"_"+taskTRId+"\" id=\"subTaskID"+strProId+"_"+cnt+"\" value=\"\"></span>"+
		    "<div><a href=\"javascript:void(0)\" onclick=\"updateTaskDescription('"+strProId+"', '"+cnt+"', 'subTaskDescription', 'ST');\">D</a>"+
		    "&nbsp;"+recurrChechbox+
		    "<input type=\"hidden\" name=\"isRecurringSubTask"+strProId+"_"+taskTRId+"\" id=\"isRecurringSubTask"+strProId+"_"+cnt+"\" value=\"0\"/></div>";
		    
		    var cell2 = row.insertCell(2);
		    cell2.innerHTML = "<span id=\"subDependencySpan"+strProId+"_"+cnt+"\"><select name=\"subDependency"+strProId+"_"+taskTRId+"\" id=\"subDependency"+strProId+"_"+cnt+"\" style=\"width:135px; font-size:10px;\"><option value=\"\">Select Dependency</option>"+strTaskDepend+"</select></span>"+
			    "<select name=\"subDependencyType"+strProId+"_"+taskTRId+"\" id=\"subDependencyType"+strProId+"_"+cnt+"\" style=\"width:135px; font-size:10px;\" onchange=\"setDependencyPeriod(this.value, '"+strProId+"', '"+cnt+"', 'SubTask');\"><option value=\"\">Select Dependency Type</option>"
			    +"<option value=\"0\">Start-Start</option><option value=\"1\">Finish-Start</option></select>";
			
		    /* var cell2 = row.insertCell(2);
		    cell2.innerHTML = "<select name=\"subDependencyType"+taskTRId+"\" id=\"subDependencyType"+cnt+"\" style=\"width:135px; font-size:10px;\"><option value=\"\">Select Dependency Type</option>"
			    +"<option value=\"0\">Start-Start</option><option value=\"1\">Finish-Start</option></select>"; */
		    var cell3 = row.insertCell(3);
			cell3.innerHTML = "<select name=\"subpriority"+strProId+"_"+taskTRId+"\" id=\"subpriority"+strProId+"_"+cnt+"\" style=\"width:70px; font-size:10px;\"><option value=\"0\">Low</option>"
		    +"<option value=\"1\">Medium</option><option value=\"2\">High</option></select>";
			
			/* var cell4 = row.insertCell(4);
			cell4.innerHTML = "<select name=\"empSubSkills"+strProId+"_"+taskTRId+"\" id=\"empSubSkills"+strProId+"_"+cnt+"\" style=\"width:85px; font-size: 10px;\" onchange=\"getSubSkillwiseEmployee(this.value, '"+cnt+"', '"+taskTRId+"', '"+strProId+"');\">"
			+"<option value=\"\">Select Skill</option>"+strEmpSkills+"</select>"; */
			
			if(usrType == '<%=IConstants.CUSTOMER %>') {
				var cell5 = row.insertCell(4);
				cell5.innerHTML = "<input type=\"hidden\" name=\"sub_emp_id"+strProId+"_"+taskTRId+"_"+cnt+"\" id=\"hide_sub_emp_id"+strProId+"_"+cnt+"\" />";
			} else {
				var cell5 = row.insertCell(4);
				cell5.innerHTML = "<span id=\"subEmpSpan"+cnt+"\"><select name=\"sub_emp_id"+strProId+"_"+taskTRId+"_"+cnt+"\" id=\"sub_emp_id"+strProId+"_"+cnt+"\" style=\"width:140px; font-size: 10px;\" class=\"validate[required]\" multiple size=\"3\">"
					+"<option value=\"\">Select Employee</option>"+strTeamEmp+"</select></span>";
			}
			
			var cell6 = row.insertCell(5);
			cell6.innerHTML = "<input type=\"text\" id=\"substartDate"+strProId+"_"+cnt+"\" name=\"substartDate"+strProId+"_"+taskTRId+"\" class=\"validate[required]\" style=\"width:55px; font-size:10px; height: 16px;\">";

			var cell7 = row.insertCell(6);
			cell7.innerHTML = "<input type=\"text\" id=\"subdeadline1"+strProId+"_"+cnt+"\" class=\"validate[required]\" name=\"subdeadline1"+strProId+"_"+taskTRId+"\" style=\"width:55px; font-size:10px; height: 16px;\">";
			
			var cell8 = row.insertCell(7);
			cell8.innerHTML = "<input type=\"text\" id=\"subidealTime"+strProId+"_"+cnt+"\" name=\"subidealTime"+strProId+"_"+taskTRId+"\" onkeypress=\"return isNumberKey(event)\" class=\"validate[required]\" style=\"width:30px; font-size:10px; height: 16px; text-align:right;\">";
			
			var cell9 = row.insertCell(8);
			cell9.innerHTML = "&nbsp;";
			
			var cell10 = row.insertCell(9);
			cell10.innerHTML = "<input type=\"text\" name=\"subcolourCode"+strProId+"_"+taskTRId+"\" id=\"subcolourCode"+strProId+"_"+cnt+"\" class=\"validate[required]\" style=\"width:7px; font-size:10px; height: 16px;\" readonly=\"readonly\"/>";
				//+"<img align=\"left\" style=\"cursor: pointer;position:absolute; padding:5px 0 0 5px\" src=\"images1/color_palate.png\""+ 
				//"id=\"pick1\" onclick=\"cp2.select(document.getElementById('frm_ViewAllProjects_"+strProId+"').subcolourCode"+strProId+"_"+cnt+",'pick1'); return false;\"/>"+
				//"<span class=\"hint ml_25\">Choose a colour for this subtask. This colour will be marked in project grant chart."+
				//"<span class=\"hint-pointer\">&nbsp;</span></span>";
						
			var cell11 = row.insertCell(10);
			cell11.setAttribute("nowrap","nowrap");
			cell11.setAttribute("valign","top");
			cell11.innerHTML = "<select name=\"subtaskActions"+strProId+"_"+cnt+"\" id=\"subtaskActions"+strProId+"_"+cnt+"\" style=\"width: 100px;\" onchange=\"executeSubTaskActions(this.value, this.parentNode.parentNode.rowIndex, '"+cnt+"', '"+strProId+"', '',  '"+taskTRId+"', '', '"+isRecurr+"', '"+isCustAdd+"', '"+pageType+"');\">"+
			"<option value=\"\">Actions</option><option value=\"1\">Delete</option><option value=\"3\">Repeat Sub-task </option><option value=\"4\">Add Sub-task </option>"+
			"</select>";
			
			/* cell11.innerHTML = "<a name=\"addName\" href=\"javascript:void(0)\" onclick=\"repeatSubTask('"+cnt+"', '"+taskTRId+"', this.parentNode.parentNode.rowIndex, '"+strProId+"')\" title=\"Repeat this sub task\" ><img src=\"images1/icons/icons/repeat_task.png\" style=\"width: 16px; height: 16px;\" /></a>"+
					"<a href=\"javascript:void(0)\" onclick=\"addNewTask('"+strProId+"');\" title=\"Add new task\" ><img src=\"images1/icons/icons/add_task.png\" style=\"width: 16px; height: 16px;\" /></a>"+
					"<a href=\"javascript:void(0)\" class=\"del\" onclick=\"deleteSubTask('"+cnt+"', '"+strProId+"')\" id=\""+cnt+"\" title=\" Remove this sub task\" ></a>"+ //<img src=\"images1/icons/icons/close_button_icon.png\" />
					"<a href=\"javascript:void(0)\" onclick=\"addNewSubTask('"+taskTRId+"', this.parentNode.parentNode.rowIndex, '"+strProId+"')\" title=\"Add new sub task\" ><img src=\"images1/icons/icons/add_sub_task.png\" style=\"width: 16px; height: 16px;\" /></a>"; */
		    
			document.getElementById("taskcount_"+strProId).value = cnt;
		    
		    //document.getElementById("empSubSkills"+strProId+"_"+cnt).setAttribute("onchange", "getSubSkillwiseEmployee(this.value, '"+cnt+"', '"+taskTRId+"', '"+strProId+"');");
		    
		    document.getElementById('subtaskname'+strProId+'_'+cnt).value = document.getElementById('subtaskname'+strProId+'_'+stCnt).value;
		    document.getElementById('subTaskDescription'+strProId+'_'+cnt).value = document.getElementById('subTaskDescription'+strProId+'_'+stCnt).value;
		    
		    getSubTasksForDependency(cnt, taskTRId, strProId);
		    document.getElementById('subDependency'+strProId+'_'+cnt).selectedIndex = document.getElementById('subDependency'+strProId+'_'+stCnt).selectedIndex;
		    document.getElementById('subDependencyType'+strProId+'_'+cnt).selectedIndex = document.getElementById('subDependencyType'+strProId+'_'+stCnt).selectedIndex;
		    document.getElementById('subpriority'+strProId+'_'+cnt).selectedIndex = document.getElementById('subpriority'+strProId+'_'+stCnt).selectedIndex;
		    
		    //document.getElementById('empSubSkills'+strProId+'_'+cnt).selectedIndex = document.getElementById('empSubSkills'+strProId+'_'+stCnt).selectedIndex;
	    	document.getElementById('sub_emp_id'+strProId+'_'+cnt).selectedIndex = document.getElementById('sub_emp_id'+strProId+'_'+stCnt).selectedIndex;
	    	
	    	document.getElementById('substartDate'+strProId+'_'+cnt).value = document.getElementById('substartDate'+strProId+'_'+stCnt).value;
	    	document.getElementById('subdeadline1'+strProId+'_'+cnt).value = document.getElementById('subdeadline1'+strProId+'_'+stCnt).value;
	    	
	    	document.getElementById('subidealTime'+strProId+'_'+cnt).value = document.getElementById('subidealTime'+strProId+'_'+stCnt).value;
	    	
	    	document.getElementById('subcolourCode'+strProId+'_'+cnt).value = document.getElementById('subcolourCode'+strProId+'_'+stCnt).value;
	    	document.getElementById('subcolourCode'+strProId+'_'+cnt).style.backgroundColor = document.getElementById('subcolourCode'+strProId+'_'+stCnt).value;
	        
		    setSubDate(cnt, strProId);

		}


	function addNewSubTask(taskTRId, rwIndex, strProId, isRecurr, isCustAdd, pageType) {
		var usrType = document.getElementById("usrType").value;
		
		var taskCnt = document.getElementById("taskcount_"+strProId).value;
		//alert("taskCnt ===>> " + taskCnt+" -- (taskTRId ===>> " + taskTRId + " -- rwIndex ===>> " + rwIndex + " -- strProId ===>> " + strProId);
		var strTaskDepend = "";
		if(document.getElementById("strProSubTaskDependency_"+strProId+"_"+taskTRId)) {
			strTaskDepend = document.getElementById("strProSubTaskDependency_"+strProId+"_"+taskTRId).value;
		}
		//var strEmpSkills = document.getElementById("strProEmpSkills_"+strProId).value;
		var strTeamEmp = document.getElementById("strProTeamEmp_"+strProId).value;
		
			var cnt=(parseInt(taskCnt)+1);
			var val=(parseInt(rwIndex)+1);
		    //alert("val ===>> " + val);
		    var table = document.getElementById("taskTable_"+strProId);
		    var rowCount = table.rows.length;
		    var row = table.insertRow(val);
		    var myColor = '#'+(Math.random()*0xFFFFFF<<0).toString(16);
		    
		    //alert("cnt ===>> " + cnt);
		    row.id="task_TR"+strProId+"_"+cnt;
		    var cell0 = row.insertCell(0);
			cell0.innerHTML = "&nbsp;";
		    
			var recurrChechbox = "";
			if(isRecurr == 'Y') {
				recurrChechbox = "<input type=\"checkbox\" name=\"recurringSubTask"+strProId+"_"+taskTRId+"\" id=\"recurringSubTask"+strProId+"_"+cnt+"\" onclick=\"setValue('isRecurringSubTask"+strProId+"_"+cnt+"');\" title=\"Add sub task to recurring in next frequency\"/>Recurr Subtask";
			}
			
		    var cell1 = row.insertCell(1);
		    cell1.setAttribute('style', 'text-align: right;' );
		    cell1.innerHTML = "<input type=\"hidden\" name=\"TskTRId"+strProId+"_"+taskTRId+"\" id=\"TskTRId"+strProId+"_"+taskTRId+"\" value=\""+taskTRId+"\"><input type=\"hidden\" name=\"subTaskTRId"+strProId+"_"+taskTRId+"\" id=\"subTaskTRId"+strProId+"_"+cnt+"\" value=\""+cnt+"\">"+
		    "<input type=\"hidden\" name=\"subTaskByCust"+strProId+"_"+taskTRId+"\" id=\"subTaskByCust"+strProId+"_"+cnt+"\" value=\""+isCustAdd+"\" />"+
		    "<input type=\"hidden\" name=\"subTaskDescription"+strProId+"_"+taskTRId+"\" id=\"subTaskDescription"+strProId+"_"+cnt+"\">"+
		    "<input type=\"text\" name=\"subtaskname"+strProId+"_"+taskTRId+"\" id=\"subtaskname"+strProId+"_"+cnt+"\" class=\"validate[required]\" style=\"width:120px; font-size:10px; height: 16px;\" onchange=\"saveSubTaskAndGetSubTaskId('"+cnt+"', '"+taskTRId+"', '"+strProId+"')\">"+
		    "<span id=\"addSubTaskSpan"+strProId+"_"+cnt+"\"><input type=\"hidden\" name=\"subTaskID"+strProId+"_"+taskTRId+"\" id=\"subTaskID"+strProId+"_"+cnt+"\" value=\"\"></span>"+
		    "<div><a href=\"javascript:void(0)\" onclick=\"updateTaskDescription('"+strProId+"', '"+cnt+"', 'subTaskDescription', 'ST');\">D</a>"+
		    "&nbsp;"+recurrChechbox+
		    "<input type=\"hidden\" name=\"isRecurringSubTask"+strProId+"_"+taskTRId+"\" id=\"isRecurringSubTask"+strProId+"_"+cnt+"\" value=\"0\"/></div>";
		    
		    var cell2 = row.insertCell(2);
		    cell2.innerHTML = "<span id=\"subDependencySpan"+strProId+"_"+cnt+"\"><select name=\"subDependency"+strProId+"_"+taskTRId+"\" id=\"subDependency"+strProId+"_"+cnt+"\" style=\"width:135px; font-size:10px;\"><option value=\"\">Select Dependency</option>"+strTaskDepend+"</select></span>"+
			    "<select name=\"subDependencyType"+strProId+"_"+taskTRId+"\" id=\"subDependencyType"+strProId+"_"+cnt+"\" style=\"width:135px; font-size:10px;\" onchange=\"setDependencyPeriod(this.value, '"+strProId+"', '"+cnt+"', 'SubTask');\"><option value=\"\">Select Dependency Type</option>"
			    +"<option value=\"0\">Start-Start</option><option value=\"1\">Finish-Start</option></select>";
			
		    /* var cell2 = row.insertCell(2);
		    cell2.innerHTML = "<select name=\"subDependencyType"+taskTRId+"\" id=\"subDependencyType"+cnt+"\" style=\"width:135px; font-size:10px;\"><option value=\"\">Select Dependency Type</option>"
			    +"<option value=\"0\">Start-Start</option><option value=\"1\">Finish-Start</option></select>"; */
		    var cell3 = row.insertCell(3);
			cell3.innerHTML = "<select name=\"subpriority"+strProId+"_"+taskTRId+"\" id=\"subpriority"+strProId+"_"+cnt+"\" style=\"width:70px; font-size:10px;\"><option value=\"0\">Low</option>"
		    +"<option value=\"1\">Medium</option><option value=\"2\">High</option></select>";
			
			/* var cell4 = row.insertCell(4);
			cell4.innerHTML = "<select name=\"empSubSkills"+strProId+"_"+taskTRId+"\" id=\"empSubSkills"+strProId+"_"+cnt+"\" style=\"width:85px; font-size: 10px;\" onchange=\"getSubSkillwiseEmployee(this.value, '"+cnt+"', '"+taskTRId+"', '"+strProId+"');\">"
			+"<option value=\"\">Select Skill</option>"+strEmpSkills+"</select>"; */
			if(usrType == '<%=IConstants.CUSTOMER %>') {
				var cell5 = row.insertCell(4);
				cell5.innerHTML = "<input type=\"hidden\" name=\"sub_emp_id"+strProId+"_"+taskTRId+"_"+cnt+"\" id=\"hide_sub_emp_id"+strProId+"_"+cnt+"\" />";
			} else {
				var cell5 = row.insertCell(4);
				cell5.innerHTML = "<span id=\"subEmpSpan"+strProId+"_"+cnt+"\"><select name=\"sub_emp_id"+strProId+"_"+taskTRId+"_"+cnt+"\" id=\"sub_emp_id"+strProId+"_"+cnt+"\" style=\"width:140px; font-size: 10px;\" class=\"validate[required]\" multiple size=\"3\">"
					+"<option value=\"\">Select Employee</option>"+strTeamEmp+"</select></span>";
			}
			
			var cell6 = row.insertCell(5);
			cell6.innerHTML = "<input type=\"text\" id=\"substartDate"+strProId+"_"+cnt+"\" name=\"substartDate"+strProId+"_"+taskTRId+"\" class=\"validate[required]\" style=\"width:55px; font-size:10px; height: 16px;\">";

			var cell7 = row.insertCell(6);
			cell7.innerHTML = "<input type=\"text\" id=\"subdeadline1"+strProId+"_"+cnt+"\" class=\"validate[required]\" name=\"subdeadline1"+strProId+"_"+taskTRId+"\" style=\"width:55px; font-size:10px; height: 16px;\">";
			
			var cell8 = row.insertCell(7);
			cell8.innerHTML = "<input type=\"text\" id=\"subidealTime"+strProId+"_"+cnt+"\" name=\"subidealTime"+strProId+"_"+taskTRId+"\" onkeypress=\"return isNumberKey(event)\" class=\"validate[required]\" style=\"width:30px; font-size:10px; height: 16px; text-align:right;\">";
			
			var cell9 = row.insertCell(8);
			cell9.innerHTML = "&nbsp;";
			
			var cell10 = row.insertCell(9);
			cell10.innerHTML = "<input type=\"text\" name=\"subcolourCode"+strProId+"_"+taskTRId+"\" id=\"subcolourCode"+strProId+"_"+cnt+"\" class=\"validate[required]\" style=\"width:7px; font-size:10px; height: 16px; background-color: "+myColor+"\" value=\""+myColor+"\" readonly=\"readonly\"/>";
				//+"<img align=\"left\" style=\"cursor: pointer;position:absolute; padding:5px 0 0 5px\" src=\"images1/color_palate.png\""+ 
				//"id=\"pick1\" onclick=\"cp2.select(document.getElementById('frm_ViewAllProjects_"+strProId+"').subcolourCode"+strProId+"_"+cnt+",'pick1'); return false;\"/>"+
				//"<span class=\"hint ml_25\">Choose a colour for this subtask. This colour will be marked in project grant chart."+
				//"<span class=\"hint-pointer\">&nbsp;</span></span>";
			
			var cell11 = row.insertCell(10);
			cell11.setAttribute("nowrap","nowrap");
			cell11.setAttribute("valign","top");
			cell11.innerHTML = "<select name=\"subtaskActions"+strProId+"_"+cnt+"\" id=\"subtaskActions"+strProId+"_"+cnt+"\" style=\"width: 100px;\" onchange=\"executeSubTaskActions(this.value, this.parentNode.parentNode.rowIndex, '"+cnt+"', '"+strProId+"', '', '"+taskTRId+"', '', '"+isRecurr+"', '"+isCustAdd+"', '"+pageType+"');\">"+
			"<option value=\"\">Actions</option><option value=\"1\">Delete</option><option value=\"3\">Repeat Sub-task </option><option value=\"4\">Add Sub-task </option>"+
			"</select>";
			
			/* cell11.innerHTML = "<a name=\"addName\" href=\"javascript:void(0)\" onclick=\"repeatSubTask('"+cnt+"', '"+taskTRId+"', this.parentNode.parentNode.rowIndex, '"+strProId+"')\" title=\"Repeat this sub task\" ><img src=\"images1/icons/icons/repeat_task.png\" style=\"width: 16px; height: 16px;\" /></a>"+
					"<a href=\"javascript:void(0)\" onclick=\"addNewTask('"+strProId+"');\" title=\"Add new task\" ><img src=\"images1/icons/icons/add_task.png\" style=\"width: 16px; height: 16px;\" /></a>"+
					"<a href=\"javascript:void(0)\" class=\"del\" onclick=\"deleteSubTask('"+cnt+"', '"+strProId+"')\" id=\""+cnt+"\" title=\"Remove this sub task\" ></a>"+ //<img src=\"images1/icons/icons/close_button_icon.png\"/>
					"<a href=\"javascript:void(0)\" onclick=\"addNewSubTask('"+taskTRId+"', this.parentNode.parentNode.rowIndex, '"+strProId+"')\" title=\"Add new sub task\" ><img src=\"images1/icons/icons/add_sub_task.png\" style=\"width: 16px; height: 16px;\" /></a>";
		     */
			document.getElementById("taskcount_"+strProId).value = cnt;
		    
		    //document.getElementById("empSubSkills"+strProId+"_"+cnt).setAttribute("onchange", "getSubSkillwiseEmployee(this.value, '"+cnt+"', '"+taskTRId+"', '"+strProId+"');");
		    //alert("taskTRId ===>>" + taskTRId + " cnt ===>> " + cnt);
		    getSubTasksForDependency(cnt, taskTRId, strProId);
		    //alert("1 taskTRId ===>>" + taskTRId + " cnt ===>> " + cnt);
		    setSubDate(cnt, strProId);

		}
		
		
		function getSubTasksForDependency(cnt, taskTRId, strProId) {
			//var proId = document.getElementById("pro_id").value;
			//alert("proId ===>> " +proId);
			var taskId = document.getElementById("taskID"+strProId+"_"+taskTRId).value;
			//alert("taskId ===>> " +taskId);
			getContent('subDependencySpan'+strProId+"_"+cnt, 'SaveTaskOrSubtaskAndGetId.action?proId='+strProId+"&count="+cnt+"&taskId="+taskId+'&taskTRId='+taskTRId+'&type=VA_GetSubTasks');
			//alert("cnt ===>> " + cnt);
		}


	function deleteSubTask(count, strProId) {
		if(confirm('Are you sure, you want to delete this sub task?')) {
			deleteSubTaskFromDB(count, strProId);
			var trIndex = document.getElementById("task_TR"+strProId+"_"+count).rowIndex;
		    document.getElementById("taskTable_"+strProId).deleteRow(trIndex);
		}
	}

	function setDate(id, strProId) {
		
		var proStDt = document.getElementById("proStartDate_"+strProId).value;
		var proEndDt = document.getElementById("proEndDate_"+strProId).value;
		
		jQuery("#frm_ViewAllProjects_"+strProId).validationEngine();
		$("#deadline1"+strProId+"_"+id).datepicker({
			dateFormat : 'dd/mm/yy', minDate: proStDt, maxDate: proEndDt, 
			onClose: function(selectedDate){
				$("#startDate"+strProId+"_"+id).datepicker("option", "maxDate", selectedDate);
			}
		});	
		$("#startDate"+strProId+"_"+id).datepicker({
			dateFormat : 'dd/mm/yy', minDate: proStDt, maxDate: proEndDt, 
			onClose: function(selectedDate){
				$("#deadline1"+strProId+"_"+id).datepicker("option", "minDate", selectedDate);
			}
		});	
	}


	function setSubDate(id, strProId) {
		
		var proStDt = document.getElementById("proStartDate_"+strProId).value;
		var proEndDt = document.getElementById("proEndDate_"+strProId).value;
		
		jQuery("#frm_ViewAllProjects_"+strProId).validationEngine();
		$("#subdeadline1"+strProId+"_"+id).datepicker({
			dateFormat : 'dd/mm/yy', minDate: proStDt, maxDate: proEndDt, 
			onClose: function(selectedDate){
				$("#substartDate"+strProId+"_"+id).datepicker("option", "maxDate", selectedDate);
			}
		});	
		$("#substartDate"+strProId+"_"+id).datepicker({
			dateFormat : 'dd/mm/yy', minDate: proStDt, maxDate: proEndDt, 
			onClose: function(selectedDate){
				$("#subdeadline1"+strProId+"_"+id).datepicker("option", "minDate", selectedDate);
			}
		});	
	}


	function deleteRow(id) {
	    try {
	    	var row = document.getElementById(id);
			row.parentElement.removeChild(row); 
	     }catch(e) {
	        alert(e);
	    }
	}

	 
	 function checkTimeFilledEmpOfAllTasks(strProId) {
		 //taskcount sub_emp_id tstFilledEmp emp_id
		 var taskCount = document.getElementById("taskcount_"+strProId).value;
		 //alert("taskCount ===>> " + taskCount);
		 
		 var empCnt = 0;
		 var filledEmpCnt = 0;
		 for(ii=0; ii<taskCount; ii++) {
			var timeFilledEmps = "";
			if(document.getElementById("tstFilledEmp"+strProId+"_"+ii)) {
				document.getElementById("tstFilledEmp"+strProId+"_"+ii).value;
			}
			//alert("timeFilledEmps ===>> " + timeFilledEmps);
			var timeFilledEmp = timeFilledEmps.split(",");
			var choice = "";
			if(document.getElementById("emp_id"+strProId+"_"+ii)) {
				choice = document.getElementById("emp_id"+strProId+"_"+ii);
				//alert("emp_id choice ===>> " + choice);
			}
			if(document.getElementById("sub_emp_id"+strProId+"_"+ii)) {
				choice = document.getElementById("sub_emp_id"+strProId+"_"+ii);
				//alert("sub_emp_id choice ===>> " + choice);
			}
			//alert("choice ===>> " + choice);
			//alert("choice.options.length ===>> " + choice.options.length);
			
			var exportchoice = "";
			if(choice != '') {
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
			}
			var selectedEmp = "";
			if(exportchoice != '') {
				selectedEmp = exportchoice.split(",");
			}
			//alert("selectedEmp ===>> " + selectedEmp);
			
			for(var a=0; a<timeFilledEmp.length; a++) {
				if(timeFilledEmp[a] != '' && timeFilledEmp[a] != ' ') {
					for(var b=0; b<selectedEmp.length; b++) {
						if(timeFilledEmp[a] == selectedEmp[b]) {
							empCnt++;
							//alert("timeFilledEmp[a] ==>>> " + timeFilledEmp[a]);
						}
					}
					filledEmpCnt++;
				}
			}
		 }
		 //alert("empCnt ===>> " + empCnt + " filledEmpCnt ===>> " + filledEmpCnt);
		 
		if(filledEmpCnt == empCnt) {
			//return true;
			document.getElementById("addTask"+strProId).value= "Save";
			var frmName = "frm_ViewAllProjects_"+strProId;
			//alert("frmName ===>> " + frmName);
			/* $( "#addTask_"+strProId).click(function() {
				$( "#"+frmName).submit();
			}); */
			document.getElementById(frmName).submit();
			//document.frmName.submit();
		} else {
			alert("Resource already fill timesheet against this task, so can't remove resource from this task.");
			//return false;
		}
	 }
	 
	
	function approveProjectsAndTasks(pageType) {
		/* var tasks = document.getElementById("cb"); */
		var tasks = document.getElementsByName('cb');
		var i;
		var taskIds = "";
		for (i = 0; i < tasks.length; i++) {
		  if (tasks[i].checked) {
			  if(taskIds == "") {
				  taskIds = taskIds + tasks[i].value;
			  } else {
				  taskIds = taskIds + "," + tasks[i].value;
			  }
		  }
		}
		
		var pros = document.getElementsByName('approvePr');
		var i;
		var proIds = "";
		for (i = 0; i < pros.length; i++) {
		  if (pros[i].checked) {
			  if(proIds == "") {
				  proIds = proIds + pros[i].value;
			  } else {
				  proIds = proIds + "," + pros[i].value;
			  }
		  }
		}
		var action = 'ViewAllProjects.action?cb='+taskIds+'&approvePr='+proIds+'&approve=approve&pageType='+pageType;
		window.location = action;
		//alert("txt ===>> "+ txt);
	}
	
	function blockedProjects(pageType) {
		/* var tasks = document.getElementById("cb"); */
		var pros = document.getElementsByName('approvePr');
		var i;
		var proIds = "";
		for (i = 0; i < pros.length; i++) {
		  if (pros[i].checked) {
			  if(proIds == "") {
				  proIds = proIds + pros[i].value;
			  } else {
				  proIds = proIds + "," + pros[i].value;
			  }
		  }
		}
		var action = 'ViewAllProjects.action?approvePr='+proIds+'&blocked=blocked&pageType='+pageType;
		window.location = action;
		//alert("txt ===>> "+ txt);
	}
	
	
	function viewWorkAllocation(emp_id,proStartDate,proEndDate) {

		removeLoadingDiv("the_div");
		var dialogEdit = '#viewworkallocation'; 
		dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
		$(dialogEdit).dialog({
			autoOpen : false,
			bgiframe : true,
			resizable : false,
			height : 450,
			width : 800,   
			modal : true,
			title : 'Task Summary',
			open : function() {
				var xhr = $.ajax({
					url : "ProjectWorkAllocation.action?emp_id="+emp_id+"&proStartDate="+proStartDate+"&proEndDate="+proEndDate,
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
	
	
	
	function executeActions(val, proIds, pageType) {
		if(val == '1') {
			var action = 'PreAddNewProject1.action?operation=E&pro_id='+proIds+'&step=0&pageType='+pageType;
			window.location = action;
		} else if(val == '2') {
			if(confirm('Are you sure, you wish to delete this project?')) {
				var action = 'ViewAllProjects.action?operation=D&ID='+proIds+'&pageType='+pageType;
				window.location = action;
			}
		} else if(val == '3') {
			if(confirm('Are you sure, you wish to complete this project?')) {
				var action = 'ViewAllProjects.action?approvePr='+proIds+'&approve=approve&pageType='+pageType;
				window.location = action;
			}
		} else if(val == '4') {	
			if(confirm('Are you sure, you wish to block this project?')) {
				var action = 'ViewAllProjects.action?approvePr='+proIds+'&blocked=blocked&pageType='+pageType;
				window.location = action;
			}
		}
	}

	
	function setDependencyPeriod(val, strProId, cnt, type) {
		if(type == 'Task') {
			var taskId = document.getElementById("dependency"+strProId+"_"+cnt).value;
			if(parseFloat(taskId) > 0) {
				var taskCnt = document.getElementById(strProId+"_"+taskId).value;
				var taskStDt = document.getElementById("startDate"+strProId+"_"+taskCnt).value;
				var taskEndDt = document.getElementById("deadline1"+strProId+"_"+taskCnt).value;
				//alert("taskCnt ===>> " +taskCnt + " taskStDt ===>> " + taskStDt + "  taskEndDt ===>> " +taskEndDt);
				if(val == '0') {
					document.getElementById("startDate"+strProId+"_"+cnt).value = taskStDt;
					document.getElementById("deadline1"+strProId+"_"+cnt).value = "";
				} else if(val == '1') {
					document.getElementById("startDate"+strProId+"_"+cnt).value = taskEndDt;
					document.getElementById("deadline1"+strProId+"_"+cnt).value = "";
				}
				//setDependencyDate(val, "startDate"+strProId+"_"+cnt, "deadline1"+strProId+"_"+cnt, taskStDt, taskEndDt, strProId);
			}
		} else {
			var subtaskId = document.getElementById("subDependency"+strProId+"_"+cnt).value;
			if(parseFloat(subtaskId) > 0) {
				var subtaskCnt = document.getElementById(strProId+"_"+subtaskId).value;
				var subtaskStDt = document.getElementById("substartDate"+strProId+"_"+subtaskCnt).value;
				var subtaskEndDt = document.getElementById("subdeadline1"+strProId+"_"+subtaskCnt).value;
				//alert("subtaskCnt ===>> " +subtaskCnt + " subtaskStDt ===>> " + subtaskStDt + "  subtaskEndDt ===>> " +subtaskEndDt);
				if(val == '0') {
					document.getElementById("substartDate"+strProId+"_"+cnt).value = subtaskStDt;
					document.getElementById("subdeadline1"+strProId+"_"+cnt).value = "";
				} else if(val == '1') {
					document.getElementById("substartDate"+strProId+"_"+cnt).value = subtaskEndDt;
					document.getElementById("subdeadline1"+strProId+"_"+cnt).value = "";
				}
				//setDependencyDate(val, "substartDate"+strProId+"_"+cnt, "subdeadline1"+strProId+"_"+cnt, subtaskStDt, subtaskEndDt, strProId);
			}
		}
	}
	
	function setDependencyDate(val, stDate, endDate, minDate, maxDate, strProId) {
		
		var proStDt = document.getElementById("proStartDate_"+strProId).value;
		var proEndDt = document.getElementById("proEndDate_"+strProId).value;
		
		if(val == '0') {
			proStDt = minDate;
		} else if(val == '1') {
			proStDt = maxDate;
		}
		//alert("val ===>> " +val+ "proStDt ===>> " + proStDt+"  " +stDate + " " + endDate);
		
		//jQuery("#frm_ViewAllProjects_"+strProId).validationEngine();
		$("#"+endDate).datepicker({
			dateFormat : 'dd/mm/yy', minDate: proStDt, maxDate: proEndDt, 
			onClose: function(selectedDate){
				$("#"+stDate).datepicker("option", "maxDate", selectedDate);
			}
		});
		//alert("endDate ===>> " + endDate);
		
		$("#"+stDate).datepicker({
			dateFormat : 'dd/mm/yy', minDate: proStDt, maxDate: proEndDt, 
			onClose: function(selectedDate){
				$("#"+endDate).datepicker("option", "minDate", selectedDate);
			}
		});
		//alert("stDate ===>> " + stDate);
	}
	
	
	function setValue(strName) {
		var val = document.getElementById(strName).value;
		if(val == '0') {
			document.getElementById(strName).value = '1';
		} else {
			document.getElementById(strName).value = '0';
		}
	}

	
	
	function executeTaskActions(val, parentVal, cnt, proId, fillData, taskId, isRecurr, isCustAdd, pageType) {
		if(val == '1') {
			if(parseFloat(fillData) > 0) {
				alert('You can not delete this task as user has already booked the time against this task.');
			} else {
				//if(confirm('Are you sure, you wish to delete this task?')) {
					deleteTask(cnt, proId);
				// } 
			}
		} else if(val == '2') {
			if(confirm('Are you sure, you wish to complete this task?')) {
				var action = 'ViewAllProjects.action?cb='+taskId+'&approve=approve&pageType='+pageType;
				window.location = action;
			}
		} else if(val == '3') {
			if(confirm('Are you sure, you wish to repeat this task?')) {
				repeatTask(cnt, proId, isRecurr, isCustAdd, pageType);
			}
		} else if(val == '4') {
			if(parseFloat(fillData) > 0) {
				alert('You can not add sub task as user has already booked the time against this task.');
			} else {
				if(confirm('Are you sure, you wish to add new sub-task?')) {
					addNewSubTask(cnt, parentVal, proId, isRecurr, isCustAdd, pageType);
				}
			}
		} else if(val == '5') {
			if(confirm('Are you sure, you wish to reassign this task?')) {
				var action = 'ViewAllProjects.action?taskId='+taskId+'&reassign=reassign&pageType='+pageType;
				window.location = action;
			}
		}
		document.getElementById("taskActions"+proId+"_"+cnt).selectedIndex = '0';
	}
	
	
	
	function executeSubTaskActions(val, parentVal, cnt, proId, fillData, taskTRId, subtaskId, isRecurr, isCustAdd, pageType) {
		if(val == '1') {
			if(parseFloat(fillData) > 0) {
				alert('You can not delete this sub-task as user has already booked the time against this sub-task.');
			} else {
				deleteSubTask(cnt, proId);
			}
		} else if(val == '2') {
			if(confirm('Are you sure, you wish to complete this sub-task?')) {
				var action = 'ViewAllProjects.action?cb='+subtaskId+'&approve=approve&pageType='+pageType;
				window.location = action;
			}
		} else if(val == '3') {
			if(confirm('Are you sure, you wish to repeat this sub-task?')) {
				repeatSubTask(cnt, taskTRId, parentVal, proId, isRecurr, isCustAdd, pageType);
			}
		} else if(val == '4') {
			if(confirm('Are you sure, you wish to add new sub-task?')) {
				addNewSubTask(taskTRId, parentVal, proId, isRecurr, isCustAdd, pageType);
			}
		} else if(val == '5') {
			if(confirm('Are you sure, you wish to reassign this sub-task?')) {
				var action = 'ViewAllProjects.action?taskId='+subtaskId+'&reassign=reassign&pageType='+pageType;
				window.location = action;
			}
		}
		document.getElementById("subtaskActions"+proId+"_"+cnt).selectedIndex = '0';
	}
	
	
	function executeRescheduleTask(val, proId, taskId, tstType, pageType) {
		var tstName = 'task';
		if(tstType == 'ST') {
			tstName = 'sub-task';
		}
		var msg = 'Are you sure, you wish to deny reschedule this '+tstName+'?';
		if(val == '1') {
			msg = 'Are you sure, you wish to allow reschedule this '+tstName+'?';
		}
		if(confirm(msg)) {
			var action = 'ViewAllProjects.action?taskId='+taskId+'&allowDeny='+val+'&reschedule=reschedule&pageType='+pageType;
			window.location = action;
		}
	}
	
	
	function executeNewTaskAlign(val, proId, taskId, tstType, pageType) {
		var tstName = 'task';
		if(tstType == 'ST') {
			tstName = 'sub-task';
		}
		var msg = 'Are you sure, you wish to deny align this '+tstName+'?';
		if(val == '1') {
			msg = 'Are you sure, you wish to allow align this '+tstName+'?';
		}
		if(confirm(msg)) {
			var action = 'ViewAllProjects.action?taskId='+taskId+'&allowDeny='+val+'&align=align&pageType='+pageType;
			window.location = action;
		}
	}
	
	
	function openFeedsForm(taskId, proId, proType) {
		removeLoadingDiv('the_div');
		var dialogEdit = '#openFeedDiv'; 
		dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
		$(dialogEdit).dialog({
			autoOpen : false,
			bgiframe : true,
			resizable : false, 
			height : 550,
			width : '98%',    
			modal : true,
			title : 'Task Feeds',
			open : function() {
				var xhr = $.ajax({
					url : 'FeedsPopup.action?pageFrom=VAPTask&taskId='+taskId+'&proId='+proId+'&pageType='+proType,
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
	
	
	function openProjectFeedsForm(proId, proType) {
		removeLoadingDiv('the_div');
		var dialogEdit = '#openProjectFeedDiv'; 
		dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
		$(dialogEdit).dialog({
			autoOpen : false,
			bgiframe : true,
			resizable : false, 
			height : 550,
			width : '98%',    
			modal : true,
			title : 'Project Feeds',
			open : function() {
				var xhr = $.ajax({
					url : 'FeedsPopup.action?pageFrom=VAPProject&proId='+proId+'&pageType='+proType,
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


    function showLoading() {
        var div = document.createElement('div');
        var img = document.createElement('img');
        /* img.src = 'loading_bar.GIF'; */
        div.innerHTML = "Uploading...<br />";
        div.style.cssText = 'position: fixed; top: 50%; left: 40%; z-index: 5000; width: 222px; text-align: center; background: #EFEFEF; border: 1px solid #000';
        /* div.appendChild(img); */
        $(div).appendTo('body');
       // document.body.appendChild(div);
        return true;
    }
    
    
    function submitForm(type) {
    	var org = document.getElementById("f_org").value;
    	var location = getSelectedValue("f_strWLocation");
    	var strSkill = getSelectedValue("strSkills");
    	var strEdu = getSelectedValue("strEducation");
    	var strExp = getSelectedValue("strExperience");
    	var strResType = getSelectedValue("resourceType");

    	var paramValues = "";
    	if(type == '2') {
    		paramValues = '&strLocation='+location+'&strSkill='+strSkill+'&strEdu='+strEdu+'&strExp='+strExp+'&strResType='+strResType;
    	}
    	//alert("paramValues ===>> " + paramValues);
    	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$.ajax({
    		type : 'POST',
    		url: 'ViewAllProjects.action?f_org='+org+paramValues,
    		data: $("#"+this.id).serialize(),
    		success: function(result){
            	$("#divResult").html(result);
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
    
    
</script>

</g:compress>

		
<%-- <link rel="stylesheet" type="text/css" href="css/select/jquery.multiselect.css" />
<script type="text/javascript" src="scripts/select/jquery.multiselect.js"></script> --%>
<script type="text/javascript">
$(function() {
	$("#location").multiselect();
	$("#f_sbu").multiselect();
	$("#f_service").multiselect();
	$("#pro_id").multiselect();
	$("#managerId").multiselect();
	$("#client").multiselect();
	$("#skill").multiselect();
});
</script>     


<style>
.tb_style {
	border-collapse: collapse;
}

.tb_style tr td {
	padding: 5px;
	border: solid 1px #c5c5c5;
	width: 100%
}

.tb_style tr th {
	padding: 5px;
	border: solid 1px #c5c5c5;
	width: 100%;
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
		String pageType = (String)request.getAttribute("pageType");
		
		String proCount = (String)request.getAttribute("proCount");
		
		String proPage = (String)request.getAttribute("proPage");
		String strMinLimit1 = (String)request.getAttribute("minLimit");
		
		
		String strTitle = (String)request.getAttribute(IConstants.TITLE); 
		String strUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
		String strEmpId = (String)session.getAttribute(IConstants.EMPID);
	%>
	
<%-- <% if(proType == null || proType.equals("") || proType.equals("null") || proType.equals("L")) { %>
	<jsp:include page="../common/SubHeader.jsp">
		<jsp:param value="Working Projects" name="title" />
	</jsp:include>
<% } else if(proType != null && proType.equals("C")) { %>
	<jsp:include page="../common/SubHeader.jsp">
		<jsp:param value="<%=strTitle %>" name="title" />
	</jsp:include>
<% } else if(proType != null && proType.equals("B")) { %>
	<jsp:include page="../common/SubHeader.jsp">
		<jsp:param value="<%=strTitle %>" name="title" />
	</jsp:include>
<% } %> --%>

	<%
		String strAction = "ViewAllProjects.action";
		if(pageType != null && pageType.equals("MP")) {
			strAction = "ViewMyProjects.action";
		}
	%>
	
	
	<section class="content">
          <!-- title row -->
          
          <div class="row">
            <div class="col-md-12">
            	<s:form name="frm_adminproject_view" action="ViewAllProjects" theme="simple">
            		<s:hidden name="proType" id="proType" />
			    	<s:hidden name="pageType" id="pageType" />
			    	<s:hidden name="proPage" id="proPage" />
			    	<s:hidden name="minLimit" id="minLimit" />
			    	<s:hidden name="sortBy" id="sortBy" />
			    	<input type="hidden" name="usrType" id="usrType" value="<%=strUserType %>" />
		            <div class="box box-default collapsed-box">
		               <div class="box-header with-border">
		                   <h3 class="box-title" style="font-size: 14px;"><%=(String)request.getAttribute("selectedFilter") %></h3>
		                   <div class="box-tools pull-right">
		                       <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
		                       <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
		                   </div>
		               </div>
		               <!-- /.box-header -->
		               <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
							<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
								<i class="fa fa-filter"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">SBU</p>
									<s:select name="f_sbu" id="f_sbu" list="sbuList" listKey="serviceId" listValue="serviceName" multiple="true" />
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Service</p>
									<s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId" listValue="serviceName" multiple="true" />
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Project</p>
									<s:select name="pro_id" id="pro_id1" listKey="projectID" listValue="projectName" list="projectdetailslist" key="" multiple="true" />
								</div>
		
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Project Owner</p>
									<s:select theme="simple" name="managerId" id="managerId" listKey="proOwnerId" listValue="proOwnerName" list="proOwnerList" key="" multiple="true" />
								</div>
								<% if(strUserType != null && !strUserType.equals(IConstants.CUSTOMER)) { %>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Client</p>
										<s:select label="Select Client" name="client" id="client" listKey="clientId" listValue="clientName" list="clientList" key="" multiple="true" />
									</div>
								<% } %>
								<% if(proType ==null || proType.equals("null") || proType.equals("") || proType.equals("L")) { %>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Status</p>
										<s:select theme="simple" name="proStatus" id="proStatus" headerKey="" headerValue="All Projects" list="#{'1':'On-Track', '2':'Not Started', '3':'Pending'}"/>
									</div>
								<% } %>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Assigned By</p>
									<select name="assignedBy" id="assignedBy" >
										<option value="">All Assigner</option>
										<%=(String)request.getAttribute("sbAddedbyOption") %>
									</select>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Project Type</p>
									<s:select theme="simple" name="recurrOrMiles" id="recurrOrMiles" headerKey="" headerValue="All Project Type" list="#{'1':'Recurring', '2':'Milestone'}" />
								</div>
								
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">&nbsp;</p>
									<input type="button" name="Submit" value="Submit" class="btn btn-primary" onclick="submitForm('2');"/>
								</div>
							</div>
		               </div>
		           </div>
		           
		           <div class="box" style="border: 0px none; margin-top: 1px; padding: 2px 5px; margin-bottom: 7px;">
					Sort By: 
					<s:select theme="simple" name="sortBy1" id="sortBy1" cssStyle="width: 120px;" 
						list="#{'1':'Latest on Top', '2':'Oldest on Top', '3':'A-Z', '4':'Z-A'}" onchange="loadMoreProjects('0', '0', '<%=proType %>');"/>
                  </div>
                  
	           </s:form>
                  
			</div>
		</div>
	
		<div class="row">
            <div class="col-md-12" style="margin-bottom: 7px;">
			<% if(strUserType != null && (strUserType.equals(IConstants.MANAGER) || strUserType.equals(IConstants.HRMANAGER) || strUserType.equals(IConstants.ADMIN) || strUserType.equals(IConstants.CEO))) { %>
				<% if((proType == null || proType.equals("") || proType.equals("null") || proType.equals("L")) && strUserType!=null && !strUserType.equals(IConstants.CUSTOMER)) { %>
					<div style="float:right; margin-right:2%;">
						<input type="button" name="approve" class="btn btn-primary" style="padding: 2px 5px;" value="Mark as Completed" onclick="approveProjectsAndTasks('<%=pageType %>');"/>
					</div>
					
					<div style="float:right; margin-right: 1%;">
						<span id="unblockedSpan">
						<input type="button" name="blocked" class="btn btn btn-primary disabled" style="padding: 2px 5px;" value="Mark as Blocked" />
						</span>
						<span id="blockedSpan" style="display: none;">
						<input type="button" name="blocked" class="btn btn-primary" style="padding: 2px 5px;" value="Mark as Blocked" onclick="blockedProjects('<%=pageType %>')"/>
						</span>
					</div>
				<% } %>
			 
				<div style="float:right; margin-right:1%;">
					<input type="button" name="newProject" class="btn btn-primary" style="padding: 2px 5px;" value="Add New Project" onclick="window.location.href='ViewAllProjects.action?singleProData=YES&proType=<%=proType %>&pageType=<%=pageType %>&step=0&proPage=<%=proPage %>&minLimit=<%=strMinLimit1 %>'"/>
				</div>
				<div class="clr"></div>
			<% } %>
			</div>
		</div>	
	
	
	<form name="frm_adminproject_view" action="<%=strAction %>">
		
		<%
			String sbData = (String) request.getAttribute("sbData");
			String strSearchJob = (String) request.getAttribute("strSearchJob");
		%>
		<div style="float:left; font-size:12px; line-height:22px; width:514px; margin-left: 224px;">
	           <span style="float:left; margin-right:7px;">Search:</span>
	           <div style="border:solid 1px #68AC3B;float:left; -moz-border-radius: 3px;	-webkit-border-radius: 3px;	border-radius: 3px;">
		            <div style="float:left">
		            	<input type="text" id="strSearchJob" name="strSearchJob" style="margin-left: 0px; border:0px solid #ccc; width:282px; box-shadow:0px 0px 0px #ccc" value="<%=uF.showData(strSearchJob,"") %>"/> 
		          	</div>
		         	 <div style="float:right">
		            	<input type="submit" value="Search"  class="input_search" >
		            </div>
	       		</div>
	       </div>
	       
	       <script>
			$( "#strSearchJob" ).autocomplete({
				source: [ <%=uF.showData(sbData,"") %> ]
			});
		</script>
		
	</form>


	<div class="clr"></div>
		
		<% if(strUserType != null && (strUserType.equals(IConstants.MANAGER) || strUserType.equals(IConstants.HRMANAGER) || strUserType.equals(IConstants.ADMIN) || strUserType.equals(IConstants.CEO))) { %>
			<div style="float: right; margin-bottom: 10px; width: 46%; margin-top: -36px;">
				<% if((proType == null || proType.equals("") || proType.equals("null") || proType.equals("L")) && strUserType!=null && !strUserType.equals(IConstants.CUSTOMER)) { %>
					<div style="float:right; margin-right:2%;">
						<!-- <input type="submit" name="approve" class="input_button" value="Mark as Completed" /> -->
						<input type="button" name="approve" class="input_button" value="Mark as Completed" onclick="approveProjectsAndTasks('<%=pageType %>');"/>
					</div>
					
					<div style="float:right; margin-right: 1%;">
						<span id="unblockedSpan">
						<input type="button" name="blocked" class="input_reset" value="Mark as Blocked" />
						</span>
						<span id="blockedSpan" style="display: none;">
						<input type="button" name="blocked" class="input_button" value="Mark as Blocked" onclick="blockedProjects('<%=pageType %>')"/>
						</span>
					</div>
				<% } %>
			
			
			<div style="float:right; margin-right:1%;">
				<input type="button" name="importProject" class="input_button" value="Import Project" onclick="window.location.href='ImportProjects.action?pageType=<%=pageType %>'"/>
			</div>
			
			<div style="float:right; margin-right:1%;">
				<input type="button" name="newProject" class="input_button" value="Add New Project" onclick="window.location.href='PreAddNewProject1.action?pageType=<%=pageType %>'"/>
			</div>
			
			
		</div>
		<div class="clr"></div>
	<% } %>
	
<script type="text/javascript">
	hs.graphicsDir = '<%=request.getContextPath()%>/images1/highslide/graphics/';
	hs.outlineType = 'rounded-white';
	hs.wrapperClassName = 'draggable-header';
</script>

<%

	//Map<String, List<List<String>>> hmTasks = (Map<String, List<List<String>>>)request.getAttribute("hmTasks");
	Map<String, List<List<String>>> hmSubTasks = (Map<String, List<List<String>>>) request.getAttribute("hmSubTasks");
	//if(hmTasks == null) hmTasks = new HashMap<String, List<List<String>>>();
	Map<String, String> hmProTaskCount = (Map<String, String>)request.getAttribute("hmProTaskCount");
	//Map<String, String> hmProTaskCompletePercent = (Map<String, String>)request.getAttribute("hmProTaskCompletePercent");
	Map<String, String> hmProCompPercent = (Map<String, String>)request.getAttribute("hmProCompPercent");
	
	Map<String, Map<String, String>> hmProMileAndCMileCnt = (Map<String, Map<String, String>>) request.getAttribute("hmProMileAndCMileCnt");
	
	Map<String, String> hmProTakenTime = (Map<String, String>) request.getAttribute("hmProTakenTime");
	
	Map hmProject = (java.util.Map)request.getAttribute("hmProject");
	if(hmProject == null) hmProject = new HashMap();
	//Map<String, String> hmPMilestoneSize = (Map<String, String>)request.getAttribute("hmPMilestoneSize");
	Map<String, String> hmPDocumentCounter = (Map<String, String>)request.getAttribute("hmPDocumentCounter");
	
	Map<String, List<List<String>>> hmProSubTasks = (Map<String, List<List<String>>>) request.getAttribute("hmProSubTasks");
 	Map<String, Map<String, List<String>>> hmProWiseTasks = (Map<String, Map<String, List<String>>>) request.getAttribute("hmProWiseTasks");
 	
 	List<GetPriorityList> priorityList = (List<GetPriorityList>)request.getAttribute("priorityList");
	List<FillTaskEmpList> TaskEmpNamesList = (List<FillTaskEmpList>)request.getAttribute("TaskEmpNamesList");
	List<FillSkills> empSkillList = (List<FillSkills>)request.getAttribute("empSkillList");
	
	Map<String, String> hmProTaskDependency = (Map<String, String>)request.getAttribute("hmProTaskDependency");
	Map<String, String> hmProEmpSkills = (Map<String, String>)request.getAttribute("hmProEmpSkills");
	Map<String, String> hmProTeamEmp = (Map<String, String>)request.getAttribute("hmProTeamEmp");
	
	Map<String, String> hmProSubTaskDependency = (Map<String, String>)request.getAttribute("hmProSubTaskDependency");
%>

<div class="clr"></div>

<%=uF.showData((String)session.getAttribute(IConstants.MESSAGE),"") %>

<div style="margin:10px 0px 0px 0px ;float:left; width:100%">
       <% if((proType == null || proType.equals("") || proType.equals("null") || proType.equals("L")) && strUserType!=null && !strUserType.equals(IConstants.CUSTOMER)) { %>
	       <%if(hmProject != null && hmProject.size()>0) { %>
	       		<div style="width: 100%; float: left; margin-left: 20px; margin-bottom: 5px;"> <input type="checkbox" name="allLivePr" id="allLivePr" onclick="checkUncheckAllProject(this.value);" /> Select All </div>
	       <% } %>
       <% } %>
         <ul class="level_list">
		<div id="moreProject_0">
		<%
			Set setProjectMap = hmProject.keySet();
			Iterator it = setProjectMap.iterator();
			
			while(it.hasNext()) {
				String strProjectId = (String)it.next();
				List alProjects = (List)hmProject.get(strProjectId);
				if(alProjects == null) alProjects = new ArrayList();
				double proCompletePecent = 0;
				//if(uF.parseToDouble(hmProTaskCount.get(strProjectId)) > 0) {
					proCompletePecent = uF.parseToDouble(hmProCompPercent.get(strProjectId));
				//}
				if(proCompletePecent < 0) {
					proCompletePecent = 0;
				}
				
				Map<String, String> hmMilestoneAndCompletedMilestone = hmProMileAndCMileCnt.get(strProjectId);
				if(hmMilestoneAndCompletedMilestone == null) hmMilestoneAndCompletedMilestone = new HashMap<String, String>();
				
				%>
				
				<% String isRecurr = "N";
				if(alProjects.get(19) != null && !alProjects.get(19).equals("O")) {
						isRecurr = "Y";
					}
				%>
				<% 
					String[] strTakenTime1 = alProjects.get(7).toString().split("::::") ;
                %>
					<input type="hidden" name="proStartDate_<%=strProjectId %>" id="proStartDate_<%=strProjectId %>" value="<%=alProjects.get(13) %>"/>
					<input type="hidden" name="proEndDate_<%=strProjectId %>" id="proEndDate_<%=strProjectId %>" value="<%=alProjects.get(14) %>"/>
					
					<li class="post" style="float: left;">
					<div style="float: left; font-size: 11px;">
					<!-- <p> -->
					<div style="float: left; margin-right: 5px;">	
						<% if((proType == null || proType.equals("") || proType.equals("null") || proType.equals("L")) && strUserType!=null && !strUserType.equals(IConstants.CUSTOMER)) { %>
							<span id="proCheckboxDiv" style="float: left; margin-right: 5px;">
								<input type="checkbox" value="<%=strProjectId%>" name="approvePr" id="approvePr" onclick="checkAllProjectCheckedUnchecked();"/>
							</span>
						<% } %>
	                    <span style="float: left; width: 20px; height: 20px; font-weight: bold; background-color: lightpink; text-align: center; font-size: 16px;" id="proLogoDiv">
	                    <% 
	                    	String proLogo = "";
	                    	if(alProjects.get(1) != null && alProjects.get(1).toString().length()>0) {
								proLogo = alProjects.get(1).toString().substring(0, 1);
		                    }
	                   	%> 
	                   	<span><%=proLogo %></span>
	                    </span>
	                    </div>
	                
	                    <div style="float: left; margin-right: 10px; padding-left:5px; width: 305px; border-left: 3px solid <%=(String)alProjects.get(20) %>;">
		                    <div style="float: left; width: 100%;">
		                    	<span style="float: left; width: 100%; <%=(proCompletePecent == 0 && uF.parseToDouble(strTakenTime1[0]) == 0) ? "background-image: url(&quot;images1/icons/new2.gif&quot;);":""%> background-repeat: no-repeat; background-position: right top;">
		                    		<strong><%=alProjects.get(1) %></strong>
		                    	</span>
								<span style="float: left; width: 100%; margin-top: -5px;"><%=alProjects.get(10) %> </span></div>
							<div style="float: left; width: 100%; margin-top: -5px;">
								<span style="float: left;"> <%=alProjects.get(17) %> </span>
								<span class="anaAttrib1" style="float: left; margin-left: 3px; font-size: 24px"><%=alProjects.get(15) %></span> <span style="float: left; margin-left: 3px;">resources</span>
								<input type="hidden" name="hideResourceSpanStatus" id="hideResourceSpanStatus<%=strProjectId %>" value = "0"/>
								<a href="javascript:void(0)" onclick="getProjectResources('<%=strProjectId %>')">
									<span id="resourceDownarrowSpan<%=strProjectId %>" style="float: left; margin-left: 2px; margin-top: 5px;"> 
										<i class="fa fa-angle-down" aria-hidden="true" style="width: 14px;"></i>
									</span>
									<span id="resourceUparrowSpan<%=strProjectId %>" style="float: left; margin-left: 2px; margin-top: 5px; display: none;">
										<i class="fa fa-angle-up" aria-hidden="true" style="width: 14px;"></i>
									</span>
								</a>
							</div>
							<div style="float: left; width: 100%; margin-top: -5px;">
								<span style="float: left;">Project Summary</span>
								<input type="hidden" name="proSummarySpanStatus" id="proSummarySpanStatus<%=strProjectId %>" value = "0"/>
								<a href="javascript:void(0)" onclick="viewSummary('<%=strProjectId %>', '<%=proType %>', '<%=pageType %>')" title="Project Summary">
									<span id="proSummaryDownarrowSpan<%=strProjectId %>" style="float: left; margin-left: 2px; margin-top: 5px;"> 
										<i class="fa fa-angle-down" aria-hidden="true" style="width: 14px;"></i> 
									</span>
									<span id="proSummaryUparrowSpan<%=strProjectId %>" style="float: left; margin-left: 2px; margin-top: 5px; display: none;">
										<i class="fa fa-angle-up" aria-hidden="true" style="width: 14px;"></i> 
									</span>
								</a>
							</div>
	                    </div>
	                    
	                    <div style="float: left; margin-right: 10px; width: 140px;">
		                    <div style="float: left; width: 100%;"><label title="<%=alProjects.get(4) %> to <%=alProjects.get(5) %>">Deadline<strong> <%=alProjects.get(5) %></strong> <%=alProjects.get(19) != null && !alProjects.get(19).equals("O") ? "("+alProjects.get(19)+")" : "" %></label></div>
		                    <div style="float: left; width: 50%; margin-top: -7px; margin-left: 10px;">
		                    	<div class="anaAttrib1"><%=uF.formatIntoOneDecimalWithOutComma(proCompletePecent) %>%</div>
								<div id="Pcomplete_<%=strProjectId %>" class="outbox">
									<div class="greenbox" style="width: <%=uF.formatIntoOneDecimalWithOutComma(proCompletePecent) %>%;"></div>
								</div>
							</div>
							<div style="float: left; width: 100%;">
	                    		<span style="float:left; width: 100%;">
	                    			Gantt Chart <a href="javascript:void(0)" onclick="viewGanttChart(<%=strProjectId %>)"><img style="width: 9px; margin-left: 3px;" src="images1/icons/popup_arrow.gif"></a>
	                    		</span>
	                    	</div>
	                    </div>
	                    
	                    <div style="float: left; margin-right: 10px; width: 150px;">
	                    	<div style="float: left; width: 100%;">Ideal Time:<strong> <%=alProjects.get(6) %></strong></div>
	                    	<div style="float: left; width: 100%;"><span class="anaAttrib1" style="font-size: 24px; color: <%=(String)alProjects.get(9) %>">
	                    	
	                    	<%=strTakenTime1[0] %></span> <%=strTakenTime1[1] %> taken</div>
	                    </div>
	                    <div style="float: left; margin-right: 10px; width: 165px;">
	                    	<div style="float: left; width: 100%;"><strong> <%=uF.formatIntoComma(uF.parseToInt(hmProTaskCount.get(strProjectId))) %></strong> Tasks & <strong><%=uF.showData(hmMilestoneAndCompletedMilestone.get("MILESTONE_COUNT"), "0") %></strong> Milestones</div>
	                    	<div style="float: left; width: 100%;">
	                    		<span style="float:left; width: 100%; margin-top: -7px;"><span class="anaAttrib1" style="font-size: 24px"><%=alProjects.get(16) %></span> tasks completed</span>
	                    		<span  style="float:left; width: 100%; margin-top: -7px;"><span class="anaAttrib1" style="font-size: 24px"><%=uF.showData(hmMilestoneAndCompletedMilestone.get("COMPLETED_MILESTONE_COUNT"), "0") %></span> milestone completed</span>
	                    	</div>
	                    	<div style="float: left; width: 100%;">
	                    		<span style="float:left; width: 100%; margin-top: -7px;">
	                    		<span style="float: left;">all tasks</span>
								<input type="hidden" name="proTaskSpanStatus" id="proTaskSpanStatus<%=strProjectId %>" value = "0"/>
								<a href="javascript:void(0)" onclick="viewProTasks(<%=strProjectId %>)">
									<span id="proTaskDownarrowSpan<%=strProjectId %>" style="float: left; margin-left: 2px; margin-top: 5px;"> 
										<i class="fa fa-angle-down" aria-hidden="true" style="width: 14px;"></i> 
									</span>
									<span id="proTaskUparrowSpan<%=strProjectId %>" style="float: left; margin-left: 2px; margin-top: 5px; display: none;">
										<i class="fa fa-angle-up" aria-hidden="true" style="width: 14px;"></i> 
									</span>
								</a>
								<% if(uF.parseToInt(hmMilestoneAndCompletedMilestone.get("MILESTONE_COUNT")) > 0) { %>
									<span style="float: left; margin-left: 3px;">all miles</span>
									<input type="hidden" name="proMilesSpanStatus" id="proMilesSpanStatus<%=strProjectId %>" value = "0"/>
									<a href="javascript:void(0)" onclick="viewMilestones('<%=strProjectId %>', '<%=pageType %>')">
										<span id="proMilesDownarrowSpan<%=strProjectId %>" style="float: left; margin-left: 2px; margin-top: 5px;"> 
											<i class="fa fa-angle-down" aria-hidden="true" style="width: 14px;"></i> 
										</span>
										<span id="proMilesUparrowSpan<%=strProjectId %>" style="float: left; margin-left: 2px; margin-top: 5px; display: none;">
											<i class="fa fa-angle-up" aria-hidden="true" style="width: 14px;"></i>
										</span>
									</a>
								<% } %>
	                    		</span>
	                    	</div>
	                    </div>
	                    <div style="float: left; margin-right: 10px; width: 85px;">
	                    	<div style="float: left; width: 100%;">Docs</div>
	                    	<div style="float: left; width: 100%;"><span class="anaAttrib1" style="font-size: 24px"><%=uF.parseToInt(hmPDocumentCounter.get(strProjectId)) > 0 ? hmPDocumentCounter.get(strProjectId) : "0"%></span> docs</div>
	                    	<div style="float: left; width: 100%;">
	                    		<span style="float:left; width: 100%; margin-top: -7px;">
	                    		<span style="float: left;">all docs</span>
								<input type="hidden" name="proDocsSpanStatus" id="proDocsSpanStatus<%=strProjectId %>" value = "0"/>
								<a href="javascript:void(0)" onclick="viewDocuments('<%=strProjectId %>', '<%=proType %>', '<%=pageType %>')">
									<span id="proDocsDownarrowSpan<%=strProjectId %>" style="float: left; margin-left: 2px; margin-top: 5px;"> 
										<i class="fa fa-angle-down" aria-hidden="true" style="width: 14px;"></i>
									</span>
									<span id="proDocsUparrowSpan<%=strProjectId %>" style="float: left; margin-left: 2px; margin-top: 5px; display: none;">
										<i class="fa fa-angle-up" aria-hidden="true" style="width: 14px;"></i>
									</span>
								</a>
	                    		</span>
	                    	</div>
	                    </div>
	                    
	                    <% if(strUserType!=null && !strUserType.equals(IConstants.CUSTOMER)) { %>
		                    <% if(pageType == null || !pageType.equals("MP") || (pageType.equals("MP") && uF.parseToInt((String)alProjects.get(21)) == uF.parseToInt(strEmpId))) { %>
			                    <div style="float: left; width: 100px;">
			                    	<div style="float: left; width: 100%;">$ Profitability</div>
			                    	<% 
			                    	String strProfitColor = "red";
			                    	//System.out.println("profit ===>> "+ uF.parseToDouble((String)alProjects.get(20)) +" -- " + (String)alProjects.get(20));
			                    	if(uF.parseToDouble((String)alProjects.get(18)) > 0) { 
			                    		strProfitColor = "green";
			                    	} else if(uF.parseToDouble((String)alProjects.get(18)) == 0) {
			                    		strProfitColor = "yellow";
			                    	}
			                    	%>
			                    	
			                    	<div style="float: left; width: 100%;"><span class="anaAttrib1" style="font-size: 24px; color: <%=strProfitColor %>"><%=alProjects.get(18) %>%</span></div>
			                    	<div style="float: left; width: 100%;">
			                    		<span style="float:left; width: 100%; margin-top: -7px;">
			                    		<span style="float: left;">business case</span>
										<input type="hidden" name="proProfitSpanStatus" id="proProfitSpanStatus<%=strProjectId %>" value = "0"/>
										<a href="javascript:void(0)" onclick="viewCostSummary('<%=strProjectId %>', '<%=pageType %>')">
											<span id="proProfitDownarrowSpan<%=strProjectId %>" style="float: left; margin-left: 2px; margin-top: 5px;"> 
												<i class="fa fa-angle-down" aria-hidden="true" style="width: 14px;"></i> 
											</span>
											<span id="proProfitUparrowSpan<%=strProjectId %>" style="float: left; margin-left: 2px; margin-top: 5px; display: none;">
												<i class="fa fa-angle-up" aria-hidden="true" style="width: 14px;"></i> 
											</span>
										</a>
			                    		</span>
			                    	</div>
			                    </div>
		                    <% } %>
	                    <% } %>
  					<!-- </p> -->
  
	  					<div style="float: left;">
	  						<div style="float: left;">
	  							<a href="javascript:void(0);" onclick="openProjectFeedsForm('<%=strProjectId %>', '<%=proType %>');" title="click here for feed">
									<img src="images1/icons/feed.png" height="16" width="16">
								</a>Feeds
							</div>
	  					</div>
  					
                    </div>
                    <div style="float:right;">
                    <% if(proType != null && (proType.equals("C") || proType.equals("B")) && strUserType!=null && !strUserType.equals(IConstants.CUSTOMER)) { %>
	                    <%if(strUserType!=null && (strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.MANAGER))) { %>
							<div id="unlock_div_<%=alProjects.get(0)%>"><a href="javascript:void(0)" onclick="((confirm('Are you sure, you want to unlock this project?'))?getContent('unlock_div_<%=alProjects.get(0)%>','UpdateStatus.action?pro_id=<%=alProjects.get(0)%>&status=n'):'')">Click to unlock</a></div>
						<% } %>
					<% } %>
                    <%-- <a class="viewdollaricon" href="javascript:void(0)" style="width: 22px;" onclick="viewCostSummary(<%=strProjectId %>)" title="View Cost Summary">View Cost Summary</a>
                    <a class="viewdocticon" href="javascript:void(0)" style="width: 22px;" onclick="viewDocuments(<%=strProjectId %>)" title="View Project Documents">View Project Documents</a> --%>
                    <%-- <a class="viewgantticon" href="javascript:void(0)" style="width: 22px;" onclick="viewSummary(<%=strProjectId %>)" title="View Summary">View Summary</a> --%>
                    <%-- <a href="javascript:void(0)" class="edit_lvl" onclick="editProject(<%=strProjectId%>)" title="Edit Project">Edit</a> --%>
                    <% if((proType == null || proType.equals("") || proType.equals("null") || proType.equals("L")) && strUserType!=null && !strUserType.equals(IConstants.CUSTOMER)) { %>
                    <select name="actions<%=strProjectId %>" id="actions<%=strProjectId %>" style="width: 100px;" onchange="executeActions(this.value, '<%=strProjectId %>', '<%=pageType %>');">
                    	<option value="">Actions</option>
                    	<% if(pageType == null || !pageType.equals("MP") || (pageType.equals("MP") && uF.parseToInt((String)alProjects.get(21)) == uF.parseToInt(strEmpId))) {  //  || strUserType.equalsIgnoreCase(IConstants.MANAGER) %>
	                    	<option value="1">Edit Project</option>
	                    	<%-- <% if(strUserType!=null && (strUserType.equalsIgnoreCase(IConstants.ADMIN)) || strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.CEO)) { %> --%>
	                    		<option value="2">Delete Project</option>
	                    	<%-- <% } %> --%>
                    	<% } %>
                    	<option value="3">Mark as Completed </option>
                    	<option value="4">Mark as Blocked </option>
                    </select>
	                    <%-- <a href="PreAddNewProject1.action?operation=E&pro_id=<%=strProjectId %>&step=0" class="edit_lvl" title="Edit Project">Edit</a>
	                   	<% if(strUserType!=null && (strUserType.equalsIgnoreCase(IConstants.ADMIN)) || strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.CEO)) { %>
	                   		<a href="ViewAllProjects.action?operation=D&ID=<%=strProjectId %>" class="del" title="Delete Project"  onclick="return confirm('Are you sure, you wish to delete this project?')"> - </a>
	                   	<% } %> --%>
                   <% } %>
                    
                    </div>
                    
                    <div class="clr"></div>
					<ul id="proUL_<%=strProjectId %>" style="float: left; width: 98%; display: none;">		
				<li class="desgn" style="float: left; width: 100%;"> 
					
				<!-- <p class="past heading_dash" style="text-align:left; padding-left:35px;">Task List (click to expand)</p> -->
				<div id="proResourcesDiv_<%=strProjectId %>" style="display: none;"></div>
				<div id="proMilestoneDiv_<%=strProjectId %>" style="display: none;"></div>
				<div id="proCostSummaryDiv_<%=strProjectId %>" style="display: none;"></div>
				<div id="proSummaryDiv_<%=strProjectId %>" style="display: none;"></div>
				<div id="proDocsDiv_<%=strProjectId %>" style="display: none;"></div>
				
				<div id="taskSubTaskDiv_<%=strProjectId %>" style="display: none;">
				<div style="margin: 5px;">
				<h3>Tasks</h3>
                 	<%
                 	String strProTaskDependency = hmProTaskDependency.get(strProjectId);
                 	String strProTeamEmp = hmProTeamEmp.get(strProjectId);
                 	String strProEmpSkills = hmProEmpSkills.get(strProjectId);
                 	
    				String estimateLbl = "h";
    				if(alProjects.get(12) != null && "D".equalsIgnoreCase((String)alProjects.get(12))) {
    					estimateLbl = "d";
    				} else if(alProjects.get(12) != null && "M".equalsIgnoreCase((String)alProjects.get(12))) {
    					estimateLbl = "m";
    				}
                 	%>
                
                <script type="text/javascript">
		          	jQuery(document).ready(function() {
						jQuery("#frm_ViewAllProjects_"+<%=strProjectId %>).validationEngine();
					});       	
          		</script>
          		
          		<form name="frm_ViewAllProjects_<%=strProjectId %>" id="frm_ViewAllProjects_<%=strProjectId %>" class="formcss" action="ViewAllProjects.action" method="post" enctype="multipart/form-data">
                 	<%-- <s:form action="ViewAllProjects" name="frm_ViewAllProjects_<%=strProjectId %>" id="frm_ViewAllProjects_<%=strProjectId %>" method="post" theme="simple"> --%>
                 	<input type="hidden" name="proId" id="proId" value="<%=strProjectId %>" />
                 	<input type="hidden" name="addTask" id="addTask<%=strProjectId %>"/>
                 	<s:hidden name="pageType" id="pageType"></s:hidden>
                 	
                 	<table class="tb_style" id="taskTable_<%=strProjectId %>" style="font-size: 10px;" width="100%">
					<tr>
						<th width="2%">&nbsp;</th>
						<th>
						<input type="hidden" name="strProTaskDependency" id="strProTaskDependency_<%=strProjectId %>" value="<%=strProTaskDependency %>"/>
						<input type="hidden" name="strProTeamEmp" id="strProTeamEmp_<%=strProjectId %>" value="<%=strProTeamEmp %>"/>
						<input type="hidden" name="strProEmpSkills" id="strProEmpSkills_<%=strProjectId %>" value="<%=strProEmpSkills %>"/>
						Task Name<sup>*</sup> </th>
						<th>Dependency & <br/>Dependency Type</th>
						<!-- <th>Dependency Type</th> -->
						<th>Priority</th>
						<!-- <th>Skills</th> -->
						<th>Resources<sup>*</sup></th>
						<th>Start Date<sup>*</sup></th>
						<th>Deadline<sup>*</sup></th>
						<th><%="Est. man-"+estimateLbl %><sup>*</sup></th>
						<th>Complete</th>
						<th>Color<sup>*</sup></th>
						<th>Actions</th>
						<th>Feeds</th>
					</tr>
					
			<% 	String isCustAdd = "0";
				if(strUserType!=null && strUserType.equals(IConstants.CUSTOMER)) {
					isCustAdd = "1";
				}
			%>
				<% 
					Map<String, List<String>> hmProTasks = hmProWiseTasks.get(strProjectId);
					if(hmProTasks == null) hmProTasks = new HashMap<String, List<String>>();
						
					Iterator<String> it1 = hmProTasks.keySet().iterator();
					int i = 0;
					int taskTRId = 0;
					while(it1.hasNext()) {
					String taskId = it1.next();
					
					String strProSubTaskDependency = hmProSubTaskDependency.get(strProjectId+"_"+taskId);
                 	
					List<String> alInner = hmProTasks.get(taskId);
					taskTRId = i;
				%>
				<% if((strUserType!=null && !strUserType.equals(IConstants.CUSTOMER)) || (strUserType!=null && strUserType.equals(IConstants.CUSTOMER) && uF.parseToInt(alInner.get(29))==1)) { %>
					<script type="text/javascript">
					$(function() {
						
						$("#deadline1"+<%=strProjectId %>+"_"+<%=i %>).datepicker({
							dateFormat : 'dd/mm/yy', minDate:"<%=(String)alProjects.get(13)%>", maxDate: "<%=(String)alProjects.get(14) %>", 
							onClose: function(selectedDate){
								$("#startDate"+<%=strProjectId %>+"_"+<%=i %>).datepicker("option", "maxDate", selectedDate);
							}
						});
						
						$("#startDate"+<%=strProjectId %>+"_"+<%=i %>).datepicker({
							dateFormat : 'dd/mm/yy', minDate:"<%=(String)alProjects.get(13)%>", maxDate: "<%=(String)alProjects.get(14)%>", 
							onClose: function(selectedDate){
								$("#deadline1"+<%=strProjectId %>+"_"+<%=i %>).datepicker("option", "minDate", selectedDate);
							}
						});
				
					});
					</script>
			<% } %>		
					<tr id="task_TR<%=strProjectId %>_<%=i%>">
						<td valign="top" width="2%">
						<% if(((uF.parseToInt(alInner.get(25)) == 0 || uF.parseToInt(alInner.get(25)) == 1) && !uF.parseToBoolean(alInner.get(27))) || (uF.parseToInt(alInner.get(25)) == 1 && uF.parseToBoolean(alInner.get(27))) ) { %>
							<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equals("L")) { %>
								<%if(uF.parseToDouble(alInner.get(16))>=100 && alInner.get(20) != null && alInner.get(20).equals("n")) { %>
									<input type="checkbox" value="<%=taskId%>" name="cb" id="cb"/> 
								<%} %>
							<%} %>
								<%=alInner.get(17) %>
						<% } else if((uF.parseToInt(alInner.get(25)) == 0) && uF.parseToBoolean(alInner.get(27))) { %>
							New task added by <%=alInner.get(28) %>
						<% } else if((uF.parseToInt(alInner.get(25)) == 2) && !uF.parseToBoolean(alInner.get(27))) { %>
							request for reschedule by <%=alInner.get(28) %>
						<% } else if((uF.parseToInt(alInner.get(25)) == 3) && !uF.parseToBoolean(alInner.get(27))) { %>
							request for reassign by <%=alInner.get(28) %>
						<% } else if((uF.parseToInt(alInner.get(25)) == -2) && !uF.parseToBoolean(alInner.get(27))) { %>
							New task requested by <%=alInner.get(28) %> (Customer)
						<% } %>
						
						</td>
						<td valign="top">
						<input type="hidden" name="strProSubTaskDependency_<%=strProjectId %>" id="strProSubTaskDependency_<%=strProjectId+"_"+taskTRId %>" value="<%=strProSubTaskDependency %>" />
						<input type="hidden" name="taskByCust<%=strProjectId %>" id="taskByCust<%=strProjectId %>_<%=i %>" value="<%=alInner.get(29) %>" />
						<input type="hidden" name="taskTRId<%=strProjectId %>" id="taskTRId<%=strProjectId %>_<%=i %>" value="<%=taskTRId %>" />
						<input type="hidden" name="tstFilledEmp<%=strProjectId %>" id="tstFilledEmp<%=strProjectId %>_<%=i %>" value="<%=alInner.get(14) %>" /> 
						<input type="hidden" name="taskDescription<%=strProjectId %>" id="taskDescription<%=strProjectId %>_<%=i %>" value="<%=alInner.get(21) %>"/>
						<input type="text" name="taskname<%=strProjectId %>" id="taskname<%=strProjectId %>_<%=i %>" value="<%=alInner.get(3) %>" class="validate[required]" style="width: 160px; font-size: 10px; height: 16px;" 
						<% if((proType == null || proType.equals("") || proType.equals("null") || proType.equals("L")) && ((strUserType!=null && !strUserType.equals(IConstants.CUSTOMER)) || (strUserType!=null && strUserType.equals(IConstants.CUSTOMER) && uF.parseToInt(alInner.get(29))==1))) { %>	
							onchange="saveTaskAndGetTaskId('<%=i %>', '<%=strProjectId %>');" 
						<% } %>
						<% if(strUserType!=null && strUserType.equals(IConstants.CUSTOMER) && uF.parseToInt(alInner.get(29))==0) { %>
							readonly="readonly"
						<% } %>
						/>
							<div id="addTaskSpan<%=strProjectId %>_<%=i %>" style="display: block;">
								<input type="hidden" name="taskID<%=strProjectId %>" id="taskID<%=strProjectId %>_<%=i %>" value="<%=alInner.get(0)%>" />
								<input type="hidden" name="<%=strProjectId %>_<%=alInner.get(0) %>" id="<%=strProjectId %>_<%=alInner.get(0) %>" value="<%=i %>" />
							</div>
						<div style="margin-top: -5px; margin-bottom: -11px; font-style: italic; color: gray;"><%=alInner.get(23) %> </div>
						<div>
						<% if(((uF.parseToInt(alInner.get(25)) == 0 || uF.parseToInt(alInner.get(25)) == 1) && !uF.parseToBoolean(alInner.get(27))) || (uF.parseToInt(alInner.get(25)) == 1 && uF.parseToBoolean(alInner.get(27))) ) { %>
							<%=alInner.get(18) %> <%=alInner.get(19) %>&nbsp;
						<% } %>
						 
						 <% if((strUserType!=null && !strUserType.equals(IConstants.CUSTOMER)) || (strUserType!=null && strUserType.equals(IConstants.CUSTOMER) && uF.parseToInt(alInner.get(29))==1)) { %>
							<a href="javascript:void(0)" onclick="updateTaskDescription(<%=strProjectId %>, '<%=i %>', 'taskDescription', 'T')">D</a>
							<%if(alProjects.get(19) != null && !alProjects.get(19).equals("O")) { %>
							<%
							String strChecked = "";
								if(uF.parseToInt(alInner.get(24)) == 1) {
									strChecked = "checked";
								}
							%>
								<div style="margin-top: -7px; margin-bottom: -14px;"><input type="checkbox" name="recurringTask<%=strProjectId %>" id="recurringTask<%=strProjectId %>_<%=i %>" <%=strChecked %> onclick="setValue('isRecurringTask<%=strProjectId %>_<%=i %>');" title="Add task to recurring in next frequency"/>Recurr Task</div>
							<% } %>
						<% } %>
								<input type="hidden" name="isRecurringTask<%=strProjectId %>" id="isRecurringTask<%=strProjectId %>_<%=i %>" value="<%=alInner.get(24) %>"/>
							
						</div>
						<div style="margin-top: -11px; margin-bottom: -10px; font-style: italic; color: gray;">assigned by: <%=alInner.get(22) %> </div>
						<% if(alInner.get(28) != null && !alInner.get(28).equals("")) { %>
							<div style="margin-top: -11px; margin-bottom: -10px; font-style: italic; color: gray;">requested by: <%=alInner.get(28) %> </div>
						<% } %>	 
						</td>

						<td valign="top">
						<% if(strUserType!=null && strUserType.equals(IConstants.CUSTOMER) && uF.parseToInt(alInner.get(29))==0) { %>
							<input type="hidden" name="dependency<%=strProjectId %>" id="dependency<%=strProjectId %>_<%=i %>"/>
							<input type="hidden" name="dependencyType<%=strProjectId %>" id="dependencyType<%=strProjectId %>_<%=i %>"/>
							<select name="dependency<%=strProjectId %>" id="dependency<%=strProjectId %>_<%=i %>" style="width:135px; font-size:10px;" disabled="disabled">
								<option value="">Select Dependency</option>
								<%=alInner.get(4)%>
							</select> <br/>
							<select name="dependencyType<%=strProjectId %>" id="dependencyType<%=strProjectId %>_<%=i %>" style="width: 135px; font-size: 10px; margin-top: 7px;" disabled="disabled">
								<option value="">Select Dependency Type</option>
								<option value="0"
									<%if(alInner.get(5) != null && alInner.get(5).equals("0")) { %>
									selected <% } %>>Start-Start</option>
								<option value="1"
									<%if(alInner.get(5) != null && alInner.get(5).equals("1")) { %>
									selected <% } %>>Finish-Start</option>
							</select>
						<% } else { %>
						<select name="dependency<%=strProjectId %>" id="dependency<%=strProjectId %>_<%=i %>" style="width:135px; font-size:10px;">
								<option value="">Select Dependency</option>
								<%=alInner.get(4)%>
						</select> <br/>
						<select name="dependencyType<%=strProjectId %>" id="dependencyType<%=strProjectId %>_<%=i %>" style="width: 135px; font-size: 10px; margin-top: 7px;" onchange="setDependencyPeriod(this.value, '<%=strProjectId %>', '<%=i %>', 'Task');">
								<option value="">Select Dependency Type</option>
								<option value="0"
									<%if(alInner.get(5) != null && alInner.get(5).equals("0")) { %>
									selected <% } %>>Start-Start</option>
								<option value="1"
									<%if(alInner.get(5) != null && alInner.get(5).equals("1")) { %>
									selected <% } %>>Finish-Start</option>
						</select>
						<% } %>
						</td>

						<td valign="top">
						<% if(strUserType!=null && strUserType.equals(IConstants.CUSTOMER) && uF.parseToInt(alInner.get(29))==0) { %>
							<input type="hidden" name="priority<%=strProjectId %>" id="priority<%=strProjectId %>_<%=i %>"/>
							<select name="priority<%=strProjectId %>" id="priority<%=strProjectId %>_<%=i %>" style="width: 70px; font-size: 10px;" disabled="disabled">
								<% for(GetPriorityList getPriorityList:priorityList) { %>
								<option value="<%=getPriorityList.getPriId() %>"
									<%if(alInner.get(6) != null && getPriorityList.getPriId().equals(alInner.get(6))) { %>
									selected <% } %>>
									<%=getPriorityList.getProName() %></option>
								<% } %>
							</select>
						<% } else { %>
							<select name="priority<%=strProjectId %>" id="priority<%=strProjectId %>_<%=i %>" style="width: 70px; font-size: 10px;" class="validate[required]">
								<% for(GetPriorityList getPriorityList:priorityList) { %>
								<option value="<%=getPriorityList.getPriId() %>"
									<%if(alInner.get(6) != null && getPriorityList.getPriId().equals(alInner.get(6))) { %>
									selected <% } %>>
									<%=getPriorityList.getProName() %></option>
								<% } %>
							</select>
						<% } %>
						</td>

						<%-- <td valign="top"><select name="empSkills<%=strProjectId %>" id="empSkills<%=strProjectId %>_<%=i %>" style="width: 85px; font-size: 10px;" onchange="getSkillwiseEmployee(this.value, '<%=i %>', '<%=strProjectId %>');">
								<option value="">Select Skill</option>
								<%=alInner.get(7)%>
						</select></td> --%>

						<td valign="top">
						<% if(strUserType!=null && strUserType.equals(IConstants.CUSTOMER)) { %>
							<% if(uF.parseToInt(alInner.get(29))==0) { %>
								<input type="hidden" name="emp_id<%=strProjectId %>_<%=taskTRId %>" id="hide_emp_id<%=strProjectId %>_<%=i %>" />
								<span id="empSpan<%=strProjectId %>_<%=i %>"> 
									<select name="emp_id<%=strProjectId %>_<%=taskTRId %>" id="emp_id<%=strProjectId %>_<%=i %>" style="width: 140px; font-size: 10px;" multiple size="3" disabled="disabled">
										<option value="">Select Employee</option><%=alInner.get(8)%>
									</select> 
								</span>
							<% } else { %>
								<input type="hidden" name="emp_id<%=strProjectId %>_<%=taskTRId %>" id="hide_emp_id<%=strProjectId %>_<%=i %>" />
							<% } %>
						<% } else { %>
							<% if((uF.parseToInt(alInner.get(25)) == 0 || uF.parseToInt(alInner.get(25)) == 1) && uF.parseToBoolean(alInner.get(27))) { 
								String[] arrResource = alInner.get(8).split("::::");
							%>
								<input type="hidden" name="emp_id<%=strProjectId %>_<%=taskTRId %>" id="hide_emp_id<%=strProjectId %>_<%=i %>" value="<%=arrResource[1] %>" />
								<%=arrResource[0] %>
							<% } else { %>
								<span id="empSpan<%=strProjectId %>_<%=i %>"> 
									<select name="emp_id<%=strProjectId %>_<%=taskTRId %>" id="emp_id<%=strProjectId %>_<%=i %>" style="width: 140px; font-size: 10px;" class="validate[required]" multiple size="3">
										<option value="">Select Employee</option><%=alInner.get(8)%>
									</select> 
								</span>
							<% } %>
						<% } %>
						</td>
						<td valign="top"><input type="text" id="startDate<%=strProjectId %>_<%=i %>" name="startDate<%=strProjectId %>" style="width: 55px; font-size: 10px; height: 16px;" class="validate[required]" value="<%=alInner.get(9)%>"
						<% if(strUserType!=null && strUserType.equals(IConstants.CUSTOMER) && uF.parseToInt(alInner.get(29))==0) { %>
							readonly="readonly"
						<% } %>
						/>
						</td>
						<td valign="top">
							<input type="text" id="deadline1<%=strProjectId %>_<%=i %>" name="deadline1<%=strProjectId %>" value="<%=alInner.get(10)%>" class="validate[required]" style="width: 55px; font-size: 10px; height: 16px;" 
							<% if(strUserType!=null && strUserType.equals(IConstants.CUSTOMER) && uF.parseToInt(alInner.get(29))==0) { %>
								readonly="readonly"
							<% } %>
							/>
						</td>
						<td valign="top">
							<input type="text" name="idealTime<%=strProjectId %>" id="idealTime<%=strProjectId %>_<%=i %>" onkeypress="return isNumberKey(event)" value="<%=alInner.get(11)%>" class="validate[required]" style="width: 30px; font-size: 10px; height: 16px; text-align: right;"
							<% if(strUserType!=null && strUserType.equals(IConstants.CUSTOMER) && uF.parseToInt(alInner.get(29))==0) { %>
								readonly="readonly"
							<% } %>
							/>
						</td>

						<td valign="top">
							<div class="anaAttrib1"><span id="TcompletePercent_<%=strProjectId %>_<%=alInner.get(0)%>"><%=alInner.get(16) %>%</span> 
							<% if(strUserType!=null && !strUserType.equals(IConstants.CUSTOMER)) { %>
							<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equals("L")) { %>
			                    <% if(uF.parseToInt(alInner.get(15)) == 0) { %>
			                    <%if(alInner.get(20) != null && alInner.get(20).equals("n")) { %>
			                   		<a href="javascript:void(0)" onclick="updateStatus(<%=alInner.get(0)%>,<%=strProjectId %>, 'Tcomplete')"><img src="images1/icons/popup_arrow.gif" title="Update Completion Status" height="8px"/></a>
			                    <% } } %>
		                    <% } %>
		                    <% } %>
							</div>
							<div id="Tcomplete_<%=strProjectId %>_<%=alInner.get(0)%>" class="outbox">
								<div class="greenbox" style="width: <%=alInner.get(16) %>%;"></div>
							</div>
						</td>
						
						<td valign="top"><input type="text" name="colourCode<%=strProjectId %>" value="<%=alInner.get(12)%>" id="colourCode<%=strProjectId %>_<%=i %>" class="validate[required]" style="width:7px; font-size: 10px; height: 16px; background-color: <%=alInner.get(12)%>" readonly="readonly"/> 
							<%-- <img align="left" style="cursor: pointer; position: absolute; padding: 5px 0 0 5px" src="images1/color_palate.png" id="pick1" onclick="cp2.select(document.getElementById('frm_ViewAllProjects_<%=strProjectId %>').colourCode<%=strProjectId %>_<%=i %>,'pick1'); return false;" /> --%>
						</td>

						<td valign="top" width="150px">
							<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equals("L")) { %>
							<% if((uF.parseToInt(alInner.get(25)) == 0 || uF.parseToInt(alInner.get(25)) == 1) && !uF.parseToBoolean(alInner.get(27))) { %>
							<select name="taskActions<%=strProjectId %>_<%=i %>" id="taskActions<%=strProjectId %>_<%=i %>" style="width: 100px;" onchange="executeTaskActions(this.value, this.parentNode.parentNode.rowIndex, '<%=i %>', '<%=strProjectId %>', '<%=uF.parseToDouble(alInner.get(13)) %>', '<%=alInner.get(0)%>', '<%=isRecurr %>', '<%=isCustAdd %>', '<%=pageType %>');">
		                    	<option value="">Actions</option>
		                    	<% if((strUserType!=null && !strUserType.equals(IConstants.CUSTOMER)) || (strUserType!=null && strUserType.equals(IConstants.CUSTOMER) && uF.parseToInt(alInner.get(29))==1)) { %>
		                    		<option value="1">Delete</option>
		                    	<% } %>
		                    	<%if(uF.parseToDouble(alInner.get(16)) >= 100 && alInner.get(20) != null && alInner.get(20).equals("n")  && strUserType!=null && !strUserType.equals(IConstants.CUSTOMER)) { %>
		                    		<option value="2">Complete</option>
		                    	<% } %>
		                    	<option value="3">Repeat Task </option>
		                    	<%if(uF.parseToInt(alInner.get(16)) < 100) { %>
		                    		<option value="4">Add Sub-task </option>
		                    	<% } %>
		                    	<%if(alInner.get(20) != null && alInner.get(20).equals("approved")  && strUserType!=null && !strUserType.equals(IConstants.CUSTOMER)) { %>
		                    		<option value="5">Reassign </option>
		                    	<% } %>	
		                    </select>
							<% } else if((uF.parseToInt(alInner.get(25)) == 0 || uF.parseToInt(alInner.get(25)) == 1) && uF.parseToBoolean(alInner.get(27))  && strUserType!=null && !strUserType.equals(IConstants.CUSTOMER)) { %>
							<select name="taskActions<%=strProjectId %>_<%=i %>" id="taskActions<%=strProjectId %>_<%=i %>" style="width: 100px;" onchange="executeNewTaskAlign(this.value, '<%=strProjectId %>', '<%=alInner.get(0)%>', 'T');">
		                    	<option value="">Actions</option>
		                    	<% if(uF.parseToInt(alInner.get(25)) == 0) { %>
		                    		<option value="1">Align</option>
		                    	<% } %>
		                    	<% if(alInner.get(14) == null || alInner.get(14).trim().equals("")) { %>
	                    			<option value="2">Deny</option>
	                    		<% } %>
		                    </select>
		                    <% } else if((uF.parseToInt(alInner.get(25)) == 2) && !uF.parseToBoolean(alInner.get(27)) && strUserType!=null && !strUserType.equals(IConstants.CUSTOMER)) { %>
							<select name="taskActions<%=strProjectId %>_<%=i %>" id="taskActions<%=strProjectId %>_<%=i %>" style="width: 100px;" onchange="executeRescheduleTask(this.value, '<%=strProjectId %>', '<%=alInner.get(0)%>', 'T');">
		                    	<option value="">Actions</option>
		                    	<option value="1">Allow</option>
	                    		<option value="2">Deny</option>
		                    </select>
		                    <% } else if((uF.parseToInt(alInner.get(25)) == 3) && !uF.parseToBoolean(alInner.get(27)) && strUserType!=null && !strUserType.equals(IConstants.CUSTOMER)) { %>
							<select name="taskActions<%=strProjectId %>_<%=alInner.get(0) %>" id="taskActions<%=strProjectId %>_<%=i %>" style="width: 100px;">
		                    	<option value="">Actions</option>
		                    	<option value="1">Reassign</option>
	                    		<option value="2">Deny</option>
		                    </select>
		                    <% } else if((uF.parseToInt(alInner.get(25)) == -2) && !uF.parseToBoolean(alInner.get(27)) && strUserType!=null && !strUserType.equals(IConstants.CUSTOMER)) { %>
							<select name="taskActions<%=strProjectId %>_<%=alInner.get(0) %>" id="taskActions<%=strProjectId %>_<%=i %>" style="width: 100px;">
		                    	<option value="">Actions</option>
		                    	<option value="1">Accept</option>
	                    		<option value="2">Deny</option>
		                    </select>
							<% } %>
							<%-- <a name="addName" href="javascript:void(0)" onclick="repeatTask('<%=i %>','<%=strProjectId %>')" title="Repeat this task" ><img src="images1/icons/icons/repeat_task.png" style="width: 16px; height: 16px;" /></a>
							<a href="javascript:void(0)" onclick="addNewTask('<%=strProjectId %>');" title="Add new task"><img src="images1/icons/icons/add_task.png" style="width: 16px; height: 16px;" /></a> 
							<% if(uF.parseToDouble(alInner.get(13)) > 0) { %>
								 <% if(i > 0) { %>
								 	<a href="javascript:void(0)" class="del" title="Delete Task" onclick="alert('You can not delete this task as user has already booked the time against this task.')"> - </a>
								 <% } %> 
								 <a href="javascript:void(0)" onclick="alert('You can not add sub task as user has already booked the time against this task.')" title="Add new sub task"><img src="images1/icons/icons/add_sub_task.png" style="width: 16px; height: 16px;" /></a>
							<% } else { %>
								<% if(i > 0) { %>
									<a href="javascript:void(0)" class="del" onclick="deleteTask('<%=i %>','<%=strProjectId %>')" title="Remove this Task"> - </a>
								<% } %> 
								<a href="javascript:void(0)" onclick="addNewSubTask('<%=i %>', this.parentNode.parentNode.rowIndex, '<%=strProjectId %>')" title="Add new sub task"><img src="images1/icons/icons/add_sub_task.png" style="width: 16px; height: 16px;" /></a>
							 <% } %> --%>
							 
							<% } %> 
						</td>
						<td valign="top">
							<a href="javascript:void(0);" onclick="openFeedsForm('<%=alInner.get(0) %>', '<%=strProjectId %>', '<%=proType %>');" title="click here for feed">
								<img src="images1/icons/feed.png" height="16" width="16">
							</a>
						</td>
					</tr>

					<% i++;
					if(hmProSubTasks == null) hmProSubTasks = new HashMap<String, List<List<String>>>();
					List<List<String>> proSubTaskList = hmProSubTasks.get(strProjectId+"_"+taskId);
						for(int j=0; proSubTaskList != null && j<proSubTaskList.size(); j++) {
							List<String> innerList = proSubTaskList.get(j);
				%>
				<% if((strUserType!=null && !strUserType.equals(IConstants.CUSTOMER)) || (strUserType!=null && strUserType.equals(IConstants.CUSTOMER) && uF.parseToInt(innerList.get(28))==1)) { %>
				<script type="text/javascript">
				$(function() {
					
					$("#subdeadline1"+<%=strProjectId %>+"_"+<%=i %>).datepicker({
						dateFormat : 'dd/mm/yy', minDate:"<%=(String)alProjects.get(13)%>", maxDate: "<%=(String)alProjects.get(14) %>", 
						onClose: function(selectedDate){
							$("#substartDate"+<%=strProjectId %>+"_"+<%=i %>).datepicker("option", "maxDate", selectedDate);
						}
					});
					
					$("#substartDate"+<%=strProjectId %>+"_"+<%=i %>).datepicker({
						dateFormat : 'dd/mm/yy', minDate:"<%=(String)alProjects.get(13)%>", maxDate: "<%=(String)alProjects.get(14)%>", 
						onClose: function(selectedDate){
							$("#subdeadline1"+<%=strProjectId %>+"_"+<%=i %>).datepicker("option", "minDate", selectedDate);
						}
					});
			
				});
				</script>
				<% } %>
					<tr id="task_TR<%=i%>">
						<td valign="top" width="2%">
						
						<% if(((uF.parseToInt(innerList.get(24)) == 0 || uF.parseToInt(innerList.get(24)) == 1) && !uF.parseToBoolean(innerList.get(26))) || (uF.parseToInt(innerList.get(24)) == 1 && uF.parseToBoolean(innerList.get(26))) ) { %>
							<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equals("L")) { %>
							<%if(uF.parseToDouble(innerList.get(15))>=100 && innerList.get(19) != null && innerList.get(19).equals("n")) { %>
								<input type="checkbox" value="<%=innerList.get(0) %>" name="cb" id="cb"/> 
							<%} %>
						<%} %>
							<%=innerList.get(16) %>
						<% } else if((uF.parseToInt(innerList.get(24)) == 0) && uF.parseToBoolean(innerList.get(26))) { %>
							New sub-task added by <%=innerList.get(27) %>
						<% } else if((uF.parseToInt(innerList.get(24)) == 2) && !uF.parseToBoolean(innerList.get(26))) { %>
							request for reschedule by <%=innerList.get(27) %>
						<% } else if((uF.parseToInt(innerList.get(24)) == 3) && !uF.parseToBoolean(innerList.get(26))) { %>
							request for reassign by <%=innerList.get(27) %>
						<% } else if((uF.parseToInt(innerList.get(24)) == -2) && !uF.parseToBoolean(innerList.get(26))) { %>
							New task requested by <%=innerList.get(27) %> (Customer)
						<% } %>
						
						
						</td>
						<td valign="top" align="right"><input type="hidden" name="TskTRId<%=strProjectId %>_<%=taskTRId %>" id="TskTRId<%=strProjectId %>_<%=taskTRId %>" value="<%=taskTRId %>">
							<input type="hidden" name="subTaskByCust<%=strProjectId %>_<%=taskTRId %>" id="subTaskByCust<%=strProjectId %>_0" value="<%=innerList.get(28) %>" />
							<input type="hidden" name="subTaskTRId<%=strProjectId %>_<%=taskTRId %>" id="subTaskTRId<%=strProjectId %>_<%=i %>" value="<%=i %>">
							<input type="hidden" name="tstFilledEmp<%=strProjectId %>" id="tstFilledEmp<%=strProjectId %>_<%=i %>" value="<%=innerList.get(14) %>" />
							<input type="hidden" name="subTaskDescription<%=strProjectId %>_<%=taskTRId %>" id="subTaskDescription<%=strProjectId %>_<%=i %>" value="<%=innerList.get(20) %>"/>
							<input type="text" name="subtaskname<%=strProjectId %>_<%=taskTRId %>" id="subtaskname<%=strProjectId %>_<%=i %>" value="<%=innerList.get(3) %>" class="validate[required]" style="width: 120px; font-size: 10px; height: 16px;" 
							<% if((proType == null || proType.equals("") || proType.equals("null") || proType.equals("L")) && ((strUserType!=null && !strUserType.equals(IConstants.CUSTOMER)) || (strUserType!=null && strUserType.equals(IConstants.CUSTOMER) && uF.parseToInt(innerList.get(28))==1))) { %>
								onchange="saveSubTaskAndGetSubTaskId('<%=i %>', '<%=taskTRId %>', '<%=strProjectId %>')" 
							<% } %>
							<% if(strUserType!=null && strUserType.equals(IConstants.CUSTOMER) && uF.parseToInt(innerList.get(28))==0) { %>
								readonly="readonly"
							<% } %>
							/>
							<span id="addSubTaskSpan<%=strProjectId %>_<%=i %>">
								<input type="hidden" name="subTaskID<%=strProjectId %>_<%=taskTRId %>" id="subTaskID<%=strProjectId %>_<%=i %>" value="<%=innerList.get(0)%>" />
								<input type="hidden" name="<%=strProjectId %>_<%=innerList.get(0) %>" id="<%=strProjectId %>_<%=innerList.get(0) %>" value="<%=i %>" />
						</span>
						<div style="margin-top: -5px; margin-bottom: -11px; font-style: italic; color: gray;"><%=innerList.get(22) %> </div>
						<div>
						<% if(((uF.parseToInt(innerList.get(24)) == 0 || uF.parseToInt(innerList.get(24)) == 1) && !uF.parseToBoolean(innerList.get(26))) || (uF.parseToInt(innerList.get(24)) == 1 && uF.parseToBoolean(innerList.get(26))) ) { %>
							<%=innerList.get(17) %> <%=innerList.get(18) %>
						<% } %>
						<% if((strUserType!=null && !strUserType.equals(IConstants.CUSTOMER)) || (strUserType!=null && strUserType.equals(IConstants.CUSTOMER) && uF.parseToInt(innerList.get(28))==1)) { %>
							<a href="javascript:void(0)" onclick="updateTaskDescription(<%=strProjectId %>, '<%=i %>', 'subTaskDescription', 'ST')">D</a>
							<%if(alProjects.get(19) != null && !alProjects.get(19).equals("O")) { %>
								<% 
								String strSTChecked = "";
									if(uF.parseToInt(innerList.get(23)) == 1) { 
										strSTChecked = "checked";
									}
							%>
								<div style="margin-top: -7px; margin-bottom: -14px;"><input type="checkbox" name="recurringSubTask<%=strProjectId %>_<%=taskTRId %>" id="recurringSubTask<%=strProjectId %>_<%=i %>" <%=strSTChecked %> onclick="setValue('isRecurringSubTask<%=strProjectId %>_<%=i %>');" title="Add sub task to recurring in next frequency"/>Recurr Subtask</div>
							<% } %>
						<% } %>	
								<input type="hidden" name="isRecurringSubTask<%=strProjectId %>_<%=taskTRId %>" id="isRecurringSubTask<%=strProjectId %>_<%=i %>" value="<%=innerList.get(23) %>"/>
						</div>
						<div style="margin-top: -11px; margin-bottom: -10px; font-style: italic; color: gray;">assigned by: <%=innerList.get(21) %> </div>
						<% if(innerList.get(27) != null && !innerList.get(27).equals("")) { %>
							<div style="margin-top: -11px; margin-bottom: -10px; font-style: italic; color: gray;">requested by: <%=innerList.get(27) %> </div>
						<% } %>
						</td>

						<td valign="top">
						<% if(strUserType!=null && strUserType.equals(IConstants.CUSTOMER) && uF.parseToInt(innerList.get(28))==0) { %>
							<input type="hidden" name="subDependency<%=strProjectId %>_<%=taskTRId %>" id="subDependency<%=strProjectId %>_<%=i %>"/>
							<input type="hidden" name="subDependencyType<%=strProjectId %>_<%=taskTRId %>" id="subDependencyType<%=strProjectId %>_<%=i %>"/>
							<select name="subDependency<%=strProjectId %>_<%=taskTRId %>" id="subDependency<%=strProjectId %>_<%=i %>" style="width: 135px; font-size: 10px;" disabled="disabled">
								<option value="">Select Dependency</option>
								<%=innerList.get(4) %>
							</select><br/>
							<select name="subDependencyType<%=strProjectId %>_<%=taskTRId %>" id="subDependencyType<%=strProjectId %>_<%=i %>" style="width: 135px; font-size: 10px; margin-top: 7px;"  disabled="disabled">
								<option value="">Select Dependency Type</option>
								<option value="0"
									<%if(innerList.get(5) != null && innerList.get(5).equals("0")) { %>
									selected <% } %>>Start-Start</option>
								<option value="1"
									<%if(innerList.get(5) != null && innerList.get(5).equals("1")) { %>
									selected <% } %>>Finish-Start</option>
							</select>
						<% } else { %>
							<select name="subDependency<%=strProjectId %>_<%=taskTRId %>" id="subDependency<%=strProjectId %>_<%=i %>" style="width: 135px; font-size: 10px;">
								<option value="">Select Dependency</option>
								<%=innerList.get(4) %>
							</select><br/>
							<select name="subDependencyType<%=strProjectId %>_<%=taskTRId %>" id="subDependencyType<%=strProjectId %>_<%=i %>" style="width: 135px; font-size: 10px; margin-top: 7px;" onchange="setDependencyPeriod(this.value, '<%=strProjectId %>', '<%=i %>', 'SubTask');">
								<option value="">Select Dependency Type</option>
								<option value="0"
									<%if(innerList.get(5) != null && innerList.get(5).equals("0")) { %>
									selected <% } %>>Start-Start</option>
								<option value="1"
									<%if(innerList.get(5) != null && innerList.get(5).equals("1")) { %>
									selected <% } %>>Finish-Start</option>
							</select>
						<% } %>
						</td>

						<td valign="top">
						<% if(strUserType!=null && strUserType.equals(IConstants.CUSTOMER) && uF.parseToInt(innerList.get(28))==0) { %>
							<input type="hidden" name="subpriority<%=strProjectId %>_<%=taskTRId %>" id="subpriority<%=strProjectId %>_<%=i %>"/>
							<select name="subpriority<%=strProjectId %>_<%=taskTRId %>" id="subpriority<%=strProjectId %>_<%=i%>" style="width: 70px; font-size: 10px;" disabled="disabled">
								<% for(GetPriorityList getPriorityList:priorityList) { %>
								<option value="<%=getPriorityList.getPriId() %>"
									<%if(innerList.get(6) != null && getPriorityList.getPriId().equals(innerList.get(6))) { %>
									selected <% } %>>
									<%=getPriorityList.getProName() %></option>
								<% } %>
							</select>
						<% } else { %>
							<select name="subpriority<%=strProjectId %>_<%=taskTRId %>" id="subpriority<%=strProjectId %>_<%=i%>" style="width: 70px; font-size: 10px;" class="validate[required]">
								<% for(GetPriorityList getPriorityList:priorityList) { %>
								<option value="<%=getPriorityList.getPriId() %>"
									<%if(innerList.get(6) != null && getPriorityList.getPriId().equals(innerList.get(6))) { %>
									selected <% } %>>
									<%=getPriorityList.getProName() %></option>
								<% } %>
							</select>
						<% } %>	
						</td>

						<%-- <td valign="top"><select name="empSubSkills<%=strProjectId %>_<%=taskTRId %>" id="empSubSkills<%=strProjectId %>_<%=i %>" style="width: 85px;  font-size: 10px;" onchange="getSubSkillwiseEmployee(this.value, '<%=i %>', '<%=taskTRId %>', '<%=strProjectId %>');">
								<option value="">Select Skill</option>
								<%=innerList.get(7)%>
						</select></td> --%>

						<td valign="top">
						<% if(strUserType!=null && strUserType.equals(IConstants.CUSTOMER) && uF.parseToInt(innerList.get(28))==0) { %>
							<% if(uF.parseToInt(innerList.get(28))==0) { %>
								<input type="hidden" name="sub_emp_id<%=strProjectId %>_<%=taskTRId %>_<%=i %>" id="hide_sub_emp_id<%=strProjectId %>_<%=i %>"/>
								<select name="sub_emp_id<%=strProjectId %>_<%=taskTRId %>_<%=i %>" id="sub_emp_id<%=strProjectId %>_<%=i %>" style="width: 140px; font-size: 10px;" class="validate[required]" multiple size="3" disabled="disabled">
									<option value="">Select Employee</option><%=innerList.get(8) %>
								</select> 
							<% } else { %>
								<input type="hidden" name="sub_emp_id<%=strProjectId %>_<%=taskTRId %>_<%=i %>" id="hide_sub_emp_id<%=strProjectId %>_<%=i %>"/>
							<% } %>	
						<% } else { %>
							<% if((uF.parseToInt(innerList.get(24)) == 0 || uF.parseToInt(innerList.get(24)) == 1) && uF.parseToBoolean(innerList.get(26))) {
								String[] arrResource = innerList.get(8).split("::::");
							%>
								<input type="hidden" name="sub_emp_id<%=strProjectId %>_<%=taskTRId %>_<%=i %>" id="hide_sub_emp_id<%=strProjectId %>_<%=i %>" value="<%=arrResource[1] %>" />
								<%=arrResource[0] %>
							<% } else { %>
							<span id="subEmpSpan<%=strProjectId %>_<%=i %>">
								<select name="sub_emp_id<%=strProjectId %>_<%=taskTRId %>_<%=i %>" id="sub_emp_id<%=strProjectId %>_<%=i %>" style="width: 140px; font-size: 10px;" class="validate[required]" multiple size="3">
									<option value="">Select Employee</option><%=innerList.get(8) %>
								</select> 
							</span>
							<% } %>
						<% } %>
						</td>
						
						<td valign="top">
							<input type="text" id="substartDate<%=strProjectId %>_<%=i %>" name="substartDate<%=strProjectId %>_<%=taskTRId %>" style="width: 55px; font-size: 10px; height: 16px;" class="validate[required]" value="<%=innerList.get(9)%>"
							<% if(strUserType!=null && strUserType.equals(IConstants.CUSTOMER) && uF.parseToInt(innerList.get(28))==0) { %>
								readonly="readonly"
							<% } %>
							/>
						</td>
						
						<td valign="top">
							<input type="text" id="subdeadline1<%=strProjectId %>_<%=i %>" name="subdeadline1<%=strProjectId %>_<%=taskTRId %>" value="<%=innerList.get(10)%>" class="validate[required]" style="width: 55px; font-size: 10px; height: 16px;"
							<% if(strUserType!=null && strUserType.equals(IConstants.CUSTOMER) && uF.parseToInt(innerList.get(28))==0) { %>
								readonly="readonly"
							<% } %>
							/>
						</td>
						
						<td valign="top">
							<input type="text" name="subidealTime<%=strProjectId %>_<%=taskTRId %>" id="subidealTime<%=strProjectId %>_<%=i %>" onkeypress="return isNumberKey(event)" value="<%=innerList.get(11)%>" class="validate[required]" style="width: 30px; font-size: 10px; height: 16px; text-align: right;"
							<% if(strUserType!=null && strUserType.equals(IConstants.CUSTOMER) && uF.parseToInt(innerList.get(28))==0) { %>
								readonly="readonly"
							<% } %>
							/>
						</td>
						
						<td>
						<div class="anaAttrib1"> <span id="STcompletePercent_<%=strProjectId %>_<%=innerList.get(0)%>"><%=innerList.get(15) %>% </span> 
							<% if(strUserType!=null && !strUserType.equals(IConstants.CUSTOMER)) { %>
							<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equals("L")) { %>
								<%if(innerList.get(19) != null && innerList.get(19).equals("n")) { %>
			                   		<a href="javascript:void(0)" onclick="updateStatus(<%=innerList.get(0)%>,<%=strProjectId %>, 'STcomplete')"><img src="images1/icons/popup_arrow.gif" title="Update Completion Status" height="8px"/></a>
			                    <% } %>
		                    <% } %>
		                    <% } %>
							</div>
							<div id="STcomplete_<%=strProjectId %>_<%=innerList.get(0)%>" class="outbox">
								<div class="greenbox" style="width: <%=innerList.get(15) %>%;"></div>
							</div>
						</td>
						
						<td valign="top">
							<input type="text" name="subcolourCode<%=strProjectId %>_<%=taskTRId %>" value="<%=innerList.get(12)%>" id="subcolourCode<%=strProjectId %>_<%=i %>" class="validate[required]" style="width:7px; font-size: 10px; height: 16px; background-color: <%=innerList.get(12)%>" readonly="readonly"/> 
							<%-- <img align="left" style="cursor: pointer; position: absolute; padding: 5px 0 0 5px" src="images1/color_palate.png" id="pick1" onclick="cp2.select(document.getElementById('frm_ViewAllProjects_<%=strProjectId %>').subcolourCode<%=strProjectId %>_<%=i %>,'pick1'); return false;" /> --%>
						</td>

						<td valign="top">
						<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equals("L")) { %>
						<% if((uF.parseToInt(innerList.get(24)) == 0 || uF.parseToInt(innerList.get(24)) == 1) && !uF.parseToBoolean(innerList.get(26))) { %>
						<select name="subtaskActions<%=strProjectId %>_<%=i %>" id="subtaskActions<%=strProjectId %>_<%=i %>" style="width: 100px;" onchange="executeSubTaskActions(this.value, this.parentNode.parentNode.rowIndex, '<%=i %>', '<%=strProjectId %>', '<%=uF.parseToDouble(innerList.get(13)) %>', '<%=taskTRId %>', '<%=innerList.get(0)%>', '<%=isRecurr %>', '<%=isCustAdd %>', '<%=pageType %>');">
	                    	<option value="">Actions</option>
	                    	<% if((strUserType!=null && !strUserType.equals(IConstants.CUSTOMER)) || (strUserType!=null && strUserType.equals(IConstants.CUSTOMER) && uF.parseToInt(innerList.get(28))==1)) { %>
	                    	<option value="1">Delete</option>
	                    	<% } %>
	                    	<%if(uF.parseToDouble(innerList.get(15)) >= 100 && innerList.get(19) != null && innerList.get(19).equals("n") && strUserType!=null && !strUserType.equals(IConstants.CUSTOMER)) { %>
	                    		<option value="2">Complete</option>
	                    	<% } %>
	                    	<option value="3">Repeat Sub-task </option>
	                    	<option value="4">Add Sub-task </option>
	                    	<%if(innerList.get(19) != null && innerList.get(19).equals("approved") && strUserType!=null && !strUserType.equals(IConstants.CUSTOMER)) { %>
	                    		<option value="5">Reassign </option>
	                    	<% } %>
	                    </select>
						<% } else if((uF.parseToInt(innerList.get(24)) == 0 || uF.parseToInt(innerList.get(24)) == 1) && uF.parseToBoolean(innerList.get(26)) && strUserType!=null && !strUserType.equals(IConstants.CUSTOMER)) { %>
						<select name="subtaskActions<%=strProjectId %>_<%=i %>" id="subtaskActions<%=strProjectId %>_<%=i %>" style="width: 100px;" onchange="executeNewTaskAlign(this.value, '<%=strProjectId %>', '<%=innerList.get(0)%>', 'ST', '<%=pageType %>');">
	                    	<option value="">Actions</option>
	                    	<option value="1">Align</option>
	                    	<option value="2">Deny</option>
	                    </select>
	                    <% } else if((uF.parseToInt(innerList.get(24)) == 2) && !uF.parseToBoolean(innerList.get(26)) && strUserType!=null && !strUserType.equals(IConstants.CUSTOMER)) { %>
						<select name="subtaskActions<%=strProjectId %>_<%=i %>" id="subtaskActions<%=strProjectId %>_<%=i %>" style="width: 100px;" onchange="executeRescheduleTask(this.value, '<%=strProjectId %>', '<%=innerList.get(0)%>', 'ST', '<%=pageType %>');">
	                    	<option value="">Actions</option>
	                    	<option value="1">Allow</option>
	                    	<option value="2">Deny</option>
	                    </select>
	                    <% } else if((uF.parseToInt(innerList.get(24)) == 3) && !uF.parseToBoolean(innerList.get(26)) && strUserType!=null && !strUserType.equals(IConstants.CUSTOMER)) { %>
						<select name="subtaskActions<%=strProjectId %>_<%=innerList.get(0) %>" id="subtaskActions<%=strProjectId %>_<%=i %>" style="width: 100px;">
	                    	<option value="">Actions</option>
	                    	<option value="1">Reassign</option>
	                    	<option value="2">Deny</option>
	                    </select>
	                    <% } else if((uF.parseToInt(innerList.get(24)) == -2) && !uF.parseToBoolean(innerList.get(26)) && strUserType!=null && !strUserType.equals(IConstants.CUSTOMER)) { %>
						<select name="subtaskActions<%=strProjectId %>_<%=innerList.get(0) %>" id="subtaskActions<%=strProjectId %>_<%=i %>" style="width: 100px;">
	                    	<option value="">Actions</option>
	                    	<option value="1">Accept</option>
	                    	<option value="2">Deny</option>
	                    </select>
						<% } %>
						<%-- <a name="addName" href="javascript:void(0)" onclick="repeatSubTask('<%=i %>', '<%=taskTRId %>', this.parentNode.parentNode.rowIndex, '<%=strProjectId %>')" title="Repeat this task"><img src="images1/icons/icons/repeat_task.png" style="width: 16px; height: 16px;" /></a>
						<a href="javascript:void(0)" onclick="addNewTask('<%=strProjectId %>');" title="Add new task"><img src="images1/icons/icons/add_task.png" style="width: 16px; height: 16px;" /></a> 
							<% if(uF.parseToDouble(innerList.get(13)) > 0) { %>
								 <a href="javascript:void(0)" class="del" title="Delete Sub Task" onclick="alert('You can not delete this sub task as user has already booked the time against this sub task.')"> - </a>
							<% } else { %>
								<a href="javascript:void(0)" class="del" onclick="deleteSubTask('<%=i %>', '<%=strProjectId %>')" title="Remove this sub task"> - </a> <!-- <img src="images1/icons/icons/close_button_icon.png" /> -->
							 <% } %>
							<a href="javascript:void(0)" onclick="addNewSubTask('<%=taskTRId %>', this.parentNode.parentNode.rowIndex, '<%=strProjectId %>')" title="Add new sub task"><img src="images1/icons/icons/add_sub_task.png" style="width: 16px; height: 16px;" /></a>
						 --%>
						<% } %>	
						</td>
						<td valign="top">
							<a href="javascript:void(0);" onclick="openFeedsForm('<%=innerList.get(0) %>', '<%=strProjectId %>', '<%=proType %>');" title="click here for feed">
								<img src="images1/icons/feed.png" height="16" width="16">
							</a>
						</td>
					</tr>
					<% 	i++;
				}
				//taskTRId++;
				}	
			%>
			
			
					<div> <input type="hidden" name="taskcount_<%=strProjectId %>" id="taskcount_<%=strProjectId %>" value="<%=i %>" /> </div>
				<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equals("L")) { %>
					<%  if(hmProTasks == null || hmProTasks.isEmpty() || hmProTasks.size()==0) { %>
					<script type="text/javascript">
						$(function() {
							
							$("#deadline1"+<%=strProjectId %>+"_0").datepicker({
								dateFormat : 'dd/mm/yy', minDate:"<%=(String)alProjects.get(13)%>", maxDate: "<%=(String)alProjects.get(14) %>", 
								onClose: function(selectedDate){
									$("#startDate"+<%=strProjectId %>+"_0").datepicker("option", "maxDate", selectedDate);
								}
							});
							
							$("#startDate"+<%=strProjectId %>+"_0").datepicker({
								dateFormat : 'dd/mm/yy', minDate:"<%=(String)alProjects.get(13)%>", maxDate: "<%=(String)alProjects.get(14)%>", 
								onClose: function(selectedDate){
									$("#deadline1"+<%=strProjectId %>+"_0").datepicker("option", "minDate", selectedDate);
								}
							});
					
							
						});
						</script>
						
						
					<tr id="task_TR<%=strProjectId %>_0">
						<td width="2%">&nbsp;</td>
						<td><input type="hidden" name="taskTRId<%=strProjectId %>" id="taskTRId<%=strProjectId %>_0" value="0" />
							<input type="hidden" name="taskByCust<%=strProjectId %>" id="taskByCust<%=strProjectId %>_0" value="<%=isCustAdd %>" />
							<input type="hidden" name="taskDescription<%=strProjectId %>" id="taskDescription<%=strProjectId %>_0" value=""/>
							<input type="text" name="taskname<%=strProjectId %>" id="taskname<%=strProjectId %>_0" class="validate[required]" style="width: 160px; font-size: 10px; height: 16px;" onchange="saveTaskAndGetTaskId('0', '<%=strProjectId %>');" />
							<div id="addTaskSpan<%=strProjectId %>_0" style="display: block;">
								<input type="hidden" name="taskID<%=strProjectId %>" id="taskID<%=strProjectId %>_0" value="0" />
							</div>
							<div><a href="javascript:void(0)" onclick="updateTaskDescription(<%=strProjectId %>, '0', 'taskDescription', 'T')">D</a> 
								<%if(alProjects.get(19) != null && !alProjects.get(19).equals("O")) { %>
									&nbsp;<input type="checkbox" name="recurringTask<%=strProjectId %>" id="recurringTask<%=strProjectId %>_0" onclick="setValue('isRecurringTask<%=strProjectId %>_0');" title="Add task to recurring in next frequency"/>Recurr Task
								<% } %>
								<input type="hidden" name="isRecurringTask<%=strProjectId %>" id="isRecurringTask<%=strProjectId %>_0" value="0"/>
							</div>
							</td>

						<td><select name="dependency<%=strProjectId %>" id="dependency<%=strProjectId %>_0" style="width:135px; font-size: 10px;">
								<option value="">Select Dependency</option>
								<%=uF.showData(strProTaskDependency, "") %></select>
							<br/>
							<select name="dependencyType<%=strProjectId %>" id="dependencyType<%=strProjectId %>_0" style="width:135px; font-size: 10px; margin-top: 7px;"> 
								<option value="">Select Dependency Type</option> 
								<option value="0">Start-Start</option> 
								<option value="1">Finish-Start</option> 
							</select>
							
							<%-- <s:select name="dependencyType<%=strProjectId %>" id="dependencyType<%=strProjectId %>_0" listKey="dependancyTypeId" headerKey="" headerValue="Select Dependency Type"
								listValue="dependancyTypeName" list="dependancyTypeList" key="" cssStyle="width:135px; font-size: 10px; margin-top: 7px;" /> --%>
						</td>

						<td><select name="priority<%=strProjectId %>" id="priority<%=strProjectId %>_0" style="width:70px; font-size: 10px;">
								<option value="0">Low</option>
								<option value="1">Medium</option>
								<option value="2">High</option>
							</select>
						</td>
						<%-- <td>
							<select name="empSkills<%=strProjectId %>" id="empSkills<%=strProjectId %>_0" style="width:85px; font-size: 10px;" onchange="getSkillwiseEmployee(this.value, '0', '<%=strProjectId %>');">
								<option value="">Select Skill</option>
								<%=uF.showData(strProEmpSkills, "") %></select>
						</td> --%>

						<td>
						<% if(strUserType!=null && strUserType.equals(IConstants.CUSTOMER)) { %>
							<input type="hidden" name="emp_id<%=strProjectId %>_<%=taskTRId %>" id="hide_emp_id<%=strProjectId %>_<%=i %>" />
						<% } else { %>
						<span id="empSpan0">
							<select name="emp_id<%=strProjectId %>_0" id="emp_id<%=strProjectId %>_0" style="width:140px; font-size: 10px;" class="validate[required]" multiple size="3">
								<option value="">Select Employee</option>
								<%=uF.showData(strProTeamEmp, "") %></select>
						</span>
						<% } %>
						</td>

						<td>
							<input type="text" id="startDate<%=strProjectId %>_0" name="startDate<%=strProjectId %>" style="width: 55px; font-size: 10px; height: 16px;" class="validate[required]">
						</td>
						
						<td>
							<input type="text" id="deadline1<%=strProjectId %>_0" name="deadline1<%=strProjectId %>" class="validate[required]" style="width: 55px; font-size: 10px; height: 16px;">
						</td>
						
						<td>
							<input type="text" name="idealTime<%=strProjectId %>" id="idealTime<%=strProjectId %>_0" onkeypress="return isNumberKey(event)" class="validate[required]" style="width: 30px; font-size: 10px; height: 16px; text-align: right;">
						</td>

						<td>&nbsp;</td>
						<% String myColor = "#C2AD99"; %>	
						<td><input type="text" name="colourCode<%=strProjectId %>" id="colourCode<%=strProjectId %>_0" class="validate[required]" style="width: 7px; font-size: 10px; height: 16px; background-color: <%=myColor %>;" value="<%=myColor %>" readonly="readonly" /> 
						<%-- <img align="left" style="cursor: pointer; position: absolute; padding: 5px 0 0 5px" src="images1/color_palate.png" id="pick1" onclick="cp2.select(document.getElementById('frm_ViewAllProjects_<%=strProjectId %>').colourCode<%=strProjectId %>_0,'pick1'); return false;" /> --%>
						</td>
					
						<td valign="top" width="150px">
						<select name="taskActions<%=strProjectId %>_0" id="taskActions<%=strProjectId %>_0" style="width: 100px;" onchange="executeTaskActions(this.value, this.parentNode.parentNode.rowIndex, '0', '<%=strProjectId %>', '', '', '<%=isRecurr %>', '<%=isCustAdd %>', '<%=pageType %>');">
		                    	<option value="">Actions</option>
		                    	<option value="3">Repeat Task </option>
		                    	<option value="4">Add Sub-task </option>
		                    </select>
						
						<%-- <a name="addName" href="javascript:void(0)" onclick="repeatTask(0, '<%=strProjectId %>')" title="Repeat this task" ><img src="images1/icons/icons/repeat_task.png" style="width: 16px; height: 16px;" /></a>
						<a href="javascript:void(0)" onclick="addNewTask('<%=strProjectId %>');" title="Add new task"><img src="images1/icons/icons/add_task.png" style="width: 16px; height: 16px;" /></a>
						<a href="javascript:void(0)" onclick="addNewSubTask(0, this.parentNode.parentNode.rowIndex, '<%=strProjectId %>')" title="Add new sub task"><img src="images1/icons/icons/add_sub_task.png" style="width: 16px; height: 16px;" /></a> --%>
						
						</td>
					</tr>
					
					<% } %>
				<% } %>	
					<%-- <tr>
						<td colspan="12" align="center"><s:submit name="addTask" cssClass="input_button" value="Save"/></td>
					</tr> --%>
				</table>
				
				
				<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equals("L")) { %>
				<div><a href="javascript:void(0)" onclick="addNewTask('<%=strProjectId %>', '<%=isRecurr %>', '<%=isCustAdd %>');" title="Add new task">
				<% if(strUserType!=null && strUserType.equals(IConstants.CUSTOMER)) { %>
					+Add New Task Request
				<% } else { %>
					+Add New Task
				<% } %>
				</a></div>
					<div style="text-align: center;">
					<%-- <s:submit name="addTask" cssClass="input_button" value="Save"/> --%>
				<% if(strUserType!=null && strUserType.equals(IConstants.CUSTOMER)) { %>
					<input type="button" name="addTask_<%=strProjectId %>" id="addTask_<%=strProjectId %>" class="input_button" value="Send Request" onclick="checkTimeFilledEmpOfAllTasks('<%=strProjectId %>')"/>
				<% } else { %>
					<input type="button" name="addTask_<%=strProjectId %>" id="addTask_<%=strProjectId %>" class="input_button" value="Save" onclick="checkTimeFilledEmpOfAllTasks('<%=strProjectId %>')"/>
				<% } %>	
					</div>
				<% } %> 
			<%-- </s:form> --%>
			</form>
							</div>
						</div>	
						</li>
					</ul>
                 </li> 
		<% } %>
		</div>
		
		<div style="text-align: center; float: left; width: 100%;">
		
		<% int intproCnt = uF.parseToInt(proCount);
			int pageCnt = 0;
			int minLimit = 0;
			
			for(int i=1; i<=intproCnt; i++) {
					minLimit = pageCnt * 10;
					pageCnt++;
		%>
		<% if(i ==1) {
			String strPgCnt = (String)request.getAttribute("proPage");
			String strMinLimit = (String)request.getAttribute("minLimit");
			if(uF.parseToInt(strPgCnt) > 1) {
				 strPgCnt = (uF.parseToInt(strPgCnt)-1) + "";
				 strMinLimit = (uF.parseToInt(strMinLimit)-10) + "";
			}
			if(strMinLimit == null) {
				strMinLimit = "0";
			}
			if(strPgCnt == null) {
				strPgCnt = "1";
			}
		%>
			<span style="color: lightgray;">
			<% if(uF.parseToInt((String)request.getAttribute("proPage")) > 1) { %>
				<a href="javascript:void(0);" onclick="loadMoreProjects('<%=strPgCnt %>','<%=strMinLimit %>','<%=uF.showData((String)request.getAttribute("proType"), "L") %>');">
				<%="< Prev" %></a>
			<% } else { %>
				<b><%="< Prev" %></b>
			<% } %>
			</span>
			<span><a href="javascript:void(0);" onclick="loadMoreProjects('<%=pageCnt %>','<%=minLimit %>','<%=uF.showData((String)request.getAttribute("proType"), "L") %>');"
			<% if(((String)request.getAttribute("proPage") == null && pageCnt == 1) || uF.parseToInt((String)request.getAttribute("proPage")) == pageCnt) { %>
			style="color: black;"
			<% } %>
			><%=pageCnt %></a></span>
			
			<% if((uF.parseToInt((String)request.getAttribute("proPage"))-3) > 1) { %>
				<b>...</b>
			<% } %>
		
		<% } %>
		
		<% if(i > 1 && i < intproCnt) { %>
		<% if(pageCnt >= (uF.parseToInt((String)request.getAttribute("proPage"))-2) && pageCnt <= (uF.parseToInt((String)request.getAttribute("proPage"))+2)) { %>
			<span><a href="javascript:void(0);" onclick="loadMoreProjects('<%=pageCnt %>','<%=minLimit %>','<%=uF.showData((String)request.getAttribute("proType"), "L") %>');"
			<% if(((String)request.getAttribute("proPage") == null && pageCnt == 1) || uF.parseToInt((String)request.getAttribute("proPage")) == pageCnt) { %>
			style="color: black;"
			<% } %>
			><%=pageCnt %></a></span>
		<% } %>
		<% } %>
		
		<% if(i == intproCnt && intproCnt > 1) {
			String strPgCnt = (String)request.getAttribute("proPage");
			String strMinLimit = (String)request.getAttribute("minLimit");
			 strPgCnt = (uF.parseToInt(strPgCnt)+1) + "";
			 strMinLimit = (uF.parseToInt(strMinLimit)+10) + "";
			 if(strMinLimit == null) {
				strMinLimit = "0";
			}
			if(strPgCnt == null) {
				strPgCnt = "1";
			}
			%>
			<% if((uF.parseToInt((String)request.getAttribute("proPage"))+3) < intproCnt) { %>
				<b>...</b>
			<% } %>
		
			<span><a href="javascript:void(0);" onclick="loadMoreProjects('<%=pageCnt %>','<%=minLimit %>','<%=uF.showData((String)request.getAttribute("proType"), "L") %>');"
			<%-- <a href="ViewAllProjects.action?proPage=<%=pageCnt %>&minLimit=<%=minLimit %>" --%>
			<% if(uF.parseToInt((String)request.getAttribute("proPage")) == pageCnt) { %>
			style="color: black;"
			<% } %>
			><%=pageCnt %></a></span>
			<span style="color: lightgray;">
			<% if(uF.parseToInt((String)request.getAttribute("proPage")) < pageCnt) { %>
				<a href="javascript:void(0);" onclick="loadMoreProjects('<%=strPgCnt %>','<%=strMinLimit %>','<%=uF.showData((String)request.getAttribute("proType"), "L") %>');"><%="Next >" %></a>
			<% } else { %>
				<b><%="Next >" %></b>
			<% } %>
			</span>
		<% } %>
		<%} %>
		
		</div>
		
		</ul>
</div>	
	 <%if(hmProject.size()==0) { %>
	 <div class="msg nodata"><span>Projects not available for this selection.</span></div>
	 <% } %>
	<%-- </s:form> --%>
</section>



<div id="editactivity"></div>
<div id="viewsummary"></div>
<div id="viewprojectsummary"></div>

<div id="viewcostsummary"></div>
<div id="editproject"></div>
<div id="edittask"></div>
<div id="addproject"></div>
<div id="addactivity"></div>
<div id="view"></div>
<div id="viewdocuments"></div>
<div id="reAssign"></div>
<div id="updateStatus"></div>
<div id="addtasksubtask"></div>
<div id="edittasksubtask"></div>
<div id="addsubtask"></div>
<div id="editsubtask"></div>
<div id="viewganttchart"></div>
<div id="addTaskDescription"></div>
<div id="openProjectFeedDiv"></div>
<div id="openFeedDiv"></div>

<script>
	//$("img.lazy").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10});
	$("img.lazy").lazyload({event : "sporty", threshold : 200,effect : "fadeIn",failure_limit : 10});

	$(window).bind("load", function() {
	    var timeout = setTimeout(function() { $("img.lazy").trigger("sporty") }, 1000);
	});  
</script>
