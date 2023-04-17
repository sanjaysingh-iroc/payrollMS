<%@page import="java.util.List"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="java.util.Map"%>
<%@page import="com.konnect.jpms.select.FillWLocation"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<% List alLoanReport = (List)request.getAttribute("alLoanReport"); %>


<% 
	String strUserType = (String)session.getAttribute(IConstants.USERTYPE);
	String strMessage = (String)request.getAttribute(IConstants.MESSAGE);
	if(strMessage == null) {
		strMessage = "";
	}
	UtilityFunctions uF = new UtilityFunctions();
	
	CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
	Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(request);
	Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>) request.getAttribute("hmFeatureUserTypeId");
	if(hmFeatureUserTypeId == null) hmFeatureUserTypeId = new HashMap<String, List<String>>();

%>
 
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
});
			
			
function viewLoanDetails(id) {

	var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 $(".modal-title").html('Loan Details');
	 $.ajax({
			url : "ViewLoanDetails.action?loanApplId="+id,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	} 


function applyNewLoan(currUserType) {

	var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 $(".modal-title").html('Apply New Loan');
	 if($(window).width() >= 900){
		 $(".modal-dialog").width(900);
	 }
	 $.ajax({
			url : 'LoanApplication.action?currUserType='+currUserType,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}
	

function payLoan(id) {
	if(document.getElementById("the_div")){
		var element = document.getElementById("the_div");
		element.parentNode.removeChild(element);
	}
	var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 $(".modal-title").html('Pay Details');
	 $.ajax({
			url : "PayLoan.action?loanApplId="+id,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
}



function viewPayments(strEmpId,strLoanId,strLoanApplicationId) {

	var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 $(".modal-title").html('Loan Payments');
	 $.ajax({
			url : "LoanPayments.action?strEmpId="+strEmpId+"&strLoanId="+strLoanId+"&strLoanApplicationId="+strLoanApplicationId,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
}
	
/* function approveLoan(count,loanAppId,approvalStatus){
	var status ='deny';
	if(approvalStatus=='1'){
		status ='approve';
	}
	if(confirm('Do you want to '+status+' the request')){
		var action = 'ApproveLoan.action?loanAppId='+loanAppId+'&approvalStatus='+approvalStatus;
		getContent('myDiv'+count,action);
	}
} */

function getApprovalStatus(id,empname){
	var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 $(".modal-title").html('Work flow of '+empname);
	 $.ajax({
			url : "GetLeaveApprovalStatus.action?effectiveid="+id+"&type=9",
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
 
}

function approveDeny(loanId,apStatus,userType){
	var divResult = 'divResult';
	var strBaseUserType = document.getElementById("strBaseUserType").value;
	var strCEO = '<%=IConstants.CEO %>';
	var strHOD = '<%=IConstants.HOD %>';
	
	if(strBaseUserType == strCEO || strBaseUserType == strHOD) {
		divResult = 'subDivResult';
	}
	var status = '';
	if(apStatus == '1'){
		status='approve';
	} else if(apStatus == '-1'){
		status='deny';
	}
	if(confirm('Are you sure, do you want to '+status+' this request?')) {
		var reason = window.prompt("Please enter your "+status+" reason.");
		if (reason != null) {
			var currUserType = document.getElementById("currUserType").value;
			$("#"+divResult).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$.ajax({
				type : 'POST',
				url: 'ApproveLoan.action?loanAppId='+loanId+'&approvalStatus='+apStatus+'&mReason='+reason+'&userType='+userType+'&currUserType='+currUserType,
				data: $("#"+this.id).serialize()/* ,
				success: function(result){
		        	$("#"+divResult).html(result);
		   		} */
			});
			
			$.ajax({ 
				url: 'LoanApplicationReport.action?&currUserType='+currUserType,
				cache: true,
				success: function(result){
					$("#"+divResult).html(result);
		   		}
			});
			
			/* var action = 'ApproveLoan.action?loanAppId='+loanId+'&approvalStatus='+apStatus+'&mReason='+reason+'&userType='+userType;
			window.location = action; */ 
		}
	}
}

function submitForm(type){
	var divResult = 'divResult';
	var strBaseUserType = document.getElementById("strBaseUserType").value;
	var strCEO = '<%=IConstants.CEO %>';
	var strHOD = '<%=IConstants.HOD %>';
	
	if(strBaseUserType == strCEO || strBaseUserType == strHOD) {
		divResult = 'subDivResult';
	}
	var currUserType = document.getElementById("currUserType").value;
	var org = "";
	var f_strWLocation = "";
	var f_department = "";
	var f_level = "";
	var strEmpId = "";
	if(document.getElementById("f_org")) {
		org = document.getElementById("f_org").value;
	}
	if(document.getElementById("f_strWLocation")) {
		f_strWLocation = document.getElementById("f_strWLocation").value;
	}
	if(document.getElementById("f_department")) {
		f_department = document.getElementById("f_department").value;
	}
	if(document.getElementById("f_level")) {
		f_level = document.getElementById("f_level").value;
	}
	if(document.getElementById("strEmpId")) {
		strEmpId = document.getElementById("strEmpId").value;
	}
	var paramValues = "";
	if(type == '2') {
		paramValues = '&f_strWLocation='+f_strWLocation+'&f_department='+f_department+'&f_level='+f_level+'&strEmpId='+strEmpId;
	}
	//alert("service ===>> " + service);
	$("#"+divResult).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'LoanApplicationReport.action?f_org='+org+'&currUserType='+currUserType+paramValues,
		data: $("#"+this.id).serialize(),
		success: function(result){
        	$("#"+divResult).html(result);
   		}
	});
}

function cancelLoan(divId,loanAppliedId,empId){
	if(confirm('Are you sure you want to cancel this entry?')){
		var action = 'LoanApplicationReport.action?action=cancel&loanAppliedId='+loanAppliedId+'&empId='+empId;
		getContent(divId,action);
	}
}

</script>
 
 	<%
		String currUserType = (String) request.getAttribute("currUserType");
		String strBaseUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
	%>
 
    <%-- <% if(strUserType != null && strUserType.equals(IConstants.MANAGER) && strBaseUserType != null && (strBaseUserType.equals(IConstants.CEO) || strBaseUserType.equals(IConstants.HOD))) { %>	
    		<div style="width: 100%;">
				<ul class="nav nav-pills">
					<li class="<%=(currUserType == null || currUserType.equals("MYTEAM")) ? "active" : "" %>" style="border-radius: 10px 10px 0px 0px; <%=(currUserType == null || currUserType.equals("MYTEAM")) ? "" : "background-color: rgb(226, 226, 226)" %>;">
						<a href="LoanApplicationReport.action?currUserType=MYTEAM" style="padding: 4px 15px !important;border-radius: 10px 10px 0px 0px;">My Team</a>
					</li>
					<li class="<%=(currUserType == null || currUserType.equals(strBaseUserType)) ? "active" : "" %>"  style="border-radius: 10px 10px 0px 0px; <%=(currUserType == null || currUserType.equals(strBaseUserType)) ? "" : "background-color: rgb(226, 226, 226)" %>;">
						<a href="LoanApplicationReport.action?currUserType=<%=strBaseUserType %>" style="padding: 4px 15px !important;border-radius: 10px 10px 0px 0px;"><%=strBaseUserType %></a>
					</li>
				</ul>
			</div> 
		<% } %> --%>
		
                <!-- /.box-header -->
	<div class="box-body" style="padding:5px; overflow-y:auto; min-height:600px;">
		<%=strMessage %>
		<% if((strUserType!=null && !strUserType.equals(IConstants.MANAGER) && !strUserType.equals(IConstants.EMPLOYEE)) || (currUserType != null && currUserType.equals(strBaseUserType))) { %>
		    <div class="box box-default collapsed-box">
				<div class="box-header with-border">
				    <h3 class="box-title"><%=(String)request.getAttribute("selectedFilter") %></h3>
				    <div class="box-tools pull-right">
				        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
				        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
				    </div>
				</div>
				
				<div class="box-body" style="padding: 5px; overflow-y: auto;">
					<s:form name="frmLoanApplication" id="frmLoanApplication" theme="simple" action="LoanApplicationReport" method="POST">
						<input type="hidden" name="strBaseUserType" id=strBaseUserType value="<%=strBaseUserType %>"/>
						<s:hidden name="currUserType" id="currUserType"/>
						<div class="row row_without_margin">
							<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
								<i class="fa fa-filter"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Organization</p>
									<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1');" list="organisationList" key=""/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Work Location</p>
									<s:select theme="simple" name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" headerKey="" headerValue="All Locations" onchange="submitForm('2');" list="wLocationList" key=""/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Department</p>
									<s:select theme="simple" name="f_department" id="f_department" list="departmentList" listKey="deptId" listValue="deptName" headerKey="0" headerValue="All Departments" onchange="submitForm('2');"/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Level</p>
									<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" headerValue="All Levels" listValue="levelCodeName" headerKey="0" onchange="submitForm('2');" list="levelList" key="" required="true"/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Employee</p>
									<s:select theme="simple" name="strEmpId" id="strEmpId" listKey="employeeId" cssClass="validateRequired" listValue="employeeCode" headerKey="" headerValue="Select Employee" list="empList" key="" required="true" onchange="submitForm('2');"/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">&nbsp;</p>
									<input type="button" name="submit" value="Submit" class="btn btn-primary" style="margin: 0px" onclick="submitForm('2');" />
								</div>
							</div>
						</div>
					</s:form>
				</div>
			</div>
		<%} else { %>
 				<input type="hidden" name="strBaseUserType" id=strBaseUserType value="<%=strBaseUserType %>"/>
				<s:hidden name="currUserType" id="currUserType"/>
		<% } %>
		
		<% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_MY_PAY_DISABLE_LINK))){ 
			List<String> disableTabList = hmFeatureUserTypeId.get(IConstants.F_MY_PAY_DISABLE_LINK);
					
		%>
			<% if(disableTabList != null && disableTabList.contains("APPLY_NEW_LOAN")){ %>
				<div class="col-md-12" style="margin: 0px 0px 10px;">
					<a href="javascript:void(0)" onclick="applyNewLoan('<%=currUserType %>');" title="Apply New Loan"><i class="fa fa-plus-circle" aria-hidden="true"></i> Apply New Loan</a>
				</div>
			<% } %>
		<% } else { %>
			<div class="col-md-12" style="margin: 0px 0px 10px;">
				<a href="javascript:void(0)" onclick="applyNewLoan('<%=currUserType %>');" title="Apply New Loan"><i class="fa fa-plus-circle" aria-hidden="true"></i> Apply New Loan</a>
			</div>
		<% } %>
		<div class="margintop20"></div>
		
		<table class="table table-bordered" id="lt">
			<thead>
				<tr>
					<%if(strUserType!=null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE)){ %>
						<th>Employee Name</th>
					<% } %>
					<th>Loan Type</th>
					<th>Effective Date</th>
					<th>Applied On</th>
					<th>Approved By</th>
					<th>Approved On</th>
					<th>Duration</th>
					<th>Acc No</th>
					<th>Principal Amount</th>
					<th>Balance Amount</th>
					<th>Monthly Installment</th>
					<%if(strUserType!=null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE)) { %>
						<th>Approving Profile</th>
					<% } %>
					<%if(strUserType!=null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE)) { %>
						<th class="no-sort">Action</th>
					<% } else { %>
						<th class="no-sort">Status</th>
					<% } %>
					<th>Workflow</th>
					<th class="no-sort">Details</th>
				</tr>
			</thead>
			<tbody>
			<% java.util.List couterlist = (java.util.List)request.getAttribute("alLoanReport"); %>
			 <% for(int i=0; i<couterlist.size(); i++) { %>
			 <% java.util.List cinnerlist = (java.util.List)couterlist.get(i); %>
				<tr id="<%=cinnerlist.get(0) %>">
					<%if(strUserType!=null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE)) { %>
						<td><%=cinnerlist.get(1) %></td>
					<% } %>
					<td><%=cinnerlist.get(2) %></td>
					<td><%=cinnerlist.get(15) %></td>
					<td><%=cinnerlist.get(3) %></td>
					<td><%=cinnerlist.get(4) %></td>
					<td><%=cinnerlist.get(5) %></td>
					<td><%=cinnerlist.get(6) %></td>
					<td><%=cinnerlist.get(7) %></td>
					<td align="right"><%=cinnerlist.get(8) %></td>
					<td align="right"><%=cinnerlist.get(9) %></td>
					<td align="right"><%=cinnerlist.get(10) %></td>
					<%if(strUserType!=null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE)){ %>
						<td align="right"><%=cinnerlist.get(14) %></td>
					<% } %>
					<td id="myDiv<%=i%>"><%=cinnerlist.get(11) %></td>
					<td><%=cinnerlist.get(13) %> </td>
					<td><%=cinnerlist.get(12) %> </td>
				</tr>
				<% } %>
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
			    <div class="legend-info"><i class="fa fa-check-circle checknew" aria-hidden="true"></i>Approve Loan</div>
			  </div>
			  <div class="custom-legend no-borderleft-for-legend">
			    <div class="legend-info"><i class="fa fa-times-circle cross" aria-hidden="true"></i>Deny Loan</div>
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
