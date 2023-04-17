<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%-- <%@ page buffer = "16kb" %> --%>
<style>
.fa {
padding-right: 0px;
}
</style>
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
</script>

<% 
	UtilityFunctions uF = new UtilityFunctions();
	
	Map<String, String> hmFeatureStatus = (Map<String, String>)request.getAttribute("hmFeatureStatus");
	if(hmFeatureStatus == null) hmFeatureStatus = new HashMap<String, String>();
	Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>)request.getAttribute("hmFeatureUserTypeId");
	if(hmFeatureUserTypeId == null) hmFeatureUserTypeId = new HashMap<String, List<String>>();
	
	String strUsertypeId = (String)session.getAttribute(IConstants.BASEUSERTYPEID);
	
	String userscreen = (String)request.getAttribute("userscreen");
	String navigationId = (String)request.getAttribute("navigationId");
	String toPage = (String)request.getAttribute("toPage");
%>

<script type="text/javascript" charset="utf-8">
		
$(document).ready( function () {
	<%-- $('#lt').dataTable({ bJQueryUI: true, 
		"sPaginationType": "full_numbers",
		"iDisplayLength": 25,
		"aLengthMenu": [
		                [1, 2, -1],
		                [1, 2, "All"]
		            ],
		"aaSorting": [[0, 'asc']],
		/* "sDom": '<"H"lTf>rt<"F"ip>', */
		"sDom": '<"H"f>rt<"F"ip>',
		oTableTools: { "sSwfPath": "<%=request.getContextPath()%>/media/copy_cvs_xls_pdf.swf",
		aButtons: [
			"csv", "xls", {
				sExtends: "pdf",
				sPdfOrientation: "landscape"
				//sPdfMessage: "Your custom message would go here."
				}, "print" 
		]
	}
	});


	$('#lt1').dataTable({ bJQueryUI: true, 
		"sPaginationType": "full_numbers",
		"iDisplayLength": 25,
		"aLengthMenu": [
		                [1, 2, -1],
		                [1, 2, "All"]
		            ],
		"aaSorting": [[0, 'asc']],
		/* "sDom": '<"H"lTf>rt<"F"ip>', */
		"sDom": '<"H"f>rt<"F"ip>',
		oTableTools: { "sSwfPath": "<%=request.getContextPath()%>/media/copy_cvs_xls_pdf.swf",
			aButtons: [
				"csv", "xls", {
					sExtends: "pdf",
					sPdfOrientation: "landscape"
					//sPdfMessage: "Your custom message would go here."
					}, "print" 
			]
		}
	}); --%>
	$('#lt').DataTable();
	$('#lt1').DataTable();
});
		
		
</script>

<script type="text/javascript" charset="utf-8">

