<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.*"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%> 
<script type="text/javascript" src="scripts/customAjax.js"></script>
<script type="text/javascript">     
    $(function(){
        getLeaveDateStatus('2');
        var date_yest = new Date();
	    var date_tom = new Date();
	    date_yest.setHours(0,0,0);
	    date_tom.setHours(23,59,59); 
	    var shiftStartTimeMoment = date_yest;
	    var shiftEndTimeMoment = date_tom;
	 $('#travelToTime').datetimepicker({
	    	format: 'HH:mm',
	    	defaultDate: date_yest
	    }).on('dp.change', function(e){ 
	    	shiftStartTimeMoment = e.date._d;
	    	if(new Date(shiftStartTimeMoment).getTime() > new Date(shiftEndTimeMoment).getTime()){
	    		shiftEndTimeMoment.setDate(new Date(shiftEndTimeMoment).getDate()+1);
	    	}
	    	$('#breakEndTime').data("DateTimePicker").clear();
	        $('#breakStartTime').data("DateTimePicker").clear();
	        $('#breakStartTime').data("DateTimePicker").defaultDate(shiftStartTimeMoment);
	        $('#breakEndTime').data("DateTimePicker").defaultDate(shiftStartTimeMoment);		
	    });
	    $('#travelFromTime').datetimepicker({
	    	format: 'HH:mm',
	    	defaultDate: date_yest
	    }).on('dp.change', function(e){ 
	    	shiftStartTimeMoment = e.date._d;
	    	if(new Date(shiftStartTimeMoment).getTime() > new Date(shiftEndTimeMoment).getTime()){
	    		shiftEndTimeMoment.setDate(new Date(shiftEndTimeMoment).getDate()+1);
	    	}
	    	$('#breakEndTime').data("DateTimePicker").clear();
	        $('#breakStartTime').data("DateTimePicker").clear();
	        $('#breakStartTime').data("DateTimePicker").defaultDate(shiftStartTimeMoment);
	        $('#breakEndTime').data("DateTimePicker").defaultDate(shiftStartTimeMoment);		
	    });
	 
    });
    
    function toggleSession() {
    	/* if(obj.checked){ */
    	if(document.getElementById("isHalfDay") && document.getElementById("isHalfDay").checked) {
    		document.getElementById("idSession").style.display="block";
    		document.getElementById("idLeaveTo").style.display="none";
    	} else {
    		document.getElementById("idSession").style.display="none";
    		document.getElementById("idLeaveTo").style.display="table-row";
    	}
    }
    
	function concierge(obj){
    	if(obj.checked){
    		document.getElementById("trModeTravel").style.display="table-row";
    		document.getElementById("trBooking").style.display="table-row";
    		document.getElementById("trAccommodation").style.display="table-row";
    	}else{
    		document.getElementById("trModeTravel").style.display="none";
    		document.getElementById("trBookingDetails").style.display="none";
    		document.frmApplyTravel.isBooking.checked=false;
    		document.frmApplyTravel.isAccommodation.checked=false;
    		document.getElementById("trAccommodationDetails").style.display="none";
    		document.getElementById("trBooking").style.display="none";
    		document.getElementById("trAccommodation").style.display="none";
    	}
    	booking(document.frmApplyTravel.isBooking);
    }
	
	function booking(obj){
		if(obj.checked){
    		document.getElementById("trBookingDetails").style.display="table-row";
    	}else{
    		document.getElementById("trBookingDetails").style.display="none";
    	}
		accommodation(document.frmApplyTravel.isAccommodation);
	}

	function accommodation(obj){
		if(obj.checked){
    		document.getElementById("trAccommodationDetails").style.display="table-row";
    	}else{
    		document.getElementById("trAccommodationDetails").style.display="none";
    	}
	}
    
    function getLeaveDateStatus(type){
    	fadeForm('frmApplyTravel');
		var empid='';
		var strSession = "";
		if(type=='1'){
			empid=document.frmApplyTravel.strEmpId.options[document.frmApplyTravel.strEmpId.selectedIndex].value;		
		}
    	var strD1=document.frmApplyTravel.leaveFromTo.value;
    	var strD2=document.frmApplyTravel.leaveToDate.value;
    	if(document.getElementById("isHalfDay")) {
    		if(document.getElementById("isHalfDay") && document.getElementById("isHalfDay").checked==true){
        		strD2=document.frmApplyTravel.leaveFromTo.value;
        		
        		var ele = document.getElementsByName("strSession");
    			for(var i=0; i < ele.length; i++) {
    				if(ele[i].checked) {
    					strSession = ele[i].value;
    				}
    			}
        	}
    	}
    	
		var action='GetEmpTravelPolicyDetails.action?empid='+empid+'&strD1='+strD1+'&strD2='+strD2+'&strSession='+strSession;
		$.ajax({
 			url : action,
 			success : function(result) {
 				//console.log("result==>"+result);
 				//document.getElementById("policyid").innerHTML = result;
 				result = $.parseHTML(result.trim());
    			 $( "#policyid" ).nextAll().remove();
    			$("#policyid").after(result);
    			unfadeForm('frmApplyTravel'); 
 			}
 		});
	}
    
    
	$("#frmApplyTravel").submit(function(e){
		e.preventDefault();
		var form_data = $("form[name='frmApplyTravel']").serialize();
		$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			url : "ApplyTravel.action",
			data: form_data,
			cache : false/* ,
			success : function(res) {
				$("#divResult").html(res);
			} */
		});
		
		$.ajax({ 
			url: 'EmployeeLeaveEntryReport.action',
			cache: true,
			success: function(result){
				$("#divResult").html(result);
	   		}
		});
		
	});

