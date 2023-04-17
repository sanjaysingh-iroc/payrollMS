<%@page import="com.konnect.jpms.select.FillSkills"%>
<%@page import="com.konnect.jpms.task.FillTaskEmpList"%>
<%@page import="com.konnect.jpms.task.GetPriorityList"%>
<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<style>
<!--
	.table-bordered1 th {
		border-right: 1px solid #FFFFFF !important;
		background-color: #EAEAEA;
	}
	
	.table-bordered1 {
		border: 1px solid #dfdfdf;
	}
	
	/* .table-bordered > thead > tr > th,
	.table-bordered > tbody > tr > th,
	.table-bordered > tfoot > tr > th,
	.table-bordered > thead > tr > td,
	.table-bordered > tbody > tr > td,
	.table-bordered > tfoot > tr > td {
		border: 1px solid #dfdfdf;
	} */
  
  
-->
</style>

<script type="text/javascript" charset="utf-8">
	$(document).ready(function() {
		//debugger;
		$('#myTable').DataTable({
			"order" : [],
			"columnDefs" : [ {
				"targets" : 'no-sort',
				"orderable" : false
			} ],
			'dom' : 'lBfrtip',
			buttons: [
			            'copy',
			            {
			                extend: 'csv',
			                "bBomInc": true
			            },
			            {
			                extend: 'excel',
			                "bBomInc": true
			            },
			            {
			                extend: 'pdf',
			                "bBomInc": true
			            },
			            {
			                extend: 'print',
			                "bBomInc": true
			            }
			        ]
		});

	});
	
	
	function addNewTaskInList(t_st_Type) {
		//alert(t_st_Type);
		var taskCnt = document.getElementById("taskcount").value;
		var strMyProjects = '';
		if(document.getElementById("strMyProjects")) {
			console.log("Before---"+document.getElementById("strMyProjects").value);
			strMyProjects = document.getElementById("strMyProjects").value;
			console.log("after---"+strMyProjects);
		}
		var cnt=(parseInt(taskCnt)+1);
	    var table = document.getElementById("myTable");
	    var rowCount = table.rows.length;
	    var row = table.insertRow(parseInt(rowCount)-1);
	    var myColor = '#'+(Math.random()*0xFFFFFF<<0).toString(16);
	    
	    /* var recurrChechbox = "";
		if(isRecurr == 'Y') {
			recurrChechbox = "<input type=\"checkbox\" name=\"recurringTask\" id=\"recurringTask"+cnt+"\" onclick=\"setValue('isRecurringTask"+cnt+"');\" title=\"Add task to recurring in next frequency\"/>Recurr Task";
		} */
	    row.id="task_TR_List"+cnt;
	    /* "+proStartDate+" "+proEndDate+" */
	    //alert("cnt ===>> " + cnt);
	    var cell0 = row.insertCell(0);
	    cell0.colSpan = 4;
	    /* cell0.innerHTML = "<input type=\"hidden\" name=\"taskTRId\" id=\"taskTRId"+cnt+"\" value=\""+cnt+"\">"+
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
	    "<input type=\"hidden\" name=\"isRecurringTask\" id=\"isRecurringTask"+cnt+"\" value=\"0\"/></div>"; */ 
	    
	    cell0.innerHTML = "<input type=\"hidden\" name=\"taskTRId\" id=\"taskTRId"+cnt+"\" value=\""+cnt+"\">"+
	    "<input type=\"hidden\" name=\"taskDescription\" id=\"taskDescription"+cnt+"\">"+
	    "<span name=\"spantaskname\" id=\"spantaskname"+cnt+"\" style=\"display: none; padding: 2px 5px; cursor: pointer;\" ondblclick=\"updateFields('spantaskname"+cnt+"', 'taskname"+cnt+"');\" onmouseover=\"addBgColor(this)\" onmouseout=\"removeBgColor(this)\" ></span>"+
	    "<input type=\"text\" name=\"taskname\" id=\"taskname"+cnt+"\" placeholder=\"Enter Task Name\" class=\"validateRequired\" style=\"width:200px !important;\" onchange=\"saveTaskAndGetTaskId('"+cnt+"', '"+t_st_Type+"', 'onchange')\" onmouseout=\"saveTaskAndGetTaskId('"+cnt+"', '"+t_st_Type+"', 'onmouseout')\" required>"+
	    "<span style=\"margin: 2px 0px 0px 5px;\"><a href=\"javascript:void(0)\" title=\"Task Description\" onclick=\"updateTaskDescription('"+cnt+"', 'taskDescription', '"+t_st_Type+"');\">D</a></span>"
	    +"<span id=\"myprojectSpan"+cnt+"\">"
	    +"<span name=\"spanstrProjects\" id=\"spanstrProjects"+cnt+"\" style=\"display: none; padding: 2px 5px; margin: 2px 0px 2px 10px; cursor: pointer;\" ondblclick=\"updateFields('spanstrProjects"+cnt+"', 'strProjects"+cnt+"');\" onmouseover=\"addBgColor(this)\" onmouseout=\"removeBgColor(this)\" ></span>"
	    +"<select name=\"strProjects\" id=\"strProjects"+cnt+"\" style=\"width:175px !important; margin: 2px 0px 0px 10px;\" onchange=\"setProjectName('"+cnt+"', '"+t_st_Type+"'); \" required>"//getTasksForDependency(this.value, '"+cnt+"', '"+t_st_Type+"'); 
	    +"<option value=\"\">Select Project</option>"+strMyProjects+"</select></span>"+
	    "<span id=\"addTaskSpan"+cnt+"\"><input type=\"hidden\" name=\"taskID\" id=\"taskID"+cnt+"\" value=\"\"></span>"+
	    "<span name=\"spanpriority\" id=\"spanpriority"+cnt+"\" style=\"display: none; width:85px !important; padding: 2px 5px; margin: 2px 0px 2px 10px; cursor: pointer;\" ondblclick=\"updateFields('spanpriority"+cnt+"', 'priority"+cnt+"');\" onmouseover=\"addBgColor(this)\" onmouseout=\"removeBgColor(this)\" ></span>"
		+"<select name=\"priority\" id=\"priority"+cnt+"\" style=\"margin: 2px 0px 0px 10px; width:85px !important;\" onchange=\"setPriority('"+cnt+"', '"+t_st_Type+"')\" ><option value=\"0\">Low</option>"
		+"<option value=\"1\">Medium</option><option value=\"2\">High</option></select>"
		+"<span name=\"spanstartDate\" id=\"spanstartDate"+cnt+"\" style=\"display: none; padding: 2px 5px; margin: 2px 0px 2px 10px; cursor: pointer;\" ondblclick=\"updateFields('spanstartDate"+cnt+"', 'startDate"+cnt+"');\" onmouseover=\"addBgColor(this)\" onmouseout=\"removeBgColor(this)\" ></span>"
		+"<input type=\"text\" id=\"startDate"+cnt+"\" name=\"startDate\" placeholder=\"Start Date\" class=\"validateRequired\" style=\"margin: 2px 0px 0px 10px; width:85px !important;\" onchange=\"setStartDate('"+cnt+"', '"+t_st_Type+"')\" onmouseout=\"setStartDate('"+cnt+"', '"+t_st_Type+"')\">"
		+"<span name=\"spandeadline1\" id=\"spandeadline1"+cnt+"\" style=\"display: none; padding: 2px 5px; margin: 2px 0px 2px 10px; cursor: pointer;\" ondblclick=\"updateFields('spandeadline1"+cnt+"', 'deadline1"+cnt+"');\" onmouseover=\"addBgColor(this)\" onmouseout=\"removeBgColor(this)\" ></span>"
		+"<input type=\"text\" id=\"deadline1"+cnt+"\" placeholder=\"Deadline\" class=\"validateRequired\" name=\"deadline1\" style=\"margin: 2px 0px 0px 10px; width:85px !important;\" onchange=\"setDeadline('"+cnt+"', '"+t_st_Type+"')\" onmouseout=\"setDeadline('"+cnt+"', '"+t_st_Type+"')\">"
		+"<span name=\"spanidealTime\" id=\"spanidealTime"+cnt+"\" title=\"Ideal Time\" style=\"display: none; width:60px !important; padding: 2px 20px; margin: 2px 0px 2px 10px; cursor: pointer;\" ondblclick=\"updateFields('spanidealTime"+cnt+"', 'idealTime"+cnt+"');\" onmouseover=\"addBgColor(this)\" onmouseout=\"removeBgColor(this)\" ></span>"
		+"<input type=\"text\" min=\"0\" id=\"idealTime"+cnt+"\" name=\"idealTime\" placeholder=\"Duration\" onkeypress=\"return isNumberKey(event)\" onblur=\"setIdealTime('"+cnt+"', '"+t_st_Type+"')\" onchange=\"setIdealTime('"+cnt+"', '"+t_st_Type+"')\" onmouseout=\"setIdealTime('"+cnt+"', '"+t_st_Type+"')\" class=\"validateRequired\" style=\"margin: 2px 0px 0px 10px; width:60px !important; text-align:right;\">"+
		"<input type=\"hidden\" name=\"colourCode\" id=\"colourCode"+cnt+"\" value=\""+myColor+"\" />"
		+"<a href=\"javascript:void(0)\" onclick=\"deleteTaskList('"+cnt+"', '"+t_st_Type+"');\"> <i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"></i></a>";
	    //alert("cnt 1 ===>> " + cnt);
	    /* var cell1 = row.insertCell(1);
	    cell1.innerHTML = "";
	    var cell2 = row.insertCell(2);
	    cell2.innerHTML = "<span name=\"spanstartDate\" id=\"spanstartDate"+cnt+"\" style=\"display: none; padding: 2px 5px; cursor: pointer;\" ondblclick=\"updateFields('spanstartDate"+cnt+"', 'startDate"+cnt+"');\" onmouseover=\"addBgColor(this)\" onmouseout=\"removeBgColor(this)\" ></span>"
		+"<input type=\"text\" id=\"startDate"+cnt+"\" name=\"startDate\" placeholder=\"Start Date\" class=\"validateRequired\" style=\"margin-bottom: 5px; width:85px !important;\" onchange=\"setStartDate('"+cnt+"', '"+t_st_Type+"')\" onmouseout=\"setStartDate('"+cnt+"', '"+t_st_Type+"')\">"
		+"<span name=\"spandeadline1\" id=\"spandeadline1"+cnt+"\" style=\"display: none; padding: 2px 5px; cursor: pointer;\" ondblclick=\"updateFields('spandeadline1"+cnt+"', 'deadline1"+cnt+"');\" onmouseover=\"addBgColor(this)\" onmouseout=\"removeBgColor(this)\" ></span>"
		+"<input type=\"text\" id=\"deadline1"+cnt+"\" placeholder=\"Deadline\" class=\"validateRequired\" name=\"deadline1\" style=\"width:85px !important;\" onchange=\"setDeadline('"+cnt+"', '"+t_st_Type+"')\" onmouseout=\"setDeadline('"+cnt+"', '"+t_st_Type+"')\">";
		var cell3 = row.insertCell(3);
		cell3.innerHTML = "<span name=\"spanidealTime\" id=\"spanidealTime"+cnt+"\" style=\"display: none; padding: 2px 5px; cursor: pointer;\" ondblclick=\"updateFields('spanidealTime"+cnt+"', 'idealTime"+cnt+"');\" onmouseover=\"addBgColor(this)\" onmouseout=\"removeBgColor(this)\" ></span>"
		+"<input type=\"text\" min=\"0\" id=\"idealTime"+cnt+"\" name=\"idealTime\" onkeypress=\"return isNumberKey(event)\" onblur=\"setIdealTime('"+cnt+"', '"+t_st_Type+"')\" onchange=\"setIdealTime('"+cnt+"', '"+t_st_Type+"')\" onmouseout=\"setIdealTime('"+cnt+"', '"+t_st_Type+"')\" class=\"validateRequired\" style=\"width:45px !important; text-align:right;\">"+
		"<input type=\"hidden\" name=\"colourCode\" id=\"colourCode"+cnt+"\" value=\""+myColor+"\" />"; */
		//alert("cnt 4 ===>> " + cnt);
	    document.getElementById("taskcount").value = cnt;
	    
//	    document.getElementById("priority").id = "priority"+cnt;
//    getTasksForDependency(cnt);
	    
	    setDate(cnt);
	}
	
	
	
	function setDate(id) {
		$("#startDate"+id).datepicker({
		    format: 'dd/mm/yyyy',
		    autoclose: true
		}).on('changeDate', function (selected) {
		    var minDate = new Date(selected.date.valueOf());
		    $('#deadline1'+id).datepicker('setStartDate', minDate);
		    //var now = new Date(minDate.getFullYear(), minDate.getMonth(), minDate.getDate(), 0, 0, 0, 0);
		    var nMonth = (minDate.getMonth()+1);
		    if(nMonth<10) {
		    	nMonth = "0"+nMonth;
		    }
		    var now = minDate.getDate()+"/"+nMonth+"/"+minDate.getFullYear();
		    document.getElementById("startDate"+id).style.display = 'none';
			document.getElementById("spanstartDate"+id).style.display = 'inline';
		    document.getElementById("spanstartDate"+id).innerHTML = now;
		    var taskID = document.getElementById("taskID"+id).value;
		    getContent("", 'SaveTaskOrSubtaskAndGetId.action?operation=OTHER_FIELDS&fieldType=STARTDATE&fieldValue='+now+'&taskId='+taskID+'&type=MP_Task');
		});
		
		$("#deadline1"+id).datepicker({
		    format: 'dd/mm/yyyy',
		    autoclose: true
		}).on('changeDate', function (selected) {
		    var minDate = new Date(selected.date.valueOf());
		    $('#startDate'+id).datepicker('setEndDate', minDate);
		    var nMonth = (minDate.getMonth()+1);
		    if(nMonth<10) {
		    	nMonth = "0"+nMonth;
		    }
		    var now = minDate.getDate()+"/"+nMonth+"/"+minDate.getFullYear();
		    document.getElementById("deadline1"+id).style.display = 'none';
			document.getElementById("spandeadline1"+id).style.display = 'inline';
		    document.getElementById("spandeadline1"+id).innerHTML = now;
		    var taskID = document.getElementById("taskID"+id).value;
		    getContent("", 'SaveTaskOrSubtaskAndGetId.action?operation=OTHER_FIELDS&fieldType=DEADLINE&fieldValue='+now+'&taskId='+taskID+'&type=MP_Task');
		});
		
	}
		
	function deleteTaskList(count, t_st_Type) {
		//alert("count ==========>> " + count);
		var tstType = "task";
		if(confirm('Are you sure, you want to cancel this '+tstType+'?')) {
			deleteTaskFromDB(count, t_st_Type);
			var trIndex = document.getElementById("task_TR_List"+count).rowIndex;
		    document.getElementById("lt").deleteRow(trIndex);
		}
	}
		
	function deleteTaskFromDB(taskTRId, t_st_Type) {
		if(t_st_Type == 'ST') {
			var taskId = document.getElementById("subTaskID"+taskTRId).value;
			getContent('addSubTaskSpan'+taskTRId, 'SaveTaskOrSubtaskAndGetId.action?taskId='+taskId+'&type=DelSubTask');
		} else {
			var proId = document.getElementById("strProjects"+taskTRId).value;
			var taskId = document.getElementById("taskID"+taskTRId).value;
			getContent('addTaskSpan'+taskTRId, 'SaveTaskOrSubtaskAndGetId.action?proId='+proId+'&taskId='+taskId+'&type=DelTask');
		}
	}
	
	function saveTaskAndGetTaskId(cnt, t_st_Type, userEvent) {
		if(t_st_Type == 'T') {
			var proId = document.getElementById("strProjects"+cnt).value;
			//alert("proId ===>>> " + proId);
			var taskName = document.getElementById("taskname"+cnt).value;
			var taskID = document.getElementById("taskID"+cnt).value;
			//alert("taskName ===>>> " + taskName);
			if(taskName != '') {
				if(userEvent !='' && userEvent == 'onchange') {
					getContent('addTaskSpan'+cnt, 'SaveTaskOrSubtaskAndGetId.action?proId='+proId+'&taskId='+taskID+'&taskName='+encodeURIComponent(taskName)
						+"&count="+cnt+'&type=MP_Task');
				}
				document.getElementById("taskname"+cnt).style.display = 'none';
				document.getElementById("spantaskname"+cnt).style.display = 'inline';
				document.getElementById("spantaskname"+cnt).innerHTML = taskName;
			}
		} else {
			var taskId = document.getElementById("strTasks"+cnt).value;
			var subTaskName = document.getElementById("subtaskname"+cnt).value;
			var subTaskID = document.getElementById("subTaskID"+cnt).value;
			if(parseFloat(taskId) > 0 && subTaskName!='') {
				getContent('addSubTaskSpan'+cnt, 'SaveTaskOrSubtaskAndGetId.action?subTaskId='+subTaskID+'&subTaskName='+encodeURIComponent(subTaskName)
					+'&taskId='+taskId+"&count="+cnt+'&type=MP_SubTask');
			} else {
				alert('No task available for this sub task, Please add task.');
			}
		}
	}
	

	function setProjectName(cnt, t_st_Type) {
		var strProjects = document.getElementById("strProjects"+cnt).value;
		document.getElementById("strProjects"+cnt).style.display = 'none';
		document.getElementById("spanstrProjects"+cnt).style.display = 'inline';
		var strstrProjects = 'Not Aligned';
		var sel = document.getElementById("strProjects"+cnt);
		var text = sel.options[sel.selectedIndex].text;
		if(parseInt(strProjects)>0) {
			strstrProjects = text;
		}
		/* if(document.getElementById(strProjects)) {
			strstrProjects = document.getElementById(strProjects).value;
		} */
		var taskID = document.getElementById("taskID"+cnt).value;
		getContent("", 'SaveTaskOrSubtaskAndGetId.action?operation=OTHER_FIELDS&fieldType=PROJECT&fieldValue='+strProjects+'&taskId='+taskID+'&type=MP_Task');
		
		document.getElementById("spanstrProjects"+cnt).innerHTML = strstrProjects;
	}

	function setPriority(cnt, t_st_Type) {
		var priority = document.getElementById("priority"+cnt).value;
		document.getElementById("priority"+cnt).style.display = 'none';
		document.getElementById("spanpriority"+cnt).style.display = 'inline';
		var strPriority = 'Low';
		if(priority=='1') {
			strPriority = 'Medium';
		} else if(priority=='2') {
			strPriority = 'High';
		}
		var taskID = document.getElementById("taskID"+cnt).value;
		getContent("", 'SaveTaskOrSubtaskAndGetId.action?operation=OTHER_FIELDS&fieldType=PRIORITY&fieldValue='+priority+'&taskId='+taskID+'&type=MP_Task');
		document.getElementById("spanpriority"+cnt).innerHTML = strPriority;
	}
	
	function setStartDate(cnt, t_st_Type) {
		var startDate = document.getElementById("startDate"+cnt).value;
		if(startDate!='') {
			document.getElementById("startDate"+cnt).style.display = 'none';
			document.getElementById("spanstartDate"+cnt).style.display = 'inline';
			document.getElementById("spanstartDate"+cnt).innerHTML = startDate;
			var taskID = document.getElementById("taskID"+cnt).value;
			getContent("", 'SaveTaskOrSubtaskAndGetId.action?operation=OTHER_FIELDS&fieldType=STARTDATE&fieldValue='+startDate+'&taskId='+taskID+'&type=MP_Task');
		}
	}
	
	function setDeadline(cnt, t_st_Type) {
		var deadline1 = document.getElementById("deadline1"+cnt).value;
		if(deadline1!='') {
			document.getElementById("deadline1"+cnt).style.display = 'none';
			document.getElementById("spandeadline1"+cnt).style.display = 'inline';
			document.getElementById("spandeadline1"+cnt).innerHTML = deadline1;
			var taskID = document.getElementById("taskID"+cnt).value;
			getContent("", 'SaveTaskOrSubtaskAndGetId.action?operation=OTHER_FIELDS&fieldType=DEADLINE&fieldValue='+deadline1+'&taskId='+taskID+'&type=MP_Task');
		}
	}
	
	function setIdealTime(cnt, t_st_Type) {
		var idealTime = document.getElementById("idealTime"+cnt).value;
		if(idealTime!='') {
			document.getElementById("idealTime"+cnt).style.display = 'none';
			document.getElementById("spanidealTime"+cnt).style.display = 'inline';
			document.getElementById("spanidealTime"+cnt).innerHTML = idealTime;
			var taskID = document.getElementById("taskID"+cnt).value;
			getContent("", 'SaveTaskOrSubtaskAndGetId.action?operation=OTHER_FIELDS&fieldType=IDEALTIME&fieldValue='+idealTime+'&taskId='+taskID+'&type=MP_Task');
		}
	}
	
	function updateFields(lblId, textId) {
		document.getElementById(textId).style.display = 'inline';
		document.getElementById(lblId).style.display = 'none';
		//document.getElementById("spantaskname"+cnt).innerHTML = taskName;
	}
	
