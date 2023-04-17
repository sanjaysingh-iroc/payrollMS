<%@page import="ChartDirector.AngularMeter"%>
<%@page import="java.util.*"%>
<%@page import="com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<style>
hr {
	margin-top: 10px;
	margin-bottom: 10px;
}

.about-header>a {
	float: right;
}

.about-body {
	margin-left: 10px;
}

a.close-font:before {
	font-size: 25px;
}

.profile-user-img {
	padding: 0px;
}
</style>
<link rel="stylesheet" type="text/css"
	href="js_bootstrap/Jcrop/jquery.Jcrop.min.css" />
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<%-- <script type="text/javascript" src="scripts/jquery-ui.min.js"> </script> --%>
<script src="scripts/charts/justgage/raphael-2.1.4.min.js"
	type="text/javascript"></script>
<script src="scripts/charts/justgage/justgage.js" type="text/javascript"></script>
<script type="text/javascript"
	src="<%= request.getContextPath()%>/scripts/_rating/js/jquery.raty.min.js"> </script>

<script type="text/javascript"> 
	var crop_max_width = 400;
	var crop_max_height = 400;
	var jcrop_api;
	var canvas;
	var context;
	var image;
	var flag = 0;
	var prefsize;
	
	function loadImage(input) {
	  if (input.files && input.files[0]) {
	    var reader = new FileReader();
	    canvas = null;
	    reader.onload = function(e) {
	      image = new Image();
	      image.onload = validateImage;
	      image.src = e.target.result;
	    }
	    reader.readAsDataURL(input.files[0]);
	  }
	}
	
	function dataURLtoBlob(dataURL) {
	  var BASE64_MARKER = ';base64,';
	  if (dataURL.indexOf(BASE64_MARKER) == -1) {
	    var parts = dataURL.split(',');
	    var contentType = parts[0].split(':')[1];
	    var raw = decodeURIComponent(parts[1]);
	
	    return new Blob([raw], {
	      type: contentType
	    });
	  }
	  var parts = dataURL.split(BASE64_MARKER);
	  var contentType = parts[0].split(':')[1];
	  var raw = window.atob(parts[1]);
	  var rawLength = raw.length;
	  var uInt8Array = new Uint8Array(rawLength);
	  for (var i = 0; i < rawLength; ++i) {
	    uInt8Array[i] = raw.charCodeAt(i);
	  }
	
	  return new Blob([uInt8Array], {
	    type: contentType
	  });
	}
	
	function validateImage() {
	  if (canvas != null) {
	    image = new Image();
	    flag = 1;
	    image.onload = restartJcrop;
	    image.src = canvas.toDataURL('image/png');
	  } else restartJcrop();
	}
	
	function restartJcrop() {
	  if (jcrop_api != null) {
	    jcrop_api.destroy();
	  }
	  $("#views").empty();
	  $("#views").append("<canvas id=\"canvas\">");
	  canvas = $("#canvas")[0];
	  context = canvas.getContext("2d");
	  
	  
	  canvas.width = image.width;
	  canvas.height = image.height;
	  context.drawImage(image, 0, 0);
	if(flag !== 1){
	    $("#canvas").Jcrop({
	      onSelect: selectcanvas,
	      aspectRatio: 1,
	      onRelease: releaseCheck,
	      boxWidth: crop_max_width,
	      boxHeight: crop_max_height,
	      setSelect: [ 175, 100, 400, 300 ]
	    }, function() {
	      jcrop_api = this;
	    });
	  }else{
		$("#canvas").Jcrop({
		  allowSelect: false,
	      boxWidth: 200,
	      boxHeight: 200
	    });
	  }
	}
		
	function clearcanvas() {
	  prefsize = {
	    x: 0,
	    y: 0,
	    w: canvas.width,
	    h: canvas.height
	  };
	}
	
	function releaseCheck() {
	    this.setOptions({ setSelect: [ 175, 100, 400, 300 ] });
	}
	
	function selectcanvas(coords) {
	  prefsize = {
	    x: Math.round(coords.x),
	    y: Math.round(coords.y),
	    w: Math.round(coords.w),
	    h: Math.round(coords.h)
	  };
	}
	
	function applyCrop() {
      $("#cropbutton").hide();
	  canvas.width = prefsize.w;
	  canvas.height = prefsize.h;
	  context.drawImage(image, prefsize.x, prefsize.y, prefsize.w, prefsize.h, 0, 0, canvas.width, canvas.height);
	  validateImage();
	}
	
	

    $(function(){
    	
    		
    	$("body").on('click','#closeButton',function(){
    		$(".modal-dialog").removeAttr('style');
    		$(".modal-body").height(400);
    		$("#modalInfo").hide();
    		$("#editProfilePhoto").hide();
    		$("#editCoverPhoto").hide();
        });
    	$("body").on('click','.close',function(){
    		$(".modal-dialog").removeAttr('style');
    		$(".modal-body").height(400);
    		$("#modalInfo").hide();
    		$("#editProfilePhoto").hide();
    		$("#editCoverPhoto").hide();
    	});
		
    	$("body").on('click','#closeButton1',function(){
    		$(".modal-dialog1").removeAttr('style');
    		$("#modal-body1").height(400);
    		$("#editProfilePhoto").hide();
    		$("#editCoverPhoto").hide();
        });
    	
    	$("body").on('click','#closeButton2',function(){
    		$(".modal-dialog1").removeAttr('style');
    		$("#modal-body1").height(400);
    		$("#editProfilePhoto").hide();
    		$("#editCoverPhoto").hide();
        });
    	
    	$(".profile-photo").mouseenter(function(){
    	    $(".profile-user-img").css("opacity", "0.9");
    	    $(".profile-user-img").next().css('display','inline');
    	});
    	$(".profile-photo").mouseleave(function(){
    		$(".profile-user-img").next().css('display','none');
    	    $(".profile-user-img").css("opacity", "1");
    	});
    	
    	$(".cover-photo").mouseenter(function(){
    	    $("#profilecontainerimg").css("opacity", "0.5");
    	    $(".editCover").css('display','inline');
    	});
    	$(".cover-photo").mouseleave(function(){
    	    $("#profilecontainerimg").css("opacity", "1");
    	    $(".editCover").css('display','none');
    	});
    });
    

  	
    function uploader(input, options) {
    	var $this = this;
    
    	// Default settings (mostly debug functions)
    	this.settings = {
    		prefix:'empImage',
    		multiple:false,
    		autoUpload:false,
    		url:window.location.href,
    		onprogress:function(ev){ console.log('onprogress'); console.log(ev); },
    		error:function(msg){ console.log('error'); console.log(msg); },
    		success:function(data){ console.log('success'); console.log(data); }
    	};
    	$.extend(this.settings, options);
    
    	this.input = input;
    	this.xhr = new XMLHttpRequest();
    
    	this.send = function(){
    		// Make sure there is at least one file selected
    		if($this.input.files.length < 1) {
    			if($this.settings.error) $this.settings.error('Must select a file to upload');
    			return false;
    		}
    		// Don't allow multiple file uploads if not specified
    		if($this.settings.multi === false && $this.input.files.length > 1) {
    			if($this.settings.error) $this.settings.error('Can only upload one file at a time');
    			return false;
    		}
    		// Must determine whether to send one or all of the selected files
    		if($this.settings.multi) {
    			$this.multiSend($this.input.files);
    		}
    		else {
    			$this.singleSend($this.input.files[0]);
    		}
    	};
    
    	// Prep a single file for upload
    	this.singleSend = function(file){
    		var data = new FormData();
    		data.append(String($this.settings.prefix),file);
    		$this.upload(data);
    	};
    
    	// Prepare all of the input files for upload
    	this.multiSend = function(files){
    		var data = new FormData();
    		for(var i = 0; i < files.length; i++) data.append(String($this.settings.prefix)+String(i), files[i]);
    		$this.upload(data);
    	};
    
    	// The actual upload calls
    	this.upload = function(data){
    		$this.xhr.open('POST',$this.settings.url, true);
    		$this.xhr.send(data);
    	};
    
    	// Modify options after instantiation
    	this.setOpt = function(opt, val){
    		$this.settings[opt] = val;
    		return $this;
    	};
    	this.getOpt = function(opt){
    		return $this.settings[opt];
    	};
    
    	// Set the input element after instantiation
    	this.setInput = function(elem){
    		$this.input = elem;
    		return $this;
    	};
    	this.getInput = function(){
    		return $this.input;
    	};
    
    	// Basic setup for the XHR stuff
    	if(this.settings.progress) this.xhr.upload.addEventListener('progress',this.settings.progress,false);
    	this.xhr.onreadystatechange = function(ev){
    		if($this.xhr.readyState == 4) {
    			console.log('done!');
    			if($this.xhr.status == 200) {
    				if($this.settings.success) $this.settings.success($this.xhr.responseText,ev);
    				$this.input.value = '';
    			}
    			else {
    				if($this.settings.error) $this.settings.error(ev);
    			}
    		}
    	};
    
    	// onChange event for autoUploads
    	if(this.settings.autoUpload) this.input.onchange = this.send;
    }
    
    /* $(function(){
    	var sname = document.getElementById("empId1").value;
    		var $b = $('#upload'),
    			$f = $('#empImage'),
    			$p = $('#progress'),
    			up = new uploader($f.get(0), {
    				url:'MyProfile.action?strImgType=img&empId='+sname,
    				progress:function(ev){ console.log('progress'); $p.html(((ev.loaded/ev.total)*100)+'%'); $p.css('width',$p.html()); },
    				error:function(ev){ console.log('error'); },
    				success:function(data){console.log('success'); $p.html('100%'); $p.css('width',$p.html()); }
    			});
    
    		$b.click(function(){
    			up.send();
    		});
    	}); */
    
    function perkStatus(id,perkSalaryId,type){
		if(type == 1){
			if(id.checked == true){
				document.getElementById("spanPerkSalaryId_"+perkSalaryId).innerHTML = 'Opt In.';
			} else {
				document.getElementById("spanPerkSalaryId_"+perkSalaryId).innerHTML = 'Opt Out.';
			}
		} else {
			if(id.checked == true){
				document.getElementById("spanPerkSalaryId_"+perkSalaryId).innerHTML = 'This is taxable.';
			} else {
				document.getElementById("spanPerkSalaryId_"+perkSalaryId).innerHTML = 'This is non-taxable.';
			}
		}
	}
	
	function savePerkStatus(strEmpId,perkSalaryId){
		if(confirm('Are you sure,you want save this?')){
			var status = document.getElementById("strPerkSalaryId_"+perkSalaryId).checked;
			var action = 'AssignPerkSalary.action?empId='+strEmpId+'&perkSalaryId='+perkSalaryId+'&perkStatus='+status;
			window.location = action;
		}
	}
	
	function reimbursementHeadStatus(id,reimbHeadId,type){
		if(type == 1){
			if(id.checked == true){
				document.getElementById("spanReimCTCHeadId_"+reimbHeadId).innerHTML = 'Opt In.';
			} else {
				document.getElementById("spanReimCTCHeadId_"+reimbHeadId).innerHTML = 'Opt Out.';
			}
		} else {
			if(id.checked == true){
				document.getElementById("spanReimCTCHeadId_"+reimbHeadId).innerHTML = '';
			} else {
				document.getElementById("spanReimCTCHeadId_"+reimbHeadId).innerHTML = '';
			}
		}
	}
	
	function saveReimCTCHeadStatus(strEmpId,strReimCTCHeadId){
		if(confirm('Are you sure,you want save this?')){
			var status = document.getElementById("strReimCTCHeadId_"+strReimCTCHeadId).checked;
			var strFinancialYearStart = document.getElementById("strFinancialYearStart").value;
			var strFinancialYearEnd = document.getElementById("strFinancialYearEnd").value;
			var action = 'AssignReimbursementHead.action?empId='+strEmpId+'&reimHeadId='+strReimCTCHeadId+'&reimHeadStatus='+status+'&strFinancialYearStart='+strFinancialYearStart+'&strFinancialYearEnd='+strFinancialYearEnd;
			window.location = action;
		}
	}
</script>
<%
	UtilityFunctions uF = new UtilityFunctions();
	CommonFunctions CF = new CommonFunctions();
	//EncryptionUtility eU = new EncryptionUtility();
	
	String roundOffCondition = (String)request.getAttribute("roundOffCondition");
    String strUserType = (String) session.getAttribute(IConstants.USERTYPE);
    String strBaseUserType = (String) session.getAttribute(IConstants.BASEUSERTYPE);
    String strUserTypeId = (String) session.getAttribute(IConstants.USERTYPEID);
    String strEmpID = (String) request.getAttribute("EMPID");
    String strProID = (String) request.getParameter("PROFILEID");
    String strSessionEmpID = (String) session.getAttribute("EMPID");
    
    ArrayList alDocuments = (ArrayList) request.getAttribute("alDocuments");
    List<Map<String,String>> empRefList = (List<Map<String,String>>) request.getAttribute("empRefList");
    List<List<String>> alSkills = (List<List<String>>) request.getAttribute("alSkills");
    List<List<String>> alHobbies = (List<List<String>>) request.getAttribute("alHobbies");
    List<List<String>> alLanguages = (List<List<String>>) request.getAttribute("alLanguages");
    List<List<String>> alEducation = (List<List<String>>) request.getAttribute("alEducation");
    Map<String, List<String>> hmEducationDocs = (Map<String, List<String>>) request.getAttribute("hmEducationDocs");
    List<List<String>> alFamilyMembers = (List<List<String>>) request.getAttribute("alFamilyMembers");
    List<List<String>> alPrevEmployment = (List<List<String>>) request.getAttribute("alPrevEmployment");
    Map<String, String> hmEmpPrevEarnDeduct = (Map<String, String>)request.getAttribute("hmEmpPrevEarnDeduct");
    
    List<List<String>> alActivityDetails = (List<List<String>>) request.getAttribute("alActivityDetails");
    
    List<List<String>> alKRADetails = (List<List<String>>) request.getAttribute("alKRADetails");
    List<List<String>> elementouterList = (List<List<String>>) request.getAttribute("elementouterList");
    
    Map<String, List<List<String>>> hmElementAttribute = (Map<String, List<List<String>>>) request.getAttribute("hmElementAttribute");
    Map<String, String> hmScoreAggregateMap = (Map<String, String>) request.getAttribute("hmScoreAggregateMap");
    
    AngularMeter semiWorkedAbsent = (AngularMeter) request.getAttribute("KPI");
    //String semiWorkedAbsent1URL = semiWorkedAbsent.makeSession(request, "chart3");
    
    boolean isFilledStatus = uF.parseToBoolean((String) request.getAttribute("isFilledStatus"));
    //boolean isOfficialFilledStatus = uF.parseToBoolean((String) request.getAttribute("isOfficialFilledStatus"));
    String AGGREGATE_SCORE = (String) request.getAttribute("AGGREGATE_SCORE");
    
    if (strEmpID != null) {
    	strProID = strEmpID;
    } else if (strProID != null) {
    } else if (strSessionEmpID != null) {
    	strProID = strSessionEmpID;
    }
    
    Map<String, String> hmEmpProfile = (Map<String, String>) request.getAttribute("hmEmpProfile");
    if (hmEmpProfile == null) {
    	hmEmpProfile = new HashMap<String, String>();
    }
    
   // System.out.println("MP.jsp/427--hmEmpProfile="+hmEmpProfile);
    //	List<List<String>> leaveTypeListWithBalance = (List<List<String>>) request.getAttribute("leaveTypeListWithBalance");
    //Map<String, String> hmLeaveTypeName = (Map<String, String>) request.getAttribute("hmLeaveTypeName");
    //Map<String, String> hmEmpLeaveBalance = (Map<String, String>) request.getAttribute("hmEmpLeaveBalance");
    
    
    //	String strImage = hmEmpProfile.get("IMAGE");
    
    String strTitle = "";
    if (strUserType != null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) && !strUserType.equalsIgnoreCase(IConstants.ARTICLE)
    		&& !strUserType.equalsIgnoreCase(IConstants.CONSULTANT)) {
    	strTitle = hmEmpProfile.get("NAME") + "'s Profile";
    } else {
    	strTitle = "My Profile";
    
    }
    
    String[] arrEnabledModules = (String[])request.getAttribute("arrEnabledModules");
    String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
    String isDeviceIntegration = (String) request.getAttribute("IS_DEVICE_INTEGRATION");
    String fromPage = (String) request.getAttribute("fromPage");
    String strCurr = (String) request.getAttribute("strCurr");
    //String BASEUSERTYPEID = (String) session.getAttribute(IConstants.BASEUSERTYPEID);
    String USERTYPEID = (String) session.getAttribute(IConstants.USERTYPEID);
    String strSessionEmpId = (String) session.getAttribute(IConstants.EMPID);
    
    Map<String, String> hmFeatureStatus = (Map<String, String>) request.getAttribute("hmFeatureStatus");
	Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>)request.getAttribute("hmFeatureUserTypeId");
	
    %>
<script>
    function getKRA(id) {
    	var dialogEdit = '.modal-body';
    	 $(dialogEdit).empty();
    	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	 $("#modalInfo").show();
    	 $(".modal-title").html('KRA\'s of <%=uF.showData(hmEmpProfile.get("NAME"), "-")%> (<%=uF.showData(hmEmpProfile.get("DESIGNATION_NAME"), "-").replace("'", "\\'")%>)');
    	 $.ajax({
    			url : "AddKRA.action?empId="+id,
    			cache : false,
    			success : function(data) {
    				$(dialogEdit).html(data);
    			}
    		});
    }
    
    function sendMail(emp_id) {
    	var dialogEdit = '.modal-body';
    	 $(dialogEdit).empty();
    	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	 $("#modalInfo").show();
    	 $(".modal-title").html('Send Document');
    	 $.ajax({
    			url : "SendMail.action?emp_id="+emp_id,
    			cache : false,
    			success : function(data) {
    				$(dialogEdit).html(data);
    			}
    		});
    }
    	
    function salaryPreview(id) {
    var dialogEdit = '.modal-body';
     $(dialogEdit).empty();
     $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
     $("#modalInfo").show();
     $(".modal-title").html('Salary Preview');
     $.ajax({
    		url : "SalaryPreview.action?emp_id="+id,
    		cache : false,
    		success : function(data) {
    			$(dialogEdit).html(data);
    		}
    	});
    }
    
    
    function salaryHistory(id) {
    var dialogEdit = '.modal-body';
     $(dialogEdit).empty();
     $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
     $("#modalInfo").show();
     $(".modal-title").html('Salary History');
     $.ajax({
    		url : "SalaryHistory.action?emp_id="+id,
    		cache : false,
    		success : function(data) {
    			$(dialogEdit).html(data);
    		}
    	});
    }

      
    function showProfileEditPhoto(){
        $("#editProfilePhoto").show();
    }
    
    function showCoverEditPhoto(){
        $("#editCoverPhoto").show();
    }
    
    function closeForm() {
    	window.location = "People.action?callFrom=ADDEMP";
    }
    
</script>
<%--  <jsp:include page="../common/SubHeader.jsp">
    <jsp:param value="<%=strTitle %>" name="title"/>
    </jsp:include> --%>
<section class="content"> <input type="hidden"
	name="strFinancialYearStart" id="strFinancialYearStart"
	value="<%=(String)request.getAttribute("strFinancialYearStart") %>" />
<input type="hidden" name="strFinancialYearEnd" id="strFinancialYearEnd"
	value="<%=(String)request.getAttribute("strFinancialYearEnd") %>" />
