<%@ taglib prefix="s" uri="/struts-tags"%>

<%-- <s:select theme="simple" cssClass="validateRequired" name="typeOfLeave" listKey="leaveTypeId" 
listValue="leavetypeName" headerKey="" headerValue="Select Leave Type" list="leaveTypeList" key="" 
required="true"  onchange="showDocument(this.options[this.selectedIndex].value); getContent('myDiv', 'GetLeaveStatus.action?EMPID='+document.frmLeave.strEmpId.options[document.frmLeave.strEmpId.selectedIndex].value+'&LTID='+this.options[this.selectedIndex].value+'&D1='+document.frmLeave.leaveFromTo.value+'&D2='+document.frmLeave.leaveToDate.value);  getLeaveTypedetails(this.value,'1');"/>
<span class="hint">Select Leave Type. These are the leave types available to you. If you do not find yours please speak to HR.<span class="hint-pointer">&nbsp;</span></span> --%>


<s:select theme="simple" cssClass="validateRequired" name="typeOfLeave" id="typeOfLeave" listKey="leaveTypeId" listValue="leavetypeName" headerKey="" headerValue="Select Leave Type" list="leaveTypeList" key="" required="true"  onchange="getLeaveTypedetails(this.value,'1');"/><span class="hint">Select Leave Type. These are the leave types available to you. If you do not find yours please speak to HR.<span class="hint-pointer">&nbsp;</span></span>