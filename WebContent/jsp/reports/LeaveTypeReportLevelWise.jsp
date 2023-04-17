<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags" %>

<script type="text/javascript">
function getData(type){
	
	var org='';
	var location='';
	if(type=='2'){
		org=document.getElementById("strOrg").value;
		location=document.getElementById("strLocation").value;
	}else{
		org=document.getElementById("strOrg").value;
		
	}
	
	window.location='LeaveTypeReport.action?strOrg='+org+"&strLocation="+location;
}

function addNewLeavePolicy(org,param,location,leaveName){
	removeLoadingDiv('the_div');
	
	var dialogEdit = '#newLeavePolicyDiv';
	dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
	$(dialogEdit).dialog({
		autoOpen : false,
		bgiframe : true,
		resizable : true,
		height : 450,
		width : 850,
		modal : true,
		title : 'Set New Leave Policy for '+leaveName,
		open : function() {
			var xhr = $.ajax({
				url : "EmployeeIssueLeave.action?orgId="+org+"&param="+param+"&strLocation="+location,
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

function editLeavePolicy(org,param,location,id,leaveName){
	removeLoadingDiv('the_div');
	
	var dialogEdit = '#editLeavePolicyDiv'; 
	dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
	$(dialogEdit).dialog({
		autoOpen : false,
		bgiframe : true,
		resizable : true,
		height : 450,
		width : 850,
		modal : true,
		title : 'Edit Leave Policy for '+leaveName,
		open : function() {
			var xhr = $.ajax({
				url : "EmployeeIssueLeave.action?operation=E&orgId="+org+"&param="+param+"&strLocation="+location+"&ID="+id,
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

function editLeavetype(id,location,strAction){
	removeLoadingDiv('the_div');
	
	var dialogEdit = '#editLeavetypeDiv';
	dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
	$(dialogEdit).dialog({
		autoOpen : false,
		bgiframe : true,
		resizable : true,
		height : 450,
		width : 650,
		modal : true,
		title : 'Edit Leave Type',
		open : function() {
			var xhr = $.ajax({
				url : 'AddLeaveType.action?operation=E&ID='+id+'&strLocation='+location+'&URI='+strAction, 
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
function addNewLeaveType(org,location){
	removeLoadingDiv('the_div');
	
	var dialogEdit = '#addNewLeaveTypeDiv';  
	dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
	$(dialogEdit).dialog({
		autoOpen : false,
		bgiframe : true,
		resizable : true,
		height : 450,
		width : 650,
		modal : true,
		title : 'New Leave type',
		open : function() {
			var xhr = $.ajax({
				url : "AddLeaveType.action?orgId="+org+"&strLocation="+location,
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
function editLeaveDescription(lId,location){
	removeLoadingDiv('the_div');
	
	var dialogEdit = '#editLeaveDescriptionDiv';
	dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
	$(dialogEdit).dialog({
		autoOpen : false,
		bgiframe : true,
		resizable : true,
		height : 450,
		width : 650,
		modal : true,
		title : 'Edit Leave Description',
		open : function() {
			var xhr = $.ajax({
				url : "LeaveDescription.action?LID="+lId,
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

function updateLeaveBalance(orgId,levelId,locationId,strLeaveTypeId,leaveType,levelName){
	if(confirm("Should I reset the balance of '"+leaveType+"' based on this new update for '"+levelName+"'?\nPlease use the above condition very carefully, before acting on it, or else use the regularize system.")){
		window.location='EmployeeIssueLeave.action?operation=ULeaveBalance&orgId='+orgId+'&strLevelId='+levelId+'&strLocation='+locationId+'&param='+strLeaveTypeId;	
	}
} 


</script>

<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Manage Leave Policies" name="title"/>
</jsp:include>


<%
UtilityFunctions uF = new UtilityFunctions();
Map hmLevelMap = (Map)request.getAttribute("hmLevelMap"); 
Map hmLeavePoliciesMap = (Map)request.getAttribute("hmLeavePoliciesMap");

String strAction = (String)request.getAttribute("javax.servlet.forward.request_uri");
if(strAction!=null){
	strAction = strAction.replace(request.getContextPath()+"/","");
}
%>

<div id="printDiv" class="leftbox reportWidth">

<%=uF.showData((String)session.getAttribute(IConstants.MESSAGE),"") %>	

<div class="filter_div">
<div class="filter_caption">Select Organisation</div>
<s:form name="frm" action="LeaveTypeReport" theme="simple">
	<s:select list="orgList" name="strOrg" id="strOrg" listKey="orgId" listValue="orgName" onchange="getData('1');"></s:select>
	<s:select theme="simple" name="strLocation" id="strLocation" listKey="wLocationId" listValue="wLocationName" list="workList" onchange="getData('2');"/>
	<s:hidden name="type" value="level"></s:hidden>
</s:form>
</div>
	 
<%if(uF.parseToBoolean((String)request.getAttribute(IConstants.ADD_ACCESS))){ %>		
<%-- <div style="float:left; margin:0px 0px 10px 0px"> <a href="AddLeaveType.action?orgId=<%=request.getAttribute("strOrg") %>&strLocation=<%=request.getAttribute("strLocation") %>" onclick="return hs.htmlExpand(this, { objectType: 'ajax',width:700 })"> + Add New Leave Type</a></div> --%>
<div style="float:left; margin:0px 0px 10px 0px"> <a href="javascript:void(0)" onclick="addNewLeaveType('<%=request.getAttribute("strOrg") %>','<%=request.getAttribute("strLocation") %>')"> + Add New Leave Type</a></div>
<%} %>

<div style="float:right; margin:0px 0px 10px 0px"> <a href="LeaveTypeReport.action?strOrg=<%=request.getAttribute("strOrg") %>&strLocation=<%=request.getAttribute("strLocation") %>"> Leave Policies Leave Type wise</a></div>
  
<div class="clr"></div>

<div>
         <ul class="level_list">

		
		<% 
			Set setLevelMap = hmLevelMap.keySet();
			Iterator it = setLevelMap.iterator();
			
			while(it.hasNext()){
				String strLevelId = (String)it.next();
				List alLeaveType = (List)hmLevelMap.get(strLevelId);
				if(alLeaveType==null)alLeaveType=new ArrayList();
				
					
					List alLeavePolicy = (List)hmLeavePoliciesMap.get(strLevelId);
					if(alLeavePolicy==null)alLeavePolicy=new ArrayList();
					%>
					
					<li>
					
					<strong><%=alLeaveType.get(2)%> [<%=alLeaveType.get(1)%>]</strong>&nbsp;&nbsp;
					<ul>		
					<%-- <li class="addnew desgn">
					<%if(uF.parseToBoolean((String)request.getAttribute(IConstants.ADD_ACCESS))){ %>
					
					<a href="javascript:void(0)" onclick="addNewLeavePolicy('<%=request.getAttribute("strOrg") %>','<%=alLeaveType.get(0) %>','<%=request.getAttribute("strLocation") %>','<%=alLeaveType.get(2) %>')"> + Add New Leave Policy</a>
					<%} %> --%>
					</li>
					
					<%
						for(int d=0; d<alLeavePolicy.size(); d+=14){
						String strPolicyId = (String)alLeavePolicy.get(d);
						
					%>  
					
					<li>
					<%-- <%if(uF.parseToBoolean((String)request.getAttribute(IConstants.DELETE_ACCESS))){ %> 
                    	<a href="EmployeeIssueLeave.action?orgId=<%=request.getAttribute("strOrg") %>&operation=D&ID=<%=strPolicyId%>&strLocation=<%=request.getAttribute("strLocation") %>" class="del" onclick="return confirm('Are you sure you wish to delete this policy?')"> - </a>
                    <%} %>
                    <%if(uF.parseToBoolean((String)request.getAttribute(IConstants.UPDATE_ACCESS))){ %> 
                    	<a href="javascript:void(0)" class="edit_lvl" onclick="editLeavePolicy('<%=request.getAttribute("strOrg") %>','<%=alLeavePolicy.get(d+2) %>','<%=request.getAttribute("strLocation") %>','<%=strPolicyId%>','<%=alLeaveType.get(2) %>')">Edit</a>
                    <%} %>  --%>
                    
                     Leave Type: <strong><%=alLeavePolicy.get(d+2)%></strong> &nbsp;&nbsp;&nbsp;
                     No. of Leaves: <strong><%=alLeavePolicy.get(d+3)%></strong> &nbsp;&nbsp;&nbsp;
                     Leave Calculation from: <strong><%=alLeavePolicy.get(d+4)%></strong> &nbsp;&nbsp;&nbsp;
                     Is Paid: <strong><%=alLeavePolicy.get(d+5)%></strong> &nbsp;&nbsp;&nbsp;
                     Is Carry Forward: <strong><%=  alLeavePolicy.get(d+6)%></strong>  
                     Monthly Leave Limit: <strong><%=uF.showData((String)alLeavePolicy.get(d+7), "0")%></strong> &nbsp;&nbsp;&nbsp;
                     Consecutive Leave Limit: <strong><%=uF.showData((String)alLeavePolicy.get(d+8), "0")%></strong> &nbsp;&nbsp;&nbsp; 
                     Is Monthly Carry Forward: <strong><%=  alLeavePolicy.get(d+9)%></strong>
	                     
                      <p style="font-size: 10px; padding-left: 42px; font-style: italic;">	Last updated by <%=uF.showData((String)alLeavePolicy.get(d+10), "N/A")%> on <%=uF.showData((String)alLeavePolicy.get(d+11),"")%></p>
                    </li>
						
				<%
					}
				%>		
					
                 
                 </ul>
                 </li> 
		<%
			}
		%>
		 
		 </ul>
         
     </div>	
</div>

<div id="newLeavePolicyDiv"></div>
<div id="editLeavePolicyDiv"></div>
<div id="editLeavetypeDiv"></div>
<div id="addNewLeaveTypeDiv"></div>
<div id="editLeaveDescriptionDiv"></div>