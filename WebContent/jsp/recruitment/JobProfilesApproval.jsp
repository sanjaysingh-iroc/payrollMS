<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib uri="http://granule.com/tags" prefix="g"%>
<script src="scripts/ckeditor_cust/ckeditor.js"></script>
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
<script type="text/javascript" src="scripts/charts/highcharts.js"></script>
<script type="text/javascript" src="scripts/charts/exporting.js"></script>
<g:compress>
    <link href="scripts/ckeditor/samples/sample.css" rel="stylesheet">
    <script>
        // The instanceReady event is fired, when an instance of CKEditor has finished
        // its initialization. 
        CKEDITOR.on( 'instanceReady', function( ev ) { 
        	if(document.getElementById( 'eMessage' )) {
        		document.getElementById( 'eMessage' ).innerHTML = 'Instance <code>' + ev.editor.name + '<\/code> loaded.';
        	}
        
        	// Show this sample buttons.
        	if(document.getElementById( 'eButtons' )){
        		document.getElementById( 'eButtons' ).style.display = 'block';
        	}
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
        	if(document.getElementById( 'eMessage' )) {
        		document.getElementById( 'eMessage' ).innerHTML = '<b>' + this.name + ' is focused </b>';
        	}
        }
        
        function onBlur() {
        	if(document.getElementById( 'eMessage' )) {
        		document.getElementById( 'eMessage' ).innerHTML = this.name + ' lost focus';
        	}
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
           
        function closePopUP(){
        var recruitID=document.getElementById('recruitID').value;
        
         $('#ViewJobPorfile').dialog('close');
         
         editProfile(recruitID);
         
        }	
        
        function editProfile(recruitId) {
         var dialogEdit = '.modal-body';
        	$(dialogEdit).empty();
        	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
        	$("#modalInfo").show();
        	$('.modal-title').html('Edit Job Profile');
        	var height = $(window).height()* 0.90;
    		var width = $(window).width()* 0.90;
    		$(".modal-dialog").css("height", height);
    		$(".modal-dialog").css("width", width);
    		$(".modal-dialog").css("max-height", height);
    		$(".modal-dialog").css("max-width", width);
        	$.ajax({					
        		url : "UpdateJobProfilePopUp.action?recruitID="+recruitId+'&frmPage=JPA',
        		cache : false,
        		success : function(data) {
        			$(dialogEdit).html(data);
        		}
        	});
        } 
         
        
        function viewProfile(recruitid) {
        	var dialogEdit = '.modal-body';
        	$(dialogEdit).empty();
        	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
        	$("#modalInfo").show();
        	$('.modal-title').html('View Job Profile');
        	$.ajax({
        		url : "ViewJobProfilesApprovalPopUp.action?recruitID="
        				+recruitid +"&view=view&frmPage=JPA",
        		cache : false,
        		success : function(data) {
        			$(dialogEdit).html(data);
        		}
        	});
        }	
        
		function denyProfile(ncount, RID) {
    		
        	var dialogEdit = '.modal-body';
        	$(dialogEdit).empty();
        	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
        	$("#modalInfo").show();
        	$('.modal-title').html('Decline Profile');
        	$.ajax({
        		url : 'JobDenyPopUp.action?ST=-1&RID=' + RID+'&requestDeny=popup&frmPage=JPA',
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
        
        function AddUpdateProfile(recruitId,from) {
			//alert("kasdjasd");
		    if(from == "" || from == "null" || from == "NULL") {
		    	from = "JPA";
		    }
			if(document.getElementById("f_org"))
				var orgID = document.getElementById("f_org").value;
			if(document.getElementById("location"))
				var wlocID = document.getElementById("location").value;
			if(document.getElementById("designation"))
				var desigID = document.getElementById("designation").value;
			var checkStatus = document.getElementById("checkStatus").value;
			var fdate = document.getElementById("fdate").value;
			var tdate = document.getElementById("tdate").value;		
		
		
			var id=document.getElementById("updateJobProfileDIV");
						
			var dialogEdit = '.modal-body';
        	$(dialogEdit).empty();
        	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
        	$("#modalInfo").show();
        	$('.modal-title').html('Job Profile');
        	$.ajax({
        		url : "UpdateJobProfilePopUp.action?recruitID="+recruitId+"&orgID="+orgID+"&wlocID="+wlocID
				+"&desigID="+desigID+"&checkStatus="+checkStatus+"&fdate="+fdate+"&tdate="+tdate+"&frmPage="+from,
				cache : false,
				success : function(data) {
					$(dialogEdit).html(data);
				}
        	});
			
		}
        

    	function reportJobProfilePopUp(recruitId) {
    		var dialogEdit = '.modal-body';
        	$(dialogEdit).empty();
        	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
        	$("#modalInfo").show();
        	$('.modal-title').html('Job Profile');
        	$.ajax({
        		url : "ReportJobProfilePopUp.action?view=openjobreport&recruitId="+recruitId ,
				cache : false,
				success : function(data) {
					$(dialogEdit).html(data);
				}
        	});
    		}
    	
    	function approveJob(nCount,RID){
    		//alert(nCount +" "+ RID);
    		if(confirm('Are you sure, you want to approve this profile?')){
    			getContent('myDivStatus'+ nCount, "JobADRequest.action?S=1&RID="+RID);
    			document.getElementById("myDivM"+nCount).innerHTML="<span style=\"color: #68AC3B;\"> Approved </span>";
    		}
    	}
    </script>
</g:compress>

<%
    UtilityFunctions uF = new UtilityFunctions();
    String strUserType = (String) session.getAttribute(IConstants.USERTYPE);
    String fromPage = (String) request.getAttribute("fromPage");
    %>
<%-- <section class="content">

    <div class="row jscroll">
        <section class="col-lg-12 connectedSortable">
            <div class="box box-primary">
                <div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 400px;"> --%>
                      <s:form name="frm_RequirementApproval" id="formId" action="JobProfilesApproval" theme="simple">
                         <input type="hidden" name="fromPage" id="fromPage" value="<%=fromPage%>"/>
                         <div class="box box-default collapsed-box">
							<div class="box-header with-border">
							    <h3 class="box-title"><%=(String)request.getAttribute("selectedFilter") %></h3>
							    <div class="box-tools pull-right">
							        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
							        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
							    </div>
							</div>
							<div class="box-body" style="padding: 5px; overflow-y: auto;">
								<% if(strUserType!=null && !strUserType.equals(IConstants.MANAGER) && !strUserType.equals(IConstants.EMPLOYEE)) { %>
								<div class="row row_without_margin">
									<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
										<i class="fa fa-filter"></i>
									</div>
									<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
										<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
											<p style="padding-left: 5px;">Organization</p>
											<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" list="organisationList" key="" headerKey="" headerValue="All Organisations" onchange="submitForm()" value="strOrg" />
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
											<input type="text" name="tdate" id="tdate" style="width: 100px !important;" value="<%=tdate %>"/>
										</div>
										<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
											<p style="padding-left: 5px;">&nbsp;</p>
											<input type="button" value="Search" class="btn btn-primary" onclick="submitForm();"/>
										</div>
									</div>
								</div>
							</div>
						</div> 
                            
					</s:form>
					<%=uF.showData((String) session.getAttribute(IConstants.MESSAGE), "")%>
					<% session.setAttribute(IConstants.MESSAGE, ""); %>
					
					<div class="col-lg-12 col-md-12 col-sm-12" style="margin: 0px 0px 10px 0px; text-align: right;">
						<div style="float: right;">
							<a style="height: 1px; width: 2px;" href="RecruitmentDashboard.action" title="Go to Requirements">Go to Requirements <i class="fa fa fa-forward" aria-hidden="true"></i></a>
						</div>
					</div>
					
                       <ul style="clear: both; width: 99%; margin-bottom: 50px" class="list_req">
                            <%
                                java.util.List requestList = (java.util.List) request.getAttribute("requestList");
                            	System.out.print("RequestList====>"+requestList.size());
                                	for (int i = 0; requestList != null && i < requestList.size(); i++) {
                                %>
                            <li class="list"><%=requestList.get(i)%></li>
                            <%
                                }
                                	if (requestList == null || (requestList != null && requestList.size() == 0)) {
                                %>
                            <li class="nodata msg">No requirement application made yet.</li>
                            <% } %>
                        </ul> 
                        
                        <%-- <table class="table table-bordered" id="lt">
								<!-- <thead>
									<tr>
										<th style="text-align: left;">Reimbursement</th>
									</tr>
								</thead> -->
								<tbody>
									<%
									 java.util.List requestList = (java.util.List) request.getAttribute("requestList");
	                            	System.out.print("RequestList====>"+requestList.size());
	                            	
	                            	if (requestList == null || (requestList != null && requestList.size() == 0)) {
	                                    %>
	                                <li class="nodata msg">No requirement application made yet.</li>
	                                <% } else 
	                                	for (int i = 0; requestList != null && i < requestList.size(); i++) {
	                                		System.out.print("RequestList element====>"+requestList.get(i));	
	                                %>
	                           		 <tr>
											<td><%=requestList.get(i)%></td>
										</tr>
	                              <%
	                                }
									%>
								</tbody>
						</table> --%>
                        
                        
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
               <%--  </div>
                <!-- /.box-body -->
            </div>
        </section>
    </div>
</section> --%>
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
    
	function submitForm() {
		
		if(document.getElementById("f_org"))
			var org = document.getElementById("f_org").value;
		if(document.getElementById("location"))
			var location = document.getElementById("location").value;
		if(document.getElementById("designation"))
			var designation = document.getElementById("designation").value;
		var checkStatus = document.getElementById("checkStatus").value;
		var fdate = document.getElementById("fdate").value;
		var tdate = document.getElementById("tdate").value;
		var fromPage = document.getElementById("fromPage").value;
		var divResult = 'divResult';
		if(fromPage != null && fromPage == 'WF') {
			divResult = 'divWFResult';
		}
		var paramValues = '&designation='+designation+'&location='+location+'&checkStatus='+checkStatus
		+'&fdate='+fdate+'&tdate='+tdate+'&fromPage='+fromPage;
		
    	var action = 'JobProfilesApproval.action?f_org='+org+paramValues;
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