<div class="row">
	<div class="col-md-3">
		<!-- Profile Image -->
		<div class="box box-primary">
			<div class="box-body box-profile">
				<div class="box box-widget widget-user">
					<div class="widget-user-header cover-photo" style="padding: 0px;">
				<!-- ====start parvez on 27-10-2022===== -->		
						<%-- <img
							style="height: 100%; width: 100%; position: absolute; border-radius: 0%;"
							id="profilecontainerimg" class="lazy"
							src="images1/user-background-photo.jpg"
							data-original='<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE_COVER+"/"+hmEmpProfile.get("EMP_ID")+"/"+hmEmpProfile.get("COVER_IMAGE")%>'> --%>
						<%if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_USER_BACKGROUND_PHOTO)) && hmFeatureUserTypeId.get(IConstants.F_USER_BACKGROUND_PHOTO)!=null && hmFeatureUserTypeId.get(IConstants.F_USER_BACKGROUND_PHOTO).size()>0){ 
							List<String> alPhotoInner = hmFeatureUserTypeId.get(IConstants.F_USER_BACKGROUND_PHOTO);
						%>
						<!-- height: 100%; -->
							<img style="width: 100%; position: absolute; border-radius: 0%;"
								id="profilecontainerimg" class="lazy" src="images1/exusia_banner_<%=alPhotoInner.get(1) %>.png"
								data-original='<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE_COVER+"/"+hmEmpProfile.get("EMP_ID")+"/"+hmEmpProfile.get("COVER_IMAGE")%>'>
						<% } else{ %>
							<img style="height: 100%; width: 100%; position: absolute; border-radius: 0%;"
									id="profilecontainerimg" class="lazy" src="images1/user-background-photo.jpg"
									data-original='<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE_COVER+"/"+hmEmpProfile.get("EMP_ID")+"/"+hmEmpProfile.get("COVER_IMAGE")%>'>
						<% } %>
				<!-- ====end parvez on 27-10-2022===== -->		
						<span style="float: right; position: relative; display: none;"
							class="editCover"><a href="javascript:void(0);"
							onclick="showCoverEditPhoto();" title="Edit Cover Photo"><i
								class="fa fa-pencil" aria-hidden="true"
								style="color: rgb(46, 45, 45);"></i>
						</a>
						</span>
					</div>
					<%if(docRetriveLocation==null) { %>
					<div class="widget-user-image profile-photo">
						<img class="profile-user-img img-responsive img-circle lazy"
							id="profilecontainerimg" src="userImages/avatar_photo.png"
							data-original="<%=IConstants.DOCUMENT_LOCATION + hmEmpProfile.get("IMAGE")%>">
						<a href="javascript:void(0)" style="display: none;"
							onclick="showProfileEditPhoto();"><i class="fa fa-pencil"
							aria-hidden="true"
							style="color: rgb(46, 45, 45); position: absolute; top: 40%; left: 40%;"></i>
						</a>
					</div>
					<% }else{ %>
					<div class="widget-user-image profile-photo">
						<img class="profile-user-img img-responsive img-circle lazy"
							id="profilecontainerimg" src="userImages/avatar_photo.png"
							data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+strProID+"/"+IConstants.I_100x100+"/"+hmEmpProfile.get("IMAGE")%>">
						<a href="javascript:void(0)" style="display: none;"
							onclick="showProfileEditPhoto();"><i class="fa fa-pencil"
							aria-hidden="true"
							style="color: rgb(46, 45, 45); position: absolute; top: 40%; left: 40%;"></i>
						</a>
					</div>
					<% } %>
				</div>
				<!-- <a href="javascript:void" onclick="showProfileEditPhoto();"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a> -->
				<h4 class="profile-username text-center" style="margin-top: 40px;"><%=uF.showData((String) hmEmpProfile.get("NAME"), "-")%>
					[<%=uF.showData((String) hmEmpProfile.get("EMPCODE"), "-")%>]
				</h4>
				<p class="text-muted text-center text-no-margin"><%=uF.showData((String) hmEmpProfile.get("DESIGNATION_NAME"), "-")%></p>
				<p class="text-muted text-center text-no-margin"><%=uF.showData((String) hmEmpProfile.get("DEPARTMENT_NAME"), "-")%>
					<%-- [<%=uF.showData((String) hmEmpProfile.get("WLOCATION_NAME"), "-")%>] --%>
				</p>
				<p class="text-muted text-center text-no-margin"><%=uF.showData((String) hmEmpProfile.get("WLOCATION_NAME"), "-")%></p>
				<p class="text-muted text-center text-no-margin"><%=uF.showData((String) hmEmpProfile.get("ORG_NAME"), "-")%></p>

				<p class="text-muted text-center text-no-margin">
					<%if(((String) hmEmpProfile.get("SUPERVISOR_NAME"))!=null) { %>
					You report to <strong><%=uF.showData((String) hmEmpProfile.get("SUPERVISOR_NAME"), "-")%></strong>
					<% } else { %>
					You don't have a reporting manager.
					<% } %>
				</p>
				<%if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT + "") >= 0) { %>
				<div id="skillPrimaryOverall" align="center"
					style="width: auto !important; margin-left: 85px;"></div>
				<% } %>
				<div align="center" style="margin-top: 10px;">

					<% if (strUserType != null && strUserType.equalsIgnoreCase(IConstants.EMPLOYEE)) { %>
						<% if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_SCHEDUING+"")>=0){ %>
							<a href="javascript:void(0);" onclick="window.location='MyTime.action?callFrom=FactQuickLink'" class="btn btn-default" title="Roster details"><i class="fa fa-calendar-o" aria-hidden="true"></i></a>
						<% } %>
						<% if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_LEAVE_MANAGEMENT+"")>=0){ %>
							<a href="javascript:void(0);" onclick="window.location='Login.action?role=3&userscreen=FactQuickLinkLeaveSummary'" class="btn btn-default" title="Leave Card"><i class="fa fa-sign-out" aria-hidden="true"></i></a>
						<% } %>
						<% if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_TIME_AND_ATTENDANCE+"")>=0){ %> 
							<a href="javascript:void(0);" onclick="window.location='Login.action?role=3&userscreen=FactQuickLinkMyClockEntry'" class="btn btn-default" title="Clock Entry"><i class="fa fa-clock-o" aria-hidden="true"></i></a>
						<% } %>
						<% if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_COMPENSATION_MANAGEMENT+"")>=0){ %> 
							<a href="javascript:void(0);" onclick="window.location='Login.action?role=3&userscreen=FactQuickLinkMySalarySlip'" class="btn btn-default" title="Compensation details"><i class="fa fa-clipboard" aria-hidden="true"></i></a>
						<% } %>
					<% } else { %>
						<% if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_SCHEDUING+"")>=0){ %>
							<a href="javascript:void(0);" onclick="window.location='Roster.action?callFrom=FactQuickLink&profileEmpId=<%=strProID %>'" class="btn btn-default" title="Roster details"><i class="fa fa-calendar-o" aria-hidden="true"></i></a>
						<% } %>
						<% if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_LEAVE_MANAGEMENT+"")>=0){ %> 
							<a href="javascript:void(0);" onclick="window.location='Login.action?role=<%=strUserTypeId %>&userscreen=FactQuickLinkLeaveCard&profileEmpId=<%=strProID %>'" class="btn btn-default" title="Leave Card"><i class="fa fa-sign-out" aria-hidden="true"></i></a>
						<% } %>
						<% if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_TIME_AND_ATTENDANCE+"")>=0){ %>
							<a href="javascript:void(0);" onclick="window.location='Login.action?role=<%=strUserTypeId %>&userscreen=FactQuickLinkClockEntry&profileEmpId=<%=strProID %>'" class="btn btn-default" title="Clock Entry"><i class="fa fa-clock-o" aria-hidden="true"></i></a>
						<% } %>
						<% if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_COMPENSATION_MANAGEMENT+"")>=0){ %> 
							<a href="javascript:void(0);" onclick="window.location='Login.action?role=<%=strUserTypeId %>&userscreen=FactQuickLinkViewSalarySlip&profileEmpId=<%=strProID %>'" class="btn btn-default" title="Compensation details"><i class="fa fa-clipboard" aria-hidden="true"></i></a>
						<% } %>
					<% } %>
				</div>
			</div>
			<!-- /.box-body -->
		</div>
		<!-- /.box -->
		<!-- About Me Box -->
		<div class="box box-primary">
			<div class="box-header with-border">
				<h3 class="box-title">About Me</h3>
			</div>
			<!-- /.box-header -->
			<div class="box-body">
			<% if(!uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_DISABLE_WORK_TIME_IN_ABOUT_ME))){ %>
				<div id="guageKpi" class="gauge"></div>
				<script>
				    document.addEventListener("DOMContentLoaded", function(event) {
				        var g1 = new JustGage({
				            id: "guageKpi",
				            title: "",
				            label: "KPI",
				            value: <%=uF.parseToDouble((String)request.getAttribute("ACTUAL_TIME_KPI"))%>,
				            min: 0,
				            max: <%=uF.parseToDouble((String)request.getAttribute("BUDGET_TIME_KPI"))%>,
				            decimals: 0,
				            gaugeWidthScale: 0.6,
				            levelColors: [
	                          "#dd4b39",
	                          "#FFE000",
	                          "#00a65a"
	                        ]
				        });
				    });
					    
                    </script>
                    
				<p
					style="font-size: 18px; margin-top: 20px; color: rgb(255, 122, 0);">
					<%if (request.getAttribute("TIME_DURATION") != null) {%>
					Since <strong style="font-family: Digital; font-size: 24px;"><%=uF.showData(hmEmpProfile.get("JOINING_DATE"), "NA")%>,
					</strong> you have worked <strong
						style="font-family: Digital; font-size: 24px;"><%=request.getAttribute("TIME_DURATION")%></strong>
					for <strong style="font-family: Digital; font-size: 24px;"><%=request.getAttribute("HRS_WORKED")%></strong>
					hrs
					<%} else { %>
					Your working hours have not been calcualated, yet.
					<% } %>
				</p>
			<% } %>	
				<hr>
				<strong><i class="fa fa-calendar-o margin-r-5"></i> Date of
					Joining</strong>
				<p><%=uF.showData((String) hmEmpProfile.get("JOINING_DATE"), "-")%></p>
				<hr>
				<% if(alSkills!=null && alSkills.size()!=0) { %>
				<strong><i class="fa fa-pencil margin-r-5"></i> Skills</strong>
				<p>
					<% for(int i=0; i<alSkills.size(); i++) { 
                            if(i%5 == 0){%>
					<span class="label label-danger"> <% }else if(i%5 == 1){ %> <span
						class="label label-success"> <% }else if(i%5 == 2){ %> <span
							class="label label-info"> <% }else if(i%5 == 3){ %> <span
								class="label label-warning"> <% }else{ %> <span
									class="label label-primary"> <% } %> <strong><%=((List)alSkills.get(i)).get(1)%></strong>
								</span>&nbsp <% } %>
							
				</p>
				<hr>
				<% } %>
				<%
                        if (strUserType != null && (strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) || !strUserType.equalsIgnoreCase(IConstants.ARTICLE) 
                        		|| !strUserType.equalsIgnoreCase(IConstants.CONSULTANT))) {%>
				<strong> <%String empStatus = (String)request.getAttribute("RESIGNATION_STATUS");
                     if(empStatus!=null && empStatus.equalsIgnoreCase("TERMINATED") ){
                     %>
					<p>
						<a href="ResignationEntry1.action?emp_id=<%=strProID%>"><%=empStatus%></a>
					</p> <%}else{ %>
					<p>
						<a href="ResignationEntry1.action?emp_id=<%=strProID%>"><%=((request.getAttribute("RESIGNATION_STATUS") != null) ? request.getAttribute("RESIGNATION_STATUS") : "Leave Organisation")%></a>
					</p> <%
                        }
                        } %> <% if (uF.parseToInt((String) request.getAttribute("RESIG_STATUS")) == 1) { %>
					<p><%=uF.showData((String) request.getAttribute("RESIGNATION_REMAINING"), "0")%></p>
					<% } %> <% if (uF.parseToInt((String) request.getAttribute("PROBATION_REMAINING")) > 0 && uF.parseToInt((String) request.getAttribute("RESIG_STATUS")) == 0) { %>
					<p>
						Your probation period will end in
						<%=uF.showData((String) request.getAttribute("PROBATION_REMAINING"), "0")%>
						days
					</p> <% } %> </strong>
				<hr>
			</div>
			<!-- /.box-body -->
		</div>
		<!-- /.box -->
	</div>
	<!-- /.col -->
	<div class="col-md-9" style="padding-left: 0px;">
		<div class="nav-tabs-custom">
			<ul class="nav nav-tabs">
				<li <%if(fromPage == null || !fromPage.equals("MyDashboard")) { %>
					class="active" <% } %>><a href="#about" data-toggle="tab">About</a>
				</li>
				<li <%if(fromPage != null && fromPage.equals("MyDashboard")) { %>
					class="active" <% } %>><a href="#position" data-toggle="tab">Position</a>
				</li>
				<li><a href="#timeline" data-toggle="tab">Timeline</a>
				</li>
				<%if(fromPage != null && fromPage.equals("P")) { %>
				<div class="box-tools pull-right">
					<a href="javascript:void(0);" onclick="closeForm()"
						class="close-font" style="margin-right: 20px;"> </a>
				</div>
				<%} %>
			</ul>
			<div class="tab-content">
				<div <%if(fromPage == null || !fromPage.equals("MyDashboard")) { %>
					class="tab-pane active" <% } else { %> class="tab-pane" <% } %>
					id="about">
					<% if (strUserType != null && (strBaseUserType.equalsIgnoreCase(IConstants.ADMIN) || strBaseUserType.equalsIgnoreCase(IConstants.HRMANAGER))) { %>
					<div style="float: right">
						<a href="javascript:void(0);" onclick="sendMail(<%=strProID%>);"
							title="Send Mail"> <i
							style="font-size: 16px; color: #06b400;" class="fa fa-envelope-o"></i>
						<!-- <img src="images1/mail_enbl.png"> --> </a>
					</div>
					<div style="clear: both; margin-bottom: 10px;"></div>
					<% } %>
					<%
                            if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT + "") >= 0) {
                            %>
					<div class="about-item">
						<h3 class="about-header">Attributes</h3>
						<div class="about-body">
							<table class="table table_no_border autoWidth"
								style="width: 100% !important;">
								<%
                                
                                double dblScorePrimary = 0, aggregeteRating = 0, totAllAttribRating = 0;
        		 				int count = 0;
        		 				for (int i = 0; elementouterList != null && i < elementouterList.size(); i++) {
        		 					List<String> innerList = elementouterList.get(i);
        		 					List<List<String>> attributeouterList1 = hmElementAttribute.get(innerList.get(0).trim());
        		 					
        		 					if(attributeouterList1 != null && !attributeouterList1.isEmpty()) {
        			 					for (int j = 0; j < attributeouterList1.size(); j++) {
        			 						List<String> attributeList1 = attributeouterList1.get(j);
        			 						double dblAttribRating = uF.parseToDouble(hmScoreAggregateMap.get(attributeList1.get(0).trim()));
        			 						if(dblAttribRating>0) {
        				 						totAllAttribRating += uF.parseToDouble(hmScoreAggregateMap.get(attributeList1.get(0).trim()));
        				 						count++;
        			 						}
        			 					}
        		 					}
        		 				}
        		 				aggregeteRating = totAllAttribRating / count;
        		 				dblScorePrimary = aggregeteRating; 
                                %>
								<tr>
									<th class="alignRight" style="width: 32% !important;">Overall:</th>
									<td>
										<div id="starAllPrimary" style="float: left; width: 100px;"></div>
										<input type="hidden" id="gradeAllwithrating" value="0" /></td>
									<script type="text/javascript">
							        	$('#starAllPrimary').raty({
							        		readOnly: true,
							        		start: <%=dblScorePrimary %>,
							        		half: true,
							        		targetType: 'number',
							        		click: function(score, evt) {
							        			$('#gradeAllwithrating').val(score);
							        		}
							        	});
						        	</script>
								</tr>

								<%
                                        for (int i = 0; elementouterList != null && i < elementouterList.size(); i++) {
                                        	List<String> innerList = elementouterList.get(i);
                                        %>
								<%-- <div style="width:100%; clear: both;"><strong><%=innerList.get(1)%></strong></div> --%>
								<%
                                        List<List<String>> attributeouterList1 = hmElementAttribute.get(innerList.get(0).trim());
                                        		for (int j = 0; attributeouterList1 != null && j < attributeouterList1.size(); j++) {
                                        			List<String> attributeList1 = attributeouterList1.get(j);
                                        %>
								<tr>
									<td class="alignRight"><%=attributeList1.get(1)%> (<%=innerList.get(1).substring(0, 2) %>):</td>
									<td>
										<div id="starPrimary<%=i%><%=j%>" style="float: left;"></div>
										<input type="hidden" id="gradewithrating<%=i%><%=j%>"
										value="<%=hmScoreAggregateMap.get(attributeList1.get(0).trim()) != null ? uF.parseToDouble(hmScoreAggregateMap.get(attributeList1.get(0).trim())) + "" : "0"%>"
										; name="gradewithrating<%=i%><%=j%>" /></td>
								</tr>
								<script type="text/javascript">
                                            $('#starPrimary<%=i%><%=j%>').raty({
                                            	readOnly: true,
                                            	start: <%=hmScoreAggregateMap.get(attributeList1.get(0).trim()) != null ? uF.parseToDouble(hmScoreAggregateMap.get(attributeList1.get(0).trim())) + "" : "0"%>,
                                            	half: true,
                                            	targetType: 'number',
                                            	click: function(score, evt) {
                                            		$('#gradewithrating<%=i%><%=j%>').val(score);
                                            	}
                                            });
                                        </script>
								<% }
                                        }
									%>
							</table>

							<div class="clr"></div>
							<% if (hmElementAttribute == null || hmElementAttribute.isEmpty()) { %>
							<div class="nodata msg">
								<span>No attribute aligned with this level</span>
							</div>
							<% } %>
						</div>
					</div>
					<% } %>
					<br />
					<div class="about-item">
						<%
                             List alKRA = (List) request.getAttribute("alKRA");
                             Map hmKRA = (Map) request.getAttribute("hmKRA");
                             %>
						<h3 class="about-header">Key Responsibility Areas
							<% if (strUserType != null && (strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.RECRUITER)) 
                                  && strBaseUserType != null && (strBaseUserType.equalsIgnoreCase(IConstants.ADMIN) || strBaseUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strBaseUserType.equalsIgnoreCase(IConstants.RECRUITER))) {
							//===start parvez date: 18-10-2021===
								if(uF.parseToBoolean(hmEmpProfile.get("IS_ALIVE")) && hmEmpProfile.get("EMPLOYMENT_END_DATE") == null){
							//===end parvez date: 18-10-2021===
							%>
									&nbsp;&nbsp;<a href="AddEmployee.action?operation=U&mode=profile&step=8&empId=<%=strProID%>"><span class="label label-danger" style="font-size: 9px;"> <strong>Edit</strong></span></a>
							<!-- ===start parvez date: 18-10-2021=== -->
								<% } %>
							<!-- ===end parvez date: 18-10-2021=== -->
							<% } %>
						</h3>
						<div class="about-body">
							<%
                                                    String effectiveDate = "";
                                                    if(alKRADetails != null && !alKRADetails.isEmpty()) {
                                                     effectiveDate = alKRADetails.get(alKRADetails.size()-1).get(0);
                                                    }
                                                    %>
							<div>
								<%=((effectiveDate != null && !effectiveDate.equals("")) ? "Since: " + effectiveDate : "<div class=\"nodata msg\" style=\"width:95%\">No KRAs defined yet.</span></div>")%>
								<table class="table table_no_border autoWidth"
									style="width: 100% !important;">
									<%
                                                            for(int i=0; alKRADetails != null && !alKRADetails.isEmpty() && i<alKRADetails.size(); i++) {
                                                            List<String> innerList = alKRADetails.get(i);                		  
                                                              %>
									<tr>
										<td class="kra"><%=innerList.get(1)%></td>
									</tr>
									<% } %>
								</table>
							</div>
						</div>
					</div>
					<br />
					<div class="about-item">
						<h3 class="about-header">
							Current Job
							<% if (strUserType != null && (strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.RECRUITER)) 
                                  && strBaseUserType != null && (strBaseUserType.equalsIgnoreCase(IConstants.ADMIN) || strBaseUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strBaseUserType.equalsIgnoreCase(IConstants.RECRUITER))) {
							//===start parvez date: 18-10-2021===
								if(uF.parseToBoolean(hmEmpProfile.get("IS_ALIVE")) && hmEmpProfile.get("EMPLOYMENT_END_DATE") == null){
							//===end parvez date: 18-10-2021===
							%>
									&nbsp;&nbsp;<a href="AddEmployee.action?operation=U&mode=profile&step=8&empId=<%=strProID%>"><span class="label label-danger" style="font-size: 9px;"> <strong>Edit</strong></span></a>
							<!-- ===start parvez date: 18-10-2021=== -->
								<% } %>
							<!-- ===end parvez date: 18-10-2021=== -->
							<% } %>
						</h3>
						<div class="about-body">
							<table class="table table_no_border autoWidth"
								style="width: 100% !important;">
								<tr>
									<td class="alignRight" style="width: 32% !important;">Employee
										Type:</td>
									<td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("EMP_TYPE"), "-")%></td>
								</tr>
								<tr>
									<td class="alignRight">Level:</td>
									<td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("LEVEL_NAME"), "-")%></td>
								</tr>
								<tr>
									<td class="alignRight">Designation:</td>
									<td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("DESIGNATION_NAME"), "-")%></td>
								</tr>
								<tr>
									<td class="alignRight">Grade:</td>
									<td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("GRADE_NAME"), "-")%></td>
								</tr>
								<tr>
									<td class="alignRight">SBU:</td>
									<td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("SBU_NAME"), "-")%></td>
								</tr>
								<tr>
									<td class="alignRight">Department:</td>
									<td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("DEPARTMENT_NAME"), "-")%></td>
								</tr>
								<tr>
									<td class="alignRight">Location:</td>
									<td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("WLOCATION_NAME"), "-")%></td>
								</tr>
								<tr>
									<td class="alignRight">Organization:</td>
									<td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("ORG_NAME"), "-")%></td>
								</tr>
							</table>
						</div>
						<%if(!uF.parseToBoolean(hmEmpProfile.get("OFFICIAL_FILLED_STATUS"))) {%>
						<div class="about-footer">
							<img src="images1/warning.png" />
						</div>
						<% } %>
					</div>
					<br />
					<div class="about-item">
						<h3 class="about-header">
							Reporting Structure
							<% if (strUserType != null && (strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.RECRUITER)) 
                                  && strBaseUserType != null && (strBaseUserType.equalsIgnoreCase(IConstants.ADMIN) || strBaseUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strBaseUserType.equalsIgnoreCase(IConstants.RECRUITER))) {
							//===start parvez date: 18-10-2021===
								if(uF.parseToBoolean(hmEmpProfile.get("IS_ALIVE")) && hmEmpProfile.get("EMPLOYMENT_END_DATE") == null){	
							//===end parvez date: 18-10-2021===
							%>
									&nbsp;&nbsp;<a href="AddEmployee.action?operation=U&mode=profile&step=8&empId=<%=strProID%>"><span class="label label-danger" style="font-size: 9px;"> <strong>Edit</strong></span></a>
							<!-- ===start parvez date: 18-10-2021=== -->
								<% } %>
							<!-- ===end parvez date: 18-10-2021=== -->
							<% } %>
						</h3>
						<div class="about-body">
							<table class="table table_no_border autoWidth"
								style="width: 100% !important;">
								<%-- <tr>
									<td class="alignRight" style="width: 32% !important;">Manager:
									</td>
									<td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("SUPERVISOR_NAME"), "-")%>
									</td>
								</tr> --%>
								<tr>
									<td class="alignRight">H.O.D.:</td>
									<td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("HOD_NAME"), "-")%>
									</td>
								</tr>
								<tr>
									<td class="alignRight">HR:</td>
									<td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("HR_NAME"), "-")%>
									</td>
								</tr>
						<!-- ===start parvez date: 30-07-2022 Note: sequence change=== -->		
								<tr>
									<td class="alignRight" style="width: 32% !important;">Manager:
									</td>
									<td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("SUPERVISOR_NAME"), "-")%>
									</td>
								</tr>
						<!-- ===end parvez date: 30-07-2022=== -->		
							</table>
						</div>
					</div>
					<br />
					<div class="about-item">
						<h3 class="about-header">
							Employee History
							<% if (strUserType != null && (strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.RECRUITER)) 
                                  && strBaseUserType != null && (strBaseUserType.equalsIgnoreCase(IConstants.ADMIN) || strBaseUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strBaseUserType.equalsIgnoreCase(IConstants.RECRUITER))) {
								//===start parvez date: 18-10-2021===
									if(uF.parseToBoolean(hmEmpProfile.get("IS_ALIVE")) && hmEmpProfile.get("EMPLOYMENT_END_DATE") == null){
								//===end parvez date: 18-10-2021===
							%>
									&nbsp;&nbsp;<a href="AddEmployee.action?operation=U&mode=profile&step=8&empId=<%=strProID%>"><span class="label label-danger" style="font-size: 9px;"> <strong>Edit</strong></span></a>
								<!-- ===start parvez date: 18-10-2021=== -->
								<% } %>
								<!-- ===end parvez date: 18-10-2021=== -->
							<% } %>
						</h3>
						<div class="about-body">
							<table class="table table_no_border autoWidth"
								style="width: 100% !important;">
								<tr>
									<td class="alignRight" style="width: 32% !important;">Joining
										Date:</td>
									<td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("JOINING_DATE"), "-")%></td>
								</tr>
								<tr>
									<td class="alignRight">Last Promotion:</td>
									<td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("PREV_PROMOTION"), "-")%></td>
								</tr>
								<tr>
									<td class="alignRight">Previous position:</td>
									<td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("PREV_DESIGNATION"), "-")%></td>
								</tr>
							</table>
						</div>
					</div>
					<br />
					<div class="about-item">
						<h3 class="about-header">
							Other Official Information
							<% if (strUserType != null && (strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.RECRUITER)) 
                                  && strBaseUserType != null && (strBaseUserType.equalsIgnoreCase(IConstants.ADMIN) || strBaseUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strBaseUserType.equalsIgnoreCase(IConstants.RECRUITER))) {
							//===start parvez date: 18-10-2021===
								if(uF.parseToBoolean(hmEmpProfile.get("IS_ALIVE")) && hmEmpProfile.get("EMPLOYMENT_END_DATE") == null){
							//===end parvez date: 18-10-2021===
							%>
									&nbsp;&nbsp;<a href="AddEmployee.action?operation=U&mode=profile&step=8&empId=<%=strProID%>"><span class="label label-danger" style="font-size: 9px;"> <strong>Edit</strong></span></a>
							<!-- ===start parvez date: 18-10-2021=== -->
								<%} %>
							<!-- ===end parvez date: 18-10-2021=== -->
							<%} %>
						</h3>
						<div class="about-body">
							<table class="table table_no_border autoWidth"
								style="width: 100% !important;">
								<tr>
									<td class="alignRight" style="width: 32% !important;">Employee Status:</td>
									<td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("EMPLOYMENT_TYPE"), "-")%></td>
								</tr>
								<%-- <tr>
									<td class="alignRight">Roster Policy:</td>
									<td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("LEVEL_NAME"), "-")%></td>
								</tr> --%>
								<% if(hmEmpProfile.get("EMPLOYMENT_TYPE")!=null && hmEmpProfile.get("EMPLOYMENT_TYPE").trim().equals(IConstants.PROBATION)){ %>
								<tr>
									<td class="alignRight">Probation Period:</td>
									<td class="textblue" valign="bottom"><%=uF.showData((String)request.getAttribute("PROBATION_PERIOD"), "-")%></td>
								</tr>
								<% } %>
								<tr>
									<td class="alignRight">Notice Period:</td>
									<td class="textblue" valign="bottom"><%=uF.showData((String)request.getAttribute("NOTICE_PERIOD"), "-")%>
										days</td>
								</tr>
								<tr>
									<td class="alignRight">Paycycle Duration:</td>
									<td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("PAYCYCLE_DURATION"), "-")%>
									</td>
								</tr>
								<tr>
									<td class="alignRight">Roster dependency?:</td>
									<td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("ROSTER_DEPENDENCY"), "-")%>
									</td>
								</tr>
								<tr>
									<td class="alignRight">Attendance dependency?:</td>
									<td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("ATTENDANCE_DEPENDENCY"), "-")%>
									</td>
								</tr>
								<tr>
									<td class="alignRight">Eligible for allowance:</td>
									<td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("ALLOWANCE"), "-")%></td>
								</tr>
								<tr>
									<td class="alignRight">Biometric Machine Id (if
										integrated):</td>
									<td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("BIOMATRIC_MACHINE_ID"), "-")%></td>
								</tr>
							</table>
						</div>
					</div>

					<br />
					<div class="about-item">
						<h3 class="about-header">
							Leave Snapshot
							<% if (strUserType != null && (strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.RECRUITER)) 
                                  && strBaseUserType != null && (strBaseUserType.equalsIgnoreCase(IConstants.ADMIN) || strBaseUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strBaseUserType.equalsIgnoreCase(IConstants.RECRUITER))) {
							
							//===start parvez date: 18-10-2021===
								if(uF.parseToBoolean(hmEmpProfile.get("IS_ALIVE")) && hmEmpProfile.get("EMPLOYMENT_END_DATE") == null){
							//===end parvez date: 18-10-2021===
							%>
									&nbsp;&nbsp;<a href="AddEmployee.action?operation=U&mode=profile&step=8&empId=<%=strProID%>"><span class="label label-danger" style="font-size: 9px;"> <strong>Edit</strong></span></a>
							<!-- ===start parvez date: 18-10-2021=== -->
								<%} %>
							<!-- ===end parvez date: 18-10-2021=== -->
							<% } %>
						</h3>
						<div class="about-body">
							<table class="table table_no_border autoWidth"
								style="width: 100% !important;">
								<tr>
									<td class="alignRight" style="width: 32% !important;"><b>Leave
											Balance </b>
									</td>
									<td class="textblue" valign="bottom"></td>
								</tr>
								<%
                                                int cnt = 0;
                                                java.util.List leaveList = (java.util.List)request.getAttribute("leaveList");
                                                for (int i=0; leaveList!=null && i<leaveList.size(); i++) {
                                                	java.util.List cinnerlist = (java.util.List)leaveList.get(i);
                                                	cnt++;
                                                %>
								<tr>
									<td class="alignRight"><%=cinnerlist.get(0) %>:</td>
									<td class="textblue" valign="bottom"><%=cinnerlist.get(5) %></td>
								</tr>
								<%} %>
								<% if(cnt == 0) { %>
								<tr>
									<td colspan="2" nowrap="nowrap">
										<div class="nodata msg">
											<span>No Leave Data Available.</span>
										</div></td>
								</tr>
								<% } %>
							</table>
						</div>
					</div>
					<br />

					<% if((uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_SHOW_SALARY_IN_PROFILE_USERWISE)) && hmFeatureUserTypeId.get(IConstants.F_SHOW_SALARY_IN_PROFILE_USERWISE).contains(USERTYPEID)) || (!uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_SHOW_SALARY_IN_PROFILE_USERWISE)) && hmFeatureUserTypeId != null && hmFeatureUserTypeId.get(IConstants.F_SHOW_SALARY_IN_PROFILE_USERWISE+"_USER_IDS") != null && hmFeatureUserTypeId.get(IConstants.F_SHOW_SALARY_IN_PROFILE_USERWISE+"_USER_IDS").contains(strSessionEmpId))) { %>
					<div class="about-item">
						<%
	                          List<List<String>> salaryHeadDetailsList = (List<List<String>>) request.getAttribute("salaryHeadDetailsList");
	                          Map<String, Double> hmSalaryTotal = (Map<String, Double>) request.getAttribute("hmSalaryTotal");
	                          Map<String, String> hmGratuityPolicy = (Map<String, String>) request.getAttribute("hmGratuityPolicy");
	                          double gratuitySalHeadAmt=0.0d;
	                          List<String> alGratuitySlaHeadId = new ArrayList<String>();
	                          if(hmGratuityPolicy !=null && hmGratuityPolicy.get("SALARY_HEAD")!=null) {
	                        	  alGratuitySlaHeadId = Arrays.asList(hmGratuityPolicy.get("SALARY_HEAD").split(","));
	                          }
                          %>
						<h3 class="about-header">
							Compensation Structure

							<% if (strUserType != null && (strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.HRMANAGER)) 
                                  && strBaseUserType != null && (strBaseUserType.equalsIgnoreCase(IConstants.ADMIN) || strBaseUserType.equalsIgnoreCase(IConstants.HRMANAGER))) {
								
							//===start parvez date: 18-10-2021===
								if(uF.parseToBoolean(hmEmpProfile.get("IS_ALIVE")) && hmEmpProfile.get("EMPLOYMENT_END_DATE") == null){
							//===end parvez date: 18-10-2021===
							%>
							<%if(uF.parseToBoolean(hmEmpProfile.get("OFFICIAL_FILLED_STATUS"))) {%>
							&nbsp;&nbsp;<a href="AddEmployee.action?operation=U&mode=profile&serviceId=0&step=9&empId=<%=strProID%>"><span class="label label-danger" style="font-size: 9px;"> <strong>Edit</strong></span></a>
							<% } else if (!uF.parseToBoolean(hmEmpProfile.get("OFFICIAL_FILLED_STATUS")) && !isFilledStatus) { %>
							&nbsp;&nbsp;<a href="javascript:void(0)" onclick="return hs.htmlExpand(this);" class="edit poplight"><span class="label label-danger" style="font-size: 9px;"> <strong>Edit</strong></span></a>
							<div class="highslide-maincontent">
								<h3>Please fill up the current job section first.</h3>
							</div>
							<% } %>
						
							<% } %>
							<% } %>

						</h3>
						<div class="about-body">
							<table class="table table_no_border autoWidth"
								style="width: 100% !important;">
								<tr>
									<td class="alignRight" style="width: 32% !important;">Payout Type:</td>
									<td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("PAYOUT_TYPE"), "-")%></td>
								</tr>
						<!-- ===start parvez date: 12-08-2022=== -->
								<% if(hmEmpProfile.get("EMP_BANK_NAME")!=null && !hmEmpProfile.get("EMP_BANK_NAME").equals("-1") && !hmEmpProfile.get("EMP_BANK_NAME").equals("")){
                                %> 
                                <tr>
									<td class="alignRight">Bank Name:</td>
									<td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("EMP_STR_BANK_NAME"), "-")%></td>
								</tr>
                                
                                <% if (hmEmpProfile.get("EMP_ACT_NO") != null && !hmEmpProfile.get("EMP_ACT_NO").equals("")) { %>
								<tr>
									<td class="alignRight">Bank Account No.:</td>
									<td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("EMP_ACT_NO"), "-")%></td>
								</tr>
								<% } %>

								<% if (hmEmpProfile.get("EMP_IFSC_CODE") != null && !hmEmpProfile.get("EMP_IFSC_CODE").equals("")) { %>
								<tr>
									<td class="alignRight">Bank IFSC Code:</td>
									<td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("EMP_IFSC_CODE"), "-")%></td>
								</tr>
								<% } %>

								<% if (hmEmpProfile.get("EMP_BANK_BRANCH") != null && !hmEmpProfile.get("EMP_BANK_BRANCH").equals("")) { %>
								<tr>
									<td class="alignRight">Bank Branch:</td>
									<td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("EMP_BANK_BRANCH"), "-")%></td>
								</tr>
								<% }}
                                  
                                  else if(hmEmpProfile.get("EMP_BANK_NAME")!=null && hmEmpProfile.get("EMP_BANK_NAME").equals("-1")){ 
                                	  if (hmEmpProfile.get("EMP_OTHER_BANK_NAME") != null && !hmEmpProfile.get("EMP_OTHER_BANK_NAME").equals("")) { 
                               			System.out.println("MProfile/1242---EMP_OTHER_BANK_NAME=="+hmEmpProfile.get("EMP_OTHER_BANK_NAME")+"--EMP_BANK_NAME=="+hmEmpProfile.get("EMP_BANK_NAME"));
                               %>
                                	<tr>
										<td class="alignRight">Bank Name:</td>
										<td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("EMP_OTHER_BANK_NAME"), "-")%></td>
									</tr>
                                	  <% if (hmEmpProfile.get("EMP_ACT_NO") != null && !hmEmpProfile.get("EMP_ACT_NO").equals("")) { %>
									<tr>
										<td class="alignRight">Bank Account No.:</td>
										<td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("EMP_ACT_NO"), "-")%></td>
									</tr>
									<% if (hmEmpProfile.get("EMP_OTHER_BANK_IFSC_CODE") != null && !hmEmpProfile.get("EMP_OTHER_BANK_IFSC_CODE").equals("")) { %>
									<tr>
										<td class="alignRight">Bank IFSC Code:</td>
										<td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("EMP_OTHER_BANK_IFSC_CODE"), "-")%></td>
									</tr>
									<%} %>
									<% if (hmEmpProfile.get("EMP_OTHER_BANK_BRANCH") != null && !hmEmpProfile.get("EMP_OTHER_BANK_BRANCH").equals("")) { %>
									<tr>
										<td class="alignRight">Bank Branch:</td>
										<td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("EMP_OTHER_BANK_BRANCH"), "-")%></td>
									</tr>
									<%} %>
								<%}}} %>
					<!-- ===end parvez date: 12-08-2022=== -->

							</table>

							<table class="table table_no_border autoWidth"
								style="width: 100% !important;">
								<tr>
									<td valign="top" style="width: 32% !important;">
										<table cellspacing="1" cellpadding="2"
											class="table table-bordered autoWidth"
											style="table-layout: fixed;">
											<tr><td colspan="3" nowrap="nowrap" align="center"><h5>EARNING DETAILS</h5></td></tr>
											<tr>
												<td class="alignRight">Salary Head</td>
												<td width="30%" class="alignRight">Monthly</td>
												<td width="30%" class="alignRight">Annual</td>
											</tr>
											<%
                                                  double grossAmount = 0.0d;
                                                  double grossYearAmount = 0.0d;
              									  double netTakeHome = 0.0d;
                                                  for(int i=0; salaryHeadDetailsList != null && !salaryHeadDetailsList.isEmpty() && i<salaryHeadDetailsList.size(); i++) {
                                                  	List<String> innerList = salaryHeadDetailsList.get(i);
                                                  		if(innerList.get(1).equals("E")) {
                                                  			double dblEarnMonth = uF.parseToDouble(innerList.get(2));
            												double dblEarnAnnual = uF.parseToDouble(innerList.get(3));
            												grossAmount += dblEarnMonth;
            												grossYearAmount += dblEarnAnnual;
            												
            												netTakeHome += dblEarnMonth;
            												if(alGratuitySlaHeadId.contains(innerList.get(4))) {
            													gratuitySalHeadAmt += dblEarnMonth;
            												}
            									%>
											<tr>
												<td class="alignRight"><%=uF.showData(innerList.get(0), "-")%></td>

												<td align="right" class="textblue" valign="bottom"><%=uF.formatIntoOneDecimal(dblEarnMonth)%></td>
												<td align="right" class="textblue" valign="bottom"><%=uF.formatIntoOneDecimal(dblEarnAnnual)%></td>
												<%-- <td align="right" class="textblue" valign="bottom"><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),dblEarnMonth) %></td>
            												<td align="right" class="textblue" valign="bottom"><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),dblEarnAnnual)%></td> --%>
											</tr>
											<% } %>
											<% } %>
											<tr>
												<td class="alignRight"><strong>Gross Salary</strong> </td>
												<td class="alignRight textblue"><strong><%=uF.formatIntoOneDecimal(grossAmount)%></strong> </td>
												<td class="alignRight textblue"><strong><%=uF.formatIntoOneDecimal(grossYearAmount)%></strong> </td>
												<%--  <td class="alignRight textblue"><strong><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),Math.round(grossAmount))%></strong></td>
													<td class="alignRight textblue"><strong><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),Math.round(grossYearAmount))%></strong></td> --%>
											</tr>
										</table></td>
									<td valign="top">
										<table cellspacing="1" cellpadding="2"
											class="table table-bordered autoWidth">
											<tr><td colspan="3" nowrap="nowrap" align="center"><h5>DEDUCTION DETAILS</h5></td></tr>
											<tr>
												<td class="alignRight">Salary Head</td>
												<td width="30%" class="alignRight">Monthly</td>
												<td width="30%" class="alignRight">Annual</td>
											</tr>
											<% 
                                                  double deductAmount = 0.0d;
                                                  double deductYearAmount = 0.0d;
                                                  
                                                  for(int i=0; salaryHeadDetailsList != null && !salaryHeadDetailsList.isEmpty() && i<salaryHeadDetailsList.size(); i++) {
                                                  List<String> innerList = salaryHeadDetailsList.get(i);
                                                  	if(innerList.get(1).equals("D")) {
                                                  		double dblDeductMonth = uF.parseToDouble(innerList.get(2));
        												double dblDeductAnnual = uF.parseToDouble(innerList.get(3));
        												deductAmount += dblDeductMonth;
        												deductYearAmount += dblDeductAnnual;
        												
        												netTakeHome -= dblDeductMonth;
        												if(alGratuitySlaHeadId.contains(innerList.get(4))) {
        													gratuitySalHeadAmt += dblDeductMonth;
        												}
        									%>
											<tr>
												<td class="alignRight"><%=uF.showData(innerList.get(0), "-")%></td>
												<%-- <td align="right" class="textblue" valign="bottom"><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),dblDeductMonth) %></td>
        										 		<td align="right" class="textblue" valign="bottom"><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),dblDeductAnnual) %></td> --%>

												<td align="right" class="textblue" valign="bottom"><%=uF.formatIntoOneDecimal(dblDeductMonth) %></td>
												<td align="right" class="textblue" valign="bottom"><%=uF.formatIntoOneDecimal(dblDeductAnnual) %></td>
											</tr>
											<% } %>
											<% } %>
											<tr>
												<td class="alignRight"><strong>Deduction</strong>
												</td>
												<%-- 	<td class="alignRight textblue"><strong><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),Math.round(deductAmount))%></strong></td>
													<td class="alignRight textblue"><strong><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),Math.round(deductYearAmount))%></strong></td> --%>

												<td class="alignRight textblue"><strong><%=uF.formatIntoOneDecimal(deductAmount)%></strong>
												</td>
												<td class="alignRight textblue"><strong><%=uF.formatIntoOneDecimal(deductYearAmount)%></strong>
												</td>
											</tr>
										</table></td>
								</tr>
								<%
						double dblCTCMonthly = grossAmount;
						double dblCTCAnnualy = grossYearAmount;
						Map<String, String> hmReimCTC = (Map<String, String>)request.getAttribute("hmReimCTC");
						if(hmReimCTC == null) hmReimCTC = new HashMap<String, String>();
						
						Map<String, String> hmReimCTCHeadAmount = (Map<String, String>)request.getAttribute("hmReimCTCHeadAmount");
						if(hmReimCTCHeadAmount == null) hmReimCTCHeadAmount = new HashMap<String, String>();
						if(hmReimCTC.size() > 0 && hmReimCTCHeadAmount.size() > 0){
						%>
								<tr>
									<td colspan="2" valign="top">
										<table cellspacing="1" cellpadding="2"
											class="table table-bordered autoWidth"
											style="width: 100% !important;">
											<tr><td colspan="3" nowrap="nowrap" align="center"><h5>EARNING DETAILS</h5>
													<h6>[Reimbursement part of CTC]</h6>
												</td>
											</tr>
											<tr>
												<td class="alignRight">Salary Head</td>
												<td width="30%" class="alignRight">Monthly</td>
												<td width="30%" class="alignRight">Annual</td>
											</tr>
											<%
										double grossReimbursementAmount = 0.0d;
										double grossReimbursementYearAmount = 0.0d;
										Iterator<String> it = hmReimCTC.keySet().iterator();
										while(it.hasNext()){
											String strReimCTCId = it.next();
											String strReimCTCName = hmReimCTC.get(strReimCTCId);
											
											double dblReimMonth = uF.parseToDouble(hmReimCTCHeadAmount.get(strReimCTCId));
											double dblReimAnnual = uF.parseToDouble(hmReimCTCHeadAmount.get(strReimCTCId+"_ANNUAL"));
											grossReimbursementAmount += dblReimMonth;
											grossReimbursementYearAmount += dblReimAnnual;
											
											netTakeHome += dblReimMonth;
										%>
											<tr>
												<td class="alignRight"><%=uF.showData(strReimCTCName, "-")%></td>
												<td align="right" class="textblue" valign="bottom"><%=uF.formatIntoOneDecimal(dblReimMonth) %></td>
												<td align="right" class="textblue" valign="bottom"><%=uF.formatIntoOneDecimal(dblReimAnnual)%></td>
												<%-- <td align="right" class="textblue" valign="bottom"><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),dblReimMonth) %></td>
												<td align="right" class="textblue" valign="bottom"><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),dblReimAnnual)%></td> --%>
											</tr>
											<%}
										dblCTCMonthly += grossReimbursementAmount;
										dblCTCAnnualy += grossReimbursementYearAmount;
										%>
											<tr>
												<td class="alignRight"><strong>Total</strong> </td>
												<td class="alignRight textblue"><strong><%=uF.formatIntoOneDecimal(grossReimbursementAmount)%></strong> </td>
												<td class="alignRight textblue"><strong><%=uF.formatIntoOneDecimal(grossReimbursementYearAmount)%></strong> </td>
												<%-- <td class="alignRight textblue"><strong><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),Math.round(grossReimbursementAmount))%></strong></td>
												<td class="alignRight textblue"><strong><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),Math.round(grossReimbursementYearAmount))%></strong></td> --%>
											</tr>
										</table></td>
								</tr>
								<%} %>
								<%
						List<List<String>> salaryAnnualVariableDetailsList = (List<List<String>>)request.getAttribute("salaryAnnualVariableDetailsList");
						if(salaryAnnualVariableDetailsList == null) salaryAnnualVariableDetailsList = new ArrayList<List<String>>();
						int nAnnualVariSize = salaryAnnualVariableDetailsList.size();
						if(nAnnualVariSize > 0){
						%>
								<tr>
									<td colspan="2" valign="top">
										<table cellspacing="1" cellpadding="2"
											class="table table-bordered autoWidth"
											style="width: 100% !important;">
											<tr>
												<td colspan="3" nowrap="nowrap" align="center"><h5>EARNING DETAILS</h5>
													<h6>[Annual Variables]</h6>
												</td>
											</tr>
											<tr>
												<td class="alignRight">Salary Head</td>
												<td width="30%" class="alignRight">Monthly</td>
												<td width="30%" class="alignRight">Annual</td>
											</tr>
											<%	
										double grossAnnualAmount = 0.0d;
										double grossAnnualYearAmount = 0.0d;
										for(int i = 0; i < nAnnualVariSize; i++){
											List<String> innerList = salaryAnnualVariableDetailsList.get(i);
											double dblEarnMonth = uF.parseToDouble(innerList.get(2));
											double dblEarnAnnual = uF.parseToDouble(innerList.get(3));
											grossAnnualAmount += dblEarnMonth;
											grossAnnualYearAmount += dblEarnAnnual;
								%>
											<tr>
												<td class="alignRight"><%=uF.showData(innerList.get(0), "-")%></td>
												<td align="right" class="textblue" valign="bottom"><%=uF.formatIntoOneDecimal(dblEarnMonth) %></td>
												<td align="right" class="textblue" valign="bottom"><%=uF.formatIntoOneDecimal(dblEarnAnnual)%></td>
												<%-- <td align="right" class="textblue" valign="bottom"><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),dblEarnMonth) %></td>
												<td align="right" class="textblue" valign="bottom"><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),dblEarnAnnual)%></td> --%>
											</tr>
											<%	} 
										dblCTCMonthly += grossAnnualAmount;
										dblCTCAnnualy += grossAnnualYearAmount;
									%>
											<tr>
												<td class="alignRight"><strong>Total</strong>
												</td>
												<td class="alignRight textblue"><strong><%=uF.formatIntoOneDecimal(grossAnnualAmount)%></strong>
												</td>
												<td class="alignRight textblue"><strong><%=uF.formatIntoOneDecimal(grossAnnualYearAmount)%></strong>
												</td>
												<%-- <td class="alignRight textblue"><strong><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),Math.round(grossAnnualAmount))%></strong></td>
											<td class="alignRight textblue"><strong><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),Math.round(grossAnnualYearAmount))%></strong></td> --%>

											</tr>
										</table></td>
								</tr>
								<%}%>

								<%
								List<List<String>> salaryContributionDetailsList = (List<List<String>>) request.getAttribute("salaryContributionDetailsList");
								
								Map<String,String> hmContribution = (Map<String,String>) request.getAttribute("hmContribution");
								if(hmContribution == null) hmContribution = new HashMap<String, String>();
								double dblMonthContri = 0.0d;
								double dblAnnualContri = 0.0d;
								boolean isEPF = uF.parseToBoolean((String)request.getAttribute("isEPF"));
								boolean isESIC = uF.parseToBoolean((String)request.getAttribute("isESIC"));
								boolean isLWF = uF.parseToBoolean((String)request.getAttribute("isLWF"));
								if(isEPF || isESIC || isLWF || (salaryContributionDetailsList!=null && salaryContributionDetailsList.size()>0)) {
							%>
								<tr>
									<td colspan="2" valign="top">
										<table cellspacing="1" cellpadding="2" class="table table-bordered autoWidth" style="width: 40% !important;">
											<tr> <td colspan="3" nowrap="nowrap" align="center"><h5>CONTRIBUTION DETAILS</h5></td></tr>
											<tr>
												<td class="alignRight">Contribution Head</td>
												<td width="30%" class="alignRight">Monthly</td>
												<td width="30%" class="alignRight">Annual</td>
											</tr>
											<%if(isEPF){
											double dblEPFMonth = uF.parseToDouble(hmContribution.get("EPF_MONTHLY"));
											//System.out.println("MP/1506---dblEPFMonth=="+dblEPFMonth);
											double dblEPFAnnual = uF.parseToDouble(hmContribution.get("EPF_ANNUALY"));
											dblMonthContri += dblEPFMonth;
											dblAnnualContri += dblEPFAnnual;
										%>
											<tr>
												<td class="alignRight">Employer PF</td>
												<td align="right" class="textblue" valign="bottom"><%=uF.formatIntoOneDecimal(dblEPFMonth)%></td>
												<td align="right" class="textblue" valign="bottom"><%=uF.formatIntoOneDecimal(dblEPFAnnual)%></td>
												<%-- <td align="right" class="textblue" valign="bottom"><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),dblEPFMonth) %></td>
										 		<td align="right" class="textblue" valign="bottom"><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),dblEPFAnnual) %></td> --%>
											</tr>
											<%} %>
											<%if(isESIC){
											
											double dblESIMonth = uF.parseToDouble(hmContribution.get("ESI_MONTHLY"));
											double dblESIAnnual = uF.parseToDouble(hmContribution.get("ESI_ANNUALY"));
											dblMonthContri += dblESIMonth;
											dblAnnualContri += dblESIAnnual;
										%>
											<tr>
												<td class="alignRight">Employer ESI</td>
												<td align="right" class="textblue" valign="bottom"><%=uF.formatIntoOneDecimal(dblESIMonth)%></td>
												<td align="right" class="textblue" valign="bottom"><%=uF.formatIntoOneDecimal(dblESIAnnual)%></td>

												<%-- <td align="right" class="textblue" valign="bottom"><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),dblESIMonth) %></td>
										 		<td align="right" class="textblue" valign="bottom"><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),dblESIAnnual) %></td> --%>
											</tr>
											<%} %>
											<%if(isLWF){
											double dblLWFMonth = uF.parseToDouble(hmContribution.get("LWF_MONTHLY"));
											double dblLWFAnnual = uF.parseToDouble(hmContribution.get("LWF_ANNUALY"));
											dblMonthContri += dblLWFMonth;
											dblAnnualContri += dblLWFAnnual;
										%>
											<tr>
												<td class="alignRight">Employer LWF</td>
												<td align="right" class="textblue" valign="bottom"><%=uF.formatIntoOneDecimal(dblLWFMonth) %></td>
												<td align="right" class="textblue" valign="bottom"><%=uF.formatIntoOneDecimal(dblLWFAnnual) %></td>

												<%-- <td align="right" class="textblue" valign="bottom"><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),dblLWFMonth) %></td>
										 		<td align="right" class="textblue" valign="bottom"><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),dblLWFAnnual) %></td> --%>
											</tr>
											<%} 
										/* dblCTCMonthly += dblMonthContri;
										dblCTCAnnualy += dblAnnualContri; */
										%>

									<%	
										//System.out.println("gratuitySalHeadAmt ===>> " + gratuitySalHeadAmt);
										double contributionMonthAmount = 0.0d;
										double contributionYearAmount = 0.0d;
										for(int i = 0; salaryContributionDetailsList!=null && i<salaryContributionDetailsList.size(); i++) {
											List<String> innerList = salaryContributionDetailsList.get(i);
											double dblEarnMonth = uF.parseToDouble(innerList.get(2));
											double dblEarnAnnual = uF.parseToDouble(innerList.get(3));
											if(innerList.get(4).equals(IConstants.GRATUITY+"")) {
												dblEarnMonth = (gratuitySalHeadAmt * uF.parseToDouble(hmGratuityPolicy.get("CALCULATE_PERCENT"))) / 100;
												String strEarnMonth = uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblEarnMonth);
												dblEarnMonth = uF.parseToDouble(strEarnMonth);
												dblEarnAnnual = dblEarnMonth * 12;
											}
											dblMonthContri += dblEarnMonth;
											dblAnnualContri += dblEarnAnnual;
								%>
											<tr>
												<td class="alignRight"><%=uF.showData(innerList.get(0), "-")%></td>
												<td align="right" class="textblue" valign="bottom"><%=uF.formatIntoOneDecimal(dblEarnMonth) %></td>
												<td align="right" class="textblue" valign="bottom"><%=uF.formatIntoOneDecimal(dblEarnAnnual)%></td>
												<%-- <td align="right" class="textblue" valign="bottom"><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),dblEarnMonth) %></td>
												<td align="right" class="textblue" valign="bottom"><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),dblEarnAnnual)%></td> --%>
											</tr>
											<%	} 
										dblCTCMonthly += dblMonthContri;
										dblCTCAnnualy += dblAnnualContri;
									%>
									
											<tr><td class="alignRight"><strong>Contribution Total</strong></td>
												<td class="alignRight textblue"><strong><%=uF.formatIntoOneDecimal(dblMonthContri)%></strong></td>
												<td class="alignRight textblue"><strong><%=uF.formatIntoOneDecimal(dblAnnualContri)%></strong></td>
												<%-- <td class="alignRight textblue"><strong><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),Math.round(dblMonthContri))%></strong></td>
												<td class="alignRight textblue"><strong><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),Math.round(dblAnnualContri))%></strong></td> --%>
											</tr>
										</table></td>
								</tr>
								<%}%>
							</table>

							<table class="table table_no_border autoWidth"
								style="width: 100% !important;">
								<tr>
								<!-- ===start parvez date: 12-08-2022=== -->
									<!-- <td class="alignRight" style="width: 32% !important;">Net Take Home Per Month:</td> -->
									<td class="alignRight" style="width: 32% !important;">Net Earning:</td>
									<td class="textblue" valign="bottom"><%=uF.showData(strCurr,"")+ uF.formatIntoOneDecimal(netTakeHome)%></td>
									<%-- <td class="textblue" valign="bottom"> <%=uF.showData(strCurr,"")+ uF.getRoundOffValue(uF.parseToInt(roundOffCondition),Math.round(netTakeHome))%></td> --%>
								</tr>
								<tr>
									<!-- <td class="alignRight">Cost To Company (Monthly):</td> -->
									<td class="alignRight">Gross Earning:</td>
									<td class="textblue" valign="bottom"><%=uF.showData(strCurr,"")+ uF.formatIntoOneDecimal(dblCTCMonthly)%></td>
									<%-- <td class="textblue" valign="bottom"> <%=uF.showData(strCurr,"")+ uF.getRoundOffValue(uF.parseToInt(roundOffCondition),Math.round(dblCTCMonthly))%></td> --%>
								</tr>
								<tr>
									<!-- <td class="alignRight">Cost To Company (Annually):</td> -->
									<td class="alignRight">Annual Gross Earning:</td>
									<td class="textblue" valign="bottom"><%=uF.showData(strCurr,"")+uF.formatIntoOneDecimal(dblCTCAnnualy)%></td>
									<%-- <td class="textblue" valign="bottom"> <%=uF.showData(strCurr,"")+uF.getRoundOffValue(uF.parseToInt(roundOffCondition),Math.round(dblCTCAnnualy))%></td> --%>
						<!-- ===end parvez date: 12-08-2022=== -->		
								</tr>
								<%if(uF.parseToInt((String)request.getAttribute("salaryStructure")) == IConstants.S_GRADE_WISE){ %>
								<tr>
									<td class="alignRight">Salary Scale:</td>
									<%-- <td class="textblue" valign="bottom"> <%=uF.showData(strCurr,"")+uF.showData(hmEmpProfile.get("MINSCALE"),"")+" - "+uF.showData(strCurr,"")+uF.showData(hmEmpProfile.get("MAXSCALE"),"")+" Increment Amount: - "+uF.showData(strCurr,"")+uF.showData(hmEmpProfile.get("INCREMENTAMOUNT"),"")%></td> --%>
									<td class="textblue" valign="bottom"><%=uF.showData(strCurr,"")+uF.showData(hmEmpProfile.get("MINSCALE"),"")+" - "+uF.showData(strCurr,"")+uF.showData(hmEmpProfile.get("INCREMENTAMOUNT"),"")+" - "+uF.showData(strCurr,"")+uF.showData(hmEmpProfile.get("MAXSCALE"),"")%></td>
								</tr>
								<%} %>
							</table>

							<%
						if (strUserType != null && (strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) || strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.ACCOUNTANT))){ 
							Map<String, String> hmPerkAlign = (Map<String, String>) request.getAttribute("hmPerkAlign");
							if(hmPerkAlign == null) hmPerkAlign = new HashMap<String,String>(); 
							Map<String, List<Map<String, String>>> hmPerkAlignSalary = (Map<String, List<Map<String, String>>>) request.getAttribute("hmPerkAlignSalary");
							if(hmPerkAlignSalary == null) hmPerkAlignSalary = new HashMap<String, List<Map<String, String>>>();
							Map<String, Map<String, String>> hmAssignPerkSalary = (Map<String, Map<String, String>>) request.getAttribute("hmAssignPerkSalary");
							if(hmAssignPerkSalary == null) hmAssignPerkSalary = new HashMap<String, Map<String,String>>();
							List<String> alPerkSalaryAppliedId = (List<String>) request.getAttribute("alPerkSalaryAppliedId");
							if(alPerkSalaryAppliedId == null) alPerkSalaryAppliedId = new ArrayList<String>();
							
							if(hmPerkAlign.size() > 0 && hmPerkAlignSalary.size() > 0){
						%>
							<div
								style="float: left; width: 100%; border-top: 1px solid #ccc;">
								<strong>Perk Options</strong>
							</div>
							<table class="table table_no_border"
								style="width: 100% !important;">
								<%	Iterator<String> it = hmPerkAlign.keySet().iterator();
								int x = 0;
								while(it.hasNext()){
									String strSalaryHeadId = it.next();
									String strSalaryHeadName = hmPerkAlign.get(strSalaryHeadId); 
									
									List<Map<String, String>> outerList = hmPerkAlignSalary.get(strSalaryHeadId);
									if (outerList == null) outerList = new ArrayList<Map<String, String>>();
									int nOuterList = outerList.size();
										
									x++;
							%>
								<tr>
									<td colspan="2"><strong><%=x %>. <%=strSalaryHeadName %></strong>
									</td>
								</tr>
								<%if(nOuterList > 0){ 
										for(int i = 0; i < nOuterList; i++){
											Map<String, String> hmPerkSalary = outerList.get(i);
											String strPerkSalaryId = hmPerkSalary.get("PERK_SALARY_ID");
											Map<String, String> hmAssignPerk = (Map<String, String>) hmAssignPerkSalary.get(strSalaryHeadId+"_"+strPerkSalaryId);
											if(hmAssignPerk == null) hmAssignPerk = new HashMap<String, String>();
											String strPerkSalaryIdStatus = ""; 
											String strPerkSalaryIdMsg = "This is non-taxable.";
											int nOptType = 0;
											if(uF.parseToBoolean(hmAssignPerk.get("STATUS")) && !uF.parseToBoolean(hmPerkSalary.get("PERK_IS_OPTIMAL"))){
												strPerkSalaryIdStatus = "checked";
												strPerkSalaryIdMsg = "This is taxable.";
											} else if(uF.parseToBoolean(hmAssignPerk.get("STATUS")) && uF.parseToBoolean(hmPerkSalary.get("PERK_IS_OPTIMAL"))){
												strPerkSalaryIdStatus = "checked";
												strPerkSalaryIdMsg = "Opt In.";
												nOptType = 1;
											} else if(!uF.parseToBoolean(hmAssignPerk.get("STATUS")) && uF.parseToBoolean(hmPerkSalary.get("PERK_IS_OPTIMAL"))){
												strPerkSalaryIdStatus = "";
												strPerkSalaryIdMsg = "Opt Out."; 
												nOptType = 1;
											}
											
											boolean accessFlag = false;
											String strDisabled = "";
											if(strUserType != null && strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) && hmAssignPerkSalary.containsKey(strSalaryHeadId+"_"+strPerkSalaryId)){
												 accessFlag = true;
												 strDisabled = " disabled";
											}
										%>
								<tr>
									<td class="textblue" nowrap="nowrap"><%=x+"."+(i+1) %>. <strong><%=uF.showData(hmPerkSalary.get("PERK_NAME"),"")+" ["+uF.showData(hmPerkSalary.get("PERK_CODE"),"")+"]" %></strong>&nbsp;
										<strong><%=uF.showData(strCurr,"")+uF.showData(uF.formatIntoOneDecimal(uF.parseToDouble(hmPerkSalary.get("PERK_AMOUNT"))),"") %></strong>&nbsp;
									</td>
									<td><input type="checkbox"
										name="strPerkSalaryId_<%=strPerkSalaryId %>"
										id="strPerkSalaryId_<%=strPerkSalaryId %>"
										value="<%=strPerkSalaryId %>"
										onclick="perkStatus(this,<%=strPerkSalaryId %>,<%=nOptType %>)"
										<%=strPerkSalaryIdStatus %> <%=strDisabled %> /> <span
										id="spanPerkSalaryId_<%=strPerkSalaryId %>"><%=strPerkSalaryIdMsg %></span>
										<%
								                    if(!accessFlag) {
									                    if(alPerkSalaryAppliedId.contains(strPerkSalaryId)) { %>
										<input type="button" class="btn btn-primary" value="Save"
										onclick="alert('You have already applied this.')" /> <% } else { %>
										<input type="button" class="btn btn-primary" value="Save"
										onclick="savePerkStatus('<%=strProID%>','<%=strPerkSalaryId %>')" />
										<%	}
								                    }%>
									</td>
								</tr>
								<%} 	
									}
								} %>
							</table>
							<%	}
						}
						%>

							<%if (strUserType != null && (strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) || strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.ACCOUNTANT))){
							List<Map<String, String>> alReimbursementCTC = (List<Map<String, String>>)request.getAttribute("alReimbursementCTC");
							if(alReimbursementCTC == null) alReimbursementCTC = new ArrayList<Map<String,String>>();	
							
							Map<String, List<Map<String, String>>> hmReimbursementCTCHead = (Map<String, List<Map<String, String>>>) request.getAttribute("hmReimbursementCTCHead");
							if(hmReimbursementCTCHead == null) hmReimbursementCTCHead = new HashMap<String, List<Map<String,String>>>();
							
							Map<String, Map<String, String>> hmAssignReimHead = (Map<String, Map<String, String>>) request.getAttribute("hmAssignReimHead");
							if(hmAssignReimHead == null) hmAssignReimHead = new HashMap<String, Map<String,String>>();
							
							List<String> alReimbursementCTCAppliedId = (List<String>) request.getAttribute("alReimbursementCTCAppliedId");
							if(alReimbursementCTCAppliedId == null) alReimbursementCTCAppliedId = new ArrayList<String>();
							
							if(alReimbursementCTC.size() > 0){
						%>
							<div
								style="float: left; width: 100%; border-top: 1px solid #ccc;">
								<strong>Reimbursement Part of CTC Options</strong>
							</div>
							<table style="width: 100% !important;">
								<%		int nAlReimbursementCTC = alReimbursementCTC.size();
								int x = 0; 
								for(int i=0; i < nAlReimbursementCTC; i++){
									Map<String, String> hmReimbursementCTCInner = (Map<String, String>) alReimbursementCTC.get(i);
									String strReimCTCId = hmReimbursementCTCInner.get("REIMBURSEMENT_CTC_ID");
									
									List<Map<String, String>> alReimCTCHead = hmReimbursementCTCHead.get(strReimCTCId);
									if(alReimCTCHead == null) alReimCTCHead = new ArrayList<Map<String,String>>();
									
									int nAlReimCTCHead = alReimCTCHead.size();  
									if(nAlReimCTCHead > 0){			
										x++;
						%>
								<tr>
									<td colspan="2"><strong><%=(x) %>. <%=uF.showData(hmReimbursementCTCInner.get("REIMBURSEMENT_CTC_NAME"),"") %>
											[<%=uF.showData(hmReimbursementCTCInner.get("REIMBURSEMENT_CTC_CODE"),"") %>]</strong>
									</td>
								</tr>
								<%
										for(int j = 0; j < nAlReimCTCHead; j++){
											Map<String, String> hmReimCTCHeadInner = (Map<String, String>) alReimCTCHead.get(j);
											if(hmReimCTCHeadInner == null) hmReimCTCHeadInner = new HashMap<String, String>();
											String strReimCTCHeadId = hmReimCTCHeadInner.get("REIMBURSEMENT_HEAD_ID");
											
											Map<String, String> hmAssignReim = (Map<String, String>) hmAssignReimHead.get(strReimCTCHeadId+"_"+strReimCTCId);
											if(hmAssignReim == null) hmAssignReim = new HashMap<String, String>();
											String strReimHeadIdStatus = ""; 
											String strReimHeadIdMsg = "";
											int nOptType = 0;
											if(uF.parseToBoolean(hmAssignReim.get("STATUS")) && !uF.parseToBoolean(hmReimCTCHeadInner.get("REIMBURSEMENT_HEAD_IS_OPTIMAL"))){
												strReimHeadIdStatus = "checked";
												strReimHeadIdMsg = "";
											} else if(uF.parseToBoolean(hmAssignReim.get("STATUS")) && uF.parseToBoolean(hmReimCTCHeadInner.get("REIMBURSEMENT_HEAD_IS_OPTIMAL"))){
												strReimHeadIdStatus = "checked";
												strReimHeadIdMsg = "Opt In.";
												nOptType = 1;
											} else if(!uF.parseToBoolean(hmAssignReim.get("STATUS")) && uF.parseToBoolean(hmReimCTCHeadInner.get("REIMBURSEMENT_HEAD_IS_OPTIMAL"))){
												strReimHeadIdStatus = "";
												strReimHeadIdMsg = "Opt Out.";
												nOptType = 1;
											}
											
											
											boolean accessFlag = false;
											String strDisabled = "";
											if(strUserType != null && strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) && hmAssignReimHead.containsKey(strReimCTCHeadId+"_"+strReimCTCId)){
												 accessFlag = true;
												 strDisabled = " disabled";
											}
									%>
								<tr>
									<td class="textblue" nowrap="nowrap"><%=(x)+"."+(j+1) %>.
										<strong><%=uF.showData(hmReimCTCHeadInner.get("REIMBURSEMENT_HEAD_NAME"),"")+" ["+uF.showData(hmReimCTCHeadInner.get("REIMBURSEMENT_HEAD_CODE"),"")+"]" %></strong>&nbsp;
										<strong><%=uF.showData(strCurr,"")+uF.showData(uF.formatIntoOneDecimal(uF.parseToDouble(hmReimCTCHeadInner.get("REIMBURSEMENT_HEAD_AMOUNT"))),"") %></strong>&nbsp;
									</td>
									<td>
										<%if(uF.parseToBoolean(hmReimCTCHeadInner.get("REIMBURSEMENT_HEAD_IS_OPTIMAL"))){ %>
										<input type="checkbox"
										name="strReimCTCHeadId_<%=strReimCTCHeadId %>"
										id="strReimCTCHeadId_<%=strReimCTCHeadId %>"
										value="<%=strReimCTCHeadId %>"
										onclick="reimbursementHeadStatus(this,<%=strReimCTCHeadId %>,<%=nOptType %>)"
										<%=strReimHeadIdStatus %> <%=strDisabled %> /> <span
										id="spanReimCTCHeadId_<%=strReimCTCHeadId %>"><%=strReimHeadIdMsg %></span>
										<%
									                    if(!accessFlag){
										                    if(alReimbursementCTCAppliedId.contains(strReimCTCHeadId)){ %>
										<input type="button" class="btn btn-primary" value="Save"
										onclick="alert('You have already applied this.')" /> <%} else { %>
										<input type="button" class="btn btn-primary" value="Save"
										onclick="saveReimCTCHeadStatus('<%=strProID%>','<%=strReimCTCHeadId %>')" />
										<%	}
									                    }
								                    }%>
									</td>
								</tr>
								<%			}
									}
				            	}%>
							</table>
							<%}
	            		} %>
						</div>
						<%if (!isFilledStatus) {%>
						<div class="about-footer">
							<img src="images1/warning.png" />
						</div>
						<% } %>
					</div>
					<br />
					<% } %>

					<div class="about-item">
						<h3 class="about-header">
							Statutory Compliance Applied
							<% if (strUserType != null && (strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.RECRUITER)) 
                                  && strBaseUserType != null && (strBaseUserType.equalsIgnoreCase(IConstants.ADMIN) || strBaseUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strBaseUserType.equalsIgnoreCase(IConstants.RECRUITER))) {
							%>
							<!-- ===start parvez date: 18-10-2021=== -->
								<% if(uF.parseToBoolean(hmEmpProfile.get("IS_ALIVE")) && hmEmpProfile.get("EMPLOYMENT_END_DATE") == null){ %>
							<!-- ===end parvez date: 18-10-2021=== -->
								&nbsp;&nbsp;<a href="AddEmployee.action?operation=U&mode=profile&step=8&empId=<%=strProID%>"><span class="label label-danger" style="font-size: 9px;"> <strong>Edit</strong></span></a>
							<!-- ===start parvez date: 18-10-2021=== -->
								<% } %>
							<!-- ===end parvez date: 18-10-2021=== -->
							<% } %>
						</h3>
						<div class="about-body">
							<table class="table table_no_border autoWidth"
								style="width: 100% !important;">
								<% 
                                    StringBuilder TdsForm16Or16A = null;
                                    if(uF.parseToBoolean(hmEmpProfile.get("IS_FORM16"))) {
                                     if(TdsForm16Or16A == null) TdsForm16Or16A = new StringBuilder();
                                     TdsForm16Or16A.append("Form 16");
                                    }
                                    if(uF.parseToBoolean(hmEmpProfile.get("IS_FORM16_A"))) {
                                     if(TdsForm16Or16A == null) TdsForm16Or16A = new StringBuilder();
                                     TdsForm16Or16A.append("Form 16 A");
                                    }
                                    if(TdsForm16Or16A == null) TdsForm16Or16A = new StringBuilder("-");
                                    %>
								<tr>
									<td class="alignRight" style="width: 32% !important;">TDS:
									</td>
									<td class="textblue" valign="bottom"><%=uF.showData(TdsForm16Or16A.toString(), "-")%></td>
								</tr>
							</table>
						</div>
					</div>
					<br />
					<div class="about-item">
						<h3 class="about-header">Corporate Information & Personal
							Information</h3>
						<div class="about-body">
							<div class="box box-default collapsed-box"
								style="margin-top: 10px;">
								<div class="box-header with-border">
									<h3 class="box-title"
										style="font-size: 14px; padding-right: 10px;">Corporate
										Information</h3>

									<div class="box-tools pull-right">
										<% if (strUserType != null && (strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.RECRUITER)) 
			                                  && strBaseUserType != null && (strBaseUserType.equalsIgnoreCase(IConstants.ADMIN) || strBaseUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strBaseUserType.equalsIgnoreCase(IConstants.RECRUITER))) {
										%>
										<!-- ===start parvez date: 18-10-2021=== -->
											<% if(uF.parseToBoolean(hmEmpProfile.get("IS_ALIVE")) && hmEmpProfile.get("EMPLOYMENT_END_DATE") == null){ %>
										
												<a href="AddEmployee.action?operation=U&mode=profile&step=8&empId=<%=strProID%>">&nbsp;&nbsp;<span class="label label-danger" style="font-size: 9px;"> <strong>Edit</strong></span></a>
											<% } %>
										<!-- ===end parvez date: 18-10-2021=== -->
										<% } %>
										<button class="btn btn-box-tool" data-widget="collapse">
											<i class="fa fa-plus"></i>
										</button>
										<button class="btn btn-box-tool" data-widget="remove">
											<i class="fa fa-times"></i>
										</button>
									</div>
								</div>
								<!-- /.box-header -->
								<div class="box-body"
									style="padding: 5px; overflow-y: auto; display: none;">
									<table class="table table_no_border autoWidth"
										style="width: 100% !important;">
										<tr>
											<td class="alignRight" style="width: 32% !important;">Corporate
												Mobile:</td>
											<td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("CORPORATE_MOBILE"), "-")%></td>
										</tr>
										<tr>
											<td class="alignRight">Corporate Desk:</td>
											<td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("CORPORATE_DESK"), "-")%></td>
										</tr>
										<tr>
											<td class="alignRight">Corporate id:</td>
											<td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("EMAIL_SEC"), "-")%></td>
										</tr>
										<tr>
											<td class="alignRight">Skype id:</td>
											<td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("SKYPE_ID"), "-")%></td>
										</tr>
									</table>
								</div>
								<!-- /.box-body -->
							</div>
							<div class="box box-default collapsed-box"
								style="margin-top: 10px;">
								<div class="box-header with-border">
									<h3 class="box-title"
										style="font-size: 14px; padding-right: 10px;">Personal
										Information</h3>
									<div class="box-tools pull-right">
				                        <% if ((strUserType != null && (strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.RECRUITER) || strUserType.equalsIgnoreCase(IConstants.EMPLOYEE))) 
			                                  || (strBaseUserType != null && (strBaseUserType.equalsIgnoreCase(IConstants.ADMIN) || strBaseUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strBaseUserType.equalsIgnoreCase(IConstants.RECRUITER) || strBaseUserType.equalsIgnoreCase(IConstants.EMPLOYEE)))) {
										%>
										<!-- ===start parvez date: 18-10-2021=== -->
												<% if(uF.parseToBoolean(hmEmpProfile.get("IS_ALIVE")) && hmEmpProfile.get("EMPLOYMENT_END_DATE") == null){ %>
										
													<a href="AddEmployee.action?operation=U&mode=profile&step=1&empId=<%=strProID%>">&nbsp;&nbsp;<span class="label label-danger" style="font-size: 9px;"> <strong>Edit</strong></span></a>
												<% } %>
										<!-- ===end parvez date: 18-10-2021=== -->
												
										<% } %>
										<button class="btn btn-box-tool" data-widget="collapse">
											<i class="fa fa-plus"></i>
										</button>
										<button class="btn btn-box-tool" data-widget="remove">
											<i class="fa fa-times"></i>
										</button>
									</div>
								</div>
								<!-- /.box-header -->
								<div class="box-body"
									style="padding: 5px; overflow-y: auto; display: none;">
									<table class="table table_no_border autoWidth"
										style="width: 100% !important;">
										<tr>
											<td class="alignRight" style="width: 32% !important;">Current
												Address:</td>
											<td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("CURRENT_ADDRESS"), "") + ", " + uF.showData(hmEmpProfile.get("CURRENT_CITY"), "") + ", " + uF.showData(hmEmpProfile.get("CURRENT_STATE"), "") + ", "
                                                            + uF.showData(hmEmpProfile.get("CURRENT_COUNTRY"), "")%></td>
										</tr>
										<tr>
											<td class="alignRight">Permanent Address:</td>
											<td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("ADDRESS"), "") + ", " + uF.showData(hmEmpProfile.get("CITY"), "") + ", " + uF.showData(hmEmpProfile.get("STATE"), "") + ", "
                                                            + uF.showData(hmEmpProfile.get("COUNTRY"), "")%></td>
										</tr>
										<tr>
											<td class="alignRight">Landline:</td>
											<td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("CONTACT"), "-")%></td>
										</tr>
										<tr>
											<td class="alignRight">Mobile:</td>
											<td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("CONTACT_MOB"), "-")%></td>
										</tr>
										<tr>
											<td class="alignRight">Email id:</td>
											<td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("EMAIL"), "-")%></td>
										</tr>
										<tr>
											<td class="alignRight">Date of Birth:</td>
											<td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("DOB"), "-")%></td>
										</tr>
										<tr>
											<td class="alignRight">Gender:</td>
											<td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("GENDER"), "-")%></td>
										</tr>
										<tr>
											<td class="alignRight">Blood Group:</td>
											<td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("EMP_BLOOD_GROUP"), "-")%></td>
										</tr>
										<tr>
											<td class="alignRight">Marital Status:</td>
											<td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("MARITAL_STATUS"), "-")%></td>
										</tr>
										<% if (hmEmpProfile.get("MARITAL_STATUS") != null && hmEmpProfile.get("MARITAL_STATUS").equals("Married")) { %>
										<tr>
											<td class="alignRight">Date Of Marriage:</td>
											<td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("MARRAIGE_DATE"), "-")%></td>
										</tr>
										<% } %>
									</table>
								</div>
								<!-- /.box-body -->
							</div>
							<div class="box box-default collapsed-box"
								style="margin-top: 10px;">
								<div class="box-header with-border">
									<h3 class="box-title"
										style="font-size: 14px; padding-right: 10px;">Other
										Personal Information</h3>
									<div class="box-tools pull-right">
				                        <% if ((strUserType != null && (strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.RECRUITER) || strUserType.equalsIgnoreCase(IConstants.EMPLOYEE))) 
			                                  || (strBaseUserType != null && (strBaseUserType.equalsIgnoreCase(IConstants.ADMIN) || strBaseUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strBaseUserType.equalsIgnoreCase(IConstants.RECRUITER) || strBaseUserType.equalsIgnoreCase(IConstants.EMPLOYEE)))) {
										%>
										<!-- ===start parvez date: 18-10-2021=== -->
												<% if(uF.parseToBoolean(hmEmpProfile.get("IS_ALIVE")) && hmEmpProfile.get("EMPLOYMENT_END_DATE") == null){ %>
											
												<a href="AddEmployee.action?operation=U&mode=profile&step=1&empId=<%=strProID%>">&nbsp;&nbsp;<span class="label label-danger" style="font-size: 9px;"> <strong>Edit</strong></span></a>
											<% } %>
										<!-- ===end parvez date: 18-10-2021=== -->
										<% } %>
										<button class="btn btn-box-tool" data-widget="collapse">
											<i class="fa fa-plus"></i>
										</button>
										<button class="btn btn-box-tool" data-widget="remove">
											<i class="fa fa-times"></i>
										</button>
									</div>
								</div>
								<!-- /.box-header -->
								<div class="box-body"
									style="padding: 5px; overflow-y: auto; display: none;">
									<table class="table table_no_border autoWidth"
										style="width: 100% !important;">
										<tr>
											<td class="alignRight" style="width: 32% !important;">Pan
												No.:</td>
											<td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("PAN_NO"), "-")%></td>
										</tr>
										<tr>
											<td class="alignRight">Passport No.:</td>
											<td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("PASSPORT_NO"), "-")%></td>
										</tr>
										<tr>
											<td class="alignRight">Passport expires on:</td>
											<td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("PASSPORT_EXPIRY"), "-")%></td>
										</tr>
										<tr>
											<td class="alignRight">MRD No.:</td>
											<td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("MRD_NO"), "-")%></td>
										</tr>
										<tr>
											<td class="alignRight">Are you medical professional?:</td>
											<td class="textblue" valign="bottom"><%=uF.showData(uF.showYesNo(hmEmpProfile.get("IS_MEDICAL_PROFESSIONAL")), "-")%></td>
										</tr>
										<% if(hmEmpProfile.get("EMP_KMC_NO") != null && !hmEmpProfile.get("EMP_KMC_NO").equals("")) { %>
										<tr>
											<td class="alignRight">KMC No.:</td>
											<td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("EMP_KMC_NO"), "-")%></td>
										</tr>
										<% } %>
										<% if(hmEmpProfile.get("EMP_KNC_NO") != null && !hmEmpProfile.get("EMP_KNC_NO").equals("")) { %>
										<tr>
											<td class="alignRight">KNC No.:</td>
											<td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("EMP_KNC_NO"), "-")%></td>
										</tr>
										<% } %>
										<% if((hmEmpProfile.get("EMP_KNC_NO") != null && !hmEmpProfile.get("EMP_KNC_NO").equals("")) || (hmEmpProfile.get("EMP_KMC_NO") != null && !hmEmpProfile.get("EMP_KMC_NO").equals(""))) { %>
										<tr>
											<td class="alignRight">Renewal Date:</td>
											<td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("RENEWAL_DATE"), "-")%></td>
										</tr>
										<% } %>
									</table>
								</div>
								<!-- /.box-body -->
							</div>
							<div class="box box-default collapsed-box"
								style="margin-top: 10px;">
								<div class="box-header with-border">
									<h3 class="box-title"
										style="font-size: 14px; padding-right: 10px;">Emergency
										Information</h3>
									<div class="box-tools pull-right">
										<% if ((strUserType != null && (strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.RECRUITER) || strUserType.equalsIgnoreCase(IConstants.EMPLOYEE))) 
			                                  || (strBaseUserType != null && (strBaseUserType.equalsIgnoreCase(IConstants.ADMIN) || strBaseUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strBaseUserType.equalsIgnoreCase(IConstants.RECRUITER) || strBaseUserType.equalsIgnoreCase(IConstants.EMPLOYEE)))) {
										%>
										<!-- ===start parvez date: 18-10-2021=== -->
											<% if(uF.parseToBoolean(hmEmpProfile.get("IS_ALIVE")) && hmEmpProfile.get("EMPLOYMENT_END_DATE") == null){ %>
												<a href="AddEmployee.action?operation=U&mode=profile&step=1&empId=<%=strProID%>">&nbsp;&nbsp;<span class="label label-danger" style="font-size: 9px;"> <strong>Edit</strong></span></a>
											<% } %>
										<!-- ===end parvez date: 18-10-2021=== -->
										<% } %>
										<button class="btn btn-box-tool" data-widget="collapse">
											<i class="fa fa-plus"></i>
										</button>
										<button class="btn btn-box-tool" data-widget="remove">
											<i class="fa fa-times"></i>
										</button>
									</div>
								</div>
								<!-- /.box-header -->
								<div class="box-body"
									style="padding: 5px; overflow-y: auto; display: none;">
									<table class="table table_no_border autoWidth"
										style="width: 100% !important;">
										<tr>
											<td class="alignRight" style="width: 32% !important;">Contact
												Name:</td>
											<td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("EMERGENCY_NAME"), "-")%></td>
										</tr>
										<tr>
											<td class="alignRight">Contact Number:</td>
											<td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("EMERGENCY_NO"), "-")%></td>
										</tr>
								<!-- ===start parvez date: 30-07-2022=== -->
										<tr>
											<td class="alignRight">Contact Relation:</td>
											<td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("EMERGENCY_RELATION"), "-")%></td>
										</tr>
								<!-- ===end parvez date: 30-07-2022=== -->		
										
										<tr>
											<td class="alignRight">Doctor's Name:</td>
											<td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("DOCTOR_NAME"), "-")%></td>
										</tr>
										<tr>
											<td class="alignRight">Doctor's Contact Number:</td>
											<td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("DOCTOR_NO"), "-")%></td>
										</tr>
									</table>
								</div>
								<!-- /.box-body -->
							</div>
							<div class="box box-default collapsed-box"
								style="margin-top: 10px;">
								<div class="box-header with-border">
									<h3 class="box-title"
										style="font-size: 14px; padding-right: 10px;">Personal
										Statutory Information</h3>
									<div class="box-tools pull-right">
										<% if ((strUserType != null && (strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.RECRUITER) || strUserType.equalsIgnoreCase(IConstants.EMPLOYEE))) 
			                                  || (strBaseUserType != null && (strBaseUserType.equalsIgnoreCase(IConstants.ADMIN) || strBaseUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strBaseUserType.equalsIgnoreCase(IConstants.RECRUITER) || strBaseUserType.equalsIgnoreCase(IConstants.EMPLOYEE)))) {
										%>
									<!-- ===start parvez date: 18-10-2021=== -->
											<% if(uF.parseToBoolean(hmEmpProfile.get("IS_ALIVE")) && hmEmpProfile.get("EMPLOYMENT_END_DATE") == null){ %>
										<a href="AddEmployee.action?operation=U&mode=profile&step=1&empId=<%=strProID%>">&nbsp;&nbsp;<span class="label label-danger" style="font-size: 9px;"> <strong>Edit</strong></span></a>
											<% } %>
									<!-- ===end parvez date: 18-10-2021=== -->
										<% } %>
										<button class="btn btn-box-tool" data-widget="collapse">
											<i class="fa fa-plus"></i>
										</button>
										<button class="btn btn-box-tool" data-widget="remove">
											<i class="fa fa-times"></i>
										</button>
									</div>
								</div>
								<!-- /.box-header -->
								<div class="box-body"
									style="padding: 5px; overflow-y: auto; display: none;">
									<table class="table table_no_border autoWidth"
										style="width: 100% !important;">
										<tr>
											<td class="alignRight" style="width: 32% !important;">Provident
												Fund No:</td>
											<td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("PF_NO"), "-")%></td>
										</tr>
										<tr>
											<td class="alignRight">GPF Acc No:</td>
											<td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("GPF_ACC_NO"), "-")%></td>
										</tr>
										<tr>
											<td class="alignRight">ESIC No:</td>
											<td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("ESIC_NO"), "-")%></td>
										</tr>
										<tr>
											<td class="alignRight">UAN No:</td>
											<td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("UAN_NO"), "-")%></td>
										</tr>
										<tr>
											<td class="alignRight">Aadhaar No:</td>
											<td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("UID_NO"), "-")%></td>
										</tr>
									</table>
								</div>
								<!-- /.box-body -->
							</div>
						</div>
					</div>

					<br />
					<div class="about-item">
						<h3 class="about-header">
							Skills 
							<% if ((strUserType != null && (strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.RECRUITER) || strUserType.equalsIgnoreCase(IConstants.EMPLOYEE))) 
                                  || (strBaseUserType != null && (strBaseUserType.equalsIgnoreCase(IConstants.ADMIN) || strBaseUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strBaseUserType.equalsIgnoreCase(IConstants.RECRUITER) || strBaseUserType.equalsIgnoreCase(IConstants.EMPLOYEE)))) {
							%>
					<!-- ===start parvez date: 18-10-2021=== -->
								<% if(uF.parseToBoolean(hmEmpProfile.get("IS_ALIVE")) && hmEmpProfile.get("EMPLOYMENT_END_DATE") == null){ %>
									&nbsp;&nbsp;<a href="AddEmployee.action?operation=U&mode=profile&step=2&empId=<%=strProID%>"><span class="label label-danger" style="font-size: 9px;"> <strong>Edit</strong></span></a>
								<% } %>
					<!-- ===end parvez date: 18-10-2021=== -->
							<% } %>
						</h3>
						<div class="about-body">
							<table class="table table_no_border autoWidth"
								style="width: 100% !important;">
								<%
                                                if (alSkills != null && alSkills.size() != 0) {
                                                	for (int i = 0; i < alSkills.size(); i++) {
                                                		List<String> alInner = alSkills.get(i);
                                                %>
								<tr>
									<td class="alignRight" style="width: 32% !important;"><strong><%=alInner.get(1)%>:</strong>
									</td>
									<td>
										<div id="star<%=i%>"></div></td>
								</tr>
								<% } } else { %>
								<tr>
									<td class="nodata msg"><span>No skill sets added</span>
									</td>
								</tr>
								<% }  %>
							</table>
						</div>
					</div>
					<br />
					<div class="about-item">
						<h3 class="about-header">
							Education &nbsp;&nbsp;
							<% if ((strUserType != null && (strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.RECRUITER) || strUserType.equalsIgnoreCase(IConstants.EMPLOYEE))) 
                                  || (strBaseUserType != null && (strBaseUserType.equalsIgnoreCase(IConstants.ADMIN) || strBaseUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strBaseUserType.equalsIgnoreCase(IConstants.RECRUITER) || strBaseUserType.equalsIgnoreCase(IConstants.EMPLOYEE)))) {
							%>
					<!-- ===start parvez date: 18-10-2021=== -->
								<% if(uF.parseToBoolean(hmEmpProfile.get("IS_ALIVE")) && hmEmpProfile.get("EMPLOYMENT_END_DATE") == null){ %>
									<a href="AddEmployee.action?operation=U&mode=profile&step=2&empId=<%=strProID%>"><span class="label label-danger" style="font-size: 9px;"> <strong>Edit</strong></span></a>
								<% } %>
					<!-- ===end parvez date: 18-10-2021=== -->
							<% } %>
						</h3>
						<div class="about-body">
							<%
								if (alEducation != null && alEducation.size() != 0) {
									for (int i = 0; i < alEducation.size(); i++) {
	                              		List<String> innerList = alEducation.get(i);
							%>
							<div class="box box-default collapsed-box"
								style="margin-top: 10px;">
								<div class="box-header with-border">
									<h3 class="box-title"
										style="font-size: 14px; padding-right: 10px;"><%=innerList.get(1)%></h3>
									<div class="box-tools pull-right">
										<button class="btn btn-box-tool" data-widget="collapse">
											<i class="fa fa-plus"></i>
										</button>
										<button class="btn btn-box-tool" data-widget="remove">
											<i class="fa fa-times"></i>
										</button>
									</div>
								</div>
								<!-- /.box-header -->
								<div class="box-body"
									style="padding: 5px; overflow-y: auto; display: none;">
									<table class="table table_no_border autoWidth"
										style="width: 100% !important;">
										<tr>
											<td class="alignRight">Duration:</td>
											<td class="textblue" valign="bottom"><%=innerList.get(2) + " Years"%></td>
											<td class="alignRight">Institute Name:</td>
											<td colspan="3" class="textblue" valign="bottom"><%=innerList.get(5)%></td>
										</tr>
										<tr>
											<td class="alignRight">Completion Year:</td>
											<td class="textblue" valign="bottom"><%=innerList.get(3)%></td>
											<td class="alignRight">Grade:</td>
											<td class="textblue" valign="bottom"><%=innerList.get(4)%></td>
											<td class="alignRight">Certificate:</td>
											<td class="textblue" valign="bottom">
												<% if(hmEducationDocs != null && hmEducationDocs.size()>0) {
                                                    	List<String> innrList = hmEducationDocs.get(innerList.get(0));
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
														href="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_DOCUMENT+"/"+IConstants.I_EDUCATION_DOC+"/"+strProID+"/"+ innrList.get(j) %>"
														title="Education Document"><i class="fa fa-file-o"
														aria-hidden="true"></i>
													</a>
													<% } %>
												</div> <% } %> <% } else { %> N/A <% } %>
											</td>
										</tr>
									</table>
								</div>
								<!-- /.box-body -->
							</div>
							<% } %>
							<% } else { %>
							<table style="width: 100% !important;"
								class="table table_no_border autoWidth">
								<tr>
									<td class="nodata msg"><span>No Education
											information added</span>
									</td>
								</tr>
							</table>
							<% } %>
						</div>
					</div>

					<br />
					<div class="about-item">
						<h3 class="about-header">
							Languages &nbsp;&nbsp;
							<% if ((strUserType != null && (strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.RECRUITER) || strUserType.equalsIgnoreCase(IConstants.EMPLOYEE))) 
                                  || (strBaseUserType != null && (strBaseUserType.equalsIgnoreCase(IConstants.ADMIN) || strBaseUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strBaseUserType.equalsIgnoreCase(IConstants.RECRUITER) || strBaseUserType.equalsIgnoreCase(IConstants.EMPLOYEE)))) {
							%>
						<!-- ===start parvez date: 18-10-2021=== -->
								<% if(uF.parseToBoolean(hmEmpProfile.get("IS_ALIVE")) && hmEmpProfile.get("EMPLOYMENT_END_DATE") == null){ %>
									<a href="AddEmployee.action?operation=U&mode=profile&step=2&empId=<%=strProID%>"><span class="label label-danger" style="font-size: 9px;"><strong>Edit</strong></span> </a>
								<% } %>
						<!-- ===end parvez date: 18-10-2021=== -->
							<% } %>
						</h3>
						<div class="about-body">
							<table class="table table_no_border autoWidth"
								style="width: 100% !important;">
								<% if (alLanguages != null && alLanguages.size() != 0) { %>
								<tr class="center">
									<td width="150px"><strong>Language</strong>
									</td>
									<td width="150px"><strong>Read</strong>
									</td>
									<td width="150px"><strong>Write</strong>
									</td>
									<td width="150px"><strong>Speak</strong>
									</td>
									<td width="150px"><strong>Mother Tongue</strong>
									</td>
								</tr>
								<%
                                    for (int i = 0; i < alLanguages.size(); i++) {
                                    List<String> alInner = alLanguages.get(i);
                                    %>
								<tr>
									<td class="textblue" valign="bottom"><strong><%=alInner.get(1)%></strong>
									</td>
									<% if ((alInner.get(2)).equals("1")) { %>
									<td class="textblue yes"></td>
									<% } else { %>
									<td class="textblue no"></td>
									<% } %>
									<% if ((alInner.get(3)).equals("1")) { %>
									<td class="textblue yes"></td>
									<% } else { %>
									<td class="textblue no"></td>
									<% } %>
									<% if ((alInner.get(4)).equals("1")) { %>
									<td class="textblue yes"></td>
									<% } else { %>
									<td class="textblue no"></td>
									<% } %>
									<% if ((alInner.get(5)).equals("1")) { %>
									<td class="textblue yes"></td>
									<% } else { %>
									<td class="textblue no"></td>
									<% } %>
								</tr>
								<% } } else { %>
								<tr>
									<td class="nodata msg"><span>No languages added</span>
									</td>
								</tr>
								<% } %>
							</table>
						</div>
					</div>

					<br />
					
					<% if(!uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_EMPLOYEE_HOBBIES_DISABLE))){ %>
						<div class="about-item">
							<h3 class="about-header">
								Hobbies &nbsp;&nbsp;
								<% if ((strUserType != null && (strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.RECRUITER) || strUserType.equalsIgnoreCase(IConstants.EMPLOYEE))) 
	                                  || (strBaseUserType != null && (strBaseUserType.equalsIgnoreCase(IConstants.ADMIN) || strBaseUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strBaseUserType.equalsIgnoreCase(IConstants.RECRUITER) || strBaseUserType.equalsIgnoreCase(IConstants.EMPLOYEE)))) {
								%>
									<% if(uF.parseToBoolean(hmEmpProfile.get("IS_ALIVE")) && hmEmpProfile.get("EMPLOYMENT_END_DATE") == null){ %>
										<a href="AddEmployee.action?operation=U&mode=profile&step=2&empId=<%=strProID%>"><span class="label label-danger" style="font-size: 9px;"><strong>Edit</strong></span> </a>
									<% } %>
								<% } %>
							</h3>
							<div class="about-body">
								<table class="table table_no_border autoWidth"
									style="width: 100% !important;">
									<% if (alHobbies != null && alHobbies.size() != 0) { %>
									<tr>
										<% for (int i = 0; i < alHobbies.size(); i++) {
	                                           List<String> alInner = alHobbies.get(i);
	                                           %>
										<td class="textblue" valign="bottom"><strong><%=i < alHobbies.size() - 1 ? alInner.get(1) + " ," : alInner.get(1)%></strong>
										</td>
										<% } %>
									</tr>
									<% } else { %>
									<tr>
										<td class="nodata msg"><span>No hobbies added</span>
										</td>
									</tr>
									<% } %>
								</table>
							</div>
						</div>
					<% } %>

					<br />
					<div class="about-item">
						<h3 class="about-header">
							Previous Employment
							<% if ((strUserType != null && (strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.RECRUITER) || strUserType.equalsIgnoreCase(IConstants.EMPLOYEE))) 
                                  || (strBaseUserType != null && (strBaseUserType.equalsIgnoreCase(IConstants.ADMIN) || strBaseUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strBaseUserType.equalsIgnoreCase(IConstants.RECRUITER) || strBaseUserType.equalsIgnoreCase(IConstants.EMPLOYEE)))) {
							%>
							<!-- ===start parvez date: 18-10-2021=== -->
								<% if(uF.parseToBoolean(hmEmpProfile.get("IS_ALIVE")) && hmEmpProfile.get("EMPLOYMENT_END_DATE") == null){ %>
									&nbsp;&nbsp;<a href="AddEmployee.action?operation=U&mode=profile&step=3&empId=<%=strProID%>"><span class="label label-danger" style="font-size: 9px;"> <strong>Edit</strong></span></a>
								<% } %>
							<!-- ===end parvez date: 18-10-2021=== -->
							<% } %>
						</h3>
						<div class="about-body">
							<% for(int i=0; alPrevEmployment != null && !alPrevEmployment.isEmpty() && i<alPrevEmployment.size(); i++) { 
                                        List<String> innerList = alPrevEmployment.get(i); 
                                        %>
							<div class="box box-default collapsed-box"
								style="margin-top: 10px;">
								<div class="box-header with-border">
									<h3 class="box-title"
										style="font-size: 14px; padding-right: 10px;"><%=uF.showData(innerList.get(1), "-") %></h3>
									<div class="box-tools pull-right">
										<button class="btn btn-box-tool" data-widget="collapse">
											<i class="fa fa-plus"></i>
										</button>
										<button class="btn btn-box-tool" data-widget="remove">
											<i class="fa fa-times"></i>
										</button>
									</div>
								</div>
								<!-- /.box-header -->
								<div class="box-body"
									style="padding: 5px; overflow-y: auto; display: none;">
									<table class="table table_no_border autoWidth"
										style="width: 100% !important;">
										<tr>
											<td class="alignRight" style="width: 32% !important;">Location:</td>
											<td class="textblue" valign="bottom"><%=uF.showData(innerList.get(2), "-")%></td>
										</tr>
										<tr>
											<td class="alignRight">City:</td>
											<td class="textblue" valign="bottom"><%=uF.showData(innerList.get(3), "-")%></td>
										</tr>
										<tr>
											<td class="alignRight">State:</td>
											<td class="textblue" valign="bottom"><%=uF.showData(innerList.get(4), "-")%></td>
										</tr>
										<tr>
											<td class="alignRight">Country:</td>
											<td class="textblue" valign="bottom"><%=uF.showData(innerList.get(5), "-")%></td>
										</tr>
										<tr>
											<td class="alignRight">Phone Number:</td>
											<td class="textblue" valign="bottom"><%=uF.showData(innerList.get(6), "-")%></td>
										</tr>
										<tr>
											<td class="alignRight">Reporting To:</td>
											<td class="textblue" valign="bottom"><%=uF.showData(innerList.get(7), "-")%></td>
										</tr>
										<tr>
											<td class="alignRight">Reporting Manager Ph. No.:</td>
											<td class="textblue" valign="bottom"><%=uF.showData(innerList.get(13), "-")%></td>
										</tr>
										<tr>
											<td class="alignRight">HR Manager:</td>
											<td class="textblue" valign="bottom"><%=uF.showData(innerList.get(14), "-")%></td>
										</tr>
										<tr>
											<td class="alignRight">HR Manager Ph. No.:</td>
											<td class="textblue" valign="bottom"><%=uF.showData(innerList.get(15), "-")%></td>
										</tr>
										<tr>
											<td class="alignRight">From:</td>
											<td class="textblue" valign="bottom"><%=uF.showData(innerList.get(8), "-")%></td>
										</tr>
										<tr>
											<td class="alignRight">To:</td>
											<td class="textblue" valign="bottom"><%=uF.showData(innerList.get(9), "-")%></td>
										</tr>
										<tr>
											<td class="alignRight">Designation:</td>
											<td class="textblue" valign="bottom"><%=uF.showData(innerList.get(10), "-")%></td>
										</tr>
										<tr>
											<td class="alignRight">Responsibility:</td>
											<td class="textblue" valign="bottom"><%=uF.showData(innerList.get(11), "-")%></td>
										</tr>
										<tr>
											<td class="alignRight">Skills:</td>
											<td class="textblue" valign="bottom"><%=uF.showData(innerList.get(12), "-")%></td>
										</tr>
								<!-- ===start parvez date: 08-08-2022=== -->		
										<tr>
											<td class="alignRight">ESIC No:</td>
											<td class="textblue" valign="bottom"><%=uF.showData(innerList.get(16), "-")%></td>
										</tr>
										<tr>
											<td class="alignRight">UAN No:</td>
											<td class="textblue" valign="bottom"><%=uF.showData(innerList.get(17), "-")%></td>
										</tr>
								<!-- ===end parvez date: 08-08-2022=== -->		
										<tr>
											<td class="alignRight">TDS information:</td>
											<td class="textblue" valign="bottom">&nbsp;</td>
										</tr>
										<tr>
											<td class="alignRight">&nbsp;</td>
											<td class="textblue" valign="bottom">Financial Year: <%=uF.showData(hmEmpPrevEarnDeduct.get("PREV_COMP_FINANCIAL_YEAR"), "-") %></td>
										</tr>
										<tr>
											<td class="alignRight">&nbsp;</td>
											<td class="textblue" valign="bottom">Gross Amount: <%=uF.showData(uF.formatIntoOneDecimal(uF.parseToDouble(hmEmpPrevEarnDeduct.get("PREV_COMP_GROSS_AMOUNT"))), "-") %></td>
										</tr>
										<tr>
											<td class="alignRight">&nbsp;</td>
											<td class="textblue" valign="bottom">TDS Amount: <%=uF.showData(uF.formatIntoOneDecimal(uF.parseToDouble(hmEmpPrevEarnDeduct.get("PREV_COMP_TDS_AMOUNT"))), "-") %></td>
										</tr>
								<!-- ===start parvez date: 01-04-2022=== -->
										<tr>
											<td class="alignRight">&nbsp;</td>
											<td class="textblue" valign="bottom">PAN Number: <%=uF.showData(hmEmpPrevEarnDeduct.get("PREV_COMP_PAN_NUMBER"), "-") %></td>
										</tr>
										<tr>
											<td class="alignRight">&nbsp;</td>
											<td class="textblue" valign="bottom">TDS Number: <%=uF.showData(hmEmpPrevEarnDeduct.get("PREV_COMP_TAN_NUMBER"), "-") %></td>
										</tr>
								<!-- ===end parvez date: 01-04-2022=== -->		
										<tr>
											<td class="alignRight">&nbsp;</td>
											<td class="textblue" valign="bottom">Please upload
												relevant document (form 16): &nbsp;&nbsp; <% if(hmEmpPrevEarnDeduct.get("PREV_COMP_DOC_NAME") != null && !hmEmpPrevEarnDeduct.get("PREV_COMP_DOC_NAME").equals("")) { %>
												<%if(docRetriveLocation == null) { %> <a
												href="<%=request.getContextPath()+ IConstants.DOCUMENT_LOCATION + hmEmpPrevEarnDeduct.get("PREV_COMP_DOC_NAME")  %>"
												title="Form 16"><i class="fa fa-file-o"
													aria-hidden="true"></i>
											</a> <% } else { %> <a
												href="<%=docRetriveLocation +hmEmpPrevEarnDeduct.get("PREV_COMP_DOC_NAME") %>"
												title="Form 16"><i class="fa fa-file-o"
													aria-hidden="true"></i>
											</a> <% } %> <% } else { %> - <% } %>
											</td>
										</tr>
									</table>
								</div>
								<!-- /.box-body -->
							</div>
							<% } %>
							<% if (alPrevEmployment == null || alPrevEmployment.isEmpty()) { %>
							<div class="nodata msg" style="width: 96%">
								<span>No previous employment</span>
							</div>
							<% } %>
						</div>
					</div>

					<br />
					<div class="about-item">
						<h3 class="about-header"> Employee References
							<% if ((strUserType != null && (strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.RECRUITER) || strUserType.equalsIgnoreCase(IConstants.EMPLOYEE))) 
                                  || (strBaseUserType != null && (strBaseUserType.equalsIgnoreCase(IConstants.ADMIN) || strBaseUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strBaseUserType.equalsIgnoreCase(IConstants.RECRUITER) || strBaseUserType.equalsIgnoreCase(IConstants.EMPLOYEE)))) {
							%>
							<!-- ===start parvez date: 18-10-2021=== -->
								<% if(uF.parseToBoolean(hmEmpProfile.get("IS_ALIVE")) && hmEmpProfile.get("EMPLOYMENT_END_DATE") == null){ %>
										&nbsp;&nbsp;<a href="AddEmployee.action?operation=U&mode=profile&step=4&empId=<%=strProID%>"><span class="label label-danger" style="font-size: 9px;"> <strong>Edit</strong></span></a>
								<% } %>
							<!-- ===end parvez date: 18-10-2021=== -->
							<% } %>
						</h3>
						<div class="about-body">
							<%
                                if (empRefList != null && empRefList.size()>0) {
                                	for (int i = 0; i < empRefList.size(); i++) {
                                		Map<String,String> hmInner = empRefList.get(i);
                                %>
                                <ul>
								<li><strong>Reference <%=(i + 1)%></strong>
								</li>
								<li>
								<table class="table table_no_border autoWidth" style="width: 100% !important;">
									<tr>
										<td class="alignRight" style="width: 32% !important;">Name: </td>
										<td class="textblue" valign="bottom"><%=hmInner.get("REF_NAME") %></td>
									</tr>
									<tr>
										<td class="alignRight" style="width: 32% !important;">Company: </td>
										<td class="textblue" valign="bottom"><%=hmInner.get("REF_COMPANY") %></td>
									</tr>
									<tr>
										<td class="alignRight" style="width: 32% !important;">Designation: </td>
										<td class="textblue" valign="bottom"><%=hmInner.get("REF_DESIGNATION") %></td>
									</tr>
									<tr>
										<td class="alignRight" style="width: 32% !important;">Contact No.: </td>
										<td class="textblue" valign="bottom"><%=hmInner.get("REF_CONTACT_NO") %></td>
									</tr>
									<tr>
										<td class="alignRight" style="width: 32% !important;">Email Id: </td>
										<td class="textblue" valign="bottom"><%=hmInner.get("REF_EMAIL") %></td>
									</tr>
								</table>
								<% } } else { %>
								<table class="table table_no_border autoWidth" style="width: 100% !important;">
									<tr>
										<td colspan="2" class="nodata msg"><span>No Employee References</span></td>
									</tr>
								</table>
								<% } %>
							</li>
							</ul>
							
						</div>
					</div>

					<br />
					
			<!-- ===start parvez date: 06-09-2022=== -->		
					<% if(!uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_DISABLE_EMPLOYEE_FAMILY_INFORMATION))){ %>
						<div class="about-item">
							<h3 class="about-header">
								Family Information &nbsp;&nbsp;
								<% if ((strUserType != null && (strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.RECRUITER) || strUserType.equalsIgnoreCase(IConstants.EMPLOYEE))) 
	                                  || (strBaseUserType != null && (strBaseUserType.equalsIgnoreCase(IConstants.ADMIN) || strBaseUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strBaseUserType.equalsIgnoreCase(IConstants.RECRUITER) || strBaseUserType.equalsIgnoreCase(IConstants.EMPLOYEE)))) {
								%>
								<!-- ===start parvez date: 18-10-2021=== -->
									<% if(uF.parseToBoolean(hmEmpProfile.get("IS_ALIVE")) && hmEmpProfile.get("EMPLOYMENT_END_DATE") == null){ %>
										<a href="AddEmployee.action?operation=U&mode=profile&step=5&empId=<%=strProID%>"><span class="label label-danger" style="font-size: 9px;"> <strong>Edit</strong></span></a>
									<% } %>
								<!-- ===end parvez date: 18-10-2021=== -->
								<% } %>
							</h3>
							<div class="about-body">
								<%
	                                                if (alFamilyMembers != null && alFamilyMembers.size() != 0) {
	                                                
	                                                	for (int i = 0; i < alFamilyMembers.size(); i++) {
	                                                		List<String> innerList = alFamilyMembers.get(i);
	                                                
	                                                		if (innerList.get(1).length() != 0) {
	                                                			%>
								<div class="box box-default collapsed-box"
									style="margin-top: 10px;">
	
	
									<%if (innerList.get(8).equals("FATHER")) {
	                                                %>
									<div class="box-header with-border">
										<h3 class="box-title"
											style="font-size: 14px; padding-right: 10px;">Father's
											Info</h3>
										<div class="box-tools pull-right">
											<button class="btn btn-box-tool" data-widget="collapse">
												<i class="fa fa-plus"></i>
											</button>
											<button class="btn btn-box-tool" data-widget="remove">
												<i class="fa fa-times"></i>
											</button>
										</div>
									</div>
									<% } else if (innerList.get(8).equals("MOTHER")) { %>
									<div class="box-header with-border">
										<h3 class="box-title"
											style="font-size: 14px; padding-right: 10px;">Mother's
											Info</h3>
										<div class="box-tools pull-right">
											<button class="btn btn-box-tool" data-widget="collapse">
												<i class="fa fa-plus"></i>
											</button>
											<button class="btn btn-box-tool" data-widget="remove">
												<i class="fa fa-times"></i>
											</button>
										</div>
									</div>
									<% } else if (innerList.get(8).equals("SPOUSE")) { %>
									<div class="box-header with-border">
										<h3 class="box-title"
											style="font-size: 14px; padding-right: 10px;">Spouse's
											Info</h3>
										<div class="box-tools pull-right">
											<button class="btn btn-box-tool" data-widget="collapse">
												<i class="fa fa-plus"></i>
											</button>
											<button class="btn btn-box-tool" data-widget="remove">
												<i class="fa fa-times"></i>
											</button>
										</div>
									</div>
									<% } else if (innerList.get(8).equals("SIBLING")) { %>
									<div class="box-header with-border">
										<h3 class="box-title"
											style="font-size: 14px; padding-right: 10px;">Sibling's
											Info</h3>
										<div class="box-tools pull-right">
											<button class="btn btn-box-tool" data-widget="collapse">
												<i class="fa fa-plus"></i>
											</button>
											<button class="btn btn-box-tool" data-widget="remove">
												<i class="fa fa-times"></i>
											</button>
										</div>
									</div>
									<% } else if (innerList.get(8).equals("CHILD")) { %>
									<div class="box-header with-border">
										<h3 class="box-title"
											style="font-size: 14px; padding-right: 10px;">Children's
											Info</h3>
										<div class="box-tools pull-right">
											<button class="btn btn-box-tool" data-widget="collapse">
												<i class="fa fa-plus"></i>
											</button>
											<button class="btn btn-box-tool" data-widget="remove">
												<i class="fa fa-times"></i>
											</button>
										</div>
									</div>
									<% } %>
									<div class="box-body"
										style="padding: 5px; overflow-y: auto; display: none;">
										<table class="table table_no_border autoWidth"
											style="width: 100% !important;">
											<tr>
												<td class="alignRight" style="width: 32% !important;">Name:
												</td>
												<td class="textblue" valign="bottom"><%=innerList.get(1)%></td>
											</tr>
											<tr>
												<td class="alignRight">Date Of Birth:</td>
												<td class="textblue" valign="bottom"><%=innerList.get(2)%></td>
											</tr>
											<tr>
												<td class="alignRight">Education:</td>
												<td class="textblue" valign="bottom"><%=innerList.get(3)%></td>
											</tr>
											<tr>
												<td class="alignRight">Occupation:</td>
												<td class="textblue" valign="bottom"><%=innerList.get(4)%></td>
											</tr>
											<tr>
												<td class="alignRight">Contact No:</td>
												<td class="textblue" valign="bottom"><%=innerList.get(5)%></td>
											</tr>
											<tr>
												<td class="alignRight">MRD No.:</td>
												<td class="textblue" valign="bottom"><%=innerList.get(10)%></td>
											</tr>
											<tr>
												<td class="alignRight">Email Id:</td>
												<td class="textblue" valign="bottom"><%=innerList.get(6)%></td>
											</tr>
											<% if (innerList.get(8).equals("SPOUSE") || innerList.get(8).equals("CHILD") || innerList.get(8).equals("SIBLING")) { %>
											<tr>
												<td class="alignRight">Gender:</td>
												<td class="textblue" valign="bottom"><%=innerList.get(7)%></td>
											</tr>
											<% } %>
											<% if (innerList.get(8).equals("CHILD") || innerList.get(8).equals("SIBLING")) { %>
											<tr>
												<td class="alignRight">Marital Status:</td>
												<td class="textblue" valign="bottom"><%=innerList.get(9)%></td>
											</tr>
											<% } %>
										</table>
									</div>
								</div>
								<% } }
	                                                } else {
	                                                %>
								<table class="table table_no_border autoWidth">
									<tr>
										<td class="nodata msg"><span>No Family members added</span>
										</td>
									</tr>
								</table>
								<% } %>
							</div>
						</div>
	
						<br />
					<% } %>
					

			<% if(!uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_DISABLE_MEDICAL_DETAILS))){ %>
					<div class="about-item">
						<%
                                List<List<String>> alMedicalDetails = (List<List<String>>) request.getAttribute("alMedicalDetails");
                                Map<String, String> medicalQuest = (Map<String, String>) request.getAttribute("medicalQuest");
                                %>
						<h3 class="about-header">
							Medical Details
							<% if (strUserType != null && (strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.RECRUITER)) 
                                  && strBaseUserType != null && (strBaseUserType.equalsIgnoreCase(IConstants.ADMIN) || strBaseUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strBaseUserType.equalsIgnoreCase(IConstants.RECRUITER))) {
							%>
								<% if(uF.parseToBoolean(hmEmpProfile.get("IS_ALIVE")) && hmEmpProfile.get("EMPLOYMENT_END_DATE") == null){ %>
								&nbsp;&nbsp;<a href="AddEmployee.action?operation=U&mode=profile&step=6&empId=<%=strProID%>"><span class="label label-danger" style="font-size: 9px;"> <strong>Edit</strong></span></a>
								<%} %>
							<%} %>
						</h3>
						<div class="about-body">
							<table class="table table_no_border autoWidth"
								style="width: 100% !important;">
								<% if (alMedicalDetails != null && alMedicalDetails.size() != 0) { %>
								<% for (int i = 0,j=0; i < alMedicalDetails.size(); i++) {
                                            List<String> alInner = alMedicalDetails.get(i);
                                            if(medicalQuest.get(alInner.get(0)) != null && !medicalQuest.get(alInner.get(0)).equals("")) {	
                                            %>
								<tr>
									<td style="width: 70%">
										<div
											style="float: left; width: 10px; font-weight: bold; padding-right: 10px;"><%=++j%>.&nbsp;&nbsp;&nbsp;
										</div>
										<div style="float: left; width: 90%;"><%=medicalQuest.get(alInner.get(0)) %></div>
									</td>
									<% if (uF.parseToBoolean(alInner.get(1))) { %>
									<td class="textblue yes" style="width: 10%"></td>
									<% } else { %>
									<td class="textblue no" style="width: 10%"></td>
									<% } %>
									<% if (alInner.get(3) != null) { %>
									<td style="width: 20%" class="alignRight">
										<%-- <a href="<%=request.getContextPath()+"/userDocuments/"+alInner.get(3)%>" >Download</a> --%>
										<%if(docRetriveLocation == null) { %> <a target="blank"
										style="float: left; padding-top: 5px;"
										href="<%=request.getContextPath()+ IConstants.DOCUMENT_LOCATION + alInner.get(3)  %>"
										title="Medical Document"><img src="images1/payslip.png">
									</a> <%} else { %> <a target="blank"
										style="float: left; padding-top: 5px;"
										href="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_DOCUMENT+"/"+IConstants.I_MEDICAL+"/"+strProID+"/"+ alInner.get(3)  %>"
										title="Medical Document"><img src="images1/payslip.png">
									</a> <%} %>
									</td>
									<% } %>
								</tr>
								<% if (uF.parseToBoolean(alInner.get(1))) { %>
								<tr>
									<td class="textblue" valign="bottom"><strong><%=alInner.get(2)%></strong>
									</td>
								</tr>
								<% } %>
								<% } } %>
								<% } else { %>
								<tr>
									<td class="nodata msg"><span>No Medical Details added</span>
									</td>
								</tr>
								<% } %>
								
							</table>
						</div>
					</div>

					<br />
			<% } %>	
	<!-- ===end parvez date: 06-09-2022=== -->		
					
					<div class="about-item">
						<h3 class="about-header">
							Supporting Documents
							<% if ((strUserType != null && (strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.RECRUITER)) 
                                && strBaseUserType != null && (strBaseUserType.equalsIgnoreCase(IConstants.ADMIN) || strBaseUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strBaseUserType.equalsIgnoreCase(IConstants.RECRUITER)))
								|| (hmFeatureUserTypeId.get(IConstants.F_EDIT_EMPLOYEE_SUPPORTING_DOCUMENT)!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_EDIT_EMPLOYEE_SUPPORTING_DOCUMENT)) && hmFeatureUserTypeId.get(IConstants.F_EDIT_EMPLOYEE_SUPPORTING_DOCUMENT).contains(USERTYPEID))) {
							%>
					<!-- ===start parvez date: 18-10-2021=== -->
								<% if(uF.parseToBoolean(hmEmpProfile.get("IS_ALIVE")) && hmEmpProfile.get("EMPLOYMENT_END_DATE") == null){ %>
								&nbsp;&nbsp;<a href="AddEmployee.action?operation=U&mode=profile&step=7&empId=<%=strProID%>"><span class="label label-danger" style="font-size: 9px;"> <strong>Edit</strong></span></a>
								<% } %>
					<!-- ===end parvez date: 18-10-2021=== -->
							<% } %>
						</h3>
						<div class="about-body">
							<table class="table table_no_border autoWidth"
								style="width: 100% !important;">
								<%
                                     if (alDocuments != null && alDocuments.size() != 0) {
                                     	for (int i = 0; i < alDocuments.size(); i++) {
                                     %>
								<tr>
									<td class="alignRight" style="width: 32% !important;"><%=((ArrayList) alDocuments.get(i)).get(1)%> </td>
									<td class="textblue" valign="bottom">-</td>
									<td class="alignRight">
										<% if (((ArrayList)alDocuments.get(i)).get(4) != null) { %> <%if(docRetriveLocation == null) { %>
										<a href="<%=request.getContextPath()+ IConstants.DOCUMENT_LOCATION + ((ArrayList)alDocuments.get(i)).get(4)  %>"
										title="Reference Document"><i class="fa fa-file-o" aria-hidden="true"></i>
									</a> <%} else { %> <a href="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_DOCUMENT+"/"+IConstants.I_ATTACHMENT+"/"+strProID+"/"+ ((ArrayList)alDocuments.get(i)).get(4)  %>"
										title="Reference Document"><i class="fa fa-file-o" aria-hidden="true"></i>
									</a> <%} %> <% } else { %> N/A <%} %>
									</td>
									<!-- <td class="textblue" valign="bottom">-</td> -->
								</tr>
								<% } } else { %>
								<tr>
									<td colspan="4" class="nodata msg"><span>No Documents attached</span></td>
								</tr>
								<% } %>
							</table>
						</div>
					</div>
					
					<!-- <br /> -->
					<%-- <div class="about-item">
						<h3 class="about-header">Employee References
							<% if (strUserType != null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) && !strUserType.equalsIgnoreCase(IConstants.ARTICLE) 
                                && !strUserType.equalsIgnoreCase(IConstants.CONSULTANT) && !strUserType.equalsIgnoreCase(IConstants.MANAGER)) { %>
							&nbsp;&nbsp;<a href="AddEmployee.action?operation=U&mode=profile&step=4&empId=<%=strProID%>"><span class="label label-danger" style="font-size: 9px;"> <strong>Edit</strong></span></a>
							<% } %>
						</h3>
						<div class="about-body">
							
								<%
                                if (empRefList != null && empRefList.size()>0) {
                                	for (int i = 0; i < empRefList.size(); i++) {
                                		Map<String,String> hmInner = empRefList.get(i);
                                %>
                                <h5 class="about-header">Reference <%=(i+1) %></h5>
								<table class="table table_no_border autoWidth" style="width: 100% !important;">
									<tr>
										<td class="alignRight" style="width: 32% !important;">Name: </td>
										<td class="textblue" valign="bottom"><%=hmInner.get("REF_NAME") %></td>
									</tr>
									<tr>
										<td class="alignRight" style="width: 32% !important;">Company: </td>
										<td class="textblue" valign="bottom"><%=hmInner.get("REF_COMPANY") %></td>
									</tr>
									<tr>
										<td class="alignRight" style="width: 32% !important;">Designation: </td>
										<td class="textblue" valign="bottom"><%=hmInner.get("REF_DESIGNATION") %></td>
									</tr>
									<tr>
										<td class="alignRight" style="width: 32% !important;">Contact No.: </td>
										<td class="textblue" valign="bottom"><%=hmInner.get("REF_CONTACT_NO") %></td>
									</tr>
									<tr>
										<td class="alignRight" style="width: 32% !important;">Email Id: </td>
										<td class="textblue" valign="bottom"><%=hmInner.get("REF_EMAIL") %></td>
									</tr>
								</table>
								<% } } else { %>
								<table class="table table_no_border autoWidth" style="width: 100% !important;">
									<tr>
										<td colspan="2" class="nodata msg"><span>No Employee References</span></td>
									</tr>
								</table>
								<% } %>
							
						</div>
					</div> --%>

				</div>
				<!-- /.tab-pane -->
				<div class="tab-pane" id="timeline">
					<!-- The timeline -->
					<div style="margin-top: 20px;"></div>
					<% if (strUserType != null && (strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.RECRUITER)) 
                    	&& strBaseUserType != null && (strBaseUserType.equalsIgnoreCase(IConstants.ADMIN) || strBaseUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strBaseUserType.equalsIgnoreCase(IConstants.RECRUITER))) {
					%>
					<div>
						<a href="EmployeeActivity.action?dataType=A&strEmpId=<%=strProID %>" title="Activity" style="float: right;"><i class="fa fa-plus-circle" aria-hidden="true"></i>Add Activity</a>
					</div>
					<%} %>
					<div class="clr"></div>
					<%if (alActivityDetails !=null && alActivityDetails.size()>0) {
                            String strDateTmp = "";%>
					<ul class="timeline timeline-inverse">
						<!-- timeline time label -->
						<%
                                for (int i = 0; i < alActivityDetails.size(); i++) {
                                	List<String> innerList = alActivityDetails.get(i);
                                	//System.out.println("MP.jsp/2910---innerList="+innerList);
                                	int nDoc = uF.parseToInt(innerList.get(11));
                                	String strColor = "#ececec";
                                	if(nDoc > 0){
                                		strColor = "#FFA500";
                                	}
                                	String strMsg = innerList.get(5)+" on "+innerList.get(7)+((innerList.get(6) != null && innerList.get(6).length() != 0) ? " for " + innerList.get(6) : "");
                                	if(innerList.get(12)!=null && innerList.get(12).equals(IConstants.ACTIVITY_EXTEND_PROBATION_ID)){
                                		strMsg = innerList.get(5)+" on "+innerList.get(7)+((innerList.get(13) != null && innerList.get(13).length() != 0) ? " for " + innerList.get(13)+" days" : "");
                                	}
                                	String strDate = uF.showData(innerList.get(7),"");%>
						<% if(strDate != null && strDate.equalsIgnoreCase(strDateTmp)){ %>
						<li><i class="fa fa-envelope bg-blue"></i>
							<div class="timeline-item">
					<!-- ===start parvez date: 29-06-2022=== -->		
								<h3 class="timeline-header"><%=innerList.get(5)%>
									<%-- <%=(nDoc == 0) ? "" : "<a href=\"DownloadDocument.action?doc_id=" + nDoc+ "\"><i class=\"fa fa-file-pdf-o\" aria-hidden=\"true\" style=\"margin-left:5px;height:16px;float:right;\"  ></i></a>"%></h3> --%>
								<% if(innerList.get(14)!=null && !innerList.get(14).isEmpty() && !innerList.get(14).equals("")){ %>
									<%if(docRetriveLocation == null) { %>
										<a href="<%=request.getContextPath()+ IConstants.DOCUMENT_LOCATION + innerList.get(14)  %>"><i class="fa fa-file-pdf-o" aria-hidden="true" style="margin-left:5px;height:16px;float:right;"  ></i></a>
									<% } else { %>
										<a href="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_DOCUMENT+"/"+IConstants.I_OFFER_LETTER+"/"+strProID+"/"+ innerList.get(14) %>">
											<i class="fa fa-file-pdf-o" aria-hidden="true" style="margin-left:5px;height:16px;float:right;"  ></i>
										</a>
									<% } %>
								<% } else { %>
										<%=(nDoc == 0) ? "" : "<a href=\"DownloadDocument.action?doc_id=" + nDoc+ "\"><i class=\"fa fa-file-pdf-o\" aria-hidden=\"true\" style=\"margin-left:5px;height:16px;float:right;\"  ></i></a>"%></h3>
								<% } %>
					<!-- ===end parvez date: 29-06-2022=== -->				
								<div class="timeline-body">
									<%=strMsg %>
								</div>
							</div></li>
						<%}else{ %>
						<li class="time-label"><span class="bg-red"> <%=strDate %>
						</span></li>
						<!-- /.timeline-label -->
						<!-- timeline item -->
						<li><i class="fa fa-envelope bg-blue"></i>
							<div class="timeline-item">
						<!-- ===start parvez date: 29-06-2022=== -->	
								<h3 class="timeline-header"><%=innerList.get(5)%>
									<%-- <%=(nDoc == 0) ? "" : "<a href=\"DownloadDocument.action?doc_id=" + nDoc+ "\"><i class=\"fa fa-file-pdf-o\" aria-hidden=\"true\" style=\"margin-left:5px;height:16px;float:right;\"  ></i></a>"%></h3> --%>
									<% if(innerList.get(14)!=null && !innerList.get(14).isEmpty() && !innerList.get(14).equals("")){ %>
										<%if(docRetriveLocation == null) { %>
											<a href="<%=request.getContextPath()+ IConstants.DOCUMENT_LOCATION + innerList.get(14)  %>"><i class="fa fa-file-pdf-o" aria-hidden="true" style="margin-left:5px;height:16px;float:right;"  ></i></a>
										<% } else { %>
											<a href="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_DOCUMENT+"/"+IConstants.I_OFFER_LETTER+"/"+strProID+"/"+ innerList.get(14) %>">
												<i class="fa fa-file-pdf-o" aria-hidden="true" style="margin-left:5px;height:16px;float:right;"  ></i>
											</a>
										<% } %>
									<% } else { %>
											<%=(nDoc == 0) ? "" : "<a href=\"DownloadDocument.action?doc_id=" + nDoc+ "\"><i class=\"fa fa-file-pdf-o\" aria-hidden=\"true\" style=\"margin-left:5px;height:16px;float:right;\"  ></i></a>"%></h3>
									<% } %>
					<!-- ===end parvez date: 29-06-2022=== -->				
								<div class="timeline-body">
									<%=strMsg %>
								</div>
							</div></li>
						<%} 
                                strDateTmp = strDate; 
                                } %>
						<!-- /.timeline-label -->
						<!-- timeline item -->
						<li>
							<div class="timeline-item">
								<h3 class="timeline-header bg-green">Current Employment
									Details</h3>
								<div class="timeline-body">
									<p>
										Joining Date:
										<%=uF.showData(hmEmpProfile.get("JOINING_DATE"), "-")%></p>
									<p>
										Designation:
										<%=uF.showData(hmEmpProfile.get("DESIGNATION_NAME"), "-")%></p>
								</div>
							</div></li>
							
					<!-- ===start parvez date: 06-08-2022=== -->		
						<% if(!uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_TIMELINE_PREVIOUS_EMPLOYMENT_DISABLE))){ %>
								<%if (alPrevEmployment !=null && alPrevEmployment.size()>0) {
	                                for (int i = 0; i < alPrevEmployment.size(); i++) {
	                                	List<String> innerList = alPrevEmployment.get(i);
	                                	String strColor = "#ececec";
	                                	String strMsg = "From "+innerList.get(8)+" To "+innerList.get(9)+" "+((innerList.get(10) != null && innerList.get(10).length() != 0) ? " with " + innerList.get(10) + " designation." : "");
	                                	String strMsg1 = (innerList.get(12) != null && innerList.get(12).length() != 0) ? "Skills: " + innerList.get(12) : "";
	                                %>
							<li><i class="fa fa-envelope bg-blue"></i>
								<div class="timeline-item">
									<h3 class="timeline-header"><%=innerList.get(1)%></h3>
									<div class="timeline-body">
										<%=strMsg %><br /><%=strMsg1 %>
									</div>
								</div></li>
							<%} %>
							<!-- cd-timeline -->
							<%} else { %>
							<li><i class="fa fa-envelope bg-blue"></i>
								<div class="timeline-item">
									<h3 class="timeline-header">No previous employment</h3>
									<div class="timeline-body">Click on the edit to add
										previous employment</div>
								</div></li>
							<%} %>
						<% } %>	
				<!-- ===end parvez date: 06-08-2022=== -->		
							
						<%-- <%if (alPrevEmployment !=null && alPrevEmployment.size()>0) {
                                for (int i = 0; i < alPrevEmployment.size(); i++) {
                                	List<String> innerList = alPrevEmployment.get(i);
                                	String strColor = "#ececec";
                                	String strMsg = "From "+innerList.get(8)+" To "+innerList.get(9)+" "+((innerList.get(10) != null && innerList.get(10).length() != 0) ? " with " + innerList.get(10) + " designation." : "");
                                	String strMsg1 = (innerList.get(12) != null && innerList.get(12).length() != 0) ? "Skills: " + innerList.get(12) : "";
                                %>
						<li><i class="fa fa-envelope bg-blue"></i>
							<div class="timeline-item">
								<h3 class="timeline-header"><%=innerList.get(1)%></h3>
								<div class="timeline-body">
									<%=strMsg %><br /><%=strMsg1 %>
								</div>
							</div></li>
						<%} %>
						<!-- cd-timeline -->
						<%} else { %>
						<li><i class="fa fa-envelope bg-blue"></i>
							<div class="timeline-item">
								<h3 class="timeline-header">No previous employment</h3>
								<div class="timeline-body">Click on the edit to add
									previous employment</div>
							</div></li>
						<%} %> --%>
						<li><i class="fa fa-clock-o bg-gray"></i></li>
					</ul>
					<%} else { %>
					<div class="msg nodata">
						<span>No data available.</span>
					</div>
					<% } %>
				</div>
				<!-- /.tab-pane -->
				<div <%if(fromPage != null && fromPage.equals("MyDashboard")) { %>
					class="tab-pane active" <% } else { %> class="tab-pane" <% } %>
					id="position">
					<s:action name="OrganisationalChart" executeResult="true">
						<s:param name="strEmpId"><%=(String)hmEmpProfile.get("EMP_ID")%></s:param>
						<s:param name="orgId"><%=(String)hmEmpProfile.get("ORG_ID")%></s:param>
						<s:param name="fromPage">MP</s:param>
					</s:action>
				</div>
			</div>
			<!-- /.tab-content -->
		</div>
		<!-- /.nav-tabs-custom -->
	</div>
	<!-- /.col -->
