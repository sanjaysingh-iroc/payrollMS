<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="java.util.List"%>
<%@ taglib prefix="s" uri="/struts-tags" %>

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

function isNumberKey(evt){
   var charCode = (evt.which) ? evt.which : event.keyCode;
   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46){
      return false;
   }
   return true;
}

function selectall(x,strEmpCTCId){
	var  status=x.checked; 
	var  arr= document.getElementsByName(strEmpCTCId);
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

function approve(){
	if(confirm('Are you sure, you want to approve of selected employee?')){
		var data = $("#frm_TDSReimbursementCTC").serialize();
		$("#subDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: 'PayReimbursementCTC.action?formType=approve',
			data: data,
			success: function(result){
	        	$("#subDivResult").html(result); 
	   		}
		});		
	}
}

function revokeAllEmp(x,strEmpCTCId){
	var status=x.checked;
	var arr= document.getElementsByName(strEmpCTCId);
	for(i=0;i<arr.length;i++){
	  	arr[i].checked=status;
	}
	
	if(x.checked == true){
		document.getElementById("unSendSpan").style.display = 'none';
		document.getElementById("sendSpan").style.display = 'inline';
	} else {
		document.getElementById("unSendSpan").style.display = 'inline';
		document.getElementById("sendSpan").style.display = 'none';
	}
}

function checkRevokeAll(){
	var revokeAll = document.getElementById("revokeAll");		
	var revokeEmpId = document.getElementsByName('revokeEmpId');
	var cnt = 0;
	var chkCnt = 0;
	for(var i=0;i<revokeEmpId.length;i++) {
		cnt++;
		 if(revokeEmpId[i].checked) {
			 chkCnt++;
		 }
	 }
	if(parseInt(chkCnt) > 0) {
		document.getElementById("unSendSpan").style.display = 'none';
		document.getElementById("sendSpan").style.display = 'inline';
	} else {
		if(document.getElementById("unSendSpan")){
			document.getElementById("unSendSpan").style.display = 'inline';
		}
		if(document.getElementById("sendSpan")){
			document.getElementById("sendSpan").style.display = 'none';
		}
		
	}
	
	if(parseInt(cnt) == parseInt(chkCnt) && parseInt(chkCnt) > 0) {
		revokeAll.checked = true;
	} else {
		console.log(revokeAll);
		if(revokeAll !== null){
			revokeAll.checked = false;
		}
		
	}
}

function revoke(){
	if(confirm('Are you sure, you want to Revoke of selected employee?')){
		var data = $("#frm_TDSReimbursementCTC").serialize();
		$("#subDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: 'PayReimbursementCTC.action?formType=revoke',
			data: data,
			success: function(result){
	        	$("#subDivResult").html(result); 
	   		}
		});
	}		
}

function checkPay(){
	var approveCount = document.getElementById("approveCount").value;		
	if(parseInt(approveCount) > 0) {
		document.getElementById("unApproveSpan").style.display = 'none';
		document.getElementById("approveSpan").style.display = 'inline';
	} else {
		document.getElementById("unApproveSpan").style.display = 'inline';
		document.getElementById("approveSpan").style.display = 'none';
		document.getElementById("payAll").checked = false;
	}
}

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

