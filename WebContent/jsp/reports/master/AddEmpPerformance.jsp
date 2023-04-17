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
		jQuery("#frmAddEmpPerformance").validationEngine();
	});
</script>

<%
	String closeReason = (String) request.getAttribute("closeReason");
%>
<div id="newReviewDiv">

	<s:form id="frmAddEmpPerformance" name="frmAddEmpPerformance" theme="simple" action="AddEmpPerformance" method="POST" cssClass="formcss" enctype="multipart/form-data">

		<s:hidden name="reviewId"></s:hidden>
       	<s:hidden name="fromPage"></s:hidden>
       	<s:hidden name="operation" value="Add"></s:hidden>
       
		<table border="0" class="formcss" style="width: 97%; float: left">
			<tr>
				<td colspan=2><s:fielderror />
				</td>
			</tr>
			<tr>
				<td class="txtlabel alignRight" valign="top">Review Name:<sup>*</sup> </td>
				<td>
					<s:textfield name="reviewName" id="reviewName" cssClass="validateRequired" cssStyle="width: 400px;" readonly="true"/>
				</td>
			</tr>
			<tr>
				<td class="txtlabel alignRight" valign="top">Upload File:<sup>*</sup> </td>
				<td>
					<s:file name="reviewFile" id="reviewFile" cssClass="validateRequired"></s:file>
				</td>
			</tr>
			<tr>
				<td colspan="2" align="center">
					 <s:submit cssClass="input_button" value="Upload" align="center" />
				</td>
			</tr>
		</table>
	</s:form>

</div>



