<%@page import="java.util.Iterator"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="java.util.List"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%
CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
UtilityFunctions uF =  new UtilityFunctions();
Map<String, String> hmMemberOption = (Map<String, String>) request.getAttribute("hmMemberOption");
if(hmMemberOption == null) hmMemberOption=new LinkedHashMap<String,String>();
String policy_id = (String) request.getAttribute("policy_id");

String strE = (String)request.getParameter("E"); %> 
<%-- <script type="text/javascript" src="scripts/charts/jquery.min.js"></script> --%>
 
<script type="text/javascript" charset="utf-8">

$(document).ready(function() {
	$("body").on('click','#closeButton',function() {
		$(".modal-dialog").removeAttr('style');
		$(".modal-body").height(400);
		$("#modalInfo").hide();
    });
	$("body").on('click','.close',function() {
		$(".modal-dialog").removeAttr('style');
		$(".modal-body").height(400);
		$("#modalInfo").hide();
	});

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
});


function getApprovalStatus(id,empname) {
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$(".modal-title").html('Work flow of '+empname);
	$.ajax({
		url : "GetLeaveApprovalStatus.action?effectiveid="+id+"&type=6",
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

function approveDeny(apStatus,perkId,userType){
	var divResult = 'subDivResult';
	var strBaseUserType = document.getElementById("strBaseUserType").value;
	var strCEO = '<%=IConstants.CEO %>';
	var strHOD = '<%=IConstants.HOD %>';
	
	if(strBaseUserType == strCEO || strBaseUserType == strHOD) {
		divResult = 'subSubDivResult';
	}
	var status = '';
	if(apStatus == '1'){
		status='approve';
	} else if(apStatus == '-1'){
		status='deny';
	}
	if(confirm('Are you sure, do you want to '+status+' this request?')){
		var reason = window.prompt("Please enter your "+status+" reason.");
		if (reason != null) {
			var currUserType = document.getElementById("currUserType").value;
			$("#"+divResult).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$.ajax({
				type : 'POST',
				url: 'UpdatePerks.action?S='+apStatus+'&RID='+perkId+'&T=PERK&M=AA&mReason='+reason+'&userType='+userType+'&currUserType='+currUserType/* ,
				success: function(result){
		        	$("#"+divResult).html(result); 
		   		} */
			});
			
			$.ajax({
				url: 'Perks.action?&currUserType='+currUserType,
				cache: true,
				success: function(result){
					$("#"+divResult).html(result);
		   		}
			});
			
			/* var action = 'UpdatePerks.action?S='+apStatus+'&RID='+perkId+'&T=PERK&M=AA&mReason='+reason+'&userType='+userType;
			window.location = action; */
		}
	}
}

function applyForNewPerk() {
	var f_financialYear = document.getElementById("f_financialYear").value;
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$(".modal-title").html('Apply for New Perk');
	$.ajax({
		url : 'ApplyNewPerk.action?financialYear='+f_financialYear,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}


function editAppliedPerk(strId) {
	
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$(".modal-title").html('Apply for New Perk');
	$.ajax({
		url : 'ApplyNewPerk.action?operation=E&strId='+strId,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}


function submitForm(type){
	var strEmployee = '<%=IConstants.EMPLOYEE %>';
	var strUserType = document.getElementById("strUserType").value;
	var divResult = 'subDivResult';
	/* if(strUserType == strEmployee) {
		divResult = 'divResult';
	} */
	var strBaseUserType = document.getElementById("strBaseUserType").value;
	var strCEO = '<%=IConstants.CEO %>';
	var strHOD = '<%=IConstants.HOD %>';
	
	if((strBaseUserType == strCEO || strBaseUserType == strHOD) && strUserType != strEmployee) {
		divResult = 'subSubDivResult';
	}
	//console.log("divResult==>"+divResult);
	var currUserType = document.getElementById("currUserType").value;
	var org = "";
	var f_strWLocation = "";
	var strSelectedEmpId = "";
	if(document.getElementById("f_org")) {
		org = document.getElementById("f_org").value;
	}
	if(document.getElementById("f_strWLocation")) {
		f_strWLocation = document.getElementById("f_strWLocation").value;
	}
	if(document.getElementById("strSelectedEmpId")) {
		strSelectedEmpId = document.getElementById("strSelectedEmpId").value;
	}
	var f_financialYear = document.getElementById("f_financialYear").value;
	var paramValues = "";
	if(type == '2') {
		paramValues = '&f_strWLocation='+f_strWLocation+'&strSelectedEmpId='+strSelectedEmpId+'&f_financialYear='+f_financialYear;
	}
	//alert("service ===>> " + service);
	$("#"+divResult).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'Perks.action?f_org='+org+'&currUserType='+currUserType+paramValues,
		data: $("#"+this.id).serialize(),
		success: function(result){
        	$("#"+divResult).html(result);
   		}
	});
}

</script>

	<%
		String strUserType = (String)session.getAttribute(IConstants.USERTYPE);
		String currUserType = (String) request.getAttribute("currUserType");
		String strBaseUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
	%>
	
	<%=uF.showData((String)session.getAttribute(IConstants.MESSAGE),"") %>
	<% session.setAttribute(IConstants.MESSAGE, ""); %>
	
	<!-- /.box-header -->
                
	<div class="box-body" style="padding: 5px; overflow-y: auto;min-height:600px;">
		<div class="box box-default collapsed-box">
			<div class="box-header with-border">
			    <h3 class="box-title"><%=(String)request.getAttribute("selectedFilter") %></h3>
			    <div class="box-tools pull-right">
			        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
			        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
			    </div>
			</div>
			<div class="box-body" style="padding: 5px; overflow-y: auto;">
				<s:form name="frm" action="Perks" theme="simple" method="post">
					<input type="hidden" name="strBaseUserType" id=strBaseUserType value="<%=strBaseUserType %>"/>
					<input type="hidden" name="strUserType" id=strUserType value="<%=strUserType %>"/>
					<s:hidden name="currUserType" id="currUserType"/>
					<div class="row row_without_margin">
						<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
							<i class="fa fa-filter"></i>
						</div>
						<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
							<% if((strUserType!=null && !strUserType.equals(IConstants.MANAGER) && !strUserType.equals(IConstants.EMPLOYEE) && !strUserType.equalsIgnoreCase(IConstants.ARTICLE) && !strUserType.equalsIgnoreCase(IConstants.CONSULTANT)) || (currUserType != null && currUserType.equals(strBaseUserType))) { %>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Organization</p>
									<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1');" list="organisationList" key=""/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Work Location</p>
									<s:select name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" headerKey="0" headerValue="All Locations" list="wLocationList" key="" onchange="submitForm('2');"/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Employee</p>
									<s:select theme="simple" name="strSelectedEmpId" id="strSelectedEmpId" listKey="employeeId" listValue="employeeCode" headerKey="0" headerValue="All Employees" list="empNamesList" onchange="submitForm('2');" key="" required="true"/>
								</div>
							<% } %>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Financial Year</p>
								<s:select theme="simple" name="f_financialYear" id="f_financialYear" listKey="financialYearId" listValue="financialYearName" list="financialYearList" key=""/>
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">&nbsp;</p>
								<input type="button" name="Submit" value="Submit" class="btn btn-primary" onclick="submitForm('2');"/>
							</div>
						</div>
					</div>
				</s:form>
			</div>
		</div>

	<%-- <% if(strUserType != null && strUserType.equals(IConstants.MANAGER) && strBaseUserType != null && (strBaseUserType.equals(IConstants.CEO) || strBaseUserType.equals(IConstants.HOD))) { %>
		<div style="width: 100%; margin-bottom: 15px;">
			<ul class="nav nav-pills">
				<li class="<%=(currUserType == null || currUserType.equals("MYTEAM")) ? "active" : "" %>" style="border-radius: 10px 10px 0px 0px; <%=(currUserType == null || currUserType.equals("MYTEAM")) ? "" : "background-color: rgb(226, 226, 226)" %>;">
					<a href="Perks.action?currUserType=MYTEAM" style="padding: 1px 10px !important;border-radius: 10px 10px 0px 0px;">My Team</a>
				</li>
				<li class="<%=(currUserType == null || currUserType.equals(strBaseUserType)) ? "active" : "" %>"  style="border-radius: 10px 10px 0px 0px; <%=(currUserType == null || currUserType.equals(strBaseUserType)) ? "" : "background-color: rgb(226, 226, 226)" %>;">
					<a href="Perks.action?currUserType=<%=strBaseUserType %>" style="padding: 1px 10px !important;border-radius: 10px 10px 0px 0px;"><%=strBaseUserType %></a>
				</li>
			</ul>
		</div>
	<% } %> --%>

	<% if(strUserType != null && strUserType.equals(IConstants.EMPLOYEE)) { %>
		<div class="col-md-12" style="margin: 0px 0px 10px 0px">
			<a href="javascript:void(0)" onclick="applyForNewPerk();" title="Apply for New Perk"><i class="fa fa-plus-circle"></i> Apply for New Perk</a>
		</div>
	<% } %>
	
	<table class="table" id="lt">
		<thead>
			<tr>
				<th style="text-align: left; width: 80%;">Perk Type</th>
				<th style="text-align: left;">Workflow</th> 
			</tr>
		</thead>
		
		<tbody>
		<% java.util.List cOuterList = (java.util.List)request.getAttribute("alReport"); %>
		<% for (int i=0; cOuterList!=null && i<cOuterList.size(); i++) { %>
		<% java.util.List cInnerList = (java.util.List)cOuterList.get(i); %>
			<tr>
				<td><%=cInnerList.get(0) %></td>
				<td><%=cInnerList.get(1) %></td>
			</tr>
		<% } %>
		</tbody>
	</table>

	<% if(cOuterList.size() != 0) { %>
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
			    <div class="legend-info"><i class="fa fa-check-circle checknew" aria-hidden="true"></i>Approve Perk</div>
			  </div>
			  <div class="custom-legend no-borderleft-for-legend">
			    <div class="legend-info"><i class="fa fa-times-circle cross" aria-hidden="true"></i>Deny Perk</div>
			  </div>
			</div>
	<% } %>
	</div>
	<!-- /.box-body -->


<script type="text/javascript">
$(document).ready( function () {
	//checkPerkPolicyOnLoad();
});
$(window).load(function(){
	$(".validateRequired").prop('required',true);
});
</script>

<%-- <jsp:include page="../common/Legends.jsp" flush="true"></jsp:include> --%> 


  
<div class="modal" id="modalInfo" role="dialog">
    <div class="modal-dialog">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">-</h4>
            </div>
            <div class="modal-body" style="height:auto;overflow-y:auto;padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>