//toggleSession(document.frmApplyTravel.isHalfDay);
toggleSession();
concierge(document.frmApplyTravel.isConcierge);
</script>

<%
	String strUserType = (String) session.getAttribute(IConstants.USERTYPE);
	UtilityFunctions uF = new UtilityFunctions();
	String strTitle = ((request.getParameter("E") != null) ? "Edit " : "Apply ") + "Travel";
%>

<%
	String strType = (String) request.getParameter("type");
	String strEmpType = (String) session.getAttribute("USERTYPE");
	//String strEmpID = (String) session.getAttribute(IConstants.EMPID);
	String strMessage = (String) request.getAttribute("MESSAGE");
	if (strMessage == null) {
		strMessage = "";
	}
	String strIsHalfDayLeave = (String) request.getAttribute("strIsHalfDayLeave");
%>


<% if (strType == null) { %>


<% } %>

<div class="leftbox reportWidth">
	<p class="message"><%=strMessage%></p>
	<%=uF.showData((String) session.getAttribute(IConstants.MESSAGE), "") %>
	<s:form id="frmApplyTravel" name="frmApplyTravel" theme="simple" action="ApplyTravel" method="POST" cssClass="formcss" enctype="multipart/form-data">
		<table style="float: left;" class="travel table table_no_border">
			<s:hidden name="leaveId" />
			<s:hidden name="entrydate" />
			<s:hidden name="empId" required="true" />
			<s:hidden name="type" />

			<%
				if (strEmpType != null && (strEmpType.equalsIgnoreCase(IConstants.ADMIN) || strEmpType.equalsIgnoreCase(IConstants.CEO)
					|| strEmpType.equalsIgnoreCase(IConstants.CFO) || strEmpType.equalsIgnoreCase(IConstants.MANAGER) || strEmpType.equalsIgnoreCase(IConstants.HRMANAGER))) {
			%>
			<tr>
				<td class="txtlabel alignRight">Select Emp Name:<sup>*</sup></td>
				<td><s:select cssClass="validateRequired" name="strEmpId" listKey="employeeId" listValue="employeeName" headerKey="" headerValue="Select Employee" list="empList" /> </td>
			</tr>
			<% } else { %>
			<s:hidden name="empId" required="true" />
			<tr style="display: none;">
				<td class="txtlabel alignRight">Emp Name:<sup>*</sup></td>
				<td><s:label name="empName" label="Emp Name" /></td>
			</tr>
			<% } %>
			<tr>
				<td class="txtlabel alignRight">Plan Name:<sup>*</sup></td>
				<td><input type="text" name="planName" class="validateRequired"></input><span class="hint">Enter Travel Plan Name.<span class="hint-pointer">&nbsp;</span></span>
				</td>
			</tr>

			<% if (uF.parseToBoolean(strIsHalfDayLeave)) { %>
			<tr>
				<td class="txtlabel alignRight" valign="top">Half day:</td>
				<td height=50 valign="top"><s:checkbox name="isHalfDay" id="isHalfDay" onclick="toggleSession()" cssStyle="float:left" />
					<div id="idSession">
						<s:radio name="strSession" list="strWorkingSession" listKey="strHaldDayId" listValue="strHaldDayName" />
					</div>
				</td>
			</tr>
			<% } %>

			<%
				if (strEmpType != null && (strEmpType.equalsIgnoreCase(IConstants.ADMIN) || strEmpType.equalsIgnoreCase(IConstants.CEO)
					|| strEmpType.equalsIgnoreCase(IConstants.CFO) || strEmpType.equalsIgnoreCase(IConstants.MANAGER) || strEmpType.equalsIgnoreCase(IConstants.HRMANAGER))) {
			%>
			<tr>
				<td class="txtlabel alignRight">Travel From Date:<sup>*</sup></td>
				<td>
					<input type="text" class="validateRequired" id="leaveFromTo" name="leaveFromTo"></input><span class="hint">On Leave From Date.<span class="hint-pointer">&nbsp;</span></span>
				</td>
			</tr>
			<tr id="idLeaveTo">
				<td class="txtlabel alignRight">Travel To Date:<sup>*</sup></td>
				<td><input type="text" class="validateRequired" id="leaveToDate" name="leaveToDate"></input><span class="hint">Leave End Date.<span class="hint-pointer">&nbsp;</span></span>
				</td>
			</tr>
			<% } else { %>
			<tr>
				<td class="txtlabel alignRight">Travel From Date:<sup>*</sup></td>
				<td><input type="text" class="validateRequired" id="leaveFromTo" name="leaveFromTo"></input><span class="hint">On Leave From Date.<span class="hint-pointer">&nbsp;</span></span></td>
			</tr>
			<tr id="idLeaveTo">
				<td class="txtlabel alignRight">Travel To Date:<sup>*</sup>
				</td>
				<td><input type="text" class="validateRequired" id="leaveToDate" name="leaveToDate"></input><span class="hint">Leave End Date.<span class="hint-pointer">&nbsp;</span></span></td>
			</tr>
			<% } %>
			 <tr>
				<td class="txtlabel alignRight">From Time:<sup>*</sup></td>
				<td>
					<s:textfield cssClass="validateRequired" id="travelFromTime" name="travelFromTime"  required="true"></s:textfield>
				 	<p class="hint"> start Time.<span class="hint-pointer">&nbsp;</span></p>
				</td>
			</tr>
			<tr>
				<td class="txtlabel alignRight">To Time:<sup>*</sup></td>
				<td>
					<s:textfield cssClass="validateRequired" id="travelToTime" name="travelToTime"  required="true"></s:textfield>
				 	<p class="hint"> End Time.<span class="hint-pointer">&nbsp;</span></p>
				</td>
			</tr>
			
			<tr>
				<td class="txtlabel alignRight">Place From:<sup>*</sup></td> 
				<td><s:textfield cssClass="validateRequired" name="placeFrom"></s:textfield></td>
			</tr>
			
			<tr>
				<td class="txtlabel alignRight">Place To:<sup>*</sup></td>
				<td><s:textfield cssClass="validateRequired" name="destinations"></s:textfield></td>
			</tr>


			<tr>
				<td class="txtlabel alignRight">Advance, if any:</td>
				<td class="label">
					<%
						if (uF.parseToBoolean((String) request.getAttribute("sbEligible")) || (strUserType != null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE)
							&& !strUserType.equalsIgnoreCase(IConstants.CONSULTANT) && !strUserType.equalsIgnoreCase(IConstants.ARTICLE))) {
					%>
					<s:textfield name="travelAdvance" onkeypress="return isNumberKey(event);" cssClass="validateRequired"></s:textfield>
					<p><%=uF.showData((String) request.getAttribute("sb"), "")%></p>
					<% } else { %>
						<font color="red">You are not eligible for travel advance</font> 
					<% } %>
					<span class="hint">Enter advance, if you require any.<span class="hint-pointer">&nbsp;</span></span>
				</td>
			</tr>

			<tr>
				<td class="txtlabel alignRight">Are you interested in Concierge Service:</td>
				<td><s:checkbox name="isConcierge" id="isConcierge" onclick="concierge(this);" /></td>
			</tr>

			<tr id="trModeTravel" style="display: none;">
				<td class="txtlabel alignRight" valign="top">Mode of Travel:<sup>*</sup></td>
				<td><s:select theme="simple" cssClass="validateRequired" name="modeOfTravel" list="#{'1':'Air','2':'Rail','3':'Taxi','4':'Bus'}" multiple="true" cssStyle="width: 65px;" /></td>
			</tr>

			<tr id="trBooking" style="display: none;">
				<td class="txtlabel alignRight">Do you need Booking?:</td>
				<td><s:checkbox name="isBooking" id="isBooking" onclick="booking(this);" /></td>
			</tr>

			<tr id="trBookingDetails" style="display: none;" valign="top">
				<td class="txtlabel alignRight">Booking Details:<sup>*</sup></td>
				<td><s:textarea cssClass="validateRequired" cols="50" rows="05" name="bookingDetails" id="bookingDetails" /></td>
			</tr>

			<tr id="trAccommodation" style="display: none;">
				<td class="txtlabel alignRight">Accommodation Required:<sup>*</sup></td>
				<td><s:checkbox name="isAccommodation" id="isAccommodation" cssClass="validateRequired" onclick="accommodation(this);" /></td>
			</tr>

			<tr id="trAccommodationDetails" style="display: none;" valign="top">
				<td class="txtlabel alignRight">Accommodation Details:</td>
				<td><s:textarea cssClass="validateRequired" cols="50" rows="05" name="accommodationDetails" id="accommodationDetails" /></td>
			</tr>

			<tr id="policyid">
				<td class="txtlabel alignRight" valign="top">Travel Reason:<sup>*</sup></td>
				<td><textarea rows="5" cols="50" class="validateRequired" name="reason"></textarea> </td>
			</tr>
			<tr>
				<td></td>
				<td>
					<input class="btn btn-default" id="submitButton" type="button" value="Apply Travel"/>
				</td>
			</tr>
		</table>
		<div id="myDiv"></div>
	</s:form>
