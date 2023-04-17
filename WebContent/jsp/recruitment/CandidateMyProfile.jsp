<%@page import="ChartDirector.AngularMeter"%>
<%@page import="java.util.*"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!-- timeline -->
<link rel="stylesheet" href="scripts/timeline/css/reset.css">
<!-- CSS reset -->
<link rel="stylesheet" href="scripts/timeline/css/style.css">
<!-- Resource style -->
<script src="scripts/timeline/js/modernizr.js"></script> <!-- Modernizr -->
<%-- <script type="text/javascript"src="js/jquery.gdocsviewer.min.js"></script> --%>
<!-- timeline end -->
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script type="text/javascript">  
		$(function(){
			$("body").on('click','#closeButton',function(){
				$(".modal-body").height(400);
				$(".modal-dialog").removeAttr('style');
				$("#modalInfo").hide();
		    });
			$("body").on('click','.close',function(){
				$(".modal-dialog").removeAttr('style');
				$("#modalInfo").hide();
				$(".modal-body").height(400);
			});
		});
    	function addToCalender(panelId,recruitId,CandidateId,i){
    
    		var dateinterview=document.getElementById("interviewdate"+i).value;
    		var interviewTime=document.getElementById("interviewTime"+i).value;
    		var action ='Addinterviewpaneldate.action?interviewTime='+interviewTime+'&dateinterview='+dateinterview+'&pageFrom=candidateMyProfile&type=insert&recruitId='+recruitId+'&panelId='+panelId+'&candidateId='+CandidateId ;
    
    		window.location= action;
    	}
    
    
    function insertinterviewdate(panelId,CandidateId,recruitId){
    	
    /*   	var id=document.getElementById("popupAjaxLoad");
        if(id){
        	id.parentNode.removeChild(id);
        }  */
    		
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
    
    
    function addNewCandidate(CandidateId,recruitID,step) {
    	/* dialogEdit3 = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>')
    			.appendTo('body'); */
    			var dialogEdit = '.modal-body';
    		    $(dialogEdit).empty();
    		    $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    		    $("#modalInfo").show();
    		    $(".modal-title").html('Edit Candidate Information');
    		    if($(window).width() >= 900){
    		    	$(".modal-dialog").width(900);
    		    }
    		    $.ajax({
    				<%-- "AddCandidate.action?show=show&type=edit&operation=U&mode=profile&CandidateId=<%=(String)request.getAttribute("CandID")%>&recruitId=<%=(String)request.getAttribute("recruitId") %>&step=6" --%>
    				url :"AddCandidate.action?show=show&type=edit&operation=U&mode=profile&CandidateId="+CandidateId+"&recruitId="+recruitID+"&step="+step,
    				cache : false,
    				success : function(data) {
    					$(dialogEdit).html(data);
    				}
    			});
    	//$(dialogEdit2).dialog('close');
     }
    
    
    function joiningCall(value){
    	 if(value==1){
    		document.getElementById("joiningTR").style.display='table-row';
    /* 		document.getElementById("ctcTR").style.display='table-row'; */
    		document.getElementById("ctcDisplayTR").style.display='table-row';
    	}else{
    		document.getElementById("joiningTR").style.display='none';
    /* 		document.getElementById("ctcTR").style.display='none'; */
    		document.getElementById("ctcDisplayTR").style.display='none';
    	} 	
    	}
    	
    $(function() {
    
        $("#joiningdate").datepicker({dateFormat: 'dd/mm/yy', yearRange: '1980:2020', changeYear: true});
     
    });
    
    $(document).ready(function() {
    	 
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
        /* $('a.close, #fade').live('click', function() { //When clicking on the close or fade layer...
            $('#fade , .popup_block').fadeOut(function() {
                $('#fade, a.close').remove();  //fade them both out
            });
            return false;
        }); */
    
    });
    
    
    function openOtherdate(){
    	document.getElementById("otherDateDiv").style.display='block';
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
    
    function viewMyResume(CandID, recruitId) {
        
    	var dialogEdit = '.modal-body';
	    $(dialogEdit).empty();
	    $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	    $('.modal-title').html('My Resume');
	    $("#modalInfo").show();
	    
	    var height = $(window).height()* 0.90;
		var width = $(window).width()* 0.90;
		$(".modal-dialog").css("height", height);
		$(".modal-dialog").css("width", width);
		$(".modal-dialog").css("max-height", height);
		$(".modal-dialog").css("max-width", width);
	    $.ajax({
		    url :"ViewCandidateResume.action?CandID="+CandID+"&recruitId="+recruitId+"&form=A",
		    cache : false,
		    success : function(data) {
		    $(dialogEdit).html(data);
	    }
	    });
     }
    
    
    function closeForm() {
    	window.location = "CandidateReport.action";
    }
    
</script>
<%
    CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
    
    Map<String, String> hmEmpName = (Map<String, String>)request.getAttribute("hmEmpName");
    
    UtilityFunctions uF = new UtilityFunctions();
    /* EncryptionUtility eU = new EncryptionUtility(); */
    
    String strUserType = (String) session.getAttribute(IConstants.USERTYPE);
    String strSessionEmpID = (String) session.getAttribute(IConstants.EMPID);
    
    Map<String,String> hmCandNameMap = (Map<String,String>)request.getAttribute("hmCandNameMap");
    ArrayList alDocuments = (ArrayList) request.getAttribute("alDocuments");
    ArrayList alResumes = (ArrayList) request.getAttribute("alResumes");
    List alSkills = (List) request.getAttribute("alSkills");
    List alHobbies = (List) request.getAttribute("alHobbies");
    List alPrevEmployment = (List) request.getAttribute("alPrevEmployment");
    List alCandiReferences = (List) request.getAttribute("alCandiReferences");
    List alLanguages = (List) request.getAttribute("alLanguages");
    List alEducation = (List) request.getAttribute("alEducation");
    List alFamilyMembers = (List) request.getAttribute("alFamilyMembers");
    List alCertification = (List) request.getAttribute("alCertification");//Created By Dattatray Date:23-08-21
    String recruitID= (String)request.getAttribute("recruitId");
    
    
    if(alResumes == null) alResumes = new ArrayList();
    if(alDocuments == null) alDocuments = new ArrayList();
    if(alSkills == null) alSkills = new ArrayList();
    if(alHobbies == null) alHobbies = new ArrayList();
    if(alPrevEmployment == null) alPrevEmployment = new ArrayList();
    if(alCandiReferences == null) alCandiReferences = new ArrayList();
    if(alLanguages == null) alLanguages = new ArrayList();
    if(alEducation == null) alEducation = new ArrayList();
    if(alFamilyMembers == null) alFamilyMembers = new ArrayList();
    
    if(hmCandNameMap == null) hmCandNameMap = new HashMap<String,String>(); 

    String candidateID = (String) request.getAttribute("CandID");
    
    Map hm = (HashMap) request.getAttribute("myProfile");
    if (hm == null) {
    	hm = new HashMap();
    }
    
    String strImage = (String) hm.get("IMAGE");
    String strTitle = "";
    if (strUserType != null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE)) {
    	strTitle = (String) hm.get("NAME") + "'s Profile";
    } else {
    	strTitle = "My Profile";
    
    }
    
    String RID=(String) request.getAttribute("recruitId");
    List<List<String>> alAMedicalDetails=(List<List<String>> )request.getAttribute("alAMedicalDetails");
    Map<String,String> medicalQuest=(Map<String,String> )request.getAttribute("medicalQuest");
    
    List<List<String>> activityList = (List<List<String>>) request.getAttribute("activityList");
    String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
    
    
    Map<String, List<String>> hmEducationDocs = (Map<String, List<String>>) request.getAttribute("hmEducationDocs");
    
  //===start parvez date: 08-08-2022===    
    Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(request);
	
  //===end parvez date: 08-08-2022===
    
    %>

<section class="content">
	<div class="row jscroll">
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
                    <div id="skillPrimary" style="width: auto !important;" align="center"></div>
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
                        <strong><%=((List) alSkills.get(i)).get(1)%></strong>
                        </span>&nbsp;
                        <%}
					     }%>
                    </p>
                </div>
                <!-- /.box-body -->
            </div>
            
            <div class="box box-body" style="padding: 5px; overflow-y: auto; max-height: 250px; background-color: rgb(229, 121, 38);">
				<div id="profilecontainer">
					<div class="content1" style="max-height: 300px; padding: 5px;">
						<a style="color: white; font-size: 14px; font-weight: bold;" href="javascript:void(0);" onclick="viewMyResume('<%=(String)request.getAttribute("CandID")%>', '<%=(String)request.getAttribute("recruitId") %>');">View My Resume</a>
					</div>
				</div>
			</div>
            
    	</section> 
    	<section class="col-lg-9 connectedSortable" style="padding-left: 0px;">
    		<div class="nav-tabs-custom">
                <ul class="nav nav-tabs">
                    <li class="active"><a href="#about" data-toggle="tab">About</a></li>
                    <!-- <li><a href="#resume" data-toggle="tab">Resume</a></li> -->
                    <li><a href="#timeline" data-toggle="tab">Timeline</a></li>
                    <div class="box-tools pull-right">
						<a href="javascript:void(0);" onclick="closeForm()" class="close-font" style="margin-right: 20px;"> </a>
					</div>
                </ul>
                <div class="tab-content" style="margin-top: 20px;">
                    <div class="active tab-pane" id="about">
                    	<div class="about-item">
	                      <h3 class="about-header">Candidate Information</h3>
	                      <div class="about-body">
							  <div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE; margin-top: 10px;">
									                <div class="box-header with-border">
									                    <h3 class="box-title" style="font-size: 14px;">
										                    
										                    Employment Status & Expectations
									                    </h3>
									                    <div class="box-tools pull-right">
									                    <% if(strUserType!=null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) && !strUserType.equalsIgnoreCase(IConstants.ARTICLE) && !strUserType.equalsIgnoreCase(IConstants.CONSULTANT) && !strUserType.equalsIgnoreCase(IConstants.MANAGER)){ %>	
							                                <a href="javascript: void(0);" onclick="addNewCandidate('<%=(String)request.getAttribute("CandID")%>','<%=(String)request.getAttribute("recruitId") %>','1');" rel="popup_name" title="Edit Employement Status & Expectations"> 
							                                	<span class="label label-danger" style="font-size: 9px;">
										                        	<strong>Edit</strong>
										                        </span>
										                    </a>
							                                <% } %>	
									                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
									                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
									                    </div>
									                </div>
									                <!-- /.box-header -->
									                <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
									                    <div class="content1">
							                                <table class=" table table_no_border autoWidth" >
							                                    <tr>
							                                        <td class="alignRight">Availability:</td>
							                                        <td class="textblue"><%=(String)hm.get("AVAILABILITY") %></td>
							                                    </tr>
							                                    <tr>
							                                        <td class="alignRight">Current CTC:</td>
							                                        <td class="textblue"><%=uF.showData((String) hm.get("CURRENT_CTC"), "-") %></td>
							                                    </tr>
							                                    <tr>
							                                        <td class="alignRight">Expected CTC:</td>
							                                        <td class="textblue"><%=uF.showData((String) hm.get("EXPECTED_CTC"), "-") %></td>
							                                    </tr>
							                                    <tr>
							                                        <td class="alignRight">Notice Period:</td>
							                                        <td class="textblue"><%=uF.showData((String) hm.get("NOTICE_PERIOD"), "-")%></td>
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
									                   		<% if(strUserType!=null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) && !strUserType.equalsIgnoreCase(IConstants.ARTICLE) && !strUserType.equalsIgnoreCase(IConstants.CONSULTANT) && !strUserType.equalsIgnoreCase(IConstants.MANAGER)){ %>	
							                                <a href="javascript: void(0);" onclick="addNewCandidate('<%=(String)request.getAttribute("CandID")%>','<%=(String)request.getAttribute("recruitId") %>','1');" rel="popup_name" title="Edit Personal Information"><span class="label label-danger" style="font-size: 9px;">
										                        	<strong>Edit</strong>
										                        </span> </a>
							                                <% } %>	
									                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
									                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
									                    </div>
									                </div>
									                <!-- /.box-header -->
									                <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
									                    <div class="content1">
							                                <table class=" table table_no_border autoWidth" >
							                                    <tr>
							                                        <td class="alignRight">Current Address:</td>
							                                       <td class="textblue"><%=uF.showData((String)hm.get("TMP_ADDRESS"), "") %></td>
							                                           
							                                    </tr>
							                                    <tr>
							                                        <td class="alignRight">Permanent Address:</td>
							                                       <td class="textblue"><%=uF.showData((String)hm.get("ADDRESS"), "") %></td>
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
																	<td class="alignRight">Bloodgroup:</td>
																	<td class="textblue"><%=(String) hm.get("BLOOD_GROUP")%></td>
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
																	<td class="alignRight">Date of marriage:</td>
																	<td class="textblue"><%=(String) hm.get("DATE_OF_MARRIAGE")%></td>
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
									                    <% if(strUserType!=null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) && !strUserType.equalsIgnoreCase(IConstants.ARTICLE) && !strUserType.equalsIgnoreCase(IConstants.CONSULTANT) && !strUserType.equalsIgnoreCase(IConstants.MANAGER)){ %>
						                                <a href="javascript: void(0);" onclick="addNewCandidate('<%=(String)request.getAttribute("CandID")%>','<%=(String)request.getAttribute("recruitId") %>','2');" rel="popup_name" title="Edit Education Details"><span class="label label-danger" style="font-size: 9px;">
										                        	<strong>Edit</strong>
										                        </span></a>
						                                <% } %>
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
							                                        <!-- ===start parvez date: 08-09-2021=== -->
							                                        <td class="alignCenter">Certificate</td>
							                                        <!-- ===end parvez date: 08-09-2021=== -->
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
									                    <% if (strUserType != null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) && !strUserType.equalsIgnoreCase(IConstants.ARTICLE) && !strUserType.equalsIgnoreCase(IConstants.CONSULTANT) && !strUserType.equalsIgnoreCase(IConstants.MANAGER)) { %>
						                                <a href="javascript: void(0);" onclick="addNewCandidate('<%=(String)request.getAttribute("CandID")%>','<%=(String)request.getAttribute("recruitId") %>','2');" rel="popup_name" title="Edit Skills Set"> <span class="label label-danger" style="font-size: 9px;">
										                        	<strong>Edit</strong>
										                        </span></a>
						                                <% } %>
									                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
									                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
									                    </div>
									                </div>
									                <!-- /.box-header -->
									                <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
									                    <div class="content1" style="margin: 0px 0px 10px 0px">
							                                <table class=" table table_no_border autoWidth"  style="width: 98%">
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
									                    <% if (strUserType != null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) && !strUserType.equalsIgnoreCase(IConstants.ARTICLE) && !strUserType.equalsIgnoreCase(IConstants.CONSULTANT) && !strUserType.equalsIgnoreCase(IConstants.MANAGER)) { %>
						                                <a href="javascript: void(0);" onclick="addNewCandidate('<%=(String)request.getAttribute("CandID")%>','<%=(String)request.getAttribute("recruitId") %>','2');" rel="popup_name"  title="Edit Languages"> <span class="label label-danger" style="font-size: 9px;">
										                        	<strong>Edit</strong>
										                        </span></a>
						                                <% } %>
									                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
									                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
									                    </div>
									                </div>
									                <!-- /.box-header -->
									                <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
									                    <div class="content1" style="margin: 0px 0px 10px 0px">
							                                <table class=" table table_no_border autoWidth"  style="width: 98%">
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
									   <!-- ===start parvez date: 08-08-2022=== -->         
					                      <% if(!uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_EMPLOYEE_HOBBIES_DISABLE))){ %>      
					                            <div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE; margin-top: 10px;">
									                <div class="box-header with-border">
									                    <h3 class="box-title" style="font-size: 14px;">Hobbies</h3>
									                    <div class="box-tools pull-right">
									                    <% if (strUserType != null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) && !strUserType.equalsIgnoreCase(IConstants.ARTICLE) && !strUserType.equalsIgnoreCase(IConstants.CONSULTANT) && !strUserType.equalsIgnoreCase(IConstants.MANAGER)) { %>
						                                <a href="javascript: void(0);" onclick="addNewCandidate('<%=(String)request.getAttribute("CandID")%>','<%=(String)request.getAttribute("recruitId") %>','2');" rel="popup_name"  title="Edit Hobbies"><span class="label label-danger" style="font-size: 9px;">
										                        	<strong>Edit</strong>
										                        </span> </a>
						                                <% } %>
									                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
									                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
									                    </div>
									                </div>
									                <!-- /.box-header -->
									                <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
									                    <div class="content1" style="margin: 0px 0px 10px 0px">
							                                <table class=" table table_no_border autoWidth"  style="width: 98%">
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
									                    <% if(strUserType!=null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) && !strUserType.equalsIgnoreCase(IConstants.ARTICLE) && !strUserType.equalsIgnoreCase(IConstants.CONSULTANT) && !strUserType.equalsIgnoreCase(IConstants.MANAGER)){ %>
						                                <a href="javascript: void(0);" onclick="addNewCandidate('<%=(String)request.getAttribute("CandID")%>','<%=(String)request.getAttribute("recruitId") %>','6');" rel="popup_name" title="Edit Medical Details"><span class="label label-danger" style="font-size: 9px;">
										                        	<strong>Edit</strong>
										                        </span> </a>
						                                <%}%>
									                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
									                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
									                    </div>
									                </div>
									                <!-- /.box-header -->
									                <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
									                    <div class="content1" style="margin: 0px 0px 10px 0px">
							                                <table class=" table table_no_border autoWidth"  style="width:98%">
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
									                    <% if(strUserType!=null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) && !strUserType.equalsIgnoreCase(IConstants.ARTICLE) && !strUserType.equalsIgnoreCase(IConstants.CONSULTANT) && !strUserType.equalsIgnoreCase(IConstants.MANAGER)) { %>
						                                <a href="javascript: void(0);" onclick="addNewCandidate('<%=(String)request.getAttribute("CandID")%>','<%=(String)request.getAttribute("recruitId") %>','7');" rel="popup_name" title="Edit Documents"><span class="label label-danger" style="font-size: 9px;">
										                        	<strong>Edit</strong>
										                        </span> </a>
						                                <%}%>
									                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
									                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
									                    </div>
									                </div>
									                <!-- /.box-header -->
									                <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
									                    <div class="content1" style="margin: 0px 0px 10px 0px">
							                                <table class=" table table_no_border autoWidth"  style="width: 98%">
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
							                                        <!-- <td class="textblue">-</td> -->
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
									                    <% if(strUserType!=null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) && !strUserType.equalsIgnoreCase(IConstants.ARTICLE) && !strUserType.equalsIgnoreCase(IConstants.CONSULTANT) && !strUserType.equalsIgnoreCase(IConstants.MANAGER)) { %>
						                                <a href="javascript: void(0);" onclick="addNewCandidate('<%=(String)request.getAttribute("CandID")%>','<%=(String)request.getAttribute("recruitId") %>','7');" rel="popup_name" title="Edit Documents"><span class="label label-danger" style="font-size: 9px;">
										                        	<strong>Edit</strong>
										                        </span> </a>
						                                <%}%>
									                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
									                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
									                    </div>
									                </div>
									                <!-- /.box-header -->
									                <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
									                    <div class="resumeContent" style="margin: 0px 0px 10px 0px;">
							                                <table class=" table table_no_border autoWidth">
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
							                                        	String action ="ViewCandidateResume.action?from=candProfile&candId="+(String)((ArrayList)alResumes.get(i)).get(3) +"&documentName="+((ArrayList)alResumes.get(i)).get(1) +"&documentId="+(String)((ArrayList)alResumes.get(i)).get(0)+"&filePath="+URLEncoder.encode(filePath);
							                                        	//System.out.println("action==>"+action);
							                                        %>
							                                    <tr>
							                                        <td class="alignRight">
							                                            <%=((ArrayList)alResumes.get(i)).get(1)%>
							                                        </td>
							                                        <td class="alignRight">
							                                        	<% 
							                                        	System.out.println("((ArrayList)alResumes.get(i)).get(4) ===>> " + ((ArrayList)alResumes.get(i)).get(4));
							                                        	if(((ArrayList)alResumes.get(i)).get(4) !=null && !((ArrayList)alResumes.get(i)).get(4).toString().trim().equals("")) { %>
								                                            <%if(docRetriveLocation == null) { %>
								                                            <a href="<%=request.getContextPath()+ IConstants.DOCUMENT_LOCATION + ((ArrayList)alResumes.get(i)).get(4) %>" title="Download Resume" ><i class="fa fa-file-o" aria-hidden="true"></i></a>
								                                            <%} else { %>
								                                            <a href="<%=docRetriveLocation +IConstants.I_CANDIDATE+"/"+IConstants.I_DOCUMENT+"/"+IConstants.I_ATTACHMENT+"/"+candidateID+"/"+ ((ArrayList)alResumes.get(i)).get(4)  %>" title="Download Resume" ><i class="fa fa-file-o" aria-hidden="true"></i></a> 
								                                            <%} %>
								                                            <%-- |  <a target="_blank" href="<%=action %>" style="font-weight: bold;" title="View Resume"> View Resume</a> --%> 
								                                            <%--  <a  href="javascript:void(0)" onclick="showResume('<%=((ArrayList)alResumes.get(i)).get(0)%>','<%=action%>','<%=hmCandNameMap.get(((ArrayList)alResumes.get(i)).get(3))%>');" style="font-weight:bold;" title="View Resume"> View Resume</a> --%> 
							                                        	<% } else { %>
							                                        		N/A
							                                        	<% } %>
							                                        </td>
							                                    </tr>
							                                    <% } %>
							                                    <%  		
							                                        } else { %>
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
	                      	<% if(strUserType!=null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) && !strUserType.equalsIgnoreCase(IConstants.ARTICLE) && !strUserType.equalsIgnoreCase(IConstants.CONSULTANT) && !strUserType.equalsIgnoreCase(IConstants.MANAGER)){ %>
					            <a href="javascript: void(0);" style="float: right;" onclick="addNewCandidate('<%=(String)request.getAttribute("CandID")%>','<%=(String)request.getAttribute("recruitId") %>','3');" rel="popup_name" title="Edit Family Information"><span class="label label-danger" style="font-size: 9px;">
										                        	<strong>Edit</strong>
										                        </span></a>
					        <%}%>
	                      </h3>
	                      <div class="about-body">
	                      	  <%
					                                if (alFamilyMembers != null && alFamilyMembers.size() != 0) { %>
					                                	
					                                	
					                                
					                                	<% for (int i = 0; i < alFamilyMembers.size(); i++) {
					                                
					                                		if (((String) ((List) alFamilyMembers.get(i)).get(1))
					                                				.length() != 0) { %>
					                                		
					                                	<div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE; margin-top: 10px;">
										                					                                		
					                                			<%if (((List) alFamilyMembers.get(i)).get(8).equals(
					                                					"FATHER")) {
									                                %>
									                            	<div class="box-header with-border">
													                    <h3 class="box-title" style="font-size: 14px;padding-right: 10px;">Father's Info</h3>
													                    <div class="box-tools pull-right">
													                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
													                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
													                    </div>
													                </div>
									                            <%
									                                } else if (((List) alFamilyMembers.get(i)).get(8)
									                                					.equals("MOTHER")) {
									                                %>
									                                <div class="box-header with-border">
													                    <h3 class="box-title" style="font-size: 14px;padding-right: 10px;">Mother's Info</h3>
													                    <div class="box-tools pull-right">
													                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
													                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
													                    </div>
													                </div>
									                            <%
									                                } else if (((List) alFamilyMembers.get(i)).get(8)
									                                					.equals("SPOUSE")) {
									                                %>
									                                <div class="box-header with-border">
													                    <h3 class="box-title" style="font-size: 14px;padding-right: 10px;">Spouse's Info</h3>
													                    <div class="box-tools pull-right">
													                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
													                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
													                    </div>
													                </div>
									                            <%
									                                } else if (((List) alFamilyMembers.get(i)).get(8)
									                                					.equals("SIBLING")) {
									                                %>
									                                <div class="box-header with-border">
													                    <h3 class="box-title" style="font-size: 14px;padding-right: 10px;">Sibling's Info</h3>
													                    <div class="box-tools pull-right">
													                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
													                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
													                    </div>
													                </div>
									                            <%
									                                }
									                                %>
									            	<div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
						                                <table class=" table table_no_border autoWidth" >
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
						                                    <%
						                                        if (((List) alFamilyMembers.get(i)).get(8).equals(
						                                        					"SPOUSE")
						                                        					|| ((List) alFamilyMembers.get(i)).get(8)
						                                        							.equals("SIBLING")) {
						                                        %>
						                                    <tr>
						                                        <td class="alignRight">Gender:</td>
						                                        <td class="textblue"><%=((List) alFamilyMembers.get(i)).get(7)%></td>
						                                    </tr>
						                                    <%
						                                        }
						                                        %>
						                                </table>
						                        	</div>
						                        </div>
					                            <%
					                                }
					                                	}%>
					                                	
					                                <%} else {
					                                %>
					                            <table class=" table table_no_border autoWidth">
					                                <tr>
					                                    <td class="nodata msg"><span>No Family members
					                                        added</span>
					                                    </td>
					                                </tr>
					                            </table>
					                            <%
					                                }
					                                %>
	                      </div>
	                    </div>
                    	<br/>
                   <% } %> 	
                   <!-- ===end parvez date: 06-09-2022=== -->
                    	
                    	<div class="about-item">
	                      <h3 class="about-header">Previous Employment
	                         <%
				  	    	  	if (strUserType != null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) && !strUserType.equalsIgnoreCase(IConstants.ARTICLE)
					  				&& !strUserType.equalsIgnoreCase(IConstants.CONSULTANT) && !strUserType.equalsIgnoreCase(IConstants.MANAGER)) {
					  		  
				  				%>
							            <a href="javascript: void(0);" style="float: right;" onclick="addNewCandidate('<%=(String)request.getAttribute("CandID")%>','<%=(String)request.getAttribute("recruitId") %>','4');" rel="popup_name" title="Edit Previous Employment">
							            	<span class="label label-danger" style="font-size: 9px;">
												<strong>Edit</strong>
											</span>
										</a>
								<% } %>
	                      </h3>
	                      <div class="about-body">
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
		                        strMsg =((String) innerList.get(6)).length() != 0 ? " with " + innerList.get(6)+ " designation" : "";%>
		                        
		                      	<div class="box box-default collapsed-box" style="margin-top: 10px;border-top-color: #EEEEEE;">
					                <div class="box-header with-border">
					                    <h3 class="box-title" style="font-size: 14px;padding-right: 10px;"><%=innerList.get(1)%></h3>
					                    <div class="box-tools pull-right">
					                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
					                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
					                    </div>
					                </div>
					                <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
					                    <table class=" table table_no_border autoWidth" >
		                                    <tr>
		                                        <td class="alignRight">Location:</td>
		                                        <td class="textblue"><%=uF.showData(innerList.get(2), "") %></td>
		                                    </tr>
		                                    <tr>
		                                        <td class="alignRight">City:</td>
		                                        <td class="textblue"><%=uF.showData(innerList.get(3), "") %></td>
		                                    </tr>
		                                    <tr>
		                                        <td class="alignRight">State:</td>
		                                        <td class="textblue"><%=uF.showData(innerList.get(4), "") %></td>
		                                    </tr>
		                                    <tr>
		                                        <td class="alignRight">Country:</td>
		                                        <td class="textblue"><%=uF.showData(innerList.get(5), "") %></td>
		                                    </tr>
		                                    <tr>
		                                        <td class="alignRight">Contact No:</td> 
		                                        <td class="textblue"><%=uF.showData(innerList.get(6), "") %></td>
		                                    </tr>
		                                    <tr>
		                                        <td class="alignRight">Reporting To:</td>
		                                        <td class="textblue"><%=uF.showData(innerList.get(7), "") %></td>
		                                    </tr>
		                                     <tr>
		                                        <td class="alignRight">Reporting Manager Ph. No.:</td>
		                                        <td class="textblue"><%=uF.showData(innerList.get(13), "") %></td>
		                                    </tr>
		                                     <tr>
		                                        <td class="alignRight">HR Manager:</td>
		                                        <td class="textblue"><%=uF.showData(innerList.get(14), "") %></td>
		                                    </tr>
		                                     <tr>
		                                        <td class="alignRight">HR Manager Ph. No.:</td>
		                                        <td class="textblue"><%=uF.showData(innerList.get(15), "") %></td>
		                                    </tr>
		                                    <tr>
		                                        <td class="alignRight">From:</td>
		                                        <td class="textblue"><%=uF.showData(innerList.get(8), "") %></td>
		                                    </tr>
		                                    <tr>
		                                        <td class="alignRight">To:</td>
		                                        <td class="textblue"><%=uF.showData(innerList.get(9), "") %></td>
		                                    </tr>
		                                    <tr>
		                                        <td class="alignRight">Designation:</td>
		                                        <td class="textblue"><%=uF.showData(innerList.get(10), "") %></td>
		                                    </tr>
		                                    <tr>
		                                        <td class="alignRight">Responsibilities:</td>
		                                        <td class="textblue"><%=uF.showData(innerList.get(11), "") %></td>
		                                    </tr>
		                                    <tr>
		                                        <td class="alignRight">Skills:</td>
		                                        <td class="textblue"><%=uF.showData(innerList.get(12), "") %></td>
		                                    </tr>
		                             <!-- ===start parvez date: 08-08-2022=== -->
		                                    
		                                    <tr>
		                                        <td class="alignRight">ESIC No:</td>
		                                        <td class="textblue"><%=uF.showData(innerList.get(17), "") %></td>
		                                    </tr>
		                                    <tr>
		                                        <td class="alignRight">UAN No:</td>
		                                        <td class="textblue"><%=uF.showData(innerList.get(18), "") %></td>
		                                    </tr>
		                              <!-- ===end parvez date: 08-08-2022=== -->
		                                    
		                                    <tr>
		                                        <td class="alignRight">Experience Letter:</td>
		                                        <td class="textblue">
		                                        	<% if(innerList.get(16) != null) {
                                                    %>
													<div>
														<%if(docRetriveLocation == null) { %>
														<a
															href="<%=request.getContextPath()+ IConstants.DOCUMENT_LOCATION + innerList.get(16)  %>"
															title="Experience Latter"><i class="fa fa-file-o"
															aria-hidden="true"></i>
														</a>
														<% } else { %>
														<a
															href="<%=docRetriveLocation +IConstants.I_CANDIDATE+"/"+IConstants.I_DOCUMENT+"/"+IConstants.I_PREVIOUS_EMPLOYMENT_DOC+"/"+candidateID+"/"+ innerList.get(16) %>"
															title="Experience Latter"><i class="fa fa-file-o"
															aria-hidden="true"></i>
														</a>
														<% } %>
													</div> <% } else { %> N/A <% } %>
												
		                                      </td>
		                                    </tr>
		                                    <!-- ===end parvez date: 08-09-2021=== -->
		                                </table>
					                </div>
					            </div>
		                        <% }
					          }else{ %>
					          	<table class=" table table_no_border autoWidth">
	                                <tr>
	                                    <td class="nodata msg"><span>No Previous Employment added</span>
	                                    </td>
	                                </tr>
	                            </table>
					          <%} %> 	             	       
	                      </div>
	                    </div>
	                    <br/>
	                    <div class="about-item">
	                      <h3 class="about-header">References
	                           <%
									if (strUserType != null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) && !strUserType.equalsIgnoreCase(IConstants.ARTICLE)
										&& !strUserType.equalsIgnoreCase(IConstants.CONSULTANT) && !strUserType.equalsIgnoreCase(IConstants.MANAGER)) {
								%>
							            <a href="javascript: void(0);" style="float: right;" onclick="addNewCandidate('<%=(String)request.getAttribute("CandID")%>','<%=(String)request.getAttribute("recruitId") %>','5');" rel="popup_name" title="Edit Candidate References">
							            	<span class="label label-danger" style="font-size: 9px;">
												<strong>Edit</strong>
											</span>
										</a>
									<%} %>
	                      </h3>
	                      <div class="about-body">
	                          <%
		                        if (alCandiReferences !=null && alCandiReferences.size()>0) {
		                        	String strDateTmp1 = "";
		                        	for (int i = 0; i < alCandiReferences.size(); i++) {
		                        		List<String> innerList = (List<String>) alCandiReferences.get(i);
		                        	String strColor = "#FFA500";
		                        	
		                        %>
		                        
		                      	<div class="box box-default collapsed-box" style="margin-top: 10px;border-top-color: #EEEEEE;">
					                <div class="box-header with-border">
					                    <h3 class="box-title" style="font-size: 14px;padding-right: 10px;"><%=innerList.get(1)%></h3>
					                    <div class="box-tools pull-right">
					                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
					                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
					                    </div>
					                </div>
					                <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
					                    <table class=" table table_no_border autoWidth" >
		                                    <tr>
		                                        <td class="alignRight">Company:</td>
		                                        <td class="textblue"><%=uF.showData(innerList.get(2), "") %></td>
		                                    </tr>
		                                    <tr>
		                                        <td class="alignRight">Designation:</td>
		                                        <td class="textblue"><%=uF.showData(innerList.get(3), "") %></td>
		                                    </tr>
		                                    <tr>
		                                        <td class="alignRight">Contact No.:</td>
		                                        <td class="textblue"><%=uF.showData(innerList.get(4), "") %></td>
		                                    </tr>
		                                    <tr>
		                                        <td class="alignRight">Email Id:</td>
		                                        <td class="textblue"><%=uF.showData(innerList.get(5), "") %></td>
		                                    </tr>
		                                </table>
					                </div>
					            </div>
		                        <% }
					          }	%> 	             	       
	                      </div>
	                    </div>
                    </div>
                    
                    
                    
				<%-- <div class="tab-pane" id="resume" >
					<script type="text/javascript">
					(function($){
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
					})( jQuery );

					$(document).ready(function () {
					    $('a.embed').gdocsViewer();
					});
					
					</script>

					<% 
					List<String> availableExt = (List<String>)request.getAttribute("availableExt");
					if(availableExt == null) availableExt = new ArrayList<String>();
					
					System.out.println("fileExt ===>> " + fileExt +" --- availableExt ===>> " + availableExt);
					boolean flag = false;
					if(fileExt!=null && availableExt.contains(fileExt)){
						flag = true;
					}
					
					//String filePath = (String) request.getParameter("filePath"); %>
					
					<% if(flag) { %>
						<div id="tblDiv" style="float: left;width: 100%;margin-left:10px;">
							<a href="<%=filePath %>" class="embed" id="test">&nbsp;</a>
						</div>
					<%	} else { %>
						<div id="tblDiv" style="float: left;width: 100%;margin-left:10px;height: 500px; background-color: #CCCCCC;">
							<div style="text-align: center; font-size: 24px; padding: 150px;">Document not available</div>
						</div>
					<%	} %>
				</div>	 --%>
                    
                    
                    <div class="tab-pane" id="timeline">
                    	<%
					            Map<String,List<String>> hmPanelScheduleInfo=(Map)request.getAttribute("hmPanelScheduleInfo");
					            Map<String,List<String>> hmPanelInterviewTaken=(Map)request.getAttribute("hmPanelInterviewTaken");
					            Map<String,List<String>> hmPanelDataHR=(Map)request.getAttribute("hmPanelDataHR");
					            List<String> panelList=(List)request.getAttribute("panelList");
					            if(panelList==null) panelList=new ArrayList<String>();
					            	if ((strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.ADMIN)) && (RID!=null && !RID.equals("") && !RID.equalsIgnoreCase("null"))) {
					            %>
					            	 <div class="fieldset">
					            	 	<div class="about-item">
					                      <h3 class="about-header">Panel Schedule</h3>
					                      <div class="about-body">
					                      	<table class=" table table_no_border autoWidth"  style="width: 98%">
					                            <%for (int i = 0; i<panelList.size(); i++) { %>
					                            <tr>
					                                <td><b><u> Panel <%=i + 1%>:</u></b>&nbsp;<%=hmEmpName.get(panelList.get(i))%>
					                                </td>
					                                <%
					                                    List<String> alInner = hmPanelScheduleInfo.get(panelList.get(i));
					                                    		if (alInner != null) {
					                                    
					                                    			%>
					                                <td class="label">Date: <%=alInner.get(0)%></td>
					                                <td class="label">Time: <%=alInner.get(1)%></td>
					                                <td><a href="Addinterviewpaneldate.action?pageFrom=candidateMyProfile&type=remove&recruitId=<%=recruitID %>&panelId=<%=panelList.get(i) %>&candidateId=<%=(String)request.getAttribute("CandID")%>"
					                                    ><img src="images1/list-remove.png" width="18" height="18" />
					                                    </a>
					                                </td>
					                                <% } else { %>
					                                <% if(hmPanelInterviewTaken.keySet().contains(panelList.get(i))) {
					                                    List<String> alInnerInterviewTaken = hmPanelInterviewTaken.get(panelList.get(i));
					                                    if (alInnerInterviewTaken != null) {
					                                %>
					                                <td class="label">Date: <%=alInnerInterviewTaken.get(0)%></td>
					                                <td class="label">Time: <%=alInnerInterviewTaken.get(1)%></td>
					                                <td><%-- <a href="javascript:void(0)"
					                                    onclick="removePanelSchedule(<%=panelList.get(i)%>,<s:property value="recruitId"/>,<%=(String)request.getAttribute("CandID")%>)">
					                                    	<img src="images1/list-remove.png" width="18" height="18" />
					                                    </a> --%> Interview Taken</td>
					                                <% } } else { %>
					                                <td class="label">Date: 
					                                    <input type="text" name="interviewdate" id="interviewdate<%=i%>" style="width: 100px" />
					                                </td>
					                                <td class="label">Time: <input type="text" name="interviewTime" id="interviewTime<%=i%>" style="width: 100px" />
					                                </td>
					                                <td id="dateSelect<%=i%>"><input class="btn btn-primary" type="button" value="Add to Calender"
					                                    style="width: 100px;" onclick="addToCalender(<%=panelList.get(i)%>,<s:property value="recruitId"/>,<%=(String)request.getAttribute("CandID")%>,<%=i%>);">
					                                </td>
					                                <td id="datesubmit<%=i%>"><input class="btn btn-primary" type="button" name="submit" value="Select from List"
					                                    style="width: 120px;" onclick="insertinterviewdate(<%=panelList.get(i)%>,<%=(String)request.getAttribute("CandID")%>,<s:property value="recruitId"/>);">
					                                </td>
					                                <% } } %>
					                            </tr>
					                            <% } %>
					                            <%if(panelList==null || panelList.size()==0) { %>
					                            <tr>
					                                <td><label><b> No Panel Added </b> <br/>Please add panel first.</label>
					                            </tr>
					                            <% } %>
					                        </table>
					                         <script type="text/javascript">
					                            $(function() {
					                            	$("input[name=interviewdate]").datepicker({format: 'dd/mm/yyyy'});
					                            	$('input[name=interviewTime]').datetimepicker({format: 'HH:mm'});
					                            });
					                            
					                        </script>
					                      </div>
					                    </div>
					                    
					                </div>
					                <br/>
					            <%
					            }	if (RID != null && !RID.equals("") && !RID.equalsIgnoreCase("null") && !strUserType.equalsIgnoreCase(IConstants.HRMANAGER) && !strUserType.equalsIgnoreCase(IConstants.ADMIN)) {
					            Map<String,List<String>>  hmPanelData=(Map)request.getAttribute("hmPanelData");
					            %>
					            	<div class="about-item">
					                      <h3 class="about-header">Interview Information of Candidate</h3>
					                      <div class="about-body">
					                      	<table class=" table table_no_border autoWidth">
					                            <s:form name="frmIntComment" action="CandidateMyProfile" method="post">
					                                <s:hidden name="recruitId"></s:hidden>
					                                <s:hidden name="CandID"></s:hidden>
					                                <%
					                                    for (int i = 0; panelList!=null && i<panelList.size(); i++) {
                                                            List<String> alDataList=hmPanelData.get(panelList.get(i));
                                                            if(alDataList==null) alDataList=new ArrayList();
                                              				
                                                            /*
                                                            aldatalist(0)==is interview taken
                                              				aldatalist(1)==comments
                                             				aldatalist(2)==status
                                             				aldatalist(3)==panel_rating 
                                             				aldatalist(4)==status 
                                             				*/
					                                    if (panelList.get(i).equals(strSessionEmpID)) {
					                                %>
					                                <tr>
					                                    <td><b> Panel <%=i + 1%> :</b> <%=hmEmpName.get(panelList.get(i))%></td>
					                                </tr>
					                                <% if ( alDataList!=null && !alDataList.isEmpty() && !uF.parseToBoolean(alDataList.get(0)) ) { %>
					                                <tr>
					                                    <td><textarea rows="3" cols="85" name="interviewcomment"></textarea></td>
					                                </tr>
					                                <tr>
					                                    <td>
					                                        <div>
					                                            <span style="float: right;">Approve for next round: <input type="checkbox" name="approvedeny" value="true" /> </span>
					                                        </div>
					                                    </td>
					                                </tr>
					                                <tr>
					                                    <td>
					                                        <div id="starPrimary" style="float:right;"></div>
					                                        <input type="hidden"
					                                            id="panelrating" name="panelrating" /> 
					                                        <script type="text/javascript">
					                                            $(function() {
						                                           	$('#starPrimary').raty({
							                                            readOnly: false,
							                                            start: 0,
							                                            half: true,
							                                            targetType: 'number',
							                                            click: function(score, evt) {
						                                                   // alert('ID: ' + $(this).attr('id') + "\nscore: " + score + "\nevent: " + evt);
						                                            		$('#panelrating').val(score);
						                                            	}
						                                           	});
					                                            });
					                                        </script>
					                                    </td>
					                                </tr>
					                                <tr>
					                                    <td style="float: right;">
					                                        <input type="submit" value="SAVE" name="intSubmitComment" class="btn btn-primary" />
					                                    </td>
					                                </tr>
					                                <%
					                                    } else {   // showing panel comments and star rating
					                                    %>
					                                <tr>
					                                    <td><b>Comments: </b><%=alDataList.get(1)!=null && alDataList.size()>0 ? alDataList.get(1) : ""%></td>
					                                </tr>
					                                <tr>
					                                    <td>
					                                        <div id="starPrimaryValue<%=panelList.get(i)%>" style="float:left;"></div>
					                                        <input type="hidden" id="panelrating" name="panelrating" /> 
					                                    </td>
					                                </tr>
					                                <script type="text/javascript">
					                                    $(function() {
					                                    		
					                                    $("#starPrimaryValue"+<%=panelList.get(i)%>).raty({
					                                    	 readOnly: true,
					                                    start:	<%= uF.parseToDouble(alDataList != null && alDataList.size()>2 ? alDataList.get(3) : "0")%>  ,
					                                    	 half: true,
					                                     targetType: 'number'
					                                    	 });
					                                                 });
					                                                
					                                </script>
					                                <%
					                                    }
					                                    %>
					                                <%
					                                    } else {
					                                    %>
					                                <tr>
					                                    <td><b> Panel <%=i + 1%> :</b> <%=hmEmpName.get(panelList.get(i))%></td>
					                                </tr>
					                                <%
					                                    if (alDataList!=null && !alDataList.isEmpty() &&  !uF.parseToBoolean(alDataList.get(0))){
					                                    %>
					                                <tr>
					                                    <td>No Interview taken Yet</td>
					                                </tr>
					                                <%}else{
					                                    //System.out.println("alDataList ---> "+alDataList);
					                                    %>
					                                <tr>
					                                    <td><b>Comments: </b><%=uF.showData(alDataList != null && alDataList.size()>0 ? alDataList.get(1) : "","")%></td>
					                                </tr>
					                                <tr>
					                                    <td>
					                                        <div id="starPrimaryValue<%=panelList.get(i)%>" style="float:left;"></div>
					                                        <input type="hidden" id="panelrating" name="panelrating" /> 
					                                    </td>
					                                </tr>
					                                <script type="text/javascript">
					                                    $(function() {
					                                    		
					                                    $("#starPrimaryValue"+<%=panelList.get(i)%>).raty({
					                                    	 readOnly: true,
					                                    start:	<%= uF.parseToDouble(alDataList != null && alDataList.size()>2 ? alDataList.get(3) : "0")%>,
					                                    	 half: true,
					                                     targetType: 'number'
					                                    
					                                    	 });
					                                    
					                                                 });
					                                                
					                                </script>
					                                <%}%>
					                                <%}}%>
					                                <!-- <tr>
					                                    <td>
					                                    	<div>
					                                    		<label>Decision : NA</label>
					                                    
					                                    	</div>
					                                    </td>
					                                    </tr> -->
					                            </s:form>
					                        </table>
					                      </div>
					                    </div>
					                   <br/>
					            <%} %>
					           	<%		
					            Map<String, String> hmDates=(Map)request.getAttribute("hmDateMap");
					            if(hmDates==null)hmDates=new HashMap<String, String>();
					            Map<String, String> hmTime=(Map)request.getAttribute("hmTimeMap");
					            	
					            /* 	Map<String, String> hmDates = (Map<String, String>) request.getAttribute("hmDates"); 	
					            String dateSelected=(String)request.getAttribute("SelectedDate");  */
					            %>
					        <%
					            if (RID != null && !RID.equals("") && !RID.equalsIgnoreCase("null")){ %>
					            <div class="about-item">
					                      <h3 class="about-header">Preferred Time<a href="javascript: void(0);" onclick="addNewCandidate('<%=(String)request.getAttribute("CandID")%>','<%=(String)request.getAttribute("recruitId") %>','8');" rel="popup_name"  style="float:right;" title="Edit Preferred Time"><span class="label label-danger" style="font-size: 9px;">
										                        	<strong>Edit</strong>
										                        </span></a></h3>
					                      <div class="about-body">
					                      	<table class=" table table-bordered autoWidth"  style="width: 80%">
						                        <tr>
						                            <th>&nbsp;</th>
						                            <th class="txtlabel alignRight">Date</th>
						                            <th class="txtlabel" align="left">Time</th>
						                        </tr>
						                        <%
						                            int count = 0;
						                            Iterator<String> itr = hmDates.keySet().iterator();
						                            while (itr.hasNext()) {
						                            String id = itr.next();
						                            %>
						                        <tr>
						                            <td class="alignRight">Option <%=count + 1%></td>
						                            <td align="right"><%=uF.showData(hmDates.get(id), "Not added")%> </td>
						                            <td><%=uF.showData(hmTime.get(id), "Not given")%></td>
						                        </tr>
						                        <% count++;
						                            }
						                            %>				
						                        <%if(hmDates.keySet().size()==0){ %>
						                        <tr>
						                            <td colspan="3"> No Further Dates Added by Candidate </td>
						                        </tr>
						                        <%}%>
						                    </table>
					                      </div>
					            </div>
					            
					        <% } %>
					        <br/>
					    <ul class="timeline timeline-inverse">
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
					                        strMsg =((String) innerList.get(6)).length() != 0 ? " with " + innerList.get(10)+ " designation" : "";
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
	</div>
</section>

<%-- <div id="popup_name" class="popup_block">
    <s:form name="uploadImage" action="UploadImage" enctype="multipart/form-data" method="post">
        <s:hidden name="imageType" value="CANDIDATE_IMAGE"></s:hidden>
        <input type="hidden" name="empId" value="<%=(String)request.getAttribute("CandID")%>" />
        <s:file name="empImage"></s:file>
        <s:submit value="Upload" cssClass="btn btn-primary"></s:submit>
    </s:form>
</div> --%>

<div class="modal" id="modalInfo" role="dialog">
    <div class="modal-dialog">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">Candidate Information</h4>
            </div>
            <div class="modal-body" style="height:400px;overflow-y:auto;padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>