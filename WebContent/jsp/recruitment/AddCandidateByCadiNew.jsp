<%@page import="com.konnect.jpms.select.FillYears"%>
<%@page import="com.konnect.jpms.select.FillSkills"%>
<%@ taglib uri="http://htmlcompressor.googlecode.com/taglib/compressor" prefix="compress" %>
<%@page import="java.util.ArrayList"%>
<%@page import="com.konnect.jpms.select.FillDegreeDuration"%>
<%@page import="java.util.Map"%>
<%@page import="com.konnect.jpms.recruitment.FillEducational"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.konnect.jpms.select.FillGender"%>
<%@page import="com.konnect.jpms.util.*" %>
<%@page import="java.util.Calendar" %>
<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	
<%-- <jsp:include page="/jsp/common/Links.jsp" flush="true" /> --%>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>
<%@ taglib prefix="s" uri="/struts-tags" %> 
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
 
 <!-- ====start parvez on 01-07-2021==== -->
 	<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
 <!-- ====start parvez on 01-07-2021===== -->   
       
	<style>
		
	<g:compress>
	.row_language {
		float: left;
		width: 100%;
		margin: 5px 0px 5px 0px;
		padding: 5px 0px 5px;
		border-bottom: solid 1px #efefef;
	}
	
	.row_hobby{
		float: left;
		width: 100%;
		margin: 5px 0px 5px 0px;
		padding: 5px 0px 5px;
		border-bottom: solid 1px #efefef;
	}
	.row_education{
		float: left;
		width: 100%;
		margin: 5px 0px 5px 0px;
		padding: 5px 0px 5px;
		border-bottom: solid 1px #efefef;
	}
	
	.textfield_height{
		height: 25px;
	}
	
	/* ====start parvez 0n 02-07-2021===== */
	
	#div_language {
        height: 300px;
        border: solid 2px #F5F5F5;
        overflow: auto;
        margin: 10px 10px 10px 10px;
        padding-left: 5px;
		padding-right: 5px;
        }
	
	#div_education {
        height: 300px;
        border: solid 2px #F5F5F5;
        overflow: auto;
        margin: 10px 10px 10px 10px;
        padding-left: 5px;
		padding-right: 5px;
        }
	
	
	#div_skills {
        height: 300px;
        border: solid 2px #F5F5F5;
        overflow: auto;
        margin: 10px 10px 10px 10px;
        padding-left: 5px;
		padding-right: 5px;
        }
	
	#div_hobbies {
        height: 300px;
        border: solid 2px #F5F5F5;
        overflow: auto;
        margin: 10px 10px 10px 10px;
        padding-left: 5px;
		padding-right: 5px;
        }
	/* ====end parvez 0n 02-07-2021===== */
	</g:compress>
	
	</style>

	<%
	String struserType = (String)session.getAttribute(IConstants.USERTYPE);
	ArrayList educationalList = (ArrayList) request.getAttribute("educationalList"); 
	ArrayList alSkills = (ArrayList) request.getAttribute("alSkills"); 
	ArrayList alHobbies = (ArrayList) request.getAttribute("alHobbies");
	ArrayList alLanguages = (ArrayList) request.getAttribute("alLanguages");
	ArrayList alEducation = (ArrayList) request.getAttribute("alEducation");
	ArrayList alDocuments = (ArrayList) request.getAttribute("alDocuments");
	if(alDocuments== null) alDocuments = new ArrayList();
	
	ArrayList alSiblings = (ArrayList) request.getAttribute("alSiblings");
	ArrayList alPrevEmployment = (ArrayList) request.getAttribute("alPrevEmployment");
	List degreeDurationList = (List) request.getAttribute("degreeDurationList");
	List yearsList = (List) request.getAttribute("yearsList");
	List empGenderList = (List) request.getAttribute("empGenderList");
	List skillsList = (List) request.getAttribute("skillsList");
	String strImage = (String) request.getAttribute("strImage");
	
	//ArrayList alOtherDocuments = (ArrayList) request.getAttribute("alOtherDocuments"); 
	//int documentcnt  = alDocuments.size();
	
	HashMap empServicesMap = (HashMap) request.getAttribute("empServicesMap");
	
	UtilityFunctions uF = new UtilityFunctions();
	String currentYear = (String)request.getAttribute("currentYear");
	
	CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
	int nEmpAlphaCodeLength = 2;
	if(CF!=null && CF.getStrOEmpCodeAlpha()!=null) {
		nEmpAlphaCodeLength = CF.getStrOEmpCodeAlpha().length();
	}
	
	String strUserType=(String)session.getAttribute(IConstants.USERTYPE);
	String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
	
	int docCount = 0;
	if(alDocuments != null && alDocuments.size()>0 ) {
		docCount = alDocuments.size();
	}
	
	Map<String, List<String>> hmValidationFields = (Map<String, List<String>> ) request.getAttribute("hmValidationFields");
	String validReqOpt = "";
	String validAsterix = "";
	%>

<script>
    $(function(){
    	$("input[name='stepSubmit']").click(function(){
    		$("#"+ this.form.id ).find('.validateRequired').filter(':hidden').prop('required',false);
    		$("#"+ this.form.id ).find('.validateRequired').filter(':visible').prop('required',true);
    		$("#"+ this.form.id ).find('.validateEmail').filter(':visible').attr('type','email');
    	});

        /* Start Dattatray Date:26-08-21 Note:Added endate in all date and also set start date and completion date validation*/
        $("#strHighestDegreeStartDate").datepicker({
	    	format : 'dd/mm/yyyy',endDate:'+0d'
	    });
        $("#strHighestDegreeCompletionDate").datepicker({
	    	format : 'dd/mm/yyyy',endDate:'+0d'
	    });

        $( "#strHighestDegreeStartDate" ).datepicker({format: 'dd/mm/yyyy', 
	    	changeYear: true
	    }).on('changeDate', function (selected) {
		    var minDate = new Date(selected.date.valueOf());
		    $('#strHighestDegreeCompletionDate').datepicker('setStartDate', minDate);
		    $('#strHighestDegreeCompletionDate').datepicker('setEndDate', '+0d');
		});

	    
        $("#strGraduateDegreeStartDate").datepicker({
	    	format : 'dd/mm/yyyy',endDate:'+0d'
	    });
        $("#strGraduateDegreeCompletionDate").datepicker({
	    	format : 'dd/mm/yyyy',endDate:'+0d'
	    });

        $( "#strGraduateDegreeStartDate" ).datepicker({format: 'dd/mm/yyyy', 
	    	changeYear: true
	    }).on('changeDate', function (selected) {
		    var minDate = new Date(selected.date.valueOf());
		    $('#strGraduateDegreeCompletionDate').datepicker('setStartDate', minDate);
		    $('#strGraduateDegreeCompletionDate').datepicker('setEndDate', '+0d');
		});
		
        $("#strHSCStartDate").datepicker({
	    	format : 'dd/mm/yyyy',endDate:'+0d'
	    });
        $("#strHSCCompletionDate").datepicker({
	    	format : 'dd/mm/yyyy',endDate:'+0d'
	    });

        $( "#strHSCStartDate" ).datepicker({format: 'dd/mm/yyyy', 
	    	changeYear: true
	    }).on('changeDate', function (selected) {
		    var minDate = new Date(selected.date.valueOf());
		    $('#strHSCCompletionDate').datepicker('setStartDate', minDate);
		    $('#strHSCCompletionDate').datepicker('setEndDate', '+0d');
		});
	    
        $("#strSSCStartDate").datepicker({
	    	format : 'dd/mm/yyyy',endDate:'+0d'
	    });
        $("#strSSCCompletionDate").datepicker({
	    	format : 'dd/mm/yyyy',endDate:'+0d'
	    });

        $( "#strSSCStartDate" ).datepicker({format: 'dd/mm/yyyy', 
	    	changeYear: true
	    }).on('changeDate', function (selected) {
		    var minDate = new Date(selected.date.valueOf());
		    $('#strSSCCompletionDate').datepicker('setStartDate', minDate);
		    $('#strSSCCompletionDate').datepicker('setEndDate', '+0d');
		});

        /*  Created By Dattatray Date:26-08-21 Note Year picker */
        $( "#certificationCompletionYear" ).datepicker({
                    format: "yyyy",
                    viewMode: "years",
                    minViewMode: "years", 
                    endDate:'+0d',
                    changeYear: true
        });
        
        /* End Dattatray Date:26-08-21 */
        
        $("#strWLocation").multiselect().multiselectfilter();
        
        Captcha();
    });

