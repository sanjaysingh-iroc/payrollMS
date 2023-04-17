<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.*"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<script>
	$(function() {
		$("#rdate").datepicker({
			format : 'dd/mm/yyyy'
		});
	});

</script>
<%
	String closeReason = (String) request.getAttribute("closeReason");
	String reviewId = (String) request.getAttribute("reviewId");
	String appFreqId = (String) request.getAttribute("appFreqId");
	String appId = (String) request.getAttribute("appFreqId");
	String fromPage = (String) request.getAttribute("fromPage");
%>
<div id="closeReviewDiv">
<% if(closeReason != null && !closeReason.equals("")) { %>
	<table class="table table_no_border">
			<tr>
				<td colspan=2><s:fielderror />
				</td>
			</tr>
			<tr>
				<td class="txtlabel alignRight" style="width: 50px;" valign="top">Reason:</td>
				<td><%=(closeReason==null ? "" : closeReason)%></td>
			</tr>
		</table>
	<% } else { %>
	<s:form id="formID" name="frmCloseReview" theme="simple" action="CloseReview" method="POST" cssClass="formcss" enctype="multipart/form-data">

		<s:hidden name="reviewId"></s:hidden> 
		<s:hidden name="orgID"></s:hidden> 
		<s:hidden name="appFreqId"></s:hidden> 
       	<s:hidden name="wlocID"></s:hidden>
       	<s:hidden name="desigID"></s:hidden>
       	<s:hidden name="checkStatus"></s:hidden>
       	<s:hidden name="fdate"></s:hidden>
       	<s:hidden name="tdate"></s:hidden>
       	<s:hidden name="fromPage"></s:hidden>
       	<s:hidden name="operation" value="update"></s:hidden>
       
		<table class="table table_no_border">
			<tr>
				<td colspan=2><s:fielderror />
				</td>
			</tr>
			<tr>
				<td class="txtlabel alignRight" valign="top">Reason:</td>
				<td><textarea name="closeReason" id="closeReason" cols="26" rows="4"><%=(closeReason==null ? "" : closeReason)%></textarea></td>
			</tr>
			<tr>
				<td colspan="2" align="center">
					 <s:submit cssClass="btn btn-primary" value="Submit" align="center" />
				</td>
			</tr>
		</table>
	</s:form>
<% } %>	
</div>


<script>

<%if(fromPage != null) { %>

$("#formID").submit(function(event){
	event.preventDefault();
	var form_data = $("#formID").serialize();
	var from = '<%=fromPage%>';
	$.ajax({ 
		type : 'POST',
		url: "CloseReview.action",
		data: form_data+"&submit=Submit",
		success: function(result){
			if(from != null && from == "AD") {
				$("#reviewResult").html(result);
			}else {
				$("#divMyHRData").html(result);
			}
		},
		error: function(result){
			if(from != null && from == 'MyReview') {
				$.ajax({
					url: 'KRATarget.action',
					cache: true,
					success: function(result){
						$("#divMyHRData").html(result);
			   		}
				});
			} else {
				$.ajax({
					url: 'ReviewNamesList.action',
					cache: true,
					success: function(result){
						$("#reviewResult").html(result);
			   		}
				});
			}
		}
	});
});
	
<%}%>

</script>

