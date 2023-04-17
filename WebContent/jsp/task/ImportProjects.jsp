<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<script>
	$(document).ready(function(){
		$("#btnSubmit").click(function(){
			$(".validateRequired").prop('required', true);
		});
	});
</script>

<%-- <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Import Projects" name="title"/>
</jsp:include>
 --%>
 

<div style="float:left;">
	<div style="width:100%; float:left; border: solid 1px #ccc; margin-top: 40px">
	<p class="past">Quick bulk upload your projects</p> 
	<div style="text-align:center; padding:10px;">
		<s:form theme="simple" action="ImportProjects" method="POST" enctype="multipart/form-data">
		<table class="table table_no_border">
			<tr>
				<td class="txtlabel alignRight">Select File to Import: </td>
				<td><s:file name="fileUpload" label="Select a File to upload" size="20" /></td>
			</tr>  
			<tr>
				<td colspan="2" align="center"><s:submit value="Import File" name="btnSubmit" id="btnSubmit" cssClass="btn btn-primary" /></td>
			</tr>
			<tr>
				<td colspan="2" align="right"><a target="_blank" href="import/ImportProjects.xlsx" title="Download Sample File">Download Sample File</a></td>
			</tr>
		</table>
		</s:form> 
	</div>
	</div>
</div>

<%-- <% UtilityFunctions uF = new UtilityFunctions(); %>
<div style="width:45%;float:right;border: solid 0px #ccc;text-align: center; margin: 9px 312px;">
<p style="color: green"><s:property value="message"/></p>
<%=uF.showData((String)session.getAttribute(IConstants.MESSAGE),"") %>
</div>


<div style="float: left;width:100%;margin-top: 10px;">
<% if(request.getAttribute("sbMessage")!=null) {
		out.println(request.getAttribute("sbMessage"));	
	}
%>
</div> --%>

 

