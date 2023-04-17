
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@page import="java.util.*"%>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>


<g:compress>
<script> 
	
	jQuery(document).ready(function() {
		//	jQuery("#frmClockEntries").validationEngine();
		  jQuery(".content2").hide();
		  //toggle the componenet with class msg_body
		  jQuery(".heading_dash").click(function() {
		    jQuery(this).next(".content2").slideToggle(500);
			$(this).toggleClass("close_div"); 
		  });
		});
	

</script>

</g:compress>

		
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
		String proType = (String)request.getAttribute("proType");
		//System.out.println("proType ---->> " + proType);
		String strTitle = (String)request.getAttribute(IConstants.TITLE); 
	%>

<%
	Map<String, List<List<String>>> hmTasks = (Map<String, List<List<String>>>)request.getAttribute("hmTasks");
	Map<String, List<List<String>>> hmSubTasks = (Map<String, List<List<String>>>) request.getAttribute("hmSubTasks");
	if(hmTasks == null) hmTasks = new HashMap<String, List<List<String>>>();
	Map hmProject = (java.util.Map)request.getAttribute("hmProject");
	if(hmProject == null) hmProject = new HashMap();
	Map<String, String> hmPMilestoneSize = (Map<String, String>)request.getAttribute("hmPMilestoneSize");
	Map<String, String> hmPDocumentCounter = (Map<String, String>)request.getAttribute("hmPDocumentCounter");
	UtilityFunctions uF = new UtilityFunctions();
	String strUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
