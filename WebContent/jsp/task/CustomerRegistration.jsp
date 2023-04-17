<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@ taglib uri="http://htmlcompressor.googlecode.com/taglib/compressor" prefix="compress" %>

<%@page import="com.konnect.jpms.util.*" %>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
	
<%@ taglib uri="http://granule.com/tags" prefix="g" %>
<%@ taglib prefix="s" uri="/struts-tags" %> 
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>


<g:compress>
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath()%>/css/displaystyle.css" />
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath()%>/css/stylesheet.css" />
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath()%>/css/jquery-ui.css" />
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath()%>/css/tooltip.css" />
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath()%>/css/style_IE_nav.css" />
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath()%>/css/demo_table_jui.css" />
<link rel="stylesheet" type="text/css" media="screen" href="<%= request.getContextPath()%>/css/style1.css">
<link rel='stylesheet' type='text/css' href='<%= request.getContextPath()%>/css/tabs.css' />
<link rel='stylesheet' type='text/css' href='<%= request.getContextPath()%>/scripts/fullcalendar/fullcalendar.css' />
<link rel='stylesheet' type='text/css' href='<%= request.getContextPath()%>/scripts/fullcalendar/fullcalendar.print.css' media='print' />
<link type="text/css" rel="stylesheet" href="<%= request.getContextPath()%>/css/jquery.modaldialog.css" />
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath()%>/css/TableTools.css" />
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath()%>/css/TableTools_JUI.css" />
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath()%>/css/pro_dropline_ie.css" />
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath()%>/css/pro_dropline.css" />
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath()%>/css/style_IE_N.css" />
<link type="text/css" rel="stylesheet" href="<%= request.getContextPath()%>/css/newsticker/ticker-style.css" />
<link type="text/css" rel="stylesheet" href="<%= request.getContextPath()%>/css/highslide/highslide.css" />
<link type="text/css" rel="stylesheet" href="<%= request.getContextPath()%>/css/validationEngine.jquery.css" />
</g:compress> 

<link rel="shortcut icon" href="images1/icons/icons/w_green.png" type="image/x-icon">
<link rel="icon" href="images1/icons/icons/w_green.png" type="image/x-icon">
 
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/jquery-ui-1.8.6.custom.min.js"> </script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/jquery-ui.min.js"> </script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/jquery.min.js"> </script>
 

<g:compress>

<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/jquery.ui.datepicker.js"> </script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/jquery.ui.widget.js"> </script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/jquery.ui.core.js"> </script>

<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/tooltip.js"> </script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/custom.js"> </script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/main.js"> </script>
<script type="text/javascript" src="<%= request.getContextPath()%>/js/jquery.PrintArea.js_4.js"> </script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/datatable/complete.js"> </script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/datatable/jquery-1.4.4.min.js"> </script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/datatable/jquery.dataTables.min.js"> </script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/datatable/jquery.jeditable.js"> </script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/datatable/jquery-ui.js"> </script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/datatable/jquery.validate.js"> </script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/datatable/jquery.dataTables.editable.js"> </script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/charts/highstock.js"> </script> 

<script type="text/javascript" src="<%= request.getContextPath()%>/js/jquery.tools.min.js"> </script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/fullcalendar/fullcalendar.min.js"> </script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/fullcalendar/timepicker.js"> </script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/jquery.modaldialog.js"> </script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/customAjax.js"> </script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/TableTools.js"> </script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/TableTools.min.js"> </script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/_rating/js/jquery.raty.js"> </script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/highslide/highslide-with-html.js"> </script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/jquery.lazyload.js"> </script>
</g:compress>

<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/charts/highcharts1.js"> </script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/charts/highcharts-more.js"> </script>

<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/newsticker/jquery.ticker.js"> </script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/jquery.validationEngine-en.js"> </script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/jquery.validationEngine.js"> </script> 
 
<script>

jQuery(document).ready(function() {
	// binds form submission and fields to the validation engine
	jQuery("#formCustomerRegistration").validationEngine();
});	
	
 	function readImageURL(input, targetDiv) {
	    if (input.files && input.files[0]) {
	        var reader = new FileReader();
	        reader.onload = function (e) {
	            $('#'+targetDiv)
	                .attr('src', e.target.result)
	                .width(60)
	                .height(60);
	        };
	        reader.readAsDataURL(input.files[0]);
	    }
	}
 
 	function checkPassword() {
 		var strUsername = document.getElementById("strUsername").value;
 		var strPassword = document.getElementById("strPassword").value;
 		var strConfirmPassword = document.getElementById("strConfirmPassword").value;
 		
 		if(strUsername == "" || strPassword == "") {
 			alert("Please enter the username and password.");
 		} else {
	 		if(strPassword != strConfirmPassword) {
	 			alert("Password does not match, please confrim the password.");
	 		} else {
	 			document.getElementById("formCustomerRegistration").submit();
	 		}
 		}
 	}