</script>
	<% 
	//System.out.println("project"+request.getAttribute("sbMyProjectList"));
		UtilityFunctions uF = new UtilityFunctions(); 
		String proType = (String)request.getAttribute("proType");
		String proPage = (String)request.getAttribute("proPage");
		String strMinLimit1 = (String)request.getAttribute("minLimit");
		
		String taskCount = (String)request.getAttribute("taskCount");
	
		List<List<String>> proTaskList = (List<List<String>>) request.getAttribute("proTaskList");
	
		String tskCnt = (String)request.getAttribute("tskCnt");   
		String subtskCnt = (String)request.getAttribute("subtskCnt");
		
		String strTitle = (String)request.getAttribute(IConstants.TITLE); 
		String strUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
		String strEmpId = (String)session.getAttribute(IConstants.EMPID);
	%>
	
	<div class="col-lg-12 col-md-12" style="margin: 7px 0px 10px;">
		<%
			String sbData = (String) request.getAttribute("sbData");
			String strSearchJob = (String) request.getAttribute("strSearchJob");
		%>
		<div class="col-lg-3 col-md-3 col-sm-12 no-padding">
			<input type="text" id="strSearchJob" class="form-control" name="strSearchJob" placeholder="Search Tasks" value="<%=uF.showData(strSearchJob, "") %>"/>
			<input type="button" value="Search" class="btn btn-primary" onclick="getSearchTaskNameList();">
		</div>
		<script>
	       $(function(){
	    	   $("#strSearchJob" ).autocomplete({
					source: [ <%=uF.showData(sbData, "") %> ]
				});
	       });
	  	</script>
	  	<div class="col-lg-9 col-md-9 col-sm-12 no-padding">
	  		<%-- <% if(proType == null || proType.equals("") || proType.equals("null") || !proType.equalsIgnoreCase("C")) { %>
	  			<a href="javascript:void(0);" style="float: right;" onclick="addNewTask('T');">+ Add New Task</a>
	  		<% } %> --%>
	  	</div>
	</div>
	
 	<div class="col-lg-9 col-md-9 col-sm-12">     
	
		<div class="nav-tabs-custom">
			<ul class="nav nav-tabs">
				<li class="<%=((proType == null || proType.equals("") || proType.equals("null") || proType.equalsIgnoreCase("L")) ? "active" : "") %>"><a href="javascript:void(0)" onclick="getAllTaskNameList('MyTaskNameList', 'L');" data-toggle="tab">Working (<%=(String)request.getAttribute("wTSTCnt") %>)</a></li>
				<li class="<%=((proType != null && proType.equalsIgnoreCase("TR")) ? "active" : "") %>"><a href="javascript:void(0)" onclick="getAllTaskNameList('MyTaskNameList', 'TR');" data-toggle="tab">Requests (<%=(String)request.getAttribute("trTSTCnt") %>)</a></li>
				<%-- <li class="<%=((proType != null && proType.equalsIgnoreCase("MR")) ? "active" : "") %>"><a href="javascript:void(0)" onclick="getAllTaskNameList('MyTaskNameList', 'MR');" data-toggle="tab">My Requests (<%=(String)request.getAttribute("mrTSTCnt") %>)</a></li> --%>
				<li class="<%=((proType != null && proType.equalsIgnoreCase("C")) ? "active" : "") %>"><a href="javascript:void(0)" onclick="getAllTaskNameList('MyTaskNameList', 'C');" data-toggle="tab">Completed (<%=(String)request.getAttribute("cTSTCnt") %>)</a></li>
			</ul>
			<div class="tab-content box-body">
				<div class="active tab-pane">
				<input type="hidden" name="proType" id="proType" />
				<input type="hidden" name="strMyProjects" id="strMyProjects" value="<%=(String)request.getAttribute("sbMyProjectList") %>"/>	
					<%
					String taskId = (String)request.getAttribute("taskId");
					 %>
					<table id="myTable" class="table table-bordered" style="width: 100%; margin-top: 30px;">
						<thead style="background-color: #c7c6c6;">
							<tr>
								<th style="text-align: left;">Task Name</th>
								<th style="text-align: left;">% Complete</th>
								<th style="text-align: left;">Deadline</th>
								<th style="text-align: left;">Duration</th>
							</tr>
						</thead>
						<tbody>
						<%
						for(int i=0; proTaskList!=null && !proTaskList.isEmpty() && i<proTaskList.size(); i++) {
							List<String> innerList = proTaskList.get(i);
							//System.out.println("MTNL.jsp/379---innerList=="+innerList);
							if(taskId == null || taskId.equalsIgnoreCase("null") || taskId.trim().equals("")) {
								taskId = innerList.get(0);
							}
						%>
						<tr id=<%=i %>>
							<td><%=uF.showData(innerList.get(10), "") %> 
							<a href="javascript:void(0);" <% if(uF.parseToInt(taskId) == uF.parseToInt(innerList.get(0))) { %> class="activelink" <% } %> onclick="getTaskDetails('EmpViewProject', '<%=innerList.get(0) %>', event)"><%=uF.showData(innerList.get(1), "") %></a></td>
							<td style="width: 20%;"><div class="anaAttrib1"><span id="TcompletePercent_<%=innerList.get(5) %>_<%=innerList.get(0) %>"><%=innerList.get(14) %>%</span> 
								<% if(strUserType!=null && !strUserType.equals(IConstants.CUSTOMER)) { %>
								<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equals("L")) { %>
				                    <%-- <% if(uF.parseToInt(innerList.get(14)) == 0) { %> subtaskCnt --%>
				                    <%if(innerList.get(15) != null && innerList.get(15).equals("n")) { %>
				                   		<a href="javascript:void(0)" onclick="updateStatus(<%=innerList.get(0) %>, <%=innerList.get(5) %>, 'Tcomplete')"><img src="images1/icons/popup_arrow.gif" title="Update Completion Status" height="8px"/></a>
				                    <% } //} %>
			                    <% } %>
			                    <% } %>
								</div>
								<div id="Tcomplete_<%=innerList.get(5) %>_<%=innerList.get(0)%>" class="outbox">
									<div class="greenbox" style="width: <%=innerList.get(14) %>%;"></div>
								</div>
							</td>
							<td ><span title="<%=innerList.get(11)%> - <%=innerList.get(12)%>"><%=innerList.get(12)%></span></td>
							<td><%=innerList.get(13)+""+innerList.get(17)%></td>
						</tr>
						<% } %>
						 <tr>
