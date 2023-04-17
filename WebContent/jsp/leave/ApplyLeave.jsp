<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.*"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>


<%
	String strTitle = ((request.getParameter("E") != null) ? "Edit " : "Apply ") + "Leave"; 
	UtilityFunctions uF = new UtilityFunctions();
	String type1 = (String) request.getAttribute("type");
//===start parvez date: 18-03-2023===	
	Map<String, String> hmFeatureStatus = (Map<String, String>)request.getAttribute("hmFeatureStatus");
	if(hmFeatureStatus == null) hmFeatureStatus = new HashMap<String, String>();
//===end parvez date: 18-03-2023===	
%>

<script>

    function toggleSession(){
    	if(document.getElementById("isHalfDay") && document.getElementById("isHalfDay").checked) {
    		document.getElementById("idSession").style.display="block";
    		document.getElementById("idLeaveTo").style.display="none";
    	} else {
    		if(document.getElementById("idSession")){
    			document.getElementById("idSession").style.display="none";
    		}
    		document.getElementById("idLeaveTo").style.display="table-row";
    		
    	}
    }
	
	function getLeaveTypedetails(val,type){
		fadeForm('formID');
    	var strD1=document.getElementById("leaveFromTo").value;
    	var strD2=document.getElementById("leaveToDate").value;
    	var strSession = "";
    	/* var strD1=document.frmLeave.leaveFromTo.value;
    	var strD2=document.frmLeave.leaveToDate.value; */
    	if(document.getElementById("isHalfDay") && document.getElementById("isHalfDay").checked==true){
    		strD2=document.frmLeave.leaveFromTo.value;

    		var ele = document.getElementsByName("strSession");
			for(var i=0; i < ele.length; i++) {
				if(ele[i].checked) {
					strSession = ele[i].value;
				}
			}
    	}
    	var isCompensate = document.getElementById("isCompensate").value;

		var empid="";
    	if(type=="1"){
    		<% if(type1!=null && type1.equals("ROSTER")) { %>
    			empid = document.getElementById("strEmpId").value;
    		<% } else { %>
    			empid = document.frmLeave.strEmpId.options[document.frmLeave.strEmpId.selectedIndex].value;
    		<% } %>
    		//alert("val 5 1===>> " + val);
    		var action='GetLeaveStatus.action?EMPID='+empid+'&LTID='+val+'&D1='+strD1+'&D2='+strD2;
    		getContent('myDiv', action);
    	} else if(type=="2") {
    		//alert("val 6 2===>> " + val);
    		var action='GetLeaveStatus.action?LTID='+val+'&D1='+strD1+'&D2='+strD2;
    		getContent('myDiv', action);	
    	}
    	//alert("val 7 ===>> " + val);
    	$.ajax({ 
    		type : 'GET',
    		url: action,
    		success: function(result){
    			//alert("val 8 ===>> " + val);
    			$( "#myDiv" ).html(result);
    			//alert("result 8 ===>> " + result);
    			var action1='GetEmployeePolicyDetails.action?leavetype='+ val+'&empid='+empid+'&strD1='+strD1+'&strD2='+strD2+'&isCompensate='+isCompensate+'&strSession='+strSession;
        		$.ajax({ 
    	    		type : 'GET',
    	    		url: action1,
    	    		success: function(result){
    	    			//alert("val 9 ===>> " + val);
    	    			result = $.parseHTML(result.trim());
    	    			//alert("result 9 ===>> " + result);
    	    			$( "#policyid" ).nextAll().remove();
    	    			$("#policyid").after(result);
    	       		}
    	    	}).done(function() {
    	 			unfadeForm('formID');
    	 		});
       		}
    	}); 
    }
	
	function getLeaveDateStatus(type) {
		fadeForm('formID');
		var empid = "";
		var strSession = "";
		//alert("empid 0 ===>> " + empid);
		if(type=="1") {
			empid = document.frmLeave.strEmpId.options[document.frmLeave.strEmpId.selectedIndex].value;
		} else if(type=="3") {
			empid = document.getElementById("strEmpId").value;
		}
		//alert("empid 1 ===>> " + empid);
		var typeOfLeave = document.frmLeave.typeOfLeave.value;
		//alert("1 ===>> ");
		var strD1 = document.frmLeave.leaveFromTo.value;
  //  	alert("2 ===>> ");
		var strD2 = document.frmLeave.leaveToDate.value;
//    	alert("3 ===>> ");
		if(document.getElementById("isHalfDay") && document.getElementById("isHalfDay").checked==true) {
			strD2 = document.frmLeave.leaveFromTo.value;

			var ele = document.getElementsByName("strSession");
			//alert("ele ===>> " + ele);
			for(var i=0; i < ele.length; i++) {
				if(ele[i].checked) {
					strSession = ele[i].value;
				}
			}
//    		strSession = document.getElementById("strSession").value;
    	}
    	//alert("strSession ===>> " + strSession);
    	var isCompensate = document.getElementById("isCompensate").value;
    	//alert(isCompensate);
    	//alert("isCompensate ===>> " + isCompensate);
		var action1='GetEmployeePolicyDetails.action?leavetype='+ typeOfLeave+'&empid='+empid+'&strD1='+strD1+'&strD2='+strD2+'&isCompensate='+isCompensate+'&strSession='+strSession;
		$.ajax({ 
    		type : 'GET',
    		url: action1,
    		success: function(result){
    			//alert("result ===>> " + result);
    			result = $.parseHTML(result.trim());
    			$( "#policyid" ).nextAll().remove();
    			$("#policyid").after(result);
    			unfadeForm('formID');
       		}
    	});	
	}
	
	
	function getTypeOFLeave(){
		var strEmpId=document.getElementById("strEmpId").value;
		var action="GetTypeOfLeave.action?strEmpID="+strEmpId;
		$.ajax({ 
    		type : 'GET',
    		url: action,
    		success: function(result){
    			$("#tdtypeofleave").html(result);
    			getLeaveDateStatus("1");
       		}
    	}).done(function() {
 			unfadeForm('formID');
 		});
		
	} 
	
	function getLeaveValidation(){
		//fadeForm('formID');
		document.getElementById("submitButton").style.display = 'none';
		var d1=document.getElementById("leaveFromTo").value;
		var d2=document.getElementById("leaveToDate").value;
		if(document.getElementById("isHalfDay") && document.getElementById("isHalfDay").checked==true){
			d2=document.getElementById("leaveFromTo").value;
    	}
		//alert("d2 ==>> " + d2);
		var val=document.getElementById("typeOfLeave").options[document.getElementById("typeOfLeave").selectedIndex].value;
		var emp_id="";
		//if(document.getElementById("strEmpId")){
		if(document.frmLeave.elements.namedItem("strEmpId")) {
			//var e = document.getElementById("strEmpId");
			var emp_id =document.frmLeave.strEmpId.options[document.frmLeave.strEmpId.selectedIndex].value;
			//emp_id = e.options[e.selectedIndex].value;
		}
		<% if(type1!=null && type1.equals("ROSTER")) { %>
			empid = document.getElementById("strEmpId").value;
		<% } %>
		var xmlhttp = GetXmlHttpObject();
		if (xmlhttp == null) {
			alert("Browser does not support HTTP Request");
			return;
		} else {
			var xhr = $.ajax({
				url : "LeaveValidation.action?emp_id=" + emp_id+"&LTID="+val+"&D1="+d1+"&D2="+d2,
				cache : false,
				success : function(data) {
					if(data.length>1){
						alert(data);
						document.getElementById("submitButton").style.display = 'block';
						unfadeForm('formID');
						return false;
					} else {
						var type = document.getElementById("type").value;
						var strPaycycle = document.getElementById("strPaycycle").value;
				/* ===start parvez date: 27-09-2022 */			
						/* var form_data = $("form[name='frmLeave']").serialize(); */
						var form_data = new FormData($("form[name='frmLeave']")[0]);
						
				     	//$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
				     	$.ajax({
				     		
				     		/* type: 'POST',
				 			url : "EmployeeLeaveEntry.action",
				 			data: form_data,
				 			cache : false,
				 			success : function(res) {
				 				console.log(res);
				 				$("#divResult").html(res);
				 			},  */
				 			url : "EmployeeLeaveEntry.action",
				 			type: 'POST',
				 			data: form_data,
				 			contentType: false,
				 			cache : false,
				 			processData: false,
				 			success : function(res) {
				 				console.log(res);
				 				$("#divResult").html(res);
				 			}, 
				 	/* ===end parvez date: 27-09-2022=== */		
				 			error : function(err) {
				 				if(type != null && type == 'ROSTER') {
				 					$.ajax({ 
										url: 'RosterOfEmployee.action?calendarYear='+calendarYear+'&strMonth='+strMonth,
										cache: true,
										success: function(result){
											$("#divResult").html(result);
								   		}
									});
				 				} else if(type != null && type == 'timesheet') {
				 					$.ajax({ 
										url: 'AddProjectActivity1.action?submitType=LOAD&strPaycycle='+strPaycycle,
										cache: true,
										success: function(result){
											$("#divResult").html(result);
								   		}
									});
				 				} else {
					 				$.ajax({ 
										url: 'EmployeeLeaveEntryReport.action',
										cache: true,
										success: function(result){
											$("#divResult").html(result);
								   		}
									});
				 				}
				 			}
				 		});
						//return true;
					}
				}
			});

		}
	}
		
		
	function GetXmlHttpObject() {
		if (window.XMLHttpRequest) {
			// code for IE7+, Firefox, Chrome, Opera, Safari
			return new XMLHttpRequest();
		}
		if (window.ActiveXObject) {
			// code for IE6, IE5
			return new ActiveXObject("Microsoft.XMLHTTP");
		}
		return null;
	}
	 
	$("#formID").submit(function(e){
		e.preventDefault();
		//console.log("getLeaveValidation()==>"+getLeaveValidation());
		//alert("for check");
		getLeaveValidation();
		/* if(getLeaveValidation()) {
			var form_data = $("form[name='frmLeave']").serialize();
	     	//$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	     	$.ajax({
	     		type: 'POST',
	 			url : "EmployeeLeaveEntry.action",
	 			data: form_data,
	 			cache : false
	 		});
	 		
	 		$.ajax({ 
				url: 'EmployeeLeaveEntryReport.action',
				cache: true,
				success: function(result){
					$("#divResult").html(result);
		   		}
			});
	 		
		} */
	});
	 
