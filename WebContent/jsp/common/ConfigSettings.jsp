<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@taglib uri="/struts-tags" prefix="s"%>

<style>
	input[type=number]::-webkit-inner-spin-button, 
	input[type=number]::-webkit-outer-spin-button { 
	    -webkit-appearance: none;
	    -moz-appearance: none;
	    appearance: none; 
	    margin: 0; 
	}
</style>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<%-- <script type="text/javascript" src="scripts/jquery-ui.min.js"> </script>
<link rel="stylesheet" href="js_bootstrap/datepicker/bootstrap-datepicker3.css">
<script type="text/javascript" src="js_bootstrap/datepicker/bootstrap-datepicker.min.js"></script>  --%>
<script>

$(document).ready(function() {
	$("#btnSaveSettings").click(function(){
		/* $(".validateRequired").prop('required',true); */
		$("#formID").find('.validateRequired').filter(':hidden').prop('required',false);
		$("#formID").find('.validateRequired').filter(':visible').prop('required',true);
		$(".validateNumber").prop('type','number');$(".validateNumber").prop('step','any');
		
		$("#email").prop('type','email');
	});
    $('a.poplight[href^=#]').click(function() {
        var popID = $(this).attr('rel'); //Get Popup Name
        var popURL = $(this).attr('href'); //Get Popup href to define size

        //Pull Query & Variables from href URL 
        var query= popURL.split('?'); 
        var dim= query[1].split('&'); 
        var popWidth = dim[0].split('=')[1]; //Gets the first query string value
  
        //Fade in the Popup and add close button
        $('#' + popID).fadeIn().css({ 'width': Number( popWidth ) }).prepend('<a href="#" class="close"><img src="<%=request.getContextPath()%>/images/close_pop.png" class="btn_close" title="Close Window" alt="Close" /></a>');

        //Define margin for center alignment (vertical   horizontal) - we add 80px to the height/width to accomodate for the padding  and border width defined in the css
        var popMargTop = ($('#' + popID).height() + 80) / 2;
        var popMargLeft = ($('#' + popID).width() + 80) / 2;

        //Apply Margin to Popup
        $('#' + popID).css({
            'margin-top' : -popMargTop,
            'margin-left' : -popMargLeft
        });

        //Fade in Background
        $('body').append('<div id="fade"></div>'); //Add the fade layer to bottom of the body tag.
        $('#fade').css({'filter' : 'alpha(opacity=80)'}).fadeIn(); //Fade in the fade layer - .css({'filter' : 'alpha(opacity=80)'}) is used to fix the IE Bug on fading transparencies

        return false;
    });

    //Close Popups and Fade Layer
    //$('a.close, #fade').live('click', function() { //When clicking on the close or fade layer...
        //$('#fade , .popup_block').fadeOut(function() {
            //$('#fade, a.close').remove();  //fade them both out
        //});
        //return false;
    //});

});

