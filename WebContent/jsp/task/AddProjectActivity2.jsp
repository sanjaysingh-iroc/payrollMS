<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
 <%
 
 UtilityFunctions uF =  new UtilityFunctions();
 String strUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
 String strTaskList = (String)request.getAttribute("strTaskList");
 String strClientList = (String)request.getAttribute("strClientList");
 String strProjectList = (String)request.getAttribute("strProjectList");

 Map hmProjects = (Map)request.getAttribute("hmProjects");
 Map hmProjectTasks = (Map)request.getAttribute("hmProjectTasks");

 Map hmClientMap = (Map)request.getAttribute("hmClientMap");
 Map hmProjectMap = (Map)request.getAttribute("hmProjectMap");


 List alDates = (List)request.getAttribute("alDates");

 Map hmLeaves = (Map)request.getAttribute("hmLeaves");
 Map hmWeekendMap = (Map)request.getAttribute("hmWeekendMap");
 Map hmHolidayDates = (Map)request.getAttribute("hmHolidayDates");
 Map hmLeavesColour = (Map)request.getAttribute("hmLeavesColour");
 String strWLocationId = (String)request.getAttribute("strWLocationId");
 
 %>
 
<script>


function submitForm(){
		
	var isHoliday = false;
	var isWeekend = false;
	var x=document.getElementsByName("holiday");
	var y=document.getElementsByName("weekend");
	
	for (var i=0;i<x.length;i++){
	  	if(x[i].value=='H' && !isHoliday){
	  		isHoliday = true;
	  		if(confirm('Are you sure you want to enter the time sheet on holidays?')){
	  			return true;
	  		}else{
	  			return false;
	  		}
	  	}
	}
	for (var i=0;i<y.length;i++){
	  	if(y[i].value=='WH' && !isWeekend && !isHoliday){
	  		isWeekend = true;
	  		if(confirm('Are you sure you want to enter the time sheet on weekends?')){
	  			return true;
	  		}else{
	  			return false;
	  		}
	  	}
	}
	
	
}


function callDatePicker(){
	$("input[name=strDate]").datepicker({dateFormat: 'dd/mm/yy'});
}
	$(function() {
		$("#idStdTimeStart").timepicker({});
		$("#idStdTimeEnd").timepicker({});
		$("#idStdDateStart").datepicker({
			dateFormat : 'dd/mm/yy'
		});
		$("#idStdDateEnd").datepicker({
			dateFormat : 'dd/mm/yy'
		});
		$("input[name=strDate]").datepicker({dateFormat: 'dd/mm/yy'});
		
	});


	
		
	
	
	jQuery(document).ready(function(){
        // binds form submission and fields to the validation engine
        jQuery("#formID1").validationEngine();
        jQuery("#formID2").validationEngine();
    });
	
	
	function removeTask(divElement, removeId, taskId) {
		if(confirm('Are you sure you want to delete this entry?')){
			getContent('divElement', 'AddProjectActivity1.action?D=D&strTaskId='+taskId);
			var remove_elem = removeId;
			var row_skill = document.getElementById(remove_elem);
			document.getElementById(divElement).removeChild(row_skill);
		}
	}
	
	var cnt=<%=alDates.size()%>;
	function addTask(divElement) {
		
		var divE = document.getElementById(divElement).firstChild;
		var strDate = '';
		var strTime = '';
		
		if(divE.hasChildNodes()){
			var divE1 = divE.childNodes[0];
			var divE2 = divE1.childNodes[1];
			strDate = divE2.value;

			divE1 = divE.childNodes[4];
			divE2 = divE1.childNodes[0];
			strTime = divE2.value;
		}
		
		cnt++;
		var divTag = document.createElement("div");
	    divTag.id = "row_task_"+cnt;
	    divTag.setAttribute("style", "float:left;width:1100px;padding:2px;");
		divTag.innerHTML = 
			"<div style=\"float:left;width:75px;\"><input type=\"hidden\" name=\"taskId\" value=\"0\" ><input type=\"text\" style=\"width:62px\" name=\"strDate\" value=\""+strDate+"\"></div>"+
			"<div style=\"float:left;width:220px;\">"+
				"<select name=\"strClient\" class=\"validateRequired\" onchange=\"getContent('myProject__"+cnt+"','GetProjectClientTask22.action?client_id='+this.value+'&count="+cnt+"')\">"+
					"<%=strClientList%>"+
				"</select>"+
			"</div>"+
			"<div style=\"float:left;width:220px;\" id='myProject__"+cnt+"'>"+
			"<select name=\"strProject\" class=\"validateRequired\" onchange=\"getContent('myTask__"+cnt+"','GetProjectClientTask22.action?project_id='+this.value)\">"+
				"<%=strProjectList%>"+
			"</select>"+
			"</div>"+
			"<div style=\"float:left;width:220px;\" id='myTask__"+cnt+"'>"+
			"<select name=\"strTask\" class=\"validateRequired\" >"+
				"<%=strTaskList%>"+
			"</select>"+ 	
			"</div>"+
			"<div style=\"float:left;width:150px;\"><input type=\"text\" style=\"width:62px\" name=\"strTime\" value=\""+strTime+"\"></div>"+
			"<div style=\"float:left;width:100px;\"><a href=\"javascript:void(0)\" onclick=\"addTask('"+divElement+"_"+cnt+"')\">Add New Task</a></div>"+
			"<div style=\"float:left;width:100px;\"><a href=\"javascript:void(0)\" onclick=\"removeTask('"+divElement+"','row_task_"+cnt+"', 0)\">Remove Task</a></div>"+
	"";
	    document.getElementById(divElement).appendChild(divTag);
	    
	    callDatePicker();
	}
	
	
	
	
	
