<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@ taglib uri="/struts-tags" prefix="s"%>

<%
Map hmEmpProDetails = (HashMap)request.getAttribute("hmEmpProDetails");%>

<s:form action="ExtendProbationDays" name="frmAddReason" theme="simple" method="POST">
	<s:hidden name="empid" id="empid"></s:hidden>
	<s:hidden name="empname" id="empname"></s:hidden>
	<s:hidden name="paycycle" id="paycycle"></s:hidden>
	<s:hidden name="f_org" id="f_org"></s:hidden>
	<s:hidden name="f_strWLocation" id="f_strWLocation"></s:hidden>
	<s:hidden name="f_department" id="f_department"></s:hidden>
	<s:hidden name="f_service" id="f_service"></s:hidden> 
	<s:hidden name="divid" id="divid"></s:hidden>
	Joining date: <%=hmEmpProDetails.get("JOINING_DATE") %><br/>
	Existing probation end date : <%=hmEmpProDetails.get("PROBAION_DATE") %><br/> 
	Extend probation by <input type="text" name="extendDays" id="extendDays" style="width: 50px;"/>&nbsp;days
	<br/>
	Reason for extension:<br/>
	<s:textarea cols="30" rows="4" name="strReason" id="strReason"></s:textarea><br/>
	<input type="button" class="input_button" value="Update" 
	onclick="validateReason(this.form.strReason.value,'<%=request.getAttribute("empid") %>','<%=request.getAttribute("empname") %>','<%=request.getAttribute("paycycle") %>','<%=request.getAttribute("f_org") %>','<%=request.getAttribute("f_strWLocation") %>','<%=request.getAttribute("f_department") %>','<%=request.getAttribute("f_service") %>','<%=request.getAttribute("divid") %>',this.form.extendDays.value);"/>
</s:form>