</script>

<div class="pagetitle" style="margin-top: 15px; margin-left: 20px;">Customer Registration Form </div>

	<div class="leftbox reportWidth" >
		<% 
			String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
			String strImage = (String) request.getAttribute("strImage");
			Map<String, List<String>> hmValidationFields = (Map<String, List<String>> ) request.getAttribute("hmValidationFields");
			UtilityFunctions uF = new UtilityFunctions();
			String validReqOpt = "";
			String validAsterix = "";
			String strClientId = (String)request.getAttribute("strClientId");
		%>

		<s:form theme="simple" action="CustomerRegistration" method="POST" cssClass="formcss" id="formCustomerRegistration" enctype="multipart/form-data">
			<s:hidden name="strClientContactId" />
			<s:hidden name="clientId" />
			<s:hidden name="operation" value="U"/>
			<div id="property_type">
			
			<table border="0" class="" style="width:auto">
			<tr>
			<% 	List<String> fNameValidList = hmValidationFields.get("COMPANY_CONTACT_FIRST_NAME"); 
				validReqOpt = "";
				validAsterix = "";
				if(uF.parseToBoolean(fNameValidList.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
				}
			%>
				<td class="txtlabel alignRight" valign="top"><label> First Name:<%=validAsterix %> </label><br/></td>
				<td colspan="2"><input type="text" class="<%=validReqOpt %>" name="strClientContactFName" id="strClientContactFName" style="height: 25px; width: 205px;" value="<%=uF.showData((String)request.getAttribute("strClientContactFName"), "") %>"/>
				</td>
			</tr>
				
			<tr>
			<% 	List<String> mNameValidList = hmValidationFields.get("COMPANY_CONTACT_MIDDLE_NAME"); 
				validReqOpt = "";
				validAsterix = "";
				if(uF.parseToBoolean(mNameValidList.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
				}
			%>
				<td class="txtlabel alignRight"><label>Middle Name:<%=validAsterix %> </label><br/></td>
				<td><input type="text" class="<%=validReqOpt %>" name="strClientContactMName" id="strClientContactMName" style="height: 25px; width: 205px;" value="<%=uF.showData((String)request.getAttribute("strClientContactMName"), "") %>"/></td>
			</tr>
			
			<tr>
			<% 	List<String> lNameValidList = hmValidationFields.get("COMPANY_CONTACT_LAST_NAME"); 
				validReqOpt = "";
				validAsterix = "";
				if(uF.parseToBoolean(lNameValidList.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
				}
			%>
				<td class="txtlabel alignRight"><label>Last Name:<%=validAsterix %> </label><br/></td>
				<td><input type="text" class="<%=validReqOpt %>" name="strClientContactLName" id="strClientContactLName" style="height: 25px; width: 205px;" value="<%=uF.showData((String)request.getAttribute("strClientContactLName"), "") %>"/></td>
			</tr>
			
			<tr>
			<% 	List<String> contactNoValidList = hmValidationFields.get("COMPANY_CONTACT_CONTACT_NO"); 
				validReqOpt = "";
				validAsterix = "";
				if(uF.parseToBoolean(contactNoValidList.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
				}
			%>
				<td class="txtlabel alignRight"><label>Contact No:<%=validAsterix %> </label><br/></td>
				<td><input type="text" class="<%=validReqOpt %>" name="strClientContactNo" id="strClientContactNo" style="height: 25px; width: 205px;" value="<%=uF.showData((String)request.getAttribute("strClientContactNo"), "") %>"/></td>
			</tr>
			
			<tr>
			<% 	List<String> emailIdValidList = hmValidationFields.get("COMPANY_CONTACT_MAIL_ID"); 
				validReqOpt = "";
				validAsterix = "";
				if(uF.parseToBoolean(emailIdValidList.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
				}
			%>
				<td class="txtlabel alignRight"><label>Email Id:<%=validAsterix %> </label><br/></td>
				<td><input type="text" class="<%=validReqOpt %>" name="strClientContactEmail" id="strClientContactEmail" style="height: 25px; width: 205px;" value="<%=uF.showData((String)request.getAttribute("strClientContactEmail"), "") %>"/></td>
			</tr>
			
			<tr>
			<% 	List<String> desigValidList = hmValidationFields.get("COMPANY_CONTACT_DESIGNATION"); 
				validReqOpt = "";
				validAsterix = "";
				if(uF.parseToBoolean(emailIdValidList.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
				}
			%>
				<td class="txtlabel alignRight"><label>Designation:<%=validAsterix %> </label><br/></td>
				<td>
				<% if(validReqOpt != null && !validReqOpt.equals("")) { %>
					<s:select id="strClientContactDesig" name="strClientContactDesig" listKey="desigId" listValue="desigCodeName" 
						cssClass="validateRequired" headerKey="" headerValue="Select Designation" list="desigList" />
				<% } else { %>
					<s:select id="strClientContactDesig" name="strClientContactDesig" listKey="desigId" listValue="desigCodeName" 
						headerKey="" headerValue="Select Designation" list="desigList" />
				<% } %>
				</td>
			</tr>
			
			<tr>
			<% 	List<String> departmentValidList = hmValidationFields.get("COMPANY_CONTACT_DEPARTMENT"); 
				validReqOpt = "";
				validAsterix = "";
				if(uF.parseToBoolean(emailIdValidList.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
				}
			%>
				<td class="txtlabel alignRight"><label>Department:<%=validAsterix %> </label><br/></td>
				<td>
				<% if(validReqOpt != null && !validReqOpt.equals("")) { %>
					<s:select id="strClientContactDepartment" name="strClientContactDepartment" listKey="deptId" listValue="deptName" 
						cssClass="validateRequired" headerKey="" headerValue="Select Department" list="departmentList" />
				<% } else { %>
					<s:select id="strClientContactDepartment" name="strClientContactDepartment" listKey="deptId" listValue="deptName" 
						headerKey="" headerValue="Select Department" list="departmentList" />
				<% } %>		
				</td>
			</tr>
			
			<tr>
			<% 	List<String> locationValidList = hmValidationFields.get("COMPANY_CONTACT_LOCATION"); 
				validReqOpt = "";
				validAsterix = "";
				if(uF.parseToBoolean(emailIdValidList.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
				}
			%>
				<td class="txtlabel alignRight"><label>Location:<%=validAsterix %> </label><br/></td>
				<td>
				<% if(validReqOpt != null && !validReqOpt.equals("")) { %>
					<s:select id="strClientContactLocation" name="strClientContactLocation" listKey="wLocationId" listValue="wLocationName" 
						cssClass="validateRequired" headerKey="" headerValue="Select Location" list="workLocationList" />
				<% } else { %>
					<s:select id="strClientContactLocation" name="strClientContactLocation" listKey="wLocationId" listValue="wLocationName" 
						headerKey="" headerValue="Select Location" list="workLocationList" />
				<% } %>
				</td>
			</tr>
			
			<tr>
				<td class="txtlabel alignRight"><label>Photo:<%=validAsterix %> </label><br/></td>
				<td>
					<img height="60" width="60" class="lazy" id="strClientContactPhotoImg0" style="border: 1px solid #CCCCCC; border: 1px lightgray solid;" src="<%=(String)request.getAttribute("strClientContactPhotoFile") %>" data-original="<%=(String)request.getAttribute("strClientContactPhotoFile") %>" />
					<input type="file" name="strClientContactPhoto" id="strClientContactPhoto0" onchange="readImageURL(this, 'strClientContactPhotoImg0');"/>
				</td>
			</tr>
			
			<tr>
				<td colspan="2">
					<div style="font-weight: bold; border-bottom: 1px solid gray; padding-bottom: 3px; margin-left: 50px; font-size: 13px;">Login Details</div>
				</td>
			</tr>
			
			<tr>
				<td class="txtlabel alignRight">Username:<sup>*</sup></td>
				<td>
					<input type="text" class="validateRequired" name="strUsername" id="strUsername" style="height: 25px; width: 205px;" value="<%=uF.showData((String)request.getAttribute("strUsername"), "") %>" readonly="readonly"/>
				</td>
			</tr>
			
			<tr>
				<td class="txtlabel alignRight">Enter New Password:<sup>*</sup></td>
				<td><input type="password" class="validateRequired" name="strPassword" id="strPassword" style="height: 25px; width: 205px;"/></td>
			</tr>
			
			<tr>
				<td class="txtlabel alignRight"><label>Confirm Password:<sup>*</sup></label><br/></td>
				<td><input type="password" class="validateRequired" name="strConfirmPassword" id="strConfirmPassword" style="height: 25px; width: 205px;" /></td> <!-- onblur="checkPassword()" onchange="checkPassword()" onmouseout="checkPassword()" -->
			</tr>
		</table>
		</div>
		<div class="clr"></div>
		<div style="margin:0px 0px 0px 210px">
		<table>
		<tr><td colspan="2" align="center">
			<input type="button" class="input_button" value="Update & Go to Login" id="btnAddNewRowOk" onclick="checkPassword()">
		 	<%-- <s:submit cssClass="input_button" value="Update & Go to Login" id="btnAddNewRowOk"/> --%>
		</td></tr>				
		 </table>
		 </div>         
		</s:form>

	</div>
<script>
//$("img.lazy").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10});
$("img.lazy").lazyload({event : "sporty", threshold : 200,effect : "fadeIn",failure_limit : 10});

$(window).bind("load", function() {
    var timeout = setTimeout(function() { $("img.lazy").trigger("sporty") }, 1000);
});  
</script>  
