<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<script type="text/javascript" charset="utf-8">
 $(function () {
	 $('#lt').DataTable({
		"order": [],
		"columnDefs": [ {
		      "targets"  : 'no-sort',
		      "orderable": false
		    }],
		'dom': 'lBfrtip',
        'buttons': [
			'copy', 'csv', 'excel', 'pdf', 'print'
        ]
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

	$("#f_strWLocation").multiselect().multiselectfilter();
	$("#f_department").multiselect().multiselectfilter();
	
 });

 
 function updateApproveDenyStatus(status, approveType, offBoardId,userType) {
	 var divResult = 'divResult';
		var strBaseUserType = document.getElementById("strBaseUserType").value;
		var strCEO = '<%=IConstants.CEO %>';
		var strHOD = '<%=IConstants.HOD %>';
		
		if(strBaseUserType == strCEO || strBaseUserType == strHOD) {
			divResult = 'subDivResult';
		}
		var currUserType = document.getElementById("currUserType").value;
	 
	 var denyApprove = "Approve";
	 if(status == '-1') {
		 denyApprove = "Deny";
	 }
	 
	 var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 $(".modal-title").html('Resignation '+ denyApprove + ' Reason');
	 $.ajax({
		url : 'UpdateRequest.action?S='+status+'&M='+approveType+'&RID='+offBoardId+'&T=REG&userType='+userType
				+'&strDivResult='+divResult+'&currUserType='+currUserType+'&type=TR', 
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}
 
 
function exitFeedbackFormsDashboard() {
		
	 var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 $(".modal-title").html('Exit Feedback Forms');
	 $.ajax({
		url : "ExitFeedbackForms.action",
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}
 
	function exitFeedBackPDF(id,resignId) {
		 var dialogEdit = '.modal-body';
		 $(dialogEdit).empty();
		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		 $("#modalInfo").show();
		 $(".modal-title").html('Exit Feedback PDF');
		 $.ajax({
			url : "ExitFeedBackPdf.action?id="+id+"&resignId="+resignId,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}
	 
function getApprovalStatus(id,empname){
	var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 $(".modal-title").html('Work flow of '+empname);
	 $.ajax({
		url : "GetLeaveApprovalStatus.action?effectiveid="+id+"&type=10",
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

function submitForm(type){
	var divResult = 'subDivResult';
	var strBaseUserType = document.getElementById("strBaseUserType").value;
	var strCEO = '<%=IConstants.CEO %>';
	var strHOD = '<%=IConstants.HOD %>';
	
	if(strBaseUserType == strCEO || strBaseUserType == strHOD) {
		divResult = 'subDivResult';
	}
	var currUserType = document.getElementById("currUserType").value;
	var org = "";
	var location = "";
	var department = "";
	if(document.getElementById("f_org")) {
		org = document.getElementById("f_org").value;
	}
	if(document.getElementById("f_strWLocation")) {
		location = getSelectedValue("f_strWLocation");
	}
	if(document.getElementById("f_department")) {
		department = getSelectedValue("f_department");
	}
	var paramValues = "";
	if(type == '2') {
		paramValues = '&strLocation='+location+'&strDepartment='+department;
	}
	$("#"+divResult).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'ResignationReport.action?f_org='+org+'&currUserType='+currUserType+paramValues,
		data: $("#"+this.id).serialize(),
		success: function(result){
        	$("#"+divResult).html(result);
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

</script>

<!-- Custom form for adding new records -->

<%-- <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="<%=IMessages.TResignationReport %>" name="title"/>
</jsp:include> --%>

<%
		CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions); 
		UtilityFunctions uF = new UtilityFunctions();
		String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
		
		String strUserType = (String)session.getAttribute(IConstants.USERTYPE);
		String currUserType = (String) request.getAttribute("currUserType");
		String strBaseUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
	%>
	


	<%-- <% if(strUserType != null && strUserType.equals(IConstants.MANAGER) && strBaseUserType != null && (strBaseUserType.equals(IConstants.CEO) || strBaseUserType.equals(IConstants.HOD))) { %>
		<div style="width: 100%;">
			<ul class="nav nav-pills">
				<li class="<%=(currUserType == null || currUserType.equals("MYTEAM")) ? "active" : "" %>" style="border-radius: 10px 10px 0px 0px; <%=(currUserType == null || currUserType.equals("MYTEAM")) ? "" : "background-color: rgb(226, 226, 226)" %>;">
					<a href="ResignationReport.action?currUserType=MYTEAM" style="padding: 4px 15px !important;border-radius: 10px 10px 0px 0px;">My Team</a>
				</li>
				<li class="<%=(currUserType == null || currUserType.equals(strBaseUserType)) ? "active" : "" %>"  style="border-radius: 10px 10px 0px 0px; <%=(currUserType == null || currUserType.equals(strBaseUserType)) ? "" : "background-color: rgb(226, 226, 226)" %>;">
					<a href="ResignationReport.action?currUserType=<%=strBaseUserType %>" style="padding: 4px 15px !important;border-radius: 10px 10px 0px 0px;"><%=strBaseUserType %></a>
				</li>
			</ul>
		</div>
	<% } %> --%>
	
		<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
			<% if((strUserType!=null && !strUserType.equals(IConstants.MANAGER) && !strUserType.equals(IConstants.EMPLOYEE)) || (currUserType != null && currUserType.equals(strBaseUserType))) { %>
				<s:form name="frm_Resignation" action="ResignationReport" theme="simple" cssStyle="margin-bottom: 30px;">
					<input type="hidden" name="strBaseUserType" id=strBaseUserType value="<%=strBaseUserType %>"/>
					<s:hidden name="currUserType" id="currUserType"/>
					<div class="box box-default collapsed-box">
						<div class="box-header with-border">
						    <h3 class="box-title"><%=(String)request.getAttribute("selectedFilter") %></h3>
						    <div class="box-tools pull-right">
						        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
						        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
						    </div>
						</div>
						<div class="box-body" style="padding: 5px; overflow-y: auto;">
							<div class="row row_without_margin">
								<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
									<i class="fa fa-filter"></i>
								</div>
								<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Organisation</p>
										<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1');" list="organisationList" />
									</div>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Location</p>
										<s:select theme="simple" name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" multiple="true" list="wLocationList" key="" />
									</div>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Department</p>
										<s:select name="f_department" id="f_department" list="departmentList" listKey="deptId" listValue="deptName" multiple="true" />
									</div>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">&nbsp;</p>
										<input type="button" name="Submit" value="Submit" class="btn btn-primary" onclick="submitForm('2');"/>
									</div>
								</div>
							</div>
						</div>
					</div>	
				</s:form>
			<% } else { %>
				<input type="hidden" name="strBaseUserType" id=strBaseUserType value="<%=strBaseUserType %>"/>
				<s:hidden name="currUserType" id="currUserType"/>
			<% } %>
			
			<table class="table table-striped table-bordered" id="lt" style="margin-top: 30px;">
				<thead>
					<tr>
						<th>Employee Name</th>
						<th>Status</th>
						<th>Applied on</th>
						<th>Reason</th>
						<th>Notice Days</th>
						<th>Last Day</th>
						<th class="no-sort">Status</th>
						<th>Approving Profile</th>
						<th class="no-sort">Workflow</th>
						<th class="no-sort">Full & Final</th>
					</tr>
				</thead>
				<tbody>
					<%java.util.List couterlist = (java.util.List) request.getAttribute("reportList");%>
					<%for (int i = 0; couterlist != null && i < couterlist.size(); i++) {%>
					<%java.util.List cinnerlist = (java.util.List) couterlist.get(i);%>
					<tr id="<%=cinnerlist.get(0)%>">
						<td>
						<%if(docRetriveLocation==null) { %>
	                        <img height="22" width="22" border="0" class="lazy img-circle" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + cinnerlist.get(11) %>" >
	                    <% }else{ %>
	                        <img height="22" width="22" border="0" class="lazy img-circle" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+cinnerlist.get(0)+"/"+IConstants.I_22x22+"/"+cinnerlist.get(11) %>">
	                    <% } %>
							<%=cinnerlist.get(1)%>
						</td>
						<td align="center"><%=cinnerlist.get(2)%></td>
						<td align="center"><%=cinnerlist.get(3)%></td>
						<td><%=cinnerlist.get(4)%></td>
						<td align="center"><%=cinnerlist.get(5)%></td>
						<td align="center"><%=cinnerlist.get(6)%></td>
						<td align="center"><%=cinnerlist.get(7)%></td>
						<td align="center"><%=cinnerlist.get(10)%></td>
						<td align="center"><%=cinnerlist.get(8)%></td>
						<td align="center"><%=cinnerlist.get(9)%></td>
					</tr>
					<%}%>
				</tbody>
			</table>


			<div class="custom-legends">
			  <div class="custom-legend pullout">
			    <div class="legend-info">Pull Out</div>
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
			  <div class="custom-legend re_submit">
			    <div class="legend-info">Waiting for workflow</div>
			  </div>
			  <br/>
			  <div class="custom-legend no-borderleft-for-legend">
			    <div class="legend-info"><i class="fa fa-check-circle checknew" aria-hidden="true"></i>Approve Resignation</div>
			  </div>
			  <div class="custom-legend no-borderleft-for-legend">
			    <div class="legend-info"><i class="fa fa-times-circle cross" aria-hidden="true"></i>Deny Resignation</div>
			  </div>
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
            <div class="modal-body" style="height:400px;overflow-y:auto;padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>


