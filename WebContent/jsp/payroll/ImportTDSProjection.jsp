<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%-- <script type="text/javascript" src="scripts/charts/jquery.min.js"></script> --%>

<script>

function generateTdsExcel(){
	 var strgrade = document.getElementById("strgrade").value;
	var strLevel = document.getElementById("strLevel").value;
	var strdepartment = document.getElementById("strdepartment").value;
	var strLocation = document.getElementById("strLocation").value;
	var strorg = document.getElementById("strorg").value;
	var strpaymentmode = document.getElementById("strpaymentmode").value;
	var financialYear = document.getElementById("financialYear").value;
	window.location="ImportTDSProjection.action?strorg="+strorg+"&strgrade="+strgrade+"&strLevel="+strLevel+"&strdepartment="+strdepartment
			+"&strLocation="+strLocation+"&strpaymentmode="+strpaymentmode+"&financialYear="+financialYear+"&exceldownload=true";
			
}
//addLoadEvent(prepareInputsForHints);

</script>
<s:form theme="simple" id="frm_ImportTDSProjection" action="ImportTDSProjection" method="POST" cssClass="formcss" enctype="multipart/form-data">
	<s:hidden name="strgrade" id="strgrade"></s:hidden>  
	<s:hidden name="strLevel" id="strLevel"></s:hidden>
	<s:hidden name="strdepartment" id="strdepartment"></s:hidden>
	<s:hidden name="strLocation" id="strLocation"></s:hidden>
	<s:hidden name="strorg" id="strorg"></s:hidden>
	<s:hidden name="strpaymentmode" id="strpaymentmode"></s:hidden>
	<s:hidden name="financialYear" id="financialYear"></s:hidden>
	<table class="table">
		<tr>
			<td class="txtlabel alignRight">Upload:<sup>*</sup></td>
			<td><s:file accept=".xls" name="fileUpload" id="fileUpload" cssClass="validateRequired"></s:file></td>
		</tr>	
		<tr>
			<td class="txtlabel alignRight">&nbsp;</td>
			<td><a title="Download File" onclick="generateTdsExcel();" href="javascript:void(0)">Download Sample File</a></td>
		</tr> 	  
		<tr>
			<td>&nbsp;</td>
			<td><s:submit cssClass="btn btn-primary" name="submit" value="Import" id="btnAddNewRowOk" /></td>
		</tr>
	</table>	
</s:form>