</div>
</section>
<div class="modal" id="modalInfo" role="dialog">
	<div class="modal-dialog">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">&times;</button>
				<h4 class="modal-title">Candidate Information</h4>
			</div>
			<div class="modal-body"
				style="height: 400px; overflow-y: auto; padding-left: 25px;">
			</div>
			<div class="modal-footer">
				<button type="button" id="closeButton" class="btn btn-default"
					data-dismiss="modal">Close</button>
			</div>
		</div>
	</div>
</div>
<div class="modal" id="editProfilePhoto" role="dialog">
	<div class="modal-dialog modal-dialog1">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h4 class="modal-title modal-title1">Edit Profile Photo</h4>
			</div>
			<div class="modal-body" id="modal-body1"
				style="height: 400px; overflow-y: auto; padding-left: 25px;">
				<div id="uploadProfilePhotoDiv"
					style="float: left; padding: 5px; width: 98%;">
					<div id="prevImage">
						<img class="profile-user-img img-responsive img-circle lazy"
							id="profilecontainerimg" src="userImages/avatar_photo.png"
							data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+strProID+"/"+IConstants.I_100x100+"/"+hmEmpProfile.get("IMAGE")%>">
					</div>
					<form id="uploadProfileImageForm">
						<input id="file" type="file"
							accept=".gif,.jpg,.png,.tif,.svg,.svgz" />
						<%-- <p><span id="progress" class="progress">0%</span></p> --%>
						<br>
						<div id="views"></div>
						<div class="clr margintop20"></div>
						<button id="cropbutton" type="button" class="btn btn-success"
							style="display: none;">Crop</button>
						<input type="submit" name="submit" id="uploadProfilePhoto"
							value="Upload" class="btn btn-primary" style="display: none;" />
						<div id="msgProfileDiv">Please select Image</div>
					</form>
				</div>
			</div>
			<div class="modal-footer">
				<button type="button" id="closeButton1" class="btn btn-default"
					data-dismiss="modal">Close</button>
			</div>
		</div>
	</div>
