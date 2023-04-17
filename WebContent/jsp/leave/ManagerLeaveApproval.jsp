<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ page import="com.konnect.jpms.util.*"%>

<%@ taglib prefix="s" uri="/struts-tags" %>

<style type="text/css">
	a{color:#993300; text-decoration:none;}
	#div1 {
	width:50%;
	display: none;
	padding:5px;
	border:1px solid;
	background-color:#7FFFD4;
	}     
	#click_here{
	border:2px solid #FFEFEF;
	}
</style>
  
<script>  
    $(function() {
        $( "#approvalFromTo" ).datepicker({dateFormat: 'dd/mm/yy'});
        $( "#approvalToDate" ).datepicker({dateFormat: 'dd/mm/yy'});
    });  

    jQuery(document).ready(function(){
        // binds form submission and fields to the validation engine
        jQuery("#frm_emp").validationEngine();
    });
    
	addLoadEvent(prepareInputsForHints);     
</script>

<s:if test="type!=type">
<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Approve/Deny Leave" name="title"/>
</jsp:include>
</s:if>

<script type="text/javascript">
     
		$(function()
		{
			$("#click_here").click(function(event) {
				event.preventDefault();
				$("#div1").slideToggle();
			});
			
			$("#div1 a").click(function(event) {
				event.preventDefault();
				$("#div1").slideUp();
			});
			
		});
		
		 function toggleSession(obj){
		    	
		    	if(obj.checked){
		    		document.getElementById("idSession").style.display="block";
		    		document.getElementById("idLeaveTo").style.display="none";
		    	}else{
		    		document.getElementById("idSession").style.display="none";
		    		document.getElementById("idLeaveTo").style.display="table-row";
		    	}
		    }
		    
		 
</script>

<%
	String strEmpType = (String) session.getAttribute("USERTYPE");
	String strMessage = (String) request.getAttribute("MESSAGE");
	boolean flag=(Boolean)request.getAttribute("flag");
	String work_flow_id=(String)request.getAttribute("work_flow_id");
	if (strMessage == null) {
		strMessage = "";
	}
%>

<div class="leftbox reportWidth">

<p class="message"><%=strMessage%></p>

<s:form theme="simple" name="frmLeave"	action="ManagerLeaveApproval" id="frm_emp" method="POST" cssClass="formcss" enctype="multipart/form-data">
<s:hidden name="leaveId" />
<s:hidden name="empId" />
<s:hidden name="userId" /> 
<%-- <s:hidden name="leaveFromTo"/>
<s:hidden name="leaveToDate"/> --%>
<s:hidden name="entryDate"/>
<s:hidden name="reason"/>
<s:hidden name="strCompensatory"/>
<s:hidden name="type" />
<input type="hidden" name="work_flow_id" value="<%=work_flow_id%>"/>
<s:hidden name="leaveStatus"></s:hidden>
<s:hidden name="strStartDate"></s:hidden>
<s:hidden name="strEndDate"></s:hidden>

<table border="0" class="formcss">

<tr><td colspan=2><s:fielderror/></td></tr>


<tr><td height="10px">&nbsp;</td></tr>
<tr><td class="txtlabel alignRight">Emp Name:<sup>*</sup></td><td><s:label name="empName" label="Emp Name"/></td></tr>
<tr><td class="txtlabel alignRight" valign="top">Half day:</td><td height=50 valign="top"><s:checkbox name="isHalfDay" onclick="toggleSession(this)" cssStyle="float:left" />  <div id="idSession" style="float:left;width:100px;"><s:radio name="strSession" list="strWorkingSession" listKey="strHaldDayId" listValue="strHaldDayName"/></div> </td></tr>
<tr><td class="txtlabel alignRight">Approval From Date:<sup>*</sup></td><td><s:textfield name="approvalFromTo" id="approvalFromTo" label="Approval From Date"  cssClass="validateRequired" ></s:textfield><span class="hint">On Leave From Date.<span class="hint-pointer">&nbsp;</span></span></td></tr>
<tr id="idLeaveTo"><td class="txtlabel alignRight">Approval To Date:<sup>*</sup></td><td><s:textfield name="approvalToDate" id="approvalToDate" label="Approval To Date"  cssClass="validateRequired" ></s:textfield><span class="hint">Leave End Date.<span class="hint-pointer">&nbsp;</span></span></td></tr>
<tr><td class="txtlabel alignRight">Leave type:<sup>*</sup></td><td><s:select name="typeOfLeave" label="Leave type" listKey="leaveTypeId" listValue="leavetypeName" headerKey="0" headerValue="Select Leave Type" list="leaveTypeList" key="" required="true"  cssClass="validateRequired" /><span class="hint">Select Leave Type.<span class="hint-pointer">&nbsp;</span></span></td></tr>
<tr><td class="txtlabel alignRight">Reason:</td><td>
<!-- <a href="" id= click_here>..Read</a> -->

<s:property value="reason"/>
</td></tr>

<%-- <s:label id="click_here" value="..Read" /></td></tr> --%>

<tr>
	<td></td>
	<td>
	<div id="div1">
	<a href="#" class="close">[x]</a>
	<s:label name="reason" />
	</div>
	</td>
</tr>


<%if(request.getAttribute("RequiredDocumentName")!=null){ %>
	<tr><td class="txtlabel alignRight" valign="top">Document Attached:<sup>*</sup></td><td><a href="<%=request.getAttribute("RequiredDocument")%>" target="_blank">Supported Document</a></td></tr>
<%} %>

<tr><td class="txtlabel alignRight" valign="top">Manager  Reason:</td><td><s:textarea cols="22" rows="05" name="managerReason" label="Manager  Reason"/></td></tr>
<tr><td class="txtlabel alignRight">Approve/Deny:<sup>*</sup></td><td><s:select name="isapproved" label="Is Approved" listKey="approvalId" listValue="approvalName" headerKey="" headerValue="Select Approval Type" list="approvalList" key="" required="true"  cssClass="validateRequired"/><span class="hint">Select Approval Type.<span class="hint-pointer">&nbsp;</span></span></td></tr>
<%--if(flag){ --%>
<tr><td></td><td><s:submit  cssClass="input_button" value="Leave Approval" align="center" /></td></tr>
	<%--} --%>
</table>

</s:form>

</div>



<script>toggleSession(document.frmLeave.isHalfDay);</script>