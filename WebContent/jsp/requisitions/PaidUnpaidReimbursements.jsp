<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="java.util.List"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<script type="text/javascript">
$(function(){
	$("#f_strWLocation").multiselect().multiselectfilter();
	$("#f_department").multiselect().multiselectfilter();
	$("#f_service").multiselect().multiselectfilter();
	$("#f_level").multiselect().multiselectfilter();
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
	
	
	  $("#strStartDate").datepicker({
          format: 'dd/mm/yyyy',
          autoclose: true
      }).on('changeDate', function (selected) {
          var minDate = new Date(selected.date.valueOf());
          $('#strEndDate').datepicker('setStartDate', minDate);
      });
      
      $("#strEndDate").datepicker({
      	format: 'dd/mm/yyyy',
      	autoclose: true
      }).on('changeDate', function (selected) {
              var minDate = new Date(selected.date.valueOf());
              $('#strStartDate').datepicker('setEndDate', minDate);
      });
	
	
});    


function submitForm(type) {

	document.frm_PaidUnpaidReimbursements.exportType.value='';
	var org = document.getElementById("f_org").value;
	var paycycle = document.getElementById("paycycle").value;
	var location = getSelectedValue("f_strWLocation");
	var department = getSelectedValue("f_department");
	var service = getSelectedValue("f_service");
	var level = getSelectedValue("f_level");
	var paidStatus = document.getElementById("paidStatus").value;
	//var financialYear = document.getElementById("financialYear").value;
	var strSelectedEmpId = document.getElementById("strSelectedEmpId").value;
	var paycycleDate = getCheckedValue("paycycleDate");
	var strStartDate = document.getElementById("strStartDate").value;
	var strEndDate = document.getElementById("strEndDate").value;
	var paramValues = "";
	if(type == '2') {
		
		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service+'&strLevel='+level
		+'&paycycle='+paycycle+'&paycycleDate='+paycycleDate+'&strStartDate='+strStartDate+'&strEndDate='+strEndDate+'&strSelectedEmpId='+strSelectedEmpId;
	
		}
		/*if(paycycleDate == 2 && strSelectedEmpId== "")
	//	{
		//	alert("Please select employee");
	//	}
	//	else
		//{*/
			//alert("service ===>> " + service);
			$("#subDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$.ajax({
			type : 'POST',
			url: 'PaidUnpaidReimbursements.action?f_org='+org+paramValues+'&paidStatus='+paidStatus,
			data: $("#"+this.id).serialize(),
			success: function(result){
        	$("#subDivResult").html(result);
   		}
	});

}

function getCheckedValue(checkId) {
    var radioObj = document.getElementsByName(checkId);
    var radioLength = radioObj.length;
	for(var i = 0; i < radioLength; i++) {
		if(radioObj[i].checked) {
			return radioObj[i].value;
		}
	}
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


/* function submitForm(type){
	if(type == '1'){
		document.getElementById("paycycle").selectedIndex = "0";
	}
	document.frm_PaidUnpaidReimbursements.exportType.value='';
	document.frm_PaidUnpaidReimbursements.submit();
} */


function donwloadBankStatement(orgId,paycycle) {
	
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$('.modal-title').html('Bank Orders');
	$("#modalInfo").show();

	$.ajax({
		url : "ViewBankStatements.action?type=reimb&orgId="+orgId+"&strPaycycle="+paycycle,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});

}

function selectall(x,strEmpId){
	var  status=x.checked; 
	var  arr= document.getElementsByName(strEmpId);
	for(i=0;i<arr.length;i++){ 
  		arr[i].checked=status;
 	}
	if(x.checked == true){
		document.getElementById("unApproveSpan").style.display = 'none';
		document.getElementById("approveSpan").style.display = 'inline';
	} else {
		document.getElementById("unApproveSpan").style.display = 'inline';
		document.getElementById("approveSpan").style.display = 'none';
	}
}

function checkAll(){
	var payAll = document.getElementById("payAll");		
	var reimbId = document.getElementsByName('reimbId');
	var cnt = 0;
	var chkCnt = 0;
	for(var i=0;i<reimbId.length;i++) {
		cnt++;
		 if(reimbId[i].checked) {
			 chkCnt++;
		 }
	 }
	if(parseFloat(chkCnt) > 0) {
		document.getElementById("unApproveSpan").style.display = 'none';
		document.getElementById("approveSpan").style.display = 'inline';
	} else {
		document.getElementById("unApproveSpan").style.display = 'inline';
		document.getElementById("approveSpan").style.display = 'none';
	}
	
	if(cnt == chkCnt) {
		payAll.checked = true;
	} else {
		payAll.checked = false;
	}
}

function checkPay(){
	var payCount = document.getElementById("payCount").value;		
	if(parseInt(payCount) > 0 && document.getElementById("unApproveSpan")) {
		document.getElementById("unApproveSpan").style.display = 'none';
		document.getElementById("approveSpan").style.display = 'inline';
	} else {
		if(document.getElementById("unApproveSpan")){
			document.getElementById("unApproveSpan").style.display = 'inline';
			document.getElementById("approveSpan").style.display = 'none';
			document.getElementById("payAll").checked = false;
		}
	}
}

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


function payReimbursement(){
	if(confirm('Are you sure, you wish to pay for selected employees?')) {
		var data = $("#frm_PaidUnpaidReimbursements").serialize();
		$("#subDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: 'PaidUnpaidReimbursements.action?strApprove=PAY',
			data: data,
			success: function(result){
	        	$("#subDivResult").html(result); 
	   		}
		});
	}
}

