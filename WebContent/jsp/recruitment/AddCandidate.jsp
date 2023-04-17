<%@page import="java.util.LinkedHashMap"%>
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
<%@page import="com.konnect.jpms.select.FillSkills"%>
<%@page import="com.konnect.jpms.select.FillYears"%>
<%@page import="com.konnect.jpms.util.*" %>
<%@page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>
<%@ taglib prefix="s" uri="/struts-tags" %> 
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<% String operation = (String)request.getAttribute("operation");%>

<style> 
    <g:compress>
        
        .textfield_height{
        }
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
        #div_hobbies {
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
        .wizard {
        margin: 30px auto;
        background: #fff;
        }
        .wizard .nav-tabs {
        position: relative;
        /* margin: 40px auto; */
        margin-bottom: 0;
        border-bottom-color: #e0e0e0;
        }
        .wizard > div.wizard-inner {
        position: relative;
        }
        .connecting-line {
        height: 2px;
        background: #e0e0e0;
        position: absolute;
        width: 71%;
        /* margin: 0 auto; */
        margin-left: 40px;
        left: 0;
        right: 0;
        top: 52%;
        z-index: 1;
        }
        .wizard .nav-tabs > li.active > a, .wizard .nav-tabs > li.active > a:hover, .wizard .nav-tabs > li.active > a:focus {
        color: #555555;
        cursor: default;
        border: 0;
        border-bottom-color: transparent;
        }
        span.round-tab {
        width: 50px;
        height: 50px;
        line-height: 50px;
        display: inline-block;
        border-radius: 100px;
        background: #fff;
        border: 2px solid #e0e0e0;
        z-index: 2;
        position: absolute;
        left: 0;
        text-align: center;
        font-size: 20px;
        }
        span.round-tab i{
        color:#555555;
        }
        .wizard li.active span.round-tab {
        background: #fff;
        border: 2px solid #5bc0de;
        }
        .wizard li.active span.round-tab i{
        color: #5bc0de;
        }
        span.round-tab:hover {
        color: #333;
        border: 2px solid #333;
        }
        .wizard .nav-tabs > li {
        width: 10%;
        }
        .wizard li:after {
        content: " ";
        position: absolute;
        left: 46%;
        opacity: 0;
        margin: 0 auto;
        bottom: 0px;
        border: 5px solid transparent;
        border-bottom-color: #5bc0de;
        transition: 0.1s ease-in-out;
        }
        .wizard li.active:after {
        content: " ";
        position: absolute;
        left: 42%;
        opacity: 1;
        margin: 0 auto;
        bottom: 0px;
        border: 10px solid transparent;
        border-bottom-color: #5bc0de;
        }
        .wizard .nav-tabs > li a {
        width: 50px;
        height: 50px;
        margin: 20px auto;
        border-radius: 100%;
        padding: 0;
        }
        .wizard .nav-tabs > li a:hover {
        background: transparent;
        }
        .wizard .tab-pane {
        position: relative;
        padding-top: 50px;
        }
        .wizard h3 {
        margin-top: 0;
        }
        @media( max-width : 585px ) {
        .wizard {
        width: 90%;
        height: auto !important;
        }
        span.round-tab {
        font-size: 16px;
        width: 50px;
        height: 50px;
        line-height: 50px;
        }
        .wizard .nav-tabs > li a {
        width: 50px;
        height: 50px;
        line-height: 50px;
        }
        .wizard li.active:after {
        content: " ";
        position: absolute;
        left: 35%;
        }
        }
        .wizard {
        margin: 0px auto;
        }
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
    ArrayList alSiblings = (ArrayList) request.getAttribute("alSiblings");
    ArrayList alPrevEmployment = (ArrayList) request.getAttribute("alPrevEmployment");
    List degreeDurationList = (List) request.getAttribute("degreeDurationList");
    List yearsList = (List) request.getAttribute("yearsList");
    List empGenderList = (List) request.getAttribute("empGenderList");
    List skillsList = (List) request.getAttribute("skillsList");
    String strImage = (String) request.getAttribute("strImage");
    
    HashMap empServicesMap = (HashMap) request.getAttribute("empServicesMap");
    
    UtilityFunctions uF = new UtilityFunctions();
    String currentYear = (String)request.getAttribute("currentYear");
    
    CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
    int nEmpAlphaCodeLength = 2;
    if(CF!=null && CF.getStrOEmpCodeAlpha()!=null){
    	nEmpAlphaCodeLength = CF.getStrOEmpCodeAlpha().length();
    }
    
    String strUserType=(String)session.getAttribute(IConstants.USERTYPE);
    String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
    
	int docCount = 0;
	if(alDocuments != null && alDocuments.size()>0 ) {
		docCount = alDocuments.size();
	}
	
	Map<String, List<String>> hmEducationDocs = (Map<String, List<String>>)request.getAttribute("hmEducationDocs");
	//===start parvez date: 08-08-2022===	
	Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(request);
	//===end parvez date: 08-08-2022===
%>

<script>

var documentcnt = '<%=docCount%>';

/* ===start parvez date: 09-08-2022=== */
    $("form").bind('submit',function(event) {
    	 $("input[type='submit']").val('Submitting..');
    	 var frmPage = document.getElementById('fromPage').value;
   <% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_DISABLE_AVAILABILITY_OF_INTERVIEW))){ %> 
		   
   		if(this.id !== "frmAvailibility" && (frmPage == 'CR' || frmPage == 'CAPP')){
		 	  event.preventDefault();
		 	  $("input[type='submit']").val('Submitting..');
		 	  
		 	  var docFrmId = this.id;
		 	  var recruitId = document.getElementById("recruitId").value;
		 	  if($("#empImage").length !== 0 && $("#profilecontainerimg").attr('src') !== "userImages/avatar_photo.png"){
		 		  var form_data = new FormData($(this)[0]);
		 		  form_data.append("empImage", $("#profilecontainerimg").attr('src'));
		 		  $.ajax({
			      		url: "AddCandidate.action",
			      		type: 'POST',
			      		data: form_data,
			      		contentType: false,
			            cache: false,
			      		processData: false,
			      		success: function(result){
			      			console.log("1--->"+docFrmId);
			      			/* $(".modal-body").html(result); */
			      			if(docFrmId === "frmDocumentation"){
			      				if(frmPage == 'CAPP'){
			      					$("#subSubDivResult").html(result);
			      				}
			      			}else{
			      				$(".modal-body").html(result);
			      			}
			      	    }
			      	 });
		  	  }else if($("#file1").length !== 0 && $("#file2").length !== 0 && $("#file3").length !== 0){
		  		  var form_data = new FormData($(this)[0]);
		  		  if($("#file1").attr('path') !== undefined){
		 		    form_data.append("que1DescFile", $("#file1").attr('path'));
		  		  }
		  		  if($("#file2").attr('path') !== undefined){
		 		    form_data.append("que1DescFile", $("#file2").attr('path'));
		  		  }
		  		  if($("#file3").attr('path') !== undefined){
		  			  
		 		    form_data.append("que1DescFile", $("#file3").attr('path'));
		  		  }
		 		  $.ajax({
			      		url: "AddCandidate.action",
			      		type: 'POST',
			      		data: form_data,
			      		contentType: false,
			            cache: false,
			      		processData: false,
			      		success: function(result){
			      			console.log("2--->"+docFrmId);
			      			/* $(".modal-body").html(result); */
			      			if(docFrmId === "frmDocumentation"){
			      				if(frmPage == 'CAPP'){
			      					$("#subSubDivResult").html(result);
			      				}
			      			} else{
			      				$(".modal-body").html(result);
			      			}
			      	    }
			      	 });
		  	  }else if($("#doc1").length !== 0 && $("#doc2").length !== 0 && $("#doc3").length !== 0){
		  		  var form_data = new FormData($(this)[0]);
		  		  if($("#doc1").attr('path') !== undefined){
		 		    form_data.append("idDoc", $("#doc1").attr('path'));
		  		  }
		  		  if($("#doc2").attr('path') !== undefined){
		 		    form_data.append("idDoc", $("#doc2").attr('path'));
		  		  }
		  		  if($("#doc3").attr('path') !== undefined){
		 		    form_data.append("idDoc", $("#doc3").attr('path'));
		  		  }
		 		  $.ajax({
			      		url: "AddCandidate.action",
			      		type: 'POST',
			      		data: form_data,
			      		contentType: false,
			            cache: false,
			      		processData: false,
			      		success: function(result){
			      			console.log("3--->"+docFrmId);
			      			if(docFrmId === "frmDocumentation"){
			      				if(frmPage == 'CAPP'){
			      					$("#subSubDivResult").html(result);
			      				}
			      			}else {
			      				$(".modal-body").html(result);
			      			}
			      			
			      	    }
			      	 });
		 		  //===start parvez date: 13-09-2021=== 
		  	   }else if($("#expfile0").length !== 0){
		  		  //console.log("expfile--step4");
		  		 
		  		  var form_data = new FormData($(this)[0]);
		  		  if($("#expfile0").attr('path') !== undefined){
		 		    form_data.append("experienceLetter0", $("#expfile0").attr('path'));
		  		  }
		 		  $.ajax({
			      		url: "AddCandidate.action",
			      		type: 'POST',
			      		data: form_data,
			      		contentType: false,
			            cache: false,
			      		processData: false,
			      		success: function(result){
			      			console.log("4--->"+docFrmId);
			      			/* $(".modal-body").html(result); */
			      			if(docFrmId === "frmDocumentation"){
			      				if(frmPage == 'CAPP'){
			      					$("#subSubDivResult").html(result);
			      				}
			      			} else {
			      				$(".modal-body").html(result);
			      			}
			      	    }
			      	 });
		  	  }else if($("#edufile0").length !== 0){
		  		 var form_data = new FormData($(this)[0]);
		  		if($("#edufile0").attr('path') !== undefined){
		 		    form_data.append("degreeCertificate0", $("#edufile0").attr('path'));
		  		}
		  		$.ajax({
			      		url: "AddCandidate.action",
			      		type: 'POST',
			      		data: form_data,
			      		contentType: false,
			            cache: false,
			      		processData: false,
			      		success: function(result){
			      			console.log("5--->"+docFrmId);
			      			/* $(".modal-body").html(result); */
			      			if(docFrmId === "frmDocumentation"){
			      				if(frmPage == 'CAPP'){
			      					$("#subSubDivResult").html(result);
			      				}
			      			} else{
			      				$(".modal-body").html(result);
			      			}
			      	    }
			      	 });
		  	  }else {
		  		  //===end parvez date: 13-09-2021=== 
		  			  //alert("else");
		  		 $.ajax({
			      		url: "AddCandidate.action",
			      		type: 'POST',
			      		data: $("#"+this.id).serialize(),
			      		success: function(result){
			      			console.log("6--->"+docFrmId);
			      			/* $(".modal-body").html(result); */
			      			if(docFrmId === "frmDocumentation"){
			      				if(frmPage == 'CAPP'){
			      					$("#subSubDivResult").html(result);
			      				}
			      			} else{
			      				$(".modal-body").html(result);
			      			}
			      	    }
			      	 });
		  	  }
		 	  
		   }
   <% } else { %>
	      if(this.id !== "frmAvailibility" && (frmPage == 'CR' || frmPage == 'CAPP')){
	    	  event.preventDefault();
	    	  $("input[type='submit']").val('Submitting..');
	    	  
	    	  //===start parvez date: 09-09-2021=== 
	    	  //var expStatusVal = parseInt(document.getElementById('expLetterStatus0').value);
	    	 // console.log("expStatusVal="+expStatusVal);
	    	  //===end parvez date: 09-09-2021=== 
	    	  
	    	  if($("#empImage").length !== 0 && $("#profilecontainerimg").attr('src') !== "userImages/avatar_photo.png"){
	    		  var form_data = new FormData($(this)[0]);
	    		  form_data.append("empImage", $("#profilecontainerimg").attr('src'));
	    		  $.ajax({
	  	      		url: "AddCandidate.action",
	  	      		type: 'POST',
	  	      		data: form_data,
	  	      		contentType: false,
	  	            cache: false,
	  	      		processData: false,
	  	      		success: function(result){
	  	      			$(".modal-body").html(result);
	  	      	    }
	  	      	 });
	     	  }else if($("#file1").length !== 0 && $("#file2").length !== 0 && $("#file3").length !== 0){
	     		  var form_data = new FormData($(this)[0]);
	     		  if($("#file1").attr('path') !== undefined){
	    		    form_data.append("que1DescFile", $("#file1").attr('path'));
	     		  }
	     		  if($("#file2").attr('path') !== undefined){
	    		    form_data.append("que1DescFile", $("#file2").attr('path'));
	     		  }
	     		  if($("#file3").attr('path') !== undefined){
	    		    form_data.append("que1DescFile", $("#file3").attr('path'));
	     		  }
	    		  $.ajax({
	  	      		url: "AddCandidate.action",
	  	      		type: 'POST',
	  	      		data: form_data,
	  	      		contentType: false,
	  	            cache: false,
	  	      		processData: false,
	  	      		success: function(result){
	  	      			$(".modal-body").html(result);
	  	      	    }
	  	      	 });
	     	  }else if($("#doc1").length !== 0 && $("#doc2").length !== 0 && $("#doc3").length !== 0){
	     		  var form_data = new FormData($(this)[0]);
	     		  if($("#doc1").attr('path') !== undefined){
	    		    form_data.append("idDoc", $("#doc1").attr('path'));
	     		  }
	     		  if($("#doc2").attr('path') !== undefined){
	    		    form_data.append("idDoc", $("#doc2").attr('path'));
	     		  }
	     		  if($("#doc3").attr('path') !== undefined){
	    		    form_data.append("idDoc", $("#doc3").attr('path'));
	     		  }
	    		  $.ajax({
	  	      		url: "AddCandidate.action",
	  	      		type: 'POST',
	  	      		data: form_data,
	  	      		contentType: false,
	  	            cache: false,
	  	      		processData: false,
	  	      		success: function(result){
	  	      			$(".modal-body").html(result);
	  	      	    }
	  	      	 });
	    		  //===start parvez date: 13-09-2021=== 
	     	   }else if($("#expfile0").length !== 0){
	     		  //console.log("expfile--step4");
	     		 
	     		  var form_data = new FormData($(this)[0]);
	     		  if($("#expfile0").attr('path') !== undefined){
	    		    form_data.append("experienceLetter0", $("#expfile0").attr('path'));
	     		  }
	    		  $.ajax({
	  	      		url: "AddCandidate.action",
	  	      		type: 'POST',
	  	      		data: form_data,
	  	      		contentType: false,
	  	            cache: false,
	  	      		processData: false,
	  	      		success: function(result){
	  	      			$(".modal-body").html(result);
	  	      	    }
	  	      	 });
	     	  }else if($("#edufile0").length !== 0){
	     		 var form_data = new FormData($(this)[0]);
	     		if($("#edufile0").attr('path') !== undefined){
	    		    form_data.append("degreeCertificate0", $("#edufile0").attr('path'));
	     		}
	     		$.ajax({
	  	      		url: "AddCandidate.action",
	  	      		type: 'POST',
	  	      		data: form_data,
	  	      		contentType: false,
	  	            cache: false,
	  	      		processData: false,
	  	      		success: function(result){
	  	      			$(".modal-body").html(result);
	  	      	    }
	  	      	 });
	     	  }else {
	     		  //===end parvez date: 13-09-2021=== 
	     			  //alert("else");
	     		 $.ajax({
	 	      		url: "AddCandidate.action",
	 	      		type: 'POST',
	 	      		data: $("#"+this.id).serialize(),
	 	      		success: function(result){
	 	      			$(".modal-body").html(result);
	 	      	    }
	 	      	 });
	     	  }
	    	  
	      }
	<% } %> 
    });
/* ===end parvez date: 08-09-2022=== */    
    
    function loadStepOnClick(action){
    	$(".modal-body").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$.ajax({
      		url: action,
      		type: 'GET',
      		success: function(result){
      			$(".modal-body").html(result);
      	    }
      	 });
    }
    
    function callPrevious(step,candiId,fromPage){
         $.ajax({
       		url: "AddCandidate.action?mode=editprofile&step="+step+"&operation=EP&CandidateId="+candiId+"&fromPage="+fromPage,
       		type: 'GET',
       		success: function(result){
       			$(".modal-body").html(result);
       	    }
       	});
    }
    
</script>


<script>
    $(function(){
    	$("input[name='stepSubmit']").click(function(){
    		$("#"+ this.form.id ).find('.validateRequired').filter(':hidden').prop('required',false);
    		$("#"+ this.form.id ).find('.validateRequired').filter(':visible').prop('required',true);
    		$("#"+ this.form.id ).find('.validateEmail').filter(':visible').attr('type','email');
    	});
    	$("input[name='strDate'").datepicker({format: 'dd/mm/yyyy'});
        $("input[name='strTime'").datetimepicker({format: 'HH:mm'});
     // Start Dattatray Date : 13-July-2021 
        $("input[name='memberDob']").datepicker({format: 'dd/mm/yyyy',endDate:'+0d'});// Created by Dattatray Date : 13-July-2021 Note : Added endDate
        $( "#empDateOfBirth" ).datepicker({format: 'dd/mm/yyyy',endDate:'-18y'});//Created by Dattatray Date : 13-July-2021
        var dboMaxDate = '<%=(String)request.getAttribute("strDOBMaxDate")%>';
        $( "#empDateOfBirth" ).datepicker({format: 'dd/mm/yyyy', 
	    	changeYear: true,
		    endDate : new Date(dboMaxDate)
	    }).on('changeDate', function (selected) {
		    var minDate = new Date(selected.date.valueOf());
		    $('#empDateOfMarriage').datepicker('setStartDate', minDate);
		    $('#empDateOfMarriage').datepicker('setEndDate', '+0d');// Created by Dattatray Date : 13-July-2021 Note : endDate changed
		});
        $( "#empDateOfMarriage" ).datepicker({format: 'dd/mm/yyyy',endDate:'+0d'});// Created by Dattatray Date : 13-July-2021 Note : added endDate and removed YearRange and currentYear
        // End Dattatray Date : 13-July-2021
        $("#empEmail").prop('type','email');
        $("#fatherEmailId").prop('type','email'); 
        $("#motherEmailId").prop('type','email');
        $("#spouseEmailId").prop('type','email');
        $("#memberEmailId").prop('type','email');
        $("#ref1Email").prop('type','email');
        $("#ref2Email").prop('type','email');
        
        $("input[name=prevCompanyFromDate]").datepicker({
            format: 'dd/mm/yyyy',
            autoclose: true
        }).on('changeDate', function (selected) {
            var minDate = new Date(selected.date.valueOf());
            $('input[name=prevCompanyToDate]').datepicker('setStartDate', minDate);
        });
        
        $("input[name=prevCompanyToDate]").datepicker({
        	format: 'dd/mm/yyyy',
        	autoclose: true
        }).on('changeDate', function (selected) {
                var minDate = new Date(selected.date.valueOf());
                $('input[name=prevCompanyFromDate]').datepicker('setEndDate', minDate);
        });
       <%-- // $("input[name=prevCompanyFromDate]").datepicker({format: 'dd/mm/yyyy', yearRange: '1950:<%=currentYear%>', changeYear: true});
        //$("input[name=prevCompanyToDate]").datepicker({format: 'dd/mm/yyyy', yearRange: '1950:<%=currentYear%>', changeYear: true}); --%>
        
        //Start Dattatray Date : 13-July-2021 Note : added endDate and removed YearRange and currentYear
        $("input[name=fatherDob]").datepicker({format: 'dd/mm/yyyy',endDate: '+0d'});
        $("input[name=motherDob]").datepicker({format: 'dd/mm/yyyy',endDate: '+0d'});
        $("input[name=spouseDob]").datepicker({format: 'dd/mm/yyyy',endDate: '+0d'});
        $("input[name=memberDob]").datepicker({format: 'dd/mm/yyyy',endDate: '+0d'});
      	//End Dattatray Date : 13-July-2021 Note : added endDate and removed YearRange and currentYear
        $("input[name=empPassportExpiryDate]").datepicker({format: 'dd/mm/yyyy', yearRange: '<%=currentYear%>:2020', changeYear: true});
        
      	//alert("strCompletionDate");
		/* Start Dattatray Date:23-08-21 */	    
        $("input[name=strStartDate]").datepicker({format: 'dd/mm/yyyy'});
        $("input[name=strCompletionDate]").datepicker({format: 'dd/mm/yyyy'}); 
        /* $("#strStartDate").datepicker({format: 'dd/mm/yyyy'});
        $("#strCompletionDate").datepicker({format: 'dd/mm/yyyy'}); */
        /* End Dattatray Date:23-08-21 */
    });
</script>

