<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.*"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>


<script>
	$(function() {
		$("#rdate").datepicker({
			dateFormat : 'dd/mm/yy'
		});
		// $( "#leaveToDate" ).datepicker({dateFormat: 'dd/mm/yy'});
	});

	
</script>
<%
	String strStatus = (String) request.getAttribute("strStatus");
	String strId = (String) request.getAttribute("strId");
	String denyReason = (String) request.getAttribute("denyReason");
	String view = (String) request.getAttribute("view");
	String frmPage = (String) request.getAttribute("frmPage");
	String currUserType = (String) request.getAttribute("currUserType");
	String strBaseUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
%>
<div id="denyRequest">
	<s:form id="formID" name="frmRequirementRequest" theme="simple" action="DenyRequest" method="POST" cssClass="formcss" enctype="multipart/form-data">
		<s:hidden name="orgID" id="orgID" />
        <s:hidden name="wlocID" id="wlocID" />
        <s:hidden name="desigID" id="desigID" />
        <s:hidden name="checkStatus" id="checkStatus" />
        <s:hidden name="fdate" id="fdate" />
        <s:hidden name="tdate" id="tdate" />
        <s:hidden name="frmPage" id="frmPage" />
        <s:hidden name="currUserType" id="currUserType" />
       <s:hidden name="userType" id="userType" />
       <s:hidden name="type" id="type" />
        <input type="hidden" name="strBaseUserType" id=strBaseUserType value="<%=strBaseUserType %>"/>
		<table border="0" class="formcss" style="float: left">
			<tr>
				<td colspan=2><s:fielderror />
				</td>
			</tr>
			<tr>
				<td><s:hidden name="requestDeny" value="RequestDeny" /> 
				<input type="hidden" name="ST" value="<%=strStatus%>" /> 
				<input type="hidden" name="RID" value="<%=strId%>" /></td>
			</tr>

			<tr>
				<td height="10px">&nbsp;</td>
			</tr>
			<tr>
				<td class="txtlabel alignRight" valign="top">Reason:<sup>*</sup></td>
				<td><textarea name="req_deny_reason" id="req_deny_reason"
						cols="26" rows="4" class="validateRequired"><%=(denyReason==null ? "" : denyReason)%></textarea></td>
			</tr>
			<tr>
				<td>&nbsp;</td>
				<td>
					<%if (view == null || !view.equals("view")) {%> 
						<s:submit cssClass="btn btn-primary" value="Submit" align="center" />
					<%}%>
				</td>
			</tr>
		</table>
	</s:form>
</div>

<script>

$("input[type='submit']").click(function(){
	$("#formID").find('.validateRequired').filter(':hidden').prop('required',false);
	$("#formID").find('.validateRequired').filter(':visible').prop('required',true);
});

$("#formID").submit(function(event){
	event.preventDefault();
	var from = '<%=frmPage%>';
	var form_data = $("#formID").serialize();
	if(from != null && (from == "RAD")) {
		var divResult = 'divResult';
		var strBaseUserType = document.getElementById("strBaseUserType").value;
		var strCEO = '<%=IConstants.CEO %>';
		var strHOD = '<%=IConstants.HOD %>';
		
		if(strBaseUserType == strCEO || strBaseUserType == strHOD) {
			divResult = 'subDivResult';
		}
		var orgID = document.getElementById("orgID").value;
		var wlocID = document.getElementById("wlocID").value;
		var desigID = document.getElementById("desigID").value;
		var checkStatus = document.getElementById("checkStatus").value;
		var fdate = document.getElementById("fdate").value;
		var tdate = document.getElementById("tdate").value;
		var currUserType = document.getElementById("currUserType").value;
		$.ajax({
			type :'POST',
			url  :'DenyRequest.action',
			data :form_data,
			cache:true/* ,
			success : function(result) { 
				$("#"+divResult).html(result);
			} */
		});
		
		$.ajax({
			url: 'RequirementApproval.action?f_org='+orgID+'&location1='+wlocID+'&designation='+desigID+'&checkStatus='+checkStatus
				+'&fdate='+fdate+'&tdate='+tdate+'&currUserType='+currUserType,
			cache: true,
			success: function(result){
				$("#"+divResult).html(result);
	   		}
		});
	}
});

	
</script>