</script>


<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Timesheet" name="title" />
</jsp:include>


<div class="leftbox reportWidth">

<div style="float:right;margin-left:10px">
<s:form action="AddProjectActivity1" theme="simple">
<%if(request.getParameter("frmDate")!=null){ %>
<input type="hidden" name="strEmpId" value="<%=request.getParameter("strEmpId")%>"/>
<input type="hidden" name="frmDate" value="<%=request.getParameter("frmDate")%>"/>
<input type="hidden" name="toDate" value="<%=request.getParameter("toDate")%>"/>
<input type="hidden" name="strProject" value="<%=request.getParameter("strProject")%>"/>
<input type="hidden" name="strActivity" value="<%=request.getParameter("strActivity")%>"/>
<%} %>
<input type="submit" value="Projectwise" style="margin:0px;" class="input_button"/>
</s:form>
</div>


<a class="xls" href="GenerateTimeSheet1.action?mailAction=sendMail&empid=<%=request.getAttribute("empid")%>&datefrom=<%=request.getAttribute("datefrom")%>&dateto=<%=request.getAttribute("dateto")%>&downloadSubmit=0">Download</a>

	<s:form id="formID1" action="AddProjectActivity2" cssClass="formcss"
		method="post" theme="simple" name="frmTimesheet">

		<s:hidden name="emp_id" />
		<s:hidden name="strEmpId" />
		<table>

			<tr>
				<td>Select PayCycle</td>
				<td colspan="3"><s:select name="strPaycycle" listKey="paycycleId"
						listValue="paycycleName" headerValue="Select Client"
						list="paycycleList" key=""
						onchange="document.frmTimesheet.submit();"/></td>
			</tr>
			
			<tr>
				<td>From Date</td>
				<td><s:textfield name="frmDate" id="idStdDateStart"
						onblur="getDateDiff()" cssClass="validateRequired" cssStyle="width:62px"/></td>

				<td>To Date</td>
				<td><s:textfield name="toDate" id="idStdDateEnd"
						onblur="getDateDiff()" cssClass="validateRequired" cssStyle="width:62px"/></td>
			</tr>

			<tr>
				<td></td>
				<td><s:submit cssClass="input_button" value="Add Activity"
						name="submit1" /></td>
			</tr>

		</table>
		
		
</s:form>		
		
