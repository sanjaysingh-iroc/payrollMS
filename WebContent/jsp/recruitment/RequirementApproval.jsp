<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib uri="http://granule.com/tags" prefix="g"%>
<%-- <script src="scripts/ckeditor_cust/ckeditor.js"></script> --%>

<style>
    .list_req>li{
    padding-bottom: 5px;
    padding-top: 5px;
    border-bottom: 1px solid #F0F0F0;
    } 
    .list_req>li>span{
    top:0px;
    }
</style> 
<g:compress>
    <script>
        
        function denyRequest(ncount, RID, userType,currUserType) {
        	
        	if(document.getElementById("f_org"))
        		var orgID = document.getElementById("f_org").value;
        	if(document.getElementById("location"))
        		var wlocID = document.getElementById("location").value;
        	if(document.getElementById("designation"))
        	var desigID = document.getElementById("designation").value;
        	var checkStatus = document.getElementById("checkStatus").value;
        	var fdate = document.getElementById("fdate").value;
        	var tdate = document.getElementById("tdate").value;
        	
        	var dialogEdit = '.modal-body';
        	$(dialogEdit).empty();
        	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
        	$("#modalInfo").show();
        	$('.modal-title').html('Decline Reason');
        	$.ajax({
        		url : "DenyRequestPopUp.action?ST=-1&RID=" + RID+"&requestDeny=popup"+"&orgID="+orgID+"&wlocID="+wlocID+"&desigID="+desigID
        				+"&checkStatus="+checkStatus+"&fdate="+fdate+"&tdate="+tdate+"&userType="+userType+"&currUserType="+currUserType+"&frmPage=RAD",
        		cache : false,
        		success : function(data) {
        			$(dialogEdit).html(data);
        		}
        	});
        }
        
        
        function getDesignationDetails(desigId, desigName) {
        	var dialogEdit = '.modal-body';
        	$(dialogEdit).empty();
        	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
        	$("#modalInfo").show();
        	$('.modal-title').html(''+desigName);
        	$.ajax({
        		url : "DesignationDetails.action?desig_id=" + desigId,
        		cache : false,
        		success : function(data) {
        			$(dialogEdit).html(data);
        		}
        	});
        }
        
        
        function viewDenyReason(ncount, RID) {
        
	        var dialogEdit = '.modal-body';
	        $(dialogEdit).empty();
	        $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	        $("#modalInfo").show();
	        $('.modal-title').html('Decline Reason');
	        $.ajax({
	        	url : "DenyRequestPopUp.action?ST=-1&RID=" + RID
	        			+ "&requestDeny=view",
	        	cache : false,
	        	success : function(data) {
	        		$(dialogEdit).html(data);
	        	}
	        });
        }
        
        
        function addRequest(currUserType) {
        	
        	if(document.getElementById("f_org"))
        		var orgID = document.getElementById("f_org").value;
        	if(document.getElementById("location"))
        		var wlocID = document.getElementById("location").value;
        	if(document.getElementById("designation"))
        		var desigID = document.getElementById("designation").value;
        	var checkStatus = document.getElementById("checkStatus").value;
        	var fdate = document.getElementById("fdate").value;
        	var tdate = document.getElementById("tdate").value;
        	
        	var dialogEdit = '.modal-body';
        	$(dialogEdit).empty();
        	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
        	$("#modalInfo").show();
        	$('.modal-title').html('Job Requirement');
        	if($(window).width() >= 900){
	       		 $(".modal-dialog").width(900);
	       	 }
        	$.ajax({
        		url : 'RequirementRequest.action?orgID='+orgID+'&wlocID='+wlocID+'&desigID='+desigID+'&checkStatus='+checkStatus
        		+'&fdate='+fdate+'&tdate='+tdate+'&currUserType='+currUserType+'&frmPage=RAD',
        		cache : false,
        		success : function(data) {
        			$(dialogEdit).html(data);
        		}
        	});
        }
        
        
        function editRequest(ncount, RID,currUserType) {
          
        	if(document.getElementById("f_org"))
        		var orgID = document.getElementById("f_org").value;
        	if(document.getElementById("location"))
        		var wlocID = document.getElementById("location").value;
        	if(document.getElementById("designation"))
        	var desigID = document.getElementById("designation").value;
        	var checkStatus = document.getElementById("checkStatus").value;
        	var fdate = document.getElementById("fdate").value;
        	var tdate = document.getElementById("tdate").value;
        	  
        	var dialogEdit = '.modal-body';
        	$(dialogEdit).empty();
        	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
        	$("#modalInfo").show();
        	$('.modal-title').html('Edit Request');
        	if($(window).width() >= 900){
	       		 $(".modal-dialog").width(900);
	       	 }
        	$.ajax({
        		url : 'RequirementRequest.action?recruitmentID='+RID+'&orgID='+orgID+'&wlocID='+wlocID+'&desigID='
        				+desigID+'&checkStatus='+checkStatus+'&currUserType='+currUserType+'&frmPage=RAD&fdate='+fdate+'&tdate='+tdate,
        		
        		cache : false,
        		success : function(data) {
        			$(dialogEdit).html(data);
        		}
        	});
        }
        
        
      function closePopUP(currUserType){
        var recruitID=document.getElementById('recruitID').value;
        
         $('#ViewJobPorfile').dialog('close');
         
         editProfile(recruitID,currUserType);
         
        }	
        
        function editProfile(recruitId,currUserType) {
         var dialogEdit = '.modal-body';
        	$(dialogEdit).empty();
        	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
        	$("#modalInfo").show();
        	$('.modal-title').html('Job Profile');
        	$.ajax({					
        		url : "UpdateJobProfilePopUp.action?recruitID="+recruitId+'&currUserType='+currUserType+'&frmPage=RAD',
        		cache : false,
        		success : function(data) {
        			$(dialogEdit).html(data);
        		}
        	});
        } 
         
        
        function viewProfile(recruitid, currUserType) {
        	var dialogEdit = '.modal-body';
        	$(dialogEdit).empty();
        	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
        	$("#modalInfo").show();
        	$('.modal-title').html('Job Profile');
        	$.ajax({
        		url : 'ViewJobProfilesApprovalPopUp.action?recruitID='+ recruitid +'&view=view&frmPage=RAD&currUserType='+currUserType,
        		cache : false,
        		success : function(data) {
        			$(dialogEdit).html(data);
        		}
        	});
        }
  
        function viewProfileRequest(recruitid, currUserType) {
        	var dialogEdit = '.modal-body';
        	$(dialogEdit).empty();
        	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
        	$("#modalInfo").show();
        	$('.modal-title').html('Job Details');
        	$.ajax({
        		url : 'ViewJobProfilesApprovalPopUp.action?recruitID='+ recruitid +'&view=Request&frmPage=RAD&currUserType='+currUserType,
        		cache : false,
        		success : function(data) {
        			$(dialogEdit).html(data);
        		}
        	});
        }
        
        
        
        function addDesignation(recruitID, nCount, userType) {
        	var dialogEdit = '.modal-body';
        	$(dialogEdit).empty();
        	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
        	$("#modalInfo").show();
        	$('.modal-title').html('Update Designation');
        	$.ajax({
        		url : "AddCustomDesignation.action?recruitmentID="+recruitID+"&nCount="+nCount+"&userType="+userType,
        		cache : false,
        		success : function(data) {
        			$(dialogEdit).html(data);
        		}
        	});
        }
        
        
        function approveDesigAndRequest(nCount) {
        	if(confirm('Are you sure, you want to approve this designation?')){
        		var recruitmentID = document.getElementById("recruitmentID").value;
        		var nCount = document.getElementById("nCount").value;
        		var userType = document.getElementById("userType").value;
        		var strLevel = document.getElementById("strLevel").value;
            	var customDesignation = document.getElementById("customDesignation").value;
            	var customGrade = document.getElementById("customGrade").value;
            	var existingDesigCheck = document.getElementById("existingDesigCheck").value;
            	var strDesignationUpdate = document.getElementById("strDesignationUpdate").value;
            	var strGradeUpdate = document.getElementById("strGradeUpdate").value;
            	
       			xmlhttp = GetXmlHttpObject();
       		    if (xmlhttp == null) {
       				alert("Browser does not support HTTP Request");
       				return;
       		    } else {
       	            var xhr = $.ajax({
       	                url : 'AddCustomDesignation.action?recruitmentID='+recruitmentID+'&strLevel='+strLevel+'&customDesignation='+encodeURIComponent(customDesignation)
       	                		+'&customGrade='+encodeURIComponent(customGrade)+'&strDesignationUpdate='+strDesignationUpdate+'&strGradeUpdate='+strGradeUpdate
       	                		+'&ApproveSubmit=Approve&nCount='+nCount+'&userType='+userType,
       	                cache : false,
       	                success : function(data) {
       	                $("#modalInfo").hide();
       	               	if(data == "") {
       	               	} else {
       	               		var allData = data.split("::::");
       	               		document.getElementById("myDivStatus"+nCount).innerHTML = allData[0];
       	               		document.getElementById("myDivM"+nCount).innerHTML= "";
       	               		if(allData[1].length > 0) {
       	               			document.getElementById("myDivDesig"+nCount).innerHTML=allData[1];
       	               		}
	       	               	if(allData[2].length > 0) {
	   	               			document.getElementById("myDivM"+nCount).innerHTML=allData[2];
	   	               			/* "<a href=\"javascript:void(0)\" onclick=\"viewProfile('" + RID + "','"+currUserType+"')\">View Job Profile</a>"; */
	   	               		}
       	               	}
       	                }
       	            });
       		    }
        	}
        }
        
        
        function approveRequest(nCount,RID, userType,currUserType) {
        	//alert("empId==>"+empId);
        	if(confirm('Are you sure, you want to approve this request?')){
        		var reason = window.prompt("Please enter your approve reason.");
        		if (reason != null) {   
        			xmlhttp = GetXmlHttpObject();
        		    if (xmlhttp == null) {
        				alert("Browser does not support HTTP Request");
        				return;
        		    } else {
        	            var xhr = $.ajax({
        	                url : 'UpdateADRRequest.action?S=1&RID='+RID+'&mReason='+reason+'&userType='+userType+'&currUserType='+currUserType,
        	                cache : false,
        	                success : function(data) {
        	            	   //alert("data ===>> " + data);
        	               	if(data == "") {
        	               	} else {
        	               		var allData = data.split("::::");
        	               		document.getElementById("myDivStatus"+nCount).innerHTML = allData[0];
        	               		document.getElementById("myDivM"+nCount).innerHTML= "";
        	               		if(allData[1] == 1) {
        	               			document.getElementById("myDivM"+nCount).innerHTML="<a href=\"javascript:void(0)\" onclick=\"viewProfile('" + RID + "','"+currUserType+"')\">View & Approve Job Profile</a>";
        	               		}
        	               	}
        	                }
        	            });
        		    }
        		}
        	}
        }
        
    
        function addRequestWOWorkFlow(currUserType) {
        	/* document.getElementById("addRequirement").innerHTML=''; */
        	if(document.getElementById("f_org"))
        		var orgID = document.getElementById("f_org").value;
        	if(document.getElementById("location"))
        		var wlocID = document.getElementById("location").value;
        	if(document.getElementById("designation"))
        		var desigID = document.getElementById("designation").value;
        	var checkStatus = document.getElementById("checkStatus").value;
        	var fdate = document.getElementById("fdate").value;
        	var tdate = document.getElementById("tdate").value;
        	
        	var dialogEdit = '.modal-body';
        	$(dialogEdit).empty();
        	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
        	$("#modalInfo").show();
        	if($(window).width() >= 900){
	       		 $(".modal-dialog").width(900);
	       	 }
        	$('.modal-title').html('Job Requirement');
        	$.ajax({
        		url : 'RequirementRequestWithoutWorkflow.action?orgID='+orgID+'&wlocID='+wlocID+'&desigID='+desigID+'&checkStatus='+checkStatus
        			+'&fdate='+fdate+'&tdate='+tdate+'&currUserType='+currUserType+'&frmPage=RAD',
        		cache : false,
        		success : function(data) {
        			$(dialogEdit).html(data);
        		}
        	});
        }
        
        
        function editRequestWOWorkflow(ncount, RID,currUserType) {
        	  
        	if(document.getElementById("f_org"))
        		var orgID = document.getElementById("f_org").value;
        	if(document.getElementById("location"))
        		var wlocID = document.getElementById("location").value;
        	if(document.getElementById("designation"))
        	var desigID = document.getElementById("designation").value;
        	var checkStatus = document.getElementById("checkStatus").value;
        	var fdate = document.getElementById("fdate").value;
        	var tdate = document.getElementById("tdate").value;
        	
        	var dialogEdit = '.modal-body';
        	$(dialogEdit).empty();
        	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
        	$("#modalInfo").show();
        	$('.modal-title').html('Edit Request');
        	$.ajax({
        		url : 'RequirementRequestWithoutWorkflow.action?recruitmentID='+RID+'&orgID='+orgID+'&wlocID='+wlocID+'&desigID='+desigID+'&checkStatus='+checkStatus
        			+'&fdate='+fdate+'&tdate='+tdate+'&frmPage=RAD&currUserType='+currUserType,
        		cache : false,
        		success : function(data) {
        			$(dialogEdit).html(data);
        		}
        	});
        }
        
        
        function getApprovalStatus(id){
        	var dialogEdit = '.modal-body';
        	$(dialogEdit).empty();
        	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
        	$("#modalInfo").show();
        	$('.modal-title').html('Work flow');
        	$.ajax({
        		url : "GetLeaveApprovalStatus.action?effectiveid="+id+"&type=11",
        		cache : false,
        		success : function(data) {
        			$(dialogEdit).html(data);
        		}
        	});
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
        
    	function denyProfile(ncount, RID,currUserType) {
    		
        	var dialogEdit = '.modal-body';
        	$(dialogEdit).empty();
        	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
        	$("#modalInfo").show();
        	$('.modal-title').html('Decline Profile');
        	$.ajax({
        		url : 'JobDenyPopUp.action?ST=-1&RID=' + RID+'&requestDeny=popup&frmPage=RAD&currUserType='+currUserType,
				cache : false,
				success : function(data) {
					$(dialogEdit).html(data);
				}
        	});
    		
    	}
    </script>
</g:compress>
<!-- Custom form for adding new records -->
   
	<%
	    UtilityFunctions uF = new UtilityFunctions();
	    String strUserType = (String) session.getAttribute("USERTYPE");
		String currUserType = (String) request.getAttribute("currUserType");
		String strBaseUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
	%>
<%-- <section class="content">
	 <div class="row jscroll">
        <section class="col-lg-12 connectedSortable">
        	<% if(strUserType != null && strUserType.equals(IConstants.MANAGER) && strBaseUserType != null && (strBaseUserType.equals(IConstants.CEO) || strBaseUserType.equals(IConstants.HOD))) { %>
				<div class="box box-none nav-tabs-custom" style="padding: 5px; overflow-y: auto; min-height: 600px;">
				<ul class="nav nav-tabs">
					<li class="<%=(currUserType == null || currUserType.equals("MYTEAM")) ? "active" : "" %>"><a href="javascript:void(0)" onclick="window.location='RequirementApproval.action?currUserType=MYTEAM'" data-toggle="tab">My Team</a></li>
					<li class="<%=(currUserType == null || currUserType.equals(strBaseUserType)) ? "active" : "" %>"><a href="javascript:void(0)" onclick="window.location='RequirementApproval.action?currUserType=<%=strBaseUserType %>'" data-toggle="tab"><%=strBaseUserType %></a></li>
				</ul>
			<% }else{ %>
               <div class="box box-primary" style="padding: 5px; overflow-y: auto; min-height: 600px;">
            <% } %> --%>
                     <s:form name="frm_RequirementApproval" action="RequirementApproval" theme="simple">
                     	<input type="hidden" name="currUserType" id=currUserType value="<%=currUserType %>"/>
                     	<input type="hidden" name="strBaseUserType" id=strBaseUserType value="<%=strBaseUserType %>"/>
                         <div class="box box-default collapsed-box">
						<div class="box-header with-border">
						    <h3 class="box-title"><%=(String)request.getAttribute("selectedFilter") %></h3>
						    <div class="box-tools pull-right">
						        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
						        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
						    </div>
						</div>
						<div class="box-body" style="padding: 5px; overflow-y: auto;">
							<% if((strUserType!=null && !strUserType.equals(IConstants.MANAGER) && !strUserType.equals(IConstants.EMPLOYEE)) || (strBaseUserType != null && strBaseUserType.equals(currUserType)) || (strUserType!=null && !strUserType.equals(IConstants.CEO) && !strUserType.equals(IConstants.EMPLOYEE)) ) { %>
							<div class="row row_without_margin">
								<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
									<i class="fa fa-filter"></i>					
								</div>
								<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Organization</p>
										<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" list="organisationList" key="" headerKey="" headerValue="All Organisations" onchange="submitForm('1');" value="strOrg" />
									</div>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Location</p>
										<s:select theme="simple" name="location" id="location" listKey="wLocationId" listValue="wLocationName" headerKey="" headerValue="All Locations" list="workLocationList" key="0" />
									</div>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Designation</p>
										<s:select theme="simple" name="designation" id="designation" listKey="desigId" listValue="desigCodeName" headerKey="0" headerValue="All Designations" list="desigList" key="" />
									</div>
								</div>
							</div><br>
							<% } %>
							<div class="row row_without_margin">
								<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
									<i class="fa fa-calendar"></i>
								</div>
								<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Status</p>
										<s:select theme="simple" name="checkStatus" id="checkStatus" headerKey="-2" headerValue="All Status" list="#{'1':'Approved', '-1':'Denied', '0':'Pending'}"/>
									</div>
									<% 
										String fdate = (String)request.getAttribute("fdate");
										String tdate = (String)request.getAttribute("tdate");
										if(fdate == null || fdate.equals("null") || fdate.equals("")) {
											 fdate = "";
										}
										if(tdate == null || tdate.equals("null") || tdate.equals("")) {
											 tdate = "";
										}
									%>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">From Date</p>
										<input type="text" name="fdate" id="fdate" style="width: 100px !important;" value="<%=fdate %>" />
									</div>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">To Date</p>
										<input type="text" name="tdate" id="tdate" style="width: 100px !important;" value="<%=tdate %>" />
									</div>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">&nbsp;</p>
										<input type="button" value="Search" class="btn btn-primary" onclick="submitForm('2');"/>
									</div>
								</div>
							</div>
						</div>
					</div>
                   </s:form>
                       
                       <%=uF.showData((String) session.getAttribute(IConstants.MESSAGE), "")%>
                       <% session.setAttribute(IConstants.MESSAGE, ""); %>
                       
                       <%
                           //System.out.println("strUserType ===> " + strUserType);
                           if(strUserType != null && (strUserType.equalsIgnoreCase(IConstants.MANAGER) || strUserType.equalsIgnoreCase(IConstants.ADMIN))
							|| strUserType.equalsIgnoreCase(IConstants.RECRUITER) || strUserType.equalsIgnoreCase(IConstants.HRMANAGER)) {
					%>
                       <div style="float: right; margin-bottom: 10px;">
                           <a href="javascript:void(0)" onclick="addRequest('<%=currUserType%>')"><input type="button" class="btn btn-primary" value="New Requirement with Approval"></a>
                           <% if (strUserType != null && !strUserType.equalsIgnoreCase(IConstants.MANAGER)) { %>
                           <a href="javascript:void(0)" onclick="addRequestWOWorkFlow('<%=currUserType%>')"><input type="button" class="btn btn-primary" value="New Requirement without Approval"> </a>
                           <% } %>
                       </div>
                       <ul style="clear: both; width: 99%; margin-bottom: 50px" class="list_req">
                           <%
                               List<String> proResRequestList = (List<String>) request.getAttribute("proResRequestList");
                               	for (int i = 0; proResRequestList != null && i < proResRequestList.size(); i++) {
						%>
                           <li class="list" style="float: left; width: 100%;"><%=proResRequestList.get(i)%></li>
                           <%
                             }
                             	if (proResRequestList == null || (proResRequestList != null && proResRequestList.size() == 0)) {
                           %>
                           <li class="nodata msg">No project resource request made yet.</li>
                           <% } %>
                       </ul>
                       
                       <ul style="clear: both; width: 99%; margin-bottom: 50px" class="list_req">
                           <%
                               java.util.List requestList = (java.util.List) request.getAttribute("requestList");
                               	for (int i = 0; requestList != null && i < requestList.size(); i++) {
						%>
                           <li class="list" style="float: left; width: 100%;"><%=requestList.get(i)%></li>
                           <%
                             }
                             	if (requestList == null || (requestList != null && requestList.size() == 0)) {
                           %>
                           <li class="nodata msg">No requirement application made yet.</li>
                           <% } %>
                       </ul>
                       <div class="custom-legends">
						  <div class="custom-legend pullout">
						    <div class="legend-info">Waiting for workflow</div>
						  </div>
						  <div class="custom-legend pending">
						    <div class="legend-info">Waiting for approval</div>
						  </div>
						  <div class="custom-legend approved">
						    <div class="legend-info">Approved</div>
						  </div>
						  <div class="custom-legend denied">
						    <div class="legend-info">Denied</div>
						  </div>
						  <%
                              if (strUserType != null && (strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.MANAGER)
                              	|| strUserType.equalsIgnoreCase(IConstants.CEO) || strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.RECRUITER))) {
                          %>
						  <br/>
						  <div class="custom-legend no-borderleft-for-legend">
						    <div class="legend-info">
						    	<i class="fa fa-check-circle checknew" aria-hidden="true"></i>Approve to create job profile
						    </div>
						  </div>
						  <div class="custom-legend no-borderleft-for-legend">
						    <div class="legend-info">
						    	<i class="fa fa-times-circle cross" aria-hidden="true"></i>Decline job requirement
						    </div>
						  </div>
						  <% } %>
						  <br/>
						  <div class="custom-legend no-borderleft-for-legend">
						    <div class="legend-info high">High Priority</div>
						  </div>
						  <div class="custom-legend no-borderleft-for-legend">
						    <div class="legend-info medium">Medium Priority</div>
						  </div>
						  <div class="custom-legend no-borderleft-for-legend">
						    <div class="legend-info low">Low Priority</div>
						  </div>
						</div>
                   <% } %>
               <!-- /.box-body -->
           <%-- </div>
       </section>
    </div>