</div>
<div class="modal" id="editCoverPhoto" role="dialog">
	<div class="modal-dialog modal-dialog2">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h4 class="modal-title modal-title2">Edit Cover Photo</h4>
			</div>
			<div class="modal-body" id="modal-body2"
				style="height: 400px; overflow-y: auto; padding-left: 25px;">
				<div id="uploadCoverPhotoDiv"
					style="float: left; padding: 5px; width: 98%;">
					<%-- <div id="prevImage">
                        <img id="covercontainerimg" src="images1/user-background-photo.jpg" style="width: 100%;height: 150px;background-size: cover;" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE_COVER+"/"+hmEmpProfile.get("COVER_IMAGE")%>">
                    </div> --%>
					<form id="uploadCoverImageForm" class="margintop20"
						action="MyProfile.action" enctype="multipart/form-data"
						method="POST">
						<input type="hidden" name="empId" id="empId"
							value="<%=(String)hmEmpProfile.get("EMP_ID")%>" /> <input
							type="hidden" name="strImgType" id="strImgType" value="imgcover" />
						<input type="file" name="empCoverImage" id="empCoverImage"
							accept=".gif,.jpg,.png,.tif,.svg,.svgz" /> <input type="submit"
							name="submit" id="uploadCoverPhoto" value="Upload"
							class="btn btn-primary margintop20" />
					</form>
				</div>
			</div>
			<div class="modal-footer">
				<button type="button" id="closeButton2" class="btn btn-default"
					data-dismiss="modal">Close</button>
			</div>
		</div>
	</div>