function submitForm(type){
	document.frm_TDSReimbursementCTC.exportType.value='';
	
	var org = document.getElementById("f_org").value;
	var financialYear = document.getElementById("financialYear").value;
	var location = getSelectedValue("f_strWLocation");
	var department = getSelectedValue("f_department");
	var service = getSelectedValue("f_service");
	var level = document.getElementById("f_level").value;
	var paramValues = "";
	if(type == '2') {
		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service+'&f_level='+level+'&financialYear='+financialYear;
	}
	var action = 'TDSReimbursementCTC.action?f_org='+org+paramValues; 
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

</script>

<%
	UtilityFunctions uF = new UtilityFunctions();
	String strTitle = (String) request.getAttribute(IConstants.TITLE);
	String roundOffCondition = (String)request.getAttribute("roundOffCondition");
	
	List<Map<String, String>> alTDSReimCTC = (List<Map<String, String>>) request.getAttribute("alTDSReimCTC"); 
	if(alTDSReimCTC == null) alTDSReimCTC = new ArrayList<Map<String,String>>(); 
	Map<String, String> hmReimAppliedAmount = (Map<String, String>) request.getAttribute("hmReimAppliedAmount");
	if(hmReimAppliedAmount == null) hmReimAppliedAmount = new HashMap<String, String>();
	Map<String, String> hmReimTaxAmount = (Map<String, String>) request.getAttribute("hmReimTaxAmount");
	if(hmReimTaxAmount == null) hmReimTaxAmount = new HashMap<String, String>(); 
	
%>

<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
	<%=uF.showData((String)session.getAttribute(IConstants.MESSAGE),"") %>
	<%session.setAttribute(IConstants.MESSAGE,""); %>
	<s:form name="frm_TDSReimbursementCTC" id="frm_TDSReimbursementCTC" action="TDSReimbursementCTC" theme="simple" method="post">
		<s:hidden name="exportType"></s:hidden>
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
				<div class="row row_without_margin">
					<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
						<i class="fa fa-filter"></i>
					</div>
					<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
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
							<p style="padding-left: 5px;">Financial Year</p>
							<s:select name="financialYear" id="financialYear" listKey="financialYearId" listValue="financialYearName" 
							list="financialYearList" headerKey="" headerValue="Select Financial Year" onchange="submitForm('2');" key=""/>
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
		
		<div class="row row_without_margin paddingtopbottom10">
			<%if(hmReimTaxAmount != null && hmReimTaxAmount.size() > 0){ %>
				<span id="unSendSpan" style="display: none;">
					<input type="button" name="unSend" class="btn btn-primary disabled" value="Revoke" onclick="alert('Please select employee.');"/>
				</span>
				<span id="sendSpan">
					<input type="button" value="Revoke" name="revokeSubmit" class="btn btn-danger" onclick="revoke();"/>
				</span>
			<%} %>
			<span id="unApproveSpan" style="display: none;">
				<input type="button" name="Submit" class="btn btn-primary disabled" value="Approve" onclick="alert('Please select employee.');"/>
			</span>
			<span id="approveSpan">
				<input type="button" value="Approve" name="Submit" class="btn btn-primary" onclick="approve();"/>
			</span>
		</div>
		<div>
			<table class="table table-bordered table-striped" id="lt">
				<thead>
					<tr>
						<%if(hmReimTaxAmount != null && hmReimTaxAmount.size() > 0){ %>
							<th class="alignCenter" nowrap>Revoke<br/><input type="checkbox" name="revokeAll" id="revokeAll" onclick="revokeAllEmp(this,'revokeEmpId')" checked="checked"/></th>
						<%} %>
						<th class="alignCenter" nowrap="nowrap">Employee Code</th>
						<th nowrap="nowrap">Employee Name</th>
						<th nowrap="nowrap">Reimbursement CTC</th>
						<th class="alignCenter" nowrap="nowrap">Approve<br/><input type="checkbox" name="payAll" id="payAll" onclick="selectall(this,'strEmpCTCId')" checked="checked"/></th>
						<th class="alignRight" nowrap="nowrap">Reimbursement Amount</th>
						<th class="alignRight" nowrap="nowrap">Non-Taxable Amount</th>
						<th class="alignRight" nowrap="nowrap">Taxable Amount</th>
						<th class="alignRight" nowrap="nowrap">Approve Taxable Amount</th>
					</tr>
				</thead>
				<tbody>
					<%
					int nApproveCount = 0;
					int nAlTDSReimCTCSize = alTDSReimCTC.size();
					for(int i = 0; i < nAlTDSReimCTCSize; i++){
						Map<String, String> hmReimCTCAmount = alTDSReimCTC.get(i);
						String strEmpId = hmReimCTCAmount.get("EMP_ID");
						String strReimCTCId  = hmReimCTCAmount.get("REIMBURSEMENT_CTC_ID");
						
						double dblReimAmt = Math.round(uF.parseToDouble(hmReimCTCAmount.get("REIMBURSEMENT_AMOUNT")));
						double dblNonTaxableAmt = Math.round(uF.parseToDouble(hmReimAppliedAmount.get(strEmpId+"_"+strReimCTCId)));
						double dblTaxableAmt = Math.round(dblReimAmt - dblNonTaxableAmt);
						double dblAssignTaxableAmt = dblTaxableAmt;
						if(hmReimTaxAmount != null && hmReimTaxAmount.containsKey(strEmpId+"_"+strReimCTCId)){
							dblAssignTaxableAmt = uF.parseToDouble(hmReimTaxAmount.get(strEmpId+"_"+strReimCTCId));
						}
					%>
						<tr id="<%=i %>">
							<%if(hmReimTaxAmount != null && hmReimTaxAmount.size() > 0){ %>
								<td class="alignCenter">
									<%if(hmReimTaxAmount.containsKey(strEmpId+"_"+strReimCTCId)){ %>
										<input type="checkbox" name="revokeEmpId" onclick="checkRevokeAll();" style="width:10px; height:10px" value="<%=strEmpId+"_"+strReimCTCId%>" checked="checked"/>
									<%} %>
								</td>
							<%} %>
							<td style="text-align: center;" nowrap="nowrap"><%=uF.showData(hmReimCTCAmount.get("EMP_CODE"),"") %></td>
							<td nowrap="nowrap"><%=uF.showData(hmReimCTCAmount.get("EMP_NAME"),"")%></td>
							<td nowrap="nowrap"><%=uF.showData(hmReimCTCAmount.get("REIMBURSEMENT_CTC_NAME"),"")%></td>
							<td style="text-align: center;">
								<%if(hmReimTaxAmount != null && hmReimTaxAmount.containsKey(strEmpId+"_"+strReimCTCId)){ %>
									Approved
								<%} else {
									nApproveCount++;
								%>
									<input type="checkbox" name="strEmpCTCId" value="<%=strEmpId+"_"+strReimCTCId%>" onclick="checkAll();" checked="checked"/>
								<%}%>
							</td>
							<td style="text-align: right;" nowrap="nowrap"><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition), dblReimAmt) %></td>
							<td style="text-align: right;" nowrap="nowrap"><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition), dblNonTaxableAmt) %></td>
							<td style="text-align: right;" nowrap="nowrap"><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition), dblTaxableAmt) %></td>
							<td style="text-align: right;" nowrap="nowrap"><input type="text" name="taxableAmt_<%=strEmpId+"_"+strReimCTCId%>" id="taxableAmt_<%=strEmpId+"_"+strReimCTCId%>" style="width:75px !important; text-align:right;" value="<%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition), dblAssignTaxableAmt) %>" onkeypress="return isNumberKey(event)"/></td>
						</tr>
					<%} %>
				</tbody> 
			</table>	
			<input type="hidden" name="approveCount" id="approveCount" value="<%=nApproveCount %>"/> 
		</div>	
	</s:form>
</div>
<script type="text/javascript">
checkAll();
checkPay();
checkRevokeAll();
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