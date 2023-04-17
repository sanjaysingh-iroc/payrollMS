<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ taglib uri="/struts-tags" prefix="s"%>

<%-- <style>
.bootstrap-timepicker-widget.timepicker-orient-bottom:after {
display: none;
}
</style> --%>
<style>
/* Created By Dattatray Date:07-10-21 */
.bootstrap-datetimepicker-widget.dropdown-menu {
        width: auto;
    }

    .timepicker-picker table td a span,
    .timepicker-picker table td,
    .timepicker-picker table td span {
        height: 25px !important;
        line-height: 25px !important;
        vertical-align: middle;
        width: 25px !important;
        padding: 0px !important;
    }
</style>
<%
	UtilityFunctions uF = new UtilityFunctions(); 
	CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions); 
	
	String strServiceId=request.getParameter("SID");
	String strEmpId=request.getParameter("EID");
	String strDate=request.getParameter("DATE");
	String strStatus=request.getParameter("S");
	String strApStatus = (String) request.getParameter("apStatus");
	String strOTMinuteStatus = (String) request.getParameter("strOTMinuteStatus");
	String strInOutType = (String)request.getAttribute("strInOutType");// Created By Dattatray Date:07-10-21
	//System.out.println("strInOutType : "+strInOutType);
	String timeApprovalType = (String)request.getAttribute("timeApprovalType");
%>



<script>
function validateTime(frmAddReason){
	
	 if(frmAddReason.strStartTime.value==""){
		alert('Please enter the valid start time');
		return false;
	}if(frmAddReason.strSEndTime.value==""){
		alert('Please enter the valid end time');
		return false;
	} 
	return true;
}

$(function () {
	/* var date_yest = new Date();
    var date_tom = new Date();
    date_yest.setHours(0,0,0);
    date_tom.setHours(23,59,59); 
	$("input[name='strStartTime']").datetimepicker({
		format: 'HH:mm',
		minDate: date_yest
    }).on('dp.change', function(e){ 
    	$("input[name='strEndTime']").data("DateTimePicker").minDate(e.date);
    });
	
	$("input[name='strEndTime']").datetimepicker({
		format: 'HH:mm',
		maxDate: date_tom
    }).on('dp.change', function(e){ 
    	$("input[name='strStartTime']").data("DateTimePicker").maxDate(e.date);
    }); */
    $("input[name='strStartTime']").datetimepicker({format: 'HH:mm'});
    $("input[name='strEndTime']").datetimepicker({format: 'HH:mm'});
});

</script>


<s:form action="AddClockEntries" name="frmAddClockEntries" theme="simple" method="POST">

<input type="hidden" name="strServiceId" value="<%=strServiceId%>" />
<input type="hidden" name="strEmpId" value="<%=strEmpId%>" />
<input type="hidden" name="strDate" value="<%=strDate%>" />
<input type="hidden" name="strStatus" value="<%=strStatus%>" />
<input type="hidden" name="strApStatus" value="<%=strApStatus%>" />
<input type="hidden" name="strOTMinuteStatus" value="<%=strOTMinuteStatus%>" />
<s:hidden name="strApStatusTmp"></s:hidden>
<s:hidden name="paycycle"></s:hidden>
<s:hidden name="f_org"></s:hidden>
<s:hidden name="location"></s:hidden> 
<s:hidden name="level"></s:hidden>
<s:hidden name="strE" />
<center>
<table class="table table_no_border autoWidth">
	<%if(uF.parseToBoolean(strOTMinuteStatus)){ %>
		<tr>
			<td colspan="2">Manual update in Clock Entries shall revoke the approved Overtime adjustments if any</td>
		</tr>
	<%} %>
	<!-- Started By Dattatray Date:01-11-21-->
	

<%
	if(timeApprovalType !=null &&  !timeApprovalType.isEmpty() && timeApprovalType.equals("individualCE")){
%>
	<tr>
		<td>Start Time:</td>
		<td nowrap="nowrap"><s:textfield name="strStartTime" cssStyle="width:65px !important;"/></td>
	</tr>
	<tr>
		<td nowrap="nowrap">End Time:</td>
		<td><s:textfield name="strEndTime" cssStyle="width:65px !important;"/></td>
	</tr>
	<%} else{ %>
		<%
		
			if(strInOutType !=null && strInOutType.equalsIgnoreCase("IN_OUT")){
		%>
			<tr>
				<td>In Time:</td>
				<td nowrap="nowrap"><s:textfield name="strStartTime" cssStyle="width:65px !important;" required="true"/></td>
			</tr>
			<tr>
				<td nowrap="nowrap">Out Time:</td>
				<td><s:textfield name="strEndTime" cssStyle="width:65px !important;" required="true"/></td>
			</tr>
			<%} else if(strInOutType !=null && strInOutType.equalsIgnoreCase("OUT")){ %>
				<tr>
					<td nowrap="nowrap">Out Time:</td>
					<td><s:textfield name="strEndTime" cssStyle="width:65px !important;" required="true"/></td>
				</tr>
			<%} %>
	
	<%} %>
	<!-- Ended By Dattatray Date:01-11-21-->
	<tr> 
		<td colspan="2">
		<%-- <%if(uF.parseToBoolean(CF.getIsExceptionAutoApprove()) && uF.parseToInt(strApStatus) != 1){ %> --%>
			<input type="button" class="btn btn-primary" value="Approve Clock Entries" onclick="updateSttings('<%=request.getParameter("DATE") %>','<%=request.getParameter("EID") %>','<%=request.getParameter("SID") %>','<%=request.getParameter("AS") %>','<%=request.getParameter("AE") %>','<%=request.getParameter("divid") %>',this.form.strStartTime.value,this.form.strEndTime.value,'<%=request.getAttribute("strE") %>','<%=request.getParameter("E") %>', '<%=request.getAttribute("strApStatusTmp") %>','<%=strOTMinuteStatus%>');"/>			
		<%-- <%}else{ %>
			<s:submit cssClass="btn btn-primary" value="Approve Clock Entries" onclick="return validateTime(this.form);"></s:submit>
		<%} %> --%>
		</td>
	</tr>
	
</table>
</center>

</s:form>