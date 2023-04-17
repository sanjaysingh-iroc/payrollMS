<%@page import="ChartDirector.AngularMeter"%>
<%@page import="java.util.*"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/_rating/js/jquery.raty.js"></script>

<%-- <script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.5.0/jquery.min.js"></script> --%> 
<%-- <script type="text/javascript" src="js/jquery.gdocsviewer.min.js"></script> --%>

<!-- Created by Dattatray Date:25-June-2021 -->
<style>
.bootstrap-datetimepicker-widget.dropdown-menu {
        width: auto;
    }

    .timepicker-picker table td a span,
    .timepicker-picker table td,
    .timepicker-picker table td span {
        height: 25px !important;
        line-height: 25px !important;
        vertical-align: middle;
        width: 25px !important;
        padding: 0px !important;
    }
</style>


<script type="text/javascript"> 
	
    $(function() {
    	 /* $("img.lazy").each(function() {
    		$(this).attr("src",$(this).attr("data-original"));
    	    //$(this).removeAttr("data-original");
    	}); */ 
    	 bindDate();
    	$("img.lazy").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10});
    	
    	
    }); 
    
    $("#hrsubmit").click(function(){
		$("#frmIntCommentHR").find('.validateRequired').filter(':hidden').prop('required',false);
		$("#frmIntCommentHR").find('.validateRequired').filter(':visible').prop('required',true);
    });

    
    function bindDate(){
    	//alert("1");
    	if(document.getElementsByName("interviewdate")) {
	    	$("input[name=interviewdate]").datepicker({format: 'dd/mm/yyyy'});
	    	//alert("2");
    	}
    	if(document.getElementsByName("interviewTime")) {
	    	$('input[name=interviewTime]').datetimepicker({format: 'HH:mm'});
	    	//alert("3");
    	}
    	if(document.getElementsByName("preferedDate")) {
	    	$("input[name=preferedDate]").datepicker({format: 'dd/mm/yyyy'});
	    	//alert("4");
    	}
    	if(document.getElementsByName("preferedTime")) {
	    	$('input[name=preferedTime]').datetimepicker({format: 'HH:mm'});
	    	//alert("5"); 
    	}
    }
    
    function showOtherDateTime(){
    	var redioCheked = document.getElementById("preferedDtTmRedio0").checked;
    	if(redioCheked == true){
    		document.getElementById("fourthDateTD").style.display='table-cell';
    		//document.getElementById("fourthTimeTD").style.display='table-cell';
    	}else{
    		document.getElementById("fourthDateTD").style.display='none';
    		//document.getElementById("fourthTimeTD").style.display='none';
    	}
    	
    }
    
    function openSendConfirmation() {
    	var status = document.getElementById("status").value;
    	if(status == '0') {
    		document.getElementById("sendConfirmationTR").style.display='table-row';
    		document.getElementById("status").value = "1";
    	} else {
    		document.getElementById("sendConfirmationTR").style.display='none';
    		document.getElementById("status").value = "0";
    	}
    }
    
    	function addToCalender(panelId, recruitId, CandidateId, i, type, notiStatus, assessmentId){
    		var preferedDate="", preferedTime="",redioCheked="";
    		for(var ii =0; ii<=3; ii++){
    			var weight = document.getElementById("preferedDtTmRedio"+ii);
    			if (weight == null){ continue;}
    			redioCheked = document.getElementById("preferedDtTmRedio"+ii).checked;
    			if(redioCheked == true){
    				//alert("redioCheked t ==> "+ redioCheked+" "+ ii);
    				var weight = document.getElementById("preferedDate"+ii);
    				if (weight == null){ continue;}
    			preferedDate = document.getElementById("preferedDate"+ii).value;
    			var weight = document.getElementById("preferedTime"+ii);
    			if (weight == null){ continue;}
    			preferedTime = document.getElementById("preferedTime"+ii).value;
    			}
    		}
    		var dateinterview=document.getElementById("interviewdate"+i).value;
    		var interviewTime=document.getElementById("interviewTime"+i).value;
    		if((preferedDate == '' || preferedTime == '') && (dateinterview =='' || interviewTime=='') && type=='insert'){
    			alert("Please select date and time ");	
    		}else{
    			var action = 'Addinterviewpaneldate.action?recruitId='+ recruitId+'&CandidateId='+ CandidateId+'&interviewTime='+interviewTime+
    					'&dateinterview='+dateinterview+'&pageFrom=candidateMyProfile&type='+type+'&panelId='+panelId+'&iCount='+i+
    					'&preferedDate='+preferedDate+'&preferedTime='+preferedTime+'&notiStatus='+notiStatus+'&assessmentId='+assessmentId;
    			getContent('panelScheduleDiv', action);
    			
    			for(var j =0; j<=3; j++){
    				var weight = document.getElementById("preferedDtTmRedio"+j);
    				if (weight == null){ continue;}
    				var redioCheked = document.getElementById("preferedDtTmRedio"+j).checked;
    				if(redioCheked == true){
    					var weight = document.getElementById("preferedDtTmRedio"+j);
    					if (weight == null){ continue;}
    					document.getElementById("preferedDtTmRedio"+j).checked=false;
    				}
    			}
    			/* window.setTimeout(function() {
    				bindDate(); 
    			}, 2000); */
    			
    		}
    		/* window.setTimeout(function() {
    			$("input[name=interviewdate]").datepicker({format: 'dd/mm/yyyy'});
    			$("input[name=interviewTime]" ).timepicker({});
    			$("input[name=preferedDate]").datepicker({format: 'dd/mm/yyyy'});
    			$("input[name=preferedTime]" ).timepicker({});
    		}, 200);  */ 
    		
    	}
    
    
    function insertinterviewdate(panelId,CandidateId,recruitId){
    		
    	var dialogEdit = '.modal-body';
	   	 $(dialogEdit).empty();
	   	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	   	 $("#modalInfo").show();
	   	 $(".modal-title").html('Interveiw Schedule');
	   	$.ajax({
			url : 'CandidateInterviewSchedule.action?pagefrom=MyProfile&recruitID='+recruitId+'&candidateID='+CandidateId+'&panelEmpID='+panelId ,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
    	
    }
    
    
    
    function joiningCall(value) {
    	 if(value==1) {
    		 document.getElementById("hrsubmit").value = "Save & Send Offer";
    		 document.getElementById("preveiwOffer").style.display = 'inline';
    		document.getElementById("joiningTR").style.display='table-row';
    /* 		document.getElementById("ctcTR").style.display='table-row'; */
    		document.getElementById("ctcDisplayTR").style.display='table-row';
    
    /* ===start parvez date: 10-01-2022=== */
    		document.getElementById("joiningBonusTR").style.display='table-row';
    		document.getElementById("additionCommentTR").style.display='table-row';
    /* ===end parvez date: 10-01-2022=== */
    
    	} else {
    		document.getElementById("joiningTR").style.display='none';
    /* 		document.getElementById("ctcTR").style.display='none'; */
    		document.getElementById("ctcDisplayTR").style.display='none';
    		document.getElementById("hrsubmit").value = "Save & Reject";
    		document.getElementById("preveiwOffer").style.display = 'none';
    		
    /* ===start parvez date: 10-01-2022=== */
    		document.getElementById("joiningBonusTR").style.display='none';
    		document.getElementById("additionCommentTR").style.display='none';
    /* ===end parvez date: 10-01-2022=== */
    
    	}
    }
    	
    $(function() {
        $("#joiningdate").datepicker({format: 'dd/mm/yyyy', yearRange: '1980:2020', changeYear: true});
    });
       
    
    function viewAssessmentDetail(assessmentId, assessmentName) {
    	var dialogEdit = '.modal-body';
	   	 $(dialogEdit).empty();
	   	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	   	 $("#modalInfo").show();
	   	 $(".modal-title").html(''+assessmentName+'');
	   	$.ajax({
			url : "ViewAssessmentDetails.action?assessmentId="+assessmentId,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
    }
    
    
    function viewAssessmentScoreSummary(assessmentId, candidateId, recruitId, roundId) {
    	var dialogEdit = '.modal-body';
	   	 $(dialogEdit).empty();
	   	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	   	 $("#modalInfo").show();
	   	 $(".modal-title").html('Assessment Score Summary');
	   	$.ajax({
			url : "CandidateAssessmentScoreSummary.action?assessmentId="+assessmentId+"&candidateId="+candidateId+"&recruitId="+recruitId+"&roundId="+roundId,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
    }
    
    
    function openOtherdate(){
    	document.getElementById("otherDateDiv").style.display='block';
    }
    
    
    function SendMailToCandidate(){
    	//alert("rid .....");
    	var empid = document.getElementById("hidecandiID").value;
    	//alert("rid "+rid);
    	window.location = "SendMailToCandiForSpecifyDates.action?empId="+empid+"&type=SpecifyDates";
    }
    
    function sendAssessmentToCandidate(recruitId, roundId, assessmentId, candidateId) {
    	
    	getContent('assessSpan_'+roundId, 'SendMailToCandiForSpecifyDates.action?empId='+candidateId+'&recruitId='+recruitId+'&roundId='+roundId+'&assessmentId='+assessmentId+'&type=Assessment');
    }
    
    function showResume(documentId,action,candName){
    	//alert("candName==>"+candName);
    	var Title = candName +" Resume";
    	var dialogEdit = '.modal-body';
	   	 $(dialogEdit).empty();
	   	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	   	 $("#modalInfo").show();
	   	 $(".modal-title").html(Title);
	   	$.ajax({
			url : action,
			cache : false,
			success : function(data){
				//alert("data==>"+data);
				$(dialogEdit).html(data);
			}
			
		});
    	}
    	
    
    function revokeExistingNegotiationCommunication() {
    	//alert("empId==>"+empId);
    	if(confirm('Are you sure, you want to revoke this communication?')) {
   			var recruitId = document.getElementById("recruitId").value;
   			var CandID = document.getElementById("CandID").value;
   			xmlhttp = GetXmlHttpObject();
   		    if (xmlhttp == null) {
   				alert("Browser does not support HTTP Request");
   				return;
   		    } else {
   	            var xhr = $.ajax({
   	                url : 'CandidateMyProfilePopup.action?revokeNegotiationReq=Revoke&recruitId='+recruitId+'&CandID='+CandID,
   	                cache : false,
   	                success : function(data) {
   	            	   //alert("data ===>> " + data);
   	                }
   	            });
   		    }
  		    	
	    	var strAction = 'Applications.action?recruitId='+recruitId;
			if(form != null && form == 'C') {
				strAction = 'Calendar.action';
			}
			
			$.ajax({
				url: strAction,
				cache: true,
				success: function(result) {
					$("#subSubDivResult").html(result);
				}
			});
    	}
    }
    
    
    function changeJobProfile() {
    	//alert("empId==>"+empId);
    	if(confirm('Are you sure, you want to trasfer this candidate?')) {
    		var reason = window.prompt("Please enter your trasfer reason.");
    		if (reason != null) {
    			var recruitId = document.getElementById("recruitId").value;
    			var CandID = document.getElementById("CandID").value;
    			var newRecruitId = document.getElementById("strLiveJobs").value;
    			xmlhttp = GetXmlHttpObject();
    		    if (xmlhttp == null) {
    				alert("Browser does not support HTTP Request");
    				return;
    		    } else {
    	            var xhr = $.ajax({
	    	                url : 'CandidateMyProfilePopup.action?transferToNewJD=Transfer&recruitId='+recruitId+'&CandID='+CandID
								+'&newRecruitId='+newRecruitId+'&transferReason='+reason,
	    	                cache : false,
	    	                success : function(data) {
    	            	   //alert("data ===>> " + data);
    	                }
    	            });
    		    }
   		    	
   		    	/* $.ajax({
   					url  :'CandidateMyProfilePopup.action?transferToNewJD=Transfer&recruitId='+recruitId+'&CandID='+CandID
   							+'&newRecruitId='+newRecruitId+'&transferReason='+reason,
   					cache: true
   				}); */
    	     
   		    	var strAction = 'Applications.action?recruitId='+recruitId;
   				if(form != null && form == 'C') {
   					strAction = 'Calendar.action';
   				}
   				
   				$.ajax({
   					url: strAction,
   					cache: true,
   					success: function(result) {
   						$("#subSubDivResult").html(result);
   					}
   				});
    		}
    	}
    }
    
    
    $("#frmOfferFinalizationCommunicationApproval").submit(function(event) {
  		event.preventDefault();
  		//alert(" ===>> 1 ");
  		var form = document.getElementById("form").value;
  		//alert(" ===>> form " + form);
  		var recruitId = document.getElementById("recruitId").value;
  		//alert(" ===>> recruitId " + recruitId);
  		var form_data = $("#frmOfferFinalizationCommunicationApproval").serialize();
		$.ajax({
			type :'POST',
			url  :'CandidateMyProfilePopup.action',
			data :form_data+"&ofcApprove=Approve",
			cache:true,
			success : function(result) {
				var strAction = 'Applications.action?recruitId='+recruitId;
				if(form != null && form == 'C') {
					strAction = 'Calendar.action';
				} else if(form != null && form == 'MH') {
					window.location = 'MyHome.action?toAction=MyHome';
				}
				
				$.ajax({
					url: strAction,
					cache: true,
					success: function(result) {
						$("#subSubDivResult").html(result);
					}
				});
			},
			error : function(result) {
				//alert(result);
				var strAction = 'Applications.action?recruitId='+recruitId;
				if(form != null && form == 'C') {
					strAction = 'Calendar.action';
				} else if(form != null && form == 'MH') {
					window.location = 'MyHome.action?toAction=MyHome';
				}
				
				$.ajax({
					url: strAction,
					cache: true,
					success: function(result) {
						$("#subSubDivResult").html(result);
					}
				});
			}
		});
		
		
	});
    
    
    function checkNeedApproval() {
    	//alert("val ===>> " + document.getElementById("needApprovalForOfferRelease").checked);
    	if (document.getElementById("needApprovalForOfferRelease").checked) {
			document.getElementById("spanOfcWorkflow").style.display = "inline";
		} else {
			document.getElementById("spanOfcWorkflow").style.display = "none";
		}
    }

    
    $("#frmOfferFinalizationCommunication").submit(function(event) {
  		event.preventDefault();
  		//alert(" ===>> 1 ");
  		var form = document.getElementById("form").value;
  		//alert(" ===>> form " + form);
  		var recruitId = document.getElementById("recruitId").value;
  		//alert(" ===>> recruitId " + recruitId);
  		var form_data = $("#frmOfferFinalizationCommunication").serialize();
		$.ajax({
			type :'POST',
			url  :'CandidateMyProfilePopup.action',
			data :form_data+"&ofcSubmit=Submit",
			cache:true/* ,
			success : function(result) {
				$("#subSubDivResult").html(result);
			} */
		});
		
		var strAction = 'Applications.action?recruitId='+recruitId;
		if(form != null && form == 'C') {
			strAction = 'Calendar.action';
		}
		
		$.ajax({
			url: strAction,
			cache: true,
			success: function(result) {
				$("#subSubDivResult").html(result);
			}
		});
	});
    
    
    $("#frmIntCommentHR").submit(function(event) {
  		event.preventDefault();
  		//alert(" ===>> 1 ");
  		var form = document.getElementById("form").value;
  		//alert(" ===>> form " + form);
  		var recruitId = document.getElementById("recruitId").value;
  		//alert(" ===>> recruitId " + recruitId);
  		var form_data = $("#frmIntCommentHR").serialize();
		$.ajax({
			type :'POST',
			url  :'CandidateMyProfilePopup.action',
			data :form_data+"&hrsubmit=Submit",
			cache:true/* ,
			success : function(result) {
				$("#subSubDivResult").html(result);
			} */
		});
		
		var strAction = 'Applications.action?recruitId='+recruitId;
		if(form != null && form == 'C') {
			strAction = 'Calendar.action';
		}
		
		$.ajax({
			url: strAction,
			cache: true,
			success: function(result) {
				$("#subSubDivResult").html(result);
			}
		});
	});
    
    
    function readFileURL(input, targetDiv) {
        if (input.files && input.files[0]) {
            var reader = new FileReader();
            reader.onload = function (e) {
                $('#'+targetDiv).attr('path', e.target.result);
            };
            reader.readAsDataURL(input.files[0]);
            document.getElementById("intSubmitComment").value = "Save";
        }
    }
    
    
    $("#frmIntComment").submit(function(event) {
  		event.preventDefault();
  		//alert(" ===>> 1 ");
  		var form = document.getElementById("form").value;
  		//alert(" ===>> form " + form);
  		var recruitId = document.getElementById("recruitId").value;
  		//alert(" ===>> recruitId " + recruitId);
  		if($("#file").attr('path') !== undefined){
    		  var form_data = new FormData($("#frmIntComment")[0]);
    		  form_data.append("strInterviewDocument", $("#file").attr('path'));
    		  //alert("form_data ===>> " + form_data);
    		  $("#subSubDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    		  $.ajax({
  	      		url: "CandidateMyProfilePopup.action",
  	      		type: 'POST',
  	      		data: form_data,
  	      		contentType: false,
  	            cache: false,
  	      		processData: false,
  	      	//====start parvez on 05-07-2021===== 
	  	      	success : function(result) {
					var strAction = 'Applications.action?recruitId='+recruitId;
					if(form != null && form == 'C') {
						window.location ='Calendar.action';
					}
					$.ajax({
						url: strAction,
						cache: true,
						success: function(result) {
							$("#subSubDivResult").html(result);
						}
					});
				},
				error : function(result) {
					var strAction = 'Applications.action?recruitId='+recruitId;
					if(form != null && form == 'C') {
						window.location ='Calendar.action';
					}
					$.ajax({
						url: strAction,
						cache: true,
						success: function(result) {
							$("#subSubDivResult").html(result);
						}
					});
				}
    		//====end parvez on 05-07-2021===== 
  	      	 });
  	   	  } else {
  	   		//alert("===>>1");
  	   		var form_data = $("#frmIntComment").serialize();
  	   		//alert("form_data ===>> " + form_data);
  	     	if(form == null && form != 'C') {
  	   			$("#subSubDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
  	     	}
  	     	//alert("===>> 2");
  	     	$.ajax({
  	     		type: 'POST',
  	 			url : 'CandidateMyProfilePopup.action',
  	 			data: form_data+"&intSubmitComment=Save",
  	 			cache : true,
				success : function(result) {
					var strAction = 'Applications.action?recruitId='+recruitId;
					if(form != null && form == 'C') {
						window.location ='Calendar.action';
					}
					$.ajax({
						url: strAction,
						cache: true,
						success: function(result) {
							$("#subSubDivResult").html(result);
						}
					});
				},
				error : function(result) {
					var strAction = 'Applications.action?recruitId='+recruitId;
					if(form != null && form == 'C') {
						window.location ='Calendar.action';
					}
					$.ajax({
						url: strAction,
						cache: true,
						success: function(result) {
							$("#subSubDivResult").html(result);
						}
					});
				}
  	 		});
  	     	
  	   	  }
  		
  		/* var form_data = $("#frmIntComment").serialize();
		$.ajax({
			type :'POST',
			url  :'CandidateMyProfilePopup.action',
			data :form_data+"&intSubmitComment=Save",
			cache:true
		}); */
		
	});
    
    
    function previewOfferLetter(candidateId, recruitId) {
    	//Created By Dattatray Date:05-10-21 Note Empty joinigdate
    	var joiningdateID = document.getElementById("joiningdate").value;
    	if(joiningdateID == ''){
    		alert('Please select the joining date');
        }else{
        	var form_data = $("#frmIntCommentHR").serialize();
        	//alert("form_data ===>> " + form_data);
    		$.ajax({
    			type :'POST',
    			url  :'CandidateMyProfilePopup.action',
    			data :form_data+"&preveiwOffer=Submit",
    			cache:true
    		});
        	window.location = "CandidateMyProfilePopup.action?operation=PREVIEW&CandID="+candidateId+"&recruitId="+recruitId;
        }
    	
    	
    
    }
    
</script>
<%
    CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
    
    Map<String, String> hmEmpName = (Map<String, String>)request.getAttribute("hmEmpName");
    
    UtilityFunctions uF = new UtilityFunctions();
    String strUserType = (String) session.getAttribute(IConstants.USERTYPE);
    String strCandiID = (String) request.getAttribute("CandID");
    String strSessionEmpID = (String) session.getAttribute(IConstants.EMPID);
    
    Map<String,String> hmCandNameMap = (Map<String,String>)request.getAttribute("hmCandNameMap");
    ArrayList alDocuments = (ArrayList) request.getAttribute("alDocuments");
    ArrayList alResumes = (ArrayList) request.getAttribute("alResumes");
    List alSkills = (List) request.getAttribute("alSkills");
    List alHobbies = (List) request.getAttribute("alHobbies");
    List alPrevEmployment = (List) request.getAttribute("alPrevEmployment");
    List alLanguages = (List) request.getAttribute("alLanguages");
    List alEducation = (List) request.getAttribute("alEducation");
    List alCertification = (List) request.getAttribute("alCertification");//Created By Dattatray Date:23-08-21
    List alFamilyMembers = (List) request.getAttribute("alFamilyMembers");
    List<List<String>> alAMedicalDetails=(List<List<String>> )request.getAttribute("alAMedicalDetails");
    Map<String,String> medicalQuest=(Map<String,String> )request.getAttribute("medicalQuest");
    String recruitID= (String)request.getAttribute("recruitId");
    
    if(alResumes == null) alResumes = new ArrayList();
    if(alDocuments == null) alDocuments = new ArrayList();
    if(alSkills == null) alSkills = new ArrayList();
    if(alHobbies == null) alHobbies = new ArrayList();
    if(alPrevEmployment == null) alPrevEmployment = new ArrayList();
    if(alLanguages == null) alLanguages = new ArrayList();
    if(alEducation == null) alEducation = new ArrayList();
    if(alFamilyMembers == null) alFamilyMembers = new ArrayList();
    
    if(hmCandNameMap == null) hmCandNameMap = new HashMap<String,String>(); 
    if(medicalQuest == null) medicalQuest = new HashMap<String,String>(); 
    /* 	boolean isFilledStatus = uF.parseToBoolean((String) request
    		.getAttribute("isFilledStatus")); */
    
    /* 	Map<String, String> hmempComment = (Map<String, String>) request.getAttribute("hmempComment");
    
    Map<String, String> hmempCommentRating = (Map<String, String>) request.getAttribute("hmempCommentRating"); */
    
    String candidateID = (String) request.getAttribute("CandID");
    
    Map hm = (HashMap) request.getAttribute("myProfile");
    if (hm == null) {
    	hm = new HashMap();
    }
    String strImage = (String) hm.get("IMAGE");
    
    String strTitle = "";
    if (strUserType != null
    		&& !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE)) {
    	strTitle = (String) hm.get("NAME") + "'s Profile";
    } else {
    	strTitle = "My Profile";
    
    }
    
    String RID = (String) request.getAttribute("recruitId");
    String apptype = (String) request.getAttribute("apptype");
    String callType = (String) request.getAttribute("callType");
    String EmpStars = (String) request.getAttribute("EmpStars");
    List<List<String>> activityList = (List<List<String>>) request.getAttribute("activityList");
    
    //Map<String, String> hmInterviewStatus=(Map<String, String>)request.getAttribute("hmInterviewStatus");
    //if(hmInterviewStatus==null) hmInterviewStatus=new HashMap<String, String>();
    String strMinRoundId = (String)request.getAttribute("strMinRoundId");
    String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
    
  //===start parvez date: 08-09-2021===
    Map<String, List<String>> hmEducationDocs = (Map<String, List<String>>) request.getAttribute("hmEducationDocs");
    
    //===end parvez date: 08-09-2021===
    		Map<String, String> hmFeatureStatus = (Map<String, String>) request.getAttribute("hmFeatureStatus");
    		//System.out.println("hmFeatureStatus=="+hmFeatureStatus.get(IConstants.F_TIMELINE_PREVIOUS_EMPLOYMENT_DISABLE));
    %>
<%-- <jsp:include page="../common/SubHeader.jsp">
    <jsp:param value="<%=strTitle %>" name="title"/>
    </jsp:include> --%>
		<section class="col-lg-3 connectedSortable">
    		<div class="box box-primary">
                <div class="box-body box-profile">
                    <%if(docRetriveLocation==null) { %>
                    <div class="profile-photo">
                        <img class="profile-user-img img-responsive img-circle lazy"  id="profilecontainerimg" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + strImage%>" >
                    </div>
                    <% }else{ %>
                    <div class="profile-photo">
                        <img class="profile-user-img img-responsive img-circle lazy" id="profilecontainerimg" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_CANDIDATE+"/"+IConstants.I_IMAGE+"/"+candidateID+"/"+IConstants.I_100x100+"/"+strImage%>">
                    </div>
                    <% } %>
                    <!-- <a href="javascript:void" onclick="showEditPhoto();"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a> -->
                    <h4 class="profile-username text-center"><%=uF.showData((String) hm.get("NAME"), "-")%></h4>
                    <p class="text-muted text-center text-no-margin">Candidate Id: <%=uF.showData((String) hm.get("CANDI_ID"), "-")%></p>
                    <p class="text-muted text-center text-no-margin">Job Code:<%=uF.showData((String) hm.get("JOB_CODE"), "-")%></p>
                    <%-- <p class="text-muted text-center text-no-margin">Primary Email: <%=uF.showData((String) hm.get("EMP_EMAIL"), "-")%></p> --%>
                    <%if(hm.get("CONTACT_MOB")!= null && !hm.get("CONTACT_MOB").equals("")){ %>
                    <p class="text-muted text-center text-no-margin"><img src="images1/telephone.png" style="width: 15px; height: 15px;"/> <%=uF.showData((String) hm.get("CONTACT_MOB"), "-")%></p>
                    <%} %>
                    <%if(hm.get("EMAIL")!= null && !hm.get("EMAIL").equals("")){ %>
                    <p class="text-muted text-center text-no-margin"><img src="images1/mail_icon.png" style="width: 15px; height: 15px;"/> <%=uF.showData((String) hm.get("EMAIL"), "-")%></p>
                    <%} %>
                    <center><div id="skillPrimary"></div></center>
                         <script type="text/javascript">
		                    $('#skillPrimary').raty({
		                          readOnly: true,
		                          start: <%=EmpStars %>,
		                          half: true,
		                          targetType: 'number'
		                    });
		                </script>
                    <p style="padding-left: 10px;padding-right: 10px;margin-top: 20px;text-align: center;">
                    	<%
					       if (alSkills != null && alSkills.size() != 0) {
					          for (int i = 0; i < alSkills.size(); i++) {

                            if(i%5 == 0){%>
                        <span class="label label-info">
                        <% }else if(i%5 == 1){ %> 
                        <span class="label label-success">
                        <% }else if(i%5 == 2){ %> 
                        <span class="label label-primary">
                        <% }else if(i%5 == 3){ %> 
                        <span class="label label-warning">
                        <% }else{ %>
                        <span class="label label-danger">
                        <% } %>
                        <strong><%=(i < alSkills.size() - 1) ? ((List) alSkills.get(i)).get(1) + ", " : ((List) alSkills.get(i)).get(1)%></strong>
                        </span>&nbsp;
                        <%}
					     }%>
                    </p>
                </div>
                <!-- /.box-body -->
            </div>
            <div class="col-lg-12 col-md-12 col-sm-12" >
            	
            	
            </div>
    	</section> 
    	<section class="col-lg-9 connectedSortable" style="padding-left: 0px;">
    		<div class="nav-tabs-custom">
    		<%
				  	Map<String,List<String>> hmPanelScheduleInfo=(Map)request.getAttribute("hmPanelScheduleInfo");
					Map<String,List<String>> hmPanelInterviewTaken=(Map)request.getAttribute("hmPanelInterviewTaken");
					Map<String,List<String>> hmPanelDataHR=(Map)request.getAttribute("hmPanelDataHR");
					List<String> panelList=(List)request.getAttribute("panelList");
					if(panelList==null) panelList=new ArrayList<String>();
					
					List<String> roundIdsRecruitwiseList = (List<String>)request.getAttribute("roundIdsRecruitwiseList");
					Map<String, String> hmpanelNameRAndRwise = (Map<String, String>)request.getAttribute("hmpanelNameRAndRwise");
					
					Map<String, String> hmRoundAssessment = (Map<String, String>) request.getAttribute("hmRoundAssessment");
					if(hmRoundAssessment == null) hmRoundAssessment = new HashMap<String, String>();
				
					Map<String, String> hmAssessRateRoundIdWise = (Map<String, String>) request.getAttribute("hmAssessRateRoundIdWise");
					if(hmAssessRateRoundIdWise == null) hmAssessRateRoundIdWise = new HashMap<String, String>();%>
                <ul class="nav nav-tabs">
                    <li <%if(callType==null || callType.equals("") || callType.equalsIgnoreCase("null")) { %> class="active" <% } %>><a href="#about" data-toggle="tab">About</a></li>
                    <li><a href="#resume" data-toggle="tab">Resume</a></li>
                    <li <%if(callType!=null && callType.equals("calendar")) { %> class="active" <% } %> ><a href="#timeline" data-toggle="tab">Timeline</a></li>
                    <%if ((strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.RECRUITER) || strUserType.equalsIgnoreCase(IConstants.ADMIN)) && (RID!=null && !RID.equals(""))) {%>	
                    <li <%if(callType!=null && callType.equals("addDates")) { %> class="active" <% } %>><a href="#pna" data-toggle="tab">Rounds</a></li>
                    <li <%if(callType!=null && callType.equals("finalisation")) { %> class="active" <% } %>><a href="#ina" data-toggle="tab">Interview & Assessment</a></li>
                    <%} %>
                    
                </ul>
                <div class="tab-content" style="margin-top: 20px;">
                    <div <%if(callType==null || callType.equals("") || callType.equalsIgnoreCase("null")) { %> class="active tab-pane" <% } else { %> class="tab-pane" <% } %> id="about">
                    	<div class="about-item">
	                      <h3 class="about-header">Candidate Information</h3>
	                      <div class="about-body">
							  <div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE; margin-top: 10px;">
									                <div class="box-header with-border">
									                    <h3 class="box-title" style="font-size: 14px;">
										                    Employment Status & Expectations
									                    </h3>
									                    <div class="box-tools pull-right">
									                    
									                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
									                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
									                    </div>
									                </div>
									                <!-- /.box-header -->
									                <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
									                    <div class="content1">
							                                <table class="table table_no_border autoWidth" >
							                                    <tr>
							                                        <td class="alignRight">Availability:</td>
							                                        <td class="textblue"><%=(String)hm.get("AVAILABILITY") %></td>
							                                    </tr>
							                                    <tr>
							                                        <td class="alignRight">Total Experience:</td>
							                                        <td class="textblue"><%=uF.showData((String) hm.get("TOT_EXPERIENCE"), "0") %> years</td>
							                                    </tr>
							                                    <tr>
							                                        <td class="alignRight">Current CTC:</td>
							                                        <td class="textblue"><%=uF.showData((String) hm.get("CURRENT_CTC"), "0") %></td>  <!-- L/A -->
							                                    </tr>
							                                    <tr>
							                                        <td class="alignRight">Expected CTC:</td>
							                                        <td class="textblue"><%=uF.showData((String) hm.get("EXPECTED_CTC"), "0") %></td>  <!-- L/A -->
							                                    </tr>
							                                    <tr>
							                                        <td class="alignRight">Notice Period:</td>
							                                        <td class="textblue"><%=uF.showData((String) hm.get("NOTICE_PERIOD"), "0")%></td>  <!-- days -->
							                                    </tr>
							                                </table>
							                            </div>
									                </div>
									                <!-- /.box-body -->
									            </div>
					                            <div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE; margin-top: 10px;">
									                <div class="box-header with-border">
									                    <h3 class="box-title" style="font-size: 14px;">
										                    Personal Information
										                </h3>
									                    <div class="box-tools pull-right">
									                   			
									                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
									                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
									                    </div>
									                </div>
									                <!-- /.box-header -->
									                <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
									                    <div class="content1">
							                                <table class="table table_no_border autoWidth" >
							                                    <tr>
							                                        <td class="alignRight">Current Address:</td>
							                                        <td class="textblue"><%=uF.showData((String)hm.get("TMP_ADDRESS"), "") + ", "
							                                            /* + uF.showData((String) hm.get("CITY"), "") + ", " */
							                                            + uF.showData((String) hm.get("TMP_STATE"), "") + ", "
							                                            + uF.showData((String) hm.get("TMP_COUNTRY"), "")%></td>
							                                    </tr>
							                                    <tr>
							                                        <td class="alignRight">Permanent Address:</td>
							                                        <td class="textblue"><%=uF.showData((String)hm.get("ADDRESS"), "") + ", "
							                                            /* + uF.showData((String) hm.get("CITY"), "") + ", " */
							                                            + uF.showData((String) hm.get("STATE"), "") + ", "
							                                            + uF.showData((String) hm.get("COUNTRY"), "")%></td>
							                                    </tr>
							                                    <tr>
							                                        <td class="alignRight">Landline:</td>
							                                        <td class="textblue"><%=uF.showData((String) hm.get("CONTACT"), "-")%></td>
							                                    </tr>
							                                    <tr>
							                                        <td class="alignRight">Mobile:</td>
							                                        <td class="textblue"><%=uF.showData((String) hm.get("CONTACT_MOB"), "-")%></td>
							                                    </tr>
							                                    <tr>
							                                        <td class="alignRight">Email id:</td>
							                                        <td class="textblue"><%=uF.showData((String) hm.get("EMAIL"), "-")%></td>
							                                    </tr>
							                                    <%-- <tr>
							                                        <td class="alignRight">Pan No:</td>
							                                        <td class="textblue"><%=uF.showData((String) hm.get("PAN_NO"), "-")%></td>
							                                        </tr>
							                                        <tr>
							                                        <td class="alignRight">Provident Fund No:</td>
							                                        <td class="textblue"><%=uF.showData((String) hm.get("PF_NO"), "-")%></td>
							                                        </tr> --%>
							                                    <tr>
							                                        <td class="alignRight">Date of Birth:</td>
							                                        <td class="textblue"><%=uF.showData((String) hm.get("DOB"), "-")%></td>
							                                    </tr>
							                                    <tr>
							                                        <td class="alignRight">Gender:</td>
							                                        <td class="textblue"><%=uF.showData((String) hm.get("GENDER"), "-")%></td>
							                                    </tr>
							                                    <tr>
							                                        <td class="alignRight">Marital Status:</td>
							                                        <td class="textblue"><%=uF.showData((String) hm.get("MARITAL_STATUS"), "-")%></td>
							                                    </tr>
							                                    <tr>
							                                        <td class="alignRight">Passport No:</td>
							                                        <td class="textblue"><%=uF.showData((String) hm.get("PASSPORT_NO"), "-")%></td>
							                                    </tr>
							                                    <tr>
							                                        <td class="alignRight">Passport expires on:</td>
							                                        <td class="textblue"><%=uF.showData((String) hm.get("PASSPORT_EXPIRY"), "-")%></td>
							                                    </tr>
							                                    <!-- Start Dattatray Date:23-08-21 -->
							                                      <tr>
							                                        <td class="alignRight">Current Location:</td>
							                                        <td class="textblue"><%=uF.showData((String) hm.get("CURRENT_LOCATION"), "-")%></td>
							                                    </tr>
							                                      <tr>
							                                        <td class="alignRight">Preferred location:</td>
							                                        <td class="textblue"><%=uF.showData((String) hm.get("PREFERRED_LOCATION"), "-")%></td>
							                                    </tr>
							                                    <!-- End Dattatray Date:23-08-21 -->
							                                </table>
							                            </div>
									                </div>
									                <!-- /.box-body -->
									            </div>
					                            <div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE; margin-top: 10px;">
									                <div class="box-header with-border">
									                    <h3 class="box-title" style="font-size: 14px;">Education</h3>
									                    <div class="box-tools pull-right">
									                    
									                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
									                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
									                    </div>
									                </div>
									                <!-- /.box-header -->
									                <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
									                    <div class="content1" style="margin: 0px 0px 10px 0px">
							                                <table class=" table table-bordered autoWidth"  style="width: 98%">
							                                    <tr>
							                                        <td class="alignCenter" width="30%">Degree Name</td>
							                                        <td class="alignCenter">Duration</td>
							                                        <td class="alignCenter">Completion Year</td>
							                                    <!-- ===start parvez date: 30-07-2022=== -->    
							                                        <td class="alignCenter">Grade / Percentage</td>
							                                    <!-- ===end parvez date: 30-07-2022=== -->    
							                                        <!-- Start Dattatray Date:23-08-21 -->
							                                        <td class="alignCenter">Name of Institute</td>
							                                        <td class="alignCenter">Board</td>
							                                        <td class="alignCenter">Subject</td>
							                                        <td class="alignCenter">Start Date</td>
							                                        <td class="alignCenter">Completion Date</td>
							                                        <td class="alignCenter">Marks / CGPA</td>
							                                        <td class="alignCenter">City</td>
							                                        <td class="alignCenter">Certificate</td>
							                                         <!-- End Dattatray Date:23-08-21 -->
							                                    </tr>
							                                    <%
							                                        if (alEducation != null && alEducation.size() != 0) {
							                                        	for (int i = 0; i < alEducation.size(); i++) {
							                                        %>
							                                    <tr>
							                                        <td class="textblue alignCenter"><%=((List) alEducation.get(i)).get(1)%></td>
							                                        <td class="textblue alignCenter"><%=((List) alEducation.get(i)).get(2) + " Years"%></td>
							                                        <td class="textblue alignCenter"><%=((List) alEducation.get(i)).get(3)%></td>
							                                        <td class="textblue alignCenter"><%=((List) alEducation.get(i)).get(4)%></td>
							                                        <!-- Start Dattatray Date:23-08-21 -->
							                                        <td class="textblue alignCenter"><%=((List) alEducation.get(i)).get(6)%></td>
							                                        <td class="textblue alignCenter"><%=((List) alEducation.get(i)).get(7)%></td>
							                                        <td class="textblue alignCenter"><%=((List) alEducation.get(i)).get(5)%></td>
							                                        <td class="textblue alignCenter"><%=((List) alEducation.get(i)).get(8)%></td>
							                                        <td class="textblue alignCenter"><%=((List) alEducation.get(i)).get(9)%></td>
							                                        <td class="textblue alignCenter"><%=((List) alEducation.get(i)).get(10)%></td>
							                                        <td class="textblue alignCenter"><%=((List) alEducation.get(i)).get(11)%></td>
							                                        <!-- ===start parvez date: 08-09-2021=== -->
							                                        <td class="textblue alignCenter">
							                                        	<!-- ===start parvez date: 08-09-2021=== -->
							                                        
							                                        	<% if(hmEducationDocs != null && hmEducationDocs.size()>0) {
						                                                    	List<String> innrList = hmEducationDocs.get(((List) alEducation.get(i)).get(12));
						                                                    	for(int j=0; innrList != null && j<innrList.size(); j++) {
						                                                    %>
																		<div>
																			<%if(docRetriveLocation == null) { %>
																			<a
																				href="<%=request.getContextPath()+ IConstants.DOCUMENT_LOCATION + innrList.get(j)  %>"
																				title="Education Document"><i class="fa fa-file-o"
																				aria-hidden="true"></i>
																			</a>
																			<% } else { %>
																			<a
																				href="<%=docRetriveLocation +IConstants.I_CANDIDATE+"/"+IConstants.I_DOCUMENT+"/"+IConstants.I_EDUCATION_DOC+"/"+candidateID+"/"+ innrList.get(j) %>"
																				title="Education Document"><i class="fa fa-file-o"
																				aria-hidden="true"></i>
																			</a>
																			<% } %>
																		</div> <% } %> <% } else { %> N/A <% } %>
							                                        </td>
							                                        <!-- ===end parvez date: 08-09-2021=== -->
							                                        <!-- End Dattatray Date:23-08-21 -->
							                                    </tr>
							                                    <%-- <tr>
							                                        <td class="alignRight">Completion Year:</td>
							                                        <td class="textblue"><%=((List) alEducation.get(i)).get(3)%></td>
							                                        </tr>
							                                        <tr>
							                                        <td class="alignRight">Grade:</td>
							                                        <td class="textblue"><%=((List) alEducation.get(i)).get(4)%></td>
							                                        </tr> --%>
							                                    <% } } else { %>
							                                    <tr>
							                                        <td colspan="4">
							                                            <div class="nodata msg"><span>No Education detail added</span> </div>
							                                        </td>
							                                    </tr>
							                                    <%} %>
							                                </table>
							                            </div>
									                </div>
									                <!-- /.box-body -->
									            </div>
					                            <div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE; margin-top: 10px;">
									                <div class="box-header with-border">
									                    <h3 class="box-title" style="font-size: 14px;">Skill Set</h3>
									                    <div class="box-tools pull-right">
									                    
									                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
									                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
									                    </div>
									                </div>
									                <!-- /.box-header -->
									                <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
									                    <div class="content1" style="margin: 0px 0px 10px 0px">
							                                <table class="table table_no_border autoWidth"  style="width: 98%">
							                                    <% if (alSkills != null && alSkills.size() != 0) {
							                                        for (int i = 0; i < alSkills.size(); i++) {
							                                        	List alInner = (List) alSkills.get(i);
							                                        %>
							                                    <script type="text/javascript">
							                                        $(function() {
							                                        $("#star"+<%=i%>).raty({
							                                         readOnly: true,
							                                        start:	<%= alInner != null  ? uF.parseToDouble( alInner.get(2).toString())/2 : "0"%>,
							                                         half: true,
							                                         targetType: 'number'
							                                        	 });
							                                                     });
							                                                    
							                                    </script>
							                                    <tr>
							                                        <td class="alignRight"><%=alInner.get(1)%>:</td>
							                                        <td class="textblue" style="padding: 0px 15px;"><%=alInner.get(2)%>/10</td>
							                                        <td>
							                                            <div id="star<%=i%>"></div>
							                                        </td>
							                                    </tr>
							                                    <% } } else { %>
							                                    <tr>
							                                        <td class="nodata msg"><span>No skill sets added</span> </td>
							                                    </tr>
							                                    <% }  %>
							                                </table>
							                            </div>
									                </div>
									                <!-- /.box-body -->
									            </div>
					                            <div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE; margin-top: 10px;">
									                <div class="box-header with-border">
									                    <h3 class="box-title" style="font-size: 14px;">Languages</h3>
									                    <div class="box-tools pull-right">
									                    
									                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
									                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
									                    </div>
									                </div>
									                <!-- /.box-header -->
									                <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
									                    <div class="content1" style="margin: 0px 0px 10px 0px">
							                                <table class="table table_no_border autoWidth"  style="width: 98%">
							                                    <% if (alLanguages != null && alLanguages.size() != 0) { %>
							                                    <tr class="center">
							                                        <td width="150px"><strong>Language</strong> </td>
							                                        <td width="150px"><strong>Read</strong> </td>
							                                        <td width="150px"><strong>Write</strong> </td>
							                                        <td width="150px"><strong>Speak</strong> </td>
							                                    </tr>
							                                    <% for (int i = 0; i < alLanguages.size(); i++) {
							                                        List alInner = (List) alLanguages.get(i);
							                                        %>
							                                    <tr>
							                                        <td class="textblue"><strong><%=alInner.get(1) %></strong></td>
							                                        <% if (((String) alInner.get(2)).equals("1")) { %>
							                                        <td class="textblue yes"></td>
							                                        <% } else { %>
							                                        <td class="textblue no"></td>
							                                        <% } %>
							                                        <% if (((String) alInner.get(3)).equals("1")) { %>
							                                        <td class="textblue yes"></td>
							                                        <% } else { %>
							                                        <td class="textblue no"></td>
							                                        <% } %>
							                                        <% if (((String) alInner.get(4)).equals("1")) { %>
							                                        <td class="textblue yes"></td>
							                                        <% } else { %>
							                                        <td class="textblue no"></td>
							                                        <% } %>
							                                    </tr>
							                                    <% } } else { %>
							                                    <tr>
							                                        <td class="nodata msg"><span>No languages added</span> </td>
							                                    </tr>
							                                    <% } %>
							                                </table>
							                            </div>
									                </div>
									                <!-- /.box-body -->
									            </div>
									            <% if(!uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_EMPLOYEE_HOBBIES_DISABLE))){ %>
						                            <div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE; margin-top: 10px;">
										                <div class="box-header with-border">
										                    <h3 class="box-title" style="font-size: 14px;">Hobbies</h3>
										                    <div class="box-tools pull-right">
										                   
										                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
										                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
										                    </div>
										                </div>
										                <!-- /.box-header -->
										                <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
										                    <div class="content1" style="margin: 0px 0px 10px 0px">
								                                <table class="table table_no_border autoWidth"  style="width: 98%">
								                                    <% if (alHobbies != null && alHobbies.size() != 0) {%>
								                                    <tr>
								                                        <td class="textblue">
								                                            <% for (int i = 0; i < alHobbies.size(); i++) {
								                                                List alInner = (List) alHobbies.get(i);
								                                                %>
								                                            <strong><%=i < alHobbies.size() - 1 ? (String) alInner.get(1) + " ," : (String) alInner.get(1)%></strong>
								                                            <% }%>
								                                        </td>
								                                    </tr>
								                                    <% } else { %>
								                                    <tr>
								                                        <td class="nodata msg"><span>No hobbies added</span> </td>
								                                    </tr>
								                                    <% } %>
								                                </table>
								                            </div>
										                </div>
										                <!-- /.box-body -->
										            </div>
									            <% } %>
									            
									            
									       <!-- ===start parvez date: 06-09-2022=== -->
									       <% if(!uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_DISABLE_MEDICAL_DETAILS))){ %>     
					                            <div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE; margin-top: 10px;">
									                <div class="box-header with-border">
									                    <h3 class="box-title" style="font-size: 14px;">Medical Details</h3>
									                    <div class="box-tools pull-right">
									                    
									                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
									                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
									                    </div>
									                </div>
									                <!-- /.box-header -->
									                <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
									                    <div class="content1" style="margin: 0px 0px 10px 0px">
							                                <table class="table table_no_border autoWidth"  style="width:98%">
							                                    <%	if(alAMedicalDetails!=null && alAMedicalDetails.size()!=0) { 
							                                        int cnt =0;
							                                        for(int i=0; i<alAMedicalDetails.size(); i++) {
							                                        		List<String> alInner =alAMedicalDetails.get(i);	
							                                        		
							                                        		if(medicalQuest.get(alInner.get(0)) != null){
							                                        			cnt++;
							                                        %>
							                                    <tr>
							                                        <td style="width:70%">
							                                            <div style="float:left;width:10px;font-weight:bold;padding-right:10px;"><%=cnt %>.&nbsp;&nbsp;&nbsp;</div>
							                                            <div style="float:left;width:90%;"><%=medicalQuest.get(alInner.get(0)) %></div>
							                                        </td>
							                                        <%if(uF.parseToBoolean(alInner.get(1))) { %>
							                                        <td class="textblue yes" style="width:10%"></td>
							                                        <%}else{ %>
							                                        <td class="textblue no" style="width:10%"></td>
							                                        <% } %>
							                                        <%if(alInner.get(3)!=null) { %>
							                                        <td style="width:20%" class="alignRight">
							                                            <%-- <a href="<%=request.getContextPath()+"/userDocuments/"+alInner.get(3)%>">Download</a> --%>
							                                            <%if(docRetriveLocation == null) { %>
							                                            <a target="blank" style="float: left; padding-top: 5px;" href="<%=request.getContextPath()+ IConstants.DOCUMENT_LOCATION + alInner.get(3)  %>" title="Medical Document" ><i class="fa fa-file-o" aria-hidden="true"></i></a>
							                                            <%} else { %>
							                                            <a target="blank" style="float: left; padding-top: 5px;" href="<%=docRetriveLocation +IConstants.I_CANDIDATE+"/"+IConstants.I_DOCUMENT+"/"+IConstants.I_MEDICAL+"/"+candidateID+"/"+ alInner.get(3)  %>" title="Medical Document" ><i class="fa fa-file-o" aria-hidden="true"></i></a>
							                                            <%} %>
							                                        </td>
							                                        <%}%>
							                                    </tr>
							                                    <%if(uF.parseToBoolean(alInner.get(1))){ %>
							                                    <tr>
							                                        <td class="textblue"><strong><%= alInner.get(2)%></strong></td>
							                                    </tr>
							                                    <% } %>
							                                    <% } %>    
							                                    <%}%>
							                                    <%}else{%>
							                                    <tr>
							                                        <td class="nodata msg"><span>No Medical Details added</span></td>
							                                    </tr>
							                                    <%}%>
							                                </table>
							                            </div>
									                </div>
									                <!-- /.box-body -->
									            </div>
									            <% } %>
									      <!-- ===end parvez date: 06-09-2022=== -->      
					                            <div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE; margin-top: 10px;">
									                <div class="box-header with-border">
									                    <h3 class="box-title" style="font-size: 14px;">Supporting Documents</h3>
									                    <div class="box-tools pull-right">
									                    
									                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
									                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
									                    </div>
									                </div>
									                <!-- /.box-header -->
									                <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
									                    <div class="content1" style="margin: 0px 0px 10px 0px">
							                                <table class="table table_no_border autoWidth"  style="width: 98%">
							                                    <%	if(alDocuments!=null && alDocuments.size()!=0) {
							                                        for(int i=0; i<alDocuments.size(); i++) {
							                                        %>
							                                    <tr>
							                                        <td class="alignRight">
							                                            <%=((ArrayList)alDocuments.get(i)).get(1)%>
							                                        </td>
							                                        <td class="textblue">-</td>
							                                        <td class="alignRight">
							                                            <%-- <a href="<%=request.getContextPath()+"/userDocuments/"+((ArrayList)alDocuments.get(i)).get(4)%>">Download</a> --%>
							                                            <%if(docRetriveLocation == null) { %>
							                                            <a href="<%=request.getContextPath()+ IConstants.DOCUMENT_LOCATION + ((ArrayList)alDocuments.get(i)).get(4)  %>" title="Reference Document" ><i class="fa fa-file-o" aria-hidden="true"></i></a>
							                                            <%} else { %>
							                                            <a href="<%=docRetriveLocation +IConstants.I_CANDIDATE+"/"+IConstants.I_DOCUMENT+"/"+IConstants.I_ATTACHMENT+"/"+candidateID+"/"+ ((ArrayList)alDocuments.get(i)).get(4)  %>" title="Reference Document" ><i class="fa fa-file-o" aria-hidden="true"></i></a>
							                                            <%} %>
							                                        </td>
							                                        <td class="textblue">-</td>
							                                    </tr>
							                                    <%}
							                                        }else {%>
							                                    <tr>
							                                        <td class="nodata msg"><span>No Documents attached</span></td>
							                                    </tr>
							                                    <%}%>
							                                </table>
							                            </div>
									                </div>
									                <!-- /.box-body -->
									            </div>
					                            <div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE; margin-top: 10px;">
									                <div class="box-header with-border">
									                    <h3 class="box-title" style="font-size: 14px;">Resume</h3>
									                    <div class="box-tools pull-right">
									                    
									                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
									                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
									                    </div>
									                </div>
									                <!-- /.box-header -->
									                <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
									                    <div class="resumeContent" style="margin: 0px 0px 10px 0px;">
							                                <table class="table table_no_border autoWidth"  style="width: 98%">
							                                    <%	
							                                    String filePath = null;
							                                    String fileExt = null;
							                                    if(alResumes!=null && alResumes.size()!=0) {
							                                        for(int i=0; i<alResumes.size(); i++) {
							                                        	filePath = request.getContextPath()+ IConstants.DOCUMENT_LOCATION + ((ArrayList)alResumes.get(i)).get(4);
							                                        	fileExt = ((ArrayList)alResumes.get(i)).get(5)!=null ? ((ArrayList)alResumes.get(i)).get(5).toString() : null;
							                                        	if(docRetriveLocation != null) {
							                                        		filePath = docRetriveLocation +IConstants.I_CANDIDATE+"/"+IConstants.I_DOCUMENT+"/"+IConstants.I_ATTACHMENT+"/"+candidateID+"/"+ ((ArrayList)alResumes.get(i)).get(4);
							                                        	}
							                                        	String action ="ViewCandidateResume.action?from=candProfile&candId="+((ArrayList)alResumes.get(i)).get(3) +"&documentName="+((ArrayList)alResumes.get(i)).get(1) +"&documentId="+((ArrayList)alResumes.get(i)).get(0)+"&filePath="+URLEncoder.encode(filePath);
							                                        	//System.out.println("action==>"+action);
							                                        %>
							                                    <tr>
							                                        <td class="alignRight">
							                                            <%=((ArrayList)alResumes.get(i)).get(1) %>
							                                        </td>
							                                        <td class="alignRight">
							                                            <%if(docRetriveLocation == null) { %>
							                                            <a href="<%=request.getContextPath()+ IConstants.DOCUMENT_LOCATION + ((ArrayList)alResumes.get(i)).get(4)  %>" title="Download Resume" >Download </a>
							                                            <% } else { %>
							                                            <a href="<%=docRetriveLocation+IConstants.I_CANDIDATE+"/"+IConstants.I_DOCUMENT+"/"+IConstants.I_ATTACHMENT+"/"+candidateID+"/"+ ((ArrayList)alResumes.get(i)).get(4)  %>" title="Download Resume" >Download</a> 
							                                            <% } %>
							                                            <%-- |  <a target="_blank" href="<%=action %>" style="font-weight: bold;" title="View Resume"> View Resume</a> --%> 
							                                            <%--  <a  href="javascript:void(0)" onclick="showResume('<%=((ArrayList)alResumes.get(i)).get(0)%>','<%=action%>','<%=hmCandNameMap.get(((ArrayList)alResumes.get(i)).get(3))%>');" style="font-weight:bold;" title="View Resume"> View Resume</a> --%> 
							                                        </td>
							                                    </tr>
							                                    <%}%>
							                                    <%  		
							                                        }else {%>
							                                    <tr>
							                                        <td class="nodata msg"><span>No Resume attached</span></td>
							                                    </tr>
							                                    <%}%>
							                                </table>
							                            </div>
									                </div>
									                <!-- /.box-body -->
									            </div>	                      	
									            <div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE; margin-top: 10px;">
									                <div class="box-header with-border">
									                    <h3 class="box-title" style="font-size: 14px;">Certification</h3>
									                    <div class="box-tools pull-right">
									                    <%-- <% if(strUserType!=null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) && !strUserType.equalsIgnoreCase(IConstants.ARTICLE) && !strUserType.equalsIgnoreCase(IConstants.CONSULTANT) && !strUserType.equalsIgnoreCase(IConstants.MANAGER)){ %>
						                                <a href="javascript: void(0);" onclick="addNewCandidate('<%=(String)request.getAttribute("CandID")%>','<%=(String)request.getAttribute("recruitId") %>','2');" rel="popup_name" title="Edit Education Details"><span class="label label-danger" style="font-size: 9px;">
										                        	<strong>Edit</strong>
										                        </span></a>
						                                <% } %> --%>
									                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
									                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
									                    </div>
									                </div>
									                <!-- /.box-header -->
									                <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
									                    <div class="content1" style="margin: 0px 0px 10px 0px">
							                                <table class=" table table-bordered autoWidth"  style="width: 98%">
							                                    <tr>
							                                        <td class="alignCenter" width="30%">Certificate Name</td>
							                                        <td class="alignCenter">Completion Year</td>
							                                        <td class="alignCenter">Location</td>
							                                    </tr>
							                                    <%
							                                        if (alCertification != null && alCertification.size() != 0) {
							                                        	for (int i = 0; i < alCertification.size(); i++) {
							                                        %>
							                                    <tr>
							                                        <td class="textblue alignCenter"><%=((List) alCertification.get(i)).get(1)%></td>
							                                        <td class="textblue alignCenter"><%=((List) alCertification.get(i)).get(2)%></td>
							                                        <td class="textblue alignCenter"><%=((List) alCertification.get(i)).get(3)%></td>
							                                    </tr>
							                                    <% } } else { %>
							                                    <tr>
							                                        <td colspan="10">
							                                            <div class="nodata msg"><span>No Certification detail added</span> </div>
							                                        </td>
							                                    </tr>
							                                    <%} %>
							                                </table>
							                            </div>
									                </div>
									                <!-- /.box-body -->
									            </div>
		                      </div>
		                    </div>
		                    <br/>
		                    
		              <!-- ===start parvez date: 06-09-2022=== -->
		              <% if(!uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_DISABLE_EMPLOYEE_FAMILY_INFORMATION))){ %>      
		                    <div class="about-item">
		                      <h3 class="about-header">Family Information
		                      </h3>
		                      <div class="about-body">
                  	  			<% if (alFamilyMembers != null && alFamilyMembers.size() != 0) {%>
                                
                                	<% for (int i = 0; i < alFamilyMembers.size(); i++) {
                                
                                		if (((String) ((List) alFamilyMembers.get(i)).get(1)).length() != 0) { %>
                                			<div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE; margin-top: 10px;">
						                		<div class="box-header with-border">
						                			<h3 class="box-title" style="font-size: 14px;">
		                                			<%if (((List) alFamilyMembers.get(i)).get(8).equals("FATHER")) { %>
						                            	Father's Info
						                            <% } else if (((List) alFamilyMembers.get(i)).get(8).equals("MOTHER")) { %>
						                                Mother's Info
						                            <% } else if (((List) alFamilyMembers.get(i)).get(8).equals("SPOUSE")) {
						                                %>
						                                Spouse's Info
						                            <% } else if (((List) alFamilyMembers.get(i)).get(8).equals("SIBLING")) { %>
						                                Sibling's Info
						                            <% } %>
	                            					</h3>
										            <div class="box-tools pull-right">
										                <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
										                <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
										            </div>
									            </div>
									            <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
						                            <div class="content1" style="margin: 0px 0px 10px 0px">
						                                <table class="table table_no_border autoWidth" >
						                                    <tr>
						                                        <td class="alignRight">Name:</td>
						                                        <td class="textblue"><%=((List) alFamilyMembers.get(i)).get(1)%></td>
						                                    </tr>
						                                    <tr>
						                                        <td class="alignRight">Date Of Birth:</td>
						                                        <td class="textblue"><%=((List) alFamilyMembers.get(i)).get(2)%></td>
						                                    </tr>
						                                    <tr>
						                                        <td class="alignRight">Education:</td>
						                                        <td class="textblue"><%=((List) alFamilyMembers.get(i)).get(3)%></td>
						                                    </tr>
						                                    <tr>
						                                        <td class="alignRight">Occupation:</td>
						                                        <td class="textblue"><%=((List) alFamilyMembers.get(i)).get(4)%></td>
						                                    </tr>
						                                    <tr>
						                                        <td class="alignRight">Contact No:</td>
						                                        <td class="textblue"><%=((List) alFamilyMembers.get(i)).get(5)%></td>
						                                    </tr>
						                                    <tr>
						                                        <td class="alignRight">Email Id:</td>
						                                        <td class="textblue"><%=((List) alFamilyMembers.get(i)).get(6)%></td>
						                                    </tr>
						                                    <%if (((List) alFamilyMembers.get(i)).get(8).equals("SPOUSE") || ((List) alFamilyMembers.get(i)).get(8).equals("SIBLING")) { %>
						                                    <tr>
						                                        <td class="alignRight">Gender:</td>
						                                        <td class="textblue"><%=((List) alFamilyMembers.get(i)).get(7)%></td>
						                                    </tr>
						                                    <% } %>
						                                </table>
						                            </div>
						                        </div>
						                        </div>
					                            <%
					                                }
					                                	} %>
					                                	
					                                <% } else { %>
					                            <table class="table table_no_border autoWidth" style="width: 98%">
					                                <tr>
					                                    <td class="nodata msg"><span>No family members added</span></td>
					                                </tr>
					                            </table>
					                            <% } %>
	                      </div>
	                    </div>
	                    <% } %>
	                    <!-- ===end parvez date: 06-09-2022=== -->
                    </div>
                    
                    
                    	
				
				<%if ((strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.RECRUITER) || strUserType.equalsIgnoreCase(IConstants.ADMIN)) && (RID!=null && !RID.equals(""))) { %>	
				<div <%if(callType!=null && callType.equals("addDates")) { %> class="active tab-pane" <% } else { %> class="tab-pane" <% } %>id="pna">
                    <%
                  //====start parvez on 09-07-2021=====
                    //  if (apptype != null && (apptype.equals("shortlist") || apptype.equals("finalize") || apptype.equals("reject"))) {
                      if (apptype != null && (apptype.equals("shortlist") || apptype.equals("finalize") || apptype.equals("reject") || apptype.equals("accept") ||  apptype.equals("offer")
                      		|| apptype.equals("today") || apptype.equals("tomorrow") || apptype.equals("daysAfterTomorrow") || apptype.equals("pending") || apptype.equals("onboarded"))) {
                      //====end parvez on 09-07-2021=====
                    	String strCandiShortlistStatus = (String)request.getAttribute("strCandiShortlistStatus");
							Map<String, String> hmDates = (Map)request.getAttribute("hmDateMap");
							if(hmDates == null)hmDates = new HashMap<String, String>();
							Map<String, String> hmTime = (Map)request.getAttribute("hmTimeMap");
							%>
							<div id="panelScheduleDiv">
							<div style="width: 100%;">
							<table class="table">
								<%
								boolean flag = false;
								if(uF.parseToInt(strCandiShortlistStatus)==2) {
									//System.out.println("roundIdsRecruitwiseList : "+roundIdsRecruitwiseList);
								for (int i = 0; i<roundIdsRecruitwiseList.size(); i++) { %>
								<tr>
									<td valign="top" colspan="4">
										<div style="float: left; width: 100%;">
										<%
											//System.out.println("roundIdsRecruitwiseList.get(i) : "+roundIdsRecruitwiseList.get(i));
										%>
											<div style="float: left; min-width: 25%; max-width: 40%;"><b>Round <%=roundIdsRecruitwiseList.get(i)%>:</b>
											<% if(hmpanelNameRAndRwise.get(roundIdsRecruitwiseList.get(i)) != null && !hmpanelNameRAndRwise.get(roundIdsRecruitwiseList.get(i)).equals("") && !hmpanelNameRAndRwise.get(roundIdsRecruitwiseList.get(i)).equalsIgnoreCase("null")) { %>
												<br/><%=hmpanelNameRAndRwise.get(roundIdsRecruitwiseList.get(i))%>
												<%
													//System.out.println("Name : "+hmpanelNameRAndRwise.get(roundIdsRecruitwiseList.get(i)));
												%>
											<% } %>
											</div>
											<% List<String> alInner = hmPanelScheduleInfo.get(roundIdsRecruitwiseList.get(i));
												if (alInner != null) { 
											%>
												<div id="interSchDateTime<%=i%>" style="float: left;"> Date: <%=alInner.get(0)%>&nbsp;&nbsp; Time: <%=alInner.get(1)%>
													<input type="hidden" name="interviewdate" id="interviewdate<%=i%>"/>
													<input type="hidden" name="interviewTime" id="interviewTime<%=i%>"/>
													<a href="javascript: void(0)" class="fa fa-times-circle cross" onclick="addToCalender(<%=roundIdsRecruitwiseList.get(i)%>, <s:property value="recruitId"/>, <%=(String)request.getAttribute("CandID")%>, <%=i%>, 'remove', <%=(String)request.getAttribute("notiStatus") %>, <%=hmRoundAssessment.get(roundIdsRecruitwiseList.get(i)+"_ID") %>);">
													</a> <!-- <img src="images1/list-remove.png" width="18" height="18" /> -->
												</div>
											<% } else { %>
											<% if(hmPanelInterviewTaken.keySet().contains(roundIdsRecruitwiseList.get(i))) {
													List<String> alInnerInterviewTaken = hmPanelInterviewTaken.get(roundIdsRecruitwiseList.get(i));
													if (alInnerInterviewTaken != null) {
														//System.out.println("alInnerInterviewTaken : "+alInnerInterviewTaken);
													
											%>
											<div id="interSchDateTime<%=i%>" style="float: left;"> Date: <%=alInnerInterviewTaken.get(0)%>&nbsp;&nbsp; Time: <%=alInnerInterviewTaken.get(1)%>
												&nbsp;Interview Taken</div>
				                             <% } } else { %>
				                             <%if(flag == false) { %>
											<div id="interSchDateTime<%=i%>" style="float: left;">
												Date: <input type="text" name="interviewdate" id="interviewdate<%=i%>" style="width: 90px !important;"/>
												Time: <input type="text" name="interviewTime" id="interviewTime<%=i%>" style="width: 50px !important;"/>&nbsp;
												<a href="javascript: void(0)" class="fa fa-check-circle checknew" title="Add to Calender" onclick="addToCalender(<%=roundIdsRecruitwiseList.get(i)%>, <s:property value="recruitId"/>, <%=(String)request.getAttribute("CandID")%>, <%=i%>, 'insert', <%=(String)request.getAttribute("notiStatus") %>, <%=hmRoundAssessment.get(roundIdsRecruitwiseList.get(i)+"_ID") %>);">
												</a> <!-- <img src="images1/calendar_grey.png" width="18" height="18"/> -->
											</div>
											
											<% } } } %>
										</div>
										<% if(uF.parseToInt(hmRoundAssessment.get(roundIdsRecruitwiseList.get(i)+"_ID")) > 0) { %>
											<div style="float: left; width: 100%;">
												<span>Assessment:1</span> 
												<span><a href="javascript:void(0);" style="font-weight: normal;" onclick="viewAssessmentDetail('<%=hmRoundAssessment.get(roundIdsRecruitwiseList.get(i)+"_ID") %>', '<%=uF.showData(hmRoundAssessment.get(roundIdsRecruitwiseList.get(i)+"_NAME"), "") %>')"><%=uF.showData(hmRoundAssessment.get(roundIdsRecruitwiseList.get(i)+"_NAME"), "") %></a></span>
												<span id="assessSpan_<%=roundIdsRecruitwiseList.get(i) %>" style="margin-left: 7px;">
													<a href="javascript:void(0);" onclick="sendAssessmentToCandidate('<%=RID %>', '<%=roundIdsRecruitwiseList.get(i) %>', '<%=hmRoundAssessment.get(roundIdsRecruitwiseList.get(i)+"_ID") %>', '<%=(String)request.getAttribute("CandID")%>');">Send Now</a>
												</span>
											</div>
										<% } %>
									</td>	
										
										
									<% if (alInner != null) { %>
									<td width="31%"></td>
									<% } else { %>
									<% if(hmPanelInterviewTaken.keySet().contains(roundIdsRecruitwiseList.get(i))) {
											List<String> alInnerInterviewTaken = hmPanelInterviewTaken.get(roundIdsRecruitwiseList.get(i));
											if (alInnerInterviewTaken != null) {
									%>
									<td width="31%"></td>
		                             <% } } else { %>
		                             <%if(flag == false) { %>
									<td width="31%" valign="top">
										<div style="margin-top: -5px; width: 100%;"><strong>Preferred Date Time</strong></div>
										<%
											int count = 0;
											Iterator<String> itr = hmDates.keySet().iterator();
											while (itr.hasNext()) {
											String id = itr.next();
										%>
										<div style="margin-top: -5px; width: 100%;"><input type="radio" name="preferedDtTmRedio" id="preferedDtTmRedio<%=count + 1%>">  <!-- onclick="showOtherDateTime();" -->
										<input type="hidden" name="preferedDate" id="preferedDate<%=count + 1%>" value="<%=uF.showData(hmDates.get(id), "")%>">
											<%=uF.showData(hmDates.get(id), "Not added")%>
										<input type="hidden" name="preferedTime" id="preferedTime<%=count + 1%>" value="<%=uF.showData(hmTime.get(id), "")%>">
											<%=uF.showData(hmTime.get(id), "Not given")%>	
										</div>
										<% count++;
										 }
										%>	
										<%if(hmDates == null || hmDates.keySet().size()==0){ %>
								           <div style="margin-top: -5px; width: 100%;">
								              <%for(int a=0;a<=3;a++){ %>
								              <input type="hidden" name="preferedDtTmRedio" id="preferedDtTmRedio<%=a %>" >
								              <input type="hidden" name="preferedDate" id="preferedDate<%=a %>" >
								              <input type="hidden" name="preferedTime" id="preferedTime<%=a %>" >
								              <%} %>
								               No Dates Added </div>                 
								               <%}%>
								          	<div style="margin-top: 0px; width: 100%;">
								          	 <span style="float: left;">Specify other Dates
								          	 <input type="hidden" name="status" id="status" value="0"/>
								          	 <input type="checkbox" name="selectionDate" onclick="openSendConfirmation();"></span>
								          	 <span id="sendConfirmationTR" style="float: left; display: none;">
								          	 <%if(request.getAttribute("notiStatus") != null && request.getAttribute("notiStatus").toString().equals("1")) { %>
								          	 	<a href="javascript: void(0);" onclick="SendMailToCandidate();">Resend Mail to Candidate</a>
								          	 <%}else{ %>
								          	 	<a href="javascript: void(0);" onclick="SendMailToCandidate();">Send Mail to Candidate</a>
								          	 <%} %>
								          	 </span>
							               </div> 
									</td>
									<% } else { %>
										<td nowrap="nowrap" colspan="4" class="label" valign="top">&nbsp;</td>
									<%} %>
									<% flag = true;
										} } %>
									</tr>
									
									<tr><td style="" colspan="5"></td></tr>
								<%} %>
								<%} else { %>
									<tr> <td><label><b> Shortlisting process not yet completed. </b></label> </td></tr>
								<% } %>
								<% if(roundIdsRecruitwiseList==null || roundIdsRecruitwiseList.size()==0){ %>
									<tr> <td><label><b> No Panel Added </b> <br/>Please add panel first.</label> </td></tr>
								<% } %>
							</table>
							</div>
							
						</div>
						<script type="text/javascript">
							$(function() {
								$("input[name=interviewdate]").datepicker({format: 'dd/mm/yyyy'});
								$("input[name=preferedDate]").datepicker({format: 'dd/mm/yyyy'});
							});
						</script>

					<%} %>
	                </div>
	                <div <%if(callType!=null && callType.equals("finalisation")) { %> class="active tab-pane" <% } else { %> class="tab-pane" <% } %> id="ina">
		                
							<table class="table">
							<tr><td>
							<%  
								Map<String, List<List<String>>> hmPanelData=(Map<String, List<List<String>>>)request.getAttribute("hmPanelData");
								for (int i = 0; roundIdsRecruitwiseList != null && i<roundIdsRecruitwiseList.size(); i++) {
                                    List<List<String>> roundDataList = hmPanelData.get(roundIdsRecruitwiseList.get(i));
                                     //List<String> roundPanelNameList = hmpanelNameRAndRwise.get(roundIdsRecruitwiseList.get(i));
								%>
								<table style="width: 98%;">
								<tr>
									<td><b> Round <%=i + 1%>:</b></td>
								</tr>
								
								<% 
								//System.out.println("hmAssessRateRoundIdWise ===>> " + hmAssessRateRoundIdWise + " --- " + hmAssessRateRoundIdWise.get(roundIdsRecruitwiseList.get(i)+"_"+hmRoundAssessment.get(roundIdsRecruitwiseList.get(i)+"_ID")));
								if(uF.parseToInt(hmRoundAssessment.get(roundIdsRecruitwiseList.get(i)+"_ID")) > 0) { %>
								<tr>
									<td>
										<div style="float: left; width: 100%;">
											<span>Assessment:</span> 
											<span><a href="javascript:void(0);" style="font-weight: normal;" onclick="viewAssessmentDetail('<%=hmRoundAssessment.get(roundIdsRecruitwiseList.get(i)+"_ID") %>', '<%=uF.showData(hmRoundAssessment.get(roundIdsRecruitwiseList.get(i)+"_NAME"), "") %>')"><%=uF.showData(hmRoundAssessment.get(roundIdsRecruitwiseList.get(i)+"_NAME"), "") %></a></span>
											<% if(uF.parseToInt(hmRoundAssessment.get(roundIdsRecruitwiseList.get(i)+"_ID")) > 0 && uF.parseToDouble(hmAssessRateRoundIdWise.get(roundIdsRecruitwiseList.get(i)+"_"+hmRoundAssessment.get(roundIdsRecruitwiseList.get(i)+"_ID"))) > 0) { %>
												<span id="assessScoreCard_<%=roundIdsRecruitwiseList.get(i) %>" style="margin-left: 7px;">
													<a href="javascript:void(0);" onclick="viewAssessmentScoreSummary('<%=hmRoundAssessment.get(roundIdsRecruitwiseList.get(i)+"_ID") %>', '<%=candidateID%>', '<%=recruitID %>', '<%=roundIdsRecruitwiseList.get(i) %>');"><%=uF.showData(hmAssessRateRoundIdWise.get(roundIdsRecruitwiseList.get(i)+"_"+hmRoundAssessment.get(roundIdsRecruitwiseList.get(i)+"_ID")), "NA") %>%</a>
												</span>
											<% } %>
										</div>
									</td>
								</tr>
								<% } %>
								
								<% 
									//System.out.println("strSessionEmpID ===>> " + strSessionEmpID+" -- roundDataList ===>> " + roundDataList);
                                    for (int j = 0; roundDataList !=null && j<roundDataList.size(); j++) {
                                    	List<String> alInner = roundDataList.get(j);
									if (alInner!=null && !alInner.isEmpty() && !uF.parseToBoolean(alInner.get(0))) { // && !alInner.get(5).equals(strSessionEmpID)
								%>
								<tr>
									<td><b>Panelist: </b><%=hmEmpName.get(alInner.get(5))%>&nbsp;No Interview taken Yet</td>
								</tr>
								<%
									} else {   // showing panel comments and star rating
										//System.out.println("roundIdsRecruitwiseList.get(i) ===>> " + roundIdsRecruitwiseList.get(i));
										//System.out.println("alInner.get(5) ===>> " + alInner.get(5) + " --- alInner.get(3) ===>> " + alInner.get(3));
								%>
								<tr>
									<td><b>Panelist: </b><%=hmEmpName.get(alInner.get(5))%></td>
								</tr>
								<tr>
									<td><b>Comments: </b><%=alInner !=null && alInner.size()>1 ? uF.showData(alInner.get(1), "") : ""%></td>
								</tr>
								<tr>
									<td><div style="float: left;"><b>Status: </b>
											<% if (uF.parseToInt(alInner.get(2)) == 1) { %>
											Approved for next Round.
											<% } else if (uF.parseToInt(alInner.get(2)) == -1) {
											 							//selectedFlag = 1;
											 %>
											 Candidate Rejected.
											 <% } %>
										 </div>
										 <% if(alInner.get(6)!=null) { %>
											 <%if(docRetriveLocation==null) { %>
							                    <div style="float: left; margin-left: 5px;">
							                        <a href="<%=request.getContextPath()+ IConstants.DOCUMENT_LOCATION + alInner.get(6) %>"><i class="fa fa-file-o" aria-hidden="true"></i></a>
							                        <img class="profile-user-img img-responsive img-circle lazy"  id="profilecontainerimg" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + alInner.get(6)%>" >
							                    </div>
							                    <% } else { %>
							                    <div style="float: left; margin-left: 5px;">
							                        <a href="<%=docRetriveLocation +IConstants.I_RECRUITMENT+"/"+IConstants.I_DOCUMENT+"/"+recruitID+"/"+candidateID+"/"+roundIdsRecruitwiseList.get(i)+"/"+alInner.get(6) %>"><i class="fa fa-file-o" aria-hidden="true"></i></a>
							                    </div>
							                    <% } %>
						                    <% } %>
								 </td>
								</tr>
								<tr>
									<td style="border-bottom: 1px solid #C6C6C6; padding-bottom: 9px;"><div id="starPrimaryValue<%=roundIdsRecruitwiseList.get(i)+"_"+alInner.get(5)%>" style="float:left;"></div>
									 <input type="hidden" id="panelrating" name="panelrating" /> 
									 
								 <script type="text/javascript">
								 $(function() {
									 $('#starPrimaryValue<%=roundIdsRecruitwiseList.get(i)%>_<%=alInner.get(5)%>').raty({
											readOnly: true,
											start:	<%= uF.parseToDouble(alInner != null && alInner.size()>2 ? alInner.get(3) : "0")%>  ,
											half: true,
										 	targetType: 'number'
											});
					                  });
				                 </script>
									 
									 </td>
								</tr>
								<% } %>

								<% } %>
								</table>
								
							<%} 
								if(roundIdsRecruitwiseList == null || roundIdsRecruitwiseList.isEmpty() || roundIdsRecruitwiseList.size() == 0 ||
									hmPanelDataHR == null || hmPanelDataHR.keySet().size() == 0) {
							%>
							<table style="width: 98%">
								<tr>
									<td>No Schedule Added</td>
								</tr>
								</table>
							<% } %>	
							</td>
							</tr>
							
							<%
							 Map<String,List<String>> hmCommentsHr = (Map)request.getAttribute("hmCommentsHr");
                            List<String> alInner = new ArrayList<String>();
                            if(hmCommentsHr!= null){
                           		alInner = hmCommentsHr.get(candidateID);
                            }
                            // Started By Dattatray Date:25-10-21
								String strDisplayNone = "";
								if(apptype !=null && apptype.equals("reject")){
									strDisplayNone = "display:none";
								}
							//	Ended By Dattatray Date:25-10-21
							%>
							<% Map<String, String> hmOfferNegoReqData = (Map<String, String>) request.getAttribute("hmOfferNegoReqData"); %>
							<%-- <% if((alInner !=null && !alInner.isEmpty() && uF.parseToInt(alInner.get(0))==0 && uF.parseToInt(alInner.get(5))==2) || (alInner.get(2) !=null && hmOfferNegoReqData!=null && hmOfferNegoReqData.size()>0)) { %> --%>
							<!-- ====start parvez on 10-07-2021==== -->
							<% if((alInner !=null && !alInner.isEmpty()) && (uF.parseToInt(alInner.get(0))==0 && uF.parseToInt(alInner.get(5))==2 || alInner.get(2) !=null && hmOfferNegoReqData!=null && hmOfferNegoReqData.size()>0)) { %>
							<!-- ====start parvez on 10-07-2021==== -->
							
								<tr style="<%=strDisplayNone %>;"><!-- Created By Dattatray Date:25-10-21 -->
									<td>
										<s:form name="frmOfferFinalizationCommunication" id="frmOfferFinalizationCommunication" theme="simple" action="CandidateMyProfilePopup" method="post">
											<s:hidden name="recruitId" id="recruitId"></s:hidden>
											<s:hidden name="CandID" id="CandID"></s:hidden>
											<s:hidden name="form" id="form" />
											<table>
												<tr><td><label><b>Offer negotiation communication:</b></label></td></tr>
												<tr>
													<td>
														<% if(hmOfferNegoReqData !=null && hmOfferNegoReqData.get("REQUESTED_REMARK") !=null) { %>
															<%=hmOfferNegoReqData.get("REQUESTED_REMARK") %>
														<% } else { %>
															<textarea rows="3" cols="57" style="width: 100% !important; resize: none;" placeholder="Please do communication for finalization..." name="offerReleaseCommunication"></textarea>
														<% } %>
													</td>
												</tr>
												<tr>
													<td style="padding-top: 5px;">
														<% if(hmOfferNegoReqData !=null && hmOfferNegoReqData.get("NEED_APPROVAL_STATUS") !=null) { %>
															Does this need approval for offer release?: <%=uF.parseToBoolean(hmOfferNegoReqData.get("NEED_APPROVAL_STATUS")) ? "Yes" : "No" %> <br/>
															<% if(uF.parseToBoolean(hmOfferNegoReqData.get("NEED_APPROVAL_STATUS"))) { %>
																Approver Name: <%=hmOfferNegoReqData.get("NEGOTIATION_APPROVER") %>
															<% } %>
														<% } else { %>
															<span style="float: left; "> Does this need approval for offer release?: <input type="checkbox" name="needApprovalForOfferRelease" id="needApprovalForOfferRelease" checked="checked" onclick="checkNeedApproval()"/></span>
															<span id="spanOfcWorkflow" style="float: left; padding-left: 10px;">
																<s:select name="strOCApprover" id="strOCApprover" listKey="employeeId" listValue="employeeCode" headerKey="" headerValue="Select Approver" list="empList" />
															</span>
														<% } %>
													</td>
												</tr>
												<tr>
													<td>
														<% if(hmOfferNegoReqData != null && hmOfferNegoReqData.size()>0 && hmOfferNegoReqData.get("APPROVED_BY")==null) { %>
															<input type="button" class="btn btn-primary" value="Revoke and Send New Negotiation" name="ofcDelete" id="ofcDelete" onclick="revokeExistingNegotiationCommunication();" />
														<% } else if(hmOfferNegoReqData == null || hmOfferNegoReqData.size()==0) { %>
															<input type="submit" class="btn btn-primary" value="Send for Approval" name="ofcSubmit" id="ofcSubmit"/>
														<% } %>
													</td>
												</tr>
											</table>
										</s:form>
										
										<% if(hmOfferNegoReqData != null && hmOfferNegoReqData.size()>0 && uF.parseToBoolean(hmOfferNegoReqData.get("NEED_APPROVAL_STATUS"))) { %>
											<s:form name="frmOfferFinalizationCommunicationApproval" id="frmOfferFinalizationCommunicationApproval" theme="simple" action="CandidateMyProfilePopup" method="post">
												<s:hidden name="recruitId" id="recruitId"></s:hidden>
												<s:hidden name="CandID" id="CandID"></s:hidden>
												<s:hidden name="form" id="form" />
												<table>
													<tr><td><label><b>Offer negotiation approve communication:</b></label></td></tr>
													<tr>
														<td>
															<% if(hmOfferNegoReqData !=null && hmOfferNegoReqData.get("APPROVED_REMARK") !=null) { %>
																<%=hmOfferNegoReqData.get("APPROVED_REMARK") %>
															<% } else { %>
																<textarea rows="3" cols="57" style="width: 100% !important; resize: none;" placeholder="Please do communication for negotiation approval..." name="offerNegotiationApproval"></textarea>
															<% } %>
														</td>
													</tr>
													<tr>
													<td style="padding-top: 5px;">
														<% if(hmOfferNegoReqData !=null && hmOfferNegoReqData.get("APPROVED_BY") !=null) { %>
															Approve Status: <%=uF.parseToBoolean(hmOfferNegoReqData.get("NEGOTIATION_APPROVE_STATUS")) ? "Yes" : "No" %> <br/>
															<% if(uF.parseToBoolean(hmOfferNegoReqData.get("NEED_APPROVAL_STATUS"))) { %>
																<% if(uF.parseToBoolean(hmOfferNegoReqData.get("NEGOTIATION_APPROVE_STATUS"))) { %>
																	Approved <% } else { %> Denied <% } %> by: <%=hmOfferNegoReqData.get("APPROVED_BY") %> on <%=hmOfferNegoReqData.get("APPROVE_DATE") %>
															<% } %>
														<% } else { %>
															<span style="float: left; "> Approve this request for offer release: <input type="checkbox" name="needApproveRequestForOfferRelease" checked="checked"/></span>
														<% } %>
													</td>
												</tr>
													<tr>
														<td>
															<% if(hmOfferNegoReqData != null && hmOfferNegoReqData.get("APPROVED_BY")==null) { %>
																<input type="submit" class="btn btn-primary" value="Save & Approve" name="ofcApprove" id="ofcApprove"/>
															<% } %>
														</td>
													</tr>
												</table>
											</s:form>
										<% } %>
									</td>
								</tr>
							<% } %>
							
							<% 
							//System.out.println("alInner ===>> " + alInner);
							
							if(alInner !=null && !alInner.isEmpty()) { // Start Dattatray Date : 10-08-21
								if(alInner.get(2)==null || alInner.get(2).equals("") || alInner.get(2).equals("-")){// Start Dattatray Date : 10-08-21
							%>
								<tr>
									<td>
										<table>
											<tr><td><label><b>Select new job profile to transfer this candidate for suitable job profile:</b></label></td></tr>
											<tr>
												<td>
													<s:select theme="simple" name="strLiveJobs" id="strLiveJobs" listKey="strJDId" listValue="strJDName" headerKey="0" headerValue="Select Job Profile" list="liveJDList"/>&nbsp;&nbsp;&nbsp;&nbsp;
													<input type="submit" class="btn btn-primary" style="margin-top: -6px;" value="Change Job Profile" name="changeJDsubmit" id="changeJDsubmit" onclick="changeJobProfile();"/>
												</td>
											</tr>
										</table>
									</td>
								</tr>
							<% } } %>
							
							<!-- Started By Dattatray Date:18-10-21 -->
							<%
							boolean isRoundComplete;
							if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_FINALISATION_WITH_FIRST_ROUND_FEEDBACK))) {
									List<String> alInnerInterviewTaken = hmPanelInterviewTaken.get(roundIdsRecruitwiseList.get(0));
									if (alInnerInterviewTaken != null && alInnerInterviewTaken.size() > 0) {
										isRoundComplete = true;
									}else{
										isRoundComplete = false;
									}
							}else{
								isRoundComplete = true;
							}
							%>
							<!-- Ended By Dattatray Date:18-10-21 -->
							
							<!-- Created By Dattatray Date:18-10-21 Note: Condition checked if else-->
							<% if(isRoundComplete) {%>
							<tr style="<%=strDisplayNone %>;"><!-- Created By Dattatray Date:25-10-21 -->
								<td>
								<s:form name="frmIntCommentHR" id="frmIntCommentHR" theme="simple" action="CandidateMyProfilePopup" method="post">
									<s:hidden name="recruitId" id="recruitId"></s:hidden>
									<s:hidden name="CandID" id="CandID"></s:hidden>
									<s:hidden name="form" id="form" />
									<table>
	                                 <%	
	                                   if(alInner !=null && !alInner.isEmpty() && uF.parseToInt(alInner.get(0))==0 && uF.parseToInt(alInner.get(5))==2){ //hmPanelDataHR != null && hmPanelDataHR.keySet().size() != 0 && alInner!=null  && !alInner.isEmpty()  && uF.parseToInt(alInner.get(0))==0 && pnlsize==givenpnlsize
	                                 %>
									<tr>
										<td><label><b>HR Decision:</b></label><sup>*</sup></td>
									</tr>
									<tr>
										<td style="width: auto;"><textarea rows="2" style="width: 556px !important;" name="strinterviewcommentHR" class="validateRequired"></textarea></td>
									</tr>
	
									<tr>
										<td>
										 <s:radio id="hrchoice" name="hrchoice" list="#{'1':'Offer', '0':'Reject'}" value="0" onchange="joiningCall(this.value);"/>
										</td>
									</tr>
									
									<tr id="joiningTR" style="display: none;">
										<td>Joining Date:<sup>*</sup> 
											<s:textfield name="joiningdate" id="joiningdate" cssClass="validateRequired text-input" required="true" cssStyle="width:105px"></s:textfield>
											<div style="float: right;"> Projects
												<select name="strDesigProjects" id="strDesigProjects">
													<option value="">Select Project</option>
													<%=(String) request.getAttribute("sbDesigProjectList") %>
												</select>
											</div>
										</td>
									</tr>
						<!-- ===start parvez date: 10-01-2021=== -->			
								<% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_ENABLE_JOINING_BONUS_DETAILS))) { %>	
									<tr id="additionCommentTR" style="display: none;">
										<td>
											<!-- </br> -->
											<div style="margin-top: 10px;">Additional Comment:</div>
											<div>
												<textarea rows="2" style="width: 556px !important;" name="strAdditionalComment" ></textarea>
												<div style="float: right;">[JOINING_BONUS]</div>
											</div>
										</td>
									</tr>
									<tr id="joiningBonusTR" style="display: none;">
										<td>
											<div style="margin-top: 10px;">
												<s:select name="strJoiningBonus" id="strJoiningBonus" listKey="salaryHeadId" listValue="salaryHeadName" headerKey="" headerValue="Select Joining Bonus" list="salaryHeadList" />
												<div style="float: right;">Joining Bonus Amount
													<s:textfield name="strJoiningBonusAmount" id="strJoiningBonusAmount" cssClass="text-input" cssStyle="width:105px" onkeypress="return isNumberKey(event)"></s:textfield>
												</div>
											</div>
										</td>
									</tr>
								<% } %>	
						<!-- ===end parvez date: 10-01-2021=== -->			
									
									<%} else if(alInner!=null  && !alInner.isEmpty()){ //&& alInner!=null && !alInner.isEmpty() && pnlsize==givenpnlsize%>
									<tr><td>
								<hr style="background-color: buttontext;">
									</td></tr>
									
									<tr>
									<td>
									<div> <b>Candidate Status :</b> 
									<%if(uF.parseToInt(alInner.get(5))==2){ %>
									<%if(uF.parseToInt(alInner.get(0))==1){ %>
									<%if(uF.parseToInt(alInner.get(4))==0){ %>
									<strong>Offered</strong>
									<%}else if(uF.parseToInt(alInner.get(4))==1){ %>
									<strong>Selected</strong>
									<%}else if(uF.parseToInt(alInner.get(4))==-1){ %>
									<strong>Rejected</strong>
									<%}%>
									<%} else if(uF.parseToInt(alInner.get(0))==-1){%>
									<strong>Candidate Rejected.</strong>
									<%}%>
									<%} else if(uF.parseToInt(alInner.get(5))==-1){%>
									<strong>Application Rejected.</strong>
									<%}%>
									</div>
									<div>
								   <span> <b> Joining Date: </b> <strong><%=uF.showData(alInner.get(2) ,"Not filled") %></strong></span>
	                               <span style="padding:50px"> <b> CTC offered:</b> 
	                               <strong><%=uF.showData(uF.formatIntoComma(uF.parseToDouble(alInner.get(3))) ,"Not filled") %>  per month</strong>
	                               </span>
	                               
	                               </div>
									</td>
									</tr>
									
									<%} %>
							
									<tr id="ctcDisplayTR" style="display: none;">
										<td>
											<div id="salDiv">
												<s:action name="CandidateSalaryDetails" executeResult="true">
													<s:param name="CandID" value="#CandID"/>
													<s:param name="recruitId" value="#recruitId"/>
												</s:action>
											</div>
										</td>
									</tr>
									  <% if(alInner!=null  && !alInner.isEmpty() && uF.parseToInt(alInner.get(0))==0  && uF.parseToInt(alInner.get(5))==2) { %>                               
									<tr>
										<td style="float: right">
											<input type="button" style="margin: 0px; display: none;" class="btn btn-primary" value="Preview Offer" name="preveiwOffer" id="preveiwOffer" onclick="previewOfferLetter('<%=candidateID%>', '<%=recruitID %>');" >										
											<!-- <input type="submit" style="margin: 0px" class="btn btn-primary" value="Save Offer" name="saveOffer" id="saveOffer" /> -->
											<input type="submit" style="margin: 0px;" class="btn btn-primary" value="Save & Reject" name="hrsubmit" id="hrsubmit"/>
										</td>
									</tr>
	                                 <%} %>
	                                </table> 
	                             </s:form>   
	                                
                                </td>
							</tr>
                            
                           <%}else{ %>
                           <!-- Created By Dattatray Date:25-10-21 -->
							 <tr style="<%=strDisplayNone %>;">
							 
								<td>Feedback of at least One Round required for rolling out Offer Letter</td>
							</tr>
							<%} %>
                            
							</table>
						
                </div>
                <% } %>
					
				<div class="tab-pane" id="resume" >
				<script type="text/javascript">
				//(function($){
					$.fn.gdocsViewer = function(options) {
						var settings = {
							width  : '98%',
							height : '742'
						};
						
						if (options) { 
							$.extend(settings, options);
							
						}
						
						return this.each(function() {
							var file = $(this).attr('href');
				            
				            var ext=file.substring(file.lastIndexOf(".")+1);
				            
				            console.log("Extension : "+ext);
							if (/^(tiff|pdf|ppt|pptx|pps|doc|docx|txt|xls|xlsx)$/.test(ext)) {
								$(this).after(function () {
									var id = $(this).attr('id');
									var gdvId = (typeof id !== 'undefined' && id !== false) ? id + '-gdocsviewer' : '';
									return '<div id="' + gdvId + '" class="gdocsviewer" style="width:98%; height: 742px;"><iframe src="https://docs.google.com/viewer?embedded=true&url=' + encodeURIComponent(file) + '"  style="width:98%; height: 742px; border: none;margin : 0 auto; display : block;"></iframe></div>';
								})
							}
						});
					};
				//})( jQuery );
				
				/* return '<div id="' + gdvId + '" class="gdocsviewer"><iframe src="https://docs.google.com/viewer?embedded=true&url=' + encodeURIComponent(file) + '" width="' + settings.width + '" height="' + settings.height + '" style="border: none;margin : 0 auto; display : block;"></iframe></div>'; */
				$(document).ready(function () {
				    $('a.embed').gdocsViewer();
				    //$('#embedURL').gdocsViewer();
				});
				
				
				
				</script>
				<% 
				List<String> availableExt = (List<String>)request.getAttribute("availableExt");
				if(availableExt == null) availableExt = new ArrayList<String>();
				
				//System.out.println("fileExt ===>> " + fileExt +" --- availableExt ===>> " + availableExt);
				boolean flag = false;
				if(fileExt!=null && availableExt.contains(fileExt)){
					flag = true;
				}
				
				//String filePath = (String) request.getParameter("filePath"); %>
				
				<% if(flag) { %>
					<div style="margin-right: 10px;"><a href="<%=filePath %>" target="_blank" title="Click here to download Resume"><i class="fa fa-download" aria-hidden="true"></i>Download</a></div>
					<div id="tblDiv" style="float: left;width: 100%;margin-left:10px;">
						<a href="<%=filePath %>" class="embed" id="test">&nbsp;</a>
					</div>
				<%	} else { %>
					<div id="tblDiv" style="float: left;width: 100%;margin-left:10px;height: 500px; background-color: #CCCCCC;">
						<div style="text-align: center; font-size: 24px; padding: 150px;">Document not available</div>
					</div>
				<%	} %>
				</div>		
							

			
            <div <%if(callType!=null && callType.equals("calendar")) { %> class="active tab-pane" <% } else { %> class="tab-pane" <% } %> id="timeline" >
			<%	if (RID != null && !RID.equals("")) { // && !strUserType.equalsIgnoreCase(IConstants.HRMANAGER) && !strUserType.equalsIgnoreCase(IConstants.ADMIN)
				String strInterviewFinalStatus = (String)request.getAttribute("strInterviewFinalStatus");
				
				Map<String, List<List<String>>> hmPanelData=(Map<String, List<List<String>>>)request.getAttribute("hmPanelData");
				//Map<String, String> hmEmpName = (Map<String, String>)request.getAttribute("hmEmpName");
			%>	
            
            <div class="trow">
			<div>
				<div class="fieldset">
					<fieldset style="background-color: #EFEFEF">
						<legend style="font-size: 16px !important;">Interview Information of Candidate</legend>
						
								<!-- <table style="width: 98%"> -->
								
								<%
								
									for (int i = 0; roundIdsRecruitwiseList != null && i<roundIdsRecruitwiseList.size(); i++) {
                                     List<List<String>> roundDataList = hmPanelData.get(roundIdsRecruitwiseList.get(i));
								%>
								<table style="width: 99% !important;">
								<tr>
									<td><b> Round <%=i + 1%>:</b></td>
								</tr>
								<% 
                                      for (int j = 0; roundDataList !=null && j<roundDataList.size(); j++) { 
                                    	  List<String> alInner = roundDataList.get(j);
									if (alInner!=null && !alInner.isEmpty() && !uF.parseToBoolean(alInner.get(0)) && !alInner.get(5).equals(strSessionEmpID) && strUserType.equals(IConstants.EMPLOYEE)) {
								%>
								<tr>
									<td> <b>Panelist: </b><%=hmEmpName.get(alInner.get(5))%>&nbsp;No Interview taken Yet</td>
								</tr>
								<% } else if (alInner!=null && !alInner.isEmpty() && !uF.parseToBoolean(alInner.get(0)) && (alInner.get(5).equals(strSessionEmpID) || strUserType.equals(IConstants.RECRUITER) || strUserType.equals(IConstants.HRMANAGER) || strUserType.equals(IConstants.ADMIN))) { %>
								<tr>
									<td><b>Panelist: </b><%=hmEmpName.get(alInner.get(5)) %></td>
								</tr>
								<% if(uF.parseToInt(strMinRoundId) == uF.parseToInt(roundIdsRecruitwiseList.get(i))) { %>
								<% if(uF.parseToInt(strInterviewFinalStatus) > 0) { %>
									<tr><td>No interview taken yet, but candidate finalized by HR. </td></tr>
								<% } else { %>
								<tr>
									<td>
										<s:form name="frmIntComment" theme="simple" id="frmIntComment" action="CandidateMyProfilePopup" method="post" enctype="multipart/form-data">
										<s:hidden name="recruitId" id="recruitId"></s:hidden>
										<s:hidden name="CandID" id="CandID"></s:hidden>
										<input type="hidden" name="userId" value="<%=alInner.get(5) %>"/>
										<s:hidden name="form" id="form"></s:hidden>
										<s:hidden name="intSubmitComment" id="intSubmitComment" value="Save"></s:hidden>
										<table style="width: 100% !important;">
										<tr>
											<td>
											<input type="hidden" name="roundID" value="<%=roundIdsRecruitwiseList.get(i) %>" />
											<textarea rows="4" cols="57" style="width: 100% !important; resize: none;" placeholder="Please provide feedback..." name="interviewcomment" class="validateRequired"></textarea>
											</td>
										</tr>
		
										<tr>
											<td align="right"> Rate the candidate: 
											<div id="starPrimary<%=roundIdsRecruitwiseList.get(i)+"_"+alInner.get(5)%>" style="float:right;"></div> <input type="hidden"
												id="panelrating<%=roundIdsRecruitwiseList.get(i)+"_"+alInner.get(5)%>" name="panelrating" /> 
													
											<script type="text/javascript">
												$(function() {
													$('#starPrimary<%=roundIdsRecruitwiseList.get(i)+"_"+alInner.get(5)%>').raty({
														readOnly: false,
														start: 0,
														half: true,
														targetType: 'number',
														click: function(score, evt) {
							                               	// alert('ID: ' + $(this).attr('id') + "\nscore: " + score + "\nevent: " + evt);
															$('#panelrating<%=roundIdsRecruitwiseList.get(i)+"_"+alInner.get(5)%>').val(score);
														}
													});
		                                        });
		                                    </script>
											</td>
										</tr>
										
										<tr>
											<td><div><span style="float: right;"> Approve for next round: <input type="checkbox" name="approvedeny" checked="checked" value="true" /> </span></div></td>
										</tr>
										
										<tr> 
											<td><span style="float: left;">Attach file here: </span>
												<span style="float: left;">
												<span id="file"></span>
												<s:file theme="simple" accept=".gif,.jpg,.png,.tif,.svg, .svgz,.xls,.pdf,.ppt,.doc,.docs" name="strInterviewDocument" onchange="readFileURL(this, 'file');"/>
												</span>
											</td>
										</tr>
										
										<tr>
											<td>
											<div style="float: right;">
												<input type="submit" value="SAVE" name="intSubmitComment" class="btn btn-primary" />
											</div>
											</td>
										</tr>
										</table>
										</s:form>
									</td>
								</tr>
								<% } %>
								
								<% } else { %>
								<tr>
									<td>This feedback section will be enabled once previous round is completed.</td>
								</tr>
								<%	}
								} else {   // showing panel comments and star rating
								%>
								<tr>
									<td><b>Panelist: </b><%=hmEmpName.get(alInner.get(5))%></td>
								</tr>
								<tr>
									<td><div style="float: left;"><b>Comments: </b><%=alInner !=null && alInner.size()>1 ? uF.showData(alInner.get(1), "") : ""%></div>
										<% if(alInner.get(6)!=null) { %>
											 <%if(docRetriveLocation==null) { %>
							                    <div style="float: left; margin-left: 5px;">
							                        <a href="<%=request.getContextPath()+ IConstants.DOCUMENT_LOCATION + alInner.get(6) %>"><i class="fa fa-file-o" aria-hidden="true"></i></a>
							                        <img class="profile-user-img img-responsive img-circle lazy"  id="profilecontainerimg" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + alInner.get(6)%>" >
							                    </div>
							                    <% } else { %>
							                    <div style="float: left; margin-left: 5px;">
							                        <a href="<%=docRetriveLocation +IConstants.I_RECRUITMENT+"/"+IConstants.I_DOCUMENT+"/"+recruitID+"/"+candidateID+"/"+roundIdsRecruitwiseList.get(i)+"/"+alInner.get(6) %>"><i class="fa fa-file-o" aria-hidden="true"></i></a>
							                    </div>
						                    <% } %>
					                    <% } %>
									</td>
								</tr>
								<tr>
									<td style="border-bottom: 1px solid #C6C6C6;  padding-bottom: 9px;"> <div id="starPrimaryValue1<%=roundIdsRecruitwiseList.get(i)+"_"+alInner.get(5)%>" style="float:left;"></div>
									 <input type="hidden" id="panelrating" name="panelrating" /> 
									 
									 <script type="text/javascript">
										$(function() {
											$('#starPrimaryValue1<%=roundIdsRecruitwiseList.get(i)%>_<%=alInner.get(5)%>').raty({
												readOnly: true,
												start:	<%= uF.parseToDouble(alInner != null && alInner.size()>2 ? alInner.get(3) : "0")%>  ,
												half: true,
												targetType: 'number'
											});
					                  	});
				                 </script>
									 </td>
								</tr>
                                    
								<% } %>
								<% } %>
							</table>
							<%} %>
							
							<% Map<String, String> hmOfferNegoReqData = (Map<String, String>) request.getAttribute("hmOfferNegoReqData"); %>
							<%
								// Created by Dattatray Date : 03-July-21 Note Condition checked 
								if(uF.parseToInt(strSessionEmpID) == uF.parseToInt(hmOfferNegoReqData.get("NEGOTIATION_APPROVER_ID")) || uF.parseToInt(strSessionEmpID) == uF.parseToInt(hmOfferNegoReqData.get("REQUESTED_BY_ID"))){
							%>
								<% if(hmOfferNegoReqData != null && hmOfferNegoReqData.size()>0) { %>	
										<table>
											<tr><td><label><b>Offer negotiation communication:</b></label></td></tr>
											<tr>
												<td>
													<% if(hmOfferNegoReqData !=null && hmOfferNegoReqData.get("REQUESTED_REMARK") !=null) { %>
														<%=hmOfferNegoReqData.get("REQUESTED_REMARK") %>
													<% } %>
												</td>
											</tr>
											<tr>
												<td style="padding-top: 5px;">
													<% if(hmOfferNegoReqData !=null && hmOfferNegoReqData.get("NEED_APPROVAL_STATUS") !=null) { %>
														Does this need approval for offer release?: <%=uF.parseToBoolean(hmOfferNegoReqData.get("NEED_APPROVAL_STATUS")) ? "Yes" : "No" %> <br/>
														<% if(uF.parseToBoolean(hmOfferNegoReqData.get("NEED_APPROVAL_STATUS"))) { %>
															Approver Name: <%=hmOfferNegoReqData.get("NEGOTIATION_APPROVER") %>
														<% } %>
													<% } %>
												</td>
											</tr>
										</table>
									
								<% if(hmOfferNegoReqData != null && hmOfferNegoReqData.size()>0 && uF.parseToBoolean(hmOfferNegoReqData.get("NEED_APPROVAL_STATUS"))) { %>
									<s:form name="frmOfferFinalizationCommunicationApproval" id="frmOfferFinalizationCommunicationApproval" theme="simple" action="CandidateMyProfilePopup" method="post">
										<s:hidden name="recruitId" id="recruitId"></s:hidden>
										<s:hidden name="CandID" id="CandID"></s:hidden>
										<s:hidden name="form" id="form" />
										<table>
											<tr><td><label><b>Offer negotiation approve communication:</b></label></td></tr>
											<tr>
												<td>
													<% if(hmOfferNegoReqData !=null && hmOfferNegoReqData.get("APPROVED_REMARK") !=null) { %>
														<%=hmOfferNegoReqData.get("APPROVED_REMARK") %>
													<% } else { %>
														<textarea rows="3" cols="57" style="width: 100% !important; resize: none;" placeholder="Please do communication for negotiation approval..." name="offerNegotiationApproval"></textarea>
													<% } %>
												</td>
											</tr>
											<tr>
											<td style="padding-top: 5px;">
												<% if(hmOfferNegoReqData !=null && hmOfferNegoReqData.get("APPROVED_BY") !=null) { %>
													Approve Status: <%=uF.parseToBoolean(hmOfferNegoReqData.get("NEGOTIATION_APPROVE_STATUS")) ? "Yes" : "No" %> <br/>
													<% if(uF.parseToBoolean(hmOfferNegoReqData.get("NEED_APPROVAL_STATUS"))) { %>
														<% if(uF.parseToBoolean(hmOfferNegoReqData.get("NEGOTIATION_APPROVE_STATUS"))) { %>
															Approved <% } else { %> Denied <% } %> by: <%=hmOfferNegoReqData.get("APPROVED_BY") %> on <%=hmOfferNegoReqData.get("APPROVE_DATE") %>
													<% } %>
												<% } else { %>
													<span style="float: left; "> Approve this request for offer release: <input type="checkbox" name="needApproveRequestForOfferRelease" checked="checked"/></span>
												<% } %>
											</td>
										</tr>
											<tr>
												<td>
													<% if(hmOfferNegoReqData != null && hmOfferNegoReqData.get("APPROVED_BY")==null) { %>
														<input type="submit" class="btn btn-primary" value="Save & Approve" name="ofcApprove" id="ofcApprove"/>
													<% } %>
												</td>
											</tr>
										</table>
									</s:form>
								<% } %>
							<% } %>
							<% } %>
					</fieldset>
				</div>
			</div>
		</div>
        <% } %>        	
                    	
                    	
                    	
                    	
                    	
					        
						    <ul class="timeline timeline-inverse" style="margin-top: 20px;">
						    <% if ((activityList !=null && activityList.size()>0) || (alPrevEmployment !=null && alPrevEmployment.size()>0)) { %>
					    	
                    			 <%String strDateTmp = "";
					                        for (int i = 0; activityList != null && i < activityList.size(); i++) {
					                        	List<String> innerList=activityList.get(i);
					                        String strColor = "#FFA500";
					                        String strStatus = "";
					                        String strMsg = "";
					                        String strDate = "";
					                        String roundName = "";
					                        
					                        if( uF.parseToInt(innerList.get(5)) == IConstants.CANDI_ACTIVITY_INTERVIEW_SCHEDULE_ID || uF.parseToInt(innerList.get(5)) == IConstants.CANDI_ACTIVITY_ROUND_SHORTLIST_OR_REJECT_ID) {
					                        	roundName = " of Round "+ innerList.get(4);
					                        }
					                        strStatus = innerList.get(0);
					                        strMsg = innerList.get(0) +" for "+ innerList.get(3) + roundName +" by "+innerList.get(1)+" on "+innerList.get(2);
					                        strDate = innerList.get(2);%>
					                        <%if(strDate != null && strDate.equalsIgnoreCase(strDateTmp)){ %> 
					                        <li>
				                                <i class="fa fa-envelope bg-blue"></i>
				                                <div class="timeline-item">
				                                    <h3 class="timeline-header"><%=strStatus%></h3>
				                                    <div class="timeline-body"><%=strMsg %>
				                                    </div>
				                                </div>
				                            </li>
					                        <%}else{ %>
					                        <li class="time-label">
				                                <span class="bg-red">
				                                	<%=strDate %>
				                                </span>
				                            </li>
				                    		<li>
				                                <i class="fa fa-envelope bg-blue"></i>
				                                <div class="timeline-item">
				                                    <h3 class="timeline-header"><%=strStatus%></h3>
				                                    <div class="timeline-body"><%=strMsg %>
				                                    </div>
				                                </div>
				                            </li>
					                        <% } 
				                            strDateTmp = strDate; 
				                            } %>
				                            
				                            <% if(!uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_TIMELINE_PREVIOUS_EMPLOYMENT_DISABLE))){ %>
				                            <%
					                        if (alPrevEmployment !=null && alPrevEmployment.size()>0) {
					                        	String strDateTmp1 = "";
					                        	for (int i = 0; i < alPrevEmployment.size(); i++) {
					                        		List<String> innerList = (List<String>) alPrevEmployment.get(i);
					                        	String strColor = "#FFA500";
					                        	String strStatus = "";
					                        	String strMsg = "";
					                        	String strDate = "";
					                        	
					                        strStatus = innerList.get(1);
					                        strMsg = "From "+innerList.get(8)+" To "+innerList.get(9);
					                        strMsg =((String) innerList.get(6)).length() != 0 ? " with " + innerList.get(6)+ " designation" : "";
					                        //strDate = "";
					                        if(strDate != null && strDate.equalsIgnoreCase(strDateTmp1)){ 	
					                        %>
					                        <li>
				                                <i class="fa fa-envelope bg-blue"></i>
				                                <div class="timeline-item">
				                                    <h3 class="timeline-header"><%=strStatus%></h3>
				                                    <div class="timeline-body"><%=strMsg %>
				                                    </div>
				                                </div>
				                            </li>
					                        <% }else{ %>
					                         <li class="time-label">
				                                <span class="bg-red">
				                                	<%=strDate %>
				                                </span>
				                            </li>
				                    		<li>
				                                <i class="fa fa-envelope bg-blue"></i>
				                                <div class="timeline-item">
				                                    <h3 class="timeline-header"><%=strStatus%></h3>
				                                    <div class="timeline-body"><%=strMsg %>
				                                    </div>
				                                </div>
				                            </li>
					                        <%} 
					                        strDateTmp1 = strDate; 
					                        }
					              } else { %>
			                    		<li>
			                                
			                                <div class="timeline-item">
			                                    <h3 class="timeline-header">No Previous Employment</h3>
			                                    <div class="timeline-body">No Previous Employment
			                                    </div>
			                                </div>
			                            </li>
					             <% } %>
					           <% } %>  
	                    	
					    <%} else if (alPrevEmployment ==null || alPrevEmployment.size() == 0) {%>
					    	<% if(!uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_TIMELINE_PREVIOUS_EMPLOYMENT_DISABLE))){ %>
					    	<li>
			                                
			                                <div class="timeline-item">
			                                    <h3 class="timeline-header">No Previous Employment</h3>
			                                    <div class="timeline-body">No Previous Employment
			                                    </div>
			                                </div>
			                            </li>
			                <% } %>            
					    <%} %>
                    	</ul>
                    </div>
                </div>
            </div>
    	</section>
    	
    <script>
    	
    </script>