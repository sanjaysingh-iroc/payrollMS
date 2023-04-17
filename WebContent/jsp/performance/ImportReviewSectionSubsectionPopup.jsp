<%-- <%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.*"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>


<%
	String opt = (String) request.getAttribute("option");
	//System.out.println("opt in new " + opt);
	String count = (String) request.getAttribute("count");
	//System.out.println("count in new " + count);
%>
<div>
	<s:form id="formID" name="frmReviewSectionSubsection" theme="simple" action="ImportReviewSectionSubsection" method="POST" cssClass="formcss">
		<s:hidden name="reviewId"></s:hidden>
	
		<table class="tb_style" style="width: 100%">
			<tr>
				<th width="30%" align="right">Select File</th>
				<td>
				<input type="file" name="uploadFile"/>
				<s:hidden name="count" id="count"></s:hidden>
				<select name="questionSelect" id="questionSelect" style="width: 80%;"><option value="">Select Question</option><%=opt %></select>
				</td>
			</tr>
			 
			<!-- <tr>
				<th align="right">&nbsp;</th>
				<td>&nbsp;</td>
			</tr> -->

		</table>
		
		<div align="center">
			<s:submit name="submit" cssClass="input_button" value="Submit"></s:submit>
			<!-- <input type="button" value="Ok" class="input_button" name="ok" onclick="setQuestionInTextfield();" /> -->
		</div>
	</s:form>
</div>

 --%>

<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<script type="text/javascript">

/* function generateSalaryExcel(){
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
} */
</script>

<div class="aboveform">

<s:form theme="simple" name="frmReviewSectionSubsection" action="ImportReviewSectionSubsection" id="frmReviewSectionSubsection" cssClass="formcss" method="post" enctype="multipart/form-data" >
		<s:hidden name="reviewId" />
		<s:hidden name="orientation" /> 
		<s:hidden name="appFreqId" />
		<s:hidden name="importType" />
		<table border="0" class="table table_no_border">
			<tr>
				<td class="txtlabel alignRight">Upload:<sup>*</sup></td>
				<td><s:file name="uploadF" id="uploadF" ></s:file></td>
			</tr>
			  
			<tr>
				<td>&nbsp;</td>
				<td>
				<s:submit cssClass="btn btn-primary" value="Import" id="btnImport"/> 
				<!-- <input type="button" class="input_button" value="Import" id="btnAddNewRowOk"/> -->
				</td>
			</tr>
		
		</table>

</s:form>

</div>

				



