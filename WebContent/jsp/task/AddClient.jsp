<%@page import="java.util.HashMap"%>
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
		$(".validateRequired").filter(':visible').prop('required',true);
		$(".validateRequired").filter(':hidden').prop('required', false);		
		$(".validateEmail").prop('type','email');
		$(".validateNumber").prop('type','number');
		$(".validateNumber").prop('step','any');
	});
	
	$("#btnSaveAndSend").click(function(){
		$(".validateRequired").filter(':visible').prop('required',true);
		$(".validateRequired").filter(':hidden').prop('required', false);
		$(".validateEmail").prop('type','email');
		$(".validateNumber").prop('type','number');
		$(".validateNumber").prop('step','any');
	});

	var country = document.getElementById("strClientCountry").value;
	var validState = document.getElementById("hiddenStateValidReqOpt").value;
	var strClientState = document.getElementById("strClientState").value;
	getContentAcs('stateTD','GetStates.action?country='+country+'&type=CLIENTADDSTATE&validReq='+validState+'&strClientState='+strClientState);
	
});


function addFirstBrand() {
	document.getElementById("clientBrand0").style.display = 'block';
	document.getElementById("addFirstBrandDiv").style.display = 'none';
	document.getElementById("addOtherBrandDiv").style.display = 'block';
}


function removeFirstBrand() {
	document.getElementById("strClientBrandName0").value='';
	document.getElementById("clientBrand0").style.display = 'none';
	document.getElementById("addFirstBrandDiv").style.display = 'block';
	document.getElementById("addOtherBrandDiv").style.display = 'none';
}
		
function addNewBrand() {
	
	var cnt = document.getElementById("hideBrandCount").value;
	var countryOpt = document.getElementById("hideCountryOption").value;
	var industryOpt = document.getElementById("hideIndustryOption").value;
	//alert("cnt ===>> " + cnt);
	var hiddenBrandNameValidAsterix = document.getElementById("hiddenBrandNameValidAsterix").value;
	var hiddenBrandNameValidReqOpt = document.getElementById("hiddenBrandNameValidReqOpt").value;
	
	var hiddenBrandAddressValidAsterix = document.getElementById("hiddenBrandAddressValidAsterix").value;
	var hiddenBrandAddressValidReqOpt = document.getElementById("hiddenBrandAddressValidReqOpt").value;
	
	var hiddenBrandCityValidAsterix = document.getElementById("hiddenBrandCityValidAsterix").value;
	var hiddenBrandCityValidReqOpt = document.getElementById("hiddenBrandCityValidReqOpt").value;
	
	var hiddenBrandCountryValidAsterix = document.getElementById("hiddenBrandCountryValidAsterix").value;
	var hiddenBrandCountryValidReqOpt = document.getElementById("hiddenBrandCountryValidReqOpt").value;
	
	var hiddenBrandStateValidAsterix = document.getElementById("hiddenBrandStateValidAsterix").value;
	var hiddenBrandStateValidReqOpt = document.getElementById("hiddenBrandStateValidReqOpt").value;
	
	var hiddenBrandPinCodeValidAsterix = document.getElementById("hiddenBrandPinCodeValidAsterix").value;
	var hiddenBrandPinCodeValidReqOpt = document.getElementById("hiddenBrandPinCodeValidReqOpt").value;
	
	var hiddenBrandIndustryValidAsterix = document.getElementById("hiddenBrandIndustryValidAsterix").value;
	var hiddenBrandIndustryValidReqOpt = document.getElementById("hiddenBrandIndustryValidReqOpt").value;
	
	var hiddenBrandWebsiteValidAsterix = document.getElementById("hiddenBrandWebsiteValidAsterix").value;
	var hiddenBrandWebsiteValidReqOpt = document.getElementById("hiddenBrandWebsiteValidReqOpt").value;
	
	var hiddenBrandDescriptionValidAsterix = document.getElementById("hiddenBrandDescriptionValidAsterix").value;
	var hiddenBrandDescriptionValidReqOpt = document.getElementById("hiddenBrandDescriptionValidReqOpt").value;
	
	var hiddenBrandGSTValidAsterix = document.getElementById("hiddenBrandGSTValidAsterix").value;
	var hiddenBrandGSTValidReqOpt = document.getElementById("hiddenBrandGSTValidReqOpt").value;
	
	var hiddenBrandGSTNoValidAsterix = document.getElementById("hiddenBrandGSTNoValidAsterix").value;
	var hiddenBrandGSTNoValidReqOpt = document.getElementById("hiddenBrandGSTNoValidReqOpt").value;
	
	cnt++;
	//alert("cnt 1 ===>> " + cnt);
	var divTag = document.createElement("div");
    divTag.id = "clientBrand" + cnt;
    //alert("divTag ===>> "+ divTag);
    divTag.setAttribute("style", "float:left; width: 100%;");
        divTag.innerHTML = "<table class=\"table table_no_border\"><tr><td colspan=\"4\"><s:fielderror/></td></tr>"
        +"<tr><td colspan=\"4\"><hr style=\"height: 1px; width: 100%; margin: 0px; background-color: lightgray;\"></td></tr>"
        +"<tr><td colspan=\"2\"><label>&nbsp;&nbsp; New Subsidiary/ Brand</label></td>"
        +"<td class=\"alignRight\" colspan=\"2\" style=\"padding-right: 25px !important;\">&nbsp;<input type=\"hidden\" name=\"clientBrandCntId\" id=\"clientBrandCntId"+cnt+"\" value=\""+cnt+"\"/>"
        //+"<a href=\"javascript:void(0)\" onclick=\"addNewBrand()\" class=\"fa fa-fw fa-plus\" title=\"Add Subsidiary/ Brand\">&nbsp;</a>"
        +"<a href=\"javascript:void(0)\"  onclick=\"removeBrand(this.id)\" id=\""+cnt+"\" class=\"fa fa-fw fa-times-circle\" style=\"color: red; font-size: 18px;\" title=\"Remove Subsidiary/ Brand\">&nbsp;</a></td>"
        +"</tr>"
        +"<tr><td class=\"txtlabel alignRight\" valign=\"top\">Name:"+hiddenBrandNameValidAsterix+"</td>"
        +"<td colspan=\"3\"><input type=\"text\" class=\""+hiddenBrandNameValidReqOpt+"\" name=\"strClientBrandName\" id=\"strClientBrandName"+cnt+"\" onkeyup =\"getContent('myBrandDiv"+cnt+"', 'GetClientName.action?strClientBrandName='+this.value)\"/>"
        +"<span class=\"hint\">Add the Subsidiary/ Brand name.<span class=\"hint-pointer\">&nbsp;</span></span>"
        +"<div id=\"myBrandDiv"+cnt+"\" style=\"font-size: 11px;\"></div></td>"
        +"</tr>"
        +"<tr><td class=\"txtlabel alignRight\">Logo:</td>"
        +"<td><img height=\"60\" width=\"60\" class=\"lazy\" id=\"clientBrandLogoImg"+cnt+"\" style=\"border: 1px solid #CCCCCC; border: 1px lightgray solid; margin-bottom: 2px;\" src=\"userImages/company_avatar_photo.png\" data-original=\"userImages/company_avatar_photo.png\" />"
        +"<input type=\"file\" name=\"clientBrandLogo\" id=\"clientBrandLogo"+cnt+"\" onchange=\"readImageURL(this, 'clientBrandLogoImg"+cnt+"');\"/>"
        +"</td>"
        +"<td class=\"txtlabel alignRight\" valign=\"top\">Address:"+hiddenBrandAddressValidAsterix+"</td>"
        +"<td><textarea name=\"strClientBrandAddress\" id=\"strClientBrandAddress"+cnt+"\" class=\""+hiddenBrandAddressValidReqOpt+"\" rows=\"4\" cols=\"22\"></textarea>"
        +"</td>"
        +"</tr>"
        +"<tr><td class=\"txtlabel alignRight\">City:"+hiddenBrandCityValidAsterix+"</td>"
        +"<td><input type=\"text\" name=\"strClientBrandCity\" id=\"strClientBrandCity"+cnt+"\" class=\""+hiddenBrandCityValidReqOpt+"\" />"
        +"</td>"
        +"<td class=\"txtlabel alignRight\">Country:"+hiddenBrandCountryValidAsterix+"</td>"
        +"<td><select name=\"strClientBrandCountry\" id=\"strClientBrandCountry"+cnt+"\" class=\""+hiddenBrandCountryValidReqOpt+"\" onchange=\"getContentAcs('stateBrandTD"+cnt+"','GetStates.action?country='+this.value+'&type=CLIENTBRANDADDSTATE');\" ><option value=\"\">Select Country</option>"+countryOpt+"</select>"
        +"</td>"
		+"</tr>"
		+"<tr><td class=\"txtlabel alignRight\">State:"+hiddenBrandStateValidAsterix+"</td>"
		+"<td id=\"stateBrandTD"+cnt+"\"><select name=\"strClientBrandState\" id=\"strClientBrandState"+cnt+"\" class=\""+hiddenBrandStateValidReqOpt+"\" ><option value=\"\">Select State</option></select>"
		+"</td>"
		+"<td class=\"txtlabel alignRight\">Pin Code:"+hiddenBrandPinCodeValidAsterix+"</td>"
		+"<td><input type=\"text\" name=\"strBrandPinCode\" id=\"strBrandPinCode"+cnt+"\" class=\""+hiddenBrandPinCodeValidReqOpt+"\" />"
		+"<span class=\"hint\">Add the pin code for this client brand.<span class=\"hint-pointer\">&nbsp;</span></span>"
		+"</td>"
		+"</tr>"
		+"<tr><td class=\"txtlabel alignRight\">Industry:"+hiddenBrandIndustryValidAsterix+"</td>"
		+"<td><select name=\"clientBrandIndustry"+cnt+"\" id=\"clientBrandIndustry"+cnt+"\" class=\""+hiddenBrandIndustryValidReqOpt+"\" multiple=\"true\" size=\"3\">"+industryOpt+"</select>"
		+"<span id=\"addBrandOtherSpan"+cnt+"\"> <a href=\"javascript:void(0);\" onclick=\"addBrandOtherIndustry('A', '"+cnt+"');\">Other</a></span>"
		+"<span id=\"removeBrandOtherSpan"+cnt+"\" style=\"display: none;\"> <a href=\"javascript:void(0);\" onclick=\"addBrandOtherIndustry('R', '"+cnt+"');\">Reset</a></span>"
		+"<div id=\"otherBrandDiv"+cnt+"\" style=\"display: none; margin-top: 10px;\"><input type=\"text\" name=\"otherBrandIndustry\" id=\"otherBrandIndustry"+cnt+"\" placeholder=\"Add New Industry\" onblur=\"checkBrandExistIndustry('"+cnt+"', 'existBrandIndustryDiv"+cnt+"', this.value);\" onchange=\"checkBrandExistIndustry('"+cnt+"', 'existBrandIndustryDiv"+cnt+"', this.value);\"/></div>"
		+"<div id=\"existBrandIndustryDiv"+cnt+"\" style=\"font-size: 11px;\"></div>"
		+"</td>"
		+"<td class=\"txtlabel alignRight\">Website:"+hiddenBrandWebsiteValidAsterix+"</td>"
		+"<td><input type=\"text\" name=\"companyBrandWebsite\" id=\"companyBrandWebsite"+cnt+"\" class=\""+hiddenBrandWebsiteValidReqOpt+"\"/></td>"
		+"</tr>"
		+"<tr><td class=\"txtlabel alignRight\">Description:"+hiddenBrandDescriptionValidAsterix+"</td>"
		+"<td colspan=\"3\"><textarea name=\"companyBrandDescription\" id=\"companyBrandDescription"+cnt+"\" class=\""+hiddenBrandDescriptionValidReqOpt+"\" rows=\"4\" cols=\"22\" style=\"width: 90% !important;\"></textarea>"
		+"</td>"
		+"</tr>"
		+"<tr><td colspan=\"4\"><hr></td> </tr>"
		+"<tr><td colspan=\"4\"> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Statutory Compliance Details</td></tr>"
		+"<tr><td class=\"txtlabel alignRight\">GST:"+hiddenBrandGSTValidAsterix+"</td>"
		+"<td> <input type=\"text\" name=\"clientBrandTds\" id=\"clientBrandTds"+cnt+"\" class=\""+hiddenBrandGSTValidReqOpt+"\" /> % </td>"
		+"<td class=\"txtlabel alignRight\">GSTIN No.:"+hiddenBrandGSTNoValidAsterix+"</td>"
		+"<td><input type=\"text\" name=\"clientBrandRegistrationNo\" id=\"clientBrandRegistrationNo"+cnt+"\" class=\""+hiddenBrandGSTNoValidReqOpt+"\" /> </td>"
		+"</tr>"
		+"</table>"
		+"<input type=\"hidden\" name=\"hideBrandSpocCount"+cnt+"\" id=\"hideBrandSpocCount"+cnt+"\" value=\"0\" />";
		//alert("cnt 2 ===>> " + cnt);

		document.getElementById("hideBrandCount").value = cnt;
		document.getElementById("addBrandDiv").appendChild(divTag);
	
		$("#clientBrandIndustry"+cnt).multiselect().multiselectfilter();
		
		addNewBrandSPOC(cnt);
}


function removeBrand(removeId) {
    var remove_elem = "clientBrand"+removeId;
    var row_skill = document.getElementById(remove_elem);
    document.getElementById("addBrandDiv").removeChild(row_skill);
}


