<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script type="text/javascript">
$(function() {
	$("body").on('click','#closeButton',function(){
		$(".modal-dialog").removeAttr('style');
		$(".modal-body").height(400);
		$("#modalInfo").hide();
	});
	$("body").on('click','.close',function(){
		$(".modal-dialog").removeAttr('style');
		$(".modal-body").height(400);
		$("#modalInfo").hide();
	});
});


function getData(type) {
	var org='';
	var location='';
	var userscreen = document.getElementById("userscreen").value;
	var navigationId = document.getElementById("navigationId").value;
	var toPage = document.getElementById("toPage").value;
	if(type=='2') {
		org=document.getElementById("strOrg").value;
		location=document.getElementById("strLocation").value;
	} else {
		org=document.getElementById("strOrg").value;
		
	}
	window.location='MyDashboard.action?strOrg='+org+'&strLocation='+location+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage;
}


function addNewLeavePolicy(org,param,location,leaveName, userscreen, navigationId, toPage) {
	
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Set New Leave Policy for '+leaveName);
	$.ajax({
		url : 'EmployeeIssueLeave.action?orgId='+org+'&param='+param+'&strLocation='+location+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}


function editLeavePolicy(org, param, location, id, leaveName, userscreen, navigationId, toPage) {
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Edit Leave Policy for '+leaveName);
	$.ajax({
		url : 'EmployeeIssueLeave.action?operation=E&orgId='+org+'&param='+param+'&strLocation='+location+'&ID='+id+'&userscreen='+userscreen
				+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}


function editLeavetype(id, location, userscreen, navigationId, toPage){
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Edit Leave Type');
	$.ajax({
		url : 'AddLeaveType.action?operation=E&ID='+id+'&strLocation='+location+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage, 
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

function addNewLeaveType(org, location, userscreen, navigationId, toPage){
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('New Leave type');
	$.ajax({
		url : 'AddLeaveType.action?orgId='+org+'&strLocation='+location+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

/* function editLeaveDescription(lId,location, userscreen, navigationId, toPage){
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Edit Leave Description');
	$.ajax({
		url : 'LeaveDescription.action?LID='+lId+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
} */

function updateLeaveBalance(orgId, levelId, locationId, strLeaveTypeId, leaveType, levelName, userscreen, navigationId, toPage){
	if(confirm("Should I reset the balance of '"+leaveType+"' based on this new update for '"+levelName+"'?\nPlease use the above condition very carefully, before acting on it, or else use the regularize system.")){
		window.location='EmployeeIssueLeave.action?operation=ULeaveBalance&orgId='+orgId+'&strLevelId='+levelId+'&strLocation='
				+locationId+'&param='+strLeaveTypeId+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage;	
	}
} 

</script>


<%
	UtilityFunctions uF = new UtilityFunctions();
	Map<String, String> hmFeatureStatus = (Map<String, String>)request.getAttribute("hmFeatureStatus");
	Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>)request.getAttribute("hmFeatureUserTypeId");
	String strUsertypeId = (String)session.getAttribute(IConstants.BASEUSERTYPEID);

	Map hmLeaveTypeMap = (Map)request.getAttribute("hmLeaveTypeMap"); 
	Map hmLeavePoliciesMap = (Map)request.getAttribute("hmLeavePoliciesMap");

	String userscreen = (String)request.getAttribute("userscreen");
	String navigationId = (String)request.getAttribute("navigationId");
	String toPage = (String)request.getAttribute("toPage");
	
%> 

	<div class="box-body">
			
		<div class="box box-default collapsed-box">
			<div class="box-header with-border">
			    <h3 class="box-title"><%=(String)request.getAttribute("selectedFilter") %></h3>
			    <div class="box-tools pull-right">
			        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
			        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
			    </div>
			</div>
			<div class="box-body" style="padding: 5px; overflow-y: auto;">
				<s:form name="frm" action="MyDashboard" theme="simple">
					<s:hidden name="userscreen" id="userscreen" />
					<s:hidden name="navigationId" id="navigationId" />
					<s:hidden name="toPage" id="toPage" />
					<div style="float: left; width: 99%; margin-left: 10px;">
						<div style="float: left; margin-right: 5px;">
							<i class="fa fa-filter"></i>
						</div>
						<div style="float: left; width: 75%;">
							<div style="float: left; margin-left: 10px;">
								<p style="padding-left: 5px;">Organisation</p>
								<% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_LOGIN_USER_ORG_IN_FILTER)) && hmFeatureUserTypeId.get(IConstants.F_LOGIN_USER_ORG_IN_FILTER).contains(strUsertypeId)) { %>
									<s:select list="orgList" name="strOrg" id="strOrg" listKey="orgId" listValue="orgName" onchange="getData('1');"></s:select>
								<% } else { %>
									<s:select list="orgList" name="strOrg" id="strOrg" listKey="orgId" listValue="orgName" onchange="getData('1');"></s:select>
								<% } %>
							</div>
							<div style="float: left; margin-left: 10px;">
								<p style="padding-left: 5px;">Location</p>
								<s:select list="workList" name="strLocation" id="strLocation" listKey="wLocationId" listValue="wLocationName" onchange="getData('2');"></s:select>
							</div>
						</div>
					</div>
				</s:form>
			</div>
		</div>
		
		<%=uF.showData((String) session.getAttribute("MESSAGE"), "")%>
		<% session.setAttribute("MESSAGE", ""); %>

	<div class="col-md-12">
		<a href="javascript:void(0)" onclick="addNewLeaveType('<%=request.getAttribute("strOrg") %>','<%=request.getAttribute("strLocation") %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')"> <i class="fa fa-plus-circle" aria-hidden="true"></i> Add New Leave Type</a>
	</div>

	<div class="col-md-12">
         <ul class="level_list">
		<% 
			Set setLevelMap = hmLeaveTypeMap.keySet();
			Iterator it = setLevelMap.iterator();
			
			while(it.hasNext()){
				String strLeaveTypeId = (String)it.next();
				List alLeaveType = (List)hmLeaveTypeMap.get(strLeaveTypeId);
				if(alLeaveType==null)alLeaveType=new ArrayList();
					
					List alLeavePolicy = (List)hmLeavePoliciesMap.get(strLeaveTypeId);
					if(alLeavePolicy==null)alLeavePolicy=new ArrayList();
					%>
					
					<li>
						<a href="AddLeaveType.action?orgId=<%=request.getAttribute("strOrg") %>&operation=D&ID=<%=strLeaveTypeId%>&strLocation=<%=request.getAttribute("strLocation") %>&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>" onclick="return confirm('Are you sure you wish to delete this leave type?\nAll leave policies associated will also be deleted.')"  style="color:rgb(233,0,0)"> <i class="fa fa-trash" aria-hidden="true"></i> </a> 
						<a href="javascript:void(0)" onclick="editLeavetype('<%=strLeaveTypeId%>','<%=request.getAttribute("strLocation") %>','<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
						
						<strong><%=alLeaveType.get(2)%> [<%=alLeaveType.get(1)%>]</strong>&nbsp;&nbsp;<span style="background-color:<%=alLeaveType.get(3)%>;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
						<ul>		
							<li class="addnew desgn">
								<a href="javascript:void(0)" onclick="addNewLeavePolicy('<%=request.getAttribute("strOrg") %>','<%=strLeaveTypeId %>','<%=request.getAttribute("strLocation") %>','<%=alLeaveType.get(2) %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')"> <i class="fa fa-plus-circle" aria-hidden="true"></i> Add New Leave Policy</a>
							</li>
							
							<%
								for(int d=0; d<alLeavePolicy.size(); d+=20){
									String strPolicyId = (String)alLeavePolicy.get(d);
							%>  
							
							<li>
								<a href="EmployeeIssueLeave.action?orgId=<%=request.getAttribute("strOrg") %>&operation=D&ID=<%=strPolicyId%>&strLocation=<%=request.getAttribute("strLocation") %>&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>" onclick="return confirm('Are you sure you wish to delete this policy?')"> <i class="fa fa-trash" aria-hidden="true"></i> </a>
			                    <a href="javascript:void(0)" onclick="editLeavePolicy('<%=request.getAttribute("strOrg") %>','<%=strLeaveTypeId %>','<%=request.getAttribute("strLocation") %>','<%=strPolicyId%>','<%=alLeaveType.get(2) %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
			          			
			                    <%if(!uF.parseToBoolean(""+alLeaveType.get(5))){ %> 
				                  	<a href="javascript:void(0)" onclick="updateLeaveBalance('<%=request.getAttribute("strOrg") %>','<%=alLeavePolicy.get(d+1) %>','<%=request.getAttribute("strLocation") %>','<%=strLeaveTypeId %>','<%=alLeaveType.get(2) %>','<%=alLeavePolicy.get(d+2) %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')"><i class="fa fa-clipboard" aria-hidden="true"></i></a>
				                <%} %>
			                    Level: <strong><%=alLeavePolicy.get(d+14)%> [<%=alLeavePolicy.get(d+2)%>]</strong> &nbsp;&nbsp;&nbsp;
			                    Is Accrual: <strong><%=alLeavePolicy.get(d+16)%></strong> &nbsp;&nbsp;&nbsp;
			                    <%if(!uF.parseToBoolean((String)alLeaveType.get(4))){
			                    	if(uF.parseToBoolean((String)alLeavePolicy.get(d+15))){
			                    		if(uF.parseToInt((String)alLeavePolicy.get(d+17)) == 2){
			                    %>			
			                    			No. of Days: <strong><%=alLeavePolicy.get(d+18)%></strong> &nbsp;&nbsp;&nbsp;
			                    			No. of Leaves: <strong><%=alLeavePolicy.get(d+19)%></strong> &nbsp;&nbsp;&nbsp;
			                    <%		} else { %>
			                    			No. of Leaves(Monthly): <strong><%=alLeavePolicy.get(d+19)%></strong> &nbsp;&nbsp;&nbsp;
			                    			No. of Leaves(Annually): <strong><%=alLeavePolicy.get(d+3)%></strong> &nbsp;&nbsp;&nbsp;
			                    <%		}
			                    %>
			                     <% } else { %>	
			                    	No. of Leaves(Monthly): <strong><%=alLeavePolicy.get(d+19)%></strong> &nbsp;&nbsp;&nbsp;
			                     	No. of Leaves: <strong><%=alLeavePolicy.get(d+3)%></strong> &nbsp;&nbsp;&nbsp;
			                     <% } %>	
			                     	Leave Calculation from: <strong><%=alLeavePolicy.get(d+4)%></strong> &nbsp;&nbsp;&nbsp;
			                     <%} %>
			                     Is Paid: <strong><%=alLeavePolicy.get(d+5)%></strong> &nbsp;&nbsp;&nbsp;
			                     Is Carry Forward: <strong><%=  alLeavePolicy.get(d+6)%></strong>  
			                     <%-- Monthly Leave Limit: <strong><%=uF.showData((String)alLeavePolicy.get(d+7), "0")%></strong> &nbsp;&nbsp;&nbsp;
			                     Consecutive Leave Limit: <strong><%=uF.showData((String)alLeavePolicy.get(d+8), "0")%></strong> &nbsp;&nbsp;&nbsp; --%> 
			                     <%-- Is Monthly Carry Forward: <strong><%=  alLeavePolicy.get(d+9)%></strong> --%> 
		                     	<ul>
			                    <%if(uF.parseToBoolean((String)alLeaveType.get(4))){ %>
			                    	<li><input type="checkbox" onclick="(confirm('Are you sure, you want to update the policy?')? getContent('myDiv_H<%=d%>', 'UpdateLeavePolicy.action?LPID=<%=strPolicyId%>&type=H&CS='+this.checked):'')" <%=((uF.parseToBoolean((String)alLeavePolicy.get(d+12)))?"checked":"") %>/> Compensated with Holidays  <span id="myDiv_H<%=d%>"></span></li>
			                    	<li><input type="checkbox" onclick="(confirm('Are you sure, you want to update the policy?')?getContent('myDiv_W<%=d%>', 'UpdateLeavePolicy.action?LPID=<%=strPolicyId%>&type=W&CS='+this.checked):'')" <%=((uF.parseToBoolean((String)alLeavePolicy.get(d+13)))?"checked":"") %>/> Compensated with Weekends  <span id="myDiv_W<%=d%>"></span></li>
			                    <%} %>
			                    </ul>
			                   
			                    <p style="font-size: 12px; padding-left: 42px; font-style: italic;">	Last updated by <%=uF.showData((String)alLeavePolicy.get(d+10), "N/A")%> on <%=uF.showData((String)alLeavePolicy.get(d+11),"")%></p>
		                    </li>
							
						<% } %>		
					</ul>
				</li> 
			<% } %>
		 </ul>
     </div>	
</div>


<div class="modal" id="modalInfo" role="dialog">
    <div class="modal-dialog">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">Candidate Information</h4>
            </div>
            <div class="modal-body" style="height:400px;overflow-y:auto;padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>
