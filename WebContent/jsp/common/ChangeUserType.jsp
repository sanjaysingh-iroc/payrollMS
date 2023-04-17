<%@page import="com.konnect.jpms.select.FillUserStatus"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<script>
$(function () {
	$("body").on("click","#btnSubmit",function(){
		$('.validateRequired').filter(':hidden').prop('required',false);
		$('.validateRequired').filter(':visible').prop('required',true);
	});
	$("#strOrg").multiselect().multiselectfilter();
});

$(document).ready( function () {
	
	var val = document.getElementById("hideUserType").value;
	/* alert("val ---> " + val);  */
	showOrgAndWLoc(val);
});	

function showOrgAndWLoc(val) {
	
	if(val == 4 || val == 5 || val == 7 || val == 11 || val == 14) {
		document.getElementById("orgTR").style.display = "table-row";
		document.getElementById("wLocTR").style.display = "table-row";
		getWlocation();
	} else {
		document.getElementById("orgTR").style.display = "none";
		document.getElementById("wLocTR").style.display = "none";
	}
}

function getWlocation() {
	var org = $('#strOrg').val();
	var empid = document.getElementById("empid").value;
 	 var xmlhttp = GetXmlHttpObject();
		if (xmlhttp == null) {
			alert("Browser does not support HTTP Request");
			return;
		} else {
			var xhr = $.ajax({
				url : 'GetOrgWLocationList.action?strOrgId='+org+"&type=UTChange&empid="+empid,
				cache : false,
				success : function(data) {
					document.getElementById('idWlocId').innerHTML=data;
					$("select[multiple]").multiselect().multiselectfilter();
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

</script>

<% 	String userType = (String)request.getAttribute("userType");
	String empid = (String)request.getAttribute("empid");
%>

<s:form theme="simple" name="formChangeUserType" id="formChangeUserType" action="ChangeUserType" method="POST" cssClass="formcss">
	<table class="table form-table table_no_border">
		<s:hidden name="userid"></s:hidden>
		<s:hidden name="empid" id="empid"></s:hidden>
		<s:hidden name="type" value="update"></s:hidden> 
		<s:hidden name="fromPage" id="fromPage"></s:hidden>
		<input type="hidden" name="hideUserType" id="hideUserType" value="<%=userType %>" />
		<tr>
			<td class="txtlabel alignRight">User Type<sup>*</sup>:</td>
			<td>
				<s:select label="Select User Type" name="userType" id="userType" cssClass="validateRequired" listKey="userTypeId" 
				listValue="userTypeName" headerKey="" headerValue="Select User Type" list="userTypeList" onchange="showOrgAndWLoc(this.options[this.selectedIndex].value);"/> 				
			</td>
		</tr>
		
		<tr id="orgTR" style="display: none;">
			<td class="txtlabel alignRight">Select Organisation<sup>*</sup>:</td>
			<td>
				<s:select label="Select Organisation" name="orgId" id="strOrg" cssClass="validateRequired" listKey="orgId" listValue="orgName" 
					list="orgList" multiple="true" size="3" value="orgValue" onchange="getWlocation();"/>
			</td>
		</tr>
		
		<tr id="wLocTR" style="display: none;">
			<td class="txtlabel alignRight">Select Work Location<sup>*</sup>:</td>
			<td id="idWlocId"><b>Please select organisation.</b>
				<%-- <s:select label="Select Work Location" name="wLocation" id="wLocation" cssClass="validateRequired" listKey="wLocationId" 
				listValue="wLocationName" headerKey="" headerValue="Select Location" list="wLocationList" multiple="true" size="3"/> --%>
			</td>
		</tr>
				
		<tr>
			<td colspan="2" align="center">
				<s:submit cssClass="btn btn-primary" name="submit" value="Save" id="btnSubmit" />
				<!-- <input type="button" align="right" value="Save" class="btn btn-primary" onclick="checkApprovalStatus();" id="btnSubmit"/> --> 
			</td>
		</tr>
	</table>
	
</s:form>


<script type="text/javascript">
function checkApprovalStatus(){
	
	/* var xmlhttp = GetXmlHttpObject();
	if (xmlhttp == null) {
		alert("Browser does not support HTTP Request");
		return;
	} else {
		var username=document.getElementById("username").value;
		var xhr = $.ajax({
			url : "ChangeUserName.action?username="+ username+"&type=ajax",
			cache : false,

			success : function(data) {
				if(data==1){
					alert("User type already exist.");
					return false;
				}else{ */
					if(confirm('Are you sure you want to change user type?')){
						document.getElementById("formChangeUserType").submit();
					}
				/* }
					
			}
		}); 

	} */
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


$("#formChangeUserType").submit(function(e){
	e.preventDefault();
	var fromPage=document.getElementById("fromPage").value;
	/* if(checkApprovalStatus()){ */
		var form_data = $("form[name='formChangeUserType']").serialize();
     	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
     	$.ajax({
 			url : "ChangeUserType.action",
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
     	
	/* } */
});
</script>

