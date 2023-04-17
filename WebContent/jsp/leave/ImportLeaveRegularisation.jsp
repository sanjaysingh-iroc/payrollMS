<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<script>

function generateImportLeaveRegulariseExcel(){
	var f_org=document.getElementById("f_org").value;
	var location = getSelectedValue("f_strWLocation");
	var department = getSelectedValue("f_department");
	var service = getSelectedValue("f_service");
	var level = getSelectedValue("f_level");
	var strEffectiveDate = document.getElementById("idEffectiveDate").value;
	
	var paramValues = "";
	paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service+'&strLevel='+level+'&strEffectiveDate='+strEffectiveDate;
	window.location="ImportLeaveRegularisation.action?f_org="+f_org+paramValues+"&exceldownload=true";
			
}
//addLoadEvent(prepareInputsForHints);
</script>
<s:form theme="simple" id="frm_ImportLeaveRegularise" action="ImportLeaveRegularisation" method="POST" cssClass="formcss" enctype="multipart/form-data">
	<s:hidden name="f_org" id="f_org"></s:hidden>  
	<s:hidden name="f_strWLocation" id="f_strWLocation"></s:hidden>
	<s:hidden name="f_department" id="f_department"></s:hidden>
	<s:hidden name="f_service" id="f_service"></s:hidden>
	<s:hidden name="f_level" id="f_level"></s:hidden>
	<s:hidden name="idEffectiveDate" id="idEffectiveDate"></s:hidden>
	
	<table class="table">
		<tr>
			<td class="txtlabel alignRight">Upload:<sup>*</sup></td>
			<td><s:file accept=".xls" name="fileUpload" id="fileUpload" cssClass="validateRequired"></s:file></td>
		</tr>
		<tr>
			<td class="txtlabel alignRight">&nbsp;</td>
			<td><a title="Download File" onclick="generateImportLeaveRegulariseExcel();" href="javascript:void(0)">Download Sample File</a></td>
		</tr>		  
		<tr>
			<td>&nbsp;</td>
			<td><s:submit cssClass="btn btn-primary" name="submit" value="Import" id="btnAddNewRowOk" /></td>
		</tr>
	</table>	
</s:form>