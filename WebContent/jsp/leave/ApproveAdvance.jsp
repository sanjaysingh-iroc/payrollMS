<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags" %>

<script type="text/javascript">

$(function () {
	$("body").on("click","#strApprove",function(){
		$(".validateRequired").prop('required',true);
    });
});


function approveAdvance(type) {
	var strAction = "ApproveAdvance.action";
	if(type == 'Approve') {
		strAction = "ApproveAdvance.action?strApprove=Approve";
	}
	
	$("#frmApproveAdvance").submit(function(e){
		e.preventDefault();
		var form_data = $("form[name='frmApproveAdvance']").serialize();
		//alert("form_data ===>> " + form_data);
	 	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 	$.ajax({
			url : ""+strAction,
			data: form_data,
			cache : false/* ,
			success : function(res) {
				$("#divResult").html(res);
			} */
		});
	 	
	 	$.ajax({
			url: 'AdvanceReport.action',
			cache: true,
			success: function(result){
				$("#divResult").html(result);
	   		}
		});
	});
	
}



</script>

<s:form id="frmApproveAdvance" name="frmApproveAdvance" theme="simple"action="ApproveAdvance" method="POST" cssClass="formcss">
	<table class="table table-bordered">
		<s:hidden name="strAdvId" />
		<tr>
			<td>Advance Amount:<sup>*</sup> </td>
			<td><s:textfield name="strAdvAmount" cssClass="validateRequired" onkeypress="return isNumberKey(event)"/></td>
		</tr>
		<tr>
			<td valign="top">Comments:</td>
			<td><s:textarea cols="50" rows="5" name="strComment" cssClass="form-control" ></s:textarea></td>
		</tr>
		<tr>
			<td colspan="2" align="center">
			<s:submit name="strApprove" id="strApprove" cssClass="btn btn-primary" value="Approve" onclick="approveAdvance('Approve');"></s:submit>
			<s:submit name="strDeny" cssClass="btn btn-danger" value="Deny" onclick="approveAdvance('Deny');"></s:submit> 
			</td>
		</tr>
	</table>
</s:form>
