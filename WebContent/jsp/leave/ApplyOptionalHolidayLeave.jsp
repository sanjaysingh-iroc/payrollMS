<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%
    String strTitle = ((request.getParameter("E") != null) ? "Edit ": "Apply ") + "Leave";
    %>
<script>
	$(function(){
		$("body").on("click","#submitButton",function(){
			$("#"+ this.form.id ).find('.validateRequired').filter(':hidden').prop('required',false);
			$("#"+ this.form.id ).find('.validateRequired').filter(':visible').prop('required',true);
		});
	});
    
    function getLeaveTypedetails(val, type) {
    	fadeForm('frmApplyOptHoliday');
    	var strHolidayDate = document.getElementById("strHolidayDate").value;
    	if(strHolidayDate != ''){
    		//var isHalfDay=document.getElementById("isHalfDay");
        	var strD1=strHolidayDate;
        	var strD2=strHolidayDate;
        	
    		var empid="";
        	if(type=="1"){
        		empid=document.frmApplyOptHoliday.strEmpId.options[document.frmApplyOptHoliday.strEmpId.selectedIndex].value;
        		var action='GetLeaveStatus.action?EMPID='+document.frmApplyOptHoliday.strEmpId.options[document.frmApplyOptHoliday.strEmpId.selectedIndex].value+'&LTID='+val+'&D1='+strD1+'&D2='+strD2;
        	}else if(type=="2"){
        		var action='GetLeaveStatus.action?LTID='+val+'&D1='+strD1+'&D2='+strD2;
        	} 
        	$.ajax({
   				url : action,
   				type : 'GET',
   				success : function(res) {
   					$("#myDiv").html(res);
   				}
   			}).done(function(){
   				var action1='GetEmployeePolicyDetails.action?leavetype='+ val+'&empid='+empid+'&strD1='+strD1+'&strD2='+strD2;
    			$.ajax({
       				url : action1,
       				type : 'GET',
       				success : function(result) {
       					result = $.parseHTML(result.trim());
       					$( "#policyid" ).nextAll().remove();
    	    			$("#policyid").after(result);
       				}
       			}).done(function(){
       				unfadeForm('frmApplyOptHoliday');
       			});
   			});
    	} else {
    		//document.getElementById("policyid").innerHTML="<div style=\"margin-left: 225px; float: left; width: 100%;\"><input class=\"btn btn-disabled\" type=\"button\" value=\"Apply Leave\"/></div>";
    		$("#submitButton").attr('disabled','disabled');
    		unfadeForm('frmApplyOptHoliday');
    	}
       }
    
    
    function getLeaveDateStatus(type){ 
    	fadeForm('frmApplyOptHoliday');
   		var empid="";
   		if(type=="1"){
   			empid=document.frmApplyOptHoliday.strEmpId.options[document.frmApplyOptHoliday.strEmpId.selectedIndex].value;		
   		}
   		var typeOfLeave=document.frmApplyOptHoliday.typeOfLeave.value;
   		var strHolidayDate = document.getElementById("strHolidayDate").value;
   		if(strHolidayDate != ''){
   			//var isHalfDay=document.getElementById("isHalfDay");				
   	    	var strD1=strHolidayDate;
   	    	var strD2=strHolidayDate;
   			
   			var action1='GetEmployeePolicyDetails.action?leavetype='+ typeOfLeave+'&empid='+empid+'&strD1='+strD1+'&strD2='+strD2;
   			$.ajax({
   				url : action1,
   				type : 'GET',
   				success : function(result) {
   					result = $.parseHTML(result.trim());
   					$( "#policyid" ).nextAll().remove();
	    			$("#policyid").after(result);
   					unfadeForm('frmApplyOptHoliday');
   				}
   			});
   		} else {
   			//document.getElementById("policyid").innerHTML="<div style=\"margin-left: 225px; float: left; width: 100%;\"><input class=\"btn btn-disabled\" type=\"button\" value=\"Apply Leave\"/></div>";
   			$("#submitButton").attr('disabled','disabled');
   			unfadeForm('frmApplyOptHoliday');
   		}
    	
    }
    
    
    function getTypeOFLeave(){
    	fadeForm('frmApplyOptHoliday');
    	var strEmpId=document.getElementById("strEmpId").value;
    	var action="GetTypeOfLeave.action?strEmpID="+strEmpId;
    
    	$.ajax({
			url : action,
			type : 'GET',
			success : function(res) {
				$("#tdtypeofleave").html(res);
				unfadeForm('frmApplyOptHoliday');
			}
		});
    	getLeaveDateStatus("1");
    } 
    
    
    function getLeaveValidation(){
    	fadeForm('frmApplyOptHoliday');
    	
    	var strHolidayDate = document.getElementById("strHolidayDate").value;
    	if(strHolidayDate != ''){
    		var d1=strHolidayDate;
    		var d2=strHolidayDate;
    		var val=document.getElementById("typeOfLeave").options[document.getElementById("typeOfLeave").selectedIndex].value;
    		var emp_id="";
    		//if(document.getElementById("strEmpId")){
    		if(document.frmApplyOptHoliday.elements.namedItem("strEmpId")){
    			//var e = document.getElementById("strEmpId");
    			var emp_id =document.frmApplyOptHoliday.strEmpId.options[document.frmApplyOptHoliday.strEmpId.selectedIndex].value;
    			//emp_id = e.options[e.selectedIndex].value;
    		}
    		var xmlhttp = GetXmlHttpObject();
    		if (xmlhttp == null) {
    			alert("Browser does not support HTTP Request");
    			return;
    		} else {
    			var xhr = $.ajax({
    				url : "LeaveValidation.action?emp_id="+emp_id+"&LTID="+val+"&D1="+d1+"&D2="+d2,
    				cache : false,
    				success : function(data) {
    					if(data.length>1){
    						alert(data);
    						unfadeForm('frmApplyOptHoliday');
    						return false;
    					} else {
    						//$('#frmApplyOptHoliday').submit();
    						var form_data = $("form[name='frmApplyOptHoliday']").serialize();
							$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
							$.ajax({
								url : "EmployeeLeaveEntry.action",
								data: form_data/* ,
								success : function(res) {
									$("#divResult").html(res);
									unfadeForm('frmApplyOptHoliday');
								} */
							});
							
							$.ajax({ 
								url: 'EmployeeLeaveEntryReport.action',
								cache: true,
								success: function(result){
									$("#divResult").html(result);
						   		}
							});
							
    						/* return true; */
    					}
    				}
    			});
    		} 
    	} else {
    		$("#submitButton").attr('disabled','disabled');
    		//document.getElementById("policyid").innerHTML="<div style=\"margin-left: 225px; float: left; width: 100%;\"><input class=\"btn btn-disabled\" type=\"button\" value=\"Apply Leave\"/></div>";
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
	
	
	$("#frmApplyOptHoliday").submit(function(e){
		e.preventDefault();
		getLeaveValidation();
	});
	
	
</script>

    <%
        String strEmpType = (String) session.getAttribute("USERTYPE");
        String strEmpID = (String) session.getAttribute(IConstants.EMPID);
        String strMessage = (String) request.getAttribute("MESSAGE");
        if (strMessage == null) {
        	strMessage = "";
        }
	%>
    <s:form id="frmApplyOptHoliday" name="frmApplyOptHoliday" theme="simple" action="EmployeeLeaveEntry" method="POST" cssClass="formcss" enctype="multipart/form-data">
        <div style="width: 58%; float: left;">
            <table border="0" class="table table_no_border" style="float:left" id="applyLeaveID">
                <s:hidden name="leaveId" />
                <s:hidden name="entrydate" />
                <s:hidden name="empId" required="true" />
                <s:hidden name="isCompensate"></s:hidden>
                <s:hidden name="isConstant"></s:hidden>
                <s:hidden name="type"></s:hidden>
                <s:hidden name="isOptHolidayLeave"></s:hidden>
                <%
                    if (strEmpType != null && (strEmpType.equalsIgnoreCase(IConstants.ADMIN) || strEmpType.equalsIgnoreCase(IConstants.CEO)
                    	|| strEmpType.equalsIgnoreCase(IConstants.CFO) || strEmpType.equalsIgnoreCase(IConstants.MANAGER) || strEmpType.equalsIgnoreCase(IConstants.HRMANAGER))) {
                    %>
                <tr>
                    <th class="txtlabel alignRight">Select Emp Name:<sup>*</sup></th>
                    <td>
                        <s:select cssClass="validateRequired form-control" name="strEmpId" id="strEmpId" listKey="employeeId" listValue="employeeName" headerKey="" headerValue="Select Employee" list="empList"  onchange="getTypeOFLeave();"/>
                    </td>
                </tr>
                <% } else { %>
                <tr style="display:none;">
                    <th class="txtlabel alignRight">
                        <s:hidden name="empId" required="true" />Emp Name:<sup>*</sup>
                    </th>
                    <td>
                        <s:label name="empName" label="Emp Name"/>
                    </td>
                </tr>
                <% } %>
                <%-- <tr><td class="txtlabel alignRight" valign="top">Half day:</td><td height=50 valign="top"><s:checkbox name="isHalfDay" id="isHalfDay" onclick="toggleSession(this)" cssStyle="float:left"/>  <div id="idSession" style="float:left;width:105px;"><s:radio name="strSession" list="strWorkingSession" listKey="strHaldDayId" listValue="strHaldDayName"/></div> </td></tr> --%>
                <%
                    if (strEmpType != null && (strEmpType.equalsIgnoreCase(IConstants.ADMIN) || strEmpType.equalsIgnoreCase(IConstants.CEO)
                    	|| strEmpType.equalsIgnoreCase(IConstants.CFO) || strEmpType.equalsIgnoreCase(IConstants.MANAGER) || strEmpType.equalsIgnoreCase(IConstants.HRMANAGER))) {
				%>
                <%-- <tr><td class="txtlabel alignRight">Leave From Date:<sup>*</sup></td><td><s:textfield cssClass="validateRequired" id="leaveFromTo" name="leaveFromTo" required="true" onchange="getLeaveDateStatus('1');" readonly="true"></s:textfield><span class="hint">On Leave From Date.<span class="hint-pointer">&nbsp;</span></span></td></tr>
                    <tr id="idLeaveTo"><td class="txtlabel alignRight">Leave To Date:<sup>*</sup></td><td><s:textfield cssClass="validateRequired" id="leaveToDate" name="leaveToDate"  required="true" onchange="getLeaveDateStatus('1');" readonly="true"></s:textfield><span class="hint">Leave End Date.<span class="hint-pointer">&nbsp;</span></span></td></tr> --%>
                <tr>
                    <th class="txtlabel alignRight">Optional Holiday Date:<sup>*</sup></th>
                    <td>
                        <s:select name="strHolidayDate" id="strHolidayDate" listKey="holidayId" listValue="holidayDate" cssClass="validateRequired form-control"
                            list="optHolidayList" key="" headerKey="" headerValue="Select Holiday Date" onchange="getLeaveDateStatus('1');"/>
                    </td>
                </tr>
                <tr>
                    <th class="txtlabel alignRight">Leave type:<sup>*</sup></th>
                    <td id="tdtypeofleave">
                        <s:select theme="simple" cssClass="validateRequired form-control" name="typeOfLeave" id="typeOfLeave" listKey="leaveTypeId" listValue="leavetypeName" headerKey="" headerValue="Select Leave Type" list="leaveTypeList" key="" required="true"  onchange="getLeaveTypedetails(this.value,'1');"/>
                        <p class="hint">Select Leave Type. These are the leave types available to you. If you do not find yours please speak to HR.<span class="hint-pointer">&nbsp;</span></p>
                    </td>
                    <td id="tdpolicyid"></td>
                </tr>
                <% } else { %>
                <%-- <tr><td class="txtlabel alignRight">Leave From Date:<sup>*</sup></td><td><s:textfield cssClass="validateRequired" id="leaveFromTo" name="leaveFromTo" required="true" onchange="getLeaveDateStatus('2');" readonly="true"></s:textfield><span class="hint">On Leave From Date.<span class="hint-pointer">&nbsp;</span></span></td></tr>
                    <tr id="idLeaveTo"><td class="txtlabel alignRight">Leave To Date:<sup>*</sup></td><td><s:textfield cssClass="validateRequired" id="leaveToDate" name="leaveToDate"  required="true" onchange="getLeaveDateStatus('2');" readonly="true"></s:textfield><span class="hint">Leave End Date.<span class="hint-pointer">&nbsp;</span></span></td></tr> --%>
                <tr>
                    <th class="txtlabel alignRight">Optional Holiday Date:<sup>*</sup></th>
                    <td>
                        <s:select name="strHolidayDate" id="strHolidayDate" listKey="holidayId" listValue="holidayDate" cssClass="validateRequired form-control"
                            list="optHolidayList" key="" headerKey="" headerValue="Select Holiday Date" onchange="getLeaveDateStatus('2');"/>
                    </td>
                </tr>
                <%-- <tr><td class="txtlabel alignRight">Leave type:<sup>*</sup></td><td><s:select cssClass="validateRequired" name="typeOfLeave" listKey="leaveTypeId" listValue="leavetypeName" headerKey="" headerValue="Select Leave Type" list="leaveTypeList" key="" required="true"  onchange="getContent('myDiv', 'GetLeaveStatus.action?LTID='+this.options[this.selectedIndex].value+'&D1='+document.frmApplyOptHoliday.leaveFromTo.value+'&D2='+document.frmApplyOptHoliday.leaveToDate.value);"/><span class="hint">Select Leave Type. These are the leave types available to you. If you do not find yours please speak to HR.<span class="hint-pointer">&nbsp;</span></span></td></tr> --%>
                <tr>
                    <th class="txtlabel alignRight">Leave type:<sup>*</sup></th>
                    <td>
                        <s:select theme="simple" cssClass="validateRequired form-control" name="typeOfLeave" id="typeOfLeave" listKey="leaveTypeId" listValue="leavetypeName" headerKey="" headerValue="Select Leave Type" list="leaveTypeList" key="" required="true"  onchange="getLeaveTypedetails(this.value,'2');"/>
                        <p class="hint">Select Leave Type. These are the leave types available to you. If you do not find yours please speak to HR.<span class="hint-pointer">&nbsp;</span></p>
                    </td>
                    <td id="tdpolicyid"></td>
                </tr>
                <% } %> 
                <tr>
                    <td id="idDocumentRequired"></td>
                </tr>
                <tr id="policyid">
                    <th class="txtlabel alignRight" valign="top">Leave Reason:<sup>*</sup></th>
                    <td>
                        <s:textarea cssClass="validateRequired" cols="22" rows="05" name="reason" label="Leave Reason" required="true" />
                    </td>
                </tr>
                <tr>
	                <td></td>
	                <td>
	                    <input class="btn btn-primary" id="submitButton" type="submit" value="Apply Leave"/>
	                </td>
	            </tr>
            </table>
        </div>
        <div style="text-align: center;">
            <p style="display: inline; padding: 0px 10px;"><strong>Leave Balance</strong></p>
            <br/>
            <p style="display: inline; padding: 0px 10px;">On Selection of Leave Type get more details</p>
            <div id="myDiv"></div>
        </div>
    </s:form>
    <%-- 
        <s:action name="LeaveEntryReportInner" executeResult="true"></s:action>
        --%>

<%-- <script>toggleSession(document.frmApplyOptHoliday.isHalfDay);</script> --%>
