<%@page import="ChartDirector.AngularMeter"%>
<%@page import="java.util.*"%>
<%@page import="com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 
<style>hr {
margin-top: 10px;
margin-bottom: 10px;
}
.timeline>li>.timeline-item>.timeline-header {
font-weight: 600;
font-size: 16px;
}
</style>
<script src="scripts/charts/justgage/raphael-2.1.4.min.js" type="text/javascript"></script>
<script src="scripts/charts/justgage/justgage.js" type="text/javascript"></script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/_rating/js/jquery.raty.min.js"> </script>
<script type="text/javascript"> 
    /* $(document).ready(function(){
    	$("body").on('click','#closeButton',function(){
    		$(".modal-dialog").removeAttr('style');
    		$(".modal-body").height(400);
    		$("#modalInfo").hide();
    		$("#editPhoto").hide();
    		
        });
    	$("body").on('click','.close',function(){
    		$(".modal-dialog").removeAttr('style');
    		$(".modal-body").height(400);
    		$("#modalInfo").hide();
    		$("#editPhoto").hide();
    	});
    });
    
    $(function(){
    	$(".profile-photo").mouseenter(function(){
    	    $(".profile-user-img").css("opacity", "0.4");
    	    $(".profile-user-img").next().css('display','inline');
    	});
    	$(".profile-photo").mouseleave(function(){
    		$(".profile-user-img").next().css('display','none');
    	    $(".profile-user-img").css("opacity", "1");
    	});
    }); */
    
    
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
    
    $(function(){
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
    	});
    
</script>
<%
    UtilityFunctions uF = new UtilityFunctions();
    String strUserType = (String) session.getAttribute(IConstants.USERTYPE);
    String strEmpID = (String) request.getAttribute("EMPID");
    String strProID = (String) request.getParameter("PROFILEID");
    String strSessionEmpID = (String) session.getAttribute("EMPID");
    String empId = (String) request.getAttribute("empId");
    String fromPage = (String) request.getAttribute("fromPage");
    ArrayList alDocuments = (ArrayList) request.getAttribute("alDocuments");
    List<List<String>> alSkills = (List<List<String>>) request.getAttribute("alSkills");
    List<List<String>> alHobbies = (List<List<String>>) request.getAttribute("alHobbies");
    List<List<String>> alLanguages = (List<List<String>>) request.getAttribute("alLanguages");
    List<List<String>> alEducation = (List<List<String>>) request.getAttribute("alEducation");
    List<List<String>> alFamilyMembers = (List<List<String>>) request.getAttribute("alFamilyMembers");
    List<List<String>> alPrevEmployment = (List<List<String>>) request.getAttribute("alPrevEmployment");
    Map<String, String> hmEmpPrevEarnDeduct = (Map<String, String>)request.getAttribute("hmEmpPrevEarnDeduct");
    
    List<List<String>> alActivityDetails = (List<List<String>>) request.getAttribute("alActivityDetails");
    
    List<List<String>> alKRADetails = (List<List<String>>) request.getAttribute("alKRADetails");
    List<List<String>> elementouterList = (List<List<String>>) request.getAttribute("elementouterList");
    
    Map<String, List<List<String>>> hmElementAttribute = (Map<String, List<List<String>>>) request.getAttribute("hmElementAttribute");
    Map<String, String> hmScoreAggregateMap = (Map<String, String>) request.getAttribute("hmScoreAggregateMap");
    
    AngularMeter semiWorkedAbsent = (AngularMeter) request.getAttribute("KPI");
    String semiWorkedAbsent1URL = semiWorkedAbsent.makeSession(request, "chart3");
    
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
    
    String strCurr = (String) request.getAttribute("strCurr");
    
    CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
    Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(request);
    
    %>
    
