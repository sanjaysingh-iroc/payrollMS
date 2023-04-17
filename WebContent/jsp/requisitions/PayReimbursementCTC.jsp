<%@page import="java.util.Iterator"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="java.util.List"%>
<%@ taglib prefix="s" uri="/struts-tags" %>

<script type="text/javascript" charset="utf-8">
$(function(){
	$("#f_strWLocation").multiselect().multiselectfilter();
	$("#f_department").multiselect().multiselectfilter();
	$("#f_service").multiselect().multiselectfilter();
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
	var strEmpCTCId = document.getElementsByName('strEmpCTCId');
	var cnt = 0;
	var chkCnt = 0;
	for(var i=0;i<strEmpCTCId.length;i++) {
		cnt++;
		 if(strEmpCTCId[i].checked) {
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
	
/* ===start parvez date: 12-10-2021=== */
	if(parseInt(payCount) > 0 && document.getElementById("unApproveSpan")) {
		document.getElementById("unApproveSpan").style.display = 'none';
		document.getElementById("approveSpan").style.display = 'inline';
	} else {
		/* document.getElementById("unApproveSpan").style.display = 'inline';
		document.getElementById("approveSpan").style.display = 'none';
		document.getElementById("payAll").checked = false; */
		
		if(document.getElementById("unApproveSpan")){
			document.getElementById("unApproveSpan").style.display = 'inline';
			document.getElementById("approveSpan").style.display = 'none';
			document.getElementById("payAll").checked = false;
		}
/* ===end parvez date: 12-10-2021=== */
		
	}
}

function donwloadBankStatement(orgId,paycycle) {
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$('.modal-title').html('Bank Orders');
	$("#modalInfo").show();

	$.ajax({
		url : "ViewBankStatements.action?type=reimbCTC&orgId="+orgId+"&strPaycycle="+paycycle,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

function submitForm(type){
	document.frm_PayReimbursementCTC.exportType.value='';
	
	var org = document.getElementById("f_org").value;
	var paycycle = document.getElementById("paycycle").value;
	var location = getSelectedValue("f_strWLocation");
	var department = getSelectedValue("f_department");
	var service = getSelectedValue("f_service");
	var level = document.getElementById("f_level").value;
	var paidStatus = document.getElementById("paidStatus").value;
	var paramValues = "";
	if(type == '2') {
		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service+'&f_level='+level+'&paycycle='+paycycle;
	}
	var action = 'PayReimbursementCTC.action?f_org='+org+paramValues+'&paidStatus='+paidStatus; 
	$("#subDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: action,
		data: $("#"+this.id).serialize(),
		success: function(result){
        	$("#subDivResult").html(result); 
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

function payReimbursementCTC(){
	if(confirm('Are you sure, you wish to pay for selected employees?')) {
		var data = $("#frm_PayReimbursementCTC").serialize();
		$("#subDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: 'PayReimbursementCTC.action?strApprove=PAY',
			data: data,
			success: function(result){
	        	$("#subDivResult").html(result); 
	   		}
		});
	}
}

</script>

<%
	UtilityFunctions uF = new UtilityFunctions();
	String strTitle = (String) request.getAttribute(IConstants.TITLE);
	String roundOffCondition = (String)request.getAttribute("roundOffCondition");
	
	Map<String, String> hmReimCTCName = (Map<String, String>)request.getAttribute("hmReimCTCName");
	if(hmReimCTCName == null) hmReimCTCName = new HashMap<String, String>();
	Map<String, String> hmEmpName = (Map<String, String>)request.getAttribute("hmEmpName");
	if(hmEmpName == null) hmEmpName = new HashMap<String, String>();
	Map<String, String> hmEmpCode = (Map<String, String>)request.getAttribute("hmEmpCode");
	if(hmEmpCode == null) hmEmpCode = new HashMap<String, String>();
	Map<String, String> hmReimCTCAmount = (Map<String, String>)request.getAttribute("hmReimCTCAmount");
	if(hmReimCTCAmount == null) hmReimCTCAmount = new HashMap<String, String>();
	Map<String, String> hmReimCTCHead = (Map<String, String>)request.getAttribute("hmReimCTCHead");
	if(hmReimCTCHead == null) hmReimCTCHead = new HashMap<String, String>();
	Map<String, Map<String, Map<String, String>>> hmReimbursementCTC = (Map<String, Map<String, Map<String, String>>>)request.getAttribute("hmReimbursementCTC");
	if(hmReimbursementCTC == null) hmReimbursementCTC = new HashMap<String, Map<String,Map<String,String>>>();
	
	Map<String, Map<String, String>> hmEmpAttendance = (Map<String, Map<String, String>>)request.getAttribute("hmEmpAttendance");
	if(hmEmpAttendance == null) hmEmpAttendance = new HashMap<String, Map<String, String>>();
	
	String paidStatus = (String)request.getAttribute("paidStatus");
%>

<%-- <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="<%=strTitle%>" name="title"/>
</jsp:include>

<div style="float: left; width: 98%; margin-bottom: -7px; margin-left: 10px;">
	<a href="PaidUnpaidReimbursements.action" class="all_dull" style="width: 154px;">Reimbursement</a>
	<a href="PayReimbursementCTC.action" class="all" style="width: 181px;">Reimbursement Part of CTC</a>
</div> --%>

<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
	<%=uF.showData((String)session.getAttribute(IConstants.MESSAGE),"") %>
	<%session.setAttribute(IConstants.MESSAGE,""); %>
	<s:form name="frm_PayReimbursementCTC" id="frm_PayReimbursementCTC" action="PayReimbursementCTC" theme="simple" method="post">
		
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
				<s:hidden name="exportType"></s:hidden>
				<div class="row row_without_margin">
					<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
						<i class="fa fa-filter"></i>
					</div>
					<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
						<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
							<p style="padding-left: 5px;">Status</p>
							<s:select theme="simple" name="paidStatus" id="paidStatus" cssStyle="width: 200px;" 
								list="#{'1':'Paid', '2':'UnPaid'}" onchange="submitForm('2');"/>
						</div>
						<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
							<p style="padding-left: 5px;">Organization</p>
							<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1')" list="orgList"/>
						</div>
						<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
							<p style="padding-left: 5px;">Level</p>
							<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" listValue="levelCodeName" list="levelList" onchange="submitForm('2')"/>
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
						
					</div>
				</div><br>
				<div class="row row_without_margin">
					<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
						<i class="fa fa-calendar"></i>
					</div>
					<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
						<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
							<p style="padding-left: 5px;">Paycycle</p>
							<s:select name="paycycle" id="paycycle" listKey="paycycleId" listValue="paycycleName" headerValue="Select Paycycle" list="paycycleList" key="" onchange="submitForm('2');"/>
						</div>
						<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
							<p style="padding-left: 5px;">&nbsp;</p>
							<input type="button" name="Submit" value="Submit" class="btn btn-primary" onclick="submitForm('2');" />
						</div>
					</div>
				</div>
			</div>
			<!-- /.box-body -->
		</div>
		
		<%
		int nPayCount = 0;
		if(hmReimbursementCTC.size() > 0 && hmReimCTCHead.size() > 0){ %>
			<div class="row row_without_margin paddingtopbottom10">
				<%if(uF.parseToInt(paidStatus) == 2){ %>
					<span style="float: left;">
						Choose Bank to pay from: <s:select theme="simple" name="bankAccount" listKey="bankId" listValue="bankName" list="bankList" key=""/>
						&nbsp;Choose Bank Account: <s:select theme="simple" name="bankAccountType" id="bankAccountType" list="#{'1':'Primary','2':'Secondary'}"/>
					</span>
					<span id="unApproveSpan" style="float: left; display: none;">
						&nbsp;<input type="button" name="strApprove" class="btn btn-primary disabled" value="PAY"/>
					</span>
					<span id="approveSpan" style="float: left; display: inline;">
						&nbsp;<input type="button" name="strApprove" class="btn btn-primary" value="PAY" onclick="payReimbursementCTC();"/>
					</span>
				<%} %>
			    <span style="float:right">
			    	Bank Order: <a href="javascript:void(0)" onclick="donwloadBankStatement('<%=(String)request.getAttribute("f_org")%>','<%=(String)request.getAttribute("paycycle")%>')"><i class="fa fa-file-o" aria-hidden="true" title="Bank Orders"></i></a>
			    </span>
			</div>
			<%
			String strDiv = "width:100%; float:left;";
			if (hmReimbursementCTC != null && hmReimbursementCTC.size() > 0) {
				strDiv = "width:100%; overflow:scroll;";
			}
			%>
			<div class="clr margintop20"></div>
			<div style="<%=strDiv %>">
				<table class="table table-bordered table-striped" id="lt">
					<thead>
						<tr>
							<th class="alignCenter" nowrap="nowrap">Employee Code</th>
							<th nowrap="nowrap">Employee Name</th>
							<th nowrap="nowrap">Reimbursement CTC</th>
							<th class="alignCenter" nowrap="nowrap">Pay
								<%if(uF.parseToInt(paidStatus) == 2){ %>
									<br/><input type="checkbox" name="payAll" id="payAll" onclick="selectall(this,'strEmpCTCId')" checked="checked"/>
								<%} %>
							</th>
							<th class="alignCenter" nowrap>Total Days</th>
							<th class="alignCenter" nowrap>Paid Days</th>
							<th class="alignCenter" nowrap>Present</th>
							<th class="alignCenter" nowrap>Leaves</th>
							<th class="alignCenter" nowrap>Absent/Unpaid</th>
							<th class="alignCenter" nowrap="nowrap">Amount</th>
							<%
							Iterator<String> it = hmReimCTCHead.keySet().iterator();
							while(it.hasNext()){
								String strReimCTCHeadId = it.next();
								String strReimCTCHeadName = hmReimCTCHead.get(strReimCTCHeadId);
							%>
								<th><%=uF.showData(strReimCTCHeadName,"") %></th>
							<%} %>
						</tr>
					</thead>
					<tbody>
					<% 
					   	Iterator<String> it1 = hmReimbursementCTC.keySet().iterator();
						int i=0;
					   	while(it1.hasNext()) {
					   		i++;
							String strEmpCTCId = it1.next();
							String[] temp = strEmpCTCId.split("_");
							String strEmpId = temp[0];
							String strReimCTCId = temp[1];
							
							Map<String, Map<String, String>> hmReimbursementCTCInner = hmReimbursementCTC.get(strEmpCTCId);
							if(hmReimbursementCTCInner == null) hmReimbursementCTCInner = new HashMap<String, Map<String,String>>();
							
							Map<String, String> hmEmpPay = (uF.parseToInt(paidStatus) == 1) ? hmEmpAttendance.get(strEmpCTCId) : hmEmpAttendance.get(strEmpId);
							if(hmEmpPay == null) hmEmpPay = new HashMap<String, String>();						
					%>
							<tr id="<%=i %>">
								<td style="text-align: center;" nowrap="nowrap"><%=uF.showData(hmEmpCode.get(strEmpId),"") %></td>
								<td nowrap="nowrap"><%=uF.showData(hmEmpName.get(strEmpId),"")%></td>
								<td nowrap="nowrap"><%=uF.showData(hmReimCTCName.get(strReimCTCId),"")%></td>
								<td style="text-align: center;">
									<%if(uF.parseToInt(paidStatus) == 1){ %>
										Paid
									<%} else {
										nPayCount++;
									%>
										<input type="checkbox" name="strEmpCTCId" value="<%=strEmpCTCId%>" onclick="checkAll();" checked="checked"/>
									<%}%>
								</td>
								<td class="alignCenter"><%=uF.parseToDouble(hmEmpPay.get("EMP_TOTAL_DAYS"))%></td>
								<td class="alignCenter"><%=uF.parseToDouble(hmEmpPay.get("EMP_PAID_DAYS"))%></td>
								<td class="alignCenter"><%=uF.parseToDouble(hmEmpPay.get("EMP_PRESENT_DAYS"))%></td>
								<td class="alignCenter" nowrap="nowrap"><%=uF.parseToDouble(hmEmpPay.get("EMP_PAID_LEAVES"))%></td>
								<td class="alignCenter"><%=uF.parseToDouble(hmEmpPay.get("EMP_ABSENT_DAYS"))%></td>
								<td style="text-align: right;" nowrap="nowrap"><%=uF.formatIntoOneDecimal(uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(roundOffCondition), Math.round(uF.parseToDouble(hmReimCTCAmount.get(strEmpCTCId))))))%></td>
								<%
								Iterator<String> it2 = hmReimCTCHead.keySet().iterator();
								while(it2.hasNext()){
									String strReimCTCHeadId = it2.next();
									Map<String, String> hmReimbursementHeadInner = hmReimbursementCTCInner.get(strReimCTCHeadId);
									if(hmReimbursementHeadInner == null) hmReimbursementHeadInner = new HashMap<String, String>();
								%>
									<td style="text-align: right;" nowrap="nowrap"><%=uF.formatIntoOneDecimal(uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(roundOffCondition),uF.parseToDouble(hmReimbursementHeadInner.get("REIMBURSEMENT_HEAD_AMOUNT")))))%></td>
								<%} %>
							</tr>
						<% } %>
					</tbody>
				</table>
			</div>
		<%} else { %>
			<div class="nodata msg"><span>No Data Found.</span></div>
		<%} %>
		<input type="hidden" name="payCount" id="payCount" value="<%=nPayCount %>"/> 
	</s:form>
</div>
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