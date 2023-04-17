<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%-- <script type="text/javascript" src="scripts/charts/jquery.min.js"></script> --%>
<script>
$("#btnAddNewRowOk").click(function(){
	$(".validateRequired").prop('required',true);
});
//addLoadEvent(prepareInputsForHints);

function generateExcel(){
	var financialYear = document.getElementById("financialYear").value;
 	var strOrg = document.getElementById("strOrg").value;
 	var strLevel = document.getElementById("strLevel").value;
 	var strSalaryHeadId = document.getElementById("strSalaryHeadId").value;
 	
	window.location = "ImportAnnualVariable.action?formType=download&financialYear="+financialYear+"&strOrg="+strOrg+"&strLevel="+strLevel+"&strSalaryHeadId="+strSalaryHeadId;
} 

/* $("#formImportAnnualVariable").submit(function(e){
	//alert("check ........");
	e.preventDefault();
	var form_data = $("#formImportAnnualVariable").serialize();
 	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
 	$.ajax({
			url : "ImportAnnualVariable.action",
			data: form_data,
			cache : false,
			success : function(res) {
				$("#divResult").html(res);
			},
			cache: false,
	        contentType: false,
	        processData: false
		});
}); */

</script>
 


<s:form theme="simple" id="formImportAnnualVariable" action="ImportAnnualVariable" method="POST" cssClass="formcss" enctype="multipart/form-data">
	<s:hidden name="financialYear" id="financialYear"></s:hidden>
	<s:hidden name="strOrg" id="strOrg"></s:hidden>
	<s:hidden name="strLevel" id="strLevel"></s:hidden>
	<s:hidden name="strSalaryHeadId" id="strSalaryHeadId"></s:hidden>
	<s:hidden name="callFrom" id="callFrom"></s:hidden>
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