<script>
    <%-- function getKRA(id) {
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
    } --%>
    
      <%-- $(document).ready(function() {
    
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
          $('a.close, #fade').live('click', function() { //When clicking on the close or fade layer...
              $('#fade , .popup_block').fadeOut(function() {
                  $('#fade, a.close').remove();  //fade them both out
              });
              return false;
          });
    
    
    
    
      }); --%>
      
      function showEditPhoto(){
    	  $("#editPhoto").show();
      }
    
      function deleteTrainer(empId) {
      
      	$("#divTrainerResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
      	if(confirm('Are you sure, you want to delete this Trainer?')) {
  			$.ajax({
  				url:'AddTrainer.action?operation=D&empId='+empId+'&trainerType=Internal',
  				cache :false,
  				success:function(result){
  					$("#divTrainerResult").html(result);
  				}		
  			});
      	}
  	}
</script>

<section class="content">
  <%
	String rowStyle= "";
	if(fromPage != null && fromPage.equals("LD")){
		rowStyle= "style=margin-top:10px";
	
%>
	<div style="width:100%;">
		<a href="javascript:void(0)" onclick="deleteTrainer('<%=empId%>')" style="color: rgb(204, 0, 0);float:right;" style="float:right;" class="del" ><i class="fa fa-trash"></i></a>
	</div>
<%} %>
    <div class="row" <%=rowStyle %>>
        <div class="col-md-3">
            <!-- Profile Image -->
            <div class="box box-primary">
                <div class="box-body box-profile">
                    <%if(docRetriveLocation==null) { %>
                    <div class="profile-photo">
                        <img class="profile-user-img img-responsive img-circle lazy"  id="profilecontainerimg" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + hmEmpProfile.get("IMAGE")%>" >
                        <a href="javascript:void(0)" style="display:none;" onclick="showEditPhoto();"><img width="6%" src="images1/edit-image.png" style="position: absolute;top: 18%;left: 46%;"/></a>
                    </div>
                    <% }else{ %>
                    <div class="profile-photo">
                        <img class="profile-user-img img-responsive img-circle lazy" id="profilecontainerimg" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+strProID+"/"+IConstants.I_100x100+"/"+hmEmpProfile.get("IMAGE")%>">
                        <a href="javascript:void(0)" style="display:none;" onclick="showEditPhoto();"><img width="6%" src="images1/edit-image.png" style="position: absolute;top: 18%;left: 46%;"/></a>
                    </div>
                    <% } %>
                    <!-- <a href="javascript:void" onclick="showEditPhoto();"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a> -->
                    <h4 class="profile-username text-center"><%=uF.showData((String) hmEmpProfile.get("NAME"), "-")%> [<%=uF.showData((String) hmEmpProfile.get("EMPCODE"), "-")%>]</h4>
                    <p class="text-muted text-center text-no-margin"><%=uF.showData((String) hmEmpProfile.get("DESIGNATION_NAME"), "-")%> [<%=uF.showData((String) hmEmpProfile.get("ORG_NAME"), "-")%>]</p>
                    <p class="text-muted text-center text-no-margin">Reporting Manager: <%=uF.showData((String) hmEmpProfile.get("SUPERVISOR_NAME"), "-")%></p>
                    <%if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT + "") >= 0) { %> 
                    <div id="skillPrimaryOverall" style="width: auto !important;" align="center"></div>
                    <% } %>
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
                    <div id="guageKpi" class="gauge"></div>
                    <script>
                    $(function(){
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
                    }); 
                    </script>
                    <p style="font-size: 18px;margin-top: 20px;color: rgb(255, 122, 0);">
                        <%if (request.getAttribute("TIME_DURATION") != null) {%>
                        Since <strong style="font-family: Digital;font-size: 24px;"><%=uF.showData(hmEmpProfile.get("JOINING_DATE"), "NA")%>, </strong>
                        you have worked <strong style="font-family: Digital;font-size: 24px;"><%=request.getAttribute("TIME_DURATION")%></strong>
                        for <strong style="font-family: Digital;font-size: 24px;"><%=request.getAttribute("HRS_WORKED")%></strong> hrs
                        <%} else { %>
                        Your working hours have not been calcualated, yet.
                        <% } %>
                    </p>
                    <hr>
                    <strong><i class="fa fa-calendar-o margin-r-5"></i> Date of Joining</strong>
                    <p><%=uF.showData((String) hmEmpProfile.get("JOINING_DATE"), "-")%></p>
                    <hr>
                    <% if(alSkills!=null && alSkills.size()!=0) { %>	
                    <strong><i class="fa fa-pencil margin-r-5"></i> Skills</strong>
                    <p>
                        <% for(int i=0; i<alSkills.size(); i++) { 
                            if(i%5 == 0){%>
                        <span class="label label-danger">
                        <% }else if(i%5 == 1){ %> 
                        <span class="label label-success">
                        <% }else if(i%5 == 2){ %> 
                        <span class="label label-info">
                        <% }else if(i%5 == 3){ %> 
                        <span class="label label-warning">
                        <% }else{ %>
                        <span class="label label-primary">
                        <% } %>
                        <strong><%=((List)alSkills.get(i)).get(1)%></strong>
                        </span>&nbsp
                        <% } %>
                    </p>
                    <hr>
                    <% } 
                    if (uF.parseToInt((String) request.getAttribute("RESIG_STATUS")) == 1) { %>
                    <p><%=uF.showData((String) request.getAttribute("RESIGNATION_REMAINING"), "0")%></p>
                    <% } %>
                    <% if (uF.parseToInt((String) request.getAttribute("PROBATION_REMAINING")) > 0 && uF.parseToInt((String) request.getAttribute("RESIG_STATUS")) == 0) { %>
                    <p>Your probation period will end in <%=uF.showData((String) request.getAttribute("PROBATION_REMAINING"), "0")%> days</p>
                    <% } %>
                    <hr>
                </div>
                <!-- /.box-body -->
            </div>
            <!-- /.box -->
        </div>
        <!-- /.col -->
        <div class="col-md-9">
            <div class="nav-tabs-custom">
                <ul class="nav nav-tabs">
                    <li class="active"><a href="#about" data-toggle="tab">About</a></li>
                    <li><a href="#timeline" data-toggle="tab">Timeline</a></li>
                </ul>
                <div class="tab-content">
                    <div class="active tab-pane" id="about">
                        <%
                            if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT + "") >= 0) {
                            %>
                        <div class="about-item">
	                      <h3 class="about-header">Attributes</h3>
	                      <div class="about-body">
	                        <%
                                        for (int i = 0; elementouterList != null && i < elementouterList.size(); i++) {
                                        	List<String> innerList = elementouterList.get(i);
                                        %>
                                    <div style="width:100%;clear: both;"><strong><%=innerList.get(1)%></strong></div>
                                    <%
                                        List<List<String>> attributeouterList1 = hmElementAttribute.get(innerList.get(0).trim());
                                        		for (int j = 0; attributeouterList1 != null && j < attributeouterList1.size(); j++) {
                                        			List<String> attributeList1 = attributeouterList1.get(j);
                                        %>
                                    <div style="width:100%;margin-left: 10px;clear: both;">
                                        <div style="float: left;"><%=attributeList1.get(1)%>&nbsp;: </div>
                                        <div id="starPrimary<%=i%><%=j%>" style="margin-left: 5px;float: left;"></div>
                                        <input type="hidden" id="gradewithrating<%=i%><%=j%>" value="<%=hmScoreAggregateMap.get(attributeList1.get(0).trim()) != null ? uF.parseToDouble(hmScoreAggregateMap.get(attributeList1.get(0).trim())) + "" : "0"%>"; name="gradewithrating<%=i%><%=j%>" />
                                        <script type="text/javascript">
                                        $(function(){   
                                        	$('#starPrimary<%=i%><%=j%>').raty({
                                            	readOnly: true,
                                            	start: <%=hmScoreAggregateMap.get(attributeList1.get(0).trim()) != null ? uF.parseToDouble(hmScoreAggregateMap.get(attributeList1.get(0).trim())) + "" : "0"%>,
                                            	half: true,
                                            	targetType: 'number',
                                            	click: function(score, evt) {
                                            		$('#gradewithrating<%=i%><%=j%>').val(score);
                                            		}
                                            });
                                        });    
                                        </script>
                                    </div>
                                    <%
                                        }
                                        	}
                                        %>
                                    <div class="clr"></div>
                                    <%
                                        if (hmElementAttribute == null || hmElementAttribute.isEmpty()) {
                                        %>
                                    <div class="nodata msg" style="width: 93%">
                                        <span>No attribute aligned with this level</span>
                                    </div>
                                    <%
                                        }
                                        %>
	                      </div>
	                    </div>
                        <%
                            }
                            %> 
                        <br/>
                        <div class="about-item">
                        	<%
                                    List alKRA = (List) request.getAttribute("alKRA");
                                    Map hmKRA = (Map) request.getAttribute("hmKRA");
                                    %>
	                      <h3 class="about-header">Key Responsibility Areas</h3>
	                      <div class="about-body">
	                         <%
                                                    String effectiveDate = "";
                                                    if(alKRADetails != null && !alKRADetails.isEmpty()) {
                                                     effectiveDate = alKRADetails.get(alKRADetails.size()-1).get(0);
                                                    }
                                                    %>
                                                <div>
                                                    <%=((effectiveDate != null && !effectiveDate.equals("")) ? "Since: " + effectiveDate : "<div class=\"nodata msg\" style=\"width:95%\">No KRAs defined yet.</span></div>")%>
                                                    <table class="table table_no_border autoWidth">
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
                        <br/>
                        <div class="about-item">
	                      <h3 class="about-header">Current Job</h3>
	                      <div class="about-body">
	                        <table class="table table_no_border autoWidth">
                                            <tr>
                                                <td class="alignRight">Employee Type:</td>
                                                <td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("EMP_TYPE"), "-")%></td>
                                            </tr>
                                            <tr>
                                                <td class="alignRight">Level:</td>
                                                <td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("LEVEL_NAME"), "-")%></td>
                                            </tr>
                                            <tr>
                                                <td class="alignRight">Designation:</td>
                                                <td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("DESIGNATION_NAME"), "-")%></td>
                                            </tr>
                                            <tr>
                                                <td class="alignRight">Grade:</td>
                                                <td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("GRADE_NAME"), "-")%></td>
                                            </tr>
                                            <tr>
                                                <td class="alignRight">SBU:</td>
                                                <td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("SBU_NAME"), "-")%></td>
                                            </tr>
                                            <tr>
                                                <td class="alignRight">Department: </td>
                                                <td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("DEPARTMENT_NAME"), "-")%></td>
                                            </tr>
                                            <tr>
                                                <td class="alignRight">Location: </td>
                                                <td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("WLOCATION_NAME"), "-")%></td>
                                            </tr>
                                            <tr>
                                                <td class="alignRight">Organization: </td>
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
                        <br/>
                        <div class="about-item">
	                      <h3 class="about-header">Reporting Structure</h3>
	                      <div class="about-body">
	                        <table class="table table_no_border autoWidth">
                                            <%-- <tr>
                                                <td class="alignRight">Manager: </td>
                                                <td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("SUPERVISOR_NAME"), "-")%> </td>
                                            </tr> --%>
                                            <tr>
                                                <td class="alignRight">H.O.D.: </td>
                                                <td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("HOD_NAME"), "-")%> </td>
                                            </tr>
                                            <tr>
                                                <td class="alignRight">HR: </td>
                                                <td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("HR_NAME"), "-")%> </td>
                                            </tr>
                                       <!-- ===start parvez date: 30-07-2022=== Note Sequence change-->     
                                            <tr>
                                                <td class="alignRight">Manager: </td>
                                                <td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("SUPERVISOR_NAME"), "-")%> </td>
                                            </tr>
                                      	<!-- ===end parvez date: 30-07-2022=== -->      
                                        </table>
	                      </div>
	                    </div>
                        <br/>
                        <div class="about-item">
		                      <h3 class="about-header">Employee History</h3>
		                      <div class="about-body">
		                        <table class="table table_no_border autoWidth">
                                            <tr>
                                                <td class="alignRight">Joining Date:</td>
                                                <td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("JOINING_DATE"), "-")%></td>
                                            </tr>
                                            <tr>
                                                <td class="alignRight">Last Promotion:</td>
                                                <td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("PREV_PROMOTION"), "-")%></td>
                                            </tr>
                                            <tr>
                                                <td class="alignRight">Previous position:</td>
                                                <td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("PREV_DESIGNATION"), "-")%></td>
                                            </tr>
                                        </table>
		                      </div>
		                </div>
                        <br/>
                        <div class="about-item">
	                      <h3 class="about-header">Other Official Information</h3>
	                      <div class="about-body">
	                        <table class="table table_no_border autoWidth">
                                            <tr>
                                                <td class="alignRight">Employee Status:</td>
                                                <td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("EMPLOYMENT_TYPE"), "-")%></td>
                                            </tr>
                                            <tr>
                                                <td class="alignRight">Roster Policy:</td>
                                                <td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("LEVEL_NAME"), "-")%></td>
                                            </tr>
                                            <% if(hmEmpProfile.get("EMPLOYMENT_TYPE")!=null && hmEmpProfile.get("EMPLOYMENT_TYPE").trim().equals(IConstants.PROBATION)){ %>
                                            <tr>
                                                <td class="alignRight">Probation Period:</td>
                                                <td class="textblue" valign="bottom"> <%=uF.showData((String)request.getAttribute("PROBATION_PERIOD"), "-")%></td>
                                            </tr>
                                            <% } %>
                                            <tr>
                                                <td class="alignRight">Notice Period:</td>
                                                <td class="textblue" valign="bottom"> <%=uF.showData((String)request.getAttribute("NOTICE_PERIOD"), "-")%> days</td>
                                            </tr>
                                            <tr>
                                                <td class="alignRight">Paycycle Duration: </td>
                                                <td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("PAYCYCLE_DURATION"), "-")%> </td>
                                            </tr>
                                            <tr>
                                                <td class="alignRight">Roster dependency?: </td>
                                                <td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("ROSTER_DEPENDENCY"), "-")%> </td>
                                            </tr>
                                            <tr>
                                                <td class="alignRight">Attendance dependency?: </td>
                                                <td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("ATTENDANCE_DEPENDENCY"), "-")%> </td>
                                            </tr>
                                            <tr>
                                                <td class="alignRight">Eligible for allowance:</td>
                                                <td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("ALLOWANCE"), "-")%></td>
                                            </tr>
                                            <tr>
                                                <td class="alignRight">Biometric Machine Id (if integrated): </td>
                                                <td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("BIOMATRIC_MACHINE_ID"), "-")%></td>
                                            </tr>
                                        </table>
	                      </div>
	                    </div>
                        
	                    <br/>
	                    <div class="about-item">
	                      <h3 class="about-header">Leave Snapshot</h3>
	                      <div class="about-body">
	                        <table class="table table_no_border autoWidth">
                                            <tr>
                                                <td class="alignRight"><b>Leave Balance </b></td>
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
                                                <td colspan="2">
                                                    <div class="nodata msg"><span>No Leave Data Available.</span></div>
                                                </td>
                                            </tr>
                                            <% } %>
                                        </table>
	                      </div>
	                    </div>
                        <br/>
                        <div class="about-item">
                          <%
                                        List<List<String>> salaryHeadDetailsList = (List<List<String>>) request.getAttribute("salaryHeadDetailsList");
                                        Map<String, Double> hmSalaryTotal = (Map<String, Double>) request.getAttribute("hmSalaryTotal");
                                        %>
	                      <h3 class="about-header">Compensation Structure</h3>
	                      <div class="about-body">
	                        <table class="table table_no_border autoWidth">
                                            <tr>
                                                <td class="alignRight">Payout Type:</td>
                                                <td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("PAYOUT_TYPE"), "-")%></td>
                                            </tr>
                                            <%
                                                if (hmEmpProfile.get("EMP_ACT_NO") != null && !hmEmpProfile.get("EMP_ACT_NO").equals("")) {
                                                %>
                                            <tr>
                                                <td class="alignRight">Bank Account No.:</td>
                                                <td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("EMP_ACT_NO"), "-")%></td>
                                            </tr>
                                            <%
                                                }
                                                %>
                                        </table>
                                        
                                       <table  class="table table_no_border autoWidth">
                                            <tr>
                                                <td valign="top">
                                                    <table cellspacing="1" cellpadding="2"  class="table table-bordered autoWidth" style="table-layout: fixed;">
                                                        <tr>
                                                            <td colspan="3" nowrap="nowrap" align="center">
                                                                <h5>EARNING DETAILS</h5>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td class="alignRight">Salary Head</td>
                                                            <td width="30%" class="alignRight">Monthly</td>
                                                            <td width="30%" class="alignRight">Annual</td>
                                                        </tr>
                                                        <%
                                                            double grossAmount = 0.0d;
                                                            double grossYearAmount = 0.0d;
                                                            for(int i=0; salaryHeadDetailsList != null && !salaryHeadDetailsList.isEmpty() && i<salaryHeadDetailsList.size(); i++) {
                                                            	List<String> innerList = salaryHeadDetailsList.get(i);
                                                            		if(innerList.get(1).equals("E")) {
                                                            			/* grossAmount +=uF.parseToDouble(innerList.get(2));
                                                            			grossYearAmount +=uF.parseToDouble(innerList.get(3)); */
                                                            			double dblEarnMonth = Math.round(uF.parseToDouble(innerList.get(2)));
                                                            			double dblEarnAnnual = Math.round(uF.parseToDouble(innerList.get(3)));
                                                            			grossAmount += dblEarnMonth;
                                                            			grossYearAmount += dblEarnAnnual;
                                                            %>
                                                        <tr>
                                                            <td class="alignRight"><%=uF.showData(innerList.get(0), "-")%></td>
                                                            <td align="right" class="textblue" valign="bottom"><%=dblEarnMonth %></td>
                                                            <td align="right" class="textblue" valign="bottom"><%=dblEarnAnnual%></td>
                                                        </tr>
                                                        <% } %>
                                                        <% } %>
                                                        <tr>
                                                            <td class="alignRight"><strong>Gross Salary</strong></td>
                                                            <%-- <td class="alignRight textblue"><strong><%=uF.formatIntoTwoDecimal(hmSalaryTotal.get("GROSS_AMOUNT"))%></strong></td>
                                                                <td class="alignRight textblue"><strong><%=uF.formatIntoTwoDecimal(hmSalaryTotal.get("GROSS_YEAR_AMOUNT"))%></strong></td> --%>
                                                            <td class="alignRight textblue"><strong><%=uF.formatIntoTwoDecimal(grossAmount)%></strong></td>
                                                            <td class="alignRight textblue"><strong><%=uF.formatIntoTwoDecimal(grossYearAmount)%></strong></td>
                                                        </tr>
                                                    </table>
                                                </td>
                                                <td valign="top">
                                                    <table cellspacing="1" cellpadding="2" class="table table-bordered autoWidth">
                                                        <tr>
                                                            <td colspan="3" nowrap="nowrap" align="center">
                                                                <h5>DEDUCTION DETAILS</h5>
                                                            </td>
                                                        </tr>
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
                                                            		/* deductAmount +=uF.parseToDouble(innerList.get(2));
                                                            		deductYearAmount +=uF.parseToDouble(innerList.get(3)); */
                                                            		double dblDeductMonth = Math.round(uF.parseToDouble(innerList.get(2)));
                                                            		double dblDeductAnnual = Math.round(uF.parseToDouble(innerList.get(3)));
                                                            		deductAmount += dblDeductMonth;
                                                            		deductYearAmount += dblDeductAnnual;
                                                            %>
                                                        <tr>
                                                            <td class="alignRight"><%=uF.showData(innerList.get(0), "-")%></td>
                                                            <td align="right" class="textblue" valign="bottom"><%=dblDeductMonth %></td>
                                                            <td align="right" class="textblue" valign="bottom"><%=dblDeductAnnual %></td>
                                                        </tr>
                                                        <% } %>
                                                        <% } %>
                                                        <tr>
                                                            <td class="alignRight"><strong>Deduction</strong></td>
                                                            <%-- <td class="alignRight textblue"><strong><%=uF.formatIntoTwoDecimal(hmSalaryTotal.get("DEDUCT_AMOUNT"))%></strong></td>
                                                                <td class="alignRight textblue"><strong><%=uF.formatIntoTwoDecimal(hmSalaryTotal.get("DEDUCT_YEAR_AMOUNT"))%></strong></td> --%>
                                                            <td class="alignRight textblue"><strong><%=uF.formatIntoTwoDecimal(deductAmount)%></strong></td>
                                                            <td class="alignRight textblue"><strong><%=uF.formatIntoTwoDecimal(deductYearAmount)%></strong></td>
                                                        </tr>
                                                    </table>
                                                </td>
                                            </tr>
                                            <%
                                                Map<String,String> hmContribution = (Map<String,String>) request.getAttribute("hmContribution");
                                                if(hmContribution == null) hmContribution = new HashMap<String, String>();
                                                double dblMonthContri = 0.0d;
                                                double dblAnnualContri = 0.0d;
                                                if(uF.parseToDouble(hmContribution.get("EPF_MONTHLY")) > 0 || uF.parseToDouble(hmContribution.get("ESI_MONTHLY")) > 0 || uF.parseToDouble(hmContribution.get("LWF_MONTHLY")) > 0){
                                                	
                                                %>
                                            <tr>
                                                <td valign="top">
                                                    <table cellspacing="1" cellpadding="2"  class="table table_no_border autoWidth" style="float: left;">
                                                        <tr>
                                                            <td colspan="3" nowrap="nowrap" align="center">
                                                                <h5>CONTRIBUTION DETAILS</h5>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td class="alignRight">Contribution Head</td>
                                                            <td width="30%" class="alignRight">Monthly</td>
                                                            <td width="30%" class="alignRight">Annual</td>
                                                        </tr>
                                                        <%if(uF.parseToDouble(hmContribution.get("EPF_MONTHLY")) > 0){
                                                            /* dblMonthContri += uF.parseToDouble(hmContribution.get("EPF_MONTHLY"));
                                                            dblAnnualContri += uF.parseToDouble(hmContribution.get("EPF_ANNUALY")); */
                                                            double dblEPFMonth = Math.round(uF.parseToDouble(hmContribution.get("EPF_MONTHLY")));
                                                            double dblEPFAnnual = Math.round(uF.parseToDouble(hmContribution.get("EPF_ANNUALY")));
                                                            dblMonthContri += dblEPFMonth;
                                                            dblAnnualContri += dblEPFAnnual;
                                                            %>
                                                        <tr>
                                                            <td class="alignRight">Employer PF</td>
                                                            <td align="right" class="textblue" valign="bottom"><%=dblEPFMonth %></td>
                                                            <td align="right" class="textblue" valign="bottom"><%=dblEPFAnnual %></td>
                                                        </tr>
                                                        <%} %>
                                                        <%if(uF.parseToDouble(hmContribution.get("ESI_MONTHLY")) > 0){
                                                            /* dblMonthContri += uF.parseToDouble(hmContribution.get("ESI_MONTHLY"));
                                                            dblAnnualContri += uF.parseToDouble(hmContribution.get("ESI_ANNUALY")); */
                                                            double dblESIMonth = Math.round(uF.parseToDouble(hmContribution.get("ESI_MONTHLY")));
                                                            double dblESIAnnual = Math.round(uF.parseToDouble(hmContribution.get("ESI_ANNUALY")));
                                                            dblMonthContri += dblESIMonth;
                                                            dblAnnualContri += dblESIAnnual;
                                                            %>
                                                        <tr>
                                                            <td class="alignRight">Employer ESI</td>
                                                            <td align="right" class="textblue" valign="bottom"><%=dblESIMonth %></td>
                                                            <td align="right" class="textblue" valign="bottom"><%=dblESIAnnual %></td>
                                                        </tr>
                                                        <%} %>
                                                        <%if(uF.parseToDouble(hmContribution.get("LWF_MONTHLY")) > 0){
                                                            /* dblMonthContri += uF.parseToDouble(hmContribution.get("LWF_MONTHLY"));
                                                            dblAnnualContri += uF.parseToDouble(hmContribution.get("LWF_ANNUALY")); */
                                                            double dblLWFMonth = Math.round(uF.parseToDouble(hmContribution.get("LWF_MONTHLY")));
                                                            double dblLWFAnnual = Math.round(uF.parseToDouble(hmContribution.get("LWF_ANNUALY")));
                                                            dblMonthContri += dblLWFMonth;
                                                            dblAnnualContri += dblLWFAnnual;
                                                            %>
                                                        <tr>
                                                            <td class="alignRight">Employer LWF</td>
                                                            <td align="right" class="textblue" valign="bottom"><%=dblLWFMonth %></td>
                                                            <td align="right" class="textblue" valign="bottom"><%=dblLWFAnnual %></td>
                                                        </tr>
                                                        <%} %>
                                                        <tr>
                                                            <td class="alignRight"><strong>Total</strong></td>
                                                            <td class="alignRight textblue"><strong><%=uF.formatIntoTwoDecimal(dblMonthContri)%></strong></td>
                                                            <td class="alignRight textblue"><strong><%=uF.formatIntoTwoDecimal(dblAnnualContri)%></strong></td>
                                                        </tr>
                                                    </table>
                                                </td>
                                                <td>&nbsp;</td>
                                            </tr>
                                            <%} %>
                                        </table>
                                        <%
                                            double dblCTCMonthly = grossAmount + dblMonthContri;
                                            double dblCTCAnnualy = grossYearAmount + dblAnnualContri;
                                            %>
                                        <table class="table table_no_border autoWidth">
                                            <tr>
                                                <td class="alignRight">Cost To Company (Monthly):</td>
                                                <td class="textblue" valign="bottom"> <%=uF.showData(strCurr,"")+ uF.formatIntoTwoDecimal(dblCTCMonthly)%></td>
                                            </tr>
                                            <tr>
                                                <td class="alignRight">Cost To Company (Annually):</td>
                                                <td class="textblue" valign="bottom"> <%=uF.showData(strCurr,"")+uF.formatIntoTwoDecimal(dblCTCAnnualy)%></td>
                                            </tr>
                                        </table>
                                        
                                 
	                      </div>
	                      <%
                                        if (!isFilledStatus) {
                                        %>
                                        <div class="about-footer">
                        <img src="images1/warning.png" />
                      </div>
                                    <% } %>
	                    </div>
	                    <br/>
                        <div class="about-item"> 
	                      <h3 class="about-header">Statutory Compliance Applied</h3>
	                      <div class="about-body">
	                        <table class="table table_no_border autoWidth">
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
                                                <td class="alignRight">TDS: </td>
                                                <td class="textblue" valign="bottom"><%=uF.showData(TdsForm16Or16A.toString(), "-")%></td>
                                            </tr>
                                        </table>
	                      </div>
	                    </div>
                        <br/>
                        <div class="about-item">
	                      <h3 class="about-header">Corporate Information & Personal Information</h3>
	                      <div class="about-body">
	                        <div class="box box-default collapsed-box" style="margin-top: 10px;">
				                <div class="box-header with-border">
				                    <h3 class="box-title" style="font-size: 14px;padding-right: 10px;">Corporate Information</h3>
				                    
				                    <div class="box-tools pull-right">
				                    	
				                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
				                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
				                    </div>
				                </div>
				                <!-- /.box-header -->
				                <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
				                    <table class="table table_no_border autoWidth">
                                                    <tr>
                                                        <td class="alignRight">Corporate Mobile:</td>
                                                        <td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("CORPORATE_MOBILE"), "-")%></td>
                                                    </tr>
                                                    <tr>
                                                        <td class="alignRight">Corporate Desk:</td>
                                                        <td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("CORPORATE_DESK"), "-")%></td>
                                                    </tr>
                                                    <tr>
                                                        <td class="alignRight">Corporate id:</td>
                                                        <td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("EMAIL_SEC"), "-")%></td>
                                                    </tr>
                                                    <tr>
                                                        <td class="alignRight">Skype id:</td>
                                                        <td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("SKYPE_ID"), "-")%></td>
                                                    </tr>
                                                </table>
				                </div>
				                <!-- /.box-body -->
				            </div>
				            <div class="box box-default collapsed-box" style="margin-top: 10px;">
				                <div class="box-header with-border">
				                    <h3 class="box-title" style="font-size: 14px;padding-right: 10px;">Personal Information</h3>
				                    <div class="box-tools pull-right">
				                    	
				                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
				                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
				                    </div>
				                </div>
				                <!-- /.box-header -->
				                <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
				                    <table  class="table table_no_border autoWidth">
                                                    <tr>
                                                        <td class="alignRight">Current Address:</td>
                                                        <td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("CURRENT_ADDRESS"), "") + ", " + uF.showData(hmEmpProfile.get("CURRENT_CITY"), "") + ", " + uF.showData(hmEmpProfile.get("CURRENT_STATE"), "") + ", "
                                                            + uF.showData(hmEmpProfile.get("CURRENT_COUNTRY"), "")%></td>
                                                    </tr>
                                                    <tr>
                                                        <td class="alignRight">Permanent Address:</td>
                                                        <td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("ADDRESS"), "") + ", " + uF.showData(hmEmpProfile.get("CITY"), "") + ", " + uF.showData(hmEmpProfile.get("STATE"), "") + ", "
                                                            + uF.showData(hmEmpProfile.get("COUNTRY"), "")%></td>
                                                    </tr>
                                                    <tr>
                                                        <td class="alignRight">Landline: </td>
                                                        <td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("CONTACT"), "-")%></td>
                                                    </tr>
                                                    <tr>
                                                        <td class="alignRight">Mobile: </td>
                                                        <td class="textblue" valign="bottom"><%=uF.showData(hmEmpProfile.get("CONTACT_MOB"), "-")%></td>
                                                    </tr>
                                                    <tr>
                                                        <td class="alignRight">Email id:</td>
                                                        <td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("EMAIL"), "-")%></td>
                                                    </tr>
                                                    <tr>
                                                        <td class="alignRight">Date of Birth:</td>
                                                        <td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("DOB"), "-")%></td>
                                                    </tr>
                                                    <tr>
                                                        <td class="alignRight">Gender:</td>
                                                        <td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("GENDER"), "-")%></td>
                                                    </tr>
                                                    <tr>
                                                        <td class="alignRight">Blood Group:</td>
                                                        <td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("EMP_BLOOD_GROUP"), "-")%></td>
                                                    </tr>
                                                    <tr>
                                                        <td class="alignRight">Marital Status:</td>
                                                        <td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("MARITAL_STATUS"), "-")%></td>
                                                    </tr>
                                                    <% if (hmEmpProfile.get("MARITAL_STATUS") != null && hmEmpProfile.get("MARITAL_STATUS").equals("Married")) { %>
                                                    <tr>
                                                        <td class="alignRight">Date Of Marriage:</td>
                                                        <td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("MARRAIGE_DATE"), "-")%></td>
                                                    </tr>
                                                    <% } %>
                                                </table>
				                </div>
				                <!-- /.box-body -->
				            </div>
				            <div class="box box-default collapsed-box" style="margin-top: 10px;">
				                <div class="box-header with-border">
				                    <h3 class="box-title" style="font-size: 14px;padding-right: 10px;">Other Personal Information</h3>
				                    <div class="box-tools pull-right">
				                    	
				                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
				                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
				                    </div>
				                </div>
				                <!-- /.box-header -->
				                <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
				                    <table class="table table_no_border autoWidth">
                                                    <tr>
                                                        <td class="alignRight">Pan No:</td>
                                                        <td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("PAN_NO"), "-")%></td>
                                                    </tr>
                                                    <tr>
                                                        <td class="alignRight">Passport No:</td>
                                                        <td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("PASSPORT_NO"), "-")%></td>
                                                    </tr>
                                                    <tr>
                                                        <td class="alignRight">Passport expires on:</td>
                                                        <td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("PASSPORT_EXPIRY"), "-")%></td>
                                                    </tr>
                                                </table>
				                </div>
				                <!-- /.box-body -->
				            </div>
				            <div class="box box-default collapsed-box" style="margin-top: 10px;">
				                <div class="box-header with-border">
				                    <h3 class="box-title" style="font-size: 14px;padding-right: 10px;">Emergency Information</h3>
				                    <div class="box-tools pull-right">
				                    	
				                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
				                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
				                    </div>
				                </div>
				                <!-- /.box-header -->
				                <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
				                    <table class="table table_no_border autoWidth">
                                                    <tr>
                                                        <td class="alignRight">Contact Name:</td>
                                                        <td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("EMERGENCY_NAME"), "-")%></td>
                                                    </tr>
                                                    <tr>
                                                        <td class="alignRight">Contact Number:</td>
                                                        <td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("EMERGENCY_NO"), "-")%></td>
                                                    </tr>
                                             <!-- ===start parvez date: 01-08-2022=== -->
                                             		<tr>
                                                        <td class="alignRight">Contact Relation:</td>
                                                        <td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("EMERGENCY_RELATION"), "-")%></td>
                                                    </tr>
                                             <!-- ===end parvez date: 01-08-2022=== -->       
                                                    <tr>
                                                        <td class="alignRight">Doctor's Name:</td>
                                                        <td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("DOCTOR_NAME"), "-")%></td>
                                                    </tr>
                                                    <tr>
                                                        <td class="alignRight">Doctor's Contact Number:</td>
                                                        <td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("DOCTOR_NO"), "-")%></td>
                                                    </tr>
                                                </table>
				                </div>
				                <!-- /.box-body -->
				            </div>
				            <div class="box box-default collapsed-box" style="margin-top: 10px;">
				                <div class="box-header with-border">
				                    <h3 class="box-title" style="font-size: 14px;padding-right: 10px;">Personal Statutory Information</h3>
				                    <div class="box-tools pull-right">
				                    	
				                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
				                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
				                    </div>
				                </div>
				                <!-- /.box-header -->
				                <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
				                    <table class="table table_no_border autoWidth">
                                                    <tr>
                                                        <td class="alignRight">Provident Fund No:</td>
                                                        <td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("PF_NO"), "-")%></td>
                                                    </tr>
                                                    <tr>
                                                        <td class="alignRight">GPF Acc No:</td>
                                                        <td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("GPF_ACC_NO"), "-")%></td>
                                                    </tr>
                                                    <tr>
                                                        <td class="alignRight">ESIC No:</td>
                                                        <td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("ESIC_NO"), "-")%></td>
                                                    </tr>
                                                    <tr>
                                                        <td class="alignRight">UAN No:</td>
                                                        <td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("UAN_NO"), "-")%></td>
                                                    </tr>
                                                    <tr>
                                                        <td class="alignRight">Aadhaar No:</td>
                                                        <td class="textblue" valign="bottom"> <%=uF.showData(hmEmpProfile.get("UID_NO"), "-")%></td>
                                                    </tr>
                                                </table>
				                </div>
				                <!-- /.box-body -->
				            </div>
	                      </div>
	                    </div>
                        <br/>
                        <div class="about-item">
	                      <h3 class="about-header">Skills</h3>
	                      <div class="about-body">
	                        <table  class="table table_no_border autoWidth">
                                            <%
                                                if (alSkills != null && alSkills.size() != 0) {
                                                	for (int i = 0; i < alSkills.size(); i++) {
                                                		List<String> alInner = alSkills.get(i);
                                                %>
                                            <tr>
                                                <td><strong><%=alInner.get(1)%>:</strong></td>
                                                <td>
                                                    <div id="star<%=i%>"></div>
                                                </td>
                                            </tr>
                                            <% } } else { %>
                                            <tr>
                                                <td class="nodata msg"><span>No skill sets added</span></td>
                                            </tr>
                                            <% }  %>
                                        </table>
	                      </div>
	                    </div>
                        <br/>
                        <div class="about-item">
	                      <h3 class="about-header">Education</h3>
	                      <div class="about-body">
	                        <%
                                            if (alEducation != null && alEducation.size() != 0) {
                                            	for (int i = 0; i < alEducation.size(); i++) {
                                            		List<String> innerList = alEducation.get(i);
                                            %>
                                <div class="box box-default collapsed-box" style="margin-top: 10px;">
					                <div class="box-header with-border">
					                    <h3 class="box-title" style="font-size: 14px;padding-right: 10px;"><%=innerList.get(1)%></h3>
					                    <div class="box-tools pull-right">
					                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
					                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
					                    </div>
					                </div>
					                <!-- /.box-header -->
					                <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
					                    <table  class="table table_no_border autoWidth">
                                                <tr>
                                                    <td class="alignRight">Duration: </td>
                                                    <td class="textblue" valign="bottom"><%=innerList.get(2) + " Years"%></td>
                                                </tr>
                                                <tr>
                                                    <td class="alignRight">Completion Year: </td>
                                                    <td class="textblue" valign="bottom"><%=innerList.get(3)%></td>
                                                </tr>
                                                <tr>
                                                    <td class="alignRight">Grade: </td>
                                                    <td class="textblue" valign="bottom"><%=innerList.get(4)%></td>
                                                </tr>
                                            </table>
					                </div>
					                <!-- /.box-body -->
					            </div>
                            <% } %>
                            <% } else { %>
                            <table style="width:98%" class="table table_no_border autoWidth">
                                            <tr>
                                                <td class="nodata msg"><span>No Education information added</span></td>
                                            </tr>
                                        </table>
                            <% } %>
	                      </div>
	                    </div>
                        <br/>
                        <div class="about-item">
	                      <h3 class="about-header">Languages</h3>
	                      <div class="about-body">
	                        <table class="table table_no_border autoWidth">
                                            <% if (alLanguages != null && alLanguages.size() != 0) { %>
                                            <tr class="center">
                                                <td width="150px"><strong>Language</strong></td>
                                                <td width="150px"><strong>Read</strong></td>
                                                <td width="150px"><strong>Write</strong></td>
                                                <td width="150px"><strong>Speak</strong></td>
                                            </tr>
                                            <%
                                                for (int i = 0; i < alLanguages.size(); i++) {
                                                List<String> alInner = alLanguages.get(i);
                                                %>
                                            <tr>
                                                <td class="textblue" valign="bottom"><strong><%=alInner.get(1)%></strong></td>
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
                                            </tr>
                                            <% } } else { %>
                                            <tr>
                                                <td class="nodata msg"><span>No languages added</span></td>
                                            </tr>
                                            <% } %>
                                        </table>
	                      </div>
	                    </div>
                        <br/>
                        
                <!-- ===start parvez date: 06-08-2022=== -->        
                    <% if(!uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_EMPLOYEE_HOBBIES_DISABLE))){ %>    
                        <div class="about-item">
	                      <h3 class="about-header">Hobbies</h3>
	                      <div class="about-body">
	                        <table class="table table_no_border autoWidth">
                              <% if (alHobbies != null && alHobbies.size() != 0) { %>
                                <tr>
                                   	<% for (int i = 0; i < alHobbies.size(); i++) {
                                           List<String> alInner = alHobbies.get(i);
                                    %>
                                      	<td class="textblue" valign="bottom"><strong><%=i < alHobbies.size() - 1 ? alInner.get(1) + " ," : alInner.get(1)%></strong></td>
                                    <% } %>
                                </tr>
                              <% } else { %>
                                   <tr>
                                     <td class="nodata msg"><span>No hobbies added</span></td>
                                   </tr>
                              <% } %>
                             </table>
	                      </div>
	                    </div>
	                <% } %>
	            <!-- ===end parvez date: 06-08-2022=== -->    
	                
                        <br/>
                        <div class="about-item">
	                      <h3 class="about-header">Previous Employment</h3>
	                      <div class="about-body">
	                        <% for(int i=0; alPrevEmployment != null && !alPrevEmployment.isEmpty() && i<alPrevEmployment.size(); i++) { 
                                        List<String> innerList = alPrevEmployment.get(i); 
                                        %>
                                <div class="box box-default collapsed-box" style="margin-top: 10px;">
					                <div class="box-header with-border">
					                    <h3 class="box-title" style="font-size: 14px;padding-right: 10px;"><%=uF.showData(innerList.get(1), "-") %></h3>
					                    <div class="box-tools pull-right">
					                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
					                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
					                    </div>
					                </div>
					                <!-- /.box-header -->
					                <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
					                    <table class="table table_no_border autoWidth">
                                            <tr>
                                                <td class="alignRight">Location:</td>
                                                <td class="textblue" valign="bottom"> <%=uF.showData(innerList.get(2), "-")%></td>
                                            </tr>
                                            <tr>
                                                <td class="alignRight">City:</td>
                                                <td class="textblue" valign="bottom"> <%=uF.showData(innerList.get(3), "-")%></td>
                                            </tr>
                                            <tr>
                                                <td class="alignRight">State:</td>
                                                <td class="textblue" valign="bottom"> <%=uF.showData(innerList.get(4), "-")%></td>
                                            </tr>
                                            <tr>
                                                <td class="alignRight">Country:</td>
                                                <td class="textblue" valign="bottom"> <%=uF.showData(innerList.get(5), "-")%></td>
                                            </tr>
                                            <tr>
                                                <td class="alignRight">Phone Number:</td>
                                                <td class="textblue" valign="bottom"> <%=uF.showData(innerList.get(6), "-")%></td>
                                            </tr>
                                            <tr>
                                                <td class="alignRight">Reporting To:</td>
                                                <td class="textblue" valign="bottom"> <%=uF.showData(innerList.get(7), "-")%></td>
                                            </tr>
                                            <tr>
                                                <td class="alignRight">Reporting Manager Ph. No.:</td>
                                                <td class="textblue" valign="bottom"> <%=uF.showData(innerList.get(13), "-")%></td>
                                            </tr>
                                            <tr>
                                                <td class="alignRight">HR Manager:</td>
                                                <td class="textblue" valign="bottom"> <%=uF.showData(innerList.get(14), "-")%></td>
                                            </tr>
                                            <tr>
                                                <td class="alignRight">HR Manager Ph. No.:</td>
                                                <td class="textblue" valign="bottom"> <%=uF.showData(innerList.get(15), "-")%></td>
                                            </tr>
                                            <tr>
                                                <td class="alignRight">From:</td>
                                                <td class="textblue" valign="bottom"> <%=uF.showData(innerList.get(8), "-")%></td>
                                            </tr>
                                            <tr>
                                                <td class="alignRight">To:</td>
                                                <td class="textblue" valign="bottom"> <%=uF.showData(innerList.get(9), "-")%></td>
                                            </tr>
                                            <tr>
                                                <td class="alignRight">Designation:</td>
                                                <td class="textblue" valign="bottom"> <%=uF.showData(innerList.get(10), "-")%></td>
                                            </tr>
                                            <tr>
                                                <td class="alignRight">Responsibility:</td>
                                                <td class="textblue" valign="bottom"> <%=uF.showData(innerList.get(11), "-")%></td>
                                            </tr>
                                            <tr>
                                                <td class="alignRight">Skills:</td>
                                                <td class="textblue" valign="bottom"> <%=uF.showData(innerList.get(12), "-")%></td>
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
                                                <td class="textblue" valign="bottom">Gross Amount: <%=uF.showData(hmEmpPrevEarnDeduct.get("PREV_COMP_GROSS_AMOUNT"), "-") %></td>
                                            </tr>
                                            <tr>
                                                <td class="alignRight">&nbsp;</td>
                                                <td class="textblue" valign="bottom">Tds Amount: <%=uF.showData(hmEmpPrevEarnDeduct.get("PREV_COMP_TDS_AMOUNT"), "-") %></td>
                                            </tr>
                                            <tr>
                                                <td class="alignRight">&nbsp;</td>
                                                <td class="textblue" valign="bottom">Please upload relevant document (form 16): 
                                                    <% if(hmEmpPrevEarnDeduct.get("PREV_COMP_DOC_NAME") != null && !hmEmpPrevEarnDeduct.get("PREV_COMP_DOC_NAME").equals("")) { %>
                                                    <a href="<%=request.getContextPath()+"/userDocuments/"+hmEmpPrevEarnDeduct.get("PREV_COMP_DOC_NAME")%>" >Download</a>
                                                    <% } else { %>
                                                    -
                                                    <% } %>
                                                </td>
                                            </tr>
                                        </table>
					                </div>
					                <!-- /.box-body -->
					            </div> 
                             <% } %>
                             <% if (alPrevEmployment == null || alPrevEmployment.isEmpty()) { %>
                                 <div class="nodata msg" style="width: 96%"> <span>No previous employment</span> </div>
                             <% } %>
	                      </div>
	                    </div>
                        <br/>
                        <%
                            List<Map<String, String>> empRefList = (List<Map<String, String>>) request.getAttribute("empRefList");
                            if (empRefList != null && empRefList.size()>0) {
                            %>
                        <div class="about-item">
	                      <h3 class="about-header">Employee References</h3>
	                      <div class="about-body">
	                        <%
                                                for (int i = 0; empRefList != null && i < empRefList.size(); i++) {
                                                		Map<String, String> hmInner = (Map<String, String>) empRefList.get(i);
                                                		if (hmInner == null) hmInner = new HashMap<String, String>();
                                                %>
                                            <ul>
                                                <li><strong>Reference <%=(i + 1)%></strong></li>
                                                <li>
                                                    <table class="table table_no_border autoWidth">
                                                        <tr>
                                                            <td class="alignRight">Reference Name:</td>
                                                            <td class="textblue" valign="bottom"><%=uF.showData(hmInner.get("REF_NAME"), "-")%></td>
                                                        </tr>
                                                        <tr>
                                                            <td class="alignRight">Company:</td>
                                                            <td class="textblue" valign="bottom"><%=uF.showData(hmInner.get("REF_COMPANY"), "-")%></td>
                                                        </tr>
                                                        <tr>
                                                            <td class="alignRight">Designation:</td>
                                                            <td class="textblue" valign="bottom"><%=uF.showData(hmInner.get("REF_DESIGNATION"), "-")%></td>
                                                        </tr>
                                                        <tr>
                                                            <td class="alignRight">Contact No.:</td>
                                                            <td class="textblue" valign="bottom"><%=uF.showData(hmInner.get("REF_CONTACT_NO"), "-")%></td>
                                                        </tr>
                                                        <tr>
                                                            <td class="alignRight">Email:</td>
                                                            <td class="textblue" valign="bottom"><%=uF.showData(hmInner.get("REF_EMAIL"), "-")%></td>
                                                        </tr>
                                                    </table>
                                                </li>
                                            </ul>
                                            <% } %>
	                      </div>
	                    </div>
                        <% } %>
                        <br/>
                        
             <!-- ===start parvez date: 06-09-2022=== -->             
                   <% if(!uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_DISABLE_EMPLOYEE_FAMILY_INFORMATION))){ %>     
                        <div class="about-item">
	                      <h3 class="about-header">Family Information</h3>
	                      <div class="about-body">
	                        <%
                                                if (alFamilyMembers != null && alFamilyMembers.size() != 0) {
                                                
                                                	for (int i = 0; i < alFamilyMembers.size(); i++) {
                                                		List<String> innerList = alFamilyMembers.get(i);
                                                
                                                		if (innerList.get(1).length() != 0) {
                                                			if (innerList.get(8).equals("FATHER")) {
                                                %>
                                            <p class="past heading_dash" style="margin:0px 0px 10px 0px"><Strong>Father's Info</Strong></p>
                                            <% } else if (innerList.get(8).equals("MOTHER")) { %>
                                            <p class="past heading_dash" style="margin:0px 0px 10px 0px"><Strong>Mother's Info</Strong></p>
                                            <% } else if (innerList.get(8).equals("SPOUSE")) { %>
                                            <p class="past heading_dash" style="margin:0px 0px 10px 0px"><Strong>Spouse's Info</Strong></p>
                                            <% } else if (innerList.get(8).equals("SIBLING")) { %>
                                            <p class="past heading_dash" style="margin:0px 0px 10px 0px"><Strong>Sibling's Info</Strong></p>
                                            <% } else if (innerList.get(8).equals("CHILD")) { %>
                                            <p class="past heading_dash" style="margin:0px 0px 10px 0px"><Strong>Children's Info</Strong></p>
                                            <% } %>
                                            <div class="content1" style="margin:0px 0px 10px 0px">
                                                <table class="table table_no_border autoWidth">
                                                    <tr>
                                                        <td class="alignRight">Name: </td>
                                                        <td class="textblue" valign="bottom"><%=innerList.get(1)%></td>
                                                    </tr>
                                                    <tr>
                                                        <td class="alignRight">Date Of Birth: </td>
                                                        <td class="textblue" valign="bottom"><%=innerList.get(2)%></td>
                                                    </tr>
                                                    <tr>
                                                        <td class="alignRight">Education: </td>
                                                        <td class="textblue" valign="bottom"><%=innerList.get(3)%></td>
                                                    </tr>
                                                    <tr>
                                                        <td class="alignRight">Occupation: </td>
                                                        <td class="textblue" valign="bottom"><%=innerList.get(4)%></td>
                                                    </tr>
                                                    <tr>
                                                        <td class="alignRight">Contact No: </td>
                                                        <td class="textblue" valign="bottom"><%=innerList.get(5)%></td>
                                                    </tr>
                                                    <tr>
                                                        <td class="alignRight">Email Id: </td>
                                                        <td class="textblue" valign="bottom"><%=innerList.get(6)%></td>
                                                    </tr>
                                                    <% if (innerList.get(8).equals("SPOUSE") || innerList.get(8).equals("CHILD") || innerList.get(8).equals("SIBLING")) { %>
                                                    <tr>
                                                        <td class="alignRight">Gender: </td>
                                                        <td class="textblue" valign="bottom"><%=innerList.get(7)%></td>
                                                    </tr>
                                                    <% } %>
                                                    <% if (innerList.get(8).equals("CHILD") || innerList.get(8).equals("SIBLING")) { %>
                                                    <tr>
                                                        <td class="alignRight">Marital Status: </td>
                                                        <td class="textblue" valign="bottom"><%=innerList.get(9)%></td>
                                                    </tr>
                                                    <% } %>
                                                </table>
                                            </div>
                                            <% } }
                                                } else {
                                                %>
                                            <table class="table table_no_border autoWidth">
                                                <tr>
                                                    <td class="nodata msg"><span>No Family members added</span></td>
                                                </tr>
                                            </table>
                                            <% } %>
	                      </div>
	                    </div>
                        <br/>
                    <% } %>   
                     
                    <% if(!uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_DISABLE_MEDICAL_DETAILS))){ %>    
                        <div class="about-item">
                        	<%
                                List<List<String>> alMedicalDetails = (List<List<String>>) request.getAttribute("alMedicalDetails");
                                Map<String, String> medicalQuest = (Map<String, String>) request.getAttribute("medicalQuest");
                                %>
	                      <h3 class="about-header">Medical Details</h3>
	                      <div class="about-body">
	                        <table class="table table_no_border autoWidth">
                                        <% if (alMedicalDetails != null && alMedicalDetails.size() != 0) { %>
                                        <% for (int i = 0,j=0; i < alMedicalDetails.size(); i++) {
                                            List<String> alInner = alMedicalDetails.get(i);
                                            if(medicalQuest.get(alInner.get(0)) != null && !medicalQuest.get(alInner.get(0)).equals("")) {	
                                            %>
                                        <tr>
                                            <td style="width:70%">
                                                <div style="float:left;width:10px;font-weight:bold;padding-right:25px;"><%=++j%>.&nbsp;&nbsp;&nbsp;</div>
                                                <div style="float:left;width:90%;"><%=medicalQuest.get(alInner.get(0)) %></div>
                                            </td>
                                            <% if (uF.parseToBoolean(alInner.get(1))) { %>
                                            <td class="textblue yes" style="width:10%"></td>
                                            <% } else { %>
                                            <td class="textblue no" style="width:10%"></td>
                                            <% } %>
                                            <% if (alInner.get(3) != null) { %>
                                            <td style="width:20%" class="alignRight">
                                                <%-- <a href="<%=request.getContextPath()+"/userDocuments/"+alInner.get(3)%>" >Download</a> --%> 
                                                <%if(docRetriveLocation == null) { %>
                                                <a target="blank" style="float: left; padding-top: 5px;" href="<%=request.getContextPath()+ IConstants.DOCUMENT_LOCATION + alInner.get(3)  %>" title="Medical Document" ><i class="fa fa-file-o" aria-hidden="true"></i></a>
                                                <%} else { %>
                                                <a target="blank" style="float: left; padding-top: 5px;" href="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_DOCUMENT+"/"+IConstants.I_MEDICAL+"/"+strProID+"/"+ alInner.get(3)  %>" title="Medical Document" ><i class="fa fa-file-o" aria-hidden="true"></i></a>
                                                <%} %>
                                            </td>
                                            <% } %>
                                        </tr>
                                        <% if (uF.parseToBoolean(alInner.get(1))) { %>
                                        <tr>
                                            <td class="textblue" valign="bottom"><strong><%=alInner.get(2)%></strong></td>
                                        </tr>
                                        <% } %>
                                        <% } } %>
                                        <% } else { %>
                                        <tr>
                                            <td class="nodata msg"><span>No Medical Details added</span></td>
                                        </tr>
                                        <% } %>
                                    </table>
	                      </div>
	                    </div>
                        <br/>
                     <% } %>   
                <!-- ===end parvez date: 06-09-2022=== -->     
                        
                        <div class="about-item">
	                      <h3 class="about-header">Supporting Documents</h3>
	                      <div class="about-body">
	                        <table class="table table_no_border autoWidth">
                                            <%
                                                if (alDocuments != null && alDocuments.size() != 0) {
                                                	for (int i = 0; i < alDocuments.size(); i++) {
                                                %>
                                            <tr>
                                                <td class="alignRight"> <%=((ArrayList) alDocuments.get(i)).get(1)%> </td>
                                                <td class="textblue" valign="bottom">-</td>
                                                <td class="alignRight"> 
                                                    <% if (((ArrayList)alDocuments.get(i)).get(4) != null) { %>
                                                    <%-- <a href="<%=request.getContextPath()+"/userDocuments/"+((ArrayList)alDocuments.get(i)).get(4)%>" >Download</a> --%>
                                                    <%if(docRetriveLocation == null) { %>
                                                    <a href="<%=request.getContextPath()+ IConstants.DOCUMENT_LOCATION + ((ArrayList)alDocuments.get(i)).get(4)  %>" title="Reference Document" ><i class="fa fa-file-o" aria-hidden="true"></i></a>
                                                    <%} else { %>
                                                    <a href="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_DOCUMENT+"/"+IConstants.I_ATTACHMENT+"/"+strProID+"/"+ ((ArrayList)alDocuments.get(i)).get(4)  %>" title="Reference Document" ><i class="fa fa-file-o" aria-hidden="true"></i></a>
                                                    <%} %>
                                                    <%} %>
                                                </td>
                                                <td class="textblue" valign="bottom">-</td>
                                            </tr>
                                            <% } } else { %>
                                            <tr>
                                                <td colspan="4" class="nodata msg"><span>No Documents attached</span></td>
                                            </tr>
                                            <% } %>
                                        </table>
	                      </div>
	                    </div>
                    </div>
                    <!-- /.tab-pane -->
                    <div class="tab-pane" id="timeline">
                        <!-- The timeline -->
                        <div style="margin-top: 20px;"></div>

                        <%if (alActivityDetails !=null && alActivityDetails.size()>0) {
                            String strDateTmp = "";%>
                        <ul class="timeline timeline-inverse">
                            <!-- timeline time label -->
                            <%
                                for (int i = 0; i < alActivityDetails.size(); i++) {
                                	List<String> innerList = alActivityDetails.get(i);
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
                            <li>
                                <i class="fa fa-envelope bg-blue"></i>
                                <div class="timeline-item">
                                    <h3 class="timeline-header"><%=innerList.get(5)%> <%=(nDoc == 0) ? "" : "<a href=\"DownloadDocument.action?doc_id=" + nDoc+ "\"><i class=\"fa fa-file-pdf-o\" aria-hidden=\"true\" style=\"margin-left:5px;height:16px;float:right;\" ></i></a>"%></h3>
                                    <div class="timeline-body">
                                        <%=strMsg %>
                                    </div>
                                </div>
                            </li>
                            <%}else{ %>
                            <li class="time-label">
                                <span class="bg-red">
                                <%=strDate %>
                                </span>
                            </li>
                            <!-- /.timeline-label -->
                            <!-- timeline item -->
                            <li>
                                <i class="fa fa-envelope bg-blue"></i>
                                <div class="timeline-item">
                                    <h3 class="timeline-header"><%=innerList.get(5)%> <%=(nDoc == 0) ? "" : "<a href=\"DownloadDocument.action?doc_id=" + nDoc+ "\"><i class=\"fa fa-file-pdf-o\" aria-hidden=\"true\" style=\"margin-left:5px;height:16px;float:right;\"  ></i></a>"%></h3>
                                    <div class="timeline-body">
                                        <%=strMsg %>
                                    </div>
                                </div>
                            </li>
                            <%} 
                                strDateTmp = strDate; 
                                } %>
                            <!-- /.timeline-label -->
                            <!-- timeline item -->
                            <li>
                                <div class="timeline-item">
                                    <h3 class="timeline-header bg-green">Current Employment Details</h3>
                                    <div class="timeline-body">
                                        <p>Joining Date: <%=uF.showData(hmEmpProfile.get("JOINING_DATE"), "-")%></p>
                                        <p>Designation: <%=uF.showData(hmEmpProfile.get("DESIGNATION_NAME"), "-")%></p>
                                    </div>
                                </div>
                            </li>
                            <%if (alPrevEmployment !=null && alPrevEmployment.size()>0) {
                                for (int i = 0; i < alPrevEmployment.size(); i++) {
                                	List<String> innerList = alPrevEmployment.get(i);
                                	String strColor = "#ececec";
                                	String strMsg = "From "+innerList.get(8)+" To "+innerList.get(9)+" "+((innerList.get(6) != null && innerList.get(6).length() != 0) ? "with" + innerList.get(6) + "designation" : "");
                                	String strMsg1 = (innerList.get(12) != null && innerList.get(12).length() != 0) ? "Skills: " + innerList.get(12) : "";
                                %>
                            <li>
                                <i class="fa fa-envelope bg-blue"></i>
                                <div class="timeline-item">
                                    <h3 class="timeline-header"><%=innerList.get(1)%></h3>
                                    <div class="timeline-body">
                                        <%=strMsg %><br/><%=strMsg1 %>
                                    </div>
                                </div>
                            </li>
                            <%} %>
                            <!-- cd-timeline -->
                            <%} else { %>
                            <li>
                                <i class="fa fa-envelope bg-blue"></i>
                                <div class="timeline-item">
                                    <h3 class="timeline-header">No Previous Employment</h3>
                                    <div class="timeline-body">
                                        Click on the edit to add Previous Employment
                                    </div>
                                </div>
                            </li>
                            <%} %>
                            <li>
                                <i class="fa fa-clock-o bg-gray"></i>
                            </li>
                        </ul>
                        <%} %>
                    </div>
                    <!-- /.tab-pane -->
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
            <div class="modal-body" style="height:400px;overflow-y:auto;padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>
