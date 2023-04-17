<%@page import="com.konnect.jpms.select.FillUserStatus"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%-- <script type="text/javascript" src="scripts/charts/jquery.min.js"></script> --%>
<script type="text/javascript">
    $(function(){
    	$("#btnSubmit").click(function(){
    		$(".validateRequired").prop('required',true);
    	});
    });    
</script> 
<s:form theme="simple" name="formChangeUserName" id="formChangeUserName" action="ChangeUserName" method="POST" cssClass="formcss">
	<table class="table form-table table_no_border">
		<s:hidden name="userid" id="userid"></s:hidden>
		<s:hidden name="empid" id="empid"></s:hidden>
		<s:hidden name="fromPage" id="fromPage"></s:hidden>
		<s:hidden name="type" value="update"></s:hidden>
		<tr>
			<td class="txtlabel alignRight">User Name:<sup>*</sup></td>
			<td>
				<s:textfield name="username" id="username" cssClass="validateRequired"/> 				
			</td>
		</tr>
		<tr> 
			<td class="txtlabel alignRight">Password:<sup>*</sup></td>
			<td>
				<s:password name="newPassword" id="password" cssClass="validateRequired" showPassword="true"/><%-- <div style="float: right; margin-left: 10px;" id="pwdMeter" class="neutral">Very Weak</div><span class="hint">Enter your new password.<span class="hint-pointer">&nbsp;</span></span> --%>
			</td>
		</tr>
		<tr>
			<td colspan="2" align="center">
				<%-- <s:submit cssClass="btn btn-primary" name="submit" value="Save" id="btnSubmit" /> --%>
				<input type="button" align="right" value="Save" id="btnSubmit" class="btn btn-primary" onclick="checkApprovalStatus();"> 
			</td>
		</tr>

	</table>
	
</s:form>


<script type="text/javascript">
function checkApprovalStatus() {
	
	var xmlhttp = GetXmlHttpObject();
	if (xmlhttp == null) {
		alert("Browser does not support HTTP Request");
		return;
	} else {
		var username=document.getElementById("username").value;
		var userid=document.getElementById("userid").value;
		var empid=document.getElementById("empid").value;
		var fromPage=document.getElementById("fromPage").value;
		var xhr = $.ajax({
			url : "ChangeUserName.action?username="+username+'&userid='+userid+'&empid='+empid+"&type=ajax",
			cache : false,
			success : function(data) {
				if(data==1) {
					alert("User name already exist.");
					return false;
				} else {
					if(confirm('Are you sure, you want to change the username/password?')){
						var form_data = $("form[name='formChangeUserName']").serialize();
				     	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
				     	$.ajax({
				 			url : "ChangeUserName.action",
				 			data: form_data,
				 			cache : false/* ,
				 			success : function(res) {
				 				$("#divResult").html(res);
				 			} */
				 		});
				    
				     	var strAction = 'UserReport';
				     	if(fromPage != null && fromPage === 'people') {
				     		strAction = 'PeopleUser';
				     	}
				     	$.ajax({
							url: strAction+'.action',
							cache: true,
							success: function(result){
								$("#divResult").html(result);
					   		}
						});
					}
				}
			}
		});
	}
}


function GetXmlHttpObject() {
	if (window.XMLHttpRequest) {
		// code for IE7+, Firefox, Chrome, Opera, Safari
		return new XMLHttpRequest();
	}
	if (window.ActiveXObject) {
		// code for IE6, IE5
		return new ActiveXObject("Microsoft.XMLHTTP");
	}
	return null;
}


/* $("#formChangeUserName").submit(function(e){
	e.preventDefault();
	if(checkApprovalStatus()){
		var form_data = $("form[name='formChangeUserName']").serialize();
     	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
     	$.ajax({
 			url : "ChangeUserName.action",
 			data: form_data,
 			cache : false,
 			success : function(res) {
 				$("#divResult").html(res);
 			} 
 		});
    
     	$.ajax({
			url: 'UserReport.action',
			cache: true,
			success: function(result){
				$("#divResult").html(result);
	   		}
		});
     	
	}
}); */

</script>

