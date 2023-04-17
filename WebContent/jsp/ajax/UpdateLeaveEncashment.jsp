<%@page import="com.konnect.jpms.util.IConstants"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<script type="text/javascript">
var type = '<%=(String) request.getAttribute("type") %>';
var userTypeName = '<%=(String) request.getAttribute("currUserType") %>';
$(function () {

	$("body").on("click","#btnAddNewRowOk",function(){
		$("#formPayLoan").find('.validateRequired').filter(':hidden').prop('required',false);
		$("#formPayLoan").find('.validateRequired').filter(':visible').prop('required',true);
    });
    
});


$("#formUpdateLeaveEncashment").submit(function(e){
	if (type != '' && type === "type") {
		
	} else {
		
		e.preventDefault();
		var userType = '<%=(String) request.getAttribute("userType") %>';
		var divResult = "divResult";
		var currUserType = '<%=(String)request.getAttribute("currUserType")%>';
		var strCEO = '<%=IConstants.CEO %>';
		var strHOD = '<%=IConstants.HOD %>';
		
		if(userTypeName == strCEO || userTypeName == strHOD) {
			divResult = 'subDivResult';
		}
		
		//console.log("userType==>"+userType+"==divResult==>"+divResult);
		var form_data = $("form[name='formUpdateLeaveEncashment']").serialize();
		//alert("form_data ===>> " + form_data);
	 	$("#"+divResult).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 	  $.ajax({
			url : "UpdateLeaveEncashment.action",
			data: form_data,
			cache : false,
			success : function(res) {
				$("#"+divResult).html(res);
			},
			error : function(err) {
				$.ajax({
					url: 'LeaveEncashment.action?currUserType='+currUserType,
					cache: true,
					success: function(result){
						$("#"+divResult).html(result);
			   		}
				});
			}
		});
	 	  
	}
});

</script>
<s:form theme="simple" name="formUpdateLeaveEncashment" id="formUpdateLeaveEncashment" action="UpdateLeaveEncashment" method="POST" cssClass="formcss" >
	<s:hidden name="approveStatus"></s:hidden>
	<s:hidden name="leaveEncashId"></s:hidden>
	<s:hidden name="empId"></s:hidden>
	<s:hidden name="userType"></s:hidden>
	<s:hidden name="currUserType"></s:hidden>
	<s:hidden name="type"></s:hidden>
	<table class="table">
		
		<tr>
			<td class="txtlabel alignRight" style="width: 40%;">Paycycle:<sup>*</sup></td>
			<td>
				<s:select theme="simple" name="paycycle" listKey="paycycleId" listValue="paycycleName" list="paycycleList" key="" />
			</td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight" valign="top">Reason:</td>
			<td><textarea name="mReason" id="mReason" cols="26" rows="4"></textarea></td>
		</tr>
		
		<tr>
			<td></td>
			<td>
				<s:submit cssClass="btn btn-primary" value="Approve" name="btnAddNewRowOk" id="btnAddNewRowOk" /> 
			</td>
		</tr>

	</table>
</s:form>

