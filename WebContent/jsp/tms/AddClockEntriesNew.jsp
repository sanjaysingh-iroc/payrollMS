<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ taglib uri="/struts-tags" prefix="s"%>


<%
UtilityFunctions uF = new UtilityFunctions();
CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions); 

String strServiceId=request.getParameter("SID");
String strEmpId=request.getParameter("EID");
String strDate=request.getParameter("DATE");
String strStatus=request.getParameter("S");
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

</script>


<s:form action="AddClockEntriesNew" name="frmAddClockEntries" theme="simple" method="POST">

<s:hidden name="paycycle"></s:hidden>
<s:hidden name="org"></s:hidden>
<s:hidden name="location"></s:hidden>
<s:hidden name="level"></s:hidden>
<s:hidden name="type" value="E" />
<s:hidden name="strDate" />
<s:hidden name="strEmpId" />


<table>
	<tr>
		<td class="label">Start Time</td>
		<td><s:textfield name="strStartTime" cssStyle="width:65px"/></td>
	</tr>
	<tr>
		<td class="label">End Time</td>
		<td><s:textfield name="strEndTime" cssStyle="width:65px"/></td>
	</tr>
	
	<tr> 
		<td colspan="2">
		<s:submit cssClass="input_button" value="Approve Clock Entries" onclick="return validateTime(this.form);"></s:submit>
		
		</td>
	</tr>
	
</table>


	
	

</s:form>