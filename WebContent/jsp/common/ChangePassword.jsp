<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<style>
    .changePassword ul{
    padding-left: 0px;
    }
</style>

<link rel="stylesheet" type="text/css" media="screen" href="<%= request.getContextPath()%>/css/passwordstrength/style.css">
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script language="javascript" src="<%= request.getContextPath()%>/scripts/passwordstrength/jquery.pwdMeter.js"></script>
<script language="javascript" src="<%= request.getContextPath()%>/scripts/passwordstrength/jquery.colorbox-min.js"></script>

<script type="text/javascript">
	$(function(){
		$("body").on("click","#submitButton",function(){
	    	$('.validateRequired').filter(':hidden').prop('required', false);
			$('.validateRequired').filter(':visible').prop('required', true);
	    });
	});
</script>

          <div class="box-body" style="padding: 5px; overflow-y: auto;">
                <%
                   String strMessage = (String) request.getAttribute("MESSAGE");
                   if (strMessage == null) {
                   	strMessage = "";
                   }
                %>
                <p class="message" style="width:50%"><%=strMessage%></p>
                <s:form theme="simple" name="frmChangePassword" id="frmChangePassword" action="ChangePassword" method="POST" cssClass="formcss" cssStyle="float:left;">
                    <s:hidden name="noticeId" />
                    <table border="0" cellspacing="2" cellpadding="2" class="table changePassword table_no_border">
                    		<tr><td colspan=2><s:fielderror><span id="errorMessage"></span></s:fielderror></td></tr>
                    
                        <tr>
                            <td class="txtlabel alignRight">Old Password:<sup>*</sup></td>
                            <td>
                                <s:password name="oldPassword" id="oldPassword" cssClass="validateRequired"/>
                                <p class="hint">Enter your current password.<span class="hint-pointer t55">&nbsp;</span></p>
                            </td>
                        </tr>
                        <tr>
                            <td class="txtlabel alignRight">New Password:<sup>*</sup></td>
                            <td>
                                <s:password name="newPassword" id="newPassword" cssClass="validateRequired"/>
                                <div style="float: right; margin-left: 10px;" id="pwdMeter" class="neutral">Very Weak</div>
                                <p class="hint">Enter your new password.<span class="hint-pointer">&nbsp;</span></p>
                            </td>
                        </tr>
                        <tr>
                            <td valign="top" class="txtlabel alignRight">Confirm Password:<sup>*</sup></td>
                            <td>
                                <s:password name="confirmPassword" id="confirmPassword" cssClass="validateRequired"/>
                                <p class="hint">Please confirm the password by entering it once again.<span class="hint-pointer">&nbsp;</span></p>
                            </td>
                        </tr>
                        <tr>
                            <td></td>
                            <td>
                                <s:submit cssClass="btn btn-primary" id="submitButton" value="Change Password" align="center" />
                            </td>
                        </tr>
                    </table>
                </s:form>
          </div>
          <!-- /.box-body -->


	<script language="javascript">
    	$('#newPassword').pwdMeter({
            minLength: 6,
            displayGeneratePassword: false,
            generatePassText: 'Password Generator',
            generatePassClass: 'GeneratePasswordLink',
            randomPassLength: 13
        });
    	
    	
    	function checkPasswordData(){
    		var oldPassword = document.getElementById("oldPassword").value;
    		var newPassword = document.getElementById("newPassword").value;
    		var confirmPassword = document.getElementById("confirmPassword").value;
			var errorMessage = "";
    		if (oldPassword!=null && oldPassword.length == 0) {
    			errorMessage = "Please enter existing password.<br/>";
    	    }
    	    if (newPassword!=null && newPassword.length == 0) {
    	    	errorMessage = errorMessage + "Please enter new password.<br/>";
    	    }
   	        if (confirmPassword!=null && confirmPassword.length == 0) {
   	        	errorMessage = errorMessage + "Please enter new password again to confirm.<br/>";
   	        }
   	        if (confirmPassword!=null && newPassword!=null && confirmPassword != newPassword) {
   	        	errorMessage = errorMessage + "Password does not match, please confrim the password.";
   	        }
   	     
    		if(errorMessage != "") {
    			document.getElementById("errorMessage").innerHTML = errorMessage;
    			return false;
    		} else {
    			document.getElementById("errorMessage").innerHTML = "";
    			return true;
    		}
    	}


    	/* $("#frmChangePassword").submit(function(e){
    		e.preventDefault();
    		if(checkPasswordData()){
    			var form_data = $("form[name='frmChangePassword']").serialize();
    	     	$.ajax({
    	 			url : "ChangePassword.action",
    	 			data: form_data,
    	 			cache : false,
    	 			success : function(res) {
    	 				$("#modalInfoCP").hide();
    	 			}, 
    	 			error: function(res) {
    	 				$("#modalInfoCP").hide();
    	 			}
    	 		});
    		}
    	}); */
    	
	</script>