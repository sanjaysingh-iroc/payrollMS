<%@page import="com.konnect.jpms.util.IConstants"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>

<%@taglib uri="/struts-tags" prefix="s"%>

<script>

	function addProject() {
		var dialogEdit = '#addproject';
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
	
	function viewDocuments(id) {

		var dialogEdit = '#viewdocuments';
		$(dialogEdit).dialog({
			autoOpen : false,
			bgiframe : true,
			resizable : false,
			height : 500,
			width : 500,
			modal : true,
			title : 'Project Documents',
			open : function() {
				var xhr = $.ajax({
					url : "ProjectDocumentView.action?pro_id="+id,
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

		var dialogEdit = '#view';

		$(dialogEdit).dialog({
			autoOpen : false,
			bgiframe : true,
			resizable : false,
			height : 200,
			width : 400,
			modal : true,
			title : 'Comment',
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
	
jQuery(document).ready(function() {
		
		//	jQuery("#frmClockEntries").validationEngine();
			
		  jQuery(".content1").hide();
		  //toggle the componenet with class msg_body
		  jQuery(".heading_dash").click(function()
		  {
		    jQuery(this).next(".content1").slideToggle(500);
			$(this).toggleClass("close_div"); 
		  });
		});
	
	
</script>


<style>
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

<link rel="stylesheet" type="text/css" href="css/select/jquery.multiselect.css" />
<script type="text/javascript" src="scripts/select/jquery.multiselect.js"></script>
<script type="text/javascript">
$(function(){
	$("#f_service").multiselect();
	$("#pro_id").multiselect();
	$("#managerId").multiselect();
	$("#client").multiselect();
	$("#skill").multiselect();
});    
</script> 

 <%String strTitle = (String)request.getAttribute(IConstants.TITLE); %>
<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="<%=strTitle%>" name="title" />
</jsp:include>
<%
	List<Integer> projectidlist = (List<Integer>) request.getAttribute("projectidlist");
	List<String> projectlist = (List<String>) request.getAttribute("projectlist");
	List<Integer> index = (List<Integer>) request.getAttribute("index");
	Map<Integer, List<String>> al = (Map<Integer, List<String>>) request.getAttribute("al");
	Map<Integer, List<List<String>>> activityDetailMap = (Map<Integer, List<List<String>>>) request.getAttribute("activityDetailMap");
%>
<div class="leftbox reportWidth">
	<!-- <div class="pagetitle" align="center" style="margin: 0px;">Approved
		Project Details</div>
	<br /> -->
	<s:form action="ViewBlockedProject" name="frm" method="post" theme="simple">


		<div class="filter_div">
			<div class="filter_caption">Filter</div>
			<%-- <s:select label="Select Client" name="client" headerKey="0"
							listKey="clientId" headerValue="Select Client" listValue="clientName"
							list="clientList" key="" required="true" onchange="document.frm.submit()"/> --%>


			<div
				style="float: left; margin-top: 8px; margin-left: 10px; width: auto;">
				<p style="padding-left: 5px;">Service</p>
				<s:select name="f_service" id="f_service" list="serviceList"
					listKey="serviceId" listValue="serviceName" multiple="true"></s:select>
			</div>

			<%-- <s:select name="pro_id" listKey="projectID" headerKey="" 
						headerValue="Select Project" listValue="projectName" list="projectdetailslist" 
						key="" onchange="SendProId(this.value);"/> --%>

			<div
				style="float: left; margin-top: 10px; margin-left: 10px; width: auto;">
				<p style="padding-left: 5px;">Project</p>
				<s:select name="pro_id" id="pro_id" listKey="projectID"
					listValue="projectName" list="projectdetailslist" key=""
					multiple="true" />
			</div>

			<div
				style="float: left; margin-top: 10px; margin-left: 10px; width: auto;">
				<p style="padding-left: 5px;">Manager</p>
				<s:select theme="simple" name="managerId" id="managerId"
					listKey="employeeId" listValue="employeeName" list="managerList"
					key="" multiple="true" />
			</div>

			<div
				style="float: left; margin-top: 10px; margin-left: 10px; width: auto;">
				<p style="padding-left: 5px;">Client</p>
				<s:select label="Select Client" name="client" id="client"
					listKey="clientId" listValue="clientName" list="clientList" key=""
					multiple="true" />
			</div>

			<div
				style="float: left; margin-top: 10px; margin-left: 10px; width: auto;">
				<p style="padding-left: 5px;">Skill</p>
				<s:select theme="simple" label="Select Skill" name="skill"
					id="skill" listKey="skillsId" listValue="skillsName"
					list="skillList" key="" multiple="true" />
			</div>
			<div
				style="float: left; margin-top: 10px; margin-left: 10px; width: auto;">
				<p style="padding-left: 5px;">&nbsp;</p>
				<s:submit value="Submit" cssClass="input_button"
					cssStyle="margin:0px" />
			</div>

		</div>


		<div style="float: left; margin: 10px 0px; width: 100%">
			<%-- <div style="float: left">
				<b>Select Project :</b> <select name="pro_id" id="pro_id"
					onchange="document.frm_adminproject_view.submit();">
					<option value="0">Select Project</option>
					<%
						for (int i = 0; i < projectidlist.size(); i++) {
					%>
					<option value="<%=projectidlist.get(i)%>" selected="selected"><%=projectlist.get(i)%></option>
					<%
						}
					%>
				</select>
			</div> --%>
			<!-- <div style="float: right; width: auto">
				<table>
					<tr>
						<td><a href="ViewAllProjects.action">View All Projects</a>
						</td>
						<td>|</td>
						<td><a href="javascript:void(0)" onclick="addProject()">+
								Add New Project</a>
						</td>
						<td>|</td>
						<td><a href="ApproveExtraActivity.action"> View Extra
								Activity</a>
						</td>
						<td>|</td>
						<td><a href="ViewApprovedProject.action"> View Approved
								Project</a>
						</td>
					</tr>
				</table>
			</div> -->
		</div>
		<%
			if (index != null && al != null && index.size() != 0
					&& al.size() != 0) {
		%>
		<br>
		<table cellpadding="0" cellspacing="0" class="tb_style">
			<tr>
				<th>Project Name</th>
				<th>Activity</th>
				<th>Priority</th>
				<th>Attachments</th>
				<th>Service</th>
				<th>Project Lead</th>
<!-- 				<th>Task Status</th> -->
				<th>Comment</th>
				<th>DeadLine</th>
				<th>Estimated Hrs</th>
				<th>Actual Hrs</th>
				<%
					if ((Integer) request.getAttribute("pro_id") != 0) {
				%>
				<th>Completed Work</th>
				<th>Time(Start/End)</th>
				<%
					} else {
				%>
				<th>Work Status</th>
				<th>Project Summary</th>
				<%
					}
				%>
			</tr>
			<%
				for (int i = 0; i < index.size(); i++) {
						List<String> reportList1 = al.get(index.get(i));
			%>
			<tr>
				<%
					for (int j = 0; j < reportList1.size(); j++) {
				%>
				<td valign="top"><%=reportList1.get(j)%></td>
				<%
					}
				%>
			</tr>
			<%
				List<List<String>> oIn = activityDetailMap
								.get(index.get(i));
						if (oIn != null) {
							for (int a = 0; a < oIn.size(); a++) {
								List<String> outInner = oIn.get(a);
			%>
			<tr>
				<%
					for (int x = 0; x < outInner.size(); x++) {
				%>
				<td valign="top"><%=outInner.get(x)%></td>
				<%
					}
				%>
			</tr>
			<%
				}
						}
					}
			%>
		</table>
		<%
			}
		%>
	</s:form>



	
<script type="text/javascript">

hs.graphicsDir = '<%=request.getContextPath()%>/images1/highslide/graphics/';
hs.outlineType = 'rounded-white';
hs.wrapperClassName = 'draggable-header';

</script>




<%

Map hmTasks = (java.util.Map)request.getAttribute("hmTasks");
Map hmProject = (java.util.Map)request.getAttribute("hmProject");




if(hmProject!=null && hmProject.size()!=0)
{
%>





<!-- <div style="float:left; margin:10px 0px 0px 0px"> <a href="AddLevel.action"  class="add_lvl" onclick="return hs.htmlExpand(this, { objectType: 'ajax' })">Add Project</a></div> -->

<!-- <div style="float:left; margin:10px 0px 0px 0px"> <a href="javascript:void(0)"  class="add_lvl" onclick="addProject()">Add Project</a></div> -->


  
<div class="clr"></div>
<div style="margin:10px 0px 0px 0px ;float:left; width:100%">
         <ul class="level_list">

		
		<% 
			Set setProjectMap = hmProject.keySet();
			Iterator it = setProjectMap.iterator();
			
			while(it.hasNext()){
				String strProjectId = (String)it.next();
				List alProjects = (List)hmProject.get(strProjectId);
				if(alProjects==null)alProjects=new ArrayList();
				
					
					List alTasks = (List)hmTasks.get(strProjectId);
					if(alTasks==null)alTasks=new ArrayList();
					%>
					
					<li>
<%-- 					<input type="checkbox" value="<%=strProjectId%>" name="approvePr" /> --%>
					<div style="float:left">
					<p>
	                    Project Name:<strong> <%=alProjects.get(1)%></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	                    Priority:<strong> <%=alProjects.get(2)%></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	                    Cost center:<strong> <%=alProjects.get(3)%></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	                    Work Status:<strong> <%=alProjects.get(8)%></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	                    Client:<strong> <%=alProjects.get(9)%></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    </p>
                    <p style="margin:0px 0px 0px 50px">   
	                    Deadline:<strong> <%=alProjects.get(4)%></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	                    Ideal Time:<strong> <%=alProjects.get(5)%></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	                    Actual Time:<strong> <%=alProjects.get(6)%></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    </p>
                    </div>
					<div style="float:right">
					<div id="unlock_div_<%=alProjects.get(0)%>"><a href="javascript:void(0)" onclick="((confirm('Are you sure you want to unlock this project'))?getContent('unlock_div_<%=alProjects.get(0)%>','UpdateStatus.action?pro_id=<%=alProjects.get(0)%>&status=n'):'')">Click to unlock</a></div>
                    <a class="viewdocticon" href="javascript:void(0)" onclick="viewDocuments(<%=strProjectId%>)" title="View Project Documents">View Project Documents</a>
                    <a class="viewgantticon" href="javascript:void(0)" onclick="viewSummary(<%=strProjectId%>)" title="View Summary">View Summary</a>
                    </div>
                    <div class="clr"></div>
                    
<%--                     <a href="ViewAllProjects.action?operation=D&ID=<%=strProjectId%>" class="del" title="Delete Project"  onclick="return confirm('Are you sure you wish to delete this project?')"> - </a>  --%>
<%--                     <a href="javascript:void(0)" class="edit_lvl" onclick="editProject(<%=strProjectId%>)" title="Edit Project">Edit</a>  --%>
                    
					<ul>		
					<%-- <li class="addnew desgn"><a href="javascript:void(0)" onclick="addActivity(<%=strProjectId%>)">+ Add New Task</a></li> --%>
					<li class="desgn">
					<p class="past heading_dash" style="text-align:left;padding-left:35px;">Task List ( click to expand )</p>
					  <div class="content1">
						<ul>
					<%
						for(int d=0; d<alTasks.size(); d+=8) {
						String strTaskId = (String)alTasks.get(d);
					%>
                    <li>
<%--                     <a href="AddNewActivity.action?operation=D&task_id=<%=alTasks.get(d)%>" class="del" title="Delete Task" onclick="return confirm('Are you sure you wish to delete this Task?')"> - </a> --%>
<%-- 					<a href="AddNewActivityPopup.action?operation=E&task_id=<%=alTasks.get(d)%>&pro_id=<%=strProjectId%>" class="edit_lvl"  title="Edit Task"  --%>
<!--                     onclick="return hs.htmlExpand(this, { objectType: 'ajax'})">Edit</a>  -->
<%--                 <a href="javascript:void(0)" class="edit_lvl" onclick="editActivity(<%=alTasks.get(d)%>)" title="Edit Task">Edit</a>  --%>

					<p>
	                    Task Name: <strong><%=alTasks.get(d+1)%></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	                    Priority: <strong><%=alTasks.get(d+2)%></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	                    Assigned To: <strong><%=alTasks.get(d+3)%></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    </p>
                    <p style="margin:0px 0px 0px 50px">   
	                    Deadline: <strong><%=alTasks.get(d+4)%></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	                    Estimated Time: <strong><%=alTasks.get(d+5)%></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	                    Worked: <strong><%=alTasks.get(d+6)%></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	                    Completion Status: <strong><%=alTasks.get(d+7)%> %</strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    </p>                   
					</li>
				<%
					}
					
				%>	
							</ul>
							</div>
						</li>
					</ul>
                 </li> 
		<%
			}
		%>
		 
		 </ul>
         
     </div>	
 <%}else{ %>
 <div class="msg nodata"><span>There are no projects available for the current selection.</span></div> 
 <%} %>
	</div>

<div id="viewsummary"></div>
<div id="addproject"></div>
<div id="view"></div>
<div id="viewdocuments"></div>