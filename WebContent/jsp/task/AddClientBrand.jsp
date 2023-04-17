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
	            $('#'+targetDiv).attr('src', e.target.result).width(60).height(60);
	        };
	        reader.readAsDataURL(input.files[0]);
	    }
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

</script>

	<% 
		String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
		String strImage = (String) request.getAttribute("strImage");
		Map<String, List<String>> hmValidationFields = (Map<String, List<String>> ) request.getAttribute("hmValidationFields");
		UtilityFunctions uF = new UtilityFunctions();
		String validReqOpt = "";
		String validAsterix = "";
		String clientBrandId = (String)request.getAttribute("clientBrandId");
		String clientId = (String)request.getAttribute("clientId");
	%>
<div class="box box-info">
      <div class="box-header with-border" style="border-bottom: 2px solid lightgray;">
        <h3 class="box-title"><b><%=uF.showData((String)request.getAttribute("clientBrandName"), "New Subsidiary/ Brand") %></b></h3>
      </div><!-- /.box-header -->
      <div class="box-body table-responsive no-padding">
		<s:form theme="simple" action="AddClientBrand" method="POST" cssClass="formcss" id="formAddClientBrand" enctype="multipart/form-data">
			<s:hidden name="clientBrandId" />
			<s:hidden name="clientId" />
			
			<% if(uF.parseToInt(clientBrandId) > 0) {
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
						<tr><td colspan="4"><s:fielderror/>
							<input type="hidden" name="clientBrandCntId" id="clientBrandCntId<%=a %>" value="<%=a %>"/>
		        			<input type="hidden" name="strClientBrandId" value= "<%=innerList.get(0) %>"/>
						</td></tr>
						<!-- <tr><td colspan="4"><hr style="height: 1px; width: 100%; margin: 0px; background-color: gray;"></td></tr> -->
						<!-- <tr>
		        			<td colspan="2"><label>&nbsp;&nbsp;Add New Subsidiary/ Brand</label></td>
		        			<td colspan="2">&nbsp;
		        			</td>
		        		</tr> -->
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
									customerImage = docRetriveLocation +IConstants.I_CUSTOMER+"/"+clientId+"/"+IConstants.I_IMAGE+"/"+((strImage!=null && !strImage.equals("")) ? strImage:"company_avatar_photo.png");
								}
							%>
		                     	<img height="60" width="60" class="lazy" id="clientBrandLogoImg<%=a %>" style="border: 1px solid #CCCCCC; border: 1px lightgray solid; margin-bottom: 2px;" src="<%=customerImage %>" data-original="<%=customerImage %>" />
		                    <%} %>
								<s:file name="clientBrandLogo" id="clientBrandLogo<%=a %>" onchange="readImageURL(this, 'clientBrandLogoImg<%=a %>');"></s:file>
							</td>
						
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
							<td class="txtlabel alignRight">Choose Industry:<%=validAsterix %>
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
				        			<td  class="alignRight" colspan="2" style="vertical-align: bottom;padding-right: 30px !important;">
				        				<a href="javascript:void(0)" onclick="addNewBrandSPOC('<%=a %>')" class="fa fa-fw fa-plus" title="Add Subsidiary/ Brand SPOC">&nbsp;</a>
				        				<% if(i>0) { %>
				        					<%-- <a href="javascript:void(0)" onclick="removeBrandSPOC('<%=a %>', this.id)" id="<%=i %>" class="fa fa-fw fa-remove" style="color: red;" title="Remove Subsidiary/ Brand SPOC">&nbsp;</a> --%>
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
				<input type="hidden" name="hideDesigOption" id="hideDesigOption" value="<%=(String)request.getAttribute("sbDesigAjax") %>" />
				<input type="hidden" name="hideDepartOption" id="hideDepartOption" value="<%=(String)request.getAttribute("sbDepartAjax") %>" />
				<input type="hidden" name="hideWLocOption" id="hideWLocOption" value="<%=(String)request.getAttribute("sbWLocAjax") %>" />
			<% } %>
				
			<% } else { %>
				<div id="clientBrand0" style="float: left; width: 100%;">
					<table border="0" class="table table_no_border">
						<tr><td colspan="4"><s:fielderror/>
						<input type="hidden" name="clientBrandCntId" id="clientBrandCntId0" value="0"/>
						</td></tr>
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
							<td class="txtlabel alignRight">Choose Industry:<%=validAsterix %>
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
					<input type="hidden" name="hideDesigOption" id="hideDesigOption" value="<%=(String)request.getAttribute("sbDesigAjax") %>" />
					<input type="hidden" name="hideDepartOption" id="hideDepartOption" value="<%=(String)request.getAttribute("sbDepartAjax") %>" />
					<input type="hidden" name="hideWLocOption" id="hideWLocOption" value="<%=(String)request.getAttribute("sbWLocAjax") %>" />
				</div>
			<% } %>
			
		<div class="clr"></div>
		<div style="margin:0px 0px 15px 210px">
		<table class="table table_no_border">
			<tr><td colspan="2" align="center">
				<% if(uF.parseToInt(clientBrandId) > 0) { %>
					<s:submit cssClass="btn btn-primary" value="Update" name="btnSave" id="btnSave"/>
					<s:submit cssClass="btn btn-danger" value="Delete" name="btnDelete" id="btnDelete"/>
				<% } else { %>
					<s:submit cssClass="btn btn-primary" value="Save" name="btnSave" id="btnSave"/>
				<% } %>
				<input type="button" value="Cancel" class="btn btn-danger" style="margin-right: 5px; padding: 3px;" name="cancel" onclick="closeForm('');">
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
