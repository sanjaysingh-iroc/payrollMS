

<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<div class="aboveform">

	<s:form theme="simple" name="frmImportJobProfiles" action="ImportJobProfiles" id="frmImportJobProfiles" cssClass="formcss" method="post" enctype="multipart/form-data" >
		<s:hidden name="orgId" />
		<table border="0" class="table table_no_border">
			<tr>
				<td class="txtlabel alignRight">Select File to Import:<sup>*</sup></td>
				<!-- Created by Dattatray Date : 07-July-21 Note : added accept and onchange properties -->
				<td><s:file name="fileUpload" id="fileUpload" cssClass="validateRequired" accept=".xlsx" onchange="readFileURL(this, 'file');"></s:file></td>
				<%-- <td><s:file name="fileUpload" id="fileUpload" cssClass="validateRequired"></s:file></td> --%>
			</tr>
			<tr>
				<td colspan="2" align="center"><s:submit cssClass="btn btn-primary" value="Import" id="btnImport"/></td>
			</tr>
			<tr>
				<td colspan="2" align="right"> 
					<a target="_blank" href="<%=request.getContextPath() %>/import/ImportJobProfiles.xlsx" title="Download Sample File" ><i class="fa fa-download" aria-hidden="true"></i> Sample File</a>
				</td>
			</tr>
		</table>
	</s:form>

</div>

<script>
	$(document).ready(function(){
		$("#btnImport").click(function(){
			$(".validateRequired").prop('required', true);
		});
	});
	
	// Start Dattatray Date:07-July-2021
	 function readFileURL(input, targetDiv) {
	      	fileValidation();
	          if (input.files && input.files[0]) {
	              var reader = new FileReader();
	              reader.onload = function (e) {
	                  $('#'+targetDiv).attr('path', e.target.result);
	              };
	              reader.readAsDataURL(input.files[0]);
	          }
	      }
	
	 function fileValidation() {
        var fileInput = document.getElementById('fileUpload');
        var filePath = fileInput.value;
        var allowedExtensions = /(\.xlsx)$/i;
          
        if (!allowedExtensions.exec(filePath)) {
            alert('Please select .xlsx format');
            fileInput.value = '';
            return false;
        } 
    }
	// End Dattatray Date:07-July-2021
	
</script>