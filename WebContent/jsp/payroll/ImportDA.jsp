<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>


					
<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Import DA" name="title"/>
</jsp:include>

<div id="printDiv" class="leftbox reportWidth">
<div style="width:50%;border: solid 1px #ccc;">
	<p class="past">Quick bulk upload your DA</p> 
	<div style="text-align:center; padding:10px;">
<s:form enctype="multipart/form-data" method="POST" action="ImportDA" >
	
	
		<s:hidden name="other_allowance"></s:hidden>
		<table style="width:100%">
			<tbody><tr>
				<td class="txtlabel alignRight">Select File to Import</td>
				<td><input type="file" id="ImportEmployees_fileUpload" value="" size="20" name="fileUpload"></td>
			</tr>  
			<tr>
				<td align="center" colspan="2"><input type="submit" class="input_button" value="Import File" id="ImportEmployees_0">
</td>
			</tr>
			<tr>
				<td align="right" colspan="2"><a title="Download Sample File" href="import/ImportDA.xlsx" target="_blank">Download Sample File</a></td>
			</tr>
		</tbody></table>
		</s:form>




	</div>
	</div>
</div>