<td colspan="4" style="text-align: left;">
<% if(proType == null || proType.equals("") || proType.equals("null") || !proType.equalsIgnoreCase("C")) { %>
<a href="javascript:void(0);" onclick="addNewTaskInList('T');">+ Add New Task</a>
<% } %>
</td>
<td style="display: none;"></td>
<td style="display: none;"></td>
<td style="display: none;"></td>
</tr>
					</tbody>
				</table>

					
                <% boolean activityRunningFlag = (Boolean) request.getAttribute("flag"); %>
	              	
	              	<%-- <%
	                	//System.out.println("taskId --->> " + taskId);
						for(int i=0; proTaskList!=null && !proTaskList.isEmpty() && i<proTaskList.size(); i++) {
							List<String> innerList = proTaskList.get(i);
							if(taskId == null || taskId.equalsIgnoreCase("null") || taskId.trim().equals("")) {
								taskId = innerList.get(0);
							}
			            %>

		                    <li class="item">
		                    	<span style="float: left; width: 20px; height: 20px; font-weight: bold; background-color: lightpink; text-align: center; font-size: 16px;" id="proLogoDiv">
				                    <% String taskLogo = "";
				                    	if(innerList.get(1) != null && innerList.get(1).toString().length()>0) {
				                    		taskLogo = innerList.get(1).toString().substring(0, 1);
					                    }
				                   	%>
				                   	<span><%=taskLogo %></span>
								</span>
		                    <div style="margin-left: 27px;"> <!-- class="product-info" -->
		                    	<span style="float: left; width: 100%; <%=(uF.parseToInt((String)innerList.get(2))==1) ? "background-image: url(&quot;images1/icons/new2.gif&quot;);":""%> background-repeat: no-repeat; background-position: right top; line-height: 14px;">
									<a href="javascript:void(0);" <% if(uF.parseToInt(taskId) == uF.parseToInt(innerList.get(0))) { %> class="activelink" <% } %> onclick="getTaskDetails('EmpViewProject', '<%=innerList.get(0) %>')"><%=innerList.get(1) %></a>
		                    	</span>
		                    	<% if(innerList.get(6) != null && !innerList.get(6).equals("")) { %>
		                    		<span style="float: left; width: 100%; color: gray; font-style: italic; line-height: 14px;">(<%=innerList.get(6) %>)</span>
		                    	<% } %>
								<span style="float: left; width: 100%; color: gray; line-height: 14px;"><%=innerList.get(3) %> </span>
								<span style="float: left; width: 100%; line-height: 14px;"> assigned by: <%=innerList.get(4) %> </span>
								<span style="float: left; width: 100%; line-height: 14px;"><%=(innerList.get(8)!=null && innerList.get(8).length() > 50) ? innerList.get(8).subSequence(0, 50) :innerList.get(8) %> 
								<%
								String sTTType = "T";
								if(uF.parseToInt(innerList.get(7))>0) { 
									sTTType = "ST";
								} %>
								<% if(innerList.get(8)!=null && innerList.get(8).length() > 50) { %>
								<a href="javascript:void(0);" onclick="updateTaskDescription1(<%=innerList.get(0) %>, '<%=i %>', 'taskDescription', '<%=sTTType %>', 'V')">...</a>
								<% } %>
								</span>
							</div>
		                    
	                    </li><!-- /.item -->
                    <% } %> --%>
                    
                    
                    <% //} %>
                    <input type="hidden" name="taskId" id="taskId" value="<%=taskId %>"/>
                    <%if(proTaskList == null || proTaskList.size() == 0) { %>
                    	<ul class="products-list product-list-in-box">
	                    	<li class="item">
								<div class="alert" style="background-color: #FCF8E3 !important; border-color: #FAEBCC; color: #8A6D3B !important; padding: 10px;">No tasks assigned to you.</div>
							</li>
						</ul>
					<% } %>
					<div>
						<input type="hidden" name="taskcount" id="taskcount" value="<%=(proTaskList != null && proTaskList.size() > 0) ? (proTaskList.size()-1) : "0" %>" />
					</div>
	                  
	                  <div class="box-footer text-center">
	                  <div style="text-align: center; float: left; width: 100%;">
						<% int inttaskCnt = uF.parseToInt(taskCount);
							int pageCnt = 0;
							int minLimit = 0;
							
							for(int i=1; i<=inttaskCnt; i++) {
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
						
						<% if(i > 1 && i < inttaskCnt) { %>
						<% if(pageCnt >= (uF.parseToInt((String)request.getAttribute("proPage"))-2) && pageCnt <= (uF.parseToInt((String)request.getAttribute("proPage"))+2)) { %>
							<span><a href="javascript:void(0);" onclick="loadMoreProjects('<%=pageCnt %>','<%=minLimit %>','<%=uF.showData((String)request.getAttribute("proType"), "L") %>');"
							<% if(((String)request.getAttribute("proPage") == null && pageCnt == 1) || uF.parseToInt((String)request.getAttribute("proPage")) == pageCnt) { %>
							style="color: black;"
							<% } %>
							><%=pageCnt %></a></span>
						<% } %>
						<% } %>
						
						<% if(i == inttaskCnt && inttaskCnt > 1) {
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
							<% if((uF.parseToInt((String)request.getAttribute("proPage"))+3) < inttaskCnt) { %>
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
						
					</div>
	                
				</div>
			</div>
		</div>
 	</div>
 	
	<div class="col-lg-3 col-md-3 col-sm-12">
		<div class="active tab-pane" id="subSubDivResult">
		</div>
	</div>
 	
 <script type="text/javascript" charset="utf-8">
 /* (document).ready */
	$(function() {
		//debugger;
		//var taskId = document.getElementById("taskId").value;
		//getTaskDetails('EmpViewProject', taskId);
		$('#myTable tr').each(function() {
			var $tds = $(this).find('td');
			var flag = false;
			$tds.each(
				function()
				{
					var x = $(this).find('a');
						if(typeof(x) === "undefined" || typeof(x[0]) === "undefined"){	}
						else {
								x[0].click();
								flag = true;
								return false;
							}
				}
				);
			if(flag == true)
			 return false;
		});
	
	});


		window.addEventListener('load', function(event) {
			var taskId = document.getElementById("taskId").value;
			getTaskDetails('EmpViewProject', taskId , event);
		    log.textContent = log.textContent + 'load\n';
		});

	function getSearchTaskNameList() {
			
		var proType = document.getElementById("proType").value;
		var taskSubtaskStatus = '';
		if(document.getElementById("divTaskStatus")) {
			if(proType != null && proType != '' && proType != 'null' && proType != 'L') {
				document.getElementById("divTaskStatus").style.display = 'none';
			} else {
				document.getElementById("divTaskStatus").style.display = 'block';
				taskSubtaskStatus = document.getElementById("taskSubtaskStatus").value;
			}
		}
		var assignedBy = document.getElementById("assignedBy").value;
		var strSearchJob = document.getElementById("strSearchJob").value;
		var recurrOrMiles = document.getElementById("recurrOrMiles").value;
		
		$("#subDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: 'MyTaskNameList.action?taskSubtaskStatus='+taskSubtaskStatus+'&assignedBy='+assignedBy+'&recurrOrMiles='+recurrOrMiles
					+'&proType='+proType+'&strSearchJob='+strSearchJob,
			success: function(result){
				$("#subDivResult").html(result);
	   		}
		});
	}
	
	
	function getTaskDetails(strAction, taskId , event) {
		//debugger;
		//alert("getTaskDetails");
		var proType = document.getElementById("proType").value;
		if(taskId==null || taskId=='null') {
			taskId = '<%=taskId %>';
		}
		$("#subSubDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			url: strAction+'.action?taskId='+taskId+'&proType='+proType,
			success: function(result) {
				$("#subSubDivResult").html(result);
	   		}
		});
		
		
		if(event.type != "load"){
			
		$('#myTable tr').each(function() {
			var $tds = $(this).find('td');
			
			$tds.each(
				function(){
					var x = $(this).find('a');
					if(typeof(x) === "undefined" || typeof(x[0]) === "undefined"){
						}
					else{
						if(x[0].className == 'activelink'){
							x[0].classList.toggle("activelink", false);
						}
					}
				}		
			);
		});
		
		$(event.target).attr('class','activelink');
		}
	}

</script> 
