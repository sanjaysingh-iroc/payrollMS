<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.select.FillSkills"%>
<%@page import="com.konnect.jpms.select.FillMaritalStatus"%>
<%@page import="com.konnect.jpms.select.FillGender"%>
<%@page import="com.konnect.jpms.select.FillYears"%>
<%@page import="com.konnect.jpms.select.FillDegreeDuration"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script type="text/javascript" src="scripts/customAjax.js" ></script>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<style> 
	.panmsg {
	    color: red;
	}
	.hidden {
	     visibility: hidden;
	}
</style>

<script>$(function() {	$("ul.tabs").tabs("div.panes > div");});</script>

<g:compress>
<script>
$(document).ready(function(){
	$("input[type='submit'").click(function(){
		$("#"+ this.form.id ).find('.validateRequired').filter(':hidden').prop('required',false);
		$("#"+ this.form.id ).find('.validateRequired').filter(':visible').prop('required',true);
		$("#"+ this.form.id ).find('.validateEmailRequired').filter(':visible').attr('type','email').prop('required',true);
		$("#"+ this.form.id ).find('.validateEmail').filter(':visible').attr('type','email').prop('required',false);
    });
	$("body").on('click','#closeButton',function(){
		$(".modal-dialog").removeAttr('style');
		$(".modal-body").height(400);
		$("#modalInfo").hide();
    });
	$("body").on('click','.close',function(){
		$(".modal-dialog").removeAttr('style');
		$(".modal-body").height(400);
		$("#modalInfo").hide();
	});
    $('.nav-tabs > li a[title]').tooltip();
    $('a[data-toggle="tab"]').on('show.bs.tab', function (e) {
        var $target = $(e.target);
        if ($target.parent().hasClass('disabled')) {
            return false;
        }
    });
    $(".next-step").click(function (e) {
		var form_id = this.form.id;
		if(document.getElementById("#"+form_id).submit()){
			var $active = $('.wizard .nav-tabs li.active');
	        nextTab($active);
		}
	});
    $(".prev-step").click(function (e) {
        var $active = $('.wizard .nav-tabs li.active');
        prevTab($active);
    });
});
function nextTab(elem) {$(elem).next().find('a[data-toggle="tab"]').click();}
function prevTab(elem) {$(elem).prev().find('a[data-toggle="tab"]').click();}
$(function(){
	$( ".datepickr" ).datepicker({format: 'dd/mm/yyyy'});
	$( "#empDateOfBirth" ).datepicker({
		format: 'dd/mm/yyyy',
		endDate: '-18y'	//Created by Dattatray Date : 12-July-21 Note : Added End Date  
	});
	$( "#empDateOfMarriage" ).datepicker({format: 'dd/mm/yyyy'});
	$( "#empPassportExpiryDate" ).datepicker({format: 'dd/mm/yyyy'});
	if(document.getElementById("empPFStartDate")){
		$("#empPFStartDate").datepicker({format: 'dd/mm/yyyy'});
	}
	$("#probationLeaves").multiselect().multiselectfilter();
	/* $("#empEmail").prop('type','email');
	$("#empEmailSec").prop('type','email');
	$("#fatherEmailId").prop('type','email'); 
    $("#motherEmailId").prop('type','email');
    $("#spouseEmailId").prop('type','email');
    $("#memberEmailId").prop('type','email');
    $("#childEmailId").prop('type','email');
	$("#refEmail").prop('type','email'); */
	$(".validateNumber").prop('type','number');
	$(".validateNumber").prop('step','any');
	//$("#empPEmail").prop('type','email');
	$('.validateRequired').prop('required',true);
	$("#locationCXO").multiselect().multiselectfilter();
});

CKEDITOR.config.width ='500px';


</script>
<script>
$(document).ready(function(){
	var BankName="<%=(String)request.getAttribute("empBankName")%>";
	/* ===start parvez date: 12-08-2022=== */
	var paymentMode = "<%=(String)request.getAttribute("empPayMode")%>";
	if(BankName == "-1"){
		if(paymentMode == "2" || paymentMode == "3"){
			$("#idEmpIFSC").hide();
			$("#idEmpOtherBankNameTr").hide();
			$("#idEmpOtherBankBranchTr").hide();
		}else{
			$("#idEmpIFSC").show();
			$("#idEmpOtherBankNameTr").show();
			$("#idEmpOtherBankBranchTr").show();
		}
	}
	/* ===end parvez date: 12-08-2022=== */
	
	var BankName2="<%=(String) request.getAttribute("empBankName2")%>";
	/* ===start parvez date: 12-08-2022=== */
	if(BankName2 == "-1"){
		if(paymentMode == "2" || paymentMode == "3"){
			$("#idEmpIFSC2").hide();
			$("#idEmpOtherBankNameTr2").hide();
			$("#idEmpOtherBankBranchTr2").hide();
		}else{
			$("#idEmpIFSC2").show();
			$("#idEmpOtherBankNameTr2").show();
			$("#idEmpOtherBankBranchTr2").show();
		}
	}/* ===end parvez date: 12-08-2022=== */
	
});


<%-- $(document).ready(function(){
	var BankName="<%=(String)request.getAttribute("empBankName")%>";
	if(BankName == "-1")
		{
			$("#idEmpIFSC").show();
		}else{
		$("#idEmpIFSC").hide();
	}
	
	var BankName2="<%=(String) request.getAttribute("empBankName2")%>";
	if(BankName2 == "-1"){
		$("#idEmpIFSC2").show();
	}else{
		$("#idEmpIFSC2").hide();
	}
}); --%>
</script>
</g:compress>


<script>
      // The instanceReady event is fired, when an instance of CKEditor has finished
      // its initialization.
      CKEDITOR.on( 'instanceReady', function( ev ) {
      	// Show the editor name and description in the browser status bar.
      	document.getElementById( 'eMessage' ).innerHTML = 'Instance <code>' + ev.editor.name + '<\/code> loaded.';
      
      	// Show this sample buttons.
      	document.getElementById( 'eButtons' ).style.display = 'block';
      });
      
      function InsertHTML() {
      	// Get the editor instance that we want to interact with.
      	var editor = CKEDITOR.instances.editor1;
      	var value = document.getElementById( 'htmlArea' ).value;
      
      	// Check the active editing mode.
      	if ( editor.mode == 'wysiwyg' )
      	{
      		// Insert HTML code.
      		// http://docs.ckeditor.com/#!/api/CKEDITOR.editor-method-insertHtml
      		editor.insertHtml( value );
      	}
      	else
      		alert( 'You must be in WYSIWYG mode!' );
      }
      
      function InsertText() {
      	// Get the editor instance that we want to interact with.
      	var editor = CKEDITOR.instances.editor1;
      	var value = document.getElementById( 'txtArea' ).value;
      
      	// Check the active editing mode.
      	if ( editor.mode == 'wysiwyg' )
      	{
      		// Insert as plain text.
      		// http://docs.ckeditor.com/#!/api/CKEDITOR.editor-method-insertText
      		editor.insertText( value );
      	}
      	else
      		alert( 'You must be in WYSIWYG mode!' );
      }
      
      function SetContents() {
      	// Get the editor instance that we want to interact with.
      	var editor = CKEDITOR.instances.editor1;
      	var value = document.getElementById( 'htmlArea' ).value;
      
      	// Set editor contents (replace current contents).
      	// http://docs.ckeditor.com/#!/api/CKEDITOR.editor-method-setData
      	editor.setData( value );
      }
      
      function GetContents() {
      	// Get the editor instance that you want to interact with.
      	var editor = CKEDITOR.instances.editor1;
      
      	// Get editor contents
      	// http://docs.ckeditor.com/#!/api/CKEDITOR.editor-method-getData
      	alert( editor.getData() );
      }
      
      function ExecuteCommand( commandName ) {
      	// Get the editor instance that we want to interact with.
      	var editor = CKEDITOR.instances.editor1;
      
      	// Check the active editing mode.
      	if ( editor.mode == 'wysiwyg' )
      	{
      		// Execute the command.
      		// http://docs.ckeditor.com/#!/api/CKEDITOR.editor-method-execCommand
      		editor.execCommand( commandName );
      	}
      	else
      		alert( 'You must be in WYSIWYG mode!' );
      }
      
      function CheckDirty() {
      	// Get the editor instance that we want to interact with.
      	var editor = CKEDITOR.instances.editor1;
      	// Checks whether the current editor contents present changes when compared
      	// to the contents loaded into the editor at startup
      	// http://docs.ckeditor.com/#!/api/CKEDITOR.editor-method-checkDirty
      	alert( editor.checkDirty() );
      }
      
      function ResetDirty() {
      	// Get the editor instance that we want to interact with.
      	var editor = CKEDITOR.instances.editor1;
      	// Resets the "dirty state" of the editor (see CheckDirty())
      	// http://docs.ckeditor.com/#!/api/CKEDITOR.editor-method-resetDirty
      	editor.resetDirty();
      	alert( 'The "IsDirty" status has been reset' );
      }
      
      function Focus() {
      	CKEDITOR.instances.editor1.focus();
      }
      
      function onFocus() {
      	document.getElementById( 'eMessage' ).innerHTML = '<b>' + this.name + ' is focused </b>';
      }
      
      function onBlur() {
      	document.getElementById( 'eMessage' ).innerHTML = this.name + ' lost focus';
      }
      	
   </script>
   
   