$(function() {
    $( "#idFinancialYearStart" ).datepicker({format: 'dd/mm/yyyy'});
    $( "#idFinancialYearEnd" ).datepicker({format: 'dd/mm/yyyy'});
    $( "#idPaycycleStart" ).datepicker({format: 'dd/mm/yyyy'});
});
	
	
	function showEmailFields(){
		var obj = document.getElementById('isEmail');
		if(obj.checked){
			document.getElementById('email_host').style.display = 'table-row';	
			document.getElementById('email_from').style.display = 'table-row';
			document.getElementById('email_password').style.display = 'table-row';
			
			document.getElementById('trRequiredAuthentication').style.display = 'table-row';
		}else{
			document.getElementById('email_host').style.display = 'none';	
			document.getElementById('email_from').style.display = 'none';
			document.getElementById('email_password').style.display = 'none';
			
			document.getElementById('trRequiredAuthentication').style.display = 'none';
		}
		showEmailRequiredAuthentication();
	}
	
	function showEmailRequiredAuthentication(){
		var obj = document.getElementById('isRequiredAuthentication');
		var obj1 = document.getElementById('isEmail');
		if(obj.checked && obj1.checked){
			document.getElementById('trEmailUsername').style.display = 'table-row';
			document.getElementById('trEmailPassword').style.display = 'table-row';
		}else{
			document.getElementById('trEmailUsername').style.display = 'none';
			document.getElementById('trEmailPassword').style.display = 'none';
		}
	}	
	
	function showCodeFields(){
		var obj = document.getElementById('isEmpCode');
		if(obj.checked){
			document.getElementById('empCode1').style.display = 'table-row';	
			document.getElementById('empCode2').style.display = 'table-row';
			$("#formID_strEmpCodeAlpha").prop('required',true);
			$("#formID_strEmpCodeNumber").prop('required',true);
		}else{
			document.getElementById('empCode1').style.display = 'none';	
			document.getElementById('empCode2').style.display = 'none';
			$("#formID_strEmpCodeAlpha").prop('required',false);
			$("#formID_strEmpCodeNumber").prop('required',false);
		}
	}
	
	function showTextFields(){
		var obj = document.getElementById('isText');
		if(obj.checked){
			document.getElementById('text_from').style.display = 'table-row';
			$("#formID_strTextFrom").prop('required',true);
		}else{
			document.getElementById('text_from').style.display = 'none';
			$("#formID_strTextFrom").prop('required',false);
			$("#formID_strTextFrom").removeClass("validateRequired");
		}
	}	
	
	function checkImageSize(){
		 if (window.File && window.FileReader && window.FileList && window.Blob){
	        var fsize = $('#empImage')[0].files[0].size;
	        var ftype = $('#empImage')[0].files[0].type;
	        var fname = $('#empImage')[0].files[0].name;
	        var flag = true;
	        switch(ftype){ 
	            case 'image/png':
	            case 'image/gif':
	            case 'image/jpeg':
	            case 'image/pjpeg':
	                if(fsize>500000){ //do something if file size more than 1 mb (1048576)
	                    alert("You are trying to upload a larger file than 500kb.");
	                    flag = false;
	                }else{
	                    //alert(fsize +" bites\nYou are good to go!");
	                    flag = true;
	                }
	                break;
	            default:
	                alert('Unsupported File!');
	            	flag = false;
	        }
	        if(flag){
	        	return true;
	        } else {
	        	return false;
	        }
	        
	    }else{
	        alert("Please upgrade your browser, because your current browser lacks some new features we need!");
	        return false;
	    }
	 }
	
	
	function editCompanyLogo(type) {
		$("#myModal").modal('show');
		document.getElementById("imageType").value = type;
	}
	
	
</script>


<%-- <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Company Settings" name="title"/>
</jsp:include> --%>

<%
	UtilityFunctions uF = new UtilityFunctions();
	String strImage = (String) request.getAttribute("COMPANY_LOGO");
	String strImageSmall = (String) request.getAttribute("COMPANY_LOGO_SMALL");
	CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
%>


