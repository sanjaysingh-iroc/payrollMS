	<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%String strTitle = ((request.getParameter("E")!=null)?"Edit ":"Apply ")+"Leave";%>
<script>
    $(function() {
        $("#leaveFromTo").datepicker({
            format: 'dd/mm/yyyy',
            autoclose: true
        }).on('changeDate', function (selected) {
            var minDate = new Date(selected.date.valueOf());
            $('#leaveToDate').datepicker('setStartDate', minDate);
        });
        
        $("#leaveToDate").datepicker({
        	format: 'dd/mm/yyyy',
        	autoclose: true
        }).on('changeDate', function (selected) {
                var minDate = new Date(selected.date.valueOf());
                $('#leaveFromTo').datepicker('setEndDate', minDate);
        });
    });
    
</script>
<div class="leftbox reportWidth">
    <%
        String strEmpType = (String) session.getAttribute("USERTYPE");
        String strEmpID = (String) session.getAttribute(IConstants.EMPID);
        String strMessage = (String) request.getAttribute("MESSAGE");
        if (strMessage == null) {
        	strMessage = "";
        }
        
        %>
    <s:form id="formID" name="frmLeave" theme="simple"	action="EmployeeCompLeaveEntry" method="POST" cssClass="formcss" enctype="multipart/form-data">
        <table border="0" class="table table_no_border">
            <tr>
                <td>
                    <s:hidden name="leaveId" />
                    <s:hidden name="entrydate" />
                    <s:hidden name="empId"  required="true" />
                </td>
            </tr>
            <%if(strEmpType!=null && (strEmpType.equalsIgnoreCase(IConstants.ADMIN) || strEmpType.equalsIgnoreCase(IConstants.CEO) || strEmpType.equalsIgnoreCase(IConstants.CFO) || strEmpType.equalsIgnoreCase(IConstants.MANAGER) || strEmpType.equalsIgnoreCase(IConstants.HRMANAGER))) {%>
            <tr>
                <th class="txtlabel alignRight">Select Emp Name:<sup>*</sup></th>
                <td>
                    <s:select cssClass="validateRequired" name="strEmpId" listKey="employeeId" listValue="employeeName" headerKey="" headerValue="Select Employee" list="empList" />
                </td>
            </tr>
            <%}else{ %>
            <tr>
                <td>
                    <s:hidden name="empId"  required="true" />
                </td>
            </tr>
            <tr style="display:none;">
                <th class="txtlabel alignRight">Emp Name:<sup>*</sup></th>
                <td>
                    <s:label name="empName" label="Emp Name"/>
                </td>
            </tr>
            <%}%>
            <tr>
                <th class="txtlabel alignRight" valign="top">Half day:</th>
                <td height=50 valign="top">
                    <s:checkbox name="isHalfDay" onclick="toggleSession(this)" cssStyle="float:left"/>
                    <div id="idSession" style="float:left;width:100px;">
                        <s:radio name="strSession" list="strWorkingSession" listKey="strHaldDayId" listValue="strHaldDayName"/>
                    </div>
                </td>
            </tr>
            <tr>
                <th class="txtlabel alignRight">From Date:<sup>*</sup></th>
                <td>
                    <s:textfield cssClass="validateRequired" id="leaveFromTo" name="leaveFromTo" required="true"></s:textfield>
                    <span class="hint">On Leave From Date.<span class="hint-pointer">&nbsp;</span></span>
                </td>
            </tr>
            <tr id="idLeaveTo">
                <th class="txtlabel alignRight">To Date:<sup>*</sup></th>
                <td>
                    <s:textfield cssClass="validateRequired" id="leaveToDate" name="leaveToDate"  required="true"></s:textfield>
                    <span class="hint">Leave End Date.<span class="hint-pointer">&nbsp;</span></span>
                </td>
            </tr>
            <%if(strEmpType!=null && (strEmpType.equalsIgnoreCase(IConstants.ADMIN) || strEmpType.equalsIgnoreCase(IConstants.CEO) || strEmpType.equalsIgnoreCase(IConstants.CFO) || strEmpType.equalsIgnoreCase(IConstants.MANAGER) || strEmpType.equalsIgnoreCase(IConstants.HRMANAGER))) {%> 
            <tr>
                <th class="txtlabel alignRight">type:<sup>*</sup></th>
                <td>
                    <s:select cssClass="validateRequired" name="typeOfLeave" listKey="leaveTypeId" listValue="leavetypeName" headerKey="" headerValue="Select Leave Type" list="leaveTypeList" key="" required="true"  onchange="getContent('myDiv', 'GetLeaveStatus.action?EMPID='+document.frmLeave.strEmpId.options[document.frmLeave.strEmpId.selectedIndex].value+'&LTID='+this.options[this.selectedIndex].value+'&D1='+document.frmLeave.leaveFromTo.value+'&D2='+document.frmLeave.leaveToDate.value);"/>
                    <span class="hint">Select Leave Type. These are the leave types available to you. If you do not find yours please speak to HR.<span class="hint-pointer">&nbsp;</span></span>
                </td>
            </tr>
            <%}else{ %>
            <tr>
                <th class="txtlabel alignRight">type:<sup>*</sup></th>
                <td>
                    <s:select cssClass="validateRequired" name="typeOfLeave" listKey="leaveTypeId" listValue="leavetypeName" headerKey="" headerValue="Select Leave Type" list="leaveTypeList" key="" required="true"  onchange="getContent('myDiv', 'GetLeaveStatus.action?LTID='+this.options[this.selectedIndex].value+'&D1='+document.frmLeave.leaveFromTo.value+'&D2='+document.frmLeave.leaveToDate.value);"/>
                    <span class="hint">Select Leave Type. These are the leave types available to you. If you do not find yours please speak to HR.<span class="hint-pointer">&nbsp;</span></span>
                </td>
            </tr>
            <%} %>
            <tr>
                <td id="idDocumentRequired"></td>
            </tr>
            <tr>
                <th class="txtlabel alignRight" valign="top">Reason:<sup>*</sup></th>
                <td>
                    <s:textarea cssClass="validateRequired" cols="50" rows="05" name="reason" label="Leave Reason" required="true" />
                </td>
            </tr>
            <%
                if (request.getParameter("E") != null) {
                %>
            <tr>
                <td></td>
                <td>
                    <s:submit  cssClass="btn btn-primary" value="Update Leave" align="center"  id="submitButton"/>
                </td>
            </tr>
            <%
                } else {
                %>
            <tr>
                <td></td>
                <td>
                    <s:submit  cssClass="btn btn-primary" value="Apply Leave" align="center" id="submitButton"/>
                </td>
            </tr>
            <%
                }
                %>
        </table>
        <div id="myDiv"></div>
    </s:form>
    <%-- 
        <s:action name="LeaveEntryReportInner" executeResult="true"></s:action>
        --%>
</div>
<script>
    $("#submitButton").click(function(){
    	$(".validateRequired").prop('required',true);
    });
    toggleSession(document.frmLeave.isHalfDay);
</script>