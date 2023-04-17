<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="com.konnect.jpms.select.FillWLocation"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<script>

jQuery(document).ready(function() {
	// binds form submission and fields to the validation engine
	$("#btnSave").click(function(){
		$(".validateRequired").prop('required',true);
		$(".validateEmail").prop('type','email');
		$(".validateNumber").prop('type','number');
	});
	
	$("#btnSaveAndSend").click(function(){
		$(".validateRequired").prop('required',true);
		$(".validateEmail").prop('type','email');
		$(".validateNumber").prop('type','number');
	});
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
 	

 	function addOtherDesig(type, cnt) {
		 if(type == 'A') {
			 document.getElementById("strClientContactDesig"+cnt).selectedIndex = '0';
			 document.getElementById("strClientContactDesig"+cnt).disabled = true;
			 document.getElementById("strClientContactDesig"+cnt).className = '';
			 document.getElementById("removeOtherDesigSpan"+cnt).style.display = 'inline';
			 document.getElementById("otherDesigDiv"+cnt).style.display = 'block';
			 document.getElementById("addOtherDesigSpan"+cnt).style.display = 'none';
		 } else {
			 document.getElementById("addOtherDesigSpan"+cnt).style.display = 'inline';
			 document.getElementById("strClientContactDesig"+cnt).disabled = false;
			 document.getElementById("strClientContactDesig"+cnt).className = document.getElementById("hiddenSPOCDesignationValidReqOpt").value;
			 document.getElementById("otherDesignation"+cnt).value='';
			 document.getElementById("otherDesigDiv"+cnt).style.display = 'none';
			 document.getElementById("existDesigDiv"+cnt).innerHTML='';
			 document.getElementById("removeOtherDesigSpan"+cnt).style.display = 'none';
		 }
	}


	function checkExistDesig(divid, val, cnt) {
		var xmlhttp = GetXmlHttpObject();
		if (xmlhttp == null) {
			alert("Browser does not support HTTP Request");
			return;
		} else {
			var xhr = $.ajax({
				url : "GetClientName.action?strDesigName="+ val,
				cache : false,
				success : function(data) {
					document.getElementById(divid).innerHTML = data;
					if(data.trim().length >1){
						document.getElementById("otherDesignation"+cnt).value='';
					}
				
				}
			});
		}
	}
	
	
	function addOtherDepart(type, cnt) {
		 if(type == 'A') {
			 document.getElementById("strClientContactDepartment"+cnt).selectedIndex = '0';
			 document.getElementById("strClientContactDepartment"+cnt).disabled = true;
			 document.getElementById("strClientContactDepartment"+cnt).className = '';
			 document.getElementById("removeOtherDepartSpan"+cnt).style.display = 'inline';
			 document.getElementById("otherDepartDiv"+cnt).style.display = 'block';
			 document.getElementById("addOtherDepartSpan"+cnt).style.display = 'none';
		 } else {
			 document.getElementById("addOtherDepartSpan"+cnt).style.display = 'inline';
			 document.getElementById("strClientContactDepartment"+cnt).disabled = false;
			 document.getElementById("strClientContactDepartment"+cnt).className = document.getElementById("hiddenSPOCDesignationValidReqOpt").value;
			 document.getElementById("otherDepartment"+cnt).value='';
			 document.getElementById("otherDepartDiv"+cnt).style.display = 'none';
			 document.getElementById("existDepartDiv"+cnt).innerHTML='';
			 document.getElementById("removeOtherDepartSpan"+cnt).style.display = 'none';
		 }
	}


	function checkExistDepart(divid, val, cnt) {
		var xmlhttp = GetXmlHttpObject();
		if (xmlhttp == null) {
			alert("Browser does not support HTTP Request");
			return;
		} else {
			var xhr = $.ajax({
				url : "GetClientName.action?strDepartName="+ val,
				cache : false,
				success : function(data) {
					document.getElementById(divid).innerHTML = data;
					if(data.trim().length >1){
						document.getElementById("otherDepartment"+cnt).value='';
					}
				
				}
			});
		}
	}
	
	
	function addOtherLoc(type, cnt) {
		 if(type == 'A') {
			 document.getElementById("strClientContactLocation"+cnt).selectedIndex = '0';
			 document.getElementById("strClientContactLocation"+cnt).disabled = true;
			 document.getElementById("strClientContactLocation"+cnt).className = '';
			 document.getElementById("removeOtherLocSpan"+cnt).style.display = 'inline';
			 document.getElementById("otherLocDiv"+cnt).style.display = 'block';
			 document.getElementById("addOtherLocSpan"+cnt).style.display = 'none';
		 } else {
			 document.getElementById("addOtherLocSpan"+cnt).style.display = 'inline';
			 document.getElementById("strClientContactLocation"+cnt).disabled = false;
			 document.getElementById("strClientContactLocation"+cnt).className = document.getElementById("hiddenSPOCDesignationValidReqOpt").value;
			 document.getElementById("otherLocation"+cnt).value='';
			 document.getElementById("otherLocDiv"+cnt).style.display = 'none';
			 document.getElementById("existLocDiv"+cnt).innerHTML='';
			 document.getElementById("removeOtherLocSpan"+cnt).style.display = 'none';
		 }
	}


	function checkExistLoc(divid, val, cnt) {
		var xmlhttp = GetXmlHttpObject();
		if (xmlhttp == null) {
			alert("Browser does not support HTTP Request");
			return;
		} else {
			var xhr = $.ajax({
				url : "GetClientName.action?strWLocName="+ val,
				cache : false,
				success : function(data) {
					document.getElementById(divid).innerHTML = data;
					if(data.trim().length >1){
						document.getElementById("otherLocation"+cnt).value='';
					}
				
				}
			});
		}
	}
	
	
function GetXmlHttpObject() {
		if (window.XMLHttpRequest) {
			return new XMLHttpRequest();
		}
		if (window.ActiveXObject) {
			return new ActiveXObject("Microsoft.XMLHTTP");
		}
	return null;
}

</script>

	<% 
		String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
		String strImage = (String) request.getAttribute("strImage");
		Map<String, List<String>> hmValidationFields = (Map<String, List<String>> ) request.getAttribute("hmValidationFields");
		UtilityFunctions uF = new UtilityFunctions();
		String validReqOpt = "";
		String validAsterix = "";
		String strClientId = (String)request.getAttribute("strClientId");
	%>
<div class="box box-info">
      <div class="box-header with-border">
        <h3 class="box-title"><b><%=uF.showData((String)request.getAttribute("strClientContactFName"), "New Customer") %></b></h3>
        <!-- <div class="box-tools pull-right">
          <button data-widget="collapse" class="btn btn-box-tool"><i class="fa fa-minus"></i></button>
          <button data-widget="remove" class="btn btn-box-tool"><i class="fa fa-times"></i></button>
        </div> -->
      </div><!-- /.box-header -->
      <div class="box-body table-responsive no-padding">
		<s:form theme="simple" action="AddNewCustomer" method="POST" cssClass="formcss" id="formAddNewCustomer" enctype="multipart/form-data">
			<s:hidden name="strClientContactId" />
			<s:hidden name="clientId" />
			<s:hidden name="proId" />
			<table border="0" class="table table_no_border">
				<tr><td class="txtlabel alignRight" valign="top">Aligned with:<sup>*</sup></td>
					<td colspan="3"><s:select theme="simple" name="clientBrand" id="clientBrand" cssClass="validateRequired" listKey="clientBrandId"
						listValue="clientBrandName" list="clientBrandList" key="" />
					</td>
				</tr>
				<tr>
				<% 	List<String> fNameValidList = hmValidationFields.get("COMPANY_CONTACT_FIRST_NAME"); 
					validReqOpt = "";
					validAsterix = "";
					if(uF.parseToBoolean(fNameValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
					<td class="txtlabel alignRight" valign="top">First Name:<%=validAsterix %>
						<input type="hidden" name="hiddenSPOCFNameValidAsterix" id="hiddenSPOCFNameValidAsterix" value="<%=validAsterix %>"/>
				        <input type="hidden" name="hiddenSPOCFNameValidReqOpt" id="hiddenSPOCFNameValidReqOpt" value="<%=validReqOpt %>"/>
					</td>
					<td><input type="text" class="<%=validReqOpt %>" name="strClientContactFName" id="strClientContactFName" value="<%=uF.showData((String)request.getAttribute("strClientContactFName"), "") %>"/>
					</td>
				<!-- </tr>
					
				<tr> -->
				<% 	List<String> mNameValidList = hmValidationFields.get("COMPANY_CONTACT_MIDDLE_NAME"); 
					validReqOpt = "";
					validAsterix = "";
					if(uF.parseToBoolean(mNameValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
					<td class="txtlabel alignRight">Middle Name:<%=validAsterix %>
						<input type="hidden" name="hiddenSPOCMNameValidAsterix" id="hiddenSPOCMNameValidAsterix" value="<%=validAsterix %>"/>
				       	<input type="hidden" name="hiddenSPOCMNameValidReqOpt" id="hiddenSPOCMNameValidReqOpt" value="<%=validReqOpt %>"/>
					</td>
					<td><input type="text" class="<%=validReqOpt %>" name="strClientContactMName" id="strClientContactMName" value="<%=uF.showData((String)request.getAttribute("strClientContactMName"), "") %>"/></td>
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
					<td class="txtlabel alignRight">Last Name:<%=validAsterix %>
						<input type="hidden" name="hiddenSPOCLNameValidAsterix" id="hiddenSPOCLNameValidAsterix" value="<%=validAsterix %>"/>
						<input type="hidden" name="hiddenSPOCLNameValidReqOpt" id="hiddenSPOCLNameValidReqOpt" value="<%=validReqOpt %>"/>
					</td>
					<td><input type="text" class="<%=validReqOpt %>" name="strClientContactLName" id="strClientContactLName" value="<%=uF.showData((String)request.getAttribute("strClientContactLName"), "") %>"/></td>
				<!-- </tr>
				
				<tr> -->
				<% 	List<String> contactNoValidList = hmValidationFields.get("COMPANY_CONTACT_CONTACT_NO"); 
					validReqOpt = "";
					validAsterix = "";
					if(uF.parseToBoolean(contactNoValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
					<td class="txtlabel alignRight">Contact No:<%=validAsterix %> 
						<input type="hidden" name="hiddenSPOCContactNoValidAsterix" id="hiddenSPOCContactNoValidAsterix" value="<%=validAsterix %>"/>
				        <input type="hidden" name="hiddenSPOCContactNoValidReqOpt" id="hiddenSPOCContactNoValidReqOpt" value="<%=validReqOpt %>"/>
					</td>
					<td><input type="text" class="<%=validReqOpt %>" name="strClientContactNo" id="strClientContactNo" value="<%=uF.showData((String)request.getAttribute("strClientContactNo"), "") %>"/></td>
				</tr>
				
				<tr>
				<% 	List<String> emailIdValidList = hmValidationFields.get("COMPANY_CONTACT_MAIL_ID"); 
					validReqOpt = "validateEmail";
					validAsterix = "";
					if(uF.parseToBoolean(emailIdValidList.get(0))) {
						validReqOpt = "validateRequired validateEmail";
						validAsterix = "<sup>*</sup>";
					}
				%>
					<td class="txtlabel alignRight">Email Id:<%=validAsterix %>
						<input type="hidden" name="hiddenSPOCEmailIdValidAsterix" id="hiddenSPOCEmailIdValidAsterix" value="<%=validAsterix %>"/>
				        <input type="hidden" name="hiddenSPOCEmailIdValidReqOpt" id="hiddenSPOCEmailIdValidReqOpt" value="<%=validReqOpt %>"/>
					</td>
					<td><input type="text" class="<%=validReqOpt %>" name="strClientContactEmail" id="strClientContactEmail" value="<%=uF.showData((String)request.getAttribute("strClientContactEmail"), "") %>"/></td>
				<!-- </tr>
				
				<tr> -->
        		<% 	List<String> spocDesigValidList = hmValidationFields.get("COMPANY_CONTACT_DESIGNATION"); 
					validReqOpt = "";
					validAsterix = "";
					if(uF.parseToBoolean(spocDesigValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
        			<td class="txtlabel alignRight">Designation:<%=validAsterix %>
        			<input type="hidden" name="hiddenSPOCDesignationValidAsterix" id="hiddenSPOCDesignationValidAsterix" value="<%=validAsterix %>"/>
				    <input type="hidden" name="hiddenSPOCDesignationValidReqOpt" id="hiddenSPOCDesignationValidReqOpt" value="<%=validReqOpt %>"/>
        			</td>
        			<td>
        				<select name="strClientContactDesig" id="strClientContactDesig0" class="<%=validReqOpt %>">
	        				<option value="">Select Designation</option>
	        				<%=(String)request.getAttribute("sbDesig") %>
        				</select>
        				<span id="addOtherDesigSpan0"> <a href="javascript:void(0);" onclick="addOtherDesig('A', '0');">Other</a></span>
						<span id="removeOtherDesigSpan0" style="display: none;"> <a href="javascript:void(0);" onclick="addOtherDesig('R', '0');">Reset</a></span>
						<div id="otherDesigDiv0" style="display: none; margin-top: 10px;"><input type="text" name="otherDesignation" id="otherDesignation0" placeholder="Add New Designation" onchange="checkExistDesig('existDesigDiv0', this.value, '0');"/></div>
						<div id="existDesigDiv0" style="font-size: 11px;"></div>
        			</td>
        		</tr>
        
        		<tr>
        		<% 	List<String> spocDepartValidList = hmValidationFields.get("COMPANY_CONTACT_DEPARTMENT"); 
					validReqOpt = "";
					validAsterix = "";
					if(uF.parseToBoolean(spocDepartValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
        			<td class="txtlabel alignRight">Department:<%=validAsterix %>
        			<input type="hidden" name="hiddenSPOCDepartmentValidAsterix" id="hiddenSPOCDepartmentValidAsterix" value="<%=validAsterix %>"/>
				    <input type="hidden" name="hiddenSPOCDepartmentValidReqOpt" id="hiddenSPOCDepartmentValidReqOpt" value="<%=validReqOpt %>"/>
        			</td>
        			<td>
        				<select name="strClientContactDepartment" id="strClientContactDepartment0" class="<%=validReqOpt %>">
	        				<option value="">Select Department</option>
	        				<%=(String)request.getAttribute("sbDepart") %>
        				</select>
        				<span id="addOtherDepartSpan0"> <a href="javascript:void(0);" onclick="addOtherDepart('A', '0');">Other</a></span>
						<span id="removeOtherDepartSpan0" style="display: none;"> <a href="javascript:void(0);" onclick="addOtherDepart('R', '0');">Reset</a></span>
						<div id="otherDepartDiv0" style="display: none; margin-top: 10px;"><input type="text" name="otherDepartment" id="otherDepartment0" placeholder="Add New Department" onchange="checkExistDepart('existDepartDiv0', this.value, '0');"/></div>
						<div id="existDepartDiv0" style="font-size: 11px;"></div>
        			</td>
        		<!-- </tr>
        
        		<tr> -->
        		<% 	List<String> spocLocationValidList = hmValidationFields.get("COMPANY_CONTACT_LOCATION"); 
					validReqOpt = "";
					validAsterix = "";
					if(uF.parseToBoolean(spocLocationValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
        			<td class="txtlabel alignRight">Location:<%=validAsterix %>
        			<input type="hidden" name="hiddenSPOCLocationValidAsterix" id="hiddenSPOCLocationValidAsterix" value="<%=validAsterix %>"/>
				    <input type="hidden" name="hiddenSPOCLocationValidReqOpt" id="hiddenSPOCLocationValidReqOpt" value="<%=validReqOpt %>"/>
        			</td>
        			<td>
        				<select name="strClientContactLocation" id="strClientContactLocation0" class="<%=validReqOpt %>">
	        				<option value="">Select Location</option>
	        				<%=(String)request.getAttribute("sbWLoc") %>
        				</select>
        				<span id="addOtherLocSpan0"> <a href="javascript:void(0);" onclick="addOtherLoc('A', '0');">Other</a></span>
						<span id="removeOtherLocSpan0" style="display: none;"> <a href="javascript:void(0);" onclick="addOtherLoc('R', '0');">Reset</a></span>
						<div id="otherLocDiv0" style="display: none; margin-top: 10px;"><input type="text" name="otherLocation" id="otherLocation0" placeholder="Add New Location" onchange="checkExistLoc('existLocDiv0', this.value, '0');"/></div>
						<div id="existLocDiv0" style="font-size: 11px;"></div>
        			</td>
        		</tr>
        		
				<%-- <tr>
				<% 	List<String> desigValidList = hmValidationFields.get("COMPANY_CONTACT_DESIGNATION"); 
					validReqOpt = "";
					validAsterix = "";
					if(uF.parseToBoolean(desigValidList.get(0))) {
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
					if(uF.parseToBoolean(departmentValidList.get(0))) {
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
					if(uF.parseToBoolean(locationValidList.get(0))) {
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
				</tr> --%>
				
				<tr>
					<td class="txtlabel alignRight">Photo:<%=validAsterix %></td>
					<td>
						<img height="60" width="60" class="lazy img-circle" id="strClientContactPhotoImg0" style="border: 1px solid #CCCCCC; " src="userImages/avatar_photo.png" data-original="<%=(String)request.getAttribute("strClientContactPhotoFile") %>" />
						<input type="file" name="strClientContactPhoto" id="strClientContactPhoto0" onchange="readImageURL(this, 'strClientContactPhotoImg0');"/>
					</td>
				</tr>	 
			</table>
		
		<div class="clr"></div>
		<div style="margin:0px 0px 15px 210px">
		<table class="table table_no_border">
			<tr><td colspan="2" align="center">
				<s:submit cssClass="btn btn-primary" value="Save" name="btnSave" id="btnSave"/>
				<%-- <s:submit cssClass="btn btn-primary" value="Save & Send to Customer" name="btnSaveAndSend" id="btnSaveAndSend"/> --%>
				<input type="button" value="Cancel" class="btn btn-danger" style="margin-right: 5px; padding: 3px;" name="cancel" onclick="closeForm('');">
					<%-- <s:reset  cssClass="input_button" value="Cancel" id="btnAddNewRowCancel"/> --%>
			</td></tr>				
		 </table>
		 </div>
		</s:form>
		
	</div>
</div>

<script>
	//$("img.lazy1").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10});
	$("img.lazy1").lazyload({event : "sporty", threshold : 200,effect : "fadeIn",failure_limit : 10});
	$(window).bind("load", function() {
	    var timeout = setTimeout(function() { $("img.lazy1").trigger("sporty") }, 1000);
	});

</script>
