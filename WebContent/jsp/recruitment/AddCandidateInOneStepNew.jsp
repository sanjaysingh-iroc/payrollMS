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
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
	
<%-- <jsp:include page="/jsp/common/Links.jsp" flush="true" /> --%>
<%@ taglib uri="http://granule.com/tags" prefix="g" %> 
<%@ taglib prefix="s" uri="/struts-tags" %> 
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

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
	
	#div_language {
		height: 300px;
		width: 46%;
		float: left;
		border: solid 2px #ccc;
		overflow: auto;
		margin: 10px 10px 10px 10px;
		padding: 10px;
	}
	
	#div_education {
		height: 300px;
		width: 46%;
		float: left;
		border: solid 2px #ccc;
		overflow: auto;
		margin: 10px 10px 10px 10px;
		padding: 10px;
	}
	
	</g:compress>
	
	</style>

	<%
	String struserType = (String)session.getAttribute(IConstants.USERTYPE);
	ArrayList alSkills = (ArrayList) request.getAttribute("alSkills"); 
	ArrayList alDocuments = (ArrayList) request.getAttribute("alDocuments");
	List yearsList = (List) request.getAttribute("yearsList");
	List empGenderList = (List) request.getAttribute("empGenderList");
	List skillsList = (List) request.getAttribute("skillsList");
	String strImage = (String) request.getAttribute("strImage");
	String fromPage = (String) request.getAttribute("fromPage");
	UtilityFunctions uF = new UtilityFunctions();
	String currentYear = (String)request.getAttribute("currentYear");
	
	CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
	int nEmpAlphaCodeLength = 2;
	if(CF!=null && CF.getStrOEmpCodeAlpha()!=null){
		nEmpAlphaCodeLength = CF.getStrOEmpCodeAlpha().length();
	}
	
	String strUserType=(String)session.getAttribute(IConstants.USERTYPE);
	Map<String, List<String>> hmValidationFields = (Map<String, List<String>> ) request.getAttribute("hmValidationFields");
	String validReqOpt = "";
	String validAsterix = "";
	%>

<g:compress>
<script>  
$(function(){
	/* $("#jobcode").multiselect().multiselect(); */
	$("#stepSubmit").click(function(){
		$('.validateRequired').filter(':hidden').prop('required',false);
		$('.validateRequired').filter(':visible').prop('required',true);
		$('.validateEmail').attr('type','email');
	});
	
	$("#strWLocation").multiselect().multiselectfilter();
	
	  $("#strStartDate").datepicker({
	    	format : 'dd/mm/yyyy',endDate:'+0d'/* Created By Dattatray Date:26-08-21 Note:endDate  */
	    });
});

<%-- function callDatePicker() {
	//alert("callDatePicker ");
    $( "#empDateOfBirth" ).datepicker({format: 'dd/mm/yyyy', yearRange: '1950:2000', changeYear: true});
    $( "#empDateOfMarriage" ).datepicker({format: 'dd/mm/yyyy', yearRange: '1970:<%=currentYear%>', changeYear: true});
    $("input[name=strDate]").datepicker({format: 'dd/mm/yyyy'});
	$('input[name=strTime]').datetimepicker({format: 'HH:mm'});
} --%>

$(function() {
	
    $( "#empDateOfBirth" ).datepicker({format: 'dd/mm/yyyy', yearRange: '1950:2000', changeYear: true});
    $( "#empDateOfMarriage" ).datepicker({format: 'dd/mm/yyyy', yearRange: '1970:<%=currentYear%>', changeYear: true});
    $("input[name=strDate]").datepicker({format: 'dd/mm/yyyy'});
    $('input[name=strTime]').datetimepicker({format: 'HH:mm'});
});


<% if (alSkills!=null) { %>
	var cnt = <%=alSkills.size() %>;
<% } else { %>
	var cnt =0;
<% } %>


