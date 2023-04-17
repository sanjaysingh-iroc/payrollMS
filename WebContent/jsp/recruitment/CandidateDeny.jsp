<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.*"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>


<script>
	$(function() {
		$("#rdate").datepicker({
			dateFormat : 'dd/mm/yy'
		});
		// $( "#leaveToDate" ).datepicker({dateFormat: 'dd/mm/yy'});
	});

	jQuery(document).ready(function() {
		// binds form submission and fields to the validation engine
		jQuery("#formID").validationEngine();
	});
</script>

<%-- <%
		String strEmpType = (String) session.getAttribute("USERTYPE");
		String strEmpID = (String) session.getAttribute(IConstants.EMPID);
		String strMessage = (String) request.getAttribute("MESSAGE");
		if (strMessage == null) {
			strMessage = "";
		}
		
	%>
	<p class="message"><%=strMessage%></p> --%>
<%
	String strStatus = (String) request.getAttribute("strStatus");
	String strId = (String) request.getAttribute("strId");
	String denyReason = (String) request.getAttribute("denyReason");
	String view = (String) request.getAttribute("view");

	/* System.out.println("in jsp strStatus " + strStatus + "\nstrId "
			+ strId);
	System.out.println("denyReason in jsp "+denyReason); */
%>
<div>
	<s:form id="formID" name="candidateDenyrequest" theme="simple"
		action="candidateDenyrequest" method="POST" cssClass="formcss"
		enctype="multipart/form-data">

		<table border="0" class="formcss" style="float: left">
			<tr>
				<td colspan=2><s:fielderror />
				</td>
			</tr>
			<tr>
				<td><s:hidden name="requestDeny" value="RequestDeny" /> <input
					type="hidden" name="ST" value="<%=strStatus%>" /> <input
					type="hidden" name="RID" value="<%=strId%>" /></td>
			</tr>

			<tr>
				<td height="10px">&nbsp;</td>
			</tr>
			<tr>
				<td class="txtlabel alignRight" valign="top">Reason:</td>
				<td><textarea name="candidate_deny_reason" id="candidate_deny_reason"
						cols="26" rows="4"><%=(denyReason==null ? "" : denyReason)%></textarea></td>
			</tr>
			<tr>
				<td>&nbsp;</td>
				<td>
					<%
						if (view == null || !view.equals("view")) {
					%> <s:submit cssClass="input_button" value="Submit" align="center" />
					<%
						}
					%>
				</td>
			</tr>
		</table>
	</s:form>
</div>