function viewReimbursmentDetails(empId,reimbursementId) {
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$('.modal-title').html('View Reimbursement Details');
	$("#modalInfo").show();

	$.ajax({
		url : "ViewReimbursementDetails.action?strEmpId="+empId+"&reimbursementId="+reimbursementId,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}


function viewBulkExpenseDetails(empId, parentId) {
	var dialogEdit = '.modal-body';
   	 $(dialogEdit).empty();
   	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
   	 $("#modalInfo").show();
   	 $(".modal-title").html('View Reimbursement Details');
  	 var height = $(window).height()* 0.95;
	 var width = $(window).width()* 0.95;
	 $(".modal-dialog").css("height", height);
	 $(".modal-dialog").css("width", width);
  
   	 $.ajax({
		url : "ViewBulkExpenseDetails.action?strEmpId="+empId+"&parentId="+parentId,
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
	$('.modal-title').html('Work flow of '+empname);
	$("#modalInfo").show();

	$.ajax({
		url : "GetLeaveApprovalStatus.action?effectiveid="+id+"&type=5",
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}


function viewCancelReason(id, empname){
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$('.modal-title').html('Cancelation reason of '+empname);
	$("#modalInfo").show();

	$.ajax({
		url : "UpdateRequest.action?T=RIM&M=VIEW&RID="+id,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}



function cancelReimbursement(reimbursementId, myDiv) {
	if(confirm('Are you sure, do you want to cancel this request?')) {
		var reason = window.prompt("Please enter your cancelation reason.");
		if (reason != null) {
			$.ajax({
				type : 'GET',
				url: 'UpdateRequest.action?S=-2&T=RIM&M=D&RID='+reimbursementId+'&strReason='+reason,
				success: function(result){
		        	$("#"+myDiv).html(result); 
		   		}
			});
		}
	}
}

function calculateExchangeAmt(reimbursementId) {
	var reimbursAmt = document.getElementById("strRiembursAmt_"+reimbursementId).value;
	var exchangeRate = document.getElementById("strExchangeRate_"+reimbursementId).value;
	if(exchangeRate == '' || parseInt(exchangeRate)<=0) {
		alert("Please enter exchange rate greater than 0");
		document.getElementById("strExchangeRate_"+reimbursementId).value = "1";
	} else {
		var exchangeAmt = reimbursAmt * exchangeRate;
		document.getElementById("exchangeAmtSpan_"+reimbursementId).innerHTML = exchangeAmt.toFixed(1);
		document.getElementById("strExchangeAmount_"+reimbursementId).value = exchangeAmt.toFixed(1);
	}
}


function generateReportExcel() {
	window.location = "ExportExcelReport.action?excelType=STANDARD";
}

</script> 

<%
	String strUserTYpe = (String) session.getAttribute(IConstants.USERTYPE);
	UtilityFunctions uF = new UtilityFunctions();
	String strTitle = (String) request.getAttribute(IConstants.TITLE);
	List<List<String>> alReport = (List<List<String>>)request.getAttribute("alReport");
	
	Map<String, Map<String, String>> hmReimbIdPaidData = (Map<String, Map<String, String>>) request.getAttribute("hmReimbIdPaidData");
	if(hmReimbIdPaidData == null) hmReimbIdPaidData = new HashMap<String, Map<String, String>>();
	
	Map<String, Map<String, String>> hmReimbParentIdPaidData = (Map<String, Map<String, String>>) request.getAttribute("hmReimbParentIdPaidData");
	if(hmReimbParentIdPaidData == null) hmReimbParentIdPaidData = new HashMap<String, Map<String, String>>();
%>

<%-- <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Paid/unpaid Reimbursements" name="title"/>
</jsp:include> --%>

	<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
		<s:form name="frm_PaidUnpaidReimbursements" id="frm_PaidUnpaidReimbursements" action="PaidUnpaidReimbursements" theme="simple" method="post">
				<div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE;">
					<div class="box-header with-border">
						<h3 class="box-title" style="font-size:14px;"><%=(String) request.getAttribute("selectedFilter")%></h3>
						<div class="box-tools pull-right">
							<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
							<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
						</div>
					</div>
					<!-- /.box-header -->
					<div class="box-body" style="padding: 5px; overflow-y: auto; display: none;">
						<%-- <s:form name="frm_PaidUnpaidReimbursements" id="frm_PaidUnpaidReimbursements" action="PaidUnpaidReimbursements" theme="simple" method="post"> --%>
							<s:hidden name="exportType"></s:hidden>
							<div class="row row_without_margin">
								<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
									<i class="fa fa-filter"></i>
								</div>
								<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Status</p>
										<s:select theme="simple" name="paidStatus" id="paidStatus" cssStyle="width: 200px;" 
											list="#{'-1':'All','1':'Paid', '2':'UnPaid'}" onchange="submitForm('2');"/>
									</div>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Organization</p>
										<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1')" list="orgList"/>
									</div>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Location</p>
										<s:select theme="simple" name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" multiple="true" list="wLocationList" key=""/>
									</div>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Department</p>
										<s:select name="f_department" id="f_department" list="departmentList" listKey="deptId" listValue="deptName" multiple="true"></s:select>
									</div>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Service</p>
										<s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId" listValue="serviceName" multiple="true"></s:select>
									</div>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Level</p>
										<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" listValue="levelCodeName" multiple="true" list="levelList"/>
									</div>
								</div>
							</div><br>
							<div class="row row_without_margin">
								<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
									<i class="fa fa-calendar"></i>
								</div>
								<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
								<%
									String paycycleDate=(String)request.getAttribute("paycycleDate");
								
									String check1="";
									String check2="";
									if(paycycleDate!=null && paycycleDate.equals("2")) {
										check1="";
										check2="checked=\"checked\"";	
										
									} else {
										check1="checked=\"checked\"";
										check2="";
										
									}
									
								%>
									<div class="col-lg-1 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">&nbsp;</p>
										<input type="radio" name="paycycleDate" id="paycycleDate" value="1" <%=check1 %>/>
									</div>
									
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Paycycle</p>
										<s:select name="paycycle" id="paycycle" listKey="paycycleId" listValue="paycycleName" headerValue="Select Paycycle" list="paycycleList" key="" onchange=""/>
									</div>
									<div class="col-lg-1 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">&nbsp;</p>
										<input type="radio" name="paycycleDate" id="paycycleDate" value="2" <%=check2 %>/>
									</div>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">From Date</p>
										<s:textfield name="strStartDate" id="strStartDate" cssStyle="width: 100px !important;"></s:textfield>
									</div>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">To Date</p>
										<s:textfield name="strEndDate" id="strEndDate" cssStyle="width: 100px !important;"></s:textfield>
									</div>
									<%-- <div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Finicial Year</p>
										<s:select name="financialYear" id="financialYear" listKey="financialYearId" listValue="financialYearName" list="financialYearList" key="" />
									</div> --%>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Employee</p>
										<s:select name="strSelectedEmpId" id="strSelectedEmpId" listKey="employeeId" listValue="employeeName" headerKey="" headerValue="All Employee"
		                                    list="empList" key="" required="true" onchange="" />
									</div>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">&nbsp;</p>
										<input type="button" name="Submit" value="Submit" class="btn btn-primary" onclick="submitForm('2');" />
									</div>
								</div>
							</div>
						<%-- </s:form> --%>
					</div>
					<!-- /.box-body -->
				</div>
			
			<%
				int nPayCount = 0;
				if(alReport!=null && alReport.size() > 0) {
			%>
				<div class="row row_without_margin paddingtopbottom10">
					<div class="col-lg-10">
						Choose Bank to pay from:&nbsp;&nbsp;<s:select theme="simple" name="bankAccount" listKey="bankId" listValue="bankName" list="bankList" key=""/>
						&nbsp;Choose Bank Account: <s:select theme="simple" name="bankAccountType" id="bankAccountType" list="#{'1':'Primary','2':'Secondary'}"/>
						<span id="unApproveSpan" style="display: none;">
							&nbsp;<input type="button" name="strApprove" class="btn btn-primary disabled" value="PAY"/>
						</span>
						<span id="approveSpan">
							&nbsp;<input type="button" name="strApprove" class="btn btn-primary" value="PAY" onclick="payReimbursement();"/>
						</span>
					</div>
					<div class="col-lg-2">
						<span class="pull-right">Bank Orders: <a href="javascript:void(0)" onclick="donwloadBankStatement('<%=(String)request.getAttribute("f_org")%>','<%=(String)request.getAttribute("paycycle")%>')"><i class="fa fa-files-o" aria-hidden="true"></i></a></span>
					</div>
				</div>
				<div class="col-md-12" style="margin: 0px 0px 10px;">   		
					<a onclick="generateReportExcel();" href="javascript:void(0)" style="background-repeat: no-repeat;float: right;"><i class="fa fa-file-excel-o" aria-hidden="true"></i></a>
				</div>
			<% } %>
			<div class="clr margintop20">
				
				<!-- Place holder where add and delete buttons will be generated -->
				<div class="add_delete_toolbar"></div>

				<table class="table table-bordered table-striped" id="lt">
					<thead>
					<tr>
						<th class="alignCenter" nowrap="nowrap">Employee Code</th>
						<th nowrap="nowrap">Employee Name</th>
						<th class="alignCenter" nowrap="nowrap">Pay<br/>
						<% if(alReport!=null && alReport.size()>0) { %>
							<input type="checkbox" name="payAll" id="payAll" onclick="selectall(this,'reimbId')" checked="checked"/>
						<% } %>
						</th>
						<th class="alignCenter" nowrap="nowrap">Applied Date</th>
						<th class="alignRight" nowrap="nowrap">Amount</th>
						<th class="alignRight" nowrap="nowrap">Exchange Rate</th>
						<th class="alignRight" nowrap="nowrap">Exchange Amount</th>
						<th>Purpose</th>
						<th nowrap="nowrap">View Attachment</th>
						<th nowrap="nowrap">View</th>
						<th nowrap="nowrap">Workflow</th>
						<th nowrap="nowrap">Action</th>
					</tr>
					</thead>
					<tbody>
						<% 
					   for (int i=0; alReport!=null && i<alReport.size(); i++) { 
							List<String> innerList = (List<String>)alReport.get(i); 
						%>
							<tr id="<%=innerList.get(0) %>">
								<td style="text-align: center;" nowrap="nowrap"><%=innerList.get(2) %></td>
								<td nowrap="nowrap"><%=innerList.get(3) %></td>
								<td style="text-align: center;">
									<%if(uF.parseToBoolean(innerList.get(4))) { %>
										Paid
									<% } else {
										nPayCount++;
									%>
										<input type="checkbox" name="reimbId" value="<%=innerList.get(0) %>" onclick="checkAll();" checked="checked"/>
									<% } %>
								</td>
								<td style="text-align: center;" nowrap="nowrap"><%=innerList.get(5) %></td>
								<td style="text-align: right;" nowrap="nowrap">
									<input type="hidden" name="strRiembursAmt_<%=innerList.get(0) %>" id="strRiembursAmt_<%=innerList.get(0) %>" value="<%=innerList.get(12) %>"/>
									<input type="hidden" name="strRiembursCurr_<%=innerList.get(0) %>" id="strRiembursCurr_<%=innerList.get(0) %>" value="<%=innerList.get(13) %>"/>
									<%=innerList.get(6) %>
								</td>
								<%if(uF.parseToBoolean(innerList.get(4))) { 
									Map<String, String> hmInner = new HashMap<String, String>();
									if(uF.parseToInt(innerList.get(14))>0) {
										hmInner = hmReimbParentIdPaidData.get(innerList.get(14));
									} else {
										hmInner = hmReimbIdPaidData.get(innerList.get(0));
									}
								%>
									<td style="text-align: right;" nowrap="nowrap"><%=hmInner.get("EXCHANGE_RATE") %></td>
									<td style="text-align: right;" nowrap="nowrap"><%=hmInner.get("EXCHANGE_AMOUNT") %></td>
								<% } else { %>
									<td nowrap="nowrap"><input type="text" name="strExchangeRate_<%=innerList.get(0) %>" id="strExchangeRate_<%=innerList.get(0) %>" style="width: 60px !important;" value="1" onkeyup="calculateExchangeAmt('<%=innerList.get(0) %>');"/></td>
									<td style="text-align: right;" nowrap="nowrap">
										<input type="hidden" name="strExchangeAmount_<%=innerList.get(0) %>" id="strExchangeAmount_<%=innerList.get(0) %>"/>
										<span id="exchangeAmtSpan_<%=innerList.get(0) %>"><%=innerList.get(12) %></span>
									</td>
								<% } %>
								<td><%=innerList.get(7) %></td>
								<td><%=innerList.get(8) %></td>
								<td><%=innerList.get(9) %></td>
								<td><%=innerList.get(10) %></td>
								<td><%=innerList.get(11) %></td>
							</tr>
						<% } %>
					</tbody>
				</table>
			</div>
		    <input type="hidden" name="payCount" id="payCount" value="<%=nPayCount %>"/> 
        </s:form>	
	</div>
	<!-- /.box-body -->

<script type="text/javascript">
checkPay();
</script>

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