<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>

<script>
      jQuery(document).ready(function(){
          // binds form submission and fields to the validation engine
    	  $("#emailEmployee").click(function(){
    			$(".validateRequired").prop('required',true);
    			$("#signUpForm_email").prop('type','email');
    		});
      });
      
     $("#signUpForm").submit(function(e){
   		e.preventDefault();
   		var form_data = $("form[name='signUpForm']").serialize();
   	   	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
   	   	$.ajax({
   			url : "AddPeopleMode.action",
   			data: form_data,
   			cache : false,
   			success : function(res) {
   				$("#divResult").html(res);
   			},
   			error : function(res) {
   				$.ajax({
   		   			url : "Peoples.action",
   		   			data: form_data,
   		   			cache : false,
   		   			success : function(res) {
   		   				$("#divResult").html(res);
   		   			}
   				});
   			}
   		});
   	});
      
     
     function statusDisable() {
    	 alert("Your email is not configured, please speak to your admin.");
     }
     
</script>

<% boolean statusFlag = (Boolean) request.getAttribute("statusFlag"); %>

<div style="width:100%;float:left;border: solid 1px #ccc; ">
   <div style=" padding:10px;">
       <s:form theme="simple" method="post" name="signUpForm" id="signUpForm" cssClass="formcss" enctype="multipart/form-data">
           <s:token name="token"></s:token>
           <s:hidden name="notification" value="signup" />
           <table class="table form-table table_no_border">
	            <tr><td class="txtlabel alignRight">Resource First Name:<sup>*</sup></td> <td><s:textfield name="fname" cssClass="validateRequired"></s:textfield></td></tr>
	            <tr><td class="txtlabel alignRight">Resource Last Name:<sup>*</sup></td> <td><s:textfield name="lname" cssClass="validateRequired"></s:textfield></td></tr>
	            <tr><td class="txtlabel alignRight">Resource Email Id:<sup>*</sup></td> <td><s:textfield name="email" cssClass="validateRequired"></s:textfield></td></tr>
	            <tr><td class="txtlabel alignRight">Choose:<sup>*</sup></td> <td><s:radio name="strEmpORContractor" list="#{'1':'Employee','2':'Contractor'}" value="1"></s:radio></td></tr>
	            <tr><td colspan="2" align="center">
		            <% if(statusFlag) { %>
		            	<s:submit cssClass="btn btn-primary" name="emailEmployee" id="emailEmployee" value="Let Resource Enter the Info"></s:submit>
		            <% } else { %>
		            	<input type="button" class="btn btn-primary" name="emailEmployee" id="emailEmployee" value="Let Resource Enter the Info" onclick="statusDisable();">
		            <% } %>
		            </td>
		        </tr>
           </table>
       </s:form>
   </div>
</div>
 

