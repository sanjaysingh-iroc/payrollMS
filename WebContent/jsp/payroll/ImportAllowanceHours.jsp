<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%-- <script type="text/javascript" src="scripts/charts/jquery.min.js"></script> --%>
<script>
$("#btnAddNewRowOk").click(function(){
	$(".validateRequired").prop('required',true);
});
//addLoadEvent(prepareInputsForHints);

function generateExcel(){
	var paycycle = document.getElementById("paycycle").value;
 	var f_org = document.getElementById("f_org").value;
 	var f_level = document.getElementById("f_level").value;
 	
	window.location = "ImportAllowanceHours.action?formType=download&paycycle="+paycycle+"&f_org="+f_org+"&f_level="+f_level;
} 
</script>

<s:form theme="simple" id="formImportAllowanceHours" action="ImportAllowanceHours" method="POST" cssClass="formcss" enctype="multipart/form-data">
	<s:hidden name="paycycle" id="paycycle"></s:hidden>
	<s:hidden name="f_org" id="f_org"></s:hidden>
	<s:hidden name="f_level" id="f_level"></s:hidden>
	<div class="row row_without_margin">
		<div class="col-lg-12 col-md-12 col-sm-12">
			<table class="table table_no_border">		
				<tr>
					<td class="txtlabel alignRight">Upload:<sup>*</sup></td>
					<td><s:file name="uploadFile" id="uploadFile" cssClass="validateRequired"></s:file></td>
				</tr>
				
				<tr>
					<td class="txtlabel alignRight">&nbsp;</td>
					<td><a title="Download File" onclick="generateExcel();" href="javascript:void(0)">Download Sample File</a></td>
				</tr>   
				
				<tr>
					<td>&nbsp;</td>
					<td><s:submit cssClass="btn btn-primary" name="submit" value="Import" id="btnAddNewRowOk"/></td>
				</tr>
			</table>
		</div>
	</div>	
</s:form>