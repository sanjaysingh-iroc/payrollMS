
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
	if(btnSubmit == null || btnSubmit.equalsIgnoreCase("null") || btnSubmit.equals("")) {
%>
	<%-- <script type="text/javascript" src="scripts/charts/jquery.min.js"> </script> --%>
	
	<link rel="stylesheet" href="js_bootstrap/datepicker/bootstrap-datepicker3.css">
	<script type="text/javascript" src="js_bootstrap/datepicker/bootstrap-datepicker.min.js"></script>
	<link type="text/css" href="js_bootstrap/timepicker/bootstrap-timepicker.min.css" />
	<script type="text/javascript" src="js_bootstrap/timepicker/bootstrap-timepicker.min.js"></script>
	
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

<script type="text/javascript" charset="utf-8">
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


function openFeedsForm(taskId, proId, proType) {
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$(".modal-title").html('Task Feeds');
	$.ajax({
		url : 'FeedsPopup.action?pageFrom=Task&taskId='+taskId+'&proId='+proId+'&pageType='+proType,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}


function updateTaskDescription1(strTaskId, cnt, divId, type, fromPage) {
	var strTitle = "Task";
	if(type == 'ST') {
		strTitle = "Sub Task";
	}
	var taskDescription = document.getElementById(divId+strTaskId+'_'+cnt).value;
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$(".modal-title").html(strTitle + ' Description');
	$.ajax({
		url : 'AddTaskDescription.action?proId='+strTaskId+'&divId='+divId+'&count='+cnt+'&taskDescription='+encodeURIComponent(taskDescription)+'&fromPage='+fromPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}


function updateTaskDescription(cnt, divId, type) {
	var strTitle = "Task";
	if(type == 'ST') {
		strTitle = "Sub Task";
	}
	var taskDescription = document.getElementById(divId+cnt).value;
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$(".modal-title").html(strTitle + ' Description');
	$.ajax({
		url : 'AddTaskDescription.action?divId='+divId+'&count='+cnt+'&taskDescription='+encodeURIComponent(taskDescription),
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}


function addTaskDescription(description, strTaskId, divId, cnt, fromPage) {
	if(fromPage == 'U') {
		document.getElementById(divId+strTaskId+'_'+cnt).value = description;
	} else {
		document.getElementById(divId+cnt).value = description;
	}
	$("#modalInfo").hide();
}


</script>

	<%  
		UtilityFunctions uF = new UtilityFunctions();
	
		String proType = (String)request.getAttribute("proType");
		String taskId = (String) request.getAttribute("taskId");
		//System.out.println("MyTasks taskId ===>> " + taskId);
		
		String strTitle = (String)request.getAttribute(IConstants.TITLE); 
		String strUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
		String strEmpId = (String)session.getAttribute(IConstants.EMPID);
	%>
	
	<section class="content">
          <div class="row">
            <div class="col-lg-12 col-md-12 col-sm-12">
            	<s:form name="frm_empproject_view" action="MyTasks" theme="simple">
            		<s:hidden name="proType" id="proType" />
            		<s:hidden name="taskId" id="taskId" />
			    	<s:hidden name="proPage" id="proPage" />
			    	<s:hidden name="minLimit" id="minLimit" />
			    	<s:hidden name="sortBy" id="sortBy" />

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
								<% if(proType ==null || proType.equals("null") || proType.equals("") || proType.equals("L")) { %>
									<div id="divTaskStatus" class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Tasks Status</p>
										<s:select theme="simple" name="taskSubtaskStatus" id="taskSubtaskStatus" headerKey="" headerValue="All Tasks" list="#{'1':'On-Track', '2':'Not Started', '3':'Pending'}"/>
									</div>
								<% } %>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Assigned By</p>
									<select name="assignedBy" id="assignedBy" style="float:left; margin-right: 10px;" >
										<option value="">All Assigner</option>
										<%=(String)request.getAttribute("sbAddedbyOption") %>
									</select>
								</div>
								
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Project Type</p>
									<s:select theme="simple" name="recurrOrMiles" id="recurrOrMiles" headerKey="" headerValue="All Project Type" list="#{'1':'Recurring', '2':'Milestone'}"/>
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
						<s:select theme="simple" name="sortBy1" id="sortBy1" cssStyle="width: 140px !important;" 
							list="#{'1':'Latest on Top', '2':'Oldest on Top', '3':'A-Z', '4':'Z-A'}" onchange="loadMoreProjects('1', '0');"/>
					</div>
	           </s:form>
                  
			</div>
		</div>
	
		<div class="row">
			<div class="active tab-pane" id="subDivResult" style="min-height: 600px;"></div>
		</div>
		 
</section>



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


<script type="text/javascript" charset="utf-8">
	
/* (document).ready */
	$(function() {
		//alert("on load .......");
		var proType = document.getElementById("proType").value;
		//alert("proType ===>> " + proType);
		getAllTaskNameList('MyTaskNameList', proType);
	});
	
	function getAllTaskNameList(strAction, proType) {
		
		var taskSubtaskStatus = '';
		if(document.getElementById("divTaskStatus")) {
			if(proType != null && proType != '' && proType != 'null' && proType != 'L') {
				document.getElementById("divTaskStatus").style.display = 'none';
			} else {
				taskSubtaskStatus = document.getElementById("taskSubtaskStatus").value;
				document.getElementById("divTaskStatus").style.display = 'block';
			}
		}
		//alert("taskSubtaskStatus ===>> " + taskSubtaskStatus);
		document.getElementById("proType").value = proType;
		var assignedBy = document.getElementById("assignedBy").value;
		var taskId = '<%=taskId %>';
		var recurrOrMiles = document.getElementById("recurrOrMiles").value;
		
		$("#subDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: strAction+'.action?taskSubtaskStatus='+taskSubtaskStatus+'&assignedBy='+assignedBy+'&recurrOrMiles='+recurrOrMiles
					+'&proType='+proType+'&taskId='+taskId,
			success: function(result){
				$("#subDivResult").html(result);
	   		}
		});
	}

	
	function submitForm(type) {
		var proType = document.getElementById("proType").value;
		var taskSubtaskStatus = '';
		if(document.getElementById("divTaskStatus")) {
			if(proType == null || proType == '' || proType == 'null' || proType != 'L') {
				taskSubtaskStatus = document.getElementById("taskSubtaskStatus").value;
			}
		}
		var assignedBy = document.getElementById("assignedBy").value;
		var recurrOrMiles = document.getElementById("recurrOrMiles").value;
		var paramValues = "";
		if(type != "" && type == '2') {
			paramValues = 'taskSubtaskStatus='+taskSubtaskStatus+'&assignedBy='+assignedBy+'&recurrOrMiles='+recurrOrMiles
			+'&proType='+proType+'&btnSubmit=Submit';
		}
    	var action = 'MyTasks.action?'+paramValues;
    	//alert("action=>"+action);
    	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$.ajax({
    		type : 'POST',
    		url: action,
    		success: function(result){
            	$("#divResult").html(result);
       		}
    	});
    }

	
function loadMoreProjects(proPage, minLimit, proType) {
		proType = document.getElementById("proType").value;
		var sortBy = document.getElementById("sortBy1").value;
		var taskSubtaskStatus = '';
		if(document.getElementById("divTaskStatus")) {
			if(proType == null || proType == '' || proType == 'null' || proType != 'L') {
				taskSubtaskStatus = document.getElementById("taskSubtaskStatus").value;
			}
		}
		var assignedBy = document.getElementById("assignedBy").value;
		var recurrOrMiles = document.getElementById("recurrOrMiles").value;
		
		var paramValues = "";
			paramValues = 'taskSubtaskStatus='+taskSubtaskStatus+'&assignedBy='+assignedBy+'&recurrOrMiles='+recurrOrMiles
			+'&proPage='+proPage+'&minLimit='+minLimit+'&proType='+proType+'&sortBy='+sortBy;
        
    	var action = 'MyTaskNameList.action?'+paramValues;
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
	
</script>


<script>
	//$("img.lazy").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10});
	$("img.lazy").lazyload({event : "sporty", threshold : 200,effect : "fadeIn",failure_limit : 10});

	$(window).bind("load", function() {
	    var timeout = setTimeout(function() { $("img.lazy").trigger("sporty") }, 1000);
	});  
</script>

</div>