<%if((uF.parseToInt((String)request.getAttribute("nApproved"))==0 && strUserType!=null && strUserType.equalsIgnoreCase(IConstants.EMPLOYEE)) || (uF.parseToInt((String)request.getAttribute("nApproved"))==1 && strUserType!=null && strUserType.equalsIgnoreCase(IConstants.MANAGER))){%>		
		
<s:form id="formID2" action="AddProjectActivity2" cssClass="formcss" method="post" theme="simple" onsubmit="return submitForm();">		
		
<input type="hidden" name="strEmpId" value="<%=request.getParameter("strEmpId")%>"/>
<input type="hidden" name="frmDate" value="<%=request.getParameter("frmDate")%>"/>
<input type="hidden" name="toDate" value="<%=request.getParameter("toDate")%>"/>
<input type="hidden" name="strProject" value="<%=request.getParameter("strProject")%>"/>
<input type="hidden" name="strActivity" value="<%=request.getParameter("strActivity")%>"/>
<input type="hidden" name="strPaycycle" value="<%=request.getParameter("strPaycycle")%>"/>
		
			<div style="float:left;width:1100px;" id="div_tasks">
		    <div style="float:left;width:1100px;padding:2px;">
			    <div  style="float:left;width:75px;font-weight:bold;">Date</div>
			    <div  style="float:left;width:220px;font-weight:bold;">Client</div>
			    <div  style="float:left;width:220px;font-weight:bold;">Project</div>
			    <div  style="float:left;width:220px;font-weight:bold;">Task</div>
			    <div  style="float:left;width:150px;font-weight:bold;">Total Hrs</div>
			    <div  style="float:left;width:100px;font-weight:bold;">Add New Task</div>
			    <div  style="float:left;width:100px;font-weight:bold;">Remove Task</div>
			 </div>
			 
			 <%=request.getAttribute("sbTasks") %>
			 
			 
			 <div style="float:left;width:1100px;padding:2px;">
			    <div  style="float:left;width:75px;">&nbsp;</div>
			    <div  style="float:left;width:220px;">&nbsp;</div>
			    <div  style="float:left;width:220px;">&nbsp;</div>
			    <div  style="float:left;width:220px;">&nbsp;</div>
			    <div  style="float:left;width:150px;">&nbsp;</div>
			    <div  style="float:left;width:100px;"><input type="submit" name="save" value="Save" class="input_button"></div>
			    <div  style="float:left;width:100px;"><input type="submit" name="cancel" value="Cancel" class="input_button"></div>
			 </div>
			 
			    
	    </div>
</s:form>		
<%} %>		
		
		


<div style="float:right;width:100%;">
	<div style="float:right;margin-right:10px;padding:2px">
		<%if(request.getAttribute("approved_by")!=null){ %>
		Approved By <b><%=request.getAttribute("approved_by") %></b>
		<%} %>
	</div>
	<div style="float:right;margin-right:10px;padding:2px">
		<%if(request.getAttribute("submitted_on")!=null){ %>
		Submitted on <b><%=request.getAttribute("submitted_on") %></b>
		<%} %>
	</div>
</div>  



<div style="float: left; overflow: scroll; width: 100%;">