%>

		<%
			Set setProjectMap = hmProject.keySet();
			Iterator it = setProjectMap.iterator();
			
			while(it.hasNext()) {
				String strProjectId = (String)it.next();
				List alProjects = (List)hmProject.get(strProjectId);
				if(alProjects == null) alProjects = new ArrayList();
				
				List<List<String>> alTasks = (List<List<String>>) hmTasks.get(strProjectId);
					if(alTasks == null) alTasks = new ArrayList<List<String>>();
					
					%>
					
					<li class="post">
					<div style="float: left; <%=(uF.parseToInt((String)alProjects.get(11))==1)?"background-image: url(&quot;images1/icons/new2.gif&quot;);":""%> background-repeat: no-repeat; background-position: right top;">
					<p>
						<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equals("L")) { %>
							<span id="proCheckboxDiv" style="float: left; margin-right: 5px;">
								<input type="checkbox" value="<%=strProjectId%>" name="approvePr" onclick="checkAllProjectCheckedUnchecked();"/>
							</span>
						<% } %>
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
	                    <strong> <%=uF.formatIntoComma(alTasks.size()) %></strong> Tasks & <strong><%=uF.parseToInt(hmPMilestoneSize.get(strProjectId)) > 0 ? hmPMilestoneSize.get(strProjectId) : "No"%></strong> Milestones&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	                    <strong> <%=uF.parseToInt(hmPDocumentCounter.get(strProjectId)) > 0 ? hmPDocumentCounter.get(strProjectId) : "No"%></strong> Documents&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
  					</p>
  
                    </div>
                    <div style="float:right;">
                    <% if(proType != null && (proType.equals("C") || proType.equals("B"))) { %>
	                    <%if(strUserType!=null && (strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.MANAGER))) { %>
							<div id="unlock_div_<%=alProjects.get(0)%>"><a href="javascript:void(0)" onclick="((confirm('Are you sure, you want to unlock this project?'))?getContent('unlock_div_<%=alProjects.get(0)%>','UpdateStatus.action?pro_id=<%=alProjects.get(0)%>&status=n'):'')">Click to unlock</a></div>
						<% } %>
					<% } %>
                    <a class="viewdollaricon" href="javascript:void(0)" onclick="viewCostSummary(<%=strProjectId %>)" title="View Cost Summary">View Cost Summary</a>
                    <a class="viewdocticon" href="javascript:void(0)" onclick="viewDocuments(<%=strProjectId %>)" title="View Project Documents">View Project Documents</a>
                    <a class="viewgantticon" href="javascript:void(0)" onclick="viewSummary(<%=strProjectId %>)" title="View Summary">View Summary</a>
                    <%-- <a href="javascript:void(0)" class="edit_lvl" onclick="editProject(<%=strProjectId%>)" title="Edit Project">Edit</a> --%>
                    <% if(proType == null || proType.equals("") || proType.equals("null") || proType.equals("L")) { %>
	                    <a href="PreAddNewProject1.action?operation=E&pro_id=<%=strProjectId %>&step=0" class="edit_lvl" title="Edit Project">Edit</a>
	                   	<% if(strUserType!=null && (strUserType.equalsIgnoreCase(IConstants.ADMIN)) || strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.CEO)) { %>
	                   		<a href="ViewAllProjects.action?operation=D&ID=<%=strProjectId %>" class="del" title="Delete Project"  onclick="return confirm('Are you sure, you wish to delete this project?')"> - </a>
	                   	<% } %>
                   <% } %>
                    
                    </div>
                    <div class="clr"></div>
					<ul>		
					<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equals("L")) { %>
					<li class="addnew"><a href="javascript:void(0)" onclick="addActivity(<%=strProjectId %>)">+ Add New Task</a> <%-- | <a href="javascript:void(0)" onclick="addMilestone(<%=strProjectId%>)">+ Add New Milestone</a> --%> </li>
					<% } %>
					<li class="desgn"> 
					
					<p id="AllTaskP<%=strProjectId %>" class="past heading_dash" style="text-align:left; padding-left:35px;" onclick="hideShowDiv('<%=strProjectId %>');" >Task List (click to expand) <span style="padding-left:10%">No of tasks: <%=uF.formatIntoComma(alTasks.size()) %></span></p>
					<input type="hidden" name="divStatus" id="divStatus<%=strProjectId %>" value="0"/>
					<div id="taskDiv<%=strProjectId %>" style="display: none;">
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
                  <% if(proType == null || proType.equals("") || proType.equals("null") || proType.equals("L")) { %>  
	                  <div style="float:right">
	                     <%if(alInner.get(10) != null && !alInner.get(10).equals("n") && percent >= 100) { %>
	                     <div class="cnfrmd"></div>  
	                     <%}else if(alInner.get(10) != null && alInner.get(10).equals("n") && percent >= 100) { %>
		                     <% if(alSubTasks == null || alSubTasks.isEmpty() || alSubTasks.size() == 0) { %>
		                     	<a href="javascript:void(0)" onclick="reAssign(<%=strTaskId%>)" title="Click to re-assign"><div class="completed"></div></a>
		                     <% } %>
	                     <%} else if(alInner.get(11) != null && alInner.get(11).equals("New Task")) { %>
	                     <div></div>
	                     <%} else { %>
	                     <div class="nt_cnfrmd"></div>
	                     <%} %>
	                     
	                    
	                    <%if(uF.parseToDouble(alInner.get(12))>0){ %>
	                    	<a href="javascript:void(0)" class="del" title="Delete Task" onclick="alert('You can not delete this task as user has already booked the time against this task.')"> - </a>
	                    <% } else { %>
	                    <% if(alSubTasks != null && !alSubTasks.isEmpty() && alSubTasks.size()>0) { %>
	                    	<a href="javascript:void(0)" class="del" title="Delete Task" onclick="alert('Please first delete sub task of this task then delete this task.')"> - </a>
	                    <% } else { %>
	                    	<a href="AddNewActivity.action?operation=D&task_id=<%=alInner.get(0)%>" class="del" title="Delete Task" onclick="return confirm('Are you sure, you wish to delete this Task?')"> - </a>
	                    <% } %>
	                    <% } %> 
	                    
						<%-- <a href="AddNewActivityPopup.action?operation=E&task_id=<%=alTasks.get(d)%>&pro_id=<%=strProjectId%>" class="edit_lvl"  title="Edit Task" 
	                    onclick="return hs.htmlExpand(this, { objectType: 'ajax'})">Edit</a>  --%>
	                 	<a href="javascript:void(0)" class="edit_lvl" onclick="editActivity(<%=alInner.get(0)%>,<%=strProjectId%>);" title="Edit Task">Edit</a>
	                 </div> 
						<%if(percent>=100 && alInner.get(10) != null && alInner.get(10).equals("n")) { %>
						<input type="checkbox" value="<%=strTaskId%>" name="cb" />
						<%} %>
					<% } %>
					
					<%=alInner.get(1) %>
					
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
	                    <%-- <a class="poplight" rel="popup_name<%=alTasks.get(d)%>" href="#?w=600"><img src="images1/icons/popup_arrow.gif" title="Update Completion Status" height="8px"/></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; --%>
	                    <% if(proType == null || proType.equals("") || proType.equals("null") || proType.equals("L")) { %>
		                    <% if(alSubTasks == null || alSubTasks.isEmpty() || alSubTasks.size() == 0) { %>
		                   		<a href="javascript:void(0)" onclick="updateStatus(<%=alInner.get(0)%>,<%=strProjectId %>)"><img src="images1/icons/popup_arrow.gif" title="Update Completion Status" height="8px"/></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		                    <% } %>
	                    <% } %>
                   </p>
                   
					</li>
				
					
				<%if(uF.parseToDouble(alInner.get(12))>0) { %>
					<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equals("L")) { %>
						<li class="addnew">
						<a href="javascript:void(0)" onclick="alert('You can not add sub task as user has already booked the time against this task.')">+ Add New Sub Task</a>
					<% } %>
				<% } else {%>
					<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equals("L")) { %>
						<li class="addnew"><a href="javascript:void(0)" onclick="addSubTask(<%=strProjectId %>, <%=strTaskId %>);">+ Add New Sub Task</a></li>
					<% } %>
				<% } %>
				<% if(alSubTasks != null) {
					for(int j=0; alSubTasks != null && j<alSubTasks.size(); j++) {
						List<String> innerList = alSubTasks.get(j);
						String strSubTaskId = innerList.get(0);
						String subPer = innerList.get(9); 
                        double subPercent = uF.parseToDouble(subPer);
				%>
					
				<li style="border-bottom: 1px solid #CCCCCC; margin-left: 100px;">
                  <% if(proType == null || proType.equals("") || proType.equals("null") || proType.equals("L")) { %>  
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
						<%if(subPercent>=100 && innerList.get(10) != null && innerList.get(10).equals("n")) { %>
							<input type="checkbox" value="<%=strSubTaskId %>" name="cb" />
						<% } %>
						<%=innerList.get(1) %>
					<% } %>
					<%
						//String strColour = null;
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
	                    <% if(proType == null || proType.equals("") || proType.equals("null") || proType.equals("L")) { %>
	                    	<a href="javascript:void(0)" onclick="updateStatus(<%=innerList.get(0)%>)"><img src="images1/icons/popup_arrow.gif" title="Update Completion Status" height="8px"/></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	                    <% } %>
                   </p>
                   
					</li>
					
					<% } } %>
					
				<% } %>
							</ul>
							</div>
						</li>
					</ul>
                 </li> 
		<% } %>
		
		<% if(hmProject != null && hmProject.size()==10) { %>
			<div id="moreProject_<%=(String)request.getAttribute("strDivCount") %>" style="text-align: center;"> <a href="javascript:void(0)" onclick="loadMoreProjects('<%=(String)request.getAttribute("strDivCount") %>','<%=(String)request.getAttribute("strLimit") %>','<%=(String)request.getAttribute("proType") %>');">Load more ... </a> </div>
		<% } %>
		