<%-- <script>
$(function(){
	//var pName = $("#prevCompanyName").val();
	if(document.getElementById('prevCompanyName').value === ''){
		
	alert("experienceLetter0");
		document.getElementById("experienceLetter0").className = 'validateRequired';
	}
});
</script> --%>
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
        
        function showState() {	
        	dojo.event.topic.publish("showState");
        }
        
        
       function callDatePicker() {
            //alert("callDatePicker ");
               $( "#empDateOfBirth" ).datepicker({format: 'dd/mm/yyyy', yearRange: '1950:2000', changeYear: true});
               $( "#empDateOfMarriage" ).datepicker({format: 'dd/mm/yyyy', yearRange: '1970:<%=currentYear%>', changeYear: true});
               
               $("input[name=prevCompanyFromDate]").datepicker({format: 'dd/mm/yyyy', yearRange: '1950:<%=currentYear%>', changeYear: true});
               $("input[name=prevCompanyToDate]").datepicker({format: 'dd/mm/yyyy', yearRange: '1950:<%=currentYear%>', changeYear: true});
               
             //Start Dattatray Date : 13-July-2021 Note : added endDate and removed YearRange and currentYear
               $("input[name=fatherDob]").datepicker({format: 'dd/mm/yyyy',endDate:'+0d'});
               $("input[name=motherDob]").datepicker({format: 'dd/mm/yyyy',endDate:'+0d'});
               $("input[name=spouseDob]").datepicker({format: 'dd/mm/yyyy',endDate:'+0d'});
               $("input[name=memberDob]").datepicker({format: 'dd/mm/yyyy',endDate:'+0d'});
             //End Dattatray Date : 13-July-2021 Note : added endDate and removed YearRange and currentYear
               $("input[name=empPassportExpiryDate]").datepicker({format: 'dd/mm/yyyy', yearRange: '<%=currentYear%>:2020', changeYear: true});
            //===start parvez date: 18-09-2021=== 
               $("input[name=strStartDate]").datepicker({format: 'dd/mm/yyyy'});
        $("input[name=strCompletionDate]").datepicker({format: 'dd/mm/yyyy'});
           //===end parvez date: 18-09-2021=== 
       }
        
        
        
        function fillFileStatus(ids){
        	//alert(ids);
        	document.getElementById(ids).value=1;
        }
        
        function showMarriageDate(){
        	if(document.frmPersonalInfo.empMaritalStatus.options[document.frmPersonalInfo.empMaritalStatus.options.selectedIndex].value=='M'){
        		document.getElementById("trMarriageDate").style.display = 'table-row';
        	}else{
        		document.getElementById("trMarriageDate").style.display = 'none';
        	}
        }
        
        <% if (alSkills!=null) {%>
        	var cnt=<%=alSkills.size()%>;
        <%}else{%>
        	var cnt =0;
        <%}%>
        
        function addSkills() {
        	
        	cnt++;
            var trTag = document.createElement("tr");
    	    trTag.id = "row_skill"+cnt;
    	    trTag.setAttribute("class", "row_skill");
    	    trTag.innerHTML = 	"<%=request.getAttribute("sbSkills")%>" +
    	    			    	"<td><a href=\"javascript:void(0)\" onclick=\"addSkills()\" class=\"add-font\"></a><a href=\"javascript:void(0)\" onclick=\"removeSkills(this.id)\" id=\""+cnt+"\" class=\"remove-font\"></a></td>"; 
    	    document.getElementById("table-skills").appendChild(trTag);
            
        }
        
        function removeSkills(removeId) {
        	
        	var remove_elem = "row_skill"+removeId;
        	var row_skill = document.getElementById(remove_elem); 
        	document.getElementById("table-skills").removeChild(row_skill);
        	
        }
        
        <% if (alHobbies!=null) {%>
        var cnt=<%=alHobbies.size()%>;
        <%}else{%>
        var cnt =0;
        <%}%>
        
        function addHobbies() {
        	
        	cnt++;
            var trTag = document.createElement("tr");
    	    trTag.id = "row_hobby"+cnt;
    	    trTag.setAttribute("class", "row_hobby");
    		trTag.innerHTML = 	"<td><input type=\"text\" style=\"; \" name=\"hobbyName\"></input></td>" +   			    	
    	    			    	"<td><a href=\"javascript:void(0)\" onclick=\"addHobbies()\" class=\"add-font\"></a>" +
    	 						"<a href=\"javascript:void(0)\" onclick=\"removeHobbies(this.id)\" id=\""+cnt+"\" class=\"remove-font\"></a></td></tr>";
    		                    
    	    document.getElementById("table-hobbies").appendChild(trTag);
            
        }
        
        function removeHobbies(removeId) {
        	
        	var remove_elem = "row_hobby"+removeId;
        	var row_skill = document.getElementById(remove_elem); 
        	document.getElementById("table-hobbies").removeChild(row_skill);
        	
        }
        
        function getState(country){
        	var action= 'GetStateDetails.action?type=candidate&country_id=' + country;
        	getContent('statetdid', action); 
        }
        
        function getState1(country){
        	var action= 'GetStateDetails.action?type=candidate1&country_id=' + country;
        	getContent('statetdid1', action); 
        }
        
        <% if (alLanguages!=null) {%>
        
        var cnt=<%=alLanguages.size()%>;
        
        <%}else{%>
        
        var cnt =0;
        
        <%}%>
        
        function addLanguages() {
        	
        	cnt++;
           
            var trTag = document.createElement("tr");
    	    trTag.id = "row_language"+cnt;
    	    trTag.setAttribute("class", "row_language");
    		trTag.innerHTML = 	"<td><input type=\"text\" name=\"languageName\" ></input></td>" + 
    	 						"<td align=\"center\"><input type=\"checkbox\" name=\"isReadcheckbox\" value=\"1\"  id=\"isRead_"+cnt+"\" onchange=\"showHideHiddenField(this.id)\" /></td>" +
    							"<input type=\"hidden\" value=\"0\" name=\"isRead\" id=\"hidden_isRead_"+cnt+"\" />" +
    							"<td  align=\"center\"><input type=\"checkbox\" name=\"isWritecheckbox\" value=\"1\" id=\"isWrite_"+cnt+"\" onchange=\"showHideHiddenField(this.id)\" /></td>"+
    							"<input type=\"hidden\" value=\"0\" name=\"isWrite\" id=\"hidden_isWrite_"+cnt+"\" />" +
    							"<td  align=\"center\"><input type=\"checkbox\" name=\"isSpeakcheckbox\" value=\"1\" id=\"isSpeak_"+cnt+"\" onchange=\"showHideHiddenField(this.id)\" /></td>"+
    							"<input type=\"hidden\" value=\"0\" name=\"isSpeak\" id=\"hidden_isSpeak_"+cnt+"\" />" +
    							"<td  align=\"center\"><a href=\"javascript:void(0)\" onclick=\"addLanguages()\" class=\"add-font\"></a><a href=\"javascript:void(0)\" onclick=\"removeLanguages(this.id)\" id=\""+cnt+"\" class=\"remove-font\" ></a></td>"; 

    	    document.getElementById("table-language").appendChild(trTag);
            
        }
        
        function removeLanguages(removeId) {
        	
        	var remove_elem = "row_language"+removeId;
        	var row_skill = document.getElementById(remove_elem); 
        	document.getElementById("table-language").removeChild(row_skill);
        	
        }
        
        /* ===start parvez date: 17-09-2021=== */
        <%-- <% if (alEducation!=null) {
        System.out.println("alEducation"+alEducation.size());%>
        	var cnt=<%=alEducation.size()-1%>;
        	
        <%}else{%>
        	var cnt =0;
        <%}%> --%>
       
        /* ===end parvez date: 17-09-2021=== */
        function addEducation() {
        	/* 
        	cnt++; */
           
            var cnt = document.getElementById("educationCnt").value;
           cnt = parseInt(cnt)+1; 
            var trTag = document.createElement("tr");
    		trTag.id = "row_education"+cnt;
    		trTag.setAttribute("class", "row_education");
    		trTag.setAttribute("style", "width: 90px !important;");
    		<%-- trTag.innerHTML = "<td><select name=\"degreeName\" onchange=\"checkEducation(this.value,"+cnt+")\"> "+
    						 "<%=request.getAttribute("sbdegreeDuration")%>" +
    				"<a href=\"javascript:void(0)\" onclick=\"removeEducation(this.id)\" id=\""+cnt+"\" class=\"remove-font\" ></a></td></tr>";
    	 			; --%> 
    	 	/* ===start parvez date:13-09-2021=== */
    	 	trTag.innerHTML = "<td><select name=\"degreeName\" style=\"width:120px!important;\" onchange=\"checkEducation(this.value,"+cnt+")\"> "+
					 "<%=request.getAttribute("sbdegreeDuration")%>" 
						+"<td><input type=\"hidden\" name=\"degreeCertiStatus\" id=\"degreeCertiStatus"+cnt+"\" value=\"0\">"
						+"<div id=\"degreeCertiDiv"+cnt+"\"><input type=\"hidden\" name=\"degreeCertiSubDivCnt"+cnt+"\" id=\"degreeCertiSubDivCnt"+cnt+"\" value=\"0\" />"
						+"<div id=\"degreeCertiSubDiv"+cnt+"_0\"><div id=\"edufile0\"></div>"
						+"<input type=\"file\" accept=\".jpeg,.jpg,.png,.tif,.svg,.svgz,.doc,.docs,.docx,.pdf\" name=\"degreeCertificate"+cnt+"\" id=\"degreeCertificate"+cnt+"\" onchange=\"fillFileStatus1('degreeCertiStatus"+cnt+"', 'degreeCertificate"+cnt+"');\" />"
						+"<a href=\"javascript:void(0)\" onclick=\"addEducationCerti('"+cnt+"')\" class=\"add-font\"></a></div></div></td>"
						+"<td><a href=\"javascript:void(0)\" onclick=\"addEducation()\" class=\"add-font\" ></a>"
						+"<a href=\"javascript:void(0)\" onclick=\"removeEducation(this.id)\" id=\""+cnt+"\" class=\"remove-font\" ></a></td></tr>";
			/* ===end parvez date: 13-09-2021=== */
    		var trTag1 = document.createElement("tr");
    		trTag1.id = "degreeNameOtherTR"+cnt;
    		trTag1.setAttribute("class", "hide-tr");	
    		trTag1.innerHTML = "<td colspan=\"1\" style=\"text-align: right;\">Enter Academics Degree :</td><td colspan=\"5\"> " +
    			"<input  type=\"text\" name=\"degreeNameOther\"></td>"; 
    		
    		
    	//	console.log(trTag);
    	    document.getElementById("table-education").appendChild(trTag);
    	    document.getElementById("table-education").appendChild(trTag1);
    	    //===start parvez date: 18-09-2021=== 
    	    callDatePicker();
    	  //===end parvez date: 18-09-2021=== 
        }
        
        
        function checkEducation(value,count){
        	//alert("count="+count);
        	if(value=="other"){
        		document.getElementById("degreeNameOtherTR"+count).style.display="table-row";
        	} else {
        		document.getElementById("degreeNameOtherTR"+count).style.display="none";
        	}
        	//===start parvez date: 14-09-2021=== 
        	if(value.length !== 0 && value !== ""){
        		document.getElementById("degreeCertificate"+count).className = 'validateRequired';
        	}else{
        		$(".validateRequired").prop('required',false);
    			document.getElementById("degreeCertificate"+count).classList.remove("validateRequired");
        	}
        	
        	//===end parvez date: 14-09-2021=== 
        }
        
        
        function removeEducation(removeId) {
        	
        	var remove_elem = "row_education"+removeId;
        	var row_skill = document.getElementById(remove_elem); 
        	document.getElementById("table-education").removeChild(row_skill);
        	
        }
        
        var siblingcnt = 0;
        
        function addSibling() {
        	
        	siblingcnt++;
        	
        	var divTag = document.createElement("div");
        	divTag.setAttribute("style","width: auto");
        	divTag.id = "col_family_siblings"+siblingcnt;
            
            divTag.innerHTML = 	"<%=request.getAttribute("sbSibling")%>" +
            		"<tr><td colspan=\"2\" ><span style=\"float: right;\">"
            		+"<a href=\"javascript:void(0)\" onclick=\"removeSibling(this.id)\" id=\""+siblingcnt+"\" class=\"remove-font\" ></a>"
            		+"<a href=javascript:void(0) onclick=addSibling() class=add-font></a></span></td></tr>" +
                    "</table>"; 
        
        	document.getElementById("div_id_family").appendChild(divTag);
        	callDatePicker();
        	
        }
        
        function removeSibling(removeId) {
        	
        	var remove_elem = "col_family_siblings"+removeId;
        	var row_skill = document.getElementById(remove_elem); 
        	document.getElementById("div_id_family").removeChild(row_skill);
        	
        }
        
        
        <% if (alDocuments!=null) {%>
        
        var cnt=<%=alDocuments.size()%>;
        
        <%}else{%>
        
        var cnt = 0;
        
        <%}%>
        
        function fillFileStatus(ids){
        	//alert("ids/712=="+ids);
    		document.getElementById(ids).value=1;
    		 // Start Dattatray Date:30-June-2021
    		 var allowedFileExtensions = /(\.doc|\.docs|\.docx|\.pdf)$/i;
    		 var allowedIdProofExtensions = /(\.jpeg|\.jpg|\.png|\.svg)$/i;
    		 var fileErrorMsg = 'Please upload pdf,docx,docs or doc only';
    		 var idProofErrorMsg = 'Please upload jpeg,jpg,png or svg only';
    		 
    		 //===start parvez date: 20-09-2021=== 
    		 var allowedDocExtensions = /(\.jpeg|\.jpg|\.png|\.tif|\.svg|\.svgz|\.doc|\.docs|\.docx|\.pdf)$/i
    		 var DocErrorMsg = 'Please upload jpeg,jpg,png,tif,svg,svgz,pdf,docx,docs or doc only';
    		 //===end parvez date: 20-09-221=== 
    		 
    		if(ids == 'idDoc1Status'){
    			fileValidation('idDoc0',allowedFileExtensions,fileErrorMsg);
    		}else if(ids == 'idDoc2Status'){
    			fileValidation('idDoc1',allowedIdProofExtensions,idProofErrorMsg);
    		}else if(ids == 'idDoc3Status'){
    			fileValidation('idDoc2',allowedIdProofExtensions,idProofErrorMsg);
    		} // End Dattatray
    		
    		//===start parvez date: 17-09-2021=== 
    		if(ids.includes("expLetterStatus")){
    			var fileVal=0;
    			
    			fileVal=document.getElementById("fileCount").value;
    			fileVal++;
    			document.getElementById("fileCount").value=fileVal;
    			//console.log(fileVal);
    		}
    		//===end parvez date: 17-09-2021=== 
    			
    		if(ids.includes("expLetterStatus") || ids.includes("degreeCertiStatus")){
    			fileValidation('idDoc2',allowedIdProofExtensions,idProofErrorMsg);
    		}
    	}
        
        function fillFileStatus1(ids,fileIds){
        	//alert("ids/712=="+fileIds);
    		document.getElementById(ids).value=1;
    		 
    		 //===start parvez date: 20-09-2021=== 
    		 var allowedDocExtensions = /(\.jpeg|\.jpg|\.png|\.tif|\.svg|\.svgz|\.doc|\.docs|\.docx|\.pdf)$/i
    		 var docErrorMsg = 'Please upload jpeg,jpg,png,tif,svg,svgz,pdf,docx,docs or doc only';
    		 //===end parvez date: 20-09-221=== 
    		 
    		//===start parvez date: 17-09-2021=== 
    		if(ids.includes("expLetterStatus")){
    			var fileVal=0;
    			
    			fileVal=document.getElementById("fileCount").value;
    			fileVal++;
    			document.getElementById("fileCount").value=fileVal;
    			//console.log(fileVal);
    		}
    		//===end parvez date: 17-09-2021=== 
    			
    		fileValidation(fileIds,allowedDocExtensions,docErrorMsg);
    		
    	}
        
     	// Start Dattatray Date:30-June-2021
        function fileValidation(documentID,allowedFileExtensions,errorMsg) {
            var fileInput = document.getElementById(documentID);
            var filePath = fileInput.value;
            var allowedExtensions = allowedFileExtensions;
              
            if (!allowedExtensions.exec(filePath)) {
                alert(errorMsg);
                fileInput.value = '';
                return false;
            } 
        }// End Dattatray
    	
        
        function addDocuments() {
    		documentcnt = parseInt(documentcnt)+1;
   	        var table = document.getElementById('row_document_table');

   	        var rowCount = table.rows.length;
   	       
   	        var row = table.insertRow(rowCount);
   	        row.id = "row_document"+rowCount;
   	        var cell1 = row.insertCell(0);
   	        cell1.setAttribute("class", "txtlabel alignRight");
   	        cell1.setAttribute("style", "text-align: -moz-center");

   	       cell1.innerHTML = "<input type=\"hidden\" name=\"idDocType\" value=\"<%=IConstants.DOCUMENT_OTHER%>\"></input><input type=\"text\" class=\"validateRequired text-input\" style=\"width: 180px !important; \" name=\"idDocName\"></input>";
   	       
   	       var cell2 = row.insertCell(1);
   	       cell2.setAttribute("class", "txtlabel alignRight");
   	       cell2.setAttribute("style", "text-align: -moz-center");
   	       cell2.innerHTML = "<input type=\"file\" name=\"idDoc\" onchange=\"fillFileStatus('idDoc"+documentcnt+"Status')\" /><input type=\"hidden\" name=\"idDocStatus\" id=\"idDoc"+documentcnt+"Status\" value=\"0\"></input>";
   	       
   	       var cell3 = row.insertCell(2);
   	       cell3.setAttribute("class", "txtlabel alignRight");
   	       cell3.setAttribute("style", "text-align: -moz-center");
   	       cell3.innerHTML = "<a href=\"javascript:void(0)\" onclick=\"addDocuments()\" class=\"add-font\"></a><a href=\"javascript:void(0)\" onclick=\"removeDocuments('row_document"+rowCount+"')\" id=\""+documentcnt+"\" class=\"remove-font\"></a>";
    	}
        
        <%-- function addDocuments() {
	        cnt++;
	        var divTag = document.createElement("div");
	        divTag.id = "row_document"+cnt;
	        divTag.innerHTML = 	"<table>" +
			"<tr><td class=\"txtlabel alignRight\"><%=IConstants.DOCUMENT_OTHER%>" +
			"<input type=\"hidden\" name=\"idDocType\" value=\"<%=IConstants.DOCUMENT_OTHER%>\"></input></td>" +
	        "<td class=\"txtlabel alignRight\"><input type=\"text\" class=\"validateRequired\" name=\"idDocName\"></input></td>" +   			    	
	    	"<td class=\"txtlabel alignRight\"><input type=\"file\" name=\"idDoc\"/></td>"+
	    	"<td><a href=\"javascript:void(0)\" onclick=\"addDocuments()\" class=\"add-font\"></a></td>" +
	    	"<td><a href=\"javascript:void(0)\" onclick=\"removeDocuments(this.id)\" id=\""+cnt+"\" class=\"remove-font\"></a></td></tr>" +
	    	"</table>"; 
	        document.getElementById("div_id_docs").appendChild(divTag);
        } --%>
    
        
        function removeDocuments(removeId) {
        	var remove_elem = "row_document"+removeId;
        	var row_document = document.getElementById(remove_elem); 
        	document.getElementById("div_id_docs").removeChild(row_document);
        }

        
        function deleteMedicalDocuments(mediDocId) {
        	if(confirm('Are you sure, you want to delete this document?')) {
        		getContent('removeDivMedicalDocument_'+mediDocId,'DeleteMedicalDocuments.action?type=candidate&mediDocId='+mediDocId);
        	}
        } 
        
        function deleteDocuments(removeId) {
        	if(confirm('Are you sure, you want to delete this document?')) {
        		getContent('removeDivDocument_'+removeId,'DeleteDocuments.action?type=candidate&documentId='+removeId);
        	}
        } 
        
        <% if (alPrevEmployment!=null) { %>
        /* ===end parvez date: 17-09-2021=== */
        	<%-- var cnt=<%=alPrevEmployment.size()%>; --%>
        	var cnt=<%=alPrevEmployment.size()%>-1;
        /* ===end parvez date: 17-09-2021=== */
        <% } else { %>
        	var cnt =0;
        <% } %>
        
     
        
        function addPrevEmployment() {
        	cnt++;
        	
        	var divTag = document.createElement("div");
        	divTag.setAttribute("style","float: left;");
        	divTag.id = "col_prev_employer"+cnt;
        	<%-- divTag.innerHTML = "<%=request.getAttribute("sbPrevEmployment")%>" +
        		"<tr><td colspan=\"2\" ><span style=\"float: right;\">"
           		+"<a href=\"javascript:void(0)\" onclick=\"removePrevEmployment(this.id)\" id=\""+cnt+"\" class=\"remove-font\" ></a>"
           		+"<a href=javascript:void(0) onclick=addPrevEmployment() class=add-font></a></span></td></tr>" +
       			"</table>"; --%>
       			
       	/* ===start parvez date: 06-09-2021=== */	
       		divTag.innerHTML = "<table style=\"width:auto\" class=\"table table_no_border form-table\">"
       			+ "<tr><td class=\"txtlabel\" style=\"text-align:right\"> Company Name:</td>"
       			+ "<td><input type=\"text\" name=\"prevCompanyName\" onchange=\"prevCompanyExpFile("+cnt+",this.value);\" ></input></td>" + "</tr>"
       			+"<%=request.getAttribute("sbPrevEmployment")%>" 
       			+"<tr><td class=\"txtlabel alignRight\"> Experience Letter:</td>"
       			+"<td><div id=\"expfile"+cnt+"\"></div>"
       			+"<input type=\"hidden\" name=\"expLetterStatus\" id=\"expLetterStatus"+cnt+"\" value=\"0\">"
    			+"<input type=\"file\" accept=\".jpeg,.jpg,.png,.tif,.svg,.svgz,.doc,.docs,.docx,.pdf\" name=\"experienceLetter"+cnt+"\" id=\"experienceLetter"+cnt+"\" onchange=\"fillFileStatus1('expLetterStatus"+cnt+"','experienceLetter"+cnt+"'); readFileURL(this, 'expfile"+cnt+"');\" />"
        		+"</td></tr>"
       			+"<tr><td colspan=\"2\" ><span style=\"float: right;\">"
           		+"<a href=\"javascript:void(0)\" onclick=\"removePrevEmployment(this.id)\" id=\""+cnt+"\" class=\"remove-font\" ></a>"
           		+"<a href=javascript:void(0) onclick=addPrevEmployment() class=add-font></a></span></td></tr>" +
       			"</table>";
       			//console.log("expfile"+cnt);
       	/* ===end parvez date: 06-09-2021=== */
        	document.getElementById("div_prev_employment").appendChild(divTag);
        	callDatePicker();
        }
        
        
        function removePrevEmployment(removeId) {
        	var remove_elem = "col_prev_employer" + removeId;
        	var row_document = document.getElementById(remove_elem); 
        	document.getElementById("div_prev_employment").removeChild(row_document);
        
        }
        
        
        function showHideHiddenField(fieldId) {
        	if(document.getElementById(fieldId).checked) {
        		document.getElementById( "hidden_"+fieldId).value="1";
        	} else {
        		document.getElementById( "hidden_"+fieldId).value="0";
        	}
        }
        
        
        
        function checkRadio(obj,val){
        	document.getElementById(val).disabled=true;
        	if(obj.value=='false'){
        		document.getElementById(val).disabled=true;
        		document.getElementById(val+'File').disabled=true;
        	} else{
        		document.getElementById(val).disabled=false;
        		document.getElementById(val+'File').disabled=false;
        	}
        }
        
        
        function copyAddress(obj) {
        	if(obj.checked) {
        		var sel=document.getElementById("countryTmp");
        		for(var i = 0, j = sel.options.length; i < j; ++i) {
        	        if(sel.options[i].innerHTML === document.getElementById("country").options[document.getElementById("country").selectedIndex].text) {
        	           sel.selectedIndex = i;
        	           break;
        	        }
        	    }
        		
        		sel=document.getElementById("stateTmp");
        		for(var i = 0, j = sel.options.length; i < j; ++i) {
        	        if(sel.options[i].innerHTML === document.getElementById("state").options[document.getElementById("state").selectedIndex].text) {
        	           sel.selectedIndex = i;
        	           break;
        	        }
        	    }
        		
        		document.getElementById("empAddress1Tmp").value = document.getElementById("empAddress1").value;
        		document.getElementById("empAddress2Tmp").value = document.getElementById("empAddress2").value;
        		document.getElementById("cityTmp").value = document.getElementById("city").value;
        		document.getElementById("empPincodeTmp").value = document.getElementById("empPincode").value;
        	} else {
        		document.getElementById("empAddress1Tmp").value = '';
        		document.getElementById("empAddress2Tmp").value = '';
        		document.getElementById("cityTmp").value = '';
        		document.getElementById("empPincodeTmp").value = '';
        		
        		var sel=document.getElementById("countryTmp");
        		for(var i = 0, j = sel.options.length; i < j; ++i) {
        	        if(sel.options[i].innerHTML === document.getElementById("country").options[0].text) {
        	           sel.selectedIndex = i;
        	           break;
        	        }
        	    }
        		
        		sel=document.getElementById("stateTmp");
        		for(var i = 0, j = sel.options.length; i < j; ++i) {
        	        if(sel.options[i].innerHTML === document.getElementById("state").options[0].text) {
        	           sel.selectedIndex = i;
        	           break;
        	        }
        	    }
        	}
        }
        
        
        $("#frmAvailibility").submit(function(event){
    		var from = document.getElementById('fromPage').value;
    		//console.log(from);
    		if(from != null && from == "CAPP") {
    			//console.log("if");
    			event.preventDefault();
    			//$(".modal-body").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    			var recruitId = document.getElementById("recruitId").value;
    			var form_data = $("#frmAvailibility").serialize();	
    			var divResult = 'subSubDivResult';
    			$.ajax({
    				type :'POST',
    				url  :'AddCandidate.action',
    				data :form_data,
    				cache:true,
    				success : function(result) {
    					//console.log(divResult);
    					$("#"+divResult).html(result);
    				},
    				error : function(result) {
    					$.ajax({
    	    				url  :'Applications.action?recruitId='+recruitId,
    	    				cache:true,
    	    				success : function(result) {
    	    					$("#"+divResult).html(result);
    	    				}
    	    			});
    				}
    			});
    		} else {
    			//console.log("else");
    			/* window.setTimeout(function() {  
        			parent.window.location="CandidateReport.action";
        		}, 200); */
    		}
    	});
        
        /* function submitForm(){
        	
        	var frmPage = document.getElementById('fromPage').value;
        	document.getElementById('frmAvailibility').submit();
        	if(frmPage == 'CR') {
        		window.setTimeout(function() {  
        			parent.window.location="CandidateReport.action";
        		}, 200);
        	} else {
        		window.setTimeout(function() {  
        			parent.window.location="Applications.action";
        		}, 500);
        	}
        } */
        
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
        
        function readFileURL(input, targetDiv) {
        	//alert(input);
            if (input.files && input.files[0]) {
                var reader = new FileReader();
                reader.onload = function (e) {
                    $('#'+targetDiv).attr('path', e.target.result);
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
        
        function showSourceData(value) {
        	
        	if(value == 2) {
        		document.getElementById("tr_ref_details").style.display = 'table-row';
        		$("#refEmpCode").prop('required',true);
        		document.getElementById("tr_other_details").style.display = 'none';
        		$("#otherRefSrc").prop('required',false);
        	} else if(value == 8) {
        		document.getElementById("tr_ref_details").style.display = 'none';
        		$("#refEmpCode").prop('required',false);
        		document.getElementById("tr_other_details").style.display = 'table-row';
        		$("#otherRefSrc").prop('required',true);
        	} else {
        		document.getElementById("tr_ref_details").style.display = 'none';
        		$("#refEmpCode").prop('required',false);
        		document.getElementById("tr_other_details").style.display = 'none';
        		$("#otherRefSrc").prop('required',false);
        	}
        	
        }
        
        function checkEmployeeCode(value) {
        //	alert("value ===> "+value);
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
    </script>
    
</g:compress>

<script>
    /* jQuery(document).ready(function(){
        // binds form submission and fields to the validation engine
        jQuery("#frmPersonalInfo").validationEngine();
        jQuery("#frmReferences").validationEngine();
        jQuery("#frmOfficialInfo").validationEngine();
        jQuery("#frmBackgroundInfo").validationEngine();
        jQuery("#frmFamilyInfo").validationEngine();
        jQuery("#frmPrevEmployment").validationEngine();
        jQuery("#frmMedicalInfo").validationEngine();
        jQuery("#frmDocumentation").validationEngine();
        
    }); */
    
    
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
    
    /* added by Parvez date: 04-09-2021
    *	start
    */
    function addEducationCerti(count) {
		var cnt = document.getElementById("degreeCertiSubDivCnt"+count).value;
		cnt = parseInt(cnt)+1;
		var divTag = document.createElement("div");
		divTag.id = "degreeCertiSubDiv"+count+"_"+cnt;
		//divTag.setAttribute("style", "float: left;");
		/* ===start parvez date: 17-09-2021=== */
		divTag.innerHTML = "<input type=\"file\" accept=\".jpeg,.jpg,.png,.tif,.svg,.svgz,.doc,.docs,.docx,.pdf\" name=\"degreeCertificate"+count+"\" id=\"degreeCertificate"+count+"\" onchange=\"fillFileStatus1('degreeCertiStatus"+count+"','degreeCertificate"+count+"');\" required='true'/>" +
			"<a href=\"javascript:void(0);\" onclick=\"addEducationCerti('"+count+"');\" class=\"add-font\"></a>"+
			"<a href=\"javascript:void(0);\" onclick=\"removeEducationCerti('"+count+"', 'degreeCertiSubDiv"+count+"_"+cnt+"');\" class=\"remove-font\"></a>";
		/* ===end parvez date: 17-09-2021=== */
		document.getElementById("degreeCertiDiv"+count).appendChild(divTag);
		
		document.getElementById("degreeCertiSubDivCnt"+count).value = cnt;
	}
    
    function removeEducationCerti(count, removeId) {
		var removeSubDiv = document.getElementById(removeId); 
		document.getElementById("degreeCertiDiv"+count).removeChild(removeSubDiv);
	} 
    
    //===start parvez date: 14-09-2021=== 
    function validateDegreeCerti(count1,value){
    	
    	if(value.length !== 0 && value !== ""){
    		document.getElementById("degreeCertificate"+count1).className = 'validateRequired';
		}else{
			//console.log("else===inputValue1="+inputValue);
			$(".validateRequired").prop('required',false);
			document.getElementById("degreeCertificate"+count1).classList.remove("validateRequired");
		}
    	
    }
    //===end parvez date: 14-09-2021=== 
    
    
   /*  function addExperienceLetter(count) {
		var cnt = document.getElementById("expLetterSubDivCnt"+count).value;
		cnt = parseInt(cnt)+1;
		var divTag = document.createElement("div");
		divTag.id = "expLetterSubDiv"+count+"_"+cnt;
		//divTag.setAttribute("style", "float: left;");
		divTag.innerHTML = "<input type=\"file\" name=\"experienceLetter"+count+"\" id=\"experienceLetter"+count+"\" onchange=\"fillFileStatus('expLetterStatus"+count+"');\" />" +
			"<a href=\"javascript:void(0);\" onclick=\"addExperienceLetter('"+count+"');\" class=\"add-font\"></a>"+
			"<a href=\"javascript:void(0);\" onclick=\"removeExperienceLetter('"+count+"', 'expLetterSubDiv"+count+"_"+cnt+"');\" class=\"remove-font\"></a>";
		document.getElementById("expLetterDiv"+count).appendChild(divTag);
		
		document.getElementById("expLetterSubDivCnt"+count).value = cnt;
	}
    
    function removeExperienceLetter(count, removeId) {
		var removeSubDiv = document.getElementById(removeId); 
		document.getElementById("expLetterDiv"+count).removeChild(removeSubDiv);
	} */ 
    
	//===start parvez date: 14-09-2021=== 
	function prevCompanyExpFile(fileCnt, inputValue){
	//	alert(inputValue);
		//console.log("inputValue="+inputValue);
		if(inputValue.length !== 0 && inputValue !== ""){
			//console.log("if==inputValue1="+inputValue);
			document.getElementById("experienceLetter"+fileCnt).className = 'validateRequired';
		}else{
			//console.log("else===inputValue1="+inputValue);
			$(".validateRequired").prop('required',false);
			document.getElementById("experienceLetter"+fileCnt).classList.remove("validateRequired");
		}
		
	}
	//===end parvez date: 14-09-2021=== 
		
	
    /* parvez end */
    
	
</script>

<div class="pagetitle">
    <%
   // System.out.println("AddCandidate");
        Map<String, List<String>> hmValidationFields = (Map<String, List<String>> ) request.getAttribute("hmValidationFields");
        String validReqOpt = "";
        String validAsterix = "";
        
        if(session.getAttribute(IConstants.USERID)!=null) { 
        //System.out.println("Operation===> "+request.getParameter("operation"));
        %>
    <span><%=(request.getParameter("operation")!=null && !request.getParameter("operation").equals(""))?"Edit":"Enter" %> Candidate Detail</span>
    <% } else { %>
    <span>Enter Your Details</span>
    <% } %>
</div>
<!-- <div class="leftbox reportWidth" > -->
<%
    String strEmpType = (String) session.getAttribute("USERTYPE");
    String strMessage = (String) request.getAttribute("MESSAGE");
    if (strMessage == null) {
    	strMessage = "";
    }
    %>
<%if(!"U".equalsIgnoreCase(request.getParameter("operation"))) { %>
<div class="wizard">
    <div class="wizard-inner 1st">
        <div class="connecting-line"></div>
        <ul class="nav nav-tabs" role="tablist">
            <s:if test="step==1">
                <li role="presentation" class="active">
	                <a href="#pi" data-toggle="tab" aria-controls="step1" role="tab" title="Personal Information"><span class="round-tab"><i class="fa fa-user"></i></span></a>
	            </li>
	            <li role="presentation" class="disabled">
	                <a href="#bi" data-toggle="tab" aria-controls="step2" role="tab" title="Background Information"><span class="round-tab"><i class="fa fa-info"></i></span></a>
	            </li>
	            <li role="presentation" class="disabled">
	                <a href="#fi" data-toggle="tab" aria-controls="step3" role="tab" title="Family Information"><span class="round-tab"><i class="fa fa-users"></i></span></a>
	            </li>
	            <li role="presentation" class="disabled">
	                <a href="#pe" data-toggle="tab" aria-controls="step4" role="tab" title="Previous Employment"><span class="round-tab"><i class="fa fa-briefcase"></i></span></a>
	            </li>
	            <li role="presentation" class="disabled">
                	<a href="#rf" data-toggle="tab" aria-controls="step5" role="tab" title="References"><span class="round-tab"><i class="fa fa-handshake-o"></i></span></a>
	            </li>
	            <li role="presentation" class="disabled">
	                <a href="#mi" data-toggle="tab" aria-controls="step6" role="tab" title="Medical Information"><span class="round-tab"><i class="fa fa-hospital-o"></i></span></a>
	            </li>
	            <li role="presentation" class="disabled">
	                <a href="#dc" data-toggle="tab" aria-controls="step7" role="tab" title="Documentation"><span class="round-tab"><i class="fa fa-file-text"></i></span></a>
	            </li>
	            <li role="presentation" class="disabled">
                	<a href="#av" data-toggle="tab" aria-controls="step8" role="tab" title="Availablity"><span class="round-tab"><i class="fa fa-clock-o"></i></span></a>
	            </li>
            </s:if>
                       
            <s:if test="step==2">
                <li role="presentation" class="">
	                <a href="#pi" data-toggle="tab" aria-controls="step1" role="tab" title="Personal Information" onclick="loadStepOnClick('AddCandidate.action?mode=editprofile&step=0&operation=EP&CandidateId=<%=(String)request.getAttribute("CandidateId")%>&fromPage=<%=(String)request.getAttribute("fromPage")%>')"><span class="round-tab"><i class="fa fa-user"></i></span></a>
	            </li>
	            <li role="presentation" class="active">
	                <a href="#bi" data-toggle="tab" aria-controls="step2" role="tab" title="Background Information"><span class="round-tab"><i class="fa fa-info"></i></span></a>
	            </li>
	            <li role="presentation" class="disabled">
	                <a href="#fi" data-toggle="tab" aria-controls="step3" role="tab" title="Family Information"><span class="round-tab"><i class="fa fa-users"></i></span></a>
	            </li>
	            <li role="presentation" class="disabled">
	                <a href="#pe" data-toggle="tab" aria-controls="step4" role="tab" title="Previous Employment"><span class="round-tab"><i class="fa fa-briefcase"></i></span></a>
	            </li>
	            <li role="presentation" class="disabled">
                	<a href="#rf" data-toggle="tab" aria-controls="step5" role="tab" title="References"><span class="round-tab"><i class="fa fa-handshake-o"></i></span></a>
	            </li>
	            <li role="presentation" class="disabled">
	                <a href="#mi" data-toggle="tab" aria-controls="step6" role="tab" title="Medical Information"><span class="round-tab"><i class="fa fa-hospital-o"></i></span></a>
	            </li>
	            <li role="presentation" class="disabled">
	                <a href="#dc" data-toggle="tab" aria-controls="step7" role="tab" title="Documentation"><span class="round-tab"><i class="fa fa-file-text"></i></span></a>
	            </li>
	            <li role="presentation" class="disabled">
                	<a href="#av" data-toggle="tab" aria-controls="step8" role="tab" title="Availablity"><span class="round-tab"><i class="fa fa-clock-o"></i></span></a>
	            </li>
            </s:if>
            
            <s:if test="step==3">
                <li role="presentation" class="">
	                <a href="#pi" data-toggle="tab" aria-controls="step1" role="tab" title="Personal Information" onclick="loadStepOnClick('AddCandidate.action?mode=editprofile&step=0&operation=EP&CandidateId=<%=(String)request.getAttribute("CandidateId")%>&fromPage=<%=(String)request.getAttribute("fromPage")%>')"><span class="round-tab"><i class="fa fa-user"></i></span></a>
	            </li>
	            <li role="presentation" class="">
	                <a href="#bi" data-toggle="tab" aria-controls="step2" role="tab" title="Background Information" onclick="loadStepOnClick('AddCandidate.action?mode=editprofile&step=1&operation=EP&CandidateId=<%=request.getAttribute("CandidateId")%>&fromPage=<%=(String)request.getAttribute("fromPage")%>')"><span class="round-tab"><i class="fa fa-info"></i></span></a>
	            </li>
	            <li role="presentation" class="active">
	                <a href="#fi" data-toggle="tab" aria-controls="step3" role="tab" title="Family Information"><span class="round-tab"><i class="fa fa-users"></i></span></a>
	            </li>
	            <li role="presentation" class="disabled">
	                <a href="#pe" data-toggle="tab" aria-controls="step4" role="tab" title="Previous Employment"><span class="round-tab"><i class="fa fa-briefcase"></i></span></a>
	            </li>
	            <li role="presentation" class="disabled">
                	<a href="#rf" data-toggle="tab" aria-controls="step5" role="tab" title="References"><span class="round-tab"><i class="fa fa-handshake-o"></i></span></a>
	            </li>
	            <li role="presentation" class="disabled">
	                <a href="#mi" data-toggle="tab" aria-controls="step6" role="tab" title="Medical Information"><span class="round-tab"><i class="fa fa-hospital-o"></i></span></a>
	            </li>
	            <li role="presentation" class="disabled">
	                <a href="#dc" data-toggle="tab" aria-controls="step7" role="tab" title="Documentation"><span class="round-tab"><i class="fa fa-file-text"></i></span></a>
	            </li>
	            <li role="presentation" class="disabled">
                	<a href="#av" data-toggle="tab" aria-controls="step8" role="tab" title="Availablity"><span class="round-tab"><i class="fa fa-clock-o"></i></span></a>
	            </li>
            </s:if>
                       
            <s:if test="step==4">
                <li role="presentation" class="">
	                <a href="#pi" data-toggle="tab" aria-controls="step1" role="tab" title="Personal Information" onclick="loadStepOnClick('AddCandidate.action?mode=editprofile&step=0&operation=EP&CandidateId=<%=(String)request.getAttribute("CandidateId")%>&fromPage=<%=(String)request.getAttribute("fromPage")%>')"><span class="round-tab"><i class="fa fa-user"></i></span></a>
	            </li>
	            <li role="presentation" class="">
	                <a href="#bi" data-toggle="tab" aria-controls="step2" role="tab" title="Background Information" onclick="loadStepOnClick('AddCandidate.action?mode=editprofile&step=1&operation=EP&CandidateId=<%=request.getAttribute("CandidateId")%>&fromPage=<%=(String)request.getAttribute("fromPage")%>')"><span class="round-tab"><i class="fa fa-info"></i></span></a>
	            </li>
	            <li role="presentation" class="">
	                <a href="#fi" data-toggle="tab" aria-controls="step3" role="tab" title="Family Information" onclick="loadStepOnClick('AddCandidate.action?mode=editprofile&step=2&operation=EP&CandidateId=<%=request.getAttribute("CandidateId")%>&fromPage=<%=(String)request.getAttribute("fromPage")%>')"><span class="round-tab"><i class="fa fa-users"></i></span></a>
	            </li>
	            <li role="presentation" class="active">
	                <a href="#pe" data-toggle="tab" aria-controls="step4" role="tab" title="Previous Employment"><span class="round-tab"><i class="fa fa-briefcase"></i></span></a>
	            </li>
	            <li role="presentation" class="disabled">
                	<a href="#rf" data-toggle="tab" aria-controls="step5" role="tab" title="References"><span class="round-tab"><i class="fa fa-handshake-o"></i></span></a>
	            </li>
	            <li role="presentation" class="disabled">
	                <a href="#mi" data-toggle="tab" aria-controls="step6" role="tab" title="Medical Information"><span class="round-tab"><i class="fa fa-hospital-o"></i></span></a>
	            </li>
	            <li role="presentation" class="disabled">
	                <a href="#dc" data-toggle="tab" aria-controls="step7" role="tab" title="Documentation"><span class="round-tab"><i class="fa fa-file-text"></i></span></a>
	            </li>
	            <li role="presentation" class="disabled">
                	<a href="#av" data-toggle="tab" aria-controls="step8" role="tab" title="Availablity"><span class="round-tab"><i class="fa fa-clock-o"></i></span></a>
	            </li>
            </s:if>
                       
            <s:if test="step==5">
                <li role="presentation" class="">
	                <a href="#pi" data-toggle="tab" aria-controls="step1" role="tab" title="Personal Information" onclick="loadStepOnClick('AddCandidate.action?mode=editprofile&step=0&operation=EP&CandidateId=<%=(String)request.getAttribute("CandidateId")%>&fromPage=<%=(String)request.getAttribute("fromPage")%>')"><span class="round-tab"><i class="fa fa-user"></i></span></a>
	            </li>
	            <li role="presentation" class="">
	                <a href="#bi" data-toggle="tab" aria-controls="step2" role="tab" title="Background Information" onclick="loadStepOnClick('AddCandidate.action?mode=editprofile&step=1&operation=EP&CandidateId=<%=request.getAttribute("CandidateId")%>&fromPage=<%=(String)request.getAttribute("fromPage")%>')"><span class="round-tab"><i class="fa fa-info"></i></span></a>
	            </li>
	            <li role="presentation" class="">
	                <a href="#fi" data-toggle="tab" aria-controls="step3" role="tab" title="Family Information" onclick="loadStepOnClick('AddCandidate.action?mode=editprofile&step=2&operation=EP&CandidateId=<%=request.getAttribute("CandidateId")%>&fromPage=<%=(String)request.getAttribute("fromPage")%>')"><span class="round-tab"><i class="fa fa-users"></i></span></a>
	            </li>
	            <li role="presentation" class="">
	                <a href="#pe" data-toggle="tab" aria-controls="step4" role="tab" title="Previous Employment" onclick="loadStepOnClick('AddCandidate.action?mode=editprofile&step=3&operation=EP&CandidateId=<%=request.getAttribute("CandidateId")%>&fromPage=<%=(String)request.getAttribute("fromPage")%>')"><span class="round-tab"><i class="fa fa-briefcase"></i></span></a>
	            </li>
	            <li role="presentation" class="active">
                	<a href="#rf" data-toggle="tab" aria-controls="step5" role="tab" title="References"><span class="round-tab"><i class="fa fa-handshake-o"></i></span></a>
	            </li>
	            <li role="presentation" class="disabled">
	                <a href="#mi" data-toggle="tab" aria-controls="step6" role="tab" title="Medical Information"><span class="round-tab"><i class="fa fa-hospital-o"></i></span></a>
	            </li>
	            <li role="presentation" class="disabled">
	                <a href="#dc" data-toggle="tab" aria-controls="step7" role="tab" title="Documentation"><span class="round-tab"><i class="fa fa-file-text"></i></span></a>
	            </li>
	            <li role="presentation" class="disabled">
                	<a href="#av" data-toggle="tab" aria-controls="step8" role="tab" title="Availablity"><span class="round-tab"><i class="fa fa-clock-o"></i></span></a>
	            </li>
            </s:if>
                        
            <s:if test="step==6">
                <li role="presentation" class="">
	                <a href="#pi" data-toggle="tab" aria-controls="step1" role="tab" title="Personal Information" onclick="loadStepOnClick('AddCandidate.action?mode=editprofile&step=0&operation=EP&CandidateId=<%=(String)request.getAttribute("CandidateId")%>&fromPage=<%=(String)request.getAttribute("fromPage")%>')"><span class="round-tab"><i class="fa fa-user"></i></span></a>
	            </li>
	            <li role="presentation" class="">
	                <a href="#bi" data-toggle="tab" aria-controls="step2" role="tab" title="Background Information" onclick="loadStepOnClick('AddCandidate.action?mode=editprofile&step=1&operation=EP&CandidateId=<%=request.getAttribute("CandidateId")%>&fromPage=<%=(String)request.getAttribute("fromPage")%>')"><span class="round-tab"><i class="fa fa-info"></i></span></a>
	            </li>
	            <li role="presentation" class="">
	                <a href="#fi" data-toggle="tab" aria-controls="step3" role="tab" title="Family Information" onclick="loadStepOnClick('AddCandidate.action?mode=editprofile&step=2&operation=EP&CandidateId=<%=request.getAttribute("CandidateId")%>&fromPage=<%=(String)request.getAttribute("fromPage")%>')"><span class="round-tab"><i class="fa fa-users"></i></span></a>
	            </li>
	            <li role="presentation" class="">
	                <a href="#pe" data-toggle="tab" aria-controls="step4" role="tab" title="Previous Employment" onclick="loadStepOnClick('AddCandidate.action?mode=editprofile&step=3&operation=EP&CandidateId=<%=request.getAttribute("CandidateId")%>&fromPage=<%=(String)request.getAttribute("fromPage")%>')"><span class="round-tab"><i class="fa fa-briefcase"></i></span></a>
	            </li>
	            <li role="presentation" class="">
                	<a href="#rf" data-toggle="tab" aria-controls="step5" role="tab" title="References" onclick="loadStepOnClick('AddCandidate.action?mode=editprofile&step=4&operation=EP&CandidateId=<%=request.getAttribute("CandidateId")%>&fromPage=<%=(String)request.getAttribute("fromPage")%>')"><span class="round-tab"><i class="fa fa-handshake-o"></i></span></a>
	            </li>
	            <li role="presentation" class="active">
	                <a href="#mi" data-toggle="tab" aria-controls="step6" role="tab" title="Medical Information"><span class="round-tab"><i class="fa fa-hospital-o"></i></span></a>
	            </li>
	            <li role="presentation" class="disabled">
	                <a href="#dc" data-toggle="tab" aria-controls="step7" role="tab" title="Documentation"><span class="round-tab"><i class="fa fa-file-text"></i></span></a>
	            </li>
	            <li role="presentation" class="disabled">
                	<a href="#av" data-toggle="tab" aria-controls="step8" role="tab" title="Availablity"><span class="round-tab"><i class="fa fa-clock-o"></i></span></a>
	            </li>
            </s:if>
                       
            <s:if test="step==7">
                <li role="presentation" class="">
	                <a href="#pi" data-toggle="tab" aria-controls="step1" role="tab" title="Personal Information" onclick="loadStepOnClick('AddCandidate.action?mode=editprofile&step=0&operation=EP&CandidateId=<%=(String)request.getAttribute("CandidateId")%>&fromPage=<%=(String)request.getAttribute("fromPage")%>')"><span class="round-tab"><i class="fa fa-user"></i></span></a>
	            </li>
	            <li role="presentation" class="">
	                <a href="#bi" data-toggle="tab" aria-controls="step2" role="tab" title="Background Information" onclick="loadStepOnClick('AddCandidate.action?mode=editprofile&step=1&operation=EP&CandidateId=<%=request.getAttribute("CandidateId")%>&fromPage=<%=(String)request.getAttribute("fromPage")%>')"><span class="round-tab"><i class="fa fa-info"></i></span></a>
	            </li>
	            <li role="presentation" class="">
	                <a href="#fi" data-toggle="tab" aria-controls="step3" role="tab" title="Family Information" onclick="loadStepOnClick('AddCandidate.action?mode=editprofile&step=2&operation=EP&CandidateId=<%=request.getAttribute("CandidateId")%>&fromPage=<%=(String)request.getAttribute("fromPage")%>')"><span class="round-tab"><i class="fa fa-users"></i></span></a>
	            </li>
	            <li role="presentation" class="">
	                <a href="#pe" data-toggle="tab" aria-controls="step4" role="tab" title="Previous Employment" onclick="loadStepOnClick('AddCandidate.action?mode=editprofile&step=3&operation=EP&CandidateId=<%=request.getAttribute("CandidateId")%>&fromPage=<%=(String)request.getAttribute("fromPage")%>')"><span class="round-tab"><i class="fa fa-briefcase"></i></span></a>
	            </li>
	            <li role="presentation" class="">
                	<a href="#rf" data-toggle="tab" aria-controls="step5" role="tab" title="References" onclick="loadStepOnClick('AddCandidate.action?mode=editprofile&step=4&operation=EP&CandidateId=<%=request.getAttribute("CandidateId")%>&fromPage=<%=(String)request.getAttribute("fromPage")%>')"><span class="round-tab"><i class="fa fa-handshake-o"></i></span></a>
	            </li>
	            <li role="presentation" class="">
	                <a href="#mi" data-toggle="tab" aria-controls="step6" role="tab" title="Medical Information" onclick="loadStepOnClick('AddCandidate.action?mode=editprofile&step=5&operation=EP&CandidateId=<%=request.getAttribute("CandidateId")%>&fromPage=<%=(String)request.getAttribute("fromPage")%>')"><span class="round-tab"><i class="fa fa-hospital-o"></i></span></a>
	            </li>
	            <li role="presentation" class="active">
	                <a href="#dc" data-toggle="tab" aria-controls="step7" role="tab" title="Documentation"><span class="round-tab"><i class="fa fa-file-text"></i></span></a>
	            </li>
	            <li role="presentation" class="disabled">
                	<a href="#av" data-toggle="tab" aria-controls="step8" role="tab" title="Availablity"><span class="round-tab"><i class="fa fa-clock-o"></i></span></a>
	            </li>
            </s:if>
                        
            <s:if test="step==8">
                <li role="presentation" class="">
	                <a href="#pi" data-toggle="tab" aria-controls="step1" role="tab" title="Personal Information" onclick="loadStepOnClick('AddCandidate.action?mode=editprofile&step=0&operation=EP&CandidateId=<%=(String)request.getAttribute("CandidateId")%>&fromPage=<%=(String)request.getAttribute("fromPage")%>')"><span class="round-tab"><i class="fa fa-user"></i></span></a>
	            </li>
	            <li role="presentation" class="">
	                <a href="#bi" data-toggle="tab" aria-controls="step2" role="tab" title="Background Information" onclick="loadStepOnClick('AddCandidate.action?mode=editprofile&step=1&operation=EP&CandidateId=<%=request.getAttribute("CandidateId")%>&fromPage=<%=(String)request.getAttribute("fromPage")%>')"><span class="round-tab"><i class="fa fa-info"></i></span></a>
	            </li>
	            <li role="presentation" class="">
	                <a href="#fi" data-toggle="tab" aria-controls="step3" role="tab" title="Family Information" onclick="loadStepOnClick('AddCandidate.action?mode=editprofile&step=2&operation=EP&CandidateId=<%=request.getAttribute("CandidateId")%>&fromPage=<%=(String)request.getAttribute("fromPage")%>')"><span class="round-tab"><i class="fa fa-users"></i></span></a>
	            </li>
	            <li role="presentation" class="">
	                <a href="#pe" data-toggle="tab" aria-controls="step4" role="tab" title="Previous Employment" onclick="loadStepOnClick('AddCandidate.action?mode=editprofile&step=3&operation=EP&CandidateId=<%=request.getAttribute("CandidateId")%>&fromPage=<%=(String)request.getAttribute("fromPage")%>')"><span class="round-tab"><i class="fa fa-briefcase"></i></span></a>
	            </li>
	            <li role="presentation" class="">
                	<a href="#rf" data-toggle="tab" aria-controls="step5" role="tab" title="References" onclick="loadStepOnClick('AddCandidate.action?mode=editprofile&step=4&operation=EP&CandidateId=<%=request.getAttribute("CandidateId")%>&fromPage=<%=(String)request.getAttribute("fromPage")%>')"><span class="round-tab"><i class="fa fa-handshake-o"></i></span></a>
	            </li>
	            <li role="presentation" class="">
	                <a href="#mi" data-toggle="tab" aria-controls="step6" role="tab" title="Medical Information" onclick="loadStepOnClick('AddCandidate.action?mode=editprofile&step=5&operation=EP&CandidateId=<%=request.getAttribute("CandidateId")%>&fromPage=<%=(String)request.getAttribute("fromPage")%>')"><span class="round-tab"><i class="fa fa-hospital-o"></i></span></a>
	            </li>
	            <li role="presentation" class="">
	                <a href="#dc" data-toggle="tab" aria-controls="step7" role="tab" title="Documentation" onclick="loadStepOnClick('AddCandidate.action?mode=editprofile&step=6&operation=EP&CandidateId=<%=request.getAttribute("CandidateId")%>&fromPage=<%=(String)request.getAttribute("fromPage")%>')"><span class="round-tab"><i class="fa fa-file-text"></i></span></a>
	            </li>
	            <li role="presentation" class="active">
                	<a href="#av" data-toggle="tab" aria-controls="step8" role="tab" title="Availablity"><span class="round-tab"><i class="fa fa-clock-o"></i></span></a>
	            </li>
            </s:if>            
        </ul>
    </div>
</div>
<%} %>

<p class="message"><%=strMessage%></p>
<div class="tab-content">
    <s:if test="step==1 || mode=='report'">
        <div class="tab-pane active" role="tabpanel" id="pi">
            <s:form theme="simple" action="AddCandidate" name="frmPersonalInfo" id="frmPersonalInfo" method="POST" cssClass="formcss" enctype="multipart/form-data" onsubmit="return checkImageSize();">
                <s:hidden name="show"></s:hidden>
                <s:hidden name="operation" />
                <s:hidden name="recruitId" />
                <s:hidden name="CandidateId" />
                <s:hidden name="mode" />
                <s:hidden name="step" />
                <s:hidden name="show"></s:hidden>
                <s:hidden name="jobcode" />
                <s:hidden name="fromPage" id="fromPage" />
                <div>
                    <table border="0" class="table table_no_border form-table">
                        <tr>
                            <td class="tdLabelheadingBg alignCenter" colspan="2"><span style="color:#68AC3B; font-size:18px; padding:5px;font-weight: 600;">
                                Step 1 : </span><span style="font-weight: 600;font-size: 16px;"> Enter Candidate Personal Information</span>
                            </td>
                        </tr>
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
                                <% if(session.getAttribute("isApproved")==null) { %>
                                <% if(uF.parseToBoolean(candiSalutationValidList.get(0))) { %>
                                <s:select list="salutationList" name="salutation" headerKey="" headerValue="Select Salutation"
                                    listKey="salutationId" listValue="salutationName" cssClass="validateRequired" />
                                <% } else { %>
                                <s:select list="salutationList" name="salutation" headerKey="" headerValue="Select Salutation"
                                    listKey="salutationId" listValue="salutationName"/>
                                <% } %>
                                <% } else { %>
                                <s:textfield name="salutation" required="true" disabled="true"/>
                                <s:hidden name="salutation" />
                                <% } %>
                                <span class="hint">Candidate's salutation.<span class="hint-pointer">&nbsp;</span></span>
                            </td>
                        </tr>
                        <tr>
                            <td colspan=2>
                                <s:fielderror >
                                    <s:param>empFname</s:param>
                                </s:fielderror>
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
                                <%if(session.getAttribute("isApproved")==null) { %>
                                <input type="text" name="empFname" class="<%=validReqOpt%>" value="<%=uF.showData((String)request.getAttribute("empFname"), "") %>"/>
                                <% } else { %>
                                <s:textfield name="empFname" required="true" disabled="true" />
                                <s:hidden name="empFname" />
                                <% } %>
                                <span class="hint">Candidate's first name.<span class="hint-pointer">&nbsp;</span></span>
                            </td>
                        </tr>
                        <tr>
                            <% 	List<String> candiMNameValidList = hmValidationFields.get("CANDI_MIDDLE_NAME"); 
                                validReqOpt = "";
                                validAsterix = "";
                                if(candiMNameValidList != null && uF.parseToBoolean(candiMNameValidList.get(0))) {
                                	validReqOpt = "validateRequired";
                                	validAsterix = "<sup>*</sup>";
                                }
                                %>
                            <td class="txtlabel alignRight">Middle Name:<%=validAsterix %></td>
                            <td>
                                <% if(session.getAttribute("isApproved")==null) { %>
                                <input type="text" name="empMname" class="<%=validReqOpt%>" value="<%=uF.showData((String)request.getAttribute("empMname"), "") %>"/>
                                <% } else { %>
                                <s:textfield name="empMname" required="true" disabled="true" />
                                <s:hidden name="empMname" />
                                <% } %>
                                <span class="hint">Candidate's Middle name.<span class="hint-pointer">&nbsp;</span></span>
                            </td>
                        </tr>
                        <tr>
                            <td colspan=2>
                                <s:fielderror >
                                    <s:param>empLname</s:param>
                                </s:fielderror>
                            </td>
                        </tr>
                        <tr>
                            <% 	List<String> candiLNameValidList = hmValidationFields.get("CANDI_LAST_NAME"); 
                                validReqOpt = "";
                                validAsterix = "";
                                if(candiLNameValidList != null && uF.parseToBoolean(candiLNameValidList.get(0))) {
                                	validReqOpt = "validateRequired";
                                	validAsterix = "<sup>*</sup>";
                                }
                                %>
                            <td class="txtlabel alignRight">Last Name:<%=validAsterix %></td>
                            <td>
                                <% if(session.getAttribute("isApproved")==null) { %>
                                <input type="text" name="empLname" class="<%=validReqOpt%>" value="<%=uF.showData((String)request.getAttribute("empLname"), "") %>"/>
                                <% } else { %>
                                <s:textfield name="empLname" required="true" disabled="true" />
                                <s:hidden name="empLname" />
                                <%}%>
                                <span class="hint">Candidate's last name.<span class="hint-pointer">&nbsp;</span></span>
                            </td>
                        </tr>
                        <tr>
                            <td colspan=2>
                                <s:fielderror >
                                    <s:param>empEmail</s:param>
                                </s:fielderror>
                            </td>
                        </tr>
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
                                <%if(session.getAttribute("isApproved")==null) { %>
                                	<input type="text" name="empEmail" id="empEmail" class="<%=validReqOpt%>" value="<%=uF.showData((String)request.getAttribute("empEmail"), "") %>" onchange="checkMailID(this.value);"/>  <!-- getContent('emailValidatorMessege','EmailValidation.action?candiEmail='+this.value) -->
                                <% } else { %>
	                                <s:textfield name="empEmail" id="empEmail" required="true" disabled="true"/>
	                                <s:hidden name="empEmail" id="empEmail"/>
                                <% } %>
                                <span class="hint">Email id is required as the user will received all information on this id.<span class="hint-pointer">&nbsp;</span></span>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="2">
                                <div id="emailValidatorMessege" style="font-size: 12px; float: right;"></div>
                            </td>
                        </tr>
                        <tr>
                            <td colspan=2 style="font-size: 14px;">Permanent Address:<hr style="background-color:#346897;height:1px">&nbsp;
                                <s:fielderror ><s:param>empAddress1</s:param></s:fielderror>
                            </td>
                        </tr>
                        <tr>
                            <% 	List<String> candiPAddress1ValidList = hmValidationFields.get("CANDI_PERMANENT_ADDRESS_1"); 
                                validReqOpt = "";
                                validAsterix = "";
                                if(candiPAddress1ValidList != null && uF.parseToBoolean(candiPAddress1ValidList.get(0))) {
                                	validReqOpt = "validateRequired";
                                	validAsterix = "<sup>*</sup>";
                                }
                                %>
                            <td class="txtlabel alignRight">Address1:<%=validAsterix %></td>
                            <td><input type="text" name="empAddress1" id="empAddress1" class="<%=validReqOpt%>" value="<%=uF.showData((String)request.getAttribute("empAddress1"), "") %>"/></td>
                        </tr>
                        <tr>
                            <% 	List<String> candiPAddress2ValidList = hmValidationFields.get("CANDI_PERMANENT_ADDRESS_2"); 
                                validReqOpt = "";
                                validAsterix = "";
                                if(candiPAddress2ValidList != null && uF.parseToBoolean(candiPAddress2ValidList.get(0))) {
                                	validReqOpt = "validateRequired";
                                	validAsterix = "<sup>*</sup>";
                                }
                                %>
                            <td class="txtlabel alignRight">Address2:<%=validAsterix %></td>
                            <td><input type="text" name="empAddress2" id="empAddress2" class="<%=validReqOpt%>" value="<%=uF.showData((String)request.getAttribute("empAddress2"), "") %>"/></td>
                        </tr>
                        <tr>
                            <% 	List<String> candiPSuburbValidList = hmValidationFields.get("CANDI_PERMANENT_SUBURB"); 
                                validReqOpt = "";
                                validAsterix = "";
                                if(candiPSuburbValidList != null && uF.parseToBoolean(candiPSuburbValidList.get(0))) {
                                	validReqOpt = "validateRequired";
                                	validAsterix = "<sup>*</sup>";
                                }
                                %>
                            <td class="txtlabel alignRight">Suburb:<%=validAsterix %></td>
                            <td><input type="text" name="city" id="city" class="<%=validReqOpt%>" value="<%=uF.showData((String)request.getAttribute("city"), "") %>"/></td>
                        </tr>
                        <tr>
                            <% 	List<String> candiPCountryValidList = hmValidationFields.get("CANDI_PERMANENT_COUNTRY"); 
                                validReqOpt = "";
                                validAsterix = "";
                                if(candiPCountryValidList != null && uF.parseToBoolean(candiPCountryValidList.get(0))) {
                                	validReqOpt = "validateRequired";
                                	validAsterix = "<sup>*</sup>";
                                }
                                %>
                            <td class="txtlabel alignRight">Select Country:<%=validAsterix %></td>
                            <td>
                                <% if(candiPCountryValidList != null && uF.parseToBoolean(candiPCountryValidList.get(0))) { %>
                                <s:select id="country" cssClass="validateRequired" name="country" listKey="countryId" listValue="countryName" headerKey=""
                                    headerValue="Select Country" onchange="getContentAcs('statetdid','GetStates.action?country='+this.value+'&type=PADD&validReq=1');" list="countryList" key="" required="true" />
                                <% } else { %>
                                <s:select id="country" name="country" listKey="countryId" listValue="countryName" headerKey=" " headerValue="Select Country" 
                                    onchange="getContentAcs('statetdid','GetStates.action?country='+this.value+'&type=PADD');" list="countryList" key="" required="true" />
                                <% } %>		
                            </td>
                        </tr>
                        <tr>
                            <% 	List<String> candiPStateValidList = hmValidationFields.get("CANDI_PERMANENT_STATE"); 
                                validReqOpt = "";
                                validAsterix = "";
                                if(candiPStateValidList != null && uF.parseToBoolean(candiPStateValidList.get(0))) {
                                	validReqOpt = "validateRequired";
                                	validAsterix = "<sup>*</sup>";
                                }
                                %>
                            <td class="txtlabel alignRight">Select State:<%=validAsterix %></td>
                            <td id="statetdid">
                                <% if(candiPStateValidList != null && uF.parseToBoolean(candiPStateValidList.get(0))) { %>
                                <s:select theme="simple" title="state" cssClass="validateRequired" id="state" name="state" listKey="stateId" 
                                    listValue="stateName" headerKey="" headerValue="Select State" list="stateList" key="" required="true" />
                                <% } else { %>
                                <s:select theme="simple" title="state" id="state" name="state" listKey="stateId" listValue="stateName" headerKey="" 
                                    headerValue="Select State" list="stateList" key="" required="true" cssClass=" " />
                                <% } %>
                            </td>
                        </tr>
                        <tr>
                            <% 	List<String> candiPPostcodeValidList = hmValidationFields.get("CANDI_PERMANENT_POSTCODE"); 
                                validReqOpt = "";
                                validAsterix = "";
                                if(candiPPostcodeValidList != null && uF.parseToBoolean(candiPPostcodeValidList.get(0))) {
                                	validReqOpt = "validateRequired";
                                	validAsterix = "<sup>*</sup>";
                                }
                                %>
                        <!-- ===start parvez date: 30-07-2022=== -->   
                            <%-- <td class="txtlabel alignRight">Postcode:<%=validAsterix %></td> --%>
                            <td class="txtlabel alignRight">Pincode:<%=validAsterix %></td>
                        <!-- ===end parvez date: 30-07-2022=== -->   
                            <td><input type="text" onkeypress="return isOnlyNumberKey(event)" name="empPincode" id= "empPincode" class="<%=validReqOpt%>" value="<%=uF.showData((String)request.getAttribute("empPincode"), "") %>"/></td>
                        </tr>
                        <tr>
                            <td style="border-bottom:1px solid #346897; font-size: 14px;">
                                Temporary Address:
                            </td>
                            <td style="border-bottom:1px solid #346897;"><input type="checkbox" onclick="copyAddress(this);" />Same as above</td>
                        </tr>
                        <tr>
                            <% 	List<String> candiTAddress1ValidList = hmValidationFields.get("CANDI_TEMPORARY_ADDRESS_1"); 
                                validReqOpt = "";
                                validAsterix = "";
                                if(candiTAddress1ValidList != null && uF.parseToBoolean(candiTAddress1ValidList.get(0))) {
                                	validReqOpt = "validateRequired";
                                	validAsterix = "<sup>*</sup>";
                                }
                                %>
                            <td class="txtlabel alignRight">Address1:<%=validAsterix %></td>
                            <td><input type="text" name="empAddress1Tmp" id="empAddress1Tmp" class="<%=validReqOpt%>" value="<%=uF.showData((String)request.getAttribute("empAddress1Tmp"), "") %>"/></td>
                        </tr>
                        <tr>
                            <% 	List<String> candiTAddress2ValidList = hmValidationFields.get("CANDI_TEMPORARY_ADDRESS_2"); 
                                validReqOpt = "";
                                validAsterix = "";
                                if(candiTAddress2ValidList != null && uF.parseToBoolean(candiTAddress2ValidList.get(0))) {
                                	validReqOpt = "validateRequired";
                                	validAsterix = "<sup>*</sup>";
                                }
                                %>
                            <td class="txtlabel alignRight">Address2:<%=validAsterix %></td>
                            <td><input type="text" name="empAddress2Tmp" id="empAddress2Tmp" class="<%=validReqOpt%>" value="<%=uF.showData((String)request.getAttribute("empAddress2Tmp"), "") %>"/></td>
                        </tr>
                        <tr>
                            <% 	List<String> candiTSuburbValidList = hmValidationFields.get("CANDI_TEMPORARY_SUBURB"); 
                                validReqOpt = "";
                                validAsterix = "";
                                if(candiTSuburbValidList != null && uF.parseToBoolean(candiTSuburbValidList.get(0))) {
                                	validReqOpt = "validateRequired";
                                	validAsterix = "<sup>*</sup>";
                                }
                                %>
                            <td class="txtlabel alignRight">Suburb:<%=validAsterix %></td>
                            <td><input type="text" name="cityTmp" id="cityTmp" class="<%=validReqOpt%>" value="<%=uF.showData((String)request.getAttribute("cityTmp"), "") %>"/></td>
                        </tr>
                        <tr>
                            <% 	List<String> candiTCountryValidList = hmValidationFields.get("CANDI_TEMPORARY_COUNTRY"); 
                                validReqOpt = "";
                                validAsterix = "";
                                if(candiTCountryValidList != null && uF.parseToBoolean(candiTCountryValidList.get(0))) {
                                	validReqOpt = "validateRequired";
                                	validAsterix = "<sup>*</sup>";
                                }
                                %>
                            <td class="txtlabel alignRight">Select Country:<%=validAsterix %></td>
                            <td>
                                <% if(candiTCountryValidList != null && uF.parseToBoolean(candiTCountryValidList.get(0))) { %>
                                <s:select id="countryTmp" cssClass="validateRequired" name="countryTmp" listKey="countryId" listValue="countryName" headerKey="" 
                                    headerValue="Select Country" onchange="getContentAcs('statetdid1','GetStates.action?country='+this.value+'&type=TADD&validReq=1');" list="countryList" key="" required="true" />
                                <% } else { %>
                                <s:select id="countryTmp" name="countryTmp" listKey="countryId"  listValue="countryName" headerKey="" headerValue="Select Country" 
                                    onchange="getContentAcs('statetdid1','GetStates.action?country='+this.value+'&type=TADD');" list="countryList" key="" required="true" />
                                <% } %>		
                            </td>
                        </tr>
                        <tr>
                            <% 	List<String> candiTStateValidList = hmValidationFields.get("CANDI_TEMPORARY_STATE"); 
                                validReqOpt = "";
                                validAsterix = "";
                                if(candiTStateValidList != null && uF.parseToBoolean(candiTStateValidList.get(0))) {
                                	validReqOpt = "validateRequired";
                                	validAsterix = "<sup>*</sup>";
                                }
                                %>
                            <td class="txtlabel alignRight">Select State:<%=validAsterix %></td>
                            <td id="statetdid1">
                                <% if(candiTStateValidList != null && uF.parseToBoolean(candiTStateValidList.get(0))) { %>
                                <s:select theme="simple" title="state" cssClass="validateRequired" id="stateTmp" name="stateTmp" listKey="stateId" 
                                    listValue="stateName" headerKey="" headerValue="Select State" list="stateList" key="" required="true" />
                                <% } else { %>
                                <s:select theme="simple" title="state" id="stateTmp" name="stateTmp" listKey="stateId" listValue="stateName" headerKey="" 
                                    headerValue="Select State" list="stateList" key="" required="true" />
                                <% } %>	
                            </td>
                        </tr>
                        <tr>
                            <% 	List<String> candiTPostcodeValidList = hmValidationFields.get("CANDI_TEMPORARY_POSTCODE"); 
                                validReqOpt = "";
                                validAsterix = "";
                                if(candiTPostcodeValidList != null && uF.parseToBoolean(candiTPostcodeValidList.get(0))) {
                                	validReqOpt = "validateRequired";
                                	validAsterix = "<sup>*</sup>";
                                }
                                %>
                       <!-- ===start parvez date: 30-07-2022=== -->     
                            <%-- <td class="txtlabel alignRight">Postcode:<%=validAsterix %></td> --%>
                            <td class="txtlabel alignRight">Pincode:<%=validAsterix %></td>
                       <!-- ===end parvez date: 30-07-2022=== -->      
                            <td><input type="text" onkeypress="return isOnlyNumberKey(event)" name="empPincodeTmp" id="empPincodeTmp" class="<%=validReqOpt%>" value="<%=uF.showData((String)request.getAttribute("empPincodeTmp"),"") %>"/></td>
                        </tr>
                        <tr>
                            <td colspan=2 style="border-bottom:1px solid #346897; font-size: 14px;">Personal Information: </td>
                        </tr>
                        <tr>
                            <% 	List<String> candiLandlineNoValidList = hmValidationFields.get("CANDI_LANDLINE_NO"); 
                                validReqOpt = "";
                                validAsterix = "";
                                if(candiLandlineNoValidList != null && uF.parseToBoolean(candiLandlineNoValidList.get(0))) {
                                	validReqOpt = "validateRequired";
                                	validAsterix = "<sup>*</sup>";
                                }
                                %>
                            <td class="txtlabel alignRight">Landline Number:<%=validAsterix %></td>
                            <td><input type="text" name="empContactno" onkeypress="return isOnlyNumberKey(event)"  class="<%=validReqOpt%>" value="<%=uF.showData((String)request.getAttribute("empContactno"), "") %>"/></td>
                        </tr>
                        <tr>
                            <% 	List<String> candiMobileNoValidList = hmValidationFields.get("CANDI_MOBILE_NO"); 
                                validReqOpt = "";
                                validAsterix = "";
                                if(candiMobileNoValidList != null && uF.parseToBoolean(candiMobileNoValidList.get(0))) {
                                	validReqOpt = "validateRequired";
                                	validAsterix = "<sup>*</sup>";
                                }
                                %>
                            <td class="txtlabel alignRight">Mobile Number:<%=validAsterix %></td>
                            <td><input type="text" name="empMobileNo" onkeypress="return isOnlyNumberKey(event)" class="<%=validReqOpt%>" value="<%=uF.showData((String)request.getAttribute("empMobileNo"),"") %>"/></td>
                        </tr>
                        <%-- 	
                            <tr><td class="txtlabel alignRight">Emergency Contact Name<sup>*</sup>:</td><td><s:textfield cssClass="validateRequired text-input" name="empEmergencyContactName" />
                            <tr><td colspan=2><s:fielderror ><s:param>empEmergencyContactNo</s:param></s:fielderror></td></tr>
                            <tr><td class="txtlabel alignRight">Emergency Contact Number<sup>*</sup>:</td><td><s:textfield name="empEmergencyContactNo" cssClass="validateRequired text-input"/></td></tr>
                            
                            <tr><td class="txtlabel alignRight">PAN<sup>*</sup>:</td><td><s:textfield name="empPanNo" cssClass="validateRequired text-input" /><span class="hint">Candidate's PAN (optional but recommended)<span class="hint-pointer">&nbsp;</span></span></td></tr>
                            <tr><td class="txtlabel alignRight">Provident Fund No :</td><td><s:textfield name="empPFNo" /><span class="hint">Candidate's Provident Number (optional but recommended)<span class="hint-pointer">&nbsp;</span></span></td></tr>
                            <tr><td class="txtlabel alignRight">GPF Acc No :</td><td><s:textfield name="empGPFNo" /><span class="hint">Candidate's GPF Number <span class="hint-pointer">&nbsp;</span></span></td></tr>
                            --%>	
                        <tr>
                            <% 	List<String> candiPassportNoValidList = hmValidationFields.get("CANDI_PASSPORT_NO"); 
                                validReqOpt = "";
                                validAsterix = "";
                                if(candiPassportNoValidList != null && uF.parseToBoolean(candiPassportNoValidList.get(0))) {
                                	validReqOpt = "validateRequired";
                                	validAsterix = "<sup>*</sup>";
                                }
                                %>
                            <td class="txtlabel alignRight">Passport Number:<%=validAsterix %></td>
                            <td><input type="text" name="empPassportNo" class="<%=validReqOpt%>" value="<%=uF.showData((String)request.getAttribute("empPassportNo"),"") %>"/></td>
                        </tr>
                        <tr>
                            <% 	List<String> candiPassportExpiryDateValidList = hmValidationFields.get("CANDI_PASSPORT_EXPIRY_DATE"); 
                                validReqOpt = "";
                                validAsterix = "";
                                if(candiPassportExpiryDateValidList != null && uF.parseToBoolean(candiPassportExpiryDateValidList.get(0))) {
                                	validReqOpt = "validateRequired";
                                	validAsterix = "<sup>*</sup>";
                                }
                                %>
                            <td class="txtlabel alignRight">Passport Expiry Date:<%=validAsterix %></td>
                            <td><input type="text" name="empPassportExpiryDate" class="<%=validReqOpt%>" value="<%=uF.showData((String)request.getAttribute("empPassportExpiryDate"),"") %>"/>
                        <tr>
                            <% 	List<String> candiBloodGroupValidList = hmValidationFields.get("CANDI_BLOOD_GROUP"); 
                                validReqOpt = "";
                                validAsterix = "";
                                if(candiBloodGroupValidList != null && uF.parseToBoolean(candiBloodGroupValidList.get(0))) {
                                	validReqOpt = "validateRequired";
                                	validAsterix = "<sup>*</sup>";
                                }
                                %>
                            <td class="txtlabel alignRight">Blood Group:<%=validAsterix %></td>
                            <td>
                                <% if(candiBloodGroupValidList != null && uF.parseToBoolean(candiBloodGroupValidList.get(0))) { %>
                                <s:select theme="simple" name="empBloodGroup" cssClass="validateRequired" listKey="bloodGroupId" listValue="bloodGroupName" headerKey="" 
                                    headerValue="Select Blood Group" list="bloodGroupList" key="" required="true" />
                                <% } else { %>
                                <s:select theme="simple" name="empBloodGroup" listKey="bloodGroupId" listValue="bloodGroupName" headerKey="" 
                                    headerValue="Select Blood Group" list="bloodGroupList" key="" required="true" />
                                <% } %>	
                            </td>
                        </tr>
                        <tr>
                            <% 	List<String> candiDateOfBirthValidList = hmValidationFields.get("CANDI_DATE_OF_BIRTH"); 
                                validReqOpt = "";
                                validAsterix = "";
                                if(candiDateOfBirthValidList != null && uF.parseToBoolean(candiDateOfBirthValidList.get(0))) {
                                	validReqOpt = "validateRequired";
                                	validAsterix = "<sup>*</sup>";
                                }
                                %>
                            <td class="txtlabel alignRight">Date Of Birth:<%=validAsterix %></td>
                            <td><input type="text" name="empDateOfBirth" id="empDateOfBirth" class="<%=validReqOpt%>" value="<%=uF.showData((String)request.getAttribute("empDateOfBirth"), "") %>"/></td>
                        </tr>
                        <tr>
                            <% 	List<String> candiGenderValidList = hmValidationFields.get("CANDI_GENDER"); 
                                validReqOpt = "";
                                validAsterix = "";
                                if(candiGenderValidList != null && uF.parseToBoolean(candiGenderValidList.get(0))) {
                                	validReqOpt = "validateRequired";
                                	validAsterix = "<sup>*</sup>";
                                }
                                %>
                            <td class="txtlabel alignRight">Gender:<%=validAsterix %></td>
                            <td>
                                <% if(candiGenderValidList != null && uF.parseToBoolean(candiGenderValidList.get(0))) { %>
                                <s:select theme="simple" cssClass="validateRequired  " label="Select Gender" name="empGender" listKey="genderId" 
                                    listValue="genderName" headerKey="" headerValue="Select Gender" list="empGenderList" key="" required="true" />
                                <% } else { %>
                                <s:select theme="simple" label="Select Gender" name="empGender" listKey="genderId" listValue="genderName" headerKey="" 
                                    headerValue="Select Gender" list="empGenderList" key="" required="true" />
                                <% } %>	
                            </td>
                        </tr>
                        <tr>
                            <% 	List<String> candiMaritalStatusValidList = hmValidationFields.get("CANDI_MARITAL_STATUS"); 
                                validReqOpt = "";
                                validAsterix = "";
                                if(candiMaritalStatusValidList != null && uF.parseToBoolean(candiMaritalStatusValidList.get(0))) {
                                	validReqOpt = "validateRequired";
                                	validAsterix = "<sup>*</sup>";
                                }
                                %>
                            <td class="txtlabel alignRight">Marital Status:<%=validAsterix %></td>
                            <td>
                                <% if(candiMaritalStatusValidList != null && uF.parseToBoolean(candiMaritalStatusValidList.get(0))) { %>
                                <s:select theme="simple" name="empMaritalStatus" cssClass="validateRequired  " listKey="maritalStatusId" listValue="maritalStatusName" 
                                    headerKey="0" headerValue="Select Marital Status" list="maritalStatusList" key="" required="true" onchange="showMarriageDate();"/>
                                <% } else { %>
                                <s:select theme="simple" name="empMaritalStatus" listKey="maritalStatusId" listValue="maritalStatusName" headerKey="0" 
                                    headerValue="Select Marital Status" list="maritalStatusList" key="" required="true" onchange="showMarriageDate();" />
                                <% } %>
                            </td>
                        </tr>
                        <tr id="trMarriageDate">
                            <% 	List<String> candiDateOfMarriageValidList = hmValidationFields.get("EMP_DATE_OF_MARRIAGE"); 
                                validReqOpt = "";
                                validAsterix = "";
                                if(candiDateOfMarriageValidList != null && uF.parseToBoolean(candiDateOfMarriageValidList.get(0))) {
                                	validReqOpt = "validateRequired";
                                	validAsterix = "<sup>*</sup>";
                                }
                                %>
                            <td class="txtlabel alignRight">Date Of Marriage:<%=validAsterix %></td>
                            <td><input type="text" name="empDateOfMarriage" id="empDateOfMarriage" class="<%=validReqOpt%>" value="<%=uF.showData((String)request.getAttribute("empDateOfMarriage"), "") %>"/></td>
                        </tr>
                        <tr>
                            <td colspan=2 style="border-bottom:1px solid #346897; font-size: 14px;">Employement Status & Expectations: </td>
                        </tr>
                        <tr>
                            <td class="txtlabel alignRight" valign="top">Availability:</td>
                            <td>
                                <s:radio name="availability" id="availability" list="#{'1':'Yes','0':'No'}" value="{strAvailability}" />
                            </td>
                        </tr>
                        <tr>
                            <td class="txtlabel alignRight" valign="top">Source:<sup>*</sup></td>
                            <td>
                                <s:select theme="simple" name="candiSource" cssClass="validateRequired  " listKey="sourceId" listValue="sourceName"
                                    headerValue="Select Source" list="sourceList" key="" required="true" onchange="showSourceData(this.value);" />
                            </td>
                        </tr>
                        <%	
                            String displayRef = "none";
                            String displayOther = "none";
                               String refEmp = (String)request.getAttribute("refEmpCode");
                               String otherSrc = (String)request.getAttribute("otherRefSrc");
                            if(refEmp != null && !refEmp.equals("")) {
                            	displayRef = "table-row";
                            	displayOther ="none";
                            }
                            
                            if(otherSrc != null && !otherSrc.equals("")) {
                            	displayOther = "table-row";
                            	displayRef ="none";
                            }
                            %>
                        <tr id = "tr_ref_details" style="display:<%=displayRef%>;">
                            <td class="txtlabel alignRight">Ref. Employee Code:<sup>*</sup></td>
                            <td>
                                <span style="float: left;">
                                    <s:hidden name="isEmpCode" id="isEmpCode"/>
                                    <s:hidden name="refEmpId" id="refEmpId"/>
                                    <input type="text" name="refEmpCode" id="refEmpCode" class="validateRequired" value="<%=uF.showData(refEmp,"")%>" onchange="checkEmployeeCode(this.value);"/>
                                </span>
                                <span id="empIdMsgSpan" style="width: 30px; float: left;"></span>
                            </td>
                        </tr>
                        <tr id = "tr_other_details" style="display: <%=displayOther%>;">
                            <td class="txtlabel alignRight"><sup>*</sup></td>
                            <td> <input type="text" name="otherRefSrc" id="otherRefSrc" class="validateRequired" value="<%=uF.showData(otherSrc,"")%>"/></td>
                        </tr>
                        <tr>
                            <% 
                                List<String> candiCurrentCTCValidList = hmValidationFields.get("CANDI_CURRENT_CTC"); 
                                validReqOpt = "";
                                validAsterix = "";
                                if(candiCurrentCTCValidList != null && uF.parseToBoolean(candiCurrentCTCValidList.get(0))) {
                                	validReqOpt = "validateRequired";
                                	validAsterix = "<sup>*</sup>";
                                }
                                %>
                            <td class="txtlabel alignRight">Current CTC:<%=validAsterix %></td>
                            <td><input type="text" name="candiCurrCTC" class="<%=validReqOpt%>" onkeypress="return isNumberKey(event)" value="<%=uF.showData((String)request.getAttribute("candiCurrCTC"), "") %>"/></td>
                        </tr>
                        <tr>
                            <% 	List<String> candiExpectedCTCValidList = hmValidationFields.get("CANDI_EXPECTED_CTC"); 
                                validReqOpt = "";
                                validAsterix = "";
                                if(candiExpectedCTCValidList != null && uF.parseToBoolean(candiExpectedCTCValidList.get(0))) {
                                	validReqOpt = "validateRequired";
                                	validAsterix = "<sup>*</sup>";
                                }
                                %>
                            <td class="txtlabel alignRight">Expected CTC:<%=validAsterix %></td>
                            <td><input type="text" name="candiExpectedCTC" class="<%=validReqOpt%>" onkeypress="return isNumberKey(event)" value="<%=uF.showData((String)request.getAttribute("candiExpectedCTC"), "") %>"/></td>
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
                            <td><input type="text" name="candiNoticePeriod" class="<%=validReqOpt%>" onkeypress="return isOnlyNumberKey(event)" value="<%=uF.showData((String)request.getAttribute("candiNoticePeriod"), "") %>"/> days</td>
                        </tr>
                        <tr>
                            <td colspan=2 style="font-size: 14px;">
                                Update Candidate image:
                                <hr style="background-color:#346897;height:1px">
                            </td>
                        </tr>
                        <tr>
                        	<td></td>
                        	<td><table class="table table_no_border">
		                        <tr>
		                            <td>
		                                <%if(docRetriveLocation==null) { %>
		                                <img height="100" width="100" class="lazy img-circle" style="border:1px solid #CCCCCC;" id="profilecontainerimg" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + strImage%>" />
		                                <%} else { %>
		                                <img height="100" width="100" class="lazy img-circle" style="border:1px solid #CCCCCC;" id="profilecontainerimg" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_CANDIDATE+"/"+IConstants.I_IMAGE+"/"+request.getAttribute("CandidateId")+"/"+strImage%>" />
		                                <%} %>
		                                <%-- <img height="100" width="100" id="profilecontainerimg" src="userImages/<%=strImage!=null ? strImage : "avatar_photo.png"%>" /> --%>
		                            </td>
		                        </tr>
		                        <tr>
		                            <% List<String> candiProfilePhotoValidList = hmValidationFields.get("CANDI_PROFILE_PHOTO"); 
		                                validReqOpt = "";
		                                validAsterix = "";
		                                if(candiProfilePhotoValidList != null && uF.parseToBoolean(candiProfilePhotoValidList.get(0))) {
		                                	validReqOpt = "validateRequired";
		                                	validAsterix = "<sup>*</sup>";
		                                }
		                                %>
		                            <td>
		                                <% if(candiProfilePhotoValidList != null && uF.parseToBoolean(candiProfilePhotoValidList.get(0))) { %>
		                                <s:file accept=".gif,.jpg,.png,.tif,.svg,.svgz"  name="empImage" id="empImage" cssClass="validateRequired" onchange="readImageURL(this, 'profilecontainerimg');"/>
		                                <% } else { %>
		                                <s:file accept=".gif,.jpg,.png,.tif,.svg,.svgz" name="empImage" id="empImage" onchange="readImageURL(this, 'profilecontainerimg');"/>
		                                <% } %>	
		                                <span style="color: #999999;font-size: 12px;">Image size must be smaller than or equal to 500kb.</span>
		                            </td>
		                        </tr>
		                    </table></td>
                        </tr>
                        <tr>
                        	<td></td>
                        	<td>
                        	   <% if(operation != null && operation.equals("U")) {%>
                        	   		<s:submit cssClass="btn btn-primary" name="stepSubmit" value="Update" align="center" />
                        	   <% } else { %>
	                        		<s:if test="mode==null">
	                        			 <s:submit cssClass="btn btn-primary" name="stepSubmit" value="Submit & Proceed" align="center" />
			                        </s:if>
			                        <s:else>
			                            <s:submit cssClass="btn btn-primary" name="stepSubmit" value="Submit & Proceed" align="center" />
			                        </s:else>
		                       <%} %>
                        	</td>
                        </tr>
                    </table>
                </div>
            </s:form>
        </div>
    </s:if>
    <%-- <script type="text/javascript">
    $(document).ready(function(){
    	alert("value"+$('input[name=degreeName]').val());
    	//$('input[name=degreeName]').onchange(function(){ 
    	$('#degreeName').onChange(function(){
    		alert("degreeName");
    		document.getElementById("degreeCertificate0").className = 'validateRequired';
    	});
    	
    });
    </script> --%>
    <s:if test="step==2 || mode=='report'">
        <div class="tab-pane active" role="tabpanel" id="bi">
            <s:form theme="simple" action="AddCandidate" id="frmBackgroundInfo" method="POST" cssClass="formcss" enctype="multipart/form-data">
                <s:hidden name="operation" />
                <s:hidden name="recruitId" />
                <s:hidden name="CandidateId" />
                <s:hidden name="mode" />
                <s:hidden name="step" />
                <s:hidden name="show" />
                <s:hidden name="fromPage" id="fromPage" />
                <div><span style="color:#68AC3B; font-size:18px;font-weight: 600;">Step 2 : </span><span class="tdLabelheadingBg" style="font-weight: 600;font-size: 16px;">Enter Candidates background information</span> </div>
                <div class="row row_without_margin">
                    <div class="col-lg-6 col-md-6 col-sm-12 col_no_padding">
                        <div  id="div_skills">
                            <h4 style="margin:5px 0px 10px 0px;padding: 5px;background: aliceblue;">Enter Candidate skills and their values</h4>
                            <% List<String> candiSkillNameValidList = hmValidationFields.get("CANDI_SKILL_NAME"); 
                                validReqOpt = "";
                                validAsterix = "";
                                if(candiSkillNameValidList != null && uF.parseToBoolean(candiSkillNameValidList.get(0))) {
                                	validReqOpt = "validateRequired";
                                	validAsterix = "<sup>*</sup>";
                                }
                                %>
                            <% 
                                String validReqOpt1 = "";
                                String validAsterix1 = "";
                                List<String> candiSkillRatingValidList = hmValidationFields.get("CANDI_SKILL_RATING"); 
                                if(candiSkillRatingValidList != null && uF.parseToBoolean(candiSkillRatingValidList.get(0))) {
                                	validReqOpt1 = "validateRequired";
                                	validAsterix1 = "<sup>*</sup>";
                                }
                                %>
                            
                            
                            <div>
                            	<table class="table table-head-highlight">
									<tbody id="table-skills">
										<tr>
											<th></th>
											<th>Skill Name</th>
											<th>Skill Rating</th>
											<th style="width:10%"></th>
										</tr>
										<% 	
		                                if(alSkills!=null && alSkills.size()!=0){
		                                	String empId = (String)((ArrayList)alSkills.get(0)).get(3);
		                                
		                                %>
		                            <% 
		                                for(int i=0; i<alSkills.size(); i++) {
		                                %>
										<tr id="row_skill<%=i%>" class="row_skill">
											<td>
												<%if(i==0){ %>
	                                            	[PRI]
	                                            <%}%>
											</td>
											<td>
												<select name="skillName" class="<%=validReqOpt%>">
	                                                <%for(int k=0; k< skillsList.size(); k++) { 
	                                                    if( (((FillSkills)skillsList.get(k)).getSkillsId()+"").equals((String)((ArrayList)alSkills.get(i)).get(0))) {
	                                                    %>
	                                                <option value="<%=((FillSkills)skillsList.get(k)).getSkillsId() %>" selected="selected">
	                                                    <%=((FillSkills)skillsList.get(k)).getSkillsName() %>
	                                                </option>
	                                                <%}else { %>
	                                                <option value="<%=((FillSkills)skillsList.get(k)).getSkillsId() %>">
	                                                    <%=((FillSkills)skillsList.get(k)).getSkillsName() %>
	                                                </option>
	                                                <% }
	                                                    }%>
	                                            </select>
											</td>
											<td>
												<select name="skillValue" class="<%=validReqOpt1 %>" style="width: 105px !important;">
	                                                <%for(int k=1; k< 11; k++) { 
	                                                    if( (k+"").equals(((String)((ArrayList)alSkills.get(i)).get(2)))) {
	                                                    %>
	                                                <option value="<%=k%>" selected="selected">
	                                                    <%=k%>
	                                                </option>
	                                                <%}else {%>
	                                                <option value="<%=k%>">
	                                                    <%=k%>
	                                                </option>
	                                                <% }
	                                                    }%>
	                                            </select>
											</td>
											<td>
												<a href="javascript:void(0)" onclick="addSkills()" class="add-font"></a>
		                                        <%if(i>0){ %>
		                                        <a href="javascript:void(0)" onclick="removeSkills(this.id)" id=<%=i%> class="remove-font" ></a>
		                                        <% } %>
											</td>
										</tr>
										<%}
		                                } else {
		                                %>
		                                	<tr class="row_skill">
												<td>[Pri]</td>
												<td>
													<select name="skillName" class="<%=validReqOpt%>">
		                                                <option value="">Select Skill Name</option>
		                                                <%for(int k=0; k< skillsList.size(); k++) {%> 
		                                                <option value="<%=((FillSkills)skillsList.get(k)).getSkillsId() %>">
		                                                    <%=((FillSkills)skillsList.get(k)).getSkillsName() %>
		                                                </option>
		                                                <%}%>
		                                            </select>
												</td>
												<td>
													<select name="skillValue" class="<%=validReqOpt1 %>" style="width: 105px !important;">
		                                                <option value="">Skill Rating</option>
		                                                <%for(int k=1; k< 11; k++) {%>
		                                                <option value="<%=k%>">
		                                                    <%=k%>
		                                                </option>
		                                                <%}%>
		                                            </select>
												</td>
												<td><a href="javascript:void(0)" onclick="addSkills()" class="add-font"></a></td>
											</tr>
		                                <%} %>
									</tbody>
								</table>
                            </div>
                        </div>
                    </div>
                    <div class="col-lg-6 col-md-6 col-sm-12 col_no_padding">
                        <div id="div_education">
                            <h4 style="padding: 5px;background: aliceblue;margin:5px 0px 10px 0px">Enter Candidate educational details</h4>
                            <% List<String> candiDegreeNameValidList = hmValidationFields.get("CANDI_DEGREE_NAME"); 
                                validReqOpt = "";
                                validAsterix = "";
                                if(candiDegreeNameValidList != null && uF.parseToBoolean(candiDegreeNameValidList.get(0))) {
                                	validReqOpt = "validateRequired";
                                	validAsterix = "<sup>*</sup>";
                                }
                                %>
                            <% 
                                String validReqOptDD = "";
                                String validAsterixDD = "";
                                List<String> candiDegreeDurationValidList = hmValidationFields.get("CANDI_DEGREE_DURATION"); 
                                if(candiDegreeDurationValidList != null && uF.parseToBoolean(candiDegreeDurationValidList.get(0))) {
                                	validReqOptDD = "validateRequired";
                                	validAsterixDD = "<sup>*</sup>";
                                }
                                %>
                            <% 
                                String validReqOptDCY = "";
                                String validAsterixDCY = "";
                                List<String> candiDegreeCompYearValidList = hmValidationFields.get("CANDI_DEGREE_COMPLETION_YEAR"); 
                                if(candiDegreeCompYearValidList != null && uF.parseToBoolean(candiDegreeCompYearValidList.get(0))) {
                                	validReqOptDCY = "validateRequired";
                                	validAsterixDCY = "<sup>*</sup>";
                                }
                                %>
                            <% 
                                String validReqOptDG = "";
                                String validAsterixDG = "";
                                List<String> candiDegreeGradeValidList = hmValidationFields.get("CANDI_DEGREE_GRADE"); 
                                if(candiDegreeGradeValidList != null && uF.parseToBoolean(candiDegreeGradeValidList.get(0))) {
                                	validReqOptDG = "validateRequired";
                                	validAsterixDG = "<sup>*</sup>";
                                }
                                %>
                            <!-- start parvez date: 04-09-2021 -->
                            <% 
                                String validReqOptNOI = "";
                                String validAsterixDNOI = "";
                                
                                List<String> candiInstituteValidList = hmValidationFields.get("CANDI_NAME_OF_INSTITUTE"); 
                        		
                        		if(candiInstituteValidList != null && uF.parseToBoolean(candiInstituteValidList.get(0))) {
                        			validReqOptNOI = "validateRequired";
                        			validAsterixDNOI = "<sup>*</sup>";
                        		}
                                %>
                            <% 
                                String validReqOptB = "";
                                String validAsterixB = "";
                                
                                List<String> candiBoardValidList = hmValidationFields.get("CANDI_DEGREE_BOARD"); 
                        		
                        		if(candiBoardValidList != null && uF.parseToBoolean(candiBoardValidList.get(0))) {
                        			validReqOptB = "validateRequired";
                        			validAsterixB = "<sup>*</sup>";
                        		}
                                %>
                            <% 
                                String validReqOptSubject = "";
                                String validAsterixSubject = "";
                                
                                List<String> candiSubjectValidList = hmValidationFields.get("CANDI_DEGREE_SUBJECT"); 
                        		
                        		if(candiSubjectValidList != null && uF.parseToBoolean(candiSubjectValidList.get(0))) {
                        			validReqOptSubject = "validateRequired";
                        			validAsterixSubject = "<sup>*</sup>";
                        		}
                                %>
                            <% 
                                String validReqOptDSDate = "";
                                String validAsterixDSDate = "";
                                
                                List<String> candiSDateValidList = hmValidationFields.get("CANDI_DEGREE_START_DATE"); 
                        		
                        		if(candiSDateValidList != null && uF.parseToBoolean(candiSDateValidList.get(0))) {
                        			validReqOptDSDate = "validateRequired";
                        			validAsterixDSDate = "<sup>*</sup>";
                        		}
                                %>
                            <% 
                                String validReqOptDCDate = "";
                                String validAsterixDCDate = "";
                                
                                List<String> candiCDateValidList = hmValidationFields.get("CANDI_DEGREE_COMPLETION_DATE"); 
                        		
                        		if(candiCDateValidList != null && uF.parseToBoolean(candiCDateValidList.get(0))) {
                        			validReqOptDCDate = "validateRequired";
                        			validAsterixDCDate = "<sup>*</sup>";
                        		}
                                %>
                            <% 
                                String validReqOptDM = "";
                                String validAsterixDM = "";
                                
                                List<String> candiDegreeMarksValidList = hmValidationFields.get("CANDI_DEGREE_MARKS"); 
                        		
                        		if(candiDegreeMarksValidList != null && uF.parseToBoolean(candiDegreeMarksValidList.get(0))) {
                        			validReqOptDM = "validateRequired";
                        			validAsterixDM = "<sup>*</sup>";
                        		}
                                %>
                                
                            <% 
                                String validReqOptCity = "";
                                String validAsterixCity = "";
                                
                                List<String> candiCityValidList = hmValidationFields.get("CANDI_DEGREE_CITY"); 
                        		
                        		if(candiCityValidList != null && uF.parseToBoolean(candiCityValidList.get(0))) {
                        			validReqOptCity = "validateRequired";
                        			validAsterixCity = "<sup>*</sup>";
                        		}
                                %>
                            <% 
                                String validReqOptDCertificate = "";
                                String validAsterixDCertificate = "";
                                
                                List<String> candiDegreeCertificateValidList = hmValidationFields.get("CANDI_EDUCATION_CERTIFICATE"); 
                        		
                        		if(candiDegreeCertificateValidList != null && uF.parseToBoolean(candiDegreeCertificateValidList.get(0))) {
                        			validReqOptDCertificate = "validateRequired";
                        			validAsterixDCertificate = "<sup>*</sup>";
                        		}
                                %>
                          <!-- end parvez date: 04-09-2021 -->
                          
                            <table class="table table-head-highlight">
								<tbody id="table-education">
										<tr>
										<!-- start parvez date: 04-09-2021 -->
											<th>Degree Name<%=validAsterix %></th>
											<th>Duration<%=validAsterixDD %></th>
											<th>Completion Year<%=validAsterixDCY %></th>
											<th>Grade / Percentage<%=validAsterixDG %></th>
											<th>Name of Institute<%=validAsterixDNOI %></th>
							                <th>Board<%=validAsterixB %></th>
							                <th>Subject<%=validReqOptSubject %></th>
							                <th>Start Date<%=validAsterixDSDate %></th>
							                <th>Completion Date<%=validAsterixDCDate %></th>
							                <th>Marks / CGPA<%=validAsterixDM %></th>
							                <th>City<%=validAsterixCity %></th>
											<th>Certificate<%=validAsterixDCertificate %></th>
											<th></th>
										<!-- end parvez date: 04-09-2021 -->
										</tr>
										 <% 	
		                                if(alEducation!=null && alEducation.size()!=0){
		                                	for(int i=0; i<alEducation.size(); i++) {
		                                %>
		                                	<tr id="row_education<%=i%>" class="row_education">
		                                		<td>
		                                		
		                                		<!-- ===start parvez date: 14-09-2021=== -->
		                                			<input type="hidden" name="degreeId" value="<%=((ArrayList)alEducation.get(i)).get(12) %>"/>
		                                			<select name="degreeName" id="degreeName" class="<%=validReqOpt%>" style="width: 120px !important;" onchange="validateDegreeCerti(<%=i%>,'this.value');">
		                                        <!-- ===end parvez date: 14-09-2021=== -->
		                                                <%for(int k=0; k< educationalList.size(); k++) { 
		                                                    if( (((FillEducational)educationalList.get(k)).getEduId()+"").equals( (String)((ArrayList)alEducation.get(i)).get(0) )) {
		                                                    %>
		                                                <option value="<%=((FillEducational)educationalList.get(k)).getEduId() %>" selected="selected">
		                                                    <%=((FillEducational)educationalList.get(k)).getEduName() %>
		                                                </option>
		                                                <%}else { %>
		                                                <option value="<%=((FillEducational)educationalList.get(k)).getEduId() %>" >
		                                                    <%=((FillEducational)educationalList.get(k)).getEduName() %>
		                                                </option>
		                                                <% }
		                                                    }%>
		                                            </select>
		                                		</td>
		                                		<td>
		                                			<select name="degreeDuration" class="<%=validReqOptDD %>" style="width: 90px !important;">
		                                                <%for(int k=0; k< degreeDurationList.size(); k++) { 
		                                                    if( (((FillDegreeDuration)degreeDurationList.get(k)).getDegreeDurationID()+"").equals( (String)((ArrayList)alEducation.get(i)).get(2) )) {
		                                                    %>
		                                                <option value="<%=((FillDegreeDuration)degreeDurationList.get(k)).getDegreeDurationID() %>" selected="selected">
		                                                    <%=((FillDegreeDuration)degreeDurationList.get(k)).getDegreeDurationName() %>
		                                                </option>
		                                                <%}else { %>
		                                                <option value="<%=((FillDegreeDuration)degreeDurationList.get(k)).getDegreeDurationID() %>">
		                                                    <%=((FillDegreeDuration)degreeDurationList.get(k)).getDegreeDurationName() %>
		                                                </option>
		                                                <% }
		                                                    }%>
		                                            </select>
		                                		</td>
		                                		<td>
		                                			 <select name="completionYear" class="<%=validReqOptDCY %>" style="width: 130px !important;">
		                                                <%for(int k=0; k< yearsList.size(); k++) { 
		                                                    if( (((FillYears)yearsList.get(k)).getYearsID()+"").equals( (String)((ArrayList)alEducation.get(i)).get(3) )) {
		                                                    %>
		                                                <option value="<%=((FillYears)yearsList.get(k)).getYearsID() %>" selected="selected">
		                                                    <%=((FillYears)yearsList.get(k)).getYearsName() %>
		                                                </option>
		                                                <%}else { %>
		                                                <option value="<%=((FillYears)yearsList.get(k)).getYearsID() %>">
		                                                    <%=((FillYears)yearsList.get(k)).getYearsName() %>
		                                                </option>
		                                                <% }
		                                                    }%>
		                                            </select>
		                                		</td>
		                                		<td>
		                                			<input type="text" class="<%=validReqOptDG %>  " style=" width: 90px !important;" name="grade" value="<%=((ArrayList)alEducation.get(i)).get(4)%>" ></input>
		                                		</td>
		                                		
		                                <!-- start parvez date: 04-09-2021 -->
		                                		<!-- Start By Dattatray Date:23-08-21 -->
		                                		<td>
		                                			<input type="text"   style=" width: 90px !important;" name="instituteName"  id="instituteName" value="<%=((ArrayList)alEducation.get(i)).get(6)%>" class="<%=validReqOptNOI %>" ></input>
		                                		</td>
		                                		<td>
		                                			<input type="text"    style=" width: 90px !important;" name="universityName"  id="universityName" value="<%=((ArrayList)alEducation.get(i)).get(7)%>" class="<%=validReqOptB %>" ></input>
		                                		</td>
		                                		<td>
		                                			<input type="text"  style=" width: 90px !important;" name="subject"  id="subject" value="<%=((ArrayList)alEducation.get(i)).get(5)%>" class="<%=validReqOptSubject %>" ></input>
		                                		</td>
		                                		<td>
		                                			<input type="text" style=" width: 90px !important;" name="strStartDate"  id="strStartDate" value="<%=((ArrayList)alEducation.get(i)).get(8)%>" class="<%=validReqOptDSDate%>" ></input>
		                                		</td>
		                                		<td>
		                                			<input type="text" style=" width: 90px !important;" name="strCompletionDate" id="strCompletionDate" value="<%=((ArrayList)alEducation.get(i)).get(9)%>" class="<%=validReqOptDCDate%>" ></input>
		                                		</td>
		                                		<td>
		                                			<input type="text"  style=" width: 90px !important;" name="marks" id="marks" value="<%=((ArrayList)alEducation.get(i)).get(10)%>" onkeypress="return isNumberKey(event)" class="<%=validReqOptDM%>" ></input>
		                                		</td>
		                                		<td>
		                                			<input type="text" style=" width: 90px !important;" name="city1" id="city1"   value="<%=((ArrayList)alEducation.get(i)).get(11)%>" class="<%=validReqOptCity%>" ></input>
		                                		</td>
		                                		<!-- End By Dattatray Date:23-08-21 -->
		                                		
		                                		<td><input type="hidden" name="degreeCertiStatus" id="degreeCertiStatus<%=i %>" value="0">
													<div id="degreeCertiDiv<%=i %>"><input type="hidden" name="degreeCertiSubDivCnt<%=i %>" id="degreeCertiSubDivCnt<%=i %>" value="0" />
														<div id="degreeCertiSubDiv<%=i %>_0">
														<!-- ===start parvez date: 09-09-2021=== -->
															<div id="edufile<%=i %>"></div>
															<input type="file" accept=".jpeg,.jpg,.png,.tif,.svg,.svgz,.doc,.docs,.docx,.pdf" name="degreeCertificate<%=i %>" 
																id="degreeCertificate<%=i %>"  onchange="fillFileStatus1('degreeCertiStatus<%=i %>','degreeCertificate<%=i %>'); readFileURL(this, 'edufile<%=i %>');" class="<%=validReqOptDCertificate%>" />
														<!-- ===end parvez date: 09-09-2021=== -->
															<a href="javascript:void(0)" onclick="addEducationCerti('<%=i %>');" class="add-font"></a>
														</div>
													</div>
													
													<!-- ===start parvez date: 18-09-2021=== -->
							                                        
							                         <% if(hmEducationDocs != null && hmEducationDocs.size()>0) {
						                                   List<String> innrList = hmEducationDocs.get(((List) alEducation.get(i)).get(12));
						                                   for(int l=0; innrList != null && l<innrList.size(); l++) {
						                             %>
																
																	<%if(docRetriveLocation == null) { %>
																		<a
																			href="<%=request.getContextPath()+ IConstants.DOCUMENT_LOCATION + innrList.get(l)  %>"
																				title="Education Document"><i class="fa fa-file-o"
																				aria-hidden="true"></i>
																		</a>
																	<% } else { %>
																	
																			<a
																				href="<%=docRetriveLocation +IConstants.I_CANDIDATE+"/"+IConstants.I_DOCUMENT+"/"+IConstants.I_EDUCATION_DOC+"/"+(String)request.getAttribute("CandidateId")+"/"+ innrList.get(l) %>"
																				title="Education Document"><i class="fa fa-file-o"
																				aria-hidden="true"></i>
																			</a>
																			<% } %>
																 <% } %> <% }  %>
							                                       
							                        <!-- ===end parvez date: 18-09-2021=== -->
													
												</td>
		                                		<td>
		                                			<a href="javascript:void(0)" onclick="addEducation()" class="add-font"></a>
			                                        <%if(i>0){ %>
			                                        <a href="javascript:void(0)" onclick="removeEducation(this.id)" id=<%=i%> class="remove-font" ></a>
			                                        <% } %>
		                                		</td>
		                                <!-- end parvez date: 04-09-2021 -->
		                                	</tr>
		                                <% }%>
		                                <div><input type="hidden" name="educationCnt" id="educationCnt" value="<%=alEducation.size()-1 %>" /></div>
		                                <% }else { %> 
		                                	<tr id="row_education" class="row_education">
		                                		<td>
		                                			<input type="hidden" name="educationCnt" id="educationCnt" value="0"/>
		                                			<select name="degreeName" class="<%=validReqOpt%>" onchange="checkEducation(this.value,0);" id="degreeName" style="width: 120px !important;" >
		                                                <option value="">Degree</option>
		                                                <%for(int k=0; k< educationalList.size(); k++) {%> 
		                                                <option value="<%=((FillEducational)educationalList.get(k)).getEduId() %>" >
		                                                    <%=((FillEducational)educationalList.get(k)).getEduName() %>
		                                                </option>
		                                                <%} %>
		                                                <option value="other">Other</option>
		                                            </select>
		                                		</td>
		                                		<td>
		                                			<% if(candiDegreeDurationValidList != null && uF.parseToBoolean(candiDegreeDurationValidList.get(0))) { %>
		                                            <s:select name="degreeDuration"	cssClass="validateRequired  " cssStyle="width: 90px !important;" listKey="degreeDurationID" listValue="degreeDurationName" 
		                                                headerKey="" headerValue="Duration" list="degreeDurationList" key="" required="true" />
		                                            <% } else { %>
		                                            <s:select name="degreeDuration"	cssStyle="width: 90px !important;" listKey="degreeDurationID" listValue="degreeDurationName" headerKey=""
		                                                headerValue="Duration" list="degreeDurationList" key="" required="true"  />
		                                            <% } %>
		                                		</td>
		                                		<td>
		                                			<% if(candiDegreeCompYearValidList != null && uF.parseToBoolean(candiDegreeCompYearValidList.get(0))) { %>
		                                            <s:select name="completionYear"	cssClass="validateRequired" cssStyle="width: 130px !important" listKey="yearsID" 
		                                            	listValue="yearsName" headerKey="" headerValue="Completion Year" list="yearsList" key="" required="true" />
		                                            <% } else { %>
		                                            <s:select name="completionYear"	cssStyle="width: 130px !important" listKey="yearsID" listValue="yearsName" 
		                                            	headerKey="" headerValue="Completion Year" list="yearsList" key="" required="true"/>
		                                            <% } %>
		                                		</td>
		                                		<td>
		                                			<input type="text" class="<%=validReqOptDG %>" style="width: 90px !important;" name="grade" />
		                                		</td>
		                                	<!-- Start Parvez Date:04-09-2021 -->
		                                		<td>
		                                			<input type="text" class="<%=validReqOptNOI %>"  style=" width: 90px !important;" name="instituteName"  id="instituteName" ></input>
		                                		</td>
		                                		<td>
		                                			<input type="text" class="<%=validReqOptB %>" style=" width: 90px !important;" name="universityName"  id="universityName" ></input>
		                                		</td>
		                                		<td>
		                                			<input type="text" class="<%=validReqOptSubject %>" style=" width: 90px !important;" name="subject"  id="subject" ></input>
		                                		</td>
		                                		<td>
		                                			<input type="text" class="<%=validReqOptDSDate%>" style=" width: 90px !important;" name="strStartDate"  id="strStartDate" ></input>
		                                		</td>
		                                		<td>
		                                			<input type="text" class="<%=validReqOptDCDate%>" style=" width: 90px !important;" name="strCompletionDate" id="strCompletionDate" ></input>
		                                		</td>
		                                		<td>
		                                			<input type="text" class="<%=validReqOptDM%>" style=" width: 90px !important;" name="marks" id="marks" onkeypress="return isNumberKey(event)"></input>
		                                		</td>
		                                		<td>
		                                			<input type="text" class="<%=validReqOptCity%>" style=" width: 90px !important;" name="city1" id="city1" ></input>
		                                		</td>
		                                		<td><input type="hidden" name="degreeCertiStatus" id="degreeCertiStatus0" value="0">
													<div id="degreeCertiDiv0"><input type="hidden" name="degreeCertiSubDivCnt0" id="degreeCertiSubDivCnt0" value="0" />
														<div id="degreeCertiSubDiv0_0">
														<!-- ===start parvez date: 09-09-2021=== -->
															<div id="edufile0"></div>
															<input type="file" accept=".jpeg,.jpg,.png,.tif,.svg,.svgz,.doc,.docs,.docx,.pdf" name="degreeCertificate0" id="degreeCertificate0" onchange="fillFileStatus1('degreeCertiStatus0','degreeCertificate0'); readFileURL(this, 'edufile0');" class="<%=validReqOptDCertificate%>" />
														<!-- ===end parvez date: 09-09-2021=== -->
															<a href="javascript:void(0)" onclick="addEducationCerti('0');" class="add-font"></a>
														</div>
													</div>
												</td>
												<td>
		                                			<a href="javascript:void(0)" onclick="addEducation()" class="add-font"></a>
		                                		</td>
		                                <!-- End Parvez Date:04-09-2021 -->
		                                	</tr>
		                                	<tr id="degreeNameOtherTR0" style="display:none;">
		                                        <td style="text-align:right;">Enter Education :</td>
		                                        <td colspan="3"><input type="text" name="degreeNameOther" class="<%=validReqOptDG %>"></td>
		                                    </tr>
		                                <%} %>
								</tbody>
							</table>
                        </div>
                    </div>
                </div>
                <div class="row row_without_margin">
                    <div class="col-lg-6 col-md-6 col-sm-12 col_no_padding">
                        <div id="div_language">
                            <h4 style="padding: 5px;background: aliceblue;margin:5px 0px 10px 0px">Enter Candidate languages</h4>
                            <% List<String> candiLanguageNameValidList = hmValidationFields.get("CANDI_LANGUAGE_NAME"); 
                                validReqOpt = "";
                                validAsterix = "";
                                if(candiLanguageNameValidList != null && uF.parseToBoolean(candiLanguageNameValidList.get(0))) {
                                	validReqOpt = "validateRequired";
                                	validAsterix = "<sup>*</sup>";
                                }
                                %>
                            <table class="table table-head-highlight">
								<tbody id="table-language">
										<tr id="row_language" class="row_language">
											<th>Language Name<%=validAsterix %></th>
											<th>Read</th>
											<th>Write</th>
											<th>Speak</th>
											<th></th>
										</tr>
										<% 	
		                                if(alLanguages!=null && alLanguages.size()!=0){
		                                	for(int i=0; i<alLanguages.size(); i++) {
		                                %>
		                                	<tr id="row_language<%=i%>" class="row_language">
		                                		<td><input type="text" class="<%=validReqOpt%>" name="languageName" value="<%=((ArrayList)alLanguages.get(i)).get(1)%>" /></td>
		                                		<td>
		                                			<% if(uF.parseToBoolean( (String)((ArrayList)alLanguages.get(i)).get(2)) ) { %>
		                                            <input type="checkbox" name="isReadcheckbox" value="1" id="isRead_<%=((ArrayList)alLanguages.get(i)).get(0)%>" 
		                                                checked="checked" onchange="showHideHiddenField(this.id)" />
		                                            <input type="hidden" name="isRead" value="1" id="hidden_isRead_<%=((ArrayList)alLanguages.get(i)).get(0)%>" />
		                                            <%}else { %>
		                                            <input type="checkbox" name="isReadcheckbox" value="0" id="isRead_<%=((ArrayList)alLanguages.get(i)).get(0)%>" 
		                                                onchange="showHideHiddenField(this.id)" />
		                                            <input type="hidden" name="isRead" value="0" id="hidden_isRead_<%=((ArrayList)alLanguages.get(i)).get(0)%>" />
		                                            <%} %>
		                                		</td>
		                                		<td>
		                                			<% if(uF.parseToBoolean( (String)((ArrayList)alLanguages.get(i)).get(3)) ) { %>
		                                            <input type="checkbox" name="isWritecheckbox" value="1" checked="checked" id="isWrite_<%=((ArrayList)alLanguages.get(i)).get(0)%>"  
		                                                onchange="showHideHiddenField(this.id)"	/>
		                                            <input type="hidden" name="isWrite" value="1" id="hidden_isWrite_<%=((ArrayList)alLanguages.get(i)).get(0)%>" />
		                                            <%}else { %>
		                                            <input type="checkbox" name="isWritecheckbox" value="0" id="isWrite_<%=((ArrayList)alLanguages.get(i)).get(0)%>" 
		                                                onchange="showHideHiddenField(this.id)"	/>
		                                            <input type="hidden" name="isWrite" value="0" id="hidden_isWrite_<%=((ArrayList)alLanguages.get(i)).get(0)%>" />
		                                            <%} %>
		                                		</td>
		                                		<td>
		                                			<% if(uF.parseToBoolean( (String)((ArrayList)alLanguages.get(i)).get(4)) ) { %>
		                                            <input type="checkbox" name="isSpeakcheckbox" value="1" checked="checked" id="isSpeak_<%=((ArrayList)alLanguages.get(i)).get(0)%>" 
		                                                onchange="showHideHiddenField(this.id)"	/>
		                                            <input type="hidden" name="isSpeak" value="1" id="hidden_isSpeak_<%=((ArrayList)alLanguages.get(i)).get(0)%>" />
		                                            <%}else { %>
		                                            <input type="checkbox" name="isSpeakcheckbox" value="0" id="isSpeak_<%=((ArrayList)alLanguages.get(i)).get(0)%>"
		                                                onchange="showHideHiddenField(this.id)"	/>
		                                            <input type="hidden" name="isSpeak" value="0" id="hidden_isSpeak_<%=((ArrayList)alLanguages.get(i)).get(0)%>" />
		                                            <%} %>
		                                		</td>
		                                		<td>
		                                			<a href="javascript:void(0)" onclick="addLanguages()" class="add-font"></a>
                                        			<%if(i>0){ %>
                                        			<a href="javascript:void(0)" onclick="removeLanguages(this.id)" id=<%=i%> class="remove-font" ></a>
                                        			<%} %>
		                                		</td>
		                                	</tr>
		                                <%}
		                                }else {
		                                %>
		                                	<tr>
		                                		<td><input type="text" class="<%=validReqOpt%>" name="languageName" /></td>
		                                		<td align="center"><input type="checkbox" id="isRead_0" name="isReadcheckbox" value="1" onclick="showHideHiddenField(this.id)"/><input type="hidden" name="isRead" value="0" id="hidden_isRead_0" /></td>
		                                		<td align="center"><input type="checkbox" id="isWrite_0" name="isWritecheckbox" value="1" onclick="showHideHiddenField(this.id)"/><input type="hidden" name="isWrite" value="0" id="hidden_isWrite_0" /></td>
		                                		<td align="center"><input type="checkbox" id="isSpeak_0" name="isSpeakcheckbox" value="1" onclick="showHideHiddenField(this.id)"/><input type="hidden" name="isSpeak" value="0" id="hidden_isSpeak_0" /></td>
		                                		<td align="center"><a href="javascript:void(0)" onclick="addLanguages()" class="add-font"></a></td>
		                                	</tr>
		                                <%}%>
								</tbody>
							</table>
                        </div>
                    </div>
                    
               <% if(!uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_EMPLOYEE_HOBBIES_DISABLE))){ %>     
                    <div class="col-lg-6 col-md-6 col-sm-12 col_no_padding">
                        <div id="div_hobbies">
                            <h4 style="padding: 5px;background: aliceblue;margin:5px 0px 10px 0px">Enter Candidate hobbies</h4>
                            <% List<String> candiHobbyNameValidList = hmValidationFields.get("CANDI_HOBBY_NAME"); 
                                validReqOpt = "";
                                validAsterix = "";
                                if(candiHobbyNameValidList != null && uF.parseToBoolean(candiHobbyNameValidList.get(0))) {
                                	validReqOpt = "validateRequired";
                                	validAsterix = "<sup>*</sup>";
                                }
                                %>
	                            <table class="table table-head-highlight">
									<tbody id="table-hobbies">
											<tr id="row_hobby" class="row_hobby">
												<th>Hobby Name<%=validAsterix %></th>
												<th></th>
											</tr>
											<% 	
			                                if(alHobbies!=null && alHobbies.size()!=0){
			                                	String empId = (String)((ArrayList)alHobbies.get(0)).get(2);
			                                
			                                for(int i=0; i<alHobbies.size(); i++) {
			                                %>
			                                	<tr id="row_hobby<%=((ArrayList)alHobbies.get(i)).get(0)%>" class="row_hobby">
			                                		<td><input type="text" class="<%=validReqOpt%>" name="hobbyName" value="<%=((ArrayList)alHobbies.get(i)).get(1)%>" ></input></td>
			                                		<td>
			                                			<a href="javascript:void(0)" onclick="addHobbies()" class="add-font"></a>
				                                        <%if(i>0){ %>
				                                        <a href="javascript:void(0)" onclick="removeHobbies(this.id)" id=<%=((ArrayList)alHobbies.get(i)).get(0)%> class="remove-font" ></a>
				                                        <% } %>
			                                		</td>
			                                	</tr>
			                                <%}
			                                }else {
			                                %>
			                                	<tr id="row_hobby" class="row_hobby">
			                                		<td><input type="text" class="<%=validReqOpt%>" name="hobbyName" ></input></td>
			                                		<td><a href="javascript:void(0)" onclick="addHobbies()" class="add-font"></a></td>
			                                	</tr>
			                                <% } %>
									</tbody>
								</table>
                        </div>
                    </div>
                <% } %>    
                </div> 
                <div class="clr"></div>
                <div style="float:right;">
                    <table class="table table_no_border">
                       <% if(operation != null && operation.equals("U")) {%>
                       		 <tr>
                                <td colspan="2" align="center">
                                	<s:submit cssClass="btn btn-primary" name="stepSubmit" value="Update" align="center" />
                                </td>
                            </tr>
                     		
                      <%}else { %>
	                        <s:if test="mode==null">
	                            <tr>
	                                <td colspan="2" align="center">
	                                	<button type="button" class="btn btn-default prev-step" onclick="callPrevious(0,'<%=request.getAttribute("CandidateId")%>','<%=(String)request.getAttribute("fromPage")%>')">Previous</button>
	                                    <s:submit cssClass="btn btn-primary" name="stepSubmit"  value="Submit & Proceed" align="center" />
	                                </td>
	                            </tr>
	                        </s:if>
	                        
	                        <s:else>
	                            <tr>
	                                <td colspan="2" align="center">
	                                	<%-- <button type="button" class="btn btn-default prev-step" onclick="callPrevious(0,'<%=request.getAttribute("CandidateId")%>','<%=(String)request.getAttribute("fromPage")%>')">Previous</button> --%>
	                                    <s:submit cssClass="btn btn-primary" name="stepSubmit" value="Submit & Proceed" align="center" />
	                                </td>
	                            </tr>
	                        </s:else>
                        <%} %>
                    </table>
                </div>
            </s:form>
        </div>
    </s:if>
    <s:if test="step==3 || mode=='report'">
        <div class="tab-pane active" role="tabpanel" id="fi">
            <s:form theme="simple" action="AddCandidate" id="frmFamilyInfo" method="POST" cssClass="formcss" enctype="multipart/form-data">
                <div style="height: auto; width: 100%; float: left; border: solid 0px black; overflow: auto;" id="div_id_family">
                    <s:hidden name="show"></s:hidden>
                    <s:hidden name="operation" />
                    <s:hidden name="recruitId" />
                    <s:hidden name="CandidateId" />
                    <s:hidden name="mode" />
                    <s:hidden name="step" />
                    <s:hidden name="fromPage" id="fromPage" />
                    <table border="0" class="table table_no_border">
                        <tr>
                            <td  class="tdLabelheadingBg alignCenter" colspan="2"><span style="color:#68AC3B; font-size:18px;font-weight: 600;">Step 3 : </span>
                                <span class="tdLabelheadingBg" style="font-weight: 600;font-size: 16px;">Enter Candidates Family Information </span>
                            </td>
                        </tr>
                    </table>
                    <table class="table table_no_border">
                        <tr>
                            <td>
                                <table class="table table_no_border form-table">
                                    <tr>
                                        <td  style="text-align:center" class="tdLabelheadingBg alignRight" colspan="2">Father's Information </td>
                                    </tr>
                                    <tr>
                                        <% List<String> candiFatherNameValidList = hmValidationFields.get("CANDI_FATHER_NAME"); 
                                            validReqOpt = "";
                                            validAsterix = "";
                                            if(candiFatherNameValidList != null && uF.parseToBoolean(candiFatherNameValidList.get(0))) {
                                            	validReqOpt = "validateRequired";
                                            	validAsterix = "<sup>*</sup>";
                                            }
                                            %>
                                        <td class="txtlabel alignRight">Name:<%=validAsterix %></td>
                                        <td><input type="text" class="<%=validReqOpt%>" name="fatherName" value="<%=uF.showData((String)request.getAttribute("fatherName"), "") %>"/></td>
                                    </tr>
                                    <tr>
                                        <% List<String> candiFatherDOBValidList = hmValidationFields.get("CANDI_FATHER_DATE_OF_BIRTH"); 
                                            validReqOpt = "";
                                            validAsterix = "";
                                            if(candiFatherDOBValidList != null && uF.parseToBoolean(candiFatherDOBValidList.get(0))) {
                                            	validReqOpt = "validateRequired";
                                            	validAsterix = "<sup>*</sup>";
                                            }
                                            %>
                                        <td class="txtlabel alignRight">Date of birth:<%=validAsterix %></td>
                                        <td><input type="text" class="<%=validReqOpt%>" name="fatherDob" value="<%=uF.showData((String)request.getAttribute("fatherDob"), "") %>"/></td>
                                    </tr>
                                    <tr>
                                        <% List<String> candiFatherEducationValidList = hmValidationFields.get("CANDI_FATHER_EDUCATION"); 
                                            validReqOpt = "";
                                            validAsterix = "";
                                            if(candiFatherEducationValidList != null && uF.parseToBoolean(candiFatherEducationValidList.get(0))) {
                                            	validReqOpt = "validateRequired";
                                            	validAsterix = "<sup>*</sup>";
                                            }
                                            %>
                                        <td class="txtlabel alignRight">Education:<%=validAsterix %></td>
                                        <td><input type="text" class="<%=validReqOpt%>" name="fatherEducation" value="<%=uF.showData((String)request.getAttribute("fatherEducation"), "") %>"/></td>
                                    </tr>
                                    <tr>
                                        <% List<String> candiFatherOccupationValidList = hmValidationFields.get("CANDI_FATHER_OCCUPATION"); 
                                            validReqOpt = "";
                                            validAsterix = "";
                                            if(candiFatherOccupationValidList != null && uF.parseToBoolean(candiFatherOccupationValidList.get(0))) {
                                            	validReqOpt = "validateRequired";
                                            	validAsterix = "<sup>*</sup>";
                                            }
                                            %>
                                        <td class="txtlabel alignRight">Occupation:<%=validAsterix %></td>
                                        <td><input type="text" class="<%=validReqOpt%>" name="fatherOccupation" value="<%=uF.showData((String)request.getAttribute("fatherOccupation"), "") %>"/></td>
                                    </tr>
                                    <tr>
                                        <% List<String> candiFatherContactNoValidList = hmValidationFields.get("CANDI_FATHER_CONTACT_NO"); 
                                            validReqOpt = "";
                                            validAsterix = "";
                                            if(candiFatherContactNoValidList != null && uF.parseToBoolean(candiFatherContactNoValidList.get(0))) {
                                            	validReqOpt = "validateRequired";
                                            	validAsterix = "<sup>*</sup>";
                                            }
                                            %>
                                        <td class="txtlabel alignRight">Contact Number:<%=validAsterix %></td>
                                        <td><input type="text" class="<%=validReqOpt%>" onkeypress="return isNumberKey(event)" name="fatherContactNumber" value="<%=uF.showData((String)request.getAttribute("fatherContactNumber"), "") %>"/></td>
                                    </tr>
                                    <tr>
                                        <% List<String> candiFatherMailIdValidList = hmValidationFields.get("CANDI_FATHER_EMAIL_ID"); 
                                            validReqOpt = "";
                                            validAsterix = "";
                                            if(candiFatherMailIdValidList != null && uF.parseToBoolean(candiFatherMailIdValidList.get(0))) {
                                            	validReqOpt = "validate[required,custom[email]]";
                                            	validAsterix = "<sup>*</sup>";
                                            }
                                            %>
                                        <td class="txtlabel alignRight">Email Id:<%=validAsterix %></td>
                                        <td><input type="text" class="<%=validReqOpt%>" name="fatherEmailId" value="<%=uF.showData((String)request.getAttribute("fatherEmailId"), "") %>" /></td>
                                    </tr>
                                    <tr>
                                        <td colspan="2">&nbsp;</td>
                                    </tr>
                                </table>
                            </td>
                            <td>
                                <table class="table table_no_border form-table">
                                    <tr>
                                        <td style="text-align:center" class="tdLabelheadingBg alignRight" colspan="2">Mother's Information </td>
                                    </tr>
                                    <tr>
                                        <% List<String> candiMotherNameValidList = hmValidationFields.get("CANDI_MOTHER_NAME"); 
                                            validReqOpt = "";
                                            validAsterix = "";
                                            if(candiMotherNameValidList != null && uF.parseToBoolean(candiMotherNameValidList.get(0))) {
                                            	validReqOpt = "validateRequired";
                                            	validAsterix = "<sup>*</sup>";
                                            }
                                            %>
                                        <td class="txtlabel alignRight">Name:<%=validAsterix %></td>
                                        <td><input type="text" class="<%=validReqOpt%>" name="motherName" value="<%=uF.showData((String)request.getAttribute("motherName"), "") %>"/></td>
                                    </tr>
                                    <tr>
                                        <% List<String> candiMotherDOBValidList = hmValidationFields.get("CANDI_MOTHER_DATE_OF_BIRTH"); 
                                            validReqOpt = "";
                                            validAsterix = "";
                                            if(candiMotherDOBValidList != null && uF.parseToBoolean(candiMotherDOBValidList.get(0))) {
                                            	validReqOpt = "validateRequired";
                                            	validAsterix = "<sup>*</sup>";
                                            }
                                            %>
                                        <td class="txtlabel alignRight">Date of birth:<%=validAsterix %></td>
                                        <td><input type="text" class="<%=validReqOpt%>" name="motherDob" value="<%=uF.showData((String)request.getAttribute("motherDob"), "") %>"/></td>
                                    </tr>
                                    <tr>
                                        <% List<String> candiMotherEducationValidList = hmValidationFields.get("CANDI_MOTHER_EDUCATION"); 
                                            validReqOpt = "";
                                            validAsterix = "";
                                            if(candiMotherEducationValidList != null && uF.parseToBoolean(candiMotherEducationValidList.get(0))) {
                                            	validReqOpt = "validateRequired";
                                            	validAsterix = "<sup>*</sup>";
                                            }
                                            %>
                                        <td class="txtlabel alignRight">Education:<%=validAsterix %></td>
                                        <td><input type="text" class="<%=validReqOpt%>" name="motherEducation" value="<%=uF.showData((String)request.getAttribute("motherEducation"), "") %>"/></td>
                                    </tr>
                                    <tr>
                                        <% List<String> candiMotherOccupationValidList = hmValidationFields.get("CANDI_MOTHER_OCCUPATION"); 
                                            validReqOpt = "";
                                            validAsterix = "";
                                            if(candiMotherOccupationValidList != null && uF.parseToBoolean(candiMotherOccupationValidList.get(0))) {
                                            	validReqOpt = "validateRequired";
                                            	validAsterix = "<sup>*</sup>";
                                            }
                                            %>
                                        <td class="txtlabel alignRight">Occupation:<%=validAsterix %></td>
                                        <td><input type="text" class="<%=validReqOpt%>" name="motherOccupation" value="<%=uF.showData((String)request.getAttribute("motherOccupation"), "") %>"/></td>
                                    </tr>
                                    <tr>
                                        <% List<String> candiMotherContactNoValidList = hmValidationFields.get("CANDI_MOTHER_CONTACT_NO"); 
                                            validReqOpt = "";
                                            validAsterix = "";
                                            if(candiMotherContactNoValidList != null && uF.parseToBoolean(candiMotherContactNoValidList.get(0))) {
                                            	validReqOpt = "validateRequired";
                                            	validAsterix = "<sup>*</sup>";
                                            }
                                            %>
                                        <td class="txtlabel alignRight">Contact Number:<%=validAsterix %></td>
                                        <td><input type="text" class="<%=validReqOpt%>" onkeypress="return isNumberKey(event)" name="motherContactNumber" value="<%=uF.showData((String)request.getAttribute("motherContactNumber"), "") %>"/></td>
                                    </tr>
                                    <tr>
                                        <% List<String> candiMotherMailIdValidList = hmValidationFields.get("CANDI_MOTHER_EMAIL_ID"); 
                                            validReqOpt = "";
                                            validAsterix = "";
                                            if(candiMotherMailIdValidList != null && uF.parseToBoolean(candiMotherMailIdValidList.get(0))) {
                                            	validReqOpt = "validate[required,custom[email]]";
                                            	validAsterix = "<sup>*</sup>";
                                            }
                                            %>
                                        <td class="txtlabel alignRight">Email Id:<%=validAsterix %></td>
                                        <td><input type="text" class="<%=validReqOpt%>" name="motherEmailId" value="<%=uF.showData((String)request.getAttribute("motherEmailId"), "") %>"/></td>
                                    </tr>
                                    <tr>
                                        <td colspan="2">&nbsp;</td>
                                    </tr>
                                </table>
                            </td>
                            <td>
                                <table class="table table_no_border form-table">
                                    <tr>
                                        <td style="text-align:center" class="tdLabelheadingBg alignRight" colspan="2">Spouse's Information </td>
                                    </tr>
                                    <tr>
                                        <% List<String> candiSpouseNameValidList = hmValidationFields.get("CANDI_SPOUSE_NAME"); 
                                            validReqOpt = "";
                                            validAsterix = "";
                                            if(candiSpouseNameValidList != null && uF.parseToBoolean(candiSpouseNameValidList.get(0))) {
                                            	validReqOpt = "validateRequired";
                                            	validAsterix = "<sup>*</sup>";
                                            }
                                            %>
                                        <td class="txtlabel alignRight">Name:<%=validAsterix %></td>
                                        <td><input type="text" class="<%=validReqOpt%>" name="spouseName" value="<%=uF.showData((String)request.getAttribute("spouseName"), "") %>"/></td>
                                    </tr>
                                    <tr>
                                        <% List<String> candiSpouseDOBValidList = hmValidationFields.get("CANDI_SPOUSE_DATE_OF_BIRTH"); 
                                            validReqOpt = "";
                                            validAsterix = "";
                                            if(candiSpouseDOBValidList != null && uF.parseToBoolean(candiSpouseDOBValidList.get(0))) {
                                            	validReqOpt = "validateRequired";
                                            	validAsterix = "<sup>*</sup>";
                                            }
                                            %>
                                        <td class="txtlabel alignRight">Date of birth:<%=validAsterix %></td>
                                        <td><input type="text" class="<%=validReqOpt%>" name="spouseDob" value="<%=uF.showData((String)request.getAttribute("spouseDob"), "") %>"/></td>
                                    </tr>
                                    <tr>
                                        <% List<String> candiSpouseEducationValidList = hmValidationFields.get("CANDI_SPOUSE_EDUCATION"); 
                                            validReqOpt = "";
                                            validAsterix = "";
                                            if(candiSpouseEducationValidList != null && uF.parseToBoolean(candiSpouseEducationValidList.get(0))) {
                                            	validReqOpt = "validateRequired";
                                            	validAsterix = "<sup>*</sup>";
                                            }
                                            %>
                                        <td class="txtlabel alignRight">Education:<%=validAsterix %></td>
                                        <td><input type="text" class="<%=validReqOpt%>" name="spouseEducation" value="<%=uF.showData((String)request.getAttribute("spouseEducation"), "") %>"/></td>
                                    </tr>
                                    <tr>
                                        <% List<String> candiSpouseOccupationValidList = hmValidationFields.get("CANDI_SPOUSE_OCCUPATION"); 
                                            validReqOpt = "";
                                            validAsterix = "";
                                            if(candiSpouseOccupationValidList != null && uF.parseToBoolean(candiSpouseOccupationValidList.get(0))) {
                                            	validReqOpt = "validateRequired";
                                            	validAsterix = "<sup>*</sup>";
                                            }
                                            %>
                                        <td class="txtlabel alignRight">Occupation:<%=validAsterix %></td>
                                        <td><input type="text" class="<%=validReqOpt%>" name="spouseOccupation" value="<%=uF.showData((String)request.getAttribute("spouseOccupation"), "") %>"/></td>
                                    </tr>
                                    <tr>
                                        <% List<String> candiSpouseContactNoValidList = hmValidationFields.get("CANDI_SPOUSE_CONTACT_NO"); 
                                            validReqOpt = "";
                                            validAsterix = "";
                                            if(candiSpouseContactNoValidList != null && uF.parseToBoolean(candiSpouseContactNoValidList.get(0))) {
                                            	validReqOpt = "validateRequired";
                                            	validAsterix = "<sup>*</sup>";
                                            }
                                            %>
                                        <td class="txtlabel alignRight">Contact Number:<%=validAsterix %></td>
                                        <td><input type="text" class="<%=validReqOpt%>" onkeypress="return isNumberKey(event)" name="spouseContactNumber" value="<%=uF.showData((String)request.getAttribute("spouseContactNumber"), "") %>"/></td>
                                    </tr>
                                    <tr>
                                        <% List<String> candiSpouseMailIdValidList = hmValidationFields.get("CANDI_SPOUSE_EMAIL_ID"); 
                                            validReqOpt = "";
                                            validAsterix = "";
                                            if(candiSpouseMailIdValidList != null && uF.parseToBoolean(candiSpouseMailIdValidList.get(0))) {
                                            	validReqOpt = "validate[required,custom[email]]";
                                            	validAsterix = "<sup>*</sup>";
                                            }
                                            %>
                                        <td class="txtlabel alignRight">Email Id:<%=validAsterix %></td>
                                        <td><input type="text" class="<%=validReqOpt%>" name="spouseEmailId" value="<%=uF.showData((String)request.getAttribute("spouseEmailId"), "") %>"/></td>
                                    </tr>
                                    <tr>
                                        <% List<String> candiSpouseGenderValidList = hmValidationFields.get("CANDI_SPOUSE_GENDER"); 
                                            validReqOpt = "";
                                            validAsterix = "";
                                            if(candiSpouseGenderValidList != null && uF.parseToBoolean(candiSpouseGenderValidList.get(0))) {
                                            	validReqOpt = "validateRequired";
                                            	validAsterix = "<sup>*</sup>";
                                            }
                                            %>
                                        <td class="txtlabel alignRight">Gender:<%=validAsterix %></td>
                                        <td>
                                            <% if(candiSpouseGenderValidList != null && uF.parseToBoolean(candiSpouseGenderValidList.get(0))) { %>
                                            <s:select theme="simple" label="Select Gender" name="spouseGender" listKey="genderId" cssClass="validateRequired  " 
                                                cssStyle=";" listValue="genderName" headerKey="0" headerValue="Select Gender" list="empGenderList" required="true" />
                                            <% } else { %>
                                            <s:select theme="simple" label="Select Gender" name="spouseGender" listKey="genderId" cssStyle=";" 
                                                listValue="genderName" headerKey="0" headerValue="Select Gender" list="empGenderList" key="" required="true" />
                                            <% } %>
                                        </td>
                                </table>
                            </td>
                        </tr>
                    </table>
                    <%	if(alSiblings!=null && alSiblings.size()!=0) {
                        for(int i=0; i<alSiblings.size(); i++) { %>
                    <div id="col_family_siblings<%=i%>" style="float:left; width: 100%; border:solid 0px #ccc;" >
                        <table class="table table_no_border form-table">
                            <tr>
                                <td style="text-align:center" class="tdLabelheadingBg alignRight" colspan="2">Sibling's Information </td>
                            </tr>
                            <tr>
                                <% List<String> candiSiblingNameValidList = hmValidationFields.get("CANDI_SIBLING_NAME"); 
                                    validReqOpt = "";
                                    validAsterix = "";
                                    if(candiSiblingNameValidList != null && uF.parseToBoolean(candiSiblingNameValidList.get(0))) {
                                    	validReqOpt = "validateRequired";
                                    	validAsterix = "<sup>*</sup>";
                                    }
                                    %>
                                <td class="txtlabel alignRight">Name:<%=validAsterix %></td>
                                <td><input type="text" class="<%=validReqOpt%>" name="memberName" value="<%=((ArrayList)alSiblings.get(i)).get(1)%>" ></input></td>
                            </tr>
                            <tr>
                                <% List<String> candiSiblingDOBValidList = hmValidationFields.get("CANDI_SIBLING_DATE_OF_BIRTH"); 
                                    validReqOpt = "";
                                    validAsterix = "";
                                    if(candiSiblingDOBValidList != null && uF.parseToBoolean(candiSiblingDOBValidList.get(0))) {
                                    	validReqOpt = "validateRequired";
                                    	validAsterix = "<sup>*</sup>";
                                    }
                                    %>
                                <td class="txtlabel alignRight">Date of birth:<%=validAsterix %></td>
                                <td><input type="text" class="<%=validReqOpt%>" name="memberDob" value="<%=((ArrayList)alSiblings.get(i)).get(2)%>"></input></td>
                            </tr>
                            <tr>
                                <% List<String> candiSiblingEducationValidList = hmValidationFields.get("CANDI_SIBLING_EDUCATION"); 
                                    validReqOpt = "";
                                    validAsterix = "";
                                    if(candiSiblingEducationValidList != null && uF.parseToBoolean(candiSiblingEducationValidList.get(0))) {
                                    	validReqOpt = "validateRequired";
                                    	validAsterix = "<sup>*</sup>";
                                    }
                                    %>
                                <td class="txtlabel alignRight">Education:<%=validAsterix %></td>
                                <td><input type="text" class="<%=validReqOpt%>" name="memberEducation" value="<%=((ArrayList)alSiblings.get(i)).get(3)%>"></input></td>
                            </tr>
                            <tr>
                                <% List<String> candiSiblingOccupationValidList = hmValidationFields.get("CANDI_SIBLING_OCCUPATION"); 
                                    validReqOpt = "";
                                    validAsterix = "";
                                    if(candiSiblingOccupationValidList != null && uF.parseToBoolean(candiSiblingOccupationValidList.get(0))) {
                                    	validReqOpt = "validateRequired";
                                    	validAsterix = "<sup>*</sup>";
                                    }
                                    %>
                                <td class="txtlabel alignRight">Occupation:<%=validAsterix %></td>
                                <td><input type="text" class="<%=validReqOpt%>" name="memberOccupation" value="<%=((ArrayList)alSiblings.get(i)).get(4)%>"></input></td>
                            </tr>
                            <tr>
                                <% List<String> candiSiblingContactNoValidList = hmValidationFields.get("CANDI_SIBLING_CONTACT_NO"); 
                                    validReqOpt = "";
                                    validAsterix = "";
                                    if(candiSiblingContactNoValidList != null && uF.parseToBoolean(candiSiblingContactNoValidList.get(0))) {
                                    	validReqOpt = "validateRequired";
                                    	validAsterix = "<sup>*</sup>";
                                    }
                                    %>
                                <td class="txtlabel alignRight">Contact Number:<%=validAsterix %></td>
                                <td><input type="text" class="<%=validReqOpt%>" onkeypress="return isNumberKey(event)" name="memberContactNumber" value="<%=((ArrayList)alSiblings.get(i)).get(5)%>"></input></td>
                            </tr>
                            <tr>
                                <% List<String> candiSiblingMailIdValidList = hmValidationFields.get("CANDI_SIBLING_EMAIL_ID"); 
                                    validReqOpt = "";
                                    validAsterix = "";
                                    if(candiSiblingMailIdValidList != null && uF.parseToBoolean(candiSiblingMailIdValidList.get(0))) {
                                    	validReqOpt = "validate[required,custom[email]]";
                                    	validAsterix = "<sup>*</sup>";
                                    }
                                    %>
                                <td class="txtlabel alignRight">Email Id:<%=validAsterix %></td>
                                <td><input type="text" class="<%=validReqOpt%>" name="memberEmailId" value="<%=((ArrayList)alSiblings.get(i)).get(6)%>"></input></td>
                            </tr>
                            <tr>
                                <% List<String> candiSiblingGenderValidList = hmValidationFields.get("CANDI_SIBLING_GENDER"); 
                                    validReqOpt = "";
                                    validAsterix = "";
                                    if(candiSiblingGenderValidList != null && uF.parseToBoolean(candiSiblingGenderValidList.get(0))) {
                                    	validReqOpt = "validateRequired";
                                    	validAsterix = "<sup>*</sup>";
                                    }
                                    %>
                                <td class="txtlabel alignRight">Gender:<%=validAsterix %></td>
                                <%-- <td><input type="text" style=";" name="memberGender" value="<%=((ArrayList)alSiblings.get(i)).get(7)%>"></input></td> --%>
                                <td>
                                    <select name="memberGender" class="<%=validReqOpt%>">
                                        <%for(int k=0; k< empGenderList.size(); k++) { 
                                            if( (((FillGender)empGenderList.get(k)).getGenderId()+"").equals( (String)((ArrayList)alSiblings.get(i)).get(7) )) {
                                            %>
                                        <option value="<%=((FillGender)empGenderList.get(k)).getGenderId() %>" selected="selected">
                                            <%=((FillGender)empGenderList.get(k)).getGenderName() %>
                                        </option>
                                        <%}else { %>
                                        <option value="<%=((FillGender)empGenderList.get(k)).getGenderId() %>">
                                            <%=((FillGender)empGenderList.get(k)).getGenderName() %>
                                        </option>
                                        <% }
                                            }%>
                                    </select>
                                </td>
                            </tr>
                            <!-- <tr><td class="txtlabel alignRight"><a href="javascript:void(0)" onclick="addSibling()"  style="float:right" class="add-font">&nbsp;</a></td> -->   
                            <tr>
                                <td class="txtlabel alignRight" colspan="2">
                                    <a href="javascript:void(0)" onclick="addSibling()"  style="float:right" class="add-font"></a>
                                    <% if(i>0){ %>
                                    <a href="javascript:void(0)" onclick="removeSibling(this.id)" id=<%=i%> class="remove-font" ></a>
                                    <%} %>
                                </td>
                            </tr>
                        </table>
                    </div>
                    <%}
                        } else { %>
                    <div id="col_family_siblings" style="float: left;border:solid 0px #f00" >
                        <table class="table table_no_border form-table">
                            <tr>
                                <td style="text-align:center" class="tdLabelheadingBg alignRight" colspan="2">Sibling's Information </td>
                            </tr>
                            <tr>
                                <% List<String> candiSiblingNameValidList = hmValidationFields.get("CANDI_SIBLING_NAME"); 
                                    validReqOpt = "";
                                    validAsterix = "";
                                    if(candiSiblingNameValidList != null && uF.parseToBoolean(candiSiblingNameValidList.get(0))) {
                                    	validReqOpt = "validateRequired";
                                    	validAsterix = "<sup>*</sup>";
                                    }
                                    %>
                                <td class="txtlabel alignRight">Name:<%=validAsterix %></td>
                                <td><input type="text" class="<%=validReqOpt%>" name="memberName" /></td>
                            </tr>
                            <tr>
                                <% List<String> candiSiblingDOBValidList = hmValidationFields.get("CANDI_SIBLING_DATE_OF_BIRTH"); 
                                    validReqOpt = "";
                                    validAsterix = "";
                                    if(candiSiblingDOBValidList != null && uF.parseToBoolean(candiSiblingDOBValidList.get(0))) {
                                    	validReqOpt = "validateRequired";
                                    	validAsterix = "<sup>*</sup>";
                                    }
                                    %>
                                <td class="txtlabel alignRight">Date of birth:<%=validAsterix %></td>
                                <td><input type="text" class="<%=validReqOpt%>" name="memberDob" /></td>
                            </tr>
                            <tr>
                                <% List<String> candiSiblingEducationValidList = hmValidationFields.get("CANDI_SIBLING_EDUCATION"); 
                                    validReqOpt = "";
                                    validAsterix = "";
                                    if(candiSiblingEducationValidList != null && uF.parseToBoolean(candiSiblingEducationValidList.get(0))) {
                                    	validReqOpt = "validateRequired";
                                    	validAsterix = "<sup>*</sup>";
                                    }
                                    %>
                                <td class="txtlabel alignRight">Education:<%=validAsterix %></td>
                                <td><input type="text" class="<%=validReqOpt%>" name="memberEducation" /></td>
                            </tr>
                            <tr>
                                <% List<String> candiSiblingOccupationValidList = hmValidationFields.get("CANDI_SIBLING_OCCUPATION"); 
                                    validReqOpt = "";
                                    validAsterix = "";
                                    if(candiSiblingOccupationValidList != null && uF.parseToBoolean(candiSiblingOccupationValidList.get(0))) {
                                    	validReqOpt = "validateRequired";
                                    	validAsterix = "<sup>*</sup>";
                                    }
                                    %>
                                <td class="txtlabel alignRight">Occupation:<%=validAsterix %></td>
                                <td><input type="text" class="<%=validReqOpt%>" name="memberOccupation" /></td>
                            </tr>
                            <tr>
                                <% List<String> candiSiblingContactNoValidList = hmValidationFields.get("CANDI_SIBLING_CONTACT_NO"); 
                                    validReqOpt = "";
                                    validAsterix = "";
                                    if(candiSiblingContactNoValidList != null && uF.parseToBoolean(candiSiblingContactNoValidList.get(0))) {
                                    	validReqOpt = "validateRequired";
                                    	validAsterix = "<sup>*</sup>";
                                    }
                                    %>
                                <td class="txtlabel alignRight">Contact Number:<%=validAsterix %></td>
                                <td><input type="text" class="<%=validReqOpt%>" onkeypress="return isNumberKey(event)" name="memberContactNumber" /></td>
                            </tr>
                            <tr>
                                <% List<String> candiSiblingMailIdValidList = hmValidationFields.get("CANDI_SIBLING_EMAIL_ID"); 
                                    validReqOpt = "";
                                    validAsterix = "";
                                    if(candiSiblingMailIdValidList != null && uF.parseToBoolean(candiSiblingMailIdValidList.get(0))) {
                                    	validReqOpt = "validate[required,custom[email]]";
                                    	validAsterix = "<sup>*</sup>";
                                    }
                                    %>
                                <td class="txtlabel alignRight">Email Id:<%=validAsterix %></td>
                                <td><input type="text" class="<%=validReqOpt%>" name="memberEmailId" /></td>
                            </tr>
                            <tr>
                                <% List<String> candiSiblingGenderValidList = hmValidationFields.get("CANDI_SIBLING_GENDER"); 
                                    validReqOpt = "";
                                    validAsterix = "";
                                    if(candiSiblingGenderValidList != null && uF.parseToBoolean(candiSiblingGenderValidList.get(0))) {
                                    	validReqOpt = "validateRequired";
                                    	validAsterix = "<sup>*</sup>";
                                    }
                                    %>
                                <td class="txtlabel alignRight">Gender:<%=validAsterix %></td>
                                <td>
                                    <% if(candiSiblingGenderValidList != null && uF.parseToBoolean(candiSiblingGenderValidList.get(0))) { %>
                                    <s:select theme="simple" label="Select Gender" name="memberGender" listKey="genderId" cssClass="validateRequired  " cssStyle=";"
                                        listValue="genderName" headerKey="0" headerValue="Select Gender" list="empGenderList" key="" required="true" />
                                    <% } else { %>
                                    <s:select theme="simple" label="Select Gender" name="memberGender" listKey="genderId" cssStyle=";"
                                        listValue="genderName" headerKey="0" headerValue="Select Gender" list="empGenderList" key="" required="true" />
                                    <% } %>		
                                </td>
                            </tr>
                            <tr>
                                <td class="txtlabel alignRight" colspan="2"><a href="javascript:void(0)" onclick="addSibling()" class="add-font" style="float:right"></a></td>
                            </tr>
                        </table>
                    </div>
                    <%}%>
                </div>
                <div class="clr"></div>
                <div style="float:right;">
                    <table class="table table_no_border">
                    <% if(operation != null && operation.equals("U")) {%>
                       		 <tr>
                                <td colspan="2" align="center">
                                	<s:submit cssClass="btn btn-primary" name="stepSubmit" value="Update" align="center" />
                                </td>
                            </tr>
                     		
                      <%}else { %>
	                        <s:if test="mode==null">
	                            <tr>
	                                <td colspan="2" align="center">
	                                	<button type="button" class="btn btn-default prev-step" onclick="callPrevious(1,'<%=request.getAttribute("CandidateId")%>','<%=(String)request.getAttribute("fromPage")%>')">Previous</button>
	                                    <s:submit cssClass="btn btn-primary" name="stepSubmit" value="Submit & Proceed" align="center" />
	                                </td>
	                            </tr>
	                        </s:if>
	                       
	                        <s:else>
	                            <tr>
	                                <td colspan="2" align="center">
	                                	<%-- <button type="button" class="btn btn-default prev-step" onclick="callPrevious(1,'<%=request.getAttribute("CandidateId")%>','<%=(String)request.getAttribute("fromPage")%>')">Previous</button> --%>
	                                    <s:submit cssClass="btn btn-primary" name="stepSubmit" value="Submit & Proceed" align="center" />
	                                </td>
	                            </tr>
	                        </s:else>
                        <% } %>
                    </table>
                </div>
            </s:form>
        </div>
    </s:if>
    <s:if test="step==4 || mode=='report'">
        <div class="tab-pane active" role="tabpanel" id="pe">
            <s:form theme="simple" action="AddCandidate" id="frmPrevEmployment" method="POST" cssClass="formcss" enctype="multipart/form-data">
                <div style="height: auto; width: 100%; float: left; border: solid 0px black; overflow: auto;" id="div_prev_employment">
                    <s:hidden name="show"></s:hidden>
                    <s:hidden name="operation" />
                    <s:hidden name="recruitId" />
                    <s:hidden name="CandidateId" />
                    <s:hidden name="mode" />
                    <s:hidden name="step" />
                    <s:hidden name="fromPage" id="fromPage" />
                    <input type="hidden" name="fileCount" id="fileCount" value="0">
                    <table border="0" class="table table_no_border">
                        <tr>
                            <td class="tdLabelheadingBg alignCenter" colspan="2"><span style="color:#68AC3B; font-size:18px;font-weight: 600;">Step 4 : </span><span style="font-weight: 600;font-size: 16px;">Enter Candidates Previous Employment</span> </td>
                        </tr>
                    </table>
                    <%	if(alPrevEmployment!=null && alPrevEmployment.size()!=0) {
                        for(int i=0; i<alPrevEmployment.size(); i++) { %>
                    <div id="col_prev_employer<%=i%>" style="float: left;">
                        <table class="table table_no_border form-table">
                            <tr>
                                <% List<String> candiPrevCompanyNameValidList = hmValidationFields.get("CANDI_PREV_COMPANY_NAME"); 
                                    validReqOpt = "";
                                    validAsterix = "";
                                    if(candiPrevCompanyNameValidList != null && uF.parseToBoolean(candiPrevCompanyNameValidList.get(0))) {
                                    	validReqOpt = "validateRequired";
                                    	validAsterix = "<sup>*</sup>";
                                    }
                                    %>
                                <td class="txtlabel alignRight">
	                                <input type="hidden" name="prevCompanyId" value="<%=((ArrayList)alPrevEmployment.get(i)).get(0) %>"/>
	                                Company Name:<%=validAsterix %>
                                </td>
                                <!-- ===start parvez date:14-09-2021 remove name="prevCompanyLocation" ===-->
                                <td><input type="text" name="prevCompanyName" id="prevCompanyName" class="<%=validReqOpt%>" value="<%=((ArrayList)alPrevEmployment.get(i)).get(1)%>" onchange="prevCompanyExpFile('<%=i%>',this.value);" /></td>
                           		<!-- ===end parvez date: 14-09-2021=== -->
                            </tr>
                            <tr>
                                <% List<String> candiPrevCompanyLocationValidList = hmValidationFields.get("CANDI_PREV_COMPANY_LOCATION"); 
                                    validReqOpt = "";
                                    validAsterix = "";
                                    if(candiPrevCompanyLocationValidList != null && uF.parseToBoolean(candiPrevCompanyLocationValidList.get(0))) {
                                    	validReqOpt = "validateRequired";
                                    	validAsterix = "<sup>*</sup>";
                                    }
                                    %>
                                <td class="txtlabel alignRight">Location:<%=validAsterix %></td>
                                <td> <input type="text" class="<%=validReqOpt%>" name="prevCompanyLocation" value="<%=((ArrayList)alPrevEmployment.get(i)).get(2)%>"></input></td>
                            </tr>
                            <tr>
                                <% List<String> candiPrevCompanyCityValidList = hmValidationFields.get("CANDI_PREV_COMPANY_CITY"); 
                                    validReqOpt = "";
                                    validAsterix = "";
                                    if(candiPrevCompanyCityValidList != null && uF.parseToBoolean(candiPrevCompanyCityValidList.get(0))) {
                                    	validReqOpt = "validateRequired";
                                    	validAsterix = "<sup>*</sup>";
                                    }
                                    %>
                                <td class="txtlabel alignRight">City:<%=validAsterix %></td>
                                <td><input type="text" style=" ;" class="<%=validReqOpt%>" name="prevCompanyCity" value="<%=((ArrayList)alPrevEmployment.get(i)).get(3)%>"></input></td>
                            </tr>
                            <tr>
                                <% List<String> candiPrevCompanyStateValidList = hmValidationFields.get("CANDI_PREV_COMPANY_STATE"); 
                                    validReqOpt = "";
                                    validAsterix = "";
                                    if(candiPrevCompanyStateValidList != null && uF.parseToBoolean(candiPrevCompanyStateValidList.get(0))) {
                                    	validReqOpt = "validateRequired";
                                    	validAsterix = "<sup>*</sup>";
                                    }
                                    %>
                                <td class="txtlabel alignRight">State:<%=validAsterix %></td>
                                <td> <input type="text" style=" ;" class="<%=validReqOpt%>" name="prevCompanyState" value="<%=((ArrayList)alPrevEmployment.get(i)).get(4)%>"></input></td>
                            </tr>
                            <tr>
                                <% List<String> candiPrevCompanyCountryValidList = hmValidationFields.get("CANDI_PREV_COMPANY_COUNRTY"); 
                                    validReqOpt = "";
                                    validAsterix = "";
                                    if(candiPrevCompanyCountryValidList != null && uF.parseToBoolean(candiPrevCompanyCountryValidList.get(0))) {
                                    	validReqOpt = "validateRequired";
                                    	validAsterix = "<sup>*</sup>";
                                    }
                                    %>
                                <td class="txtlabel alignRight">Country:<%=validAsterix %></td>
                                <td> <input type="text" style=" ;" class="<%=validReqOpt%>" name="prevCompanyCountry" value="<%=((ArrayList)alPrevEmployment.get(i)).get(5)%>"></input></td>
                            </tr>
                            <tr>
                                <% List<String> candiPrevCompanyContactNoValidList = hmValidationFields.get("CANDI_PREV_COMPANY_CONTACT_NO"); 
                                    validReqOpt = "";
                                    validAsterix = "";
                                    if(candiPrevCompanyContactNoValidList != null && uF.parseToBoolean(candiPrevCompanyContactNoValidList.get(0))) {
                                    	validReqOpt = "validateRequired";
                                    	validAsterix = "<sup>*</sup>";
                                    }
                                    %>
                                <td class="txtlabel alignRight">Phone Number:<%=validAsterix %></td>
                                <td> <input type="text" onkeypress="return isNumberKey(event)" class="<%=validReqOpt%>" name="prevCompanyContactNo" value="<%=((ArrayList)alPrevEmployment.get(i)).get(6)%>"></input></td>
                            </tr>
                            <tr>
                                <% List<String> candiPrevCompanyReportingToValidList = hmValidationFields.get("CANDI_PREV_COMPANY_REPORTING_TO"); 
                                    validReqOpt = "";
                                    validAsterix = "";
                                    if(candiPrevCompanyReportingToValidList != null && uF.parseToBoolean(candiPrevCompanyReportingToValidList.get(0))) {
                                    	validReqOpt = "validateRequired";
                                    	validAsterix = "<sup>*</sup>";
                                    }
                                    %>
                                <td class="txtlabel alignRight">Reporting To:<%=validAsterix %></td>
                                <td><input type="text" style=" ;" class="<%=validReqOpt%>" name="prevCompanyReportingTo" value="<%=((ArrayList)alPrevEmployment.get(i)).get(7)%>"></input></td>
                            </tr>
                            <tr>
                                <% List<String> candiPrevCompanyReportingToPhoneNoValidList = hmValidationFields.get("CANDI_PREV_COMPANY_REPORTING_TO_PHONE_NO"); 
                                    validReqOpt = "";
                                    validAsterix = "";
                                    if(candiPrevCompanyReportingToPhoneNoValidList != null && uF.parseToBoolean(candiPrevCompanyReportingToPhoneNoValidList.get(0))) {
                                    	validReqOpt = "validateRequired";
                                    	validAsterix = "<sup>*</sup>";
                                    }
                                    %>
                                <td class="txtlabel alignRight">Reporting Manager Phone Number:<%=validAsterix %></td>
                                <td> <input type="text" onkeypress="return isNumberKey(event)" class="<%=validReqOpt%>" name="prevCompanyReportManagerPhNo" value="<%=((ArrayList)alPrevEmployment.get(i)).get(8)%>"></input></td>
                            </tr>
                            <tr>
                                <% List<String> candiPrevCompanyHrManagerValidList = hmValidationFields.get("CANDI_PREV_COMPANY_HR_MANAGER"); 
                                    validReqOpt = "";
                                    validAsterix = "";
                                    if(candiPrevCompanyHrManagerValidList != null && uF.parseToBoolean(candiPrevCompanyHrManagerValidList.get(0))) {
                                    	validReqOpt = "validateRequired";
                                    	validAsterix = "<sup>*</sup>";
                                    }
                                    %>
                                <td class="txtlabel alignRight">HR Manager:<%=validAsterix %></td>
                                <td> <input type="text" style=" ;" class="<%=validReqOpt%>" name="prevCompanyHRManager" value="<%=((ArrayList)alPrevEmployment.get(i)).get(9)%>"></input></td>
                            </tr>
                            <tr>
                                <% List<String> candiPrevCompanyHrManagerPhoneNoValidList = hmValidationFields.get("CANDI_PREV_COMPANY_HR_MANAGER_PHONE_NO"); 
                                    validReqOpt = "";
                                    validAsterix = "";
                                    if(candiPrevCompanyHrManagerPhoneNoValidList != null && uF.parseToBoolean(candiPrevCompanyHrManagerPhoneNoValidList.get(0))) {
                                    	validReqOpt = "validateRequired";
                                    	validAsterix = "<sup>*</sup>";
                                    }
                                    %>
                                <td class="txtlabel alignRight">HR Manager Phone Number:<%=validAsterix %></td>
                                <td> <input type="text" onkeypress="return isNumberKey(event)" class="<%=validReqOpt%>" name="prevCompanyHRManagerPhNo" value="<%=((ArrayList)alPrevEmployment.get(i)).get(10)%>"></input></td>
                            </tr>
                            <tr>
                                <% List<String> candiPrevCompanyFromDateValidList = hmValidationFields.get("CANDI_PREV_COMPANY_FROM_DATE"); 
                                    validReqOpt = "";
                                    validAsterix = "";
                                    if(candiPrevCompanyFromDateValidList != null && uF.parseToBoolean(candiPrevCompanyFromDateValidList.get(0))) {
                                    	validReqOpt = "validateRequired";
                                    	validAsterix = "<sup>*</sup>";
                                    }
                                    %>
                                <td class="txtlabel alignRight">From:<%=validAsterix %> </td>
                                <td><input type="text" style=" ;"  class="<%=validReqOpt%>" name="prevCompanyFromDate" value="<%=((ArrayList)alPrevEmployment.get(i)).get(11)%>"></input></td>
                            </tr>
                            <tr>
                                <% List<String> candiPrevCompanyToDateValidList = hmValidationFields.get("CANDI_PREV_COMPANY_TO_DATE"); 
                                    validReqOpt = "";
                                    validAsterix = "";
                                    if(candiPrevCompanyToDateValidList != null && uF.parseToBoolean(candiPrevCompanyToDateValidList.get(0))) {
                                    	validReqOpt = "validateRequired";
                                    	validAsterix = "<sup>*</sup>";
                                    }
                                    %>
                                <td class="txtlabel alignRight">To:<%=validAsterix %></td>
                                <td> <input type="text" style=" ;" class="<%=validReqOpt%>" name="prevCompanyToDate" value="<%=((ArrayList)alPrevEmployment.get(i)).get(12)%>"></input></td>
                            </tr>
                            <tr>
                                <% List<String> candiPrevCompanyDesignationValidList = hmValidationFields.get("CANDI_PREV_COMPANY_DESIGNATION"); 
                                    validReqOpt = "";
                                    validAsterix = "";
                                    if(candiPrevCompanyDesignationValidList != null && uF.parseToBoolean(candiPrevCompanyDesignationValidList.get(0))) {
                                    	validReqOpt = "validateRequired";
                                    	validAsterix = "<sup>*</sup>";
                                    }
                                    %>
                                <td class="txtlabel alignRight">Designation:<%=validAsterix %></td>
                                <td> <input type="text" style=" ;" class="<%=validReqOpt%>" name="prevCompanyDesination" value="<%=((ArrayList)alPrevEmployment.get(i)).get(13)%>"></input></td>
                            </tr>
                            <tr>
                                <% List<String> candiPrevCompanyResponsibilityValidList = hmValidationFields.get("CANDI_PREV_COMPANY_RESPONSIBILITY"); 
                                    validReqOpt = "";
                                    validAsterix = "";
                                    if(candiPrevCompanyResponsibilityValidList != null && uF.parseToBoolean(candiPrevCompanyResponsibilityValidList.get(0))) {
                                    	validReqOpt = "validateRequired";
                                    	validAsterix = "<sup>*</sup>";
                                    }
                                    %>
                                <td class="txtlabel alignRight">Responsibility:<%=validAsterix %></td>
                                <td> <input type="text" style=" ;" class="<%=validReqOpt%>" name="prevCompanyResponsibilities" value="<%=((ArrayList)alPrevEmployment.get(i)).get(14)%>"></input></td>
                            </tr>
                            <tr>
                                <% List<String> candiPrevCompanySkillsValidList = hmValidationFields.get("CANDI_PREV_COMPANY_SKILLS"); 
                                    validReqOpt = "";
                                    validAsterix = "";
                                    if(candiPrevCompanySkillsValidList != null && uF.parseToBoolean(candiPrevCompanySkillsValidList.get(0))) {
                                    	validReqOpt = "validateRequired";
                                    	validAsterix = "<sup>*</sup>";
                                    }
                                    %>
                                <td class="txtlabel alignRight">Skills:<%=validAsterix %></td>
                                <td> <input type="text" style=" ;" class="<%=validReqOpt%>" name="prevCompanySkills" value="<%=((ArrayList)alPrevEmployment.get(i)).get(15)%>"></input></td>
                            </tr>
                            
                   <!-- ===start parvez date: 08-08-2022=== -->
                   
                   			<tr>
                                <% List<String> candiPrevCompanyESICNoValidList = hmValidationFields.get("CANDI_PREV_COMPANY_ESIC_NO"); 
                                    validReqOpt = "";
                                    validAsterix = "";
                                    if(candiPrevCompanyESICNoValidList != null && uF.parseToBoolean(candiPrevCompanyESICNoValidList.get(0))) {
                                    	validReqOpt = "validateRequired";
                                    	validAsterix = "<sup>*</sup>";
                                    }
                                    %>
                                <td class="txtlabel alignRight"> ESIC No.:<%=validAsterix %></td>
                                <td> <input type="text" style=" ;" class="<%=validReqOpt%>" name="prevCompanyESICNo" value="<%=((ArrayList)alPrevEmployment.get(i)).get(17)%>" ></input></td>
                            </tr>
                            <tr>
                                <% List<String> candiPrevCompanyUANNoValidList = hmValidationFields.get("CANDI_PREV_COMPANY_UAN_NO"); 
                                    validReqOpt = "";
                                    validAsterix = "";
                                    if(candiPrevCompanyUANNoValidList != null && uF.parseToBoolean(candiPrevCompanyUANNoValidList.get(0))) {
                                    	validReqOpt = "validateRequired";
                                    	validAsterix = "<sup>*</sup>";
                                    }
                                    %>
                                <td class="txtlabel alignRight"> UAN No.:<%=validAsterix %></td>
                                <td> <input type="text" style=" ;" class="<%=validReqOpt%>" name="prevCompanyUANNo" value="<%=((ArrayList)alPrevEmployment.get(i)).get(18)%>" ></input></td>
                            </tr>
                            
                     <!-- ===end parvez date: 08-08-2022=== -->
                            
                            <!-- ===start parvez date: 07-09-2021=== -->
                            <tr>
                            <% List<String> candiPrevCompanyExpLetterValidList = hmValidationFields.get("CANDI_PREV_COMPANY_EXP_LETTER"); 
								validReqOpt = "";
								validAsterix = "";
								if(candiPrevCompanyExpLetterValidList != null && uF.parseToBoolean(candiPrevCompanyExpLetterValidList.get(0))) {
									validReqOpt = "validateRequired";
									validAsterix = "<sup>*</sup>";
								}
							%>
                                <td class="txtlabel alignRight"> Experience Letter:</td>
                                <td>
                                	<div id="expfile<%=i%>"></div>
                                	<input type="hidden" name="expLetterStatus" id="expLetterStatus<%=i%>" value="0">
                                	<input type="file" accept=".jpeg,.jpg,.png,.tif,.svg,.svgz,.doc,.docs,.docx,.pdf" name="experienceLetter<%=i%>" id="experienceLetter<%=i%>" onchange="fillFileStatus1('expLetterStatus<%=i%>','experienceLetter<%=i%>'); readFileURL(this, 'expfile<%=i%>');" />
                                			<!-- <a href="javascript:void(0)" onclick="addExperienceLetter('0')" class="add-font"></a> -->
								</td>
								<!-- ===start parvez date: 20-09-2021=== -->
								<td class="textblue">
		                            <% if(((ArrayList)alPrevEmployment.get(i)).get(16) != null) {%>
										<div>
										<%if(docRetriveLocation == null) { %>
											<a
												href="<%=request.getContextPath()+ IConstants.DOCUMENT_LOCATION + ((ArrayList)alPrevEmployment.get(i)).get(16)  %>"
												title="Experience Latter"><i class="fa fa-file-o"
												aria-hidden="true"></i>
											</a>
										<% } else { %>
											<a
												href="<%=docRetriveLocation +IConstants.I_CANDIDATE+"/"+IConstants.I_DOCUMENT+"/"+IConstants.I_PREVIOUS_EMPLOYMENT_DOC+"/"+(String)request.getAttribute("CandidateId")+"/"+ ((ArrayList)alPrevEmployment.get(i)).get(16) %>"
												title="Experience Latter"><i class="fa fa-file-o"
												aria-hidden="true"></i>
											</a>
										<% } %>
								</div> <% } %>
												
		                       </td>
		                       <!-- ===end parvez date: 20-09-2021=== -->
                            </tr>
                            <!-- ===end parvez date: 07-09-2021=== -->
                            <tr>
                                <td colspan="2" style="margin:0px 5px 0px 0px"><span style="float: right;"> 
                                    <%if(i>0){ %>
                                    <a href="javascript:void(0)" onclick="removePrevEmployment(this.id)" id=<%=i%> class="remove-font" ></a>
                                    <%} %>
                                    <a href=javascript:void(0) onclick="addPrevEmployment()" class=add-font></a></span>
                                </td>
                            </tr>
                        </table>
                    </div>
                    <%}
                        }else { %>
                    <div id="col_prev_employer" style="float: left;">
                        <table class="table table_no_border form-table">
                            <tr>
                                <% List<String> candiPrevCompanyNameValidList = hmValidationFields.get("CANDI_PREV_COMPANY_NAME"); 
                                    validReqOpt = "";
                                    validAsterix = "";
                                    if(candiPrevCompanyNameValidList != null && uF.parseToBoolean(candiPrevCompanyNameValidList.get(0))) {
                                    	validReqOpt = "validateRequired";
                                    	validAsterix = "<sup>*</sup>";
                                    }
                                    %>
                                <td class="txtlabel alignRight"> Company Name:<%=validAsterix %></td>
                                <!-- ===start parvez date: 14-09-2021=== name="prevCompanyLocation"-->
                                <td><input type="text" name="prevCompanyName" class="<%=validReqOpt%>" onchange="prevCompanyExpFile('0',this.value);"></input></td>
                                <!-- ===end parvez date: 14-09-2021=== -->
                            </tr>
                            <tr>
                                <% List<String> candiPrevCompanyLocationValidList = hmValidationFields.get("CANDI_PREV_COMPANY_LOCATION"); 
                                    validReqOpt = "";
                                    validAsterix = "";
                                    if(candiPrevCompanyLocationValidList != null && uF.parseToBoolean(candiPrevCompanyLocationValidList.get(0))) {
                                    	validReqOpt = "validateRequired";
                                    	validAsterix = "<sup>*</sup>";
                                    }
                                    %>
                                <td class="txtlabel alignRight"> Location:<%=validAsterix %></td>
                                <td> <input type="text" class="<%=validReqOpt%>" name="prevCompanyLocation"></input></td>
                            </tr>
                            <tr>
                                <% List<String> candiPrevCompanyCityValidList = hmValidationFields.get("CANDI_PREV_COMPANY_CITY"); 
                                    validReqOpt = "";
                                    validAsterix = "";
                                    if(candiPrevCompanyCityValidList != null && uF.parseToBoolean(candiPrevCompanyCityValidList.get(0))) {
                                    	validReqOpt = "validateRequired";
                                    	validAsterix = "<sup>*</sup>";
                                    }
                                    %>
                                <td class="txtlabel alignRight"> City: <%=validAsterix %></td>
                                <td><input type="text" style=" ;" class="<%=validReqOpt%>" name="prevCompanyCity" ></input></td>
                            </tr>
                            <tr>
                                <% List<String> candiPrevCompanyStateValidList = hmValidationFields.get("CANDI_PREV_COMPANY_STATE"); 
                                    validReqOpt = "";
                                    validAsterix = "";
                                    if(candiPrevCompanyStateValidList != null && uF.parseToBoolean(candiPrevCompanyStateValidList.get(0))) {
                                    	validReqOpt = "validateRequired";
                                    	validAsterix = "<sup>*</sup>";
                                    }
                                    %>
                                <td class="txtlabel alignRight"> State:<%=validAsterix %></td>
                                <td> <input type="text" style=" ;"class="<%=validReqOpt%>" name="prevCompanyState" ></input></td>
                            </tr>
                            <tr>
                                <% List<String> candiPrevCompanyCountryValidList = hmValidationFields.get("CANDI_PREV_COMPANY_COUNRTY"); 
                                    validReqOpt = "";
                                    validAsterix = "";
                                    if(candiPrevCompanyCountryValidList != null && uF.parseToBoolean(candiPrevCompanyCountryValidList.get(0))) {
                                    	validReqOpt = "validateRequired";
                                    	validAsterix = "<sup>*</sup>";
                                    }
                                    %>
                                <td class="txtlabel alignRight"> Country:<%=validAsterix %></td>
                                <td> <input type="text" style=" ;" class="<%=validReqOpt%>" name="prevCompanyCountry" ></input></td>
                            </tr>
                            <tr>
                                <% List<String> candiPrevCompanyContactNoValidList = hmValidationFields.get("CANDI_PREV_COMPANY_CONTACT_NO"); 
                                    validReqOpt = "";
                                    validAsterix = "";
                                    if(candiPrevCompanyContactNoValidList != null && uF.parseToBoolean(candiPrevCompanyContactNoValidList.get(0))) {
                                    	validReqOpt = "validateRequired";
                                    	validAsterix = "<sup>*</sup>";
                                    }
                                    %>
                                <td class="txtlabel alignRight"> Contact Number:<%=validAsterix %></td>
                                <td> <input type="text" onkeypress="return isNumberKey(event)" class="<%=validReqOpt%>" name="prevCompanyContactNo" ></input></td>
                            </tr>
                            <tr>
                                <% List<String> candiPrevCompanyReportingToValidList = hmValidationFields.get("CANDI_PREV_COMPANY_REPORTING_TO"); 
                                    validReqOpt = "";
                                    validAsterix = "";
                                    if(candiPrevCompanyReportingToValidList != null && uF.parseToBoolean(candiPrevCompanyReportingToValidList.get(0))) {
                                    	validReqOpt = "validateRequired";
                                    	validAsterix = "<sup>*</sup>";
                                    }
                                    %>
                                <td class="txtlabel alignRight"> Reporting To:<%=validAsterix %></td>
                                <td> <input type="text" style=" ;" class="<%=validReqOpt%>" name="prevCompanyReportingTo" ></input></td>
                            </tr>
                            <tr>
                                <% List<String> candiPrevCompanyReportingToPhoneNoValidList = hmValidationFields.get("CANDI_PREV_COMPANY_REPORTING_TO_PHONE_NO"); 
                                    validReqOpt = "";
                                    validAsterix = "";
                                    if(candiPrevCompanyReportingToPhoneNoValidList != null && uF.parseToBoolean(candiPrevCompanyReportingToPhoneNoValidList.get(0))) {
                                    	validReqOpt = "validateRequired";
                                    	validAsterix = "<sup>*</sup>";
                                    }
                                    %>
                                <td class="txtlabel alignRight">Reporting Manager Phone Number:<%=validAsterix %></td>
                                <td> <input type="text" onkeypress="return isNumberKey(event)" class="<%=validReqOpt%>" name="prevCompanyReportManagerPhNo"></input></td>
                            </tr>
                            <tr>
                                <% List<String> candiPrevCompanyHrManagerValidList = hmValidationFields.get("CANDI_PREV_COMPANY_HR_MANAGER"); 
                                    validReqOpt = "";
                                    validAsterix = "";
                                    if(candiPrevCompanyHrManagerValidList != null && uF.parseToBoolean(candiPrevCompanyHrManagerValidList.get(0))) {
                                    	validReqOpt = "validateRequired";
                                    	validAsterix = "<sup>*</sup>";
                                    }
                                    %>
                                <td class="txtlabel alignRight">HR Manager:<%=validAsterix %></td>
                                <td> <input type="text" style=" ;" class="<%=validReqOpt%>" name="prevCompanyHRManager"></input></td>
                            </tr>
                            <tr>
                                <% List<String> candiPrevCompanyHrManagerPhoneNoValidList = hmValidationFields.get("CANDI_PREV_COMPANY_HR_MANAGER_PHONE_NO"); 
                                    validReqOpt = "";
                                    validAsterix = "";
                                    if(candiPrevCompanyHrManagerPhoneNoValidList != null && uF.parseToBoolean(candiPrevCompanyHrManagerPhoneNoValidList.get(0))) {
                                    	validReqOpt = "validateRequired";
                                    	validAsterix = "<sup>*</sup>";
                                    }
                                    %>
                                <td class="txtlabel alignRight">HR Manager Phone Number:<%=validAsterix %></td>
                                <td> <input type="text" onkeypress="return isNumberKey(event)" class="<%=validReqOpt%>" name="prevCompanyHRManagerPhNo"></input></td>
                            </tr>
                            <tr>
                                <% List<String> candiPrevCompanyFromDateValidList = hmValidationFields.get("CANDI_PREV_COMPANY_FROM_DATE"); 
                                    validReqOpt = "";
                                    validAsterix = "";
                                    if(candiPrevCompanyFromDateValidList != null && uF.parseToBoolean(candiPrevCompanyFromDateValidList.get(0))) {
                                    	validReqOpt = "validateRequired";
                                    	validAsterix = "<sup>*</sup>";
                                    }
                                    %>
                                <td class="txtlabel alignRight"> From:<%=validAsterix %></td>
                                <td> <input type="text" style=" ;" class="<%=validReqOpt%>" name="prevCompanyFromDate" ></input></td>
                            </tr>
                            <tr>
                                <% List<String> candiPrevCompanyToDateValidList = hmValidationFields.get("CANDI_PREV_COMPANY_TO_DATE"); 
                                    validReqOpt = "";
                                    validAsterix = "";
                                    if(candiPrevCompanyToDateValidList != null && uF.parseToBoolean(candiPrevCompanyToDateValidList.get(0))) {
                                    	validReqOpt = "validateRequired";
                                    	validAsterix = "<sup>*</sup>";
                                    }
                                    %>
                                <td class="txtlabel alignRight"> To:<%=validAsterix %></td>
                                <td> <input type="text" style=" ;" class="<%=validReqOpt%>" name="prevCompanyToDate" ></input></td>
                            </tr>
                            <tr>
                                <% List<String> candiPrevCompanyDesignationValidList = hmValidationFields.get("CANDI_PREV_COMPANY_DESIGNATION"); 
                                    validReqOpt = "";
                                    validAsterix = "";
                                    if(candiPrevCompanyDesignationValidList != null && uF.parseToBoolean(candiPrevCompanyDesignationValidList.get(0))) {
                                    	validReqOpt = "validateRequired";
                                    	validAsterix = "<sup>*</sup>";
                                    }
                                    %>
                                <td class="txtlabel alignRight"> Designation:<%=validAsterix %></td>
                                <td> <input type="text" style=" ;" class="<%=validReqOpt%>" name="prevCompanyDesination" ></input></td>
                            </tr>
                            <tr>
                                <% List<String> candiPrevCompanyResponsibilityValidList = hmValidationFields.get("CANDI_PREV_COMPANY_RESPONSIBILITY"); 
                                    validReqOpt = "";
                                    validAsterix = "";
                                    if(candiPrevCompanyResponsibilityValidList != null && uF.parseToBoolean(candiPrevCompanyResponsibilityValidList.get(0))) {
                                    	validReqOpt = "validateRequired";
                                    	validAsterix = "<sup>*</sup>";
                                    }
                                    %>
                                <td class="txtlabel alignRight"> Responsibility:<%=validAsterix %></td>
                                <td> <input type="text" style=" ;" class="<%=validReqOpt%>" name="prevCompanyResponsibilities" ></input>  </td>
                            </tr>
                            <tr>
                                <% List<String> candiPrevCompanySkillsValidList = hmValidationFields.get("CANDI_PREV_COMPANY_SKILLS"); 
                                    validReqOpt = "";
                                    validAsterix = "";
                                    if(candiPrevCompanySkillsValidList != null && uF.parseToBoolean(candiPrevCompanySkillsValidList.get(0))) {
                                    	validReqOpt = "validateRequired";
                                    	validAsterix = "<sup>*</sup>";
                                    }
                                    %>
                                <td class="txtlabel alignRight"> Skills:<%=validAsterix %></td>
                                <td> <input type="text" style=" ;" class="<%=validReqOpt%>" name="prevCompanySkills" ></input></td>
                            </tr>
                     <!-- ===start parvez date: 08-08-2022=== -->
                            <tr>
                                <% List<String> candiPrevCompanyESICNoValidList = hmValidationFields.get("CANDI_PREV_COMPANY_ESIC_NO"); 
                                    validReqOpt = "";
                                    validAsterix = "";
                                    if(candiPrevCompanyESICNoValidList != null && uF.parseToBoolean(candiPrevCompanyESICNoValidList.get(0))) {
                                    	validReqOpt = "validateRequired";
                                    	validAsterix = "<sup>*</sup>";
                                    }
                                    %>
                                <td class="txtlabel alignRight"> ESIC No.:<%=validAsterix %></td>
                                <td> <input type="text" style=" ;" class="<%=validReqOpt%>" name="prevCompanyESICNo" ></input></td>
                            </tr>
                            
                            <tr>
                                <% List<String> candiPrevCompanyUANNoValidList = hmValidationFields.get("CANDI_PREV_COMPANY_UAN_NO"); 
                                    validReqOpt = "";
                                    validAsterix = "";
                                    if(candiPrevCompanyUANNoValidList != null && uF.parseToBoolean(candiPrevCompanyUANNoValidList.get(0))) {
                                    	validReqOpt = "validateRequired";
                                    	validAsterix = "<sup>*</sup>";
                                    }
                                    %>
                                <td class="txtlabel alignRight"> UAN No.:<%=validAsterix %></td>
                                <td> <input type="text" style=" ;" class="<%=validReqOpt%>" name="prevCompanyUANNo" ></input></td>
                            </tr>
                            
                     <!-- ===end parvez date: 08-08-2022=== -->       
                            
                            <tr>
                            <% List<String> candiPrevCompanyExpLetterValidList = hmValidationFields.get("CANDI_PREV_COMPANY_EXP_LETTER"); 
								validReqOpt = "";
								validAsterix = "";
								if(candiPrevCompanyExpLetterValidList != null && uF.parseToBoolean(candiPrevCompanyExpLetterValidList.get(0))) {
									validReqOpt = "validateRequired";
									validAsterix = "<sup>*</sup>";
								}
							%>
                                <td class="txtlabel alignRight"> Experience Letter:</td>
                                <td>
                                	<div id="expfile0"></div>
                                	<input type="hidden" name="expLetterStatus" id="expLetterStatus0" value="0">
                                	<input type="file" accept=".jpeg,.jpg,.png,.tif,.svg,.svgz,.doc,.docs,.docx,.pdf" name="experienceLetter0" id="experienceLetter0" onchange="fillFileStatus1('expLetterStatus0','experienceLetter0'); readFileURL(this, 'expfile0');" />
                                			<!-- <a href="javascript:void(0)" onclick="addExperienceLetter('0')" class="add-font"></a> -->
								</td>
                            </tr>
                            
                            <tr>
                                <td colspan="2"><span style="float:right;"><a href="javascript:void(0)" onclick="addPrevEmployment()" class="add-font"></a></span></td>
                            </tr>
                        </table>
                    </div>
                    <%}%>
                </div>
                <div class="clr"></div>
                <div style="float:right;">
                    <table class="table table_no_border">
                    	<% if(operation != null && operation.equals("U")) {%>
                       		 <tr>
                                <td colspan="2" align="center">
                                	<s:submit cssClass="btn btn-primary" name="stepSubmit" value="Update" align="center" />
                                </td>
                            </tr>
                     		
                      <%}else { %>
	                        <s:if test="mode==null">
	                            <tr>
	                                <td colspan="2" align="center">
	                                	<button type="button" class="btn btn-default prev-step" onclick="callPrevious(2,'<%=request.getAttribute("CandidateId")%>','<%=(String)request.getAttribute("fromPage")%>')">Previous</button>
	                                    <s:submit cssClass="btn btn-primary" name="stepSubmit" value="Submit & Proceed" align="center" />
	                                </td>
	                            </tr>
	                        </s:if>
	                        
	                        <s:else>
	                            <tr>
	                                <td colspan="2" align="center">
	                                	<%-- <button type="button" class="btn btn-default prev-step" onclick="callPrevious(2,'<%=request.getAttribute("CandidateId")%>','<%=(String)request.getAttribute("fromPage")%>')">Previous</button> --%>
	                                    <s:submit cssClass="btn btn-primary" name="stepSubmit" value="Submit & Proceed" align="center" />
	                                </td>
	                            </tr>
	                        </s:else>
                        <%} %>
                    </table>
                </div>
            </s:form>
        </div>
        <script type="text/javascript">
            function showOtherCompTextField1(val) {
            	if(val == 'Other') {
            		document.getElementById("ref1CompOtherTR").style.display="table-row";
            	} else {
            		document.getElementById("ref1CompOtherTR").style.display="none";
            	}
            }
            
            function showOtherCompTextField2(val) {
            	if(val == 'Other') {
            		document.getElementById("ref2CompOtherTR").style.display="table-row";
            	} else {
            		document.getElementById("ref2CompOtherTR").style.display="none";
            	}
            }
            
        </script>
    </s:if>
    <s:if test="step==5 || mode=='report'">
        <div class="tab-pane active" role="tabpanel" id="rf">
            <s:form theme="simple" action="AddCandidate" id="frmReferences" method="POST" cssClass="formcss" enctype="multipart/form-data">
                <s:hidden name="show"></s:hidden>
                <s:hidden name="operation" />
                <s:hidden name="recruitId" />
                <s:hidden name="CandidateId" />
                <s:hidden name="mode" />
                <s:hidden name="step" />
                <s:hidden name="fromPage" id="fromPage" />
                <%
                    String ref1Company = (String) request.getAttribute("ref1Company");
                    String ref2Company = (String) request.getAttribute("ref2Company");
                    //System.out.println("ref1Company ===> " + ref1Company + "  ref2Company ===> " + ref2Company);
                    List<String> prevCompList = (List<String>) request.getAttribute("prevCompList");
                    //System.out.println("prevCompList ===> " + prevCompList);
                    %>
                <table border="0" class="table table_no_border">
                    <tr>
                        <td class="tdLabelheadingBg alignCenter" colspan="2"><span style="color:#68AC3B; font-size:18px;font-weight: 600;">Step 5 : </span><span style="font-weight: 600;font-size: 16px;">Enter Candidate References 1:</span></td>
                    </tr>
                    <tr>
                        <% List<String> candiReferencesNameValidList = hmValidationFields.get("CANDI_REFERENCES_NAME"); 
                            validReqOpt = "";
                            validAsterix = "";
                            if(candiReferencesNameValidList != null && uF.parseToBoolean(candiReferencesNameValidList.get(0))) {
                            	validReqOpt = "validateRequired";
                            	validAsterix = "<sup>*</sup>";
                            }
                            %>
                        <td class="txtlabel alignRight">Name:<%=validAsterix%></td>
                        <td><input type="text" name="ref1Name" style="" class="<%=validReqOpt%>" value="<%=uF.showData((String)request.getAttribute("ref1Name"), "") %>"/></td>
                    </tr>
                    <tr>
                        <% List<String> candiReferencesCompanyValidList = hmValidationFields.get("CANDI_REFERENCES_COMPANY"); 
                            validReqOpt = "";
                            validAsterix = "";
                            if(candiReferencesCompanyValidList != null && uF.parseToBoolean(candiReferencesCompanyValidList.get(0))) {
                            	validReqOpt = "validateRequired";
                            	validAsterix = "<sup>*</sup>";
                            }
                            %>
                        <td class="txtlabel alignRight">Company:<%=validAsterix%></td>
                        <td>
                            <select name="ref1Company" class="<%=validReqOpt%>" onchange="showOtherCompTextField1(this.value);">
                                <option value="">Select Company</option>
                                <%
                                    int cnt1 = 0;
                                    for(int i=0; prevCompList != null && !prevCompList.isEmpty() && i<prevCompList.size(); i++) {
                                    	if(prevCompList.get(i).equals(ref1Company)) {
                                    		cnt1++;
                                    %>
                                <option value="<%=prevCompList.get(i) %>" selected="selected"><%=prevCompList.get(i) %></option>
                                <% } else { %>
                                <option value="<%=prevCompList.get(i) %>"><%=prevCompList.get(i) %></option>
                                <% } %>
                                <% } %>
                                <option value="Other" <%if(ref1Company != null && !ref1Company.equals("") && cnt1 == 0) { %>selected="selected" <% } %>>Other</option>
                            </select>
                        </td>
                    </tr>
                    <tr id="ref1CompOtherTR" style="display: <%if(ref1Company != null && !ref1Company.equals("") && cnt1 == 0) { %>table-row; <% } else { %> none; <% } %>">
                        <td></td>
                        <td>
                            <input type="text" class="<%=validReqOpt%>" name="ref1CompanyOther" <%if(ref1Company != null && !ref1Company.equals("") && cnt1 == 0) { %>value="<%=ref1Company %>" <% } %>/>
                        </td>
                    </tr>
                    <tr>
                        <% List<String> candiReferencesDesignationValidList = hmValidationFields.get("CANDI_REFERENCES_DESIGNATION"); 
                            validReqOpt = "";
                            validAsterix = "";
                            if(candiReferencesDesignationValidList != null && uF.parseToBoolean(candiReferencesDesignationValidList.get(0))) {
                            	validReqOpt = "validateRequired";
                            	validAsterix = "<sup>*</sup>";
                            }
                            %>
                        <td class="txtlabel alignRight">Designation:<%=validAsterix%></td>
                        <td><input type="text" name="ref1Designation" class="<%=validReqOpt%>" value="<%=uF.showData((String)request.getAttribute("ref1Designation"), "") %>"/></td>
                    </tr>
                    <tr>
                        <% List<String> candiReferencesContactNoValidList = hmValidationFields.get("CANDI_REFERENCES_CONTACT_NO"); 
                            validReqOpt = "";
                            validAsterix = "";
                            if(candiReferencesContactNoValidList != null && uF.parseToBoolean(candiReferencesContactNoValidList.get(0))) {
                            	validReqOpt = "validateRequired";
                            	validAsterix = "<sup>*</sup>";
                            }
                            %>
                        <td class="txtlabel alignRight">Contact No:<%=validAsterix%></td>
                        <td><input type="text" name="ref1ContactNo" class="<%=validReqOpt%>" onkeypress="return isNumberKey(event)" value="<%=uF.showData((String)request.getAttribute("ref1ContactNo"), "") %>"/></td>
                    </tr>
                    <tr>
                        <% List<String> candiReferencesEmailIdValidList = hmValidationFields.get("CANDI_REFERENCES_EMAIL_ID"); 
                            validReqOpt = "";
                            validAsterix = "";
                            if(candiReferencesEmailIdValidList != null && uF.parseToBoolean(candiReferencesEmailIdValidList.get(0))) {
                            	validReqOpt = "validateRequired";
                            	validAsterix = "<sup>*</sup>";
                            }
                            %>
                        <td class="txtlabel alignRight">Email Id:<%=validAsterix%></td> 
                        <td><input type="text" class="<%=validReqOpt%>" name="ref1Email" value="<%=uF.showData((String)request.getAttribute("ref1Email"), "") %>"/></td>
                    </tr>
                </table>
                <table border="0" class="table table_no_border form-table">
                    <tr>
                        <td>
                        </td>
                    </tr>
                    <tr>
                        <td class="tdLabelheadingBg alignCenter" colspan="2"><span style="font-weight: 600;font-size: 16px;">Enter Candidate References 2:</span></td>
                    </tr>
                    <tr>
                        <% List<String> candiReferencesName2ValidList = hmValidationFields.get("CANDI_REFERENCES_NAME"); 
                            validReqOpt = "";
                            validAsterix = "";
                            if(candiReferencesName2ValidList != null && uF.parseToBoolean(candiReferencesName2ValidList.get(0))) {
                            	validReqOpt = "validateRequired";
                            	validAsterix = "<sup>*</sup>";
                            }
                            %>
                        <td class="txtlabel alignRight">Name:<%=validAsterix%></td>
                        <td><input type="text" class="<%=validReqOpt%>" name="ref2Name" value="<%=uF.showData((String)request.getAttribute("ref2Name"), "") %>" /></td>
                    </tr>
                    <tr>
                        <% List<String> candiReferencesCompany2ValidList = hmValidationFields.get("CANDI_REFERENCES_COMPANY"); 
                            validReqOpt = "";
                            validAsterix = "";
                            if(candiReferencesCompany2ValidList != null && uF.parseToBoolean(candiReferencesCompany2ValidList.get(0))) {
                            	validReqOpt = "validateRequired";
                            	validAsterix = "<sup>*</sup>";
                            }
                            %>
                        <td class="txtlabel alignRight">Company:<%=validAsterix%></td>
                        <td>
                            <select name="ref2Company" class="<%=validReqOpt%>" onchange="showOtherCompTextField2(this.value);">
                                <option value="">Select Company</option>
                                <%
                                    int cnt2 = 0;
                                    for(int i=0; prevCompList != null && !prevCompList.isEmpty() && i<prevCompList.size(); i++) {
                                    	if(prevCompList.get(i).equals(ref2Company)) {
                                    		cnt2++;
                                    %>
                                <option value="<%=prevCompList.get(i) %>" selected="selected"><%=prevCompList.get(i) %></option>
                                <% } else { %>
                                <option value="<%=prevCompList.get(i) %>"><%=prevCompList.get(i) %></option>
                                <% } %>
                                <% } %>
                                <option value="Other" <%if(ref2Company != null && !ref2Company.equals("") && cnt2==0) { %>selected="selected" <% } %>>Other</option>
                            </select>
                        </td>
                    </tr>
                    <tr id="ref2CompOtherTR" style="display: <%if(ref2Company != null && !ref2Company.equals("") && cnt2==0) { %>table-row; <% } else { %> none; <% } %>">
                        <td></td>
                        <td>
                            <input type="text" class="<%=validReqOpt%>" name="ref2CompanyOther" <%if(ref2Company != null && !ref2Company.equals("") && cnt2 == 0) { %>value="<%=ref2Company %>" <% } %>/>
                        </td>
                    </tr>
                    <tr>
                        <% List<String> candiReferences2DesignationValidList = hmValidationFields.get("CANDI_REFERENCES_DESIGNATION"); 
                            validReqOpt = "";
                            validAsterix = "";
                            if(candiReferences2DesignationValidList != null && uF.parseToBoolean(candiReferences2DesignationValidList.get(0))) {
                            	validReqOpt = "validateRequired";
                            	validAsterix = "<sup>*</sup>";
                            }
                            %>
                        <td class="txtlabel alignRight">Designation:<%=validAsterix%></td>
                        <td><input type="text" class="<%=validReqOpt%>" name="ref2Designation" value="<%=uF.showData((String)request.getAttribute("ref2Designation"), "") %>"/></td>
                    </tr>
                    <tr>
                        <% List<String> candiReferences2ContactNoValidList = hmValidationFields.get("CANDI_REFERENCES_CONTACT_NO"); 
                            validReqOpt = "";
                            validAsterix = "";
                            if(candiReferences2ContactNoValidList != null && uF.parseToBoolean(candiReferences2ContactNoValidList.get(0))) {
                            	validReqOpt = "validateRequired";
                            	validAsterix = "<sup>*</sup>";
                            }
                            %>
                        <td class="txtlabel alignRight">Contact No:<%=validAsterix%></td>
                        <td><input type="text" class="<%=validReqOpt%>" onkeypress="return isOnlyNumberKey(event)" name="ref2ContactNo" value="<%=uF.showData((String)request.getAttribute("ref2ContactNo"), "") %>"/></td>
                    </tr>
                    <tr>
                        <% List<String> candiReferences2EmailIdValidList = hmValidationFields.get("CANDI_REFERENCES_EMAIL_ID"); 
                            validReqOpt = "";
                            validAsterix = "";
                            if(candiReferences2EmailIdValidList != null && uF.parseToBoolean(candiReferences2EmailIdValidList.get(0))) {
                            	validReqOpt = "validateRequired";
                            	validAsterix = "<sup>*</sup>";
                            }
                            %>
                        <td class="txtlabel alignRight">Email Id:<%=validAsterix%></td>
                        <td><input type="text" class="<%=validReqOpt%>" name="ref2Email" value="<%=uF.showData((String)request.getAttribute("ref2Email"), "") %>"/></td>
                    </tr>
                </table>
                <div class="clr"></div>
                <div style="float:right;">
                    <table class="table table_no_border">
                    <% if(operation != null && operation.equals("U")) {%>
                       		 <tr>
                                <td colspan="2" align="center">
                                	<s:submit cssClass="btn btn-primary" name="stepSubmit" value="Update" align="center" />
                                </td>
                            </tr>
                     		
                      <%}else { %>
	                        <s:if test="mode==null">
	                            <tr>
	                                <td colspan="2" align="center">
	                                	<button type="button" class="btn btn-default prev-step" onclick="callPrevious(3,'<%=request.getAttribute("CandidateId")%>','<%=(String)request.getAttribute("fromPage")%>')">Previous</button>
	                                    <s:submit cssClass="btn btn-primary" name="stepSubmit" value="Submit & Proceed" align="center" />
	                                </td>
	                            </tr>
	                        </s:if>
	                       
	                        <s:else>
	                            <tr>
	                                <td colspan="2" align="center">
	                                <%-- <button type="button" class="btn btn-default prev-step" onclick="callPrevious(3,'<%=request.getAttribute("CandidateId")%>','<%=(String)request.getAttribute("fromPage")%>')">Previous</button> --%>
	                                    <s:submit cssClass="btn btn-primary" name="stepSubmit" value="Submit & Proceed" align="center" />
	                                </td>
	                            </tr>
	                        </s:else>
                        <%} %>
                    </table>
                </div>
            </s:form>
        </div>
    </s:if>
    <s:if test="step==6 || mode=='report'">
        <div class="tab-pane active" role="tabpanel" id="mi">
            <s:form theme="simple" action="AddCandidate" id="frmMedicalInfo" method="POST" cssClass="formcss" enctype="multipart/form-data">
                <s:hidden name="operation" />
                <s:hidden name="recruitId" />
                <s:hidden name="CandidateId" />
                <s:hidden name="mode" />
                <s:hidden name="step" />
                <s:hidden name="show"></s:hidden>
                <s:hidden name="fromPage" id="fromPage" />
                <table border="0" class="table table_no_border">
                    <tr>
                        <td  class="tdLabelheadingBg alignCenter" colspan="2"><span style="color:#68AC3B; font-size:18px;font-weight: 600;">Step 6 : </span><span style="font-weight: 600;font-size: 16px;">Medical Information </span></td>
                    </tr>
                </table>
                <table border="0" class="table table_no_border">
                    <tr>
                        <td class="tdLabelheadingBg alignCenter" colspan="2">Enter Candidate's Medical Information </td>
                    </tr>
                    <tr>
                        <td>
                            <table class="table table_no_border form-table">
                                <tr>
                                    <td class="txtlabel alignRight" style="width: 25%;">Are you now receiving medical attention:</td>
                                    <td>
                                        <s:radio list="#{'true':'Yes','false':'No'}" name="checkQue1" onclick="checkRadio(this,'text1');"></s:radio>
                                        <s:hidden name="empMedicalId1" />
                                        <s:hidden name="que1Id" value="1"></s:hidden>
                                        <s:hidden name="que1IdFileStatus" id="que1IdFileStatus" value="0"></s:hidden>
                                    </td>
                                    <% List<String> candiMedical_1ValidList = hmValidationFields.get("CANDI_MEDICAL_1"); 
                                        validReqOpt = "";
                                        validAsterix = "";
                                        if(candiMedical_1ValidList != null && uF.parseToBoolean(candiMedical_1ValidList.get(0))) {
                                        	validReqOpt = "validateRequired";
                                        	validAsterix = "<sup>*</sup>";
                                        }
                                    %>
                                    <s:if test="checkQue1==true">
								 		<td><textarea rows="7" cols="63" id="text1" name="que1Desc" class="<%=validReqOpt %> form-control "><%=uF.showData((String)request.getAttribute("que1Desc"), "") %></textarea></td>
								 		<td><input type="file" accept=".jpeg,.jpg,.png,.tif,.svg,.svgz,.doc,.docs,.docx,.pdf" name="que1DescFile" id="text1File" class="<%=validReqOpt %>"  onchange="fillFileStatus('que1IdFileStatus')"/></td>
								 		<td>
									 		<% if(request.getAttribute("que1DocName") != null && !((String)request.getAttribute("que1DocName")).equalsIgnoreCase("null") && !((String)request.getAttribute("que1DocName")).equalsIgnoreCase("")) {
									 			String empMedicalId1 = (String)request.getAttribute("empMedicalId1");
								 			%>
									 			<div id="removeDivMedicalDocument_<%=empMedicalId1 %>">
										 			
										 			<%-- <a style="float: left; padding-top: 5px;" href="<%=request.getContextPath()+"/userDocuments/"+(String)request.getAttribute("que1DocName")%>" ><img src="images1/payslip.png" title="click to download"/></a> --%>
										 			<%if(docRetriveLocation == null) { %>
														<a target="blank" style="float: left; padding-top: 5px;" href="<%=request.getContextPath()+ IConstants.DOCUMENT_LOCATION + (String)request.getAttribute("que1DocName")  %>" title="Medical Document" ><i class="fa fa-file" aria-hidden="true"></i></a>
													<% } else { %>
														<a target="blank" style="float: left; padding-top: 5px;" href="<%=docRetriveLocation +IConstants.I_CANDIDATE+"/"+IConstants.I_DOCUMENT+"/"+IConstants.I_MEDICAL+"/"+request.getAttribute("CandidateId")+"/"+ (String)request.getAttribute("que1DocName")  %>" title="Medical Document" ><i class="fa fa-file" aria-hidden="true"></i></a>
													<% } %>
										 			<%if (struserType != null && (struserType.equalsIgnoreCase(IConstants.ADMIN) || struserType.equalsIgnoreCase(IConstants.HRMANAGER))) { %>
											  			<a href="javascript:void(0)" style="float: left; padding: 0px 30px 20px 0px;" onclick="deleteMedicalDocuments('<%=empMedicalId1 %>')" class="remove-font" title="click to delete document"></a>
											  		<% } %>
											  	</div>	
									 		<% } else { %>
									 		-
									 		<% } %>
								 		</td> 
							 		</s:if>
							 		<s:else>
								 		<td><textarea rows="7" cols="63" id="text1" name="que1Desc" class="<%=validReqOpt %> form-control " disabled="disabled"><%=uF.showData((String)request.getAttribute("que1Desc"), "") %></textarea></td>
								 		<td><input type="file" accept=".jpeg,.jpg,.png,.tif,.svg,.svgz,.doc,.docs,.docx,.pdf" name="que1DescFile" id="text1File" class="<%=validReqOpt %>" disabled="disabled" onchange="fillFileStatus('que1IdFileStatus')"/></td> 
							 		</s:else>
                                </tr>
                                <tr>
                                    <td class="txtlabel alignRight">Have you had any form of serious illness or operation:</td>
                                    <td>
                                        <s:radio list="#{'true':'Yes','false':'No'}" name="checkQue2" onclick="checkRadio(this,'text2');"></s:radio>
                                        <s:hidden name="empMedicalId2" />
                                        <s:hidden name="que2Id" value="2"></s:hidden>
                                        <s:hidden name="que1IdFileStatus" id="que2IdFileStatus" value="0"></s:hidden>
                                    </td>
                                    <% List<String> candiMedical_2ValidList = hmValidationFields.get("CANDI_MEDICAL_2"); 
                                        validReqOpt = "";
                                        validAsterix = "";
                                        if(candiMedical_2ValidList != null && uF.parseToBoolean(candiMedical_2ValidList.get(0))) {
                                        	validReqOpt = "validateRequired";
                                        	validAsterix = "<sup>*</sup>";
                                        }
                                    %>
                                     <s:if test="checkQue2==true">
								 		<td><textarea rows="7" cols="63" id="text2" name="que2Desc" class="<%=validReqOpt %> form-control "><%=uF.showData((String)request.getAttribute("que2Desc"), "") %></textarea></td>
								 		<td><input type="file" accept=".jpeg,.jpg,.png,.tif,.svg,.svgz,.doc,.docs,.docx,.pdf" name="que1DescFile" id="text2File" class="<%=validReqOpt %>" onchange="fillFileStatus('que2IdFileStatus')"/></td>
								 		<td>
								 		<% if(request.getAttribute("que2DocName") != null && !((String)request.getAttribute("que2DocName")).equalsIgnoreCase("null") && !((String)request.getAttribute("que2DocName")).equalsIgnoreCase("")) { 
								 			String empMedicalId2 = (String)request.getAttribute("empMedicalId2");
								 		%>
									 		<div id="removeDivMedicalDocument_<%=empMedicalId2 %>">
									 			<%if(docRetriveLocation == null) { %>
													<a target="blank" style="float: left; padding-top: 5px;" href="<%=request.getContextPath()+ IConstants.DOCUMENT_LOCATION + (String)request.getAttribute("que2DocName")  %>" title="Medical Document" ><i class="fa fa-file" aria-hidden="true"></i></a>
												<% } else { %>
													<a target="blank" style="float: left; padding-top: 5px;" href="<%=docRetriveLocation +IConstants.I_CANDIDATE+"/"+IConstants.I_DOCUMENT+"/"+IConstants.I_MEDICAL+"/"+request.getAttribute("CandidateId")+"/"+ (String)request.getAttribute("que2DocName")  %>" title="Medical Document" ><i class="fa fa-file" aria-hidden="true"></i></a>
												<% } %>
									 			<%if (struserType != null && (struserType.equalsIgnoreCase(IConstants.ADMIN) || struserType.equalsIgnoreCase(IConstants.HRMANAGER))) { %>
										  			<a href="javascript:void(0)" style="float: left; padding: 0px 30px 20px 0px;" onclick="deleteMedicalDocuments('<%=empMedicalId2 %>')" class="remove-font" title="click to delete document"></a>
										  		<% } %>
										  	</div>
								 		<% } else { %>
								 		-
								 		<% } %>
								 		</td> 
							 		</s:if>
							 		<s:else>
								 		<td><textarea rows="7" cols="63" id="text2" name="que2Desc" class="<%=validReqOpt %> form-control" disabled="disabled"><%=uF.showData((String)request.getAttribute("que2Desc"), "") %></textarea></td>
								 		<td><input type="file" accept=".jpeg,.jpg,.png,.tif,.svg,.svgz,.doc,.docs,.docx,.pdf" name="que1DescFile" id="text2File" class="<%=validReqOpt %>" disabled="disabled" onchange="fillFileStatus('que2IdFileStatus')"/></td> 
							 		</s:else>
                                </tr>
                                
                                <tr>
                                    <td class="txtlabel alignRight">Have you had any illness in the last two years? YES/NO If YES, please give the details about the same and any absences from work:</td>
                                    <td>
                                        <s:radio list="#{'true':'Yes','false':'No'}" name="checkQue3" onclick="checkRadio(this,'text3');"></s:radio>
                                        <s:hidden name="empMedicalId3" />
                                        <s:hidden name="que3Id" value="3"></s:hidden>
                                        <s:hidden name="que1IdFileStatus" id="que3IdFileStatus" value="0"></s:hidden>
                                    </td>
                                    <% List<String> candiMedical_3ValidList = hmValidationFields.get("CANDI_MEDICAL_3"); 
                                        validReqOpt = "";
                                        validAsterix = "";
                                        if(candiMedical_3ValidList != null && uF.parseToBoolean(candiMedical_3ValidList.get(0))) {
                                        	validReqOpt = "validateRequired";
                                        	validAsterix = "<sup>*</sup>";
                                        }
                                    %>
                                    <s:if test="checkQue3==true">
								 		<td><textarea rows="7" cols="63" id="text3" name="que3Desc" class="<%=validReqOpt %> form-control "><%=uF.showData((String)request.getAttribute("que3Desc"), "") %></textarea></td>
								 		<td> <input type="file" accept=".jpeg,.jpg,.png,.tif,.svg,.svgz,.doc,.docs,.docx,.pdf" name="que1DescFile" id="text3File" class="<%=validReqOpt %>" disabled="disabled" onchange="fillFileStatus('que3IdFileStatus')" /></td>
								 		<td>
									 		<% if(request.getAttribute("que3DocName") != null && !((String)request.getAttribute("que3DocName")).equalsIgnoreCase("null") && !((String)request.getAttribute("que3DocName")).equalsIgnoreCase("")) { 
									 			String empMedicalId3 = (String)request.getAttribute("empMedicalId3");
									 		%>
										 		<div id="removeDivMedicalDocument_<%=empMedicalId3 %>">
										 			<%-- <a style="float: left; padding-top: 5px;" href="<%=request.getContextPath()+"/userDocuments/"+(String)request.getAttribute("que3DocName")%>" ><img src="images1/payslip.png" title="click to download"/></a> --%>
										 			<%if(docRetriveLocation == null) { %>
														<a target="blank" style="float: left; padding-top: 5px;" href="<%=request.getContextPath()+ IConstants.DOCUMENT_LOCATION + (String)request.getAttribute("que3DocName")  %>" title="Medical Document" ><i class="fa fa-file" aria-hidden="true"></i></a>
													<% } else { %>
														<a target="blank" style="float: left; padding-top: 5px;" href="<%=docRetriveLocation +IConstants.I_CANDIDATE+"/"+IConstants.I_DOCUMENT+"/"+IConstants.I_MEDICAL+"/"+request.getAttribute("CandidateId")+"/"+ (String)request.getAttribute("que3DocName")  %>" title="Medical Document" ><i class="fa fa-file" aria-hidden="true"></i></a>
													<% } %>
										 			<% if (struserType.equalsIgnoreCase(IConstants.ADMIN) || struserType.equalsIgnoreCase(IConstants.HRMANAGER)) { %>
											  			<a href="javascript:void(0)" style="float: left; padding: 0px 30px 20px 0px;" onclick="deleteMedicalDocuments('<%=empMedicalId3 %>')" class="remove-font" title="click to delete document"></a>
											  		<% } %>
											  	</div>	
									 		<% } else { %> 
									 			-
									 		<% } %>
								 		</td> 
							 		</s:if>
							 		<s:else>
								 		<td><textarea rows="7" cols="63" id="text3" name="que3Desc" class="<%=validReqOpt %> form-control " disabled="disabled"><%=uF.showData((String)request.getAttribute("que3Desc"), "") %></textarea></td>
								 		<td><input type="file" accept=".jpeg,.jpg,.png,.tif,.svg,.svgz,.doc,.docs,.docx,.pdf" name="que1DescFile" id="text3File" class="<%=validReqOpt %>" disabled="disabled" onchange="fillFileStatus('que3IdFileStatus')" /></td> 
							 		</s:else>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
                <div class="clr"></div>
                <div style="float:right;">
                    <table class="table table_no_border">
                    	 <% if(operation != null && operation.equals("U")) {%>
                       		 <tr>
                                <td colspan="2" align="center">
                                	<s:submit cssClass="btn btn-primary" name="stepSubmit" value="Update" align="center" />
                                </td>
                            </tr>
                     		
                      <%}else { %>
	                        <s:if test="mode==null">
	                            <tr>
	                                <td colspan="2" align="center">
	                                	<button type="button" class="btn btn-default prev-step" onclick="callPrevious(4,'<%=request.getAttribute("CandidateId")%>','<%=(String)request.getAttribute("fromPage")%>')">Previous</button>
	                                    <s:submit cssClass="btn btn-primary" name="stepSubmit" value="Submit & Proceed" align="center" />
	                                </td>
	                            </tr>
	                        </s:if>
	                        <s:else>
	                            <tr>
	                                <td colspan="2" align="center">
	                                	<%-- <button type="button" class="btn btn-default prev-step" onclick="callPrevious(4,'<%=request.getAttribute("CandidateId")%>','<%=(String)request.getAttribute("fromPage")%>')">Previous</button> --%>
	                                    <s:submit cssClass="btn btn-primary" name="stepSubmit" value="Submit & Proceed" align="center" />
	                                </td>
	                            </tr>
	                        </s:else>
                        <%} %>
                    </table>
                </div>
            </s:form>
        </div>
    </s:if>
    
    <s:if test="step==7 || mode=='report'">
        <div class="tab-pane active" role="tabpanel" id="dc">
            <form action="AddCandidate.action" id="frmDocumentation" method="POST" class="formcss" enctype="multipart/form-data">
                <div style="height: auto; width:100%; float: left; border: solid 0px black; overflow: auto;" id="div_id_docs">
                    <s:hidden name="operation" />
                    <s:hidden name="recruitId" />
                    <s:hidden name="CandidateId" />
                    <s:hidden name="mode" />
                    <s:hidden name="step" />
                    <s:hidden name="show"></s:hidden>
                    <s:hidden name="fromPage" id="fromPage" />
                    <table border="0" class="table table_no_border">
                        <tr>
                            <td  class="tdLabelheadingBg alignCenter" colspan="2"><span style="color:#68AC3B; font-size:18px;font-weight: 600;">Step 7 : </span><span style="font-weight: 600;font-size: 16px;">Attach Documents </span></td>
                        </tr>
                    </table>
                    <% if(alDocuments!=null && alDocuments.size()!=0) {
						String empId = (String)((ArrayList)alDocuments.get(0)).get(3);
					%>
                    <input type="hidden" name="empId" value="<%=empId%>" />
                    <table style="width:70%" id="row_document_table" class="table form-table">
		               <tr>
		                <td class="txtlabel alignRight" style="text-align: -moz-center"><label><b>Document Name</b></label></td>
		                <td class="txtlabel alighcenter" style="text-align: -moz-center" ><label><b>Attached Document</b></label></td>
		                <td class="txtlabel alignRight" style="text-align: -moz-center"><label><b>Added By</b></label></td>
		                <td class="txtlabel alignRight" style="text-align: -moz-center"><label><b>Entry Date</b></label></td>
		           	   </tr>  
						<% 	for(int i=0; i<alDocuments.size(); i++) { %>
						  <tr>
						  		<td class="txtlabel alignRight" style="text-align: -moz-center"><input type="hidden" name="idDocType<%=i %>" value="<%=((ArrayList)alDocuments.get(i)).get(2)%>"></input>
						  			<input type="hidden" name="docId<%=i %>" value="<%=((ArrayList)alDocuments.get(i)).get(0)%>"/> <%=((ArrayList)alDocuments.get(i)).get(1)%>
						  		</td>
						  		<td class="txtlabel alignRight" style="text-align: -moz-center">
						  		     <%String onChangeFunct = "fillFileStatus('idDoc1Status')";
						  		        if(i ==1) {
						  		        	onChangeFunct = "fillFileStatus('idDoc2Status')";
						  		        } else if(i == 2) {
						  		        	onChangeFunct = "fillFileStatus('idDoc3Status')";
						  		        }
						  		     %>
						  		     <input type="file" accept=".jpeg,.jpg,.png,.tif,.svg,.svgz,.doc,.docs,.docx,.pdf" name="idDoc<%=i%>" id="idDoc<%=i%>" class="<%=validReqOpt %>" onchange="<%=onChangeFunct%>"/>
						  		     <input type="hidden" name="idDocStatus<%=i%>" id="idDoc<%=i+1 %>Status" value="0"></input>
						  			<% if(((ArrayList)alDocuments.get(i)).get(4) != null && !((ArrayList)alDocuments.get(i)).get(4).toString().equalsIgnoreCase("null") && !((ArrayList)alDocuments.get(i)).get(4).toString().equals("")) { %>
							  			<%if(docRetriveLocation == null) { %>
											<a href="<%=request.getContextPath()+ IConstants.DOCUMENT_LOCATION + ((ArrayList)alDocuments.get(i)).get(4) %>" title="Reference Document" ><i class="fa fa-file-o" aria-hidden="true"></i></a>
										<%} else { %>
											<a href="<%=docRetriveLocation +IConstants.I_CANDIDATE+"/"+IConstants.I_DOCUMENT+"/"+IConstants.I_ATTACHMENT+"/"+(String)request.getAttribute("CandidateId")+"/"+ ((ArrayList)alDocuments.get(i)).get(4) %>" title="Reference Document" ><i class="fa fa-file-o" aria-hidden="true"></i></a>
										<%} %>
									<%} %>
						  		</td>
						  		<td class="txtlabel alignRight" style="text-align: -moz-center"><%=((ArrayList)alDocuments.get(i)).get(6)%></td>
						  		<td class="txtlabel alignRight" style="text-align: -moz-center"><%=((ArrayList)alDocuments.get(i)).get(5)%></td>
		                 </tr>
						 <% } %>
						 </table>
                    <% } else { %>
                    <div id="row_document">
                    <table class="table table-bordered form-table autoWidth">
	                    <tr>
			                <td class="txtlabel alignRight"><label><b>Document Name</b></label></td>
			                <td class="txtlabel alignRight"><label><b>Attached Document</b></label></td>
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
					 		<td class="txtlabel alignRight"><%=IConstants.DOCUMENT_RESUME%>:<%=validAsterix %>
					 		<input type="hidden" name="idDocType0" value="<%=IConstants.DOCUMENT_RESUME%>"></input>
					 		<input type="hidden" style="" value="<%=IConstants.DOCUMENT_RESUME%>" name="idDocName0" ></input>
					 		<input type="hidden" name="idDocStatus0" id="idDoc1Status" value="0"></input>
					 		</td>
					 		<!-- Created By Dattatray Date : 30-June-2021 Note : and changed  accept=".pdf,.docx,.doc,.docs"-->
							<td class="txtlabel alignRight"><input type="file" accept=".pdf,.docx,.doc,.docs" name="idDoc0" id="idDoc0" class="<%=validReqOpt %>" onchange="fillFileStatus('idDoc1Status')"/></td>
	                    </tr>
					 	<tr>
					 		<% 	List<String> candiDocIdProofValidList = hmValidationFields.get("CANDI_DOC_IDENTITY_PROOF"); 
								validReqOpt = "";
								validAsterix = "";
								if(candiDocIdProofValidList != null && uF.parseToBoolean(candiDocIdProofValidList.get(0))) {
									validReqOpt = "validateRequired";
									validAsterix = "<sup>*</sup>";
								}
							%>
					 		<td class="txtlabel alignRight"><%=IConstants.DOCUMENT_ID_PROOF%>:<%=validAsterix %>
					 		<input type="hidden" name="idDocType1" value="<%=IConstants.DOCUMENT_ID_PROOF%>"></input>
					 		<input type="hidden" value="<%=IConstants.DOCUMENT_ID_PROOF%>" style="" name="idDocName1" ></input>
					 		<input type="hidden" name="idDocStatus1" id="idDoc2Status" value="0"></input>
					 		</td>
					 		<!-- Created By Dattatray Date : 30-June-2021 Note : changed  accept=".jpeg,.jpg,.png,.svg"-->
							<td class="txtlabel alignRight"><input type="file" accept=".jpeg,.jpg,.png,.svg" name="idDoc1" id="idDoc1" class="<%=validReqOpt %>" onchange="fillFileStatus('idDoc2Status')"/></td>
	                   	</tr>
					 	<tr>
					 		<% 	List<String> candiDocAddressProofValidList = hmValidationFields.get("CANDI_DOC_ADDRESS_PROOF"); 
								validReqOpt = "";
								validAsterix = "";
								if(candiDocAddressProofValidList != null && uF.parseToBoolean(candiDocAddressProofValidList.get(0))) {
									validReqOpt = "validateRequired";
									validAsterix = "<sup>*</sup>";
								}
							%>
					 		<td class="txtlabel alignRight"><%=IConstants.DOCUMENT_ADDRESS_PROOF%>:<%=validAsterix %>
					 		<input type="hidden" name="idDocType2" value="<%=IConstants.DOCUMENT_ADDRESS_PROOF%>"></input>
					 		<input type="hidden" value="<%=IConstants.DOCUMENT_ADDRESS_PROOF%>" style="" name="idDocName2" ></input>
					 		<input type="hidden" name="idDocStatus2" id="idDoc3Status" value="0"></input>
					 		</td>
					 		<!-- Created By Dattatray Date : 30-June-2021 Note : duplicate name="idDoc2" to id="idDoc2" and changed  accept=".jpeg,.jpg,.png,.svg"-->
							<td class="txtlabel alignRight"><input type="file" accept=".jpeg,.jpg,.png,.svg" name="idDoc2" id="idDoc2" class="<%=validReqOpt %>" onchange="fillFileStatus('idDoc3Status')"/></td>
				    	</tr>
				   <!-- ===start parvez date: 28-10-2022=== -->
				   		<% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_COMPANY_PROFILE_DOCUMENT))){ %>		
					   		
					   		<tr>
						 		<% 	List<String> candiDocCompanyProfileValidList = hmValidationFields.get("CANDI_DOC_COMPANY_PROFILE"); 
									validReqOpt = "";
									validAsterix = "";
									if(candiDocCompanyProfileValidList != null && uF.parseToBoolean(candiDocCompanyProfileValidList.get(0))) {
										validReqOpt = "validateRequired";
										validAsterix = "<sup>*</sup>";
									}
								%>
						 		<td class="txtlabel alignRight"><%=IConstants.DOCUMENT_COMPANY_PROFILE%>:<%=validAsterix %>
						 		<input type="hidden" name="idDocType3" value="<%=IConstants.DOCUMENT_COMPANY_PROFILE%>"></input>
						 		<input type="hidden" value="<%=IConstants.DOCUMENT_COMPANY_PROFILE%>" style="" name="idDocName3" ></input>
						 		<input type="hidden" name="idDocStatus3" id="idDoc4Status" value="0"></input>
						 		</td>
						 		<td class="txtlabel alignRight"><input type="file" accept=".jpeg,.jpg,.png,.svg,.pdf,.docx,.doc,.docs" name="idDoc3" id="idDoc3" class="<%=validReqOpt %>" onchange="fillFileStatus('idDoc4Status')"/></td>
					    	</tr>
				    	<% } %>	
				   <!-- ===end parvez date: 28-10-2022=== -->
				    </table>
                        <%-- <table class="table table-bordered form-table autoWidth">
                            <tr>
                                <td class="txtlabel alignRight"><label><b>Document Name</b></label></td>
                                <td class="txtlabel "><label><b>Attached Document</b></label></td>
                            </tr>
                            <tr>
                                <% List<String> candiDocResumeValidList = hmValidationFields.get("CANDI_DOC_RESUME"); 
                                    validReqOpt = "";
                                    validAsterix = "";
                                    if(candiDocResumeValidList != null && uF.parseToBoolean(candiDocResumeValidList.get(0))) {
                                    	validReqOpt = "validateRequired";
                                    	validAsterix = "<sup>*</sup>";
                                    }
                                    %>
                                <td class="txtlabel alignRight"><%=IConstants.DOCUMENT_RESUME%>:<%=validAsterix %>
                                    <input type="hidden" name="idDocType" value="<%=IConstants.DOCUMENT_RESUME%>"></input>
                                    <input type="hidden" style=";" value="<%=IConstants.DOCUMENT_RESUME%>" name="idDocName" ></input>
                                    <input type="hidden" name="idDocStatus" id="idDoc1Status" value="0"></input>
                                </td>
                                <td class="txtlabel alignRight">
                                	<div id="doc1"></div>
                                    <input type="file" name="idDoc" class="<%=validReqOpt%>   " onchange="readFileURL(this, 'doc1');fillFileStatus('idDoc1Status')"/>
                                </td>
                            <tr>
                                <% List<String> candiDocIdentityProofValidList = hmValidationFields.get("CANDI_DOC_IDENTITY_PROOF"); 
                                    validReqOpt = "";
                                    validAsterix = "";
                                    if(candiDocIdentityProofValidList != null && uF.parseToBoolean(candiDocIdentityProofValidList.get(0))) {
                                    	validReqOpt = "validateRequired";
                                    	validAsterix = "<sup>*</sup>";
                                    }
                                    %>
                                <td class="txtlabel alignRight"><%=IConstants.DOCUMENT_ID_PROOF%>:<%=validAsterix %>
                                    <input type="hidden" name="idDocType" value="<%=IConstants.DOCUMENT_ID_PROOF%>"></input>
                                    <input type="hidden" value="<%=IConstants.DOCUMENT_ID_PROOF%>" style=";" name="idDocName" ></input>
                                    <input type="hidden" name="idDocStatus" id="idDoc2Status" value="0"></input>
                                </td>
                                <td class="txtlabel alignRight"><div id="doc2"></div><input type="file" name="idDoc" class="<%=validReqOpt%>   " onchange="readFileURL(this, 'doc2');fillFileStatus('idDoc2Status')"/></td>
                            <tr>
                                <% List<String> candiDocAddressProofValidList = hmValidationFields.get("CANDI_DOC_ADDRESS_PROOF"); 
                                    validReqOpt = "";
                                    validAsterix = "";
                                    if(candiDocAddressProofValidList != null && uF.parseToBoolean(candiDocAddressProofValidList.get(0))) {
                                    	validReqOpt = "validateRequired";
                                    	validAsterix = "<sup>*</sup>";
                                    }
                                    %>
                                <td class="txtlabel alignRight"><%=IConstants.DOCUMENT_ADDRESS_PROOF%>:<%=validAsterix %>
                                    <input type="hidden" name="idDocType" value="<%=IConstants.DOCUMENT_ADDRESS_PROOF%>"></input>
                                    <input type="hidden" value="<%=IConstants.DOCUMENT_ADDRESS_PROOF%>" style=";" name="idDocName" ></input>
                                    <input type="hidden" name="idDocStatus" id="idDoc3Status" value="0"></input>
                                </td>
                                <td class="txtlabel alignRight"><div id="doc3"></div><input type="file" name="idDoc" class="<%=validReqOpt%>" onchange="readFileURL(this, 'doc3');fillFileStatus('idDoc3Status')"/></td>
                            </tr>
                        </table> --%>
                    </div>
                    <% } %>
                    
                    <div class="clr"></div>
                    <div style="float:right;">
                        <table class="table table_no_border">
                  <!-- ===start parvez date: 09-08-2022=== -->      
                        <% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_DISABLE_AVAILABILITY_OF_INTERVIEW))){ %>
                        	<tr>
                        		<td colspan="2" align="center">
	                               <s:submit cssClass="btn btn-primary" name="stepSubmit" value="Submit" align="center" />
	                            </td>
	                        </tr>
                        <% } else { %>
	                        <% if(operation != null && operation.equals("U")) {%>
		                       		 <tr>
		                                <td colspan="2" align="center">
		                                	<s:submit cssClass="btn btn-primary" name="stepSubmit" value="Update" align="center" />
		                                </td>
		                            </tr>
	                     		
	                         <% } else { %>
		                            <s:if test="mode==null">
		                                <tr>
		                                    <td colspan="2" align="center">
		                                    	<button type="button" class="btn btn-default prev-step" onclick="callPrevious(5,'<%=request.getAttribute("CandidateId")%>','<%=(String)request.getAttribute("fromPage")%>')">Previous</button>
		                                        <s:submit cssClass="btn btn-primary" name="stepSubmit" value="Submit & Proceed" align="center" />
		                                    </td>
		                                </tr>
		                            </s:if>
		                           
		                            <s:else>
		                                <tr>
		                                    <td colspan="2" align="center">
		                                        <s:submit cssClass="btn btn-primary" name="stepSubmit" value="Submit & Proceed" align="center" />
		                                    </td>
		                                </tr>
		                            </s:else>
	                            <% } %>
                            <% } %>
                        <!-- ===end parvez date: 09-08-2022=== -->    
                        </table>
                    </div>
                </div>
            </form>
        </div>
    </s:if>
    <s:if test="step==8 || mode=='report'">
        <div class="tab-pane active" role="tabpanel" id="av">
            <script>
                $(function() {
                	$("input[name=strDate]").datepicker({format: 'dd/mm/yyyy'});
                	$( "input[name=strTime]" ).datetimepicker({format: 'HH:mm'});
                });
            </script>
            <form action="AddCandidate.action" id="frmAvailibility" method="POST" class="formcss">
                <s:hidden name="operation"/>
                <s:hidden name="recruitId" id="recruitId"/>
                <s:hidden name="CandidateId"/>
                <s:hidden name="mode"/>
                <s:hidden name="step"/>
                <s:hidden name="candibymail"/>
                <s:hidden name="show"></s:hidden>
                <s:hidden name="fromPage" id="fromPage" />
                <%if(strUserType!=null && strUserType.equalsIgnoreCase(IConstants.HRMANAGER)){ %>
                <table border="0" class="table table_no_border" style="font-size: 12px">
                    <tr>
                        <td  class="tdLabelheadingBg alignCenter" colspan="2"><span style="color:#68AC3B; font-size:18px"></span>  <%=uF.showData((String)request.getAttribute("CandidateName"), "") %> Availability for interview:-</td>
                    </tr>
                    <tr>
                        <td colspan="3">Please enter  <%=uF.showData((String)request.getAttribute("CandidateName"), "") %> availability details. Interview would be sheduled based on any of the available time specified by you. </td>
                    </tr>
                    <tr>
                        <td colspan="3">Please ignore this step if <%=uF.showData((String)request.getAttribute("CandidateName"), "") %> have already given Interview. </td>
                    </tr>
                </table>
                <%}else{ %>
                <table border="0" class="table table_no_border" style="font-size: 12px">
                    <tr>
                        <td  class="tdLabelheadingBg alignCenter" colspan="2"><span style="color:#68AC3B; font-size:18px;font-weight: 600;">Step 8 : </span><span style="font-weight: 600;font-size: 16px;"> Your Availability for interview:-</span></td>
                    </tr>
                    <tr>
                        <td colspan="3">Please enter your availability details. You will be scheduled for an interview based on any of the available time specified by you. </td>
                    </tr>
                    <tr>
                        <td colspan="3">Please ignore this step if you have alreay joined the organisation. </td>
                    </tr>
                </table>
                <%} %>
                <div style="float:left;">
                    <table style="width: 500px;" class="table table_no_border">
                        <tr>
                            <th>&nbsp;</th>
                            <th class="txtlabel alignRight">Date</th>
                            <th class="txtlabel" align="left">Time</th>
                        </tr>
                        <%
                            Map<String,String> hmDatesSelected	= (Map<String,String>)request.getAttribute("hmDatesSelected");
                            if(hmDatesSelected == null) hmDatesSelected = new LinkedHashMap<String, String>();
                            Map<String,String> hmDatesRejected	= (Map<String,String>)request.getAttribute("hmDatesRejected");
                            if(hmDatesRejected == null) hmDatesRejected = new LinkedHashMap<String, String>();
                            List<String> alDates = (List<String>)request.getAttribute("alDates");
                            //if(alDates == null) alDates = new ArrayList<String>();
                            
                            Map<String,String> hmDates = (Map<String,String>)request.getAttribute("hmDates");
                            if(hmDates == null) hmDates = new LinkedHashMap<String, String>();
                            //System.out.println("alDates ===> "+alDates);
                            //System.out.println("hmDates ===> "+hmDates);
                            
                            
                            %>
                        <tr>
                            <% List<String> candiInterviewAvailable_1ValidList = hmValidationFields.get("CANDI_INTERVIEW_AVAILABLE_1"); 
                                validReqOpt = "";
                                validAsterix = "";
                                if(candiInterviewAvailable_1ValidList != null && uF.parseToBoolean(candiInterviewAvailable_1ValidList.get(0))) {
                                		validReqOpt = "validateRequired";
                                		validAsterix = "<sup>*</sup>";
                                }
                                %>
                            <td class="txtlabel alignRight">Option 1:<%=validAsterix %></td>
                            <td align="right"><input type="text" name="strDate" class="<%=validReqOpt%>" style="width: 95px !important;"
                                <%if(alDates !=null && alDates.size()>0 && alDates.get(0)!=null) {%>
                                value="<%=alDates.get(0)%>"
                                <%} %>
                                ></td>
                            <td><input type="text" name="strTime" class="<%=validReqOpt%>" style=" width: 95px !important;"
                                <%if(alDates !=null && alDates.size()>0  && hmDates.get(alDates.get(0))!=null) {%>
                                value="<%=hmDates.get(alDates.get(0)) %>"
                                <%} %>
                                >
                            </td>
                        </tr>
                        <tr>
                            <% List<String> candiInterviewAvailable_2ValidList = hmValidationFields.get("CANDI_INTERVIEW_AVAILABLE_2"); 
                                validReqOpt = "";
                                validAsterix = "";
                                if(candiInterviewAvailable_2ValidList != null && uF.parseToBoolean(candiInterviewAvailable_2ValidList.get(0))) {
                                		validReqOpt = "validateRequired";
                                		validAsterix = "<sup>*</sup>";
                                }
                                %>
                            <td class="txtlabel alignRight">Option 2:<%=validAsterix %></td>
                            <td align="right"><input type="text" name="strDate" class="<%=validReqOpt%>" style="width: 95px !important;"
                                <%if(alDates !=null && alDates.size()>0 && alDates.get(0)!=null) {%>
                                value="<%=alDates.get(0)%>"
                                <%} %>
                                ></td>
                            <td><input type="text" name="strTime" class="<%=validReqOpt%>" style="width: 95px !important;"
                                <%if(alDates !=null && alDates.size()>0  && hmDates.get(alDates.get(0))!=null) {%>
                                value="<%=hmDates.get(alDates.get(0)) %>"
                                <%} %>
                                >
                            </td>
                        </tr>
                        <tr>
                            <% List<String> candiInterviewAvailable_3ValidList = hmValidationFields.get("CANDI_INTERVIEW_AVAILABLE_3"); 
                                validReqOpt = "";
                                validAsterix = "";
                                if(candiInterviewAvailable_3ValidList != null && uF.parseToBoolean(candiInterviewAvailable_3ValidList.get(0))) {
                                		validReqOpt = "validateRequired";
                                		validAsterix = "<sup>*</sup>";
                                }
                                %>
                            <td class="txtlabel alignRight">Option 3:<%=validAsterix%></td>
                            <td align="right"><input type="text" name="strDate" class="<%=validReqOpt%>" style="width: 95px !important;"
                                <%if(alDates !=null && alDates.size()>0 && alDates.get(0)!=null) {%>
                                value="<%=alDates.get(0)%>"
                                <%} %>
                                ></td>
                            <td><input type="text" name="strTime" class="<%=validReqOpt%>" style="width: 75px !important;"
                                <%if(alDates !=null && alDates.size()>0  && hmDates.get(alDates.get(0))!=null) {%>
                                value="<%=hmDates.get(alDates.get(0)) %>"
                                <%} %>
                                >
                            </td>
                        </tr>
                    </table>
                </div>
                <!-- 			<div style="float: left; margin: 50px; width: 40%;" id="selectedDates"> -->
                <% 	
                    if(hmDatesSelected!=null && hmDatesSelected.size()>0){%>
                <!-- 		<div style="border: 2px solid #ccc;"> -->
                <div style="float:left;border: 2px solid #ccc;margin-top:45px;">
                    <b>Selected Dates</b>
                    <table border="0" width="100%" class="table table_no_border">
                        <thead>
                            <tr>
                                <th>
                                    S.No
                                </th>
                                <th>
                                    Date
                                </th>
                                <th>
                                    Interview Person
                                </th>
                            </tr>
                        </thead>
                        <%
                            Iterator<String> it= hmDatesSelected.keySet().iterator();
                            int i=0;
                            while(it.hasNext()){
                            String key=it.next();
                            %>
                        <tr>
                            <td nowrap="nowrap" style="font-weight: bold;"><%=i+1 %></td>
                            <td align="left"><%=key %></td>
                            <td align="left"><%=hmDatesSelected.get(key) %></td>
                        </tr>
                        <%
                            i++;    
                            }
                                %>
                    </table>
                </div>
                <%} %>
                <% 	
                    if(hmDatesRejected!=null && hmDatesRejected.size()>0){%>
                <div style="float:left;border: 2px solid #ccc;margin-left:15px;margin-top:45px;">
                    <b>Rejected Dates</b>
                    <table border="0" width="100%" class="table table_no_border">
                        <thead>
                            <tr>
                                <th>
                                    S.No
                                </th>
                                <th>
                                    Date
                                </th>
                                <th>
                                    Interview Person
                                </th>
                            </tr>
                        </thead>
                        <%
                            Iterator<String> it= hmDatesRejected.keySet().iterator();
                            int i=0;
                            while(it.hasNext()){
                            String key=it.next();
                            %>
                        <tr>
                            <td nowrap="nowrap" style="font-weight: bold;"><%=i+1 %></td>
                            <td align="left"><%=key %></td>
                            <td align="left"><%=hmDatesRejected.get(key) %></td>
                        </tr>
                        <%
                            i++;    
                            }
                                %>
                    </table>
                </div>
                <%} %>
                <div style="float: right; margin-top: 200px;">
                    <table class="table table_no_border">
                        <tr>
                            <td align="center" colspan="2">
                            </td>
                        </tr>
                        <tr>
                            <td colspan="2">
                                <div align="center">
                                	<%-- <button type="button" class="btn btn-default prev-step" onclick="callPrevious(6,'<%=request.getAttribute("CandidateId")%>','<%=(String)request.getAttribute("fromPage")%>')">Previous</button> --%>
                                    <s:submit cssClass="btn btn-primary" name="stepSubmit" value="Submit" align="center" /> <!-- onclick="submitForm();" -->
                                    <!-- <input type="button" name="save" style="width: 200px; float: right;" class="btn btn-primary" value="Submit &amp; Proceed"> -->
                                    <!-- <input type="submit" style="width: 200px; float: right;" class="btn btn-primary" value="Submit &amp; Proceed" id=""> -->
                                </div>
                            </td>
                        </tr>
                    </table>
                </div>
            </form>
        </div>
    </s:if>
    <script>
        showMarriageDate();
        validateMandatory(document.frmOfficial.empType.options[document.frmOfficial.empType.options.selectedIndex].value);
    </script>
</div>
<script>
    $(function(){
        $(".prev-step").click(function (e) {
            var $active = $('.wizard .nav-tabs li.active');
            prevTab($active);
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
    function nextTab(elem) {
        $(elem).next().find('a[data-toggle="tab"]').click();
    }
    function prevTab(elem) {
        $(elem).prev().find('a[data-toggle="tab"]').click();
    }
    
</script>