<%
	String struserType = (String)session.getAttribute(IConstants.USERTYPE);
	String sessionUserId = (String)session.getAttribute(IConstants.EMPID);
	ArrayList educationalList = (ArrayList) request.getAttribute("educationalList");
	List<List<String>> alSkills = (List<List<String>>) request.getAttribute("alSkills"); 
	List<List<String>> alHobbies = (List<List<String>>) request.getAttribute("alHobbies");
	List<List<String>> alLanguages = (List<List<String>>) request.getAttribute("alLanguages");
	List<List<String>> alEducation = (List<List<String>>) request.getAttribute("alEducation");
	if (alEducation == null)alEducation = new ArrayList<List<String>>();
	ArrayList alDocuments = (ArrayList) request.getAttribute("alDocuments");
	List<List<String>> alSiblings = (List<List<String>>) request.getAttribute("alSiblings");
	List<List<String>> alchilds = (List<List<String>>) request.getAttribute("alchilds");
	List<List<String>> alPrevEmployment = (List<List<String>>) request.getAttribute("alPrevEmployment");
	List<List<String>> alEmpReferences = (List<List<String>>) request.getAttribute("alEmpReferences");
	List<FillDegreeDuration> degreeDurationList = (List<FillDegreeDuration>) request.getAttribute("degreeDurationList");
	if (degreeDurationList == null)	degreeDurationList = new ArrayList<FillDegreeDuration>();
	/* List<FillYears> yearsList = (List<FillYears>) request.getAttribute("yearsList");
	if (yearsList == null)yearsList = new ArrayList<FillYears>(); */
	List<FillGender> empGenderList = (List<FillGender>) request.getAttribute("empGenderList");
	List<FillMaritalStatus> maritalStatusList = (List<FillMaritalStatus>) request.getAttribute("maritalStatusList");
	String countryOptions = (String) request.getAttribute("countryOptions");
	String statesOptions = (String) request.getAttribute("statesOptions");
	List<FillSkills> skillsList = (List<FillSkills>) request.getAttribute("skillsList");
	String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
	String strImage = (String) request.getAttribute("strImage");
	String sbdegreeDuration=(String)request.getAttribute("sbdegreeDuration");
	
	Map<String, String> hmFeatureStatus = (Map<String, String>) request.getAttribute("hmFeatureStatus");
	if(hmFeatureStatus==null) hmFeatureStatus = new HashMap<String, String>();

	UtilityFunctions uF = new UtilityFunctions();
	String currentYear = (String) request.getAttribute("currentYear");
	CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
	int nEmpAlphaCodeLength = 2;
	if (CF != null && CF.getStrOEmpCodeAlpha() != null) {
		nEmpAlphaCodeLength = CF.getStrOEmpCodeAlpha().length();
	}
	Map<String,String> hmPrevEmpTds=(Map<String,String>)request.getAttribute("hmPrevEmpTds");
	if(hmPrevEmpTds==null)hmPrevEmpTds=new HashMap<String, String>();
	
	int hobbiesCount = 0;
	if(alHobbies != null && alHobbies.size()>0 ) {
		hobbiesCount = alHobbies.size();
	}
	
	int eduCount =0;
	if(alEducation != null && alEducation.size()>0 ) {
		eduCount = alEducation.size();
	}
	
	int skillCount =0;
	if(alSkills != null && alSkills.size()>0 ) {
		skillCount = alSkills.size();
	}

	int empRefCount =0;
	if(alEmpReferences != null && alEmpReferences.size()>0 ) {
		empRefCount = alEmpReferences.size();
	}

	int langCount = 0;
	if(alLanguages != null && alLanguages.size()>0 ) {
		langCount = alLanguages.size();
	}
	
	int prevEmplCount = 0;
	if(alPrevEmployment != null && alPrevEmployment.size()>0 ) {
		prevEmplCount = alPrevEmployment.size();
	}
	
	int siblingCount = 0;
	if(alSiblings != null && alSiblings.size()>0 ) {
		siblingCount = alSiblings.size();
	}
	
	int childCount = 0;
	if(alchilds != null && alchilds.size()>0 ) {
		childCount = alchilds.size();
	}
	
	int docCount = 0;
	if(alDocuments != null && alDocuments.size()>0 ) {
		docCount = alDocuments.size();
	}