</script>



<g:compress>


<script>  
function validateMandatory(value){
	if(value=='AT' || value=='CO'){
		document.getElementById("desigId").style.display = 'none';
		document.getElementById("gradeId").style.display = 'none';
		
		document.getElementById("desigIdV").className = '';
		document.getElementById("gradeIdV").className = '';
	}else{
		document.getElementById("desigId").style.display = 'inline';
		document.getElementById("gradeId").style.display = 'inline';
		
		document.getElementById("desigIdV").className = 'validateRequired';
		document.getElementById("gradeIdV").className = 'validateRequired';
	}
}


//====start parvez on 02-07-2021=====

function callDatePicker() {
	//alert("callDatePicker ");
    $( "#empDateOfBirth" ).datepicker({dateFormat: 'dd/mm/yy', yearRange: '1950:2000', changeYear: true});
    $( "#empDateOfMarriage" ).datepicker({dateFormat: 'dd/mm/yy', yearRange: '1970:<%=currentYear%>', changeYear: true});
    $("input[name=prevCompanyFromDate]").datepicker({dateFormat: 'dd/mm/yy', yearRange: '1950:<%=currentYear%>', changeYear: true});
    $("input[name=prevCompanyToDate]").datepicker({dateFormat: 'dd/mm/yy', yearRange: '1950:<%=currentYear%>', changeYear: true});
    $("input[name=fatherDob]").datepicker({dateFormat: 'dd/mm/yy', yearRange: '1950:<%=currentYear%>', changeYear: true});
    $("input[name=motherDob]").datepicker({dateFormat: 'dd/mm/yy', yearRange: '1950:<%=currentYear%>', changeYear: true});
    $("input[name=spouseDob]").datepicker({dateFormat: 'dd/mm/yy', yearRange: '1950:<%=currentYear%>', changeYear: true});
    $("input[name=memberDob]").datepicker({dateFormat: 'dd/mm/yy', yearRange: '1950:<%=currentYear%>', changeYear: true});
    $("input[name=empPassportExpiryDate]").datepicker({dateFormat: 'dd/mm/yy', yearRange: '<%=currentYear%>:2020', changeYear: true});
}
//====end parvez on 02-07-2021=====

$(function() {
	//===start parvez on 01-07-2021==== 
	$("input[name=strDate]").datepicker({format: 'dd/mm/yy'});
	$( "input[name=strTime]" ).datetimepicker({format: 'HH:mm'});
	//====end parvez on 01-07-2021===== 
	
});



function showHideHiddenField(fieldId) {
	
	if(document.getElementById(fieldId).checked) {
		document.getElementById( "hidden_"+fieldId).value="1";
	}else {
		document.getElementById( "hidden_"+fieldId).value="0";
	}
}



/* ===start parvez on 05-08-2021=== */
function submitForm(){
	
	document.getElementById('frmAvailibility').submit();
	
	window.setTimeout(function() {  
		parent.window.location="Applications.action";
		}, 500); 
}
/* ===end parvez on 05-08-2021=== */