function addNewSPOC() {
	var cnt = document.getElementById("hideSpocCount").value;
	var desigOpt = document.getElementById("hideDesigOption").value;
	var departOpt = document.getElementById("hideDepartOption").value;
	var WLocOpt = document.getElementById("hideWLocOption").value;
	var hiddenSPOCFNameValidAsterix = document.getElementById("hiddenSPOCFNameValidAsterix").value;
	var hiddenSPOCFNameValidReqOpt = document.getElementById("hiddenSPOCFNameValidReqOpt").value;
	var hiddenSPOCMNameValidAsterix = document.getElementById("hiddenSPOCMNameValidAsterix").value;
	var hiddenSPOCMNameValidReqOpt = document.getElementById("hiddenSPOCMNameValidReqOpt").value;
	var hiddenSPOCLNameValidAsterix = document.getElementById("hiddenSPOCLNameValidAsterix").value;
	var hiddenSPOCLNameValidReqOpt = document.getElementById("hiddenSPOCLNameValidReqOpt").value;
	
	var hiddenSPOCContactNoValidAsterix = document.getElementById("hiddenSPOCContactNoValidAsterix").value;
	var hiddenSPOCContactNoValidReqOpt = document.getElementById("hiddenSPOCContactNoValidReqOpt").value;
	
	var hiddenSPOCEmailIdValidAsterix = document.getElementById("hiddenSPOCEmailIdValidAsterix").value;
	var hiddenSPOCEmailIdValidReqOpt = document.getElementById("hiddenSPOCEmailIdValidReqOpt").value;
	
	var hiddenSPOCDesignationValidAsterix = document.getElementById("hiddenSPOCDesignationValidAsterix").value;
	var hiddenSPOCDesignationValidReqOpt = document.getElementById("hiddenSPOCDesignationValidReqOpt").value;
	var hiddenSPOCDepartmentValidAsterix = document.getElementById("hiddenSPOCDepartmentValidAsterix").value;
	var hiddenSPOCDepartmentValidReqOpt = document.getElementById("hiddenSPOCDepartmentValidReqOpt").value;
	var hiddenSPOCLocationValidAsterix = document.getElementById("hiddenSPOCLocationValidAsterix").value;
	var hiddenSPOCLocationValidReqOpt = document.getElementById("hiddenSPOCLocationValidReqOpt").value;
    cnt++;
    //alert("cnt ===>> "+ cnt);
    var divTag = document.createElement("div");
    divTag.id = "clientSPOC" + cnt;
    //alert("divTag ===>> "+ divTag);
    divTag.setAttribute("style", "float:left; width: 100%;");
        divTag.innerHTML = "<table class=\"table table_no_border\" ><tr><td colspan=\"4\"><hr></td></tr>"
        +"<tr><td colspan=\"2\" ><label>&nbsp;&nbsp;Add New Contact Person</label></td><td colspan=\"2\">&nbsp;</td></tr>"
        
        +"<tr><td class=\"txtlabel alignRight\">First Name:"+hiddenSPOCFNameValidAsterix+"</td>"+
        "<td><input type=\"text\" name=\"strClientContactFName\" id=\"strClientContactFName"+cnt+"\" class=\""+hiddenSPOCFNameValidReqOpt+"\" /></td>" //</tr>
        +"<td class=\"txtlabel alignRight\">Middle Name:"+hiddenSPOCMNameValidAsterix+"</td>"+ //<tr>
        "<td><input type=\"text\" name=\"strClientContactMName\" id=\"strClientContactMName"+cnt+"\" class=\""+hiddenSPOCMNameValidReqOpt+"\" /></td></tr>"
        
        +"<tr><td class=\"txtlabel alignRight\">Last Name:"+hiddenSPOCLNameValidAsterix+"</td>"+
        "<td><input type=\"text\" name=\"strClientContactLName\" id=\"strClientContactLName"+cnt+"\" class=\""+hiddenSPOCLNameValidReqOpt+"\" /></td>" //</tr>
        +"<td class=\"txtlabel alignRight\">Contact No:"+hiddenSPOCContactNoValidAsterix+"</td>" //<tr>
        +"<td><input type=\"text\" name=\"strClientContactNo\" id=\"strClientContactNo"+cnt+"\" class=\""+hiddenSPOCContactNoValidAsterix+"\" /></td></tr>"
        
        +"<tr><td class=\"txtlabel alignRight\">Email Id:"+hiddenSPOCEmailIdValidAsterix+"</td>"
        +"<td><input type=\"text\" name=\"strClientContactEmail\" id=\"strClientContactEmail"+cnt+"\" class=\""+hiddenSPOCEmailIdValidReqOpt+"\" /></td>" //</tr>
        +"<td class=\"txtlabel alignRight\">Designation:"+hiddenSPOCDesignationValidAsterix+"</td>" //<tr>
        +"<td><input type=\"hidden\" name=\"strClientContactDesig\" id=\"strClientContactDesigHidden"+cnt+"\" disabled=\"disabled\">"
        +"<select name=\"strClientContactDesig\" id=\"strClientContactDesig"+cnt+"\" class=\""+hiddenSPOCDesignationValidReqOpt+"\" ><option value=\"\">Select Designation</option>"+desigOpt+"</select>"
        +"<span id=\"addOtherDesigSpan"+cnt+"\"> <a href=\"javascript:void(0);\" onclick=\"addOtherDesig('A', '"+cnt+"');\">Other</a></span>"
		+"<span id=\"removeOtherDesigSpan"+cnt+"\" style=\"display: none;\"> <a href=\"javascript:void(0);\" onclick=\"addOtherDesig('R', '"+cnt+"');\">Reset</a></span>"
		+"<div id=\"otherDesigDiv"+cnt+"\" style=\"display: none; margin-top: 10px;\"><input type=\"text\" name=\"otherDesignation\" id=\"otherDesignation"+cnt+"\" placeholder=\"Add New Designation\" onblur=\"checkExistDesig('existDesigDiv"+cnt+"', this.value, '"+cnt+"');\" onchange=\"checkExistDesig('existDesigDiv"+cnt+"', this.value, '"+cnt+"');\"/></div>"
		+"<div id=\"existDesigDiv"+cnt+"\" style=\"font-size: 11px;\"></div>"
		+"</td>"
        +"</tr>"
        
        +"<tr><td class=\"txtlabel alignRight\">Department:"+hiddenSPOCDepartmentValidAsterix+"</td>"
        +"<td><input type=\"hidden\" name=\"strClientContactDepartment\" id=\"strClientContactDepartmentHidden"+cnt+"\" disabled=\"disabled\">"
        +"<select name=\"strClientContactDepartment\" id=\"strClientContactDepartment"+cnt+"\" class=\""+hiddenSPOCDepartmentValidReqOpt+"\" ><option value=\"\">Select Department</option>"+departOpt+"</select>"
        +"<span id=\"addOtherDepartSpan"+cnt+"\"> <a href=\"javascript:void(0);\" onclick=\"addOtherDepart('A', '"+cnt+"');\">Other</a></span>"
		+"<span id=\"removeOtherDepartSpan"+cnt+"\" style=\"display: none;\"> <a href=\"javascript:void(0);\" onclick=\"addOtherDepart('R', '"+cnt+"');\">Reset</a></span>"
		+"<div id=\"otherDepartDiv"+cnt+"\" style=\"display: none; margin-top: 10px;\"><input type=\"text\" name=\"otherDepartment\" id=\"otherDepartment"+cnt+"\" placeholder=\"Add New Department\" onblur=\"checkExistDepart('existDepartDiv"+cnt+"', this.value, '"+cnt+"');\" onchange=\"checkExistDepart('existDepartDiv"+cnt+"', this.value, '"+cnt+"');\"/></div>"
		+"<div id=\"existDepartDiv"+cnt+"\" style=\"font-size: 11px;\"></div>"
		+"</td>"
        //+"</tr>"
        
        +"<td class=\"txtlabel alignRight\">Location:"+hiddenSPOCLocationValidAsterix+"</td>" //<tr>
        +"<td><input type=\"hidden\" name=\"strClientContactLocation\" id=\"strClientContactLocationHidden"+cnt+"\" disabled=\"disabled\">"
        +"<select name=\"strClientContactLocation\" id=\"strClientContactLocation"+cnt+"\" class=\""+hiddenSPOCLocationValidReqOpt+"\" ><option value=\"\">Select Location</option>"+WLocOpt+"</select>"
        +"<span id=\"addOtherLocSpan"+cnt+"\"> <a href=\"javascript:void(0);\" onclick=\"addOtherLoc('A', '"+cnt+"');\">Other</a></span>"
		+"<span id=\"removeOtherLocSpan"+cnt+"\" style=\"display: none;\"> <a href=\"javascript:void(0);\" onclick=\"addOtherLoc('R', '"+cnt+"');\">Reset</a></span>"
		+"<div id=\"otherLocDiv"+cnt+"\" style=\"display: none; margin-top: 10px;\"><input type=\"text\" name=\"otherLocation\" id=\"otherLocation"+cnt+"\" placeholder=\"Add New Location\" onblur=\"checkExistLoc('existLocDiv"+cnt+"', this.value, '"+cnt+"');\" onchange=\"checkExistLoc('existLocDiv"+cnt+"', this.value, '"+cnt+"');\"/></div>"
		+"<div id=\"existLocDiv"+cnt+"\" style=\"font-size: 11px;\"></div>"
		+"</td>"
        +"</tr>"
       
        +"<tr><td class=\"txtlabel alignRight\">Photo:</td>"
        +"<td><img height=\"60\" width=\"60\" class=\"lazy\" id=\"strClientContactPhotoImg"+cnt+"\" style=\"width: 1px solid #AAAAAA; border: 1px lightgray solid; margin-bottom: 2px;\" src=\"userImages/avatar_photo.png\" data-original=\"\" /> "
        +"<input type=\"file\" name=\"strClientContactPhoto\" id=\"strClientContactPhoto"+cnt+"\" onchange=\"readImageURL(this, 'strClientContactPhotoImg"+cnt+"');\"/></td>"
        +"<td class=\"alignRight\" colspan=\"2\" style=\"vertical-align: bottom; padding-right: 30px !important;\">"
        +"<a href=\"javascript:void(0)\"  onclick=\"addNewSPOC()\" class=\"fa fa-fw fa-plus\" title=\"Add SPOC\">&nbsp;</a>"
        +"<a href=\"javascript:void(0)\"  onclick=\"removeSPOC(this.id)\" id=\""+cnt+"\" class=\"fa fa-fw fa-remove\" style=\"color: red;\" title=\"Remove SPOC\">&nbsp;</a></td>"
        +"</tr>"
        
        +"</table>";
        //alert("divTag after ===>> "+ divTag);
        
    document.getElementById("hideSpocCount").value = cnt;
	document.getElementById("addSPOCDiv").appendChild(divTag);
}


function removeSPOC(removeId) {
    var remove_elem = "clientSPOC"+removeId;
    var row_skill = document.getElementById(remove_elem);
    document.getElementById("addSPOCDiv").removeChild(row_skill);
}


	function addNewBrandSPOC(brandCnt) {

		var cnt = document.getElementById("hideBrandSpocCount"+brandCnt).value;
		var desigOpt = document.getElementById("hideDesigOption").value;
		var departOpt = document.getElementById("hideDepartOption").value;
		var WLocOpt = document.getElementById("hideWLocOption").value;
		var hiddenSPOCFNameValidAsterix = document.getElementById("hiddenSPOCFNameValidAsterix").value;
		var hiddenSPOCFNameValidReqOpt = document.getElementById("hiddenSPOCFNameValidReqOpt").value;
		var hiddenSPOCMNameValidAsterix = document.getElementById("hiddenSPOCMNameValidAsterix").value;
		var hiddenSPOCMNameValidReqOpt = document.getElementById("hiddenSPOCMNameValidReqOpt").value;
		var hiddenSPOCLNameValidAsterix = document.getElementById("hiddenSPOCLNameValidAsterix").value;
		var hiddenSPOCLNameValidReqOpt = document.getElementById("hiddenSPOCLNameValidReqOpt").value;
		
		var hiddenSPOCContactNoValidAsterix = document.getElementById("hiddenSPOCContactNoValidAsterix").value;
		var hiddenSPOCContactNoValidReqOpt = document.getElementById("hiddenSPOCContactNoValidReqOpt").value;
		
		var hiddenSPOCEmailIdValidAsterix = document.getElementById("hiddenSPOCEmailIdValidAsterix").value;
		var hiddenSPOCEmailIdValidReqOpt = document.getElementById("hiddenSPOCEmailIdValidReqOpt").value;
		
		var hiddenSPOCDesignationValidAsterix = document.getElementById("hiddenSPOCDesignationValidAsterix").value;
		var hiddenSPOCDesignationValidReqOpt = document.getElementById("hiddenSPOCDesignationValidReqOpt").value;
		var hiddenSPOCDepartmentValidAsterix = document.getElementById("hiddenSPOCDepartmentValidAsterix").value;
		var hiddenSPOCDepartmentValidReqOpt = document.getElementById("hiddenSPOCDepartmentValidReqOpt").value;
		var hiddenSPOCLocationValidAsterix = document.getElementById("hiddenSPOCLocationValidAsterix").value;
		var hiddenSPOCLocationValidReqOpt = document.getElementById("hiddenSPOCLocationValidReqOpt").value;
	    cnt++;
	    //alert("cnt ===>> "+ cnt);
	    var divTag = document.createElement("div");
	    divTag.id = "clientBrandSPOC"+brandCnt+"_"+cnt;
	    //alert("divTag ===>> "+ divTag);
	    divTag.setAttribute("style", "float:left; width: 100%;");
	        divTag.innerHTML = "<table class=\"table table_no_border\" ><tr><td colspan=\"4\"><hr></td></tr>"
	        +"<tr><td colspan=\"2\" ><label>&nbsp;&nbsp; New Subsidiary/ Brand Contact Person</label></td><td colspan=\"2\">&nbsp;</td></tr>"
	        
	        +"<tr><td class=\"txtlabel alignRight\">First Name:"+hiddenSPOCFNameValidAsterix+"</td>"+
	        "<td><input type=\"text\" name=\"strClientBrandContactFName"+brandCnt+"\" id=\"strClientBrandContactFName"+brandCnt+"_"+cnt+"\" class=\""+hiddenSPOCFNameValidReqOpt+"\" /></td>" //</tr>
	        +"<td class=\"txtlabel alignRight\">Middle Name:"+hiddenSPOCMNameValidAsterix+"</td>"+ //<tr>
	        "<td><input type=\"text\" name=\"strClientBrandContactMName"+brandCnt+"\" id=\"strClientBrandContactMName"+brandCnt+"_"+cnt+"\" class=\""+hiddenSPOCMNameValidReqOpt+"\" /></td></tr>"
	        
	        +"<tr><td class=\"txtlabel alignRight\">Last Name:"+hiddenSPOCLNameValidAsterix+"</td>"+
	        "<td><input type=\"text\" name=\"strClientBrandContactLName"+brandCnt+"\" id=\"strClientBrandContactLName"+brandCnt+"_"+cnt+"\" class=\""+hiddenSPOCLNameValidReqOpt+"\" /></td>" //</tr>
	        +"<td class=\"txtlabel alignRight\">Contact No:"+hiddenSPOCContactNoValidAsterix+"</td>" //<tr>
	        +"<td><input type=\"text\" name=\"strClientBrandContactNo"+brandCnt+"\" id=\"strClientBrandContactNo"+brandCnt+"_"+cnt+"\" class=\""+hiddenSPOCContactNoValidAsterix+"\" /></td></tr>"
	        
	        +"<tr><td class=\"txtlabel alignRight\">Email Id:"+hiddenSPOCEmailIdValidAsterix+"</td>"
	        +"<td><input type=\"text\" name=\"strClientBrandContactEmail"+brandCnt+"\" id=\"strClientBrandContactEmail"+brandCnt+"_"+cnt+"\" class=\""+hiddenSPOCEmailIdValidReqOpt+"\" /></td>" //</tr>
	        +"<td class=\"txtlabel alignRight\">Designation:"+hiddenSPOCDesignationValidAsterix+"</td>" //<tr>
	        +"<td><input type=\"hidden\" name=\"strClientBrandContactDesig"+brandCnt+"\" id=\"strClientBrandContactDesigHidden"+brandCnt+"_"+cnt+"\" disabled=\"disabled\">"
	        +"<select name=\"strClientBrandContactDesig"+brandCnt+"\" id=\"strClientBrandContactDesig"+brandCnt+"_"+cnt+"\" class=\""+hiddenSPOCDesignationValidReqOpt+"\" ><option value=\"\">Select Designation</option>"+desigOpt+"</select>"
	        +"<span id=\"addBrandOtherDesigSpan"+brandCnt+"_"+cnt+"\"> <a href=\"javascript:void(0);\" onclick=\"addBrandOtherDesig('A', '"+brandCnt+"', '"+cnt+"');\">Other</a></span>"
			+"<span id=\"removeBrandOtherDesigSpan"+brandCnt+"_"+cnt+"\" style=\"display: none;\"> <a href=\"javascript:void(0);\" onclick=\"addBrandOtherDesig('R', '"+brandCnt+"', '"+cnt+"');\">Reset</a></span>"
			+"<div id=\"otherBrandDesigDiv"+brandCnt+"_"+cnt+"\" style=\"display: none; margin-top: 10px;\"><input type=\"text\" name=\"otherBrandDesignation"+brandCnt+"\" id=\"otherBrandDesignation"+brandCnt+"_"+cnt+"\" placeholder=\"Add New Designation\" onblur=\"checkBrandExistDesig('existBrandDesigDiv"+brandCnt+"_"+cnt+"', this.value, '"+brandCnt+"', '"+cnt+"');\" onchange=\"checkBrandExistDesig('existBrandDesigDiv"+brandCnt+"_"+cnt+"', this.value, '"+brandCnt+"', '"+cnt+"');\"/></div>"
			+"<div id=\"existBrandDesigDiv"+brandCnt+"_"+cnt+"\" style=\"font-size: 11px;\"></div>"
			+"</td>"
	        +"</tr>"
	        
	        +"<tr><td class=\"txtlabel alignRight\">Department:"+hiddenSPOCDepartmentValidAsterix+"</td>"
	        +"<td><input type=\"hidden\" name=\"strClientBrandContactDepartment"+brandCnt+"\" id=\"strClientBrandContactDepartmentHidden"+brandCnt+"_"+cnt+"\" disabled=\"disabled\">"
	        +"<select name=\"strClientBrandContactDepartment"+brandCnt+"\" id=\"strClientBrandContactDepartment"+brandCnt+"_"+cnt+"\" class=\""+hiddenSPOCDepartmentValidReqOpt+"\" ><option value=\"\">Select Department</option>"+departOpt+"</select>"
	        +"<span id=\"addBrandOtherDepartSpan"+brandCnt+"_"+cnt+"\"> <a href=\"javascript:void(0);\" onclick=\"addBrandOtherDepart('A', '"+brandCnt+"', '"+cnt+"');\">Other</a></span>"
			+"<span id=\"removeBrandOtherDepartSpan"+brandCnt+"_"+cnt+"\" style=\"display: none;\"> <a href=\"javascript:void(0);\" onclick=\"addBrandOtherDepart('R', '"+brandCnt+"', '"+cnt+"');\">Reset</a></span>"
			+"<div id=\"otherBrandDepartDiv"+brandCnt+"_"+cnt+"\" style=\"display: none; margin-top: 10px;\"><input type=\"text\" name=\"otherBrandDepartment"+brandCnt+"\" id=\"otherBrandDepartment"+brandCnt+"_"+cnt+"\" placeholder=\"Add New Department\" onblur=\"checkBrandExistDepart('existBrandDepartDiv"+brandCnt+"_"+cnt+"', this.value, '"+brandCnt+"', '"+cnt+"');\" onchange=\"checkBrandExistDepart('existBrandDepartDiv"+brandCnt+"_"+cnt+"', this.value, '"+brandCnt+"', '"+cnt+"');\"/></div>"
			+"<div id=\"existBrandDepartDiv"+brandCnt+"_"+cnt+"\" style=\"font-size: 11px;\"></div>"
			+"</td>"
	        //+"</tr>"
	        
	        +"<td class=\"txtlabel alignRight\">Location:"+hiddenSPOCLocationValidAsterix+"</td>" //<tr>
	        +"<td><input type=\"hidden\" name=\"strClientBrandContactLocation"+brandCnt+"\" id=\"strClientBrandContactLocationHidden"+brandCnt+"_"+cnt+"\" disabled=\"disabled\">"
	        +"<select name=\"strClientBrandContactLocation"+brandCnt+"\" id=\"strClientBrandContactLocation"+brandCnt+"_"+cnt+"\" class=\""+hiddenSPOCLocationValidReqOpt+"\" ><option value=\"\">Select Location</option>"+WLocOpt+"</select>"
	        +"<span id=\"addBrandOtherLocSpan"+brandCnt+"_"+cnt+"\"> <a href=\"javascript:void(0);\" onclick=\"addBrandOtherLoc('A', '"+brandCnt+"', '"+cnt+"');\">Other</a></span>"
			+"<span id=\"removeBrandOtherLocSpan"+brandCnt+"_"+cnt+"\" style=\"display: none;\"> <a href=\"javascript:void(0);\" onclick=\"addBrandOtherLoc('R', '"+brandCnt+"', '"+cnt+"');\">Reset</a></span>"
			+"<div id=\"otherBrandLocDiv"+brandCnt+"_"+cnt+"\" style=\"display: none; margin-top: 10px;\"><input type=\"text\" name=\"otherBrandLocation"+brandCnt+"\" id=\"otherBrandLocation"+brandCnt+"_"+cnt+"\" placeholder=\"Add New Location\" onblur=\"checkBrandExistLoc('existBrandLocDiv"+brandCnt+"_"+cnt+"', this.value, '"+brandCnt+"', '"+cnt+"');\" onchange=\"checkBrandExistLoc('existBrandLocDiv"+brandCnt+"_"+cnt+"', this.value, '"+brandCnt+"', '"+cnt+"');\"/></div>"
			+"<div id=\"existBrandLocDiv"+brandCnt+"_"+cnt+"\" style=\"font-size: 11px;\"></div>"
			+"</td>"
	        +"</tr>"
	       
	        +"<tr><td class=\"txtlabel alignRight\">Photo:</td>"
	        +"<td><img height=\"60\" width=\"60\" class=\"lazy\" id=\"strClientBrandContactPhotoImg"+brandCnt+"_"+cnt+"\" style=\"width: 1px solid #AAAAAA; border: 1px lightgray solid; margin-bottom: 2px;\" src=\"userImages/avatar_photo.png\" data-original=\"\" /> "
	        +"<input type=\"file\" name=\"strClientBrandContactPhoto"+brandCnt+"\" id=\"strClientBrandContactPhoto"+brandCnt+"_"+cnt+"\" onchange=\"readImageURL(this, 'strClientBrandContactPhotoImg"+brandCnt+"_"+cnt+"');\"/></td>"
	        +"<td class=\"alignRight\" colspan=\"2\" style=\"vertical-align: bottom; padding-right: 30px !important;\">"
	        +"<a href=\"javascript:void(0)\"  onclick=\"addNewBrandSPOC('"+brandCnt+"')\" class=\"fa fa-fw fa-plus\" title=\"Add Subsidiary/ Brand SPOC\">&nbsp;</a>"
	        +"<a href=\"javascript:void(0)\"  onclick=\"removeBrandSPOC('"+brandCnt+"', this.id)\" id=\""+cnt+"\" class=\"fa fa-fw fa-remove\" style=\"color: red;\" title=\"Remove Subsidiary/ Brand SPOC\">&nbsp;</a></td>"
	        +"</tr>"
	        
	        +"</table>";
	        //alert("divTag after ===>> "+ divTag);
	        
	    document.getElementById("hideBrandSpocCount"+brandCnt).value = cnt;
		document.getElementById("clientBrand"+brandCnt).appendChild(divTag);
	}


	function removeBrandSPOC(brandCnt, removeId) {
	    var remove_elem = "clientBrandSPOC"+brandCnt+"_"+removeId;
	    var row_skill = document.getElementById(remove_elem);
	    document.getElementById("clientBrand"+brandCnt).removeChild(row_skill);
	}
	
	