</div>

<script type="text/javascript">
$(function(){
    $('body').on('click',"#submitButton , #submitButton1", function(){
		$("#"+ this.form.id ).find('.validateRequired').filter(':hidden').prop('required',false);
		$("#"+ this.form.id ).find('.validateRequired').filter(':visible').prop('required',true);
	}); 

	$("select[name='modeOfTravel']").multiselect().multiselectfilter();
	 $("#leaveFromTo").datepicker({
	     format: 'dd/mm/yyyy',
	     autoclose: true
	 }).on('changeDate', function (selected) {
	     var minDate = new Date(selected.date.valueOf());
	     $('#leaveToDate').datepicker('setStartDate', minDate);
	     $('#leaveToDate').datepicker('setDate', minDate);
	     <%
			if (strEmpType != null && (strEmpType.equalsIgnoreCase(IConstants.ADMIN) || strEmpType.equalsIgnoreCase(IConstants.CEO)
				|| strEmpType.equalsIgnoreCase(IConstants.CFO) || strEmpType.equalsIgnoreCase(IConstants.MANAGER) || strEmpType.equalsIgnoreCase(IConstants.HRMANAGER))) {
			%> 
				getLeaveDateStatus('1');
			<% }else{%>
			getLeaveDateStatus('2');
			<%}%>
	 });
	 
	 $("#leaveToDate").datepicker({
	 	format: 'dd/mm/yyyy',
	 	autoclose: true
	 }).on('changeDate', function (selected) {
         var minDate = new Date(selected.date.valueOf());
         $('#leaveFromTo').datepicker('setEndDate', minDate);
         <%
			if (strEmpType != null && (strEmpType.equalsIgnoreCase(IConstants.ADMIN) || strEmpType.equalsIgnoreCase(IConstants.CEO)
				|| strEmpType.equalsIgnoreCase(IConstants.CFO) || strEmpType.equalsIgnoreCase(IConstants.MANAGER) || strEmpType.equalsIgnoreCase(IConstants.HRMANAGER))) {
			%> 
				getLeaveDateStatus('1');
			<% }else{%>
			getLeaveDateStatus('2');
			<%}%>
	 });

	/* if(document.getElementById("isHalfDay")){
		toggleSession(document.frmLeave.isHalfDay);
	} */
	//concierge(document.frmLeave.isConcierge);
}); 
</script>

