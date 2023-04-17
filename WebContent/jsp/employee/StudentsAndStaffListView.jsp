<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<%
    String strUserType = (String) session.getAttribute(IConstants.USERTYPE);
    UtilityFunctions uF=new UtilityFunctions();
    /* EncryptionUtility eU = new EncryptionUtility(); */
    
    String fromPage = (String)request.getAttribute("fromPage");
    Map<String, String> hmFeatureStatus = (Map<String, String>)request.getAttribute("hmFeatureStatus");
    Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>)request.getAttribute("hmFeatureUserTypeId");
    String strUsertypeId = (String)session.getAttribute(IConstants.BASEUSERTYPEID);
    String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
    
    String isEmpLimit = (String)request.getAttribute("isEmpLimit");
    String strEmpLimit = (String)request.getAttribute("strEmpLimit");
%>

<%-- <script type="text/javascript" src="scripts/charts/jquery.min.js"></script> --%>
<script type="text/javascript">
	$(document).ready(function(){
		
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
	});

    function showAddEmpModes(value) {
    	//alert("value == >"+value);
    	var isEmpLimit = document.getElementById("isEmpLimit").value;
    	var strEmpLimit = document.getElementById("strEmpLimit").value;
    	if(isEmpLimit == 'true') {
    		alert('You have exceeded your user limit of '+strEmpLimit+'. Please contact accounts@workrig.com to add additional slab for continued uasage.');
		} else {
	    	if(value == 1) {
	    		window.location='AddEditStudentAndTeacher.action';
	    	}
		}
    }
</script>