function addShift(userscreen, navigationId, toPage) {
	
	var strOrg = document.getElementById("strOrg").value;
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Add Shift');
	$.ajax({
		url : 'AddShiftRoster.action?strOrg='+strOrg+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage, 
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
} 

function editShift(strId, userscreen, navigationId, toPage) {

	var strOrg = document.getElementById("strOrg").value;
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Edit Shift');
	$.ajax({
		url : 'AddShiftRoster.action?operation=E&ID='+strId+'&strOrg='+strOrg+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage, 
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
} 

function addWeeklyOff(userscreen, navigationId, toPage) {
	var strOrg = document.getElementById("strOrg").value;
	
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Add Weekly OFF');
	$.ajax({
		url : 'AddRosterWeeklyOff.action?strOrg='+strOrg+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage, 
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

function editWeeklyOff(strId, userscreen, navigationId, toPage) {

	var strOrg = document.getElementById("strOrg").value;
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Edit Weekly OFF');
	$.ajax({
		url : 'AddRosterWeeklyOff.action?operation=E&ID='+strId+'&strOrg='+strOrg+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage, 
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
} 


function addRosterPolicyRules(userscreen, navigationId, toPage) {
	var strOrg = document.getElementById("strOrg").value;
	
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	var height = $(window).height()* 0.90;
	var width = $(window).width()* 0.90;
	$(".modal-dialog").css("height", height);
	$(".modal-dialog").css("width", width);
	$(".modal-dialog").css("max-height", height);
	$(".modal-dialog").css("max-width", width);
	$('.modal-title').html('Add Roster Policy Rule');
	$.ajax({
		url : 'AddRosterPolicyRules.action?strOrg='+strOrg+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage, 
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}


function editRosterPolicyRules(strId, userscreen, navigationId, toPage) {

	var strOrg = document.getElementById("strOrg").value;
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	var height = $(window).height()* 0.90;
	var width = $(window).width()* 0.90;
	$(".modal-dialog").css("height", height);
	$(".modal-dialog").css("width", width);
	$(".modal-dialog").css("max-height", height);
	$(".modal-dialog").css("max-width", width);
	$('.modal-title').html('Edit Roster Policy Rule');
	$.ajax({
		url : 'AddRosterPolicyRules.action?operation=E&ID='+strId+'&strOrg='+strOrg+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage, 
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}


function previewRosterPolicyRules(strId, userscreen, navigationId, toPage) {

	var strOrg = document.getElementById("strOrg").value;
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	var height = $(window).height()* 0.90;
	var width = $(window).width()* 0.90;
	$(".modal-dialog").css("height", height);
	$(".modal-dialog").css("width", width);
	$(".modal-dialog").css("max-height", height);
	$(".modal-dialog").css("max-width", width);
	$('.modal-title').html('Preview of Roster Policy Rule');
	$.ajax({
		url : 'AddRosterPolicyRules.action?operation=PREVIEW&ID='+strId+'&strOrg='+strOrg+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage, 
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}


</script>


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
							<% if(hmFeatureStatus.get(IConstants.F_LOGIN_USER_ORG_IN_FILTER) != null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_LOGIN_USER_ORG_IN_FILTER)) && hmFeatureUserTypeId.get(IConstants.F_LOGIN_USER_ORG_IN_FILTER).contains(strUsertypeId)) { %>
								<s:select list="orgList" name="strOrg" id="strOrg" listKey="orgId" listValue="orgName" onchange="document.frm.submit();"></s:select>
							<% } else { %>
								<s:select list="orgList" name="strOrg" id="strOrg" listKey="orgId" listValue="orgName" onchange="document.frm.submit();"></s:select>
							<% } %>
						</div>
					</div>
				</div>
			</s:form>
		</div>
	</div>
	
	<%=uF.showData((String) session.getAttribute("MESSAGE"), "")%>
	<% session.setAttribute("MESSAGE", ""); %>
		

	<div class="col-md-12 col_no_padding">
		<input type="button" class="btn btn-primary" value="Add New Shift" onclick="addShift('<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>');" style="float:right;margin-bottom:10px;"/>
		<div style="clear: both;"></div>
		<table class="table table-bordered" id="lt">
			<thead>
				<tr>
					<th style="text-align: left;">Shift Code</th>
					<th style="text-align: left;">Shift Type </th>
					<th style="text-align: left;">Shift Start Time</th>
					<th style="text-align: left;">Shift End Time</th>
					<th style="text-align: left;">Break Start Time</th>
					<th style="text-align: left;">Break End Time</th>
					<th style="text-align: left;">Total Shift Hours</th>
					<th style="text-align: left;">Colour Code</th>
					<th>Action</th>
				</tr>
			</thead>
			<tbody>
			<% List<List<String>> couterlist = (List<List<String>>)request.getAttribute("reportList"); %>
			 <% for (int i=0; couterlist!=null && i<couterlist.size(); i++) { %>
			 <% List<String> cinnerlist = (List<String>)couterlist.get(i); %>
				<tr id = <%=cinnerlist.get(0) %> >
					<td><%=cinnerlist.get(1) %></td>
					<td><%=cinnerlist.get(2) %></td>
					<td class="alignRight"><%=cinnerlist.get(3) %></td>
					<td class="alignRight"><%=cinnerlist.get(4) %></td>
					<td class="alignRight"><%=cinnerlist.get(5) %></td>
					<td class="alignRight"><%=cinnerlist.get(6) %></td>
					<td class="alignRight"><%=cinnerlist.get(7) %></td>
					<td><%=cinnerlist.get(8) %></td>
					<td>
						<a href="AddShiftRoster.action?operation=D&ID=<%=cinnerlist.get(0) %>&strOrg=<%=cinnerlist.get(9) %>&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>" onclick="return confirm('Are you sure you wish to delete this shift policy?')" style="color:rgb(233,0,0)"> <i class="fa fa-trash" aria-hidden="true"></i> </a>
						<a href="javascript:void(0)" onclick="editShift('<%=cinnerlist.get(0) %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')" title="Edit Shift"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
					</td>
				</tr>
				<% } %>
			</tbody>
		</table>
	</div>
	
	<div class="col-md-12 col_no_padding" style="margin-top: 25px;">
	
	<input type="button" class="btn btn-primary" value="Add Roster Weekly Off" onclick="addWeeklyOff('<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>');" style="float:right;margin-bottom:10px;"/>	
		<%
			List<Map<String, String>> rosterWeeklyoffList = (List<Map<String, String>>) request.getAttribute("rosterWeeklyoffList");
			if(rosterWeeklyoffList == null) rosterWeeklyoffList = new ArrayList<Map<String, String>>();
		%>	
		<div style="clear: both;"></div>
			<table class="table table-bordered" id="lt1">
				<thead>	
					<tr>
						<th>Weekly Off</th>
						<th>Weekly Off Type</th>
						<th>Weekly Off Day</th>
						<th>Weeks</th>
						<th>Entry Date</th>
						<th>Added By</th>
						<th>Action</th>
					</tr>
				</thead>
				<tbody>
					<%
						for(int i=0; i<rosterWeeklyoffList.size(); i++) {
							Map<String, String> hmInner = (Map<String, String>) rosterWeeklyoffList.get(i); 
					%>
						<tr id="<%=hmInner.get("ROSTER_WEEKLYOFF_ID") %>">
							<td><%=hmInner.get("WEEKLYOFF_NAME") %></td>
							<td><%=hmInner.get("WEEKLYOFF_TYPE") %></td>
							<td><%=hmInner.get("WEEKLYOFF_DAY") %></td>
							<td><%=hmInner.get("WEEKLYOFF_WEEKNO") %></td>
							<td><%=hmInner.get("ENTRY_DATE") %></td>
							<td><%=hmInner.get("ADDED_BY") %></td>
							<td>
								<a href="AddRosterWeeklyOff.action?operation=D&ID=<%=hmInner.get("ROSTER_WEEKLYOFF_ID") %>&strOrg=<%=hmInner.get("ORG_ID") %>&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>" onclick="return confirm('Are you sure you wish to delete this Weekly Off policy?')" style="color:rgb(233,0,0)"> <i class="fa fa-trash" aria-hidden="true"></i> </a>
								<a href="javascript:void(0)" onclick="editWeeklyOff('<%=hmInner.get("ROSTER_WEEKLYOFF_ID") %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')" title="Edit Roster Weekly OFF"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
							</td>
						</tr>
					<%} %>
				</tbody>
			</table>
		</div>
		
		
		<div class="col-md-12 col_no_padding" style="margin-top: 25px;">
	
			<input type="button" class="btn btn-primary" value="Add Roster Policy Rules" onclick="addRosterPolicyRules('<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>');" style="float:right;margin-bottom:10px;"/>	
			<%
				List<Map<String, String>> rosterPolicyRulesList = (List<Map<String, String>>) request.getAttribute("rosterPolicyRulesList");
				if(rosterPolicyRulesList == null) rosterPolicyRulesList = new ArrayList<Map<String, String>>();
			%>	
			<div style="clear: both;"></div>
				<table class="table table-bordered" id="lt1">
					<thead>	
						<tr>
							<th>Rule Name</th>
							<th>Action</th>
						</tr>
					</thead>
					<tbody>
						<%
							for(int i = 0; i < rosterPolicyRulesList.size(); i++){
								List<String> alInner = (List<String>) rosterPolicyRulesList.get(i); 
						%>
							<tr id="<%=alInner.get(1) %>">
								<td><%=alInner.get(0) %></td>
								<td>
									<a href="AddRosterPolicyRules.action?operation=D&ID=<%=alInner.get(1) %>&strOrg=<%=alInner.get(2) %>&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>" onclick="return confirm('Are you sure you wish to delete this Roster policy rule?')" style="color:rgb(233,0,0)"> <i class="fa fa-trash" aria-hidden="true"></i> </a>
									<a href="javascript:void(0)" onclick="editRosterPolicyRules('<%=alInner.get(1) %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')" title="Edit Roster Policy Rule"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
									<a href="javascript:void(0)" onclick="previewRosterPolicyRules('<%=alInner.get(1) %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')" title="Preview Roster Policy Rule"><i class="fa fa-eye" aria-hidden="true"></i></a>
								</td>
								
							</tr>
						<%} %>
					</tbody>
				</table>
			</div>

<%-- 	<script type="text/javascript" >
	
	
	Highcharts.setOptions({
	    colors: [<%=request.getAttribute("sbColour")%>]
	});
	
	
	
	var chartAttendance;
	var chartAttendance1;
	$(document).ready(function() {
		
		chartAttendance = new Highcharts.Chart({
	   		
	      chart: {
	         renderTo: 'container_shift',
	        	type: 'area',
	        	spacingBottom:30
	      },
	      title: {
	         text: 'Shift Report'
	      },
	      subtitle:{
	    	  text:'Shift Time',
	    	  floating:true,
	    	  align:'right',
	    	  verticalAlign:'bottom',
	    	  y:15
	      },
	      legend:{
	    	layout:'vertical',
	    	align:'left',
	    	verticalAlign:'top',
	    	x:150,
	    	y:150,
	    	floating:true,
	    	borderWidth:1,
	    	backgroundColor:'#ffffff'
	      },
	      xAxis: {
	         categories: ['0','1','2','3','4','5','6','7','8','9','10','11','12','13','14','15','16','17','18','19','20','21','22','23','24']
	      },
	      yAxis: {
	        
	         title: {
	            text: 'No.of Employes'
	         },
	      	ladels:{
	      	formatter:function(){
	      		return this.value;
	      	}	
	      	}
	      },
	      tooltip:{
	    	formatter:function(){
	    		return'<b>'+this.series.name+'</b><br>'+this.x+':'+this.y;
	    	}  
	      },
	      plotOptions: {
	       area:{
	    	   filopacity:0.5
	       }
	      },
	      credits:{
	    	  enabled:false
	      },
	     series: [<%=((String)request.getAttribute("shiftGraphList"))%>]
	   });
		
	});
		
	</script>
	
	<div id="clockcontainer">
				<p class="past">Employee Shift Summary</p>
				<div id="container_shift" style="height: 300px; width:95%; margin:10px 0 0 0px;"></div>
	</div> --%>

</div>

<div id="addShiftId"></div>
<div id="editShiftId"></div>
<div id="addWeeklyOffid"></div>
<div id="editWeeklyOffid"></div>
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
