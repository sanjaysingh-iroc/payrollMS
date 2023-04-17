<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@taglib uri="/struts-tags" prefix="s"%>

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

function setInformationDisplay(dataType, userscreen, navigationId, toPage) {

	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Update Information Display Setting');
	$.ajax({
		url : 'SetInformationDisplay.action?dataType='+dataType+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}


</script>

<% 
UtilityFunctions uF = new UtilityFunctions();

Map<String, String> hmInfoDisplay = (Map<String, String>)request.getAttribute("hmInfoDisplay");
if(hmInfoDisplay == null) hmInfoDisplay = new HashMap<String, String>();

String userscreen = (String)request.getAttribute("userscreen");
String navigationId = (String)request.getAttribute("navigationId");
String toPage = (String)request.getAttribute("toPage");

%>

 
<div id="printDiv" class="leftbox reportWidth">

<%=uF.showData((String) session.getAttribute("MESSAGE"), "")%>

<% session.setAttribute("MESSAGE", ""); %>

	<!-- *************************************** Information Display ********************** -->
	<div style="float: left; width: 100%; margin-left: 15px; margin-top: 15px; margin-bottom: 10px; color: #346897; font-size: 16px; font-weight: bolder; text-shadow: 0 1px 2px #FFFFFF;">Information Display Settings</div>
		<%
			String strAttendADCheck = "";
			String strAttendTDCheck = "";
			if(uF.parseToBoolean(hmInfoDisplay.get("IS_ATTEND_FROM_ATTEND_DETAILS"))) {
				strAttendADCheck = "checked";
			}
			if(uF.parseToBoolean(hmInfoDisplay.get("IS_ATTEND_FROM_TIMESHEET_DETAILS"))) {
				strAttendTDCheck = "checked";
			}
		%>
	<div style="float:left; width:100%">
         <ul class="level_list">
			<li>
				Display only Team Assigned: <strong><%=uF.showData(hmInfoDisplay.get("ONLY_TEAM"), "No") %></strong>,&nbsp;&nbsp;&nbsp;
				Display Resource Cost in Project: <strong><%=uF.showData(hmInfoDisplay.get("IS_COST"), "No") %></strong>,&nbsp;&nbsp;&nbsp;
				Display Resource Rate in Project: <strong><%=uF.showData(hmInfoDisplay.get("IS_RATE"), "No") %></strong>&nbsp;&nbsp;&nbsp;
				<a href="javascript:void(0);" class="fa fa-edit" onclick="setInformationDisplay('InfoDisp', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')">&nbsp;</a>
			</li>	
			<li>Snapshot Time Frequency: <strong><%=uF.showData(hmInfoDisplay.get("SNAPSHOT_TIME"), "0.0 mns") %></strong>&nbsp;&nbsp;&nbsp;
			<a href="javascript:void(0);" class="fa fa-edit" onclick="setInformationDisplay('SnapTime', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')">&nbsp;</a></li>
			
			<li>Attendance data from attendance details: <input type="radio" <%=strAttendADCheck %> disabled="disabled">&nbsp;&nbsp; or &nbsp;&nbsp;
				Attendance data from timesheet details: <input type="radio" <%=strAttendTDCheck %> disabled="disabled">&nbsp;&nbsp;&nbsp;
			<a href="javascript:void(0);" class="fa fa-edit" onclick="setInformationDisplay('Attendance', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')">&nbsp;</a></li>	
		
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
