<%@page import="com.konnect.jpms.export.DataStyle"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="com.konnect.jpms.select.FillLevel"%>
<%@page import="com.konnect.jpms.select.FillApproval"%>
<%@page import="com.konnect.jpms.select.FillLeaveType"%>
<%@page import="com.konnect.jpms.select.FillUserType"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.*"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<script type="text/javascript">

function generateSalaryExcel(){
	$('#addRegularizeLeaveBalance').dialog('close');
	//window.location="ExportExcelReport.action?type=type";
	window.location="ExportExcelReport.action";
}

function importLeaveBalance(){
	var updatedate=document.getElementById("updateDate").value;
	var uploadFile=document.getElementById("uploadFile").value;
	if(updatedate!='' && uploadFile!=''){
		if(confirm('Please, Confirm you are uploading balance as on '+updatedate)){
			document.getElementById("frmleavedate").submit();
		}
	}
}
</script>

<div class="aboveform">
<%
List<List<DataStyle>> reportListExport =(List<List<DataStyle>>)request.getAttribute("reportListExport");
session.setAttribute("reportListExport",reportListExport); 
%>


<s:form theme="simple" name="frmleavedate" action="RegularizeLeaveBalance" id="frmleavedate" cssClass="formcss" method="post" enctype="multipart/form-data" >
		<s:hidden name="f_org" />   
		<s:hidden name="paycycle" />
		<s:hidden name="wLocation" />		
		<s:hidden name="f_department" />
		<s:hidden name="updateDate" id="updateDate" />
		
		<table border="0" class="formcss">
			<tr>
				<td class="txtlabel alignRight">Upload:<sup>*</sup></td>
				<td><s:file name="uploadFile" id="uploadFile" ></s:file></td>
			</tr>
			<tr>
				<td class="txtlabel alignRight">&nbsp;</td>
				<td><a title="Download File" onclick="generateSalaryExcel();" href="javascript:void(0)">Download File</a></td>
			</tr>   
			<tr>
				<td>&nbsp;</td>
				<td>
				<%-- <s:submit cssClass="input_button" value="Save" id="btnAddNewRowOk"/> --%>
				<input type="button" class="input_button" value="Import" id="btnAddNewRowOk" onclick="importLeaveBalance();"/>
				</td>
			</tr>
		
		</table>

</s:form>

</div>

				