<div class="modal" id="editPhoto" role="dialog">
    <div class="modal-dialog">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">Edit Photo</h4>
            </div>
            <div class="modal-body" style="height:400px;overflow-y:auto;padding-left: 25px;">
                <div id="uploadPhotoDiv" style="float:left;padding:5px; width:98%;">
                    <input type="file" name="empImage" id="empImage" style="margin-bottom: 10px;"> 
                    <div style="font-size: 11px; margin-top: -10px; width: 100%;">Ensure the height x width are the same. eg.100px x 100px.</div>
                    <div id="message"></div>
                    <input type="hidden" name="empId" id="empId1" value="<%=strProID%>"/>
                    <input type="button" name="submit" id="upload" value="Upload" class="btn btn-primary" style="margin-top: 20px;"/> 
                    <input type="button" name="cancel" value="Cancel" onclick="hideEditPhoto();" class="btn btn-danger" style="margin-top: 20px;"/>
                    <p><span id="progress" class="progress">0%</span></p>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>

<script>
    $(function(){
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
        		totAllAttribMarks += uF.parseToDouble(hmScoreAggregateMap.get(attributeList1.get(0).trim()));
        		count++;
        	}
        }
        aggregeteMarks = totAllAttribMarks / count;
        
        dblScorePrimary = aggregeteMarks / 20; 
        %>
    	$('#skillPrimaryOverall').raty({
    		  readOnly: true,
    		  start:    <%=dblScorePrimary%>,
    		  half: true
    		});
    	
    
    <% } %>   
    });
</script>