<script type="text/javascript" charset="utf-8">
	$(document).ready(function() {
		$('#lt').DataTable({
			"order" : [],
			"columnDefs" : [ {
				"targets" : 'no-sort',
				"orderable" : false
			} ],
			'dom' : 'lBfrtip',
			'buttons' : [ 'copy', 'csv', 'excel', 'pdf', 'print' ]
		});

		$("#f_strWLocation").multiselect().multiselectfilter();
		$("#strClass").multiselect().multiselectfilter();
		
	});

	function getClasswiseDivision() {
		
		var orgId = document.getElementById("f_org").value;
		var levelIds = getSelectedValue('f_level');
		var xmlhttp = GetXmlHttpObject();
		if (xmlhttp == null) {
			alert("Browser does not support HTTP Request");
			return;
		} else {
			var xhr = $.ajax({
				url : 'GetGradeList.action?fromPage=filter&orgId='+orgId+'&levelIds='+levelIds,
				cache : false,
				success : function(data) {
					document.getElementById('myGrade').innerHTML = data;
					$("#f_grade").multiselect().multiselectfilter();
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
	
	function getSelectedValue(selectId) {
		var choice = document.getElementById(selectId);
		var exportchoice = "";
		for ( var i = 0, j = 0; i < choice.options.length; i++) {
			var value = choice.options[i].value;
			if(choice.options[i].selected == true && value != "") {
				
				if (j == 0) {
					exportchoice = "," + choice.options[i].value + ",";
					j++;
				} else {
					exportchoice += choice.options[i].value + ",";
					j++;
				}
			}else if(choice.options[i].selected == true && value == ""){
				exportchoice = "";
				break;
			}
		}
		return exportchoice;
	}
	
	
	function submitForm(type) {
		var org = document.getElementById("f_org").value;
		var userType = document.getElementById("userType").value;
		var location = getSelectedValue("f_strWLocation");
		var strClass = "";
		if (document.getElementById("strClass")) {
			status = getSelectedValue("strClass");
		}
		var paramValues = "";
		if (type == '2') {
			paramValues = '&strLocation=' + location + '&userType=' + userType + '&strstrClass=' + strClass;
		}
		$("#divResult").html(
				'<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url : 'StudentsAndStaffListView.action?f_org=' + org + paramValues,
			data : $("#" + this.id).serialize(),
			success : function(result) {
				$("#divResult").html(result);
			}
		});
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

	function getCheckedValue(advanceFilter) {
		var strFilter = document.getElementsByName(advanceFilter);
		var exportchoice = "";
		for ( var i = 0, j = 0; i < strFilter.length; i++) {
			if (strFilter[i].checked) {
				if (j == 0) {
					exportchoice = strFilter[i].value;
					j++;
				} else {
					exportchoice += "," + strFilter[i].value;
					j++;
				}
			}
		}
		return exportchoice;
	}
	

	function getViewData(type, event) {
		$(".view").removeClass("active");
		//$(event.target).parent().addClass("active");
		if (type === "list") {
			var action = "EmployeeReport.action";
			$("#btnList").addClass("active");
		} else if (type === "grid") {
			var action = "SearchEmployee.action?strFirstName=";
			$("#btnGrid").addClass("active");
		} else if (type === "org") {
			var action = "OrganisationalChart.action?fromPage=TS&divResult=changeViewDiv";
			$("#btnOrg").addClass("active");
		}
		$("#changeViewDiv").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'GET',
			url : action,
			success : function(result) {
				$("#changeViewDiv").html(result);
				if (type === "list") {
					$(".viewChangeButtons")[1].remove();
				}
			}
		});
	}
</script>

	<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
		<%=uF.showData((String)session.getAttribute(IConstants.MESSAGE),"") %>
		<%
		String userType = (String)request.getAttribute("userType");
		session.removeAttribute(IConstants.MESSAGE); 
		String strMessage = (String)request.getAttribute(IConstants.MESSAGE);
		if(strMessage == null) {
			strMessage = "";
		} %>
		<%=strMessage %>
	
		<div style="float: left; width: 98%; margin: 0px 0px; padding: 0px;">
			<%=uF.showData((String)request.getAttribute("sbMessage"), "") %>
		</div>
	
		<div id="changeViewDiv" class="margintop20">
			<% if(uF.parseToBoolean(isEmpLimit)) { %>
				<div class="callout callout-warning" style="margin-bottom: 0px; padding: 7px 10px; font-weight: 600;">
					Dear Customer, You have exceeded your user limit of <%=strEmpLimit %> employees. We have extended a grace period. Please contact <a href="mailto:accounts@workrig.com">accounts@workrig.com</a> to add additional slab for continued usage.
				</div>
			<% } %>
			<s:form name="frmStudentsAndStaffListView" action="StudentsAndStaffListView" theme="simple">
				<div class="box box-default" style="margin-top: -20px;"> <!--  collapsed-box -->
					<div class="box-body" style="padding: 5px; overflow-y: auto;"><!--  display: none; -->
						<div class="row row_without_margin">
							<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
								<i class="fa fa-filter"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Organization</p>
									<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1', 'ExEmployeeReport');" list="organisationList" key="" />
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Location</p>
									<s:select theme="simple" name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" key="" multiple="true" />
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Usertype</p>
									<s:select theme="simple" name="userType" id="userType" list="#{'1':'Staff'}" headerKey="0" headerValue="Student"></s:select>
								</div>
								<div id="classDiv" class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Class</p>
									<s:select theme="simple" name="strClass" id="strClass" list="classList" listKey="classDivId" listValue="classDivName" multiple="true"></s:select>
								</div>
								<%-- <div id="sectionDiv" class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Section</p>
									<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" listValue="levelCodeName" multiple="true" list="levelList" key=""  onchange="getLevelwiseGrade();" />
								</div> --%>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">&nbsp;</p>
									<input type="button" name="Submit" value="Submit" class="btn btn-primary" onclick="submitForm('2');" />
								</div>
							</div>
						</div>
						<br>
					</div>
				</div>
			</s:form>


				<% if (strUserType != null && (strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || 
                      strUserType.equalsIgnoreCase(IConstants.CEO) || strUserType.equalsIgnoreCase(IConstants.CFO))) {
                %>
				<div class="row row_without_margin" style="margin-bottom: 10px;">
					<div class="col-lg-6 col-md-6 col-sm-6 autoWidth">
						<input type="hidden" name="isEmpLimit" id="isEmpLimit" value="<%=isEmpLimit %>" />
						<input type="hidden" name="strEmpLimit" id="strEmpLimit" value="<%=strEmpLimit %>" />
						<select name="addEmpModes" id="addEmpModes" class="validateRequired" onchange="showAddEmpModes(this.value);">
							<option value="">Select Mode to Add User</option>
							<option value="1">Add New Student/Staff</option>
						</select>
					</div>
					<div class="col-lg-6 col-md-6 col-sm-6 autoWidth" style="float: right;">
						<% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_BULK_EMPLOYEE_ACTIVITY)) && hmFeatureUserTypeId.get(IConstants.F_BULK_EMPLOYEE_ACTIVITY).contains(strUsertypeId)) { %>
						<!-- <p style="margin-bottom: 15px; float: right;"><input type="submit" value="Bulk User Activity" class="btn btn-primary" /></p> -->
						<% } %>
					</div>
				</div>
				<% } %>

				<% if(uF.parseToInt(userType) == 0) { %>
				<table id="lt" class="table table-bordered" style="width: 100%; margin-top: 30px; clear: both;">
					<thead>
						<tr>
							<th style="text-align: left;">Student Id</th>
							<th style="text-align: left;">Student Name</th>
							<th style="text-align: left;">Class Name</th>
							<th style="text-align: left;">Section Name</th>
							<th style="text-align: left;">Father Name</th>
							<th style="text-align: left;">Mother Name</th>
							<th style="text-align: left;">Mail Id</th>
							<th style="text-align: left;">Admission Date</th>
							<th style="text-align: left;">Date of Birth</th>
							<th style="text-align: left;">Gender</th>
							<th style="text-align: left;" class="no-sort">Actions</th>
						</tr>
					</thead>
					<tbody>
						<%
                            java.util.List couterlist = (java.util.List) request.getAttribute("reportList");
                           	for (int i = 0; couterlist != null && i < couterlist.size(); i++) {
                           		java.util.List cinnerlist = (java.util.List) couterlist.get(i);
						%>
						<tr id=<%=cinnerlist.get(0)%>>
							<td><%=cinnerlist.get(1)%></td>
							<td>
								<%if(docRetriveLocation==null) { %>
									<img height="22" width="22" border="0" class="lazy img-circle" style="float: left; margin-right: 5px; border: 1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + cinnerlist.get(12) %>">
								<% } else { %>
									<img height="22" width="22" border="0" class="lazy img-circle" style="float: left; margin-right: 5px; border: 1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+cinnerlist.get(0)+"/"+IConstants.I_22x22+"/"+cinnerlist.get(12) %>">
								<% } %> <%=cinnerlist.get(2)%></td>
							<td><%=cinnerlist.get(3)%></td>
							<td><%=cinnerlist.get(4)%></td>
							<td><%=cinnerlist.get(5)%></td>
							<td><%=cinnerlist.get(6)%></td>
							<td><%=cinnerlist.get(7)%></td>
							<td><%=cinnerlist.get(8)%></td>
							<td><%=cinnerlist.get(9)%></td>
							<td><%=cinnerlist.get(10)%></td>
							<td><%=cinnerlist.get(11)%></td>
						</tr>
						<% } %>
					</tbody>
				</table>
				<% } else { %>
				<table id="lt" class="table table-bordered" style="width: 100%; margin-top: 30px; clear: both;">
					<thead>
						<tr>
							<th style="text-align: left;">User Type</th>
							<th style="text-align: left;">Staff Name</th>
							<th style="text-align: left;">Mail Id</th>
							<th style="text-align: left;">Gender</th>
							<th style="text-align: left;" class="no-sort">Actions</th>
						</tr>
					</thead>
					<tbody>
						<%
                            java.util.List couterlist = (java.util.List) request.getAttribute("reportList");
                           	for (int i = 0; couterlist != null && i < couterlist.size(); i++) {
                           		java.util.List cinnerlist = (java.util.List) couterlist.get(i);
						%>
						<tr id=<%=cinnerlist.get(0)%>>
							<td><%=cinnerlist.get(1)%></td>
							<td>
								<%if(docRetriveLocation==null) { %>
									<img height="22" width="22" border="0" class="lazy img-circle" style="float: left; margin-right: 5px; border: 1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + cinnerlist.get(6) %>">
								<% } else { %>
									<img height="22" width="22" border="0" class="lazy img-circle" style="float: left; margin-right: 5px; border: 1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+cinnerlist.get(0)+"/"+IConstants.I_22x22+"/"+cinnerlist.get(6) %>">
								<% } %> <%=cinnerlist.get(2)%></td>
							<td><%=cinnerlist.get(3)%></td>
							<td><%=cinnerlist.get(4)%></td>
							<td><%=cinnerlist.get(5)%></td>
						</tr>
						<% } %>
					</tbody>
				</table>
			<% } %>
	</div>
</div>
<!-- /.box-body -->

<div class="modal" id="modalInfo" role="dialog">
	<div class="modal-dialog">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">&times;</button>
				<h4 class="modal-title">-</h4>
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


<script>
	$("img.lazy").lazyload({
		threshold : 200,
		effect : "fadeIn",
		failure_limit : 10
	});
</script>