</script>

	<%
		String strEmpType = (String) session.getAttribute("USERTYPE");
		String strEmpID = (String) session.getAttribute(IConstants.EMPID);
		
		//System.out.println("applyleave type1 ===>> " + type1);
		String strMessage = (String) request.getAttribute("MESSAGE");
		if (strMessage == null) {
			strMessage = "";
		}
		String strIsHalfDayLeave = (String)request.getAttribute("strIsHalfDayLeave");
		String isCompensate = (String)request.getAttribute("isCompensate");
		String strLeaveLabel= "Leave";
		if(uF.parseToBoolean(isCompensate)){
			strLeaveLabel= "Extra Working";
		}
	%>
	

	<s:form id="formID" name="frmLeave" theme="simple"	action="EmployeeLeaveEntry" method="POST" cssClass="formcss" enctype="multipart/form-data">
	<div class="row row_without_margin">
		<div class="col-lg-8 col-md-6 col-sm-12">
			<table border="0" class="table table_no_border" id="applyLeaveID">
			<s:hidden name="leaveId" />
			<s:hidden name="entrydate" />
			<s:hidden name="empId" required="true" />
			<s:hidden name="isCompensate" id="isCompensate"/>
			<s:hidden name="isWorkFromHome" id="isWorkFromHome"/>
			<s:hidden name="isConstant" />
			<s:hidden name="type" id="type" />
			<s:hidden name="calendarYear" id="calendarYear" />
			<s:hidden name="strMonth" id="strMonth" />
			<s:hidden name="strPaycycle" id="strPaycycle"/>
			<s:hidden name="strCurrDate" id="strCurrDate"/>
	
			<%
				if (strEmpType != null && (strEmpType.equalsIgnoreCase(IConstants.ADMIN) || strEmpType.equalsIgnoreCase(IConstants.CEO) || strEmpType.equalsIgnoreCase(IConstants.CFO)
					|| strEmpType.equalsIgnoreCase(IConstants.MANAGER) || strEmpType.equalsIgnoreCase(IConstants.HRMANAGER)) && (type1==null || !type1.equals("ROSTER"))) {
			%>
				<tr><td class="txtlabel alignRight">Select Emp Name:<sup>*</sup></td><td><s:select cssClass="validateRequired" name="strEmpId" id="strEmpId" listKey="employeeId" listValue="employeeName" headerKey="" headerValue="Select Employee" list="empList"  onchange="getTypeOFLeave();"/></td></tr>
			<% } else { %>
				<%-- <s:hidden name="empId" required="true" /> --%>
				<% if(type1!=null && type1.equals("ROSTER")) { %>
					<tr><s:hidden name="strEmpId" id="strEmpId" /><td class="txtlabel alignRight">Emp Name:<sup>*</sup></td><td><s:label name="empName" label="Emp Name"/></td></tr>
				<% } %>
			<% } %>
		
			<%if(uF.parseToBoolean(strIsHalfDayLeave)){ %>
				<tr><td class="txtlabel alignRight" valign="top">Half day:</td><td height=50 valign="top"><s:checkbox name="isHalfDay" id="isHalfDay" onclick="toggleSession()" cssStyle="float:left"/>  <div id="idSession"><s:radio name="strSession" id="strSession" list="strWorkingSession" listKey="strHaldDayId" listValue="strHaldDayName"/></div></td></tr>  <!-- style="float:left; width:115px;" -->
			<%} %>
		
			<%-- <tr><td class="txtlabel alignRight">Leave From Date:<sup>*</sup></td><td><s:textfield cssClass="validate[required]" id="leaveFromTo" name="leaveFromTo" required="true" onblur="getLeaveDateStatus();"></s:textfield><span class="hint">On Leave From Date.<span class="hint-pointer">&nbsp;</span></span></td></tr>
			<tr id="idLeaveTo"><td class="txtlabel alignRight">Leave To Date:<sup>*</sup></td><td><s:textfield cssClass="validate[required]" id="leaveToDate" name="leaveToDate"  required="true" onblur="getLeaveDateStatus();"></s:textfield><span class="hint">Leave End Date.<span class="hint-pointer">&nbsp;</span></span></td></tr> --%>
			
			<%
				if (strEmpType != null && (strEmpType.equalsIgnoreCase(IConstants.ADMIN) || strEmpType.equalsIgnoreCase(IConstants.CEO) || strEmpType.equalsIgnoreCase(IConstants.CFO) 
					|| strEmpType.equalsIgnoreCase(IConstants.MANAGER) || strEmpType.equalsIgnoreCase(IConstants.HRMANAGER)) && (type1==null || !type1.equals("ROSTER"))) {
			%> 
			<tr><td class="txtlabel alignRight">From Date:<sup>*</sup></td><td><s:textfield cssClass="validateRequired" id="leaveFromTo" name="leaveFromTo" required="true" ></s:textfield><p class="hint">On Leave From Date.<span class="hint-pointer">&nbsp;</span></p></td></tr>
			<tr id="idLeaveTo"><td class="txtlabel alignRight">To Date:<sup>*</sup></td><td><s:textfield cssClass="validateRequired" id="leaveToDate" name="leaveToDate"  required="true" ></s:textfield><p class="hint">Leave End Date.<span class="hint-pointer">&nbsp;</span></p></td></tr>
					
			<%-- <tr><td class="txtlabel alignRight">Leave type:<sup>*</sup></td><td><s:select cssClass="validate[required]" name="typeOfLeave" listKey="leaveTypeId" listValue="leavetypeName" headerKey="" headerValue="Select Leave Type" list="leaveTypeList" key="" required="true"  onchange="getContent('myDiv', 'GetLeaveStatus.action?EMPID='+document.frmLeave.strEmpId.options[document.frmLeave.strEmpId.selectedIndex].value+'&LTID='+this.options[this.selectedIndex].value+'&D1='+document.frmLeave.leaveFromTo.value+'&D2='+document.frmLeave.leaveToDate.value);"/><span class="hint">Select Leave Type. These are the leave types available to you. If you do not find yours please speak to HR.<span class="hint-pointer">&nbsp;</span></span></td></tr> --%>
			<tr><td class="txtlabel alignRight"><%=strLeaveLabel %> Type:<sup>*</sup></td><td id="tdtypeofleave"><s:select theme="simple" cssClass="validateRequired" name="typeOfLeave" id="typeOfLeave" listKey="leaveTypeId" listValue="leavetypeName" list="leaveTypeList" key="" required="true"  onchange="getLeaveTypedetails(this.value,'1');"/><p class="hint">Select Leave Type. These are the leave types available to you. If you do not find yours please speak to HR.<span class="hint-pointer">&nbsp;</span></p></td>
			<td id="tdpolicyid"></td>
			</tr>
			<% } else { %>
			
			<tr>
			<td class="txtlabel alignRight">From Date:<sup>*</sup></td>
			<td>
				<s:textfield cssClass="validateRequired" id="leaveFromTo" name="leaveFromTo" required="true" ></s:textfield>
				<p class="hint">On Leave From Date.<span class="hint-pointer">&nbsp;</span></p>
			</td>
			</tr>
			<tr id="idLeaveTo">
				<td class="txtlabel alignRight">To Date:<sup>*</sup></td>
				<td>
					<s:textfield cssClass="validateRequired" id="leaveToDate" name="leaveToDate" required="true"></s:textfield>
					<p class="hint">Leave End Date.<span class="hint-pointer">&nbsp;</span></p>
				</td>
			</tr>
			<% if(uF.parseToBoolean(isCompensate)) { %>
			 <tr id = "idLeaveFromtime">
				<td class="txtlabel alignRight">From Time:<sup>*</sup></td>
				<td>
					<s:textfield cssClass="validateRequired" id="extraWorkingFromTime" name="extraWorkingFromTime"  required="true"></s:textfield>
				 	<p class="hint"> start Time.<span class="hint-pointer">&nbsp;</span></p>
				</td>
			</tr>
			<tr id="idLeaveTotime">
				<td class="txtlabel alignRight">To Time:<sup>*</sup></td>
				<td>
					<s:textfield cssClass="validateRequired" id="extraWorkingToTime" name="extraWorkingToTime"  required="true"></s:textfield>
				 	<p class="hint"> End Time.<span class="hint-pointer">&nbsp;</span></p>
				</td>
			</tr>
			<% } %>
			
			<%-- <tr><td class="txtlabel alignRight">Leave type:<sup>*</sup></td><td><s:select cssClass="validate[required]" name="typeOfLeave" listKey="leaveTypeId" listValue="leavetypeName" headerKey="" headerValue="Select Leave Type" list="leaveTypeList" key="" required="true"  onchange="getContent('myDiv', 'GetLeaveStatus.action?LTID='+this.options[this.selectedIndex].value+'&D1='+document.frmLeave.leaveFromTo.value+'&D2='+document.frmLeave.leaveToDate.value);"/><span class="hint">Select Leave Type. These are the leave types available to you. If you do not find yours please speak to HR.<span class="hint-pointer">&nbsp;</span></span></td></tr> --%>
			<tr>
			<td class="txtlabel alignRight"><%=strLeaveLabel %> Type:<sup>*</sup></td>
			
			<td><s:select theme="simple" cssClass="validateRequired" name="typeOfLeave" id="typeOfLeave" listKey="leaveTypeId" listValue="leavetypeName" list="leaveTypeList" key="" required="true"  onchange="getLeaveTypedetails(this.value,'2');"/>
				<p class="hint">Select Leave Type. These are the leave types available to you. If you do not find yours please speak to HR.<span class="hint-pointer">&nbsp;</span></p>
				</td>
			<td id="tdpolicyid"></td>
			</tr>
			<% } %> 
			
			<tr><td id="idDocumentRequired"></td></tr>
			
		<!-- start parvez date: 18-03-2023=== -->	
			<%if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_BACKUP_EMPLOYEE_FOR_LEAVE))){ %>
				<tr>
					<td class="txtlabel alignRight">Back-up:<sup>*</sup></td>
					<td>
						<s:textfield cssClass="validateRequired" id="backupEmp" name="backupEmp" required="true"></s:textfield>
					 	<p class="hint"> Back-up<span class="hint-pointer">&nbsp;</span></p>
				 	</td>
				</tr>
			<%} %>
		<!-- end parvez date: 18-03-2023=== -->
			
			<tr id="policyid"><td class="txtlabel alignRight" valign="top"><%=strLeaveLabel %> Reason:<sup>*</sup></td><td><s:textarea cssClass="validateRequired" cols="22" rows="05" name="reason" label="Leave Reason" required="true" /></td></tr>
			<tr>
               	<td></td>
               	<td>
		            <input class="btn btn-default" id="submitButton" type="button" value="Apply <%=strLeaveLabel %>"/>
               	</td>
              </tr> 	
			</table>
		</div>
		<% if(!uF.parseToBoolean(isCompensate)){ %>
			<div class="col-lg-4 col-md-6 col-sm-12">
				<div style="text-align: center;">
					<p style="display: inline;padding: 0px 10px;"><strong>Leave Balance</strong></p><br/>
					<p style="display: inline;padding: 0px 10px;">On Selection of Leave Type get more details</p>
					<div id="myDiv"></div>
				</div>
			</div>
		<% } %>
	</div>
	</s:form>
	
