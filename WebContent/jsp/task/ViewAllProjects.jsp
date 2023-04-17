
<%@page import="com.konnect.jpms.select.FillSkills"%>
<%@page import="com.konnect.jpms.task.FillTaskEmpList"%>
<%@page import="com.konnect.jpms.task.GetPriorityList"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@page import="java.util.*"%>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>


<%	String btnSubmit = (String)request.getAttribute("btnSubmit");
	String pageType = (String)request.getAttribute("pageType");
	if((pageType == null || pageType.equalsIgnoreCase("null") || pageType.equals("")) && (btnSubmit == null || btnSubmit.equalsIgnoreCase("null") || btnSubmit.equals(""))) {
%>
	<script type="text/javascript" src="scripts/charts/jquery.min.js"> </script>
	<%-- <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jquery.lazyload/1.9.1/jquery.lazyload.min.js"></script> --%>
<% } %>

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

<div id="divResult">

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
	        +"<span style=\"float:left; width: 100%;\"><a href=\"javascript:void(0);\" style=\"margin: -15px 0px 15px 50px;\" onclick=\"addNewSubFolder('"+strProId+"','"+cnt+"', this.parentNode.parentNode.parentNode.rowIndex, '"+tableName+"', '"+rowCountName+"');\"> +Add Folder</a>"
	        +"<a href=\"javascript:void(0);\" style=\"margin: -15px 0px 15px 15px;\" onclick=\"addNewFolderDocs('"+strProId+"','"+cnt+"', this.parentNode.parentNode.parentNode.rowIndex, '"+tableName+"', '"+rowCountName+"');\"> +Add Document</a></span>";
	        
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
		    cell3.innerHTML = "<div style=\"float: left; width: 100%;\"><span style=\"float: left;\"><select name=\"folderSharingType"+strProId+"\" id=\"folderSharingType"+strProId+"_"+cnt+"\" style=\"width:100px\" class=\"validateRequired\" onchange=\"showHideResources('"+strProId+"', this.value, '"+cnt+"')\">"
			    +"<option value=\"0\">Public</option><option value=\"1\">Private Team</option><option value=\"2\">Individual Resource</option></select></span>"
			    +"<span id=\"proResourceSpan"+strProId+"_"+cnt+"\" style=\"display: none; float: left; margin-left: 9px;\"><select name=\"proFolderEmployee"+strProId+"_"+cnt+"\" id=\"proFolderEmployee"+strProId+"_"+cnt+"\" style=\"width:100px;\" multiple size=\"4\">"+proEmployee+"</select></span>"
			    +"</div>"
			    +"<div style=\"float: left; width: 100%; margin-top: 5px;\"><span style=\"float: left;\"><a href=\"javascript:void(0);\" style=\"font-weight:normal;\" id=\"sharePoc"+strProId+"_"+cnt+"\" onclick=\"showPoc('"+strProId+"','"+cnt+"')\">share customer</a></span>"
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
	        +"<a href=\"javascript:void(0);\" style=\"float:left; width:86%; margin: 4px 0px 15px 70px;\" onclick=\"addNewSubFolderDocs('"+strProId+"','"+cnt+"', this.parentNode.parentNode.rowIndex, '"+tableName+"', '"+rowCountName+"');\"> +Add Document</a>";
		    
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
		    cell3.innerHTML = "<div style=\"float: left; width: 100%;\"><span style=\"float: left;\"><select name=\"SubfolderSharingType"+strProId+"_"+folderTRId+"\" id=\"SubfolderSharingType"+strProId+"_"+cnt+"\" style=\"width:100px\" class=\"validateRequired\" onchange=\"showHideResources('"+strProId+"', this.value, '"+cnt+"')\">"
			    +"<option value=\"0\">Public</option><option value=\"1\">Private Team</option><option value=\"2\">Individual Resource</option></select></span>"
			    +"<span id=\"proResourceSpan"+strProId+"_"+cnt+"\" style=\"display: none; float: left; margin-left: 9px;\"><select name=\"proSubFolderEmployee"+strProId+"_"+folderTRId+"_"+cnt+"\" id=\"proSubFolderEmployee"+strProId+"_"+cnt+"\" style=\"width:100px;\" multiple size=\"4\">"+proEmployee+"</select></span>"
			    +"</div>"
			    +"<div style=\"float: left; width: 100%; margin-top: 5px;\"><span style=\"float: left;\"><a href=\"javascript:void(0);\" style=\"font-weight:normal;\" id=\"sharePoc"+strProId+"_"+cnt+"\" onclick=\"showPoc('"+strProId+"','"+cnt+"')\">share customer</a></span>"
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
		    cell3.innerHTML = "<div style=\"float: left; width: 100%;\"><span style=\"float: left;\"><select name=\"SubfolderDocDharingType"+strProId+"_"+folderTRId+"\" id=\"SubfolderDocDharingType"+strProId+"_"+cnt+"\" style=\"width:100px\" class=\"validateRequired\" onchange=\"showHideResources('"+strProId+"', this.value, '"+cnt+"')\">"
			    +"<option value=\"0\">Public</option><option value=\"1\">Private Team</option><option value=\"2\">Individual Resource</option></select></span>"
			    +"<span id=\"proResourceSpan"+strProId+"_"+cnt+"\" style=\"display: none; float: left; margin-left: 9px;\"><select name=\"proSubFolderDocEmployee"+strProId+"_"+folderTRId+"_"+cnt+"\" id=\"proSubFolderDocEmployee"+strProId+"_"+cnt+"\" style=\"width:100px;\" multiple size=\"4\">"+proEmployee+"</select></span>"
			    +"</div>"
			    +"<div style=\"float: left; width: 100%; margin-top: 5px;\"><span style=\"float: left;\"><a href=\"javascript:void(0);\" style=\"font-weight:normal;\" id=\"sharePoc"+strProId+"_"+cnt+"\" onclick=\"showPoc('"+strProId+"','"+cnt+"')\">share customer</a></span>"
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
		    cell3.innerHTML = "<div style=\"float: left; width: 100%;\"><span style=\"float: left;\"><select name=\"docSharingType"+strProId+"\" id=\"docSharingType"+strProId+"_"+cnt+"\" style=\"width:100px\" class=\"validateRequired\" onchange=\"showHideResources('"+strProId+"', this.value, '"+cnt+"')\">"
			    +"<option value=\"0\">Public</option><option value=\"1\">Private Team</option><option value=\"2\">Individual Resource</option></select></span>"
			    +"<span id=\"proResourceSpan"+strProId+"_"+cnt+"\" style=\"display: none; float: left; margin-left: 9px;\"><select name=\"proDocEmployee"+strProId+"_"+cnt+"\" id=\"proDocEmployee"+strProId+"_"+cnt+"\" style=\"width:100px;\" multiple size=\"4\">"+proEmployee+"</select></span>"
			    +"</div>"
			    +"<div style=\"float: left; width: 100%; margin-top: 5px;\"><span style=\"float: left;\"><a href=\"javascript:void(0);\" style=\"font-weight:normal;\" id=\"sharePoc"+strProId+"_"+cnt+"\" onclick=\"showPoc('"+strProId+"','"+cnt+"')\">share customer</a></span>"
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
		    cell3.innerHTML = "<div style=\"float: left; width: 100%;\"><span style=\"float: left;\"><select name=\"folderDocDharingType"+strProId+"_"+folderTRId+"\" id=\"folderDocDharingType"+strProId+"_"+cnt+"\" style=\"width:100px\" class=\"validateRequired\" onchange=\"showHideResources('"+strProId+"', this.value, '"+cnt+"')\">"
			    +"<option value=\"0\">Public</option><option value=\"1\">Private Team</option><option value=\"2\">Individual Resource</option></select></span>"
			    +"<span id=\"proResourceSpan"+strProId+"_"+cnt+"\" style=\"display: none; float: left; margin-left: 9px;\"><select name=\"proFolderDocEmployee"+strProId+"_"+folderTRId+"_"+cnt+"\" id=\"proFolderDocEmployee"+strProId+"_"+cnt+"\" style=\"width:100px;\" multiple size=\"4\">"+proEmployee+"</select></span>"
			    +"</div>"
			    +"<div style=\"float: left; width: 100%; margin-top: 5px;\"><span style=\"float: left;\"><a href=\"javascript:void(0);\" style=\"font-weight:normal;\" id=\"sharePoc"+strProId+"_"+cnt+"\" onclick=\"showPoc('"+strProId+"','"+cnt+"')\">share customer</a></span>"
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
         
        	$('#fade').on('click', 'a.close', function(){ //When clicking on the close or fade layer...
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
			 document.getElementById("uncompletedSpan").style.display = 'none';
			 document.getElementById("completedSpan").style.display = 'block';
		} else {		
			 for(var i=0; i<approvePr.length; i++) {
				 approvePr[i].checked = false;
			 }
			 document.getElementById("unblockedSpan").style.display = 'block';
			 document.getElementById("blockedSpan").style.display = 'none';
			 document.getElementById("uncompletedSpan").style.display = 'block';
			 document.getElementById("completedSpan").style.display = 'none';
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
			document.getElementById("uncompletedSpan").style.display = 'none';
			document.getElementById("completedSpan").style.display = 'block';
		} else {
			document.getElementById("unblockedSpan").style.display = 'block';
			document.getElementById("blockedSpan").style.display = 'none';
			document.getElementById("uncompletedSpan").style.display = 'block';
			document.getElementById("completedSpan").style.display = 'none';
		}
		
		if(cnt == chkCnt) {
			allLivePr.checked = true;
		} else {
			allLivePr.checked = false;
		}
	}
	
	
	function loadMoreProjects(proPage, minLimit, proType) {
		
		var proStatus = "";
		var strClient = "";
		if(document.getElementById("proStatus")) {
			proStatus = document.getElementById("proStatus").value;
		}
		var assignedBy = document.getElementById("assignedBy").value;
		var sortBy = document.getElementById("sortBy1").value;
		proType = document.getElementById("proType").value;
		var recurrOrMiles = document.getElementById("recurrOrMiles").value;
		var strSBU = getSelectedValue("f_sbu");
		var strService = getSelectedValue("f_service");
		var strProjectId = getSelectedValue("pro_id");
		var strManagerId = getSelectedValue("managerId");
		if(document.getElementById("client")) {
			strClient = getSelectedValue("client");
		}
		var pageType = document.getElementById("pageType").value;
		var paramValues = "";
			paramValues = 'proStatus='+proStatus+'&assignedBy='+assignedBy+'&recurrOrMiles='+recurrOrMiles+'&strSBU='+strSBU
			+'&strService='+strService+'&strProjectId='+strProjectId+'&strManagerId='+strManagerId+'&strClient='+strClient
			+'&proPage='+proPage+'&minLimit='+minLimit+'&proType='+proType+'&pageType='+pageType+'&sortBy='+sortBy;
        
    	var action = 'AllProjectNameList.action?'+paramValues;
    	//alert("action=>"+action);
    	$("#subDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$.ajax({
    		type : 'POST',
    		url: action,
    		success: function(result) {
            	$("#subDivResult").html(result);
       		}
    	});
    	
	}
	
	
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
		    "<input type=\"text\" name=\"taskname"+strProId+"\" id=\"taskname"+strProId+"_"+cnt+"\" class=\"validateRequired\" style=\"width:160px; font-size:10px; height: 16px;\" onchange=\"saveTaskAndGetTaskId('"+cnt+"', '"+strProId+"')\">"+
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
				cell5.innerHTML = "<span id=\"empSpan"+strProId+"_"+cnt+"\"><select name=\"emp_id"+strProId+"_"+cnt+"\" id=\"emp_id"+strProId+"_"+cnt+"\" style=\"width:140px; font-size: 10px;\" class=\"validateRequired\" multiple size=\"3\">"
					+"<option value=\"\">Select Employee</option>"+strTeamEmp+"</select></span>";
			}	
			//cell5.innerHTML = "<span id=\"empSpan"+cnt+"\">"+opt2+"</span>";
			
			var cell6 = row.insertCell(5);
			cell6.innerHTML = "<input type=\"text\" id=\"startDate"+strProId+"_"+cnt+"\" name=\"startDate"+strProId+"\" class=\"validateRequired\" style=\"width:55px; font-size:10px; height: 16px;\">";

			var cell7 = row.insertCell(6);
			cell7.innerHTML = "<input type=\"text\" id=\"deadline1"+strProId+"_"+cnt+"\" class=\"validateRequired\" name=\"deadline1"+strProId+"\" style=\"width:55px; font-size:10px; height: 16px;\">";
			
			var cell8 = row.insertCell(7);
			cell8.innerHTML = "<input type=\"text\" id=\"idealTime"+strProId+"_"+cnt+"\" name=\"idealTime"+strProId+"\" onkeypress=\"return isNumberKey(event)\" class=\"validateRequired\" style=\"width:30px; font-size:10px; height: 16px; text-align:right;\">";
			
			var cell9 = row.insertCell(8);
			cell9.innerHTML = "&nbsp;";
			
			var cell10 = row.insertCell(9);
			cell10.innerHTML = "<input type=\"text\" name=\"colourCode"+strProId+"\" id=\"colourCode"+strProId+"_"+cnt+"\" class=\"validateRequired\" style=\"width:7px; font-size:10px; height: 16px;\" readonly=\"readonly\"/>";
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
		

	<%-- function addNewTask(strProId, isRecurr, isCustAdd, pageType) {
		
		var usrType = document.getElementById("usrType").value;
	
		var taskCnt = document.getElementById("taskcount_"+strProId).value;
		var strTaskDepend = document.getElementById("strProTaskDependency_"+strProId).value;
		var strTeamEmp = document.getElementById("strProTeamEmp_"+strProId).value;
		
			var cnt=(parseInt(taskCnt)+1);
		    var table = document.getElementById("taskTable_"+strProId);
		    var rowCount = table.rows.length;
		    var row = table.insertRow(rowCount);
		    var myColor = '#'+(Math.random()*0xFFFFFF<<0).toString(16);
		    row.id="task_TR"+strProId+"_"+cnt;
		    var cell0 = row.insertCell(0);
			cell0.innerHTML = "&nbsp;";
			
			var recurrChechbox = "";
			if(isRecurr == 'Y') {
				recurrChechbox = "<input type=\"checkbox\" name=\"recurringTask"+strProId+"\" id=\"recurringTask"+strProId+"_"+cnt+"\" onclick=\"setValue('isRecurringTask"+strProId+"_"+cnt+"');\" title=\"Add task to recurring in next frequency\"/>Recurr Task";
			}
		    var cell1 = row.insertCell(1);
		    cell1.innerHTML = "<input type=\"hidden\" name=\"taskTRId"+strProId+"\" id=\"taskTRId"+strProId+"_"+cnt+"\" value=\""+cnt+"\">"+
		    "<input type=\"hidden\" name=\"taskByCust"+strProId+"\" id=\"taskByCust"+strProId+"_"+cnt+"\" value=\""+isCustAdd+"\" />"+
		    "<input type=\"hidden\" name=\"taskDescription"+strProId+"\" id=\"taskDescription"+strProId+"_"+cnt+"\">"+
		    "<input type=\"text\" name=\"taskname"+strProId+"\" id=\"taskname"+strProId+"_"+cnt+"\" class=\"validateRequired\" style=\"width:160px; font-size:10px; height: 16px;\" onchange=\"saveTaskAndGetTaskId('"+cnt+"', '"+strProId+"')\">"+
		    "<span id=\"addTaskSpan"+strProId+"_"+cnt+"\"><input type=\"hidden\" name=\"taskID"+strProId+"\" id=\"taskID"+strProId+"_"+cnt+"\" value=\"\"></span>"+
		    "<div><a href=\"javascript:void(0)\" onclick=\"updateTaskDescription('"+strProId+"', '"+cnt+"', 'taskDescription', 'T');\">D</a>"+
		    "&nbsp;"+recurrChechbox+
		    "<input type=\"hidden\" name=\"isRecurringTask"+strProId+"\" id=\"isRecurringTask"+strProId+"_"+cnt+"\" value=\"0\"/></div>";
		    
		    var cell2 = row.insertCell(2);
		    cell2.innerHTML = "<span id=\"dependencySpan"+strProId+"_"+cnt+"\"> <select name=\"dependency"+strProId+"\" id=\"dependency"+strProId+"_"+cnt+"\" style=\"width:135px; font-size:10px;\" ><option value=\"\">Select Dependency</option>"+strTaskDepend+"</select></span>"
			    +"<select name=\"dependencyType"+strProId+"\" id=\"dependencyType"+strProId+"_"+cnt+"\" style=\"width:135px; font-size:10px; margin-top:7px;\" onchange=\"setDependencyPeriod(this.value, '"+strProId+"', '"+cnt+"', 'Task');\"><option value=\"\">Select Dependency Type</option>"
			    +"<option value=\"0\">Start-Start</option><option value=\"1\">Finish-Start</option></select>";
			
		    var cell3 = row.insertCell(3);
			cell3.innerHTML = "<select name=\"priority"+strProId+"\" id=\"priority"+strProId+"_"+cnt+"\" style=\"width:70px; font-size:10px;\"><option value=\"0\">Low</option>"
			    +"<option value=\"1\">Medium</option><option value=\"2\">High</option></select>";
			
			if(usrType == '<%=IConstants.CUSTOMER %>') {
				var cell5 = row.insertCell(4);
				cell5.innerHTML = "<input type=\"hidden\" name=\"emp_id"+strProId+"_"+cnt+"\" id=\"hide_emp_id"+strProId+"_"+cnt+"\" />";
			} else { 
				var cell5 = row.insertCell(4);
				cell5.innerHTML = "<span id=\"empSpan"+strProId+"_"+cnt+"\"><select name=\"emp_id"+strProId+"_"+cnt+"\" id=\"emp_id"+strProId+"_"+cnt+"\" style=\"width:140px; font-size: 10px;\" class=\"validateRequired\" multiple size=\"3\">"
					+"<option value=\"\">Select Employee</option>"+strTeamEmp+"</select></span>";
			}	
			
			var cell6 = row.insertCell(5);
			cell6.innerHTML = "<input type=\"text\" id=\"startDate"+strProId+"_"+cnt+"\" name=\"startDate"+strProId+"\" class=\"validateRequired\" style=\"width:55px; font-size:10px; height: 16px;\">";

			var cell7 = row.insertCell(6);
			cell7.innerHTML = "<input type=\"text\" id=\"deadline1"+strProId+"_"+cnt+"\" class=\"validateRequired\" name=\"deadline1"+strProId+"\" style=\"width:55px; font-size:10px; height: 16px;\">";
			
			var cell8 = row.insertCell(7);
			cell8.innerHTML = "<input type=\"text\" id=\"idealTime"+strProId+"_"+cnt+"\" name=\"idealTime"+strProId+"\" onkeypress=\"return isNumberKey(event)\" class=\"validateRequired\" style=\"width:30px; font-size:10px; height: 16px; text-align:right;\">";
			
			var cell9 = row.insertCell(8);
			cell9.innerHTML = "&nbsp;";
			
			var cell10 = row.insertCell(9);
			cell10.innerHTML = "<input type=\"text\" name=\"colourCode"+strProId+"\" id=\"colourCode"+strProId+"_"+cnt+"\" class=\"validateRequired\" style=\"width:7px; font-size:10px; height: 16px; background-color: "+myColor+"\" value=\""+myColor+"\" readonly=\"readonly\"/>";
						
			var cell11 = row.insertCell(10);
			cell11.setAttribute("nowrap","nowrap");
			cell11.setAttribute("valign","top");
			cell11.innerHTML = "<select name=\"taskActions"+strProId+"_"+cnt+"\" id=\"taskActions"+strProId+"_"+cnt+"\" style=\"width: 100px;\" onchange=\"executeTaskActions(this.value, this.parentNode.parentNode.rowIndex, '"+cnt+"', '"+strProId+"', '', '', '"+isRecurr+"', '"+isCustAdd+"', '"+pageType+"');\">"+
			"<option value=\"\">Actions</option><option value=\"1\">Delete</option><option value=\"3\">Repeat Task </option><option value=\"4\">Add Sub-task </option>"+
			"</select>";
			
		    document.getElementById("taskcount_"+strProId).value = cnt;
		    
		    getTasksForDependency(cnt, strProId);
		    
		    setDate(cnt, strProId);
		} --%>
		
		
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
		    "<input type=\"text\" name=\"subtaskname"+strProId+"_"+taskTRId+"\" id=\"subtaskname"+strProId+"_"+cnt+"\" class=\"validateRequired\" style=\"width:120px; font-size:10px; height: 16px;\" onchange=\"saveSubTaskAndGetSubTaskId('"+cnt+"', '"+taskTRId+"', '"+strProId+"')\">"+
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
				cell5.innerHTML = "<span id=\"subEmpSpan"+cnt+"\"><select name=\"sub_emp_id"+strProId+"_"+taskTRId+"_"+cnt+"\" id=\"sub_emp_id"+strProId+"_"+cnt+"\" style=\"width:140px; font-size: 10px;\" class=\"validateRequired\" multiple size=\"3\">"
					+"<option value=\"\">Select Employee</option>"+strTeamEmp+"</select></span>";
			}
			
			var cell6 = row.insertCell(5);
			cell6.innerHTML = "<input type=\"text\" id=\"substartDate"+strProId+"_"+cnt+"\" name=\"substartDate"+strProId+"_"+taskTRId+"\" class=\"validateRequired\" style=\"width:55px; font-size:10px; height: 16px;\">";

			var cell7 = row.insertCell(6);
			cell7.innerHTML = "<input type=\"text\" id=\"subdeadline1"+strProId+"_"+cnt+"\" class=\"validateRequired\" name=\"subdeadline1"+strProId+"_"+taskTRId+"\" style=\"width:55px; font-size:10px; height: 16px;\">";
			
			var cell8 = row.insertCell(7);
			cell8.innerHTML = "<input type=\"text\" id=\"subidealTime"+strProId+"_"+cnt+"\" name=\"subidealTime"+strProId+"_"+taskTRId+"\" onkeypress=\"return isNumberKey(event)\" class=\"validateRequired\" style=\"width:30px; font-size:10px; height: 16px; text-align:right;\">";
			
			var cell9 = row.insertCell(8);
			cell9.innerHTML = "&nbsp;";
			
			var cell10 = row.insertCell(9);
			cell10.innerHTML = "<input type=\"text\" name=\"subcolourCode"+strProId+"_"+taskTRId+"\" id=\"subcolourCode"+strProId+"_"+cnt+"\" class=\"validateRequired\" style=\"width:7px; font-size:10px; height: 16px;\" readonly=\"readonly\"/>";
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
	    cell1.setAttribute('style', 'text-align: right;');
	    cell1.innerHTML = "<input type=\"hidden\" name=\"TskTRId"+strProId+"_"+taskTRId+"\" id=\"TskTRId"+strProId+"_"+taskTRId+"\" value=\""+taskTRId+"\"><input type=\"hidden\" name=\"subTaskTRId"+strProId+"_"+taskTRId+"\" id=\"subTaskTRId"+strProId+"_"+cnt+"\" value=\""+cnt+"\">"+
	    "<input type=\"hidden\" name=\"subTaskByCust"+strProId+"_"+taskTRId+"\" id=\"subTaskByCust"+strProId+"_"+cnt+"\" value=\""+isCustAdd+"\" />"+
	    "<input type=\"hidden\" name=\"subTaskDescription"+strProId+"_"+taskTRId+"\" id=\"subTaskDescription"+strProId+"_"+cnt+"\">"+
	    "<input type=\"text\" name=\"subtaskname"+strProId+"_"+taskTRId+"\" id=\"subtaskname"+strProId+"_"+cnt+"\" class=\"validateRequired\" style=\"width:120px; font-size:10px; height: 16px;\" onchange=\"saveSubTaskAndGetSubTaskId('"+cnt+"', '"+taskTRId+"', '"+strProId+"')\">"+
	    "<span id=\"addSubTaskSpan"+strProId+"_"+cnt+"\"><input type=\"hidden\" name=\"subTaskID"+strProId+"_"+taskTRId+"\" id=\"subTaskID"+strProId+"_"+cnt+"\" value=\"\"></span>"+
	    "<div><a href=\"javascript:void(0)\" onclick=\"updateTaskDescription('"+strProId+"', '"+cnt+"', 'subTaskDescription', 'ST');\">D</a>"+
	    "&nbsp;"+recurrChechbox+
	    "<input type=\"hidden\" name=\"isRecurringSubTask"+strProId+"_"+taskTRId+"\" id=\"isRecurringSubTask"+strProId+"_"+cnt+"\" value=\"0\"/></div>";
	    
	    var cell2 = row.insertCell(2);
	    cell2.innerHTML = "<span id=\"subDependencySpan"+strProId+"_"+cnt+"\"><select name=\"subDependency"+strProId+"_"+taskTRId+"\" id=\"subDependency"+strProId+"_"+cnt+"\" style=\"width:135px; font-size:10px;\"><option value=\"\">Select Dependency</option>"+strTaskDepend+"</select></span>"+
		    "<select name=\"subDependencyType"+strProId+"_"+taskTRId+"\" id=\"subDependencyType"+strProId+"_"+cnt+"\" style=\"width:135px; font-size:10px;\" onchange=\"setDependencyPeriod(this.value, '"+strProId+"', '"+cnt+"', 'SubTask');\"><option value=\"\">Select Dependency Type</option>"
		    +"<option value=\"0\">Start-Start</option><option value=\"1\">Finish-Start</option></select>";
		
	    var cell3 = row.insertCell(3);
		cell3.innerHTML = "<select name=\"subpriority"+strProId+"_"+taskTRId+"\" id=\"subpriority"+strProId+"_"+cnt+"\" style=\"width:70px; font-size:10px;\"><option value=\"0\">Low</option>"
	    +"<option value=\"1\">Medium</option><option value=\"2\">High</option></select>";
		
		if(usrType == '<%=IConstants.CUSTOMER %>') {
			var cell5 = row.insertCell(4);
			cell5.innerHTML = "<input type=\"hidden\" name=\"sub_emp_id"+strProId+"_"+taskTRId+"_"+cnt+"\" id=\"hide_sub_emp_id"+strProId+"_"+cnt+"\" />";
		} else {
			var cell5 = row.insertCell(4);
			cell5.innerHTML = "<span id=\"subEmpSpan"+strProId+"_"+cnt+"\"><select name=\"sub_emp_id"+strProId+"_"+taskTRId+"_"+cnt+"\" id=\"sub_emp_id"+strProId+"_"+cnt+"\" style=\"width:140px; font-size: 10px;\" class=\"validateRequired\" multiple size=\"3\">"
				+"<option value=\"\">Select Employee</option>"+strTeamEmp+"</select></span>";
		}
		
		var cell6 = row.insertCell(5);
		cell6.innerHTML = "<input type=\"text\" id=\"substartDate"+strProId+"_"+cnt+"\" name=\"substartDate"+strProId+"_"+taskTRId+"\" class=\"validateRequired\" style=\"width:55px; font-size:10px; height: 16px;\">";

		var cell7 = row.insertCell(6);
		cell7.innerHTML = "<input type=\"text\" id=\"subdeadline1"+strProId+"_"+cnt+"\" class=\"validateRequired\" name=\"subdeadline1"+strProId+"_"+taskTRId+"\" style=\"width:55px; font-size:10px; height: 16px;\">";
		
		var cell8 = row.insertCell(7);
		cell8.innerHTML = "<input type=\"text\" id=\"subidealTime"+strProId+"_"+cnt+"\" name=\"subidealTime"+strProId+"_"+taskTRId+"\" onkeypress=\"return isNumberKey(event)\" class=\"validateRequired\" style=\"width:30px; font-size:10px; height: 16px; text-align:right;\">";
		
		var cell9 = row.insertCell(8);
		cell9.innerHTML = "&nbsp;";
		
		var cell10 = row.insertCell(9);
		cell10.innerHTML = "<input type=\"text\" name=\"subcolourCode"+strProId+"_"+taskTRId+"\" id=\"subcolourCode"+strProId+"_"+cnt+"\" class=\"validateRequired\" style=\"width:7px; font-size:10px; height: 16px; background-color: "+myColor+"\" value=\""+myColor+"\" readonly=\"readonly\"/>";
		
		var cell11 = row.insertCell(10);
		cell11.setAttribute("nowrap","nowrap");
		cell11.setAttribute("valign","top");
		cell11.innerHTML = "<select name=\"subtaskActions"+strProId+"_"+cnt+"\" id=\"subtaskActions"+strProId+"_"+cnt+"\" style=\"width: 100px;\" onchange=\"executeSubTaskActions(this.value, this.parentNode.parentNode.rowIndex, '"+cnt+"', '"+strProId+"', '', '"+taskTRId+"', '', '"+isRecurr+"', '"+isCustAdd+"', '"+pageType+"');\">"+
		"<option value=\"\">Actions</option><option value=\"1\">Delete</option><option value=\"3\">Repeat Sub-task </option><option value=\"4\">Add Sub-task </option>"+
		"</select>";
		
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
		if(confirm('Are you sure, you wish to complete these projects?')) {
			var taskIds = "";
			if(document.getElementsByName('cb')) {
				var tasks = document.getElementsByName('cb');
				var i;
				for (i = 0; i < tasks.length; i++) {
				  if (tasks[i].checked) {
					  if(taskIds == "") {
						  taskIds = taskIds + tasks[i].value;
					  } else {
						  taskIds = taskIds + "," + tasks[i].value;
					  }
				  }
				}
			}
			
			var proIds = "";
			if(document.getElementsByName('approvePr')) {
				var pros = document.getElementsByName('approvePr');
				var i;
				for (i = 0; i < pros.length; i++) {
				  if (pros[i].checked) {
					  if(proIds == "") {
						  proIds = proIds + pros[i].value;
					  } else {
						  proIds = proIds + "," + pros[i].value;
					  }
				  }
				}
			}
			
			var proStatus = "";
			var strClient = "";
			if(document.getElementById("proStatus")) {
				proStatus = document.getElementById("proStatus").value;
			}
			var assignedBy = document.getElementById("assignedBy").value;
			var recurrOrMiles = document.getElementById("recurrOrMiles").value;
			var strSBU = getSelectedValue("f_sbu");
			var strService = getSelectedValue("f_service");
			var strProjectId = getSelectedValue("pro_id");
			var strManagerId = getSelectedValue("managerId");
			if(document.getElementById("client")) {
				strClient = getSelectedValue("client");
			}
			//alert("strClient ===>> " + strClient);
			$("#subDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$.ajax({
				type : 'POST',
				url: 'AllProjectNameList.action?proStatus='+proStatus+'&assignedBy='+assignedBy+'&recurrOrMiles='+recurrOrMiles+'&strSBU='+strSBU
					+'&strService='+strService+'&strProjectId='+strProjectId+'&strManagerId='+strManagerId+'&strClient='+strClient
					+'&approvePr='+proIds+'&approve=approve&pageType='+pageType+'&proType=L',
				success: function(result){
					$("#subDivResult").html(result);
		   		}
			});
		}
	}
	
	function blockedProjects(pageType) {
		/* var tasks = document.getElementById("cb"); */
		if(confirm('Are you sure, you wish to block these projects?')) {
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
			var proStatus = "";
			var strClient = "";
			if(document.getElementById("proStatus")) {
				proStatus = document.getElementById("proStatus").value;
			}
			var assignedBy = document.getElementById("assignedBy").value;
			var recurrOrMiles = document.getElementById("recurrOrMiles").value;
			var strSBU = getSelectedValue("f_sbu");
			var strService = getSelectedValue("f_service");
			var strProjectId = getSelectedValue("pro_id");
			var strManagerId = getSelectedValue("managerId");
			if(document.getElementById("client")) {
				strClient = getSelectedValue("client");
			}
			//alert("strClient ===>> " + strClient);
			$("#subDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$.ajax({
				type : 'POST',
				url: 'AllProjectNameList.action?proStatus='+proStatus+'&assignedBy='+assignedBy+'&recurrOrMiles='+recurrOrMiles+'&strSBU='+strSBU
					+'&strService='+strService+'&strProjectId='+strProjectId+'&strManagerId='+strManagerId+'&strClient='+strClient
					+'&approvePr='+proIds+'&blocked=blocked&pageType='+pageType+'&proType=L',
				success: function(result){
					$("#subDivResult").html(result);
		   		}
			});
		}
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
    