function addSkills() {
	cnt++;
	var divTag = document.createElement("div");
    divTag.id = "row_skill"+cnt;
    divTag.setAttribute("class", "row_skill");
    divTag.setAttribute("style", "padding: 5px 0px 0px 0px; margin: 0px;");
    /* Start Dattatray Date:24-08-21 */
	/* Start Dattatray Date:25-08-21 Note:Validate set */
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
	divTag.innerHTML = 	"<input type=\"text\" name=\"skillName\" class=\""+validReqOpt+"\"/>"+
						"<input type=\"text\" placeholder=\"rating\" name=\"skillValue\" id=\"skillValue\" class=\""+validReqOpt1+"\" style=\"margin-left: 10px; width: 50px !important;\" onkeyup=\"checkRatingValue(this.id,'Please skill rating between 1 to 10');\" onkeypress=\"return isNumberKey(event)\" />"+<!-- Created by Dattatray Date:25-08-21 Note:placeholder-->
    			    	"<a href=\"javascript:void(0)\" onclick=\"addSkills()\" style=\"margin-left: 4px;\" class=\"add-font\"></a>" +<!-- Created by Dattatray Date:25-08-21 Note:margin-left: 4px;-->
    			    	"<a href=\"javascript:void(0)\" onclick=\"removeSkills(this.id)\" id=\""+cnt+"\" class=\"remove-font\"></a>"; 
    			    	/* End Dattatray Date:25-08-21 */ 
    			    	/* End Dattatray Date:24-08-21 */
    document.getElementById("div_skills").appendChild(divTag);
}

function addSkills1() {
	cnt++;
	var divTag = document.createElement("div");
    divTag.id = "row_skill_s"+cnt;
    divTag.setAttribute("class", "row_skill_s");
    divTag.setAttribute("style", "padding: 5px 0px 0px 0px; margin: 0px;");
    /* Start Dattatray Date:24-08-21 */
	/* Start Dattatray Date:25-08-21 Note:Validate set */
    <% 	
    	List<String> candiSkillNameValidList1 = hmValidationFields.get("CANDI_SECONDARY_SKILL_NAME"); 
								validReqOpt = "";
						if(candiSkillNameValidList1 != null && uF.parseToBoolean(candiSkillNameValidList1.get(0))) {
							validReqOpt = "validateRequired";
			}
						
				String validReqOpt11= "";
				List<String> candiSkillRatingValidList1 = hmValidationFields.get("CANDI_SECONDARY_SKILL_RATING"); 
				if(candiSkillRatingValidList1 != null && uF.parseToBoolean(candiSkillRatingValidList1.get(0))) {/* Created by Dattatray Date:25-08-21 Note:Added candiSkillRatingValidList1 */
					validReqOpt11 = "validateRequired";
				}
	%>
		var validReqOpt = '<%=validReqOpt %>';
		var validReqOpt11 = '<%=validReqOpt11 %>';
	divTag.innerHTML = 	"<input type=\"text\" name=\"skillNameSec\" class=\""+validReqOpt+"\" />"+
						"<input type=\"text\" placeholder=\"rating\" name=\"skillValueSec\" id=\"skillValueSec\" class=\""+validReqOpt11+"\" style=\"margin-left: 10px; width: 50px !important;\" onkeyup=\"checkRatingValue(this.id,'Please skill rating between 1 to 10');\" onkeypress=\"return isNumberKey(event)\"/>"+<!-- Created by Dattatray Date:25-08-21 Note:placeholder-->
    			    	"<a href=\"javascript:void(0)\" onclick=\"addSkills1()\" style=\"margin-left: 4px;\" class=\"add-font\"></a>" +	<!-- Created by Dattatray Date:25-08-21 Note:margin-left: 4px;-->
    			    	"<a href=\"javascript:void(0)\" onclick=\"removeSkills1(this.id)\" id=\""+cnt+"\" class=\"remove-font\"></a>"; 
    			    	/* End Dattatray Date:25-08-21 */ 
    			    	/* End Dattatray Date:24-08-21 */
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

function fillFileStatus(ids){
	document.getElementById(ids).value=1;
}

function submitForm(){
	
	var frmPage = document.getElementById('fromPage').value;
	document.getElementById('frmAvailibility').submit();
	//alert("frmPage ===>> " + frmPage);
	if(frmPage == 'CR') {
		window.setTimeout(function() {  
			parent.window.location="CandidateReport.action";
		}, 200);
	} else {
		window.setTimeout(function() {  
			parent.window.location="Applications.action";
		}, 500);
	}
}

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
               	} else {
               		document.getElementById("emailValidatorMessege").innerHTML = data;
               	}
               }
             });
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