<script>
	$(function(){
	    $("body").on("click","#submitButton",function(){
	    	$("#"+ this.form.id ).find('.validateRequired').filter(':hidden').prop('required',false);
			$("#"+ this.form.id ).find('.validateRequired').filter(':visible').prop('required',true);
	    });
		<%-- $("#leaveFromTo").datetimepicker({
		    format: "dd/mm/yyyy",
		    weekStart: 1,
		    todayBtn:  1,
		    autoclose: 1,
		    todayHighlight: 1,
		    startView: 2,
		    minView: 2,
		    startDate : new Date('08/01/2018'),
		    endDate : new Date('28/01/2018')
		}).on('changeDate', function (selected) {
		    var minDate = new Date(selected.date.valueOf());
		    $('#leaveToDate').datetimepicker('setStartDate', minDate);
		    $('#leaveToDate').datetimepicker('setDate', minDate);
		    <%
			if (strEmpType != null && (strEmpType.equalsIgnoreCase(IConstants.ADMIN) || strEmpType.equalsIgnoreCase(IConstants.CEO)
				|| strEmpType.equalsIgnoreCase(IConstants.CFO) || strEmpType.equalsIgnoreCase(IConstants.MANAGER) || strEmpType.equalsIgnoreCase(IConstants.HRMANAGER))) {
			%> 
				getLeaveDateStatus('1');
			<% }else{%>
			getLeaveDateStatus('2');
			<%}%>
		});  --%>
				
		var strMinDate = document.getElementById("strCurrDate").value;
		 
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
				if(type1!= null && type1.equals("ROSTER")) {
			%> 
				getLeaveDateStatus('3');
				<% } else { %>
					getLeaveDateStatus('1');
				<% } %>	
			<% } else { %>
				getLeaveDateStatus('2');
			<% } %>
			
		});
	    $("#leaveToDate").datepicker({
			format: 'dd/mm/yyyy',
			startDate : new Date(strMinDate),
			autoclose: true
		}).on('changeDate', function (selected) {
	        var minDate = new Date(selected.date.valueOf());
	        $('#leaveFromTo').datepicker('setEndDate', minDate);
	        <% 
			if (strEmpType != null && (strEmpType.equalsIgnoreCase(IConstants.ADMIN) || strEmpType.equalsIgnoreCase(IConstants.CEO)
				|| strEmpType.equalsIgnoreCase(IConstants.CFO) || strEmpType.equalsIgnoreCase(IConstants.MANAGER) || strEmpType.equalsIgnoreCase(IConstants.HRMANAGER))) {
				if(type1!= null && type1.equals("ROSTER")) {
			%> 
				getLeaveDateStatus('3');
				<% } else { %>
					getLeaveDateStatus('1');
				<% } %>	
			<% } else { %>
				getLeaveDateStatus('2');
			<% } %>
		});
	    var date_yest = new Date();
	    var date_tom = new Date();
	    date_yest.setHours(0,0,0);
	    date_tom.setHours(23,59,59); 
	    var shiftStartTimeMoment = date_yest;
	    var shiftEndTimeMoment = date_tom;
		
	    $('#extraWorkingToTime').datetimepicker({
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
	    $('#extraWorkingFromTime').datetimepicker({
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
	    
	    //alert("test onload .........2");
	    
		/* startDate : new Date('01/08/2018'), 
	    endDate : new Date('01/28/2018'), */
	    toggleSession();
	});
	
	
</script>

<!-- 
<input type="date" id="myDate" name="bday" min="2017-01-01" max="2018-01-01">
<button onclick="myFunction()">Try it</button>

<script>
function myFunction() {
    var x = document.getElementById("myDate").min = "2017-01-01";
    document.getElementById("demo").innerHTML = "The value of the min attribute was changed from '2017-01-01' to '2018-01-01'.";
}
</script>-->
				
				
			
			