</script>

</g:compress>


<script type="text/javascript">
$(document).ready( function () {
	$("#f_sbu").multiselect().multiselectfilter();
	$("#f_service").multiselect().multiselectfilter();
	$("#pro_id").multiselect().multiselectfilter();
	$("#managerId").multiselect().multiselectfilter();
	$("#client").multiselect().multiselectfilter();
});

</script>     


	<%  
		UtilityFunctions uF = new UtilityFunctions();
	
		String proType = (String)request.getAttribute("proType");
		//String pageType = (String)request.getAttribute("pageType");
		
		String proCount = (String)request.getAttribute("proCount");
		
		String proPage = (String)request.getAttribute("proPage");
		String strMinLimit1 = (String)request.getAttribute("minLimit");
		
		
		String strTitle = (String)request.getAttribute(IConstants.TITLE); 
		String strUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
		String strEmpId = (String)session.getAttribute(IConstants.EMPID);
	%>
	
	<%
		String strAction = "ViewAllProjects.action";
		if(pageType != null && pageType.equals("MP")) {
			strAction = "ViewMyProjects.action";
		}
	%>
	
	
	<section class="content">
          <div class="row" <% if(strUserType != null && strUserType.equals(IConstants.EMPLOYEE)) { %> style="display: none;" <% } %>>
            <div class="col-md-12">
            	<s:form name="frm_adminproject_view" action="ViewAllProjects" theme="simple">
            		<s:hidden name="proId" id="proId" />
			    	<s:hidden name="proType" id="proType" />
            		<s:hidden name="pageType" id="pageType" />
			    	<s:hidden name="proPage" id="proPage" />
			    	<s:hidden name="minLimit" id="minLimit" />
			    	<s:hidden name="sortBy" id="sortBy" />
			    	<input type="hidden" name="usrType" id="usrType" value="<%=strUserType %>" />
			    	<s:hidden name="submitType" id="submitType" />
		            <div class="box box-default collapsed-box" style="margin-bottom: 0px;">
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
									<s:select name="pro_id" id="pro_id" listKey="projectID" listValue="projectName" list="projectdetailslist" key="" multiple="true" />
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
									<input type="button" name="btnSubmit" value="Submit" class="btn btn-primary" onclick="submitForm('2');"/>
								</div>
							</div>
		               </div>
		           </div>
		           
		           <div class="box" style="border: 0px none; margin-top: 1px; padding: 2px 5px; margin-bottom: 7px;">
					Sort By: 
					<s:select theme="simple" name="sortBy1" id="sortBy1" cssStyle="width: 120px;" 
						list="#{'1':'Latest on Top', '2':'Oldest on Top', '3':'A-Z', '4':'Z-A'}" onchange="loadMoreProjects('1', '0', '<%=proType %>');"/>
                  </div>
                  
	           </s:form>
                  
			</div>
		</div>
	
			
	
		<div class="row">
            <div class="col-md-12" style="margin-bottom: 10px;">
            	<%=uF.showData((String)session.getAttribute(IConstants.MESSAGE), "") %>
				<div style="float: left;width:98%;">
				<% if(session.getAttribute("sbMessage")!=null) {
						out.println(session.getAttribute("sbMessage"));	
					}
				%>
				</div>
            </div>
		</div>
		<% 	session.setAttribute(IConstants.MESSAGE, "");
			session.setAttribute("sbMessage", "");
		%>
		
		<div class="row">
			<div class="active tab-pane" id="subDivResult" style="min-height: 600px;"></div>
		</div>
		 
