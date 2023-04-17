<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@taglib uri="/struts-tags" prefix="s"%>

<%-- <script>
   addLoadEvent(prepareInputsForHints);    
</script> --%>

 

<script language="javascript">
    /* $(document).ready(function(){
 
        $('#newPassword').pwdMeter({
            minLength: 6,
            displayGeneratePassword: false,
            generatePassText: 'Password Generator',
            generatePassClass: 'GeneratePasswordLink',
            randomPassLength: 13
        });
 
    }); */
    
    jQuery(document).ready(function(){
        // binds form submission and fields to the validation engine
        jQuery("#formID").validationEngine();
    });
    
    function checkPasswordDetails() {
    	var hideOldPassword = document.getElementById('hideOldPassword').value;
    	var oldPassword = document.getElementById('oldPassword').value;
    	var newPassword = document.getElementById('newPassword').value;
    	var confirmPassword = document.getElementById('confirmPassword').value;
    	
    	if(newPassword == '' || confirmPassword == '') {
    		alert('Please enter the new password.');
    	} else if(hideOldPassword != oldPassword && newPassword != confirmPassword) {
    		alert('Old password is incurrect and new password does not match, please confrim the new password.');
    	} else if(hideOldPassword != oldPassword) {
    		alert('Old password is incurrect.');
    	} else if(newPassword != confirmPassword) {
    		alert('New password does not match, please confrim the new password.');
    	} else {
    		saveTrackerPassword(newPassword);
    	}
    }
    
</script>

 
<script language="javascript" src="<%= request.getContextPath()%>/scripts/passwordstrength/jquery.pwdMeter.js"></script>
<script language="javascript" src="<%= request.getContextPath()%>/scripts/passwordstrength/jquery.colorbox-min.js"></script>

<link rel="stylesheet" type="text/css" media="screen" href="<%= request.getContextPath()%>/css/passwordstrength/style.css">


<%-- 
<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Change Password" name="title"/>
</jsp:include>  --%>

<div style="float: left; width: 100%;">
        
	<s:form theme="simple" name="formID" id="formID" action="ChangeTrackerPassword" method="POST" cssClass="formcss" cssStyle="float:left;">
		<table border="0" cellspacing="2" cellpadding="2">
			<tr><td colspan=2><s:fielderror/></td></tr>
			<tr>
				<td class="txtlabel alignRight">Old Password:<sup>*</sup></td>
				<td>
				<s:hidden name="hideOldPassword" id="hideOldPassword"/>
				<s:textfield name="oldPassword" id="oldPassword" readonly="true"/><span class="hint">This is your current password.<span class="hint-pointer t55">&nbsp;</span></span></td>
			</tr>
			
			<tr>
				<td class="txtlabel alignRight">New Password:<sup>*</sup></td>
				<td><s:textfield name="newPassword" id="newPassword"/>
					<!-- <div style="float: right; margin-left: 10px;" id="pwdMeter" class="neutral">Very Weak</div> -->
					<span class="hint">Enter your new password.<span class="hint-pointer">&nbsp;</span></span>
				</td>
			</tr>
			
			<tr>
				<td valign="top" class="txtlabel alignRight">Confirm Password:<sup>*</sup></td>
				<td><s:textfield name="confirmPassword" id="confirmPassword"/><span class="hint">Please confirm the password by entering it once again.<span class="hint-pointer">&nbsp;</span></span></td>
			</tr>
				
			<tr><td colspan="2" align="center">
				<input type="button" value="Change Password" class="input_button" style="margin-left:50px" onclick="checkPasswordDetails()"/>
				<%-- <s:submit  cssClass="input_button" value="Change Password" align="center" /> --%>
				</td>
			</tr>
		</table>
	</s:form> 

    </div>
