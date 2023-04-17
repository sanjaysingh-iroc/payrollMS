<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<%
	UtilityFunctions uF = new UtilityFunctions();
	String strTitle = (String) request.getAttribute(IConstants.TITLE);
	String formId = (String) request.getAttribute("formId");
%>


<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script>
$(document).ready( function () {
	$("#frmEditForm_submit").click(function(){
		$(".validateRequired").prop('required',true);
	});
});
</script>


<div>
	<s:form name="frmEditForm" id="frmEditForm" theme="simple" action="EditForm" method="POST">
		<s:hidden name="formId" id="formId"></s:hidden>
		<s:hidden name="strOrg" id="strOrg"></s:hidden>
		<%-- <s:hidden name="strNode" id="strNode"></s:hidden> --%>
       	<s:hidden name="userscreen" id="userscreen"></s:hidden>
		<s:hidden name="navigationId" id="navigationId"></s:hidden>
		<s:hidden name="toPage" id="toPage"></s:hidden>
		<div style="width:100%; float:left">
			<s:hidden name="operation" value="U"/>
			<table border="0" class="table table_no_border" style="float: left">
				<tr>
					<td class="txtlabel alignRight">Organisation:<sup>*</sup></td>
					<td><%=uF.showData(((String)request.getAttribute("strOrgName")),"") %></td>
				</tr>
				<tr>
					<td class="txtlabel alignRight">Form Title:<sup>*</sup></td>
					<td><s:textfield name="strFormName" id="strFormName" cssClass="validateRequired" /></td>
				</tr>
				<tr>
					<td class="txtlabel alignRight">Form Node:<sup>*</sup></td>
					<td><%-- <%=uF.showData(((String)request.getAttribute("strNodeName")),"") %> --%>
						<s:select name="strNode" id="strNode" list="nodeList" listKey="nodeId" listValue="nodeName" headerKey=""
								headerValue="Select Node" cssClass="validateRequired"></s:select>
					</td>
				</tr>
				<tr>
					<td class="txtlabel alignRight">&nbsp;</td>
					<td><s:submit value="Update" cssClass="btn btn-primary" name="submit"></s:submit></td>
				</tr>
			</table>
		</div>
	</s:form>
</div>
