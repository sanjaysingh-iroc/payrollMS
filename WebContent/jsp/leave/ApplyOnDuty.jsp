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
	    });
	    $('#travelFromTime').datetimepicker({
	    	format: 'HH:mm',
	    	defaultDate: date_yest
	    });
    });
    
    
    function getLeaveDateStatus(type){
    	fadeForm('frmApplyOnDuty');
		var empid='';
		var strSession = "";
		if(type=='1'){
			empid=document.frmApplyOnDuty.strEmpId.options[document.frmApplyOnDuty.strEmpId.selectedIndex].value;		
		}
    	var strD1=document.frmApplyOnDuty.leaveFromTo.value;
		var action='GetEmpTravelPolicyDetails.action?empid='+empid+'&strD1='+strD1+'&travelType=OD';
		$.ajax({
 			url : action,
 			success : function(result) {
 				console.log("result==>"+result);
 				//document.getElementById("policyid").innerHTML = result;
 				result = $.parseHTML(result.trim());
    			 $( "#policyid" ).nextAll().remove();
    			$("#policyid").after(result);
    			unfadeForm('frmApplyOnDuty'); 
 			}
 		});
	}
    
    
	$("#frmApplyOnDuty").submit(function(e){
		e.preventDefault();
		var form_data = $("form[name='frmApplyOnDuty']").serialize();
		$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			url : "ApplyOnDuty.action",
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

</script>

<%
	String strUserType = (String) session.getAttribute(IConstants.USERTYPE);
	UtilityFunctions uF = new UtilityFunctions();
	String strTitle = ((request.getParameter("E") != null) ? "Edit " : "Apply ") + "On Duty";
%>

<%
	String strType = (String) request.getParameter("type");
	String strEmpType = (String) session.getAttribute("USERTYPE");
	//String strEmpID = (String) session.getAttribute(IConstants.EMPID);
	String strMessage = (String) request.getAttribute("MESSAGE");
	if (strMessage == null) {
		strMessage = "";
	}
%>



<div class="leftbox reportWidth">
	<p class="message"><%=strMessage%></p>
	<%=uF.showData((String) session.getAttribute(IConstants.MESSAGE), "") %>
	<s:form id="frmApplyOnDuty" name="frmApplyOnDuty" theme="simple" action="ApplyOnDuty" method="POST" cssClass="formcss" enctype="multipart/form-data">
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
				<td class="txtlabel alignRight">Title:<sup>*</sup></td>
				<td><input type="text" name="planName" class="validateRequired"></input><span class="hint">Enter On Duty Name.<span class="hint-pointer">&nbsp;</span></span>
				</td>
			</tr>

			<%
				if (strEmpType != null && (strEmpType.equalsIgnoreCase(IConstants.ADMIN) || strEmpType.equalsIgnoreCase(IConstants.CEO)
					|| strEmpType.equalsIgnoreCase(IConstants.CFO) || strEmpType.equalsIgnoreCase(IConstants.MANAGER) || strEmpType.equalsIgnoreCase(IConstants.HRMANAGER))) {
			%>
			<tr>
				<td class="txtlabel alignRight">On Duty Date:<sup>*</sup></td>
				<td>
					<input type="text" class="validateRequired" id="leaveFromTo" name="leaveFromTo"></input><span class="hint">On Duty Date.<span class="hint-pointer">&nbsp;</span></span>
				</td>
			</tr>
			<%-- <tr id="idLeaveTo">
				<td class="txtlabel alignRight">Travel To Date:<sup>*</sup></td>
				<td><input type="text" class="validateRequired" id="leaveToDate" name="leaveToDate"></input><span class="hint">Leave End Date.<span class="hint-pointer">&nbsp;</span></span>
				</td>
			</tr> --%>
			<% } else { %>
			<tr>
				<td class="txtlabel alignRight">On Duty Date:<sup>*</sup></td>
				<td><input type="text" class="validateRequired" id="leaveFromTo" name="leaveFromTo"></input><span class="hint">On Duty Date.<span class="hint-pointer">&nbsp;</span></span></td>
			</tr>
			<%-- <tr id="idLeaveTo">
				<td class="txtlabel alignRight">Travel To Date:<sup>*</sup>
				</td>
				<td><input type="text" class="validateRequired" id="leaveToDate" name="leaveToDate"></input><span class="hint">On Duty Date.<span class="hint-pointer">&nbsp;</span></span></td>
			</tr> --%>
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
				<td class="txtlabel alignRight">On Duty Place:<sup>*</sup></td> 
				<td><s:textfield cssClass="validateRequired" name="placeFrom"></s:textfield></td>
			</tr>

			<tr id="policyid">
				<td class="txtlabel alignRight" valign="top">On Duty Reason:<sup>*</sup></td>
				<td><textarea rows="5" cols="50" class="validateRequired" name="reason"></textarea> </td>
			</tr>
			<tr>
				<td></td>
				<td>
					<input class="btn btn-default" id="submitButton" type="button" value="Apply On Duty"/>
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
	     <%
			if (strEmpType != null && (strEmpType.equalsIgnoreCase(IConstants.ADMIN) || strEmpType.equalsIgnoreCase(IConstants.CEO)
				|| strEmpType.equalsIgnoreCase(IConstants.CFO) || strEmpType.equalsIgnoreCase(IConstants.MANAGER) || strEmpType.equalsIgnoreCase(IConstants.HRMANAGER))) {
			%> 
				getLeaveDateStatus('1');
			<% }else{%>
			getLeaveDateStatus('2');
			<%}%>
	 });
	 
}); 
</script>

