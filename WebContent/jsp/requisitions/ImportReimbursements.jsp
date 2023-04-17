<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<script>
	$(function () {
          // binds form submission and fields to the validation engine
    	  $("body").on("click","#submit",function(){
  	    	$(".validateRequired").prop('required',true);
  	    });
    });
      
	$("#frmImportReimbursements").submit(function(e){
		e.preventDefault();
		var form_data = $("form[name='frmImportReimbursements']").serialize();
     	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
     	$.ajax({
 			url : "ImportReimbursements.action",
 			data: form_data,
 			cache : false,
 			success : function(res) {
 				$("#divResult").html(res);
 			}
 		});
	});
	
</script>


	<div class="box-body" style="padding: 5px; overflow-y: auto;">
		<div style="text-align:center; padding:10px;">
			<s:form theme="simple" name="frmImportReimbursements" id="frmImportReimbursements" action="ImportReimbursements" method="POST" theme="simple" enctype="multipart/form-data">
				<table class="table table_no_border form-table">
					<tr>
						<td class="txtlabel alignRight">Select File to Import:<sup>*</sup></td>
						<td><s:file name="fileUpload" label="Select a File to upload" size="20" cssClass="validateRequired"/></td> 
					</tr>
					<tr>
						<td colspan="2" align="center"><s:submit value="Import File" align="center" cssClass="btn btn-primary" /></td>
					</tr>
					<tr>
						<td colspan="2" align="center"><a target="_blank" href="import/ImportReimbursements.xlsx" title="Download Sample File">Download Sample File</a></td>
					</tr>
				</table>
			</s:form>
		</div>
	</div>
	<!-- /.box-body -->

	<% UtilityFunctions uF = new UtilityFunctions(); %>
	<div style="width:45%;float:right;border: solid 0px #ccc;text-align: center; margin: 9px 312px;">
	<p style="color: green"><s:property value="message"/></p>
		<%=uF.showData((String)session.getAttribute(IConstants.MESSAGE),"") %>
	</div>
	
	
	<div style="float: left;width:100%;margin-top: 10px;">
		<%
		if(request.getAttribute("sbMessage")!=null) {
			out.println(request.getAttribute("sbMessage"));	
		}
		%>
	</div>


 