%>
<script>

	 var empRefCnt= '<%=empRefCount%>';
	 var skillcnt= '<%=skillCount%>';
	 var hobbiescnt= '<%=hobbiesCount%>';
	 var educationcnt= '<%=eduCount%>';
	 var languagecnt= '<%=langCount%>';	
	 var prevcnt= '<%=prevEmplCount%>';
	 var siblingcnt= '<%=siblingCount%>';
	 var childcnt= '<%=childCount%>';
	 var documentcnt= '<%=docCount%>';
	 
      /* function validateMandatory(value) {
      	if(value=='AT' || value=='CO') {
      		if(document.getElementById("desigId")) {
      			document.getElementById("desigId").style.display = 'none';
      		}
      		
      		if(document.getElementById("gradeId")) {
      			document.getElementById("gradeId").style.display = 'none';
      		}
      		
      		if(document.getElementById("desigIdV")) {
      			document.getElementById("desigIdV").className = '';
      		}
      		
      		if(document.getElementById("gradeIdV")) {
      			document.getElementById("gradeIdV").className = '';
      		}
     		
      	} else {
      		if(document.getElementById("desigId")) {
      			document.getElementById("desigId").style.display = 'inline';
      		}
      		
      		if(document.getElementById("gradeId")) {
      			document.getElementById("gradeId").style.display = 'inline';
      		}
      		
      		if(document.getElementById("desigIdV")) {
      			document.getElementById("desigIdV").className = 'validateRequired';
      		}
      		
      		if(document.getElementById("gradeIdV")) {
      			document.getElementById("gradeIdV").className = 'validateRequired';
      		}
      		
      	}
      } */
      
      jQuery(document).ready(function() {
    	  
          var isMedicalProfessional = document.getElementById("isMedicalProfessional");
          checkInfo(isMedicalProfessional);

          if(document.getElementById('strEmpKncKmc')) {
          	
          	checkKmcKnc(document.getElementById('strEmpKncKmc').value);
          }
      });
      
      
      function fnValidatePAN() {
    	  
  		var panPat = /^([A-Z]{5})(\d{4})([A-Z]{1})$/;
  		var objVal = $('#empPanNo').val();
  		//alert("objVal ===>> " + objVal);
  		if(objVal != '') {
			if (objVal.search(panPat) == -1) {
			// there is a mismatch, hence show the error message
				$('#empPanNoMsg').removeClass('hidden');
				$('#empPanNoMsg').text('"'+objVal+'" PAN No. is invalid, please enter a valid PAN No.');
				$('#empPanNoMsg').show();
				window.setTimeout(function() {
					$('#empPanNo').val('');
				}, 700);
			} else {
				// else, do not display message
				$('#empPanNoMsg').addClass('hidden');
			}
  		} else {
  			$('#empPanNoMsg').addClass('hidden');
  		}
	}
      
      function fnValidateADHAR() {
    	  
   		var adharPat = /^\d{4}\s\d{4}\s\d{4}$/;
   		var objVal = $('#empUIDNo').val();
   		//alert("objVal ===>> " + objVal);
   		if(objVal != '') {
	  		if (objVal.search(adharPat) == -1) {
	  		// there is a mismatch, hence show the error message
	  			$('#empUIDNoMsg').removeClass('hidden');
	  			$('#empUIDNoMsg').text('"'+objVal+'" Adhar No. is invalid, please enter a valid Adhar No. (Ex. XXXX XXXX XXXX)');
	  			$('#empUIDNoMsg').show();
	  			window.setTimeout(function() {
	  				$('#empUIDNo').val('');
				}, 700);
	  		} else {
	  			// else, do not display message
	  			$('#empUIDNoMsg').addClass('hidden');
	  		}
   		} else {
  			$('#empUIDNoMsg').addClass('hidden');
  		}
  	}
      
      
      function checkEmpORContractorCode(val) {
    	  var empORContractor = '<%=(String)request.getAttribute("EMP_OR_CONTRACTOR")%>';
    	  var empCodeAlpha = '<%=(String)request.getAttribute("EMP_CODE_ALPHA")%>';
    	  var empCodeNum = '<%=(String)request.getAttribute("EMP_CODE_NUM")%>';
    	 
    	 if(val == empORContractor) {
    		if(empCodeAlpha == null || empCodeAlpha == 'null') {
    			empCodeAlpha = '';
    		}
    		if(empCodeNum == null || empCodeNum == 'null') { 
    			empCodeNum = '';
	   		}
    		if(val == 1) {
    			document.getElementById('empORContractorSpan').innerHTML = "Employee";
    		} else if(val == 2) {
    			document.getElementById('empORContractorSpan').innerHTML = "Contractor";
    		}
			document.getElementById('empCodeAlphabet').value = empCodeAlpha;
			if(document.getElementById('empCodeAlphabetDis')) {
				document.getElementById('empCodeAlphabetDis').value = empCodeAlpha;
			}
			document.getElementById('empCodeNumber').value = empCodeNum;
      		return;
      	}

    	var xmlhttp = GetXmlHttpObject();
   		if (xmlhttp == null) {
   			alert("Browser does not support HTTP Request");
   			return;
   		} else {
   			var xhr = $.ajax({
   				url : "EmailValidation.action?strEmpOrContractor="+ val +"&empId="+ empId,
   				cache : false,
   				success : function(data) {
   					//alert("data ===>> " +data.length + " --- " + data);
   					if(data.trim() == "") {
   						if(val == 1) {
   			    			document.getElementById('empORContractorSpan').innerHTML = "Employee";
   			    		} else if(val == 2) {
   			    			document.getElementById('empORContractorSpan').innerHTML = "Contractor";
   			    		}
   						document.getElementById('empCodeAlphabet').value = '';
   						document.getElementById('empCodeNumber').value = '';
   						
                 	} else {
    					var allData = data.split("::::");
   						document.getElementById('empORContractorSpan').innerHTML = allData[0];
   						document.getElementById('empCodeAlphabet').value = allData[1];
   						if(document.getElementById('empCodeAlphabetDis')) {
   							document.getElementById('empCodeAlphabetDis').value = allData[1];
   						}
   						document.getElementById('empCodeNumber').value = allData[2];
   					}
   				}
   			});
   		}
      }
      
      
      var empCode='<%=(String)request.getAttribute("EMP_CODE")%>';
      function checkCodeValidation() {
    	  //alert(" checkCodeValidation ...");
    	  var empId = document.getElementById("empId").value;
    	  //alert("empId ==>>> " + empId);
    	 var empCodeAlphabet='';
    	 if(document.getElementById('empCodeAlphabet')){
    		 empCodeAlphabet =document.getElementById('empCodeAlphabet').value;
    	 }
    	 var empCodeNumber= document.getElementById('empCodeNumber').value;
    	 //alert("empCode ==>>> " + empCode + " -- empCodeAlphabet+empCodeNumber ==>>> " + empCodeAlphabet+empCodeNumber);
    	 if(empCode==(empCodeAlphabet+empCodeNumber)){
    		 document.getElementById('empCodeMessege').innerHTML='';
      		return;
      	}
    	 //alert("empCode 11 ==>>> " + empCode + " -- empCodeAlphabet+empCodeNumber ==>>> " + empCodeAlphabet+empCodeNumber);
	   	var xmlhttp = GetXmlHttpObject();
   		if (xmlhttp == null) {
   			alert("Browser does not support HTTP Request");
   			return;
   		} else {
   			var xhr = $.ajax({
   				url : "EmailValidation.action?empCodeAlphabet=" +empCodeAlphabet+empCodeNumber+"&empId="+ empId,
   				cache : false,
   				success : function(data) {
   					document.getElementById('empCodeMessege').innerHTML = data;
   					if(data.length>1){
   						document.getElementById('empCodeNumber').value='';
   					}
   				}
   			});
   		}
      }
      
      
      var val='<%=(String)request.getAttribute("EMPLOYEE_EMAIL")%>';
      
      function emailValidation(id,id1,val1,action) {
    	  
    	 var empId = document.getElementById("empId").value;
    	 /* if(document.getElementById("empPEmail")) {
    		  document.getElementById("empPEmail").value = val1;
    	 } */
    	
     	if(val == val1) {
     		document.getElementById(id).innerHTML='';
     		return;
     	}
     	 var xmlhttp = GetXmlHttpObject();
    		if (xmlhttp == null) {
    			alert("Browser does not support HTTP Request");
    			return;
    		} else {
    			var xhr = $.ajax({
    				url : action+"&empId="+ empId,
    				cache : false,
    				success : function(data) {
    					//console.log("data==>"+data);
    					document.getElementById(id).innerHTML = data;
    					if(data.length > 1) {
    						document.getElementById(id1).value = '';
    						/* document.getElementById("empPEmail").value = ''; */
    					}
    				}
    			});
    		}
       }
      
      var val2 = '<%=(String)request.getAttribute("EMPLOYEE_EMAIL2") %>';
      function emailValidation1(id,id1,val1,action) {
    	  var empId = document.getElementById("empId").value;
     	if(val2 == val1) {
     		document.getElementById(id).innerHTML = '';
     		return;
     	}
     	var xmlhttp = GetXmlHttpObject();
    		if (xmlhttp == null) {
    			alert("Browser does not support HTTP Request");
    			return;
    		} else {
    			var xhr = $.ajax({
    				url : action+"&empId="+ empId,
    				cache : false,
    				success : function(data) {
    					document.getElementById(id).innerHTML = data;
    					if(data.length>1) {
    						document.getElementById(id1).value='';
    					}
    				}
    			});
    		}
       }
     
	 
	function isNumberKey(evt) {
	   var charCode = (evt.which) ? evt.which : event.keyCode;
	   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46) {
	      return false;
	   }
	   return true;
	}
	
	function isOnlyNumberKey(evt) {
	   var charCode = (evt.which) ? evt.which : event.keyCode;
	   if (charCode <= 31 || (charCode >= 48 && charCode <= 57)) {
	      return true; 
	   }
	   return false;
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
	
	function callDatePicker() {
		// Created By Dattatray Date : 13-July-2021 Note : added endDate:'+0d' in memberDob and childDob
		$("input[name=memberDob]").datepicker({format: 'dd/mm/yyyy', endDate:'+0d', yearRange: '1950:<%=currentYear%>', changeYear: true});
		$("input[name=childDob]").datepicker({format: 'dd/mm/yyyy', endDate:'+0d', yearRange: '1950:<%=currentYear%>', changeYear: true});
		$("input[name=prevCompanyFromDate]").datepicker({format: 'dd/mm/yyyy', yearRange: '1950:<%=currentYear%>', changeYear: true});
	    
	}

	/* startDate : new Date('01/01/1996'), 
    endDate : new Date(dboMaxDate) */
	
	$(function() {

		var dboMaxDate = '<%=(String)request.getAttribute("strDOBMaxDate")%>';
		var currDate = '<%=(String)request.getAttribute("strCurrDate")%>';
	    $( "#empStartDate" ).datepicker({format: 'dd/mm/yyyy', yearRange: '<%=(String)request.getAttribute("dobYear")%>:<%=uF.parseToInt(currentYear)+1%>', changeYear: true}); 
	    <%-- $( "#empDateOfBirth" ).datepicker({format: 'dd/mm/yyyy',yearRange: '<%=uF.parseToInt(currentYear)-80%>:<%=uF.parseToInt(currentYear)-14%>', changeYear: true, defaultDate: new Date("01/01/<%=""+(uF.parseToInt(currentYear)-80)%>"), --%>
	    $( "#empDateOfBirth" ).datepicker({format: 'dd/mm/yyyy', 
	    	changeYear: true,
		    endDate : new Date(dboMaxDate)
	    }).on('changeDate', function (selected) {
		    var minDate = new Date(selected.date.valueOf());
		   /*  $('#empDateOfMarriage').datepicker('setStartDate', minDate);
		    $('#empDateOfMarriage').datepicker('setEndDate', new Date(currDate)); */
		    $('#empDateOfMarriage').datepicker('setStartDate', minDate);
		    $('#empDateOfMarriage').datepicker('setEndDate', '+0d');// Created by Dattatray Date : 13-July-2021 Note : endDate changed
		});
	    
	    $( "#empDateOfMarriage").datepicker({format: 'dd/mm/yyyy',
	    	/* changeYear: true, */
	    	endDate : '+0d'// Created by Dattatray Date : 13-July-2021 Note : changeYear commited and endDate changed
	    });
	    
	    
	    $("input[name=prevCompanyFromDate]").datepicker({format: 'dd/mm/yyyy', 
	    	changeYear: true,
	    	endDate : new Date(currDate)
	    });
	    
	 // Created By Dattatray Date : 13-July-2021 Note : added endDate:'+0d' in father,motherDob,spouseDob,DobmemberDob and childDob
	    $("input[name=fatherDob]").datepicker({format: 'dd/mm/yyyy',endDate: '+0d'});
	    $("input[name=motherDob]").datepicker({format: 'dd/mm/yyyy',endDate: '+0d'});
	    $("input[name=spouseDob]").datepicker({format: 'dd/mm/yyyy',endDate: '+0d'});
	    $("input[name=memberDob]").datepicker({format: 'dd/mm/yyyy',endDate: '+0d'});
	    $("input[name=childDob]").datepicker({format: 'dd/mm/yyyy',endDate: '+0d'});
	    $("input[name=empPassportExpiryDate]").datepicker({format: 'dd/mm/yyyy'});
	    
	    $("#empSeparationDate").datepicker({format: 'dd/mm/yyyy'});
	    $("#empConfirmationDate").datepicker({format: 'dd/mm/yyyy'});
	    $("#empActConfirmDate").datepicker({format: 'dd/mm/yyyy'});
	    $("#empPromotionDate").datepicker({format: 'dd/mm/yyyy'});
	    $("#empIncrementDate").datepicker({format: 'dd/mm/yyyy'});
	    $("input[name=strRenewalDate]").datepicker({format: 'dd/mm/yyyy'});
	    
	});
	
	function addPrevEmployment() {
		prevcnt = parseInt(prevcnt)+1;
		var divTag = document.createElement("div");
		divTag.id = "col_prev_employer_"+prevcnt;
		divTag.setAttribute("style", "float:left");
		divTag.innerHTML = "<%=request.getAttribute("sbPrevEmployment")%>" + 
						"<tr><td></td><td class=\"txtlabel\">"+
						"<a href=\"javascript:void(0)\"  onclick=\"removePrevEmployment(this.id)\" id=\""+prevcnt+"\" class=\"remove-font\" ></a>"+
						"<a href=\"javascript:void(0)\" style=\"float: right\" onclick=\"addPrevEmployment()\" class=\"add-font\"></a></td>";
						"</tr>" + 
					     "</table>";
		document.getElementById("div_prev_employment").appendChild(divTag);
		document.getElementById("prevCompanyCountry").id="prevCompanyCountry"+prevcnt;
		document.getElementById("prevEmpmentstateTD").id="prevEmpmentstateTD"+prevcnt;
		document.getElementById("prevCompanyCountry"+prevcnt).setAttribute("onchange", "getContentAcs('prevEmpmentstateTD"+prevcnt+"','GetStatesCountrywise.action?country='+this.value)");
		callDatePicker();
	}

	function addEmpReferences() {
		empRefCnt = parseInt(empRefCnt)+1;
		var divTag = document.createElement("div");
		divTag.id = "emp_references_div_"+empRefCnt;
		divTag.setAttribute("style", "float:left");
		
		var strEmpRef = "<%=request.getAttribute("sbEmpReferences")%>" + 
			"<tr><td></td><td class=\"txtlabel\">";
		<% if(uF.parseToBoolean(hmFeatureStatus.get("SHOW_DEFAULT_TWO_REFERENCE_IN_ADD_EMPLOYEE"))) { %>
			if(empRefCnt>1) {
				strEmpRef = strEmpRef + "<a href=\"javascript:void(0)\" style=\"float: right\" onclick=\"removeEmpReferences(this.id)\" id=\""+empRefCnt+"\" class=\"remove-font\" ></a>";
			}
		<% } else { %>
			strEmpRef = strEmpRef + "<a href=\"javascript:void(0)\" style=\"float: right\" onclick=\"removeEmpReferences(this.id)\" id=\""+empRefCnt+"\" class=\"remove-font\" ></a>";
		<% } %>
		strEmpRef = strEmpRef + "<a href=\"javascript:void(0)\" style=\"float: right\" onclick=\"addEmpReferences()\" class=\"add-font\"></a></td>";
			"</tr>" + 
		    "</table>";
		
		divTag.innerHTML = strEmpRef;
		document.getElementById("div_emp_references").appendChild(divTag);
		document.getElementById("refCompany").id="refCompany"+empRefCnt;
		document.getElementById("refCompOtherTR").id="refCompOtherTR"+empRefCnt;
		document.getElementById("refCompanyOther").id="refCompanyOther"+empRefCnt;
		
		document.getElementById("refCompany"+empRefCnt).setAttribute("onchange", "showOtherCompTextField(this.value, '"+empRefCnt+"');");
	}


	function removeEmpReferences(removeId) {
		var remove_elem = "emp_references_div_" + removeId;
		var row_document = document.getElementById(remove_elem); 
		document.getElementById("div_emp_references").removeChild(row_document);
	}

	function removePrevEmployment(removeId) {
		var remove_elem = "col_prev_employer_" + removeId;
		var row_document = document.getElementById(remove_elem); 
		document.getElementById("div_prev_employment").removeChild(row_document);
	}
	    
	
	function setcompanyTodd(val) {
		var val1=val.split("/");
	    $("input[name=prevCompanyToDate]").datepicker({format: 'dd/mm/yyyy', yearRange: val1[2]+':<%=currentYear%>', changeYear: true});
	}

	function showMarriageDate(){
		if(document.getElementById("empMaritalStatus")) {
			if(document.frmPersonalInfo.empMaritalStatus.options[document.frmPersonalInfo.empMaritalStatus.options.selectedIndex].value=='M'){
				document.getElementById("trMarriageDate").style.display = 'table-row';
			}else{
				if(document.getElementById("trMarriageDate")){
					document.getElementById("trMarriageDate").style.display = 'none';
				}
			}
		}
	}
	
	
	function addSkills() {
		skillcnt = parseInt(skillcnt)+1;
		var trTag = document.createElement("tr");
	    trTag.id = "row_skill_"+skillcnt;
	    trTag.setAttribute("class", "row_skill");
	    trTag.innerHTML = 	"<%=request.getAttribute("sbSkills")%>" +
	    			    	"<td><a href=\"javascript:void(0)\" onclick=\"addSkills()\" class=\"add-font\"></a><a href=\"javascript:void(0)\" onclick=\"removeSkills(this.id)\" id=\""+skillcnt+"\" class=\"remove-font\"></a></td>"; 
	    document.getElementById("table-skills").appendChild(trTag);
	    
	}

	function removeSkills(removeId) {
		
		var remove_elem = "row_skill_"+removeId;
		var row_skill = document.getElementById(remove_elem); 
		document.getElementById("table-skills").removeChild(row_skill);
		
	}


	function addHobbies() {
		hobbiescnt = parseInt(hobbiescnt)+1;
		var trTag = document.createElement("tr");
	    trTag.id = "row_hobby_"+hobbiescnt;
	    trTag.setAttribute("class", "row_hobby");
		trTag.innerHTML = 	"<td><input type=\"text\" style=\"width: 180px; \" name=\"hobbyName\"></input></td>" +   			    	
	    			    	"<td><a href=\"javascript:void(0)\" onclick=\"addHobbies()\" class=\"add-font\"></a>" +
	 						"<a href=\"javascript:void(0)\" onclick=\"removeHobbies(this.id)\" id=\""+hobbiescnt+"\" class=\"remove-font\"></a></td></tr>";
		                    
	    document.getElementById("table-hobbies").appendChild(trTag);
	    
	}

	function removeHobbies(removeId) {
		var remove_elem = "row_hobby_"+removeId;
		var row_hobby = document.getElementById(remove_elem); 
		document.getElementById("table-hobbies").removeChild(row_hobby);
	}

	
	function addLanguages() {
		languagecnt = parseInt(languagecnt)+1;
		var trTag = document.createElement("tr");
		
	    trTag.id = "row_language_"+languagecnt;
	    trTag.setAttribute("class", "row_language");
		trTag.innerHTML = 	"<td><input type=\"text\" name=\"languageName\" ></input></td>" + 
			"<td align=\"center\"><input type=\"checkbox\" name=\"isReadcheckbox\" value=\"1\"  id=\"isRead_"+languagecnt+"\" onchange=\"showHideHiddenField(this.id)\" />" +
			"<input type=\"hidden\" value=\"0\" name=\"isRead\" id=\"hidden_isRead_"+languagecnt+"\" /></td>" +
			"<td  align=\"center\"><input type=\"checkbox\" name=\"isWritecheckbox\" value=\"1\" id=\"isWrite_"+languagecnt+"\" onchange=\"showHideHiddenField(this.id)\" />"+
			"<input type=\"hidden\" value=\"0\" name=\"isWrite\" id=\"hidden_isWrite_"+languagecnt+"\" /></td>" +
			"<td  align=\"center\"><input type=\"checkbox\" name=\"isSpeakcheckbox\" value=\"1\" id=\"isSpeak_"+languagecnt+"\" onchange=\"showHideHiddenField(this.id)\" />"+
			"<input type=\"hidden\" value=\"0\" name=\"isSpeak\" id=\"hidden_isSpeak_"+languagecnt+"\" /></td>" +
			"<td  align=\"center\"><input type=\"radio\" name=\"isMotherToungeRadio\" value=\"1\" id=\"isMotherTounge_\" onchange=\"showHideHiddenField1(this.id, '"+languagecnt+"')\" />"+
			"<input type=\"hidden\" value=\"0\" name=\"isMotherTounge\" id=\"hidden_isMotherTounge_"+languagecnt+"\" /></td>" +
			"<td  align=\"center\"><a href=\"javascript:void(0)\" onclick=\"addLanguages()\" class=\"add-font\"></a><a href=\"javascript:void(0)\" onclick=\"removeLanguages(this.id)\" id=\""+languagecnt+"\" class=\"remove-font\" ></a></td>"; 

		document.getElementById("table-language").appendChild(trTag);
	    document.getElementById("hideLanguageRowCount").value = parseInt(languagecnt)+1;
	}

	function removeLanguages(removeId) {
		
		var remove_elem = "row_language_"+removeId;
		var row_language = document.getElementById(remove_elem); 
		document.getElementById("table-language").removeChild(row_language);
		
	}

	function addEducation() {
		var educationcnt = document.getElementById("educationCnt").value;
		educationcnt = parseInt(educationcnt)+1;
		var trTag = document.createElement("tr");
		trTag.id = "row_education_"+educationcnt;
		trTag.setAttribute("class", "row_education");
		trTag.innerHTML = "<td><select name=\"degreeName\" onchange=\"checkEducation(this.value, "+educationcnt+")\"> "+"<%=request.getAttribute("sbdegreeDuration")%>"
			+"<td><input type=\"hidden\" name=\"degreeCertiStatus\" id=\"degreeCertiStatus"+educationcnt+"\" value=\"0\">"
			+"<div id=\"degreeCertiDiv"+educationcnt+"\"><input type=\"hidden\" name=\"degreeCertiSubDivCnt"+educationcnt+"\" id=\"degreeCertiSubDivCnt"+educationcnt+"\" value=\"0\" />"
			+"<div id=\"degreeCertiSubDiv"+educationcnt+"_0\"><input type=\"file\" name=\"degreeCertificate"+educationcnt+"\" id=\"degreeCertificate"+educationcnt+"\" onchange=\"fillFileStatus('degreeCertiStatus"+educationcnt+"');\" />"
			+"<a href=\"javascript:void(0)\" onclick=\"addEducationCerti('"+educationcnt+"')\" class=\"add-font\"></a></div></div></td>"
			+"<td><a href=\"javascript:void(0)\" onclick=\"addEducation()\" class=\"add-font\" ></a>"
			+"<a href=\"javascript:void(0)\" onclick=\"removeEducation(this.id)\" id=\""+educationcnt+"\" class=\"remove-font\" ></a></td>";
		var trTag1 = document.createElement("tr");
		trTag1.id = "degreeNameOtherTR"+educationcnt;
		trTag1.setAttribute("class", "hide-tr");
		trTag1.innerHTML = "<td colspan=\"1\" style=\"text-align: right;\">Enter Academics Degree:</td><td colspan=\"5\"> "+
			"<input type=\"text\" name=\"degreeNameOther\" style=\"height: 25px;\"></td>"+
			"<td><input type=\"file\" name=\"degreeCertificate"+educationcnt+"\" id=\"degreeCertificate"+educationcnt+"\" /></td>";
		
		/* document.getElementById("degreeCertiStatus").id = "degreeCertiStatus"+educationcnt;
		document.getElementById("degreeCertiStatus").name = "degreeCertiStatus"+educationcnt;
		document.getElementById("degreeCertificate").id = "degreeCertificate"+educationcnt;
		document.getElementById("hidedegreeNameOther").id = "hidedegreeNameOther"+educationcnt; */
				
	    document.getElementById("table-education").appendChild(trTag);
	    document.getElementById("table-education").appendChild(trTag1);
	    
	    document.getElementById("educationCnt").value = educationcnt;
	}

	function checkEducation(value, count){
		if(value=="other") {
			document.getElementById("degreeNameOtherTR"+count).style.display = "table-row";
			document.getElementById("hidedegreeNameOther"+count).disabled = true;
		} else {
			document.getElementById("degreeNameOtherTR"+count).style.display = "none";
			document.getElementById("hidedegreeNameOther"+count).disabled = false;
		}
	}

	function removeEducation(removeId) {
		var remove_elem = "row_education_"+removeId;
		var row_education = document.getElementById(remove_elem); 
		document.getElementById("table-education").removeChild(row_education);
		
		if(document.getElementById("degreeNameOtherTR"+removeId)) {
			var remove_elem1 = "degreeNameOtherTR"+removeId;
			var row_education1 = document.getElementById(remove_elem1); 
			document.getElementById("table-education").removeChild(row_education1);
		}
	} 
	
	
	function addEducationCerti(count) {
		var cnt = document.getElementById("degreeCertiSubDivCnt"+count).value;
		cnt = parseInt(cnt)+1;
		var divTag = document.createElement("div");
		divTag.id = "degreeCertiSubDiv"+count+"_"+cnt;
		//divTag.setAttribute("style", "float: left;");
		divTag.innerHTML = "<input type=\"file\" name=\"degreeCertificate"+count+"\" id=\"degreeCertificate"+count+"\" onchange=\"fillFileStatus('degreeCertiStatus"+count+"');\" />" +
			"<a href=\"javascript:void(0);\" onclick=\"addEducationCerti('"+count+"');\" class=\"add-font\"></a>"+
			"<a href=\"javascript:void(0);\" onclick=\"removeEducationCerti('"+count+"', 'degreeCertiSubDiv"+count+"_"+cnt+"');\" class=\"remove-font\"></a>";
		document.getElementById("degreeCertiDiv"+count).appendChild(divTag);
		
		document.getElementById("degreeCertiSubDivCnt"+count).value = cnt;
	}
	
	
	function removeEducationCerti(count, removeId) {
		var removeSubDiv = document.getElementById(removeId); 
		document.getElementById("degreeCertiDiv"+count).removeChild(removeSubDiv);
	} 
	
	
	function addSibling() {
		siblingcnt = parseInt(siblingcnt)+1;
		var divTag = document.createElement("div");
		divTag.id = "col_family_siblings_"+siblingcnt;
		divTag.setAttribute("style", "float: left;");
	    divTag.innerHTML = 	"<%=request.getAttribute("sbSibling")%>" +
	 			"<td><a href=\"javascript:void(0)\" onclick=\"removeSibling(this.id)\" id=\""+siblingcnt+"\" class=\"remove-font\" ></a></td></tr>" +
	            "</table>"; 
		document.getElementById("div_id_family").appendChild(divTag);
		callDatePicker();
	}

	function removeSibling(removeId) {
		var remove_elem = "col_family_siblings_"+removeId;
		var col_family_siblings = document.getElementById(remove_elem); 
		document.getElementById("div_id_family").removeChild(col_family_siblings);
		
	}  
	
	function addChildren() {
		
		childcnt = parseInt(childcnt)+1;
		var divTag = document.createElement("div");
		divTag.id = "col_family_child_"+childcnt;
		divTag.setAttribute("style", "float: left;");
	    
	    divTag.innerHTML = 	"<%=request.getAttribute("sbChildren")%>" +
	 			"<td><a href=\"javascript:void(0)\" onclick=\"removeChildren(this.id)\" id=\""+childcnt+"\" class=\"remove-font\" ></a></td></tr>" +
	            "</table>"; 

		document.getElementById("div_id_child").appendChild(divTag);
		callDatePicker();
		
	}

	function removeChildren(removeId) {
		
		var remove_elem = "col_family_child_"+removeId;
		var col_family_child = document.getElementById(remove_elem); 
		document.getElementById("div_id_child").removeChild(col_family_child);
		
	} 
	

	function addDocuments(addType) {
		
		var documentCnt = document.getElementById("documentCnt").value;
		documentCnt = (parseInt(documentCnt)+1);
	    var table = document.getElementById("supportDocTable");
	    var rowCount = table.rows.length;
	    var row = table.insertRow(rowCount);
	    
        row.id = "row_document"+rowCount;
        var cell1 = row.insertCell(0);
        cell1.setAttribute("class", "txtlabel alignRight");

       cell1.innerHTML = "<input type=\"hidden\" name=\"idDocType\" value=\"<%=IConstants.DOCUMENT_OTHER%>\"></input><input type=\"text\" class=\"validateRequired text-input\" style=\"width: 180px; \" name=\"idDocName\"></input>";
       
       var cell2 = row.insertCell(1);
       cell2.setAttribute("class", "txtlabel alignRight");
       cell2.setAttribute("style", "text-align: -moz-center");
       cell2.innerHTML = "<input type=\"file\" name=\"idDoc"+documentCnt+"\" id=\"idDoc"+documentCnt+"\" onchange=\"fillFileStatus('idDocStatus"+documentCnt+"')\" /><input type=\"hidden\" name=\"idDocStatus\" id=\"idDocStatus"+documentCnt+"\" value=\"0\"></input>";
       if(addType != null && addType == 'EDIT') {
    	   var cell3 = row.insertCell(2);
    	   cell3.innerHTML = "";
    	   
    	   var cell4 = row.insertCell(3);
    	   cell4.innerHTML = "";
    	   
    	   var cell5 = row.insertCell(4);
	       cell5.setAttribute("class", "txtlabel alignRight");
	       cell5.innerHTML = "<a href=\"javascript:void(0);\" onclick=\"addDocuments('"+addType+"');\" class=\"add-font\"></a>"
	       	+"<a href=\"javascript:void(0)\" onclick=\"removeDocuments('row_document"+rowCount+"');\" id=\""+documentCnt+"\" class=\"remove-font\"></a>";
       } else {
	       var cell3 = row.insertCell(2);
	       cell3.setAttribute("class", "txtlabel alignRight");
	       cell3.innerHTML = "<a href=\"javascript:void(0)\" onclick=\"addDocuments('"+addType+"');\" class=\"add-font\"></a>"
	       	+"<a href=\"javascript:void(0)\" onclick=\"removeDocuments('row_document"+rowCount+"');\" id=\""+documentCnt+"\" class=\"remove-font\"></a>";
       }
       document.getElementById("documentCnt").value = documentCnt;
	}


	function removeDocuments(rowid) {
	    var table =  document.getElementById('supportDocTable');
	    var row = document.getElementById(rowid);
	    table.deleteRow(row.rowIndex);
	    
	} 
	 
	function deleteDocuments(removeId) {
		if(confirm('Are you sure, you want to delete this document?')) {
			getContent('removeDivDocument_'+removeId,'DeleteDocuments.action?documentId='+removeId);
		}
	} 

	function deleteMedicalDocuments(mediDocId) {
		if(confirm('Are you sure, you want to delete this document?')) {
			getContent('removeDivMedicalDocument_'+mediDocId,'DeleteMedicalDocuments.action?mediDocId='+mediDocId);
		}
	} 

	function showHideHiddenField(fieldId) {
		if(document.getElementById(fieldId).checked) {
			document.getElementById( "hidden_"+fieldId).value="1";
		} else {
			document.getElementById( "hidden_"+fieldId).value="0";
		}
	}
	 
	 
	 function showHideHiddenField1(fieldId, fieldCnt) {
		 var langCount = document.getElementById("hideLanguageRowCount").value;
		 for(var i=0; i<langCount; i++) {
			 if(i==fieldCnt) {
				 document.getElementById("hidden_"+fieldId+i).value = "1";
			 } else {
				 if(document.getElementById("hidden_"+fieldId+i)) {
					 document.getElementById("hidden_"+fieldId+i).value = "0";
				 }
			 }
		 }
	} 

	 function checkRadio(obj,val){
		document.getElementById(val).disabled=true;
		if(obj.value=='false'){
			document.getElementById(val).disabled=true;
		document.getElementById(val+'File').disabled=true;

		}else{
			document.getElementById(val).disabled=false;
			document.getElementById(val+'File').disabled=false;

		}
			
	} 

	 function fillFileStatus(ids){
		document.getElementById(ids).value=1;
	} 

	 function copyAddress(obj){    
			if(obj.checked){
				
				var sel=document.getElementById("country");
				for(var i = 0, j = sel.options.length; i < j; ++i) {
			        if(sel.options[i].innerHTML === document.getElementById("countryTmp").options[document.getElementById("countryTmp").selectedIndex].text) {
			           sel.selectedIndex = i;
			           break;
			        }
			    }
				
				var country = document.getElementById("country").value;
				var action = "GetStates.action?country="+country+"&type=PADD";
				getContentAcs('stateTD',action);
				
				var sel1 = document.getElementById("state");
				//alert("sel1 ===>> " + sel1);
				for(var i = 0, j = sel1.options.length; i < j; ++i) {
			        if(sel1.options[i].innerHTML === document.getElementById("stateTmp").options[document.getElementById("stateTmp").selectedIndex].text) {
			           sel1.selectedIndex = i;
			           break;
			        }
			    }
				//alert("sel11 ===>> " + sel1);
				document.getElementById("empAddress1").value = document.getElementById("empAddress1Tmp").value;
				//alert("sel12 ===>> " + sel1);
				document.getElementById("city").value = document.getElementById("cityTmp").value;
				//alert("sel13 ===>> " + sel1);
				document.getElementById("empPincode").value = document.getElementById("empPincodeTmp").value;
				//alert("empPincodeTmp ===>> " + document.getElementById("empPincodeTmp").value);
				
				
			} else {
				document.getElementById("empAddress1").value = '';
				document.getElementById("city").value = '';
				document.getElementById("empPincode").value = '';
				
				var sel=document.getElementById("country");
				for(var i = 0, j = sel.options.length; i < j; ++i) {
			        if(sel.options[i].innerHTML === document.getElementById("countryTmp").options[0].text) {
			           sel.selectedIndex = i;
			           break;
			        }
			    }
				
				sel=document.getElementById("state");
				for(var i = 0, j = sel.options.length; i < j; ++i) {
			        if(sel.options[i].innerHTML === document.getElementById("stateTmp").options[0].text) {
			           sel.selectedIndex = i;
			           break;
			        }
			    }
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
	 
	 
	 
	 // ************* From Add Employee Page ************************* 28-09-16
	 
	 function showHideBankName() {
		 
		 var obj = document.getElementById("idEmpPaymentMode");
			/* Start Dattatray */
			var BankName="<%=(String)request.getAttribute("empBankName")%>";
			var BankName2="<%=(String) request.getAttribute("empBankName2")%>";
			/* End Dattatray */
			if(obj.options[obj.selectedIndex].value==1){
				
				document.getElementById("idBankName").style.display="table-row";
				document.getElementById("idEmpAccount").style.display="table-row";
				document.getElementById("idBankName2").style.display="table-row";
				document.getElementById("idEmpAccount2").style.display="table-row";
				/* Start Dattatray */
				if(BankName == "-1"){
					document.getElementById("idEmpIFSC").style.display="table-row";
					document.getElementById("idEmpOtherBankNameTr").style.display="table-row";
					document.getElementById("idEmpOtherBankBranchTr").style.display="table-row";
				}
				if(BankName2 == "-1"){
					document.getElementById("idEmpIFSC2").style.display="table-row";
					document.getElementById("idEmpOtherBankNameTr2").style.display="table-row";
					document.getElementById("idEmpOtherBankBranchTr2").style.display="table-row";
				}
				/* End Dattatray  */
			}else {
				if(document.getElementById("empBankName")){
					document.getElementById("empBankName").value='';
				}
				if(document.getElementById("idEmpOtherBankName")){
					document.getElementById("idEmpOtherBankName").value='';
				}
				if(document.getElementById("idEmpOtherBankBranch")){
					document.getElementById("idEmpOtherBankBranch").value='';
				}
				if(document.getElementById("idEmpBankAcctNbr")){
					document.getElementById("idEmpBankAcctNbr").value='';
				}
				if(document.getElementById("idEmpBankIFSCNbr")){
					document.getElementById("idEmpBankIFSCNbr").value='';
				}
				if(document.getElementById("empBankName2")){
					document.getElementById("empBankName2").value='';
				}
				if(document.getElementById("idEmpOtherBankName2")){
					document.getElementById("idEmpOtherBankName2").value='';
				}
				if(document.getElementById("idEmpOtherBankBranch2")){
					document.getElementById("idEmpOtherBankBranch2").value='';
				}
				if(document.getElementById("idEmpBankAcctNbr2")){
					document.getElementById("idEmpBankAcctNbr2").value='';
				}
				if(document.getElementById("idEmpBankIFSCNbr2")){
					document.getElementById("idEmpBankIFSCNbr2").value='';
				}
				
				document.getElementById("idBankName").style.display="none";
				document.getElementById("idEmpAccount").style.display="none";
				document.getElementById("idBankName2").style.display="none";
				document.getElementById("idEmpAccount2").style.display="none";
				document.getElementById("idEmpIFSC").style.display="none";
				document.getElementById("idEmpIFSC2").style.display="none";
				document.getElementById("idEmpOtherBankNameTr").style.display="none";
				document.getElementById("idEmpOtherBankBranchTr").style.display="none";
				document.getElementById("idEmpOtherBankNameTr2").style.display="none";
				document.getElementById("idEmpOtherBankBranchTr2").style.display="none";
			}
	}
		
	 
	 //*************************showHideBankAccNo function created 30th april 2019***********
	 function showHideBankAccNo(){
		 
	 		var obj = document.getElementById("empBankName");
	/* ===start parvez date: 12-08-2022=== */			
	 		if(obj.options[obj.selectedIndex].value == -1){
				 
				document.getElementById("idEmpBankIFSCNbr").value='';
				document.getElementById("idEmpBankAcctNbr").value='';
				document.getElementById("idEmpOtherBankName").value='';
				document.getElementById("idEmpOtherBankBranch").value='';
				
				document.getElementById("idEmpIFSC").style.display="table-row";
				document.getElementById("idEmpAccount").style.display="table-row";
				document.getElementById("idEmpOtherBankNameTr").style.display="table-row";
				document.getElementById("idEmpOtherBankBranchTr").style.display="table-row";
				
			}else{
				
				 document.getElementById("idEmpBankIFSCNbr").value='';
				 document.getElementById("idEmpBankAcctNbr").value='';
				 document.getElementById("idEmpOtherBankName").value='';
				
				document.getElementById("idEmpAccount").style.display="table-row";
				document.getElementById("idEmpIFSC").style.display="none";
				document.getElementById("idEmpOtherBankNameTr").style.display="none";
			}
	 /* ===end parvez date: 12-08-2022=== */
	 }
	 
	 function showHideBankAccNo_second(){
	 		var obj1 = document.getElementById("empBankName2");	 
	 		
	 	/* ===start parvez date: 12-08-2022=== */
			if(obj1.options[obj1.selectedIndex].value == -1){
				
				document.getElementById("idEmpBankIFSCNbr2").value='';
				document.getElementById("idEmpBankAcctNbr2").value='';
				document.getElementById("idEmpOtherBankName2").value='';
				document.getElementById("idEmpOtherBankBranch2").value='';
				
				document.getElementById("idEmpIFSC2").style.display="table-row";
				document.getElementById("idEmpAccount2").style.display="table-row";
				document.getElementById("idEmpOtherBankNameTr2").style.display="table-row";
				document.getElementById("idEmpOtherBankBranchTr2").style.display="table-row";
				
			}else{
				document.getElementById("idEmpBankIFSCNbr2").value='';
				document.getElementById("idEmpBankAcctNbr2").value='';
				document.getElementById("idEmpOtherBankName2").value='';
				document.getElementById("idEmpOtherBankBranch2").value='';
				
				document.getElementById("idEmpIFSC2").style.display="none";
				document.getElementById("idEmpAccount2").style.display="table-row";
				document.getElementById("idEmpOtherBankNameTr2").style.display="none";
				document.getElementById("idEmpOtherBankBranchTr2").style.display="none";
				
			}
	/* ===end parvez date: 12-08-2022=== */
	 } 
	 
	//add extra here*******************
	function showOtherCompTextField(val, count) {
		if(val == 'Other') {
			document.getElementById("refCompOtherTR"+count).style.display="table-row";
		} else {
			document.getElementById("refCompOtherTR"+count).style.display="none";
		}
	}
	 
	 	function getDataFromAjax(val) {
			getWlocation(val);
			createEmpCodeByOrg();
		}

	 	function getDataFromAjaxOneStep(val) {
	 		var strOrg = document.getElementById("strOrg").value;
	 		getLevelOneStep(strOrg);
			createEmpCodeByOrg();
		}
		   
		function createEmpCodeByOrg() {
			var strOrg = document.getElementById("strOrg").value;
			var empContractor = document.getElementById("empContractor").value;
			var validReqOpt = document.getElementById("empCodeValidReqOpt").value;
			getContent('empCodeTD', 'CreateEmployeeCodeByOrg.action?empContractor='+empContractor+'&strOrg='+strOrg+'&validReqOpt='+validReqOpt);
		}
		
		function getWlocation(val) {
		 	var xmlhttp = GetXmlHttpObject();
			if (xmlhttp == null) {
				alert("Browser does not support HTTP Request");
				return;
			} else {
				var xhr = $.ajax({
					url : 'GetOrgWLocationList.action?type=AEMP&OID='+val,
					cache : false,
					success : function(data) {
						document.getElementById('idOrgId').innerHTML = data;
						getdepartment(val);
					}
				});
			}
		}
		
		function getdepartment(val){
			var xmlhttp = GetXmlHttpObject();
			if (xmlhttp == null) {
				alert("Browser does not support HTTP Request");
				return;
			} else {
				var xhr = $.ajax({
					url : 'GetOrgDepartmentList.action?OID='+val,
					cache : false,
					success : function(data) {
						document.getElementById('idDepartment').innerHTML = data;
						getLevel(val);
					}
				});
			}
		 }
		function getLevel(val){
			 var xmlhttp = GetXmlHttpObject();
				if (xmlhttp == null) {
					alert("Browser does not support HTTP Request");
					return;
				} else {
					var xhr = $.ajax({
						url : 'GetOrgLevelList.action?OID='+val,
						cache : false,
						success : function(data) {
							document.getElementById('idLevel').innerHTML = data;
							getService(val);
						}
					});
				}
		 }
		
		function getLevelOneStep(val){
		 var xmlhttp = GetXmlHttpObject();
			if (xmlhttp == null) {
				alert("Browser does not support HTTP Request");
				return;
			} else {
				var xhr = $.ajax({
					url : 'GetOrgLevelList.action?OID='+val,
					cache : false,
					success : function(data) {
						document.getElementById('idLevel').innerHTML = data;
						getHRList(val);
						getHODList(val);
						getSupervisorList();
						showCurrentOrgLoaction();
					}
				});
			}
		 }
		
		function getDesig(val){
			 var xmlhttp = GetXmlHttpObject();
				if (xmlhttp == null) {
					alert("Browser does not support HTTP Request");
					return;
				} else {
					var xhr = $.ajax({
						url : 'GetDesigList.action?strLevel='+val,
						cache : false,
						success : function(data) {
							if(document.getElementById('myDesig')) {
								document.getElementById('myDesig').innerHTML = data;
							}
							
							if(document.getElementById('desigIdV')) {
								document.getElementById('desigIdV').innerHTML=data;
							}
							getGrades(val);
						}
					});
				}
		}
		
		function getGrades(val) {
			var orgId = document.getElementById("strOrg").value;
			var empId = document.getElementById("empId").value;
			
			 var xmlhttp = GetXmlHttpObject();
				if (xmlhttp == null) {
					alert("Browser does not support HTTP Request");
					return;
				} else {
					var xhr = $.ajax({
						url : 'GetGradeList.action?DId='+val,
						cache : false,
						success : function(data) {
							document.getElementById('myGrade').innerHTML = data;					
						}
					});
				}
				window.setTimeout(function() {
					getContentAcs('designKRADiv', 'GetDesignationKRAs.action?desigId='+ val + '&orgId=' + orgId + '&empId=' + empId);
				}, 200);
			}

		function getService(val){
			var xmlhttp = GetXmlHttpObject();
			if (xmlhttp == null) {
				alert("Browser does not support HTTP Request");
				return;
			} else {
				var action = 'GetOrgServiceList.action?OID='+val; 
				var xhr = $.ajax({
					url : action,
					cache : false,
					success : function(data) {
						document.getElementById('idService').innerHTML = data;
						getHRList(val);
						getHODList(val);
						getSupervisorList();
						window.setTimeout(function() {
							getLeaves();
						}, 500);
					}
				});
			}
		}
		
		function getDesigAndLeave(val) {
			var strOrg = document.getElementById("strOrg").value; 
			getContent('myDesig','GetDesigList.action?strLevel='+val+'&fromPage=AddEmp');
			window.setTimeout(function() {
				//getleaveTypeByLevel(strOrg);
				getLeaves();
			}, 400);
		}	


		function getHRList(val) {
			var location = document.getElementById("wLocation").value;
			 var xmlhttp = GetXmlHttpObject();
				if (xmlhttp == null) {
					alert("Browser does not support HTTP Request");
					return;
				} else {
					var hrValidReq = document.getElementById('hrValidReq').value;
					var xhr = $.ajax({
						url : 'GetEmployeeList.action?type=HR&f_org='+val+'&hrValidReq='+hrValidReq+'&location='+location,
						cache : false,
						success : function(data) {
							document.getElementById('hrListID').innerHTML = data;
							
						}
					});
				}
			}
		
		function getHODList(val) {
		 var xmlhttp = GetXmlHttpObject();
			if (xmlhttp == null) {
				alert("Browser does not support HTTP Request");
				return;
			} else {
				var hodValidReq = document.getElementById('hodValidReq').value;
				var strEmpId = document.getElementById('empId').value;
				var xhr = $.ajax({
					url : 'GetEmployeeList.action?type=HOD&f_org='+val+'&hodValidReq='+hodValidReq+'&strEmpId='+strEmpId,
					cache : false,
					success : function(data) {
						document.getElementById('hodListID').innerHTML = data;
					}
				});
			}
		}
		
		
		function getSupervisorList() {
			var xmlhttp = GetXmlHttpObject();
			if (xmlhttp == null) {
				alert("Browser does not support HTTP Request");
				return;
			} else {
				var orgId = document.getElementById("strOrg").value;
				var wLocation ="";
				if(document.getElementById("wLocation")){
					wLocation = document.getElementById("wLocation").value;
				}
				
				var department ="";
				if(document.getElementById("department")){
					department = document.getElementById("department").value;
				}
				
				var level = "";
				if(document.getElementById("levelIdV")){
					level = document.getElementById("levelIdV").value;
				}
				var supervisorValidReq = document.getElementById('supervisorValidReq').value;
				var strEmpId = document.getElementById('empId').value;
				//alert("orgId==>"+orgId);
				
				var xhr = $.ajax({
					url : 'GetEmployeeList.action?type=SUPERVISOR&f_org='+orgId+'&strLocation='+wLocation+'&strDepart='+department
							+'&strLevel='+level+'&supervisorValidReq='+supervisorValidReq+'&strEmpId='+strEmpId,
					cache : false,
					success : function(data) {
						document.getElementById('supervisorIdsSpan').innerHTML = data;
					}
				});
			}
		}
		
		
		function getAttributes(value, count) {
			var orgId = document.getElementById("strOrg").value;
			var action = 'GetElementwiseAttributeList.action?elementID=' + value + '&orgId=' + orgId;
			getContent('attributeDiv'+count, action);
		}
		
		function addNewKRA() {
			var kraCnt = document.getElementById("kracount").value;
				var val=(parseInt(kraCnt)+1);
			    var table = document.getElementById("tbl_all_kras");
			    var rowCount = table.rows.length;
			    var row = table.insertRow(rowCount);
			    row.id="emp_kra_TR"+val;
			    var cell1 = row.insertCell(0);
			    cell1.setAttribute('style', 'border-bottom: 1px solid #EEEEEE' );
			    
			    cell1.innerHTML = "<table class='table'><tr><td>"+"<%=(String)request.getAttribute("elementSelectBox") %>"+
			    "</span></td><td>"+
			    "<span id=\"attributeDiv"+val+"\"><select name=\"elementAttribute\" id=\"elementAttribute"+val+"\" class=\"validateRequired\" style=\"width: 130px !important;\"><option value=\"\">Select Attribute</option></select></span></td></tr>"+
			    "<tr><td colspan=2><p>KRA: <input type=\"text\" name=\"empKRA\" id=\"empKRA"+val+"\" class=\"validateRequired \"/></p>" +
			    "<p>Task: <input type=\"text\" name=\"empKRATask\" id=\"empKRATask"+val+"\" class=\"validateRequired \"/></p>"+
				"<a href=\"javascript:void(0)\" style=\"float: right;\" onclick=\"removeKRA("+row.id+")\" class=\"remove-font\" title=\"Remove KRA\"></a>"+
				"<a href=\"javascript:void(0)\" style=\"float: right;\" onclick=\"addNewKRA()\" class=\"add-font\" title=\"Add New KRA\"></a></span></td></tr>";
			    document.getElementById("kracount").value = val;
			    
			    document.getElementById("goalElements").id="goalElements"+val;
				document.getElementById("goalElements"+val).setAttribute("onchange", "getAttributes(this.value, '"+val+"');");
			}
		 	
		 function removeKRA(trIndex) {
			   //document.getElementById("tbl_all_kras").deleteRow(trIndex);
			 return trIndex.parentNode.removeChild(trIndex);
		 }
		 
		 
	 function showEmpStatus(val,leavesValue) {
		 //alert("val ==>> " + val);
		 if(val == '1') {
			 document.getElementById("trProbationId").style.display = 'table-row';
		 } else {
			 document.getElementById("trProbationId").style.display = 'none';
		 }
		 
		 
		 getLeaves();
	}

	 
	 function changeStatus(id) {
			if (document.getElementById('addFlag' + id).checked == true) {
				document.getElementById('status' + id).value = '1';
			} else {
				document.getElementById('status' + id).value = '0';
			}
		}
		function editDesig() { 
			var orgid = document.getElementById('strOrg').value;
			var desigid = "";
			if(document.getElementById('desigIdV')) {
				desigid = document.getElementById('desigIdV').value;
			}
			if(document.getElementById('strDesignation')) {
				desigid = document.getElementById('strDesignation').value;
			}
			var levelid = document.getElementById('levelIdV').value;
			var empId = document.getElementById('empId').value;
			if(parseInt(desigid) > 0) {
				var dialogEdit = '.modal-body';
	      		 $(dialogEdit).empty();
	      		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	      		 $("#modalInfo").show();
	      		 $(".modal-title").html('Edit Designation');
	      		if($(window).width() >= 1100){
	      			 $(".modal-dialog").width(1100);
	      		 }
	      		$.ajax({
					url : "AddDesig.action?orgId="+orgid+"&operation=E&ID="+desigid+"&param="+levelid+"&fromPage=AE"+"&empId="+empId,
					cache : false,
					success : function(data) {
						$(dialogEdit).html(data);
					}
				});
			}
		}


		function getLeaves(){  	
			var empStatus = $("input:radio[name=empStatus]:checked").val();
			if(parseInt(empStatus) > 0){
				var orgId = document.getElementById('strOrg').value;
				var wlocation = document.getElementById('wLocation').value;
				var level = document.getElementById('levelIdV').value;
				var leavesValidReqOpt = document.getElementById('leavesValidReqOpt').value;
				var empId = document.getElementById('empId').value;
			//alert("leavesValidReqOpt ===>>>> " + leavesValidReqOpt);
				var xmlhttp = GetXmlHttpObject();
				if (xmlhttp == null) {
					alert("Browser does not support HTTP Request");
					return;
				} else {
					var xhr = $.ajax({
						url : "GetLeavesAvailable.action?orgId="+orgId+"&wlocationId="+wlocation+"&levelId="+level+"&empStatus="+empStatus
								+"&leavesValidReqOpt="+leavesValidReqOpt+"&empId="+empId+"&strHeaderLabel=true",
						cache : false,
						success : function(data) {
							document.getElementById('leaveProbationListID').innerHTML = data;
							$("#probationLeaves").multiselect().multiselectfilter();
						}
					});
				}
			}
		}

		function getEmpLeaveBalance() {
			var empId = document.getElementById('empId').value;
			var strLeaves = getSelectedValue("probationLeaves");
			var levelIdV = document.getElementById('levelIdV').value;
			var wLocation = document.getElementById('wLocation').value;
			var strOrg = document.getElementById('strOrg').value;
			var empStartDate = document.getElementById('empStartDate').value;
			var empStatus = $("input:radio[name=empStatus]:checked").val();
			
			var action = 'GetEmpLeavesBalance.action?strLeaves='+strLeaves+'&levelId='+levelIdV;
			action +='&wLocationId='+wLocation+'&strOrg='+strOrg+'&joiningDate='+empStartDate+'&empStatus='+empStatus;
			action +='&strEmpId='+empId;
			
			var xmlhttp = GetXmlHttpObject();
			if (xmlhttp == null) {
				alert("Browser does not support HTTP Request");
				return;
			} else {
				var xhr = $.ajax({
					url : action,
					cache : false,
					success : function(data) {
						document.getElementById('divLeaveBal').innerHTML = data;
					}
				});
			}
		}

		function getSelectedValue(selectId) {
			var choice = document.getElementById(selectId);
			var exportchoice = "";
			for ( var i = 0, j = 0; i < choice.options.length; i++) {
				if (choice.options[i].selected == true) {
					if (j == 0) {
						exportchoice = choice.options[i].value;
						j++;
					} else {
						exportchoice += "," + choice.options[i].value;
						j++;
					}
				}
			}
			return exportchoice;
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

		function showCurrentOrgSelectedDepartment(isHOD, callFrom) {
			if(isHOD == 1) {
				var departId = document.getElementById('department').value;
				var xmlhttp = GetXmlHttpObject();
				if (xmlhttp == null) {
					alert("Browser does not support HTTP Request");
					return;
				} else {
					var xhr = $.ajax({
						url : "GetOrgDepartmentName.action?departId="+departId,
						cache : false,
						success : function(data) {
							document.getElementById('locDivHOD').innerHTML = data;
						}
					});
				}
			} else {
				document.getElementById('locDivHOD').innerHTML = "";
			}
			
			if(callFrom != 'HOD') {
				getSupervisorList();
			}
		}

		
		function showCurrentOrgLoaction(isCXO) { 
			if(isCXO == 1) {	
			 	var xmlhttp = GetXmlHttpObject();
				if (xmlhttp == null) {
					alert("Browser does not support HTTP Request");
					return;
				} else {
					var orgId = document.getElementById('strOrg').value;
					var xhr = $.ajax({
						url : 'GetOrgWLocationList.action?type=CXOLOCATION&strOrgId='+orgId,
						cache : false,
						success : function(data) {
							document.getElementById('locDivCXO').innerHTML = data;
							$("#locationCXO").multiselect().multiselectfilter();
						}
					});
				}
			} else {
				document.getElementById('locDivCXO').innerHTML = "";
			}
		}
		
		function checkCXOHOD(val) {
			if(val == 1) {
				document.getElementById('locDivCXO').style.display = 'block';
				document.getElementById('locDivCXOLbl').style.display = 'block';
				document.getElementById('locDivHOD').innerHTML = '';
				document.getElementById('locDivHOD').style.display = 'none';
				document.getElementById('locDivHODLbl').style.display = 'none';
				showCurrentOrgLoaction(1);
			} else if(val == 2) {
				document.getElementById('locDivCXO').innerHTML = '';
				document.getElementById('locDivCXO').style.display = 'none';
				document.getElementById('locDivCXOLbl').style.display = 'none';
				document.getElementById('locDivHOD').style.display = 'block';
				document.getElementById('locDivHODLbl').style.display = 'block';
				showCurrentOrgSelectedDepartment(1, 'HOD');
			} else {
				document.getElementById('locDivHOD').innerHTML = '';
				document.getElementById('locDivCXO').innerHTML = '';
				document.getElementById('locDivCXO').style.display = 'none';
				document.getElementById('locDivCXOLbl').style.display = 'none';
				document.getElementById('locDivHOD').style.display = 'none';
				document.getElementById('locDivHODLbl').style.display = 'none';
			}
		}
		
		/* CKEDITOR.on( 'instanceReady', function( ev ) {
			document.getElementById( 'eMessage' ).innerHTML = 'Instance <code>' + ev.editor.name + '<\/code> loaded.';
			document.getElementById( 'eButtons' ).style.display = 'block';
		});	
		function InsertHTML() {
			var editor = CKEDITOR.instances.editor1;
			var value = document.getElementById( 'htmlArea' ).value;
			if ( editor.mode == 'wysiwyg' ){
				editor.insertHtml( value );
			}else
				alert( 'You must be in WYSIWYG mode!' );
		}
		function InsertText() {
			var editor = CKEDITOR.instances.editor1;
			var value = document.getElementById( 'txtArea' ).value;	
			if ( editor.mode == 'wysiwyg' ){
				editor.insertText( value );
			}else
				alert( 'You must be in WYSIWYG mode!' );
		}
		function SetContents() {
			var editor = CKEDITOR.instances.editor1;
			var value = document.getElementById( 'htmlArea' ).value;
			editor.setData( value );
		}
		function GetContents() {
			var editor = CKEDITOR.instances.editor1;
			alert( editor.getData() );
		}
		function ExecuteCommand( commandName ) {
			var editor = CKEDITOR.instances.editor1;
			if ( editor.mode == 'wysiwyg' ){
				editor.execCommand( commandName );
			} else
				alert( 'You must be in WYSIWYG mode!' );
		}
		function CheckDirty() {
			var editor = CKEDITOR.instances.editor1;
			alert( editor.checkDirty() );
		}
		function ResetDirty() {
			var editor = CKEDITOR.instances.editor1;
			editor.resetDirty();
			alert( 'The "IsDirty" status has been reset' );
		}
		function Focus() {CKEDITOR.instances.editor1.focus();	}
		function onFocus() {document.getElementById( 'eMessage' ).innerHTML = '<b>' + this.name + ' is focused </b>';}
		function onBlur() {document.getElementById( 'eMessage' ).innerHTML = this.name + ' lost focus';	} */
		
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
		
		function checkFile(){
	        var fileValue = document.getElementById('text1File');
	        var fileName = fileValue.value;
	        var ext = fileName.substring(fileName.lastIndexOf('.') + 1);
	          // alert(fileName);
	        if(fileName == null || fileName == "")
	        	{
	        	    return true;
	        	}
	        else
	        	{
	        	 if(ext =="doc" || ext=="DOC" || ext=="DOCX" || ext=="docx" )
	        	    {
	        	        return true;
	        	    }
	        	    else
	        	    {
	        	        alert("Upload .doc or .docx file only");
	        	        return false;
	        	    }
	        	
	        	}
	   
	    if(flag){
	    	return true;
	    } else {
	    	return false;
	    }
	    
	}
		

		function getReportingByLocation() {
			var orgId = document.getElementById("strOrg").value;
			getHRList(orgId);
			getSupervisorList();
			
		}
		

		function getEnableTds(obj) {
			//alert("checkInfo value==>"+obj.checked);
				if(obj.checked==true) {
					document.getElementById('prevTdsDiv').style.display='block';
				}else{
					document.getElementById('prevTdsDiv').style.display='none';
				}
			  
		}
		
		function checkInfo(obj) {
			//alert("checkInfo value==>"+obj.checked);
			if(obj) {
				if(obj.checked){
					document.getElementById('tr_kmc_knc').style.display = 'table-row';
					document.getElementById('isMedicalCheck').value = "1";
				}else {
					document.getElementById('isMedicalCheck').value = "0";
					document.getElementById('tr_kmc_knc').style.display = 'none';
					if(document.getElementById('renewalDate_tr')) {
						document.getElementById('renewalDate_tr').style.display = 'none';
					}
					
					if(document.getElementById('kmcNo_tr')){
						document.getElementById('kmcNo_tr').style.display = 'none';
					}
					
					if(document.getElementById('kncNo_tr')){
						document.getElementById('kncNo_tr').style.display = 'none';
					}
				}
			}
		}
	      
	      function checkKmcKnc(val) {
	  		//	alert("checkKmcKnc value==>"+val);
	  			if(val == 1) {
	  				document.getElementById('kmcNo_tr').style.display = 'table-row';
	  				document.getElementById('renewalDate_tr').style.display = 'table-row';
	  				if(document.getElementById('kncNo_tr')){
	  					document.getElementById('kncNo_tr').style.display = 'none';
	  				}
	  				
	  				if(document.getElementById('strKncNo')) {
	  					document.getElementById('strKncNo').value = "";
	  				}
	  				
	  				
	  				
	  			} else if(val == 2) {
	  				document.getElementById('kncNo_tr').style.display = 'table-row';
	  				document.getElementById('renewalDate_tr').style.display = 'table-row';
	  				if(document.getElementById('kmcNo_tr')) {
	  					document.getElementById('kmcNo_tr').style.display = 'none';
	  				}
	  				
	  				if(document.getElementById('strKmcNo')) {
	  					document.getElementById('strKmcNo').value = "";
	  				}
	  				
	  				
	  				
	  			} else {
	  				if(document.getElementById('kmcNo_tr')) {
	  					document.getElementById('kmcNo_tr').style.display = 'none';
	  				}
	  				
	  				if(document.getElementById('renewalDate_tr')){
	  					document.getElementById('renewalDate_tr').style.display = 'none';
	  				}
	  				
	  				if(document.getElementById('kncNo_tr')) {
	  					document.getElementById('kncNo_tr').style.display = 'none';
	  				}
	  				
	  				if(document.getElementById('strKmcNo')) {
	  					document.getElementById('strKmcNo').value = "";
	  				}
	  				
	  				if(document.getElementById('strKncNo')) {
	  					document.getElementById('strKncNo').value = "";
	  				}
	  				
	  				if(document.getElementById('strRenewalDate')) {
	  					document.getElementById('strRenewalDate').value = "";
	  				}
	  			}
	  		}

</script>

<div class="modal" id="modalInfo" role="dialog">
    <div class="modal-dialog">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">-</h4>
            </div>
            <div class="modal-body" style="height:400px;overflow-y:auto;padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>