</section>



<script type="text/javascript" charset="utf-8">
	$(document).ready(function() {
		//alert("on load .......");
		var proType = document.getElementById("proType").value;
		getAllProjectNameList('AllProjectNameList', proType);
	});
	
	function getAllProjectNameList(strAction, proType) {
		/* $(".col-md-9 .nav-tabs-custom .nav-tabs").find("li").removeClass("active");
		$(".col-md-9").find('a:contains(Details)').parent().addClass("active"); */
		//alert("strAction ===>> " + strAction);
		var oldProType = document.getElementById("proType").value;
		document.getElementById("proType").value = proType;
		var pageType = document.getElementById("pageType").value;
		//alert("proType ===>> " + proType);
		var submitType = document.getElementById("submitType").value;
		var proId = '';
		if(submitType!=null && submitType != '') {
			proId = document.getElementById("proId").value;
		}
		<% if(pageType!=null) { %>
			pageType = '<%=pageType %>';
		<% } %>
		//alert("pageType ===>> " + pageType)
		var proStatus = "";
		var strClient = "";
		if(document.getElementById("proStatus")) {
			proStatus = document.getElementById("proStatus").value;
		}
		var assignedBy = document.getElementById("assignedBy").value;
		var recurrOrMiles = document.getElementById("recurrOrMiles").value;
		var strSBU = getSelectedValue("f_sbu");
		var strService = getSelectedValue("f_service");
		var strProjectId = getSelectedValue("pro_id");
		var strManagerId = getSelectedValue("managerId");
		if(document.getElementById("client")) {
			strClient = getSelectedValue("client");
		}
		//alert("strSBU ===>> " + strSBU);
		$("#subDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		
		$.ajax({
			type : 'POST',
			url: strAction+'.action?proStatus='+proStatus+'&assignedBy='+assignedBy+'&recurrOrMiles='+recurrOrMiles+'&strSBU='+strSBU
				+'&strService='+strService+'&strProjectId='+strProjectId+'&strManagerId='+strManagerId+'&strClient='+strClient
				+'&proType='+proType+'&pageType='+pageType+'&proId='+proId+'&submitType='+submitType,
			success: function(result){
				$("#subDivResult").html(result);
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
	
	
	function submitForm(type) {
		
		var proType = document.getElementById("proType").value;
		var pageType = document.getElementById("pageType").value;
		var proStatus = "";
		var strClient = "";
		if(document.getElementById("proStatus")) {
			proStatus = document.getElementById("proStatus").value;
		}
		var assignedBy = document.getElementById("assignedBy").value;
		var recurrOrMiles = document.getElementById("recurrOrMiles").value;
		var strSBU = getSelectedValue("f_sbu");
		var strService = getSelectedValue("f_service");
		var strProjectId = getSelectedValue("pro_id");
		var strManagerId = getSelectedValue("managerId");
		if(document.getElementById("client")) {
			strClient = getSelectedValue("client");
		}
		var paramValues = "";
		if(type != "" && type == '2') {
			paramValues = 'proStatus='+proStatus+'&assignedBy='+assignedBy+'&recurrOrMiles='+recurrOrMiles+'&strSBU='+strSBU
			+'&strService='+strService+'&strProjectId='+strProjectId+'&strManagerId='+strManagerId+'&strClient='+strClient
			+'&proType='+proType+'&btnSubmit=Submit'+'&pageType='+pageType;
		}
        
    	var action = 'ViewAllProjects.action?'+paramValues;
    	//alert("action=>"+action);
    	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$.ajax({
    		type : 'POST',
    		url: action,
    		success: function(result) {
            	$("#divResult").html(result);
            	$("#f_sbu").multiselect().multiselectfilter();
            	$("#f_service").multiselect().multiselectfilter();
            	$("#pro_id").multiselect().multiselectfilter();
            	$("#managerId").multiselect().multiselectfilter();
            	$("#client").multiselect().multiselectfilter();
       		}
    	});
    }

</script>


<script>
	//$("img.lazy").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10});
	$("img.lazy").lazyload({event : "sporty", threshold : 200,effect : "fadeIn",failure_limit : 10});

	$(window).bind("load", function() {
	    var timeout = setTimeout(function() { $("img.lazy").trigger("sporty") }, 1000);
	});  
</script>

</div>