function checkMailID(value) {
	//alert("value ===> "+value);
     xmlhttp = GetXmlHttpObject();
     if (xmlhttp == null) {
             alert("Browser does not support HTTP Request");
             return;
     } else {
             var xhr = $.ajax({
               url : "EmailValidation.action?candiEmail=" + value,
               cache : false,
               success : function(data) {
               	//alert("data.length ===> "+data.length + "  data ===> "+data);
               	if(data.length > 1){
               		document.getElementById("empEmail").value = "";
                    document.getElementById("emailValidatorMessege").innerHTML = data;
               	}else{
               		document.getElementById("emailValidatorMessege").innerHTML = data;
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

function isNumberKey(evt) {
	   var charCode = (evt.which) ? evt.which : event.keyCode;
	   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46)
	      return false;

	   return true;
	}
function Captcha(){
    var alpha = new Array('0','1','2','3','4','5','6','7','8','9',
'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z',
'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z');
    var i;
    for (i=0;i<6;i++){
      var a = alpha[Math.floor(Math.random() * alpha.length)];
      var b = alpha[Math.floor(Math.random() * alpha.length)];
      var c = alpha[Math.floor(Math.random() * alpha.length)];
      var d = alpha[Math.floor(Math.random() * alpha.length)];
      var e = alpha[Math.floor(Math.random() * alpha.length)];
      var f = alpha[Math.floor(Math.random() * alpha.length)];
      var g = alpha[Math.floor(Math.random() * alpha.length)];
     }
   var code = a + ' ' + b + ' ' + ' ' + c + ' ' + d + ' ' + e + ' '+ f + ' ' + g;
//   document.getElementById("mainCaptcha").value = code
	document.getElementById("mainCaptcha").innerHTML = ""+code;
 }
 function ValidCaptcha(){
     var string1 = removeSpaces(document.getElementById('mainCaptcha').innerHTML);
     var string2 = removeSpaces(document.getElementById('txtInput').value);
     if (string1 == string2){
       return true;
     }
     else{        
       return false;
     }
 }
 function removeSpaces(string){
   return string.split(' ').join('');
 }
 
 function readFileURL(input, targetDiv) {
   	fileValidation();// Created by Dattatray Date:25-June-2021
       if (input.files && input.files[0]) {
           var reader = new FileReader();
           reader.onload = function (e) {
               $('#'+targetDiv).attr('path', e.target.result);
           };
           reader.readAsDataURL(input.files[0]);
       }
   }
   
 //=====start parvez on 28/06/2021====
   function showReferal(strValue){
   	//alert("ShowReferal() call===>> "+strValue.value);
   	if(strValue=="2"){
   		document.getElementById("empReferenceId").style.display = 'table-cell';
   		document.getElementById("empReferenceId1").style.display = 'table-cell';
   	}else{
   		document.getElementById("empReferenceId").style.display = 'none';
   		document.getElementById("empReferenceId1").style.display = 'none';
   	}
   }
   //====end parvez on 28/06/2021====
   
   
   // Create by Dattatray Note: File allowed given format pdf,docx,docs or doc - Date:25-June-2021
   function fileValidation() {
       var fileInput = document.getElementById('idDoc0');
       var filePath = fileInput.value;
       var allowedExtensions = /(\.doc|\.docs|\.docx|\.pdf)$/i;
         
       if (!allowedExtensions.exec(filePath)) {
           alert('Please select pdf,docx,docs or doc format');
           fileInput.value = '';
           return false;
       } 
   }
   
   function checkEmployeeCode(value) {
		//alert("value ===> "+value);
		if(value.trim() == '') {
			document.getElementById("isEmpCode").value = '';
	       	document.getElementById("refEmpId").value = '';
	    	document.getElementById("empIdMsgSpan").innerHTML = '';
		} else {
		     xmlhttp = GetXmlHttpObject();
		     if (xmlhttp == null) {
		             alert("Browser does not support HTTP Request");
		             return;
		     } else {
		             var xhr = $.ajax({
		               url : "EmailValidation.action?empCode="+value,
		               cache : false,
		               success : function(data) {
		               	//alert("data.length ===> "+data.length + "  data ===> "+data);
		               	var allData = data.split("::::");
		               	document.getElementById("isEmpCode").value = allData[0];
		               	document.getElementById("refEmpId").value = allData[1];
		            	document.getElementById("empIdMsgSpan").innerHTML = allData[2];
		               }
		             });
		     	}
			}
		}
  
   function checkRatingValue(id,msg) {
  		var val = document.getElementById(id).value;
  		if(parseInt(val) > 10 || parseInt(val) <= 0){
			alert(msg);
			document.getElementById(id).value="";
		}
  	}

  
  
</script>

</g:compress>

<script>
     /*  jQuery(document).ready(function(){
          // binds form submission and fields to the validation engine
          jQuery("#frmPersonalInfo").validationEngine();
      }); */
      
      function checkYear(id){
		   var Year = document.getElementById(id).value;
		   var currentYear = now.getFullYear();
		   if(parseInt(Year) > parseInt(currentYear)){
			   document.getElementById(id).value="";
		   }
	   }
      var cnt=0;
	


	function addSkills() {
		cnt++;
		var divTag = document.createElement("div");
	    divTag.id = "row_skill"+cnt;
	    divTag.setAttribute("class", "row_skill");
	    divTag.setAttribute("style", "padding: 5px 0px 0px 0px; margin: 0px;");
	    /* Started By Dattatray Date:25-08-21 */
	    <% 	List<String> candiSkillNameValidList = hmValidationFields.get("CANDI_PRIMARY_SKILL_NAME");
		System.out.println("candiSkillNameValidList : "+candiSkillNameValidList);
					validReqOpt = "";
					validAsterix = "";
			if(candiSkillNameValidList != null && uF.parseToBoolean(candiSkillNameValidList.get(0))) {
				validReqOpt = "validateRequired";
				validAsterix = "<sup>*</sup>";
			}
			
			String validReqOpt1= "";
			String validAsterix1= "";
			List<String> candiSkillRatingValidList = hmValidationFields.get("CANDI_PRIMARY_SKILL_RATING"); //Created By Dattatray Date:25-08-21 Note Key Changed CANDI_PRIMARY_SKILL_RATING
			if(candiSkillRatingValidList != null && uF.parseToBoolean(candiSkillRatingValidList.get(0))) {
				validReqOpt1 = "validateRequired";
				validAsterix1 = "<sup>*</sup>";
			}
		%>
		var validReqOpt = '<%=validReqOpt %>';
		var validReqOpt1 = '<%=validReqOpt1 %>';
		<!-- Created by Dattatray Date:25-08-21 Note: placeholder added -->
		divTag.innerHTML = 	"<input type=\"text\" name=\"skillName\" class=\""+validReqOpt+"\"/>"+
							"<input type=\"text\" name=\"skillValue\" placeholder=\"rating\" class=\""+validReqOpt1+"\" style=\"margin-left: 10px; width: 50px !important;\" onkeyup=\"checkRatingValue(this.id,'Please skill rating between 1 to 10');\" onkeypress=\"return isOnlyNumberKey(event)\"/>"+/* Created By Dattatray date:26-08-21 Note: isOnlyNumberKey*/
	    			    	"<a href=\"javascript:void(0)\" onclick=\"addSkills()\" style=\"margin-left: 4px;\" class=\"add-font\"></a>" +
	    			    	"<a href=\"javascript:void(0)\" onclick=\"removeSkills(this.id)\" id=\""+cnt+"\" class=\"remove-font\"></a>"; 
	    			    	/* end By Dattatray Date:25-08-21 */
	    			    	document.getElementById("div_skills").appendChild(divTag);
	}

	function addSkills1() {
		cnt++;
		var divTag = document.createElement("div");
	    divTag.id = "row_skill_s"+cnt;
	    divTag.setAttribute("class", "row_skill_s");
	    divTag.setAttribute("style", "padding: 5px 0px 0px 0px; margin: 0px;");
	    /* Started By Dattatray Date:25-08-21 */
	    <% 	
    	List<String> candiSkillNameValidList1 = hmValidationFields.get("CANDI_SECONDARY_SKILL_NAME"); 
								validReqOpt = "";
						if(candiSkillNameValidList1 != null && uF.parseToBoolean(candiSkillNameValidList1.get(0))) {
							validReqOpt = "validateRequired";
			}
						
				String validReqOpt11= "";
				List<String> candiSkillRatingValidList1 = hmValidationFields.get("CANDI_SECONDARY_SKILL_RATING"); 
				if(candiSkillRatingValidList1 != null && uF.parseToBoolean(candiSkillRatingValidList1.get(0))) {
					validReqOpt11 = "validateRequired";
				}
	%>
		var validReqOpt = '<%=validReqOpt %>';
		var validReqOpt11 = '<%=validReqOpt11 %>';
		<!-- Created by Dattatray Date:25-08-21 Note: placeholder added -->
		divTag.innerHTML = 	"<input type=\"text\" name=\"skillName\" class=\""+validReqOpt+"\"/>"+
							"<input type=\"text\" name=\"skillValue\" placeholder=\"rating\" class=\""+validReqOpt11+"\" style=\"margin-left: 10px; width: 50px !important;\" onkeyup=\"checkRatingValue(this.id,'Please skill rating between 1 to 10');\" onkeypress=\"return isOnlyNumberKey(event)\"/>"+/* Created By Dattatray date:26-08-21 Note: isOnlyNumberKey*/
	    			    	"<a href=\"javascript:void(0)\" onclick=\"addSkills1()\" style=\"margin-left: 4px;\" class=\"add-font\"></a>" +
	    			    	"<a href=\"javascript:void(0)\" onclick=\"removeSkills1(this.id)\" id=\""+cnt+"\" class=\"remove-font\"></a>"; 
	    			    	/* End By Dattatray Date:25-08-21 */
	    document.getElementById("div_skills_s").appendChild(divTag);
	}

	function removeSkills(removeId) {
		var remove_elem = "row_skill"+removeId;
		var row_skill = document.getElementById(remove_elem); 
		document.getElementById("div_skills").removeChild(row_skill);
		
	}

	function removeSkills1(removeId) {
		var remove_elem = "row_skill_s"+removeId;
		var row_skill = document.getElementById(remove_elem); 
		document.getElementById("div_skills_s").removeChild(row_skill);
		
	}
	
	function addCertifications(rowCnt, rowIndex1){
		/* cnt++; */  
		rowCnt++;
	    var certicount = document.getElementById("certicount").value;//Created by Dattatray Date:26-08-21 Note:name changed
	    var cnt1=(parseInt(certicount)+1);
		/* alert("cnt1 : "+cnt1); */
		var val=(parseInt(rowIndex1)+1);
		/* alert("val : "+val);
		alert("rowCnt : "+rowCnt); */
	    var table = document.getElementById("candidateTable");
	    var rowCount = table.rows.length;
	    var row = table.insertRow(val);
	    
	    row.id="cert_title"+cnt1;
	    var cell0 = row.insertCell(0);
	    /* Started By Dattatray Date:25-08-21 */
	    <% 	List<String> titleList1 = hmValidationFields.get("CANDI_CERTIFICATION_TITLE"); 
		validReqOpt = "";
		validAsterix = "";
		if(titleList1 != null && uF.parseToBoolean(titleList1.get(0))) {
			validReqOpt = "validateRequired";
			validAsterix = "<sup>*</sup>";
		}
		%>
		var validAsterix='<%= validAsterix%>';
		var validReqOpt='<%= validReqOpt%>';
	    cell0.setAttribute("class", "txtlabel alignRight ");
	    
	   	cell0.innerHTML = "Title:"+validAsterix;
	    /* cell0.innerHTML = 	"<td class=\"txtlabel alignRight\">Title : </td><td><input type=\"text\" name=\"candiTitle\" id=\"candiTitle"+cnt1+"\"/>" +
							"<input style=\"margin-left: 10px; width: 50px !important;\" type=\"text\" name=\"candiTitleValue\" onkeypress=\"return isNumberKey(event)\"/>"+
	    			    	"<a href=\"javascript:void(0)\" onclick=\"addCertifications('"+cnt1+"',this.parentNode.parentNode.rowIndex)\" class=\"add-font\"></a>" +
	    			    	"<a href=\"javascript:void(0)\" onclick=\"removeCertification(this.id)\" id=\""+cnt1+"\" class=\"remove-font\"></a></td>"; */
	    
	    			    	
						
	    var cell1 = row.insertCell(1);
	    				    // Created By Dattatray Date:25-08-21 Note placeholder added
	    				    <!-- Created By Dattatray date:26-08-21 Note: onkeypress removed and readonly="readonly" added and id year -->
	    cell1.innerHTML="<input type=\"text\" name=\"certificationTitle\" class=\""+validReqOpt+"\" id=\"certificationTitle"+cnt1+"\"/><input style=\"margin-left: 14px; width: 50px !important;\" type=\"text\" placeholder=\"year\" name=\"certificationCompletionYear\" id=\"certificationCompletionYear"+cnt1+"\" readonly=\"readonly\"/><a style=\"padding-left: 3px;\" href=\"javascript:void(0)\" onclick=\"addCertifications('"+cnt1+"',this.parentNode.parentNode.rowIndex)\" class=\"add-font\"></a><a href=\"javascript:void(0)\" onclick=\"removeCertification(this.id)\" id=\""+cnt1+"\" class=\"remove-font\"></a>";
	    /* End By Dattatray Date:25-08-21 */
	    /* cell1.innerHTML = "<input style=\"margin-left: 10px; width: 50px !important;\" type=\"text\" name=\"candiTitleValue\" onkeypress=\"return isNumberKey(event)\"/>"; */
	    /* cell1.innerHTML = "<input style=\"margin-left: 10px; width: 50px !important;\" type=\"text\" name=\"candiTitleValue\" onkeypress=\"return isNumberKey(event)\"/>"; */
	    /* cell1.innerHTML = "<td class=\"txtlabel alignRight\"><b>Location: </b></td>"+
							"<td><input type=\"text\" name=\"location\"</td>"; */
							/* Started By Dattatray Date:25-08-21 */
							<% 	List<String> locationList1 = hmValidationFields.get("CANDI_CERTIFICATION_LOCATION");//Created by Dattatray Date:21-08-21 Note: chnaged CANDI_LOCATION to CANDI_CERTIFICATION_LOCATION
							validReqOpt = "";
							validAsterix = "";
							if(locationList1 != null && uF.parseToBoolean(locationList1.get(0))) {
								validReqOpt = "validateRequired";
								validAsterix = "<sup>*</sup>";
							}
						%>
						var validAsterix1='<%= validAsterix%>';
						var validReqOpt1='<%= validReqOpt%>';
	     var cell2 = row.insertCell(2);
		 cell2.setAttribute("class", "txtlabel alignRight");
		 cell2.setAttribute("style", "font-weight:bold;");
		 cell2.innerHTML = "Location:"+validAsterix1;
	     
	     var cell3 = row.insertCell(3);
		 cell3.innerHTML = "<input type=\"text\"  class=\""+validReqOpt1+"\" name=\"location\"/>";
		 /* End By Dattatray Date:25-08-21 */
		 /* Start By Dattatray Date:26-08-21 */
		document.getElementById("certicount").value = cnt1;
		 $( "#certificationCompletionYear"+cnt1).datepicker({
             format: "yyyy",
             viewMode: "years",
             minViewMode: "years", 
             endDate:'+0d',
             changeYear: true
 });
		 /* End By Dattatray Date:25-08-21 */
		 
	}
	
	function removeCertification(removeId) {
		
		var trIndex = document.getElementById("cert_title"+removeId).rowIndex;
	    document.getElementById("candidateTable").deleteRow(trIndex);
		
	}

</script>


<section class="content">
    <div class="row jscroll">
        <section class="col-lg-12 connectedSortable">
        	<div class="box box-primary">
				<div class="box-header with-border">
                    <h4 class="box-title">
						<%if(session.getAttribute(IConstants.USERID)!=null) { 
						//System.out.println("Operation===> "+request.getParameter("operation"));
						%>
							<%=(request.getParameter("operation")!=null && !request.getParameter("operation").equals(""))?"Edit":"Enter" %> Candidate Detail
						<% } else { %>
							Enter your Details
						<%}%>
					</h4>
				</div>

<%
	String strEmpType = (String) session.getAttribute("USERTYPE");
	String strMessage = (String) request.getAttribute("MESSAGE");
	if (strMessage == null) {
		strMessage = "";
	}
	
	
%>
<p class="message"><%=strMessage%></p>
<div class="panes" style="padding-left: 15px; padding-right: 15px;">
		<div >
		<s:form theme="simple" action="AddCandidateByCadiNew" name="frmPersonalInfo" id="frmPersonalInfo" method="POST" cssClass="formcss" enctype="multipart/form-data">
			<div style="float: center;" >
			
			<table border="0" class="table table_no_border form-table" id="candidateTable">
			<tr>
			<% 	List<String> candiSalutationValidList = hmValidationFields.get("CANDI_SALUTATION"); 
				validReqOpt = "";
				validAsterix = "";
				if(candiSalutationValidList != null && uF.parseToBoolean(candiSalutationValidList.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
				}
			%>
			<td class="txtlabel alignRight">Salutation:<%=validAsterix %></td>
			<td>
			<%if(session.getAttribute("isApproved")==null) {%>
				<% if(candiSalutationValidList != null && uF.parseToBoolean(candiSalutationValidList.get(0))) { %>
				<s:select list="salutationList" name="salutation" headerKey="" headerValue="Select Salutation" listKey="salutationId" listValue="salutationName" cssClass="validateRequired"></s:select>
			<% } else { %>
				<s:select list="salutationList" name="salutation" headerKey="" headerValue="Select Salutation" listKey="salutationId" listValue="salutationName"></s:select>
			<% } %>
				
			<%}else{%>
				<s:textfield name="salutation" required="true" disabled="true"/>
				<s:hidden name="salutation" />
			<%}%>
			<span class="hint">Employee's salutation.<span class="hint-pointer">&nbsp;</span></span></td></tr>
			
			<tr><td colspan=2><s:fielderror ><s:param>empFname</s:param></s:fielderror></td></tr>
			<tr>
			<% 	List<String> candiFNameValidList = hmValidationFields.get("CANDI_FIRST_NAME"); 
				validReqOpt = "";
				validAsterix = "";
				if(candiFNameValidList != null && uF.parseToBoolean(candiFNameValidList.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
				}
			%>
			<td class="txtlabel alignRight">First Name:<%=validAsterix %></td>
			<td>
			<%if(session.getAttribute("isApproved")==null) {%>
				<input type="text" name="empFname" class="<%=validReqOpt %>" style="height: 25px; width: 206px;" />
			<%}else{%>
				<s:textfield name="empFname" required="true" disabled="true"/>
				<s:hidden name="empFname" />
			<%}%>
			<span class="hint">Candidate's first name.<span class="hint-pointer">&nbsp;</span></span></td><!-- </tr> -->
						
			<% 	List<String> candiLNameValidList = hmValidationFields.get("CANDI_LAST_NAME"); 
				validReqOpt = "";
				validAsterix = "";
				if(candiLNameValidList != null && uF.parseToBoolean(candiLNameValidList.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
				}
			%>
			<td class="txtlabel alignRight"><b>Last Name:<%=validAsterix %></b></td>
			<td>
			<%if(session.getAttribute("isApproved")==null) {%>
				<input type="text" name="empLname"  class="<%=validReqOpt %>" style="height: 25px; width: 206px;"/>
			<%}else{%>
				<s:textfield name="empLname" required="true" disabled="true"/>
				<s:hidden name="empLname" />
			<%}%>
			<span class="hint">Candidate's last name.<span class="hint-pointer">&nbsp;</span></span></td></tr>
			
			<tr><td colspan=2><s:fielderror ><s:param>empEmail</s:param></s:fielderror></td></tr>
			<tr>
			<% 	List<String> candiPersonalEmailIdValidList = hmValidationFields.get("CANDI_PERSONAL_EMAIL_ID"); 
				validReqOpt = "";
				validAsterix = "";
				if(candiPersonalEmailIdValidList != null && uF.parseToBoolean(candiPersonalEmailIdValidList.get(0))) {
					validReqOpt = "validate[required,custom[email]]";
					validAsterix = "<sup>*</sup>";
				}
			%>
			<td class="txtlabel alignRight" >Personal Email Id:<%=validAsterix %></td>
			<td>
			
			<%if(session.getAttribute("isApproved")==null) {%>
				<input type="text" name="empEmail" id="empEmail" class="<%=validReqOpt %>" style="height: 25px; width: 206px;"  onchange="checkMailID(this.value);"/>  <!-- getContent('emailValidatorMessege','EmailValidation.action?candiEmail='+this.value) -->
			<%}else{%>
				<s:textfield name="empEmail" id="empEmail" required="true" disabled="true"/>
				<s:hidden name="empEmail" id="empEmail"/>
			<%}%>
			<br/>
			<span id="emailValidatorMessege" style="font-size: 12px;"></span><!-- Created By Dattatray Date:26-08-21 Note:email validation set   -->
			<span class="hint">Email id is required as the user will received all information on this id.<span class="hint-pointer">&nbsp;</span></span></td>
			<!-- <tr> -->
			<% 	List<String> candiMobileNoValidList1 = hmValidationFields.get("CANDI_MOBILE_NO"); 
				validReqOpt = "";
				validAsterix = "";
				if(candiMobileNoValidList1 != null && uF.parseToBoolean(candiMobileNoValidList1.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
				}
			%>
			<td class="txtlabel alignRight"><b>Mobile Number:<%=validAsterix %></b></td>
			<td><input type="text" name="empMobileNo" class="<%=validReqOpt %>" style="height: 25px; width: 206px;" onkeypress="return isNumberKey(event)"/><span class="hint">Candidate's Mobile No<span class="hint-pointer">&nbsp;</span></span></td>
			<!-- </tr> -->
			</tr>
			
			<tr>
					<% 	List<String> candiPrimarySkillName = hmValidationFields.get("CANDI_PRIMARY_SKILL_NAME"); 
								validReqOpt = "";
								validAsterix = "";
						if(candiPrimarySkillName != null && uF.parseToBoolean(candiPrimarySkillName.get(0))) {
							validReqOpt = "validateRequired";
							validAsterix = "<sup>*</sup>";
						}
						
						String validPrimaryReqOpt1= "";
						String validPrimaryAsterix1= "";
						List<String> candiPrimarySkillRatingValidList = hmValidationFields.get("CANDI_PRIMARY_SKILL_RATING"); 
						System.out.println("candiPrimarySkillRatingValidList : "+candiPrimarySkillRatingValidList);
						if(candiPrimarySkillRatingValidList != null && uF.parseToBoolean(candiPrimarySkillRatingValidList.get(0))) {
							validPrimaryReqOpt1 = "validateRequired";
							validPrimaryAsterix1 = "<sup>*</sup>";// Created By Dattatray date:26-08-21 Note: validPrimaryAsterix1 changed
						}
					%>
						<td class="txtlabel alignRight" valign="top">Primary Skill:<%=validAsterix1 %></td>
						<td>
						<div  id="div_skills" style="padding: 0px; margin: 0px;  height: auto; font-size: 12px;border-color: transparent;"><!-- width: 390px; -->
						<div id="row_skill" class="row_skill" style="padding: 0px; margin: 0px;">
		                 <input type="text" name="skillName" class="<%=validReqOpt%>"  value=""/>
		                        	<!-- Created by Dattatray Date:25-08-21 Note: placeholder added -->
		                        	<!-- Created By Dattatray date:26-08-21 Note: isOnlyNumberKey -->
		                 <input type="text"  placeholder="rating" name="skillValue" id="skillValue" class="<%=validPrimaryReqOpt1%>" style="margin-left: 8px; width: 50px !important;" onkeypress="return isOnlyNumberKey(event)" onkeyup="checkRatingValue(this.id,'Please skill rating between 1 to 10');"/>
		                 <a href="javascript:void(0)" onclick="addSkills()" class="add-font"></a>
						</div>
						
						</div>
						</td>
						
						<% 	List<String> candiSecSkillNameValidList1 = hmValidationFields.get("CANDI_SECONDARY_SKILL_NAME"); 
								validReqOpt = "";
								validAsterix = "";
						if(candiSecSkillNameValidList1 != null && uF.parseToBoolean(candiSecSkillNameValidList1.get(0))) {
							validReqOpt = "validateRequired";
							validAsterix = "<sup>*</sup>";
						}
						
						String validSecReqOpt11= "";
						String validSecAsterix11= "";
						List<String> candiSecSkillRatingValidList1 = hmValidationFields.get("CANDI_SECONDARY_SKILL_RATING"); 
						if(candiSecSkillRatingValidList1 != null && uF.parseToBoolean(candiSecSkillRatingValidList1.get(0))) {
							validSecReqOpt11 = "validateRequired";
							validSecAsterix11 = "<sup>*</sup>";
						}
					%>
						<td class="txtlabel alignRight" valign="top"><b>Secondary Skill:<%=validAsterix %></b></td>
						<td>
						<div  id="div_skills_s" style="padding: 0px; margin: 0px;  height: auto; max-height: 200px; font-size: 12px;"><!-- width: 390px; -->
						
						<div id="row_skill_s" class="row_skill_s" style="padding: 0px; margin: 0px;">
		                        	<input type="text" name="skillNameSec" class="<%=validReqOpt%>"  value=""/>
		                        	
		                        	<!-- Created by Dattatray Date:25-08-21 Note: placeholder added -->
		                        	<!-- Created By Dattatray date:26-08-21 Note: isOnlyNumberKey -->
		                        		<input type="text" name="skillValueSec" placeholder="rating" id="skillValueSec" class="<%=validSecReqOpt11%>"  value="" style="margin-left: 8px; width: 50px !important;" onkeypress="return isOnlyNumberKey(event)" onkeyup="checkRatingValue(this.id,'Please skill rating between 1 to 10');"/>
		                        	<a href="javascript:void(0)" onclick="addSkills1()" class="add-font"></a>
						</div>
						</div>
						</td>
						
						
						</tr>
						
						<tr>
					<% 	List<String> candiCurrentLocationList = hmValidationFields.get("CANDI_CURRENT_LOCATION"); 
						validReqOpt = "";
						validAsterix = "";
						if(candiCurrentLocationList != null && uF.parseToBoolean(candiCurrentLocationList.get(0))) {
							validReqOpt = "validateRequired";
							validAsterix = "<sup>*</sup>";
						}
					%>
					<td class="txtlabel alignRight">Current Location:<%=validAsterix %></td>
					<td>
						<input type="text" name="currentLocation" class="<%=validReqOpt %>" />
					</td>
					
					<% 	List<String> candiLNameValidList11 = hmValidationFields.get("CANDI_PREFFERED_LOCATION"); 
						validReqOpt = "";
						validAsterix = "";
						if(candiLNameValidList11 != null && uF.parseToBoolean(candiLNameValidList11.get(0))) {
							validReqOpt = "validateRequired";
							validAsterix = "<sup>*</sup>";
						}
					%>
					<td class="txtlabel alignRight" valign="top"><b>Preferred location:</b><%=validAsterix %></td>
			         	<td>
			         	<!-- Start Dattatray Date:25-08-21 -->
			         	<%
			         	if(candiLNameValidList11 != null && uF.parseToBoolean(candiLNameValidList11.get(0))) {
			         	%>
			         		<s:select theme="simple" name="strWLocation" id="strWLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" key="" multiple="true" cssClass="validateRequired"/>
			         	<% }else{ %>
			         		<s:select theme="simple" name="strWLocation" id="strWLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" key="" multiple="true" cssClass=""/>
			         	<%} %>
			         	<!-- End Dattatray Date:25-08-21 -->
			         	</td>
					
					</tr>
					<tr><td class="txtlabel alignRight">Certification, If Any</td></tr>
					<%						
						int i=0;
					%>
					<div>
						<input type="hidden" name="certicount" id="certicount" value="<%=i %>" /><!-- Created by Dattatray Date:26-08-21 Note:name changed -->
					</div>
				<tr id="cert_title<%=i%>">
					<% 	List<String> titleList = hmValidationFields.get("CANDI_CERTIFICATION_TITLE"); 
						validReqOpt = "";
						validAsterix = "";
						if(titleList != null && uF.parseToBoolean(titleList.get(0))) {
							validReqOpt = "validateRequired";
							validAsterix = "<sup>*</sup>";
						}
					%>
					<td class="txtlabel alignRight">Title:<%=validAsterix %></td>
						<td>
		                    <input type="text" name="certificationTitle" class="<%=validReqOpt%>"  value=""/>
		                    <!-- Created by Dattatray Date:25-08-21 Note: placeholder added -->
		                    <!-- Created By Dattatray date:26-08-21 Note: readonly="readonly"-->
		                    <input style="margin-left: 10px; width: 50px !important;" type="text" placeholder="year" name="certificationCompletionYear" id="certificationCompletionYear" class="<%=validReqOpt %>" readonly="readonly"/>
		                    <a href="javascript:void(0)" onclick="addCertifications('<%=i %>', this.parentNode.parentNode.rowIndex);" class="add-font"></a>
		                </td>
					<% 	List<String> locationList = hmValidationFields.get("CANDI_CERTIFICATION_LOCATION");//Created by Dattatray Date:21-08-21 Note: chnaged CANDI_LOCATION to CANDI_CERTIFICATION_LOCATION
						validReqOpt = "";
						validAsterix = "";
						if(locationList != null && uF.parseToBoolean(locationList.get(0))) {
							validReqOpt = "validateRequired";
							validAsterix = "<sup>*</sup>";
						}
					%>
					<td class="txtlabel alignRight"><b>Location:</b><%=validAsterix %></td>
					<td><input type="text" name="location" class="<%=validReqOpt %>"/></td>
					<%
						i++;					
					%>
			</tr>
			
			<!-- Highest Degree Section  -->
			<tr>
					<% 	List<String> hightesDegreeList = hmValidationFields.get("CANDI_HIGHEST_DEGREE"); 
						validReqOpt = "";
						validAsterix = "";
						if(hightesDegreeList != null && uF.parseToBoolean(hightesDegreeList.get(0))) {
							validReqOpt = "validateRequired";
							validAsterix = "<sup>*</sup>";
						}
					%>
					<td class="txtlabel alignRight">Highest Degree (PG):<%=validAsterix %></td>
					<td>
						<input type="text" name="strHighestDegree" class="<%=validReqOpt %>" />
					</td>
					<% 	List<String> subjectList = hmValidationFields.get("CANDI_HIGHEST_DEGREE_SUBJECT"); 
						validReqOpt = "";
						validAsterix = "";
						if(subjectList != null && uF.parseToBoolean(subjectList.get(0))) {
							validReqOpt = "validateRequired";
							validAsterix = "<sup>*</sup>";
						}
					%>
					<td class="txtlabel alignRight"><b>Subject:</b><%=validAsterix %></td>
					<td><input type="text" name="strHighestDegreeSubject" class="<%=validReqOpt %>"/></td>
			</tr>
			<tr>
			 <%
                    String effectiveDate="";
                    if(request.getAttribute("strHighestDegreeStartDate")==null) {
                    	effectiveDate="";
                    } else {
                    	effectiveDate=(String)request.getAttribute("strHighestDegreeStartDate");
                    }
				%>
			<%
		            List<String> reqEffectiveDate = hmValidationFields.get("CANDI_HIGHEST_DEGREE_START_DATE"); 
					validAsterix = "";
					validReqOpt = "";
					if(reqEffectiveDate != null && uF.parseToBoolean(reqEffectiveDate.get(0))) {
						validAsterix = "<sup>*</sup>";
						validReqOpt = "validateRequired";
					}
	           	%>
                <td class="txtlabel alignRight"><b>Start Date:<%=validAsterix %></b></td>
                <td><input type="text" name="strHighestDegreeStartDate" id="strHighestDegreeStartDate" class="<%=validReqOpt %>"/></td>
                
                 <%
                    String completionDate="";
                    if(request.getAttribute("strHighestDegreeCompletionDate")==null) {
                    	completionDate="";
                    } else {
                    	completionDate=(String)request.getAttribute("strHighestDegreeCompletionDate");
                    }
				%>
			<%
		            List<String> reqCompletionDate = hmValidationFields.get("CANDI_HIGHEST_DEGREE_COMPLETION_DATE"); 
					validAsterix = "";
					validReqOpt = "";
					if(reqEffectiveDate != null && uF.parseToBoolean(reqCompletionDate.get(0))) {
						validAsterix = "<sup>*</sup>";
						validReqOpt = "validateRequired";
					}
	           	%>
                <td class="txtlabel alignRight"><b>Completion Date:<%=validAsterix %></b></td>
                <td><input type="text" name="strHighestDegreeCompletionDate" id="strHighestDegreeCompletionDate" class="<%=validReqOpt %>"/></td>
					
					</tr>
					
			<tr>
					<% 	List<String> hightesDegreeGredList = hmValidationFields.get("CANDI_HIGHEST_DEGREE_GRADE"); 
						validReqOpt = "";
						validAsterix = "";
						if(hightesDegreeGredList != null && uF.parseToBoolean(hightesDegreeGredList.get(0))) {
							validReqOpt = "validateRequired";
							validAsterix = "<sup>*</sup>";
						}
					%>
					<td class="txtlabel alignRight">Grade / Percentage:<%=validAsterix %></td>
					<td>
						<input type="text" name="strHighestDegreeGrade" class="<%=validReqOpt %>" />
					</td>
					<% 	List<String> marksCGPAList = hmValidationFields.get("CANDI_HIGHEST_DEGREE_MARKS_CGPA"); 
						validReqOpt = "";
						validAsterix = "";
						if(marksCGPAList != null && uF.parseToBoolean(marksCGPAList.get(0))) {
							validReqOpt = "validateRequired";
							validAsterix = "<sup>*</sup>";
						}
					%>
					<td class="txtlabel alignRight"><b>Marks / CGPA:</b><%=validAsterix %></td>
					<td><input type="text" name="strHighestDegreeMarksCGPA" class="<%=validReqOpt %>" onkeypress="return isNumberKey(event)"/></td>
			</tr>	
			
			
			<!-- Graduate Degree Section  -->
			<tr>
					<% 	List<String> graduateDegreeList = hmValidationFields.get("CANDI_GRADUATE_DEGREE"); 
						validReqOpt = "";
						validAsterix = "";
						if(graduateDegreeList != null && uF.parseToBoolean(graduateDegreeList.get(0))) {
							validReqOpt = "validateRequired";
							validAsterix = "<sup>*</sup>";
						}
					%>
					<td class="txtlabel alignRight">Graduate Degree:<%=validAsterix %></td>
					<td>
						<input type="text" name="strGraduateDegree" class="<%=validReqOpt %>" />
					</td>
					<% 	List<String> graduateSubjectList = hmValidationFields.get("CANDI_GRADUATE_DEGREE_SUBJECT"); 
						validReqOpt = "";
						validAsterix = "";
						if(graduateSubjectList != null && uF.parseToBoolean(graduateSubjectList.get(0))) {
							validReqOpt = "validateRequired";
							validAsterix = "<sup>*</sup>";
						}
					%>
					<td class="txtlabel alignRight"><b>Subject:</b><%=validAsterix %></td>
					<td><input type="text" name="strGraduateDegreeSubject" class="<%=validReqOpt %>" /></td>
			</tr>
			<tr>
			 <%
                    String graduateStartDate="";
                    if(request.getAttribute("strGraduateDegreeStartDate")==null) {
                    	graduateStartDate="";
                    } else {
                    	graduateStartDate=(String)request.getAttribute("strGraduateDegreeStartDate");
                    }
				%>
			<%
		            List<String> reqGraduateDate = hmValidationFields.get("CANDI_GRADUATE_DEGREE_START_DATE"); 
					validAsterix = "";
					validReqOpt = "";
					if(reqGraduateDate != null && uF.parseToBoolean(reqGraduateDate.get(0))) {
						validAsterix = "<sup>*</sup>";
						validReqOpt = "validateRequired";
					}
	           	%>
                <td class="txtlabel alignRight"><b>Start Date:<%=validAsterix %></b></td>
                <td><input type="text" name="strGraduateDegreeStartDate" id="strGraduateDegreeStartDate" class="<%=validReqOpt %>"/></td>
                
                 <%
                    String graduateCompletionDate="";
                    if(request.getAttribute("strGraduateDegreeCompletionDate")==null) {
                    	graduateCompletionDate="";
                    } else {
                    	graduateCompletionDate=(String)request.getAttribute("strGraduateDegreeCompletionDate");
                    }
				%>
			<%
		            List<String> reqGraduateCompletionDate = hmValidationFields.get("CANDI_GRADUATE_DEGREE_COMPLETION_DATE"); 
					validAsterix = "";
					validReqOpt = "";
					if(reqGraduateCompletionDate != null && uF.parseToBoolean(reqGraduateCompletionDate.get(0))) {
						validAsterix = "<sup>*</sup>";
						validReqOpt = "validateRequired";
					}
	           	%>
                <td class="txtlabel alignRight"><b>Completion Date:<%=validAsterix %></b></td>
                <td><input type="text" name="strGraduateDegreeCompletionDate" id="strGraduateDegreeCompletionDate" class="<%=validReqOpt %>"/></td>
					
					</tr>
					
			<tr>
					<% 	List<String> graduateDegreeGredList = hmValidationFields.get("CANDI_GRADUATE_DEGREE_GRADE"); 
						validReqOpt = "";
						validAsterix = "";
						if(graduateDegreeGredList != null && uF.parseToBoolean(graduateDegreeGredList.get(0))) {
							validReqOpt = "validateRequired";
							validAsterix = "<sup>*</sup>";
						}
					%>
					<td class="txtlabel alignRight">Grade / Percentage:<%=validAsterix %></td>
					<td>
						<input type="text" name="strGraduateDegreeGrade" class="<%=validReqOpt %>"/>
					</td>
					<% 	List<String> graduateMarksCGPAList = hmValidationFields.get("CANDI_GRADUATE_DEGREE_MARKS_CGPA"); 
						validReqOpt = "";
						validAsterix = "";
						if(graduateMarksCGPAList != null && uF.parseToBoolean(graduateMarksCGPAList.get(0))) {
							validReqOpt = "validateRequired";
							validAsterix = "<sup>*</sup>";
						}	
					%>
					<td class="txtlabel alignRight"><b>Marks / CGPA:</b><%=validAsterix %></td>
					<td><input type="text" name="strGraduateDegreeMarksCGPA" class="<%=validReqOpt %>" onkeypress="return isNumberKey(event)"/></td>
			</tr>		
					
			
			
			<!-- 12th Section  -->
			<tr>
					<% 	List<String> hscList = hmValidationFields.get("CANDI_HSC"); 
						validReqOpt = "";
						validAsterix = "";
						if(hscList != null && uF.parseToBoolean(hscList.get(0))) {
							validReqOpt = "validateRequired";
							validAsterix = "<sup>*</sup>";
						}
					%>
					<td class="txtlabel alignRight">12<sup style="color: black !important;">th</sup>:<%=validAsterix %></td>
					<td>
						<input type="text" name="strHSC" class="<%=validReqOpt %>"/>
					</td>
					<% 	List<String> hscBoardList = hmValidationFields.get("CANDI_HSC_BOARD"); 
						validReqOpt = "";
						validAsterix = "";
						if(hscBoardList != null && uF.parseToBoolean(hscBoardList.get(0))) {
							validReqOpt = "validateRequired";
							validAsterix = "<sup>*</sup>";
						}
					%>
					<td class="txtlabel alignRight"><b>Board:</b><%=validAsterix %></td>
					<td><input type="text" name="strHSCBoard" class="<%=validReqOpt %>"/></td>
			</tr>
			
			<tr>
					<% 	List<String> nameOfInstituteList = hmValidationFields.get("CANDI_HSC_NAME_OF_INSTITUTE"); 
						validReqOpt = "";
						validAsterix = "";
						if(nameOfInstituteList != null && uF.parseToBoolean(nameOfInstituteList.get(0))) {
							validReqOpt = "validateRequired";
							validAsterix = "<sup>*</sup>";
						}
					%>
					<td class="txtlabel alignRight">Name of Institute:<%=validAsterix %></td>
					<td>
						<input type="text" name="strHSCInstitute" class="<%=validReqOpt %>"/>
					</td>
					<% 	List<String> hscCityList = hmValidationFields.get("CANDI_HSC_CITY"); 
						validReqOpt = "";
						validAsterix = "";
						if(hscCityList != null && uF.parseToBoolean(hscCityList.get(0))) {
							validReqOpt = "validateRequired";
							validAsterix = "<sup>*</sup>";
						}
					%>
					<td class="txtlabel alignRight"><b>City:</b><%=validAsterix %></td>
					<td><input type="text" name="strHSCCity" class="<%=validReqOpt %>"/></td>
			</tr>
			
			<tr>
			 <%
                    String hscStartDate="";
                    if(request.getAttribute("strHSCStartDate")==null) {
                    	hscStartDate="";
                    } else {
                    	hscStartDate=(String)request.getAttribute("strHSCStartDate");
                    }
				%>
			<%
		            List<String> reqHSCDate = hmValidationFields.get("CANDI_HSC_START_DATE"); 
					validAsterix = "";
					validReqOpt = "";
					if(reqHSCDate != null && uF.parseToBoolean(reqHSCDate.get(0))) {
						validAsterix = "<sup>*</sup>";
						validReqOpt = "validateRequired";
					}
	           	%>
                <td class="txtlabel alignRight"><b>Start Date:<%=validAsterix %></b></td>
                <td><input type="text" name="strHSCStartDate" id="strHSCStartDate" class="<%=validReqOpt %>"/></td>
                
                 <%
                    String hscCompletionDate="";
                    if(request.getAttribute("strHSCCompletionDate")==null) {
                    	hscCompletionDate="";
                    } else {
                    	hscCompletionDate=(String)request.getAttribute("strHSCCompletionDate");
                    }
				%>
			<%
		            List<String> reqHSCCompletionDate = hmValidationFields.get("CANDI_HSC_COMPLETION_DATE"); 
					validAsterix = "";
					validReqOpt = "";
					if(reqHSCCompletionDate != null && uF.parseToBoolean(reqHSCCompletionDate.get(0))) {
						validAsterix = "<sup>*</sup>";
						validReqOpt = "validateRequired";
					}
	           	%>
                <td class="txtlabel alignRight"><b>Completion Date:<%=validAsterix %></b></td>
                <td><input type="text" name="strHSCCompletionDate" id="strHSCCompletionDate" class="<%=validReqOpt %>"/></td>
					
					</tr>
					
			<tr>
					<% 	List<String> hscGradeList = hmValidationFields.get("CANDI_HSC_GRADE"); 
						validReqOpt = "";
						validAsterix = "";
						if(hscGradeList != null && uF.parseToBoolean(hscGradeList.get(0))) {
							validReqOpt = "validateRequired";
							validAsterix = "<sup>*</sup>";
						}
					%>
					<td class="txtlabel alignRight">Grade / Percentage:<%=validAsterix %></td>
					<td>
						<input type="text" name="strHSCGrade" class="<%=validReqOpt %>"/>
					</td>
					<% 	List<String> hscMarksCGPAList = hmValidationFields.get("CANDI_HSC_MARKS_CGPA"); 
						validReqOpt = "";
						validAsterix = "";
						if(hscMarksCGPAList != null && uF.parseToBoolean(hscMarksCGPAList.get(0))) {
							validReqOpt = "validateRequired";
							validAsterix = "<sup>*</sup>";
						}
					%>
					<td class="txtlabel alignRight"><b>Marks / CGPA:</b><%=validAsterix %></td>
					<td><input type="text" name="strHSCMarksCGPA" class="<%=validReqOpt %>" onkeypress="return isNumberKey(event)"/></td>
			</tr>
			
			
			<!-- 10th Section  -->
			<tr>
					<% 	List<String> sscList = hmValidationFields.get("CANDI_SSC"); 
						validReqOpt = "";
						validAsterix = "";
						if(sscList != null && uF.parseToBoolean(sscList.get(0))) {
							validReqOpt = "validateRequired";
							validAsterix = "<sup>*</sup>";
						}
					%>
					<td class="txtlabel alignRight">10<sup style="color: black !important;">th</sup>:<%=validAsterix %></td>
					<td>
						<input type="text" name="strSSC" class="<%=validReqOpt %>" />
					</td>
					<% 	List<String> sscBoardList = hmValidationFields.get("CANDI_SSC_BOARD"); 
						validReqOpt = "";
						validAsterix = "";
						if(sscBoardList != null && uF.parseToBoolean(sscBoardList.get(0))) {
							validReqOpt = "validateRequired";
							validAsterix = "<sup>*</sup>";
						}
					%>
					<td class="txtlabel alignRight"><b>Board:</b><%=validAsterix %></td>
					<td><input type="text" name="strSSCBoard" class="<%=validReqOpt %>"/></td>
			</tr>
			
			<tr>
					<% 	List<String> nameOfInstituteSSCList = hmValidationFields.get("CANDI_SSC_NAME_OF_INSTITUTE"); 
						validReqOpt = "";
						validAsterix = "";
						if(nameOfInstituteSSCList != null && uF.parseToBoolean(nameOfInstituteSSCList.get(0))) {
							validReqOpt = "validateRequired";
							validAsterix = "<sup>*</sup>";
						}
					%>
					<td class="txtlabel alignRight">Name of Institute:<%=validAsterix %></td>
					<td>
						<input type="text" name="strSSCInstitute" class="<%=validReqOpt %>" />
					</td>
					<% 	List<String> SSCCityList = hmValidationFields.get("CANDI_SSC_CITY"); 
						validReqOpt = "";
						validAsterix = "";
						if(SSCCityList != null && uF.parseToBoolean(SSCCityList.get(0))) {
							validReqOpt = "validateRequired";
							validAsterix = "<sup>*</sup>";
						}
					%>
					<td class="txtlabel alignRight"><b>City:</b><%=validAsterix %></td>
					<td><input type="text" name="strSSCCity" class="<%=validReqOpt %>"/></td>
			</tr>
			
			<tr>
			 <%
                    String sscStartDate="";
                    if(request.getAttribute("strSSCStartDate")==null) {
                    	sscStartDate="";
                    } else {
                    	sscStartDate=(String)request.getAttribute("strSSCStartDate");
                    }
				%>
			<%
		            List<String> reqSSCDate = hmValidationFields.get("CANDI_SSC_START_DATE"); 
					validAsterix = "";
					validReqOpt = "";
					if(reqSSCDate != null && uF.parseToBoolean(reqSSCDate.get(0))) {
						validAsterix = "<sup>*</sup>";
						validReqOpt = "validateRequired";
					}
	           	%>
                <td class="txtlabel alignRight"><b>Start Date:<%=validAsterix %></b></td>
                <td><input type="text" name="strSSCStartDate" id="strSSCStartDate" class="<%=validReqOpt %>"/></td>
                
                 <%
                    String sscCompletionDate="";
                    if(request.getAttribute("strSSCCompletionDate")==null) {
                    	sscCompletionDate="";
                    } else {
                    	sscCompletionDate=(String)request.getAttribute("strSSCCompletionDate");
                    }
				%>
			<%
		            List<String> reqSSCCompletionDate = hmValidationFields.get("CANDI_SSC_COMPLETION_DATE"); 
					validAsterix = "";
					validReqOpt = "";
					if(reqSSCCompletionDate != null && uF.parseToBoolean(reqSSCCompletionDate.get(0))) {
						validAsterix = "<sup>*</sup>";
						validReqOpt = "validateRequired";
					}
	           	%>
                <td class="txtlabel alignRight"><b>Completion Date:<%=validAsterix %></b></td>
                <td><input type="text" name="strSSCCompletionDate" id="strSSCCompletionDate" class="<%=validReqOpt %>"/></td>
					
					</tr>
					
			<tr>
					<% 	List<String> sscGradeList = hmValidationFields.get("CANDI_SSC_GRADE"); 
						validReqOpt = "";
						validAsterix = "";
						if(sscGradeList != null && uF.parseToBoolean(sscGradeList.get(0))) {
							validReqOpt = "validateRequired";
							validAsterix = "<sup>*</sup>";
						}
					%>
					<td class="txtlabel alignRight">Grade / Percentage:<%=validAsterix %></td>
					<td>
						<input type="text" name="strSSCGrade" class="<%=validReqOpt %>"/>
					</td>
					<% 	List<String> sscMarksCGPAList = hmValidationFields.get("CANDI_SSC_MARKS_CGPA"); 
						validReqOpt = "";
						validAsterix = "";
						if(sscMarksCGPAList != null && uF.parseToBoolean(sscMarksCGPAList.get(0))) {
							validReqOpt = "validateRequired";
							validAsterix = "<sup>*</sup>";
						}
					%>
					<td class="txtlabel alignRight"><b>Marks / CGPA:</b><%=validAsterix %></td>
					<td><input type="text" name="strSSCMarksCGPA" class="<%=validReqOpt %>" onkeypress="return isNumberKey(event)"/></td>
			</tr>	
			
			<tr>
				 		<% 	List<String> candiDocResumeValidList = hmValidationFields.get("CANDI_DOC_RESUME"); 
							validReqOpt = "";
							validAsterix = "";
							if(candiDocResumeValidList != null && uF.parseToBoolean(candiDocResumeValidList.get(0))) {
								validReqOpt = "validateRequired";
								validAsterix = "<sup>*</sup>";
							}
						%>	
				 		<td class="txtlabel alignRight"><%=IConstants.DOCUMENT_RESUME %>:<%=validAsterix %>
				 		<input type="hidden" name="idDocType0" value="<%=IConstants.DOCUMENT_RESUME %>"></input>
				 		<input type="hidden" style="" value="<%=IConstants.DOCUMENT_RESUME %>" name="idDocName0" ></input>
				 		<input type="hidden" name="idDocStatus0" id="idDoc1Status" value="0"></input>
				 		</td>
						<td class="txtlabel alignRight">
							<span id="file"></span>
							<input type="file" name="idDoc0" id="idDoc0" accept=".jpg,.png,.svg,.doc,.docs,.docx,.pdf" class="<%=validReqOpt %>" onchange="readFileURL(this, 'file');"/>  <!-- 	onchange="fillFileStatus('idDoc1Status')" -->
						</td>
						
						<% List<String> candiReferencesNameValidList = hmValidationFields.get("CANDI_REFERENCES_NAME"); 
								validReqOpt = "";
								validAsterix = "";
								if(candiReferencesNameValidList != null && uF.parseToBoolean(candiReferencesNameValidList.get(0))) {
									validReqOpt = "validateRequired";
									validAsterix = "<sup>*</sup>";
								}
							%>
							<td class="txtlabel alignRight" valign="top"><b>Reference (Source):<%=validAsterix%></b></td>
							<td><span style="float: left;"><s:hidden name="isEmpCode" id="isEmpCode"/><s:hidden name="refEmpId" id="refEmpId"/>
									<input type="text" name="refEmpCode" id="refEmpCode" class="<%=validReqOpt%>" placeholder="Please enter Employee Id" onchange="checkEmployeeCode(this.value);"/>
								</span>
								<span id="empIdMsgSpan" style="width: 30px; float: left;"></span>
							</td>
                    </tr>
						<tr>
							<td class="txtlabel alignRight" valign="top"><!-- so that you are not a bot --> Captcha:<sup>*</sup></td>
						 	<td>
						 		<strike>
						 			<label id="mainCaptcha" for="refresh" style="background-color: #cccccc; padding: 5px;"></label>
						 		</strike>&nbsp;
						 		<i class="fa fa-refresh" style="font-size: 18px;" aria-hidden="true" onclick="Captcha();"></i>
							</td>
						</tr>
						<tr>
						   	<td>&nbsp;</td>
				           	<td><input type="text" name="txtInput" id="txtInput" class="validateRequired"/></td>
				         </tr>
						
		</table>
	</div>
			<div style="float:right;">
				<table class="table table_no_border">
					<tr>
						<td colspan="2" align="center">
							<s:submit cssClass="btn btn-primary" name="stepSubmit" cssStyle="width:200px; float:right;" value="Submit" align="center"/>
						</td>
					</tr>
				</table>
			</div>
		<div class="clr"></div>	
		</s:form>
		</div>
</div>