</div>

<script>
    $('#default').raty();
     <%double dblPrimary = 0;
        if (alSkills != null && alSkills.size() != 0) {
        	for (int i = 0; i < alSkills.size(); i++) {
        		List<String> alInner = alSkills.get(i);%>
    		$('#star<%=i%>').raty({
    			  readOnly: true,
    			  start:    <%=uF.parseToDouble(alInner.get(2)) / 2%>,
    			  half: true
    			});
    		<%if (i == 0) {
        dblPrimary = uF.parseToDouble(alInner.get(2)) / 2;
        }
        }
        }%>
    $('#skillPrimary').raty({
    	  readOnly: true,
    	  start:    <%=dblPrimary%>,
    	  half: true
    	});
    
    <%if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT + "") >= 0) {
        double dblScorePrimary = 0, aggregeteMarks = 0, totAllAttribMarks = 0;
        int count = 0;
        
        for (int i = 0; elementouterList != null && i < elementouterList.size(); i++) {
        	List<String> innerList = elementouterList.get(i);
        	List<List<String>> attributeouterList1 = hmElementAttribute.get(innerList.get(0).trim());
        	for (int j = 0; attributeouterList1 != null && j < attributeouterList1.size(); j++) {
        		List<String> attributeList1 = attributeouterList1.get(j);
        		double dblAttribRating = uF.parseToDouble(hmScoreAggregateMap.get(attributeList1.get(0).trim()));
				if(dblAttribRating>0) {
	        		totAllAttribMarks += uF.parseToDouble(hmScoreAggregateMap.get(attributeList1.get(0).trim()));
	        		count++;
				}
        	}
        }
        aggregeteMarks = totAllAttribMarks / count;
        
        dblScorePrimary = aggregeteMarks; 
        %>
    	$('#skillPrimaryOverall').raty({
    		  readOnly: true,
    		  start:    <%=dblScorePrimary%>,
    		  half: true
    		});
    	
    
    <% } %>   
    
    $("#cropbutton").click(function(e) {
      applyCrop();
      $("#uploadProfilePhoto").show();
    });
	$("#file").change(function() {
	  flag = 0;
      loadImage(this);
      $("#prevImage").hide();
      $("#msgProfileDiv").hide();
      $("#cropbutton").show();
      $("#uploadProfilePhoto").hide();
	});
	
    $("#uploadProfileImageForm").submit(function(e) {
      e.preventDefault();
      var empId = '<%=(String)hmEmpProfile.get("EMP_ID")%>';
      formData = new FormData($(this)[0]);
      var blob = dataURLtoBlob(canvas.toDataURL('image/png'));
      formData.append("empImage", blob);
      $("#uploadProfilePhoto").prop('disabled', true);
      $("#uploadProfilePhoto").val('Uploading...');
      $.ajax({
        url: "MyProfile.action?strImgType=img&empId="+empId,
        type: "POST",
        data: formData,
        contentType: false,
        cache: false,
        processData: false,
        success: function(data) {
          alert("Successfully updated the profile picture.");
          $("#editProfilePhoto").hide();
        },
        error: function(data) {
          alert("Error while updating the profile picture. Please try again");
          $("#editProfilePhoto").hide();
        },
        complete: function(data) {
       	  $("#uploadProfilePhoto").prop('disabled', false);
          $("#uploadProfilePhoto").val('Upload');
        }
      });
    });
    
   <%--  $("#uploadCoverImageForm").submit(function(e) {
        e.preventDefault();
        var empId = '<%=(String)hmEmpProfile.get("EMP_ID")%>';
        //var form_data = $("#uploadCoverImageForm").serialize();
        var form_data = new FormData();
        var coverImage = $("#empCoverImage").val();
        console.log("coverImage==>"+coverImage);
        //form_data = form_data+"empCoverImage='"+coverImage+"'";
        form_data.append("empCoverImage",coverImage);
        console.log("form_data==>"+form_data);
        $("#uploadCoverPhoto").prop('disabled', true);
        $("#uploadCoverPhoto").val('Uploading...');
        $.ajax({
            url: "MyProfile.action?strImgType=imgcover&empId="+empId,
            type: "POST",
            data: form_data,
            success: function(data) {
              alert("Successfully updated the cover picture.");
              $("#editCoverPhoto").hide();
            },
            error: function(data) {
              alert("Error while updating the cover picture. Please try again");
              $("#editCoverPhoto").hide();
            },
            complete: function(data) {
           	  $("#uploadCoverPhoto").prop('disabled', false);
              $("#uploadCoverPhoto").val('Upload');
            }
          });
    }); --%>

</script>
