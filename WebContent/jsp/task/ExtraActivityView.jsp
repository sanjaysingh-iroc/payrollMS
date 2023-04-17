<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<script>

	function sendMail() {

		var date = document.frm.task_date.value;
		window.location = 'GenerateTimeSheet.action?mailAction=sendMail&task_date='
				+ date;
	}
	 
	 function sendTimesheet1(empid, datefrom, dateto, downloadSubmit) {
	  	var date = document.frm.task_date.value;
		window.location = 'GenerateTimeSheet1.action?mailAction=sendMail&empid='+ empid+'&datefrom='+datefrom+'&dateto='+dateto+'&downloadSubmit='+downloadSubmit;	  
	}  
	
	function sendMonthlyTimesheet() {

		removeLoadingDiv('the_div');
		var dialogEdit = '#sendTimesheet';
		dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
		$(dialogEdit).dialog({
			autoOpen : false, 
			bgiframe : true,
			resizable : false,
			height : 'auto',
			width : 650,
			modal : true,
			title : 'Timesheets',
			open : function() {
				var xhr = $.ajax({
					url : "Timesheet.action?type=",
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
	
	
	/* function doSomething(selObj) {
		
	alert(selObl); 
			window.location = 'ExtraActivityView.action';
		
	} */
	function addExtraActivity(type) {

		removeLoadingDiv('the_div');
		var dialogEdit = '#addExtraActivity';
		dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
		$(dialogEdit).dialog({
			autoOpen : false,
			bgiframe : true,
			resizable : false,
			height : 'auto',
			width : 650,
			modal : true,
			title : 'Add Activity',
			open : function() {
				var xhr = $.ajax({
					url : "AddExtraActivityPopup.action?type=" + type,
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

	
	function addProjectActivity() {

		removeLoadingDiv('the_div');
		var dialogEdit = '#addProjectActivity';
		dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
		$(dialogEdit).dialog({
			autoOpen : false,
			bgiframe : true,
			resizable : false,
			height : 'auto',
			width : 650,
			modal : true,
			title : 'Add Activity',
			open : function() {
				var xhr = $.ajax({
					url : "AddProjectActivityPopup.action",
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
	
	
	function endTask(tid, pid, taskId) {
		
		var dialogEdit = '#endTask';
		var url1='EndTaskPopup.action?id='+tid+'&taskId='+taskId;
		url1+='&pro_id='+pid+'&fromPage=MyActivity';
		
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
	
	
	$(function() {
		$("#task_date").datepicker({
			dateFormat : 'dd/mm/yy'
		});
	});
	
	addLoadEvent(prepareInputsForHints);
	
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
</style>
<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="My Activities" name="title" />
</jsp:include>
<%
	UtilityFunctions uF = new UtilityFunctions();
	List<Integer> taskiddatelist = (List<Integer>) request.getAttribute("taskiddatelist");
	List<String> taskdatelist = (List<String>) request.getAttribute("taskdatelist");
%>
<div class="leftbox reportWidth">
	<%-- 	<div style="width: 100%;text-align:center"> <%=uF.showData((String)session.getAttribute("MESSAGE"), "") %></div> --%>

	<!-- <div class="filter_caption">Filter</div> -->
	<s:form action="ExtraActivityView" id="formID" name="frm" method="post"
		theme="simple">

		<div class="filter_div">
			<div class="filter_caption">Filter</div>
			Select Date:
			<s:textfield label="Select Task Date" id="task_date" name="task_date" cssStyle="width:100px"></s:textfield>

			<s:submit value="Search" cssClass="input_button"></s:submit>
		</div>
	</s:form>

	<div style="float: left; width: 100%; margin: 10px 0;">
		<%
			if ((Boolean) request.getAttribute("flag")) {
		%>
		<a class="add_lvl" href="javascript:void(0)"
			onclick="if(confirm('You are already working on one activity, would you like to finish that activity by starting the new activity?')){addExtraActivity('A')} ">Add
			New Activity</a> | <a class="add_lvl" href="javascript:void(0)"
			onclick="if(confirm('You are already working on one activity, would you like to finish that activity by starting the new activity?')){addExtraActivity('M')} ">Add
			New Manual Activity</a> |

		<%
			} else {
		%>
		<a class="add_lvl" href="javascript:void(0)"
			onclick="addExtraActivity('A')">Add New Activity</a> | <a
			class="add_lvl" href="javascript:void(0)"
			onclick="addExtraActivity('M')">Add New Manual Activity</a> |
			<!-- <a	class="add_lvl" href="javascript:void(0)"
			onclick="addProjectActivity()">Add New Project Activity</a> | -->
		<%
			}
		%>
		<%--  <%if((String)session.getAttribute("MSG")!=null && (String)session.getAttribute("MESSAGE")!=null) {%>
		<a class="add_lvl" href="javascript:void(0)" onclick="if(confirm('You are already working on one activity, would you like to finish that activity by starting the new activity?')){addExtraActivity('A')} " >Add New Activity</a> |
		<a class="add_lvl" href="javascript:void(0)" onclick="if(confirm('You are already working on one activity, would you like to finish that activity by starting the new activity?')){addExtraActivity('M')} " >Add New Manual Activity</a> |
		
		<%}else{ %>
		<a class="add_lvl" href="javascript:void(0)" onclick="addExtraActivity('A')" >Add New Activity</a> |
		<a class="add_lvl" href="javascript:void(0)" onclick="addExtraActivity('M')" >Add New Manual Activity</a> |
		  <%} %> --%>
		<!-- <a class="add_lvl" href="javascript:void(0)" onclick="" >Share a Doc</a> | -->
		<!-- 		<a class="" href="GenerateTimeSheet.action" >Send Days Report To Manager</a> -->
		<a href="javascript:void(0)" onclick="sendMail()">Send Daily Report</a> |
		<!-- <a href="javascript:void(0)" onclick="sendMonthlyTimesheet()">Send Monthly Timesheet</a> -->



	</div>

	<div class="clr"></div>


	<%-- <%
		List<Integer> datewiseindex = (List<Integer>) request.getAttribute("datewiseindex");
		Map<Integer, List<String>> datewisemap = (Map<Integer, List<String>>) request.getAttribute("datewisemap");
		if (datewiseindex != null && datewisemap != null && datewiseindex.size() != 0) {
	%>
	<center>
		<table cellpadding="0" cellspacing="0" class="tb_style">
			<tr>
				<th>Task No.</th>
				<th>Task Name</th>
				<th>Task Status</th>
				<th>Comment</th>
				<th>Start Time</th>
				<th>End Time</th>
			</tr>

			<%
				for (int i = 0; i < datewiseindex.size(); i++) {
						List<String> newInner = datewisemap.get(datewiseindex.get(i));
			%>
			<tr>
				<%
					for (int j = 0; j < newInner.size(); j++) {
				%>

				<td><%=newInner.get(j)%></td>
				<%
					}
				%>
			</tr>
			<%
				}
			%>
		</table>
		<br>
	</center>
	<%
		}
	%> --%>

	<%
		List alActivitiesList = (List)request.getAttribute("alActivitiesList");
		String totalHrs = (String)request.getAttribute("totalHrs");
	%>


	<form action="AddExtraActivity.action" method="post">

		<table class="tb_style" style="width: 50%">
			<tr>
				<th>Activities</th>
				<th>Time</th>
			</tr>
			<%
				int i = 0;
				for (i = 0; alActivitiesList != null && i < alActivitiesList.size(); i++) {
					List alInner = (List) alActivitiesList.get(i);
					if (alInner == null)
						alInner = new ArrayList();
			%>
			<tr>
				<td><%=alInner.get(0)%></td>
				<td><%=alInner.get(1)%></td>
			</tr>

			<% } %>
			
			<% if (i != 0) { %>
			<tr>
				<td><b>Total</b></td>
				<td><b><%=totalHrs%></b></td>
			</tr>
			<% } %>
			
			<% if (i == 0) { %>
			<tr>
				<td colspan="2"><div class="msg nodata" style="width:96%">
						<span>No activities found for the selected date.</span>
					</div></td>
			</tr>
			<% } %>
		</table>

		<% if (i == 0) { %>
		<div></div>
		<% } %>

		<input type="submit" class="input_button" value="Submit Your Report" />
	</form>

</div>
<div id="addExtraActivity"></div>
<div id="addProjectActivity"></div>
<div id="sendTimesheet"></div>
<div id="endTask"></div>

<%-- 	<%session.setAttribute("MESSAGE", null);%> --%>
<%-- 	<%session.setAttribute("MSG", null);%> --%>