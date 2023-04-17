<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

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


function getData(type, userscreen, navigationId, toPage) {
	var org='';
	var location='';
	var userscreen = document.frm_RosterPolicyReport.userscreen.value;
	var navigationId = document.frm_RosterPolicyReport.navigationId.value;
	var toPage = document.frm_RosterPolicyReport.toPage.value;
	if(type=='2') {
		org=document.frm_RosterPolicyReport.strOrg.value;
		location=document.frm_RosterPolicyReport.strWLocation.value;
	} else {
		org=document.frm_RosterPolicyReport.strOrg.value;
	}
	window.location='MyDashboard.action?strOrg='+org+'&strLocation='+location+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage;
}


function addNewExceptionRule(userscreen, navigationId, toPage) { 
	
	var org=document.frm_RosterPolicyReport.strOrg.value;
	var location=document.frm_RosterPolicyReport.strWLocation.value;
	
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('New Exception Rule');
	$.ajax({
		url : 'RosterPolicy.action?orgId='+org+'&strWlocation='+location+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}


function editNewExceptionRule(strRosterPolicyId, userscreen, navigationId, toPage){
	
	var org=document.frm_RosterPolicyReport.strOrg.value;
	var location=document.frm_RosterPolicyReport.strWLocation.value;
	
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html( 'Edit Exception Rule');
	$.ajax({
		url : 'RosterPolicy.action?operation=E&ID='+strRosterPolicyId+'&orgId='+org+'&strWlocation='+location+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}


function addHalfDayRule(userscreen, navigationId, toPage){
	
	var org=document.frm_RosterPolicyReport.strOrg.value;
	var location=document.frm_RosterPolicyReport.strWLocation.value;
	
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Add Half Day Rule');
	$.ajax({
		url : 'RosterPolicyHD.action?orgId='+org+'&strWlocation='+location+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}


function editHalfDayRule(strRosterHDPolicyId, userscreen, navigationId, toPage){
	
	var org=document.frm_RosterPolicyReport.strOrg.value;
	var location=document.frm_RosterPolicyReport.strWLocation.value;
	
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Edit Half Day Rule');
	$.ajax({
		url : 'RosterPolicyHD.action?operation=E&ID='+strRosterHDPolicyId+'&orgId='+org+'&strWlocation='+location+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}


function addBreakRule(userscreen, navigationId, toPage){
	
	var org=document.frm_RosterPolicyReport.strOrg.value;
	var location=document.frm_RosterPolicyReport.strWLocation.value;
	
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Add Break Rule');
	$.ajax({
		url : 'RosterPolicyBreak.action?orgId='+org+'&strWLocation='+location+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}


function editBreakRule(strRosterBreakPolicyId, userscreen, navigationId, toPage){
	
	var org=document.frm_RosterPolicyReport.strOrg.value;
	var location=document.frm_RosterPolicyReport.strWLocation.value;
	
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Edit Break Rule');
	$.ajax({
		url : 'RosterPolicyBreak.action?operation=E&ID='+strRosterBreakPolicyId+'&orgId='+org+'&strWlocation='+location+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}


function addFullDayRule(userscreen, navigationId, toPage) {
	
	var org = document.frm_RosterPolicyReport.strOrg.value;
	var location = document.frm_RosterPolicyReport.strWLocation.value;
	
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Add Full Day Rule');
	$.ajax({
		url : 'RosterPolicyFD.action?orgId='+org+'&strWlocation='+location+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}


function editFullDayRule(strRosterHDPolicyId, userscreen, navigationId, toPage) {
	var org = document.frm_RosterPolicyReport.strOrg.value;
	var location = document.frm_RosterPolicyReport.strWLocation.value;
	
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Edit Full Day Rule');
	$.ajax({
		url : 'RosterPolicyFD.action?operation=E&ID='+strRosterHDPolicyId+'&orgId='+org+'&strWlocation='+location+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

function addHalfDayFullDayMinHrsRule(userscreen, navigationId, toPage, exceptionType) {
	var org = document.frm_RosterPolicyReport.strOrg.value;
	var location = document.frm_RosterPolicyReport.strWLocation.value;
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	var strMsg = 'Full Day';
	if(exceptionType == 'HD') {
		strMsg = 'Half Day';
	}
	$('.modal-title').html('Add '+strMsg+' Exception Rule');
	$.ajax({
		url : 'RosterPolicyMinHrsHD_FD.action?orgId='+org+'&strWlocation='+location+'&exceptionType='+exceptionType+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}


function editHalfDayFullDayMinHrsRule(strRosterHDFDPolicyId, userscreen, navigationId, toPage, exceptionType) {
	var org = document.frm_RosterPolicyReport.strOrg.value;
	var location = document.frm_RosterPolicyReport.strWLocation.value;
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	var strMsg = 'Full Day';
	if(exceptionType == 'HD') {
		strMsg = 'Half Day';
	}
	$('.modal-title').html('Edit '+strMsg+' Exception Rule');
	$.ajax({
		url : 'RosterPolicyMinHrsHD_FD.action?operation=E&ID='+strRosterHDFDPolicyId+'&orgId='+org+'&strWlocation='+location+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

</script>


<%
	UtilityFunctions uF = new UtilityFunctions();
	Map<String, String> hmFeatureStatus = (Map<String, String>)request.getAttribute("hmFeatureStatus");
	Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>)request.getAttribute("hmFeatureUserTypeId");
	String strUsertypeId = (String)session.getAttribute(IConstants.BASEUSERTYPEID);
	
	Map hmRosterPolicyReport = (Map)request.getAttribute("hmRosterPolicyReport");
	Map hmRosterHDPolicyReport = (Map)request.getAttribute("hmRosterHDPolicyReport");
	Map hmRosterBreakPolicyReport = (Map)request.getAttribute("hmRosterBreakPolicyReport");
	
	Map<String,List<String>> hmRosterFDPolicyReport = (Map<String,List<String>>)request.getAttribute("hmRosterFDPolicyReport");
	
	Map<String,List<String>> hmRosterHDFDMinHrsPolicyReport = (Map<String,List<String>>) request.getAttribute("hmRosterHDFDMinHrsPolicyReport");
	if(hmRosterHDFDMinHrsPolicyReport==null) hmRosterHDFDMinHrsPolicyReport = new HashMap<String,List<String>>();
	
	//out.println(hmOfficeTypeMap);
	
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
				<s:form name="frm_RosterPolicyReport" action="MyDashboard" theme="simple">
					<s:hidden name="userscreen" />
					<s:hidden name="navigationId" />
					<s:hidden name="toPage" />
					<div style="float: left; width: 99%; margin-left: 10px;">
						<div style="float: left; margin-right: 5px;">
							<i class="fa fa-filter"></i>
						</div>
						<div style="float: left; width: 75%;">
							<div style="float: left; margin-left: 10px;">
								<p style="padding-left: 5px;">Organisation</p>
								<% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_LOGIN_USER_ORG_IN_FILTER)) && hmFeatureUserTypeId.get(IConstants.F_LOGIN_USER_ORG_IN_FILTER).contains(strUsertypeId)) { %>
									<s:select list="orgList" name="strOrg" listKey="orgId" listValue="orgName" onchange="document.frm_RosterPolicyReport.submit();"></s:select>
								<% } else { %>
									<s:select list="orgList" name="strOrg" listKey="orgId" listValue="orgName" onchange="document.frm_RosterPolicyReport.submit();"></s:select>
								<% } %>
							</div>
							<div style="float: left; margin-left: 10px;">
								<p style="padding-left: 5px;">Location</p>
								<s:select list="wLocationList" name="strWLocation" listKey="wLocationId" listValue="wLocationName" onchange="getData('2');"></s:select>
							</div>
						</div>
					</div>
				</s:form>
			</div>
		</div>
		
	<%=uF.showData((String)session.getAttribute(IConstants.MESSAGE),"") %>
	<% session.setAttribute(IConstants.MESSAGE, ""); %>


	<div class="col-md-12">
		<a href="javascript:void(0);" onclick="addNewExceptionRule('<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>');"> <i class="fa fa-plus-circle" aria-hidden="true"></i> Add New Exception Rule</a>
	</div>

	<div class="col-md-12">
		<% 
			Set setRosterPolicyMap = hmRosterPolicyReport.keySet();
			Iterator it = setRosterPolicyMap.iterator();
			int count=0;
			while(it.hasNext()){
				String strRosterPolicyId = (String)it.next();
				List alRosterPolicy = (List)hmRosterPolicyReport.get(strRosterPolicyId);
				if(alRosterPolicy==null)alRosterPolicy=new ArrayList();
				count++;
			%>
			 <ul class="level_list">
				<li>
				<%if(uF.parseToInt((String)alRosterPolicy.get(7))==-1){ %>
					<div style="float:left;padding-right:8px;" id="myDiv<%=count%>"><a href="javascript:void(0)" onclick="getContent('myDiv<%=count%>', 'UpdateRequest.action?S=1&T=RP&RID=<%=(String)alRosterPolicy.get(0)%>')"><i class="fa fa-circle" aria-hidden="true" style="color:#e22d25" title="Disabled. Click to enable this policy"></i><!-- <img src="images1/icons/denied.png" title="Disabled. Click to enable this policy" /> --></a></div>
				<%}else if(uF.parseToInt((String)alRosterPolicy.get(7))==1){ %>
					<div style="float:left;padding-right:8px;" id="myDiv<%=count%>"><a href="javascript:void(0)" onclick="getContent('myDiv<%=count%>', 'UpdateRequest.action?S=-1&T=RP&RID=<%=(String)alRosterPolicy.get(0)%>')"> <i class="fa fa-circle" aria-hidden="true" style="color:#54aa0d"  title="Enabled. Click to disable this policy"></i><!-- <img src="images1/icons/approved.png" title="Enabled. Click to disable this policy" /> --></a></div>
				<%} %>
				<a href="RosterPolicy.action?orgId=<%=request.getAttribute("strOrg") %>&strWlocation=<%=request.getAttribute("strWLocation") %>&operation=D&ID=<%=strRosterPolicyId%>&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>" onclick="return confirm('Are you sure you wish to delete this rule?')" style="color:red;"><i class="fa fa-trash" aria-hidden="true"></i></a> 
				<a href="javascript:void(0)" onclick="editNewExceptionRule('<%=strRosterPolicyId%>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
				Anyone who <strong><%=(("IN".equalsIgnoreCase((String)alRosterPolicy.get(4)))?"comes":"leaves") %></strong> <strong><%=(("LATE".equalsIgnoreCase((String)alRosterPolicy.get(3)))?"late":"early") %></strong> by <strong><%=alRosterPolicy.get(1) %></strong> mins  <%=((alRosterPolicy.get(2)!=null && ((String)alRosterPolicy.get(2)).length()>0)?" will be asked "+"<strong>\""+alRosterPolicy.get(2) +"\"</strong>":" will not be asked a question ") %> and this <strong><%=(("YES".equalsIgnoreCase((String)alRosterPolicy.get(5)))?"needs to be approved":"does not need approval") %></strong> and is effective from <strong><%=alRosterPolicy.get(6) %></strong>
				
				<%if("IN".equalsIgnoreCase((String)alRosterPolicy.get(4)) && "LATE".equalsIgnoreCase((String)alRosterPolicy.get(3)) && alRosterPolicy.get(2)!=null && ((String)alRosterPolicy.get(2)).length()>0 ){ %>
					<div class="camelate" style="float:right">&nbsp;</div>
				<%}else if("OUT".equalsIgnoreCase((String)alRosterPolicy.get(4)) && "LATE".equalsIgnoreCase((String)alRosterPolicy.get(3)) && alRosterPolicy.get(2)!=null && ((String)alRosterPolicy.get(2)).length()>0 ){ %>
					<div class="worklate" style="float:right">&nbsp;</div>
				<%}else if("IN".equalsIgnoreCase((String)alRosterPolicy.get(4)) && "EARLY".equalsIgnoreCase((String)alRosterPolicy.get(3)) && alRosterPolicy.get(2)!=null && ((String)alRosterPolicy.get(2)).length()>0 ){ %>
					<div class="cameearly" style="float:right">&nbsp;</div>
				<%}else if("OUT".equalsIgnoreCase((String)alRosterPolicy.get(4)) && "EARLY".equalsIgnoreCase((String)alRosterPolicy.get(3)) && alRosterPolicy.get(2)!=null && ((String)alRosterPolicy.get(2)).length()>0 ){ %>
					<div class="leftearly" style="float:right">&nbsp;</div>
				<%}else if("IN".equalsIgnoreCase((String)alRosterPolicy.get(4)) ) { %>
					<div class="cameontime" style="float:right">&nbsp;</div>
				<%}else if("OUT".equalsIgnoreCase((String)alRosterPolicy.get(4))) { %>
					<div class="wentontime" style="float:right">&nbsp;</div>
				<%}%>
				
				<p style="font-size: 10px; padding-left: 66px; font-style: italic;"> Last updated by <%=uF.showData((String)alRosterPolicy.get(8), "") %> on <%=uF.showData((String)alRosterPolicy.get(9), "") %></p>
				  
				</li> 
			</ul>
		<% } if(count==0) { %>
			<div class="filter"><div class="msg nodata"><span>No exception rule has been set.</span></div></div>
		<% } %>
     </div>	 
     
     
     
	<div class="col-md-12"> 
	<a href="javascript:void(0);" onclick="addHalfDayRule('<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>');"> <i class="fa fa-plus-circle" aria-hidden="true"></i> Add New Half day Rule</a>
	</div>
	
	<div class="col-md-12">
		<% 
			Set setRosterHDPolicyMap = hmRosterHDPolicyReport.keySet();
			it = setRosterHDPolicyMap.iterator();
			count=0;
			while(it.hasNext()){
				String strRosterHDPolicyId = (String)it.next();
				List alRosterHDPolicy = (List)hmRosterHDPolicyReport.get(strRosterHDPolicyId);
				count++;
			%>
				<ul class="level_list">
					<li>
					<%if(uF.parseToInt((String)alRosterHDPolicy.get(6))==-1){ %>
						<div style="float:left;padding-right:8px;" id="myDivHD<%=count%>"><a href="javascript:void(0)" onclick="getContent('myDivHD<%=count%>', 'UpdateRequest.action?S=1&T=RPHD&RID=<%=(String)alRosterHDPolicy.get(0)%>')"><!-- <img src="images1/icons/denied.png" title="Disabled. Click to enable this policy" /> --><i class="fa fa-circle" aria-hidden="true" title="Disabled. Click to enable this policy" style="color:#e22d25"></i></a></div>
					<%}else if(uF.parseToInt((String)alRosterHDPolicy.get(6))==1){ %>
						<div style="float:left;padding-right:8px;" id="myDivHD<%=count%>"><a href="javascript:void(0)" onclick="getContent('myDivHD<%=count%>', 'UpdateRequest.action?S=-1&T=RPHD&RID=<%=(String)alRosterHDPolicy.get(0)%>')"><!-- <img src="images1/icons/approved.png" title="Enabled. Click to disable this policy" />  --><i class="fa fa-circle" title="Enabled. Click to disable this policy" aria-hidden="true" style="color:#54aa0d"></i></a></div>
					<%} %>
					<a href="RosterPolicyHD.action?orgId=<%=request.getAttribute("strOrg") %>&strWlocation=<%=request.getAttribute("strWLocation") %>&operation=D&ID=<%=strRosterHDPolicyId%>&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>" onclick="return confirm('Are you sure you wish to delete this rule?')" style="color: red;"><i class="fa fa-trash" aria-hidden="true"></i></a> 
					<a href="javascript:void(0);" onclick="editHalfDayRule('<%=strRosterHDPolicyId%>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
					 
					Anyone who <strong><%=(("IN".equalsIgnoreCase((String)alRosterHDPolicy.get(2)))?"comes late":"leaves early") %></strong> by <strong><%=alRosterHDPolicy.get(1) %></strong> mins  every <strong><%=alRosterHDPolicy.get(3)%> days</strong> in <strong><%=alRosterHDPolicy.get(4)%> months </strong> will be considered as <strong>unpaid half day(absent)</strong> and is effective from <strong><%=alRosterHDPolicy.get(5) %></strong>
					<p style="font-size: 10px; padding-left: 66px; font-style: italic;">	Last updated by <%=uF.showData((String)alRosterHDPolicy.get(7), "") %> on <%=uF.showData((String)alRosterHDPolicy.get(8), "") %></p>
					  
					</li> 
				</ul>
		<% } if(count==0) { %>
			<div class="filter"><div class="msg nodata"><span>No halfday policy has been set.</span></div></div>
		<% } %>
     </div>	
     
     

	<div class="col-md-12"> 
		<a href="javascript:void(0);" onclick="addFullDayRule('<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>');"> <i class="fa fa-plus-circle" aria-hidden="true"></i> Add New Full day Rule</a>
	</div>
	
	<div class="col-md-12">
		<% 
			Set setRosterFDPolicyMap = hmRosterFDPolicyReport.keySet();
			it = setRosterFDPolicyMap.iterator();
			count=0;
			while(it.hasNext()){
				String strRosterFDPolicyId = (String)it.next();
				List alRosterFDPolicy = (List)hmRosterFDPolicyReport.get(strRosterFDPolicyId);
				count++;
			%>
				<ul class="level_list">
					<li>
					<%if(uF.parseToInt((String)alRosterFDPolicy.get(6))==-1) { %>
						<div style="float:left;padding-right:8px;" id="myDivFD<%=count%>"><a href="javascript:void(0)" onclick="getContent('myDivFD<%=count%>', 'UpdateRequest.action?S=1&T=RPFD&RID=<%=(String)alRosterFDPolicy.get(0)%>')"><!-- <img src="images1/icons/denied.png" title="Disabled. Click to enable this policy" /> --><i class="fa fa-circle" aria-hidden="true" style="color:#e22d25" title="Disabled. Click to enable this policy"></i></a></div>
					<%} else if(uF.parseToInt((String)alRosterFDPolicy.get(6))==1) { %>
						<div style="float:left;padding-right:8px;" id="myDivFD<%=count%>"><a href="javascript:void(0)" onclick="getContent('myDivFD<%=count%>', 'UpdateRequest.action?S=-1&T=RPFD&RID=<%=(String)alRosterFDPolicy.get(0)%>')"> <i class="fa fa-circle" aria-hidden="true" style="color:#54aa0d" title="Enabled. Click to disable this policy" ></i> <!-- <img src="images1/icons/approved.png" title="Enabled. Click to disable this policy" /> --></a></div>
					<%} %>
					<a href="RosterPolicyFD.action?orgId=<%=request.getAttribute("strOrg") %>&strWlocation=<%=request.getAttribute("strWLocation") %>&operation=D&ID=<%=strRosterFDPolicyId%>&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>" onclick="return confirm('Are you sure you wish to delete this rule?')" style="color: red;"><i class="fa fa-trash" aria-hidden="true"></i></a> 
					<a href="javascript:void(0);" onclick="editFullDayRule('<%=strRosterFDPolicyId%>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
					 
					Anyone who <strong><%=(("IN".equalsIgnoreCase((String)alRosterFDPolicy.get(2)))?"comes late":"leaves early") %></strong> by <strong><%=alRosterFDPolicy.get(1) %></strong> mins  every <strong><%=alRosterFDPolicy.get(3)%> days</strong> in <strong><%=alRosterFDPolicy.get(4)%> months </strong> will be considered as <strong>unpaid full day (absent)</strong> and is effective from <strong><%=alRosterFDPolicy.get(5) %></strong>
					<p style="font-size: 10px; padding-left: 66px; font-style: italic;">	Last updated by <%=uF.showData((String)alRosterFDPolicy.get(7), "") %> on <%=uF.showData((String)alRosterFDPolicy.get(8), "") %></p>
					  
					</li> 
				</ul>
		<% } %>
		<%if(count==0) { %>
			<div class="filter"><div class="msg nodata"><span>No fullday policy has been set.</span></div></div>
		<% } %>
         
     </div>	
     
     
	<div class="col-md-12"> 
		<a href="javascript:void(0);" onclick="addHalfDayFullDayMinHrsRule('<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>', 'HD');"> <i class="fa fa-plus-circle" aria-hidden="true"></i> Add New Half day Exception Rule</a>
	</div>
	
	<div class="col-md-12">
		<% 
			Iterator<String> it1 = hmRosterHDFDMinHrsPolicyReport.keySet().iterator();
			count=0;
			while(it1.hasNext()){
				String strRosterHDFDPolicyId = it1.next();
				List<String> alRosterHDFDMinHrsPolicy = hmRosterHDFDMinHrsPolicyReport.get(strRosterHDFDPolicyId);
				if(alRosterHDFDMinHrsPolicy != null && alRosterHDFDMinHrsPolicy.get(1).equals("HD")) {
					count++;
				%>
					<ul class="level_list">
						<li>
						<%if(uF.parseToInt((String)alRosterHDFDMinHrsPolicy.get(6))==-1) { %>
							<div style="float:left;padding-right:8px;" id="myDivHDMinHrs<%=count%>"><a href="javascript:void(0)" onclick="getContent('myDivHDMinHrs<%=count%>', 'UpdateRequest.action?S=1&T=RPHDFDMinHrs&RID=<%=(String)alRosterHDFDMinHrsPolicy.get(0)%>')"><!-- <img src="images1/icons/denied.png" title="Disabled. Click to enable this policy" /> --><i class="fa fa-circle" aria-hidden="true" style="color:#e22d25" title="Disabled. Click to enable this policy"></i></a></div>
						<% } else if(uF.parseToInt((String)alRosterHDFDMinHrsPolicy.get(6))==1) { %>
							<div style="float:left;padding-right:8px;" id="myDivHDMinHrs<%=count%>"><a href="javascript:void(0)" onclick="getContent('myDivHDMinHrs<%=count%>', 'UpdateRequest.action?S=-1&T=RPHDFDMinHrs&RID=<%=(String)alRosterHDFDMinHrsPolicy.get(0)%>')"> <i class="fa fa-circle" aria-hidden="true" style="color:#54aa0d" title="Enabled. Click to disable this policy" ></i> <!-- <img src="images1/icons/approved.png" title="Enabled. Click to disable this policy" /> --></a></div>
						<% } %>
						<a href="RosterPolicyMinHrsHD_FD.action?orgId=<%=request.getAttribute("strOrg") %>&strWlocation=<%=request.getAttribute("strWLocation") %>&operation=D&ID=<%=strRosterHDFDPolicyId %>&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>" onclick="return confirm('Are you sure you wish to delete this rule?')" style="color: red;"><i class="fa fa-trash" aria-hidden="true"></i></a> 
						<a href="javascript:void(0);" onclick="editHalfDayFullDayMinHrsRule('<%=strRosterHDFDPolicyId %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>', 'HD')"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
						 
						Anyone who works for less than <strong><%=alRosterHDFDMinHrsPolicy.get(2) %></strong> Hrs, half day exception will be generated. If exception will be denied full day salary will be deducted and is effective from <strong><%=alRosterHDFDMinHrsPolicy.get(3) %></strong>
						<p style="font-size: 10px; padding-left: 66px; font-style: italic;">	Last updated by <%=uF.showData((String)alRosterHDFDMinHrsPolicy.get(4), "") %> on <%=uF.showData((String)alRosterHDFDMinHrsPolicy.get(5), "") %></p>
						  
						</li> 
					</ul>
				<% } %>
		<% } %>
		<%if(count==0) { %>
			<div class="filter"><div class="msg nodata"><span>No halfday exception policy has been set.</span></div></div>
		<% } %>
     </div>
     
     
     <div class="col-md-12"> 
		<a href="javascript:void(0);" onclick="addHalfDayFullDayMinHrsRule('<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>', 'FD');"> <i class="fa fa-plus-circle" aria-hidden="true"></i> Add New Full day Exception Rule</a>
	</div>
	
	<div class="col-md-12">
		<% 
			Iterator<String> it2 = hmRosterHDFDMinHrsPolicyReport.keySet().iterator();
			count=0;
			while(it2.hasNext()){
				String strRosterHDFDPolicyId = it2.next();
				List<String> alRosterHDFDMinHrsPolicy = hmRosterHDFDMinHrsPolicyReport.get(strRosterHDFDPolicyId);
				if(alRosterHDFDMinHrsPolicy != null && alRosterHDFDMinHrsPolicy.get(1).equals("FD")) {
					count++;
				%>
					<ul class="level_list">
						<li>
						<%if(uF.parseToInt((String)alRosterHDFDMinHrsPolicy.get(6))==-1) { %>
							<div style="float:left;padding-right:8px;" id="myDivFDMinHrs<%=count%>"><a href="javascript:void(0)" onclick="getContent('myDivFDMinHrs<%=count%>', 'UpdateRequest.action?S=1&T=RPHDFDMinHrs&RID=<%=(String)alRosterHDFDMinHrsPolicy.get(0)%>')"><!-- <img src="images1/icons/denied.png" title="Disabled. Click to enable this policy" /> --><i class="fa fa-circle" aria-hidden="true" style="color:#e22d25" title="Disabled. Click to enable this policy"></i></a></div>
						<% } else if(uF.parseToInt((String)alRosterHDFDMinHrsPolicy.get(6))==1) { %>
							<div style="float:left;padding-right:8px;" id="myDivFDMinHrs<%=count%>"><a href="javascript:void(0)" onclick="getContent('myDivFDMinHrs<%=count%>', 'UpdateRequest.action?S=-1&T=RPHDFDMinHrs&RID=<%=(String)alRosterHDFDMinHrsPolicy.get(0)%>')"> <i class="fa fa-circle" aria-hidden="true" style="color:#54aa0d" title="Enabled. Click to disable this policy" ></i> <!-- <img src="images1/icons/approved.png" title="Enabled. Click to disable this policy" /> --></a></div>
						<% } %>
						<a href="RosterPolicyMinHrsHD_FD.action?orgId=<%=request.getAttribute("strOrg") %>&strWlocation=<%=request.getAttribute("strWLocation") %>&operation=D&ID=<%=strRosterHDFDPolicyId %>&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>" onclick="return confirm('Are you sure you wish to delete this rule?')" style="color: red;"><i class="fa fa-trash" aria-hidden="true"></i></a> 
						<a href="javascript:void(0);" onclick="editHalfDayFullDayMinHrsRule('<%=strRosterHDFDPolicyId %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>', 'FD')"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
						 
						Anyone who works for less than <strong><%=alRosterHDFDMinHrsPolicy.get(2) %></strong> Hrs, full day exception will be generated. If exception will be denied half day salary will be deducted and is effective from <strong><%=alRosterHDFDMinHrsPolicy.get(3) %></strong>
						<p style="font-size: 10px; padding-left: 66px; font-style: italic;">	Last updated by <%=uF.showData((String)alRosterHDFDMinHrsPolicy.get(4), "") %> on <%=uF.showData((String)alRosterHDFDMinHrsPolicy.get(5), "") %></p>
						  
						</li> 
					</ul>
				<% } %>
		<% } %>
		<%if(count==0) { %>
			<div class="filter"><div class="msg nodata"><span>No fullday exception policy has been set.</span></div></div>
		<% } %>
     </div>
	
     
     
		 
		     
<%-- <%if(uF.parseToBoolean((String)request.getAttribute(IConstants.ADD_ACCESS))){ %>     
<div style="float:left; margin:0px 0px 10px 0px"> 
<a href="javascript:void(0);" onclick="addBreakRule();"> + Add New Break Rule</a>
</div>
<%} %>  
<div class="clr"></div>

<div>
         <ul class="level_list">

		
		<% 
			Set setRosterBreakPolicyMap = hmRosterBreakPolicyReport.keySet();
			it = setRosterBreakPolicyMap.iterator();
			count=0;
			while(it.hasNext()){
				String strRosterBreakPolicyId = (String)it.next();
				List alRosterBreakPolicy = (List)hmRosterBreakPolicyReport.get(strRosterBreakPolicyId);
				count++;
					
					
					%>
					
					<li>
					<%if(uF.parseToInt((String)alRosterBreakPolicy.get(6))==-1){ %>
						<div style="float:left;padding:5px;" id="myDivHD<%=count%>"><a href="javascript:void(0)" onclick="getContent('myDivHD<%=count%>', 'UpdateRequest.action?S=1&T=RPHD&RID=<%=(String)alRosterBreakPolicy.get(0)%>')"><img src="images1/icons/denied.png" title="Disabled. Clcik to enable this policy" /></a></div>
					<%}else if(uF.parseToInt((String)alRosterBreakPolicy.get(6))==1){ %>
						<div style="float:left;padding:5px;" id="myDivHD<%=count%>"><a href="javascript:void(0)" onclick="getContent('myDivHD<%=count%>', 'UpdateRequest.action?S=-1&T=RPHD&RID=<%=(String)alRosterBreakPolicy.get(0)%>')"><img src="images1/icons/approved.png" title="Enabled. Clcik to disable this policy" /></a></div>
					<%} %>
					<%if(uF.parseToBoolean((String)request.getAttribute(IConstants.DELETE_ACCESS))){ %>
					<a href="RosterPolicyBreak.action?orgId=<%=request.getAttribute("strOrg") %>&operation=D&ID=<%=strRosterBreakPolicyId%>" class="del" onclick="return confirm('Are you sure you wish to delete this rule?')"> - </a> 
					<%} %>
					<%if(uF.parseToBoolean((String)request.getAttribute(IConstants.UPDATE_ACCESS))){ %>
					<a href="javascript:void(0);" class="edit_lvl" onclick="editBreakRule('<%=strRosterBreakPolicyId%>');">Edit</a>
					<%} %>
					 
					Anyone who <strong><%=(String)alRosterBreakPolicy.get(2) %></strong> by <strong><%=alRosterBreakPolicy.get(1) %></strong> mins  every <strong><%=alRosterBreakPolicy.get(3)%> days</strong> in <strong><%=alRosterBreakPolicy.get(4)%> months </strong> will be deducted <strong><%=alRosterBreakPolicy.get(7) %></strong> and is effective from <strong><%=alRosterBreakPolicy.get(5) %></strong>
					
					
					
					<p style="font-size: 10px; padding-left: 66px; font-style: italic;">	Last updated by <%=uF.showData((String)alRosterBreakPolicy.get(8), "") %> on <%=uF.showData((String)alRosterBreakPolicy.get(9), "") %></p>
					  
					</li> 
					
		<%
			}
			if(count==0){
			%>
			<div class="filter"><div class="msg nodata"><span>No break policy has been set.</span></div></div>
			<%
			}
		%>
		 
		 </ul>
         
     </div>	 --%>
		 
	</div>
	
	
	<div class="modal" id="modalInfo" role="dialog">
	    <div class="modal-dialog">
	        <!-- Modal content-->
	        <div class="modal-content">
	            <div class="modal-header">
	                <button type="button" class="close" data-dismiss="modal">&times;</button>
	                <h4 class="modal-title">-</h4>
	            </div>
	            <div class="modal-body" style="height:400px; overflow-y:auto; padding-left: 25px;">
	            </div>
	            <div class="modal-footer">
	                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
	            </div>
	        </div>
	    </div>
	</div>