<table class="tb_style" style="width:100%">

	<tr>
		<th colspan="34"><%=request.getAttribute("timesheet_title") %></th>
	</tr>

	<tr>
		<th>Client</th>
		<th>Project</th>
		<th>Task</th>
		<%for(int i=0; i<alDates.size(); i++){ %>
			<th><%=uF.getDateFormat((String)alDates.get(i), IConstants.DATE_FORMAT, "dd")%></th>
		<%} %>
	</tr>



		<%

		Set set = hmProjectTasks.keySet();
		Iterator it = set.iterator();
		Map hmTotal = new HashMap();
		while(it.hasNext()){
			//String strProId = (String)it.next();
			
			String strActivityId = (String)it.next();
			
			Map hmTasks =  (Map)hmProjectTasks.get(strActivityId);
			if(hmTasks==null)hmTasks = new HashMap();
			
			String strProjectId = (String)hmTasks.get(strActivityId+"_P");
			
			
			Map hmDates =  (Map)hmProjects.get(strActivityId);
			if(hmDates==null)hmDates = new HashMap();
			
			
			%>
			
			<tr>
				<td><%=uF.showData((String)hmClientMap.get(strProjectId), "")%></td>
				<td><%=uF.showData((String)hmProjectMap.get(strProjectId), "")%></td>
				  
				<%
				for(int i=0; i<alDates.size(); i++){ %>
					<%
					
					String strText = "-";
					String strBgColor = (String) hmWeekendMap.get((String) alDates.get(i)+"_"+strWLocationId);
					if(strBgColor!=null){
						strText = "H";
					}
					if(strBgColor==null){
						strBgColor = (String)hmHolidayDates.get((String) alDates.get(i)+"_"+strWLocationId);
						strText = "H";
					}
					 if(strBgColor==null){
						strBgColor = (String) hmLeavesColour.get((String)hmLeaves.get(alDates.get(i)));
						strText = (String)hmLeaves.get((String)alDates.get(i));
					}  
					if(strText==null){
						strText = "-";
					}
					 
					    
					 
					
					if(i==0){ %>
					<td><%=uF.showData((String)hmTasks.get(strActivityId+"_T"), "-") %></td>
					<%} %>
					<td align="right" style="background-color: <%=strBgColor%>">
					<%-- <a href="AddProjectActivity2.action?strEmpId=<%=request.getAttribute("strEmpId") %>&frmDate=<%=(String)alDates.get(i)%>&toDate=<%=(String)alDates.get(i)%>&strProject=<%=strProjectId%>&strActivity=<%=strActivityId%>">
					<%=uF.showData((String)hmDates.get((String)alDates.get(i)), strText)%>
					</a>
					 --%>
					
						<%if(uF.parseToDouble(uF.showData((String)hmDates.get((String)alDates.get(i)), strText))>0){ %>
						<a href="AddProjectActivity1.action?strPaycycle=<%=request.getAttribute("strPaycycle") %>&strEmpId=<%=request.getAttribute("strEmpId") %>&frmDate=<%=(String)alDates.get(i)%>&toDate=<%=(String)alDates.get(i)%>&strProject=<%=strProjectId%>&strActivity=<%=strActivityId%>">
							<%=uF.showData((String)hmDates.get((String)alDates.get(i)), strText)%>
						</a>
						<%}else{ %>
							<%=uF.showData((String)hmDates.get((String)alDates.get(i)), strText)%>
						<%}%>
						
						
					</td>
				<%
				double dblHrs = uF.parseToDouble((String)hmDates.get((String)alDates.get(i)));
				dblHrs += uF.parseToDouble((String)hmTotal.get((String)alDates.get(i)));
				hmTotal.put((String)alDates.get(i), dblHrs+"");
				
				} %>
			</tr>
			
			<%
			}
			%>

		<tr>
				<td></td>
				<td>Total Hrs.</td>
				<td></td>
				<%for(int i=0; i<alDates.size(); i++){ %>
					<td align="right"><%=uF.showData((String)hmTotal.get((String)alDates.get(i)), "-")%></td>
				<%
				} %>
			</tr>
			
			
			
			<tr>
				<td></td>
				<td>Comp Off</td>
				<td></td>
				<%for(int i=0; i<alDates.size(); i++){ %>
					<td align="center">
					<%
					if(((String) hmWeekendMap.get((String) alDates.get(i)+"_"+strWLocationId)!=null || (String)hmHolidayDates.get((String) alDates.get(i)+"_"+strWLocationId)!=null) && uF.parseToDouble((String)hmTotal.get((String)alDates.get(i)))>0){
					%>
					<input type="checkbox" checked name="compOff" value="<%=(String)alDates.get(i)%>"/>
					<%}%>					
					
					</td>
				<%
				} %>
			</tr>
</table>
</div>


<s:form name="timesheet" action="AddProjectActivity1">

<input type="hidden" name="strPaycycle" value="<%=request.getAttribute("strPaycycle") %>" />


<div style="float:right">

<%if(uF.parseToInt((String)request.getAttribute("nApproved"))==0 && strUserType!=null && strUserType.equalsIgnoreCase(IConstants.EMPLOYEE)){%>
	<input type="hidden" name="type" value="submit"/>
	<input type="submit" align="right" value="Submit" class="input_button" onclick="return confirm('Are you sure you want to submit your timesheet?')">
<%} else if(uF.parseToInt((String)request.getAttribute("nApproved"))==1 && strUserType!=null && (strUserType.equalsIgnoreCase(IConstants.MANAGER) || strUserType.equalsIgnoreCase(IConstants.HRMANAGER))){%>
	<input type="hidden" name="type" value="approve"/>
	<input type="hidden" name="strEmpId" value="<%=request.getParameter("strEmpId")%>"/>
	<input type="hidden" name="frmDate" value="<%=request.getParameter("frmDate")%>"/>
	<input type="hidden" name="toDate" value="<%=request.getParameter("toDate")%>"/>
	<input type="hidden" name="strProject" value="<%=request.getParameter("strProject")%>"/>
	<input type="hidden" name="strActivity" value="<%=request.getParameter("strActivity")%>"/>
	<input type="hidden" name="timesheetId" value="<%=request.getAttribute("timesheetId")%>"/>
	<input type="submit" align="right" value="Approve" class="input_button" onclick="return confirm('Are you sure you want to approve this timesheet?')">
<%} else if(uF.parseToInt((String)request.getAttribute("nApproved"))==2){%>
<%}%>
</div>

</s:form>





</div>