function readImageURL(input, targetDiv) {
    if (input.files && input.files[0]) {
        var reader = new FileReader();
        reader.onload = function (e) {
        $('#'+targetDiv).attr('src', e.target.result).width(60).height(60);
    	};
        reader.readAsDataURL(input.files[0]);
    }
}


	/* function readImageURL(input, targetDiv) {
	    if (input.files && input.files[0]) {
	        var reader = new FileReader();
	        reader.onload = function (e) {
	            $('#'+targetDiv).attr('src', e.target.result).width(60).height(60);
	        };
	        reader.readAsDataURL(input.files[0]);
	    }
	} */
 
 function getClient(divid, val) {
	var xmlhttp = GetXmlHttpObject();
	if (xmlhttp == null) {
		alert("Browser does not support HTTP Request");
		return;
	} else {
		var xhr = $.ajax({
			url : "GetClientName.action?strClientName="+ val,
			cache : false,
			success : function(data) {
				document.getElementById(divid).innerHTML=data;
				if(data.trim().length >1){
					document.getElementById("strOrganisationName").value='';
				}
			
			}
		});
	}
 }
 
	function addOtherIndustry(type) {
		 if(type == 'A') {
			 document.getElementById("removeOtherSpan").style.display = 'inline';
			 document.getElementById("otherDiv").style.display = 'block';
			 document.getElementById("addOtherSpan").style.display = 'none';
		 } else {
			 document.getElementById("addOtherSpan").style.display = 'inline';
			 document.getElementById("otherIndustry").value='';
			 document.getElementById("otherDiv").style.display = 'none';
			 document.getElementById("existIndustryDiv").innerHTML='';
			 document.getElementById("removeOtherSpan").style.display = 'none';
		 }
	}
	
	
	function addBrandOtherIndustry(type, brandCnt) {
		 if(type == 'A') {
			 document.getElementById("removeBrandOtherSpan"+brandCnt).style.display = 'inline';
			 document.getElementById("otherBrandDiv"+brandCnt).style.display = 'block';
			 document.getElementById("addBrandOtherSpan"+brandCnt).style.display = 'none';
		 } else {
			 document.getElementById("addBrandOtherSpan"+brandCnt).style.display = 'inline';
			 document.getElementById("otherBrandIndustry"+brandCnt).value='';
			 document.getElementById("otherBrandDiv"+brandCnt).style.display = 'none';
			 document.getElementById("existBrandIndustryDiv"+brandCnt).innerHTML='';
			 document.getElementById("removeBrandOtherSpan"+brandCnt).style.display = 'none';
		 }
	}
 
 
	function checkExistIndustry(divid, val) {
		var xmlhttp = GetXmlHttpObject();
		if (xmlhttp == null) {
			alert("Browser does not support HTTP Request");
			return;
		} else {
			var xhr = $.ajax({
				url : "GetClientName.action?strIndustryName="+ val,
				cache : false,
				success : function(data) {
					document.getElementById(divid).innerHTML=data;
					if(data.trim().length >1){
						document.getElementById("otherIndustry").value='';
					}
				
				}
			});
		}
	}
 
	
	function checkBrandExistIndustry(brandCnt, divid, val) {
		var xmlhttp = GetXmlHttpObject();
		if (xmlhttp == null) {
			alert("Browser does not support HTTP Request");
			return;
		} else {
			var xhr = $.ajax({
				url : "GetClientName.action?strIndustryName="+ val,
				cache : false,
				success : function(data) {
					document.getElementById(divid).innerHTML=data;
					if(data.trim().length >1){
						document.getElementById("otherBrandIndustry"+brandCnt).value='';
					}
				
				}
			});
		}
	}
	
 
	function addOtherDesig(type, cnt) {
		 if(type == 'A') {
			 document.getElementById("strClientContactDesig"+cnt).selectedIndex = '0';
			 document.getElementById("strClientContactDesig"+cnt).className = '';
			 document.getElementById("strClientContactDesig"+cnt).disabled = true;
			 document.getElementById("strClientContactDesigHidden"+cnt).disabled = false;
			 document.getElementById("removeOtherDesigSpan"+cnt).style.display = 'inline';
			 document.getElementById("otherDesigDiv"+cnt).style.display = 'block';
			 document.getElementById("addOtherDesigSpan"+cnt).style.display = 'none';
		 } else {
			 document.getElementById("addOtherDesigSpan"+cnt).style.display = 'inline';
			 document.getElementById("strClientContactDesig"+cnt).className = document.getElementById("hiddenSPOCDesignationValidReqOpt").value;
			 document.getElementById("strClientContactDesig"+cnt).disabled = false;
			 document.getElementById("strClientContactDesigHidden"+cnt).disabled = true;
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
	
	
	function addBrandOtherDesig(type, brandCnt, cnt) {
		 if(type == 'A') {
			 document.getElementById("strClientBrandContactDesig"+brandCnt+"_"+cnt).selectedIndex = '0';
			 document.getElementById("strClientBrandContactDesig"+brandCnt+"_"+cnt).className = '';
			 document.getElementById("strClientBrandContactDesig"+brandCnt+"_"+cnt).disabled = true;
			 document.getElementById("strClientBrandContactDesigHidden"+brandCnt+"_"+cnt).disabled = false;
			 document.getElementById("removeBrandOtherDesigSpan"+brandCnt+"_"+cnt).style.display = 'inline';
			 document.getElementById("otherBrandDesigDiv"+brandCnt+"_"+cnt).style.display = 'block';
			 document.getElementById("addBrandOtherDesigSpan"+brandCnt+"_"+cnt).style.display = 'none';
		 } else {
			 document.getElementById("addBrandOtherDesigSpan"+brandCnt+"_"+cnt).style.display = 'inline';
			 document.getElementById("strClientBrandContactDesig"+brandCnt+"_"+cnt).className = document.getElementById("hiddenSPOCDesignationValidReqOpt").value;
			 document.getElementById("strClientBrandContactDesig"+brandCnt+"_"+cnt).disabled = false;
			 document.getElementById("strClientBrandContactDesigHidden"+brandCnt+"_"+cnt).disabled = true;
			 document.getElementById("otherBrandDesignation"+brandCnt+"_"+cnt).value='';
			 document.getElementById("otherBrandDesigDiv"+brandCnt+"_"+cnt).style.display = 'none';
			 document.getElementById("existBrandDesigDiv"+brandCnt+"_"+cnt).innerHTML='';
			 document.getElementById("removeBrandOtherDesigSpan"+brandCnt+"_"+cnt).style.display = 'none';
		 }
	}
	
	
	function checkBrandExistDesig(divid, val, brandCnt, cnt) {
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
						document.getElementById("otherBrandDesignation"+brandCnt+'_'+cnt).value='';
					}
				
				}
			});
		}
	}
	
	
	function addOtherDepart(type, cnt) {
		 if(type == 'A') {
			 document.getElementById("strClientContactDepartment"+cnt).selectedIndex = '0';
			 document.getElementById("strClientContactDepartment"+cnt).className = '';
			 document.getElementById("strClientContactDepartment"+cnt).disabled = true;
			 document.getElementById("strClientContactDepartmentHidden"+cnt).disabled = false;
			 document.getElementById("removeOtherDepartSpan"+cnt).style.display = 'inline';
			 document.getElementById("otherDepartDiv"+cnt).style.display = 'block';
			 document.getElementById("addOtherDepartSpan"+cnt).style.display = 'none';
		 } else {
			 document.getElementById("addOtherDepartSpan"+cnt).style.display = 'inline';
			 document.getElementById("strClientContactDepartment"+cnt).className = document.getElementById("hiddenSPOCDepartmentValidReqOpt").value;
			 document.getElementById("strClientContactDepartment"+cnt).disabled = false;
			 document.getElementById("strClientContactDepartmentHidden"+cnt).disabled = true;
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
	
	
	function addBrandOtherDepart(type, brandCnt, cnt) {
		 if(type == 'A') {
			 document.getElementById("strClientBrandContactDepartment"+brandCnt+"_"+cnt).selectedIndex = '0';
			 document.getElementById("strClientBrandContactDepartment"+brandCnt+"_"+cnt).className = '';
			 document.getElementById("strClientBrandContactDepartment"+brandCnt+"_"+cnt).disabled = true;
			 document.getElementById("strClientBrandContactDepartmentHidden"+brandCnt+"_"+cnt).disabled = false;
			 document.getElementById("removeBrandOtherDepartSpan"+brandCnt+"_"+cnt).style.display = 'inline';
			 document.getElementById("otherBrandDepartDiv"+brandCnt+"_"+cnt).style.display = 'block';
			 document.getElementById("addBrandOtherDepartSpan"+brandCnt+"_"+cnt).style.display = 'none';
		 } else {
			 document.getElementById("addBrandOtherDepartSpan"+brandCnt+"_"+cnt).style.display = 'inline';
			 document.getElementById("strClientBrandContactDepartment"+brandCnt+"_"+cnt).className = document.getElementById("hiddenSPOCDepartmentValidReqOpt").value;
			 document.getElementById("strClientBrandContactDepartment"+brandCnt+"_"+cnt).disabled = false;
			 document.getElementById("strClientBrandContactDepartmentHidden"+brandCnt+"_"+cnt).disabled = true;
			 document.getElementById("otherBrandDepartment"+brandCnt+"_"+cnt).value='';
			 document.getElementById("otherBrandDepartDiv"+brandCnt+"_"+cnt).style.display = 'none';
			 document.getElementById("existBrandDepartDiv"+brandCnt+"_"+cnt).innerHTML='';
			 document.getElementById("removeBrandOtherDepartSpan"+brandCnt+"_"+cnt).style.display = 'none';
		 }
	}
	
	
	function checkBrandExistDepart(divid, val, brandCnt, cnt) {
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
						document.getElementById("otherBrandDepartment"+brandCnt+'_'+cnt).value='';
					}
				}
			});
		}
	}
	
	function addOtherLoc(type, cnt) {
		 if(type == 'A') {
			 document.getElementById("strClientContactLocation"+cnt).selectedIndex = '0';
			 document.getElementById("strClientContactLocation"+cnt).className = '';
			 document.getElementById("strClientContactLocation"+cnt).disabled = true;
			 document.getElementById("strClientContactLocationHidden"+cnt).disabled = false;
			 document.getElementById("removeOtherLocSpan"+cnt).style.display = 'inline';
			 document.getElementById("otherLocDiv"+cnt).style.display = 'block';
			 document.getElementById("addOtherLocSpan"+cnt).style.display = 'none';
		 } else {
			 document.getElementById("addOtherLocSpan"+cnt).style.display = 'inline';
			 document.getElementById("strClientContactLocation"+cnt).className = document.getElementById("hiddenSPOCLocationValidReqOpt").value;
			 document.getElementById("strClientContactLocation"+cnt).disabled = false;
			 document.getElementById("strClientContactLocationHidden"+cnt).disabled = true;
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
	
	
	function addBrandOtherLoc(type, brandCnt, cnt) {
		 if(type == 'A') {
			 document.getElementById("strClientBrandContactLocation"+brandCnt+"_"+cnt).selectedIndex = '0';
			 document.getElementById("strClientBrandContactLocation"+brandCnt+"_"+cnt).className = '';
			 document.getElementById("strClientBrandContactLocation"+brandCnt+"_"+cnt).disabled = true;
			 document.getElementById("strClientBrandContactLocationHidden"+brandCnt+"_"+cnt).disabled = false;
			 document.getElementById("removeBrandOtherLocSpan"+brandCnt+"_"+cnt).style.display = 'inline';
			 document.getElementById("otherBrandLocDiv"+brandCnt+"_"+cnt).style.display = 'block';
			 document.getElementById("addBrandOtherLocSpan"+brandCnt+"_"+cnt).style.display = 'none';
		 } else {
			 document.getElementById("addBrandOtherLocSpan"+brandCnt+"_"+cnt).style.display = 'inline';
			 document.getElementById("strClientBrandContactLocation"+brandCnt+"_"+cnt).className = document.getElementById("hiddenSPOCLocationValidReqOpt").value;
			 document.getElementById("strClientBrandContactLocation"+brandCnt+"_"+cnt).disabled = false;
			 document.getElementById("strClientBrandContactLocationHidden"+brandCnt+"_"+cnt).disabled = true;
			 document.getElementById("otherBrandLocation"+brandCnt+"_"+cnt).value='';
			 document.getElementById("otherBrandLocDiv"+brandCnt+"_"+cnt).style.display = 'none';
			 document.getElementById("existBrandLocDiv"+brandCnt+"_"+cnt).innerHTML='';
			 document.getElementById("removeBrandOtherLocSpan"+brandCnt+"_"+cnt).style.display = 'none';
		 }
	}
	
	
	function checkBrandExistLoc(divid, val, brandCnt, cnt) {
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
						document.getElementById("otherBrandLocation"+brandCnt+'_'+cnt).value='';
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
 
 	jQuery(document).ready(function() {
		$("#clientIndustry").multiselect().multiselectfilter();
	});
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
      <div class="box-header with-border" style="border-bottom: 2px solid lightgray;">
        <h3 class="box-title"><b><%=uF.showData((String)request.getAttribute("strOrganisationName"), "New Client") %></b></h3>
        <div class="box-tools pull-right">
          <!-- <button data-widget="collapse" class="btn btn-box-tool"><i class="fa fa-minus"></i></button>
          <button data-widget="remove" class="btn btn-box-tool"><i class="fa fa-times"></i></button> -->
        </div>
      </div><!-- /.box-header -->
      <div class="box-body table-responsive no-padding">
		<s:form theme="simple" action="AddClient" name="formAddNewRow" id="formAddNewRow" method="POST" enctype="multipart/form-data">
			<s:hidden name="strClientId" />
			<s:hidden name="fromPage" id="fromPage" />
			<s:hidden name="proId" id="proId1" />
			<s:hidden name="clientID" id="clientID" />
			<div id="addSPOCDiv">

			<table border="0" class="table table_no_border">
				<tr><td colspan=2><s:fielderror/></td></tr>
				
				<tr><td class="txtlabel alignRight" valign="top">Organisation:<sup>*</sup></td>
				<td><s:select theme="simple" name="organisation" id="organisation" cssClass="validateRequired" listKey="orgId"
							listValue="orgName" list="organisationList" key="" /> <!-- onchange="getLocationOrganization(this.value);"  -->
					</td>
				<!-- </tr>
				
				<tr> -->
				<% 	List<String> orgValidList = hmValidationFields.get("COMPANY_NAME");
					validReqOpt = "";
					validAsterix = "";
					if(uF.parseToBoolean(orgValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
					<td class="txtlabel alignRight" valign="top">Company Name:<%=validAsterix %></td>
					<td>
					<input type="text" class="<%=validReqOpt %>" name="strOrganisationName" id="strOrganisationName" value="<%=uF.showData((String)request.getAttribute("strOrganisationName"), "") %>"
					 onchange ="getClient('myDiv', this.value)"/>
					<span class="hint">Add the organisation name. This will be displayed in time-sheets and clock entries.<span class="hint-pointer">&nbsp;</span></span>
					<div id="myDiv" style="font-size: 11px;"></div>
					</td>
				</tr>
				
				<tr>
					<td class="txtlabel alignRight">Company Logo:<br/></td>
					<td>
					<%if(docRetriveLocation==null) { %>
						<img height="60" width="60" class="lazy" id="companyLogoImg" style="border: 1px solid #CCCCCC; border: 1px lightgray solid; margin-bottom: 2px;" src="userImages/company_avatar_photo.png" data-original="<%=IConstants.IMAGE_LOCATION + ((strImage!=null && !strImage.toString().equals("")) ? strImage:"company_avatar_photo.png") %>" />
					<%} else { 
					String customerImage = IConstants.IMAGE_LOCATION + ((strImage!=null && !strImage.equals("")) ? strImage:"company_avatar_photo.png");
						if(strImage!=null && !strImage.equals("")) {
							customerImage = docRetriveLocation +IConstants.I_CUSTOMER+"/"+strClientId+"/"+IConstants.I_IMAGE+"/"+((strImage!=null && !strImage.equals("")) ? strImage:"company_avatar_photo.png");
						}
					%>
                     	<img height="60" width="60" class="lazy" id="companyLogoImg" style="border: 1px solid #CCCCCC; margin-bottom: 2px;" src="<%=customerImage %>" data-original="<%=customerImage %>" />
                    <%} %>
                    
						<s:file name="companyLogo" id="companyLogo" onchange="readImageURL(this, 'companyLogoImg');"></s:file>
					</td>
				<!-- </tr>
				 
				<tr> -->
				<% 	List<String> addValidList = hmValidationFields.get("COMPANY_ADDRESS"); 
					validReqOpt = "";
					validAsterix = "";
					if(uF.parseToBoolean(addValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
					<td class="txtlabel alignRight" valign="top">Address:<%=validAsterix %><br/></td>
					<td> <textarea name="strClientAddress" id="strClientAddress" class="<%=validReqOpt %>" rows="4" cols="22"><%=uF.showData((String)request.getAttribute("strClientAddress"), "") %></textarea>
					</td>
				</tr>
				
				<tr>
				<% 	List<String> cityValidList = hmValidationFields.get("COMPANY_CITY"); 
					validReqOpt = "";
					validAsterix = "";
					if(uF.parseToBoolean(cityValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
					<td class="txtlabel alignRight">City:<%=validAsterix %><br/></td>
					<td>
					<input type="text" name="strClientCity" id="strClientCity" class="<%=validReqOpt %>" value="<%=uF.showData((String)request.getAttribute("strClientCity"), "") %>">
					</td> 
				<!-- </tr>
				<tr> -->
				<% 	List<String> countryValidList = hmValidationFields.get("COMPANY_CONTRY"); 
					validReqOpt = "";
					validAsterix = "";
					if(uF.parseToBoolean(countryValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
				
				
				<td class="txtlabel alignRight">Country:<%=validAsterix %></td>
				<td>
				<% if(validReqOpt != null && !validReqOpt.equals("")) { %>
					<s:select id="strClientCountry" name="strClientCountry" listKey="countryId" listValue="countryName" cssClass="validateRequired" headerKey="" 
						headerValue="Select Country" onchange="getContentAcs('stateTD','GetStates.action?country='+this.value+'&type=CLIENTADDSTATE&validReq=1');" list="countryList" key="" required="true" />
				<% } else { %>
					<s:select id="strClientCountry" name="strClientCountry" listKey="countryId" listValue="countryName" headerKey="" 
						headerValue="Select Country" onchange="getContentAcs('stateTD','GetStates.action?country='+this.value+'&type=CLIENTADDSTATE');" list="countryList" key="" required="true" />
				<% } %>		
				</td>
				</tr>

				<tr>
				<% 	List<String> stateValidList = hmValidationFields.get("COMPANY_STATE"); 
					validReqOpt = "";
					validAsterix = "";
					if(uF.parseToBoolean(stateValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					} 
				%>
				
				<td class="txtlabel alignRight">State:<%=validAsterix %>
					<input type="hidden" name="hiddenStateValidReqOpt" id="hiddenStateValidReqOpt" value="<%=validReqOpt %>"/>
				</td><td id="stateTD">
				<% if(validReqOpt != null && !validReqOpt.equals("")) { %>
					<s:select theme="simple" title="state" cssClass="validateRequired" id="strClientState" name="strClientState" listKey="stateId" 
						listValue="stateName" headerKey="" headerValue="Select State" list="stateList" key="" required="true" /> 
				<% } else { %>
					<s:select theme="simple" title="state" id="strClientState" name="strClientState" listKey="stateId" listValue="stateName" 
						headerKey="" headerValue="Select State" list="stateList" key="" /> 
				<% } %>		
				</td>
				<!-- </tr>
				
				<tr> -->
				<% 	List<String> pinCodeValidList = hmValidationFields.get("COMPANY_PIN_CODE"); 
					validReqOpt = "";
					validAsterix = "";
					if(uF.parseToBoolean(pinCodeValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
					<td class="txtlabel alignRight">Pin Code:<%=validAsterix %><br/></td>
					<td><input type="text" name="strPinCode" id="strPinCode" class="<%=validReqOpt %>" value="<%=uF.showData((String)request.getAttribute("strPinCode"), "") %>"/>
					</td> 
				</tr>
				
				<tr>
				<% 	List<String> industryValidList = hmValidationFields.get("COMPANY_INDUSTRY"); 
					validReqOpt = "";
					validAsterix = "";
					if(uF.parseToBoolean(industryValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
					<td class="txtlabel alignRight">Industry:<%=validAsterix %><br/></td>
					<td>
					<% if(uF.parseToBoolean(industryValidList.get(0))) { %>
						<s:select label="Select Industry" name="clientIndustry" id="clientIndustry" listKey="industryId" cssClass="validateRequired"
							listValue="industryName" multiple="true" size="3" list="clientIndustryList" key="" />
					<% } else { %>
						<s:select label="Select Industry" name="clientIndustry" id="clientIndustry" listKey="industryId" 
							listValue="industryName" multiple="true" size="3" list="clientIndustryList" key="" />
					<% } %> <span id="addOtherSpan"> <a href="javascript:void(0);" onclick="addOtherIndustry('A');">Other</a></span>
							<span id="removeOtherSpan" style="display: none;"> <a href="javascript:void(0);" onclick="addOtherIndustry('R');">Reset</a></span>
							<div id="otherDiv" style="display: none; margin-top: 10px;"><input type="text" name="otherIndustry" id="otherIndustry" placeholder="Add New Industry" onblur="checkExistIndustry('existIndustryDiv', this.value);" onchange="checkExistIndustry('existIndustryDiv', this.value);"/></div>
							<div id="existIndustryDiv" style="font-size: 11px;"></div>
					</td>
				<!-- </tr>
				
				<tr> -->
					<% 	List<String> websiteValidList = hmValidationFields.get("COMPANY_WEBSITE"); 
						validReqOpt = "";
						validAsterix = "";
						if(uF.parseToBoolean(websiteValidList.get(0))) {
							validReqOpt = "validateRequired";
							validAsterix = "<sup>*</sup>";
						}
					%>
					<td class="txtlabel alignRight">Website:<%=validAsterix %><br/></td>
					<td><input type="text" name="companyWebsite" id="companyWebsite" class="<%=validReqOpt %>" value="<%=uF.showData((String)request.getAttribute("companyWebsite"), "") %>"/></td>
				</tr>
				
				<tr>
				<% 	List<String> compDescValidList = hmValidationFields.get("COMPANY_DESCRIPTION"); 
					validReqOpt = "";
					validAsterix = "";
					if(uF.parseToBoolean(compDescValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
					<td class="txtlabel alignRight">Description:<%=validAsterix %><br/></td>
					<td colspan="3">
						<textarea name="companyDescription" id="companyDescription" class="<%=validReqOpt %>" rows="4" cols="22" style="width: 90% !important;"><%=uF.showData((String)request.getAttribute("companyDescription"), "") %></textarea>
					</td> 
				 
				</tr>
				
				<tr><td colspan="4"><hr></td></tr>
				
				<tr>
					<td colspan="4">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Statutory Compliance Details</td>
				</tr>	
				
				<tr>
				<% 	List<String> tdsValidList = hmValidationFields.get("COMPANY_TDS"); 
					validReqOpt = "";
					validAsterix = "";
					if(uF.parseToBoolean(tdsValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
					<td class="txtlabel alignRight">GST:<%=validAsterix %><br/></td>
					<td> <input type="text" name="clientTds" id="clientTds" class="<%=validReqOpt %>" value="<%=uF.showData((String)request.getAttribute("clientTds"), "") %>"/> % </td> 
				<!-- </tr>
				
				<tr> -->
				<% 	List<String> regNoValidList = hmValidationFields.get("COMPANY_REG_NO"); 
					validReqOpt = "";
					validAsterix = "";
					if(uF.parseToBoolean(regNoValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
					<td class="txtlabel alignRight">GSTIN No.:<%=validAsterix %><br/></td>
					<td><input type="text" name="clientRegistrationNo" id="clientRegistrationNo" class="<%=validReqOpt %>" value="<%=uF.showData((String)request.getAttribute("clientRegistrationNo"), "") %>"/> </td> 
				</tr>
			</table>
		
			<% List<List<String>> alSpocData = (List<List<String>>) request.getAttribute("alSpocData");
				//System.out.println("alSpocData ===>> " + alSpocData);
				if(alSpocData != null && !alSpocData.isEmpty()) {
					for(int i=0; i<alSpocData.size(); i++) {
						List<String> innerList = alSpocData.get(i);
			%>
					<div id="clientSPOC<%=i %>" style="float: left; width: 100%;">
						<table class="table table_no_border">
							<tr><td colspan="4"><hr></td></tr> <!-- <hr style="height: 1px; width: 100%; margin: 0px; background-color: gray;"> -->
			        		<tr>
			        			<td colspan="2"><label>&nbsp;&nbsp;Add New Contact Person</label></td><td colspan="2">&nbsp;</td>
			        		</tr>
			        		<tr>
			        		<% 	List<String> spocFNameValidList = hmValidationFields.get("COMPANY_CONTACT_FIRST_NAME"); 
								validReqOpt = "";
								validAsterix = "";
								if(uF.parseToBoolean(spocFNameValidList.get(0))) {
									validReqOpt = "validateRequired";
									validAsterix = "<sup>*</sup>";
								}
							%>
			        			<td class="txtlabel alignRight">First Name:<%=validAsterix %>
			        				<input type="hidden" name="strClientContactId" value= "<%=innerList.get(0) %>"/>
				        			<input type="hidden" name="hiddenSPOCFNameValidAsterix" id="hiddenSPOCFNameValidAsterix" value="<%=validAsterix %>"/>
				        			<input type="hidden" name="hiddenSPOCFNameValidReqOpt" id="hiddenSPOCFNameValidReqOpt" value="<%=validReqOpt %>"/>
			        			</td>
			        			<td><input type="text" name="strClientContactFName" id="strClientContactFName<%=i %>" class="<%=validReqOpt %>" value="<%=innerList.get(1) %>"/></td>
			        		<!-- </tr>
			        		<tr> -->
			        		<% 	List<String> spocMNameValidList = hmValidationFields.get("COMPANY_CONTACT_MIDDLE_NAME"); 
								validReqOpt = "";
								validAsterix = "";
								if(uF.parseToBoolean(spocMNameValidList.get(0))) {
									validReqOpt = "validateRequired";
									validAsterix = "<sup>*</sup>";
								}
							%>
			        			<td class="txtlabel alignRight">Middle Name:<%=validAsterix %>
				        			<input type="hidden" name="hiddenSPOCMNameValidAsterix" id="hiddenSPOCMNameValidAsterix" value="<%=validAsterix %>"/>
				        			<input type="hidden" name="hiddenSPOCMNameValidReqOpt" id="hiddenSPOCMNameValidReqOpt" value="<%=validReqOpt %>"/>
			        			</td>
			        			<td><input type="text" name="strClientContactMName" id="strClientContactMName<%=i %>" class="<%=validReqOpt %>" value="<%=innerList.get(2) %>"/></td>
			        		</tr>
			        		<tr>
			        		<% 	List<String> spocLNameValidList = hmValidationFields.get("COMPANY_CONTACT_LAST_NAME"); 
								validReqOpt = "";
								validAsterix = "";
								if(uF.parseToBoolean(spocLNameValidList.get(0))) {
									validReqOpt = "validateRequired";
									validAsterix = "<sup>*</sup>";
								}
							%>
			        			<td class="txtlabel alignRight">Last Name:<%=validAsterix %>
			        				<input type="hidden" name="hiddenSPOCLNameValidAsterix" id="hiddenSPOCLNameValidAsterix" value="<%=validAsterix %>"/>
				        			<input type="hidden" name="hiddenSPOCLNameValidReqOpt" id="hiddenSPOCLNameValidReqOpt" value="<%=validReqOpt %>"/>
			        			</td>
			        			<td><input type="text" name="strClientContactLName" id="strClientContactLName<%=i %>" class="<%=validReqOpt %>" value="<%=innerList.get(3) %>"/></td>
			        		<!-- </tr>
			        
			        		<tr> -->
			        		<% 	List<String> spocContactNoValidList = hmValidationFields.get("COMPANY_CONTACT_CONTACT_NO"); 
								validReqOpt = "";
								validAsterix = "";
								if(uF.parseToBoolean(spocContactNoValidList.get(0))) {
									validReqOpt = "validateRequired";
									validAsterix = "<sup>*</sup>";
								}
							%>
			        			<td class="txtlabel alignRight">Contact No:<%=validAsterix %>
			        				<input type="hidden" name="hiddenSPOCContactNoValidAsterix" id="hiddenSPOCContactNoValidAsterix" value="<%=validAsterix %>"/>
				        			<input type="hidden" name="hiddenSPOCContactNoValidReqOpt" id="hiddenSPOCContactNoValidReqOpt" value="<%=validReqOpt %>"/>
			        			</td>
			        			<td><input type="text" name="strClientContactNo" id="strClientContactNo<%=i %>" class="<%=validReqOpt %>" value="<%=innerList.get(4) %>"/></td>
			        		</tr>
			        
			        		<tr>
			        		<% 	List<String> spocEmailIdValidList = hmValidationFields.get("COMPANY_CONTACT_MAIL_ID"); 
								validReqOpt = "validateEmail";
								validAsterix = "";
								if(uF.parseToBoolean(spocEmailIdValidList.get(0))) {
									validReqOpt = "validateRequired validateEmail";
									validAsterix = "<sup>*</sup>";
								}
							%>
			        			<td class="txtlabel alignRight">Email Id:<%=validAsterix %>
			        				<input type="hidden" name="hiddenSPOCEmailIdValidAsterix" id="hiddenSPOCEmailIdValidAsterix" value="<%=validAsterix %>"/>
				        			<input type="hidden" name="hiddenSPOCEmailIdValidReqOpt" id="hiddenSPOCEmailIdValidReqOpt" value="<%=validReqOpt %>"/>
			        			</td>
			        			<td><input type="text" name="strClientContactEmail" id="strClientContactEmail<%=i %>" class="<%=validReqOpt %>" value="<%=innerList.get(5) %>"/></td>
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
			        				<input type="hidden" name="strClientContactDesig" id="strClientContactDesigHidden<%=i %>" disabled="disabled">
			        				<select name="strClientContactDesig" id="strClientContactDesig<%=i %>" class="<%=validReqOpt %>" >
				        				<option value="">Select Designation</option>
				        				<%=innerList.get(6) %>
			        				</select>
			        				<span id="addOtherDesigSpan<%=i %>"> <a href="javascript:void(0);" onclick="addOtherDesig('A', '<%=i %>');">Other</a></span>
									<span id="removeOtherDesigSpan<%=i %>" style="display: none;"> <a href="javascript:void(0);" onclick="addOtherDesig('R', '<%=i %>');">Reset</a></span>
									<div id="otherDesigDiv<%=i %>" style="display: none; margin-top: 10px;"><input type="text" name="otherDesignation" id="otherDesignation<%=i %>" placeholder="Add New Designation" onblur="checkExistDesig('existDesigDiv<%=i %>', this.value, '<%=i %>');" onchange="checkExistDesig('existDesigDiv<%=i %>', this.value, '<%=i %>');"/></div>
									<div id="existDesigDiv<%=i %>" style="font-size: 11px;"></div>
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
			        				<input type="hidden" name="strClientContactDepartment" id="strClientContactDepartmentHidden<%=i %>" disabled="disabled">
			        				<select name="strClientContactDepartment" id="strClientContactDepartment<%=i %>" class="<%=validReqOpt %>" >
				        				<option value="">Select Department</option>
				        				<%=innerList.get(7) %>
			        				</select>
			        				<span id="addOtherDepartSpan<%=i %>"> <a href="javascript:void(0);" onclick="addOtherDepart('A', '<%=i %>');">Other</a></span>
									<span id="removeOtherDepartSpan<%=i %>" style="display: none;"> <a href="javascript:void(0);" onclick="addOtherDepart('R', '<%=i %>');">Reset</a></span>
									<div id="otherDepartDiv<%=i %>" style="display: none; margin-top: 10px;"><input type="text" name="otherDepartment" id="otherDepartment<%=i %>" placeholder="Add New Department" onblur="checkExistDepart('existDepartDiv<%=i %>', this.value, '<%=i %>');" onchange="checkExistDepart('existDepartDiv<%=i %>', this.value, '<%=i %>');"/></div>
									<div id="existDepartDiv<%=i %>" style="font-size: 11px;"></div>
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
			        				<input type="hidden" name="strClientContactLocation" id="strClientContactLocationHidden<%=i %>" disabled="disabled">
			        				<select name="strClientContactLocation" id="strClientContactLocation<%=i %>" class="<%=validReqOpt %>" >
				        				<option value="">Select Location</option>
				        				<%=innerList.get(8) %>
			        				</select>
			        				<span id="addOtherLocSpan<%=i %>"> <a href="javascript:void(0);" onclick="addOtherLoc('A', '<%=i %>');">Other</a></span>
									<span id="removeOtherLocSpan<%=i %>" style="display: none;"> <a href="javascript:void(0);" onclick="addOtherLoc('R', '<%=i %>');">Reset</a></span>
									<div id="otherLocDiv<%=i %>" style="display: none; margin-top: 10px;"><input type="text" name="otherLocation" id="otherLocation<%=i %>" placeholder="Add New Location" onblur="checkExistLoc('existLocDiv<%=i %>', this.value, '<%=i %>');" onchange="checkExistLoc('existLocDiv<%=i %>', this.value, '<%=i %>');"/></div>
									<div id="existLocDiv<%=i %>" style="font-size: 11px;"></div>
			        			</td>
			        		</tr>
			       
			        		<tr>
			        			<td class="txtlabel alignRight">Photo:</td>
			        			<td><img height="60" width="60" class="lazy" id="strClientContactPhotoImg<%=i %>" style="width: 1px solid #AAAAAA; border: 1px lightgray solid; margin-bottom: 2px;" src="<%=innerList.get(9) %>" data-original="<%=innerList.get(9) %>" />
			        				<input type="file" name="strClientContactPhoto" id="strClientContactPhoto<%=i %>" onchange="readImageURL(this, 'strClientContactPhotoImg<%=i %>');"/>
			        			</td>
			        			<td class="alignRight" colspan="2" style="vertical-align: bottom;padding-right: 30px !important;"><a href="javascript:void(0)"  onclick="addNewSPOC()" class="fa fa-fw fa-plus" title="Add SPOC">&nbsp;</a>
			        				<% if(i>0) { %>
			        					<a href="javascript:void(0)"  onclick="removeSPOC(this.id)" id="<%=i %>" class="fa fa-fw fa-remove" style="color: red;" title="Remove SPOC">&nbsp;</a>
			        				<% } %>
			        			</td>
			        		</tr>
			        	</table>
					</div>
				<% } %>
			<% } else { %>
				<div id="clientSPOC0" style="float: left; width: 100%;">
					<table class="table table_no_border">
						<tr><td colspan="4"><hr></td></tr>
		        		<tr>
		        			<td colspan="2"><label>&nbsp;&nbsp;Add New Contact Person</label></td><td colspan="2">&nbsp;</td>
		        		</tr>
		        		<tr>
		        		<% 	List<String> spocFNameValidList = hmValidationFields.get("COMPANY_CONTACT_FIRST_NAME"); 
							validReqOpt = "";
							validAsterix = "";
							if(uF.parseToBoolean(spocFNameValidList.get(0))) {
								validReqOpt = "validateRequired";
								validAsterix = "<sup>*</sup>";
							}
						%>
		        			<td class="txtlabel alignRight">First Name:<%=validAsterix %>
		        				<input type="hidden" name="hiddenSPOCFNameValidAsterix" id="hiddenSPOCFNameValidAsterix" value="<%=validAsterix %>"/>
				        		<input type="hidden" name="hiddenSPOCFNameValidReqOpt" id="hiddenSPOCFNameValidReqOpt" value="<%=validReqOpt %>"/>
		        			</td>
		        			<td><input type="text" name="strClientContactFName" id="strClientContactFName0" class="<%=validReqOpt %>"/></td>
		        		<!-- </tr>
		        		<tr> -->
		        		<% 	List<String> spocMNameValidList = hmValidationFields.get("COMPANY_CONTACT_MIDDLE_NAME"); 
							validReqOpt = "";
							validAsterix = "";
							if(uF.parseToBoolean(spocMNameValidList.get(0))) {
								validReqOpt = "validateRequired";
								validAsterix = "<sup>*</sup>";
							}
						%>
		        			<td class="txtlabel alignRight">Middle Name:<%=validAsterix %>
			        			<input type="hidden" name="hiddenSPOCMNameValidAsterix" id="hiddenSPOCMNameValidAsterix" value="<%=validAsterix %>"/>
				        		<input type="hidden" name="hiddenSPOCMNameValidReqOpt" id="hiddenSPOCMNameValidReqOpt" value="<%=validReqOpt %>"/>
		        			</td>
		        			<td><input type="text" name="strClientContactMName" id="strClientContactMName0" class="<%=validReqOpt %>"/></td>
		        		</tr>
		        		<tr>
		        		<% 	List<String> spocLNameValidList = hmValidationFields.get("COMPANY_CONTACT_LAST_NAME"); 
							validReqOpt = "";
							validAsterix = "";
							if(uF.parseToBoolean(spocLNameValidList.get(0))) {
								validReqOpt = "validateRequired";
								validAsterix = "<sup>*</sup>";
							}
						%>
		        			<td class="txtlabel alignRight">Last Name:<%=validAsterix %>
		        				<input type="hidden" name="hiddenSPOCLNameValidAsterix" id="hiddenSPOCLNameValidAsterix" value="<%=validAsterix %>"/>
				        		<input type="hidden" name="hiddenSPOCLNameValidReqOpt" id="hiddenSPOCLNameValidReqOpt" value="<%=validReqOpt %>"/>
		        			</td>
		        			<td><input type="text" name="strClientContactLName" id="strClientContactLName0" class="<%=validReqOpt %>"/></td>
		        		<!-- </tr>
		        
		        		<tr> -->
		        		<% 	List<String> spocContactNoValidList = hmValidationFields.get("COMPANY_CONTACT_CONTACT_NO"); 
							validReqOpt = "";
							validAsterix = "";
							if(uF.parseToBoolean(spocContactNoValidList.get(0))) {
								validReqOpt = "validateRequired";
								validAsterix = "<sup>*</sup>";
							}
						%>
		        			<td class="txtlabel alignRight">Contact No:<%=validAsterix %>
		        				<input type="hidden" name="hiddenSPOCContactNoValidAsterix" id="hiddenSPOCContactNoValidAsterix" value="<%=validAsterix %>"/>
				        		<input type="hidden" name="hiddenSPOCContactNoValidReqOpt" id="hiddenSPOCContactNoValidReqOpt" value="<%=validReqOpt %>"/>
		        			</td>
		        			<td><input type="text" name="strClientContactNo" id="strClientContactNo0" class="<%=validReqOpt %>"/></td>
		        		</tr>
		        
		        		<tr>
		        		<% 	List<String> spocEmailIdValidList = hmValidationFields.get("COMPANY_CONTACT_MAIL_ID"); 
							validReqOpt = "validateEmail";
							validAsterix = "";
							if(uF.parseToBoolean(spocEmailIdValidList.get(0))) {
								validReqOpt = "validateRequired validateEmail";
								validAsterix = "<sup>*</sup>";
							}
						%>
		        			<td class="txtlabel alignRight">Email Id:<%=validAsterix %>
		        				<input type="hidden" name="hiddenSPOCEmailIdValidAsterix" id="hiddenSPOCEmailIdValidAsterix" value="<%=validAsterix %>"/>
				        		<input type="hidden" name="hiddenSPOCEmailIdValidReqOpt" id="hiddenSPOCEmailIdValidReqOpt" value="<%=validReqOpt %>"/>
		        			</td>
		        			<td><input type="text" name="strClientContactEmail" id="strClientContactEmail0" class="<%=validReqOpt %>"/></td>
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
		        				<input type="hidden" name="strClientContactDesig" id="strClientContactDesigHidden0" disabled="disabled">
		        				<select name="strClientContactDesig" id="strClientContactDesig0" class="<%=validReqOpt %>">
			        				<option value="">Select Designation</option>
			        				<%=(String)request.getAttribute("sbDesig") %>
		        				</select>
		        				<span id="addOtherDesigSpan0"> <a href="javascript:void(0);" onclick="addOtherDesig('A', '0');">Other</a></span>
								<span id="removeOtherDesigSpan0" style="display: none;"> <a href="javascript:void(0);" onclick="addOtherDesig('R', '0');">Reset</a></span>
								<div id="otherDesigDiv0" style="display: none; margin-top: 10px;"><input type="text" name="otherDesignation" id="otherDesignation0" placeholder="Add New Designation" onblur="checkExistDesig('existDesigDiv0', this.value, '0');" onchange="checkExistDesig('existDesigDiv0', this.value, '0');"/></div>
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
		        				<input type="hidden" name="strClientContactDepartment" id="strClientContactDepartmentHidden0" disabled="disabled">
		        				<select name="strClientContactDepartment" id="strClientContactDepartment0" class="<%=validReqOpt %>">
			        				<option value="">Select Department</option>
			        				<%=(String)request.getAttribute("sbDepart") %>
		        				</select>
		        				<span id="addOtherDepartSpan0"> <a href="javascript:void(0);" onclick="addOtherDepart('A', '0');">Other</a></span>
								<span id="removeOtherDepartSpan0" style="display: none;"> <a href="javascript:void(0);" onclick="addOtherDepart('R', '0');">Reset</a></span>
								<div id="otherDepartDiv0" style="display: none; margin-top: 10px;"><input type="text" name="otherDepartment" id="otherDepartment0" placeholder="Add New Department" onblur="checkExistDepart('existDepartDiv0', this.value, '0');" onchange="checkExistDepart('existDepartDiv0', this.value, '0');"/></div>
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
		        				<input type="hidden" name="strClientContactLocation" id="strClientContactLocationHidden0" disabled="disabled">
		        				<select name="strClientContactLocation" id="strClientContactLocation0" class="<%=validReqOpt %>">
			        				<option value="">Select Location</option>
			        				<%=(String)request.getAttribute("sbWLoc") %>
		        				</select>
		        				<span id="addOtherLocSpan0"> <a href="javascript:void(0);" onclick="addOtherLoc('A', '0');">Other</a></span>
								<span id="removeOtherLocSpan0" style="display: none;"> <a href="javascript:void(0);" onclick="addOtherLoc('R', '0');">Reset</a></span>
								<div id="otherLocDiv0" style="display: none; margin-top: 10px;"><input type="text" name="otherLocation" id="otherLocation0" placeholder="Add New Location" onblur="checkExistLoc('existLocDiv0', this.value, '0');" onchange="checkExistLoc('existLocDiv0', this.value, '0');"/></div>
								<div id="existLocDiv0" style="font-size: 11px;"></div>
		        			</td>
		        		</tr>
		       
		        		<tr>
		        			<td class="txtlabel alignRight">Photo:</td>
		        			<td><img height="60" width="60" class="lazy" id="strClientContactPhotoImg0" style="width: 1px solid #AAAAAA; border: 1px lightgray solid; margin-bottom: 2px;" src="userImages/avatar_photo.png" data-original="" />
		        				<input type="file" name="strClientContactPhoto" id="strClientContactPhoto0" onchange="readImageURL(this, 'strClientContactPhotoImg0');"/>
		        			</td>
		        			<td class="alignRight" colspan="2" style="vertical-align: bottom;padding-right: 30px !important;">
		        				<a href="javascript:void(0)" onclick="addNewSPOC()" class="fa fa-fw fa-plus" title="Add SPOC">&nbsp;</a>
		        			</td>
		        		</tr>
		        	</table>
				</div>
			<% } %>
			<input type="hidden" name="hideSpocCount" id="hideSpocCount" value="<%=(alSpocData != null && !alSpocData.isEmpty()) ? alSpocData.size(): "0" %>" />
			<input type="hidden" name="hideDesigOption" id="hideDesigOption" value="<%=(String)request.getAttribute("sbDesigAjax") %>" />
			<input type="hidden" name="hideDepartOption" id="hideDepartOption" value="<%=(String)request.getAttribute("sbDepartAjax") %>" />
			<input type="hidden" name="hideWLocOption" id="hideWLocOption" value="<%=(String)request.getAttribute("sbWLocAjax") %>" />
			<input type="hidden" name="hideCountryOption" id="hideCountryOption" value="<%=(String)request.getAttribute("sbCountryAjax") %>" />
			<input type="hidden" name="hideIndustryOption" id="hideIndustryOption" value="<%=(String)request.getAttribute("sbIndustryAjax") %>" />
			
		</div>
		
		
		
		<div id="addBrandDiv">
			<% 
				List<List<String>> alBrandData = (List<List<String>>) request.getAttribute("alBrandData"); 
				Map<String, List<List<String>>> hmBrandSpocData = (Map<String, List<List<String>>>) request.getAttribute("hmBrandSpocData");
				if(hmBrandSpocData==null) hmBrandSpocData = new HashMap<String, List<List<String>>>();
			%>
			
			<% if(alBrandData!=null && alBrandData.size()>0) { 
				for(int a=0; a<alBrandData.size(); a++) {
					List<String> innerList = alBrandData.get(a);
			%>
				<div id="clientBrand<%=a %>" style="float: left; width: 100%;">
					<table border="0" class="table table_no_border">
						<tr><td colspan="4"><s:fielderror/></td></tr>
						<tr><td colspan="4"><hr style="height: 1px; width: 100%; margin: 0px; background-color: lightgray;"></td></tr>
						<tr>
		        			<td colspan="2"><label>&nbsp;&nbsp; New Subsidiary/ Brand</label></td>
		        			<td class="alignRight" colspan="2" style="padding-right: 25px !important;">&nbsp;<input type="hidden" name="clientBrandCntId" id="clientBrandCntId<%=a %>" value="<%=a %>"/>
		        				<input type="hidden" name="strClientBrandId" value= "<%=innerList.get(0) %>"/>
		        				<!-- <a href="javascript:void(0)" onclick="addNewBrand()" class="fa fa-fw fa-plus" title="Add Subsidiary/ Brand">&nbsp;</a> -->
		        				<% if(a>0) { %>
		        					<a href="javascript:void(0)" onclick="removeBrand(this.id)" id="<%=a %>" class="fa fa-fw fa-times-circle" style="color: red; font-size: 18px;" title="Remove Subsidiary/ Brand">&nbsp;</a>
		        				<% } %>
		        			</td>
		        		</tr>
						<tr>
						<% 	List<String> brandValidList = hmValidationFields.get("COMPANY_NAME");
							validReqOpt = "";
							validAsterix = "";
							if(uF.parseToBoolean(brandValidList.get(0))) {
								validReqOpt = "validateRequired";
								validAsterix = "<sup>*</sup>";
							}
						%>
							<td class="txtlabel alignRight" valign="top">Name:<%=validAsterix %>
								<input type="hidden" name="hiddenBrandNameValidAsterix" id="hiddenBrandNameValidAsterix" value="<%=validAsterix %>"/>
					        	<input type="hidden" name="hiddenBrandNameValidReqOpt" id="hiddenBrandNameValidReqOpt" value="<%=validReqOpt %>"/>
							</td>
							<td colspan="3">
							<input type="text" class="<%=validReqOpt %>" name="strClientBrandName" id="strClientBrandName<%=a %>" value="<%=uF.showData(innerList.get(1), "") %>"
							onkeyup ="getContent('myBrandDiv<%=a %>', 'GetClientName.action?strClientBrandName='+this.value)"/>
							<span class="hint">Add the Subsidiary/ Brand name.<span class="hint-pointer">&nbsp;</span></span>
							<div id="myBrandDiv<%=a %>" style="font-size: 11px;"></div>
							</td>
						</tr>
						
						<tr>
							<td class="txtlabel alignRight">Logo:<br/></td>
							<td>
							<%if(docRetriveLocation==null) { %>
								<img height="60" width="60" class="lazy" id="clientBrandLogoImg<%=a %>" style="border: 1px solid #CCCCCC; border: 1px lightgray solid; margin-bottom: 2px;" src="userImages/company_avatar_photo.png" data-original="<%=IConstants.IMAGE_LOCATION + ((strImage!=null && !strImage.toString().equals("")) ? strImage:"company_avatar_photo.png") %>" />
							<%} else { 
							String customerImage = IConstants.IMAGE_LOCATION + ((strImage!=null && !strImage.equals("")) ? strImage:"company_avatar_photo.png");
								if(strImage!=null && !strImage.equals("")) {
									customerImage = docRetriveLocation +IConstants.I_CUSTOMER+"/"+strClientId+"/"+IConstants.I_IMAGE+"/"+((strImage!=null && !strImage.equals("")) ? strImage:"company_avatar_photo.png");
								}
							%>
		                     	<img height="60" width="60" class="lazy" id="clientBrandLogoImg<%=a %>" style="border: 1px solid #CCCCCC; border: 1px lightgray solid; margin-bottom: 2px;" src="<%=customerImage %>" data-original="<%=customerImage %>" />
		                    <%} %>
								<s:file name="clientBrandLogo" id="clientBrandLogo<%=a %>" onchange="readImageURL(this, 'clientBrandLogoImg<%=a %>');"></s:file>
							</td>
						<!-- </tr>
						 
						<tr> -->
						<% 	List<String> addCBValidList = hmValidationFields.get("COMPANY_ADDRESS"); 
							validReqOpt = "";
							validAsterix = "";
							if(uF.parseToBoolean(addCBValidList.get(0))) {
								validReqOpt = "validateRequired";
								validAsterix = "<sup>*</sup>";
							}
						%>
							<td class="txtlabel alignRight" valign="top">Address:<%=validAsterix %>
								<input type="hidden" name="hiddenBrandAddressValidAsterix" id="hiddenBrandAddressValidAsterix" value="<%=validAsterix %>"/>
						        <input type="hidden" name="hiddenBrandAddressValidReqOpt" id="hiddenBrandAddressValidReqOpt" value="<%=validReqOpt %>"/>
							</td>
							<td> <textarea name="strClientBrandAddress" id="strClientBrandAddress<%=a %>" class="<%=validReqOpt %>" rows="4" cols="22"><%=uF.showData(innerList.get(3), "") %></textarea>
							</td>
						</tr>
						
						<tr>
						<% 	List<String> cityCBValidList = hmValidationFields.get("COMPANY_CITY"); 
							validReqOpt = "";
							validAsterix = "";
							if(uF.parseToBoolean(cityCBValidList.get(0))) {
								validReqOpt = "validateRequired";
								validAsterix = "<sup>*</sup>";
							}
						%>
							<td class="txtlabel alignRight">City:<%=validAsterix %>
								<input type="hidden" name="hiddenBrandCityValidAsterix" id="hiddenBrandCityValidAsterix" value="<%=validAsterix %>"/>
						        <input type="hidden" name="hiddenBrandCityValidReqOpt" id="hiddenBrandCityValidReqOpt" value="<%=validReqOpt %>"/>
							</td>
							<td>
								<input type="text" name="strClientBrandCity" id="strClientBrandCity<%=a %>" class="<%=validReqOpt %>" value="<%=uF.showData(innerList.get(4), "") %>">
							</td> 
						<!-- </tr>
						
						<tr> -->
						<% 	List<String> countryCBValidList = hmValidationFields.get("COMPANY_CONTRY"); 
							validReqOpt = "";
							validAsterix = "";
							String strval="";
							if(uF.parseToBoolean(countryCBValidList.get(0))) {
								validReqOpt = "validateRequired";
								validAsterix = "<sup>*</sup>";
								strval="1";
							}
						%>
							<td class="txtlabel alignRight">Country:<%=validAsterix %>
								<input type="hidden" name="hiddenBrandCountryValidAsterix" id="hiddenBrandCountryValidAsterix" value="<%=validAsterix %>"/>
						        <input type="hidden" name="hiddenBrandCountryValidReqOpt" id="hiddenBrandCountryValidReqOpt" value="<%=validReqOpt %>"/>
							</td>
							<td><select name="strClientBrandCountry" id="strClientBrandCountry<%=a %>" class="<%=validReqOpt %>" onchange="getContentAcs('stateBrandTD','GetStates.action?country='+this.value+'&type=CLIENTBRANDADDSTATE&validReq=<%=strval %>');">
			        				<option value="">Select Country</option>
			        				<%=innerList.get(5) %>
		        				</select>
							</td>
						</tr>
		
						<tr>
						<% 	List<String> stateCBValidList = hmValidationFields.get("COMPANY_STATE"); 
							validReqOpt = "";
							validAsterix = "";
							if(uF.parseToBoolean(stateCBValidList.get(0))) {
								validReqOpt = "validateRequired";
								validAsterix = "<sup>*</sup>";
							} 
						%>
						
							<td class="txtlabel alignRight">State:<%=validAsterix %>
								<input type="hidden" name="hiddenBrandStateValidAsterix" id="hiddenBrandStateValidAsterix" value="<%=validAsterix %>"/>
						        <input type="hidden" name="hiddenBrandStateValidReqOpt" id="hiddenBrandStateValidReqOpt" value="<%=validReqOpt %>"/>
							</td>
							<td id="stateBrandTD">
								<select name="strClientBrandState" id="strClientBrandState<%=a %>" class="<%=validReqOpt %>">
			        				<option value="">Select State</option>
			        				<%=innerList.get(6) %>
		        				</select>
							</td>
						<!-- </tr>
						
						<tr> -->
						<% 	List<String> pinCodeCBValidList = hmValidationFields.get("COMPANY_PIN_CODE"); 
							validReqOpt = "";
							validAsterix = "";
							if(uF.parseToBoolean(pinCodeCBValidList.get(0))) {
								validReqOpt = "validateRequired";
								validAsterix = "<sup>*</sup>";
							}
						%>
							<td class="txtlabel alignRight">Pin Code:<%=validAsterix %>
								<input type="hidden" name="hiddenBrandPinCodeValidAsterix" id="hiddenBrandPinCodeValidAsterix" value="<%=validAsterix %>"/>
						        <input type="hidden" name="hiddenBrandPinCodeValidReqOpt" id="hiddenBrandPinCodeValidReqOpt" value="<%=validReqOpt %>"/>
							</td>
							<td><input type="text" name="strBrandPinCode" id="strBrandPinCode<%=a %>" class="<%=validReqOpt %>" value="<%=uF.showData(innerList.get(7), "") %>"/>
							<span class="hint">Add the pin code for this client brand.<span class="hint-pointer">&nbsp;</span></span>
							</td> 
						</tr>
						
						<tr>
						<% 	List<String> industryCBValidList = hmValidationFields.get("COMPANY_INDUSTRY"); 
							validReqOpt = "";
							validAsterix = "";
							if(uF.parseToBoolean(industryCBValidList.get(0))) {
								validReqOpt = "validateRequired";
								validAsterix = "<sup>*</sup>";
							}
						%>
							<td class="txtlabel alignRight">Industry:<%=validAsterix %>
								<input type="hidden" name="hiddenBrandIndustryValidAsterix" id="hiddenBrandIndustryValidAsterix" value="<%=validAsterix %>"/>
						        <input type="hidden" name="hiddenBrandIndustryValidReqOpt" id="hiddenBrandIndustryValidReqOpt" value="<%=validReqOpt %>"/>
							</td>
							<td>
								<select name="clientBrandIndustry<%=a %>" id="clientBrandIndustry<%=a %>" class="<%=validReqOpt %>" multiple="multiple" size="3">
			        				<option value="">Select Industry</option>
			        				<%=innerList.get(8) %>
		        				</select>
								<span id="addBrandOtherSpan<%=a %>"> <a href="javascript:void(0);" onclick="addBrandOtherIndustry('A', '<%=a %>');">Other</a></span>
								<span id="removeBrandOtherSpan<%=a %>" style="display: none;"> <a href="javascript:void(0);" onclick="addBrandOtherIndustry('R', '<%=a %>');">Reset</a></span>
								<div id="otherBrandDiv<%=a %>" style="display: none; margin-top: 10px;"><input type="text" name="otherBrandIndustry" id="otherBrandIndustry<%=a %>" placeholder="Add New Industry" onblur="checkBrandExistIndustry('<%=a %>', 'existBrandIndustryDiv<%=a %>', this.value);" onchange="checkBrandExistIndustry('<%=a %>', 'existBrandIndustryDiv<%=a %>', this.value);"/></div>
								<div id="existBrandIndustryDiv<%=a %>" style="font-size: 11px;"></div>
								<script type="text/javascript">
									$(function() {
										$("#clientBrandIndustry<%=a %>").multiselect().multiselectfilter();
									});
								</script>
							</td>
						<!-- </tr>
						
						<tr> -->
						<% 	List<String> websiteCBValidList = hmValidationFields.get("COMPANY_WEBSITE"); 
							validReqOpt = "";
							validAsterix = "";
							if(uF.parseToBoolean(websiteCBValidList.get(0))) {
								validReqOpt = "validateRequired";
								validAsterix = "<sup>*</sup>";
							}
						%>
							<td class="txtlabel alignRight">Website:<%=validAsterix %>
								<input type="hidden" name="hiddenBrandWebsiteValidAsterix" id="hiddenBrandWebsiteValidAsterix" value="<%=validAsterix %>"/>
						        <input type="hidden" name="hiddenBrandWebsiteValidReqOpt" id="hiddenBrandWebsiteValidReqOpt" value="<%=validReqOpt %>"/>
							</td>
							<td><input type="text" name="companyBrandWebsite" id="companyBrandWebsite<%=a %>" class="<%=validReqOpt %>" value="<%=uF.showData(innerList.get(9), "") %>"/></td> 
						</tr>
						
						<tr>
						<% 	List<String> compCBDescValidList = hmValidationFields.get("COMPANY_DESCRIPTION"); 
							validReqOpt = "";
							validAsterix = "";
							if(uF.parseToBoolean(compCBDescValidList.get(0))) {
								validReqOpt = "validateRequired";
								validAsterix = "<sup>*</sup>";
							}
						%>
							<td class="txtlabel alignRight">Description:<%=validAsterix %>
								<input type="hidden" name="hiddenBrandDescriptionValidAsterix" id="hiddenBrandDescriptionValidAsterix" value="<%=validAsterix %>"/>
						        <input type="hidden" name="hiddenBrandDescriptionValidReqOpt" id="hiddenBrandDescriptionValidReqOpt" value="<%=validReqOpt %>"/>
							</td>
							<td colspan="3">
								<textarea name="companyBrandDescription" id="companyBrandDescription<%=a %>" class="<%=validReqOpt %>" rows="4" cols="22" style="width: 90% !important;"><%=uF.showData(innerList.get(10), "") %></textarea>
							</td> 
						</tr>
						
						<tr><td colspan="4"><hr></td> </tr>
						
						<tr>
							<td colspan="4"> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Statutory Compliance Details</td>
							<td></td> 
						</tr>	
						
						<tr>
						<% 	List<String> tdsCBValidList = hmValidationFields.get("COMPANY_TDS"); 
							validReqOpt = "";
							validAsterix = "";
							if(uF.parseToBoolean(tdsCBValidList.get(0))) {
								validReqOpt = "validateRequired";
								validAsterix = "<sup>*</sup>";
							}
						%>
							<td class="txtlabel alignRight">GST:<%=validAsterix %>
								<input type="hidden" name="hiddenBrandGSTValidAsterix" id="hiddenBrandGSTValidAsterix" value="<%=validAsterix %>"/>
						        <input type="hidden" name="hiddenBrandGSTValidReqOpt" id="hiddenBrandGSTValidReqOpt" value="<%=validReqOpt %>"/>
							</td>
							<td> <input type="text" name="clientBrandTds" id="clientBrandTds<%=a %>" class="<%=validReqOpt %>" value="<%=uF.showData(innerList.get(11), "") %>"/> % </td> 

						<% 	List<String> regNoCBValidList = hmValidationFields.get("COMPANY_REG_NO"); 
							validReqOpt = "";
							validAsterix = "";
							if(uF.parseToBoolean(regNoCBValidList.get(0))) {
								validReqOpt = "validateRequired";
								validAsterix = "<sup>*</sup>";
							}
						%>
							<td class="txtlabel alignRight">GSTIN No.:<%=validAsterix %>
								<input type="hidden" name="hiddenBrandGSTNoValidAsterix" id="hiddenBrandGSTNoValidAsterix" value="<%=validAsterix %>"/>
						        <input type="hidden" name="hiddenBrandGSTNoValidReqOpt" id="hiddenBrandGSTNoValidReqOpt" value="<%=validReqOpt %>"/>
							</td>
							<td><input type="text" name="clientBrandRegistrationNo" id="clientBrandRegistrationNo<%=a %>" class="<%=validReqOpt %>" value="<%=uF.showData(innerList.get(12), "") %>"/> </td> 
						</tr>
					</table>
					
			
					<% List<List<String>> alCBSpocData = hmBrandSpocData.get(innerList.get(0));
						if(alCBSpocData != null && !alCBSpocData.isEmpty()) {
							for(int i=0; i<alCBSpocData.size(); i++) {
								innerList = alCBSpocData.get(i);
					%>
						<div id="clientBrandSPOC<%=a %>_<%=i %>" style="float: left; width: 100%;">
							<table class="table table_no_border">
								<tr><td colspan="4"><hr></td></tr>
				        		<tr>
				        			<td colspan="4"><label>&nbsp;&nbsp;New Subsidiary/ Brand Contact Person</label></td>
				        		</tr>
				        		<tr>
				        		<% 	List<String> spocFNameValidList = hmValidationFields.get("COMPANY_CONTACT_FIRST_NAME"); 
									validReqOpt = "";
									validAsterix = "";
									if(uF.parseToBoolean(spocFNameValidList.get(0))) {
										validReqOpt = "validateRequired";
										validAsterix = "<sup>*</sup>";
									}
								%>
				        			<td class="txtlabel alignRight">First Name:<%=validAsterix %>
				        				<input type="hidden" name="strClientBrandContactId<%=a %>" value= "<%=innerList.get(0) %>"/>
					        			<input type="hidden" name="hiddenSPOCFNameValidAsterix" id="hiddenSPOCFNameValidAsterix" value="<%=validAsterix %>"/>
					        			<input type="hidden" name="hiddenSPOCFNameValidReqOpt" id="hiddenSPOCFNameValidReqOpt" value="<%=validReqOpt %>"/>
				        			</td>
				        			<td><input type="text" name="strClientBrandContactFName<%=a %>" id="strClientBrandContactFName<%=a %>_<%=i %>" class="<%=validReqOpt %>" value="<%=innerList.get(1) %>"/></td>

				        		<% 	List<String> spocMNameValidList = hmValidationFields.get("COMPANY_CONTACT_MIDDLE_NAME"); 
									validReqOpt = "";
									validAsterix = "";
									if(uF.parseToBoolean(spocMNameValidList.get(0))) {
										validReqOpt = "validateRequired";
										validAsterix = "<sup>*</sup>";
									}
								%>
				        			<td class="txtlabel alignRight">Middle Name:<%=validAsterix %>
					        			<input type="hidden" name="hiddenSPOCMNameValidAsterix" id="hiddenSPOCMNameValidAsterix" value="<%=validAsterix %>"/>
					        			<input type="hidden" name="hiddenSPOCMNameValidReqOpt" id="hiddenSPOCMNameValidReqOpt" value="<%=validReqOpt %>"/>
				        			</td>
				        			<td><input type="text" name="strClientBrandContactMName<%=a %>" id="strClientBrandContactMName<%=a %>_<%=i %>" class="<%=validReqOpt %>" value="<%=innerList.get(2) %>"/></td>
				        		</tr>
				        		
				        		<tr>
				        		<% 	List<String> spocLNameValidList = hmValidationFields.get("COMPANY_CONTACT_LAST_NAME"); 
									validReqOpt = "";
									validAsterix = "";
									if(uF.parseToBoolean(spocLNameValidList.get(0))) {
										validReqOpt = "validateRequired";
										validAsterix = "<sup>*</sup>";
									}
								%>
				        			<td class="txtlabel alignRight">Last Name:<%=validAsterix %>
				        				<input type="hidden" name="hiddenSPOCLNameValidAsterix" id="hiddenSPOCLNameValidAsterix" value="<%=validAsterix %>"/>
					        			<input type="hidden" name="hiddenSPOCLNameValidReqOpt" id="hiddenSPOCLNameValidReqOpt" value="<%=validReqOpt %>"/>
				        			</td>
				        			<td><input type="text" name="strClientBrandContactLName<%=a %>" id="strClientBrandContactLName<%=a %>_<%=i %>" class="<%=validReqOpt %>" value="<%=innerList.get(3) %>"/></td>

				        		<% 	List<String> spocContactNoValidList = hmValidationFields.get("COMPANY_CONTACT_CONTACT_NO"); 
									validReqOpt = "";
									validAsterix = "";
									if(uF.parseToBoolean(spocContactNoValidList.get(0))) {
										validReqOpt = "validateRequired";
										validAsterix = "<sup>*</sup>";
									}
								%>
				        			<td class="txtlabel alignRight">Contact No:<%=validAsterix %>
				        				<input type="hidden" name="hiddenSPOCContactNoValidAsterix" id="hiddenSPOCContactNoValidAsterix" value="<%=validAsterix %>"/>
					        			<input type="hidden" name="hiddenSPOCContactNoValidReqOpt" id="hiddenSPOCContactNoValidReqOpt" value="<%=validReqOpt %>"/>
				        			</td>
				        			<td><input type="text" name="strClientBrandContactNo<%=a %>" id="strClientBrandContactNo<%=a %>_<%=i %>" class="<%=validReqOpt %>" value="<%=innerList.get(4) %>"/></td>
				        		</tr>
				        
				        		<tr>
				        		<% 	List<String> spocEmailIdValidList = hmValidationFields.get("COMPANY_CONTACT_MAIL_ID"); 
									validReqOpt = "validateEmail";
									validAsterix = "";
									if(uF.parseToBoolean(spocEmailIdValidList.get(0))) {
										validReqOpt = "validateRequired validateEmail";
										validAsterix = "<sup>*</sup>";
									}
								%>
				        			<td class="txtlabel alignRight">Email Id:<%=validAsterix %>
				        				<input type="hidden" name="hiddenSPOCEmailIdValidAsterix" id="hiddenSPOCEmailIdValidAsterix" value="<%=validAsterix %>"/>
					        			<input type="hidden" name="hiddenSPOCEmailIdValidReqOpt" id="hiddenSPOCEmailIdValidReqOpt" value="<%=validReqOpt %>"/>
				        			</td>
				        			<td><input type="text" name="strClientBrandContactEmail<%=a %>" id="strClientBrandContactEmail<%=a %>_<%=i %>" class="<%=validReqOpt %>" value="<%=innerList.get(5) %>"/></td>

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
				        				<input type="hidden" name="strClientBrandContactDesig<%=a %>" id="strClientBrandContactDesigHidden<%=a %>_<%=i %>" disabled="disabled">
				        				<select name="strClientBrandContactDesig<%=a %>" id="strClientBrandContactDesig<%=a %>_<%=i %>" class="<%=validReqOpt %>" >
					        				<option value="">Select Designation</option>
					        				<%=innerList.get(6) %>
				        				</select>
				        				<span id="addBrandOtherDesigSpan<%=a %>_<%=i %>"> <a href="javascript:void(0);" onclick="addBrandOtherDesig('A', '<%=a %>', '<%=i %>');">Other</a></span>
										<span id="removeBrandOtherDesigSpan<%=a %>_<%=i %>" style="display: none;"> <a href="javascript:void(0);" onclick="addBrandOtherDesig('R', '<%=a %>', '<%=i %>');">Reset</a></span>
										<div id="otherBrandDesigDiv<%=a %>_<%=i %>" style="display: none; margin-top: 10px;"><input type="text" name="otherBrandDesignation<%=a %>" id="otherBrandDesignation<%=a %>_<%=i %>" placeholder="Add New Designation" onblur="checkBrandExistDesig('existBrandDesigDiv<%=a %>_<%=i %>', this.value, '<%=a %>', '<%=i %>');" onchange="checkBrandExistDesig('existBrandDesigDiv<%=a %>_<%=i %>', this.value, '<%=a %>', '<%=i %>');"/></div>
										<div id="existBrandDesigDiv<%=a %>_<%=i %>" style="font-size: 11px;"></div>
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
				        				<input type="hidden" name="strClientBrandContactDepartment<%=a %>" id="strClientBrandContactDepartmentHidden<%=a %>_<%=i %>" disabled="disabled">
				        				<select name="strClientBrandContactDepartment<%=a %>" id="strClientBrandContactDepartment<%=a %>_<%=i %>" class="<%=validReqOpt %>" >
					        				<option value="">Select Department</option>
					        				<%=innerList.get(7) %>
				        				</select>
				        				<span id="addBrandOtherDepartSpan<%=a %>_<%=i %>"> <a href="javascript:void(0);" onclick="addBrandOtherDepart('A', '<%=a %>', '<%=i %>');">Other</a></span>
										<span id="removeBrandOtherDepartSpan<%=a %>_<%=i %>" style="display: none;"> <a href="javascript:void(0);" onclick="addBrandOtherDepart('R', '<%=a %>', '<%=i %>');">Reset</a></span>
										<div id="otherBrandDepartDiv<%=a %>_<%=i %>" style="display: none; margin-top: 10px;"><input type="text" name="otherBrandDepartment<%=a %>" id="otherBrandDepartment<%=a %>_<%=i %>" placeholder="Add New Department" onblur="checkBrandExistDepart('existBrandDepartDiv<%=a %>_<%=i %>', this.value, '<%=a %>', '<%=i %>');" onchange="checkBrandExistDepart('existBrandDepartDiv<%=a %>_<%=i %>', this.value, '<%=a %>', '<%=i %>');"/></div>
										<div id="existBrandDepartDiv<%=a %>_<%=i %>" style="font-size: 11px;"></div>
				        			</td>

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
				        				<input type="hidden" name="strClientBrandContactLocation<%=a %>" id="strClientBrandContactLocationHidden<%=a %><%=i %>" disabled="disabled">
				        				<select name="strClientBrandContactLocation<%=a %>" id="strClientBrandContactLocation<%=a %>_<%=i %>" class="<%=validReqOpt %>" >
					        				<option value="">Select Location</option>
					        				<%=innerList.get(8) %>
				        				</select>
				        				<span id="addBrandOtherLocSpan<%=a %>_<%=i %>"> <a href="javascript:void(0);" onclick="addBrandOtherLoc('A', '<%=a %>', '<%=i %>');">Other</a></span>
										<span id="removeBrandOtherLocSpan<%=a %>_<%=i %>" style="display: none;"> <a href="javascript:void(0);" onclick="addBrandOtherLoc('R', '<%=a %>', '<%=i %>');">Reset</a></span>
										<div id="otherBrandLocDiv<%=a %>_<%=i %>" style="display: none; margin-top: 10px;"><input type="text" name="otherBrandLocation<%=a %>" id="otherBrandLocation<%=a %>_<%=i %>" placeholder="Add New Location" onblur="checkBrandExistLoc('existBrandLocDiv<%=a %>_<%=i %>', this.value, '<%=a %>', '<%=i %>');" onchange="checkBrandExistLoc('existBrandLocDiv<%=a %>_<%=i %>', this.value, '<%=a %>', '<%=i %>');"/></div>
										<div id="existBrandLocDiv<%=a %>_<%=i %>" style="font-size: 11px;"></div>
				        			</td>
				        		</tr>
				       
				        		<tr>
				        			<td class="txtlabel alignRight">Photo:</td>
				        			<td><img height="60" width="60" class="lazy" id="strClientBrandContactPhotoImg<%=a %>_<%=i %>" style="width: 1px solid #AAAAAA; border: 1px lightgray solid; margin-bottom: 2px;" src="<%=innerList.get(9) %>" data-original="<%=innerList.get(9) %>" />
				        				<input type="file" name="strClientBrandContactPhoto<%=a %>" id="strClientBrandContactPhoto<%=a %>_<%=i %>" onchange="readImageURL(this, 'strClientBrandContactPhotoImg<%=a %>_<%=i %>');"/>
				        			</td>
				        			<td class="alignRight" colspan="2" style="vertical-align: bottom;padding-right: 30px !important;">
				        				<a href="javascript:void(0)" onclick="addNewBrandSPOC('<%=a %>')" class="fa fa-fw fa-plus" title="Add Subsidiary/ Brand SPOC">&nbsp;</a>
				        				<% if(i>0) { %>
				        					<a href="javascript:void(0)" onclick="removeBrandSPOC('<%=a %>', this.id)" id="<%=i %>" class="fa fa-fw fa-remove" style="color: red;" title="Remove Subsidiary/ Brand SPOC">&nbsp;</a>
				        				<% } %>
				        			</td>
				        		</tr>
				        	</table>
						</div>
					<% } %>
				<% } else { %>
					<div id="clientBrandSPOC<%=a %>_0" style="float: left; width: 100%;">
						<table class="table table_no_border">
							<tr><td colspan="4"><hr></td></tr>
			        		<tr>
			        			<td colspan="2"><label>&nbsp;&nbsp; New Subsidiary/ Brand Contact Person</label></td>
			        			<td colspan="2">&nbsp;</td>
			        		</tr>
			        		
			        		<tr>
			        		<% 	List<String> spocFNameValidList = hmValidationFields.get("COMPANY_CONTACT_FIRST_NAME"); 
								validReqOpt = "";
								validAsterix = "";
								if(uF.parseToBoolean(spocFNameValidList.get(0))) {
									validReqOpt = "validateRequired";
									validAsterix = "<sup>*</sup>";
								}
							%>
			        			<td class="txtlabel alignRight">First Name:<%=validAsterix %>
			        				<input type="hidden" name="hiddenSPOCFNameValidAsterix" id="hiddenSPOCFNameValidAsterix" value="<%=validAsterix %>"/>
					        		<input type="hidden" name="hiddenSPOCFNameValidReqOpt" id="hiddenSPOCFNameValidReqOpt" value="<%=validReqOpt %>"/>
			        			</td>
			        			<td><input type="text" name="strClientBrandContactFName<%=a %>" id="strClientBrandContactFName<%=a %>_0" class="<%=validReqOpt %>"/></td>

			        		<% 	List<String> spocMNameValidList = hmValidationFields.get("COMPANY_CONTACT_MIDDLE_NAME"); 
								validReqOpt = "";
								validAsterix = "";
								if(uF.parseToBoolean(spocMNameValidList.get(0))) {
									validReqOpt = "validateRequired";
									validAsterix = "<sup>*</sup>";
								}
							%>
			        			<td class="txtlabel alignRight">Middle Name:<%=validAsterix %>
				        			<input type="hidden" name="hiddenSPOCMNameValidAsterix" id="hiddenSPOCMNameValidAsterix" value="<%=validAsterix %>"/>
					        		<input type="hidden" name="hiddenSPOCMNameValidReqOpt" id="hiddenSPOCMNameValidReqOpt" value="<%=validReqOpt %>"/>
			        			</td>
			        			<td><input type="text" name="strClientBrandContactMName<%=a %>" id="strClientBrandContactMName<%=a %>_0" class="<%=validReqOpt %>"/></td>
			        		</tr>
			        		
			        		<tr>
			        		<% 	List<String> spocLNameValidList = hmValidationFields.get("COMPANY_CONTACT_LAST_NAME"); 
								validReqOpt = "";
								validAsterix = "";
								if(uF.parseToBoolean(spocLNameValidList.get(0))) {
									validReqOpt = "validateRequired";
									validAsterix = "<sup>*</sup>";
								}
							%>
			        			<td class="txtlabel alignRight">Last Name:<%=validAsterix %>
			        				<input type="hidden" name="hiddenSPOCLNameValidAsterix" id="hiddenSPOCLNameValidAsterix" value="<%=validAsterix %>"/>
					        		<input type="hidden" name="hiddenSPOCLNameValidReqOpt" id="hiddenSPOCLNameValidReqOpt" value="<%=validReqOpt %>"/>
			        			</td>
			        			<td><input type="text" name="strClientBrandContactLName<%=a %>" id="strClientBrandContactLName<%=a %>_0" class="<%=validReqOpt %>"/></td>

			        		<% 	List<String> spocContactNoValidList = hmValidationFields.get("COMPANY_CONTACT_CONTACT_NO"); 
								validReqOpt = "";
								validAsterix = "";
								if(uF.parseToBoolean(spocContactNoValidList.get(0))) {
									validReqOpt = "validateRequired";
									validAsterix = "<sup>*</sup>";
								}
							%>
			        			<td class="txtlabel alignRight">Contact No:<%=validAsterix %>
			        				<input type="hidden" name="hiddenSPOCContactNoValidAsterix" id="hiddenSPOCContactNoValidAsterix" value="<%=validAsterix %>"/>
					        		<input type="hidden" name="hiddenSPOCContactNoValidReqOpt" id="hiddenSPOCContactNoValidReqOpt" value="<%=validReqOpt %>"/>
			        			</td>
			        			<td><input type="text" name="strClientBrandContactNo<%=a %>" id="strClientBrandContactNo<%=a %>_0" class="<%=validReqOpt %>"/></td>
			        		</tr>
			        
			        		<tr>
			        		<% 	List<String> spocEmailIdValidList = hmValidationFields.get("COMPANY_CONTACT_MAIL_ID"); 
								validReqOpt = "validateEmail";
								validAsterix = "";
								if(uF.parseToBoolean(spocEmailIdValidList.get(0))) {
									validReqOpt = "validateRequired validateEmail";
									validAsterix = "<sup>*</sup>";
								}
							%>
			        			<td class="txtlabel alignRight">Email Id:<%=validAsterix %>
			        				<input type="hidden" name="hiddenSPOCEmailIdValidAsterix" id="hiddenSPOCEmailIdValidAsterix" value="<%=validAsterix %>"/>
					        		<input type="hidden" name="hiddenSPOCEmailIdValidReqOpt" id="hiddenSPOCEmailIdValidReqOpt" value="<%=validReqOpt %>"/>
			        			</td>
			        			<td><input type="text" name="strClientBrandContactEmail<%=a %>" id="strClientBrandContactEmail<%=a %>_0" class="<%=validReqOpt %>"/></td>

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
			        				<input type="hidden" name="strClientBrandContactDesig<%=a %>" id="strClientBrandContactDesigHidden<%=a %>_0" disabled="disabled">
			        				<select name="strClientBrandContactDesig<%=a %>" id="strClientBrandContactDesig<%=a %>_0" class="<%=validReqOpt %>">
				        				<option value="">Select Designation</option>
				        				<%=(String)request.getAttribute("sbDesig") %>
			        				</select>
			        				<span id="addBrandOtherDesigSpan<%=a %>_0"> <a href="javascript:void(0);" onclick="addBrandOtherDesig('A', '<%=a %>', '0');">Other</a></span>
									<span id="removeBrandOtherDesigSpan<%=a %>_0" style="display: none;"> <a href="javascript:void(0);" onclick="addBrandOtherDesig('R', '<%=a %>', '0');">Reset</a></span>
									<div id="otherBrandDesigDiv<%=a %>_0" style="display: none; margin-top: 10px;"><input type="text" name="otherBrandDesignation<%=a %>" id="otherBrandDesignation<%=a %>_0" placeholder="Add New Designation" onblur="checkBrandExistDesig('existBrandDesigDiv<%=a %>_0', this.value, '<%=a %>', '0');" onchange="checkBrandExistDesig('existBrandDesigDiv<%=a %>_0', this.value, '<%=a %>', '0');"/></div>
									<div id="existBrandDesigDiv<%=a %>_0" style="font-size: 11px;"></div>
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
			        				<input type="hidden" name="strClientBrandContactDepartment<%=a %>" id="strClientBrandContactDepartmentHidden<%=a %>_0" disabled="disabled">
			        				<select name="strClientBrandContactDepartment<%=a %>" id="strClientBrandContactDepartment<%=a %>_0" class="<%=validReqOpt %>">
				        				<option value="">Select Department</option>
				        				<%=(String)request.getAttribute("sbDepart") %>
			        				</select>
			        				<span id="addBrandOtherDepartSpan<%=a %>_0"> <a href="javascript:void(0);" onclick="addBrandOtherDepart('A', '<%=a %>', '0');">Other</a></span>
									<span id="removeBrandOtherDepartSpan<%=a %>_0" style="display: none;"> <a href="javascript:void(0);" onclick="addBrandOtherDepart('R', '<%=a %>', '0');">Reset</a></span>
									<div id="otherBrandDepartDiv<%=a %>_0" style="display: none; margin-top: 10px;"><input type="text" name="otherBrandDepartment<%=a %>" id="otherBrandDepartment<%=a %>_0" placeholder="Add New Department" onblur="checkBrandExistDepart('existBrandDepartDiv<%=a %>_0', this.value, '<%=a %>', '0');" onchange="checkBrandExistDepart('existBrandDepartDiv<%=a %>_0', this.value, '<%=a %>', '0');"/></div>
									<div id="existBrandDepartDiv<%=a %>_0" style="font-size: 11px;"></div>
			        			</td>
			        		
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
			        				<input type="hidden" name="strClientBrandContactLocation<%=a %>" id="strClientBrandContactLocationHidden<%=a %>_0" disabled="disabled">
			        				<select name="strClientBrandContactLocation<%=a %>" id="strClientBrandContactLocation<%=a %>_0" class="<%=validReqOpt %>">
				        				<option value="">Select Location</option>
				        				<%=(String)request.getAttribute("sbWLoc") %>
			        				</select>
			        				<span id="addBrandOtherLocSpan<%=a %>_0"> <a href="javascript:void(0);" onclick="addBrandOtherLoc('A', '<%=a %>', '0');">Other</a></span>
									<span id="removeBrandOtherLocSpan<%=a %>_0" style="display: none;"> <a href="javascript:void(0);" onclick="addBrandOtherLoc('R', '<%=a %>', '0');">Reset</a></span>
									<div id="otherBrandLocDiv<%=a %>_0" style="display: none; margin-top: 10px;"><input type="text" name="otherBrandLocation<%=a %>" id="otherBrandLocation<%=a %>_0" placeholder="Add New Location" onblur="checkBrandExistLoc('existBrandLocDiv<%=a %>_0', this.value, '<%=a %>', '0');" onchange="checkBrandExistLoc('existBrandLocDiv<%=a %>_0', this.value, '<%=a %>', '0');"/></div>
									<div id="existBrandLocDiv<%=a %>_0" style="font-size: 11px;"></div>
			        			</td>
			        		</tr>
			       
			        		<tr>
			        			<td class="txtlabel alignRight">Photo:</td>
			        			<td><img height="60" width="60" class="lazy" id="strClientBrandContactPhotoImg<%=a %>_0" style="width: 1px solid #AAAAAA; border: 1px lightgray solid; margin-bottom: 2px;" src="userImages/avatar_photo.png" data-original="" />
			        				<input type="file" name="strClientBrandContactPhoto<%=a %>" id="strClientBrandContactPhoto<%=a %>_0" onchange="readImageURL(this, 'strClientBrandContactPhotoImg<%=a %>_0');"/>
			        			</td>
			        			<td class="alignRight" colspan="2" style="vertical-align: bottom;padding-right: 30px !important;">
			        				<a href="javascript:void(0)" onclick="addNewBrandSPOC('<%=a %>')" class="fa fa-fw fa-plus" title="Add Subsidiary/ Brand SPOC">&nbsp;</a>
			        			</td>
			        		</tr>
			        	</table>
					</div>
				<% } %>
				<input type="hidden" name="hideBrandSpocCount<%=a %>" id="hideBrandSpocCount<%=a %>" value="<%=(alCBSpocData != null && !alCBSpocData.isEmpty()) ? alCBSpocData.size(): "0" %>" />
				</div>
				<% } %>
				
			</div>
			<% } else { %>
			<div id="clientBrand0" style="display: none; float: left; width: 100%;">
				<table border="0" class="table table_no_border">
					<tr><td colspan="4"><s:fielderror/></td></tr>
					<tr><td colspan="4"><hr style="height: 1px; width: 100%; margin: 0px; background-color: lightgray;"></td></tr>
					<tr>
	        			<td colspan="2"><label>&nbsp;&nbsp; New Subsidiary/ Brand</label></td>
	        			<td class="alignRight" colspan="2" style="padding-right: 25px !important;">&nbsp;<input type="hidden" name="clientBrandCntId" id="clientBrandCntId0" value="0"/>
	        				<!-- <a href="javascript:void(0)" onclick="addNewBrand()" class="fa fa-fw fa-plus" title="Add Subsidiary/ Brand">&nbsp;</a> -->
	        				<a href="javascript:void(0)" onclick="removeFirstBrand(this.id)" id="0" class="fa fa-fw fa-times-circle" style="color: red; font-size: 18px;" title="Remove Subsidiary/ Brand">&nbsp;</a>
	        			</td>
	        		</tr>
					<tr>
					<% 	List<String> brandValidList = hmValidationFields.get("COMPANY_NAME");
						validReqOpt = "";
						validAsterix = "";
						if(uF.parseToBoolean(brandValidList.get(0))) {
							validReqOpt = "validateRequired";
							validAsterix = "<sup>*</sup>";
						}
					%>
						<td class="txtlabel alignRight" valign="top">Name:<%=validAsterix %>
							<input type="hidden" name="hiddenBrandNameValidAsterix" id="hiddenBrandNameValidAsterix" value="<%=validAsterix %>"/>
							<input type="hidden" name="hiddenBrandNameValidReqOpt" id="hiddenBrandNameValidReqOpt" value="<%=validReqOpt %>"/>
						</td>
						<td colspan="3">
							<input type="text" class="<%=validReqOpt %>" name="strClientBrandName" id="strClientBrandName0"  onkeyup ="getContent('myBrandDiv0', 'GetClientName.action?strClientBrandName='+this.value)"/>
							<span class="hint">Add the Subsidiary/ Brand name.<span class="hint-pointer">&nbsp;</span></span>
							<div id="myBrandDiv0" style="font-size: 11px;"></div>
						</td>
					</tr>
					
					<tr>
						<td class="txtlabel alignRight">Logo:<br/></td>
						<td>
	                     	<img height="60" width="60" class="lazy" id="clientBrandLogoImg0" style="border: 1px solid #CCCCCC; border: 1px lightgray solid; margin-bottom: 2px;" src="userImages/company_avatar_photo.png" data-original="userImages/company_avatar_photo.png" />
							<s:file name="clientBrandLogo" id="clientBrandLogo0" onchange="readImageURL(this, 'clientBrandLogoImg0');"></s:file>
						</td>
					<!-- </tr>
					 
					<tr> -->
					<% 	List<String> addCBValidList = hmValidationFields.get("COMPANY_ADDRESS"); 
						validReqOpt = "";
						validAsterix = "";
						if(uF.parseToBoolean(addCBValidList.get(0))) {
							validReqOpt = "validateRequired";
							validAsterix = "<sup>*</sup>";
						}
					%>
						<td class="txtlabel alignRight" valign="top">Address:<%=validAsterix %>
							<input type="hidden" name="hiddenBrandAddressValidAsterix" id="hiddenBrandAddressValidAsterix" value="<%=validAsterix %>"/>
							<input type="hidden" name="hiddenBrandAddressValidReqOpt" id="hiddenBrandAddressValidReqOpt" value="<%=validReqOpt %>"/>
						</td>
						<td> <textarea name="strClientBrandAddress" id="strClientBrandAddress0" class="<%=validReqOpt %>" rows="4" cols="22"></textarea>
						</td>
					</tr>
					
					<tr>
					<% 	List<String> cityCBValidList = hmValidationFields.get("COMPANY_CITY"); 
						validReqOpt = "";
						validAsterix = "";
						if(uF.parseToBoolean(cityCBValidList.get(0))) {
							validReqOpt = "validateRequired";
							validAsterix = "<sup>*</sup>";
						}
					%>
						<td class="txtlabel alignRight">City:<%=validAsterix %>
							<input type="hidden" name="hiddenBrandCityValidAsterix" id="hiddenBrandCityValidAsterix" value="<%=validAsterix %>"/>
							<input type="hidden" name="hiddenBrandCityValidReqOpt" id="hiddenBrandCityValidReqOpt" value="<%=validReqOpt %>"/>
						</td>
						<td>
							<input type="text" name="strClientBrandCity" id="strClientBrandCity0" class="<%=validReqOpt %>" />
						</td> 
					<!-- </tr>
					
					<tr> -->
					<% 	List<String> countryCBValidList = hmValidationFields.get("COMPANY_CONTRY"); 
						validReqOpt = "";
						validAsterix = "";
						if(uF.parseToBoolean(countryCBValidList.get(0))) {
							validReqOpt = "validateRequired";
							validAsterix = "<sup>*</sup>";
						}
					%>
					
					<td class="txtlabel alignRight">Country:<%=validAsterix %>
						<input type="hidden" name="hiddenBrandCountryValidAsterix" id="hiddenBrandCountryValidAsterix" value="<%=validAsterix %>"/>
						<input type="hidden" name="hiddenBrandCountryValidReqOpt" id="hiddenBrandCountryValidReqOpt" value="<%=validReqOpt %>"/>
					</td>
					<td>
					<% if(validReqOpt != null && !validReqOpt.equals("")) { %>
						<s:select id="strClientBrandCountry0" name="strClientBrandCountry" listKey="countryId" listValue="countryName" cssClass="validateRequired" headerKey="" 
							headerValue="Select Country" onchange="getContentAcs('stateBrandTD','GetStates.action?country='+this.value+'&type=CLIENTBRANDADDSTATE&validReq=1');" list="countryList" key="" required="true" />
					<% } else { %>
						<s:select id="strClientBrandCountry0" name="strClientBrandCountry" listKey="countryId" listValue="countryName" headerKey="" 
							headerValue="Select Country" onchange="getContentAcs('stateBrandTD','GetStates.action?country='+this.value+'&type=CLIENTBRANDADDSTATE');" list="countryList" key="" required="true" />
					<% } %>
						
					</td>
					</tr>
	
					<tr>
					<% 	List<String> stateCBValidList = hmValidationFields.get("COMPANY_STATE"); 
						validReqOpt = "";
						validAsterix = "";
						if(uF.parseToBoolean(stateCBValidList.get(0))) {
							validReqOpt = "validateRequired";
							validAsterix = "<sup>*</sup>";
						} 
					%>
					
					<td class="txtlabel alignRight">State:<%=validAsterix %>
						<input type="hidden" name="hiddenBrandStateValidAsterix" id="hiddenBrandStateValidAsterix" value="<%=validAsterix %>"/>
						<input type="hidden" name="hiddenBrandStateValidReqOpt" id="hiddenBrandStateValidReqOpt" value="<%=validReqOpt %>"/>
					</td><td id="stateBrandTD">
					<% if(validReqOpt != null && !validReqOpt.equals("")) { %>
						<s:select theme="simple" title="state" cssClass="validateRequired" id="strClientBrandState0" name="strClientBrandState" listKey="stateId" 
							listValue="stateName" headerKey="" headerValue="Select State" list="stateList" key="" required="true" /> 
					<% } else { %>
						<s:select theme="simple" title="state" id="strClientBrandState0" name="strClientBrandState" listKey="stateId" listValue="stateName" 
							headerKey="" headerValue="Select State" list="stateList" key="" /> 
					<% } %>		
					</td>
					<!-- </tr>
					
					<tr> -->
					<% 	List<String> pinCodeCBValidList = hmValidationFields.get("COMPANY_PIN_CODE"); 
						validReqOpt = "";
						validAsterix = "";
						if(uF.parseToBoolean(pinCodeCBValidList.get(0))) {
							validReqOpt = "validateRequired";
							validAsterix = "<sup>*</sup>";
						}
					%>
						<td class="txtlabel alignRight">Pin Code:<%=validAsterix %>
							<input type="hidden" name="hiddenBrandPinCodeValidAsterix" id="hiddenBrandPinCodeValidAsterix" value="<%=validAsterix %>"/>
							<input type="hidden" name="hiddenBrandPinCodeValidReqOpt" id="hiddenBrandPinCodeValidReqOpt" value="<%=validReqOpt %>"/>
						</td>
						<td><input type="text" name="strBrandPinCode" id="strBrandPinCode0" class="<%=validReqOpt %>" />
						<span class="hint">Add the pin code for this client brand.<span class="hint-pointer">&nbsp;</span></span>
						</td> 
					</tr>
					
					<tr>
					<% 	List<String> industryCBValidList = hmValidationFields.get("COMPANY_INDUSTRY"); 
						validReqOpt = "";
						validAsterix = "";
						if(uF.parseToBoolean(industryCBValidList.get(0))) {
							validReqOpt = "validateRequired";
							validAsterix = "<sup>*</sup>";
						}
					%>
						<td class="txtlabel alignRight">Industry:<%=validAsterix %>
							<input type="hidden" name="hiddenBrandIndustryValidAsterix" id="hiddenBrandIndustryValidAsterix" value="<%=validAsterix %>"/>
							<input type="hidden" name="hiddenBrandIndustryValidReqOpt" id="hiddenBrandIndustryValidReqOpt" value="<%=validReqOpt %>"/>
						</td>
						<td>
						<% if(uF.parseToBoolean(industryCBValidList.get(0))) { %>
							<s:select label="Select Industry" name="clientBrandIndustry0" id="clientBrandIndustry0" listKey="industryId" cssClass="validateRequired"
								listValue="industryName" multiple="true" size="3" list="clientIndustryList" key="" />
						<% } else { %>
							<s:select label="Select Industry" name="clientBrandIndustry0" id="clientBrandIndustry0" listKey="industryId" 
								listValue="industryName" multiple="true" size="3" list="clientIndustryList" key="" />
						<% } %> <span id="addBrandOtherSpan0"> <a href="javascript:void(0);" onclick="addBrandOtherIndustry('A', '0');">Other</a></span>
								<span id="removeBrandOtherSpan0" style="display: none;"> <a href="javascript:void(0);" onclick="addBrandOtherIndustry('R', '0');">Reset</a></span>
								<div id="otherBrandDiv0" style="display: none; margin-top: 10px;"><input type="text" name="otherBrandIndustry" id="otherBrandIndustry0" placeholder="Add New Industry" onblur="checkBrandExistIndustry('0', 'existBrandIndustryDiv0', this.value);" onchange="checkBrandExistIndustry('0', 'existBrandIndustryDiv0', this.value);"/></div>
								<div id="existBrandIndustryDiv0" style="font-size: 11px;"></div>
							<script type="text/javascript">
								$(function() {
									$("#clientBrandIndustry0").multiselect().multiselectfilter();
								});
							</script>
						</td>
					<!-- </tr>
					
					<tr> -->
					<% 	List<String> websiteCBValidList = hmValidationFields.get("COMPANY_WEBSITE"); 
						validReqOpt = "";
						validAsterix = "";
						if(uF.parseToBoolean(websiteCBValidList.get(0))) {
							validReqOpt = "validateRequired";
							validAsterix = "<sup>*</sup>";
						}
					%>
						<td class="txtlabel alignRight">Website:<%=validAsterix %>
							<input type="hidden" name="hiddenBrandWebsiteValidAsterix" id="hiddenBrandWebsiteValidAsterix" value="<%=validAsterix %>"/>
							<input type="hidden" name="hiddenBrandWebsiteValidReqOpt" id="hiddenBrandWebsiteValidReqOpt" value="<%=validReqOpt %>"/>
						</td>
						<td><input type="text" name="companyBrandWebsite" id="companyBrandWebsite0" class="<%=validReqOpt %>"/></td> 
					</tr>
					
					<tr>
					<% 	List<String> compCBDescValidList = hmValidationFields.get("COMPANY_DESCRIPTION"); 
						validReqOpt = "";
						validAsterix = "";
						if(uF.parseToBoolean(compCBDescValidList.get(0))) {
							validReqOpt = "validateRequired";
							validAsterix = "<sup>*</sup>";
						}
					%>
						<td class="txtlabel alignRight">Description:<%=validAsterix %>
							<input type="hidden" name="hiddenBrandDescriptionValidAsterix" id="hiddenBrandDescriptionValidAsterix" value="<%=validAsterix %>"/>
							<input type="hidden" name="hiddenBrandDescriptionValidReqOpt" id="hiddenBrandDescriptionValidReqOpt" value="<%=validReqOpt %>"/>
						</td>
						<td colspan="3">
							<textarea name="companyBrandDescription" id="companyBrandDescription0" class="<%=validReqOpt %>" rows="4" cols="22" style="width: 90% !important;"></textarea>
						</td> 
					</tr>
					
					<tr><td colspan="4"><hr></td> </tr>
					
					<tr>
						<td colspan="4"> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Statutory Compliance Details</td>
					</tr>	
					
					<tr>
					<% 	List<String> tdsCBValidList = hmValidationFields.get("COMPANY_TDS"); 
						validReqOpt = "";
						validAsterix = "";
						if(uF.parseToBoolean(tdsCBValidList.get(0))) {
							validReqOpt = "validateRequired";
							validAsterix = "<sup>*</sup>";
						}
					%>
						<td class="txtlabel alignRight">GST:<%=validAsterix %>
							<input type="hidden" name="hiddenBrandGSTValidAsterix" id="hiddenBrandGSTValidAsterix" value="<%=validAsterix %>"/>
							<input type="hidden" name="hiddenBrandGSTValidReqOpt" id="hiddenBrandGSTValidReqOpt" value="<%=validReqOpt %>"/>
						</td>
						<td> <input type="text" name="clientBrandTds" id="clientBrandTds0" class="<%=validReqOpt %>" /> % </td> 

					<% 	List<String> regNoCBValidList = hmValidationFields.get("COMPANY_REG_NO"); 
						validReqOpt = "";
						validAsterix = "";
						if(uF.parseToBoolean(regNoCBValidList.get(0))) {
							validReqOpt = "validateRequired";
							validAsterix = "<sup>*</sup>";
						}
					%>
						<td class="txtlabel alignRight">GSTIN No.:<%=validAsterix %>
							<input type="hidden" name="hiddenBrandGSTNoValidAsterix" id="hiddenBrandGSTNoValidAsterix" value="<%=validAsterix %>"/>
							<input type="hidden" name="hiddenBrandGSTNoValidReqOpt" id="hiddenBrandGSTNoValidReqOpt" value="<%=validReqOpt %>"/>
						</td>
						<td><input type="text" name="clientBrandRegistrationNo" id="clientBrandRegistrationNo0" class="<%=validReqOpt %>" /> </td> 
					</tr>
				</table>
				
				<div id="clientBrandSPOC0_0" style="float: left; width: 100%;">
					<table class="table table_no_border">
						<tr><td colspan="4"><hr></td></tr>
		        		<tr>
		        			<td colspan="2"><label>&nbsp;&nbsp; New Subsidiary/ Brand Contact Person</label></td>
		        			<td colspan="2">&nbsp;</td>
		        		</tr>
		        		
		        		<tr>
		        		<% 	List<String> spocFNameValidList = hmValidationFields.get("COMPANY_CONTACT_FIRST_NAME"); 
							validReqOpt = "";
							validAsterix = "";
							if(uF.parseToBoolean(spocFNameValidList.get(0))) {
								validReqOpt = "validateRequired";
								validAsterix = "<sup>*</sup>";
							}
						%>
		        			<td class="txtlabel alignRight">First Name:<%=validAsterix %>
		        				<input type="hidden" name="hiddenSPOCFNameValidAsterix" id="hiddenSPOCFNameValidAsterix" value="<%=validAsterix %>"/>
				        		<input type="hidden" name="hiddenSPOCFNameValidReqOpt" id="hiddenSPOCFNameValidReqOpt" value="<%=validReqOpt %>"/>
		        			</td>
		        			<td><input type="text" name="strClientBrandContactFName0" id="strClientBrandContactFName0_0" class="<%=validReqOpt %>"/></td>

		        		<% 	List<String> spocMNameValidList = hmValidationFields.get("COMPANY_CONTACT_MIDDLE_NAME"); 
							validReqOpt = "";
							validAsterix = "";
							if(uF.parseToBoolean(spocMNameValidList.get(0))) {
								validReqOpt = "validateRequired";
								validAsterix = "<sup>*</sup>";
							}
						%>
		        			<td class="txtlabel alignRight">Middle Name:<%=validAsterix %>
			        			<input type="hidden" name="hiddenSPOCMNameValidAsterix" id="hiddenSPOCMNameValidAsterix" value="<%=validAsterix %>"/>
				        		<input type="hidden" name="hiddenSPOCMNameValidReqOpt" id="hiddenSPOCMNameValidReqOpt" value="<%=validReqOpt %>"/>
		        			</td>
		        			<td><input type="text" name="strClientBrandContactMName0" id="strClientBrandContactMName0_0" class="<%=validReqOpt %>"/></td>
		        		</tr>
		        		
		        		<tr>
		        		<% 	List<String> spocLNameValidList = hmValidationFields.get("COMPANY_CONTACT_LAST_NAME"); 
							validReqOpt = "";
							validAsterix = "";
							if(uF.parseToBoolean(spocLNameValidList.get(0))) {
								validReqOpt = "validateRequired";
								validAsterix = "<sup>*</sup>";
							}
						%>
		        			<td class="txtlabel alignRight">Last Name:<%=validAsterix %>
		        				<input type="hidden" name="hiddenSPOCLNameValidAsterix" id="hiddenSPOCLNameValidAsterix" value="<%=validAsterix %>"/>
				        		<input type="hidden" name="hiddenSPOCLNameValidReqOpt" id="hiddenSPOCLNameValidReqOpt" value="<%=validReqOpt %>"/>
		        			</td>
		        			<td><input type="text" name="strClientBrandContactLName0" id="strClientBrandContactLName0_0" class="<%=validReqOpt %>"/></td>

		        		<% 	List<String> spocContactNoValidList = hmValidationFields.get("COMPANY_CONTACT_CONTACT_NO"); 
							validReqOpt = "";
							validAsterix = "";
							if(uF.parseToBoolean(spocContactNoValidList.get(0))) {
								validReqOpt = "validateRequired";
								validAsterix = "<sup>*</sup>";
							}
						%>
		        			<td class="txtlabel alignRight">Contact No:<%=validAsterix %>
		        				<input type="hidden" name="hiddenSPOCContactNoValidAsterix" id="hiddenSPOCContactNoValidAsterix" value="<%=validAsterix %>"/>
				        		<input type="hidden" name="hiddenSPOCContactNoValidReqOpt" id="hiddenSPOCContactNoValidReqOpt" value="<%=validReqOpt %>"/>
		        			</td>
		        			<td><input type="text" name="strClientBrandContactNo0" id="strClientBrandContactNo0_0" class="<%=validReqOpt %>"/></td>
		        		</tr>
		        
		        		<tr>
		        		<% 	List<String> spocEmailIdValidList = hmValidationFields.get("COMPANY_CONTACT_MAIL_ID"); 
							validReqOpt = "validateEmail";
							validAsterix = "";
							if(uF.parseToBoolean(spocEmailIdValidList.get(0))) {
								validReqOpt = "validateRequired validateEmail";
								validAsterix = "<sup>*</sup>";
							}
						%>
		        			<td class="txtlabel alignRight">Email Id:<%=validAsterix %>
		        				<input type="hidden" name="hiddenSPOCEmailIdValidAsterix" id="hiddenSPOCEmailIdValidAsterix" value="<%=validAsterix %>"/>
				        		<input type="hidden" name="hiddenSPOCEmailIdValidReqOpt" id="hiddenSPOCEmailIdValidReqOpt" value="<%=validReqOpt %>"/>
		        			</td>
		        			<td><input type="text" name="strClientBrandContactEmail0" id="strClientBrandContactEmail0_0" class="<%=validReqOpt %>"/></td>

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
		        				<input type="hidden" name="strClientBrandContactDesig0" id="strClientBrandContactDesigHidden0_0" disabled="disabled">
		        				<select name="strClientBrandContactDesig0" id="strClientBrandContactDesig0_0" class="<%=validReqOpt %>">
			        				<option value="">Select Designation</option>
			        				<%=(String)request.getAttribute("sbDesig") %>
		        				</select>
		        				<span id="addBrandOtherDesigSpan0_0"> <a href="javascript:void(0);" onclick="addBrandOtherDesig('A', '0', '0');">Other</a></span>
								<span id="removeBrandOtherDesigSpan0_0" style="display: none;"> <a href="javascript:void(0);" onclick="addBrandOtherDesig('R', '0', '0');">Reset</a></span>
								<div id="otherBrandDesigDiv0_0" style="display: none; margin-top: 10px;"><input type="text" name="otherBrandDesignation0" id="otherBrandDesignation0_0" placeholder="Add New Designation" onblur="checkBrandExistDesig('existBrandDesigDiv0_0', this.value, '0', '0');" onchange="checkBrandExistDesig('existBrandDesigDiv0_0', this.value, '0', '0');"/></div>
								<div id="existBrandDesigDiv0_0" style="font-size: 11px;"></div>
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
		        				<input type="hidden" name="strClientBrandContactDepartment0" id="strClientBrandContactDepartmentHidden0_0" disabled="disabled">
		        				<select name="strClientBrandContactDepartment0" id="strClientBrandContactDepartment0_0" class="<%=validReqOpt %>">
			        				<option value="">Select Department</option>
			        				<%=(String)request.getAttribute("sbDepart") %>
		        				</select>
		        				<span id="addBrandOtherDepartSpan0_0"> <a href="javascript:void(0);" onclick="addBrandOtherDepart('A', '0', '0');">Other</a></span>
								<span id="removeBrandOtherDepartSpan0_0" style="display: none;"> <a href="javascript:void(0);" onclick="addBrandOtherDepart('R', '0', '0');">Reset</a></span>
								<div id="otherBrandDepartDiv0_0" style="display: none; margin-top: 10px;"><input type="text" name="otherBrandDepartment0" id="otherBrandDepartment0_0" placeholder="Add New Department" onblur="checkBrandExistDepart('existBrandDepartDiv0_0', this.value, '0', '0');" onchange="checkBrandExistDepart('existBrandDepartDiv0_0', this.value, '0', '0');"/></div>
								<div id="existBrandDepartDiv0_0" style="font-size: 11px;"></div>
		        			</td>

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
		        				<input type="hidden" name="strClientBrandContactLocation0" id="strClientBrandContactLocationHidden0_0" disabled="disabled">
		        				<select name="strClientBrandContactLocation0" id="strClientBrandContactLocation0_0" class="<%=validReqOpt %>">
			        				<option value="">Select Location</option>
			        				<%=(String)request.getAttribute("sbWLoc") %>
		        				</select>
		        				<span id="addBrandOtherLocSpan0_0"> <a href="javascript:void(0);" onclick="addBrandOtherLoc('A', '0', '0');">Other</a></span>
								<span id="removeBrandOtherLocSpan0_0" style="display: none;"> <a href="javascript:void(0);" onclick="addBrandOtherLoc('R', '0', '0');">Reset</a></span>
								<div id="otherBrandLocDiv0_0" style="display: none; margin-top: 10px;"><input type="text" name="otherBrandLocation0" id="otherBrandLocation0_0" placeholder="Add New Location" onblur="checkBrandExistLoc('existBrandLocDiv0_0', this.value, '0', '0');" onchange="checkBrandExistLoc('existBrandLocDiv0_0', this.value, '0', '0');"/></div>
								<div id="existBrandLocDiv0_0" style="font-size: 11px;"></div>
		        			</td>
		        		</tr>
		       
		        		<tr>
		        			<td class="txtlabel alignRight">Photo:</td>
		        			<td><img height="60" width="60" class="lazy" id="strClientBrandContactPhotoImg0_0" style="width: 1px solid #AAAAAA; border: 1px lightgray solid; margin-bottom: 2px;" src="userImages/avatar_photo.png" data-original="" />
		        				<input type="file" name="strClientBrandContactPhoto0" id="strClientBrandContactPhoto0_0" onchange="readImageURL(this, 'strClientBrandContactPhotoImg0_0');"/>
		        			</td>
		        			<td class="alignRight" colspan="2" style="vertical-align: bottom;padding-right: 30px !important;">
		        				<a href="javascript:void(0)" onclick="addNewBrandSPOC('0')" class="fa fa-fw fa-plus" title="Add Subsidiary/ Brand SPOC">&nbsp;</a>
		        			</td>
		        		</tr>
		        	</table>
				</div>
				<input type="hidden" name="hideBrandSpocCount0" id="hideBrandSpocCount0" value="0" />
			</div>
			<% } %>
			<input type="hidden" name="hideBrandCount" id="hideBrandCount" value="<%=(alBrandData!=null && alBrandData.size()>0) ? alBrandData.size()-1 : "0" %>" />
		</div>
		
		<div id="addFirstBrandDiv" style="display: <%=(alBrandData!=null && alBrandData.size()>0) ? "none" : "block" %>"><a href="javascript:void(0)" onclick="addFirstBrand()" title="Add Subsidiary/ Brand"><i class="fa fa-plus-circle" aria-hidden="true"></i>Add Subsidiary/ Brand</a></div>
		<div id="addOtherBrandDiv" style="display: <%=(alBrandData!=null && alBrandData.size()>0) ? "block" : "none" %>"><a href="javascript:void(0)" onclick="addNewBrand()" title="Add Subsidiary/ Brand"><i class="fa fa-plus-circle" aria-hidden="true"></i>Add Subsidiary/ Brand</a></div>
		
		<div class="clr"></div>
		<div style="margin:0px 0px 15px 210px">
			<table class="table table_no_border">
			<tr>
				<td colspan="2" align="center">
					<s:submit cssClass="btn btn-primary" cssStyle="margin-right: 5px; padding: 3px;" value="Save" name="btnSave" id="btnSave"/>
					<%-- <s:submit cssClass="btn btn-success" cssStyle="margin-right: 5px; padding: 3px;" value="Save Company Details & Send to Customer" name="btnSaveAndSend" id="btnSaveAndSend"/> --%>
					<input type="button" value="Cancel" class="btn btn-danger" style="margin-right: 5px; padding: 3px;" name="cancel" onclick="closeForm('');">
				</td>
			</tr>
			</table>
		</div>         
		</s:form>
	</div>
</div>	
	
<script>

$("#formAddNewRow").submit(function(e){
	
	var fromPage = document.getElementById("fromPage").value;
	var clientID = document.getElementById("clientID").value;
	var proId = document.getElementById("proId1").value;
	
	if (fromPage != '' && fromPage === "Project") {
		e.preventDefault();
		var form_data = $("form[name='formAddNewRow']").serialize();
	 	  $.ajax({
			url : "AddClient.action",
			data: form_data,
			cache : false
		});
	 	 $("#modalInfo").hide();
		//alert("clientID ===>> " + clientID +" -- proId ===>> " + proId);
	 	$.ajax({
			url: 'PreAddNewProject1.action?clientID='+clientID+'&pro_id='+proId+'&operation=E&step=0',
			cache: true,
			success: function(result){
				$("#subSubDivResult").html(result);
	   		}
		});
	}
});


	//$("img.lazy").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10});
	$("img.lazy").lazyload({event : "sporty", threshold : 200,effect : "fadeIn",failure_limit : 10});
	
	$(window).bind("load", function() {
	    var timeout = setTimeout(function() { $("img.lazy").trigger("sporty") }, 1000);
	});
	
</script>