function readImageURL(input, targetDiv) {
    if (input.files && input.files[0]) {
        var reader = new FileReader();
        reader.onload = function (e) {
            $('#'+targetDiv).attr('src', e.target.result).width(100).height(100);
        };
        reader.readAsDataURL(input.files[0]);
    }
}

function isNumberKey(evt) {
   var charCode = (evt.which) ? evt.which : event.keyCode;
   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46)
      return false;

   return true;
}

function checkImageSize(){
	if(document.getElementById("empImage") ) {
		var fromPage = document.getElementById("fromPage").value;
		/* if(fromPage == 'JO') {
			if(ValidCaptcha()) {
				document.getElementById("stepSubmit").style.display = 'none';
				return true;
			} else {
				alert("Invalid Entry!");
				return false;
			}
		} */
	
		if($('#empImage').length !== 0)	{
			if (window.File && window.FileReader && window.FileList && window.Blob){
				
			 if($('#empImage')[0].files[0] !== null && $('#empImage')[0].files[0]) {	
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
			 }
		   }else{
		       alert("Please upgrade your browser, because your current browser lacks some new features we need!");
		       return false;
     	   } 
	    }
	} else {
		var fromPage = document.getElementById("fromPage").value;
		if(fromPage == 'JO') {
			if(ValidCaptcha()) {
				document.getElementById("stepSubmit").style.display = 'none';
				return true;
			} else {
				alert("Invalid Captcha!");
				return false;
			}
		} else {
			return true;
		}
	}
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
 function checkRatingValue(id,msg) {
		var val = document.getElementById(id).value;
		if(parseInt(val) > 10 || parseInt(val) <= 0){
			alert(msg);
			document.getElementById(id).value="";
		}
}
</script>

</g:compress>

<%-- <script>

// perform JavaScript after the document is scriptable.
$(function() {
	// setup ul.tabs to work as tabs for each div directly under div.panes 
	$("ul.tabs").tabs("div.panes > div");
});
</script> --%>

<script>
      $(function(){
          // binds form submission and fields to the validation engine
         //jQuery("#frmAddCandidateInOneStep").validationEngine();
      
     <%if(fromPage != null && fromPage.equals("A")) { %>
		$("#frmAddCandidateInOneStepNew").submit(function(event){
			event.preventDefault();
			document.getElementById("stepSubmit").value = 'Submittng...';
			document.getElementById("stepSubmit").disabled = true;

			var recruitId = document.getElementById("recruitId").value;
			//alert("path ===>> " + $("#resumeFile").attr('path'));
			if($("#file").attr('path') !== undefined) {
				//var form = $('#frmAddCandidateInOneStep')[0];
				//var form_data = new FormData(form);
				var form_data = new FormData($(this)[0]);
				form_data.append("stepSubmit", "Submit");
				if($("#idDoc0")[0].files.length>0) {
					form_data.append("idDoc0",$("#idDoc0")[0].files[0]);
				}
				/* document.getElementById("stepSubmit").style.display = 'none'; */
				//form_data.append("idDoc0", $("#file").attr('path'));
				//alert("form_data ===>> " + form_data);
				$.ajax({
					url: "AddCandidateInOneStepNew.action",
					type: 'POST',
					//enctype: 'multipart/form-data',
					data: form_data,
					contentType: false,
					cache: false,
					//timeout: 300000,
			      	processData: false,
		      		success: function(result){
		      			$.ajax({
		    				url: 'Applications.action?recruitId='+recruitId,
		    				cache: true,
		    				success: function(result){
		    					$("#subSubDivResult").html(result);
		    		   		}
		    			});
		      	    },
		      		error: function(result){
		      			$.ajax({
		    				url: 'Applications.action?recruitId='+recruitId,
		    				cache: true,
		    				success: function(result){
		    					$("#subSubDivResult").html(result);
		    		   		}
		    			});
		      	    }
		      	});
				
			} else {
		  		var form_data = $("#frmAddCandidateInOneStepNew").serialize();
				$.ajax({
					type :'POST',
					url  :'AddCandidateInOneStepNew.action',
					data :form_data+"&stepSubmit=Submit", 
					cache:true,
					success: function(result){
		      			$.ajax({
		    				url: 'Applications.action?recruitId='+recruitId,
		    				cache: true,
		    				success: function(result){
		    					$("#subSubDivResult").html(result);
		    		   		}
		    			});
		      	    },
		      		error: function(result){
		      			$.ajax({
		    				url: 'Applications.action?recruitId='+recruitId,
		    				cache: true,
		    				success: function(result){
		    					$("#subSubDivResult").html(result);
		    		   		}
		    			});
		      	    }
				});
			}
			var dialogEdit = '.modal-body';
		    $(dialogEdit).empty();
		    $("#modalInfo").hide();
		    
			
	  	});
    <% } else if(fromPage != null && fromPage.equals("JO")) { %>
  		Captcha();
  		 
 		$("#frmAddCandidateInOneStepNew").submit(function(event){
 			event.preventDefault();
 			if(ValidCaptcha()) {
			
	 			document.getElementById("stepSubmit").value = 'Submittng...';
	 			document.getElementById("stepSubmit").disabled = true;
	
	 			var recruitId = document.getElementById("recruitId").value;
	 			//alert("path ===>> " + $("#resumeFile").attr('path'));
	 			if($("#file").attr('path') !== undefined) {
	 				//var form = $('#frmAddCandidateInOneStep')[0];
	 				//var form_data = new FormData(form);
	 				var form_data = new FormData($(this)[0]);
	 				form_data.append("stepSubmit", "Submit");
	 				if($("#idDoc0")[0].files.length>0) {
	 					form_data.append("idDoc0",$("#idDoc0")[0].files[0]);
	 				}
	 				/* document.getElementById("stepSubmit").style.display = 'none'; */
	 				//form_data.append("idDoc0", $("#file").attr('path'));
	 				//alert("form_data ===>> " + form_data);
	 				$.ajax({
	 					url: "AddCandidateInOneStepNew.action",
	 					type: 'POST',
	 					//enctype: 'multipart/form-data',
	 					data: form_data,
	 					contentType: false,
	 					cache: false,
	 					//timeout: 300000,
	 			      	processData: false,
	 		      		success: function(result){
	 		      			$.ajax({
	 		    				url: 'JobOpportunities.action',
	 		    				cache: true,
	 		    				success: function(result){
	 		    					$("#divResult").html(result);
	 		    		   		}
	 		    			});
	 		      	    },
	 		      		error: function(result){
	 		      			$.ajax({
	 		    				url: 'JobOpportunities.action',
	 		    				cache: true,
	 		    				success: function(result){
	 		    					$("#divResult").html(result);
	 		    		   		}
	 		    			});
	 		      	    }
	 		      	});
	 				
	 			} else {
	 		  		var form_data = $("#frmAddCandidateInOneStepNew").serialize();
	 				$.ajax({
	 					type :'POST',
	 					url  :'AddCandidateInOneStepNew.action',
	 					data :form_data+"&stepSubmit=Submit", 
	 					cache:true,
	 					success: function(result){
	 		      			$.ajax({
	 		    				url: 'JobOpportunities.action',
	 		    				cache: true,
	 		    				success: function(result){
	 		    					$("#divResult").html(result);
	 		    		   		}
	 		    			});
	 		      	    },
	 		      		error: function(result){
	 		      			$.ajax({
	 		    				url: 'JobOpportunities.action',
	 		    				cache: true,
	 		    				success: function(result){
	 		    					$("#divResult").html(result);
	 		    		   		}
	 		    			});
	 		      	    }
	 				});
	 			}
	 			var dialogEdit = '.modal-body';
	 		    $(dialogEdit).empty();
	 		    $("#modalInfo").hide();
	 			return true;
			} else {
				alert("Invalid Entry!");
				return false;
			}
 	  	});
     
    <% } %>
      
	});

	
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
    
</script>

<%
	//System.out.println("fromPage==>"+fromPage);
	String recruitId = (String) request.getAttribute("recruitId");
	
%>
<!-- <div class="leftbox reportWidth" > -->
	<div class="panes">
		<s:form theme="simple" action="AddCandidateInOneStepNew" name="AddCandidateInOneStepNew" id="frmAddCandidateInOneStepNew" method="POST" cssClass="formcss" enctype="multipart/form-data" onsubmit="return checkImageSize();">
			<s:hidden name="fromPage" id="fromPage"/>
			<s:hidden name="recruitId" id="recruitId" />
			<input type="hidden" name="jobcode" id="jobcode" value="<%=recruitId %>"/> 
			<s:hidden name="CandidateId" />
			<s:hidden name="applyType" />
			
			<div>
				<table border="0" class="table table_no_border form-table">
					<tr>
					<% List<String> candiSalutationValidList = hmValidationFields.get("CANDI_SALUTATION"); 
						validReqOpt = "";
						validAsterix = "";
						if(candiSalutationValidList != null && uF.parseToBoolean(candiSalutationValidList.get(0))) {
							validReqOpt = "validateRequired";
							validAsterix = "<sup>*</sup>";
						}
					%>
					<td class="txtlabel alignRight">Salutation:<%=validAsterix %></td>
					<td>
					
						<% if(candiSalutationValidList != null && uF.parseToBoolean(candiSalutationValidList.get(0))) { %>
							<s:select list="salutationList" name="salutation" headerKey="" headerValue="Select Salutation"
								listKey="salutationId" listValue="salutationName" cssClass="validateRequired"></s:select>
						<% } else { %>
							<s:select list="salutationList" name="salutation" headerKey="" headerValue="Select Salutation"
								listKey="salutationId" listValue="salutationName" cssClass=""/>
						<% } %>
					
					</td>
					</tr>
					
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
						<input type="text" name="empFname" class="<%=validReqOpt %>" value="<%=uF.showData((String)request.getAttribute("empFname"), "") %>" />
					</td>
					<% 	List<String> candiLNameValidList = hmValidationFields.get("CANDI_LAST_NAME"); 
						validReqOpt = "";
						validAsterix = "";
						if(candiLNameValidList != null && uF.parseToBoolean(candiLNameValidList.get(0))) {
							validReqOpt = "validateRequired";
							validAsterix = "<sup>*</sup>";
						}
					%>
					<td class="txtlabel alignRight"><b>Last Name:</b><%=validAsterix %></td>
					<td><input type="text" name="empLname" class="<%=validReqOpt %>" value="<%=uF.showData((String)request.getAttribute("empLname"), "") %>"/></td>
					</tr>
					
					<tr>
					<% 	List<String> candiPersonalEmailIdValidList = hmValidationFields.get("CANDI_PERSONAL_EMAIL_ID"); 
						validReqOpt = "";
						validAsterix = "";
						if(candiPersonalEmailIdValidList != null && uF.parseToBoolean(candiPersonalEmailIdValidList.get(0))) {
							validReqOpt = "validateRequired";
							validAsterix = "<sup>*</sup>";
						}
					%>
					<td class="txtlabel alignRight" >Personal Email Id:<%=validAsterix %></td>
					<td><input type="text" name="empEmail" id="empEmail" class="<%=validReqOpt%>" value="<%=uF.showData((String)request.getAttribute("empEmail"), "") %>"  onchange="checkMailID(this.value);"/>  <!-- getContent('emailValidatorMessege','EmailValidation.action?candiEmail='+this.value) -->
					<br/><span id="emailValidatorMessege" style="font-size: 12px;"></span><!-- Created By Dattatray Date:26-08-21 Note:email validation set   -->
					</td>
					<% 	List<String> candiMobileNoValidList = hmValidationFields.get("CANDI_MOBILE_NO"); 
						validReqOpt = "";
						validAsterix = "";
						if(candiMobileNoValidList != null && uF.parseToBoolean(candiMobileNoValidList.get(0))) {
							validReqOpt = "validateRequired";
							validAsterix = "<sup>*</sup>";
						}
					%>
					<td class="txtlabel alignRight"><b>Mobile Number:<%=validAsterix %></b></td>
					<td><input type="text" name="empMobileNo" class="<%=validReqOpt%>"  value="<%=uF.showData((String)request.getAttribute("empMobileNo"), "") %>"/><span class="hint">Candidate's Mobile No<span class="hint-pointer">&nbsp;</span></span> <!-- onkeypress="return isOnlyNumberKey(event)"  -->
					</td>
					</tr>
		
					<tr>
							<% 	
										List<String> candiPrimarySkillNameValidList = hmValidationFields.get("CANDI_PRIMARY_SKILL_NAME");/* Created by Dattatray Date:25-08-21 Note:Added candiPrimarySkillNameValidList */
										validReqOpt = "";
										validAsterix = "";
								if(candiPrimarySkillNameValidList != null && uF.parseToBoolean(candiPrimarySkillNameValidList.get(0))) {
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
						<td class="txtlabel alignRight" valign="top">Primary Skill:<%=validAsterix1 %></td>
						<td>
						<div  id="div_skills" style="padding: 0px; margin: 0px;  height: auto; max-height: 200px; font-size: 12px;"><!-- width: 390px; -->
						
						<div id="row_skill" class="row_skill" style="padding: 0px; margin: 0px;">
	                       	<input type="text" name="skillName" class="<%=validReqOpt%>"  value=""/>
	                       	<!-- Created by Dattatray Date:25-08-21 Note:margin-left: 10px; to margin-left: 8px;-->
	                       	<!-- Created by Dattatray Date:25-08-21 Note:placeholder-->
	                       	<input type="text" placeholder="rating" name="skillValue" id="skillValue" class="<%=validReqOpt1%>"  value="" style="margin-left: 8px; width: 50px !important;" onkeyup="checkRatingValue(this.id,'Please skill rating between 1 to 10');"  onkeypress="return isOnlyNumberKey(event)"/>
	                       	<a href="javascript:void(0)" onclick="addSkills()" class="add-font"></a>   
						</div>
						
						</div>
						</td>
						
						<% 	List<String> candiSecSkillNameValidList1 = hmValidationFields.get("CANDI_SECONDARY_SKILL_NAME"); /* Created by Dattatray Date:25-08-21 Note:Added candiSecSkillRatingValidList1 */
						validReqOpt = "";
						validAsterix = "";
				if(candiSecSkillNameValidList1 != null && uF.parseToBoolean(candiSecSkillNameValidList1.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
				}
				
				String validReqOpt11= "";
				String validAsterix11= "";
				List<String> candiSecSkillRatingValidList1 = hmValidationFields.get("CANDI_SECONDARY_SKILL_RATING"); 
				if(candiSecSkillRatingValidList1 != null && uF.parseToBoolean(candiSecSkillRatingValidList1.get(0))) {/* Created by Dattatray Date:25-08-21 Note:Added candiSecSkillRatingValidList1 */
					validReqOpt11 = "validateRequired";
					validAsterix11 = "<sup>*</sup>";
				}
					%>
						<td class="txtlabel alignRight" valign="top"><b>Secondary Skill:<%=validAsterix11 %></b></td>
						<td>
						<div  id="div_skills_s" style="padding: 0px; margin: 0px;  height: auto; max-height: 200px; font-size: 12px;"><!-- width: 390px; -->
						
						<div id="row_skill_s" class="row_skill_s" style="padding: 0px; margin: 0px;">
		                    
		                        	<input type="text" name="skillNameSec" class="<%=validReqOpt11%>"  value=""/>
		                        	
		                        	<!-- /* Created Dattatray Date:24-08-21 Note:onkeypress */ -->
		                        	<!-- Created by Dattatray Date:25-08-21 Note:placeholder and class:validReqOpt11-->
		                        		<input type="text" placeholder="rating" name="skillValueSec" id="skillValueSec" class="<%=validReqOpt11%>"  value="" style="margin-left: 10px; width: 50px !important;" onkeyup="checkRatingValue(this.id,'Please skill rating between 1 to 10');" onkeypress="return isOnlyNumberKey(event)"/>
		                        	
		                            <a href="javascript:void(0)" onclick="addSkills1()" style="margin-left: 4px;" class="add-font"></a>
						</div>
						
						</div>
						</td>
						
						
						</tr>
		
					<tr>
						<% 	List<String> candiTotExperienceValidList = hmValidationFields.get("CANDI_TOT_EXPERIENCE"); 
							validReqOpt = "";
							validAsterix = "";
							if(candiTotExperienceValidList != null && uF.parseToBoolean(candiTotExperienceValidList.get(0))) {
								validReqOpt = "validateRequired";
								validAsterix = "<sup>*</sup>";
							}
						%>
						<td class="txtlabel alignRight">Total Experience:<%=validAsterix %></td>
						<td>
							<div>
								<input style="width: 15% !important;" type="text" name="candiTotalExperience" class="<%=validReqOpt %>" value="<%=uF.showData((String)request.getAttribute("candiTotalExperience"), "") %>" onkeypress="return isOnlyNumberKey(event)"/> Years &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								<input style="width: 15% !important;" type="text" name="candiExperienceMonth" class="<%=validReqOpt %>" value="<%=uF.showData((String)request.getAttribute("candiExperienceMonth"), "") %>" onkeypress="return isOnlyNumberKey(event)"/> Month
							</div>
						</td>
					</tr>
					
					<tr>
					<% 	List<String> candiFNameValidList2 = hmValidationFields.get("CANDI_CURRENT_ORG_NAME"); 
						validReqOpt = "";
						validAsterix = "";
						if(candiFNameValidList2 != null && uF.parseToBoolean(candiFNameValidList2.get(0))) {
							validReqOpt = "validateRequired";
							validAsterix = "<sup>*</sup>";
						}
					%>
					<td class="txtlabel alignRight">Current Organization:<%=validAsterix %></td>
					<td>
						<input type="text" name="strCurrentOrgName" class="<%=validReqOpt %>" value="<%=uF.showData((String)request.getAttribute("strCurrentOrgName"), "") %>" />
					</td>
					<% 	List<String> candiLNameValidList1 = hmValidationFields.get("CANDI_JOB_TITLE"); 
						validReqOpt = "";
						validAsterix = "";
						if(candiLNameValidList1 != null && uF.parseToBoolean(candiLNameValidList1.get(0))) {
							validReqOpt = "validateRequired";
							validAsterix = "<sup>*</sup>";
						}
					%>
					<td class="txtlabel alignRight"><b>Job Title:<%=validAsterix %></b></td>
					<td><input type="text" name="strJobTitle" class="<%=validReqOpt %>" value="<%=uF.showData((String)request.getAttribute("strJobTitle"), "") %>"/></td>
					</tr>
					
					<tr>
					<% 	List<String> candiCurrentCTCValidList = hmValidationFields.get("CANDI_CURRENT_CTC"); 
						validReqOpt = "";
						validAsterix = "";
						if(candiCurrentCTCValidList != null && uF.parseToBoolean(candiCurrentCTCValidList.get(0))) {
							validReqOpt = "validateRequired";
							validAsterix = "<sup>*</sup>";
						}
					%>
					<td class="txtlabel alignRight">Current CTC:</td>
					<td><input type="text"  name="candiCurrCTC" class="" value="<%=uF.showData((String)request.getAttribute("candiCurrCTC"), "") %>" onkeypress="return isNumberKey(event)"/> LPA</td>  <!-- lacs -->
					
					 <%
                    String effectiveDate="";
                    if(request.getAttribute("strStartDate")==null) {
                    	effectiveDate="";
                    } else {
                    	effectiveDate=(String)request.getAttribute("strStartDate");
                    }
				%>
				<%
		            List<String> reqEffectiveDate = hmValidationFields.get("CANDI_START_DATE"); 
					validAsterix = "";
					validReqOpt = "";
					if(reqEffectiveDate != null && uF.parseToBoolean(reqEffectiveDate.get(0))) {
						validAsterix = "<sup>*</sup>";
						validReqOpt = "validateRequired";
					}
	           	%>
                <td class="txtlabel alignRight"><b>Start Date:<%=validAsterix %></b></td>
                <td><input type="text" name="strStartDate" id="strStartDate" class="<%=validReqOpt %>" value="<%=effectiveDate%>"/></td>
					
					</tr>
					<tr>
					<% 	List<String> candiNoticePeriodValidList = hmValidationFields.get("CANDI_NOTICE_PERIOD"); 
							validReqOpt = "";
							validAsterix = "";
						if(candiNoticePeriodValidList != null && uF.parseToBoolean(candiNoticePeriodValidList.get(0))) {
							validReqOpt = "validateRequired";
							validAsterix = "<sup>*</sup>";
						}
					%>
					<td class="txtlabel alignRight">Notice Period:<%=validAsterix %></td>
					<td><input type="text" name="candiNoticePeriod" class="<%=validReqOpt %>" value="<%=uF.showData((String)request.getAttribute("candiNoticePeriod"), "") %>" onkeypress="return isOnlyNumberKey(event)"/> days</td>
					</tr>
					
					<tr>
					<% 	List<String> candiFNameValidList24 = hmValidationFields.get("CANDI_CURRENT_LOCATION"); 
						validReqOpt = "";
						validAsterix = "";
						if(candiFNameValidList2 != null && uF.parseToBoolean(candiFNameValidList2.get(0))) {
							validReqOpt = "validateRequired";
							validAsterix = "<sup>*</sup>";
						}
					%>
					<td class="txtlabel alignRight">Current Location:<%=validAsterix %></td>
					<td>
						<input type="text" name="currentLocation" class="<%=validReqOpt %>" value="<%=uF.showData((String)request.getAttribute("currentLocation"), "") %>" />
					</td>
					
					<% 	List<String> candiPrefferedLocation = hmValidationFields.get("CANDI_PREFFERED_LOCATION"); 
						validReqOpt = "";
						validAsterix = "";
						if(candiPrefferedLocation != null && uF.parseToBoolean(candiPrefferedLocation.get(0))) {
							validReqOpt = "validateRequired";
							validAsterix = "<sup>*</sup>";
						}
					%>
					<%-- <td class="txtlabel alignRight"><b>Preferred location:<%=validAsterix %></b></td>
					<td><input type="text" name="prefLocation" class="<%=validReqOpt %>" value="<%=uF.showData((String)request.getAttribute("prefLocation"), "") %>"/></td> --%>
					<td class="txtlabel alignRight" valign="top"><b>Preferred location:<sup><%=validAsterix %></sup></b></td>
			         	<td>
			         	<!-- Start Dattatray Date:25-08-21 -->
			         	<%  	if(candiPrefferedLocation != null && uF.parseToBoolean(candiPrefferedLocation.get(0))){
			         	%>
			         		<s:select theme="simple" name="strWLocation" id="strWLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" key="" multiple="true" cssClass="validateRequired"/>
			         	<%}else{ %>
			         		<s:select theme="simple" name="strWLocation" id="strWLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" key="" multiple="true"/>
			         	<%} %>
			         	<!-- End Dattatray Date:25-08-21 -->
			         	</td>
					
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
									<input type="text" name="refEmpCode" id="refEmpCode" class="<%=validReqOpt%>" placeholder="Please enter Employee Id" value="<%=uF.showData((String)request.getAttribute("refEmpCode"), "") %>" onchange="checkEmployeeCode(this.value);"/>
								</span>
								<span id="empIdMsgSpan" style="width: 30px; float: left;"></span>
							</td>
                    </tr>
					<%if(fromPage!=null && fromPage.equals("JO")) { %>
						<tr>
							<td class="txtlabel alignRight" valign="top"><!-- so that you are not a bot --> Captcha:<sup>*</sup></td>
						 	<td><strike><label id="mainCaptcha" for="refresh" style="background-color: #cccccc; padding: 5px;"></label></strike>&nbsp;
						 		<i class="fa fa-refresh" style="font-size: 18px;" aria-hidden="true" onclick="Captcha();"></i>
							</td>
						</tr>
						<tr>
						   	<td>&nbsp;</td>
				           	<td><input type="text" name="txtInput" id="txtInput" class="validateRequired"/></td>
				         </tr>
					<% } %>
					
					<tr>
					   <td>&nbsp;</td>
			           <td>
			            	<s:submit cssClass="btn btn-primary" name="stepSubmit" id="stepSubmit" cssStyle="float: left;" value="Submit"/>
			          </td>
			         </tr>
			         
				</table>
			</div>

				<div class="clr"></div>
		</s:form>
	</div>