<div class="box-body table-responsive no-padding">

    <div style="float: left; width: 100%;"><div class="callout callout-warning" style="margin-bottom: 0px; font-weight: 600;"> If you are not sure of the setting please do not change them as they can affect the system.</div>
    	<p style="font-size: 12px; font-style: italic; float:right">Last updated by <%=uF.showData((String)request.getAttribute("UPDATED_NAME"), "N/A") %> on <%=uF.showData((String)request.getAttribute("UPDATED_DATE"), "N/A") %></p>    
 	</div>


	<%=uF.showData((String) session.getAttribute("MESSAGE"), "")%>
	<% session.setAttribute("MESSAGE", ""); %>
		<div style="float:right;width:100%;color:#999999;line-height:35px"><span style="float:right;margin-right: 60px;"><strong>Edit Company Logos</strong></span></div>
		<div style="float: right; margin-right: 15px;">
			<div style="float:right; margin-left: 10px;">
				<a href="javascript:void(0);" class="fa fa-edit" title="Edit Company Logo"  onclick="editCompanyLogo('COMPANY_LOGO');">&nbsp;</a>
			</div>
			<div style="float:right;">
				<img style="height:60px" class="lazy" src="userImages/company_avatar_photo.png" data-original="<%=CF.getStrDocRetriveLocation()+IConstants.I_COMPANY+"/"+IConstants.I_IMAGE+"/"+strImage%>" />
			</div>      
		</div>
		<div style="float: right;margin-right: 15px;margin-top: 15px;">
			<div style="float:right; margin-left: 10px;">
				<a href="javascript:void(0);" class="fa fa-edit" title="Edit Company Small Logo" onclick="editCompanyLogo('COMPANY_LOGO_SMALL');">&nbsp;</a>
			</div>
			<div style="float:right;">
				<img style="height: 30px;" class="lazy" src="userImages/company_avatar_photo.png" data-original="<%=CF.getStrDocRetriveLocation()+IConstants.I_COMPANY+"/"+IConstants.I_IMAGE_SMALL+"/"+strImageSmall%>">
			</div>      
		</div>
		<div>
			<s:form id="formID" theme="simple" action="ConfigSettings" method="POST" cssClass="formcss">
				<s:hidden name="strSettingsId" />
				<s:hidden name="userscreen" />
				<s:hidden name="navigationId" />
				<s:hidden name="toPage" />
				
				<table class="table table-hover table_no_border">
					<tr>
						<td colspan="2"><h4>Organisation Details</h4><hr style="border:solid 1px #000; margin: 0px;"/></td>
					</tr>
					 
					<tr> 
						<th class="txtlabel alignRight" style="width:300px">Name of the Organization:<sup>*</sup></th>
						<td><s:textfield name="orgFullName" cssClass="validateRequired"/></td>
					</tr>
					
					<tr>
						<th class="txtlabel alignRight" style="width:300px">Organizations Sub Title:</th>
						<td><s:textfield name="orgSubTitle"/></td>
					</tr>
					
					<tr>
						<th class="txtlabel alignRight" style="width:300px">Organization Description:</th>
						<td><s:textarea name="orgDescription" rows="2" cols="22"></s:textarea></td>
					</tr>
					
					<tr>
						<th class="txtlabel alignRight" style="width:300px" valign="top">Address:<sup>*</sup></th>
						<td><s:textarea name="orgFullAddress" rows="2" cols="22" cssClass="validateRequired"></s:textarea></td>
					</tr>
					
					<tr>
						<th class="txtlabel alignRight">City:<sup>*</sup><br/></th>
						<td>
							<s:textfield name="orgCity" id="city" maxlength="40" cssClass="validateRequired"/>
						</td>
					</tr>
					
					<tr>
						<th class="txtlabel alignRight">Select Country:<sup>*</sup><br/></th>
						<td>
							<s:select id="country" cssClass="validateRequired" name="orgCountry" listKey="countryId" 	listValue="countryName" headerKey=""  
								headerValue="Select Country" onchange="getContent('statetdid', 'GetStateDetails.action?type=employee&country_id=' + this.value)"
						    	list="countryList" key="" required="true" />
						</td>
					</tr>
								
					<tr>
						<th class="txtlabel alignRight">Select State:<sup>*</sup><br/></th>
						<td id="statetdid">
							<s:select id="state" cssClass="validateRequired" name="state" listKey="stateId" listValue="stateName" headerKey="" 
								headerValue="Select State" list="stateList" key="" required="true" />
						</td>
					</tr>
					
					<tr>
						<th class="txtlabel alignRight" style="width:300px">Pincode:<sup>*</sup></th>
						<td><s:textfield name="orgPincode" cssClass="validateNumber validateRequired"/></td>
					</tr>
					
					<tr>
						<th class="txtlabel alignRight" style="width:300px">Contact No.:</th>
						<td><s:textfield name="orgContactNo" cssClass="validateNumber"/></td>
					</tr>
					
					<tr>
						<th class="txtlabel alignRight" style="width:300px">Fax No.:</th>
						<td><s:textfield name="orgFaxNo" cssClass="validateNumber"/></td>
					</tr>
					
					<tr>
						<th class="txtlabel alignRight">E-mail Address:<br/></th>
						<td><s:textfield name="orgEmailAddress" id="email" maxlength="100"/> </td>  <!-- cssClass="validate[required,custom[email]]" -->
					</tr>
								
					<tr>
						<th class="txtlabel alignRight" style="width:300px">Website:</th>
						<td><s:textfield name="orgWebsite"/></td>
					</tr>
					
					<tr>
						<th class="txtlabel alignRight" style="width:300px">Industry:</th>
						<td><s:textfield name="orgIndustry"/></td>
					</tr>
					
					<tr>
						<td colspan="2"><h4>Organisation Working Details</h4><hr style="border:solid 1px #000; margin: 0px;"/></td>
					</tr>
					
					<tr>
						<th class="txtlabel alignRight" style="width:300px">Enter date format:<sup>*</sup></th>
						<td>
							<s:select name="strDateFormat" listKey="dateFormatId" listValue="dateFormatName" list="dateFormatList" key=""  cssClass="validateRequired"/>
						</td>
					</tr>
					
					<tr>
						<th class="txtlabel alignRight" style="width:300px">Enter time format:<sup>*</sup></th>
						<td>
							<s:select name="strTimeFormat" listKey="timeFormatId" listValue="timeFormatName" list="timeFormatList" key=""  cssClass="validateRequired"/>
						</td>
					</tr>
						
					<tr>
						<th class="txtlabel alignRight" style="width:300px">Standard Full time hours:<sup>*</sup></th>
						<td><s:textfield name="stndFullTimeHrs" cssClass="validateRequired validateNumber"/></td>
					</tr>
					
					<tr>
						<th class="txtlabel alignRight" style="width:300px">Break Policy- On Completion of (hrs):<sup>*</sup></th>
						<td><s:textfield name="strLunchDeduct" cssClass="validateRequired validateNumber" cssStyle="width: 50px"/></td>
					</tr>
					
					<tr>
						<th class="txtlabel alignRight" style="width:300px">Deduct Lunch Break (hrs):<sup>*</sup></th>
						<td><s:textfield name="strLunchBreak" cssClass="validateRequired validateNumber" cssStyle="width:50px"/></td>
					</tr>
					
					<tr>
						<td class="txtlabel alignRight" style="width:300px">&nbsp;</td>
						<td class="">
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<span class="txtlabel">FROM</span>
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<span class="txtlabel">TO</span>
					   </td>
					</tr>
					
					<tr>
						<th class="txtlabel alignRight" style="width:300px">Financial Year:<sup>*</sup></th>
						<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							<s:textfield name="strFinancialYearStart" id="idFinancialYearStart" cssStyle="width:80px !important;" cssClass="validateRequired"/>
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							<s:textfield name="strFinancialYearEnd" id="idFinancialYearEnd" cssStyle="width:80px !important;" cssClass="validateRequired"/>
						</td>
					</tr>
					
					<%-- <tr>
						<td colspan="2"><h4>Code &amp; Standards</h4><hr style="border:solid 1px #000; margin: 0px;"/></td>
					</tr>
					
					<tr>
						<th valign="top" class="txtlabel alignRight">Auto-generate Employee Code:</th>
						<td><s:checkbox name="isAutoGenerate" id="isEmpCode" onclick="showCodeFields()"/></td>
					</tr>
					
					<tr id="empCode1">
						<th valign="top" class="txtlabel alignRight">Employee Code Alpha:<sup>*</sup></th>
						<td><s:textfield name="strEmpCodeAlpha"/></td>
					</tr>
					
					<tr id="empCode2">
						<th valign="top" class="txtlabel alignRight">Employee Code start numeric :<sup>*</sup></th>
						<td><s:textfield name="strEmpCodeNumber"/></td>
					</tr> --%>
					
					
					<tr>
						<td colspan="2"><h4>Other Information</h4><hr style="border:solid 1px #000; margin: 0px;"/></td>
					</tr>
					 
					<tr>
						<th class="txtlabel alignRight">Select Currency:<sup>*</sup></th>
						<td>
							<s:select name="orgCurrency" id="orgCurrency" cssClass="validateRequired" listKey="currencyId" listValue="currencyName" 
							  headerKey="" headerValue="Select Currency" list="currencyList" key="" required="true" />
						</td>
					</tr>
					
					<tr>
						<th class="txtlabel alignRight">Round Off Condtion:</th>
						<td>
							<s:select theme="simple" name="roundOffCondtion" id="roundOffCondtion" headerKey="" headerValue="Select Round Off Condition"
             					 list="#{'1':'1 Decimal Round Off','2':'2 Decimal Round Off','3':'Default Round Off'}" />  
						</td>
					</tr>
					
					<tr>
						<td colspan="2"><h4>Hosting &amp; Other System Settings</h4><hr style="border:solid 1px #000; margin: 0px;"/></td>
					</tr>
					
					<tr>
						<th valign="top" class="txtlabel alignRight">Host Address:<sup>*</sup></th>
						<td><s:textfield name="strEmailLocalHost"  cssClass="validateRequired"/></td>
					</tr>
					
					<tr>
						<th valign="top" class="txtlabel alignRight">Enable/Disable Email Notification:</th>
						<td><s:checkbox name="isEmail" id="isEmail" onclick="showEmailFields()"/></td>
					</tr>
								
					<%-- <tr id="email_host">
						<th valign="top" class="txtlabel alignRight">Email Host Address:<sup>*</sup></th>
						<td><s:textfield name="strEmailHost"  cssClass="validateRequired"/></td>
					</tr>
					
					<tr id="email_from">
						<th valign="top" class="txtlabel alignRight">Email From:<sup>*</sup></th>
						<td><s:textfield name="strEmailFrom"  cssClass="validateRequired"/></td>
					</tr>
					
					<tr id="email_password">
						<th valign="top" class="txtlabel alignRight">Email Password:<sup>*</sup></th>
						<td><s:textfield name="strEmailHostPassword"  cssClass="validateRequired"/></td>
					</tr>
					
					<tr id="trRequiredAuthentication">
						<th valign="top" class="txtlabel alignRight">&nbsp;</th>
						<td><s:checkbox name="isRequiredAuthentication" id="isRequiredAuthentication" onclick="showEmailRequiredAuthentication()"/> if required authentication</td>
					</tr> 
					
					<tr id="trEmailUsername">
						<th valign="top" class="txtlabel alignRight">User Name:<sup>*</sup></th>
						<td><s:textfield name="strEmailAuthUsername" cssClass="validateRequired"/></td>
					</tr>
					
					<tr id="trEmailPassword">
						<th valign="top" class="txtlabel alignRight">Password:<sup>*</sup></th>
						<td><s:textfield name="strEmailAuthPassword" cssClass="validateRequired"/></td>
					</tr>	 --%>				
					
					<tr>
						<th valign="top" class="txtlabel alignRight">Enable/Disable Text Notification:</th>
						<td><s:checkbox name="isText" id="isText" onclick="showTextFields()"/></td>
					</tr>
					
					<tr id="text_from">
						<th valign="top" class="txtlabel alignRight">Text From:<sup>*</sup></th>
						<td><s:textfield name="strTextFrom" cssClass="validateRequired" /></td>
					</tr>
					
					<tr>
						<th valign="top" class="txtlabel alignRight">Attendance Integrated with Activities:</th>
						<td><s:checkbox name="isAttendanceIntegrationWithActivities"/></td>
					</tr>
					
					<tr>
						<th valign="top" class="txtlabel alignRight">Show Password:</th>
						<td><s:checkbox  name="isShowPassword"/></td>
					</tr>
					
					<tr>
						<th valign="top" class="txtlabel alignRight">Enable/Disable Clock On/Off:</th>
						<td>
							<s:checkbox name="isClockOnOff"/><span class="hint">By enabling this, system will show clock on/off block on employee's my dashboard..<span class="hint-pointer">&nbsp;</span></span>
							<span> (Please Sign out & Login to effect the change.)</span>
						</td>
					</tr>
					
					<%-- <tr>
						<td valign="top" class="txtlabel alignRight">If Receipt not submited, taxable:</td>
						<td><s:checkbox name="isReceipt"/><span class="hint">By enabling this, system will automatic calculated for tds.<span class="hint-pointer">&nbsp;</span></span></td>
					</tr> --%>
					
					<tr>
						<th valign="top" class="txtlabel alignRight">Is Half Day Leave:</th>
						<td><s:checkbox name="isHalfDayLeave"/><span class="hint">By enabling this, system will show half day leave for apply.<span class="hint-pointer">&nbsp;</span></span></td>
					</tr>
					
					<tr>
						<th valign="top" class="txtlabel alignRight">Production Line:</th>
						<td><s:checkbox name="isProductionLine"/></td>
					</tr>
					
					<tr>
						<th valign="top" class="txtlabel alignRight">Sandwich to be applied on Absent status?:</th>
						<td><s:checkbox name="isSandwichAbsent"/></td>
					</tr>
					
					<tr>
						<th valign="top" class="txtlabel alignRight">Terminate without Full &amp; Final:</th>
						<td><s:checkbox name="isTerminateWithoutFullAndFinal"/></td>
					</tr>
					
					<tr>
						<th valign="top" class="txtlabel alignRight">TDS Auto Approve:</th>
						<td><s:checkbox name="isTDSAutoApprove"/></td>
					</tr>
					
					<tr>
						<th valign="top" class="txtlabel alignRight">Show Time Variance:</th>
						<td><s:checkbox name="isShowTimeVariance"/></td>
					</tr>
					<%-- <tr>
						<td class="txtlabel alignRight" style="width:300px">Salary Structure:<sup>*</sup></td>
						<td>
							<s:select theme="simple" name="salaryStructure" id="salaryStructure" list="#{'1':'Level Based','2':'Grade Based'}" cssClass="validate[required]"/>
							<span class="hint mt">Please select the salary structure.<span class="hint-pointer t55">&nbsp;</span></span>
						</td>
					</tr> --%> 
					
					<tr>
						<th valign="top" class="txtlabel alignRight">Tracker Password:</th>
						<td nowrap="nowrap">
						<% String passTrackerPassword = (String)request.getAttribute("passTrackerPassword"); %>
							<span id="passTrackerPasswordSpan" style="float: left;">
								<%-- <s:password name="passTrackerPassword" readonly="true" /> --%>
								<input type="password" name="passTrackerPassword" id="passTrackerPassword" readonly="readonly" value="<%=passTrackerPassword != null ? passTrackerPassword : "" %>"/>
							</span>
							<span id="textTrackerPasswordSpan" style="float: left; display: none;"><s:textfield name="textTrackerPassword" id="textTrackerPassword" readonly="true" /></span>
							<span style="float: left; margin-left: 10px;">
								<a href="javascript:void(0);" style="font-weight: normal;" onclick="seeTrackerPassword()">See Password</a>
							</span>
							<span style="float: left; margin-left: 10px;">
								<a href="javascript:void(0);" style="font-weight: normal;" onclick="changeTrackerPassword()">Generate New Password</a>
							</span>
						</td>
					</tr>
					
					<tr>
					    <th>&nbsp;</th>
						<td>
						<s:submit cssClass="btn btn-primary" id="btnSaveSettings" value="Save Settings" />
						</td>
					</tr>
					
				</table>
			</s:form>
		</div>



		<div style="float: right; width: 39%; margin-left: 5px; margin-top: 60px;">
		
			<div id="myModal" class="modal fade">
			    <div class="modal-dialog" style="width: 350px;">
			        <div class="modal-content">
			            <div class="modal-header">
			                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			                <h4 class="modal-title">Update Company Logo</h4>
			            </div>
			            <div class="modal-body">
							<s:form theme="simple" name="uploadImage" action="UploadImage" enctype="multipart/form-data" method="post" onsubmit="return checkImageSize();">
							<table class="table table_no_border">
								<tr>
									<td>
										<s:hidden  name="imageType" id="imageType" ></s:hidden>
										<s:hidden  name="userscreen"></s:hidden>
										<s:hidden  name="navigationId"></s:hidden>
										<s:hidden  name="toPage"></s:hidden>
										<s:file name="empImage" id="empImage" size="15"></s:file>
									</td>
								</tr>
								<tr>
									<td><span style="color:#999999">Image size must be smaller than or equal to 500kb.</span></td>
								</tr>
								<tr>
									<td align="center">
								        <s:submit value="Upload" cssClass="btb btn-primary"></s:submit>
								    </td>
								</tr>
								</table>
							</s:form>
			            </div>
			        </div>
			    </div>
			</div>
			
			
		
		</div>


   </div>

   
   <script>
   //showEmailFields();
   showTextFields();
   showCodeFields();
   </script>
<script>
	$("#idFinancialYearStart").datepicker({
		todayHighlight: true,
		format : 'dd/mm/yyyyyy',
		autoclose: true
	});
	$("#idFinancialYearEnd").datepicker({
		todayHighlight: true,
		format : 'dd/mm/yyyyyy',
		autoclose: true
	});
	
	$("#idFinancialYearStart").on('keydown', function() {
	    return false;
	});
	$("#idFinancialYearEnd").on('keydown', function() {
	    return false;
	});

	$("img.lazy").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10});
</script>