</section> --%>


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


<script type="text/javascript" charset="utf-8">
    $(function(){
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
    
    	$("#fdate").datepicker({
            format: 'dd/mm/yyyy',
            autoclose: true
        }).on('changeDate', function (selected) {
            var minDate = new Date(selected.date.valueOf());
            $('#tdate').datepicker('setStartDate', minDate);
        });
        
        $("#tdate").datepicker({
        	format: 'dd/mm/yyyy',
        	autoclose: true
        }).on('changeDate', function (selected) {
                var minDate = new Date(selected.date.valueOf());
                $('#fdate').datepicker('setEndDate', minDate);
        });
    });
    
	function submitForm(type) {
		var currUserType = "<%=currUserType%>";
		if(document.getElementById("f_org"))
    		var org = document.getElementById("f_org").value;
    	if(document.getElementById("location"))
    		var location = document.getElementById("location").value;
    	if(document.getElementById("designation"))
			var designation = document.getElementById("designation").value;
		var checkStatus = document.getElementById("checkStatus").value;
		var fdate = document.getElementById("fdate").value;
		var tdate = document.getElementById("tdate").value;
		
		var divResult = 'divResult';
		var strBaseUserType = document.getElementById("strBaseUserType").value;
		var strCEO = '<%=IConstants.CEO %>';
		var strHOD = '<%=IConstants.HOD %>';
		
		if(strBaseUserType == strCEO || strBaseUserType == strHOD) {
			divResult = 'subDivResult';
		}
		
		var paramValues = "";
		
		if(type=='1'){
			paramValues = '&currUserType='+currUserType;
			}
		
		if(type != "" && type == '2') {
			paramValues = '&designation='+designation+'&location='+location+'&checkStatus='+checkStatus
			+'&fdate='+fdate+'&tdate='+tdate+'&currUserType='+currUserType;
		}
		
    	var action = 'RequirementApproval.action?f_org='+org+paramValues;
    	
    	$("#"+divResult).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$.ajax({
    		type : 'POST',
    		url: action,
    		success: function(result){
    			$("#"+divResult).html(result);
       		}
    	});
    	
    }
</script>