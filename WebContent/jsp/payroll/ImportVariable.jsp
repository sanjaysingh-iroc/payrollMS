<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<script>

function generateImportVariable(){
	var org = document.getElementById("f_org").value;
	var paycycle = document.getElementById("paycycle").value;
	var strPaycycleDuration = document.getElementById("strPaycycleDuration").value;
	var f_salaryhead = document.getElementById("f_salaryhead").value;
	var location = getSelectedValue("f_strWLocation");
	var department = getSelectedValue("f_department");
	var level = getSelectedValue("f_level");
	var strGrade = getSelectedValue("f_grade");
	var strEmployeType = getSelectedValue("f_employeType");
	var salary_head_name = document.getElementById("salaryheadname").value;
	var paramValues = "";
	paramValues = '&location='+location+'&strDepartment='+department+'&strLevel='+level+'&paycycle='+paycycle
	+'&strPaycycleDuration='+strPaycycleDuration+'&f_salaryhead='+f_salaryhead+'&strGrade='+strGrade+'&strEmptype='+strEmployeType+'&salaryheadname='+salary_head_name;
	window.location="ImportVariable.action?f_org="+org+paramValues+"&exceldownload=true";
			
}
//addLoadEvent(prepareInputsForHints);

</script>
<s:form theme="simple" id="frm_ImportVariable" action="ImportVariable" method="POST" cssClass="formcss" enctype="multipart/form-data">
	<s:hidden name="f_org" id="f_org"></s:hidden>  
	<s:hidden name="paycycle" id="paycycle"></s:hidden>
	<s:hidden name="strPaycycleDuration" id="strPaycycleDuration"></s:hidden>
	<s:hidden name="f_salaryhead" id="f_salaryhead"></s:hidden>
	<s:hidden name="f_strWLocation" id="f_strWLocation"></s:hidden>
	<s:hidden name="f_department" id="f_department"></s:hidden>
	<s:hidden name="f_level" id="f_level"></s:hidden>
	<s:hidden name="f_grade" id="f_grade"></s:hidden> 
	<s:hidden name="f_employeType" id="f_employeType"></s:hidden>
	<s:hidden name="salaryheadname" id="salaryheadname"></s:hidden>
	<table class="table">
		<tr>
			<td class="txtlabel alignRight">Upload:<sup>*</sup></td>
			<td><s:file name="fileUpload" id="fileUpload" accept=".xls"  cssClass="validateRequired"></s:file></td>
		</tr>
		<tr>
			<td class="txtlabel alignRight">&nbsp;</td>
			<td><a title="Download File" onclick="generateImportVariable();" href="javascript:void(0)">Download Sample File</a></td>
		</tr>		  
		<tr>
			<td>&nbsp;</td>
			<td><s:submit cssClass="btn btn-primary" name="submit" value="Import" id="btnAddNewRowOk" /></td>
		</tr>
	</table>	
</s:form>